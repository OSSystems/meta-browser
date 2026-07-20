SUMMARY = "cbindgen creates C/C++11 headers for Rust libraries which expose a public C API"
HOMEPAGE = "https://github.com/mozilla/cbindgen"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9741c346eef56131163e13b9db1241b3"

require cbindgen-crates.inc
SRC_URI += "git://github.com/mozilla/cbindgen.git;protocol=https;branch=main"

SRCREV = "b826cb8911488fe8a209d2b693492c0c673e8cca"

inherit cargo pkgconfig cargo-update-recipe-crates

BBCLASSEXTEND += "native"
