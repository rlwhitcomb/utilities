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
 *	    #223: Implement ":predefined" command.
 *	26-Jan-2022 (rlwhitcomb
 *	    #227: Add "timethis" statement.
 *	30-Jan-2022 (rlwhitcomb)
 *	    #229: Change grammar for actualParams to allow detection of which params
 *	    are actually missing, and should be defaulted.
 *	31-Jan-2022 (rlwhitcomb)
 *	    #212: Change grammar for "typeof" so we can recognize functions.
 *	02-Feb-2022 (rlwhitcomb)
 *	    We're having trouble with variables and fields named "format", so rename it
 *	    until we get a better solution to fields with the same names as the predefined
 *	    functions.
 *	    #230: Allow wildcards on ":variables", ":clear", and ":predefs".
 *	04-Feb-2022 (rlwhitcomb)
 *	    #237: Need to allow WILD_ID to include the "$" and "#" characters.
 *	05-Feb-2022 (rlwhitcomb)
 *	    #219: Add the dot selector to case expressions.
 *	    #144: Add "matches" function and "matches" case selector.
 *	07-Feb-2022 (rlwhitcomb)
 *	    #239: Add "compareOp expr" as another caseSelector.
 *	11-Feb-2022 (rlwhitcomb)
 *	    #199: Allow any id for function parameters and loop variables.
 *	15-Feb-2022 (rlwhitcomb)
 *	    #249: Add "expr IN loopCtl" as another expression type.
 *	27-Mar-2022 (rlwhitcomb)
 *	    #190: Support "caret" notation inside strings.
 *	11-Apr-2022 (rlwhitcomb)
 *	    #267: Add "Elvis" operator.
 *	26-Apr-2022 (rlwhitcomb)
 *	    #290: Add optional statement blocks to directives.
 *	04-May-2022 (rlwhitcomb)
 *	    #307: Cleanup.
 *	    #308: Add "<>" as an alternative for "not equals".
 *	05-May-2022 (rlwhitcomb)
 *	    #296: Add "notnull" function.
 *	    #298: Add "within" keyword to "loop" statement and "in" expression.
 *	06-May-2022 (rlwhitcomb)
 *	    #305: Change "chars" to "codes" and add new "chars" that separates
 *	    the string into characters.
 *	    #287: Allow "define" and "const" at any level.
 *	07-May-2022 (rlwhitcomb)
 *	    #292: Add ":require" directive for version checking.
 *	10-May-2022 (rlwhitcomb)
 *	    #317: Support "/" escape.
 *	    #316: Add reverse "Elvis" operator.
 *	11-May-2022 (rlwhitcomb)
 *	    #319: Add "!!" operator (explicitly).
 *	12-May-2022 (rlwhitcomb)
 *	    #321: Allow ":requires" spelling for new directive.
 *	16-May-2022 (rlwhitcomb)
 *	    #325: Change EOL? to EOL* everywhere to allow more variety of empty objects.
 *	    And backout one change because it results in an infinite loop always.
 *	18-May-2022 (rlwhitcomb)
 *	    #335: "BASE" needs to be a valid ID also.
 *	21-May-2022 (rlwhitcomb)
 *	    #327: Add "unique" function.
 *	22-May-2022 (rlwhitcomb)
 *	    Refactor because parsing had gotten way too slow.
 *	23-May-2022 (rlwhitcomb)
 *	    #341: Add "~~" operator ("to number").
 *	25-May-2022 (rlwhitcomb)
 *	    #348: Add "var" statement.
 *	    Allow EOL after colon in object declarations.
 *	01-Jun-2022 (rlwhitcomb)
 *	    #45: Add "write" function.
 *	20-Jun-2022 (rlwhitcomb)
 *	    #364: Add optional flag to ":echo" to set output destination.
 *	21-Jun-2022 (rlwhitcomb)
 *	    #314: Add syntax for set object.
 *	    Add "set minus" symbol.
 *	24-Jun-2022 (rlwhitcomb)
 *	    #373: Add "exists" function.
 *	05-Jul-2022 (rlwhitcomb)
 *	    #291: Add optional flags to "matches" function.
 *	06-Jul-2022 (rlwhitcomb)
 *	    #375: Allow end of line in more places where long expressions might be common.
 *	    #388: Add optional flags value to case's "matches" selector (same as "matches" function).
 *	07-Jul-2022 (rlwhitcomb)
 *	    #389: "var id" shouldn't need an initial expression value (unlike "const").
 *	08-Jul-2022 (rlwhitcomb)
 *	    #394: More allowed EOL* in assignment statements.
 *	09-Jul-2022 (rlwhitcomb)
 *	    #397: Add multiline string constant.
 *	10-Jul-2022 (rlwhitcomb)
 *	    #392: Directive to keep objects sorted by keys.
 *	11-Jul-2022 (rlwhitcomb)
 *	    #403: Introduce raw string format.
 *	    #401, #290: Add bracket block processing to the other (non-mode-option) directives.
 *	    Change some lexical fragments in the grammar into explicit lexical tokens to keep
 *	    some of the code from breaking if things are reordered here (see "isEmptyStmt()"
 *	    for example).
 *	24-Jul-2022 (rlwhitcomb)
 *	    #412: Add optional skip level element to "@j" formatting.
 *	29-Jul-2022 (rlwhitcomb)
 *	    #390: Move "case" down to expressions instead of statements to allow the return
 *	    value to be assigned, etc.
 *	15-Aug-2022 (rlwhitcomb)
 *	    #440: Implement "has" operator.
 *	16-Aug-2022 (rlwhitcomb)
 *	    #439: Implement "next" statement.
 *	23-Aug-2022 (rlwhitcomb)
 *	    #459: Implement "@@" (to string) operator.
 *	24-Aug-2022 (rlwhitcomb)
 *	    #454: Implement ":colors" directive.
 *	    #447: Implement "grads" mode for trig operations.
 *	25-Aug-2022 (rlwhitcomb)
 *	    #465: Add "delete" and "rename" file operations.
 *	26-Aug-2022 (rlwhitcomb)
 *	    #458: Add optional "parallel" keyword to function declaration, and "wait" statement.
 *	29-Aug-2022 (rlwhitcomb)
 *	    #453: Add "fileinfo" function.
 *	08-Sep-2022 (rlwhitcomb)
 *	    #475: New "callers" function.
 *	13-Sep-2022 (rlwhitcomb)
 *	    #480: Additional KB suffixes.
 *	14-Sep-2022 (rlwhitcomb)
 *	    #485: Add "mod" operator (multiply operator).
 *	17-Sep-2022 (rlwhitcomb)
 *	    #426: "todate" function with 1, 2, or 3 arguments.
 *	    Add other aliases for function names that are two words (such as 'sumOf').
 *	    Adjust DATE_CONST pattern for negative years.
 *	06-Oct-2022 (rlwhitcomb)
 *	    #501: Add "tobase" function.
 *	08-Oct-2022 (rlwhitcomb)
 *	    #501: Add "frombase" function.
 *	21-Oct-2022 (rlwhitcomb)
 *	    #473: Add "findfiles" function.
 *	06-Nov-2022 (rlwhitcomb)
 *	    #476: New "readProperties" and "writeProperties" functions.
 *	09-Nov-2022 (rlwhitcomb)
 *	    #550: ":assert" directive.
 */

grammar Calc;

@lexer::members {
boolean allowWild = false;
}

prog
   : stmtOrExpr * EOF
   ;

stmtOrExpr
   : exprStmt
   | directive
   | defineStmt
   | constStmt
   | varStmt
   | loopStmt
   | whileStmt
   | ifStmt
   | leaveStmt
   | nextStmt
   | waitStmt
   | timeThisStmt
   | emptyStmt
   ;

exprStmt
   : expr FORMAT ?
   ;

defineStmt
   : K_DEFINE K_PARALLEL ? id formalParamList ? ASSIGN stmtBlock
   ;

constStmt
   : K_CONST id ASSIGN expr
   ;

varStmt
   : K_VAR id ( ASSIGN expr ) ?
   ;

loopStmt
   : K_LOOP ( id ? ( K_OVER | K_IN | K_WITHIN | SET_IN ) ) ? loopCtl stmtBlock
   ;

whileStmt
   : K_WHILE expr stmtBlock
   ;

ifStmt
   : K_IF expr stmtBlock ( EOL* K_ELSE stmtBlock ) ?
   ;

caseStmt
   : K_CASE expr ( K_OF | K_IN | SET_IN ) LBRACE caseBlock ( COMMA caseBlock ) * RBRACE
   | K_CASE expr ( K_OF | K_IN | SET_IN ) caseBlock ( COMMA caseBlock ) *
   ;

leaveStmt
   : K_LEAVE expr1 ?
   ;

nextStmt
   : K_NEXT
   ;

waitStmt
   : K_WAIT
   ;

timeThisStmt
   : K_TIMETHIS ( expr COMMA ) ? stmtBlock
   ;

bracketBlock
   : EOL* LBRACE ( EOL* stmtOrExpr ) * RBRACE EOL*
   ;

stmtBlock
   : EOL* LBRACE ( EOL* stmtOrExpr ) * RBRACE EOL*
   | EOL* stmtOrExpr EOL*
   ;

caseBlock
   : EOL* caseSelector ( COMMA EOL* caseSelector ) * COLON stmtBlock
   ;

emptyStmt
   : EOL
   | ENDEXPR
   ;

expr
   : value                               # valueExpr
   | obj                                 # objExpr
   | arr                                 # arrExpr
   | set                                 # setExpr
   | complex                             # complexValueExpr
   | var                                 # varExpr
   | expr K_HAS ( id | STRING | ISTRING | ( LBRACK expr RBRACK ) ) # hasExpr
   | LPAREN expr RPAREN                  # parenExpr
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
   | ( K_ISNULL | K_NOTNULL ) expr1      # isNullExpr
   | K_TYPEOF typeArg                    # typeofExpr
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
   | K_FORMATSTRING exprN                # formatExpr
   | K_SORT ( expr2 | expr1 )            # sortExpr
   | K_REVERSE expr1                     # reverseExpr
   | K_UNIQUE expr1                      # uniqueExpr
   | K_TRIM expr1                        # trimExpr
   | K_PAD padArgs                       # padExpr
   | K_FIB expr1                         # fibExpr
   | K_BN expr1                          # bernExpr
   | K_DEC expr1                         # decExpr
   | K_TODATE ( expr3 | expr2 | expr1 )  # dateExpr
   | K_TOBASE expr2                      # toBaseExpr
   | K_FROMBASE expr2                    # fromBaseExpr
   | K_FRAC ( expr3 | expr2 | expr1 )    # fracExpr
   | K_COMPLEX ( expr2 | expr1 )         # complexFuncExpr
   | K_ROMAN expr1                       # romanExpr
   | ( K_UPPER | K_LOWER ) expr1         # caseConvertExpr
   | K_FACTORS expr1                     # factorsExpr
   | K_PFACTORS expr1                    # primeFactorsExpr
   | K_CHARS expr1                       # charsExpr
   | K_CODES expr1                       # codesExpr
   | K_DOW expr1                         # dayOfWeekExpr
   | K_DOM expr1                         # dayOfMonthExpr
   | K_DOY expr1                         # dayOfYearExpr
   | K_MOY expr1                         # monthOfYearExpr
   | K_YOD expr1                         # yearOfDateExpr
   | K_EVAL expr1                        # evalExpr
   | K_EXEC exprN                        # execExpr
   | K_DECODE expr1                      # decodeExpr
   | K_ENCODE expr1                      # encodeExpr
   | K_EXISTS ( expr2 | expr1 )          # existsExpr
   | K_FILEINFO expr1                    # fileInfoExpr
   | K_FINDFILES ( expr3 | expr2 )       # findFilesExpr
   | K_READ ( expr2 | expr1 )            # readExpr
   | K_WRITE ( expr3 | expr2 )           # writeExpr
   | K_READPROPERTIES ( expr2 | expr1 )  # readPropExpr
   | K_WRITEPROPERTIES ( expr3 | expr2 ) # writePropExpr
   | K_DELETE exprN                      # deleteExpr
   | K_RENAME expr2                      # renameExpr
   | K_MATCHES ( expr3 | expr2 )         # matchesExpr
   | K_CALLERS LPAREN optExpr RPAREN     # callersExpr
   | var INC_OP                          # postIncOpExpr
   |<assoc=right> INC_OP var             # preIncOpExpr
   |<assoc=right> ADD_OP expr            # negPosExpr
   |<assoc=right> '!!' expr              # toBooleanExpr
   |<assoc=right> ('!'|'\u00AC') expr    # booleanNotExpr
   |<assoc=right> TO_STRING_OP expr      # toStringExpr
   |<assoc=right> TO_NUM_OP expr         # toNumberExpr
   |<assoc=right> BIT_NOT_OP expr        # bitNotExpr
   | expr '!'                            # factorialExpr
   |<assoc=right> expr POW_OP expr       # powerExpr
   |<assoc=right> expr POWERS            # powerNExpr
   | expr ( MULT_OP | K_MOD ) expr                # multiplyExpr
   | expr ADD_OP expr                             # addExpr
   | expr SHIFT_OP expr                           # shiftExpr
   | expr SPACE_OP expr                           # spaceshipExpr
   | expr COMPARE_OP expr                         # compareExpr
   | expr ( K_OF|K_IN|K_WITHIN|SET_IN ) loopCtl   # inExpr
   | expr EQUAL_OP expr                           # equalExpr
   | expr BIT_OP expr                             # bitExpr
   | expr EOL* BOOL_AND_OP EOL* expr              # booleanAndExpr
   | expr EOL* BOOL_OR_OP EOL* expr               # booleanOrExpr
   | expr EOL* BOOL_XOR_OP EOL* expr              # booleanXorExpr
   | expr EOL* ELVIS_OP EOL* expr                 # elvisExpr
   |<assoc=right> expr EOL* QUEST EOL* expr EOL* COLON EOL* expr # eitherOrExpr
   |<assoc=right> var EOL* ASSIGN EOL* expr       # assignExpr
   |<assoc=right> var EOL* POW_ASSIGN EOL* expr   # powerAssignExpr
   |<assoc=right> var EOL* MULT_ASSIGN EOL* expr  # multAssignExpr
   |<assoc=right> var EOL* ADD_ASSIGN EOL* expr   # addAssignExpr
   |<assoc=right> var EOL* SHIFT_ASSIGN EOL* expr # shiftAssignExpr
   |<assoc=right> var EOL* BIT_ASSIGN EOL* expr   # bitAssignExpr
   | caseStmt                            # caseExpr
   ;

expr1
   : LPAREN expr RPAREN
   | expr
   ;

expr2
   : LPAREN expr COMMA expr RPAREN
   | expr COMMA expr
   ;

expr3
   : LPAREN expr COMMA expr COMMA expr RPAREN
   | expr COMMA expr COMMA expr
   ;

exprN
   : LPAREN exprList RPAREN
   | exprList
   ;

typeArg
   : LPAREN ( var | expr ) RPAREN
   | ( var | expr )
   ;

replaceArgs
   : LPAREN expr COMMA expr COMMA expr ( COMMA replaceOption ) ? RPAREN
   | expr COMMA expr COMMA expr ( COMMA replaceOption ) ?
   ;

spliceArgs
   : LPAREN expr COMMA dropObjs COMMA obj RPAREN
   | LPAREN expr COMMA dropObjs RPAREN
   | LPAREN expr COMMA obj RPAREN
   | expr COMMA dropObjs COMMA obj
   | expr COMMA dropObjs
   | expr COMMA obj
   | exprN
   | expr3
   | expr2
   | expr1
   ;

fillArgs
   : LPAREN var COMMA expr COMMA expr COMMA expr RPAREN
   | LPAREN var COMMA expr COMMA expr RPAREN
   | LPAREN var COMMA expr RPAREN
   | LPAREN var RPAREN
   | var COMMA expr COMMA expr COMMA expr
   | var COMMA expr COMMA expr
   | var COMMA expr
   | var
   ;

padArgs
   : LPAREN var COMMA expr COMMA expr RPAREN
   | LPAREN var COMMA expr RPAREN
   | var COMMA expr COMMA expr
   | var COMMA expr
   ;

dotRange
   : ( expr DOTS ) ? expr ( COMMA expr ) ?
   ;

loopCtl
   : dotRange
   | LPAREN exprList RPAREN
   | LPAREN RPAREN
   ;

arr
   : LBRACK EOL* exprList EOL* RBRACK
   | LBRACK EOL* RBRACK
   ;

exprList
   : expr ( COMMA EOL* expr ) *
   ;

caseSelector
   : K_MATCHES ( expr2 | expr1 )
   | expr DOTS expr ( COMMA expr ) ?
   | expr
   | compareOp expr
   | K_DEFAULT
   ;

obj
   : LBRACE EOL* pair ( COMMA EOL* pair ) * EOL* RBRACE
   ;

pair
   : id COLON EOL* expr
   | STRING COLON EOL* expr
   | ISTRING COLON EOL* expr
   ;

set
   : LBRACE EOL* exprList EOL* RBRACE
   ;

complex
   : LPAREN expr COMMA expr RPAREN
   ;

var
   : var ( DOT ( var | STRING | ISTRING ) ) # objVar
   | var ( LBRACK expr RBRACK | INDEXES )   # arrVar
   | var actualParams                       # functionVar
   | id                                     # idVar
   | GLOBALVAR                              # globalVar
   ;

value
   : STRING                             # stringValue
   | ISTRING                            # iStringValue
   | NUMBER                             # numberValue
   | NUM_CONST                          # numberConstValue
   | BIN_CONST                          # binaryValue
   | OCT_CONST                          # octalValue
   | HEX_CONST                          # hexValue
   | KB_CONST                           # kbValue
   | FRAC_CONST                         # fracValue
   | ROMAN_CONST                        # romanValue
   | TIME_CONST                         # timeValue
   | DATE_CONST                         # dateValue
   | ( LBRACE EOL* RBRACE | EMPTY_SET ) # emptyObjValue
   ;

formalParamList
   : LPAREN formalParam ( COMMA formalParam ) * ( COMMA DOTS ) ? RPAREN
   | LPAREN DOTS RPAREN
   | LPAREN RPAREN
   ;

formalParam
   : id ( ASSIGN expr ) ?
   ;

optExpr
   : expr ?
   ;

actualParams
   : LPAREN optExpr ( COMMA optExpr ) * RPAREN
   ;

dropObjs
   : LBRACK ( id | STRING | ISTRING ) ( COMMA ( id | STRING | ISTRING ) ) * RBRACK
   | LBRACK RBRACK
   ;

directive
   : ( D_DECIMAL | D_PRECISION ) numberOption
                               bracketBlock ? # decimalDirective
   | D_DEFAULT bracketBlock ?                 # defaultDirective
   | D_DOUBLE bracketBlock ?                  # doubleDirective
   | D_FLOAT bracketBlock ?                   # floatDirective
   | D_UNLIMITED bracketBlock ?               # unlimitedDirective
   | D_DEGREES bracketBlock ?                 # degreesDirective
   | D_RADIANS bracketBlock ?                 # radiansDirective
   | D_GRADS bracketBlock ?                   # gradsDirective
   | D_BINARY bracketBlock ?                  # binaryDirective
   | D_SI bracketBlock ?                      # siDirective
   | D_MIXED bracketBlock ?                   # mixedDirective
   | D_ECHO ( expr ( COMMA expr ) ? ) ?       # echoDirective
   | D_CLEAR wildIdList ?                     # clearDirective
   | D_VARIABLES wildIdList ?                 # variablesDirective
   | D_PREDEFINED wildIdList ?                # predefinedDirective
   | D_INCLUDE expr ( COMMA expr ) ?          # includeDirective
   | D_SAVE expr ( COMMA expr ) ?             # saveDirective
   | D_TIMING modeOption bracketBlock ?       # timingDirective
   | D_RATIONAL modeOption bracketBlock ?     # rationalDirective
   | D_DEBUG modeOption bracketBlock ?        # debugDirective
   | D_RESULTSONLY modeOption bracketBlock ?  # resultsOnlyDirective
   | D_QUIET modeOption bracketBlock ?        # quietDirective
   | D_SILENCE modeOption bracketBlock ?      # silenceDirective
   | D_SEPARATORS modeOption bracketBlock ?   # separatorsDirective
   | D_IGNORECASE modeOption bracketBlock ?   # ignoreCaseDirective
   | D_QUOTESTRINGS modeOption bracketBlock ? # quoteStringsDirective
   | D_SORTOBJECTS modeOption bracketBlock ?  # sortObjectsDirective
   | D_COLORS modeOption bracketBlock ?       # colorsDirective
   | D_REQUIRE requireOptions                 # requireDirective
   | D_ASSERT expr ( COMMA expr ) ?           # assertDirective
   ;

numberOption
   : LPAREN NUMBER RPAREN
   | NUMBER
   | var
   ;

idList
   : LBRACK id ( COMMA id ) * RBRACK
   | id ( COMMA id ) *
   | LBRACK RBRACK
   ;

id
   : ID
   | MODES
   | REPLACE_MODES
   | BASE
   ;

wildIdList
   : LBRACK wildId ( COMMA wildId ) * RBRACK
   | wildId ( COMMA wildId ) *
   | LBRACK RBRACK
   ;

wildId
   : WILD_ID
   | MODES
   | REPLACE_MODES
   | BASE
   ;

compareOp
   : COMPARE_OP
   | EQUAL_OP
   ;

modeOption
   : MODES
   | var
   ;

requireOptions
   : versionNumber ( COMMA BASE versionNumber ) ?
   | BASE versionNumber
   ;

replaceOption
   : REPLACE_MODES
   | var
   ;

versionNumber
   : STRING
   | ISTRING
   | NUMBER
   | VERSION
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

BASE     : 'base' | 'BASE' | 'Base'
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
         : [Hh] '\'' '-' ? DIG ? DIG ( COLON DIG DIG ( COLON DIG DIG ( DOT DIG+ ) ? ) ? ) ? ( [ \t] * AM_PM ) ? '\''
         | [Tt] '\'' '-' ? DIG + ( DOT DIG * ) ? [ \t] * DURATIONS '\''
         ;

DATE_CONST
/* ISO-8601 format with more separators allowed */
         : 'd' '\'' '-' ? ( DIG DIG | DIG DIG DIG DIG ) DTSEP DIG ? DIG DTSEP DIG ? DIG '\''
         | 'd' '\'' '-' ? DIG DIG DIG DIG DIG DIG DIG DIG '\''
         | 'd' '\'' '-' ? DIG DIG DIG DIG DIG DIG '\''
/* US format (MM/dd/yyyy or MM/dd/yy or MMddyyyy or MMddyy) */
         | 'D' '\'' DIG ? DIG DTSEP DIG ? DIG DTSEP '-' ? ( DIG DIG | DIG DIG DIG DIG ) '\''
         | 'D' '\'' DIG DIG DIG DIG '-' ? DIG DIG DIG DIG '\''
         | 'D' '\'' DIG DIG DIG DIG '-' ? DIG DIG '\''
         ;


/*
 * Predefined function names
 */

K_MOD      : 'mod' | 'MOD' | 'Mod' ;

K_HAS      : 'has' | 'HAS' | 'Has' ;

K_ABS      : 'abs' | 'ABS' | 'Abs' ;

K_SINH     : 'sinh' | 'SINH' | 'Sinh' | 'SinH' | 'sinH' ;

K_SIN      : 'sin' | 'SIN' | 'Sin' ;

K_COSH     : 'cosh' | 'COSH' | 'Cosh' | 'CosH' | 'cosH' ;

K_COS      : 'cos' | 'COS' | 'Cos' ;

K_TANH     : 'tanh' | 'TANH' | 'Tanh' | 'TanH' | 'tanH' ;

K_TAN      : 'tan' | 'TAN' | 'Tan' ;

K_ASIN     : 'asin' | 'ASIN' | 'Asin' | 'ASin' | 'aSin' ;

K_ACOS     : 'acos' | 'ACOS' | 'Acos' | 'ACos' | 'aCos' ;

K_ATAN2    : 'atan2' | 'ATAN2' | 'Atan2' | 'ATan2' | 'aTan2' ;

K_ATAN     : 'atan' | 'ATAN' | 'Atan' | 'ATan' | 'aTan' ;

K_SQRT     : 'sqrt' | 'SQRT' | 'Sqrt' | '\u221A' ;

K_CBRT     : 'cbrt' | 'CBRT' | 'Cbrt' | '\u221B' ;

K_FORT     : 'fort' | 'FORT' | 'Fort' | '\u221C' ;

K_LOG      : 'log' | 'LOG' | 'Log' ;

K_LN       : 'ln' | 'LN' | 'Ln' ;

K_LN2      : 'ln2' | 'LN2' | 'Ln2' ;

K_EPOW     : 'epow' | 'EPOW' | 'Epow' | 'EPow' | 'ePow' ;

K_TENPOW   : 'tenpow' | 'TENPOW' | 'Tenpow' | 'TenPow' | 'tenPow' ;

K_RANDOM   : 'random' | 'RANDOM' | 'Random'
           | 'rand'   | 'RAND'   | 'Rand'
           ;

K_SIGNUM   : 'signum' | 'SIGNUM' | 'Signum' | 'SigNum' | 'sigNum'
           | 'sgn' | 'SGN' | 'Sgn' | 'SgN' | 'sgN'
           ;

K_LENGTH   : 'length' | 'LENGTH' | 'Length' ;

K_SCALE    : 'scale' | 'SCALE' | 'Scale' ;

K_ROUND    : 'round' | 'ROUND' | 'Round' ;

K_CEIL     : 'ceil' | 'CEIL' | 'Ceil' ;

K_FLOOR    : 'floor' | 'FLOOR' | 'Floor' ;

K_ISPRIME  : 'isprime' | 'ISPRIME' | 'Isprime' | 'IsPrime' | 'isPrime' ;

K_ISNULL   : 'isnull' | 'ISNULL' | 'Isnull' | 'IsNull' | 'isNull' ;

K_NOTNULL  : 'notnull' | 'NOTNULL' | 'Notnull' | 'NotNull' | 'notNull' ;

K_TYPEOF   : 'typeof' | 'TYPEOF' | 'Typeof' | 'TypeOf' | 'typeOf' ;

K_CAST     : 'cast' | 'CAST' | 'Cast' ;

K_GCD      : 'gcd' | 'GCD' ;

K_LCM      : 'lcm' | 'LCM' ;

K_MAX      : 'max' | 'MAX' | 'Max' ;

K_MIN      : 'min' | 'MIN' | 'Min' ;

K_JOIN     : 'join' | 'JOIN' | 'Join' ;

K_SPLIT    : 'split' | 'SPLIT' | 'Split' ;

K_INDEX    : 'index' | 'INDEX' | 'Index' ;

K_SUBSTR   : 'substr' | 'SUBSTR' | 'Substr' | 'SubStr' | 'subStr' ;

K_REPLACE  : 'replace' | 'REPLACE' | 'Replace' ;

K_SLICE    : 'slice' | 'SLICE' | 'Slice' ;

K_SPLICE   : 'splice' | 'SPLICE' | 'Splice' ;

K_FILL     : 'fill' | 'FILL' | 'Fill' ;

K_FORMATSTRING : 'formatstring' | 'FORMATSTRING' | 'FormatString' | 'Formatstring' | 'formatString' ;

K_SORT     : 'sort' | 'SORT' | 'Sort' ;

K_REVERSE  : 'reverse' | 'REVERSE' | 'Reverse' ;

K_UNIQUE   : 'unique' | 'UNIQUE' | 'Unique' ;

K_TRIM     : 'trim'  | 'TRIM'  | 'Trim'
           | 'ltrim' | 'LTRIM' | 'Ltrim' | 'LTrim' | 'lTrim'
           | 'rtrim' | 'RTRIM' | 'Rtrim' | 'RTrim' | 'rTrim'
           ;

K_PAD      : 'pad'  | 'PAD'  | 'Pad'
           | 'lpad' | 'LPAD' | 'Lpad' | 'LPad' | 'lPad'
           | 'rpad' | 'RPAD' | 'Rpad' | 'RPad' | 'rPad'
           ;

K_FIB      : 'fib' | 'FIB' | 'Fib' ;

K_BN       : 'bn' | 'BN' | 'Bn' ;

K_DEC      : 'dec' | 'DEC' | 'Dec' ;

K_TODATE   : 'todate' | 'TODATE' | 'ToDate' | 'Todate' | 'toDate' ;

K_TOBASE   : 'tobase' | 'TOBASE' | 'ToBase' | 'Tobase' | 'toBase' ;

K_FROMBASE : 'frombase' | 'FROMBASE' | 'FromBase' | 'Frombase' | 'fromBase' ;

K_FRAC     : 'frac' | 'FRAC' | 'Frac' ;

K_COMPLEX  : 'complex' | 'COMPLEX' | 'Complex' ;

K_ROMAN    : 'roman' | 'ROMAN' | 'Roman' ;

K_UPPER    : 'upper' | 'UPPER' | 'Upper' ;

K_LOWER    : 'lower' | 'LOWER' | 'Lower' ;

K_FACTORS  : 'factors' | 'FACTORS' | 'Factors' ;

K_PFACTORS : 'pfactors' | 'PFACTORS' | 'Pfactors' | 'PFactors' | 'pFactors' ;

K_CHARS    : 'chars' | 'CHARS' | 'Chars' ;

K_CODES    : 'codes' | 'CODES' | 'Codes' ;

K_DOW      : 'dow' | 'DOW' | 'DoW' ;

K_DOM      : 'dom' | 'DOM' | 'DoM' ;

K_DOY      : 'doy' | 'DOY' | 'DoY' ;

K_MOY      : 'moy' | 'MOY' | 'MoY' ;

K_YOD      : 'yod' | 'YOD' | 'YoD' ;

K_EVAL     : 'eval' | 'EVAL' | 'Eval' ;

K_EXEC     : 'exec' | 'EXEC' | 'Exec' ;

K_DECODE   : 'decode' | 'DECODE' | 'Decode' ;

K_ENCODE   : 'encode' | 'ENCODE' | 'Encode' ;

K_EXISTS   : 'exists' | 'EXISTS' | 'Exists' ;

K_FILEINFO : 'fileinfo' | 'FILEINFO' | 'FileInfo' | 'fileInfo' ;

K_FINDFILES: 'findfiles' | 'FINDFILES' | 'FindFiles' | 'findFiles' ;

K_READ     : 'read' | 'READ' | 'Read' ;

K_WRITE    : 'write' | 'WRITE' | 'Write' ;

K_READPROPERTIES
           : 'readproperties' | 'READPROPERTIES' | 'ReadProperties' | 'readProperties' ;

K_WRITEPROPERTIES
           : 'writeproperties' | 'WRITEPROPERTIES' | 'WriteProperties' | 'writeProperties' ;

K_DELETE   : 'delete' | 'DELETE' | 'Delete' ;

K_RENAME   : 'rename' | 'RENAME' | 'Rename' ;

K_MATCHES  : 'matches' | 'MATCHES' | 'Matches' ;

K_CALLERS  : 'callers' | 'CALLERS' | 'Callers' ;

K_SUMOF    : 'sumof' | 'SUMOF' | 'Sumof' | 'SumOf' | 'sumOf' | '\u2211' ;

K_PRODUCTOF: 'productof' | 'PRODUCTOF' | 'Productof' | 'ProductOf' | 'productOf' | '\u220F' ;

/*
 * Statement keywords
 */

K_LOOP     : 'loop' | 'LOOP' | 'Loop' ;

K_WHILE    : 'while' | 'WHILE' | 'While' ;

K_IN       : 'in' | 'IN' | 'In' ;

K_OVER     : 'over' | 'OVER' | 'Over' ;

K_WITHIN   : 'within' | 'WITHIN' | 'Within' ;

K_IF       : 'if' | 'IF' | 'If' ;

K_ELSE     : 'else' | 'ELSE' | 'Else' ;

K_DEFINE   : 'define' | 'DEFINE' | 'Define'
           | 'def' | 'DEF' | 'Def' ;

K_PARALLEL : 'parallel' | 'PARALLEL' | 'Parallel'
           ;

K_CONST    : 'constant' | 'CONSTANT' | 'Constant'
           | 'const' | 'CONST' | 'Const' ;

K_VAR      : 'variable' | 'VARIABLE' | 'Variable'
           | 'var' | 'VAR' | 'Var' ;

K_CASE     : 'case' | 'CASE' | 'Case' ;

K_OF       : 'of' | 'OF' | 'Of' ;

K_DEFAULT  : 'default' | 'DEFAULT' | 'Default' ;

K_LEAVE    : 'leave' | 'LEAVE' | 'Leave' ;

K_NEXT     : 'next' | 'NEXT' | 'Next' ;

K_WAIT     : 'wait' | 'WAIT' | 'Wait' ;

K_TIMETHIS : 'timethis' | 'TIMETHIS' | 'TimeThis' | 'Timethis' | 'timeThis' ;


/* This has to include the localvar and globalvar variants */
WILD_ID : ( '$' | NAME_START_CHAR | '?' | '*' ) ( '#' | NAME_CHAR | '?' | '*' ) * {allowWild}?
        ;

/* Note: this needs to be last so that these other "ID" like things
 * will be recognized first. */

ID     : NAME_START_CHAR NAME_CHAR *
       ;

DOTS
       : '...'
       | '..'
       | '\u2026'
       ;

DOT    : '.' ;
LPAREN : '(' ;
RPAREN : ')' ;
LBRACE : '{' ;
RBRACE : '}' ;
LBRACK : '[' ;
RBRACK : ']' ;
COMMA  : ',' ;
COLON  : ':' ;
QUEST  : '?' ;

GLOBALVAR
       : '$' INT
       | '$#'
       | '$*'
       | '_#'
       | '_*'
       | '__'
       ;

TO_STRING_OP
       : '@@'
       ;

TO_NUM_OP
       : '~~'
       ;

BIT_NOT_OP
       : '~'
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
       | ( '\\' | '\u2216' )
       | '%'
       ;

POW_ASSIGN
       : ( '**=' | '\u00D7\u00D7=' | '\u2217\u2217=' | '\u2715\u2715=' | '\u2716\u2716=' )
       ;

MULT_ASSIGN
       : ( '*=' | '\u00D7=' | '\u2217=' | '\u2715=' | '\u2716=' )
       | ( '/=' | '\u00F7=' | '\u2215=' | '\u2797=' )
       | ( '\\=' | '\u2216=' )
       | '%='
       ;

ADD_ASSIGN
       : ( '+=' | '\u2795=' )
       | ( '-=' | '\u2212=' | '\u2796=' )
       ;

EQUAL_OP
       : ( '===' | '\u2A76' | '\u2261' )
       | ( '!==' | '\u2262' )
       | ( '==' | '\u2A75' )
       | ( '!=' | '<>' | '\u2260' )
       ;

SPACE_OP
       : '<=>'
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

ELVIS_OP
       : '?:'
       | '?!'
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

D_GRADS
   : DIR  ( 'gradians' | 'GRADIANS' | 'Gradians' )
   | DIR  ( 'grads'    | 'GRADS'    | 'Grads'    )
   | DIR  ( 'grad'     | 'GRAD'     | 'Grad'     )
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
   : DIR  ( 'clear' | 'CLEAR' | 'Clear' ) { allowWild = true; }
   | DIR  ( 'clr'   | 'CLR'   | 'Clr'   ) { allowWild = true; }
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
   : DIR  ( 'variables' | 'VARIABLES' | 'Variables' ) { allowWild = true; }
   | DIR  ( 'variable'  | 'VARIABLE'  | 'Variable'  ) { allowWild = true; }
   | DIR  ( 'vars'      | 'VARS'      | 'Vars'      ) { allowWild = true; }
   | DIR  ( 'var'       | 'VAR'       | 'Var'       ) { allowWild = true; }
   ;

D_PREDEFINED
   : DIR  ( 'predefined' | 'PREDEFINED' | 'Predefined' ) { allowWild = true; }
   | DIR  ( 'predefs'    | 'PREDEFS'    | 'Predefs'    ) { allowWild = true; }
   | DIR  ( 'predef'     | 'PREDEF'     | 'Predef'     ) { allowWild = true; }
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

D_SORTOBJECTS
   : DIR  ( 'sortobjects' | 'SORTOBJECTS' | 'Sortobjects' | 'SortObjects' )
   | DIR  ( 'sortobject'  | 'SORTOBJECT'  | 'Sortobject'  | 'SortObject'  )
   | DIR  ( 'sortkeys'    | 'SORTKEYS'    | 'Sortkeys'    | 'SortKeys'    )
   | DIR  ( 'sortkey'     | 'SORTKEY'     | 'Sortkey'     | 'SortKey'     )
   ;

D_COLORS
   : DIR  ( 'colors' | 'COLORS' | 'Colors' )
   | DIR  ( 'color'  | 'COLOR'  | 'Color'  )
   ;

D_REQUIRE
   : DIR  ( 'requires' | 'REQUIRES' | 'Requires' )
   | DIR  ( 'require'  | 'REQUIRE'  | 'Require' )
   ;

D_ASSERT
   : DIR  ( 'assert' | 'ASSERT' | 'Assert' )
   ;


FORMAT
   : '@' [\-+] ? INT ? ( '.' INT ? ( '.' INT ) ? ) ? [a-zA-Z,_] ? [a-zA-Z%$]
   ;

STRING
   :      '"' ( ESC1 | SAFECODEPOINT1 ) * '"'
   |     '\'' ( ESC2 | SAFECODEPOINT2 ) * '\''
   | '\u2018' ( ESC3 | SAFECODEPOINT3 ) * '\u2019'
   | '\u201C' ( ESC4 | SAFECODEPOINT4 ) * '\u201D'
   | '\u2039' ( ESC5 | SAFECODEPOINT5 ) * '\u203A'
   | '\u00AB' ( ESC6 | SAFECODEPOINT6 ) * '\u00BB'
// multiline strings
   |                '"""' ( ESC1 | SAFECODEPOINT7 ) * '"""'
   |             '\'\'\'' ( ESC2 | SAFECODEPOINT8 ) * '\'\'\''
   | '\u2018\u2018\u2018' ( ESC3 | SAFECODEPOINT9 ) * '\u2019\u2019\u2019'
   | '\u201C\u201C\u201C' ( ESC4 | SAFECODEPOINTA ) * '\u201D\u201D\u201D'
   | '\u2039\u2039\u2039' ( ESC5 | SAFECODEPOINTB ) * '\u203A\u203A\u203A'
   | '\u00AB\u00AB\u00AB' ( ESC6 | SAFECODEPOINTC ) * '\u00BB\u00BB\u00BB'
// raw strings
   | 's"' (~'"') * '"'
   | 's\'' (~'\'') * '\''
   | 's\u2018' (~'\u2019') * '\u2019'
   | 's\u201C' (~'\u201D') * '\u201D'
   | 's\u2039' (~'\u203A') * '\u203A'
   | 's\u00AB' (~'\u00BB') * '\u00BB'
   ;

ISTRING
   : '`'  ( ESCI | SAFECODEPOINTI ) * '`'
   ;

fragment ESC1
   : ESC ( ["] | ESCAPES | UNICODE | CARET )
   ;

fragment ESC2
   : ESC ( ['] | ESCAPES | UNICODE | CARET )
   ;

fragment ESC3
   : ESC ( '\u2019' | ESCAPES | UNICODE | CARET )
   ;

fragment ESC4
   : ESC ( '\u201D' | ESCAPES | UNICODE | CARET )
   ;

fragment ESC5
   : ESC ( '\u203A' | ESCAPES | UNICODE | CARET )
   ;

fragment ESC6
   : ESC ( '\u00BB' | ESCAPES | UNICODE | CARET )
   ;

fragment ESCI
   : ESC ( [`] | ESCAPES | UNICODE | CARET )
   ;

fragment ESCAPES
   : [\\bfnrt0/]
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

fragment CARET
   : 'c' [a-zA-Z]
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

fragment SAFECODEPOINT7
   : ~ ["\\\u0000-\u0009\u000b\u000c\u000e-\u001F]
   ;

fragment SAFECODEPOINT8
   : ~ ['\\\u0000-\u0009\u000b\u000c\u000e-\u001F]
   ;

fragment SAFECODEPOINT9
   : ~ [\\\u0000-\u0009\u000b\u000c\u000e-\u001F\u2019]
   ;

fragment SAFECODEPOINTA
   : ~ [\\\u0000-\u0009\u000b\u000c\u000e-\u001F\u201D]
   ;

fragment SAFECODEPOINTB
   : ~ [\\\u0000-\u0009\u000b\u000c\u000e-\u001F\u203A]
   ;

fragment SAFECODEPOINTC
   : ~ [\\\u0000-\u0009\u000b\u000c\u000e-\u001F\u00BB]
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

VERSION
   : INT '.' INT '.' INT
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
   : INT ( [Kk] | [Mm] | [Gg] | [Tt] | [Pp] | [Ee] | [Zz] | [Yy] | [Bb] ) ( [Ii] ? [Bb] ) ?
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
   : NL {allowWild = false;}
   ;

ENDEXPR
   : ';' {allowWild = false;}
   ;

