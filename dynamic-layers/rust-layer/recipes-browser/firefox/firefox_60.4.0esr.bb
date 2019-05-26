# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DESCRIPTION ?= "Browser made by mozilla"
DEPENDS += "curl startup-notification libevent cairo libnotify \
            virtual/libgl pulseaudio yasm-native icu unzip-native \
            virtual/${TARGET_PREFIX}rust cargo-native ${RUSTLIB_DEP} \
           "
RDEPENDS_${PN}-dev = "dbus"

LICENSE = "MPLv2"
LIC_FILES_CHKSUM = "file://toolkit/content/license.html;endline=33;md5=f51d0fbc370c551d7371775b4f6544ca"

SRC_URI = "https://ftp.mozilla.org/pub/firefox/releases/${PV}/source/${PN}-${PV}.source.tar.xz;name=archive \
           file://mozconfig \
           file://mozilla-firefox.png \
           file://mozilla-firefox.desktop \
           file://prefs/vendor.js \
           file://fixes/avoid-running-autoconf2.13.patch \
           file://fixes/link-with-libpangoft.patch \
           file://fixes/bug1433081-fix-with-gl-provider-option.patch \
           file://fixes/0001-Enable-to-specify-RUST_TARGET-via-enviroment-variabl.patch \
           file://fixes/rustc_cross_flags.patch \
           file://fixes/0001-Add-clang-s-include-path-on-cross-compiling.patch \
           file://fixes/0001-Add-a-preference-to-force-enable-touch-events-withou.patch \
           file://fixes/fix-get-cpu-feature-definition-conflict.patch \
           file://fixes/fix-camera-permission-dialg-doesnot-close.patch \
           file://fixes/Allow-.js-preference-files-to-set-locked-prefs-with-.patch \
           file://fixes/Bug-1463035-Remove-MOZ_SIGNAL_TRAMPOLINE.-r-darchons.patch \
           file://fixes/Bug-1470701-Use-run-time-page-size-when-changing-map.patch \
           file://fixes/Bug-1444834-MIPS-Stubout-MacroAssembler-speculationB.patch \
           file://fixes/Bug-1144632-fix-big-endian-Skia-builds.-r-rhunt.patch \
           file://fixes/Bug-1505608-Try-to-ensure-the-bss-section-of-the-elf.patch \
           file://fixes/Bug-1500850-Wayland-Add-missing-dbus-header-dbus-gli.patch \
           file://fixes/0001-libloading-Use-lazy_static-instead-of-weak-static.patch \
           file://gn-configs/x64_False_arm64_linux.json \
           file://gn-configs/x64_False_arm_linux.json \
           file://porting/Add-xptcall-support-for-SH4-processors.patch \
           file://porting/NSS-Fix-FTBFS-on-Hurd-because-of-MAXPATHLEN.patch \
           file://porting/Make-powerpc-not-use-static-page-sizes-in-mozjemallo.patch \
           file://porting/Disable-libyuv-assembly-on-mips64.patch \
           file://porting/Fix-CPU_ARCH-test-for-libjpeg-on-mips.patch \
           file://porting/Work-around-Debian-bug-844357.patch \
           file://porting/Bug-1444303-MIPS-Fix-build-failures-after-Bug-142558.patch \
           file://prefs/Set-javascript.options.showInConsole.patch \
           file://prefs/Set-DPI-to-system-settings.patch \
           file://prefs/Don-t-auto-disable-extensions-in-system-directories.patch \
           file://debian-hacks/Avoid-wrong-sessionstore-data-to-keep-windows-out-of.patch \
           file://debian-hacks/Add-another-preferences-directory-for-applications-p.patch \
           file://debian-hacks/Don-t-register-plugins-if-the-MOZILLA_DISABLE_PLUGIN.patch \
           file://debian-hacks/Don-t-error-out-when-run-time-libsqlite-is-older-tha.patch \
           file://debian-hacks/Add-a-2-minutes-timeout-on-xpcshell-tests.patch \
           file://debian-hacks/Don-t-build-image-gtests.patch \
           file://debian-hacks/Allow-to-override-ICU_DATA_FILE-from-the-environment.patch \
           file://debian-hacks/Set-program-name-from-the-remoting-name.patch \
           file://debian-hacks/Use-remoting-name-for-call-to-gdk_set_program_class.patch \
           file://debian-hacks/Use-the-Mozilla-Location-Service-key-when-the-Google.patch \
           file://debian-hacks/Attempt-to-fix-building-webrtc-on-non-x86.patch \
           file://debian-hacks/Only-build-webrtc-neon-on-aarch64.patch \
           file://debian-hacks/Avoid-using-vmrs-vmsr-on-armel.patch \
           "
SRC_URI_append_libc-musl = "\
           file://musl/musl-mutex.patch \
           file://musl/musl_webrtc_glibcism.patch \
           file://musl/fix-bug-1261392.patch \
           file://musl/musl-tools-fix.patch \
           file://musl/musl-cmsghdr.patch \
"

SRC_URI[archive.md5sum] = "7b8e233ef47f0d341eb1a903552ed9a3"
SRC_URI[archive.sha256sum] = "205258548c3f245d42377b338f0db1272df39489d61305c39b83e52750ebff85"

MOZ_APP_BASE_VERSION = "${@'${PV}'.replace('esr', '')}"
S = "${WORKDIR}/firefox-${MOZ_APP_BASE_VERSION}"

inherit mozilla rust-common

DISABLE_STATIC=""

ARM_INSTRUCTION_SET_armv5 = "arm"

PACKAGECONFIG ??= "${@bb.utils.contains("DISTRO_FEATURES", "alsa", "alsa", "", d)} \
                   ${@bb.utils.contains("DISTRO_FEATURES", "wayland", "wayland", "", d)} \
                   ${@bb.utils.contains_any("TARGET_ARCH", "x86_64 arm aarch64", "webrtc", "", d)} \
"
PACKAGECONFIG[alsa] = "--enable-alsa,--disable-alsa,alsa-lib"
PACKAGECONFIG[wayland] = "--enable-default-toolkit=cairo-gtk3-wayland,"
PACKAGECONFIG[glx] = ",,,"
PACKAGECONFIG[egl] = "--with-gl-provider=EGL,,virtual/egl,"
PACKAGECONFIG[openmax] = "--enable-openmax,,,"
PACKAGECONFIG[webgl] = ",,,"
PACKAGECONFIG[canvas-gpu] = ",,,"
PACKAGECONFIG[stylo] = "--enable-stylo,--disable-stylo,,"
PACKAGECONFIG[webrtc] = "--enable-webrtc,--disable-webrtc,,"
PACKAGECONFIG[disable-e10s] = ",,,"
PACKAGECONFIG[forbit-multiple-compositors] = ",,,"

# Additional upstream patches to improve wayland patches
SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'wayland', \
           ' \
            file://wayland/bug1468670-enable-alt-modifier-on-wayland.patch \
            file://wayland/bug1438131-Implement-Drop-on-Wayland.patch \
            file://wayland/bug1460810-fix-segfault-while-pasting-text.patch \
            file://wayland/bug1438136-clipboard-text-null-terminate.patch \
            file://wayland/bug1461306-fix-size-of-mime-type-array.patch \
            file://wayland/bug1462622-Dont-use-GLXVsyncSource-on-non-X11-displays.patch \
            file://wayland/bug1462640-Allow-content-processes-to-mincore.patch \
            file://wayland/bug1464808-Set-move-as-default-Drag-Drop-action.patch \
            file://wayland/bug1451816-workaround-for-grabbing-popup.patch \
           ', '', d)}"

# Additional patches to support EGL on wayland
SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'wayland egl', \
           ' \
            file://wayland/egl/bug1460603-GLLibraryEGL-Use-wl_display-to-get-EGLDisplay-on-Way.patch \
            file://wayland/egl/bug1460605-Provide-NS_NATIVE_EGL_WINDOW-to-get-a-native-EGL-window-on-wa.patch \
            file://wayland/egl/bug1460605-Use-NS_NATIVE_EGL_WINDOW-instead-of-NS_NATIVE_WINDOW-on-GTK.patch \
            file://wayland/egl/bug1374136-Enable-sharing-SharedSurface_EGLImage.patch \
            file://wayland/egl/bug1462642-Use-dummy-wl_egl_window-instead-of-PBuffer.patch \
            file://wayland/egl/bug1464823-avoid-freeze-on-starting-compositor.patch \
            file://wayland/egl/0001-GLLibraryLoader-Use-given-symbol-lookup-function-fir.patch \
            file://wayland/egl/0002-Disable-query-EGL_EXTENSIONS.patch \
            file://wayland/egl/0001-Mark-GLFeature-framebuffer_multisample-as-unsupporte.patch \
           ', '', d)}"

# Add a config file to enable GPU acceleration by default.
SRC_URI += "${@bb.utils.contains_any('PACKAGECONFIG', 'glx egl', \
           '\
            file://prefs/gpu.js \
	   ', '', d)}"

# Additional upstream patches to support OpenMAX IL
SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'openmax', \
           ' \
            file://openmax/0001-Add-initial-implementation-of-PureOmxPlatformLayer.patch \
            file://openmax/0002-OmxDataDecoder-Fix-a-stall-issue-on-shutting-down.patch \
            file://openmax/0003-Plug-memory-leak-of-PureOmxPlatformLayer.patch \
            file://openmax/0004-Don-t-test-OMX_UseEGLImage.patch \
            file://prefs/openmax.js \
           ', \
           '', d)}"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'webgl', \
           'file://prefs/webgl.js', '', d)}"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'canvas-gpu', \
           'file://prefs/canvas-gpu.js', '', d)}"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'disable-e10s', \
           'file://prefs/disable-e10s.js', '', d)}"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'forbit-multiple-compositors', \
           ' \
            file://prefs/single-compositor.js \
            file://fixes/suppress-multiple-compositors.patch \
	   ', '', d)}"

python do_check_variables() {
    if bb.utils.contains('PACKAGECONFIG', 'glx egl', True, False, d):
        bb.warn("%s: GLX support will be disabled when EGL is enabled!" % bb.data.getVar('PN', d, 1))
    if bb.utils.contains_any('PACKAGECONFIG', 'glx egl', False, True, d):
        if bb.utils.contains('PACKAGECONFIG', 'webgl', True, False, d):
            bb.warn("%s: WebGL won't be enabled when both glx and egl aren't enabled!" % bb.data.getVar('PN', d, 1))
        if bb.utils.contains('PACKAGECONFIG', 'canvas-gpu', True, False, d):
            bb.warn("%s: Canvas acceleration won't be enabled when both glx and egl aren't enabled!" % bb.data.getVar('PN', d, 1))
}
addtask check_variables before do_configure

do_configure_append() {
    cp ${WORKDIR}/gn-configs/*.json ${S}/media/webrtc/gn-configs/
    ./mach build-backend -b GnMozbuildWriter
}

do_install_append() {
    install -d ${D}${datadir}/applications
    install -d ${D}${datadir}/pixmaps

    install -m 0644 ${WORKDIR}/mozilla-firefox.desktop ${D}${datadir}/applications/
    install -m 0644 ${WORKDIR}/mozilla-firefox.png ${D}${datadir}/pixmaps/
    install -m 0644 ${WORKDIR}/prefs/vendor.js ${D}${libdir}/${PN}/defaults/pref/
    if [ -n "${@bb.utils.contains_any('PACKAGECONFIG', 'glx egl', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/gpu.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'openmax', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/openmax.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'webgl', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/webgl.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'canvas-gpu', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/canvas-gpu.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'disable-e10s', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/disable-e10s.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'forbit-multiple-compositors', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/single-compositor.js ${D}${libdir}/${PN}/defaults/pref/
    fi

    # Fix ownership of files
    chown root:root -R ${D}${datadir}
    chown root:root -R ${D}${libdir}
}

FILES_${PN} = "${bindir}/${PN} \
               ${datadir}/applications/ \
               ${datadir}/pixmaps/ \
               ${libdir}/${PN}/* \
               ${libdir}/${PN}/.autoreg \
               ${bindir}/defaults"
FILES_${PN}-dev += "${datadir}/idl ${bindir}/${PN}-config ${libdir}/${PN}-devel-*"
FILES_${PN}-staticdev += "${libdir}/${PN}-devel-*/sdk/lib/*.a"
FILES_${PN}-dbg += "${libdir}/${PN}/.debug \
                    ${libdir}/${PN}/*/.debug \
                    ${libdir}/${PN}/*/*/.debug \
                    ${libdir}/${PN}/*/*/*/.debug \
                    ${libdir}/${PN}-devel-*/*/.debug \
                    ${libdir}/${PN}-devel-*/*/*/.debug \
                    ${libdir}/${PN}-devel-*/*/*/*/.debug \
                    ${bindir}/.debug"

# We don't build XUL as system shared lib, so we can mark all libs as private
PRIVATE_LIBS = " \
    libmozjs.so \
    libxpcom.so \
    libnspr4.so \
    libxul.so \
    libmozalloc.so \
    libplc4.so \
    libplds4.so \
    liblgpllibs.so \
    libmozgtk.so \
    libmozwayland.so \
    libmozsqlite3.so \
    libclearkey.so \
    libmozavcodec.so \
    libmozavutil.so \
"

# mark libraries also provided by nss as private too
PRIVATE_LIBS += " \
    libfreebl3.so \
    libfreeblpriv3.so \
    libnss3.so \
    libnssckbi.so \
    libsmime3.so \
    libnssutil3.so \
    libnssdbm3.so \
    libssl3.so \
    libsoftokn3.so \
"
