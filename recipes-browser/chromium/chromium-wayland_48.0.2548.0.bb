CHROMIUM_ENABLE_WAYLAND = "1"

include chromium-browser.inc

DEPENDS += "wayland libxkbcommon"

SRC_URI += "\
        file://chromium-wayland/add_missing_stat_h_include.patch \
        file://0003-Remove-hard-coded-values-for-CC-and-CXX.patch \
        file://0004-Create-empty-i18n_process_css_test.html-file-to-avoi.patch \
        file://0005-Override-root-filesystem-access-restriction.patch \
	file://chromium-wayland/0007-Workaround-for-glib-related-build-error-with-ozone-w.patch \
        file://chromium-wayland/0011-Replace-readdir_r-with-readdir.patch \
        file://chromium-wayland/remove-Werror.patch \
        ${@bb.utils.contains('PACKAGECONFIG', 'component-build', 'file://component-build.gypi', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'ignore-lost-context', 'file://0001-Remove-accelerated-Canvas-support-from-blacklist.patch', '', d)} \
"

LIC_FILES_CHKSUM = "file://LICENSE;md5=0fca02217a5d49a14dfe2d11837bb34d"
SRC_URI[md5sum] = "0534981cc21efcd11e64b67b85854420"
SRC_URI[sha256sum] = "4ca4e2adb340b3fb4d502266ad7d6bda45fa3519906dbf63cce11a63f680dbc8"

OZONE_WAYLAND_GIT_BRANCH = "Milestone-SouthSister"
OZONE_WAYLAND_GIT_SRCREV = "c605505044af3345a276abbd7c29fd53db1dea40"

SRC_URI += "git://github.com/01org/ozone-wayland.git;destsuffix=${OZONE_WAYLAND_GIT_DESTSUFFIX};branch=${OZONE_WAYLAND_GIT_BRANCH};rev=${OZONE_WAYLAND_GIT_SRCREV} file://chromium-wayland/0006-Remove-GBM-support-from-wayland.gyp.patch"

# Component build is unsupported in ozone-wayland for Chromium 48
python() {
    if not bb.utils.contains('DISTRO_FEATURES', 'wayland', True, False, d):
        raise bb.parse.SkipPackage("Wayland is not available")
    if bb.utils.contains('PACKAGECONFIG', 'component-build', True, False, d):
        bb.fatal("Chromium 48 Wayland version cannot be built in component-mode")
}

CHROMIUM_WAYLAND_GYP_DEFINES = "use_ash=1 use_aura=1 chromeos=0 use_ozone=1 use_xkbcommon=1"
