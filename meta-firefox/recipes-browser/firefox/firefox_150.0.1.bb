# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-latest:"

SRC_URI += "file://0001-cc-crate-skip-HOST_CFLAGS-and-HOST_CXXFLAGS-env-vars.patch"
SRC_URI:append:libc-musl = " \
    file://musl-remove-single-threaded.h.patch \
    file://fix-musl-with-arm.patch \
    file://webrtc-musl-prctl_mm_map-redefinition.patch \
    file://fix-undeclared-identifier__NR_riscv_hwprobe.patch \
    "

SRC_URI[sha256sum] = "2acffa708d924f5c81ecf08ce99dd9e0cfca2f3797f1a7791f154ed0365f6eec"

PACKAGECONFIG[legacy-appdir] = ""

EXTRA_OECONF:append = "${@bb.utils.contains('PACKAGECONFIG', 'legacy-appdir', ' --with-user-appdir=.mozilla', '', d)}"
