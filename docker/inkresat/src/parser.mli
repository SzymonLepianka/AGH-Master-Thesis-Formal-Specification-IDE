type token =
  | NEG
  | AND
  | OR
  | LPAR
  | RPAR
  | EOF
  | TRUE
  | FALSE
  | EQUIV
  | IMPLIES
  | ID of (string)
  | DIA of (string)
  | BOX of (string)

val file :
  (Lexing.lexbuf  -> token) -> Lexing.lexbuf -> Syntax.xformula
