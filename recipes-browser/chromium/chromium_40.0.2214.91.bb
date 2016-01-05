# Recipe files have to perform the following tasks after including this file:
# 1) Add patches to SRC_URI. Version specific patches should be contained in a
#    "chromium-XX" subdirectory, where XX is the major version. There are also
#    patches that are shared amongst versions but may one day no longer be
#    needed (like unistd2.patch). These do not belong in such a subdirectory,
#    but still need to be explicitely be added. Do NOT add ozone-wayland patches
#    to SRC_URI here!
# 2) Add md5sum and sha256sum hashes of the tarball.
# 3) Add ozone-wayland patches to the OZONE_WAYLAND_EXTRA_PATCHES variable.
#    The rule with the chromium-XX subdirectory also applies here.
# 4) Set the OZONE_WAYLAND_GIT_BRANCH and OZONE_WAYLAND_GIT_SRCREV values.
# 5) Optionally, set values for these variables:
#    * OZONE_WAYLAND_PATCH_FILE_GLOB
#    * CHROMIUM_X11_DEPENDS
#    * CHROMIUM_X11_GYP_DEFINES
#    * CHROMIUM_WAYLAND_DEPENDS
#    * CHROMIUM_WAYLAND_GYP_DEFINES

include chromium.inc
DESCRIPTION = "Chromium browser"
DEPENDS += "libgnome-keyring"
SRC_URI = "\
        http://gsdview.appspot.com/chromium-browser-official/${P}.tar.xz \
        file://include.gypi \
        file://oe-defaults.gypi \
        ${@bb.utils.contains('PACKAGECONFIG', 'component-build', 'file://component-build.gypi', '', d)} \
        file://google-chrome \
        file://google-chrome.desktop \
        file://unistd-2.patch \
        file://chromium-40/fix-build-error-with-GCC-in-Debug-mode.patch \
        file://chromium-40/add_missing_stat_h_include.patch \
        file://chromium-40/0001-bignum.cc-disable-warning-from-gcc-5.patch \
        file://chromium-40/0002-image_util.cc-disable-warning-from-gcc-5.patch \
        file://chromium-40/0004-Remove-hard-coded-values-for-CC-and-CXX.patch \
"

# PACKAGECONFIG options
# ^^^^^^^^^^^^^^^^^^^^^
# * use-egl: (on by default)
#       Without this packageconfig, the Chromium build will use GLX for
#       creating an OpenGL context in X11, and regular OpenGL for painting
#       operations. Neither are desirable on embedded platforms. With this
#       packageconfig, EGL and OpenGL ES 2.x are used instead.
#
# * disable-api-keys-info-bar: (off by default)
#       This disables the info bar that warns: "Google API keys are missing".
#       With some builds, missing API keys are considered OK, so the bar needs
#       to go. Conversely, if Chromium is compiled with this option off and
#       the user wishes to disable the warning, the following lines can be
#       added to the "google-chrome" script (see patchset) before the
#       chromium binary is called:
#           export GOOGLE_API_KEY="no"
#           export GOOGLE_DEFAULT_CLIENT_ID="no"
#           export GOOGLE_DEFAULT_CLIENT_SECRET="no"
#
# * component-build: (off by default)
#       Enables component build mode. By default, all of Chromium (with the
#       exception of FFmpeg) is linked into one big binary. The linker step
#       requires at least 8 GB RAM. Component mode was created to facilitate
#       development and testing, since with it, there is not one big binary;
#       instead, each component is linked to a separate shared object. Use
#       component mode for development, testing, and in case the build machine
#       is not a 64-bit one, or has less than 8 GB RAM.
#
# * ignore-lost-context: (off by default)
#       There is a flaw in the HTML Canvas specification. When the canvas'
#       backing store is some kind of hardware resource like an OpenGL
#       texture, this resource might get lost. In case of OpenGL textures,
#       this happens when the OpenGL context gets lost. The canvas should then
#       be repainted, but nothing in the Canvas standard reflects that. This
#       packageconfig is to be used if the underlying OpenGL (ES) drivers do
#       not lose the context, or if losing the context is considered okay
#       (note that canvas contents can vanish then).
#
# * impl-side-painting: (off by default)
#       This is a new painting mechanism. Still in
#       development stages, it can improve performance See
#       http://www.chromium.org/developers/design-documents/impl-side-painting
#       for more.
#
# * kiosk-mode: (off by default)
#       Enable this option if you want your browser to start up full-screen,
#       without any menu bars, without any clutter, and without any initial
#       start-up indicators.
SRC_URI += "\
        ${@bb.utils.contains('PACKAGECONFIG', 'ignore-lost-context', 'file://chromium-40/0001-Remove-accelerated-Canvas-support-from-blacklist.patch', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'impl-side-painting', 'file://chromium-40/0002-Add-Linux-to-impl-side-painting-whitelist.patch', '', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'disable-api-keys-info-bar', 'file://chromium-40/0003-Disable-API-keys-info-bar.patch', '', d)} \
"
CHROMIUM_EXTRA_ARGS ?= " \
	${@bb.utils.contains('PACKAGECONFIG', 'use-egl', '--use-gl=egl', '', d)} \
	${@bb.utils.contains('PACKAGECONFIG', 'ignore-lost-context', '--gpu-no-context-lost', '', d)} \
	${@bb.utils.contains('PACKAGECONFIG', 'impl-side-painting', '--enable-gpu-rasterization --enable-impl-side-painting', '', d)} \
	${@bb.utils.contains('PACKAGECONFIG', 'kiosk-mode', '--start-fullscreen --kiosk --no-first-run', '', d)} \
"

# Conditionally add ozone-wayland and its patches to the Chromium sources
# You can override this by setting CHROMIUM_ENABLE_WAYLAND=1 or CHROMIUM_ENABLE_WAYLAND=0 in local.conf
CHROMIUM_ENABLE_WAYLAND ??= "${@base_contains('DISTRO_FEATURES', 'x11', '0', \
                     base_contains('DISTRO_FEATURES', 'wayland', '1', \
                     '0', d),d)}"
# Some sanity checks.
python do_check_variables() {
    CHROMIUM_BUILD_TYPE = d.getVar('CHROMIUM_BUILD_TYPE', True)
    CHROMIUM_ENABLE_WAYLAND = d.getVar('CHROMIUM_ENABLE_WAYLAND', True)
    DISTRO_FEATURES = d.getVar("DISTRO_FEATURES", True).split()
    if CHROMIUM_BUILD_TYPE not in ['Release', 'Debug']:
        bb.fatal("Wrong value for CHROMIUM_BUILD_TYPE. Please set it either to \'Release\' or to \'Debug\'")
    if CHROMIUM_ENABLE_WAYLAND not in ['0', '1']:
        bb.fatal("Wrong value for CHROMIUM_ENABLE_WAYLAND. Please set it to \'1\' to enable the feature or to \'0\' to disable it")
    if ( (CHROMIUM_ENABLE_WAYLAND == '1') and ('wayland' not in DISTRO_FEATURES) ):
        bb.warn("You have selected to build Chromium with Wayland support, but you have not enabled wayland in DISTRO_FEATURES")
    if ( (CHROMIUM_ENABLE_WAYLAND != '1') and ('x11' not in DISTRO_FEATURES) ):
        bb.warn("You have selected to build Chromium without Wayland support, but you have not enabled x11 in DISTRO_FEATURES")
    # Print both on log.do_checkvariables and on the console the configuration that is selected.
    # This useful both for throubleshooting and for checking how the build is finally configured.
    if (CHROMIUM_ENABLE_WAYLAND == '1'):
        bb.plain("INFO: Chromium has been configured with Wayland support (ozone-wayland). Build type is \'%s\'" %CHROMIUM_BUILD_TYPE)
    else:
        bb.plain("INFO: Chromium has been configured without Wayland support. Build type is \'%s\'" %CHROMIUM_BUILD_TYPE)
}
addtask check_variables before do_fetch

OZONE_WAYLAND_GIT_DESTSUFFIX = "ozone-wayland-git"
OZONE_WAYLAND_GIT_BRANCH = "Milestone-ThanksGiving"
OZONE_WAYLAND_GIT_SRCREV = "5d7baa9bc3b8c88e9b7e476e3d6bc8cd44a887fe"
SRC_URI += "${@base_conditional('CHROMIUM_ENABLE_WAYLAND', '1', 'git://github.com/01org/ozone-wayland.git;destsuffix=${OZONE_WAYLAND_GIT_DESTSUFFIX};branch=${OZONE_WAYLAND_GIT_BRANCH};rev=${OZONE_WAYLAND_GIT_SRCREV}', '', d)}"
OZONE_WAYLAND_PATCH_FILE_GLOB = "*.patch"

do_unpack[postfuncs] += "${@base_conditional('CHROMIUM_ENABLE_WAYLAND', '1', 'copy_ozone_wayland_files', '', d)}"
do_patch[prefuncs] += "${@base_conditional('CHROMIUM_ENABLE_WAYLAND', '1', 'add_ozone_wayland_patches', '', d)}"

LIC_FILES_CHKSUM = "file://LICENSE;md5=537e0b52077bf0a616d0a0c8a79bc9d5"
SRC_URI[md5sum] = "1f5093bd7e435fdebad070e74bfb3438"
SRC_URI[sha256sum] = "f72fda9ff1ea256ab911610ee532eadf8303137d431f2481d01d3d60e5e64149"

# Variable for extra ozone-wayland patches, typically extended by BSP layer .bbappends
# IMPORTANT: do not simply add extra ozone-wayland patches to the SRC_URI in a
# .bbappend, since the base ozone-wayland patches need to be applied first (see below)
OZONE_WAYLAND_EXTRA_PATCHES = " \
        file://chromium-40/0005-Remove-X-libraries-from-GYP-files.patch \
        file://chromium-40/0010-systemd-218.patch \
"
# using 00*.patch to skip the WebRTC patches in ozone-wayland
# the WebRTC patches remove X11 libraries from the linker flags, which is
# already done by another patch (see above). Furthermore, to be able to use
# these patches, it is necessary to update the git repository in third_party/webrtc,
# which would further complicate this recipe.
OZONE_WAYLAND_PATCH_FILE_GLOB = "00*.patch"

# Component build is broken in ozone-wayland for Chromium 40,
# and is not planned to work again before version 41
python() {
    if (d.getVar('CHROMIUM_ENABLE_WAYLAND', True) == '1'):
        if bb.utils.contains('PACKAGECONFIG', 'component-build', True, False, d):
            bb.fatal("Chromium 40 Wayland version cannot be built in component-mode")
}

copy_ozone_wayland_files() {
	# ozone-wayland sources must be placed in an "ozone"
	# subdirectory in ${S} in order for the .gyp build
	# scripts to work
	cp -r ${WORKDIR}/ozone-wayland-git ${S}/ozone
}

python add_ozone_wayland_patches() {
    import glob
    srcdir = d.getVar('S', True)
    # find all ozone-wayland patches and add them to SRC_URI
    upstream_patches_dir = srcdir + "/ozone/patches"
    upstream_patches = glob.glob(upstream_patches_dir + "/" + d.getVar('OZONE_WAYLAND_PATCH_FILE_GLOB', True))
    upstream_patches.sort()
    for upstream_patch in upstream_patches:
        d.appendVar('SRC_URI', ' file://' + upstream_patch)
    # then, add the extra patches to SRC_URI order matters;
    # extra patches may depend on the base ozone-wayland ones
    d.appendVar('SRC_URI', ' ' + d.getVar('OZONE_WAYLAND_EXTRA_PATCHES'))
}

EXTRA_OEGYP =	" \
	-Dangle_use_commit_id=0 \
	-Dclang=0 \
	-Dhost_clang=0 \
	-Ddisable_fatal_linker_warnings=1 \
	${@base_contains('DISTRO_FEATURES', 'ld-is-gold', '', '-Dlinux_use_gold_binary=0', d)} \
	${@base_contains('DISTRO_FEATURES', 'ld-is-gold', '', '-Dlinux_use_gold_flags=0', d)} \
	-I ${WORKDIR}/oe-defaults.gypi \
	-I ${WORKDIR}/include.gypi \
	${@bb.utils.contains('PACKAGECONFIG', 'component-build', '-I ${WORKDIR}/component-build.gypi', '', d)} \
	-f ninja \
"
ARMFPABI_armv7a = "${@bb.utils.contains('TUNE_FEATURES', 'callconvention-hard', 'arm_float_abi=hard', 'arm_float_abi=softfp', d)}"

GYP_DEFINES += "${ARMFPABI} release_extra_cflags='-Wno-error=unused-local-typedefs' sysroot=''"

# These are present as their own variables, since they have changed between versions
# a few times in the past already; making them variables makes it easier to handle that
CHROMIUM_X11_DEPENDS = "xextproto gtk+ libxi libxss"
CHROMIUM_X11_GYP_DEFINES = ""
CHROMIUM_WAYLAND_DEPENDS = "wayland libxkbcommon"
CHROMIUM_WAYLAND_GYP_DEFINES = "use_ash=1 use_aura=1 chromeos=0 use_ozone=1"

python() {
    if d.getVar('CHROMIUM_ENABLE_WAYLAND', True) == '1':
        d.appendVar('DEPENDS', ' %s ' % d.getVar('CHROMIUM_WAYLAND_DEPENDS', True))
        d.appendVar('GYP_DEFINES', ' %s ' % d.getVar('CHROMIUM_WAYLAND_GYP_DEFINES', True))
    else:
        d.appendVar('DEPENDS', ' %s ' % d.getVar('CHROMIUM_X11_DEPENDS', True))
        d.appendVar('GYP_DEFINES', ' %s ' % d.getVar('CHROMIUM_X11_GYP_DEFINES', True))
}

do_configure_append() {

	build/gyp_chromium --depth=. ${EXTRA_OEGYP}

}

do_compile() {
        # build with ninja
        ninja -C ${S}/out/${CHROMIUM_BUILD_TYPE} ${PARALLEL_MAKE} chrome chrome_sandbox
}


do_install_append() {

	# Add extra command line arguments to google-chrome script by modifying
        # the dummy "CHROME_EXTRA_ARGS" line
        sed -i "s/^CHROME_EXTRA_ARGS=\"\"/CHROME_EXTRA_ARGS=\"${CHROMIUM_EXTRA_ARGS}\"/" ${D}${bindir}/google-chrome

        # update ROOT_HOME with the root user's $HOME
        sed -i "s#ROOT_HOME#${ROOT_HOME}#" ${D}${bindir}/google-chrome

	# Always adding this libdir (not just with component builds), because the
        # LD_LIBRARY_PATH line in the google-chromium script refers to it
        install -d ${D}${libdir}/${BPN}/
        if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'component-build', 'component-build', '', d)}" ]; then
                install -m 0755 ${B}/out/${CHROMIUM_BUILD_TYPE}/lib/*.so ${D}${libdir}/${BPN}/
        fi
}
