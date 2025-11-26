require wasi-sdk.inc

DEPENDS += "wasi-sdk-toolchain-native wasm-component-ld-native"
RDEPENDS:${PN} = "wasi-sdk-toolchain-native wasm-component-ld-native"

# The first 3 options come from the project's readme, about the compiling
# intructions. The WASI_SDK_CPU_FLAGS' mcpu argument is the default, from
# the main CMakeLists.txt file. The DEBUG_PREFIX_MAP is required, otherwise
# it embeds some host-paths into libcxx, which later leak into the final binaries.
EXTRA_OECMAKE += " \
    -DCMAKE_TOOLCHAIN_FILE=${STAGING_DATADIR_NATIVE}/wasi-sysroot/share/cmake/wasi-sdk.cmake \
    -DCMAKE_C_COMPILER_WORKS=ON \
    -DCMAKE_CXX_COMPILER_WORKS=ON \
    -DWASI_SDK_CPU_CFLAGS='-mcpu=lime1 ${DEBUG_PREFIX_MAP}' \
    "

BUILD_LD = "wasm-ld"

# wasi-sysroot is built for wasi target, not for host/target.
# The wasi toolchain file for cmake contains wasi-specific
# attributes. To avoid overshadowing, unset the build-target
# specific attributes.
CFLAGS = ""
CXXFLAGS = ""
LDFLAGS = ""
CMAKE_C_LINK_FLAGS = ""
CMAKE_CXX_LINK_FLAGS = ""
OECMAKE_C_LINK_FLAGS = ""
OECMAKE_CXX_LINK_FLAGS = ""
BUILD_CC = ""
COMPILER_RT:remove = "-rtlib=libgcc"

do_install:append(){
    install -d ${D}${datadir}/wasi-sysroot/lib/clang/${WASI_CLANG_VERSION}
    mv ${D}${prefix}/clang-resource-dir/lib ${D}${datadir}/wasi-sysroot/lib/clang/${WASI_CLANG_VERSION}/
}
