/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019,2020-2023 Roger L. Whitcomb.
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
 *	A dynamically sized array class.
 *
 *  History:
 *	04-Jun-2019 (rlwhitcomb)
 *	    Created for dynamic array support.
 *	01-Feb-2021 (rlwhitcomb)
 *	    Rework the get and put methods; add exception for negative indexes.
 *	    Change the value returned by "size()" and add "capacity()" method.
 *	    Redo the way initial allocation is done.
 *	26-Mar-2021 (rlwhitcomb)
 *	    Move some methods from NumericUtil to MathUtil.
 *	03-Dec-2021 (rlwhitcomb)
 *	    #123: New constructor that takes varargs.
 *	13-Dec-2021 (rlwhitcomb)
 *	    #123: Redo this new constructor by reworking "internalClass" as
 *	    "Class<?>" and all users.
 *	14-Apr-2022 (rlwhitcomb)
 *	    #273: Move math-related classes to "math" package.
 *	09-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 *	11-Sep-2023 (rlwhitcomb)
 *	    Add "copy" constructor; final parameters.
 *	27-Nov-2023 (rlwhitcomb)
 *	    #627: New constructor with just size given. New logic in reallocate().
 *	14-Dec-2023 (rlwhitcomb)
 *	    Use MaxInt appropriately.
 */
package info.rlwhitcomb.util;

import info.rlwhitcomb.math.MathUtil;
import info.rlwhitcomb.math.MaxInt;

import java.lang.reflect.Array;


/**
 * A class to implement dynamically sized arrays.
 * <p> Note: Resizing is always upward (that is, we never recover
 * "unused" space if that ever occurs). And sizes are always powers of two
 * (that is, min 16, then 32, 64, 128, etc.) This (hopefully) keeps
 * expensive reallocation and copying to a minimum.
 */
public class DynamicArray<T>
{
	/** A "suitable" default size when one is not specified by the constructor. */
	private static final int DEFAULT_SIZE = 16;

	/** The actual array used for storage (of objects, not primitives). */
	private volatile T[] internalArray;
	/** Class of objects to be stored in this array. */
	private transient Class<?> internalClass;
	/** The "size" of this array, which is the largest index yet seen by {@link #put}. */
	private MaxInt largestIndex = MaxInt.of(-1);


	/**
	 * Construct a dynamic array which stores objects of the given type, and
	 * default size.
	 *
	 * @param clazz Class of objects to store in this array.
	 */
	public DynamicArray(final Class<T> clazz) {
	    init(clazz, 0);
	}

	/**
	 * Construct a dynamic array which stores objects of the given type, with
	 * the given (initial) size.  The array can always be sized larger if the
	 * need arises.
	 *
	 * @param clazz Class of objects to store in this array.
	 * @param size The initial size of the array (but always expandable). If this is
	 * less or equal zero then the {@link #DEFAULT_SIZE} will be used instead.
	 */
	public DynamicArray(final Class<T> clazz, final int size) {
	    init(clazz, size);
	}

	/**
	 * Construct a dynamic array of {@link Object} of the given (initial) size.
	 *
	 * @param size The initial size of the array. The default size will be used if a value
	 * less or equal zero is given.
	 */
	public DynamicArray(final int size) {
	    init(Object.class, size);
	}

	/**
	 * Initialize this array with the given values. Note: they should all be the same type.
	 *
	 * @param values The initial values for the array.
	 */
	@SuppressWarnings("unchecked")
	public DynamicArray(final T... values) {
	    if (values == null || values.length == 0) {
		init(Object.class, 0);
	    }
	    else {
		init(values[0].getClass(), values.length);
		System.arraycopy(values, 0, internalArray, 0, values.length);
		largestIndex.set(values.length - 1);
	    }
	}

	/**
	 * Initialize this array from another dynamic array.
	 *
	 * @param array The other array to copy.
	 */
	public DynamicArray(final DynamicArray<T> array) {
	    this(array.internalArray);
	}

	/**
	 * Initialize the array to store the given class of objects and allocate
	 * the given size internal array.
	 *
	 * @param clazz Class of objects to store in this array.
	 * @param size The initial size of the array (but always expandable).
	 */
	private void init(final Class<?> clazz, final int size) {
	    internalClass = clazz;

	    reallocate(size <= 0 ? DEFAULT_SIZE : size);
	}

	/**
	 * Reallocate the internal array to fit this new size.
	 *
	 * @param newSize The new (presumably larger) size required for the array.
	 */
	private void reallocate(final int newSize) {
	    int roundedSize = MathUtil.roundUpPowerTwo(newSize);

	    if (internalArray == null || roundedSize > internalArray.length) {
		@SuppressWarnings("unchecked")
		final T[] newArray = (T[]) Array.newInstance(internalClass, roundedSize);

		if (internalArray != null)
		    System.arraycopy(internalArray, 0, newArray, 0, internalArray.length);

		internalArray = newArray;
	    }
	}

	/**
	 * @return The current size of the array, that is, the largest
	 * index seen by {@link #put} plus one.
	 */
	public int size() {
	    return largestIndex.next();
	}

	/**
	 * @return The current capacity of the array, without needing to reallocate.
	 */
	public int capacity() {
	    return internalArray.length;
	}

	/**
	 * @return The value at the given index. If the index is greater or equal the
	 * existing array size, return {@code null}.
	 *
	 * @param index Zero-based index into the array.
	 */
	public T get(final int index) {
	    if (index < 0)
		throw new Intl.IndexOutOfBoundsException("util#dyn.indexLessThanZero", index);

	    if (index >= internalArray.length)
		return null;

	    return internalArray[index];
	}

	/**
	 * Set the new value at the given index.
	 *
	 * @param index The zero-based index position to set the value for.
	 * @param value The new value to set there.
	 * @return The already existing value (or null if the array needs resizing)
	 * at the given index position.
	 */
	public T put(final int index, final T value) {
	    if (index < 0)
		throw new Intl.IndexOutOfBoundsException("util#dyn.indexLessThanZero", index);

	    T oldValue = null;
	    if (index >= internalArray.length) {
		reallocate(index + 1);
		internalArray[index] = value;
	    }
	    else {
		oldValue = internalArray[index];
		internalArray[index] = value;
	    }

	    largestIndex.set(index);

	    return oldValue;
	}
}
