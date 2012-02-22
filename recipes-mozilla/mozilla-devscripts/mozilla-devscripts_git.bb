DESCRIPTION = "Development scripts used by Mozilla's addons packages"
SECTION = "devel"
DEPENDS = "python-native"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

PV = "0.30"
PR = "r1"

SRCREV = "9634b056366addd80d3ddb79cf22cf3ddcc65ffe"
SRC_URI = "git://git.debian.org/pkg-mozext/mozilla-devscripts.git;protocol=git"

S = "${WORKDIR}/git"

inherit distutils

do_install_append() {
    rm -r ${D}${datadir}
    rm ${D}${bindir}/dh_xul-ext ${D}${bindir}/install-xpi
}

RDEPENDS_${PN} += "unzip"

BBCLASSEXTEND = "native"
