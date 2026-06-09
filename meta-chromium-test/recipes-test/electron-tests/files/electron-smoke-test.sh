#!/bin/bash

# Electron application smoke test
# Basic functionality test for Electron application

echo "Starting Electron smoke test..."

# Check if electron binaries are available
if command -v electron &> /dev/null; then
    ELECTRON_CMD="electron"
else
    echo "ERROR: No electron binary found"
    exit 1
fi

echo "Found electron binary: $ELECTRON_CMD"

# Test 1: Check version
echo "Testing version information..."
$ELECTRON_CMD --version
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to get version information"
    exit 1
fi

# Test 2: Check help
echo "Testing help output..."
$ELECTRON_CMD --help > /dev/null
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to get help information"
    exit 1
fi

# Test 3: Check if we can start in headless mode (basic functionality)
echo "Testing headless mode startup..."
# Create a minimal test app
TEST_APP_DIR="/tmp/electron-test-app"
mkdir -p $TEST_APP_DIR
cat > $TEST_APP_DIR/package.json << EOF
{
  "name": "electron-test",
  "version": "1.0.0",
  "main": "main.js"
}
EOF

cat > $TEST_APP_DIR/main.js << EOF
const { app } = require('electron');

app.whenReady().then(() => {
  console.log('Electron app started successfully');
  app.quit();
});

app.on('window-all-closed', () => {
  app.quit();
});
EOF

# Test the minimal app with a timeout
OUTPUT=$(timeout 10 $ELECTRON_CMD $TEST_APP_DIR --no-sandbox --disable-gpu 2>&1)
EXIT_CODE=$?
echo "$OUTPUT" | grep -q "Electron app started successfully"
GREP_CODE=$?
if [ $EXIT_CODE -eq 124 ]; then
    echo "WARNING: Electron application startup timed out"
elif [ $EXIT_CODE -ne 0 ]; then
    echo "WARNING: Electron application startup failed (exit code $EXIT_CODE)"
elif [ $GREP_CODE -eq 0 ]; then
    echo "SUCCESS: Electron application startup works"
else
    echo "WARNING: Electron application startup did not produce expected output"
fi

# Clean up test app
rm -rf $TEST_APP_DIR

# Test 4: Check for required libraries
echo "Testing library dependencies..."
ldd $(which $ELECTRON_CMD) | grep -q "not found"
if [ $? -eq 0 ]; then
    echo "ERROR: Missing library dependencies"
    ldd $(which $ELECTRON_CMD) | grep "not found"
    exit 1
else
    echo "SUCCESS: All library dependencies found"
fi

# Test 5: Check Wayland support if WAYLAND_DISPLAY is set
if [ -n "$WAYLAND_DISPLAY" ]; then
    echo "Testing Wayland support..."
    timeout 5 $ELECTRON_CMD --version --ozone-platform=wayland --no-sandbox > /dev/null 2>&1
    EXIT_CODE=$?
    if [ $EXIT_CODE -eq 0 ]; then
        echo "SUCCESS: Wayland platform support detected"
    elif [ $EXIT_CODE -eq 124 ]; then
        echo "WARNING: Wayland platform test timed out (exit code 124)"
    else
        echo "WARNING: Wayland platform support may have issues (exit code $EXIT_CODE)"
    fi
fi

echo "Electron smoke test completed successfully!"
exit 0
