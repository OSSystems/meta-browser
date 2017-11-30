require chromium-ozone-wayland-tarball.inc
require chromium-gn.inc

DEPENDS += "\
 ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'virtual/egl wayland libxkbcommon', '', d)} \
 ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libx11 libxcomposite libxcursor libxdamage libxext libxfixes libxi libxrandr libxrender libxscrnsaver libxtst', '', d)} \
"

GN_ARGS += "\
 enable_mus=true \
 use_ozone=true \
 ozone_auto_platforms=false \
 ozone_platform_headless=true \
 ozone_platform_wayland=${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'true', 'false', d)} \
 ozone_platform_x11=${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'true', 'false', d)} \
 use_xkbcommon=true \
 use_jumbo_build = true \
"
