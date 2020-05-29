require chromium-gn.inc

REQUIRED_DISTRO_FEATURES = "wayland"

DEPENDS += "\
        libxkbcommon \
        virtual/egl \
        wayland \
        wayland-native \
"

GN_ARGS += "\
        ${PACKAGECONFIG_CONFARGS} \
        use_ozone=true \
        ozone_auto_platforms=false \
        ozone_platform_headless=true \
        ozone_platform_wayland=true \
        ozone_platform_x11=false \
        system_wayland_scanner_path="${STAGING_BINDIR_NATIVE}/wayland-scanner" \
        use_xkbcommon=true \
        use_system_libwayland=true \
        use_system_minigbm=true \
        use_system_libdrm=true \
        use_gtk=false \
"

# The chromium binary must always be started with those arguments.
CHROMIUM_EXTRA_ARGS_append = " --ozone-platform=wayland"
