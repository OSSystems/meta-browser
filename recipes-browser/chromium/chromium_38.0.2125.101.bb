include chromium.inc

LIC_FILES_CHKSUM = "file://LICENSE;md5=537e0b52077bf0a616d0a0c8a79bc9d5"
SRC_URI += "\
        ${@bb.utils.contains('PACKAGECONFIG', 'ignore-lost-context', 'file://chromium-38/0001-Remove-accelerated-Canvas-support-from-blacklist.patch', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'impl-side-painting', 'file://chromium-38/0001-Disable-rasterization-whitelist-unlocking-impl-side-.patch', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'disable-api-keys-info-bar', 'file://chromium-38/0002-Disable-API-keys-info-bar.patch', '', d)} \
        file://chromium-38/0001-Remove-hard-coded-values-for-CC-and-CXX.patch \
        file://unistd-2.patch \
"
# conditionally add shell integration patch (ozone-wayland contains a patch that makes
# this one redundant, therefore use this patch only for X11 builds)
SRC_URI += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'file://chromium-38/0001-shell-integration-conditionally-compile-routines-for.patch', '', d)}"
SRC_URI[md5sum] = "be4d3ad6944e43132e4fbde5a23d1ab8"
SRC_URI[sha256sum] = "d3303519ab471a3bc831e9b366e64dc2fe651894e52ae5d1e74de60ee2a1198a"

OZONE_WAYLAND_EXTRA_PATCHES += " \
        file://chromium-38/0001-Remove-X-libraries-from-GYP-files.patch \
"
OZONE_WAYLAND_GIT_BRANCH = "Milestone-Harvest"
OZONE_WAYLAND_GIT_SRCREV = "0f8b830730d9b696a667c331c50ac6333bb85c13"
# using 00*.patch to skip the WebRTC patches in ozone-wayland
# the WebRTC patches remove X11 libraries from the linker flags, which is
# already done by another patch (see above). Furthermore, to be able to use
# these patches, it is necessary to update the git repository in third_party/webrtc,
# which would further complicate this recipe.
OZONE_WAYLAND_PATCH_FILE_GLOB = "00*.patch"
