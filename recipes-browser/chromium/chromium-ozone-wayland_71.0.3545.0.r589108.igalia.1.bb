require chromium-ozone-wayland-tarball.inc
require chromium-gn.inc

SRC_URI += " \
 file://0001-Fix-GCC-build-remove-constexpr-for-non-resolved-comp.patch \
 file://0001-OmniboxTextView-fix-gcc-error-for-structure-initiali.patch \
 file://0001-ScrollPaintPropertyNode-Rename-SnapContainerData-to-.patch \
 file://0001-Fix-a-GCC-error-about-undeclared-std-unique_ptr.patch \
 file://0001-ozone-wayland-Fix-fpermissive-problem-for-GCC.patch \
 file://oe-clang-fixes.patch \
 file://aarch64-skia-build-fix.patch \
 file://0001-Exclude-CRC32-define-for-the-Renesas-M3-board.patch \
 file://libdrm-fixes.patch \
"

REQUIRED_DISTRO_FEATURES = "wayland"

DEPENDS += "\
        libxkbcommon \
        virtual/egl \
        wayland \
"

GN_ARGS += "\
        use_ozone=true \
        ozone_auto_platforms=false \
        ozone_platform_headless=true \
        ozone_platform_wayland=true \
        ozone_platform_x11=false \
        use_xkbcommon=true \
        use_system_libwayland=true \
        use_system_minigbm=true \
"

# The chromium binary must always be started with those arguments.
CHROMIUM_EXTRA_ARGS_append = " --ozone-platform=wayland --in-process-gpu"

# http://errors.yoctoproject.org/Errors/Details/186958/
EXCLUDE_FROM_WORLD_libc-musl = "1"
