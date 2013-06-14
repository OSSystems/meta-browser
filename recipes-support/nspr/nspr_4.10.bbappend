# nspr-native is needed by nss
BBCLASSEXTEND = "native"

# don't build tests : this ICE on armv7
do_configure_append() {
	sed -i "s:CSRCS =:CSRCS = \nDONTBUILD =:g" ${S}/pr/tests/Makefile
}

# HACK : to get do_install working when tests are not built
do_install_prepend () {
	for i in ${TESTS}; do
		touch ${S}/pr/tests/$i
	done
}

# FIXME : wtihout this nss will depend on nspr-dev
FILES_${PN} = "${bindir}/* ${libdir}/*.so"
