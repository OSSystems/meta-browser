# Copyright (C) 2009, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

require firefox-addon.inc

PR = "${INC_PR}.1"

SRC_URI = "http://ftp.mozilla.org/pub/mozilla.org/addons/1568/full_fullscreen-${PV}-fx.xpi"
EXTENSION = "{bfe3406c-6f31-4789-86d5-efa50e12c9eb}"

SRC_URI[md5sum] = "a4c113f29e1076a7c04b8f442012d30a"
SRC_URI[sha256sum] = "f3e86fa152a347accf6fe42571a59d5d327476f9233b04d890b77b8e788ce67d"
