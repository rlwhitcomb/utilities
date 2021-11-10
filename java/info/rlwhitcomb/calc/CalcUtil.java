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
 *	21-Apr-2021 (rlwhitcomb)
 *	    Make the lowest-level "compareValues" method public for use in "case" statement.
 *	26-Apr-2021 (rlwhitcomb)
 *	    More generally remove whitespace at the end of tree text.
 *	26-Apr-2021 (rlwhitcomb)
 *	    New method to format numbers with/without thousands separators.
 *	27-Apr-2021 (rlwhitcomb)
 *	    Treat empty and non-empty strings as valid boolean values (as does JavaScript).
 *	07-Jun-2021 (rlwhitcomb)
 *	    Fix thousands separator formatting with negative scale.
 *	02-Jul-2021 (rlwhitcomb)
 *	    Changes for always displaying thousands separators.
 *	10-Jul-2021 (rlwhitcomb)
 *	    Implement "ignore case" functions for variables / members.
 *	09-Sep-2021 (rlwhitcomb)
 *	    More Javadoc. Move the "istring" processing into here for member names.
 *	    Fix an issue with string member names with "handed" quotes.
 *	21-Sep-2021 (rlwhitcomb)
 *	    Add "fixup" method to strip trailing (unnecessary) zeros.
 *	06-Oct-2021 (rlwhitcomb)
 *	    #24 Full implementation of function parameters.
 *	07-Oct-2021 (rlwhitcomb)
 *	    Add context parameter to "toStringValue" and "evaluateFunction".
 *	14-Oct-2021 (rlwhitcomb)
 *	    New "getArrayValue" method.
 *	16-Oct-2021 (rlwhitcomb)
 *	    Add "ignoreCase" parameter to "compareValues".
 *	18-Oct-2021 (rlwhitcomb)
 *	    #34: Remove "getArrayValue" now that it is not used anywhere.
 *	26-Oct-2021 (rlwhitcomb)
 *	    #31: Change the way "convert" works to make real escape sequences.
 *	28-Oct-2021 (rlwhitcomb)
 *	    Fix addOp if the values are functions that must be evaluated.
 *	02-Nov-2021 (rlwhitcomb)
 *	    #56: Take out extra space before "(" in function call.
 *	04-Nov-2021 (rlwhitcomb)
 *	    #71: Add natural order comparator to "compareValues".
 *	07-Nov-2021 (rlwhitcomb)
 *	    #73: Strip outer quotes during interpolation.
 *	    #69: Define "$#" and "$*" inside interpolations.
 *	09-Nov-2021 (rlwhitcomb)
 *	    #78: Make "compareStrings" public for use in "min", "max".
 */
package info.rlwhitcomb.calc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import org.padler.natorder.NaturalOrderComparator;

import info.rlwhitcomb.calc.CalcObjectVisitor.Settings;
import info.rlwhitcomb.util.BigFraction;
import info.rlwhitcomb.util.CharUtil;
import static info.rlwhitcomb.util.CharUtil.Justification;


/**
 * Static utility methods for the {@link CalcObjectVisitor} class --
 * mostly object conversion and common code.
 */
public final class CalcUtil
{
	/** Natural order comparator, case-sensitive. */
	private static final NaturalOrderComparator NATURAL_SENSITIVE_COMPARATOR = new NaturalOrderComparator(true);

	/** Natural order comparator, case-insensitive. */
	private static final NaturalOrderComparator NATURAL_INSENSITIVE_COMPARATOR = new NaturalOrderComparator(false);


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
		/** {@code false} means {@code "("}, while {@code true} means ({@code " ("} */
		boolean spaceOpenParen = true;


		TreeTextOptions() {
		}

		TreeTextOptions(final TreeTextOptions other) {
		    this.spaceColon = other.spaceColon;
		    this.spaceMinus = other.spaceMinus;
		    this.spaceOpenBracket = other.spaceOpenBracket;
		    this.spaceOpenParen = other.spaceOpenParen;
		}
	}


	/**
	 * Get a nicely formatted string of the contents of the given parse tree.
	 *
	 * @param ctx Root node of the parse tree to display.
	 * @return    The formatted string of the contents.
	 */
	public static String getTreeText(final ParseTree ctx) {
	    StringBuilder buf = new StringBuilder();
	    TreeTextOptions options = new TreeTextOptions();

	    getTreeText(buf, ctx, options);

	    int len = buf.length();
	    while (len > 0 && Character.isWhitespace(buf.charAt(len - 1)))
		len--;
	    buf.setLength(len);

	    return buf.toString();
	}

	/**
	 * The workhorse recursive method for retrieving / formatting parse tree text.
	 * <p> This method makes context-sensitive decisions about spacing and other
	 * formatting embellishments.
	 *
	 * @param buf     The buffer where we're building the final text.
	 * @param ctx     Current parse tree node.
	 * @param options The current options used for adjusting spacing.
	 */
	private static void getTreeText(final StringBuilder buf, final ParseTree ctx, final TreeTextOptions options) {
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
	    else if (ctx instanceof CalcParser.ActualParamsContext) {
		localOptions = new TreeTextOptions(options);
		localOptions.spaceOpenParen = false;
	    }

	    for (int i = 0; i < ctx.getChildCount(); i++) {
		ParseTree child = ctx.getChild(i);
		if (child.getChildCount() > 0) {
		    getTreeText(buf, child, localOptions);
		}
		else {
		    boolean replace = false;
		    boolean space = true;
		    String childText = child.getText();

		    switch (childText) {
			case "(":
			     space = false;
			     if (!localOptions.spaceOpenParen)
				replace = true;
			     break;
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

	/**
	 * Is this character a valid start for an identifier name?
	 * <p> Corresponds to the {@code ID} rule in the Calc.g4 grammar.
	 *
	 * @param ch	The character to check.
	 * @return	Whether or not this is a valid identifier start character.
	 */
	public static boolean isIdentifierStart(final char ch) {
	    // Corresponds to the "ID" rule in Calc.g4
	    if ((ch >= 'a' && ch <= 'z')
	     || (ch >= 'A' && ch <= 'Z')
	     || (ch == '_'))
		return true;
	    return false;
	}

	/**
	 * Is this character a valid identifier character (after the start)?
	 * <p> Corresponds to the grammar for ID.
	 *
	 * @param ch	The character to check.
	 * @return	Whether the character is a valid following part of an identifier.
	 */
	public static boolean isIdentifierPart(final char ch) {
	    if (isIdentifierStart(ch))
		return true;

	    if (ch >= '0' && ch <= '9')
		return true;

	    return false;
	}

	/**
	 * Check if the given value is {@code null} and throw an exception if so.
	 *
	 * @param value	The value to check.
	 * @param ctx	Parsing context for error reporting.
	 * @throws CalcExprException if the value is null
	 */
	public static void nullCheck(final Object value, final ParserRuleContext ctx) {
	    if (value == null)
		throw new CalcExprException(ctx, "%calc#valueNotNull", getTreeText(ctx));
	}

	/**
	 * Fixup a {@link BigDecimal} value by stripping trailing zeros for a nicer presentation.
	 *
	 * @param bd	The candidate value.
	 * @return	The numerically equivalent value with no trailing zeros.
	 */
	public static BigDecimal fixup(final BigDecimal bd) {
	    return bd.stripTrailingZeros();
	}

	/**
	 * Cast or convert the given value to a {@link BigDecimal} value for use in calculations.
	 *
	 * @param visitor	The visitor, used to evaluate expressions.
	 * @param obj		The object to convert.
	 * @param mc		Math precision to use for any necessary conversions.
	 * @param ctx		The parse tree (used for error reporting).
	 * @return		A decimal value converted (if needed) from the input object.
	 * @throws CalcExprException for null input values, or other errors in conversion.
	 */
	public static BigDecimal toDecimalValue(final CalcObjectVisitor visitor, final Object obj, final MathContext mc, final ParserRuleContext ctx) {
	    Object value = visitor.evaluateFunction(ctx, obj);
	    return convertToDecimal(value, mc, ctx);
	}

	public static BigDecimal convertToDecimal(final Object value, final MathContext mc, final ParserRuleContext ctx) {
	    nullCheck(value, ctx);

	    if (value instanceof BigDecimal)
		return fixup((BigDecimal) value);
	    else if (value instanceof BigInteger)
		return fixup(new BigDecimal((BigInteger) value));
	    else if (value instanceof BigFraction)
		return fixup(((BigFraction) value).toDecimal(mc));
	    else if (value instanceof String)
		return fixup(new BigDecimal((String) value));
	    else if (value instanceof Boolean)
		return ((Boolean) value).booleanValue() ? BigDecimal.ONE : BigDecimal.ZERO;
	    else if (value instanceof Double || value instanceof Float)
		return fixup(new BigDecimal(((Number) value).doubleValue()));
	    else if (value instanceof Number)
		return fixup(BigDecimal.valueOf(((Number) value).longValue()));

	    // Here we are not able to make sense of the object, so we have an error
	    String typeName = value.getClass().getSimpleName();
	    if (value instanceof ObjectScope)
		typeName = "object";
	    else if (value instanceof ArrayScope)
		typeName = "array";
	    throw new CalcExprException(ctx, "%calc#noConvertDecimal", typeName);
	}

	/**
	 * Cast or convert the given value to a {@link BigFraction} value for use in rational mode calculations.
	 *
	 * @param visitor	The visitor, used to evaluate expressions.
	 * @param obj		The input object value to be converted.
	 * @param ctx		The parse tree context (for error reporting).
	 * @return		The converted fraction value from the input.
	 * @throws CalcExprException for null inputs, or other errors from conversion.
	 */
	public static BigFraction toFractionValue(final CalcObjectVisitor visitor, final Object obj, final ParserRuleContext ctx) {
	    Object value = visitor.evaluateFunction(ctx, obj);
	    return convertToFraction(value, ctx);
	}

	public static BigFraction convertToFraction(final Object value, final ParserRuleContext ctx) {
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
	    if (value instanceof ObjectScope)
		typeName = "object";
	    else if (value instanceof ArrayScope)
		typeName = "array";
	    throw new CalcExprException(ctx, "%calc#noConvertFraction", typeName);
	}

	/**
	 * Cast or convert the given object value to a {@link BigInteger} for certain integer calculations.
	 *
	 * @param visitor	The visitor used to evaluate expressions.
	 * @param value		The input value to convert.
	 * @param mc		The math context to use for any conversions from decimal values.
	 * @param ctx		The parse tree context, used for error reporting.
	 * @return		The converted value, if possible.
	 * @throws CalcExprException if the value is not or cannot be converted to an exact integer value.
	 */
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

	/**
	 * Cast or convert the given object value to a regular integer value for certain parameter values.
	 *
	 * @param visitor	The visitor used to evaluate expressions.
	 * @param value		The input value to convert.
	 * @param mc		The math context to use for any conversions from decimal values.
	 * @param ctx		The parse tree context, used for error reporting.
	 * @return		The converted value, if possible.
	 * @throws CalcExprException if the value is not or cannot be converted to an exact integer value.
	 */
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

	/**
	 * Cast or convert the given value to a boolean, using JavaScript semantics for "truthy" values (that is,
	 * null or empty strings are {@code false} and string which are non-empty are {@code true}.
	 *
	 * @param visitor	The visitor for evaluating expressions.
	 * @param obj		The input object to convert.
	 * @param ctx		The parse tree context for error reporting.
	 * @return		The converted boolean value from the input.
	 * @throws CalcExprException if there was a problem (for instance, in evaluating an expression).
	 */
	public static Boolean toBooleanValue(final CalcObjectVisitor visitor, final Object obj, final ParserRuleContext ctx) {
	    Object value = visitor.evaluateFunction(ctx, obj);

	    // Compatibility with JavaScript here...
	    if (CharUtil.isNullOrEmpty(value))
		return Boolean.FALSE;

	    try {
		boolean boolValue = CharUtil.getBooleanValue(value);
		return Boolean.valueOf(boolValue);
	    }
	    catch (IllegalArgumentException iae) {
		// Even if the string isn't a "valid" boolean value, accept a
		// non-empty string as "true" (as does JavaScript)
		if ((value instanceof String) && !((String) value).isEmpty())
		    return Boolean.TRUE;

		throw new CalcExprException(iae, ctx);
	    }
	}

	/**
	 * Convenience method to convert a value to a string, using the most common parameters.
	 *
	 * @param visitor	The tree visitor, for calculating expressions.
	 * @param ctx		The parsing context, for error reporting.
	 * @param result	The input value to be converted.
	 * @param separators	Whether or not to use thousands separators when converting numeric values.
	 * @return		The converted string value.
	 * @see #toStringValue(CalcObjectVisitor, ParserRuleContext, Object, boolean, boolean, boolean, String)
	 */
	public static String toStringValue(
		final CalcObjectVisitor visitor,
		final ParserRuleContext ctx,
		final Object result,
		final boolean separators) {
	    return toStringValue(visitor, ctx, result, true, false, separators, "");
	}

	/**
	 * The workhorse, recursive method used to convert values to strings.
	 *
	 * @param visitor	The outermost visitor object that is being used to calculate everything.
	 * @param ctx		The parsing context, for error reporting.
	 * @param obj		The input object to be converted to a string.
	 * @param quote		Whether or not the resulting string should be quoted (double quotes) if
	 *			the input is an actual string object.
	 * @param pretty	Whether or not to "pretty" print the contents of an object (map) or list (array).
	 * @param separators	Should thousands separators be used for numeric values?
	 * @param indent	The recursive indentation for pretty printing.
	 * @return		The formatted string representation of the input object.
	 */
	@SuppressWarnings("unchecked")
	public static String toStringValue(
		final CalcObjectVisitor visitor,
		final ParserRuleContext ctx,
		final Object obj,
		final boolean quote,
		final boolean pretty,
		final boolean separators,
		final String indent)
	{
	    Object result = visitor.evaluateFunction(ctx, obj);

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
		return formatWithSeparators(((BigDecimal) result), separators);
	    }
	    else if (result instanceof BigInteger) {
		if (separators)
		    return String.format("%1$,d", (BigInteger)result);
		else
		    return result.toString();
	    }
	    else if (result instanceof BigFraction) {
		return ((BigFraction) result).toString();
	    }
	    else if (result instanceof ObjectScope) {
		return toStringValue(visitor, ctx, ((ObjectScope) result).map(), quote, pretty, separators, indent);
	    }
	    else if (result instanceof ArrayScope) {
		return toStringValue(visitor, ctx, ((ArrayScope) result).list(), quote, pretty, separators, indent);
	    }

	    // Any other type, just get the string representation
	    return result.toString();
	}

	/**
	 * The recursive method used to convert an object (map) to a string.
	 *
	 * @param visitor	The outermost visitor object that is being used to calculate everything.
	 * @param ctx		The parsing context, for error reporting.
	 * @param map		The input object (map) to be converted.
	 * @param quote		Whether or not actual string objects should be double-quoted in the result.
	 * @param pretty	Whether or not to "pretty" print the contents of an object (map) or list (array).
	 * @param separators	Should thousands separators be used for numeric values?
	 * @param indent	The recursive indentation for pretty printing.
	 * @return		The formatted string representation of the input object.
	 */
	public static String toStringValue(
		final CalcObjectVisitor visitor,
		final ParserRuleContext ctx,
		final Map<String, Object> map,
		final boolean quote,
		final boolean pretty,
		final boolean separators,
		final String indent)
	{
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
		    buf.append(toStringValue(visitor, ctx, entry.getValue(), quote, pretty, separators, myIndent));
		}
		buf.append(pretty ? "\n" + indent + "}" : " }");
	    }
	    else {
		buf.append("{ }");
	    }
	    return buf.toString();
	}

	/**
	 * The recursive method used to convert a list (array) to a string.
	 *
	 * @param visitor	The outermost visitor object that is being used to calculate everything.
	 * @param ctx		The parsing context, for error reporting.
	 * @param list		The input list (array) to be converted.
	 * @param quote		Whether or not actual string objects should be double-quoted in the result.
	 * @param pretty	Whether or not to "pretty" print the contents of an object (map) or list (array).
	 * @param separators	Should thousands separators be used for numeric values?
	 * @param indent	The recursive indentation for pretty printing.
	 * @return		The formatted string representation of the input list.
	 */
	public static String toStringValue(
		final CalcObjectVisitor visitor,
		final ParserRuleContext ctx,
		final List<Object> list,
		final boolean quote,
		final boolean pretty,
		final boolean separators,
		final String indent)
	{
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
		    buf.append(toStringValue(visitor, ctx, value, quote, pretty, separators, myIndent));
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
	    Object obj = visitor.evaluateFunction(ctx, valueObj);

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
	    if (obj instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope array = (ArrayScope) obj;
		if (recursive) {
		    int len = 0;
		    for (Object listObj : array.list()) {
			if (listObj instanceof ArrayScope || listObj instanceof ObjectScope)
			    len += length(visitor, listObj, ctx, recursive);
			else
			    len++;	// Note: this will count null entries as one
		    }
		    return len;
		}
		else {
		    return array.size();
		}
	    }
	    if (obj instanceof ObjectScope) {
		@SuppressWarnings("unchecked")
		ObjectScope map = (ObjectScope) obj;
		if (recursive) {
		    int len = 0;
		    for (Object mapObj : map.values()) {
			if (mapObj instanceof ArrayScope || mapObj instanceof ObjectScope)
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
	    Object obj = visitor.evaluateFunction(ctx, valueObj);

	    if (obj instanceof BigDecimal)
		return ((BigDecimal) obj).scale();
	    if (obj instanceof BigFraction)
		return ((BigFraction) obj).toDecimal().scale();
	    if (obj instanceof ArrayScope || obj instanceof ObjectScope)
		return length(visitor, obj, ctx, false);
	    return 0;
	}

	/**
	 * Convert an array of bytes to their numeric representation in the given radix.
	 *
	 * @param bytes  The set of bytes to convert.
	 * @param radix  Radix to use for conversion (supports 2, 8, and 16).
	 * @param upper  Whether to use UPPER case characters (hex only).
	 * @param escape Use escape sequences (suitable for strings)?
	 * @param buf	 The buffer to append to.
	 */
	public static void convert(
		final byte[] bytes,
		final int radix,
		final boolean upper,
		final boolean escape,
		final StringBuilder buf)
	{
	    char formatChar = ' ';
	    int padWidth = 0;
	    switch (radix) {
		case 2:  formatChar = 'B'; padWidth = 8; break;
		case 8:  formatChar = 'o'; padWidth = 3; break;
		case 16: formatChar = 'u'; padWidth = escape ? 4 : 2; break;
	    }
	    for (byte b : bytes) {
		String number = Integer.toString(Byte.toUnsignedInt(b), radix);
		if (upper)
		    number = number.toUpperCase();
		if (escape)
		    buf.append('\\').append(formatChar);
		CharUtil.padToWidth(buf, number, padWidth, '0', Justification.RIGHT);
	    }
	}


	/**
	 * Compare string values using either the natural-order comparator or the standard string comparison, either ignoring case or not.
	 *
	 * @param s1		First string to compare.
	 * @param s2		Second string to compare to it.
	 * @param ignoreCase	Whether to ignore letter case in the comparison.
	 * @param naturalOrder	Whether to use the natural order comparator.
	 * @return		<code>&lt; 0</code> if s1 compares "before" s2, <code>== 0</code> if they compare the same,
	 *			<code>&gt; 0</code> if s1 compares "after" s2
	 */
	public static int compareStrings(final String s1, final String s2, final boolean ignoreCase, final boolean naturalOrder) {
	    return naturalOrder
		? (ignoreCase
			? NATURAL_INSENSITIVE_COMPARATOR.compare(s1, s2)
			: NATURAL_SENSITIVE_COMPARATOR.compare(s1, s2))
		: (ignoreCase
			? s1.compareToIgnoreCase(s2)
			: s1.compareTo(s2));
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
		mc, strict, allowNulls, false, false);
	}

	/**
	 * Compare two objects of possibly differing types.
	 * <p> This is the recursive version, for use when comparing elements of lists and maps.
	 *
	 * @param visitor      The object visitor used to calculate the values.
	 * @param ctx1         The Rule context of the first operand.
	 * @param ctx2         The Rule context of the second operand.
	 * @param obj1         The first object to compare.
	 * @param obj2         The second object to compare.
	 * @param mc           Rounding mode used when converting to decimal.
	 * @param strict       Whether or not the object classes must match for the comparison.
	 * @param allowNulls   Some comparisons (strings) can be compared even if one or both operands are null.
	 * @param ignoreCase   For strings, whether to ignore case or not.
	 * @param naturalOrder For strings, whether to use natural ordering or normal string ordering.
	 * @return {@code -1} if the first object is "less than" the second,
	 *         {@code 0} if the objects are "equal",
	 *         {@code +1} if the first object is "greater than" the second.
	 */
	public static int compareValues(final CalcObjectVisitor visitor,
		final ParserRuleContext ctx1, final ParserRuleContext ctx2,
		final Object obj1, final Object obj2,
		final MathContext mc, final boolean strict, final boolean allowNulls,
		final boolean ignoreCase, final boolean naturalOrder) {
	    Object e1 = visitor.evaluateFunction(ctx1, obj1);
	    Object e2 = visitor.evaluateFunction(ctx2, obj2);

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

		return compareStrings(s1, s2, ignoreCase, naturalOrder);
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
	    else if (e1 instanceof ArrayScope && e2 instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope list1 = (ArrayScope) e1;
		@SuppressWarnings("unchecked")
		ArrayScope list2 = (ArrayScope) e2;
		int size1 = list1.size();
		int size2 = list2.size();

		if (size1 != size2)
		    return Integer.signum(size1 - size2);

		for (int i = 0; i < size1; i++) {
		    Object o1 = list1.getValue(i);
		    Object o2 = list2.getValue(i);

		    int ret = compareValues(visitor, ctx1, ctx2, o1, o2, mc, strict, allowNulls, ignoreCase, naturalOrder);
		    if (ret != 0)
			return ret;
		}
		return 0;
	    }
	    else if (e1 instanceof ObjectScope && e2 instanceof ObjectScope) {
		@SuppressWarnings("unchecked")
		ObjectScope map1 = (ObjectScope) e1;
		@SuppressWarnings("unchecked")
		ObjectScope map2 = (ObjectScope) e2;
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
			int ret = compareStrings(key1, key2, ignoreCase, naturalOrder);
			if (ret != 0)
			    return ret;
		    }
		}

		// If the key sets are the same, then iterate through the values and compare them
		for (String key : keySet1) {
		    Object value1 = map1.getValue(key, false);
		    Object value2 = map2.getValue(key, false);

		    int ret = compareValues(visitor, ctx1, ctx2, value1, value2, mc, strict, allowNulls, ignoreCase, naturalOrder);
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

	    Object v1 = visitor.evaluateFunction(ctx1, e1);
	    Object v2 = visitor.evaluateFunction(ctx2, e2);

	    // Do string concatenation if either expr is a string
	    if (v1 instanceof String || v2 instanceof String) {
		String s1 = v1 == null ? "" : v1.toString();
		String s2 = v2 == null ? "" : v2.toString();
		return s1 + s2;
	    }

	    // TODO: what to do with char?
	    // could add char codepoint values, or concat strings

	    // Otherwise, numeric values get added numerically
	    if (rational) {
		BigFraction f1 = convertToFraction(v1, ctx1);
		BigFraction f2 = convertToFraction(v2, ctx2);

		return f1.add(f2);
	    }
	    else {
		BigDecimal d1 = convertToDecimal(v1, mc, ctx1);
		BigDecimal d2 = convertToDecimal(v2, mc, ctx2);

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


	/**
	 * Format a decimal/integer number with/without thousands separators.
	 *
	 * @param value A decimal value which could actually be an integer.
	 * @param sep   Whether or not to use separators.
	 * @return      The value formatted appropriately with thousands separators.
	 */
	public static String formatWithSeparators(final BigDecimal value, final boolean sep) {
	    if (sep) {
		int scale = value.scale();
		// There are no digits right of the decimal point, so treat as an integer
		if (scale <= 0) {
		    return String.format("%1$,d", value.toBigInteger());
		}
		else {
		    String formatString = String.format("%%1$,.%1$df", scale);
		    return String.format(formatString, value);
		}
	    }
	    else {
		return value.toPlainString();
	    }
	}

	private static boolean isLocalVarPart(final char ch) {
	    return (isIdentifierPart(ch) || ch == '#' || ch == '*');
	}

	/**
	 * Given an interpolated string constant, get the current interpolated value, by evaluating all
	 * embedded variable references and expressions and substituting their values in place.
	 *
	 * @param visitor	The visitor used to calculate expressions.
	 * @param iStringNode	The parse tree node containing the parsed constant.
	 * @param ctx		The parsing context which the node is part of (for error reporting).
	 * @return		The current value of this interpolated string.
	 */
	public static String getIStringValue(final CalcObjectVisitor visitor, final TerminalNode iStringNode, final ParserRuleContext ctx) {
	    String value = iStringNode.getText();
	    NestedScope variables = visitor.getVariables();
	    Settings settings = visitor.getSettings();

	    String rawValue = getRawString(value);
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
		    while (identPos < rawValue.length() && isLocalVarPart(rawValue.charAt(identPos)))
			identPos++;
		    if (identPos > pos + 2) {
			String varName = rawValue.substring(pos + 1, identPos);
			Object varValue = variables.getValue(varName, settings.ignoreNameCase);
			// But if $var is not defined, then forget it, and just output "$" and go on
			if (varValue != null) {
			    output.append(toStringValue(visitor, ctx, varValue, false, false, settings.separatorMode, ""));
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
		    String stringValue = toStringValue(visitor, ctx, exprValue, false, false, settings.separatorMode, "");
		    // The result is going to be formatted with quotes, separators, everything that it currently
		    // needs to be output, BUT it will go through the quoting again inside the formatter code
		    // so we need to strip quotes and escaped quotes or we will get double
		    output.append(CharUtil.stripDoubleQuotes(stringValue));
		    lastPos = nextPos;
		}
		else if (isIdentifierStart(rawValue.charAt(pos + 1))) {
		    int identPos = pos + 2;
		    while (identPos < rawValue.length() && isIdentifierPart(rawValue.charAt(identPos)))
			identPos++;
		    String varName = rawValue.substring(pos + 1, identPos);
		    output.append(toStringValue(visitor, ctx,
			variables.getValue(varName, settings.ignoreNameCase), false, false, settings.separatorMode, ""));
		    lastPos = identPos - 1;
		}
		else
		    throw new CalcExprException("%calc#invalidConstruct", ctx);
	    }
	    if (lastPos < rawValue.length())
		output.append(rawValue.substring(lastPos));

	    return output.toString();
	}

	/**
	 * Given the escaped form of a string (that is, what appears in the script as the user
	 * typed it), remove the outer quotes, convert any escape sequences, and return the
	 * raw string ready to be further processed.
	 *
	 * @param escapedForm	The input string value.
	 * @return		The raw string data, with all quotes removed and escape sequences converted.
	 */
	public static String getRawString(final String escapedForm) {
	    return CharUtil.convertEscapeSequences(CharUtil.stripAnyQuotes(escapedForm, true));
	}

	/**
	 * A string constant can be used as a member name, but it needs quotes around it.
	 * This method gets the raw string value (with Unicode escapes decoded, and etc.) and
	 * adds the required quotes (whichever quotes it started with, which could be "handed"
	 * quotes as in <code>&#x00AB;</code> and <code>&#x00BB;</code>).
	 *
	 * @param constantText	The text as it appears in the script.
	 * @return		The properly decoded and quoted member name from it.
	 */
	public static String getStringMemberName(final String constantText) {
	    char leftQuote = constantText.charAt(0);
	    char rightQuote = constantText.charAt(constantText.length() - 1);
	    return CharUtil.addQuotes(getRawString(constantText), leftQuote, rightQuote);
	}

}

