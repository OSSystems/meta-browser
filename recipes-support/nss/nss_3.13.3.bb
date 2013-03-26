DESCRIPTION = "Mozilla's SSL and TLS implementation"
HOMEPAGE = "http://www.mozilla.org/projects/security/pki/nss/"

PR = "r4"

LICENSE = "MPL1.1 GPL LGPL"
LIC_FILES_CHKSUM = "file://security/nss/manifest.mn;md5=d71978748f23eae3156c38ac2a691924"

DEPENDS = "sqlite3 nspr nspr-native"

SRC_URI = "\
	http://ftp.mozilla.org/pub/mozilla.org/security/nss/releases/NSS_3_13_3_RTM/src/${PN}-${PV}.tar.gz \
	file://nss.pc.in \
	file://skip_signing.patch \
        file://native_cc.patch \
"

S = "${WORKDIR}/${P}/mozilla"

SRC_URI[md5sum] = "006cb82fa900e9e664b4b14a9b7810ca"
SRC_URI[sha256sum] = "efa10f2c70da4bddabf1a6081964969bb23359b93d6eadbf4739274a77bc3587"

TD = "${S}/tentative-dist"

PARALLEL_MAKE = ""

USE64BIT_x86-64 = "USE_64=1"
USE64BIT ?= ""

do_compile() {
#	sed -e 's:SOURCE_PREFIX = $(CORE_DEPTH)/\.\./dist:SOURCE_PREFIX = $(CORE_DEPTH)/dist:' -i ${S}/security/coreconf/source.mk
	make -C security/nss \
		build_coreconf \
		build_dbm \
		export libs program \
		MOZILLA_CLIENT=1 \
		BUILD_OPT=1 \
		SKIP_SHLIBSIGN=1 \
		OS_TARGET="Linux" \
		OS_TARGET_RELEASE="2.6" \
		OS_TEST="${TARGET_ARCH}" \
		NSPR_INCLUDE_DIR="${STAGING_INCDIR}/mozilla/nspr" \
		NSPR_LIB_DIR="${STAGING_LIBDIR}" \
		SQLITE3_INCLUDE_DIR="${STAGING_INCDIR}" \
		OPTIMIZER="${CFLAGS}" \
		NS_USE_GCC=1 \
		NSS_USE_SYSTEM_SQLITE=1 \
		NSS_ENABLE_ECC=1 \
		DEFAULT_COMPILER="${TARGET_CC}" \
		CC="${CC}" \
		CCC="${CXX}" \
		CXX="${CXX}" \
		RANLIB="${RANLIB}" \
		NATIVE_CC="${BUILD_CC}" \
		NATIVE_FLAGS="${BUILD_CFLAGS}" \
		${USE64BIT}
}

do_install() {
	make -C security/nss \
		install \
		MOZILLA_CLIENT=1 \
		BUILD_OPT=1 \
		SKIP_SHLIBSIGN=1 \
		OS_TARGET=Linux \
		OS_TARGET_RELEASE="2.6" \
		OS_TEST="${TARGET_ARCH}" \
		NSPR_INCLUDE_DIR="${STAGING_INCDIR}/mozilla/nspr" \
		NSPR_LIB_DIR="${STAGING_LIBDIR}" \
		NS_USE_GCC=1 \
		NSS_USE_SYSTEM_SQLITE=1 \
		NSS_ENABLE_ECC=1 \
		FREEBL_CHILD_BUILD=1 \
		CC="${CC}" \
		SOURCE_LIB_DIR="${TD}/${libdir}" \
		SOURCE_BIN_DIR="${TD}/${bindir}" \
		${USE64BIT}

	install -d ${D}/${libdir}

	for shared_lib in ${TD}/${libdir}/*.so*
	do
#		oe_libinstall -C ${TD}/${libdir} `basename $shared_lib .so` ${D}/${libdir}
		cp $shared_lib ${D}/${libdir}
#		ln -sf $(basename $shared_lib) ${D}/${libdir}/$(basename $shared_lib .1oe)
	done

	install -d ${D}/${includedir}/mozilla/nss
	install -m 644 -t ${D}/${includedir}/mozilla/nss ${S}/dist/public/nss/*

	for static_lib in ${TD}/${libdir}/*.a
	do
		oe_libinstall -C ${TD}/${libdir} `basename $static_lib .a` ${D}/${libdir}
	done

	install -d ${D}/${bindir}
	for binary in ${TD}/${bindir}/*
	do
		install -m 755 -t ${D}/${bindir} $binary
	done

	install -D ${WORKDIR}/nss.pc.in ${D}${libdir}/pkgconfig/nss.pc
	sed -i s:@VERSION@:${PV}:g ${D}${libdir}/pkgconfig/nss.pc
	sed -i s:OEPREFIX:${prefix}:g ${D}${libdir}/pkgconfig/nss.pc
	sed -i s:OELIBDIR:${libdir}:g ${D}${libdir}/pkgconfig/nss.pc
	sed -i s:OEINCDIR:${includedir}:g ${D}${libdir}/pkgconfig/nss.pc
	sed -i s:OEEXECPREFIX:${exec_prefix}:g ${D}${libdir}/pkgconfig/nss.pc

	sed -i s:/usr/local/bin/perl:${bindir}/perl:g ${D}${bindir}/smime
}

FILES_SOLIBSDEV := ""
FILES_${PN} += "${base_libdir}/lib*${SOLIBSDEV} ${libdir}/lib*${SOLIBSDEV}"
RDEPENDS_${PN} += "perl"
