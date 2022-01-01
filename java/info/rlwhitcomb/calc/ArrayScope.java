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
 *      Data structures for Calc to hold user-defined function definitions,
 *      local symbol tables, etc.
 *
 *  History:
 *	06-Oct-2021 (rlwhitcomb)
 *	    Initial coding.
 *	07-Oct-2021 (rlwhitcomb)
 *	    Use generic data types.
 *	28-Dec-2021 (rlwhitcomb)
 *	    #128: Add "insert" method for "pad".
 */
package info.rlwhitcomb.calc;

import java.util.ArrayList;
import java.util.List;

import info.rlwhitcomb.util.Intl;


/**
 * Scope that represents an array or list of values, accessible by index.
 *
 * @param <T> Type of value stored in the array.
 */
class ArrayScope<T> extends Scope
{
	/**
	 * The list / array of values contained in this scope, accessible by index value.
	 */
	private List<T> values;


	/**
	 * Default constructor, giving an initially empty list.
	 */
	ArrayScope() {
	    super(Type.ARRAY);
	    this.values = new ArrayList<>();
	}

	/**
	 * Construct given the initial values.
	 *
	 * @param initialValues The initial set of values.
	 */
	@SuppressWarnings("unchecked")
	ArrayScope(final T... initialValues) {
	    super(Type.ARRAY);
	    this.values = new ArrayList<>(initialValues.length);
	    for (T value : initialValues) {
		values.add(value);
	    }
	}

	/**
	 * Construct given the initial value list.
	 *
	 * @param initialValueList List of initial values.
	 */
	ArrayScope(final List<T> initialValueList) {
	    super(Type.ARRAY);
	    this.values = new ArrayList<>(initialValueList.size());
	    for (T value : initialValueList) {
		values.add(value);
	    }
	}

	/**
	 * Access a value by index.
	 *
	 * @param index The 0-based index value to retrieve.
	 * @return      The value at that index, which could be {@code null} if no value has been assigned yet.
	 * @throws      IndexOutOfBoundsException if the index is negative.
	 */
	public T getValue(final int index) {
	    if (index < 0)
		throw new Intl.IndexOutOfBoundsException("calc#indexNegative", index);

	    if (values == null || index >= values.size())
		return null;

	    return values.get(index);
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param index The index in the list to set.
	 * @param value The value to put there.
	 * @throws      IndexOutOfBoundsException if the index is negative.
	 */
	public void setValue(final int index, final T value) {
	    if (index < 0)
		throw new Intl.IndexOutOfBoundsException("calc#indexNegative", index);

	    // Fill up any intervening values up to the index
	    for (int i = values.size(); i < index; i++) {
		values.add(null);
	    }
	    if (index < values.size()) {
		values.set(index, value);
	    }
	    else {
		values.add(value);
	    }
	}

	/**
	 * Insert a value at the given index, moving any existing values further out.
	 *
	 * @param index Where in the list to insert the new value. Values from index to length
	 *              will move to positions index + 1, index + 2, etc.
	 * @param value The new value to insert at position index.
	 */
	public void insert(final int index, final T value) {
	    values.add(index, value);
	}

	/**
	 * Add the next value to the list.
	 *
	 * @param value The next value to add to the end of the list.
	 */
	public void add(final T value) {
	    values.add(value);
	}

	/**
	 * Access the underlying list.
	 *
	 * @return The underlying list we are wrapping.
	 */
	public List<T> list() {
	    return values;
	}

	/**
	 * Access the size of the list.
	 *
	 * @return Size of the underlying list.
	 */
	public int size() {
	    return values.size();
	}

}
