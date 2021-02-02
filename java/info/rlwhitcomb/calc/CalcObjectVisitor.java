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
 */
package info.rlwhitcomb.calc;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import info.rlwhitcomb.util.BigFraction;
import static info.rlwhitcomb.calc.CalcUtil.*;
import info.rlwhitcomb.util.CharUtil;
import static info.rlwhitcomb.util.ConsoleColor.Code.*;
import info.rlwhitcomb.util.ExceptionUtil;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.NumericUtil;
import static info.rlwhitcomb.util.NumericUtil.RangeMode;

/**
 * Visit each node of the parse tree and do the appropriate calculations at each level.
 * <p> Separate from the grammar, which at this point is completely language-agnostic.
 */
public class CalcObjectVisitor extends CalcBaseVisitor<Object>
{
	/**
	 * The mode used for doing trig calculations.
	 */
	private enum TrigMode
	{
		DEGREES,
		RADIANS
	}

	/** Scale for double operations. */
	private static final MathContext mcDouble = MathContext.DECIMAL64;

	/** MathContext to use for pi/e calculations when regular context is unlimited.
	 * Note: precision is arbitrary, but {@link NumericUtil#pi} is limited to ~12,500 digits.
	 */
	private static final MathContext mcMaxDigits = new MathContext(12000);

	/** Initialization flag -- delays print until constructor is finished.  */
	private boolean initialized = false;

	/** Note: the precision will be determined by the number of digits desired. */
	private MathContext mc;

	/** Whether trig inputs are in degrees or radians. */
	private TrigMode trigMode;

	/** The kind of units to use for the ",k" format. */
	private RangeMode units = RangeMode.MIXED;

	/** Decimal vs. rational/fractional mode ({@code true} for rational); default {@code false}. */
	private boolean rationalMode;

	/** The worker used to maintain the current e/pi values, and calculate them
	 * in a background thread.
	 */
	private CalcPiWorker piWorker = null;

	/** Symbol table for variables. */
	private Map<String, Object> variables;

	/** The outermost {@code LValueContext} for the (global) variables. */
	private LValueContext globalContext;

	/** {@link CalcDisplayer} object so we can output results to either the console or GUI window. */
	private CalcDisplayer displayer;

	/** Silent flag (set to true) while evaluating nested expressions (or via :quiet directive). */
	private boolean silent = false;

	/** Stack of previous "debug" mode values. */
	private Deque<Boolean> debugModeStack = new ArrayDeque<>();

	/** Stack of previous "rational" mode values. */
	private Deque<Boolean> rationalModeStack = new ArrayDeque<>();

	/** Stack of previous "resultsOnly" mode values. */
	private Deque<Boolean> resultsOnlyModeStack = new ArrayDeque<>();

	/** Stack of previous "quiet" mode values. */
	private Deque<Boolean> quietModeStack = new ArrayDeque<>();


	public boolean getSilent() {
	    return silent;
	}

	public boolean setSilent(boolean newSilent) {
	    boolean oldSilent = silent;
	    silent = newSilent;
	    return oldSilent;
	}

	private void displayActionMessage(String formatOrKey, Object... args) {
	    if (initialized && !silent) {
		String message = Intl.formatKeyString(formatOrKey, args);
		displayer.displayActionMessage(message);
	    }
	}

	public CalcObjectVisitor(CalcDisplayer resultDisplayer, boolean rational) {
	    setRationalMode(rational);
	    setMathContext(MathContext.DECIMAL128);
	    setTrigMode(TrigMode.RADIANS);

	    variables     = new HashMap<>();
	    globalContext = new LValueContext(variables);

	    displayer     = resultDisplayer;

	    initialized   = true;
	}

	private void setMathContext(MathContext newMathContext) {
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
	}

	private void setTrigMode(TrigMode newTrigMode) {
	    trigMode = newTrigMode;

	    displayActionMessage("%calc#trigMode", trigMode);
	}

	private boolean setRationalMode(boolean mode) {
	    boolean oldMode = rationalMode;
	    rationalMode = mode;
	    return oldMode;
	}

	private BigDecimal getDecimalValue(ParserRuleContext ctx) {
	    return toDecimalValue(visit(ctx), mc, ctx);
	}

	private BigFraction getFractionValue(ParserRuleContext ctx) {
	    return toFractionValue(visit(ctx), ctx);
	}

	private BigInteger getIntegerValue(ParserRuleContext ctx) {
	    return toIntegerValue(visit(ctx), mc, ctx);
	}

	private Boolean getBooleanValue(ParserRuleContext ctx) {
	    return toBooleanValue(visit(ctx), ctx);
	}

	protected String getStringValue(ParserRuleContext ctx) {
	    return getStringValue(ctx, false);
	}

	private String getStringValue(ParserRuleContext ctx, boolean allowNull) {
	    Object value = visit(ctx);

	    if (!allowNull)
		nullCheck(value, ctx);

	    if (value instanceof String)
		return (String)value;
	    else
		return value == null ? "" : value.toString();
	}

	private double getDoubleValue(ParserRuleContext ctx) {
	    BigDecimal dec = getDecimalValue(ctx);

	    return dec.doubleValue();
	}

	protected int getIntValue(ParserRuleContext ctx) {
	    return toIntValue(visit(ctx), mc, ctx);
	}

	private BigDecimal getDecimalTrigValue(ParserRuleContext ctx) {
	    BigDecimal value = getDecimalValue(ctx);

	    if (trigMode == TrigMode.DEGREES)
		value = value.multiply(piWorker.getPiOver180(), mc);

	    return value;
	}

	private double getTrigValue(ParserRuleContext ctx) {
	    return getDecimalTrigValue(ctx).doubleValue();
	}

	private BigDecimal returnTrigValue(double value) {
	    BigDecimal radianValue = new BigDecimal(value, mcDouble);

	    if (trigMode == TrigMode.DEGREES)
		return radianValue.divide(piWorker.getPiOver180(), mcDouble);

	    return radianValue;
	}

	private int compareValues(ParserRuleContext ctx1, ParserRuleContext ctx2) {
	    return compareValues(ctx1, ctx2, false, false);
	}

	private int compareValues(ParserRuleContext ctx1, ParserRuleContext ctx2, boolean strict, boolean allowNulls) {
	    return CalcUtil.compareValues(this, ctx1, ctx2, mc, strict, allowNulls);
	}


	@Override
	public Object visitDecimalDirective(CalcParser.DecimalDirectiveContext ctx) {
	    BigDecimal dPrecision = new BigDecimal(ctx.numberOption().NUMBER().getText());
	    int precision = 0;

	    try {
		precision = dPrecision.intValueExact();
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ctx, "%calc#precNotInteger", dPrecision);
	    }

	    if (precision == 0) {
		setMathContext(MathContext.UNLIMITED);
	    }
	    else if (precision > 1 && precision <= mcMaxDigits.getPrecision()) {
		setMathContext(new MathContext(precision));
	    }
	    else {
		throw new CalcExprException(ctx, "%calc#precOutOfRange", precision);
	    }

	    return null;
	}

	@Override
	public Object visitDoubleDirective(CalcParser.DoubleDirectiveContext ctx) {
	    setMathContext(MathContext.DECIMAL64);
	    return null;
	}

	@Override
	public Object visitFloatDirective(CalcParser.FloatDirectiveContext ctx) {
	    setMathContext(MathContext.DECIMAL32);
	    return null;
	}

	@Override
	public Object visitDefaultDirective(CalcParser.DefaultDirectiveContext ctx) {
	    setMathContext(MathContext.DECIMAL128);
	    return null;
	}

	@Override
	public Object visitUnlimitedDirective(CalcParser.UnlimitedDirectiveContext ctx) {
	    setMathContext(MathContext.UNLIMITED);
	    return null;
	}

	@Override
	public Object visitDegreesDirective(CalcParser.DegreesDirectiveContext ctx) {
	    setTrigMode(TrigMode.DEGREES);
	    return null;
	}

	@Override
	public Object visitRadiansDirective(CalcParser.RadiansDirectiveContext ctx) {
	    setTrigMode(TrigMode.RADIANS);
	    return null;
	}

	@Override
	public Object visitBinaryDirective(CalcParser.BinaryDirectiveContext ctx) {
	    units = RangeMode.BINARY;
	    displayActionMessage("%calc#unitsBinary");
	    return null;
	}

	@Override
	public Object visitSiDirective(CalcParser.SiDirectiveContext ctx) {
	    units = RangeMode.DECIMAL;
	    displayActionMessage("%calc#unitsTen");
	    return null;
	}

	@Override
	public Object visitMixedDirective(CalcParser.MixedDirectiveContext ctx) {
	    units = RangeMode.MIXED;
	    displayActionMessage("%calc#unitsMixed");
	    return null;
	}

	@Override
	public Object visitClearDirective(CalcParser.ClearDirectiveContext ctx) {
	    CalcParser.IdListContext idList = ctx.idList();
	    List<TerminalNode> ids;
	    if (idList == null || (ids = idList.ID()).isEmpty()) {
		variables.clear();
		displayActionMessage("%calc#varsAllCleared");
	    }
	    else {
		StringBuilder vars = new StringBuilder();
		for (TerminalNode node : ids) {
		    String varName = node.getText();
		    if (varName.equals("<missing ID>"))
			continue;
		    variables.remove(varName);
		    if (vars.length() > 0)
			vars.append(", ");
		    vars.append("'").append(varName).append("'");
		}
		if (ids.size() == 1)
		    vars.insert(0, Intl.getString("calc#varOneVariable"));
		else
		    vars.insert(0, Intl.getString("calc#varVariables"));
		displayActionMessage("%calc#varCleared", vars);
	    }
	    return null;
	}

	@Override
	public Object visitEchoDirective(CalcParser.EchoDirectiveContext ctx) {
	    CalcParser.ExprContext expr = ctx.expr();
	    String msg = (expr != null) ? toStringValue(visit(expr)) : "";

	    displayer.displayMessage(CharUtil.stripAnyQuotes(msg, true));

	    return null;
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


	private void processModeOption(CalcParser.ModeOptionContext ctx, Deque<Boolean> stack, UnaryOperator<Boolean> setOperator) {
	    boolean push = true;
	    boolean mode;
	    String option = ctx.getText().toLowerCase();
	    switch (option) {
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
		case "":
		default:
		    // Syntax error -> don't do anything
		    return;
	    }

	    // Run the process to actually set the new mode
	    boolean previousMode = setOperator.apply(mode);

	    if (push)
		stack.push(previousMode);
	}

	@Override
	public Object visitDebugDirective(CalcParser.DebugDirectiveContext ctx) {
	    processModeOption(ctx.modeOption(), debugModeStack, (mode) -> {
		boolean previousMode = Calc.setDebugMode(mode);
		displayActionMessage("%calc#debugMode", mode);
		return previousMode;
	    });

	    return null;
	}

	@Override
	public Object visitRationalDirective(CalcParser.RationalDirectiveContext ctx) {
	    processModeOption(ctx.modeOption(), rationalModeStack, (mode) -> {
		boolean previousMode = setRationalMode(mode);
		displayActionMessage("%calc#rationalMode",
			Intl.getString(mode ? "calc#rational" : "calc#decimal"));
		return previousMode;
	    });

	    return null;
	}

	@Override
	public Object visitResultsOnlyDirective(CalcParser.ResultsOnlyDirectiveContext ctx) {
	    processModeOption(ctx.modeOption(), resultsOnlyModeStack, (mode) -> {
		return Calc.setResultsOnlyMode(mode);
	    });

	    return null;
	}

	@Override
	public Object visitQuietDirective(CalcParser.QuietDirectiveContext ctx) {
	    processModeOption(ctx.modeOption(), quietModeStack, (mode) -> {
		return Calc.setQuietMode(mode);
	    });

	    return null;
	}


	@Override
	public Object visitExprStmt(CalcParser.ExprStmtContext ctx) {
	    Object result           = visit(ctx.expr());
	    String resultString     = "";

	    TerminalNode formatNode = ctx.FORMAT();
	    String format           = formatNode == null ? "" : formatNode.getText();

	    StringBuilder exprBuf   = getTreeText(ctx.expr());
	    exprBuf.append(format);
	    String exprString       = exprBuf.toString();

	    if (result != null && !format.isEmpty()) {
		char formatChar = format.charAt(1);

		if ((result instanceof Map || result instanceof List) && (formatChar != 'j' && formatChar != 'J')) {
		    throw new CalcExprException(ctx, "%calc#noConvertObjArr", formatChar);
		}

		StringBuilder valueBuf = new StringBuilder();
		boolean toUpperCase    = false;

		switch (formatChar) {
		    case 'h':
		    case 'H':
			// TODO: convert to hours
			break;

		    case 'd':
		    case 'D':
			valueBuf.append(toDecimalValue(result, mc, ctx).toPlainString());
			break;
		    case 'f':
			valueBuf.append(toFractionValue(result, ctx));
			break;
		    case 'F':
			valueBuf.append(toFractionValue(result, ctx).toProperString());
			break;
		    case 'j':
		    case 'J':
			valueBuf.append('\n');
			valueBuf.append(toStringValue(result, true, true, ""));
			break;
		    case 'X':
			toUpperCase = true;
			// fall through
		    case 'x':
			if (result instanceof String) {
			    byte[] b = ((String)result).getBytes(StandardCharsets.UTF_8);
			    valueBuf.append('\'');
			    convert(b, 16, valueBuf);
			    valueBuf.append('\'');
			}
			else {
			    valueBuf.append('0').append(formatChar);
			    BigInteger iValue = toIntegerValue(result, mc, ctx);
			    if (iValue.compareTo(BigInteger.ZERO) < 0)
				convert(iValue.toByteArray(), 16, valueBuf);
			    else
				valueBuf.append(iValue.toString(16));
			}
			break;

		    case 'o':
		    case 'O':
			if (result instanceof String) {
			    byte[] b = ((String)result).getBytes(StandardCharsets.UTF_8);
			    valueBuf.append('\'');
			    convert(b, 8, valueBuf);
			    valueBuf.append('\'');
			}
			else {
			    valueBuf.append('0');
			    BigInteger iValue = toIntegerValue(result, mc, ctx);
			    if (iValue.compareTo(BigInteger.ZERO) < 0)
				convert(iValue.toByteArray(), 8, valueBuf);
			    else
				valueBuf.append(iValue.toString(8));
			}
			break;

		    case 'b':
		    case 'B':
			if (result instanceof String) {
			    byte[] b = ((String)result).getBytes(StandardCharsets.UTF_8);
			    valueBuf.append('\'');
			    convert(b, 2, valueBuf);
			    valueBuf.append('\'');
			}
			else {
			    valueBuf.append('0').append(formatChar);
			    BigInteger iValue = toIntegerValue(result, mc, ctx);
			    if (iValue.compareTo(BigInteger.ZERO) < 0)
				convert(iValue.toByteArray(), 2, valueBuf);
			    else
				valueBuf.append(iValue.toString(2));
			}
			break;

		    case 'K':
			toUpperCase = true;
			// fall through
		    case 'k':
			BigInteger iValue = toIntegerValue(result, mc, ctx);
			try {
			    long lValue = iValue.longValueExact();
			    valueBuf.append(NumericUtil.formatToRange(lValue, units));
			} catch (ArithmeticException ae) {
			    throw new CalcExprException(ae, ctx);
			}
			break;

		    case '%':
			BigDecimal dValue = toDecimalValue(result, mc, ctx);
			BigDecimal percentValue = dValue.multiply(BigDecimal.valueOf(100L), mc);
			valueBuf.append(percentValue.toPlainString()).append('%');
			break;
		}
		// Set the "result" for the case of interpolated strings with formats
		result = resultString = toUpperCase ? valueBuf.toString().toUpperCase() : valueBuf.toString();
	    }
	    else {
		resultString = toStringValue(result);
	    }

	    if (!silent) displayer.displayResult(exprString, resultString);

	    return result;

	}

	@Override
	public Object visitLoopStmt(CalcParser.LoopStmtContext ctx) {
	    CalcParser.LoopCtlContext ctlCtx    = ctx.loopCtl();
	    CalcParser.ExprListContext exprList = ctlCtx.exprList();
	    List<CalcParser.ExprContext> exprs  = ctlCtx.expr();

	    Iterator<Object> iter = null;
	    java.util.stream.IntStream codePoints  = null;

	    TerminalNode loopVar = ctx.LOOPVAR();
	    String loopVarName   = loopVar != null ? loopVar.getText() : null;

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
			Map<Object, Object> map = (Map<Object, Object>)obj;
			iter = map.keySet().iterator();
		    }
		    else if (obj instanceof List) {
			stepWise = false;
			@SuppressWarnings("unchecked")
			List<Object> list = (List<Object>)obj;
			iter = list.iterator();
		    }
		    else if (obj instanceof String) {
			stepWise = false;
			codePoints = ((String)obj).codePoints();
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

	    if (loopVarName != null) {
		if (variables.containsKey(loopVarName))
		    throw new CalcExprException(ctx, "%calc#noDupLoopVar", loopVarName);
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
				if (loopVarName != null)
				    variables.put(loopVarName, loopIndex);
				lastValue = visit(ctx.block());
			    }
			}
			else {
			    for (int loopIndex = start; loopIndex <= stop; loopIndex += step) {
				if (loopVarName != null)
				    variables.put(loopVarName, loopIndex);
				lastValue = visit(ctx.block());
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
				if (loopVarName != null)
				    variables.put(loopVarName, loopIndex);
				lastValue = visit(ctx.block());
			    }
			}
			else {
			    for (BigDecimal loopIndex = dStart; loopIndex.compareTo(dStop) <= 0; loopIndex = loopIndex.add(dStep)) {
				if (loopVarName != null)
				    variables.put(loopVarName, loopIndex);
				lastValue = visit(ctx.block());
			    }
			}
		    }
		}
		else if (iter != null) {
		    while (iter.hasNext()) {
			Object value = iter.next();
			if (loopVarName != null)
			    variables.put(loopVarName, value);
			lastValue = visit(ctx.block());
		    }
		}
		else if (codePoints != null) {
		    StringBuilder buf = new StringBuilder(2);
		    codePoints.forEach(cp -> {
			buf.setLength(0);
			buf.appendCodePoint(cp);
			if (loopVarName != null)
			    variables.put(loopVarName, buf.toString());
			// TODO: this is ugly, but we can't assign to "lastValue" in a lambda
			// BUT since lastValue isn't actually available anywhere, it doesn't matter
			/* lastValue = */ visit(ctx.block());
		    });
		}
		else {
		    for (CalcParser.ExprContext expr : exprs) {
			Object loopValue = visit(expr);
			if (loopVarName != null)
			    variables.put(loopVarName, loopValue);
			lastValue = visit(ctx.block());
		    }
		}
	    }
	    finally {
		// Make sure the loop var gets removed, even on exceptions
		if (loopVarName != null)
		    variables.remove(loopVarName);
	    }

	    return lastValue;
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

	    BigDecimal dValue = toDecimalValue(value, mc, ctx.var());
	    BigDecimal dAfter;

	    String op = ctx.INC_OP().getText();
	    switch (op) {
		case "++":
		    dAfter = dValue.add(BigDecimal.ONE);
		    break;
		case "--":
		    dAfter = dValue.subtract(BigDecimal.ONE);
		    break;
		default:
		    throw new UnknownOpException(op, ctx);
	    }

	    lValue.putContextObject(dAfter);

	    // post operation, return original value
	    return dValue;
	}

	@Override
	public Object visitPreIncOpExpr(CalcParser.PreIncOpExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());
	    Object value = lValue.getContextObject();

	    BigDecimal dValue = toDecimalValue(value, mc, ctx.var());
	    BigDecimal dAfter;

	    String op = ctx.INC_OP().getText();
	    switch (op) {
		case "++":
		    dAfter = dValue.add(BigDecimal.ONE);
		    break;
		case "--":
		    dAfter = dValue.subtract(BigDecimal.ONE);
		    break;
		default:
		    throw new UnknownOpException(op, ctx);
	    }

	    // pre operation, return the modified value
	    return lValue.putContextObject(dAfter);
	}

	@Override
	public Object visitNegPosExpr(CalcParser.NegPosExprContext ctx) {
	    ParserRuleContext expr = ctx.expr();
	    Object e = visit(expr);

	    String op = ctx.ADD_OP().getText();

	    if (rationalMode) {
		BigFraction f = toFractionValue(e, expr);

		switch (op) {
		    case "+":
			return f;
		    case "-":
			return f.negate();
		    default:
			throw new UnknownOpException(op, expr);
		}
	    }
	    else {
		BigDecimal d = toDecimalValue(e, mc, expr);

		switch (op) {
		    case "+":
			// Interestingly, this operation can change the value, if the previous
			// value was not to the specified precision.
			return d.plus(mc);
		    case "-":
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

	    return NumericUtil.pow(base, exp).round(mc);
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
		if (rationalMode) {
		    BigFraction f1 = toFractionValue(e1, ctx);
		    BigFraction f2 = toFractionValue(e2, ctx);

		    switch (op) {
			case "*":
			    return f1.multiply(f2);
			case "/":
			    return f1.divide(f2);
			case "%":
			    // ??? I think there is never any remainder dividing a fraction by a fraction
			    return BigFraction.ZERO;
			default:
			    throw new UnknownOpException(op, ctx);
		    }
		}
		else {
		    BigDecimal d1 = toDecimalValue(e1, mc, ctx);
		    BigDecimal d2 = toDecimalValue(e2, mc, ctx);

		    switch (op) {
			case "*":
			    return d1.multiply(d2, mc);
			case "/":
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
		    return addOp(e1, e2, ctx1, ctx2, mc, rationalMode);
		case "-":
		    if (rationalMode) {
			BigFraction f1 = toFractionValue(e1, ctx1);
			BigFraction f2 = toFractionValue(e2, ctx2);

			return f1.subtract(f2);
		    }
		    else {
			BigDecimal d1 = toDecimalValue(e1, mc, ctx1);
			BigDecimal d2 = toDecimalValue(e2, mc, ctx2);

			return d1.subtract(d2, mc);
		    }
		default:
		    throw new UnknownOpException(op, ctx);
	    }
	}

	@Override
	public Object visitAbsExpr(CalcParser.AbsExprContext ctx) {
	    if (rationalMode) {
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

	    return NumericUtil.sin(e, mc);
	}

	@Override
	public Object visitCosExpr(CalcParser.CosExprContext ctx) {
	    BigDecimal e = getDecimalTrigValue(ctx.expr());

	    return NumericUtil.cos(e, mc);
	}

	@Override
	public Object visitTanExpr(CalcParser.TanExprContext ctx) {
	    // Convert to double and use standard Math method
	    double d = getTrigValue(ctx.expr());

	    return new BigDecimal(Math.tan(d), mcDouble);
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
	    return NumericUtil.sqrt(getDecimalValue(ctx.expr()), mc);
	}

	@Override
	public Object visitCbrtExpr(CalcParser.CbrtExprContext ctx) {
	    return NumericUtil.cbrt(getDecimalValue(ctx.expr()), mc);
	}

	@Override
	public Object visitLogExpr(CalcParser.LogExprContext ctx) {
	    // For now, get a double value and use the standard Math method
	    double d = getDoubleValue(ctx.expr());

	    return new BigDecimal(Math.log10(d), mcDouble);
	}

	@Override
	public Object visitLn2Expr(CalcParser.Ln2ExprContext ctx) {
	    double d = getDoubleValue(ctx.expr());

	    double d10_2 = Math.log10(2.0d);
	    double ln10  = Math.log10(d);

	    return new BigDecimal(ln10 / d10_2, mcDouble);
	}

	@Override
	public Object visitLnExpr(CalcParser.LnExprContext ctx) {
	    double d = getDoubleValue(ctx.expr());

	    return new BigDecimal(Math.log(d), mcDouble);
	}

	@Override
	public Object visitSignumExpr(CalcParser.SignumExprContext ctx) {
	    int signum;

	    if (rationalMode) {
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
	    return BigInteger.valueOf((long)length(obj, ctx, true));
	}

	@Override
	public Object visitScaleExpr(CalcParser.ScaleExprContext ctx) {
	    Object obj = visit(ctx.expr());

	    // This returns the non-recursive size of objects and arrays
	    // so, use "length" to calculate the recursive (full) size
	    return BigInteger.valueOf((long)scale(obj, ctx));
	}

	@Override
	public Object visitRoundExpr(CalcParser.RoundExprContext ctx) {
	    CalcParser.Expr2Context e2ctx = ctx.expr2();
	    // Note: this should be good even for rational mode (converts to decimal)
	    BigDecimal e = getDecimalValue(e2ctx.expr(0));
	    int iPlaces  = getIntValue(e2ctx.expr(1));

	    /* iPlaces is going to be the number of fractional digits to round to:
	     * 0 = round to an integer, 1 to x.y, 2 to x.yy, etc.
	     * and a negative number will round above the decimal point, as in:
	     * -2 to x00.
	     * So, if precision is the number of digits we keep, and scale is how far
	     * left of the last digit the decimal point is situated, then
	     * (precision - scale) is the number of whole digits, then we can add
	     * that to "iPlaces" to get the MathContext precision to use for rounding here.
	     * Also, it appears that rounding to 0 means no change, so we set a min value
	     * of one to ensure *some* rounding always occurs, such that 0.714... rounded
	     * to -2 will give 0.7, not retain the 0.714... value.
	     */
	    int prec       = e.precision();
	    int scale      = e.scale();
	    int iRoundPrec = Math.max(1, (prec - scale) + iPlaces);

	    return e.round(new MathContext(iRoundPrec));
	}

	@Override
	public Object visitIsPrimeExpr(CalcParser.IsPrimeExprContext ctx) {
	    BigInteger i;

	    if (rationalMode) {
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

	    return Boolean.valueOf(NumericUtil.isPrime(i));
	}

	@Override
	public Object visitGcdExpr(CalcParser.GcdExprContext ctx) {
	    CalcParser.Expr2Context e2ctx = ctx.expr2();

	    if (rationalMode) {
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

	    if (rationalMode) {
		BigFraction f1 = getFractionValue(e2ctx.expr(0));
		BigFraction f2 = getFractionValue(e2ctx.expr(1));

		return f1.lcm(f2);
	    }
	    else {
		BigInteger e1 = getIntegerValue(e2ctx.expr(0));
		BigInteger e2 = getIntegerValue(e2ctx.expr(1));

		return BigFraction.lcm(e1, e2);
	    }
	}

	@Override
	public Object visitMaxExpr(CalcParser.MaxExprContext ctx) {
	    List<CalcParser.ExprContext> exprs = ctx.exprN().exprList().expr();
	    CalcParser.ExprContext eCtx = exprs.get(0);
	    Object firstValue = visit(eCtx);
	    nullCheck(firstValue, eCtx);

	    if (firstValue instanceof String) {
		String maxString = (String)firstValue;
		for (int i = 1; i < exprs.size(); i++) {
		    eCtx = exprs.get(i);
		    String value = getStringValue(eCtx);
		    if (value.compareTo(maxString) > 0)
			maxString = value;
		}
		return maxString;
	    }
	    else {
		if (rationalMode) {
		    BigFraction maxFraction = toFractionValue(firstValue, ctx);
		    for (int i = 1; i < exprs.size(); i++) {
			eCtx = exprs.get(i);
			BigFraction value = getFractionValue(eCtx);
			if (value.compareTo(maxFraction) > 0)
			    maxFraction = value;
		    }
		    return maxFraction;
		}
		else {
		    BigDecimal maxNumber = toDecimalValue(firstValue, mc, ctx);
		    for (int i = 1; i < exprs.size(); i++) {
			eCtx = exprs.get(i);
			BigDecimal value = getDecimalValue(eCtx);
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
	    CalcParser.ExprContext eCtx = exprs.get(0);
	    Object firstValue = visit(eCtx);
	    nullCheck(firstValue, eCtx);

	    if (firstValue instanceof String) {
		String minString = (String)firstValue;
		for (int i = 1; i < exprs.size(); i++) {
		    eCtx = exprs.get(i);
		    String value = getStringValue(eCtx);
		    if (value.compareTo(minString) < 0)
			minString = value;
		}
		return minString;
	    }
	    else {
		if (rationalMode) {
		    BigFraction minFraction = toFractionValue(firstValue, ctx);
		    for (int i = 1; i < exprs.size(); i++) {
			eCtx = exprs.get(i);
			BigFraction value = getFractionValue(eCtx);
			if (value.compareTo(minFraction) < 0)
			    minFraction = value;
		    }
		    return minFraction;
		}
		else {
		    BigDecimal minNumber = toDecimalValue(firstValue, mc, ctx);
		    for (int i = 1; i < exprs.size(); i++) {
			eCtx = exprs.get(i);
			BigDecimal value = getDecimalValue(eCtx);
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
	    StringBuilder buf = new StringBuilder();
	    int length = exprs.size();

	    // This doesn't make sense unless there are at least 3 values
	    // So, one value just gets that value
	    // two values gets the two just concatenated together
	    // three or more, the first n - 1 are joined by the nth (string) value
	    if (length == 1) {
		return getStringValue(exprs.get(0), true);
	    }
	    else if (length == 2) {
		buf.append(getStringValue(exprs.get(0), true));
		buf.append(getStringValue(exprs.get(1), true));
		return buf.toString();
	    }
	    else {
		String joinExpr = getStringValue(exprs.get(length - 1), true);
		for (int i = 0; i < length - 1; i++) {
		    if (i > 0)
			buf.append(joinExpr);
		    buf.append(getStringValue(exprs.get(i), true));
		}
		return buf.toString();
	    }
	}

	@Override
	public Object visitFibExpr(CalcParser.FibExprContext ctx) {
	    BigDecimal e = getDecimalValue(ctx.expr());

	    return NumericUtil.fib(e);
	}

	@Override
	public Object visitBernExpr(CalcParser.BernExprContext ctx) {
	    int n = getIntValue(ctx.expr());

	    return NumericUtil.bernoulli(n, mc, rationalMode);
	}

	@Override
	public Object visitFracExpr(CalcParser.FracExprContext ctx) {
	    CalcParser.Expr2Context e2ctx = ctx.expr2();
	    BigInteger n = getIntegerValue(e2ctx.expr(0));
	    BigInteger d = getIntegerValue(e2ctx.expr(1));

	    return new BigFraction(n, d);
	}

	@Override
	public Object visitFactorialExpr(CalcParser.FactorialExprContext ctx) {
	    BigDecimal e = getDecimalValue(ctx.expr());

	    return NumericUtil.factorial(e, mc);
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
		return rationalMode ? BigFraction.ONE.negate() : BigInteger.ONE.negate();
	    else if (ret == 0)
		return rationalMode ? BigFraction.ONE : BigInteger.ZERO;
	    else
		return rationalMode ? BigFraction.ONE : BigInteger.ONE;
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
		    result = (cmp <= 0);
		    break;
		case "<":
		    result = (cmp < 0);
		    break;
		case ">=":
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
		case "!==":
		case "\u2262": // NOT IDENTICAL
		    cmp = compareValues(expr1, expr2, true, true);
		    break;
		case "==":
		case "!=":
		case "\u2260": // NOT EQUAL
		    cmp = compareValues(expr1, expr2, false, true);
		    break;
	    }

	    switch (op) {
		case "===":
		case "==":
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
		    // Due to the short-circuit nature of this operator, the second expression
		    // is only evaluated if necessary
		    if (!b1)
			return Boolean.FALSE;
		    break;

		case "||":
		    // Due to the short-circuit nature of this operator, the second expression
		    // is only evaluated if necessary
		    if (b1)
			return Boolean.TRUE;
		    break;

		case "^^":
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

	    return CharUtil.stripAnyQuotes(value, true);
	}

	@Override
	public Object visitIStringValue(CalcParser.IStringValueContext ctx) {
	    String value = ctx.ISTRING().getText();

	    String rawValue = CharUtil.stripAnyQuotes(value, true);
	    int lastPos = -1;
	    int pos;
	    StringBuilder output = new StringBuilder(rawValue.length() * 2);
	    while ((pos = rawValue.indexOf('$', ++lastPos)) >= 0) {
		output.append(rawValue.substring(lastPos, pos));

		if (pos == rawValue.length() - 1)
		    throw new CalcExprException("%calc#invalidConstruct", ctx);

		if (rawValue.charAt(pos + 1) == '$') {
		    output.append('$');
		    lastPos = pos + 1;
		}
		else if (rawValue.charAt(pos + 1) == '{') {
		    int nextPos = rawValue.indexOf('}', pos + 1);

		    if (pos + 2 >= rawValue.length() || nextPos < 0)
			throw new CalcExprException("%calc#invalidConst2", ctx);

		    String expr = rawValue.substring(pos + 2, nextPos);
		    Object exprValue = Calc.processString(expr, true);
		    output.append(toStringValue(exprValue, false, false, ""));
		    lastPos = nextPos;
		}
		else if (isIdentifierStart(rawValue.charAt(pos + 1))) {
		    int identPos = pos + 2;
		    while (identPos < rawValue.length() && isIdentifierPart(rawValue.charAt(identPos)))
			identPos++;
		    String varName = rawValue.substring(pos + 1, identPos);
		    output.append(toStringValue(variables.get(varName), false, false, ""));
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
	    if (ctx.TRUE() != null)
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
	    if (rationalMode)
		return new BigFraction(d);
	    else
		return d;
	}

	@Override
	public Object visitEValue(CalcParser.EValueContext ctx) {
	    BigDecimal d = piWorker.getE();
	    if (rationalMode)
		return new BigFraction(d);
	    else
		return d;
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
		    result = addOp(e1, e2, varCtx, exprCtx, mc, rationalMode);
		    break;
		case "-=":
		    if (rationalMode) {
			BigFraction f1 = toFractionValue(e1, varCtx);
			BigFraction f2 = toFractionValue(e2, exprCtx);

			result = f1.subtract(f2);
		    }
		    else {
			BigDecimal d1 = toDecimalValue(e1, mc, varCtx);
			BigDecimal d2 = toDecimalValue(e2, mc, exprCtx);

			result = d1.subtract(d2, mc);
		    }
		    break;
		default:
		    throw new UnknownOpException(op, ctx);
	    }

	    return lValue.putContextObject(result);
	}

	@Override
	public Object visitPowerAssignExpr(CalcParser.PowerAssignExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());

	    BigDecimal base = toDecimalValue(lValue.getContextObject(), mc, ctx);
	    double exp      = getDoubleValue(ctx.expr());

	    return lValue.putContextObject(NumericUtil.pow(base, exp).round(mc));
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
		if (rationalMode) {
		    BigFraction f1 = toFractionValue(e1, varCtx);
		    BigFraction f2 = toFractionValue(e2, exprCtx);

		    switch (op) {
			case "*=":
			    result = f1.multiply(f2);
			    break;
			case "/=":
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
		    BigDecimal d1 = toDecimalValue(e1, mc, varCtx);
		    BigDecimal d2 = toDecimalValue(e2, mc, exprCtx);

		    switch (op) {
			case "*=":
			    result = d1.multiply(d2, mc);
			    break;
			case "/=":
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

	    return lValue.putContextObject(result);
	}

	@Override
	public Object visitBitAssignExpr(CalcParser.BitAssignExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());

	    BigInteger i1 = toIntegerValue(lValue.getContextObject(), mc, ctx);
	    BigInteger i2 = getIntegerValue(ctx.expr());

	    String op = ctx.BIT_ASSIGN().getText();
	    // Strip off the trailing '=' of the operator
	    op = op.substring(0, op.length() - 1);

	    return lValue.putContextObject(bitOp(i1, i2, op, ctx));
	}

	@Override
	public Object visitShiftAssignExpr(CalcParser.ShiftAssignExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());

	    BigInteger i1 = toIntegerValue(lValue.getContextObject(), mc, ctx);
	    int e2        = getIntValue(ctx.expr());

	    String op = ctx.SHIFT_ASSIGN().getText();
	    // Strip off the trailing "="
	    op = op.substring(0, op.length() - 1);

	    return lValue.putContextObject(shiftOp(i1, e2, op, ctx));
	}

	@Override
	public Object visitAssignExpr(CalcParser.AssignExprContext ctx) {
	    Object value = visit(ctx.expr());

	    LValueContext lValue = getLValue(ctx.var());
	    return lValue.putContextObject(value);
	}
}
