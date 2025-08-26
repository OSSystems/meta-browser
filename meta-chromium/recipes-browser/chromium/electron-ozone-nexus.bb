require electron-gn.inc

# Only Broadcom STB chips have have Nexus and Bitbake will complain
# when building for anything else without something like this.
COMPATIBLE_MACHINE = "^brcmstb$"

RPROVIDES:${PN} = "electron"

SRC_URI += "\
	file://remove_dri.patch \
"

DEPENDS += "\
        libnotify \
        at-spi2-atk \
        libffi \
        nexus \
"

GN_ARGS += "\
        ${PACKAGECONFIG_CONFARGS} \
        use_ozone=true \
        ozone_auto_platforms=false \
        ozone_platform_nexus=true \
        use_xkbcommon=true \
        use_system_minigbm=true \
        use_system_libffi=true \
        use_gtk=false \
        use_gio=false \
        use_lttng=true \
        use_bundled_fontconfig=false \
        override_electron_version="${PV}" \
        import("//electron/build/args/release.gn") \
"

# The electron binary must always be started with those arguments.
CHROMIUM_EXTRA_ARGS:append = " --ozone-platform=nexus"
