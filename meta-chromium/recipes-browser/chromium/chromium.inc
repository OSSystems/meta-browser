DESCRIPTION = "Chromium is an open-source browser project that aims to build a safer, faster, and more stable way for all users to experience the web."
HOMEPAGE = "https://www.chromium.org/Home"

CVE_PRODUCT = "chromium:chromium google:chrome"

SRC_URI = "https://commondatastorage.googleapis.com/chromium-browser-official/chromium-${PV}.tar.xz"
SRC_URI[sha256sum] = "eded233c26ab631be325ad49cb306c338513b6a6528197d42653e66187548e5d"

S = "${WORKDIR}/chromium-${PV}"

# GCC is not tested or officially supported upstream, and supporting it here
# requires an ever-growing amount of backports and custom patches, without any
# guarantee that the generated code will work as expected.
TOOLCHAIN = "clang"

# By default, clang.bbclass sets TOOLCHAIN:class-native to "gcc", but we want
# to build the native recipes (e.g. GN) with clang too.
TOOLCHAIN:class-native = "clang"

# This makes the target build use libc++ and compiler_rt instead of the GNU
# runtime, just like upstream does. The native binaries compiled and run as
# part of the build still use libstdc++ and libgcc though (see
# https://github.com/kraj/meta-clang/issues/449).
RUNTIME = "llvm"

# meta-clang uses RUNTIME variable to add build dependencies on libcxx/compiler-rt
# but does not add compiler options to commandline. It expects compiler's own
# defaults to be used and compiler's buildtime defaults are chosen based on
# global RUNTIME variable setting when building clang compiler, which defaults to 'gnu'.
# For chromium libc++ is expected to be used therefore ensure that
# right build flags are set for chromium irrespective of
# distro defaults (which could be 'gnu' or 'llvm').
# This also ensures that it does not rely too much on meta-clang's defaults.
LIBCPLUSPLUS = "-stdlib=libc++"

# Explicitly add dependency on libcxx, because meta-clang/hardknott checks for
# --stdlib=libc++ in LIBCPLUSPLUS (with extra dash, which was removed in
# newer meta-clang in https://github.com/kraj/meta-clang/commit/cda2283e4cf3c90ea42b43dbc3a2268be6655670)
DEPENDS += "libcxx"

inherit mime-xdg python3native
DEPENDS += "python3-setuptools-native"

# Chromium itself is licensed under the 3-clause BSD license. However, it
# depends upon several other projects whose copyright files are listed in
# LIC_FILES_CHKSUM. The licenses in those files vary, but none of them should
# cause the main Chromium license to change. The list of copyright files was
# generated with the aid of Chromium's own tools/licenses.py module. As such,
# it contains more items than we actually use in our build (e.g. there are
# components specific to Android, OS X, iOS etc, or bundled dependencies that
# we replace with system-wide versions). For more information about each
# project, you can consult README.chromium in each directory listed below.
# LICENSE does not cover all the licenses in LIC_FILES_CHKSUM; instead, it
# currently contains Chromium's license (BSD-3-Clause) as well as Blink's
# (LGPL2.0, LGPL2.1 and BSD-2-Clause).
LICENSE = "BSD-3-Clause & LGPL-2.0-or-later & LGPL-2.1-or-later"

# The generate_license_list.py script can help generate and update this list.
LIC_FILES_CHKSUM = "\
    file://${S}/LICENSE;md5=c408a301e3407c3803499ce9290515d6 \
    file://${S}/LICENSE.chromium_os;md5=775979d3e1e3bf163e4882e231727de5 \
    file://${S}/base/third_party/cityhash/COPYING;md5=f714c995c5bd9b175938acdd69226b60 \
    file://${S}/base/third_party/cityhash_v103/COPYING;md5=f714c995c5bd9b175938acdd69226b60 \
    file://${S}/base/third_party/double_conversion/LICENSE;md5=1ea35644f0ec0d9767897115667e901f \
    file://${S}/base/third_party/dynamic_annotations/LICENSE;md5=b6bd7011ecc1a4f7ac3525589f45e565 \
    file://${S}/base/third_party/nspr/LICENSE;md5=0806173dfb7e6d060074595e968fd02e \
    file://${S}/base/third_party/superfasthash/LICENSE;md5=c66981f8ad23c9f279a5b9e07385128c \
    file://${S}/base/third_party/symbolize/LICENSE;md5=17ae3b22fe8fa438966625593e2eea85 \
    file://${S}/base/third_party/valgrind/LICENSE;md5=b95c9cb2560f157c60ea60ac1aa69469 \
    file://${S}/base/third_party/xdg_mime/LICENSE;md5=743a91118d40f173467c8ecfd8793c62 \
    file://${S}/base/third_party/xdg_user_dirs/LICENSE;md5=d998f250c491c329a8254dd1ca62c647 \
    file://${S}/buildtools/third_party/libc++/trunk/LICENSE.TXT;md5=55d89dd7eec8d3b4204b680e27da3953 \
    file://${S}/buildtools/third_party/libc++abi/trunk/LICENSE.TXT;md5=7b9334635b542c56868400a46b272b1e \
    file://${S}/buildtools/third_party/libunwind/trunk/LICENSE.TXT;md5=f66970035d12f196030658b11725e1a1 \
    file://${S}/chrome/installer/mac/third_party/bsdiff/LICENSE;md5=0dbe7a50f028269750631fcbded3846a \
    file://${S}/chrome/installer/mac/third_party/xz/LICENSE;md5=8bbde65ae0523dc878ed1ac1252d2d14 \
    file://${S}/chrome/third_party/mozilla_security_manager/LICENSE;md5=0c259b853bbf067b361100ce560adce7 \
    file://${S}/courgette/third_party/bsdiff/LICENCE;md5=7c2ad392281bc3d9b04a31102ee7abdd \
    file://${S}/courgette/third_party/divsufsort/LICENSE;md5=e261f081e52882dad3ff0225497ce451 \
    file://${S}/ios/third_party/blink/LICENSE;md5=9fdb47308c4e0a2b6d07c5af39e5da1a \
    file://${S}/ios/third_party/material_components_ios/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/ios/third_party/material_font_disk_loader_ios/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/ios/third_party/material_internationalization_ios/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/ios/third_party/material_roboto_font_loader_ios/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/ios/third_party/material_sprited_animation_view_ios/LICENSE;md5=175792518e4ac015ab6696d16c4f607e \
    file://${S}/ios/third_party/material_text_accessibility_ios/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/ios/third_party/motion_animator_objc/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/ios/third_party/motion_interchange_objc/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/ios/third_party/motion_transitioning_objc/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/native_client/LICENSE;md5=cbbd27594afd089daa160d3a16dd515a \
    file://${S}/native_client_sdk/src/libraries/third_party/newlib-extras/README;md5=e944d73ca3817b7ca8656eafb9497fed \
    file://${S}/net/third_party/mozilla_win/LICENSE;md5=9b13a17f35cae227ee726ee0108d72a2 \
    file://${S}/net/third_party/nss/LICENSE;md5=3b1e88e1b9c0b5a4b2881d46cce06a18 \
    file://${S}/net/third_party/quiche/src/LICENSE;md5=0fca02217a5d49a14dfe2d11837bb34d \
    file://${S}/net/third_party/uri_template/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/abseil-cpp/LICENSE;md5=df52c6edb7adc22e533b2bacc3bd3915 \
    file://${S}/third_party/accessibility-audit/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/afl/src/docs/COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_crazy_linker/LICENSE;md5=6ac427101b06097fd70652bb9238ca23 \
    file://${S}/third_party/android_deps/libs/android_arch_core_runtime/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/android_arch_lifecycle_common/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/android_arch_lifecycle_common_java8/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/android_arch_lifecycle_livedata/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/android_arch_lifecycle_livedata_core/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/android_arch_lifecycle_runtime/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/android_arch_lifecycle_viewmodel/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_animated_vector_drawable/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_appcompat_v7/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_asynclayoutinflater/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_cardview_v7/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_collections/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_coordinatorlayout/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_cursoradapter/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_customview/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_design/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_documentfile/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_drawerlayout/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_interpolator/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_loader/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_localbroadcastmanager/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_multidex/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_print/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_recyclerview_v7/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_slidingpanelayout/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_support_annotations/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_support_compat/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_support_core_ui/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_support_core_utils/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_support_fragment/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_support_media_compat/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_support_v4/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_support_vector_drawable/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_swiperefreshlayout/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_transition/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_versionedparcelable/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_support_viewpager/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_android_tools_desugar_jdk_libs/LICENSE;md5=3e0b59f8fac05c3c03d4a26bbda13f8f \
    file://${S}/third_party/android_deps/libs/com_android_tools_desugar_jdk_libs_configuration/LICENSE;md5=b6cda58008bb60623be55223bfe531b3 \
    file://${S}/third_party/android_deps/libs/com_google_android_datatransport_transport_api/LICENSE;md5=7f7d74108ee1b7a743cca7d9a86784d6 \
    file://${S}/third_party/android_deps/libs/com_google_android_gms_play_services_auth/LICENSE;md5=b8c82a390fc5a5bba473726868fc5e5d \
    file://${S}/third_party/android_deps/libs/com_google_android_gms_play_services_auth_api_phone/LICENSE;md5=5c9317538bd492b4fd01eec9d6972640 \
    file://${S}/third_party/android_deps/libs/com_google_android_gms_play_services_auth_base/LICENSE;md5=b8c82a390fc5a5bba473726868fc5e5d \
    file://${S}/third_party/android_deps/libs/com_google_android_gms_play_services_base/LICENSE;md5=ccf2e51a4d2e697e200e15c6a5b3dc43 \
    file://${S}/third_party/android_deps/libs/com_google_android_gms_play_services_basement/LICENSE;md5=ccf2e51a4d2e697e200e15c6a5b3dc43 \
    file://${S}/third_party/android_deps/libs/com_google_android_gms_play_services_cast/LICENSE;md5=1e61529f08860f8fa1c90bf8af8007ff \
    file://${S}/third_party/android_deps/libs/com_google_android_gms_play_services_cast_framework/LICENSE;md5=6688c3d596b2e35dd1928d1ffdf5a9e2 \
    file://${S}/third_party/android_deps/libs/com_google_android_gms_play_services_clearcut/LICENSE;md5=32c019d8c1f8222a8aad9c3e1f1d2d10 \
    file://${S}/third_party/android_deps/libs/com_google_android_gms_play_services_cloud_messaging/LICENSE;md5=fb1c3d71ca3681654ec1604b10fc1ace \
    file://${S}/third_party/android_deps/libs/com_google_android_gms_play_services_fido/LICENSE;md5=8da3462ecfc5c138a49a7b5ac05abf09 \
    file://${S}/third_party/android_deps/libs/com_google_android_gms_play_services_flags/LICENSE;md5=92b728c587a67a1b9577e9ca141ca520 \
    file://${S}/third_party/android_deps/libs/com_google_android_gms_play_services_gcm/LICENSE;md5=92b728c587a67a1b9577e9ca141ca520 \
    file://${S}/third_party/android_deps/libs/com_google_android_gms_play_services_iid/LICENSE;md5=05fc50851d6d550ef2bb400b326353f0 \
    file://${S}/third_party/android_deps/libs/com_google_android_gms_play_services_instantapps/LICENSE;md5=cd033bdb088af16ecdbcd95bd5562a30 \
    file://${S}/third_party/android_deps/libs/com_google_android_gms_play_services_location/LICENSE;md5=d332b0f8bf5e67cef70541572ba69b61 \
    file://${S}/third_party/android_deps/libs/com_google_android_gms_play_services_phenotype/LICENSE;md5=32c019d8c1f8222a8aad9c3e1f1d2d10 \
    file://${S}/third_party/android_deps/libs/com_google_android_gms_play_services_places_placereport/LICENSE;md5=32c019d8c1f8222a8aad9c3e1f1d2d10 \
    file://${S}/third_party/android_deps/libs/com_google_android_gms_play_services_stats/LICENSE;md5=92b728c587a67a1b9577e9ca141ca520 \
    file://${S}/third_party/android_deps/libs/com_google_android_gms_play_services_tasks/LICENSE;md5=ccf2e51a4d2e697e200e15c6a5b3dc43 \
    file://${S}/third_party/android_deps/libs/com_google_android_gms_play_services_vision/LICENSE;md5=d332b0f8bf5e67cef70541572ba69b61 \
    file://${S}/third_party/android_deps/libs/com_google_android_gms_play_services_vision_common/LICENSE;md5=d332b0f8bf5e67cef70541572ba69b61 \
    file://${S}/third_party/android_deps/libs/com_google_android_material_material/LICENSE;md5=7f7d74108ee1b7a743cca7d9a86784d6 \
    file://${S}/third_party/android_deps/libs/com_google_android_play_core/LICENSE;md5=7f7d74108ee1b7a743cca7d9a86784d6 \
    file://${S}/third_party/android_deps/libs/com_google_code_findbugs_jsr305/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_google_dagger_dagger/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_google_errorprone_error_prone_annotations/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_google_firebase_firebase_annotations/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_google_firebase_firebase_common/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_google_firebase_firebase_components/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_google_firebase_firebase_encoders/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_google_firebase_firebase_encoders_json/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_google_firebase_firebase_iid/LICENSE;md5=18b56b380e9331ebdf0247a0b10bbaf0 \
    file://${S}/third_party/android_deps/libs/com_google_firebase_firebase_iid_interop/LICENSE;md5=a5923809e496d46d8842ab7d8a52621f \
    file://${S}/third_party/android_deps/libs/com_google_firebase_firebase_installations/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_google_firebase_firebase_installations_interop/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_google_firebase_firebase_measurement_connector/LICENSE;md5=a5923809e496d46d8842ab7d8a52621f \
    file://${S}/third_party/android_deps/libs/com_google_firebase_firebase_messaging/LICENSE;md5=6287e01249a45d9de9bc62ca9163c913 \
    file://${S}/third_party/android_deps/libs/com_google_guava_failureaccess/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_google_guava_guava_android/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_google_guava_listenablefuture/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_google_j2objc_j2objc_annotations/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/com_google_protobuf_protobuf_javalite/LICENSE;md5=37b5762e07f0af8c74ce80a8bda4266b \
    file://${S}/third_party/android_deps/libs/javax_inject_javax_inject/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/org_checkerframework_checker_compat_qual/LICENSE;md5=f32f668c1f4eea36fb53fc4cc7d96385 \
    file://${S}/third_party/android_deps/libs/org_checkerframework_checker_qual/LICENSE;md5=f32f668c1f4eea36fb53fc4cc7d96385 \
    file://${S}/third_party/android_deps/libs/org_codehaus_mojo_animal_sniffer_annotations/LICENSE;md5=a5dd953e661e22a77f7b8062ae790f6a \
    file://${S}/third_party/android_deps/libs/org_jetbrains_annotations/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/org_jetbrains_kotlin_kotlin_stdlib/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/org_jetbrains_kotlin_kotlin_stdlib_common/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/org_jetbrains_kotlin_kotlin_stdlib_jdk7/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/org_jetbrains_kotlin_kotlin_stdlib_jdk8/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/org_jetbrains_kotlinx_kotlinx_coroutines_android/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_deps/libs/org_jetbrains_kotlinx_kotlinx_coroutines_core_jvm/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_media/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_opengl/LICENSE;md5=d10e92761a860d4113a7a525c78daf13 \
    file://${S}/third_party/android_provider/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_sdk/LICENSE;md5=a9559ed17808a8b10eec6672f993ce75 \
    file://${S}/third_party/android_swipe_refresh/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/android_system_sdk/LICENSE;md5=cbb64e76df0f98fc138b1918c089417e \
    file://${S}/third_party/angle/LICENSE;md5=7abdb66a6948f39c2f469140db5184e2 \
    file://${S}/third_party/angle/src/common/third_party/xxhash/LICENSE;md5=cb91c07001f1ca6fd50b6bd4f042946a \
    file://${S}/third_party/angle/src/libANGLE/renderer/vulkan/shaders/src/third_party/ffx_spd/LICENSE;md5=64356ee6f79c94525e102a3264a62653 \
    file://${S}/third_party/apple_apsl/LICENSE;md5=c8c14a9f985552dba8d3edbd76660088 \
    file://${S}/third_party/arcore-android-sdk-client/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/arcore-android-sdk/LICENSE;md5=c1bc4f7fbdd7a5a3f2c0dfefd457665e \
    file://${S}/third_party/ashmem/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/axe-core/LICENSE;md5=f27a50d2e878867827842f2c60e30bfc \
    file://${S}/third_party/bidimapper/LICENSE;md5=2b42edef8fa55315f34f2370b4715ca9 \
    file://${S}/third_party/blink/LICENSE_FOR_ABOUT_CREDITS;md5=11e90d553b211de885f245900c4ccf89 \
    file://${S}/third_party/boringssl/src/LICENSE;md5=9b082148f9953258347788edb83e401b \
    file://${S}/third_party/boringssl/src/third_party/fiat/LICENSE;md5=8eb8c1cdeb53faab99e4673182bed9f7 \
    file://${S}/third_party/breakpad/LICENSE;md5=828152b41153892a725f0e7f56faff73 \
    file://${S}/third_party/brotli/LICENSE;md5=941ee9cd1609382f946352712a319b4b \
    file://${S}/third_party/bspatch/LICENSE;md5=3e837ede9697ce4c789c3ca32aabe003 \
    file://${S}/third_party/cast_core/LICENSE;md5=175792518e4ac015ab6696d16c4f607e \
    file://${S}/third_party/ced/LICENSE;md5=f63cd7e51ca7b7aac748e20dc60e083f \
    file://${S}/third_party/chromevox/LICENSE;md5=45739090b454125d851ac7bb1bbd23a2 \
    file://${S}/third_party/cld_3/LICENSE;md5=d8b32ba83f8b11e4badd979f4319e706 \
    file://${S}/third_party/cldr/LICENSE;md5=fe2500bb52e825476d4b93d6f1458232 \
    file://${S}/third_party/closure_compiler/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/content_analysis_sdk/LICENSE;md5=ee5fc272be9139e1816c73ce09611ca5 \
    file://${S}/third_party/cpuinfo/src/LICENSE;md5=cabca80e8e4a4bbe90e9a4b4ad53f5f4 \
    file://${S}/third_party/crashpad/crashpad/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/crc32c/src/LICENSE;md5=e9ed01b5e5ac9eae23fc2bb33701220c \
    file://${S}/third_party/crubit/LICENSE;md5=e8237eb3ff53402dada6d65b7640b60b \
    file://${S}/third_party/d3/src/LICENSE;md5=0679931d76eb2f1dd1915249da84a722 \
    file://${S}/third_party/dav1d/LICENSE;md5=59cb7015cbeea4a8675b9bdb657d41b3 \
    file://${S}/third_party/dawn/LICENSE;md5=94758054e79f4a16d45043dfa6248541 \
    file://${S}/third_party/decklink/LICENSE;md5=85b197f7b7b1ef270b01a1b6377be50e \
    file://${S}/third_party/devtools-frontend/src/LICENSE;md5=537e0b52077bf0a616d0a0c8a79bc9d5 \
    file://${S}/third_party/devtools-frontend/src/front_end/third_party/acorn/package/LICENSE;md5=33e8b86a084578431a31c72608f226b8 \
    file://${S}/third_party/devtools-frontend/src/front_end/third_party/codemirror.next/LICENSE;md5=b5494d4f10ac6bb043e5f684e59cd13f \
    file://${S}/third_party/devtools-frontend/src/front_end/third_party/codemirror/package/LICENSE;md5=8554e1ee437cc3fb3cfee9ad4a11b8ab \
    file://${S}/third_party/devtools-frontend/src/front_end/third_party/diff/LICENSE;md5=175792518e4ac015ab6696d16c4f607e \
    file://${S}/third_party/devtools-frontend/src/front_end/third_party/i18n/LICENSE;md5=a873c5645c184d51e0f9b34e1d7cf559 \
    file://${S}/third_party/devtools-frontend/src/front_end/third_party/intl-messageformat/LICENSE;md5=913015c0e75ff89ef9461fadca555f65 \
    file://${S}/third_party/devtools-frontend/src/front_end/third_party/lighthouse/LICENSE;md5=a873c5645c184d51e0f9b34e1d7cf559 \
    file://${S}/third_party/devtools-frontend/src/front_end/third_party/lit-html/package/LICENSE;md5=cfc0c20794ef30f41515b56ff0d51668 \
    file://${S}/third_party/devtools-frontend/src/front_end/third_party/marked/LICENSE;md5=449f8b1cf0bfef1e5ec7824a4179ac6f \
    file://${S}/third_party/devtools-frontend/src/front_end/third_party/wasmparser/package/LICENSE;md5=2ee41112a44fe7014dce33e26468ba93 \
    file://${S}/third_party/distributed_point_functions/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/dom_distiller_js/LICENSE;md5=6cd778870ce5c0c08c8e16fbdf378532 \
    file://${S}/third_party/eigen3/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/emoji-metadata/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/emoji-segmenter/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/expat/src/expat/COPYING;md5=9e2ce3b3c4c0f2670883a23bbd7c37a9 \
    file://${S}/third_party/farmhash/LICENSE;md5=7dfaa79e2b070897e495fec386e3acfc \
    file://${S}/third_party/fdlibm/LICENSE;md5=b17367d6c97e638599071d99a3049dfe \
    file://${S}/third_party/ffmpeg/CREDITS.chromium;md5=edd5404ebfcb158090789e3cb2cb494d \
    file://${S}/third_party/fft2d/LICENSE;md5=a062f975da2ca26745be122d5a0b1356 \
    file://${S}/third_party/flac/COPYING.Xiph;md5=a2c4b71c0198682376d483eb5bcc9197 \
    file://${S}/third_party/flatbuffers/LICENSE;md5=a873c5645c184d51e0f9b34e1d7cf559 \
    file://${S}/third_party/flex/LICENSE;md5=e4742cf92e89040b39486a6219b68067 \
    file://${S}/third_party/fontconfig/src/COPYING;md5=7a0449e9bc5370402a94c00204beca3d \
    file://${S}/third_party/fp16/LICENSE;md5=855dd24c28c76c916c5c2301b1958728 \
    file://${S}/third_party/freetype/src/docs/FTL.TXT;md5=9f37b4e6afa3fef9dba8932b16bd3f97 \
    file://${S}/third_party/fusejs/LICENSE;md5=f61de6d0d3d200550e44d63cc2a4fb67 \
    file://${S}/third_party/gemmlowp/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/gif_player/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
    file://${S}/third_party/google-closure-library/LICENSE;md5=e23fadd6ceef8c618fc1c65191d846fa \
    file://${S}/third_party/google_input_tools/LICENSE;md5=ff0b0f0202337c9d4ae2d869797049d3 \
    file://${S}/third_party/google_toolbox_for_mac/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/google_trust_services/src/LICENSE;md5=cfaf8febfe7b4f3e07a08dd4d0db1914 \
    file://${S}/third_party/grpc/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/gvr-android-keyboard/LICENSE;md5=67d07a07ec29a50a3ded12b2ba952257 \
    file://${S}/third_party/gvr-android-sdk/LICENSE;md5=634fdf826e0846931133dca5785f59b8 \
    file://${S}/third_party/harfbuzz-ng/src/COPYING;md5=6ee0f16281694fb6aa689cca1e0fb3da \
    file://${S}/third_party/highway/LICENSE;md5=2b42edef8fa55315f34f2370b4715ca9 \
    file://${S}/third_party/hunspell/COPYING.MPL;md5=bfe1f75d606912a4111c90743d6c7325 \
    file://${S}/third_party/hyphenation-patterns/LICENSE;md5=53b217e6f2560833cbb279119065ae61 \
    file://${S}/third_party/iaccessible2/LICENSE;md5=43bbd0bfb581347ec10def720000a645 \
    file://${S}/third_party/iccjpeg/LICENSE;md5=26834d132689a03abf860f4572705494 \
    file://${S}/third_party/icu/LICENSE;md5=a89d03060ff9c46552434dbd1fe3ed1f \
    file://${S}/third_party/inspector_protocol/LICENSE;md5=db90dcb101891a7285d25f2fbceca752 \
    file://${S}/third_party/ipcz/LICENSE;md5=1649e1dc663940b442197bd7f99cd228 \
    file://${S}/third_party/isimpledom/LICENSE;md5=822502c0e010ccdbb6a3e681dd47888e \
    file://${S}/third_party/jinja2/LICENSE.rst;md5=5dc88300786f1c214c1e9827a5229462 \
    file://${S}/third_party/jsoncpp/LICENSE;md5=c56ee55c03a55f8105b969d8270632ce \
    file://${S}/third_party/jstemplate/COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/khronos/LICENSE;md5=1d2ef853a9ae7ace4e16fda0d48f597b \
    file://${S}/third_party/lcov/COPYING;md5=751419260aa954499f7abaabaa882bbe \
    file://${S}/third_party/leveldatabase/src/LICENSE;md5=92d1b128950b11ba8495b64938fc164d \
    file://${S}/third_party/libFuzzer/LICENSE.TXT;md5=4c0bc17c954e99fd547528d938832bfa \
    file://${S}/third_party/libaddressinput/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/libaom/source/libaom/LICENSE;md5=6ea91368c1bbdf877159435572b931f5 \
    file://${S}/third_party/libavif/LICENSE;md5=15744a27d810133340dc64ad411ec4cd \
    file://${S}/third_party/libbrlapi/LICENSE;md5=fad9b3332be894bab9bc501572864b29 \
    file://${S}/third_party/libevent/LICENSE;md5=a2b0302a710150293fb85d2bfabd3de6 \
    file://${S}/third_party/libgav1/src/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/libipp/LICENSE;md5=af9e58383a1b2b17c75c6c9ff561ca9d \
    file://${S}/third_party/libjingle_xmpp/LICENSE;md5=ad296492125bc71530d06234d9bfebe0 \
    file://${S}/third_party/libjpeg_turbo/LICENSE.md;md5=a75feaa2de6d8942f50d412241fca1f1 \
    file://${S}/third_party/libjxl/LICENSE;md5=6a905a337cc228a1f68f0b5416f52a7f \
    file://${S}/third_party/liblouis/LICENSE;md5=8ab69863de0d3a0e00c4f97a4d78dd4a \
    file://${S}/third_party/libphonenumber/LICENSE;md5=e23fadd6ceef8c618fc1c65191d846fa \
    file://${S}/third_party/libpng/LICENSE;md5=b0085051bf265bac2bfc38bc89f50000 \
    file://${S}/third_party/libprotobuf-mutator/src/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/libsecret/LICENSE;md5=23c2a5e0106b99d75238986559bb5fc6 \
    file://${S}/third_party/libsrtp/LICENSE;md5=2909fcf6f09ffff8430463d91c08c4e1 \
    file://${S}/third_party/libsync/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/libudev/LICENSE;md5=4fbd65380cdd255951079008b364516c \
    file://${S}/third_party/liburlpattern/LICENSE;md5=5be7cac9f8920c488c4dda56dbfeaec2 \
    file://${S}/third_party/libusb/src/COPYING;md5=fbc093901857fcd118f065f900982c24 \
    file://${S}/third_party/libvpx/source/libvpx/LICENSE;md5=d5b04755015be901744a78cc30d390d4 \
    file://${S}/third_party/libwebm/source/LICENSE.TXT;md5=6e8dee932c26f2dab503abf70c96d8bb \
    file://${S}/third_party/libwebp/LICENSE;md5=72dceabd78c6f435bb304047aff1745a \
    file://${S}/third_party/libx11/LICENSE;md5=28a27b3e1a66e95206d5645b787dad69 \
    file://${S}/third_party/libxcb-keysyms/LICENSE;md5=5b0382211c6a98f8e4861daa0f3f4322 \
    file://${S}/third_party/libxml/src/Copyright;md5=2044417e2e5006b65a8b9067b683fcf1 \
    file://${S}/third_party/libxslt/src/Copyright;md5=0cd9a07afbeb24026c9b03aecfeba458 \
    file://${S}/third_party/libyuv/LICENSE;md5=464282cfb405b005b9637f11103a7325 \
    file://${S}/third_party/libzip/LICENSE;md5=6bb2408fd544544b86946e67ec2da9a0 \
    file://${S}/third_party/logilab/logilab/common/LICENSE.txt;md5=751419260aa954499f7abaabaa882bbe \
    file://${S}/third_party/lottie/LICENSE;md5=827837648055a0bfb7782b91ab42eed1 \
    file://${S}/third_party/lzma_sdk/LICENSE;md5=a8d1f5a28cfd35e6dfea09b82672fae7 \
    file://${S}/third_party/maldoca/src/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
    file://${S}/third_party/material_design_icons/LICENSE;md5=175792518e4ac015ab6696d16c4f607e \
    file://${S}/third_party/material_web_components/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/mesa_headers/LICENSE;md5=072744090020e72ad7387e201873fe43 \
    file://${S}/third_party/metrics_proto/LICENSE;md5=cfaf8febfe7b4f3e07a08dd4d0db1914 \
    file://${S}/third_party/microsoft_webauthn/LICENSE;md5=b98fddd052bb2f5ddbcdbd417ffb26a8 \
    file://${S}/third_party/minigbm/LICENSE;md5=fcd558ab2fd7f53408826e80eb0c11eb \
    file://${S}/third_party/minizip/src/LICENSE;md5=3fb541eb8359212703e21d14eba7ac64 \
    file://${S}/third_party/modp_b64/LICENSE;md5=eb7e2e0af1d4971360553aedadee8d86 \
    file://${S}/third_party/mozilla/LICENSE;md5=822502c0e010ccdbb6a3e681dd47888e \
    file://${S}/third_party/nasm/LICENSE;md5=90904486f8fbf1861cf42752e1a39efe \
    file://${S}/third_party/nearby/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/neon_2_sse/LICENSE;md5=53abad1ded16f44100126962f4bbef6c \
    file://${S}/third_party/one_euro_filter/LICENSE;md5=5b4d092181a412ae42c63e7fc1e53366 \
    file://${S}/third_party/openh264/src/LICENSE;md5=bb6d3771da6a07d33fd50d4d9aa73bcf \
    file://${S}/third_party/openscreen/src/LICENSE;md5=d5fead7a94377fb7b73964d9144c6cbb \
    file://${S}/third_party/opus/src/COPYING;md5=e304cdf74c2a1b0a33a5084c128a23a3 \
    file://${S}/third_party/ots/LICENSE;md5=e06eff2aa65b917034a81599bea73dc4 \
    file://${S}/third_party/pdfium/LICENSE;md5=c93507531cc9bb8e24a05f2a1a4036c7 \
    file://${S}/third_party/perfetto/LICENSE;md5=f87516e0b698007e9e75a1fe1012b390 \
    file://${S}/third_party/pffft/LICENSE;md5=0f39e43e9bc20e7e103e54750e1ec3a2 \
    file://${S}/third_party/ply/LICENSE;md5=f298aa3b0cfe0b0ea96acabf944529c0 \
    file://${S}/third_party/polymer/LICENSE.polymer;md5=324f45ce459ffd97e41d175a4e95a4be \
    file://${S}/third_party/private-join-and-compute/src/LICENSE;md5=175792518e4ac015ab6696d16c4f607e \
    file://${S}/third_party/private_membership/LICENSE;md5=9d44acd6df6b90cbbabbd467d67e74c7 \
    file://${S}/third_party/protobuf/LICENSE;md5=37b5762e07f0af8c74ce80a8bda4266b \
    file://${S}/third_party/puffin/LICENSE;md5=b1ed361f9fc790c1054d81a7ef041a34 \
    file://${S}/third_party/pyjson5/src/LICENSE;md5=d2794c0df5b907fdace235a619d80314 \
    file://${S}/third_party/pylint/pylint/LICENSE.txt;md5=4325afd396febcb659c36b49533135d4 \
    file://${S}/third_party/pywebsocket3/src/LICENSE;md5=b661a96f569e068e4af269db23b09a90 \
    file://${S}/third_party/qcms/src/COPYING;md5=65636fa951328d3be95f6047ec3936ce \
    file://${S}/third_party/re2/LICENSE;md5=3b5c31eb512bdf3cb11ffd5713963760 \
    file://${S}/third_party/rjsmin/LICENSE;md5=34f8c1142fd6208a8be89399cb521df9 \
    file://${S}/third_party/rnnoise/COPYING;md5=1890bf89a18f8339491894a0b45428bf \
    file://${S}/third_party/ruy/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/securemessage/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/setupdesign/LICENSE;md5=302a09339e8de37ba06c121279be1707 \
    file://${S}/third_party/shell-encryption/src/LICENSE;md5=c96a2157133614bf8a135bd27cd230d6 \
    file://${S}/third_party/simplejson/LICENSE.txt;md5=8a9f8dcfcd28c34daa443e5b1d29813b \
    file://${S}/third_party/skia/LICENSE;md5=822f02cc7736281816581cd064afbb1c \
    file://${S}/third_party/smhasher/LICENSE;md5=0d948322ab524e2b74f55eb8ef57c74a \
    file://${S}/third_party/snappy/src/COPYING;md5=f62f3080324a97b3159a7a7e61812d0c \
    file://${S}/third_party/speech-dispatcher/COPYING;md5=5828ebb4b7590269a994ed46b0a70df0 \
    file://${S}/third_party/sqlite/LICENSE;md5=7256769d4613c0af5a72be32ba20ccf7 \
    file://${S}/third_party/subresource-filter-ruleset/LICENSE;md5=408c32b7944dedb9d57797eca2447ea1 \
    file://${S}/third_party/sudden_motion_sensor/LICENSE;md5=6f89397ba4dda73a940f27f76e9ad9d4 \
    file://${S}/third_party/swift-toolchain/LICENSE;md5=97ecb46ea45fefa64206d9b7e9671afa \
    file://${S}/third_party/swiftshader/LICENSE.txt;md5=d273d63619c9aeaf15cdaf76422c4f87 \
    file://${S}/third_party/tensorflow-text/LICENSE;md5=01e86893010a1b87e69a213faa753ebd \
    file://${S}/third_party/test_fonts/LICENSE;md5=a78e43d492b7bd2ad4a4529c7a6266ca \
    file://${S}/third_party/text-fragments-polyfill/LICENSE;md5=86d3f3a95c324c9479bd8986968f4327 \
    file://${S}/third_party/tflite/LICENSE;md5=64a34301f8e355f57ec992c2af3e5157 \
    file://${S}/third_party/tflite_support/LICENSE;md5=e03a906d141508f60cbe30013b8d064b \
    file://${S}/third_party/ukey2/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/unrar/src/license.txt;md5=fc9c335ec05a5f36764ef9ce7a79daa1 \
    file://${S}/third_party/usb_ids/LICENSE;md5=8f694e3758e3e57a5223bfa2bf29d452 \
    file://${S}/third_party/utf/LICENSE;md5=b940b7cb006a9c3a53ac180bdbe41473 \
    file://${S}/third_party/v4l-utils/COPYING.libv4l;md5=d749e86a105281d7a44c2328acebc4b0 \
    file://${S}/third_party/vulkan-deps/LICENSE;md5=0fca02217a5d49a14dfe2d11837bb34d \
    file://${S}/third_party/vulkan-deps/glslang/LICENSE;md5=ce523927d7bcd789d6c3af579d03ad73 \
    file://${S}/third_party/vulkan-deps/spirv-cross/src/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/vulkan-deps/spirv-headers/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/vulkan-deps/spirv-tools/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/vulkan-deps/vulkan-headers/LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/vulkan-deps/vulkan-loader/src/LICENSE.txt;md5=7dbefed23242760aa3475ee42801c5ac \
    file://${S}/third_party/vulkan-deps/vulkan-tools/src/LICENSE.txt;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/vulkan-deps/vulkan-validation-layers/src/LICENSE.txt;md5=8df9e8826734226d08cb412babfa599c \
    file://${S}/third_party/vulkan_memory_allocator/LICENSE.txt;md5=28469cb09e41bc708efbb6a69fb39a29 \
    file://${S}/third_party/wayland-protocols/src/COPYING;md5=c7b12b6702da38ca028ace54aae3d484 \
    file://${S}/third_party/wayland/src/COPYING;md5=b31d8f53b6aaf2b4985d7dd7810a70d1 \
    file://${S}/third_party/web-animations-js/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/third_party/webrtc/LICENSE;md5=ad296492125bc71530d06234d9bfebe0 \
    file://${S}/third_party/weston/LICENSE;md5=58164c3e354cc1d2f50a79ea98eb2b15 \
    file://${S}/third_party/woff2/LICENSE;md5=027c71da9e4664fdf192e6ec615f4d18 \
    file://${S}/third_party/wtl/Ms-PL.txt;md5=1d2c3cdd7e748a6bc4213321d9157c5a \
    file://${S}/third_party/wuffs/LICENSE;md5=2ee41112a44fe7014dce33e26468ba93 \
    file://${S}/third_party/x11proto/LICENSE;md5=f242876648161c7208413d60498dc20d \
    file://${S}/third_party/xdg-utils/LICENSE;md5=a5367a90934098d6b05af3b746405014 \
    file://${S}/third_party/zlib/LICENSE;md5=8c75f2b4df47a77f9445315a9500cd1c \
    file://${S}/third_party/zxcvbn-cpp/LICENSE.txt;md5=50f9303d0b3adb4952217b0d8c00d83f \
    file://${S}/tools/mac/power/protos/third_party/pprof/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://${S}/url/third_party/mozilla/LICENSE.txt;md5=437ced1e9b232651b0912a9594da43b2 \
    file://${S}/v8/LICENSE;md5=f38a2942edcfe4abb45e9a83a1ad2f82 \
    file://${S}/v8/LICENSE.fdlibm;md5=b17367d6c97e638599071d99a3049dfe \
    file://${S}/v8/LICENSE.strongtalk;md5=956a43818dd13f2e93088fc2c93589d2 \
    "
