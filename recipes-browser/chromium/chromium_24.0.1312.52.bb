DESCRIPTION = "Chromium browser"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0750f191c9bbf46869b70508e7eb455b"
DEPENDS = "xextproto cairo nss gtk+ zlib-native libav libxi libgnome-keyring libxss cups ninja-native gconf"

SRC_URI = "http://commondatastorage.googleapis.com/chromium-browser-official/${P}.tar.bz2 \
        file://include.gypi \
        file://oe-defaults.gypi \
        file://unistd-2.patch \
        file://glib-2.16-use-siginfo_t.patch \
        file://ui-gl-no-narrowing.patch \
        file://google-chrome \
        file://google-chrome.desktop \
        file://0001-browser_main_loop.cc-fix-build-with-glib-2.35.patch \
"

PR = "r2"

# include.gypi exists only for armv6 and armv7a and there isn't something like COMPATIBLE_ARCH afaik
COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE_i586 = "(.*)"
COMPATIBLE_MACHINE_x86-64 = "(.*)"
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
ARMFPABI_arm = "${@bb.utils.contains('TUNE_FEATURES', 'callconvention-hard', 'arm_float_abi=hard', 'arm_float_abi=softfp', d)}"
export GYP_DEFINES="${ARMFPABI} release_extra_cflags='-Wno-error=unused-local-typedefs'"
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
