SOURCES:=src test/src cap/src cap/test/src
LIBS:=lib

-include abe.mk

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
