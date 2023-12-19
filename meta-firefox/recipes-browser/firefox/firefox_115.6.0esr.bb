# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox_crates_esr.inc
include firefox.inc

LIC_FILES_CHKSUM = "file://toolkit/content/license.html;md5=1b074cb88f7e9794d795c1346bcc9c80"

SRC_URI += "git://github.com/hsivonen/packed_simd.git;protocol=https;branch=0_3_9;name=packed-simd;destsuffix=packed_simd \
            git://github.com/gfx-rs/d3d12-rs;protocol=https;branch=master;name=d3d12-rs;destsuffix=d3d12-rs \
            git://git@github.com/dtolnay/syn.git;protocol=https;branch=master;name=syn;destsuffix=syn"

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-esr:"

SRC_URI[sha256sum] = "66d7e6e5129ac8e6fe83e24227dc7bb8dc42650bc53b21838e614de80d22bc66"

SRCREV_FORMAT .= "_application-services"
SRCREV_application-services = "86c84c217036c12283d19368867323a66bf35883"
SRCREV_FORMAT .= "_packed-simd"
SRCREV_packed-simd = "e588ceb568878e1a3156ea9ce551d5b63ef0cdc4"
SRCREV_FORMAT .= "_syn"
SRCREV_syn = "43632bfb6c78ee1f952645a268ab1ac4af162977"

SRCREV_FORMAT .= "_d3d12-rs"
SRCREV_d3d12-rs = "b940b1d71ab7083ae80eec697872672dc1f2bd32"

SRCREV_FORMAT .= "_neqo"
SRCREV_neqo = "80db3a01f3273c7e742ba560fa99246fc8b30c4f"

SRCREV_FORMAT .= "_mp4parse"
SRCREV_mp4parse = "cf8b0e04de9c60f38f7f057f9f29c74d19336d0c"

SRCREV_FORMAT .= "_wgpu"
SRCREV_wgpu = "f71a1bc736fde37509262ca03e91d8f56a13aeb5"

SRCREV_FORMAT .= "_naga"
SRCREV_naga = "b99d58ea435090e561377949f428bce2c18451bb"

SRCREV_FORMAT .= "_uniffi-rs"
SRCREV_uniffi-rs = "bc7ff8977bf38d0fdd1a458810b14f434d4dc4de"

SRCREV_FORMAT .= "_cssparser"
SRCREV_cssparser = "45bc47e2bcb846f1efb5aea156be5fe7d18624bf"

SRCREV_FORMAT .= "_wgpu"
SRCREV_wgpu = "f71a1bc736fde37509262ca03e91d8f56a13aeb5"

SRCREV_FORMAT .= "_audioipc"
SRCREV_audioipc = "0b51291d2483a17dce3e300c7784b369e02bee73"

SRCREV_FORMAT .= "_wpf-gpu-raster"
SRCREV_wpf-gpu-raster = "5ab6fe33d00021325ee920b3c10526dc8301cf46"
