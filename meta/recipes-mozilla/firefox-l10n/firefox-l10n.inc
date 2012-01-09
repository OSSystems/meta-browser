# Copyright (C) 2009, 2011, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DEPENDS = "unzip-native"
RDEPENDS = "firefox"
LICENSE = "CLOSED"
LANGUAGE = "${@get_language_name('${PN}')}"
FIREFOX_PV = "${PV}"
INC_PR = "r0"

# Mangle place where to store the file
DL_DIR_append = "/${PN}-${PV}"

SRC_URI = "ftp://ftp.mozilla.org/pub/mozilla.org/mozilla.org/firefox/releases/${PV}/linux-i686/xpi/${LANGUAGE}.xpi"
S = "${WORKDIR}"

inherit allarch

def get_language_name(package):
    lang = package.replace('firefox-l10n-', '')
    if lang.find('-') != -1:
        lang = lang.split('-')
        lang[1] = lang[1].upper()
        lang = '-'.join(lang)

    return lang

do_configure() {
	unzip -o -q -d ${LANGUAGE} *.xpi
}

do_install() {
	cd ${S}/${LANGUAGE}

	# Get extension UUID
	EXTENSION=`cat install.rdf | sed --posix '/em:id[=>]/!d;s/[[:space:]]*<*em:id[=>]"*//;s/["<].*//' | head -n 1`

	mkdir -p ${D}/usr/lib/firefox-${FIREFOX_PV}/extensions/${EXTENSION}/chrome
	mkdir -p ${D}/usr/lib/firefox-${FIREFOX_PV}/extensions/${EXTENSION}/uninstall
	cp -R chrome ${D}/usr/lib/firefox-${FIREFOX_PV}/extensions/${EXTENSION}/
	install -m 644 install.rdf ${D}/usr/lib/firefox-${FIREFOX_PV}/extensions/${EXTENSION}
	install -m 644 chrome.manifest ${D}/usr/lib/firefox-${FIREFOX_PV}/extensions/${EXTENSION}

	cat install.rdf \
	   | sed --posix '/<em:locale>/!d' \
	   | sed --posix "s/.*/register      global  locale  ${LANGUAGE}/" \
	   > ${D}/usr/lib/firefox-${FIREFOX_PV}/extensions/${EXTENSION}/uninstall/Uninstall
}

FILES_${PN} += "/usr/lib/firefox-${FIREFOX_PV}"
