# Copyright (C) 2009, 2012, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

require firefox-addon.inc

PR = "${INC_PR}.3"

PV = "20120215.1"

S = "${WORKDIR}/git"

SRCREV = "49fa6bfbb955b5a9106d6430efed857c4ea08c4d"
SRC_URI = "git://github.com/Webconverger/iceweasel-webconverger.git;protocol=git \
           file://unbind-f10-quit.patch"

do_configure_prepend() {
    oe_runmake webconverger
}
