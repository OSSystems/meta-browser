require chromium-upstream-tarball.inc
require chromium-gn.inc

SRC_URI += " \
        file://0001-Add-missing-algorithm-header-to-webcursor_ozone.patch \
        file://0001-fixup-ozone-move-gbm-wrapper-into-a-separate-source-.patch \
        file://0002-ozone-Rename-ClipboardDelegate-to-PlatformClipboard.patch \
        file://0003-ozone-wayland-WaylandScreen-return-widget-on-screen-.patch \
        file://0004-ozone-wayland-Switch-to-wl_output-version-2.patch \
        file://0005-ozone-wayland-Use-nearest-to-origin-display-as-a-pri.patch \
        file://0006-ozone-wayland-Fix-event-routing-issue-when-multiple-.patch \
        file://0007-Fix-ime-linux-export-include-and-fix-dtor.patch \
        file://0008-ui-base-move-clipboard-to-own-folder.patch \
        file://0009-ozone-wayland-Implement-GetDisplayMatching.patch \
        file://0010-ozone-wayland-Use-surface-listener-to-get-display-fo.patch \
        file://0011-ozone-Implement-Clipboard-for-Ozone-platforms.patch \
        file://0012-ozone-wayland-Implement-GetCursorScreenPoint.patch \
        file://0013-ozone-wayland-Fix-default-window-bounds.patch \
        file://0014-Reland-ozone-common-Make-gbm_wrapper-to-be-compiled-.patch \
        file://0015-Add-support-for-V4L2VDA-on-Linux.patch \
        file://0016-Add-mmap-via-libv4l-to-generic_v4l2_device.patch \
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
