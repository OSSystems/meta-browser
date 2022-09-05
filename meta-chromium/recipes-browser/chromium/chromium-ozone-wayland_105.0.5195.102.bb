require chromium-gn.inc

REQUIRED_DISTRO_FEATURES = "wayland"

DEPENDS += "\
        at-spi2-atk \
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
        use_system_wayland_scanner=true \
        use_xkbcommon=true \
        use_system_libwayland=true \
        use_system_minigbm=true \
        use_system_libdrm=true \
        use_gtk=false \
"

# The chromium binary must always be started with those arguments.
CHROMIUM_EXTRA_ARGS:append = " --ozone-platform=wayland"

# Angle is not used by Wayland yet, but it was still built. It wasn't a problem
# until wayland-protocols were updated to 1.20 in newest yocto releases, which
# changed the implementation of the wayland-scanner tool. That tool uses a new
# API, which is not part of older wayland-protocols. And given ANGLE includes
# libwayland headers from //third_party/wayland instead of relaying on the
# system ones, that results in undeclared methods.
# TODO(msisov): remove this once Chromium's //third_party/wayland is updated
# to >=1.20. This tracks https://crbug.com/1359189
GN_ARGS += "\
        angle_use_wayland=false \
"
