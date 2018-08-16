# Copyright (C) 2009-2014, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DEPENDS = "mozilla-devscripts-native unzip-native zip-native"
# drop to keep allarch
# RDEPENDS_${PN} = "firefox"

LICENSE = "MPLv2"

LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MPL-2.0;md5=815ca599c9df247a0c7f619bab123dad"

LANGUAGE = "${@get_language_name('${PN}')}"
PR = "r3"

# Mangle place where to store the file
DL_DIR_append = "/${PN}-${PV}"

SRC_URI = "https://archive.mozilla.org/pub/firefox/releases/${PV}/linux-i686/xpi/${LANGUAGE}.xpi"
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
    xpi-unpack ${LANGUAGE}.xpi ${LANGUAGE}
}

do_install() {
    EXTENSION=`sed --posix '/em:id=/!d;s/[ ]*em:id="//;s/".*//' ${LANGUAGE}/install.rdf`

    xpi-pack ${LANGUAGE} ${EXTENSION}.xpi

    mkdir -p ${D}${libdir}/firefox/browser/extensions/
    install -m 0644 ${EXTENSION}.xpi ${D}${libdir}/firefox/browser/extensions/${EXTENSION}.xpi
}

FILES_${PN} += "${libdir}/firefox"
