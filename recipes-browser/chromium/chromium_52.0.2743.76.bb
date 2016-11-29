CHROMIUM_ENABLE_WAYLAND = "0"

include chromium-browser.inc

DEPENDS += "xextproto gtk+ libxi libxss"

SRC_URI += "\
        file://chromium/add_missing_stat_h_include.patch \
        file://0003-Remove-hard-coded-values-for-CC-and-CXX.patch \
        file://0004-Create-empty-i18n_process_css_test.html-file-to-avoi.patch \
        file://0005-Override-root-filesystem-access-restriction.patch \
        file://chromium/0011-Replace-readdir_r-with-readdir.patch \
        file://chromium/remove-Werror.patch \
        file://chromium/unset-madv-free.patch \
        file://chromium/Do-not-depend-on-Linux-4.5.patch \
        ${@bb.utils.contains('PACKAGECONFIG', 'component-build', 'file://component-build.gypi', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'ignore-lost-context', 'file://0001-Remove-accelerated-Canvas-support-from-blacklist.patch', '', d)} \
"

SRC_URI_append_i586 = "file://x86-m32.patch"

LIC_FILES_CHKSUM = "file://LICENSE;md5=0fca02217a5d49a14dfe2d11837bb34d"
SRC_URI[md5sum] = "0fee71466e1f2dc39ed4549d04b58ee2"
SRC_URI[sha256sum] = "c54cdc11c3324152f3d5be98dcb4eae2bda0fc9dac7dd5f9010150458d68c18c"

# X11 must be available for this flavor of Chromium
python() {
    if not bb.utils.contains('DISTRO_FEATURES', 'x11', True, False, d):
        raise bb.parse.SkipPackage("X11 is not available")
}
