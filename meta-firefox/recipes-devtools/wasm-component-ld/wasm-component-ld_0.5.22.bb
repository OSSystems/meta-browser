LICENSE = "Apache-2.0-with-LLVM-exception | MIT | Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE-APACHE;md5=1836efb2eb779966696f473ee8540542 \
                    file://LICENSE-Apache-2.0_WITH_LLVM-exception;md5=a1ba2b4c4f909ac0b517d8a37d2ac70f \
                    file://LICENSE-MIT;md5=b377b220f43d747efdec40d69fcaa69d"

SRC_URI += "git://github.com/bytecodealliance/wasm-component-ld.git;protocol=https;branch=main"
SRCREV = "c9e38b9bc181ce7a4f8b3d21edb48c47ddf197de"

require wasm-component-ld-crates.inc

inherit cargo cargo-update-recipe-crates

BBCLASSEXTEND = "native"
