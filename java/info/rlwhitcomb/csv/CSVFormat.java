/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017,2019-2022 Roger L. Whitcomb.
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
 *	Class to define the format of a CSV parsing/writing session.
 *
 * History:
 *	27-Feb-2014 (rlwhitcomb)
 *	    Initial coding, based on many other CSV packages.
 *	03-Mar-2014 (rlwhitcomb)
 *	    Add options for escape character and ignoring empty lines.
 *	04-Mar-2014 (rlwhitcomb)
 *	    Move text to resources file.
 *	12-Mar-2014 (rlwhitcomb)
 *	    Allow a null or empty record separator to indicate the default.
 *	07-Oct-2015 (whir01)
 *	    Address Javadoc warnings found by Java 8.
 *	27-Jun-2016 (rlwhitcomb)
 *	    Implement left- and right-quote characters.  Use the new Quotes
 *	    and Delimiter enums for easy param setting (and compatibility with
 *	    the new FileStatement syntax).
 *	24-May-2017 (rlwhitcomb)
 *	    Implement "quoting disabled" and the "None" quote option.
 *	30-Dec-2019 (rlwhitcomb)
 *	    Add option method for always delimiting strings.
 *	27-Feb-2020 (rlwhitcomb)
 *	    Add option for using new Separator enum.
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	21-Dec-2020 (rlwhitcomb)
 *	    Update obsolete Javadoc constructs.
 *	29-Jan-2021 (rlwhitcomb)
 *	    Use new Exception variants using Intl for convenience.
 *	17-Feb-2021 (rlwhitcomb)
 *	    Add new "hasHeaderRow" and "preserveQuotes" flags; new
 *	    "getLeftQuote" and "getRightQuote" methods.
 *	15-Dec-2021 (rlwhitcomb)
 *	    #146: Additional methods to expose new flags and such.
 *	    Okay, okay, this isn't a true Java Bean because the get/set methods
 *	    are not symmetric (partly due to the "Builder" pattern and partly
 *	    because the flag names aren't quite grammatical with "is" prefix).
 *	    Add "Scriptable" annotations for fields to be exposed.
 */
package info.rlwhitcomb.csv;

import info.rlwhitcomb.annotations.Scriptable;
import info.rlwhitcomb.util.Intl;


/**
 * Defines the various parameters needed for parsing a (general-purpose) CSV
 * file or string.  As a superset of the RFC 4180 format (in that quote
 * characters, field separators and record separators can all be user-defined),
 * this is usable in a wide variety of situations.
 * <p> This class uses a "builder" pattern, in that once the object is
 * constructed, each method that changes a parameter value returns the
 * object ({@code this}) so multiple calls to change parameters can be
 * chained together.
 */
public class CSVFormat
{
	/** The default quote character (according to RFC 4180). */
	public static final char DEFAULT_QUOTE_CHAR = '"';

	/** The default field separator (according to RFC 4180). */
	public static final char DEFAULT_FIELD_SEP_CHAR = ',';

	/** The default record separator string (according to RFC 4180). */
	public static final String DEFAULT_RECORD_SEP = "\r\n";

	/**
	 * The default escape character. Used primarily to embed the ending quote
	 * within quoted values. Disable by setting to {@code '\0'}, which will
	 * enable doubling quotes inside quoted values to embed the quote.
	 */
	public static final char DEFAULT_ESCAPE_CHAR = '\\';

	/** Our custom quote character. */
	@Scriptable
	protected char quoteChar = DEFAULT_QUOTE_CHAR;

	/** Our custom right-quote character (if different than the single one). */
	@Scriptable
	protected char rightQuoteChar = DEFAULT_QUOTE_CHAR;

	/** Flag for easy testing to see if the left and right quotes are different. */
	protected boolean handedQuoting = false;

	/** Our custom field separator character. */
	@Scriptable
	protected char fieldSepChar = DEFAULT_FIELD_SEP_CHAR;

	/** Our custom record separator string. */
	@Scriptable
	protected String recordSep = DEFAULT_RECORD_SEP;

	/** For a one character record separator, that character. */
	protected char recordSepChar = '\n';

	/** Our custom escape character. */
	@Scriptable
	protected char escapeChar = DEFAULT_ESCAPE_CHAR;

	/** Whether or not to preserve extra whitespace in unquoted tokens. */
	@Scriptable
	protected boolean preserveWhitespace = false;

	/** Whether or not to ignore empty lines. */
	@Scriptable
	protected boolean ignoreEmptyLines = false;

	/** Whether or not the line ending (record separator) is a "normal" value. */
	protected boolean normalLineEnding = true;

	/** Whether we have an escape character. */
	protected boolean disableEscape = false;

	/** Whether we have any quote character(s). */
	protected boolean disableQuoting = false;

	/** Whether string values are always delimited on output (regardless of contents). */
	@Scriptable
	protected boolean alwaysDelimitStrings = false;

	/** Whether to preserve quotes around values on input. */
	@Scriptable
	protected boolean preserveQuotes = false;

	/** Whether on input there is a header row before the actual data. */
	@Scriptable
	protected boolean hasHeaderRow = false;


	/**
	 * Construct a new (default) format.
	 */
	public CSVFormat() {
	    // Accept all defaults.
	}

	/**
	 * Construct a new format with the given values.
	 *
	 * @param	quoteChar	Quote character to use.
	 * @param	fieldSepChar	Field separator.
	 * @param	recordSep	Separator for lines (records).
	 */
	public CSVFormat(final char quoteChar, final char fieldSepChar, final String recordSep) {
	    this();
	    withQuoteChar(quoteChar).withFieldSepChar(fieldSepChar).withRecordSep(recordSep);
	}

	/**
	 * Construct a new format with the given field separator, and all other defaults.
	 *
	 * @param	fieldSepChar	Custom field separator.
	 */
	public CSVFormat(final char fieldSepChar) {
	    this();
	    withFieldSepChar(fieldSepChar);
	}

	/**
	 * Set the quote character of an already existing format.
	 * <p> This will set both the left and right quote characters to the same value, so if
	 * you intend to implement "handed quoting" (where the left and right quotes are different)
	 * then set this first and the {@link #withRightQuoteChar} second.
	 *
	 * @param	newQuoteChar	The new quote character to use for formatting or parsing.  Can
	 *				be {@code '\0'} or {@link Quotes.Constants#NO_QUOTE_CHAR}
	 *				to disable quote recognition.
	 *
	 * @return	The {@link CSVFormat} object (updated).
	 *
	 * @throws	IllegalArgumentException if the quote character is the same as the escape character.
	 */
	public CSVFormat withQuoteChar(final char newQuoteChar) {
	    if (newQuoteChar == escapeChar)
		throw new Intl.IllegalArgumentException("csv#format.quoteNotEscape");
	    quoteChar = rightQuoteChar = newQuoteChar;
	    if (quoteChar == Quotes.Constants.NO_QUOTE_CHAR)
		disableQuoting = true;
	    handedQuoting = false;
	    return this;
	}

	/**
	 * Set the right quote character of an already existing format.
	 * <p> This only sets the right quote character (and the internal {@link #handedQuoting} flag),
	 * so call this method AFTER calling {@link #withQuoteChar}.
	 *
	 * @param	newRightQuoteChar	The new right quote character to use for formatting or parsing
	 *					(can be {@code '\0'} or {@link Quotes.Constants#NO_QUOTE_CHAR}
	 *					to disable handed quoting).
	 *
	 * @return	The {@link CSVFormat} object (updated).
	 *
	 * @throws	IllegalArgumentException if the quote character is the same as the escape character.
	 */
	public CSVFormat withRightQuoteChar(final char newRightQuoteChar) {
	    if (newRightQuoteChar == escapeChar)
		throw new Intl.IllegalArgumentException("csv#format.quoteNotEscape");
	    rightQuoteChar = (newRightQuoteChar == Quotes.Constants.NO_QUOTE_CHAR ? quoteChar : newRightQuoteChar);
	    handedQuoting = (quoteChar != rightQuoteChar);
	    return this;
	}

	/**
	 * Set the left and right quote characters of the already existing format.
	 *
	 * @param	newQuoteChar		The new left quote character.
	 * @param	newRightQuoteChar	The new right quote character.
	 *
	 * @return	The {@link CSVFormat} object (updated).
	 *
	 * @see #withQuoteChar
	 * @see #withRightQuoteChar
	 */
	public CSVFormat withQuotes(final char newQuoteChar, final char newRightQuoteChar) {
	    return withQuoteChar(newQuoteChar).withRightQuoteChar(newRightQuoteChar);
	}

	/**
	 * Set the quote character(s) from the given enum value.
	 *
	 * @param	quote	The {@link Quotes} value that will determine the left (and possibly right)
	 *			quote characters.
	 *
	 * @return	The {@link CSVFormat} object (updated).
	 *
	 * @throws	IllegalArgumentException if the quote value is {@code null}.
	 */
	public CSVFormat withQuote(final Quotes quote) {
	    if (quote == null)
		throw new Intl.IllegalArgumentException("csv#format.quoteNotNull");
	    return withQuotes(quote.leftChar(), quote.rightChar());
	}

	/**
	 * Set the field separator of an already existing format.
	 *
	 * @param	newFieldSepChar	The new field separator to use for formatting and parsing.
	 *
	 * @return	The {@link CSVFormat} object (updated).
	 *
	 * @throws	IllegalArgumentException if the separator character is a {@code NUL}.
	 */
	public CSVFormat withFieldSepChar(final char newFieldSepChar) {
	    if (newFieldSepChar == '\0')
		throw new Intl.IllegalArgumentException("csv#format.fieldSepNotNull");
	    fieldSepChar = newFieldSepChar;
	    return this;
	}

	/**
	 * Set the field separator of an already existing format using the enum value.
	 *
	 * @param	delimiter	The new field separator enum value.
	 *
	 * @return	The {@link CSVFormat} object (updated).
	 *
	 * @throws	IllegalArgumentException if the enum value is {@code null}.
	 */
	public CSVFormat withDelimiter(final Delimiter delimiter) {
	    if (delimiter == null)
		throw new Intl.IllegalArgumentException("csv#format.fieldSepNotNull");
	    fieldSepChar = delimiter.delim();
	    return this;
	}

	/**
	 * Decide if the given string is a "normal" line ending, which is a single CR, single LF,
	 * CR, LF or LF, CR (non-standard, but whatever).
	 *
	 * @param sepString	Candidate record separator string.
	 * @return		Whether or not it is "normal".
	 */
	private static boolean isNormal(final String sepString) {
	    int len = sepString.length();
	    if (len == 1 || len == 2) {
		char ch1 = sepString.charAt(0);
		if (len > 1) {
		    char ch2 = sepString.charAt(1);
		    return (ch1 == '\r' || ch1 == '\n') &&
			   (ch2 == '\r' || ch2 == '\n') &&
			   (ch1 != ch2);
		}
		else {
		    return (ch1 == '\r' || ch1 == '\n');
		}
	    }
	    return false;
	}

	/**
	 * Set the record separator of an already existing format.
	 *
	 * @param	newRecordSep	{@code null} to set the default line ending
	 *				(see {@link #DEFAULT_RECORD_SEP}) or a
	 *				one or two character string that will
	 *				mark the end of a record.
	 *
	 * @return	The {@link CSVFormat} object (updated).
	 *
	 * @throws	IllegalArgumentException if the new separator is longer than two characters,
	 *		or if the length is two and both characters are the same, or if the string
	 *		does not represent a "normal" line ending and the length is greater than one.
	 */
	public CSVFormat withRecordSep(final String newRecordSep) {
	    if (newRecordSep == null || newRecordSep.isEmpty()) {
		recordSep = DEFAULT_RECORD_SEP;
		recordSepChar = '\n';
		normalLineEnding = true;
		return this;
	    }
	    else if (newRecordSep.length() > 2)
		throw new Intl.IllegalArgumentException("csv#format.recordSepTwoChars");
	    else if (newRecordSep.length() == 2) {
		if (newRecordSep.charAt(0) == newRecordSep.charAt(1))
		    throw new Intl.IllegalArgumentException("csv#format.recordSepTwoDifferent");
	    }

	    // Decide if this is a "normal" line ending, which has special meaning during parsing
	    normalLineEnding = isNormal(newRecordSep);

	    if (!normalLineEnding && newRecordSep.length() > 1)
		throw new Intl.IllegalArgumentException("csv#format.recordSepStandard");

	    recordSep = newRecordSep;
	    recordSepChar = recordSep.charAt(0);

	    return this;
	}

	/**
	 * Set the record separator to the given character.
	 *
	 * @param	newRecordSepChar	The new value for the record separator character for formatting and parsing.
	 *
	 * @return	The updated {@link CSVFormat} object.
	 */
	public CSVFormat withRecordSepChar(final char newRecordSepChar) {
	    return withRecordSep(Character.toString(newRecordSepChar));
	}

	/**
	 * Set the record separator to the given well-known value.
	 *
	 * @param	separator	The new value for the record separator for formatting and parsing.
	 *
	 * @return	The updated {@link CSVFormat} object.
	 */
	public CSVFormat withSeparator(final Separator separator) {
	    return withRecordSep(separator.getSeparator());
	}

	/**
	 * Set the escape character of an already existing format.
	 *
	 * @param	newEscapeChar	The new value for the escape character for formatting and parsing.
	 *
	 * @return	The updated {@link CSVFormat} object.
	 *
	 * @throws	IllegalArgumentException if this escape character is the same as the quote character.
	 */
	public CSVFormat withEscapeChar(final char newEscapeChar) {
	    if (newEscapeChar == quoteChar)
		throw new Intl.IllegalArgumentException("csv#format.escapeNotQuote");
	    escapeChar = newEscapeChar;
	    if (escapeChar == '\0')
		disableEscape = true;
	    return this;
	}

	/**
	 * Set the "preserve whitespace" flag of an existing format.
	 *
	 * @param	flag	The new value of the "preserve whitespace" flag.
	 *
	 * @return	The updated {@link CSVFormat} object.
	 */
	public CSVFormat withPreserveWhitespace(final boolean flag) {
	    preserveWhitespace = flag;
	    return this;
	}

	/**
	 * Set the "ignore empty lines" flag of an existing format.
	 *
	 * @param	flag	New value of the "ignore empty lines" flag.
	 *
	 * @return	The updated {@link CSVFormat} object.
	 */
	public CSVFormat withIgnoreEmptyLines(final boolean flag) {
	    ignoreEmptyLines = flag;
	    return this;
	}

	/**
	 * Set the "always delimit strings" flag of an existing format.
	 *
	 * @param	flag	New value of the "always delimit strings" flag.
	 *
	 * @return	The updated {@link CSVFormat} object.
	 */
	public CSVFormat withAlwaysDelimitStrings(final boolean flag) {
	    alwaysDelimitStrings = flag;
	    return this;
	}

	/**
	 * Set the "preserve quotes" flag for input on an existing format.
	 *
	 * @param	flag	New value for the "preserve quotes" flag.
	 *
	 * @return	The updated {@link CSVFormat} object.
	 */
	public CSVFormat withPreserveQuotes(final boolean flag) {
	    preserveQuotes = flag;
	    return this;
	}

	/**
	 * Set the "has header row" flag on an existing format.
	 *
	 * @param	flag	New value for the "has header row" flag.
	 *
	 * @return	The updated {@link CSVFormat} object.
	 */
	public CSVFormat withHasHeaderRow(final boolean flag) {
	    hasHeaderRow = flag;
	    return this;
	}

	/**
	 * Are there different quotes on the left and right sides of strings?
	 * <p> This corresponds to several standard sets of {@code Quotes}, such as
	 * {@link Quotes#SMARTSINGLE} and {@link Quotes#SMARTDOUBLE}.
	 *
	 * @return	Whether or not the left and right quotes are different.
	 */
	protected boolean isHandedQuoting() {
	    return handedQuoting;
	}

	/**
	 * @return The computed left quote value for this format.
	 */
	protected char getLeftQuote() {
	    return quoteChar;
	}

	/**
	 * @return The computed right quote value for this format.
	 */
	protected char getRightQuote() {
	    return rightQuoteChar;
	}

	/**
	 * Are we preserving whitespace around non-quoted values?
	 *
	 * @return	The {@link #preserveWhitespace} flag.
	 */
	protected boolean isPreservingWhitespace() {
	    return preserveWhitespace;
	}

	/**
	 * Are we ignoring empty lines on input?
	 *
	 * @return	The {@link #ignoreEmptyLines} flag.
	 */
	protected boolean isIgnoringEmptyLines() {
	    return ignoreEmptyLines;
	}

	/**
	 * Is this a "normal" line ending value, meaning is it any of the following:
	 * <code>CR</code> or <code>LF</code> or <code>CRLF</code>
	 * <p> This has special meaning in that any of these combinations will also
	 * match any of these values in the parsed file.
	 *
	 * @return	The current value of the "normal line ending" flag.
	 */
	protected boolean isNormalLineEnding() {
	    return normalLineEnding;
	}

	/**
	 * Is escaping disabled by the lack of an escape character? Set when the escape
	 * character is set to {@code '\0'}.
	 *
	 * @return	The {@link #disableEscape} flag.
	 */
	protected boolean isEscapeDisabled() {
	    return disableEscape;
	}

	/**
	 * Is quoting disabled by the quote characters being set to {@code '\0'}.
	 *
	 * @return	The {@link #disableQuoting} flag.
	 */
	protected boolean isQuotingDisabled() {
	    return disableQuoting;
	}

	/**
	 * Are we always delimiting strings on output? Normally strings don't need to be
	 * delimited unless they contain separators and/or leading or trailing spaces, but
	 * some programs basically differentiate between numbers and strings by having
	 * strings always quoted. So, that's what this option is used for.
	 *
	 * @return	The {@link #alwaysDelimitStrings} flag.
	 */
	protected boolean isAlwaysDelimitingStrings() {
	    return alwaysDelimitStrings;
	}

	/**
	 * Are we preserving quotes on input? Normally quotes around input fields are discarded
	 * on input, leaving the pure values, but some programs distinguish between numeric and
	 * string values by the quotes around strings, so preserving them will help to distinguish
	 * data types on input.
	 *
	 * @return	The {@link #preserveQuotes} flag.
	 */
	protected boolean isPreservingQuotes() {
	    return preserveQuotes;
	}

	/**
	 * Does the data have a header row? This affects both input and output. On input the header
	 * row provides column/field names, while on output IF headers have been provided they will
	 * be used, otherwise generic names will be used.
	 *
	 * @return	The {@link #hasHeaderRow} flag.
	 */
	protected boolean isHasHeaderRow() {
	    return hasHeaderRow;
	}

}

