mocha=./node_modules/.bin/_mocha
istanbul=./node_modules/.bin/istanbul
sources=$(wildcard src/*.js)
tests=$(wildcard test/*_test.js)

.PHONY: all test
all: node_modules test

node_modules: package.json
	npm set progress=false && npm install
	touch $@

test: $(sources) $(tests)
	$(istanbul) cover $(mocha)
