SOURCES:=src test/src cap/src cap/test/src
LIBS:=lib

-include abe.mk

run-tests: $(JSTAMPS)
	bin/run-tests

jdo-tests: $(JSTAMPS)
	bin/run com.redhat.persistence.TestRunner \
		com.redhat.persistence.jdo.JDOSuite

persistence-tests: $(JSTAMPS)
	bin/run com.redhat.persistence.TestRunner \
		com.arsdigita.persistence.PersistenceSuite

oql-tests: $(JSTAMPS)
	bin/run com.redhat.persistence.TestRunner \
		com.redhat.persistence.oql.QuerySuite
