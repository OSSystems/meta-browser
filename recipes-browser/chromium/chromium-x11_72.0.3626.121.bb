require chromium-upstream-tarball.inc
require chromium-gn.inc

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI += " \
        file://0001-IWYU-Include-string.h-for-memset.patch \
"

DEPENDS += "\
        libx11 \
        libxcomposite \
        libxcursor \
        libxdamage \
        libxext \
        libxfixes \
        libxi \
        libxrandr \
        libxrender \
        libxscrnsaver \
        libxtst \
"

# Compatibility glue while we have both chromium-x11 and
# chromium-ozone-wayland recipes, and the former used to be called just
# "chromium".
PROVIDES = "chromium"
