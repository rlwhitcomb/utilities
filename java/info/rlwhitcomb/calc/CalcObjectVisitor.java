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
 *	    Cache e value when precision changes.
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
 *	    #234: Convert integer loop veriables to BigInteger so that "===" works inside loops against the index var.
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
 */
package info.rlwhitcomb.calc;

import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.iharder.b64.Base64;

import de.onyxbits.SemanticVersion;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import static info.rlwhitcomb.calc.CalcUtil.*;
import info.rlwhitcomb.directory.Match;
import info.rlwhitcomb.math.BigFraction;
import info.rlwhitcomb.math.ComplexNumber;
import info.rlwhitcomb.math.MathUtil;
import info.rlwhitcomb.math.NumericUtil;
import static info.rlwhitcomb.math.NumericUtil.RangeMode;
import info.rlwhitcomb.util.CharUtil;
import static info.rlwhitcomb.util.CharUtil.Justification.*;
import info.rlwhitcomb.util.ClassUtil;
import static info.rlwhitcomb.util.ConsoleColor.Code.*;
import static info.rlwhitcomb.util.Constants.*;
import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.Exceptions;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.RunCommand;
import info.rlwhitcomb.util.Which;


/**
 * Visit each node of the parse tree and do the appropriate calculations at each level.
 * <p> Separate from the grammar, which at this point is completely language-agnostic.
 */
public class CalcObjectVisitor extends CalcBaseVisitor<Object>
{
	/** Version identifier for library (saved) files; compatible with <code>LIB_VERSION</code> in Calc. */
	private static final String LIB_FORMAT = "//** Version: %1$s Base: %2$s";

	/** Pattern for format specifiers. */
	private static final Pattern FORMAT_PATTERN = Pattern.compile("\\s*@([\\-+])?([0-9]+)?([\\.]([0-9]+))?([a-zA-Z,_])?([a-zA-Z%$])");


	/**
	 * Conversion mode for "buildValueList".
	 */
	public static enum Conversion
	{
		/** Convert all values to strings (for "exec"). */
		STRING,
		/** Convert all values to decimal (for "sumof" or "productof" in decimal mode). */
		DECIMAL,
		/** Convert to fractions (rational mode). */
		FRACTION,
		/** Convert to complex numbers. */
		COMPLEX,
		/** Leave values as they are (for "sort"). */
		UNCHANGED
	}


	/** Scale for double operations. */
	private static final MathContext MC_DOUBLE = MathContext.DECIMAL64;

	/**
	 * MathContext to use for pi/e calculations when regular context is unlimited.
	 * Note: precision is arbitrary, but {@link MathUtil#pi} is limited to ~12,500 digits.
	 * Note also that this precision is used for division operations in unlimited mode,
	 * where often an exception would be thrown due to infinite repeating digits.
	 */
	private static final MathContext MC_MAX_DIGITS = new MathContext(12000);

	/** A BigInteger <code>-1</code> value (for repeated use here). */
	private static final BigInteger I_MINUS_ONE = BigInteger.ONE.negate();

	/** Whether we are running on Windows or not. */
	private static final boolean RUNNING_ON_WINDOWS = Environment.isWindows();


	/** Initialization flag -- delays print until constructor is finished.  */
	private boolean initialized = false;

	/**
	 * Flag set during {@link FunctionScope#setParameterValue} so that 0-arg functions
	 * passed as parameters without parens don't get erroneously called prematurely.
	 */
	private boolean doNotCallZeroArg = false;

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
	private CalcPiWorker piWorker = null;

	/**
	 * Our provider of random values.
	 */
	private Random random = null;

	/** Stack of previous "timing" mode values. */
	private final Deque<Boolean> timingModeStack = new ArrayDeque<>();

	/** Stack of previous "debug" mode values. */
	private final Deque<Boolean> debugModeStack = new ArrayDeque<>();

	/** Stack of previous "rational" mode values. */
	private final Deque<Boolean> rationalModeStack = new ArrayDeque<>();

	/** Stack of previous "separator" mode values. */
	private final Deque<Boolean> separatorModeStack = new ArrayDeque<>();

	/** Stack of previous "ignore case" mode values. */
	private final Deque<Boolean> ignoreCaseModeStack = new ArrayDeque<>();

	/** Stack of previous "quote strings" mode values. */
	private final Deque<Boolean> quoteStringsModeStack = new ArrayDeque<>();

	/** Stack of previous "resultsOnly" mode values. */
	private final Deque<Boolean> resultsOnlyModeStack = new ArrayDeque<>();

	/** Stack of previous "quiet" mode values. */
	private final Deque<Boolean> quietModeStack = new ArrayDeque<>();

	/** Stack of previous "silence" mode values. */
	private final Deque<Boolean> silenceModeStack = new ArrayDeque<>();


	/**
	 * Access the currently active symbol table, whether the globals,
	 * or the current function, or statement block.
	 *
	 * @return The {@link #currentScope}.
	 */
	public NestedScope getVariables() {
	    return currentScope;
	}

	public void pushScope(final NestedScope newScope) {
	    newScope.setEnclosingScope(currentScope);
	    currentScope = newScope;
	    currentContext = new LValueContext(currentScope, settings.ignoreNameCase);
	}

	public void popScope() {
	    currentScope = currentScope.getEnclosingScope();
	    currentContext = new LValueContext(currentScope, settings.ignoreNameCase);
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


	public CalcObjectVisitor(
		final CalcDisplayer resultDisplayer,
		final boolean rational,
		final boolean separators,
		final boolean silence,
		final boolean ignoreCase,
		final boolean quotes)
	{
	    displayer = resultDisplayer;
	    settings  = new Settings(rational, separators, silence, ignoreCase, quotes);
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

	public void setDoNotCall(final boolean value) {
	    doNotCallZeroArg = value;
	}

	public MathContext getMathContext() {
	    return settings.mc;
	}

	private void triggerPiCalculation() {
	    if (piWorker == null) {
		piWorker = new CalcPiWorker(settings.mcDivide);
	    }
	    else {
		piWorker.calculate(settings.mcDivide);
	    }
	}

	public int setMathContext(final MathContext newMathContext) {
	    int prec = newMathContext.getPrecision();

	    settings.mc        = newMathContext;
	    settings.precision = prec;

	    // Use a limited precision of our max digits in the case of unlimited precision
	    settings.mcDivide = (prec == 0) ? MC_MAX_DIGITS : newMathContext;

	    // Either create the worker object, or trigger a recalculation
	    triggerPiCalculation();

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

	public boolean setRationalMode(final Object mode) {
	    boolean oldMode = settings.rationalMode;

	    settings.rationalMode = CharUtil.getBooleanValue(mode);
	    piWorker.setRational(settings.rationalMode);

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
	 * Setup a {@link FunctionScope} given a {@link FunctionDeclaration} and the
	 * list of actual parameter values.
	 *
	 * @param ctx   The function call context.
	 * @param decl  The function declaration.
	 * @param exprs The actual parameter value list.
	 * @return      The function scope with the actual values set in the scope.
	 */
	FunctionScope setupFunctionCall(final ParserRuleContext ctx, final FunctionDeclaration decl, final List<CalcParser.OptExprContext> exprs) {
	    FunctionScope funcScope = new FunctionScope(decl);
	    int numParams = decl.getNumberOfParameters();
	    int numActuals = exprs != null ? exprs.size() : 0;

	    if (exprs != null) {
		// Special case: 0 or variable # params, but one actual, except the actual expr is zero -> zero actuals
		if (numParams <= 0 && numActuals == 1 && exprs.get(0).expr() == null)
		    numActuals--;

		if (numParams >= 0 && numActuals > numParams) {
		    if (numParams == 1)
			throw new CalcExprException(ctx, "%calc#tooManyForOneValue", numActuals);
		    else
			throw new CalcExprException(ctx, "%calc#tooManyForValues", numActuals, numParams);
		}

		for (int index = 0; index < numActuals; index++) {
		    funcScope.setParameterValue(this, index, exprs.get(index).expr());
		}
	    }

	    // In case there were fewer actuals passed than declared, explicitly set the remaining values
	    // to null so that their parameter names are present in the symbol table
	    for (int index = numActuals; index < numParams; index++) {
		funcScope.setParameterValue(this, index, null);
	    }

	    return funcScope;
	}

	/**
	 * Evaluate a function by calling {@link #evaluateFunction(ParserRuleContext, Object)}
	 * by calling {@code visit(ctx)} to get the value.
	 *
	 * @param ctx The parsing context to visit to get the value.
	 * @return Either the value or the result of visiting that function context if it is one.
	 */
	Object evaluateFunction(final ParserRuleContext ctx) {
	    return evaluateFunction(ctx, visit(ctx));
	}

	/**
	 * Evaluate a function: basically call {@code visit} on that context if the value itself
	 * is a function scope (that is, the declaration of a function).
	 * <p> But also, if the value is a {@link FunctionDeclaration} we have a choice:
	 * <ul><li> if the function was declared WITHOUT parameters, then call the function</li>
	 * <li> if the function was defined WITH parameters, then treat the value as a function object
	 * and just return the value</li>
	 * </ul>
	 * <p> BUT, during parameter evaluation, a special flag ({@link #doNotCallZeroArg}) is set
	 * to (obviously) not call zero-arg functions without parens so it is passed still as a
	 * function declaration without calling it.
	 * <p> Also, if the function return (or the initial value, for that matter) is a {@link ValueScope}
	 * then call its {@code getValue()} function to get the real value (which, for the moment at least,
	 * will never be another function).
	 *
	 * @param ctx   The parsing context (for error reporting).
	 * @param value The result of an expression, which could be a reference to a function call.
	 * @return Either the value or the result of visiting that function context if it is one.
	 */
	Object evaluateFunction(final ParserRuleContext ctx, final Object value) {
	    Object returnValue = value;

	    if (returnValue instanceof FunctionDeclaration) {
		FunctionDeclaration funcDecl = (FunctionDeclaration) returnValue;
		if (funcDecl.getNumberOfParameters() == 0 && !doNotCallZeroArg) {
		    returnValue = setupFunctionCall(ctx, funcDecl, null);
		}
	    }

	    if (returnValue != null && returnValue instanceof FunctionScope) {
		FunctionScope func = (FunctionScope) returnValue;

		pushQuietMode.accept(true);
		pushScope(func);
		try {
		    returnValue = visit(func.getDeclaration().getFunctionBody());
		}
		catch (LeaveException lex) {
		    if (lex.hasValue()) {
			returnValue = lex.getValue();
		    }
		}
		finally {
		    popScope();
		    pushQuietMode.accept("pop");
		}
	    }

	    if (returnValue instanceof ValueScope) {
		returnValue = ((ValueScope) returnValue).getValue();
	    }

	    return returnValue;
	}

	private LValueContext getLValue(CalcParser.VarContext var) {
	    return LValueContext.getLValue(this, var, currentContext);
	}

	private BigDecimal getDecimalValue(final ParserRuleContext ctx) {
	    return toDecimalValue(this, visit(ctx), settings.mc, ctx);
	}

	private BigFraction getFractionValue(final ParserRuleContext ctx) {
	    return toFractionValue(this, visit(ctx), ctx);
	}

	private BigInteger getIntegerValue(final ParserRuleContext ctx) {
	    return toIntegerValue(this, visit(ctx), settings.mc, ctx);
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

	    Object value = evaluateFunction(ctx);

	    if (!allowNull)
		nullCheck(value, ctx);

	    return value == null ? "" : toStringValue(this, ctx, value, quote, separators);
	}

	public String toNonNullString(final ParserRuleContext ctx, final Object value) {
	    nullCheck(value, ctx);
	    return toStringValue(this, ctx, value, false, settings.separatorMode);
	}

	private double getDoubleValue(final ParserRuleContext ctx) {
	    BigDecimal dec = getDecimalValue(ctx);

	    return dec.doubleValue();
	}

	protected int getIntValue(final ParserRuleContext ctx) {
	    return toIntValue(this, visit(ctx), settings.mc, ctx);
	}

	private BigDecimal getDecimalTrigValue(final ParserRuleContext ctx) {
	    BigDecimal value = getDecimalValue(ctx);

	    if (settings.trigMode == TrigMode.DEGREES)
		value = value.multiply(piWorker.getPiOver180(), settings.mc);

	    return value;
	}

	private double getTrigValue(final ParserRuleContext ctx) {
	    return getDecimalTrigValue(ctx).doubleValue();
	}

	private BigDecimal returnTrigValue(final double value) {
	    BigDecimal radianValue = new BigDecimal(value, MC_DOUBLE);

	    if (settings.trigMode == TrigMode.DEGREES)
		return radianValue.divide(piWorker.getPiOver180(), MC_DOUBLE);

	    return radianValue;
	}

	private BigDecimal returnTrigValue(final BigDecimal value) {
	    BigDecimal radianValue = value;

	    if (settings.trigMode == TrigMode.DEGREES)
		return radianValue.divide(piWorker.getPiOver180(), settings.mcDivide);

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
	    return compareValues(ctx1, ctx2, false, false);
	}

	private int compareValues(final ParserRuleContext ctx1, final ParserRuleContext ctx2, final boolean strict, final boolean allowNulls) {
	    return CalcUtil.compareValues(this, ctx1, ctx2, settings.mc, strict, allowNulls);
	}


	@Override
	public Object visitDecimalDirective(CalcParser.DecimalDirectiveContext ctx) {
	    CalcParser.NumberOptionContext opt = ctx.numberOption();
	    BigDecimal dPrecision;

	    if (opt.NUMBER() != null) {
		dPrecision = new BigDecimal(opt.NUMBER().getText());
	    }
	    else {
		LValueContext lValue = getLValue(opt.var());
		dPrecision = toDecimalValue(this, lValue.getContextObject(this, false), settings.mc, opt);
	    }

	    int precision = 0;

	    try {
		precision = dPrecision.intValueExact();
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ctx, "%calc#precNotInteger", dPrecision);
	    }

	    return BigInteger.valueOf(setPrecision(precision));
	}

	@Override
	public Object visitDoubleDirective(CalcParser.DoubleDirectiveContext ctx) {
	    return setIntMathContext(MathContext.DECIMAL64);
	}

	@Override
	public Object visitFloatDirective(CalcParser.FloatDirectiveContext ctx) {
	    return setIntMathContext(MathContext.DECIMAL32);
	}

	@Override
	public Object visitDefaultDirective(CalcParser.DefaultDirectiveContext ctx) {
	    return setIntMathContext(MathContext.DECIMAL128);
	}

	@Override
	public Object visitUnlimitedDirective(CalcParser.UnlimitedDirectiveContext ctx) {
	    return setIntMathContext(MathContext.UNLIMITED);
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
	    return setTrigMode(TrigMode.DEGREES);
	}

	@Override
	public Object visitRadiansDirective(CalcParser.RadiansDirectiveContext ctx) {
	    return setTrigMode(TrigMode.RADIANS);
	}

	@Override
	public Object visitBinaryDirective(CalcParser.BinaryDirectiveContext ctx) {
	    return setUnits(RangeMode.BINARY);
	}

	@Override
	public Object visitSiDirective(CalcParser.SiDirectiveContext ctx) {
	    return setUnits(RangeMode.DECIMAL);
	}

	@Override
	public Object visitMixedDirective(CalcParser.MixedDirectiveContext ctx) {
	    return setUnits(RangeMode.MIXED);
	}

	private int addName(String name, StringBuilder message) {
	    if (message.length() > 0)
		message.append(", ");

	    int lastNamePos = message.length();
	    message.append("'").append(name).append("'");

	    return lastNamePos;
	}

	/**
	 * Is the value a predefined object, which may or may not include any parameters of this scope.
	 *
	 * @param value	Value of the object, which would need to be some form of {@link PredefinedValue}
	 *		to return true.
	 * @param includesParams If {@code true} then {@link ParameterValue}s are considered "predefined",
	 *		but if {@code false} they are not.
	 * @return	Whether or not to consider this value as predefined.
	 */
	private boolean isPredefined(Object value, boolean includesParams) {
	    boolean isPredefinedType = (value instanceof Scope) && ((Scope) value).isPredefined();

	    return isPredefinedType || (includesParams && value instanceof ParameterValue);
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
		if (value instanceof FunctionDeclaration) {
		    FunctionDeclaration func = (FunctionDeclaration) value;
		    displayer.displayResult(func.getFullFunctionName(), getTreeText(func.getFunctionBody()));
		}
		else {
		    displayer.displayResult(key, toStringValue(this, ctx, value, true, settings.separatorMode));
		}
		return true;
	    }
	    return false;
	}

	@Override
	public Object visitVariablesDirective(CalcParser.VariablesDirectiveContext ctx) {
	    CalcParser.WildIdListContext idList = ctx.wildIdList();
	    List<CalcParser.WildIdContext> ids;
	    Set<String> sortedKeys;
	    int numberDisplayed = 0;
	    boolean listSpecific = false;

	    if (idList == null || (ids = idList.wildId()).isEmpty()) {
		sortedKeys = new TreeSet<>(globals.keySet());
	    }
	    else {
		sortedKeys = new TreeSet<>();
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
	    if (isPredefined(value, false)) {
		PredefinedValue predef = (PredefinedValue) value;
		if (predef.isConstant()) {
		    displayer.displayResult(key, toStringValue(this, ctx, value, true, settings.separatorMode));
		}
		else {
		    displayer.displayResult(key, Intl.formatString("calc#predefVariable",
			    toStringValue(this, ctx, value, true, settings.separatorMode)));
		}
		return true;
	    }
	    return false;
	}

	@Override
	public Object visitPredefinedDirective(CalcParser.PredefinedDirectiveContext ctx) {
	    CalcParser.WildIdListContext idList = ctx.wildIdList();
	    List<CalcParser.WildIdContext> ids;
	    Set<String> sortedKeys;
	    int numberDisplayed = 0;
	    boolean listSpecific = false;

	    if (idList == null || (ids = idList.wildId()).isEmpty()) {
		sortedKeys = new TreeSet<>(globals.keySet());
	    }
	    else {
		sortedKeys = new TreeSet<>();
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
	    String msg = getStringValue(ctx.expr(), true, false, settings.separatorMode);

	    displayer.displayMessage(getRawString(msg));

	    return msg;
	}

	@Override
	public Object visitIncludeDirective(CalcParser.IncludeDirectiveContext ctx) {
	    String paths = getStringValue(ctx.expr(0), false, false, false);
	    Charset charset = getCharsetValue(ctx.expr(1), false);

	    try {
		String contents = Calc.getFileContents(paths, charset);
		return Calc.processString(contents, settings.silent);
	    }
	    catch (IOException ioe) {
		throw new CalcExprException(ctx, "%calc#ioError", Exceptions.toString(ioe));
	    }
	}

	public void saveVariables(final ParserRuleContext ctx, final Path path, final Charset charset)
		throws IOException
	{
	    try (BufferedWriter writer = Files.newBufferedWriter(path, charset == null ? DEFAULT_CHARSET : charset)) {
		// Write out the current version
		SemanticVersion prog_version = Environment.programVersion();
		SemanticVersion prog_base = Environment.implementationVersion();
		writer.write(String.format(LIB_FORMAT, prog_version.toSimpleString(), prog_base.toSimpleString()));
		writer.newLine();
		writer.write(Intl.getString("calc#libVersionDescription"));
		writer.newLine();

		// Note: the keySet returned from ObjectScope is in order of declaration, which is important here, since we
		// must be able to read back the saved file and have the values computed to be the same as they are now.
		for (String key : globals.keySet()) {
		    Object value = globals.getValue(key, settings.ignoreNameCase);
		    if (!isPredefined(value, true)) {
			if (value instanceof FunctionDeclaration) {
			    FunctionDeclaration func = (FunctionDeclaration) value;
			    writer.write(String.format("def %1$s = %2$s", func.getFullFunctionName(), getTreeText(func.getFunctionBody())));
			}
			else {
			    writer.write(String.format("%1$s = %2$s", key, toStringValue(this, ctx, value, true, settings.separatorMode)));
			}
		    }
		    writer.newLine();
		}
	    }
	}

	@Override
	public Object visitSaveDirective(CalcParser.SaveDirectiveContext ctx) {
	    String path = getStringValue(ctx.expr(0), false, false, false);
	    Charset charset = getCharsetValue(ctx.expr(1), true);

	    try {
		saveVariables(ctx, Paths.get(path), charset);
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
	 * @return The previous value of the mode, before the value given on the directive.
	 */
	private Boolean processModeOption(
		final CalcParser.ModeOptionContext ctx,
		final Deque<Boolean> stack,
		final CalcParser.BracketBlockContext bracketBlock,
		final UnaryOperator<Boolean> setOperator)
	{
	    String option;

	    if (ctx.var() != null) {
		CalcParser.VarContext var = ctx.var();
		LValueContext lValue = getLValue(var);
		Object modeObject = evaluateFunction(ctx, lValue.getContextObject(this, false));
		option = toStringValue(this, var, modeObject, false, false);
	    }
	    else {
		option = ctx.getText();
	    }

	    try {
		if (bracketBlock != null) {
		    processModeOption(option, stack, setOperator);

		    visit(bracketBlock);

		    return processModeOption("pop", stack, setOperator);
		}
		else {
		    return processModeOption(option, stack, setOperator);
		}
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, ctx);
	    }
	}

	private Boolean processModeOption(final Object value, final Deque<Boolean> stack, final UnaryOperator<Boolean> setOperator) {
	    boolean mode = false;
	    boolean push = true;

	    String option = (value instanceof String) ? (String) value : value.toString();

	    switch (option.toLowerCase()) {
		case "true":
		case "on":
		case "yes":
		    mode = true;
		    break;
		case "false":
		case "off":
		case "no":
		    mode = false;
		    break;
		case "pop":
		case "prev":
		case "previous":
		    if (stack.isEmpty())
			mode = false;
		    else
			mode = stack.pop();
		    push = false;
		    break;
		default:
		    throw new Intl.IllegalArgumentException("%calc#modeError", option);
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


	private boolean isEmptyStmt(final ParseTree root) {
	    ParseTree node = root;
	    while (node != null) {
		// Two cases: the "emptyStmt" from the grammar and the EOF token
		if (node instanceof CalcParser.EmptyStmtContext)
		    return true;
		if (node instanceof TerminalNode) {
		    TerminalNode terminal = (TerminalNode) node;
		    if (terminal.getSymbol().getType() == Token.EOF)
			return true;
		}

		if (node instanceof ParserRuleContext)
		    node = ((ParserRuleContext) node).children.get(0);
		else
		    node = null;
	    }
	    return false;
	}

	/**
	 * Given the root of a parse tree (branch), is the leaf node of this branch
	 * a directive (that is, the class name contains "Directive").
	 *
	 * @param root The parse tree to examine.
	 * @return     Whether the leftmost leaf node is a "directive" or not.
	 */
	private boolean isDirective(final ParseTree root) {
	    ParseTree node = root;
	    while (node != null) {
		if (node.getClass().getSimpleName().indexOf("Directive") >= 0)
		    return true;

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
	public Object visitStmt(CalcParser.StmtContext ctx) {
	    return internalVisitStatements(ctx);
	}

	@Override
	public Object visitStmtOrExpr(CalcParser.StmtOrExprContext ctx) {
	    return internalVisitStatements(ctx);
	}

	@Override
	public Object visitFormattedExprs(CalcParser.FormattedExprsContext ctx) {
	    return internalVisitStatements(ctx);
	}

	@Override
	public Object visitExprStmt(CalcParser.ExprStmtContext ctx) {
	    CalcParser.ExprContext expr = ctx.expr();
	    Object result               = evaluateFunction(expr);
	    String resultString         = "";

	    BigInteger iValue = null;
	    BigDecimal dValue = null;

	    int precision = Integer.MIN_VALUE;
	    int scale     = Integer.MIN_VALUE;
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
		    String scaleStr = m.group(4);
		    String modStr   = m.group(5);
		    String formStr  = m.group(6);

		    if (signStr != null)
			signChar = signStr.charAt(0);
		    if (precStr != null)
			precision = Integer.parseInt(precStr);
		    if (scaleStr != null)
			scale = Integer.parseInt(scaleStr);
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
			valueBuf.append("h'");
			iValue = toIntegerValue(this, result, settings.mc, ctx);
			valueBuf.append(NumericUtil.convertToTime(iValue.longValue(), meridianFlag));
			valueBuf.append('\'');
			break;
		    case 'T':
			toUpperCase = true;
			// fall through
		    case 't':
			char durationUnit = modifierChar;
			// Value will be nanoseconds
			valueBuf.append("t'");
			iValue = toIntegerValue(this, result, settings.mc, ctx);
			valueBuf.append(NumericUtil.convertToDuration(iValue, durationUnit, settings.mcDivide, precision));
			valueBuf.append('\'');
			break;

		    case 'U':
		    case 'u':
			toUpperCase = true;
			valueBuf.append(toStringValue(this, ctx, result, settings.quoteStrings, settings.separatorMode));
			break;
		    case 'L':
		    case 'l':
			toLowerCase = true;
			valueBuf.append(toStringValue(this, ctx, result, settings.quoteStrings, settings.separatorMode));
			break;

		    case 'Q':
			valueBuf.append(toStringValue(this, ctx, result, true, settings.separatorMode));
			addQuotes = true;	// double quote the result
			break;

		    case 'q':
			valueBuf.append(toStringValue(this, ctx, result, false, settings.separatorMode));
			break;

		    case 'C':
		    case 'c':
			iValue = toIntegerValue(this, result, settings.mc, ctx);
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
			    String stringValue = (String) result;
			    int count = Character.codePointCount(stringValue, 0, stringValue.length());
			    if (count == 1) {
				int cp = Character.codePointAt(stringValue, 0);
				dValue = new BigDecimal(cp);
			    }
			}

			// This is the default handling for all other cases
			if (dValue == null)
			    dValue = toDecimalValue(this, result, settings.mc, ctx);

			if (precision != Integer.MIN_VALUE) {
			    dValue = MathUtil.round(dValue, precision);
			}
			valueBuf.append(formatWithSeparators(dValue, separators, scale));
			break;

		    case 'I':
		    case 'i':
			ComplexNumber c = null;
			if (result instanceof ComplexNumber) {
			    c = (ComplexNumber) result;
			}
			else {
			    c = ComplexNumber.real(toDecimalValue(this, result, settings.mc, ctx));
			}
			valueBuf.append(c.toLongString(formatChar == 'I'));
			break;

		    case 'P':
		    case 'p':
			ComplexNumber c2 = null;
			if (result instanceof ComplexNumber) {
			    c2 = (ComplexNumber) result;
			}
			else {
			    c2 = ComplexNumber.real(toDecimalValue(this, result, settings.mc, ctx));
			}
			valueBuf.append(c2.toPolarString(formatChar == 'P', settings.mcDivide));
			break;

		    // @E = US format: MM/dd/yyyy
		    // @e = ISO format: yyyy-MM-dd
		    case 'E':
		    case 'e':
			char dateChar = (formatChar == 'E') ? 'D' : 'd';
			valueBuf.append(dateChar).append('\'');
			iValue = toIntegerValue(this, result, settings.mc, ctx);
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
			valueBuf.append(dateStr).append('\'');
			break;

		    case 'f':
			valueBuf.append(toFractionValue(this, result, ctx));
			break;
		    case 'F':
			valueBuf.append(toFractionValue(this, result, ctx).toProperString());
			break;

		    case 'J':
		    case 'j':
			if (signChar != '-')
			    valueBuf.append('\n');
			String indent = "";
			String increment = null;
			if (precision != Integer.MIN_VALUE)
			    indent = CharUtil.padToWidth("", Math.abs(precision));
			if (scale != Integer.MIN_VALUE)
			    increment = CharUtil.padToWidth("", scale);
			valueBuf.append(indent);
			valueBuf.append(toStringValue(this, ctx, result, true, true, true, separators, indent, increment));
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
			    iValue = toIntegerValue(this, result, settings.mc, ctx);
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
			    iValue = toIntegerValue(this, result, settings.mc, ctx);
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
			    iValue = toIntegerValue(this, result, settings.mc, ctx);
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
			iValue = toIntegerValue(this, result, settings.mc, ctx);
			try {
			    int intValue = iValue.intValueExact();
			    valueBuf.append("r'");
			    valueBuf.append(NumericUtil.convertToRoman(intValue, false));
			    valueBuf.append("'");
			}
			catch (ArithmeticException | IllegalArgumentException ex) {
			    throw new CalcExprException(ex, ctx);
			}
			break;

		    case 'K':
			toUpperCase = true;
			// fall through
		    case 'k':
			iValue = toIntegerValue(this, result, settings.mc, ctx);
			try {
			    long lValue = iValue.longValueExact();
			    valueBuf.append(NumericUtil.formatToRange(lValue, settings.units));
			}
			catch (ArithmeticException ae) {
			    throw new CalcExprException(ae, ctx);
			}
			break;

		    case 'W':
			toUpperCase = true;
			// fall through
		    case 'w':
			iValue = toIntegerValue(this, result, settings.mc, ctx);
			try {
			    long lValue = iValue.longValueExact();
			    NumericUtil.convertToWords(lValue, valueBuf);
			}
			catch (IllegalArgumentException iae) {
			    throw new CalcExprException(iae, ctx);
			}
			break;

		    case '%':
			dValue = toDecimalValue(this, result, settings.mc, ctx);
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
			dValue = toDecimalValue(this, result, settings.mc, ctx);
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
			    valueBuf.append(toStringValue(this, ctx, result, false, false, extraSpace, separators, "", null));
			}
			else {
			    String stringValue = toStringValue(this, ctx, result, false, separators);
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
		    resultString = CharUtil.addDoubleQuotes(resultString);

		// Set the "result" for the case of interpolated strings with formats
		result = resultString;
	    }
	    else {
		// For large numbers it takes a significant amount of time just to convert
		// to a string, so don't even try if we don't need to for display
		if (!settings.silent)
		    resultString = toStringValue(this, ctx, result, settings.quoteStrings, separators);
	    }

	    if (!settings.silent) displayer.displayResult(exprString, resultString);

	    return result;

	}

	private class LoopVisitor implements Function<Object, Object>
	{
		private CalcParser.StmtBlockContext block;
		private String localVarName;

		LoopVisitor(final CalcParser.StmtBlockContext blockContext, final String varName) {
		    this.block        = blockContext;
		    this.localVarName = varName;

		    // Predefine the loop variable in the local scope so it will definitely
		    // override a global one
		    currentScope.setValueLocally(localVarName, settings.ignoreNameCase, null);
		}

		@Override
		public Object apply(final Object value) {
		    currentScope.setValue(localVarName, settings.ignoreNameCase, value);
		    return visit(block);
		}

		public void finish() {
		    // Make sure the local loop var gets removed, even on exceptions
		    currentScope.remove(localVarName, settings.ignoreNameCase);
		}
	}

	private Number cleanDecimal(final BigDecimal value) {
	    BigDecimal bd = fixup(value);
	    if (bd.scale() <= 0)
		return bd.toBigIntegerExact();
	    return bd;
	}

	private Object iterateOverDotRange(
		final List<CalcParser.ExprContext> valueExprs,
		final List<CalcParser.ExprContext> dotExprs,
		final boolean hasDots,
		final Function<Object, Object> visitor,
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
			Object obj = evaluateFunction(exprs.get(0));
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

		    if (sign < 0) {
			for (BigDecimal loopIndex = dStart; loopIndex.compareTo(dStop) >= 0; loopIndex = loopIndex.add(dStep)) {
			    lastValue = visitor.apply(cleanDecimal(loopIndex));
			}
		    }
		    else {
			for (BigDecimal loopIndex = dStart; loopIndex.compareTo(dStop) <= 0; loopIndex = loopIndex.add(dStep)) {
			    lastValue = visitor.apply(cleanDecimal(loopIndex));
			}
		    }
		}
	    }
	    else if (iter != null) {
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
		    lastValue = visitor.apply(visit(expr));
		}
	    }

	    return lastValue;
	}

	@Override
	public Object visitLoopStmt(CalcParser.LoopStmtContext ctx) {
	    CalcParser.IdContext id             = ctx.id();
	    CalcParser.LoopCtlContext ctlCtx    = ctx.loopCtl();
	    CalcParser.StmtBlockContext block   = ctx.stmtBlock();
	    CalcParser.ExprListContext exprList = ctlCtx.exprList();
	    CalcParser.DotRangeContext dotCtx   = ctlCtx.dotRange();

	    String localVarName = id != null ? id.getText() : LoopScope.LOOP_VAR;

	    boolean doingWithin = ctx.K_WITHIN() != null;

	    pushScope(new LoopScope());
	    LoopVisitor visitor = new LoopVisitor(block, localVarName);

	    Object value = null;

	    try {
		if (exprList != null)
		    value = iterateOverDotRange(exprList.expr(), null, false, visitor, true, doingWithin);
		else
		    value = iterateOverDotRange(null, dotCtx.expr(), dotCtx.DOTS() != null, visitor, true, doingWithin);
	    }
	    catch (LeaveException lex) {
		if (lex.hasValue()) {
		    value = lex.getValue();
		}
	    }
	    finally {
		visitor.finish();
		popScope();
	    }

	    return value;
	}

	@Override
	public Object visitWhileStmt(CalcParser.WhileStmtContext ctx) {
	    CalcParser.ExprContext exprCtx    = ctx.expr();
	    CalcParser.StmtBlockContext block = ctx.stmtBlock();

	    Object lastValue = null;

	    boolean exprResult = getBooleanValue(exprCtx);

	    pushScope(new WhileScope());
	    try {
		while (exprResult) {
		    lastValue = visit(block);
		    exprResult = getBooleanValue(exprCtx);
		}
	    }
	    catch (LeaveException lex) {
		if (lex.hasValue()) {
		    lastValue = lex.getValue();
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
		    resultValue = visit(thenBlock);
		}
		else if (elseBlock != null) {
		    resultValue = visit(elseBlock);
		}
	    }
	    finally {
		popScope();
	    }

	    return resultValue;
	}

	private class CaseVisitor implements Function<Object, Object>
	{
		private CalcParser.CaseBlockContext blockCtx;
		private CalcParser.ExprContext caseExpr;
		private Object caseValue;
		private NestedScope caseScope;
		private Object blockValue = null;
		private boolean isMatched = false;

		public CaseVisitor(
			final CalcParser.CaseBlockContext block,
			final CalcParser.ExprContext expr,
			final Object value,
			final NestedScope scope) {
		    this.blockCtx = block;
		    this.caseExpr = expr;
		    this.caseValue = value;
		    this.caseScope = scope;
		}

		@Override
		public Object apply(final Object value) {
		    if (!isMatched
		     && CalcUtil.compareValues(CalcObjectVisitor.this, caseExpr, blockCtx, caseValue, value, settings.mc, false, true, false, false) == 0) {
			blockValue = execute();
		    }
		    return blockValue;
		}

		public Object execute() {
		    Object returnValue = null;
		    pushScope(caseScope);
		    try {
			returnValue = visit(blockCtx.stmtBlock());
		    }
		    finally {
			popScope();
			isMatched = true;
		    }
		    return returnValue;
		}

		public boolean matched() {
		    return isMatched;
		}

		public Object lastValue() {
		    return blockValue;
		}
	}

	@Override
	public Object visitCaseStmt(CalcParser.CaseStmtContext ctx) {
	    CalcParser.ExprContext caseExpr = ctx.expr();
	    Object caseValue = evaluateFunction(caseExpr);
	    List<CalcParser.CaseBlockContext> blocks = ctx.caseBlock();
	    CalcParser.CaseBlockContext defaultCtx = null;
	    CaseScope scope = new CaseScope();

	    for (CalcParser.CaseBlockContext cbCtx : blocks) {
		CaseVisitor visitor = new CaseVisitor(cbCtx, caseExpr, caseValue, scope);

		List<CalcParser.CaseSelectorContext> selectors = cbCtx.caseSelector();
		for (CalcParser.CaseSelectorContext select : selectors) {
		    if (select.K_DEFAULT() != null) {
			defaultCtx = cbCtx;
		    }
		    else if (select.DOTS() != null) {
			iterateOverDotRange(null, select.expr(), true, visitor, true, false);
			if (visitor.matched())
			    return visitor.lastValue();
		    }
		    else if (select.K_MATCHES() != null) {
			CalcParser.ExprContext expr = select.expr().get(0);
			String pattern = getStringValue(expr);
			String input = toStringValue(this, caseExpr, caseValue, false, false);
			if (matches(input, pattern))
			    return visitor.execute();
		    }
		    else if (select.compareOp() != null) {
			String op = select.compareOp().getText();
			CalcParser.ExprContext expr = select.expr().get(0);
			if (compareOp(caseExpr, expr, Optional.ofNullable(caseValue), op))
			    return visitor.execute();
		    }
		    else {
			CalcParser.ExprContext expr = select.expr().get(0);
			Object value = evaluateFunction(expr);
			Object returnValue = visitor.apply(value);
			if (visitor.matched())
			    return returnValue;
		    }
		}
	    }

	    if (defaultCtx != null) {
		CaseVisitor visitor = new CaseVisitor(defaultCtx, caseExpr, caseValue, scope);
		return visitor.execute();
	    }

	    return null;
	}

	@Override
	public Object visitLeaveStmt(CalcParser.LeaveStmtContext ctx) {
	    CalcParser.Expr1Context exprCtx = ctx.expr1();
	    if (exprCtx != null) {
		throw new LeaveException(evaluateFunction(exprCtx.expr()));
	    }
	    else {
		throw new LeaveException();
	    }
	}

	@Override
	public Object visitTimeThisStmt(CalcParser.TimeThisStmtContext ctx) {
	    CalcParser.ExprContext descExpr = ctx.expr();
	    if (descExpr != null) {
		Object descObj = evaluateFunction(descExpr);
		if (descObj != null) {
		    String description = descObj.toString();
		    return Environment.timeThis(description, () -> {
			return visit(ctx.stmtBlock());
		    });
		}
	    }
	    return Environment.timeThis( () -> {
		return visit(ctx.stmtBlock());
	    });
	}

	@Override
	public Object visitStmtBlock(CalcParser.StmtBlockContext ctx) {
	    Object returnValue = null;

	    for (CalcParser.StmtOrExprContext child : ctx.stmtOrExpr()) {
		if (child.emptyStmt() == null) {
		    if (isDirective(child))
			visit(child);
		    else
			returnValue = visit(child);
		}
	    }

	    return returnValue;
	}

	@Override
	public Object visitDefineStmt(CalcParser.DefineStmtContext ctx) {
	    String name = ctx.id().getText();

	    // Can't redefine a predefined value
	    Object oldValue = currentScope.getValue(name, settings.ignoreNameCase);
	    if (oldValue != null && isPredefined(oldValue, false)) {
		throw new CalcExprException(ctx, "%calc#noChangeValue", oldValue.toString(), name, "");
	    }

	    CalcParser.StmtBlockContext functionBody       = ctx.stmtBlock();
	    CalcParser.FormalParamListContext formalParams = ctx.formalParamList();

	    String paramString = formalParams == null ? "" : getTreeText(formalParams);
	    List<CalcParser.FormalParamContext> paramVars = formalParams == null ? null : formalParams.formalParam();
	    FunctionDeclaration func = new FunctionDeclaration(name, functionBody);

	    if (formalParams != null) {
		for (CalcParser.FormalParamContext paramVar : formalParams.formalParam()) {
		    String paramName = paramVar.id().getText();
		    func.defineParameter(paramVar, paramName, paramVar.expr());
		}
		if (formalParams.DOTS() != null) {
		    func.defineParameter(formalParams, FunctionDeclaration.VARARG, null);
		}
	    }

	    currentScope.setValue(name, settings.ignoreNameCase, func);

	    displayActionMessage("%calc#definingFunc", func.getFullFunctionName(), getTreeText(functionBody));

	    return functionBody;
	}

	@Override
	public Object visitConstStmt(CalcParser.ConstStmtContext ctx) {
	    String constantName = ctx.id().getText();
	    CalcParser.ExprContext expr = ctx.expr();
	    Object value = evaluateFunction(expr);

	    ConstantValue.define(currentScope, constantName, value);

	    displayActionMessage("%calc#definingConst", constantName, value);

	    return value;
	}

	private void addPairsToObject(CalcParser.ObjContext objCtx, ObjectScope object) {
	    for (CalcParser.PairContext pairCtx : objCtx.pair()) {
		CalcParser.IdContext id = pairCtx.id();
		TerminalNode str  = pairCtx.STRING();
		TerminalNode istr = pairCtx.ISTRING();
		String key =
			(id != null) ? id.getText()
		      : (str != null) ? getStringMemberName(str.getText())
		      : getIStringValue(this, istr, pairCtx);
		Object value = evaluateFunction(pairCtx.expr());
		object.setValue(key, settings.ignoreNameCase, value);
	    }
	}

	@Override
	public Object visitObjExpr(CalcParser.ObjExprContext ctx) {
	    CalcParser.ObjContext objCtx = ctx.obj();
	    ObjectScope obj = new ObjectScope();

	    addPairsToObject(objCtx, obj);

	    return obj;
	}

	@Override
	public Object visitArrExpr(CalcParser.ArrExprContext ctx) {
	   CalcParser.ExprListContext exprList = ctx.arr().exprList();
	   ArrayScope<Object> list = new ArrayScope<>();
	   if (exprList != null) {
		for (CalcParser.ExprContext expr : exprList.expr()) {
		    Object value = evaluateFunction(expr);
		    list.add(value);
		}
	   }
	   return list;
	}

	@Override
	public Object visitComplexValueExpr(CalcParser.ComplexValueExprContext ctx) {
	    CalcParser.ComplexContext complex = ctx.complex();
	    BigDecimal r = getDecimalValue(complex.expr(0));
	    BigDecimal i = getDecimalValue(complex.expr(1));

	    return new ComplexNumber(r, i);
	}

	@Override
	public Object visitVarExpr(CalcParser.VarExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());
	    return evaluateFunction(ctx, lValue.getContextObject(this));
	}

	@Override
	public Object visitPostIncOpExpr(CalcParser.PostIncOpExprContext ctx) {
	    CalcParser.VarContext var = ctx.var();
	    LValueContext lValue = getLValue(var);
	    Object value = evaluateFunction(var, lValue.getContextObject(this));
	    String op = ctx.INC_OP().getText();
	    Object beforeValue;
	    Object afterValue;

	    if (settings.rationalMode) {
		BigFraction fValue = toFractionValue(this, value, var);
		beforeValue = fValue;

		switch (op) {
		    case "++":
		    case "\u2795\u2795":
			afterValue = fValue.add(BigFraction.ONE);
			break;
		    case "--":
		    case "\u2212\u2212":
		    case "\u2796\u2796":
			afterValue = fValue.subtract(BigFraction.ONE);
			break;
		    default:
			throw new UnknownOpException(op, ctx);
		}
	    }
	    else if (value instanceof ComplexNumber) {
		ComplexNumber cValue = (ComplexNumber) value;
		beforeValue = cValue;

		switch (op) {
		    case "++":
		    case "\u2795\u2795":
			afterValue = cValue.add(C_ONE);
			break;
		    case "--":
		    case "\u2212\u2212":
		    case "\u2796\u2796":
			afterValue = cValue.subtract(C_ONE);
			break;
		    default:
			throw new UnknownOpException(op, ctx);
		}
	    }
	    else {
		BigDecimal dValue = toDecimalValue(this, value, settings.mc, var);
		beforeValue = dValue;

		switch (op) {
		    case "++":
		    case "\u2795\u2795":
			afterValue = dValue.add(BigDecimal.ONE);
			break;
		    case "--":
		    case "\u2212\u2212":
		    case "\u2796\u2796":
			afterValue = dValue.subtract(BigDecimal.ONE);
			break;
		    default:
			throw new UnknownOpException(op, ctx);
		}
	    }

	    lValue.putContextObject(this, afterValue);

	    // post operation, return original value
	    return beforeValue;
	}

	@Override
	public Object visitPreIncOpExpr(CalcParser.PreIncOpExprContext ctx) {
	    CalcParser.VarContext var = ctx.var();
	    LValueContext lValue = getLValue(var);
	    Object value = evaluateFunction(var, lValue.getContextObject(this));
	    String op = ctx.INC_OP().getText();
	    Object afterValue;

	    if (settings.rationalMode) {
		BigFraction fValue = toFractionValue(this, value, var);

		switch (op) {
		    case "++":
		    case "\u2795\u2795":
			afterValue = fValue.add(BigFraction.ONE);
			break;
		    case "--":
		    case "\u2212\u2212":
		    case "\u2796\u2796":
			afterValue = fValue.subtract(BigFraction.ONE);
			break;
		    default:
			throw new UnknownOpException(op, ctx);
		}
	    }
	    else if (value instanceof ComplexNumber) {
		ComplexNumber cValue = (ComplexNumber) value;

		switch (op) {
		    case "++":
		    case "\u2795\u2795":
			afterValue = cValue.add(C_ONE);
			break;
		    case "--":
		    case "\u2212\u2212":
		    case "\u2796\u2796":
			afterValue = cValue.subtract(C_ONE);
			break;
		    default:
			throw new UnknownOpException(op, ctx);
		}
	    }
	    else {
		BigDecimal dValue = toDecimalValue(this, value, settings.mc, var);

		switch (op) {
		    case "++":
		    case "\u2795\u2795":
			afterValue = dValue.add(BigDecimal.ONE);
			break;
		    case "--":
		    case "\u2212\u2212":
		    case "\u2796\u2796":
			afterValue = dValue.subtract(BigDecimal.ONE);
			break;
		    default:
			throw new UnknownOpException(op, ctx);
		}
	    }

	    // pre operation, return the modified value
	    return lValue.putContextObject(this, afterValue);
	}

	@Override
	public Object visitNegPosExpr(CalcParser.NegPosExprContext ctx) {
	    ParserRuleContext expr = ctx.expr();
	    Object e = evaluateFunction(expr);

	    String op = ctx.ADD_OP().getText();

	    if (settings.rationalMode) {
		BigFraction f = toFractionValue(this, e, expr);

		switch (op) {
		    case "+":
		    case "\u2795":
			return f;
		    case "-":
		    case "\u2212":
		    case "\u2796":
			return f.negate();
		    default:
			throw new UnknownOpException(op, expr);
		}
	    }
	    else if (e instanceof ComplexNumber) {
		ComplexNumber c = (ComplexNumber) e;

		switch (op) {
		    case "+":
		    case "\u2795":
			return c;
		    case "-":
		    case "\u2212":
		    case "\u2796":
			return c.negate();
		    default:
			throw new UnknownOpException(op, expr);
		}
	    }
	    else {
		BigDecimal d = toDecimalValue(this, e, settings.mc, expr);

		switch (op) {
		    case "+":
		    case "\u2795":
			// Interestingly, this operation can change the value, if the previous
			// value was not to the specified precision.
			return fixup(d.plus(settings.mc));
		    case "-":
		    case "\u2212":
		    case "\u2796":
			return fixup(d.negate());
		    default:
			throw new UnknownOpException(op, expr);
		}
	    }
	}

	@Override
	public Object visitBitNotExpr(CalcParser.BitNotExprContext ctx) {
	    BigInteger iValue = getIntegerValue(ctx.expr());

	    return iValue.not();
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

	    Object value = evaluateFunction(expr);

	    if (value instanceof ComplexNumber) {
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
	    Object value = evaluateFunction(expr);
	    String power = ctx.POWERS().getText();
	    int exp = nToPower(power, ctx);

	    if (settings.rationalMode) {
		BigFraction base = convertToFraction(value, expr);

		return base.pow(exp);
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
	    return visit(ctx.expr());
	}

	@Override
	public Object visitMultiplyExpr(CalcParser.MultiplyExprContext ctx) {
	    CalcParser.ExprContext ctx1 = ctx.expr(0);
	    CalcParser.ExprContext ctx2 = ctx.expr(1);
	    Object e1 = visit(ctx1);
	    Object e2 = visit(ctx2);

	    String op = ctx.MULT_OP().getText();

	    try {
		if (settings.rationalMode) {
		    BigFraction f1 = toFractionValue(this, e1, ctx1);
		    BigFraction f2 = toFractionValue(this, e2, ctx2);

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
			    return f1.divide(f2);
			case "%":
			    return f1.modulus(f2);
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
			    return c1.divide(c2, settings.mcDivide);
			case "\\":
			    throw new UnknownOpException(op, ctx); // ?? what to do here?
			case "%":
			    throw new UnknownOpException(op, ctx); // ?? I don't know the math yet!!
			default:
			    throw new UnknownOpException(op, ctx);
		    }
		}
		else {
		    BigDecimal d1 = toDecimalValue(this, e1, settings.mc, ctx1);
		    BigDecimal d2 = toDecimalValue(this, e2, settings.mc, ctx2);

		    switch (op) {
			case "*":
			case "\u00D7":
			case "\u2217":
			case "\u2715":
			case "\u2716":
			    return fixup(d1.multiply(d2, settings.mc));
			case "/":
			case "\u00F7":
			case "\u2215":
			case "\u2797":
			    return fixup(d1.divide(d2, settings.mcDivide));
			case "\\":
			    return fixup(d1.divideToIntegralValue(d2, settings.mcDivide));
			case "%":
			    return fixup(d1.remainder(d2, settings.mcDivide));
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
	    Object e1 = visit(ctx1);
	    Object e2 = visit(ctx2);

	    String op = ctx.ADD_OP().getText();
	    switch (op) {
		case "+":
		case "\u2795":
		    return addOp(this, e1, e2, ctx1, ctx2, settings.mc, settings.rationalMode);
		case "-":
		case "\u2212":
		case "\u2796":
		    if (settings.rationalMode) {
			BigFraction f1 = toFractionValue(this, e1, ctx1);
			BigFraction f2 = toFractionValue(this, e2, ctx2);

			return f1.subtract(f2);
		    }
		    else if (e1 instanceof ComplexNumber || e2 instanceof ComplexNumber) {
			ComplexNumber c1 = ComplexNumber.valueOf(e1);
			ComplexNumber c2 = ComplexNumber.valueOf(e2);

			return c1.subtract(c2);
		    }
		    else {
			BigDecimal d1 = toDecimalValue(this, e1, settings.mc, ctx1);
			BigDecimal d2 = toDecimalValue(this, e2, settings.mc, ctx2);

			return fixup(d1.subtract(d2, settings.mc));
		    }
		default:
		    throw new UnknownOpException(op, ctx);
	    }
	}

	@Override
	public Object visitAbsExpr(CalcParser.AbsExprContext ctx) {
	    CalcParser.ExprContext expr = ctx.expr1().expr();
	    Object value = evaluateFunction(expr);

	    if (settings.rationalMode) {
		BigFraction f = toFractionValue(this, value, ctx);
		return f.abs();
	    }
	    else if (value instanceof ComplexNumber) {
		ComplexNumber c = (ComplexNumber) value;
		return c.abs(settings.mc);
	    }
	    else {
		BigDecimal e = toDecimalValue(this, value, settings.mc, ctx);
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

	    return returnTrigValue(MathUtil.atan2(y, x, settings.mcDivide));
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
	    Object value = evaluateFunction(expr);

	    if (value instanceof ComplexNumber) {
		ComplexNumber cValue = (ComplexNumber) value;
		return cValue.pow(D_ONE_HALF, settings.mc);
	    }
	    else {
		try {
		    return MathUtil.sqrt(convertToDecimal(value, settings.mc, expr), settings.mc);
		}
		catch (IllegalArgumentException iae) {
		    throw new CalcExprException(iae, ctx);
		}
	    }
	}

	@Override
	public Object visitCbrtExpr(CalcParser.CbrtExprContext ctx) {
	    CalcParser.ExprContext expr = ctx.expr1().expr();
	    Object value = evaluateFunction(expr);

	    if (value instanceof ComplexNumber) {
		ComplexNumber cValue = (ComplexNumber) value;
		return cValue.pow(BigDecimal.ONE.divide(D_THREE, settings.mcDivide), settings.mc);
	    }
	    else {
		return MathUtil.cbrt(convertToDecimal(value, settings.mc, expr), settings.mc);
	    }
	}

	@Override
	public Object visitFortExpr(CalcParser.FortExprContext ctx) {
	    CalcParser.ExprContext expr = ctx.expr1().expr();
	    Object value = evaluateFunction(expr);

	    if (value instanceof ComplexNumber) {
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
		throw new CalcExprException("%util#numeric.outOfRange", ctx);

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
	    if (ctx.expr1() != null) {
		CalcParser.ExprContext expr = ctx.expr1().expr();
		if (expr != null) {
		    Object seed = evaluateFunction(expr);
		    if (seed != null) {
			byte[] bytes = ClassUtil.getBytes(seed);
			BigInteger seedInt = new BigInteger(bytes);
			random = new Random(seedInt.longValue());
		    }
		}
	    }
	    if (random == null) {
		random = new SecureRandom();
	    }
	    int precision = settings.mc.getPrecision();
	    BigInteger randomBits = new BigInteger(precision * 6, random);
	    BigDecimal dValue = new BigDecimal(randomBits, settings.mcDivide);
	    return dValue.scaleByPowerOfTen(dValue.scale() - precision);
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
		Object value = evaluateFunction(expr);
		if (value instanceof ComplexNumber) {
		    ComplexNumber c = (ComplexNumber) value;
		    return c.signum(settings.mcDivide);
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
	    Object obj = evaluateFunction(ctx.expr1().expr());
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
		obj = evaluateFunction(arg.expr());
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
	    obj = evaluateFunction(expr);

	    try {
		return castTo(this, expr, obj, castType, settings.mc, settings.separatorMode);
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, ctx);
	    }
	}

	private class LengthVisitor implements Function<Object, Object>
	{
		private BigInteger count = BigInteger.ZERO;

		@Override
		public Object apply(final Object value) {
		    count = count.add(BigInteger.ONE);
		    return count;
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
		Object obj = evaluateFunction(ctx.expr1().expr());

		// This returns the non-recursive size of objects and arrays
		// so, use "scale" to calculate the recursive (full) size
		return BigInteger.valueOf((long) length(this, obj, ctx, false));
	    }
	}

	@Override
	public Object visitScaleExpr(CalcParser.ScaleExprContext ctx) {
	    Object obj = evaluateFunction(ctx.expr1().expr());

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
	    BigDecimal e = getDecimalValue(ctx.expr1().expr());

	    return MathUtil.ceil(e);
	}

	@Override
	public Object visitFloorExpr(CalcParser.FloorExprContext ctx) {
	    BigDecimal e = getDecimalValue(ctx.expr1().expr());

	    return MathUtil.floor(e);
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
	    CalcParser.Expr2Context e2ctx = ctx.expr2();

	    if (settings.rationalMode) {
		BigFraction f1 = getFractionValue(e2ctx.expr(0));
		BigFraction f2 = getFractionValue(e2ctx.expr(1));

		return f1.gcd(f2);
	    }
	    else {
		BigInteger e1 = getIntegerValue(e2ctx.expr(0));
		BigInteger e2 = getIntegerValue(e2ctx.expr(1));

		return e1.gcd(e2);
	    }
	}

	@Override
	public Object visitLcmExpr(CalcParser.LcmExprContext ctx) {
	    CalcParser.Expr2Context e2ctx = ctx.expr2();

	    try {
		if (settings.rationalMode) {
		    BigFraction f1 = getFractionValue(e2ctx.expr(0));
		    BigFraction f2 = getFractionValue(e2ctx.expr(1));

		    return f1.lcm(f2);
		}
		else {
		    BigInteger e1 = getIntegerValue(e2ctx.expr(0));
		    BigInteger e2 = getIntegerValue(e2ctx.expr(1));

		    // Note: this "lcm" method is a helper function inside BigFraction
		    // that works on and returns a BigInteger value
		    return BigFraction.lcm(e1, e2);
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
	 * @param eCtx	  The expression context we're evaluating (i.e., the first parse tree).
	 * @param obj	  The first object, which could be an object, a list, or an actual value.
	 * @param forJoin Whether or not this is a "join" operation.
	 * @return	  The real first value, descending to the lowest level of a compound object.
	 */
	private Object getFirstValue(final CalcParser.ExprContext eCtx, final Object obj, final boolean forJoin) {
	    Object value = evaluateFunction(eCtx, obj);

	    nullCheck(value, eCtx);

	    if (!forJoin) {
		if (value instanceof ArrayScope) {
		    @SuppressWarnings("unchecked")
		    ArrayScope list = (ArrayScope) value;
		    return list.size() > 0 ? getFirstValue(eCtx, list.getValue(0), forJoin) : null;
		}
		else if (value instanceof ObjectScope) {
		    @SuppressWarnings("unchecked")
		    ObjectScope map = (ObjectScope) value;
		    Iterator<Object> iter = map.values().iterator();
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
	 * @param isString	Whether to build the value list as strings, or numeric (determined by the
	 *			first actual value, or always {@code true} for {@code join}).
	 * @param forJoin	{@code true} for a "join" operation, which will not recurse into lists or maps to begin with.
	 * @see #getFirstValue
	 */
	private void buildFlatMap(
		final ParserRuleContext ctx,
		final Object obj,
		final List<Object> objectList,
		final boolean isString,
		final boolean forJoin)
	{
	    Object value = evaluateFunction(ctx, obj);

	    nullCheck(value, ctx);

	    if (value instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope array = (ArrayScope) value;
		if (forJoin) {
		    objectList.add(array);
		}
		else {
		    for (Object listObj : array.list()) {
			buildFlatMap(ctx, listObj, objectList, isString, forJoin);
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
			buildFlatMap(ctx, mapObj, objectList, isString, forJoin);
		    }
		}
	    }
	    else if (isString) {
		// Note: this logic follows "getStringValue"
		if (value instanceof String)
		    objectList.add(value);
		else
		    objectList.add(value.toString());
	    }
	    else if (settings.rationalMode) {
		objectList.add(toFractionValue(this, value, ctx));
	    }
	    else {
		objectList.add(toDecimalValue(this, value, settings.mc, ctx));
	    }
	}

	/**
	 * Construct the "flat map" or value list for {@code min}, {@code max}, or {@code join}
	 * so that we can traverse a simple list to obtain the desired result.
	 *
	 * @param exprs	  The list of expressions parsed as the arguments to the function.
	 * @param forJoin Whether or not this is a "join" operation.
	 * @return	  The "flat map" of the values from those arguments.
	 */
	private List<Object> buildFlatMap(final List<CalcParser.ExprContext> exprs, final boolean forJoin) {
	    // Do a "peek" inside any lists or maps to get the first value
	    CalcParser.ExprContext firstCtx = exprs.get(0);
	    boolean isString = getFirstValue(firstCtx, visit(firstCtx), forJoin) instanceof String;

	    List<Object> objects = new ArrayList<>();

	    for (CalcParser.ExprContext eCtx : exprs) {
		buildFlatMap(eCtx, evaluateFunction(eCtx), objects, isString, forJoin);
	    }

	    return objects;
	}

	@Override
	public Object visitMaxExpr(CalcParser.MaxExprContext ctx) {
	    List<CalcParser.ExprContext> exprs = ctx.exprN().exprList().expr();
	    List<Object> objects = buildFlatMap(exprs, false);
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
	    List<Object> objects = buildFlatMap(exprs, false);
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
	    List<Object> objects = new ArrayList<>();

	    for (CalcParser.ExprContext eCtx : exprs) {
		buildFlatMap(eCtx, evaluateFunction(eCtx), objects, true, true);
	    }

	    StringBuilder buf = new StringBuilder();
	    int length = objects.size();

	    // This doesn't make sense unless there are at least 3 values
	    // So, one value just gets that value
	    // two values gets the two just concatenated together
	    // three or more, the first N - 1 are joined by the Nth (string) value
	    if (length == 1) {
		Object obj0 = objects.get(0);
		// One map or list => concatenate all objects in it
		if (obj0 instanceof ArrayScope || obj0 instanceof ObjectScope) {
		    objects.clear();
		    buildFlatMap(ctx, obj0, objects, true, false);
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
		if (obj0 instanceof ArrayScope || obj0 instanceof ObjectScope) {
		    // One map or list, plus a join expression
		    objects.clear();
		    buildFlatMap(ctx, obj0, objects, true, false);
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
			buildFlatMap(ctx, obj, objects1, true, false);
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
	    sourceObj = evaluateFunction(e0ctx);
	    searchObj = evaluateFunction(e1ctx);
	    if (indexCtx != null)
		start = getIntValue(indexCtx);

	    if (sourceObj instanceof ObjectScope) {
		// Return value is index of key in the map
		ObjectScope obj = (ObjectScope) sourceObj;
		String searchKey = toNonNullString(e1ctx, searchObj);
		size = obj.size();

		ret = obj.indexOf(searchKey, start, settings.ignoreNameCase);
	    }
	    else if (sourceObj instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<Object> list = (ArrayScope<Object>) sourceObj;
		size = list.size();

		ret = indexOf(this, e0ctx, e1ctx, list, searchObj, start, settings.mc);
	    }
	    else {
		String sourceString = toNonNullString(e0ctx, sourceObj);
		String searchString = toNonNullString(e1ctx, searchObj);
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

	private String substring(Object value, CalcParser.ExprContext ctx, CalcParser.Expr2Context e2ctx, CalcParser.Expr3Context e3ctx) {
	    CalcParser.ExprContext beginCtx = null, endCtx = null;

	    if (e2ctx != null) {
		beginCtx = e2ctx.expr(1);
	    }
	    else if (e3ctx != null) {
		beginCtx = e3ctx.expr(1);
		endCtx   = e3ctx.expr(2);
	    }

	    String stringValue = toStringValue(this, ctx, value, false, settings.separatorMode);

	    if (beginCtx == null) {
		return stringValue;
	    }

	    int stringLen  = stringValue.length();
	    int beginIndex = beginCtx == null ? 0 : getIntValue(beginCtx);
	    int endIndex   = endCtx == null ? stringLen : getIntValue(endCtx);

	    if (beginIndex < 0)
		beginIndex += stringLen;
	    if (endIndex < 0)
		endIndex += stringLen;

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

	private String substring(CalcParser.Expr1Context e1ctx, CalcParser.Expr2Context e2ctx, CalcParser.Expr3Context e3ctx) {
	    CalcParser.ExprContext valueCtx;

	    if (e1ctx != null) {
		valueCtx = e1ctx.expr();
	    }
	    else if (e2ctx != null) {
		valueCtx = e2ctx.expr(0);
	    }
	    else {
		valueCtx = e3ctx.expr(0);
	    }

	    String stringValue = getStringValue(valueCtx);

	    return substring(stringValue, valueCtx, e2ctx, e3ctx);
	}

	@Override
	public Object visitSubstrExpr(CalcParser.SubstrExprContext ctx) {
	    return substring(ctx.expr1(), ctx.expr2(), ctx.expr3());
	}

	@Override
	public Object visitReplaceExpr(CalcParser.ReplaceExprContext ctx) {
	    CalcParser.ReplaceArgsContext args = ctx.replaceArgs();
	    String option = "";

	    String original = getStringValue(args.expr(0));
	    String pattern  = getStringValue(args.expr(1));
	    String replace  = getStringValue(args.expr(2));
	    String result   = original;

	    if (args.replaceOption() != null) {
		if (args.replaceOption().var() != null) {
		    CalcParser.VarContext var = args.replaceOption().var();
		    LValueContext lValue = getLValue(var);
		    Object optionObject = lValue.getContextObject(this, false);
		    option = optionObject == null ? "" : toStringValue(this, var, optionObject, true, false);
		}
		else {
		    option = args.replaceOption().getText();
		}
	    }

	    try {
		switch (option) {
		    case "":
			// Regular replace using the pattern as a literal
			result = original.replace(pattern, replace);
			break;
		    case "all":
			// Use pattern as a regex and replace all matching values
			result = original.replaceAll(pattern, replace);
			break;
		    case "first":
			// Again, pattern is a regex, but only replace the first match
			result = original.replaceFirst(pattern, replace);
			break;
		    case "last":
			// New functionality, pattern is a regex, but only replace the last match
			result = original.replaceFirst("(?s)(.*)" + pattern, "$1" + replace);
			break;
		    default:
			throw new CalcExprException(args.replaceOption(), "%calc#replaceOptionError", option);
		}
	    }
	    catch (Exception ex) {
		throw new CalcExprException(ex, ctx);
	    }

	    return result;
	}

	@Override
	public Object visitSliceExpr(CalcParser.SliceExprContext ctx) {
	    CalcParser.Expr1Context e1ctx = ctx.expr1();
	    CalcParser.Expr2Context e2ctx = ctx.expr2();
	    CalcParser.Expr3Context e3ctx = ctx.expr3();
	    CalcParser.ExprContext valueCtx, beginCtx = null, endCtx = null;
	    int beginIndex, endIndex;
	    int arrayLen;
	    Object value;
	    ArrayScope<?> listValue;

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

	    value = evaluateFunction(valueCtx);

	    if (value instanceof ArrayScope) {
		arrayLen = ((ArrayScope) value).size();
	    }
	    else if (value instanceof ObjectScope) {
		arrayLen = ((ObjectScope) value).size();
	    }
	    else {
		// Any simple object behaves the same way as "substr"
		return substring(value, valueCtx, e2ctx, e3ctx);
	    }

	    beginIndex = beginCtx == null ? 0 : getIntValue(beginCtx);
	    endIndex   = endCtx == null ? arrayLen : getIntValue(endCtx);

	    if (beginIndex < 0)
		beginIndex += arrayLen;
	    if (endIndex < 0)
		endIndex += arrayLen;

	    if (beginIndex < 0)
		beginIndex = 0;
	    if (beginIndex > arrayLen)
		beginIndex = arrayLen;
	    if (endIndex < beginIndex)
		endIndex = beginIndex;
	    if (endIndex > arrayLen)
		endIndex = arrayLen;

	    ArrayScope<Object> result = new ArrayScope<>();

	    if (value instanceof ArrayScope) {
		List<?> valueList = ((ArrayScope) value).list();
		for (int index = beginIndex; index < endIndex; index++) {
		    Object val = valueList.get(index);
		    result.add(val);
		}
	    }
	    else {
		Object[] valueArray = ((ObjectScope) value).values().toArray();
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

	    Object source = evaluateFunction(objCtx);

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
		    List<Object> list = array.list();
		    for (int index = 0; index < count; index++) {
			removed.list().add(list.remove(start));
		    }

		    // Now if any elements were given to add/insert, do that starting from "start" also
		    for (int index = 3; index < exprLen; index++) {
			CalcParser.ExprContext valueCtx = exprs.get(index);
			Object value = evaluateFunction(valueCtx);
			list.add(index - 3 + start, value);
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
			String key;
			Object value;

			for (CalcParser.IdContext id : dropObjs.id()) {
			    key = id.getText();
			    value = object.remove(key, settings.ignoreNameCase);
			    removed.map().put(key, value);
			}
			for (TerminalNode string : dropObjs.STRING()) {
			    key = string.getText();
			    value = object.remove(key, settings.ignoreNameCase);
			    removed.map().put(key, value);
			}
			for (TerminalNode istring : dropObjs.ISTRING()) {
			    key = getIStringValue(this, istring, addObjs);
			    value = object.remove(key, settings.ignoreNameCase);
			    removed.map().put(key, value);
			}
		    }
		    else {
			// With no drop list, we need to clear out and return everything
			removed.map().putAll(object.map());
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

	private class ObjectComparator implements Comparator<Object>
	{
		private CalcObjectVisitor visitor;
		private ParserRuleContext ctx;
		private boolean ignoreCase;

		ObjectComparator(CalcObjectVisitor v, ParserRuleContext c, boolean ignore) {
		    this.visitor    = v;
		    this.ctx        = c;
		    this.ignoreCase = ignore;
		}

		@Override
		public int compare(Object o1, Object o2) {
		    return CalcUtil.compareValues(visitor, ctx, ctx, o1, o2, settings.mc, false, true, ignoreCase, true);
		}
	}

	@Override
	public Object visitSortExpr(CalcParser.SortExprContext ctx) {
	    CalcParser.Expr1Context e1ctx = ctx.expr1();
	    CalcParser.Expr2Context e2ctx = ctx.expr2();
	    CalcParser.ExprContext arrCtx;
	    boolean caseInsensitive = false;

	    if (e1ctx != null) {
		arrCtx = e1ctx.expr();
	    }
	    else {
		arrCtx = e2ctx.expr(0);
		caseInsensitive = getBooleanValue(e2ctx.expr(1));
	    }

	    List<CalcParser.ExprContext> exprs = new ArrayList<>();
	    exprs.add(arrCtx);
	    List<Object> values = buildValueList(exprs, Conversion.UNCHANGED);

	    Collections.sort(values, new ObjectComparator(this, arrCtx, caseInsensitive));

	    return new ArrayScope<Object>(values);
	}

	@Override
	public Object visitReverseExpr(CalcParser.ReverseExprContext ctx) {
	    CalcParser.ExprContext expr = ctx.expr1().expr();
	    Object value = evaluateFunction(expr);

	    if (value instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<Object> array = (ArrayScope<Object>) value;
		Collections.reverse(array.list());
		return array;
	    }
	    else {
		String string;
		if (value instanceof String)
		    string = (String) value;
		else
		    string = toStringValue(this, expr, value, false, false);
		StringBuilder buf = new StringBuilder(string);
		return buf.reverse().toString();
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
		fillValue = evaluateFunction(fillExpr);
	    }
	    if (exprs.size() == 2) {
		length = getIntValue(exprs.get(1));
	    }
	    else if (exprs.size() == 3) {
		start  = getIntValue(exprs.get(1));
		length = getIntValue(exprs.get(2));
	    }

	    if (value instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<Object> list = (ArrayScope<Object>) value;
		if (length == 0)
		    length = list.size();
		for (int index = start; index < (start + length); index++) {
		    list.setValue(index, fillValue);
		}
	    }
	    else if (value instanceof String) {
		StringBuilder buf = new StringBuilder((String) value);
		if (length == 0)
		    length = buf.length();
		if (buf.length() < start + length) {
		    buf.setLength(start + length);
		}

		char fillChar = getCharValue(fillExpr, fillValue, "Fill", '\0');

		for (int index = start; index < (start + length); index++) {
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
		args[i - 1] = evaluateFunction(expr);
	    }

	    return String.format(formatString, args);
	}

	@Override
	public Object visitTrimExpr(CalcParser.TrimExprContext ctx) {
	    String stringValue = getStringValue(ctx.expr1().expr());
	    String op          = ctx.K_TRIM().getText().toLowerCase();
	    String result;

	    switch (op) {
		case "trim":
		    result = stringValue.trim();
		    break;

		case "ltrim":
		    result = CharUtil.ltrim(stringValue);
		    break;

		case "rtrim":
		    result = CharUtil.rtrim(stringValue);
		    break;

		default:
		    throw new UnknownOpException(op, ctx);
	    }

	    return result;
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
			padValue = evaluateFunction(padExpr);
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
		String input = toStringValue(this, varCtx, value, false, settings.separatorMode);
		if (input.length() < posWidth) {
		    StringBuilder buf = new StringBuilder(width);
		    CharUtil.Justification just;
		    char padChar = ' ';

		    if (exprs.size() > 1) {
			padExpr = exprs.get(1);
			padValue = evaluateFunction(padExpr);
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
	public Object visitFracExpr(CalcParser.FracExprContext ctx) {
	    try {
		CalcParser.Expr1Context expr1 = ctx.expr1();
		if (expr1 != null) {
		    CalcParser.ExprContext expr = expr1.expr();
		    Object e = evaluateFunction(expr);
		    return toFractionValue(this, e, expr);
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
		CalcParser.Expr1Context expr1 = ctx.expr1();
		if (expr1 != null) {
		    CalcParser.ExprContext expr = expr1.expr();
		    Object e = evaluateFunction(expr);

		    if (e instanceof ArrayScope) {
			@SuppressWarnings("unchecked")
			ArrayScope<Object> array = (ArrayScope<Object>) e;
			return ComplexNumber.valueOf(array.list());
		    }
		    else if (e instanceof ObjectScope) {
			ObjectScope obj = (ObjectScope) e;
			return ComplexNumber.valueOf(obj.map());
		    }
		    else {
			return ComplexNumber.valueOf(e);
		    }
		}
		else {
		    CalcParser.Expr2Context e2ctx = ctx.expr2();
		    BigDecimal r = getDecimalValue(e2ctx.expr(0));
		    BigDecimal i = getDecimalValue(e2ctx.expr(1));

		    return new ComplexNumber(r, i);
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

	@Override
	public Object visitCaseConvertExpr(CalcParser.CaseConvertExprContext ctx) {
	    TerminalNode upper = ctx.K_UPPER();
	    String exprString  = getStringValue(ctx.expr1().expr());

	    if (upper != null) {
		return exprString == null ? exprString : exprString.toUpperCase();
	    }
	    else {
		return exprString == null ? exprString : exprString.toLowerCase();
	    }
	}

	@Override
	public Object visitFactorsExpr(CalcParser.FactorsExprContext ctx) {
	    ArrayScope<Integer> result = new ArrayScope<>();
	    BigInteger n = getIntegerValue(ctx.expr1().expr());

	    MathUtil.getFactors(n, result.list());

	    return result;
	}

	@Override
	public Object visitPrimeFactorsExpr(CalcParser.PrimeFactorsExprContext ctx) {
	    ArrayScope<Integer> result = new ArrayScope<>();
	    BigInteger n = getIntegerValue(ctx.expr1().expr());

	    MathUtil.getPrimeFactors(n, result.list());

	    return result;
	}

	@Override
	public Object visitCharsExpr(CalcParser.CharsExprContext ctx) {
	    final ArrayScope<String> result = new ArrayScope<>();
	    String string = getStringValue(ctx.expr1().expr());

	    string.codePoints().forEachOrdered(cp -> result.list().add(String.valueOf(Character.toChars(cp))));

	    return result;
	}

	@Override
	public Object visitCodesExpr(CalcParser.CodesExprContext ctx) {
	    final ArrayScope<Integer> result = new ArrayScope<>();
	    String string = getStringValue(ctx.expr1().expr());

	    string.codePoints().forEachOrdered(cp -> result.list().add(cp));

	    return result;
	}

	@Override
	public Object visitDayOfWeekExpr(CalcParser.DayOfWeekExprContext ctx) {
	    BigInteger iValue = getIntegerValue(ctx.expr1().expr());
	    try {
		LocalDate date = LocalDate.ofEpochDay(iValue.longValueExact());
		DayOfWeek dow = date.getDayOfWeek();
		// Adjust the return b/c I didn't like their ordering
		// theirs = 1 (Monday) to 7 (Sunday) while we have defined
		// ours = 0 (Sunday) to 6 (Saturday)
		int adjustedDow = dow.getValue() % 7;
		return BigInteger.valueOf((long) adjustedDow);
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx);
	    }
	}

	@Override
	public Object visitDayOfMonthExpr(CalcParser.DayOfMonthExprContext ctx) {
	    BigInteger iValue = getIntegerValue(ctx.expr1().expr());
	    try {
		LocalDate date = LocalDate.ofEpochDay(iValue.longValueExact());
		int dom = date.getDayOfMonth();
		// Theirs matches ours: 1 .. n value
		return BigInteger.valueOf((long) dom);
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx);
	    }
	}

	@Override
	public Object visitDayOfYearExpr(CalcParser.DayOfYearExprContext ctx) {
	    BigInteger iValue = getIntegerValue(ctx.expr1().expr());
	    try {
		LocalDate date = LocalDate.ofEpochDay(iValue.longValueExact());
		int doy = date.getDayOfYear();
		// Theirs matches ours: 1 .. 365/366 value
		return BigInteger.valueOf((long) doy);
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx);
	    }
	}

	@Override
	public Object visitMonthOfYearExpr(CalcParser.MonthOfYearExprContext ctx) {
	    BigInteger iValue = getIntegerValue(ctx.expr1().expr());
	    try {
		LocalDate date = LocalDate.ofEpochDay(iValue.longValueExact());
		int moy = date.getMonthValue();
		// Theirs matches ours: 1 .. 12 value
		return BigInteger.valueOf((long) moy);
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx);
	    }
	}

	@Override
	public Object visitYearOfDateExpr(CalcParser.YearOfDateExprContext ctx) {
	    BigInteger iValue = getIntegerValue(ctx.expr1().expr());
	    try {
		LocalDate date = LocalDate.ofEpochDay(iValue.longValueExact());
		int yod = date.getYear();
		return BigInteger.valueOf((long) yod);
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx);
	    }
	}

	@Override
	public Object visitEvalExpr(CalcParser.EvalExprContext ctx) {
	    String exprString = getStringValue(ctx.expr1().expr());

	    return Calc.processString(exprString, true);
	}


	/**
	 * Do a "flat map" of values for the "sumof", "productof", "sort", and "exec" functions.  Since each
	 * value to be processed could be an array or map, we need to traverse these objects
	 * as well as the simple values in order to get the full list to process.
	 *
	 * @param ctx		The overall parser context for the function (for error messages).
	 * @param obj		The object to be added to the list, or recursed into when the object
	 *			is a list or map.
	 * @param objectList	The complete list of values to be built.
	 * @param conversion	Type of conversion to do on the values.
	 */
	private void buildValueList(
		final ParserRuleContext ctx,
		final Object obj,
		final List<Object> objectList,
		final Conversion conversion)
	{
	    Object value = evaluateFunction(ctx, obj);

	    nullCheck(value, ctx);

	    if (value instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope array = (ArrayScope) value;
		for (Object listObj : array.list()) {
		    buildValueList(ctx, listObj, objectList, conversion);
		}
	    }
	    else if (value instanceof ObjectScope) {
		@SuppressWarnings("unchecked")
		ObjectScope map = (ObjectScope) value;
		for (Object mapObj : map.values()) {
		    buildValueList(ctx, mapObj, objectList, conversion);
		}
	    }
	    else {
		switch (conversion) {
		    case STRING:
			objectList.add(toStringValue(this, ctx, value, false, false));
			break;
		    case DECIMAL:
			objectList.add(toDecimalValue(this, value, settings.mc, ctx));
			break;
		    case FRACTION:
			objectList.add(toFractionValue(this, value, ctx));
			break;
		    case UNCHANGED:
			objectList.add(value);
			break;
		}
	    }
	}

	/**
	 * Traverse the expression list for the "sumof", "productof", and "sort" functions and
	 * build the complete list of values to be processed.
	 *
	 * @param exprs	     The parsed list of expression contexts.
	 * @param conversion How or whether to convert the values for the final list.
	 * @return	     The completely built "flat map" of values.
	 */
	private List<Object> buildValueList(final List<CalcParser.ExprContext> exprs, final Conversion conversion) {
	    List<Object> objects = new ArrayList<>();

	    for (CalcParser.ExprContext exprCtx : exprs) {
		buildValueList(exprCtx, evaluateFunction(exprCtx), objects, conversion);
	    }

	    return objects;
	}

	@Override
	public Object visitExecExpr(CalcParser.ExecExprContext ctx) {
	    List<CalcParser.ExprContext> exprs = ctx.exprN().exprList().expr();
	    List<Object> objects = buildValueList(exprs, Conversion.STRING);

	    // As a convenience, if we're on Windows and the target is a ".bat" or ".cmd" file
	    // then prepend "cmd /c" before running it.
	    if (RUNNING_ON_WINDOWS) {
		List<String> names = new ArrayList<>();
		names.add(objects.get(0).toString());
		List<File> files = Which.findAll(names, false);
		if (!files.isEmpty()) {
		    String name = files.get(0).getName().toLowerCase();
		    if (name.endsWith(".bat") || name.endsWith(".cmd")) {
			objects.add(0, "cmd");
			objects.add(1, "/c");
		    }
		}
	    }

	    String[] args = new String[objects.size()];
	    for (int i = 0; i < objects.size(); i++) {
		args[i] = objects.get(i).toString();
	    }

	    try {
		RunCommand cmd = new RunCommand(args);
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
	public Object visitReadExpr(CalcParser.ReadExprContext ctx) {
	    CalcParser.Expr1Context e1ctx = ctx.expr1();
	    CalcParser.Expr2Context e2ctx = ctx.expr2();
	    String fileName = "";
	    Charset cs = null;

	    if (e1ctx != null) {
		fileName = getStringValue(e1ctx.expr());
	    }
	    else {
		fileName = getStringValue(e2ctx.expr(0));
		cs = getCharsetValue(e2ctx.expr(1), false);
	    }

	    try {
		return Calc.getFileContents(fileName, cs);
	    }
	    catch (IOException ioe) {
		throw new CalcExprException(ioe, ctx);
	    }
	}

	private boolean matches(String input, String pattern) {
	    Pattern p = Pattern.compile(pattern);
	    Matcher m = p.matcher(input);

	    return m.matches();
	}

	@Override
	public Object visitMatchesExpr(CalcParser.MatchesExprContext ctx) {
	    CalcParser.Expr2Context expr2 = ctx.expr2();
	    String input = getStringValue(expr2.expr(0));
	    String pattern = getStringValue(expr2.expr(1));

	    return Boolean.valueOf(matches(input, pattern));
	}

	private class SumOfVisitor implements Function<Object, Object>
	{
		private BigFraction sumFrac = BigFraction.ZERO;
		private BigDecimal sum = BigDecimal.ZERO;
		private ParserRuleContext ctx;

		public SumOfVisitor(final ParserRuleContext context) {
		    this.ctx = context;
		}

		@Override
		public Object apply(final Object value) {
		    if (settings.rationalMode) {
			BigFraction frac = value instanceof BigFraction
				? (BigFraction) value
				: toFractionValue(CalcObjectVisitor.this, value, ctx);
			sumFrac = sumFrac.add(frac);
			return sumFrac;
		    }
		    else {
			BigDecimal dec = value instanceof BigDecimal
				? (BigDecimal) value
				: toDecimalValue(CalcObjectVisitor.this, value, settings.mc, ctx);
			sum = sum.add(dec, settings.mc);
			return sum;
		    }
		}
	}

	@Override
	public Object visitSumOfExpr(CalcParser.SumOfExprContext ctx) {
	    SumOfVisitor sumVisitor = new SumOfVisitor(ctx);
	    Object sum = null;

	    CalcParser.DotRangeContext dotRange = ctx.dotRange();
	    if (dotRange == null) {
		List<CalcParser.ExprContext> exprs = ctx.exprN().exprList().expr();
		List<Object> objects = buildValueList(exprs,
			settings.rationalMode ? Conversion.FRACTION : Conversion.DECIMAL);

		for (Object obj : objects) {
		    sum = sumVisitor.apply(obj);
		}
	    }
	    else {
		sum = iterateOverDotRange(null, dotRange.expr(), dotRange.DOTS() != null, sumVisitor, false, false);
	    }

	    return sum;
	}

	private class ProductOfVisitor implements Function<Object, Object>
	{
		private BigFraction productFrac = BigFraction.ONE;
		private BigDecimal product = BigDecimal.ONE;
		private ParserRuleContext ctx;

		public ProductOfVisitor(final ParserRuleContext context) {
		    this.ctx = context;
		}

		@Override
		public Object apply(final Object value) {
		    if (settings.rationalMode) {
			BigFraction frac = value instanceof BigFraction
				? (BigFraction) value
				: toFractionValue(CalcObjectVisitor.this, value, ctx);
			productFrac = productFrac.multiply(frac);
			return productFrac;
		    }
		    else {
			BigDecimal dec = value instanceof BigDecimal
				? (BigDecimal) value
				: toDecimalValue(CalcObjectVisitor.this, value, settings.mc, ctx);
			product = product.multiply(dec, settings.mc);
			return product;
		    }
		}
	}

	@Override
	public Object visitProductOfExpr(CalcParser.ProductOfExprContext ctx) {
	    ProductOfVisitor productVisitor = new ProductOfVisitor(ctx);
	    Object product = null;

	    CalcParser.DotRangeContext dotRange = ctx.dotRange();
	    if (dotRange == null) {
		List<CalcParser.ExprContext> exprs = ctx.exprN().exprList().expr();
		List<Object> objects = buildValueList(exprs,
			settings.rationalMode ? Conversion.FRACTION : Conversion.DECIMAL);

		for (Object obj : objects) {
		    product = productVisitor.apply(obj);
		}
	    }
	    else {
		product = iterateOverDotRange(null, dotRange.expr(), dotRange.DOTS() != null, productVisitor, false, false);
	    }

	    return product;
	}

	@Override
	public Object visitFactorialExpr(CalcParser.FactorialExprContext ctx) {
	    BigDecimal e = getDecimalValue(ctx.expr());

	    return MathUtil.factorial(e, settings.mc);
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
	    int ret = compareValues(ctx.expr(0), ctx.expr(1), false, true);

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
				settings.mc, true, true, false, false);
		    else
			cmp = compareValues(expr1, expr2, true, true);
		    break;

		case "==":
		case "\u2A75":
		case "!=":
		case "<>":
		case "\u2260": // NOT EQUAL
		    if (optObj1 != null)
			cmp = CalcUtil.compareValues(this, expr1, expr2, optObj1.orElse(null), visit(expr2),
				settings.mc, false, true, false, false);
		    else
			cmp = compareValues(expr1, expr2, false, true);
		    break;

		default:
		    if (optObj1 != null)
			cmp = CalcUtil.compareValues(this, expr1, expr2, optObj1.orElse(null), visit(expr2),
				settings.mc, false, false, false, false);
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

	private class InVisitor implements Function<Object, Object>
	{
		private CalcParser.ExprContext valueCtx;
		private CalcParser.LoopCtlContext loopCtx;
		private Object inValue;
		private boolean compared = false;


		InVisitor(CalcParser.ExprContext ctx1, CalcParser.LoopCtlContext ctx2, Object value) {
		    this.valueCtx = ctx1;
		    this.loopCtx  = ctx2;
		    this.inValue  = value;
		}

		@Override
		public Object apply(final Object value) {
		    int cmp = CalcUtil.compareValues(
			CalcObjectVisitor.this,
			valueCtx, loopCtx,
			inValue, value,
			settings.mc,
			false, true, false, false);

		    if (cmp == 0)
			compared = true;

		    return Boolean.valueOf(compared);
		}
	}

	@Override
	public Object visitInExpr(CalcParser.InExprContext ctx) {
	    CalcParser.ExprContext expr         = ctx.expr();
	    CalcParser.LoopCtlContext ctlCtx    = ctx.loopCtl();
	    CalcParser.ExprListContext exprList = ctlCtx.exprList();
	    CalcParser.DotRangeContext dotCtx   = ctlCtx.dotRange();
	    Object value = evaluateFunction(expr);
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
	    BigInteger e1 = getIntegerValue(ctx.expr(0));
	    BigInteger e2 = getIntegerValue(ctx.expr(1));

	    String op = ctx.BIT_OP().getText();

	    return bitOp(e1, e2, op, ctx);
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
	    Object v0 = evaluateFunction(expr0);
	    if (toBooleanValue(this, v0, expr0)) {
		return v0;
	    }
	    return evaluateFunction(ctx.expr(1));
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
	    return BigInteger.valueOf(NumericUtil.convertKMGValue(value));
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

	static final long ZERO_DAY = -719528; // d'0000-01-01'
	static final long Y10K_DAY = 2932897; // d'10000-01-01'

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

		if (negate) {
		    // Use year 10,000 as a base to figure out how negative from d'0000-01-01'
		    // we need to go (since LocalDate won't handle negative years in parsing)
		    // since year 10,000 and ff. share the same leap year calculations as year 0000
		    String y10kDateString = String.format("%1$04d-%2$02d-%3$02d", 10000 - year, month, day);
		    LocalDate y10kDateDate = LocalDate.parse(y10kDateString);
		    long offset = y10kDateDate.toEpochDay() - Y10K_DAY;
		    epochDate = ZERO_DAY + offset;
		}
		else {
		    // Get a value in strict ISO-8601 format for parsing
		    value = String.format("%1$04d-%2$02d-%3$02d", year, month, day);
		    LocalDate date = LocalDate.parse(value);
		    epochDate = date.toEpochDay();
		}

		return BigInteger.valueOf(epochDate);
	    }
	    catch (DateTimeParseException | NumberFormatException ex) {
		throw new CalcExprException(ex, ctx);
	    }
	}

	@Override
	public Object visitEitherOrExpr(CalcParser.EitherOrExprContext ctx) {
	    boolean ifExpr = getBooleanValue(ctx.expr(0));

	    return evaluateFunction(ifExpr ? ctx.expr(1) : ctx.expr(2));
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
		    result = addOp(this, e1, e2, varCtx, exprCtx, settings.mc, settings.rationalMode);
		    break;
		case "-=":
		case "\u2212=":
		case "\u2796=":
		    if (settings.rationalMode) {
			BigFraction f1 = toFractionValue(this, e1, varCtx);
			BigFraction f2 = toFractionValue(this, e2, exprCtx);

			result = f1.subtract(f2);
		    }
		    else if (e1 instanceof ComplexNumber || e2 instanceof ComplexNumber) {
			ComplexNumber c1 = ComplexNumber.valueOf(e1);
			ComplexNumber c2 = ComplexNumber.valueOf(e2);

			result = c1.subtract(c2);
		    }
		    else {
			BigDecimal d1 = toDecimalValue(this, e1, settings.mc, varCtx);
			BigDecimal d2 = toDecimalValue(this, e2, settings.mc, exprCtx);

			result = d1.subtract(d2, settings.mc);
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

	    BigDecimal base = toDecimalValue(this, lValue.getContextObject(this), settings.mc, ctx);
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
	    Object e2 = visit(exprCtx);

	    try {
		if (settings.rationalMode) {
		    BigFraction f1 = toFractionValue(this, e1, varCtx);
		    BigFraction f2 = toFractionValue(this, e2, exprCtx);

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
			    result = f1.divide(f2);
			    break;
			case "%=":
			    // ??? remainder of fraction / fraction is always 0
			    result = BigFraction.ZERO;
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
			    result = c1.divide(c2, settings.mcDivide);
			case "\\=":
			    throw new UnknownOpException(op, ctx); // ?? what to do here?
			case "%=":
			    throw new UnknownOpException(op, ctx); // ?? I don't know the math yet!!
			default:
			    throw new UnknownOpException(op, ctx);
		    }
		}
		else {
		    BigDecimal d1 = toDecimalValue(this, e1, settings.mc, varCtx);
		    BigDecimal d2 = toDecimalValue(this, e2, settings.mc, exprCtx);

		    switch (op) {
			case "*=":
			case "\u00D7=":
			case "\u2217=":
			case "\u2715=":
			case "\u2716=":
			    result = d1.multiply(d2, settings.mc);
			    break;
			case "/=":
			case "\u00F7=":
			case "\u2215=":
			case "\u2797=":
			    result = d1.divide(d2, settings.mcDivide);
			    break;
			case "\\=":
			    result = d1.divideToIntegralValue(d2, settings.mcDivide);
			    break;
			case "%=":
			    result = d1.remainder(d2, settings.mcDivide);
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

	    BigInteger i1 = toIntegerValue(this, lValue.getContextObject(this), settings.mc, ctx);
	    BigInteger i2 = getIntegerValue(ctx.expr());

	    String op = ctx.BIT_ASSIGN().getText();
	    // Strip off the trailing '=' of the operator
	    op = op.substring(0, op.length() - 1);

	    return lValue.putContextObject(this, bitOp(i1, i2, op, ctx));
	}

	@Override
	public Object visitShiftAssignExpr(CalcParser.ShiftAssignExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());

	    BigInteger i1 = toIntegerValue(this, lValue.getContextObject(this), settings.mc, ctx);
	    int e2        = getIntValue(ctx.expr());

	    String op = ctx.SHIFT_ASSIGN().getText();
	    // Strip off the trailing "="
	    op = op.substring(0, op.length() - 1);

	    return lValue.putContextObject(this, shiftOp(i1, e2, op, ctx));
	}

	@Override
	public Object visitAssignExpr(CalcParser.AssignExprContext ctx) {
	    Object value = evaluateFunction(ctx.expr());

	    LValueContext lValue = getLValue(ctx.var());
	    return lValue.putContextObject(this, value);
	}
}
