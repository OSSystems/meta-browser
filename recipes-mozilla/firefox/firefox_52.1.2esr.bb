# Copyright (C) 2009-2017, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DESCRIPTION ?= "Browser made by mozilla"
DEPENDS += "alsa-lib curl startup-notification libevent libnotify libvpx \
            virtual/libgl nss nspr pulseaudio yasm-native icu unzip-native"

LICENSE = "MPLv1 | GPLv2+ | LGPLv2.1+"
LIC_FILES_CHKSUM = "file://toolkit/content/license.html;endline=39;md5=f7e14664a6dca6a06efe93d70f711c0e"

SRC_URI = "https://archive.mozilla.org/pub/firefox/releases/${PV}/source/firefox-${PV}.source.tar.xz;name=archive \
           file://mozilla-firefox.png \
           file://mozilla-firefox.desktop \
           file://vendor.js \
           file://fix-python-path.patch \
           file://fix-autoconf2.13-path.patch \
           file://remove-needless-windows-dependency.patch \
           file://fixes/Allow-.js-preference-files-to-set-locked-prefs-with-.patch \
           file://porting/Add-xptcall-support-for-SH4-processors.patch \
           file://porting/NSS-Fix-FTBFS-on-Hurd-because-of-MAXPATHLEN.patch \
           file://porting/NSS-GNU-kFreeBSD-support.patch \
           file://porting/Make-powerpc-not-use-static-page-sizes-in-mozjemallo.patch \
           file://porting/Disable-libyuv-assembly-on-mips64.patch \
           file://porting/Fix-CPU_ARCH-test-for-libjpeg-on-mips.patch \
           file://porting/Fix-crashes-in-AtomicOperations-none-on-s390x.patch \
           file://porting/Fix-broken-MIPS-build.patch \
           file://porting/Work-around-Debian-bug-844357.patch \
           file://prefs/Set-javascript.options.showInConsole.patch \
           file://prefs/Set-DPI-to-system-settings.patch \
           file://prefs/Don-t-auto-disable-extensions-in-system-directories.patch \
           file://debian-hacks/Avoid-wrong-sessionstore-data-to-keep-windows-out-of.patch \
           file://debian-hacks/Add-another-preferences-directory-for-applications-p.patch \
           file://debian-hacks/Don-t-register-plugins-if-the-MOZILLA_DISABLE_PLUGIN.patch \
           file://debian-hacks/Use-a-variable-for-xulrunner-base-version-in-various.patch \
           file://debian-hacks/Don-t-error-out-when-run-time-libsqlite-is-older-tha.patch \
           file://debian-hacks/Add-a-2-minutes-timeout-on-xpcshell-tests.patch \
           file://debian-hacks/Work-around-binutils-assertion-on-mips.patch \
           file://debian-hacks/Don-t-build-image-gtests.patch \
           file://debian-hacks/Allow-to-override-ICU_DATA_FILE-from-the-environment.patch \
           file://debian-hacks/Force-use-the-i686-rust-target.patch \
           file://debian-hacks/Set-program-name-from-the-remoting-name.patch \
           file://debian-hacks/Build-against-system-libjsoncpp.patch \
           file://debian-hacks/Use-the-Mozilla-Location-Service-key-when-the-Google.patch \
           file://0001-use-pkg-config-to-find-nss.patch \
           file://0002-use-pkg-config-to-find-nspr.patch \
           file://0003-do-not-link-against-crmf-library-it-is-not-there.patch \
           "

SRC_URI[archive.md5sum] = "cf3e1f554836056bbe6a6b78b61fe68a"
SRC_URI[archive.sha256sum] = "388e0454d69801f0f2010f8f93b796420ec0493fbf138fc659dbd5b497b8373b"

S = "${WORKDIR}/firefox-${PV}"
# MOZ_APP_BASE_VERSION should be incremented after a release
MOZ_APP_BASE_VERSION = "52.1"

inherit mozilla

DISABLE_STATIC=""
EXTRA_OEMAKE += "installdir=${libdir}/${PN}"

ARM_INSTRUCTION_SET = "arm"

CFLAGS +=" -fno-delete-null-pointer-checks -fno-lifetime-dse"
CXXFLAGS +=" -fno-delete-null-pointer-checks -fno-lifetime-dse"

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
