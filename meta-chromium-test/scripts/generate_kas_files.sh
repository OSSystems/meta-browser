#!/bin/bash

# Generate KAS files for all combinations
# Usage: generate_kas_files.sh [browser]
# browser: chromium, electron, or both (default)

BROWSER=${1:-both}
ARCHS=("arm" "aarch64" "riscv" "x86-64")

declare -A arch_machine_dict
arch_machine_dict["arm"]="qemuarm"
arch_machine_dict["aarch64"]="qemuarm64"
arch_machine_dict["riscv"]="qemuriscv64"
arch_machine_dict["x86-64"]="qemux86-64"

BASE_DIR="${KAS_BASE_DIR:-./kas}"

# Validate browser argument
if [ "$BROWSER" != "chromium" ] && [ "$BROWSER" != "electron" ] && [ "$BROWSER" != "both" ]; then
    echo "Error: Browser must be 'chromium', 'electron', or 'both'"
    echo "Usage: generate_kas_files.sh [browser]"
    exit 1
fi

# Determine which browsers to generate
if [ "$BROWSER" = "both" ]; then
    BROWSERS=("chromium" "electron")
else
    BROWSERS=("$BROWSER")
fi

echo "Generating KAS files for: ${BROWSERS[*]}"

for current_browser in "${BROWSERS[@]}"; do
    echo "Processing $current_browser..."
    
    # Define versions per browser
    if [ "$current_browser" = "electron" ]; then
        BROWSER_VERSIONS=("ozone-wayland" "ozone-x11")
    else
        BROWSER_VERSIONS=("ozone-wayland" "x11")
    fi

    for browser_version in "${BROWSER_VERSIONS[@]}"; do
        for arch in "${ARCHS[@]}"; do
            machine=${arch_machine_dict[$arch]}
            kas_file_name="glibc-$browser_version-$arch-$current_browser-test.yml"

            echo "Creating $kas_file_name"

            cat > "$BASE_DIR/$kas_file_name" << EOF
header:
  version: 11
  includes:
    - meta-chromium-test/kas/$current_browser/$browser_version.yml
    - meta-chromium-test/kas/repos/common-repos.yml
    - meta-chromium-test/kas/$current_browser-common.yml

machine: $machine

local_conf_header:
  image_name: |
    IMAGE_NAME = "glibc-$browser_version-${arch//-/_}-$current_browser"
EOF
        done
    done
done

echo "All KAS files generated successfully for: ${BROWSERS[*]}!"
