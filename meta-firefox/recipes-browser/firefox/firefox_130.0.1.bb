# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox_crates_stable.inc
include firefox.inc

LIC_FILES_CHKSUM = "file://toolkit/content/license.html;md5=d0e5b4ce18f96c425788b6ff00f64563 \
                    file://LICENSE;md5=dc9b6ecd19a14a54a628edaaf23733bf"

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-stable:"

SRC_URI += "git://github.com/franziskuskiefer/cose-rust;protocol=https;branch=master;name=cose-rust;destsuffix=cose-rust \
            git://github.com/mozilla/mp4parse-rust;protocol=https;branch=master;name=mp4parse;destsuffix=mp4parse \
            git://github.com/servo/unicode-bidi;protocol=https;branch=main;name=unicode-bidi;destsuffix=unicode-bidi \
            git://github.com/zakarumych/gpu-descriptor;protocol=https;branch=master;name=gpu-descriptor;destsuffix=gpu-descriptor \
            git://github.com/hsivonen/any_all_workaround;protocol=https;branch=simdsplit;name=any-all-workaround;destsuffix=any-all-workaround \
            git://github.com/mozilla/application-services.git;protocol=https;branch=release-v130-desktop;name=application-services;destsuffix=application-services"

SRC_URI[sha256sum] = "027225a1e9b074f0072e22c7264cf27b0d2364c675c3ca811aa6c25fb01b9f70"

SRCREV_FORMAT .= "_application-services"
SRCREV_application-services = "9fc8d02e6cc4751c08c75a7b3c5aee432b66116f"
SRCREV_FORMAT .= "_cose-rust"
SRCREV_cose-rust = "43c22248d136c8b38fe42ea709d08da6355cf04b"

SRCREV_FORMAT .= "_mp4parse"
SRCREV_mp4parse = "a138e40ec1c603615873e524b5b22e11c0ec4820"
SRCREV_FORMAT .= "_neqo"
SRCREV_neqo = "f0bffce01be53309a9cc93dc344141f57918cb3b"
SRCREV_FORMAT .= "_wgpu"
SRCREV_wgpu = "aeb2067e8120c1ff480625c00b9571db8d01d5a4"

SRCREV_FORMAT .= "_audioipc"
SRCREV_audioipc = "e6f44a2bd1e57d11dfc737632a9e849077632330"
SRCREV_FORMAT .= "_wpf-gpu-raster"
SRCREV_wpf-gpu-raster = "99979da091fd58fba8477e7fcdf5ec0727102916"

SRCREV_FORMAT .= "_cubeb-pulse"
SRCREV_cubeb-pulse = "8678dcab1c287de79c4c184ccc2e065bc62b70e2"

SRCREV_FORMAT .="_midir"
SRCREV_midir = "85156e360a37d851734118104619f86bd18e94c6"

SRCREV_FORMAT .= "_cubeb-coreaudio"
SRCREV_cubeb-coreaudio = "8bce3b333a920999055397a397e59c2b81a93b9a"
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

SRCREV_FORMAT .= "_mapped_hyph"
SRCREV_mapped_hyph = "eff105f6ad7ec9b79816cfc1985a28e5340ad14b"

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
