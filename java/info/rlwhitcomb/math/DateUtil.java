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
 *	Date Utility library.
 *
 * History:
 *  24-Sep-22 rlw #426:	Initial coding from existing code in CalcObjectVisitor
 *			plus new code for "date()" function.
 *  08-Oct-22 rlw #504:	Allow "y-m-d" form (one "y" only); fix bad resource keys.
 */
package info.rlwhitcomb.math;

import info.rlwhitcomb.util.CharUtil;
import info.rlwhitcomb.util.Exceptions;
import info.rlwhitcomb.util.Intl;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeParseException;


/**
 * Utility class for date functions, including utility functions to parse dates from
 * strings, plus methods to construct from individual values.
 */
public final class DateUtil
{
	/** Strict ISO-8601 format for dates, suitable for parsing into a {@link LocalDate} value. */
	private static final String ISO_8601_DATE = "%1$04d-%2$02d-%3$02d";

	/** ISO-8601 pattern for dates. */
	private static final String ISO_8601_FORMAT = "YYYY-MM-DD";

	/** Number of days in each month (non-leap years) (index is month - 1). */
	private static final int[] DAYS_PER_MONTH = {
		31,	// January
		28,	// February
		31,	// March
		30,	// April
		31,	// May
		30,	// June
		31,	// July
		31,	// August
		30,	// September
		31,	// October
		30,	// November
		31	// December
	};


	/**
	 * Enumeration of possible field formats (used by the {@link #valueOf} method).
	 */
	private static enum Fmt
	{
		/** Year of any number of digits. */
		YR,
		/** Year of exactly 4 digits. */
		Y4,
		/** Year of exactly 2 digits. */
		Y2,
		/** Month, 1 or 2 digits. */
		MN,
		/** Month, exactly 2 digits. */
		M2,
		/** Day, 1 or 2 digits. */
		DY,
		/** Day, exactly 2 digits. */
		D2
	}


	/** Private constructor since this is a static class. */
	private DateUtil() {
	}


	/**
	 * Is this year a leap year?
	 *
	 * @param year The year in question.
	 * @return Whether the year is a leap year, according to the ISO proleptic conventions.
	 */
	public static boolean isLeapYear(final int year) {
	    return Year.isLeap(year);
	}

	/**
	 * Format month, day, year to ISO-8601 date string, convert to {@link LocalDate}
	 * and return {@link BigInteger} result.
	 *
	 * @param m Month value (1-12).
	 * @param d Day value (1-31).
	 * @param y Year value (4-digit value, -9999 to 9999).
	 * @return Integer epoch day value.
	 * @throws IllegalArgumentException if the values are out of range.
	 */
	private static BigInteger mdyValue(final int m, final int d, final int y) {
	    int year = y;
	    boolean negative = year < 0;
	    if (negative)
		year = - year;

	    if (m < 1 || m > 12)
		throw new Intl.IllegalArgumentException("math#date.monthOutOfRange", m);
	    if (m == 2 && d > (isLeapYear(y) ? 29 : 28))
		throw new Intl.IllegalArgumentException("math#date.dayOutOfRange", d);
	    if (d < 1 || (m != 2 && d > DAYS_PER_MONTH[m - 1]))
		throw new Intl.IllegalArgumentException("math#date.dayOutOfRange", d);

	    if (year == 0 || year > 9999)
		throw new Intl.IllegalArgumentException("math#date.yearOutOfRange", year);

	    try {
		String ISOdate = String.format(negative? "-" + ISO_8601_DATE : ISO_8601_DATE, year, m, d);
		LocalDate date = LocalDate.parse(ISOdate);
		return BigInteger.valueOf(date.toEpochDay());
	    }
	    catch (DateTimeParseException | NumberFormatException ex) {
		throw new IllegalArgumentException(Exceptions.toString(ex));
	    }
	}

	/**
	 * Construct a date from month, day, and year values.
	 *
	 * @param m Month value (1-12).
	 * @param d Day value (1-31).
	 * @param y Year value (4 digits).
	 * @return  Constructed date value.
	 * @throws IllegalArgumentException if any of the values are out of range.
	 */
	public static BigInteger date(final int m, final int d, final int y) {
	    return mdyValue(m, d, y);
	}

	/**
	 * Parse a format string into an array of {@link Fmt} values indicating the fields to
	 * expect in the input string.
	 * <p> One {@code m} means one or two month digits, {@code mm} or {@code M} means fixed two-digit
	 * month. One {@code d} means one or two digit day, {@code dd} or {@code D} means fixed two-digit
	 * day. One {@code y} means two or four digit year, {@code yy} or {@code Y} is a fixed two-digit
	 * year, while {@code yyyy} or {@code YY} is a fixed four-digit year.
	 * TODO: check Java string formatting for conventions.
	 *
	 * @param format The format string, consisting of separators and {@code m, d, y, M, D, Y}
	 *               characters.
	 * @return       An array of the format values.
	 */
	private static Fmt[] parseFormat(final String format) {
	    Fmt[] ret = new Fmt[3];

	    String f = format.replaceAll("[\\-/,;\\._]", "-").toUpperCase();
	    int ix = 0;
	    int len = 1;
	    char last = f.charAt(0);

	    // Examples (based on Calc.g4 "DATE_CONST")
	    // yy-m?m-d?d or yyyy-m?m-d?d
	    // yyyymmdd or yymmdd
	    // m?m-d?d-yy or m?m-d?d-yyyy
	    // yyyymmdd or yymmdd
	    // y-m-d

	    for (int i = 1; i < f.length(); i++) {
		char ch = f.charAt(i);

		if (ch == last) {
		    len++;
		}
		else {
		    switch (last) {
			case 'D':
			    ret[ix++] = (len == 1 && ch == '-') ? Fmt.DY : Fmt.D2;
			    break;
			case 'M':
			    ret[ix++] = (len == 1 && ch == '-') ? Fmt.MN : Fmt.M2;
			    break;
			case 'Y':
			    if (len == 1 && ch != '-')
				throw new Intl.IllegalArgumentException("math#date.badFormatString", format);
			    ret[ix++] = (len == 1 && ch == '-') ? Fmt.YR : len == 2 ? Fmt.Y2 : Fmt.Y4;
			    break;
			case '-':
			    break;
			default:
			    throw new Intl.IllegalArgumentException("math#date.badFormatString", format);
		    }

		    last = ch;
		    len = 1;
		}
	    }

	    switch (last) {
		case 'D':
		    ret[ix] = len == 1 ? Fmt.DY : Fmt.D2;
		    break;
		case 'M':
		    ret[ix] = len == 1 ? Fmt.MN : Fmt.M2;
		    break;
		case 'Y':
		    ret[ix] = len == 1 ? Fmt.YR : len == 2 ? Fmt.Y2 : Fmt.Y4;
		    break;
		default:
		    throw new Intl.IllegalArgumentException("math#date.badFormatString", format);
	    }

	    return ret;
	}

	/**
	 * Adjust a "short" year value (two digits) to the appropriate century, based on
	 * a cutover year of 30 years from today. So, two digit years less than the current
	 * two-digit year + 30 are treated as the current century, otherwise the previous one.
	 * <p> Examples: in the year 2022: 00..51 -&gt; 2000..2051, while 52..99 -&gt; 1952..1999.
	 *
	 * @param year The two-digit year to adjust.
	 * @return     Adjusted year according to the cutover value.
	 */
	public static int adjustShortYear(final int year) {
	    int cutoverYear = (LocalDate.now().getYear() % 100) + 30;
	    int newYear = year;
	    boolean negative = newYear < 0;

	    if (negative)
		newYear = -newYear;

	    if (newYear < cutoverYear)
		newYear += 2000;
	    else
		newYear += 1900;

	    return negative ? -newYear : newYear;
	}

	/**
	 * Find the next delimiter in the string, starting from the given position.
	 *
	 * @param string The string to parse.
	 * @param charPos Current position in the string.
	 * @return        Position of the next delimiter, or the length of the string,
	 *                if no next delimiter found.
	 */
	private static int findDelim(final String string, final int charPos) {
	    int len = string.length();

	    for (int pos = charPos; pos < len; pos++) {
		char ch = string.charAt(pos);
		if (ch < '0' || ch > '9')
		    return pos;
	    }

	    return len;
	}


	/**
	 * Get a date value from the given value. Values convertible to an integer will become
	 * the "epoch" date. Anything else will be converted to a string and parsed, which if
	 * the string format matches the ISO-8601 format, will be parsed as such, otherwise
	 * as a US-format date (MM-DD-YYYY).
	 *
	 * @param obj    Any object with a compatible value.
	 * @param format An optional format string (such as {@code "m/d/y"}) which gives the
	 *               order of the values; can also have multiple characters, with or without
	 *               separators.
	 * @return Constructed date value.
	 * @throws IllegalArgumentException if the value cannot be converted to a date.
	 */
	public static BigInteger valueOf(final Object obj, final String format) {
	    if (obj instanceof Number) {
		long value = ((Number) obj).longValue();
		return BigInteger.valueOf(value);
	    }

	    String string = obj.toString();

	    int m = 0;
	    int d = 0;
	    int y = 0;

	    Fmt[] fmt = parseFormat(CharUtil.isNullOrEmpty(format) ? ISO_8601_FORMAT : format);

	    int charPos = 0;
	    int endPos = 0;
	    for (int ix = 0; ix < fmt.length; ix++) {
		Fmt f = fmt[ix];

		switch (f) {
		    case YR:
			endPos = charPos;
			if (string.charAt(endPos) == '-')
			    endPos++;
			while (endPos < string.length() && string.charAt(endPos) >= '0' && string.charAt(endPos) <= '9')
			    endPos++;
			y = Integer.parseInt(string.substring(charPos, endPos));
			break;
		    case Y4:
			endPos = string.charAt(charPos) == '-' ? charPos + 5 : charPos + 4;
			y = Integer.parseInt(string.substring(charPos, endPos));
			break;
		    case Y2:
			endPos = string.charAt(charPos) == '-' ? charPos + 3 : charPos + 2;
			y = adjustShortYear(Integer.parseInt(string.substring(charPos, endPos)));
			break;
		    case MN:
			endPos = findDelim(string, charPos);
			m = Integer.parseInt(string.substring(charPos, endPos));
			break;
		    case M2:
			endPos = charPos + 2;;
			m = Integer.parseInt(string.substring(charPos, endPos));
			break;
		    case DY:
			endPos = findDelim(string, charPos);
			d = Integer.parseInt(string.substring(charPos, endPos));
			break;
		    case D2:
			endPos = charPos + 2;
			d = Integer.parseInt(string.substring(charPos, endPos));
			break;
		}

		// Skip the separator, if any
		charPos = endPos;
		if (charPos < string.length()) {
		    char ch = string.charAt(charPos);
		    if (ch < '0' || ch > '9')
			charPos++;
		}
	    }

	    return mdyValue(m, d, y);
	}

}
