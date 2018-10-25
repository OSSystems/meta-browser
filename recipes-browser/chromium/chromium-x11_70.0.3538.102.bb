require chromium-upstream-tarball.inc
require chromium-gn.inc

SRC_URI += "\
        file://0001-Fix-build-error-for-blink.patch \
        file://0001-IWYU-stdint.h-in-pdfium_mem_buffer_file_write.h-for-.patch \
        file://0001-OmniboxTextView-fix-gcc-error-for-structure-initiali.patch \
        file://0001-ScrollPaintPropertyNode-Rename-SnapContainerData-to-.patch \
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
