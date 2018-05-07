require chromium-upstream-tarball.inc
require chromium-gn.inc

SRC_URI += "\
        file://0001-GCC-IDB-methods-String-renamed-to-GetString.patch \
        file://0001-GCC-PlaybackImageProvider-Settings-explicitely-set-c.patch \
        file://0001-GCC-build-fix-base-Optional-T-requires-the-full-decl.patch \
        file://0001-GCC-build-fix-cannot-call-member-function-without-ob.patch \
        file://0001-GCC-build-fix-constexpr-static-data-member-must-be-i.patch \
        file://0001-GCC-build-fix-mark-is_trivially_copy_constructible-f.patch \
        file://0001-GCC-do-not-initialize-NEON-int32x4_t-with-braces-ini.patch \
        file://0001-GCC-do-not-use-initializer-list-for-NoDestructor-of-.patch \
        file://0001-GCC-explicitely-std-move-to-base-Optional-instead-of.patch \
        file://0001-GCC-fully-declare-ConfigurationPolicyProvider.patch \
        file://0001-jumbo-Fix-extensions-common-jumbo-build.patch \
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
