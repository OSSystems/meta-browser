SECTION = "x11/utils"
DEPENDS += "gnu-config-native virtual/libintl libxt libxi \
	    zip-native gtk+ orbit2 libidl-native"

SRC_URI += "file://mozconfig"

inherit gettext pkgconfig

EXTRA_OECONF = "--target=${TARGET_SYS} --host=${BUILD_SYS} \
                --build=${BUILD_SYS} --prefix=${prefix} --disable-elf-hack"
SELECTED_OPTIMIZATION = "-Os -fsigned-char -fno-strict-aliasing"

export CROSS_COMPILE = "1"
export MOZCONFIG = "${WORKDIR}/mozconfig"
export MOZ_OBJDIR = "${S}"

export CONFIGURE_ARGS = "${EXTRA_OECONF}"
export HOST_CC = "${BUILD_CC}"
export HOST_CXX = "${BUILD_CXX}"
export HOST_CFLAGS = "${BUILD_CFLAGS}"
export HOST_CXXFLAGS = "${BUILD_CXXFLAGS}"
export HOST_LDFLAGS = "${BUILD_LDFLAGS}"
export HOST_RANLIB = "${BUILD_RANLIB}"
export HOST_AR = "${BUILD_AR}"
# Set the host libIDL stuff correctly.
export HOST_LIBIDL_CONFIG="PKG_CONFIG_PATH=${STAGING_LIBDIR_NATIVE}/pkgconfig pkg-config libIDL-2.0"
# Due to sysroot we need to sed out references to the target staging
# when building the native version of xpidl Symptons of the failure
# include "gthread.h:344: error: size of array 'type name' is negative"
export HOST_LIBIDL_CFLAGS="`${HOST_LIBIDL_CONFIG} --cflags | sed -e s:${STAGING_DIR_TARGET}::g`"
export HOST_LIBIDL_LIBS="`${HOST_LIBIDL_CONFIG} --libs`"


mozilla_do_configure() {
	(
		set -e
		for cg in `find ${S} -name config.guess`; do
			install -m 0755 \
			${STAGING_DATADIR_NATIVE}/gnu-config/config.guess \
			${STAGING_DATADIR_NATIVE}/gnu-config/config.sub \
			`dirname $cg`/
		done
	)

	# Put PARALLEL_MAKE into mozconfig
	if [ ! -z "${PARALLEL_MAKE}" ] ; then
		echo mk_add_options MOZ_MAKE_FLAGS=\"${PARALLEL_MAKE}\" \
			>> ${MOZCONFIG}
	fi

	if [ -e ${MOZ_OBJDIR}/Makefile ] ; then
		oe_runmake -f client.mk ${MOZ_OBJDIR}/Makefile \
					${MOZ_OBJDIR}/config.status
	fi

	sed -i -e 's,@prefix@,${prefix},g' \
	       -e 's,@STAGING_INCDIR@,${STAGING_INCDIR},g' \
	       -e 's,@STAGING_DIR_TARGET@,${STAGING_DIR_TARGET},g' \
	       ${MOZCONFIG}
}

mozilla_do_compile() {
	oe_runmake -f client.mk build_all
}

mozilla_do_install() {
	oe_runmake DESTDIR="${D}" destdir="${D}" install
}

EXPORT_FUNCTIONS do_configure do_compile do_install
