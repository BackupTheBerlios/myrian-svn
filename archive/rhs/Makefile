BUILD=build

empty:=
space:=$(empty) $(empty)

JAVACC=java -cp javacc/JavaCC.zip COM.sun.labs.javacc.Main

SOURCES:=src test/src cap/src cap/test/src

SOURCEPATH:=$(subst $(space),:,$(SOURCES:%=$(BUILD)/%))

CLASSPATH:=$(CLASSPATH):$(subst $(space),:,$(wildcard lib/*.jar))
CLASSPATH:=$(CLASSPATH):$(subst $(space),:,$(wildcard lib/*.zip))
CLASSPATH:=$(CLASSPATH):$(SOURCEPATH)

JSTAMPS:=$(SOURCES:%=$(BUILD)/%.jstamp)

all: $(JSTAMPS)

-include $(SOURCES:%=$(BUILD)/%.jmake)

clean:
	rm -rf $(BUILD)

jar: all
	jar cf $(BUILD)/crp.jar -C $(BUILD)/src .
	jar uf $(BUILD)/crp.jar -C src .

tags:
	rm -f TAGS
	find $(SOURCES) -name "*.java" | xargs etags -a \
		--language=none \
		--regex='/^\([ \t]\|public\|private\|static\|protected\|final\|abstract\)*[ \t\n]*\(class\|interface\)+[ \t\n]+\([a-zA-Z0-9_]+\)/\3/' \
		--output=TAGS

$(BUILD)/%.java: %.jj
	@mkdir -p $(dir $(BUILD)/$<)
	@$(JAVACC) -OUTPUT_DIRECTORY=$(dir $(BUILD)/$<) $<

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
