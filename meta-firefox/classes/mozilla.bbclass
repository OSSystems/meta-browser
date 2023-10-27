SECTION = "x11/utils"
DEPENDS += "gnu-config-native virtual/libintl libxt libxi zip-native gtk+"

inherit pkgconfig cargo

EXTRA_OECONF = "--target=${TARGET_SYS} --host=${BUILD_SYS} \
                --with-toolchain-prefix=${TARGET_SYS}- \
                --prefix=${prefix} \
                --libdir=${libdir}"
EXTRA_OECONF:append:arm = " --disable-elf-hack"
EXTRA_OECONF:append:x86 = " --disable-elf-hack"
EXTRA_OECONF:append:x86-64 = " --disable-elf-hack"
SELECTED_OPTIMIZATION = "-Os -fsigned-char -fno-strict-aliasing"

export CROSS_COMPILE = "1"
export MOZCONFIG = "${B}/mozconfig"
export MOZ_OBJDIR = "${S}/firefox-build-dir"
export MOZBUILD_STATE_PATH = "${S}/mozbuild_state"

export OUT_DIR="${S}/build/target/release/deps"
export WASI_SYSROOT="${STAGING_DATADIR_NATIVE}/wasi-sysroot"

export WASM_CC="${WASI_SYSROOT}/bin/clang -target wasm32-wasi "
export WASM_CXX="${WASI_SYSROOT}/bin/clang++ -target wasm32-wasi "

#export WASM_CXXFLAGS=" -Xclang -target-feature -Xclang -bulk-memory "

export BUILD_VERBOSE_LOG="1"

mozilla_run_mach() {
	export SHELL="/bin/sh"
	export RUSTFLAGS="${RUSTFLAGS} -Cpanic=abort -Clinker=${WORKDIR}/wrapper/target-rust-ccld --sysroot=${RECIPE_SYSROOT}"

	# For Kirkstone meta-rust layer is required, however
	# that seems to set different target/host triplets compared
	# to the rust used in poky (in later releases)
	export RUST_HOST='${@bb.utils.contains("LAYERSERIES_CORENAMES", "kirkstone", "${BUILD_SYS}", "${RUST_BUILD_SYS}", d)}'
	export RUST_TARGET='${@bb.utils.contains("LAYERSERIES_CORENAMES", "kirkstone", "${TARGET_SYS}", "${RUST_HOST_SYS}", d)}'

	export BINDGEN_MFLOAT="${@bb.utils.contains('TUNE_CCARGS_MFLOAT', 'hard', '-mfloat-abi=hard', '', d)}"
	export BINDGEN_CFLAGS="--target=${TARGET_SYS} --sysroot=${RECIPE_SYSROOT} ${BINDGEN_MFLOAT}"
	export INSTALL_SDK=0
	export DESTDIR="${D}"

        cd "${S}"

	./mach "$@"
}

do_configure:append() {
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

        oe_cargo_fix_env
	mozilla_run_mach configure
}

mozilla_do_compile() {
        oe_cargo_fix_env
	mozilla_run_mach build
}

mozilla_do_install() {
        oe_cargo_fix_env
	mozilla_run_mach install
}

EXPORT_FUNCTIONS do_install do_compile
