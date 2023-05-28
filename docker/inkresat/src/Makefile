INCLUDES= -I . -I $(HSETSRC) 
OPTIONS=  -unsafe -inline 100 $(INCLUDES) -package extlib
OCAMLC=ocamlfind ocamlopt $(OPTIONS)
OCAMLDEP=ocamldep $(INCLUDES)
OCAMLLEX = ocamllex
OCAMLYACC = ocamlyacc
MINISATSRC = $(shell pwd)/../minisat-2.2.0
HSETSRC = ../hashSet-0.2
MINISATMAKE = make rs || make r || make   

SOURCES = $(wildcard *.ml *.mli *.mly *.mll $(HSETSRC)/*.ml $(HSETSRC)/*.mli)

PROVER = inkresat
EXEC = $(PROVER)

# The object file order has to respect module dependencies. The only way around this would be ocamldsort, which is not included in the default ocaml installation.
OBJS_$(PROVER) = minisat.cmxa syntax.cmx parser.cmx lexer.cmx options.cmx preprocessor.cmx $(HSETSRC)/hashSet.cmx simpleAgenda.cmx agenda.cmx solverAux.cmx prefixes.cmx patterns.cmx agendaBlocked.cmx solver.cmx main.cmx

.PHONY: all clean minisat minisat-clean r

$(PROVER):

all: $(EXEC)

r: MINISATMAKE = make r
r: all

.SECONDEXPANSION:

$(EXEC): $$(OBJS_$$@)
	$(OCAMLC)  -package unix -linkpkg -cclib -lstdc++ -o $@ $(OBJS_$@)
	@echo succesfully created binary '"'$@'"'!

#this deactivates implicit rules -- gnumake is weird...
.SUFFIXES:

.SUFFIXES: .ml .mli .cmo .cmi .cmx .mll .mly

.mli.cmi:
	$(OCAMLC) -c $<

.ml.cmx:
	$(OCAMLC) -c $<

.mll.cmx:
	$(OCAMLLEX) $<
	$(OCAMLC) -c $*.ml

.mly.cmx:
	$(OCAMLYACC) $<
	$(OCAMLC) -c $*.mli
	$(OCAMLC) -c $*.ml

.mly.cmi:
	$(OCAMLYACC) $<
	$(OCAMLC) -c $*.mli

.mll.ml:
	$(OCAMLLEX) $<

.mly.ml:
	$(OCAMLYACC) $<
	
preprocessor.cmo: lexer.ml

minisat_if.o: $(MINISATSRC)/core/Solver.o $(MINISATSRC)/simp/SimpSolver.o minisat_if.cc
	g++ -c -I$(MINISATSRC) -I`ocamlc -where` -D __STDC_LIMIT_MACROS -D __STDC_FORMAT_MACROS minisat_if.cc -o minisat_if.o -fPIC

minisat.cmxa: $(MINISATSRC)/core/Solver.o $(MINISATSRC)/simp/SimpSolver.o minisat_if.o  minisat.cmi minisat.cmx
	ocamlmklib -o minisat $(MINISATSRC)/core/Solver.o $(MINISATSRC)/simp/SimpSolver.o minisat_if.o
	ocamlmklib -o minisat minisat.cmx
	
minisat:
	export MROOT=$(MINISATSRC); cd $(MINISATSRC)/core; $(MINISATMAKE);
	export MROOT=$(MINISATSRC); cd $(MINISATSRC)/simp; $(MINISATMAKE);
	
$(MINISATSRC)%.o: $(MINISATSRC)%.or | minisat
	-cp -f $< $@
	
$(MINISATSRC)%.or: | minisat
	@ :

# prevents some strange intermediate file removal bug	
dummy: $(MINISATSRC)/core/Solver.or $(MINISATSRC)/simp/SimpSolver.or
	
minisat-clean:
	export MROOT=$(MINISATSRC); cd $(MINISATSRC)/core; make clean
	export MROOT=$(MINISATSRC); cd $(MINISATSRC)/simp; make clean

# Clean up
MLYSOURCES= $(filter %.mly %.mll, $(SOURCES))
MLSOURCES= $(patsubst %.mly, %.ml, $(SOURCES:.mll=.ml))
GEN_ML_FILES = $(patsubst %.mly, %.ml, $(MLYSOURCES:.mll=.ml))
GEN_FILES = $(GEN_ML_FILES) $(GEN_ML_FILES:.ml=.mli)

clean: minisat-clean
	rm -f $(EXEC)
	rm -f $(foreach list,$(EXEC),$(OBJS_$(list))) $(GEN_FILES)
	rm -f *.cm[iox] *.[ao] *.so $(HSETSRC)/*.cm[iox]  hashSet-0.2/*.[ao] hashSet-0.2/*.so
	rm -f .depend
	echo cleaned
	
# cancel implicit rule
%.o: %.cc

# Dependency calculations
.depend: $(MLSOURCES)
	@touch .depend
	$(OCAMLDEP) $(INCLUDES) $(MLSOURCES) > .depend

-include .depend
