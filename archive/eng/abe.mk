ifndef BUILD
BUILD:=build
endif

ifndef DRIVERS
DRIVERS:=org.postgresql.Driver
endif

ifndef JAVACC
JAVACC=sh javacc-3.2/bin/javacc
endif

ifndef JJTREE
JJTREE:=sh javacc-3.2/bin/jjtree
endif

ifndef JDO_ENHANCE
JDO_ENHANCE=java -classpath lib/jdo.jar:jdori/jdori.jar:jdori/jdori-enhancer.jar com.sun.jdori.enhancer.Main
endif

ifndef JAVAC_OPTS
JAVAC_OPTS:=-g
endif

ifndef CONF
CONF:=conf
endif

empty:=
space:=$(empty) $(empty)

SOURCEPATH:=$(subst $(space),:,$(SOURCES:%=$(BUILD)/%))
LIB_JARS:=$(foreach dir,$(LIBS),$(wildcard $(dir)/*.jar))
CLASSPATH:=$(CLASSPATH):$(subst $(space),:,$(LIB_JARS))
###CLASSPATH:=$(CLASSPATH):jdori/jdori.jar:jdori/jdori-enhancer.jar
CLASSPATH:=$(CLASSPATH):$(subst $(space),:,$(wildcard lib/*.zip))
CLASSPATH:=$(CLASSPATH):$(SOURCEPATH):$(CONF)

JSTAMPS:=$(SOURCES:%=$(BUILD)/%.jstamp)
JARS:=$(SOURCES:%=$(BUILD)/%.jar)

ifndef JAVADOC
JAVADOC:=javadoc
endif

ifndef JAVADOC_OPTS
JAVADOC_OPTS:=-version -author -quiet
endif

ifndef JAVADOC_DEST
JAVADOC_DEST:=$(BUILD)/doc
endif

.PHONY: all jars docs tags clean

all: $(JSTAMPS)

jars: $(JARS)

docs:
	@mkdir -p $(JAVADOC_DEST)
	$(JAVADOC) $(JAVADOC_OPTS) -d $(JAVADOC_DEST) \
		`find $(SOURCES) -name "*.java"`

tags:
	bin/jtags TAGS $(SOURCES)

clean:
	rm -rf $(BUILD)

-include $(SOURCES:%=$(BUILD)/%.jmake)

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
	@echo -n "	@javac \$$(JAVAC_OPTS) -classpath \$$(CLASSPATH)" >> $@
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
