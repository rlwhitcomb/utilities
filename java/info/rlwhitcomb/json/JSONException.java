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
 *	23-Mar-2022 (rlwhitcomb)
 *	    New constructor without the line number; different format for messages
 *	    in this case. Add "getMessage".
 */
package info.rlwhitcomb.json;

import info.rlwhitcomb.util.Exceptions;
import info.rlwhitcomb.util.Intl;


/**
 * An exception thrown during JSON parsing or writing that includes the
 * context for the error so that line information can be displayed.
 */
public class JSONException extends RuntimeException
{
	/**
	 * The saved line number in the source where the error occurred (can be
	 * {@code -1} for I/O exceptions unrelated to source position).
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
	    super(Exceptions.toString(cause), cause);
	    this.lineNumber = lineNo;
	}

	public JSONException(final Throwable cause) {
	    super(Exceptions.toString(cause), cause);
	    this.lineNumber = -1;
	}


	/**
	 * @return The line number where the exception occurred.
	 */
	public int getLine() {
	    return lineNumber;
	}


	/**
	 * Return a suitable message for this exception (different form if the line number
	 * was not specified).
	 *
	 * @return Suitable message.
	 */
	@Override
	public String getMessage() {
	    Throwable cause = getCause();
	    // Note: from constructors above, if "cause" is given, the message is already nicely formatted
	    // by Exceptions.toString, so no need to actually reference the cause itself, just the super message.
	    if (cause != null) {
		if (lineNumber < 0) {
		    return Intl.formatString("json#except.causeNoLine", super.getMessage());
		}
		else {
		    return Intl.formatString("json#except.message", lineNumber, super.getMessage());
		}
	    }
	    else {
		if (lineNumber < 0) {
		    return Intl.formatString("json#except.noCauseNoLine", super.getMessage());
		}
		else {
		    // Actually, this should never happen (from the constructors) unless cause
		    // is actually given as null
		    return Intl.formatString("json#except.noCause", lineNumber);
		}
	    }
	}

}

