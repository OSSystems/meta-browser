require chromium-gn.inc

REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS += "\
        libx11 \
        libxcomposite \
        libxcursor \
        libxdamage \
        libxext \
        libxfixes \
        libxi \
        libxrandr \
        libxrender \
        libxscrnsaver \
        libxtst \
"

# Ozone/X11 has been set to default via finch since M93. Thus,
# no longer disable ozone on X11 builds and let Chromium use
# by default as non-Ozone/X11 path was deprecated. And even
# though it is still possible to choose non-Ozone/X11 path by
# passing --disable-features=UseOzonePlatform in M94, it is
# impossible to disable Ozone since M95 as the non-Ozone path
# has been completely deprecated
# TODO(msisov): update the above comment once M95 is released.
GN_ARGS += "\
        ozone_auto_platforms=false \
        ozone_platform_x11=true \
"

# Compatibility glue while we have both chromium-x11 and
# chromium-ozone-wayland recipes, and the former used to be called just
# "chromium".
PROVIDES = "chromium"
