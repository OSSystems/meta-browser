DESCRIPTION = "Chromium Embedded Framework"

include chromium.inc

RDEPENDS_${PN} += "pango cairo fontconfig pciutils pulseaudio freetype fontconfig-utils"

SRCREV_tools = "99bcb0e676eb396bcf8e1af3903aa4b578aeeee0"
SRCREV_cef = "bbad53dfca9f98dddcb31a590410fece0a4f0234"
SRCREV_egl = "a5b81b7617ba6757802b9b5f8c950034d5f961ec"
SRCREV_FORMAT = "cef_egl_tools"

SRC_URI = "http://people.linaro.org/~zoltan.kuscsik/chromium-browser/chromium_rev_${PV}.tar.xz \
           git://github.com/kuscsik/chromiumembedded.git;protocol=https;destsuffix=src/cef;branch=aura;name=cef \
           git://github.com/kuscsik/ozone-egl.git;protocol=https;destsuffix=src/ui/ozone/platform/egl;branch=master;name=egl \
           git://chromium.googlesource.com/chromium/tools/depot_tools.git;protocol=https;destsuffix=depot_tools;branch=master;name=tools \
           file://01_get_svn_version_from_LASTCHANGE.patch \
	   file://cef-simple \
	   file://0001-bignum.cc-disable-warning-from-gcc-5.patch \
	   file://0002-image_util.cc-disable-warning-from-gcc-5.patch \
           file://0003-disable-uninitialized-warning.patch \
	   file://0003-gtest-typed-test.h-disable-warning-unused-definition.patch \
	   file://0004-SaturatedArithmetic.h-put-parentheses-to-silence-war.patch \
	  "
SRC_URI[md5sum] = "9efbb50283b731042e62b9bd5e312b2f"
SRC_URI[sha256sum] = "f608e97dadf6ea4d885b24fd876896d46840fa39bf743ea2025075aee9fb348d"

S = "${WORKDIR}/chromium_rev_${PV}"

do_fetch[vardeps] += "SRCREV_FORMAT SRCREV_cef SRCREV_egl SRCREV_tools"

GYP_ARCH_DEFINES_x86 = " target_arch=ia32"
GYP_ARCH_DEFINES_armv7a = " target_arch=arm arm_float_abi=hard"

export GYP_GENERATORS="ninja"
export BUILD_TARGET_ARCH="${TARGET_ARCH}"
export GYP_DEFINES="${GYP_ARCH_DEFINES} release_extra_cflags='-Wno-error=unused-local-typedefs' sysroot=''"

EXTRA_OEGYP =	" \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', '-Ddisable_fatal_linker_warnings=1', '', d)} \
"

do_configure_prepend() {
    # there is no rule for the x86-64 architecture, recycle the i586 one
    cp  cef/i586_ozone.gypi  cef/x86_64_ozone.gypi
}

do_configure_append() {
    export PATH=${WORKDIR}/depot_tools:"$PATH"
    # End of LD Workaround
    #-----------------------
    # Configure cef
    #------------------------
    cd cef
    ./cef_create_projects.sh -I ${BUILD_TARGET_ARCH}_ozone.gypi --depth ../ ${EXTRA_OEGYP}
    cd -
}

# Workaround to disable qa_configure
do_qa_configure() {
    echo "do_qa_configure"
}

do_compile() {
    ninja -C out/${CHROMIUM_BUILD_TYPE} ${PARALLEL_MAKE} cefsimple
}

# | ../../v8/src/objects.h:8925:36: error: left operand of shift expression '(-8 << 26u)' is negative [-fpermissive]
PNBLACKLIST[cef3] ?= "BROKEN: fails to build with gcc-6"
