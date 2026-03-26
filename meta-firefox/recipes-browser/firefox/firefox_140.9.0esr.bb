# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

include firefox.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/firefox-esr:"

SRC_URI[sha256sum] = "b972b2a4c17244d51c10123cbd6c936e2cf26ebc29eb724570d285c283e9e92c"

SRC_URI += "file://0001-Bug-1993797-Fix-AST-parsing-in-DecoratorVisitor-for-.patch \
            file://0001-Bug-1983713-Use-non-deprecated-ast-value.-r-firefox-.patch \
            file://0001-Bug-1983736-Patch-jsonschema-to-work-with-Python-3.1.patch \
            file://0001-Bug-1969769-Change-uses-of-ast.Str-with-ast.Constant.patch"
