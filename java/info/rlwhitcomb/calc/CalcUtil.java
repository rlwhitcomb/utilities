/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2022 Roger L. Whitcomb.
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
 *	    parameters for consistency.
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
 *	12-Nov-2021 (rlwhitcomb)
 *	    #81: Add "quotes" param to most common form of "toStringValue".
 *	08-Dec-2021 (rlwhitcomb)
 *	    #131: Allow null/not-null check for arrays and objects in "toBooleanValue".
 *	18-Dec-2021 (rlwhitcomb)
 *	    #148: Deal more gracefully with char values in "toStringValue"
 *	23-Dec-2021 (rlwhitcomb)
 *	    #179: Fix interpolated expression evaluation with nested brackets.
 *	    Fix identifier identification to completely match the expanded definitions in the grammar.
 *	27-Dec-2021 (rlwhitcomb)
 *	    #170: Switch "length" and "scale".
 *	31-Dec-2021 (rlwhitcomb)
 *	    #180: Refactor parameters to "toStringValue"; allow variable indent increment.
 *	05-Jan-2022 (rlwhitcomb)
 *	    As part of #182: we need to evaluate Number subclasses since they are "in the wild" now too.
 *	18-Jan-2022 (rlwhitcomb)
 *	    #211: Implement "typeof" operator.
 *	19-Jan-2022 (rlwhitcomb)
 *	    #214: Implement "cast"; add an enum for the values.
 *	20-Jan-2022 (rlwhitcomb)
 *	    #215: Add "scale" parameter to "formatWithSeparators" to add leading zeros.
 *	24-Jan-2022 (rlwhitcomb)
 *	    #103: Start of complex number support.
 *	    Move "stringToValue" to here from the visitor. More work with ComplexNumber.
 *	    Compare ComplexNumber to each other.
 *	31-Jan-2022 (rlwhitcomb)
 *	    #212: type of function and cast to function.
 *	    #103: cast complex to/from map and list
 *	10-Feb-2022 (rlwhitcomb)
 *	    Rearrange tests inside "compareValues" so that fraction/complex compared to int/decimal
 *	    will work correctly.
 *	13-Feb-2022 (rlwhitcomb)
 *	    #199: Rearrange parameter value testing, and variable detection inside interpolated strings.
 *	14-Apr-2022 (rlwhitcomb)
 *	    #273: Move math-related classes to "math" package.
 *	29-Apr-2022 (rlwhitcomb)
 *	    #68: Add "indexOf" method for arrays (complicated because of numeric equivalence messes).
 *	04-May-2022 (rlwhitcomb)
 *	    #300: Extra parameter for fewer spaces in object/list string representations.
 *	06-May-2022 (rlwhitcomb)
 *	    #286: Truthy value for empty objects and arrays should be false.
 *	10-May-2022 (rlwhitcomb)
 *	    #315: Implement object concatenation in "addOp".
 *	11-May-2022 (rlwhitcomb)
 *	    #318: Rename "evaluateFunction" to just "evaluate".
 *	17-May-2022 (rlwhitcomb)
 *	    #315: Change "putAll" on ObjectScope to accept the whole object.
 *	18-May-2022 (rlwhitcomb)
 *	    #315: Oops! Concat list + obj or obj + list shouldn't do anything special.
 *	    #334: Move "findMatching" to CharUtil. New flavor of "getRawString" that skips
 *	    embedded expressions.
 *	20-May-2022 (rlwhitcomb)
 *	    #339: Add "fixupToInteger" (from "cleanDecimal" in CalcObjectVisitor).
 *	27-May-2022 (rlwhitcomb)
 *	    Move "isPredefined", "saveVariables", "copyAndTransform", and "buildValueList"  out of
 *	    CalcObjectVisitor into here.
 *	28-May-2022 (rlwhitcomb)
 *	    #344: Add "isValidIdentifier" method.
 *	30-May-2022 (rlwhitcomb)
 *	    #301: Fix "toIntegerValue" for BigDecimal input.
 *	04-Jun-2022 (rlwhitcomb)
 *	    #351: Change spacing of "{ }" in statement blocks.
 *	21-Jun-2022 (rlwhitcomb)
 *	    #314: Add processing of SetScope in all applicable places.
 *	29-Jun-2022 (rlwhitcomb)
 *	    #381: Add "sortMap".
 *	08-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 *	10-Jul-2022 (rlwhitcomb)
 *	    #392: Code to sort keys in objects.
 *	11-Jul-2022 (rlwhitcomb)
 *	    #403: Add raw string support.
 *	13-Jul-2022 (rlwhitcomb)
 *	    #403: Fix second flavor of "getRawString".
 *	19-Jul-2022 (rlwhitcomb)
 *	    #412: Refactor parameters of "toStringValue".
 *	24-Jul-2022 (rlwhitcomb)
 *	    #412: Add "skipLevels" functionality to "toStringValue".
 *	29-Jul-2022 (rlwhitcomb)
 *	    #402: New "checkRequiredVersions" method (from CalcObjectVisitor for use
 *	    on command line also).
 *	29-Aug-2022 (rlwhitcomb)
 *	    #469: New method to search ObjectScope recursively for "has"-ness.
 *	12-Oct-2022 (rlwhitcomb)
 *	    #103: Add "equality" parameter to "compareValues" for the strange case of
 *	    ComplexNumber comparisons.
 *	06-Nov-2022 (rlwhitcomb)
 *	    #476: Make the NaturalOrderComparator instances public for use elsewhere.
 *	29-Nov-2022 (rlwhitcomb)
 *	    #567: Sort in descending order.
 *	05-Dec-2022 (rlwhitcomb)
 *	    #573: New "scanIntoVars" method.
 *	06-Dec-2022 (rlwhitcomb)
 *	    #573: Fix some "scan" delimiter issues.
 *	06-Dec-2022 (rlwhitcomb)
 *	    #573: Quote literal patterns in "scan".
 */
package info.rlwhitcomb.calc;

import de.onyxbits.SemanticVersion;
import info.rlwhitcomb.math.BigFraction;
import info.rlwhitcomb.math.ComplexNumber;
import info.rlwhitcomb.util.CharUtil;
import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.Exceptions;
import info.rlwhitcomb.util.Intl;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.padler.natorder.NaturalOrderComparator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;

import static info.rlwhitcomb.util.CharUtil.Justification;
import static info.rlwhitcomb.util.Constants.*;


/**
 * Static utility methods for the {@link CalcObjectVisitor} class --
 * mostly object conversion and common code.
 */
public final class CalcUtil
{
	/** Natural order comparator, case-sensitive. */
	public static final NaturalOrderComparator NATURAL_SENSITIVE_COMPARATOR = new NaturalOrderComparator(true);

	/** Natural order comparator, case-insensitive. */
	public static final NaturalOrderComparator NATURAL_INSENSITIVE_COMPARATOR = new NaturalOrderComparator(false);

	/** Default indent increment for JSON display. */
	private static final String DEFAULT_INCREMENT = "  ";

	/** Version identifier for library (saved) files. */
	private static final String LIB_FORMAT = ":Requires '%1$s', Base '%2$s'";


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
		/** {@code false} means {@code "("}, while {@code true} means {@code " ("} */
		boolean spaceOpenParen = true;
		/** {@code false} means <code>"{...}"</code>, while {@code true} means <code>"{ ... }"</code> */
		boolean spaceBraces = false;


		TreeTextOptions() {
		}

		TreeTextOptions(final TreeTextOptions other) {
		    spaceColon       = other.spaceColon;
		    spaceMinus       = other.spaceMinus;
		    spaceOpenBracket = other.spaceOpenBracket;
		    spaceOpenParen   = other.spaceOpenParen;
		    spaceBraces      = other.spaceBraces;
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

	private static char prevChar(final StringBuilder buf, final int offset) {
	    int len = buf.length();
	    return len >= offset ? buf.charAt(len - offset) : '\0';
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
	    else if (ctx instanceof CalcParser.StmtBlockContext) {
		localOptions = new TreeTextOptions(options);
		localOptions.spaceBraces = true;
	    }

	    for (int i = 0; i < ctx.getChildCount(); i++) {
		ParseTree child = ctx.getChild(i);
		if (child.getChildCount() > 0) {
		    getTreeText(buf, child, localOptions);
		}
		else {
		    boolean replace = false;
		    boolean space = true;
		    char firstChar = '\0';
		    char prevChar = prevChar(buf, 2);
		    String childText = child.getText();

		    switch (childText) {
			case "(":
			     space = false;
			     if (!localOptions.spaceOpenParen)
				replace = true;
			     break;
			case "{":
			     space = localOptions.spaceBraces;
			     break;
			case "[":
			     space = false;
			     if (!localOptions.spaceOpenBracket)
				replace = true;
			     break;
			case ",":
			case ")":
			case "]":
			    replace = true;
			    break;
			case "}":
			    replace = !localOptions.spaceBraces || (prevChar == '\r' || prevChar == '\n');
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
			    if (childText.length() > 0)
				firstChar = childText.charAt(0);
			    break;
		    }

		    if (!replace && (firstChar == '\n' || firstChar == '\r'))
			replace = true;

		    if (replace) {
			int len = buf.length();
			if (len > 0 && buf.charAt(len - 1) == ' ')
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
	 * <p> Corresponds to the {@code NAME_START_CHAR} rule in the Calc.g4 grammar.
	 *
	 * @param str	The string we're navigating through
	 * @param pos	Position of the character to examine
	 * @return	Whether or not this is a valid identifier start character.
	 */
	public static boolean isIdentifierStart(final String str, final int pos) {
	    char ch = str.charAt(pos);

	    if ((ch >= 'a' && ch <= 'z')
	     || (ch >= 'A' && ch <= 'Z')
	     || (ch >= '\u00C0' && ch <= '\u00D6')
	     || (ch >= '\u00D8' && ch <= '\u00F6')
	     || (ch >= '\u00F8' && ch <= '\u02FF')
	     || (ch >= '\u0370' && ch <= '\u037D')
	     || (ch >= '\u037F' && ch <= '\u1FFF')
	     || (ch >= '\u200C' && ch <= '\u200D')
	     || (ch >= '\u2071' && ch <= '\u2073')
	     || (ch >= '\u207A' && ch <= '\u207F')
	     || (ch >= '\u208A' && ch <= '\u218F')
	     || (ch >= '\u2C00' && ch <= '\u2FEF')
	     || (ch >= '\u3001' && ch <= '\uD7FF')
	     || (ch >= '\uF900' && ch <= '\uFDCF')
	     || (ch >= '\uFDF0' && ch <= '\uFF0F')
	     || (ch >= '\uFF1A' && ch <= '\uFFFD')
	     || (ch == '_'))
		return true;

	    // This is the low surrogate of all the PI_VALUES below
	    if (pos >= str.length() || ch != '\uD835')
		return false;

	    // These are the high surrogates of the PI_VALUES from Calc.g4
	    char ch2 = str.charAt(pos + 1);
	    if (ch2 == '\uDEB7'  // 1D6B7
	     || ch2 == '\uDED1'  // 1D6D1
	     || ch2 == '\uDEE1'  // 1D6E1
	     || ch2 == '\uDEF1'  // 1D6F1
	     || ch2 == '\uDF0B'  // 1D70B
	     || ch2 == '\uDF1B'  // 1D71B
	     || ch2 == '\uDF2B'  // 1D72B
	     || ch2 == '\uDF45'  // 1D745
	     || ch2 == '\uDF55'  // 1D755
	     || ch2 == '\uDF65'  // 1D765
	     || ch2 == '\uDF7F'  // 1D77F
	     || ch2 == '\uDF8F'  // 1D78F
	     || ch2 == '\uDF9F'  // 1D79F
	     || ch2 == '\uDFB9'  // 1D7B9
	     || ch2 == '\uDFC9') // 1D7C9
		return true;

	    return false;
	}

	/**
	 * Is this character a valid identifier character (after the start)?
	 * <p> Corresponds to the grammar for {@code NAME_CHAR}.
	 *
	 * @param str	The string we're navigating through
	 * @param start The starting position of the identifier
	 * @param pos	Position of the character to examine
	 * @return	Whether the character is a valid following part of an identifier.
	 */
	public static boolean isIdentifierPart(final String str, final int start, final int pos) {
	    if (isIdentifierStart(str, pos))
		return true;

	    char ch = str.charAt(pos);

	    if ((ch >= '0' && ch <= '9')
	     || (ch == '\u00B7')
	     || (ch >= '\u0300' && ch <= '\u036F')
	     || (ch >= '\u203F' && ch <= '\u2040'))
		return true;

	    // Special case for the local (parameter) count array/count variables (not allowed as regular ids)
	    if (str.charAt(start) == '_' && (pos == start + 1) && (ch == '#' || ch == '*' || ch == '_'))
		return true;

	    return false;
	}

	/**
	 * Is this string a valid identifier?
	 *
	 * @param str The string to check.
	 * @return    Whether or not the input string is a valid identifier.
	 */
	public static boolean isValidIdentifier(final String str) {
	    if (isIdentifierStart(str, 0)) {
		for (int i = 1; i < str.length(); i++) {
		    if (!isIdentifierPart(str, 0, i))
			return false;
		}
		return true;
	    }
	    return false;
	}

	/**
	 * Is the value a predefined object, which may or may not include any parameters of this scope?
	 *
	 * @param value Value of the object, which would need to be some form of {@link PredefinedValue}
	 *              to return true.
	 * @param includesParams If {@code true} then {@link ParameterValue}s are considered "predefined",
	 *              but if {@code false} they are not.
	 * @return      Whether or not to consider this value as predefined.
	 */
	public static boolean isPredefined(final Object value, final boolean includesParams) {
	    boolean isPredefinedType = (value instanceof Scope) && ((Scope) value).isPredefined();

	    return isPredefinedType || (includesParams && value instanceof ParameterValue);
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

	private static String typeName(final Object value) {
	    String name = value.getClass().getSimpleName();

	    if (value instanceof ObjectScope)
		name = "object";
	    else if (value instanceof ArrayScope)
		name = "array";
	    else if (value instanceof SetScope)
		name = "set";
	    else if (value instanceof CollectionScope)
		name = "collection";

	    return name;
	}

	private static boolean isFloat(final Object value) {
	    return (value instanceof Double || value instanceof Float);
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
	 * Fixup a {@link BigDecimal} value and convert to {@link BigInteger} if possible.
	 *
	 * @param bd	The candidate value.
	 * @return	The numerically equivalent value, integer if possible.
	 */
	public static Number fixupToInteger(final BigDecimal bd) {
	    BigDecimal dValue = bd.stripTrailingZeros();
	    if (dValue.scale() <= 0)
		return dValue.toBigIntegerExact();
	    return dValue;
	}

	/**
	 * Convert a string argument (sourced from the command line) to a possibly better value
	 * (as in, more accurate of its true value).
	 * <p> First try is {@link BigDecimal} which can be converted to {@link BigInteger}
	 * if there is no fractional part. If that fails, try to get a {@link Boolean} value
	 * from the string, and failing that just leave the result as an unquoted string.
	 *
	 * @param arg	The command line argument as typed by the user.
	 * @return	The best possible representation of that argument.
	 * @see CalcObjectVisitor#setArgument
	 * @see CalcObjectVisitor#setVariable
	 */
	public static Object stringToValue(final String arg) {
	    try {
		return fixupToInteger(new BigDecimal(arg));
	    }
	    catch (NumberFormatException nfe) {
		try {
		    return Boolean.valueOf(CharUtil.getBooleanValue(arg));
		}
		catch (IllegalArgumentException iae) {
		    return CharUtil.stripAnyQuotes(arg, true);
		}
	    }
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
	    Object value = visitor.evaluate(ctx, obj);
	    return convertToDecimal(value, mc, ctx);
	}

	/**
	 * Convert the given object into a {@link BigDecimal} value.
	 *
	 * @param value		The value to convert.
	 * @param mc		Rounding context.
	 * @param ctx		Parse tree of the current expression (used for error reporting).
	 * @return		The decimal equivalent of the input (if possible).
	 * @throws CalcExprException for null input, or other conversion errors.
	 */
	public static BigDecimal convertToDecimal(final Object value, final MathContext mc, final ParserRuleContext ctx) {
	    nullCheck(value, ctx);

	    if (value instanceof BigDecimal)
		return fixup((BigDecimal) value);
	    else if (value instanceof BigInteger)
		return fixup(new BigDecimal((BigInteger) value));
	    else if (value instanceof BigFraction)
		return fixup(((BigFraction) value).toDecimal(mc));
	    else if (value instanceof ComplexNumber)
		return fixup(((ComplexNumber) value).r());
	    else if (value instanceof String)
		return fixup(new BigDecimal((String) value));
	    else if (value instanceof Boolean)
		return ((Boolean) value).booleanValue() ? BigDecimal.ONE : BigDecimal.ZERO;
	    else if (isFloat(value))
		return fixup(new BigDecimal(((Number) value).doubleValue()));
	    else if (value instanceof Number)
		return fixup(BigDecimal.valueOf(((Number) value).longValue()));

	    // Here we are not able to make sense of the object, so we have an error
	    throw new CalcExprException(ctx, "%calc#noConvertDecimal", typeName(value));
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
	    Object value = visitor.evaluate(ctx, obj);
	    return convertToFraction(value, ctx);
	}

	/**
	 * Convert the given object into a {@link BigFraction} value.
	 *
	 * @param value		The value to convert.
	 * @param ctx		Parse tree of the current expression (used for error reporting).
	 * @return		The fraction equivalent of the input (if possible).
	 * @throws CalcExprException for null input, or other conversion errors.
	 */
	public static BigFraction convertToFraction(final Object value, final ParserRuleContext ctx) {
	    nullCheck(value, ctx);

	    if (value instanceof BigFraction)
		return (BigFraction) value;
	    else if (value instanceof BigDecimal)
		return new BigFraction((BigDecimal) value);
	    else if (value instanceof BigInteger)
		return new BigFraction((BigInteger) value);
	    else if (value instanceof ComplexNumber)
		return new BigFraction(((ComplexNumber) value).r());
	    else if (value instanceof String)
		return BigFraction.valueOf((String) value);
	    else if (value instanceof Boolean)
		return ((Boolean) value).booleanValue() ? BigFraction.ONE : BigFraction.ZERO;
	    else if (isFloat(value))
		return new BigFraction(new BigDecimal(((Number) value).doubleValue()));
	    else if (value instanceof Number)
		return new BigFraction(((Number) value).longValue());

	    // Here we are not able to make sense of the object, so we have an error
	    throw new CalcExprException(ctx, "%calc#noConvertFraction", typeName(value));
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
		if (value instanceof BigDecimal) {
		    return ((BigDecimal) value).toBigIntegerExact();
		}
		else if (value instanceof BigInteger) {
		    return (BigInteger) value;
		}
		else if (value instanceof BigFraction) {
		    return ((BigFraction) value).toIntegerExact();
		}
		else if (value instanceof ComplexNumber) {
		    return (((ComplexNumber) value).r().toBigIntegerExact());
		}
		else if (value instanceof Number) {
		    return BigInteger.valueOf(((Number) value).longValue());
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
		else if (value instanceof ComplexNumber) {
		    return (((ComplexNumber) value).r().intValueExact());
		}
		else if (value instanceof Number) {
		    return ((Number) value).intValue();
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
	 * null objects or empty strings are {@code false}, objects which are non-empty are {@code true}, and
	 * non-empty strings and numbers are sent to {@link CharUtil#getBooleanValue} for evaluation).
	 *
	 * @param visitor	The visitor for evaluating expressions.
	 * @param obj		The input object to convert.
	 * @param ctx		The parse tree context for error reporting.
	 * @return		The converted boolean value from the input.
	 * @throws CalcExprException if there was a problem (for instance, in evaluating an expression).
	 */
	public static Boolean toBooleanValue(final CalcObjectVisitor visitor, final Object obj, final ParserRuleContext ctx) {
	    Object value = visitor.evaluate(ctx, obj);

	    // Compatibility with JavaScript here...
	    if (CharUtil.isNullOrEmpty(value))
		return Boolean.FALSE;

	    if (value instanceof ArrayScope) {
		return Boolean.valueOf(((ArrayScope) value).size() != 0);
	    }
	    else if (value instanceof ObjectScope) {
		return Boolean.valueOf(((ObjectScope) value).size() != 0);
	    }
	    else if (value instanceof SetScope) {
		return Boolean.valueOf(((SetScope) value).size() != 0);
	    }
	    else if (value instanceof CollectionScope) {
		// This is always the empty collection
		return Boolean.FALSE;
	    }
	    else if (value instanceof ValueScope) {
		value = ((ValueScope) value).getValue();
	    }

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
	 * @param format	Formatting parameters.
	 * @return		The converted string value.
	 * @see #toStringValue(CalcObjectVisitor, ParserRuleContext, Object, StringFormat, String, int)
	 */
	public static String toStringValue(
		final CalcObjectVisitor visitor,
		final ParserRuleContext ctx,
		final Object result,
		final StringFormat format) {
	    return toStringValue(visitor, ctx, result, format, "", 0);
	}

	/**
	 * The workhorse, recursive method used to convert values to strings.
	 *
	 * @param visitor	The outermost visitor object that is being used to calculate everything.
	 * @param ctx		The parsing context, for error reporting.
	 * @param obj		The input object to be converted to a string.
	 * @param format	Format settings for the process.
	 * @param indent	Current indentation.
	 * @param level		Nesting / recursion level (0 = top).
	 * @return		The formatted string representation of the input object.
	 */
	@SuppressWarnings("unchecked")
	public static String toStringValue(
		final CalcObjectVisitor visitor,
		final ParserRuleContext ctx,
		final Object obj,
		final StringFormat format,
		final String indent,
		final int level)
	{
	    Object result = visitor.evaluate(ctx, obj);

	    if (result instanceof CollectionScope) {
		if (result instanceof ObjectScope) {
		    return toStringValue(visitor, ctx, ((ObjectScope) result).map(), format, indent, level);
		}
		else if (result instanceof ArrayScope) {
		    return toStringValue(visitor, ctx, ((ArrayScope) result).list(), format, indent, level);
		}
		else if (result instanceof SetScope) {
		    return toStringValue(visitor, ctx, ((SetScope) result).set(), format, indent, level);
		}
		else if (result instanceof CollectionScope) {
		    return format.extraSpace ? "{ }" : "{}";
		}
	    }
	    else {
		if (level >= format.skipLevels) {
		    if (result == null) {
			return format.quotes ? "<null>" : "";
		    }
		    else if (result instanceof Character) {
			String charString = Character.toString((Character) result);
			if (format.quotes)
			    return CharUtil.addDoubleQuotes(CharUtil.quoteControl(charString));
			else
			    return charString;
		    }
		    else if (result instanceof String) {
			if (format.quotes)
			    return CharUtil.addDoubleQuotes(CharUtil.quoteControl((String) result));
			else
			    return (String) result;
		    }
		    else if (result instanceof BigDecimal) {
			return formatWithSeparators(((BigDecimal) result), format.separators, Integer.MIN_VALUE);
		    }
		    else if (result instanceof BigInteger) {
			if (format.separators)
			    return String.format("%1$,d", (BigInteger) result);
			else
			    return result.toString();
		    }

		    // Any other type, just get the string representation
		    return result.toString();
		}
	    }

	    return "";
	}

	/**
	 * The recursive method used to convert an object (map) to a string.
	 *
	 * @param visitor	The outermost visitor object that is being used to calculate everything.
	 * @param ctx		The parsing context, for error reporting.
	 * @param map		The input object (map) to be converted.
	 * @param format	Settings used to format the string.
	 * @param indent	Current indentation string.
	 * @param level		The level of indent (recursion) where we are.
	 * @return		The formatted string representation of the input object.
	 */
	public static String toStringValue(
		final CalcObjectVisitor visitor,
		final ParserRuleContext ctx,
		final Map<String, Object> map,
		final StringFormat format,
		final String indent,
		final int level)
	{
	    StringBuilder buf = new StringBuilder();
	    String myIndent = indent + format.increment;

	    if (map.size() > 0) {
		boolean comma = false;
		if (level >= format.skipLevels) {
		    buf.append(format.pretty ? "{\n" : format.extraSpace ? "{ " : "{");
		    for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (comma)
			    buf.append(format.pretty ? ",\n" : format.extraSpace ? ", " : ",");
			else
			    comma = true;
			if (format.pretty) buf.append(myIndent);
			buf.append(entry.getKey()).append(format.extraSpace ? ": " : ":");
			buf.append(toStringValue(visitor, ctx, entry.getValue(), format, myIndent, level + 1));
		    }
		    buf.append(format.pretty ? "\n" + indent + "}" : format.extraSpace ? " }" : "}");
		}
		else {
		    for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (comma)
			    buf.append(format.pretty ? ",\n" : format.extraSpace ? ", " : ",");
			if (level + 1 >= format.skipLevels) {
			    if (format.pretty) buf.append(indent);
			    buf.append(entry.getKey()).append(format.extraSpace ? ": " : ":");
			}
			String children = toStringValue(visitor, ctx, entry.getValue(), format,
				(level >= format.skipLevels ? myIndent : indent), level + 1);
			comma = !children.isEmpty();
			buf.append(children);
		    }
		}
	    }
	    else if (level >= format.skipLevels) {
		buf.append(format.extraSpace ? "{ }" : "{}");
	    }

	    return buf.toString();
	}

	/**
	 * The recursive method used to convert a list (array) to a string.
	 *
	 * @param visitor	The outermost visitor object that is being used to calculate everything.
	 * @param ctx		The parsing context, for error reporting.
	 * @param list		The input list (array) to be converted.
	 * @param format	Formatting parameters for the process.
	 * @param indent	The recursive indentation for pretty printing.
	 * @param level		Recursive level (0 = outermost).
	 * @return		The formatted string representation of the input list.
	 */
	public static String toStringValue(
		final CalcObjectVisitor visitor,
		final ParserRuleContext ctx,
		final List<Object> list,
		final StringFormat format,
		final String indent,
		final int level)
	{
	    StringBuilder buf = new StringBuilder();
	    String myIndent = indent + format.increment;

	    if (list.size() > 0) {
		boolean comma = false;
		if (level >= format.skipLevels) {
		    buf.append(format.pretty ? "[\n" : format.extraSpace ? "[ " : "[");
		    for (Object value : list) {
			if (comma)
			    buf.append(format.pretty ? ",\n" : format.extraSpace ? ", " : ",");
			else
			    comma = true;
			if (format.pretty) buf.append(myIndent);
			buf.append(toStringValue(visitor, ctx, value, format, myIndent, level + 1));
		    }
		    buf.append(format.pretty ? "\n" + indent + "]" : format.extraSpace ? " ]" : "]");
		}
		else {
		    for (Object value : list) {
			if (comma)
			    buf.append(format.pretty ? ",\n" : format.extraSpace ? ", " : ",");
			if (level + 1 >= format.skipLevels) {
			    if (format.pretty) buf.append(indent);
			}
			String children = toStringValue(visitor, ctx, value, format,
				(level >= format.skipLevels ? myIndent : indent), level + 1);
			comma = !children.isEmpty();
			buf.append(children);
		    }
		}
	    }
	    else if (level >= format.skipLevels) {
		buf.append(format.extraSpace ? "[ ]" : "[]");
	    }

	    return buf.toString();
	}

	/**
	 * The recursive method used to convert a set to a string.
	 *
	 * @param visitor	The outermost visitor object that is being used to calculate everything.
	 * @param ctx		The parsing context, for error reporting.
	 * @param set		The input set to be converted.
	 * @param format	String formatting parameters.
	 * @param indent	The recursive indentation for pretty printing.
	 * @param level		Recursive level inside a nested object.
	 * @return		The formatted string representation of the input list.
	 */
	public static String toStringValue(
		final CalcObjectVisitor visitor,
		final ParserRuleContext ctx,
		final Set<Object> set,
		final StringFormat format,
		final String indent,
		final int level)
	{
	    StringBuilder buf = new StringBuilder();
	    String myIndent = indent + format.increment;

	    if (set.size() > 0) {
		boolean comma = false;
		if (level >= format.skipLevels) {
		    buf.append(format.pretty ? "{\n" : format.extraSpace ? "{ " : "{");
		    for (Object value : set) {
			if (comma)
			    buf.append(format.pretty ? ",\n" : format.extraSpace ? ", " : ",");
			else
			    comma = true;
			if (format.pretty) buf.append(myIndent);
			buf.append(toStringValue(visitor, ctx, value, format, myIndent, level + 1));
		    }
		    buf.append(format.pretty ? "\n" + indent + "}" : format.extraSpace ? " }" : "}");
		}
		else {
		    for (Object value : set) {
			if (comma)
			    buf.append(format.pretty ? ",\n" : format.extraSpace ? ", " : ",");
			if (level + 1 >= format.skipLevels) {
			    if (format.pretty) buf.append(indent);
			}
			String children = toStringValue(visitor, ctx, value, format,
				(level >= format.skipLevels ? myIndent : indent), level + 1);
			comma = !children.isEmpty();
			buf.append(children);
		    }
		}
	    }
	    else if (level >= format.skipLevels) {
		buf.append(format.extraSpace ? "{ }" : "{}");
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
	    Object obj = visitor.evaluate(ctx, valueObj);

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
	    if (obj instanceof ComplexNumber)
		return ((ComplexNumber) obj).r().precision();		// ?? again, not helpful, probably
	    if (obj instanceof String) {
		String str = (String) obj;
		return str.codePointCount(0, str.length());
	    }
	    if (obj instanceof Number) {
		return ((Number) obj).toString().length();
	    }
	    if (obj instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<Object> array = (ArrayScope<Object>) obj;
		if (recursive) {
		    int len = 0;
		    for (Object listObj : array.list()) {
			if (listObj instanceof Scope)
			    len += length(visitor, listObj, ctx, recursive);
			else
			    len++;	// Note: this will count null entries as one
		    }
		    return len;
		}
		return array.size();
	    }
	    if (obj instanceof ObjectScope) {
		@SuppressWarnings("unchecked")
		ObjectScope map = (ObjectScope) obj;
		if (recursive) {
		    int len = 0;
		    for (Object mapObj : map.values()) {
			if (mapObj instanceof Scope)
			    len += length(visitor, mapObj, ctx, recursive);
			else
			    len++;	// Note: this will count null values as one
		    }
		    return len;
		}
		return map.size();
	    }
	    if (obj instanceof SetScope) {
		@SuppressWarnings("unchecked")
		SetScope<Object> set = (SetScope<Object>) obj;
		if (recursive) {
		    int len = 0;
		    for (Object setObj : set.set()) {
			if (setObj instanceof Scope)
			    len += length(visitor, setObj, ctx, recursive);
			else
			    len++;	// Note: this will count null values as one
		    }
		    return len;
		}
		return set.size();
	    }
	    if (obj instanceof CollectionScope) {
		return 0;
	    }

	    throw new CalcExprException(ctx, "%calc#unknownType", obj.getClass().getSimpleName());
	}

	/**
	 * Compute the "scale" of the given object:
	 * <ul><li>{@code BigDecimal} = the {@code scale()} value.</li>
	 * <li>{@code Object} = the recursive (total) size</li>
	 * <li>{@code Array} = the recursive (total) size</li>
	 * <li>everything else = {@code 0}</li>
	 * </ul>
	 *
	 * @param visitor	The visitor (for function evaluation).
	 * @param valueObj	The object to interrogate.
	 * @param ctx		The context to use for error reporting.
	 * @return		The scale of the object.
	 */
	public static int scale(final CalcObjectVisitor visitor, final Object valueObj, final ParserRuleContext ctx) {
	    Object obj = visitor.evaluate(ctx, valueObj);

	    if (obj instanceof BigDecimal)
		return ((BigDecimal) obj).scale();
	    if (obj instanceof BigFraction)
		return ((BigFraction) obj).toDecimal().scale();
	    if (obj instanceof ComplexNumber)
		return ((ComplexNumber) obj).r().scale();	// ??
	    if (obj instanceof Scope)
		return length(visitor, obj, ctx, true);
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
	 * A comparator that uses {@code compareValues} to do the comparison.
	 */
	private static class ObjectComparator implements Comparator<Object>
	{
		private CalcObjectVisitor visitor;
		private ParserRuleContext ctx;
		private MathContext mc;
		private boolean ignoreCase;
		private boolean descending;

		ObjectComparator(
			final CalcObjectVisitor v,
			final ParserRuleContext c,
			final MathContext m,
			final boolean ignore,
			final boolean descend)
		{
		    visitor = v;
		    ctx = c;
		    mc = m;
		    ignoreCase = ignore;
		    descending = descend;
		}

		@Override
		public int compare(final Object o1, final Object o2) {
		    int ret = compareValues(visitor, ctx, ctx, o1, o2, mc, false, true, ignoreCase, true, false);
		    return descending ? -ret : ret;
		}
	}


	/**
	 * Sort a list according to our {@code compareValues} method.
	 *
	 * @param visitor	The visitor used to evaluate expressions.
	 * @param list		The list that will be sorted (in place).
	 * @param ctx		The parse tree (source) of the list.
	 * @param mc		Math context used to round decimal values.
	 * @param ignore	Whether the string comparison is case-sensitive or not.
	 * @param descending	Do the sort in descending order.
	 */
	public static void sort(final CalcObjectVisitor visitor, final List<Object> list,
		final ParserRuleContext ctx, final MathContext mc, final boolean ignore,
		final boolean descending) {
	    Collections.sort(list, new ObjectComparator(visitor, ctx, mc, ignore, descending));
	}


	/**
	 * A comparator of map entries that can either sort by key or value, using the {@code compareValues}
	 * method in either case to do the comparison.
	 */
	private static class MapEntryComparator implements Comparator<Map.Entry<String, Object>>
	{
		private CalcObjectVisitor visitor;
		private ParserRuleContext ctx;
		private MathContext mc;
		private boolean ignoreCase;
		private boolean sortByKey;
		private boolean sortDescending;

		MapEntryComparator(final CalcObjectVisitor v, final ParserRuleContext c,
			final MathContext m, final boolean ign, final boolean sortKey, final boolean descend) {
		    visitor = v;
		    ctx = c;
		    mc = m;
		    ignoreCase = ign;
		    sortByKey = sortKey;
		    sortDescending = descend;
		}

		@Override
		public int compare(final Map.Entry<String, Object> e1, final Map.Entry<String, Object> e2) {
		    int ret = 0;
		    if (sortByKey) {
			ret = compareValues(visitor, ctx, ctx, e1.getKey(), e2.getKey(), mc, false, true, ignoreCase, true, false);
		    }
		    else {
			ret = compareValues(visitor, ctx, ctx, e1.getValue(), e2.getValue(), mc, false, true, ignoreCase, true, false);
		    }
		    return sortDescending ? -ret : ret;
		}
	}


	/**
	 * Sort a map, either by key or value, according to our {@code compareValues} method.
	 *
	 * @param visitor	The visitor used to evaluate expressions.
	 * @param map		The input map to be sorted.
	 * @param ctx		The parse tree (source) of the map.
	 * @param mc		Math context used to round decimal values.
	 * @param ignore	Whether the string comparison is case-sensitive or not.
	 * @param sortByKey	{@code true} to sort by keys, or {@code false} by value.
	 * @param descending	Whether to reverse the sort order.
	 * @return		The (new) sorted map.
	 */
	public static ObjectScope sortMap(final CalcObjectVisitor visitor, final ObjectScope map,
		final ParserRuleContext ctx, final MathContext mc, final boolean ignore,
		final boolean sortByKey, final boolean descending) {
	    Comparator<Map.Entry<String, Object>> comparator =
		new MapEntryComparator(visitor, ctx, mc, ignore, sortByKey, descending);

	    List<Map.Entry<String, Object>> sortedList = new ArrayList<>(map.map().entrySet());
	    Collections.sort(sortedList, comparator);

	    ObjectScope sortedMap = new ObjectScope();
	    for (Map.Entry<String, Object> entry : sortedList) {
		sortedMap.setValue(entry.getKey(), entry.getValue());
	    }

	    return sortedMap;
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
		final MathContext mc, final boolean strict, final boolean allowNulls,
		final boolean equality) {
	    return compareValues(visitor, ctx1, ctx2, visitor.visit(ctx1), visitor.visit(ctx2),
		mc, strict, allowNulls, false, false, equality);
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
	 * @param equality     The operator is something like "==" or "!=" (an "equality" operator).
	 * @return {@code -1} if the first object is "less than" the second,
	 *         {@code 0} if the objects are "equal",
	 *         {@code +1} if the first object is "greater than" the second.
	 */
	public static int compareValues(final CalcObjectVisitor visitor,
		final ParserRuleContext ctx1, final ParserRuleContext ctx2,
		final Object obj1, final Object obj2,
		final MathContext mc, final boolean strict, final boolean allowNulls,
		final boolean ignoreCase, final boolean naturalOrder, final boolean equality) {
	    Object e1 = visitor.evaluate(ctx1, obj1);
	    Object e2 = visitor.evaluate(ctx2, obj2);

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
	    else if (e1 instanceof BigFraction || e2 instanceof BigFraction) {
		BigFraction f1 = toFractionValue(visitor, e1, ctx1);
		BigFraction f2 = toFractionValue(visitor, e2, ctx2);

		return f1.compareTo(f2);
	    }
	    else if (e1 instanceof ComplexNumber || e2 instanceof ComplexNumber) {
		ComplexNumber c1 = ComplexNumber.valueOf(e1);
		ComplexNumber c2 = ComplexNumber.valueOf(e2);

		if (equality) {
		    return c1.equals(c2) ? 0 : -1;
		}

		return c1.compareTo(c2);
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
	    else if (e1 instanceof Boolean || e2 instanceof Boolean) {
		Boolean b1 = toBooleanValue(visitor, e1, ctx1);
		Boolean b2 = toBooleanValue(visitor, e2, ctx2);

		return b1.compareTo(b2);
	    }
	    else if (isFloat(e1) || isFloat(e2)) {
		double d1 = ((Number) e1).doubleValue();
		double d2 = ((Number) e2).doubleValue();

		return Double.compare(d1, d2);
	    }
	    else if (e1 instanceof Number || e2 instanceof Number) {
		long l1 = ((Number) e1).longValue();
		long l2 = ((Number) e2).longValue();

		return Long.compare(l1, l2);
	    }
	    else if (e1 instanceof ArrayScope && e2 instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<Object> list1 = (ArrayScope<Object>) e1;
		@SuppressWarnings("unchecked")
		ArrayScope<Object> list2 = (ArrayScope<Object>) e2;
		int size1 = list1.size();
		int size2 = list2.size();

		if (size1 != size2)
		    return Integer.signum(size1 - size2);

		for (int i = 0; i < size1; i++) {
		    Object o1 = list1.getValue(i);
		    Object o2 = list2.getValue(i);

		    int ret = compareValues(visitor, ctx1, ctx2, o1, o2, mc, strict, allowNulls, ignoreCase, naturalOrder, equality);
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

		    int ret = compareValues(visitor, ctx1, ctx2, value1, value2, mc, strict, allowNulls, ignoreCase, naturalOrder, equality);
		    if (ret != 0)
			return ret;
		}

		// Finally, if no differences were found, the maps are the same
		return 0;
	    }
	    else if (e1 instanceof SetScope && e2 instanceof SetScope) {
		@SuppressWarnings("unchecked")
		SetScope<Object> set1 = (SetScope<Object>) e1;
		@SuppressWarnings("unchecked")
		SetScope<Object> set2 = (SetScope<Object>) e2;
		int size1 = set1.size();
		int size2 = set2.size();

		if (size1 != size2)
		    return Integer.signum(size1 - size2);

		// Sort the two sets and compare the values in sorted order
		List<Object> list1 = new ArrayList<>(set1.set());
		List<Object> list2 = new ArrayList<>(set2.set());

		sort(visitor, list1, ctx1, mc, ignoreCase, false);
		sort(visitor, list2, ctx2, mc, ignoreCase, false);

		// Iterate through the set and compare the values in order
		for (int i = 0; i < list1.size(); i++) {
		    int ret = compareValues(visitor, ctx1, ctx2, list1.get(i), list2.get(i), mc, strict, allowNulls, ignoreCase, naturalOrder, equality);
		    if (ret != 0)
			return ret;
		}

		return 0;
	    }
	    else if (e1 instanceof CollectionScope && e2 instanceof CollectionScope) {
		return 0;
	    }

	    throw new CalcExprException(ctx1, "%calc#unknownType", e1.getClass().getSimpleName());
	}

	/**
	 * Adjunct to {@link #addOp} to do object "addition" or concatenation.
	 *
	 * @param obj1     The first object, to which the second object will be added.
	 * @param obj2     The object to add to the first.
	 * @param sortKeys How the result object (map) is to be constructed.
	 * @return         The concatenated object.
	 */
	private static Object concatObjects(final Object obj1, final Object obj2, final boolean sortKeys) {
	    Object ret = null;

	    if (obj1 instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<Object> arr1 = (ArrayScope<Object>) obj1;
		ArrayScope<Object> result = new ArrayScope<>(arr1);

		if (obj2 instanceof ArrayScope) {
		    @SuppressWarnings("unchecked")
		    ArrayScope<Object> arr2 = (ArrayScope<Object>) obj2;

		    result.addAll(arr2.list());
		}
		else if (obj2.equals(CollectionScope.EMPTY)) {
		    ;
		}
		else {
		    result.add(obj2);
		}

		ret = result;
	    }
	    else if (obj1 instanceof ObjectScope) {
		ObjectScope map1 = (ObjectScope) obj1;
		ObjectScope result = new ObjectScope(map1, sortKeys);

		if (obj2 instanceof ObjectScope) {
		    ObjectScope map2 = (ObjectScope) obj2;

		    result.putAll(map2);
		}
		else if (obj2.equals(CollectionScope.EMPTY)) {
		    ;
		}
		else {
		    result.setValue(result.size(), obj2);
		}

		ret = result;
	    }
	    else if (obj1 instanceof SetScope) {
		@SuppressWarnings("unchecked")
		SetScope<Object> set1 = (SetScope<Object>) obj1;
		SetScope<Object> result = new SetScope<>(set1);

		if (obj2 instanceof SetScope) {
		    @SuppressWarnings("unchecked")
		    SetScope<Object> set2 = (SetScope<Object>) obj2;

		    result.addAll(set2);
		}
		else if (obj2.equals(CollectionScope.EMPTY)) {
		    ;
		}
		else {
		    result.add(obj2);
		}

		ret = result;
	    }
	    else if (obj1 instanceof CollectionScope) {
		// This has to be the EMPTY object
		ret = obj2;
	    }

	    return ret;
	}

	/**
	 * Returns the result of the "add" operation on the two values.
	 * <p> If the first value is an object or list, then concatenate the values.
	 * <p> If either object is a string, do string concatenation.
	 * <p> Else if we're doing rational calculations, convert to
	 * {@link BigFraction}, otherwise deal with them either as
	 * {@link ComplexNumber} or {@link BigDecimal} and do the addition.
	 *
	 * @param visitor  The visitor (for function evaluation).
	 * @param e1       The LHS operand.
	 * @param e2       The RHS operand.
	 * @param ctx1     The Rule context for the first operand (for error reporting).
	 * @param ctx2     The Rule context for the second operand.
	 * @param mc       The {@code MathContext} to use in rounding the result.
	 * @param rational Whether we're doing rational ({@code true}) or decimal arithmetic.
	 * @param sortKeys How to add values to objects.
	 * @return {@code e1 + e2}
	 */
	public static Object addOp(final CalcObjectVisitor visitor,
		final Object e1, final Object e2,
		final ParserRuleContext ctx1, final ParserRuleContext ctx2,
		final MathContext mc,
		final boolean rational, final boolean sortKeys)
	{
	    if (e1 == null && e2 == null)
		return null;

	    Object v1 = visitor.evaluate(ctx1, e1);
	    Object v2 = visitor.evaluate(ctx2, e2);

	    // Concatenate objects if the first is an object
	    if (v1 instanceof CollectionScope) {
		return concatObjects(v1, v2, sortKeys);
	    }

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
		// TODO: deal with complex numbers here??
		BigFraction f1 = convertToFraction(v1, ctx1);
		BigFraction f2 = convertToFraction(v2, ctx2);

		return f1.add(f2);
	    }
	    else {
		if (v1 instanceof ComplexNumber || v2 instanceof ComplexNumber) {
		    ComplexNumber c1 = ComplexNumber.valueOf(v1);
		    ComplexNumber c2 = ComplexNumber.valueOf(v2);

		    return c1.add(c2);
		}
		else {
		    BigDecimal d1 = convertToDecimal(v1, mc, ctx1);
		    BigDecimal d2 = convertToDecimal(v2, mc, ctx2);

		    return fixupToInteger(d1.add(d2, mc));
		}
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
	 * Get the type of the given object.
	 *
	 * @param obj	An object to inspect.
	 * @return	The type of the value.
	 */
	public static Typeof typeof(final Object obj) {
	    if (obj == null)
		return Typeof.NULL;
	    if (obj instanceof String)
		return Typeof.STRING;
	    if (obj instanceof BigDecimal) {
		BigDecimal dValue = (BigDecimal) obj;
		if (dValue.scale() <= 0)
		    return Typeof.INTEGER;
		return Typeof.FLOAT;
	    }
	    if (obj instanceof BigInteger || obj instanceof Long || obj instanceof Integer)
		return Typeof.INTEGER;
	    if (obj instanceof BigFraction)
		return Typeof.FRACTION;
	    if (obj instanceof ComplexNumber)
		return Typeof.COMPLEX;
	    if (obj instanceof Boolean)
		return Typeof.BOOLEAN;
	    if (obj instanceof ArrayScope)
		return Typeof.ARRAY;
	    if (obj instanceof ObjectScope)
		return Typeof.OBJECT;
	    if (obj instanceof SetScope)
		return Typeof.SET;
	    if (obj instanceof CollectionScope)
		return Typeof.COLLECTION;
	    if (obj instanceof FunctionDeclaration)
		return Typeof.FUNCTION;
// TODO: "date" or "time" ??

	    return Typeof.UNKNOWN;
	}


	/**
	 * Convert the input object to the cast type.
	 *
	 * @param visitor    The calculation engine for conversions.
	 * @param ctx        The parse tree position (for error reporting).
	 * @param value      The value to convert.
	 * @param cast       The type to convert to (if possible).
	 * @param mc         The math context to use for rounding (if necessary).
	 * @param separators Whether to use numeric separators in numeric to string conversions.
	 * @param sortKeys   How to construct any new map.
	 * @return Input value converted to the given type.
	 */
	public static Object castTo(
		final CalcObjectVisitor visitor,
		final ParserRuleContext ctx,
		final Object value,
		final Typeof cast,
		final MathContext mc,
		final boolean separators,
		final boolean sortKeys)
	{
	    Object castValue = value;

	    switch (cast) {
		case NULL:
		    castValue = null;	// just ... WHY???
		    break;
		case STRING:
		    castValue = toStringValue(visitor, ctx, value, new StringFormat(false, separators));
		    break;
		case INTEGER:
		    castValue = toIntegerValue(visitor, value, mc, ctx);
		    break;
		case FLOAT:
		    castValue = toDecimalValue(visitor, value, mc, ctx);
		    break;
		case FRACTION:
		    castValue = toFractionValue(visitor, value, ctx);
		    break;
		case COMPLEX:
		    try {
			if (castValue instanceof ArrayScope) {
			    @SuppressWarnings("unchecked")
			    ArrayScope<Object> array = (ArrayScope<Object>) castValue;
			    castValue = array.list();
			}
			else if (castValue instanceof ObjectScope) {
			    @SuppressWarnings("unchecked")
			    ObjectScope object = (ObjectScope) castValue;
			    castValue = object.map();
			}
			else if (castValue instanceof SetScope) {
			    @SuppressWarnings("unchecked")
			    SetScope<Object> set = (SetScope<Object>) castValue;
			    castValue = set.set();
			}

			castValue = ComplexNumber.valueOf(castValue);
		    }
		    catch (ArithmeticException ae) {
			throw new CalcExprException(ae, ctx);
		    }
		    break;
		case BOOLEAN:
		    castValue = toBooleanValue(visitor, value, ctx);
		    break;
		case ARRAY:
		    if (value instanceof ComplexNumber) {
			ComplexNumber c = (ComplexNumber) value;
			castValue = new ArrayScope<Object>(c.toList());
		    }
		    else if (!(value instanceof ArrayScope)) {
			ArrayScope<Object> array = new ArrayScope<>(value);
			castValue = array;
		    }
		    break;
		case OBJECT:
		    if (value instanceof ComplexNumber) {
			ComplexNumber c = (ComplexNumber) value;
			castValue = new ObjectScope(c.toMap(), sortKeys);
		    }
		    else if (!(value instanceof ObjectScope)) {
			ObjectScope obj = new ObjectScope(sortKeys);
			obj.setValue("_", value); // Name??
			castValue = obj;
		    }
		    break;
		case SET:
		    if (value instanceof ComplexNumber) {
			ComplexNumber c = (ComplexNumber) value;
			castValue = new SetScope<Object>(c.toSet());
		    }
		    else if (!(value instanceof SetScope)) {
			SetScope<Object> set = new SetScope<>(value);
			castValue = set;
		    }
		    break;
		case FUNCTION:
		    // Well, this is interesting ...
		    String text = getTreeText(ctx);
		    String name = "_" + CharUtil.getJSONForm(text);
		    FunctionDeclaration func = new FunctionDeclaration(name, ctx);
		    visitor.getVariables().setValue(name, false, func);
		    visitor.displayActionMessage("%calc#definingFunc", func.getFullFunctionName(), text);
		    castValue = func;
		    break;
		case UNKNOWN:
		    // Just leave the object as-is, since we don't know what kind it is
		    break;
	    }

	    return castValue;
	}


	/**
	 * Search for an object inside an {@link ArrayScope}. This is not straightforward
	 * due to (especially with numbers) differences in representation of the array objects
	 * and the search object.
	 * <p> We will do the compare using our {@code compareValues} method, which does a lot of
	 * type conversion.
	 *
	 * @param visitor The visitor needed to do the conversions.
	 * @param ctx1    Parse tree of the source array.
	 * @param ctx2    And for the search value.
	 * @param list    The array object containing the values to search.
	 * @param search  Object to search for in the array.
	 * @param start   The starting index for the search (zero-based); can be negative to search backwards.
	 * @param mc      Rounding context in case we need to convert strings to numbers.
	 * @return        A zero-based index into the array if the value is found, or {@code -1} if not.
	 */
	public static int indexOf(
		CalcObjectVisitor visitor,
		ParserRuleContext ctx1,
		ParserRuleContext ctx2,
		ArrayScope<Object> list,
		Object search,
		int start,
		MathContext mc)
	{
	    List<Object> objects = list.list();
	    int size = objects.size();
	    int index;

	    if (start < 0) {
		// Search backwards, but with result always zero-based and positive
		for (index = size + start; index >= 0; index--) {
		    Object listObj = objects.get(index);
		    if (compareValues(visitor, ctx1, ctx2, listObj, search, mc, false, true, false, false, true) == 0)
			return index;
		}
	    }
	    else {
		// Search forwards
		for (index = start; index < size; index++) {
		    Object listObj = objects.get(index);
		    if (compareValues(visitor, ctx1, ctx2, listObj, search, mc, false, true, false, false, true) == 0)
			return index;
		}
	    }

	    return -1;
	}


	/**
	 * Format a decimal/integer number with/without thousands separators.
	 *
	 * @param value A decimal value which could actually be an integer.
	 * @param sep   Whether or not to use separators.
	 * @param scale The minimum number of decimal digits to display (pad on the left
	 *              with zeros, before adding separators); {@code Integer.MIN_VALUE}
	 *              to ignore.
	 * @return      The value formatted appropriately with thousands separators.
	 */
	public static String formatWithSeparators(final BigDecimal value, final boolean sep, final int scale) {
	    String formatString = "";
	    int valueScale = value.scale();
	    int valuePrec = value.precision();
	    int displayScale = scale;

	    // There are no digits right of the decimal point, so treat as an integer
	    if (valueScale <= 0) {
		if (scale != Integer.MIN_VALUE) {
		    if (sep)
			displayScale += (valuePrec - valueScale) / 3;
		    formatString = String.format("%%1$0%1$dd", displayScale);
		}
		else {
		    formatString = "%1$d";
		}
	    }
	    else {
		if (scale != Integer.MIN_VALUE) {
		    if (sep)
			displayScale += (valuePrec - valueScale) / 3;
		    formatString = String.format("%%1$0%1$d.%2$df", displayScale, valueScale);
		}
		else {
		    formatString = String.format("%%1$.%1$df", valueScale);
		}
	    }

	    if (sep && !formatString.isEmpty()) {
		formatString = formatString.replace("%1$", "%1$,");
	    }

	    if (formatString.isEmpty()) {
		return value.toPlainString();
	    }
	    else {
		if (formatString.endsWith("d"))
		    return String.format(formatString, value.toBigInteger());
		else
		    return String.format(formatString, value);
	    }
	}

	/**
	 * Is the given character a "part" (that is, legal for after the start char)
	 * of a local variable name.
	 *
	 * @param str	The string we're navigating through
	 * @param start The starting position of the identifier
	 * @param pos	Position of the character to examine
	 * @return	Whether the given character is legal for a local variable name.
	 */
	private static boolean isGlobalVarPart(final String str, final int start, final int pos) {
	    char ch = str.charAt(pos);
	    return ((pos == start + 1) && (ch == '#' || ch == '*')) || (ch >= '0' && ch <= '9');
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

	    String rawValue = getRawString(value, true);
	    int lastPos = -1;
	    int pos, startPos;
	    StringBuilder output = new StringBuilder(rawValue.length() * 2);

	    // Scan the string looking for the values to interpolate, starting with '$'.
	    // After that could be a special "global" variable name, a bracketed expression, or
	    // a normal variable name.
	    while ((pos = rawValue.indexOf('$', ++lastPos)) >= 0) {
		output.append(rawValue.substring(lastPos, pos));

		if (pos == rawValue.length() - 1)
		    throw new CalcExprException("%calc#invalidConstruct", ctx);

		startPos = pos + 1;
		char nextChar = rawValue.charAt(startPos);

		if (nextChar == '$') {
		    int identPos = startPos + 1;
		    while (identPos < rawValue.length() && isGlobalVarPart(rawValue, startPos, identPos))
			identPos++;
		    if (identPos > startPos + 1) {
			String varName = rawValue.substring(startPos, identPos);
			Object varValue = variables.getValue(varName, settings.ignoreNameCase);
			// But if the special global var is not defined, then output the nextChar and go on
			if (varValue != null) {
			    output.append(toStringValue(visitor, ctx, varValue, new StringFormat(false, settings)));
			    lastPos = identPos - 1;
			}
			else {
			    output.append(nextChar);
			    lastPos = startPos;
			}
		    }
		    else {
			output.append(nextChar);
			lastPos = startPos;
		    }
		}
		else if (nextChar == '{') {
		    // Get position of matching '}'
		    int nextPos = CharUtil.findMatching(rawValue, startPos);

		    if (pos + 2 >= rawValue.length() || nextPos < 0)
			throw new CalcExprException("%calc#invalidConst2", ctx);

		    String expr = rawValue.substring(pos + 2, nextPos);
		    Object exprValue = Calc.processString(expr, true);
		    String stringValue = toStringValue(visitor, ctx, exprValue, new StringFormat(false, settings));

		    // The result is going to be formatted with quotes, separators, everything that it currently
		    // needs to be output, BUT it will go through the quoting again inside the formatter code
		    // so we need to strip quotes and escaped quotes or we will get double
		    output.append(CharUtil.stripDoubleQuotes(stringValue));
		    lastPos = nextPos;
		}
		else if (isIdentifierStart(rawValue, startPos)) {
		    int identPos = startPos + 1;
		    while (identPos < rawValue.length() && isIdentifierPart(rawValue, startPos, identPos))
			identPos++;
		    String varName = rawValue.substring(startPos, identPos);
		    output.append(toStringValue(visitor, ctx,
			variables.getValue(varName, settings.ignoreNameCase), new StringFormat(false, settings)));
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
	 * @see #getRawString(String, boolean)
	 */
	public static String getRawString(final String escapedForm) {
	    return getRawString(escapedForm, false);
	}

	/**
	 * Given the escaped form of a string (that is, what appears in the script as the user
	 * typed it), remove the outer quotes, convert any escape sequences, and return the
	 * raw string ready to be further processed.
	 *
	 * @param escapedForm	The input string value.
	 * @param skipExprs	For interpolated strings, don't convert escape seqs in expressions.
	 * @return		The raw string data, with all quotes removed and escape sequences converted.
	 */
	public static String getRawString(final String escapedForm, final boolean skipExprs) {
	    if (escapedForm.charAt(0) == 's') {
		return CharUtil.stripAnyQuotes(escapedForm.substring(1), true);
	    }
	    else {
		return CharUtil.convertEscapeSequences(CharUtil.stripAnyQuotes(escapedForm, true), skipExprs);
	    }
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

	/**
	 * Save the given set of variables to the file path given (with given charset).
	 *
	 * @param visitor   The visitor used for calculating expressions.
	 * @param ctx       Parsing context (for error reporting).
	 * @param variables The set of variables to save.
	 * @param path      Path of file to save to.
	 * @param charset   The character set to use when writing.
	 * @throws IOException if there was a problem writing the output file.
	 */
	public static void saveVariables(
		final CalcObjectVisitor visitor,
		final ParserRuleContext ctx,
		final ObjectScope variables,
		final Path path,
		final Charset charset)
			throws IOException
	{
	    try (BufferedWriter writer = Files.newBufferedWriter(path, charset == null ? DEFAULT_CHARSET : charset)) {
		// Write out the current version as a ":requires" directive
		SemanticVersion prog_version = Environment.programVersion();
		SemanticVersion prog_base = Environment.implementationVersion();
		writer.write(String.format(LIB_FORMAT, prog_version.toPreReleaseString(), prog_base.toPreReleaseString()));
		writer.newLine();
		writer.write(Intl.getString("calc#libVersionDescription"));
		writer.newLine();

		// Note: the keySet returned from ObjectScope is in order of declaration, which is important here, since we
		// must be able to read back the saved file and have the values computed to be the same as they are now.
		for (String key : variables.keySet()) {
		    Object value = variables.getValue(key, false);
		    if (!isPredefined(value, true)) {
			if (value instanceof FunctionDeclaration) {
			    FunctionDeclaration func = (FunctionDeclaration) value;
			    writer.write(String.format("def %1$s = %2$s", func.getFullFunctionName(), getTreeText(func.getFunctionBody())));
			}
			else {
			    // Note: cannot use separators on output here because the input grammar won't recognize them
			    writer.write(String.format("%1$s = %2$s", key,
				toStringValue(visitor, ctx, value, new StringFormat(true, false))));
			}
			writer.newLine();
		    }
		}
	    }
	}

	/**
	 * A recursive procedure to do a deep copy of any object and apply the given transform to any
	 * of the strings within the the objects.
	 *
	 * @param visitor  Visitor used to calculate expressions.
	 * @param ctx      The expression context for the object being copied.
	 * @param obj      The object to copy.
	 * @param sortKeys How to create new objects.
	 * @param mapper   Transformation to apply to all the strings within the object.
	 * @return         The deep copy of the original object with the transformer applied to all strings.
	 */
	public static Object copyAndTransform(
		final CalcObjectVisitor visitor,
		final ParserRuleContext ctx,
		final Object obj,
		final boolean sortKeys,
		final Transformer mapper)
	{
	    if (obj == null) {
		return null;
	    }
	    else if (obj instanceof ObjectScope) {
		ObjectScope map = (ObjectScope) obj;
		ObjectScope result = new ObjectScope(sortKeys);

		for (Map.Entry<String, Object> entry : map.map().entrySet()) {
		    String key = entry.getKey();
		    Object value = entry.getValue();
		    Object newValue = null;

		    if (value != null) {
			value = visitor.evaluate(ctx, value);

			if (value instanceof CollectionScope) {
			    newValue = copyAndTransform(visitor, ctx, value, sortKeys, mapper);
			}
			else {
			    newValue = mapper.applyToMap(value, false);
			}
		    }

		    if (newValue != null || mapper.copyNull()) {
			if (mapper.forKeys()) {
			    String newKey = (String) mapper.applyToMap(key, true);
			    if (newKey != null || mapper.copyNull()) {
				result.setValue(newKey, newValue);
			    }
			}
			else {
			    result.setValue(key, newValue);
			}
		    }
		}

		return result;
	    }
	    else if (obj instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<Object> list = (ArrayScope<Object>) obj;
		ArrayScope<Object> result = new ArrayScope<>();

		for (Object value : list.list()) {
		    Object newValue = null;
		    if (value != null) {
			value = visitor.evaluate(ctx, value);

			if (value instanceof CollectionScope) {
			    newValue = copyAndTransform(visitor, ctx, value, sortKeys, mapper);
			}
			else {
			    newValue = mapper.apply(value);
			}
		    }

		    if (newValue != null || mapper.copyNull()) {
			result.add(newValue);
		    }
		}

		return result;
	    }
	    else if (obj instanceof SetScope) {
		@SuppressWarnings("unchecked")
		SetScope<Object> set = (SetScope<Object>) obj;
		SetScope<Object> result = new SetScope<>();

		for (Object value : set.set()) {
		    Object newValue = null;
		    if (value != null) {
			value = visitor.evaluate(ctx, value);

			if (value instanceof CollectionScope) {
			    newValue = copyAndTransform(visitor, ctx, value, sortKeys, mapper);
			}
			else {
			    newValue = mapper.apply(value);
			}
		    }

		    if (newValue != null || mapper.copyNull()) {
			result.add(newValue);
		    }
		}

		return result;
	    }
	    else if (obj instanceof CollectionScope) {
		return obj;
	    }

	    // For all other scalar values, just get the string value and apply the transform
	    String exprString = toStringValue(visitor, ctx, obj, new StringFormat(false, visitor.getSettings()));
	    return mapper.apply(exprString);
	}

	/**
	 * Do a "flat map" of values for the "sumof", "productof", and "exec" functions.  Since each
	 * value to be processed could be an array or map, we need to traverse these objects
	 * as well as the simple values in order to get the full list to process.
	 *
	 * @param visitor       Visitor used to evaluate expressions.
	 * @param ctx		The overall parser context for the function (for error messages).
	 * @param obj		The object to be added to the list, or recursed into when the object
	 *			is a list or map.
	 * @param objectList	The complete list of values to be built.
	 * @param conversion	Type of conversion to do on the values.
	 * @param level		Level of recursion.
	 */
	private static void buildValueList(
		final CalcObjectVisitor visitor,
		final ParserRuleContext ctx,
		final Object obj,
		final List<Object> objectList,
		final Conversion conversion,
		final int level)
	{
	    Object value = visitor.evaluate(ctx, obj);

	    if (value instanceof ArrayScope) {
		if (conversion == Conversion.UNCHANGED && level > 0) {
		    objectList.add(value);
		}
		else {
		    @SuppressWarnings("unchecked")
		    ArrayScope<Object> array = (ArrayScope<Object>) value;
		    for (Object listObj : array.list()) {
			buildValueList(visitor, ctx, listObj, objectList, conversion, level + 1);
		    }
		}
	    }
	    else if (value instanceof ObjectScope) {
		if (conversion == Conversion.UNCHANGED && level > 0) {
		    objectList.add(value);
		}
		else {
		    @SuppressWarnings("unchecked")
		    ObjectScope map = (ObjectScope) value;
		    for (Object mapObj : map.values()) {
			buildValueList(visitor, ctx, mapObj, objectList, conversion, level + 1);
		    }
		}
	    }
	    else if (value instanceof SetScope) {
		if (conversion == Conversion.UNCHANGED && level > 0) {
		    objectList.add(value);
		}
		else {
		    @SuppressWarnings("unchecked")
		    SetScope<Object> set = (SetScope<Object>) value;
		    for (Object setObj : set.set()) {
			buildValueList(visitor, ctx, setObj, objectList, conversion, level + 1);
		    }
		}
	    }
	    else if (value instanceof CollectionScope) {
		// This is always an empty object, so nothing to do here
	    }
	    else {
		switch (conversion) {
		    case STRING:
			objectList.add(toStringValue(visitor, ctx, value, new StringFormat(false, false)));
			break;
		    case DECIMAL:
			nullCheck(value, ctx);
			objectList.add(toDecimalValue(visitor, value, visitor.getSettings().mc, ctx));
			break;
		    case FRACTION:
			nullCheck(value, ctx);
			objectList.add(toFractionValue(visitor, value, ctx));
			break;
		    case UNCHANGED:
			objectList.add(value);
			break;
		}
	    }
	}

	/**
	 * Traverse the expression list for the "sumof", "productof", and "sort" functions and
	 * build the complete list of values to be processed.
	 *
	 * @param visitor    Visitor used to evaluate expressions.
	 * @param exprs	     The parsed list of expression contexts.
	 * @param conversion How or whether to convert the values for the final list.
	 * @return	     The completely built "flat map" of values.
	 */
	public static List<Object> buildValueList(final CalcObjectVisitor visitor, final List<CalcParser.ExprContext> exprs, final Conversion conversion) {
	    List<Object> objects = new ArrayList<>();

	    for (CalcParser.ExprContext exprCtx : exprs) {
		buildValueList(visitor, exprCtx, visitor.evaluate(exprCtx), objects, conversion, 0);
	    }

	    return objects;
	}

	/**
	 * Check required base and / or regular versions and throw if not compatible.
	 *
	 * @param required	The regular version text (or {@code null}).
	 * @param baseRequired	Base version text (or {@code null}).
	 * @throws IllegalArgumentException if there is a version mismatch or parsing error.
	 */
	public static void checkRequiredVersions(final String required, final String baseRequired) {
	    SemanticVersion requireVersion = null;
	    SemanticVersion requireBaseVersion = null;

	    try {
		if (required == null && baseRequired != null) {
		    // Just a base version
		    requireBaseVersion = new SemanticVersion(baseRequired);
		}
		else if (required != null && baseRequired != null) {
		    // version + base version
		    requireVersion = new SemanticVersion(required);
		    requireBaseVersion = new SemanticVersion(baseRequired);
		}
		else {
		    // Just a regular version
		    requireVersion = new SemanticVersion(required);
		}
	    }
	    catch (ParseException pe) {
		throw new Intl.IllegalArgumentException("calc#versionParseError", Exceptions.toString(pe));
	    }

	    if (requireVersion != null) {
		SemanticVersion progVersion = Environment.programVersion();
		if (progVersion.compareTo(requireVersion) < 0)
		    throw new Intl.IllegalArgumentException("calc#libVersionMismatch", requireVersion, progVersion);
	    }
	    if (requireBaseVersion != null) {
		SemanticVersion baseVersion = Environment.implementationVersion();
		if (baseVersion.compareTo(requireBaseVersion) < 0)
		    throw new Intl.IllegalArgumentException("calc#baseVersionMismatch", requireBaseVersion, baseVersion);
	    }
	}

	/**
	 * Check recursively for the given key name in an object.
	 *
	 * @param obj	The top-most object to start the search in.
	 * @param key	Name of a member to search for.
	 * @param ignoreCase	Whether to ignore the case of names.
	 * @return	Whether or not the key was found anywhere in the object hierarchy.
	 */
	public static boolean isDefinedRecursively(final ObjectScope obj, final String key, final boolean ignoreCase) {
	    if (obj.isDefinedLocally(key, ignoreCase))
		return true;

	    for (String name : obj.keySet()) {
		Object value = obj.getValueLocally(name, ignoreCase);
		if (value instanceof ValueScope) {
		    value = ((ValueScope) value).getValue();
		}
		if (value instanceof ObjectScope) {
		    if (isDefinedRecursively((ObjectScope) value, key, ignoreCase))
			return true;
		}
	    }

	    return false;
	}

	/**
	 * Scan a source string, according to the given format, into a set of variables.
	 *
	 * @param visitor The calculation object, used to resolve expressions.
	 * @param source  The source to scan.
	 * @param format  Format of the source information.
	 * @param vars    List of variables to hold the scanned values.
	 * @return        ??
	 * @throws IllegalArgumentException if the source can't be interpreted according to the format.
	 */
	public static Object scanIntoVars(final CalcObjectVisitor visitor, final String source, final String format, final List<LValueContext> vars) {
	    Scanner scanner = new Scanner(source);
	    Object lastValue = null;

	    StringBuilder pattern = new StringBuilder();
	    int varIndex = 0;
	    Pattern defaultDelimiter = scanner.delimiter();

	formatLoop:
	    for (int i = 0; i < format.length(); i++) {
		char ch = format.charAt(i);
		if (ch == '%') {
		    if (pattern.length() > 0) {
			String pat = Pattern.quote(pattern.toString());
			if (scanner.findWithinHorizon(pat, pat.length()) == null) {
			    throw new Intl.IllegalArgumentException("calc#scanPatternError", pat);
			}
			pattern.setLength(0);
		    }
		    if (i + 1 < format.length()) {
			Object value = null;
			char formatCh = format.charAt(++i);
			if (formatCh == 'c')
			    scanner.useDelimiter("");
			else if (i + 1 < format.length())
			    scanner.useDelimiter(format.substring(i + 1, i + 2));
			else
			    scanner.useDelimiter(defaultDelimiter);

			try {
			    switch (formatCh) {
				case '%':
				    pattern.append(formatCh);
				    continue formatLoop;
				case 'd':
				    value = scanner.nextBigDecimal();
				    break;
				case 'i':
				    value = scanner.nextBigInteger();
				    break;
				case 'b':
				    value = scanner.nextBoolean();
				    break;
				case 'n':
				    pattern.append("\\r?\\n");
				    continue formatLoop;
				case 'c':
				    value = scanner.next();
				    break;
				case 's':
				    value = scanner.nextLine();
				    break;
				default:
				    throw new Intl.IllegalArgumentException("calc#illegalScanType", formatCh);
			    }
			}
			catch (NoSuchElementException ex) {
			    throw new Intl.IllegalArgumentException("calc#scanPatternError", String.format("%%%1$c", formatCh));
			}
			LValueContext lValue = vars.get(varIndex++);
			lValue.putContextObject(visitor, value);
			lastValue = value;
		    }
		    else {
			throw new Intl.IllegalArgumentException("calc#badScanPattern", format);
		    }
		}
		else {
		    pattern.append(ch);
		}
	    }
	    if (pattern.length() > 0) {
		String pat = Pattern.quote(pattern.toString());
		if (scanner.findWithinHorizon(pat, pat.length()) == null) {
		    throw new Intl.IllegalArgumentException("calc#scanPatternError", pat);
		}
	    }
	    return lastValue;
	}

}

