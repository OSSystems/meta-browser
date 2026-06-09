#!/bin/bash

# Validation script for meta-chromium-test layer
# This script checks if all required files are present and properly configured

echo "=== Meta-Chromium-Test Layer Validation ==="

LAYER_DIR="/home/cal/work/yocto/poky/sources/meta-chromium-test"
ERRORS=0

# Check basic layer structure
echo "1. Checking layer structure..."
if [ ! -f "$LAYER_DIR/conf/layer.conf" ]; then
    echo "ERROR: Missing layer.conf"
    ERRORS=$((ERRORS + 1))
fi

if [ ! -f "$LAYER_DIR/README.md" ]; then
    echo "ERROR: Missing README.md"
    ERRORS=$((ERRORS + 1))
fi

# Check scripts
echo "2. Checking scripts..."
for script in "build.sh" "test.sh" "generate_kas_files.sh"; do
    if [ ! -x "$LAYER_DIR/scripts/$script" ]; then
        echo "ERROR: Missing or not executable: scripts/$script"
        ERRORS=$((ERRORS + 1))
    fi
done

# Check recipes
echo "3. Checking recipes..."
if [ ! -f "$LAYER_DIR/recipes-core/images/chromium-test-image.bb" ]; then
    echo "ERROR: Missing chromium-test-image.bb"
    ERRORS=$((ERRORS + 1))
fi

if [ ! -f "$LAYER_DIR/recipes-test/chromium-tests/chromium-tests.bb" ]; then
    echo "ERROR: Missing chromium-tests.bb"
    ERRORS=$((ERRORS + 1))
fi

# Check KAS configurations
echo "4. Checking KAS configurations..."
kas_count=$(find "$LAYER_DIR/kas" -name "*.yml" | wc -l)
echo "Found $kas_count KAS configuration files"

if [ $kas_count -lt 60 ]; then
    echo "WARNING: Expected more KAS files (found $kas_count)"
fi

# Check chromium variants
if [ ! -f "$LAYER_DIR/kas/chromium/ozone-wayland.yml" ]; then
    echo "ERROR: Missing ozone-wayland.yml"
    ERRORS=$((ERRORS + 1))
fi

if [ ! -f "$LAYER_DIR/kas/chromium/x11.yml" ]; then
    echo "ERROR: Missing x11.yml"
    ERRORS=$((ERRORS + 1))
fi

# Check GitHub workflow
echo "5. Checking GitHub workflow..."
if [ ! -f "/home/cal/work/yocto/poky/meta-browser/.github/workflows/chromium.yml" ]; then
    echo "ERROR: Missing chromium.yml workflow"
    ERRORS=$((ERRORS + 1))
fi

# Validate layer.conf syntax
echo "6. Validating layer.conf syntax..."
if ! grep -q "chromium-test-layer" "$LAYER_DIR/conf/layer.conf"; then
    echo "ERROR: layer.conf missing chromium-test-layer collection"
    ERRORS=$((ERRORS + 1))
fi

if ! grep -q "chromium-browser-layer" "$LAYER_DIR/conf/layer.conf"; then
    echo "ERROR: layer.conf missing chromium-browser-layer dependency"
    ERRORS=$((ERRORS + 1))
fi

# Summary
echo "=== Validation Summary ==="
if [ $ERRORS -eq 0 ]; then
    echo "✓ All checks passed! Meta-chromium-test layer is properly configured."
    echo ""
    echo "You can now:"
    echo "1. Build with: ./scripts/build.sh kirkstone x86-64 ozone-wayland glibc"
    echo "2. Test with: ./scripts/test.sh kirkstone x86-64 ozone-wayland glibc"
    echo "3. Use KAS: kas build kas/glibc-kirkstone-ozone-wayland-x86-64-test.yml"
    echo ""
    echo "GitHub Actions workflow is available at:"
    echo "  .github/workflows/chromium.yml"
else
    echo "✗ $ERRORS errors found. Please fix them before using the layer."
fi

exit $ERRORS
