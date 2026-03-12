SUMMARY = "WebAssembly SDK"
HOMEPAGE = "https://github.com/WebAssembly/wasi-sdk"
DESCRIPTION = "SDK for WebAssembly System Interface (WASI)"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=a1ba2b4c4f909ac0b517d8a37d2ac70f"

# Note: cross-compilation does not work yet (possibly never will, unless someone does
# the legwork), these recipes are native only currently.

inherit cmake native

DEPENDS = "clang-native cmake-native ninja-native"

SRC_URI = "gitsm://github.com/WebAssembly/wasi-sdk.git;protocol=https;branch=main \
           file://0001-lower-cmake-requirement.patch \
           file://0001-don-t-build-wasm-component-ld.patch \
           file://0001-don-t-strip-symbols-from-binaries.patch"

SRCREV = "2e6b578a6986005e29e01c10b94843fdb2a76f0a"

TOOLCHAIN = "clang"
TOOLCHAIN:class-native = "clang"

RUNTIME = "llvm"
RUNTIME:class-native = "llvm"

FILES:${PN} = "${datadir}/wasi-sysroot/*"

# this needs manual updating
WASI_CLANG_VERSION = "22"
