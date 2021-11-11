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

# Ozone is default path on Linux since M95.
# Disable all backends except x11 to ensure this is
# x11 only recipe.
GN_ARGS += "\
        ozone_auto_platforms=false \
        ozone_platform_x11=true \
"

# Compatibility glue while we have both chromium-x11 and
# chromium-wayland recipes, and the former used to be called just
# "chromium".
PROVIDES = "chromium"
