DESCRIPTION = "Chromium browser"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0750f191c9bbf46869b70508e7eb455b"
DEPENDS = "xextproto cairo nss gtk+-native zlib-native libav libxi libgnome-keyring libxss cups"

SRC_URI = "http://commondatastorage.googleapis.com/chromium-browser-official/${P}.tar.bz2 \
	file://include.gypi \
"

# include.gypi exists only for armv6 and armv7a and there isn't something like COMPATIBLE_ARCH afaik
COMPATIBLE_HOST = "(armv6|armv7).*-linux"

DEFAULT_PREFERENCE = "-1"

SRC_URI[md5sum] = "86535af2d00d157b358e8fd6fb6ad38c"
SRC_URI[sha256sum] = "6d215eed43cc607e54991d1a87798ed0ac1683dfde9a0cf2f64ed06be79a9423"

inherit gettext

EXTRA_OEGYP =	" \
	-Duse_system_bzip2=1 \
	\
	-Dproprietary_codecs=1 \
	\
	-Dtarget_arch=${TARGET_ARCH} \ 
	-Ddisable_nacl=1 \
	-Dlinux_use_tcmalloc=0 \
	\
	-Dlinux_link_kerberos=0 \
	-Duse_kerberos=0 \
	-Duse_cups=1 \
	-Duse_gnome_keyring=1 \
	-Dlinux_link_gnome_keyring=1 \
	\
	${@base_contains('DISTRO_FEATURES', 'ld-is-gold', '', '-Dlinux_use_gold_binary=0', d)} \
	${@base_contains('DISTRO_FEATURES', 'ld-is-gold', '', '-Dlinux_use_gold_flags=0', d)} \
"

EXTRA_OEGYP_append_armv7a = " \
	-Darmv7=1 \
	-Darm_neon=1 \
"

do_configure() {
	cd ${WORKDIR}
	export GYP_GENERATORS=make
	rm -f ${S}/tools/gyp/pylib/gyp/__init__.pyc
	rm -f ${S}/tools/gyp/pylib/gyp/__init__.pyo
	sed -e 's|__PATH__TO_BE_REPLACED__|"${WORKDIR}/include.gypi"|' -i ${S}/tools/gyp/pylib/gyp/__init__.py
	sed -e "s|__PATH__TO_BE_REPLACED__||" -i ${WORKDIR}/include.gypi

	cd ${S}
	build/gyp_chromium --depth=. ${EXTRA_OEGYP}
}

EXTRA_OEMAKE = "-r ${PARALLEL_MAKE} LDFLAGS.host=-L${STAGING_LIBDIR_NATIVE} V=1 BUILDTYPE=Release chrome"

TARGET_CFLAGS += "-I${STAGING_INCDIR}/mozilla/nss -I${STAGING_INCDIR}/dst"
TARGET_CXXFLAGS += "-I${STAGING_INCDIR}/mozilla/nss -I${STAGING_INCDIR}/dst"

TARGET_CC_ARCH += " -Wno-error=deprecated-declarations"

do_compile_prepend() {
	export CROSSTOOL=${TARGET_PREFIX}
	export AR=${CROSSTOOL}ar
	export AS=${CROSSTOOL}as
	export RANLIB=${CROSSTOOL}ranlib
	# host tools are supposed to be linked with gold so we hack the
	# makefiles to use gold which must be present on the host and named
	# ld.gold.
	# -fuse-ld=gold could be an option but that actually fails on Fedora
	ln -sf `which ld.gold` ${WORKDIR}/ld
	sed -i "s#LDFLAGS.host ?=#LDFLAGS.host = -B${WORKDIR}/#g" Makefile
	for i in `find . -iname *.host*.mk`; do 
		sed -i "s#-B\$(builddir)/../../third_party/gold#-B${WORKDIR}/#g" $i
	done
	# chromium seems to force the usage of its internal copy of a binary
	# gold linker so remove this setting
	for i in `find . -iname *.target*.mk`; do 
		sed -i "s#-B\$(builddir)/../../third_party/gold# #g" $i
	done
}

do_install() {
	install -d ${D}${bindir}
	install -d ${D}${bindir}/chrome/
	install -m 0755 ${S}/out/Release/chrome ${D}${bindir}/chrome/chrome
	install -m 0644 ${S}/out/Release/chrome.pak ${D}${bindir}/chrome/
	install -m 0644 ${S}/out/Release/resources.pak ${D}${bindir}/chrome/
	install -m 0644 ${S}/out/Release/product_logo_48.png ${D}${bindir}/chrome/
	install -d ${D}${bindir}/chrome/locales/
	install -m 0644 ${S}/out/Release/locales/en-US.pak ${D}${bindir}/chrome/locales
	cp -a ${S}/out/Release/obj ${D}${bindir}/chrome/
	cp -a ${S}/out/Release/obj.target ${D}${bindir}/chrome/
	cp -a ${S}/out/Release/resources ${D}${bindir}/chrome/

	mv ${D}${bindir}/chrome/obj.target/third_party/ffmpeg/libffmpegsumo.so ${D}/${bindir}/chrome/libffmpegsumo.so

	find ${D}${bindir}/chrome/ -name "*.d" -delete
	find ${D}${bindir}/chrome/ -name "*.o" -delete
	find ${D}${bindir}/chrome/ -name "*.a" -delete
	find ${D}${bindir}/chrome/ -name "*.cpp" -delete
	find ${D}${bindir}/chrome/ -name "*.h" -delete
	find ${D}${bindir}/chrome/ -name "*.cc" -delete
}

# FIXME : hundred of WARNINGs
FILES_${PN} = "/usr/bin/chrome/"
FILES_${PN}-dbg = " \
	${bindir}/chrome/.debug \
	${bindir}/chrome/obj.target/third_party/WebKit/Source/WebKit/chromium/.debug \
	${bindir}/chrome/obj.target/third_party/ffmpeg/.debug \
	${bindir}/chrome/obj.target/third_party/angle/src/.debug \
	${bindir}/chrome/obj.target/third_party/icu/.debug \
	${bindir}/chrome/obj.target/third_party/icu/.debug \
	${bindir}/chrome/obj.target/content/.debug \
	${bindir}/chrome/obj.target/webkit/support/.debug \
	${bindir}/chrome/obj.target/webkit/support/.debug \
	${bindir}/chrome/obj.target/webkit/support/.debug \
	${bindir}/chrome/obj.target/ui/gfx/surface/.debug \
	${bindir}/chrome/obj.target/ui/gfx/gl/.debug \
	${bindir}/chrome/obj.target/ui/.debug \
	${bindir}/chrome/obj.target/sql/.debug \
	${bindir}/chrome/obj.target/v8/tools/gyp/.debug \
	${bindir}/chrome/obj.target/printing/.debug \
	${bindir}/chrome/obj.target/crypto/.debug \
	${bindir}/chrome/obj.target/net/.debug \
	${bindir}/chrome/obj.target/base/.debug \
	${bindir}/chrome/obj.target/base/.debug \
	${bindir}/chrome/obj.target/ipc/.debug \
	${bindir}/chrome/obj.target/media/.debug \
	${bindir}/chrome/obj.target/skia/.debug \
	${bindir}/chrome/obj.target/gpu/command_buffer/.debug \
	${bindir}/chrome/obj.target/gpu/.debug \
	${bindir}/chrome/obj.target/ppapi/.debug \
	${bindir}/chrome/obj.target/ppapi/.debug \
	${bindir}/chrome/obj.target/build/temp_gyp/.debug \
"
