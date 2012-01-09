DEPENDS += "cairo sqlite3 libnotify"
DEPENDS_append_libc-uclibc = " virtual/libintl-native"

# The .pc files below have "3.6" hardcoded, fix that before using them in a newer FF version!
SRC_URI = "ftp://ftp.mozilla.org/pub/mozilla.org/firefox/releases/${PV}/source/firefox-${PV}.source.tar.bz2;name=archive \
	file://jsautocfg.h \
	file://security-cross.patch \
	file://Bug339782.additional.fix.diff \
	file://Bug385583.nspr.jmp_buf.eabi.diff \
	file://Bug405992.atomic.nspr.diff \
	file://jemalloc-tls.patch \
	file://0001-Remove-Werror-from-build.patch \
	file://0002-Fix-security-cross-compile-cpu-detection-error.patch \
	file://plugins-dir.patch \
	file://Add-nanojit-support-for-ARMv4T.patch \
	file://firefox-plugin.pc \
	file://firefox-xpcom.pc \
	file://nspr.pc \
	file://xulrunner-chromium-mips.patch \
	file://xulrunner-1.9.2.8-mips-bus-error.patch \
	file://Fix-xptinvoke-and-xptstubs-on-mips-o32-big-endian.patch \
	file://Increase-stability-and-performance-on-mips.patch \
"

SRC_URI_append_mipsel = " file://mips-disable-TLS.patch "

SRC_URI[archive.md5sum] = "0ee5f14fd8be07513d16131027ebcb61"
SRC_URI[archive.sha256sum] = "fc609cc6a0ddaa2a9ebd8511ec39ae4a404e1107a12e07b233e2afca51d9a10e"

S = "${WORKDIR}/mozilla-1.9.2"

inherit mozilla
require firefox.inc

EXTRA_OECONF += " --enable-official-branding --disable-crashreporter"

FULL_OPTIMIZATION = "-fexpensive-optimizations -fomit-frame-pointer -frename-registers -O2"

do_compile_prepend() {
    cp ${WORKDIR}/jsautocfg.h ${S}/js/src/
    sed -i "s|CPU_ARCH =|CPU_ARCH = ${TARGET_ARCH}|" security/coreconf/Linux.mk
    sed -i "s|HOST_LIBIDL_CFLAGS = \@HOST_LIBIDL_CFLAGS\@|HOST_LIBIDL_CFLAGS = $(pkg-config --cflags libIDL-2.0)|" config/autoconf.mk.in
    sed -i "s|HOST_LIBIDL_LIBS   = @HOST_LIBIDL_LIBS@|HOST_LIBIDL_LIBS = $(pkg-config --libs libIDL-2.0)|" config/autoconf.mk.in
}

