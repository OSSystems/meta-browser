DESCRIPTION = "Chromium browser"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=537e0b52077bf0a616d0a0c8a79bc9d5"
DEPENDS = "xz-native pciutils pulseaudio xextproto cairo nss gtk+ zlib-native libav libxi libgnome-keyring libxss cups ninja-native gconf"
SRC_URI = "\
        http://gsdview.appspot.com/chromium-browser-official/${P}.tar.xz \
        file://include.gypi \
        file://oe-defaults.gypi \
        file://unistd-2.patch \
        file://google-chrome \
        file://google-chrome.desktop \
"
SRC_URI[md5sum] = "c0659bc3c6b540e106e043fd27f54358"
SRC_URI[sha256sum] = "666d5948c6508072f9f5d6acff82290fa5939e1da1b94b042a1e05daf3357b61"

# include.gypi exists only for armv6 and armv7a and there isn't something like COMPATIBLE_ARCH afaik
COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE_i586 = "(.*)"
COMPATIBLE_MACHINE_x86-64 = "(.*)"
COMPATIBLE_MACHINE_armv6 = "(.*)"
COMPATIBLE_MACHINE_armv7a = "(.*)"

inherit gettext

EXTRA_OEGYP =	" \
	${@base_contains('DISTRO_FEATURES', 'ld-is-gold', '', '-Dlinux_use_gold_binary=0', d)} \
	${@base_contains('DISTRO_FEATURES', 'ld-is-gold', '', '-Dlinux_use_gold_flags=0', d)} \
	-I ${WORKDIR}/oe-defaults.gypi \
	-I ${WORKDIR}/include.gypi \
	-f ninja \
"
ARMFPABI_armv7a = "${@bb.utils.contains('TUNE_FEATURES', 'callconvention-hard', 'arm_float_abi=hard', 'arm_float_abi=softfp', d)}"

export GYP_DEFINES="${ARMFPABI} release_extra_cflags='-Wno-error=unused-local-typedefs' sysroot=''"
do_configure() {
	cd ${S}
	# replace LD with CXX, to workaround a possible gyp issue?
	LD="${CXX}" export LD
	CC="${CC}" export CC
	CXX="${CXX}" export CXX
	CC_host="gcc" export CC_host
	CXX_host="g++" export CXX_host
	build/gyp_chromium --depth=. ${EXTRA_OEGYP}
}

do_compile() {
	# build with ninja
	ninja -C ${S}/out/Release chrome chrome_sandbox
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}/google-chrome ${D}${bindir}/

	install -d ${D}${datadir}/applications
	install -m 0644 ${WORKDIR}/google-chrome.desktop ${D}${datadir}/applications/

	install -d ${D}${bindir}/chrome/
	install -m 0755 ${S}/out/Release/chrome ${D}${bindir}/chrome/chrome
	install -m 0644 ${S}/out/Release/resources.pak ${D}${bindir}/chrome/
	install -m 0644 ${S}/out/Release/icudtl.dat ${D}${bindir}/chrome/
	install -m 0644 ${S}/out/Release/content_resources.pak ${D}${bindir}/chrome/
	install -m 0644 ${S}/out/Release/keyboard_resources.pak ${D}${bindir}/chrome/
	install -m 0644 ${S}/out/Release/chrome_100_percent.pak ${D}${bindir}/chrome/
	install -m 0644 ${S}/out/Release/product_logo_48.png ${D}${bindir}/chrome/
	install -m 0755 ${S}/out/Release/libffmpegsumo.so ${D}${bindir}/chrome/

	install -d ${D}${sbindir}
	install -m 4755 ${S}/out/Release/chrome_sandbox ${D}${sbindir}/chrome-devel-sandbox

	install -d ${D}${bindir}/chrome/locales/
	install -m 0644 ${S}/out/Release/locales/en-US.pak ${D}${bindir}/chrome/locales
}

FILES_${PN} = "${bindir}/chrome/ ${bindir}/google-chrome ${datadir}/applications ${sbindir}/"
FILES_${PN}-dbg += "${bindir}/chrome/.debug/"

PACKAGE_DEBUG_SPLIT_STYLE = "debug-without-src"

