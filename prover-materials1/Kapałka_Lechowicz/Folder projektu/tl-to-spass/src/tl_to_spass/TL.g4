/*
* TL rewritten for Antlr4 by Kamil Kapałka
*
*/

grammar TL;

/*------------------------------------------------------------------
 * PARSER RULES
 *------------------------------------------------------------------*/

 condition
   :formula (ENDLINE formula)* ENDLINE* EOF
   |EOF
   ;
 formula
   : LPAREN formula bin_connective formula RPAREN 
   | NOT formula 
   | ALWAYS LPAREN variable separator formula RPAREN
   | EVENTUALLY LPAREN variable separator formula RPAREN  
   | pred_constant LPAREN term (separator term)* RPAREN
   | term EQUAL term
   | formula '?'
   ;


term
   : ind_constant
   | variable
   | func_constant LPAREN term (separator term)* RPAREN
   ;

bin_connective
   : CONJ
   | DISJ
   | IMPL
   | BICOND
   ;
//used in FORALL|EXISTS and following predicates
variable
   : VAR_ID WORD
   ;
//predicate constant - np. _isProfesor(?x)   
pred_constant
   : PRED_ID WORD
   ;
//individual constant - used in single predicates
ind_constant
   :IND_ID WORD
   ;
//used to create functions, np. .presidentOf(?America) = #Trump
func_constant
   :FUNC_ID WORD 
   ;
VAR_ID
    : '?'
    ;
PRED_ID
    : '_'
    ;
IND_ID
    :'#'
    ;

FUNC_ID
    :'.'
    ;
WORD
    :CHARACTER+ 
    ;
LPAREN
   :'('
   ;
RPAREN
   :')'
   ;
separator
   :','
   ;
EQUAL
   :'='
   ;
NOT
   :'\u00ac' 
   ;
ALWAYS
   :'\u25A1' 
   ;
EVENTUALLY
   :'\u25C7'
   ;
fragment CHARACTER
   :('0' .. '9' | 'a' .. 'z' | 'A' .. 'Z')
   ;
CONJ
   :'\u2227'
   ;
DISJ
   :'\u2228'
   ;
IMPL
   :'\u2192'
   ;
BICOND
   :'\u2194'
   ;
ENDLINE
   :('\r'|'\n')+
   ;
WHITESPACE
   :(' '|'\t')+->skip
   ;