DESCRIPTION = "Chromium is an open-source browser project that aims to build a safer, faster, and more stable way for all users to experience the web."
HOMEPAGE = "https://www.chromium.org/Home"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0fca02217a5d49a14dfe2d11837bb34d"

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
SRC_URI[md5sum] = "3f596ecbd6a39d5ada29f11780ec6dcf"
SRC_URI[sha256sum] = "f038e72cbd8b7383d13c286329623fda8d6d48f45fa2d964e554b5565283ad71"

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
