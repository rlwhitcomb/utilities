/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2023,2025 Roger L. Whitcomb.
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
 *	An integer that always keeps its maximum value.
 *
 * History:
 *  15-Dec-22 rlw ----	Initial coding.
 *  13-Dec-23 rlw ----	Add "next" method to get max value + 1; rename "pos" to "zero";
 *			Add "increment" and "reset" methods. Add "min" static method.
 *  14-Mar-25 rlw ----	Add constructor from a list of initial values.
 */
package info.rlwhitcomb.math;


/**
 * Instead of the rather verbose syntax: <code>int j = 0; j = Math.max(j, some_other_value);</code>
 * use one of these to say: <code>MaxInt j = MaxInt.zero(); j.set(some_other_value);</code>
 * <p> But really, the main purpose is to (hopefully) reduce copy/paste kind of errors, because
 * there is only one reference to the "max" variable per use, instead of two needed with just
 * {@link Math#max(int,int)}.
 */
public final class MaxInt extends Number
{
	/**
	 * The default minimum value (suitable for any signed value).
	 */
	private static final int DEFAULT_MINIMUM = Integer.MIN_VALUE;

	/**
	 * The value of this variable.
	 */
	private int value;

	/**
	 * The initial value (minimum), used by {@link #set()}, and {@link #reset()}.
	 */
	private final int initialValue;


	/**
	 * Construct one with the default minimum value.
	 */
	public MaxInt() {
	    value = initialValue = DEFAULT_MINIMUM;
	}

	/**
	 * Construct the default version with {@link #DEFAULT_MINIMUM} as the initial value.
	 *
	 * @return A new object with the default minimum value.
	 */
	public static MaxInt min() {
	    return new MaxInt();
	}

	/**
	 * Construct one suitable for positive values only.
	 *
	 * @return A new object with minimum value of zero.
	 */
	public static MaxInt zero() {
	    return new MaxInt(0);
	}

	/**
	 * Construct one with the given minimum value.
	 *
	 * @param v The starting minimum value.
	 */
	public MaxInt(final int v) {
	    value = initialValue = v;
	}

	/**
	 * Construct the maximum of a list of initial values.
	 *
	 * @param values The list of values to inspect.
	 */
	public MaxInt(final int... values) {
	    this();
	    for (int v : values)
		set(v);
	}

	/**
	 * Alternate static constructor for any minimum value.
	 *
	 * @param v The starting minimum value.
	 * @return  A new object with this value.
	 */
	public static MaxInt of(final int v) {
	    return new MaxInt(v);
	}

	/**
	 * Set the new value of this object to be the maximum of the existing value
	 * and this new value.
	 *
	 * @param v The new value to set.
	 * @return  This object again.
	 */
	public MaxInt set(final int v) {
	    value = Math.max(value, v);
	    return this;
	}

	/**
	 * Has the value been set above the initial minimum?
	 *
	 * @return Whether the value is above the minimum.
	 */
	public boolean set() {
	    return value > initialValue;
	}

	/**
	 * Reset the value to the initial value (to setup for another round of calculations).
	 *
	 * @return     The current object.
	 */
	public MaxInt reset() {
	    value = initialValue;
	    return this;
	}

	/**
	 * Increment the current value by one.
	 *
	 * @return     The current object.
	 */
	public MaxInt increment() {
	    value++;
	    return this;
	}

	/**
	 * Increment the current value by any amount.
	 *
	 * @param incr The amount to increment by.
	 * @return     The current object.
	 */
	public MaxInt increment(final int incr) {
	    value += incr;
	    return this;
	}

	/**
	 * Access the current value of this object.
	 *
	 * @return The current value (maximum of all the values set).
	 */
	public int get() {
	    return value;
	}

	/**
	 * Access the current value plus one.
	 *
	 * @return The current maximum value plus one.
	 */
	public int next() {
	    return value + 1;
	}

	@Override
	public byte byteValue() {
	    return Integer.valueOf(value).byteValue();
	}

	@Override
	public short shortValue() {
	    return Integer.valueOf(value).shortValue();
	}

	@Override
	public int intValue() {
	    return value;
	}

	@Override
	public long longValue() {
	    return Integer.valueOf(value).longValue();
	}

	@Override
	public float floatValue() {
	    return Integer.valueOf(value).floatValue();
	}

	@Override
	public double doubleValue() {
	    return Integer.valueOf(value).doubleValue();
	}
}
