define MODULE_HELP
@echo "  make [all]"
@echo "    Perform a full build of all modules."
@echo ""
@echo "  make clean"
@echo "    Remove all files generated during a full build."
@echo ""
@echo "  make jars"
@echo "    Build jars for all modules."
@echo ""
@echo "  make docs"
@echo "    Build javadoc for all modules."
@echo ""
@echo "  make <module>"
@echo "    Build just one module."
@echo ""
@echo "  make clean-<module>"
@echo "    Remove all files generated during a build of <module>."
@echo ""
@echo "  make <module>.jar"
@echo "    Build the jar for <module>. The result will be placed in"
@echo "    build/<module>.jar."
@echo ""
@echo "  make <module>-docs"
@echo "    Build javadoc for <module>. The result will be placed in"
@echo "    build/<module>/doc"
@echo ""
@echo "Available modules: $(MODULES)"
endef

ifndef BUILD
BUILD:=build
endif

ifndef JAVADOC
JAVADOC:=javadoc
endif

ifndef JAVADOC_OPTS
JAVADOC_OPTS:=-version -author -quiet
endif

.PHONY: all

all: $(MODULES)

jars: $(MODULES:%=%.jar)

clean:
	rm -rf $(BUILD)

docs: $(MODULES:%=%-docs)

empty:=
space:=$(empty) $(empty)

list2path=$(subst $(space),:,$(1))
path2list=$(subst :,$(space),$(1))

MODULE_CLASSPATH_LIST:=$(MODULES:%=$(BUILD)/%/classes) $(MODULES:%=%/src)
MODULE_CLASSPATH:=$(call list2path,$(MODULE_CLASSPATH_LIST))

$(BUILD)/%.java: %.jj
	@mkdir -p $(@D)
	@javacc -OUTPUT_DIRECTORY=$(@D) $<

expand-deps=$(sort $(foreach mod,$(1),\
		$($(mod).deps) $(strip $(call expand-deps,$($(mod).deps)))))

FILE_EXISTS=$(shell if test -e $(lib); then echo $(lib); fi)

define MODULE_TEMPLATE
@M_JAR:=$(BUILD)/@M.jar
@M_TIMESTAMP:=$(BUILD)/@M/timestamp
@M_DEPS:=$(@M.deps:%=$(BUILD)/%/timestamp)
@M_CLASSES:=$(BUILD)/@M/classes
@M_FILES:=$(shell find @M/src -path "*/.svn" -prune -or -type f -print)
@M_SOURCES:=$(filter %.java %.jj,$(@M_FILES))
@M_SOURCES:=$(@M_SOURCES:%.jj=$(BUILD)/%.java)
@M_RESOURCES:=$(filter-out %.java %.jj,$(@M_FILES))
@M_JDO_FILES:=$(filter %.jdo,$(@M_RESOURCES))
@M_EXPANDED_DEPS:=$(call expand-deps,@M)

@M_DEP_CP:=$(call list2path,$(patsubst \
		%,$(BUILD)/%/classes,$(@M_EXPANDED_DEPS)))
@M_CLASSPATH:=$(CLASSPATH):$(@M_DEP_CP)
@M_CLASSPATH_LIST:=$(call path2list,$(@M_CLASSPATH))
@M_CLASSPATH:=$(@M_CLASSPATH):$(@M_CLASSES)

@M_CLEAN_CP_LIST=$(foreach lib,$(@M_CLASSPATH_LIST),$(FILE_EXISTS))
@M_CLEAN_CLASSPATH=$(call list2path,$(@M_CLEAN_CP_LIST)):$(@M_CLASSES)

@M_enhance:=java -classpath $(@M_CLASSPATH) com.sun.jdori.enhancer.Main

.PHONY: @M @M.jar

@M: $(@M_TIMESTAMP)

@M.jar: $(@M_JAR)

$(@M_JAR): $(@M_TIMESTAMP) $(@M_RESOURCES)
	@echo building $(@M_JAR)
	@jar cf $(@M_JAR) -C $(@M_CLASSES) . \
	    $(@M_RESOURCES:@M/src/%=-C @M/src %)

$(@M_TIMESTAMP): JAVA_MOD=$(filter %.java,$?)
$(@M_TIMESTAMP): JDO_MOD=$(filter %.jdo,$?)
$(@M_TIMESTAMP): REBUILD=$(if $(JDO_MOD),$(@M_SOURCES),$(JAVA_MOD))
$(@M_TIMESTAMP): CLASSES=$(filter %.class,$(REBUILD:@M/src/%.java=$(@M_CLASSES)/%.class))
$(@M_TIMESTAMP): $(@M_SOURCES) $(@M_JDO_FILES) $(@M_DEPS)
	@mkdir -p $(@M_CLASSES)
	$(if $(REBUILD),@echo compiling $(words $(REBUILD)) \
		files from @M/src to $(@M_CLASSES))
	$(if $(REBUILD),@javac -classpath $(@M_CLASSPATH) \
		-sourcepath $(BUILD)/@M/src \
		-d $(@M_CLASSES) $(REBUILD))
	$(if $(REBUILD),@echo enhancing $(words $(CLASSES)) \
		files in $(@M_CLASSES))
	$(if $(REBUILD),@$(@M_enhance) -d $(@M_CLASSES) \
		-s @M/src:$(@M_CLEAN_CLASSPATH) \
		$(CLASSES))
	@touch $(@M_TIMESTAMP)

@M_JAVADOC_DEST:=$(BUILD)/@M/doc
@M_JAVADOC_STAMP:=$(BUILD)/@M/doc/timestamp
@M_JAVADOC_SOURCES:=$(filter %.java,$(@M_FILES))

.PHONY: @M-docs

@M-docs: $(@M_JAVADOC_STAMP)

$(@M_JAVADOC_STAMP): $(@M_JAVADOC_SOURCES) $(@M_TIMESTAMP) \
		$(@M.deps:%=$(BUILD)/%/doc/timestamp)
	@mkdir -p $(@M_JAVADOC_DEST)
	@$(JAVADOC) $(JAVADOC_OPTS) -classpath $(@M_CLASSPATH) \
		$(foreach dep,$(@M_EXPANDED_DEPS), \
		    -link ../../$(dep)/doc) \
		-d $(@M_JAVADOC_DEST) \
		$(@M_JAVADOC_SOURCES)
	@touch $(@M_JAVADOC_STAMP)

clean-@M:
	rm -f $(@M_JAR)
	rm -rf $(BUILD)/@M
endef

APPLY_MODULE=$(eval $(subst @M,$(module),$(value MODULE_TEMPLATE)))
$(foreach module,$(MODULES),$(APPLY_MODULE))
