ifndef BUILD
BUILD=build
endif

ifndef DRIVERS
DRIVERS := org.postgresql.Driver
endif

empty:=
space:=$(empty) $(empty)

ifndef JAVACC
JAVACC=sh javacc-3.2/bin/javacc
endif

ifndef JJTREE
JJTREE=sh javacc-3.2/bin/jjtree
endif

SOURCES:=src test/src cap/src cap/test/src
CONF   := conf

SOURCEPATH:=$(subst $(space),:,$(SOURCES:%=$(BUILD)/%))

CLASSPATH:=$(CLASSPATH):$(subst $(space),:,$(wildcard lib/*.jar))
CLASSPATH:=$(CLASSPATH):$(subst $(space),:,$(wildcard lib/*.zip))
CLASSPATH:=$(CLASSPATH):$(SOURCEPATH):$(CONF)

JSTAMPS:=$(SOURCES:%=$(BUILD)/%.jstamp)
JARS:=$(SOURCES:%=$(BUILD)/%.jar)

JD:=javadoc
JD_FLAGS:=-version -author -quiet
DOC_DEST:=$(BUILD)/doc

ifndef JDO_ENHANCE
JDO_ENHANCE=java -classpath lib/jdo.jar:jdori/jdori.jar:jdori/jdori-enhancer.jar com.sun.jdori.enhancer.Main
endif

.PHONY: all jars clean docs tags runtests

all: $(JSTAMPS)

jars: $(JARS)

-include $(SOURCES:%=$(BUILD)/%.jmake)

clean:
	rm -rf $(BUILD)

docs:
	@mkdir -p $(DOC_DEST)
	$(JD) $(JD_FLAGS) -d $(DOC_DEST) `find $(SOURCES) -name "*.java"`

tags:
	bin/jtags TAGS $(SOURCES)

$(BUILD)/%.jmake: %
	@echo generating $@
# header
	@mkdir -p $(dir $@)
	@mkdir -p $(patsubst %.jmake,%,$@)
	@echo "# Generated makefile, DO NOT EDIT" > $@
# defines
	@echo -n "JAVA_SOURCES:=" >> $@
	@find $< -follow -name *.java -printf " \\\\\n	%p" >> $@
	@find $< -follow -name *.jj -printf " \\\\\n	\$$(BUILD)/%p" \
		| sed -e "s@\.jj@\.java@g" >> $@
	@find $< -follow -name *.jjt -printf " \\\\\n	\$$(BUILD)/%p" \
		| sed -e "s@\.jjt@\.java@g" >> $@
	@echo "" >> $@
	@echo -n "JDO_SOURCES:=" >> $@
	@find $< -follow -name *.jdo -printf " \\\\\n	%p" >> $@
	@echo "" >> $@
	@echo "JDO_MODIFIED=\$$(if \$$(filter %.jdo,\$$?),\$$^,\$$?)" >> $@
# deps
	@echo "\$$(BUILD)/$<.jstamp: \$$(JAVA_SOURCES) \$$(JDO_SOURCES)" >> $@
# mkdir
	@echo "	@mkdir -p \$$(BUILD)/$<" >> $@
# javac
	@echo "	@echo building $<" >> $@
	@echo -n "	@javac -classpath \$$(CLASSPATH)" >> $@
	@echo -n " -sourcepath \$$(SOURCEPATH) -d \$$(BUILD)/$< " >> $@
	@echo "\$$(filter %.java,\$$(JDO_MODIFIED))" >> $@
# JDO enhancer
	@echo "ifdef JDO_SOURCES" >> $@
	@echo "	@echo enhancing $<" >> $@
	@echo -n "	@\$$(JDO_ENHANCE) -d \$$(BUILD)/$< " >> $@
	@echo -n "-s $<:\$$(BUILD)/$<:\$$(CLASSPATH) " >> $@
	@echo -n "\$$(filter %.class,\$$(patsubst %.java,\$$(BUILD)/%.class,\$$(patsubst \$$(BUILD)%,%,\$$(JDO_MODIFIED)))) " >> $@
	@echo "\$$(filter %.jdo,\$$^)" >> $@
	@echo "endif" >> $@
# timestamp
	@echo "	@touch \$$(BUILD)/$<.jstamp" >> $@
	@echo "" >> $@
	@echo -n "$@:" >> $@
	@find $< -follow -type d -printf " \\\\\n	%p" >> $@
	@echo "" >> $@
	@echo "" >> $@
	@find $< -follow -type d -printf "%p:\n" >> $@

$(BUILD)/%.java: %.jj
	@mkdir -p $(dir $(BUILD)/$<)
	@$(JAVACC) -OUTPUT_DIRECTORY=$(dir $(BUILD)/$<) $<

$(BUILD)/%.java: $(BUILD)/%.jj
	@$(JAVACC) -OUTPUT_DIRECTORY=$(dir $<) $<

$(BUILD)/%.jj: %.jjt
	@mkdir -p $(dir $(BUILD)/$<)
	@$(JJTREE) -OUTPUT_DIRECTORY=$(dir $(BUILD)/$<) $<

$(BUILD)/%.jar: $(BUILD)/%.jstamp
	jar cf $@ -C $(BUILD)/$* .
	find $* \( -name "*.pdl" -or -name "*.xsl" \) -printf "-C $* %P\n" | \
		xargs -r jar uf $@

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
