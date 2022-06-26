/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Roger L. Whitcomb.
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
 *	Data structure for Calc to hold a set of unique values, with operations
 *	such as intersection, union, etc.
 *
 *  History:
 *	21-Jun-2021 (rlwhitcomb)
 *	    Initial coding.
 *	25-Jun-2022 (rlwhitcomb)
 *	    #314: Add "diff".
 */
package info.rlwhitcomb.calc;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import info.rlwhitcomb.util.Intl;


/**
 * Scope that represents a set of unique values.
 *
 * @param <T> Type of value stored in the set.
 */
class SetScope<T> extends CollectionScope
{
	/**
	 * The set of values contained in this scope, accessible by iterator or "in" operation.
	 */
	private Set<T> values;


	/**
	 * Default constructor, giving an initially empty set.
	 */
	SetScope() {
	    super(Type.SET);
	    values = new LinkedHashSet<>();
	}

	/**
	 * Construct given the initial values.
	 *
	 * @param initialValues The initial set of values.
	 */
	@SuppressWarnings("unchecked")
	SetScope(final T... initialValues) {
	    super(Type.SET);
	    values = new LinkedHashSet<>(initialValues.length);
	    for (T value : initialValues) {
		values.add(value);
	    }
	}

	/**
	 * Construct given another set.
	 *
	 * @param set The other set to copy.
	 */
	SetScope(final SetScope<T> set) {
	    this(set.values);
	}

	/**
	 * Construct given the initial value list.
	 *
	 * @param initialValues Collection of initial values.
	 */
	SetScope(final Collection<? extends T> initialValues) {
	    super(Type.SET);
	    values = new LinkedHashSet<>(initialValues);
	}

	/**
	 * Check if this set is immutable, and throw if so. Called from every operation
	 * that might modify the contents.
	 *
	 * @throws IllegalStateException if the set is marked immumtable.
	 */
	void checkImmutable() {
	    if (isImmutable())
		throw new Intl.IllegalStateException("calc#immutableSet");
	}

	/**
	 * Add a new value to the set.
	 *
	 * @param value The new value to add to the set.
	 */
	void add(final T value) {
	    checkImmutable();

	    values.add(value);
	}

	/**
	 * Add all the values from the given set to this one.
	 *
	 * @param set The new set to add to this one.
	 * @return    Whether the set changed as a result of this operation.
	 */
	boolean addAll(final SetScope<T> set) {
	    checkImmutable();

	    return values.addAll(set.values);
	}

	/**
	 * Is the given object already a member of this set?
	 *
	 * @param value The object to search for in the set.
	 * @return      Whether the object is already a member.
	 */
	boolean isMember(final T value) {
	    return values.contains(value);
	}

	/**
	 * Remove the given object from the set.
	 *
	 * @param value The object to remove from the set.
	 */
	void remove(final T value) {
	    checkImmutable();

	    values.remove(value);
	}

	/**
	 * Compute the difference between this set and the other collection.
	 *
	 * @param c Another collection to compute the difference of.
	 * @return  The difference between this set and the other collection.
	 */
	SetScope<T> diff(final CollectionScope c) {
	    if (c.equals(CollectionScope.EMPTY))
		return this;

	    SetScope<T> result = new SetScope<T>(values);

	    if (c instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<Object> array = (ArrayScope<Object>) c;
		result.values.removeAll(array.list());
	    }
	    else if (c instanceof ObjectScope) {
		ObjectScope map = (ObjectScope) c;
		result.values.removeAll(map.values());
	    }
	    else if (c instanceof SetScope) {
		@SuppressWarnings("unchecked")
		SetScope<Object> set = (SetScope<Object>) c;
		result.values.removeAll(set.set());
	    }

	    return result;
	}

	/**
	 * Access the underlying set.
	 *
	 * @return The underlying set we are wrapping.
	 */
	Set<T> set() {
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
	 * Is the set empty?
	 *
	 * @return {@code true} for an empty set
	 */
	boolean isEmpty() {
	    return values.isEmpty();
	}

}
