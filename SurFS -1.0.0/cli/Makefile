SOURCEDIR=.
SOURCES := $(shell find $(SOURCEDIR) -name '*.go')

VERSION=1.0.0
BUILD_TIME=$(shell date +%FT%T%z)

BINDATA=cmd/surfs/assets/config.go
BINARY=bin/surfs
GIT_SHA=$(shell git rev-parse --short HEAD)

PACKAGES=.

LDFLAGS="-X ./version.GitRevision=$(GIT_SHA) \
	-X ./version.Version=$(VERSION) \
	-X ./version.BuildTime=$(BUILD_TIME)"

.PHONY: all
all: $(BINARY)

$(BINARY): $(SOURCES)
	go build -ldflags $(LDFLAGS) -o bin/surfs ./cmd/surfs

.PHONY: install
install:
	go install ${LDFLAGS} ./...

.PHONY: clean
clean:
	if [ -f ${BINARY} ] ; then rm ${BINARY} ; fi

.PHONY: test
test:
	go test -v $(PACKAGES)

.PHONY: silent-test
silent-test:
	go test $(PACKAGES)
