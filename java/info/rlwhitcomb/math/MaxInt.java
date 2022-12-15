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
 *	An integer that always keeps its maximum value.
 *
 * History:
 *  15-Dec-22 rlw  ---	Initial coding.
 */
package info.rlwhitcomb.math;


/**
 * Instead of the rather verbose syntax: <code>int j = 0; j = Math.max(j, some_other_value);</code>
 * use one of these to say: <code>MaxInt j = MaxInt.pos(); j.set(some_other_value);</code>
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
	 * The initial value (minimum), used by {@link #set()}.
	 */
	private final int initialValue;


	/**
	 * Construct one with the default minimum value.
	 */
	public MaxInt() {
	    value = initialValue = DEFAULT_MINIMUM;
	}

	/**
	 * Construct one suitable for positive values only.
	 *
	 * @return A new object with minimum value of zero.
	 */
	public static MaxInt pos() {
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
