Note from OldManYellsAtCloud
============

This is an updated version of meta-firefox layer, with a newer version of Firefox.

Currently supports Dunfell, Kirkstone, Mickledore and  Nanbield version of Yocto.

Plan to upstream this in the near future. If you want to help in, feel free to solve an issue or open a new one.

Make sure to add `RUST_PANIC_STRATEGY = "abort"` to local.conf before compiling Rust.

OpenEmbedded/Yocto BSP layer for Firefox Browser
================================================

This layer provides web browser recipes for use with OpenEmbedded
and/or Yocto.

This layer depends on:

Firefox can be successfully compiled with the following dependencies and branches/revisions:

| Yocto Version | meta-oe    | poky    | meta-clang | meta-rust |
| ------------- | ---------- | ------- | ---------- | --------- |
| Dunfell       | [Dunfell](https://github.com/openembedded/meta-openembedded/commit/300be975359fdb3a3b2bf7c6fe15dea7acac575d) | [Dunfell](https://git.yoctoproject.org/poky/commit/?h=dunfell&id=aeac1034661725b5c83e79f76238429fb236b090)        | [Dunfell](https://github.com/kraj/meta-clang/commit/67ac939d47b3dfc383fec7244e4a0ff85ca4340f)           | [Master](https://github.com/meta-rust/meta-rust/commit/e3082dc0728023b121d648da4c5c856943b5e425) |
| Kirkstone     | [Kirkstone](https://github.com/openembedded/meta-openembedded/commit/529620141e773080a6a7be4615fb7993204af883)  | [Kirkstone](https://git.yoctoproject.org/poky/commit/?h=kirkstone&id=75239ddd8d67c00139c6e88c1c2a790b471b12c5) | [Kirkstone](https://github.com/kraj/meta-clang/commit/2d08d6bf376a1e06c53164fd6283b03ec2309da4) | [Master](https://github.com/meta-rust/meta-rust/commit/e3082dc0728023b121d648da4c5c856943b5e425) |
| Mickledore    | [Mickledore](https://github.com/openembedded/meta-openembedded/commit/922f41b39f364e5b6be596b4b51e0fb37ffe1971) | [Mickledore](https://git.yoctoproject.org/poky/commit/?h=mickledore&id=a57506c46d92bf0767060102d2139be06d46f575) | [Master](https://github.com/kraj/meta-clang/commit/869df95b61ba44a7ad6bc57da8a31e459eec5059)  | N/A       |
| Nanbield      | [Nanbield](https://github.com/openembedded/meta-openembedded/commit/278c3f75e32f38f71bb52d161fe06bcb6f6bdd2f) | [Nanbield](https://git.yoctoproject.org/poky/commit/?h=nanbield&id=0e351df0425968fd58983e445391012e64f7f4ad)  | [Master](https://github.com/kraj/meta-clang/commit/869df95b61ba44a7ad6bc57da8a31e459eec5059) | N/A       |

Mickledore and Nanbield work with the Rust recipes included in poky.

Kirkstone however has a too old Rust version, and Dunfell has no Rust.
These version require the meta-rust layer also. For Kirkstone make sure to set the 
`PREFERRED_VERSION` for rust (&co) to the versions in the meta-rust layer.

The revisions in the table are indicative only. If there are newer commits in the
branch, they are expected to be compatible.

Contributing
------------

The preferred way to contribute to this layer is to send GitHub pull requests or
report problems in GitHub's issue tracker.

Maintainers
-----------
* OldManYellsAtCloud <skandigraun@gmail.com>

Recipes
-------
recipes-browser/firefox:
Firefox browser.

This recipe provides a package for the Firefox web browser.

Two separate recipes are available: one for the latest stable version, and one
for the latest ESR version.

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
  option adds a config file to enable it focedly.

* forbid-multiple-compositors: (off by default)
  This option allows to create only one GPU accelerated compositor, second and
  the following windows will use basic compositors. Multiple compositor may
  cause crash on platforms that doesn't support multiple EGL windows.

* disable-sandboxed-libraries: (off by default)
  By default WASM libraries are sanboxed for security purposes, however that can
  introduce quite a big build-time overhead due to extra dependencies.
  Setting this disallows sandboxing these libraries, and removes the wasi-sdk dependency.

* wayland-only: (off by default)
  Don't build Firefox with X11 dependencies. This config should not be enabled if x11-only
  is enabled.

* x11-only: (off by default)
  Don't build Firefox with Wayland dependencies. This config should not be enable if wayland-only
  is enabled.

Runtime options
---------------
* The enviromental variable `GDK_BACKEND=wayland` is needed to run Firefox with
  the wayland backend.
