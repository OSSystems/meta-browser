# Also copy the PIC version of libffi.a to the sysroot, as Chromium needs it.
# This is the same approach that Debian takes, which Chromium uses for its
# sysroot: https://sources.debian.org/src/libffi/3.4.4-1/debian/rules/#L103
do_install:append() {
	install -m 0644 ${B}/.libs/libffi_convenience.a ${D}/${libdir}/libffi_pic.a
}
