# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-esr:"

SRC_URI[sha256sum] = "4e75c0c3e2c5530de9364de388272bf81b2b32209d98fa4a7eb50d268a17a5bb"

SRC_URI += "file://0001-Bug-1993797-Fix-AST-parsing-in-DecoratorVisitor-for-.patch \
            file://0001-Bug-1983713-Use-non-deprecated-ast-value.-r-firefox-.patch \
            file://0001-Bug-1983736-Patch-jsonschema-to-work-with-Python-3.1.patch \
            file://0001-Bug-1969769-Change-uses-of-ast.Str-with-ast.Constant.patch \
            file://undeclared-identifier__builtin_ia32_vcvtph2ps256.patch \
            file://clang22-nss-prelude-fix.patch \
            file://fix-musl-with-arm.patch \
"
