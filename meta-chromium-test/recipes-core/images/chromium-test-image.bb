# Use core-image-minimal as base and build up from there
inherit core-image

# Install the preferred Chromium provider (set via KAS configuration)
IMAGE_INSTALL += "${PREFERRED_PROVIDER_chromium} chromium-tests"

SUMMARY = "Test image for Chromium browser builds"
DESCRIPTION = "This image contains the configured Chromium variant for testing purposes."

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

# Additional features for testing
EXTRA_IMAGE_FEATURES += "allow-empty-password allow-root-login ssh-server-openssh tools-debug"
