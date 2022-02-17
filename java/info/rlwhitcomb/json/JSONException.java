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
 *	General exceptions for JSON processing.
 *
 *  History:
 *      16-Feb-2022 (rlwhitcomb)
 *	    #196: Initial coding from other packages.
 */
package info.rlwhitcomb.json;

import info.rlwhitcomb.util.ExceptionUtil;


/**
 * An exception thrown during JSON parsing or writing that includes the
 * context for the error so that line information can be displayed.
 */
public class JSONException extends RuntimeException
{
	/**
	 * The saved line number in the source where the error occurred.
	 */
	private int lineNumber;


	public JSONException(final String message, final Throwable cause, final int lineNo) {
	    super(message, cause);
	    this.lineNumber = lineNo;
	}

	public JSONException(final String message, final int lineNo) {
	    super(message);
	    this.lineNumber = lineNo;
	}

	public JSONException(final Throwable cause, final int lineNo) {
	    super(ExceptionUtil.toString(cause), cause);
	    this.lineNumber = lineNo;
	}

	/**
	 * @return The line number where the exception occurred.
	 */
	public int getLine() {
	    return lineNumber;
	}
}

