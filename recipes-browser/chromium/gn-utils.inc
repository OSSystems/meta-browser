# GN host architecture helpers.
#
# BUILD_ARCH's value corresponds to what uname returns as the machine name.
# The mapping in gn_host_arch_name() tries to match several possible values
# returned by the Linux kernel in uname(2) into the corresponding values GN
# understands.
def gn_host_arch_name(d):
    """Returns a GN architecture name corresponding to the build host's machine
    architecture."""
    import re
    arch_translations = {
        r'aarch64.*': 'arm64',
        r'arm.*': 'arm',
        r'i[3456]86$': 'x86',
        r'x86_64$': 'x64',
    }
    build_arch = d.getVar("BUILD_ARCH")
    for arch_regexp, gn_arch_name in arch_translations.items():
        if re.match(arch_regexp, build_arch):
            return gn_arch_name
    bb.fatal('Unsuported BUILD_ARCH value: "%s"' % build_arch)

# GN target architecture helpers.
#
# Determining the target architecture is more difficult, as there are many
# different values we can use on the Yocto side (e.g. TUNE_ARCH, TARGET_ARCH,
# MACHINEOVERRIDES etc). What we do is define the mapping with regular,
# non-Python variables with overrides that are generic enough (i.e. "x86"
# instead of "i586") and then use gn_target_arch_name() to return the right
# value with some validation.
GN_TARGET_ARCH_NAME_aarch64 = "arm64"
GN_TARGET_ARCH_NAME_arm = "arm"
GN_TARGET_ARCH_NAME_x86 = "x86"
GN_TARGET_ARCH_NAME_x86-64 = "x64"

BUILD_CC_toolchain-clang = "clang"
BUILD_CXX_toolchain-clang = "clang++"
BUILD_LD_toolchain-clang = "clang"

# knob for clang, when using meta-clang to provide clang and case where
# clang happens to be default compiler for OE we should let it use clang
def is_default_cc_clang(d):
    """Return true if clang is default cross compiler."""
    toolchain = d.getVar("TOOLCHAIN")
    overrides = d.getVar("OVERRIDES")
    if toolchain == "clang" and "toolchain-clang" in overrides.split(":"):
        return "true"
    return "false"

def clang_install_path(d):
    """Return clang compiler install path."""
    return d.getVar("STAGING_BINDIR_NATIVE")

def gn_target_arch_name(d):
    """Returns a GN architecture name corresponding to the target machine's
    architecture."""
    name = d.getVar("GN_TARGET_ARCH_NAME")
    if name is None:
        bb.fatal('Unsupported target architecture. A valid override for the '
                 'GN_TARGET_ARCH_NAME variable could not be found.')
    return name

def write_toolchain_file(d, file_path):
    """Creates a complete GN toolchain file in |file_path|."""
    import string
    toolchain_tmpl = string.Template(
        'gcc_toolchain("${toolchain_name}") {\n'
        '  cc = "${cc}"\n'
        '  cxx = "${cxx}"\n'
        '  ar = "${ar}"\n'
        '  ld = cxx  # GN expects a compiler, not a linker.\n'
        '  nm = "${nm}"\n'
        '  readelf = "${readelf}"\n'
        '  extra_cflags = "${extra_cflags}"\n'
        '  extra_cppflags = "${extra_cppflags}"\n'
        '  extra_cxxflags = "${extra_cxxflags}"\n'
        '  extra_ldflags = "${extra_ldflags}"\n'
        '  toolchain_args = {\n'
        '    current_cpu = "${current_cpu}"\n'
        '    current_os = "linux"\n'
        '    is_clang = ${is_clang}\n'
        '    clang_base_path = "${clang_base_path}"\n'
        '    clang_use_chrome_plugins = false\n'
        '  }\n'
        '}\n'
    )

    native_toolchain = {
        'toolchain_name': 'yocto_native',
        'current_cpu': gn_host_arch_name(d),
        'cc': d.expand('${BUILD_CC}'),
        'cxx': d.expand('${BUILD_CXX}'),
        'ar': d.expand('${BUILD_AR}'),
        'nm': d.expand('${BUILD_NM}'),
        'readelf': d.expand('${BUILD_PREFIX}readelf'),
        'extra_cflags': d.expand('${BUILD_CFLAGS}'),
        'extra_cppflags': d.expand('${BUILD_CPPFLAGS}'),
        'extra_cxxflags': d.expand('${BUILD_CXXFLAGS}'),
        'extra_ldflags': d.expand('${BUILD_LDFLAGS}'),
        'is_clang': is_default_cc_clang(d),
        'clang_base_path': clang_install_path(d),
    }
    target_toolchain = {
        'toolchain_name': 'yocto_target',
        'current_cpu': gn_target_arch_name(d),
        'cc': d.expand('${CC}'),
        'cxx': d.expand('${CXX}'),
        'ar': d.expand('${AR}'),
        'nm': d.expand('${NM}'),
        'readelf': d.expand('${TARGET_PREFIX}readelf'),
        'extra_cflags': d.expand('${TARGET_CFLAGS}'),
        'extra_cppflags': d.expand('${TARGET_CPPFLAGS}'),
        'extra_cxxflags': d.expand('${TARGET_CXXFLAGS}'),
        'extra_ldflags': d.expand('${TARGET_LDFLAGS}'),
        'is_clang': is_default_cc_clang(d),
        'clang_base_path': clang_install_path(d),
    }

    with open(file_path, 'w') as toolchain_file:
        toolchain_file.write(
            '# This file has been generated automatically.\n'
            '\n'
            'import("//build/config/sysroot.gni")\n'
            'import("//build/toolchain/gcc_toolchain.gni")\n'
            '\n'
        )
        toolchain_file.write(toolchain_tmpl.substitute(native_toolchain))
        toolchain_file.write(toolchain_tmpl.substitute(target_toolchain))
