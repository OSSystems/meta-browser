FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += '${@bb.utils.contains("LAYERSERIES_CORENAMES", "kirkstone", \
            "file://0001-fix-compilation-with-rust-1.76.patch", \
            "", d)}'

