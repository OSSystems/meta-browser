DESCRIPTION ?= "Browser made by mozilla"

DEPENDS:append = "pulseaudio cbindgen-native clang-native \
            nodejs-native python-zstandard gtk+3 \
            libevent dbus-glib"

DEPENDS:x86-64:append = " nasm-native "
DEPENDS:x86:append = " nasm-native "

RDEPENDS:${PN} = "libva libpci"
RDEPENDS:${PN}-dev = "dbus"

# Use system's nss in case the CPU has no native crypto support (e.g. armv7)
# On dunfell and kirkstone use the vendored version, as they contain too old version.
# This is only for ARM arch: Firefox's nss explicitly includes code using hw-crypto support for this arch.
ARM_AND_NO_HW_CRYPTO = "${@ True if 'crypto' not in d.getVar('TUNE_FEATURES') and \
                        ( 'arm' in d.getVar('TARGET_ARCH') or \
                          'aarch64' in d.getVar('TARGET_ARCH') \
                        ) else False }"

LICENSE = "MPL-2.0"

CVE_PRODUCT = "mozilla:firefox"


FF_BASEDIR = '${@ "${WORKDIR}" if d.getVar("UNPACKDIR") == None \
                 else d.getVar("UNPACKDIR")}'

S = "${FF_BASEDIR}/firefox-${MOZ_APP_BASE_VERSION}"

MOZ_APP_BASE_VERSION = "${@'${PV}'.replace('esr', '')}"

inherit mozilla

# target-c-int-width size for rust target
# in poky it is defined as 64, however it fails to compile
# a small handful of crates.
TARGET_C_INT_WIDTH[riscv64gc] = "32"

SRC_URI += "https://ftp.mozilla.org/pub/firefox/releases/${PV}/source/firefox-${PV}.source.tar.xz \
           file://mozilla-firefox.png \
           file://mozilla-firefox.desktop \
           file://mozconfig \
           file://prefs/vendor.js \
           file://bug1545437-enable-to-specify-rust-target.patch \
           file://0001-trust-yocto-rust-binary.patch \
           file://0002-use-offline-crates.patch \
           file://0003-configure-use-rustflags-and-stay-offline.patch \
           file://0004-Fix-compiling-with-enable-openmax-config.patch \
           file://debian-hacks/Add-another-preferences-directory-for-applications-p.patch \
           file://debian-hacks/Avoid-using-vmrs-vmsr-on-armel.patch \
           file://debian-hacks/Avoid-wrong-sessionstore-data-to-keep-windows-out-of.patch \
           file://debian-hacks/Don-t-build-image-gtests.patch \
           file://debian-hacks/Fix-math_private.h-for-i386-FTBFS.patch \
           file://debian-hacks/Remove-workaround-for-old-libstdc-problem-which-now-.patch \
           file://debian-hacks/Use-build-id-as-langpack-version-for-reproducibility.patch \
           file://debian-hacks/Use-the-Mozilla-Location-Service-key-when-the-Google.patch \
           file://debian-hacks/Work-around-https-sourceware.org-bugzilla-show_bug.c.patch \
           file://fixes/Allow-.js-preference-files-to-set-locked-prefs-with-.patch \
           file://porting/Add-xptcall-support-for-SH4-processors.patch \
           file://porting/NSS-Fix-FTBFS-on-Hurd-because-of-MAXPATHLEN.patch \
           file://wayland/egl/bug1571603-Disable-eglQueryString-nullptr-EGL_EXTENSIONS.patch \
           file://wayland/egl/0001-Mark-GLFeature-framebuffer_multisample-as-unsupporte.patch \
           file://wayland/egl/0001-GLLibraryLoader-Use-given-symbol-lookup-function-fir.patch \
           file://prefs/Don-t-auto-disable-extensions-in-system-directories.patch \
           file://prefs/Set-DPI-to-system-settings.patch \
           git://github.com/hsivonen/chardetng.git;protocol=https;branch=tldquery;name=chardetng;destsuffix=chardetng \
           git://github.com/hsivonen/chardetng_c.git;protocol=https;branch=tldquery;name=chardetng_c;destsuffix=chardetng_c \
           git://github.com/chris-zen/coremidi.git;protocol=https;branch=master;name=coremidi;destsuffix=coremidi \
           git://github.com/mozilla/midir.git;protocol=https;branch=firefox-vendoring;name=midir;destsuffix=midir \
           git://github.com/mozilla-spidermonkey/jsparagus;protocol=https;branch=ci_generated;name=jsparagus;destsuffix=jsparagus \
           git://github.com/FirefoxGraphics/aa-stroke;protocol=https;branch=main;name=aa-stroke;destsuffix=aa-stroke \
           git://github.com/mozilla/cubeb-coreaudio-rs;protocol=https;branch=trailblazer;name=cubeb-coreaudio;destsuffix=cubeb-coreaudio \
           git://github.com/mozilla/cubeb-pulse-rs;protocol=https;branch=dev;name=cubeb-pulse;destsuffix=cubeb-pulse \
           git://github.com/jfkthame/mapped_hyph;protocol=https;branch=master;name=mapped_hyph;destsuffix=mapped_hyph \
           git://github.com/FirefoxGraphics/wpf-gpu-raster;protocol=https;branch=rust-only;name=wpf-gpu-raster;destsuffix=wpf-gpu-raster \
           git://github.com/mozilla/application-services.git;protocol=https;branch=main;name=application-services;destsuffix=application-services \
           git://github.com/mozilla/neqo;protocol=https;branch=main;name=neqo;destsuffix=neqo \
           git://github.com/gfx-rs/wgpu.git;protocol=https;branch=trunk;name=wgpu;destsuffix=wgpu \
           git://github.com/mozilla/audioipc;protocol=https;branch=master;name=audioipc;destsuffix=audioipc \
           git://github.com/mozilla/mp4parse-rust;protocol=https;branch=master;name=mp4parse;destsuffix=mp4parse \
           "

SRCREV_FORMAT .= "_chardetng"
SRCREV_chardetng = "3484d3e3ebdc8931493aa5df4d7ee9360a90e76b"
SRCREV_FORMAT .= "_chardetng_c"
SRCREV_chardetng_c = "ed8a4c6f900a90d4dbc1d64b856e61490a1c3570"
SRCREV_FORMAT .= "_coremidi"
SRCREV_coremidi = "fc68464b5445caf111e41f643a2e69ccce0b4f83"
SRCREV_FORMAT .= "_mapped_hyph"
SRCREV_mapped_hyph = "c7651a0cffff41996ad13c44f689bd9cd2192c01"

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


TOOLCHAIN:pn-firefox = "clang"
AS = "${CC}"

DISABLE_STATIC=""

PACKAGECONFIG ??= "${@bb.utils.contains("DISTRO_FEATURES", "alsa", "alsa", "", d)} \
                   ${@bb.utils.contains("DISTRO_FEATURES", "wayland", "wayland", "", d)} \
                   ${@bb.utils.contains_any("TARGET_ARCH", "x86_64 arm aarch64", "webrtc", "", d)} \
"

PACKAGECONFIG:append = "${@ ' system-nss system-nspr ' if ${ARM_AND_NO_HW_CRYPTO} else '' }"

PACKAGECONFIG[alsa] = "--enable-alsa,--disable-alsa,alsa-lib"
PACKAGECONFIG[wayland] = "--enable-default-toolkit=cairo-gtk3-wayland,--enable-default-toolkit=cairo-gtk3,virtual/egl,"
PACKAGECONFIG[gpu] = ",,,"
PACKAGECONFIG[openmax] = "--enable-openmax,,,"
PACKAGECONFIG[webgl] = ",,,"
PACKAGECONFIG[webrtc] = "--enable-webrtc,--disable-webrtc,,"
PACKAGECONFIG[forbid-multiple-compositors] = ",,,"
PACKAGECONFIG[disable-sandboxed-libraries] = "--without-wasm-sandboxed-libraries,,,"

# nspr and nss dependencies are handled separately, due to requiring
# different versions depending on Yocto version and CPU capabilities.
PACKAGECONFIG[system-nspr] = "--with-system-nspr,,nspr-4.35,nspr-4.35"
PACKAGECONFIG[system-nss] = "--with-system-nss,,nss-3.100,nss-3.100"
PACKAGECONFIG[system-ffi] = "--with-system-ffi,,libffi,libffi"
PACKAGECONFIG[system-icu] = "--with-system-icu,,icu,icu"
PACKAGECONFIG[system-zlib] = "--with-system-zlib,,zlib,zlib"
PACKAGECONFIG[system-pixman] = "--enable-system-pixman,,pixman,pixman"
PACKAGECONFIG[system-jpeg] = "--with-system-jpeg,,libjpeg-turbo,libjpeg-turbo"
PACKAGECONFIG[system-libevent] = "--with-system-libevent,,libevent,libevent"
PACKAGECONFIG[system-libvpx] = "--with-system-libvpx,,libvpx,libvpx"
PACKAGECONFIG[system-png] = "--with-system-png,,libpng,libpng"
PACKAGECONFIG[system-webp] = "--with-system-webp,,libwebp,libwebp"

DEPENDS:append = '${@bb.utils.contains("PACKAGECONFIG", "disable-sandboxed-libraries", "", " wasi-sdk-native ", d)}'

do_compile:prepend(){
    head -n 38 "${FF_BASEDIR}/cargo_home/config" > "${FF_BASEDIR}/cargo_home/config_tmp"
    mv "${FF_BASEDIR}/cargo_home/config_tmp" "${FF_BASEDIR}/cargo_home/config"
}

do_install:append() {
    install -d ${D}${datadir}/applications
    install -d ${D}${datadir}/pixmaps

    install -m 0644 ${FF_BASEDIR}/mozilla-firefox.desktop ${D}${datadir}/applications/
    install -m 0644 ${FF_BASEDIR}/mozilla-firefox.png ${D}${datadir}/pixmaps/
    install -m 0644 ${FF_BASEDIR}/prefs/vendor.js ${D}${libdir}/${PN}/defaults/pref/
    if [ -n "${@bb.utils.contains_any('PACKAGECONFIG', 'gpu', '1', '', d)}" ]; then
        install -m 0644 ${FF_BASEDIR}/prefs/gpu.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'openmax', '1', '', d)}" ]; then
        install -m 0644 ${FF_BASEDIR}/prefs/openmax.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'webgl', '1', '', d)}" ]; then
        install -m 0644 ${FF_BASEDIR}/prefs/webgl.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'forbit-multiple-compositors', '1', '', d)}" ]; then
        install -m 0644 ${FF_BASEDIR}/prefs/single-compositor.js ${D}${libdir}/${PN}/defaults/pref/
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

