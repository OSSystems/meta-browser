require wasi-sdk.inc

DEPENDS += "libxml2-native"

RDEPENDS:${PN} = "ncurses-native libedit-native libxml2-native"

# wasi-sdk builds its own sysroot within the oe-sysroot,
# this is where both the toolchain and the sysroot needs to
# be installed. sysroot is installed automatically here, but
# this needs to be specified for the toolchain.
EXTRA_OECMAKE += "-DWASI_SDK_BUILD_TOOLCHAIN=ON -DCMAKE_INSTALL_PREFIX:PATH=${datadir}/wasi-sysroot -DWASI_SDK_LIBEDIT=OFF -DWASI_SDK_LIBXML2=OFF -DLLVM_CMAKE_FLAGS:STRING='-DLLDB_ENABLE_LIBXML2=ON -DLLVM_ENABLE_LIBXML2=ON -DLibXml2_ROOT=${RECIPE_SYSROOT_NATIVE}/usr'"

do_install:append(){
    ln -s ../../../lib/libedit.so.0 ${D}${datadir}/wasi-sysroot/lib/libedit.so.0
    ln -s ../../../lib/libtinfo.so.5 ${D}${datadir}/wasi-sysroot/lib/libtinfo.so.5
    install -d ${D}${datadir}/wasi-sysroot/lib/pkgconfig
    ln -s ../../../lib/libxml2.so ${D}${datadir}/wasi-sysroot/lib/libxml2.so
    ln -s ../../../lib/libxml2.so.16 ${D}${datadir}/wasi-sysroot/lib/libxml2.so.16
    install -m 0644 ${RECIPE_SYSROOT_NATIVE}/usr/lib/pkgconfig/libxml-2.0.pc ${D}${datadir}/wasi-sysroot/lib/pkgconfig/
    # contains TMPDIR absolute path
    sed -i 's,^prefix=.*,prefix=$\{pcfiledir}/../../../..,' ${D}${datadir}/wasi-sysroot/lib/pkgconfig/libxml-2.0.pc
}
