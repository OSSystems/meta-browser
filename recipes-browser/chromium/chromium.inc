LICENSE = "BSD"
DEPENDS = "xz-native pciutils pulseaudio cairo nss zlib-native libav cups ninja-native gconf libexif pango libdrm"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE_i586 = "(.*)"
COMPATIBLE_MACHINE_x86-64 = "(.*)"
COMPATIBLE_MACHINE_armv6 = "(.*)"
COMPATIBLE_MACHINE_armv7a = "(.*)"

export CHROMIUM_BUILD_TYPE="Release"

inherit gettext

PACKAGECONFIG ??= "use-egl"

# this makes sure the dependencies for the EGL mode are present; otherwise, the configure scripts
# automatically and silently fall back to GLX
PACKAGECONFIG[use-egl] = ",,virtual/egl virtual/libgles2"

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
	install -d ${D}${bindir}
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/cefsimple" ]; then
		install -m 0755 ${B}/out/${CHROMIUM_BUILD_TYPE}/cefsimple ${D}${bindir}
	fi
	if [ -f "${WORKDIR}/google-chrome" ]; then
		install -m 0755 ${WORKDIR}/google-chrome ${D}${bindir}/
	fi
	install -d ${D}${datadir}/applications
	if [ -f "${WORKDIR}/google-chrome.desktop" ]; then
		install -m 0644 ${WORKDIR}/google-chrome.desktop ${D}${datadir}/applications/
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/icudtl.dat" ]; then
		install -m 0644 ${B}/out/${CHROMIUM_BUILD_TYPE}/icudtl.dat ${D}${bindir}
	fi
	install -d ${D}${libdir}
        if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/libcef.so" ]; then
		install -m 0755 ${B}/out/${CHROMIUM_BUILD_TYPE}/lib/libcef.so ${D}${libdir}
        fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/libosmesa.so" ]; then
		install -m 0755 ${B}/out/${CHROMIUM_BUILD_TYPE}/libosmesa.so ${D}${libdir}
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/libffmpegsumo.so" ]; then
		install -m 0755 ${B}/out/${CHROMIUM_BUILD_TYPE}/libffmpegsumo.so ${D}${libdir}
	fi
	install -d ${D}${bindir}/chrome
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/chrome" ]; then
		install -m 0755 ${B}/out/${CHROMIUM_BUILD_TYPE}/chrome ${D}${bindir}/chrome/chrome
	fi
	#Chromium *.pak files

	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/content_resources.pak" ]; then
		install -m 0644 ${B}/out/${CHROMIUM_BUILD_TYPE}/content_resources.pak ${D}${bindir}/chrome
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/keyboard_resources.pak" ]; then
		install -m 0644 ${B}/out/${CHROMIUM_BUILD_TYPE}/keyboard_resources.pak ${D}${bindir}/chrome
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/chrome_100_percent.pak" ]; then
		install -m 0644 ${B}/out/${CHROMIUM_BUILD_TYPE}/chrome_100_percent.pak ${D}${bindir}/chrome
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/product_logo_48.png" ]; then
		install -m 0644 ${S}/out/${CHROMIUM_BUILD_TYPE}/product_logo_48.png ${D}${bindir}/chrome/
    	fi

    	# CEF *.pak files

	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/cef_100_percent.pak" ]; then
		install -m 0644 ${B}/out/${CHROMIUM_BUILD_TYPE}/cef_100_percent.pak ${D}${bindir}/chrome
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/cef_200_percent.pak" ]; then
		install -m 0644 ${B}/out/${CHROMIUM_BUILD_TYPE}/cef_200_percent.pak ${D}${bindir}/chrome
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/cef_resources.pak" ]; then
		install -m 0644 ${B}/out/${CHROMIUM_BUILD_TYPE}/cef_resources.pak ${D}${bindir}/chrome
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/cef.pak" ]; then
		install -m 0644 ${B}/out/${CHROMIUM_BUILD_TYPE}/cef.pak ${D}${bindir}/chrome
	fi
	install -d ${D}${bindir}/chrome/locales
	install -m 0644 ${B}/out/${CHROMIUM_BUILD_TYPE}/locales/en-US.pak ${D}${bindir}/chrome/locales

	install -d ${D}${sbindir}
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/chrome_sandbox" ]; then
		install -m 4755 ${B}/out/${CHROMIUM_BUILD_TYPE}/chrome_sandbox ${D}${sbindir}/chrome-devel-sandbox
	fi

	# take care of yocto-way libraries naming (versions)
	cd ${D}${libdir}
	for library in $(find -type f -name '*.so'); do
		startDir="$(pwd)"
		cd "$(dirname "$library")"
		rm -f "$library.0.0.1"
		mv "$library" "$library.0.0.1"
		ln -sf "$library.0.0.1" "$library.0.0"
		ln -sf "$library.0.0" "$library.0"
		ln -sf "$library.0" "$library"
		cd "$startDir"
	done

}


FILES_${PN} = "${bindir}/chrome/ ${bindir}/google-chrome ${datadir}/applications ${sbindir}/ ${libdir}/chrome/"
FILES_${PN}-dbg += "${bindir}/chrome/.debug/ ${libdir}/chrome/.debug/"

PACKAGE_DEBUG_SPLIT_STYLE = "debug-without-src"

INSANE_SKIP_${PN} = "ldflags"
FILES_SOLIBSDEV = ""
FILES_${PN} += "${bindir} ${bindir}/chrome/ ${libdir}"
FILES_${PN} += "${bindir}/chrome/*.pak"
FILES_${PN} += "${bindir}/chrome/locales/*.pak"
FILES_${PN}-dbg += "${bindir}/chrome/.debug/ ${libdir}/.debug/"



