# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-latest:"

SRC_URI += " \
    file://0001-cc-crate-skip-HOST_CFLAGS-and-HOST_CXXFLAGS-env-vars.patch \
    "
SRC_URI:append:libc-musl = " \
    file://fix-musl-with-arm.patch \
    file://webrtc-musl-prctl_mm_map-redefinition.patch \
    "
SRC_URI:append:riscv64 = " file://detect-new-riscv64-extensions-on-old-kernel.patch"

SRC_URI[sha256sum] = "ac586cfe4573cd02a0b96dd496ae28da0157a9fcc6530ede6b1decfd2a3fb512"

PACKAGECONFIG[legacy-appdir] = ""

EXTRA_OECONF:append = "${@bb.utils.contains('PACKAGECONFIG', 'legacy-appdir', ' --with-user-appdir=.mozilla', '', d)}"
