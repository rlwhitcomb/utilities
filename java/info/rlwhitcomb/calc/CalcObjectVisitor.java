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
 */
package info.rlwhitcomb.calc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import info.rlwhitcomb.util.CharUtil;
import static info.rlwhitcomb.util.ConsoleColor.Code.*;
import info.rlwhitcomb.util.NumericUtil;

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

	/** Value used to convert degrees to radians. */
	private static final BigDecimal B180 = BigDecimal.valueOf(180L);

	/** Scale for double operations. */
	private static final MathContext mcDouble = MathContext.DECIMAL64;

	/** Initialization flag -- delays print until constructor is finished.  */
	private boolean initialized = false;

	/** Note: the precision will be determined by the number of digits desired. */
	private MathContext mc;

	/** Whether trig inputs are in degrees or radians. */
	private TrigMode trigMode;

	/** PI to the precision of our current math mode. */
	private BigDecimal pi;

	/** PI / 180 for degrees to radians conversion. */
	private BigDecimal piOver180;

	/** E to the precision of our current math mode. */
	private BigDecimal e;

	/** Symbol table for variables. */
	private Map<String, Object> variables;
 

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

	public CalcObjectVisitor() {
	    setMathContext(MathContext.DECIMAL128);
	    setTrigMode(TrigMode.RADIANS);
	    variables = new HashMap<>();

	    initialized = true;
	}

	private void setMathContext(MathContext newMathContext) {
	    int prec  = newMathContext.getPrecision();

	    mc        = newMathContext;

	    // Calculate these values to one more place than required, just for precision
	    e         = NumericUtil.e(prec + 1);
	    pi        = NumericUtil.pi(prec + 1);
	    piOver180 = pi.divide(B180, mc);

	    if (initialized)
		System.out.println(Calc.VALUE_COLOR + "Precision is now " + prec + " digits." + RESET);
	}

	private void setTrigMode(TrigMode newTrigMode) {
	    trigMode = newTrigMode;

	    if (initialized)
		System.out.println(Calc.VALUE_COLOR + "Trig mode is now " + trigMode + "." + RESET);
	}


	private void nullCheck(Object value, ParserRuleContext ctx) {
	    if (value == null)
		throw new CalcException("Value must not be null", ctx);
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

	    throw new CalcException("Unable to convert value of type '" + value.getClass().getSimpleName() + "' to decimal", ctx);
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
		value = value.multiply(piOver180, mc);

	    return value;
	}

	private BigInteger toIntegerValue(Object value, ParserRuleContext ctx) {
	    BigDecimal decValue = toDecimalValue(value, ctx);

	    try {
		return decValue.toBigIntegerExact();
	    }
	    catch (ArithmeticException ae) {
		throw new CalcException(ae.getMessage(), ae, ctx);
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
		throw new CalcException(ae.getMessage(), ae, ctx);
	    }
	}

	private double getTrigValue(ParserRuleContext ctx) {
	    return getDecimalTrigValue(ctx).doubleValue();
	}

	private BigDecimal returnTrigValue(double value) {
	    BigDecimal radianValue = new BigDecimal(value, mcDouble);

	    if (trigMode == TrigMode.DEGREES)
		return radianValue.divide(piOver180, mcDouble);

	    return radianValue;
	}

	private Boolean toBooleanValue(Object value, ParserRuleContext ctx) {
	    nullCheck(value, ctx);

	    try {
		boolean boolValue = CharUtil.getBooleanValue(value);
		return Boolean.valueOf(boolValue);
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcException(iae.getMessage(), iae, ctx);
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


	private int compareValues(ParserRuleContext ctx1, ParserRuleContext ctx2) {
	    return compareValues(ctx1, ctx2, false);
	}

	private int compareValues(ParserRuleContext ctx1, ParserRuleContext ctx2, boolean strict) {
	    Object e1 = visit(ctx1);
	    Object e2 = visit(ctx2);

	    nullCheck(e1, ctx1);
	    nullCheck(e2, ctx2);

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

	    throw new CalcException("Unknown value type: " + e1.getClass().getSimpleName(), ctx1);
	}


	@Override
	public Object visitDecimalDirective(CalcParser.DecimalDirectiveContext ctx) {
	    Double dPrecision = Double.valueOf(ctx.NUMBER().getText());
	    if (Math.floor(dPrecision) != dPrecision) {
		throw new CalcException("Decimal precision of " + dPrecision + " must be an integer value", ctx);
	    }	
	    int precision = dPrecision.intValue();
	    if (precision > 1 && precision <= 10000 /* arbitrary, but NumericUtil.pi only has ~12,500 digit capability */)
		setMathContext(new MathContext(precision));
	    else {
		throw new CalcException("Decimal precision of " + precision + " is out of range", ctx);
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
	public Object visitClearDirective(CalcParser.ClearDirectiveContext ctx) {
	    variables.clear();
	    System.out.println(Calc.VALUE_COLOR + "All variables cleared." + RESET);
	    return null;
	}

	@Override
	public Object visitVersionDirective(CalcParser.VersionDirectiveContext ctx) {
	    Calc.printTitleAndVersion();
	    return null;
	}

	@Override
	public Object visitHelpDirective(CalcParser.HelpDirectiveContext ctx) {
	    Calc.printHelp();
	    return null;
	}

	@Override
	public Object visitExitDirective(CalcParser.ExitDirectiveContext ctx) {
	    System.exit(0);
	    return null;
	}

	@Override
	public Object visitExprStmt(CalcParser.ExprStmtContext ctx) {
	    Object result = visit(ctx.expr());
	    TerminalNode formatNode = ctx.FORMAT();
	    String format = formatNode == null ? "" : formatNode.getText();
	    String suffix = "";

	    StringBuilder buf = new StringBuilder();
	    buf.append(Calc.EXPR_COLOR);
	    getTreeText(buf, ctx.expr());
	    buf.append(Calc.ARROW_COLOR);
	    buf.append("-> ");
	    buf.append(Calc.VALUE_COLOR);

	    if (result == null) {
		buf.append("<null>");
	    }
	    else {
		StringBuilder valueBuf = new StringBuilder();

		if (!format.isEmpty()) {
		    char formatChar = format.charAt(1);
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
				valueBuf.append('0').append(formatChar);
				if (formatChar == 'x')
				    valueBuf.append(iValue.toString(16));
				else
				    valueBuf.append(iValue.toString(16).toUpperCase());
			    }
			    result = valueBuf;
			    break;
			case '%':
			    if (result instanceof BigDecimal)
				result = ((BigDecimal)result).multiply(BigDecimal.valueOf(100L), mc);
			    suffix = " %";
			    break;
		    }
		}

		if (result instanceof StringBuilder) {
		    // This is from format conversion
		    buf.append((StringBuilder)result);
		}
		else if (result instanceof String)
		    buf.append(CharUtil.addDoubleQuotes((String)result));
		else if (result instanceof BigDecimal)
		    buf.append(((BigDecimal)result).toPlainString());
		else
		    buf.append(result.toString());
	    }
	    buf.append(suffix);

	    buf.append(RESET);
	    System.out.println(buf.toString());
	    return result;
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

	    return e1.divide(e2, mc);
	}

	@Override
	public Object visitModulusExpr(CalcParser.ModulusExprContext ctx) {
	    BigDecimal e1 = getDecimalValue(ctx.expr(0));
	    BigDecimal e2 = getDecimalValue(ctx.expr(1));

	    return e1.remainder(e2, mc);
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
		throw new CalcException(ae.getMessage(), ae, ctx);
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
	    int cmp = compareValues(ctx.expr(0), ctx.expr(1), true);
	    return Boolean.valueOf(cmp == 0);
	}

	@Override
	public Object visitStrictNotEqualExpr(CalcParser.StrictNotEqualExprContext ctx) {
	    int cmp = compareValues(ctx.expr(0), ctx.expr(1), true);
	    return Boolean.valueOf(cmp != 0);
	}

	@Override
	public Object visitEqualExpr(CalcParser.EqualExprContext ctx) {
	    int cmp = compareValues(ctx.expr(0), ctx.expr(1));
	    return Boolean.valueOf(cmp == 0);
	}

	@Override
	public Object visitNotEqualExpr(CalcParser.NotEqualExprContext ctx) {
	    int cmp = compareValues(ctx.expr(0), ctx.expr(1));
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
	public Object visitPiValue(CalcParser.PiValueContext ctx) {
	    return pi;
	}

	@Override
	public Object visitEValue(CalcParser.EValueContext ctx) {
	    return e;
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
	public Object visitAssignExpr(CalcParser.AssignExprContext ctx) {
	    Object value = visit(ctx.expr());
	    String name  = ctx.ID().getText();

	    variables.put(name, value);

	    return value;
	}
}

