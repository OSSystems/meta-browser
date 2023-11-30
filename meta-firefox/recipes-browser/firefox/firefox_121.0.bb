# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox_crates_stable.inc
include firefox.inc

LIC_FILES_CHKSUM = "file://toolkit/content/license.html;md5=e13b71cb6651c085fb08cffb04334366"

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-stable:"

SRC_URI += "git://github.com/franziskuskiefer/cose-rust;protocol=https;branch=master;name=cose-rust;destsuffix=cose-rust \
            git://github.com/rust-minidump/minidump-writer.git;protocol=https;branch=main;name=minidump-writer;destsuffix=minidump-writer \
            git://github.com/rust-minidump/rust-minidump;protocol=https;branch=main;name=minidump-common;destsuffix=minidump-common \
            git://github.com/rust-diplomat/diplomat;protocol=https;branch=main;name=diplomat;destsuffix=diplomat \
            git://github.com/unicode-org/icu4x;protocol=https;branch=main;name=icu4x;destsuffix=icu4x \
            git://github.com/hsivonen/packed_simd.git;protocol=https;branch=0_3_8;name=packed-simd;destsuffix=packed_simd \
            git://github.com/mozilla/mp4parse-rust;protocol=https;branch=master;name=mp4parse;destsuffix=mp4parse \
            git://github.com/gfx-rs/metal-rs;protocol=https;branch=master;name=metal;destsuffix=metal"

SRC_URI[sha256sum] = "edc7a5159d23ff2a23e22bf5abe22231658cee2902b93b5889ee73958aa06aa4"

SRCREV_FORMAT .= "_application-services"
SRCREV_application-services = "c82bccfa500813f273f4db0ead64fc73bfa2b34c"
SRCREV_FORMAT .= "_cose-rust"
SRCREV_cose-rust = "43c22248d136c8b38fe42ea709d08da6355cf04b"
SRCREV_FORMAT .= "_minidump-writer"
SRCREV_minidump-writer = "491eb330e78e310c32927e5cc3bd2350af1e93f8"
SRCREV_FORMAT .= "_minidump-common"
SRCREV_minidump-common = "902c857770fb5c962885f2b1655ca29489cb4332"
SRCREV_FORMAT .= "_diplomat"
SRCREV_diplomat = "8d125999893fedfdf30595e97334c21ec4b18da9"
SRCREV_FORMAT .= "_icu4x"
SRCREV_icu4x = "14e9a3a9857be74582abe2dfa7ab799c5eaac873"
SRCREV_FORMAT .= "_packed-simd"
SRCREV_packed-simd = "412f9a0aa556611de021bde89dee8fefe6e0fbbd"


SRCREV_FORMAT .= "_mp4parse"
SRCREV_mp4parse = "d262e40e7b80f949dcdb4db21caa6dbf1a8b2043"
SRCREV_FORMAT .= "_neqo"
SRCREV_neqo = "34b2c3c45dc8c396f82ef5c3a5246f04bf0adad3"
SRCREV_FORMAT .= "_wgpu"
SRCREV_wgpu = "858d7d590ef9ebfe1b91f6f0925aacde15cce714"
SRCREV_FORMAT .= "_naga"
SRCREV_naga = "92e41b43e437146b5d946eb238de963be1168016"
SRCREV_FORMAT .= "_uniffi-rs"
SRCREV_uniffi-rs = "c0e64b839018728d8153ce1758d391b7782e2e21"
SRCREV_FORMAT .= "_metal"
SRCREV_metal = "f507da4686234e658f31de54d2aa0dfa8abd236b"

SRCREV_FORMAT .= "_cssparser"
SRCREV_cssparser = "aaa966d9d6ae70c4b8a62bb5e3a14c068bb7dff0"
SRCREV_FORMAT .= "_wgpu"
SRCREV_wgpu = "3ec547cdcaaa14488327d8f1b5f7736278c4178d"
SRCREV_FORMAT .= "_audioipc"
SRCREV_audioipc = "6be424d75f1367e70f2f5ddcacd6d0237e81a6a9"
SRCREV_FORMAT .= "_wpf-gpu-raster"
SRCREV_wpf-gpu-raster = "99979da091fd58fba8477e7fcdf5ec0727102916"


PACKAGECONFIG[x11-only] = "--enable-default-toolkit=cairo-gtk3-x11-only,,,,,wayland-only"
PACKAGECONFIG[wayland-only] = "--enable-default-toolkit=cairo-gtk3-wayland-only,,virtual/egl,,,x11-only"

do_configure:append(){
    wayland_only='${@bb.utils.contains("PACKAGECONFIG", "wayland-only", "true", "false", d)}'
    x11_only='${@bb.utils.contains("PACKAGECONFIG", "x11-only", "true", "false", d)}'

    if [ "$x11_only" = "true" ]; then
        sed -i '/ac_add_options --enable-default-toolkit=cairo-gtk3$/d' ${MOZCONFIG}
    fi

    if [ "$wayland_only" = "true" ]; then
        sed -i '/ac_add_options --enable-default-toolkit=cairo-gtk3-wayland$/d' ${MOZCONFIG}
    fi
}
