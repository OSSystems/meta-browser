# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox_crates_stable.inc
include firefox.inc

LIC_FILES_CHKSUM = "file://toolkit/content/license.html;md5=1c7c8a999b435cb71deb175b3d59b7fe"

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-stable:"

SRC_URI += "git://github.com/franziskuskiefer/cose-rust;protocol=https;branch=master;name=cose-rust;destsuffix=cose-rust"

SRC_URI[sha256sum] = "215d076945bf744327a252f498227ab68e9ba4e3b703e058683d9e4ab92cba76"

SRCREV_cose-rust = "43c22248d136c8b38fe42ea709d08da6355cf04b"

PACKAGECONFIG[x11-only] = ",,,"
PACKAGECONFIG[wayland-only] = ",,,"

EXTRA_OECONF += '${@bb.utils.contains("PACKAGECONFIG", "x11-only", "--enable-default-toolkit=cairo-gtk3-x11-only", "", d)}'
EXTRA_OECONF += '${@bb.utils.contains("PACKAGECONFIG", "wayland-only", "--enable-default-toolkit=cairo-gtk3-wayland-only", "", d)}'

do_configure:prepend(){
    x11_only='${@bb.utils.contains("PACKAGECONFIG", "x11-only", "true", "false", d)}'
    wayland_only='${@bb.utils.contains("PACKAGECONFIG", "wayland-only", "true", "false", d)}'
    if [ "$x11_only" = "true" -a "$wayland_only" = "true" ]; then
        bbfatal "Only one of x11-only and wayland-only can be present in PACKAGECONFIG!"
        exit 1
    fi
}
