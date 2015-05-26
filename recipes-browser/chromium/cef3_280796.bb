DESCRIPTION = "Chromium Embedded Framework"

include chromium.inc

RDEPENDS_${PN} += "pango cairo fontconfig pciutils pulseaudio freetype fontconfig-utils"

SRCREV_tools = "99bcb0e676eb396bcf8e1af3903aa4b578aeeee0"
SRCREV_cef = "bbad53dfca9f98dddcb31a590410fece0a4f0234"
SRCREV_egl = "a5b81b7617ba6757802b9b5f8c950034d5f961ec"
SRCREV_FORMAT = "cef_egl"

SRC_URI = "git://chromium.googlesource.com/chromium/tools/depot_tools.git;protocol=https;destsuffix=depot_tools;branch=master;name=tools \
           git://github.com/kuscsik/chromiumembedded.git;protocol=https;destsuffix=${S}/src/cef;branch=aura;name=cef \
           git://github.com/kuscsik/ozone-egl.git;protocol=https;destsuffix=${S}/src/ui/ozone/platform/egl;branch=master;name=egl\
           file://cef-simple\
  "

export CHROMIUM_SRC_GIT_URL="https://chromium.googlesource.com/chromium/src"
export CHROMIUM_SRC_GIT_COMMIT="e800fe7470fa87dc1ca5b148a7c2c41f603fdcbd"

S = "${WORKDIR}/chromium_rev_${PV}/"

do_fetch[vardeps] += "SRCREV_FORMAT SRCREV_cef SRCREV_egl"

GYP_ARCH_DEFINES_armv7a = " target_arch=arm arm_float_abi=hard"
GYP_ARCH_DEFINES_i586 = " target_arch=ia32"

export GYP_GENERATORS="ninja"
export BUILD_TARGET_ARCH="${TARGET_ARCH}"
export GYP_DEFINES="${GYP_ARCH_DEFINES} release_extra_cflags='-Wno-error=unused-local-typedefs' sysroot=''"

do_fetch_chromium_source_from_git() {
  cd ${S}
  export PATH=${WORKDIR}/depot_tools:"$PATH"

  # Note: --deps-file needs to set to .DEPS.git for syncing to old
  # chromium versions
  gclient config ${CHROMIUM_SRC_GIT_URL} --deps-file=".DEPS.git"

  # Runhooks is disabled. Running the hooks will trigger the
  # gyp_chromium command replaced with cef_create_projects.sh by
  # cef3.

  gclient sync -f --nohooks --revision src@${CHROMIUM_SRC_GIT_COMMIT}

  # We need the lastchange hook to generate the LASTCHANGE revision info
  # file.

  python src/build/util/lastchange.py -o src/build/util/LASTCHANGE
}

addtask fetch_chromium_source_from_git after do_patch before do_configure

do_configure_append() {
	export PATH=${WORKDIR}/depot_tools:"$PATH"
	# End of LD Workaround
	#-----------------------
	# Configure cef
	#------------------------
	cd ${S}/src/cef

  # Fatal linker warnings need to be disalbed to avoid linker issu
  # with GCC 4.9:
  # http://lists.openembedded.org/pipermail/openembedded-core/2014-August/095947.html

	./cef_create_projects.sh -I ${BUILD_TARGET_ARCH}_ozone.gypi --depth ../ -Ddisable_fatal_linker_warnings=
	cd -
}

# Workaround to disable qa_configure
do_qa_configure() {
	echo "do_qa_configure"
}

do_compile() {
 cd ${S}/src
	ninja -C out/${CHROMIUM_BUILD_TYPE} cefsimple
}
