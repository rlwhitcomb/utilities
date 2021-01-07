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
 */
package info.rlwhitcomb.calc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
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

	public static StringBuilder getTreeText(ParserRuleContext ctx) {
	    StringBuilder buf = new StringBuilder();

	    getTreeText(buf, ctx);

	    int len = buf.length();
	    while (buf.charAt(len - 1) == ' ')
		len--;
	    buf.setLength(len);

	    return buf;
	}

	public static void getTreeText(StringBuilder buf, ParserRuleContext ctx) {
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

	public static boolean isIdentifierStart(char ch) {
	    // Corresponds to the "ID" rule in Calc.g4
	    if ((ch >= 'a' && ch <= 'z')
	     || (ch >= 'A' && ch <= 'Z')
	     || (ch == '_'))
		return true;
	    return false;
	}

	public static boolean isIdentifierPart(char ch) {
	    if (isIdentifierStart(ch))
		return true;

	    if (ch >= '0' && ch <= '9')
		return true;

	    return false;
	}

	public static void nullCheck(Object value, ParserRuleContext ctx) {
	    if (value == null)
		throw new CalcExprException("Value must not be null", ctx);
	}


	public static BigDecimal toDecimalValue(Object value, ParserRuleContext ctx) {
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

	public static BigInteger toIntegerValue(Object value, ParserRuleContext ctx) {
	    BigDecimal decValue = toDecimalValue(value, ctx);

	    try {
		return decValue.toBigIntegerExact();
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx);
	    }
	}

	public static Boolean toBooleanValue(Object value, ParserRuleContext ctx) {
	    nullCheck(value, ctx);

	    try {
		boolean boolValue = CharUtil.getBooleanValue(value);
		return Boolean.valueOf(boolValue);
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, ctx);
	    }
	}

	public static String toStringValue(Object result) {
	    return toStringValue(result, true, false, "");
	}

	@SuppressWarnings("unchecked")
	public static String toStringValue(Object result, boolean quote, boolean pretty, String indent) {
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

	public static String toStringValue(Map<String, Object> map, boolean quote, boolean pretty, String indent) {
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

	public static String toStringValue(List<Object> list, boolean quote, boolean pretty, String indent) {
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

	public static void convert(byte[] bytes, int radix, StringBuilder buf) {
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
	 * Returns the result of the "add" operation on the two values.
	 * <p> If either object is a string, do string concatenation.
	 * <p> Else convert to {@link BigDecimal} and do the addition.
	 *
	 * @param e1 The LHS value.
	 * @param e2 The RHS value.
	 * @param mc The {@code MathContext} to use in rouding the result.
	 * @param ctx The rule context (for error reporting).
	 * @return {@code e1 + e2}
	 */
	public static Object addOp(final Object e1, final Object e2, MathContext mc, ParserRuleContext ctx) {
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

}

