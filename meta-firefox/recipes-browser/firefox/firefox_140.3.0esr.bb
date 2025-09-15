# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox_crates_esr.inc
include firefox.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-esr:"

SRC_URI[sha256sum] = "efc6eb3c93756311bd2f9db3796c0bbee6e3f182975d857284168b3dec672316"
