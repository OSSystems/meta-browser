# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DESCRIPTION ?= "Browser made by mozilla"
DEPENDS += "alsa-lib curl startup-notification libevent cairo libnotify libvpx \
            virtual/libgl nss nspr pulseaudio yasm-native icu"

LICENSE = "MPLv1 | GPLv2+ | LGPLv2.1+"
LIC_FILES_CHKSUM = "file://toolkit/content/license.html;endline=39;md5=f7e14664a6dca6a06efe93d70f711c0e"

SRC_URI = "https://archive.mozilla.org/pub/firefox/releases/${PV}/source/firefox-${PV}.source.tar.bz2;name=archive \
           file://mozilla-firefox.png \
           file://mozilla-firefox.desktop \
           file://vendor.js \
           file://fix-python-path.patch \
           file://prefs/Don-t-auto-disable-extensions-in-system-directories.patch \
           file://prefs/Set-DPI-to-system-settings.patch \
           file://prefs/Set-javascript.options.showInConsole.patch \
           file://porting/Add-xptcall-support-for-SH4-processors.patch \
           file://porting/NSS-Fix-FTBFS-on-Hurd-because-of-MAXPATHLEN.patch \
           file://porting/NSS-GNU-kFreeBSD-support.patch \
           file://porting/Make-powerpc-not-use-static-page-sizes-in-mozjemalloc.patch \
           file://fixes/Allow-.js-preference-files-to-set-locked-prefs-with-.patch \
           file://fixes/Avoid-spurious-Run-items-in-application-handlers.patch \
           file://fixes/Bug-1136958-Remove-duplicate-SkDiscardableMemory_none.patch \
           file://fixes/Bug-1166243-Remove-build-function-from-js-and-xpc.patch \
           file://fixes/Bug-1166538-Use-dozip.py-for-langpacks.patch \
           file://fixes/Bug-1094324-Set-browser.newtabpage.enhanced-default.patch \
           file://fixes/Bug-1168231-Normalize-file-mode-in-jars.patch \
           file://fixes/Bug-1098343-part-1-support-sticky-preferences-meaning.patch \
           file://fixes/Fix-build-error-in-MIPS-SIMD-when-compiling-with-mfpxx.patch \
           file://iceweasel-branding/Use-MOZ_APP_DISPLAYNAME-to-fill-appstrings.properties.patch \
           file://iceweasel-branding/Modify-search-plugins-depending-on-MOZ_APP_NAME.patch \
           file://iceweasel-branding/Determine-which-phishing-shavar-to-use-depending-on.patch \
           file://iceweasel-branding/Use-firefox-instead-of-MOZ_APP_NAME-for-profile-reset.patch \
           file://debian-hacks/Avoid-wrong-sessionstore-data-to-keep-windows-out-of.patch \
           file://debian-hacks/Add-another-preferences-directory-for-applications.patch \
           file://debian-hacks/Don-t-register-plugins-if-the-MOZILLA_DISABLE_PLUGINS.patch \
           file://debian-hacks/Use-a-variable-for-xulrunner-base-version-in-various.patch \
           file://debian-hacks/Don-t-error-out-when-run-time-libsqlite-is-older-than.patch \
           file://debian-hacks/Add-a-2-minutes-timeout-on-xpcshell-tests.patch \
           file://debian-hacks/Load-distribution-search-plugins-from.patch \
           file://debian-hacks/Handle-transition-to-etc-appname-searchplugins.patch \
           file://debian-hacks/Preprocess-appstrings.properties.patch \
           file://debian-hacks/Disable-Firefox-Health-Report.patch \
           file://debian-hacks/Bump-search-engine-max-icon-size-to-35kB.patch \
           file://debian-hacks/NSS-Adds-the-SPI-Inc.-and-CAcert.org-CA-certificates.patch \
           file://debian-hacks/Work-around-binutils-assertion-on-mips.patch \
           file://debian-hacks/Revert-Bump-search-engine-max-icon-size-to-35kB.patch \
           file://fixes/Fix-firefox-install-dir.patch \
           "

SRC_URI[archive.md5sum] = "af46898414a433f8260c5373efb97d19"
SRC_URI[archive.sha256sum] = "9475adcee29d590383c4885bc5f958093791d1db4302d694a5d2766698f59982"

S = "${WORKDIR}/mozilla-esr38"
# MOZ_APP_BASE_VERSION should be incremented after a release
MOZ_APP_BASE_VERSION = "38.8"

inherit mozilla

EXTRA_OEMAKE += "installdir=${libdir}/${PN}"

ARM_INSTRUCTION_SET = "arm"

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
                libplds4.so"

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

# | i586-oe-linux-gcc  -m32 -march=i586 --sysroot=/home/jenkins/oe/world/shr-core/tmp-glibc/sysroots/qemux86 -o WebMElement.o -c
#  -I../../dist/system_wrappers -include /home/jenkins/oe/world/shr-core/tmp-glibc/work/i586-oe-linux/firefox/38.7.1esr-r0/mozilla-esr38/config/gcc_hidden.h -DMOZ_GLUE_IN_PROGRAM -DAB_CD=en-US -DNO_NSPR_10_SUPPORT
#  -I/home/jenkins/oe/world/shr-core/tmp-glibc/work/i586-oe-linux/firefox/38.7.1esr-r0/mozilla-esr38/media/libmkv -I. 
#  -I../../dist/include   -I/home/jenkins/oe/world/shr-core/tmp-glibc/work/i586-oe-linux/firefox/38.7.1esr-r0/mozilla-esr38/firefox-build-dir/dist/include/nspr -I/home/jenkins/oe/world/shr-core/tmp-glibc/work/i586-oe-linux/firefox/38.7.1esr-r0/mozilla-esr38/firefox-build-dir/dist/include/nss   
#  -I/home/jenkins/oe/world/shr-core/tmp-glibc/sysroots/qemux86/usr/include/pixman-1   -fPIC   -include ../../mozilla-config.h -DMOZILLA_CLIENT -MD -MP -MF .deps/WebMElement.o.pp  -Wall -Wdeclaration-after-statement -Wempty-body -Wpointer-to-int-cast -Wsign-compare -Wtype-limits -Wno-unused -Wcast-align -Os -fsigned-char -fno-strict-aliasing -std=gnu99 -fgnu89-inline -fno-strict-aliasing -fno-math-errno -pthread -pipe  -DNDEBUG -DTRIMMED -freorder-blocks -Os -fomit-frame-pointer     /home/jenkins/oe/world/shr-core/tmp-glibc/work/i586-oe-linux/firefox/38.7.1esr-r0/mozilla-esr38/media/libmkv/WebMElement.c
# | In file included from ../../../dist/system_wrappers/stdlib.h:3:0,
# |                  from ../../../dist/include/mozilla/mozalloc.h:15,
# |                  from ../../../dist/stl_wrappers/cstdlib:39,
# |                  from /home/jenkins/oe/world/shr-core/tmp-glibc/work/i586-oe-linux/firefox/38.7.1esr-r0/mozilla-esr38/gfx/graphite2/src/inc/Main.h:29,
# |                  from /home/jenkins/oe/world/shr-core/tmp-glibc/work/i586-oe-linux/firefox/38.7.1esr-r0/mozilla-esr38/gfx/graphite2/src/CmapCache.cpp:28,
# |                  from /home/jenkins/oe/world/shr-core/tmp-glibc/work/i586-oe-linux/firefox/38.7.1esr-r0/mozilla-esr38/firefox-build-dir/gfx/graphite2/src/Unified_cpp_gfx_graphite2_src0.cpp:11:
# | /home/jenkins/oe/world/shr-core/tmp-glibc/sysroots/qemux86/usr/include/c++/6.1.1/stdlib.h:38:12: error: 'std::abort' has not been declared
# |  using std::abort;
PNBLACKLIST[firefox] ?= "BROKEN: fails to build with gcc-6"
