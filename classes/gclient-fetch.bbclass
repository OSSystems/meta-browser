#
# gclient-fetch class
#
# Registers GCLIENT method for Bitbake fetch2.
#
# Adds support for following format in recipe SRC_URI:
# gclient://<packagename>;version=<version>
#
#
DEPENDS_prepend = "gclient-native xz-native"
do_fetch[depends] += "gclient-native:do_populate_sysroot xz-native:do_populate_sysroot"
EXTRANATIVEPATH += "gclient-native"

GCLIENT ?= "gclient"

python () {
        import gclient
        bb.fetch2.methods.append( gclient.Gclient() )
}
