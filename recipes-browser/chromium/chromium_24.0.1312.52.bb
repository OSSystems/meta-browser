DESCRIPTION = "Chromium browser"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0750f191c9bbf46869b70508e7eb455b"
DEPENDS = "xextproto cairo nss gtk+-native zlib-native libav libxi libgnome-keyring libxss cups ninja-native"

SRC_URI = "http://commondatastorage.googleapis.com/chromium-browser-official/${P}.tar.bz2 \
        file://include.gypi \
	file://oe-defaults.gypi \
	file://unistd-2.patch;patch=1 \
	file://glib-2.16-use-siginfo_t.patch;patch=1 \
	file://ui-gl-no-narrowing.patch;patch=1 \
"

PR="r1"

# include.gypi exists only for armv6 and armv7a and there isn't something like COMPATIBLE_ARCH afaik
COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE_i586 = "(.*)"
COMPATIBLE_MACHINE_armv6 = "(.*)"
COMPATIBLE_MACHINE_armv7a = "(.*)"

SRC_URI[md5sum] = "ed027b16b20e673af6dcf1831a6d8652"
SRC_URI[sha256sum] = "f30df8b0f4c999185bb11d873f0caa5d8bc581eb4f37ce0fe85fb7900a01d711"

inherit gettext

EXTRA_OEGYP =	" \
	${@base_contains('DISTRO_FEATURES', 'ld-is-gold', '', '-Dlinux_use_gold_binary=0', d)} \
	${@base_contains('DISTRO_FEATURES', 'ld-is-gold', '', '-Dlinux_use_gold_flags=0', d)} \
	-I ${WORKDIR}/oe-defaults.gypi \
	-I ${WORKDIR}/include.gypi \
	-f ninja \
"

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

FILES_${PN} = "${bindir}/chrome/"
FILES_${PN}-dbg = "${bindir}/chrome/.debug/"
