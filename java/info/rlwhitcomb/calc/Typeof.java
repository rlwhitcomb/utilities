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
 *      Enumeration of the Calc data types (for "typeof" and "cast").
 *
 *  History:
 *	19-Jan-2022 (rlwhitcomb)
 *	    Initial coding.
 */
package info.rlwhitcomb.calc;


/**
 * An enumeration of the possible data types in Calc.
 * <p> Returned from the {@code "typeof"} function and used
 * by the {@code "cast"} function.
 */
enum Typeof
{
	/**
	 * A null value (typeless).
	 */
	NULL,

	/**
	 * The most general form, a string value.
	 */
	STRING,

	/**
	 * A numeric value with no fractional part.
	 */
	INTEGER,

	/**
	 * A numeric value with a fractional part.
	 */
	FLOAT,

	/**
	 * A true rational fraction.
	 */
	FRACTION,

	/**
	 * Boolean value ({@code true} or {@code false}).
	 */
	BOOLEAN,

	/**
	 * One of our array objects ({@link ArrayScope}).
	 */
	ARRAY,

	/**
	 * One of our object/map objects ({@link ObjectScope}).
	 */
	OBJECT,

	/**
	 * Something else (somehow we missed a type that we actually use).
	 */
	UNKNOWN;


	/**
	 * Get the expression value, which is the lower case equivalent
	 * of the defined name.
	 *
	 * @return The value to be used in results.
	 */
	String getValue() {
	    return toString().toLowerCase();
	}

	/**
	 * Return the corresponding value given the (lowercase, but it doesn't matter)
	 * string value.
	 *
	 * @param stringValue The result of {@link #getValue} now used to convert back.
	 * @return One of our values.
	 * @throws IllegalArgumentException if the value can't be converted.
	 */
	public static Typeof fromString(final String stringValue) {
	    return valueOf(stringValue.trim().toUpperCase());
	}

}

