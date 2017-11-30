require chromium-ozone-wayland.inc

SRC_URI[md5sum] = "1015ec7534923178edb61502b4d4ec34"
SRC_URI[sha256sum] = "9b5098ab9989a0d0df975f74ed7e65bd922fac593c74de9887450643047756c6"

SRC_URI += " \
 file://0001-Use-v8-qemu-wrapper.sh-on-v8-context-snapshot-creati.patch \
 file://0001-Rotate-gcc-toolchain-s-build-flags.patch \
 file://0001-GCC-fix-lambda-expression-cannot-reach-this-scope.patch \
 file://0001-Fix-memcpy-was-not-declared-in-this-scope.patch \
"
