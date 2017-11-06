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

SRC_URI = "\
        https://commondatastorage.googleapis.com/chromium-browser-official/chromium-${PV}.tar.xz \
        file://include.gypi \
        file://oe-defaults.gypi \
        file://unset-madv-free.patch \
        file://0001-v8-fix-build-with-gcc7.patch \
        file://0002-WebKit-fix-build-with-gcc7.patch \
	file://0001-openh264-disable-format-security-warning.patch.patch \
	file://0002-replace-struct-ucontext-with-ucontext_t.patch \
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

PACKAGECONFIG ??= "use-egl"

# this makes sure the dependencies for the EGL mode are present; otherwise, the configure scripts
# automatically and silently fall back to GLX
PACKAGECONFIG[use-egl] = ",,virtual/egl virtual/libgles2"

# Empty PACKAGECONFIG options listed here to avoid warnings.
# The .bb file should use these to conditionally add patches
# and command-line switches (extra dependencies should not
# be necessary but are OK to add).
PACKAGECONFIG[component-build] = ""
PACKAGECONFIG[cups] = "-Duse_cups=1,-Duse_cups=0,cups"
PACKAGECONFIG[ignore-lost-context] = ""
PACKAGECONFIG[impl-side-painting] = ""
PACKAGECONFIG[kiosk-mode] = ""
PACKAGECONFIG[proprietary-codecs] = ""

CHROMIUM_EXTRA_ARGS ?= " \
        ${@bb.utils.contains('PACKAGECONFIG', 'use-egl', '--use-gl=egl', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'ignore-lost-context', '--gpu-no-context-lost', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'impl-side-painting', '--enable-gpu-rasterization --enable-impl-side-painting', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'kiosk-mode', '--start-fullscreen --kiosk --no-first-run --incognito', '', d)} \
"
