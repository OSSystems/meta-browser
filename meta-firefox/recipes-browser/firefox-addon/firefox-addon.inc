# Copyright (C) 2009-2014, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DEPENDS = "mozilla-devscripts-native"
RDEPENDS_${PN} = "firefox"

LICENSE = "MPLv1 | GPLv2+ | LGPLv2.1+"

# Ugly hack for now; we need to check license per recipe later
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

S = "${WORKDIR}"

PR = "r5"

inherit allarch

do_configure() {
    i=0
    for e in `find -iname '*.xpi'`; do
        xpi-unpack $e xpi$i
        i=`expr $i + 1`
    done
}

do_install() {
    for e in `ls -d xpi[0-9]`; do
        EXTENSION=`sed --posix '/em:id=/!d;s/[ ]*em:id="//;s/".*//' $e/install.rdf`
        [ -z "$EXTENSION" ] && exit 1

        mkdir -p ${D}${libdir}/firefox/browser/extensions/
        cp -R --no-dereference --preserve=mode,links -v $e ${D}${libdir}/firefox/browser/extensions/${EXTENSION}
    done
}

FILES_${PN} += "${libdir}/firefox"
