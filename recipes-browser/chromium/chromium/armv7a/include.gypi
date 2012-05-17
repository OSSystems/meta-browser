{
  'variables': {

    # Configure for armv7 compilation
    'target_arch': 'arm',
    'armv7': 1,
    'arm_neon': 1,

    # Disable native client (Google's settings)
    'disable_nacl': 1,

    # V8 config (Google's settings)
    'v8_use_snapshot': 'false',
    
    # No ffmpeg binaries in the tree, so make sure the build will not fail
    'use_system_ffmpeg' : '1',	

    # Needed for ARM compilation (build fails otherwise)
    'linux_use_tcmalloc': 0,

    # Change to your rootfs path
    'sysroot': '__PATH__TO_BE_REPLACED__',

    #'arm_thumb': 1,
  }
}
