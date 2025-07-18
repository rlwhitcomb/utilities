/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2024 Roger L. Whitcomb.
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
 * History:
 *  21-Jun-21 rlw ----	Initial coding.
 *  25-Jun-22 rlw #314	Add "diff".
 *  08-Jul-22 rlw #393	Cleanup imports.
 *  15-Aug-22 rlw #440	Move "size()" up to CollectionScope.
 *  08-Jan-23 rlw #592	Move "isEmpty()" to CollectionScope.
 *  16-May-23 rlw ----	New "addAll" method from a Collection.
 *  27-Sep-23 rlw #630	New indexing capability.
 *  07-Mar-24 rlw #661	New methods for "union" and "intersect".
 *  08-Mar-24 rlw #657	Use ArrayList instead of array for indexed access;
 *			new accessor for the values as a list.
 *  13-Mar-24 rlw #661	New "from" static function to convert from other collections.
 *  18-Jul-25 rlw #738	Implement "valueList" base method by refactoring "list";
 *			rename "valueList" variable to "valuesAsList".
 */
package info.rlwhitcomb.calc;

import info.rlwhitcomb.util.Intl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


/**
 * Scope that represents a set of unique values.
 *
 * @param <T> Type of value stored in the set.
 */
class SetScope<T extends Object> extends CollectionScope
{
	/**
	 * The set of values contained in this scope, accessible by iterator or "in" operation.
	 */
	private Set<T> values;

	/**
	 * The current set of values, as an array (for convenience in indexing). This is only
	 * present (allocated) as needed, and not constantly maintained (for performance reasons).
	 */
	private transient List<Object> valuesAsList = new ArrayList<>();


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
	    // Invalidate the list copy now that we (potentially) have a new member
	    valuesAsList.clear();
	}

	/**
	 * Add all the values from the given set to this one.
	 *
	 * @param set The new set to add to this one.
	 * @return    Whether the set changed as a result of this operation.
	 */
	boolean addAll(final SetScope<T> set) {
	    checkImmutable();

	    valuesAsList.clear();
	    return values.addAll(set.values);
	}

	/**
	 * Add all the values from the given collection to this set.
	 *
	 * @param c The new collection to add to this set.
	 * @return  Whether the set changed as a result of this operation.
	 */
	boolean addAll(final Collection<T> c) {
	    checkImmutable();

	    valuesAsList.clear();
	    return values.addAll(c);
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
	    valuesAsList.clear();
	}

	/**
	 * Get the indexed value out of the set.
	 * <p> Since we maintain the set using {@link LinkedHashSet} we can guarantee
	 * the ordering, so this operation makes sense where it normally would not.
	 *
	 * @param index The specific element index.
	 * @return      The typed object at that index in the set.
	 */
	@SuppressWarnings("unchecked")
	T get(final int index) {
	    if (valuesAsList.isEmpty()) {
		valuesAsList.addAll(values);
	    }

	    return (T) valuesAsList.get(index);
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
	 * Compute the union of this set and another collection.
	 *
	 * @param c Another collection to "union" with this set.
	 * @return  A new set containing the members of this set along
	 *          with the members of the other collection.
	 */
	SetScope<T> union(final CollectionScope c) {
	    if (c.equals(CollectionScope.EMPTY))
		return this;

	    SetScope<T> result = new SetScope<T>(values);

	    if (c instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<? extends T> array = (ArrayScope<? extends T>) c;
		result.values.addAll(array.list());
	    }
	    else if (c instanceof ObjectScope) {
		ObjectScope map = (ObjectScope) c;
		@SuppressWarnings("unchecked")
		Collection<? extends T> values = (Collection<? extends T>) map.values();
		result.values.addAll(values);
	    }
	    else if (c instanceof SetScope) {
		@SuppressWarnings("unchecked")
		SetScope<? extends T> set = (SetScope<? extends T>) c;
		result.values.addAll(set.set());
	    }

	    return result;
	}

	/**
	 * Compute the intersection of this set and another collection.
	 *
	 * @param c Another collection to intersect with this set.
	 * @return  A new set containing the members of this set which are
	 *          also contained in the other collection.
	 */
	SetScope<T> intersect(final CollectionScope c) {
	    if (c.equals(CollectionScope.EMPTY))
		return new SetScope<T>();

	    SetScope<T> result = new SetScope<T>(values);

	    if (c instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<Object> array = (ArrayScope<Object>) c;
		result.values.retainAll(array.list());
	    }
	    else if (c instanceof ObjectScope) {
		ObjectScope map = (ObjectScope) c;
		result.values.retainAll(map.values());
	    }
	    else if (c instanceof SetScope) {
		@SuppressWarnings("unchecked")
		SetScope<Object> set = (SetScope<Object>) c;
		result.values.retainAll(set.set());
	    }

	    return result;
	}

	/**
	 * Access the values as a list.
	 *
	 * @return Indexable list of the values.
	 */
	@Override
	protected List<Object> valueList() {
	    if (valuesAsList.isEmpty() && !values.isEmpty())
		valuesAsList.addAll(values);

	    return valuesAsList;
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
	@Override
	protected int size() {
	    return values.size();
	}

	/**
	 * Is the set empty?
	 *
	 * @return {@code true} for an empty set
	 */
	@Override
	protected boolean isEmpty() {
	    return values.isEmpty();
	}

	/**
	 * Convert any {@link CollectionScope} object to one of us.
	 *
	 * @param <T> Type of object in this set.
	 * @param coll Any collection to convert to a set.
	 * @return     The set of values from that collection.
	 */
	static <T> SetScope<T> from(final CollectionScope coll) {
	    if (coll.equals(EMPTY))
		return new SetScope<T>();

	    if (coll instanceof SetScope) {
		@SuppressWarnings("unchecked")
		SetScope<T> set = (SetScope<T>) coll;
		return set;
	    }
	    if (coll instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<T> array = (ArrayScope<T>) coll;
		return new SetScope<T>(array.list());
	    }
	    if (coll instanceof ObjectScope) {
		ObjectScope map = (ObjectScope) coll;
		@SuppressWarnings("unchecked")
		SetScope<T> set = new SetScope(map.keyObjectSet());
		return set;
	    }
	    return null;
	}

}
