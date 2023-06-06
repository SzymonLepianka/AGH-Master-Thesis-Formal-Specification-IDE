/*  File: minisat_if.cc
 *  Author: Mark Kaminski <kaminski@ps.uni-saarland.de>
 *
 *  Copyright: Mark Kaminski, 2012
 *
 *  This file is part of InKreSAT.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files (the
 *  "Software"), to deal in the Software without restriction, including
 *  without limitation the rights to use, copy, modify, merge, publish,
 *  distribute, sublicense, and/or sell copies of the Software, and to
 *  permit persons to whom the Software is furnished to do so, subject to
 *  the following conditions:
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *  OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

#include <ctime>
#include <cstring>
#include <stdint.h>
#include <errno.h>

#include <signal.h>
#include <zlib.h>

#include <simp/SimpSolver.h>

extern "C" {
#include <caml/mlvalues.h>
#include <caml/memory.h>
#include <caml/alloc.h>
#include <caml/fail.h>
#include <caml/callback.h>
}

using namespace Minisat;

SimpSolver*    solver;
//vec<Lit>       lits;

bool redirect = true;

//void printState () {
// printf("minisat state:\nnClauses: %d\nnVars:%d\n",(*solver).nClauses(),(*solver).nVars());
//}

extern "C" value init(value _l) {
  //  CAMLparam1(_l);
  int v = Int_val(_l);
  if (solver != NULL) { delete solver; }
  solver = new SimpSolver;
  (*solver).verbosity = v;
  if (redirect && (v < 1)) { freopen("/dev/null", "w", stderr); /*redirect = false;*/ } // redirect stderr to avoid reportf info being printed
  // CAMLreturn(Val_unit);
  return Val_unit;
}

extern "C" value addVar(value unit) {
  int ret = (*solver).newVar();
  return Val_int(ret);
}

extern "C" value addUnitClause(value _l, value _lp) {
  //  printf("calling addUnitClause\n");
  //  printState();
  int v = Int_val(_l);
  bool vp = !(bool)Bool_val(_lp);
  bool ret = (*solver).addClause(mkLit(v,vp));
  //  printf("addUnitClause: %d\n",ret);
  //  printState();
  return Val_bool(ret);
}

extern "C" value addBinaryClause(value _l, value _lp, value _m, value _mp) {
  //  printf("calling addBinaryClause\n");
  //  printState();
  int v = Int_val(_l);
  bool vp = !(bool)Bool_val(_lp);
  int w = Int_val(_m);
  bool wp = !(bool)Bool_val(_mp);
  bool ret = (*solver).addClause(mkLit(v,vp),mkLit(w,wp));
  //  printf("addBinaryClause: %d\n",ret);
  //  printState();
  return Val_bool(ret);
}

extern "C" value addTernaryClause_native(value _l, value _lp, value _m, value _mp, value _n, value _np) {
  //  printf("calling addTernaryClause\n");
  //  printState();
  int v = Int_val(_l);
  bool vp = !(bool)Bool_val(_lp);
  int w = Int_val(_m);
  bool wp = !(bool)Bool_val(_mp);
  int x = Int_val(_n);
  bool xp = !(bool)Bool_val(_np);
  bool ret = (*solver).addClause(mkLit(v,vp),mkLit(w,wp),mkLit(x,xp));
  //  printf("addTernaryClause: %d\n",ret);
  //  printState();
  return Val_bool(ret);
}

extern "C" value addTernaryClause_bytecode(value* argv, int argn) {
  return addTernaryClause_native(argv[0], argv[1], argv[2], argv[3], argv[4], argv[5]);
}

extern "C" value solve (value unit) {
  //  printf("calling solve\n");
  //  printState();
  //  if (!(*solver).simplify()){
  //    return Val_bool(0);
  //  }
  bool ret = (*solver).solve();
  //  printf("solve: %d\nnClauses: %d\nnVars:%d\n",ret,(*solver).nClauses(),(*solver).nVars());
  //  printState();
  return Val_bool(ret);
}

extern "C" value isVarTrue (value _l) {
  int v = Int_val(_l);
  return Val_bool(!(bool)toInt((*solver).modelValue(v)));
}

extern "C" value freezeVar (value _l) {
  (*solver).setFrozen(Int_val(_l),true);
  return Val_unit;
}

extern "C" value thawVar (value _l) {
  (*solver).setFrozen(Int_val(_l),false);
  return Val_unit;
}

//// search, with 1 assumed lit
//extern "C" value minisat_search1 (value _l) {
//  CAMLparam1(_l);
//  int parsed_lit = Int_val(_l);
//  int var = abs(parsed_lit)-1;
//  bool ret = true;
//  if (var < (*solver).nVars()) {
//    vec<Lit> a;
//    a.push( (parsed_lit > 0) ? Lit(var) : ~Lit(var));
//    ret = (*solver).solve(a,true,true);
//  }
//  CAMLreturn(Val_bool(ret));
//}

extern "C" value addUnitLClause(value _l) {
  return Val_bool((*solver).addClause(toLit(Int_val(_l))));
}

extern "C" value addBinaryLClause(value _l, value _m) {
  return Val_bool((*solver).addClause(toLit(Int_val(_l)),
				      toLit(Int_val(_m))));
}

extern "C" value addTernaryLClause(value _l, value _m, value _n) {
  return Val_bool((*solver).addClause(toLit(Int_val(_l)),
				      toLit(Int_val(_m)),
				      toLit(Int_val(_n))));
}

extern "C" value addLClause(value _l) {
  CAMLparam1(_l);
  CAMLlocal1(hd);
  vec<Lit> ls;
  while (_l != Val_emptylist) {
    hd = Field(_l,0);
    ls.push(toLit(Int_val(hd)));
    _l = Field(_l,1);
  }
  bool ret = (*solver).addClause_(ls);
  CAMLreturn(Val_bool(ret));
}

extern "C" value isLitTrue (value _l) {
  int v = Int_val(_l);
  return Val_bool(!(bool)toInt((*solver).modelValue(toLit(v))));
}

extern "C" value extend (value unit) {
  int i;
  vec<Lit> ls;
  for(i = 0; i < (*solver).model.size(); i++) {
    if((*solver).model[i] == l_True) ls.push(mkLit(i,false));
    else if((*solver).model[i] == l_False) ls.push(mkLit(i,true));
  }
  return Val_bool((*solver).solve(ls));
}

extern "C" value solveAssuming (value _l) {
  CAMLparam1(_l);
  CAMLlocal1(hd);
  vec<Lit> ls;
  while (_l != Val_emptylist) {
    hd = Field(_l,0);
    ls.push(toLit(Int_val(hd)));
    _l = Field(_l,1);
  }
  bool ret = (*solver).solve(ls);
  CAMLreturn(Val_bool(ret));
}
