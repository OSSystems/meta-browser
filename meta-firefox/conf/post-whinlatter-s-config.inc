# In whinlatter the UNPACKDIR location has changed, and pathes created using
# the ${WORKDIR} variable are not valid anymore. However older versions still
# require these. This fragment contains a list of recipes in the meta-firefox
# layer that needs their "S" variable modified depending on the used Yocto
# version.

WHINLATTER_S = "${UNPACKDIR}/${BB_GIT_DEFAULT_DESTSUFFIX}"
NON_WHINLATTER_S = "${WORKDIR}/git"

CORRECT_S = '${@bb.utils.contains_any("LAYERSERIES_CORENAMES", "whinlatter", d.getVar("WHINLATTER_S"), d.getVar("NON_WHINLATTER_S"), d)}'

S:pn-cbindgen = "${CORRECT_S}"
S:pn-cbindgen-native = "${CORRECT_S}"
S:pn-wasi-sdk-sysroot-native = "${CORRECT_S}"
S:pn-wasi-sdk-toolchain-native = "${CORRECT_S}"
S:pn-wasm-component-ld = "${CORRECT_S}"
S:pn-wasm-component-ld-native = "${CORRECT_S}"
