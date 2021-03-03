/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015,2020-2021 Roger L. Whitcomb.
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
 *	Class to facilitate reading a complete CSV formatted file.
 *
 * History:
 *	27-Feb-2014 (rlwhitcomb)
 *	    Initial coding, based on many other CSV packages.
 *	03-Mar-2014 (rlwhitcomb)
 *	    Add processing for "ignore empty lines" option.
 *	    Implement Iterator interfaces.
 *	04-Mar-2014 (rlwhitcomb)
 *	    Move exception text to resources file.
 *	16-Sep-2014 (rlwhitcomb)
 *	    Fix "next" to set currentRecord null at end of file.
 *	07-Oct-2015 (rlwhitcomb)
 *	    Address Javadoc warnings found by Java 8.
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	21-Dec-2020 (rlwhitcomb)
 *	    Update obsolete Javadoc constructs.
 *	29-Jan-2021 (rlwhitcomb)
 *	    Use new Exception variants using Intl for convenience.
 *	02-Mar-2021 (rlwhitcomb)
 *	    Implement new "hasHeaderRow" logic.
 */
package info.rlwhitcomb.csv;

import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import info.rlwhitcomb.util.Intl;


/**
 * Read a complete CSV formatted file into a series of fields organized
 * into records.  There may be one or more header rows.  The options for
 * reading are given by a {@link CSVFormat} object passed to the constructor.
 */
public class CSVReader implements Iterator<CSVRecord>, Iterable<CSVRecord>
{
	/** The internal parser class used to separate the fields. */
	private CSVParser parser = null;

	/** The internal format we're using to do the parse. */
	private CSVFormat format = null;

	/** The header keys if the format says we have a header row. */
	private Object[] headerKeys = null;

	/** For the iterator interface, the next record to return. */
	private CSVRecord currentRecord = null;

	/** Flag to say we've exhausted the input. */
	private boolean endOfInputReached = false;

	/**
	 * Construct a new reader for the given data using the given options.
	 *
	 * @param	reader	The input reader where we're getting data.
	 * @param	format	The formatting/parsing parameters.
	 */
	public CSVReader(Reader reader, CSVFormat format) {
	    this.parser = new CSVParser(reader, format);
	    this.format = format;
	}

	/**
	 * Fetch the next record from the input reader.
	 *
	 * @return	{@code null} for the end of input or
	 *		the next {@link CSVRecord} parsed from
	 *		the input.
	 *
	 * @throws	CSVException on any I/O or parsing errors.
	 */
	public CSVRecord getNextRecord()
		throws CSVException
	{
	    CSVRecord record = new CSVRecord();
	    String field;

	    record.setHeaderKeys(headerKeys);

	  retryLoop:
	    while (true) {
		while ((field = parser.getNextField()) != null) {
		    record.addField(null, field);

		    if (parser.atEndOfRecord())
			break;
		}

		if (format.hasHeaderRow && headerKeys == null) {
		    headerKeys = record.getFields();
		    record = new CSVRecord();
		    record.setHeaderKeys(headerKeys);

		    continue retryLoop;
		}

		if ((endOfInputReached = parser.atEndOfInput()) == true)
		    if (record.size() == 0)
			return null;

		if (format.ignoreEmptyLines && record.isEmpty())
		    record.clear();
		else
		    break retryLoop;
	    }

	    return record;
	}

	/**
	 * The {@link Iterable} interface.
	 */
	@Override
	public Iterator<CSVRecord> iterator() {
	    try {
		currentRecord = getNextRecord();
	    }
	    catch (CSVException csve) {
		throw new RuntimeException(csve);
	    }
	    return this;
	}

	/**
	 * Part of the {@link Iterator} interface.
	 */
	@Override
	public boolean hasNext() {
	    return currentRecord != null;
	}

	/**
	 * Part of the {@link Iterator} interface.
	 */
	@Override
	public CSVRecord next() {
	    CSVRecord record = currentRecord;
	    try {
		if (!endOfInputReached)
		    currentRecord = getNextRecord();
		else
		    currentRecord = null;
	    }
	    catch (CSVException csve) {
		throw new RuntimeException(csve);
	    }
	    return record;
	}

	/**
 	 * The (optional) remove method of {@link Iterator} interface.
	 */
	@Override
	public void remove() {
	    throw new Intl.IllegalStateException("csv#removeNotSupported");
	}

}

