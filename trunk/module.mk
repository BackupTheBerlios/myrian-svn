define MODULE_HELP
@echo "  make [all]"
@echo "    This goal does a full build that respects dependencies. This means "
@echo "    that if a file is modified in module A, and module B depends on "
@echo "    module A, then most likely module B will be completely rebuilt."
@echo "    If you wish to avoid this behavior during iterative development,"
@echo "    then you may use make A to build just module A if you know that "
@echo "    there should be no interface changes that will effect module B."
@echo ""
@echo "  make clean"
@echo "    Remove all files generated during a full build."
@echo ""
@echo "  make jars"
@echo "    Build each module's jar."
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
@echo "Available modules: $(MODULES)"
endef

.PHONY: all

all: $(MODULES)

JARS:=$(MODULES:%=build/%.jar)

jars: $(JARS)

clean:
	rm -rf build

empty:=
space:=$(empty) $(empty)

list2path=$(subst $(space),:,$(1))
path2list=$(subst :,$(space),$(1))

MODULE_CLASSPATH_LIST:=$(MODULES:%=build/%/classes) $(MODULES:%=%/src)
MODULE_CLASSPATH:=$(call list2path,$(MODULE_CLASSPATH_LIST))

build/%.java: %.jj
	@mkdir -p $(@D)
	@javacc -OUTPUT_DIRECTORY=$(@D) $<

expand-deps=$(sort $(foreach mod,$(1),\
		$($(mod).deps) $(strip $(call expand-deps,$($(mod).deps)))))

FILE_EXISTS=$(shell if test -e $(lib); then echo $(lib); fi)

define MODULE_TEMPLATE
@M_JAR:=build/@M.jar
@M_TIMESTAMP:=build/@M/timestamp
@M_DEPS:=$(@M.deps:%=build/%/timestamp)
@M_CLASSES:=build/@M/classes
@M_FILES:=$(shell find @M/src -path "*/.svn" -prune -or -type f -print)
@M_SOURCES:=$(filter %.java %.jj,$(@M_FILES))
@M_SOURCES:=$(@M_SOURCES:%.jj=build/%.java)
@M_RESOURCES:=$(filter-out %.java %.jj,$(@M_FILES))
@M_JDO_FILES:=$(filter %.jdo,$(@M_RESOURCES))

@M_DEP_CP:=$(call list2path,$(patsubst \
		%,build/%/classes,$(call expand-deps,@M)))
@M_CLASSPATH:=$(CLASSPATH):$(@M_DEP_CP)
@M_CLASSPATH_LIST:=$(call path2list,$(@M_CLASSPATH))
@M_CLASSPATH:=$(@M_CLASSPATH):$(@M_CLASSES)

@M_CLEAN_CP_LIST=$(foreach lib,$(@M_CLASSPATH_LIST),$(FILE_EXISTS))
@M_CLEAN_CLASSPATH=$(call list2path,$(@M_CLEAN_CP_LIST)):$(@M_CLASSES)

@M_enhance:=java -classpath $(@M_CLASSPATH) com.sun.jdori.enhancer.Main

.PHONY: @M

@M: $(@M_TIMESTAMP)

$(@M_JAR): $(@M_TIMESTAMP) $(@M_RESOURCES)
	@echo building $(@M_JAR)
	@jar cf $(@M_JAR) -C $(@M_CLASSES) . \
	    $(@M_RESOURCES:@M/src/%=-C @M/src %)

clean-@M:
	rm -f $(@M_JAR)
	rm -rf build/@M

$(@M_TIMESTAMP): JAVA_MOD=$(filter %.java,$?)
$(@M_TIMESTAMP): JDO_MOD=$(filter %.jdo,$?)
$(@M_TIMESTAMP): DEP_MOD=$(filter %/timestamp,$?)
$(@M_TIMESTAMP): REBUILD=$(if $(JDO_MOD)$(DEP_MOD),$(@M_SOURCES),$(JAVA_MOD))
$(@M_TIMESTAMP): CLASSES=$(filter %.class,$(REBUILD:@M/src/%.java=$(@M_CLASSES)/%.class))
$(@M_TIMESTAMP): $(@M_SOURCES) $(@M_JDO_FILES) $(@M_DEPS)
	@mkdir -p $(@M_CLASSES)
	@echo compiling $(words $(REBUILD)) files from @M/src to $(@M_CLASSES)
	@javac -classpath $(@M_CLASSPATH) -sourcepath build/@M/src \
	    -d $(@M_CLASSES) $(REBUILD)
	@echo enhancing $(words $(CLASSES)) files in $(@M_CLASSES)
	@$(@M_enhance) -d $(@M_CLASSES) -s @M/src:$(@M_CLEAN_CLASSPATH) \
	    $(CLASSES)
	@touch $(@M_TIMESTAMP)
endef

APPLY_MODULE=$(eval $(subst @M,$(module),$(value MODULE_TEMPLATE)))
$(foreach module,$(MODULES),$(APPLY_MODULE))
