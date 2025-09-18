# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-latest:"

SRC_URI += "file://0001-cc-crate-skip-HOST_CFLAGS-and-HOST_CXXFLAGS-env-vars.patch"

SRC_URI[sha256sum] = "6c45ca38091820c2c9925a85c80c04120de0ced98589065358c34d257e1edf83"
