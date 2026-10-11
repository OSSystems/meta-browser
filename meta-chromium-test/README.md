# Meta-Chromium-Test Layer

A comprehensive Yocto/OpenEmbedded layer for automated testing of Chromium browser builds across multiple architectures, Yocto versions, and C library implementations.

> **Note**: This layer is now integrated into the [meta-browser](https://github.com/OSSystems/meta-browser) repository as a subdirectory. When cloning meta-browser, you'll find this layer at `meta-browser/meta-chromium-test/`.

## Table of Contents

- [Overview](#overview)
- [Purpose](#purpose)
- [Features](#features)
- [Layer Structure](#layer-structure)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Configuration Matrix](#configuration-matrix)
- [Scripts Usage](#scripts-usage)
- [KAS Configuration](#kas-configuration)
- [Adding New Combinations](#adding-new-combinations)
- [Testing](#testing)
- [Recipes](#recipes)
- [Docker Infrastructure](#docker-infrastructure)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)

## Overview

The `meta-chromium-test` layer provides automated testing infrastructure for the Chromium browser on embedded Linux systems. It builds and tests Chromium for the current branch of meta-browser across multiple combinations of:

- **Architectures**: ARM, AArch64, RISC-V, x86-64
- **Chromium Variants**: Ozone Wayland, X11
- **C Library**: glibc (musl support removed for simplicity)

This layer is integrated into the [meta-browser](https://github.com/OSSystems/meta-browser) layer and uses [KAS](https://kas.readthedocs.io/) for reproducible builds. The testing infrastructure automatically adapts to the current branch of meta-browser (e.g., scarthgap, master) rather than maintaining separate configurations for each Yocto version.

## Purpose

This testing layer serves several key purposes:

1. **Automated CI/CD Testing**: Provides consistent testing across multiple configurations
2. **Regression Detection**: Catches issues early across different platforms and versions
3. **Quality Assurance**: Ensures Chromium builds work reliably on embedded systems
4. **Development Support**: Facilitates testing new features and configurations
5. **Performance Benchmarking**: Enables systematic performance testing

## Features

- **Matrix Testing**: Automated generation of test configurations for all supported combinations
- **KAS Integration**: Uses KAS for reproducible and isolated builds
- **QEMU Testing**: Automated testing using QEMU emulation
- **Smoke Tests**: Built-in smoke tests for basic Chromium functionality
- **Script Automation**: Comprehensive build, test, and validation scripts
- **Flexible Architecture**: Easy to extend with new configurations

## Layer Structure

```
meta-chromium-test/
├── conf/
│   └── layer.conf                      # Layer configuration
├── kas/                                # KAS configuration files
│   ├── chromium/                       # Chromium variant configs
│   │   ├── ozone-wayland.yml          # Wayland/Ozone configuration
│   │   └── x11.yml                     # X11 configuration
│   ├── repos/                          # Repository configurations
│   │   └── common-repos.yml           # Common repository definitions
│   ├── common.yml                     # Common build settings
│   ├── musl.yml                       # musl C library configuration
│   └── *.yml                          # Generated test configurations
├── recipes-core/
│   └── images/
│       └── chromium-test-image.bb     # Test image recipe
├── recipes-test/
│   └── chromium-tests/
│       ├── chromium-tests.bb          # Test package recipe
│       └── files/
│           └── chromium-smoke-test.sh # Smoke test script
├── scripts/
│   ├── generate_kas_files.sh          # Generate KAS configurations
│   ├── build.sh                       # Build automation script
│   ├── test.sh                        # Test automation script
│   └── validate.sh                    # Layer validation script
└── README.md                          # This file
```

## Prerequisites

- **Yocto/OpenEmbedded**: Compatible with kirkstone, scarthgap, walnascar, and master
- **KAS**: Version 3.0 or later
- **Python**: 3.8+
- **Git**: For repository management
- **Docker**: Required for the recommended testing infrastructure

### Infrastructure Requirements

The testing infrastructure expects the following base directory structure:

- `/yocto/` - Base directory for all builds and test images
- `/yocto/{yocto_version}/` - Version-specific build directories (created by GitHub Actions/CI)

The following subdirectories are created automatically by the `build.sh` script:
- `/yocto/yocto_dl/` - Download directory for source packages
- `/yocto/yocto_sstate_chromium/` - Shared state cache directory
- `/yocto/yocto_ccache/` - ccache directory for faster rebuilds
- `/yocto/test-images/` - Directory for storing built test images

**Note**: The recommended approach is to use the provided Docker infrastructure which handles these requirements automatically. Only the root `/yocto` directory needs manual setup.

### Required Layers

- `meta-browser/meta-chromium` - Chromium recipes
- `meta-openembedded/meta-oe` - Additional recipes
- `meta-clang` - Clang compiler support (for some configurations)

## Quick Start

### Recommended Method: Docker

The recommended approach is to use the Docker-based testing infrastructure which provides a consistent environment:

```bash
# Example: Build and test master branch with aarch64 ozone-wayland glibc
docker run --rm -it \
  -v /yocto:/yocto \
  -v /path/to/meta-browser:/src/meta-browser:ro \
  skandigraun/yocto:latest \
  bash -c '
    set -e
    mkdir -p /yocto/master && cd /yocto/master
    rm -rf meta-browser
    cp -r /src/meta-browser ./meta-browser
    ./meta-browser/meta-chromium-test/scripts/build.sh master aarch64 ozone-wayland glibc
    ./meta-browser/meta-chromium-test/scripts/test.sh master aarch64 ozone-wayland glibc
  '
```

**Docker Command Breakdown**:
- `-v /yocto:/yocto` - Mounts host `/yocto` directory for persistent builds
- `-v /path/to/meta-browser:/src/meta-browser:ro` - Mounts meta-browser layer (read-only, includes meta-chromium-test)
- `skandigraun/yocto:latest` - Pre-configured Yocto build environment

### Alternative Method: Native Setup

For native builds without Docker:

1. **Clone the required layers**:
   ```bash
   git clone https://github.com/OSSystems/meta-browser.git
   git clone https://github.com/openembedded/meta-openembedded.git
   git clone https://github.com/kraj/meta-clang.git
   ```
   
   Note: meta-chromium-test is now included as a subdirectory within meta-browser.

2. **Generate KAS files** (if not already present):
   ```bash
   cd meta-browser/meta-chromium-test
   ./scripts/generate_kas_files.sh
   ```

3. **Build a test image**:
   ```bash
   kas build meta-browser/meta-chromium-test/kas/glibc-kirkstone-ozone-wayland-x86-64-test.yml
   ```

4. **Run automated build and test**:
   ```bash
   ./meta-browser/meta-chromium-test/scripts/build.sh kirkstone x86-64 ozone-wayland
   ./meta-browser/meta-chromium-test/scripts/test.sh kirkstone x86-64 ozone-wayland
   ```

**Note**: Native setup requires manual creation of the `/yocto/` directory structure and proper environment configuration.

## Configuration Matrix

The layer supports the following configuration matrix for the current branch:

| Architectures | Chromium Variants | C Library |
|---------------|-------------------|-----------|
| arm, aarch64, riscv, x86-64 | ozone-wayland, x11 | glibc |

This generates **16 unique configurations** for comprehensive testing across all supported combinations. The layer automatically uses the correct Yocto configuration based on the meta-browser branch you're working with (e.g., scarthgap commits for scarthgap branch).

## Scripts Usage

**Important**: All scripts are designed to run within the Docker infrastructure. For native execution, ensure the `/yocto/` directory structure exists and is properly configured.

### Docker Usage Examples

```bash
# Build and test x86-64 ozone-wayland with glibc
docker run --rm -it \
  -v /yocto:/yocto \
  -v /path/to/meta-browser:/src/meta-browser:ro \
  skandigraun/yocto:latest \
  bash -c '
    set -e
    mkdir -p /yocto && cd /yocto
    rm -rf meta-browser
    cp -r /src/meta-browser ./meta-browser
    ./meta-browser/meta-chromium-test/scripts/build.sh x86-64 ozone-wayland chromium
    ./meta-browser/meta-chromium-test/scripts/test.sh x86-64 ozone-wayland
  '

# Test with aarch64 x11
docker run --rm -it \
  -v /yocto:/yocto \
  -v /path/to/meta-browser:/src/meta-browser:ro \
  skandigraun/yocto:latest \
  bash -c '
    set -e
    mkdir -p /yocto && cd /yocto
    rm -rf meta-browser
    cp -r /src/meta-browser ./meta-browser
    ./meta-browser/meta-chromium-test/scripts/build.sh aarch64 x11 chromium
    ./meta-browser/meta-chromium-test/scripts/test.sh aarch64 x11
  '
    ./meta-browser/meta-chromium-test/scripts/build.sh aarch64 x11 chromium musl
    ./meta-browser/meta-chromium-test/scripts/test.sh aarch64 x11 musl
  '
```

### generate_kas_files.sh

Generates all KAS configuration files for the supported matrix:

```bash
# Generate all KAS files
./scripts/generate_kas_files.sh
```

**Customizing the script**:
- Update `CHROMIUM_VERSIONS` for new Chromium variants  
- Add new architectures to `ARCHS` and update `arch_machine_dict`

### build.sh

Automated build script for specific configurations:

```bash
# Usage: build.sh arch chromium_version browser

# Examples (within Docker container):
./scripts/build.sh x86-64 ozone-wayland chromium
./scripts/build.sh aarch64 x11 chromium
./scripts/build.sh riscv ozone-wayland electron
```

**Script behavior**:
- Creates build directory structure in `/yocto/`
- Checks for existing builds to avoid rebuilding
- Uses KAS for reproducible builds
- Copies final images to `/yocto/test-images/` for testing

### test.sh

Automated testing using QEMU:

```bash
# Usage: test.sh arch chromium_version

# Examples (within Docker container):
./scripts/test.sh x86-64 ozone-wayland
./scripts/test.sh aarch64 x11
```

**Script behavior**:
- Looks for built images in `/yocto/test-images/`
- Launches QEMU with appropriate configuration
- Runs automated smoke tests
- Creates `.done` files to track completed tests

### validate.sh

Validates the layer structure and configuration:

```bash
./scripts/validate.sh
```

## KAS Configuration

The layer uses a modular KAS configuration approach:

### Base Configuration Files

- **`common.yml`**: Shared settings for all builds
- **`musl.yml`**: musl C library specific settings
- **`chromium/ozone-wayland.yml`**: Wayland/Ozone variant settings
- **`chromium/x11.yml`**: X11 variant settings

### Repository Configurations

- **`repos/common-repos.yml`**: Common repository definitions
- **`repos/{version}.yml`**: Version-specific repository commits and settings

### Generated Test Configurations

Each test configuration follows the naming pattern:
```
{libc}-{yocto_version}-{chromium_version}-{arch}-test.yml
```

Examples:
- `glibc-kirkstone-ozone-wayland-x86-64-test.yml`
- `musl-scarthgap-x11-aarch64-test.yml`

## Adding New Combinations

### Adding a New Yocto Version

1. **Create repository configuration**:
   ```bash
   cp kas/repos/common-repos.yml kas/repos/newfversion.yml
   # Edit newfversion.yml with appropriate repository commits
   ```

2. **Update generate_kas_files.sh**:
   ```bash
   YOCTO_VERSIONS=("newfversion")
   ```

3. **Regenerate KAS files**:
   ```bash
   ./scripts/generate_kas_files.sh
   ```

### Adding a New Architecture

1. **Update generate_kas_files.sh**:
   ```bash
   ARCHS=("arm" "aarch64" "riscv" "x86-64" "newarch")

   # Add machine mapping
   arch_machine_dict["newarch"]="qemunewarch"
   ```

2. **Update build.sh and test.sh** if needed for architecture-specific settings

3. **Regenerate configurations**:
   ```bash
   ./scripts/generate_kas_files.sh
   ```

### Adding a New Chromium Variant

1. **Create variant configuration**:
   ```yaml
   # kas/chromium/newvariant.yml
   header:
     version: 11

   local_conf_header:
     chromium-newvariant: |
       PREFERRED_PROVIDER_chromium = "chromium-newvariant"
       PACKAGECONFIG:append:pn-chromium-newvariant = " proprietary-codecs"
   ```

2. **Update generate_kas_files.sh**:
   ```bash
   CHROMIUM_VERSIONS=("ozone-wayland" "x11" "newvariant")
   ```

3. **Regenerate configurations**:
   ```bash
   ./scripts/generate_kas_files.sh
   ```

## Testing

### Smoke Tests

The layer includes basic smoke tests for Chromium functionality:

```bash
# Run smoke test on target
chromium-smoke-test
```

### Manual Testing

1. **Build and boot image**:
   ```bash
   # If running from meta-browser root:
   kas build meta-browser/meta-chromium-test/kas/glibc-kirkstone-ozone-wayland-x86-64-test.yml
   # If running from meta-chromium-test directory:
   kas build kas/glibc-kirkstone-ozone-wayland-x86-64-test.yml
   runqemu qemux86-64
   ```

2. **Test Chromium manually**:
   ```bash
   # On target system
   chromium --no-sandbox
   ```

### Automated Testing Pipeline

For CI/CD integration using Docker with ccache:

```bash
#!/bin/bash
# Example CI script for Docker-based testing with ccache

DOCKER_IMAGE="skandigraun/yocto:latest"
META_BROWSER_PATH="/path/to/meta-browser"
META_CHROMIUM_TEST_PATH="/path/to/meta-chromium-test"

# Test configurations
CONFIGS=(
    "kirkstone x86-64 ozone-wayland glibc"
    "scarthgap aarch64 x11 musl"
    "master riscv ozone-wayland glibc"
)

# Show initial ccache stats
echo "Initial ccache statistics:"
docker run --rm -v yocto:/yocto "$DOCKER_IMAGE" bash -c "
    export CCACHE_DIR=/yocto/yocto_ccache
    ccache -s 2>/dev/null || echo 'ccache not yet initialized'
"

for config in "${CONFIGS[@]}"; do
    read -r yocto_version arch chromium_version libc <<< "$config"

    echo "Testing: $config"

    docker run --rm -it \
        -v yocto:/yocto \
        -v "$META_BROWSER_PATH":/src/meta-browser:ro \
        -v "$META_CHROMIUM_TEST_PATH":/src/meta-chromium-test:ro \
        "$DOCKER_IMAGE" \
        bash -c "
            set -e
            # Only create Yocto version directory - build.sh handles the rest
            mkdir -p /yocto/$yocto_version && cd /yocto/$yocto_version
            rm -rf meta-browser meta-chromium-test
            cp -r /src/meta-browser ./meta-browser
            cp -r /src/meta-chromium-test ./meta-chromium-test
            ./meta-chromium-test/scripts/build.sh $yocto_version $arch $chromium_version $libc
            ./meta-chromium-test/scripts/test.sh $yocto_version $arch $chromium_version $libc
        " || exit 1
done

# Show final ccache stats
echo "Final ccache statistics:"
docker run --rm -v yocto:/yocto "$DOCKER_IMAGE" bash -c "
    export CCACHE_DIR=/yocto/yocto_ccache
    ccache -s
"

echo "All tests completed successfully with ccache acceleration!"
```

## Recipes

### chromium-test-image.bb

A comprehensive test image including:
- Both Chromium variants (Ozone Wayland and X11)
- Development and debugging tools
- Network utilities
- Font packages
- X11 server components

### chromium-tests.bb

Provides smoke tests and utility scripts for Chromium testing.

## Docker Infrastructure

### Container Setup

The testing infrastructure uses the `skandigraun/yocto:latest` Docker image which provides:

- Pre-configured Yocto build environment
- All required tools and dependencies
- Optimized build cache and download directories
- ccache support for faster rebuilds
- Consistent environment across different host systems

### Directory Structure

The Docker container uses the following directory structure (created automatically by `build.sh`):

```
/yocto/                           # Host directory mounted to container
├── yocto_dl/                     # Download cache (created by build.sh)
├── yocto_sstate_chromium/        # Shared state cache (created by build.sh)
├── yocto_ccache/                 # ccache directory (created by build.sh)
├── test-images/                  # Built test images (created by build.sh)
├── kirkstone/                    # Kirkstone builds (created as needed)
├── scarthgap/                    # Scarthgap builds (created as needed)
├── walnascar/                    # Walnascar builds (created as needed)
└── master/                       # Master builds (created as needed)
```

**Note**: Only the root `/yocto` directory needs to be set up manually. All subdirectories are created automatically by the build script with proper permissions.

### Volume Mounts

- `/yocto:/yocto` - Persistent build artifacts and caches
- `/path/to/meta-browser:/src/meta-browser:ro` - Meta-browser layer (read-only)
- `/path/to/meta-chromium-test:/src/meta-chromium-test:ro` - This test layer (read-only)

### Environment Variables

The Docker environment includes optimized settings for:
- Download directory: `/yocto/yocto_dl`
- Shared state directory: `/yocto/yocto_sstate_chromium`
- ccache directory: `/yocto/yocto_ccache` (up to 50GB cache)
- Source mirror URL for faster downloads
- Build optimizations for containerized environments

### ccache Configuration

The layer is configured with ccache to significantly speed up rebuilds:

- **Cache Location**: `/yocto/yocto_ccache`
- **Maximum Size**: 50GB (configurable in `kas/common.yml`)
- **Compression**: Enabled with level 6 for space efficiency
- **Shared Across**: All architectures and Yocto versions

**ccache Benefits**:
- **First Build**: Same speed as without ccache
- **Subsequent Builds**: 2-10x faster depending on changes
- **Cross-Architecture**: Shared cache across ARM, AArch64, x86-64, RISC-V
- **Cross-Version**: Shared cache across different Yocto versions

### Setting Up the Docker Volume

Before running any builds, you need to create the Docker volume with the required directory structure and proper permissions. This ensures that the `yoctouser` in the container can write to all necessary directories.

#### Step 1: Create the Docker Volume

```bash
# Create the named Docker volume
docker volume create yocto
```

#### Step 2: Initialize Basic Structure and Permissions

```bash
# Run a temporary container as root to set up basic structure and permissions
docker run --rm -v yocto:/yocto --user root skandigraun/yocto:latest bash -c "
  # Create the root /yocto directory with proper ownership
  mkdir -p /yocto
  
  # Set proper ownership for yoctouser (UID 1000, GID 1000 typically)
  chown -R 1000:1000 /yocto
  
  # Set proper permissions
  chmod -R 755 /yocto
  
  echo 'Docker volume setup completed successfully!'
  echo 'Note: Build-specific directories will be created automatically by build.sh'
"
```

**Note**: The build script (`build.sh`) automatically creates all required subdirectories (`yocto_dl`, `yocto_sstate_chromium`, `yocto_ccache`, `test-images`) with proper permissions when you run your first build. This eliminates the need to manually create these directories during setup.

#### Step 3: Verify the Setup

```bash
# Verify that yoctouser can write to the volume
docker run --rm -v yocto:/yocto skandigraun/yocto:latest bash -c "
  echo 'Testing write permissions...'
  touch /yocto/test-file
  ls -la /yocto/test-file
  rm /yocto/test-file
  echo 'Volume setup verified successfully!'
  echo 'Ready for builds - subdirectories will be created automatically'
"
```

#### Alternative: One-Command Setup

For convenience, you can set up everything in a single command:

```bash
# Create volume and set up basic structure in one go
docker volume create yocto && \
docker run --rm -v yocto:/yocto --user root skandigraun/yocto:latest bash -c "
  mkdir -p /yocto
  chown -R 1000:1000 /yocto
  chmod -R 755 /yocto
  echo 'Yocto Docker volume ready for use!'
  echo 'Build-specific directories will be created automatically by build.sh'
" && \
echo "Volume setup complete. You can now run builds with automatic ccache setup."

#### ccache Management

Monitor and manage ccache performance:

```bash
# Check ccache statistics
docker run --rm -v yocto:/yocto skandigraun/yocto:latest bash -c "
  export CCACHE_DIR=/yocto/yocto_ccache
  ccache -s
"

# Clear ccache if needed
docker run --rm -v yocto:/yocto skandigraun/yocto:latest bash -c "
  export CCACHE_DIR=/yocto/yocto_ccache
  ccache -C
"

# Set different cache size limit (e.g., 100GB)
docker run --rm -v yocto:/yocto skandigraun/yocto:latest bash -c "
  export CCACHE_DIR=/yocto/yocto_ccache
  ccache -M 100G
"
```

#### Troubleshooting Volume Setup

If you encounter permission issues:

1. **Check the yoctouser UID/GID in your container**:
   ```bash
   docker run --rm skandigraun/yocto:latest id yoctouser
   ```

2. **Adjust ownership if needed** (replace 1000:1000 with actual UID:GID):
   ```bash
   docker run --rm -v yocto:/yocto --user root skandigraun/yocto:latest bash -c "
     chown -R 1000:1000 /yocto
   "
   ```

3. **Remove and recreate volume if corrupted**:
   ```bash
   docker volume rm yocto
   # Then repeat the setup steps above
   ```

#### Volume Management

- **View volume information**:
  ```bash
  docker volume inspect yocto
  ```

- **Clean up old builds** (preserves download, sstate, and ccache):
  ```bash
  docker run --rm -v yocto:/yocto skandigraun/yocto:latest bash -c "
    rm -rf /yocto/*/build 2>/dev/null || true
    rm -rf /yocto/test-images/* 2>/dev/null || true
    echo 'Build artifacts cleaned, caches preserved'
    echo 'Note: Shared directories will be recreated automatically on next build'
  "
  ```

- **Complete volume cleanup** (removes everything including ccache):
  ```bash
  docker volume rm yocto
  ```

#### Performance Optimization

For optimal ccache performance:

1. **Monitor cache hit rate**:
   ```bash
   docker run --rm -v yocto:/yocto skandigraun/yocto:latest bash -c "
     export CCACHE_DIR=/yocto/yocto_ccache
     ccache -s | grep 'cache hit rate'
   "
   ```

2. **Adjust cache size** based on available disk space:
   - **Minimum recommended**: 20GB for basic usage
   - **Optimal**: 50GB for full matrix testing
   - **Maximum useful**: 100GB+ for extensive development

3. **Keep cache across container restarts** by using the persistent volume mount

Once the volume is set up, you can proceed with the Docker usage examples in the Quick Start section. Your builds will automatically benefit from ccache acceleration on subsequent runs.

## Troubleshooting

### Common Issues

1. **Build Failures**:
   ```bash
   # Clean previous builds (within Docker container)
   kas shell kas/your-config.yml -c "bitbake -c clean chromium-ozone-wayland"
   ```

2. **Missing Dependencies**:
   - Ensure all required layers are present in the Docker container
   - Check layer compatibility with `bitbake-layers show-layers`

3. **QEMU Issues**:
   - Verify QEMU machine configuration
   - Check available system resources
   - Ensure `/yocto/test-images/` contains the required images

4. **Docker Volume Issues**:
   - Verify `/yocto` directory exists on host system
   - Check volume mount permissions
   - Ensure sufficient disk space in `/yocto` directory

5. **KAS Problems**:
   ```bash
   # Validate KAS file (within Docker container)
   kas dump kas/your-config.yml
   ```

### Debug Mode

Enable debug output within Docker container:
```bash
kas --log-level debug build kas/your-config.yml
```

### Getting Help

- Check the [meta-browser documentation](https://github.com/OSSystems/meta-browser)
- Review [KAS documentation](https://kas.readthedocs.io/)
- Examine build logs in `build/tmp/log/` within the container
- For Docker-specific issues, check container logs and volume mounts

## Contributing

1. **Fork the repository**
2. **Create a feature branch**
3. **Add your changes**:
   - Update configurations as needed
   - Add new test cases
   - Update documentation
4. **Test your changes**:
   ```bash
   ./scripts/validate.sh
   ./scripts/generate_kas_files.sh
   ```
5. **Submit a pull request**

### Development Guidelines

- Follow existing naming conventions
- Update documentation for new features
- Test changes across multiple configurations
- Maintain backward compatibility
- Add appropriate error handling

## License

This layer is distributed under the MIT License. See the `COPYING.MIT` file for details.

## Maintainers

- See `MAINTAINERS` file for current maintainer information

---

For questions, issues, or contributions, please use the project's issue tracker or contact the maintainers directly.
