DESCRIPTION = "Chromium browser"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2d164565cc10f298390174d9cb6d18d"
DEPENDS = "xz-native pciutils pulseaudio xextproto cairo nss gtk+ zlib-native libav libxi libgnome-keyring libxss cups ninja-native gconf"
SRC_URI = "\
        http://gsdview.appspot.com/chromium-browser-official/${P}.tar.xz \
        file://include.gypi \
        file://oe-defaults.gypi \
        file://unistd-2.patch \
        file://google-chrome \
        file://google-chrome.desktop \
        file://uninitialised-warning.patch \
"
SRC_URI[md5sum] = "2480c3fd109ef000575629acb8a906ca"
SRC_URI[sha256sum] = "7f08624f7d9bd120de0043da7ee09985ae6b6990d22d8c1b1f7c66fd4ed681a1"

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
	ninja -C ${S}/out/Release chrome 
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}/google-chrome ${D}${bindir}/

	install -d ${D}${datadir}/applications
	install -m 0644 ${WORKDIR}/google-chrome.desktop ${D}${datadir}/applications/

	install -d ${D}${bindir}/chrome/
	install -m 0755 ${S}/out/Release/chrome ${D}${bindir}/chrome/chrome
	install -m 0644 ${S}/out/Release/chrome.pak ${D}${bindir}/chrome/
	install -m 0644 ${S}/out/Release/resources.pak ${D}${bindir}/chrome/
	install -m 0644 ${S}/out/Release/chrome_100_percent.pak ${D}${bindir}/chrome/
	install -m 0644 ${S}/out/Release/product_logo_48.png ${D}${bindir}/chrome/
	install -m 0755 ${S}/out/Release/libffmpegsumo.so ${D}${bindir}/chrome/

	install -d ${D}${bindir}/chrome/locales/
	install -m 0644 ${S}/out/Release/locales/en-US.pak ${D}${bindir}/chrome/locales
}

FILES_${PN} = "${bindir}/chrome/ ${bindir}/google-chrome ${datadir}/applications"
FILES_${PN}-dbg = "${bindir}/chrome/.debug/"
