/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Roger L. Whitcomb.
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
 *      Utility methods for the command-line calculator.
 *
 *  History:
 *	07-Jan-2021 (rlwhitcomb)
 *	    Moved into separate class from CalcObjectVisitor.
 *	08-Jan-2021 (rlwhitcomb)
 *	    Move more common code into here.
 *	18-Jan-2021 (rlwhitcomb)
 *	    Move "toIntValue" code into here.
 *	18-Jan-2021 (rlwhitcomb)
 *	    Move text to resource file.
 */
package info.rlwhitcomb.calc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import info.rlwhitcomb.util.CharUtil;
import static info.rlwhitcomb.util.CharUtil.Justification;


/**
 * Static utility methods for the {@link CalcObjectVisitor} class --
 * mostly object conversion and common code.
 */
public final class CalcUtil
{
	/** Private constructor since this is a static class. */
	private CalcUtil() { }

	public static StringBuilder getTreeText(final ParserRuleContext ctx) {
	    StringBuilder buf = new StringBuilder();

	    getTreeText(buf, ctx);

	    int len = buf.length();
	    while (buf.charAt(len - 1) == ' ')
		len--;
	    buf.setLength(len);

	    return buf;
	}

	public static void getTreeText(final StringBuilder buf, final ParserRuleContext ctx) {
	    for (ParseTree child : ctx.children) {
		if (child instanceof ParserRuleContext) {
		    getTreeText(buf, (ParserRuleContext)child);
		}
		else {
		    String childText = child.getText();
		    boolean sp1 = false, sp2 = true;
		    // TODO: maybe we can do better??
		    if (sp1)
			buf.append(' ');
		    buf.append(childText);
		    if (sp2)
			buf.append(' ');
		}
	    }
	}

	public static boolean isIdentifierStart(final char ch) {
	    // Corresponds to the "ID" rule in Calc.g4
	    if ((ch >= 'a' && ch <= 'z')
	     || (ch >= 'A' && ch <= 'Z')
	     || (ch == '_'))
		return true;
	    return false;
	}

	public static boolean isIdentifierPart(final char ch) {
	    if (isIdentifierStart(ch))
		return true;

	    if (ch >= '0' && ch <= '9')
		return true;

	    return false;
	}

	public static void nullCheck(final Object value, final ParserRuleContext ctx) {
	    if (value == null)
		throw new CalcExprException(ctx, "%calc#valueNotNull", getTreeText(ctx));
	}


	public static BigDecimal toDecimalValue(final Object value, final ParserRuleContext ctx) {
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

	    String typeName = value.getClass().getSimpleName();
	    if (value instanceof Map)
		typeName = "object";
	    else if (value instanceof List)
		typeName = "array";
	    throw new CalcExprException(ctx, "%calc#noConvertDecimal", typeName);
	}

	public static BigInteger toIntegerValue(final Object value, final ParserRuleContext ctx) {
	    BigDecimal decValue = toDecimalValue(value, ctx);

	    try {
		return decValue.toBigIntegerExact();
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx);
	    }
	}

	public static int toIntValue(final Object value, final ParserRuleContext ctx) {
	    BigDecimal decValue = toDecimalValue(value, ctx);

	    try {
		return decValue.intValueExact();
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx);
	    }
	}

	public static Boolean toBooleanValue(final Object value, final ParserRuleContext ctx) {
	    nullCheck(value, ctx);

	    try {
		boolean boolValue = CharUtil.getBooleanValue(value);
		return Boolean.valueOf(boolValue);
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, ctx);
	    }
	}

	public static String toStringValue(final Object result) {
	    return toStringValue(result, true, false, "");
	}

	@SuppressWarnings("unchecked")
	public static String toStringValue(final Object result, final boolean quote, final boolean pretty, final String indent) {
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
		return toStringValue((Map<String, Object>)result, quote, pretty, indent);
	    }
	    else if (result instanceof List) {
		return toStringValue((List<Object>)result, quote, pretty, indent);
	    }

	    return result.toString();
	}

	public static String toStringValue(final Map<String, Object> map, final boolean quote, final boolean pretty, final String indent) {
	    String myIndent = indent + "  ";
	    StringBuilder buf = new StringBuilder();
	    if (map.size() > 0) {
		boolean comma = false;
		buf.append(pretty ? "{\n" : "{ ");
		for (Map.Entry<String, Object> entry : map.entrySet()) {
		    if (comma)
			buf.append(pretty ? ",\n" : ", ");
		    else
			comma = true;
		    if (pretty) buf.append(myIndent);
		    buf.append(entry.getKey()).append(": ");
		    buf.append(toStringValue(entry.getValue(), quote, pretty, myIndent));
		}
		buf.append(pretty ? "\n" + indent + "}" : " }");
	    }
	    else {
		buf.append("{ }");
	    }
	    return buf.toString();
	}

	public static String toStringValue(final List<Object> list, final boolean quote, final boolean pretty, final String indent) {
	    String myIndent = indent + "  ";
	    StringBuilder buf = new StringBuilder();
	    if (list.size() > 0) {
		boolean comma = false;
		buf.append(pretty ? "[\n" : "[ ");
		for (Object value : list) {
		    if (comma)
			buf.append(pretty ? ",\n" : ", ");
		    else
			comma = true;
		    if (pretty) buf.append(myIndent);
		    buf.append(toStringValue(value, quote, pretty, myIndent));
		}
		buf.append(pretty ? "\n" + indent + "]" : " ]");
	    }
	    else {
		buf.append("[ ]");
	    }
	    return buf.toString();
	}

	public static void convert(final byte[] bytes, final int radix, final StringBuilder buf) {
	    int padWidth = 0;
	    switch (radix) {
		case 2:  padWidth = 8; break;
		case 8:  padWidth = 3; break;
		case 16: padWidth = 2; break;
	    }
	    for (byte b : bytes) {
		String number = Integer.toString(Byte.toUnsignedInt(b), radix);
		CharUtil.padToWidth(buf, number, padWidth, '0', Justification.RIGHT);
	    }
	}


	/**
	 * Compare two objects of possibly differing types.
	 *
	 * @param visitor    The object visitor used to calculate the values.
	 * @param ctx1       The Rule context of the first operand.
	 * @param ctx2       The Rule context of the second operand.
	 * @param strict     Whether or not the class must match for the comparison.
	 * @param allowNulls Some comparisons (strings) can be compared even if one or both operands are null.
	 * @return {@code -1} if the first object is "less than" the second,
	 *         {@code 0} if the objects are "equal",
	 *         {@code +1} if the first object is "greater than" the second.
	 */
	public static int compareValues(final CalcObjectVisitor visitor, final ParserRuleContext ctx1, final ParserRuleContext ctx2, final boolean strict, final boolean allowNulls) {
	    Object e1 = visitor.visit(ctx1);
	    Object e2 = visitor.visit(ctx2);

	    if (allowNulls) {
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
	    else if (e1 instanceof BigInteger || e2 instanceof BigInteger) {
		BigInteger i1 = toIntegerValue(e1, ctx1);
		BigInteger i2 = toIntegerValue(e2, ctx2);
		return i1.compareTo(i2);
	    }
	    else if (e1 instanceof Boolean || e2 instanceof Boolean) {
		Boolean b1 = toBooleanValue(e1, ctx1);
		Boolean b2 = toBooleanValue(e2, ctx2);
		return b1.compareTo(b2);
	    }

	    throw new CalcExprException(ctx1, "%calc#unknownType", e1.getClass().getSimpleName());
	}

	/**
	 * Returns the result of the "add" operation on the two values.
	 * <p> If either object is a string, do string concatenation.
	 * <p> Else convert to {@link BigDecimal} and do the addition.
	 *
	 * @param e1   The LHS operand.
	 * @param e2   The RHS operand.
	 * @param ctx1 The Rule context for the first operand (for error reporting).
	 * @param ctx2 The Rule context for the second operand.
	 * @param mc   The {@code MathContext} to use in rouding the result.
	 * @return {@code e1 + e2}
	 */
	public static Object addOp(final Object e1, final Object e2, final ParserRuleContext ctx1, final ParserRuleContext ctx2, final MathContext mc) {
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
	    BigDecimal d1 = toDecimalValue(e1, ctx1);
	    BigDecimal d2 = toDecimalValue(e2, ctx2);

	    return d1.add(d2, mc);
	}

	/**
	 * Calculates the given bit-wise operation on the operands.
	 *
	 * @param i1  The LHS operand
	 * @param i2  The RHS operand.
	 * @param op  The desired bit-wise operation.
	 * @param ctx The rule context (for error reporting).
	 * @return <code>i1 <i>op</i> i2</code>
	 */
	public static BigInteger bitOp(final BigInteger i1, final BigInteger i2, final String op, final ParserRuleContext ctx) {
	    BigInteger result;

	    switch (op) {
		case "&":
		    result = i1.and(i2);
		    break;
		case "~&":
		    result = i1.and(i2).not();
		    break;
		case "&~":
		    result = i1.andNot(i2);
		    break;
		case "^":
		    result = i1.xor(i2);
		    break;
		case "~^":
		    result = i1.xor(i2).not();
		    break;
		case "|":
		    result = i1.or(i2);
		    break;
		case "~|":
		    result = i1.or(i2).not();
		    break;
		default:
		    throw new UnknownOpException(op, ctx);
	    }

	    return result;
	}

	/**
	 * Calculates the given {@code BigInteger} shifted by the given amount according to the operator specified.
	 *
	 * @param i1       The LHS operand (the bits to be shifted).
	 * @param shiftAmt The number of bits to shift (can be negative, which results in a shift the opposite direction).
	 * @param op       The shift operator.
	 * @param ctx      The rule context (for error reporting).
	 * @return <code>i1 <i>op</i> shiftAmt</code>
	 */
	public static BigInteger shiftOp(final BigInteger i1, final int shiftAmt, final String op, final ParserRuleContext ctx) {
	    BigInteger result;

	    switch (op) {
		case ">>>":
		    // Convert to Long because ">>>" doesn't make sense for BigInteger (unlimited size) values
		    try {
			long longValue = i1.longValueExact();
			result = BigInteger.valueOf(longValue >>> shiftAmt);
		    }
		    catch (ArithmeticException ae) {
			throw new CalcExprException(ae, ctx);
		    }
		    break;

		case ">>":
		    result = i1.shiftRight(shiftAmt);
		    break;

		case "<<":
		    result = i1.shiftLeft(shiftAmt);
		    break;

		default:
		    throw new UnknownOpException(op, ctx);
	    }

	    return result;
	}


}

