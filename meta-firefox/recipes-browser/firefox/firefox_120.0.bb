# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox_crates_stable.inc
include firefox.inc

LIC_FILES_CHKSUM = "file://toolkit/content/license.html;md5=1c51c8501f5b0200190264e84ebfc8b6"

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-stable:"

SRC_URI += "git://github.com/franziskuskiefer/cose-rust;protocol=https;branch=master;name=cose-rust;destsuffix=cose-rust \
            git://github.com/rust-minidump/minidump-writer.git;protocol=https;branch=main;name=minidump-writer;destsuffix=minidump-writer \
            git://github.com/rust-minidump/rust-minidump;protocol=https;branch=main;name=minidump-common;destsuffix=minidump-common \
            git://github.com/rust-diplomat/diplomat;protocol=https;branch=main;name=diplomat;destsuffix=diplomat \
            git://github.com/unicode-org/icu4x;protocol=https;branch=main;name=icu4x;destsuffix=icu4x \
            git://github.com/hsivonen/packed_simd.git;protocol=https;branch=0_3_8;name=packed-simd;destsuffix=packed_simd \
            git://github.com/mozilla/mp4parse-rust;protocol=https;branch=master;name=mp4parse;destsuffix=mp4parse \
            git://github.com/gfx-rs/metal-rs;protocol=https;branch=master;name=metal;destsuffix=metal"

SRC_URI[sha256sum] = "e710058701074eda53ca9f5fd52c57254858a027984f735bdcd58d6906f6b574"

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
SRCREV_neqo = "23ae8e8b75b412c1c10fa44fc3f68e5422342b86"
SRCREV_FORMAT .= "_wgpu"
SRCREV_wgpu = "858d7d590ef9ebfe1b91f6f0925aacde15cce714"
SRCREV_FORMAT .= "_naga"
SRCREV_naga = "33b75a27d93c6574b11b4dd4492b85b5783d6c52"
SRCREV_FORMAT .= "_uniffi-rs"
SRCREV_uniffi-rs = "c0e64b839018728d8153ce1758d391b7782e2e21"
SRCREV_FORMAT .= "_metal"
SRCREV_metal = "d24f1a4ae92470bf87a0c65ecfe78c9299835505"

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
