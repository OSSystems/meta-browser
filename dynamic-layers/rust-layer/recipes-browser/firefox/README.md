Firefox recipe
===============

This recipe provides a package for the Firefox web browser.

Build requirements
------------------

* GCC 4.9 or 7
* [meta-rust](https://github.com/meta-rust/meta-rust)
  * Need Rust-1.24 or later

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

* glx: (off by default)
  Use GLX to create OpenGL context on X11. Although GLX feature is built-in on
  X11 but not enabled against most GPUs by default. This option enables it
  forcedly.

* egl: (off by default)
  Use EGL instead of GLX to create OpenGL context. It may be desired than GLX
  on embedded platforms. On wayland, this is the only way to enable GPU
  acceleration.

* openmax: (off by default)
  Enable OpenMAX IL decoder to play H.264 video.
  This features is confirmed only on Renesas RZ/G1.

* webgl: (off by default)
  Firefox on Linux doesn't enable WebGL against most GPUs by default. This
  option adds a config file to enable it focedly.

* canvas-gpu: (off by default)
  Firefox on Linux doesn't enable GPU acceleration for 2D canvas by default.
  This option enables it focedly.

* stylo: (off by default)
  This option enables Quantum CSS (aka Stylo) feature of Firefox.
  You need clang-3.9 & llvm-3.9 packages of host to build it. In addition you
  need to add the following setting in your local.conf:
  `HOSTTOOLS += " llvm-config-3.9 "`

* disable-e10s: (off by default)
  This option disables Electrolysis (e10s) fearture which hosts, renders, or
  executes web related content in background child processes. This feature
  improves security and reactivity of UI, but may eat much more memory or may
  breaks GPU acceleration on certain platforms.

* forbit-multiple-compositors: (off by default)
  This option allows to create only one GPU accelerated compositor, second and
  the following windows will use basic compositors. Multiple compositor may
  cause crash on platforms that doesn't support multiple EGL windows.
