# Copyright (C) 2009-2016, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DESCRIPTION ?= "Browser made by mozilla"
DEPENDS += "alsa-lib curl startup-notification libevent cairo libnotify libvpx \
            virtual/libgl nss nspr pulseaudio yasm-native icu"

LICENSE = "MPLv1 | GPLv2+ | LGPLv2.1+"
LIC_FILES_CHKSUM = "file://toolkit/content/license.html;endline=39;md5=f7e14664a6dca6a06efe93d70f711c0e"

SRC_URI = "https://archive.mozilla.org/pub/firefox/releases/${PV}/source/firefox-${PV}.source.tar.xz;name=archive \
           file://mozilla-firefox.png \
           file://mozilla-firefox.desktop \
           file://vendor.js \
           file://porting/Add-xptcall-support-for-SH4-processors.patch \
           file://porting/NSS-Fix-FTBFS-on-Hurd-because-of-MAXPATHLEN.patch \
           file://porting/Disable-libyuv-assembly-on-mips64.patch \
           file://porting/Make-powerpc-not-use-static-page-sizes-in-mozjemallo.patch \
           file://porting/NSS-GNU-kFreeBSD-support.patch \
           file://fix-python-path.patch \
           file://prefs/Set-DPI-to-system-settings.patch \
           file://prefs/Set-javascript.options.showInConsole.patch \
           file://prefs/Don-t-auto-disable-extensions-in-system-directories.patch \
           file://fixes/Bug-1178266-Link-against-libatomic-when-necessary.patch \
           file://fixes/Bug-1245076-Don-t-include-mozalloc.h-from-the-cstdli.patch \
           file://fixes/Allow-.js-preference-files-to-set-locked-prefs-with-.patch \
           file://fixes/Fix-build-error-in-MIPS-SIMD-when-compiling-with-mfp.patch \
           file://fixes/Bug-1259537-Unbreak-libc-build-after-bug-1245076.-r-.patch \
           file://fixes/Bug-1257888-Link-chromium-mutex-based-atomics-implem.patch \
           file://fixes/Bug-1239866-Remove-signaling-standalone-tests.-r-bwc.patch \
           file://fixes/Bug-1235107-Move-bookmarks.html-to-a-chrome-localize.patch \
           file://debian-hacks/Add-a-2-minutes-timeout-on-xpcshell-tests.patch \
           file://debian-hacks/Add-another-preferences-directory-for-applications-p.patch \
           file://debian-hacks/Allow-unsigned-addons-in-usr-lib-share-mozilla-exten.patch \
           file://debian-hacks/Work-around-binutils-assertion-on-mips.patch \
           file://debian-hacks/Don-t-error-out-when-run-time-libsqlite-is-older-tha.patch \
           file://debian-hacks/Use-a-variable-for-xulrunner-base-version-in-various.patch \
           file://debian-hacks/Don-t-register-plugins-if-the-MOZILLA_DISABLE_PLUGIN.patch \
           file://debian-hacks/Avoid-wrong-sessionstore-data-to-keep-windows-out-of.patch \
           file://Fix-firefox-install-dir.patch \
           file://fixes/Correct-the-source-to-be-compatible-with-gcc6-by-pre.patch \
           "

SRC_URI[archive.md5sum] = "ee3cf2401a5716cebacaae5fb70d133f"
SRC_URI[archive.sha256sum] = "c1e7ddf6efb0f54c8071131b6395f4942a422c2ab70f2e9a81b588373d6fbf5b"

S = "${WORKDIR}/firefox-45.6.0esr"
# MOZ_APP_BASE_VERSION should be incremented after a release
MOZ_APP_BASE_VERSION = "45.6"

inherit mozilla

EXTRA_OEMAKE += "installdir=${libdir}/${PN}"

ARM_INSTRUCTION_SET = "arm"

CFLAGS +=" -fno-delete-null-pointer-checks -fno-lifetime-dse"
CXXFLAGS +=" -fno-delete-null-pointer-checks -fno-lifetime-dse"
TARGET_CC_ARCH += "${LDFLAGS}"

do_install_append() {
    install -d ${D}${datadir}/applications
    install -d ${D}${datadir}/pixmaps
    install -d ${D}${libdir}/${PN}/defaults/pref

    install -m 0644 ${WORKDIR}/mozilla-firefox.desktop ${D}${datadir}/applications/
    install -m 0644 ${WORKDIR}/mozilla-firefox.png ${D}${datadir}/pixmaps/
    install -m 0644 ${WORKDIR}/vendor.js ${D}${libdir}/${PN}/defaults/pref/

    # Fix ownership of files
    chown root:root -R ${D}${datadir}
    chown root:root -R ${D}${libdir}
}

FILES_${PN} = "${bindir}/${PN} \
               ${datadir}/applications/ \
               ${datadir}/pixmaps/ \
               ${libdir}/${PN}/* \
               ${bindir}/defaults"
FILES_${PN}-dev += "${datadir}/idl ${bindir}/${PN}-config ${libdir}/${PN}-devel-*"
FILES_${PN}-staticdev += "${libdir}/${PN}-devel-*/sdk/lib/*.a"
FILES_${PN}-dbg += "${libdir}/${PN}/*/*/.debug/* ${libdir}/${PN}/*/.debug/* ${libdir}/${PN}-devel-*/sdk/lib/.debug/*"
# We don't build XUL as system shared lib, so we can mark all libs as private
PRIVATE_LIBS = "libmozjs.so \
                libxpcom.so \
                libnspr4.so \
                libxul.so \
                libmozalloc.so \
                libplc4.so \
                libplds4.so \
                liblgpllibs.so \
                libmozsqlite3.so \
                libbrowsercomps.so \
                libclearkey.so"

# mark libraries also provided by nss as private too
PRIVATE_LIBS += " \
    libfreebl3.so \
    libnss3.so \
    libnssckbi.so \
    libsmime3.so \
    libnssutil3.so \
    libnssdbm3.so \
    libssl3.so \
    libsoftokn3.so \
"
