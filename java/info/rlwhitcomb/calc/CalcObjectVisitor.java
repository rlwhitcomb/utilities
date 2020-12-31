/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Roger L. Whitcomb.
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
 */
package info.rlwhitcomb.calc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import info.rlwhitcomb.util.CharUtil;
import static info.rlwhitcomb.util.ConsoleColor.Code.*;
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
 
	/** {@link CalcDisplayer} object so we can output results to either the console or GUI window. */
	private CalcDisplayer displayer;

	/** Silent flag (set to true) while evaluating nested expressions. */
	private boolean silent = false;


	public boolean getSilent() {
	    return silent;
	}

	public boolean setSilent(boolean newSilent) {
	    boolean oldSilent = silent;
	    silent = newSilent;
	    return oldSilent;
	}

	private void displayActionMessage(String message) {
	    if (initialized && !silent)
		displayer.displayActionMessage(message);
	}

	private void getTreeText(StringBuilder buf, ParserRuleContext ctx) {
	    for (ParseTree child : ctx.children) {
		if (child instanceof ParserRuleContext) {
		    getTreeText(buf, (ParserRuleContext)child);
		}
		else {
		    buf.append(child.getText()).append(' ');
		}
	    }
	}


	public CalcObjectVisitor(CalcDisplayer resultDisplayer) {
	    setMathContext(MathContext.DECIMAL128);
	    setTrigMode(TrigMode.RADIANS);
	    variables = new HashMap<>();
	    displayer = resultDisplayer;

	    initialized = true;
	}

	private boolean isIdentifierStart(char ch) {
	    // Corresponds to the "ID" rule in Calc.g4
	    if ((ch >= 'a' && ch <= 'z')
	     || (ch >= 'A' && ch <= 'Z')
	     || (ch == '_'))
		return true;
	    return false;
	}

	private boolean isIdentifierPart(char ch) {
	    if (isIdentifierStart(ch))
		return true;

	    if (ch >= '0' && ch <= '9')
		return true;

	    return false;
	}

	private void setMathContext(MathContext newMathContext) {
	    int prec  = newMathContext.getPrecision();
	    mc        = newMathContext;

	    // Either create the worker object, or trigger a recalculation
	    if (piWorker == null) {
		piWorker = new CalcPiWorker(mc);
	    }
	    else {
		piWorker.calculate(mc);
	    }

	    displayActionMessage("Precision is now " + prec + " digits.");
	}

	private void setTrigMode(TrigMode newTrigMode) {
	    trigMode = newTrigMode;

	    displayActionMessage("Trig mode is now " + trigMode + ".");
	}


	private void nullCheck(Object value, ParserRuleContext ctx) {
	    if (value == null)
		throw new CalcExprException("Value must not be null", ctx);
	}


	private BigDecimal toDecimalValue(Object value, ParserRuleContext ctx) {
	    nullCheck(value, ctx);

	    if (value instanceof BigDecimal)
		return (BigDecimal)value;
	    else if (value instanceof BigInteger)
		return new BigDecimal((BigInteger)value);
	    else if (value instanceof String)
		return new BigDecimal((String)value);
	    else if (value instanceof Boolean)
		return ((Boolean)value).booleanValue() ? BigDecimal.ONE : BigDecimal.ZERO;
	    else if (value instanceof Double || value instanceof Float)
		return new BigDecimal(((Number)value).doubleValue());
	    else if (value instanceof Number)
		return BigDecimal.valueOf(((Number)value).longValue());

	    throw new CalcExprException("Unable to convert value of type '" + value.getClass().getSimpleName() + "' to decimal", ctx);
	}

	private BigDecimal getDecimalValue(ParserRuleContext ctx) {
	    return toDecimalValue(visit(ctx), ctx);
	}

	private double getDoubleValue(ParserRuleContext ctx) {
	    BigDecimal dec = getDecimalValue(ctx);

	    return dec.doubleValue();
	}

	private BigDecimal getDecimalTrigValue(ParserRuleContext ctx) {
	    BigDecimal value = getDecimalValue(ctx);

	    if (trigMode == TrigMode.DEGREES)
		value = value.multiply(piWorker.getPiOver180(), mc);

	    return value;
	}

	private BigInteger toIntegerValue(Object value, ParserRuleContext ctx) {
	    BigDecimal decValue = toDecimalValue(value, ctx);

	    try {
		return decValue.toBigIntegerExact();
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx);
	    }
	}

	private BigInteger getIntegerValue(ParserRuleContext ctx) {
	    return toIntegerValue(visit(ctx), ctx);
	}

	private int getShiftValue(ParserRuleContext ctx) {
	    BigDecimal value = getDecimalValue(ctx);

	    try {
		return value.intValueExact();
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx);
	    }
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

	private Boolean toBooleanValue(Object value, ParserRuleContext ctx) {
	    nullCheck(value, ctx);

	    try {
		boolean boolValue = CharUtil.getBooleanValue(value);
		return Boolean.valueOf(boolValue);
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, ctx);
	    }
	}
	
	private Boolean getBooleanValue(ParserRuleContext ctx) {
	    return toBooleanValue(visit(ctx), ctx);
	}

	private String getStringValue(ParserRuleContext ctx) {
	    Object value = visit(ctx);

	    nullCheck(value, ctx);

	    if (value instanceof String)
		return (String)value;
	    else
		return value.toString();
	}

	private String toString(Object result) {
	    return toString(result, true);
	}

	@SuppressWarnings("unchecked")
	private String toString(Object result, boolean quote) {
	    if (result == null) {
		return quote ? "<null>" : "";
	    }
	    else if (result instanceof String) {
		if (quote)
		    return CharUtil.addDoubleQuotes((String)result);
		else
		    return (String)result;
	    }
	    else if (result instanceof BigDecimal) {
		return ((BigDecimal)result).toPlainString();
	    }
	    else if (result instanceof Map) {
		return toString((Map<String, Object>)result);
	    }
	    else if (result instanceof List) {
		return toString((List<Object>)result);
	    }

	    return result.toString();
	}

	private String toString(Map<String, Object> map) {
	    StringBuilder buf = new StringBuilder();
	    buf.append("{ ");
	    if (map.size() > 0) {
		for (Map.Entry<String, Object> entry : map.entrySet()) {
		    buf.append(entry.getKey()).append(": ");
		    buf.append(toString(entry.getValue())).append(", ");
		}
		buf.delete(buf.length() - 2, buf.length()).append(" }");
	    }
	    else {
		buf.append('}');
	    }
	    return buf.toString();
	}

	private String toString(List<Object> list) {
	    StringBuilder buf = new StringBuilder();
	    buf.append("[ ");
	    if (list.size() > 0) {
		for (Object value : list) {
		    buf.append(toString(value)).append(", ");
		}
		buf.delete(buf.length() - 2, buf.length()).append(" ]");
	    }
	    else {
		buf.append(']');
	    }
	    return buf.toString();
	}

	private int compareValues(ParserRuleContext ctx1, ParserRuleContext ctx2) {
	    return compareValues(ctx1, ctx2, false, false);
	}

	private int compareValues(ParserRuleContext ctx1, ParserRuleContext ctx2, boolean strict, boolean allowNull) {
	    Object e1 = visit(ctx1);
	    Object e2 = visit(ctx2);

	    if (allowNull) {
		if (e1 == null && e2 == null)
		    return 0;
		else if (e1 == null && e2 != null)
		    return -1;
		else if (e1 != null && e2 == null)
		    return +1;
	    }
	    else {
		nullCheck(e1, ctx1);
		nullCheck(e2, ctx2);
	    }

	    if (strict) {
		if (!e1.getClass().equals(e2.getClass()))
		    return -1;
	    }

	    if (e1 instanceof String || e2 instanceof String) {
		String s1 = e1.toString();
		String s2 = e2.toString();
		return s1.compareTo(s2);
	    }
	    else if (e1 instanceof BigDecimal || e2 instanceof BigDecimal) {
		BigDecimal d1 = toDecimalValue(e1, ctx1);
		BigDecimal d2 = toDecimalValue(e2, ctx2);
		return d1.compareTo(d2);
	    }
	    else if (e1 instanceof Boolean || e2 instanceof Boolean) {
		Boolean b1 = toBooleanValue(e1, ctx1);
		Boolean b2 = toBooleanValue(e2, ctx2);
		return b1.compareTo(b2);
	    }

	    throw new CalcExprException("Unknown value type: " + e1.getClass().getSimpleName(), ctx1);
	}


	@Override
	public Object visitDecimalDirective(CalcParser.DecimalDirectiveContext ctx) {
	    Double dPrecision = Double.valueOf(ctx.NUMBER().getText());
	    if (Math.floor(dPrecision) != dPrecision) {
		throw new CalcExprException("Decimal precision of " + dPrecision + " must be an integer value", ctx);
	    }	
	    int precision = dPrecision.intValue();
	    if (precision > 1 && precision <= 10000 /* arbitrary, but NumericUtil.pi only has ~12,500 digit capability */)
		setMathContext(new MathContext(precision));
	    else {
		throw new CalcExprException("Decimal precision of " + precision + " is out of range", ctx);
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
	    List<TerminalNode> ids = ctx.ID();
	    if (ids.isEmpty()) {
		variables.clear();
		displayActionMessage("All variables cleared.");
	    }
	    else {
		StringBuilder vars = new StringBuilder();
		for (TerminalNode node : ids) {
		    String varName = node.getText();
		    variables.remove(varName);
		    if (vars.length() > 0)
			vars.append(", ");
		    vars.append("'").append(varName).append("'");
		}
		if (ids.size() == 1)
		    vars.insert(0, "Variable ");
		else
		    vars.insert(0, "Variables ");
		displayActionMessage(vars + " cleared.");
	    }
	    return null;
	}

	@Override
	public Object visitEchoDirective(CalcParser.EchoDirectiveContext ctx) {
	    CalcParser.ExprContext expr = ctx.expr();
	    String msg = (expr != null) ? toString(visit(expr)) : "";

	    displayer.displayMessage(CharUtil.stripAnyQuotes(msg, true));

	    return null;
	}

	@Override
	public Object visitDebugDirective(CalcParser.DebugDirectiveContext ctx) {
	    boolean mode = ctx.TRUE() != null;
	    Calc.setDebugMode(mode);
	    displayActionMessage("Debug mode set to " + mode + ".");
	    return null;
	}

	@Override
	public Object visitExprStmt(CalcParser.ExprStmtContext ctx) {
	    Object result           = visit(ctx.expr());
	    TerminalNode formatNode = ctx.FORMAT();
	    String format           = formatNode == null ? "" : formatNode.getText();

	    StringBuilder exprBuf  = new StringBuilder();
	    StringBuilder valueBuf = new StringBuilder();

	    getTreeText(exprBuf, ctx.expr());
	    exprBuf.setLength(exprBuf.length() - 1); // take off trailing blank
	    exprBuf.append(format);

	    if (result != null && !format.isEmpty()) {
		char formatChar = format.charAt(1);

		if (result instanceof Map || result instanceof List) {
		    throw new CalcExprException("Cannot convert object or array to '" + formatChar + "' format", ctx);
		}

		switch (formatChar) {
		    case 'h':
		    case 'H':
			// TODO: convert to hours
			break;

		    case 'x':
		    case 'X':
			if (result instanceof String) {
			    byte[] b = ((String)result).getBytes(StandardCharsets.UTF_8);
			    String formatString = String.format("%%1$02%1$s", formatChar);
			    valueBuf.append('\'');
			    for (int i = 0; i < b.length; i++) {
				int j = ((int)b[i]) & 0xFF;
				valueBuf.append(String.format(formatString, j));
			    }
			    valueBuf.append('\'');
			}
			else {
			    BigInteger iValue = toIntegerValue(result, ctx);
			    if (iValue.compareTo(BigInteger.ZERO) < 0) {
				iValue = iValue.abs();
			    }
			    valueBuf.append('0').append(formatChar);
			    if (formatChar == 'x')
				valueBuf.append(iValue.toString(16));
			    else
				valueBuf.append(iValue.toString(16).toUpperCase());
			}
			break;

		    case 'o':
		    case 'O':
			if (result instanceof String) {
			    byte[] b = ((String)result).getBytes(StandardCharsets.UTF_8);
			    valueBuf.append('\'');
			    for (int i = 0; i < b.length; i++) {
				int j = ((int)b[i]) & 0xFF;
				valueBuf.append(String.format("%1$03o", j));
			    }
			    valueBuf.append('\'');
			}
			else {
			    BigInteger iValue = toIntegerValue(result, ctx);
			    if (iValue.compareTo(BigInteger.ZERO) < 0) {
				iValue = iValue.abs();
			    }
			    valueBuf.append('0');
			    valueBuf.append(iValue.toString(8));
			}
			break;

		    case 'b':
		    case 'B':
			if (result instanceof String) {
			    byte[] b = ((String)result).getBytes(StandardCharsets.UTF_8);
			    valueBuf.append('\'');
			    for (int i = 0; i < b.length; i++) {
				int j = ((int)b[i]) & 0xFF;
				for (int k = Byte.SIZE - 1; k >= 0; k--) {
				    char digit = (j & (1 << k)) != 0 ? '1' : '0';
				    valueBuf.append(digit);
				}
			    }
			    valueBuf.append('\'');
			}
			else {
			    BigInteger iValue = toIntegerValue(result, ctx);
			    if (iValue.compareTo(BigInteger.ZERO) < 0) {
				iValue = iValue.abs();
			    }
			    valueBuf.append('0').append(formatChar);
			    valueBuf.append(iValue.toString(2));
			}
			break;

		    case 'k':
		    case 'K':
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
		result = valueBuf.toString();
	    }
	    else {
		valueBuf.append(toString(result));
	    }

	    if (!silent) displayer.displayResult(exprBuf.toString(), valueBuf.toString());
	    return result;

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
	public Object visitPostIncExpr(CalcParser.PostIncExprContext ctx) {
	    String name = ctx.ID().getText();

	    BigDecimal value  = toDecimalValue(variables.get(name), ctx);
	    BigDecimal eAfter = value.add(BigDecimal.ONE);

	    variables.put(name, eAfter);

	    // post increment, return original value
	    return value;
	}

	@Override
	public Object visitPostDecExpr(CalcParser.PostDecExprContext ctx) {
	    String name = ctx.ID().getText();

	    BigDecimal value  = toDecimalValue(variables.get(name), ctx);
	    BigDecimal eAfter = value.subtract(BigDecimal.ONE);

	    variables.put(name, eAfter);

	    // post decrement, return the original value
	    return value;
	}

	@Override
	public Object visitPreIncExpr(CalcParser.PreIncExprContext ctx) {
	    String name = ctx.ID().getText();

	    BigDecimal value  = toDecimalValue(variables.get(name), ctx);
	    BigDecimal eAfter = value.add(BigDecimal.ONE);

	    variables.put(name, eAfter);

	    // pre increment, return the modified value
	    return eAfter;
	}

	@Override
	public Object visitPreDecExpr(CalcParser.PreDecExprContext ctx) {
	    String name = ctx.ID().getText();

	    BigDecimal value  = toDecimalValue(variables.get(name), ctx);
	    BigDecimal eAfter = value.subtract(BigDecimal.ONE);

	    variables.put(name, eAfter);

	    // pre decrement, return the modified value
	    return eAfter;
	}

	@Override
	public Object visitPosateExpr(CalcParser.PosateExprContext ctx) {
	    BigDecimal e = getDecimalValue(ctx.expr());

	    // Interestingly, this operation can change the value if the previous
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
	public Object visitParenExpr(CalcParser.ParenExprContext ctx) {
	    return visit(ctx.expr());
	}

	@Override
	public Object visitPowerExpr(CalcParser.PowerExprContext ctx) {
	    BigDecimal base = getDecimalValue(ctx.expr(0));
	    double exp = getDoubleValue(ctx.expr(1));

	    return NumericUtil.pow(base, exp).round(mc);
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
	    Object e1 = visit(ctx.expr(0));
	    Object e2 = visit(ctx.expr(1));

	    if (e1 == null && e2 == null)
		return null;

	    // Do string concatenation if either expr is a string
	    if (e1 instanceof String || e2 instanceof String) {
		String s1 = e1 == null ? "" : e1.toString();
		String s2 = e2 == null ? "" : e2.toString();
		return s1 + s2;
	    }

	    // TODO: what to do with char?
	    // could add char codepoint values, or concat strings

	    // Otherwise, numeric values get added numerically
	    BigDecimal d1 = toDecimalValue(e1, ctx);
	    BigDecimal d2 = toDecimalValue(e2, ctx);

	    return d1.add(d2, mc);
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
	    int iPlaces  = getShiftValue(e2ctx.expr(1));

	    return e.round(new MathContext(iPlaces));
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
	    CalcParser.ExprNContext eCtx = ctx.exprN();
	    List<CalcParser.ExprContext> exprs = eCtx.expr();
	    Object maxResult = visit(exprs.get(0));

	    nullCheck(maxResult, exprs.get(0));

	    if (maxResult instanceof String) {
		String maxString = (String)maxResult;
		for (int i = 1; i < exprs.size(); i++) {
		    String value = getStringValue(exprs.get(i));
		    if (value.compareTo(maxString) > 0)
			maxString = value;
		}
		return maxString;
	    }
	    else {
		BigDecimal maxNumber = (BigDecimal)maxResult;
		for (int i = 1; i < exprs.size(); i++) {
		   BigDecimal value = getDecimalValue(exprs.get(i));
		   if (value.compareTo(maxNumber) > 0)
			maxNumber = value;
		}
		return maxNumber;
	    }
	}

	@Override
	public Object visitMinExpr(CalcParser.MinExprContext ctx) {
	    CalcParser.ExprNContext eCtx = ctx.exprN();
	    List<CalcParser.ExprContext> exprs = eCtx.expr();
	    Object minResult = visit(exprs.get(0));

	    nullCheck(minResult, exprs.get(0));

	    if (minResult instanceof String) {
		String minString = (String)minResult;
		for (int i = 1; i < exprs.size(); i++) {
		    String value = getStringValue(exprs.get(i));
		    if (value.compareTo(minString) < 0)
			minString = value;
		}
		return minString;
	    }
	    else {
		BigDecimal minNumber = (BigDecimal)minResult;
		for (int i = 1; i < exprs.size(); i++) {
		   BigDecimal value = getDecimalValue(exprs.get(i));
		   if (value.compareTo(minNumber) < 0)
			minNumber = value;
		}
		return minNumber;
	    }
	}

	@Override
	public Object visitJoinExpr(CalcParser.JoinExprContext ctx) {
	    CalcParser.ExprNContext eCtx = ctx.exprN();
	    List<CalcParser.ExprContext> exprs = eCtx.expr();
	    StringBuilder buf = new StringBuilder();
	    int length = exprs.size();

	    // This doesn't make sense unless there are at least 3 values
	    // So, one value just gets that value
	    // two values gets the two just concatenated together
	    // three or more, the first n - 1 are joined by the nth (string) value
	    if (length == 1) {
		return getStringValue(exprs.get(0));
	    }
	    else if (length == 2) {
		buf.append(getStringValue(exprs.get(0)));
		buf.append(getStringValue(exprs.get(1)));
		return buf.toString();
	    }
	    else {
		String joinExpr = getStringValue(exprs.get(length - 1));
		for (int i = 0; i < length - 1; i++) {
		    if (i > 0)
			buf.append(joinExpr);
		    buf.append(getStringValue(exprs.get(i)));
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
	public Object visitShiftRightUnsignedExpr(CalcParser.ShiftRightUnsignedExprContext ctx) {
	    BigInteger e1 = getIntegerValue(ctx.expr(0));
	    BigInteger e2 = getIntegerValue(ctx.expr(1));

	    // Convert to Long because ">>>" doesn't make sense for BigInteger (unlimited size) values
	    try {
		long longValue = e1.longValueExact();
		int shiftValue = e2.intValueExact();
		return BigInteger.valueOf(longValue >>> shiftValue);
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx);
	    }
	}

	@Override
	public Object visitShiftRightExpr(CalcParser.ShiftRightExprContext ctx) {
	    BigInteger e1 = getIntegerValue(ctx.expr(0));
	    int e2        = getShiftValue(ctx.expr(1));

	    return e1.shiftRight(e2);
	}

	@Override
	public Object visitShiftLeftExpr(CalcParser.ShiftLeftExprContext ctx) {
	    BigInteger e1 = getIntegerValue(ctx.expr(0));
	    int e2        = getShiftValue(ctx.expr(1));

	    return e1.shiftLeft(e2);
	}

	@Override
	public Object visitSpaceshipExpr(CalcParser.SpaceshipExprContext ctx) {
	    Object e1 = visit(ctx.expr(0));
	    Object e2 = visit(ctx.expr(1));

	    nullCheck(e1, ctx);
	    nullCheck(e2, ctx);

	    int ret;

	    if (e1 instanceof String || e2 instanceof String) {
		String s1 = e1.toString();
		String s2 = e2.toString();
		ret = s1.compareTo(s2);
	    }
	    else if (e1 instanceof Boolean || e2 instanceof Boolean) {
		Boolean b1 = toBooleanValue(e1, ctx);
		Boolean b2 = toBooleanValue(e2, ctx);
		ret = b1.compareTo(b2);
	    }
	    else {
		BigDecimal d1 = toDecimalValue(e1, ctx);
		BigDecimal d2 = toDecimalValue(e2, ctx);
		ret = d1.compareTo(d2);
	    }

	    if (ret < 0)
		return BigDecimal.ONE.negate();
	    else if (ret == 0)
		return BigDecimal.ZERO;
	    else
		return BigDecimal.ONE;
	}

	@Override
	public Object visitLessEqualExpr(CalcParser.LessEqualExprContext ctx) {
	    int cmp = compareValues(ctx.expr(0), ctx.expr(1));
	    return Boolean.valueOf(cmp <= 0);
	}

	@Override
	public Object visitLessExpr(CalcParser.LessExprContext ctx) {
	    int cmp = compareValues(ctx.expr(0), ctx.expr(1));
	    return Boolean.valueOf(cmp < 0);
	}

	@Override
	public Object visitGreaterEqualExpr(CalcParser.GreaterEqualExprContext ctx) {
	    int cmp = compareValues(ctx.expr(0), ctx.expr(1));
	    return Boolean.valueOf(cmp >= 0);
	}

	@Override
	public Object visitGreaterExpr(CalcParser.GreaterExprContext ctx) {
	    int cmp = compareValues(ctx.expr(0), ctx.expr(1));
	    return Boolean.valueOf(cmp > 0);
	}

	@Override
	public Object visitStrictEqualExpr(CalcParser.StrictEqualExprContext ctx) {
	    int cmp = compareValues(ctx.expr(0), ctx.expr(1), true, true);
	    return Boolean.valueOf(cmp == 0);
	}

	@Override
	public Object visitStrictNotEqualExpr(CalcParser.StrictNotEqualExprContext ctx) {
	    int cmp = compareValues(ctx.expr(0), ctx.expr(1), true, true);
	    return Boolean.valueOf(cmp != 0);
	}

	@Override
	public Object visitEqualExpr(CalcParser.EqualExprContext ctx) {
	    int cmp = compareValues(ctx.expr(0), ctx.expr(1), false, true);
	    return Boolean.valueOf(cmp == 0);
	}

	@Override
	public Object visitNotEqualExpr(CalcParser.NotEqualExprContext ctx) {
	    int cmp = compareValues(ctx.expr(0), ctx.expr(1), false, true);
	    return Boolean.valueOf(cmp != 0);
	}

	@Override
	public Object visitBitAndExpr(CalcParser.BitAndExprContext ctx) {
	    BigInteger e1 = getIntegerValue(ctx.expr(0));
	    BigInteger e2 = getIntegerValue(ctx.expr(1));

	    return e1.and(e2);
	}

	@Override
	public Object visitBitXorExpr(CalcParser.BitXorExprContext ctx) {
	    BigInteger e1 = getIntegerValue(ctx.expr(0));
	    BigInteger e2 = getIntegerValue(ctx.expr(1));

	    return e1.xor(e2);
	}

	@Override
	public Object visitBitOrExpr(CalcParser.BitOrExprContext ctx) {
	    BigInteger e1 = getIntegerValue(ctx.expr(0));
	    BigInteger e2 = getIntegerValue(ctx.expr(1));

	    return e1.or(e2);
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
		    output.append(toString(exprValue, false));
		    lastPos = nextPos;
		}
		else if (isIdentifierStart(rawValue.charAt(pos + 1))) {
		    int identPos = pos + 2;
		    while (identPos < rawValue.length() && isIdentifierPart(rawValue.charAt(identPos)))
			identPos++;
		    String varName = rawValue.substring(pos + 1, identPos);
		    output.append(toString(variables.get(varName), false));
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
	public Object visitIdValue(CalcParser.IdValueContext ctx) {
	    String name = ctx.ID().getText();

	    if (variables.containsKey(name)) {
		return variables.get(name);
	    }

	    return null;
	}

	@Override
	public Object visitEitherOrExpr(CalcParser.EitherOrExprContext ctx) {
	    boolean ifExpr = getBooleanValue(ctx.expr(0));

	    if (ifExpr)
		return visit(ctx.expr(1));
	    else
		return visit(ctx.expr(2));
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object visitAssignExpr(CalcParser.AssignExprContext ctx) {
	    Object value = visit(ctx.expr());
	    String name;

	    CalcParser.VarContext var   = ctx.var();
	    TerminalNode id             = var.ID();
	    CalcParser.ExprContext expr = var.expr();
	    CalcParser.VarContext var2  = var.var(0);

	    // TODO: until we figure out recursive lookup, just do one level of indirection
	    // If expr is present, this is an array reference at this level
	    if (expr != null) {
		id        = var2.ID();
		name      = id.getText();
		int index = getShiftValue(expr);

		if (index < 0)
		    throw new CalcExprException("Index " + index + " cannot be negative", ctx);

		Object listValue = variables.get(name);
		List<Object> list = null;
		if (listValue != null && listValue instanceof List) {
		    list = (List<Object>)listValue;
		}
		else if (listValue == null) {
		    list = new ArrayList<>();
		    variables.put(name, list);
		}
		else {
		    throw new CalcExprException("Variable '" + name + "' already has a non-array value", ctx);
		}
		// Set empty values up to the index desired
		int size = list.size();
		for (int i = size; i <= index; i++)
		    list.add(null);

		list.set(index, value);
	    }
	    else {
		if (id == null) {
		    id   = var2.ID();
		    name = id.getText();
		    CalcParser.VarContext var3 = var.var(1);
		    TerminalNode keyNode;
		    if (var3 != null)
			keyNode = var3.ID();
		    else
			keyNode = var.STRING(0);
		    String nameKey = keyNode.getText();
		    Object mapValue = variables.get(name);
		    Map<String, Object> map = null;
		    if (mapValue != null && mapValue instanceof Map) {
			map = (Map<String, Object>)mapValue;
		    }
		    else if (mapValue == null) {
			map = new HashMap<>();
			variables.put(name, map);
		    }
		    else {
			throw new CalcExprException("Variable '" + name + "' already has a non-object value", ctx);
		    }
		    map.put(nameKey, value);
		}
		else {
		    name = id.getText();
		    variables.put(name, value);
		}
	    }

	    return value;
	}
}

