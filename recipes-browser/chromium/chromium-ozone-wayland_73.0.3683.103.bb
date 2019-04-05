require chromium-upstream-tarball.inc
require chromium-gn.inc

SRC_URI += " \
        file://0001-minigbm-add-MINIGBM-define.patch \
        file://0001-ozone-wayland-Use-nearest-to-origin-display-as-a-pri.patch \
        file://0002-ozone-wayland-Implement-GetDisplayMatching.patch \
        file://0003-ozone-wayland-Use-surface-listener-to-get-display-fo.patch \
        file://0004-ozone-Implement-Clipboard-for-Ozone-platforms.patch \
        file://0005-ozone-wayland-Implement-GetCursorScreenPoint.patch \
        file://0006-ozone-wayland-Fix-default-window-bounds.patch \
        file://0007-Add-support-for-V4L2VDA-on-Linux.patch \
        file://0008-Add-mmap-via-libv4l-to-generic_v4l2_device.patch \
        file://0009-Move-CharacterComposer-into-ui-base-ime.patch \
        file://0010-ozone-wayland-Support-dead-keys.patch \
        file://0011-ozone-wayland-Pass-0-modifier-on-DRM_FORMAT_MOD_INVA.patch \
        file://0012-ozone-wayland-The-fuzzer-for-wayland-buffers-is-intr.patch \
        file://0013-ozone-wayland-Remove-WaylandXkbKeyboardLayoutEngine.patch \
        file://0014-ozone-wayland-Fix-wl_pointer-enter-event-handling.patch \
        file://0015-ozone-wayland-Handle-preedit-string-in-WaylandInputM.patch \
        file://0016-ozone-wayland-Use-gbm-with-in-process-gpu-mode.patch \
        file://0017-ozone-wayland-Make-buffer-manager-resistant-to-event.patch \
        file://0018-ozone-wayland-Reuse-tooltip-subsurfaces.patch \
        file://0019-ozone-Implement-receiving-dragging.patch \
        file://0020-ozone-wayland-Fix-includes-and-deps.patch \
        file://0021-ozone-wayland-xkbcommon-Fix-layout-switching.patch \
        file://0022-ozone-wayland-Fix-crash-when-there-is-no-zwp_linux_d.patch \
        file://0023-Remove-dependency-cycle-between-ui-base-ime-and-ui-o.patch \
        file://0024-Reland-minigbm-define-GBM_BO_IMPORT_FD_MODIFIER.patch \
        file://0025-Reland-ozone-common-Make-gbm_wrapper-to-be-compiled-.patch \
        file://0026-ozone-wayland-Fix-software-rendering.patch \
        file://0027-ozone-wayland-Return-primary-display-on-null-window.patch \
        file://0028-ozone-Implement-single-window-tab-dragging.patch \
        file://0029-ozone-wayland-Fix-software-rendering.patch \
        file://0030-ozone-wayland-Improve-DataDevice-initialization.patch \
        file://0031-ozone-wayland-Fixed-wrong-behaviour-on-maximize-rest.patch \
        file://0032-ozone-wayland-gardening-move-method-definition-below.patch \
        file://0033-ozone-wayland-rely-on-implicit-sync-for-Broadcom-and.patch \
        file://0034-ozone-wayland-Fixed-buffer-handling-in-the-WaylandCu.patch \
        file://0035-ozone-wayland-Let-Wayland-compositor-decide-how-to-p.patch \
        file://0036-Fix-the-bug-that-Launcher-is-triggered-unexpectedly.patch \
        file://0037-ozone-Do-not-add-display-origin-to-bounds-in-pixels.patch \
        file://0038-ozone-Fix-ScreenOzone-GetWindowAtScreenPoint.patch \
        file://0039-ozone-wayland-Fix-NEO-keyboards-layout-handling.patch \
        file://0040-ozone-wayland-Support-NumLock-in-non-chromeos-builds.patch \
        file://0041-ozone-xkbcommon-Pre-compute-masks-when-setting-keyma.patch \
        file://0042-ozone-wayland-Use-opaque-region-for-opaque-windows.patch \
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
# The VizDisplayCompositor is temporarely disabled.
# See https://crbug.com/c/943096
CHROMIUM_EXTRA_ARGS_append = " --disable-features=VizDisplayCompositor --ozone-platform=wayland"
