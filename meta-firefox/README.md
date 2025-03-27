This is an updated version of meta-firefox layer, with a newer version of Firefox.

Plan to upstream this in the near future. If you want to help in, feel free to solve an issue or open a new one.

Current build status:

| Yocto-FF version / Arch | arm (32-bit) | aarch64 | x86_64 | risc-v |
| ----------------------- | ------------ | ------- | ------ | ------ |
| Kirkstone - 128.3.0esr | ![kirkstone-arm-esr](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/kirkstone-arm-v128.3.0esr.yml/badge.svg) | ![kirkstone-aarch64-esr](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/kirkstone-aarch64-v128.3.0esr.yml/badge.svg) | ![kirkstone-x86_64-esr](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/kirkstone-x86_64-v128.3.0esr.yml/badge.svg) | - |
| Kirkstone - 131.0     | ![kirkstone-arm-latest](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/kirkstone-arm-v131.0.yml/badge.svg)  | ![kirkstone-aarch64-latest](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/kirkstone-aarch64-v131.0.yml/badge.svg)  | ![kirkstone-x86_64-latest](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/kirkstone-x86_64-v131.0.yml/badge.svg) | - |
| Scarthgap - 128.3.0esr | ![scarthgap-arm-esr](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/scarthgap-arm-v128.3.0esr.yml/badge.svg) | ![scarthgap-aarch64-esr](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/scarthgap-aarch64-v128.3.0esr.yml/badge.svg) | ![scarthgap-x86_64-esr](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/scarthgap-x86_64-v128.3.0esr.yml/badge.svg) | ![scarthgap-riscv-esr](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/scarthgap-riscv-v128.3.0esr.yml/badge.svg) |
| Scarthgap - 131.0     | ![scarthgap-arm-latest](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/scarthgap-arm-v131.0.yml/badge.svg)  | ![scarthgap-aarch64-latest](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/scarthgap-aarch64-v131.0.yml/badge.svg)  | ![scarthgap-x86_64-latest](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/scarthgap-x86_64-v131.0.yml/badge.svg) | ![scarthgap-riscv-latest](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/scarthgap-riscv-v131.0.yml/badge.svg) |
| Styhead - 128.3.0esr   | ![styhead-arm-esr](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/styhead-arm-v128.3.0esr.yml/badge.svg)     | ![styhead-aarch64-esr](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/styhead-aarch64-v128.3.0esr.yml/badge.svg)     | ![styhead-x86_64-esr](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/styhead-x86_64-v128.3.0esr.yml/badge.svg) | ![styhead-riscv-esr](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/styhead-riscv-v128.3.0esr.yml/badge.svg) |
| Styhead - 131.0       | ![styhead-arm-latest](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/styhead-arm-v131.0.yml/badge.svg)      | ![styhead-aarch64-latest](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/styhead-aarch64-v131.0.yml/badge.svg)      | ![styhead-86_64-latest](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/styhead-x86_64-v131.0.yml/badge.svg) | ![styhead-riscv-latest](https://github.com/OldManYellsAtCloud/meta-browser/actions/workflows/styhead-riscv-v131.0.yml/badge.svg) |

OpenEmbedded/Yocto BSP layer for Firefox Browser
================================================

This layer provides web browser recipes for use with OpenEmbedded
and/or Yocto.

##Dependencies

This layer depends on poky, meta-oe, meta-clang and meta-rust (only for Kirkstone). To see the tested revision/release
combinations, see the contents of the `kas` folder - that contains all the branch/revision information used for
testing.

`meta-clang` layer requires `libstdc++` to be installed on the build machine - make sure to install it, in case it is missing 
(e.g. on Debian based systems install `libstdc++-dev` or on Fedora install `libstdc++-devel`)

Note: Make sure to add `RUST_PANIC_STRATEGY = "abort"` to local.conf before compiling Rust.

Note: Firefox requires at least Rust 1.76 starting from version 127. meta-rust layer provides 1.78, however this layer is not compatible with Scarthgap (nor newer).
Due to this, Rust 1.76 recipes are also shipped as part of this layer (adapted from Scarthgap). 

Unfortunately this needs some caution when it comes to the Rust version used, and its provider. Testing is performed using the following combinations:

| Yocto/FF version | ESR | Latest |
| ---------------- | ---- | ----- |
| Kirkstone | Rust 1.75 from meta-rust | Rust 1.78 from meta-rust (along with adding rust from oe-core to BBMASK) |
| Scarthgap | Rust 1.75 from oe-core | Rust 1.76 shipped with this layer (along with adding rust oe-core to BBMASK) |
| Styhead | Rust 1.75 from oe-core | Rust 1.76 shipped with this layer (along with adding rust oe-core to BBMASK) |

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
| 128.3.0esr + 131.0 | any_all_workaround | 7fb1b7034c9f172aade21ee1c8554e8d8a48af80 | Required by top level Cargo.toml |
| 128.3.0esr + 131.0 | chardetng | 3484d3e3ebdc8931493aa5df4d7ee9360a90e76b | Required by top level Cargo.toml |
| 128.3.0esr + 131.0 | chardetng_c | ed8a4c6f900a90d4dbc1d64b856e61490a1c3570 | Required by top level Cargo.toml |
| 128.3.0esr + 131.0 | coremidi | fc68464b5445caf111e41f643a2e69ccce0b4f83 | Required by top level Cargo.toml |
| 128.3.0esr + 131.0 | cose | 43c22248d136c8b38fe42ea709d08da6355cf04b | Required by top level Cargo.toml |
| 128.3.0esr + 131.0 | midir | 85156e360a37d851734118104619f86bd18e94c6 | Required by top level Cargo.toml |
| 128.3.0esr + 131.0 | gpu-descriptor | 7b71a4e47c81903ad75e2c53deb5ab1310f6ff4d | Required by top level Cargo.toml |
| 128.3.0esr + 131.0 | mp4parse | a138e40ec1c603615873e524b5b22e11c0ec4820 | Required by top level Cargo.toml |
| 128.3.0esr + 131.0 | cubeb-pulse | 8678dcab1c287de79c4c184ccc2e065bc62b70e2 | Required by `toolkit/library/rust/shared/Cargo.toml` |
| 128.3.0esr + 131.0 | wpf-gpu-raster | 99979da091fd58fba8477e7fcdf5ec0727102916 | Required by `toolkit/library/rust/shared/Cargo.toml` |
| 128.3.0esr + 131.0 | aa-stroke | d94278ed9c7020f50232689a26d1277eb0eb74d2 | Required by `toolkit/library/rust/shared/Cargo.toml` |
| 128.3.0esr + 131.0 | jsparagus | 61f399c53a641ebd3077c1f39f054f6d396a633c | Required by `js/src/frontend/smoosh/Cargo.toml` |
| 128.3.0esr + 131.0 | unicode-bidi | ca612daf1c08c53abe07327cb3e6ef6e0a760f0c | Required by top level Cargo.toml |
| 128.3.0esr | warp | 9d081461ae1167eb321585ce424f4fef6cf0092b | Required by top level Cargo.toml |
| 128.3.0esr | application-services | 7c275b9088557abcbc8f3c2834f9aaa9064ca5e4 | Required by top level Cargo.toml |
| 128.3.0esr | mio | 9a2ef335c366044ffe73b1c4acabe50a1daefe05 | Required by top level Cargo.toml |
| 128.3.0esr | cubeb-coreaudio | 8bce3b333a920999055397a397e59c2b81a93b9a | Required by `toolkit/library/rust/shared/Cargo.toml` |
| 128.3.0esr | audioipc | e1071472c55193032aa4c1403317844005f9d0fc | Required by `toolkit/library/rust/shared/Cargo.toml` |
| 128.3.0esr | mapped_hyph | c7651a0cffff41996ad13c44f689bd9cd2192c01 | Required by `toolkit/library/rust/shared/Cargo.toml` |
| 128.3.0esr | neqo | 121fe683ae4b39a5b694f671abfd397cbd9b4322 | Required by `netwerk/socket/neqo_glue/Cargo.toml` |
| 128.3.0esr | wgpu | c7458638d14921c7562e4197ddeefa17be413587 | Required by `gfx/wgpu_bindings/Cargo.toml` |
| 131.0 | application-services | c3774b262f27fabdd8ae7d064db5745029b347b9 | Required by top level Cargo.toml |
| 131.0	| cubeb-coreaudio | 2407441a2f67341a0e13b4ba6547555e387c671c | Required by `toolkit/library/rust/shared/Cargo.toml` |
| 131.0	| audioipc | e6f44a2bd1e57d11dfc737632a9e849077632330 | Required by `toolkit/library/rust/shared/Cargo.toml` |
| 131.0	| mapped_hyph | eff105f6ad7ec9b79816cfc1985a28e5340ad14b | Required by `toolkit/library/rust/shared/Cargo.toml` |
| 131.0	| neqo | b7e17668eb8f2fb13c1d945a9a7f79bd31257eb8 | Required by `netwerk/socket/neqo_glue/Cargo.toml` |
| 131.0 | wgpu | bbdbafdf8a947b563b46f632a778632b906d9eb4 | Required by `gfx/wgpu_bindings/Cargo.toml` |
| 131.0 | glutin | 03285da9c14ec56296c2400c781d2c32b80d745a | Required by `gfx/wr/Cargo.toml` |
