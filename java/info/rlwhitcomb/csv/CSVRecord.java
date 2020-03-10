/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016,2019-2020 Roger L. Whitcomb.
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
 *	Class to hold a complete record of a CSV file.
 *
 * History:
 *	27-Feb-2014 (rlwhitcomb)
 *	    Initial coding, based on many other CSV packages.
 *	03-Mar-2014 (rlwhitcomb)
 *	    Add helper method to check for an empty record and
 *	    a "get" method to get the indexed field and a "clear"
 *	    method.
 *	04-Mar-2014 (rlwhitcomb)
 *	    Moved exception text to resources file.
 *	07-Oct-2015 (rlwhitcomb)
 *	    Address Javadoc warnings found by Java 8.
 *	29-Jun-2016 (rlwhitcomb)
 *	    Add public method to add a new field to this record.
 *	18-Dec-2019 (rlwhitcomb)
 *	    Change field storage to Object instead of String so
 *	    we can properly implement "alwaysDelimitStrings" property.
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 */
package info.rlwhitcomb.csv;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import info.rlwhitcomb.util.Intl;


/**
 * Holds one record of a CSV file.  If the file had a header row, then
 * the header names can be used to access the record values via the map.
 * Otherwise the map keys are just Integer strings.  Alternatively the
 * fields can be accessed via the iterator or as an array of strings.
 */
public class CSVRecord implements Iterator<Object>, Iterable<Object>
{
	private Map<String, Object> recordMap = new LinkedHashMap<>();
	private int nextWrite = 0;
	private int nextRead = 0;
	private int length = 0;
	private String[] headerKeys = null;

	/**
	 * Construct a new (empty) record.
	 */
	public CSVRecord() {
	}

	/**
	 * Sets the header keys for this record.
	 * @param	headerKeys	List of header keys for the record.
	 */
	public void setHeaderKeys(String[] headerKeys) {
	    this.headerKeys = headerKeys;
	}

	/**
	 * Internal method to add fields to the record.
	 *
	 * @param	key	name of the current field, or <tt>null</tt>
	 *			to use the default name, which will be the next
	 *			element of the header keys array (if any) or the
	 *			next integer.
	 * @param	field	Value for this field.
	 */
	protected void addField(String key, Object field) {
	    String thisKey = key;
	    if (thisKey == null) {
		if (headerKeys == null) {
		    thisKey = String.valueOf(nextWrite++);
		}
		else {
		    thisKey = headerKeys[nextWrite++];
		}
	    }
	    recordMap.put(thisKey, field);
	    length++;
	}

	/**
	 * @return The length of the input record (number of fields).
	 */
	public int size() {
	    return length;
	}

	/**
	 * The {@link Iterable} interface.
	 */
	@Override
	public Iterator<Object> iterator() {
	    nextRead = 0;
	    return this;
	}

	/**
	 * Is there another field available in the record?
	 */
	@Override
	public boolean hasNext() {
	    return nextRead < length;
	}

	/**
	 * Get the next available field in the record.
	 *
	 * @return	The next field.
	 *
	 * @throws	NoSuchElementException at the end of the record.
	 */
	@Override
	public Object next()
		throws NoSuchElementException
	{
	    if (nextRead >= length)
		throw new NoSuchElementException();
	    if (headerKeys == null)
		return recordMap.get(String.valueOf(nextRead++));
	    else
		return recordMap.get(headerKeys[nextRead++]);
	}

	/**
 	 * The (optional) remove method of {@link Iterator} interface.
	 */
	@Override
	public void remove() {
	    throw new IllegalStateException(Intl.getString("csv#removeNotSupported"));
	}

	/**
	 * Get the fields of the record in a single array.
	 *
	 * @return	All the fields of the current record.
	 */
	public Object[] getFields() {
	    Object[] fields = new Object[length];
	    int i = 0;
	    for (Object value : recordMap.values()) {
		fields[i++] = value;
	    }
	    return fields;
	}

	/**
	 * Get the map of the current fields, keyed by the header names (or integers
	 * if no header was present).
	 *
	 * @return	The current map of fields.
	 */
	public Map<String, Object> getRecordMap() {
	    return recordMap;
	}

	/**
	 * Access the indexed field.
	 *
	 * @param	index	Which field to access.
	 *
	 * @return	The value of the given field.
	 *
	 * @throws	IndexOutOfBoundsException when appropriate.
	 */
	public Object get(int index) {
	    if (index < 0 || index >= length)
		throw new IndexOutOfBoundsException(Intl.formatString("csv#record.indexOutOfRange", index, length - 1));

	    if (headerKeys == null)
		return recordMap.get(String.valueOf(index));
	    else
		return recordMap.get(headerKeys[index]);
	}

	/**
	 * Add the new value as the next field in the record.
	 *
	 * @param	obj	Object for the next field.
	 */
	public void add(Object obj) {
	    addField(null, obj);
	}

	/**
	 * Is this an empty record (consisting of no fields, or just one empty field)?
	 *
	 * @return	{@code true} if the above criteria are met.
	 */
	public boolean isEmpty() {
	    if (length == 0) {
		return true;
	    }
	    if (length == 1) {
		Object obj0 = get(0);
		if (obj0 == null) {
		    return true;
		}
		else if (obj0 instanceof String) {
		    return ((String)obj0).isEmpty();
		}
	    }
	    return false;
	}

	/**
	 * Clear any fields we might have already added.  Header keys will not be cleared.
	 */
	public void clear() {
	    length = nextRead = nextWrite = 0;
	    recordMap.clear();
	}

}

