# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox_crates_latest.inc
include firefox.inc

LIC_FILES_CHKSUM = "file://toolkit/content/license.html;md5=20d883514bd953070b1a93a475d0d0cb \
                    file://LICENSE;md5=dc9b6ecd19a14a54a628edaaf23733bf"

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-latest:"

SRC_URI += "git://github.com/mozilla/application-services.git;protocol=https;branch=main;name=application-services;destsuffix=application-services \
            git://github.com/mozilla/audioipc;protocol=https;branch=master;name=audioipc;destsuffix=audioipc \
            git://github.com/jamienicol/glutin;protocol=https;branch=wr;name=glutin;destsuffix=glutin \
            git://github.com/trifectatechfoundation/zlib-rs;protocol=https;branch=main;name=zlib;destsuffix=zlib \
            git://github.com/beurdouche/nss-gk-api;protocol=https;branch=main;name=nss-gk-api;destsuffix=nss-gk-api \
            git://github.com/beurdouche/mls-rs;protocol=https;branch=main;name=mls-rs;destsuffix=mls-rs \
            git://github.com/beurdouche/mls-platform-api;protocol=https;branch=main;name=mls-platform-api;destsuffix=mls-platform-api \
            git://github.com/glandium/rust-objc;protocol=https;branch=ATOMIC_USIZE_INIT;name=objc;destsuffix=objc"

SRC_URI[sha256sum] = "4f12bdcfaa7271f7f2b18ab8d377a7b22be95ea01f11e18e1ebf77953e3221b6"

SRCREV_FORMAT .= "_application-services"
SRCREV_application-services = "25934715ecc08fc922c80797c637dea64ee742d1"

SRCREV_FORMAT .= "_neqo"
SRCREV_neqo = "c6d5502fb5b827473e7c5d7c4c380275cdb3d931"
SRCREV_FORMAT .= "_wgpu"
SRCREV_wgpu = "aa7bec65b90028e4db6ec8def8589b52097d92f9"

SRCREV_FORMAT .= "_audioipc"
SRCREV_audioipc = "e6f44a2bd1e57d11dfc737632a9e849077632330"

SRCREV_FORMAT .= "_cubeb-coreaudio"
SRCREV_cubeb-coreaudio = "2407441a2f67341a0e13b4ba6547555e387c671c"

SRCREV_FORMAT .= "_mapped_hyph"
SRCREV_mapped_hyph = "eff105f6ad7ec9b79816cfc1985a28e5340ad14b"

SRCREV_FORMAT .= "_glutin"
SRCREV_glutin = "03285da9c14ec56296c2400c781d2c32b80d745a"

SRCREV_FORMAT .= "_aa-stroke"
SRCREV_aa-stroke = "a821fa621c2def48e90c82774b4c6563b5a8ea4a"

SRCREV_FORMAT .= "_zlib"
SRCREV_zlib = "4aa430ccb77537d0d60dab8db993ca51bb1194c5"

SRCREV_FORMAT .= "_nss-gk-api"
SRCREV_nss-gk-api = "e48a946811ffd64abc78de3ee284957d8d1c0d63"

SRCREV_FORMAT .= "_mls-rs"
SRCREV_mls-rs = "eedb37e50e3fca51863f460755afd632137da57c"

SRCREV_FORMAT .= "_mls-platform-api"
SRCREV_mls-platform-api = "19c3f18b747d13354370ba84440bb0b963932634"

SRCREV_FORMAT .= "_objc"
SRCREV_objc = "4de89f5aa9851ceca4d40e7ac1e2759410c04324"

PACKAGECONFIG[x11-only] = "--enable-default-toolkit=cairo-gtk3-x11-only,,,,,wayland-only"
PACKAGECONFIG[wayland-only] = "--enable-default-toolkit=cairo-gtk3-wayland-only,,virtual/egl,,,x11-only"
PACKAGECONFIG[disable-gecko-profiler] = "--disable-gecko-profiler,,,"
PACKAGECONFIG[system-av1] = "--with-system-av1,,dav1d"

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
