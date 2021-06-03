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

# Since M87, Chromium builds Ozone by default, but continues to use X11.
# However, the goal of two recipes - Ozone/Wayland and non-Ozone/X11 is
# to provide either Wayland or X11 and avoid pulling dependencies of each
# on environments where Wayland or X11 is not actually required.
# See https://crrev.com/c/2382834
GN_ARGS += "use_ozone=false"

# Compatibility glue while we have both chromium-x11 and
# chromium-ozone-wayland recipes, and the former used to be called just
# "chromium".
PROVIDES = "chromium"
