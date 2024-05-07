FILES:${PN} += "${libdir}/rustlib/*/target.json"

# Without this, rustc fails with "could not find specification for target ...".
do_install:append() {
    target_dir=`realpath "${D}${rustlibdir}"/..`
    target_triple=`basename "${target_dir}"`
    target_json="${target_dir}/target.json"
    RUSTC_BOOTSTRAP=1 rustc -Z unstable-options --print target-spec-json --target "${target_triple}" > "${target_json}"
}
