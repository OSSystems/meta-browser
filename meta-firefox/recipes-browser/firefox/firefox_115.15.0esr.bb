# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox_crates_esr.inc
include firefox.inc

LIC_FILES_CHKSUM = "file://toolkit/content/license.html;md5=1b074cb88f7e9794d795c1346bcc9c80"

SRC_URI += "git://github.com/hsivonen/packed_simd.git;protocol=https;branch=0_3_9;name=packed-simd;destsuffix=packed_simd \
            git://github.com/gfx-rs/d3d12-rs;protocol=https;branch=master;name=d3d12-rs;destsuffix=d3d12-rs \
            git://github.com/glandium/warp.git;protocol=https;branch=pemfile;name=warp;destsuffix=warp \
            git://github.com/gfx-rs/naga;protocol=https;branch=master;name=naga;destsuffix=naga \
            git://github.com/mozilla/uniffi-rs.git;protocol=https;branch=main;name=uniffi-rs;destsuffix=uniffi-rs \
            git://github.com/mozilla/application-services.git;protocol=https;branch=main;name=application-services;destsuffix=application-services \
            file://debian-hacks/Work-around-bz-1775202-to-fix-FTBFS-on-ppc64el.patch \
            file://0004-fix-compilation-with-clang18.patch \
            file://0005-non-constant-expression-cannot-be-narrowed-from.patch"

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-esr:"

SRC_URI[sha256sum] = "effed92aa0d961871614c611259dfe3eab72e5ebfe8f2405f9bc92c5e7feae81"

SRCREV_FORMAT .= "_application-services"
SRCREV_application-services = "86c84c217036c12283d19368867323a66bf35883"
SRCREV_FORMAT .= "_packed-simd"
SRCREV_packed-simd = "e588ceb568878e1a3156ea9ce551d5b63ef0cdc4"

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

SRCREV_FORMAT .= "_audioipc"
SRCREV_audioipc = "0b51291d2483a17dce3e300c7784b369e02bee73"

SRCREV_FORMAT .= "_wpf-gpu-raster"
SRCREV_wpf-gpu-raster = "5ab6fe33d00021325ee920b3c10526dc8301cf46"

SRCREV_FORMAT .= "_warp"
SRCREV_warp = "4af45fae95bc98b0eba1ef0db17e1dac471bb23d"

SRCREV_FORMAT .= "_cubeb-pulse"
SRCREV_cubeb-pulse = "cf48897be5cbe147d051ebbbe1eaf5fd8fb6bbc9"

SRCREV_FORMAT .="_midir"
SRCREV_midir = "519e651241e867af3391db08f9ae6400bc023e18"

SRCREV_FORMAT .= "_cubeb-coreaudio"
SRCREV_cubeb-coreaudio = "93b5c01a131f65c83c11aeb317f4583405c5eb79"

SRCREV_FORMAT .= "_aa-stroke"
SRCREV_aa-stroke = "07d3c25322518f294300e96246e09b95e118555d"

SRCREV_FORMAT .= "_jsparagus"
SRCREV_jsparagus = "64ba08e24749616de2344112f226d1ef4ba893ae"

SRCREV_FORMAT .= "_mapped_hyph"
SRCREV_mapped_hyph = "c7651a0cffff41996ad13c44f689bd9cd2192c01"
