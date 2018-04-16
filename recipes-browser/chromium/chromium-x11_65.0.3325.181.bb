require chromium-upstream-tarball.inc
require chromium-gn.inc

SRC_URI += "\
        file://0001-Add-missing-stdint-include.patch \
        file://0001-Fix-build-with-glibc-2.27.patch \
        file://0001-GCC-IDB-methods-String-renamed-to-GetString.patch \
        file://0001-GCC-PlaybackImageProvider-Settings-explicitely-set-c.patch \
        file://0001-GCC-build-fix-base-Optional-T-requires-the-full-decl.patch \
        file://0001-GCC-build-fix-mark-is_trivially_copy_constructible-f.patch \
        file://0001-GCC-fully-declare-ConfigurationPolicyProvider.patch \
        file://0001-Implement-conditional-copy-move-ctors-assign-operato.patch \
        file://0001-Workaround-for-g-7-is_trivially_copy_constructible-f.patch \
        file://aarch64-skia-build-fix.patch \
        file://chromium-gcc6-workarounds.patch \
"

REQUIRED_DISTRO_FEATURES = "x11"

DEPENDS += "\
        gtk+3 \
        libx11 \
        libxcomposite \
        libxcursor \
        libxdamage \
        libxext \
        libxfixes \
        libxi \
        libxrandr \
        libxrender \
        libxscrnsaver \
        libxtst \
"
DEPENDS_append_libc-musl = " libexecinfo"

# Compatibility glue while we have both chromium-x11 and
# chromium-ozone-wayland recipes, and the former used to be called just
# "chromium".
PROVIDES = "chromium"
