require chromium-ozone-wayland.inc

SRC_URI += " \
 file://0001-Use-v8-qemu-wrapper.sh-on-v8-context-snapshot-creati.patch \
 file://0001-Rotate-gcc-toolchain-s-build-flags.patch \
 file://0001-GCC-fix-lambda-expression-cannot-reach-this-scope.patch \
 file://0001-Fix-memcpy-was-not-declared-in-this-scope.patch \
"
