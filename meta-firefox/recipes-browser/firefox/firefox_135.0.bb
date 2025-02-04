# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox_crates_latest.inc
include firefox.inc

LIC_FILES_CHKSUM = "file://toolkit/content/license.html;md5=4a01ba992cfd635409980e79333d43c1 \
                    file://LICENSE;md5=dc9b6ecd19a14a54a628edaaf23733bf"

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-latest:"

SRC_URI += "git://github.com/mozilla/application-services.git;protocol=https;branch=main;name=application-services;destsuffix=application-services \
            git://github.com/mozilla/audioipc;protocol=https;branch=master;name=audioipc;destsuffix=audioipc \
            git://github.com/jamienicol/glutin;protocol=https;branch=wr;name=glutin;destsuffix=glutin \
            git://github.com/trifectatechfoundation/zlib-rs;protocol=https;branch=main;name=zlib;destsuffix=zlib \
            git://github.com/beurdouche/nss-gk-api;protocol=https;branch=main;name=nss-gk-api;destsuffix=nss-gk-api \
            git://github.com/beurdouche/mls-rs;protocol=https;branch=main;name=mls-rs;destsuffix=mls-rs \
            git://github.com/beurdouche/mls-platform-api;protocol=https;branch=main;name=mls-platform-api;destsuffix=mls-platform-api"

SRC_URI[sha256sum] = "827e12a962ef47511089af4498f65ebf42fa57ca31db790bfd7e9a820d16b960"

SRCREV_FORMAT .= "_application-services"
SRCREV_application-services = "03cf4a362408b9caffa6848aae2fcf472a789460"

SRCREV_FORMAT .= "_neqo"
SRCREV_neqo = "c6d5502fb5b827473e7c5d7c4c380275cdb3d931"
SRCREV_FORMAT .= "_wgpu"
SRCREV_wgpu = "5543961a71cc8e5399b696fae3f6aae82c019717"

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
