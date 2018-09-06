require chromium-upstream-tarball.inc
require chromium-gn.inc

SRC_URI += "\
        file://0001-CORS-legacy-add-missing-string-include.patch \
        file://0001-GCC-do-not-std-move-unique-ptr-of-forward-declared-U.patch \
        file://0001-GCC-include-stddef-for-size_t-definition.patch \
        file://0001-GCC-workaround-GCC-bug-std-map-insert-value_type-not.patch \
        file://0001-aarch64-Use-xzr-instead-of-x31-in-the-ASM-code.patch \
        file://0001-skia-Build-skcms-with-mfp16-format-ieee-on-GCC-ARM-b.patch \
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
