# Copyright (C) 2009-2012, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DEPENDS = "unzip-native"
RDEPENDS = "firefox"

LICENSE = "MPL-1.1"

# Ugly hack for now; we need to check license per recipe later
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=3f40d7994397109285ec7b81fdeb3b58"

S = "${WORKDIR}"

do_configure() {
	unzip -o -q -d xpi *.xpi
}

INC_PR = "r1"

inherit allarch

do_install() {
	cd ${S}/xpi

	if [ -z ${EXTENSION} ]; then
	   echo "You NEED to define EXTENSION in your recipe. Exiting"
	   exit 1
	fi

	mkdir -p ${D}/usr/lib/firefox/extensions/${EXTENSION}/chrome
	mkdir -p ${D}/usr/lib/firefox/extensions/${EXTENSION}/uninstall
	install -m 644 chrome/*.jar ${D}/usr/lib/firefox/extensions/${EXTENSION}/chrome
	install -m 644 install.rdf ${D}/usr/lib/firefox/extensions/${EXTENSION}
	install -m 644 chrome.manifest ${D}/usr/lib/firefox/extensions/${EXTENSION}

	cat install.rdf > ${D}/usr/lib/firefox/extensions/${EXTENSION}/uninstall/Uninstall
}

FILES_${PN} += "/usr/lib/firefox"
