/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2021 Roger L. Whitcomb.
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
 *	20-Dec-2020 (rlwhitcomb)
 *	    Change the way we process quit, help, etc. commands.
 *	24-Dec-2020 (rlwhitcomb)
 *	    Change lexer rules for better readability of the
 *	    case-insensitive rules. I don't think eitherOr or
 *	    assign need right associativity. Allow variable
 *	    lists on $CLEAR directive.
 *	24-Dec-2020 (rlwhitcomb)
 *	    $debug directive.
 *	28-Dec-2020 (rlwhitcomb)
 *	    Interpolated strings.
 *	28-Dec-2020 (rlwhitcomb)
 *	    New tree methods to distinguish nested object/array references.
 *	31-Dec-2020 (rlwhitcomb)
 *	    Allow variables as expressions and targets of ++ and --.
 *	04-Jan-2021 (rlwhitcomb)
 *	    Add NAND, ANDNOT, NOR, and XNOR bit operations, and boolean XOR.
 *	04-Jan-2021 (rlwhitcomb)
 *	    Format option for object and arrays in "pretty" JSON format.
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
   : value                               # valueExpr
   | obj                                 # objExpr
   | arr                                 # arrExpr
   | var                                 # varExpr
   | var '++'                            # postIncExpr
   | var '--'                            # postDecExpr
   | '++' var                            # preIncExpr
   | '--' var                            # preDecExpr
   | '+' expr                            # posateExpr
   | '-' expr                            # negateExpr
   | '~' expr                            # bitNotExpr
   | '!' expr                            # booleanNotExpr
   | expr '!'                            # factorialExpr
   | '(' expr ')'                        # parenExpr
   | ABS expr                            # absExpr
   | SIN expr                            # sinExpr
   | COS expr                            # cosExpr
   | TAN expr                            # tanExpr
   | ASIN expr                           # asinExpr
   | ACOS expr                           # acosExpr
   | ATAN expr                           # atanExpr
   | ATAN2 expr2                         # atan2Expr
   | SINH expr                           # sinhExpr
   | COSH expr                           # coshExpr
   | TANH expr                           # tanhExpr
   | SQRT expr                           # sqrtExpr
   | CBRT expr                           # cbrtExpr
   | LOG expr                            # logExpr
   | LN expr                             # lnExpr
   | SIGNUM expr                         # signumExpr
   | ROUND expr2                         # roundExpr
   | GCD expr2                           # gcdExpr
   | MAX exprN                           # maxExpr
   | MIN exprN                           # minExpr
   | JOIN exprN                          # joinExpr
   | FIB expr                            # fibExpr
   |<assoc=right> expr '**' expr         # powerExpr
   | expr '*' expr                       # multiplyExpr
   | expr '/' expr                       # divideExpr
   | expr '%' expr                       # modulusExpr
   | expr '+' expr                       # addExpr
   | expr '-' expr                       # subtractExpr
   | expr '>>>' expr                     # shiftRightUnsignedExpr
   | expr '>>' expr                      # shiftRightExpr
   | expr '<<' expr                      # shiftLeftExpr
   | expr '<=>' expr                     # spaceshipExpr
   | expr '<=' expr                      # lessEqualExpr
   | expr '<' expr                       # lessExpr
   | expr '>=' expr                      # greaterEqualExpr
   | expr '>' expr                       # greaterExpr
   | expr STRICTEQUAL expr               # strictEqualExpr
   | expr STRICTNOTEQUAL expr            # strictNotEqualExpr
   | expr EQUAL expr                     # equalExpr
   | expr NOTEQUAL expr                  # notEqualExpr
   | expr BIT_AND expr                   # bitAndExpr
   | expr BIT_NAND expr                  # bitNandExpr
   | expr BIT_ANDNOT expr                # bitAndNotExpr
   | expr BIT_XOR expr                   # bitXorExpr
   | expr BIT_XNOR expr                  # bitXnorExpr
   | expr BIT_OR expr                    # bitOrExpr
   | expr BIT_NOR expr                   # bitNorExpr
   | expr BOOL_AND expr                  # booleanAndExpr
   | expr BOOL_XOR expr                  # booleanXorExpr
   | expr BOOL_OR expr                   # booleanOrExpr
   | expr '?' expr ':' expr              # eitherOrExpr
   | var ASSIGN expr                     # assignExpr
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
   : var ( '.' ( var | STRING ) ) +     # objVar
   | var '[' expr ']'                   # arrVar
   | ID                                 # idVar
   ;

value
   : STRING                      # stringValue
   | ISTRING                     # iStringValue
   | NUMBER                      # numberValue
   | BIN_CONST                   # binaryValue
   | OCT_CONST                   # octalValue
   | HEX_CONST                   # hexValue
   | KB_CONST                    # kbValue
   | ( TRUE | FALSE )            # booleanValue
   | NULL                        # nullValue
   | PI_CONST                    # piValue
   | E_CONST                     # eValue
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
   | CLEAR ( ID ( ',' ID ) * )?  # clearDirective
   | ECHO expr ?                 # echoDirective
   | DEBUG ( TRUE | FALSE )      # debugDirective 
   ;

/* Lexer rules start here */

TRUE     : T R U E ;

FALSE    : F A L S E ;

NULL     : N U L L ;

PI_CONST : ( '\u03c0' | P I ) ;

E_CONST  : E ;

ABS      : A B S ;

SINH     : S I N H ;

SIN      : S I N ;

COSH     : C O S H ;

COS      : C O S ;

TANH     : T A N H ;

TAN      : T A N ;

ASIN     : A S I N ;

ACOS     : A C O S ;

ATAN2    : A T A N '2' ;

ATAN     : A T A N ;

SQRT     : S Q R T ;

CBRT     : C B R T ;

LOG      : L O G ;

LN       : L N ;

SIGNUM   : S I G N U M ;

ROUND    : R O U N D ;

GCD      : G C D ;

MAX      : M A X ;

MIN      : M I N ;

JOIN     : J O I N ;

FIB      : F I B ;


/* Note: this needs to be last so that these other "ID" like things
 * will be recognized first. */

ID     : [a-zA-Z_] [a-zA-Z_0-9]* ;


BOOL_AND       : '&&' ;

BIT_AND        : '&' ;

BIT_NAND       : '~&' ;

BIT_ANDNOT     : '&~' ;

BOOL_OR        : '||' ;

BIT_OR         : '|' ;

BIT_NOR        : '~|' ;

BOOL_XOR       : '^^' ;

BIT_XOR        : '^' ;

BIT_XNOR       : '~^' ;

STRICTEQUAL    : '===' ;

EQUAL          : '==' ;

ASSIGN         : '=' ;

STRICTNOTEQUAL : '!==' ;

NOTEQUAL       : '!=' ;


DECIMAL
   : DIR ( D E C | D E C I M A L )
   ;

DEFAULT
   : DIR ( D E F | D E F A U L T )
   ;

DOUBLE
   : DIR D O U B L E
   ;

FLOAT
   : DIR F L O A T
   ;

DEGREES
   : DIR ( D E G | D E G R E E S )
   ;

RADIANS
   : DIR ( R A D | R A D I A N S )
   ;

BINARY
   : DIR ( B I N | B I N A R Y )
   ;

SI
   : DIR ( S I | T E N )
   ;

MIXED
   : DIR ( M I X | M I X E D )
   ;


CLEAR
   : DIR ( C L R | C L E A R )
   ;

ECHO
   : DIR E C H O
   ;

DEBUG
   : DIR D E B U G
   ;


FORMAT
   : ',' ( X | T | H | O | B | K | J | '%' )
   ;

STRING
   : '"'  (ESC  | SAFECODEPOINT)*  '"'
   | '\'' (ESC2 | SAFECODEPOINT2)* '\''
   ;

ISTRING
   : '`'  (ESC3 | SAFECODEPOINT3)* '`'
   ;

NUMBER
   : INT ('.' [0-9] *)? EXP?
   ;

BIN_CONST
   : '0' B [01]+
   ;

OCT_CONST
   : '0' [0-7]+
   ;

HEX_CONST
   : '0' X [0-9a-fA-F] +
   ;

KB_CONST
   : INT ( K | M | G | T | P | E ) I? B?
   ;


fragment A : [aA] ;
fragment B : [bB] ;
fragment C : [cC] ;
fragment D : [dD] ;
fragment E : [eE] ;
fragment F : [fF] ;
fragment G : [gG] ;
fragment H : [hH] ;
fragment I : [iI] ;
fragment J : [jJ] ;
fragment K : [kK] ;
fragment L : [lL] ;
fragment M : [mM] ;
fragment N : [nN] ;
fragment O : [oO] ;
fragment P : [pP] ;
fragment Q : [qQ] ;
fragment R : [rR] ;
fragment S : [sS] ;
fragment T : [tT] ;
fragment U : [uU] ;
fragment V : [vV] ;
fragment W : [wW] ;
fragment X : [xX] ;
fragment Y : [yY] ;
fragment Z : [zZ] ;

fragment DIR : '$' ;

fragment ESC
   : '\\' (["\\/bfnrt] | UNICODE)
   ;

fragment ESC2
   : '\\' (['\\/bfnrt] | UNICODE)
   ;

fragment ESC3
   : '\\' ([`\\/bfnrt] | UNICODE)
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

fragment SAFECODEPOINT3
   : ~ [`\\\u0000-\u001F]
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

