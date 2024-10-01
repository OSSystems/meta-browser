# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox_crates_stable.inc
include firefox.inc

LIC_FILES_CHKSUM = "file://toolkit/content/license.html;md5=da3b3e9e9800c08468071c9e19190dde \
                    file://LICENSE;md5=dc9b6ecd19a14a54a628edaaf23733bf"

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-stable:"

SRC_URI += "git://github.com/mozilla/application-services.git;protocol=https;branch=main;name=application-services;destsuffix=application-services \
            git://github.com/mozilla/audioipc;protocol=https;branch=master;name=audioipc;destsuffix=audioipc \
            git://github.com/jamienicol/glutin;protocol=https;branch=wr;name=glutin;destsuffix=glutin "

SRC_URI[sha256sum] = "e4066ef47c65aac570aa16deefb8c3937c1c9b0755dbf2f760937c451174fb1d"

SRCREV_FORMAT .= "_application-services"
SRCREV_application-services = "c3774b262f27fabdd8ae7d064db5745029b347b9"

SRCREV_FORMAT .= "_neqo"
SRCREV_neqo = "b7e17668eb8f2fb13c1d945a9a7f79bd31257eb8"
SRCREV_FORMAT .= "_wgpu"
SRCREV_wgpu = "bbdbafdf8a947b563b46f632a778632b906d9eb4"

SRCREV_FORMAT .= "_audioipc"
SRCREV_audioipc = "e6f44a2bd1e57d11dfc737632a9e849077632330"

SRCREV_FORMAT .= "_cubeb-coreaudio"
SRCREV_cubeb-coreaudio = "2407441a2f67341a0e13b4ba6547555e387c671c"

SRCREV_FORMAT .= "_mapped_hyph"
SRCREV_mapped_hyph = "eff105f6ad7ec9b79816cfc1985a28e5340ad14b"

SRCREV_FORMAT .= "_glutin"
SRCREV_glutin = "03285da9c14ec56296c2400c781d2c32b80d745a"

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
