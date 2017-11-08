require chromium.inc
require chromium-unbundle.inc
require gn-utils.inc

inherit distro_features_check gtk-icon-cache qemu

# The actual directory name in out/ is irrelevant for GN.
OUTPUT_DIR = "out/Release"
B = "${S}/${OUTPUT_DIR}"

# Append instead of assigning; the gtk-icon-cache class inherited above also
# adds packages to DEPENDS.
DEPENDS += " \
    alsa-lib \
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
    gtk+ \
    libdrm \
    libwebp \
    libx11 \
    libxcomposite \
    libxcursor \
    libxdamage \
    libxext \
    libxfixes \
    libxi \
    libxml2 \
    libxrandr \
    libxrender \
    libxscrnsaver \
    libxslt \
    libxtst \
    ninja-native \
    nss \
    pango \
    pciutils \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', 'pulseaudio', '', d)} \
    qemu-native \
    virtual/libgl \
"
DEPENDS_append_libc-musl = " libexecinfo"
DEPENDS_append_x86 = "yasm-native"
DEPENDS_append_x86-64 = "yasm-native"

# The wrapper script we use from upstream requires bash.
RDEPENDS_${PN} = "bash"

SRC_URI += "\
        file://add_missing_stat_h_include.patch \
        file://0005-Override-root-filesystem-access-restriction.patch \
        file://0011-Replace-readdir_r-with-readdir.patch \
        ${@bb.utils.contains('PACKAGECONFIG', 'ignore-lost-context', 'file://0001-Remove-accelerated-Canvas-support-from-blacklist.patch', '', d)} \
        file://unset-madv-free.patch \
        file://0001-v8-fix-build-with-gcc7.patch \
        file://0002-WebKit-fix-build-with-gcc7.patch \
        file://0001-openh264-disable-format-security-warning.patch.patch \
        file://0002-replace-struct-ucontext-with-ucontext_t.patch \
        file://0001-make-use-of-existing-gn-args-in-ui-build-config.patch \
        file://0002-gn-Stop-asserting-on-use_gconf-when-looking-for-atk.patch \
        file://0003-gn-Stop-making-use_atk-depend-on-use_gconf.patch \
        file://0004-make-use-of-use_gconf-use_glib-gn-args-in-content-br.patch \
        file://0001-gn-Use-a-separate-flag-for-enabling-libgnome-keyring.patch \
        file://0001-gn-Make-arm_arch-configurable.patch \
        file://0002-gn-Make-arm_fpu-configurable.patch \
        file://v8-qemu-wrapper.patch \
        file://wrapper-extra-flags.patch \
"
SRC_URI_append_libc-musl = "\
        file://musl-support/0001-sandbox-Define-TEMP_FAILURE_RETRY-if-not-defined.patch \
        file://musl-support/0002-breakpad-Replace-__WORDSIZE-with-defines-from-limits.patch \
        file://musl-support/0003-Avoid-mallinfo-APIs-on-non-glibc-linux.patch \
        file://musl-support/0004-include-fcntl.h-for-loff_t.patch \
        file://musl-support/0005-use-off64_t-instead-of-the-internal-__off64_t.patch \
        file://musl-support/0006-linux-glibc-make-the-distinction.patch \
        file://musl-support/0007-allocator-Do-not-include-glibc_weak_symbols-for-musl.patch \
        file://musl-support/0008-Use-correct-member-name-__si_fields-from-LinuxSigInf.patch \
        file://musl-support/0009-Match-syscalls-to-match-musl.patch \
        file://musl-support/0010-Define-res_ninit-and-res_nclose-for-non-glibc-platfo.patch \
        file://musl-support/0011-Do-not-define-__sbrk-on-musl.patch \
        file://musl-support/0012-Adjust-default-pthread-stack-size.patch \
        file://musl-support/0013-include-asm-generic-ioctl.h-for-TCGETS2.patch \
        file://musl-support/0014-link-with-libexecinfo-on-musl.patch \
        file://musl-support/0015-metrics-Keep-GNU-extentions-effective-only-when-usin.patch \
        file://musl-support/0016-getcontext-API-is-unimplemented-in-musl.patch \
        file://musl-support/0017-Use-_fpstate-instead-of-_libc_fpstate.patch \
        file://musl-support/0018-tcmalloc-Use-off64_t-insread-of-__off64_t.patch \
"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE_aarch64 = "(.*)"
COMPATIBLE_MACHINE_armv6 = "(.*)"
COMPATIBLE_MACHINE_armv7a = "(.*)"
COMPATIBLE_MACHINE_armv7ve = "(.*)"
COMPATIBLE_MACHINE_x86 = "(.*)"
COMPATIBLE_MACHINE_x86-64 = "(.*)"

# For now, we need X11 for Chromium to build and run.
REQUIRED_DISTRO_FEATURES = "x11"

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
PACKAGECONFIG[ignore-lost-context] = ""
PACKAGECONFIG[impl-side-painting] = ""
PACKAGECONFIG[kiosk-mode] = ""
PACKAGECONFIG[proprietary-codecs] = ' \
        ffmpeg_branding="Chrome" proprietary_codecs=true, \
        ffmpeg_branding="Chromium" proprietary_codecs=false \
'

# Base GN arguments, mostly related to features we want to enable or disable.
GN_ARGS = " \
        ${PACKAGECONFIG_CONFARGS} \
        is_component_build=${@bb.utils.contains('PACKAGECONFIG', 'component-build', 'true', 'false', d)} \
        use_gconf=false \
        use_gnome_keyring=false \
        use_kerberos=false \
        use_pulseaudio=${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', 'true', 'false', d)} \
"

# From Chromium's BUILDCONFIG.gn:
# Set to enable the official build level of optimization. This has nothing
# to do with branding, but enables an additional level of optimization above
# release (!is_debug). This might be better expressed as a tri-state
# (debug, release, official) but for historical reasons there are two
# separate flags.
# See also: https://groups.google.com/a/chromium.org/d/msg/chromium-dev/hkcb6AOX5gE/PPT1ukWoBwAJ
GN_ARGS += "is_debug=false is_official_build=true"

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
        is_clang=false \
        linux_use_bundled_binutils=false \
        target_cpu="${@gn_target_arch_name(d)}" \
        use_gold=${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', 'true', 'false', d)} \
        v8_snapshot_toolchain="//build/toolchain/yocto:yocto_target" \
'

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
GN_ARGS_append_arm = ' \
        arm_arch="${ARM_ARCH}" \
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

CHROMIUM_EXTRA_ARGS ?= " \
        ${@bb.utils.contains('PACKAGECONFIG', 'use-egl', '--use-gl=egl', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'ignore-lost-context', '--gpu-no-context-lost', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'impl-side-painting', '--enable-gpu-rasterization --enable-impl-side-painting', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'kiosk-mode', '--start-fullscreen --kiosk --no-first-run --incognito', '', d)} \
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

do_configure_prepend_libc-musl() {
	for f in `find ${S}/third_party/ffmpeg/chromium/config/{Chrome,Chromium}/linux/ -name config.h -o -name config.asm`; do
		sed -i -e "s:define HAVE_SYSCTL 1:define HAVE_SYSCTL 0:g" $f
	done
	sed -i -e "s:define HAVE_STRUCT_MALLINFO 1:/*undef HAVE_STRUCT_MALLINFO */:g" ${S}/third_party/tcmalloc/chromium/src/config_linux.h
}

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
		-e "s,@@DEFAULT_FLAGS@@ ,,g" \
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

	# update ROOT_HOME with the root user's $HOME
	sed -i "s#ROOT_HOME#${ROOT_HOME}#" ${D}${libdir}/chromium/chromium-wrapper

	if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'component-build', 'component-build', '', d)}" ]; then
		install -m 0755 *.so ${D}${libdir}/chromium/
	fi

	# ChromeDriver.
	install -m 0755 chromedriver ${D}${bindir}/chromedriver
}

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
