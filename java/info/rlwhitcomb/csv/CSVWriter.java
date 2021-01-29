/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016,2019-2021 Roger L. Whitcomb.
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
 *	Class to facilitate writing out CSV-formatted files.
 *
 * History:
 *	27-Feb-2014 (rlwhitcomb)
 *	    Initial coding, based on many other CSV packages.
 *	04-Mar-2014 (rlwhitcomb)
 *	    Moved exception text to resources file.
 *	07-Oct-2015 (rlwhitcomb)
 *	    Address Javadoc warnings found by Java 8.
 *	27-Jun-2016 (rlwhitcomb)
 *	    Implement left- and right-quote characters.
 *	18-Dec-2019 (rlwhitcomb)
 *	    Implement "alwaysDelimitStrings" processing.
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	21-Dec-2020 (rlwhitcomb)
 *	    Update obsolete Javadoc constructs.
 *	29-Jan-2021 (rlwhitcomb)
 *	    Use new Intl Exception variants for convenience.
 */
package info.rlwhitcomb.csv;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;

import info.rlwhitcomb.util.Intl;


/**
 * Class used to write CSV-format files using a given (possibly non-standard)
 * format.
 */
public class CSVWriter
{
	/** The {@link BufferedWriter} we will use to write the output. */
	private BufferedWriter writer = null;

	/** The {@link CSVFormat} specifying the delimiters, etc. used to format the output. */
	private CSVFormat format = null;

	/** The computed left-hand quote character for the beginning of fields that must be delimited. */
	private char leftQuote;

	/** The computed right-hand quote character for the end of fields that must be delimited. */
	private char rightQuote;

	/**
	 * Construct a new writer using the given {@link Writer} and {@link CSVFormat}.
	 * <p> Note: if the given {@code Writer} is not a {@code BufferedWriter} then one
	 * of the latter will be created to wrap the given {@code Writer} (to increase
	 * performance).
	 *
	 * @param	writer	The sink for the data to be written to.
	 * @param	format	The (optional) format for data to be written.  If {@code null}
	 *			then the default options will be used.
	 *
	 * @throws	IllegalArgumentException if the writer is {@code null}.
	 */
	public CSVWriter(Writer writer, CSVFormat format) {
	    if (writer == null)
		throw new Intl.IllegalArgumentException("csv#writer.writerNotNull");
	    if (format == null)
		this.format = new CSVFormat();	// use all defaults
	    else
		this.format = format;

	    char defaultQuote = Quotes.DOUBLE.leftChar();
	    leftQuote = this.format.quoteChar == '\0' ? defaultQuote : this.format.quoteChar;
	    rightQuote = this.format.handedQuoting ? this.format.rightQuoteChar :
			    (this.format.quoteChar == '\0' ? defaultQuote : this.format.quoteChar);

	    if (writer instanceof BufferedWriter)
		this.writer = (BufferedWriter)writer;
	    else
		this.writer = new BufferedWriter(writer);
	}

	/**
	 * Write out the next record according to the given format.
	 * If the record is {@code null} the output writer will be closed.
	 *
	 * @param	record	The complete record to be written.
	 *
	 * @throws	CSVException if the writer has already been closed or there
	 *		are other I/O errors..
	 */
	public void writeNextRecord(CSVRecord record)
		throws CSVException
	{
	    if (writer == null)
		throw new CSVException("csv#writer.noWriteAfterClose");
	    try {
		if (record == null) {
		    writer.flush();
		    writer.close();
		    writer = null;
		}
		else {
		    int i = 0;
		    for (Object field : record) {
			writeNextField(i++, field);
		    }
		    if (format.normalLineEnding) {
			writer.write('\n');
		    }
		    else {
			writer.write(format.recordSep);
		    }
		}
	    }
	    catch (IOException ioe) {
		throw new CSVException(ioe, "csv#writer.writingException");
	    }
	}

	/**
	 * Private method to write one field, respecting format settings.
	 *
	 * @param	num	The current field number (0-based).
	 * @param	field	The field value to write.
	 *
	 * @throws	CSVException if there are I/O errors.
	 */
	private void writeNextField(int num, Object field)
		throws CSVException
	{
	    try {
		if (num > 0)
		    writer.write(format.fieldSepChar);

		boolean isRealString = false;
		String result = null;

		if (field != null) {
		    if (field instanceof BigDecimal) {
			result = ((BigDecimal)field).toPlainString();
		    }
		    else if (field instanceof String) {
			result = (String)field;
			isRealString = true;
		    }
		    else {
			result = field.toString();
		    }

		    // Real "String" values have a flag to always delimit or not
		    // Otherwise we should scan the result to determine delimiting
		    boolean requiresDelimiting = format.alwaysDelimitStrings && isRealString;
		    if (!requiresDelimiting) {
			// Look for: whitespace, the quote char, the field separator,
			// or the record separator.
			for (int i = 0; i < result.length(); i++) {
			    char ch = result.charAt(i);
			    if (ch == format.fieldSepChar ||
				(format.normalLineEnding && ch == '\r' || ch == '\n') ||
				(!format.normalLineEnding && ch == format.recordSepChar) ||
				ch == rightQuote ||
				Character.isWhitespace(ch)) {
				requiresDelimiting = true;
				break;
			    }
			}
		    }
		    if (requiresDelimiting) {
			writer.write(leftQuote);
			for (int i = 0; i < result.length(); i++) {
			    char ch = result.charAt(i);
			    // If the quotes are the same, then an embedded quote needs doubling
			    // else if the quotes are different, then an embedded right quote
			    // needs escaping instead.
			    if (ch == rightQuote) {
				if (format.handedQuoting)
				    writer.write(format.escapeChar);
				else
				    writer.write(rightQuote);
			    }
			    writer.write(ch);
			}
			writer.write(rightQuote);
		    }
		    else {
			writer.write(result);
		    }
		}
	    }
	    catch (IOException ioe) {
		throw new CSVException(ioe, "csv#writer.writingException");
	    }
	}

}

