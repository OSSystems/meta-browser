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
        file://0001-ozone-add-va-api-support-to-wayland.patch \
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
        use_gtk=false \
"

# Since M87, Chromium builds Ozone by default, but continues to use X11.
# However, the goal of two recipes - Ozone/Wayland and non-Ozone/X11 is
# to provide either Wayland or X11 and avoid pulling dependencies of each
# on environments where Wayland or X11 is not actually required.
# See https://crrev.com/c/2382834
GN_ARGS += "use_x11=false"

# The chromium binary must always be started with those arguments.
CHROMIUM_EXTRA_ARGS_append = " --ozone-platform=wayland"

