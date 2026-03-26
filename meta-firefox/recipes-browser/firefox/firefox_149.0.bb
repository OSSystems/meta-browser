# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-latest:"

SRC_URI += "file://0001-cc-crate-skip-HOST_CFLAGS-and-HOST_CXXFLAGS-env-vars.patch"
SRC_URI:append:libc-musl = " \
    file://musl-remove-single-threaded.h.patch \
    file://fix-musl-with-arm.patch \
    file://webrtc-musl-prctl_mm_map-redefinition.patch \
    file://musl-add-missing-headers.patch \
    "

SRC_URI[sha256sum] = "b861fdee999d9b6404e1e865d6f707c41b4bded1b5ea62affc176288c1484b8a"

PACKAGECONFIG[legacy-appdir] = ""

EXTRA_OECONF:append = "${@bb.utils.contains('PACKAGECONFIG', 'legacy-appdir', ' --with-user-appdir=.mozilla', '', d)}"
