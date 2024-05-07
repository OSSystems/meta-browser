# Chromium copies this file to a local sysroot. For class-target, it already
# gets installed, but for the native build we need to append this.
# Note: this isn't strictly needed for the compilation to work, but it's easier
# than making Chromium only copy it for the target and not the host.
rust_do_install:append() {
    install -m 0644 ${WORKDIR}/rust-targets/${RUST_TARGET_SYS}.json ${D}${libdir}/rustlib/${RUST_TARGET_SYS}/target.json
}
