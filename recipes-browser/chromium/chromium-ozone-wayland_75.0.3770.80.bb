require chromium-upstream-tarball.inc
require chromium-gn.inc

SRC_URI += " \
        file://0001-ozone-wayland-Factored-the-clipboard-logic-out-of-Wa.patch \
        file://0002-Convert-wayland-buffer-to-the-new-shared-memory-API.patch \
        file://0003-Migrate-WaylandCanvasSurface-to-the-new-shared-memor.patch \
        file://0004-ozone-wayland-Ease-the-buffer-swap-and-maintenance.patch \
        file://0005-ozone-wayland-Fix-presentation-feedback-flags.patch \
        file://0006-wayland-Do-not-release-shared-memory-fd-when-passing.patch \
        file://0007-ozone-wayland-Don-t-wait-for-frame-callback-after-su.patch \
        file://0008-ozone-wayland-Do-not-add-window-if-manager-does-not-.patch \ 
        file://0009-ozone-wayland-Fix-NativeGpuMemoryBuffers-usage.patch \
        file://0010-ozone-wayland-Add-immediate-release-support.patch \
        file://0011-ozone-wayland-Wrap-wl_shm-to-WaylandShm.patch \
        file://0012-ozone-wayland-Shm-Proxy-make-mojo-calls-on-the-gpu-t.patch \
        file://0013-ozone-wayland-Shm-add-buffer_id.patch \
        file://0014-ozone-wayland-Unite-Wayland-ShmBufferManager-and-Buf.patch \
        file://0015-ozone-wayland-Stop-providing-WaylandConnection-throu.patch \
        file://0016-ozone-wayland-Improve-logging-when-creating-gbm-buff.patch \
        file://0017-ozone-wayland-Establish-BufferManagerGpu-and-BufferM.patch \
        file://0018-ozone-wayland-Use-new-shmen-API-when-loading-keymap.patch \
        file://0019-ozone-wayland-Prepare-WaylandCanvasSurface-for-compl.patch \
        file://0020-ozone-wayland-Reset-surface-contents-in-a-safe-way.patch \
        file://0021-Ozone-Wayland-Manager-make-mojo-calls-on-IO-thread.patch \
        file://0022-ozone-wayland-Manager-tests-exercise-tests-with-mojo.patch \
        file://0023-ozone-wayland-Fix-broken-software-rendering-path.patch \
        file://0001-v4l2_device-CanCreateEGLImageFrom-support-all-ARM-So.patch \
        file://0001-Add-support-for-V4L2VDA-on-Linux.patch \
        file://0002-Add-mmap-via-libv4l-to-generic_v4l2_device.patch \
        file://0001-ozone-wayland-Fix-method-prototype-match.patch \
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
