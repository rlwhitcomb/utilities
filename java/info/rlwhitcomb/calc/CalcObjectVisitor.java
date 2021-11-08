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
 *  History:
 *      04-Dec-2020 (rlwhitcomb)
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
 */
package info.rlwhitcomb.calc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.UnaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

import net.iharder.b64.Base64;

import de.onyxbits.SemanticVersion;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import info.rlwhitcomb.util.BigFraction;
import static info.rlwhitcomb.calc.CalcUtil.*;
import info.rlwhitcomb.util.CharUtil;
import static info.rlwhitcomb.util.ConsoleColor.Code.*;
import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.ExceptionUtil;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.MathUtil;
import info.rlwhitcomb.util.NumericUtil;
import static info.rlwhitcomb.util.NumericUtil.RangeMode;
import info.rlwhitcomb.util.RunCommand;


/**
 * Visit each node of the parse tree and do the appropriate calculations at each level.
 * <p> Separate from the grammar, which at this point is completely language-agnostic.
 */
public class CalcObjectVisitor extends CalcBaseVisitor<Object>
{
	/**
	 * The mode used for doing trig calculations.
	 */
	public static enum TrigMode
	{
		DEGREES,
		RADIANS
	}


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

	/**
	 * The system-specific default charset to use for ":open" and ":save" when no other
	 * charset is specified.
	 */
	private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();


	/** Initialization flag -- delays print until constructor is finished.  */
	private boolean initialized = false;

	/** Note: the precision will be determined by the number of digits desired. */
	private MathContext mc;

	/** The precision to use for certain operations (division) when UNLIMITED is otherwise specified. */
	private MathContext mcDivide;

	/** A BigInteger <code>-1</code> value (for repeated use here). */
	private static BigInteger I_MINUS_ONE = BigInteger.ONE.negate();


	/**
	 * The settings object.
	 */
	public static class Settings
	{
		/** Whether trig inputs are in degrees or radians. */
		TrigMode trigMode;
		/** The kind of units to use for the "@k" format. */
		RangeMode units;
		/** Decimal vs. rational/fractional mode ({@code true} for rational); default {@code false}. */
		boolean rationalMode;
		/** Separators displayed always. */
		boolean separatorMode;
		/** Silent flag (set to true) while evaluating nested expressions (or via :quiet directive). */
		boolean silent;
		/** Ignore case when selecting members / variables. */
		boolean ignoreNameCase;

		/**
		 * Construct default settings, including the command-line "-rational" flag.
		 *
		 * @param rational   The initial rational mode setting.
		 * @param separators The initial setting for displaying separators.
		 * @param ignoreCase Whether to ignore case on variable / member names.
		 */
		public Settings(boolean rational, boolean separators, boolean ignoreCase) {
		    trigMode       = TrigMode.RADIANS;
		    units          = RangeMode.MIXED;
		    rationalMode   = rational;
		    separatorMode  = separators;
		    silent         = false;
		    ignoreNameCase = ignoreCase;
		}

		/**
		 * Copy constructor - make a copy of another {@code Settings} object.
		 *
		 * @param otherSettings The object to copy.
		 */
		public Settings(Settings otherSettings) {
		    this.trigMode       = otherSettings.trigMode;
		    this.units          = otherSettings.units;
		    this.rationalMode   = otherSettings.rationalMode;
		    this.separatorMode  = otherSettings.separatorMode;
		    this.silent         = otherSettings.silent;
		    this.ignoreNameCase = otherSettings.ignoreNameCase;
		}
	}

	/** The mode settings for this instantiation of the visitor. */
	private final Settings settings;

	/** Global symbol table for variables. */
	private final GlobalScope globals;

	/** The current topmost scope for variables. */
	private NestedScope currentScope;

	/** The array object of the command-line arguments. */
	private ArrayScope<Object> arguments;

	/** The current {@code LValueContext} for variables. */
	private LValueContext currentContext;

	/** {@link CalcDisplayer} object so we can output results to either the console or GUI window. */
	private final CalcDisplayer displayer;

	/**
	 * The worker used to maintain the current e/pi values, and calculate them
	 * in a background thread.
	 */
	private CalcPiWorker piWorker = null;

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

	/** Stack of previous "resultsOnly" mode values. */
	private final Deque<Boolean> resultsOnlyModeStack = new ArrayDeque<>();

	/** Stack of previous "quiet" mode values. */
	private final Deque<Boolean> quietModeStack = new ArrayDeque<>();

	/**
	 * Aliases for "pi" - need prefixes of '\\' and 'u'  before calling {@link CharUtil#convertEscapeSequences}.
	 */
	private static final String[] PI_ALIASES = {
	    "\u03A0", "\u03C0", "\u03D6", "\u1D28", "\u213C", "\u213F",
	    "{1D6B7}", "{1D6D1}", "{1D6E1}",
	    "{1D6F1}", "{1D70B}", "{1D71B}",
	    "{1D72B}", "{1D745}", "{1D755}",
	    "{1D765}", "{1D77F}", "{1D78F}",
	    "{1D79F}", "{1D7B9}", "{1D7C9}"
	};

	/**
	 * Aliases for "e".
	 */
	private static final String[] E_ALIASES = {
	    "\u2107"
	};

	/** Name for global argument array value. */
	private static final String ARG_ARRAY = "$*";

	/** Name for global argument count value. */
	private static final String ARG_COUNT = "$#";


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

	private void predefine(final GlobalScope globalScope) {
	    PredefinedValue.define(globalScope, "true", Boolean.TRUE);
	    PredefinedValue.define(globalScope, "false", Boolean.FALSE);
	    PredefinedValue.define(globalScope, "null", null);

	    PredefinedValue.define(globalScope, "today", () -> {
		LocalDate today = LocalDate.now();
		return BigInteger.valueOf(today.toEpochDay());
	    });
	    PredefinedValue.define(globalScope, "now", () -> {
		LocalTime now = LocalTime.now();
		return BigInteger.valueOf(now.toNanoOfDay());
	    });

	    SemanticVersion v = Environment.programVersion();
	    ObjectScope version = new ObjectScope();

	    version.setValue("major",      settings.ignoreNameCase, v.major);
	    version.setValue("minor",      settings.ignoreNameCase, v.minor);
	    version.setValue("patch",      settings.ignoreNameCase, v.patch);
	    version.setValue("prerelease", settings.ignoreNameCase, v.getPreReleaseString());
	    version.setValue("build",      settings.ignoreNameCase, v.getBuildMetaString());

	    PredefinedValue.define(globalScope, "versioninfo", version);

	    Supplier<Object> piSupplier = () -> {
		BigDecimal d = piWorker.getPi();
		if (settings.rationalMode)
		    return new BigFraction(d);
		else
		    return d;
	    };
	    PredefinedValue.define(globalScope, "pi", piSupplier);
	    for (int i = 0; i < PI_ALIASES.length; i++) {
		String alias = PI_ALIASES[i];
		if (alias.charAt(0) == '{') {
		    String key = CharUtil.convertEscapeSequences("\\u" + alias);
		    PredefinedValue.define(globalScope, key, piSupplier);
		}
		else {
		    PredefinedValue.define(globalScope, alias, piSupplier);
		}
	    }

	    Supplier<Object> eSupplier = () -> {
		BigDecimal d = piWorker.getE();
		if (settings.rationalMode)
		    return new BigFraction(d);
		else
		    return d;
	    };
	    PredefinedValue.define(globalScope, "e", eSupplier);
	    for (int i = 0; i < E_ALIASES.length; i++) {
		String alias = E_ALIASES[i];
		PredefinedValue.define(globalScope, alias, eSupplier);
	    }

	    arguments = new ArrayScope<Object>();
	    globalScope.setValue(ARG_ARRAY, false, arguments);
	    globalScope.setValue(ARG_COUNT, false, BigInteger.ZERO);
	}

	public CalcObjectVisitor(final CalcDisplayer resultDisplayer, final boolean rational, final boolean separators, final boolean ignoreCase) {
	    setMathContext(MathContext.DECIMAL128);

	    settings  = new Settings(rational, separators, ignoreCase);
	    globals   = new GlobalScope();
	    displayer = resultDisplayer;

	    pushScope(globals);

	    predefine(globals);

	    initialized   = true;
	}

	public boolean setSilent(final boolean newSilent) {
	    boolean oldSilent = settings.silent;
	    settings.silent = newSilent;
	    return oldSilent;
	}

	private void displayActionMessage(final String formatOrKey, final Object... args) {
	    if (initialized && !settings.silent) {
		String message = Intl.formatKeyString(formatOrKey, args);
		displayer.displayActionMessage(message);
	    }
	}

	/**
	 * Set one of the global argument variables ({@code $0}, {@code $1}, etc)
	 * to the given value.
	 * <p> Also maintain the {@link #ARG_ARRAY} array and {@link #ARG_COUNT} count variables.
	 *
	 * @param index	The zero-based index for the variable.
	 * @param arg	The argument value, which will be parsed and set as a numeric value
	 *		if it successfully parses as a {@link BigDecimal}, otherwise it
	 *		will be set as a string.
	 */
	public void setArgument(final int index, final String arg) {
	    try {
		BigDecimal dValue = new BigDecimal(arg);
		globals.setValue(String.format("$%1$d", index), false, dValue);
		arguments.add(dValue);
	    }
	    catch (NumberFormatException nfe) {
		globals.setValue(String.format("$%1$d", index), false, arg);
		arguments.add(arg);
	    }
	    globals.setValue(ARG_COUNT, false, BigInteger.valueOf(arguments.size()));
	}

	public MathContext getMathContext() {
	    return mc;
	}

	public BigInteger setMathContext(final MathContext newMathContext) {
	    int prec  = newMathContext.getPrecision();
	    mc        = newMathContext;

	    // Use a limited precision of our max digits in the case of unlimited precision
	    MathContext mcPi = (prec == 0) ? MC_MAX_DIGITS : mc;
	    mcDivide = mcPi;

	    // Either create the worker object, or trigger a recalculation
	    if (piWorker == null) {
		piWorker = new CalcPiWorker(mcPi);
	    }
	    else {
		piWorker.calculate(mcPi);
	    }

	    if (prec == 0)
		displayActionMessage("%calc#precUnlimited");
	    else
		displayActionMessage("%calc#precDigits", prec);

	    return BigInteger.valueOf(prec);
	}

	public Settings getSettings() {
	    return settings;
	}

	public String setTrigMode(final TrigMode newTrigMode) {
	    settings.trigMode = newTrigMode;

	    displayActionMessage("%calc#trigMode", settings.trigMode);

	    return settings.trigMode.toString();
	}

	public String setUnits(final RangeMode mode) {
	    settings.units = mode;

	    switch (mode) {
		case BINARY:
		    displayActionMessage("%calc#unitsBinary");
		    break;
		case DECIMAL:
		    displayActionMessage("%calc#unitsTen");
		    break;
		case MIXED:
		    displayActionMessage("%calc#unitsMixed");
		    break;
	    }

	    return settings.units.toString();
	}

	public boolean setSeparatorMode(final boolean mode) {
	    boolean oldMode = settings.separatorMode;
	    settings.separatorMode = mode;

	    displayActionMessage("%calc#separatorMode", mode);

	    return oldMode;
	}

	public boolean setIgnoreCaseMode(final boolean mode) {
	    boolean oldMode = settings.ignoreNameCase;
	    settings.ignoreNameCase = mode;
	    currentContext.setIgnoreCase(mode);

	    displayActionMessage("%calc#ignoreCaseMode", mode);

	    return oldMode;
	}

	public boolean setRationalMode(final boolean mode) {
	    boolean oldMode = settings.rationalMode;
	    settings.rationalMode = mode;

	    displayActionMessage("%calc#rationalMode",
			Intl.getString(mode ? "calc#rational" : "calc#decimal"));

	    return oldMode;
	}

	public boolean setTimingMode(final boolean mode) {
	    boolean oldMode = Calc.setTimingMode(mode);

	    displayActionMessage("%calc#timingMode", mode);

	    return oldMode;
	}

	public boolean setDebugMode(final boolean mode) {
	    boolean oldMode = Calc.setDebugMode(mode);

	    displayActionMessage("%calc#debugMode", mode);

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
	FunctionScope setupFunctionCall(final ParserRuleContext ctx, final FunctionDeclaration decl, final List<CalcParser.ExprContext> exprs) {
	    FunctionScope funcScope = new FunctionScope(decl);
	    int numParams = decl.getNumberOfParameters();
	    int numActuals = exprs != null ? exprs.size() : 0;

	    if (exprs != null) {
		if (numActuals > numParams)
		    throw new CalcExprException(ctx, "%calc#tooManyValues", numActuals, numParams);

		for (int index = 0; index < numActuals; index++) {
		    funcScope.setParameterValue(this, index, exprs.get(index));
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
	 * Evaluate a function: basically call {@code visit} on that context if the value itself
	 * is a function scope (that is, the declaration of a function).
	 * <p> But also, if the value is a {@link FunctionDeclaration} we have a choice:
	 * <ul><li> if the function was declared WITHOUT parameters, then call the function</li>
	 * <li> if the function was defined WITH parameters, then treat the value as a function object
	 * and just return the value</li>
	 * </ul>
	 *
	 * @param ctx   The parsing context (for error reporting).
	 * @param value The result of an expression, which could be a reference to a function call.
	 * @return Either the value or the result of visiting that function context if it is one.
	 */
	Object evaluateFunction(final ParserRuleContext ctx, final Object value) {
	    Object returnValue = value;

	    if (returnValue instanceof FunctionDeclaration) {
		FunctionDeclaration funcDecl = (FunctionDeclaration) returnValue;
		if (funcDecl.getNumberOfParameters() == 0) {
		    returnValue = setupFunctionCall(ctx, funcDecl, null);
		}
	    }

	    if (returnValue != null && returnValue instanceof FunctionScope) {
		FunctionScope func = (FunctionScope) returnValue;

		boolean prevSilent = setSilent(true);
		pushScope(func);
		try {
		    returnValue = visit(func.getDeclaration().getFunctionBody());
		}
		finally {
		    popScope();
		    setSilent(prevSilent);
		}
	    }

	    return returnValue;
	}

	private LValueContext getLValue(CalcParser.VarContext var) {
	    return LValueContext.getLValue(this, var, currentContext);
	}

	private BigDecimal getDecimalValue(final ParserRuleContext ctx) {
	    return toDecimalValue(this, visit(ctx), mc, ctx);
	}

	private BigFraction getFractionValue(final ParserRuleContext ctx) {
	    return toFractionValue(this, visit(ctx), ctx);
	}

	private BigInteger getIntegerValue(final ParserRuleContext ctx) {
	    return toIntegerValue(this, visit(ctx), mc, ctx);
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

	    Object value = evaluateFunction(ctx, visit(ctx));

	    if (!allowNull)
		nullCheck(value, ctx);

	    return value == null ? "" : toStringValue(this, ctx, value, quote, false, separators, "");
	}

	private double getDoubleValue(final ParserRuleContext ctx) {
	    BigDecimal dec = getDecimalValue(ctx);

	    return dec.doubleValue();
	}

	protected int getIntValue(final ParserRuleContext ctx) {
	    return toIntValue(this, visit(ctx), mc, ctx);
	}

	private BigDecimal getDecimalTrigValue(final ParserRuleContext ctx) {
	    BigDecimal value = getDecimalValue(ctx);

	    if (settings.trigMode == TrigMode.DEGREES)
		value = value.multiply(piWorker.getPiOver180(), mc);

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
		throw new CalcExprException(ctx, "%calc#charsetError", charsetName, ExceptionUtil.toString(ex));
	    }
	}

	private int compareValues(final ParserRuleContext ctx1, final ParserRuleContext ctx2) {
	    return compareValues(ctx1, ctx2, false, false);
	}

	private int compareValues(final ParserRuleContext ctx1, final ParserRuleContext ctx2, final boolean strict, final boolean allowNulls) {
	    return CalcUtil.compareValues(this, ctx1, ctx2, mc, strict, allowNulls);
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
		dPrecision = toDecimalValue(this, lValue.getContextObject(false), mc, opt);
	    }

	    int precision = 0;
	    BigInteger ret = null;

	    try {
		precision = dPrecision.intValueExact();
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ctx, "%calc#precNotInteger", dPrecision);
	    }

	    if (precision == 0) {
		ret = setMathContext(MathContext.UNLIMITED);
	    }
	    else if (precision > 1 && precision <= MC_MAX_DIGITS.getPrecision()) {
		ret = setMathContext(new MathContext(precision));
	    }
	    else {
		throw new CalcExprException(ctx, "%calc#precOutOfRange", precision);
	    }

	    return ret;
	}

	@Override
	public Object visitDoubleDirective(CalcParser.DoubleDirectiveContext ctx) {
	    return setMathContext(MathContext.DECIMAL64);
	}

	@Override
	public Object visitFloatDirective(CalcParser.FloatDirectiveContext ctx) {
	    return setMathContext(MathContext.DECIMAL32);
	}

	@Override
	public Object visitDefaultDirective(CalcParser.DefaultDirectiveContext ctx) {
	    return setMathContext(MathContext.DECIMAL128);
	}

	@Override
	public Object visitUnlimitedDirective(CalcParser.UnlimitedDirectiveContext ctx) {
	    return setMathContext(MathContext.UNLIMITED);
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

	@Override
	public Object visitClearDirective(CalcParser.ClearDirectiveContext ctx) {
	    CalcParser.IdListContext idList = ctx.idList();
	    List<CalcParser.IdContext> ids;
	    int numberCleared = 0;

	    if (idList == null || (ids = idList.id()).isEmpty()) {
		Iterator<Map.Entry<String, Object>> iter = globals.map().entrySet().iterator();
		while (iter.hasNext()) {
		    Map.Entry<String, Object> entry = iter.next();
		    if (entry.getValue() instanceof PredefinedValue)
			continue;
		    if (entry.getKey().startsWith("$"))
			continue;
		    iter.remove();
		    numberCleared++;
		}
		displayActionMessage("%calc#varsAllCleared");
	    }
	    else {
		StringBuilder vars = new StringBuilder();
		int lastNamePos = 0;
		for (CalcParser.IdContext node : ids) {
		    String varName = node.getText();
		    if (varName.equals("<missing ID>"))
			continue;
		    if (globals.getValue(varName, settings.ignoreNameCase) instanceof PredefinedValue)
			continue;
		    numberCleared++;
		    globals.remove(varName, settings.ignoreNameCase);
		    if (vars.length() > 0)
			vars.append(", ");
		    lastNamePos = vars.length();
		    vars.append("'").append(varName).append("'");
		}
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
		displayActionMessage("%calc#varCleared", vars);
	    }

	    return BigInteger.valueOf(numberCleared);
	}

	@Override
	public Object visitVariablesDirective(CalcParser.VariablesDirectiveContext ctx) {
	    CalcParser.IdListContext idList = ctx.idList();
	    List<CalcParser.IdContext> ids;
	    Set<String> sortedKeys;

	    if (idList == null || (ids = idList.id()).isEmpty()) {
		sortedKeys = new TreeSet<>(globals.keySet());
	    }
	    else {
		sortedKeys = new TreeSet<>();
		ids = idList.id();
		for (CalcParser.IdContext node : ids) {
		    sortedKeys.add(node.getText());
		}
	    }

	    boolean oldMode = Calc.setResultsOnlyMode(false);
	    boolean replMode = Calc.getReplMode();

	    if (!replMode) {
		displayActionMessage("%calc#variables");
		displayActionMessage("%calc#varUnder1");
	    }
	    for (String key : sortedKeys) {
		Object value = globals.getValue(key, settings.ignoreNameCase);
		if (value instanceof PredefinedValue || key.startsWith("$")) {
		    continue;
		}
		else if (value instanceof FunctionDeclaration) {
		    FunctionDeclaration func = (FunctionDeclaration) value;
		    displayer.displayResult(func.getFullFunctionName(), getTreeText(func.getFunctionBody()));
		}
		else {
		    displayer.displayResult(key, toStringValue(this, ctx, value, settings.separatorMode));
		}
	    }
	    if (!replMode) {
		displayActionMessage("%calc#varUnder2");
	    }

	    Calc.setResultsOnlyMode(oldMode);

	    return BigInteger.valueOf(sortedKeys.size());
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
		return Calc.processString(contents, false);
	    }
	    catch (IOException ioe) {
		throw new CalcExprException(ctx, "%calc#ioError", ExceptionUtil.toString(ioe));
	    }
	}

	public void saveVariables(final ParserRuleContext ctx, final Path path, final Charset charset)
		throws IOException
	{
	    try (BufferedWriter writer = Files.newBufferedWriter(path, charset == null ? DEFAULT_CHARSET : charset)) {
		// Note: the keySet returned from ObjectScope is in order of declaration, which is important here, since we
		// must be able to read back the saved file and have the values computed to be the same as they are now.
		for (String key : globals.keySet()) {
		    Object value = globals.getValue(key, settings.ignoreNameCase);
		    if (value instanceof PredefinedValue || key.startsWith("$")) {
			continue;
		    }
		    else if (value instanceof FunctionDeclaration) {
			FunctionDeclaration func = (FunctionDeclaration) value;
			writer.write(String.format("def %1$s = %2$s", func.getFullFunctionName(), getTreeText(func.getFunctionBody())));
		    }
		    else {
			writer.write(String.format("%1$s = %2$s", key, toStringValue(this, ctx, value, settings.separatorMode)));
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
		throw new CalcExprException(ctx, "%calc#ioError", ExceptionUtil.toString(ioe));
	    }

	    return BigInteger.valueOf(globals.size());
	}


	private Boolean processModeOption(final CalcParser.ModeOptionContext ctx, final Deque<Boolean> stack, final UnaryOperator<Boolean> setOperator) {
	    boolean push = true;
	    boolean mode = false;
	    String option = null;

	    if (ctx.var() != null) {
		CalcParser.VarContext var = ctx.var();
		// could be boolean, or string mode value
		LValueContext lValue = getLValue(var);
		Object modeObject = evaluateFunction(ctx, lValue.getContextObject(false));
		if (modeObject instanceof Boolean) {
		    mode = ((Boolean) modeObject).booleanValue();
		}
		else {
		    option = toStringValue(this, var, modeObject, false, false, false, "");
		}
	    }
	    else {
		option = ctx.getText();
	    }
	    if (option != null) {
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
			throw new CalcExprException(ctx, "%calc#modeError", option);
		}
	    }

	    // Run the process to actually set the new mode
	    boolean previousMode = setOperator.apply(mode);

	    if (push)
		stack.push(previousMode);

	    return Boolean.valueOf(mode);
	}

	@Override
	public Object visitTimingDirective(CalcParser.TimingDirectiveContext ctx) {
	    return processModeOption(ctx.modeOption(), timingModeStack, mode -> {
		return setTimingMode(mode);
	    });
	}

	@Override
	public Object visitDebugDirective(CalcParser.DebugDirectiveContext ctx) {
	    return processModeOption(ctx.modeOption(), debugModeStack, mode -> {
		return setDebugMode(mode);
	    });
	}

	@Override
	public Object visitRationalDirective(CalcParser.RationalDirectiveContext ctx) {
	    return processModeOption(ctx.modeOption(), rationalModeStack, mode -> {
		return setRationalMode(mode);
	    });
	}

	@Override
	public Object visitResultsOnlyDirective(CalcParser.ResultsOnlyDirectiveContext ctx) {
	    return processModeOption(ctx.modeOption(), resultsOnlyModeStack, mode -> {
		return Calc.setResultsOnlyMode(mode);
	    });
	}

	@Override
	public Object visitQuietDirective(CalcParser.QuietDirectiveContext ctx) {
	    return processModeOption(ctx.modeOption(), quietModeStack, mode -> {
		return Calc.setQuietMode(mode);
	    });
	}

	@Override
	public Object visitSeparatorsDirective(CalcParser.SeparatorsDirectiveContext ctx) {
	    return processModeOption(ctx.modeOption(), separatorModeStack, mode -> {
		return setSeparatorMode(mode);
	    });
	}

	@Override
	public Object visitIgnoreCaseDirective(CalcParser.IgnoreCaseDirectiveContext ctx) {
	    return processModeOption(ctx.modeOption(), ignoreCaseModeStack, mode -> {
		return setIgnoreCaseMode(mode);
	    });
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
		    node = ((ParserRuleContext)node).children.get(0);
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
	    Object result               = evaluateFunction(expr, visit(expr));
	    String resultString         = "";

	    BigInteger iValue = null;
	    BigDecimal dValue = null;

	    int precision = Integer.MIN_VALUE;
	    boolean separators = false;
	    boolean addQuotes = false;
	    char signChar = ' ';

	    TerminalNode formatNode = ctx.FORMAT();
	    String format           = formatNode == null ? "" : " " + formatNode.getText();
	    String exprString       = String.format("%1$s%2$s", getTreeText(ctx.expr()), format);

	    // Some formats allow a precision to be given ('%', '$', and 'd' for instance)
	    // but some others (like 't') allow a second alpha as well
	    if (format.length() > 3) {
		int index = 2;
		char ch;
		while (((ch = format.charAt(index)) >= '0' && ch <= '9') || (ch == '-' || ch == '+'))
		    index++;
		if (index > 2) {
		    String num = format.substring(2, index);
		    if (num.charAt(0) == '-' || num.charAt(0) == '+')
			signChar = num.charAt(0);
		    precision = Integer.parseInt(num);
		}
	    }
	    separators = format.indexOf(',') >= 0 || settings.separatorMode;


	    if (result != null && !format.isEmpty()) {
		char formatChar = format.charAt(format.length() - 1);

		if ((result instanceof Scope) && (formatChar != 'j' && formatChar != 'J')) {
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
			char meridianFlag = format.charAt(format.length() - 2);
			// Value will be nanoseconds since midnight
			valueBuf.append("h'");
			iValue = toIntegerValue(this, result, mc, ctx);
			valueBuf.append(NumericUtil.convertToTime(iValue.longValue(), meridianFlag));
			valueBuf.append('\'');
			break;
		    case 'T':
			toUpperCase = true;
			// fall through
		    case 't':
			char durationUnit = format.charAt(format.length() - 2);
			// Value will be nanoseconds
			valueBuf.append("t'");
			iValue = toIntegerValue(this, result, mc, ctx);
			valueBuf.append(NumericUtil.convertToDuration(iValue, durationUnit, mcDivide, precision));
			valueBuf.append('\'');
			break;

		    case 'U':
		    case 'u':
			toUpperCase = true;
			valueBuf.append(toStringValue(this, ctx, result, false));
			break;
		    case 'L':
		    case 'l':
			toLowerCase = true;
			valueBuf.append(toStringValue(this, ctx, result, false));
			break;

		    case 'C':
		    case 'c':
			iValue = toIntegerValue(this, result, mc, ctx);
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
			    dValue = toDecimalValue(this, result, mc, ctx);

			if (precision != Integer.MIN_VALUE) {
			    dValue = MathUtil.round(dValue, precision);
			}
			valueBuf.append(formatWithSeparators(dValue, separators));
			break;

		    // @E = US format: MM/dd/yyyy
		    // @e = ISO format: yyyy-MM-dd
		    case 'E':
		    case 'e':
			char dateChar = (formatChar == 'E') ? 'D' : 'd';
			valueBuf.append(dateChar).append('\'');
			iValue = toIntegerValue(this, result, mc, ctx);
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
			valueBuf.append('\n');
			valueBuf.append(toStringValue(this, ctx, result, true, true, separators, ""));
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
			    iValue = toIntegerValue(this, result, mc, ctx);
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
			    iValue = toIntegerValue(this, result, mc, ctx);
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
			    iValue = toIntegerValue(this, result, mc, ctx);
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
			iValue = toIntegerValue(this, result, mc, ctx);
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
			iValue = toIntegerValue(this, result, mc, ctx);
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
			iValue = toIntegerValue(this, result, mc, ctx);
			try {
			    long lValue = iValue.longValueExact();
			    NumericUtil.convertToWords(lValue, valueBuf);
			}
			catch (IllegalArgumentException iae) {
			    throw new CalcExprException(iae, ctx);
			}
			break;

		    case '%':
			dValue = toDecimalValue(this, result, mc, ctx);
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
			dValue = toDecimalValue(this, result, mc, ctx);
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
			String stringValue = toStringValue(this, ctx, result, false, false, separators, "");
			switch (signChar) {
			    case '+':	/* center - positive width puts extra spaces on left always */
				CharUtil.padToWidth(valueBuf, stringValue, precision, CharUtil.Justification.CENTER);
				break;
			    case '-':	/* right-justify (spaces on left) */
				CharUtil.padToWidth(valueBuf, stringValue, Math.abs(precision), CharUtil.Justification.RIGHT);
				break;
			    default:	/* left-justify (spaces on right) */
				CharUtil.padToWidth(valueBuf, stringValue, precision, CharUtil.Justification.LEFT);
				break;
			}
			addQuotes = true;
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
		    resultString = toStringValue(this, ctx, result, separators);
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
		}

		@Override
		public Object apply(final Object value) {
		    if (localVarName != null)
			currentScope.setValue(localVarName, settings.ignoreNameCase, value);
		    return visit(block);
		}

		public void finish() {
		    // Make sure the local loop var gets removed, even on exceptions
		    currentScope.remove(localVarName, settings.ignoreNameCase);
		}
	}

	private Object iterateOverDotRange(
		final CalcParser.ExprListContext exprList,
		final CalcParser.DotRangeContext dotRange,
		final Function<Object, Object> visitor,
		final boolean allowSingle)
	{
	    List<CalcParser.ExprContext> exprs;

	    Iterator<Object> iter = null;
	    java.util.stream.IntStream codePoints  = null;

	    boolean stepWise = false;

	    BigDecimal dStart = BigDecimal.ONE;
	    BigDecimal dStop  = BigDecimal.ONE;
	    BigDecimal dStep  = BigDecimal.ONE;

	    int start = 1;
	    int stop  = 1;
	    int step  = 1;

	    Object lastValue = null;

	    if (exprList != null) {
		// This is only true if we have "expr , expr (, expr)*"
		// or more than one separated by commas
		exprs = exprList.expr();
	    }
	    else {
		exprs = dotRange.expr();
		stepWise = true;

		if (exprs.size() == 0) {
		    // This is the case of '(' ')', or an empty list
		    return lastValue;
		}
		else if (exprs.size() == 1) {
		    // number of times, starting from 1
		    if (allowSingle) {
			// or it could be an array, object, or string to iterate over
			Object obj = visit(exprs.get(0));
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
			}
		    }
		    else {
			dStop = getDecimalValue(exprs.get(0));
		    }
		}
		else if (exprs.size() == 2) {
		    if (dotRange != null && dotRange.DOTS() != null) {
			// start .. stop
			dStart = getDecimalValue(exprs.get(0));
			dStop  = getDecimalValue(exprs.get(1));
		    }
		    else {
			// stop, step
			dStop = getDecimalValue(exprs.get(0));
			dStep = getDecimalValue(exprs.get(1));
		    }
		}
		else {
		    // start .. stop, step
		    dStart = getDecimalValue(exprs.get(0));
		    dStop  = getDecimalValue(exprs.get(1));
		    dStep  = getDecimalValue(exprs.get(2));
		}
	    }

	    try {
		if (stepWise) {
		    // Try to convert loop values to exact integers if possible
		    try {
			start = dStart.intValueExact();
			stop  = dStop.intValueExact();
			step  = dStep.intValueExact();

			if (step == 0)
			    throw new CalcExprException("%calc#infLoopStepZero", dotRange);
			else if (step < 0) {
			    for (int loopIndex = start; loopIndex >= stop; loopIndex += step) {
				lastValue = visitor.apply(loopIndex);
			    }
			}
			else {
			    for (int loopIndex = start; loopIndex <= stop; loopIndex += step) {
				lastValue = visitor.apply(loopIndex);
			    }
			}
		    }
		    catch (ArithmeticException ae) {
			// This means we stubbornly have fractional values, so use as such
			int sign = dStep.signum();
			if (sign == 0)
			    throw new CalcExprException("%calc#infLoopStepZero", dotRange);
			else if (sign < 0) {
			    for (BigDecimal loopIndex = dStart; loopIndex.compareTo(dStop) >= 0; loopIndex = loopIndex.add(dStep)) {
				lastValue = visitor.apply(loopIndex);
			    }
			}
			else {
			    for (BigDecimal loopIndex = dStart; loopIndex.compareTo(dStop) <= 0; loopIndex = loopIndex.add(dStep)) {
				lastValue = visitor.apply(loopIndex);
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
	    }
	    finally {
		return lastValue;
	    }
	}

	@Override
	public Object visitLoopStmt(CalcParser.LoopStmtContext ctx) {
	    CalcParser.LoopCtlContext ctlCtx    = ctx.loopCtl();
	    CalcParser.StmtBlockContext block   = ctx.stmtBlock();
	    CalcParser.ExprListContext exprList = ctlCtx.exprList();
	    CalcParser.DotRangeContext dotCtx   = ctlCtx.dotRange();

	    TerminalNode localVar = ctx.LOCALVAR();
	    String localVarName   = localVar != null ? localVar.getText() : "$_";

	    pushScope(new LoopScope());
	    LoopVisitor visitor = new LoopVisitor(block, localVarName);

	    Object value = null;

	    try {
		value = iterateOverDotRange(exprList, dotCtx, visitor, true);
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

	@Override
	public Object visitCaseStmt(CalcParser.CaseStmtContext ctx) {
	    Object caseValue = visit(ctx.expr());
	    List<CalcParser.CaseBlockContext> blocks = ctx.caseBlock();
	    CalcParser.CaseBlockContext defaultCtx = null;
	    CaseScope scope = new CaseScope();

	    for (CalcParser.CaseBlockContext cbCtx : blocks) {
		List<CalcParser.ExprListContext> exprLists = cbCtx.caseExprList().exprList();
		if (cbCtx.caseExprList().K_DEFAULT() != null) {
		    defaultCtx = cbCtx;
		}
		if (exprLists != null) {
		    for (CalcParser.ExprListContext exprListCtx : exprLists) {
			for (CalcParser.ExprContext exprCtx : exprListCtx.expr()) {
			    Object blockValue = visit(exprCtx);
			    if (CalcUtil.compareValues(this, ctx, cbCtx, caseValue, blockValue, mc, false, true, false, false) == 0) {
				Object returnValue = null;
				pushScope(scope);
				try {
				    returnValue = visit(cbCtx.stmtBlock());
				}
				finally {
				    popScope();
				}
				return returnValue;
			    }
			}
		    }
		}
	    }

	    if (defaultCtx != null) {
		Object returnValue = null;
		pushScope(scope);
		try {
		    returnValue = visit(defaultCtx.stmtBlock());
		}
		finally {
		    popScope();
		}
		return returnValue;
	    }

	    return null;
	}

	@Override
	public Object visitStmtBlock(CalcParser.StmtBlockContext ctx) {
	    Object returnValue = null;

	    for (CalcParser.StmtOrExprContext child : ctx.stmtOrExpr()) {
		if (child.emptyStmt() == null) {
		    returnValue = visit(child);
		}
	    }

	    return returnValue;
	}

	@Override
	public Object visitDefineStmt(CalcParser.DefineStmtContext ctx) {
	    String functionName = ctx.id().getText();

	    CalcParser.StmtBlockContext functionBody       = ctx.stmtBlock();
	    CalcParser.FormalParamListContext formalParams = ctx.formalParamList();

	    String paramString = formalParams == null ? "" : getTreeText(formalParams);
	    List<CalcParser.FormalParamContext> paramVars = formalParams == null ? null : formalParams.formalParam();
	    FunctionDeclaration func = new FunctionDeclaration(functionName, functionBody);

	    if (formalParams != null) {
		for (CalcParser.FormalParamContext paramVar : formalParams.formalParam()) {
		    String paramName = paramVar.LOCALVAR().getText();
		    func.defineParameter(paramVar, paramName, paramVar.expr());
		}
	    }

	    currentScope.setValue(functionName, settings.ignoreNameCase, func);

	    displayActionMessage("%calc#defining", func.getFullFunctionName(), getTreeText(functionBody));

	    return functionBody;
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
		Object value = visit(pairCtx.expr());
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
		    Object value = visit(expr);
		    list.add(value);
		}
	   }
	   return list;
	}

	@Override
	public Object visitVarExpr(CalcParser.VarExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());
	    return evaluateFunction(ctx, lValue.getContextObject());
	}

	@Override
	public Object visitPostIncOpExpr(CalcParser.PostIncOpExprContext ctx) {
	    CalcParser.VarContext var = ctx.var();
	    LValueContext lValue = getLValue(var);
	    Object value = evaluateFunction(var, lValue.getContextObject());
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
	    else {
		BigDecimal dValue = toDecimalValue(this, value, mc, var);
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
	    Object value = evaluateFunction(var, lValue.getContextObject());
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
	    else {
		BigDecimal dValue = toDecimalValue(this, value, mc, var);

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
	    Object e = visit(expr);

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
	    else {
		BigDecimal d = toDecimalValue(this, e, mc, expr);

		switch (op) {
		    case "+":
		    case "\u2795":
			// Interestingly, this operation can change the value, if the previous
			// value was not to the specified precision.
			return fixup(d.plus(mc));
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
	    double exp = getDoubleValue(ctx.expr(1));

	    if (settings.rationalMode) {
		if (Math.floor(exp) == exp && !Double.isInfinite(exp)) {
		    BigFraction f = getFractionValue(ctx.expr(0));
		    return f.pow((int)exp);
		}
	    }

	    BigDecimal base = getDecimalValue(ctx.expr(0));
	    return MathUtil.pow(base, exp, mc);
	}

	@Override
	public Object visitPowerNExpr(CalcParser.PowerNExprContext ctx) {
	    String power = ctx.POWERS().getText();

	    if (settings.rationalMode) {
		BigFraction base = getFractionValue(ctx.expr());
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

		return base.pow(exp);
	    }
	    else {
		BigDecimal base = getDecimalValue(ctx.expr());
		double exp;

		switch (power) {
		    case "\u2070":
			exp = 0.0d;
			break;
		    case "\u00B9":
			exp = 1.0d;
			break;
		    case "\u00B2":
			exp = 2.0d;
			break;
		    case "\u00B3":
			exp = 3.0d;
			break;
		    case "\u2074":
			exp = 4.0d;
			break;
		    case "\u2075":
			exp = 5.0d;
			break;
		    case "\u2076":
			exp = 6.0d;
			break;
		    case "\u2077":
			exp = 7.0d;
			break;
		    case "\u2078":
			exp = 8.0d;
			break;
		    case "\u2079":
			exp = 9.0d;
			break;
		    default:
			throw new UnknownOpException(power, ctx);
		}

		return MathUtil.pow(base, exp, mc);
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
			    // ??? I think there is never any remainder dividing a fraction by a fraction
			    return BigFraction.ZERO;
			default:
			    throw new UnknownOpException(op, ctx);
		    }
		}
		else {
		    BigDecimal d1 = toDecimalValue(this, e1, mc, ctx1);
		    BigDecimal d2 = toDecimalValue(this, e2, mc, ctx2);

		    switch (op) {
			case "*":
			case "\u00D7":
			case "\u2217":
			case "\u2715":
			case "\u2716":
			    return fixup(d1.multiply(d2, mc));
			case "/":
			case "\u00F7":
			case "\u2215":
			case "\u2797":
			    return fixup(d1.divide(d2, mcDivide));
			case "\\":
			    return fixup(d1.divideToIntegralValue(d2, mcDivide));
			case "%":
			    return fixup(d1.remainder(d2, mcDivide));
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
		    return addOp(this, e1, e2, ctx1, ctx2, mc, settings.rationalMode);
		case "-":
		case "\u2212":
		case "\u2796":
		    if (settings.rationalMode) {
			BigFraction f1 = toFractionValue(this, e1, ctx1);
			BigFraction f2 = toFractionValue(this, e2, ctx2);

			return f1.subtract(f2);
		    }
		    else {
			BigDecimal d1 = toDecimalValue(this, e1, mc, ctx1);
			BigDecimal d2 = toDecimalValue(this, e2, mc, ctx2);

			return fixup(d1.subtract(d2, mc));
		    }
		default:
		    throw new UnknownOpException(op, ctx);
	    }
	}

	@Override
	public Object visitAbsExpr(CalcParser.AbsExprContext ctx) {
	    if (settings.rationalMode) {
		BigFraction f = getFractionValue(ctx.expr1().expr());
		return f.abs();
	    }
	    else {
		BigDecimal e = getDecimalValue(ctx.expr1().expr());
		return e.abs();
	    }
	}

	@Override
	public Object visitSinExpr(CalcParser.SinExprContext ctx) {
	    BigDecimal e = getDecimalTrigValue(ctx.expr1().expr());

	    return MathUtil.sin(e, mc);
	}

	@Override
	public Object visitCosExpr(CalcParser.CosExprContext ctx) {
	    BigDecimal e = getDecimalTrigValue(ctx.expr1().expr());

	    return MathUtil.cos(e, mc);
	}

	@Override
	public Object visitTanExpr(CalcParser.TanExprContext ctx) {
	    BigDecimal e = getDecimalTrigValue(ctx.expr1().expr());

	    return MathUtil.tan(e, mc);
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
	    double y = getDoubleValue(e2ctx.expr(0));
	    double x = getDoubleValue(e2ctx.expr(1));

	    return new BigDecimal(Math.atan2(y, x), MC_DOUBLE);
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
	    try {
		return MathUtil.sqrt(getDecimalValue(ctx.expr1().expr()), mc);
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, ctx);
	    }
	}

	@Override
	public Object visitCbrtExpr(CalcParser.CbrtExprContext ctx) {
	    return MathUtil.cbrt(getDecimalValue(ctx.expr1().expr()), mc);
	}

	@Override
	public Object visitFortExpr(CalcParser.FortExprContext ctx) {
	    try {
		return MathUtil.sqrt(MathUtil.sqrt(getDecimalValue(ctx.expr1().expr()), mc), mc);
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, ctx);
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
		return MathUtil.ln2(d, mc);
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, ctx);
	    }
	}

	@Override
	public Object visitLnExpr(CalcParser.LnExprContext ctx) {
	    BigDecimal d = getDecimalValue(ctx.expr1().expr());

	    try {
		return MathUtil.ln(d, mc);
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, ctx);
	    }
	}

	@Override
	public Object visitEPowerExpr(CalcParser.EPowerExprContext ctx) {
	    BigDecimal e = getDecimalValue(ctx.expr1().expr());

	    return MathUtil.ePower(e, mc);
	}

	@Override
	public Object visitTenPowerExpr(CalcParser.TenPowerExprContext ctx) {
	    BigDecimal e = getDecimalValue(ctx.expr1().expr());

	    return MathUtil.tenPower(e, mc);
	}

	@Override
	public Object visitSignumExpr(CalcParser.SignumExprContext ctx) {
	    int signum;

	    if (settings.rationalMode) {
		BigFraction f = getFractionValue(ctx.expr1().expr());
		signum = f.signum();
	    }
	    else {
		BigDecimal e = getDecimalValue(ctx.expr1().expr());
		signum = e.signum();
	    }
	    return BigInteger.valueOf(signum);
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
		return iterateOverDotRange(null, dotRange, visitor, false);
	    }
	    else {
		Object obj = visit(ctx.expr1().expr());

		// This calculates the recursive size of objects and arrays
		// so, use "scale" to calculate the non-recursive size
		return BigInteger.valueOf((long) length(this, obj, ctx, true));
	    }
	}

	@Override
	public Object visitScaleExpr(CalcParser.ScaleExprContext ctx) {
	    Object obj = visit(ctx.expr1().expr());

	    // This returns the non-recursive size of objects and arrays
	    // so, use "length" to calculate the recursive (full) size
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
	public Object visitIsNullExpr(CalcParser.IsNullExprContext ctx) {
	    Object obj = visit(ctx.expr1().expr());
	    return Boolean.valueOf(obj == null);
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
		objectList.add(toDecimalValue(this, value, mc, ctx));
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
		buildFlatMap(eCtx, visit(eCtx), objects, isString, forJoin);
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
		    if (value.compareTo(maxString) > 0)
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
		    if (value.compareTo(minString) < 0)
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
		buildFlatMap(eCtx, visit(eCtx), objects, true, true);
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
	    String stringValue;
	    String searchValue;

	    int ret = -1;
	    if (e2ctx != null) {
		stringValue = getStringValue(e2ctx.expr(0));
		searchValue = getStringValue(e2ctx.expr(1));

		ret = stringValue.indexOf(searchValue);
		if (ret < 0)
		    return null;
	    }
	    else {
		CalcParser.Expr3Context e3ctx   = ctx.expr3();
		CalcParser.ExprContext indexCtx = e3ctx.expr(2);
		stringValue = getStringValue(e3ctx.expr(0));
		searchValue = getStringValue(e3ctx.expr(1));
		int index   = indexCtx == null ? 0 : getIntValue(indexCtx);

		if (index < 0) {
		    int stringLen = stringValue.length();
		    ret = stringValue.lastIndexOf(searchValue, stringLen + index);
		    if (ret < 0)
			return null;
		    ret -= stringLen;
		}
		else {
		    ret = stringValue.indexOf(searchValue, index);
		    if (ret < 0)
			return null;
		}
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

	    String stringValue = toStringValue(this, ctx, value, false, false, settings.separatorMode, "");

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
		    Object optionObject = lValue.getContextObject(false);
		    option = optionObject == null ? "" : toStringValue(this, var, optionObject, true, false, false, "");
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

	    value = evaluateFunction(valueCtx, visit(valueCtx));

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

	    Object source = evaluateFunction(objCtx, visit(objCtx));

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
			Object value = evaluateFunction(valueCtx, visit(valueCtx));
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
		    return CalcUtil.compareValues(visitor, ctx, ctx, o1, o2, mc, false, true, ignoreCase, true);
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
	public Object visitFillExpr(CalcParser.FillExprContext ctx) {
	    CalcParser.FillArgsContext fillCtx  = ctx.fillArgs();
	    CalcParser.VarContext varCtx        = fillCtx.var();
	    LValueContext lValue                = getLValue(varCtx);
	    Object value		        = lValue.getContextObject();
	    List<CalcParser.ExprContext> exprs  = fillCtx.expr();
	    CalcParser.ExprContext fillExpr     = exprs.get(0);

	    Object fillValue = evaluateFunction(fillExpr, visit(fillExpr));
	    int start  = 0;
	    int length = 0;

	    if (exprs.size() == 2) {
		length = getIntValue(exprs.get(1));
	    }
	    else {
		start  = getIntValue(exprs.get(1));
		length = getIntValue(exprs.get(2));
	    }

	    if (value instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<Object> list = (ArrayScope<Object>) value;
		for (int index = start; index < (start + length); index++) {
		    list.setValue(index, fillValue);
		}
	    }
	    else if (value instanceof String) {
		StringBuilder buf = new StringBuilder((String) value);
		if (buf.length() < start + length) {
		    buf.setLength(start + length);
		}

		char fillChar = '\0';
		if (fillValue != null) {
		    if (fillValue instanceof String) {
			String fillString = (String) fillValue;
			if (fillString.length() != 1) {
			    throw new CalcExprException("%calc#fillOneCharInt", ctx);
			}
			fillChar = fillString.charAt(0);
		    }
		    else if (fillValue instanceof Number) {
			int intValue = ((Number) fillValue).intValue();
			if (intValue < 0 || intValue > Short.MAX_VALUE) {
			    throw new CalcExprException("%calc#fillOneCharInt", ctx);
			}
			fillChar = (char) intValue;
		    }
		}
		for (int index = start; index < (start + length); index++) {
		    buf.setCharAt(index, fillChar);
		}
		value = buf.toString();
	    }
	    else {
		throw new CalcExprException("%calc#fillTargetWrongType", ctx);
	    }

	    return lValue.putContextObject(this, value);
	}

	@Override
	public Object visitTrimExpr(CalcParser.TrimExprContext ctx) {
	    String stringValue = getStringValue(ctx.expr1().expr());
	    String result;

	    if (ctx.K_TRIM() != null) {
		result = stringValue.trim();
	    }
	    else if (ctx.K_LTRIM() != null) {
		result = CharUtil.ltrim(stringValue);
	    }
	    else {
		result = CharUtil.rtrim(stringValue);
	    }

	    return result;
	}

	@Override
	public Object visitFibExpr(CalcParser.FibExprContext ctx) {
	    BigDecimal e = getDecimalValue(ctx.expr1().expr());

	    return MathUtil.fib(e);
	}

	@Override
	public Object visitBernExpr(CalcParser.BernExprContext ctx) {
	    int n = getIntValue(ctx.expr1().expr());

	    return MathUtil.bernoulli(n, mcDivide, settings.rationalMode);
	}

	@Override
	public Object visitFracExpr(CalcParser.FracExprContext ctx) {
	    try {
		CalcParser.Expr1Context expr1 = ctx.expr1();
		if (expr1 != null) {
		    return BigFraction.valueOf(getStringValue(expr1.expr(), false, false, false));
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
			objectList.add(toStringValue(this, ctx, value, false, false, false, ""));
			break;
		    case DECIMAL:
			objectList.add(toDecimalValue(this, value, mc, ctx));
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
		buildValueList(exprCtx, visit(exprCtx), objects, conversion);
	    }

	    return objects;
	}

	@Override
	public Object visitExecExpr(CalcParser.ExecExprContext ctx) {
	    List<CalcParser.ExprContext> exprs = ctx.exprN().exprList().expr();
	    List<Object> objects = buildValueList(exprs, Conversion.STRING);
	    String[] args = new String[objects.size()];
	    for (int i = 0; i < objects.size(); i++) {
		args[i] = (String) objects.get(i);
	    }

	    RunCommand cmd = new RunCommand(args);
	    StringBuilder result = new StringBuilder();
	    int retCode = cmd.runToCompletion(result);

	    return result.toString();
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
				: toDecimalValue(CalcObjectVisitor.this, value, mc, ctx);
			sum = sum.add(dec, mc);
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
		sum = iterateOverDotRange(null, dotRange, sumVisitor, false);
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
				: toDecimalValue(CalcObjectVisitor.this, value, mc, ctx);
			product = product.multiply(dec, mc);
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
		product = iterateOverDotRange(null, dotRange, productVisitor, false);
	    }

	    return product;
	}

	@Override
	public Object visitFactorialExpr(CalcParser.FactorialExprContext ctx) {
	    BigDecimal e = getDecimalValue(ctx.expr());

	    return MathUtil.factorial(e, mc);
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

	@Override
	public Object visitCompareExpr(CalcParser.CompareExprContext ctx) {
	    ParserRuleContext expr1 = ctx.expr(0);
	    ParserRuleContext expr2 = ctx.expr(1);

	    int cmp = compareValues(expr1, expr2);

	    boolean result;

	    String op = ctx.COMPARE_OP().getText();
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
		default:
		    throw new UnknownOpException(op, ctx);
	    }

	    return Boolean.valueOf(result);
	}

	@Override
	public Object visitEqualExpr(CalcParser.EqualExprContext ctx) {
	    ParserRuleContext expr1 = ctx.expr(0);
	    ParserRuleContext expr2 = ctx.expr(1);

	    int cmp = 0;
	    boolean result;

	    String op = ctx.EQUAL_OP().getText();
	    switch (op) {
		case "===":
		case "\u2A76":
		case "\u2261": // IDENTICAL TO
		case "!==":
		case "\u2262": // NOT IDENTICAL
		    cmp = compareValues(expr1, expr2, true, true);
		    break;
		case "==":
		case "\u2A75":
		case "!=":
		case "\u2260": // NOT EQUAL
		    cmp = compareValues(expr1, expr2, false, true);
		    break;
	    }

	    switch (op) {
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
		case "\u2260": // NOT EQUAL
		    result = (cmp != 0);
		    break;
		default:
		    throw new UnknownOpException(op, ctx);
	    }

	    return Boolean.valueOf(result);
	}

	@Override
	public Object visitBitExpr(CalcParser.BitExprContext ctx) {
	    BigInteger e1 = getIntegerValue(ctx.expr(0));
	    BigInteger e2 = getIntegerValue(ctx.expr(1));

	    String op = ctx.BIT_OP().getText();

	    return bitOp(e1, e2, op, ctx);
	}

	@Override
	public Object visitBooleanExpr(CalcParser.BooleanExprContext ctx) {
	    Boolean b1 = getBooleanValue(ctx.expr(0));
	    String op = ctx.BOOL_OP().getText();

	    switch (op) {
		case "&&":
		case "\u2227":
		    // Due to the short-circuit nature of this operator, the second expression
		    // is only evaluated if necessary
		    if (!b1)
			return Boolean.FALSE;
		    break;

		case "||":
		case "\u2228":
		    // Due to the short-circuit nature of this operator, the second expression
		    // is only evaluated if necessary
		    if (b1)
			return Boolean.TRUE;
		    break;

		case "^^":
		case "\u22BB":
		    // Unfortunately, there is no possibility of short-circuit evaluation
		    // for this operator -- either first value could produce either result
		    Boolean b2 = getBooleanValue(ctx.expr(1));

		    return ((b1 && b2) || (!b1 && !b2)) ? Boolean.FALSE : Boolean.TRUE;

		default:
		    throw new UnknownOpException(op, ctx);
	    }

	    // For the short-circuit operators, this is the result if the first is not conclusive
	    return getBooleanValue(ctx.expr(1));
	}

	@Override
	public Object visitStringValue(CalcParser.StringValueContext ctx) {
	    String value = ctx.STRING().getText();

	    return getRawString(value);
	}

	@Override
	public Object visitIStringValue(CalcParser.IStringValueContext ctx) {
	    return getIStringValue(this, ctx.ISTRING(), ctx);
	}

	@Override
	public Object visitNumberValue(CalcParser.NumberValueContext ctx) {
	    return new BigDecimal(ctx.NUMBER().getText());
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
		return fraction.toDecimal(mc);
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

	    return visit(ifExpr ? ctx.expr(1) : ctx.expr(2));
	}

	@Override
	public Object visitAddAssignExpr(CalcParser.AddAssignExprContext ctx) {
	    CalcParser.VarContext varCtx = ctx.var();
	    ParserRuleContext exprCtx    = ctx.expr();

	    LValueContext lValue = getLValue(varCtx);
	    Object result;

	    String op = ctx.ADD_ASSIGN().getText();
	    Object e1 = lValue.getContextObject();
	    Object e2 = visit(exprCtx);

	    switch (op) {
		case "+=":
		case "\u2795=":
		    result = addOp(this, e1, e2, varCtx, exprCtx, mc, settings.rationalMode);
		    break;
		case "-=":
		case "\u2212=":
		case "\u2796=":
		    if (settings.rationalMode) {
			BigFraction f1 = toFractionValue(this, e1, varCtx);
			BigFraction f2 = toFractionValue(this, e2, exprCtx);

			result = f1.subtract(f2);
		    }
		    else {
			BigDecimal d1 = toDecimalValue(this, e1, mc, varCtx);
			BigDecimal d2 = toDecimalValue(this, e2, mc, exprCtx);

			result = d1.subtract(d2, mc);
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

	    BigDecimal base = toDecimalValue(this, lValue.getContextObject(), mc, ctx);
	    double exp      = getDoubleValue(ctx.expr());

	    return lValue.putContextObject(this, MathUtil.pow(base, exp, mc));
	}

	@Override
	public Object visitMultAssignExpr(CalcParser.MultAssignExprContext ctx) {
	    CalcParser.VarContext varCtx = ctx.var();
	    ParserRuleContext exprCtx    = ctx.expr();

	    LValueContext lValue = getLValue(varCtx);
	    Object result;

	    String op = ctx.MULT_ASSIGN().getText();
	    Object e1 = lValue.getContextObject();
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
		else {
		    BigDecimal d1 = toDecimalValue(this, e1, mc, varCtx);
		    BigDecimal d2 = toDecimalValue(this, e2, mc, exprCtx);

		    switch (op) {
			case "*=":
			case "\u00D7=":
			case "\u2217=":
			case "\u2715=":
			case "\u2716=":
			    result = d1.multiply(d2, mc);
			    break;
			case "/=":
			case "\u00F7=":
			case "\u2215=":
			case "\u2797=":
			    result = d1.divide(d2, mcDivide);
			    break;
			case "\\=":
			    result = d1.divideToIntegralValue(d2, mcDivide);
			    break;
			case "%=":
			    result = d1.remainder(d2, mcDivide);
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

	    BigInteger i1 = toIntegerValue(this, lValue.getContextObject(), mc, ctx);
	    BigInteger i2 = getIntegerValue(ctx.expr());

	    String op = ctx.BIT_ASSIGN().getText();
	    // Strip off the trailing '=' of the operator
	    op = op.substring(0, op.length() - 1);

	    return lValue.putContextObject(this, bitOp(i1, i2, op, ctx));
	}

	@Override
	public Object visitShiftAssignExpr(CalcParser.ShiftAssignExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());

	    BigInteger i1 = toIntegerValue(this, lValue.getContextObject(), mc, ctx);
	    int e2        = getIntValue(ctx.expr());

	    String op = ctx.SHIFT_ASSIGN().getText();
	    // Strip off the trailing "="
	    op = op.substring(0, op.length() - 1);

	    return lValue.putContextObject(this, shiftOp(i1, e2, op, ctx));
	}

	@Override
	public Object visitAssignExpr(CalcParser.AssignExprContext ctx) {
	    Object value = visit(ctx.expr());

	    LValueContext lValue = getLValue(ctx.var());
	    return lValue.putContextObject(this, value);
	}
}
