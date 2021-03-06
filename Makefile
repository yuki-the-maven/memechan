sources=$(wildcard src/**/*.clj)
tests=$(wildcard test/**/*_test.clj)

# TODO: make version changeable


.PHONY: all clean run shell test

all: test build

build: target/memechan-0.1.0-SNAPSHOT.jar

target/memechan-0.1.0-SNAPSHOT.jar: $(sources) project.clj
	lein jar

test: $(sources) $(tests)
	lein test

run:
	lein run

shell:
	lein repl

clean:
	rm -rf target