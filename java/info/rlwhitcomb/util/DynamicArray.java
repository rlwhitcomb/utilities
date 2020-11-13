/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Roger L. Whitcomb.
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
 */
package info.rlwhitcomb.util;

import java.lang.reflect.Array;

/**
 * A class to implement dynamically sized arrays.
 * <p> Note: Resizing is always upward (that is, we never recover
 * "unused" space if that ever occurs). And sizes are always powers of two
 * (that is, min 16, then 32, 64, 128, etc.) This (hopefully) keeps
 * expensive reallocation to a minimum.
 */
public class DynamicArray<T>
{
	/** A "suitable" default size when one is not specified by the constructor. */
	private static final int DEFAULT_SIZE = 16;

	/** The actual array used for storage (of objects, not primitives). */
	private volatile T[] internalArray;
	/** Class of objects to be store in this array. */
	private Class<T> internalClass;

	/**
	 * Construct a dynamic array which stores objects of the given type, and
	 * default size.
	 *
	 * @param clazz Class of objects to store in this array.
	 */
	public DynamicArray(Class<T> clazz) {
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
	public DynamicArray(Class<T> clazz, int size) {
	    init(clazz, size);
	}

	/**
	 * Initialize the array to store the given class of objects and allocate
	 * the given size internal array.
	 *
	 * @param clazz Class of objects to store in this array.
	 * @param size The initial size of the array (but always expandable).
	 */
	private void init(Class<T> clazz, int size) {
	    int roundedSize = NumericUtil.roundUpPowerTwo(size <= 0 ? DEFAULT_SIZE : size);
	    @SuppressWarnings("unchecked")
	    final T[] array = (T[]) Array.newInstance(clazz, roundedSize);
	    this.internalArray = array;
	    this.internalClass = clazz;
	}

	/**
	 * Reallocate the internal array to fit this new size.
	 *
	 * @param newSize The new (presumably larger) size required for the array.
	 */
	private void reallocate(int newSize) {
	    T[] oldArray = internalArray;
	    int roundedSize = NumericUtil.roundUpPowerTwo(newSize);
	    @SuppressWarnings("unchecked")
	    final T[] newArray = (T[]) Array.newInstance(internalClass, roundedSize);
	    System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
	    this.internalArray = newArray;
	}

	/**
	 * @return The current size of the array.
	 */
	public int size() {
	    return this.internalArray.length;
	}

	/**
	 * @return The value at the given index. If the index is greater or equal the
	 * existing array size, return {@code null}.
	 *
	 * @param index Zero-based index into the array.
	 */
	public T get(int index) {
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
	public T put(int index, T value) {
	    T oldValue = null;
	    try {
		oldValue = internalArray[index];
		internalArray[index] = value;
	    }
	    catch (RuntimeException re) {
		if (index >= internalArray.length) {
		    reallocate(index + 1);
		    this.internalArray[index] = value;
		}
		else {
		    throw re;
		}
	    }
	    return oldValue;
	}
}
