SOURCES:=src test/src cap/src cap/test/src
LIBS:=lib

-include abe.mk

TEST_RUNNER:=bin/run
ifdef INCLUDE
TEST_RUNNER:=$(TEST_RUNNER) -Djunit.include=$(INCLUDE)
endif
ifdef EXCLUDE
TEST_RUNNER:=$(TEST_RUNNER) -Djunit.exclude=$(EXCLUDE)
endif
TEST_RUNNER:=$(TEST_RUNNER) com.redhat.persistence.TestRunner

tests: all
	$(TEST_RUNNER) `find $(SOURCES) -name "*Suite.java" -printf "%P " | sed s@[.]java@@g | sed s@/@.@g`

jdo-tests: all
	$(TEST_RUNNER) com.redhat.persistence.jdo.JDOSuite

persistence-tests: all
	$(TEST_RUNNER) com.arsdigita.persistence.PersistenceSuite

oql-tests: all
	$(TEST_RUNNER) com.redhat.persistence.oql.QuerySuite

data-tests: all
	$(TEST_RUNNER) com.arsdigita.persistence.tests.data.DataSuite

pdl-tests: all
	$(TEST_RUNNER) com.redhat.persistence.pdl.PDLSuite \
		com.arsdigita.persistence.pdl.PDLSuite
