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
 *	02-May-2022 (rlwhitcomb)
 *	    #68: Allow negative indexing (offset from end of array).
 *	08-May-2022 (rlwhitcomb)
 *	    #315: Add "addAll" method.
 *	15-May-2022 (rlwhitcomb)
 *	    #315: Add "remove" method, and copy constructor.
 *	18-May-2022 (rlwhitcomb)
 *	    #315: Add "isEmpty" method.
 *	21-May-2022 (rlwhitcomb)
 *	    #327: Convert "List" constructor to Collection.
 *	25-May-2022 (rlwhitcomb)
 *	    #348: Make all methods package private.
 *	11-Jun-2022 (rlwhitcomb)
 *	    #365: Add "immutable" flag and checks.
 *	21-Jun-2022 (rlwhitcomb)
 *	    #314: Derive from CollectionScope.
 *	08-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 */
package info.rlwhitcomb.calc;

import info.rlwhitcomb.util.Intl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Scope that represents an array or list of values, accessible by index.
 *
 * @param <T> Type of value stored in the array.
 */
class ArrayScope<T> extends CollectionScope
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
	    values = new ArrayList<>();
	}

	/**
	 * Construct given the initial values.
	 *
	 * @param initialValues The initial set of values.
	 */
	@SuppressWarnings("unchecked")
	ArrayScope(final T... initialValues) {
	    super(Type.ARRAY);
	    values = new ArrayList<>(initialValues.length);
	    for (T value : initialValues) {
		values.add(value);
	    }
	}

	/**
	 * Construct given another list.
	 *
	 * @param list The other list to copy.
	 */
	ArrayScope(final ArrayScope<T> list) {
	    this(list.values);
	}

	/**
	 * Construct given the initial value list.
	 *
	 * @param initialValues Collection of initial values.
	 */
	ArrayScope(final Collection<? extends T> initialValues) {
	    super(Type.ARRAY);
	    values = new ArrayList<>(initialValues);
	}

	/**
	 * Check if this array is immutable, and throw if so. Called from every operation
	 * that might modify the contents.
	 *
	 * @throws IllegalStateException if the array is marked immumtable.
	 */
	void checkImmutable() {
	    if (isImmutable())
		throw new Intl.IllegalStateException("calc#immutableArray");
	}

	/**
	 * Access a value by index.
	 *
	 * @param index The 0-based index value to retrieve.
	 * @return      The value at that index, which could be {@code null} if no value has been assigned yet.
	 * @throws      IndexOutOfBoundsException if the index is negative.
	 */
	T getValue(final int index) {
	    int size = values.size();
	    int pos = index < 0 ? index + size : index;

	    if (pos < 0)
		throw new Intl.IndexOutOfBoundsException("calc#indexNegative", pos);

	    return pos < size ? values.get(pos) : null;
	}

	/**
	 * Set the value at the given index.
	 *
	 * @param index The index in the list to set.
	 * @param value The value to put there.
	 * @throws      IndexOutOfBoundsException if the index is negative.
	 */
	void setValue(final int index, final T value) {
	    checkImmutable();

	    int size = values.size();
	    int pos = index < 0 ? index + size : index;

	    if (pos < 0)
		throw new Intl.IndexOutOfBoundsException("calc#indexNegative", pos);

	    // Fill up any intervening values up to the index
	    for (int i = size; i < pos; i++) {
		values.add(null);
	    }
	    if (pos < size) {
		values.set(pos, value);
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
	void insert(final int index, final T value) {
	    checkImmutable();

	    values.add(index, value);
	}

	/**
	 * Add the next value to the list.
	 *
	 * @param value The next value to add to the end of the list.
	 */
	void add(final T value) {
	    checkImmutable();

	    values.add(value);
	}

	/**
	 * Add all the values from the given collection to the list.
	 *
	 * @param c The collection to add to this list.
	 * @return  Whether the list changed as a result of this operation.
	 */
	boolean addAll(final Collection<? extends T> c) {
	    checkImmutable();

	    return values.addAll(c);
	}

	/**
	 * Remove the specified element from the list and return it.
	 *
	 * @param index Index of the element to remove.
	 * @return      The previous element at that position.
	 */
	T remove(final int index) {
	    checkImmutable();

	    return values.remove(index);
	}

	/**
	 * Access the underlying list.
	 *
	 * @return The underlying list we are wrapping.
	 */
	List<T> list() {
	    return values;
	}

	/**
	 * Access the size of the list.
	 *
	 * @return Size of the underlying list.
	 */
	int size() {
	    return values.size();
	}

	/**
	 * Is the list empty?
	 *
	 * @return {@code true} for an empty list
	 */
	boolean isEmpty() {
	    return values.isEmpty();
	}

}
