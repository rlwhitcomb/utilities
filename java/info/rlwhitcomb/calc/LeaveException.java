/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2023 Roger L. Whitcomb.
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
 * History:
 *  02-Dec-21 rlw ----	Initial coding.
 *  15-Aug-22 rlw ----	Add static singleton instance for the no-value case.
 *  12-Jan-23 rlw ----	New method to return the instance.
 *  05-Dec-23 rlw #600	Add label value and accessor.
 */
package info.rlwhitcomb.calc;


/**
 * An exception thrown from the "leave [expr]" statement.
 */
public class LeaveException extends RuntimeException
{
	/**
	 * The singleton instance to throw if there is no value needed.
	 */
	private static final LeaveException INSTANCE = new LeaveException();


	/** The (optional) expression included with the statement. */
	private Object leftValue;

	/**
	 * The label (or not) specified on the leave statement.
	 */
	private String blockLabel;


	/**
	 * Flag to say whether or not the value was included (separate so that
	 * the value itself can be null, but still be "included").
	 */
	private boolean valueIncluded = false;


	public LeaveException() {
	    super();
	}

	public LeaveException(final Object value, final String label) {
	    super();
	    leftValue = value;
	    valueIncluded = true;
	    blockLabel = label;
	}

	/**
	 * The singleton instance of this exception that has no value or label specified.
	 *
	 * @return The single instance.
	 */
	public static final LeaveException instance() {
	    return INSTANCE;
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

	/**
	 * @return The block label (if any) specified on the {@code leave} statement.
	 */
	public String getLabel() {
	    return blockLabel;
	}

}

