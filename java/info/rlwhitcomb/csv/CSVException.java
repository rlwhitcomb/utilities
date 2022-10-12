/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015,2020-2022 Roger L. Whitcomb.
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
 *	Class to represent exceptional conditions occurring in the CSV package.
 *
 * History:
 *	27-Feb-2014 (rlwhitcomb)
 *	    Initial coding, based on many other CSV packages.
 *	13-Mar-2014 (rlwhitcomb)
 *	    Log these exceptions at the point of construction -- they often
 *	    don't get reported to the UI so we need some kind of record.
 *	07-Oct-2015 (rlwhitcomb)
 *	    Address Javadoc warnings found by Java 8.
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	21-Dec-2020 (rlwhitcomb)
 *	    Update obsolete Javadoc constructs.
 *	29-Jan-2021 (rlwhitcomb)
 *	    Use Intl methods for all messages.
 *	12-Oct-2022 (rlwhitcomb)
 *	    #513: Move Logging to new package.
 */
package info.rlwhitcomb.csv;

import info.rlwhitcomb.logging.Logging;
import info.rlwhitcomb.util.Intl;


/**
 * A checked exception occurring in the CSV package.
 */
public class CSVException extends Exception
{
	/**
	 * Construct a new exception with {@code null} as the detail message.
	 */
	public CSVException() {
	    super();
	    Logging.Except(this);
	}

	/**
	 * Construct a new exception with the specified detail message.
	 *
	 * @param	messageKey	Message key for details.
	 */
	public CSVException(final String messageKey) {
	    super(Intl.getString(messageKey));
	    Logging.Except(this);
	}

	/**
	 * Construct a new exception with the specified detail message.
	 *
	 * @param	formatKey	Message key for details.
	 * @param	args		Arguments to use in formatting the message.
	 */
	public CSVException(final String formatKey, final Object... args) {
	    super(Intl.formatString(formatKey, args));
	    Logging.Except(this);
	}

	/**
	 * Construct a new exception with the specified cause.
	 *
	 * @param	cause	The underlying cause.
	 */
	public CSVException(Throwable cause) {
	    super(cause);
	    Logging.Except(this);
	}

	/**
	 * Construct a new exception with the specified message and cause.
	 * <p> Note: the parameter order is different than normal to accommodate
	 * the format arguments.
	 *
	 * @param	cause		The underlying cause of this exception.
	 * @param	messageKey	Key for the detail message.
	 * @param	args		Arguments for the message format.
	 */
	public CSVException(final Throwable cause, final String messageKey, final Object... args) {
	    super(Intl.formatString(messageKey, args), cause);
	    Logging.Except(this);
	}

}

