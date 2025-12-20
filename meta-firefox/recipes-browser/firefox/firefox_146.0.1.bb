# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-latest:"

SRC_URI += "file://0001-cc-crate-skip-HOST_CFLAGS-and-HOST_CXXFLAGS-env-vars.patch"
SRC_URI:append:libc-musl = " \
    file://musl-remove-single-threaded.h.patch \
    file://fix-musl-with-arm.patch \
    file://webrtc-musl-prctl_mm_map-redefinition.patch \
    "

SRC_URI[sha256sum] = "e9678a0e8473923953e1dc312c37919068623b6aa20adade16266049258191eb"
