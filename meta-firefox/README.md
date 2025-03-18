Current build and test status: [![Firefox build- and smoke-test](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/yocto_matrix.yml/badge.svg)](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/yocto_matrix.yml)

The following build combinations are tested automatically for each PR (the current config only allows to generate 1 summary badge, for everything):
- all supported Yocto versions (Kirkstone, Scarthgap, Styhead, Walnascar/master)
- both ESR and latest Firefox version
- glibc and musl 
- 4 arches: arm, aarch64, riscv-64 and x86-64

All combinations of the above are tested, except for RISC-V@Kirkstone combinations, which are not tested.

OpenEmbedded/Yocto BSP layer for Firefox Browser
================================================

This layer provides web browser recipes for use with OpenEmbedded
and/or Yocto.

## Dependencies

This layer depends on oe-core (poky), meta-oe and meta-clang. Additionally, the Rust version
shipped with Kirkstone and Scarthgap oe-core is out of date, which can be remediated by other
third-party layers: for Kirkstone and Scarthgap version the corresponding branch of meta-lts-mixins
is required for up to date Rust compiler. It is fairly easy to make meta-rust layer work also,
however there is no out of box support for it anymore.

For the tested revision/release combinations, see meta-firefox-test repository, especially
the kas folder - that contains all the branch/revision information used for testing.

`meta-clang` layer requires `libstdc++` to be installed on the build machine - make sure to 
install it, in case it is missing  (e.g. on Debian based systems install `libstdc++-dev` or
on Fedora install `libstdc++-devel`)

Note: Firefox requires at least Rust 1.76 starting from version 127. meta-rust layer provides
1.78, however this layer is not compatible with Scarthgap (nor newer). Fortunately lately a
meta-lts-mixins layer has been created with a more up to date version of Rust, for Scarthgap.

The state of Rust needs some caution when it comes to the the version used, and its provider.
Testing is performed using the following combinations:

| Yocto version | Rust version |
| ---------------- | ----- |
| Kirkstone | Rust 1.80.1 from meta-lts-mixins (kirkstone/rust branch) |
| Scarthgap | Rust 1.81.0 from meta-lts-mixins (scarthgap/rust branch) |
| Styhead | Rust 1.79 from oe-core |
| Walnascar | Rust 1.83.0 from oe-core |

Based on my testing, some datalayouts has changed in Rust 1.76, which also made it necessary to change them for Arm achitectures, by adding the following to local.conf:

```
DATA_LAYOUT[arm-eabi] = "e-m:e-p:32:32-Fi8-i64:64-v128:64:128-a:0:32-n32-S64"
DATA_LAYOUT[armv7-eabi] = "e-m:e-p:32:32-Fi8-i64:64-v128:64:128-a:0:32-n32-S64"
```

Contributing
------------

The preferred way to contribute to this layer is to send GitHub pull requests or
report problems in GitHub's issue tracker.

Classic patch submissions via email are not accepted for this layer.

Maintainers
-----------
* OldManYellsAtCloud/Gyorgy Sarvari <skandigraun@gmail.com>

Recipes
-------
recipes-browser/firefox:
Firefox browser.

This recipe provides a package for the Firefox web browser.

Two separate recipes are available: one for the latest stable version, and one
for the latest ESR version.

recipes-browser/firefox-i10l:
Language packs for Firefox. The default language is English, but installing any
of these packages will make the new language available in the settings menu.

PACKAGECONFIG knobs
-------------------
* alsa: (detected automatically)
  Enable ALSA support to play audio. It will be enabled automatically when
  DISTRO_FEATURES contains "alsa". Note that Firefox's default audio backend
  is PulseAudio, not ALSA. Although it's not enabled in official build, we
  enable it by default to make easy to play audio without additional daemons.
  When PulseAudio is running, it will be used instead of using ALSA directly
  even if this config is specified.

* wayland: (detected automatically)
  Enable wayland support. It will be enabled automatically when DISTRO_FEATURES
  contains "wayland".

* gpu: (off by default)
  Enable GPU acceleration.

* openmax: (off by default)
  Enable OpenMAX IL decoder to play H.264 video.
  This features is confirmed only on Renesas RZ/G1. Note: it is untested with version 113+.

* webgl: (off by default)
  Firefox on Linux doesn't enable WebGL against most GPUs by default. This
  option adds a config file to enable it forcedly.

* forbid-multiple-compositors: (off by default)
  This option allows to create only one GPU accelerated compositor, second and
  the following windows will use basic compositors. Multiple compositor may
  cause crash on platforms that doesn't support multiple EGL windows.

* disable-sandboxed-libraries: (off by default)
  By default WASM libraries are sandboxed for security purposes, however that can
  introduce quite a big build-time overhead due to extra dependencies.
  Setting this disallows sandboxing these libraries, and removes the wasi-sdk dependency.

* disable-gecko-profiler: (on by default, only v127+)
  Disable collecting profile statistics in different components of the browser. Reduces
  resource and size footprint (though not drastically).

* rust-simd: (on by default)
  Enable explicit SIMD in Rust code, when supported by the CPU.

* wayland-only: (off by default, only v120+)
  Don't build Firefox with X11 dependencies. This config should not be enabled if x11-only
  is enabled.

* x11-only: (off by default, only v120+)
  Don't build Firefox with Wayland dependencies. This config should not be enable if wayland-only
  is enabled.

* system-av1: (off by default)
  Use system av1. Currently untested.

* system-nspr: (off by default and autodetected in some cases)
  Use NSPR from the operating system, instead of building it with Firefox. In case an ARM CPU
  is the target without cryptographic capabilities, this config is automatically enabled.

* system-nss: (off by default and autodetected in some cases)
  Use NSS from the operating system, instead of building it with Firefox. In case an ARM CPU
  is the target without cryptographic capabilities, this config is automatically enabled.

* system-ffi: (off by default)
  Use system libffi

* system-icu: (off by default)
  Use system ICU.

* system-zlib: (off by default)
  Use system libz.

* system-pixman: (off by default)
  Use system pixman.

* system-jpeg: (off by default)
  Use system libjpeg.

* system-libevent: (off by default)
  Use system libevent.

* system-libvpx: (off by default)
  Use system libvpx.

* system-png: (off by default)
  Use system libpng (APNG support is required).

* system-webp: (off by default)
  Use system libwebp.

Source for "magic" commits
========================
While the source tarballs contains *most* of the compile dependencies, unfortunately some are missing, making it 
impossible to build Firefox without network connection. Due to this the Rust crates are downloaded directly 
(the list of downloaded crates and their version is taken from the `third_party/rust` folder), and some are downloaded
from git directly (for the most part these were gathered from the top level `Cargo.toml`). This section hopefully
documents where those magic commit hashes come from. (And will also be helpful for me, when I'm trying to figure
out where a specific dependency is coming from during updating to new versions).

In case a dependency is required by multiple sources, and they specify different versions, the latest version is
taken into account only.

Note: the dependency column refers to the `destsuffix` used in the recipes.

| Firefox version | Dependency | Used commit | Source / Comment |
| --------------- | ---------- | -------------- | ---------------- |
| 128.8.0esr + 136.0.3 | any_all_workaround | 7fb1b7034c9f172aade21ee1c8554e8d8a48af80 | Required by top level Cargo.toml |
| 128.8.0esr + 136.0.3 | chardetng | 3484d3e3ebdc8931493aa5df4d7ee9360a90e76b | Required by top level Cargo.toml |
| 128.8.0esr + 136.0.3 | chardetng_c | ed8a4c6f900a90d4dbc1d64b856e61490a1c3570 | Required by top level Cargo.toml |
| 128.8.0esr + 136.0.3 | coremidi | fc68464b5445caf111e41f643a2e69ccce0b4f83 | Required by top level Cargo.toml |
| 128.8.0esr + 136.0.3 | cose | 43c22248d136c8b38fe42ea709d08da6355cf04b | Required by top level Cargo.toml |
| 128.8.0esr + 136.0.3 | midir | 85156e360a37d851734118104619f86bd18e94c6 | Required by top level Cargo.toml |
| 128.8.0esr + 136.0.3 | mp4parse | a138e40ec1c603615873e524b5b22e11c0ec4820 | Required by `toolkit/library/rust/shared/Cargo.toml` |
| 128.8.0esr + 136.0.3 | cubeb-pulse | 8678dcab1c287de79c4c184ccc2e065bc62b70e2 | Required by `toolkit/library/rust/shared/Cargo.toml` |
| 128.8.0esr + 136.0.3 | wpf-gpu-raster | 99979da091fd58fba8477e7fcdf5ec0727102916 | Required by `toolkit/library/rust/shared/Cargo.toml` |
| 128.8.0esr + 136.0.3 | jsparagus | 61f399c53a641ebd3077c1f39f054f6d396a633c | Required by `js/src/frontend/smoosh/Cargo.toml` |
| 128.8.0esr + 136.0.3 | unicode-bidi | ca612daf1c08c53abe07327cb3e6ef6e0a760f0c | Required by top level Cargo.toml |
| 128.8.0esr | gpu-descriptor | 7b71a4e47c81903ad75e2c53deb5ab1310f6ff4d | Required by top level Cargo.toml |
| 128.8.0esr | aa-stroke | d94278ed9c7020f50232689a26d1277eb0eb74d2 | Required by `toolkit/library/rust/shared/Cargo.toml` |
| 128.8.0esr | warp | 9d081461ae1167eb321585ce424f4fef6cf0092b | Required by top level Cargo.toml |
| 128.8.0esr | application-services | 7c275b9088557abcbc8f3c2834f9aaa9064ca5e4 | Required by top level Cargo.toml |
| 128.8.0esr | mio | 9a2ef335c366044ffe73b1c4acabe50a1daefe05 | Required by top level Cargo.toml |
| 128.8.0esr | cubeb-coreaudio | 8bce3b333a920999055397a397e59c2b81a93b9a | Required by `toolkit/library/rust/shared/Cargo.toml` |
| 128.8.0esr | audioipc | e1071472c55193032aa4c1403317844005f9d0fc | Required by `toolkit/library/rust/shared/Cargo.toml` |
| 128.8.0esr | mapped_hyph | c7651a0cffff41996ad13c44f689bd9cd2192c01 | Required by `toolkit/library/rust/shared/Cargo.toml` |
| 128.8.0esr | ~~neqo~~ | ~~121fe683ae4b39a5b694f671abfd397cbd9b4322~~ | Required by `netwerk/socket/neqo_glue/Cargo.toml` - Not used by 128.8.0esr, uses vendored version. |
| 128.8.0esr | wgpu | c7458638d14921c7562e4197ddeefa17be413587 | Required by `gfx/wgpu_bindings/Cargo.toml` |
| 136.0.3 | application-services | 25934715ecc08fc922c80797c637dea64ee742d1 | Required by top level Cargo.toml |
| 136.0.3 | aa-stroke | a821fa621c2def48e90c82774b4c6563b5a8ea4a | Required by `toolkit/library/rust/shared/Cargo.toml` |
| 136.0.3 | cubeb-coreaudio | 2407441a2f67341a0e13b4ba6547555e387c671c | Required by `toolkit/library/rust/shared/Cargo.toml` |
| 136.0.3 | audioipc | e6f44a2bd1e57d11dfc737632a9e849077632330 | Required by `toolkit/library/rust/shared/Cargo.toml` |
| 136.0.3 | mapped_hyph | eff105f6ad7ec9b79816cfc1985a28e5340ad14b | Required by `toolkit/library/rust/shared/Cargo.toml` |
| 136.0.3 | neqo | c6d5502fb5b827473e7c5d7c4c380275cdb3d931 | Required by `netwerk/socket/neqo_glue/Cargo.toml` |
| 136.0.3 | wgpu | aa7bec65b90028e4db6ec8def8589b52097d92f9 | Required by `gfx/wgpu_bindings/Cargo.toml` |
| 136.0.3 | glutin | 03285da9c14ec56296c2400c781d2c32b80d745a | Required by `gfx/wr/Cargo.toml` |
| 136.0.3 | zlib | 4aa430ccb77537d0d60dab8db993ca51bb1194c5 | Required by `toolkit/library/rust/shared/Cargo.toml` |
| 136.0.3 | nss-gk-api | e48a946811ffd64abc78de3ee284957d8d1c0d63 | Required by top level Cargo.toml and `third-party/rust/mls-rs-crypto-nss/Cargo.toml` |
| 136.0.3 | mls-rs | eedb37e50e3fca51863f460755afd632137da57c | Required by `third-party/rust/mls-platform-api/Cargo.toml` crate |
| 136.0.3 | mls-platform-api | 19c3f18b747d13354370ba84440bb0b963932634 |  Required by `security/mls/mls_gk/Cargo.toml` |
| 136.0.3 | objc | 4de89f5aa9851ceca4d40e7ac1e2759410c04324 | Required by top level Cargo.toml |
