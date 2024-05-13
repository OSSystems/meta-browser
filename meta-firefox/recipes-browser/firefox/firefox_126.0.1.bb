# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox_crates_stable.inc
include firefox.inc

LIC_FILES_CHKSUM = "file://toolkit/content/license.html;md5=e7ba18eda62a2c7008019264d83e0c3a \
                    file://LICENSE;md5=dc9b6ecd19a14a54a628edaaf23733bf"

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-stable:"

SRC_URI += "git://github.com/franziskuskiefer/cose-rust;protocol=https;branch=master;name=cose-rust;destsuffix=cose-rust \
            git://github.com/rust-minidump/minidump-writer.git;protocol=https;branch=main;name=minidump-writer;destsuffix=minidump-writer \
            git://github.com/rust-minidump/rust-minidump;protocol=https;branch=main;name=minidump-common;destsuffix=minidump-common \
            git://github.com/hsivonen/packed_simd.git;protocol=https;branch=master;name=packed-simd;destsuffix=packed_simd \
            git://github.com/mozilla/mp4parse-rust;protocol=https;branch=master;name=mp4parse;destsuffix=mp4parse \
            git://github.com/gfx-rs/metal-rs;protocol=https;branch=master;name=metal;destsuffix=metal \
            git://github.com/seanmonstar/warp;protocol=https;branch=master;name=warp;destsuffix=warp \
            git://github.com/servo/rust-cssparser;protocol=https;branch=main;name=cssparser;destsuffix=cssparser \
            git://github.com/glandium/mio;protocol=https;branch=windows-sys;name=mio;destsuffix=mio \
            git://github.com/servo/unicode-bidi;protocol=https;branch=main;name=unicode-bidi;destsuffix=unicode-bidi"

SRC_URI[sha256sum] = "f63026359f678a5d45cea4c7744fcef512abbb58a5b016bbbb1c6ace723a263b"

SRCREV_FORMAT .= "_application-services"
SRCREV_application-services = "e6ccfed09ebe663f464a33968f42e656c152e584"
SRCREV_FORMAT .= "_cose-rust"
SRCREV_cose-rust = "43c22248d136c8b38fe42ea709d08da6355cf04b"
SRCREV_FORMAT .= "_minidump-writer"
SRCREV_minidump-writer = "99c561931fe8cf1fa2135b3f23ff6588bef8fd1e"
SRCREV_FORMAT .= "_minidump-common"
SRCREV_minidump-common = "c3de84b061339c686a572fb9f059e7ba3fad38d6"
SRCREV_FORMAT .= "_packed-simd"
SRCREV_packed-simd = "d938e39bee9bc5c222f5f2f2a0df9e53b5ce36ae"
SRCREV_FORMAT .= "_mio"
SRCREV_mio = "9a2ef335c366044ffe73b1c4acabe50a1daefe05"

SRCREV_FORMAT .= "_mp4parse"
SRCREV_mp4parse = "a138e40ec1c603615873e524b5b22e11c0ec4820"
SRCREV_FORMAT .= "_neqo"
SRCREV_neqo = "c004359a817ffdea33394e94944d2f882e7e78af"
SRCREV_FORMAT .= "_wgpu"
SRCREV_wgpu = "0c5bebca514eb06d9387f87666c1c658f3f673b4"
SRCREV_FORMAT .= "_uniffi-rs"
SRCREV_uniffi-rs = "0ecafdc06799205caf1432b93787a9c1f810a168"
SRCREV_FORMAT .= "_metal"
SRCREV_metal = "f507da4686234e658f31de54d2aa0dfa8abd236b"

SRCREV_FORMAT .= "_cssparser"
SRCREV_cssparser = "aaa966d9d6ae70c4b8a62bb5e3a14c068bb7dff0"
SRCREV_FORMAT .= "_audioipc"
SRCREV_audioipc = "409e11f8de6288e9ddfe269654523735302e59e6"
SRCREV_FORMAT .= "_wpf-gpu-raster"
SRCREV_wpf-gpu-raster = "99979da091fd58fba8477e7fcdf5ec0727102916"

SRCREV_FORMAT .= "_warp"
SRCREV_warp = "9d081461ae1167eb321585ce424f4fef6cf0092b"
SRCREV_FORMAT .= "_cubeb-pulse"
SRCREV_cubeb-pulse = "8ff972c8e2ec1782ff262ac4071c0415e69b1367"

SRCREV_FORMAT .="_midir"
SRCREV_midir = "85156e360a37d851734118104619f86bd18e94c6"

SRCREV_FORMAT .= "_cubeb-coreaudio"
SRCREV_cubeb-coreaudio = "cc58f92f28015e4e25eba9e482007cf464c10474"
SRCREV_FORMAT .= "_aa-stroke"
SRCREV_aa-stroke = "d94278ed9c7020f50232689a26d1277eb0eb74d2"
SRCREV_FORMAT .= "_jsparagus"
SRCREV_jsparagus = "61f399c53a641ebd3077c1f39f054f6d396a633c"

SRCREV_FORMAT .= "_unicode-bidi"
SRCREV_unicode-bidi = "ca612daf1c08c53abe07327cb3e6ef6e0a760f0c"

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
