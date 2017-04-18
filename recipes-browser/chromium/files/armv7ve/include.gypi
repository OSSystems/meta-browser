{
  'variables': {
    # Configure for armv7 compilation
    'target_arch': 'arm',
    'armv7': 1,
    'arm_arch': 'armv7ve',
    'arm_thumb': 1,
    'arm_neon': 1,
  'cflags!': [
    # Some components in Chromium (e.g. v8, skia, ffmpeg)
    # define their own cflags for arm builds that could
    # conflict with the flags we set here (e.g.
    # '-mcpu=cortex-a9'). Remove these flags explicitly.
    '-march=armv7-a',
    '-mtune=cortex-a8',
  ],
  },
}
