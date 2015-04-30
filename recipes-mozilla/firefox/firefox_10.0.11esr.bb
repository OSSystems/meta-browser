# Copyright (C) 2009-2013, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DESCRIPTION ?= "Browser made by mozilla"
DEPENDS += "alsa-lib curl startup-notification libevent cairo libnotify libvpx virtual/libgl nss nspr"

LICENSE = "MPLv1 | GPLv2+ | LGPLv2.1+"
LIC_FILES_CHKSUM = "file://toolkit/content/license.html;endline=39;md5=9cb02f27e77e702043b827c9418bfbf8"

SRC_URI = "ftp://ftp.mozilla.org/pub/mozilla.org/firefox/releases/${PV}/source/firefox-${PV}.source.tar.bz2;name=archive \
           file://mozilla-${BPN}.png \
           file://mozilla-${BPN}.desktop \
           file://fixes/Allow-.js-preference-files-to-set-locked-prefs-with-.patch \
           file://fixes/Avoid-spurious-Run-items-in-application-handlers-con.patch \
           file://fixes/Properly-launch-applications-set-in-HOME-.mailcap.patch \
           file://fixes/Fix-some-tests-using-CurProcD-where-GreD-should-be-u.patch \
           file://fixes/Bug-691898-Use-YARR-interpreter-instead-of-PCRE-on-p.patch \
           file://fixes/Bug-722127-Bump-required-libvpx-version-to-1.0.0.-r-.patch \
           file://fixes/Bug-728229-Allow-to-build-with-system-python-ply-lib.patch \
           file://fixes/Bug-720682-Don-t-crash-an-app-using-libxul-because-o.patch \
           file://fixes/Bug-696636-Block-OpenGL-1-drivers-explicitly-to-stee.patch \
           file://fixes/Load-dependent-libraries-with-their-real-path-to-avo.patch \
           file://fixes/Bug-515232-Try-getting-general.useragent.locale-as-a.patch \
           file://fixes/Bug-729817-Block-the-Nouveau-3D-driver-as-it-s-insta.patch \
           file://fixes/Bug-729817-Allow-the-Nouveau-driver-with-Mesa-8.0.1-.patch \
           file://fixes/Bug-747322-Fix-jemalloc-mmap-wrapper-for-s390x.patch \
           file://fixes/Bug-725655-gcc-4.7-build-failures-missing-headers-.-.patch \
           file://fixes/Bug-734490-fix-build-failures-with-Clang-and-GCC-4.7.patch \
           file://fixes/Bug-706724-Fix-for-error-ftruncate-was-not-declared-.patch \
           file://fixes/Bug-709259-Try-creating-a-named-cursor-before-a-bitm.patch \
           file://fixes/Bug-761082-Only-export-TabMessageUtils.h-in-mozilla-.patch \
           file://fixes/bug-693343-a11y-disabled-in-Gnome-3-when-GNOME_ACCES.patch \
           file://fixes/Allow-webGL-with-mesa-assuming-users-will-have-updat.patch \
           file://iceweasel-branding/Use-MOZ_APP_DISPLAYNAME-to-fill-appstrings.propertie.patch \
           file://iceweasel-branding/Determine-which-phishing-shavar-to-use-depending-on-.patch \
           file://porting/Add-xptcall-support-for-SH4-processors.patch \
           file://porting/Allow-ipc-code-to-build-on-GNU-kfreebsd.patch \
           file://porting/Allow-ipc-code-to-build-on-GNU-Hurd.patch \
           file://porting/Bug-698923-Don-t-require-16-bytes-alignment-for-VMFr.patch \
           file://porting/Fix-GNU-non-Linux-failure-to-build-because-of-ipc-ch.patch \
           file://porting/Bug-703531-Fix-ARMAssembler-getOp2RegScale-on-ARMv5.patch \
           file://porting/Bug-703534-Fix-build-failure-on-platforms-without-YA.patch \
           file://porting/Bug-703842-Avoid-R_SPARC_WDISP22-relocation-in-Tramp.patch \
           file://porting/Bug-703833-Avoid-invalid-conversion-from-const-size_.patch \
           file://porting/Bug-711353-Add-support-for-GNU-kFreeBSD-and-GNU-Hurd.patch \
           file://porting/Bug-747870-Properly-align-XPCLazyCallContext-mData.-.patch \
           file://porting/Bug-706787-Crash-on-s390x-nsXPCComponents-AttachNewC.patch \
           file://prefs/Remove-general.useragent.locale-prefs-from-firefox-..patch \
           file://prefs/Enable-intl.locale.matchOS-and-report-the-locale-cor.patch \
           file://prefs/Set-javascript.options.showInConsole.patch \
           file://prefs/Set-DPI-to-system-settings.patch \
           file://prefs/Don-t-auto-disable-extensions-in-system-directories.patch \
           file://debian-hacks/Check-less-things-during-configure-when-using-libxul.patch \
           file://debian-hacks/Avoid-wrong-sessionstore-data-to-keep-windows-out-of.patch \
           file://debian-hacks/Avoid-libxpcom-being-excluded-from-linked-libraries-.patch \
           file://debian-hacks/Don-t-build-example-component.patch \
           file://debian-hacks/Don-t-install-system-profile.patch \
           file://debian-hacks/Ignore-system-libjpeg-libpng-and-zlib-version-checki.patch \
           file://debian-hacks/Add-soname-to-appropriate-libraries.patch \
           file://debian-hacks/Add-another-preferences-directory-for-applications-p.patch \
           file://debian-hacks/Gross-workaround-to-avoid-installing-test-idl-and-in.patch \
           file://debian-hacks/Don-t-register-plugins-if-the-MOZILLA_DISABLE_PLUGIN.patch \
           file://debian-hacks/Install-missing-nanojit-and-.tbl-headers-from-js-src.patch \
           file://debian-hacks/Use-a-variable-for-xulrunner-base-version-in-various.patch \
           file://debian-hacks/Install-js-shell-when-running-make-install-from-js-s.patch \
           file://debian-hacks/Don-t-error-out-when-run-time-libsqlite-is-older-tha.patch \
           file://debian-hacks/Do-build-time-detection-of-2-bytes-wchar_t-and-char1.patch \
           file://debian-hacks/pkg-config-files-don-t-need-to-require-the-version-o.patch \
           file://debian-hacks/Add-a-2-minutes-timeout-on-xpcshell-tests.patch \
           file://debian-hacks/Fix-tracejit-to-build-against-nanojit-headers-in-dis.patch \
           file://debian-hacks/Load-distribution-search-plugins-from-etc-appname-se.patch \
           file://debian-hacks/Handle-transition-to-etc-appname-searchplugins-more-.patch \
           file://debian-hacks/Bug-508942-Use-Preprocessor.py-filters-in-defines-an.patch \
           file://configure.patch \
           file://powerpc_va_list.patch \
           file://freetype-2.5.patch \
           file://x86_64-fix.patch \
           file://vendor.js"

SRC_URI[archive.md5sum] = "2f0e3a1dd7480e03f374c0121b4155e2"
SRC_URI[archive.sha256sum] = "94b4d5a339d97dc56fd349f93407c3af4f408a4a8409a64e3680d564d37594f8"

S = "${WORKDIR}/mozilla-esr10"

inherit mozilla

EXTRA_OEMAKE = "installdir=${libdir}/${PN}"

ARM_INSTRUCTION_SET = "arm"

do_install() {
	oe_runmake DESTDIR="${D}" destdir="${D}" install
	install -d ${D}${datadir}/applications
	install -d ${D}${datadir}/pixmaps
	install -m 0644 ${WORKDIR}/mozilla-${PN}.desktop ${D}${datadir}/applications/
	install -m 0644 ${WORKDIR}/mozilla-${PN}.png ${D}${datadir}/pixmaps/
	install -m 0644 ${WORKDIR}/vendor.js ${D}${libdir}/${PN}/defaults/pref/
	rm -f ${D}${libdir}/${PN}/TestGtkEmbed
	rm -f ${D}${libdir}/${PN}/defaults/pref/firefox-l10n.js

	# use locale settings
	grep -Rl intl.locale.matchOS ${D}${libdir}/${PN}/ \
	   | grep '.js$' \
	   | xargs sed -i 's/\(pref("intl.locale.matchOS",\s*\)false)/\1true)/g'

	# disable application updating
	grep -Rl app.update.enabled ${D}${libdir}/${PN}/ \
	   | grep '.js$' \
	   | xargs sed -i 's/\(pref("app.update.enabled",\s*\)true)/\1false)/g'
}

PACKAGES += "${PN}-inspector"

FILES_${PN}-inspector = "${libdir}/${PN}/chrome/inspector* \
                         ${libdir}/${PN}/components/*nspector* \
                         ${libdir}/${PN}/extensions/inspector* \
                         ${libdir}/${PN}/defaults/preferences/inspector*"
FILES_${PN} = "${bindir}/${PN} \
               ${datadir}/applications/ \
               ${datadir}/pixmaps/ \
               ${libdir}/${PN}/* \
               ${libdir}/${PN}/.autoreg \
               ${bindir}/defaults"
FILES_${PN}-dev += "${datadir}/idl ${bindir}/${PN}-config ${libdir}/${PN}-devel-*"
FILES_${PN}-staticdev += "${libdir}/${PN}-devel-*/sdk/lib/*.a"
FILES_${PN}-dbg += "${libdir}/${PN}/.debug \
                    ${libdir}/${PN}/*/.debug \
                    ${libdir}/${PN}/*/*/.debug \
                    ${libdir}/${PN}/*/*/*/.debug \
                    ${libdir}/${PN}-devel-*/*/.debug \
                    ${libdir}/${PN}-devel-*/*/*/.debug \
                    ${libdir}/${PN}-devel-*/*/*/*/.debug \
                    ${bindir}/.debug"

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
