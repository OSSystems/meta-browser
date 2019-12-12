# gn-native contains the GN binary used to configure Chromium.
# It is not released separately, and each Chromium release is only expected to
# work with the GN version provided with it.

require chromium.inc

inherit native

S = "${WORKDIR}/chromium-${PV}"

# bootstrap.py --no_clean hardcodes the build location to out_bootstrap.
# Omitting --no_clean causes the script to create a temporary directory with a
# random name outside the build directory, so we choose the lesser of the two
# evils.
B = "${S}/out_bootstrap"

SRC_URI += " \
        file://0001-Pass-no-static-libstdc-to-gen.py.patch \
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
	python ${S}/tools/gn/bootstrap/bootstrap.py --skip-generate-buildfiles
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${S}/out/Release/gn ${D}${bindir}/gn
}

INSANE_SKIP_${PN} += "already-stripped"
