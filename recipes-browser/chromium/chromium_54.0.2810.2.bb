include chromium-browser.inc

DEPENDS += "xextproto gtk+ libxi libxss"

SRC_URI += "\
        file://chromium/add_missing_stat_h_include.patch \
        file://0003-Remove-hard-coded-values-for-CC-and-CXX.patch \
        file://0004-Create-empty-i18n_process_css_test.html-file-to-avoi.patch \
        file://0005-Override-root-filesystem-access-restriction.patch \
        file://chromium/0011-Replace-readdir_r-with-readdir.patch \
        file://chromium/remove-Werror.patch \
        ${@bb.utils.contains('PACKAGECONFIG', 'component-build', 'file://component-build.gypi', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'ignore-lost-context', 'file://chromium/0001-Remove-accelerated-Canvas-support-from-blacklist.patch', '', d)} \
        file://chromium/m32.patch \
"

LIC_FILES_CHKSUM = "file://LICENSE;md5=0fca02217a5d49a14dfe2d11837bb34d"
SRC_URI[md5sum] = "3f596ecbd6a39d5ada29f11780ec6dcf"
SRC_URI[sha256sum] = "f038e72cbd8b7383d13c286329623fda8d6d48f45fa2d964e554b5565283ad71"

# X11 must be available for this flavor of Chromium
python() {
    if not bb.utils.contains('DISTRO_FEATURES', 'x11', True, False, d):
        raise bb.parse.SkipPackage("X11 is not available")
}
