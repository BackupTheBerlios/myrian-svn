SOURCES:=src test/src cap/src cap/test/src
LIBS:=lib

-include abe.mk

tests: all
	bin/run com.redhat.persistence.TestRunner \
		`find $(SOURCES) -name "*Suite.java" -printf "%P " | sed s@[.]java@@g | sed s@/@.@g`

run-tests: all
	bin/run-tests

jdo-tests: all
	bin/run com.redhat.persistence.TestRunner \
		com.redhat.persistence.jdo.JDOSuite

persistence-tests: all
	bin/run com.redhat.persistence.TestRunner \
		com.arsdigita.persistence.PersistenceSuite

oql-tests: all
	bin/run com.redhat.persistence.TestRunner \
		com.redhat.persistence.oql.QuerySuite

data-tests: all
	bin/run com.redhat.persistence.TestRunner \
		com.arsdigita.persistence.tests.data.DataSuite

pdl-tests: all
	bin/run com.redhat.persistence.TestRunner \
		com.redhat.persistence.pdl.PDLSuite \
		com.arsdigita.persistence.pdl.PDLSuite
