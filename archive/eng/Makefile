ifndef BUILD
BUILD=build
endif

empty:=
space:=$(empty) $(empty)

ifndef JAVACC
JAVACC=sh javacc-3.2/bin/javacc
endif

SOURCES:=src test/src cap/src cap/test/src

SOURCEPATH:=$(subst $(space),:,$(SOURCES:%=$(BUILD)/%))

CLASSPATH:=$(CLASSPATH):$(subst $(space),:,$(wildcard lib/*.jar))
CLASSPATH:=$(CLASSPATH):$(subst $(space),:,$(wildcard lib/*.zip))
CLASSPATH:=$(CLASSPATH):$(SOURCEPATH)

JSTAMPS:=$(SOURCES:%=$(BUILD)/%.jstamp)
JARS:=$(SOURCES:%=$(BUILD)/%.jar)

JD:=javadoc
JD_FLAGS:=-version -author -quiet
DOC_DEST:=$(BUILD)/doc

all: $(JSTAMPS)

jars: $(JARS)

-include $(SOURCES:%=$(BUILD)/%.jmake)

clean:
	rm -rf $(BUILD)

docs:
	@mkdir -p $(DOC_DEST)
	$(JD) $(JD_FLAGS) -d $(DOC_DEST) `find $(SOURCES) -name "*.java"`

tags:
	rm -f TAGS
	find $(SOURCES) -name "*.java" | xargs etags -a \
		--language=none \
		--regex='/^\([ \t]\|public\|private\|static\|protected\|final\|abstract\)*[ \t\n]*\(class\|interface\)+[ \t\n]+\([a-zA-Z0-9_]+\)/\3/' \
		--output=TAGS

$(BUILD)/%.jmake: %
	@echo generating $@
	@mkdir -p $(dir $@)
	@echo -n "\$$(BUILD)/$<.jstamp" > $@
	@echo -n ":" >> $@
	@find $< -name *.java -printf " \\\\\n	%p" >> $@
	@find $< -name *.jj -printf " \\\\\n	\$$(BUILD)/%p" \
		| sed -e "s@\.jj@\.java@g" >> $@
	@echo "" >> $@
	@echo "	@mkdir -p \$$(BUILD)/$<" >> $@
	@echo -n "	javac -classpath \$$(CLASSPATH)" >> $@
	@echo " -sourcepath \$$(SOURCEPATH) -d \$$(BUILD)/$< \$$?" >> $@
	@echo "	@touch \$$(BUILD)/$<.jstamp" >> $@
	@echo "" >> $@
	@echo -n "$@:" >> $@
	@find $< -type d -printf " \\\\\n	%p" >> $@
	@echo "" >> $@
	@echo "" >> $@
	@find $< -type d -printf "%p:\n" >> $@

$(BUILD)/%.java: %.jj
	@mkdir -p $(dir $(BUILD)/$<)
	@$(JAVACC) -OUTPUT_DIRECTORY=$(dir $(BUILD)/$<) $<

$(BUILD)/%.jar: $(BUILD)/%.jstamp
	jar cf $@ -C $(BUILD)/$* .
	find $* \( -name "*.pdl" -or -name "*.xsl" \) -printf "-C $* %P\n" | \
		xargs -r jar uf $@
