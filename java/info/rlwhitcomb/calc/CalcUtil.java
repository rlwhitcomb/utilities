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
 *	19-Jan-2021 (rlwhitcomb)
 *	    Add "length" and "scale" functions.
 *	30-Jan-2021 (rlwhitcomb)
 *	    Add BigFraction calculations.
 *	31-Jan-2021 (rlwhitcomb)
 *	    Need to pass around the MathContext for rounding. Reorder some
 * 	    parameters for consistency.
 *	02-Feb-2021 (rlwhitcomb)
 *	    Tweak the integer conversions.
 *	17-Feb-2021 (rlwhitcomb)
 *	    Add "visitor" parameters and evaluate functions.
 *	03-Mar-2021 (rlwhitcomb)
 *	    Change "getTreeText" to just return a String.
 *	07-Mar-2021 (rlwhitcomb)
 *	    Evaluate functions in "compareValues".
 *	08-Mar-2021 (rlwhitcomb)
 *	    Get the "silent" setting right everywhere when evaluating a function.
 *	26-Mar-2021 (rlwhitcomb)
 *	    Implement "compareValues" for lists and maps.
 *	20-Apr-2021 (rlwhitcomb)
 *	    Change spacing in "getTreeText()" to be context-sensitive and therefore
 *	    a lot more intelligent.
 */
package info.rlwhitcomb.calc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import info.rlwhitcomb.util.BigFraction;
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

	/**
	 * Flags for how to format tree text, depending on context.
	 */
	private static class TreeTextOptions
	{
		/** {@code false} means {@code ": "}, while {@code true} means {@code " : "} */
		boolean spaceColon = false;
		/** {@code false} means {@code "-"}, while {@code true} means {@code "- "} */
		boolean spaceMinus = true;
		/** {@code false} means {@code "["}, while {@code true} means {@code " ["} */
		boolean spaceOpenBracket = true;


		TreeTextOptions() {
		}

		TreeTextOptions(final TreeTextOptions other) {
		    this.spaceColon = other.spaceColon;
		    this.spaceMinus = other.spaceMinus;
		    this.spaceOpenBracket = other.spaceOpenBracket;
		}
	}


	public static String getTreeText(final ParserRuleContext ctx) {
	    StringBuilder buf = new StringBuilder();
	    TreeTextOptions options = new TreeTextOptions();

	    getTreeText(buf, ctx, options);

	    int len = buf.length();
	    while (buf.charAt(len - 1) == ' ')
		len--;
	    buf.setLength(len);

	    return buf.toString();
	}

	private static void getTreeText(final StringBuilder buf, final ParserRuleContext ctx, final TreeTextOptions options) {
	    TreeTextOptions localOptions = options;

	    // Some situations require context-sensitive alterations
	    if (ctx instanceof CalcParser.PairContext) {
		localOptions = new TreeTextOptions(options);
		localOptions.spaceColon = false;
	    }
	    else if (ctx instanceof CalcParser.EitherOrExprContext) {
		localOptions = new TreeTextOptions(options);
		localOptions.spaceColon = true;
	    }
	    else if (ctx instanceof CalcParser.NegPosExprContext) {
		localOptions = new TreeTextOptions(options);
		localOptions.spaceMinus = false;
	    }
	    else if (ctx instanceof CalcParser.ArrVarContext) {
		localOptions = new TreeTextOptions(options);
		localOptions.spaceOpenBracket = false;
	    }

	    for (ParseTree child : ctx.children) {
		if (child instanceof ParserRuleContext) {
		    getTreeText(buf, (ParserRuleContext) child, localOptions);
		}
		else {
		    boolean replace = false;
		    boolean space = true;
		    String childText = child.getText();

		    switch (childText) {
			case "(":
			case "{":
			     space = false;
			     break;
			case "[":
			     space = false;
			     if (!localOptions.spaceOpenBracket)
				replace = true;
			     break;
			case ",":
			case ")":
			case "]":
			case "}":
			    replace = true;
			    break;
			case ":":
			    if (!localOptions.spaceColon)
				replace = true;
			    break;
			case "-":
			    if (!localOptions.spaceMinus)
				space = false;
			    break;
			case ".":
			    replace = true;
			    space = false;
			    break;
			default:
			    break;
		    }

		    if (replace) {
			int len = buf.length();
			if (buf.charAt(len - 1) == ' ')
			    buf.replace(len - 1, len, childText);
			else
			    buf.append(childText);
		    }
		    else {
			buf.append(childText);
		    }

		    if (space) {
			buf.append(' ');
		    }
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


	public static BigDecimal toDecimalValue(final CalcObjectVisitor visitor, final Object obj, final MathContext mc, final ParserRuleContext ctx) {
	    Object value = visitor.evaluateFunction(obj);

	    nullCheck(value, ctx);

	    if (value instanceof BigDecimal)
		return (BigDecimal) value;
	    else if (value instanceof BigInteger)
		return new BigDecimal((BigInteger) value);
	    else if (value instanceof BigFraction)
		return ((BigFraction) value).toDecimal(mc);
	    else if (value instanceof String)
		return new BigDecimal((String) value);
	    else if (value instanceof Boolean)
		return ((Boolean) value).booleanValue() ? BigDecimal.ONE : BigDecimal.ZERO;
	    else if (value instanceof Double || value instanceof Float)
		return new BigDecimal(((Number) value).doubleValue());
	    else if (value instanceof Number)
		return BigDecimal.valueOf(((Number) value).longValue());

	    // Here we are not able to make sense of the object, so we have an error
	    String typeName = value.getClass().getSimpleName();
	    if (value instanceof Map)
		typeName = "object";
	    else if (value instanceof List)
		typeName = "array";
	    throw new CalcExprException(ctx, "%calc#noConvertDecimal", typeName);
	}

	public static BigFraction toFractionValue(final CalcObjectVisitor visitor, final Object obj, final ParserRuleContext ctx) {
	    Object value = visitor.evaluateFunction(obj);

	    nullCheck(value, ctx);

	    if (value instanceof BigFraction)
		return (BigFraction) value;
	    else if (value instanceof BigDecimal)
		return new BigFraction((BigDecimal) value);
	    else if (value instanceof BigInteger)
		return new BigFraction((BigInteger) value);
	    else if (value instanceof String)
		return BigFraction.valueOf((String) value);
	    else if (value instanceof Boolean)
		return ((Boolean) value).booleanValue() ? BigFraction.ONE : BigFraction.ZERO;
	    else if (value instanceof Double || value instanceof Float)
		return new BigFraction(new BigDecimal(((Number) value).doubleValue()));
	    else if (value instanceof Number)
		return new BigFraction(((Number) value).longValue());

	    // Here we are not able to make sense of the object, so we have an error
	    String typeName = value.getClass().getSimpleName();
	    if (value instanceof Map)
		typeName = "object";
	    else if (value instanceof List)
		typeName = "array";
	    throw new CalcExprException(ctx, "%calc#noConvertFraction", typeName);
	}

	public static BigInteger toIntegerValue(final CalcObjectVisitor visitor, final Object value, final MathContext mc, final ParserRuleContext ctx) {
	    try {
		if (value instanceof BigInteger) {
		    return (BigInteger) value;
		}
		else if (value instanceof BigFraction) {
		    return ((BigFraction) value).toIntegerExact();
		}
		else {
		    return toDecimalValue(visitor, value, mc, ctx).toBigIntegerExact();
		}
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx);
	    }
	}

	public static int toIntValue(final CalcObjectVisitor visitor, final Object value, final MathContext mc, final ParserRuleContext ctx) {
	    try {
		if (value instanceof BigInteger) {
		    return ((BigInteger) value).intValueExact();
		}
		else if (value instanceof BigFraction) {
		    return ((BigFraction) value).intValueExact();
		}
		else {
		    return toDecimalValue(visitor, value, mc, ctx).intValueExact();
		}
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx);
	    }
	}

	public static Boolean toBooleanValue(final CalcObjectVisitor visitor, final Object obj, final ParserRuleContext ctx) {
	    Object value = visitor.evaluateFunction(obj);

	    nullCheck(value, ctx);

	    try {
		boolean boolValue = CharUtil.getBooleanValue(value);
		return Boolean.valueOf(boolValue);
	    }
	    catch (IllegalArgumentException iae) {
		throw new CalcExprException(iae, ctx);
	    }
	}

	public static String toStringValue(final CalcObjectVisitor visitor, final Object result) {
	    return toStringValue(visitor, result, true, false, "");
	}

	@SuppressWarnings("unchecked")
	public static String toStringValue(final CalcObjectVisitor visitor, final Object obj, final boolean quote, final boolean pretty, final String indent) {
	    Object result = visitor.evaluateFunction(obj);

	    if (result == null) {
		return quote ? "<null>" : "";
	    }
	    else if (result instanceof String) {
		if (quote)
		    return CharUtil.addDoubleQuotes((String) result);
		else
		    return (String) result;
	    }
	    else if (result instanceof BigDecimal) {
		return ((BigDecimal) result).toPlainString();
	    }
	    else if (result instanceof BigFraction) {
		return ((BigFraction) result).toString();
	    }
	    else if (result instanceof Map) {
		return toStringValue(visitor, (Map<String, Object>) result, quote, pretty, indent);
	    }
	    else if (result instanceof List) {
		return toStringValue(visitor, (List<Object>) result, quote, pretty, indent);
	    }

	    // Any other type, just get the string representation
	    return result.toString();
	}

	public static String toStringValue(final CalcObjectVisitor visitor, final Map<String, Object> map, final boolean quote, final boolean pretty, final String indent) {
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
		    buf.append(toStringValue(visitor, entry.getValue(), quote, pretty, myIndent));
		}
		buf.append(pretty ? "\n" + indent + "}" : " }");
	    }
	    else {
		buf.append("{ }");
	    }
	    return buf.toString();
	}

	public static String toStringValue(final CalcObjectVisitor visitor, final List<Object> list, final boolean quote, final boolean pretty, final String indent) {
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
		    buf.append(toStringValue(visitor, value, quote, pretty, myIndent));
		}
		buf.append(pretty ? "\n" + indent + "]" : " ]");
	    }
	    else {
		buf.append("[ ]");
	    }
	    return buf.toString();
	}

	/**
	 * Compute the "length" of something.
	 * <p> Differs depending on the object:
	 * <ul><li>{@code Object} = (possibly recursive) number of entries</li>
	 * <li>{@code Array} = (possibly recursive) length of the array</li>
	 * <li>{@code String} = code point length</li>
	 * <li>{@code BigDecimal} = precision (or number of significant digits)</li>
	 * <li>{@code BigFraction} = convert to BigDecimal and compute
	 * <li>{@code BigInteger} = number of digits</li>
	 * <li>{@code Boolean} = one</li>
	 * <li>{@code Null} = zero</li>
	 * </ul>
	 *
	 * @param visitor	The visitor object (for function evaluation).
	 * @param valueObj	The object to be "measured".
	 * @param ctx		The context to use for error reporting.
	 * @param recursive	Whether or not to descend into the object / array or not.
	 * @return		The "length" according to the above rules.
	 */
	public static int length(final CalcObjectVisitor visitor, final Object valueObj, final ParserRuleContext ctx, final boolean recursive) {
	    Object obj = visitor.evaluateFunction(valueObj);

	    if (obj == null)
		return 0;
	    if (obj instanceof Boolean)
		return 1;
	    if (obj instanceof BigInteger) {
		String strValue = ((BigInteger) obj).toString();
		// Number of digits does not include the leading sign (if present)
		if (strValue.charAt(0) == '-')
		    return strValue.length() - 1;
		else
		    return strValue.length();
	    }
	    if (obj instanceof BigDecimal)
		return ((BigDecimal) obj).precision();
	    if (obj instanceof BigFraction)
		return ((BigFraction) obj).toDecimal().precision();	// ?? not really helpful, probably
	    if (obj instanceof String) {
		String str = (String) obj;
		return str.codePointCount(0, str.length());
	    }
	    if (obj instanceof List) {
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) obj;
		if (recursive) {
		    int len = 0;
		    for (Object listObj : list) {
			if (listObj instanceof List || listObj instanceof Map)
			    len += length(visitor, listObj, ctx, recursive);
			else
			    len++;	// Note: this will count null entries as one
		    }
		    return len;
		}
		else {
		    return list.size();
		}
	    }
	    if (obj instanceof Map) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) obj;
		if (recursive) {
		    int len = 0;
		    for (Object mapObj : map.values()) {
			if (mapObj instanceof List || mapObj instanceof Map)
			    len += length(visitor, mapObj, ctx, recursive);
			else
			    len++;	// Note: this will count null values as one
		    }
		    return len;
		}
		else {
		    return map.size();
		}
	    }

	    throw new CalcExprException(ctx, "%calc#unknownType", obj.getClass().getSimpleName());
	}

	/**
	 * Compute the "scale" of the given object:
	 * <ul><li>{@code BigDecimal} = the {@code scale()} value.</li>
	 * <li>{@code Object} = the non-recursive size (number of entries)</li>
	 * <li>{@code Array} = the non-recursive size (number of entries)</li>
	 * <li>everything else = {@code 0}</li>
	 * </ul>
	 *
	 * @param visitor	The visitor (for function evaluation).
	 * @param valueObj	The object to interrogate.
	 * @param ctx		The context to use for error reporting.
	 * @return		The scale of the object.
	 */
	public static int scale(final CalcObjectVisitor visitor, final Object valueObj, final ParserRuleContext ctx) {
	    Object obj = visitor.evaluateFunction(valueObj);

	    if (obj instanceof BigDecimal)
		return ((BigDecimal) obj).scale();
	    if (obj instanceof BigFraction)
		return ((BigFraction) obj).toDecimal().scale();
	    if (obj instanceof List || obj instanceof Map)
		return length(visitor, obj, ctx, false);
	    return 0;
	}

	/**
	 * Convert an array of bytes to their numeric representation in the given radix.
	 *
	 * @param bytes	The set of bytes to convert.
	 * @param radix	Radix to use for conversion (supports 2, 8, and 16).
	 * @param buf	The buffer to append to.
	 */
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
	 * @param mc         Rounding mode used when converting to decimal.
	 * @param strict     Whether or not the object classes must match for the comparison.
	 * @param allowNulls Some comparisons (strings) can be compared even if one or both operands are null.
	 * @return {@code -1} if the first object is "less than" the second,
	 *         {@code 0} if the objects are "equal",
	 *         {@code +1} if the first object is "greater than" the second.
	 */
	public static int compareValues(final CalcObjectVisitor visitor,
		final ParserRuleContext ctx1, final ParserRuleContext ctx2,
		final MathContext mc, final boolean strict, final boolean allowNulls) {
	    return compareValues(visitor, ctx1, ctx2, visitor.visit(ctx1), visitor.visit(ctx2),
		mc, strict, allowNulls);
	}

	/**
	 * Compare two objects of possibly differing types.
	 * <p> This is the recursive version, for use when comparing elements of lists and maps.
	 *
	 * @param visitor    The object visitor used to calculate the values.
	 * @param ctx1       The Rule context of the first operand.
	 * @param ctx2       The Rule context of the second operand.
	 * @param obj1       The first object to compare.
	 * @param obj2       The second object to compare.
	 * @param mc         Rounding mode used when converting to decimal.
	 * @param strict     Whether or not the object classes must match for the comparison.
	 * @param allowNulls Some comparisons (strings) can be compared even if one or both operands are null.
	 * @return {@code -1} if the first object is "less than" the second,
	 *         {@code 0} if the objects are "equal",
	 *         {@code +1} if the first object is "greater than" the second.
	 */
	private static int compareValues(final CalcObjectVisitor visitor,
		final ParserRuleContext ctx1, final ParserRuleContext ctx2,
		final Object obj1, final Object obj2,
		final MathContext mc, final boolean strict, final boolean allowNulls) {
	    Object e1 = visitor.evaluateFunction(obj1);
	    Object e2 = visitor.evaluateFunction(obj2);

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
		BigDecimal d1 = toDecimalValue(visitor, e1, mc, ctx1);
		BigDecimal d2 = toDecimalValue(visitor, e2, mc, ctx2);
		return d1.compareTo(d2);
	    }
	    else if (e1 instanceof BigInteger || e2 instanceof BigInteger) {
		BigInteger i1 = toIntegerValue(visitor, e1, mc, ctx1);
		BigInteger i2 = toIntegerValue(visitor, e2, mc, ctx2);
		return i1.compareTo(i2);
	    }
	    else if (e1 instanceof BigFraction || e2 instanceof BigFraction) {
		BigFraction f1 = toFractionValue(visitor, e1, ctx1);
		BigFraction f2 = toFractionValue(visitor, e2, ctx2);
		return f1.compareTo(f2);
	    }
	    else if (e1 instanceof Boolean || e2 instanceof Boolean) {
		Boolean b1 = toBooleanValue(visitor, e1, ctx1);
		Boolean b2 = toBooleanValue(visitor, e2, ctx2);
		return b1.compareTo(b2);
	    }
	    else if (e1 instanceof List && e2 instanceof List) {
		@SuppressWarnings("unchecked")
		List<Object> list1 = (List<Object>) e1;
		@SuppressWarnings("unchecked")
		List<Object> list2 = (List<Object>) e2;
		int size1 = list1.size();
		int size2 = list2.size();

		if (size1 != size2)
		    return Integer.signum(size1 - size2);

		for (int i = 0; i < size1; i++) {
		    Object o1 = list1.get(i);
		    Object o2 = list2.get(i);
		    int ret = compareValues(visitor, ctx1, ctx2, o1, o2, mc, strict, allowNulls);
		    if (ret != 0)
			return ret;
		}
		return 0;
	    }
	    else if (e1 instanceof Map && e2 instanceof Map) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map1 = (Map<String, Object>) e1;
		@SuppressWarnings("unchecked")
		Map<String, Object> map2 = (Map<String, Object>) e2;
		int size1 = map1.size();
		int size2 = map2.size();

		// The "smaller" map is less than the "bigger" one
		if (size1 != size2)
		    return Integer.signum(size1 - size2);

		// First, compare the key sets -- if they are different then sort and compare them lexicographically
		Set<String> keySet1 = map1.keySet();
		Set<String> keySet2 = map2.keySet();
		if (!keySet1.equals(keySet2)) {
		    TreeSet<String> sortedKeys1 = new TreeSet<>(keySet1);
		    TreeSet<String> sortedKeys2 = new TreeSet<>(keySet2);

		    String key1, key2;
		    while ((key1 = sortedKeys1.pollFirst()) != null) {
			key2 = sortedKeys2.pollFirst();
			int ret = key1.compareTo(key2);
			if (ret != 0)
			    return ret;
		    }
		}

		// If the key sets are the same, then iterate through the values and compare them
		for (String key : keySet1) {
		    Object value1 = map1.get(key);
		    Object value2 = map2.get(key);
		    int ret = compareValues(visitor, ctx1, ctx2, value1, value2, mc, strict, allowNulls);
		    if (ret != 0)
			return ret;
		}

		// Finally, if no differences were found, the maps are the same
		return 0;
	    }

	    throw new CalcExprException(ctx1, "%calc#unknownType", e1.getClass().getSimpleName());
	}

	/**
	 * Returns the result of the "add" operation on the two values.
	 * <p> If either object is a string, do string concatenation.
	 * <p> Else convert to {@link BigDecimal} or {@link BigFraction}
	 * and do the addition.
	 *
	 * @param visitor  The visitor (for function evaluation).
	 * @param e1       The LHS operand.
	 * @param e2       The RHS operand.
	 * @param ctx1     The Rule context for the first operand (for error reporting).
	 * @param ctx2     The Rule context for the second operand.
	 * @param mc       The {@code MathContext} to use in rounding the result.
	 * @param rational Whether we're doing rational ({@code true}) or decimal arithmetic.
	 * @return {@code e1 + e2}
	 */
	public static Object addOp(final CalcObjectVisitor visitor,
		final Object e1, final Object e2,
		final ParserRuleContext ctx1, final ParserRuleContext ctx2,
		final MathContext mc, final boolean rational) {
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
	    if (rational) {
		BigFraction f1 = toFractionValue(visitor, e1, ctx1);
		BigFraction f2 = toFractionValue(visitor, e2, ctx2);

		return f1.add(f2);
	    }
	    else {
		BigDecimal d1 = toDecimalValue(visitor, e1, mc, ctx1);
		BigDecimal d2 = toDecimalValue(visitor, e2, mc, ctx2);

		return d1.add(d2, mc);
	    }
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

