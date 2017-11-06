LICENSE = "BSD"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE_x86 = "(.*)"
COMPATIBLE_MACHINE_x86-64 = "(.*)"
COMPATIBLE_MACHINE_armv6 = "(.*)"
COMPATIBLE_MACHINE_armv7a = "(.*)"
COMPATIBLE_MACHINE_armv7ve = "(.*)"
COMPATIBLE_MACHINE_aarch64 = "(.*)"

# The build type defaults to Release. If you want a Debug build, you can set
# the variable CHROMIUM_BUILD_TYPE to "Debug" in your conf/local.conf file.
CHROMIUM_BUILD_TYPE ??= "Release"

inherit pythonnative
