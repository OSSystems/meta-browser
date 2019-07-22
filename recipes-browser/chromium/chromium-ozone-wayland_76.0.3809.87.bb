require chromium-gn.inc

SRC_URI += " \
        file://0001-ozone-wayland-Fix-method-prototype-match.patch \
        file://V4L2/0001-Add-support-for-V4L2VDA-on-Linux.patch \
        file://V4L2/0002-Add-mmap-via-libv4l-to-generic_v4l2_device.patch \
"

REQUIRED_DISTRO_FEATURES = "wayland"

DEPENDS += "\
        libxkbcommon \
        virtual/egl \
        wayland \
        wayland-native \
"

# Chromium can use v4l2 device for hardware accelerated video decoding. Make sure that
# /dev/video-dec exists.
PACKAGECONFIG[use-linux-v4l2] = "use_v4l2_codec=true use_v4lplugin=true use_linux_v4l2_only=true"

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
"

# The chromium binary must always be started with those arguments.
CHROMIUM_EXTRA_ARGS_append = " --ozone-platform=wayland"
