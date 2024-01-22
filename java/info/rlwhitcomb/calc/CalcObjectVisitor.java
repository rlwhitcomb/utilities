/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2024 Roger L. Whitcomb.
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
 *  History:
 *	04-Dec-2020 (rlwhitcomb)
 *	    Initial coding, not complete.
 *	06-Dec-2020 (rlwhitcomb)
 *	    More functionality.
 *	07-Dec-2020 (rlwhitcomb)
 *	    Help and Version directives; add some color.
 *	07-Dec-2020 (rlwhitcomb)
 *	    Degrees and radians directives.
 *	07-Dec-2020 (rlwhitcomb)
 *	    Cache "e" value when precision changes.
 *	08-Dec-2020 (rlwhitcomb)
 *	    Use new NumericUtil.sin method; add "round" evaluation.
 *	11-Dec-2020 (rlwhitcomb)
 *	    Bit and shift operations implemented. Refactoring for error
 *	    handling. More arithmetic functions (GCD, MIN, MAX).
 *	    Implement more result formatting. Binary, octal, and hex
 *	    constants.
 *	11-Dec-2020 (rlwhitcomb)
 *	    Join operator.
 *	11-Dec-2020 (rlwhitcomb)
 *	    Trap division by zero.
 *	11-Dec-2020 (rlwhitcomb)
 *	    Refactor for GUI mode; implement ! and ~.
 *	14-Dec-2020 (rlwhitcomb)
 *	    Octal and Binary output format.
 *	14-Dec-2020 (rlwhitcomb)
 *	    Output to KB, MB, size format.
 *	15-Dec-2020 (rlwhitcomb)
 *	    Binary or SI directives.
 *	16-Dec-2020 (rlwhitcomb)
 *	    Implement fib(n) and $echo directive.
 *	16-Dec-2020 (rlwhitcomb)
 *	    Implement KB, MB, etc. inputs.
 *	17-Dec-2020 (rlwhitcomb)
 *	    Implement object and array.
 *	18-Dec-2020 (rlwhitcomb)
 *	    Start to implement assignment to array / map elements.
 *	19-Dec-2020 (rlwhitcomb)
 *	    Rework fib().
 *	19-Dec-2020 (rlwhitcomb)
 *	    Regularize the exit process.
 *	20-Dec-2020 (rlwhitcomb)
 *	    Fix the recursive print of objects inside arrays.
 *	21-Dec-2020 (rlwhitcomb)
 *	    Change the way we do exit and help commands in REPL mode.
 *	24-Dec-2020 (rlwhitcomb)
 *	    Implement EitherOr expression. Allow $clear to do a set
 *	    of variables also.
 *	24-Dec-2020 (rlwhitcomb)
 *	    $debug directive.
 *	28-Dec-2020 (rlwhitcomb)
 *	    Interpolated strings.
 *	28-Dec-2020 (rlwhitcomb)
 *	    Fix screwed up result from visitExprStmt.
 *	28-Dec-2020 (rlwhitcomb)
 *	    Tweak result formatting again.
 *	30-Dec-2020 (rlwhitcomb)
 *	    Tweak the "toString" methods for Map and List for
 *	    the empty value case.
 *	31-Dec-2020 (rlwhitcomb)
 *	    Do the expensive e/pi calculations in a background thread
 *	    using the CalcPiWorker class.
 *	31-Dec-2020 (rlwhitcomb)
 *	    Get nested object/array referencing right.
 *	04-Jan-2021 (rlwhitcomb)
 *	    Add the "not" bit operations, and boolean XOR.
 *	    Fix the hex, octal and binary formats with negative values.
 *	04-Jan-2021 (rlwhitcomb)
 *	    Implement some "pretty" printing of object / array values.
 *	05-Jan-2021 (rlwhitcomb)
 *	    Fix the "strings" case of object references.
 *	05-Jan-2021 (rlwhitcomb)
 *	    Add the "$include" directive.
 *	05-Jan-2021 (rlwhitcomb)
 *	    Allow unlimited precision setting (still limit pi to 12,000 digits though).
 *	07-Jan-2021 (rlwhitcomb)
 *	    New handling of "mode" directives; add "$resultsonly".
 *	07-Jan-2021 (rlwhitcomb)
 *	    Move common methods into CalcUtil.
 *	    Start of the +=, -=, etc. assign operators.
 *	07-Jan-2021 (rlwhitcomb)
 *	    The rest of the assign operators.
 *	    Reduce common code.
 *	08-Jan-2021 (rlwhitcomb)
 *	    Implement loop construct.
 *	09-Jan-2021 (rlwhitcomb)
 *	    Implement ln2 and isprime.
 *	10-Jan-2021 (rlwhitcomb)
 *	    Quiet mode directive. Refactor the context mode processing.
 *	11-Jan-2021 (rlwhitcomb)
 *	    No need to display action messages for the display modes (IMO), since the
 *	    effect will be fairly obvious by the subsequent displays.
 *	12-Jan-2021 (rlwhitcomb)
 *	    Use NumericUtil.cos() now.
 *	15-Jan-2021 (rlwhitcomb)
 *	    Make loop over an expression list, arrays, and maps work too.
 *	    Fix looping with a negative step value and check infinite loop conditions.
 *	15-Jan-2021 (rlwhitcomb)
 *	    Fix operator precedence.
 *	16-Jan-2021 (rlwhitcomb)
 *	    Use NumericUtil.sqrt().
 *	18-Jan-2021 (rlwhitcomb)
 *	    Change/fix the way "round()" works, so that the number of places is the
 *	    number of decimal places, thus independent of the scale of the value.
 *	18-Jan-2021 (rlwhitcomb)
 *	    Allow "loop" to use fractional values (not just integers) for start, end, step.
 *	18-Jan-2021 (rlwhitcomb)
 *	    Use NumericUtil.cbrt().
 *	18-Jan-2021 (rlwhitcomb)
 *	    Put the action messages in the resources.
 *	19-Jan-2021 (rlwhitcomb)
 *	    Add "length" and "scale" functions.
 *	20-Jan-2021 (rlwhitcomb)
 *	    Adjust compare / equal operator precedence.
 *	26-Jan-2021 (rlwhitcomb)
 *	    Allow access from LValueContext to "getStringValue". And now that we have
 *	    dynamic access to map members, make "loop" over map return keys, not values.
 *	28-Jan-2021 (rlwhitcomb)
 *	    Allow "loop" over the characters (codepoints) in a String.
 *	28-Jan-2021 (rlwhitcomb)
 *	    Add LCM function.
 *	28-Jan-2021 (rlwhitcomb)
 *	    Add Bernoulli number function.
 *	30-Jan-2021 (rlwhitcomb)
 *	    Introduce rational / fraction mode. Start doing rational calculations.
 *	31-Jan-2021 (rlwhitcomb)
 *	    Have to pass the MathContext around to more places.
 *	01-Feb-2021 (rlwhitcomb)
 *	    Set rational mode on the command line, and pass to constructor here.
 *	    Trap arithmetic exception for rational divide also.
 *	    Recognize the Unicode NOT EQUAL and NOT IDENTICAL characters.
 *	02-Feb-2021 (rlwhitcomb)
 *	    Implement GCD and LCM for rational mode.
 *	    More tweaking for rational mode.
 *	    Catch exception in LCM.
 *	03-Feb-2021 (rlwhitcomb)
 *	    Use tan() from NumericUtil. Allow variable reference in "numberOption"
 *	    and "modeOption".
 *	16-Feb-2021 (rlwhitcomb)
 *	    Add "if" and "while" statements, and make them into expressions.
 *	    Add "define" statement and implement.
 *	17-Feb-2021 (rlwhitcomb)
 *	    Add "this" parameter to various methods in order to (completely) implement
 *	    the recursion necessary for nested function evaluation.
 *	19-Feb-2021 (rlwhitcomb)
 *	    Illegal format error. Implement percent format precision.
 *	22-Feb-2021 (rlwhitcomb)
 *	    Tweak the processing of "$" inside interpolated strings so that "$$var"
 *	    will also get the "$var" value instead of having to do "${$var}" for it.
 *	22-Feb-2021 (rlwhitcomb)
 *	    Add "eval" function.
 *	22-Feb-2021 (rlwhitcomb)
 *	    Refactor "loopvar" to "localvar".
 *	23-Feb-2021 (rlwhitcomb)
 *	    Add ":timing" directive.
 *	01-Mar-2021 (rlwhitcomb)
 *	    More Unicode equivalents. Correctly convert escape sequences in strings.
 *	02-Mar-2021 (rlwhitcomb)
 *	    Eval looks better if the internal calculation is silent even if that means
 *	    we can't see the string it is executing; this is the same (now) as functions.
 *	    Fix "eval func" case; although it is not silent...
 *	03-Mar-2021 (rlwhitcomb) Issue #9
 *	    Fix silent setting doing "eval" of a function.
 *	04-Mar-2021 (rlwhitcomb)
 *	    Add "FACTORS" function.
 *	05-Mar-2021 (rlwhitcomb)
 *	    Add "PFACTORS" function now that the code works in NumericUtil.
 *	08-Mar-2021 (rlwhitcomb)
 *	    Get the "silent" operation right for functions everywhere.
 *	08-Mar-2021 (rlwhitcomb)
 *	    Implement "sumOf" and "productOf" functions.
 *	08-Mar-2021 (rlwhitcomb)
 *	    Make the same recursive changes for min/max and join.
 *	09-Mar-2021 (rlwhitcomb)
 *	    One more level of recursion is necessary in "getFirstValue".
 *	    Add Javadoc for the min/max/join list helpers.
 *	09-Mar-2021 (rlwhitcomb)
 *	    Handle alternate argument lists for "FRAC".
 *	15-Mar-2021 (rlwhitcomb)
 *	    Trap ArithmeticException in "FRAC".
 *	18-Mar-2021 (rlwhitcomb)
 *	    Regularize the uppercasing of results with formats.
 *	23-Mar-2021 (rlwhitcomb)
 *	    Add upper/lower functions and @u, @l formats.
 *	24-Mar-2021 (rlwhitcomb)
 *	    Support for Roman Numeral input and output, and the ROMAN function.
 *	24-Mar-2021 (rlwhitcomb)
 *	    Trap errors on Roman constant conversion to get nicer errors.
 *	24-Mar-2021 (rlwhitcomb)
 *	    Add fourth root function ("fort").
 *	24-Mar-2021 (rlwhitcomb)
 *	    Add Unicode two- and three-equals sign symbols.
 *	24-Mar-2021 (rlwhitcomb)
 *	    One more Unicode "identical to" symbol.
 *	25-Mar-2021 (rlwhitcomb)
 *	    Add the "FILL" function.
 *	25-Mar-2021 (rlwhitcomb)
 *	    Bug fix for "getStringValue" of a Map (affects "eval func" where "func"
 *	    is a function defined as an object).
 *	26-Mar-2021 (rlwhitcomb)
 *	    Move some methods from NumericUtil to MathUtil.
 *	27-Mar-2021 (rlwhitcomb)
 *	    Add "epow" function, using new MathUtil method.
 *	28-Mar-2021 (rlwhitcomb)
 *	    Allow precision on all format arguments, and implement (now) for @d.
 *	29-Mar-2021 (rlwhitcomb)
 *	    Calculate ln2 from our own method.
 *	30-Mar-2021 (rlwhitcomb)
 *	    Calculate ln from our own method.
 *	01-Apr-2021 (rlwhitcomb)
 *	    Regularize settings so we can use a GUI dialog to set them.
 *	05-Apr-2021 (rlwhitcomb)
 *	    Catch exceptions in the root and log functions.
 *	06-Apr-2021 (rlwhitcomb)
 *	    Add "INDEX" and "SUBSTR" functions.
 *	07-Apr-2021 (rlwhitcomb)
 *	    Add "EXEC" function.
 *	07-Apr-2021 (rlwhitcomb)
 *	    Add "SPLIT" function.
 *	07-Apr-2021 (rlwhitcomb)
 *	    Add "TRIM" functions; add Unicode powers.
 *	08-Apr-2021 (rlwhitcomb)
 *	    Add time constants and time/duration formatting. Move "round" to MathUtil
 *	    for more general use.
 *	12-Apr-2021 (rlwhitcomb)
 *	    Fix problem with the fixed-size array returned from "split".
 *	20-Apr-2021 (rlwhitcomb)
 *	    Partial implementation of function parameters.
 *	20-Apr-2021 (rlwhitcomb)
 *	    Tweak spacing of expression format.
 *	20-Apr-2021 (rlwhitcomb)
 *	    Add :VARIABLES directive.
 *	20-Apr-2021 (rlwhitcomb)
 *	    Put text around variables list in non-REPL mode.
 *	21-Apr-2021 (rlwhitcomb)
 *	    Implement "CASE" statement; rename lexical tokens.
 *	21-Apr-2021 (rlwhitcomb)
 *	    Ignore empty statements in a block.
 *	22-Apr-2021 (rlwhitcomb)
 *	    Now that EOF is required at "prog" level, we need to explicitly handle "prog"
 *	    here (for "processString" to work right again). Also for "eval", return a
 *	    value from all directives, and implement "visitStmt" here for that to work.
 *	22-Apr-2021 (rlwhitcomb)
 *	    Revamp our whole top-level grammar to allow newlines in a lot more places.
 *	26-Apr-2021 (rlwhitcomb)
 *	    Implement "," formatting for "d" and "%" formats.
 *	28-Apr-2021 (rlwhitcomb)
 *	    Put "and" into the list of cleared variables when needed.
 *	28-Apr-2021 (rlwhitcomb)
 *	    More Unicode math symbols.
 *	29-Apr-2021 (rlwhitcomb)
 *	    Catch out of bounds exception in "substr".
 *	13-May-2021 (rlwhitcomb)
 *	    Date values, arithmetic, and formatting.
 *	20-May-2021 (rlwhitcomb)
 *	    Fix negative date parsing.
 *	21-May-2021 (rlwhitcomb)
 *	    Introduce US format dates.
 *	21-May-2021 (rlwhitcomb)
 *	    DOW, TODAY, and NOW functions.
 *	07-Jun-2021 (rlwhitcomb)
 *	    Use not-quite-unlimited precision for divide operations.
 *	02-Jul-2021 (rlwhitcomb)
 *	    Implement "always displaying thousands separators" mode.
 *	10-Jul-2021 (rlwhitcomb)
 *	    Implement ignore variable / member name case.
 *	27-Jul-2021 (rlwhitcomb)
 *	    Start on fractional powers.
 *	27-Jul-2021 (rlwhitcomb)
 *	    More work on powers of fractions.
 *	27-Jul-2021 (rlwhitcomb)
 *	    Fix #13 - parse/format of negative years.
 *	04-Aug-2021 (rlwhitcomb)
 *	    Support "yes" and "no" for mode options.
 *	04-Aug-2021 (rlwhitcomb)
 *	    For "@d" conversions if the input is a single codepoint,
 *	    convert the codepoint and print the numeric value.
 *	06-Aug-2021 (rlwhitcomb)
 *	    Finish moving the fraction parsing to BigFraction entirely.
 *	09-Aug-2021 (rlwhitcomb)
 *	    Implement dot range for "length", "sumof", and "productof".
 *	    Put loop value into special "$_" variable if none specified.
 *	11-Aug-2021 (rlwhitcomb)
 *	    Add integer divide "\" and "\=".
 *	11-Aug-2021 (rlwhitcomb)
 *	    Add "CHARS" function to break up a string into an array of codepoints.
 *	12-Aug-2021 (rlwhitcomb)
 *	    In the Variables list, for a function list the body, not the value since
 *	    many times the function shouldn't be executed at this time, and the
 *	    definition is more germane in this setting.
 *	12-Aug-2021 (rlwhitcomb)
 *	    More date functions.
 *	16-Aug-2021 (rlwhitcomb)
 *	    Need to use "mcDivide" for Bernoulli numbers to avoid infinite repeating digits errors.
 *	23-Aug-2021 (rlwhitcomb)
 *	    Fix precision value for formatting.
 *	24-Aug-2021 (rlwhitcomb)
 *	    Implement "@c" formatting (integer -> character).
 *	25-Aug-2021 (rlwhitcomb)
 *	    Fix the parsing of a couple of functions when optional parens are given. Fix a LOT of
 *	    weirdness with "getStringValue" when separators and quotes were improperly handled.
 *	25-Aug-2021 (rlwhitcomb)
 *	    Add "setArgument" for global variables.
 *	26-Aug-2021 (rlwhitcomb)
 *	    Add number constant conversion, and "isnull" function.
 *	01-Sep-2021 (rlwhitcomb)
 *	    Attempt to convert global variables to numbers if possible, otherwise leave as strings.
 *	02-Sep-2021 (rlwhitcomb)
 *	    Don't convert result to a string if "silent" because we won't display that string anyway.
 *	02-Sep-2021 (rlwhitcomb)
 *	    Issue #16: Change cutover for two-digit years to "today + 30 years" instead of 50.
 *	    Issue #10: Use BigInteger for duration conversions.
 *	08-Sep-2021 (rlwhitcomb)
 *	    Allow ISTRING for member names.
 *	10-Sep-2021 (rlwhitcomb)
 *	    #21 Fix the way "join" works with maps and lists.
 *	20-Sep-2021 (rlwhitcomb)
 *	    Add "tenpow" function (like "epow"). Add "fixup" calls to strip trailing zeros.
 *	26-Sep-2021 (rlwhitcomb)
 *	    Refine the error context for multiply/divide errors.
 *	04-Oct-2021 (rlwhitcomb)
 *	    Implement ":save" directive, with charset selection for it and ":open". Rename some
 *	    static final values as the constants they really are. Use LinkedHashMap for variables
 *	    so that the key sets list in the order they were defined.
 *	05-Oct-2021 (rlwhitcomb)
 *	    Split out "saveVariables" method to be called from GUI button code.
 *	06-Oct-2021 (rlwhitcomb)
 *	    #24 Full implementation of function parameters. Massive rewrite to use Scope.
 *	07-Oct-2021 (rlwhitcomb)
 *	    #24 Fix places that create List to convert to ArrayScope.
 *	    Context parameter for "toStringValue", new "setupFunctionCall" method, add
 *	    context also to "evaluateFunction", and "saveVariables". Add data type param
 *	    to ArrayScope, and change the way we initialize ArrayScope from regular lists.
 *	08-Oct-2021 (rlwhitcomb)
 *	    Add format to convert an integer value to words.
 *	14-Oct-2021 (rlwhitcomb)
 *	    Allow the "mode" keywords as ID values.
 *	15-Oct-2021 (rlwhitcomb)
 *	    #32: Fix arg parsing precedence with single-arg predefined functions.
 *	    Fix "substr" so we don't get index errors.
 *	    New "slice" and "splice" functions (equivalent to JavaScript). Enhance "substr" also
 *	    to perform sensibly with only one argument.
 *	16-Oct-2021 (rlwhitcomb)
 *	    #33: Make the convention that a bare reference to a function that was defined with parameters
 *	    is NOT a call to that function, but just a reference to it, then our problem is solved.
 *	    Implement "sort", and add "ignoreCase" parameter to base "compareValues" function.
 *	    Use "buildValueList" for "sort" so it works better for multi-dim arrays. Change the way
 *	    value conversion is done for that method.
 *	19-Oct-2021 (rlwhitcomb)
 *	    #35: Add "replace" function for strings. For options set using variables, throw an error
 *	    if the variable is not defined (makes more sense in that other values are keywords, and this
 *	    could just be a typo ("pre" instead of "prev") so saying '"pre" is undefined' is more user-friendly).
 *	    #34: Completely rewrite "splice" with new syntax for objects that makes sense, and not allowing
 *	    non-object, non-array values.
 *	20-Oct-2021 (rlwhitcomb)
 *	    #37: Currency format.
 *	21-Oct-2021 (rlwhitcomb)
 *	    #40: Use locale-based "%" formatter.
 *	23-Oct-2021 (rlwhitcomb)
 *	    #42: Implement decode and encode (base64) functions.
 *	25-Oct-2021 (rlwhitcomb)
 *	    #46: Implement "versioninfo" structure.
 *	27-Oct-2021 (rlwhitcomb)
 *	    #45: Implement "read" function.
 *	28-Oct-2021 (rlwhitcomb)
 *	    Revise CASE syntax a little bit to make "default" work better.
 *	    Implement predefined values very differently.
 *	02-Nov-2021 (rlwhitcomb)
 *	    #57: Implement "@+nns" formatting.
 *	03-Nov-2021 (rlwhitcomb)
 *	    #69: Introduce "$*" and "$#" global variables.
 *	    Don't clear "$..." variables in ":clear", nor display by ":variables", nor write in ":save".
 *	04-Nov-2021 (rlwhitcomb)
 *	    #71: Use "natural" ordering for "sort".
 *	07-Nov-2021 (rlwhitcomb)
 *	    #73: Fix '@s' formatting.
 *	    #69: Implement "$#" and "$*" for function parameters, and varargs in param lists.
 *	09-Nov-2021 (rlwhitcomb)
 *	    #62: Don't return inside the finally block inside "iterateOverDotRange"
 *	    #78: use same string compare as "sort" for "min" and "max".
 *	12-Nov-2021 (rlwhitcomb)
 *	    #81: Add directive to quote strings (or not).
 *	16-Nov-2021 (rlwhitcomb)
 *	    #87: Strip any quotes from incoming string argument values.
 *	    #85: Trap exceptions and wrap in our own during "exec" call.
 *	    #86: Change "versioninfo" to just "info" and add "os" and "java" parts.
 *	17-Nov-2021 (rlwhitcomb)
 *	    #96: add "this" to "getContextObject" calls so that LHS functions can be
 *	    evaluated.
 *	18-Nov-2021 (rlwhitcomb)
 *	    #95: Add constant predefined values for "phi" and "PHI" (the Golden Ratio
 *	    and its reciprocal).
 *	03-Dec-2021 (rlwhitcomb)
 *	    #95: Add new "ratphi" function for rational approximations of "phi" and "PHI".
 *	13-Dec-2021 (rlwhitcomb)
 *	    #129: Check for ".bat" or ".cmd" file for "exec" and automatically call "cmd /c".
 *	14-Dec-2021 (rlwhitcomb)
 *	    #106: Add "leave" statement to exit loops and functions.
 *	15-Dec-2021 (rlwhitcomb)
 *	    #151: Fix precedence of the logical operators.
 *	18-Dec-2021 (rlwhitcomb)
 *	    #159: Silence directives on command.
 *	24-Dec-2021 (rlwhitcomb)
 *	    #125: Add new Java version fields to "info.java".
 *	27-Dec-2021 (rlwhitcomb)
 *	    #125: Changed order of "info.java" fields.
 *	    #170: Switch "length" and "scale" computation.
 *	    #176: Directives shouldn't affect a return value.
 *	28-Dec-2021 (rlwhitcomb)
 *	    #183: Introduce '@q' to deliver unquoted strings, no matter the settings.
 *	    #188: Add "ceil" and "floor" functions.
 *	    #137: Add "reverse" function.
 *	    #128: Add "lpad", "pad", and "rpad" functions.
 *	31-Dec-2021 (rlwhitcomb)
 *	    Refactor the "getCharValue" code into a single method. Refactor "fill"
 *	    a bit to make only the variable mandatory.
 *	    #180: Use nn.mm on @j notation to specify indent size and increment,
 *	    and "-" to eliminate leading newline. Refactor parameters on "toStringValue".
 *	01-Jan-2022 (rlwhitcomb)
 *	    #178: Use current "silent" setting for ":include", instead of "false".
 *	    #175: More precise message going back to decimal mode (with precision).
 *	    #177: Save current program version as part of saved variables (to ensure compatibility).
 *	04-Jan-2022 (rlwhitcomb)
 *	    #194: New library version description in ":save".
 *	05-Jan-2022 (rlwhitcomb)
 *	    #182: Do the redirection of predefined values inside "evaluateFunction" as a common
 *	    location to get it done everywhere it is necessary. Define the subobjects of "info"
 *	    as predefined values themselves, to avoid redefinition.
 *	08-Jan-2022 (rlwhitcomb)
 *	    #183: Change '@Q' to double quote the result (whereas '@q' gets rid of quotes).
 *	    Enable '@q' and '@Q' for arrays and objects.
 *	10-Jan-2022 (rlwhitcomb)
 *	    #153: Add "setVariable" method, break out conversions to "stringToValue" method.
 *	    #108: Add more aliases for "null" as predefined values.
 *	14-Jan-2022 (rlwhitcomb)
 *	    Fix "frac" with one argument to convert anything to a fraction (not just a string).
 *	17-Jan-2022 (rlwhitcomb)
 *	    #130: Add "info.locale" object with relevant information.
 *	    #125: Add timezone information to "info" also.
 *	18-Jan-2022 (rlwhitcomb)
 *	    #211: Add "typeof" operator.
 *	19-Jan-2022 (rlwhitcomb)
 *	    #214: Add "cast" operator.
 *	20-Jan-2022 (rlwhitcomb)
 *	    #215: Enhance "@d" formatting to use "scale" for left padding with zeros.
 *	    Broaden "pad" functions to convert numbers, etc. to strings.
 *	21-Jan-2022 (rlwhitcomb)
 *	    Add "libversion" with the base implementation version of the library to "info".
 *	    #135: Add "const" values.
 *	22-Jan-2022 (rlwhitcomb)
 *	    #220: Don't output a message for ":clear pi" (for instance) where nothing gets cleared.
 *	    #216: Add "format" function; try to keep primitive numbers as integers if possible.
 *	    Add more environment-related values to the "info.os" object.
 *	24-Jan-2022 (rlwhitcomb)
 *	    #103: Start to implement complex number support.
 *	    #223: Implement ":predefined" command.
 *	    Move "stringToValue" out to CalcUtil.
 *	26-Jan-2022 (rlwhitcomb)
 *	    #206: Refactor by moving predefined variable init to separate file; also Settings and TrigMode.
 *	    #227: Add "timethis" statement.
 *	30-Jan-2022 (rlwhitcomb)
 *	    #229: Fix calling of functions with missing / defaulted parameters.
 *	31-Jan-2022 (rlwhitcomb)
 *	    #212: Changes to "typeof" to work right with functions.
 *	    #103: Implement complex number to real powers; complex number roots; '@i' formatting.
 *	01-Feb-2022 (rlwhitcomb)
 *	    #231: Move some constants out to Constants class.
 *	02-Feb-2022 (rlwhitcomb)
 *	    #115: Supply a read-only view of the Settings to CalcPredefine for "info.settings".
 *	    #115: Move "mc" and "mcDivide" into Settings.
 *	    #234: Convert integer loop veriables to BigInteger so that "===" works inside loops
 *		  against the index var.
 *	03-Feb-2022 (rlwhitcomb)
 *	    #230: Add id list to ":predefs", allow wildcards in ":vars", ":clear", and now ":predefs" too.
 *	04-Feb-2022 (rlwhitcomb)
 *	    Refactor a bit and fix bugs with return value of ":clear", ":vars", and ":predefs".
 *	    #237: Tiny fix to not list duplicates because of "$*" as a predefined value.
 *	05-Feb-2022 (rlwhitcomb)
 *	    #233: Implement SystemValue as a way to set value via the "settings" object.
 *	    Fix one-argument "complex".
 *	    #144: Implement "matches" standalone function and case selector.
 *	07-Feb-2022 (rlwhitcomb)
 *	    #239: Add "compareOp expr" as another caseSelector.
 *	08-Feb-2022 (rlwhitcomb)
 *	    #235: Implement atan2() ourselves. Add "@p" formatting.
 *	10-Feb-2022 (rlwhitcomb)
 *	    Oops! Implement "modulus" for fractions.
 *	11-Feb-2022 (rlwhitcomb)
 *	    #245: Fix stacking of the "settings" values, plus quiet mode during functions.
 *	13-Feb-2022 (rlwhitcomb)
 *	    #199: Refactoring of values to allow arbitrary "id" for loop variables and parameters.
 *	15-Feb-2022 (rlwhitcomb)
 *	    #169: New version of evaluateFunction with just the one parameter (context).
 *	    But also create a special flag to NOT do the zero-arg call during parameter evaluation
 *	    so that we can pass zero-arg functions as parameters successfully.
 *	    #249: Add "expr IN loopCtl" as another expression type.
 *	    #252: Predefine the loop control value in the local symbol scope so the visitor
 *	    will find it during the block execution.
 *	18-Feb-2022 (rlwhitcomb)
 *	    #103: More support for complex numbers; some cleanup of "visit" -> "evaluateFunction".
 *	11-Apr-2022 (rlwhitcomb)
 *	    #267: Add "Elvis" operator.
 *	14-Apr-2022 (rlwhitcomb)
 *	    #273: Move math-related classes to "math" package.
 *	26-Apr-2022 (rlwhitcomb)
 *	    #290: Implement optional statement blocks for directives with mode options.
 *	03-May-2022 (rlwhitcomb)
 *	    #68: Reimplement "index" to work correctly with objects and lists, including
 *	    indexes of objects, and negative indexes.
 *	04-May-2022 (rlwhitcomb)
 *	    #300: Allow "@s" for objects and lists.
 *	    #308: Add "<>" as an alternative for "not equals".
 *	05-May-2022 (rlwhitcomb)
 *	    #296: Add "notnull" function.
 *	    #298: Add "within" keyword to "loop" statement and "in" expressions.
 *	06-May-2022 (rlwhitcomb)
 *	    #305: Change "chars" to "codes" and add new "chars" that separates a string
 *	    into a character array.
 *	    #287: Allow "define" and "const" at any level.
 *	07-May-2022 (rlwhitcomb)
 *	    #292: Add ":require" directive. Tweak saved file format to use it.
 *	    Turn off separator mode during ":save".
 *	    Use try/finally to make sure the "pop" happens during bracket block for
 *	    mode options.
 *	10-May-2022 (rlwhitcomb)
 *	    #316: Add reverse "Elvis" operator.
 *	11-May-2022 (rlwhitcomb)
 *	    #64: Separately operate on objects and lists in the case convert expression.
 *	    #318: Fix evaluation of ValueScope within the convertCase function. Rename
 *	    "evaluateFunction" to just "evaluate".
 *	    #319: Implement "!!" operator.
 *	12-May-2022 (rlwhitcomb)
 *	    #320: Implement case conversion with a new recursive method and a Transformer.
 *	13-May-2022 (rlwhitcomb)
 *	    #320: Need to rearrange code between Transformer and "copyAndTransform".
 *	    #320: Rework "trim" using Transformer.
 *	    #320: Rework "replace" also.
 *	15-May-2022 (rlwhitcomb)
 *	    #315: Implement pre- and post-inc/dec operators for objects and lists.
 *	17-May-2022 (rlwhitcomb)
 *	    #334: Fix the extra processing of ":echo" messages.
 *	18-May-2022 (rlwhitcomb)
 *	    #315: Protect "--" on empty lists and objects.
 *	20-May-2022 (rlwhitcomb)
 *	    #334: Part of "addQuotes" in formatting is "quoteControl" also.
 *	    #334: Maybe "@Q" shouldn't double the quotes.
 *	    #339: Move "cleanDecimal" to "fixupToInteger" and use it more places.
 *	21-May-2022 (rlwhitcomb)
 *	    #327: Add "unique" function.
 *	22-May-2022 (rlwhitcomb)
 *	    Simplify the grammar which gets rid of the slowdown from several versions ago.
 *	    #340: Use "Which.find" in "exec".
 *	23-May-2022 (rlwhitcomb)
 *	    #341: Add "~~" ("to number") operator.
 *	25-May-2022 (rlwhitcomb)
 *	    #348: Add "var" statement and clear local vars on each loop/while iteration.
 *	    #349: Fix "buildValueList" for sort; add context for error message.
 *	26-May-2022 (rlwhitcomb)
 *	    #320: Redo "matches" for arrays and objects to return similar objects whose
 *	    keys / values are matching.
 *	27-May-2022 (rlwhitcomb)
 *	    #320: Refactor the "copyNull" handling for Transformers.
 *	    Move "setupFunctionCall" out to FunctionDeclaration. More refactoring around
 *	    "evaluate" during parameter evaluation. Move "isPredefined", "saveVariables",
 *	    "copyAndTransform", and "buildValueList" out to CalcUtil.
 *	30-May-2022 (rlwhitcomb)
 *	    More places need to call "fixupToInteger".
 *	    #301: "convertToWords" accepts BigInteger.
 *	01-Jun-2022 (rlwhitcomb)
 *	    #45: Add "write" function.
 *	11-Jun-2022 (rlwhitcomb)
 *	    #365: Check for immutable arrays.
 *	15-Jun-2022 (rlwhitcomb)
 *	    #365: For constant objects display the string value of the object, not the "toString" value.
 *	    #191: Change "reverse" to return a new (modified) array as the result.
 *	20-Jun-2022 (rlwhitcomb)
 *	    #364: Allow ":echo" to output different places.
 *	23-Jun-2022 (rlwhitcomb)
 *	    #314: Add processing for sets.
 *	    Add recognition of "set minus" symbol.
 *	24-Jun-2022 (rlwhitcomb)
 *	    #373: Add "exists" function for files/directories.
 *	25-Jun-2022 (rlwhitcomb)
 *	    #314: Add set difference.
 *	27-Jun-2022 (rlwhitcomb)
 *	    #376: Move "exists" code to FileUtilities; add check for proper name case.
 *	29-Jun-2022 (rlwhitcomb)
 *	    #383: Display action message for "var" statement.
 *	    #381: Revamp sort completely to work nicely with maps and sets (including sort map by key or value).
 *	05-Jul-2022 (rlwhitcomb)
 *	    #291: Add optional flags to "matches".
 *	06-Jul-2022 (rlwhitcomb)
 *	    #388: Add same optional flags to case "matches" selector.
 *	07-Jul-2022 (rlwhitcomb)
 *	    #389: A "var id" declaration doesn't need an initial value expression. Fix the value quoting
 *	    on the action messages for both "const" and "var".
 *	08-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 *	10-Jul-2022 (rlwhitcomb)
 *	    #392: Option to sort objects by keys.
 *	11-Jul-2022 (rlwhitcomb)
 *	    #404: Fix wrong value for "quotes" in "@s" formatting.
 *	    #401: Return result of bracket block (if present) in "processModeOption".
 *	    Refactor some of the lexical tokens in the grammar to help with coding "isEmptyStmt".
 *	19-Jul-2022 (rlwhitcomb)
 *	    #412: Refactor parameters to "toStringValue".
 *	13-Jul-2022 (rlwhitcomb)
 *	    #314, #315: Actually, ++/-- of empty objects doesn't work.
 *	19-Jul-2022 (rlwhitcomb)
 *	    #417: Throw error on ":include" if the file is not found.
 *	24-Jul-2022 (rlwhitcomb)
 *	    #412: Add "skipLevels" to StringFormat.
 *	25-Jul-2022 (rlwhitcomb)
 *	    #412: Fix problem of duplicate initial indent with skip level > 0.
 *	29-Jul-2022 (rlwhitcomb)
 *	    #402: Move version check out to separate method in CalcUtil (for use on
 *	    command line).
 *	    #390: Turn on "quiet" mode inside CaseVisitor (same as for function evaluation)
 *	    so we only see the final "case" result.
 *	09-Aug-2022 (rlwhitcomb)
 *	    #436: Put out "define", "const", and "var" on :vars and :predefs.
 *	19-Aug-2022 (rlwhitcomb)
 *	    #439: Implement "next" statement in loop, while, and case.
 *	23-Aug-2022 (rlwhitcomb)
 *	    #452: Fix weird error due to "leave" not setting return value from function.
 *	    #455: Change "chars" and "codes" to deal differently depending on input value.
 *	    #459: Add "@@" (to string) operator.
 *	24-Aug-2022 (rlwhitcomb)
 *	    #454: Implement ":colors" directive.
 *	    #447: Add "grads" mode for trig calculations.
 *	    Move "I_MINUS_ONE" out to Constants.
 *	    Simplify date constant parsing.
 *	    Factor out conversion to LocalDate into a helper method.
 *	25-Aug-2022 (rlwhitcomb)
 *	    #466: Make normally ignored return values from "definition" statements more readable
 *	    (as return value from "eval").
 *	    #465: Add "delete" and "rename" functions.
 *	29-Aug-2022 (rlwhitcomb)
 *	    #453: Add "fileinfo" function.
 *	    #469: Update "has" function to search objects recursively.
 *	31-Aug-2022 (rlwhitcomb)
 *	    #453: Return empty object for FileInfo if it doesn't exist.
 *	08-Sep-2022 (rlwhitcomb)
 *	    #475: Add "caller(n)" function for doing stack tracing.
 *	12-Sep-2022 (rlwhitcomb)
 *	    #480: Update KB constant range to beyond exabytes (and extend to BigInteger).
 *	    Change '@K' to format using long names.
 *	14-Sep-2022 (rlwhitcomb)
 *	    #485: Add "mod" operator to multiply operator.
 *	    Implement "ceil" and "floor" for fractions.
 *	25-Sep-2022 (rlwhitcomb)
 *	    #426: Add "toDate" function.
 *	30-Sep-2022 (rlwhitcomb)
 *	    #496: Optional commas in "@w" format.
 *	03-Oct-2022 (rlwhitcomb)
 *	    #499: Set rational mode in CalcPiWorker right away during initialization.
 *	03-Oct-2022 (rlwhitcomb)
 *	    #497: Use new "divideContext" method to get working context for divisions.
 *	06-Oct-2022 (rlwhitcomb)
 *	    #501: Add "tobase" function.
 *	08-Oct-2022 (rlwhitcomb)
 *	    #501: Add "frombase" function.
 *	12-Oct-2022 (rlwhitcomb)
 *	    #103: Add extra param to "compareValues" for equality checks.
 *	17-Oct-2022 (rlwhitcomb)
 *	    #522: Implement "-" qualifiers for several "@" formats.
 *	21-Oct-2022 (rlwhitcomb)
 *	    #470: Several optimizations of "iterateOverDotRange" depending on the operation
 *	    to be performed.
 *	    #473: Add flags into FileUtilities.exists(). Add "findfiles" function.
 *	24-Oct-2022 (rlwhitcomb)
 *	    #473: Additional flags for "findfiles" to search recursively, provide full paths,
 *	    and to ignore case of file names when matching.
 *	25-Oct-2022 (rlwhitcomb)
 *	    #534: Error on duplicate "const" declaration.
 *	01-Nov-2022 (rlwhitcomb)
 *	    #544: Respect the "quotestrings" setting for "@j" formatting; turn off
 *	    "extraSpace" with the "-" flag.
 *	    #543: Trap DateTimeException and wrap with CalcExprException.
 *	06-Nov-2022 (rlwhitcomb)
 *	    #476: New "readProperties" and "writeProperties" functions.
 *	    Use "natural order" comparator for properties keys.
 *	07-Nov-2022 (rlwhitcomb)
 *	    #549: Fix Intl tag for a message that moved.
 *	09-Nov-2022 (rlwhitcomb)
 *	    #550: ":assert" directive.
 *	10-Nov-2022 (rlwhitcomb)
 *	    #554: Don't reallocate the LValueContext during popScope,
 *	    but cache it in the NestedScope.
 *	11-Nov-2022 (rlwhitcomb)
 *	    #554: Don't do the extra pop/push of function scope.
 *	28-Nov-2022 (rlwhitcomb)
 *	    #557: Call the coloring routine without the map inside ":echo".
 *	29-Nov-2022 (rlwhitcomb)
 *	    #564: Add "color" function.
 *	29-Nov-2022 (rlwhitcomb)
 *	    #567: Add "descending" flag to sort.
 *	30-Nov-2022 (rlwhitcomb)
 *	    #566: Multiple declarations on "const" and "var".
 *	01-Dec-2022 (rlwhitcomb)
 *	    Add "nullCheck" for pre- and postInc operators.
 *	    Reverse ".equals" test with empty collection to avoid NPEs.
 *	02-Dec-2022 (rlwhitcomb)
 *	    #564: Use new ConsoleColor codes to expose color codes for "@Q" format.
 *	05-Dec-2022 (rlwhitcomb)
 *	    #573: New "scan" function.
 *	13-Dec-2022 (rlwhitcomb)
 *	    #580: Fix "sumof" bug with integer values.
 *	17-Dec-2022 (rlwhitcomb)
 *	    #572: Regularize member naming conventions.
 *	19-Dec-2022 (rlwhitcomb)
 *	    #79: Move "random" function out to MathUtil.
 *	    #588: Another flavor of case selector with two compare ops.
 *	    #559: Changes for rational complex numbers.
 *	20-Dec-2022 (rlwhitcomb)
 *	    #588: Fix incorrect evaluation of XOR in double compare op selector.
 *	22-Dec-2022 (rlwhitcomb)
 *	    #559: Don't insist that both values be fractions before doing fraction
 *	    calculations; either one will do.
 *	24-Dec-2022 (rlwhitcomb)
 *	    #441: Implement "is" operator.
 *	29-Dec-2022 (rlwhitcomb)
 *	    #558: Beginnings of "quaternion" support.
 *	05-Jan-2023 (rlwhitcomb)
 *	    #558: Quaternion basic arithmetic.
 *	10-Jan-2023 (rlwhitcomb)
 *	    #103: New complex "sqrt" function; add rounding context to other functions.
 *	    #558: Give quaternion priority over complex so operations with "i" will promote.
 *	12-Jan-2023 (rlwhitcomb)
 *	    Refactor the Next and Leave exceptions.
 *	24-Jan-2023 (rlwhitcomb)
 *	    #594: Redo the bit operations on pure boolean values.
 *	04-Feb-2023 (rlwhitcomb)
 *	    #558: More quaternion arithmetic, particularly integer powers.
 *	12-Feb-2023 (rlwhitcomb)
 *	    #68: Fixes to "substr" for null begin/end values.
 *	16-Feb-2023 (rlwhitcomb)
 *	    #244: Move "formatWithSeparators" from CalcUtil to Num. Apply to formatting
 *	    fractions.
 *	21-Feb-2023 (rlwhitcomb)
 *	    #244: Move to applying separators to complex numbers too.
 *	26-Mar-2023 (rlwhitcomb)
 *	    Modify "timethis" grammar to not require ugly comma before LBRACE.
 *	28-Mar-2023 (rlwhitcomb)
 *	    #596: Move pure REPL commands into the grammar, and implement here.
 *	03-Apr-2023 (rlwhitcomb)
 *	    #263: More work on conversions in flat maps, etc.
 *	08-Apr-2023 (rlwhitcomb)
 *	    #601: Make "lcm" and "gcd" work on n-ary inputs.
 *	09-Apr-2023 (rlwhitcomb)
 *	    #605: Add "arrayof" function.
 *	11-Apr-2023 (rlwhitcomb)
 *	    Make mode option enum for all the relevant values.
 *	03-May-2023 (rlwhitcomb)
 *	    #599: New parameter to "convertToWords" for British ("and") usage.
 *	05-May-2023 (rlwhitcomb)
 *	    #558: Negate quaternions, and other quaternion arithmetic.
 *	    Rename some helper methods.
 *	24-May-2023 (rlwhitcomb)
 *	    #611: Need small parameter update for rearrangement of builtin functions
 *	    within the grammar.
 *	02-Jun-2023 (rlwhitcomb)
 *	    #615: Rearrange logic for loops so that any variables assigned during loop
 *	    initialization get defined in the enclosing scope instead of the loop scope.
 *	12-Jun-2023 (rlwhitcomb)
 *	    #616: Fix "$echo" output parameter.
 *	14-Jul-2023 (rlwhitcomb)
 *	    #613: Expand the width of results for the "factors" and "pfactors" functions.
 *	15-Jul-2023 (rlwhitcomb)
 *	    #619: Add the "defined" function.
 *	05-Aug-2023 (rlwhitcomb)
 *	    #621: Add processing for "enum" statement.
 *	06-Sep-2023 (rlwhitcomb)
 *	    #621: Fix "enum" to start at zero, not one.
 *	20-Sep-2023 (rlwhitcomb)
 *	    #629: Introduce "evaluateToValue" for the case of passing no-arg functions as
 *	    parameters meant to be called, not used as function references.
 *	26-Sep-2023 (rlwhitcomb)
 *	    #626: Recursive procedures for "negate" and "number" to do the right things
 *	    for objects, lists, and sets.
 *	16-Oct-2023 (rlwhitcomb)
 *	    #625: Fix "replace" to work with missing replacement value. Change some uses
 *	    of "Boolean.valueOf".
 *	    #424: Change syntax of "read" and "write" to use COLON separator for charset
 *	    name (removes ambiguity with optional params in "replace(read(..." sequences).
 *	28-Nov-2023 (rlwhitcomb)
 *	    #627: Make sure the ArrayScope object has enough room preallocated before a "fill".
 *	29-Nov-2023 (rlwhitcomb)
 *	    #636: Never use separators for the "@@" operator.
 *	06-Dec-2023 (rlwhitcomb)
 *	    #600: Labeled "leave" statement and optional labels on loop/while statements.
 *	03-Jan-2024 (rlwhitcomb)
 *	    #640: Refactor CalcPiWorker.
 *	09-Jan-2024 (rlwhitcomb)
 *	    #644: Add Python "slice" notation.
 *	11-Jan-2024 (rlwhitcomb)
 *	    #644: Remove the "slice" builtin function and refactor again.
 *	17-Jan-2024 (rlwhitcomb)
 *	    #646: Implement Python's multiple assignment notation.
 *	22-Jan-2024 (rlwhitcomb)
 *	    #647: Allow multiple names for "defined" function.
 */
package info.rlwhitcomb.calc;

import de.onyxbits.SemanticVersion;
import info.rlwhitcomb.directory.FileInfo;
import info.rlwhitcomb.directory.Match;
import info.rlwhitcomb.math.BigFraction;
import info.rlwhitcomb.math.ComplexNumber;
import info.rlwhitcomb.math.DateUtil;
import info.rlwhitcomb.math.MathUtil;
import info.rlwhitcomb.math.Num;
import info.rlwhitcomb.math.NumericUtil;
import info.rlwhitcomb.math.Quaternion;
import info.rlwhitcomb.util.*;
import net.iharder.b64.Base64;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static info.rlwhitcomb.calc.CalcUtil.*;
import static info.rlwhitcomb.math.NumericUtil.RangeMode;
import static info.rlwhitcomb.util.CharUtil.Justification.*;
import static info.rlwhitcomb.util.ConsoleColor.Code.*;
import static info.rlwhitcomb.util.Constants.*;


/**
 * Visit each node of the parse tree and do the appropriate calculations at each level.
 * <p> Separate from the grammar, which at this point is completely language-agnostic.
 */
public class CalcObjectVisitor extends CalcBaseVisitor<Object>
{
	/**
	 * One of the mode options for "on", "off", "pop", and so forth.
	 * <p> Note: these must match the values in Calc.g4
	 */
	private enum ModeOption
	{
		TRUE,
		ON,
		YES,
		FALSE,
		OFF,
		NO,
		POP,
		PREV,
		PREVIOUS;

		static ModeOption fromValue(final Object obj) {
		    String string = obj.toString();
		    for (ModeOption opt : values()) {
			if (opt.toString().equalsIgnoreCase(string))
			    return opt;
		    }
		    throw new Intl.IllegalArgumentException("calc#modeError", string);
		}
	}

	/**
	 * Enumeration of possible optimization strategies for {@link #iterateOverDotRange},
	 * depending on what we're really trying to do during the iteration.
	 */
	private enum Purpose
	{
		/** We need to visit all the values (as for {@code loop}). */
		ALL,
		/** We only need to match one (or a small number of) values, as for {@code case}, or {@code in}. */
		SELECT,
		/** We're trying to calculate a sum (as for {@code sumof}). */
		SUM,
		/** We're trying to calculate a product (as for {@code productof}). */
		PRODUCT,
		/** The length (or number of values) in the range is all we need (as for {@code length}). */
		LENGTH
	}

	/**
	 * Interface for the {@link #iterateOverDotRange iterateOverDotRange(...)} method, which is either called for each value
	 * in the range, or is used to optimally calculate the result without having to do the entire
	 * iteration.
	 * <p> We use the {@link Purpose} to partially decide if the optimization will apply. For instance,
	 * for the {@code loop} statement, no optimization is possible because the whole purpose is to
	 * do something with every value in the range. This is only a partial determination, also depending
	 * on the type of object(s) passed to the statement.
	 */
	private interface IterationVisitor extends Function<Object, Object>
	{
		/**
		 * Supply the overarching function we're working on, in order to suggest
		 * possible short-circuit optimizations (as in {@code sumof n}).
		 *
		 * @return The type of calculation we're doing with this range.
		 */
		default Purpose getPurpose() {
		    return Purpose.ALL;
		}

		/**
		 * Do any startup operations before the actual iteration begins.
		 */
		default void start() {
		}

		@Override
		Object apply(Object value);

		/**
		 * Do any cleanup operations after the iteration finishes.
		 */
		default void finish() {
		}

		/**
		 * Given the "dot" range values, compute the short-circuit "final" value.
		 * <p>Note: this is only called for a visitor whose purpose is not {@link Purpose#ALL}.
		 *
		 * @param start The beginning value.
		 * @param stop  Ending value of the range.
		 * @param step  The step value through the range.
		 * @return      The final value, computed from the range.
		 * @throws IllegalArgumentException if for some reason the given values aren't amenable
		 *         to calculating the final value all at once (so, we have to go back to every value).
		 */
		default Object finalValue(Number start, Number stop, Number step)
			throws IllegalArgumentException
		{
		    return null;
		}

		/**
		 * Find the length of the arithmetic sequence, which is {@code (stop - start) / step}.
		 *
		 * @param start  Starting value of the sequence.
		 * @param stop   Ending value of the sequence.
		 * @param step   Difference between successive terms.
		 * @return       Number of terms in the sequence.
		 */
		static Number length(Number start, Number stop, Number step) {
		    if (start instanceof Integer) {
			int iStart = (Integer) start;
			int iStop  = (Integer) stop;
			int iStep  = (Integer) step;

			return (iStop - iStart) / iStep + 1;
		    }
		    else {
			BigDecimal dStart = (BigDecimal) start;
			BigDecimal dStop  = (BigDecimal) stop;
			BigDecimal dStep  = (BigDecimal) step;

			return fixup(dStop.subtract(dStart).divide(dStep).add(BigDecimal.ONE));
		    }
		}

		/**
		 * Given range values, and a target value, determine if the target is contained in the range.
		 *
		 * @param visitor The visitor used to calculate values.
		 * @param value   The target expression value to match (can be any kind of value).
		 * @param start   Starting value (either integer or decimal) for the range, can be negative.
		 * @param stop    Ending value of the range.
		 * @param step    Step value through the range, again this can be negative.
		 * @param ctx     The value context (for error reporting).
		 * @return        Whether or not the target value is exactly contained in the range.
		 */
		static boolean containedIn(CalcObjectVisitor visitor, Object value, Number start, Number stop, Number step, ParserRuleContext ctx) {
		    // The range values will either be all integers or all decimal values
		    if (start instanceof Integer) {
			int iValue = convertToInt(value, visitor.settings.mc, ctx);
			int iStart = (Integer) start;
			int iStop  = (Integer) stop;
			int iStep  = (Integer) step;

			if (iStep < 0) {
			    return (iValue >= iStop && iValue <= iStart) && ((iValue + iStart) % iStep == 0);
			}
			else {
			    return (iValue >= iStart && iValue <= iStop) && ((iValue - iStart) % iStep == 0);
			}
		    }
		    else {
			BigDecimal dValue = convertToDecimal(value, visitor.settings.mc, ctx);
			BigDecimal dStart = (BigDecimal) start;
			BigDecimal dStop  = (BigDecimal) stop;
			BigDecimal dStep  = (BigDecimal) step;

			if (dStep.signum() < 0) {
			    return (dValue.compareTo(dStop) >= 0 && dValue.compareTo(dStart) <= 0) &&
				fixup(dValue.add(dStart).remainder(dStep)).equals(BigDecimal.ZERO);
			}
			else {
			    return (dValue.compareTo(dStart) >= 0 && dValue.compareTo(dStop) <= 0) &&
				fixup(dValue.subtract(dStart).remainder(dStep)).equals(BigDecimal.ZERO);
			}
		    }
		}
	}

	/** Flag for case-insensitive sort. */
	private static final int SORT_CASE_INSENSITIVE = 0x0001;
	/** Flag for sort of keys vs values in maps. */
	private static final int SORT_SORT_KEYS        = 0x0002;
	/** Flag for sort in descending order. */
	private static final int SORT_DESCENDING       = 0x0004;
	/** The set of all the valid sort flags we support. */
	private static final int SORT_ALL_FLAGS =
	    ( SORT_CASE_INSENSITIVE | SORT_SORT_KEYS | SORT_DESCENDING );

	/** Flag for case-insensitive matches. */
	private static final int MATCH_CASE_INSENSITIVE = 0x0001;
	/** Flag for "dotall" matches. */
	private static final int MATCH_DOTALL           = 0x0002;
	/** Flag for Unicode-case match. */
	private static final int MATCH_UNICODE_CASE     = 0x0004;
	/** Flag for literal match. */
	private static final int MATCH_LITERAL          = 0x0008;
	/** Flag for multi-line match. */
	private static final int MATCH_MULTILINE        = 0x0010;
	/** Flag for Unix lines mode. */
	private static final int MATCH_UNIX_LINES       = 0x0020;
	/** The set of all the valid flags we support. */
	private static final int MATCH_ALL_FLAGS =
	    ( MATCH_CASE_INSENSITIVE | MATCH_DOTALL | MATCH_UNICODE_CASE | MATCH_LITERAL | MATCH_MULTILINE | MATCH_UNIX_LINES );


	/** Pattern for format specifiers. */
	private static final Pattern FORMAT_PATTERN =
		Pattern.compile("\\s*@([\\-+])?([0-9]+)?([\\.](([0-9]+)?([\\.]([0-9]+))?))?([a-zA-Z,_])?([a-zA-Z%$])");

	/** Scale for double operations. */
	private static final MathContext MC_DOUBLE = MathContext.DECIMAL64;

	/**
	 * MathContext to use for pi/e calculations when regular context is unlimited.
	 * Note: precision is arbitrary, but {@link MathUtil#pi} is limited to ~12,500 digits.
	 * Note also that this precision is used for division operations in unlimited mode,
	 * where often an exception would be thrown due to infinite repeating digits.
	 */
	private static final MathContext MC_MAX_DIGITS = new MathContext(12000);

	/** Whether we are running on Windows or not. */
	private static final boolean RUNNING_ON_WINDOWS = Environment.isWindows();


	/** Initialization flag -- delays print until constructor is finished.  */
	private boolean initialized = false;

	/**
	 * Flag set during {@link FunctionScope#setParameterValue} so that zero-arg functions
	 * passed as parameters without parens don't get erroneously called prematurely.
	 */
	private boolean doNotCallZeroArgFunctions = false;

	/** The mode settings for this instantiation of the visitor. */
	private Settings settings = new Settings();

	/** Global symbol table for variables. */
	private final GlobalScope globals;

	/** The current topmost scope for variables. */
	private NestedScope currentScope;

	/** The current {@code LValueContext} for variables. */
	private LValueContext currentContext;

	/** {@link CalcDisplayer} object so we can output results to either the console or GUI window. */
	private final CalcDisplayer displayer;

	/**
	 * The worker used to maintain the current e/pi values, and calculate them
	 * in a background thread.
	 */
	private CalcPiWorker piWorker = new CalcPiWorker();

	/** Stack of previous "timing" mode values. */
	private final Deque<Boolean> timingModeStack       = new ArrayDeque<>();

	/** Stack of previous "debug" mode values. */
	private final Deque<Boolean> debugModeStack        = new ArrayDeque<>();

	/** Stack of previous "rational" mode values. */
	private final Deque<Boolean> rationalModeStack     = new ArrayDeque<>();

	/** Stack of previous "separator" mode values. */
	private final Deque<Boolean> separatorModeStack    = new ArrayDeque<>();

	/** Stack of previous "ignore case" mode values. */
	private final Deque<Boolean> ignoreCaseModeStack   = new ArrayDeque<>();

	/** Stack of previous "quote strings" mode values. */
	private final Deque<Boolean> quoteStringsModeStack = new ArrayDeque<>();

	/** Stack of previous "sort keys" mode values. */
	private final Deque<Boolean> sortKeysModeStack     = new ArrayDeque<>();

	/** Stack of previous "colored" mode values. */
	private final Deque<Boolean> coloredModeStack      = new ArrayDeque<>();

	/** Stack of previous "resultsOnly" mode values. */
	private final Deque<Boolean> resultsOnlyModeStack  = new ArrayDeque<>();

	/** Stack of previous "quiet" mode values. */
	private final Deque<Boolean> quietModeStack        = new ArrayDeque<>();

	/** Stack of previous "silence" mode values. */
	private final Deque<Boolean> silenceModeStack      = new ArrayDeque<>();


	/**
	 * Access the currently active symbol table, whether the globals,
	 * or the current function, or statement block.
	 *
	 * @return The {@link #currentScope}.
	 */
	public NestedScope getVariables() {
	    return currentScope;
	}

	/**
	 * Set the given symbol table scope as the topmost (current) context.
	 * <p> Also set {@link #currentContext} for symbol table lookup.
	 *
	 * @param newScope The new symbol table scope to be the current one.
	 */
	public void pushScope(final NestedScope newScope) {
	    newScope.setEnclosingScope(currentScope);
	    currentScope = newScope;

	    LValueContext context = currentScope.getContext();
	    if (context == null) {
		context = new LValueContext(currentScope, settings.ignoreNameCase);
		currentScope.setContext(context);
	    }
	    else {
		context.setIgnoreCase(settings.ignoreNameCase);
	    }
	    currentContext = context;
	}

	/**
	 * Pop the topmost symbol table scope off the stack, making the previous (parent)
	 * scope as the current one. Also pops the {@link #currentContext} for correct
	 * symbol lookup.
	 */
	public void popScope() {
	    currentScope = currentScope.getEnclosingScope();

	    currentContext = currentScope.getContext();
	    currentContext.setIgnoreCase(settings.ignoreNameCase);
	}

	/**
	 * Get the symbol table context for the given variable.
	 *
	 * @param var The parsing context to construct the lookup context for.
	 * @return    A variable lookup context for this variable.
	 */
	private LValueContext getLValue(CalcParser.VarContext var) {
	    return LValueContext.getLValue(this, var, currentContext);
	}


	Supplier<Object> phiSupplier = () -> {
	    if (settings.rationalMode)
		return MathUtil.ratphi(settings.mcDivide, false);
	    else
		return MathUtil.phi(settings.mcDivide, false);
	};
	Supplier<Object> phi1Supplier = () -> {
	    if (settings.rationalMode)
		return MathUtil.ratphi(settings.mcDivide, true);
	    else
		return MathUtil.phi(settings.mcDivide, true);
	};


	/**
	 * Construct the visitor, with the given displayer for results, and the settings
	 * from the command-line options.
	 *
	 * @param resultDisplayer Where to display the results.
	 * @param rational        The initial rational flag setting.
	 * @param separators      Setting for numeric separators.
	 * @param silence         Flag for "quiet" mode.
	 * @param ignoreCase      Whether to ignore variable name case.
	 * @param quotes          Setting for quoting strings on output.
	 * @param sortKeys        Whether to sort map keys or leave them in declared order.
	 */
	public CalcObjectVisitor(
		final CalcDisplayer resultDisplayer,
		final boolean rational,
		final boolean separators,
		final boolean silence,
		final boolean ignoreCase,
		final boolean quotes,
		final boolean sortKeys)
	{
	    displayer = resultDisplayer;
	    settings  = new Settings(rational, separators, silence, ignoreCase, quotes, sortKeys);
	    setIntMathContext(MathContext.DECIMAL128);

	    globals = new GlobalScope();
	    pushScope(globals);

	    CalcPredefine.define(globals, piWorker, phiSupplier, phi1Supplier);

	    ObjectScope sets = new ObjectScope();
	    PredefinedValue.define(globals, "settings", sets);

	    SystemValue.define(sets, settings, "trigMode",          this::setTrigMode    );
	    SystemValue.define(sets, settings, "units",             this::setUnits       );
	    SystemValue.define(sets, settings, "rationalMode",      pushRationalMode     );
	    SystemValue.define(sets, settings, "separatorMode",     pushSeparatorMode    );
	    SystemValue.define(sets, settings, "silent",            pushQuietMode        );
	    SystemValue.define(sets, settings, "silenceDirectives", pushSilenceMode      );
	    SystemValue.define(sets, settings, "ignoreNameCase",    pushIgnoreCaseMode   );
	    SystemValue.define(sets, settings, "quoteStrings",      pushQuoteStringsMode );
	    SystemValue.define(sets, settings, "sortKeys",          pushSortKeysMode     );
	    SystemValue.define(sets, settings, "precision",         this::setPrecision   );
	    sets.setImmutable(true);

	    initialized = true;
	}



	private void displayDirectiveMessage(final String formatOrKey, final Object... args) {
	    if (initialized && !settings.silent && !settings.silenceDirectives) {
		String message = Intl.formatKeyString(formatOrKey, args);
		displayer.displayActionMessage(message);
	    }
	}

	public void displayActionMessage(final String formatOrKey, final Object... args) {
	    if (initialized && !settings.silent) {
		String message = Intl.formatKeyString(formatOrKey, args);
		displayer.displayActionMessage(message);
	    }
	}


	/**
	 * Set one of the global argument variables ({@code $0}, {@code $1}, etc)
	 * to the given value.
	 *
	 * @param index	The zero-based index for the variable.
	 * @param arg	The argument value, which will be parsed and set as a numeric value
	 *		if it successfully parses as a {@link BigDecimal}, or a boolean
	 *		if it fails that, otherwise it will be set as a string.
	 * @see GlobalScope#GLOBAL_PREFIX
	 */
	public void setArgument(final int index, final String arg) {
	    String argKey = String.format("%1$s%2$d", GlobalScope.GLOBAL_PREFIX, index);
	    Object value = stringToValue(arg);

	    ParameterValue.define(globals, argKey, value);
	}

	/**
	 * Set the value of a global variable (from command line).
	 *
	 * @param name	The variable's name.
	 * @param value	The original string value to set, which will be converted
	 *		(if possible) to a number or boolean.
	 */
	public void setVariable(final String name, final String value) {
	    globals.setValue(name, stringToValue(value));
	}


	public Settings getSettings() {
	    return settings;
	}

	public MathContext getMathContext() {
	    return settings.mc;
	}


	public int setMathContext(final MathContext newMathContext) {
	    int prec = newMathContext.getPrecision();

	    settings.mc        = newMathContext;
	    settings.precision = prec;

	    // Use a limited precision of our max digits in the case of unlimited precision
	    settings.mcDivide = (prec == 0) ? MC_MAX_DIGITS : newMathContext;

	    // Trigger a background (re)calculation with the new precision
	    piWorker.apply(settings.mcDivide, settings.rationalMode);

	    if (settings.precision == 0)
		displayDirectiveMessage("%calc#precUnlimited");
	    else if (settings.precision == 1)
		displayDirectiveMessage("%calc#precOneDigit");
	    else
		displayDirectiveMessage("%calc#precDigits", settings.precision);

	    return settings.precision;
	}

	public BigInteger setIntMathContext(final MathContext newMathContext) {
	    return BigInteger.valueOf(setMathContext(newMathContext));
	}

	public boolean setSilent(final boolean newSilent) {
	    boolean oldSilent = settings.silent;
	    settings.silent = newSilent;
	    return oldSilent;
	}

	private UnaryOperator<Boolean> setQuietMode = mode -> {
	    return Calc.setQuietMode(mode);
	};

	private Consumer<Object> pushQuietMode = mode -> {
	    processModeOption(mode, quietModeStack, setQuietMode);
	};

	public boolean setSilenceDirectives(final boolean newSilence) {
	    boolean oldSilence = settings.silenceDirectives;
	    settings.silenceDirectives = newSilence;
	    return oldSilence;
	}

	private UnaryOperator<Boolean> setSilenceMode = mode -> {
	    return Calc.setSilenceMode(mode);
	};

	private Consumer<Object> pushSilenceMode = mode -> {
	    processModeOption(mode, silenceModeStack, setSilenceMode);
	};

	public String setTrigMode(final Object mode) {
	    settings.trigMode = TrigMode.getFrom(mode);

	    displayDirectiveMessage("%calc#trigMode", settings.trigMode);

	    return settings.trigMode.toString();
	}

	public String setUnits(final Object mode) {
	    settings.units = RangeMode.getFrom(mode);

	    switch (settings.units) {
		case BINARY:
		    displayDirectiveMessage("%calc#unitsBinary");
		    break;
		case DECIMAL:
		    displayDirectiveMessage("%calc#unitsTen");
		    break;
		case MIXED:
		    displayDirectiveMessage("%calc#unitsMixed");
		    break;
	    }

	    return settings.units.toString();
	}

	public boolean setSeparatorMode(final Object mode) {
	    boolean oldMode = settings.separatorMode;
	    settings.separatorMode = CharUtil.getBooleanValue(mode);

	    displayDirectiveMessage("%calc#separatorMode", settings.separatorMode);

	    return oldMode;
	}

	private UnaryOperator<Boolean> setSeparatorMode = mode -> {
	    return setSeparatorMode(mode);
	};

	private Consumer<Object> pushSeparatorMode = mode -> {
	    processModeOption(mode, separatorModeStack, setSeparatorMode);
	};

	public boolean setIgnoreCaseMode(final Object mode) {
	    boolean oldMode = settings.ignoreNameCase;
	    settings.ignoreNameCase = CharUtil.getBooleanValue(mode);
	    currentContext.setIgnoreCase(settings.ignoreNameCase);

	    displayDirectiveMessage("%calc#ignoreCaseMode", settings.ignoreNameCase);

	    return oldMode;
	}

	private UnaryOperator<Boolean> setIgnoreCaseMode = mode -> {
	    return setIgnoreCaseMode(mode);
	};

	private Consumer<Object> pushIgnoreCaseMode = mode -> {
	    processModeOption(mode, ignoreCaseModeStack, setIgnoreCaseMode);
	};

	public boolean setQuoteStringsMode(final Object mode) {
	    boolean oldMode = settings.quoteStrings;
	    settings.quoteStrings = CharUtil.getBooleanValue(mode);

	    displayDirectiveMessage("%calc#quoteStringsMode", settings.quoteStrings);

	    return oldMode;
	}

	private UnaryOperator<Boolean> setQuoteStringsMode = mode -> {
	    return setQuoteStringsMode(mode);
	};

	private Consumer<Object> pushQuoteStringsMode = mode -> {
	    processModeOption(mode, quoteStringsModeStack, setQuoteStringsMode);
	};

	public boolean setSortKeysMode(final Object mode) {
	    boolean oldMode = settings.sortKeys;
	    settings.sortKeys = CharUtil.getBooleanValue(mode);

	    displayDirectiveMessage("%calc#sortKeysMode", settings.sortKeys);

	    return oldMode;
	}

	private UnaryOperator<Boolean> setSortKeysMode = mode -> {
	    return setSortKeysMode(mode);
	};

	private Consumer<Object> pushSortKeysMode = mode -> {
	    processModeOption(mode, sortKeysModeStack, setSortKeysMode);
	};

	public boolean setColoredMode(final Object mode) {
	    boolean oldMode = Calc.getColoredMode();
	    Calc.setColoredMode(CharUtil.getBooleanValue(mode));

	    return oldMode;
	}

	private UnaryOperator<Boolean> setColoredMode = mode -> {
	    return setColoredMode(mode);
	};

	private Consumer<Object> pushColoredMode = mode -> {
	    processModeOption(mode, coloredModeStack, setColoredMode);
	};

	public boolean setRationalMode(final Object mode) {
	    boolean oldMode = settings.rationalMode;

	    settings.rationalMode = CharUtil.getBooleanValue(mode);
	    piWorker.apply(settings.mcDivide, settings.rationalMode);

	    String msg;

	    if (settings.rationalMode) {
		msg = Intl.getString("calc#rational");
	    }
	    else {
		int prec = settings.mc.getPrecision();
		if (prec == 0)
		    msg = Intl.formatString("calc#decimal", Intl.getString("calc#unlimited"));
		else
		    msg = Intl.formatString("calc#decimal", prec);
	    }

	    displayDirectiveMessage("%calc#rationalMode", msg);

	    return oldMode;
	}

	private UnaryOperator<Boolean> setRationalMode = mode -> {
	    return setRationalMode(mode);
	};

	private Consumer<Object> pushRationalMode = mode -> {
	    processModeOption(mode, rationalModeStack, setRationalMode);
	};

	public boolean setTimingMode(final boolean mode) {
	    boolean oldMode = Calc.setTimingMode(mode);

	    displayDirectiveMessage("%calc#timingMode", mode);

	    return oldMode;
	}

	public boolean setDebugMode(final boolean mode) {
	    boolean oldMode = Calc.setDebugMode(mode);

	    displayDirectiveMessage("%calc#debugMode", mode);

	    return oldMode;
	}


	/**
	 * Evaluate a parameter value, doing {@link #evaluate(ParserRuleContext)} but without
	 * calling any functions encountered (that is, passing the function reference itself).
	 *
	 * @param ctx The parsing context to visit to get the value.
	 * @return    Either the value or the result of visiting that function context if it is one.
	 */
	Object evaluateParameter(final ParserRuleContext ctx) {
	    // Note: this has to be set for any recursive calls made during the evaluation
	    // and restored regardless of any exceptions thrown
	    doNotCallZeroArgFunctions = true;
	    try {
		return evaluate(ctx);
	    }
	    finally {
		doNotCallZeroArgFunctions = false;
	    }
	}

	/**
	 * Evaluate to a real value, by doing {@link #evaluate(ParserRuleContext)} but making sure to
	 * call any functions encountered by resetting the {@link #doNotCallZeroArgFunctions} flag.
	 *
	 * @param ctx The parsing context to visit to get the value.
	 * @return    Either the value or the result of visiting that function context if it is one.
	 */
	Object evaluateToValue(final ParserRuleContext ctx) {
	    boolean functions = doNotCallZeroArgFunctions;
	    doNotCallZeroArgFunctions = false;
	    try {
		return evaluate(ctx);
	    }
	    finally {
		doNotCallZeroArgFunctions = functions;
	    }
	}

	/**
	 * Evaluate to a real value, by doing {@link #evaluate(ParserRuleContext)} but making sure to
	 * call any functions encountered by resetting the {@link #doNotCallZeroArgFunctions} flag.
	 *
	 * @param ctx   The parsing context (for error reporting).
	 * @param value The result of an expression, which could be a reference to a function call.
	 * @return      Either the value or the result of visiting that function context if it is one.
	 */
	Object evaluateToValue(final ParserRuleContext ctx, final Object value) {
	    boolean functions = doNotCallZeroArgFunctions;
	    doNotCallZeroArgFunctions = false;
	    try {
		return evaluate(ctx, value);
	    }
	    finally {
		doNotCallZeroArgFunctions = functions;
	    }
	}

	/**
	 * Evaluate a function by calling {@link #evaluate(ParserRuleContext, Object)}
	 * by calling {@code visit(ctx)} to get the value.
	 *
	 * @param ctx The parsing context to visit to get the value.
	 * @return    Either the value or the result of visiting that function context if it is one.
	 */
	Object evaluate(final ParserRuleContext ctx) {
	    return evaluate(ctx, visit(ctx));
	}

	/**
	 * Evaluate a function: basically call {@code visit} on that context if the value itself
	 * is a function scope (that is, the declaration of a function).
	 * <p> But also, if the value is a {@link FunctionDeclaration} we have a choice:
	 * <ul><li> if the function was declared WITHOUT parameters, then call the function</li>
	 * <li> if the function was defined WITH parameters, then treat the value as a function object
	 * and just return the value</li>
	 * </ul>
	 * <p> BUT, during parameter evaluation, the {@link #doNotCallZeroArgFunctions} flag is set
	 * to (obviously) not call zero-arg functions without parens so it is passed still as a
	 * function declaration without calling it.
	 * <p> Also, if the function return (or the initial value, for that matter) is a {@link ValueScope}
	 * then call its {@code getValue()} function to get the real value (which, for the moment at least,
	 * will never be another function).
	 *
	 * @param ctx   The parsing context (for error reporting).
	 * @param value The result of an expression, which could be a reference to a function call.
	 * @return      Either the value or the result of visiting that function context if it is one.
	 */
	Object evaluate(final ParserRuleContext ctx, final Object value) {
	    Object returnValue = value;

	    if (returnValue instanceof FunctionDeclaration) {
		FunctionDeclaration funcDecl = (FunctionDeclaration) returnValue;
		if (funcDecl.getNumberOfParameters() == 0 && !doNotCallZeroArgFunctions) {
		    returnValue = funcDecl.setupFunctionCall(ctx, this, null);
		}
	    }

	    if (returnValue != null && returnValue instanceof FunctionScope) {
		FunctionScope func = (FunctionScope) returnValue;
		String functionName = func.getFunctionName();

		pushQuietMode.accept(true);
		try {
		    returnValue = visit(func.getFunctionBody());
		}
		catch (LeaveException lex) {
		    // Slightly different rules here: an unlabeled "leave" is sufficient to exit
		    // a function (because a function always has a name, where a loop might not
		    // have a label). So, the only way NOT to get caught here is to have a "leave"
		    // label that doesn't match the function name.
		    String leaveLabel = lex.getLabel();
		    if (compareLabels(functionName, leaveLabel) || compareLabels("", leaveLabel)) {
			returnValue = lex.hasValue() ? lex.getValue() : null;
		    }
		    else {
			throw lex;
		    }
		}
		finally {
		    popScope();
		    pushQuietMode.accept(ModeOption.POP);
		}
	    }

	    if (returnValue instanceof ValueScope) {
		returnValue = ((ValueScope) returnValue).getValue();
	    }

	    return returnValue;
	}

	private BigDecimal getDecimalValue(final ParserRuleContext ctx) {
	    return toDecimalValue(this, visit(ctx), settings.mc, ctx);
	}

	private BigFraction getFractionValue(final ParserRuleContext ctx) {
	    return toFractionValue(this, visit(ctx), ctx);
	}

	private BigInteger getIntegerValue(final ParserRuleContext ctx) {
	    return convertToInteger(evaluateToValue(ctx), settings.mc, ctx);
	}

	private LocalDate getDateValue(final ParserRuleContext ctx) {
	    try {
		BigInteger iValue = convertToInteger(evaluateToValue(ctx), settings.mc, ctx);
		return LocalDate.ofEpochDay(iValue.longValueExact());
	    }
	    catch (ArithmeticException | DateTimeException ex) {
		throw new CalcExprException(ex, ctx);
	    }
	}

	private Boolean getBooleanValue(final ParserRuleContext ctx) {
	    return toBooleanValue(this, visit(ctx), ctx);
	}

	protected String getStringValue(final ParserRuleContext ctx) {
	    return getStringValue(ctx, false, false, settings.separatorMode);
	}

	private String getStringValue(final ParserRuleContext ctx, final boolean allowNull, final boolean quote, final boolean separators) {
	    if (ctx == null && allowNull)
		return "";

	    Object value = evaluateToValue(ctx);

	    if (!allowNull)
		nullCheck(value, ctx);

	    return value == null ? "" : toStringValue(this, ctx, value, new StringFormat(quote, separators));
	}

	public String getNonNullString(final ParserRuleContext ctx, final Object value) {
	    nullCheck(value, ctx);
	    return toStringValue(this, ctx, value, new StringFormat(false, settings));
	}

	private double getDoubleValue(final ParserRuleContext ctx) {
	    BigDecimal dec = getDecimalValue(ctx);

	    return dec.doubleValue();
	}

	protected int getIntValue(final ParserRuleContext ctx, final Integer nullValue) {
	    return convertToInt(evaluateToValue(ctx), settings.mc, ctx, nullValue);
	}

	protected int getIntValue(final ParserRuleContext ctx) {
	    return convertToInt(evaluateToValue(ctx), settings.mc, ctx);
	}

	private BigDecimal getDecimalTrigValue(final ParserRuleContext ctx) {
	    BigDecimal value = getDecimalValue(ctx);

	    if (settings.trigMode == TrigMode.DEGREES)
		value = value.multiply(piWorker.getPiOver180(), settings.mc);
	    else if (settings.trigMode == TrigMode.GRADS)
		value = value.multiply(piWorker.getPiOver200(), settings.mc);

	    return value;
	}

	private double getTrigValue(final ParserRuleContext ctx) {
	    return getDecimalTrigValue(ctx).doubleValue();
	}

	private BigDecimal returnTrigValue(final double value) {
	    BigDecimal radianValue = new BigDecimal(value, MC_DOUBLE);

	    if (settings.trigMode == TrigMode.DEGREES)
		return radianValue.divide(piWorker.getPiOver180(), MC_DOUBLE);
	    else if (settings.trigMode == TrigMode.GRADS)
		return radianValue.divide(piWorker.getPiOver200(), MC_DOUBLE);

	    return radianValue;
	}

	private BigDecimal returnTrigValue(final BigDecimal value) {
	    BigDecimal radianValue = value;

	    MathContext mcDivide = MathUtil.divideContext(radianValue, settings.mcDivide);

	    if (settings.trigMode == TrigMode.DEGREES)
		return radianValue.divide(piWorker.getPiOver180(), mcDivide);
	    else if (settings.trigMode == TrigMode.GRADS)
		return radianValue.divide(piWorker.getPiOver200(), mcDivide);

	    return radianValue;
	}

	private Charset getCharsetValue(final ParserRuleContext ctx, final boolean useDefault) {
	    Charset defaultCharset = useDefault ? DEFAULT_CHARSET : null;

	    if (ctx == null)
		return defaultCharset;

	    String charsetName = getStringValue(ctx, true, false, false);
	    if (charsetName == null)
		return defaultCharset;

	    try {
		return Charset.forName(charsetName);
	    }
	    catch (IllegalCharsetNameException | UnsupportedCharsetException ex) {
		throw new CalcExprException(ctx, "%calc#charsetError", charsetName, Exceptions.toString(ex));
	    }
	}

	private int compareValues(final ParserRuleContext ctx1, final ParserRuleContext ctx2) {
	    return compareValues(ctx1, ctx2, false, false, false);
	}

	private int compareValues(final ParserRuleContext ctx1, final ParserRuleContext ctx2, final boolean strict, final boolean allowNulls, final boolean equality) {
	    return CalcUtil.compareValues(this, ctx1, ctx2, settings.mc, strict, allowNulls, equality);
	}


	private Object directiveMathContextBlock(CalcParser.BracketBlockContext blockCtx, Object defValue, MathContext oldContext) {
	    if (blockCtx != null) {
		try {
		    return evaluate(blockCtx);
		}
		finally {
		    setMathContext(oldContext);
		}
	    }

	    return defValue;
	}

	private Object directiveTrigModeBlock(CalcParser.BracketBlockContext blockCtx, Object defValue, TrigMode oldMode) {
	    if (blockCtx != null) {
		try {
		    return evaluate(blockCtx);
		}
		finally {
		    setTrigMode(oldMode);
		}
	    }

	    return defValue;
	}

	private Object directiveRangeModeBlock(CalcParser.BracketBlockContext blockCtx, Object defValue, RangeMode oldMode) {
	    if (blockCtx != null) {
		try {
		    return evaluate(blockCtx);
		}
		finally {
		    setUnits(oldMode);
		}
	    }

	    return defValue;
	}

	@Override
	public Object visitDecimalDirective(CalcParser.DecimalDirectiveContext ctx) {
	    CalcParser.NumberOptionContext opt = ctx.numberOption();
	    CalcParser.BracketBlockContext blockCtx = ctx.bracketBlock();
	    BigDecimal dPrecision;
	    MathContext oldMc = settings.mc;

	    if (opt.NUMBER() != null) {
		dPrecision = new BigDecimal(opt.NUMBER().getText());
	    }
	    else {
		LValueContext lValue = getLValue(opt.var());
		dPrecision = convertToDecimal(lValue.getContextObject(this, false), settings.mc, opt);
	    }

	    int precision = 0;
	    try {
		precision = dPrecision.intValueExact();
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ctx, "%calc#precNotInteger", dPrecision);
	    }

	    BigInteger intValue = BigInteger.valueOf(setPrecision(precision));

	    return directiveMathContextBlock(blockCtx, intValue, oldMc);
	}

	@Override
	public Object visitDoubleDirective(CalcParser.DoubleDirectiveContext ctx) {
	    CalcParser.BracketBlockContext blockCtx = ctx.bracketBlock();
	    MathContext oldMc = settings.mc;

	    BigInteger intValue = setIntMathContext(MathContext.DECIMAL64);

	    return directiveMathContextBlock(blockCtx, intValue, oldMc);
	}

	@Override
	public Object visitFloatDirective(CalcParser.FloatDirectiveContext ctx) {
	    CalcParser.BracketBlockContext blockCtx = ctx.bracketBlock();
	    MathContext oldMc = settings.mc;

	    BigInteger intValue = setIntMathContext(MathContext.DECIMAL32);

	    return directiveMathContextBlock(blockCtx, intValue, oldMc);
	}

	@Override
	public Object visitDefaultDirective(CalcParser.DefaultDirectiveContext ctx) {
	    CalcParser.BracketBlockContext blockCtx = ctx.bracketBlock();
	    MathContext oldMc = settings.mc;

	    BigInteger intValue = setIntMathContext(MathContext.DECIMAL128);

	    return directiveMathContextBlock(blockCtx, intValue, oldMc);
	}

	@Override
	public Object visitUnlimitedDirective(CalcParser.UnlimitedDirectiveContext ctx) {
	    CalcParser.BracketBlockContext blockCtx = ctx.bracketBlock();
	    MathContext oldMc = settings.mc;

	    BigInteger intValue = setIntMathContext(MathContext.UNLIMITED);

	    return directiveMathContextBlock(blockCtx, intValue, oldMc);
	}

	private static final MathContext AVAILABLE_CONTEXTS[] = {
	    MathContext.DECIMAL32,
	    MathContext.DECIMAL64,
	    MathContext.DECIMAL128
	};

	public int setPrecision(Number prec) {
	    int precision = prec.intValue();

	    if (precision <= 0) {
		return setMathContext(MathContext.UNLIMITED);
	    }
	    else {
		for (MathContext mcAvail : AVAILABLE_CONTEXTS) {
		    if (precision == mcAvail.getPrecision())
			return setMathContext(mcAvail);
		}
	    }
	    if (precision >= 1 && precision <= MC_MAX_DIGITS.getPrecision()) {
		return setMathContext(new MathContext(precision));
	    }
	    else {
		return setMathContext(MC_MAX_DIGITS);
	    }
	}

	@Override
	public Object visitDegreesDirective(CalcParser.DegreesDirectiveContext ctx) {
	    CalcParser.BracketBlockContext blockCtx = ctx.bracketBlock();
	    TrigMode oldMode = settings.trigMode;

	    String value = setTrigMode(TrigMode.DEGREES);

	    return directiveTrigModeBlock(blockCtx, value, oldMode);
	}

	@Override
	public Object visitRadiansDirective(CalcParser.RadiansDirectiveContext ctx) {
	    CalcParser.BracketBlockContext blockCtx = ctx.bracketBlock();
	    TrigMode oldMode = settings.trigMode;

	    String value = setTrigMode(TrigMode.RADIANS);

	    return directiveTrigModeBlock(blockCtx, value, oldMode);
	}

	@Override
	public Object visitGradsDirective(CalcParser.GradsDirectiveContext ctx) {
	    CalcParser.BracketBlockContext blockCtx = ctx.bracketBlock();
	    TrigMode oldMode = settings.trigMode;

	    String value = setTrigMode(TrigMode.GRADS);

	    return directiveTrigModeBlock(blockCtx, value, oldMode);
	}

	@Override
	public Object visitBinaryDirective(CalcParser.BinaryDirectiveContext ctx) {
	    CalcParser.BracketBlockContext blockCtx = ctx.bracketBlock();
	    RangeMode oldUnits = settings.units;

	    String value = setUnits(RangeMode.BINARY);

	    return directiveRangeModeBlock(blockCtx, value, oldUnits);
	}

	@Override
	public Object visitSiDirective(CalcParser.SiDirectiveContext ctx) {
	    CalcParser.BracketBlockContext blockCtx = ctx.bracketBlock();
	    RangeMode oldUnits = settings.units;

	    String value = setUnits(RangeMode.DECIMAL);

	    return directiveRangeModeBlock(blockCtx, value, oldUnits);
	}

	@Override
	public Object visitMixedDirective(CalcParser.MixedDirectiveContext ctx) {
	    CalcParser.BracketBlockContext blockCtx = ctx.bracketBlock();
	    RangeMode oldUnits = settings.units;

	    String value = setUnits(RangeMode.MIXED);

	    return directiveRangeModeBlock(blockCtx, value, oldUnits);
	}

	private int addName(String name, StringBuilder message) {
	    if (message.length() > 0)
		message.append(", ");

	    int lastNamePos = message.length();
	    message.append("'").append(name).append("'");

	    return lastNamePos;
	}

	@Override
	public Object visitClearDirective(CalcParser.ClearDirectiveContext ctx) {
	    CalcParser.WildIdListContext idList = ctx.wildIdList();
	    List<CalcParser.WildIdContext> ids;
	    int numberCleared = 0;

	    if (idList == null || (ids = idList.wildId()).isEmpty()) {
		Iterator<Map.Entry<String, Object>> iter = globals.map().entrySet().iterator();
		while (iter.hasNext()) {
		    Map.Entry<String, Object> entry = iter.next();
		    Object value = entry.getValue();
		    if (!isPredefined(value, true)) {
			iter.remove();
			numberCleared++;
		    }
		}
		displayDirectiveMessage("%calc#varsAllCleared");
	    }
	    else {
		StringBuilder vars = new StringBuilder();
		int lastNamePos = 0;
		for (CalcParser.WildIdContext node : ids) {
		    String varName = node.getText();
		    if (varName.equals("<missing ID>"))
			continue;

		    if (Match.hasWildCards(varName)) {
			Map<String, Object> values = globals.getWildValues(varName, settings.ignoreNameCase);
			for (Map.Entry<String, Object> entry : values.entrySet()) {
			    String name = entry.getKey();
			    Object value = entry.getValue();

			    // Explicitly allow clearing a parameter value if named in the list
			    if (!isPredefined(value, false)) {
				numberCleared++;
				globals.remove(name, settings.ignoreNameCase);
				lastNamePos = addName(name, vars);
			    }
			}
		    }
		    else {
			Object value = globals.getValue(varName, settings.ignoreNameCase);
			if (!isPredefined(value, false)) {
			    numberCleared++;
			    globals.remove(varName, settings.ignoreNameCase);
			    lastNamePos = addName(varName, vars);
			}
		    }
		}

		if (numberCleared > 0) {
		    if (numberCleared == 1) {
			vars.insert(0, Intl.getString("calc#varOneVariable"));
		    }
		    else if (numberCleared == 2) {
			vars.deleteCharAt(lastNamePos - 2);
			vars.insert(lastNamePos - 1, Intl.getString("calc#varAnd"));
			vars.insert(0, Intl.getString("calc#varVariables"));
		    }
		    else {
			vars.insert(lastNamePos, Intl.getString("calc#varAnd"));
			vars.insert(0, Intl.getString("calc#varVariables"));
		    }

		    displayDirectiveMessage("%calc#varCleared", vars);
		}
	    }

	    return BigInteger.valueOf(numberCleared);
	}

	private boolean displayValue(String key, Object value, ParserRuleContext ctx) {
	    if (!isPredefined(value, false)) {
		String fullKey = key;

		if (value instanceof FunctionDeclaration) {
		    FunctionDeclaration func = (FunctionDeclaration) value;
		    fullKey = Intl.formatString("calc#defineKey", func.getFullFunctionName());
		    displayer.displayResult(fullKey, getTreeText(func.getFunctionBody()));
		}
		else {
		    if (value instanceof ConstantValue)
			fullKey = Intl.formatString("calc#constKey", key);
		    else if (value instanceof EnumValue)
			fullKey = Intl.formatString("calc#enumKey", key);
		    else if (!(value instanceof ParameterValue))
			fullKey = Intl.formatString("calc#varKey", key);
		    displayer.displayResult(fullKey, toStringValue(this, ctx, value, new StringFormat(true, settings)));
		}
		return true;
	    }
	    return false;
	}

	@Override
	public Object visitVariablesDirective(CalcParser.VariablesDirectiveContext ctx) {
	    CalcParser.WildIdListContext idList = ctx.wildIdList();
	    List<CalcParser.WildIdContext> ids;
	    Set<String> sortedKeys = new TreeSet<>();
	    int numberDisplayed = 0;
	    boolean listSpecific = false;

	    if (idList == null || (ids = idList.wildId()).isEmpty()) {
		sortedKeys.addAll(globals.keySet());
	    }
	    else {
		ids = idList.wildId();
		for (CalcParser.WildIdContext node : ids) {
		    sortedKeys.add(node.getText());
		}
		listSpecific = true;
	    }

	    boolean oldMode = Calc.setResultsOnlyMode(false);
	    boolean replMode = Calc.getReplMode();

	    if (!replMode) {
		displayActionMessage("%calc#variables");
		displayActionMessage("%calc#varUnder1");
	    }

	    for (String key : sortedKeys) {
		if (listSpecific && Match.hasWildCards(key)) {
		    Map<String, Object> entries = globals.getWildValues(key, settings.ignoreNameCase);
		    for (Map.Entry<String, Object> entry : entries.entrySet()) {
			if (displayValue(entry.getKey(), entry.getValue(), ctx))
			    numberDisplayed++;
		    }
		}
		else {
		    Object value = globals.getValue(key, settings.ignoreNameCase);
		    if (displayValue(key, value, ctx))
			numberDisplayed++;
		}
	    }

	    if (!replMode) {
		displayActionMessage("%calc#varUnder2");
	    }

	    Calc.setResultsOnlyMode(oldMode);

	    return BigInteger.valueOf(numberDisplayed);
	}

	private boolean displayPredefValue(String key, Object value, ParserRuleContext ctx) {
	    StringFormat format = new StringFormat(true, settings);

	    if (isPredefined(value, false)) {
		PredefinedValue predef = (PredefinedValue) value;
		String fullKey;

		if (predef.isConstant()) {
		    fullKey = Intl.formatString("calc#constKey", key);
		}
		else {
		    fullKey = Intl.formatString("calc#varKey", key);
		}
		displayer.displayResult(fullKey, toStringValue(this, ctx, value, format));

		return true;
	    }
	    return false;
	}

	@Override
	public Object visitPredefinedDirective(CalcParser.PredefinedDirectiveContext ctx) {
	    CalcParser.WildIdListContext idList = ctx.wildIdList();
	    List<CalcParser.WildIdContext> ids;
	    Set<String> sortedKeys = new TreeSet<>();
	    int numberDisplayed = 0;
	    boolean listSpecific = false;

	    if (idList == null || (ids = idList.wildId()).isEmpty()) {
		sortedKeys.addAll(globals.keySet());
	    }
	    else {
		ids = idList.wildId();
		for (CalcParser.WildIdContext node : ids) {
		    sortedKeys.add(node.getText());
		}
		listSpecific = true;
	    }

	    boolean oldMode = Calc.setResultsOnlyMode(false);
	    boolean replMode = Calc.getReplMode();

	    if (!replMode) {
		displayActionMessage("%calc#predefined");
		displayActionMessage("%calc#preUnder1");
	    }

	    for (String key : sortedKeys) {
		if (listSpecific && Match.hasWildCards(key)) {
		    Map<String, Object> values = globals.getWildValues(key, settings.ignoreNameCase);
		    for (Map.Entry<String, Object> entry : values.entrySet()) {
			if (displayPredefValue(entry.getKey(), entry.getValue(), ctx))
			    numberDisplayed++;
		    }
		}
		else {
		    Object value = globals.getValue(key, settings.ignoreNameCase);
		    if (displayPredefValue(key, value, ctx))
			numberDisplayed++;
		}
	    }

	    if (!replMode) {
		displayActionMessage("%calc#preUnder2");
	    }

	    Calc.setResultsOnlyMode(oldMode);

	    return BigInteger.valueOf(numberDisplayed);
	}

	@Override
	public Object visitEchoDirective(CalcParser.EchoDirectiveContext ctx) {
	    String msg = getStringValue(ctx.expr(0), true, false, settings.separatorMode);
	    CalcParser.ExprContext expr = ctx.expr(1);
	    String out = expr == null ? "" : getTreeText(expr);
	    CalcDisplayer.Output output = CalcDisplayer.Output.OUTPUT;

	    try {
		output = CalcDisplayer.Output.fromString(out);
	    }
	    catch (IllegalArgumentException iae) {
		out = getStringValue(expr, false, false, false);
		output = CalcDisplayer.Output.fromString(out);
	    }

	    displayer.displayMessage(ConsoleColor.color(msg, Calc.getColoredMode()), output);

	    return msg;
	}

	@Override
	public Object visitIncludeDirective(CalcParser.IncludeDirectiveContext ctx) {
	    String paths = getStringValue(ctx.expr(0), false, false, false);
	    Charset charset = getCharsetValue(ctx.expr(1), false);

	    try {
		String contents = Calc.getFileContents(paths, charset, true);
		return Calc.processString(contents, settings.silent);
	    }
	    catch (IOException ioe) {
		throw new CalcExprException(ctx, "%calc#ioError", Exceptions.toString(ioe));
	    }
	}

	@Override
	public Object visitSaveDirective(CalcParser.SaveDirectiveContext ctx) {
	    String path = getStringValue(ctx.expr(0), false, false, false);
	    Charset charset = getCharsetValue(ctx.expr(1), true);

	    try {
		saveVariables(this, ctx, globals, Paths.get(path), charset);
	    }
	    catch (IOException ioe) {
		throw new CalcExprException(ctx, "%calc#ioError", Exceptions.toString(ioe));
	    }

	    return BigInteger.valueOf(globals.size());
	}


	/**
	 * Process the mode option for one of the directives, including visiting the statement block,
	 * if specified, which then pops the mode at the end.
	 *
	 * @param ctx          The expression context for the mode value.
	 * @param stack        Mode value stack to use with directive.
	 * @param bracketBlock Optional statement block to be executed with the new mode value.
	 * @param setOperator  The function used to set the appropriate mode for the directive.
	 * @return The previous value of the mode, before the value given on the directive if there
	 *         is no block of code to execute, otherwise we return the result of the code block.
	 */
	private Object processModeOption(
		final CalcParser.ModeOptionContext ctx,
		final Deque<Boolean> stack,
		final CalcParser.BracketBlockContext bracketBlock,
		final UnaryOperator<Boolean> setOperator)
	{
	    String option;

	    if (ctx.var() != null) {
		CalcParser.VarContext var = ctx.var();
		LValueContext lValue = getLValue(var);
		Object modeObject = evaluate(ctx, lValue.getContextObject(this, false));
		option = toStringValue(this, var, modeObject, new StringFormat(false, false));
	    }
	    else {
		option = ctx.getText();
	    }

	    try {
		Object ret = null;

		if (bracketBlock != null) {
		    processModeOption(option, stack, setOperator);
		    try {
			ret = evaluate(bracketBlock);
		    }
		    finally {
			processModeOption(ModeOption.POP, stack, setOperator);
		    }
		}
		else {
		    ret = processModeOption(option, stack, setOperator);
		}

		return ret;
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, ctx);
	    }
	}

	private Boolean processModeOption(final Object value, final Deque<Boolean> stack, final UnaryOperator<Boolean> setOperator) {
	    boolean mode = false;
	    boolean push = true;

	    ModeOption option = ModeOption.fromValue(value);

	    switch (option) {
		case TRUE:
		case ON:
		case YES:
		    mode = true;
		    break;
		case FALSE:
		case OFF:
		case NO:
		    mode = false;
		    break;
		case POP:
		case PREV:
		case PREVIOUS:
		    if (stack.isEmpty())
			mode = false;
		    else
			mode = stack.pop();
		    push = false;
		    break;
	    }

	    // Run the process to actually set the new mode
	    boolean previousMode = setOperator.apply(mode);

	    if (push)
		stack.push(previousMode);

	    return Boolean.valueOf(mode);
	}

	@Override
	public Object visitTimingDirective(CalcParser.TimingDirectiveContext ctx) {
	    return processModeOption(ctx.modeOption(), timingModeStack, ctx.bracketBlock(),
		mode -> { return setTimingMode(mode); });
	}

	@Override
	public Object visitDebugDirective(CalcParser.DebugDirectiveContext ctx) {
	    return processModeOption(ctx.modeOption(), debugModeStack, ctx.bracketBlock(),
		mode -> { return setDebugMode(mode); });
	}

	@Override
	public Object visitRationalDirective(CalcParser.RationalDirectiveContext ctx) {
	    return processModeOption(ctx.modeOption(), rationalModeStack, ctx.bracketBlock(), setRationalMode);
	}

	@Override
	public Object visitResultsOnlyDirective(CalcParser.ResultsOnlyDirectiveContext ctx) {
	    return processModeOption(ctx.modeOption(), resultsOnlyModeStack, ctx.bracketBlock(),
		mode -> { return Calc.setResultsOnlyMode(mode); });
	}

	@Override
	public Object visitQuietDirective(CalcParser.QuietDirectiveContext ctx) {
	    return processModeOption(ctx.modeOption(), quietModeStack, ctx.bracketBlock(), setQuietMode);
	}

	@Override
	public Object visitSilenceDirective(CalcParser.SilenceDirectiveContext ctx) {
	    return processModeOption(ctx.modeOption(), silenceModeStack, ctx.bracketBlock(), setSilenceMode);
	}

	@Override
	public Object visitSeparatorsDirective(CalcParser.SeparatorsDirectiveContext ctx) {
	    return processModeOption(ctx.modeOption(), separatorModeStack, ctx.bracketBlock(), setSeparatorMode);
	}

	@Override
	public Object visitIgnoreCaseDirective(CalcParser.IgnoreCaseDirectiveContext ctx) {
	    return processModeOption(ctx.modeOption(), ignoreCaseModeStack, ctx.bracketBlock(), setIgnoreCaseMode);
	}

	@Override
	public Object visitQuoteStringsDirective(CalcParser.QuoteStringsDirectiveContext ctx) {
	    return processModeOption(ctx.modeOption(), quoteStringsModeStack, ctx.bracketBlock(), setQuoteStringsMode);
	}

	@Override
	public Object visitSortObjectsDirective(CalcParser.SortObjectsDirectiveContext ctx) {
	    return processModeOption(ctx.modeOption(), sortKeysModeStack, ctx.bracketBlock(), setSortKeysMode);
	}

	@Override
	public Object visitColorsDirective(CalcParser.ColorsDirectiveContext ctx) {
	    return processModeOption(ctx.modeOption(), coloredModeStack, ctx.bracketBlock(), setColoredMode);
	}


	private String versionText(CalcParser.VersionNumberContext versionCtx) {
	    if (versionCtx == null)
		return null;
	    else if (versionCtx.STRING() != null)
		return getRawString(versionCtx.STRING().getText());
	    else if (versionCtx.ISTRING() != null)
		return getIStringValue(this, versionCtx.ISTRING(), versionCtx);
	    else if (versionCtx.NUMBER() != null)
		return versionCtx.NUMBER().getText();
	    else
		return versionCtx.VERSION().getText();
	}

	private void checkVersionText(CalcParser.VersionNumberContext requireCtx, CalcParser.VersionNumberContext baseCtx) {
	    checkRequiredVersions(versionText(requireCtx), versionText(baseCtx));
	}

	@Override
	public Object visitRequireDirective(CalcParser.RequireDirectiveContext ctx) {
	    CalcParser.RequireOptionsContext optCtx = ctx.requireOptions();
	    List<CalcParser.VersionNumberContext> versionNumbers = optCtx.versionNumber();

	    if (optCtx.BASE() != null) {
		if (versionNumbers.size() == 1) {
		    // Just a base version
		    checkVersionText(null, versionNumbers.get(0));
		}
		else {
		    // version + base version
		    checkVersionText(versionNumbers.get(0), versionNumbers.get(1));
		}
	    }
	    else {
		// Just a regular version
		checkVersionText(versionNumbers.get(0), null);
	    }

	    return Boolean.TRUE;
	}

	@Override
	public Object visitAssertDirective(CalcParser.AssertDirectiveContext ctx) {
	    CalcParser.ExprContext expr1 = ctx.expr(0);
	    CalcParser.ExprContext expr2 = ctx.expr(1);
	    String assertMessage = "";

	    // expr1 is the asserted expression
	    // expr2 is the optional message, which doesn't need to be evaluated until and unless
	    // the assert fails
	    boolean value = getBooleanValue(expr1);
	    if (!value) {
		assertMessage = Intl.formatString("calc#assertFailure",
			expr2 != null ? getStringValue(expr2) : getTreeText(expr1));
		throw new AssertException(assertMessage, ctx);
	    }
	    return Boolean.TRUE;
	}

	@Override
	public Object visitQuitDirective(CalcParser.QuitDirectiveContext ctx) {
	    Calc.exit();
	    return null;
	}

	@Override
	public Object visitHelpDirective(CalcParser.HelpDirectiveContext ctx) {
	    Calc.printIntro();
	    Calc.displayHelp();
	    return null;
	}

	@Override
	public Object visitVersionDirective(CalcParser.VersionDirectiveContext ctx) {
	    Calc.printTitleAndVersion();
	    return null;
	}

	@Override
	public Object visitGuiDirective(CalcParser.GuiDirectiveContext ctx) {
	    Calc.doGuiMode(new String[0]);
	    return null;
	}



	private boolean isEmptyStmt(final ParseTree root) {
	    ParseTree node = root;
	    while (node != null) {
		if (node instanceof CalcParser.EmptyStmtContext)
		    return true;

		if (node instanceof TerminalNode) {
		    TerminalNode terminal = (TerminalNode) node;
		    int type = terminal.getSymbol().getType();
		    switch (type) {
			case Token.EOF:
			case CalcParser.EOL:
			case CalcParser.RPAREN:
			case CalcParser.RBRACE:
			case CalcParser.RBRACK:
			    return true;
		    }
		}

		if (node instanceof ParserRuleContext)
		    node = ((ParserRuleContext) node).children.get(0);
		else
		    node = null;
	    }
	    return false;
	}

	private Object internalVisitStatements(final ParserRuleContext ctx) {
	    Object returnValue = null;

	    for (ParseTree child : ctx.children) {
		if (!isEmptyStmt(child)) {
		    returnValue = visit(child);
		}
	    }

	    return returnValue;
	}

	@Override
	public Object visitProg(CalcParser.ProgContext ctx) {
	    return internalVisitStatements(ctx);
	}

	@Override
	public Object visitStmtOrExpr(CalcParser.StmtOrExprContext ctx) {
	    return internalVisitStatements(ctx);
	}

	@Override
	public Object visitBracketBlock(CalcParser.BracketBlockContext ctx) {
	    return internalVisitStatements(ctx);
	}

	/**
	 * Produce a constant string in the form of <code>X'xxxx'</code>, unless
	 * the sign modifier is <code>'-'</code> in which case only the inner value
	 * is used (but double quoted).
	 *
	 * @param buf    The buffer to add the constant to.
	 * @param sign   Either a space ({@code ' '}) or minus ({@code '-'}).
	 * @param type   The constant type character.
	 * @param value  The actual constant value.
	 */
	private void constant(StringBuilder buf, char sign, char type, String value) {
	    boolean quote = (sign == ' ');
	    if (quote)
		buf.append(type).append('\'');
	    else if (settings.quoteStrings)
		buf.append('"');

	    buf.append(value);

	    if (quote)
		buf.append('\'');
	    else if (settings.quoteStrings)
		buf.append('"');
	}

	@Override
	public Object visitExprStmt(CalcParser.ExprStmtContext ctx) {
	    CalcParser.ExprContext expr = ctx.expr();
	    Object result               = evaluate(expr);
	    String resultString         = "";
	    String stringValue;

	    BigInteger iValue = null;
	    BigDecimal dValue = null;

	    int precision = Integer.MIN_VALUE;
	    int scale     = Integer.MIN_VALUE;
	    int levels    = Integer.MIN_VALUE;
	    boolean separators = false;
	    boolean addQuotes  = false;
	    char signChar      = ' ';
	    char formatChar    = ' ';
	    char modifierChar  = ' ';

	    TerminalNode formatNode = ctx.FORMAT();
	    String format           = formatNode == null ? "" : " " + formatNode.getText();
	    String exprString       = String.format("%1$s%2$s", getTreeText(ctx.expr()), format);

	    if (!format.isEmpty()) {
		// Some formats allow a precision to be given ('%', '$', and 'd' for instance)
		// but some others (like 't') allow a second alpha as well
		Matcher m = FORMAT_PATTERN.matcher(format);
		if (m.matches()) {
		    String signStr  = m.group(1);
		    String precStr  = m.group(2);
		    String scaleStr = m.group(5);
		    String levelStr = m.group(7);
		    String modStr   = m.group(8);
		    String formStr  = m.group(9);

		    if (signStr != null)
			signChar = signStr.charAt(0);
		    if (precStr != null)
			precision = Integer.parseInt(precStr);
		    if (scaleStr != null)
			scale = Integer.parseInt(scaleStr);
		    if (levelStr != null)
			levels = Integer.parseInt(levelStr);
		    if (modStr != null)
			modifierChar = modStr.charAt(0);
		    formatChar = formStr.charAt(0);
		}
	    }
	    separators = modifierChar == ',' || (settings.separatorMode && modifierChar != '_');

	    if (result != null && !format.isEmpty()) {
		// Some formats have special characteristics
		char formatTestChar = Character.toLowerCase(formatChar);
		if (formatTestChar != 'j' && formatTestChar != 'd') {
		    if (scale != Integer.MIN_VALUE)
			throw new CalcExprException(ctx, "%calc#noScaleFormat", scale, formatChar);
		}
		if (formatTestChar != 'j' && formatTestChar != 'q' && formatTestChar != 's') {
		    if (result instanceof Scope)
			throw new CalcExprException(ctx, "%calc#noConvertObjArr", formatChar);
		}

		StringBuilder valueBuf = new StringBuilder();
		StringBuffer buf       = new StringBuffer();
		boolean toUpperCase    = false;
		boolean toLowerCase    = false;

		switch (formatChar) {
		    case 'H':
			toUpperCase = true;
			// fall through
		    case 'h':
			char meridianFlag = modifierChar;
			// Value will be nanoseconds since midnight
			iValue = convertToInteger(result, settings.mc, ctx);
			constant(valueBuf, signChar, 'h',
				NumericUtil.convertToTime(iValue.longValue(), meridianFlag));
			break;
		    case 'T':
			toUpperCase = true;
			// fall through
		    case 't':
			char durationUnit = modifierChar;
			// Value will be nanoseconds
			iValue = convertToInteger(result, settings.mc, ctx);
			constant(valueBuf, signChar, 't',
				NumericUtil.convertToDuration(iValue, durationUnit, settings.mcDivide, precision));
			break;

		    case 'U':
		    case 'u':
			toUpperCase = true;
			valueBuf.append(toStringValue(this, ctx, result, new StringFormat(settings)));
			break;
		    case 'L':
		    case 'l':
			toLowerCase = true;
			valueBuf.append(toStringValue(this, ctx, result, new StringFormat(settings)));
			break;

		    case 'Q':
		    case 'q':
			stringValue = toStringValue(this, ctx, result, new StringFormat(formatChar == 'Q', settings));
			if (formatChar == 'Q')
			    valueBuf.append(ConsoleColor.uncolor(stringValue));
			else
			    valueBuf.append(stringValue);
			break;

		    case 'C':
		    case 'c':
			iValue = convertToInteger(result, settings.mc, ctx);
			try {
			    int cValue = iValue.intValueExact();
			    if (cValue < 0 || cValue > 0x10FFFF)
				throw new CalcExprException(ctx, "%calc#charOutOfRange", cValue);
			    valueBuf.append('"').appendCodePoint(cValue).append('"');
			}
			catch (ArithmeticException ae) {
			    throw new CalcExprException(ae, ctx);
			}
			break;

		    case 'D':
		    case 'd':
			// special case for a one character string -> codepoint
			if (result instanceof String) {
			    stringValue = (String) result;
			    int count = Character.codePointCount(stringValue, 0, stringValue.length());
			    if (count == 1) {
				int cp = Character.codePointAt(stringValue, 0);
				dValue = new BigDecimal(cp);
			    }
			}

			// This is the default handling for all other cases
			if (dValue == null) {
			    dValue = convertToDecimal(result, settings.mc, ctx);
			}

			if (precision != Integer.MIN_VALUE) {
			    dValue = MathUtil.round(dValue, precision);
			}
			valueBuf.append(Num.formatWithSeparators(dValue, separators, scale));
			break;

		    case 'I':
		    case 'i':
			ComplexNumber c = null;
			if (result instanceof ComplexNumber) {
			    c = (ComplexNumber) result;
			}
			else {
			    c = ComplexNumber.real(convertToDecimal(result, settings.mc, ctx));
			}
			valueBuf.append(c.toLongString(formatChar == 'I', separators));
			break;

		    case 'P':
		    case 'p':
			ComplexNumber c2 = null;
			if (result instanceof ComplexNumber) {
			    c2 = (ComplexNumber) result;
			}
			else {
			    c2 = ComplexNumber.real(convertToDecimal(result, settings.mc, ctx));
			}
			valueBuf.append(c2.toPolarString(formatChar == 'P', separators,
				MathUtil.divideContext(c2, settings.mcDivide)));
			break;

		    // @E = US format: MM/dd/yyyy
		    // @e = ISO format: yyyy-MM-dd
		    case 'E':
		    case 'e':
			char dateChar = (formatChar == 'E') ? 'D' : 'd';
			iValue = convertToInteger(result, settings.mc, ctx);
			try {
			    LocalDate date = LocalDate.ofEpochDay(iValue.longValue());
			    int year = date.getYear();
			    String dateStr;
			    if (formatChar == 'E') {
				if (year < 0)
				    dateStr = String.format("%1$02d/%2$02d/-%3$04d",
					date.getMonthValue(), date.getDayOfMonth(), -year);
				else
				    dateStr = String.format("%1$02d/%2$02d/%3$04d",
					date.getMonthValue(), date.getDayOfMonth(), year);
			    }
			    else {
				if (year < 0)
				    dateStr = String.format("-%1$04d-%2$02d-%3$02d",
					-year, date.getMonthValue(), date.getDayOfMonth());
				else
				    dateStr = String.format("%1$04d-%2$02d-%3$02d",
					year, date.getMonthValue(), date.getDayOfMonth());
			    }
			    constant(valueBuf, signChar, dateChar, dateStr);
			}
			catch (DateTimeException ex) {
			    throw new CalcExprException(ex, ctx);
			}
			break;

		    case 'f':
			valueBuf.append(convertToFraction(result, ctx).toFormatString(separators));
			break;
		    case 'F':
			valueBuf.append(convertToFraction(result, ctx).toProperString(separators));
			break;

		    case 'J':
		    case 'j':
			if (signChar != '-')
			    valueBuf.append('\n');

			String indent = "";
			String increment = null;
			int skip = 0;

			if (precision != Integer.MIN_VALUE)
			    indent = CharUtil.padToWidth("", Math.abs(precision));
			if (scale != Integer.MIN_VALUE)
			    increment = CharUtil.padToWidth("", scale);
			if (levels != Integer.MIN_VALUE)
			    skip = levels;
			if (skip == 0)
			    valueBuf.append(indent);
			valueBuf.append(toStringValue(this, ctx, result,
				new StringFormat(settings.quoteStrings, true, (signChar != '-'),
					separators, increment, skip), indent, 0));
			break;

		    case 'X':
		    case 'x':
			if (result instanceof String) {
			    byte[] b = ((String) result).getBytes(StandardCharsets.UTF_8);
			    valueBuf.append('\'');
			    convert(b, 16, formatChar == 'X', true, valueBuf);
			    valueBuf.append('\'');
			}
			else {
			    valueBuf.append('0').append(formatChar);
			    iValue = convertToInteger(result, settings.mc, ctx);
			    if (iValue.compareTo(BigInteger.ZERO) < 0)
				convert(iValue.toByteArray(), 16, formatChar == 'X', false, valueBuf);
			    else {
				toUpperCase = formatChar == 'X';
				valueBuf.append(iValue.toString(16));
			    }
			}
			break;

		    case 'O':
		    case 'o':
			if (result instanceof String) {
			    byte[] b = ((String) result).getBytes(StandardCharsets.UTF_8);
			    valueBuf.append('\'');
			    convert(b, 8, false, true, valueBuf);
			    valueBuf.append('\'');
			}
			else {
			    valueBuf.append('0');
			    iValue = convertToInteger(result, settings.mc, ctx);
			    if (iValue.compareTo(BigInteger.ZERO) < 0)
				convert(iValue.toByteArray(), 8, false, false, valueBuf);
			    else
				valueBuf.append(iValue.toString(8));
			}
			break;

		    case 'B':
		    case 'b':
			if (result instanceof String) {
			    byte[] b = ((String) result).getBytes(StandardCharsets.UTF_8);
			    valueBuf.append('\'');
			    convert(b, 2, false, true, valueBuf);
			    valueBuf.append('\'');
			}
			else {
			    valueBuf.append('0').append(formatChar);
			    iValue = convertToInteger(result, settings.mc, ctx);
			    if (iValue.compareTo(BigInteger.ZERO) < 0)
				convert(iValue.toByteArray(), 2, false, false, valueBuf);
			    else
				valueBuf.append(iValue.toString(2));
			}
			break;

		    case 'R':
			toUpperCase = true;
			// fall through
		    case 'r':
			iValue = convertToInteger(result, settings.mc, ctx);
			try {
			    int intValue = iValue.intValueExact();
			    constant(valueBuf, signChar, 'r', NumericUtil.convertToRoman(intValue, false));
			}
			catch (ArithmeticException | IllegalArgumentException ex) {
			    throw new CalcExprException(ex, ctx);
			}
			break;

		    case 'K':
		    case 'k':
			iValue = convertToInteger(result, settings.mc, ctx);
			valueBuf.append(formatChar == 'K'
			  ? NumericUtil.formatToRangeLong(iValue, settings.units)
			  : NumericUtil.formatToRange(iValue, settings.units));
			break;

		    case 'W':
			toUpperCase = true;
			// fall through
		    case 'w':
			iValue = convertToInteger(result, settings.mc, ctx);
			try {
			    NumericUtil.convertToWords(iValue, valueBuf, separators, signChar == '+');
			}
			catch (IllegalArgumentException iae) {
			    throw new CalcExprException(iae, ctx);
			}
			break;

		    case '%':
			dValue = convertToDecimal(result, settings.mc, ctx);
			NumberFormat percentFormat = NumberFormat.getPercentInstance();
			// Round the value to given precision (if any)
			if (precision != Integer.MIN_VALUE) {
			    // Precision here is digits in the fraction portion, but because we're effectively
			    // multiplying by 100, we need to get 2 more decimal places out of the precision
			    dValue = MathUtil.round(dValue, precision + 2);
			    percentFormat.setMinimumFractionDigits(precision);
			    percentFormat.setMaximumFractionDigits(precision);
			}
			percentFormat.setGroupingUsed(separators);
			buf.setLength(0);
			percentFormat.format(dValue, buf, new FieldPosition(NumberFormat.Field.PERCENT));
			valueBuf.append(buf);
			break;

		    case '$':
			dValue = convertToDecimal(result, settings.mc, ctx);
			NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
			if (precision != Integer.MIN_VALUE) {
			    dValue = MathUtil.round(dValue, precision);
			    currencyFormat.setMinimumFractionDigits(precision);
			    currencyFormat.setMaximumFractionDigits(precision);
			}
			currencyFormat.setGroupingUsed(separators);
			buf.setLength(0);
			currencyFormat.format(dValue, buf, new FieldPosition(NumberFormat.Field.CURRENCY));
			valueBuf.append(buf);
			break;

		    case 'S':
		    case 's':
			if (result instanceof Scope) {
			    boolean extraSpace = signChar != '-';
			    valueBuf.append(toStringValue(this, ctx, result,
				new StringFormat(true, false, extraSpace, separators, null, 0), "", 0));
			}
			else {
			    stringValue = toStringValue(this, ctx, result, new StringFormat(false, separators));
			    switch (signChar) {
				case '+':	/* center - positive width puts extra spaces on left always */
				    CharUtil.padToWidth(valueBuf, stringValue, precision, CENTER);
				    break;
				case '-':	/* right-justify (spaces on left) */
				    CharUtil.padToWidth(valueBuf, stringValue, Math.abs(precision), RIGHT);
				    break;
				default:	/* left-justify (spaces on right) */
				    CharUtil.padToWidth(valueBuf, stringValue, precision, LEFT);
				    break;
			    }
			}
			addQuotes = settings.quoteStrings;
			break;

		    default:
			throw new CalcExprException(ctx, "%calc#illegalFormat", formatNode.getText());
		}

		resultString = valueBuf.toString();

		if (toUpperCase)
		    resultString = resultString.toUpperCase();
		else if (toLowerCase)
		    resultString = resultString.toLowerCase();
		else if (addQuotes)
		    resultString = CharUtil.addDoubleQuotes(CharUtil.quoteControl(resultString));

		// Set the "result" for the case of interpolated strings with formats
		result = resultString;
	    }
	    else {
		// For large numbers it takes a significant amount of time just to convert
		// to a string, so don't even try if we don't need to for display
		if (!settings.silent)
		    resultString = toStringValue(this, expr, result, new StringFormat(settings, separators));
	    }

	    if (!settings.silent) displayer.displayResult(exprString, resultString);

	    return result;

	}

	private class LoopVisitor implements IterationVisitor
	{
		private CalcParser.StmtBlockContext block;
		private LoopScope localScope;
		private String localVarName;
		private Object lastValue = null;

		LoopVisitor(final CalcParser.StmtBlockContext blockContext, final LoopScope scope, final String varName) {
		    block        = blockContext;
		    localScope   = scope;
		    localVarName = varName;
		}

		@Override
		public void start() {
		    pushScope(localScope);
		}

		@Override
		public Object apply(final Object value) {
		    currentScope.clear();
		    currentScope.setValue(localVarName, value);
		    try {
			lastValue = evaluate(block);
		    }
		    catch (NextException next) {
			// Note: don't update lastValue in this case
		    }
		    return lastValue;
		}

		@Override
		public void finish() {
		    popScope();
		}
	}

	/**
	 * Iterate over a range given by {@code [ x .. ] y [ , n ]} for various purposes,
	 * like: <ul>
	 * <li>"case" selector
	 * <li>"sumof" operation
	 * <li>"productof" operation
	 * <li>"arrayof" operator
	 * <li>"loop" statement
	 * </ul>
	 *
	 * @param valueExprs  A list of values to iterate over.
	 * @param dotExprs    Or the "dot" expressions, only one of which is required.
	 * @param hasDots     Whether we have the ".." part or not.
	 * @param visitor     The piece that acts on each value in the range or list.
	 * @param allowSingle If {@code true} then the expression is allowed to be a single
	 *                    array or object which we will iterate through.
	 * @param doingWithin Indicator of the "within" keyword, which subtly changes the
	 *                    meaning of a single-valued expression (that is, "23" using "in"
	 *                    means "1..23" while "within" means "0..22").
	 * @return Whatever the "last" value is as determined by the visitor (could be a sum,
	 *         a product, the result of the selected block in a case statement, etc.)
	 */
	private Object iterateOverDotRange(
		final List<CalcParser.ExprContext> valueExprs,
		final List<CalcParser.ExprContext> dotExprs,
		final boolean hasDots,
		final IterationVisitor visitor,
		final boolean allowSingle,
		final boolean doingWithin)
	{
	    List<CalcParser.ExprContext> exprs = null;
	    CalcParser.ExprContext stepExpr = null;
	    Iterator<Object> iter = null;
	    java.util.stream.IntStream codePoints  = null;

	    boolean stepWise = false;
	    boolean allowWithin = false;

	    BigDecimal dStart = BigDecimal.ONE;
	    BigDecimal dStop  = BigDecimal.ONE;
	    BigDecimal dStep  = BigDecimal.ONE;

	    int start = 1;
	    int stop  = 1;
	    int step  = 1;

	    Object lastValue = null;

	    if (valueExprs != null) {
		// This is only true if we have "expr , expr (, expr)*"
		// or more than one separated by commas
		exprs = valueExprs;
	    }
	    else {
		exprs = dotExprs;
		stepWise = true;

		if (exprs.size() == 0) {
		    // This is the case of '(' ')', or an empty list
		    return lastValue;
		}
		else if (exprs.size() == 1) {
		    // number of times, starting from 1
		    if (allowSingle) {
			// or it could be an array, object, or string to iterate over
			Object obj = evaluate(exprs.get(0));
			if (obj instanceof ObjectScope) {
			    stepWise = false;
			    @SuppressWarnings("unchecked")
			    ObjectScope map = (ObjectScope) obj;
			    iter = map.keyObjectSet().iterator();
			}
			else if (obj instanceof ArrayScope) {
			    stepWise = false;
			    @SuppressWarnings("unchecked")
			    ArrayScope<Object> array = (ArrayScope<Object>) obj;
			    iter = array.list().iterator();
			}
			else if (obj instanceof SetScope) {
			    stepWise = false;
			    @SuppressWarnings("unchecked")
			    SetScope<Object> set = (SetScope<Object>) obj;
			    iter = set.set().iterator();
			}
			else if (obj instanceof String) {
			    stepWise = false;
			    codePoints = ((String) obj).codePoints();
			}
			else {
			    dStop = getDecimalValue(exprs.get(0));
			    allowWithin = true;
			}
		    }
		    else {
			dStop = getDecimalValue(exprs.get(0));
			allowWithin = true;
		    }
		}
		else if (exprs.size() == 2) {
		    if (hasDots) {
			// start .. stop
			dStart = getDecimalValue(exprs.get(0));
			dStop  = getDecimalValue(exprs.get(1));
		    }
		    else {
			// stop, step
			stepExpr = exprs.get(1);
			dStop = getDecimalValue(exprs.get(0));
			dStep = getDecimalValue(stepExpr);
			allowWithin = true;
		    }
		}
		else {
		    // start .. stop, step
		    stepExpr = exprs.get(2);
		    dStart = getDecimalValue(exprs.get(0));
		    dStop  = getDecimalValue(exprs.get(1));
		    dStep  = getDecimalValue(stepExpr);
		}
	    }

	    // Do any initialization required for the visitor before the actual iteration begins
	    visitor.start();

	    if (stepWise) {
		// Try to convert loop values to exact integers if possible
		try {
		    start = dStart.intValueExact();
		    stop  = dStop.intValueExact();
		    step  = dStep.intValueExact();

		    if (step == 0)
			throw new CalcExprException("%calc#infLoopStepZero", stepExpr);

		    if (allowWithin) {
			if (doingWithin) {
			    start = 0;
			    if (step < 0)
				stop++;
			    else
				stop--;
			}
			else if (step < 0) {
			    start = -start;
			}
		    }
		    else if (doingWithin) {
			throw new CalcExprException("%calc#withinNotAllowed", exprs.get(0));
		    }

		    // Try to apply optimization if possible to avoid stupidly running through
		    // all the values if we don't need to
		    boolean skipLoop = false;
		    if (visitor.getPurpose() != Purpose.ALL) {
			try {
			    lastValue = visitor.finalValue(start, stop, step);
			    skipLoop = true;
			}
			catch (IllegalArgumentException iae) {
			    ;
			}
		    }
		    if (!skipLoop) {
			if (step < 0) {
			    for (int loopIndex = start; loopIndex >= stop; loopIndex += step) {
				lastValue = visitor.apply(BigInteger.valueOf(loopIndex));
			    }
			}
			else {
			    for (int loopIndex = start; loopIndex <= stop; loopIndex += step) {
				lastValue = visitor.apply(BigInteger.valueOf(loopIndex));
			    }
			}
		    }
		}
		catch (ArithmeticException ae) {
		    // This means we stubbornly have fractional values, so use as such
		    int sign = dStep.signum();

		    if (sign == 0)
			throw new CalcExprException("%calc#infLoopStepZero", stepExpr);

		    if (allowWithin) {
			if (doingWithin) {
			    dStart = BigDecimal.ZERO;
			    if (sign < 0)
				dStop = dStop.add(BigDecimal.ONE);
			    else
				dStop = dStop.subtract(BigDecimal.ONE);
			}
			else if (step < 0) {
			    dStart = dStart.negate();
			}
		    }
		    else if (doingWithin) {
			throw new CalcExprException("%calc#withinNotAllowed", exprs.get(0));
		    }

		    // Try to apply an optimization if possible to avoid running through all the values if
		    // we can get the result some other way (such as for "sumof").
		    boolean skipLoop = false;
		    if (visitor.getPurpose() != Purpose.ALL) {
			try {
			    lastValue = visitor.finalValue(dStart, dStop, dStep);
			    skipLoop = true;
			}
			catch (IllegalArgumentException iae) {
			    ;
			}
		    }
		    if (!skipLoop) {
			if (sign < 0) {
			    for (BigDecimal loopIndex = dStart; loopIndex.compareTo(dStop) >= 0; loopIndex = loopIndex.add(dStep)) {
				lastValue = visitor.apply(fixupToInteger(loopIndex));
			    }
			}
			else {
			    for (BigDecimal loopIndex = dStart; loopIndex.compareTo(dStop) <= 0; loopIndex = loopIndex.add(dStep)) {
				lastValue = visitor.apply(fixupToInteger(loopIndex));
			    }
			}
		    }
		}
		finally {
		    visitor.finish();
		}
	    }
	    else {
		try {
		    if (iter != null) {
			while (iter.hasNext()) {
			    Object value = iter.next();
			    lastValue = visitor.apply(value);
			}
		    }
		    else if (codePoints != null) {
			StringBuilder buf = new StringBuilder(4);
			for (Iterator<Integer> intIter = codePoints.iterator(); intIter.hasNext(); ) {
			    Integer cp = intIter.next();
			    buf.setLength(0);
			    buf.appendCodePoint(cp);
			    lastValue = visitor.apply(buf.toString());
			}
		    }
		    else {
			for (CalcParser.ExprContext expr : exprs) {
			    lastValue = visitor.apply(evaluate(expr));
			}
		    }
		}
		finally {
		    visitor.finish();
		}
	    }

	    return lastValue;
	}

	/**
	 * Do a compare of the label given to this loop/while/function with any label specified
	 * on the "leave" statement. If both are empty, we have a match, or if both are given
	 * and they match according to the case-sensitivity rules currently in effect, then we
	 * have a match. Otherwise not.
	 *
	 * @param loopLabel  The optional loop/while label, or function name.
	 * @param leaveLabel The also optional label given in the "leave" statement.
	 * @return           Whether they match according to the rules.
	 */
	private boolean compareLabels(String loopLabel, String leaveLabel) {
	    boolean emptyLoop  = CharUtil.isNullOrEmpty(loopLabel);
	    boolean emptyLeave = CharUtil.isNullOrEmpty(leaveLabel);

	    if (emptyLoop && emptyLeave)
		return true;
	    if ((emptyLoop && !emptyLeave) || (!emptyLoop && emptyLeave))
		return false;

	    if (settings.ignoreNameCase)
		return loopLabel.equalsIgnoreCase(leaveLabel);
	    else
		return loopLabel.equals(leaveLabel);
	}

	@Override
	public Object visitLoopStmt(CalcParser.LoopStmtContext ctx) {
	    CalcParser.LoopLabelContext label   = ctx.loopLabel();
	    CalcParser.IdContext id             = ctx.id();
	    CalcParser.LoopCtlContext ctlCtx    = ctx.loopCtl();
	    CalcParser.StmtBlockContext block   = ctx.stmtBlock();
	    CalcParser.ExprListContext exprList = ctlCtx.exprList();
	    CalcParser.DotRangeContext dotCtx   = ctlCtx.dotRange();

	    String localVarName = id != null ? id.getText() : LoopScope.LOOP_VAR;
	    String loopLabel = label != null ? label.id().getText() : null;

	    boolean doingWithin = ctx.K_WITHIN() != null;

	    LoopVisitor visitor = new LoopVisitor(block, new LoopScope(), localVarName);

	    Object value = null;

	    try {
		if (exprList != null)
		    value = iterateOverDotRange(exprList.expr(), null, false, visitor, true, doingWithin);
		else
		    value = iterateOverDotRange(null, dotCtx.expr(), dotCtx.DOTS() != null, visitor, true, doingWithin);
	    }
	    catch (LeaveException lex) {
		if (compareLabels(loopLabel, lex.getLabel())) {
		    if (lex.hasValue()) {
			value = lex.getValue();
		    }
		}
		else {
		    throw lex;
		}
	    }

	    return value;
	}

	@Override
	public Object visitWhileStmt(CalcParser.WhileStmtContext ctx) {
	    CalcParser.LoopLabelContext label = ctx.loopLabel();
	    CalcParser.ExprContext exprCtx    = ctx.expr();
	    CalcParser.StmtBlockContext block = ctx.stmtBlock();

	    Object lastValue = null;
	    String loopLabel = label != null ? label.id().getText() : null;

	    boolean exprResult = getBooleanValue(exprCtx);

	    pushScope(new WhileScope());
	    try {
		while (exprResult) {
		    currentScope.clear();
		    try {
			lastValue = evaluate(block);
		    }
		    catch (NextException next) {
			// Note: lastValue should not be updated here
		    }
		    exprResult = getBooleanValue(exprCtx);
		}
	    }
	    catch (LeaveException lex) {
		if (compareLabels(loopLabel, lex.getLabel())) {
		    if (lex.hasValue()) {
			lastValue = lex.getValue();
		    }
		}
		else {
		    throw lex;
		}
	    }
	    finally {
		popScope();
	    }

	    return lastValue;
	}

	@Override
	public Object visitIfStmt(CalcParser.IfStmtContext ctx) {
	    CalcParser.ExprContext exprCtx        = ctx.expr();
	    CalcParser.StmtBlockContext thenBlock = ctx.stmtBlock(0);
	    CalcParser.StmtBlockContext elseBlock = ctx.stmtBlock(1);
	    Object resultValue = null;

	    boolean controlValue = getBooleanValue(exprCtx);

	    pushScope(new IfScope());
	    try {
		if (controlValue) {
		    resultValue = evaluate(thenBlock);
		}
		else if (elseBlock != null) {
		    resultValue = evaluate(elseBlock);
		}
	    }
	    finally {
		popScope();
	    }

	    return resultValue;
	}

	private class CaseVisitor implements IterationVisitor
	{
		private CalcParser.CaseBlockContext blockCtx;
		private CalcParser.ExprContext caseExpr;
		private Object caseValue;
		private NestedScope caseScope;
		private Object blockValue = null;
		private boolean isMatched = false;
		private boolean fallThrough = false;

		public CaseVisitor(
			final CalcParser.ExprContext expr,
			final Object value,
			final NestedScope scope) {
		    caseExpr  = expr;
		    caseValue = value;
		    caseScope = scope;
		}

		public void setBlock(final CalcParser.CaseBlockContext block) {
		    blockCtx = block;
		}

		@Override
		public Purpose getPurpose() {
		    return Purpose.SELECT;
		}

		@Override
		public Object apply(final Object value) {
		    if (!isMatched
		     && CalcUtil.compareValues(CalcObjectVisitor.this, caseExpr, blockCtx, caseValue, value, settings.mc, false, true, false, false, true) == 0) {
			blockValue = execute();
		    }
		    return blockValue;
		}

		@Override
		public Object finalValue(final Number start, final Number stop, final Number step) {
		    isMatched = IterationVisitor.containedIn(CalcObjectVisitor.this, caseValue, start, stop, step, caseExpr);
		    if (isMatched) {
			blockValue = execute();
		    }
		    return blockValue;
		}

		public Object execute() {
		    Object returnValue = null;
		    pushQuietMode.accept(true);
		    pushScope(caseScope);
		    try {
			returnValue = evaluate(blockCtx.stmtBlock());
		    }
		    catch (NextException next) {
			fallThrough = true;
		    }
		    finally {
			popScope();
			pushQuietMode.accept(ModeOption.POP);
			isMatched = true;
		    }
		    return returnValue;
		}

		public Object lastValue() {
		    return blockValue;
		}

		public boolean matched() {
		    return isMatched;
		}

		public boolean fallIntoNext() {
		    return fallThrough;
		}
	}

	/**
	 * Convert our set of pattern flags (such as {@link #MATCH_CASE_INSENSITIVE}, or
	 * {@link #MATCH_DOTALL}) to the flags defined in {@link Pattern}.
	 *
	 * @param flags The set of our flags, set by the script.
	 * @return      Set of flags used by the regex code.
	 * @throws IllegalArgumentException if invalid flags are set.
	 */
	private int patternFlags(int flags) {
	    int matchFlags = 0;

	    if ((flags & ~MATCH_ALL_FLAGS) != 0)
		throw new Intl.IllegalArgumentException("calc#illegalFlagValuess", flags);

	    if ((flags & MATCH_CASE_INSENSITIVE) != 0)
		matchFlags |= Pattern.CASE_INSENSITIVE;
	    if ((flags & MATCH_DOTALL) != 0)
		matchFlags |= Pattern.DOTALL;
	    if ((flags & MATCH_UNICODE_CASE) != 0)
		matchFlags |= Pattern.UNICODE_CASE;
	    if ((flags & MATCH_LITERAL) != 0)
		matchFlags |= Pattern.LITERAL;
	    if ((flags & MATCH_MULTILINE) != 0)
		matchFlags |= Pattern.MULTILINE;
	    if ((flags & MATCH_UNIX_LINES) != 0)
		matchFlags |= Pattern.UNIX_LINES;

	    return matchFlags;
	}

	private boolean matches(String input, String pattern, int flags) {
	    Pattern p = Pattern.compile(pattern, patternFlags(flags));
	    Matcher m = p.matcher(input);

	    return m.matches();
	}

	@Override
	public Object visitCaseStmt(CalcParser.CaseStmtContext ctx) {
	    CalcParser.ExprContext caseExpr = ctx.expr();
	    Object caseValue = evaluate(caseExpr);
	    List<CalcParser.CaseBlockContext> blocks = ctx.caseBlock();
	    CalcParser.CaseBlockContext defaultCtx = null;
	    CaseScope scope = new CaseScope();
	    CaseVisitor visitor = new CaseVisitor(caseExpr, caseValue, scope);
	    StringFormat format = new StringFormat(false, false);
	    boolean fallThrough = false;
	    Object returnValue = null;

	    for (CalcParser.CaseBlockContext cbCtx : blocks) {
		visitor.setBlock(cbCtx);

		if (fallThrough) {
		    returnValue = visitor.execute();
		    fallThrough = visitor.fallIntoNext();
		    if (fallThrough)
			continue;

		    return returnValue;
		}

	      selectors:
		for (CalcParser.CaseSelectorContext select : cbCtx.caseSelector()) {
		    if (select.K_DEFAULT() != null) {
			defaultCtx = cbCtx;
		    }
		    else if (select.DOTS() != null) {
			iterateOverDotRange(null, select.expr(), true, visitor, true, false);
			if (visitor.fallIntoNext()) {
			    fallThrough = true;
			    break selectors;
			}
			else if (visitor.matched()) {
			    return visitor.lastValue();
			}
		    }
		    else if (select.K_MATCHES() != null) {
			CalcParser.Expr2Context e2ctx = select.expr2();
			String pattern;
			int flags = 0x0000;

			if (e2ctx != null) {
			    pattern = getStringValue(e2ctx.expr(0));
			    flags = getIntValue(e2ctx.expr(1));
			}
			else {
			    CalcParser.Expr1Context e1ctx = select.expr1();
			    pattern = getStringValue(e1ctx.expr());
			}

			String input = toStringValue(this, caseExpr, caseValue, format);
			if (matches(input, pattern, flags)) {
			    returnValue = visitor.execute();
			    fallThrough = visitor.fallIntoNext();
			    if (fallThrough)
				break selectors;

			    return returnValue;
			}
		    }
		    else if (!select.compareOp().isEmpty()) {
			String op = select.compareOp(0).getText();
			CalcParser.ExprContext expr = select.expr(0);

			boolean first = compareOp(caseExpr, expr, Optional.ofNullable(caseValue), op).booleanValue();
			boolean matched = false;

			if (select.boolOp() == null) {
			    matched = first;
			}
			else {
			    String boolOp = select.boolOp().getText();

			    // Some combinations can be solved with just the first result
			    switch (boolOp) {
				case "&&":
				case "\u2227":
				    if (!first)
					continue selectors;
				    break;
				case "||":
				case "\u2228":
				    if (first)
					matched = true;
				    break;
			    }
			    if (!matched) {
				op = select.compareOp(1).getText();
				expr = select.expr(1);
				boolean second = compareOp(caseExpr, expr, Optional.ofNullable(caseValue), op).booleanValue();

				switch (boolOp) {
				    case "&&":
				    case "\u2227":
				    case "||":
				    case "\u2228":
					if (second)
					    matched = true;
					break;
				    default:
					// XOR - results must be different
					matched = (first != second);
					break;
				}
			    }
			}
			if (matched) {
			    returnValue = visitor.execute();
			    fallThrough = visitor.fallIntoNext();
			    if (fallThrough)
				break selectors;

			    return returnValue;
			}
		    }
		    else {
			CalcParser.ExprContext expr = select.expr().get(0);
			Object value = evaluate(expr);
			returnValue = visitor.apply(value);
			if (visitor.fallIntoNext()) {
			    fallThrough = true;
			    break selectors;
			}
			else if (visitor.matched()) {
			    return returnValue;
			}
		    }
		}
	    }

	    if (defaultCtx != null) {
		visitor.setBlock(defaultCtx);
		returnValue = visitor.execute();
		fallThrough = visitor.fallIntoNext();
		if (!fallThrough)
		    return returnValue;

		// Hmmm, we need to find the selector after the default...
		boolean seenDefault = false;

	      blocks:
		for (CalcParser.CaseBlockContext cbCtx : blocks) {
		    if (seenDefault) {
			visitor.setBlock(cbCtx);
			returnValue = visitor.execute();
			if (!visitor.fallIntoNext())
			    return returnValue;

			continue blocks;
		    }

		    for (CalcParser.CaseSelectorContext select : cbCtx.caseSelector()) {
			if (select.K_DEFAULT() != null) {
			    seenDefault = true;
			    continue blocks;
			}
		    }
		}
	    }

	    return returnValue;
	}

	@Override
	public Object visitLeaveStmt(CalcParser.LeaveStmtContext ctx) {
	    CalcParser.LeaveLabelContext label = ctx.leaveLabel();
	    CalcParser.Expr1Context exprCtx    = ctx.expr1();

	    String leaveLabel = label != null ? label.id().getText() : null;

	    if (exprCtx != null) {
		throw new LeaveException(evaluate(exprCtx.expr()), leaveLabel);
	    }
	    else if (leaveLabel != null) {
		throw new LeaveException(null, leaveLabel);
	    }
	    else {
		throw LeaveException.instance();
	    }
	}

	@Override
	public Object visitNextStmt(CalcParser.NextStmtContext ctx) {
	    throw NextException.instance();
	}

	private Object executeTimeBlock(CalcParser.TimeThisStmtContext ctx) {
	    CalcParser.BracketBlockContext block = ctx.bracketBlock();
	    if (block != null) {
		return evaluate(block);
	    }
	    else {
		return evaluate(ctx.stmtOrExpr());
	    }
	}

	@Override
	public Object visitTimeThisStmt(CalcParser.TimeThisStmtContext ctx) {
	    CalcParser.ExprContext descExpr = ctx.expr();
	    if (descExpr != null) {
		Object descObj = evaluate(descExpr);
		if (descObj != null) {
		    String description = descObj.toString();
		    return Environment.timeThis(description, () -> {
			return executeTimeBlock(ctx);
		    });
		}
	    }
	    return Environment.timeThis( () -> {
		return executeTimeBlock(ctx);
	    });
	}

	@Override
	public Object visitStmtBlock(CalcParser.StmtBlockContext ctx) {
	    return internalVisitStatements(ctx);
	}

	@Override
	public Object visitDefineStmt(CalcParser.DefineStmtContext ctx) {
	    String funcName = ctx.id().getText();

	    // Can't redefine a predefined value
	    Object oldValue = currentScope.getValue(funcName, settings.ignoreNameCase);
	    if (oldValue != null && isPredefined(oldValue, false)) {
		throw new CalcExprException(ctx, "%calc#noChangeValue", oldValue.toString(), funcName, "");
	    }

	    CalcParser.StmtBlockContext functionBody       = ctx.stmtBlock();
	    CalcParser.FormalParamListContext formalParams = ctx.formalParamList();

	    String paramString = formalParams == null ? "" : getTreeText(formalParams);
	    List<CalcParser.FormalParamContext> paramVars = formalParams == null ? null : formalParams.formalParam();
	    FunctionDeclaration func = new FunctionDeclaration(funcName, functionBody);

	    if (formalParams != null) {
		for (CalcParser.FormalParamContext paramVar : formalParams.formalParam()) {
		    String paramName = paramVar.id().getText();
		    func.defineParameter(paramVar, paramName, paramVar.expr());
		}
		if (formalParams.DOTS() != null) {
		    func.defineParameter(formalParams, FunctionDeclaration.VARARG, null);
		}
	    }

	    currentScope.setValue(funcName, settings.ignoreNameCase, func);

	    displayActionMessage("%calc#definingFunc", func.getFullFunctionName(), getTreeText(functionBody));

	    return Intl.formatString("calc#definedFunc", func.getFullFunctionName());
	}

	@Override
	public Object visitConstStmt(CalcParser.ConstStmtContext ctx) {
	    List<CalcParser.IdContext>   ids   = ctx.id();
	    List<CalcParser.ExprContext> exprs = ctx.expr();
	    List<String> constNames = new ArrayList<>();
	    StringFormat format = new StringFormat(settings);

	    for (int i = 0; i < ids.size(); i++) {
		String constantName         = ids.get(i).getText();
		CalcParser.ExprContext expr = exprs.get(i);

		if (currentScope.isDefinedLocally(constantName, settings.ignoreNameCase))
		    throw new CalcExprException(ctx, "%calc#noDupConstant", constantName);

		Object value = evaluate(expr);

		ConstantValue.define(currentScope, constantName, value);

		displayActionMessage("%calc#definingConst", constantName, toStringValue(this, ctx, value, format));

		constNames.add(constantName);
	    }

	    return constNames.size() == 1 ? Intl.formatString("calc#definedConst", constNames.get(0))
					  : Intl.formatString("calc#definedConsts", CharUtil.makeSimpleStringList(constNames));
	}

	@Override
	public Object visitVarStmt(CalcParser.VarStmtContext ctx) {
	    List<CalcParser.VarAssignContext> assigns = ctx.varAssign();
	    List<String> varNames = new ArrayList<>();
	    StringFormat format = new StringFormat(settings);

	    for (int i = 0; i < assigns.size(); i++) {
		String varName              = assigns.get(i).id().getText();
		CalcParser.ExprContext expr = assigns.get(i).expr();

		if (currentScope.isDefinedLocally(varName, settings.ignoreNameCase))
		    throw new CalcExprException(ctx, "%calc#noDupLocalVar", varName);

		Object value = null;

		if (expr != null) {
		    value = evaluate(expr);

		    displayActionMessage("%calc#definingVar", varName,
			    toStringValue(this, ctx, value, format));
		}
		else {
		    displayActionMessage("%calc#definingVarOnly", varName);
		}

		currentScope.setValueLocally(varName, settings.ignoreNameCase, value);
		varNames.add(varName);
	    }

	    return varNames.size() == 1 ? Intl.formatString("calc#definedVar", varNames.get(0))
					: Intl.formatString("calc#definedVars", CharUtil.makeSimpleStringList(varNames));
	}

	@Override
	public Object visitEnumStmt(CalcParser.EnumStmtContext ctx) {
	    List<CalcParser.VarAssignContext> assigns = ctx.varAssign();
	    List<String> enumNames = new ArrayList<>();
	    StringFormat format = new StringFormat(settings);
	    BigInteger value = BigInteger.ZERO;

	    for (int i = 0; i < assigns.size(); i++) {
		String enumName             = assigns.get(i).id().getText();
		CalcParser.ExprContext expr = assigns.get(i).expr();

		if (currentScope.isDefinedLocally(enumName, settings.ignoreNameCase))
		    throw new CalcExprException(ctx, "%calc#noDupLocalVar", enumName);

		if (expr != null) {
		    value = getIntegerValue(expr);
		}
		displayActionMessage("%calc#definingEnum", enumName, toStringValue(this, ctx, value, format));

		EnumValue.define(currentScope, enumName, value);

		enumNames.add(enumName);

		value = value.add(BigInteger.ONE);
	    }

	    return enumNames.size() == 1 ? Intl.formatString("calc#definedEnum", enumNames.get(0))
					 : Intl.formatString("calc#definedEnums", CharUtil.makeSimpleStringList(enumNames));
	}

	private void addPairsToObject(CalcParser.ObjContext objCtx, ObjectScope object) {
	    for (CalcParser.PairContext pairCtx : objCtx.pair()) {
		String key = getMemberName(this, pairCtx.member());
		Object value = evaluate(pairCtx.expr());
		object.setValue(key, settings.ignoreNameCase, value);
	    }
	}

	@Override
	public Object visitObjExpr(CalcParser.ObjExprContext ctx) {
	    CalcParser.ObjContext objCtx = ctx.obj();
	    ObjectScope obj = new ObjectScope(settings.sortKeys);

	    addPairsToObject(objCtx, obj);

	    return obj;
	}

	@Override
	public Object visitArrExpr(CalcParser.ArrExprContext ctx) {
	   CalcParser.ExprListContext exprList = ctx.arr().exprList();
	   ArrayScope<Object> list = new ArrayScope<>();
	   if (exprList != null) {
		for (CalcParser.ExprContext expr : exprList.expr()) {
		    Object value = evaluate(expr);
		    list.add(value);
		}
	   }
	   return list;
	}

	@Override
	public Object visitSetExpr(CalcParser.SetExprContext ctx) {
	    CalcParser.ExprListContext exprList = ctx.set().exprList();
	    SetScope<Object> set = new SetScope<>();
	    if (exprList != null) {
		for (CalcParser.ExprContext expr : exprList.expr()) {
		    Object value = evaluate(expr);
		    set.add(value);
		}
	    }
	    return set;
	}

	@Override
	public Object visitComplexValueExpr(CalcParser.ComplexValueExprContext ctx) {
	    CalcParser.ComplexContext complex = ctx.complex();
	    CalcParser.ExprContext expr1 = complex.expr(0);
	    CalcParser.ExprContext expr2 = complex.expr(1);
	    Object o1 = evaluate(expr1);
	    Object o2 = evaluate(expr2);

	    if (settings.rationalMode || o1 instanceof BigFraction || o2 instanceof BigFraction) {
		BigFraction rFrac = convertToFraction(o1, expr1);
		BigFraction iFrac = convertToFraction(o2, expr2);

		return new ComplexNumber(rFrac, iFrac);
	    }
	    else {
		BigDecimal r = convertToDecimal(o1, settings.mc, expr1);
		BigDecimal i = convertToDecimal(o2, settings.mc, expr2);

		return new ComplexNumber(r, i);
	    }
	}

	@Override
	public Object visitQuaternionValueExpr(CalcParser.QuaternionValueExprContext ctx) {
	    CalcParser.QuaternionContext quatern = ctx.quaternion();
	    CalcParser.ExprContext expr1 = quatern.expr(0);
	    CalcParser.ExprContext expr2 = quatern.expr(1);
	    CalcParser.ExprContext expr3 = quatern.expr(2);
	    CalcParser.ExprContext expr4 = quatern.expr(3);
	    Object o1 = evaluate(expr1);
	    Object o2 = evaluate(expr2);
	    Object o3 = evaluate(expr3);
	    Object o4 = evaluate(expr4);

	    if (settings.rationalMode ||
		(o1 instanceof BigFraction || o2 instanceof BigFraction || o3 instanceof BigFraction || o4 instanceof BigFraction)) {
		BigFraction aFrac = convertToFraction(o1, expr1);
		BigFraction bFrac = convertToFraction(o2, expr2);
		BigFraction cFrac = convertToFraction(o3, expr3);
		BigFraction dFrac = convertToFraction(o4, expr4);

		return new Quaternion(aFrac, bFrac, cFrac, dFrac);
	    }
	    else {
		BigDecimal a = convertToDecimal(o1, settings.mc, expr1);
		BigDecimal b = convertToDecimal(o2, settings.mc, expr2);
		BigDecimal c = convertToDecimal(o3, settings.mc, expr3);
		BigDecimal d = convertToDecimal(o4, settings.mc, expr4);

		return new Quaternion(a, b, c, d);
	    }
	}

	@Override
	public Object visitVarExpr(CalcParser.VarExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());
	    return evaluate(ctx, lValue.getContextObject(this));
	}

	@Override
	public Object visitHasExpr(CalcParser.HasExprContext ctx) {
	    Object source = evaluate(ctx.expr(0));
	    CalcParser.ExprContext indexExpr = null;
	    Object indexValue = null;

	    if (!(source instanceof CollectionScope))
		return Boolean.FALSE;

	    if (ctx.LBRACK() != null) {
		indexExpr = ctx.expr(1);
		indexValue = evaluate(indexExpr);
		if (indexValue instanceof Number) {
		    CollectionScope collection = (CollectionScope) source;

		    int index = convertToInt(indexValue, settings.mc, indexExpr);
		    int size = collection.size();
		    if (index < 0)
			index += size;

		    return Boolean.valueOf(index >= 0 && index < size);
		}
	    }

	    if (!(source instanceof ObjectScope))
		return Boolean.FALSE;

	    String key = getMemberName(this, ctx.member());
	    if (key == null)
		key = getNonNullString(indexExpr, indexValue);

	    ObjectScope obj = (ObjectScope) source;

	    return Boolean.valueOf(isDefinedRecursively(obj, key, settings.ignoreNameCase));
	}

	@Override
	public Object visitIsExpr(CalcParser.IsExprContext ctx) {
	    Object value = evaluate(ctx.expr());
	    Typeof type = typeof(value);
	    String valueType;
	    TerminalNode string  = ctx.STRING();
	    TerminalNode istring = ctx.ISTRING();
	    TerminalNode types   = ctx.TYPES();

	    if (string != null)
		valueType = getRawString(string.getText());
	    else if (istring != null)
		valueType = getIStringValue(this, istring, ctx);
	    else
		valueType = types.getText();

	    return Boolean.valueOf(type.toString().equalsIgnoreCase(valueType));
	}

	@Override
	public Object visitPostIncOpExpr(CalcParser.PostIncOpExprContext ctx) {
	    CalcParser.VarContext var = ctx.var();
	    LValueContext lValue = getLValue(var);
	    Object value = evaluate(var, lValue.getContextObject(this));
	    String op = ctx.INC_OP().getText();
	    boolean incr = false;
	    Object beforeValue;
	    Object afterValue;

	    nullCheck(value, var);

	    switch (op) {
		case "++":
		case "\u2795\u2795":
		    incr = true;
		    break;
		case "--":
		case "\u2212\u2212":
		case "\u2796\u2796":
		    break;
		default:
		    throw new UnknownOpException(op, ctx);
	    }

	    if (value instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<Object> list = (ArrayScope<Object>) value;
		beforeValue = new ArrayScope<Object>(list);

		if (incr) {
		    list.add(null);
		}
		else {
		    if (!list.isEmpty()) {
			list.remove(list.size() - 1);
		    }
		}

		afterValue = list;
	    }
	    else if (value instanceof ObjectScope || CollectionScope.EMPTY.equals(value)) {
		ObjectScope obj;
		if (value instanceof ObjectScope) {
		    obj = (ObjectScope) value;
		    beforeValue = new ObjectScope(obj);
		}
		else {
		    obj = new ObjectScope();
		    beforeValue = value;
		}

		if (incr) {
		    obj.setValue(obj.size(), null);
		}
		else {
		    if (!obj.isEmpty()) {
			List<String> keys = obj.keyList();
			obj.remove(keys.get(obj.size() - 1), settings.ignoreNameCase);
		    }
		}

		afterValue = obj;
	    }
	    else if (settings.rationalMode || value instanceof BigFraction) {
		BigFraction fValue = convertToFraction(value, var);
		beforeValue = fValue;

		if (incr)
		    afterValue = fValue.add(BigFraction.ONE);
		else
		    afterValue = fValue.subtract(BigFraction.ONE);
	    }
	    else if (value instanceof Quaternion) {
		Quaternion qValue = (Quaternion) value;
		beforeValue = qValue;

		if (qValue.isRational()) {
		    if (incr)
			afterValue = qValue.add(QR_ONE);
		    else
			afterValue = qValue.subtract(QR_ONE);
		}
		else {
		    if (incr)
			afterValue = qValue.add(Quaternion.ONE);
		    else
			afterValue = qValue.subtract(Quaternion.ONE);
		}
	    }
	    else if (value instanceof ComplexNumber) {
		ComplexNumber cValue = (ComplexNumber) value;
		beforeValue = cValue;

		if (cValue.isRational()) {
		    if (incr)
			afterValue = cValue.add(CR_ONE);
		    else
			afterValue = cValue.subtract(CR_ONE, settings.mc);
		}
		else {
		    if (incr)
			afterValue = cValue.add(C_ONE);
		    else
			afterValue = cValue.subtract(C_ONE, settings.mc);
		}
	    }
	    else {
		BigDecimal dValue = convertToDecimal(value, settings.mc, var);
		beforeValue = dValue;

		if (incr)
		    afterValue = fixupToInteger(dValue.add(BigDecimal.ONE));
		else
		    afterValue = fixupToInteger(dValue.subtract(BigDecimal.ONE));
	    }

	    lValue.putContextObject(this, afterValue);

	    // post operation, return original value
	    return beforeValue;
	}

	@Override
	public Object visitPreIncOpExpr(CalcParser.PreIncOpExprContext ctx) {
	    CalcParser.VarContext var = ctx.var();
	    LValueContext lValue = getLValue(var);
	    Object value = evaluate(var, lValue.getContextObject(this));
	    String op = ctx.INC_OP().getText();
	    boolean incr = false;
	    Object afterValue;

	    nullCheck(value, var);

	    switch (op) {
		case "++":
		case "\u2795\u2795":
		    incr = true;
		    break;
		case "--":
		case "\u2212\u2212":
		case "\u2796\u2796":
		    break;
		default:
		    throw new UnknownOpException(op, ctx);
	    }

	    if (CollectionScope.EMPTY.equals(value)) {
		value = new ObjectScope();
	    }
	    if (value instanceof ObjectScope) {
		ObjectScope obj = (ObjectScope) value;

		if (incr) {
		    // It is impossible to rearrange the objects once they've been inserted the first time
		    // so we have to make a new object with the blank first value and then copy the original
		    // values in after that.
		    ObjectScope newObj = new ObjectScope(settings.sortKeys);
		    newObj.setValue(0, null);
		    newObj.putAll(obj);

		    afterValue = newObj;
		}
		else {
		    if (!obj.isEmpty()) {
			List<String> keys = obj.keyList();
			obj.remove(keys.get(0), settings.ignoreNameCase);
		    }

		    afterValue = obj;
		}
	    }
	    else if (value instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<Object> list = (ArrayScope<Object>) value;

		if (incr) {
		    list.insert(0, null);
		}
		else {
		    if (!list.isEmpty()) {
			list.remove(0);
		    }
		}

		afterValue = list;
	    }
	    else if (settings.rationalMode || value instanceof BigFraction) {
		BigFraction fValue = convertToFraction(value, var);

		if (incr)
		    afterValue = fValue.add(BigFraction.ONE);
		else
		    afterValue = fValue.subtract(BigFraction.ONE);
	    }
	    else if (value instanceof Quaternion) {
		Quaternion qValue = (Quaternion) value;

		if (qValue.isRational()) {
		    if (incr)
			afterValue = qValue.add(QR_ONE);
		    else
			afterValue = qValue.subtract(QR_ONE);
		}
		else {
		    if (incr)
			afterValue = qValue.add(Quaternion.ONE);
		    else
			afterValue = qValue.subtract(Quaternion.ONE);
		}
	    }
	    else if (value instanceof ComplexNumber) {
		ComplexNumber cValue = (ComplexNumber) value;

		if (cValue.isRational()) {
		    if (incr)
			afterValue = cValue.add(CR_ONE);
		    else
			afterValue = cValue.subtract(CR_ONE, settings.mc);
		}
		else {
		    if (incr)
			afterValue = cValue.add(C_ONE);
		    else
			afterValue = cValue.subtract(C_ONE, settings.mc);
		}
	    }
	    else {
		BigDecimal dValue = convertToDecimal(value, settings.mc, var);

		if (incr)
		    afterValue = fixupToInteger(dValue.add(BigDecimal.ONE));
		else
		    afterValue = fixupToInteger(dValue.subtract(BigDecimal.ONE));
	    }

	    // pre operation, return the modified value
	    return lValue.putContextObject(this, afterValue);
	}

	@Override
	public Object visitNegPosExpr(CalcParser.NegPosExprContext ctx) {
	    ParserRuleContext expr = ctx.expr();
	    Object e = evaluate(expr);

	    String op = ctx.ADD_OP().getText();
	    boolean negate = false;

	    switch (op) {
		case "+":
		case "\u2795":
		    break;
		case "-":
		case "\u2212":
		case "\u2796":
		    negate = true;
		    break;
		default:
		    throw new UnknownOpException(op, expr);
	    }

	    return negate(expr, e, negate, settings.rationalMode, settings.mc);
	}

	@Override
	public Object visitToStringExpr(CalcParser.ToStringExprContext ctx) {
	    return getStringValue(ctx.expr(), true, false, false);
	}

	@Override
	public Object visitToNumberExpr(CalcParser.ToNumberExprContext ctx) {
	    CalcParser.ExprContext expr = ctx.expr();
	    Object value = evaluate(expr);

	    return number(expr, value, settings.rationalMode, settings.mc);
	}

	@Override
	public Object visitBitNotExpr(CalcParser.BitNotExprContext ctx) {
	    BigInteger iValue = getIntegerValue(ctx.expr());

	    return iValue.not();
	}

	@Override
	public Object visitToBooleanExpr(CalcParser.ToBooleanExprContext ctx) {
	    return getBooleanValue(ctx.expr());
	}

	@Override
	public Object visitBooleanNotExpr(CalcParser.BooleanNotExprContext ctx) {
	    Boolean bValue = getBooleanValue(ctx.expr());

	    return bValue.booleanValue() ? Boolean.FALSE : Boolean.TRUE;
	}

	@Override
	public Object visitPowerExpr(CalcParser.PowerExprContext ctx) {
	    CalcParser.ExprContext expr = ctx.expr(0);
	    double exp = getDoubleValue(ctx.expr(1));

	    if (settings.rationalMode) {
		if (Math.floor(exp) == exp && !Double.isInfinite(exp)) {
		    BigFraction f = getFractionValue(expr);
		    return f.pow((int) exp);
		}
	    }

	    Object value = evaluate(expr);

	    if (value instanceof Quaternion) {
		Quaternion base = (Quaternion) value;
		if (Math.floor(exp) == exp && !Double.isInfinite(exp)) {
		    return base.power((int) exp, settings.mc);
		}
		// TODO: temporary
		throw new CalcExprException(ctx, "%calc#notImplemented", "quaternion to decimal power");
	    }
	    else if (value instanceof ComplexNumber) {
		ComplexNumber base = (ComplexNumber) value;
		return base.pow(new BigDecimal(exp), settings.mc);
	    }
	    else {
		BigDecimal base = convertToDecimal(value, settings.mc, expr);
		return MathUtil.pow(base, exp, settings.mc);
	    }
	}

	private int nToPower(String power, ParserRuleContext ctx) {
		int exp;

		switch (power) {
		    case "\u2070":
			exp = 0;
			break;
		    case "\u00B9":
			exp = 1;
			break;
		    case "\u00B2":
			exp = 2;
			break;
		    case "\u00B3":
			exp = 3;
			break;
		    case "\u2074":
			exp = 4;
			break;
		    case "\u2075":
			exp = 5;
			break;
		    case "\u2076":
			exp = 6;
			break;
		    case "\u2077":
			exp = 7;
			break;
		    case "\u2078":
			exp = 8;
			break;
		    case "\u2079":
			exp = 9;
			break;
		    default:
			throw new UnknownOpException(power, ctx);
		}
		return exp;
	}

	@Override
	public Object visitPowerNExpr(CalcParser.PowerNExprContext ctx) {
	    CalcParser.ExprContext expr = ctx.expr();
	    Object value = evaluate(expr);
	    String power = ctx.POWERS().getText();
	    int exp = nToPower(power, ctx);

	    if (settings.rationalMode) {
		BigFraction base = convertToFraction(value, expr);

		return base.pow(exp);
	    }
	    else if (value instanceof Quaternion) {
		Quaternion base = (Quaternion) value;

		return base.power(exp, settings.mc);
	    }
	    else if (value instanceof ComplexNumber) {
		ComplexNumber base = (ComplexNumber) value;

		return base.pow(new BigDecimal(exp), settings.mc);
	    }
	    else {
		BigDecimal base = convertToDecimal(value, settings.mc, expr);

		return MathUtil.pow(base, (double) exp, settings.mc);
	    }
	}

	@Override
	public Object visitParenExpr(CalcParser.ParenExprContext ctx) {
	    return evaluate(ctx.expr());
	}

	@Override
	public Object visitMultiplyExpr(CalcParser.MultiplyExprContext ctx) {
	    CalcParser.ExprContext ctx1 = ctx.expr(0);
	    CalcParser.ExprContext ctx2 = ctx.expr(1);
	    Object e1 = evaluate(ctx1);
	    Object e2 = evaluate(ctx2);

	    String op;
	    if (ctx.K_MOD() == null)
		op = ctx.MULT_OP().getText();
	    else
		op = "mod";

	    try {
		if (settings.rationalMode || (e1 instanceof BigFraction || e2 instanceof BigFraction)) {
		    BigFraction f1 = convertToFraction(e1, ctx1);
		    BigFraction f2 = convertToFraction(e2, ctx2);

		    switch (op) {
			case "*":
			case "\u00D7":
			case "\u2217":
			case "\u2715":
			case "\u2716":
			    return f1.multiply(f2);
			case "/":
			case "\u00F7":
			case "\u2215":
			case "\u2797":
			case "\\":
			case "\u2216":
			    return f1.divide(f2);
			case "%":
			    return f1.remainder(f2);
			case "mod":
			    return f1.modulus(f2);
			default:
			    throw new UnknownOpException(op, ctx);
		    }
		}
		else if (e1 instanceof Quaternion || e2 instanceof Quaternion) {
		    Quaternion q1 = Quaternion.valueOf(e1);
		    Quaternion q2 = Quaternion.valueOf(e2);

		    switch (op) {
			case "*":
			case "\u00D7":
			case "\u2217":
			case "\u2715":
			case "\u2716":
			    return q1.multiply(q2, settings.mc);
			case "/":
			case "\u00F7":
			case "\u2215":
			case "\u2797":
			    return q1.divide(q2, MathUtil.divideContext(q1, settings.mcDivide));
			case "\\":
			case "\u2216":
			case "%":
			case "mod":
			default:
			    throw new UnknownOpException(op, ctx);
		    }
		}
		else if (e1 instanceof ComplexNumber || e2 instanceof ComplexNumber) {
		    ComplexNumber c1 = ComplexNumber.valueOf(e1);
		    ComplexNumber c2 = ComplexNumber.valueOf(e2);

		    switch (op) {
			case "*":
			case "\u00D7":
			case "\u2217":
			case "\u2715":
			case "\u2716":
			    return c1.multiply(c2, settings.mc);
			case "/":
			case "\u00F7":
			case "\u2215":
			case "\u2797":
			    return c1.divide(c2, MathUtil.divideContext(c1, settings.mcDivide));
			case "\\":
			case "\u2216":
			case "%":
			case "mod":
			    // This one in particular potentially could be done with the same definition as for reals
			default:
			    throw new UnknownOpException(op, ctx);
		    }
		}
		else if (e1 instanceof SetScope && e2 instanceof CollectionScope) {
		    @SuppressWarnings("unchecked")
		    SetScope<Object> set = (SetScope<Object>) e1;
		    CollectionScope c = (CollectionScope) e2;

		    switch (op) {
			case "\\":
			case "\u2216":
			     return set.diff(c);
			default:
			    throw new UnknownOpException(op, ctx);
		    }
		}
		else {
		    BigDecimal d1 = convertToDecimal(e1, settings.mc, ctx1);
		    BigDecimal d2 = convertToDecimal(e2, settings.mc, ctx2);

		    MathContext mcDivide = MathUtil.divideContext(d1, settings.mcDivide);

		    switch (op) {
			case "*":
			case "\u00D7":
			case "\u2217":
			case "\u2715":
			case "\u2716":
			    return fixupToInteger(d1.multiply(d2, settings.mc));
			case "/":
			case "\u00F7":
			case "\u2215":
			case "\u2797":
			    return fixupToInteger(d1.divide(d2, mcDivide));
			case "\\":
			case "\u2216":
			    return fixupToInteger(d1.divideToIntegralValue(d2, mcDivide));
			case "%":
			    return fixupToInteger(d1.remainder(d2, mcDivide));
			case "mod":
			    return fixupToInteger(MathUtil.modulus(d1, d2, mcDivide));
			default:
			    throw new UnknownOpException(op, ctx);
		    }
		}
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx);
	    }
	}

	@Override
	public Object visitAddExpr(CalcParser.AddExprContext ctx) {
	    CalcParser.ExprContext ctx1 = ctx.expr(0);
	    CalcParser.ExprContext ctx2 = ctx.expr(1);
	    Object e1 = evaluate(ctx1);
	    Object e2 = evaluate(ctx2);

	    String op = ctx.ADD_OP().getText();
	    switch (op) {
		case "+":
		case "\u2795":
		    return addOp(this, e1, e2, ctx1, ctx2, settings.mc, settings.rationalMode, settings.sortKeys);
		case "-":
		case "\u2212":
		case "\u2796":
		    if (settings.rationalMode) {
			BigFraction f1 = convertToFraction(e1, ctx1);
			BigFraction f2 = convertToFraction(e2, ctx2);

			return f1.subtract(f2);
		    }
		    else if (e1 instanceof Quaternion || e2 instanceof Quaternion) {
			Quaternion q1 = Quaternion.valueOf(e1);
			Quaternion q2 = Quaternion.valueOf(e2);

			return q1.subtract(q2);
		    }
		    else if (e1 instanceof ComplexNumber || e2 instanceof ComplexNumber) {
			ComplexNumber c1 = ComplexNumber.valueOf(e1);
			ComplexNumber c2 = ComplexNumber.valueOf(e2);

			return c1.subtract(c2, settings.mc);
		    }
		    else if (e1 instanceof SetScope && e2 instanceof CollectionScope) {
			@SuppressWarnings("unchecked")
			SetScope<Object> set = (SetScope<Object>) e1;
			CollectionScope c = (CollectionScope) e2;

			return set.diff(c);
		    }
		    else {
			BigDecimal d1 = convertToDecimal(e1, settings.mc, ctx1);
			BigDecimal d2 = convertToDecimal(e2, settings.mc, ctx2);

			return fixupToInteger(d1.subtract(d2, settings.mc));
		    }
		default:
		    throw new UnknownOpException(op, ctx);
	    }
	}

	@Override
	public Object visitAbsExpr(CalcParser.AbsExprContext ctx) {
	    CalcParser.ExprContext expr = ctx.expr1().expr();
	    Object value = evaluate(expr);

	    if (settings.rationalMode || value instanceof BigFraction) {
		BigFraction f = convertToFraction(value, ctx);
		return f.abs();
	    }
	    else if (value instanceof Quaternion) {
		Quaternion q = (Quaternion) value;
		return q.magnitude(settings.mcDivide);
	    }
	    else if (value instanceof ComplexNumber) {
		ComplexNumber c = (ComplexNumber) value;
		return c.abs(settings.mcDivide);
	    }
	    else {
		BigDecimal e = convertToDecimal(value, settings.mc, ctx);
		return e.abs();
	    }
	}

	@Override
	public Object visitSinExpr(CalcParser.SinExprContext ctx) {
	    BigDecimal e = getDecimalTrigValue(ctx.expr1().expr());

	    return MathUtil.sin(e, settings.mc);
	}

	@Override
	public Object visitCosExpr(CalcParser.CosExprContext ctx) {
	    BigDecimal e = getDecimalTrigValue(ctx.expr1().expr());

	    return MathUtil.cos(e, settings.mc);
	}

	@Override
	public Object visitTanExpr(CalcParser.TanExprContext ctx) {
	    BigDecimal e = getDecimalTrigValue(ctx.expr1().expr());

	    return MathUtil.tan(e, settings.mc);
	}

	@Override
	public Object visitAsinExpr(CalcParser.AsinExprContext ctx) {
	    // Convert to double and use standard Math method
	    double d = getDoubleValue(ctx.expr1().expr());

	    return returnTrigValue(Math.asin(d));
	}

	@Override
	public Object visitAcosExpr(CalcParser.AcosExprContext ctx) {
	    // Convert to double and use standard Math method
	    double d = getDoubleValue(ctx.expr1().expr());

	    return returnTrigValue(Math.acos(d));
	}

	@Override
	public Object visitAtanExpr(CalcParser.AtanExprContext ctx) {
	    // Convert to double and use standard Math method
	    double d = getDoubleValue(ctx.expr1().expr());

	    return returnTrigValue(Math.atan(d));
	}

	@Override
	public Object visitAtan2Expr(CalcParser.Atan2ExprContext ctx) {
	    CalcParser.Expr2Context e2ctx = ctx.expr2();

	    BigDecimal y = getDecimalValue(e2ctx.expr(0));
	    BigDecimal x = getDecimalValue(e2ctx.expr(1));

	    return returnTrigValue(MathUtil.atan2(y, x, MathUtil.divideContext(y, settings.mcDivide)));
	}

	@Override
	public Object visitSinhExpr(CalcParser.SinhExprContext ctx) {
	    // Convert to double and use standard Math method
	    double d = getDoubleValue(ctx.expr1().expr());

	    return new BigDecimal(Math.sinh(d), MC_DOUBLE);
	}

	@Override
	public Object visitCoshExpr(CalcParser.CoshExprContext ctx) {
	    // Convert to double and use standard Math method
	    double d = getDoubleValue(ctx.expr1().expr());

	    return new BigDecimal(Math.cosh(d), MC_DOUBLE);
	}

	@Override
	public Object visitTanhExpr(CalcParser.TanhExprContext ctx) {
	    // Convert to double and use standard Math method
	    double d = getDoubleValue(ctx.expr1().expr());

	    return new BigDecimal(Math.tanh(d), MC_DOUBLE);
	}

	@Override
	public Object visitSqrtExpr(CalcParser.SqrtExprContext ctx) {
	    CalcParser.ExprContext expr = ctx.expr1().expr();
	    Object value = evaluate(expr);

	    if (value instanceof Quaternion) {
		// TODO: temporary
		throw new CalcExprException(ctx, "%calc#notImplemented", "square root of quaternion");
	    }
	    else if (value instanceof ComplexNumber) {
		ComplexNumber cValue = (ComplexNumber) value;
		return cValue.sqrt(settings.mcDivide);
	    }
	    else {
		try {
		    return MathUtil.sqrt(convertToDecimal(value, settings.mc, expr), settings.mcDivide);
		}
		catch (IllegalArgumentException iae) {
		    throw new CalcExprException(iae, ctx);
		}
	    }
	}

	@Override
	public Object visitCbrtExpr(CalcParser.CbrtExprContext ctx) {
	    CalcParser.ExprContext expr = ctx.expr1().expr();
	    Object value = evaluate(expr);

	    if (value instanceof Quaternion) {
		// TODO: temporary
		throw new CalcExprException(ctx, "%calc#notImplemented", "cube root of quaternion");
	    }
	    else if (value instanceof ComplexNumber) {
		ComplexNumber cValue = (ComplexNumber) value;
		MathContext mcPow = MathUtil.divideContext(cValue, settings.mcDivide);
		return cValue.pow(BigDecimal.ONE.divide(D_THREE, mcPow), mcPow);
	    }
	    else {
		return MathUtil.cbrt(convertToDecimal(value, settings.mc, expr), settings.mcDivide);
	    }
	}

	@Override
	public Object visitFortExpr(CalcParser.FortExprContext ctx) {
	    CalcParser.ExprContext expr = ctx.expr1().expr();
	    Object value = evaluate(expr);

	    if (value instanceof Quaternion) {
		// TODO: temporary
		throw new CalcExprException(ctx, "%calc#notImplemented", "fourth root of quaternion");
	    }
	    else if (value instanceof ComplexNumber) {
		ComplexNumber cValue = (ComplexNumber) value;
		return cValue.pow(D_ONE_FOURTH, settings.mc);
	    }
	    else {
		try {
		    return MathUtil.sqrt(MathUtil.sqrt(convertToDecimal(value, settings.mc, expr), settings.mc), settings.mc);
		}
		catch (IllegalArgumentException iae) {
		    throw new CalcExprException(iae, ctx);
		}
	    }
	}

	@Override
	public Object visitLogExpr(CalcParser.LogExprContext ctx) {
	    // For now, get a double value and use the standard Math method
	    double d = getDoubleValue(ctx.expr1().expr());

	    double logValue = Math.log10(d);
	    if (Double.isInfinite(logValue) || Double.isNaN(logValue))
		throw new CalcExprException("%math#numeric.outOfRange", ctx);

	    return new BigDecimal(logValue, MC_DOUBLE);
	}

	@Override
	public Object visitLn2Expr(CalcParser.Ln2ExprContext ctx) {
	    BigDecimal d = getDecimalValue(ctx.expr1().expr());

	    try {
		return MathUtil.ln2(d, settings.mc);
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, ctx);
	    }
	}

	@Override
	public Object visitLnExpr(CalcParser.LnExprContext ctx) {
	    BigDecimal d = getDecimalValue(ctx.expr1().expr());

	    try {
		return MathUtil.ln(d, settings.mc);
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, ctx);
	    }
	}

	@Override
	public Object visitEPowerExpr(CalcParser.EPowerExprContext ctx) {
	    BigDecimal e = getDecimalValue(ctx.expr1().expr());

	    return MathUtil.ePower(e, settings.mc);
	}

	@Override
	public Object visitTenPowerExpr(CalcParser.TenPowerExprContext ctx) {
	    BigDecimal e = getDecimalValue(ctx.expr1().expr());

	    return MathUtil.tenPower(e, settings.mc);
	}

	@Override
	public Object visitRandomExpr(CalcParser.RandomExprContext ctx) {
	    Object seed = null;

	    if (ctx.expr1() != null) {
		CalcParser.ExprContext expr = ctx.expr1().expr();
		if (expr != null) {
		    seed = evaluate(expr);
		}
	    }
	    return MathUtil.random(seed, settings.mc.getPrecision(), settings.mcDivide);
	}

	@Override
	public Object visitSignumExpr(CalcParser.SignumExprContext ctx) {
	    CalcParser.ExprContext expr = ctx.expr1().expr();
	    int signum;

	    if (settings.rationalMode) {
		BigFraction f = getFractionValue(expr);
		signum = f.signum();
	    }
	    else {
		Object value = evaluate(expr);
		if (value instanceof ComplexNumber) {
		    ComplexNumber c = (ComplexNumber) value;
		    return c.signum(MathUtil.divideContext(c, settings.mcDivide));
		}
		else {
		    BigDecimal e = convertToDecimal(value, settings.mc, expr);
		    signum = e.signum();
		}
	    }

	    return BigInteger.valueOf(signum);
	}

	@Override
	public Object visitIsNullExpr(CalcParser.IsNullExprContext ctx) {
	    Object obj = evaluate(ctx.expr1().expr());
	    if (ctx.K_ISNULL() != null) {
		return Boolean.valueOf(obj == null);
	    }
	    else {
		return Boolean.valueOf(obj != null);
	    }
	}

	@Override
	public Object visitTypeofExpr(CalcParser.TypeofExprContext ctx) {
	    CalcParser.TypeArgContext arg = ctx.typeArg();
	    Object obj = null;

	    if (arg.var() != null) {
		LValueContext lValue = getLValue(arg.var());
		obj = lValue.getContextObject(this);
	    }
	    else {
		obj = evaluate(arg.expr());
	    }

	    return typeof(obj).getValue();
	}

	@Override
	public Object visitCastExpr(CalcParser.CastExprContext ctx) {
	    Typeof castType = Typeof.STRING;
	    CalcParser.ExprContext expr;
	    Object obj;

	    if (ctx.expr2() != null) {
		expr = ctx.expr2().expr(0);
		CalcParser.ExprContext type = ctx.expr2().expr(1);
		castType = Typeof.fromString(getStringValue(type));
	    }
	    else {
		expr = ctx.expr1().expr();
	    }
	    obj = evaluate(expr);

	    try {
		return castTo(this, expr, obj, castType, settings.mc, settings.separatorMode, settings.sortKeys);
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, ctx);
	    }
	}

	private class LengthVisitor implements IterationVisitor
	{
		private BigInteger count = BigInteger.ZERO;

		@Override
		public Purpose getPurpose() {
		    return Purpose.ALL;
		}

		@Override
		public Object apply(final Object value) {
		    count = count.add(BigInteger.ONE);
		    return count;
		}

		@Override
		public Object finalValue(final Number start, final Number stop, final Number step) {
		    if (start instanceof Integer) {
			int iStart = (Integer) start;
			int iStop  = (Integer) stop;
			int iStep  = (Integer) step;

			return (iStop - iStart) / iStep + 1;
		    }
		    else {
			BigDecimal dStart = (BigDecimal) start;
			BigDecimal dStop  = (BigDecimal) stop;
			BigDecimal dStep  = (BigDecimal) step;

			return fixup(dStop.subtract(dStart).divide(dStep, settings.mcDivide).add(BigDecimal.ONE));
		    }
		}
	}

	@Override
	public Object visitLengthExpr(CalcParser.LengthExprContext ctx) {
	    CalcParser.DotRangeContext dotRange = ctx.dotRange();

	    if (dotRange != null) {
		LengthVisitor visitor = new LengthVisitor();
		return iterateOverDotRange(null, dotRange.expr(), dotRange.DOTS() != null, visitor, false, false);
	    }
	    else {
		Object obj = evaluate(ctx.expr1().expr());

		// This returns the non-recursive size of objects and arrays
		// so, use "scale" to calculate the recursive (full) size
		return BigInteger.valueOf((long) length(this, obj, ctx, false));
	    }
	}

	@Override
	public Object visitScaleExpr(CalcParser.ScaleExprContext ctx) {
	    Object obj = evaluate(ctx.expr1().expr());

	    // This calculates the recursive size of objects and arrays
	    // so, use "length" to calculate the non-recursive size
	    return BigInteger.valueOf((long) scale(this, obj, ctx));
	}

	@Override
	public Object visitRoundExpr(CalcParser.RoundExprContext ctx) {
	    CalcParser.Expr2Context e2ctx = ctx.expr2();
	    // Note: this should be good even for rational mode (converts to decimal)
	    BigDecimal e = getDecimalValue(e2ctx.expr(0));
	    int iPlaces  = getIntValue(e2ctx.expr(1));

	    return MathUtil.round(e, iPlaces);
	}

	@Override
	public Object visitCeilExpr(CalcParser.CeilExprContext ctx) {
	    if (settings.rationalMode) {
		BigFraction f = getFractionValue(ctx.expr1().expr());

		return f.ceil();
	    }
	    else {
		BigDecimal e = getDecimalValue(ctx.expr1().expr());

		return MathUtil.ceil(e);
	    }
	}

	@Override
	public Object visitFloorExpr(CalcParser.FloorExprContext ctx) {
	    if (settings.rationalMode) {
		BigFraction f = getFractionValue(ctx.expr1().expr());

		return f.floor();
	    }
	    else {
		BigDecimal e = getDecimalValue(ctx.expr1().expr());

		return MathUtil.floor(e);
	    }
	}

	@Override
	public Object visitIsPrimeExpr(CalcParser.IsPrimeExprContext ctx) {
	    BigInteger i;

	    if (settings.rationalMode) {
		BigFraction f = getFractionValue(ctx.expr1().expr());

		if (f.isWholeNumber()) {
		    i = f.toInteger();
		}
		else {
		    throw new CalcExprException(ctx, "%calc#noConvertInteger", f);
		}
	    }
	    else {
		i = getIntegerValue(ctx.expr1().expr());
	    }

	    return Boolean.valueOf(MathUtil.isPrime(i));
	}

	@Override
	public Object visitGcdExpr(CalcParser.GcdExprContext ctx) {
	    List<CalcParser.ExprContext> exprs = ctx.exprN().exprList().expr();
	    List<Object> objects = buildFlatMap(exprs, false,
		settings.rationalMode ? Conversion.FRACTION : Conversion.INTEGER);
	    int index;

	    if (settings.rationalMode) {
		BigFraction result = (BigFraction) objects.get(0);
		for (index = 1; index < objects.size(); index++) {
		    result = result.gcd((BigFraction) objects.get(index));
		}

		return result;
	    }
	    else {
		BigInteger result = (BigInteger) objects.get(0);
		for (index = 1; index < objects.size(); index++) {
		    result = result.gcd((BigInteger) objects.get(index));
		}

		return result;
	    }
	}

	@Override
	public Object visitLcmExpr(CalcParser.LcmExprContext ctx) {
	    List<CalcParser.ExprContext> exprs = ctx.exprN().exprList().expr();
	    List<Object> objects = buildFlatMap(exprs, false,
		settings.rationalMode ? Conversion.FRACTION : Conversion.INTEGER);
	    int index;

	    try {
		if (settings.rationalMode) {
		    BigFraction result = (BigFraction) objects.get(0);
		    for (index = 1; index < objects.size(); index++) {
			result = result.lcm((BigFraction) objects.get(index));
		    }

		    return result;
		}
		else {
		    BigInteger result = (BigInteger) objects.get(0);
		    for (index = 1; index < objects.size(); index++) {
			// Note: this "lcm" method is a helper function inside BigFraction
			// that works on and returns a BigInteger value
			result = BigFraction.lcm(result, (BigInteger) objects.get(index));
		    }

		    return result;
		}
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx);
	    }
	}

	/**
	 * Recursively descend to the leaf values in the first object of a min/max/join list
	 * to find the actual first value, in order to determine if the comparisons will be
	 * done as string or numeric.
	 *
	 * @param eCtx	  The value context we're evaluating (i.e., the first parse tree).
	 * @param obj	  The first object, which could be an object, a list, or an actual value.
	 * @param forJoin Whether or not this is a "join" operation.
	 * @return	  The real first value, descending to the lowest level of a compound object.
	 */
	private Object getFirstValue(final ParserRuleContext eCtx, final Object obj, final boolean forJoin) {
	    Object value = evaluate(eCtx, obj);

	    nullCheck(value, eCtx);

	    if (!forJoin) {
		if (value instanceof ArrayScope) {
		    @SuppressWarnings("unchecked")
		    ArrayScope<Object> list = (ArrayScope<Object>) value;
		    return list.size() > 0 ? getFirstValue(eCtx, list.getValue(0), forJoin) : null;
		}
		else if (value instanceof ObjectScope) {
		    @SuppressWarnings("unchecked")
		    ObjectScope map = (ObjectScope) value;
		    Iterator<Object> iter = map.values().iterator();
		    return iter.hasNext() ? getFirstValue(eCtx, iter.next(), forJoin) : null;
		}
		else if (value instanceof SetScope) {
		    @SuppressWarnings("unchecked")
		    SetScope<Object> set = (SetScope<Object>) value;
		    Iterator<Object> iter = set.set().iterator();
		    return iter.hasNext() ? getFirstValue(eCtx, iter.next(), forJoin) : null;
		}
	    }

	    return value;
	}

	/**
	 * Do a "flat map" of the arguments to the {@code min}, {@code max}, or {@code join} functions into a single list
	 * of the values: either strings, fractions, or decimals.
	 *
	 * @param ctx	The outer level context (that of the function itself) (for error reporting).
	 * @param obj	One of the objects listed as parameters to the function, which could be arrays, maps, etc.
	 * @param objectList	The "flat map" or list of objects we're building.
	 * @param conv		Conversion for the result list values (determined by the first actual value, or always
	 *			{@code true} for {@code join}).
	 * @param forJoin	{@code true} for a "join" operation, which will not recurse into lists or maps to begin with.
	 * @see #getFirstValue
	 */
	private void buildFlatMap(
		final ParserRuleContext ctx,
		final Object obj,
		final List<Object> objectList,
		final Conversion conv,
		final boolean forJoin)
	{
	    Object value = evaluate(ctx, obj);

	    nullCheck(value, ctx);

	    if (value instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<Object> array = (ArrayScope<Object>) value;
		if (forJoin) {
		    objectList.add(array);
		}
		else {
		    for (Object listObj : array.list()) {
			buildFlatMap(ctx, listObj, objectList, conv, forJoin);
		    }
		}
	    }
	    else if (value instanceof ObjectScope) {
		@SuppressWarnings("unchecked")
		ObjectScope object = (ObjectScope) value;
		if (forJoin) {
		    objectList.add(object);
		}
		else {
		    for (Object mapObj : object.values()) {
			buildFlatMap(ctx, mapObj, objectList, conv, forJoin);
		    }
		}
	    }
	    else if (value instanceof SetScope) {
		@SuppressWarnings("unchecked")
		SetScope<Object> set = (SetScope<Object>) value;
		if (forJoin) {
		    objectList.add(set);
		}
		else {
		    for (Object setObj : set.set()) {
			buildFlatMap(ctx, setObj, objectList, conv, forJoin);
		    }
		}
	    }
	    else if (value instanceof CollectionScope) {
		// This is an empty map or set, so nothing to do
	    }
	    else if (settings.rationalMode || conv == Conversion.FRACTION) {
		objectList.add(convertToFraction(value, ctx));
	    }
	    else if (conv == Conversion.QUATERNION) {
		objectList.add(Quaternion.valueOf(value));
	    }
	    else if (conv == Conversion.COMPLEX) {
		objectList.add(ComplexNumber.valueOf(value));
	    }
	    else if (conv == Conversion.DECIMAL) {
		objectList.add(convertToDecimal(value, settings.mc, ctx));
	    }
	    else if (conv == Conversion.STRING) {
		// Note: this logic follows "getStringValue"
		if (value instanceof String)
		    objectList.add(value);
		else
		    objectList.add(value.toString());
	    }
	    else /* UNCHANGED */ {
		objectList.add(value);
	    }
	}

	/**
	 * Construct the "flat map" or value list for {@code min}, {@code max}, or {@code join}
	 * so that we can traverse a simple list to obtain the desired result.
	 *
	 * @param exprs	  The list of expressions parsed as the arguments to the function.
	 * @param forJoin Whether or not this is a "join" operation.
	 * @param conv	  Default conversion (or {@code null} to peek inside)
	 * @return	  The "flat map" of the values from those arguments.
	 */
	private List<Object> buildFlatMap(final List<CalcParser.ExprContext> exprs, final boolean forJoin, final Conversion conv) {
	    Conversion conversion = conv;
	    if (conversion == null) {
		// Do a "peek" inside any lists or maps to get the first value
		CalcParser.ExprContext firstCtx = exprs.get(0);
		conversion = Conversion.fromValue(getFirstValue(firstCtx, evaluate(firstCtx), forJoin));
		// Don't do INTEGER unless specifically requested
		if (conversion == Conversion.INTEGER)
		    conversion = Conversion.DECIMAL;
	    }

	    List<Object> objects = new ArrayList<>();

	    for (CalcParser.ExprContext eCtx : exprs) {
		buildFlatMap(eCtx, evaluate(eCtx), objects, conversion, forJoin);
	    }

	    return objects;
	}

	@Override
	public Object visitMaxExpr(CalcParser.MaxExprContext ctx) {
	    List<CalcParser.ExprContext> exprs = ctx.exprN().exprList().expr();
	    List<Object> objects = buildFlatMap(exprs, false, null);
	    Object firstValue = objects.size() > 0 ? objects.get(0) : null;

	    if (firstValue instanceof String) {
		String maxString = (String) firstValue;
		for (int i = 1; i < objects.size(); i++) {
		    String value = (String) objects.get(i);
		    if (compareStrings(value, maxString, settings.ignoreNameCase, true) > 0)
			maxString = value;
		}
		return maxString;
	    }
	    else {
		if (settings.rationalMode) {
		    BigFraction maxFraction = (BigFraction) firstValue;
		    for (int i = 1; i < objects.size(); i++) {
			BigFraction value = (BigFraction) objects.get(i);
			if (value.compareTo(maxFraction) > 0)
			    maxFraction = value;
		    }
		    return maxFraction;
		}
		else {
		    BigDecimal maxNumber = (BigDecimal) firstValue;
		    for (int i = 1; i < objects.size(); i++) {
			BigDecimal value = (BigDecimal) objects.get(i);
			if (value.compareTo(maxNumber) > 0)
			    maxNumber = value;
		    }
		    return maxNumber;
		}
	    }
	}

	@Override
	public Object visitMinExpr(CalcParser.MinExprContext ctx) {
	    List<CalcParser.ExprContext> exprs = ctx.exprN().exprList().expr();
	    List<Object> objects = buildFlatMap(exprs, false, null);
	    Object firstValue = objects.size() > 0 ? objects.get(0) : null;

	    if (firstValue instanceof String) {
		String minString = (String) firstValue;
		for (int i = 1; i < objects.size(); i++) {
		    String value = (String) objects.get(i);
		    if (compareStrings(value, minString, settings.ignoreNameCase, true) < 0)
			minString = value;
		}
		return minString;
	    }
	    else {
		if (settings.rationalMode) {
		    BigFraction minFraction = (BigFraction) firstValue;
		    for (int i = 1; i < objects.size(); i++) {
			BigFraction value = (BigFraction) objects.get(i);
			if (value.compareTo(minFraction) < 0)
			    minFraction = value;
		    }
		    return minFraction;
		}
		else {
		    BigDecimal minNumber = (BigDecimal) firstValue;
		    for (int i = 1; i < objects.size(); i++) {
			BigDecimal value = (BigDecimal) objects.get(i);
			if (value.compareTo(minNumber) < 0)
			    minNumber = value;
		    }
		    return minNumber;
		}
	    }
	}

	@Override
	public Object visitJoinExpr(CalcParser.JoinExprContext ctx) {
	    List<CalcParser.ExprContext> exprs = ctx.exprN().exprList().expr();
	    List<Object> objects = buildFlatMap(exprs, true, Conversion.STRING);

	    StringBuilder buf = new StringBuilder();
	    int length = objects.size();

	    // This doesn't make sense unless there are at least 3 values
	    // So, one value just gets that value
	    // two values gets the two just concatenated together
	    // three or more, the first N - 1 are joined by the Nth (string) value
	    if (length == 1) {
		Object obj0 = objects.get(0);
		// One map, list, or set => concatenate all objects in it
		if (obj0 instanceof CollectionScope) {
		    objects.clear();
		    buildFlatMap(ctx, obj0, objects, Conversion.STRING, false);
		    for (Object obj : objects) {
			buf.append(obj);
		    }
		    return buf.toString();
		}
		return obj0;
	    }
	    else if (length == 2) {
		Object obj0 = objects.get(0);
		Object obj1 = objects.get(1);
		if (obj0 instanceof CollectionScope) {
		    // One map, list, or set, plus a join expression
		    objects.clear();
		    buildFlatMap(ctx, obj0, objects, Conversion.STRING, false);
		    for (int i = 0; i < objects.size(); i++) {
			if (i > 0)
			    buf.append(obj1);
			buf.append(objects.get(i));
		    }
		}
		else {
		    // Two simple objects, just concatenate their string representations
		    buf.append(obj0);
		    buf.append(obj1);
		}
		return buf.toString();
	    }
	    else {
		String joinExpr = objects.get(length - 1).toString();
		for (int i = 0; i < length - 1; i++) {
		    if (i > 0)
			buf.append(joinExpr);
		    Object obj = objects.get(i);
		    if (obj instanceof Scope) {
			List<Object> objects1 = new ArrayList<>();
			buildFlatMap(ctx, obj, objects1, Conversion.STRING, false);
			for (int j = 0; j < objects1.size(); j++) {
			    if (j > 0)
				buf.append(joinExpr);
			    buf.append(objects1.get(j));
			}
		    }
		    else {
			buf.append(objects.get(i));
		    }
		}
		return buf.toString();
	    }
	}

	@Override
	public Object visitSplitExpr(CalcParser.SplitExprContext ctx) {
	    CalcParser.Expr2Context e2ctx = ctx.expr2();
	    String stringValue;
	    String patternValue;
	    String[] parts;

	    if (e2ctx != null) {
		stringValue  = getStringValue(e2ctx.expr(0));
		patternValue = getStringValue(e2ctx.expr(1));

		parts = stringValue.split(patternValue);
	    }
	    else {
		CalcParser.Expr3Context e3ctx = ctx.expr3();
		stringValue  = getStringValue(e3ctx.expr(0));
		patternValue = getStringValue(e3ctx.expr(1));
		int limit    = getIntValue(e3ctx.expr(2));

		parts = stringValue.split(patternValue, limit);
	    }

	    return new ArrayScope<Object>((Object[]) parts);
	}

	@Override
	public Object visitIndexExpr(CalcParser.IndexExprContext ctx) {
	    CalcParser.Expr2Context e2ctx = ctx.expr2();
	    CalcParser.ExprContext e0ctx;
	    CalcParser.ExprContext e1ctx;
	    CalcParser.ExprContext indexCtx = null;
	    Object sourceObj;
	    Object searchObj;
	    int start = 0;
	    int size;

	    int ret = -1;

	    if (e2ctx != null) {
		e0ctx = e2ctx.expr(0);
		e1ctx = e2ctx.expr(1);
	    }
	    else {
		CalcParser.Expr3Context e3ctx = ctx.expr3();
		e0ctx    = e3ctx.expr(0);
		e1ctx    = e3ctx.expr(1);
		indexCtx = e3ctx.expr(2);
	    }
	    sourceObj = evaluate(e0ctx);
	    searchObj = evaluate(e1ctx);
	    if (indexCtx != null)
		start = getIntValue(indexCtx);

	    if (sourceObj instanceof ObjectScope) {
		// Return value is index of key in the map
		ObjectScope obj = (ObjectScope) sourceObj;
		String searchKey = getNonNullString(e1ctx, searchObj);
		size = obj.size();

		ret = obj.indexOf(searchKey, start, settings.ignoreNameCase);
	    }
	    else if (sourceObj instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<Object> list = (ArrayScope<Object>) sourceObj;
		size = list.size();

		ret = indexOf(this, e0ctx, e1ctx, list, searchObj, start, settings.mc);
	    }
	    else if (sourceObj instanceof SetScope) {
		@SuppressWarnings("unchecked")
		SetScope<Object> set = (SetScope<Object>) sourceObj;

		boolean contains = set.set().contains(searchObj);
		if (!contains)
		    return null;

		// This is different: for a set we return null or one
		return BigInteger.ONE;
	    }
	    else {
		String sourceString = getNonNullString(e0ctx, sourceObj);
		String searchString = getNonNullString(e1ctx, searchObj);
		size = sourceString.length();

		if (start < 0) {
		    ret = sourceString.lastIndexOf(searchString, size + start);
		}
		else {
		    ret = sourceString.indexOf(searchString, start);
		}
	    }

	    if (ret < 0)
		return null;

	    if (start < 0) {
		// start was negative, so result should be negative too
		// ret is 0-based index, so result will be  0,  1,  2
		// with size = 3                           -3, -2, -1
		ret -= size;
	    }

	    return BigInteger.valueOf((long) ret);
	}

	private String substring(Object value, CalcParser.ExprContext ctx, CalcParser.ExprContext beginCtx, CalcParser.ExprContext endCtx) {
	    String stringValue = toStringValue(this, ctx, value, new StringFormat(false, settings));

	    if (beginCtx == null && endCtx == null) {
		return stringValue;
	    }

	    int stringLen  = stringValue.length();
	    int beginIndex = 0;
	    int endIndex   = stringLen;

	    if (beginCtx != null) {
		Object beginValue = evaluate(beginCtx);
		if (beginValue != null) {
		    beginIndex = convertToInt(beginValue, settings.mc, beginCtx);
		}
	    }
	    if (endCtx != null) {
		Object endValue = evaluate(endCtx);
		if (endValue != null) {
		    endIndex = convertToInt(endValue, settings.mc, endCtx);
		}
	    }

	    // Negative indices at this point are relative to the string length
	    if (beginIndex < 0)
		beginIndex += stringLen;
	    if (endIndex < 0)
		endIndex += stringLen;

	    // But, by this point, the values should be in the range of 0..length
	    // Note: should we do index checking errors? or just limit and go on?
	    if (beginIndex < 0)
		beginIndex = 0;
	    if (beginIndex > stringLen)
		beginIndex = stringLen;
	    if (endIndex < beginIndex)
		endIndex = beginIndex;
	    if (endIndex > stringLen)
		endIndex = stringLen;

	    return stringValue.substring(beginIndex, endIndex);
	}

	@Override
	public Object visitSubstrExpr(CalcParser.SubstrExprContext ctx) {
	    CalcParser.Expr1Context e1ctx = ctx.expr1();
	    CalcParser.Expr2Context e2ctx = ctx.expr2();
	    CalcParser.Expr3Context e3ctx = ctx.expr3();
	    CalcParser.ExprContext valueCtx;
	    CalcParser.ExprContext beginCtx = null, endCtx = null;

	    if (e1ctx != null) {
		valueCtx = e1ctx.expr();
	    }
	    else if (e2ctx != null) {
		valueCtx = e2ctx.expr(0);
		beginCtx = e2ctx.expr(1);
	    }
	    else {
		valueCtx = e3ctx.expr(0);
		beginCtx = e3ctx.expr(1);
		endCtx   = e3ctx.expr(2);
	    }

	    return substring(evaluate(valueCtx), valueCtx, beginCtx, endCtx);
	}

	/**
	 * Transformer for the "replace" option.
	 */
	private class ReplaceTransformer implements Transformer
	{
		private String pattern;
		private String replace;
		private String option;

		ReplaceTransformer(final String searchPattern, final String replaceValue, final String replaceOption) {
		    pattern = searchPattern;
		    replace = replaceValue;
		    option = replaceOption;
		}

		@Override
		public Object apply(final Object value) {
		    if (value instanceof String) {
			String original = (String) value;

			switch (option) {
			    case "":
				// Regular replace using the pattern as a literal
				return original.replace(pattern, replace);
			    case "all":
				// Use pattern as a regex and replace all matching values
				return original.replaceAll(pattern, replace);
			    case "first":
				// Again, pattern is a regex, but only replace the first match
				return original.replaceFirst(pattern, replace);
			    case "last":
				// New functionality, pattern is a regex, but only replace the last match
				return original.replaceFirst("(?s)(.*)" + pattern, "$1" + replace);
			    default:
				break;
			}
		    }
		    return value;
		}
	}

	@Override
	public Object visitReplaceExpr(CalcParser.ReplaceExprContext ctx) {
	    CalcParser.ReplaceArgsContext args = ctx.replaceArgs();
	    List<CalcParser.ExprContext> exprs = args.expr();
	    CalcParser.ExprContext exprCtx = exprs.get(0);

	    Object originalObj = evaluate(exprCtx);
	    String pattern     = getStringValue(exprs.get(1));
	    String replace     = exprs.size() > 2 ? getStringValue(exprs.get(2)) : "";

	    Object result = originalObj;
	    String option = "";

	    if (args.replaceOption() != null) {
		if (args.replaceOption().var() != null) {
		    CalcParser.VarContext var = args.replaceOption().var();
		    LValueContext lValue = getLValue(var);
		    Object optionObject = lValue.getContextObject(this, false);
		    option = optionObject == null ? "" : toStringValue(this, var, optionObject, new StringFormat(true, false));
		}
		else {
		    option = args.replaceOption().getText();
		}
		option = option.toLowerCase();

		switch (option) {
		    case "":
		    case "all":
		    case "first":
		    case "last":
			break;
		    default:
			throw new CalcExprException(args.replaceOption(), "%calc#replaceOptionError", option);
		}
	    }

	    try {
		result = copyAndTransform(this, exprCtx, originalObj, settings.sortKeys,
			new ReplaceTransformer(pattern, replace, option));
	    }
	    catch (Exception ex) {
		throw new CalcExprException(ex, exprCtx);
	    }

	    return result;
	}

	@Override
	public Object visitSliceExpr(CalcParser.SliceExprContext ctx) {
	    CalcParser.ExprContext valueCtx = ctx.expr();
	    CalcParser.ExprContext beginCtx = null, endCtx = null;

	    if (ctx.slice3() != null) {
		beginCtx = ctx.slice3().expr(0);
		endCtx   = ctx.slice3().expr(1);
	    }
	    else if (ctx.slice2() != null) {
		endCtx = ctx.slice2().expr();
	    }
	    else {
		beginCtx = ctx.slice1().expr();
	    }

	    int beginIndex, endIndex;
	    int arrayLen;

	    Object value = evaluate(valueCtx);

	    if (value instanceof ArrayScope) {
		arrayLen = ((ArrayScope) value).size();
	    }
	    else if (value instanceof ObjectScope) {
		arrayLen = ((ObjectScope) value).size();
	    }
	    else if (value instanceof SetScope) {
		arrayLen = ((SetScope) value).size();
	    }
	    else {
		// Any simple object behaves the same way as "substr"
		return substring(value, valueCtx, beginCtx, endCtx);
	    }

	    // We are going to let either a null context (meaning no text was present) OR
	    // a null value mean the beginning or end of the array or string respectively
	    beginIndex = beginCtx == null ? 0 : getIntValue(beginCtx, Integer.valueOf(0));
	    endIndex   = endCtx == null ? arrayLen : getIntValue(endCtx, Integer.valueOf(arrayLen));

	    // Of course, negative values here are relative to the length of the array/string
	    if (beginIndex < 0)
		beginIndex += arrayLen;
	    if (endIndex < 0)
		endIndex += arrayLen;

	    // This is questionable: should we LIMIT the values to 0..length or throw exceptions
	    // for them being out of that range?
	    if (beginIndex < 0)
		beginIndex = 0;
	    if (beginIndex > arrayLen)
		beginIndex = arrayLen;
	    if (endIndex < beginIndex)
		endIndex = beginIndex;
	    if (endIndex > arrayLen)
		endIndex = arrayLen;

	    // No matter the type of the input object, the result here will be an array
	    ArrayScope<Object> result = new ArrayScope<>();

	    if (value instanceof ArrayScope) {
		List<?> valueList = ((ArrayScope) value).list();
		for (int index = beginIndex; index < endIndex; index++) {
		    Object val = valueList.get(index);
		    result.add(val);
		}
	    }
	    else if (value instanceof ObjectScope) {
		Object[] valueArray = ((ObjectScope) value).values().toArray();
		for (int index = beginIndex; index < endIndex; index++) {
		    Object val = valueArray[index];
		    result.add(val);
		}
	    }
	    else {
		Object[] valueArray = ((SetScope) value).set().toArray();
		for (int index = beginIndex; index < endIndex; index++) {
		    Object val = valueArray[index];
		    result.add(val);
		}
	    }

	    return result;
	}

	@Override
	public Object visitSpliceExpr(CalcParser.SpliceExprContext ctx) {
	    CalcParser.SpliceArgsContext args = ctx.spliceArgs();
	    CalcParser.Expr1Context e1ctx = args.expr1();
	    CalcParser.Expr2Context e2ctx = args.expr2();
	    CalcParser.Expr3Context e3ctx = args.expr3();
	    CalcParser.ExprNContext eNctx = args.exprN();
	    List<CalcParser.ExprContext> exprs = null;
	    CalcParser.ExprContext objCtx = null;
	    boolean doingArray;

	    if (args.dropObjs() != null || args.obj() != null) {
		doingArray = false;

		objCtx = args.expr();
	    }
	    else {
		// Actually, this could be an object with no drops or adds, so check below
		// for the actual object type
		doingArray = true;

		if (e1ctx != null) {
		    objCtx = e1ctx.expr();
		}
		else if (e2ctx != null) {
		    exprs = e2ctx.expr();
		}
		else if (e3ctx != null) {
		    exprs = e3ctx.expr();
		}
		else {
		    exprs = eNctx.exprList().expr();
		}

		if (objCtx == null)
		    objCtx = exprs.get(0);
	    }

	    Object source = evaluate(objCtx);

	    String sourceClass = source.getClass().getSimpleName();

	    // Special case here: with a single argument (the source) we will set "doingArray" true
	    // without knowing the argument type, so check that here
	    if (doingArray && (source instanceof ObjectScope))
		doingArray = false;

	    if (doingArray) {
		if (source instanceof ArrayScope) {
		    @SuppressWarnings("unchecked")
		    ArrayScope<Object> array = (ArrayScope<Object>) source;
		    ArrayScope<Object> removed = new ArrayScope<>();

		    int arrayLen = array.size();
		    int exprLen = exprs != null ? exprs.size() : 0;
		    int start = exprLen > 1 ? getIntValue(exprs.get(1)) : 0;

		    if (start < 0)
			start += arrayLen;
		    if (start < 0)
			start = 0;
		    if (start > arrayLen)
			start = arrayLen;

		    int count = exprLen > 2 ? getIntValue(exprs.get(2)) : arrayLen - start;
		    if (count < 0)
			count = 0;
		    if (count > arrayLen - start)
			count = arrayLen - start;

		    // Remove the specified number of elements beginning from "start" and add to the result array
		    for (int index = 0; index < count; index++) {
			removed.add(array.remove(start));
		    }

		    // Now if any elements were given to add/insert, do that starting from "start" also
		    for (int index = 3; index < exprLen; index++) {
			CalcParser.ExprContext valueCtx = exprs.get(index);
			Object value = evaluate(valueCtx);
			array.insert(index - 3 + start, value);
		    }

		    return removed;
		}
		else {
		    throw new CalcExprException(ctx, "%calc#mustBeArray", sourceClass);
		}
	    }
	    else {
		if (source instanceof ObjectScope) {
		    ObjectScope object = (ObjectScope) source;
		    ObjectScope removed = new ObjectScope();
		    CalcParser.DropObjsContext dropObjs = args.dropObjs();
		    CalcParser.ObjContext addObjs = args.obj();

		    if (dropObjs != null) {
			for (CalcParser.MemberContext member : dropObjs.member()) {
			    String key = getMemberName(this, member);
			    Object value = object.remove(key, settings.ignoreNameCase);
			    removed.map().put(key, value);
			}
		    }
		    else {
			// With no drop list, we need to clear out and return everything
			removed.putAll(object);
			object.clear();
		    }

		    if (addObjs != null) {
			addPairsToObject(addObjs, object);
		    }

		    return removed;
		}
		else {
		    throw new CalcExprException(ctx, "%calc#mustBeObject", sourceClass);
		}
	    }
	}

	@Override
	public Object visitSortExpr(CalcParser.SortExprContext ctx) {
	    CalcParser.Expr1Context e1ctx = ctx.expr1();
	    CalcParser.Expr2Context e2ctx = ctx.expr2();
	    CalcParser.ExprContext objCtx;

	    boolean caseInsensitive = false;
	    boolean sortKeys        = false;
	    boolean sortDescending  = false;

	    if (e1ctx != null) {
		objCtx = e1ctx.expr();
	    }
	    else {
		objCtx = e2ctx.expr(0);
		int flags = getIntValue(e2ctx.expr(1));

		if ((flags & ~SORT_ALL_FLAGS) != 0)
		    throw new Intl.IllegalArgumentException("calc#illegalFlagValues", flags);

		caseInsensitive = (flags & SORT_CASE_INSENSITIVE) != 0;
		sortKeys        = (flags & SORT_SORT_KEYS)        != 0;
		sortDescending  = (flags & SORT_DESCENDING)       != 0;
	    }

	    Object obj = evaluate(objCtx);

	    if (obj instanceof ObjectScope) {
		ObjectScope map = (ObjectScope) obj;
		return sortMap(this, map, objCtx, settings.mc, caseInsensitive, sortKeys, sortDescending);
	    }
	    else if (obj instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<Object> array = (ArrayScope<Object>) obj;
		ArrayScope<Object> result = new ArrayScope<>(array);
		sort(this, result.list(), objCtx, settings.mc, caseInsensitive, sortDescending);
		return result;
	    }
	    else if (obj instanceof SetScope) {
		@SuppressWarnings("unchecked")
		SetScope<Object> set = (SetScope<Object>) obj;
		List<Object> list = new ArrayList<Object>(set.set());
		sort(this, list, objCtx, settings.mc, caseInsensitive, sortDescending);
		return new SetScope<Object>(list);
	    }
	    else if (obj instanceof CollectionScope) {
		return obj;
	    }

	    // A scalar object, just return it unchanged
	    return obj;
	}

	@Override
	public Object visitReverseExpr(CalcParser.ReverseExprContext ctx) {
	    CalcParser.ExprContext expr = ctx.expr1().expr();
	    Object value = evaluate(expr);

	    if (value instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<Object> array = (ArrayScope<Object>) value;
		ArrayScope<Object> result = new ArrayScope<>(array);
		Collections.reverse(result.list());
		return result;
	    }
	    else {
		String string;
		if (value instanceof String)
		    string = (String) value;
		else
		    string = toStringValue(this, expr, value, new StringFormat(false, false));
		StringBuilder buf = new StringBuilder(string);
		return buf.reverse().toString();
	    }
	}

	@Override
	public Object visitUniqueExpr(CalcParser.UniqueExprContext ctx) {
	    CalcParser.ExprContext expr = ctx.expr1().expr();
	    Object value = evaluate(expr);

	    if (value instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<Object> list = (ArrayScope<Object>) value;
		LinkedHashSet<Object> set = new LinkedHashSet<>(list.list());
		return new ArrayScope<Object>(set);
	    }
	    else if (value instanceof ObjectScope || value instanceof SetScope) {
		// Well, this is embarrassing - the keys or set values already have to be unique
		// so there is no point to this
		return value;
	    }
	    else {
		String string;
		if (value instanceof String)
		    string = (String) value;
		else
		    string = toStringValue(this, expr, value, new StringFormat(false, false));

		StringBuilder result = new StringBuilder(string.length());
		string.codePoints().distinct().forEach(cp -> result.appendCodePoint(cp));

		return result.toString();
	    }
	}

	/**
	 * Get a one-character value for "fill" or "pad" from the given value, in the given context.
	 *
	 * @param ctx	The parser context (for error reporting).
	 * @param value	The expression value to be evaluated.
	 * @param op	Whether this is a "fill" or "pad" operation (for errors).
	 * @param def	The default character to use (in case the value is {@code null}).
	 * @return	The single character (either the first of a string, or an int converted to char).
	 */
	private char getCharValue(ParserRuleContext ctx, Object value, String op, char def) {
	    char charValue = def;

	    if (value != null) {
		if (value instanceof String) {
		    String string = (String) value;
		    if (string.length() != 1) {
			throw new CalcExprException(ctx, "%calc#oneCharInt", op);
		    }
		    charValue = string.charAt(0);
		}
		else if (value instanceof Number) {
		    int intValue = ((Number) value).intValue();
		    if (intValue < 0 || intValue > Short.MAX_VALUE) {
			throw new CalcExprException(ctx, "%calc#oneCharInt", op);
		    }
		    charValue = (char) intValue;
		}
	    }

	    return charValue;
	}

	@Override
	public Object visitFillExpr(CalcParser.FillExprContext ctx) {
	    CalcParser.FillArgsContext fillCtx  = ctx.fillArgs();
	    CalcParser.VarContext varCtx        = fillCtx.var();
	    LValueContext lValue                = getLValue(varCtx);
	    Object value		        = lValue.getContextObject(this);
	    List<CalcParser.ExprContext> exprs  = fillCtx.expr();
	    CalcParser.ExprContext fillExpr	= null;

	    Object fillValue = null;
	    int start  = 0;
	    int length = 0;

	    if (exprs.size() > 0) {
		fillExpr = exprs.get(0);
		fillValue = evaluate(fillExpr);
	    }
	    if (exprs.size() == 2) {
		length = getIntValue(exprs.get(1));
	    }
	    else if (exprs.size() == 3) {
		start  = getIntValue(exprs.get(1));
		length = getIntValue(exprs.get(2));
	    }

	    int end = start + length;

	    if (value instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<Object> list = (ArrayScope<Object>) value;
		if (length == 0)
		    length = list.size();
		list.ensureCapacity(end);
		for (int index = start; index < end; index++) {
		    list.setValue(index, fillValue);
		}
	    }
	    else if (value instanceof String) {
		StringBuilder buf = new StringBuilder((String) value);
		if (length == 0)
		    length = buf.length();
		if (buf.length() < end) {
		    buf.setLength(end);
		}

		char fillChar = getCharValue(fillExpr, fillValue, "Fill", '\0');

		for (int index = start; index < end; index++) {
		    buf.setCharAt(index, fillChar);
		}
		value = buf.toString();
	    }
	    else {
		throw new CalcExprException(ctx, "%calc#targetWrongType", ctx.K_FILL().getText());
	    }

	    return lValue.putContextObject(this, value);
	}

	@Override
	public Object visitFormatExpr(CalcParser.FormatExprContext ctx) {
	    CalcParser.ExprListContext exprList = ctx.exprN().exprList();
	    CalcParser.ExprContext formatExpr = exprList.expr(0);
	    String formatString = getStringValue(formatExpr);

	    Object[] args = new Object[exprList.expr().size() - 1];
	    for (int i = 1; i < exprList.expr().size(); i++) {
		CalcParser.ExprContext expr = exprList.expr(i);
		args[i - 1] = evaluate(expr);
	    }

	    return String.format(formatString, args);
	}

	@Override
	public Object visitScanExpr(CalcParser.ScanExprContext ctx) {
	    CalcParser.ExprVarsContext exprVars = ctx.exprVars();
	    CalcParser.ExprContext sourceExpr   = exprVars.expr(0);
	    CalcParser.ExprContext formatExpr   = exprVars.expr(1);
	    String sourceString = getStringValue(sourceExpr);
	    String formatString = getStringValue(formatExpr);

	    List<LValueContext> varList = new ArrayList<>(exprVars.var().size());
	    for (CalcParser.VarContext var : exprVars.var()) {
		varList.add(getLValue(var));
	    }

	    return scanIntoVars(this, sourceString, formatString, varList);
	}

	/**
	 * A transformer for doing string trimming.
	 */
	private class TrimTransformer implements Transformer
	{
		private String op;

		TrimTransformer(final String oper) {
		    op = oper;
		}

		@Override
		public Object apply(final Object value) {
		    if (value instanceof String) {
			String string = (String) value;

			switch (op) {
			    case "trim":
				return string.trim();

			    case "ltrim":
				return CharUtil.ltrim(string);

			    case "rtrim":
				return CharUtil.rtrim(string);

			    default:
				break;
			}
		    }
		    return value;
		}
	}

	@Override
	public Object visitTrimExpr(CalcParser.TrimExprContext ctx) {
	    String op = ctx.K_TRIM().getText().toLowerCase();
	    CalcParser.ExprContext exprCtx = ctx.expr1().expr();
	    Object value = evaluate(exprCtx);

	    switch (op) {
		case "trim":
		case "ltrim":
		case "rtrim":
		    return copyAndTransform(this, exprCtx, value, settings.sortKeys,
			    new TrimTransformer(op));

		default:
		    throw new UnknownOpException(op, ctx);
	    }
	}

	@Override
	public Object visitPadExpr(CalcParser.PadExprContext ctx) {
	    CalcParser.PadArgsContext args	= ctx.padArgs();
	    CalcParser.VarContext varCtx	= args.var();
	    LValueContext lValue		= getLValue(varCtx);
	    Object value			= lValue.getContextObject(this);
	    List<CalcParser.ExprContext> exprs	= args.expr();
	    String op				= ctx.K_PAD().getText();

	    int width = getIntValue(exprs.get(0));
	    int posWidth = Math.abs(width);

	    CalcParser.ExprContext padExpr;
	    Object padValue = null;

	    if (value instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<Object> array = (ArrayScope<Object>) value;
		if (array.size() < posWidth) {
		    if (exprs.size() > 1) {
			padExpr = exprs.get(1);
			padValue = evaluate(padExpr);
		    }
		    else {
			padValue = BigInteger.ZERO;
		    }

		    int leftover = posWidth - array.size();

		    switch (op.toLowerCase()) {
			case "pad":
			    // Note: same logic as CharUtil.padToWidth
			    int left = (width < 0) ? leftover / 2 : (leftover + 1) / 2;
			    int right = leftover - left;
			    for (int i = 0; i < left; i++) {
				array.insert(i, padValue);
			    }
			    for (int i = 0; i < right; i++) {
				array.add(padValue);
			    }
			    break;

			case "lpad":
			    for (int i = 0; i < leftover; i++) {
				array.insert(i, padValue);
			    }
			    break;

			case "rpad":
			    for (int i = 0; i < leftover; i++) {
				array.add(padValue);
			    }
			    break;

			default:
			    throw new UnknownOpException(op, ctx);
		    }
		}
	    }
	    else if (!(value instanceof ObjectScope)) {
		String input = toStringValue(this, varCtx, value, new StringFormat(false, settings));
		if (input.length() < posWidth) {
		    StringBuilder buf = new StringBuilder(width);
		    CharUtil.Justification just;
		    char padChar = ' ';

		    if (exprs.size() > 1) {
			padExpr = exprs.get(1);
			padValue = evaluate(padExpr);
			padChar = getCharValue(padExpr, padValue, "Pad", ' ');
		    }

		    switch (op.toLowerCase()) {
			case "pad":
			    just = CENTER;
			    break;

			case "lpad":
			    just = RIGHT;
			    break;

			case "rpad":
			    just = LEFT;
			    break;

			default:
			    throw new UnknownOpException(op, ctx);
		    }

		    CharUtil.padToWidth(buf, input, width, padChar, just);
		    value = buf.toString();
		}
	    }
	    else {
		throw new CalcExprException(ctx, "%calc#targetWrongType", op);
	    }

	    return lValue.putContextObject(this, value);
	}

	@Override
	public Object visitFibExpr(CalcParser.FibExprContext ctx) {
	    BigDecimal e = getDecimalValue(ctx.expr1().expr());

	    return MathUtil.fib(e);
	}

	@Override
	public Object visitBernExpr(CalcParser.BernExprContext ctx) {
	    int n = getIntValue(ctx.expr1().expr());

	    return MathUtil.bernoulli(n, settings.mcDivide, settings.rationalMode);
	}

	@Override
	public Object visitDecExpr(CalcParser.DecExprContext ctx) {
	    // Note: this will convert fractions, strings, etc.
	    return getDecimalValue(ctx.expr1().expr());
	}

	@Override
	public Object visitDateExpr(CalcParser.DateExprContext ctx) {
	    CalcParser.Expr1Context e1ctx = ctx.expr1();
	    if (e1ctx != null) {
		CalcParser.ExprContext expr = e1ctx.expr();
		Object obj = evaluate(expr);
		return DateUtil.valueOf(obj, "");
	    }
	    else {
		CalcParser.Expr2Context e2ctx = ctx.expr2();
		if (e2ctx != null) {
		    CalcParser.ExprContext expr1 = e2ctx.expr(0);
		    CalcParser.ExprContext expr2 = e2ctx.expr(1);
		    String s = getNonNullString(expr1, evaluate(expr1));
		    String f = getNonNullString(expr2, evaluate(expr2));

		    return DateUtil.valueOf(s, f);
		}
		else {
		    CalcParser.Expr3Context e3ctx = ctx.expr3();
		    int m = getIntValue(e3ctx.expr(0));
		    int d = getIntValue(e3ctx.expr(1));
		    int y = getIntValue(e3ctx.expr(2));

		    return DateUtil.date(m, d, y);
		}
	    }
	}

	@Override
	public Object visitToBaseExpr(CalcParser.ToBaseExprContext ctx) {
	    CalcParser.Expr2Context e2ctx = ctx.expr2();
	    CalcParser.ExprContext vExpr = e2ctx.expr(0);
	    CalcParser.ExprContext rExpr = e2ctx.expr(1);

	    Object valueObj = evaluate(vExpr);
	    int radix = getIntValue(rExpr);

	    if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
		throw new CalcExprException(rExpr, "%calc#radixOutOfRange", radix, Character.MIN_RADIX, Character.MAX_RADIX);

	    if (valueObj instanceof BigInteger) {
		return ((BigInteger) valueObj).toString(radix);
	    }
	    else {
		BigDecimal dValue = convertToDecimal(valueObj, settings.mc, vExpr);
		return MathUtil.toString(dValue, radix, settings.mcDivide);
	    }
	}

	@Override
	public Object visitFromBaseExpr(CalcParser.FromBaseExprContext ctx) {
	    CalcParser.Expr2Context e2ctx = ctx.expr2();
	    CalcParser.ExprContext vExpr = e2ctx.expr(0);
	    CalcParser.ExprContext rExpr = e2ctx.expr(1);

	    String value = getStringValue(vExpr);
	    int radix = getIntValue(rExpr);

	    if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
		throw new CalcExprException(rExpr, "%calc#radixOutOfRange", radix, Character.MIN_RADIX, Character.MAX_RADIX);

	    return MathUtil.fromString(value, radix, settings.mcDivide);
	}

	@Override
	public Object visitFracExpr(CalcParser.FracExprContext ctx) {
	    try {
		CalcParser.Expr1Context expr1 = ctx.expr1();
		if (expr1 != null) {
		    CalcParser.ExprContext expr = expr1.expr();
		    Object e = evaluate(expr);
		    return convertToFraction(e, expr);
		}
		else {
		    CalcParser.Expr2Context e2ctx = ctx.expr2();
		    if (e2ctx != null) {
			BigInteger n = getIntegerValue(e2ctx.expr(0));
			BigInteger d = getIntegerValue(e2ctx.expr(1));

			return new BigFraction(n, d);
		    }
		    else {
			CalcParser.Expr3Context e3ctx = ctx.expr3();
			BigInteger i = getIntegerValue(e3ctx.expr(0));
			BigInteger n = getIntegerValue(e3ctx.expr(1));
			BigInteger d = getIntegerValue(e3ctx.expr(2));

			return new BigFraction(i, n, d);
		    }
		}
	    }
	    catch (ArithmeticException ae) {
		// Possible divide by zero errors (at least)
		throw new CalcExprException(ae, ctx);
	    }
	}

	@Override
	public Object visitComplexFuncExpr(CalcParser.ComplexFuncExprContext ctx) {
	    try {
		CalcParser.Expr1Context e1ctx = ctx.expr1();

		if (e1ctx != null) {
		    CalcParser.ExprContext expr = e1ctx.expr();
		    Object e = evaluate(expr);

		    if (e instanceof ArrayScope) {
			@SuppressWarnings("unchecked")
			ArrayScope<Object> array = (ArrayScope<Object>) e;
			return ComplexNumber.valueOf(array.list());
		    }
		    else if (e instanceof ObjectScope) {
			ObjectScope obj = (ObjectScope) e;
			return ComplexNumber.valueOf(obj.map());
		    }
		    else if (e instanceof SetScope) {
			@SuppressWarnings("unchecked")
			SetScope<Object> set = (SetScope<Object>) e;
			return ComplexNumber.valueOf(set.set());
		    }
		    else {
			return ComplexNumber.valueOf(e);
		    }
		}
		else {
		    CalcParser.Expr2Context e2ctx = ctx.expr2();

		    CalcParser.ExprContext expr1 = e2ctx.expr(0);
		    CalcParser.ExprContext expr2 = e2ctx.expr(1);
		    Object o1 = evaluate(expr1);
		    Object o2 = evaluate(expr2);

		    if (settings.rationalMode || o1 instanceof BigFraction || o2 instanceof BigFraction) {
			BigFraction rFrac = convertToFraction(o1, expr1);
			BigFraction iFrac = convertToFraction(o2, expr2);

			return new ComplexNumber(rFrac, iFrac);
		    }
		    else {
			BigDecimal r = convertToDecimal(o1, settings.mc, expr1);
			BigDecimal i = convertToDecimal(o2, settings.mc, expr2);

			return new ComplexNumber(r, i);
		    }
		}
	    }
	    catch (Exception ex) {
		throw new CalcExprException(ex, ctx);
	    }
	}

	@Override
	public Object visitQuaternionFuncExpr(CalcParser.QuaternionFuncExprContext ctx) {
	    try {
		CalcParser.ExprContext expr1;
		CalcParser.ExprContext expr2;
		CalcParser.ExprContext expr3;
		CalcParser.ExprContext expr4;
		Object o1, o2, o3, o4;
		BigFraction f1 = null;
		BigFraction f2 = null;
		BigFraction f3 = null;
		BigFraction f4 = null;
		BigDecimal d1 = null;
		BigDecimal d2 = null;
		BigDecimal d3 = null;
		BigDecimal d4 = null;

		CalcParser.Expr4Context e4ctx = ctx.expr4();
		if (e4ctx != null) {
		    expr1 = e4ctx.expr(0);
		    expr2 = e4ctx.expr(1);
		    expr3 = e4ctx.expr(2);
		    expr4 = e4ctx.expr(3);
		    o1 = evaluate(expr1);
		    o2 = evaluate(expr2);
		    o3 = evaluate(expr3);
		    o4 = evaluate(expr4);

		    if (settings.rationalMode ||
			(o1 instanceof BigFraction || o2 instanceof BigFraction || o3 instanceof BigFraction || o4 instanceof BigFraction)) {
			f1 = convertToFraction(o1, expr1);
			f2 = convertToFraction(o2, expr2);
			f3 = convertToFraction(o3, expr3);
			f4 = convertToFraction(o4, expr4);
		    }
		    else {
			d1 = convertToDecimal(o1, settings.mc, expr1);
			d2 = convertToDecimal(o2, settings.mc, expr2);
			d3 = convertToDecimal(o3, settings.mc, expr3);
			d4 = convertToDecimal(o4, settings.mc, expr4);
		    }
		}
		else {
		    CalcParser.Expr3Context e3ctx = ctx.expr3();
		    if (e3ctx != null) {
			expr1 = e3ctx.expr(0);
			expr2 = e3ctx.expr(1);
			expr3 = e3ctx.expr(2);
			o1 = evaluate(expr1);
			o2 = evaluate(expr2);
			o3 = evaluate(expr3);

			if (settings.rationalMode ||
			    (o1 instanceof BigFraction || o2 instanceof BigFraction || o3 instanceof BigFraction)) {
			    f1 = convertToFraction(o1, expr1);
			    f2 = convertToFraction(o2, expr2);
			    f3 = convertToFraction(o3, expr3);
			}
			else {
			    d1 = convertToDecimal(o1, settings.mc, expr1);
			    d2 = convertToDecimal(o2, settings.mc, expr2);
			    d3 = convertToDecimal(o3, settings.mc, expr3);
			}
		    }
		    else {
			CalcParser.Expr2Context e2ctx = ctx.expr2();
			if (e2ctx != null) {
			    expr1 = e2ctx.expr(0);
			    expr2 = e2ctx.expr(1);
			    o1 = evaluate(expr1);
			    o2 = evaluate(expr2);

			    if (settings.rationalMode || o1 instanceof BigFraction || o2 instanceof BigFraction) {
				f1 = convertToFraction(o1, expr1);
				f2 = convertToFraction(o2, expr2);
			    }
			    else {
				d1 = convertToDecimal(o1, settings.mc, expr1);
				d2 = convertToDecimal(o2, settings.mc, expr2);
			    }
			}
		    }
		}

		if (f1 != null) {
		    return new Quaternion(f1, f2, f3, f4);
		}
		else if (d1 != null) {
		    return new Quaternion(d1, d2, d3, d4);
		}
		else {
		    CalcParser.Expr1Context e1ctx = ctx.expr1();
		    expr1 = e1ctx.expr();
		    o1 = evaluate(expr1);

		    return Quaternion.valueOf(o1);
		}
	    }
	    catch (Exception ex) {
		throw new CalcExprException(ex, ctx);
	    }
	}

	@Override
	public Object visitRomanExpr(CalcParser.RomanExprContext ctx) {
	    String exprString = getStringValue(ctx.expr1().expr());

	    try {
		return NumericUtil.convertFromRoman(exprString);
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, ctx);
	    }
	}

	/**
	 * A transformer for doing case conversion.
	 */
	private class ConvertCaseTransformer implements Transformer
	{
		private boolean toUpper;
		private boolean ignoreCase;

		ConvertCaseTransformer(final boolean upper, final boolean ignore) {
		    toUpper = upper;
		    ignoreCase = ignore;
		}

		@Override
		public Object apply(final Object value) {
		    if (value instanceof String) {
			String string = (String) value;
			if (toUpper)
			    return string.toUpperCase();
			else
			    return string.toLowerCase();
		    }
		    return value;
		}

		@Override
		public boolean forKeys() {
		    return !ignoreCase;
		}
	}

	@Override
	public Object visitCaseConvertExpr(CalcParser.CaseConvertExprContext ctx) {
	    CalcParser.ExprContext exprCtx = ctx.expr1().expr();
	    boolean upper = ctx.K_UPPER() != null;

	    return copyAndTransform(this, exprCtx, evaluate(exprCtx), settings.sortKeys,
		    new ConvertCaseTransformer(upper, settings.ignoreNameCase));
	}

	@Override
	public Object visitFactorsExpr(CalcParser.FactorsExprContext ctx) {
	    ArrayScope<BigInteger> result = new ArrayScope<>();
	    BigInteger n = getIntegerValue(ctx.expr1().expr());

	    MathUtil.getFactors(n, result.list());

	    return result;
	}

	@Override
	public Object visitPrimeFactorsExpr(CalcParser.PrimeFactorsExprContext ctx) {
	    ArrayScope<BigInteger> result = new ArrayScope<>();
	    BigInteger n = getIntegerValue(ctx.expr1().expr());

	    MathUtil.getPrimeFactors(n, result.list());

	    return result;
	}

	@Override
	public Object visitCharsExpr(CalcParser.CharsExprContext ctx) {
	    CalcParser.ExprContext expr = ctx.expr1().expr();
	    Object value = evaluate(expr);

	    if (value instanceof String) {
		String string = (String) value;
		final ArrayScope<String> result = new ArrayScope<>();

		string.codePoints().forEachOrdered(cp -> result.add(String.valueOf(Character.toChars(cp))));

		return result;
	    }
	    else if (value instanceof Number) {
		int cp = convertToInt(value, settings.mc, expr);
		char[] chars = Character.toChars(cp);

		return new String(chars);
	    }
	    else if (value instanceof ArrayScope) {
		final ArrayScope<String> result = new ArrayScope<>();
		// Needs to be array of integer codepoints
		@SuppressWarnings("unchecked")
		ArrayScope<Object> array = (ArrayScope<Object>) value;
		for (Object obj : array.list()) {
		    int cp = convertToInt(obj, settings.mc, expr);
		    char[] chars = Character.toChars(cp);
		    result.add(new String(chars));
		}

		return result;
	    }
	    else {
		throw new CalcExprException(expr, "%calc#illegalArgument", typeof(value).toString(), "chars");
	    }
	}

	@Override
	public Object visitCodesExpr(CalcParser.CodesExprContext ctx) {
	    CalcParser.ExprContext expr = ctx.expr1().expr();
	    Object value = evaluate(expr);
	    String string;

	    if (value instanceof ArrayScope) {
		final ArrayScope<Integer> result = new ArrayScope<>();
		// Needs to be array of strings
		@SuppressWarnings("unchecked")
		ArrayScope<Object> array = (ArrayScope<Object>) value;
		for (Object obj : array.list()) {
		    if (obj instanceof String || obj instanceof Number) {
			string = getNonNullString(expr, obj);
			string.codePoints().forEachOrdered(cp -> result.add(cp));
		    }
		    else {
			throw new CalcExprException(expr, "%calc#illegalArgument", typeof(value).toString(), "codes");
		    }
		}

		return result;
	    }
	    else if (value instanceof String || value instanceof Number) {
		string = getNonNullString(expr, value);
	    }
	    else {
		throw new CalcExprException(expr, "%calc#illegalArgument", typeof(value).toString(), "codes");
	    }

	    if (Character.codePointCount(string, 0, string.length()) == 1) {
		return BigInteger.valueOf(Character.codePointAt(string, 0));
	    }
	    else {
		final ArrayScope<Integer> result = new ArrayScope<>();

		string.codePoints().forEachOrdered(cp -> result.add(cp));

		return result;
	    }
	}

	@Override
	public Object visitDayOfWeekExpr(CalcParser.DayOfWeekExprContext ctx) {
	    LocalDate date = getDateValue(ctx.expr1().expr());
	    DayOfWeek dow = date.getDayOfWeek();
	    // Adjust the return b/c I didn't like their ordering
	    // theirs = 1 (Monday) to 7 (Sunday) while we have defined
	    // ours = 0 (Sunday) to 6 (Saturday)
	    int adjustedDow = dow.getValue() % 7;
	    return BigInteger.valueOf((long) adjustedDow);
	}

	@Override
	public Object visitDayOfMonthExpr(CalcParser.DayOfMonthExprContext ctx) {
	    LocalDate date = getDateValue(ctx.expr1().expr());
	    int dom = date.getDayOfMonth();
	    // Theirs matches ours: 1 .. n value
	    return BigInteger.valueOf((long) dom);
	}

	@Override
	public Object visitDayOfYearExpr(CalcParser.DayOfYearExprContext ctx) {
	    LocalDate date = getDateValue(ctx.expr1().expr());
	    int doy = date.getDayOfYear();
	    // Theirs matches ours: 1 .. 365/366 value
	    return BigInteger.valueOf((long) doy);
	}

	@Override
	public Object visitMonthOfYearExpr(CalcParser.MonthOfYearExprContext ctx) {
	    LocalDate date = getDateValue(ctx.expr1().expr());
	    int moy = date.getMonthValue();
	    // Theirs matches ours: 1 .. 12 value
	    return BigInteger.valueOf((long) moy);
	}

	@Override
	public Object visitYearOfDateExpr(CalcParser.YearOfDateExprContext ctx) {
	    LocalDate date = getDateValue(ctx.expr1().expr());
	    int yod = date.getYear();
	    return BigInteger.valueOf((long) yod);
	}

	@Override
	public Object visitEvalExpr(CalcParser.EvalExprContext ctx) {
	    String exprString = getStringValue(ctx.expr1().expr());

	    return Calc.processString(exprString, true);
	}


	@Override
	public Object visitExecExpr(CalcParser.ExecExprContext ctx) {
	    List<CalcParser.ExprContext> exprs = ctx.exprN().exprList().expr();
	    List<Object> objects = buildValueList(this, exprs, Conversion.STRING);

	    // As a convenience, if we're on Windows and the target is a ".bat" or ".cmd" file
	    // then prepend "cmd /c" before running it.
	    if (RUNNING_ON_WINDOWS) {
		File f = Which.find(objects.get(0).toString());
		if (f != null && Which.isWindowsBatch(f)) {
		    objects.add(0, "cmd");
		    objects.add(1, "/c");
		}
	    }

	    String[] args = new String[objects.size()];
	    for (int i = 0; i < objects.size(); i++) {
		args[i] = objects.get(i).toString();
	    }

	    try {
		RunCommand cmd = new RunCommand(args).removeStdEnv();
		StringBuilder result = new StringBuilder();
		int retCode = cmd.runToCompletion(result, true);

		return result.toString();
	    }
	    catch (RuntimeException rex) {
		// These will wrap other checked exceptions, so unwrap first
		throw new CalcExprException(ctx, rex.getCause().getLocalizedMessage());
	    }
	}

	@Override
	public Object visitColorExpr(CalcParser.ColorExprContext ctx) {
	    String value = getStringValue(ctx.expr1().expr());
	    return ConsoleColor.color(value, Calc.getColoredMode());
	}

	@Override
	public Object visitDecodeExpr(CalcParser.DecodeExprContext ctx) {
	    String source = getStringValue(ctx.expr1().expr());
	    return Base64.decodeUTF8(source);
	}

	@Override
	public Object visitEncodeExpr(CalcParser.EncodeExprContext ctx) {
	    String source = getStringValue(ctx.expr1().expr());
	    return Base64.encodeUTF8(source);
	}

	@Override
	public Object visitExistsExpr(CalcParser.ExistsExprContext ctx) {
	    CalcParser.Expr1Context e1ctx = ctx.expr1();
	    CalcParser.Expr2Context e2ctx = ctx.expr2();
	    CalcParser.ExprContext flagExpr = null;
	    String path = "";
	    String flags = "";

	    if (e1ctx != null) {
		path = getStringValue(e1ctx.expr());
	    }
	    else {
		path = getStringValue(e2ctx.expr(0));
		flagExpr = e2ctx.expr(1);
		flags = getStringValue(flagExpr).trim();
	    }

	    // The only combinations that make sense are: "d", "f", "fr", "fw", "fx"
	    if (flags.isEmpty())
		flags = "fr";

	    try {
		return Boolean.valueOf(FileUtilities.exists(new File(path), flags));
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, flagExpr);
	    }
	}

	@Override
	public Object visitFileInfoExpr(CalcParser.FileInfoExprContext ctx) {
	    String fileName = getStringValue(ctx.expr1().expr());

	    FileInfo finfo = new FileInfo(fileName);
	    if (finfo.exists()) {
		Map<String, Object> map = ClassUtil.getMapFromObject(finfo);
		return new ObjectScope(map);
	    }
	    return CollectionScope.EMPTY;
	}

	/**
	 * A line processor for collecting matching files.
	 */
	private class FindFilesProcessor implements LineProcessor
	{
		private ArrayScope<String> result;
		private boolean fullPaths;

		FindFilesProcessor(ArrayScope<String> res, boolean paths) {
		    result = res;
		    fullPaths = paths;
		}

		@Override
		public boolean preProcess(final File inputFile) {
		    try {
			result.add(fullPaths ? inputFile.getCanonicalPath() : inputFile.getName());
		    }
		    catch (IOException ioe) {
			// This will be from "getCanonicalPath", so just use the name
			result.add(inputFile.getName());
		    }
		    return true;
		}
	}

	@Override
	public Object visitFindFilesExpr(CalcParser.FindFilesExprContext ctx) {
	    CalcParser.Expr2Context e2ctx = ctx.expr2();
	    CalcParser.Expr3Context e3ctx = ctx.expr3();
	    CalcParser.ExprContext flagExpr = null;
	    String dir     = "";
	    String pattern = "";
	    String flags   = null;
	    boolean recursive  = false;
	    boolean fullPaths  = false;
	    boolean ignoreCase = false;
	    ArrayScope<String> result = new ArrayScope<>();

	    if (e2ctx != null) {
		dir = getStringValue(e2ctx.expr(0));
		pattern = getStringValue(e2ctx.expr(1));
	    }
	    else {
		dir = getStringValue(e3ctx.expr(0));
		pattern = getStringValue(e3ctx.expr(1));
		flagExpr = e3ctx.expr(2);
		flags = getStringValue(flagExpr).trim();
		if (flags.contains("*")) {
		    recursive = true;
		    flags = flags.replace("*", "");
		}
		if (flags.contains("!")) {
		    fullPaths = true;
		    flags = flags.replace("!", "");
		}
		if (flags.contains("~")) {
		    ignoreCase = true;
		    flags = flags.replace("~", "");
		}
	    }

	    try {
		new DirectoryProcessor(dir, new FindFilesProcessor(result, fullPaths))
			.setWildcardFilter(pattern, CharUtil.getNullForEmpty(flags), ignoreCase)
			.setNameOnlyMode(true)
			.processDirectory(recursive);

		Collections.sort(result.list());

		return result;
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, ctx);
	    }
	}

	@Override
	public Object visitReadExpr(CalcParser.ReadExprContext ctx) {
	    List<CalcParser.ExprContext> exprs = ctx.readExprs().expr();

	    String fileName = getStringValue(exprs.get(0));
	    Charset cs = exprs.size() > 1 ? getCharsetValue(exprs.get(1), false) : null;

	    try {
		return FileUtilities.readRawText(new File(fileName), cs);
	    }
	    catch (IOException ioe) {
		throw new CalcExprException(ioe, ctx);
	    }
	}

	@Override
	public Object visitWriteExpr(CalcParser.WriteExprContext ctx) {
	    List<CalcParser.ExprContext> exprs = ctx.writeExprs().expr();

	    CalcParser.ExprContext exprCtx = exprs.get(0);
	    Object outputObj = evaluate(exprCtx);
	    String fileName = getStringValue(exprs.get(1));
	    Charset cs = exprs.size() > 2 ? getCharsetValue(exprs.get(2), false) : null;
	    CharSequence seq = null;

	    try {
		if (outputObj != null) {
		    String eol = Environment.lineSeparator();

		    if (outputObj instanceof ArrayScope) {
			@SuppressWarnings("unchecked")
			ArrayScope<Object> array = (ArrayScope<Object>) outputObj;
			StringBuilder buf = new StringBuilder();
			for (Object obj : array.list()) {
			    buf.append(getNonNullString(exprCtx, obj)).append(eol);
			}
			seq = buf;
		    }
		    else if (outputObj instanceof SetScope) {
			@SuppressWarnings("unchecked")
			SetScope<Object> set = (SetScope<Object>) outputObj;
			StringBuilder buf = new StringBuilder();
			for (Object obj : set.set()) {
			    buf.append(getNonNullString(exprCtx, obj)).append(eol);
			}
			seq = buf;
		    }
		    else if (outputObj instanceof ObjectScope) {
			ObjectScope obj = (ObjectScope) outputObj;
			// For now (maybe always?) write out a JSON object
			seq = toStringValue(this, exprCtx, obj.map(), new StringFormat(true, false, false, false, "", 0), "", 0);
		    }
		    else {
			seq = getNonNullString(exprCtx, outputObj);
		    }
		    return BigInteger.valueOf(FileUtilities.writeRawText(seq, new File(fileName), cs));
		}
		return BigInteger.ZERO;
	    }
	    catch (IOException ioe) {
		throw new CalcExprException(ioe, ctx);
	    }
	}

	private Reader newReader(final String fileName, final Charset cs) throws IOException {
	    Path path = Paths.get(fileName);
	    if (cs == null)
		return Files.newBufferedReader(path);
	    else
		return Files.newBufferedReader(path, cs);
	}

	private Writer newWriter(final String fileName, final Charset cs) throws IOException {
	    Path path = Paths.get(fileName);
	    if (cs == null)
		return Files.newBufferedWriter(path);
	    else
		return Files.newBufferedWriter(path, cs);
	}

	@Override
	public Object visitReadPropExpr(CalcParser.ReadPropExprContext ctx) {
	    List<CalcParser.ExprContext> exprs = ctx.readExprs().expr();

	    String fileName = getStringValue(exprs.get(0));
	    Charset cs = exprs.size() > 1 ? getCharsetValue(exprs.get(1), false) : null;

	    try (Reader r = newReader(fileName, cs)) {
		Properties p = new Properties();
		p.load(r);
		ObjectScope obj = new ObjectScope();
		Set<String> sortedNames = new TreeSet<>(NATURAL_SENSITIVE_COMPARATOR);
		sortedNames.addAll(p.stringPropertyNames());
		for (String key : sortedNames) {
		    obj.setValue(key, p.getProperty(key));
		}
		return obj;
	    }
	    catch (IOException ioe) {
		throw new CalcExprException(ioe, ctx);
	    }
	}

	@Override
	public Object visitWritePropExpr(CalcParser.WritePropExprContext ctx) {
	    List<CalcParser.ExprContext> exprs = ctx.writeExprs().expr();

	    CalcParser.ExprContext exprCtx = exprs.get(0);
	    Object outputObj = evaluate(exprCtx);
	    String fileName = getStringValue(exprs.get(1));
	    Charset cs = exprs.size() > 2 ? getCharsetValue(exprs.get(2), false) : null;
	    Properties p = new Properties();

	    try {
		if (outputObj != null) {
		    if (outputObj instanceof ArrayScope) {
			@SuppressWarnings("unchecked")
			ArrayScope<Object> array = (ArrayScope<Object>) outputObj;
			int seq = 0;
			for (Object obj : array.list()) {
			    String key = String.format("_%1$d", seq++);
			    p.setProperty(key, getNonNullString(exprCtx, obj));
			}
		    }
		    else if (outputObj instanceof SetScope) {
			@SuppressWarnings("unchecked")
			SetScope<Object> set = (SetScope<Object>) outputObj;
			int seq = 0;
			for (Object obj : set.set()) {
			    String key = String.format("_%1$d", seq++);
			    p.setProperty(key, getNonNullString(exprCtx, obj));
			}
		    }
		    else if (outputObj instanceof ObjectScope) {
			ObjectScope obj = (ObjectScope) outputObj;
			for (String key : obj.keyList()) {
			    p.setProperty(key, getNonNullString(exprCtx, obj.getValue(key, false)));
			}
		    }
		    else {
			p.setProperty("_0", getNonNullString(exprCtx, outputObj));
		    }

		    try (Writer w = newWriter(fileName, cs)) {
			p.store(w, String.format("Calc %1$s", Environment.getProductVersion()));
		    }
		    return BigInteger.valueOf(p.size());
		}
		return BigInteger.ZERO;
	    }
	    catch (IOException ioe) {
		throw new CalcExprException(ioe, ctx);
	    }
	}

	@Override
	public Object visitDeleteExpr(CalcParser.DeleteExprContext ctx) {
	    CalcParser.ExprNContext exprN = ctx.exprN();
	    List<CalcParser.ExprContext> exprs = exprN.exprList().expr();
	    Boolean result = Boolean.TRUE;

	    for (CalcParser.ExprContext expr : exprs) {
		Path path = new File(getStringValue(expr)).toPath();
		try {
		    if (!Files.deleteIfExists(path))
			result = Boolean.FALSE;
		}
		catch (DirectoryNotEmptyException dnee) {
		    throw new CalcExprException(dnee, ctx);
		}
		catch (IOException ioe) {
		    throw new CalcExprException(ioe, ctx);
		}
	    }

	    return result;
	}

	@Override
	public Object visitRenameExpr(CalcParser.RenameExprContext ctx) {
	    CalcParser.Expr2Context expr2 = ctx.expr2();
	    Path p1 = new File(getStringValue(expr2.expr(0))).toPath();
	    Path p2 = new File(getStringValue(expr2.expr(1))).toPath();

	    try {
		Path newPath = Files.move(p1, p2);
		return newPath.toRealPath().toString();
	    }
	    catch (FileAlreadyExistsException faee) {
		throw new CalcExprException(faee, ctx);
	    }
	    catch (IOException ioe) {
		throw new CalcExprException(ioe, ctx);
	    }
	}


	/**
	 * Transformer for the "matches" function.
	 */
	private class MatchesTransformer implements Transformer
	{
		private ParserRuleContext exprCtx;
		private Pattern pattern;

		MatchesTransformer(final ParserRuleContext ctx, final Pattern p) {
		    exprCtx = ctx;
		    pattern = p;
		}

		@Override
		public boolean forKeys() {
		    return true;
		}

		@Override
		public boolean copyNull() {
		    return false;
		}

		@Override
		public Object apply(final Object value) {
		    String string = getNonNullString(exprCtx, value);
		    return pattern.matcher(string).matches() ? value : null;
		}

		@Override
		public Object applyToMap(final Object value, final boolean key) {
		    // Only apply the transform on keys
		    if (key) {
			return apply(value);
		    }
		    else {
			return value;
		    }
		}
	}

	@Override
	public Object visitMatchesExpr(CalcParser.MatchesExprContext ctx) {
	    CalcParser.Expr2Context expr2 = ctx.expr2();
	    CalcParser.ExprContext inputExpr;
	    CalcParser.ExprContext patternExpr;
	    int flags = 0x0000;

	    if (expr2 != null) {
		inputExpr = expr2.expr(0);
		patternExpr = expr2.expr(1);
	    }
	    else {
		CalcParser.Expr3Context expr3 = ctx.expr3();
		inputExpr = expr3.expr(0);
		patternExpr = expr3.expr(1);
		flags = getIntValue(expr3.expr(2));
	    }

	    Object input = evaluate(inputExpr);
	    String pattern = getStringValue(patternExpr);

	    Pattern p = Pattern.compile(pattern, patternFlags(flags));

	    // For lists, objects, and sets, return a similar object with only the matching keys or values
	    if (input instanceof CollectionScope) {
		return copyAndTransform(this, inputExpr, input, settings.sortKeys,
			new MatchesTransformer(inputExpr, p));
	    }
	    else {
		// For ordinary objects, just return a boolean if the string representation matches the pattern
		String inputString = getNonNullString(inputExpr, input);
		return Boolean.valueOf(p.matcher(inputString).matches());
	    }
	}

	@Override
	public Object visitCallersExpr(CalcParser.CallersExprContext ctx) {
	    CalcParser.ExprContext expr = ctx.optExpr().expr();

	    List<FunctionScope> funcStack = FunctionScope.getCallers(currentScope);

	    if (expr != null) {
		int level = getIntValue(expr);

		if (level < 0 || level >= funcStack.size())
		    return null;

		return funcStack.get(level).getFullFunctionName();
	    }
	    else {
		ArrayScope<String> callers = new ArrayScope<>();

		for (FunctionScope func : funcStack) {
		    callers.add(func.getFullFunctionName());
		}

		return callers;
	    }
	}

	@Override
	public Object visitDefinedExpr(CalcParser.DefinedExprContext ctx) {
	    boolean allDefined = true;

	    for (CalcParser.MemberContext member : ctx.idExpr().member()) {
		String name = getMemberName(this, member);

		// Short-circuit analysis: first one that fails ends the loop
		if (!currentScope.isDefined(name, settings.ignoreNameCase)) {
		    allDefined = false;
		    break;
		}
	    }

	    return Boolean.valueOf(allDefined);
	}

	/**
	 * Visitor for the {@code SumOf} function, to do the actual summing during iteration.
	 */
	private class SumOfVisitor implements IterationVisitor
	{
		private BigFraction sumFrac = BigFraction.ZERO;
		private ComplexNumber sumCmplx = C_ZERO;
		private Quaternion sumQuat = Quaternion.ZERO;
		private BigDecimal sum = BigDecimal.ZERO;
		private ParserRuleContext ctx;
		private Conversion conv;

		public SumOfVisitor(final ParserRuleContext context, final Conversion conversion) {
		    ctx = context;
		    conv = conversion;
		}

		@Override
		public Purpose getPurpose() {
		    return Purpose.SUM;
		}

		@Override
		public Object apply(final Object value) {
		    switch (conv) {
			case FRACTION:
			    BigFraction frac = convertToFraction(value, ctx);
			    sumFrac = sumFrac.add(frac);
			    return sumFrac;
			case COMPLEX:
			    ComplexNumber cmplx = ComplexNumber.valueOf(value);
			    sumCmplx = sumCmplx.add(cmplx);
			    return sumCmplx;
			case QUATERNION:
			    Quaternion quat = Quaternion.valueOf(value);
			    sumQuat = sumQuat.add(quat);
			    return sumQuat;
			default:
			    BigDecimal dec = convertToDecimal(value, settings.mc, ctx);
			    sum = sum.add(dec, settings.mc);
			    return sum;
		    }
		}

		@Override
		public Object finalValue(final Number start, final Number stop, final Number step) {
		    // Sum of arithmetic progression: n * ((a1 + an) / 2)

		    Number len = IterationVisitor.length(start, stop, step);

		    // This function is only used for the "dot range" case, so there will only be
		    // fraction / not choice, which we further divide into fraction/integer/decimal here
		    // but no need to handle complex, or quaternion (at least for now)
		    if (conv == Conversion.FRACTION) {
			BigFraction fStart = BigFraction.valueOf(start);
			BigFraction fStop  = BigFraction.valueOf(stop);
			BigFraction fLen   = BigFraction.valueOf(len);

			return fLen.multiply(fStart.add(fStop).divide(BigFraction.TWO));
		    }
		    else {
			if (start instanceof Integer) {
			    int iStart = (Integer) start;
			    int iStop  = (Integer) stop;
			    int iLen   = (Integer) len;

			    // Do the division last so we don't lose the .5 fraction for odd values
			    return (iLen * (iStart + iStop)) / 2;
			}
			else {
			    BigDecimal dStart = (BigDecimal) start;
			    BigDecimal dStop  = (BigDecimal) stop;
			    BigDecimal dLen   = (BigDecimal) len;

			    return dLen.multiply(dStart.add(dStop).divide(D_TWO));
			}
		    }
		}
	}

	@Override
	public Object visitSumOfExpr(CalcParser.SumOfExprContext ctx) {
	    SumOfVisitor sumVisitor;
	    Conversion conv;
	    Object sum = null;

	    CalcParser.DotRangeContext dotRange = ctx.dotRange();
	    if (dotRange == null) {
		List<CalcParser.ExprContext> exprs = ctx.exprN().exprList().expr();
		conv = Conversion.fromValue(getFirstValue(ctx, evaluate(exprs.get(0)), false));
		if (settings.rationalMode)
		    conv = Conversion.FRACTION;
		else if (conv == Conversion.STRING || conv == Conversion.INTEGER)
		    conv = Conversion.DECIMAL;

		List<Object> objects = buildValueList(this, exprs, conv);
		sumVisitor = new SumOfVisitor(ctx, conv);

		for (Object obj : objects) {
		    sum = sumVisitor.apply(obj);
		}
	    }
	    else {
		conv = settings.rationalMode ? Conversion.FRACTION : Conversion.DECIMAL;
		sumVisitor = new SumOfVisitor(ctx, conv);

		sum = iterateOverDotRange(null, dotRange.expr(), dotRange.DOTS() != null, sumVisitor, false, false);
	    }

	    return sum;
	}

	private class ProductOfVisitor implements IterationVisitor
	{
		private BigFraction productFrac = BigFraction.ONE;
		private ComplexNumber productCmplx = C_ONE;
		private Quaternion productQuat = Quaternion.ONE;
		private BigDecimal product = BigDecimal.ONE;
		private ParserRuleContext ctx;
		private Conversion conv;

		public ProductOfVisitor(final ParserRuleContext context, final Conversion conversion) {
		    ctx = context;
		    conv = conversion;
		}

		@Override
		public Purpose getPurpose() {
		    return Purpose.PRODUCT;
		}

		@Override
		public Object apply(final Object value) {
		    switch (conv) {
			case FRACTION:
			    BigFraction frac = convertToFraction(value, ctx);
			    productFrac = productFrac.multiply(frac);
			    return productFrac;
			case COMPLEX:
			    ComplexNumber cmplx = ComplexNumber.valueOf(value);
			    productCmplx = productCmplx.multiply(cmplx, settings.mc);
			    return productCmplx;
			case QUATERNION:
			    Quaternion quat = Quaternion.valueOf(value);
			    productQuat = productQuat.multiply(quat, settings.mc);
			    return productQuat;
			default:
			    BigDecimal dec = convertToDecimal(value, settings.mc, ctx);
			    product = product.multiply(dec, settings.mc);
			    return product;
		    }
		}

		@Override
		public Object finalValue(final Number start, final Number stop, final Number step) {
		    // Product of arithmetic progression:
		    // product over k=0..n-1 of (a1 + k*d) => d**n * ( gamma(a1 / d + n) / gamma(a1 / d) )
		    // where gamma(n) for real n = (n - 1)!

		    Number len = IterationVisitor.length(start, stop, step);

		    if (start instanceof Integer) {
			// First special case: 0 is contained in the sequence
			if (IterationVisitor.containedIn(CalcObjectVisitor.this, 0, start, stop, step, ctx))
			    return 0;

			int iStart = (Integer) start;
			int iStop  = (Integer) stop;
			int iStep  = (Integer) step;
			int iLen   = (Integer) len;

			// Second special case: 1..n,1 = n!
			if (iStart == 1 && iStep == 1)
			    return MathUtil.factorial(stop, settings.mc);

			// Third case: m..n,1 = n! / (m-1)!
			if (iStep == 1)
			    return MathUtil.factorial(stop, settings.mc).divide(MathUtil.factorial(iStart - 1, settings.mc));
		    }

		    // Otherwise this is too complicated for the effort, so just punt to
		    // stepping through all the values.
		    throw new IllegalArgumentException();
		}
	}

	@Override
	public Object visitProductOfExpr(CalcParser.ProductOfExprContext ctx) {
	    ProductOfVisitor productVisitor;
	    Conversion conv;
	    Object product = null;

	    CalcParser.DotRangeContext dotRange = ctx.dotRange();
	    if (dotRange == null) {
		List<CalcParser.ExprContext> exprs = ctx.exprN().exprList().expr();
		conv = Conversion.fromValue(getFirstValue(ctx, evaluate(exprs.get(0)), false));
		if (settings.rationalMode)
		    conv = Conversion.FRACTION;
		else if (conv == Conversion.STRING || conv == Conversion.INTEGER)
		    conv = Conversion.DECIMAL;

		List<Object> objects = buildValueList(this, exprs, conv);
		productVisitor = new ProductOfVisitor(ctx, conv);

		for (Object obj : objects) {
		    product = productVisitor.apply(obj);
		}
	    }
	    else {
		conv = settings.rationalMode ? Conversion.FRACTION : Conversion.DECIMAL;
		productVisitor = new ProductOfVisitor(ctx, conv);

		product = iterateOverDotRange(null, dotRange.expr(), dotRange.DOTS() != null, productVisitor, false, false);
	    }

	    return product;
	}

	private class ArrayOfVisitor implements IterationVisitor
	{
		private ArrayScope<Object> array;

		public ArrayOfVisitor(ArrayScope<Object> arr) {
		    array = arr;
		}

		@Override
		public Object apply(Object obj) {
		    array.add(obj);
		    return obj;
		}
	}

	@Override
	public Object visitArrayOfExpr(CalcParser.ArrayOfExprContext ctx) {
	    ArrayScope<Object> array = new ArrayScope<>();
	    ArrayOfVisitor arrayVisitor = new ArrayOfVisitor(array);

	    CalcParser.DotRangeContext dotRange = ctx.dotRange();
	    if (dotRange == null) {
		List<CalcParser.ExprContext> exprs = ctx.exprN().exprList().expr();

		List<Object> objects = buildValueList(this, exprs, Conversion.UNCHANGED);

		for (Object obj : objects) {
		    arrayVisitor.apply(obj);
		}
	    }
	    else {
		iterateOverDotRange(null, dotRange.expr(), dotRange.DOTS() != null, arrayVisitor, true, false);
	    }

	    return array;
	}

	@Override
	public Object visitFactorialExpr(CalcParser.FactorialExprContext ctx) {
	    BigDecimal value = getDecimalValue(ctx.expr());

	    return MathUtil.factorial(value, settings.mc);
	}

	@Override
	public Object visitShiftExpr(CalcParser.ShiftExprContext ctx) {
	    BigInteger e1 = getIntegerValue(ctx.expr(0));
	    int e2        = getIntValue(ctx.expr(1));
	    String op     = ctx.SHIFT_OP().getText();

	    return shiftOp(e1, e2, op, ctx);
	}

	@Override
	public Object visitSpaceshipExpr(CalcParser.SpaceshipExprContext ctx) {
	    int ret = compareValues(ctx.expr(0), ctx.expr(1), false, true, false);

	    if (ret < 0)
		return settings.rationalMode ? BigFraction.MINUS_ONE : I_MINUS_ONE;
	    else if (ret == 0)
		return settings.rationalMode ? BigFraction.ZERO : BigInteger.ZERO;
	    else
		return settings.rationalMode ? BigFraction.ONE : BigInteger.ONE;
	}

	/**
	 * The compare operators.
	 *
	 * @param expr1   The first expression context.
	 * @param expr2   The second expression context.
	 * @param optObj1 For "case", the already-evaluated case expression value,
	 *                otherwise {@code null} to indicate we need to evaluate {@code expr1}.
	 * @param op      The textual representation of the operator to execute.
	 * @return        Result of the comparison.
	 */
	private Boolean compareOp(
		CalcParser.ExprContext expr1,
		CalcParser.ExprContext expr2,
		Optional<Object> optObj1,
		String op)
	{
	    int cmp;
	    boolean result;

	    switch (op) {
		case "===":
		case "\u2A76":
		case "\u2261": // IDENTICAL TO
		case "!==":
		case "\u2262": // NOT IDENTICAL
		    if (optObj1 != null)
			cmp = CalcUtil.compareValues(this, expr1, expr2, optObj1.orElse(null), visit(expr2),
				settings.mc, true, true, false, false, true);
		    else
			cmp = compareValues(expr1, expr2, true, true, true);
		    break;

		case "==":
		case "\u2A75":
		case "!=":
		case "<>":
		case "\u2260": // NOT EQUAL
		    if (optObj1 != null)
			cmp = CalcUtil.compareValues(this, expr1, expr2, optObj1.orElse(null), visit(expr2),
				settings.mc, false, true, false, false, true);
		    else
			cmp = compareValues(expr1, expr2, false, true, true);
		    break;

		default:
		    if (optObj1 != null)
			cmp = CalcUtil.compareValues(this, expr1, expr2, optObj1.orElse(null), visit(expr2),
				settings.mc, false, false, false, false, false);
		    else
			cmp = compareValues(expr1, expr2);
		    break;
	    }

	    switch (op) {
		case "<=":
		case "\u2264":
		    result = (cmp <= 0);
		    break;

		case "<":
		    result = (cmp < 0);
		    break;

		case ">=":
		case "\u2265":
		    result = (cmp >= 0);
		    break;

		case ">":
		    result = (cmp > 0);
		    break;

		case "===":
		case "\u2A76":
		case "\u2261": // IDENTICAL TO
		case "==":
		case "\u2A75":
		    result = (cmp == 0);
		    break;

		case "!==":
		case "\u2262": // NOT IDENTICAL
		case "!=":
		case "<>":
		case "\u2260": // NOT EQUAL
		    result = (cmp != 0);
		    break;

		default:
		    // Note: we will never get here because of the grammar,
		    // so the context for this error is unimportant.
		    throw new UnknownOpException(op, expr2);
	    }

	    return Boolean.valueOf(result);
	}

	@Override
	public Object visitCompareExpr(CalcParser.CompareExprContext ctx) {
	    CalcParser.ExprContext expr1 = ctx.expr(0);
	    CalcParser.ExprContext expr2 = ctx.expr(1);
	    String op = ctx.COMPARE_OP().getText();

	    return compareOp(expr1, expr2, null, op);
	}

	private class InVisitor implements IterationVisitor
	{
		private CalcParser.ExprContext valueCtx;
		private CalcParser.LoopCtlContext loopCtx;
		private Object inValue;
		private Boolean compared = Boolean.FALSE;


		InVisitor(CalcParser.ExprContext ctx1, CalcParser.LoopCtlContext ctx2, Object value) {
		    valueCtx = ctx1;
		    loopCtx  = ctx2;
		    inValue  = value;
		}

		@Override
		public Purpose getPurpose() {
		    return Purpose.SELECT;
		}

		@Override
		public Object apply(final Object value) {
		    int cmp = CalcUtil.compareValues(
			CalcObjectVisitor.this,
			valueCtx, loopCtx,
			inValue, value,
			settings.mc,
			false, true, false, false, true);

		    if (cmp == 0)
			compared = Boolean.TRUE;

		    return compared;
		}

		@Override
		public Object finalValue(Number start, Number stop, Number step) {
		    return IterationVisitor.containedIn(CalcObjectVisitor.this, inValue, start, stop, step, valueCtx);
		}
	}

	@Override
	public Object visitInExpr(CalcParser.InExprContext ctx) {
	    CalcParser.ExprContext expr         = ctx.expr();
	    CalcParser.LoopCtlContext ctlCtx    = ctx.loopCtl();
	    CalcParser.ExprListContext exprList = ctlCtx.exprList();
	    CalcParser.DotRangeContext dotCtx   = ctlCtx.dotRange();
	    Object value = evaluate(expr);
	    Object retValue;
	    boolean doingWithin = ctx.K_WITHIN() != null;

	    InVisitor visitor = new InVisitor(expr, ctlCtx, value);

	    if (exprList != null)
		retValue = iterateOverDotRange(exprList.expr(), null, false, visitor, true, doingWithin);
	    else
		retValue = iterateOverDotRange(null, dotCtx.expr(), dotCtx.DOTS() != null, visitor, true, doingWithin);

	    return retValue;
	}

	@Override
	public Object visitEqualExpr(CalcParser.EqualExprContext ctx) {
	    CalcParser.ExprContext expr1 = ctx.expr(0);
	    CalcParser.ExprContext expr2 = ctx.expr(1);
	    String op = ctx.EQUAL_OP().getText();

	    return compareOp(expr1, expr2, null, op);
	}

	@Override
	public Object visitBitExpr(CalcParser.BitExprContext ctx) {
	    Object o1 = evaluate(ctx.expr(0));
	    Object o2 = evaluate(ctx.expr(1));

	    String op = ctx.BIT_OP().getText();

	    return bitOp(this, o1, o2, op, ctx, settings.mc);
	}

	@Override
	public Object visitBooleanAndExpr(CalcParser.BooleanAndExprContext ctx) {
	    Boolean b1 = getBooleanValue(ctx.expr(0));

	    // Due to the short-circuit nature of this operator, the second expression
	    // is only evaluated if necessary
	    if (!b1)
		return Boolean.FALSE;

	    return getBooleanValue(ctx.expr(1));
	}

	@Override
	public Object visitBooleanOrExpr(CalcParser.BooleanOrExprContext ctx) {
	    Boolean b1 = getBooleanValue(ctx.expr(0));

	    // Due to the short-circuit nature of this operator, the second expression
	    // is only evaluated if necessary
	    if (b1)
		return Boolean.TRUE;

	    return getBooleanValue(ctx.expr(1));
	}

	@Override
	public Object visitBooleanXorExpr(CalcParser.BooleanXorExprContext ctx) {
	    Boolean b1 = getBooleanValue(ctx.expr(0));
	    Boolean b2 = getBooleanValue(ctx.expr(1));

	    // Unfortunately, there is no possibility of short-circuit evaluation
	    // for this operator -- either first value could produce either result
	    return ((b1 && b2) || (!b1 && !b2)) ? Boolean.FALSE : Boolean.TRUE;
	}

	@Override
	public Object visitElvisExpr(CalcParser.ElvisExprContext ctx) {
	    CalcParser.ExprContext expr0 = ctx.expr(0);
	    CalcParser.ExprContext expr1 = ctx.expr(1);
	    String op = ctx.ELVIS_OP().getText();

	    Object v0 = evaluate(expr0);
	    switch (op) {
		case "?:":
		    if (toBooleanValue(this, v0, expr0)) {
			return v0;
		    }
		    break;
		case "?!":
		    if (!toBooleanValue(this, v0, expr0)) {
			return v0;
		    }
		    break;
	    }
	    return evaluate(expr1);
	}

	@Override
	public Object visitStringValue(CalcParser.StringValueContext ctx) {
	    return getRawString(ctx.STRING().getText());
	}

	@Override
	public Object visitIStringValue(CalcParser.IStringValueContext ctx) {
	    return getIStringValue(this, ctx.ISTRING(), ctx);
	}

	@Override
	public Object visitNumberValue(CalcParser.NumberValueContext ctx) {
	    return stringToValue(ctx.NUMBER().getText());
	}

	@Override
	public Object visitNumberConstValue(CalcParser.NumberConstValueContext ctx) {
	    String constant = ctx.NUM_CONST().getText();
	    return NumericUtil.convertDingbatNumber(constant.codePointAt(0));
	}

	@Override
	public Object visitBinaryValue(CalcParser.BinaryValueContext ctx) {
	    String value = ctx.BIN_CONST().getText();
	    return new BigInteger(value.substring(2), 2);
	}

	@Override
	public Object visitOctalValue(CalcParser.OctalValueContext ctx) {
	    String value = ctx.OCT_CONST().getText();
	    return new BigInteger(value.substring(1), 8);
	}

	@Override
	public Object visitHexValue(CalcParser.HexValueContext ctx) {
	    String value = ctx.HEX_CONST().getText();
	    return new BigInteger(value.substring(2), 16);
	}

	@Override
	public Object visitKbValue(CalcParser.KbValueContext ctx) {
	    String value = ctx.KB_CONST().getText();
	    return NumericUtil.convertKMGValue(value);
	}

	@Override
	public Object visitFracValue(CalcParser.FracValueContext ctx) {
	    String value = ctx.FRAC_CONST().getText();
	    BigFraction fraction;

	    if (value.startsWith("f'") || value.startsWith("F'")) {
		value = CharUtil.stripQuotes(value.substring(1));
		fraction = BigFraction.valueOf(value);
	    }
	    else {
		fraction = BigFraction.valueOf(value);
	    }

	    if (settings.rationalMode)
		return fraction;
	    else
		return fraction.toDecimal(settings.mc);
	}

	@Override
	public Object visitRomanValue(CalcParser.RomanValueContext ctx) {
	    String constant = ctx.ROMAN_CONST().getText();

	    // Strip the quotes before conversion
	    String value = CharUtil.stripQuotes(constant.substring(1));
	    try {
		return NumericUtil.convertFromRoman(value);
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, ctx);
	    }
	}

	@Override
	public Object visitTimeValue(CalcParser.TimeValueContext ctx) {
	    String constant = ctx.TIME_CONST().getText();

	    // Strip the quotes before conversion
	    String value = CharUtil.stripQuotes(constant.substring(1));
	    try {
		switch (constant.charAt(0)) {
		    case 't':
		    case 'T':
			// This gives us nanoseconds of duration
			return NumericUtil.convertFromDuration(value);
		    case 'h':
		    case 'H':
			// This gives us nanoseconds since midnight
			return BigInteger.valueOf(NumericUtil.convertFromTime(value));
		    default:
			return null;
		}
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, ctx);
	    }
	}

	@Override
	public Object visitDateValue(CalcParser.DateValueContext ctx) {
	    String constant   = ctx.DATE_CONST().getText();
	    String value      = CharUtil.stripQuotes(constant.substring(1));

	    StringBuilder buf = new StringBuilder();
	    boolean negate    = false;
	    boolean isoDate   = constant.charAt(0) == 'd';
	    boolean shortYear = false;

	    int ixSep1, ixSep2, sepWidth = 0;
	    String yearStr, monthStr, dayStr;
	    int year, month, day;;
	    long epochDate;

	    try {
		// Special case of US date with minus sign before 4-digit year
		int slot = value.length() - 5;
		if (!isoDate && value.charAt(slot) == '-' && !Character.isDigit(value.charAt(slot - 1))) {
		    negate = true;
		    value = value.substring(0, slot) + value.substring(slot + 1);
		}

		// Note: this regex should be the same as DTSEP in Calc.g4
		buf.append(value.replaceAll("[\\-/,;\\._]", "-"));

		if (buf.charAt(0) == '-') {
		    negate = true;
		    buf.deleteCharAt(0);
		}

		if (buf.indexOf("-") < 0) {
		    shortYear = (buf.length() == 6);
		    ixSep1 = isoDate ? (shortYear ? 2 : 4) : 2;
		    ixSep2 = ixSep1 + 2;
		}
		else {
		    sepWidth = 1;
		    ixSep1 = buf.indexOf("-");
		    ixSep2 = buf.indexOf("-", ixSep1 + 1);
		    shortYear = isoDate ? (ixSep1 < 3) : (buf.length() - ixSep2 < 4);
		}

		if (isoDate) {
		    yearStr = buf.substring(0, ixSep1);
		    monthStr = buf.substring(ixSep1 + sepWidth, ixSep2);
		    dayStr = buf.substring(ixSep2 + sepWidth);
		}
		else {
		    monthStr = buf.substring(0, ixSep1);
		    dayStr = buf.substring(ixSep1 + sepWidth, ixSep2);
		    yearStr = buf.substring(ixSep2 + sepWidth);
		}

		year = Integer.parseInt(yearStr);
		month = Integer.parseInt(monthStr);
		day = Integer.parseInt(dayStr);

		if (shortYear) {
		    int cutoverYear = (LocalDate.now().getYear() % 100) + 30;
		    if (year < cutoverYear)
			year += 2000;
		    else
			year += 1900;
		}

		return DateUtil.date(month, day, negate ? -year : year);
	    }
	    catch (DateTimeParseException | NumberFormatException ex) {
		throw new CalcExprException(ex, ctx);
	    }
	}

	@Override
	public Object visitEmptyObjValue(CalcParser.EmptyObjValueContext ctx) {
	    return CollectionScope.EMPTY;
	}

	@Override
	public Object visitEitherOrExpr(CalcParser.EitherOrExprContext ctx) {
	    boolean ifExpr = getBooleanValue(ctx.expr(0));

	    return evaluate(ifExpr ? ctx.expr(1) : ctx.expr(2));
	}

	@Override
	public Object visitAddAssignExpr(CalcParser.AddAssignExprContext ctx) {
	    CalcParser.VarContext varCtx = ctx.var();
	    ParserRuleContext exprCtx    = ctx.expr();

	    LValueContext lValue = getLValue(varCtx);
	    Object result;

	    String op = ctx.ADD_ASSIGN().getText();
	    Object e1 = lValue.getContextObject(this);
	    Object e2 = visit(exprCtx);

	    switch (op) {
		case "+=":
		case "\u2795=":
		    result = addOp(this, e1, e2, varCtx, exprCtx, settings.mc, settings.rationalMode, settings.sortKeys);
		    break;
		case "-=":
		case "\u2212=":
		case "\u2796=":
		    if (settings.rationalMode || e1 instanceof BigFraction || e2 instanceof BigFraction) {
			BigFraction f1 = convertToFraction(e1, varCtx);
			BigFraction f2 = convertToFraction(e2, exprCtx);

			result = f1.subtract(f2);
		    }
		    else if (e1 instanceof Quaternion || e2 instanceof Quaternion) {
			Quaternion q1 = Quaternion.valueOf(e1);
			Quaternion q2 = Quaternion.valueOf(e2);

			result = q1.subtract(q2);
		    }
		    else if (e1 instanceof ComplexNumber || e2 instanceof ComplexNumber) {
			ComplexNumber c1 = ComplexNumber.valueOf(e1);
			ComplexNumber c2 = ComplexNumber.valueOf(e2);

			result = c1.subtract(c2, settings.mc);
		    }
		    else {
			BigDecimal d1 = convertToDecimal(e1, settings.mc, varCtx);
			BigDecimal d2 = convertToDecimal(e2, settings.mc, exprCtx);

			result = fixupToInteger(d1.subtract(d2, settings.mc));
		    }
		    break;
		default:
		    throw new UnknownOpException(op, ctx);
	    }

	    return lValue.putContextObject(this, result);
	}

	@Override
	public Object visitPowerAssignExpr(CalcParser.PowerAssignExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());

	    BigDecimal base = convertToDecimal(lValue.getContextObject(this), settings.mc, ctx);
	    double exp      = getDoubleValue(ctx.expr());

	    return lValue.putContextObject(this, MathUtil.pow(base, exp, settings.mc));
	}

	@Override
	public Object visitMultAssignExpr(CalcParser.MultAssignExprContext ctx) {
	    CalcParser.VarContext varCtx = ctx.var();
	    ParserRuleContext exprCtx    = ctx.expr();

	    LValueContext lValue = getLValue(varCtx);
	    Object result;

	    String op = ctx.MULT_ASSIGN().getText();
	    Object e1 = lValue.getContextObject(this);
	    Object e2 = evaluate(exprCtx);

	    try {
		if (settings.rationalMode || e1 instanceof BigFraction || e2 instanceof BigFraction) {
		    BigFraction f1 = convertToFraction(e1, varCtx);
		    BigFraction f2 = convertToFraction(e2, exprCtx);

		    switch (op) {
			case "*=":
			case "\u00D7=":
			case "\u2217=":
			case "\u2715=":
			case "\u2716=":
			    result = f1.multiply(f2);
			    break;
			case "/=":
			case "\u00F7=":
			case "\u2215=":
			case "\u2797=":
			case "\\=":
			case "\u2216=":
			    result = f1.divide(f2);
			    break;
			case "%=":
			    result = f1.remainder(f2);
			    break;
			default:
			    throw new UnknownOpException(op, ctx);
		    }
		}
		else if (e1 instanceof ComplexNumber || e2 instanceof ComplexNumber) {
		    ComplexNumber c1 = ComplexNumber.valueOf(e1);
		    ComplexNumber c2 = ComplexNumber.valueOf(e2);

		    switch (op) {
			case "*=":
			case "\u00D7=":
			case "\u2217=":
			case "\u2715=":
			case "\u2716=":
			    result = c1.multiply(c2, settings.mc);
			case "/=":
			case "\u00F7=":
			case "\u2215=":
			case "\u2797=":
			    result = c1.divide(c2, MathUtil.divideContext(c1, settings.mcDivide));
			case "\\=":
			case "\u2216=":
			case "%=":
			default:
			    throw new UnknownOpException(op, ctx);
		    }
		}
		else {
		    BigDecimal d1 = convertToDecimal(e1, settings.mc, varCtx);
		    BigDecimal d2 = convertToDecimal(e2, settings.mc, exprCtx);

		    MathContext mcDivide = MathUtil.divideContext(d1, settings.mcDivide);

		    switch (op) {
			case "*=":
			case "\u00D7=":
			case "\u2217=":
			case "\u2715=":
			case "\u2716=":
			    result = fixupToInteger(d1.multiply(d2, settings.mc));
			    break;
			case "/=":
			case "\u00F7=":
			case "\u2215=":
			case "\u2797=":
			    result = fixupToInteger(d1.divide(d2, mcDivide));
			    break;
			case "\\=":
			case "\u2216=":
			    result = fixupToInteger(d1.divideToIntegralValue(d2, mcDivide));
			    break;
			case "%=":
			    result = fixupToInteger(d1.remainder(d2, mcDivide));
			    break;
			default:
			    throw new UnknownOpException(op, ctx);
		    }
		}
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx);
	    }

	    return lValue.putContextObject(this, result);
	}

	@Override
	public Object visitBitAssignExpr(CalcParser.BitAssignExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());

	    Object o1 = lValue.getContextObject(this);
	    Object o2 = evaluate(ctx.expr());

	    String op = ctx.BIT_ASSIGN().getText();
	    // Strip off the trailing '=' of the operator
	    op = op.substring(0, op.length() - 1);

	    return lValue.putContextObject(this, bitOp(this, o1, o2, op, ctx, settings.mc));
	}

	@Override
	public Object visitShiftAssignExpr(CalcParser.ShiftAssignExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());

	    BigInteger i1 = convertToInteger(lValue.getContextObject(this), settings.mc, ctx);
	    int e2        = getIntValue(ctx.expr());

	    String op = ctx.SHIFT_ASSIGN().getText();
	    // Strip off the trailing "="
	    op = op.substring(0, op.length() - 1);

	    return lValue.putContextObject(this, shiftOp(i1, e2, op, ctx));
	}

	@Override
	public Object visitAssignExpr(CalcParser.AssignExprContext ctx) {
	    List<CalcParser.VarContext> vars = ctx.var();
	    List<CalcParser.ExprContext> exprs = ctx.expr();

	    if (vars.size() != exprs.size())
		throw new CalcExprException(ctx, "%calc#mismatchVarExpr", exprs.size(), vars.size());

	    // In order to make a swap work correctly, we must evaluate all the expressions before
	    // doing any assignments back
	    List<Object> valueList = new ArrayList<>(exprs.size());
	    for (int i = 0; i < exprs.size(); i++) {
		valueList.add(evaluate(exprs.get(i)));
	    }

	    Object value = null;

	    for (int i = 0; i < vars.size(); i++) {
		LValueContext lValue = getLValue(vars.get(i));
		value = valueList.get(i);
		lValue.putContextObject(this, value);
	    }

	    return value;	// this is the last value we dealt with...
	}
}
