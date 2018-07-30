require chromium-ozone-wayland-tarball.inc
require chromium-gn.inc

SRC_URI += " \
  file://v8-qemu-wrapper-tools.patch \
  file://v8-v7.0.85-qemu-wrapper.patch;patchdir=v8 \
  file://0001-Fix-internal-compiler-error-in-convert_move.patch;patchdir=third_party/skia \
  file://0001-IME-for-Platform-integration-remove-chromeos-deps.patch \
  file://0002-IME-for-Wayland-Ozone-Wayland-IME-integration.patch \
  file://0001-Use-explicitly-defined-move-constructor-with-noexcep.patch \
  file://0001-Add-memory-header-when-unique_ptr-is-used.patch \
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
        use_wayland_gbm=false \
        use_system_libwayland=true \
"

# The chromium binary must always be started with those arguments.
CHROMIUM_EXTRA_ARGS_append = " --in-process-gpu --ozone-platform=wayland"
