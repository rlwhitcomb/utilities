/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2022 Roger L. Whitcomb.
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
 *	27-Jun-2011 (rlwhitcomb)
 *	    Created.
 *	10-Aug-2011 (rlwhitcomb)
 *	    Added methods to add quotes to strings (paths)
 *	    with spaces.
 *	06-Sep-2011 (rlwhitcomb)
 *	    New methods to quote delimited identifiers and validate
 *	    DBMS identifiers.
 *	06-Sep-2011 (rlwhitcomb)
 *	    Trying to deal with mixed case identifiers also.
 *	15-Sep-2011
 *	    Added regularizeSpaces method to consolidate the regex pattern for
 *	    converting runs of spaces into a single space.
 *	02-Oct-2011 (rlwhitcomb)
 *	    Add utility routine to pad strings with spaces to a certain width.
 *	07-Nov-2011 (rlwhitcomb)
 *	    New method to undelimit identifiers.
 *	09-Nov-2011
 *	    Added rtrim method.
 *	11-Nov-2011
 *	    specialCharsPattern and delimitedIdentifierPattern were missing some characters:
 *	    Backslash (\) Caret (^) Braces ({}) Exclamation point (!) Left quote
 *	    (` X'60') and Tilde (~).  I also added the specialCharsPattern case for
 *	    identifiers that BEGIN WITH a digit, $, @, or #.  Also removed some
 *	    unnecessary escapes of metacharacters that are not interpreted as
 *	    metacharacters within the context of a character-class expression.
 *	    TODO: Identifiers that match SQL KEYWORDS must also be delimited.
 *	17-Nov-2011 (rlwhitcomb)
 *	    Add a special transform function to change an ENUM_FORMAT name to
 *	    a camelCase name.
 *	21-Dec-2011
 *	    Added windowsEscapeForCommandLine to deal with delimited identifiers in
 *	    arguments to utility processes when launching on Windows platforms.
 *	10-Jan-2012
 *	    Split the rtrim method into rtrim and delimRtrim, because the standard
 *	    delimited identifier handling (that normalizes an all-blank string to
 *	    a single blank) doesn't work in some contexts. In those cases, we want
 *	    handling that does NOT convert an all-blank string to a single space.
 *	    So the "rtrim" method now does simply that: trims all trailing spaces,
 *	    even if that yields an empty-string result.  The new "delimRtrim" method
 *	    provides full delimited identifier handling: trims trailing spaces, but
 *	    retains a single space if the input is all spaces.
 *	25-Jan-2012 (rlwhitcomb)
 *	    Add special method to transform a column name (which could be basically
 *	    any arbitrary string if it happens to be quoted in the select) into a
 *	    JSON compatible string.
 *	01-Mar-2012 (rlwhitcomb)
 *	    Special routine to "quote" a string that contains the given delimiter,
 *	    giving special consideration to delimiters themselves that are quotes.
 *	30-Apr-2012 (rlwhitcomb)
 *	    Add composite helper method to construct an "in" list of quoted strings
 *	    for use in a query.
 *	01-May-2012 (rlwhitcomb)
 *	    Deal with escaped quotes in "stripQuotes" also.
 *	01-May-2012 (rlwhitcomb)
 *	    Add utility method to get the UTF-8 bytes of a String.
 *	07-May-2012 (rlwhitcomb)
 *	    Add methods to correctly quote for CSV.
 *	11-May-2012 (rlwhitcomb)
 *	    Move the "substituteEnvValue" method from Logging into here so it
 *	    can be used by a broader clientele.
 *	15-May-2012 (rlwhitcomb)
 *	    Add method to deconstruct a CSV string into pieces.
 *	27-Aug-2012 (rlwhitcomb)
 *	    Add flavors of "makeStringList" using Object arrays; allow for nulls;
 *	    strange utility function to turn a Java-like name into regular words.
 *	30-Aug-2012 (rlwhitcomb)
 *	    Add the regular expression string from Double.valueOf to use with the
 *	    parser in recognizing floating-point values.
 *	04-Oct-2012 (rlwhitcomb)
 *	    Add a reserved word check to the list of things that would necessitate
 *	    delimiting a word (like a user or column name).
 *	07-Dec-2012 (rlwhitcomb)
 *	    Add the complement of getUtf8Bytes: getUtf8String.
 *	13-Dec-2012 (rlwhitcomb) SD 158242
 *	    Add null checks to these routines to deal with null inputs.
 *	18-Dec-2012 (rlwhitcomb)
 *	    Allow "undelimitIdentifier" to only need a leading or a trailing quote.
 *	23-Jan-2013 (rlwhitcomb)
 *	    Make the essential flavor of "padToWidth" do justification; add
 *	    flavors to pass the justification flag.
 *	24-Mar-2013 (rlwhitcomb)
 *	    Add "makeSimpleStringList" and fix a bug in it on empty string.
 *	16-May-2013 (rlwhitcomb)
 *	    Add method to convert Unicode literals to a real Java string.
 *	29-Jul-2013 (rlwhitcomb)
 *	    Also support the U&'\+000041' form for Unicode literals.
 *	17-Aug-2013 (rlwhitcomb)
 *	    Add method to count number of lines in a piece of text.
 *	17-Sep-2013
 *	    Added a helper method for attempting to build a valid identifier
 *	    from an arbitrary string.
 *	26-Sep-2013 (rlwhitcomb)
 *	    Add a method to parse a set representation like [a, b, c] into an
 *	    array of strings.
 *	07-Feb-2014 (rlwhitcomb)
 *	    Add new convenience method "getFirstFromCSV" to do what it's name implies.
 *	21-May-2014 (rlwhitcomb)
 *	    Add method to do the reverse of "makeStringList" to deconstruct a string into a list.
 *	16-Jun-2014 (rlwhitcomb)
 *	    New method to unescape a pattern string (to make it human readable).
 *	22-Aug-2014 (rlwhitcomb)
 *	    Method to turn an array into a name string (JSON compatible).
 *	06-Nov-2014 (rlwhitcomb)
 *	    Put error message strings into resource bundle.
 *	15-Dec-2014 (rlwhitcomb)
 *	    Code cleanup:  move "getListFromCSV" from NewTableDocument into here.
 *	06-Jan-2015 (rlwhitcomb)
 *	    Add method to capitalize the first letter of a string.
 *	08-Jan-2015 (rlwhitcomb)
 *	    Safely handle null input value in "delimitIdentifier".
 *	11-Feb-2015 (rlwhitcomb)
 *	    Add a new method to create a string of a particular character.
 *	26-Mar-2015 (rlwhitcomb)
 *	    Add an accessor for the UTF-8 charset, and get it from the
 *	    standard place.
 *	07-Apr-2015 (rlwhitcomb)
 *	    Add a method for determining a boolean value from a range of possible
 *	    (reasonable) values -- these are determined from uses in the config.dat.
 *	23-Apr-2015 (rlwhitcomb)
 *	    Really adhere to the contract for "getFromCSV" and return null at the end
 *	    for empty strings.  Skip leading and trailing whitespace in there.  Fix
 *	    "getListFromCSV" to allow null values for empty fields and only quit
 *	    when the string is empty.
 *	16-Jun-2015 (rlwhitcomb)
 *	    Add a utility function to test a string for null or "trim" is empty
 *	    (since this is an oft-used check).
 *	31-Aug-2015 (rlwhitcomb)
 *	    Cleanup Javadoc (things found by Java 8).
 *	22-Sep-2015 (rlwhitcomb)
 *	    Add a new flavor of "makeSimpleStringList" that takes an array as input.
 *	07-Jan-2016 (rlwhitcomb)
 *	    More Javadoc cleanup.
 *	25-Feb-2016 (rlwhitcomb)
 *	    Try to be a little more graceful in "getBooleanValue" so that during
 *	    Logging initialization we can still parse "true"/"false" even if the
 *	    Intl resources haven't been loaded yet.
 *	21-Apr-2016 (rlwhitcomb)
 *	    Make another version of "makeSimpleStringList" that accepts an arbitrary
 *	    separator character, and make the original one call the new one with comma.
 *	11-Aug-2016 (rlwhitcomb)
 *	    Add new methods to add double quotes to text and escape as per Java/C
 *	    conventions and methods to strip them off.
 *	22-Sep-2016 (rlwhitcomb)
 *	    Add a method to take a regular Java string (presumably decoded using
 *	    "convertUnicodeLiteral()") and encode it in the U&'xxx' form for
 *	    inserting into a SQL stream to work around charset constraints.
 *	26-Sep-2016 (rlwhitcomb)
 *	    Apparently (empirically) the N'xxx' form is a synonym for the U&'xxx'
 *	    form and supports the same escapes.  So, change the methods to reflect
 *	    this fact.  And add a new method to detect that a string can't be encoded
 *	    by the given charset and do the escapes in that case.
 *	04-Jan-2017 (rlwhitcomb)
 *	    Add a method to add space-separated words to an in-progress SQL string.
 *	08-Feb-2017 (rlwhitcomb)
 *	    Add a new flavor of arrayToNameString for map keys.
 *	22-Feb-2017 (rlwhitcomb)
 *	    Add additional capability to "substituteEnvValue" to support "System.getProperty"
 *	    values as well as "System.getenv".  Add metachars to support UPPER/lower casing
 *	    of either the first or all characters of the result.  Add a "lowercaseFirst" method
 *	    to support this (public).
 *	22-Feb-2017 (rlwhitcomb)
 *	    Rename the method to "substituteEnvValues".
 *	04-May-2017 (rlwhitcomb)
 *	    Redo the pattern matching for DBMS names along the lines of what was done for
 *	    Database names, that is, "a-zA-Z" becomes "\p{IsAlphabetic}" and "0-9" is
 *	    "\p{IsDigit}" to support full Japanese (or other non-Latin character sets).
 *	07-Jun-2017 (rlwhitcomb)
 *	    Add a "normalizeWhitespace" method to trim and change embedded newlines, etc.
 *	    to single spaces.
 *	13-Jun-2017 (rlwhitcomb)
 *	    Make the regular and delimited identifier patterns public so TokenType can use
 *	    them for Query language parsing.  That makes this module the definitive source!
 *	06-Jul-2017 (rlwhitcomb)
 *	    Change the signature for "isNullOrEmpty" to allow arbitrary Object values, and
 *	    then to test especially for String for the empty check.
 *	10-Jul-2017 (rlwhitcomb)
 *	    OMG!  The delimited identifier and special character regex strings were all messed
 *	    up as regex patterns (many of the special chars needed delimiting).
 *	    NOTE: the delimited identifier pattern does NOT include the double quote now, even
 *	    though the special chars pattern does include it because the parsing of delimited
 *	    identifiers doesn't work right if it is there.
 *	29-Aug-2017 (rlwhitcomb)
 *	    Adjust the unicode literal parsing to tolerate double backslash in N'' literals
 *	    and to correctly convert them for the DBMS.
 *	05-Sep-2017 (rlwhitcomb)
 *	    New method to compare strings, accounting for possible nulls.
 *	18-Sep-2017 (rlwhitcomb)
 *	    Add a new method for debugging string encoding problems (converts to hex array form).
 *	20-Sep-2017 (rlwhitcomb)
 *	    Add the "convertHexLiteral" method to convert to a string.
 *	25-Oct-2017 (rlwhitcomb)
 *	    New form of "toHexArrayForm" for use with an array of bytes.
 *	08-Jan-2018 (rlwhitcomb)
 *	    New method to quote/escape control characters in a string (for "vwload" options).
 *	10-Apr-2018 (rlwhitcomb)
 *	    Simplify a few methods.
 *	20-Apr-2018 (rlwhitcomb)
 *	    Tweak the logic and the doc in "normalizeWhitespace" a little bit to be clearer.
 *	17-May-2018 (rlwhitcomb)
 *	    Change the "makeSimpleStringList" methods to use more general Collection<String>
 *	    rather than List so we can use sets, or maps, etc.
 *	11-Jun-2018 (rlwhitcomb)
 *	    Tweak some Javadoc and other comments.
 *	20-Jun-2018 (rlwhitcomb)
 *	    Change the "arrayToNameString" for integers into "listToNameString".
 *	    Add a regex for long constants.
 *	09-Aug-2018 (rlwhitcomb)
 *	    Add support for octal, binary and big decimal constants.
 *	16-Aug-2018 (rlwhitcomb)
 *	    Genericize the methods to add and strip quotes and make outer methods for backquote.
 *	17-Aug-2018 (rlwhitcomb)
 *	    Fix the strip quotes method if there is only the one quote in the string.
 *	14-Feb-2019 (rlwhitcomb)
 *	    Enhance / fix the CENTER justification code to: a) accept negative length; and
 *	    b) correctly calculate the padding.
 *	17-Sep-2019 (rlwhitcomb)
 *	    Implement word wrap method for HELP VIEW, etc.
 *	31-Oct-2019 (rlwhitcomb)
 *	    Implement "isValidInteger" method.
 *	29-Jan-2020 (rlwhitcomb)
 *	    Add BigInteger ("bigint") support.
 *	13-Feb-2020 (rlwhitcomb)
 *	    New method to get a locale from an input locale spec (taken from ScriptRunner).
 *	20-Feb-2020 (rlwhitcomb)
 *	    Make the new "getLocale" more friendly by allowing null input to produce the default.
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	14-Apr-2020 (rlwhitcomb)
 *	    Another flavor of "padToWidth" that takes single char input and char padding.
 *	08-Oct-2020 (rlwhitcomb)
 *	    Add "matchesAnyOf" and "matchesAnyOfIgnoreCase" methods.
 *	04-Dec-2020 (rlwhitcomb)
 *	    Another flavor of quote stripping that works with either flavor of doubled
 *	    embedded quotes (such as for Calc).
 *	11-Dec-2020 (rlwhitcomb)
 *	    Tweak an exception message. Change the params to "stripAnyQuotes".
 *	21-Dec-2020 (rlwhitcomb)
 *	    Update obsolete Javadoc constructs.
 *	22-Jan-2021 (rlwhitcomb)
 *	    New method for making a list of file names.
 *	29-Jan-2021 (rlwhitcomb)
 *	    Use new Intl Exception variants for convenience.
 *	16-Feb-2021 (rlwhitcomb)
 *	    Redo "makeStringOfChars" more succinctly.
 *	01-Mar-2021 (rlwhitcomb)
 *	    Enhance "stripAllQuotes" to deal with all the values supported by CSV and Calc.
 *	    Add another method to also deal with other escape sequences ("\\" and "\\uXXXX").
 *	10-Mar-2021 (rlwhitcomb)
 *	    Make another flavor of "makeStringOfChars" that uses a StringBuilder.
 *	07-Apr-2021 (rlwhitcomb)
 *	    Add "ltrim" function and pattern; update trim patterns to include all chars below '\u0020'.
 *	07-May-2021 (rlwhitcomb)
 *	    Add "parseCommandLine" function to deal with quotes, etc. (hopefully the same way Java does it).
 *	07-Jul-2021 (rlwhitcomb)
 *	    Make class final and constructor private.
 *	09-Sep-2021 (rlwhitcomb)
 *	    Another form of "addQuotes".
 *	    Which needs tweaking for "handed" quotes.
 *	20-Sep-2021 (rlwhitcomb)
 *	    Fix Javadoc for "convertEscapeSequences" and add "\$" processing.
 *	26-Sep-2021 (rlwhitcomb)
 *	    The "\$" processing doesn't work well.
 *	21-Oct-2021 (rlwhitcomb)
 *	    #41: Remove "getLocale" in favor of better method in Intl ("getValidLocale").
 *	26-Oct-2021 (rlwhitcomb)
 *	    #31: Introduce octal and binary escape sequences.
 *	11-Nov-2021 (rlwhitcomb)
 *	    #53: Tweak both versions of "toHexArrayForm" to always output two digits per byte.
 *	20-Nov-2021 (rlwhitcomb)
 *	    #97: Change parameter type of "makeFileStringList".
 *	18-Dec-2021 (rlwhitcomb)
 *	    #148: Add handling of "\" in "quoteControl".
 *	19=Dec-2021 (rlwhitcomb)
 *	    #148: Fix handling of "\" with valid escape sequences in "quoteControl".
 *	22-Dec-2021 (rlwhitcomb)
 *	    Another version of "quoteControl" with control over the escape char. Don't do
 *	    any other fixups in there now.
 *	    #174: Fix "stripAnyQuotes" to make sure the string is actually quoted using the
 *	    "Quotes" class before stripping them. Make all parameters final.
 *	31-Dec-2021 (rlwhitcomb)
 *	    Fix missing "break" in "quoteControl" that was causing dup values.
 *	02-Jan-2022 (rlwhitcomb)
 *	    #192: New "countQuotes" method to detect embedded quotes.
 *	09-Jan-2022 (rlwhitcomb)
 *	    #200: Redo "quoteControl" and "convertEscapeSequences" to match escape sequences
 *	    in Calc grammar.
 *	19-Feb-2022 (rlwhitcomb)
 *	    #241: Alternate form of "substituteEnvValues". FIx bug if no ending "%".
 *	22-Feb-2022 (rlwhitcomb)
 *	    #254: "stringToLines" function, and "maxLength".
 *	27-Mar-2022 (rlwhitcomb)
 *	    Use UTF-8 charset from Constants, not our own.
 *	    #190: Support "caret" notation for control characters inside strings.
 *	17-Apr-2022 (rlwhitcomb)
 *	    #274: Add "isNumber" method.
 *	10-May-2022 (rlwhitcomb)
 *	    #317: Support "/" as a valid escape.
 *	17-May-2022 (rlwhitcomb)
 *	    #334: Add another flavor of "convertEscapeSequences" for Calc interpolated strings
 *	    that skips "$" expressions and variables. Move "findMatching" into here from CalcUtil.
 *	20-May-2022 (rlwhitcomb)
 *	    #334 (again): In "quoteControl" we have to double the escape char also... doh!
 *	29-Jun-2022 (rlwhitcomb)
 *	    #384: Make a new version of "getBooleanValue" with a flag whether to do the extended
 *	    string conversions (which are pretty risky in Calc, and actually unused in all other
 *	    current scenarios).
 */

package info.rlwhitcomb.util;

import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.rlwhitcomb.csv.Quotes;
import static info.rlwhitcomb.util.Constants.*;


/**
 * Various utility routines dealing with character strings.
 */
public final class CharUtil
{
	/** Pattern string for a regular DBMS identifier. */
	public static String regularIdentifier = "[_\\p{IsAlphabetic}][_\\p{IsAlphabetic}\\p{IsDigit}\\$@#]*";
	/** Pattern for a regular DBMS identifier. */
	private static Pattern regularIdentifierPattern = Pattern.compile(regularIdentifier);
	/** Pattern string for a delimited identifier. Note: does not include double quote here.... */
	public static String delimitedIdentifier = "\"[_\\p{IsAlphabetic}\\p{IsDigit}\\$@#\\&\\*:\\,\\=/\\<\\>\\(\\)\\-%\\.\\+\\?;' \\|\\\\\\^\\{\\}\\!`~]+\"";
	/** Pattern to recognize a delimited identifier. */
	private static Pattern delimitedIdentifierPattern = Pattern.compile(delimitedIdentifier);
	/** Pattern for an identifier that should be UPPER CASE only. */
	private static Pattern upperIdentifierPattern = Pattern.compile("[_\\p{Lu}][_\\p{Lu}\\p{IsDigit}\\$@#]*");
	/** Pattern to identify presence of special characters that require delimiting. Note: this DOES include the double quote.... */
	private static Pattern specialCharsPattern = Pattern.compile("[0-9\\$@#].*|.*[&\\*:\\,\"\\=/\\<\\>\\(\\)\\-%\\.\\+\\?;' \\|\\\\\\^\\{\\}\\!`~].*");
	/** Pattern to recognize runs of multiple spaces. */
	private static Pattern runsOfSpacesPattern = Pattern.compile("  +");
	/** Pattern to recognize trailing spaces. */
	private static Pattern rtrimPattern = Pattern.compile("[\u0000-\u0020]+$");
	/** Pattern to recognize leading spaces. */
	private static Pattern ltrimPattern = Pattern.compile("^[\u0000-\u0020]+");
	/** Pattern to recognize command-line arguments that need quoting (on Windows platforms). */
	private static Pattern winCmdArgNeedsQuotingPattern = Pattern.compile("[\\s\"\\\\]+");
	/** Pattern to recognize a string of zero or more backslashes preceding a doublequote,
	 *  OR, a string of one or more backslashes preceding the end of the string. */
	private static Pattern backslashesBeforeDoubleQuotePattern = Pattern.compile("\\\\*\"|\\\\+$");
	/** The regular expression string used to parse floating-point values according
	 * to the Java language specification.
	 */
	public static final String Digits     = "(\\p{Digit}+)";
	public static final String HexDigits  = "(\\p{XDigit}+)";
	public static final String LongConstant = Digits+"[lL]";
	public static final String BigIntConstant = Digits+"[nN]";
	public static final String HexConstant = "0[xX]"+HexDigits;
	public static final String OctalConstant = "0[1-7][0-7]*";
	public static final String BinaryConstant = "0[bB][0-1]+";
	// an exponent is 'e' or 'E' followed by an optionally
	// signed decimal integer.
	private static final String Exp        = "[eE][+-]?"+Digits;
	public static final String fpRegex    =
	    ("(" +
	     "NaN|" +           // "NaN" string
	     "Infinity|" +      // "Infinity" string

	     // A decimal floating-point string representing a finite positive
	     // number without a leading sign has at most five basic pieces:
	     // Digits . Digits ExponentPart FloatTypeSuffix
	     //
	     // Since this method allows integer-only strings as input
	     // in addition to strings of floating-point literals, the
	     // two sub-patterns below are simplifications of the grammar
	     // productions from the Java Language Specification, 2nd
	     // edition, section 3.10.2.

	     // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
	     "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?))"+

	     "[fFdDgG]?))");

	public static final String fpRegex2	=
	     // . Digits ExponentPart_opt FloatTypeSuffix_opt
	     ("(((\\.("+Digits+")("+Exp+")?)[fFdDgG]?))");

	public static final String fpRegex3	=
	     // Hexadecimal strings
	     ("((" +
	     // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
	     "(0[xX]" + HexDigits + "(\\.)?)|" +

	     // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
	     "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

	     ")[pP][+-]?" + Digits + ")" +
	     "[fFdD]?");

	public static final String SANITIZATION_PREFIX = "_";

	public static final char STD_ESCAPE_CHAR = '\\';

	/**
	* The characters we will allow when attempting to construct a valid identifier from an arbitrary string.
	*/
	private static String identifierConstructionCharacterSet ="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_";


	/**
	 * Enum to specify padding justification values.
	 */
	public static enum Justification
	{
		/** Text is justified on the left, with spaces to the right. */
		LEFT,
		/** Text is justified in the center; negative width puts odd leftovers on the right, otherwise on the left. */
		CENTER,
		/** Text is justified on the right, with spaces on the left. */
		RIGHT
	}


	/**
	 * Private constructor since this is a utility class.
	 */
	private CharUtil() {
	}


	/**
	 * Count the number of the given quote in the input string.
	 *
	 * @param input	The original input string.
	 * @param quote	The quote character to count.
	 * @return	Number of the given quote in the input string.
	 */
	public static int countQuotes(final String input, final char quote) {
	    int count = 0;
	    for (int i = 0; i < input.length(); i++) {
		if (input.charAt(i) == quote)
		    count++;
	    }
	    return count;
	}


	/**
	 * Deals with SQL-style strings, and strips the leading/trailing
	 * quotes and undoubles any embedded quotes.
	 * <p> For supporting the Unicode literal forms <code>U&amp;'<i>xxx</i>'</code> and <code>N'<i>...</i>'</code>
	 * we won't disturb anything that doesn't both start and end with
	 * a single quote.
	 *
	 * @param value	The original correctly quoted string.
	 * @return	The raw text of the string.
	 *
	 * @see	#addQuotes
	 */
	public static String stripQuotes(final String value) {
	    return internalStripQuotes(value, "'", "'", "''");
	}


	/**
	 * Deals with Java/C-style strings, and strips the leading/trailing
	 * quotes and unescapes any embedded quotes.
	 *
	 * @param value	The original correctly quoted string.
	 * @return	The raw text of the string.
	 *
	 * @see	#addDoubleQuotes
	 */
	public static String stripDoubleQuotes(final String value) {
	    return internalStripQuotes(value, "\"", "\"", "\\\"");
	}


	/**
	 * Deals with template-style strings, and strips the leading/trailing
	 * quotes and unescapes any embedded quotes.
	 *
	 * @param value	The original correctly quoted string.
	 * @return	The raw text of the string.
	 *
	 * @see	#addBackQuotes
	 */
	public static String stripBackQuotes(final String value) {
	    return internalStripQuotes(value, "`", "`", "\\`");
	}


	/**
	 * Strip any type of quote that has either embedded or escaped quotes within it
	 * depending on what the leading/trailing quote is.
	 * <p> Assume there is no whitespace outside the quotes.
	 *
	 * @param value   The original correctly quoted string.
	 * @param escaped Whether the embedded quotes are escaped (with "\") or doubled.
	 * @return	The raw text of the string with the doubled embedded quotes changed.
	 * @see Quotes for all the supported values
	 */
	public static String stripAnyQuotes(final String value, final boolean escaped) {
	    if (value == null || value.length() < 2)
		return value;

	    char quote = value.charAt(0);
	    char endQuote = value.charAt(value.length() - 1);

	    // If the quotes are not valid, then skip this whole process
	    Quotes q = Quotes.fromChar(quote, endQuote);
	    if (q == null)
		return value;

	    // Construct the embedded string to either doubled or escaped quotes
	    StringBuilder embedded = new StringBuilder(2);

	    if (escaped)
		embedded.append(STD_ESCAPE_CHAR);
	    else
		embedded.append(endQuote);
	    embedded.append(endQuote);

	    return internalStripQuotes(value, Character.toString(quote), Character.toString(endQuote), embedded.toString());
	}


	private static String internalStripQuotes(
		final String value,
		final String startQuote,
		final String endQuote,
		final String escapedQuote)
	{
	    // If the string doesn't start and end with a quote, then just return as-is
	    String trimmedValue = value.trim();
	    if (trimmedValue.length() < 2 || !trimmedValue.startsWith(startQuote) || !trimmedValue.endsWith(endQuote)) {
		return value;
	    }

	    StringBuilder buf = new StringBuilder(trimmedValue);
	    // Strip the leading/trailing quotes
	    buf.deleteCharAt(0);
	    buf.deleteCharAt(buf.length() - 1);

	    // Unescape any embedded quotes
	    int ix = buf.indexOf(escapedQuote);
	    while (ix >= 0) {
		buf.deleteCharAt(ix);
		ix = buf.indexOf(escapedQuote, ix + 1);
	    }

	    return buf.toString();
	}


	/**
	 * Deals with SQL-style strings, and adds leading/trailing quotes
	 * and doubles any embedded quotes.
	 *
	 * @param value	The raw text value.
	 * @return	The text correctly quoted and ready for SQL use.
	 *
	 * @see	#stripQuotes
	 */
	public static String addQuotes(final String value) {
	    StringBuilder buf = new StringBuilder();
	    addQuotes(value, buf);
	    return buf.toString();
	}


	/**
	 * Deals with SQL-style strings, and adds leading/trailing quotes
	 * and doubles any embedded quotes.
	 * <p> Uses the passed-in {@link StringBuilder} for use with
	 * larger strings (like queries).
	 * <p> Null values will result in nothing being added to the buffer.
	 *
	 * @param value	The raw text value to quote.
	 * @param buf	The buffer we're working in.
	 */
	public static void addQuotes(final String value, final StringBuilder buf) {
	    internalAddQuotes(value, "'", "'", '\'', buf);
	}


	/**
	 * Deals with Java/C-style strings, and adds leading/trailing double quotes
	 * and escapes any embedded double quotes.
	 *
	 * @param value	The raw text value.
	 * @return	The text correctly quoted and ready for use.
	 */
	public static String addDoubleQuotes(final String value) {
	    StringBuilder buf = new StringBuilder();
	    addDoubleQuotes(value, buf);
	    return buf.toString();
	}


	/**
	 * Deals with Java/C-style strings, and adds leading/trailing double quotes
	 * and escapes any embedded double quotes.
	 * <p> Uses the passed-in {@link StringBuilder} for use with
	 * larger strings (like queries).
	 * <p> Null values will result in nothing being added to the buffer.
	 *
	 * @param value	The raw text value to quote.
	 * @param buf	The buffer we're working in.
	 *
	 * @see	#stripDoubleQuotes
	 */
	public static void addDoubleQuotes(final String value, final StringBuilder buf) {
	    internalAddQuotes(value, "\"", "\"", STD_ESCAPE_CHAR, buf);
	}


	/**
	 * Deals with template-style strings, and adds leading/trailing back quotes
	 * and escapes any embedded back quotes.
	 *
	 * @param value	The raw text value.
	 * @return	The text correctly quoted and ready for use.
	 */
	public static String addBackQuotes(final String value) {
	    StringBuilder buf = new StringBuilder();
	    addBackQuotes(value, buf);
	    return buf.toString();
	}


	/**
	 * Deals with template-style strings, and adds leading/trailing back quotes
	 * and escapes any embedded back quotes.
	 * <p> Uses the passed-in {@link StringBuilder} for use with
	 * larger strings.
	 * <p> Null values will result in nothing being added to the buffer.
	 *
	 * @param value	The raw text value to quote.
	 * @param buf	The buffer we're working in.
	 *
	 * @see	#stripBackQuotes
	 */
	public static void addBackQuotes(final String value, final StringBuilder buf) {
	    internalAddQuotes(value, "`", "`", STD_ESCAPE_CHAR, buf);
	}


	/**
	 * Add any kind of quote, while escaping any embedded ones.
	 *
	 * @param value		The raw text value to quote.
	 * @param leftQuote	The left quote character to use.
	 * @param rightQuote	The right quote (often the same as the left).
	 * @return		The text correctly quoted and escaped.
	 */
	public static String addQuotes(final String value, final char leftQuote, final char rightQuote) {
	    StringBuilder buf = new StringBuilder();
	    internalAddQuotes(value, Character.toString(leftQuote), Character.toString(rightQuote), STD_ESCAPE_CHAR, buf);
	    return buf.toString();
	}


	private static void internalAddQuotes(
		final String value,
		final String leftQuote,
		final String rightQuote,
		final char escapeChar,
		final StringBuilder buf)
	{
	    if (value != null) {
		buf.append(leftQuote);
		int ix = buf.length();
		buf.append(value);
		int len = rightQuote.length();
		ix = buf.indexOf(rightQuote, ix);
		while (ix >= 0) {
		    buf.insert(ix, escapeChar);
		    ix = buf.indexOf(rightQuote, ix + len + 1);
		}
		buf.append(rightQuote);
	    }
	}


	/**
	 * Quote control characters in the string, using '\\' as the escape char.
	 *
	 * @param input The input string to process.
	 * @return The string with ISO control characters (usually \n or \r)
	 * escaped to the "\n" form.
	 */
	public static String quoteControl(final String input) {
	    return quoteControl(input, STD_ESCAPE_CHAR);
	}

	/**
	 * Quote control characters in the string, using the designated escape char.
	 *
	 * @param input      The input string to process.
	 * @param escapeChar The escape character to recognize and use.
	 * @return The string with ISO control characters (usually \n or \r)
	 * escaped to the "\n" form (with designated escape char).
	 */
	public static String quoteControl(final String input, final char escapeChar) {
	    if (input == null || input.isEmpty())
		return input;

	    StringBuilder buf = new StringBuilder(input.length() + 10);
	    for (int i = 0; i < input.length(); i++) {
		char ch = input.charAt(i);
		if (Character.isISOControl(ch)) {
		    switch (ch) {
			case '\b':
			    buf.append(escapeChar).append('b');
			    break;
			case '\f':
			    buf.append(escapeChar).append('f');
			    break;
			case '\n':
			    buf.append(escapeChar).append('n');
			    break;
			case '\r':
			    buf.append(escapeChar).append('r');
			    break;
			case '\t':
			    buf.append(escapeChar).append('t');
			    break;
			case '\0':
			    buf.append(escapeChar).append('0');
			    break;
			default:
			    if (ch <= '\u001A') {
				int caretCh = ch + ('A' - 1);
				buf.append(escapeChar).append('c');
				buf.appendCodePoint(caretCh);
			    }
			    else {
				buf.append(escapeChar).append('u');
				buf.append(String.format("%1$04x", (int) ch));
			    }
			    break;
		    }
		}
		else if (ch == escapeChar) {
		    // We have to double the actual escape character also, or this whole mess doesn't work
		    buf.append(ch).append(ch);
		}
		else {
		    buf.append(ch);
		}
	    }
	    return buf.toString();
	}


	/**
	 * Convert a Unicode literal (such as <code>U&amp;'<i>xxx</i>'</code> or
	 * <code>N'<i>xxx</i>'</code> to a real Java string, doing the hex literal conversion
	 * as we go.  Assumes the embedded single quotes are still
	 * doubled on input and will be single on output.
	 * <p> Also supports the <code>U&amp;'\+000041'</code> form.
	 *
	 * @param input	The Unicode literal.
	 * @return	A Java string with the Unicode escapes correctly interpreted
	 *		({@code null} input produces {@code null} output).
	 * @throws	IllegalArgumentException if a Unicode escape is improperly formatted.
	 */
	public static String convertUnicodeLiteral(final String input) {
	    int len = input == null ? 0 : input.length();
	    if (len < 3) {
		return input;
	    }

	    char ch0 = input.charAt(0);
	    char ch1 = input.charAt(1);
	    char ch2 = input.charAt(2);
	    char chn = input.charAt(len - 1);
	    if ((((ch0 == 'u' || ch0 == 'U') && ch1 == '&' && ch2 == '\'' && chn == '\'') ||
		 ((ch0 == 'n' || ch0 == 'N') && ch1 == '\'')) && chn == '\'') {
		// start == 3 means the U form(s) while start == 2 is the N form
		int start = (ch1 == '&' ? 3 : 2);
		StringBuilder buf = new StringBuilder(len - start);
		boolean sawQuote = false;
		for (int i = start; i < len - 1; i++) {
		    char ch = input.charAt(i);
		    if (ch == STD_ESCAPE_CHAR) {
			if (input.charAt(i + 1) == STD_ESCAPE_CHAR) {
			    buf.append("\\\\");
			    i++;
			}
			else {
			    int value = 0;
			    // If we get \+ then it is a 6-byte value
			    if (input.charAt(i + 1) == '+') {
				if (i >= len - 8)
				    throw new Intl.IllegalArgumentException("util#char.illegalUnicodeEscape", input.substring(i));
				for (int j = 1; j <= 6; j++) {
				    int cp = input.codePointAt(i + j + 1);
				    value *= 16;
				    if (cp >= '0' && cp <= '9')
					value += cp - '0';
				    else if (cp >= 'a' && cp <= 'f')
					value += (cp - 'a') + 10;
				    else if (cp >= 'A' && cp <= 'F')
					value += (cp - 'A') + 10;
				    else
					throw new Intl.IllegalArgumentException("util#char.illegalUnicodeEscape", input.substring(i));
				    // Check for values outside the (current) valid range
				    if (value > 0x10FFFF)
					throw new Intl.IllegalArgumentException("util#char.illegalUnicodeEscape", input.substring(i));
				}
				i += 7;
			    }
			    else {
				if (i >= len - 5)
				    throw new Intl.IllegalArgumentException("util#char.illegalUnicodeEscape", input.substring(i));
				for (int j = 1; j <= 4; j++) {
				    int cp = input.codePointAt(i + j);
				    value *= 16;
				    if (cp >= '0' && cp <= '9')
					value += cp - '0';
				    else if (cp >= 'a' && cp <= 'f')
					value += (cp - 'a') + 10;
				    else if (cp >= 'A' && cp <= 'F')
					value += (cp - 'A') + 10;
				    else
					throw new Intl.IllegalArgumentException("util#char.illegalUnicodeEscape", input.substring(i));
				}
				i += 4;
			    }
			    buf.appendCodePoint(value);
			}
		    }
		    else if (ch == '\'') {
			if (sawQuote)
			    buf.append('\'');
			sawQuote = !sawQuote;
		    }
		    else {
			buf.append(ch);
		    }
		}
		return buf.toString();
	    }
	    else {
		return input;
	    }
	}


	/**
	 * Convert a Unicode-encoded input string to a "U&amp;" type literal string with
	 * all the non-ASCII characters escaped.
	 * <p> This is necessary for SQL statements in certain situations where the DBMS
	 * doesn't support some characters, but you want to enter them here as
	 * straight Unicode.  In order for the parser to handle them correctly, they must
	 * be escaped in the SQL.
	 *
	 * @param input	A Java String value with possible Unicode codepoints.
	 * @return	A "U&amp;" formatted literal with all the non-ASCII escaped,
	 *		or {@code null} if the input is null.
	 */
	public static String escapeToUnicodeLiteral(final String input) {
	    if (input != null) {
		StringBuilder buf = new StringBuilder(input.length() * 4);
		buf.append("U&'");
		final int length = input.length();
		for (int i = 0; i < length; ) {
		    final int cp = input.codePointAt(i);
		    if (cp > 0x7E || cp < 0x20) {
			if (cp > 0xFFFF) {
			    // Use 6-byte escape form
			    buf.append(String.format("\\+%06X", cp));
			}
			else {
			    // Use 4-byte escape form
			    buf.append(String.format("\\%04X", cp));
			}
		    }
		    else if (cp == '\'') {
			buf.append("''");
		    }
		    else {
			buf.appendCodePoint(cp);
		    }
		    i += Character.charCount(cp);
		}
		buf.append("'");
		return buf.toString();
	    }
	    return input;
	}


	/**
	 * Convert a "hex literal" in the form <pre>"X'hhhhhh'"</pre> into a String
	 * value using the given charset.  If a charset is not given, use the platform default.
	 *
	 * @param input The entire hex literal value including the X'' surrounding it.
	 * @param charset The charset to use for conversion to a String, or {@code null}
	 * to use the platform default.
	 * @return The hex literal converted to a String according to the charset.
	 * @throws IllegalArgumentException if the string is malformed.
	 */
	public static String convertHexLiteral(final String input, final Charset charset) {
	    if (isNullOrEmpty(input))
		throw new Intl.IllegalArgumentException("util#char.illegalHexLiteral");
	    int len = input.length();
	    if (len < 3 || (len - 3) % 2 != 0)
		throw new Intl.IllegalArgumentException("util#char.illegalHexLiteral");
	    if (Character.toUpperCase(input.charAt(0)) != 'X' ||
		input.charAt(1) != '\'' ||
		input.charAt(len - 1) != '\'')
		throw new Intl.IllegalArgumentException("util#char.illegalHexLiteral");

	    String digits = input.substring(2, len - 1);
	    len = digits.length();
	    byte[] bytes = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
		bytes[i/2] = (byte)Short.parseShort(digits.substring(i, i + 2), 16);
	    }

	    if (charset == null)
		return new String(bytes);
	    else
		return new String(bytes, charset);
	}


	private static int parseCharEscape(
		final String input,
		final int index,
		final int base,
		final int normalLength,
		final StringBuilder output)
	{
	    StringBuilder charBuilder = new StringBuilder();
	    int pos = index;
	    int charValue;
	    char ch;

	    if (input.charAt(pos + 1) == '{') {
		pos++;
		while ((ch = input.charAt(++pos)) != '}') {
		    charBuilder.append(ch);
		}
	    }
	    else {
		for (int j = 0; j < normalLength; j++) {
		    if (++pos < input.length()) {
			charBuilder.append(input.charAt(pos));
		    }
		}
	    }

	    charValue = Integer.parseInt(charBuilder.toString(), base);
	    output.appendCodePoint(charValue);

	    return pos;
	}

	/**
	 * Find the matching end bracket/brace/paren/quote for the start character
	 * at the given position.
	 *
	 * @param value The char sequence to search.
	 * @param pos   Position of the starting character to match.
	 * @return      -1 if the end character is not found in the string, otherwise
	 *              the position of the ending character that matches the beginning.
	 */
	public static int findMatching(final CharSequence value, final int pos) {
	    char start = value.charAt(pos);
	    char end = start;

	    switch (start) {
		case '{':
		    end = '}';
		    break;
		case '(':
		    end = ')';
		    break;
		case '[':
		    end = ']';
		    break;
		case '`':
		case '"':
		case '\'':
		    break;
	    }

	    int depth = 0;
	    for (int ix = pos; ix < value.length(); ix++) {
		char ch = value.charAt(ix);
		if (ch == start) {
		    depth++;
		}
		else if (ch == end) {
		    if (--depth == 0)
			return ix;
		}
	    }

	    return -1;
	}

	/**
	 * Unescape the escape sequences in a string literal.
	 *
	 * @param input	    The input string with embedded escape sequences.
	 * @return	    The string with the embedded escape sequences converted to their
	 *		    literal character equivalents.
	 * @see #convertEscapeSequences(String, boolean)
	 */
	public static String convertEscapeSequences(final String input) {
	    return convertEscapeSequences(input, false);
	}

	/**
	 * Unescape the escape sequences in a string literal.
	 * <p> Deals with things like {@code \\} to {@code \} and {@code \\uXXXX} to
	 * its equivalent Unicode character.
	 * <p> There is no real error-checking in here; we assume that a parser at a
	 * higher level has determined the syntax here is correct. It is our job here
	 * simply to interpret the correct syntax into an unescaped form.
	 * <p> Handles either <code>&bsol;uXXXX</code> or <code>&bsol;u{XXXXXX}</code> Unicode escapes.
	 * <p> This should match the grammar in Calc.g4 (ESCAPES and UNICODE).
	 *
	 * @param input	    The input string with embedded escape sequences.
	 * @param skipExprs Whether to skip <code>${expr}</code> sequences.
	 * @return	    The string with the embedded escape sequences converted to their
	 *		    literal character equivalents.
	 */
	public static String convertEscapeSequences(final String input, final boolean skipExprs) {
	    if (input == null || input.isEmpty())
		return input;


	    StringBuilder buf = new StringBuilder(input.length());

	    for (int i = 0; i < input.length(); i++) {
		char ch = input.charAt(i);
		if (ch == STD_ESCAPE_CHAR) {
		    if (i + 1 < input.length()) {
			char ch2 = input.charAt(++i);
			switch (ch2) {
			    case STD_ESCAPE_CHAR:
				buf.append(ch2);
				break;
			    case 'b':
				buf.append('\b');
				break;
			    case 'f':
				buf.append('\f');
				break;
			    case 'n':
				buf.append('\n');
				break;
			    case 'r':
				buf.append('\r');
				break;
			    case 't':
				buf.append('\t');
				break;
			    case 'u':
				i = parseCharEscape(input, i, 16, 4, buf);
				break;
			    case 'o':
				i = parseCharEscape(input, i, 8, 3, buf);
				break;
			    case 'B':
				i = parseCharEscape(input, i, 2, 8, buf);
				break;
			    case '0':
				buf.append('\0');
				break;
			    case '/':
				buf.append(ch2);
				break;
			    case 'c':
				if (i + 1 < input.length()) {
				    char ch3 = input.charAt(++i);
				    if (ch3 >= 'a' && ch3 <= 'z')
					buf.appendCodePoint(ch3 - 'a' + 1);
				    else if (ch3 >= 'A' && ch3 <= 'Z')
					buf.appendCodePoint(ch3 - 'A' + 1);
				    else
					buf.append(ch).append(ch2).append(ch3);
				}
				else {
				    buf.append(ch).append(ch2);
				}
				break;
			    default:
				buf.append(ch).append(ch2);
				break;
			}
			continue;
		    }
		}
		else if (skipExprs) {
		    if (ch == '$' && i + 1 < input.length() && input.charAt(i + 1) == '{') {
			// For embedded "${...}" pieces, just copy them as-is
			// to the output, instead of interpreting escape sequences
			// within... (they will be parsed once this embedded
			// expression is itself executed)
			int e = CharUtil.findMatching(input, i + 1);
			buf.append(input, i, e + 1);
			i = e;
			continue;
		    }
		}
		// If nothing else special should happen, just copy input char to the output
		buf.append(ch);
	    }

	    return buf.toString();
	}


	private static Map<Charset, CharsetEncoder> encoderMap = new HashMap<>();

	/**
	 * Lookup a cached encoder for the given {@link Charset}.  This reduces the
	 * overhead of creating a new encoder for each call to {@link #checkAndEscapeString}
	 * since we will use it repeatedly for a given installation.
	 *
	 * @param charset	The character set whose encoder we need to get.
	 * @return		Either a new or cached encoder for this character set.
	 */
	public static CharsetEncoder getCharsetEncoder(final Charset charset) {
	    CharsetEncoder encoder = encoderMap.get(charset);
	    if (encoder == null) {
		encoder = charset.newEncoder();
		encoderMap.put(charset, encoder);
	    }
	    else {
		encoder.reset();
	    }
	    return encoder;
	}


	/**
	 * Check if the given Java string can be completely/correctly mapped
	 * with the given character set, and if NOT, convert it to a
	 * {@code U&'xxx'} Unicode literal with the "foreign" characters
	 * properly escaped.  The result string will be a properly formed
	 * DBMS string literal (that is, with leading and trailing single
	 * quotes and embedded single quotes doubled).
	 * <p> Note: the Unicode literal may have more things escaped than strictly
	 * necessary because it escapes anything outside of the US-ASCII range,
	 * rather than the "unencodable" values checked for in here.
	 * <p> Note: there is also a problem with backslash characters ("\") in that
	 * N'...' literals are processed in the DBMS differently than regular literals
	 * in this regard, so we need to distinguish them here.
	 * TODO: change that?  what is the performance impact?
	 *
	 * @param value		A Java string (with possible Unicode characters)
	 * @param charset	The target character set used to check for being encodable.
	 * @param fromUnicodeLiteral	If the input was a U&amp; or N form, then
	 *			special attention needs to be paid if there are
	 *			backslashes in the string, regardless of the
	 *			other tests in here.
	 * @return		A valid DBMS string literal that completely and
	 *			correctly represents the input value.
	 */
	public static String checkAndEscapeString(final String value, final Charset charset, final boolean fromUnicodeLiteral) {
	    if (value == null) {
		return null;
	    }
	    if (value.isEmpty()) {
		return "''";
	    }
	    boolean needsEscaping = false;
	    if (fromUnicodeLiteral && value.indexOf(STD_ESCAPE_CHAR) >= 0) {
		needsEscaping = true;
	    }
	    else {
		CharsetEncoder encoder = getCharsetEncoder(charset);
		needsEscaping = ! encoder.canEncode(value);
	    }
	    if (needsEscaping) {
		return escapeToUnicodeLiteral(value);
	    }
	    return addQuotes(value);
	}


	/**
	 * Double quote a string if it has spaces or parens or semicolons
	 * or commas (and some other things).
	 * <p> Mainly used for operating system arguments.
	 *
	 * @param input	The raw text.
	 * @return	The properly quoted text, or the original input
	 *		if nothing was found that required quoting.
	 */
	public static String doubleQuoteIfNeeded(final String input) {
	    if (input.indexOf(' ') >= 0 ||
		input.indexOf(',') >= 0 ||
		input.indexOf(';') >= 0 ||
		input.indexOf('(') >= 0 ||
		input.indexOf(')') >= 0) {
		StringBuilder buf = new StringBuilder(input);
		buf.insert(0, '"');
		buf.append('"');
		return buf.toString();
	    }
	    return input;
	}

	/**
	 * Delimit identifiers that need it.
	 * <p> TODO: deal with locale correctly for UPPER/lower case tests.
	 *
	 * @param	value	The identifier value that may or may not
	 *			need delimiting.
	 * @param	mixedcase	Flag to say that the database
	 *				supports mixed case (i.e., SQL-92 compliant)
	 * @return		Delimited result if necessary or the original.
	 */
	public static String delimitIdentifier(final String value, final boolean mixedcase) {
	    boolean needsDelimiting = false;
	    String strValue = value;

	    if (strValue != null) {
		int len = strValue.length();
		boolean isDelimited = len > 1 && strValue.charAt(0) == '"' && strValue.charAt(len-1) == '"';
		if (isDelimited) {
		    needsDelimiting = true;
		    strValue = strValue.substring(1, len - 1);
		}
		else {
		    // TODO: deal with proper charset in this casing test
		    if (mixedcase)
			needsDelimiting = !strValue.toUpperCase().equals(strValue);
		    if (!needsDelimiting) {
			Matcher m = specialCharsPattern.matcher(strValue);
			needsDelimiting = m.matches();
		    }
		}
	    }

	    if (needsDelimiting) {
		StringBuilder buf = new StringBuilder(strValue);
		// Double the embedded double quotes
		int ix = buf.indexOf("\"");
		while (ix >= 0) {
		    buf.insert(ix, '"');
		    ix = buf.indexOf("\"", ix + 2);
		}
		buf.insert(0, '"');
		buf.append('"');
		return buf.toString();
	    }

	    return value;
	}


	/**
	 * Undelimit an identifier, that is strip the quotes and
	 * undouble the embedded quotes.  This will work for only
	 * a leading or trailing quote as well.
	 *
	 * @param	input	The delimited identifier string.
	 * @return		The identifer in raw form (suitable for
	 *			input to {@link #delimitIdentifier}).
	 */
	public static String undelimitIdentifier(final String input) {
	    int len = input.length();
	    boolean leading = len > 1 && input.charAt(0) == '"';
	    boolean trailing = len > 1 && input.charAt(len-1) == '"';
	    if (leading || trailing) {
		StringBuilder buf = new StringBuilder(input);
		if (leading)
		    buf.deleteCharAt(0);
		if (trailing)
		    buf.deleteCharAt(buf.length()-1);
		int ix = buf.indexOf("\"\"");
		while (ix >= 0) {
		    buf.replace(ix, ix+2, "\"");
		    ix = buf.indexOf("\"\"", ix+1);
		}
		return buf.toString();
	    }
	    return input;
	}


	/**
	 * Transform an arbitrary string into a JSON compatible form.
	 * <p> Basically takes anything for which {@link Character#isJavaIdentifierPart}
	 * is not {@code true} and substitutes its hex representation.
	 *
	 * @param	input	The raw text.
	 * @return		The text with non-identifier characters replaced.
	 */
	public static String getJSONForm(final String input) {
	    StringBuilder buf = new StringBuilder();
	    for (int i = 0; i < input.length(); i++) {
		char ch = input.charAt(i);
		if (!Character.isJavaIdentifierPart(ch)) {
		    buf.append(Integer.toString((int)ch, 16));
		}
		else {
		    buf.append(ch);
		}
	    }
	    return buf.toString();
	}


	/**
	 * Pad a given string to a certain width with spaces at the end.
	 *
	 * @param	input	Input string (should always be less or equal
	 *			the given width).
	 * @param	width	The width to pad the string to.
	 * @return		The padded string.
	 */
	public static String padToWidth(final String input, final int width) {
	    return padToWidth(input, width, ' ');
	}


	/**
	 * Pad a given string to a certain width with spaces at the end using a given
	 * {@code StringBuilder}.
	 *
	 * @param	buf	The {@link StringBuilder} already in use.
	 * @param	input	Input string (should always be less or equal
	 *			the given width).
	 * @param	width	The width to pad the string to.
	 * @return		The original {@code StringBuilder}
	 */
	public static StringBuilder padToWidth(final StringBuilder buf, final String input, final int width) {
	    return padToWidth(buf, input, width, ' ', Justification.LEFT);
	}


	/**
	 * Pad a given string to a certain width with spaces with justification.
	 *
	 * @param	input	Input string (should always be less or equal
	 *			the given width).
	 * @param	width	The width to pad the string to.
	 * @param	just	The justification.
	 * @return		The padded string.
	 */
	public static String padToWidth(final String input, final int width, final Justification just) {
	    return padToWidth(input, width, ' ', just);
	}


	/**
	 * Pad a given string to a certain width with spaces with justification
	 * using a given {@code StringBuilder}
	 *
	 * @param	buf	The {@link StringBuilder} already in use.
	 * @param	input	Input string (should always be less or equal
	 *			the given width).
	 * @param	width	The width to pad the string to.
	 * @param	just	The justification.
	 * @return		The original {@code StringBuilder}.
	 */
	public static StringBuilder padToWidth(final StringBuilder buf, final String input, final int width, final Justification just) {
	    return padToWidth(buf, input, width, ' ', just);
	}


	/**
	 * Pad a given string to a certain width with the given character, adding
	 * the pad character at the end.
	 *
	 * @param	input	Input string (should always be less or equal
	 *			the given width).
	 * @param	width	The width to pad the string to.
	 * @param	pad	The padding character.
	 * @return		The padded string.
	 */
	public static String padToWidth(final String input, final int width, final char pad) {
	    return padToWidth(input, width, pad, Justification.LEFT);
	}


	/**
	 * Pad a given string to a certain width with the given character at the end using
	 * the given {@code StringBuilder}.
	 *
	 * @param	buf	The {@link StringBuilder} already in use.
	 * @param	input	Input string.
	 * @param	width	The width to pad the string to.
	 * @param	pad	The padding character.
	 * @return		The original {@code StringBuilder}
	 */
	public static StringBuilder padToWidth(final StringBuilder buf, final String input, final int width, final char pad) {
	    return padToWidth(buf, input, width, pad, Justification.LEFT);
	}


	/**
	 * Pad a given string to a certain width with the given character, either
	 * left-, right- or center-justified.
	 *
	 * @param	input	Input string (should always be less or equal
	 *			the given space.
	 * @param	width	The width to pad the string to.
	 * @param	pad	The padding character.
	 * @param	just	The justification.
	 * @return		The padded string.
	 */
	public static String padToWidth(final String input, final int width, final char pad, final Justification just) {
	    if (input.length() >= Math.abs(width))
		return input;
	    return padToWidth(new StringBuilder(), input, width, pad, just).toString();
	}


	/**
	 * Pad a given char to a certain width with padding char at the end.
	 *
	 * @param	input	Input character.
	 * @param	width	The width to pad the input to.
	 * @param	pad	The padding character.
	 * @param	just	The justification for the padding.
	 * @return		The padded string.
	 */
	public static String padToWidth(final char input, final int width, final char pad, final Justification just) {
	    return padToWidth(Character.toString(input), width, pad, just);
	}


	/**
	 * Pad a given string to a certain width with the given character, either
	 * left-, right- or center-justified using a given {@link StringBuilder}.
	 *
	 * @param	buf	The builder we're using (probably for a larger line).
	 * @param	input	Input string.
	 * @param	width	The width to pad the string to. For {@link Justification#CENTER}
	 *			this can be negative:  odd leftovers and negative width puts the extra
	 *			on the right, while positive width puts the odd leftovers on the left.
	 * @param	pad	The padding character.
	 * @param	just	The justification of the input string within the given width.
	 * @return		The original {@code StringBuilder}.
	 */
	public static StringBuilder padToWidth(
		final StringBuilder buf,
		final String input,
		final int width,
		final char pad,
		final Justification just)
	{
	    switch (just) {
		case LEFT:	// text on the left, pad on the right
		    buf.append(input);
		    for (int i = input.length(); i < width; i++) {
			buf.append(pad);
		    }
		    break;

		case CENTER:	// text in the center, pad on both left and right
		    int leftover = Math.abs(width) - input.length();
		    int left = (width < 0) ? leftover / 2 : (leftover + 1) / 2;
		    int right = leftover - left;
		    for (int i = 0; i < left; i++) {
			buf.append(pad);
		    }
		    buf.append(input);
		    for (int i = 0; i < right; i++) {
			buf.append(pad);
		    }
		    break;

		case RIGHT:	// pad on the left, text on the right
		    for (int i = input.length(); i < width; i++) {
			buf.append(pad);
		    }
		    buf.append(input);
		    break;
	    }
	    return buf;
	}


	/**
	 * Recognize a valid DBMS identifier.
	 * <p>TODO: deal with keywords and length restrictions.
	 *
	 * @param	value	The string to be validated.
	 * @param	uppercase	{@code true} if only UPPER CASE identifiers
	 *				should be allowed.
	 * @return		{@code true} if the string is a valid DBMS regular
	 *			identifier according to the case rules, or a valid
	 *			delimited identifier.
	 */
	public static boolean isValidIdentifier(final String value, final boolean uppercase) {
	    Matcher m;
	    int len = value.length();
	    if (len > 1 && value.charAt(0) == '"' && value.charAt(len-1) == '"') {
		m = delimitedIdentifierPattern.matcher(value);
	    }
	    else {
		// TODO: with keywords in these cases
		if (uppercase) {
		    m = upperIdentifierPattern.matcher(value);
		}
		else {
		    m = regularIdentifierPattern.matcher(value);
		}
	    }
	    return m.matches();
	}


	/**
	 * Is the input string a valid number? Relies on the system parsing plus
	 * the {@code '+'} sign.
	 *
	 * @param input	The input string to test.
	 * @return Whether or not the input string is a valid number.
	 */
	public static boolean isValidNumber(final String input) {
	    try {
		new BigDecimal(input);
		return true;
	    } catch (NumberFormatException nfe) {
		;
	    }
	    return false;
	}


	/**
	 * Trim and compress all runs of spaces into a single space.
	 * (This is used to preprocess various SQL clauses to simplifiy
	 * subsequent parsing.)
	 *
	 * @param	value	The string to be regularized.
	 *
	 * @return		Regularized result.
	 */
	public static String regularizeSpaces(final String value) {
	    return runsOfSpacesPattern.matcher(value.trim()).replaceAll(" ");
	}


	/**
	 * Trim trailing spaces from a string.  If the string consists of nothing
	 * but spaces, it is regularized to a single space.
	 *
	 * <p> This method is used to trim the RAW form of a delimited identifier (such
	 * as we would see in the query result from identifier columns in the catalogs).
	 * It follows the rules for delimited identifier regularization. Only trailing
	 * spaces are trimmed. If the identifier consists of nothing but spaces, it is
	 * regularized to a single space.
	 *
	 * <p> This method trims only spaces, not "whitespace".  According to SQL rules,
	 * space characters are the only legal whitespace that can be embedded within an
	 * identifier, so trimming whitespace might tend to obscure invalid values.
	 *
	 * @param	input	The string (in RAW form) to be trimmed (as an Object
	 *			because our query result values are always Object).
	 *
	 * @return		Trimmed result.
	 */
	public static String delimRtrim(final Object input) {
	    String result = rtrimPattern.matcher((String) input).replaceAll("");
	    if (result.length() == 0)
		result = " ";
	    return result;
	}


	/**
	 * Trim trailing spaces from a string.  Will return an empty string if
	 * the input is all spaces.
	 *
	 * @param	input	The string to be trimmed (as an Object because
	 *			our query result values are always Object).
	 *
	 * @return		Trimmed result.
	 */
	public static String rtrim(final Object input) {
	    return rtrimPattern.matcher((String) input).replaceAll("");
	}


	/**
	 * Trim leading spaces from a string.  Will return an empty string if
	 * the input is all spaces.
	 *
	 * @param	input	The string to be trimmed (as an Object because
	 *			our query result values are always Object).
	 *
	 * @return		Trimmed result.
	 */
	public static String ltrim(final Object input) {
	    return ltrimPattern.matcher((String) input).replaceAll("");
	}


	/**
	 * Transform an ENUM_FORMAT name into a camelCase name for use with property
	 * enums.
	 *
	 * @param	enumName	An all UPPER CASE name with possibly embedded
	 *				underscores.
	 * @return			A camelCase version with the underscores removed
	 *				and the characters after the underscores UpperCased.
	 */
	public static String toCamelCase(final String enumName) {
	    StringBuilder buf = new StringBuilder(enumName.toLowerCase());
	    int ix;
	    while ((ix = buf.indexOf("_")) >= 0) {
		buf.deleteCharAt(ix);
		if (ix < buf.length()) {
		    buf.setCharAt(ix, Character.toUpperCase(buf.charAt(ix)));
		}
	    }
	    return buf.toString();
	}


	/**
	 * Escapes a command-line argument for a Windows platform.
	 *
	 * This method adds whatever quoting and/or escapements are necessary to ensure
	 * that the launched utility executable will see an argv[i] string that is exactly
	 * the same as the arg string input here.
	 *
	 * <p> The rules for parsing a command line on Windows were obtained from the
	 * Microsoft C Runtime Library source code (stdargv.c):
	 * <pre>
	 *      2N backslashes + " ==&gt; N backslashes and begin/end quote
	 *      2N+1 backslashes + " ==&gt; N backslashes + literal "
	 *      N backslashes ==&gt; N backslashes
	 *      "" within a quoted string ==&gt; single literal "
	 * </pre>
	 *
	 * <p> So, somewhat surprisingly, embedded backslashes need not be escaped unless
	 * they are immediately followed by a doublequote (or end-of-string, since we will
	 * be adding our own trailing doublequote when we enclose the entire argument).
	 *
	 * @param	arg	One command argument string.
	 * @return		That argument string escaped to survive command-line parsing.
	 */
	public static String windowsEscapeForCmdLine(final String arg) {
	    String value = arg;
	    //
	    // If there are no doubleqoutes, backslashes, or whitespace, our argument
	    // will pass through the CRTL parsing (and ProcessBuilder) unchanged, so we
	    // don't even need to add our own doublequotes.
	    //
	    Matcher m = winCmdArgNeedsQuotingPattern.matcher(value);
	    if (m.find()) {
		// We need to doublequote the entire argument (which prevents ProcessBuilder
		// from doing that), and escape any embedded doublequotes, and ensure that
		// existing embedded backslashes survive the argv parsing.
		StringBuffer sb = new StringBuffer();
		m = backslashesBeforeDoubleQuotePattern.matcher(value);
		while (m.find()) {
		    if (!m.hitEnd()) {
			// We found a doublequote, preceded by N (possibly zero) backslashes.
			// Double the number of backslashes, and add one, in front of the
			// doublequote.  (Adding one backslash ensures that the doublequote
			// will be treated as literal, and not the end of our quoted string.)
			// This inverts the rule:
			//     2N+1 backslashes + " ==> N backslashes + literal "
			// and ensures that any embedded doublequotes (even consecutive ones)
			// are passed through as literal.
			String backslashes = m.group().substring(0, m.group().length() - 1);
			String replacement = String.format("%1$s%1$s\\\"", backslashes);
			m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
		    }
		    else {
			// We found a group of trailing backslashes.  Must double them
			// now because we will be appending our own doublequote later.
			// (Don't add one more backslash, because we want our trailing
			// doublequote to end the quoted string and not be literal.)
			// This inverts the rule:
			//     2N backslashes + " ==> N backslashes and begin/end quote
			String replacement = String.format("%1$s%1$s", m.group());
			m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
		    }
		}
		m.appendTail(sb);

		// Now surround the whole thing with our own doublequotes
		// (which will be stripped by the CRTL).
		value = sb.insert(0, '"').append('"').toString();
	    }

	    return value;
	}


	/**
	 * Quote a value that contains the given delimiter.
	 * <p> Pay special attention to delimiters themselves that are quotes
	 * and use the opposite one for quoting.
	 *
	 * @param	value		The raw text value.
	 * @param	delimiter	The delimiter that could be found within
	 *				the raw text.
	 * @return			The input text if the delimiter is not
	 *				found, or the text quoted by the "opposite"
	 *				kind of quote if it is found.
	 */
	public static String quoteValue(final String value, final char delimiter) {
	    if (value.indexOf(delimiter) >= 0) {
		char quoteChar;
		switch (delimiter) {
		    case '\'':
			quoteChar = '\"';
			break;
		    case '\"':
			quoteChar = '\'';
			break;
		    default:
			quoteChar = '"';
			break;
		}
		StringBuilder buf = new StringBuilder(value.length() + 2);
		buf.append(quoteChar);
		buf.append(value);
		buf.append(quoteChar);
		return buf.toString();
	    }
	    return value;
	}


	/**
	 * Construct a parenthesized list of quoted strings for use in queries.
	 *
	 * @param	values	Array of values to put into the list.
	 *
	 * @return	String consisting of "(" followed by the values in
	 *		single quotes and separated by commas, then ")".
	 */
	public static String makeStringList(final String[] values) {
	    StringBuilder buf = new StringBuilder();
	    makeStringList(values, buf);
	    return buf.toString();
	}


	/**
	 * Construct a parenthesized list of quoted strings for use in queries.
	 * <p> Uses an existing {@link StringBuilder} in the (common) case
	 * where we're building a larger query using this list.
	 *
	 * @param	values	Array of values to insert as a parenthesized list.
	 * @param	buf	The existing {@link StringBuilder} to append to.
	 */
	public static void makeStringList(final String[] values, final StringBuilder buf) {
	    buf.append('(');
	    for (String value : values) {
		addQuotes(value, buf);
		buf.append(',');
	    }
	    buf.setCharAt(buf.length() - 1, ')');
	}


	/**
	 * Construct a parenthesized list of quoted strings for use in queries.
	 *
	 * @param	values	Array of arbitrary values to put into the list.
	 *
	 * @return	String consisting of "(" followed by the values in
	 *		single quotes and separated by commas, then ")".
	 */
	public static String makeStringList(final Object[] values) {
	    StringBuilder buf = new StringBuilder();
	    makeStringList(values, buf);
	    return buf.toString();
	}


	/**
	 * Construct a parenthesized list of quoted strings for use in queries.
	 * <p> Uses an existing {@link StringBuilder} in the (common) case
	 * where we're building a larger query using this list.
	 *
	 * @param	values	Array of arbitrary values to insert.
	 * @param	buf	The existing {@link StringBuilder} to append to.
	 */
	public static void makeStringList(final Object[] values, final StringBuilder buf) {
	    buf.append('(');
	    for (Object value : values) {
		addQuotes(value == null ? null : value.toString(), buf);
		buf.append(',');
	    }
	    buf.setCharAt(buf.length() - 1, ')');
	}


	/**
	 * Another flavor of list making:  make a parenthesized list, into an
	 * existing buffer, separated by commas.
	 *
	 * @param	values	The list of values.
	 * @param	buf	The buffer we're appending this list to.
	 */
	public static void addStringList(final List<?> values, final StringBuilder buf) {
	    buf.append('(');
	    for (Object value : values) {
		if (value != null) {
		    buf.append(value.toString());
		}
		buf.append(',');
	    }
	    buf.setCharAt(buf.length() - 1, ')');
	}


	/**
	 * Get a simple string list (comma separated) from the array of strings.
	 *
	 * @param	values	Array of strings
	 * @return		Comma-separated list as one string
	 */
	public static String makeSimpleStringList(final String[] values) {
	    return makeSimpleStringList(Arrays.asList(values));
	}

	/**
	 * Get a simple string list (custom separator) from the array of strings.
	 *
	 * @param	values	Array of strings
	 * @param	separator	The character to use to separate the values.
	 * @return		The list as one string.
	 */
	public static String makeSimpleStringList(final String[] values, final char separator) {
	    return makeSimpleStringList(Arrays.asList(values), separator);
	}

	/**
	 * Get a simple string list (comma separated) from the collection of strings.
	 *
	 * @param	values	{@link Collection} of string values
	 * @return		Comma-separated list as one string
	 */
	public static String makeSimpleStringList(final Collection<String> values) {
	    return makeSimpleStringList(values, ',');
	}

	/**
	 * Get a simple string list (with arbitrary separator) from the collection of strings.
	 *
	 * @param	values	{@link Collection} of string values.
	 * @param	separator	The character to use to separate the values.
	 * @return		The list as one string.
	 */
	public static String makeSimpleStringList(final Collection<String> values, final char separator) {
	    StringBuilder buf = new StringBuilder();
	    for (String value : values) {
		if (buf.length() > 0) {
		    buf.append(separator);
		}
		buf.append(value);
	    }
	    return buf.toString();
	}


	/**
	 * Make a list of file names (only), suitable for display.
	 *
	 * @param files	The list of objects (which resolve to file names).
	 * @return	A string that looks like: {@code [ f1, f2, ... ]}
	 */
	public static String makeFileStringList(final List<?> files) {
	    List<String> names = new ArrayList<>(files.size());
	    for (Object file : files) {
		File f = new File(file.toString());
		String nameOnly = f.getName();
		int dotPos = nameOnly.lastIndexOf('.');
		if (dotPos < 0)
		    names.add(nameOnly);
		else
		    names.add(nameOnly.substring(0, dotPos));
	    }
	    if (names.size() <= 1)
		return makeSimpleStringList(names);
	    else
		return String.format("[ %1$s ]", makeSimpleStringList(names));
	}


	/**
	 * Get a list of values from a single string (comma, space separated).
	 *
	 * @param	value	Input comma-separated list.
	 * @return		The list of separate values (or {@code null}
	 *			if the input string is {@code null} or empty).
	 */
	public static String[] getListFromString(final String value) {
	    if (value != null && !value.isEmpty()) {
		return value.trim().split("\\,\\s*");
	    }
	    return null;
	}


	/**
	 * Gets the UTF-8 bytes of a {@link String}.
	 *
	 * @param	s	The input string to deconstruct.
	 * @return		The constituent bytes encoded as UTF-8
	 *			(or {@code null} if the input is).
	 */
	public static byte[] getUtf8Bytes(final String s) {
	    return s == null ? null : s.getBytes(UTF_8_CHARSET);
	}


	/**
	 * Gets a string from the UTF-8 bytes.
	 *
	 * @param	bytes	The string of UTF-8 encoded bytes.
	 * @return		The Java string that corresponds (or
	 *			{@code null} if the input is {@code null}).
	 */
	public static String getUtf8String(final byte[] bytes) {
	    return bytes == null ? null : new String(bytes, UTF_8_CHARSET);
	}


	/**
	 * Quote for CSV into a larger {@link StringBuilder}.
	 *
	 * @param	input	The raw text to be added to a CSV record (can
	 *			be {@code null} or empty).
	 * @param	buf	The buffer where we're building the record.
	 */
	public static void quoteForCSV(final String input, final StringBuilder buf) {
	    if (input != null && !input.isEmpty()) {
		if (input.indexOf("\"") >= 0 ||
		    input.indexOf(",") >= 0 ||
		    input.indexOf("\n") >= 0 ||
		    input.indexOf("\r") >= 0 ||
		    input.startsWith(" ") ||
		    input.endsWith(" "))
		{
		    buf.append('"');
		    int ix = buf.length();
		    buf.append(input);
		    ix = buf.indexOf("\"", ix);
		    while (ix >= 0) {
			buf.insert(ix, '"');
			ix = buf.indexOf("\"", ix + 2);
		    }
		    buf.append('"');
		}
		else {
		    buf.append(input);
		}
	    }
	}


	/**
	 * Append a value to a CSV string. The value (properly quoted) is
	 * appended, followed by a comma. So, at the end of the record, the
	 * last trailing comma should be removed.
	 *
	 * @param	input	The raw value to append to the CSV record.
	 * @param	buf	The record buffer.
	 */
	public static void appendToCSV(final String input, final StringBuilder buf) {
	    quoteForCSV(input, buf);
	    buf.append(',');
	}


	/**
	 * Get the next value from a CSV string.
	 * <p> Assumes the input has been constructed according to
	 * the {@link #quoteForCSV} rules.
	 * <p> Empty strings produce {@code null} results.
	 * <p> The returned string is removed from the input sequence
	 * as is the trailing comma (if any) so the next call to read a field
	 * will work correctly.
	 * <p> Note: a trailing empty field will be indistinguishable from
	 * the end of string.
	 * <p> Leading and trailing whitspace is tossed away.
	 * <p> Major note:  This is not suitable for regular CSV parsing in that
	 * it makes no distinction at the end of string between trailing empty
	 * fields (as in ",,,") and the real end of string.  This is "okay" in
	 * the places we use it, but not in general, which is why there is a
	 * whole "info.rlwhitcomb.csv" package that works better in this regard.
	 *
	 * @param	buf	The CSV record we're tearing apart.
	 * @return Next field value or {@code null} for an empty field
	 * or the end of string.
	 */
	public static String getFromCSV(final StringBuilder buf) {
	    String result = null;
	    // Skip leading whitespace
	    int ix = 0;
	    while (ix < buf.length() && Character.isWhitespace(buf.charAt(ix))) {
		ix++;
	    }
	    if (ix > 0) {
		buf.delete(0, ix);
		ix = 0;
	    }
	    if (buf.length() == 0)
		return result;

	    if (buf.charAt(0) == '"') {
		buf.deleteCharAt(0);
		while (ix < buf.length()) {
		    if (buf.charAt(ix) == '"') {
			if (ix + 1 < buf.length() && buf.charAt(ix + 1) == '"')
			    buf.deleteCharAt(ix);
			else
			    break;
		    }
		    ix++;
		}
		result = buf.substring(0, ix);
		// If we ran off the end of the string without seeing the trailing quote
		// then don't advance the end pointer here
		if (ix < buf.length())
		    ix++;
	    }
	    else {
		// The next non-quoted value ends at optional whitespace followed by comma
		char ch;
		while (ix < buf.length()) {
		    if ((ch = buf.charAt(ix)) == ',')
			break;
		    if (Character.isWhitespace(ch)) {
			// Skip whitespace and check if followed by end of string or comma
			while (ix < buf.length() && Character.isWhitespace(buf.charAt(ix)))
			    ix++;
			if (ix >= buf.length() || buf.charAt(ix) == ',')
			    break;
			continue;
		    }
		    ix++;
		}
		result = buf.substring(0, ix).trim();
	    }
	    // Skip trailing whitespace until the next comma or end of string
	    while (ix < buf.length() && Character.isWhitespace(buf.charAt(ix))) {
		ix++;
	    }
	    if (ix < buf.length() && buf.charAt(ix) == ',') {
		ix++;
	    }
	    buf.delete(0, ix);
	    return result.length() == 0 ? null : result;
	}


	/**
	 * Gets the first CSV token from the given string.
	 * @param	input	The complete CSV record.
	 * @return	{@code null} if input is {@code null}, else
	 *		the first CSV-delimited token from the input
	 *		(if any, otherwise {@code null}).
	 * @see	#getFromCSV
	 */
	public static String getFirstFromCSV(final String input) {
	    if (input == null)
		return input;
	    StringBuilder buf = new StringBuilder(input);
	    return getFromCSV(buf);
	}


	/**
	* Return a list from a CSV string.
	* <p> Note: empty fields will generate a {@code null} entry in the returned list.
	*
	* @param csv The string in CSV format.
	* @return The list or {@code null} if the input is {@code null} or empty.
	* @see	#getFromCSV
	*/
	public static List<String> getListFromCSV(final String csv) {
	    if (csv == null || csv.trim().isEmpty()){
		return null;
	    }

	    StringBuilder buf = new StringBuilder(csv);
	    List<String> result = new ArrayList<String>();

	    do {
		result.add(getFromCSV(buf));
	    } while (buf.length() != 0);

	    return result;
	}


	/**
	 * Substitute any environment variables found in the string. Alternate form without the
	 * secondary symbol lookup table.
	 *
	 * @param   input   Input string needing its substitution tokens replaced from the
	 *		    environment	(or the	alternate symbol map).
	 * @return	    The original string with the variable substitutions made.
	 * @see #substituteEnvValues(String, Map)
	 */
	public static String substituteEnvValues(final String input) {
	    return substituteEnvValues(input, null);
	}

	/**
	 * Substitute any environment variables found in the string
	 * of the form <code>%var%</code> with their defined values from the environment or from
	 * the alternate symbol map.
	 * <p>If the variable is not found then if a <code>|<i>value</i></code> is found inside the
	 * <code>%var%</code> construct (as in <code>%var|value%</code>) then this default value will
	 * be used, otherwise the original token is left (without the <code>%%</code>).
	 * <p> If the variable name begins with <code>$</code>, then search for it as a system property
	 * instead of from the environment (that is, using {@link System#getProperty} instead).
	 * <p> Additionally, if the first character of the key (not the default) is <code>^</code> (before the
	 * <code>$</code> if present), then the resulting value has its first letter capitalized (this is
	 * useful for Windows where this is an oft-used convention for directory names, for instance).
	 * If the prefix is <code>^^</code> then the whole word is capitalized.  Capitalization is done according
	 * to the default locale.
	 * <p> Similarly <code>_</code> or <code>__</code> is used to lowercase the resulting value (either first or all).
	 * <p> Examples:
	 * <pre> %$user.home%/.acme/%_product%/%^^id|AMP% -&gt; C:\Users\admin/.acme/backup/AMP
	 * %APPDATA%/Acme/%^product%/%^^id|AMP% -&gt; C:\Users\admin\AppData\Roaming/Acme/Backup/AMP
	 * </pre>
	 * where the supplied map has: "product" -&gt; "Backup" and "id" -&gt; "amp"
	 *
	 * @param   input   Input string needing its substitution tokens replaced from the
	 *		    environment	(or the	alternate symbol map).
	 * @param   symbols Alternate source of	environment values if an override
	 *		    symbol is not available in the environment (usually	gotten
	 *		    from some other configuration file); can be	{@code null}
	 * @return	    The original string with the variable substitutions made.
	 */
	public static String substituteEnvValues(final String input, final Map<String,String> symbols) {
	    StringBuilder buf = new StringBuilder(input);
	    int ix = buf.indexOf("%");
	    while (ix >= 0) {
		int iy = buf.indexOf("%", ix + 1);
		if (iy >= 0) {
		    String var = buf.substring(ix + 1, iy);
		    // Check for list of options, finally default
		    String[] values = var.split("\\|");
		    String defaultValue = null;
		    int len = values.length;
		    if (len > 1) {
			defaultValue = values[--len];
		    }
		    String value = null;
		    for (int i = 0; i < len; i++) {
			String key = values[i];
			boolean capitalizeFirst = false, capitalizeAll = false;
			boolean lowercaseFirst = false, lowercaseAll = false;
			if (key.charAt(0) == '^') {
			    if (key.charAt(1) == '^') {
				key = key.substring(2);
				capitalizeAll = true;
			    }
			    else {
				key = key.substring(1);
				capitalizeFirst = true;
			    }
			}
			else if (key.charAt(0) == '_') {
			    if (key.charAt(1) == '_') {
				key = key.substring(2);
				lowercaseAll = true;
			    }
			    else {
				key = key.substring(1);
				lowercaseFirst = true;
			    }
			}
			// "$key" indicates a system property
			// while just "key" is an environment variable
			if (key.charAt(0) == '$') {
			    // Strip the leading "$" in case we have to
			    // look this up in the supplied "symbols" list later
			    key = key.substring(1);
			    value = System.getProperty(key);
			}
			else {
			    value = System.getenv(key);
			}
			if (value == null && symbols != null) {
			    value = symbols.get(key);
			}
			if (value != null) {
			    if (capitalizeFirst) {
				value = capitalizeFirst(value);
			    }
			    else if (capitalizeAll) {
				value = value.toUpperCase();
			    }
			    else if (lowercaseFirst) {
				value = lowercaseFirst(value);
			    }
			    else if (lowercaseAll) {
				value = value.toLowerCase();
			    }
			    break;
			}
		    }
		    if (value != null) {
			buf.replace(ix, iy + 1, value);
		    }
		    else if (defaultValue != null) {
			buf.replace(ix, iy + 1, defaultValue);
		    }
		    else {
			buf.deleteCharAt(iy);
			buf.deleteCharAt(ix);
		    }
		}
		else {
		    // No matching "%", so no more substitutions either
		    break;
		}
		ix = buf.indexOf("%");
	    }
	    return buf.toString();
	}


	/**
	 * Turn a Java-rules name into regular words.
	 * <p> Example:  varName =&gt; Var name
	 *
	 * @param	input	The Java name (such as a "dscript" variable name).
	 * @return		The input suitably munged (or just the input if it
	 *			is {@code null} or empty).
	 */
	public static String changeJavaNameToWords(final String input) {
	    if (input != null && !input.isEmpty()) {
		StringBuilder buf = new StringBuilder();
		buf.append(Character.toTitleCase(input.charAt(0)));
		CharSequence seq = input.subSequence(1, input.length());
		for (int i = 0; i < seq.length(); i++) {
		    char ch = seq.charAt(i);
		    if (Character.isUpperCase(ch)) {
			buf.append(' ');
			buf.append(Character.toLowerCase(ch));
		    }
		    else {
			buf.append(ch);
		    }
		}
		return buf.toString();
	    }
	    return input;
	}


	/**
	 * Turn a Java enum value into a Java case word without the
	 * <code>"_"</code>s, in lower case, except for letters after the <code>"_"</code>s.
	 * Example:
	 * <pre>ENUM_NAME -&gt; enumName</pre>
	 *
	 * @param	enumName	The input name (presumably in the form we want).
	 * @return			The transformed name.
	 */
	public static String changeEnumNameToWords(final String enumName) {
	    StringBuilder buf = new StringBuilder(enumName.length());
	    boolean sawUnderscore = false;
	    for (int i = 0; i < enumName.length(); i++) {
		char ch = enumName.charAt(i);
		if (sawUnderscore) {
		    buf.append(Character.toUpperCase(ch));
		    sawUnderscore = false;
		}
		else if (ch == '_') {
		    sawUnderscore = true;
		}
		else {
		    buf.append(Character.toLowerCase(ch));
		}
	    }
	    return buf.toString();
	}

	/**
	 * Count the number of lines in a piece of text.
	 * <p> The count will be the number of line-ending sequences
	 * found (\n, \r or \r\n).
	 *
	 * @param	seq	The input sequence (so it can be a regular
	 *			{@link String} or a buffer, or something else).
	 * @return	The number of separate lines in the text.
	 */
	public static int countNumberOfLines(final CharSequence seq) {
	    int count = 0;
	    if (seq != null) {
		boolean seenCR = false;
		for (int i = 0; i < seq.length(); i++) {
		    char ch = seq.charAt(i);
		    if (ch == '\r') {
			seenCR = true;
		    }
		    else if (ch == '\n' || seenCR) {
			count++;
			seenCR = false;
		    }
		}
		if (seenCR) {
		    count++;
		}
	    }
	    return count;
	}

	/**
	* Attempts to obtain a valid DBMS identifier from an arbitrary string.
	*
	* @param str The input string.
	*
	* @return An identifier based on the input string. Returns {@code null} if a suitable identifier cannot be found.
	*/
	public static String getValidIdentifier(final String str) {
	    if (str == null || str.isEmpty()) {
		return null;
	    }

	    // Replace spaces with underscores.
	    String strValue = str.trim().replace(' ', '_');

	    String cleanedString = "";

	    // Ensure that any characters in the string are valid.
	    for (int index = 0; index < strValue.length(); index++) {
		char currentCharacter = strValue.charAt(index);
		if (identifierConstructionCharacterSet.indexOf(currentCharacter) >= 0){
		    cleanedString += currentCharacter;
		}
	    }

	    // Ensure that we actually have something left after cleaning the string.
	    if (cleanedString.isEmpty()) {
		return null;
	    }

	    // Ensure that we don't have just a string containing underscores!
	    boolean hasNonUnderscoreCharacter = false;
	    for (int index = 0; index < cleanedString.length(); index++) {
		char currentCharacter = cleanedString.charAt(index);
		if (currentCharacter != '_') {
		    hasNonUnderscoreCharacter = true;
		    break;
		}
	    }

	    if (!hasNonUnderscoreCharacter) {
		return null;
	    }

	    // Check if we have a valid identifier
	    if (isValidIdentifier(cleanedString, false)) {
		return cleanedString;
	    }

	    // See if adding an underscore gives us a valid identifier.
	    String worker = String.format("%1$s%2$s", SANITIZATION_PREFIX, cleanedString);
	    if (isValidIdentifier(worker, false)) {
		return worker;
	    }

	    // As a last ditch attempt, try quoting the cleaned string.
	    worker = delimitIdentifier(cleanedString, false);
	    if (isValidIdentifier(worker, false)) {
		return worker;
	    }

	    // We weren't able to determine an appropriate identifier, so return null and let the caller generate one.
	    return null;
	}


	/**
	 * Parse a set representation like <code>[a, b, c]</code> into an array of
	 * the constituent strings.
	 * @param	setString	A properly formatted set string.
	 * @return	{@code null} if the string isn't in the proper format,
	 *		or the parsed set of values.
	 */
	public static String[] getArrayFromSetString(final String setString) {
	    int length = setString.length();
	    if (length > 2 && setString.charAt(0) == '[' && setString.charAt(length - 1) == ']') {
		return setString.substring(1, length - 1).split("\\,\\s*");
	    }
	    return null;
	}


	/**
	 * Take a simple expression (likely only a single character or an escaped single character)
	 * and remove the escapes.
	 *
	 * @param	input	The simple expression string.
	 * @return		The input with the escapes parsed out.
	 */
	public static String unescape(final String input) {
	    StringBuilder buf = new StringBuilder(input);
	    int ix = 0;
	    while ((ix = buf.indexOf("\\", ix)) >= 0) {
		buf.deleteCharAt(ix);
	    }
	    return buf.toString();
	}


	/**
	 * Turn an list into a JSON-compatible name string.
	 *
	 * @param	list	The list of values to make printable.
	 * @return		A JSON-compatible version.
	 */
	public static String listToNameString(final List<Integer> list) {
	    StringBuilder buf = new StringBuilder(list.size() * 5);
	    buf.append('_');
	    for (Integer i : list) {
		buf.append(i).append('_');
	    }
	    return buf.toString();
	}


	/**
	 * Turn an array into a JSON-compatible name string.
	 *
	 * @param	array	The array of values to make printable.
	 * @return		A JSON-compatible version.
	 */
	public static String arrayToNameString(final Object[] array) {
	    StringBuilder buf = new StringBuilder(array.length * 5);
	    buf.append('_');
	    for (int i = 0; i < array.length; i++) {
		String name = array[i] == null ? "null" : array[i].toString();
		buf.append(name).append('_');
	    }
	    return buf.toString();
	}


	/**
	 * Capitalize the first letter of the string.
	 *
	 * @param	input	The raw text.
	 * @return		The input with the first letter capitalized
	 *			(according to the default locale), unless
	 *			the input is {@code null} or empty, in which
	 *			case the input itself is returned.
	 */
	public static String capitalizeFirst(final String input) {
	    if (input == null || input.isEmpty())
		return input;

	    char first = input.charAt(0);
	    StringBuilder buf = new StringBuilder(input.length());
	    buf.append(Character.toUpperCase(first));
	    if (input.length() > 1) {
		buf.append(input.substring(1));
	    }

	    return buf.toString();
	}


	/**
	 * Lowercase the first letter of the string.
	 *
	 * @param	input	The raw text.
	 * @return		The input with the first letter converted to lower case
	 *			(according to the default locale), unless
	 *			the input is {@code null} or empty, in which
	 *			case the input itself is returned.
	 */
	public static String lowercaseFirst(final String input) {
	    if (input == null || input.isEmpty())
		return input;

	    char first = input.charAt(0);
	    StringBuilder buf = new StringBuilder(input.length());
	    buf.append(Character.toLowerCase(first));
	    if (input.length() > 1) {
		buf.append(input.substring(1));
	    }

	    return buf.toString();
	}


	/**
	 * Make a string of the given character of the given width.
	 *
	 * @param	ch	The fill character.
	 * @param	width	How much of it you want.
	 * @return		That many of the fill character.
	 */
	public static String makeStringOfChars(final char ch, final int width) {
	    if (width > 0) {
		char[] chars = new char[width];
		Arrays.fill(chars, ch);
		return new String(chars);
	    }
	    return "";
	}


	/**
	 * Make a string of the given character of the given width and append
	 * to the input {@code StringBuilder}.
	 *
	 * @param	buf	The buffer that is already in progress.
	 * @param	ch	The fill character.
	 * @param	width	How many of the fill character to add.
	 * @return		The input {@code StringBuilder} with the chars appended.
	 */
	public static StringBuilder makeStringOfChars(final StringBuilder buf, final char ch, final int width) {
	    if (width > 0) {
		char[] chars = new char[width];
		Arrays.fill(chars, ch);
		buf.append(chars);
	    }
	    return buf;
	}


	/**
	 * Add a new word to the existing string, and separate it with a space
	 * (if not at the beginning of string).  Other special cases include
	 * the existing string ending with "(" or the word starting with ")".
	 * This is used for building SQL strings.
	 *
	 * @param	buf	The buffer that is already in progress.
	 * @param	word	The new word to add at the end of the string.
	 */
	public static void addWord(final StringBuilder buf, final String word) {
	    if (buf.length() > 0 &&
		buf.charAt(buf.length() - 1) != '(' &&
		!word.startsWith(")")) {
		buf.append(' ');
	    }
	    buf.append(word);
	}


	/**
	 * Add a new word to the existing string, and separate it with a space
	 * (if not at the beginning of string) with the integer value being
	 * converted to string first.  This is used for building SQL strings.
	 *
	 * @param	buf	The buffer that is already in progress.
	 * @param	value	The new value to add as a word at the end of the string.
	 */
	public static void addWord(final StringBuilder buf, final int value) {
	    addWord(buf, Integer.toString(value));
	}


	/**
	 * Add any number of new words to the existing string, and separate them
	 * with a space (if not at the beginning of string).  This is used for
	 * building SQL strings.
	 *
	 * @param	buf	The buffer that is already in progress.
	 * @param	words	The new words to add at the end of the string.
	 */
	public static void addWords(final StringBuilder buf, final Object... words) {
	    for (Object word : words) {
		if (word != null) {
		    addWord(buf, word.toString());
		}
	    }
	}


	/**
	 * Add a "clause" to an existing (SQL) string.  This adds a {@code ", "}
	 * to the end of a non-empty string, followed by the word given.
	 *
	 * @param	buf	The SQL string buffer that is already in progress.
	 * @param	word	The new word to add after the comma.
	 */
	public static void addClause(final StringBuilder buf, final String word) {
	    if (buf.length() > 0) {
		buf.append(',');
	    }
	    addWord(buf, word);
	}


	/**
	 * Add a "clause" to an existing (SQL) string.  This adds a {@code ", "}
	 * to the end of a non-empty string, followed by the key word, " = ", and
	 * the value phrase.
	 *
	 * @param	buf	The SQL string buffer that is already in progress.
	 * @param	keyWord	The new keyword to add after the comma.
	 * @param	value	The value for the keyword phrase.
	 */
	public static void addClause(final StringBuilder buf, final String keyWord, final String value) {
	    addClause(buf, keyWord, value, false);
	}


	/**
	 * Add a "clause" to an existing (SQL) string.  This adds a {@code ", "}
	 * to the end of a non-empty string, followed by the key word, " = ", and
	 * the value phrase, with the value phrase optionally SQL-quoted (that is,
	 * single quotes)..
	 *
	 * @param	buf	The SQL string buffer that is already in progress.
	 * @param	keyWord	The new keyword to add after the comma.
	 * @param	value	The value for the keyword phrase.
	 * @param	quoteValue Whether or not to quote the value part.
	 */
	public static void addClause(
		final StringBuilder buf,
		final String keyWord,
		final String value,
		final boolean quoteValue)
	{
	    if (buf.length() > 0) {
		buf.append(',');
	    }
	    if (quoteValue) {
		addWords(buf, keyWord, "=");
		addQuotes(value, buf);
	    }
	    else {
		addWords(buf, keyWord, "=", value);
	    }
	}


	/**
	 * @return Is the given string null or (trimmed) empty?
	 * @param value	The (presumably) string value to test.
	 */
	public static boolean isNullOrEmpty(final Object value) {
	    return value == null || (value instanceof String && ((String)value).trim().isEmpty());
	}


	/**
	 * Convert a null or trimmed empty string to null.
	 *
	 * @param	string	Input value to test (can be {@code null}).
	 * @return	{@code null} if the input value is {@code null} OR
	 *		if the trimmed input string is empty (that is, the string
	 *		consists of all blanks), or the input string otherwise.
	 */
	public static String getNullForEmpty(final String string) {
	    return (string == null || string.trim().isEmpty()) ? null : string;
	}


	/**
	 * The obverse of {@link #getNullForEmpty} in that a null input string
	 * gives an empty string back.
	 *
	 * @param	string	String which could be {@code null}.
	 * @return		{@code ""} if the input is {@code null} or
	 *			the input otherwise.
	 */
	public static String getEmptyForNull(final String string) {
	    return (string == null) ? "" : string;
	}


	/**
	 * Determine the boolean value from the given object.
	 * <p> This version only translates true booleans, strings that directly
	 * convert to boolean, or numbers (or numeric strings) that convert to
	 * zero/non-zero values.
	 *
	 * @param	value	The candidate value.
	 * @return		If possible, the boolean value that corresponds.
	 * @throws	IllegalArgumentException for bad string values or bad types.
	 * @see	#getBooleanValue(Object, boolean)
	 */
	public static boolean getBooleanValue(final Object value) {
	    return getBooleanValue(value, false);
	}

	/**
	 * Determine the boolean value from the given object.
	 * <p> Accepts values such as "Yes", "No", "T", "F", "On", and "Off"
	 * (case-insensitive), numeric values (true == non-zero) in addition
	 * to just plain "true" and "false", but only if {@code extended} is {@code true}.
	 *
	 * @param	value		The candidate value.
	 * @param	extended	Whether to use the extended string conversions
	 *				(like "T"/"F", "yes"/"no", etc.)
	 * @return			If possible, the boolean value that corresponds.
	 * @throws	IllegalArgumentException for bad string values or bad types.
	 */
	public static boolean getBooleanValue(final Object value, final boolean extended) {
	    if (value instanceof Boolean) {
		return ((Boolean)value).booleanValue();
	    }
	    else if (value instanceof Number) {
		return ((Number)value).longValue() != 0L;
	    }
	    else if (value instanceof String) {
		String stringValue = (String)value;
		boolean bool = Boolean.parseBoolean(stringValue);
		// A little explanation:  if the value returns true
		// then it must be "true" in some mixed-case sense
		// but a false value could be "false" (mixed case) or
		// just some random junk, so in that case test some
		// other possible (reasonable) values
		if (bool)
		    return bool;
		if (stringValue.equalsIgnoreCase("false"))
		    return false;

		// The extended string translations, which can be used in some situations (such as
		// database fields, or user input).
		if (extended) {
		    // Now, get the internationalized version of other strings that we will accept
		    try {
			String trueChoiceList = Intl.getString("util#char.validTrueStrings");
			for (String choice : trueChoiceList.split(",\\s*")) {
			    if (stringValue.equalsIgnoreCase(choice)) {
				return true;
			    }
			}
			String falseChoiceList = Intl.getString("util#char.validFalseStrings");
			for (String choice : falseChoiceList.split(",\\s*")) {
			    if (stringValue.equalsIgnoreCase(choice)) {
				return false;
			    }
			}
		    }
		    catch (IllegalArgumentException iae) {
			// This would be because we couldn't load the Intl string(s), so no matter...
		    }
		}

		// One last attempt: try to convert string to a number and check zero/non-zero
		try {
		    return Long.parseLong(stringValue) != 0L;
		}
		catch (NumberFormatException nfe) {
		    // Fall through to throw IllegalArgumentException
		}

		throw new Intl.IllegalArgumentException("util#char.unknownBooleanString", stringValue);
	    }
	    throw new Intl.IllegalArgumentException("util#char.unknownBooleanType",
		value == null ? "null" : value.getClass().getSimpleName());
	}


	/**
	 * Normalize whitespace in the given strings.  This means:
	 * <ul>
	 * <li>Remove leading and trailing whitespace (like {@link String#trim}.
	 * <li>Change any run of embedded whitespace (except inside quotes) into
	 * a single space.
	 * </ul>
	 * <p> Note: this does nothing to whitespace inside quotes (that is, SQL-quoted).
	 * <p> Also note: treats embedded quotes like in SQL strings (double them inside).
	 *
	 * @param string The input value to transform.
	 * @return The transformed value, or {@code null} if the input is {@code null}
	 * or empty (after "trim").
	 */
	public static String normalizeWhitespace(final String string) {
	    if (string == null)
		return string;
	    String workString = string.trim();
	    if (workString.isEmpty())
		return null;

	    StringBuilder buf = new StringBuilder(workString.length());
	    boolean insideQuotes = false;
	    boolean justSawWhite = false;
	    boolean justSawQuote = false;

	    for (int i = 0; i < workString.length(); i++) {
		char ch = workString.charAt(i);
		if (ch == '\'') {
		    if (!insideQuotes) {
			insideQuotes = true;
		    }
		    else {
			if (i >= workString.length() - 1 || workString.charAt(i + 1) != '\'') {
			    if (justSawQuote) {
				justSawQuote = false;
			    }
			    else {
				insideQuotes = false;
			    }
			}
			else {
			    justSawQuote = true;
			}
		    }
		    buf.append(ch);
		    justSawWhite = false;
		}
		else if (insideQuotes) {
		    buf.append(ch);
		    justSawWhite = justSawQuote = false;
		}
		else {
		    if (Character.isWhitespace(ch)) {
			if (!justSawWhite) {
			    buf.append(' ');
			    justSawWhite = true;
			}
		    }
		    else {
			buf.append(ch);
			justSawWhite = justSawQuote = false;
		    }
		}
	    }

	    return buf.toString();
	}

	/**
	 * Compare if two strings are equal, with the possibility that one or both
	 * could be null, which is okay: they are equal if both are null.
	 *
	 * @param s1	The first string to compare.
	 * @param s2	The second string.
	 * @return	{@code true} if both strings are null or compare exactly
	 *		with the {@link String#equals} method, or {@code false} otherwise.
	 */
	public static boolean stringsEqual(final String s1, final String s2) {
	    if (s1 == null && s2 == null)
		return true;

	    if (s1 != null && s2 != null && s1.equals(s2))
		return true;

	    return false;
	}


	/**
	 * For debug purposes, convert a string to an array of chars and format
	 * into a string of hex (upper case).
	 *
	 * @param input The input string to convert.
	 * @return The characters of this string, in hex, surrounded with "[ ]"
	 * and separated by commas.
	 */
	public static String toHexArrayForm(final String input) {
	    if (input == null)
		return "null";
	    if (input.isEmpty()) {
		return "[]";
	    }
	    StringBuilder buf = new StringBuilder(input.length() * 3 + 1);
	    buf.append('[');
	    char[] chars = input.toCharArray();
	    for (char c : chars) {
		if (buf.length() > 1)
		    buf.append(',');
		int i = (int) c;
		if (i < 16)
		    buf.append('0');
		buf.append(Integer.toString(i, 16).toUpperCase());
	    }
	    buf.append(']');
	    return buf.toString();
	}

	/**
	 * For debug purposes, convert an array of bytes and format
	 * into a string of hex (upper case).
	 *
	 * @param input The array of bytes to convert.
	 * @return The bytes of this array, in hex, surrounded with "[ ]"
	 * and separated by commas.
	 */
	public static String toHexArrayForm(final byte[] input) {
	    if (input == null)
		return "null";
	    if (input.length == 0) {
		return "[]";
	    }
	    StringBuilder buf = new StringBuilder(input.length * 3 + 1);
	    buf.append('[');
	    for (byte b : input) {
		if (buf.length() > 1)
		    buf.append(',');
		int i = ((int) b) & 0xFF;
		if (i < 16)
		    buf.append('0');
		buf.append(Integer.toString(i, 16).toUpperCase());
	    }
	    buf.append(']');
	    return buf.toString();
	}

	/**
	 * Convert a normal Unix/Windows/Linux style wildcard file spec into
	 * a regular expression suitable for use with {@link Pattern}.
	 *
	 * @param wildcard The wildcard format string to convert.
	 * @return The equivalent regex string.
	 */
	public static String wildcardToRegex(final String wildcard) {
	    // Here are the rules:
	    // . -> \.
	    // * -> .*
	    // ? -> .
	    // \ -> \\
	    // : -> \:
	    if (isNullOrEmpty(wildcard)) {
		return wildcard;
	    }
	    StringBuilder buf = new StringBuilder(wildcard.length() + 10);
	    for (int i = 0; i < wildcard.length(); i++) {
		char ch = wildcard.charAt(i);
		switch (ch) {
		    case '.':
			buf.append("\\.");
			break;
		    case '*':
			buf.append(".*");
			break;
		    case '?':
			buf.append(".");
			break;
		    case STD_ESCAPE_CHAR:
			buf.append("\\\\");
			break;
		    case ':':
			buf.append("\\:");
			break;
		    default:
			buf.append(ch);
			break;
		}
	    }
	    return buf.toString();
	}

	/**
	 * Wrap an input string to the given line width, basically breaking at whitespace, unless
	 * there is none, in which case the break is exactly at the line width.
	 *
	 * @param input The input string to wrap.
	 * @param width Width of the line to wrap at.
	 * @param pad Specify {@code true} to pad each output line with spaces up to the given width,
	 * or {@code false} to simply output the input characters (appropriately wrapped) to the output
	 * without additional padding.
	 * @return The wrapped string.
	 */
	public static String wrapText(final String input, final int width, final boolean pad) {
	    StringBuilder buf = new StringBuilder(input.length() + 50);

	    int currentLineWidth = 0;
	    int lastLineStart = 0;
	    int lastWhitePos = -1;
	    int lastPunctPos = -1;

	    for (int cp, i = 0; i < input.length(); i += Character.charCount(cp)) {
		cp = input.codePointAt(i);

		boolean hardBreak = (cp == '\n');
		if (hardBreak) {
		    lastWhitePos = i;
		}
		if (++currentLineWidth >= width || hardBreak) {
		    int endPos;
		    int whiteLength = lastWhitePos >= 0 ? (lastWhitePos - lastLineStart) : -1;
		    int punctLength = lastPunctPos >= 0 ? (lastPunctPos - lastLineStart) : -1;

		    if (whiteLength >= 0 && whiteLength <= width && whiteLength > punctLength) {
			endPos = lastWhitePos;
		    }
		    else if (punctLength >= 0 && punctLength <= width) {
			endPos = lastPunctPos;
		    }
		    else {
			endPos = i;
		    }
		    CharSequence seq = input.subSequence(lastLineStart, endPos);
		    lastLineStart = endPos;
		    buf.append(seq);
		    if (pad) {
			int numToPad = width - seq.length();
			for (int j = 0; j < numToPad; j++) {
			    buf.append(' ');
			}
		    }
		    buf.append('\n');
		    // If new line beginning is a space, then skip it
		    cp = input.codePointAt(lastLineStart);
		    if (cp == ' ' || cp == '\n') {
			lastLineStart++;
		    }
		    currentLineWidth = 0;
		    lastWhitePos = lastPunctPos = -1;
		    i = lastLineStart;
		}
		else {
		    boolean isPunct = false;
		    switch (cp) {
			case '+':
			case ')':
			case ',':
			    isPunct = true;
			    break;
			default:
			    break;
		    }
		    if (isPunct) {
			lastPunctPos = i + 1;
		    }
		    else if (Character.isWhitespace(cp)) {
			lastWhitePos = i + 1;
		    }
		}
	    }
	    // Handle last line
	    if (lastLineStart < input.length()) {
		String lastLine = input.substring(lastLineStart);
		buf.append(lastLine);
		if (pad) {
		    int numToPad = width - lastLine.length();
		    for (int j = 0; j < numToPad; j++) {
			buf.append(' ');
		    }
		}
	    }
	    return buf.toString();
	}


	/**
	 * Is the given string a valid integer value (as defined by {@link Integer#parseUnsignedInt} method)?
	 *
	 * @param input The input string to test.
	 * @return {@code true} or {@code false} depending on whether the input represents a valid integer value.
	 */
	public static boolean isValidInt(final String input) {
	    try {
		int value = Integer.parseUnsignedInt(input);
	    }
	    catch (NumberFormatException nfe) {
		return false;
	    }
	    return true;
	}


	/**
	 * Match an input argument with any of a list of choices (case-sensitive checks).
	 *
	 * @param input		The input value to test.
	 * @param choices	The expected choices to test against.
	 * @return		{@code true} if the input matches exactly any of the choices,
	 *			or {@code false} otherwise, including null or empty input.
	 */
	public static boolean matchesAnyOf(final String input, final String... choices) {
	    if (input == null || input.isEmpty())
		return false;

	    for (String choice : choices) {
		if (input.equals(choice))
		    return true;
	    }

	    return false;
	}


	/**
	 * Match an input argument with any of a list of choices (case-insensitive checks).
	 *
	 * @param input		The input value to test.
	 * @param choices	The expected choices to test against.
	 * @return		{@code true} if the input matches any of the choices, regardless
	 *			of case, or {@code false} otherwise, including null or empty input.
	 */
	public static boolean matchesAnyOfIgnoreCase(final String input, final String... choices) {
	    if (input == null || input.isEmpty())
		return false;

	    for (String choice : choices) {
		if (input.equalsIgnoreCase(choice))
		    return true;
	    }

	    return false;
	}


	/**
	 * Parse a command line into its separate arguments. If any arguments are enclosed
	 * in single or double quotes, the quotes will be stripped in the final result array.
	 * No escape character is recognized, nor are double quotes honored inside a quote.
	 * No wildcard expansion is done either (note: this is explicitly different than
	 * the way Java handles the command line). So, the main difference between this
	 * method and just splitting the input on whitespace is that quotes around spaces
	 * here will make the quoted value into a single argument instead of multiple.
	 *
	 * @param line	The original command line string.
	 * @return	The input parsed into pieces.
	 */
	public static String[] parseCommandLine(final String line) {
	    List<String> args = new ArrayList<>();
	    boolean inQuotes = false;
	    boolean startOfWord = true;
	    char quoteChar ='\0';
	    StringBuilder buf = new StringBuilder(line.length());

	    for (int ix = 0; ix < line.length(); ix++) {
		char ch = line.charAt(ix);
		if (inQuotes) {
		    if (ch == quoteChar) {
			inQuotes = false;
			args.add(buf.toString());
			buf.setLength(0);
			startOfWord = true;
		    }
		    else {
			buf.append(ch);
		    }
		}
		else {
		    if (startOfWord && (ch == '"' || ch == '\'')) {
			quoteChar = ch;
			inQuotes = true;
			startOfWord = false;
		    }
		    else {
			if (startOfWord) {
			    if (Character.isWhitespace(ch))
				continue;
			    else {
				startOfWord = false;
				buf.append(ch);
			    }
			}
			else {
			    if (Character.isWhitespace(ch)) {
				args.add(buf.toString());
				buf.setLength(0);
				startOfWord = true;
			    }
			    else {
				buf.append(ch);
			    }
			}
		    }
		}
	    }

	    // Add the last word (if any)
	    if (buf.length() > 0) {
		args.add(buf.toString());
	    }

	    return args.toArray(new String[0]);
	}


	/**
	 * Given a string which contains embedded line separators, separate into an array of lines without the separators.
	 *
	 * @param input The input string with line separators.
	 * @return      The array of individual lines without the separators.
	 */
	public static String[] stringToLines(final String input) {
	    return input.split("[\\r?\\n]");
	}


	/**
	 * Given an array of lines, compute the maximum length of those lines.
	 *
	 * @param lines The input lines to consider.
	 * @return      The maximum length of any of the lines.
	 */
	public static int maxLength(final String[] lines) {
	    int max = 0;
	    for (String line : lines) {
		max = Math.max(max, line.length());
	    }
	    return max;
	}


}
