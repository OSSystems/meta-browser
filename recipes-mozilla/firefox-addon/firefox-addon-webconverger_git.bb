# Copyright (C) 2009, 2012, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

require firefox-addon.inc

PR = "${INC_PR}.0"

PV = "20120215"

S = "${WORKDIR}/git"

SRCREV = "80879d1448a18122ef681bae8988c90329814ba3"
SRC_URI = "git://github.com/Webconverger/iceweasel-webconverger.git;protocol=git"

do_configure_prepend() {
    oe_runmake webcfullscreen
}
