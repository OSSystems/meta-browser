LICENSE = "Apache-2.0-with-LLVM-exception | MIT | Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE-APACHE;md5=1836efb2eb779966696f473ee8540542 \
                    file://LICENSE-Apache-2.0_WITH_LLVM-exception;md5=a1ba2b4c4f909ac0b517d8a37d2ac70f \
                    file://LICENSE-MIT;md5=b377b220f43d747efdec40d69fcaa69d"

SRC_URI += "git://github.com/bytecodealliance/wasm-component-ld.git;protocol=https;branch=main"
SRCREV = "0a5f2513f6157e2c01c0485aa68365d8e4622ccb"

require wasm-component-ld-crates.inc

inherit cargo
# Kirkstone has no cargo-update-recipe-crates class, so just comment
# it to avoid the parsing error
# cargo-update-recipe-crates

BBCLASSEXTEND = "native"
