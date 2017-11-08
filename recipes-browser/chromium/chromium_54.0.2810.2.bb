require chromium-browser.inc

inherit distro_features_check gtk-icon-cache

OUTPUT_DIR = "out/${CHROMIUM_BUILD_TYPE}"
B = "${S}/${OUTPUT_DIR}"

# Append instead of assigning; the gtk-icon-cache class inherited above also
# adds packages to DEPENDS.
DEPENDS += " \
    alsa-lib \
    atk \
    bison-native \
    cairo \
    dbus \
    expat \
    freetype \
    glib-2.0 \
    gperf-native \
    gtk+ \
    libdrm \
    libx11 \
    libxcomposite \
    libxcursor \
    libxdamage \
    libxext \
    libxfixes \
    libxi \
    libxrandr \
    libxrender \
    libxscrnsaver \
    libxtst \
    ninja-native \
    nss \
    pango \
    pciutils \
    ${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', 'pulseaudio', '', d)} \
    virtual/libgl \
"
DEPENDS_append_libc-musl = " libexecinfo"

# The wrapper script we use from upstream requires bash.
RDEPENDS_${PN} = "bash"

SRC_URI += "\
        file://add_missing_stat_h_include.patch \
        file://0003-Remove-hard-coded-values-for-CC-and-CXX.patch \
        file://0005-Override-root-filesystem-access-restriction.patch \
        file://0011-Replace-readdir_r-with-readdir.patch \
        ${@bb.utils.contains('PACKAGECONFIG', 'ignore-lost-context', 'file://0001-Remove-accelerated-Canvas-support-from-blacklist.patch', '', d)} \
        file://m32.patch \
"

LIC_FILES_CHKSUM = "file://LICENSE;md5=0fca02217a5d49a14dfe2d11837bb34d"
SRC_URI[md5sum] = "3f596ecbd6a39d5ada29f11780ec6dcf"
SRC_URI[sha256sum] = "f038e72cbd8b7383d13c286329623fda8d6d48f45fa2d964e554b5565283ad71"

# For now, we need X11 for Chromium to build and run.
REQUIRED_DISTRO_FEATURES = "x11"

# These are present as their own variables, since they have changed between versions
# a few times in the past already; making them variables makes it easier to handle that
CHROMIUM_X11_GYP_DEFINES ?= ""

python() {
    d.appendVar('GYP_DEFINES', ' %s ' % d.getVar('CHROMIUM_X11_GYP_DEFINES', True))
}

do_configure_prepend_libc-musl() {
	for f in `find ${S}/third_party/ffmpeg/chromium/config/{Chrome,Chromium}/linux/ -name config.h -o -name config.asm`; do
		sed -i -e "s:define HAVE_SYSCTL 1:define HAVE_SYSCTL 0:g" $f
	done
	sed -i -e "s:define HAVE_STRUCT_MALLINFO 1:/*undef HAVE_STRUCT_MALLINFO */:g" ${S}/third_party/tcmalloc/chromium/src/config_linux.h
}

do_configure() {
	cd ${S}
	GYP_DEFINES="${GYP_DEFINES}" export GYP_DEFINES
	# replace LD with CXX, to workaround a possible gyp issue?
	LD="${CXX}" export LD
	CC="${CC}" export CC
	CXX="${CXX}" export CXX
	CC_host="${BUILD_CC}" export CC_host
	CXX_host="${BUILD_CXX}" export CXX_host

	build/gyp_chromium --depth=. ${EXTRA_OEGYP}
}

do_compile() {
	ninja -v ${PARALLEL_MAKE} chrome chrome_sandbox chromedriver
}
do_compile[progress] = "outof:^\[(\d+)/(\d+)\]\s+"

do_install() {
	install -d ${D}${bindir}
	install -d ${D}${datadir}
	install -d ${D}${datadir}/applications
	install -d ${D}${datadir}/icons
	install -d ${D}${datadir}/icons/hicolor
	install -d ${D}${libdir}/chromium
	install -d ${D}${libdir}/chromium/locales

	install -m 4755 chrome_sandbox ${D}${libdir}/chromium/chrome-sandbox
	install -m 0755 chrome ${D}${libdir}/chromium/chromium-bin
	install -m 0644 icudtl.dat ${D}${libdir}/chromium/icudtl.dat

	# Process and install Chromium's template .desktop file.
	sed -e "s,@@MENUNAME@@,Chromium Browser,g" \
	    -e "s,@@PACKAGE@@,chromium,g" \
	    -e "s,@@USR_BIN_SYMLINK_NAME@@,chromium,g" \
	    ${S}/chrome/installer/linux/common/desktop.template > chromium.desktop
	install -m 0644 chromium.desktop ${D}${datadir}/applications/chromium.desktop

	# Install icons.
	for size in 16 22 24 32 48 64 128 256; do
		install -d ${D}${datadir}/icons/hicolor/${size}x${size}
		install -d ${D}${datadir}/icons/hicolor/${size}x${size}/apps
		for dirname in "chromium" "default_100_percent/chromium"; do
			icon="${S}/chrome/app/theme/${dirname}/product_logo_${size}.png"
			if [ -f "${icon}" ]; then
				install -m 0644 "${icon}" \
					${D}${datadir}/icons/hicolor/${size}x${size}/apps/chromium.png
			fi
		done
	done

	# A wrapper for the proprietary Google Chrome version already exists.
	# We can just use that one instead of reinventing the wheel.
	WRAPPER_FILE=${S}/chrome/installer/linux/common/wrapper
	sed -e "s,@@CHANNEL@@,stable,g" \
		-e "s,@@DEFAULT_FLAGS@@ ,,g" \
		-e "s,@@PROGNAME@@,chromium-bin,g" \
		${WRAPPER_FILE} > chromium-wrapper
	install -m 0755 chromium-wrapper ${D}${libdir}/chromium/chromium-wrapper
	ln -s ${libdir}/chromium/chromium-wrapper ${D}${bindir}/chromium

	# Chromium *.pak files
	install -m 0644 chrome_*.pak ${D}${libdir}/chromium/
	install -m 0644 resources.pak ${D}${libdir}/chromium/resources.pak

	# Locales.
	install -m 0644 locales/*.pak ${D}${libdir}/chromium/locales/

	# Add extra command line arguments to the chromium-wrapper script by
	# modifying the dummy "CHROME_EXTRA_ARGS" line
	sed -i "s/^CHROME_EXTRA_ARGS=\"\"/CHROME_EXTRA_ARGS=\"${CHROMIUM_EXTRA_ARGS}\"/" ${D}${libdir}/chromium/chromium-wrapper

	# update ROOT_HOME with the root user's $HOME
	sed -i "s#ROOT_HOME#${ROOT_HOME}#" ${D}${libdir}/chromium/chromium-wrapper

	if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'component-build', 'component-build', '', d)}" ]; then
		install -m 0755 lib/*.so ${D}${libdir}/chromium/
	fi

	# ChromeDriver.
	install -m 0755 chromedriver ${D}${bindir}/chromedriver
}
