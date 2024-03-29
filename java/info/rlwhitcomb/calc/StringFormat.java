/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022,2024 Roger L. Whitcomb.
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
 *	Flags used during calls to "toStringValue" (to reduce the number of invariant
 *	parameters passed around everywhere).
 *
 * History:
 *  14-Jul-22 rlw #412	Initial coding.
 *  24-Jul-22 rlw #412	Add "skipLevels" value.
 *  30-Jan-24 rlw #649	New constructor.
 */
package info.rlwhitcomb.calc;


/**
 * Parameter values for the {@code toStringValue} methods -- invariants that get passed
 * down to each level from the outermost.
 */
class StringFormat
{
	/** The default amount of space to indent each level while "pretty printing." */
	public static final String DEFAULT_INCREMENT = "  ";

	/** Whether or not strings should be quoted in the final result. */
	boolean quotes;

	/** Whether we are "pretty printing" the output -- extra formatting for lists and objects. */
	boolean pretty;

	/** Whether there should be extra spaces at various places to make the output more legible. */
	boolean extraSpace;

	/** Whether numeric values should be formatted with thousands separators. */
	boolean separators;

	/** The amount of space to increment each level of indentation. */
	String increment;

	/** The number of outer levels to skip before printing/displaying (0 = print all levels). */
	int skipLevels;


	/**
	 * Construct and set the defaults, used by most cases.
	 *
	 * @param settings Some of these values are usually set by the global settings object.
	 */
	StringFormat(final Settings settings) {
	    quotes = settings.quoteStrings;
	    pretty = false;
	    extraSpace = true;
	    separators = settings.separatorMode;
	    increment = DEFAULT_INCREMENT;
	    skipLevels = 0;
	}

	/**
	 * Construct with global quote setting and individual separators value.
	 *
	 * @param settings The global settings (for quoting).
	 * @param sep      The local value for thousands separators.
	 */
	StringFormat(final Settings settings, final boolean sep) {
	    quotes = settings.quoteStrings;
	    pretty = false;
	    extraSpace = true;
	    separators = sep;
	    increment = DEFAULT_INCREMENT;
	    skipLevels = 0;
	}

	/**
	 * Construct with individual quote setting and global separators value.
	 *
	 * @param q        Setting for quoting strings.
	 * @param settings Global setting for separators.
	 */
	StringFormat(final boolean q, final Settings settings) {
	    quotes = q;
	    pretty = false;
	    extraSpace = true;
	    separators = settings.separatorMode;
	    increment = DEFAULT_INCREMENT;
	    skipLevels = 0;
	}

	/**
	 * Construct without global settings, but with those values specified individually.
	 *
	 * @param q   Value for quoting strings.
	 * @param sep Value for numeric thousands separators.
	 */
	StringFormat(final boolean q, final boolean sep) {
	    quotes = q;
	    pretty = false;
	    extraSpace = true;
	    separators = sep;
	    increment = DEFAULT_INCREMENT;
	    skipLevels = 0;
	}

	/**
	 * Construct for pretty printing.
	 *
	 * @param q   Whether to quote strings or not.
	 * @param p   Whether to do "pretty printing".
	 * @param sep Value for thousands separators.
	 */
	StringFormat(final boolean q, final boolean p, final boolean sep) {
	    quotes = q;
	    pretty = p;
	    extraSpace = true;
	    separators = sep;
	    increment = DEFAULT_INCREMENT;
	    skipLevels = 0;
	}

	/**
	 * Construct for extra spacing ({@code @s} and {@code @f} formats).
	 *
	 * @param q   Whether to quote strings or not.
	 * @param p   Whether to do "pretty printing".
	 * @param es  Extra spaces?
	 * @param sep Value for thousands separators.
	 */
	StringFormat(final boolean q, final boolean p, final boolean es, final boolean sep) {
	    quotes = q;
	    pretty = p;
	    extraSpace = es;
	    separators = sep;
	    increment = DEFAULT_INCREMENT;
	    skipLevels = 0;
	}

	/**
	 * Construct and set all the values individually.
	 *
	 * @param q    Value for quoting strings.
	 * @param p    Whether to do pretty printing.
	 * @param es   Whether extra spaces should be added.
	 * @param sep  Use numeric separators?
	 * @param inc  Amount of space to increment at each level ({@code null} &#x21E8; {@link #DEFAULT_INCREMENT}).
	 * @param skip Number of outer levels to skip before printing.
	 */
	StringFormat(final boolean q, final boolean p, final boolean es, final boolean sep, final String inc, final int skip) {
	    quotes = q;
	    pretty = p;
	    extraSpace = es;
	    separators = sep;
	    increment = (inc == null) ? DEFAULT_INCREMENT : inc;
	    skipLevels = skip <= 0 ? 0 : skip;
	}

}

