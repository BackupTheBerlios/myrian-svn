BUILD=build

empty:=
space:=$(empty) $(empty)

JAVACC=sh javacc-3.2/bin/javacc

SOURCES:=src test/src cap/src cap/test/src

SOURCEPATH:=$(subst $(space),:,$(SOURCES:%=$(BUILD)/%))

CLASSPATH:=$(CLASSPATH):$(subst $(space),:,$(wildcard lib/*.jar))
CLASSPATH:=$(CLASSPATH):$(subst $(space),:,$(wildcard lib/*.zip))
CLASSPATH:=$(CLASSPATH):$(SOURCEPATH)

JSTAMPS:=$(SOURCES:%=$(BUILD)/%.jstamp)

PKGS:=com.arsdigita.db com.arsdigita.persistence
PKGS:=$(PKGS) com.arsdigita.persistence.metadata com.arsdigita.persistence.pdl
PKGS:=$(PKGS) com.arsdigita.util.cmd com.redhat.persistence
PKGS:=$(PKGS) com.redhat.persistence.common com.redhat.persistence.engine
PKGS:=$(PKGS) com.redhat.persistence.engine.rdbms com.redhat.persistence.metadata
PKGS:=$(PKGS) com.redhat.persistence.oql com.redhat.persistence.pdl
PKGS:=$(PKGS) com.redhat.persistence.pdl.adapters com.redhat.persistence.pdl.nodes
PKGS:=$(PKGS) com.redhat.persistence.profiler com.redhat.persistence.profiler.rdbms
JD:=javadoc
JD_SOURCEPATH:=-sourcepath src:cap/src
JD_INCLUDES:=-version -author
JD_FLAGS:=$(JD_SOURCEPATH) $(JD_INCLUDES)
DOC_DEST:=$(BUILD)/doc

all: $(JSTAMPS)

-include $(SOURCES:%=$(BUILD)/%.jmake)

clean:
	rm -rf $(BUILD)

jar: all
	jar cf $(BUILD)/crp.jar -C $(BUILD)/src .
	cd src; find . -name "*.pdl" -or -name "*.xsl" | \
		xargs jar uf ../$(BUILD)/crp.jar

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

doc:
	@mkdir -p $(DOC_DEST)
	$(JD) $(JD_FLAGS) $(PKGS) -d $(DOC_DEST)
