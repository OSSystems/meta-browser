require electron.inc

# Override the compile target for Electron
do_compile() {
	ninja -v ${PARALLEL_MAKE} electron
}
do_compile[progress] = "outof:^\[(\d+)/(\d+)\]\s+"

# Override the install task for Electron-specific installation
do_install() {
	install -d ${D}${bindir}
	install -d ${D}${libdir}/electron
	install -d ${D}${libdir}/electron/locales
	install -d ${D}${libdir}/electron/resources

	install -m 0755 electron ${D}${libdir}/electron/electron
	ln -s ${libdir}/electron/electron ${D}${bindir}/electron
	install -m 0644 resources/default_app.asar ${D}${libdir}/electron/resources/default_app.asar
	install -m 0644 *.bin ${D}${libdir}/electron/
	install -m 0644 icudtl.dat ${D}${libdir}/electron/icudtl.dat

	# Chromium *.pak files
	install -m 0644 chrome_*.pak ${D}${libdir}/electron/
	install -m 0644 resources.pak ${D}${libdir}/electron/resources.pak

	# Locales.
	install -m 0644 locales/*.pak ${D}${libdir}/electron/locales/

	install -m 0755 libffmpeg.so ${D}${libdir}/electron/

	# ANGLE.
	if [ -e libEGL.so ]; then
		install -m 0644 libEGL.so ${D}${libdir}/electron/
		install -m 0644 libGLESv2.so ${D}${libdir}/electron/
	fi

	# libvulkan (for ANGLE).
	if [ -e libvulkan.so.1 ]; then
		install -m 0644 libvulkan.so.1 ${D}${libdir}/electron/
	fi

	# Swiftshader VK.
	if [ -e libvk_swiftshader.so ]; then
		install -m 0644 libvk_swiftshader.so ${D}${libdir}/electron/
		# This is needed for ANGLE to find libvk_swiftshader.so.
		install -m 0644 vk_swiftshader_icd.json ${D}${libdir}/electron/
	fi

	if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'component-build', 'component-build', '', d)}" ]; then
		install -m 0755 *.so ${D}${libdir}/electron/
	fi

	# When building chromium with use_system_minigbm=false,
	# libminigbm.so does not seem to get linked in statically.
	# So we simply check whether it exists in all cases and ship it.
	if [ -e libminigbm.so ]; then
		install -m 0755 libminigbm.so ${D}${libdir}/electron/
	fi
}

# Electron-specific package files
FILES:${PN} = " \
        ${bindir}/electron \
        ${libdir}/electron/* \
"

PACKAGE_DEBUG_SPLIT_STYLE = "debug-without-src"

# There is no need to ship empty -dev packages.
ALLOW_EMPTY:${PN}-dev = "0"

# ERROR: QA Issue: lib32-chromium-ozone-wayland: ELF binary /usr/bin/chromium has relocations in .text [textrel]
INSANE_SKIP:${PN}:append:x86 = "textrel"

PACKAGECONFIG:append = " proprietary-codecs"
