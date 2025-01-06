# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox_crates_latest.inc
include firefox.inc

LIC_FILES_CHKSUM = "file://toolkit/content/license.html;md5=da3b3e9e9800c08468071c9e19190dde \
                    file://LICENSE;md5=dc9b6ecd19a14a54a628edaaf23733bf"

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-latest:"

SRC_URI += "git://github.com/mozilla/application-services.git;protocol=https;branch=main;name=application-services;destsuffix=application-services \
            git://github.com/mozilla/audioipc;protocol=https;branch=master;name=audioipc;destsuffix=audioipc \
            git://github.com/jamienicol/glutin;protocol=https;branch=wr;name=glutin;destsuffix=glutin \
            git://github.com/trifectatechfoundation/zlib-rs;protocol=https;branch=main;name=zlib;destsuffix=zlib"

SRC_URI[sha256sum] = "6c6eb7ff13fa689c5cace23a28533361d1ca29158329b6f1c2f2d1c91c53dd27"

SRCREV_FORMAT .= "_application-services"
SRCREV_application-services = "3303de12a04710164f7c80fb5e466fbf2560a20f"

SRCREV_FORMAT .= "_neqo"
SRCREV_neqo = "214ad48e0dbb8cc35c7148b225fbc6f58eb85f36"
SRCREV_FORMAT .= "_wgpu"
SRCREV_wgpu = "b89a57f8ca9d8f93ef7995c8003152240fdbbe12"

SRCREV_FORMAT .= "_audioipc"
SRCREV_audioipc = "e6f44a2bd1e57d11dfc737632a9e849077632330"

SRCREV_FORMAT .= "_cubeb-coreaudio"
SRCREV_cubeb-coreaudio = "2407441a2f67341a0e13b4ba6547555e387c671c"

SRCREV_FORMAT .= "_mapped_hyph"
SRCREV_mapped_hyph = "eff105f6ad7ec9b79816cfc1985a28e5340ad14b"

SRCREV_FORMAT .= "_glutin"
SRCREV_glutin = "03285da9c14ec56296c2400c781d2c32b80d745a"

SRCREV_FORMAT .= "_aa-stroke"
SRCREV_aa-stroke = "35a650261605662795a04cc249c465436cbfab45"

SRCREV_FORMAT .= "_zlib"
SRCREV_zlib = "4aa430ccb77537d0d60dab8db993ca51bb1194c5"

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
