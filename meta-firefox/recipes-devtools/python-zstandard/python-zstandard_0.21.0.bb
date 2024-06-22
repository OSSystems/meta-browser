SUMMARY = "Zstandard bindings for Python"
HOMEPAGE = "https://github.com/indygreg/python-zstandard"
AUTHOR = "Gregory Szorc <gregory.szorc@gmail.com>"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3ae87c50fd64b6f0942823686871e758"

SRC_URI = "https://files.pythonhosted.org/packages/4d/70/1f883646641d7ad3944181549949d146fa19e286e892bc013f7ce1579e8f/zstandard-0.21.0.tar.gz"
SRC_URI[md5sum] = "7baafeb332651d70881ce692edf912b3"
SRC_URI[sha256sum] = "f08e3a10d01a247877e4cb61a82a319ea746c356a3786558bed2481e6c405546"

S = "${WORKDIR}/zstandard-0.21.0"

RDEPENDS_${PN} = ""

inherit setuptools3
