# Recipe files have to perform the following tasks after including this file:
# 1) Add patches to SRC_URI. Platform-specific patches should be contained in
#    either "chromium-x11" or "chromium-wayland". There are also patches that
#    are shared amongst platforms but may one day no longer be needed. These
#    do not belong in such a subdirectory, but still need to be explicitely be
#    added.
# 2) Add md5sum and sha256sum hashes of the tarball.

require chromium.inc

DESCRIPTION = "Chromium is an open-source browser project that aims to build a safer, faster, and more stable way for all users to experience the web."
HOMEPAGE = "https://www.chromium.org/Home"

DEPENDS += "libgnome-keyring gperf-native"
DEPENDS_append_libc-musl = " libexecinfo"

SRC_URI = "\
        https://commondatastorage.googleapis.com/chromium-browser-official/chromium-${PV}.tar.xz \
        file://include.gypi \
        file://oe-defaults.gypi \
        file://google-chrome \
        file://google-chrome.desktop \
        file://unset-madv-free.patch \
        file://0001-v8-fix-build-with-gcc7.patch \
        file://0002-WebKit-fix-build-with-gcc7.patch \
	file://0001-openh264-disable-format-security-warning.patch.patch \
	file://0002-replace-struct-ucontext-with-ucontext_t.patch \
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

PACKAGECONFIG ??= "use-egl"

# this makes sure the dependencies for the EGL mode are present; otherwise, the configure scripts
# automatically and silently fall back to GLX
PACKAGECONFIG[use-egl] = ",,virtual/egl virtual/libgles2"

# Empty PACKAGECONFIG options listed here to avoid warnings.
# The .bb file should use these to conditionally add patches
# and command-line switches (extra dependencies should not
# be necessary but are OK to add).
PACKAGECONFIG[component-build] = ""
PACKAGECONFIG[ignore-lost-context] = ""
PACKAGECONFIG[impl-side-painting] = ""
PACKAGECONFIG[kiosk-mode] = ""
PACKAGECONFIG[proprietary-codecs] = ""


# These are present as their own variables, since they have changed between versions
# a few times in the past already; making them variables makes it easier to handle that
CHROMIUM_X11_GYP_DEFINES ?= ""


CHROMIUM_EXTRA_ARGS ?= " \
        ${@bb.utils.contains('PACKAGECONFIG', 'use-egl', '--use-gl=egl', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'ignore-lost-context', '--gpu-no-context-lost', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'impl-side-painting', '--enable-gpu-rasterization --enable-impl-side-painting', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'kiosk-mode', '--start-fullscreen --kiosk --no-first-run --incognito', '', d)} \
"


EXTRA_OEGYP = " \
	-Dangle_use_commit_id=0 \
	-Dclang=0 \
	-Dhost_clang=0 \
	-Ddisable_fatal_linker_warnings=1 \
	-Dwerror= \
	-Dv8_use_external_startup_data=0 \
	-Dlinux_use_bundled_gold=0 \
	-Dlinux_use_bundled_binutils=0 \
	-I ${WORKDIR}/oe-defaults.gypi \
	-I ${WORKDIR}/include.gypi \
	${@bb.utils.contains('PACKAGECONFIG', 'component-build', '-Dcomponent=shared_library', '', d)} \
	${@bb.utils.contains('PACKAGECONFIG', 'proprietary-codecs', '-Dproprietary_codecs=1 -Dffmpeg_branding=Chrome', '', d)} \
	-Dlinux_use_gold_flags=${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', '1', '0', d)} \
	-f ninja \
"

# API keys for accessing Google services. By default, we use an invalid key
# only to prevent the "you are missing an API key" infobar from being shown on
# startup.
# See https://dev.chromium.org/developers/how-tos/api-keys for more information
# on how to obtain your own keys. You can then set the variables below in
# local.conf or a .bbappend file.
GOOGLE_API_KEY ??= "invalid-api-key"
GOOGLE_DEFAULT_CLIENT_ID ??= "invalid-client-id"
GOOGLE_DEFAULT_CLIENT_SECRET ??= "invalid-client-secret"
EXTRA_OEGYP += "\
        -Dgoogle_api_key=${GOOGLE_API_KEY} \
        -Dgoogle_default_client_id=${GOOGLE_DEFAULT_CLIENT_ID} \
        -Dgoogle_default_client_secret=${GOOGLE_DEFAULT_CLIENT_SECRET} \
"

python() {
    d.appendVar('GYP_DEFINES', ' %s ' % d.getVar('CHROMIUM_X11_GYP_DEFINES', True))
}

do_configure_append() {

	build/gyp_chromium --depth=. ${EXTRA_OEGYP}

}

do_compile[progress] = "outof:^\[(\d+)/(\d+)\]\s+"

do_compile() {
        # build with ninja
        ninja -v ${PARALLEL_MAKE} chrome chrome_sandbox chromedriver
}

do_install_append() {

	# Add extra command line arguments to google-chrome script by modifying
        # the dummy "CHROME_EXTRA_ARGS" line
        sed -i "s/^CHROME_EXTRA_ARGS=\"\"/CHROME_EXTRA_ARGS=\"${CHROMIUM_EXTRA_ARGS}\"/" ${D}${bindir}/chromium

        # update ROOT_HOME with the root user's $HOME
        sed -i "s#ROOT_HOME#${ROOT_HOME}#" ${D}${bindir}/chromium

        if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'component-build', 'component-build', '', d)}" ]; then
                install -m 0755 lib/*.so ${D}${libdir}/chromium/
        fi
}
