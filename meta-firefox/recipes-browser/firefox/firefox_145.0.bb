# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-latest:"

SRC_URI += "file://0001-cc-crate-skip-HOST_CFLAGS-and-HOST_CXXFLAGS-env-vars.patch"
SRC_URI:append:libc-musl = " \
    file://musl-remove-single-threaded.h.patch \
    file://fix-musl-with-arm.patch \
    "

SRC_URI[sha256sum] = "eb0828db0e942ad345c725e2cbf2ed3b90d23771b054b6db0ded57cfa10b8c9c"

# the current wasi-sdk recipe is too old
PACKAGECONFIG += "disable-sandboxed-libraries"
