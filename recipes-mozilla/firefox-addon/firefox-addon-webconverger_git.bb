# Copyright (C) 2009, 2012, 2014, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

require firefox-addon.inc

DEPENDS += "zip-native"

PV = "20120228"

S = "${WORKDIR}/git"

SRCREV = "0706c0cf5693e137e1fe64ce3f635d311cb8b063"
SRC_URI = "git://github.com/Webconverger/iceweasel-webconverger.git;protocol=git \
           file://unbind-f10-quit.patch \
           file://disable-C-l.patch"

do_configure_prepend() {
    oe_runmake webconverger
}

do_compile() {
    :
}
