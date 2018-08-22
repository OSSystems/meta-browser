SECTION = "x11/utils"
DEPENDS += "gnu-config-native virtual/libintl libxt libxi zip-native gtk+"

SRC_URI += "file://mozconfig"

inherit pkgconfig

EXTRA_OEMAKE += "SHELL=/bin/sh"
EXTRA_OECONF = "--target=${TARGET_SYS} --host=${BUILD_SYS} \
                --with-toolchain-prefix=${TARGET_SYS}- \
                --prefix=${prefix}"
EXTRA_OECONF_append_arm = " --disable-elf-hack"
EXTRA_OECONF_append_x86 = " --disable-elf-hack"
EXTRA_OECONF_append_x86-64 = " --disable-elf-hack"
SELECTED_OPTIMIZATION = "-Os -fsigned-char -fno-strict-aliasing"

export CROSS_COMPILE = "1"

export MOZCONFIG = "${B}/mozconfig"
export OBJDIR = "${S}/firefox-build-dir"
export MOZ_OBJDIR = "${S}/firefox-build-dir"
export FOUND_MOZCONFIG = "${B}/mozconfig"

export TARGET_SYS
export STAGING_INCDIR
export STAGING_LIBDIR

export CONFIGURE_ARGS = "${EXTRA_OECONF}"
export HOST_CC = "${BUILD_CC}"
export HOST_CXX = "${BUILD_CXX}"
export HOST_CFLAGS = "${BUILD_CFLAGS}"
export HOST_CXXFLAGS = "${BUILD_CXXFLAGS}"
export HOST_LDFLAGS = "${BUILD_LDFLAGS}"
export HOST_RANLIB = "${BUILD_RANLIB}"
export HOST_AR = "${BUILD_AR}"

mozilla_do_configure() {
	install -D -m 0644 ${WORKDIR}/mozconfig ${MOZCONFIG}
	# Put PARALLEL_MAKE into mozconfig
	if [ ! -z "${PARALLEL_MAKE}" ] ; then
		echo mk_add_options MOZ_MAKE_FLAGS=\"${PARALLEL_MAKE}\" \
			>> ${MOZCONFIG}
	fi
	if [ ! -z "${EXTRA_OECONF}" ] ; then
		for f in ${EXTRA_OECONF}
		do
			echo ac_add_options $f >> ${MOZCONFIG}
		done

	fi
	echo ac_add_options --enable-optimize=\"${SELECTED_OPTIMIZATION}\" \
		>> ${MOZCONFIG}

	oe_runmake -f client.mk -s configure
}

mozilla_do_compile() {
	oe_runmake -f client.mk build
}

mozilla_do_install() {
	oe_runmake -f client.mk install INSTALL_SDK= DESTDIR="${D}"
}

EXPORT_FUNCTIONS do_configure do_compile do_install
