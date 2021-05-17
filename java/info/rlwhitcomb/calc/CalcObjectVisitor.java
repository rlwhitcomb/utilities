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
 */
package info.rlwhitcomb.calc;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.UnaryOperator;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import info.rlwhitcomb.util.BigFraction;
import static info.rlwhitcomb.calc.CalcUtil.*;
import info.rlwhitcomb.util.CharUtil;
import static info.rlwhitcomb.util.ConsoleColor.Code.*;
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

	/** Scale for double operations. */
	private static final MathContext mcDouble = MathContext.DECIMAL64;

	/** MathContext to use for pi/e calculations when regular context is unlimited.
	 * Note: precision is arbitrary, but {@link MathUtil#pi} is limited to ~12,500 digits.
	 */
	private static final MathContext mcMaxDigits = new MathContext(12000);

	/** Initialization flag -- delays print until constructor is finished.  */
	private boolean initialized = false;

	/** Note: the precision will be determined by the number of digits desired. */
	private MathContext mc;

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
		/** Silent flag (set to true) while evaluating nested expressions (or via :quiet directive). */
		boolean silent;

		/**
		 * Construct default settings, including the command-line "-rational" flag.
		 *
		 * @param rational The initial rational mode setting.
		 */
		public Settings(boolean rational) {
		    trigMode     = TrigMode.RADIANS;
		    units        = RangeMode.MIXED;
		    rationalMode = rational;
		    silent       = false;
		}

		/**
		 * Copy constructor - make a copy of another {@code Settings} object.
		 *
		 * @param otherSettings The object to copy.
		 */
		public Settings(Settings otherSettings) {
		    this.trigMode     = otherSettings.trigMode;
		    this.units        = otherSettings.units;
		    this.rationalMode = otherSettings.rationalMode;
		    this.silent       = otherSettings.silent;
		}
	}

	/** The mode settings for this instantiation of the visitor. */
	private final Settings settings;

	/** Symbol table for variables. */
	private final Map<String, Object> variables;

	/** The outermost {@code LValueContext} for the (global) variables. */
	private final LValueContext globalContext;

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

	/** Stack of previous "resultsOnly" mode values. */
	private final Deque<Boolean> resultsOnlyModeStack = new ArrayDeque<>();

	/** Stack of previous "quiet" mode values. */
	private final Deque<Boolean> quietModeStack = new ArrayDeque<>();


	public CalcObjectVisitor(CalcDisplayer resultDisplayer, boolean rational) {
	    setMathContext(MathContext.DECIMAL128);
	    settings      = new Settings(rational);
	    variables     = new HashMap<>();
	    globalContext = new LValueContext(variables);
	    displayer     = resultDisplayer;

	    initialized   = true;
	}

	public boolean setSilent(boolean newSilent) {
	    boolean oldSilent = settings.silent;
	    settings.silent = newSilent;
	    return oldSilent;
	}

	private String processString(String escapedForm) {
	    return CharUtil.convertEscapeSequences(CharUtil.stripAnyQuotes(escapedForm, true));
	}

	private void displayActionMessage(String formatOrKey, Object... args) {
	    if (initialized && !settings.silent) {
		String message = Intl.formatKeyString(formatOrKey, args);
		displayer.displayActionMessage(message);
	    }
	}

	public MathContext getMathContext() {
	    return mc;
	}

	public BigInteger setMathContext(MathContext newMathContext) {
	    int prec  = newMathContext.getPrecision();
	    mc        = newMathContext;

	    // Use a limited precision of our max digits in the case of unlimited precision
	    MathContext mcPi = (prec == 0) ? mcMaxDigits : mc;

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

	public String setTrigMode(TrigMode newTrigMode) {
	    settings.trigMode = newTrigMode;

	    displayActionMessage("%calc#trigMode", settings.trigMode);

	    return settings.trigMode.toString();
	}

	public String setUnits(RangeMode mode) {
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

	public boolean setRationalMode(boolean mode) {
	    boolean oldMode = settings.rationalMode;
	    settings.rationalMode = mode;

	    displayActionMessage("%calc#rationalMode",
			Intl.getString(mode ? "calc#rational" : "calc#decimal"));

	    return oldMode;
	}

	public boolean setTimingMode(boolean mode) {
	    boolean oldMode = Calc.setTimingMode(mode);

	    displayActionMessage("%calc#timingMode", mode);

	    return oldMode;
	}

	public boolean setDebugMode(boolean mode) {
	    boolean oldMode = Calc.setDebugMode(mode);

	    displayActionMessage("%calc#debugMode", mode);

	    return oldMode;
	}


	private BigDecimal getDecimalValue(ParserRuleContext ctx) {
	    return toDecimalValue(this, visit(ctx), mc, ctx);
	}

	private BigFraction getFractionValue(ParserRuleContext ctx) {
	    return toFractionValue(this, visit(ctx), ctx);
	}

	private BigInteger getIntegerValue(ParserRuleContext ctx) {
	    return toIntegerValue(this, visit(ctx), mc, ctx);
	}

	private Boolean getBooleanValue(ParserRuleContext ctx) {
	    return toBooleanValue(this, visit(ctx), ctx);
	}

	protected String getStringValue(ParserRuleContext ctx) {
	    return getStringValue(ctx, false);
	}

	private String getStringValue(ParserRuleContext ctx, boolean allowNull) {
	    Object value = evaluateFunction(visit(ctx));

	    if (!allowNull)
		nullCheck(value, ctx);

	    if (value instanceof String)
		return (String) value;

	    return value == null ? "" : toStringValue(this, value);
	}

	private double getDoubleValue(ParserRuleContext ctx) {
	    BigDecimal dec = getDecimalValue(ctx);

	    return dec.doubleValue();
	}

	protected int getIntValue(ParserRuleContext ctx) {
	    return toIntValue(this, visit(ctx), mc, ctx);
	}

	private BigDecimal getDecimalTrigValue(ParserRuleContext ctx) {
	    BigDecimal value = getDecimalValue(ctx);

	    if (settings.trigMode == TrigMode.DEGREES)
		value = value.multiply(piWorker.getPiOver180(), mc);

	    return value;
	}

	private double getTrigValue(ParserRuleContext ctx) {
	    return getDecimalTrigValue(ctx).doubleValue();
	}

	private BigDecimal returnTrigValue(double value) {
	    BigDecimal radianValue = new BigDecimal(value, mcDouble);

	    if (settings.trigMode == TrigMode.DEGREES)
		return radianValue.divide(piWorker.getPiOver180(), mcDouble);

	    return radianValue;
	}

	private int compareValues(ParserRuleContext ctx1, ParserRuleContext ctx2) {
	    return compareValues(ctx1, ctx2, false, false);
	}

	private int compareValues(ParserRuleContext ctx1, ParserRuleContext ctx2, boolean strict, boolean allowNulls) {
	    return CalcUtil.compareValues(this, ctx1, ctx2, mc, strict, allowNulls);
	}


	/**
	 * Evaluate a function: basically call {@code visit} on that
	 * context if the value is itself is a parse tree (that is,
	 * the body of a function).
	 *
	 * @param value The result of an expression, which could be
	 * the body of a function.
	 * @return Either the value or the result of visiting that
	 * function context if it is one.
	 */
	protected Object evaluateFunction(Object value) {
	    Object returnValue = value;

	    if (value != null && value instanceof ParserRuleContext) {
		ParserRuleContext funcCtx = (ParserRuleContext) value;
		boolean prevSilent = setSilent(true);
		returnValue = visit(funcCtx);
		setSilent(prevSilent);
	    }

	    return returnValue;
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
		dPrecision = toDecimalValue(this, lValue.getContextObject(), mc, opt);
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
	    else if (precision > 1 && precision <= mcMaxDigits.getPrecision()) {
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
	    List<TerminalNode> ids;
	    int numberCleared = 0;

	    if (idList == null || (ids = idList.ID()).isEmpty()) {
		numberCleared = variables.size();
		variables.clear();
		displayActionMessage("%calc#varsAllCleared");
	    }
	    else {
		StringBuilder vars = new StringBuilder();
		int lastNamePos = 0;
		for (TerminalNode node : ids) {
		    String varName = node.getText();
		    if (varName.equals("<missing ID>"))
			continue;
		    numberCleared++;
		    variables.remove(varName);
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
	    List<TerminalNode> ids;
	    Set<String> sortedKeys;

	    if (idList == null || (ids = idList.ID()).isEmpty()) {
		sortedKeys = new TreeSet<>(variables.keySet());
	    }
	    else {
		sortedKeys = new TreeSet<>();
		ids = idList.ID();
		for (TerminalNode node : ids) {
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
		Object value = variables.get(key);
		displayer.displayResult(key, toStringValue(this, value));
	    }
	    if (!replMode) {
		displayActionMessage("%calc#varUnder2");
	    }

	    Calc.setResultsOnlyMode(oldMode);

	    return BigInteger.valueOf(variables.size());
	}

	@Override
	public Object visitEchoDirective(CalcParser.EchoDirectiveContext ctx) {
	    CalcParser.ExprContext expr = ctx.expr();
	    String msg = (expr != null) ? toStringValue(this, visit(expr)) : "";

	    displayer.displayMessage(processString(msg));

	    return msg;
	}

	@Override
	public Object visitIncludeDirective(CalcParser.IncludeDirectiveContext ctx) {
	    String paths = getStringValue(ctx.expr());

	    try {
		String contents = Calc.getFileContents(paths);
		return Calc.processString(contents, false);
	    }
	    catch (IOException ioe) {
		throw new CalcExprException(ctx, "%calc#ioError", ExceptionUtil.toString(ioe));
	    }
	}


	private Boolean processModeOption(CalcParser.ModeOptionContext ctx, Deque<Boolean> stack, UnaryOperator<Boolean> setOperator) {
	    boolean push = true;
	    boolean mode = false;
	    String option = null;

	    if (ctx.var() != null) {
		// could be boolean, or string mode value
		LValueContext lValue = getLValue(ctx.var());
		Object modeObject = lValue.getContextObject();
		if (modeObject instanceof Boolean) {
		    mode = ((Boolean) modeObject).booleanValue();
		}
		else {
		    option = toStringValue(this, modeObject, false, false, "");
		}
	    }
	    else {
		option = ctx.getText();
	    }
	    if (option != null) {
		switch (option.toLowerCase()) {
		    case "true":
		    case "on":
			mode = true;
			break;
		    case "false":
		    case "off":
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


	private boolean isEmptyStmt(ParseTree root) {
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

	private Object internalVisitStatements(ParserRuleContext ctx) {
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
	    Object result           = evaluateFunction(visit(ctx.expr()));
	    String resultString     = "";

	    BigInteger iValue;
	    BigDecimal dValue;

	    int precision = Integer.MIN_VALUE;
	    boolean separators = false;

	    TerminalNode formatNode = ctx.FORMAT();
	    String format           = formatNode == null ? "" : " " + formatNode.getText();
	    String exprString       = String.format("%1$s%2$s", getTreeText(ctx.expr()), format);

	    // Some formats allow a precision to be given ('%', and 'd' for instance)
	    // but some others (like 't') allow a second alpha as well
	    if (format.length() > 2) {
		int index = 1;
		char ch;
		while ((ch = format.charAt(index)) >= '0' && ch <= '9')
		    index++;
		if (index > 1) {
		    String num = format.substring(1, index);
		    precision = Integer.parseInt(num);
		}
	    }
	    separators = format.indexOf(',') >= 0;


	    if (result != null && !format.isEmpty()) {
		char formatChar = format.charAt(format.length() - 1);

		if ((result instanceof Map || result instanceof List) && (formatChar != 'j' && formatChar != 'J')) {
		    throw new CalcExprException(ctx, "%calc#noConvertObjArr", formatChar);
		}

		StringBuilder valueBuf = new StringBuilder();
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
			valueBuf.append(NumericUtil.convertToDuration(iValue.longValue(), durationUnit, mc, precision));
			valueBuf.append('\'');
			break;

		    case 'U':
		    case 'u':
			toUpperCase = true;
			valueBuf.append(toStringValue(this, result));
			break;
		    case 'L':
		    case 'l':
			toLowerCase = true;
			valueBuf.append(toStringValue(this, result));
			break;

		    case 'D':
		    case 'd':
			dValue = toDecimalValue(this, result, mc, ctx);
			if (precision != Integer.MIN_VALUE) {
			    dValue = MathUtil.round(dValue, precision);
			}
			valueBuf.append(formatWithSeparators(dValue, separators, ctx));
			break;

		    case 'E':
			toUpperCase = true;
			// fall through
		    case 'e':
			valueBuf.append("d'");
			iValue = toIntegerValue(this, result, mc, ctx);
			LocalDate date = LocalDate.ofEpochDay(iValue.longValue());
			valueBuf.append(String.format("%1$04d-%2$02d-%3$02d",
				date.getYear(), date.getMonthValue(), date.getDayOfMonth()));
			valueBuf.append('\'');
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
			valueBuf.append(toStringValue(this, result, true, true, ""));
			break;

		    case 'X':
			toUpperCase = true;
			// fall through
		    case 'x':
			if (result instanceof String) {
			    byte[] b = ((String) result).getBytes(StandardCharsets.UTF_8);
			    valueBuf.append('\'');
			    convert(b, 16, valueBuf);
			    valueBuf.append('\'');
			}
			else {
			    valueBuf.append('0').append(formatChar);
			    iValue = toIntegerValue(this, result, mc, ctx);
			    if (iValue.compareTo(BigInteger.ZERO) < 0)
				convert(iValue.toByteArray(), 16, valueBuf);
			    else
				valueBuf.append(iValue.toString(16));
			}
			break;

		    case 'O':
		    case 'o':
			if (result instanceof String) {
			    byte[] b = ((String) result).getBytes(StandardCharsets.UTF_8);
			    valueBuf.append('\'');
			    convert(b, 8, valueBuf);
			    valueBuf.append('\'');
			}
			else {
			    valueBuf.append('0');
			    iValue = toIntegerValue(this, result, mc, ctx);
			    if (iValue.compareTo(BigInteger.ZERO) < 0)
				convert(iValue.toByteArray(), 8, valueBuf);
			    else
				valueBuf.append(iValue.toString(8));
			}
			break;

		    case 'B':
			toUpperCase = true;
			// fall through
		    case 'b':
			if (result instanceof String) {
			    byte[] b = ((String) result).getBytes(StandardCharsets.UTF_8);
			    valueBuf.append('\'');
			    convert(b, 2, valueBuf);
			    valueBuf.append('\'');
			}
			else {
			    valueBuf.append('0').append(formatChar);
			    iValue = toIntegerValue(this, result, mc, ctx);
			    if (iValue.compareTo(BigInteger.ZERO) < 0)
				convert(iValue.toByteArray(), 2, valueBuf);
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

		    case '%':
			dValue = toDecimalValue(this, result, mc, ctx);
			BigDecimal percentValue = dValue.multiply(BigDecimal.valueOf(100L), mc);
			// Round the value to given precision (if any)
			if (precision != Integer.MIN_VALUE) {
			    percentValue = MathUtil.round(percentValue, precision);
			}
			valueBuf.append(formatWithSeparators(percentValue, separators, ctx)).append('%');
			break;

		    default:
			throw new CalcExprException(ctx, "%calc#illegalFormat", formatNode.getText());
		}
		// Set the "result" for the case of interpolated strings with formats
		result = resultString = toUpperCase ? valueBuf.toString().toUpperCase()
				      : toLowerCase ? valueBuf.toString().toLowerCase()
				      : valueBuf.toString();
	    }
	    else {
		resultString = toStringValue(this, result);
	    }

	    if (!settings.silent) displayer.displayResult(exprString, resultString);

	    return result;

	}

	@Override
	public Object visitLoopStmt(CalcParser.LoopStmtContext ctx) {
	    CalcParser.LoopCtlContext ctlCtx    = ctx.loopCtl();
	    CalcParser.StmtBlockContext block   = ctx.stmtBlock();
	    CalcParser.ExprListContext exprList = ctlCtx.exprList();
	    List<CalcParser.ExprContext> exprs  = ctlCtx.expr();

	    Iterator<Object> iter = null;
	    java.util.stream.IntStream codePoints  = null;

	    TerminalNode localVar = ctx.LOCALVAR();
	    String localVarName   = localVar != null ? localVar.getText() : null;

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
		stepWise = true;

		if (exprs.size() == 0) {
		    // This is the case of '(' ')', or an empty list
		    return lastValue;
		}
		else if (exprs.size() == 1) {
		    // number of times, starting from 1
		    // or it could be an array, object, or string to iterate over
		    Object obj = visit(exprs.get(0));
		    if (obj instanceof Map) {
			stepWise = false;
			@SuppressWarnings("unchecked")
			Map<Object, Object> map = (Map<Object, Object>) obj;
			iter = map.keySet().iterator();
		    }
		    else if (obj instanceof List) {
			stepWise = false;
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>) obj;
			iter = list.iterator();
		    }
		    else if (obj instanceof String) {
			stepWise = false;
			codePoints = ((String) obj).codePoints();
		    }
		    else {
			dStop = getDecimalValue(exprs.get(0));
		    }
		}
		else if (exprs.size() == 2) {
		    if (ctlCtx.DOTS() != null) {
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

	    if (localVarName != null) {
		if (variables.containsKey(localVarName))
		    throw new CalcExprException(ctx, "%calc#noDupLocalVar", localVarName);
	    }

	    try {
		if (stepWise) {
		    // Try to convert loop values to exact integers if possible
		    try {
			start = dStart.intValueExact();
			stop  = dStop.intValueExact();
			step  = dStep.intValueExact();

			if (step == 0)
			    throw new CalcExprException("%calc#infLoopStepZero", ctx);
			else if (step < 0) {
			    for (int loopIndex = start; loopIndex >= stop; loopIndex += step) {
				if (localVarName != null)
				    variables.put(localVarName, loopIndex);
				lastValue = visit(block);
			    }
			}
			else {
			    for (int loopIndex = start; loopIndex <= stop; loopIndex += step) {
				if (localVarName != null)
				    variables.put(localVarName, loopIndex);
				lastValue = visit(block);
			    }
			}
		    }
		    catch (ArithmeticException ae) {
			// This means we stubbornly have fractional values, so use as such
			int sign = dStep.signum();
			if (sign == 0)
			    throw new CalcExprException("%calc#infLoopStepZero", ctx);
			else if (sign < 0) {
			    for (BigDecimal loopIndex = dStart; loopIndex.compareTo(dStop) >= 0; loopIndex = loopIndex.add(dStep)) {
				if (localVarName != null)
				    variables.put(localVarName, loopIndex);
				lastValue = visit(block);
			    }
			}
			else {
			    for (BigDecimal loopIndex = dStart; loopIndex.compareTo(dStop) <= 0; loopIndex = loopIndex.add(dStep)) {
				if (localVarName != null)
				    variables.put(localVarName, loopIndex);
				lastValue = visit(block);
			    }
			}
		    }
		}
		else if (iter != null) {
		    while (iter.hasNext()) {
			Object value = iter.next();
			if (localVarName != null)
			    variables.put(localVarName, value);
			lastValue = visit(block);
		    }
		}
		else if (codePoints != null) {
		    StringBuilder buf = new StringBuilder(4);
		    for (Iterator<Integer> intIter = codePoints.iterator(); intIter.hasNext(); ) {
			Integer cp = intIter.next();
			buf.setLength(0);
			buf.appendCodePoint(cp);
			if (localVarName != null)
			    variables.put(localVarName, buf.toString());
			lastValue = visit(block);
		    }
		}
		else {
		    for (CalcParser.ExprContext expr : exprs) {
			Object loopValue = visit(expr);
			if (localVarName != null)
			    variables.put(localVarName, loopValue);
			lastValue = visit(block);
		    }
		}
	    }
	    finally {
		// Make sure the local loop var gets removed, even on exceptions
		if (localVarName != null)
		    variables.remove(localVarName);
	    }

	    return lastValue;
	}

	@Override
	public Object visitWhileStmt(CalcParser.WhileStmtContext ctx) {
	    CalcParser.ExprContext exprCtx    = ctx.expr();
	    CalcParser.StmtBlockContext block = ctx.stmtBlock();

	    Object lastValue = null;

	    boolean exprResult = getBooleanValue(exprCtx);
	    while (exprResult) {
		lastValue = visit(block);
		exprResult = getBooleanValue(exprCtx);
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
	    if (controlValue) {
		resultValue = visit(thenBlock);
	    }
	    else if (elseBlock != null) {
		resultValue = visit(elseBlock);
	    }

	    return resultValue;
	}

	@Override
	public Object visitCaseStmt(CalcParser.CaseStmtContext ctx) {
	    Object caseValue = visit(ctx.expr());
	    List<CalcParser.CaseBlockContext> blocks = ctx.caseBlock();
	    CalcParser.CaseBlockContext defaultCtx = null;

	    for (CalcParser.CaseBlockContext cbCtx : blocks) {
		CalcParser.ExprListContext exprListCtx = cbCtx.exprList();
		if (exprListCtx == null) {
		    defaultCtx = cbCtx;
		}
		else {
		    for (CalcParser.ExprContext exprCtx : exprListCtx.expr()) {
			Object blockValue = visit(exprCtx);
			if (CalcUtil.compareValues(this, ctx, cbCtx, caseValue, blockValue, mc, false, true) == 0) {
			    return visit(cbCtx.stmtBlock());
			}
		    }
		}
	    }

	    if (defaultCtx != null) {
		return visit(defaultCtx.stmtBlock());
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
	    String functionName = ctx.ID().getText();

	    CalcParser.StmtBlockContext functionBody    = ctx.stmtBlock();
	    CalcParser.FormalParamsContext formalParams = ctx.formalParams();

	    String paramString = formalParams == null ? "" : getTreeText(formalParams);

// TODO: make a Function object and add name, params, body to it; save this instead of functionBody as the variable
	    variables.put(functionName, functionBody);

	    displayActionMessage("%calc#defining", functionName, paramString, getTreeText(functionBody));

	    return functionBody;
	}

	@Override
	public Object visitObjExpr(CalcParser.ObjExprContext ctx) {
	    CalcParser.ObjContext oCtx = ctx.obj();
	    Map<String, Object> obj = new HashMap<>();
	    for (CalcParser.PairContext pCtx : oCtx.pair()) {
		TerminalNode id  = pCtx.ID();
		TerminalNode str = pCtx.STRING();
		String key = (id != null) ? id.getText() : str.getText();
		Object value = visit(pCtx.expr());
		obj.put(key, value);
	    }
	    return obj;
	}

	@Override
	public Object visitArrExpr(CalcParser.ArrExprContext ctx) {
	   CalcParser.ExprListContext exprList = ctx.arr().exprList();
	   List<Object> list = new ArrayList<>();
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
	    return lValue.getContextObject();
	}

	@Override
	public Object visitPostIncOpExpr(CalcParser.PostIncOpExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());
	    Object value = lValue.getContextObject();

	    BigDecimal dValue = toDecimalValue(this, value, mc, ctx.var());
	    BigDecimal dAfter;

	    String op = ctx.INC_OP().getText();
	    switch (op) {
		case "++":
		case "\u2795\u2795":
		    dAfter = dValue.add(BigDecimal.ONE);
		    break;
		case "--":
		case "\u2212\u2212":
		case "\u2796\u2796":
		    dAfter = dValue.subtract(BigDecimal.ONE);
		    break;
		default:
		    throw new UnknownOpException(op, ctx);
	    }

	    lValue.putContextObject(this, dAfter);

	    // post operation, return original value
	    return dValue;
	}

	@Override
	public Object visitPreIncOpExpr(CalcParser.PreIncOpExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());
	    Object value = lValue.getContextObject();

	    BigDecimal dValue = toDecimalValue(this, value, mc, ctx.var());
	    BigDecimal dAfter;

	    String op = ctx.INC_OP().getText();
	    switch (op) {
		case "++":
		case "\u2795\u2795":
		    dAfter = dValue.add(BigDecimal.ONE);
		    break;
		case "--":
		case "\u2212\u2212":
		case "\u2796\u2796":
		    dAfter = dValue.subtract(BigDecimal.ONE);
		    break;
		default:
		    throw new UnknownOpException(op, ctx);
	    }

	    // pre operation, return the modified value
	    return lValue.putContextObject(this, dAfter);
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
			return d.plus(mc);
		    case "-":
		    case "\u2212":
		    case "\u2796":
			return d.negate();
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
	    BigDecimal base = getDecimalValue(ctx.expr(0));
	    double exp = getDoubleValue(ctx.expr(1));

	    return MathUtil.pow(base, exp, mc);
	}

	@Override
	public Object visitPowerNExpr(CalcParser.PowerNExprContext ctx) {
	    BigDecimal base = getDecimalValue(ctx.expr());
	    String power    = ctx.POWERS().getText();
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

	@Override
	public Object visitParenExpr(CalcParser.ParenExprContext ctx) {
	    return visit(ctx.expr());
	}

	@Override
	public Object visitMultiplyExpr(CalcParser.MultiplyExprContext ctx) {
	    Object e1 = visit(ctx.expr(0));
	    Object e2 = visit(ctx.expr(1));

	    String op = ctx.MULT_OP().getText();

	    try {
		if (settings.rationalMode) {
		    BigFraction f1 = toFractionValue(this, e1, ctx);
		    BigFraction f2 = toFractionValue(this, e2, ctx);

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
			    return f1.divide(f2);
			case "%":
			    // ??? I think there is never any remainder dividing a fraction by a fraction
			    return BigFraction.ZERO;
			default:
			    throw new UnknownOpException(op, ctx);
		    }
		}
		else {
		    BigDecimal d1 = toDecimalValue(this, e1, mc, ctx);
		    BigDecimal d2 = toDecimalValue(this, e2, mc, ctx);

		    switch (op) {
			case "*":
			case "\u00D7":
			case "\u2217":
			case "\u2715":
			case "\u2716":
			    return d1.multiply(d2, mc);
			case "/":
			case "\u00F7":
			case "\u2215":
			case "\u2797":
			    return d1.divide(d2, mc);
			case "%":
			    return d1.remainder(d2, mc);
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

			return d1.subtract(d2, mc);
		    }
		default:
		    throw new UnknownOpException(op, ctx);
	    }
	}

	@Override
	public Object visitAbsExpr(CalcParser.AbsExprContext ctx) {
	    if (settings.rationalMode) {
		BigFraction f = getFractionValue(ctx.expr());
		return f.abs();
	    }
	    else {
		BigDecimal e = getDecimalValue(ctx.expr());
		return e.abs();
	    }
	}

	@Override
	public Object visitSinExpr(CalcParser.SinExprContext ctx) {
	    BigDecimal e = getDecimalTrigValue(ctx.expr());

	    return MathUtil.sin(e, mc);
	}

	@Override
	public Object visitCosExpr(CalcParser.CosExprContext ctx) {
	    BigDecimal e = getDecimalTrigValue(ctx.expr());

	    return MathUtil.cos(e, mc);
	}

	@Override
	public Object visitTanExpr(CalcParser.TanExprContext ctx) {
	    BigDecimal e = getDecimalTrigValue(ctx.expr());

	    return MathUtil.tan(e, mc);
	}

	@Override
	public Object visitAsinExpr(CalcParser.AsinExprContext ctx) {
	    // Convert to double and use standard Math method
	    double d = getDoubleValue(ctx.expr());

	    return returnTrigValue(Math.asin(d));
	}

	@Override
	public Object visitAcosExpr(CalcParser.AcosExprContext ctx) {
	    // Convert to double and use standard Math method
	    double d = getDoubleValue(ctx.expr());

	    return returnTrigValue(Math.acos(d));
	}

	@Override
	public Object visitAtanExpr(CalcParser.AtanExprContext ctx) {
	    // Convert to double and use standard Math method
	    double d = getDoubleValue(ctx.expr());

	    return returnTrigValue(Math.atan(d));
	}

	@Override
	public Object visitAtan2Expr(CalcParser.Atan2ExprContext ctx) {
	    CalcParser.Expr2Context e2ctx = ctx.expr2();
	    double y = getDoubleValue(e2ctx.expr(0));
	    double x = getDoubleValue(e2ctx.expr(1));

	    return new BigDecimal(Math.atan2(y, x), mcDouble);
	}

	@Override
	public Object visitSinhExpr(CalcParser.SinhExprContext ctx) {
	    // Convert to double and use standard Math method
	    double d = getDoubleValue(ctx.expr());

	    return new BigDecimal(Math.sinh(d), mcDouble);
	}

	@Override
	public Object visitCoshExpr(CalcParser.CoshExprContext ctx) {
	    // Convert to double and use standard Math method
	    double d = getDoubleValue(ctx.expr());

	    return new BigDecimal(Math.cosh(d), mcDouble);
	}

	@Override
	public Object visitTanhExpr(CalcParser.TanhExprContext ctx) {
	    // Convert to double and use standard Math method
	    double d = getDoubleValue(ctx.expr());

	    return new BigDecimal(Math.tanh(d), mcDouble);
	}

	@Override
	public Object visitSqrtExpr(CalcParser.SqrtExprContext ctx) {
	    try {
		return MathUtil.sqrt(getDecimalValue(ctx.expr()), mc);
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, ctx);
	    }
	}

	@Override
	public Object visitCbrtExpr(CalcParser.CbrtExprContext ctx) {
	    return MathUtil.cbrt(getDecimalValue(ctx.expr()), mc);
	}

	@Override
	public Object visitFortExpr(CalcParser.FortExprContext ctx) {
	    try {
		return MathUtil.sqrt(MathUtil.sqrt(getDecimalValue(ctx.expr()), mc), mc);
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, ctx);
	    }
	}

	@Override
	public Object visitLogExpr(CalcParser.LogExprContext ctx) {
	    // For now, get a double value and use the standard Math method
	    double d = getDoubleValue(ctx.expr());

	    double logValue = Math.log10(d);
	    if (Double.isInfinite(logValue) || Double.isNaN(logValue))
		throw new CalcExprException("%util#numeric.outOfRange", ctx);

	    return new BigDecimal(logValue, mcDouble);
	}

	@Override
	public Object visitLn2Expr(CalcParser.Ln2ExprContext ctx) {
	    BigDecimal d = getDecimalValue(ctx.expr());

	    try {
		return MathUtil.ln2(d, mc);
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, ctx);
	    }
	}

	@Override
	public Object visitLnExpr(CalcParser.LnExprContext ctx) {
	    BigDecimal d = getDecimalValue(ctx.expr());

	    try {
		return MathUtil.ln(d, mc);
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, ctx);
	    }
	}

	@Override
	public Object visitEPowerExpr(CalcParser.EPowerExprContext ctx) {
	    BigDecimal e = getDecimalValue(ctx.expr());

	    return MathUtil.ePower(e, mc);
	}

	@Override
	public Object visitSignumExpr(CalcParser.SignumExprContext ctx) {
	    int signum;

	    if (settings.rationalMode) {
		BigFraction f = getFractionValue(ctx.expr());
		signum = f.signum();
	    }
	    else {
		BigDecimal e = getDecimalValue(ctx.expr());
		signum = e.signum();
	    }
	    return BigInteger.valueOf(signum);
	}

	@Override
	public Object visitLengthExpr(CalcParser.LengthExprContext ctx) {
	    Object obj = visit(ctx.expr());

	    // This calculates the recursive size of objects and arrays
	    // so, use "scale" to calculate the non-recursive size
	    return BigInteger.valueOf((long) length(this, obj, ctx, true));
	}

	@Override
	public Object visitScaleExpr(CalcParser.ScaleExprContext ctx) {
	    Object obj = visit(ctx.expr());

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
		BigFraction f = getFractionValue(ctx.expr());

		if (f.isWholeNumber()) {
		    i = f.toInteger();
		}
		else {
		    throw new CalcExprException(ctx, "%calc#noConvertInteger", f);
		}
	    }
	    else {
		i = getIntegerValue(ctx.expr());
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
	 * @param eCtx	The expression context we're evaluating (i.e., the first parse tree).
	 * @param obj	The first object, which could be an object, a list, or an actual value.
	 * @return	The real first value, descending to the lowest level of a compound object.
	 */
	private Object getFirstValue(CalcParser.ExprContext eCtx, Object obj) {
	    Object value = evaluateFunction(obj);

	    nullCheck(value, eCtx);

	    if (value instanceof List) {
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) value;
		return list.size() > 0 ? getFirstValue(eCtx, list.get(0)) : null;
	    }
	    else if (value instanceof Map) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) value;
		Iterator<Object> iter = map.values().iterator();
		return iter.hasNext() ? getFirstValue(eCtx, iter.next()) : null;
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
	 * @see #getFirstValue
	 */
	private void buildMinMaxJoinList(ParserRuleContext ctx, Object obj, List<Object> objectList, boolean isString) {
	    Object value = evaluateFunction(obj);

	    nullCheck(value, ctx);

	    if (value instanceof List) {
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) value;
		for (Object listObj : list) {
		    buildMinMaxJoinList(ctx, listObj, objectList, isString);
		}
	    }
	    else if (value instanceof Map) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) value;
		for (Object mapObj : map.values()) {
		    buildMinMaxJoinList(ctx, mapObj, objectList, isString);
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
	 * @param exprs	The list of expressions parsed as the arguments to the function.
	 * @return	The "flat map" of the values from those arguments.
	 */
	private List<Object> buildMinMaxJoinList(List<CalcParser.ExprContext> exprs) {
	    // Do a "peek" inside any lists or maps to get the first value
	    CalcParser.ExprContext firstCtx = exprs.get(0);
	    boolean isString = getFirstValue(firstCtx, visit(firstCtx)) instanceof String;

	    List<Object> objects = new ArrayList<>();

	    for (CalcParser.ExprContext eCtx : exprs) {
		buildMinMaxJoinList(eCtx, visit(eCtx), objects, isString);
	    }

	    return objects;
	}

	@Override
	public Object visitMaxExpr(CalcParser.MaxExprContext ctx) {
	    List<CalcParser.ExprContext> exprs = ctx.exprN().exprList().expr();
	    List<Object> objects = buildMinMaxJoinList(exprs);
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
	    List<Object> objects = buildMinMaxJoinList(exprs);
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
		buildMinMaxJoinList(eCtx, visit(eCtx), objects, true);
	    }

	    StringBuilder buf = new StringBuilder();
	    int length = objects.size();

	    // This doesn't make sense unless there are at least 3 values
	    // So, one value just gets that value
	    // two values gets the two just concatenated together
	    // three or more, the first n - 1 are joined by the nth (string) value
	    if (length == 1) {
		return objects.get(0);
	    }
	    else if (length == 2) {
		buf.append(objects.get(0));
		buf.append(objects.get(1));
		return buf.toString();
	    }
	    else {
		String joinExpr = objects.get(length - 1).toString();
		for (int i = 0; i < length - 1; i++) {
		    if (i > 0)
			buf.append(joinExpr);
		    buf.append(objects.get(i));
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

	    // We need a variable sized list, not the fixed-size one returned by Arrays.asList
	    return new ArrayList<String>(Arrays.asList(parts));
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

	@Override
	public Object visitSubstrExpr(CalcParser.SubstrExprContext ctx) {
	    CalcParser.Expr2Context e2ctx = ctx.expr2();
	    CalcParser.ExprContext indexCtx;
	    String stringValue;

	    try {
		if (e2ctx != null) {
		    stringValue = getStringValue(e2ctx.expr(0));
		    indexCtx    = e2ctx.expr(1);

		    if (indexCtx == null)
			return stringValue;
		    else {
			int beginIndex = getIntValue(indexCtx);
			if (beginIndex < 0) {
			    int stringLen = stringValue.length();
			    return stringValue.substring(stringLen + beginIndex);
			}
		        return stringValue.substring(beginIndex);
		    }
		}
		else {
		    CalcParser.Expr3Context e3ctx = ctx.expr3();
		    stringValue    = getStringValue(e3ctx.expr(0));
		    int beginIndex = getIntValue(e3ctx.expr(1));
		    int endIndex   = getIntValue(e3ctx.expr(2));
		    int stringLen  = stringValue.length();

		    if (beginIndex < 0)
			beginIndex += stringLen;
		    if (endIndex < 0)
			endIndex += stringLen;

		    return stringValue.substring(beginIndex, endIndex);
		}
	    }
	    catch (StringIndexOutOfBoundsException ex) {
		throw new CalcExprException(ex, ctx);
	    }
	}

	@Override
	public Object visitFillExpr(CalcParser.FillExprContext ctx) {
	    CalcParser.VarContext varCtx  = ctx.var();
	    LValueContext lValue          = getLValue(varCtx);
	    Object value		  = lValue.getContextObject();

	    CalcParser.Expr2Context e2ctx = ctx.expr2();
	    CalcParser.Expr3Context e3ctx = ctx.expr3();

	    Object fillValue;
	    int start  = 0;
	    int length = 0;

	    if (e3ctx == null) {
		fillValue = visit(e2ctx.expr(0));
		length    = getIntValue(e2ctx.expr(1));
	    }
	    else {
		fillValue = visit(e3ctx.expr(0));
		start     = getIntValue(e3ctx.expr(1));
		length    = getIntValue(e3ctx.expr(2));
	    }

	    if (value instanceof List) {
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) value;
		// Fill in the missing values up to start
		if (start > 0 && list.size() < start) {
		    for (int index = list.size(); index < start; index++) {
			list.add(null);
		    }
		}
		for (int index = start; index < (start + length); index++) {
		    if (index < list.size()) {
			list.set(index, fillValue);
		    }
		    else {
			list.add(fillValue);
		    }
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
	    String stringValue = getStringValue(ctx.expr());
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
	    BigDecimal e = getDecimalValue(ctx.expr());

	    return MathUtil.fib(e);
	}

	@Override
	public Object visitBernExpr(CalcParser.BernExprContext ctx) {
	    int n = getIntValue(ctx.expr());

	    return MathUtil.bernoulli(n, mc, settings.rationalMode);
	}

	@Override
	public Object visitFracExpr(CalcParser.FracExprContext ctx) {
	    TerminalNode stringNode = ctx.STRING();
	    if (stringNode == null)
		stringNode = ctx.ISTRING();

	    try {
		if (stringNode != null) {
		    return BigFraction.valueOf(processString(stringNode.getText()));
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
	    String exprString = getStringValue(ctx.expr());

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
	    String exprString  = getStringValue(ctx.expr());

	    if (upper != null) {
		return exprString == null ? exprString : exprString.toUpperCase();
	    }
	    else {
		return exprString == null ? exprString : exprString.toLowerCase();
	    }
	}

	@Override
	public Object visitFactorsExpr(CalcParser.FactorsExprContext ctx) {
	    List<Integer> factors = new ArrayList<>();
	    BigInteger n = getIntegerValue(ctx.expr());

	    MathUtil.getFactors(n, factors);

	    return factors;
	}

	@Override
	public Object visitPrimeFactorsExpr(CalcParser.PrimeFactorsExprContext ctx) {
	    List<Integer> primeFactors = new ArrayList<>();
	    BigInteger n = getIntegerValue(ctx.expr());

	    MathUtil.getPrimeFactors(n, primeFactors);

	    return primeFactors;
	}

	@Override
	public Object visitEvalExpr(CalcParser.EvalExprContext ctx) {
	    String exprString = getStringValue(ctx.expr());

	    return Calc.processString(exprString, true);
	}


	/**
	 * Do a "flat map" of values for the "sumof", "productof", and "exec" functions.  Since each
	 * value to be processed could be an array or map, we need to traverse these objects
	 * as well as the simple values in order to get the full list to process.
	 *
	 * @param ctx		The overall parser context for the function (for error messages).
	 * @param obj		The object to be added to the list, or recursed into when the object
	 *			is a list or map.
	 * @param objectList	The complete list of values to be built.
	 * @param toString      Whether to coerce values always to strings, or let them be numeric also.
	 */
	private void buildValueList(ParserRuleContext ctx, Object obj, List<Object> objectList, boolean toString) {
	    Object value = evaluateFunction(obj);

	    nullCheck(value, ctx);

	    if (value instanceof List) {
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) value;
		for (Object listObj : list) {
		    buildValueList(ctx, listObj, objectList, toString);
		}
	    }
	    else if (value instanceof Map) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) value;
		for (Object mapObj : map.values()) {
		    buildValueList(ctx, mapObj, objectList, toString);
		}
	    }
	    else {
		if (toString) {
		    objectList.add(toStringValue(this, value, false, false, ""));
		}
		else if (settings.rationalMode) {
		    objectList.add(toFractionValue(this, value, ctx));
		}
		else {
		    objectList.add(toDecimalValue(this, value, mc, ctx));
		}
	    }
	}

	/**
	 * Traverse the expression list for the "sumof" or "productof" function and
	 * build the complete list of values to be processed.
	 *
	 * @param exprs	The parsed list of expression contexts.
	 * @param toString Whether to convert all values to strings or do numeric conversions.
	 * @return	The completely built "flat map" of values.
	 */
	private List<Object> buildValueList(List<CalcParser.ExprContext> exprs, boolean toString) {
	    List<Object> objects = new ArrayList<>();

	    for (CalcParser.ExprContext exprCtx : exprs) {
		buildValueList(exprCtx, visit(exprCtx), objects, toString);
	    }

	    return objects;
	}

	@Override
	public Object visitExecExpr(CalcParser.ExecExprContext ctx) {
	    List<CalcParser.ExprContext> exprs = ctx.exprN().exprList().expr();
	    List<Object> objects = buildValueList(exprs, true);
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
	public Object visitSumOfExpr(CalcParser.SumOfExprContext ctx) {
	    List<CalcParser.ExprContext> exprs = ctx.exprN().exprList().expr();
	    List<Object> objects = buildValueList(exprs, false);

	    if (settings.rationalMode) {
		BigFraction sum = BigFraction.ZERO;

		for (Object obj : objects) {
		    // At this point everything should be a BigFraction, or we would
		    // have already thrown an error
		    sum = sum.add((BigFraction) obj);
		}

		return sum;
	    }
	    else {
		BigDecimal sum = BigDecimal.ZERO;

		for (Object obj : objects) {
		    sum = sum.add((BigDecimal) obj, mc);
		}

		return sum;
	    }
	}

	@Override
	public Object visitProductOfExpr(CalcParser.ProductOfExprContext ctx) {
	    List<CalcParser.ExprContext> exprs = ctx.exprN().exprList().expr();
	    List<Object> objects = buildValueList(exprs, false);

	    if (settings.rationalMode) {
		BigFraction product = BigFraction.ONE;

		for (Object obj : objects) {
		    product = product.multiply((BigFraction) obj);
		}

		return product;
	    }
	    else {
		BigDecimal product = BigDecimal.ONE;

		for (Object obj : objects) {
		    product = product.multiply((BigDecimal) obj, mc);
		}

		return product;
	    }
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
		return settings.rationalMode ? BigFraction.MINUS_ONE : BigInteger.ONE.negate();
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

	    return processString(value);
	}

	@Override
	public Object visitIStringValue(CalcParser.IStringValueContext ctx) {
	    String value = ctx.ISTRING().getText();

	    String rawValue = processString(value);
	    int lastPos = -1;
	    int pos;
	    StringBuilder output = new StringBuilder(rawValue.length() * 2);
	    while ((pos = rawValue.indexOf('$', ++lastPos)) >= 0) {
		output.append(rawValue.substring(lastPos, pos));

		if (pos == rawValue.length() - 1)
		    throw new CalcExprException("%calc#invalidConstruct", ctx);

		if (rawValue.charAt(pos + 1) == '$') {
		    // Try to parse out a loop variable name here and substitute if found
		    // so that $$var would get $var value, but "$$(" would result in "$("
		    int identPos = pos + 2;
		    while (identPos < rawValue.length() && isIdentifierPart(rawValue.charAt(identPos)))
			identPos++;
		    if (identPos > pos + 2) {
			String varName = rawValue.substring(pos + 1, identPos);
			Object varValue = variables.get(varName);
			// But if $var is not defined, then forget it, and just output "$" and go on
			if (varValue != null) {
			    output.append(toStringValue(this, varValue, false, false, ""));
			    lastPos = identPos - 1;
			}
			else {
			    output.append('$');
			    lastPos = pos + 1;
			}
		    }
		    else {
			output.append('$');
			lastPos = pos + 1;
		    }
		}
		else if (rawValue.charAt(pos + 1) == '{') {
		    int nextPos = rawValue.indexOf('}', pos + 1);

		    if (pos + 2 >= rawValue.length() || nextPos < 0)
			throw new CalcExprException("%calc#invalidConst2", ctx);

		    String expr = rawValue.substring(pos + 2, nextPos);
		    Object exprValue = Calc.processString(expr, true);
		    output.append(toStringValue(this, exprValue, false, false, ""));
		    lastPos = nextPos;
		}
		else if (isIdentifierStart(rawValue.charAt(pos + 1))) {
		    int identPos = pos + 2;
		    while (identPos < rawValue.length() && isIdentifierPart(rawValue.charAt(identPos)))
			identPos++;
		    String varName = rawValue.substring(pos + 1, identPos);
		    output.append(toStringValue(this, variables.get(varName), false, false, ""));
		    lastPos = identPos - 1;
		}
		else
		    throw new CalcExprException("%calc#invalidConstruct", ctx);
	    }
	    if (lastPos < rawValue.length())
		output.append(rawValue.substring(lastPos));

	    return output.toString();
	}

	@Override
	public Object visitBooleanValue(CalcParser.BooleanValueContext ctx) {
	    if (ctx.K_TRUE() != null)
		return Boolean.TRUE;
	    return Boolean.FALSE;
	}

	@Override
	public Object visitNumberValue(CalcParser.NumberValueContext ctx) {
	    return new BigDecimal(ctx.NUMBER().getText());
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
	public Object visitPiValue(CalcParser.PiValueContext ctx) {
	    BigDecimal d = piWorker.getPi();
	    if (settings.rationalMode)
		return new BigFraction(d);
	    else
		return d;
	}

	@Override
	public Object visitEValue(CalcParser.EValueContext ctx) {
	    BigDecimal d = piWorker.getE();
	    if (settings.rationalMode)
		return new BigFraction(d);
	    else
		return d;
	}

	private static final int[][] FRACTIONS = {
	    {  1,  4 },
	    {  1,  2 },
	    {  3,  4 },
	    {  1,  7 },
	    {  1,  9 },
	    {  1, 10 },
	    {  0,  3 },
	    {  1,  3 },
	    {  2,  3 },
	    {  1,  5 },
	    {  2,  5 },
	    {  3,  5 },
	    {  4,  5 },
	    {  1,  6 },
	    {  5,  6 },
	    {  1,  8 },
	    {  3,  8 },
	    {  5,  8 },
	    {  7,  8 }
	};

	@Override
	public Object visitFracValue(CalcParser.FracValueContext ctx) {
	    String value = ctx.FRAC_CONST().getText();

	    int index = 0;
	    switch (value) {
		case "\u00BC": index = 0;  break; /* 1/4  */
		case "\u00BD": index = 1;  break; /* 1/2  */
		case "\u00BE": index = 2;  break; /* 3/4  */
		case "\u2150": index = 3;  break; /* 1/7  */
		case "\u2151": index = 4;  break; /* 1/9  */
		case "\u2152": index = 5;  break; /* 1/10 */
		case "\u2189": index = 6;  break; /* 0/3  */
		case "\u2153": index = 7;  break; /* 1/3  */
		case "\u2154": index = 8;  break; /* 2/3  */
		case "\u2155": index = 9;  break; /* 1/5  */
		case "\u2156": index = 10; break; /* 2/5  */
		case "\u2157": index = 11; break; /* 3/5  */
		case "\u2158": index = 12; break; /* 4/5  */
		case "\u2159": index = 13; break; /* 1/6  */
		case "\u215A": index = 14; break; /* 5/6  */
		case "\u215B": index = 15; break; /* 1/8  */
		case "\u215C": index = 16; break; /* 3/8  */
		case "\u215D": index = 17; break; /* 5/8  */
		case "\u215E": index = 18; break; /* 7/8  */
		default:
		    throw new UnknownOpException(value, ctx);
	    }

	    BigFraction fraction = new BigFraction(FRACTIONS[index][0], FRACTIONS[index][1]);
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
			return BigInteger.valueOf(NumericUtil.convertFromDuration(value));
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
	    String constant = ctx.DATE_CONST().getText();
	    String value    = CharUtil.stripQuotes(constant.substring(1));
	    StringBuilder buf = new StringBuilder();

	    try {
		buf.append(value.replaceAll("[\\-/,;]", "-"));
		if (buf.indexOf("-") < 0) {
		    if (buf.length() == 6) {
			int year = Integer.parseInt(buf.substring(0, 2));
			if (year < 50)
			    year += 2000;
			else
			    year += 1900;
			buf.replace(0, 2, String.valueOf(year));
		    }
		    buf.insert(4, "-");
		    buf.insert(7, "-");
		    value = buf.toString();
		}
		else {
		    int ix1 = buf.indexOf("-");
		    int ix2 = buf.indexOf("-", ix1+1);
		    int year = Integer.parseInt(buf.substring(0, ix1));
		    if (year < 50)
			year += 2000;
		    else if (year < 100)
			year += 1900;
		    int month = Integer.parseInt(buf.substring(ix1+1, ix2));
		    int day = Integer.parseInt(buf.substring(ix2+1));
		    value = String.format("%1$04d-%2$02d-%3$02d", year, month, day);
		}
		LocalDate date = LocalDate.parse(value);
		return BigInteger.valueOf(date.toEpochDay());
	    }
	    catch (DateTimeParseException | NumberFormatException ex) {
		throw new CalcExprException(ex, ctx);
	    }
	}

	@Override
	public Object visitNullValue(CalcParser.NullValueContext ctx) {
	    return null;
	}

	@Override
	public Object visitEitherOrExpr(CalcParser.EitherOrExprContext ctx) {
	    boolean ifExpr = getBooleanValue(ctx.expr(0));

	    return visit(ifExpr ? ctx.expr(1) : ctx.expr(2));
	}

	private LValueContext getLValue(CalcParser.VarContext var) {
	    return LValueContext.getLValue(this, var, globalContext);
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
			    result = d1.divide(d2, mc);
			    break;
			case "%=":
			    result = d1.remainder(d2, mc);
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
