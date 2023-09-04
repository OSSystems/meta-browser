# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox_crates_esr.inc
include firefox.inc

LIC_FILES_CHKSUM = "file://toolkit/content/license.html;md5=1b074cb88f7e9794d795c1346bcc9c80"

SRC_URI += "git://github.com/hsivonen/packed_simd.git;protocol=https;branch=0_3_9;name=packed-simd;destsuffix=packed_simd"

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-esr:"

SRC_URI[sha256sum] = "7dda256e49ca054f72d495914a2d82533518d4472e06f45f85ed763897aa1e53"

SRCREV_application-services = "86c84c217036c12283d19368867323a66bf35883"
SRCREV_packed-simd = "e588ceb568878e1a3156ea9ce551d5b63ef0cdc4"
