SUMMARY = "Chromium Virtual Keyboard"
DESCRIPTION = "Virtual Keyboard Extension for Chromium/Chrome"
HOMEPAGE = "https://apps.xontab.com/VirtualKeyboard"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a8366463bacd4f2ec5edd419e8a113ff"

SRC_URI = " \
            git://github.com/xontab/${BPN}.git \
"
SRCREV = "e2b9adf4885cc4ed600cd9bccb77e0df8ff549aa"
S = "${WORKDIR}/git"
PV = "1.11.3+git${SRCPV}"

inherit allarch chromium-extension-package

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${CHROMIUM_EXTENSION_DIR}/
    cp -r ${S} ${D}${CHROMIUM_EXTENSION_DIR}/${BPN}

    # remove unecessary artifacts
    rm  -f ${D}${CHROMIUM_EXTENSION_DIR}/${BPN}/_config.yml
    rm  -rf ${D}${CHROMIUM_EXTENSION_DIR}/${BPN}/.git/
}

FILES_${PN} += "${CHROMIUM_EXTENSION_DIR}/${BPN}"
