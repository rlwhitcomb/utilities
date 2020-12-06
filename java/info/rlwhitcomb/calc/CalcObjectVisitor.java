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
 */
package info.rlwhitcomb.calc;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import info.rlwhitcomb.util.CharUtil;
import info.rlwhitcomb.util.NumericUtil;

/**
 * Visit each node of the parse tree and do the appropriate calculations at each level.
 * <p> Separate from the grammar, which at this point is completely language-agnostic.
 */
public class CalcObjectVisitor extends CalcBaseVisitor<Object>
{
	/** Note: the scale will be determined by the number of digits desired. */
	private MathContext mc = MathContext.DECIMAL128;

	/** Symbol table for variables. */
	private Map<String, Object> variables = new HashMap<>();
 
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

	private BigDecimal getDecimalValue(ParserRuleContext ctx) {
	    Object value = visit(ctx);
	    if (value instanceof BigDecimal)
		return (BigDecimal)value;
	    else if (value instanceof Number)
		return BigDecimal.valueOf(((Number)value).longValue());
	    throw new IllegalArgumentException("Value must be numeric, not '" + value.getClass().getSimpleName() + "'");
	}

	private Boolean getBooleanValue(ParserRuleContext ctx) {
	    Object value = visit(ctx);
	    if (value instanceof Boolean)
		return (Boolean)value;
	    throw new IllegalArgumentException("Value must be boolean, not '" + value.getClass().getSimpleName() + "'");
	}

	private String getStringValue(ParserRuleContext ctx) {
	    Object value = visit(ctx);
	    if (value instanceof String)
		return (String)value;
	    else if (value instanceof Character)
		return ((Character)value).toString();
	    throw new IllegalArgumentException("Value must be string or character, not '" + value.getClass().getSimpleName() + "'");
	}

	private int compareValues(ParserRuleContext ctx1, ParserRuleContext ctx2) {
	    Object e1 = visit(ctx1);
	    Object e2 = visit(ctx2);
	    // TODO: numeric / string / character conversions here?
	    if (!e1.getClass().equals(e2.getClass()))
		throw new IllegalArgumentException("Values are not comparable.");

	    if (e1 instanceof String) {
		String s1 = (String)e1;
		String s2 = (String)e2;
		return s1.compareTo(s2);
	    }
	    else if (e1 instanceof BigDecimal) {
		BigDecimal d1 = (BigDecimal)e1;
		BigDecimal d2 = (BigDecimal)e2;
		return d1.compareTo(d2);
	    }
	    else if (e1 instanceof Boolean) {
		Boolean b1 = (Boolean)e1;
		Boolean b2 = (Boolean)e2;
		return b1.compareTo(b2);
	    }
	    throw new IllegalArgumentException("Unknown value type: " + e1.getClass().getSimpleName());
	}


	@Override
	public Object visitDecimalDirective(CalcParser.DecimalDirectiveContext ctx) {
	    /* Get line number in the source in case of errors. */
	    int line = ctx.getStart().getLine();

	    Double dPrecision = Double.valueOf(ctx.NUMBER().getText());
	    if (Math.floor(dPrecision) != dPrecision) {
		throw new IllegalArgumentException("Decimal precision of " + dPrecision + " must be an integer value at line " + line + ".");
	    }	
	    int precision = dPrecision.intValue();
	    if (precision > 1 && precision < 32768 /* arbitrary */)
		mc = new MathContext(precision);
	    else {
		throw new IllegalArgumentException("Decimal precision of " + precision + " is out of range at line " + line + ".");
	    }
	    return null;
	}

	@Override
	public Object visitDoubleDirective(CalcParser.DoubleDirectiveContext ctx) {
	    mc = MathContext.DECIMAL64;
	    return null;
	}

	@Override
	public Object visitFloatDirective(CalcParser.FloatDirectiveContext ctx) {
	    mc = MathContext.DECIMAL32;
	    return null;
	}

	@Override
	public Object visitDefaultDirective(CalcParser.DefaultDirectiveContext ctx) {
	    mc = MathContext.DECIMAL128;
	    return null;
	}

	@Override
	public Object visitClearDirective(CalcParser.ClearDirectiveContext ctx) {
	    variables.clear();
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
	    // TODO: deal with formats
	    StringBuilder buf = new StringBuilder();
	    getTreeText(buf, ctx.expr());
	    buf.append("-> ");
	    if (result == null)
		buf.append("<null>");
	    else if (result instanceof BigDecimal)
		buf.append(((BigDecimal)result).toPlainString());
	    else
		buf.append(result.toString());
	    System.out.println(buf.toString());
	    return result;
	}

	@Override
	public Object visitParenExpr(CalcParser.ParenExprContext ctx) {
	    return visit(ctx.expr());
	}

	@Override
	public Object visitMultiplyExpr(CalcParser.MultiplyExprContext ctx) {
	    BigDecimal e1 = getDecimalValue(ctx.expr(0));
	    BigDecimal e2 = getDecimalValue(ctx.expr(1));
	    return e1.multiply(e2);
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
	    // TODO: can concat strings here too, what to do with char?
	    // could add char codepoint values, or concat strings
	    BigDecimal e1 = getDecimalValue(ctx.expr(0));
	    BigDecimal e2 = getDecimalValue(ctx.expr(1));
	    return e1.add(e2);
	}

	@Override
	public Object visitSubtractExpr(CalcParser.SubtractExprContext ctx) {
	    BigDecimal e1 = getDecimalValue(ctx.expr(0));
	    BigDecimal e2 = getDecimalValue(ctx.expr(1));
	    return e1.subtract(e2);
	}

	@Override
	public Object visitAbsExpr(CalcParser.AbsExprContext ctx) {
	    BigDecimal e = getDecimalValue(ctx.expr());
	    return e.abs();
	}

	@Override
	public Object visitSinExpr(CalcParser.SinExprContext ctx) {
	    // Note: for now, convert BigDecimal to double and use standard Math method
	    BigDecimal e = getDecimalValue(ctx.expr());
	    double d = e.doubleValue();
	    return new BigDecimal(Math.sin(d), mc);
	}

	@Override
	public Object visitSqrtExpr(CalcParser.SqrtExprContext ctx) {
	    // Note: for now, convert to double and use standard Math method
	    BigDecimal e = getDecimalValue(ctx.expr());
	    double d = e.doubleValue();
	    return new BigDecimal(Math.sqrt(d), mc);
	}

	@Override
	public Object visitCbrtExpr(CalcParser.CbrtExprContext ctx) {
	    // Note: for now, convert to double and use standard Math method
	    BigDecimal e = getDecimalValue(ctx.expr());
	    double d = e.doubleValue();
	    return new BigDecimal(Math.cbrt(d), mc);
	}

	@Override
	public Object visitFactorialExpr(CalcParser.FactorialExprContext ctx) {
	    BigDecimal e = getDecimalValue(ctx.expr());
	    return NumericUtil.factorial(e);
	}

	@Override
	public Object visitSpaceshipExpr(CalcParser.SpaceshipExprContext ctx) {
	    // TODO: deal with string / boolean also
	    BigDecimal e1 = getDecimalValue(ctx.expr(0));
	    BigDecimal e2 = getDecimalValue(ctx.expr(1));
	    int ret = e1.compareTo(e2);
	    if (ret < 0)
		return BigDecimal.ONE.negate();
	    else if (ret == 0)
		return BigDecimal.ZERO;
	    else
		return BigDecimal.ONE;
	}

	@Override
	public Object visitLessEqualExpr(CalcParser.LessEqualExprContext ctx) {
	    // TODO: need to deal with converting value types here
	    int cmp = compareValues(ctx.expr(0), ctx.expr(1));
	    return (cmp <= 0) ? Boolean.TRUE : Boolean.FALSE;
	}

	@Override
	public Object visitLessExpr(CalcParser.LessExprContext ctx) {
	    // TODO: need to deal with converting value types here
	    int cmp = compareValues(ctx.expr(0), ctx.expr(1));
	    return (cmp < 0) ? Boolean.TRUE : Boolean.FALSE;
	}

	@Override
	public Object visitGreaterEqualExpr(CalcParser.GreaterEqualExprContext ctx) {
	    // TODO: need to deal with converting value types here
	    int cmp = compareValues(ctx.expr(0), ctx.expr(1));
	    return (cmp >= 0) ? Boolean.TRUE : Boolean.FALSE;
	}

	@Override
	public Object visitGreaterExpr(CalcParser.GreaterExprContext ctx) {
	    // TODO: need to deal with converting value types here
	    int cmp = compareValues(ctx.expr(0), ctx.expr(1));
	    return (cmp > 0) ? Boolean.TRUE : Boolean.FALSE;
	}

	@Override
	public Object visitStrictEqualExpr(CalcParser.StrictEqualExprContext ctx) {
	    int cmp = compareValues(ctx.expr(0), ctx.expr(1));
	    return (cmp == 0) ? Boolean.TRUE : Boolean.FALSE;
	}

	@Override
	public Object visitStrictNotEqualExpr(CalcParser.StrictNotEqualExprContext ctx) {
	    int cmp = compareValues(ctx.expr(0), ctx.expr(1));
	    return (cmp != 0) ? Boolean.TRUE : Boolean.FALSE;
	}

	@Override
	public Object visitEqualExpr(CalcParser.EqualExprContext ctx) {
	    // TODO: need to deal with converting value types here
	    int cmp = compareValues(ctx.expr(0), ctx.expr(1));
	    return (cmp == 0) ? Boolean.TRUE : Boolean.FALSE;
	}

	@Override
	public Object visitNotEqualExpr(CalcParser.NotEqualExprContext ctx) {
	    // TODO: need to deal with converting value types here
	    int cmp = compareValues(ctx.expr(0), ctx.expr(1));
	    return (cmp != 0) ? Boolean.TRUE : Boolean.FALSE;
	}


	@Override
	public Object visitStringValue(CalcParser.StringValueContext ctx) {
	    String value = ctx.STRING().getText();
	    return CharUtil.stripAnyQuotes(value);
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
	public Object visitPiValue(CalcParser.PiValueContext ctx) {
	    return NumericUtil.pi(mc.getPrecision());
	}

	@Override
	public Object visitEValue(CalcParser.EValueContext ctx) {
	    return NumericUtil.e(mc.getPrecision());
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
	    String name = ctx.ID().getText();
	    variables.put(name, value);
	    return value;
	}
}

