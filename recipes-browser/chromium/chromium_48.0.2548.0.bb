include chromium-browser.inc

DEPENDS += "xextproto gtk+ libxi libxss"

SRC_URI += "\
        file://chromium/add_missing_stat_h_include.patch \
        file://chromium/0003-Remove-hard-coded-values-for-CC-and-CXX.patch \
        file://chromium/0004-Create-empty-i18n_process_css_test.html-file-to-avoi.patch \
        file://chromium/0005-Override-root-filesystem-access-restriction.patch \
        file://chromium/0011-Replace-readdir_r-with-readdir.patch \
        file://chromium/remove-Werror.patch \
        ${@bb.utils.contains('PACKAGECONFIG', 'component-build', 'file://component-build.gypi', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'ignore-lost-context', 'file://chromium/0001-Remove-accelerated-Canvas-support-from-blacklist.patch', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'disable-api-keys-info-bar', 'file://chromium/0002-Disable-API-keys-info-bar.patch', '', d)} \
"

LIC_FILES_CHKSUM = "file://LICENSE;md5=0fca02217a5d49a14dfe2d11837bb34d"
SRC_URI[md5sum] = "0534981cc21efcd11e64b67b85854420"
SRC_URI[sha256sum] = "4ca4e2adb340b3fb4d502266ad7d6bda45fa3519906dbf63cce11a63f680dbc8"
