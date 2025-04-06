/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023,2025 Roger L. Whitcomb.
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
 *	An integer that always keeps its minimum value.
 *
 * History:
 *  14-Dec-23 rlw ----	Clone from "MaxInt".
 *  14-Mar-25 rlw ----	Add constructor of a list of values.
 */
package info.rlwhitcomb.math;


/**
 * Instead of the rather verbose syntax: <code>int j = Integer.MAX_VALUE; j = Math.min(j, some_other_value);</code>
 * use one of these to say: <code>MinInt j = MinInt.max(); j.set(some_other_value);</code>
 * <p> But really, the main purpose is to (hopefully) reduce copy/paste kind of errors, because
 * there is only one reference to the "min" variable per use, instead of two needed with just
 * {@link Math#min(int,int)}.
 */
public final class MinInt extends Number
{
	/**
	 * The default maximum value (suitable for any signed value).
	 */
	private static final int DEFAULT_MAXIMUM = Integer.MAX_VALUE;

	/**
	 * The value of this variable.
	 */
	private int value;

	/**
	 * The initial value (maximum), used by {@link #set()}, and {@link #reset()}.
	 */
	private final int initialValue;


	/**
	 * Construct one with the default maximum value.
	 */
	public MinInt() {
	    value = initialValue = DEFAULT_MAXIMUM;
	}

	/**
	 * Construct the default version with {@link #DEFAULT_MAXIMUM} as the initial value.
	 *
	 * @return A new object with the default maximum value.
	 */
	public static MinInt max() {
	    return new MinInt();
	}

	/**
	 * Construct one suitable for negative values only.
	 *
	 * @return A new object with maximum value of zero.
	 */
	public static MinInt zero() {
	    return new MinInt(0);
	}

	/**
	 * Construct one with the given minimum value.
	 *
	 * @param v The starting minimum value.
	 */
	public MinInt(final int v) {
	    value = initialValue = v;
	}

	/**
	 * Construct the minimum of a list of values.
	 *
	 * @param values The set of values to inspect.
	 */
	public MinInt(final int... values) {
	    this();
	    for (int v : values)
		set(v);
	}

	/**
	 * Alternate static constructor for any maximum value.
	 *
	 * @param v The starting maximum value.
	 * @return  A new object with this value.
	 */
	public static MinInt of(final int v) {
	    return new MinInt(v);
	}

	/**
	 * Set the new value of this object to be the minimum of the existing value
	 * and this new value.
	 *
	 * @param v The new value to set.
	 * @return  This object again.
	 */
	public MinInt set(final int v) {
	    value = Math.min(value, v);
	    return this;
	}

	/**
	 * Has the value been set below the initial maximum?
	 *
	 * @return Whether the value is below the initial value.
	 */
	public boolean set() {
	    return value < initialValue;
	}

	/**
	 * Reset the value to the initial value (to setup for another round of calculations).
	 *
	 * @return     The current object.
	 */
	public MinInt reset() {
	    value = initialValue;
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
