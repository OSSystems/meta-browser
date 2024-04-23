FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI += '${@bb.utils.contains_any("LAYERSERIES_CORENAMES", "dunfell", \
            " file://0001-fix-__STDC_LIMIT_MACROS-macro-redifined.patch \
              file://0002-fix-python-typemaps.swig-cannot-bind-rvalue-error.patch ", \
            "", d)}'


SRC_URI += '${@ "file://0003-bugfix-64102-riscv-linking-error.patch" \
               if "${LLVMVERSION}".startswith("17") or "${LLVMVERSION}".startswith("16") \
               else ""}'
