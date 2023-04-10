/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2023 Roger L. Whitcomb.
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
 *  28-May-22 rlw  ---	Refactor into a separate file.
 *  10-Jan-23 rlw #558:	Add QUATERNION.
 *  03-Apr-23 rlw #263:	New conversion function for "buildFlatMap".
 *  08-Apr-23 rlw #601:	Add INTEGER for "gcd" and "lcm".
 */
package info.rlwhitcomb.calc;

import info.rlwhitcomb.math.BigFraction;
import info.rlwhitcomb.math.ComplexNumber;
import info.rlwhitcomb.math.Quaternion;
import info.rlwhitcomb.util.ClassUtil;


/**
 * Conversion modes for "buildValueList".
 */
public enum Conversion
{
	/** Convert all values to strings (for "exec"). */
	STRING,
	/** Convert all values to integers (for "lcm" or "gcd"). */
	INTEGER,
	/** Convert all values to decimal (for "sumof" or "productof" in decimal mode). */
	DECIMAL,
	/** Convert to fractions (rational mode). */
	FRACTION,
	/** Convert to complex numbers. */
	COMPLEX,
	/** Convert to quaternions. */
	QUATERNION,
	/** Leave values as they are (for "sort"). */
	UNCHANGED;


	/**
	 * Find a proper conversion given an object (from its type).
	 *
	 * @param obj	Typically the first object in a map, set, or list.
	 * @return	A simplified idea of the conversion to use for the
	 *		whole object based on the first value type.
	 */
	public static Conversion fromValue(final Object obj) {
	    if (obj instanceof Quaternion)
		return QUATERNION;
	    if (obj instanceof ComplexNumber)
		return COMPLEX;
	    if (obj instanceof BigFraction)
		return FRACTION;

	    if (ClassUtil.isInteger(obj))
		return INTEGER;

	    // Any remaining number that isn't an integer from above will be a decimal
	    if (obj instanceof Number)
		return DECIMAL;

	    return STRING;
	}

}
