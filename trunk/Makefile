MODULES:=myrian tests cap cap-tests

tests.deps:=myrian
cap.deps:=myrian
cap-tests.deps:=cap tests

-include module.mk

help:
	$(MODULE_HELP)

ifndef TEST_RUNNER
TEST_RUNNER=org.myrian.persistence.TestRunner
endif

TEST_CMD:=java -classpath $(CLASSPATH):$(MODULE_CLASSPATH)
ifdef INCLUDE
TEST_CMD:=$(TEST_CMD) -Djunit.include=$(INCLUDE)
endif
ifdef EXCLUDE
TEST_CMD:=$(TEST_CMD) -Djunit.exclude=$(EXCLUDE)
endif
TEST_CMD:=$(TEST_CMD) $(TEST_RUNNER)

all-tests: all
	@$(TEST_CMD) `find $(MODULES:%=%/src) -name "*Suite.java" -printf "%P " | sed s@[.]java@@g | sed s@/@.@g` \
	    $(TEST_RUNNER_ARGS)

jdo-tests: all
	@$(TEST_CMD) org.myrian.persistence.jdo.JDOSuite $(TEST_RUNNER_ARGS)

persistence-tests: all
	@$(TEST_CMD) com.arsdigita.persistence.PersistenceSuite \
	    $(TEST_RUNNER_ARGS)

oql-tests: all
	@$(TEST_CMD) org.myrian.persistence.oql.QuerySuite $(TEST_RUNNER_ARGS)

data-tests: all
	@$(TEST_CMD) com.arsdigita.persistence.tests.data.DataSuite \
	    $(TEST_RUNNER_ARGS)

pdl-tests: all
	@$(TEST_CMD) org.myrian.persistence.pdl.PDLSuite \
	    com.arsdigita.persistence.pdl.PDLSuite $(TEST_RUNNER_ARGS)

engine-tests: all
	@$(TEST_CMD) org.myrian.persistence.engine.rdbms.RDBMSSuite \
	    $(TEST_RUNNER_ARGS)
