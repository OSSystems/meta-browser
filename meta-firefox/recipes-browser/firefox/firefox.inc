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
           file://0002-use-offline-crates.patch \
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
           git://github.com/mozilla/application-services.git;protocol=https;branch=main;name=application-services;destsuffix=application-services \
           git://github.com/mozilla/audioipc;protocol=https;branch=master;name=audioipc;destsuffix=audioipc \
           git://github.com/jamienicol/glutin;protocol=https;branch=wr;name=glutin;destsuffix=glutin \
           git://github.com/beurdouche/nss-gk-api;protocol=https;branch=main;name=nss-gk-api;destsuffix=nss-gk-api \
           git://github.com/beurdouche/mls-rs;protocol=https;branch=main;name=mls-rs;destsuffix=mls-rs \
           git://github.com/beurdouche/mls-platform-api;protocol=https;branch=main;name=mls-platform-api;destsuffix=mls-platform-api \
           git://github.com/glandium/rust-objc;protocol=https;branch=ATOMIC_USIZE_INIT;name=objc;destsuffix=objc \
           git://github.com/servo/osmesa-src;protocol=https;branch=main;name=osmesa-src;destsuffix=osmesa-src \
           git://github.com/servo/rust-cssparser;protocol=https;branch=main;name=cssparser;destsuffix=cssparser \
           git://github.com/hsivonen/chardetng.git;protocol=https;branch=tldquery;name=chardetng;destsuffix=chardetng \
           git://github.com/hsivonen/chardetng_c.git;protocol=https;branch=tldquery;name=chardetng_c;destsuffix=chardetng_c \
           git://github.com/chris-zen/coremidi.git;protocol=https;branch=master;name=coremidi;destsuffix=coremidi \
           git://github.com/mozilla/midir.git;protocol=https;branch=firefox-vendoring;name=midir;destsuffix=midir \
           git://github.com/FirefoxGraphics/aa-stroke;protocol=https;branch=main;name=aa-stroke;destsuffix=aa-stroke \
           git://github.com/mozilla/cubeb-coreaudio-rs;protocol=https;branch=trailblazer;name=cubeb-coreaudio;destsuffix=cubeb-coreaudio \
           git://github.com/mozilla/cubeb-pulse-rs;protocol=https;branch=dev;name=cubeb-pulse;destsuffix=cubeb-pulse \
           git://github.com/jfkthame/mapped_hyph;protocol=https;branch=master;name=mapped_hyph;destsuffix=mapped_hyph \
           git://github.com/FirefoxGraphics/wpf-gpu-raster;protocol=https;branch=rust-only;name=wpf-gpu-raster;destsuffix=wpf-gpu-raster \
           git://github.com/mozilla/neqo;protocol=https;branch=main;name=neqo;destsuffix=neqo \
           git://github.com/mozilla/mp4parse-rust;protocol=https;branch=master;name=mp4parse;destsuffix=mp4parse \
           git://github.com/hsivonen/any_all_workaround;protocol=https;branch=simdsplit;name=any-all-workaround;destsuffix=any-all-workaround \
           git://github.com/franziskuskiefer/cose-rust;protocol=https;branch=master;name=cose-rust;destsuffix=cose-rust \
           git://github.com/servo/unicode-bidi;protocol=https;branch=main;name=unicode-bidi;destsuffix=unicode-bidi \
           "

SRCREV_FORMAT .= "_aa-stroke"
SRCREV_aa-stroke = "a821fa621c2def48e90c82774b4c6563b5a8ea4a"

SRCREV_FORMAT .= "_any-all-woraround" 
SRCREV_any-all-workaround = "7fb1b7034c9f172aade21ee1c8554e8d8a48af80"

SRCREV_FORMAT .= "_audioipc"
SRCREV_audioipc = "e6f44a2bd1e57d11dfc737632a9e849077632330"

SRCREV_FORMAT .= "_chardetng"
SRCREV_chardetng = "3484d3e3ebdc8931493aa5df4d7ee9360a90e76b"

SRCREV_FORMAT .= "_chardetng_c"
SRCREV_chardetng_c = "ed8a4c6f900a90d4dbc1d64b856e61490a1c3570"

SRCREV_FORMAT .= "_coremidi"
SRCREV_coremidi = "fc68464b5445caf111e41f643a2e69ccce0b4f83"

SRCREV_FORMAT .= "_cose-rust"
SRCREV_cose-rust = "43c22248d136c8b38fe42ea709d08da6355cf04b"

SRCREV_FORMAT .= "_cssparser"
SRCREV_cssparser = "958a3f098acb92ddacdce18a7ef2c4a87ac3326f"

SRCREV_FORMAT .= "_cubeb-coreaudio"
SRCREV_cubeb-coreaudio = "2407441a2f67341a0e13b4ba6547555e387c671c"

SRCREV_FORMAT .= "_cubeb-pulse"
SRCREV_cubeb-pulse = "8678dcab1c287de79c4c184ccc2e065bc62b70e2"

SRCREV_FORMAT .= "_glutin"
SRCREV_glutin = "03285da9c14ec56296c2400c781d2c32b80d745a"

SRCREV_FORMAT .= "_mapped_hyph"
SRCREV_mapped_hyph = "eff105f6ad7ec9b79816cfc1985a28e5340ad14b"

SRCREV_FORMAT .= "_mls-platform-api"  
SRCREV_mls-platform-api = "5d88241b9765cae3669aba21f0946bd3700f7db1"

SRCREV_FORMAT .= "_mls-rs"
SRCREV_mls-rs = "b747d7efb85a776b97ad8afa8d1b32893fa5efa3"

SRCREV_FORMAT .= "_nss-gk-api"
SRCREV_nss-gk-api = "e48a946811ffd64abc78de3ee284957d8d1c0d63"

SRCREV_FORMAT .= "_objc"
SRCREV_objc = "4de89f5aa9851ceca4d40e7ac1e2759410c04324"

SRCREV_FORMAT .= "_osmesa-src"
SRCREV_osmesa-src = "a9c57bae855c8a16c8397c04127f75ab14171c81"

SRCREV_FORMAT .= "_unicode-bidi"
SRCREV_unicode-bidi = "ca612daf1c08c53abe07327cb3e6ef6e0a760f0c"

SRCREV_FORMAT .= "_wpf-gpu-raster"
SRCREV_wpf-gpu-raster = "99979da091fd58fba8477e7fcdf5ec0727102916"

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

DEPENDS:append = '${@bb.utils.contains("PACKAGECONFIG", "disable-sandboxed-libraries", "", " wasi-sdk-native ", d)}'

LDFLAGS:append = "${@bb.utils.contains('PACKAGECONFIG', 'system-av1', ' -Wl,--undefined-version ', '', d)}"

# At the end of do_configure task cargo-common class takes all
# the git repos from the SRC_URI, and appends them to the end
# of cargo_home/config as repo patches for separate crates, if
# they have a name and destsuffix parameter.

# This works for most crates, however not for all - this is mostly
# a problem for repositories where the source code is not in the
# top level folder, and for repositories which are actually a workspace,
# and may contain multiple crates in multiple subfolders - they might
# even refer to different subfolders. When this is the case, the above
# method only sets the top folder in the patch, which makes cargo not
# find the crates, and fail. This is also the main reason why Cargo.toml
# is patched directly, instead of letting cargo_common class just include
# these patches.

# To avoid this, save the SRC_URI variable before the do_configure
# task, and remove all the git repos from it. At the end of the do_configure
# task just restore it to its original value.
python save_src_uri(){
  d.setVar("SRC_URI_BAK", d.getVar("SRC_URI"))
  all_uris = d.getVar("SRC_URI").split()

  uris_without_git = ""
  for uri in all_uris:
    if not uri.startswith("git://"):
      uris_without_git += " " + uri

  d.setVar("SRC_URI", uris_without_git)
}

python restore_src_uri(){
  d.setVar("SRC_URI", d.getVar("SRC_URI_BAK"))
}

do_configure[prefuncs] += "save_src_uri"
do_configure[postfuncs] += "restore_src_uri"


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

