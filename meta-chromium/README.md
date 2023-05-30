# OpenEmbedded/Yocto BSP layer for Chromium Browsers

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

## Contributing

The preferred way to contribute to this layer is to send GitHub pull requests or
report problems in GitHub's issue tracker.

Alternatively there is the classic way of review on the OpenEmbedded dev mailing
list openembedded-devel@lists.openembedded.org (you have to be subscribed to
post to the list). Please cc the maintainers if you send your patches.

## Maintainers

* Fabio Berton <fabio.berton@ossystems.com.br>
* Raphael Kubo da Costa <raphael.kubo.da.costa@intel.com>
* Khem Raj <raj.khem@gmail.com>
* Otavio Salvador <otavio@ossystems.com.br>
* Max Ihlenfeldt <max@igalia.com>

When sending single patches, please use something like:
```
git send-email -1 -s --to openembedded-devel@lists.openembedded.org --subject-prefix='meta-browser][PATCH'
```

## Recipes

recipes-browser/chromium:
Chromium browser.

This recipe provides a package for the Chromium web browser. It strives to
always follow the latest stable Linux release as listed in
https://omahaproxy.appspot.com

We refer to the web browser as Chromium, not Chrome, because "Chrome" is
Google's version of the web browser with proprietary content on top of the
open-source Chromium browser.

Compared to a usual, smaller recipe, the Chromium recipe has a few
peculiarities:
- We manually patch parts of the build system to replace Chromium's bundled
  copies of some packages (flac, libjpeg and others) with system-wide ones.
- Parts of the V8 (Chromium's JavaScript engine) build need to run binaries
  built for the target, for which we use QEMU.

## Build requirements

This recipe requires clang, and GCC is not supported. Upstream Chromium has not
tested or officially supported GCC for years, so it is safer and easier to
follow their lead and only support one compiler.

As part of its build process, Chromium builds and runs some binaries on the
host. clang-native from the meta-clang layer is used to build those binaries.

Additionally, make sure the machine being used to build Chromium is powerful
enough: a x86-64 machine with at least 16GB RAM is recommended.

### dunfell-specific requirements

The dunfell OE/Yocto branch is an LTS release, which is often at odds with
Chromium's release model because it often requires recent versions of certain
recipes to build correctly.

This is particularly a problem for the toolchain (i.e. LLVM) and, at times,
node.js. As such, dunfell users **must** ensure the following:
* Chromium needs clang >= 12 and for that, meta-clang's **dunfell-clang12**
  branch needs to be used.
* Since Chromium 112, at least Nodejs 14 is required for parts of the build.
  meta-oe needs to be at least a commit
  116bfe8d5e5851e7fc5424f40da8691a19c5b5ee ("nodejs: make 14.18.1 available but
  not default") and `PREFERRED_VERSION_nodejs-native = "14.%"` needs to be
  added to `conf/local.conf`.

## PACKAGECONFIG knobs

* component-build: (off by default)
  Enables component build mode. By default, all of Chromium (with the exception
  of FFmpeg) is linked into one big binary. The linker step requires at least 8
  GB RAM. Component mode was created to facilitate development and testing,
  since with it, there is not one big binary; instead, each component is linked
  to a separate shared object. Use component mode for development, testing, and
  in case the build machine is not a 64-bit one, or has less than 8 GB RAM.

* cups: (off by default)
  Enables CUPS support in Chromium, and adds a dependency on the "cups" recipe.

* custom-libcxx (off by default)
  Enable vendored version of C++ runtime ( libc++ ) instead of using this from
  meta-clang provided libc++, this could be useful in some cases, where the
  binary is to be run on foreign systems which are not built using OE/Yocto
  base

* gtk4: (off by default)
  Enables GTK4 runtime support in Chromium by adding --gtk-version=4
  to the command line. Chromium is still built against GTK3.

* kiosk-mode: (off by default)
  Enable this option if you want your browser to start up full-screen, without
  any menu bars, without any clutter, and without any initial start-up
  indicators.

* proprietary-codecs: (off by default)
  Enable this option if you want to add support for additional proprietary
  codecs, most notably MPEG standards (h.264, h.265, MP4, MP3, AAC etc). It is
  your responsibility to make sure you are complying with the codecs' licensing
  terms.

* use-egl: (on by default)
  Without this packageconfig, the Chromium build will use GLX for creating an
  OpenGL context in X11, and regular OpenGL for painting operations. Neither
  are desirable on embedded platforms. With this packageconfig, EGL and OpenGL
  ES 2.x are used instead.

* use-vaapi: (off by default)
  Enables VA-API support with that allows hardware accelerated media playback on
  GPUs that support VA-API. If h.264 codec is required, proprietary-codecs must
  also be enabled. Please note that not all the possible hardware configs are
  tested and supported.

* upower: (on by default)
  Chromium expects the presence of `org.freedesktop.UPower` via D-Bus to
  query battery status. If disabled, there will be warning messages seen on
  stderr and Battery Status Web API will not work.

## Google API keys

Some Chromium features use Google APIs, and to access those APIs either an API
Key or a set of OAuth 2.0 tokens is required. Setting up API keys is optional.

If you don't do it, the specific APIs using Google services won't work in your
build, but all other features will run normally.

By default, we build Chromium with invalid keys to avoid the "Google API keys
are missing" error message in the browser's infobar. If you have your own API
keys, you need to set the GOOGLE_API_KEY, GOOGLE_DEFAULT_CLIENT_ID and
GOOGLE_DEFAULT_CLIENT_SECRET appropriately in your local.conf.

For more information, see https://dev.chromium.org/developers/how-tos/api-keys.
