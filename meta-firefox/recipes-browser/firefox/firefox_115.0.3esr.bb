# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox_crates.inc
DESCRIPTION ?= "Browser made by mozilla"

DEPENDS += "pulseaudio cbindgen-native clang-native \
            nodejs-native python-zstandard gtk+3 \
            libevent dbus-glib"

DEPENDS += '${@bb.utils.contains("PACKAGECONFIG", "disable-sandboxed-libraries", "", "wasi-sdk-native", d)}'

RDEPENDS:${PN} = "libva"
RDEPENDS:${PN}-dev = "dbus"

# use system's nss in case the CPU has no native crypto support (e.g. armv7)
DEPENDS += "${@bb.utils.contains("TUNE_FEATURES", "crypto", "", "nss-3.89", d)}"
RDEPENDS:${PN} += "${@bb.utils.contains("TUNE_FEATURES", "crypto", "", "nss-3.89 nspr", d)}"

LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://toolkit/content/license.html;md5=1b074cb88f7e9794d795c1346bcc9c80"

CVE_PRODUCT = "mozilla:firefox"

SRC_URI += "https://ftp.mozilla.org/pub/firefox/releases/${PV}/source/firefox-${PV}.source.tar.xz \
           file://mozilla-firefox.png \
           file://mozilla-firefox.desktop \
           file://mozconfig \
           file://prefs/vendor.js \
           file://0001-trust-yocto-rust-binary.patch \
           file://0002-use-offline-crates.patch \
           file://0003-configure-use-rustflags-and-stay-offline.patch \
           file://0004-Fix-compiling-with-enable-openmax-config.patch \
           file://debian-hacks/Add-a-2-minutes-timeout-on-xpcshell-tests.patch \
           file://debian-hacks/Add-another-preferences-directory-for-applications-p.patch \
           file://debian-hacks/Avoid-using-vmrs-vmsr-on-armel.patch \
           file://debian-hacks/Avoid-wrong-sessionstore-data-to-keep-windows-out-of.patch \
           file://debian-hacks/Don-t-build-image-gtests.patch \
           file://debian-hacks/Fix-math_private.h-for-i386-FTBFS.patch \
           file://debian-hacks/Remove-workaround-for-old-libstdc-problem-which-now-.patch \
           file://debian-hacks/Use-build-id-as-langpack-version-for-reproducibility.patch \
           file://debian-hacks/Use-the-Mozilla-Location-Service-key-when-the-Google.patch \
           file://debian-hacks/Work-around-bz-1775202-to-fix-FTBFS-on-ppc64el.patch \
           file://debian-hacks/Work-around-https-sourceware.org-bugzilla-show_bug.c.patch \
           file://fixes/Allow-.js-preference-files-to-set-locked-prefs-with-.patch \
           file://fixes/Bug-1841197-Undefine-the-mips-builtin-macro-on-mips-.patch \
           file://porting/Add-xptcall-support-for-SH4-processors.patch \
           file://porting/NSS-Fix-FTBFS-on-Hurd-because-of-MAXPATHLEN.patch \
           file://porting/Use-NEON_FLAGS-instead-of-VPX_ASFLAGS-for-libaom-neo.patch \
           file://wayland/egl/bug1571603-Disable-eglQueryString-nullptr-EGL_EXTENSIONS.patch \
           file://wayland/egl/0001-Mark-GLFeature-framebuffer_multisample-as-unsupporte.patch \
           file://wayland/egl/0001-GLLibraryLoader-Use-given-symbol-lookup-function-fir.patch \
           file://prefs/Don-t-auto-disable-extensions-in-system-directories.patch \
           file://prefs/Set-DPI-to-system-settings.patch \
           git://github.com/hsivonen/chardetng.git;protocol=https;branch=tldquery;name=chardetng;destsuffix=chardetng \
           git://github.com/hsivonen/chardetng_c.git;protocol=https;branch=tldquery;name=chardetng_c;destsuffix=chardetng_c \
           git://github.com/chris-zen/coremidi.git;protocol=https;branch=master;name=coremidi;destsuffix=coremidi \
           git://github.com/mozilla/midir.git;protocol=https;branch=firefox-vendoring;name=midir;destsuffix=midir \
           git://github.com/glandium/warp.git;protocol=https;branch=pemfile;name=warp;destsuffix=warp \
           git://github.com/hsivonen/packed_simd.git;protocol=https;branch=0_3_8;name=packed-simd;destsuffix=packed_simd \
           git://github.com/mozilla-spidermonkey/jsparagus;protocol=https;branch=ci_generated;name=jsparagus;destsuffix=jsparagus \
           git://github.com/FirefoxGraphics/aa-stroke;protocol=https;branch=main;name=aa-stroke;destsuffix=aa-stroke \
           git://github.com/mozilla/cubeb-coreaudio-rs;protocol=https;branch=trailblazer;name=cubeb-coreaudio;destsuffix=cubeb-coreaudio \
           git://github.com/mozilla/cubeb-pulse-rs;protocol=https;branch=dev;name=cubeb-pulse;destsuffix=cubeb-pulse \
           git://github.com/jfkthame/mapped_hyph;protocol=https;branch=master;name=mapped_hyph;destsuffix=mapped_hyph \
           git://github.com/FirefoxGraphics/wpf-gpu-raster;protocol=https;branch=rust-only;name=wpf-gpu-raster;destsuffix=wpf-gpu-raster \
           git://github.com/mozilla/application-services.git;protocol=https;branch=main;name=application-services;destsuffix=application-services \
           git://github.com/mozilla/uniffi-rs.git;protocol=https;branch=main;name=uniffi-rs;destsuffix=uniffi-rs \
           git://github.com/mozilla/neqo;protocol=https;branch=main;name=neqo;destsuffix=neqo \
           git://github.com/gfx-rs/wgpu.git;protocol=https;branch=trunk;name=wgpu;destsuffix=wgpu \
           git://github.com/mozilla/audioipc;protocol=https;branch=master;name=audioipc;destsuffix=audioipc \
           git://github.com/servo/rust-cssparser;protocol=https;branch=master;name=cssparser;destsuffix=cssparser \
           "

SRC_URI[sha256sum] = "0bcc571c44f94ac6b8c26e896fd771eb7bd41b2a8ec35598bced0102c1b855fa"

SRCREV_chardetng = "3484d3e3ebdc8931493aa5df4d7ee9360a90e76b"
SRCREV_chardetng_c = "ed8a4c6f900a90d4dbc1d64b856e61490a1c3570"
SRCREV_coremidi = "fc68464b5445caf111e41f643a2e69ccce0b4f83"
SRCREV_packed-simd = "412f9a0aa556611de021bde89dee8fefe6e0fbbd"
SRCREV_midir = "519e651241e867af3391db08f9ae6400bc023e18"
SRCREV_warp = "4af45fae95bc98b0eba1ef0db17e1dac471bb23d"
SRCREV_application-services = "86c84c217036c12283d19368867323a66bf35883"
SRCREV_jsparagus = "64ba08e24749616de2344112f226d1ef4ba893ae"
SRCREV_neqo = "80db3a01f3273c7e742ba560fa99246fc8b30c4f"
SRCREV_aa-stroke = "07d3c25322518f294300e96246e09b95e118555d"
SRCREV_cubeb-coreaudio = "93b5c01a131f65c83c11aeb317f4583405c5eb79"
SRCREV_cubeb-pulse = "cf48897be5cbe147d051ebbbe1eaf5fd8fb6bbc9"
SRCREV_mapped_hyph = "c7651a0cffff41996ad13c44f689bd9cd2192c01"
SRCREV_wpf-gpu-raster = "a6514854d4518b02f2805719ff6cd74dae7dfde6"
SRCREV_uniffi-rs = "bc7ff8977bf38d0fdd1a458810b14f434d4dc4de"
SRCREV_wgpu = "73b4257b17cc62ecc8df6d6aa3730bd9c6cba4b9"
SRCREV_audioipc = "0b51291d2483a17dce3e300c7784b369e02bee73"
SRCREV_cssparser = "45bc47e2bcb846f1efb5aea156be5fe7d18624bf"


S = "${WORKDIR}/firefox-${MOZ_APP_BASE_VERSION}"

MOZ_APP_BASE_VERSION = "${@'${PV}'.replace('esr', '')}"

inherit mozilla

TOOLCHAIN:pn-firefox = "clang"
AS = "${CC}"

DISABLE_STATIC=""

ARM_INSTRUCTION_SET:armv5 = "arm"

PACKAGECONFIG ??= "${@bb.utils.contains("DISTRO_FEATURES", "alsa", "alsa", "", d)} \
                   ${@bb.utils.contains("DISTRO_FEATURES", "wayland", "wayland", "", d)} \
                   ${@bb.utils.contains_any("TARGET_ARCH", "x86_64 arm aarch64", "webrtc", "", d)} \
"
PACKAGECONFIG[alsa] = "--enable-alsa,--disable-alsa,alsa-lib"
PACKAGECONFIG[wayland] = "--enable-default-toolkit=cairo-gtk3-wayland,--enable-default-toolkit=cairo-gtk3,virtual/egl,"
PACKAGECONFIG[gpu] = ",,,"
PACKAGECONFIG[openmax] = "--enable-openmax,,,"
PACKAGECONFIG[webgl] = ",,,"
PACKAGECONFIG[webrtc] = "--enable-webrtc,--disable-webrtc,,"
PACKAGECONFIG[forbid-multiple-compositors] = ",,,"
PACKAGECONFIG[disable-sandboxed-libraries] = ",,,"

# Add a config file to enable GPU acceleration by default.
SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'gpu', \
           'file://prefs/gpu.js', '', d)}"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'openmax', \
           'file://fixes/Bug-1590977-openmax-Import-latest-OpenMAX-IL-1.1.2-headers.patch \
            file://prefs/openmax.js \
           ', '', d)}"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'webgl', \
           'file://prefs/webgl.js', '', d)}"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'forbid-multiple-compositors', \
           'file://prefs/single-compositor.js \
            file://fixes/0001-Enable-to-suppress-multiple-compositors.patch \
	   ', '', d)}"

EXTRA_OECONF += '${@bb.utils.contains("TUNE_FEATURES", "crypto", "", "--with-system-nss", d)}'
EXTRA_OECONF += '${@bb.utils.contains("PACKAGECONFIG", "disable-sandboxed-libraries", "--without-wasm-sandboxed-libraries", "", d)}'

do_compile:prepend(){
    head -n 38 "${WORKDIR}/cargo_home/config" > "${WORKDIR}/cargo_home/config_tmp"
    mv "${WORKDIR}/cargo_home/config_tmp" "${WORKDIR}/cargo_home/config"
}

do_install:append() {
    install -d ${D}${datadir}/applications
    install -d ${D}${datadir}/pixmaps

    install -m 0644 ${WORKDIR}/mozilla-firefox.desktop ${D}${datadir}/applications/
    install -m 0644 ${WORKDIR}/mozilla-firefox.png ${D}${datadir}/pixmaps/
    install -m 0644 ${WORKDIR}/prefs/vendor.js ${D}${libdir}/${PN}/defaults/pref/
    if [ -n "${@bb.utils.contains_any('PACKAGECONFIG', 'gpu', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/gpu.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'openmax', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/openmax.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'webgl', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/webgl.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'forbit-multiple-compositors', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/single-compositor.js ${D}${libdir}/${PN}/defaults/pref/
    fi

    # Fix ownership of files
    chown root:root -R ${D}${datadir}
    chown root:root -R ${D}${libdir}
}

FILES:${PN} = "${bindir}/${PN} \
               ${datadir}/applications/ \
               ${datadir}/pixmaps/ \
               ${libdir}/${PN}/* \
               ${libdir}/${PN}/.autoreg \
               ${bindir}/defaults \
               ${bindir} \
               ${libdir}/firefox/*"

FILES:${PN}-dev += "${datadir}/idl ${bindir}/${PN}-config ${libdir}/${PN}-devel-*"
FILES:${PN}-staticdev += "${libdir}/${PN}-devel-*/sdk/lib/*.a"
FILES:${PN}-dbg += "${libdir}/${PN}/.debug \
                    ${libdir}/${PN}/*/.debug \
                    ${libdir}/${PN}/*/*/.debug \
                    ${libdir}/${PN}/*/*/*/.debug \
                    ${libdir}/${PN}-devel-*/*/.debug \
                    ${libdir}/${PN}-devel-*/*/*/.debug \
                    ${libdir}/${PN}-devel-*/*/*/*/.debug \
                    ${libdir}/${PN}/fix_linux_stack.py \
                    ${bindir}/.debug"

# We don't build XUL as system shared lib, so we can mark all libs as private
PRIVATE_LIBS = " \
    libmozjs.so \
    libxpcom.so \
    libnspr4.so \
    libxul.so \
    libmozalloc.so \
    libplc4.so \
    libplds4.so \
    liblgpllibs.so \
    libmozgtk.so \
    libmozwayland.so \
    libmozsqlite3.so \
    libclearkey.so \
    libmozavcodec.so \
    libmozavutil.so \
"

# mark libraries also provided by nss as private too
PRIVATE_LIBS += " \
    libfreebl3.so \
    libfreeblpriv3.so \
    libnss3.so \
    libnssckbi.so \
    libsmime3.so \
    libnssutil3.so \
    libnssdbm3.so \
    libssl3.so \
    libsoftokn3.so \
"

CLEANBROKEN = "1"

