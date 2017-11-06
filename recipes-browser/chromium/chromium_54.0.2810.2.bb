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

GYP_DEFINES += "release_extra_cflags='-Wno-error=unused-local-typedefs' sysroot='' \
	${@bb.utils.contains("AVAILTUNES", "mips", "", "release_extra_cflags='-fno-delete-null-pointer-checks'", d)}"
GYP_DEFINES_append_x86 = " generate_character_data=0"

# Disable activation of field trial tests that can cause problems in
# production.
# See https://groups.google.com/a/chromium.org/d/msg/chromium-packagers/ECWC57W7E0k/9Kc5UAmyDAAJ
GYP_DEFINES += "-Dfieldtrial_testing_like_official_build=1"

EXTRA_OEGYP = " \
	${PACKAGECONFIG_CONFARGS} \
	-Dangle_use_commit_id=0 \
	-Dclang=0 \
	-Dhost_clang=0 \
	-Ddisable_fatal_linker_warnings=1 \
	-Dwerror= \
	-Dv8_use_external_startup_data=0 \
	-Dlinux_use_bundled_gold=0 \
	-Dlinux_use_bundled_binutils=0 \
	-Duse_gconf=0 \
	-Duse_pulseaudio=${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', '1', '0', d)} \
	-I ${WORKDIR}/oe-defaults.gypi \
	-I ${WORKDIR}/include.gypi \
	${@bb.utils.contains('PACKAGECONFIG', 'component-build', '-Dcomponent=shared_library', '', d)} \
	${@bb.utils.contains('PACKAGECONFIG', 'proprietary-codecs', '-Dproprietary_codecs=1 -Dffmpeg_branding=Chrome', '', d)} \
	-Dlinux_use_gold_flags=${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', '1', '0', d)} \
	-f ninja \
"

# API keys for accessing Google services. By default, we use an invalid key
# only to prevent the "you are missing an API key" infobar from being shown on
# startup.
# See https://dev.chromium.org/developers/how-tos/api-keys for more information
# on how to obtain your own keys. You can then set the variables below in
# local.conf or a .bbappend file.
GOOGLE_API_KEY ??= "invalid-api-key"
GOOGLE_DEFAULT_CLIENT_ID ??= "invalid-client-id"
GOOGLE_DEFAULT_CLIENT_SECRET ??= "invalid-client-secret"
EXTRA_OEGYP += "\
        -Dgoogle_api_key=${GOOGLE_API_KEY} \
        -Dgoogle_default_client_id=${GOOGLE_DEFAULT_CLIENT_ID} \
        -Dgoogle_default_client_secret=${GOOGLE_DEFAULT_CLIENT_SECRET} \
"

# ARM builds need special additional flags (see ${S}/build/config/arm.gni).
# If we do not pass |arm_arch| and friends to GN, it will deduce a value that
# will then conflict with TUNE_CCARGS and CC.
def get_arm_version(arm_arch):
    import re
    try:
        return re.match(r'armv(\d).*', arm_arch).group(1)
    except IndexError:
        bb.fatal('Unrecognized ARM architecture value: %s' % arm_arch)
def get_compiler_flag(params, param_name, d):
    """Given a sequence of compiler arguments in |params|, returns the value of
    an option |param_name| or an empty string if the option is not present."""
    for param in params:
      if param.startswith(param_name):
        return param.split('=')[1]
    return ''
ARM_ARCH = "${@get_compiler_flag(d.getVar('TUNE_CCARGS').split(), '-march', d)}"
ARM_FLOAT_ABI = "${@bb.utils.contains('TUNE_FEATURES', 'callconvention-hard', 'hard', 'softfp', d)}"
ARM_FPU = "${@get_compiler_flag(d.getVar('TUNE_CCARGS').split(), '-mfpu', d)}"
ARM_TUNE = "${@get_compiler_flag(d.getVar('TUNE_CCARGS').split(), '-mcpu', d)}"
ARM_VERSION = "${@get_arm_version(d.getVar('ARM_ARCH'))}"
GYP_DEFINES_append_arm = " \
        -Darm_arch=${ARM_ARCH} \
        -Darm_float_abi=${ARM_FLOAT_ABI} \
        -Darm_fpu=${ARM_FPU} \
        -Darm_tune=${ARM_TUNE} \
        -Darm_version=${ARM_VERSION} \
"

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

PACKAGES =+ "${PN}-chromedriver"

FILES_${PN}-chromedriver = "${bindir}/chromedriver"

FILES_${PN} = " \
        ${bindir}/${PN} \
        ${datadir}/applications/${PN}.desktop \
        ${datadir}/icons/hicolor/*x*/apps/chromium.png \
        ${libdir}/${PN}/* \
"

PACKAGE_DEBUG_SPLIT_STYLE = "debug-without-src"

# chromium-54.0.2810.2: ELF binary 'i586-oe-linux/chromium/54.0.2810.2-r0/packages-split/chromium/usr/bin/chromium/chrome' has relocations in .text [textrel]
INSANE_SKIP_${PN} = "ldflags textrel"
SOLIBS = ".so"
FILES_SOLIBSDEV = ""

# There is no need to ship empty -dev packages.
ALLOW_EMPTY_${PN}-dev = "0"
