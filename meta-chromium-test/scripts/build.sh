#!/bin/bash

if [ $# -le 2 ]; then
  echo Usage: $0 arch browser_version browser
  echo E.g. $0 aarch64 ozone-wayland electron
  echo E.g. $0 aarch64 ozone-wayland chromium
  exit 1
fi

declare -A arch_qemu_dict
arch_qemu_dict["arm"]="qemuarm"
arch_qemu_dict["aarch64"]="qemuarm64"
arch_qemu_dict["riscv"]="qemuriscv64"
arch_qemu_dict["x86-64"]="qemux86-64"

arch=$1
browser_version=$2
browser=$3

kas_file_name=glibc-$browser_version-$arch-$browser

# check if it needs to be built, if the flag has been set
#if [ -d /yocto/test-images/$kas_file_name ]; then
#  echo Image has been already built, exiting.
#  exit 0
#fi

cd /yocto/meta-browser

# Ensure all required Yocto subdirectories exist with proper permissions
# This creates the shared directories that are used across all builds
mkdir -p /yocto/yocto_dl
mkdir -p /yocto/yocto_sstate_chromium
mkdir -p /yocto/yocto_ccache
mkdir -p /yocto/test-images
chmod 755 /yocto/yocto_dl /yocto/yocto_sstate_chromium /yocto/yocto_ccache /yocto/test-images

if [ "$arch" = "riscv" ]; then
  OPENSBI="opensbi"
else
  OPENSBI=""
fi

qemu_machine=${arch_qemu_dict[$arch]}

rm -rf ../build/tmp/deploy/images/$qemu_machine

kas checkout --update meta-chromium-test/kas/$kas_file_name-test.yml || exit 1

# Clean previous builds - adjust packages based on browser
if [ "$browser" = "electron" ]; then
  BROWSER_PACKAGES="electron-ozone-wayland electron-ozone-x11"
else
  BROWSER_PACKAGES="chromium-ozone-wayland chromium-x11"
fi

kas shell meta-chromium-test/kas/$kas_file_name-test.yml -c "bitbake -c clean $BROWSER_PACKAGES \
         virtual/kernel $OPENSBI" || exit 1

# Build the image
kas build meta-chromium-test/kas/$kas_file_name-test.yml || exit 1

# Copy the built image to test images directory
echo "Checking for image directories using KAS..."

# Use KAS to get the actual build directory and DEPLOY_DIR
BUILD_DIR=$(kas shell meta-chromium-test/kas/$kas_file_name-test.yml -c 'echo $BUILDDIR' 2>/dev/null | tail -1)
DEPLOY_DIR=$(kas shell meta-chromium-test/kas/$kas_file_name-test.yml -c 'echo $DEPLOY_DIR' 2>/dev/null | tail -1)

echo "KAS Build directory: $BUILD_DIR"
echo "KAS Deploy directory: $DEPLOY_DIR" 

# Construct the image path
if [ -n "$DEPLOY_DIR" ]; then
  IMAGE_DIR="$DEPLOY_DIR/images/$qemu_machine"
elif [ -n "$BUILD_DIR" ]; then
  IMAGE_DIR="$BUILD_DIR/tmp/deploy/images/$qemu_machine"
else
  # Fallback to relative path
  IMAGE_DIR="build/tmp/deploy/images/$qemu_machine"
fi

echo "Looking for images in: $IMAGE_DIR"

if [ -d "$IMAGE_DIR" ]; then
  echo "Copying built image to /yocto/test-images/$kas_file_name"
  rm -rf /yocto/test-images/$kas_file_name
  cp -r "$IMAGE_DIR" /yocto/test-images/$kas_file_name
  echo "Successfully created test image: $kas_file_name"
  ls -la /yocto/test-images/$kas_file_name
else
  echo "ERROR: Built image directory not found: $IMAGE_DIR"
  echo "Available directories in deploy area:"
  if [ -n "$DEPLOY_DIR" ] && [ -d "$DEPLOY_DIR" ]; then
    find "$DEPLOY_DIR" -name images -type d 2>/dev/null || echo "No images directory found in $DEPLOY_DIR"
  fi
  exit 1
fi
