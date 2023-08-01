# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox_crates_esr.inc
include firefox.inc

LIC_FILES_CHKSUM = "file://toolkit/content/license.html;md5=1b074cb88f7e9794d795c1346bcc9c80"

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-esr:"

SRC_URI += "file://fixes/Bug-1841197-Undefine-the-mips-builtin-macro-on-mips-.patch "

SRC_URI[sha256sum] = "0bcc571c44f94ac6b8c26e896fd771eb7bd41b2a8ec35598bced0102c1b855fa"
