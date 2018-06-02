require chromium-upstream-tarball.inc
require chromium-gn.inc

SRC_URI += "\
        file://0001-aarch64-Use-xzr-instead-of-x31-in-the-ASM-code.patch \
        file://0001-Ensure-all-targets-build-when-target_arch-arm-and-target_os-linux.patch \
        file://aarch64-skia-build-fix.patch \
"

REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS += "\
        gtk+3 \
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
