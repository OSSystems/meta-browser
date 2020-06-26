require chromium-gn.inc

DEPENDS += "\
        atk \
        at-spi2-atk \
        freetype \
        libxkbcommon \
        virtual/egl \
"

RRECOMMENDS_${PN} += " \
        ttf-bitstream-vera \
"

# Chromium can use v4l2 device for hardware accelerated video decoding. Make sure that
# /dev/video-dec exists.
PACKAGECONFIG[use-linux-v4l2] = "use_v4l2_codec=true use_v4lplugin=true use_linux_v4l2_only=true"

GN_ARGS += "\
        ${PACKAGECONFIG_CONFARGS} \
        use_ozone=true \
        ozone_auto_platforms=false \
        ozone_platform_gbm=false \
        ozone_platform_headless=true \
        ozone_platform_wayland=false \
        ozone_platform_x11=false \
        use_xkbcommon=true \
        use_system_minigbm=false \
        use_system_libdrm=false \
        use_gtk=false \
        is_desktop_linux=true \
        use_glib=true \
"

# The chromium binary must always be started with those arguments.
CHROMIUM_EXTRA_ARGS_append = " --ozone-platform=headless "
