SUMMARY = "WebAssembly SDK"
HOMEPAGE = "https://github.com/WebAssembly/wasi-sdk"
DESCRIPTION = "SDK for WebAssembly System Interface (WASI)"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=a1ba2b4c4f909ac0b517d8a37d2ac70f"

#Note: cross-compilation does not work yet, this recipe is native only currently.

inherit native

DEPENDS = "clang-native cmake-native ninja-native"

S = "${WORKDIR}/git"

SRC_URI = "gitsm://github.com/WebAssembly/wasi-sdk.git;protocol=https;branch=main"
SRC_URI += "file://0001-disable-exceptions-and-bulk-memory.patch"

SRCREV = "3fb0057a6da0313d18dad241ef7a87aa43f1485c"


TOOLCHAIN = "clang"
TOOLCHAIN:class-native = "clang"

RUNTIME = "llvm"
RUNTIME:class-native = "llvm"

OEMAKE_EXTRA:task-compile = " strip "

do_compile:prepend(){
    export PREFIX=/usr/share/wasi
}

do_install(){

    mkdir -p ${D}${datadir}/wasi-sysroot/bin
    mkdir -p ${D}${datadir}/wasi-sysroot/lib
    mkdir -p ${D}${datadir}/wasi-sysroot/include
    mkdir -p ${D}${datadir}/wasi-sysroot/share

    cp ${S}/build/install/usr/share/wasi/bin/* ${D}${datadir}/wasi-sysroot/bin/

    cp -r ${S}/build/install/usr/share/wasi/lib/* ${D}${datadir}/wasi-sysroot/lib/
    cp -r ${S}/build/install/usr/share/wasi/share/clang ${D}${datadir}/wasi-sysroot/share/
    cp -r ${S}/build/install/usr/share/wasi/share/cmake ${D}${datadir}/wasi-sysroot/share/
    cp -r ${S}/build/install/usr/share/wasi/share/misc ${D}${datadir}/wasi-sysroot/share/

    cp -r ${S}/build/install/usr/share/wasi/share/wasi-sysroot/include/* ${D}${datadir}/wasi-sysroot/include/

    cp -r ${S}/build/install/usr/share/wasi/share/wasi-sysroot/lib/* ${D}${datadir}/wasi-sysroot/lib/
    cp -r ${S}/build/install/usr/share/wasi/share/wasi-sysroot/share/* ${D}${datadir}/wasi-sysroot/share/
}

FILES:${PN} = "/usr/share/wasi-sysroot/*"
