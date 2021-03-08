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
 *	05-Jan-2021 (rlwhitcomb)
 *	    Add "$include" directive.
 *	05-Jan-2021 (rlwhitcomb)
 *	    Unlimited precision directive.
 *	07-Jan-2021 (rlwhitcomb)
 *	    $resultsonly directive.
 *	    Change format leading to ';' to reduce confusion.
 *	07-Jan-2021 (rlwhitcomb)
 *	    Start of the +=, -=, etc. assign operators.
 *	07-Jan-2021 (rlwhitcomb)
 *	    Allow comments.
 *	07-Jan-2021 (rlwhitcomb)
 *	    Remaining assignment operators; update associativity of operators.
 *	08-Jan-2021 (rlwhitcomb)
 *	    Loop construct. Power assign op.
 *	09-Jan-2021 (rlwhitcomb)
 *	    ln2 and isprime.
 *	10-Jan-2021 (rlwhitcomb)
 *	    Quiet mode directive.
 *	15-Jan-2021 (rlwhitcomb)
 *	    Allow looping over an empty expression list.
 *	15-Jan-2021 (rlwhitcomb)
 *	    Fix precedence of operators.
 *	19-Jan-2021 (rlwhitcomb)
 *	    Add "length" and "scale" functions.
 *	20-Jan-2021 (rlwhitcomb)
 *	    Adjust compare / equal operator precedence.
 *	21-Jan-2021 (rlwhitcomb)
 *	    Tweak operator precedence again.
 *	28-Jan-2021 (rlwhitcomb)
 *	    Add LCM function.
 *	28-Jan-2021 (rlwhitcomb)
 *	    New function for Bernoulli numbers.
 *	30-Jan-2021 (rlwhitcomb)
 *	    New "rational" or "fraction" mode; "frac" function to make
 *	    a fraction value. New 'd' and 'f' formats.
 *	01-Feb-2021 (rlwhitcomb)
 *	    Recognize Unicode "not equal" and "not identical" symbols,
 *	    as well as all the relevant Unicode PI symbols.
 *	08-Feb-2021 (rlwhitcomb)
 *	    Allow "numberOption" and "modeOption" in directives to use
 *	    a variable reference for the value. Allow "precision" as an
 *	    alias for "decimal".
 *	16-Feb-2021 (rlwhitcomb)
 *	    Add "if" and "while" statements. Make LOOP, IF, and WHILE into expressions.
 *	    Add "define" statement.
 *	17-Feb-2021 (rlwhitcomb)
 *	    Move LOOP, WHILE, and IF back outside of "expr" and rename "loopOrExpr" to "stmtOrExpr".
 *	19-Feb-2021 (rlwhitcomb)
 *	    Change format directive character because of conflicts with ENDEXPR.
 *	    Widen possible format characters and allow % precision.
 *	22-Feb-2021 (rlwhitcomb)
 *	    Add "eval" function.
 *	22-Feb-2021 (rlwhitcomb)
 *	    Refactor "loopvar" to "localvar".
 *	23-Feb-2021 (rlwhitcomb)
 *	    Add ":timing" directive.
 *	24-Feb-2021 (rlwhitcomb)
 *	    Add a bunch more Unicode symbols.
 *	01-Mar-2021 (rlwhitcomb)
 *	    And more...
 *	04-Mar-2021 (rlwhitcomb)
 *	    Add a "FACTORS" function.
 *	05-Mar-2021 (rlwhitcomb)
 *	    Add "PFACTORS" function.
 */

grammar Calc;

prog
   : stmt+
   ;

stmt
   : stmtOrExpr
   | directive ENDEXPR
   | define ENDEXPR
   ;

stmtOrExpr
   : expr FORMAT ? ENDEXPR ?              # exprStmt
   | LOOP ( LOCALVAR IN ) ? loopCtl block # loopStmt
   | WHILE expr block                     # whileStmt
   | IF expr block ( ELSE block ) ?       # ifStmt
   | ENDEXPR                              # emptyStmt
   ;

block
   : '{' stmtOrExpr * '}'                # stmtBlock
   ;

expr
   : value                               # valueExpr
   | obj                                 # objExpr
   | arr                                 # arrExpr
   | var                                 # varExpr
   | '(' expr ')'                        # parenExpr
   | var INC_OP                          # postIncOpExpr
   |<assoc=right> INC_OP var             # preIncOpExpr
   |<assoc=right> ADD_OP expr            # negPosExpr
   |<assoc=right> ('!'|'\u00AC') expr    # booleanNotExpr
   |<assoc=right> '~' expr               # bitNotExpr
   | expr '!'                            # factorialExpr
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
   | LN2 expr                            # ln2Expr
   | LN expr                             # lnExpr
   | SIGNUM expr                         # signumExpr
   | LENGTH expr                         # lengthExpr
   | SCALE expr                          # scaleExpr
   | ROUND expr2                         # roundExpr
   | ISPRIME expr                        # isPrimeExpr
   | GCD expr2                           # gcdExpr
   | LCM expr2                           # lcmExpr
   | MAX exprN                           # maxExpr
   | MIN exprN                           # minExpr
   | JOIN exprN                          # joinExpr
   | FIB expr                            # fibExpr
   | BN expr                             # bernExpr
   | FRAC expr2                          # fracExpr
   | EVAL expr                           # evalExpr
   | FACTORS expr                        # factorsExpr
   | PFACTORS expr                       # primeFactorsExpr
   |<assoc=right> expr '**' expr         # powerExpr
   | expr MULT_OP expr                   # multiplyExpr
   | expr ADD_OP expr                    # addExpr
   | expr SHIFT_OP expr                  # shiftExpr
   | expr '<=>' expr                     # spaceshipExpr
   | expr COMPARE_OP expr                # compareExpr
   | expr EQUAL_OP expr                  # equalExpr
   | expr BIT_OP expr                    # bitExpr
   | expr BOOL_OP expr                   # booleanExpr
   |<assoc=right> expr '?' expr ':' expr # eitherOrExpr
   |<assoc=right> var ASSIGN expr        # assignExpr
   |<assoc=right> var '**=' expr         # powerAssignExpr
   |<assoc=right> var MULT_ASSIGN expr   # multAssignExpr
   |<assoc=right> var ADD_ASSIGN expr    # addAssignExpr
   |<assoc=right> var SHIFT_ASSIGN expr  # shiftAssignExpr
   |<assoc=right> var BIT_ASSIGN expr    # bitAssignExpr
   ;

expr2
   : '(' expr ',' expr ')'
   | expr ',' expr
   ;

exprN
   : '(' exprList ')'
   | exprList
   ;

loopCtl
   : ( expr DOTS ) ? expr ( ',' expr ) ?
   | '(' exprList ')'
   | '(' ')'
   ;

arr
   : '[' exprList ']'
   | '[' ']'
   ;

exprList
   : expr ( ',' expr ) *
   ;

obj
   : '{' pair ( ',' pair ) * '}'
   | '{' '}'
   ;

pair
   : ID ':' expr
   | STRING ':' expr
   ;

var
   : var ( '.' ( var | STRING ) ) +     # objVar
   | var '[' expr ']'                   # arrVar
   | ID                                 # idVar
   | LOCALVAR                           # localVar
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
   | FRAC_CONST                  # fracValue
   ;

define
   : DEFINE ID '=' ( stmtOrExpr | block )  # defineStmt
   ;

directive
   : ( DECIMAL | PRECISION ) numberOption  # decimalDirective
   | DEFAULT                               # defaultDirective
   | DOUBLE                                # doubleDirective
   | FLOAT                                 # floatDirective
   | UNLIMITED                             # unlimitedDirective
   | DEGREES                               # degreesDirective
   | RADIANS                               # radiansDirective
   | BINARY                                # binaryDirective
   | SI                                    # siDirective
   | MIXED                                 # mixedDirective
   | CLEAR idList ?                        # clearDirective
   | ECHO expr ?                           # echoDirective
   | INCLUDE expr                          # includeDirective
   | TIMING modeOption                     # timingDirective
   | RATIONAL modeOption                   # rationalDirective
   | DEBUG modeOption                      # debugDirective
   | RESULTSONLY modeOption                # resultsOnlyDirective
   | QUIET modeOption                      # quietDirective
   ;

numberOption
   : '(' NUMBER ')'
   | NUMBER
   | var
   ;

idList
   : '[' ID ( ',' ID ) * ']'
   | ID ( ',' ID ) *
   | '[' ']'
   ;

modeOption
   : TRUE
   | FALSE
   | 'on'
   | 'off'
   | 'pop'
   | 'previous'
   | 'prev'
   | var
   ;


/* Lexer rules start here */

TRUE     : T R U E ;

FALSE    : F A L S E ;

NULL     : N U L L ;

PI_CONST : P I
         | ( '\u03a0' | '\u03c0' | '\u03d6' | '\u1d28' | '\u213c' | '\u213f' )
         | ( '\u{1D6B7}' | '\u{1D6D1}' | '\u{1D6E1}' )
         | ( '\u{1D6F1}' | '\u{1D70B}' | '\u{1D71B}' )
         | ( '\u{1D72B}' | '\u{1D745}' | '\u{1D755}' )
         | ( '\u{1D765}' | '\u{1D77F}' | '\u{1D78F}' )
         | ( '\u{1D79F}' | '\u{1D7B9}' | '\u{1D7C9}' )
         ;

E_CONST  : E
         | '\u2107'
         ;

FRAC_CONST
         : ( '\u00BC' | '\u00BD' | '\u00BE' )             /* 1/4, 1/2, 3/4      */
         | ( '\u2150' | '\u2151' | '\u2152' )             /* 1/7, 1/9, 1/10     */
         | ( '\u2189' | '\u2153' | '\u2154' )             /* 0/3, 1/3, 2/3      */
         | ( '\u2155' | '\u2156' | '\u2157' | '\u2158' )  /* 1/5, 2/5, 3/5, 4/5 */
         | ( '\u2159' | '\u215A' )                        /* 1/6, 5/6           */
         | ( '\u215B' | '\u215C' | '\u215D' | '\u215E' )  /* 1/8, 3/8, 5/8, 7/8 */
         ;

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

SQRT     : ( S Q R T | '\u221A' ) ;

CBRT     : ( C B R T | '\u221B' ) ;

LOG      : L O G ;

LN       : L N ;

LN2      : L N '2' ;

SIGNUM   : S I G N U M ;

LENGTH   : L E N G T H ;

SCALE    : S C A L E ;

ROUND    : R O U N D ;

ISPRIME  : I S P R I M E ;

GCD      : G C D ;

LCM      : L C M ;

MAX      : M A X ;

MIN      : M I N ;

JOIN     : J O I N ;

FIB      : F I B ;

BN       : B N ;

FRAC     : F R A C ;

EVAL     : E V A L ;

FACTORS  : F A C T O R S ;

PFACTORS : P F A C T O R S ;

LOOP     : L O O P ;

WHILE    : W H I L E ;

IN       : I N ;

IF       : I F ;

ELSE     : E L S E ;

DEFINE   : ( D E F | D E F I N E ) ;


/* Note: this needs to be last so that these other "ID" like things
 * will be recognized first. */

ID     : [a-zA-Z_] [a-zA-Z_0-9]* ;


DOTS
       : '..'
       | '\u2026'
       ;

LOCALVAR
       : '$' [a-zA-Z_] [a-zA-Z_0-9]* ;

INC_OP
       : '++'
       | ( '--' | '\u2212\u2212' )
       ;

ADD_OP
       : '+'
       | ( '-' | '\u2212' )
       ;

MULT_OP
       : '*'
       | '\u00D7'
       | '\u2217'
       | '/'
       | '\u00F7'
       | '%'
       ;

ADD_ASSIGN
       : '+='
       | ( '-=' | '\u2212=' )
       ;

MULT_ASSIGN
       : '*='
       | '\u00D7='
       | '\u2217='
       | '/='
       | '\u00F7='
       | '%='
       ;

SHIFT_ASSIGN
       : '>>>='
       | '>>='
       | '<<='
       ;

SHIFT_OP
       : '>>>'
       | '>>'
       | '<<'
       ;

COMPARE_OP
       : ( '<=' | '\u2264' )
       | '<'
       | ( '>=' | '\u2265' )
       | '>'
       ;

EQUAL_OP
       : '==='
       | '!=='
       | '\u2262'
       | '=='
       | '!='
       | '\u2260'
       ;

BIT_ASSIGN
       : '&='
       | '~&='
       | '&~='
       | '^='
       | '~^='
       | '|='
       | '~|='
       ;

BOOL_OP
       : ( '&&' | '\u2227' )
       | ( '||' | '\u2228' )
       | ( '^^' | '\u22BB' )
       ;

BIT_OP
       : '&'
       | ( '~&' | '\u22BC' )
       | '&~'
       | '^'
       | '~^'
       | '|'
       | ( '~|' | '\u22BD' )
       ;

ASSIGN : '=' ;


DECIMAL
   : DIR  ( D E C | D E C I M A L )
   ;

PRECISION
   : DIR  ( P R E C | P R E C I S I O N )
   ;

DEFAULT
   : DIR  ( D E F | D E F A U L T )
   ;

DOUBLE
   : DIR  ( D B L | D O U B L E )
   ;

FLOAT
   : DIR  ( F L T | F L O A T )
   ;

UNLIMITED
   : DIR  ( U N L | U N L I M I T E D )
   ;

DEGREES
   : DIR  ( D E G | D E G R E E S )
   ;

RADIANS
   : DIR  ( R A D | R A D I A N S )
   ;

BINARY
   : DIR  ( B I N | B I N A R Y )
   ;

SI
   : DIR  ( S I | T E N )
   ;

MIXED
   : DIR  ( M I X | M I X E D )
   ;

TIMING
   : DIR  ( T I M E | T I M I N G )
   ;

RATIONAL
   : DIR  ( R A T I O N | F R A C | R A T I O N A L | F R A C T I O N )
   ;

CLEAR
   : DIR  ( C L R | C L E A R )
   ;

ECHO
   : DIR  E C H O
   ;

INCLUDE
   : DIR  ( I N C | I N C L U D E )
   ;

DEBUG
   : DIR  ( D E B | D E B U G )
   ;

RESULTSONLY
   : DIR  ( R E S | R E S U L T | R E S U L T S | R E S U L T O N L Y | R E S U L T S O N L Y )
   ;

QUIET
   : DIR  Q U I E T
   ;


FORMAT
   : '@' ( [a-zA-Z,] | ( '-' ? [0-9]* ) ? '%' )
   ;

STRING
   : '"'      (ESC1 | SAFECODEPOINT1)* '"'
   | '\''     (ESC2 | SAFECODEPOINT2)* '\''
   | '\u2018' (ESC3 | SAFECODEPOINT3)* '\u2019'
   | '\u201C' (ESC4 | SAFECODEPOINT4)* '\u201D'
   | '\u2039' (ESC5 | SAFECODEPOINT5)* '\u203A'
   | '\u00AB' (ESC6 | SAFECODEPOINT6)* '\u00BB'
   ;

ISTRING
   : '`'  (ESCI | SAFECODEPOINTI)* '`'
   ;

fragment ESC1
   : '\\' (["\\/bfnrt] | UNICODE)
   ;

fragment ESC2
   : '\\' (['\\/bfnrt] | UNICODE)
   ;

fragment ESC3
   : '\\' ([\\/bfnrt] | '\u2019' | UNICODE)
   ;

fragment ESC4
   : '\\' ([\\/bfnrt] | '\u201D' | UNICODE)
   ;

fragment ESC5
   : '\\' ([\\/bfnrt] | '\u203A' | UNICODE)
   ;

fragment ESC6
   : '\\' ([\\/bfnrt] | '\u00BB' | UNICODE)
   ;

fragment ESCI
   : '\\' ([`\\/bfnrt] | UNICODE)
   ;

fragment UNICODE
   : 'u' HEX HEX HEX HEX
   | 'u' '{' HEX + '}'
   ;

fragment HEX
   : [0-9a-fA-F]
   ;

fragment SAFECODEPOINT1
   : ~ ["\\\u0000-\u001F]
   ;

fragment SAFECODEPOINT2
   : ~ ['\\\u0000-\u001F]
   ;

fragment SAFECODEPOINT3
   : ~ [\\\u0000-\u001F\u2019]
   ;

fragment SAFECODEPOINT4
   : ~ [\\\u0000-\u001F\u201D]
   ;

fragment SAFECODEPOINT5
   : ~ [\\\u0000-\u001F\u203A]
   ;

fragment SAFECODEPOINT6
   : ~ [\\\u0000-\u001F\u00BB]
   ;

fragment SAFECODEPOINTI
   : ~ [`\\\u0000-\u001F]
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

// no leading zeros
fragment INT
   : '0' | [1-9] [0-9]*
   ;

// \- since - means "range" inside [...]
fragment EXP
   : [Ee] [+\-]? INT
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

fragment DIR : ':' ;

WS
   : [ \t] + -> skip
   ;

COMMENT
   : '/*' .*? '*/' -> skip
   ;

LINE_COMMENT
   : ( '#' | '//' ) .*? '\r'? '\n' -> skip
   ;

ENDEXPR
   : '\r'? '\n'
   | ';'
   ;

