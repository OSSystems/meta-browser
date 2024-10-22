# Chromium copies this file to a local sysroot. For class-target, it already
# gets installed, but for the native build we need to append this.
# Note: this isn't strictly needed for the compilation to work, but it's easier
# than making Chromium only copy it for the target and not the host.
rust_do_install:append() {
    install -m 0644 ${WORKDIR}/rust-targets/${RUST_TARGET_SYS}.json ${D}${libdir}/rustlib/${RUST_TARGET_SYS}/target.json
}

# This makes sure that all .rlib files that Chromium needs get installed. The
# libraries installed by libstd-rs don't include e.g. libprofiler_builtins.
# Additionally, libstd and libtest installed by libstd-rs don't follow the usual
# naming scheme, which would trip up Chromium.
rust_do_install:class-target:append() {
    mkdir -p _dist
    rlib_path="rust-std-${PV}-${RUST_TARGET_SYS}/rust-std-${RUST_TARGET_SYS}/lib/rustlib"
    tar -C _dist -xf build/dist/rust-std-${PV}-${RUST_TARGET_SYS}.tar.xz $rlib_path

    target_dir=${D}${libdir}/rustlib
    install -d $target_dir
    cp -r _dist/$rlib_path/${RUST_TARGET_SYS} $target_dir
    rm -rf _dist
}

# Override the default dependency on libstd-rs, as we copy the libraries
# manually above.
RUSTLIB_DEP = ""
