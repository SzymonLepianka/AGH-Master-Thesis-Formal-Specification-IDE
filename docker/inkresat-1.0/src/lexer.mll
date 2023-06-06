(*****************************************************************************
 *  Author:
 *    Mark Kaminski <kaminski@ps.uni-saarland.de>
 *
 *  Copyright:
 *    Mark Kaminski, 2012
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
 *
 *****************************************************************************)

{
  open Parser
  exception LexingError of string
}

(*let number = ['0'-'9']+*)
let alphanum = ['A'-'Z' 'a'-'z' '0'-'9']+
let aalphanum = ['A'-'Z' 'a'-'z'] ['A'-'Z' 'a'-'z' '0'-'9']*
(*let space = [' ' '\t' '\n']+*)

rule lexFormula = parse
  | [' ' '\t' '\n']         { lexFormula lexbuf }
  | "->"                    { IMPLIES }
  | '~' | '-'               { NEG }
  | '('                     { LPAR }
  | ')'                     { RPAR }
  | "<>"                    { DIA "_" }
  | "[]"                    { BOX "_" }
  | "<->"                   { EQUIV }
  | '<' (alphanum as s) '>' { DIA s }
  | '[' (alphanum as s) ']' { BOX s }
  | '&'                     { AND }
  | '|'                     { OR }
  | '0'                     { FALSE }
  | '1'                     { TRUE }
  | "false" | "False" | "FALSE"    { FALSE }
  | "true" | "True" | "TRUE"       { TRUE }
  | "begin" | "BEGIN"       { lexFormula lexbuf }
  | "end" | "END"           { lexFormula lexbuf }
  | aalphanum as s          { ID s }
  | _ as c                  { raise (LexingError ("Unrecognized character: " ^ Char.escaped c))}
  | eof                     { EOF }
