/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Roger L. Whitcomb.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *	Command line calculator Antlr (v4) grammar.
 *
 *  History:
 *      04-Dec-2020 (rlwhitcomb)
 *	    First version (not quite complete yet).
 *	06-Dec-2020 (rlwhitcomb)
 *	    More functions.
 */

grammar Calc;

prog
   : stmt+
   ;

stmt
   : expr FORMAT ? ENDEXPR      # exprStmt
   | directive ENDEXPR          # directiveStmt
   | ENDEXPR                    # emptyStmt
   ;

expr
   : value                       # valueExpr
   | ID '++'                     # postIncExpr
   | ID '--'                     # postDecExpr
   | '++' ID                     # preIncExpr
   | '--' ID                     # preDecExpr
   | '+' expr                    # posateExpr
   | '-' expr                    # negateExpr
   | '~' expr                    # logicalNotExpr
   | '!' expr                    # booleanNotExpr
   | '(' expr ')'                # parenExpr
   |<assoc=right> expr '**' expr # powerExpr
   | expr '*' expr               # multiplyExpr
   | expr '/' expr               # divideExpr
   | expr '%' expr               # modulusExpr
   | expr '+' expr               # addExpr
   | expr '-' expr               # subtractExpr
   | ABS expr                    # absExpr
   | SIN expr                    # sinExpr
   | COS expr                    # cosExpr
   | TAN expr                    # tanExpr
   | ASIN expr                   # asinExpr
   | ACOS expr                   # acosExpr
   | ATAN expr                   # atanExpr
   | ATAN2 expr2                 # atan2Expr
   | SINH expr                   # sinhExpr
   | COSH expr                   # coshExpr
   | TANH expr                   # tanhExpr
   | SQRT expr                   # sqrtExpr
   | CBRT expr                   # cbrtExpr
   | LOG expr                    # logExpr
   | LN expr                     # lnExpr
   | SIGNUM expr                 # signumExpr
   | expr '!'                    # factorialExpr
   | expr '>>>' expr             # shiftRightUnsignedExpr
   | expr '>>' expr              # shiftRightExpr
   | expr '<<' expr              # shiftLeftExpr
   | expr '<=>' expr             # spaceshipExpr
   | expr '<=' expr              # lessEqualExpr
   | expr '<' expr               # lessExpr
   | expr '>=' expr              # greaterEqualExpr
   | expr '>' expr               # greaterExpr
   | expr STRICTEQUAL expr       # strictEqualExpr
   | expr STRICTNOTEQUAL expr    # strictNotEqualExpr
   | expr EQUAL expr             # equalExpr
   | expr NOTEQUAL expr          # notEqualExpr
   | expr L_AND expr             # logicalAndExpr
   | expr L_XOR expr             # logicalXorExpr
   | expr L_OR expr              # logicalOrExpr
   | expr B_AND expr             # booleanAndExpr
   | expr B_OR expr              # booleanOrExpr
   |<assoc=right> expr '?' expr ':' expr # eitherOrExpr
   |<assoc=right> ID ASSIGN expr # assignExpr
   ;

expr2
   : '(' expr ',' expr ')'
   | expr ',' expr
   ;

value
   : STRING                      # stringValue
   | NUMBER                      # numberValue
   | ( TRUE | FALSE )            # booleanValue
   | NULL                        # nullValue
   | PI                          # piValue
   | E                           # eValue
   | ID                          # idValue
   ;

directive
   : DECIMAL ( '(' NUMBER ')' | NUMBER )     # decimalDirective
   | DEFAULT                     # defaultDirective
   | DOUBLE                      # doubleDirective
   | FLOAT                       # floatDirective
   | CLEAR                       # clearDirective
   | ( QUIT | EXIT )             # exitDirective
   ;

/* Lexer rules start here */

TRUE   : [tT][rR][uU][eE] ;

FALSE  : [fF][aA][lL][sS][eE] ;

NULL   : [nN][uU][lL][lL] ;

PI     : [pP][iI] ;

E      : [eE] ;

ABS    : [aA][bB][sS] ;

SINH   : [sS][iI][nN][hH] ;

SIN    : [sS][iI][nN] ;

COSH   : [cC][oO][sS][hH] ;

COS    : [cC][oO][sS] ;

TANH   : [tT][aA][nN][hH] ;

TAN    : [tT][aA][nN] ;

ASIN   : [aA][sS][iI][nN] ;

ACOS   : [aA][cC][oO][sS] ;

ATAN   : [aA][tT][aA][nN] ;

ATAN2  : [aA][tT][aA][nN][2] ;

SQRT   : [sS][qQ][rR][tT] ;

CBRT   : [cC][bB][rR][tT] ;

LOG    : [lL][oO][gG] ;

LN     : [lL][nN] ;

SIGNUM : [sS][iI][gG][nN][uU][mM] ;

QUIT   : [qQ][uU][iI][tT] ;

EXIT   : [eE][xX][iI][tT] ;


/* Note: this needs to be last so that these other "ID" like things
 * will be recognized first. */

ID     : [a-zA-Z_] [a-zA-Z_0-9]* ;


B_AND          : '&&' ;

L_AND          : '&' ;

B_OR           : '||' ;

L_OR           : '|' ;

L_XOR          : '^' ;

STRICTEQUAL    : '===' ;

EQUAL          : '==' ;

ASSIGN         : '=' ;

STRICTNOTEQUAL : '!==' ;

NOTEQUAL       : '!=' ;


DECIMAL
   : DIR [dD][eE][cC][iI][mM][aA][lL]
   ;

DEFAULT
   : DIR [dD][eE][fF][aA][uU][lL][tT]
   ;

DOUBLE
   : DIR [dD][oO][uU][bB][lL][eE]
   ;

FLOAT
   : DIR [fF][lL][oO][aA][tT]
   ;

CLEAR
   : DIR [cC][lL][eE][aA][rR]
   ;

FORMAT
   : ',' [xXtThH]
   ;

STRING
   : '"' (ESC | SAFECODEPOINT)* '"'
   | '\'' (ESC2 | SAFECODEPOINT)* '\''
   ;

NUMBER
   : '-'? INT ('.' [0-9] *)? EXP?
   ;


fragment DIR
   : '$'
   ;

fragment ESC
   : '\\' (["\\/bfnrt] | UNICODE)
   ;

fragment ESC2
   : '\\' (['\\/bfnrt] | UNICODE)
   ;

fragment UNICODE
   : 'u' HEX HEX HEX HEX
   ;

fragment HEX
   : [0-9a-fA-F]
   ;

fragment SAFECODEPOINT
   : ~ ["\\\u0000-\u001F]
   ;

fragment INT
   : '0' | [1-9] [0-9]*
   ;

// no leading zeros

fragment EXP
   : [Ee] [+\-]? INT
   ;

// \- since - means "range" inside [...]

WS
   : [ \t] + -> skip
   ;

ENDEXPR
   : '\r'? '\n'
   | ';'
   ;

