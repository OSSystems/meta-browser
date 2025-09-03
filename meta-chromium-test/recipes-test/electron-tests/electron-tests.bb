SUMMARY = "Electron application smoke tests"
DESCRIPTION = "Simple smoke tests for Electron application functionality"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://electron-smoke-test.sh"

S = "${UNPACKDIR}"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 electron-smoke-test.sh ${D}${bindir}/electron-smoke-test
}

RDEPENDS:${PN} = "bash"
FILES:${PN} = "${bindir}/electron-smoke-test"
