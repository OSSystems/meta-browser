require chromium.inc
require chromium-unbundle.inc
require gn-utils.inc

inherit distro_features_check gtk-icon-cache qemu

# The actual directory name in out/ is irrelevant for GN.
OUTPUT_DIR = "out/Release"
B = "${S}/${OUTPUT_DIR}"

SRC_URI += " \
        file://0001-Add-support-for-GCC-Compilers.patch \
        file://0001-Revert-Use-noexcept-consistently-on-the-move-ctor-of.patch \
        file://0001-TrackEventJSONExporter-HandleLegacyEvent-Use-std-mov.patch \
        file://0001-webrequest-Fix-GCC-build-after-2d47b917314a.patch \
        file://0001-GCC-7-workaround-remove-constexpr-ctor.patch \
        file://0001-libstdc-do-not-assume-unique_ptr-has-ostream-operato.patch \
        file://aarch64-skia-build-fix.patch \
        file://oe-clang-fixes.patch \
        file://v8-qemu-wrapper.patch \
        file://wrapper-extra-flags.patch \
        file://do-not-specify-march-on-arm.patch \
        file://0005-Remove-banned-designated-initializer-list-usage-from.patch \
        file://0006-GCC-add-std-move-to-return-to-base-Optional.patch \
        file://0001-GCC-fix-another-GCC-bug-by-implementing-copy-assign-.patch \
"

SRC_URI_append_libc-musl = "\
        file://musl/musl-include-fcntl.h-for-loff_t.patch \
        file://musl/musl_no_mallinfo.patch \
        file://musl/musl_no_execinfo.patch \
        file://musl/musl_TEMP_FAILURE_RETRY.patch \
        file://musl/musl_fix-stack.patch \
        file://musl/musl_breakpad.patch \
        file://musl/musl_fontconfig.patch \
        file://musl/musl_secure_getenv.patch \
        file://musl/musl_libc_malloc.patch \
        file://musl/musl_gnu_libc-version.patch \
        file://musl/musl_resolver.patch \
        file://musl/musl_off64_t.patch \
        file://musl/musl_lss-match_syscalls.patch \
        file://musl/musl_crashpad.patch \
        file://musl/musl_replace_libc_fpstate.patch \
"
# This patch could be enabled for clang builds, one just needs to make it work.
# On sumo and MACHINE="raspberrypi3", the build fails with errors such as
#   /tmp/crc32_simd-01be44.s:86: Error: selected processor does not support `crc32b r0,r0,r5' in Thumb mode
# Enabling this on GCC requires more work, see the comments in the patch itself
# for directions.
SRC_URI += "file://0001-zlib-GCC-Disable-CRC32-optimizations-on-ARM.patch"

# Append instead of assigning; the gtk-icon-cache class inherited above also
# adds packages to DEPENDS.
DEPENDS += " \
    alsa-lib \
    ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'at-spi2-core', '', d)} \
    atk \
    bison-native \
    cairo \
    dbus \
    expat \
    flac \
    freetype \
    glib-2.0 \
    gn-native \
    gperf-native \
    gtk+3 \
    jpeg \
    libdrm \
    libwebp \
    libxml2 \
    libxslt \
    ninja-native \
    nodejs-native \
    nspr \
    nspr-native \
    nss \
    nss-native \
    pango \
    pciutils \
    pkgconfig-native \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', 'pulseaudio', '', d)} \
    qemu-native \
    virtual/libgl \
"
DEPENDS_append_x86 = " yasm-native"
DEPENDS_append_x86-64 = " yasm-native"

# The wrapper script we use from upstream requires bash.
RDEPENDS_${PN} = "bash"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE_aarch64 = "(.*)"
COMPATIBLE_MACHINE_armv6 = "(.*)"
COMPATIBLE_MACHINE_armv7a = "(.*)"
COMPATIBLE_MACHINE_armv7ve = "(.*)"
COMPATIBLE_MACHINE_x86 = "(.*)"
COMPATIBLE_MACHINE_x86-64 = "(.*)"

PACKAGECONFIG ??= "use-egl"

# this makes sure the dependencies for the EGL mode are present; otherwise, the configure scripts
# automatically and silently fall back to GLX
PACKAGECONFIG[use-egl] = ",,virtual/egl virtual/libgles2"

# Empty PACKAGECONFIG options listed here to avoid warnings.
# The .bb file should use these to conditionally add patches
# and command-line switches (extra dependencies should not
# be necessary but are OK to add).
PACKAGECONFIG[component-build] = ""
PACKAGECONFIG[cups] = "use_cups=true,use_cups=false,cups"
PACKAGECONFIG[impl-side-painting] = ""
PACKAGECONFIG[jumbo-build] = ""
PACKAGECONFIG[kiosk-mode] = ""
PACKAGECONFIG[proprietary-codecs] = ' \
        ffmpeg_branding="Chrome" proprietary_codecs=true, \
        ffmpeg_branding="Chromium" proprietary_codecs=false \
'

# Base GN arguments, mostly related to features we want to enable or disable.
GN_ARGS = " \
        ${PACKAGECONFIG_CONFARGS} \
        is_component_build=${@bb.utils.contains('PACKAGECONFIG', 'component-build', 'true', 'false', d)} \
        use_atk=${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'true', 'false', d)} \
        use_gnome_keyring=false \
        use_jumbo_build=${@bb.utils.contains('PACKAGECONFIG', 'jumbo-build', 'true', 'false', d)} \
        use_kerberos=false \
        use_pulseaudio=${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', 'true', 'false', d)} \
        use_system_freetype=true \
        use_system_libjpeg=true \
"

# Closure_compile needs to be disabled to avoid pulling in java dependencies,
# which are typicaly not wanted. It started to happen after https://crrev.com/c/1278470
GN_ARGS += "closure_compile=false"

# Make sure pkg-config, when used with the host's toolchain to build the
# binaries we need to run on the host, uses the right pkg-config to avoid
# passing include directories belonging to the target.
GN_ARGS += 'host_pkg_config="pkg-config-native"'

# From Chromium's BUILDCONFIG.gn:
# Set to enable the official build level of optimization. This has nothing
# to do with branding, but enables an additional level of optimization above
# release (!is_debug). This might be better expressed as a tri-state
# (debug, release, official) but for historical reasons there are two
# separate flags.
# See also: https://groups.google.com/a/chromium.org/d/msg/chromium-dev/hkcb6AOX5gE/PPT1ukWoBwAJ
GN_ARGS += "is_debug=false is_official_build=true"

# Starting with M61, Chromium defaults to building with its own copy of libc++
# instead of the system's libstdc++. Explicitly disable this behavior.
# https://groups.google.com/a/chromium.org/d/msg/chromium-packagers/8aYO3me2SCE/SZ8pJXhZAwAJ
GN_ARGS += "use_custom_libcxx=false"

# When using meta-clang, one can switch to using the lld linker
# by using the ld-is-lld distro feature.
GN_ARGS += "use_lld=${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-lld', 'true', 'false', d)}"

# By default, passing is_official_build=true to GN causes its symbol_level
# variable to be set to "2". This means the compiler will be passed "-g2" and
# we will end up with a very large chrome binary (around 5Gb as of M58)
# regardless of whether DEBUG_BUILD has been set or not. In addition, binutils,
# file and other utilities are unable to read a 32-bit binary this size, which
# causes it not to be stripped.
# The solution is two-fold:
# 1. Make sure -g is not passed on 32-bit architectures via DEBUG_FLAGS. -g is
#    the same as -g2. -g1 generates an 800MB binary, which is a lot more
#    manageable.
# 2. Explicitly pass symbol_level=0 to GN. This causes -g0 to be passed
#    instead, so that if DEBUG_BUILD is not set GN will not create a huge debug
#    binary anyway. Since our compiler flags are passed after GN's, -g0 does
#    not cause any issues if DEBUG_BUILD is set, as -g1 will be passed later.
DEBUG_FLAGS_remove_arm = "-g"
DEBUG_FLAGS_append_arm = "-g1"
DEBUG_FLAGS_remove_x86 = "-g"
DEBUG_FLAGS_append_x86 = "-g1"
GN_ARGS += "symbol_level=0"

# As of Chromium 62.0.3202.94 and Yocto Rocko (GCC 7, binutils 2.29), passing
# -g to the compiler results in many linker errors on aarch64, such as:
# obj/third_party/WebKit/Source/modules/payments/libpayments.a(PaymentEventDataConversion.o)(.debug_loc+0x4e25): error: relocation overflow in R_AARCH64_ABS32
DEBUG_FLAGS_remove_aarch64 = "-g"
DEBUG_FLAGS_append_aarch64 = "-g1"

# As of Chromium 60.0.3112.101 and Yocto Pyro (GCC 6, binutils 2.28), passing
# -g to the compiler results in many linker errors on x86_64, such as:
# obj/third_party/WebKit/Source/core/loader/libloader.a(ModuleTreeLinker.o)(.debug_loc+0x1e9a5): error: relocation overflow: reference to local symbol 82 in obj/third_party/WebKit/Source/core/loader/libloader.a(ModuleTreeLinker.o)
# obj/third_party/WebKit/Source/core/libcore_generated.a(ScriptModule.o)(.debug_loc+0x253c): error: relocation overflow: reference to local symbol 31 in obj/third_party/WebKit/Source/core/libcore_generated.a(ScriptModule.o)
# so we have to use the same hack described above.
DEBUG_FLAGS_remove_x86-64 = "-g"
DEBUG_FLAGS_append_x86-64 = "-g1"

# Disable Chrome Remote Desktop (aka Chromoting) support. Building host support
# (so that the machine running this recipe can be controlled remotely from
# another machine) requires additional effort to build some extra binaries,
# whereas connecting to other machines requires building and installing a
# Chrome extension (it is not clear how to do that automatically).
GN_ARGS += "enable_remoting=false"

# NaCl support depends on the NaCl toolchain that needs to be built before NaCl
# itself. The whole process is quite cumbersome so we just disable the whole
# thing for now.
GN_ARGS += "enable_nacl=false"

# We do not want to use Chromium's own Debian-based sysroots, it is easier to
# just let Chromium's build system assume we are not using a sysroot at all and
# let Yocto handle everything.
GN_ARGS += "use_sysroot=false"

# Upstream Chromium uses clang on Linux, and GCC is not regularly tested. This
# means new GCC releases can introduce build failures as Chromium uses "-Wall
# -Werror" by default and we do not have much control over which warnings GCC
# decides to include in -Wall.
GN_ARGS += "treat_warnings_as_errors=false"

# As of M63, we explicitly need to set is_cfi to false, otherwise we fail the
# following assertion in //build/config/sanitizers/sanitizers.gni:
#   assert(!is_cfi || is_clang,
#          "is_cfi requires setting is_clang = true in 'gn args'")
GN_ARGS += "is_cfi=false"

# Disable activation of field trial tests that can cause problems in
# production.
# See https://groups.google.com/a/chromium.org/d/msg/chromium-packagers/ECWC57W7E0k/9Kc5UAmyDAAJ
GN_ARGS += "fieldtrial_testing_like_official_build=true"

# API keys for accessing Google services. By default, we use an invalid key
# only to prevent the "you are missing an API key" infobar from being shown on
# startup.
# See https://dev.chromium.org/developers/how-tos/api-keys for more information
# on how to obtain your own keys. You can then set the variables below in
# local.conf or a .bbappend file.
GOOGLE_API_KEY ??= "invalid-api-key"
GOOGLE_DEFAULT_CLIENT_ID ??= "invalid-client-id"
GOOGLE_DEFAULT_CLIENT_SECRET ??= "invalid-client-secret"
GN_ARGS += ' \
        google_api_key="${GOOGLE_API_KEY}" \
        google_default_client_id="${GOOGLE_DEFAULT_CLIENT_ID}" \
        google_default_client_secret="${GOOGLE_DEFAULT_CLIENT_SECRET}" \
'

# Toolchains we will use for the build. We need to point to the toolchain file
# we've created, set the right target architecture and make sure we are not
# using Chromium's toolchain (bundled clang, bundled binutils etc).
GN_ARGS += ' \
        custom_toolchain="//build/toolchain/yocto:yocto_target" \
        gold_path="" \
        host_toolchain="//build/toolchain/yocto:yocto_native" \
        is_clang=${@is_default_cc_clang(d)} \
        clang_base_path="${@clang_install_path(d)}" \
        clang_use_chrome_plugins=false \
        linux_use_bundled_binutils=false \
        target_cpu="${@gn_target_arch_name(d)}" \
        use_gold=${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', 'true', 'false', d)} \
        v8_snapshot_toolchain="//build/toolchain/yocto:yocto_target" \
'

# ARM builds need special additional flags (see ${S}/build/config/arm.gni).
# If we do not pass |arm_arch| and friends to GN, it will deduce a value that
# will then conflict with TUNE_CCARGS and CC.
# Note that as of M61 in some corner cases parts of the build system disable
# the "compiler_arm_fpu" GN config, whereas -mfpu is always passed via ${CC}.
# We might want to rework that if there are issues in the future.
def get_compiler_flag(params, param_name, d):
    """Given a sequence of compiler arguments in |params|, returns the value of
    an option |param_name| or an empty string if the option is not present."""
    for param in params:
      if param.startswith(param_name):
        return param.split('=')[1]
    return ''

ARM_FLOAT_ABI = "${@bb.utils.contains('TUNE_FEATURES', 'callconvention-hard', 'hard', 'softfp', d)}"
ARM_FPU = "${@get_compiler_flag(d.getVar('TUNE_CCARGS').split(), '-mfpu', d)}"
ARM_TUNE = "${@get_compiler_flag(d.getVar('TUNE_CCARGS').split(), '-mcpu', d)}"
ARM_VERSION_aarch64 = "8"
ARM_VERSION_armv7a = "7"
ARM_VERSION_armv7ve = "7"
ARM_VERSION_armv6 = "6"

# GN computes and defaults to it automatically where needed
# forcing it from cmdline breaks build on places where it ends up
# overriding what GN wants
TUNE_CCARGS_remove = "-mthumb"

GN_ARGS_append_arm = ' \
        arm_float_abi="${ARM_FLOAT_ABI}" \
        arm_fpu="${ARM_FPU}" \
        arm_tune="${ARM_TUNE}" \
        arm_version=${ARM_VERSION} \
'
# tcmalloc's atomicops-internals-arm-v6plus.h uses the "dmb" instruction that
# is not available on (some?) ARMv6 models, which causes the build to fail.
GN_ARGS_append_armv6 += 'use_allocator="none"'
# The WebRTC code fails to build on ARMv6 when NEON is enabled.
# https://bugs.chromium.org/p/webrtc/issues/detail?id=6574
GN_ARGS_append_armv6 += 'arm_use_neon=false'

# Disable glibc shims on musl
# tcmalloc does not play well with musl as of M62 (and possibly earlier).
# https://github.com/gperftools/gperftools/issues/693
GN_ARGS_append_libc-musl = ' use_allocator_shim=false'

CHROMIUM_EXTRA_ARGS ?= " \
        ${@bb.utils.contains('PACKAGECONFIG', 'use-egl', '--use-gl=egl', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'impl-side-painting', '--enable-gpu-rasterization --enable-impl-side-painting', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'kiosk-mode', '--kiosk --no-first-run --incognito', '', d)} \
"

# V8's JIT infrastructure requires binaries such as mksnapshot and
# mkpeephole to be run in the host during the build. However, these
# binaries must have the same bit-width as the target (e.g. a x86_64
# host targeting ARMv6 needs to produce a 32-bit binary). Instead of
# depending on a third Yocto toolchain, we just build those binaries
# for the target and run them on the host with QEMU.
python do_create_v8_qemu_wrapper () {
    """Creates a small wrapper that invokes QEMU to run some target V8 binaries
    on the host."""
    qemu_libdirs = [d.expand('${STAGING_DIR_HOST}${libdir}'),
                    d.expand('${STAGING_DIR_HOST}${base_libdir}')]
    qemu_cmd = qemu_wrapper_cmdline(d, d.getVar('STAGING_DIR_HOST', True),
                                    qemu_libdirs)
    wrapper_path = d.expand('${B}/v8-qemu-wrapper.sh')
    with open(wrapper_path, 'w') as wrapper_file:
        wrapper_file.write("""#!/bin/sh

# This file has been generated automatically.
# It invokes QEMU to run binaries built for the target in the host during the
# build process.

%s "$@"
""" % qemu_cmd)
    os.chmod(wrapper_path, 0o755)
}
do_create_v8_qemu_wrapper[dirs] = "${B}"
addtask create_v8_qemu_wrapper after do_patch before do_configure

python do_write_toolchain_file () {
    """Writes a BUILD.gn file for Yocto detailing its toolchains."""
    toolchain_dir = d.expand("${S}/build/toolchain/yocto")
    bb.utils.mkdirhier(toolchain_dir)
    toolchain_file = os.path.join(toolchain_dir, "BUILD.gn")
    write_toolchain_file(d, toolchain_file)
}
addtask write_toolchain_file after do_patch before do_configure

do_add_nodejs_symlink () {
	# Adds a symlink to the node binary to the location expected by
	# Chromium's build system.
	chromium_node_dir="${S}/third_party/node/linux/node-linux-x64/bin"
	nodejs_native_path="${STAGING_BINDIR_NATIVE}/node"
	mkdir -p "${chromium_node_dir}"
	if [ ! -f "${nodejs_native_path}" ]; then
		echo "${nodejs_native_path} does not exist."
		exit 1
	fi
	ln -sf "${nodejs_native_path}" "${chromium_node_dir}/node"
}
addtask add_nodejs_symlink after do_configure before do_compile

do_configure() {
	cd ${S}
	./build/linux/unbundle/replace_gn_files.py --system-libraries ${GN_UNBUNDLE_LIBS}
	gn gen --args='${GN_ARGS}' "${OUTPUT_DIR}"
}

do_compile() {
	ninja -v ${PARALLEL_MAKE} chrome chrome_sandbox chromedriver
}
do_compile[progress] = "outof:^\[(\d+)/(\d+)\]\s+"

do_install() {
	install -d ${D}${bindir}
	install -d ${D}${datadir}
	install -d ${D}${datadir}/applications
	install -d ${D}${datadir}/icons
	install -d ${D}${datadir}/icons/hicolor
	install -d ${D}${libdir}/chromium
	install -d ${D}${libdir}/chromium/locales

	install -m 4755 chrome_sandbox ${D}${libdir}/chromium/chrome-sandbox
	install -m 0755 chrome ${D}${libdir}/chromium/chromium-bin
	install -m 0644 *.bin ${D}${libdir}/chromium/
	install -m 0644 icudtl.dat ${D}${libdir}/chromium/icudtl.dat

	# Process and install Chromium's template .desktop file.
	sed -e "s,@@MENUNAME@@,Chromium Browser,g" \
	    -e "s,@@PACKAGE@@,chromium,g" \
	    -e "s,@@USR_BIN_SYMLINK_NAME@@,chromium,g" \
	    ${S}/chrome/installer/linux/common/desktop.template > chromium.desktop
	install -m 0644 chromium.desktop ${D}${datadir}/applications/chromium.desktop

	# Install icons.
	for size in 16 22 24 32 48 64 128 256; do
		install -d ${D}${datadir}/icons/hicolor/${size}x${size}
		install -d ${D}${datadir}/icons/hicolor/${size}x${size}/apps
		for dirname in "chromium" "default_100_percent/chromium"; do
			icon="${S}/chrome/app/theme/${dirname}/product_logo_${size}.png"
			if [ -f "${icon}" ]; then
				install -m 0644 "${icon}" \
					${D}${datadir}/icons/hicolor/${size}x${size}/apps/chromium.png
			fi
		done
	done

	# A wrapper for the proprietary Google Chrome version already exists.
	# We can just use that one instead of reinventing the wheel.
	WRAPPER_FILE=${S}/chrome/installer/linux/common/wrapper
	sed -e "s,@@CHANNEL@@,stable,g" \
		-e "s,@@PROGNAME@@,chromium-bin,g" \
		${WRAPPER_FILE} > chromium-wrapper
	install -m 0755 chromium-wrapper ${D}${libdir}/chromium/chromium-wrapper
	ln -s ${libdir}/chromium/chromium-wrapper ${D}${bindir}/chromium

	# Chromium *.pak files
	install -m 0644 chrome_*.pak ${D}${libdir}/chromium/
	install -m 0644 resources.pak ${D}${libdir}/chromium/resources.pak

	# Locales.
	install -m 0644 locales/*.pak ${D}${libdir}/chromium/locales/

	# Add extra command line arguments to the chromium-wrapper script by
	# modifying the dummy "CHROME_EXTRA_ARGS" line
	sed -i "s/^CHROME_EXTRA_ARGS=\"\"/CHROME_EXTRA_ARGS=\"${CHROMIUM_EXTRA_ARGS}\"/" ${D}${libdir}/chromium/chromium-wrapper

	if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'component-build', 'component-build', '', d)}" ]; then
		install -m 0755 *.so ${D}${libdir}/chromium/
	fi

	# When building chromium with use_system_minigbm=false,
	# libminigbm.so does not seem to get linked in statically.
	# So we simply check whether it exists in all cases and ship it.
	if [ -e libminigbm.so ]; then
		install -m 0755 libminigbm.so ${D}${libdir}/chromium/
	fi

	# Swiftshader is only built for x86 and x86-64.
	if [ -d "swiftshader" ]; then
		install -d ${D}${libdir}/chromium/swiftshader
		install -m 0644 swiftshader/libEGL.so ${D}${libdir}/chromium/swiftshader/
		install -m 0644 swiftshader/libGLESv2.so ${D}${libdir}/chromium/swiftshader/
	fi

	# ChromeDriver.
	install -m 0755 chromedriver ${D}${bindir}/chromedriver
}

PACKAGES =+ "${PN}-chromedriver"

FILES_${PN}-chromedriver = "${bindir}/chromedriver"

FILES_${PN} = " \
        ${bindir}/chromium \
        ${datadir}/applications/chromium.desktop \
        ${datadir}/icons/hicolor/*x*/apps/chromium.png \
        ${libdir}/chromium/* \
"

PACKAGE_DEBUG_SPLIT_STYLE = "debug-without-src"

# There is no need to ship empty -dev packages.
ALLOW_EMPTY_${PN}-dev = "0"
