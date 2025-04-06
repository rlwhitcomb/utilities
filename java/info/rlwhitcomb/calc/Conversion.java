/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 Roger L. Whitcomb.
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
 *	Conversion modes for "buildValueList".
 *
 * History:
 *  28-May-22 rlw ----	Refactor into a separate file.
 *  10-Jan-23 rlw #558	Add QUATERNION.
 *  03-Apr-23 rlw #263	New conversion function for "buildFlatMap".
 *  08-Apr-23 rlw #601	Add INTEGER for "gcd" and "lcm".
 *  11-Feb-24 rlw #65	New param for "fromValue"; reorder the enum values.
 *  26-Mar-25 rlw ----	Move "isInteger" from ClassUtil to MathUtil.
 */
package info.rlwhitcomb.calc;

import info.rlwhitcomb.math.BigFraction;
import info.rlwhitcomb.math.ComplexNumber;
import info.rlwhitcomb.math.MathUtil;
import info.rlwhitcomb.math.Quaternion;


/**
 * Conversion modes for "buildValueList".
 */
public enum Conversion
{
	/** Leave values as they are (for "sort"). */
	UNCHANGED,
	/** Convert all values to strings (for "exec"). */
	STRING,
	/** Convert all values to decimal (for "sumof" or "productof" in decimal mode). */
	DECIMAL,
	/** Convert all values to integers (for "lcm" or "gcd"). */
	INTEGER,
	/** Convert to fractions (rational mode). */
	FRACTION,
	/** Convert to complex numbers. */
	COMPLEX,
	/** Convert to quaternions. */
	QUATERNION;


	/**
	 * Find a proper conversion given an object (from its type).
	 *
	 * @param obj		Typically the first object in a map, set, or list.
	 * @param rational	Whether we're in "rational" mode or calculating numbers as fractions.
	 * @return		A simplified idea of the conversion to use for the
	 *			whole object based on the first value type.
	 */
	public static Conversion fromValue(final Object obj, final boolean rational) {
	    if (obj instanceof Quaternion)
		return QUATERNION;
	    if (obj instanceof ComplexNumber)
		return COMPLEX;
	    if (rational || (obj instanceof BigFraction))
		return FRACTION;

	    if (MathUtil.isInteger(obj))
		return INTEGER;

	    // Any remaining number that isn't an integer from above will be a decimal
	    if (obj instanceof Number)
		return DECIMAL;

	    return STRING;
	}

}
