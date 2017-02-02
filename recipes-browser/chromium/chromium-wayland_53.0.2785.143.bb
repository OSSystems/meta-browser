CHROMIUM_ENABLE_WAYLAND = "1"

include chromium-browser.inc

DEPENDS += "wayland libxkbcommon"

SRC_URI += "\
        file://chromium-wayland/add_missing_stat_h_include.patch \
        file://0004-Create-empty-i18n_process_css_test.html-file-to-avoi.patch \
        file://0005-Override-root-filesystem-access-restriction.patch \
        file://chromium-wayland/Remove-hard-coded-values-for-CC-and-CXX.patch \
	file://chromium-wayland/0007-Workaround-for-glib-related-build-error-with-ozone-w.patch \
        file://chromium-wayland/0011-Replace-readdir_r-with-readdir.patch \
        file://chromium-wayland/remove-Werror.patch \
        file://chromium-wayland/guard-x11_desktop_handler-inclusion.patch \
        file://chromium-wayland/fix-non-x11-build-when-use_xkbcommon-1.patch \
        ${@bb.utils.contains('PACKAGECONFIG', 'component-build', 'file://component-build.gypi', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'ignore-lost-context', 'file://0001-Remove-accelerated-Canvas-support-from-blacklist.patch', '', d)} \
"

LIC_FILES_CHKSUM = "file://LICENSE;md5=0fca02217a5d49a14dfe2d11837bb34d"
SRC_URI[md5sum] = "fdc737af242421b2a9a0bb84f6b17040"
SRC_URI[sha256sum] = "c52a58b79bfb27bb87e4a0a6ff213001485fbc747657b290f75d39ddce07dcc3"

OZONE_WAYLAND_GIT_BRANCH = "rebase_m53"
OZONE_WAYLAND_GIT_SRCREV = "1a5e7982bf7b8743c20a20e5aa33d9d6a42d48d6"

SRC_URI += "git://github.com/01org/ozone-wayland.git;destsuffix=${OZONE_WAYLAND_GIT_DESTSUFFIX};branch=${OZONE_WAYLAND_GIT_BRANCH};rev=${OZONE_WAYLAND_GIT_SRCREV} file://chromium-wayland/0006-Remove-GBM-support-from-wayland.gyp.patch"

# Component build is unsupported in ozone-wayland for Chromium 53
python() {
    if not bb.utils.contains('DISTRO_FEATURES', 'wayland', True, False, d):
        raise bb.parse.SkipPackage("Wayland is not available")
    if bb.utils.contains('PACKAGECONFIG', 'component-build', True, False, d):
        bb.fatal("Chromium 53 Wayland version cannot be built in component-mode")
}

CHROMIUM_WAYLAND_GYP_DEFINES = "use_ash=0 use_aura=1 chromeos=0 use_ozone=1 use_xkbcommon=1 ozone_auto_platforms=0 ozone_platform_wayland=1"
