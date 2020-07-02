SUMMARY = "Google Client"
SRC_URI = "git://chromium.googlesource.com/chromium/tools/depot_tools.git;protocol=https"
SRCREV = "6e6c67d0eac4eaca84e81c1d6c10ba615c99872b"
PV = "1.0+git${SRCPV}"
S = "${WORKDIR}/git"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c2c05f9bdd5fc0b458037c2d1fb8d95e"

inherit native

do_install() {
        mkdir -p ${D}${bindir}
        cp -r ${S} ${D}${bindir}/${PN}
}

FILES_${PN}="${bindir}/*"
