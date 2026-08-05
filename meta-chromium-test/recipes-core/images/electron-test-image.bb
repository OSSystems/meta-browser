# Use core-image-minimal as base and build up from there
inherit core-image

# Install the preferred Electron provider (set via KAS configuration)
IMAGE_INSTALL += "${PREFERRED_PROVIDER_electron} electron-tests"

SUMMARY = "Test image for Electron application builds"
DESCRIPTION = "This image contains the configured Electron variant for testing purposes."

# Development and debugging tools
IMAGE_INSTALL += " \
    strace \
    gdb \
    valgrind \
    procps \
    util-linux \
    coreutils \
"

# Network tools for testing
IMAGE_INSTALL += " \
    curl \
    wget \
    openssh-sftp-server \
    openssh-scp \
"

# Wayland support for electron-ozone-wayland
IMAGE_INSTALL += " \
    weston \
    weston-init \
    wayland-protocols \
"

# Additional features for testing
EXTRA_IMAGE_FEATURES += "allow-empty-password allow-root-login ssh-server-openssh tools-debug"

# Ensure Wayland is enabled for electron testing
REQUIRED_DISTRO_FEATURES = "wayland"
