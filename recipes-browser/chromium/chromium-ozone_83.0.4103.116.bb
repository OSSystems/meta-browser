require chromium-gn.inc

DEPENDS += "\
        at-spi2-atk \
        libxkbcommon \
        virtual/egl \
        ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland wayland-native', '', d)} \
        ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libx11 libxcomposite libxcursor libxdamage libxext libxfixes libxi libxrandr libxrender libxscrnsaver libxtst', '', d)} \
"

RRECOMMENDS_${PN} += " \
        ttf-bitstream-vera \
"

WAYLAND_GN_ARGS = " \
        ozone_platform_wayland=true \
        system_wayland_scanner_path="${STAGING_BINDIR_NATIVE}/wayland-scanner" \
        use_system_libwayland=true \
"

X11_GN_ARGS = " \
        ozone_platform_x11=true \
"

GN_ARGS += "\
        ${PACKAGECONFIG_CONFARGS} \
        use_ozone=true \
        ozone_auto_platforms=false \
        ozone_platform_headless=true \
        use_xkbcommon=true \
        use_system_minigbm=true \
        use_system_libdrm=true \
        use_gtk=false \
        ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', '${WAYLAND_GN_ARGS}', '', d)} \
        ${@bb.utils.contains('DISTRO_FEATURES', 'x11', '${X11_GN_ARGS}', '', d)} \
"
