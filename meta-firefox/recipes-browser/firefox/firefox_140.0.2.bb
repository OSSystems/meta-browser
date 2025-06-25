# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox_crates_latest.inc
include firefox.inc

DEPENDS:append:libc-musl = " libexecinfo "

LIC_FILES_CHKSUM = "file://toolkit/content/license.html;md5=cd15aee00c3f630670c3eabf7455643b \
                    file://LICENSE;md5=dc9b6ecd19a14a54a628edaaf23733bf"

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-latest:"

SRC_URI += "git://github.com/mozilla/application-services.git;protocol=https;branch=main;name=application-services;destsuffix=application-services \
            git://github.com/mozilla/audioipc;protocol=https;branch=master;name=audioipc;destsuffix=audioipc \
            git://github.com/jamienicol/glutin;protocol=https;branch=wr;name=glutin;destsuffix=glutin \
            git://github.com/beurdouche/nss-gk-api;protocol=https;branch=main;name=nss-gk-api;destsuffix=nss-gk-api \
            git://github.com/beurdouche/mls-rs;protocol=https;branch=main;name=mls-rs;destsuffix=mls-rs \
            git://github.com/beurdouche/mls-platform-api;protocol=https;branch=main;name=mls-platform-api;destsuffix=mls-platform-api \
            git://github.com/glandium/rust-objc;protocol=https;branch=ATOMIC_USIZE_INIT;name=objc;destsuffix=objc \
            git://github.com/servo/osmesa-src;protocol=https;branch=main;name=osmesa-src;destsuffix=osmesa-src \
            git://github.com/servo/rust-cssparser;protocol=https;branch=main;name=cssparser;destsuffix=cssparser"

SRC_URI[sha256sum] = "70ae55a840f5d5656a74e224607af3748d2187f880d129e28afe64433c8a5c03"

SRCREV_FORMAT .= "_application-services"
SRCREV_application-services = "9b46be5beedb6a1d859014a71bac58e2d722f954"

SRCREV_FORMAT .= "_neqo"
SRCREV_neqo = "c3179d9f363bf92a908d90e4240b315b9ff72516"

SRCREV_FORMAT .= "_wgpu"
SRCREV_wgpu = "85001b2436f686451d6d783225311c06b60e6680"

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

SRCREV_FORMAT .= "_nss-gk-api"
SRCREV_nss-gk-api = "e48a946811ffd64abc78de3ee284957d8d1c0d63"

SRCREV_FORMAT .= "_mls-rs"
SRCREV_mls-rs = "b747d7efb85a776b97ad8afa8d1b32893fa5efa3"

SRCREV_FORMAT .= "_mls-platform-api"
SRCREV_mls-platform-api = "5d88241b9765cae3669aba21f0946bd3700f7db1"

SRCREV_FORMAT .= "_objc"
SRCREV_objc = "4de89f5aa9851ceca4d40e7ac1e2759410c04324"

SRCREV_FORMAT .= "_mp4parse"
SRCREV_mp4parse = "e64650a686e5c5732395cd059e17cfd3b1e5b63b"

SRCREV_FORMAT .= "_osmesa-src"
SRCREV_osmesa-src = "a9c57bae855c8a16c8397c04127f75ab14171c81"

SRCREV_FORMAT .= "_cssparser"
SRCREV_cssparser = "958a3f098acb92ddacdce18a7ef2c4a87ac3326f"

# system-pixman PACKAGECONFIG is also present in the firefox.inc file
# it was changed in v138 - once there is a major ESR version update,
# this can be reunified
PACKAGECONFIG[system-pixman] = "--with-system-pixman,,pixman,pixman"
PACKAGECONFIG[x11-only] = "--enable-default-toolkit=cairo-gtk3-x11-only,,,,,wayland-only"
PACKAGECONFIG[wayland-only] = "--enable-default-toolkit=cairo-gtk3-wayland-only,,virtual/egl,,,x11-only"
PACKAGECONFIG[disable-gecko-profiler] = "--disable-gecko-profiler,,,"
PACKAGECONFIG[system-av1] = "--with-system-av1,,aom dav1d"
PACKAGECONFIG[system-pipewire] = "--with-system-pipewire,,pipewire,pipewire"
PACKAGECONFIG[system-gbm] = "--with-system-gbm,,virtual/libgbm,libgbm"
PACKAGECONFIG[system-libdrm] = "--with-system-libdrm,,libdrm,libdrm"


PACKAGECONFIG:append = " disable-gecko-profiler "

LDFLAGS:append = "${@bb.utils.contains('PACKAGECONFIG', 'system-av1', ' -Wl,--undefined-version ', '', d)}"

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
