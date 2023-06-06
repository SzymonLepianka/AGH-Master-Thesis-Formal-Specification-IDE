/*  File: parser.mly
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

%{
  open Syntax
%}

%token NEG AND OR LPAR RPAR EOF TRUE FALSE EQUIV IMPLIES
%token <string> ID
%token <string> DIA BOX

%left EQUIV
%right IMPLIES
%left OR
%left AND
%nonassoc NEG DIA BOX

%start file
%type <Syntax.xformula> formula file

%%
file:    formula EOF               { $1 }
formula: ID                        { try XVar(Hashtbl.find var_of_string $1) with
					 Not_found -> let n = !num_vars in
					   Hashtbl.add var_of_string $1 n; num_vars := n+1; XVar n }
       | TRUE                      { XTrue }
       | FALSE                     { XFalse }
       | LPAR formula RPAR         { $2 }
       | formula EQUIV formula     { XOr [XAnd [$1; $3]; XAnd [XNeg $1; XNeg $3]] }
       | formula IMPLIES formula   { XOr [XNeg $1; $3] }
       | formula OR formula        { match $1, $3 with
					 XOr fs, XOr fs' -> XOr (List.rev_append fs fs')
				       | XOr fs, f' -> XOr (f' :: fs)
				       | f, XOr fs' -> XOr (f :: fs')
				       | f, f' -> XOr [f;f'] }
       | formula AND formula       { match $1, $3 with
					 XAnd fs, XAnd fs' -> XAnd (List.rev_append fs fs')
				       | XAnd fs, f' -> XAnd (f' :: fs)
				       | f, XAnd fs' -> XAnd (f :: fs')
				       | f, f' -> XAnd [f;f'] }
       | NEG formula               { XNeg $2 }
       | DIA formula               { try XDia(Hashtbl.find rel_of_string $1, $2) with
					 Not_found -> let n = !num_rels in
					   Hashtbl.add rel_of_string $1 n; num_rels := n+1; XDia (n, $2) }
       | BOX formula               { try XBox(Hashtbl.find rel_of_string $1, $2) with
					 Not_found -> let n = !num_rels in
					   Hashtbl.add rel_of_string $1 n; num_rels := n+1; XBox (n, $2) }
;
%%
