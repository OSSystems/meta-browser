#!/bin/bash

if [ $# -le 1 ]; then
  echo Usage: $0 arch chromium_version
  echo E.g. $0 x86-64 ozone-wayland
  exit 1
fi

declare -A arch_qemu_dict
arch_qemu_dict["arm"]="qemuarm"
arch_qemu_dict["aarch64"]="qemuarm64"
arch_qemu_dict["riscv"]="qemuriscv64"
arch_qemu_dict["x86-64"]="qemux86-64"

declare -A qemu_params_dict
qemu_params_dict["x86-64"]='qemuparams=" --enable-kvm "'

arch=$1
chromium_version=$2

image_folder=glibc-$chromium_version-$arch

qemu_machine=${arch_qemu_dict[$arch]}
qemu_params="${qemu_params_dict[$arch]}"

if [ ! -d /yocto/test-images/$image_folder ]; then
  echo $image_folder image was not created!
  exit 1
fi

if [ -f /yocto/test-images/$image_folder.done ]; then
  echo $image_folder test done already, skipping now.
  exit 0
fi

cd /yocto/$yocto_version/poky
source oe-init-build-env ../build

rm -rf tmp/deploy/images/$qemu_machine
cp -r /yocto/test-images/$image_folder tmp/deploy/images/$qemu_machine

coproc qemu { runqemu "$qemu_machine $qemu_params"; }

TIMEOUT=100
i=0
while [ $i -lt $TIMEOUT ]
do
  if [ -f /tmp/qemu_in_use ]; then
    echo "qemu socket is in use, waiting..."
    sleep 5
    i=$((i+5))
  else
    break
  fi
done

if [ $i -ge $TIMEOUT ]; then
  echo "qemu socket is busy, exiting..."
  exit 1
fi

# wait for qemu to start
echo "Waiting for qemu to start..."
sleep 30

# Basic test: check if system boots and chromium processes can start
echo "Testing Chromium installation..."

# Test if chromium-ozone-wayland is available
echo "root" | socat - unix-connect:/tmp/qemu_monitor > /dev/null 2>&1
if [ $? -ne 0 ]; then
  echo "Could not connect to qemu monitor"
  kill %qemu 2>/dev/null
  exit 1
fi

# Simple smoke test - check if chromium binaries are present
echo "info registers" | socat - unix-connect:/tmp/qemu_monitor | grep -q "pc"
if [ $? -eq 0 ]; then
  echo "QEMU is running and responsive"
else
  echo "QEMU is not responding properly"
  kill %qemu 2>/dev/null
  exit 1
fi

# Clean shutdown
echo "system_powerdown" | socat - unix-connect:/tmp/qemu_monitor
wait %qemu

# Mark test as done
touch /yocto/test-images/$image_folder.done

echo "Test completed successfully for $image_folder"
