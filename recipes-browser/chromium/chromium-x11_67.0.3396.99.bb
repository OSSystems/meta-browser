require chromium-upstream-tarball.inc
require chromium-gn.inc

SRC_URI += "\
        file://0001-aarch64-Use-xzr-instead-of-x31-in-the-ASM-code.patch \
        file://0001-Ensure-all-targets-build-when-target_arch-arm-and-target_os-linux.patch \
        file://0001-Fix-operator-bool-in-AssociatedInterfacePtrInfo-and-.patch \
        file://aarch64-skia-build-fix.patch \
        file://cross-build-pkgconfig.patch \
"

BUILD_CPPFLAGS += "-isystem${STAGING_INCDIR_NATIVE}/glib-2.0 \
                   -isystem${STAGING_LIBDIR_NATIVE}/glib-2.0/include \
                   -isystem${STAGING_INCDIR_NATIVE}/nss3 \
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
