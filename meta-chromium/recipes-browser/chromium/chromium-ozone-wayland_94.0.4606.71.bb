require chromium-gn.inc

REQUIRED_DISTRO_FEATURES = "wayland"

DEPENDS += "\
        at-spi2-atk \
        libxkbcommon \
        virtual/egl \
        wayland \
        wayland-native \
"

SRC_URI += "\
        file://chromium-ozone-wayland/0001-ozone-add-va-api-support-to-wayland.patch \
"

GN_ARGS += "\
        ${PACKAGECONFIG_CONFARGS} \
        use_ozone=true \
        ozone_auto_platforms=false \
        ozone_platform_headless=true \
        ozone_platform_wayland=true \
        ozone_platform_x11=false \
        system_wayland_scanner_path="${STAGING_BINDIR_NATIVE}/wayland-scanner" \
        use_system_wayland_scanner=true \
        use_xkbcommon=true \
        use_system_libwayland=true \
        use_system_minigbm=true \
        use_system_libdrm=true \
"

# Ozone is default via finch since M94 and default via compile time
# changes since M95. However, use_x11 still must be disable to provide
# a pure Wayland build. Since M95, use_x11 is controlled via
# ozone_platform_x11 and won't be needed to be altered here, but rather
# through the mentioned ozone_platform_x11 arg.
# TODO(msisov): remove this once recipe is updated to M95.
GN_ARGS += "use_x11=false"

# The chromium binary must always be started with those arguments.
CHROMIUM_EXTRA_ARGS:append = " --ozone-platform=wayland"

PACKAGECONFIG ??= "upower use-egl"
