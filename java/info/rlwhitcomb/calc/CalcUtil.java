/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2025 Roger L. Whitcomb.
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
 *	    #573: More work quoting patterns, including "%n".
 *	17-Dec-2022 (rlwhitcomb)
 *	    #572: New method to regularize member name processing.
 *	31-Dec-2022 (rlwhitcomb)
 *	    #558: Basic support for quaternions.
 *	04-Jan-2023 (rlwhitcomb)
 *	    #537: Add method to load/cache the scripts properties.
 *	05-Jan-2023 (rlwhitcomb)
 *	    #558: Quaternion basic arithmetic.
 *	10-Jan-2023 (rlwhitcomb)
 *	    #558: Give quaternion priority over complex, so "i" promotion works.
 *	24-Jan-2023 (rlwhitcomb)
 *	    #594: Redo "bitOp" to work better on pure boolean values; add two more
 *	    bit operations.
 *	16-Feb-2023 (rlwhitcomb)
 *	    #244: Move "formatWithSeparators" into Num, for use in more places. Apply
 *	    to fraction formatting.
 *	21-Feb-2023 (rlwhitcomb)
 *	    #244: Apply separators to complex numbers now too.
 *	23-Feb-2023 (rlwhitcomb)
 *	    #244: Upgrades for Quaternions.
 *	03-Apr-2023 (rlwhitcomb)
 *	    #263: New conversions for complex and quaternions.
 *	07-Apr-2023 (rlwhitcomb)
 *	    #603: Change directive char to "$" to eliminate amibiguity.
 *	16-May-2023 (rlwhitcomb)
 *	    Protect against NPE in some obscure cases of object concatenation.
 *	    Rename some methods. Implement object concatenation more completely.
 *	19-Sep-2023 (rlwhitcomb)
 *	    #629: Calling "evaluateToValue" now so that function parameters get called
 *	    as needed to get their return values.
 *	26-Sep-2023 (rlwhitcomb)
 *	    #626: Add (recursive) "negate" and "number" methods.
 *	28-Nov-2023 (rlwhitcomb)
 *	    #635: Add indenting to "getTreeText".
 *	01-Jan-2024 (rlwhitcomb)
 *	    #638: Add a new kind of debug printout of the parse tree.
 *	09-Jan-2024 (rlwhitcomb)
 *	    #644: New flavors of "convertToInteger" and "convertToInt" with possible null substitutions.
 *	30-Jan-2024 (rlwhitcomb)
 *	    #649: Options for extra spacing for fractions.
 *	15-Feb-2024 (rlwhitcomb)
 *	    #654: Add INTEGER conversion to "buildValueList" for "isPrime".
 *	04-Mar-2024 (rlwhitcomb)
 *	    #657: Make ObjectComparator non-private for use by "search".
 *	07-Mar-2024 (rlwhitcomb)
 *	    #657: New binary search method that works on ArrayList...
 *	13-Mar-2024 (rlwhitcomb)
 *	    #661: Add set union and intersection to "bitOp".
 *	20-Mar-2024 (rlwhitcomb)
 *	    #665: "addOp" and "compareValues" need to convert values using
 *	    "toStringValue" instead of generic "toString".
 *	14-May-2024 (rlwhitcomb)
 *	    "toDecimalValue" does a better job on complex and quaternion values.
 *	06-Nov-2024 (rlwhitcomb)
 *	    #693: Fix strict equality in one specific case of wanna-be integers.
 *	31-Jan-2025 (rlwhitcomb)
 *	    #706: Fix cast of a collection to a set.
 *	23-Mar-2025 (rlwhitcomb)
 *	    Broaden the use of integer base for "powerOp" to use the appropriate MathUtil method.
 *	26-Mar-2025 (rlwhitcomb)
 *	    Move "isInteger()" from ClassUtil to MathUtil.
 *	14-Apr-2025 (rlwhitcomb)
 *	    #713: Fix "scale" for integer values.
 *	17-May-2025 (rlwhitcomb)
 *	    #719: Recursive interpolation of strings.
 *	22-Jun-2025 (rlwhitcomb)
 *	    #694: A little tweaking of "bitOp" parameters.
 *	13-Jul-2025 (rlwhitcomb)
 *	    #740: New method for dot product of lists.
 *	10-Aug-2025 (rlwhitcomb)
 *	    #745: Change exponent for "powerOp" to BigDecimal.
 *	10-Aug-2025 (rlwhitcomb)
 *	    #750: New "rootOp" method.
 *	14-Aug-2025 (rlwhitcomb)
 *	    #744: Symmetric difference of sets.
 *	12-Sep-2025 (rlwhitcomb)
 *	    #762: Small changes for integer conversions. Remove '\u00B7' as part of a name.
 *	14-Sep-2025 (rlwhitcomb)
 *	    #761: Changes to the way we handle "quiet" calculations inside interpolated strings.
 *	13-Sep-2025 (rlwhitcomb)
 *	    #754: Strict equality of sets means same order also.
 *	27-Sep-2025 (rlwhitcomb)
 *	    #768: Better handling of "cast" with sets and objects in and out of array form.
 *	24-Oct-2025 (rlwhitcomb)
 *	    Add "isScalar" method to support determining when "dot" operator is just a multiply.
 *	20-Nov-2025 (rlwhitcomb)
 *	    #643: Start of continued fraction support.
 *	11-Dec-2025 (rlwhitcomb)
 *	    #643: More continued fraction support.
 */
package info.rlwhitcomb.calc;

import de.onyxbits.SemanticVersion;
import info.rlwhitcomb.math.BigFraction;
import info.rlwhitcomb.math.ComplexNumber;
import info.rlwhitcomb.math.ContinuedFraction;
import info.rlwhitcomb.math.MathUtil;
import info.rlwhitcomb.math.Num;
import info.rlwhitcomb.math.Quaternion;
import info.rlwhitcomb.util.CharUtil;
import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.Exceptions;
import info.rlwhitcomb.util.Intl;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.padler.natorder.NaturalOrderComparator;

import java.io.BufferedWriter;
import java.io.InputStream;
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
	private static final String LIB_FORMAT = "$Requires '%1$s', Base '%2$s'";

	/** Name of the scripts properties file (see also "makeScripts.calc" where it is generated). */
	public static final String SCRIPT_PROPERTIES_FILE = "calcscripts.properties";

	/** Cached properties for the utilities programs. */
	private static Properties calcScripts = null;

	/** Square root symbol. */
	public static final String SQ_ROOT = "\u221A";
	/** Cube root symbol. */
	public static final String CB_ROOT = "\u221B";
	/** Fourth root symbol. */
	public static final String FT_ROOT = "\u221C";


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
		/** Level of indent (each is 3 spaces) for the new lines. */
		int level = 0;


		TreeTextOptions() {
		}

		TreeTextOptions(final TreeTextOptions other) {
		    spaceColon       = other.spaceColon;
		    spaceMinus       = other.spaceMinus;
		    spaceOpenBracket = other.spaceOpenBracket;
		    spaceOpenParen   = other.spaceOpenParen;
		    spaceBraces      = other.spaceBraces;
		    level            = other.level;
		}
	}


	/**
	 * Make simple text string from the given parse tree, with a single space between nodes.
	 *
	 * @param ctx The parse tree to scan.
	 * @return    The text from the tree.
	 */
	static String getSimpleTreeText(final ParseTree ctx) {
	    StringBuilder buf = new StringBuilder();
	    getSimpleTreeText(buf, ctx);
	    return CharUtil.rtrim(CharUtil.quoteControl(buf.toString()));
	}

	private static void getSimpleTreeText(final StringBuilder buf, final ParseTree ctx) {
	    int count = ctx.getChildCount();
	    if (count == 0) {
		buf.append(ctx.getText());
	    }
	    else {
		for (int i = 0; i < count; i++) {
		    getSimpleTreeText(buf, ctx.getChild(i));
		}
	    }
	    int len = buf.length();
	    while (len > 0) {
		if (buf.charAt(--len) != ' ')
		    break;
	    }
	    buf.setLength(++len);
	    buf.append(' ');
	}

	/**
	 * Try to derive a "nice" name for the object, which should be a parse tree node.
	 *
	 * @param obj The parse tree object for which we want a good name for display.
	 * @return    A suitable name for this object for display.
	 */
	static String objName(Object obj) {
	    String name = obj.getClass().getSimpleName();
	    name = name.replace("Context", "").replace("TerminalNodeImpl", "text");
	    return CharUtil.capitalizeFirst(name);
	}

	/**
	 * Append the tree node display name (olus newline) to the buffer being built.
	 *
	 * @param buf   The buffer to build the entire string in.
	 * @param ctx   Current parse tree node to print.
	 * @param level Indent level of this node (should start at zero for the root node).
	 */
	private static void printTreeNode(StringBuilder buf, ParseTree ctx, int level) {
	    CharUtil.makeStringOfChars(buf, ' ', level * 3);

	    buf.append(objName(ctx))
	       .append(": ")
	       .append(getSimpleTreeText(ctx))
	       .append(Environment.lineSeparator());

	    for (int i = 0; i < ctx.getChildCount(); i++) {
		printTreeNode(buf, ctx.getChild(i), level + 1);
	    }
	}

	/**
	 * Come up with a debug representation of the given parse tree.
	 *
	 * @param ctx The entire or partial parse tree to traverse.
	 * @return    An appropriate string representation of the tree to display
	 *            with embedded newlines, but not a trailing one.
	 */
	public static String printTree(ParseTree ctx) {
	    StringBuilder buf = new StringBuilder();
	    printTreeNode(buf, ctx, 0);
	    return CharUtil.rtrim(buf.toString());
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

	    return CharUtil.rtrim(buf);
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
	    else if (ctx instanceof CalcParser.StmtBlockContext || ctx instanceof CalcParser.CaseStmtContext) {
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
			case "{":
			    space = localOptions.spaceBraces;
			    localOptions.level++;
			    break;
			case "}":
			    replace = !localOptions.spaceBraces || (prevChar == '\r' || prevChar == '\n');
			    // reduce indent already added by removing 3 spaces prior (if present)
			    int len = buf.length() - 1;
			    if (buf.charAt(len) == ' ' && buf.charAt(len - 1) == ' ' && buf.charAt(len - 2) == ' ') {
				buf.setLength(buf.length() - 3);
			    }
			    localOptions.level--;
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

		    if ((firstChar == '\n' || firstChar == '\r') && localOptions.level > 0) {
			CharUtil.makeStringOfChars(buf, ' ', localOptions.level * 3);
			space = false;
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
	 * Convert a suitable object to {@link BigDecimal} and strip trailing zeros.
	 * <p> The suitable objects are: {@link BigDecimal} (of course), {@link BigInteger},
	 * or {@link String}.
	 *
	 * @param obj	The candidate value.
	 * @return	The numerically equivalent value with no trailing zeros.
	 */
	public static BigDecimal fixup(final Object obj) {
	    BigDecimal bd;

	    if (obj instanceof BigDecimal)
		bd = (BigDecimal) obj;
	    else if (obj instanceof BigInteger)
		bd = new BigDecimal((BigInteger) obj);
	    else
		bd = new BigDecimal(obj.toString());

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
	 * @see CalcObjectVisitor#setGlobalVariable
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
	    Object value = visitor.evaluateToValue(ctx, obj);
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

	    try {
		if (value instanceof BigDecimal || value instanceof BigInteger || value instanceof String)
		    return fixup(value);
		else if (value instanceof BigFraction)
		    return fixup(((BigFraction) value).toDecimal(mc));
		else if (value instanceof ComplexNumber) {
		    ComplexNumber cValue = (ComplexNumber) value;
		    return fixup(cValue.isPureReal() ? cValue.r() : cValue.abs(mc));
		}
		else if (value instanceof Quaternion) {
		    Quaternion qValue = (Quaternion) value;
		    return fixup(qValue.isPureReal() ? qValue.a() : qValue.magnitude(mc));
		}
		else if (value instanceof ContinuedFraction)
		    return ((ContinuedFraction) value).toDecimal(mc);
		else if (value instanceof Boolean)
		    return ((Boolean) value).booleanValue() ? BigDecimal.ONE : BigDecimal.ZERO;
		else if (isFloat(value))
		    return fixup(new BigDecimal(((Number) value).doubleValue()));
		else if (value instanceof Number)
		    return fixup(BigDecimal.valueOf(((Number) value).longValue()));
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx);
	    }

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
	    Object value = visitor.evaluateToValue(ctx, obj);
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
	    else if (value instanceof Quaternion)
		return new BigFraction(((Quaternion) value).a());
	    else if (value instanceof ContinuedFraction)
		return ((ContinuedFraction) value).toFraction();
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
	 * Cast or convert the given value to a {@link BigInteger} value for use in strictly integer calculations
	 * (such as gcd, lcm, or isprime).
	 *
	 * @param visitor	The visitor, used to evaluate expressions.
	 * @param obj		The input object value to be converted.
	 * @param mc		Rounding mode for conversions.
	 * @param ctx		The parse tree context (for error reporting).
	 * @return		The converted integer value from the input.
	 * @throws CalcExprException for null inputs, or other errors from conversion.
	 */
	public static BigInteger toIntegerValue(final CalcObjectVisitor visitor, final Object obj, final MathContext mc, final ParserRuleContext ctx) {
	    Object value = visitor.evaluateToValue(ctx, obj);
	    return convertToInteger(value, mc, ctx);
	}

	/**
	 * Cast or convert the given object value to a {@link BigInteger} for certain integer calculations.
	 *
	 * @param value		The input value to convert.
	 * @param mc		The math context to use for any conversions from decimal values.
	 * @param ctx		The parse tree context, used for error reporting.
	 * @return		The converted value, if possible.
	 * @throws CalcExprException if the value is not or cannot be converted to an exact integer value.
	 */
	public static BigInteger convertToInteger(final Object value, final MathContext mc, final ParserRuleContext ctx) {
	    return convertToInteger(value, mc, ctx, null);
	}

	/**
	 * Cast or convert the given object value to a {@link BigInteger} for certain integer calculations.
	 *
	 * @param value		The input value to convert.
	 * @param mc		The math context to use for any conversions from decimal values.
	 * @param ctx		The parse tree context, used for error reporting.
	 * @param nullValue	What a null value should be converted to (can still be null).
	 * @return		The converted value, if possible.
	 * @throws CalcExprException if the value is not or cannot be converted to an exact integer value.
	 */
	public static BigInteger convertToInteger(final Object value, final MathContext mc, final ParserRuleContext ctx, final BigInteger nullValue) {
	    if (value == null && nullValue != null) {
		return nullValue;
	    }

	    try {
		if (value instanceof BigInteger)
		    return (BigInteger) value;
		else if (value instanceof BigDecimal)
		    return ((BigDecimal) value).toBigIntegerExact();
		else if (value instanceof BigFraction)
		    return ((BigFraction) value).toIntegerExact();
		else if (value instanceof ComplexNumber)
		    return ((ComplexNumber) value).r().toBigIntegerExact();
		else if (value instanceof Quaternion)
		    return ((Quaternion) value).a().toBigIntegerExact();
		else if (value instanceof ContinuedFraction)
		    return ((ContinuedFraction) value).toIntegerExact();
		else if (value instanceof Number)
		    return BigInteger.valueOf(((Number) value).longValue());
		else
		    return convertToDecimal(value, mc, ctx).toBigIntegerExact();
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx);
	    }
	}

	/**
	 * Cast or convert the given object value to a regular integer value for certain parameter values.
	 *
	 * @param value		The input value to convert.
	 * @param mc		The math context to use for any conversions from decimal values.
	 * @param ctx		The parse tree context, used for error reporting.
	 * @return		The converted value, if possible.
	 * @throws CalcExprException if the value is not or cannot be converted to an exact integer value.
	 */
	public static int convertToInt(final Object value, final MathContext mc, final ParserRuleContext ctx) {
	    return convertToInt(value, mc, ctx, null);
	}

	/**
	 * Cast or convert the given object value to a regular integer value for certain parameter values.
	 *
	 * @param value		The input value to convert.
	 * @param mc		The math context to use for any conversions from decimal values.
	 * @param ctx		The parse tree context, used for error reporting.
	 * @param nullValue	Possible value to use if the input is null (can be null still).
	 * @return		The converted value, if possible.
	 * @throws CalcExprException if the value is not or cannot be converted to an exact integer value.
	 */
	public static int convertToInt(final Object value, final MathContext mc, final ParserRuleContext ctx, final Integer nullValue) {
	    if (value == null && nullValue != null) {
		return nullValue.intValue();
	    }

	    try {
		if (value instanceof BigInteger)
		    return ((BigInteger) value).intValueExact();
		else if (value instanceof BigDecimal)
		    return ((BigDecimal) value).intValueExact();
		else if (value instanceof BigFraction)
		    return ((BigFraction) value).intValueExact();
		else if (value instanceof ComplexNumber)
		    return ((ComplexNumber) value).r().intValueExact();
		else if (value instanceof Quaternion)
		    return ((Quaternion) value).a().intValueExact();
		else if (value instanceof ContinuedFraction)
		    return ((ContinuedFraction) value).intValueExact();
		else if (value instanceof Number)
		    return ((Number) value).intValue();
		else
		    return convertToDecimal(value, mc, ctx).intValueExact();
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
	    Object value = visitor.evaluateToValue(ctx, obj);

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
	    Object result = visitor.evaluateToValue(ctx, obj);

	    if (result instanceof CollectionScope) {
		if (result instanceof ObjectScope)
		    return toStringValue(visitor, ctx, ((ObjectScope) result).map(), format, indent, level);
		else if (result instanceof ArrayScope)
		    return toStringValue(visitor, ctx, ((ArrayScope) result).list(), format, indent, level);
		else if (result instanceof SetScope)
		    return toStringValue(visitor, ctx, ((SetScope) result).set(), format, indent, level);
		else if (result instanceof CollectionScope)
		    return format.extraSpace ? "{ }" : "{}";
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
			return Num.formatWithSeparators(((BigDecimal) result), format.separators, Integer.MIN_VALUE);
		    }
		    else if (MathUtil.isInteger(result)) {
			BigInteger iResult = convertToInteger(result, visitor.getSettings().mc, ctx);
			return Num.formatWithSeparators(iResult, format.separators);
		    }
		    else if (result instanceof BigFraction) {
			return ((BigFraction) result).toFormatString(format.separators, format.extraSpace);
		    }
		    else if (result instanceof ComplexNumber) {
			return ((ComplexNumber) result).toFormatString(format.separators, format.extraSpace);
		    }
		    else if (result instanceof Quaternion) {
			return ((Quaternion) result).toFormatString(format.separators, format.extraSpace);
		    }
		    else if (result instanceof ContinuedFraction) {
			return ((ContinuedFraction) result).toFormatString(format.separators, format.extraSpace);
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
	    Object obj = visitor.evaluateToValue(ctx, valueObj);

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
		return ((BigFraction) obj).precision();
	    if (obj instanceof ComplexNumber)
		return ((ComplexNumber) obj).precision();
	    if (obj instanceof Quaternion)
		return ((Quaternion) obj).precision();
	    // So, continued fractions are like lists, so "length" should be size
	    if (obj instanceof ContinuedFraction)
		return ((ContinuedFraction) obj).size();
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
	 * @param mc            Rounding mode and precision for conversion to decimal.
	 * @param ctx		The context to use for error reporting.
	 * @return		The scale of the object.
	 */
	public static int scale(final CalcObjectVisitor visitor, final Object valueObj, final MathContext mc, final ParserRuleContext ctx) {
	    Object obj = visitor.evaluateToValue(ctx, valueObj);

	    if (MathUtil.isInteger(obj))
		return convertToDecimal(obj, mc, ctx).scale();
	    if (obj instanceof BigDecimal)
		return ((BigDecimal) obj).scale();
	    if (obj instanceof BigFraction)
		return ((BigFraction) obj).toDecimal().scale();
	    if (obj instanceof ContinuedFraction)
		return ((ContinuedFraction) obj).toDecimal(mc).scale();
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
	 * Compare two sets. Turns out {@link Set#equals} returns true if the sets both contain
	 * the same elements, but could be in different "order". In our paradigms, set elements can be
	 * selected in order, so strict equality means they need to be in the same order. Normal set
	 * semantics say that order doesn't matter, as that is an implementation matter, but we use
	 * {@link LinkedHashSet} which does maintain order.
	 *
	 * @param visitor      Calculation visitor to do expression evaluations.
	 * @param ctx1         Context for first set.
	 * @param ctx2         Context for second set.
	 * @param s1           First set to be tested.
	 * @param s2           Set to be checked against the first.
	 * @param mc           MathContext for rouding during comparisons.
	 * @param strict       Whether the comparison should be "strict" ordering.
	 * @param allowNulls   Will nulls be allowed for either value.
	 * @param ignoreCase   Ignore case on the string compares?
	 * @param naturalOrder Use the natural order comparator?
	 * @param equality     Is this an equality test only?
	 * @return             {@code &lt; 0} if {@code s1} less than {@code s2}, {@code 0} if they are equal according
	 *                     to the parameters, or {@code &gt; 0} otherwise
	 */
	public static int setCompare(
		final CalcObjectVisitor visitor,
		final ParserRuleContext ctx1,
		final ParserRuleContext ctx2,
		final Set<?> s1,
		final Set<?> s2,
		final MathContext mc,
		final boolean strict,
		final boolean allowNulls,
		final boolean ignoreCase,
		final boolean naturalOrder,
		final boolean equality)
	{
	    int size1 = s1.size();
	    int size2 = s2.size();

	    // Set sizes must be the same, just to start with
	    if (size1 != size2)
		return Integer.signum(size1 - size2);

	    Object obj1, obj2;
	    int ret;

	    // Strict equality means the keys must be in the same order, but may be in different case
	    if (strict) {
		Iterator<?> iter1 = s1.iterator();
		Iterator<?> iter2 = s2.iterator();
		for (int i = 0; i < size1; i++) {
		    obj1 = iter1.next();
		    obj2 = iter2.next();
		    ret = compareValues(visitor, ctx1, ctx2, obj1, obj2, mc, strict, allowNulls, ignoreCase, naturalOrder, equality);
		    if (ret != 0)
			return ret;
		}
	    }
	    else {
		// Non-strict equality means the keys can be in any order, so we need to sort first
		List<Object> list1 = new ArrayList<>(s1);
		List<Object> list2 = new ArrayList<>(s2);
		sort(visitor, list1, ctx1, mc, ignoreCase, false);
		sort(visitor, list2, ctx2, mc, ignoreCase, false);

		for (int i = 0; i < size1; i++) {
		    obj1 = list1.get(i);
		    obj2 = list2.get(i);

		    ret = compareValues(visitor, ctx1, ctx2, obj1, obj2, mc, strict, allowNulls, ignoreCase, naturalOrder, equality);
		    if (ret != 0)
			return ret;
		}
	    }

	    // Depending on the parameters, no differences found
	    return 0;
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
	static class ObjectComparator implements Comparator<Object>
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
		final boolean ignoreCase, final boolean naturalOrder, final boolean equality)
	{
	    Object e1 = visitor.evaluateToValue(ctx1, obj1);
	    Object e2 = visitor.evaluateToValue(ctx2, obj2);

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
		// Okay ... here we *could have* a decimal that has no fraction compared to an integer
		// that by most reasonable metrics should be strictly equal and aren't just by class, so
		// do a bit more checking

		if (e1 instanceof BigDecimal && e2 instanceof BigInteger) {
		    BigDecimal d1 = (BigDecimal) e1;
		    if (d1.scale() <= 0)
			e1 = d1.toBigIntegerExact();
		}
		else if (e1 instanceof BigInteger && e2 instanceof BigDecimal) {
		    BigDecimal d2 = (BigDecimal) e2;
		    if (d2.scale() <= 0)
			e2 = d2.toBigIntegerExact();
		}

		if (!e1.getClass().equals(e2.getClass()))
		    return -1;
	    }

	    if (e1 instanceof String || e2 instanceof String) {
		StringFormat fmt = new StringFormat(false, visitor.getSettings());

		String s1 = toStringValue(visitor, ctx1, e1, fmt);
		String s2 = toStringValue(visitor, ctx2, e2, fmt);

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

		if (equality)
		    return c1.equals(c2) ? 0 : -1;

		return c1.compareTo(c2);
	    }
	    else if (e1 instanceof Quaternion || e2 instanceof Quaternion) {
		Quaternion q1 = Quaternion.valueOf(e1);
		Quaternion q2 = Quaternion.valueOf(e2);

		if (equality)
		    return q1.equals(q2) ? 0 : -1;

		return q1.compareTo(q2);
	    }
	    else if (e1 instanceof ContinuedFraction || e2 instanceof ContinuedFraction) {
		ContinuedFraction cf1 = ContinuedFraction.valueOf(e1);
		ContinuedFraction cf2 = ContinuedFraction.valueOf(e2);

		if (equality && strict)
		    return cf1.equals(cf2) ? 0 : -1;

		return cf1.compareTo(cf2);
	    }
	    else if (e1 instanceof BigDecimal || e2 instanceof BigDecimal) {
		BigDecimal d1 = toDecimalValue(visitor, e1, mc, ctx1);
		BigDecimal d2 = toDecimalValue(visitor, e2, mc, ctx2);

		return d1.compareTo(d2);
	    }
	    else if (e1 instanceof BigInteger || e2 instanceof BigInteger) {
		BigInteger i1 = convertToInteger(e1, mc, ctx1);
		BigInteger i2 = convertToInteger(e2, mc, ctx2);

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

		// First, compare the key sets
		Set<String> keySet1 = map1.keySet();
		Set<String> keySet2 = map2.keySet();
		int cmp = setCompare(visitor, ctx1, ctx2, keySet1, keySet2, mc, strict, allowNulls, ignoreCase, naturalOrder, equality);
		if (cmp != 0)
		    return cmp;

		// If the key sets are the same, then iterate through the values and compare them
		for (String key : keySet1) {
		    Object value1 = map1.getValue(key, ignoreCase);
		    Object value2 = map2.getValue(key, ignoreCase);

		    cmp = compareValues(visitor, ctx1, ctx2, value1, value2, mc, strict, allowNulls, false, naturalOrder, equality);
		    if (cmp != 0)
			return cmp;
		}

		// Finally, if no differences were found, the maps are the same
		return 0;
	    }
	    else if (e1 instanceof SetScope && e2 instanceof SetScope) {
		@SuppressWarnings("unchecked")
		SetScope<Object> set1 = (SetScope<Object>) e1;
		@SuppressWarnings("unchecked")
		SetScope<Object> set2 = (SetScope<Object>) e2;

		return setCompare(visitor, ctx1, ctx2, set1.set(), set2.set(), mc, strict, allowNulls, false, naturalOrder, equality);
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
		else if (CollectionScope.EMPTY.equals(obj2)) {
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
		else if (CollectionScope.EMPTY.equals(obj2)) {
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
		else if (CollectionScope.EMPTY.equals(obj2)) {
		    ;
		}
		else {
		    result.add(obj2);
		}

		ret = result;
	    }
	    else if (obj1 instanceof CollectionScope) {
		// This is an empty object, so convert to either a set or a map
		// depending on the value added to it
		if (obj2 instanceof SetScope) {
		    ret = obj2;
		}
		else if (obj2 instanceof ArrayScope) {
		    @SuppressWarnings("unchecked")
		    ArrayScope<Object> array = (ArrayScope<Object>) obj2;
		    ret = new SetScope<Object>(array.list());
		}
		else if (obj2 instanceof ObjectScope || CollectionScope.EMPTY.equals(obj2)) {
		    ret = obj2;
		}
		else {
		    // Single values means this should be a set
		    ret = new SetScope<Object>(obj2);
		}
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

	    Object v1 = visitor.evaluateToValue(ctx1, e1);
	    Object v2 = visitor.evaluateToValue(ctx2, e2);

	    try {
		// Concatenate objects if the first is an object
		if (v1 instanceof CollectionScope) {
		    return concatObjects(v1, v2, sortKeys);
		}

		// Do string concatenation if either expr is a string
		if (v1 instanceof String || v2 instanceof String) {
		    StringFormat fmt = new StringFormat(false, visitor.getSettings());

		    String s1 = v1 == null ? "" : toStringValue(visitor, ctx1, v1, fmt);
		    String s2 = v2 == null ? "" : toStringValue(visitor, ctx2, v2, fmt);

		    return s1 + s2;
		}

		// TODO: what to do with char?
		// could add char codepoint values, or concat strings

		nullCheck(v1, ctx1);
		nullCheck(v2, ctx2);

		// Otherwise, numeric values get added numerically
		if (rational || (v1 instanceof BigFraction || v2 instanceof BigFraction)) {
		    BigFraction f1 = convertToFraction(v1, ctx1);
		    BigFraction f2 = convertToFraction(v2, ctx2);

		    return f1.add(f2);
	        }
	        else if (v1 instanceof Quaternion || v2 instanceof Quaternion) {
		    Quaternion q1 = Quaternion.valueOf(v1);
		    Quaternion q2 = Quaternion.valueOf(v2);

		    return q1.add(q2);
		}
		else if (v1 instanceof ComplexNumber || v2 instanceof ComplexNumber) {
		    ComplexNumber c1 = ComplexNumber.valueOf(v1);
		    ComplexNumber c2 = ComplexNumber.valueOf(v2);

		    return c1.add(c2);
		}
		else if (v1 instanceof ContinuedFraction || v2 instanceof ContinuedFraction) {
		    ContinuedFraction cf1 = ContinuedFraction.valueOf(v1);
		    ContinuedFraction cf2 = ContinuedFraction.valueOf(v2);

		    return cf1.add(cf2);
		}
		else {
		    BigDecimal d1 = convertToDecimal(v1, mc, ctx1);
		    BigDecimal d2 = convertToDecimal(v2, mc, ctx2);

		    return fixupToInteger(d1.add(d2, mc));
		}
	    }
	    catch (ArithmeticException ae) {
		throw new CalcExprException(ae, ctx1);
	    }
	}

	/**
	 * Calculates the given bit-wise operation on the operands.
	 *
	 * @param visitor The calculation visitor.
	 * @param oper The desired bit-wise operation.
	 * @param o1   The LHS operand.
	 * @param o2   The RHS operand.
	 * @param ctx  The rule context (for error reporting).
	 * @param mc   Rounding mode and precision for the result.
	 * @return <code>o1 <i>op</i> o2</code>
	 */
	public static Object bitOp(final CalcObjectVisitor visitor, final String oper, final Object o1, final Object o2, final ParserRuleContext ctx, final MathContext mc) {
	    String op = oper.replace("=", "");

	    if (o1 instanceof CollectionScope && o2 instanceof CollectionScope) {
		SetScope<Object> s1 = SetScope.from((CollectionScope) o1);
		SetScope<Object> s2 = SetScope.from((CollectionScope) o2);
		SetScope<Object> result = null;

		switch (op) {
		    case "&":
		    case "\u2229":   // INTERSECTION
		    case "\u22C2":   // N-ARY INTERSECTION
			// set intersection
			result = s1.intersect(s2);
			break;
		    case "|":
		    case "\u222A":   // UNION
		    case "\u22C3":   // UN-ARY UNION
			// set union
			result = s1.union(s2);
			break;
		    case "^":
			// symmetric difference
			result = s1.symdiff(s2);
			break;
		    default:
			throw new UnknownOpException(oper, ctx);
		}

		return result;
	    }
	    else if (o1 instanceof Boolean && o2 instanceof Boolean) {
		boolean b1 = ((Boolean) o1).booleanValue();
		boolean b2 = ((Boolean) o2).booleanValue();
		boolean result;

		switch (op) {
		    case "&":
			result = b1 & b2;
			break;
		    case "~&":
		    case "\u22BC":
			result = !(b1 & b2);
			break;
		    case "&~":
			result = b1 & !b2;
			break;
		    case "^":
			result = b1 ^ b2;
			break;
		    case "~^":
			result = !(b1 ^ b2);
			break;
		    case "^~":
			result = b1 ^ !b2;
			break;
		    case "|":
			result = b1 | b2;
			break;
		    case "~|":
		    case "\u22BD":
			result = !(b1 | b2);
			break;
		    case "|~":
			result = b1 | !b2;
			break;
		    default:
			throw new UnknownOpException(oper, ctx);
		}

		return Boolean.valueOf(result);
	    }
	    else {
		BigInteger i1 = convertToInteger(o1, mc, ctx);
		BigInteger i2 = convertToInteger(o2, mc, ctx);
		BigInteger result;

		switch (op) {
		    case "&":
		    case "\u2229":   // INTERSECTION
		    case "\u22C2":   // N-ARY INTERSECTION
			result = i1.and(i2);
			break;
		    case "~&":
		    case "\u22BC":
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
		    case "^~":
			result = i1.xor(i2.not());
			break;
		    case "|":
		    case "\u222A":   // UNION
		    case "\u22C3":   // UN-ARY UNION
			result = i1.or(i2);
			break;
		    case "~|":
		    case "\u22BD":
			result = i1.or(i2).not();
			break;
		    case "|~":
			result = i1.or(i2.not());
			break;
		    default:
			throw new UnknownOpException(oper, ctx);
		}

		return result;
	    }
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
	 * Do a "power" operation (that is, {@code a**b}) where {@code a} can be arbitrary (numeric, not a data structure)
	 * type, and {@code b} will be a real (double) value. It's not realistic to work with exponents outside that range.
	 *
	 * @param visitor  The visitor object for evaluating expressions.
	 * @param baseExpr Expression node for the base value (for error reporting).
	 * @param value    Base value object.
	 * @param exp      The exponent value.
	 * @param settings Global settings for rounding, modes, etc.
	 * @return         Result of the power operation.
	 * @throws CalcExprException mostly for unimplemented things
	 */
	public static Object powerOp(final CalcObjectVisitor visitor, final ParserRuleContext baseExpr, final Object value, final BigDecimal exp, final Settings settings) {
	    boolean isIntPower = MathUtil.isInteger(exp);

	    Object result = null;

	    if (settings.rationalMode && isIntPower) {
		BigFraction f = toFractionValue(visitor, value, baseExpr);
		result = f.pow(exp.intValueExact());
	    }
	    else if (value instanceof Quaternion) {
		Quaternion base = (Quaternion) value;
		if (isIntPower) {
		    result = base.power(exp.intValueExact(), settings.mc);
		}
		else {
		    // TODO: temporary
		    throw new CalcExprException(baseExpr, "%calc#notImplemented", "quaternion to decimal power");
		}
	    }
	    else if (value instanceof ComplexNumber) {
		ComplexNumber base = (ComplexNumber) value;
		result = base.pow(exp, settings.mc);
	    }
	    else if (value instanceof ContinuedFraction) {
		ContinuedFraction base = (ContinuedFraction) value;
		if (isIntPower) {
		    result = base.power(exp.intValueExact());
		}
		else {
		    result = MathUtil.pow(base.toDecimal(settings.mc), exp, settings.mc);
		}
	    }
	    else if (MathUtil.isInteger(value)) {
		BigInteger iValue = convertToInteger(value, settings.mc, baseExpr);
		return MathUtil.pow(iValue, exp, settings.mc);
	    }
	    else {
		BigDecimal base = convertToDecimal(value, settings.mc, baseExpr);
		result = MathUtil.pow(base, exp, settings.mc);
	    }

	    return result;
	}


	/**
	 * Calculate one of the fixed "root" operations.
	 *
	 * @param visitor  Visitor to use for evaluation.
	 * @param op       The operation to perform.
	 * @param expr     Expression context for the operand.
	 * @param settings Precision settings.
	 * @return         The given root of the operand.
	 */
	public static Number rootOp(final CalcObjectVisitor visitor, final String op, final CalcParser.ExprContext expr, final Settings settings) {
	    Object value = visitor.evaluate(expr);
	    Number result = null;

	    switch (op) {
		case SQ_ROOT:
		    if (value instanceof Quaternion) {
			// TODO: temporary
			throw new CalcExprException(expr, "%calc#notImplemented", "square root of quaternion");
		    }
		    else if (value instanceof ComplexNumber) {
			result = ((ComplexNumber) value).sqrt(settings.mcDivide);
		    }
		    else {
			try {
			    result = MathUtil.sqrt2(convertToDecimal(value, settings.mc, expr), settings.mcDivide);
			}
			catch (IllegalArgumentException iae) {
			    throw new CalcExprException(iae, expr);
			}
		    }
		    break;

		case CB_ROOT:
		    if (value instanceof Quaternion) {
			// TODO: temporary
			throw new CalcExprException(expr, "%calc#notImplemented", "cube root of quaternion");
		    }
		    else if (value instanceof ComplexNumber) {
			ComplexNumber cValue = (ComplexNumber) value;
			MathContext mcPow = MathUtil.divideContext(cValue, settings.mcDivide);
			result = cValue.pow(BigDecimal.ONE.divide(D_THREE, mcPow), mcPow);
		    }
		    else {
			result = MathUtil.cbrt(convertToDecimal(value, settings.mc, expr), settings.mcDivide);
		    }
		    break;

		case FT_ROOT:
		    if (value instanceof Quaternion) {
			// TODO: temporary
			throw new CalcExprException(expr, "%calc#notImplemented", "fourth root of quaternion");
		    }
		    else if (value instanceof ComplexNumber) {
			result = ((ComplexNumber) value).pow(D_ONE_FOURTH, settings.mc);
		    }
		    else {
			try {
			    Number firstRoot = MathUtil.sqrt2(convertToDecimal(value, settings.mc, expr), settings.mcDivide);
			    if (firstRoot instanceof ComplexNumber) {
				result = ((ComplexNumber) firstRoot).sqrt(settings.mcDivide);
			    }
			    else {
				result = MathUtil.sqrt2((BigDecimal) firstRoot, settings.mcDivide);
			    }
			}
			catch (IllegalArgumentException iae) {
			    throw new CalcExprException(iae, expr);
			}
		    }
		    break;
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
	    if (MathUtil.isInteger(obj))
		return Typeof.INTEGER;
	    if (obj instanceof BigFraction)
		return Typeof.FRACTION;
	    if (obj instanceof ContinuedFraction)
		return Typeof.CFRACTION;
	    if (obj instanceof ComplexNumber)
		return Typeof.COMPLEX;
	    if (obj instanceof Quaternion)
		return Typeof.QUATERNION;
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
	 * Building on {@link typeof}, determine if the value is a scalar (e.g., not complex or quaternion, not map nor list).
	 *
	 * @param obj	An object to inspect.
	 * @return	Whether or not this object is a scalar value.
	 */
	public static boolean isScalar(final Object obj) {
	    switch (typeof(obj)) {
		case NULL:
		case INTEGER:
		case FLOAT:
		case FRACTION:
		case BOOLEAN:
		    return true;
	    }
	    return false;
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
		    castValue = convertToInteger(value, mc, ctx);
		    break;
		case FLOAT:
		    castValue = convertToDecimal(value, mc, ctx);
		    break;
		case FRACTION:
		    castValue = convertToFraction(value, ctx);
		    break;
		case CFRACTION:
		    if (value instanceof ArrayScope) {
			@SuppressWarnings("unchecked")
			ArrayScope<Object> array = (ArrayScope<Object>) value;
			List<BigInteger> values = new ArrayList<>();
			for (Object obj : array.list()) {
			    values.add((BigInteger) castTo(visitor, ctx, obj, Typeof.INTEGER, mc, separators, sortKeys));
			}
			castValue = new ContinuedFraction(values);
		    }
		    else {
			castValue = ContinuedFraction.valueOf(value);
		    }
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
		case QUATERNION:
		    throw new CalcExprException(ctx, "%calc#notImplemented", "Cast to quaternion");
		case BOOLEAN:
		    castValue = toBooleanValue(visitor, value, ctx);
		    break;
		case ARRAY:
		    if (value instanceof ComplexNumber) {
			ComplexNumber c = (ComplexNumber) value;
			castValue = new ArrayScope<Object>(c.toList());
		    }
		    else if (value instanceof SetScope) {
			@SuppressWarnings("unchecked")
			SetScope<Object> set = (SetScope<Object>) value;
			castValue = new ArrayScope<Object>(set.set());
		    }
		    else if (value instanceof ObjectScope) {
			ObjectScope obj = (ObjectScope) value;
			ArrayScope<Object> array = new ArrayScope<>(obj.size());
			for (String key : obj.keySet()) {
			    ObjectScope element = new ObjectScope(sortKeys);
			    element.setValue(key, obj.getValue(key, false));
			    array.add(element);
			}
			castValue = array;
		    }
		    else if (!(value instanceof ArrayScope)) {
			if (value instanceof CollectionScope) {
			    castValue = new ArrayScope<Object>();
			}
			else {
			    castValue = new ArrayScope<Object>(value);
			}
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
		    else if (value instanceof CollectionScope) {
			castValue = SetScope.from((CollectionScope) value);
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

	    return getRecursiveStringValue(visitor, variables, settings, ctx, rawValue);
	}

	private static String getRecursiveStringValue(
		final CalcObjectVisitor visitor,
		final NestedScope variables,
		final Settings settings,
		final ParserRuleContext ctx,
		final String rawValue)
	{
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
		    if (expr.indexOf("${") >= 0)
			expr = getRecursiveStringValue(visitor, variables, settings, ctx, expr);

		    Object exprValue = null;
		    boolean oldQuiet = Calc.setQuietMode(true);
		    try {
			exprValue = Calc.processString(expr);
		    }
		    finally {
			Calc.setQuietMode(oldQuiet);
		    }
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
	 * From the parser "member" context, extract just the proper member name.
	 *
	 * @param visitor The visitor used to calculate expressions.
	 * @param ctx     The "member" context.
	 * @return        Just the member name from the context.
	 */
	public static String getMemberName(final CalcObjectVisitor visitor, final CalcParser.MemberContext ctx) {
	    if (ctx != null) {
		CalcParser.IdContext idCtx = ctx.id();
		if (idCtx != null) {
		    return idCtx.getText();
		}
		TerminalNode string = ctx.STRING();
		if (string != null) {
		    return getRawString(string.getText());
		}
		string = ctx.ISTRING();
		if (string != null) {
		    return getIStringValue(visitor, string, ctx);
		}
	    }
	    return null;
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
		// Write out the current version as a "$requires" directive
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
			value = visitor.evaluateToValue(ctx, value);

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
			value = visitor.evaluateToValue(ctx, value);

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
			value = visitor.evaluateToValue(ctx, value);

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
	 * Negate (or posate) a value, which includes recursively applying the function
	 * to arrays, objects, and sets.
	 *
	 * @param expr     The expression context of the value.
	 * @param e        Value from evaluating that expression.
	 * @param negate   Whether to negate the value or save the sign.
	 * @param rational Rational mode, which dictates converting all to fractions.
	 * @param mc       Precision setting for conversion to decimal.
	 * @return         The resulting negated object.
	 */
	public static Object negate(final ParserRuleContext expr, final Object e, final boolean negate, final boolean rational, final MathContext mc) {
	    if (rational || e instanceof BigFraction) {
		BigFraction f = convertToFraction(e, expr);

		return negate ? f.negate() : f;
	    }
	    else if (e instanceof Quaternion) {
		Quaternion q = (Quaternion) e;

		return negate ? q.negate() : q;
	    }
	    else if (e instanceof ComplexNumber) {
		ComplexNumber c = (ComplexNumber) e;

		return negate ? c.negate() : c;
	    }
	    else if (e instanceof ContinuedFraction) {
		ContinuedFraction cf = (ContinuedFraction) e;

		return negate ? cf.negate() : cf;
	    }
	    else if (e instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<Object> array = (ArrayScope<Object>) e;
		ArrayScope<Object> result = new ArrayScope<>();
		for (int i = 0; i < array.size(); i++) {
		    Object v = array.getValue(i);
		    result.setValue(i, negate(expr, v, negate, rational, mc));
		}
		return result;
	    }
	    else if (e instanceof ObjectScope) {
		ObjectScope map = (ObjectScope) e;
		ObjectScope result = new ObjectScope();
		for (Map.Entry<String, Object> entry : map.map().entrySet()) {
		    result.setValue(entry.getKey(), negate(expr, entry.getValue(), negate, rational, mc));
		}
		return result;
	    }
	    else if (e instanceof SetScope) {
		@SuppressWarnings("unchecked")
		SetScope<Object> set = (SetScope<Object>) e;
		SetScope<Object> result = new SetScope<>();
		for (Object v : set.set()) {
		    result.add(negate(expr, v, negate, rational, mc));
		}
		return result;
	    }
	    else if (e instanceof CollectionScope) {
		return e;
	    }
	    else {
		BigDecimal d = convertToDecimal(e, mc, expr);

		// Interestingly, the "plus" operation can change the value, if the previous
		// value was not to the specified precision.
		return fixupToInteger(negate ? d.negate() : d.plus(mc));
	    }
	}

	/**
	 * Convert an object recursively to a number. Sets, arrays, and maps have their structures
	 * preserved, but all values are converted to numbers.
	 *
	 * @param ctx      The parsed expression node.
	 * @param value    Object value evaluated from the expression.
	 * @param rational Whether we're converting to fractions.
	 * @param mc       Precision setting for decimal conversions.
	 * @return         The object converted to a number.
	 */
	public static Object number(final ParserRuleContext ctx, final Object value, final boolean rational, final MathContext mc) {
	    if (rational) {
		return convertToFraction(value, ctx);
	    }
	    else if (value instanceof ComplexNumber || value instanceof Quaternion || value instanceof ContinuedFraction) {
		return value;
	    }
	    else if (value instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<Object> array = (ArrayScope<Object>) value;
		ArrayScope<Object> result = new ArrayScope<>();
		for (int i = 0; i < array.size(); i++) {
		    Object v = array.getValue(i);
		    result.setValue(i, number(ctx, v, rational, mc));
		}
		return result;
	    }
	    else if (value instanceof ObjectScope) {
		ObjectScope map = (ObjectScope) value;
		ObjectScope result = new ObjectScope();
		for (Map.Entry<String, Object> entry : map.map().entrySet()) {
		    result.setValue(entry.getKey(), number(ctx, entry.getValue(), rational, mc));
		}
		return result;
	    }
	    else if (value instanceof SetScope) {
		@SuppressWarnings("unchecked")
		SetScope<Object> set = (SetScope<Object>) value;
		SetScope<Object> result = new SetScope<>();
		for (Object v : set.set()) {
		    result.add(number(ctx, v, rational, mc));
		}
		return result;
	    }
	    else if (value instanceof CollectionScope) {
		return value;
	    }

	    return fixupToInteger(convertToDecimal(value, mc, ctx));
	}

	/**
	 * Do a "flat map" of values for the "sumof", "productof", and "exec" functions.  Since each
	 * value to be processed could be an array or map, we need to traverse these objects
	 * as well as the simple values in order to get the full list to process.
	 *
	 * @param visitor       Visitor used to evaluate expressions.
	 * @param ctx		The overall parser context for the function (for error messages).
	 * @param obj		The object to be added to the list, or recursed into when the object
	 *			is a list, map, or set.
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
	    Object value = visitor.evaluateToValue(ctx, obj);

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
		    case INTEGER:
			nullCheck(value, ctx);
			objectList.add(toIntegerValue(visitor, value, visitor.getSettings().mc, ctx));
			break;
		    case FRACTION:
			nullCheck(value, ctx);
			objectList.add(toFractionValue(visitor, value, ctx));
			break;
		    case CFRACTION:
			nullCheck(value, ctx);
			objectList.add(ContinuedFraction.valueOf(value));
			break;
		    case COMPLEX:
			nullCheck(value, ctx);
			objectList.add(ComplexNumber.valueOf(value));
			break;
		    case QUATERNION:
			nullCheck(value, ctx);
			objectList.add(Quaternion.valueOf(value));
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
		buildValueList(visitor, exprCtx, visitor.evaluateToValue(exprCtx), objects, conversion, 0);
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
			String pat = CharUtil.quoteRegEx(pattern.toString());
			if (scanner.findWithinHorizon(pat, pattern.length()) == null) {
			    throw new Intl.IllegalArgumentException("calc#scanPatternError", pattern.toString());
			}
			pattern.setLength(0);
		    }
		    if (i + 1 < format.length()) {
			Object value = null;
			char formatCh = format.charAt(++i);
			if (formatCh == 'c')
			    scanner.useDelimiter("");
			else if (i + 1 < format.length())
			    scanner.useDelimiter(Pattern.quote(format.substring(i + 1, i + 2)));
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
				    pattern.append("\\R");
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
		String pat = CharUtil.quoteRegEx(pattern.toString());
		if (scanner.findWithinHorizon(pat, pattern.length()) == null) {
		    throw new Intl.IllegalArgumentException("calc#scanPatternError", pattern.toString());
		}
	    }
	    return lastValue;
	}

	/**
	 * Load the "calcscripts.properties" file and cache it for future use.
	 *
	 * @return The cached properties for our utilities programs.
	 */
	public static Properties loadScriptProperties() {
	    if (calcScripts == null) {
		try (InputStream is = CalcUtil.class.getResourceAsStream(SCRIPT_PROPERTIES_FILE)) {
		    calcScripts = new Properties();
		    calcScripts.load(is);
		}
		catch (IOException ioe) {
		    return null;
		}
	    }

	    return calcScripts;
	}

	/**
	 * Search for a given object in the (presumably, and required) sorted list.
	 * <p> The list is presumed to be an {@link ArrayList} which has constant time
	 * access and thus is suitable for this algorithm.
	 *
	 * @param list  The already sorted list to search.
	 * @param obj   Object to search for in the list.
	 * @param begin Beginning index in the list (instead of 0).
	 * @param end   Ending index in the list.
	 * @param cmp   Comparator for the items in the list.
	 * @return      Zero or positive index into the list if the object was found,
	 *              or negative index where the object should be if not found.
	 */
	public static int binarySearch(final List<?> list, final Object obj, final int begin, final int end, final Comparator<Object> cmp) {
	    int b = begin, e = end;

	    while (b < e) {
		int mid = (b + e) / 2;
		int ret = cmp.compare(list.get(mid), obj);
		if (ret == 0)
		    return mid;
		if (ret < 0)
		    b = mid + 1;
		else
		    e = mid;
	    }
	    return (b > end) ? -(end + 1) : -(b + 1);
	}

	/**
	 * Compute the dot product of two vectors.
	 *
	 * @param visitor The visitor used to do all evaluations.
	 * @param v1      First operand.
	 * @param v2      Second operand.
	 * @param mc      Rounding mode and precision for the result.
	 * @param ctx     Parsing context for any error reporting.
	 * @return Dot product of the two vectors.
	 * @throws IllegalArgumentException if the vectors are not the same size.
	 */
	public static Object dotProduct(final CalcObjectVisitor visitor, final List<?> v1, final List<?> v2, final MathContext mc, final ParserRuleContext ctx) {
	    if (v1.size() != v2.size())
		throw new Intl.IllegalArgumentException("calc#vectorSizeMismatch", v1.size(), v2.size());

	    // For now just do a decimal calculation
	    BigDecimal result = BigDecimal.ZERO;
	    for (int i = 0; i < v1.size(); i++) {
		Object obj1 = v1.get(i);
		Object obj2 = v2.get(i);
		BigDecimal e1 = toDecimalValue(visitor, obj1, mc, ctx);
		BigDecimal e2 = toDecimalValue(visitor, obj2, mc, ctx);
		result = result.add(e1.multiply(e2));
	    }
	    return fixupToInteger(MathUtil.fixup(result, mc));
	}

}

