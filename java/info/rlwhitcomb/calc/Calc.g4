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
 *	08-Mar-2021 (rlwhitcomb)
 *	    Add "SUMOF" and "PRODUCTOF" functions.
 *	09-Mar-2021 (rlwhitcomb)
 *	    Lower precedence of all the functions below all the other arithmetic operators
 *	    and just above the comparison / relational operators.
 *	09-Mar-2021 (rlwhitcomb)
 *	    Alternate argument lists for "FRAC".
 *	23-Mar-2021 (rlwhitcomb)
 *	    Add "upper" and "lower" functions (for strings).
 *	23-Mar-2021 (rlwhitcomb)
 *	    Add Roman Numeral constant support, and the ROMAN function.
 *	24-Mar-2021 (rlwhitcomb)
 *	    Add fourth root ("fort").
 *	24-Mar-2021 (rlwhitcomb)
 *	    Add two- and three-consecutive equals sign Unicode chars.
 *	24-Mar-2021 (rlwhitcomb)
 *	    One more Unicode "identical to" symbol.
 *	25-Mar-2021 (rlwhitcomb)
 *	    Add the "fill" function for arrays and strings.
 *	27-Mar-2021 (rlwhitcomb)
 *	    Add "epow" function.
 *	28-Mar-2021 (rlwhitcomb)
 *	    Allow precision on all format arguments.
 *	29-Mar-2021 (rlwhitcomb)
 *	    Add Unicode equivalents for SUMOF and PRODUCTOF.
 *	06-Apr-2021 (rlwhitcomb)
 *	    Add "INDEX" and "SUBSTR" functions.
 *	07-Apr-2021 (rlwhitcomb)
 *	    Add "EXEC" function.
 *	07-Apr-2021 (rlwhitcomb)
 *	    Add "SPLIT" function (same semantics as String.split).
 *	07-Apr-2021 (rlwhitcomb)
 *	    Implement "TRIM", "LTRIM", and "RTRIM" functions.
 *	    Add superscript powers and subscript indexes.
 *	08-Apr-2021 (rlwhitcomb)
 *	    Add time constants and time/duration formatting.
 *	20-Apr-2021 (rlwhitcomb)
 *	    Simplify objVar. Add formal and actual params for functions.
 *	20-Apr-2021 (rlwhitcomb)
 *	    Add ":variables" directive.
 *	21-Apr-2021 (rlwhitcomb)
 *	    Add syntax for "case" statement. Massive renaming of lexical tokens.
 *	21-Apr-2021 (rlwhitcomb)
 *	    Add EOF to main rule to only allow fully valid input.
 *	22-Apr-2021 (rlwhitcomb)
 *	    Allow continuation lines and a completely empty program.
 *	    Revamp the way we do ENDEXPR.
 *	26-Apr-2021 (rlwhitcomb)
 *	    Fix FORMAT syntax.
 *	28-Apr-2021 (rlwhitcomb)
 *	    More Unicode math symbols.
 *	13-May-2021 (rlwhitcomb)
 *	    Date constants (ISO-8601 format).
 *	17-May-2021 (rlwhitcomb)
 *	    Allow for negative date inputs.
 *	21-May-2021 (rlwhitcomb)
 *	    Allow d'...' for ISO dates and D'...' for US dates (MM/dd/yyyy).
 *	    Add "TODAY" constant.
 *	21-May-2021 (rlwhitcomb)
 *	    Add "DOW" (day of week) function and "NOW" constant.
 *	02-Jul-2021 (rlwhitcomb)
 *	    New directive to turn on thousands separators always (without
 *	    the format specified). New aliases for some directives.
 *	12-Jul-2021 (rlwhitcomb)
 *	    New directive to ignore case of variable/member names.
 *	27-Jul-2021 (rlwhitcomb)
 *	    Allow negative years in US format in the "proper" place.
 *	28-Jul-2021 (rlwhitcomb)
 *	    New fraction constants (f'...') - accepts the same string format
 *	    as "FRAC" function.
 *	04-Aug-2021 (rlwhitcomb)
 *	    Support "yes" and "no" for mode options.
 *	06-Aug-2021 (rlwhitcomb)
 *	    Finish work moving fraction constant parsing to BigFraction.
 *	09-Aug-2021 (rlwhitcomb)
 *	    Trying to get 1..10 to work as loop control.
 *	09-Aug-2021 (rlwhitcomb)
 *	    Allowing a dot range on "length", "sumof", and "productof".
 *	11-Aug-2021 (rlwhitcomb)
 *	    Add integer divide ("\") operator.
 *	11-Aug-2021 (rlwhitcomb)
 *	    Add "CHARS" function to break up a string into codepoints.
 *	12-Aug-2021 (rlwhitcomb)
 *	    More date functions.
 *	16-Aug-2021 (rlwhitcomb)
 *	    Add ":OPEN" as an alternative to ":INCLUDE".
 */

grammar Calc;

prog
   : stmt* EOF
   ;

stmt
   : stmtOrExpr
   | defineStmt
   | directive (EOL | ENDEXPR)
   ;

stmtOrExpr
   : formattedExprs
   | loopStmt
   | whileStmt
   | ifStmt
   | caseStmt
   | emptyStmt
   ;

formattedExprs
   : exprStmt
   | exprStmt (EOL | ENDEXPR) stmtOrExpr
   ;

exprStmt
   : expr FORMAT ?
   ;

defineStmt
   : K_DEFINE ID formalParams ? '=' stmtBlock
   ;

loopStmt
   : K_LOOP ( LOCALVAR K_IN ) ? loopCtl stmtBlock
   ;

whileStmt
   : K_WHILE expr stmtBlock
   ;

ifStmt
   : K_IF expr stmtBlock ( EOL? K_ELSE stmtBlock ) ?
   ;

caseStmt
   : K_CASE expr K_OF '{' caseBlock ( ',' caseBlock ) * '}'
   | K_CASE expr K_OF caseBlock ( ',' caseBlock ) *
   ;

stmtBlock
   : EOL? '{' EOL? stmtOrExpr * '}' EOL?
   | EOL? stmtOrExpr
   ;

caseBlock
   : EOL? ( exprList | K_DEFAULT ) ':' stmtBlock
   ;

emptyStmt
   : EOL
   | ENDEXPR
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
   |<assoc=right> expr POW_OP expr       # powerExpr
   |<assoc=right> expr POWERS            # powerNExpr
   | expr MULT_OP expr                   # multiplyExpr
   | expr ADD_OP expr                    # addExpr
   | expr SHIFT_OP expr                  # shiftExpr
   | K_ABS expr                          # absExpr
   | K_SIN expr                          # sinExpr
   | K_COS expr                          # cosExpr
   | K_TAN expr                          # tanExpr
   | K_ASIN expr                         # asinExpr
   | K_ACOS expr                         # acosExpr
   | K_ATAN expr                         # atanExpr
   | K_ATAN2 expr2                       # atan2Expr
   | K_SINH expr                         # sinhExpr
   | K_COSH expr                         # coshExpr
   | K_TANH expr                         # tanhExpr
   | K_SQRT expr                         # sqrtExpr
   | K_CBRT expr                         # cbrtExpr
   | K_FORT expr                         # fortExpr
   | K_LOG expr                          # logExpr
   | K_LN2 expr                          # ln2Expr
   | K_LN expr                           # lnExpr
   | K_EPOW expr                         # ePowerExpr
   | K_SIGNUM expr                       # signumExpr
   | K_LENGTH ( expr | dotRange )        # lengthExpr
   | K_SCALE expr                        # scaleExpr
   | K_ROUND expr2                       # roundExpr
   | K_ISPRIME expr                      # isPrimeExpr
   | K_GCD expr2                         # gcdExpr
   | K_LCM expr2                         # lcmExpr
   | K_MAX exprN                         # maxExpr
   | K_MIN exprN                         # minExpr
   | K_SUMOF ( exprN | dotRange )        # sumOfExpr
   | K_PRODUCTOF ( exprN | dotRange )    # productOfExpr
   | K_JOIN exprN                        # joinExpr
   | K_SPLIT ( expr2 | expr3 )           # splitExpr
   | K_INDEX ( expr2 | expr3 )           # indexExpr
   | K_SUBSTR ( expr2 | expr3 )          # substrExpr
   | K_FILL var ',' ( expr2 | expr3 )    # fillExpr
   | (K_TRIM|K_LTRIM|K_RTRIM) expr       # trimExpr
   | K_FIB expr                          # fibExpr
   | K_BN expr                           # bernExpr
   | K_FRAC ( STRING | ISTRING | expr2 | expr3 ) # fracExpr
   | K_ROMAN expr                        # romanExpr
   | ( K_UPPER | K_LOWER ) expr          # caseConvertExpr
   | K_FACTORS expr                      # factorsExpr
   | K_PFACTORS expr                     # primeFactorsExpr
   | K_CHARS expr                        # charsExpr
   | K_DOW expr                          # dayOfWeekExpr
   | K_DOM expr                          # dayOfMonthExpr
   | K_DOY expr                          # dayOfYearExpr
   | K_MOY expr                          # monthOfYearExpr
   | K_YOD expr                          # yearOfDateExpr
   | K_EVAL expr                         # evalExpr
   | K_EXEC exprN                        # execExpr
   | expr '<=>' expr                     # spaceshipExpr
   | expr COMPARE_OP expr                # compareExpr
   | expr EQUAL_OP expr                  # equalExpr
   | expr BIT_OP expr                    # bitExpr
   | expr BOOL_OP expr                   # booleanExpr
   |<assoc=right> expr '?' expr ':' expr # eitherOrExpr
   |<assoc=right> var ASSIGN expr        # assignExpr
   |<assoc=right> var POW_ASSIGN expr    # powerAssignExpr
   |<assoc=right> var MULT_ASSIGN expr   # multAssignExpr
   |<assoc=right> var ADD_ASSIGN expr    # addAssignExpr
   |<assoc=right> var SHIFT_ASSIGN expr  # shiftAssignExpr
   |<assoc=right> var BIT_ASSIGN expr    # bitAssignExpr
   ;

expr2
   : '(' expr ',' expr ')'
   | expr ',' expr
   ;

expr3
   : '(' expr ',' expr ',' expr ')'
   | expr ',' expr ',' expr
   ;

exprN
   : '(' exprList ')'
   | exprList
   ;

dotRange
   : ( expr DOTS ) ? expr ( ',' expr ) ?
   ;

loopCtl
   : dotRange
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
   : var ( DOT ( var | STRING ) )       # objVar
   | var ( '[' expr ']' | INDEXES )     # arrVar
   | var actualParams                   # functionVar
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
   | ( K_TRUE | K_FALSE )        # booleanValue
   | K_NULL                      # nullValue
   | PI_CONST                    # piValue
   | E_CONST                     # eValue
   | FRAC_CONST                  # fracValue
   | ROMAN_CONST                 # romanValue
   | TIME_CONST                  # timeValue
   | DATE_CONST                  # dateValue
   | K_TODAY                     # todayValue
   | K_NOW                       # nowValue
   ;

formalParams
   : '(' LOCALVAR ( '=' expr ) ? ( ',' LOCALVAR ( '=' expr ) ? ) * ')'
   | '(' ')'
   ;

actualParams
   : '(' expr ? ( ',' expr ? ) * ')'
   ;

directive
   : ( D_DECIMAL | D_PRECISION ) numberOption # decimalDirective
   | D_DEFAULT                                # defaultDirective
   | D_DOUBLE                                 # doubleDirective
   | D_FLOAT                                  # floatDirective
   | D_UNLIMITED                              # unlimitedDirective
   | D_DEGREES                                # degreesDirective
   | D_RADIANS                                # radiansDirective
   | D_BINARY                                 # binaryDirective
   | D_SI                                     # siDirective
   | D_MIXED                                  # mixedDirective
   | D_CLEAR idList ?                         # clearDirective
   | D_ECHO expr ?                            # echoDirective
   | D_INCLUDE expr                           # includeDirective
   | D_TIMING modeOption                      # timingDirective
   | D_RATIONAL modeOption                    # rationalDirective
   | D_DEBUG modeOption                       # debugDirective
   | D_RESULTSONLY modeOption                 # resultsOnlyDirective
   | D_QUIET modeOption                       # quietDirective
   | D_VARIABLES idList ?                     # variablesDirective
   | D_SEPARATORS modeOption                  # separatorsDirective
   | D_IGNORECASE modeOption                  # ignoreCaseDirective
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
   : K_TRUE
   | K_FALSE
   | 'on'
   | 'off'
   | 'yes'
   | 'no'
   | 'pop'
   | 'previous'
   | 'prev'
   | var
   ;


/* Lexer rules start here */

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
         : FRACTIONS
         | F '\'' '-' ? ( ( INT ( FS ? '-' ? ( INT | ( INT FS ? '-' ? INT ) | FRACTIONS ) ) ? ) | FRACTIONS ) '\''
         ;

ROMAN_CONST
         : R '\'' [IiVvXxLlCcDdMm\u2160-\u2182] + '\''
         ;

TIME_CONST
         : H '\'' '-' ? DIG ? DIG ( ':' DIG DIG ( ':' DIG DIG ( DOT DIG+ ) ? ) ? ) ? ( [ \t] * ( A | A M | P | P M ) ) ? '\''
         | T '\'' '-' ? DIG + ( DOT DIG * ) ? [ \t] * ( W | D | H | M | S ) '\''
         ;

DATE_CONST
/* ISO-8601 format with more separators allowed */
         : 'd' '\'' '-' ? ( DIG DIG | DIG DIG DIG DIG ) DTSEP DIG ? DIG DTSEP DIG ? DIG '\''
         | 'd' '\'' '-' ? DIG DIG DIG DIG DIG DIG DIG DIG '\''
         | 'd' '\'' '-' ? DIG DIG DIG DIG DIG DIG '\''
/* US format (MM/dd/yyyy or MM/dd/yy or MMddyyyy or MMddyy) */
         | 'D' '\'' '-' ? DIG ? DIG DTSEP DIG ? DIG DTSEP '-' ? ( DIG DIG | DIG DIG DIG DIG ) '\''
         | 'D' '\'' '-' ? DIG DIG DIG DIG DIG DIG DIG DIG '\''
         | 'D' '\'' '-' ? DIG DIG DIG DIG DIG DIG '\''
         ;


K_TRUE     : T R U E ;

K_FALSE    : F A L S E ;

K_NULL     : N U L L ;

K_TODAY    : T O D A Y ;

K_NOW      : N O W ;

K_ABS      : A B S ;

K_SINH     : S I N H ;

K_SIN      : S I N ;

K_COSH     : C O S H ;

K_COS      : C O S ;

K_TANH     : T A N H ;

K_TAN      : T A N ;

K_ASIN     : A S I N ;

K_ACOS     : A C O S ;

K_ATAN2    : A T A N '2' ;

K_ATAN     : A T A N ;

K_SQRT     : ( S Q R T | '\u221A' ) ;

K_CBRT     : ( C B R T | '\u221B' ) ;

K_FORT     : ( F O R T | '\u221C' ) ;

K_LOG      : L O G ;

K_LN       : L N ;

K_LN2      : L N '2' ;

K_EPOW     : E P O W ;

K_SIGNUM   : S I G N U M ;

K_LENGTH   : L E N G T H ;

K_SCALE    : S C A L E ;

K_ROUND    : R O U N D ;

K_ISPRIME  : I S P R I M E ;

K_GCD      : G C D ;

K_LCM      : L C M ;

K_MAX      : M A X ;

K_MIN      : M I N ;

K_JOIN     : J O I N ;

K_SPLIT    : S P L I T ;

K_INDEX    : I N D E X ;

K_SUBSTR   : S U B S T R ;

K_FILL     : F I L L ;

K_TRIM     : T R I M ;

K_LTRIM    : L T R I M ;

K_RTRIM    : R T R I M ;

K_FIB      : F I B ;

K_BN       : B N ;

K_FRAC     : F R A C ;

K_ROMAN    : R O M A N ;

K_UPPER    : U P P E R ;

K_LOWER    : L O W E R ;

K_FACTORS  : F A C T O R S ;

K_PFACTORS : P F A C T O R S ;

K_CHARS    : C H A R S ;

K_DOW      : D O W ;

K_DOM      : D O M ;

K_DOY      : D O Y ;

K_MOY      : M O Y ;

K_YOD      : Y O D ;

K_EVAL     : E V A L ;

K_EXEC     : E X E C ;

K_SUMOF    : ( S U M O F | '\u2211' ) ;

K_PRODUCTOF: ( P R O D U C T O F | '\u220F' ) ;

K_LOOP     : L O O P ;

K_WHILE    : W H I L E ;

K_IN       : I N ;

K_IF       : I F ;

K_ELSE     : E L S E ;

K_DEFINE   : ( D E F | D E F I N E ) ;

K_CASE     : C A S E ;

K_OF       : O F ;

K_DEFAULT  : D E F A U L T ;


/* Note: this needs to be last so that these other "ID" like things
 * will be recognized first. */

ID     : [a-zA-Z_] [a-zA-Z_0-9]* ;


DOTS
       : '..'
       | '\u2026'
       ;

DOT
       : '.'
       ;

LOCALVAR
       : '$' [a-zA-Z_] [a-zA-Z_0-9]* ;

INC_OP
       : ( '++' | '\u2795\u2795' )
       | ( '--' | '\u2212\u2212' | '\u2796\u2796' )
       ;

ADD_OP
       : ( '+' | '\u2795' )
       | ( '-' | '\u2212' | '\u2796' )
       ;

POWERS
       : '\u2070' // 0
       | '\u00B9' // 1
       | '\u00B2' // 2
       | '\u00B3' // 3
       | '\u2074' // 4
       | '\u2075' // 5
       | '\u2076' // 6
       | '\u2077' // 7
       | '\u2078' // 8
       | '\u2079' // 9
       ;

INDEXES
       : [\u2080-\u2089]
       ;

POW_OP
       : ( '**' | '\u00D7\u00D7' | '\u2217\u2217' | '\u2715\u2715' | '\u2716\u2716' )
       ;

MULT_OP
       : ( '*' | '\u00D7' | '\u2217' | '\u2715' | '\u2716' )
       | ( '/' | '\u00F7' | '\u2215' | '\u2797' )
       | '\\'
       | '%'
       ;

POW_ASSIGN
       : ( '**=' | '\u00D7\u00D7=' | '\u2217\u2217=' | '\u2715\u2715=' | '\u2716\u2716=' )
       ;

MULT_ASSIGN
       : ( '*=' | '\u00D7=' | '\u2217=' | '\u2715=' | '\u2716=' )
       | ( '/=' | '\u00F7=' | '\u2215=' | '\u2797=' )
       | '\\='
       | '%='
       ;

ADD_ASSIGN
       : ( '+=' | '\u2795=' )
       | ( '-=' | '\u2212=' | '\u2796=' )
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
       | '\u2A76'
       | '\u2261'
       | '!=='
       | '\u2262'
       | '=='
       | '\u2A75'
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


D_DECIMAL
   : DIR  ( D E C | D E C I M A L )
   ;

D_PRECISION
   : DIR  ( P R E C | P R E C I S I O N )
   ;

D_DEFAULT
   : DIR  ( D E F | D E F A U L T )
   ;

D_DOUBLE
   : DIR  ( D B L | D O U B L E )
   ;

D_FLOAT
   : DIR  ( F L T | F L O A T )
   ;

D_UNLIMITED
   : DIR  ( U N L | U N L I M I T E D )
   ;

D_DEGREES
   : DIR  ( D E G | D E G R E E S )
   ;

D_RADIANS
   : DIR  ( R A D | R A D I A N S )
   ;

D_BINARY
   : DIR  ( B I N | B I N A R Y )
   ;

D_SI
   : DIR  ( S I | T E N )
   ;

D_MIXED
   : DIR  ( M I X | M I X E D )
   ;

D_TIMING
   : DIR  ( T I M E | T I M I N G )
   ;

D_RATIONAL
   : DIR  ( F R | F R A C | R A T I O N | R A T I O N A L | F R A C T I O N )
   ;

D_CLEAR
   : DIR  ( C L R | C L E A R )
   ;

D_ECHO
   : DIR  E C H O
   ;

D_INCLUDE
   : DIR  ( I N C | I N C L U D E | O P E N )
   ;

D_DEBUG
   : DIR  ( D E B | D B G | D E B U G )
   ;

D_RESULTSONLY
   : DIR  ( R E S | R E S U L T | R E S U L T S | R E S U L T O N L Y | R E S U L T S O N L Y )
   ;

D_QUIET
   : DIR  Q U I E T
   ;

D_VARIABLES
   : DIR ( V A R | V A R S | V A R I A B L E | V A R I A B L E S )
   ;

D_SEPARATORS
   : DIR ( S E P | S E P S | S E P A R A T O R | S E P A R A T O R S )
   ;

D_IGNORECASE
   : DIR ( I N S | I G N | C A S E | I N S E N S I T I V E | C A S E I N S E N S I T I V E | I G N O R E | I G N O R E C A S E )
   ;


FORMAT
   : '@' ( '-' ? INT ) ? [a-zA-Z,] ? [a-zA-Z%]
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

fragment FRACTIONS
   : ( '\u00BC' | '\u00BD' | '\u00BE' )             /* 1/4, 1/2, 3/4      */
   | ( '\u2150' | '\u2151' | '\u2152' )             /* 1/7, 1/9, 1/10     */
   | ( '\u2189' | '\u2153' | '\u2154' )             /* 0/3, 1/3, 2/3      */
   | ( '\u2155' | '\u2156' | '\u2157' | '\u2158' )  /* 1/5, 2/5, 3/5, 4/5 */
   | ( '\u2159' | '\u215A' )                        /* 1/6, 5/6           */
   | ( '\u215B' | '\u215C' | '\u215D' | '\u215E' )  /* 1/8, 3/8, 5/8, 7/8 */
   ;

fragment FS
   : [ \t] * ( ',' | ';' | '/' | [ \t] + ) [ \t] *
   ;

NUMBER
   : INT ('.' [0-9] +)? EXP?
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

fragment DTSEP : [\-/,;._] ;

fragment DIG : [0-9] ;

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

fragment NL : '\r'? '\n' ;

WS
   : [ \t] + -> skip
   ;

COMMENT
   : '/*' .*? '*/' -> skip
   ;

LINE_COMMENT
   : ( '#' | '//' ) .*? NL -> skip
   ;

LINE_ESCAPE
   : '\\' NL -> skip
   ;

EOL
   : NL
   ;

ENDEXPR
   : ';'
   ;

