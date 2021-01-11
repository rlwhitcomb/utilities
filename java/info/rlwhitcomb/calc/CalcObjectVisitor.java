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
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import static info.rlwhitcomb.calc.CalcUtil.*;
import info.rlwhitcomb.util.CharUtil;
import static info.rlwhitcomb.util.ConsoleColor.Code.*;
import info.rlwhitcomb.util.ExceptionUtil;
import info.rlwhitcomb.util.NumericUtil;
import static info.rlwhitcomb.util.NumericUtil.RangeMode;

/**
 * Visit each node of the parse tree and do the appropriate calculations at each level.
 * <p> Separate from the grammar, which at this point is completely language-agnostic.
 */
public class CalcObjectVisitor extends CalcBaseVisitor<Object>
{
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

	private void displayActionMessage(String messageFormat, Object... args) {
	    if (initialized && !silent) {
		String message = String.format(messageFormat, args);
		displayer.displayActionMessage(message);
	    }
	}

	public CalcObjectVisitor(CalcDisplayer resultDisplayer) {
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
		displayActionMessage("Precision is now unlimited.");
	    else
		displayActionMessage("Precision is now %1$d digits.", prec);
	}

	private void setTrigMode(TrigMode newTrigMode) {
	    trigMode = newTrigMode;

	    displayActionMessage("Trig mode is now %1$s.", trigMode);
	}


	private BigDecimal getDecimalValue(ParserRuleContext ctx) {
	    return toDecimalValue(visit(ctx), ctx);
	}

	private BigInteger getIntegerValue(ParserRuleContext ctx) {
	    return toIntegerValue(visit(ctx), ctx);
	}

	private Boolean getBooleanValue(ParserRuleContext ctx) {
	    return toBooleanValue(visit(ctx), ctx);
	}

	private String getStringValue(ParserRuleContext ctx) {
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
	    BigDecimal value = getDecimalValue(ctx);

	    try {
		return value.intValueExact();
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx);
	    }
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
	    return CalcUtil.compareValues(this, ctx1, ctx2, strict, allowNulls);
	}


	@Override
	public Object visitDecimalDirective(CalcParser.DecimalDirectiveContext ctx) {
	    BigDecimal dPrecision = new BigDecimal(ctx.numberOption().NUMBER().getText());
	    int precision = 0;

	    try {
		precision = dPrecision.intValueExact();
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ctx, "Decimal precision of %1$s must be an integer value", dPrecision);
	    }

	    if (precision == 0) {
		setMathContext(MathContext.UNLIMITED);
	    }
	    else if (precision > 1 && precision <= mcMaxDigits.getPrecision()) {
		setMathContext(new MathContext(precision));
	    }
	    else {
		throw new CalcExprException(ctx, "Decimal precision of %1$d is out of range", precision);
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
	    displayActionMessage("Units in binary.");
	    return null;
	}

	@Override
	public Object visitSiDirective(CalcParser.SiDirectiveContext ctx) {
	    units = RangeMode.DECIMAL;
	    displayActionMessage("Units in SI (base ten) form.");
	    return null;
	}

	@Override
	public Object visitMixedDirective(CalcParser.MixedDirectiveContext ctx) {
	    units = RangeMode.MIXED;
	    displayActionMessage("Units in mixed form.");
	    return null;
	}

	@Override
	public Object visitClearDirective(CalcParser.ClearDirectiveContext ctx) {
	    CalcParser.IdListContext idList = ctx.idList();
	    List<TerminalNode> ids;
	    if (idList == null || (ids = idList.ID()).isEmpty()) {
		variables.clear();
		displayActionMessage("All variables cleared.");
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
		    vars.insert(0, "Variable ");
		else
		    vars.insert(0, "Variables ");
		displayActionMessage("%1$s cleared.", vars);
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
		throw new CalcExprException(ctx, "I/O Error: %1$s", ExceptionUtil.toString(ioe));
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
		displayActionMessage("Debug mode set to %1$s.", mode);
		return previousMode;
	    });

	    return null;
	}

	@Override
	public Object visitResultsOnlyDirective(CalcParser.ResultsOnlyDirectiveContext ctx) {
	    processModeOption(ctx.modeOption(), resultsOnlyModeStack, (mode) -> {
		// Switch the mode off in order to display the message, then set to the new mode
		boolean previousMode = Calc.setResultsOnlyMode(false);
		displayActionMessage("Results-only mode set to %1$s.", mode);
		Calc.setResultsOnlyMode(mode);
		return previousMode;
	    });

	    return null;
	}

	@Override
	public Object visitQuietDirective(CalcParser.QuietDirectiveContext ctx) {
	    processModeOption(ctx.modeOption(), quietModeStack, (mode) -> {
		// Switch the mode off in order to display the message, then set to the new mode
		boolean previousMode = Calc.setQuietMode(false);
		displayActionMessage("Quiet mode set to %1$s.", mode);
		Calc.setQuietMode(mode);
		return previousMode;
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
		    throw new CalcExprException(ctx, "Cannot convert object or array to '%1$c' format", formatChar);
		}

		StringBuilder valueBuf = new StringBuilder();
		boolean toUpperCase    = false;

		switch (formatChar) {
		    case 'h':
		    case 'H':
			// TODO: convert to hours
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
			    BigInteger iValue = toIntegerValue(result, ctx);
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
			    BigInteger iValue = toIntegerValue(result, ctx);
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
			    BigInteger iValue = toIntegerValue(result, ctx);
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
			BigInteger iValue = toIntegerValue(result, ctx);
			try {
			    long lValue = iValue.longValueExact();
			    valueBuf.append(NumericUtil.formatToRange(lValue, units));
			} catch (ArithmeticException ae) {
			    throw new CalcExprException(ae, ctx);
			}
			break;

		    case '%':
			BigDecimal dValue = toDecimalValue(result, ctx);
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
	    CalcParser.LoopCtlContext ctlCtx = ctx.loopCtl();
	    List<CalcParser.ExprContext> exprs = ctlCtx.expr();
	    TerminalNode loopVar = ctx.LOOPVAR();
	    String loopVarName   = loopVar != null ? loopVar.getText() : null;

	    int start, stop, step;

	    if (exprs.size() == 1) {
		// number of times, starting from 1
		start = step = 1;
		stop  = getIntValue(exprs.get(0));
	    }
	    else if (exprs.size() == 2) {
		if (ctlCtx.DOTS() != null) {
		    // start .. stop
		    start = getIntValue(exprs.get(0));
		    stop  = getIntValue(exprs.get(1));
		    step  = 1;
		}
		else {
		    // stop, step
		    start = 1;
		    stop  = getIntValue(exprs.get(0));
		    step  = getIntValue(exprs.get(1));
		}
	    }
	    else {
		// start, stop, step
		start = getIntValue(exprs.get(0));
		stop  = getIntValue(exprs.get(1));
		step  = getIntValue(exprs.get(2));
	    }

	    if (loopVarName != null) {
		if (variables.containsKey(loopVarName))
		    throw new CalcExprException(ctx, "Duplicate loop variable name '%1$s' not allowed", loopVarName);
	    }

	    Object lastValue = null;
	    try {
		for (int loop = start; loop <= stop; loop += step) {
		    if (loopVarName != null)
			variables.put(loopVarName, loop);
		    lastValue = visit(ctx.block());
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
	   CalcParser.ArrContext aCtx = ctx.arr();
	   List<Object> list = new ArrayList<>();
	   for (CalcParser.ExprContext expr : aCtx.expr()) {
		Object value = visit(expr);
		list.add(value);
	   }
	   return list;
	}

	@Override
	public Object visitVarExpr(CalcParser.VarExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());
	    return lValue.getContextObject();
	}

	@Override
	public Object visitPostIncExpr(CalcParser.PostIncExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());
	    Object value = lValue.getContextObject();

	    BigDecimal dValue = toDecimalValue(value, ctx.var());
	    BigDecimal dAfter = dValue.add(BigDecimal.ONE);

	    lValue.putContextObject(dAfter);

	    // post increment, return original value
	    return dValue;
	}

	@Override
	public Object visitPostDecExpr(CalcParser.PostDecExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());
	    Object value = lValue.getContextObject();

	    BigDecimal dValue = toDecimalValue(value, ctx.var());
	    BigDecimal dAfter = dValue.subtract(BigDecimal.ONE);

	    lValue.putContextObject(dAfter);

	    // post decrement, return the original value
	    return dValue;
	}

	@Override
	public Object visitPreIncExpr(CalcParser.PreIncExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());
	    Object value = lValue.getContextObject();

	    BigDecimal dValue = toDecimalValue(value, ctx.var());
	    BigDecimal dAfter = dValue.add(BigDecimal.ONE);

	    // pre increment, return the modified value
	    return lValue.putContextObject(dAfter);
	}

	@Override
	public Object visitPreDecExpr(CalcParser.PreDecExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());
	    Object value = lValue.getContextObject();

	    BigDecimal dValue = toDecimalValue(value, ctx.var());
	    BigDecimal dAfter = dValue.subtract(BigDecimal.ONE);

	    // pre decrement, return the modified value
	    return lValue.putContextObject(dAfter);
	}

	@Override
	public Object visitPosateExpr(CalcParser.PosateExprContext ctx) {
	    BigDecimal e = getDecimalValue(ctx.expr());

	    // Interestingly, this operation can change the value, if the previous
	    // value was not to the specified precision.
	    return e.plus(mc);
	}

	@Override
	public Object visitNegateExpr(CalcParser.NegateExprContext ctx) {
	    BigDecimal e = getDecimalValue(ctx.expr());

	    return e.negate();
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
	    BigDecimal e1 = getDecimalValue(ctx.expr(0));
	    BigDecimal e2 = getDecimalValue(ctx.expr(1));

	    return e1.multiply(e2, mc);
	}

	@Override
	public Object visitDivideExpr(CalcParser.DivideExprContext ctx) {
	    BigDecimal e1 = getDecimalValue(ctx.expr(0));
	    BigDecimal e2 = getDecimalValue(ctx.expr(1));

	    try {
		return e1.divide(e2, mc);
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx);
	    }
	}

	@Override
	public Object visitModulusExpr(CalcParser.ModulusExprContext ctx) {
	    BigDecimal e1 = getDecimalValue(ctx.expr(0));
	    BigDecimal e2 = getDecimalValue(ctx.expr(1));

	    try {
		return e1.remainder(e2, mc);
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

	    return addOp(e1, e2, ctx1, ctx2, mc);
	}

	@Override
	public Object visitSubtractExpr(CalcParser.SubtractExprContext ctx) {
	    BigDecimal e1 = getDecimalValue(ctx.expr(0));
	    BigDecimal e2 = getDecimalValue(ctx.expr(1));

	    return e1.subtract(e2, mc);
	}

	@Override
	public Object visitAbsExpr(CalcParser.AbsExprContext ctx) {
	    BigDecimal e = getDecimalValue(ctx.expr());

	    return e.abs();
	}

	@Override
	public Object visitSinExpr(CalcParser.SinExprContext ctx) {
	    BigDecimal e = getDecimalTrigValue(ctx.expr());

	    return NumericUtil.sin(e, mc);
	}

	@Override
	public Object visitCosExpr(CalcParser.CosExprContext ctx) {
	    // For now, convert to double and use standard Math method
	    double d = getTrigValue(ctx.expr());

	    return new BigDecimal(Math.cos(d), mcDouble);
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
	    // Note: for now, convert to double and use standard Math method
	    double d = getDoubleValue(ctx.expr());

	    return new BigDecimal(Math.sqrt(d), mcDouble);
	}

	@Override
	public Object visitCbrtExpr(CalcParser.CbrtExprContext ctx) {
	    // Note: for now, convert to double and use standard Math method
	    double d = getDoubleValue(ctx.expr());

	    return new BigDecimal(Math.cbrt(d), mcDouble);
	}

	@Override
	public Object visitLogExpr(CalcParser.LogExprContext ctx) {
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
	    BigDecimal e = getDecimalValue(ctx.expr());

	    return BigDecimal.valueOf(e.signum());
	}

	@Override
	public Object visitRoundExpr(CalcParser.RoundExprContext ctx) {
	    CalcParser.Expr2Context e2ctx = ctx.expr2();
	    BigDecimal e = getDecimalValue(e2ctx.expr(0));
	    int iPlaces  = getIntValue(e2ctx.expr(1));

	    return e.round(new MathContext(iPlaces));
	}

	@Override
	public Object visitIsPrimeExpr(CalcParser.IsPrimeExprContext ctx) {
	    BigInteger i = getIntegerValue(ctx.expr());

	    return Boolean.valueOf(NumericUtil.isPrime(i));
	}

	@Override
	public Object visitGcdExpr(CalcParser.GcdExprContext ctx) {
	    CalcParser.Expr2Context e2ctx = ctx.expr2();
	    BigInteger e1 = getIntegerValue(e2ctx.expr(0));
	    BigInteger e2 = getIntegerValue(e2ctx.expr(1));

	    return e1.gcd(e2);
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
		BigDecimal maxNumber = toDecimalValue(firstValue, ctx);
		for (int i = 1; i < exprs.size(); i++) {
		    eCtx = exprs.get(i);
		    BigDecimal value = getDecimalValue(eCtx);
		    if (value.compareTo(maxNumber) > 0)
			maxNumber = value;
		}
		return maxNumber;
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
		BigDecimal minNumber = toDecimalValue(firstValue, ctx);
		for (int i = 1; i < exprs.size(); i++) {
		    eCtx = exprs.get(i);
		    BigDecimal value = getDecimalValue(eCtx);
		    if (value.compareTo(minNumber) < 0)
			minNumber = value;
		}
		return minNumber;
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
	public Object visitFactorialExpr(CalcParser.FactorialExprContext ctx) {
	    BigDecimal e = getDecimalValue(ctx.expr());

	    return NumericUtil.factorial(e);
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
		return BigInteger.ONE.negate();
	    else if (ret == 0)
		return BigInteger.ZERO;
	    else
		return BigInteger.ONE;
	}

	@Override
	public Object visitCompareExpr(CalcParser.CompareExprContext ctx) {
	    ParserRuleContext expr1 = ctx.expr(0);
	    ParserRuleContext expr2 = ctx.expr(1);
	    int cmp;

	    String op = ctx.COMPARE_OP().getText();
	    switch (op) {
		case "===":
		case "!==":
		    cmp = compareValues(expr1, expr2, true, true);
		    break;
		case "==":
		case "!=":
		    cmp = compareValues(expr1, expr2, false, true);
		    break;
		default:
		    cmp = compareValues(expr1, expr2);
		    break;
	    }

	    boolean result;

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
		case "===":
		case "==":
		    result = (cmp == 0);
		    break;
		case "!==":
		case "!=":
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
		    throw new CalcExprException("Invalid '$' construct", ctx);

		if (rawValue.charAt(pos + 1) == '$') {
		    output.append('$');
		    lastPos = pos + 1;
		}
		else if (rawValue.charAt(pos + 1) == '{') {
		    int nextPos = rawValue.indexOf('}', pos + 1);

		    if (pos + 2 >= rawValue.length() || nextPos < 0)
			throw new CalcExprException("Invalid '${...}' construct", ctx);

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
		    throw new CalcExprException("Invalid '$' construct", ctx);
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
	    return piWorker.getPi();
	}

	@Override
	public Object visitEValue(CalcParser.EValueContext ctx) {
	    return piWorker.getE();
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
	    LValueContext lValue = getLValue(ctx.var());

	    Object e1 = lValue.getContextObject();
	    Object e2 = visit(ctx.expr());

	    return lValue.putContextObject(addOp(e1, e2, ctx.var(), ctx.expr(), mc));
	}

	@Override
	public Object visitSubAssignExpr(CalcParser.SubAssignExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());

	    BigDecimal d1 = toDecimalValue(lValue.getContextObject(), ctx);
	    BigDecimal d2 = getDecimalValue(ctx.expr());

	    return lValue.putContextObject(d1.subtract(d2, mc));
	}

	@Override
	public Object visitPowerAssignExpr(CalcParser.PowerAssignExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());

	    BigDecimal base = toDecimalValue(lValue.getContextObject(), ctx);
	    double exp      = getDoubleValue(ctx.expr());

	    return lValue.putContextObject(NumericUtil.pow(base, exp).round(mc));
	}

	@Override
	public Object visitMultAssignExpr(CalcParser.MultAssignExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());

	    BigDecimal d1 = toDecimalValue(lValue.getContextObject(), ctx);
	    BigDecimal d2 = getDecimalValue(ctx.expr());

	    return lValue.putContextObject(d1.multiply(d2, mc));
	}

	@Override
	public Object visitDivAssignExpr(CalcParser.DivAssignExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());

	    BigDecimal d1 = toDecimalValue(lValue.getContextObject(), ctx);
	    BigDecimal d2 = getDecimalValue(ctx.expr());

	    try {
		return lValue.putContextObject(d1.divide(d2, mc));
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx);
	    }
	}

	@Override
	public Object visitModAssignExpr(CalcParser.ModAssignExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());

	    BigDecimal d1 = toDecimalValue(lValue.getContextObject(), ctx);
	    BigDecimal d2 = getDecimalValue(ctx.expr());

	    try {
		return lValue.putContextObject(d1.remainder(d2, mc));
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx);
	    }
	}

	@Override
	public Object visitBitAssignExpr(CalcParser.BitAssignExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());

	    BigInteger i1 = toIntegerValue(lValue.getContextObject(), ctx);
	    BigInteger i2 = getIntegerValue(ctx.expr());

	    String op = ctx.BIT_ASSIGN().getText();
	    // Strip off the trailing '=' of the operator
	    op = op.substring(0, op.length() - 1);

	    return lValue.putContextObject(bitOp(i1, i2, op, ctx));
	}

	@Override
	public Object visitShiftAssignExpr(CalcParser.ShiftAssignExprContext ctx) {
	    LValueContext lValue = getLValue(ctx.var());

	    BigInteger i1 = toIntegerValue(lValue.getContextObject(), ctx);
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

