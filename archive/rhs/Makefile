BUILD=build
JAVACC=java -cp javacc/JavaCC.zip COM.sun.labs.javacc.Main

empty:=
space:=$(empty) $(empty)

CLASSPATH:=$(CLASSPATH):$(BUILD)/classes
CLASSPATH:=$(CLASSPATH):$(subst $(space),:,$(wildcard lib/*.jar))
CLASSPATH:=$(CLASSPATH):$(subst $(space),:,$(wildcard lib/*.zip))

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
	javac -sourcepath $(BUILD)/src -d $(BUILD)/classes $?
	@touch $(BUILD)/classes/.timestamp

$(BUILD)/src/%.java: src/%.jj
	@mkdir -p $(dir $(BUILD)/$<)
	@$(JAVACC) -OUTPUT_DIRECTORY=$(dir $(BUILD)/$<) $<
