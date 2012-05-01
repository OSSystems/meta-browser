DESCRIPTION ?= "Browser made by mozilla"
DEPENDS += "alsa-lib curl startup-notification libevent cairo libnotify libvpx virtual/libgl"

LICENSE = "MPLv1 | GPLv2+ | LGPLv2.1+"
LIC_FILES_CHKSUM = "file://toolkit/content/license.html;endline=39;md5=9cb02f27e77e702043b827c9418bfbf8"

PR = "r2"

SRC_URI = "ftp://ftp.mozilla.org/pub/mozilla.org/firefox/releases/${PV}/source/firefox-${PV}.source.tar.bz2;name=archive \
           file://mozilla-${PN}.png \
           file://mozilla-${PN}.desktop \
           file://fixes/Allow-.js-preference-files-to-set-locked-prefs-with-.patch \
           file://fixes/Avoid-spurious-Run-items-in-application-handlers-con.patch \
           file://fixes/Properly-launch-applications-set-in-HOME-.mailcap.patch \
           file://fixes/Fix-some-tests-using-CurProcD-where-GreD-should-be-u.patch \
           file://fixes/Bug-703633-TreePanel.jsm-uses-a-resource-url-that-ou.patch \
           file://fixes/Bug-691898-Use-YARR-interpreter-instead-of-PCRE-on-p.patch \
           file://fixes/Bug-722127-Bump-required-libvpx-version-to-1.0.0.-r-.patch \
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
           file://prefs/Remove-general.useragent.locale-prefs-from-firefox-..patch \
           file://prefs/Enable-intl.locale.matchOS-and-report-the-locale-cor.patch \
           file://prefs/Set-javascript.options.showInConsole.patch \
           file://prefs/Set-DPI-to-system-settings.patch \
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
           file://configure.patch \
           file://powerpc_va_list.patch \
           file://vendor.js \
           file://firefox.gcc-4.7.patch"

SRC_URI[archive.md5sum] = "5ce038d591964f72c534fa33b75a62f5"
SRC_URI[archive.sha256sum] = "6ec5feba56f180a6fbf437a68cf0c77146ade28b936e5187573e3d95f13a0449"

S = "${WORKDIR}/mozilla-release"

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
