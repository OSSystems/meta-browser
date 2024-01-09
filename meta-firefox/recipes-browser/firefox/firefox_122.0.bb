# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox_crates_stable.inc
include firefox.inc

LIC_FILES_CHKSUM = "file://toolkit/content/license.html;md5=34509cf93e5a458d990638bceb12e14a"

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-stable:"

SRC_URI += "git://github.com/franziskuskiefer/cose-rust;protocol=https;branch=master;name=cose-rust;destsuffix=cose-rust \
            git://github.com/rust-minidump/minidump-writer.git;protocol=https;branch=main;name=minidump-writer;destsuffix=minidump-writer \
            git://github.com/rust-minidump/rust-minidump;protocol=https;branch=main;name=minidump-common;destsuffix=minidump-common \
            git://github.com/rust-diplomat/diplomat;protocol=https;branch=main;name=diplomat;destsuffix=diplomat \
            git://github.com/unicode-org/icu4x;protocol=https;branch=main;name=icu4x;destsuffix=icu4x \
            git://github.com/hsivonen/packed_simd.git;protocol=https;branch=0_3_8;name=packed-simd;destsuffix=packed_simd \
            git://github.com/mozilla/mp4parse-rust;protocol=https;branch=master;name=mp4parse;destsuffix=mp4parse \
            git://github.com/gfx-rs/metal-rs;protocol=https;branch=master;name=metal;destsuffix=metal \
            git://github.com/rust-lang/rust-bindgen;protocol=https;branch=main;name=bindgen;destsuffix=bindgen \
            git://github.com/seanmonstar/warp;protocol=https;branch=master;name=warp;destsuffix=warp"

SRC_URI[sha256sum] = "b84815a90e147965e4c0b50599c85b1022ab0fce42105e5ef45c630dcca5dec3"

SRCREV_FORMAT .= "_application-services"
SRCREV_application-services = "57ecda94fae3226cf67b3e12ce7d6b504e0e2417"
SRCREV_FORMAT .= "_cose-rust"
SRCREV_cose-rust = "43c22248d136c8b38fe42ea709d08da6355cf04b"
SRCREV_FORMAT .= "_minidump-writer"
SRCREV_minidump-writer = "99c561931fe8cf1fa2135b3f23ff6588bef8fd1e"
SRCREV_FORMAT .= "_minidump-common"
SRCREV_minidump-common = "c3de84b061339c686a572fb9f059e7ba3fad38d6"
SRCREV_FORMAT .= "_diplomat"
SRCREV_diplomat = "8d125999893fedfdf30595e97334c21ec4b18da9"
SRCREV_FORMAT .= "_icu4x"
SRCREV_icu4x = "14e9a3a9857be74582abe2dfa7ab799c5eaac873"
SRCREV_FORMAT .= "_packed-simd"
SRCREV_packed-simd = "412f9a0aa556611de021bde89dee8fefe6e0fbbd"


SRCREV_FORMAT .= "_mp4parse"
SRCREV_mp4parse = "d262e40e7b80f949dcdb4db21caa6dbf1a8b2043"
SRCREV_FORMAT .= "_neqo"
SRCREV_neqo = "83735a88217a6b3a6a9d3cd5d9243040c5e41319"
SRCREV_FORMAT .= "_wgpu"
SRCREV_wgpu = "6dc9ccab8592645fda3204be1cfb5929fd7f924d"
SRCREV_FORMAT .= "_naga"
SRCREV_naga = "92e41b43e437146b5d946eb238de963be1168016"
SRCREV_FORMAT .= "_uniffi-rs"
SRCREV_uniffi-rs = "c0e64b839018728d8153ce1758d391b7782e2e21"
SRCREV_FORMAT .= "_metal"
SRCREV_metal = "f507da4686234e658f31de54d2aa0dfa8abd236b"

SRCREV_FORMAT .= "_cssparser"
SRCREV_cssparser = "aaa966d9d6ae70c4b8a62bb5e3a14c068bb7dff0"
SRCREV_FORMAT .= "_audioipc"
SRCREV_audioipc = "6be424d75f1367e70f2f5ddcacd6d0237e81a6a9"
SRCREV_FORMAT .= "_wpf-gpu-raster"
SRCREV_wpf-gpu-raster = "99979da091fd58fba8477e7fcdf5ec0727102916"

SRCREV_FORMAT .= "_warp"
SRCREV_warp = "9d081461ae1167eb321585ce424f4fef6cf0092b"
SRCREV_FORMAT .= "_cubeb-pulse"
SRCREV_cubeb-pulse = "c04c4d2c7f2291cb81a1c48f5a8c425748f18cd8"

SRCREV_FORMAT .= "_bindgen"
SRCREV_bindgen = "86f3dbe846020e2ba573d6eb38d1434d0cbcbb40"

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
