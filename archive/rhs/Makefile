BUILD=build

empty:=
space:=$(empty) $(empty)

CLASSPATH:=$(CLASSPATH):$(BUILD)/classes
CLASSPATH:=$(CLASSPATH):$(subst $(space),:,$(wildcard lib/*.jar))
CLASSPATH:=$(CLASSPATH):$(subst $(space),:,$(wildcard lib/*.zip))

#JAVAC=gcj-ssa -L~/lib -o build/dumper --main=Main --classpath $(CLASSPATH):$(BUILD)/src ~/lib/log4j.jar lib/xerces.jar
# -C -d $(BUILD)/classes
JAVAC=javac -classpath $(CLASSPATH) -sourcepath $(BUILD)/src -d $(BUILD)/classes
JAVACC=java -cp javacc/JavaCC.zip COM.sun.labs.javacc.Main

FILES:=$(shell find src -name *.java -or -name *.jj)
GRAMMARS:=$(filter %.jj,$(FILES))
PARSERS:=$(GRAMMARS:src/%.jj=$(BUILD)/src/%.java)
SOURCES:=$(filter %.java,$(FILES))

TIMESTAMP:=$(BUILD)/classes/.timestamp

all: $(TIMESTAMP)

tags:
	rm -f TAGS
	find src -name "*.java" | xargs etags -a \
		--language=none \
		--regex='/^\([ \t]\|public\|private\|static\|protected\|final\|abstract\)*[ \t\n]*\(class\|interface\)+[ \t\n]+\([a-zA-Z0-9_]+\)/\3/' \
		--output=TAGS

clean:
	rm -rf $(BUILD)

$(TIMESTAMP): $(SOURCES) $(PARSERS)
	@mkdir -p $(BUILD)/classes
#	$(JAVAC) $? `find $(BUILD)/src -name "*.java"`
	$(JAVAC) $?
	@touch $(BUILD)/classes/.timestamp

$(BUILD)/src/%.java: src/%.jj
	@mkdir -p $(dir $(BUILD)/$<)
	@$(JAVACC) -OUTPUT_DIRECTORY=$(dir $(BUILD)/$<) $<

#-include build/foo.jdep

#build/%.jdep: %/src
#	echo -n $@ > $@
#	echo -n ": " >> $@
#	find $</ -name "*.java" -printf " %p" >> $@

#%.fake: %.jdep
#	echo asdf
