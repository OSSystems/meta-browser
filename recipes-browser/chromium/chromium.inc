LICENSE = "BSD"
DEPENDS = "xz-native pciutils pulseaudio cairo nss zlib-native cups ninja-native gconf libexif pango libdrm"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE_x86 = "(.*)"
COMPATIBLE_MACHINE_x86-64 = "(.*)"
COMPATIBLE_MACHINE_armv6 = "(.*)"
COMPATIBLE_MACHINE_armv7a = "(.*)"
COMPATIBLE_MACHINE_armv7ve = "(.*)"
COMPATIBLE_MACHINE_aarch64 = "(.*)"

# The build type defaults to Release. If you want a Debug build, you can set
# the variable CHROMIUM_BUILD_TYPE to "Debug" in your conf/local.conf file.
CHROMIUM_BUILD_TYPE ??= "Release"

inherit pythonnative

GYP_DEFINES += "release_extra_cflags='-Wno-error=unused-local-typedefs' sysroot='' \
	${@bb.utils.contains("AVAILTUNES", "mips", "", "release_extra_cflags='-fno-delete-null-pointer-checks'", d)}"
GYP_DEFINES_append_x86 = " generate_character_data=0"

# Disable activation of field trial tests that can cause problems in
# production.
# See https://groups.google.com/a/chromium.org/d/msg/chromium-packagers/ECWC57W7E0k/9Kc5UAmyDAAJ
GYP_DEFINES += "-Dfieldtrial_testing_like_official_build=1"

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
}

do_install() {
	if [ -f "${WORKDIR}/google-chrome" ]; then
		install -Dm 0755 ${WORKDIR}/google-chrome ${D}${bindir}/google-chrome
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/chrome_sandbox" ]; then
		install -Dm 4755 ${B}/out/${CHROMIUM_BUILD_TYPE}/chrome_sandbox ${D}${sbindir}/chrome-devel-sandbox
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/chrome" ]; then
		install -Dm 0755 ${B}/out/${CHROMIUM_BUILD_TYPE}/chrome ${D}${bindir}/${BPN}/chrome
	fi
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/chromedriver" ]; then
		install -Dm 0755 ${B}/out/${CHROMIUM_BUILD_TYPE}/chromedriver ${D}${bindir}/chromedriver
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
	if [ -f "${B}/out/${CHROMIUM_BUILD_TYPE}/libosmesa.so" ]; then
		install -Dm 0644 ${B}/out/${CHROMIUM_BUILD_TYPE}/libosmesa.so ${D}${libdir}/${BPN}/libosmesa.so
	fi

	# Chromium *.pak files
	for f in $(cd ${B}/out/${CHROMIUM_BUILD_TYPE}/ && find . -type f -name \*.pak); do
			install -Dm 0644 "${B}/out/${CHROMIUM_BUILD_TYPE}/${f}" "${D}${bindir}/${BPN}/${f}"
	done

	# Chromium resource files (for the inspector).
	for f in $(cd ${B}/out/${CHROMIUM_BUILD_TYPE}/ && test -d resources && find resources -type f); do
			install -Dm 0644 "${B}/out/${CHROMIUM_BUILD_TYPE}/${f}" "${D}${bindir}/${BPN}/${f}"
	done

}

PACKAGES =+ "${PN}-chromedriver"

FILES_${PN}-chromedriver = "${bindir}/chromedriver"

FILES_${PN} = "${bindir}/${BPN} ${datadir}/applications ${sbindir}/ ${libdir}/${BPN}/"
FILES_${PN} += "${bindir} ${libdir}"
FILES_${PN} += "${bindir}/${BPN}/*.pak"
FILES_${PN} += "${bindir}/${BPN}/locales/*.pak"

FILES_${PN}-dbg += "${bindir}/${BPN}/.debug/ ${libdir}/${BPN}/.debug/ ${libdir}/.debug/"

PACKAGE_DEBUG_SPLIT_STYLE = "debug-without-src"

# chromium-54.0.2810.2: ELF binary 'i586-oe-linux/chromium/54.0.2810.2-r0/packages-split/chromium/usr/bin/chromium/chrome' has relocations in .text [textrel]
INSANE_SKIP_${PN} = "ldflags textrel"
SOLIBS = ".so"
FILES_SOLIBSDEV = ""
