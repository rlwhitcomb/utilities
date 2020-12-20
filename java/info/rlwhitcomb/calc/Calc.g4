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
 *	07-Dec-2020 (rlwhitcomb)
 *	    Help and version commands.
 *	07-Dec-2020 (rlwhitcomb)
 *	    Degrees and radians directives.
 *	07-Dec-2020 (rlwhitcomb)
 *	    Some aliases for the directives.
 *	08-Dec-2020 (rlwhitcomb)
 *	    Allow Unicode pi symbol.
 *	08-Dec-2020 (rlwhitcomb)
 *	    Add "round" function.
 *	09-Dec-2020 (rlwhitcomb)
 *	    Straighten out expr-expr conflict.
 *	11-Dec-2020 (rlwhitcomb)
 *	    Some alias renaming; more operators; hex, octal, and binary constants.
 *	11-Dec-2020 (rlwhitcomb)
 *	    Join operator.
 *	11-Dec-2020 (rlwhitcomb)
 *	    Fix precedence of factorial; rename some aliases.
 *	14-Dec-2020 (rlwhitcomb)
 *	    Allow octal and binary output format.
 *	14-Dec-2020 (rlwhitcomb)
 *	    Output formats in the KB, MB, etc. format (,k).
 *	    Shortcuts for Quit or Exit.
 *	16-Dec-2020 (rlwhitcomb)
 *	    Add fib(n) and $echo directive.
 *	16-Dec-2020 (rlwhitcomb)
 *	    Add KB constant for input.
 *	17-Dec-2020 (rlwhitcomb)
 *	    Add object and array.
 *	18-Dec-2020 (rlwhitcomb)
 *	    Allow assignments to object and array.
 *	    Adjust precedence of functions vs. arithmetic.
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
   | obj                         # objExpr
   | arr                         # arrExpr
   | ID '++'                     # postIncExpr
   | ID '--'                     # postDecExpr
   | '++' ID                     # preIncExpr
   | '--' ID                     # preDecExpr
   | '+' expr                    # posateExpr
   | '-' expr                    # negateExpr
   | '~' expr                    # bitNotExpr
   | '!' expr                    # booleanNotExpr
   | expr '!'                    # factorialExpr
   | '(' expr ')'                # parenExpr
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
   | ROUND expr2                 # roundExpr
   | GCD expr2                   # gcdExpr
   | MAX exprN                   # maxExpr
   | MIN exprN                   # minExpr
   | JOIN exprN                  # joinExpr
   | FIB expr                    # fibExpr
   |<assoc=right> expr '**' expr # powerExpr
   | expr '*' expr               # multiplyExpr
   | expr '/' expr               # divideExpr
   | expr '%' expr               # modulusExpr
   | expr '+' expr               # addExpr
   | expr '-' expr               # subtractExpr
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
   | expr BIT_AND expr           # bitAndExpr
   | expr BIT_XOR expr           # bitXorExpr
   | expr BIT_OR expr            # bitOrExpr
   | expr BOOL_AND expr          # booleanAndExpr
   | expr BOOL_OR expr           # booleanOrExpr
   |<assoc=right> expr '?' expr ':' expr # eitherOrExpr
   |<assoc=right> var ASSIGN expr # assignExpr
   ;

expr2
   : '(' expr ',' expr ')'
   | expr ',' expr
   ;

exprN
   : '(' expr ( ',' expr ) * ')'
   | expr ( ',' expr ) *
   ;

obj
   : '{' pair ( ',' pair ) * '}'
   | '{' '}'
   ;

pair
   : ID ':' expr
   | STRING ':' expr
   ;

arr
   : '[' expr ( ',' expr ) * ']'
   | '[' ']'
   ;

var
   : var ( '.' ( var | STRING ) ) +
   | var '[' expr ']'
   | ID
   ;

value
   : STRING                      # stringValue
   | NUMBER                      # numberValue
   | BIN_CONST                   # binaryValue
   | OCT_CONST                   # octalValue
   | HEX_CONST                   # hexValue
   | KB_CONST                    # kbValue
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
   | DEGREES                     # degreesDirective
   | RADIANS                     # radiansDirective
   | BINARY                      # binaryDirective
   | SI                          # siDirective
   | MIXED			 # mixedDirective
   | CLEAR                       # clearDirective
   | VERSION                     # versionDirective
   | ECHO expr ?                 # echoDirective
   | ( HELP | '?' )              # helpDirective
   | ( QUIT | EXIT )             # exitDirective
   ;

/* Lexer rules start here */

TRUE    : [tT][rR][uU][eE] ;

FALSE   : [fF][aA][lL][sS][eE] ;

NULL    : [nN][uU][lL][lL] ;

PI      : ( '\u03c0' | [pP][iI] ) ;

E       : [eE] ;

ABS     : [aA][bB][sS] ;

SINH    : [sS][iI][nN][hH] ;

SIN     : [sS][iI][nN] ;

COSH    : [cC][oO][sS][hH] ;

COS     : [cC][oO][sS] ;

TANH    : [tT][aA][nN][hH] ;

TAN     : [tT][aA][nN] ;

ASIN    : [aA][sS][iI][nN] ;

ACOS    : [aA][cC][oO][sS] ;

ATAN    : [aA][tT][aA][nN] ;

ATAN2   : [aA][tT][aA][nN][2] ;

SQRT    : [sS][qQ][rR][tT] ;

CBRT    : [cC][bB][rR][tT] ;

LOG     : [lL][oO][gG] ;

LN      : [lL][nN] ;

SIGNUM  : [sS][iI][gG][nN][uU][mM] ;

ROUND   : [rR][oO][uU][nN][dD] ;

GCD     : [gG][cC][dD] ;

MAX     : [mM][aA][xX] ;

MIN     : [mM][iI][nN] ;

JOIN    : [jJ][oO][iI][nN] ;

FIB     : [fF][iI][bB] ;


/* Commands (or directives) that are specially treated,
 * NOT as identifiers.  */

HELP    : [hH][eE][lL][pP] ;

VERSION : [vV][eE][rR][sS][iI][oO][nN] ;

QUIT    : ( [qQ] | [qQ][uU][iI][tT] ) ;

EXIT    : ( [xX] | [eE][xX][iI][tT] ) ;


/* Note: this needs to be last so that these other "ID" like things
 * will be recognized first. */

ID     : [a-zA-Z_] [a-zA-Z_0-9]* ;


BOOL_AND       : '&&' ;

BIT_AND        : '&' ;

BOOL_OR        : '||' ;

BIT_OR         : '|' ;

BIT_XOR        : '^' ;

STRICTEQUAL    : '===' ;

EQUAL          : '==' ;

ASSIGN         : '=' ;

STRICTNOTEQUAL : '!==' ;

NOTEQUAL       : '!=' ;


DECIMAL
   : DIR ( [dD][eE][cC] | [dD][eE][cC][iI][mM][aA][lL] )
   ;

DEFAULT
   : DIR ( [dD][eE][fF] | [dD][eE][fF][aA][uU][lL][tT] )
   ;

DOUBLE
   : DIR [dD][oO][uU][bB][lL][eE]
   ;

FLOAT
   : DIR [fF][lL][oO][aA][tT]
   ;

DEGREES
   : DIR ( [dD][eE][gG] | [dD][eE][gG][rR][eE][eE][sS] )
   ;

RADIANS
   : DIR ( [rR][aA][dD] | [rR][aA][dD][iI][aA][nN][sS] )
   ;

BINARY
   : DIR ( [bB][iI][nN] | [bB][iI][nN][aA][rR][yY] )
   ;

SI
   : DIR ( [sS][iI] | [tT][eE][nN] )
   ;

MIXED
   : DIR ( [mM][iI][xX] | [mM][iI][xX][eE][dD] )
   ;


CLEAR
   : DIR ( [cC][lL][rR] | [cC][lL][eE][aA][rR] )
   ;

ECHO
   : DIR [eE][cC][hH][oO]
   ;


FORMAT
   : ',' [xXtThHoObBkK%]
   ;

STRING
   : '"' (ESC | SAFECODEPOINT)* '"'
   | '\'' (ESC2 | SAFECODEPOINT2)* '\''
   ;

NUMBER
   : INT ('.' [0-9] *)? EXP?
   ;

BIN_CONST
   : '0' ('b' | 'B') [01]+
   ;

OCT_CONST
   : '0' [0-7]+
   ;

HEX_CONST
   : '0' [xX] [0-9a-fA-F] +
   ;

KB_CONST
   : INT [kKmMgGtTpPeE][iI]?[bB]?
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

fragment SAFECODEPOINT2
   : ~ ['\\\u0000-\u001F]
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

