SECTION = "x11/utils"
DEPENDS += "gnu-config-native virtual/libintl libxt libxi zip-native gtk+"

SRC_URI += "file://mozconfig"

inherit pkgconfig

EXTRA_OECONF = "--target=${TARGET_SYS} --host=${BUILD_SYS} \
                --with-toolchain-prefix=${TARGET_SYS}- \
                --prefix=${prefix} \
                --libdir=${libdir}"
EXTRA_OECONF_append_arm = " --disable-elf-hack"
EXTRA_OECONF_append_x86 = " --disable-elf-hack"
EXTRA_OECONF_append_x86-64 = " --disable-elf-hack"
SELECTED_OPTIMIZATION = "-Os -fsigned-char -fno-strict-aliasing"

export CROSS_COMPILE = "1"
export MOZCONFIG = "${B}/mozconfig"
export MOZ_OBJDIR = "${S}/firefox-build-dir"

export HOST_CC = "${BUILD_CC}"
export HOST_CXX = "${BUILD_CXX}"
export HOST_CFLAGS = "${BUILD_CFLAGS}"
export HOST_CXXFLAGS = "${BUILD_CXXFLAGS}"
export HOST_LDFLAGS = "${BUILD_LDFLAGS}"
export HOST_RANLIB = "${BUILD_RANLIB}"
export HOST_AR = "${BUILD_AR}"

mozilla_run_mach() {
	export SHELL="/bin/sh"
	export RUSTFLAGS="${RUSTFLAGS} -Cpanic=unwind"
	export RUST_HOST="${BUILD_SYS}"
	export RUST_TARGET="${TARGET_SYS}"
	export RUST_TARGET_PATH="${STAGING_LIBDIR_NATIVE}/rustlib"
	export BINDGEN_MFLOAT="${@bb.utils.contains('TUNE_CCARGS_MFLOAT', 'hard', '-mfloat-abi=hard', '', d)}"
	export BINDGEN_CFLAGS="--target=${TARGET_SYS} --sysroot=${RECIPE_SYSROOT} ${BINDGEN_MFLOAT}"

	export INSTALL_SDK=0
	export DESTDIR="${D}"

	./mach "$@"
}

mozilla_do_configure() {
	install -D -m 0644 ${WORKDIR}/mozconfig ${MOZCONFIG}
	if [ ! -z "${EXTRA_OECONF}" ] ; then
		for f in ${EXTRA_OECONF}
		do
			echo ac_add_options $f >> ${MOZCONFIG}
		done
	fi
	if [ ! -z "${PACKAGECONFIG_CONFARGS}" ] ; then
		for f in ${PACKAGECONFIG_CONFARGS}
		do
			echo ac_add_options $f >> ${MOZCONFIG}
		done
	fi
	echo ac_add_options --enable-optimize=\"${SELECTED_OPTIMIZATION}\" \
		>> ${MOZCONFIG}

	mozilla_run_mach configure
}

mozilla_do_compile() {
	mozilla_run_mach build
}

mozilla_do_install() {
	mozilla_run_mach install
}

EXPORT_FUNCTIONS do_configure do_compile do_install
