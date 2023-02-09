#!/usr/bin/env python3
"""
This script can be used to generate LIC_FILES_CHKSUM in chromium.inc.

It uses Chromium's own tools/licenses/licenses.py script to scan for third_party
directories and license files. This means its output is generated on a
best-effort basis, as some directories are non-compliant upstream or may not be
found. It might also include directories which are not used in a
Yocto/OpenEmbedded build.
"""

import argparse
import hashlib
import os
import sys

# These are directories that are known to cause licenses.LicenseError to be
# thrown because but should not cause a failure in this script for
# different reasons.
SKIPPED_DIRECTORIES = (
    # These directories are not part of the Chromium tarballs (upstream's
    # export_tarball.py declares them "non-essential" or plain test
    # directories, and their README.chromium in the git repositories mark
    # them as NOT_SHIPPED).
    'chrome/test/data',
    'third_party/hunspell_dictionaries',

    # android_protobuf is checked out and used only in Android builds, so a
    # LicenseError will be thrown because README.chromium will point to a
    # file that is not present in a Chromium tarball.
    'third_party/android_protobuf',

    # Starting with M61, Chromium is shipping its own pinned version of
    # depot_tools. It's only part of the build and the directory structure
    # does not follow the standard. Skip it.
    'third_party/depot_tools',

    # M63 and later: we do not consume win_build_output.
    'third_party/win_build_output',

    # M67: third_party/fuchsia-sdk has no LICENSE file. This is not used in
    # Linux builds though.
    # https://bugs.chromium.org/p/chromium/issues/detail?id=847821
    'third_party/fuchsia-sdk',
)


def find_chromium_licenses(chromium_root):
    """Look for license files in a Chromium checkout and return a set with all
    files that are actually shipped and used in the final Chromium binary."""
    try:
        import licenses
    except ImportError:
        raise ImportError('Failed to import licenses.py. Make sure %s '
                          'contains tools/licenses/licenses.py.' % chromium_root)

    # Make sure the main Chromium LICENSE file is always present.
    license_files = set([os.path.join(chromium_root, 'LICENSE')])

    for d in licenses.FindThirdPartyDirs(licenses.PRUNE_PATHS, chromium_root):
        if d in SKIPPED_DIRECTORIES:
            continue
        try:
            metadata = licenses.ParseDir(d, chromium_root)
        except licenses.LicenseError as e:
            print('Exception in directory %s: %s' % (d, e))
            if input('Ignore (y)? ') == 'y':
                continue
            raise
        # We are not interested in licenses for projects that are not marked as
        # used in the final product (ie. they might be optional development
        # aids, or only used in a build).
        if metadata['License File'] != licenses.NOT_SHIPPED:
            license_files.add(metadata['License File'])
    return license_files


def print_license_list(chromium_root, output_file):
    """Print a list of Chromium license paths and checksums in a format
    suitable for use in a Yocto recipe."""
    licenses = {}
    for license_file in find_chromium_licenses(chromium_root):
        with open(license_file, 'rb') as file_handle:
            license_hash = hashlib.md5(file_handle.read()).hexdigest()
        license_relpath = os.path.relpath(license_file, chromium_root)
        licenses[license_relpath] = license_hash
    with open(output_file, 'w') as out:
        out.write('LIC_FILES_CHKSUM = "\\\n')
        for f in sorted(licenses):
            out.write('    file://${S}/%s;md5=%s \\\n' % (f, licenses[f]))
        out.write('    "\n')


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('chromium_root',
                        help='Path to the root directory of a Chromium '
                        'checkout or extracted tarball.')
    parser.add_argument('output_file',
                        help='File to write the output to (it will be '
                        'overwritten)')
    args = parser.parse_args()

    tools_licenses_dir = os.path.join(args.chromium_root, 'tools/licenses')
    if not os.path.isdir(tools_licenses_dir):
        print('%s does not look like a valid directory.' % tools_licenses_dir)
        sys.exit(1)
    sys.path = [tools_licenses_dir] + sys.path

    print_license_list(args.chromium_root, args.output_file)
