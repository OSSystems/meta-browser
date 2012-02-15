# Copyright (C) 2009-2012, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DEPENDS = "mozilla-devscripts-native"
RDEPENDS = "firefox"

LICENSE = "MPLv1 | GPLv2+ | LGPLv2.1+"

# Ugly hack for now; we need to check license per recipe later
LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=3f40d7994397109285ec7b81fdeb3b58"

S = "${WORKDIR}"

INC_PR = "r2"

inherit allarch

do_configure() {
    xpi-unpack *.xpi xpi
}

do_install() {
    EXTENSION=`sed --posix '/em:id=/!d;s/[ ]*em:id="//;s/".*//' xpi/install.rdf`

    xpi-pack xpi ${EXTENSION}.xpi

    mkdir -p ${D}${libdir}/firefox/extensions/
    install -m 0644 ${EXTENSION}.xpi ${D}${libdir}/firefox/extensions/${EXTENSION}.xpi
}

FILES_${PN} += "${libdir}/firefox"
