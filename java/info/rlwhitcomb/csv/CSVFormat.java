/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017,2019-2020 Roger L. Whitcomb.
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
 */
package info.rlwhitcomb.csv;

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

	/** The default escape character. */
	public static final char DEFAULT_ESCAPE_CHAR = '\\';

	/** Our custom quote character. */
	protected char quoteChar = DEFAULT_QUOTE_CHAR;

	/** Our custom right-quote character (if different than the single one). */
	protected char rightQuoteChar = DEFAULT_QUOTE_CHAR;

	/** Flag for easy testing to see if the left and right quotes are different. */
	protected boolean handedQuoting = false;

	/** Our custom field separator character. */
	protected char fieldSepChar = DEFAULT_FIELD_SEP_CHAR;

	/** Our custom record separator string. */
	protected String recordSep = DEFAULT_RECORD_SEP;

	/** Our custom escape character. */
	protected char escapeChar = DEFAULT_ESCAPE_CHAR;

	/** For one character record separator, that character. */
	protected char recordSepChar = '\0';

	/** Whether or not to preserve extra whitespace in unquoted tokens. */
	protected boolean preserveWhitespace = false;

	/** Whether or not to ignore empty lines. */
	protected boolean ignoreEmptyLines = false;

	/** Whether or not the line ending (record separator) is a "normal" value. */
	protected boolean normalLineEnding = true;

	/** Whether we have an escape character. */
	protected boolean disableEscape = false;

	/** Whether we have any quote character(s). */
	protected boolean disableQuoting = false;

	/** Whether string values are always delimited on output (regardless of contents). */
	protected boolean alwaysDelimitStrings = false;

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
	public CSVFormat(char quoteChar, char fieldSepChar, String recordSep) {
	    this();
	    this.withQuoteChar(quoteChar).withFieldSepChar(fieldSepChar).withRecordSep(recordSep);
	}

	/**
	 * Construct a new format with the given field separator, and all other defaults.
	 *
	 * @param	fieldSepChar	Custom field separator.
	 */
	public CSVFormat(char fieldSepChar) {
	    this();
	    this.withFieldSepChar(fieldSepChar);
	}

	/**
	 * Set the quote character of an already existing format.
	 * <p> This will set both the left and right quote characters to the same value, so if
	 * you intend to implement "handed quoting" (where the left and right quotes are different)
	 * then set this first and the {@link #withRightQuoteChar} second.
	 *
	 * @param	quoteChar	The new quote character to use for formatting or parsing.  Can
	 *				be {@code NUL} to disable quote recognition.
	 *
	 * @return	The {@link CSVFormat} object (updated).
	 *
	 * @throws	IllegalArgumentException if the quote character is the same as the escape character.
	 */
	public CSVFormat withQuoteChar(char quoteChar) {
	    if (quoteChar == escapeChar)
		throw new IllegalArgumentException(Intl.getString("csv#format.quoteNotEscape"));
	    this.quoteChar = this.rightQuoteChar = quoteChar;
	    if (this.quoteChar == Quotes.Constants.NO_QUOTE_CHAR)
		this.disableQuoting = true;
	    this.handedQuoting = false;
	    return this;
	}

	/**
	 * Set the right quote character of an already existing format.
	 * <p> This only sets the right quote character (and the internal {@link #handedQuoting} flag),
	 * so call this method AFTER calling {@link #withQuoteChar}.
	 *
	 * @param	rightQuoteChar	The new right quote character to use for formatting or parsing
	 *				(can be {@code '\0'} to disable handed quoting).
	 *
	 * @return	The {@link CSVFormat} object (updated).
	 *
	 * @throws	IllegalArgumentException if the quote character is the same as the escape character.
	 */
	public CSVFormat withRightQuoteChar(char rightQuoteChar) {
	    if (rightQuoteChar == escapeChar)
		throw new IllegalArgumentException(Intl.getString("csv#format.quoteNotEscape"));
	    this.rightQuoteChar = rightQuoteChar == Quotes.Constants.NO_QUOTE_CHAR ? this.quoteChar : rightQuoteChar;
	    this.handedQuoting = this.quoteChar != this.rightQuoteChar;
	    return this;
	}

	/**
	 * Select the quote character(s) from the given enum value.
	 *
	 * @param	quote	The {@link Quotes} value that will determine the left (and possibly right)
	 *			quote characters.
	 *
	 * @return	The {@link CSVFormat} object (updated).
	 *
	 * @throws	IllegalArgumentException if the quote value is {@code null}.
	 */
	public CSVFormat withQuote(Quotes quote) {
	    if (quote == null)
		throw new IllegalArgumentException(Intl.getString("csv#format.quoteNotNull"));
	    this.quoteChar = quote.leftChar();
	    this.rightQuoteChar = quote.rightChar();
	    this.handedQuoting = this.quoteChar != this.rightQuoteChar;
	    if (this.quoteChar == Quotes.Constants.NO_QUOTE_CHAR)
		this.disableQuoting = true;
	    return this;
	}

	/**
	 * Set the field separator of an already existing format.
	 *
	 * @param	fieldSepChar	The new field separator to use for formatting and parsing.
	 *
	 * @return	The {@link CSVFormat} object (updated).
	 *
	 * @throws	IllegalArgumentException if the separator character is a {@code NUL}.
	 */
	public CSVFormat withFieldSepChar(char fieldSepChar) {
	    if (fieldSepChar == '\0')
		throw new IllegalArgumentException(Intl.getString("csv#format.fieldSepNotNull"));
	    this.fieldSepChar = fieldSepChar;
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
	public CSVFormat withDelimiter(Delimiter delimiter) {
	    if (delimiter == null)
		throw new IllegalArgumentException(Intl.getString("csv#format.fieldSepNotNull"));
	    this.fieldSepChar = delimiter.delim();
	    return this;
	}

	/**
	 * Set the record separator of an already existing format.
	 *
	 * @param	recordSep	{@code null} to set the default line ending
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
	public CSVFormat withRecordSep(String recordSep) {
	    if (recordSep == null || recordSep.isEmpty()) {
		this.recordSep = DEFAULT_RECORD_SEP;
		this.recordSepChar = '\n';
		this.normalLineEnding = true;
		return this;
	    }
	    else if (recordSep.length() > 2)
		throw new IllegalArgumentException(Intl.getString("csv#format.recordSepTwoChars"));
	    else if (recordSep.length() == 2) {
		if (recordSep.charAt(0) == recordSep.charAt(1))
		    throw new IllegalArgumentException(Intl.getString("csv#format.recordSepTwoDifferent"));
	    }
	    // Decide if this is a "normal" line ending, which has special meaning during parsing
	    this.normalLineEnding = true;
	    for (int i = 0; i < recordSep.length(); i++) {
		if (recordSep.charAt(i) != '\r' || recordSep.charAt(i) != '\n') {
		    this.normalLineEnding = false;
		    break;
		}
	    }
	    if (!normalLineEnding && recordSep.length() > 1)
		throw new IllegalArgumentException(Intl.getString("csv#format.recordSepStandard"));

	    this.recordSep = recordSep;
	    this.recordSepChar = recordSep.charAt(0);

	    return this;
	}

	/**
	 * Set the record separator to the given character.
	 *
	 * @param	recordSepChar	The new value for the record separator character for formatting and parsing.
	 *
	 * @return	The updated {@link CSVFormat} object.
	 */
	public CSVFormat withRecordSepChar(char recordSepChar) {
	    this.recordSep = Character.toString(recordSepChar);
	    this.recordSepChar = recordSepChar;
	    if (recordSepChar == '\r' || recordSepChar == '\n')
		this.normalLineEnding = true;
	    return this;
	}

	/**
	 * Set the record separator to the given well-known value.
	 *
	 * @param	separator	The new value for the record separator for formatting and parsing.
	 *
	 * @return	The updated {@link CSVFormat} object.
	 */
	public CSVFormat withSeparator(Separator separator) {
	    return withRecordSep(separator.getSeparator());
	}

	/**
	 * Set the escape character of an already existing format.
	 *
	 * @param	escapeChar	The new value for the escape character for formatting and parsing.
	 *
	 * @return	The updated {@link CSVFormat} object.
	 *
	 * @throws	IllegalArgumentException if this escape character is the same as the quote character.
	 */
	public CSVFormat withEscapeChar(char escapeChar) {
	    if (escapeChar == quoteChar)
		throw new IllegalArgumentException(Intl.getString("csv#format.escapeNotQuote"));
	    this.escapeChar = escapeChar;
	    if (escapeChar == '\0')
		this.disableEscape = true;
	    return this;
	}

	/**
	 * Set the "preserve whitespace" flag of an existing format.
	 *
	 * @param	flag	The new value of the "preserve whitespace" flag.
	 *
	 * @return	The updated {@link CSVFormat} object.
	 */
	public CSVFormat withPreserveWhitespace(boolean flag) {
	    this.preserveWhitespace = flag;
	    return this;
	}

	/**
	 * Set the "ignore empty lines" flag of an existing format.
	 *
	 * @param	flag	New value of the "ignore empty lines" flag.
	 *
	 * @return	The updated {@link CSVFormat} object.
	 */
	public CSVFormat withIgnoreEmptyLines(boolean flag) {
	    this.ignoreEmptyLines = flag;
	    return this;
	}

	/**
	 * Set the "always delimit strings" flag of an existing format.
	 *
	 * @param	flag	New value of the "always delimit strings" flag.
	 *
	 * @return	The updated {@link CSVFormat} object.
	 */
	public CSVFormat withAlwaysDelimitStrings(boolean flag) {
	    this.alwaysDelimitStrings = flag;
	    return this;
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

}

