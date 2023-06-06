MINISATSRC =  $(shell pwd)/minisat-2.2.0
HSETSRC = $(shell pwd)/hashSet-0.2

MAKEARG = "MINISATSRC=$(MINISATSRC)" "HSETSRC=$(HSETSRC)"

PROVER = inkresat
EXECS = $(PROVER)

all: $(PROVER)

r: MAKEARG += "MINISATMAKE=make r"
r: all

.SUFFIXES:

$(EXECS): FORCE
	cd src && make $(MAKEARG) $@ && cp -f $@ ..
	

clean:
	cd src && make $(MAKEARG) clean
	rm -f $(EXECS)
	
.PHONY: all clean FORCE r

FORCE:
	:

