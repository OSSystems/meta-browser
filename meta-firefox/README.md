This is an updated version of meta-firefox layer, with a newer version of Firefox.

Plan to upstream this in the near future. If you want to help in, feel free to solve an issue or open a new one.

Current build status:

| Yocto-FF version / Arch | arm (32-bit) | aarch64 | x86_64 | risc-v |
| ----------------------- | ------------ | ------- | ------ | ------ |
| Dunfell - 115.12.0esr   | ![dunfell-arm-esr](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/dunfell-arm-v115.12.0esr.yml/badge.svg) | ![dunfell-aarch64-esr](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/dunfell-aarch64-v115.12.0esr.yml/badge.svg) | ![dunfell-x86_64-esr](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/dunfell-x86_64-v115.12.0esr.yml/badge.svg) | - |
| Kirkstone - 115.12.0esr | ![kirkstone-arm-esr](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/kirkstone-arm-v115.12.0esr.yml/badge.svg) | ![kirkstone-aarch64-esr](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/kirkstone-aarch64-v115.12.0esr.yml/badge.svg) | ![kirkstone-x86_64-esr](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/kirkstone-x86_64-v115.12.0esr.yml/badge.svg) | - |
| Kirkstone - 127.0.1     | ![kirkstone-arm-latest](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/kirkstone-arm-v127.0.1.yml/badge.svg) | ![kirkstone-aarch64-latest](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/kirkstone-aarch64-v127.0.1.yml/badge.svg) | ![kirkstone-x86_64-latest](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/kirkstone-x86_64-v127.0.1.yml/badge.svg) | - |
| Scarthgap - 115.12.0esr | ![scarthgap-arm-esr](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/scarthgap-arm-v115.12.0esr.yml/badge.svg) | ![scarthgap-aarch64-esr](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/scarthgap-aarch64-v115.12.0esr.yml/badge.svg) | ![scarthgap-x86_64-esr](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/scarthgap-x86_64-v115.12.0esr.yml/badge.svg) | ![scarthgap-riscv-esr](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/scarthgap-riscv-v115.12.0esr.yml/badge.svg) |
| Scarthgap - 127.0.1     | ![scarthgap-arm-latest](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/scarthgap-arm-v127.0.1.yml/badge.svg) | ![scarthgap-aarch64-latest](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/scarthgap-aarch64-v127.0.1.yml/badge.svg) | ![scarthgap-x86_64-latest](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/scarthgap-x86_64-v127.0.1.yml/badge.svg) | ![scarthgap-riscv-latest](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/scarthgap-riscv-v127.0.1.yml/badge.svg) |

OpenEmbedded/Yocto BSP layer for Firefox Browser
================================================

This layer provides web browser recipes for use with OpenEmbedded
and/or Yocto.

##Dependencies

This layer depends on poky, meta-oe, and meta-rust (only for Kirkstone and Dunfell). To see the tested revision/release
combinations, see the contents of the `kas` folder - that contains all the branch/revision information used for
testing.

Note: Make sure to add `RUST_PANIC_STRATEGY = "abort"` to local.conf before compiling Rust.

Note: On Dunfell currently only the ESR version of Firefox is supported. (Latest version requires a version of rust that is way too new
for this release). Possibly version 115 will be the latest supported version on that release due to this issue, unless
a newer version of rust toolchain becomes available. 

Note: Firefox requires at least Rust 1.76 starting from version 127. meta-rust layer provides 1.78, however this layer is not compatible with Scarthgap (nor newer).
Due to this, Rust 1.76 recipes are also shipped as part of this layer (adapted from Scarthgap). 

Unfortunately this needs some caution when it comes to the Rust version used. Testing is performed using the following combinations:

| Yocto/FF version | ESR | Latest |
| ---------------- | ---- | ----- |
| Dunfell | Rust 1.72 from meta-rust | N/A |
| Kirkstone | Rust 1.75 from meta-rust | Rust 1.78 from meta-rust (along with adding rust from oe-core to BBMASK) |
| Scarthgap | Rust 1.75 from oe-core | Rust 1.76 shipped with this layer (along with adding rust oe-core to BBMASK) |

Based on my testing, some datalayouts has changed in Rust 1.76, which also made it necessary to change them for Arm achitectures, by adding the following to local.conf:

```
DATA_LAYOUT[arm-eabi] = "e-m:e-p:32:32-Fi8-i64:64-v128:64:128-a:0:32-n32-S64"
DATA_LAYOUT[armv7-eabi] = "e-m:e-p:32:32-Fi8-i64:64-v128:64:128-a:0:32-n32-S64"
```

Contributing
------------

The preferred way to contribute to this layer is to send GitHub pull requests or
report problems in GitHub's issue tracker.

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

* wayland-only: (off by default, only v120+)
  Don't build Firefox with X11 dependencies. This config should not be enabled if x11-only
  is enabled.

* x11-only: (off by default, only v120+)
  Don't build Firefox with Wayland dependencies. This config should not be enable if wayland-only
  is enabled.

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
| 115.12.0esr | application-services | 86c84c217036c12283d19368867323a66bf35883 | This involves a collection of crates developed by Mozilla. These are specified in the top level Cargo.toml, by explicit hash. (interrupt-support, sql-support, sync15, tabs, viaduct, webext-storage) |
| 115.12.0esr | packed-simd | e588ceb568878e1a3156ea9ce551d5b63ef0cdc4 | Specified in top level Cargo.toml, by explicit hash. |
| 115.12.0esr | d3d12-rs | b940b1d71ab7083ae80eec697872672dc1f2bd32 | This commit is required explicitly by `third_party/rust/wgpu-hal/Cargo.toml`. |
| 115.12.0esr | neqo | 80db3a01f3273c7e742ba560fa99246fc8b30c4f | This commit corresponds to version 0.6.4, which is required by `netwerk/socket/neqo_glue/Cargo.toml` |
| 115.12.0esr | mp4parse | cf8b0e04de9c60f38f7f057f9f29c74d19336d0c | This commit is required explicitly by `toolkit/library/rust/shared/Cargo.toml` |
| 115.12.0esr | wgpu | f71a1bc736fde37509262ca03e91d8f56a13aeb5 | This commit is required explicitly by `gfx/wgpu_bindings/Cargo.toml` |
| 115.12.0esr | naga | b99d58ea435090e561377949f428bce2c18451bb | This commit is required explicitly by `third_party/rust/wgpu-hal/Cargo.toml` |
| 115.12.0esr | uniffi-rs | bc7ff8977bf38d0fdd1a458810b14f434d4dc4de | This commit corresponds to version 0.23.0, which is specified by the top level Cargo.toml |
| 115.12.0esr | audioipc | 0b51291d2483a17dce3e300c7784b369e02bee73 | This commit is required explicitly by `toolkit/library/rust/shared/Cargo.toml` |
| 115.12.0esr | wpf-gpu-raster | 5ab6fe33d00021325ee920b3c10526dc8301cf46 | This commit is required explicitly by `toolkit/library/rust/shared/Cargo.toml` |
| 115.12.0esr | warp | 4af45fae95bc98b0eba1ef0db17e1dac471bb23d | This commit is required explicitly by the top level Cargo.toml |
| 115.12.0esr | cubeb-pulse | cf48897be5cbe147d051ebbbe1eaf5fd8fb6bbc9 | This commit is required explicitly by `toolkit/library/rust/shared/Cargo.toml` |
| 115.12.0esr | midir | 519e651241e867af3391db08f9ae6400bc023e18 | This commit is required explicitly by the top level Cargo.toml |
| 115.12.0esr | cubeb-coreaudio | 93b5c01a131f65c83c11aeb317f4583405c5eb79 | Required explicitly by `./toolkit/library/rust/shared/Cargo.toml`. |
| 115.12.0esr | aa-stroke | 07d3c25322518f294300e96246e09b95e118555d | Required explicitly by `./toolkit/library/rust/shared/Cargo.toml`. |
| 115.12.0esr | jsparagus | 64ba08e24749616de2344112f226d1ef4ba893ae | Required explicitly by `js/src/frontend/smoosh/Cargo.toml`. |
| 127.0.1 | application-services | e0563d725f852f617878ecc13a03cdf50c85cd5a | This involves a collection of crates developed by Mozilla. These are specified in the top level Cargo.toml, by explicit hash. (interrupt-support, sql-support, sync15, tabs, viaduct, webext-storage, suggest, relevancy) |
| 127.0.1 | cose-rust | 43c22248d136c8b38fe42ea709d08da6355cf04b | This commit is required explicitly by the top level Cargo.toml |
| 127.0.1 | mp4parse | a138e40ec1c603615873e524b5b22e11c0ec4820 | This commit is required explicitly by `toolkit/library/rust/shared/Cargo.toml` |
| 127.0.1 | neqo | 343df5cc0d02e0b0953de4a0a390ae8980d89081 | This commit corresponds to v0.7.7, which is required by `netwerk/socket/neqo_glue/Cargo.toml` |
| 127.0.1 | wgpu | d5d683d3c491ec8cd2f5cdb43ac61e526cb7c231 | This commit is required explicitly by `gfx/wgpu_bindings/Cargo.toml` |
| 127.0.1 | audioipc | 409e11f8de6288e9ddfe269654523735302e59e6 | This commit is required explicitly by `toolkit/library/rust/shared/Cargo.toml` |
| 127.0.1 | wpf-gpu-raster | 99979da091fd58fba8477e7fcdf5ec0727102916 | This commit is required explicitly by `toolkit/library/rust/shared/Cargo.toml` |
| 127.0.1 | warp | 9d081461ae1167eb321585ce424f4fef6cf0092b | This commit is required explicitly by the top level Cargo.toml |
| 127.0.1 | cubeb-pulse | 8ff972c8e2ec1782ff262ac4071c0415e69b1367 | This commit is required explicitly by `toolkit/library/rust/shared/Cargo.toml` |
| 127.0.1 | cubeb-coreaudio | 4ca174cf83ebe32b3198478c2211d69678845bc7 | Required explicitly by `./toolkit/library/rust/shared/Cargo.toml`. |
| 127.0.1 | midir | 85156e360a37d851734118104619f86bd18e94c6 | This commit is required explicitly by the top level Cargo.toml |
| 127.0.1 | aa-stroke | d94278ed9c7020f50232689a26d1277eb0eb74d2 | Required explicitly by `./toolkit/library/rust/shared/Cargo.toml`. |
| 127.0.1 | jsparagus | 61f399c53a641ebd3077c1f39f054f6d396a633c | Required explicitly by `js/src/frontend/smoosh/Cargo.toml`.|
| 127.0.1 | mio | 9a2ef335c366044ffe73b1c4acabe50a1daefe05 | This commit is required explicitly by the top level Cargo.toml |
| 127.0.1 | unicode-bidi | ca612daf1c08c53abe07327cb3e6ef6e0a760f0c | Specified in top level Cargo.toml, by explicit hash. |
| 127.0.1 | gpu-descriptor | 7b71a4e47c81903ad75e2c53deb5ab1310f6ff4d | Specified in top level Cargo.toml, by explicit hash. |
| 127.0.1 | any-all-workaround | 7fb1b7034c9f172aade21ee1c8554e8d8a48af80 | Specified in top level Cargo.toml, by explicit hash. |
| common | chardetng | 3484d3e3ebdc8931493aa5df4d7ee9360a90e76b | This commit is required explicitly by the top level Cargo.toml |
| common | chardetng_c | ed8a4c6f900a90d4dbc1d64b856e61490a1c3570 | This commit is required explicitly by the top level Cargo.toml |
| common | coremidi | fc68464b5445caf111e41f643a2e69ccce0b4f83 | This commit is required explicitly by the top level Cargo.toml |
| common | mapped_hyph | c7651a0cffff41996ad13c44f689bd9cd2192c01 | Required explicitly by `./toolkit/library/rust/shared/Cargo.toml`. |
