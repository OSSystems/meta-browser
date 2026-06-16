# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-esr:"

SRC_URI[sha256sum] = "85dfb9f6021152b4302b8968ef485d958c8c471cb02415a19853daaad5acce62"

SRC_URI += "file://0001-Bug-1993797-Fix-AST-parsing-in-DecoratorVisitor-for-.patch \
            file://0001-Bug-1983713-Use-non-deprecated-ast-value.-r-firefox-.patch \
            file://0001-Bug-1983736-Patch-jsonschema-to-work-with-Python-3.1.patch \
            file://0001-Bug-1969769-Change-uses-of-ast.Str-with-ast.Constant.patch \
            file://undeclared-identifier__builtin_ia32_vcvtph2ps256.patch \
            file://clang22-nss-prelude-fix.patch \
            file://fix-musl-with-arm.patch \
            file://fix-crash-with-glibc-2.43-on-23-bit-systems.patch \
            file://debian-hacks/Fix-math_private.h-for-i386-FTBFS.patch \
            file://0001-Fix-conflicting-types-for-once_flag-and-call_once-wi.patch \
"
