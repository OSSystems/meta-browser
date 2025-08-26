# Inherit from chromium-gn.inc to get all shared configuration
require chromium-gn.inc

# Electron-specific metadata
DESCRIPTION = "Electron is an open-source project that aims to enable building cross-platform desktop apps with JavaScript, HTML, and CSS"
HOMEPAGE = "https://www.electronjs.org/"

# Override CVE product to include Electron and Node.js
CVE_PRODUCT = "chromium:chromium google:chrome electronjs:electron nodejs:node.js"

# Electron version - override the Chromium version
PV = "37.1.0-bs.alpha2"

# Electron-specific source - replace Chromium source with Electron source
SRC_URI = "https://electron-ci-public-artifact-bucket.s3.amazonaws.com/v${PV}/v${PV}.tar.xz"
SRC_URI[sha256sum] = "4921ad086f4c7be4374cd6b1e18a22c0f90a58c6b2253b6e90f16ae88c9cfb12"
S = "${UNPACKDIR}/src"

SRC_URI += " \
    file://0001-electron-fix-js2c-toolchain-for-Yocto-cross-compilat.patch \
    file://0002-fix-Remove-X11-dependencies.patch \
    file://0003-fix-Electron-yocto-build-problems.patch \
"

# Non-specific patches.
SRC_URI += "\
    file://0001-Drop-GN-compiler-settings-conflicting-with-OE.patch \
    file://0002-v8-qemu-wrapper.patch \
    file://0003-wrapper-extra-flags.patch \
    file://0004-Delete-compiler-options-not-available-in-release-ver.patch \
    file://0005-avoid-link-latomic-failure-on-CentOS-8-host.patch \
    file://0006-Don-t-pass-unknown-LLVM-options.patch \
    file://0007-Fix-constexpr-variable-must-be-initialized-by-a-cons.patch \
    file://0008-Use-the-correct-path-to-libclang_rt.builtins.a.patch \
    file://0009-Adjust-the-Rust-build-to-our-needs.patch \
    file://0010-Don-t-require-profiler_builtins.rlib.patch \
    file://0011-fix-check_version-Only-compare-node.js-major-version.patch \
    file://0012-chromium-fix-v4l2-compiler-error-on-arm.patch \
"

# ARM/AArch64-specific patches.
# Note: 0001-Fix-AES-crypto-SIGILL-on-rpi4-64.patch is already included in Electron source
# SRC_URI:append:aarch64 = "${@bb.utils.contains('TUNE_FEATURES', 'crypto', '', ' file://arm/0001-Fix-AES-crypto-SIGILL-on-rpi4-64.patch', d)}"
