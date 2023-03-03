/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2008-2009,2014,2020-2023 Roger L. Whitcomb.
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
 *	Numeric Utility Library
 *
 * History:
 *  11-Nov-08 rlw  ---	Initial creation from code in ClientService.
 *  06-Jan-09 rlw  ---	Added Javadoc comments all around.
 *  24-Jul-14 rlw  ---	Add "parse" methods using the formats.
 *  14-Aug-14 rlw  ---	Cleanup "lint" warnings.
 *  14-Apr-20 rlw  ---	Cleanup and prepare for GitHub.
 *  09-Jul-21 rlw  ---	Make the class final and constructor private for a utility class.
 *  18-Feb-22 rlw  ---	Use Exceptions to get better error messages.
 *  14-Apr-22 rlw #273:	Move to "math" package.
 *  08-Jul-22 rlw #393:	Cleanup imports.
 *  12-Oct-22 rlw #513:	Move Logging to new package.
 *  16-Feb-23 rlw #244:	Move "formatWithSeparators" into here, add BigInteger version,
 *			and add variants with fewer params.
 */
package info.rlwhitcomb.math;

import info.rlwhitcomb.logging.Logging;
import info.rlwhitcomb.util.Exceptions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;


/**
 * Utility class specifically for numeric formatting for the statistics report.
 * <p><i>But, these are general enough and could be expanded for other uses later.</i>
 * <p> Uses non-thread-safe {@link NumberFormat} objects and does no synchronization
 * internally, so all these methods must be invoked in a thread-safe manner using
 * synchronization externally.
 */
public final class Num
{
	/** Used to format long integer values. Uses grouping to nicely display the numbers. */
	private static NumberFormat f1 = null;
	/** Used to format floating-point values. Uses grouping to nicely display the numbers
	 * and minimum of 3 decimal digits. */
	private static NumberFormat f2 = null;
	static {
	    f1 = NumberFormat.getIntegerInstance();
	    f1.setGroupingUsed(true);
	    f2 = NumberFormat.getInstance();
	    if (f2 instanceof DecimalFormat) {
		DecimalFormat df = (DecimalFormat)f2;
		df.setGroupingUsed(true);
		df.setDecimalSeparatorAlwaysShown(true);
		df.setMinimumFractionDigits(3);
	    }
	}


	/**
	 * Private constructor so no one can instantiate this as an object.
	 */
	private Num() {
	}

	/**
	 * Take base**n where n is a positive integer.
	 * <p> Uses shift/multiply method for fastest operation.
	 *
	 * @param	base	normally 10 or 2 (base number to get power of)
	 * @param	n	integer exponent (must be positive or 0)
	 * @return		base**n (if n == 0, result will be 1 as mathematically correct)
	 */
	public static long lpow(long base, int n) {
	    int bitMask = n;
	    long evenPower = base;
	    long result = 1;
	    while (bitMask != 0) {
		if ((bitMask & 1) != 0)
		    result *= evenPower;
		evenPower *= evenPower;
		bitMask >>>= 1;
	    }
	    return result;
	}

	/**
	 * Take base**n where n is a positive integer.
	 * <p> Uses shift/multiply method for fastest operation.
	 *
	 * @param	base	normally 10.0 or 2.0 (base number to get power of)
	 * @param	n	integer exponent (must be positive or 0)
	 * @return		base**n (if n == 0, result will be 1.0 as mathematically correct)
	 */
	public static double dpow(double base, int n) {
	    int bitMask = n;
	    double evenPower = base;
	    double result = 1.0;
	    while (bitMask != 0) {
		if ((bitMask & 1) != 0)
		    result *= evenPower;
		evenPower *= evenPower;
		bitMask >>>= 1;
	    }
	    return result;
	}

	/**
	 * Format a long integer value "nicely".
	 * <p> Takes the value, scales it (divides) by 10**scale and pads it to
	 * "places" characters long.
	 * <p><b>Note: this assumes the result will be displayed with a fixed-pitch font
	 * so that proportional spacing and display characteristics are not
	 * important.</b>
	 * <p> Uses the {@link #f1} object for formatting <b>(so this method must not be
	 * called by multiple threads at the same time)</b>.
	 *
	 * @param	value	value to be formatted
	 * @param	scale	power of 10 used to divide down the value (use 0 to
	 *			leave the value unchanged)
	 * @param	places	desired minimum width of output string (output will be
	 *			padded on the left to this minimum width)
	 * @return		formatted string value
	 */
	public static String fmt1(long value, int scale, int places) {
	    StringBuffer buf = new StringBuffer(places * 2);
	    FieldPosition pos = new FieldPosition(NumberFormat.INTEGER_FIELD);
	    f1.format(value / lpow(10, scale), buf, pos);
	    int pad = places - pos.getEndIndex();
	    while (pad-- > 0)
		buf.insert(0, ' ');
	    return buf.toString();
	}

	/**
	 * Format an unscaled long integer value "nicely".
	 * <p> Calls the general {@link #fmt1(long,int,int)} method with 0 for the
	 * "scale" parameter so the value is unchanged before formatting.
	 *
	 * @param	value	value to be formatted
	 * @param	places	minimum width of the formatted output string
	 * @return		formatted string
	 * @see		#fmt1(long,int,int)
	 */
	public static String fmt1(long value, int places) {
	    return fmt1(value, 0, places);
	}

	/**
	 * Format a floating-point value "nicely".
	 * <p> Scales the value by 10.0**scale and pads the result on the left
	 * to a minimum width of "places" characters.
	 * <p> Uses the {@link #f2} object for formatting <b>(so this method must not be
	 * called by multiple threads at the same time)</b>.
	 *
	 * @param	value	floating-point value to be formatted
	 * @param	scale	power of 10 used to divide down the value (to display
	 *			value in 1000s, use 3 for scale)
	 * @param	places	minimum width of output string
	 * @return		formatted string
	 */
	public static String fmt2(double value, int scale, int places) {
	    StringBuffer buf = new StringBuffer(places * 2);
	    FieldPosition pos = new FieldPosition(NumberFormat.INTEGER_FIELD);
	    f2.format(value / dpow(10.0, scale), buf, pos);
	    int pad = places - pos.getEndIndex();
	    while (pad-- > 0)
		buf.insert(0, ' ');
	    return buf.toString();
	}

	/**
	 * Parse out a long value from the given string (assuming the value was formatted
	 * by the {@link #fmt1} method).
	 *
	 * @param	value	string to be parsed
	 * @return		long value corresponding to the input
	 */
	public static long parse1(String value) {
	    try {
		return f1.parse(value).longValue();
	    }
	    catch (ParseException pe) {
		Logging.Error("Parsing error: %1$s", Exceptions.toString(pe));
	    }
	    return 0L;
	}

	/**
	 * Parse out a double value from the given string (assuming the value was formatted
	 * by the {@link #fmt2} method).
	 *
	 * @param	value	string to be parsed
	 * @return		double value
	 */
	public static double parse2(String value) {
	    try {
		return f2.parse(value).doubleValue();
	    }
	    catch (ParseException pe) {
		Logging.Error("Parsing error: %1$s", Exceptions.toString(pe));
	    }
	    return 0.0d;
	}

	/**
	 * Format an integer number with thousands separators.
	 *
	 * @param	value	An integer value to format.
	 * @return		The value formatted appropriately with thousands separators.
	 */
	public static String formatWithSeparators(final BigInteger value) {
	    return formatWithSeparators(new BigDecimal(value), true, Integer.MIN_VALUE);
	}

	/**
	 * Format an integer number with/without thousands separators.
	 *
	 * @param	value	An integer value to format.
	 * @param	sep	Whether or not to use separators.
	 * @return		The value formatted appropriately with thousands separators.
	 */
	public static String formatWithSeparators(final BigInteger value, final boolean sep) {
	    return formatWithSeparators(new BigDecimal(value), sep, Integer.MIN_VALUE);
	}

	/**
	 * Format an integer number with/without thousands separators.
	 *
	 * @param	value	An integer value to format.
	 * @param	sep	Whether or not to use separators.
	 * @param	scale	The minimum number of decimal digits to display (pad on the left
	 *			with zeros, before adding separators); {@code Integer.MIN_VALUE}
	 *			to ignore.
	 * @return		The value formatted appropriately with thousands separators.
	 */
	public static String formatWithSeparators(final BigInteger value, final boolean sep, final int scale) {
	    return formatWithSeparators(new BigDecimal(value), sep, scale);
	}

	/**
	 * Format a decimal/integer number with thousands separators.
	 *
	 * @param	value	A decimal value which could actually be an integer.
	 * @return		The value formatted appropriately with thousands separators.
	 */
	public static String formatWithSeparators(final BigDecimal value) {
	    return formatWithSeparators(value, true, Integer.MIN_VALUE);
	}

	/**
	 * Format a decimal/integer number with/without thousands separators.
	 *
	 * @param	value	A decimal value which could actually be an integer.
	 * @param	sep	Whether or not to use separators.
	 * @return		The value formatted appropriately with thousands separators.
	 */
	public static String formatWithSeparators(final BigDecimal value, final boolean sep) {
	    return formatWithSeparators(value, sep, Integer.MIN_VALUE);
	}

	/**
	 * Format a decimal/integer number with/without thousands separators.
	 *
	 * @param	value	A decimal value which could actually be an integer.
	 * @param	sep	Whether or not to use separators.
	 * @param	scale	The minimum number of decimal digits to display (pad on the left
	 *			with zeros, before adding separators); {@code Integer.MIN_VALUE}
	 *			to ignore.
	 * @return		The value formatted appropriately with thousands separators.
	 */
	public static String formatWithSeparators(final BigDecimal value, final boolean sep, final int scale) {
	    String formatString = "";
	    int valueScale = value.scale();
	    int valuePrec = value.precision();
	    int displayScale = scale;

	    // There are no digits right of the decimal point, so treat as an integer
	    if (valueScale <= 0) {
		if (scale != Integer.MIN_VALUE) {
		    if (sep)
			displayScale += (valuePrec - valueScale) / 3;
		    formatString = String.format("%%1$0%1$dd", displayScale);
		}
		else {
		    formatString = "%1$d";
		}
	    }
	    else {
		if (scale != Integer.MIN_VALUE) {
		    if (sep)
			displayScale += (valuePrec - valueScale) / 3;
		    formatString = String.format("%%1$0%1$d.%2$df", displayScale, valueScale);
		}
		else {
		    formatString = String.format("%%1$.%1$df", valueScale);
		}
	    }

	    if (sep && !formatString.isEmpty()) {
		formatString = formatString.replace("%1$", "%1$,");
	    }

	    if (formatString.isEmpty()) {
		return value.toPlainString();
	    }
	    else {
		if (formatString.endsWith("d"))
		    return String.format(formatString, value.toBigInteger());
		else
		    return String.format(formatString, value);
	    }
	}

}
