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
 *	Generic data structure for holding two values with possibly different types.
 *
 * History:
 *	28-Dec-2021 (rlwhitcomb)
 *	    #183: Initial coding.
 */
package info.rlwhitcomb.util;


/**
 * Immutable storage for two different values.
 *
 * @param <U> type of first value
 * @param <V> type of second value
 */
public class Pair<U, V>
{
	/**
	 * The first stored value.
	 */
	private final U first;

	/**
	 * The second stored value.
	 */
	private final V second;

	/**
	 * Supply the two values to store.
	 *
	 * @param one The first value.
	 * @param two The second value.
	 */
	public Pair(final U one, final V two) {
	    first = one;
	    second = two;
	}

	/**
	 * Access the first value.
	 *
	 * @return The stored first/left value.
	 */
	public U getFirst() {
	    return first;
	}

	/**
	 * Access the second/right value.
	 *
	 * @return The stored second/right value.
	 */
	public V getSecond() {
	    return second;
	}

}

