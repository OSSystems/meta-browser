require chromium-upstream-tarball.inc
require chromium-gn.inc

SRC_URI += " \
        file://0001-Reland-minigbm-define-GBM_BO_IMPORT_FD_MODIFIER.patch \
        file://0001-ozone-wayland-Fix-kiosk-mode.patch \
        file://0002-ozone-common-Make-planar-import-work-with-0.patch \
        file://0003-ozone-common-Fix-IsValidBufferFormat.patch \
        file://0004-Removed-dependencies-on-third_party-libdrm.patch \
        file://0005-ozone-wayland-Clean-up-duplicated-header.patch \
        file://0006-ozone-wayland-Start-using-announced-formats.patch \
        file://0007-Ozone-Implement-starting-dragging-with-ozon.patch \
        file://0008-ozone-wayland-Enable-native-gpu-memory-buff.patch \
        file://0009-Ozone-Add-OzonePlatform-IsNativePixmapConfi.patch \
        file://0010-ozone-move-gbm-wrapper-into-a-separate-sour.patch \
        file://0011-fixup-ozone-move-gbm-wrapper-into-a-separat.patch \
        file://0012-ozone-Rename-ClipboardDelegate-to-PlatformC.patch \
        file://0013-Wayland-wayland-for-tracing.patch \
        file://0014-Whitelist-dmabuf-sync-ioctl-on-all-linux-pl.patch \
        file://0015-Reland-ozone-common-Make-gbm_wrapper-to-be-compiled-.patch \
        file://0016-Add-support-for-V4L2VDA-on-Linux.patch \
        file://0017-Add-mmap-via-libv4l-to-generic_v4l2.patch \
"

REQUIRED_DISTRO_FEATURES = "wayland"

DEPENDS += "\
        libxkbcommon \
        virtual/egl \
        wayland \
"

# Chromium can use v4l2 device for hardware accelerated video decoding. Make sure that
# /dev/video-dec exists.
PACKAGECONFIG[use-linux-v4l2] = "use_v4l2_codec=true use_v4lplugin=true use_linux_v4l2_only=true"

GN_ARGS += "\
        ${PACKAGECONFIG_CONFARGS} \
        use_ozone=true \
        ozone_auto_platforms=false \
        ozone_platform_headless=true \
        ozone_platform_wayland=true \
        ozone_platform_x11=false \
        use_xkbcommon=true \
        use_system_libwayland=true \
        use_system_minigbm=true \
        use_system_libdrm=true \
"

# The chromium binary must always be started with those arguments.
CHROMIUM_EXTRA_ARGS_append = " --ozone-platform=wayland"

# http://errors.yoctoproject.org/Errors/Details/186958/
EXCLUDE_FROM_WORLD_libc-musl = "1"
