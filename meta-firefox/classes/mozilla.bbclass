SECTION = "x11/utils"
DEPENDS:append = " gnu-config-native virtual/libintl \
	${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libxt libxi', '', d)} \
	zip-native \
"


inherit pkgconfig cargo

EXTRA_OECONF = "--target=${TARGET_SYS} --host=${BUILD_SYS} \
                --with-toolchain-prefix=${TARGET_SYS}- \
                --prefix=${prefix} \
                --libdir=${libdir}"

EXTRA_OECONF:append:arm = " --disable-sandbox --disable-elf-hack "
EXTRA_OECONF:append:aarch64 = " --disable-elf-hack "
EXTRA_OECONF:append:x86-64 = " --disable-elf-hack "

EXTRA_OECONF:append:libc-musl = " --disable-jemalloc "
EXTRA_OECONF:append:libc-musl:x86-64 = " --disable-sandbox "

SELECTED_OPTIMIZATION = "-Os -fsigned-char -fno-strict-aliasing"

LDFLAGS:append:libc-musl = " -Wl,-rpath,${libdir}/firefox "

export CROSS_COMPILE = "1"
export MOZCONFIG = "${B}/mozconfig"
export MOZ_OBJDIR = "${S}/firefox-build-dir"
export MOZBUILD_STATE_PATH = "${S}/mozbuild_state"

export OUT_DIR = "${S}/build/target/release/deps"
export WASI_SYSROOT = "${STAGING_DATADIR_NATIVE}/wasi-sysroot"

export WASM_CC = "${WASI_SYSROOT}/bin/clang -target wasm32-wasi "
export WASM_CXX = "${WASI_SYSROOT}/bin/clang++ -target wasm32-wasi "

export BUILD_VERBOSE_LOG = "1"

RUST_HOST ?= "${RUST_BUILD_SYS}"
RUST_TARGET ?= "${RUST_HOST_SYS}"

export CRATE_CC_NO_DEFAULTS = "1"

mozilla_run_mach() {
	export SHELL="/bin/sh"
	export RUSTFLAGS="${RUSTFLAGS} -Clinker=${WORKDIR}/wrapper/target-rust-ccld --sysroot=${RECIPE_SYSROOT}"

	export RUST_HOST=${RUST_HOST}
	export RUST_TARGET=${RUST_TARGET}

	export BINDGEN_MFLOAT="${@bb.utils.contains('TUNE_CCARGS_MFLOAT', 'hard', '-mfloat-abi=hard', '', d)}"
	export BINDGEN_CFLAGS="--target=${TARGET_SYS} --sysroot=${RECIPE_SYSROOT} ${BINDGEN_MFLOAT}"
	export INSTALL_SDK=0
	export DESTDIR="${D}"

	export AS="${CC}"

        cd "${S}"

	./mach "$@"
}

do_configure:append() {
	install -D -m 0644 ${FF_BASEDIR}/mozconfig ${MOZCONFIG}
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

deltask rust_gen_targets
addtask rust_gen_targets after do_patch before do_configure
