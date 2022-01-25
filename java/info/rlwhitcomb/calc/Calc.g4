/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2022 Roger L. Whitcomb.
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
 *	16-Aug-2021 (rlwhitcomb)
 *	    Got the precedence of all the functions wrong; adjust.
 *	25-Aug-2021 (rlwhitcomb)
 *	    Redo the syntax for a couple functions to get the optional
 *	    parens correct.
 *	25-Aug-2021 (rlwhitcomb)
 *	    Add global variables.
 *	26-Aug-2021 (rlwhitcomb)
 *	    Add some more Unicode number symbols. Add "isnull" function.
 *	07-Sep-2021 (rlwhitcomb)
 *	    Make ":library", ":lib", ":libs", and ":libraries" synonyms for
 *	    ":include" (because the command line option is "-library").
 *	08-Sep-2021 (rlwhitcomb)
 *	    Allow ISTRING as a member name for objects!
 *	20-Sep-2021 (rlwhitcomb)
 *	    Slightly redefine formalParams to make it easier to implement.
 *	    Allow "\$" in interpolated strings, to make currency constructs
 *	    easier to read.
 *	    Add "tenpow" function.
 *	26-Sep-2021 (rlwhitcomb)
 *	    Oops! The "\$" experiment doesn't work well.
 *	02-Oct-2021 (rlwhitcomb)
 *	    Add charset to ":include", add ":save".
 *	14-Oct-2021 (rlwhitcomb)
 *	    Add mode words as possible IDs. Add ":load" as alias for ":include".
 *	15-Oct-2021 (rlwhitcomb)
 *	    #32: Fix arg parsing for all one-arg predefined functions.
 *	    New "slice" and "splice" functions (as per JavaScript).
 *	16-Oct-2021 (rlwhitcomb)
 *	    "sort" function.
 *	19-Oct-2021 (rlwhitcomb)
 *	    #35: "replace" function for strings.
 *	    #34: Update arguments to "splice" to work with objects.
 *	20-Oct-2021 (rlwhitcomb)
 *	    #37: Currency format.
 *	23-Oct-2021 (rlwhitcomb)
 *	    #42: Add encode/decode functions.
 *	25-Oct-2021 (rlwhitcomb)
 *	    #46: Add "versioninfo" predefined structure.
 *	26-Oct-2021 (rlwhitcomb)
 *	    #31: Add octal and binary escape forms in strings.
 *	27-Oct-2021 (rlwhitcomb)
 *	    #45: Add "read" function.
 *	    Adjust precedence such that expr3 is preferred over expr2 over expr1
 *	    (another attempt to get function parameters correct).
 *	28-Oct-2021 (rlwhitcomb)
 *	    Needed another possible EOL at end of 2nd type of stmtBlock. Revise
 *	    CASE expression list syntax to make "default" work better.
 *	    Remove the predefined constants, in favor of predefined variables / values.
 *	    Allow BMP letters (plus high PI values) in identifiers.
 *	02-Nov-2021 (rlwhitcomb)
 *	    #60: Fix order of declarations for "fillArgs" to get the 3-arg version correct.
 *	    #57: Add "+" to format prefix options.
 *	03-Nov-2021 (rlwhitcomb)
 *	    #69: Introduce "$*" and "$#" global variables.
 *	07-Nov-2021 (rlwhitcomb)
 *	    #67: Allow multi-line array and object declarations.
 *	    #69: Allow dots for function declarations for variable parameter lists.
 *	09-Nov-2021 (rlwhitcomb)
 *	    Allow "over" as a synonym for "in" in loop, as in "loop $i over 0..9"; allow
 *	    "in" or "over" without loop variable; also allow "in" for "case" statements.
 *	11-Nov-2021 (rlwhitcomb)
 *	    #81: Add directive to not quote strings on output.
 *	18-Nov-2021 (rlwhitcomb)
 *	    #83: Add Unicode symbols for "in" and "empty set".
 *	27-Nov-2021 (rlwhitcomb)
 *	    #105: Allow directives inside loops and functions.
 *	30-Nov-2021 (rlwhitcomb)
 *	    #83: Another symbol for "in" (found in the issue).
 *	05-Dec-2021 (rlwhitcomb)
 *	    #106: Add "leave" statement.
 *	14-Dec-2021 (rlwhitcomb)
 *	    #142: Cosmetic cleanup.
 *	15-Dec-2021 (rlwhitcomb)
 *	    #151: Fix precedence of boolean operators.
 *	18-Dec-2021 (rlwhitcomb)
 *	    #159: Add directive to silence display of directives.
 *	28-Dec-2021 (rlwhitcomb)
 *	    #188: Add "ceil" and "floor" functions.
 *	    #137: Add "reverse" function.
 *	    #128: Add "lpad", "pad", and "rpad" functions.
 *	    Refactor "trim" and "pad" lexical tokens.
 *	31-Dec-2021 (rlwhitcomb)
 *	    Allow "fill" to only have one or two arguments.
 *	    #180: Allow fractional precision on FORMAT.
 *	05-Jan-2022 (rlwhitcomb)
 *	    #104: Add "dec()" function to convert from fraction or string, etc.
 *	09-Jan-2022 (rlwhitcomb)
 *	    #200: Redo the escape sequences for strings (to match CharUtil.quoteControl and
 *	    convertEscapeSequences).
 *	10-Jan-2022 (rlwhitcomb)
 *	    #108: Allow the Unicode null character as an identifier start (for the alias to "null").
 *	17-Jan-2022 (rlwhitcomb)
 *	    Allow local variable names to have a digit right after the '$' (as long as there is one
 *	    other name start character, so "$2n" is valid, for instance.
 *	18-Jan-2022 (rlwhitcomb)
 *	    #211: Add "typeof"; rearrange the function list a little bit.
 *	19-Jan-2022 (rlwhitcomb)
 *	    #214: Add "cast" operator.
 *	20-Jan-2022 (rlwhitcomb)
 *	    #215: Tweak the pattern for FORMAT.
 *	21-Jan-2022 (rlwhitcomb)
 *	    #135: Add "const" values.
 *	22-Jan-2022 (rlwhitcomb)
 *	    #160: Restrict function names, directives, and statement keywords to
 *		  3 variants: all lower, all upper, and a canonical mixed-case form
 *		  (usually "Exp" with Title case), except some like "sinh" have one more: "SinH".
 *	    #216: Add "format" function.
 *	24-Jan-2022 (rlwhitcomb)
 *	    #103: Add "complex" object, and "COMPLEX" function (similar to "FRAC").
 *	    #79: Implement "random" function. Complex value cannot be empty.
 */

grammar Calc;

prog
   : stmt* EOF
   ;

stmt
   : stmtOrExpr
   | defineStmt
   | constStmt
   ;

stmtOrExpr
   : formattedExprs
   | loopStmt
   | whileStmt
   | ifStmt
   | caseStmt
   | leaveStmt
   | emptyStmt
   ;

formattedExprs
   : exprStmt
   | exprStmt (EOL | ENDEXPR) stmtOrExpr
   | directive
   | directive (EOL | ENDEXPR) stmtOrExpr
   ;

exprStmt
   : expr FORMAT ?
   ;

defineStmt
   : K_DEFINE id formalParamList ? '=' stmtBlock
   ;

constStmt
   : K_CONST id '=' expr
   ;

loopStmt
   : K_LOOP ( LOCALVAR ? ( K_IN | K_OVER | SET_IN ) ) ? loopCtl stmtBlock
   ;

whileStmt
   : K_WHILE expr stmtBlock
   ;

ifStmt
   : K_IF expr stmtBlock ( EOL? K_ELSE stmtBlock ) ?
   ;

caseStmt
   : K_CASE expr ( K_OF | K_IN | SET_IN ) '{' caseBlock ( ',' caseBlock ) * '}'
   | K_CASE expr ( K_OF | K_IN | SET_IN ) caseBlock ( ',' caseBlock ) *
   ;

leaveStmt
   : K_LEAVE expr1 ?
   ;

stmtBlock
   : EOL? '{' EOL? stmtOrExpr * '}' EOL?
   | EOL? stmtOrExpr EOL?
   ;

caseBlock
   : EOL? caseExprList ':' stmtBlock
   ;

emptyStmt
   : EOL
   | ENDEXPR
   ;

expr
   : value                               # valueExpr
   | obj                                 # objExpr
   | arr                                 # arrExpr
   | complex                             # complexValueExpr
   | var                                 # varExpr
   | '(' expr ')'                        # parenExpr
   | K_ABS expr1                         # absExpr
   | K_SIN expr1                         # sinExpr
   | K_COS expr1                         # cosExpr
   | K_TAN expr1                         # tanExpr
   | K_ASIN expr1                        # asinExpr
   | K_ACOS expr1                        # acosExpr
   | K_ATAN expr1                        # atanExpr
   | K_ATAN2 expr2                       # atan2Expr
   | K_SINH expr1                        # sinhExpr
   | K_COSH expr1                        # coshExpr
   | K_TANH expr1                        # tanhExpr
   | K_SQRT expr1                        # sqrtExpr
   | K_CBRT expr1                        # cbrtExpr
   | K_FORT expr1                        # fortExpr
   | K_LOG expr1                         # logExpr
   | K_LN2 expr1                         # ln2Expr
   | K_LN expr1                          # lnExpr
   | K_EPOW expr1                        # ePowerExpr
   | K_TENPOW expr1                      # tenPowerExpr
   | K_RANDOM expr1 ?                    # randomExpr
   | K_SIGNUM expr1                      # signumExpr
   | K_ISNULL expr1                      # isNullExpr
   | K_TYPEOF expr1                      # typeofExpr
   | K_CAST ( expr2 | expr1 )            # castExpr
   | K_LENGTH ( expr1 | dotRange )       # lengthExpr
   | K_SCALE expr1                       # scaleExpr
   | K_ROUND expr2                       # roundExpr
   | K_CEIL expr1                        # ceilExpr
   | K_FLOOR expr1                       # floorExpr
   | K_ISPRIME expr1                     # isPrimeExpr
   | K_GCD expr2                         # gcdExpr
   | K_LCM expr2                         # lcmExpr
   | K_MAX exprN                         # maxExpr
   | K_MIN exprN                         # minExpr
   | K_SUMOF ( exprN | dotRange )        # sumOfExpr
   | K_PRODUCTOF ( exprN | dotRange )    # productOfExpr
   | K_JOIN exprN                        # joinExpr
   | K_SPLIT ( expr3 | expr2 )           # splitExpr
   | K_INDEX ( expr3 | expr2 )           # indexExpr
   | K_SUBSTR ( expr3 | expr2 | expr1 )  # substrExpr
   | K_REPLACE replaceArgs               # replaceExpr
   | K_SLICE ( expr3 | expr2 | expr1 )   # sliceExpr
   | K_SPLICE spliceArgs                 # spliceExpr
   | K_FILL fillArgs                     # fillExpr
   | K_FORMAT exprN                      # formatExpr
   | K_SORT ( expr2 | expr1 )            # sortExpr
   | K_REVERSE expr1                     # reverseExpr
   | K_TRIM expr1                        # trimExpr
   | K_PAD padArgs                       # padExpr
   | K_FIB expr1                         # fibExpr
   | K_BN expr1                          # bernExpr
   | K_DEC expr1                         # decExpr
   | K_FRAC ( expr3 | expr2 | expr1 )    # fracExpr
   | K_COMPLEX ( expr2 | expr1 )         # complexFuncExpr
   | K_ROMAN expr1                       # romanExpr
   | ( K_UPPER | K_LOWER ) expr1         # caseConvertExpr
   | K_FACTORS expr1                     # factorsExpr
   | K_PFACTORS expr1                    # primeFactorsExpr
   | K_CHARS expr1                       # charsExpr
   | K_DOW expr1                         # dayOfWeekExpr
   | K_DOM expr1                         # dayOfMonthExpr
   | K_DOY expr1                         # dayOfYearExpr
   | K_MOY expr1                         # monthOfYearExpr
   | K_YOD expr1                         # yearOfDateExpr
   | K_EVAL expr1                        # evalExpr
   | K_EXEC exprN                        # execExpr
   | K_DECODE expr1                      # decodeExpr
   | K_ENCODE expr1                      # encodeExpr
   | K_READ ( expr2 | expr1 )            # readExpr
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
   | expr '<=>' expr                     # spaceshipExpr
   | expr COMPARE_OP expr                # compareExpr
   | expr EQUAL_OP expr                  # equalExpr
   | expr BIT_OP expr                    # bitExpr
   | expr BOOL_AND_OP expr               # booleanAndExpr
   | expr BOOL_OR_OP expr                # booleanOrExpr
   | expr BOOL_XOR_OP expr               # booleanXorExpr
   |<assoc=right> expr '?' expr ':' expr # eitherOrExpr
   |<assoc=right> var ASSIGN expr        # assignExpr
   |<assoc=right> var POW_ASSIGN expr    # powerAssignExpr
   |<assoc=right> var MULT_ASSIGN expr   # multAssignExpr
   |<assoc=right> var ADD_ASSIGN expr    # addAssignExpr
   |<assoc=right> var SHIFT_ASSIGN expr  # shiftAssignExpr
   |<assoc=right> var BIT_ASSIGN expr    # bitAssignExpr
   ;

expr1
   : '(' expr ')'
   | expr
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

replaceArgs
   : '(' expr ',' expr ',' expr ( ',' replaceOption ) ? ')'
   | expr ',' expr ',' expr ( ',' replaceOption ) ?
   ;

spliceArgs
   : '(' expr ',' dropObjs ',' obj ')'
   | '(' expr ',' dropObjs ')'
   | '(' expr ',' obj ')'
   | expr ',' dropObjs ',' obj
   | expr ',' dropObjs
   | expr ',' obj
   | exprN
   | expr3
   | expr2
   | expr1
   ;

fillArgs
   : '(' var ',' expr ',' expr ',' expr ')'
   | '(' var ',' expr ',' expr ')'
   | '(' var ',' expr ')'
   | '(' var ')'
   | var ',' expr ',' expr ',' expr
   | var ',' expr ',' expr
   | var ',' expr
   | var
   ;

padArgs
   : '(' var ',' expr ',' expr ')'
   | '(' var ',' expr ')'
   | var ',' expr ',' expr
   | var ',' expr
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
   : '[' EOL? exprList EOL? ']'
   | '[' EOL? ']'
   ;

exprList
   : expr ( ',' EOL? expr ) *
   ;

caseExprList
   : exprList ',' EOL? K_DEFAULT ',' EOL? exprList
   | K_DEFAULT ( ',' EOL? exprList ) ?
   | exprList ( ',' EOL? K_DEFAULT ) ?
   ;

obj
   : '{' EOL? pair ( ',' EOL? pair ) * EOL? '}'
   | '{' EOL? '}'
   | EMPTY_SET
   ;

pair
   : id ':' expr
   | STRING ':' expr
   | ISTRING ':' expr
   ;

complex
   : '(' expr ',' expr ')'
   ;

var
   : var ( DOT ( var | STRING | ISTRING ) ) # objVar
   | var ( '[' expr ']' | INDEXES )         # arrVar
   | var actualParams                       # functionVar
   | id                                     # idVar
   | LOCALVAR                               # localVar
   | GLOBALVAR                              # globalVar
   ;

value
   : STRING                      # stringValue
   | ISTRING                     # iStringValue
   | NUMBER                      # numberValue
   | NUM_CONST                   # numberConstValue
   | BIN_CONST                   # binaryValue
   | OCT_CONST                   # octalValue
   | HEX_CONST                   # hexValue
   | KB_CONST                    # kbValue
   | FRAC_CONST                  # fracValue
   | ROMAN_CONST                 # romanValue
   | TIME_CONST                  # timeValue
   | DATE_CONST                  # dateValue
   ;

formalParamList
   : '(' formalParam ( ',' formalParam ) * ( ',' DOTS ) ? ')'
   | '(' DOTS ')'
   | '(' ')'
   ;

formalParam
   : LOCALVAR ( '=' expr ) ?
   ;

actualParams
   : '(' expr ? ( ',' expr ? ) * ')'
   ;

dropObjs
   : '[' ( id | STRING | ISTRING ) ( ',' ( id | STRING | ISTRING ) ) * ']'
   | '[' ']'
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
   | D_INCLUDE expr ( ',' expr ) ?            # includeDirective
   | D_SAVE expr ( ',' expr ) ?               # saveDirective
   | D_TIMING modeOption                      # timingDirective
   | D_RATIONAL modeOption                    # rationalDirective
   | D_DEBUG modeOption                       # debugDirective
   | D_RESULTSONLY modeOption                 # resultsOnlyDirective
   | D_QUIET modeOption                       # quietDirective
   | D_SILENCE modeOption                     # silenceDirective
   | D_VARIABLES idList ?                     # variablesDirective
   | D_SEPARATORS modeOption                  # separatorsDirective
   | D_IGNORECASE modeOption                  # ignoreCaseDirective
   | D_QUOTESTRINGS modeOption                # quoteStringsDirective
   ;

numberOption
   : '(' NUMBER ')'
   | NUMBER
   | var
   ;

idList
   : '[' id ( ',' id ) * ']'
   | id ( ',' id ) *
   | '[' ']'
   ;

id
   : ID
   | MODES
   | REPLACE_MODES
   ;

modeOption
   : MODES
   | var
   ;

replaceOption
   : REPLACE_MODES
   | var
   ;

/* Lexer rules start here */

FRAC_CONST
         : FRACTIONS
         | [Ff] '\'' '-' ? ( ( INT ( FS ? '-' ? ( INT | ( INT FS ? '-' ? INT ) | FRACTIONS ) ) ? ) | FRACTIONS ) '\''
         ;

ROMAN_CONST
         : [Rr] '\'' [IiVvXxLlCcDdMm\u2160-\u2182] + '\''
         ;

MODES
         : 'true'  | 'on'       | 'yes'
         | 'TRUE'  | 'ON'       | 'YES'
         | 'True'  | 'On'       | 'Yes'
         | 'false' | 'off'      | 'no'
         | 'FALSE' | 'OFF'      | 'NO'
         | 'False' | 'Off'      | 'No'
         | 'pop'   | 'previous' | 'prev'
         | 'POP'   | 'PREVIOUS' | 'PREV'
         | 'Pop'   | 'Previous' | 'Prev'
         ;

REPLACE_MODES
         : 'all'   | 'ALL'   | 'All'
         | 'first' | 'FIRST' | 'First'
         | 'last'  | 'LAST'  | 'Last'
         ;

fragment AM_PM
         : [Aa] | 'am' | 'AM'
         | [Pp] | 'pm' | 'PM'
         ;

fragment DURATIONS
         : [Ww] | [Dd] | [Hh] | [Mm] | [Ss]
         ;

TIME_CONST
         : [Hh] '\'' '-' ? DIG ? DIG ( ':' DIG DIG ( ':' DIG DIG ( DOT DIG+ ) ? ) ? ) ? ( [ \t] * AM_PM ) ? '\''
         | [Tt] '\'' '-' ? DIG + ( DOT DIG * ) ? [ \t] * DURATIONS '\''
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


/*
 * Predefined function names
 */

K_ABS      : 'abs' | 'ABS' | 'Abs' ;

K_SINH     : 'sinh' | 'SINH' | 'Sinh' | 'SinH' ;

K_SIN      : 'sin' | 'SIN' | 'Sin' ;

K_COSH     : 'cosh' | 'COSH' | 'Cosh' | 'CosH' ;

K_COS      : 'cos' | 'COS' | 'Cos' ;

K_TANH     : 'tanh' | 'TANH' | 'Tanh' | 'TanH' ;

K_TAN      : 'tan' | 'TAN' | 'Tan' ;

K_ASIN     : 'asin' | 'ASIN' | 'Asin' | 'ASin' ;

K_ACOS     : 'acos' | 'ACOS' | 'Acos' | 'ACos' ;

K_ATAN2    : 'atan2' | 'ATAN2' | 'Atan2' | 'ATan2' ;

K_ATAN     : 'atan' | 'ATAN' | 'Atan' | 'ATan' ;

K_SQRT     : 'sqrt' | 'SQRT' | 'Sqrt' | '\u221A' ;

K_CBRT     : 'cbrt' | 'CBRT' | 'Cbrt' | '\u221B' ;

K_FORT     : 'fort' | 'FORT' | 'Fort' | '\u221C' ;

K_LOG      : 'log' | 'LOG' | 'Log' ;

K_LN       : 'ln' | 'LN' | 'Ln' ;

K_LN2      : 'ln2' | 'LN2' | 'Ln2' ;

K_EPOW     : 'epow' | 'EPOW' | 'Epow' | 'EPow' ;

K_TENPOW   : 'tenpow' | 'TENPOW' | 'Tenpow' | 'TenPow' ;

K_RANDOM   : 'random' | 'RANDOM' | 'Random'
           | 'rand'   | 'RAND'   | 'Rand'
           ;

K_SIGNUM   : 'signum' | 'SIGNUM' | 'Signum' | 'SigNum'
           | 'sgn' | 'SGN' | 'Sgn' | 'SgN'
           ;

K_LENGTH   : 'length' | 'LENGTH' | 'Length' ;

K_SCALE    : 'scale' | 'SCALE' | 'Scale' ;

K_ROUND    : 'round' | 'ROUND' | 'Round' ;

K_CEIL     : 'ceil' | 'CEIL' | 'Ceil' ;

K_FLOOR    : 'floor' | 'FLOOR' | 'Floor' ;

K_ISPRIME  : 'isprime' | 'ISPRIME' | 'Isprime' | 'IsPrime' ;

K_ISNULL   : 'isnull' | 'ISNULL' | 'Isnull' | 'IsNull' ;

K_TYPEOF   : 'typeof' | 'TYPEOF' | 'Typeof' | 'TypeOf' ;

K_CAST     : 'cast' | 'CAST' | 'Cast' ;

K_GCD      : 'gcd' | 'GCD' ;

K_LCM      : 'lcm' | 'LCM' ;

K_MAX      : 'max' | 'MAX' | 'Max' ;

K_MIN      : 'min' | 'MIN' | 'Min' ;

K_JOIN     : 'join' | 'JOIN' | 'Join' ;

K_SPLIT    : 'split' | 'SPLIT' | 'Split' ;

K_INDEX    : 'index' | 'INDEX' | 'Index' ;

K_SUBSTR   : 'substr' | 'SUBSTR' | 'Substr' | 'SubStr' ;

K_REPLACE  : 'replace' | 'REPLACE' | 'Replace' ;

K_SLICE    : 'slice' | 'SLICE' | 'Slice' ;

K_SPLICE   : 'splice' | 'SPLICE' | 'Splice' ;

K_FILL     : 'fill' | 'FILL' | 'Fill' ;

K_FORMAT   : 'format' | 'FORMAT' | 'Format' ;

K_SORT     : 'sort' | 'SORT' | 'Sort' ;

K_REVERSE  : 'reverse' | 'REVERSE' | 'Reverse' ;

K_TRIM     : 'trim'  | 'TRIM'  | 'Trim'
           | 'ltrim' | 'LTRIM' | 'Ltrim' | 'LTrim'
           | 'rtrim' | 'RTRIM' | 'Rtrim' | 'RTrim'
           ;

K_PAD      : 'pad'  | 'PAD'  | 'Pad'
           | 'lpad' | 'LPAD' | 'Lpad' | 'LPad'
           | 'rpad' | 'RPAD' | 'Rpad' | 'RPad'
           ;

K_FIB      : 'fib' | 'FIB' | 'Fib' ;

K_BN       : 'bn' | 'BN' | 'Bn' ;

K_DEC      : 'dec' | 'DEC' | 'Dec' ;

K_FRAC     : 'frac' | 'FRAC' | 'Frac' ;

K_COMPLEX  : 'complex' | 'COMPLEX' | 'Complex' ;

K_ROMAN    : 'roman' | 'ROMAN' | 'Roman' ;

K_UPPER    : 'upper' | 'UPPER' | 'Upper' ;

K_LOWER    : 'lower' | 'LOWER' | 'Lower' ;

K_FACTORS  : 'factors' | 'FACTORS' | 'Factors' ;

K_PFACTORS : 'pfactors' | 'PFACTORS' | 'Pfactors' | 'PFactors' ;

K_CHARS    : 'chars' | 'CHARS' | 'Chars' ;

K_DOW      : 'dow' | 'DOW' | 'DoW' ;

K_DOM      : 'dom' | 'DOM' | 'DoM' ;

K_DOY      : 'doy' | 'DOY' | 'DoY' ;

K_MOY      : 'moy' | 'MOY' | 'MoY' ;

K_YOD      : 'yod' | 'YOD' | 'YoD' ;

K_EVAL     : 'eval' | 'EVAL' | 'Eval' ;

K_EXEC     : 'exec' | 'EXEC' | 'Exec' ;

K_DECODE   : 'decode' | 'DECODE' | 'Decode' ;

K_ENCODE   : 'encode' | 'ENCODE' | 'Encode' ;

K_READ     : 'read' | 'READ' | 'Read' ;

K_SUMOF    : 'sumof' | 'SUMOF' | 'Sumof' | 'SumOf' | '\u2211' ;

K_PRODUCTOF: 'productof' | 'PRODUCTOF' | 'Productof' | 'ProductOf' | '\u220F' ;

/*
 * Statement keywords
 */

K_LOOP     : 'loop' | 'LOOP' | 'Loop' ;

K_WHILE    : 'while' | 'WHILE' | 'While' ;

K_IN       : 'in' | 'IN' | 'In' ;

K_OVER     : 'over' | 'OVER' | 'Over' ;

K_IF       : 'if' | 'IF' | 'If' ;

K_ELSE     : 'else' | 'ELSE' | 'Else' ;

K_DEFINE   : 'define' | 'DEFINE' | 'Define'
           | 'def' | 'DEF' | 'Def' ;

K_CONST    : 'constant' | 'CONSTANT' | 'Constant'
           | 'const' | 'CONST' | 'Const' ;

K_CASE     : 'case' | 'CASE' | 'Case' ;

K_OF       : 'of' | 'OF' | 'Of' ;

K_DEFAULT  : 'default' | 'DEFAULT' | 'Default' ;

K_LEAVE    : 'leave' | 'LEAVE' | 'Leave' ;


/* Note: this needs to be last so that these other "ID" like things
 * will be recognized first. */

ID     : NAME_START_CHAR NAME_CHAR *
       ;


DOTS
       : '...'
       | '..'
       | '\u2026'
       ;

DOT
       : '.'
       ;

LOCALVAR
       : '$' [0-9] ? NAME_START_CHAR NAME_CHAR *
       ;

GLOBALVAR
       : '$' INT
       | '$' '#'
       | '$' '*'
       ;

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
       : ( '===' | '\u2A76' | '\u2261' )
       | ( '!==' | '\u2262' )
       | ( '==' | '\u2A75' )
       | ( '!=' | '\u2260' )
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

BOOL_AND_OP
       : ( '&&' | '\u2227' )
       ;

BOOL_OR_OP
       : ( '||' | '\u2228' )
       ;

BOOL_XOR_OP
       : ( '^^' | '\u22BB' )
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

SET_IN
       : '\u2208'
       | '\u220A'
       ;

EMPTY_SET
       : '\u2205'
       | '\u29B0'
       ;


D_DECIMAL
   : DIR  ( 'decimal' | 'DECIMAL' | 'Decimal' )
   | DIR  ( 'dec'     | 'DEC'     | 'Dec'     )
   ;

D_PRECISION
   : DIR  ( 'precision' | 'PRECISION' | 'Precision' )
   | DIR  ( 'prec'      | 'PREC'      | 'Prec'      )
   ;

D_DEFAULT
   : DIR  ( 'default' | 'DEFAULT' | 'Default' )
   | DIR  ( 'def'     | 'DEF'     | 'Def'     )
   ;

D_DOUBLE
   : DIR  ( 'double' | 'DOUBLE' | 'Double' )
   | DIR  ( 'dbl'    | 'DBL'    | 'Dbl'    )
   ;

D_FLOAT
   : DIR  ( 'float' | 'FLOAT' | 'Float' )
   | DIR  ( 'flt'   | 'FLT'   | 'Flt'   )
   ;

D_UNLIMITED
   : DIR  ( 'unlimited' | 'UNLIMITED' | 'Unlimited' )
   | DIR  ( 'unl'       | 'UNL'       | 'Unl'       )
   ;

D_DEGREES
   : DIR  ( 'degrees' | 'DEGREES' | 'Degrees' )
   | DIR  ( 'deg'     | 'DEG'     | 'Deg'     )
   ;

D_RADIANS
   : DIR  ( 'radians' | 'RADIANS' | 'Radians' )
   | DIR  ( 'rad'     | 'RAD'     | 'Rad'     )
   ;

D_BINARY
   : DIR  ( 'binary' | 'BINARY' | 'Binary' )
   | DIR  ( 'bin'    | 'BIN'    | 'Bin'    )
   ;

D_SI
   : DIR  ( 'ten' | 'TEN' | 'Ten' )
   | DIR  ( 'si'  | 'SI'  )
   ;

D_MIXED
   : DIR  ( 'mixed' | 'MIXED' | 'Mixed' )
   | DIR  ( 'mix'   | 'MIX'   | 'Mix'   )
   ;

D_TIMING
   : DIR  ( 'timing' | 'TIMING' | 'Timing' )
   | DIR  ( 'time'   | 'TIME'   | 'Time'   )
   ;

D_RATIONAL
   : DIR  ( 'rational' | 'RATIONAL' | 'Rational' )
   | DIR  ( 'fraction' | 'FRACTION' | 'Fraction' )
   | DIR  ( 'ration'   | 'RATION'   | 'Ration'   )
   | DIR  ( 'frac'     | 'FRAC'     | 'Frac'     )
   | DIR  ( 'fr'       | 'FR'       | 'Fr'       )
   ;

D_CLEAR
   : DIR  ( 'clear' | 'CLEAR' | 'Clear' )
   | DIR  ( 'clr'   | 'CLR'   | 'Clr'   )
   ;

D_ECHO
   : DIR  ( 'echo' | 'ECHO' | 'Echo' )
   ;

D_INCLUDE
   : DIR  ( 'libraries' | 'LIBRARIES' | 'Libraries' )
   | DIR  ( 'library'   | 'LIBRARY'   | 'Library'   )
   | DIR  ( 'include'   | 'INCLUDE'   | 'Include'   )
   | DIR  ( 'load'      | 'LOAD'      | 'Load'      )
   | DIR  ( 'open'      | 'OPEN'      | 'Open'      )
   | DIR  ( 'libs'      | 'LIBS'      | 'Libs'      )
   | DIR  ( 'lib'       | 'LIB'       | 'Lib'       )
   | DIR  ( 'inc'       | 'INC'       | 'Inc'       )
   ;

D_SAVE
   : DIR  ( 'save' | 'SAVE' | 'Save' )
   ;

D_DEBUG
   : DIR  ( 'debug' | 'DEBUG' | 'Debug' )
   | DIR  ( 'deb'   | 'DEB'   | 'Deb'   )
   | DIR  ( 'dbg'   | 'DBG'   | 'Dbg'   )
   ;

D_RESULTSONLY
   : DIR  ( 'resultsonly' | 'RESULTSONLY' | 'Resultsonly' | 'ResultsOnly' )
   | DIR  ( 'resultonly'  | 'RESULTONLY'  | 'Resultonly'  | 'ResultOnly'  )
   | DIR  ( 'results'     | 'RESULTS'     | 'Results'     )
   | DIR  ( 'result'      | 'RESULT'      | 'Result'      )
   | DIR  ( 'res'         | 'RES'         | 'Res'         )
   ;

D_QUIET
   : DIR  ( 'quiet' | 'QUIET' | 'Quiet' )
   ;

D_SILENCE
   : DIR  ( 'silencedirectives' | 'SILENCEDIRECTIVES' | 'Silencedirectives' | 'SilenceDirectives' )
   | DIR  ( 'silentdirectives'  | 'SILENTDIRECTIVES'  | 'Silentdirectives'  | 'SilentDirectives'  )
   | DIR  ( 'silencedir'        | 'SILENCEDIR'        | 'Silencedir'        | 'SilenceDir'        )
   | DIR  ( 'silentdir'         | 'SILENTDIR'         | 'Silentdir'         | 'SilentDir'         )
   | DIR  ( 'silence'           | 'SILENCE'           | 'Silence'           )
   | DIR  ( 'silent'            | 'SILENT'            | 'Silent'            )
   ;

D_VARIABLES
   : DIR  ( 'variables' | 'VARIABLES' | 'Variables' )
   | DIR  ( 'variable'  | 'VARIABLE'  | 'Variable'  )
   | DIR  ( 'vars'      | 'VARS'      | 'Vars'      )
   | DIR  ( 'var'       | 'VAR'       | 'Var'       )
   ;

D_SEPARATORS
   : DIR  ( 'separators' | 'SEPARATORS' | 'Separators' )
   | DIR  ( 'separator'  | 'SEPARATOR'  | 'Separator'  )
   | DIR  ( 'seps'       | 'SEPS'       | 'Seps'       )
   | DIR  ( 'sep'        | 'SEP'        | 'Sep'        )
   ;

D_IGNORECASE
   : DIR  ( 'caseinsensitive' | 'CASEINSENSITIVE' | 'Caseinsensitive' | 'CaseInsensitive' )
   | DIR  ( 'insensitive'     | 'INSENSITIVE'     | 'Insensitive'     )
   | DIR  ( 'ignorecase'      | 'IGNORECASE'      | 'Ignorecase'      | 'IgnoreCase'      )
   | DIR  ( 'ignore'          | 'IGNORE'          | 'Ignore'          )
   | DIR  ( 'case'            | 'CASE'            | 'Case'            )
   | DIR  ( 'ins'             | 'INS'             | 'Ins'             )
   | DIR  ( 'ign'             | 'IGN'             | 'Ign'             )
   ;

D_QUOTESTRINGS
   : DIR  ( 'quotestrings' | 'QUOTESTRINGS' | 'Quotestrings' | 'QuoteStrings' )
   | DIR  ( 'quotestring'  | 'QUOTESTRING'  | 'Quotestring'  | 'QuoteString'  )
   | DIR  ( 'quotes'       | 'QUOTES'       | 'Quotes'       )
   | DIR  ( 'quote'        | 'QUOTE'        | 'Quote'        )
   ;


FORMAT
   : '@' [\-+] ? INT ? ( '.' INT ) ? [a-zA-Z,_] ? [a-zA-Z%$]
   ;

STRING
   : '"'      ( ESC1 | SAFECODEPOINT1 ) * '"'
   | '\''     ( ESC2 | SAFECODEPOINT2 ) * '\''
   | '\u2018' ( ESC3 | SAFECODEPOINT3 ) * '\u2019'
   | '\u201C' ( ESC4 | SAFECODEPOINT4 ) * '\u201D'
   | '\u2039' ( ESC5 | SAFECODEPOINT5 ) * '\u203A'
   | '\u00AB' ( ESC6 | SAFECODEPOINT6 ) * '\u00BB'
   ;

ISTRING
   : '`'  ( ESCI | SAFECODEPOINTI ) * '`'
   ;

fragment ESC1
   : ESC ( ["] | ESCAPES | UNICODE )
   ;

fragment ESC2
   : ESC ( ['] | ESCAPES | UNICODE )
   ;

fragment ESC3
   : ESC ( '\u2019' | ESCAPES | UNICODE )
   ;

fragment ESC4
   : ESC ( '\u201D' | ESCAPES | UNICODE )
   ;

fragment ESC5
   : ESC ( '\u203A' | ESCAPES | UNICODE )
   ;

fragment ESC6
   : ESC ( '\u00BB' | ESCAPES | UNICODE )
   ;

fragment ESCI
   : ESC ( [`] | ESCAPES | UNICODE )
   ;

fragment ESCAPES
   : [\\bfnrt0]
   ;

fragment ESC
   : '\\'
   ;

fragment UNICODE
   : 'u' HEX HEX HEX HEX
   | 'u' '{' HEX + '}'
   | 'o' OCT OCT OCT
   | 'o' '{' OCT + '}'
   | 'B' BIN BIN BIN BIN BIN BIN BIN BIN
   | 'B' '{' BIN + '}'
   ;

fragment HEX
   : [0-9a-fA-F]
   ;

fragment OCT
   : [0-7]
   ;

fragment BIN
   : [01]
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

//
// This must match "isIdentifierStart" in CalcUtil
//
fragment NAME_START_CHAR
   : 'A'..'Z'
   | 'a'..'z'
   | '_'
   | '\u00C0'..'\u00D6'
   | '\u00D8'..'\u00F6'
   | '\u00F8'..'\u02FF'
   | '\u0370'..'\u037D'
   | '\u037F'..'\u1FFF'
   | '\u200C'..'\u200D'
   | '\u2071'..'\u2073'
   | '\u207A'..'\u207F'
   | '\u208A'..'\u218F'
   | '\u2400'
   | '\u2C00'..'\u2FEF'
   | '\u3001'..'\uD7FF'
   | '\uF900'..'\uFDCF'
   | '\uFDF0'..'\uFF0F'
   | '\uFF1A'..'\uFFFD'
   | PI_VALUES
   ;

//
// This must match "isIdentifierPart" in CalcUtil
//
fragment NAME_CHAR
   : NAME_START_CHAR
   | '0'..'9'
   | '\u00B7'
   | '\u0300'..'\u036F'
   | '\u203F'..'\u2040'
   ;

fragment PI_VALUES
   : '\u{1D6B7}' | '\u{1D6D1}' | '\u{1D6E1}'
   | '\u{1D6F1}' | '\u{1D70B}' | '\u{1D71B}'
   | '\u{1D72B}' | '\u{1D745}' | '\u{1D755}'
   | '\u{1D765}' | '\u{1D77F}' | '\u{1D78F}'
   | '\u{1D79F}' | '\u{1D7B9}' | '\u{1D7C9}'
   ;

NUMBER
   : INT ('.' [0-9] +)? EXP?
   ;

NUM_CONST
   : [\u2460-\u2473]         /* circled 1..20       */
   | [\u2474-\u2487]         /* (1)..(20)           */
   | [\u2488-\u249B]         /* 1. .. 20.           */
   | ( '\u24EA' | '\u24FF' ) /* 0                   */
   | [\u24EB-\u24F4]         /* circled 11..20      */
   | [\u24F5-\u24FE]         /* circled 1..10       */
   | [\u2776-\u277F]         /* small circled 1..10 */
   | [\u2780-\u2789]         /* small circled 1..10 */
   | [\u278A-\u2793]         /* small circled 1..10 */
   | [\uFF10-\uFF19]         /* full width 0..9     */
   | [\u{1D7CE}-\u{1D7D7}]   /* mathematical 0..9   */
   | [\u{1D7D8}-\u{1D7E1}]   /* mathematical 0..9   */
   | [\u{1D7E2}-\u{1D7EB}]   /* mathematical 0..9   */
   | [\u{1D7EC}-\u{1D7F5}]   /* mathematical 0..9   */
   | [\u{1D7F6}-\u{1D7FF}]   /* mathematical 0..9   */
   ;

BIN_CONST
   : '0' [Bb] [01]+
   ;

OCT_CONST
   : '0' [0-7]+
   ;

HEX_CONST
   : '0' [Xx] [0-9a-fA-F] +
   ;

KB_CONST
   : INT ( [Kk] | [Mm] | [Gg] | [Tt] | [Pp] | [Ee] ) [Ii]? [Bb]?
   ;

// no leading zeros
fragment INT
   : '0'
   | [1-9] [0-9]*
   ;

// \- since - means "range" inside [...]
fragment EXP
   : [Ee] [+\-]? INT
   ;

fragment DTSEP
   : [\-/,;._]
   ;

fragment DIG
   : [0-9]
   ;

fragment DIR
   : ':'
   ;

fragment NL
   : '\r'? '\n'
   ;


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

