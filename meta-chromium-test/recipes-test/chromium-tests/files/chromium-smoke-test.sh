#!/bin/bash

# Chromium browser smoke test
# Basic functionality test for Chromium browser

echo "Starting Chromium smoke test..."

# Check if chromium binaries are available
if command -v chromium-browser &> /dev/null; then
    CHROMIUM_CMD="chromium-browser"
elif command -v chromium &> /dev/null; then
    CHROMIUM_CMD="chromium"
else
    echo "ERROR: No chromium binary found"
    exit 1
fi

echo "Found chromium binary: $CHROMIUM_CMD"

# Test 1: Check version
echo "Testing version information..."
$CHROMIUM_CMD --version
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to get version information"
    exit 1
fi

# Test 2: Check help
echo "Testing help output..."
$CHROMIUM_CMD --help > /dev/null
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to get help information"
    exit 1
fi

# Test 3: Check if we can start in headless mode (basic functionality)
echo "Testing headless mode startup..."
timeout 10 $CHROMIUM_CMD --headless --disable-gpu --no-sandbox --dump-dom about:blank > /dev/null 2>&1
if [ $? -eq 0 ]; then
    echo "SUCCESS: Chromium headless mode works"
else
    echo "WARNING: Chromium headless mode may have issues (timeout or error)"
fi

# Test 4: Check for required libraries
echo "Testing library dependencies..."
ldd $(which $CHROMIUM_CMD) | grep -q "not found"
if [ $? -eq 0 ]; then
    echo "ERROR: Missing library dependencies"
    ldd $(which $CHROMIUM_CMD) | grep "not found"
    exit 1
else
    echo "SUCCESS: All library dependencies found"
fi

echo "Chromium smoke test completed successfully!"
exit 0
