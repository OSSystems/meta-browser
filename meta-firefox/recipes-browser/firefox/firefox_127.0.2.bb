# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox_crates_stable.inc
include firefox.inc

LIC_FILES_CHKSUM = "file://toolkit/content/license.html;md5=45d340824df93b4c76d133d9b2ab8ff7 \
                    file://LICENSE;md5=dc9b6ecd19a14a54a628edaaf23733bf"

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-stable:"

SRC_URI += "git://github.com/franziskuskiefer/cose-rust;protocol=https;branch=master;name=cose-rust;destsuffix=cose-rust \
            git://github.com/mozilla/mp4parse-rust;protocol=https;branch=master;name=mp4parse;destsuffix=mp4parse \
            git://github.com/seanmonstar/warp;protocol=https;branch=master;name=warp;destsuffix=warp \
            git://github.com/glandium/mio;protocol=https;branch=windows-sys;name=mio;destsuffix=mio \
            git://github.com/servo/unicode-bidi;protocol=https;branch=main;name=unicode-bidi;destsuffix=unicode-bidi \
            git://github.com/zakarumych/gpu-descriptor;protocol=https;branch=master;name=gpu-descriptor;destsuffix=gpu-descriptor \
            git://github.com/hsivonen/any_all_workaround;protocol=https;branch=simdsplit;name=any-all-workaround;destsuffix=any-all-workaround"

SRC_URI[sha256sum] = "0b7b2a8c3def52fcab13c48769645d0e963902ee916e4f3d2a0098796e73e3e8"

SRCREV_FORMAT .= "_application-services"
SRCREV_application-services = "e0563d725f852f617878ecc13a03cdf50c85cd5a"
SRCREV_FORMAT .= "_cose-rust"
SRCREV_cose-rust = "43c22248d136c8b38fe42ea709d08da6355cf04b"
SRCREV_FORMAT .= "_mio"
SRCREV_mio = "9a2ef335c366044ffe73b1c4acabe50a1daefe05"

SRCREV_FORMAT .= "_mp4parse"
SRCREV_mp4parse = "a138e40ec1c603615873e524b5b22e11c0ec4820"
SRCREV_FORMAT .= "_neqo"
SRCREV_neqo = "343df5cc0d02e0b0953de4a0a390ae8980d89081"
SRCREV_FORMAT .= "_wgpu"
SRCREV_wgpu = "d5d683d3c491ec8cd2f5cdb43ac61e526cb7c231"

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
SRCREV_cubeb-coreaudio = "4ca174cf83ebe32b3198478c2211d69678845bc7"
SRCREV_FORMAT .= "_aa-stroke"
SRCREV_aa-stroke = "d94278ed9c7020f50232689a26d1277eb0eb74d2"
SRCREV_FORMAT .= "_jsparagus"
SRCREV_jsparagus = "61f399c53a641ebd3077c1f39f054f6d396a633c"

SRCREV_FORMAT .= "_unicode-bidi"
SRCREV_unicode-bidi = "ca612daf1c08c53abe07327cb3e6ef6e0a760f0c"

SRCREV_FORMAT .= "_gpu-descriptor"
SRCREV_gpu-descriptor = "7b71a4e47c81903ad75e2c53deb5ab1310f6ff4d"
SRCREV_FORMAT .= "_any-all-woraround"
SRCREV_any-all-workaround = "7fb1b7034c9f172aade21ee1c8554e8d8a48af80"

PACKAGECONFIG[x11-only] = "--enable-default-toolkit=cairo-gtk3-x11-only,,,,,wayland-only"
PACKAGECONFIG[wayland-only] = "--enable-default-toolkit=cairo-gtk3-wayland-only,,virtual/egl,,,x11-only"
PACKAGECONFIG[disable-gecko-profiler] = "--disable-gecko-profiler,,,"

PACKAGECONFIG:append = " disable-gecko-profiler "

do_configure:append(){
    wayland_only='${@bb.utils.contains("PACKAGECONFIG", "wayland-only", "true", "false", d)}'
    x11_only='${@bb.utils.contains("PACKAGECONFIG", "x11-only", "true", "false", d)}'

    if $x11_only; then
        sed -i '/ac_add_options --enable-default-toolkit=cairo-gtk3$/d' ${MOZCONFIG}
    fi

    if $wayland_only; then
        sed -i '/ac_add_options --enable-default-toolkit=cairo-gtk3-wayland$/d' ${MOZCONFIG}
    fi
}
