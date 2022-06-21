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
 *  History:
 *      02-Dec-2021 (rlwhitcomb)
 *	    Initial coding.
 */
package info.rlwhitcomb.calc;


/**
 * An exception thrown from the "leave [expr]" statement.
 */
public class LeaveException extends RuntimeException
{
	/** The (optional) expression included with the statement. */
	private Object leftValue = null;

	/**
	 * Flag to say whether or not the value was included (separate so that
	 * the value itself can be null, but still be "included").
	 */
	private boolean valueIncluded = false;


	public LeaveException() {
	    super();
	}

	public LeaveException(final Object value) {
	    super();
	    leftValue = value;
	    valueIncluded = true;
	}

	/**
	 * @return Whether or not the value was included (does not depend on the
	 * value being non-null).
	 */
	public boolean hasValue() {
	    return valueIncluded;
	}

	/**
	 * @return The value given on the {@code "leave"} statement (if any).
	 */
	public Object getValue() {
	    return leftValue;
	}
}

