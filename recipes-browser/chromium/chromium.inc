DESCRIPTION = "Chromium is an open-source browser project that aims to build a safer, faster, and more stable way for all users to experience the web."
HOMEPAGE = "https://www.chromium.org/Home"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0fca02217a5d49a14dfe2d11837bb34d"

SRC_URI = "https://commondatastorage.googleapis.com/chromium-browser-official/chromium-${PV}.tar.xz"
SRC_URI[md5sum] = "3f596ecbd6a39d5ada29f11780ec6dcf"
SRC_URI[sha256sum] = "f038e72cbd8b7383d13c286329623fda8d6d48f45fa2d964e554b5565283ad71"

# The build type defaults to Release. If you want a Debug build, you can set
# the variable CHROMIUM_BUILD_TYPE to "Debug" in your conf/local.conf file.
CHROMIUM_BUILD_TYPE ??= "Release"

inherit pythonnative
