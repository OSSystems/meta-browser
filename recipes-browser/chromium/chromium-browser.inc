# Recipe files have to perform the following tasks after including this file:
# 1) Add patches to SRC_URI. Platform-specific patches should be contained in
#    either "chromium-x11" or "chromium-wayland". There are also patches that
#    are shared amongst platforms but may one day no longer be needed. These
#    do not belong in such a subdirectory, but still need to be explicitely be
#    added.
# 2) Add md5sum and sha256sum hashes of the tarball.

include chromium.inc

DESCRIPTION = "Chromium browser"
DEPENDS += "libgnome-keyring gperf-native"
DEPENDS_append_libc-musl = " libexecinfo"

SRC_URI = "\
        http://gsdview.appspot.com/chromium-browser-official/chromium-${PV}.tar.xz \
        file://include.gypi \
        file://oe-defaults.gypi \
        file://google-chrome \
        ${@bb.utils.contains('PACKAGECONFIG', 'disable-api-keys-info-bar', 'file://api-keys.patch;patchdir=${WORKDIR}', '', d)} \
        file://google-chrome.desktop \
        file://unset-madv-free.patch \
        file://0001-v8-fix-build-with-gcc7.patch \
        file://0002-WebKit-fix-build-with-gcc7.patch \
	file://0001-openh264-disable-format-security-warning.patch.patch \
	file://0002-replace-struct-ucontext-with-ucontext_t.patch \
"
SRC_URI_append_libc-musl = "\
           file://0001-sandbox-Define-TEMP_FAILURE_RETRY-if-not-defined.patch \
           file://0002-breakpad-Replace-__WORDSIZE-with-defines-from-limits.patch \
           file://0003-Avoid-mallinfo-APIs-on-non-glibc-linux.patch \
           file://0004-include-fcntl.h-for-loff_t.patch \
           file://0005-use-off64_t-instead-of-the-internal-__off64_t.patch \
           file://0006-linux-glibc-make-the-distinction.patch \
           file://0007-allocator-Do-not-include-glibc_weak_symbols-for-musl.patch \
           file://0008-Use-correct-member-name-__si_fields-from-LinuxSigInf.patch \
           file://0009-Match-syscalls-to-match-musl.patch \
           file://0010-Define-res_ninit-and-res_nclose-for-non-glibc-platfo.patch \
           file://0011-Do-not-define-__sbrk-on-musl.patch \
           file://0012-Adjust-default-pthread-stack-size.patch \
           file://0013-include-asm-generic-ioctl.h-for-TCGETS2.patch \
           file://0014-link-with-libexecinfo-on-musl.patch \
           file://0015-metrics-Keep-GNU-extentions-effective-only-when-usin.patch \
           file://0016-getcontext-API-is-unimplemented-in-musl.patch \
           file://0017-Use-_fpstate-instead-of-_libc_fpstate.patch \
           file://0018-tcmalloc-Use-off64_t-insread-of-__off64_t.patch \
"

S = "${WORKDIR}/chromium-${PV}"
BPN = "chromium"


# PACKAGECONFIG options
# ^^^^^^^^^^^^^^^^^^^^^
# * use-egl: (on by default)
#       Without this packageconfig, the Chromium build will use GLX for
#       creating an OpenGL context in X11, and regular OpenGL for painting
#       operations. Neither are desirable on embedded platforms. With this
#       packageconfig, EGL and OpenGL ES 2.x are used instead.
#
# * disable-api-keys-info-bar: (off by default)
#       This disables the info bar that warns: "Google API keys are missing".
#       With some builds, missing API keys are considered OK, so the bar needs
#       to go. Conversely, if Chromium is compiled with this option off and
#       the user wishes to disable the warning, the following lines can be
#       added to the "google-chrome" script (see patchset) before the
#       chromium binary is called:
#           export GOOGLE_API_KEY="no"
#           export GOOGLE_DEFAULT_CLIENT_ID="no"
#           export GOOGLE_DEFAULT_CLIENT_SECRET="no"
#
# * component-build: (off by default)
#       Enables component build mode. By default, all of Chromium (with the
#       exception of FFmpeg) is linked into one big binary. The linker step
#       requires at least 8 GB RAM. Component mode was created to facilitate
#       development and testing, since with it, there is not one big binary;
#       instead, each component is linked to a separate shared object. Use
#       component mode for development, testing, and in case the build machine
#       is not a 64-bit one, or has less than 8 GB RAM.
#
# * ignore-lost-context: (off by default)
#       There is a flaw in the HTML Canvas specification. When the canvas'
#       backing store is some kind of hardware resource like an OpenGL
#       texture, this resource might get lost. In case of OpenGL textures,
#       this happens when the OpenGL context gets lost. The canvas should then
#       be repainted, but nothing in the Canvas standard reflects that. This
#       packageconfig is to be used if the underlying OpenGL (ES) drivers do
#       not lose the context, or if losing the context is considered okay
#       (note that canvas contents can vanish then).
#
# * impl-side-painting: (off by default)
#       This is a new painting mechanism. Still in
#       development stages, it can improve performance See
#       http://www.chromium.org/developers/design-documents/impl-side-painting
#       for more.
#
# * kiosk-mode: (off by default)
#       Enable this option if you want your browser to start up full-screen,
#       without any menu bars, without any clutter, and without any initial
#       start-up indicators.
#
# * proprietary-codecs: (off by default)
#       Enable this option if you want to add support for additional proprietary
#       codecs, most notably MPEG standards (h.264, h.265, MP4, MP3, AAC, MPEG-2 ..)

PACKAGECONFIG ??= "use-egl build-chromedriver"

# this makes sure the dependencies for the EGL mode are present; otherwise, the configure scripts
# automatically and silently fall back to GLX
PACKAGECONFIG[use-egl] = ",,virtual/egl virtual/libgles2"

# Empty PACKAGECONFIG options listed here to avoid warnings.
# The .bb file should use these to conditionally add patches
# and command-line switches (extra dependencies should not
# be necessary but are OK to add).
PACKAGECONFIG[disable-api-keys-info-bar] = ""
PACKAGECONFIG[component-build] = ""
PACKAGECONFIG[ignore-lost-context] = ""
PACKAGECONFIG[impl-side-painting] = ""
PACKAGECONFIG[kiosk-mode] = ""
PACKAGECONFIG[proprietary-codecs] = ""
PACKAGECONFIG[build-chromedriver] = ""


# These are present as their own variables, since they have changed between versions
# a few times in the past already; making them variables makes it easier to handle that
CHROMIUM_X11_GYP_DEFINES ?= ""


CHROMIUM_EXTRA_ARGS ?= " \
        ${@bb.utils.contains('PACKAGECONFIG', 'use-egl', '--use-gl=egl', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'ignore-lost-context', '--gpu-no-context-lost', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'impl-side-painting', '--enable-gpu-rasterization --enable-impl-side-painting', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'kiosk-mode', '--start-fullscreen --kiosk --no-first-run --incognito', '', d)} \
"


# uncomment below or set it in a bbappend if you want to use gold linker
# to link chromium irrespective of system linker, gold speeds up linking
# chromium ( 8mins to < 2mins on a decent xeon system)
#
#USEGOLD = "-Dlinux_use_gold_flags=1"
USEGOLD ??= ""

EXTRA_OEGYP = " \
	-Dangle_use_commit_id=0 \
	-Dclang=0 \
	-Dhost_clang=0 \
	-Ddisable_fatal_linker_warnings=1 \
	-Dv8_use_external_startup_data=0 \
	-Dlinux_use_bundled_gold=0 \
	-Dlinux_use_bundled_binutils=0 \
	-I ${WORKDIR}/oe-defaults.gypi \
	-I ${WORKDIR}/include.gypi \
	${@bb.utils.contains('PACKAGECONFIG', 'component-build', '-I ${WORKDIR}/component-build.gypi', '', d)} \
	${@bb.utils.contains('PACKAGECONFIG', 'proprietary-codecs', '-Dproprietary_codecs=1 -Dffmpeg_branding=Chrome', '', d)} \
	${USEGOLD} \
	-f ninja \
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
        ninja -C ${S}/out/${CHROMIUM_BUILD_TYPE} ${PARALLEL_MAKE} chrome chrome_sandbox \
		${@bb.utils.contains('PACKAGECONFIG', 'build-chromedriver', 'chromedriver', '', d)}
}

do_install_append() {

	# Add extra command line arguments to google-chrome script by modifying
        # the dummy "CHROME_EXTRA_ARGS" line
        sed -i "s/^CHROME_EXTRA_ARGS=\"\"/CHROME_EXTRA_ARGS=\"${CHROMIUM_EXTRA_ARGS}\"/" ${D}${bindir}/google-chrome

        # update ROOT_HOME with the root user's $HOME
        sed -i "s#ROOT_HOME#${ROOT_HOME}#" ${D}${bindir}/google-chrome

	# Always adding this libdir (not just with component builds), because the
        # LD_LIBRARY_PATH line in the google-chromium script refers to it
        install -d ${D}${libdir}/${BPN}/
        if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'component-build', 'component-build', '', d)}" ]; then
                install -m 0755 ${B}/out/${CHROMIUM_BUILD_TYPE}/lib/*.so ${D}${libdir}/${BPN}/
        fi
}
