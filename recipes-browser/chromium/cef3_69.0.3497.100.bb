require chromium-upstream-tarball.inc
require chromium-gn.inc

SRC_URI += " git://bitbucket.org/chromiumembedded/cef.git;protocol=https;branch=3538;destsuffix=chromium-${PV}/cef;name=cef \
             file://0001-vpx_sum_squares_2d_i16_neon-Make-s2-a-uint64x1_t.patch \
             file://aarch64-skia-build-fix.patch \
             file://oe-clang-fixes.patch \
             file://patch_apply.patch \
             file://no_gtkglext.patch \
             file://linux_build_patch.patch \
             file://network_delegate_cookies.patch \
             file://renderer_url_requeste_create_url_loader.patch \
"

SRCREV_cef = "7d096429250cc4a47fa641337f0db3820e74736d"

PACKAGECONFIG = "use-egl cups"

REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS += "\
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
        gtk+ \
        libgnome-keyring \
"

do_configure_append() {
  ln -s Release out/Release_GN_x64
  cd cef
  bbnote "create cef3 projects"
  export PATH="$PATH:${S}/third_party/depot_tools:"
  export GN_DEFINES="${GN_ARGS}"
  ./cef_create_projects.sh
  bbnote "created cef3 projects"
  cd -
}

do_compile() {
  ninja -v ${PARALLEL_MAKE} cefsimple
}

do_install() {
	install -d ${D}${libdir}/cef3
	install -d ${D}${libdir}/cef3/locales
	install -d ${D}${includedir}/cef

	install -m 0755 cefsimple ${D}${libdir}/cef3/cefsimple
	install -m 0644 *.bin ${D}${libdir}/cef3/
	install -m 0644 icudtl.dat ${D}${libdir}/cef3/icudtl.dat

	# Cef *.pak files
	install -m 0644 *.pak ${D}${libdir}/cef3/

	# Locales.
	install -m 0644 locales/*.pak ${D}${libdir}/cef3/locales/

	install -m 0755 *.so ${D}${libdir}/cef3/

	# Swiftshader is only built for x86 and x86-64.
	if [ -d "swiftshader" ]; then
		install -d ${D}${libdir}/cef3/swiftshader
		install -m 0644 swiftshader/libEGL.so ${D}${libdir}/cef3/swiftshader/
		install -m 0644 swiftshader/libGLESv2.so ${D}${libdir}/cef3/swiftshader/
	fi

	# Header files
	cp -r ${S}/cef/include ${D}${includedir}/cef/
}

RDEPENDS_${PN} += " pango cairo fontconfig pciutils pulseaudio freetype fontconfig-utils "

# http://errors.yoctoproject.org/Errors/Details/186969/
EXCLUDE_FROM_WORLD_libc-musl = "1"

PACKAGES = "libcef3 libcef3-dbg libcef3-dev"
FILES_libcef3 = "${libdir}"
FILES_libcef3-dev = "${includedir}"
