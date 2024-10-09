FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += '${@ "file://0003-bugfix-64102-riscv-linking-error.patch" \
               if "${LLVMVERSION}".startswith("17") or "${LLVMVERSION}".startswith("16") \
               else ""}'

SRC_URI += '${@ "file://0004-Copy-missing-header-to-include-folder.patch" \
               if "${LLVMVERSION}".startswith("14") else ""}'
