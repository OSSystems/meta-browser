include chromium-browser.inc

SRC_URI += "\
        file://unistd-2.patch \
        file://chromium-40/fix-build-error-with-GCC-in-Debug-mode.patch \
        file://chromium-40/add_missing_stat_h_include.patch \
        file://chromium-40/0001-bignum.cc-disable-warning-from-gcc-5.patch \
        file://chromium-40/0002-image_util.cc-disable-warning-from-gcc-5.patch \
        file://chromium-40/0003-disable-uninitialized-warning.patch \
        file://chromium-40/0004-Remove-hard-coded-values-for-CC-and-CXX.patch \
        ${@bb.utils.contains('PACKAGECONFIG', 'ignore-lost-context', 'file://chromium-40/0001-Remove-accelerated-Canvas-support-from-blacklist.patch', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'impl-side-painting', 'file://chromium-40/0002-Add-Linux-to-impl-side-painting-whitelist.patch', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'disable-api-keys-info-bar', 'file://chromium-40/0003-Disable-API-keys-info-bar.patch', '', d)} \
"

LIC_FILES_CHKSUM = "file://LICENSE;md5=537e0b52077bf0a616d0a0c8a79bc9d5"
SRC_URI[md5sum] = "1f5093bd7e435fdebad070e74bfb3438"
SRC_URI[sha256sum] = "f72fda9ff1ea256ab911610ee532eadf8303137d431f2481d01d3d60e5e64149"

OZONE_WAYLAND_GIT_BRANCH = "Milestone-ThanksGiving"
OZONE_WAYLAND_GIT_SRCREV = "5d7baa9bc3b8c88e9b7e476e3d6bc8cd44a887fe"

OZONE_WAYLAND_EXTRA_PATCHES = " \
        file://chromium-40/0005-Remove-X-libraries-from-GYP-files.patch \
        file://chromium-40/0010-systemd-218.patch \
"
# using 00*.patch to skip the WebRTC patches in ozone-wayland ThanksGiving milestone.
# The WebRTC patches remove X11 libraries from the linker flags, which is
# already done by another patch (see above). Furthermore, to be able to use
# these patches, it is necessary to update the git repository in third_party/webrtc,
# which would further complicate this recipe.
OZONE_WAYLAND_PATCH_FILE_GLOB = "00*.patch"

# Component build is broken in ozone-wayland for Chromium 40,
# and is not planned to work again before version 41
python() {
    if (d.getVar('CHROMIUM_ENABLE_WAYLAND', True) == '1'):
        if bb.utils.contains('PACKAGECONFIG', 'component-build', True, False, d):
            bb.fatal("Chromium 40 Wayland version cannot be built in component-mode")
}

CHROMIUM_X11_DEPENDS = "xextproto gtk+ libxi libxss"
CHROMIUM_X11_GYP_DEFINES = ""
CHROMIUM_WAYLAND_DEPENDS = "wayland libxkbcommon"
CHROMIUM_WAYLAND_GYP_DEFINES = "use_ash=1 use_aura=1 chromeos=0 use_ozone=1"
