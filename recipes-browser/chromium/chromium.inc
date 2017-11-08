LICENSE = "BSD"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE_x86 = "(.*)"
COMPATIBLE_MACHINE_x86-64 = "(.*)"
COMPATIBLE_MACHINE_armv6 = "(.*)"
COMPATIBLE_MACHINE_armv7a = "(.*)"
COMPATIBLE_MACHINE_armv7ve = "(.*)"
COMPATIBLE_MACHINE_aarch64 = "(.*)"

# The build type defaults to Release. If you want a Debug build, you can set
# the variable CHROMIUM_BUILD_TYPE to "Debug" in your conf/local.conf file.
CHROMIUM_BUILD_TYPE ??= "Release"

inherit pythonnative

GYP_DEFINES += "release_extra_cflags='-Wno-error=unused-local-typedefs' sysroot='' \
	${@bb.utils.contains("AVAILTUNES", "mips", "", "release_extra_cflags='-fno-delete-null-pointer-checks'", d)}"
GYP_DEFINES_append_x86 = " generate_character_data=0"

# Disable activation of field trial tests that can cause problems in
# production.
# See https://groups.google.com/a/chromium.org/d/msg/chromium-packagers/ECWC57W7E0k/9Kc5UAmyDAAJ
GYP_DEFINES += "-Dfieldtrial_testing_like_official_build=1"

# ARM builds need special additional flags (see ${S}/build/config/arm.gni).
# If we do not pass |arm_arch| and friends to GN, it will deduce a value that
# will then conflict with TUNE_CCARGS and CC.
def get_arm_version(arm_arch):
    import re
    try:
        return re.match(r'armv(\d).*', arm_arch).group(1)
    except IndexError:
        bb.fatal('Unrecognized ARM architecture value: %s' % arm_arch)
def get_compiler_flag(params, param_name, d):
    """Given a sequence of compiler arguments in |params|, returns the value of
    an option |param_name| or an empty string if the option is not present."""
    for param in params:
      if param.startswith(param_name):
        return param.split('=')[1]
    return ''
ARM_ARCH = "${@get_compiler_flag(d.getVar('TUNE_CCARGS').split(), '-march', d)}"
ARM_FLOAT_ABI = "${@bb.utils.contains('TUNE_FEATURES', 'callconvention-hard', 'hard', 'softfp', d)}"
ARM_FPU = "${@get_compiler_flag(d.getVar('TUNE_CCARGS').split(), '-mfpu', d)}"
ARM_TUNE = "${@get_compiler_flag(d.getVar('TUNE_CCARGS').split(), '-mcpu', d)}"
ARM_VERSION = "${@get_arm_version(d.getVar('ARM_ARCH'))}"
GYP_DEFINES_append_arm = " \
        -Darm_arch=${ARM_ARCH} \
        -Darm_float_abi=${ARM_FLOAT_ABI} \
        -Darm_fpu=${ARM_FPU} \
        -Darm_tune=${ARM_TUNE} \
        -Darm_version=${ARM_VERSION} \
"

PACKAGES =+ "${PN}-chromedriver"

FILES_${PN}-chromedriver = "${bindir}/chromedriver"

FILES_${PN} = " \
        ${bindir}/${PN} \
        ${datadir}/applications/${PN}.desktop \
        ${datadir}/icons/hicolor/*x*/apps/chromium.png \
        ${libdir}/${PN}/* \
"

PACKAGE_DEBUG_SPLIT_STYLE = "debug-without-src"

# chromium-54.0.2810.2: ELF binary 'i586-oe-linux/chromium/54.0.2810.2-r0/packages-split/chromium/usr/bin/chromium/chrome' has relocations in .text [textrel]
INSANE_SKIP_${PN} = "ldflags textrel"
SOLIBS = ".so"
FILES_SOLIBSDEV = ""

# There is no need to ship empty -dev packages.
ALLOW_EMPTY_${PN}-dev = "0"
