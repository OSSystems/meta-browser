# gn-native contains the GN binary used to configure Chromium.
# It is not released separately, and each Chromium release is only expected to
# work with the GN version provided with it.

inherit native

SRC_URI = "git://gn.googlesource.com/gn;protocol=https;rev=cd3869be2477f7ee1aa3f27f43ee934e74722dfb"
S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

LICENSE = "BSD-3-Clause & LGPL-2.0 & LGPL-2.1"
LIC_FILES_CHKSUM = "\
    file://${S}/LICENSE;md5=0fca02217a5d49a14dfe2d11837bb34d \
"

# The build system expects the linker to be invoked via the compiler. If we use
# the default value for BUILD_LD, it will fail because it does not recognize
# some of the arguments passed to it.
BUILD_LD = "${CXX}"

# Use LLVM's ar rather than binutils'. Depending on the optimizations enabled
# in the build ar(1) may not be enough.
BUILD_AR = "llvm-ar"

DEPENDS = "clang-native ninja-native"

do_configure[noexec] = "1"

do_compile() {
        python ${S}/build/gen.py --no-static-libstdc++ --out-path=${B}
        ninja -C ${B}
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${B}/gn ${D}${bindir}/gn
}

INSANE_SKIP_${PN} += "already-stripped"
