require electron-gn.inc

RPROVIDES:${PN} = "electron"

REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS += "\
        libnotify \
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
        at-spi2-atk \
        virtual/egl \
        libffi \
        gtk+3 \
"

# Loaded at runtime.
# https://source.chromium.org/chromium/chromium/src/+/main:ui/gfx/x/xlib_support.cc
RDEPENDS:${PN} += "libx11-xcb"

GN_ARGS += "\
        ${PACKAGECONFIG_CONFARGS} \
        use_ozone=true \
        ozone_auto_platforms=false \
        ozone_platform_headless=true \
        ozone_platform_wayland=false \
        ozone_platform_x11=true \
        use_xkbcommon=true \
        use_system_minigbm=true \
        use_system_libffi=true \
        use_bundled_fontconfig=false \
        override_electron_version="${PV}" \
        import("//electron/build/args/release.gn") \
"

# The electron binary must always be started with those arguments.
CHROMIUM_EXTRA_ARGS:append = " --ozone-platform=x11"

# Compatibility glue while we have both electron-ozone-x11 and
# electron-ozone-wayland recipes.
PROVIDES = "electron"
