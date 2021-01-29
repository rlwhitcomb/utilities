/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017,2019-2021 Roger L. Whitcomb.
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
 *	Class to do incremental parsing of a CSV file.
 *
 * History:
 *	27-Feb-2014 (rlwhitcomb)
 *	    Initial coding, based on many other CSV packages.
 *	03-Mar-2014 (rlwhitcomb)
 *	    Add processing for custom escape character and flag
 *	    for ignoring empty lines.
 *	04-Mar-2014 (rlwhitcomb)
 *	    Ignore BOM if present.
 *	04-Mar-2014 (rlwhitcomb)
 *	    Move error text to resources file.
 *	13-Mar-2014 (rlwhitcomb)
 *	    Add some more exceptions when unexpected EOF occurs.
 *	    Make helper method "getNextChar" to do common processing.
 *	07-Oct-2015 (rlwhitcomb)
 *	    Address Javadoc warnings found by Java 8.
 *	27-Jun-2016 (rlwhitcomb)
 *	    Implement left- and right-quote characters.
 *	24-May-2017 (rlwhitcomb)
 *	    Implement "quoting disabled" and the "None" quote option.
 *	28-Jun-2019 (rlwhitcomb) 9 months
 *	    Fix an NPE when EOF was right after a proper quote (thus null'ing
 *	    out the reader, but going back for another char after that).
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	21-Dec-2020 (rlwhitcomb)
 *	    Update obsolete Javadoc constructs.
 *	29-Jan-2021 (rlwhitcomb)
 *	    Use the new Intl exception subclasses and extended CSVException
 *	    for convenience.
 */
package info.rlwhitcomb.csv;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;

import info.rlwhitcomb.util.Intl;


/**
 * Implements the lowest level parsing of a CSV record, using the supplied
 * {@link CSVFormat} to define the various parameters for the parse.
 */
public class CSVParser
{
	/** The {@link CSVFormat} we will be using throughout. */
	private CSVFormat format = null;

	/** The {@link BufferedReader} we will be parsing. */
	private BufferedReader reader = null;

	/** Flag to say we've reached the end of a record, that is, we recognized the record separator. */
	private boolean endOfRecord = false;

	/** Flag to say we've reached the end of the input, that is, the underlying {@link Reader} said "end of file". */
	private boolean endOfInput = false;

	/** The next token to be returned. */
	private String nextToken = null;

	/** The current position in the input stream. */
	private long inputPos = 0L;

	/** The lookahead character (for quoted quote recognition). */
	private Integer lastChar = null;


	/**
	 * Construct a new parser using the given {@link Reader} and {@link CSVFormat}.
	 *
	 * @param	reader	A reader using whatever character set you like to convert
	 *			bytes to Unicode characters.  A {@link BufferedReader} will
	 *			be wrapped around it for use (unless, of course, you pass in
	 *			a {@code BufferedReader} yourself.
	 * @param	format	The {@link CSVFormat} to use to interpret the data.  If
	 *			{@code null} is passed, then a default format will be used.
	 * @throws	IllegalArgumentException if the {@code reader} is {@code null}.
	 */
	public CSVParser(Reader reader, CSVFormat format) {
	    if (reader == null)
		throw new Intl.IllegalArgumentException("csv#parser.readerNotNull");

	    if (reader instanceof BufferedReader)
		this.reader = (BufferedReader)reader;
	    else
		this.reader = new BufferedReader(reader);

	    if (format == null)
		this.format = new CSVFormat();
	    else
		this.format = format;
	}

	/**
	 * Are we at the of the record (i.e., just seen the record separator)?
	 *
	 * @return	{@code true} if we're at the end of record.
	 */
	public boolean atEndOfRecord() {
	    return endOfRecord;
	}

	/**
	 * Are we at the end of the input?
	 *
	 * @return	{@code true} if we're at the end of the input.
	 */
	public boolean atEndOfInput() {
	    return endOfInput;
	}

	/**
	 * Return the next available record element (if any).
	 *
	 * @return the next field or {@code null}.  Check the
	 * {@link #atEndOfRecord} and {@link #atEndOfInput} to decide
	 * what the {@code null} return means.
	 *
	 * @throws	CSVException on any error.
	 */
	public String getNextField()
		throws CSVException
	{
	    advance();
	    return nextToken;
	}

	/**
	 * Get the next character from the input reader.
	 * <p> Updates the {@link #inputPos} value and consumes
	 * the peekahead value {@link #lastChar} if set.
	 *
	 * @return	-1 at end of file, and will set the {@link #endOfInput}
	 *		and {@link #endOfRecord} flags in that case also, as
	 *		well as closing the {@link #reader}.
	 *
	 * @throws	IOException on errors reading the input.
	 */
	private int getNextChar()
		throws IOException
	{
	    int nextChar;
	    // Consume the lookahead character if available
	    if (lastChar != null) {
		nextChar = lastChar;
		lastChar = null;
	    }
	    else if (endOfInput) {
		return -1;
	    }
	    else {
		nextChar = reader.read();
		inputPos++;
	    }
	    if (nextChar == -1) {
		reader.close();
		reader = null;
		endOfInput = endOfRecord = true;
	    }
	    return nextChar;
	}

	/**
	 * Internal method to advance to the next token.
	 * Sets the {@link #nextToken}, {@link #endOfRecord} and
	 * {@link #endOfInput} flags appropriately.
	 *
	 * @throws	CSVException on any I/O or parsing errors.
	 */
	private void advance()
		throws CSVException
	{
	    try {
		nextToken = null;
		if (reader != null) {
		    endOfRecord = false;
		    StringBuffer buf = null;
		    boolean insideQuotes = false;
		    boolean sawQuotes = false;
		    for (;;) {
			int nextChar = getNextChar();
			// Ignore BOM (Byte-Order Mark) wherever it occurs (but typically only as first char of file)
			if (nextChar == 0xFEFF)
			    continue;
			if (nextChar == -1) {
			    if (insideQuotes)
				throw new CSVException("csv#parser.unterminatedToken", inputPos);
			    break;
			}
			else {
			    if (buf == null)
				buf = new StringBuffer();
			    if (insideQuotes) {
				if (!format.disableEscape && (char)nextChar == format.escapeChar) {
				    nextChar = getNextChar();
				    if (nextChar == -1) {
					throw new CSVException("csv#parser.unexpectedEOF", inputPos);
				    }
				    // Interpret the next character literally
				    buf.append((char)nextChar);
				}
				else if (!format.disableQuoting) {
				    if ((char)nextChar == format.quoteChar && !format.handedQuoting) {
					nextChar = getNextChar();
					if (nextChar == -1) {
					    // Found the end of the quoted string
					    insideQuotes = false;
					}
					else if ((char)nextChar == format.quoteChar) {
					    // Doubled quote inserts a single quote, but only if the left and right are the same
					    buf.append((char)nextChar);
					}
					else {
					    // Found the end of the quoted string
					    insideQuotes = false;
					    // Save the lookahead character for next time
					    lastChar = nextChar;
					}
				    }
				    else if (format.handedQuoting && (char)nextChar == format.rightQuoteChar) {
					// Found the end of the quoted string
					insideQuotes = false;
				    }
				    // Support the Quotes "ALL" choice
				    else if (format.quoteChar == Quotes.Constants.ALL_QUOTE_CHAR &&
					     Quotes.isValidEnd((char)nextChar)) {
					// Found the end of the quoted string
					insideQuotes = false;
				    }
				    else {
					buf.append((char)nextChar);
				    }
				}
				else {
				    buf.append((char)nextChar);
				}
			    }
			    else {
				if (!format.disableEscape && (char)nextChar == format.escapeChar) {
				    nextChar = getNextChar();
				    if (nextChar == -1) {
					throw new CSVException("csv#parser.unexpectedEOF", inputPos);
				    }
				    // Interpret the next character literally
				    buf.append((char)nextChar);
				    // Tricky bit here also:  if the next character is a normal line ending
				    // (such as \r or \n) then allow both of them to be escaped as if one
				    if (nextChar == '\r' || nextChar == '\n') {
					int ch = getNextChar();
					if (ch == -1) {
					    break;
					}
					if ((ch == '\r' || ch == '\n') && ch != nextChar) {
					    buf.append((char)ch);
					}
					else {
					    lastChar = ch;
					}
				    }
				    continue;
				}
				// Tricky bit here:  allow a record separator consisting of any of the
				// "normal" line endings to match any.  Meaning a CR or LF or CRLF as
				// the defined separator will match any of CR or LF or CRLF.
				if (format.normalLineEnding) {
				    if (nextChar == '\n' || nextChar == '\r') {
					int ch = getNextChar();
					if ((ch != '\n' && ch != '\r') || ch == nextChar) {
					    lastChar = ch;
					}
					endOfRecord = true;
					break;
				    }
				}
				else {
				    // Non-normal line endings must only be one character
				    if ((char)nextChar == format.recordSepChar) {
					endOfRecord = true;
					break;
				    }
				}
				if ((char)nextChar == format.fieldSepChar) {
				    break;
				}
				else if (Character.isWhitespace(nextChar)) {
				    buf.append((char)nextChar);
				}
				else if (!format.disableQuoting) {
				    if ((char)nextChar == format.quoteChar) {
					insideQuotes = sawQuotes = true;
				    }
				    // Support the "ALL" Quotes value
				    else if (format.quoteChar == Quotes.Constants.ALL_QUOTE_CHAR &&
					     Quotes.isValidStart((char)nextChar)) {
					insideQuotes = sawQuotes = true;
				    }
				    else {
					buf.append((char)nextChar);
				    }
				}
				else {
				    buf.append((char)nextChar);
				}
			    }
			}
		    }
		    if (buf != null) {
			// TODO: this isn't quite right if we had ,   "ab c d"   ,
			// but, wondering what the result should be if preserving whitespace is true
			if (format.preserveWhitespace || sawQuotes)
			    nextToken = buf.toString();
			else
			    nextToken = buf.toString().trim();
		    }
		}
	    }
	    catch (IOException ioe) {
		throw new CSVException(ioe, "csv#parser.readingException", inputPos);
	    }
	}

}

