require chromium-gn.inc

DEPENDS += " \
        virtual/libgles3 \
        virtual/libgles2 \
        virtual/egl \
        libdrm \
        virtual/libgbm \
"

GN_ARGS += "\
        ${PACKAGECONFIG_CONFARGS} \
        use_ozone=true \
        ozone_auto_platforms=false \
        ozone_platform_headless=true \
        ozone_platform_drm=true \
        use_xkbcommon=true \
        use_system_minigbm=true \
        use_system_libffi=true \
        use_gtk=false \
"

# The chromium binary must always be started with those arguments.
CHROMIUM_EXTRA_ARGS:append = " --ozone-platform=drm"
