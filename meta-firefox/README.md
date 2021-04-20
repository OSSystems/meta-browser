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

* URI: git://github.com/meta-rust/meta-rust
  - branch: master
  - revision: HEAD

* python2.7 and python on host for HOSTTOOLS
  - e.g. on newer ubuntu which doesn't install python2 at all by default
    you need to install python-is-python2 (which will pull python2-minimal/python2.7-minimal)

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
  This features is confirmed only on Renesas RZ/G1.

* webgl: (off by default)
  Firefox on Linux doesn't enable WebGL against most GPUs by default. This
  option adds a config file to enable it focedly.

* forbit-multiple-compositors: (off by default)
  This option allows to create only one GPU accelerated compositor, second and
  the following windows will use basic compositors. Multiple compositor may
  cause crash on platforms that doesn't support multiple EGL windows.

Runtime options
---------------
* The enviromental variable `GDK_BACKEND=wayland` is needed to run Firefox with
  the wayland backend.
