RUST_VERSION ?= "${@d.getVar('PV').split('-')[0]}"

SRC_URI += "https://static.rust-lang.org/dist/rustc-${RUST_VERSION}-src.tar.xz;name=rust \
            file://cargo-path.patch;patchdir=${RUSTSRC} \
            file://0001-Handle-vendored-sources-when-remapping-paths.patch;patchdir=${RUSTSRC} \
"
SRC_URI[rust.sha256sum] = "805482b436442a6786d270cacbab8f00529e06141b27b7fb01909b97ce4f3464"

RUSTSRC_BASEDIR = '${@ "${WORKDIR}" if d.getVar("UNPACKDIR") == None \
                   else d.getVar("UNPACKDIR")}'

RUSTSRC = "${RUSTSRC_BASEDIR}/rustc-${RUST_VERSION}-src"

UPSTREAM_CHECK_URI = "https://forge.rust-lang.org/infra/other-installation-methods.html"
UPSTREAM_CHECK_REGEX = "rustc-(?P<pver>\d+(\.\d+)+)-src"

CVE_STATUS[CVE-2024-24576] = "not-applicable-platform: Issue only applies on Windows"
