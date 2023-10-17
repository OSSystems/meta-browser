# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox_crates_stable.inc
include firefox.inc

LIC_FILES_CHKSUM = "file://toolkit/content/license.html;md5=44856d1ce1c8730735d156dd3119633f"

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-stable:"

SRC_URI += "git://github.com/franziskuskiefer/cose-rust;protocol=https;branch=master;name=cose-rust;destsuffix=cose-rust \
            git://github.com/rust-minidump/minidump-writer.git;protocol=https;branch=main;name=minidump-writer;destsuffix=minidump-writer \
            git://github.com/rust-minidump/rust-minidump;protocol=https;branch=main;name=minidump-common;destsuffix=minidump-common \
            git://github.com/glandium/prost;protocol=https;branch=syn2-0.11.9;name=prost;destsuffix=prost \
            git://github.com/rust-diplomat/diplomat;protocol=https;branch=main;name=diplomat;destsuffix=diplomat \
            git://github.com/unicode-org/icu4x;protocol=https;branch=main;name=icu4x;destsuffix=icu4x \
            git://github.com/hsivonen/packed_simd.git;protocol=https;branch=0_3_8;name=packed-simd;destsuffix=packed_simd \
            git://github.com/mozilla/mp4parse-rust;protocol=https;branch=master;name=mp4parse;destsuffix=mp4parse"

SRC_URI[sha256sum] = "89626520f2f0f782f37c074b94690e0f08dcf416be2b992f4aad68df5d727b21"

SRCREV_FORMAT .= "_application-services"
SRCREV_application-services = "25972c388a4cf3a6d8256504f3a09b711db2fc6a"
SRCREV_FORMAT .= "_cose-rust"
SRCREV_cose-rust = "43c22248d136c8b38fe42ea709d08da6355cf04b"
SRCREV_FORMAT .= "_minidump-writer"
SRCREV_minidump-writer = "a15bd5cab6a3de251c0c23264be14b977c0af09c"
SRCREV_FORMAT .= "_minidump-common"
SRCREV_minidump-common = "87a29fba5e19cfae5ebf73a57ba31504a3872545"
SRCREV_FORMAT .= "_prost"
SRCREV_prost = "95964e9d33df3c2a9c3f14285e262867cab6f96b"
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
SRCREV_wgpu = "7c6b85756237f77bfe8d6231dfc7a1412ff662b6"
SRCREV_FORMAT .= "_naga"
SRCREV_naga = "bac2d82a430fbfcf100ee22b7c3bc12f3d593079"
SRCREV_FORMAT .= "_uniffi-rs"
SRCREV_uniffi-rs = "c0e64b839018728d8153ce1758d391b7782e2e21"

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
