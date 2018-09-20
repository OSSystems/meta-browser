require chromium-upstream-tarball.inc
require chromium-gn.inc

SRC_URI += "\
        file://0001-vpx_sum_squares_2d_i16_neon-Make-s2-a-uint64x1_t.patch \
        file://aarch64-skia-build-fix.patch \
        file://oe-clang-fixes.patch \
"

REQUIRED_DISTRO_FEATURES = "x11"

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

# http://errors.yoctoproject.org/Errors/Details/186969/
EXCLUDE_FROM_WORLD_libc-musl = "1"
