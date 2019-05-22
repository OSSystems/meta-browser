require chromium-upstream-tarball.inc
require chromium-gn.inc

SRC_URI += " \
        file://0001-ozone-wayland-add-CreateSurfaceCheckGbm-test.patch \
        file://0002-ozone-wayland-Fix-flaky-DataDevice-unit-tests.patch \
        file://0003-ozone-wayland-Use-opaque-region-for-opaque-windows.patch \
        file://0004-ozone-wayland-Clean-up-TestDataSource.patch \
        file://0005-ozone-wayland-Add-const-keyword-to-getters.patch \
        file://0006-ozone-wayland-Clean-up-data-device-related-code.patch \
        file://0007-ozone-wayland-Fix-Drag-Drop-crash-when-dragging-with.patch \
        file://0008-ozone-wayland-Fix-cursor-regression-when-running-in-.patch \
        file://0009-ozone-wayland-Clean-up-WaylandCursor.patch \
        file://0010-Make-minigbm-users-use-non-deprecated-gbm-apis.patch \
        file://0011-ozone-wayland-Fix-crash-when-trying-to-read-clipboar.patch \
        file://0012-ozone-wayland-Fix-drag-icon-handling.patch \
        file://0013-ozone-Include-memory-and-utility-where-necessary.patch \
        file://0014-ozone-wayland-Change-the-order-when-the-opaque-regio.patch \
        file://0015-ozone-wayland-Factor-out-zwp-linux-dmabuf-from-the-m.patch \
        file://0016-ozone-wayland-Introduce-WaylandShmBuffer-class.patch \
        file://0017-ozone-wayland-Handle-viz-process-restart.patch \
        file://0018-ozone-wayland-Move-the-host-gpu-common-and-test-code.patch \
        file://0019-ozone-wayland-Drop-dependency-wayland-ui-display-man.patch \
        file://0020-ozone-wayland-Separate-swap-buffer-and-presentation-.patch \
        file://0021-ozone-wayland-Add-possibility-to-create-wl-buffer-im.patch \
        file://0022-ozone-wayland-Factored-the-clipboard-logic-out-of-Wa.patch \
        file://0023-Convert-wayland-buffer-to-the-new-shared-memory-API.patch \
        file://0024-Migrate-WaylandCanvasSurface-to-the-new-shared-memor.patch \
        file://0025-ozone-wayland-Ease-the-buffer-swap-and-maintenance.patch \
        file://0026-ozone-wayland-Fix-presentation-feedback-flags.patch \
        file://0027-wayland-Do-not-release-shared-memory-fd-when-passing.patch \
        file://0001-ozone-wayland-Don-t-wait-for-frame-callback-after-su.patch \
        file://0001-Add-support-for-V4L2VDA-on-Linux.patch \
        file://0002-Add-mmap-via-libv4l-to-generic_v4l2_device.patch \
        file://0001-ozone-wayland-Do-not-add-window-if-manager-does-not-.patch \
        file://0001-ozone-wayland-Fix-NativeGpuMemoryBuffers-usage.patch \
        file://0001-v4l2_device-Update-CanCreateEGLImageFrom-to-support-.patch \
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
