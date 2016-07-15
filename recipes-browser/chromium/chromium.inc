LICENSE = "BSD"
DEPENDS = "xz-native pciutils pulseaudio cairo nss zlib-native libav cups ninja-native gconf libexif pango libdrm"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE_x86 = "(.*)"
COMPATIBLE_MACHINE_x86-64 = "(.*)"
COMPATIBLE_MACHINE_armv6 = "(.*)"
COMPATIBLE_MACHINE_armv7a = "(.*)"

# The build type defaults to Release. If you want a Debug build, you can set
# the variable CHROMIUM_BUILD_TYPE to "Debug" in your conf/local.conf file.
CHROMIUM_BUILD_TYPE ??= "Release"

inherit gettext pythonnative

ARMFPABI_armv7a = "${@bb.utils.contains('TUNE_FEATURES', 'callconvention-hard', 'arm_float_abi=hard', 'arm_float_abi=softfp', d)}"
GYP_DEFINES += "${ARMFPABI} release_extra_cflags='-Wno-error=unused-local-typedefs' sysroot=''"

do_configure() {
	cd ${S}
	GYP_DEFINES="${GYP_DEFINES}" export GYP_DEFINES
	# replace LD with CXX, to workaround a possible gyp issue?
	LD="${CXX}" export LD
	CC="${CC}" export CC
	CXX="${CXX}" export CXX
	CC_host="${BUILD_CC}" export CC_host
	CXX_host="${BUILD_CXX}" export CXX_host
}

do_install() {
	if [ -f "${WORKDIR}/google-chrome" ]; then
		install -Dm 0755 ${WORKDIR}/google-chrome ${D}${bindir}/google-chrome
	fi
	if [ -f "${WORKDIR}/cef-simple" ]; then
		install -Dm 0755 ${WORKDIR}/cef-simple ${D}${bindir}/cef-simple
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/chrome_sandbox" ]; then
		install -Dm 4755 ${B}/out/${CHROMIUM_BUILD_TYPE}/chrome_sandbox ${D}${sbindir}/chrome-devel-sandbox
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/cefsimple" ]; then
		install -Dm 0755 ${B}/out/${CHROMIUM_BUILD_TYPE}/cefsimple ${D}${bindir}/${BPN}/cefsimple
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/lib/libcef.so" ]; then
		install -Dm 0755 ${B}/out/${CHROMIUM_BUILD_TYPE}/lib/libcef.so ${D}${libdir}/${BPN}/libcef.so
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/chrome" ]; then
		install -Dm 0755 ${B}/out/${CHROMIUM_BUILD_TYPE}/chrome ${D}${bindir}/${BPN}/chrome
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/icudtl.dat" ]; then
		install -Dm 0644 ${B}/out/${CHROMIUM_BUILD_TYPE}/icudtl.dat ${D}${bindir}/${BPN}/icudtl.dat
	fi
	if [ -f "${WORKDIR}/google-chrome.desktop" ]; then
		install -Dm 0644 ${WORKDIR}/google-chrome.desktop ${D}${datadir}/applications/google-chrome.desktop
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/product_logo_48.png" ]; then
		install -Dm 0644 ${B}/out/${CHROMIUM_BUILD_TYPE}/product_logo_48.png ${D}${bindir}/${BPN}/product_logo_48.png
	fi

	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/natives_blob.bin" ]; then
		install -Dm 0644 ${B}/out/${CHROMIUM_BUILD_TYPE}/natives_blob.bin ${D}${bindir}/${BPN}/natives_blob.bin
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/snapshot_blob.bin" ]; then
		install -Dm 0644 ${B}/out/${CHROMIUM_BUILD_TYPE}/snapshot_blob.bin ${D}${bindir}/${BPN}/snapshot_blob.bin
	fi

	# Chromium plugins libs
	for f in libpdf.so libosmesa.so libffmpegsumo.so; do
		if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/$f" ]; then
			install -Dm 0644 ${B}/out/${CHROMIUM_BUILD_TYPE}/$f ${D}${libdir}/${BPN}/$f
		fi
	done

	# Chromium *.pak files and CEF pak files ( prefixed with cef_ )
	for f in $(cd ${B}/out/${CHROMIUM_BUILD_TYPE}/ && find . -type f -name \*.pak); do
			install -Dm 0644 "${B}/out/${CHROMIUM_BUILD_TYPE}/${f}" "${D}${bindir}/${BPN}/${f}"
	done

	# Chromium resource files (for the inspector).
	for f in $(cd ${B}/out/${CHROMIUM_BUILD_TYPE}/ && test -d resources && find resources -type f); do
			install -Dm 0644 "${B}/out/${CHROMIUM_BUILD_TYPE}/${f}" "${D}${bindir}/${BPN}/${f}"
	done

}

PACKAGES =+ "${PN}-codecs-ffmpeg ${PN}-plugin-pdf"

FILES_${PN}-codecs-ffmpeg = "${libdir}/${BPN}/libffmpegsumo.so"
FILES_${PN}-plugin-pdf = "${libdir}/${BPN}/libpdf.so"

FILES_${PN} = "${bindir}/${BPN} ${datadir}/applications ${sbindir}/ ${libdir}/${BPN}/"
FILES_${PN} += "${bindir} ${libdir}"
FILES_${PN} += "${bindir}/${BPN}/*.pak"
FILES_${PN} += "${bindir}/${BPN}/locales/*.pak"

FILES_${PN}-dbg += "${bindir}/${BPN}/.debug/ ${libdir}/${BPN}/.debug/ ${libdir}/.debug/"

PACKAGE_DEBUG_SPLIT_STYLE = "debug-without-src"

INSANE_SKIP_${PN} = "ldflags"
SOLIBS = ".so"
FILES_SOLIBSDEV = ""
