require chromium-x11.inc

SRC_URI[md5sum] = "98cf7b6eca255e2422f96094eccc1887"
SRC_URI[sha256sum] = "cabc4d267bf08aabe11c5739048c43dde18c61acf595223a1c3aa1d3499558d4"

SRC_URI += "\
        file://0001-Replace-remaining-references-to-struct-ucontext-with.patch \
        file://0001-Rename-ArrayBufferContents-AllocationKind-to-GetAllo.patch \
        file://0001-aec3-Use-fabsf-instead-of-std-abs-for-floats.patch \
        file://0001-WebCORS-Use-WebString-directly-instead-of-converting.patch \
        file://0001-More-conservative-check-for-string_view-availability.patch \
        file://chromium-gcc5-cxx14-workaround.patch \
        file://chromium-gcc5-workarounds.patch \
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
        file://musl-support/0019-Disable-HAVE_SECURE_GETENV.patch \
        file://musl-support/0020-Disable-mallinfo-usage-in-base.patch \
"

# For now, we need X11 for Chromium to build and run.
REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS_append_libc-musl = " libexecinfo"

# Compatibility glue while we have both chromium-x11 and
# chromium-ozone-wayland recipes, and the former used to be called just
# "chromium".
PROVIDES = "chromium"
