Note from OldManYellsAtCloud
============

This is an updated version of meta-firefox layer, with a newer version of Firefox.

It is based on the meta-browser layer. It has been tested with Yocto Mickledore only, mostly due to lack of laptop power. I would expect it to work with previous version also, if meta-rust is pulled in.
Feel free to test, and report back the results. 

The layer build successfully with aarch64-poky-linux-gnu arch (tested with PinePhone). Tests with other architectures would be welcome.

Plan to upstream this in the near future. If you want to help in, feel free to solve an issue or open a new one.

Make sure to add `RUST_PANIC_STRATEGY = "abort"` to local.conf before compiling Rust.

In case you are building the browser for a CPU without hardware encryption support (e.g. anything before armv8, like the first gen RPi), set the following in your distro.conf: `PREFERRED_VERSION_nss = "3.89"`

OpenEmbedded/Yocto BSP layer for Firefox Browser
================================================

This layer provides web browser recipes for use with OpenEmbedded
and/or Yocto.

This layer depends on:

* URI: git://git.openembedded.org/openembedded-core
  - branch: master
  - revision: HEAD

* URI: git://git.openembedded.org/meta-openembedded
  - layers: meta-oe
  - branch: master
  - revision: HEAD

* URI: git://github.com/kraj/meta-clang
  - branch: master
  - revision: HEAD

Contributing
------------

The preferred way to contribute to this layer is to send GitHub pull requests or
report problems in GitHub's issue tracker.

Alternatively there is the classic way of review on the OpenEmbedded dev mailing
list openembedded-devel@lists.openembedded.org (you have to be subscribed to
post to the list). Please cc the maintainers if you send your patches.

Maintainers
-----------
* Fabio Berton <fabio.berton@ossystems.com.br>
* Khem Raj <raj.khem@gmail.com>
* Otavio Salvador <otavio@ossystems.com.br>
* Takuro Ashie <ashie@clear-code.com>

When sending single patches, please use something like :
```
git send-email -1 -s --to openembedded-devel@lists.openembedded.org --subject-prefix='meta-browser][PATCH'
```

Recipes
-------
recipes-browser/firefox:
Firefox browser.

This recipe provides a package for the Firefox web browser.

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

Runtime options
---------------
* The enviromental variable `GDK_BACKEND=wayland` is needed to run Firefox with
  the wayland backend.
