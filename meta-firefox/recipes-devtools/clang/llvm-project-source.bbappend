FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += '${@bb.utils.contains_any("LAYERSERIES_CORENAMES", "dunfell kirkstone", \
            " file://0001-fix-__STDC_LIMIT_MACROS-macro-redifined.patch \
              file://0002-fix-python-typemaps.swig-cannot-bind-rvalue-error.patch ", \
            "", d)}'
