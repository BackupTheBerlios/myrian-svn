SOURCES:=src test/src cap/src cap/test/src
LIBS:=lib

-include abe.mk

ifndef TEST_RUNNER
TEST_RUNNER=com.redhat.persistence.TestRunner
endif

TEST_CMD:=bin/run
ifdef INCLUDE
TEST_CMD:=$(TEST_CMD) -Djunit.include=$(INCLUDE)
endif
ifdef EXCLUDE
TEST_CMD:=$(TEST_CMD) -Djunit.exclude=$(EXCLUDE)
endif
TEST_CMD:=$(TEST_CMD) $(TEST_RUNNER)

tests: all
	$(TEST_CMD) `find $(SOURCES) -name "*Suite.java" -printf "%P " | sed s@[.]java@@g | sed s@/@.@g`

jdo-tests: all
	$(TEST_CMD) com.redhat.persistence.jdo.JDOSuite

persistence-tests: all
	$(TEST_CMD) com.arsdigita.persistence.PersistenceSuite

oql-tests: all
	$(TEST_CMD) com.redhat.persistence.oql.QuerySuite

data-tests: all
	$(TEST_CMD) com.arsdigita.persistence.tests.data.DataSuite

pdl-tests: all
	$(TEST_CMD) com.redhat.persistence.pdl.PDLSuite \
		com.arsdigita.persistence.pdl.PDLSuite
