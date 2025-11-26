DESCRIPTION ?= "Browser made by Mozilla"

DEPENDS:append = "pulseaudio cbindgen-native clang-native \
            nodejs-native python3-zstandard gtk+3 \
            libevent dbus-glib"

DEPENDS:x86-64:append = " nasm-native "
DEPENDS:x86:append = " nasm-native "

RDEPENDS:${PN} = "libva libpci"
RDEPENDS:${PN}-dev = "dbus"

DEPENDS:append:libc-musl = " libexecinfo "

# Disable hw crypto support in freebl/nss in case the CPU has no native crypto support (e.g. armv7)
EXTRA_OECONF:append = "${@ ' --nss-disable-arm-crypto=1 ' if 'crypto' not in d.getVar('TUNE_FEATURES') and \
                        ( 'arm' in d.getVar('TARGET_ARCH') or \
                          'aarch64' in d.getVar('TARGET_ARCH') \
                        ) else '' }"

LICENSE = "MPL-2.0"

LIC_FILES_CHKSUM = "file://toolkit/content/license.html;md5=d58bde4dde94cc14c4ce52499979fe4a;endline=5 \
                    file://LICENSE;md5=dc9b6ecd19a14a54a628edaaf23733bf"

CVE_PRODUCT = "mozilla:firefox"


FF_BASEDIR = '${@ "${WORKDIR}" if d.getVar("UNPACKDIR") == None \
                 else d.getVar("UNPACKDIR")}'

S = "${FF_BASEDIR}/firefox-${MOZ_APP_BASE_VERSION}"

MOZ_APP_BASE_VERSION = "${@'${PV}'.replace('esr', '')}"

inherit mozilla

# target-c-int-width size for rust target
# in oe-core is defined as 64, however it fails to compile
# a small handful of crates.
TARGET_C_INT_WIDTH[riscv64gc] = "32"

SRC_URI += "https://ftp.mozilla.org/pub/firefox/releases/${PV}/source/firefox-${PV}.source.tar.xz \
           file://mozilla-firefox.png \
           file://mozilla-firefox.desktop \
           file://mozconfig \
           file://prefs/vendor.js \
           file://bug1545437-enable-to-specify-rust-target.patch \
           file://0001-make-build-reproducible-by-removing-TMPDIR.patch \
           file://0001-trust-yocto-rust-binary.patch \
           file://0004-Fix-compiling-with-enable-openmax-config.patch \
           file://0001-rust-don-t-abort-on-panic.patch \
           file://0001-add-musl-support.patch \
           file://0001-Add-option-to-disable-arm-hw-crypto-engine.patch \
           file://freebl-ignore-unused-arguments-during-configure.patch \
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
           "

SRCREV_FORMAT = "default"

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

DISABLE_STATIC = ""

PACKAGECONFIG ??= "${@bb.utils.contains("DISTRO_FEATURES", "alsa", "alsa", "", d)} \
                   ${@bb.utils.contains("DISTRO_FEATURES", "wayland", "wayland", "", d)} \
                   ${@bb.utils.contains_any("TARGET_ARCH", "x86_64 arm aarch64", "webrtc", "", d)} \
                   rust-simd disable-gecko-profiler \
"

PACKAGECONFIG[alsa] = "--enable-alsa,--disable-alsa,alsa-lib"
PACKAGECONFIG[wayland] = "--enable-default-toolkit=cairo-gtk3-wayland,--enable-default-toolkit=cairo-gtk3,virtual/egl,"
PACKAGECONFIG[gpu] = ",,,"
PACKAGECONFIG[openmax] = "--enable-openmax,,,"
PACKAGECONFIG[webgl] = ",,,"
PACKAGECONFIG[webrtc] = "--enable-webrtc,--disable-webrtc,,"
PACKAGECONFIG[forbid-multiple-compositors] = ",,,"
PACKAGECONFIG[disable-sandboxed-libraries] = "--without-wasm-sandboxed-libraries,,,"
PACKAGECONFIG[rust-simd] = "--enable-rust-simd,,"

PACKAGECONFIG[system-nspr] = "--with-system-nspr,,nspr,nspr"
PACKAGECONFIG[system-nss] = "--with-system-nss,,nss,nss"
PACKAGECONFIG[system-ffi] = "--with-system-ffi,,libffi,libffi"
PACKAGECONFIG[system-icu] = "--with-system-icu,,icu,icu"
PACKAGECONFIG[system-zlib] = "--with-system-zlib,,zlib,zlib"
PACKAGECONFIG[system-pixman] = "--with-system-pixman,,pixman,pixman"
PACKAGECONFIG[system-jpeg] = "--with-system-jpeg,,libjpeg-turbo,libjpeg-turbo"
PACKAGECONFIG[system-libevent] = "--with-system-libevent,,libevent,libevent"
PACKAGECONFIG[system-libvpx] = "--with-system-libvpx,,libvpx,libvpx"
PACKAGECONFIG[system-png] = "--with-system-png,,libpng,libpng"
PACKAGECONFIG[system-webp] = "--with-system-webp,,libwebp,libwebp"
PACKAGECONFIG[x11-only] = "--enable-default-toolkit=cairo-gtk3-x11-only,,,,,wayland-only"
PACKAGECONFIG[wayland-only] = "--enable-default-toolkit=cairo-gtk3-wayland-only,,virtual/egl,,,x11-only"
PACKAGECONFIG[disable-gecko-profiler] = "--disable-gecko-profiler,,,"
PACKAGECONFIG[system-av1] = "--with-system-av1,,aom dav1d"
PACKAGECONFIG[system-pipewire] = "--with-system-pipewire,,pipewire,pipewire"
PACKAGECONFIG[system-gbm] = "--with-system-gbm,,virtual/libgbm,libgbm"
PACKAGECONFIG[system-libdrm] = "--with-system-libdrm,,libdrm,libdrm"

DEPENDS:append = '${@bb.utils.contains("PACKAGECONFIG", "disable-sandboxed-libraries", "", " wasi-sdk-sysroot-native ", d)}'

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

