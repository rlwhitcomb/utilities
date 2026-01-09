/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022,2026 Roger L. Whitcomb.
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
 *	Enumeration of the modes for the trigonometric functions in Calc.
 *
 * History:
 *  26-Jan-22 rlw ----	Moved out of CalcObjectVisitor.
 *  01-Feb-22 rlw #115	Add "getFrom" method.
 *  24-Aug-22 rlw #447	Add "GRADS" mode.
 *  05-Jan-26 rlw #799	Fix NPE on null/empty inputs to "getFrom"; use our own
 *			exception for invalid values.
 */
package info.rlwhitcomb.calc;

import info.rlwhitcomb.util.CharUtil;
import info.rlwhitcomb.util.Intl;


/**
 * The mode used for doing trigonometric calculations.
 */
public enum TrigMode
{
	/**
	 * Angles are measured / returned in degrees (0 - 360).
	 */
	DEGREES,

	/**
	 * Angles are measured / returned in radians (0 - 2𝛑).
	 */
	RADIANS,

	/**
	 * Angles are measured / returned in gradians (0 - 400).
	 */
	GRADS;


	/**
	 * Convert from arbitrary object (basically its string representation)
	 * into one of these mode objects.
	 *
	 * @param obj Input object to convert (cannot be {@code null} or an empty string).
	 * @return    The corresponding trig mode, if possible.
	 * @throws    IllegalArgumentException if the input is null or an empty string or
	 *            its string representation is an invalid value.
	 * @see       #valueOf
	 */
	public static TrigMode getFrom(final Object obj) {
	    if (obj instanceof TrigMode)
		return (TrigMode) obj;

	    if (CharUtil.isNullOrEmpty(obj))
		throw new Intl.IllegalArgumentException("calc#trigNullEmpty");

	    String name = obj.toString();
	    try {
		return valueOf(name.toUpperCase());
	    }
	    catch (IllegalArgumentException iae) {
		throw new Intl.IllegalArgumentException("calc#trigUnknown", name);
	    }
	}

}

