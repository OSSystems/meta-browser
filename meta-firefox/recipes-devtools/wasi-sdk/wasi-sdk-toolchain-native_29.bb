require wasi-sdk.inc

# wasi-sdk builds its own sysroot within the oe-sysroot,
# this is where both the toolchain and the sysroot needs to
# be installed. sysroot is installed automatically here, but
# this needs to be specified for the toolchain.
EXTRA_OECMAKE += "-DWASI_SDK_BUILD_TOOLCHAIN=ON -DCMAKE_INSTALL_PREFIX:PATH=${datadir}/wasi-sysroot"

do_install:append(){
    ln -s ../../../lib/libedit.so.0 ${D}${datadir}/wasi-sysroot/lib/libedit.so.0
    ln -s ../../../lib/libtinfo.so.5 ${D}${datadir}/wasi-sysroot/lib/libtinfo.so.5
}
