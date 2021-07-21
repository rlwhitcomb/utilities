/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2011,2013-2014,2016-2018,2020-2021 Roger L. Whitcomb.
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
 *	Various static methods for numeric conversions and manipulations.
 *
 *  History:
 *	23-Feb-2011 (rlwhitcomb)
 *	    Created.
 *	11-Mar-2013 (rlwhitcomb)
 *	    Create static methods to determine the appropriate size
 *	    range for a (possibly) large value, and then to format it
 *	    into that range.
 *	21-Aug-2013 (rlwhitcomb)
 *	    Really support Terabytes and Petabytes in the value match pattern
 *	    but still only support KMG value range in that method.
 *	04-Oct-2013 (rlwhitcomb)
 *	    Add method to convert a long to human-readable words.
 *	06-Nov-2014 (rlwhitcomb)
 *	    Move error message strings to resource bundle.  Not quite ready to do
 *	    the same for the number names.
 *	20-Nov-2014 (rlwhitcomb)
 *	    Make a new method to format big numbers using short suffixes (space-constrained).
 *	05-Jan-2016 (rlwhitcomb)
 *	    After writing tests for "convertToWords" it was discovered that it fails for
 *	    Long.MAX_VALUE, so redo the logic to fix that.
 *	07-Jan-2016 (rlwhitcomb)
 *	    Finally getting to fixing the Java 8 Javadoc warnings.
 *	12-Apr-2016 (rlwhitcomb)
 *	    Methods to convert to/from BCD format and BigDecimal.
 *	22-Aug-2017 (rlwhitcomb)
 *	    Method to round up to a power of two.
 *	12-Feb-2018 (rlwhitcomb)
 *	    More methods to deal with binary files.
 *	14-Feb-2018 (rlwhitcomb)
 *	    Implement RAW binary file read support.
 *	07-May-2018 (rlwhitcomb)
 *	    Fix BCD support a little bit, implement raw writing.
 *	10-Aug-2018 (rlwhitcomb)
 *	    Add method to do exponentiation on BigDecimal values.
 *	29-Jan-2020 (rlwhitcomb)
 *	    Add BigInteger ("bigint") support.
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	02-Dec-2020 (rlwhitcomb)
 *	    Add "constants" for PI and E to arbitrary (well, under 10,000) digits
 *	    (as BigDecimal).
 *	02-Dec-2020 (rlwhitcomb)
 *	    Add "piDigits" method (which was used to generate the PI digit string).
 *	    Add "eDecimal" method (which was used to generate the E digit string).
 *	03-Dec-2020 (rlwhitcomb)
 *	    Tweak the number of loops in "eDecimal" because sometimes we are off
 *	    (according to "TestNumericUtil").
 *	03-Dec-2020 (rlwhitcomb)
 *	    Code style tweaks.
 *	04-Dec-2020 (rlwhitcomb)
 *	    Fix some compile errors from last change.
 *	    Add "factorial" method for BigDecimal.
 *	08-Dec-2020 (rlwhitcomb)
 *	    Add "sin" method for BigDecimal (using Maclaurin series expansion).
 *	13-Dec-2020 (rlwhitcomb)
 *	    Implement fib(n).
 *	14-Dec-2020 (rlwhitcomb)
 *	    Add Exabytes to the long range; option for SI vs. binary values.
 *	19-Dec-2020 (rlwhitcomb)
 *	    Ooops! Fib was doing factorial instead of fib.
 *	19-Dec-2020 (rlwhitcomb)
 *	    Implement fib for negative values.
 *	21-Dec-2020 (rlwhitcomb)
 *	    Update obsolete Javadoc constructs.
 *	05-Jan-2021 (rlwhitcomb)
 *	    Optimizations in "pow".
 *	09-Jan-2021 (rlwhitcomb)
 *	    Add "isPrime()".
 *	11-Jan-2021 (rlwhitcomb)
 *	    Just as an experiment, raise the upper limit of "isPrime()" to 2**32-1.
 *	12-Jan-2021 (rlwhitcomb)
 *	    Implement "cos".
 *	16-Jan-2021 (rlwhitcomb)
 *	    Implement "sqrt".
 *	18-Jan-2021 (rlwhitcomb)
 *	    Implement "cbrt". Implement "factorial" for negative numbers.
 *	28-Jan-2021 (rlwhitcomb)
 *	    Fix bug needing commas in "convertToWords" results.
 *	28-Jan-2021 (rlwhitcomb)
 *	    Refactor around DataType. Add "BOOL" type.
 *	29-Jan-2021 (rlwhitcomb)
 *	    Use new Intl forms of exceptions.
 *	29-Jan-2021 (rlwhitcomb)
 *	    Add function and tables for Bernoulli numbers.
 *	30-Jan-2021 (rlwhitcomb)
 *	    New "tenPower" function.
 *	30-Jan-2021 (rlwhitcomb)
 *	    Implement rational mode for "bernoulli".
 *	31-Jan-2021 (rlwhitcomb)
 *	    Now that we have BigFraction, implement the real algorithm
 *	    for Bernoulli numbers using that class.
 *	01-Feb-2021 (rlwhitcomb)
 *	    And now that we have Bernoulli numbers, implement Taylor
 *	    series expansion for tan().
 *	16-Feb-2021 (rlwhitcomb)
 *	    Turn the debug printouts into logging statements for easy analysis.
 *	02-Mar-2021 (rlwhitcomb)
 *	    Some more optimization with the digits of PI/E (keep around the longest
 *	    digit string used so far, and just harvest substrings from it), as
 *	    well as using a rational approximation for PI for fewer than 25 digits.
 *	04-Mar-2021 (rlwhitcomb)
 *	    Keep around the prime number sieve for reuse and some other "optimizations"
 *	    for "isPrime".
 *	05-Mar-2021 (rlwhitcomb)
 *	    Implement "getFactors".  And now that calculating primes is faster, start to
 *	    implement "getPrimeFactors" (not quite working yet as I think through how to
 *	    get it right).
 *	05-Mar-2021 (rlwhitcomb)
 *	    Almost fix/finish "getPrimeFactors" -- still some bugs.
 *	07-Mar-2021 (rlwhitcomb)
 *	    One is NOT a prime; also finally fix "getPrimeFactors".
 *	10-Mar-2021 (rlwhitcomb)
 *	    Conversions to/from Roman Numerals.
 *	10-Mar-2021 (rlwhitcomb)
 *	    Rework some of the Roman Numeral code.
 *	22-Mar-2021 (rlwhitcomb)
 *	    Implement lower-case Roman Numeral recognition and formatting.
 *	24-Mar-2021 (rlwhitcomb)
 *	    Beef up the Roman Numeral support with all the Unicode variants.
 *	26-Mar-2021 (rlwhitcomb)
 *	    A little bit more optimization in BigInteger "pow" method.
 *	26-Mar-2021 (rlwhitcomb)
 *	    Move the trig, log, and other "math" functions into a separate class.
 *	08-Apr-2021 (rlwhitcomb)
 *	    Conversions to/from times and durations.
 *	07-Jul-2021 (rlwhitcomb)
 *	    Make the class final.
 */
package info.rlwhitcomb.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;


/**
 * Collection of static methods to deal with numeric conversions.
 * <p> NOTE: this class will (somewhat confusingly) deal with conversions
 * to binary units instead of SI units to go along with historical usage
 * in the computer industry.  This differs from the units used by disk-drive
 * manufacturers (for instance) which specify sizes in SI (decimal) units.
 */
public final class NumericUtil
{
	/**
	 * Enum representing the binary data types.
	 */
	public static enum DataType
	{
	    NUL   (null            ),
	    BYTE  (Byte.class      ),
	    SHORT (Short.class     ),
	    INT   (Integer.class   ),
	    LONG  (Long.class      ),
	    DEC   (BigDecimal.class),
	    FLOAT (Float.class     ),
	    DOUBLE(Double.class    ),
	    DATE  (Date.class      ),
	    CHAR  (Character.class ),
	    STRING(String.class    ),
	    BIGINT(BigInteger.class),
	    BOOL  (Boolean.class   );

	    /**
	     * Reverse lookup class, by class and code (ordinal).
	     */
	    private static class Lookup
	    {
		static Map<Class<?>, DataType> map = new HashMap<>();
		static DataType[] codeMap = new DataType[20];
		static int numValues = -1;

		static void add(final DataType type, final Class<?> clazz) {
		    if (clazz != null)
			map.put(clazz, type);
		    int ord = type.ordinal();
		    if (ord > numValues)
			numValues = ord;
		    codeMap[ord] = type;
		}

		static DataType fromObjClass(final Object value) {
		    if (value == null)
			return NUL;
		    DataType type = map.get(value.getClass());
		    if (type == null) {
			throw new Intl.IllegalArgumentException("util#numeric.badDataType", value.getClass().getName());
		    }
		    return type;
		}

		static DataType fromCode(final int code) {
		    if (code < 0 || code > numValues) {
			throw new Intl.IllegalArgumentException("util#numeric.badDataTypeCode", code);
		    }
		    return codeMap[code];
		}
	    }

	    private DataType(final Class<?> thisClass) {
		Lookup.add(this, thisClass);
	    }

	    public int getCode() {
		return this.ordinal();
	    }

	    public static DataType fromCode(final int code) {
		return Lookup.fromCode(code);
	    }

	    public static DataType fromObjClass(final Object value) {
		return Lookup.fromObjClass(value);
	    }

	    public void writeCode(final DataOutputStream dos)
		throws IOException
	    {
		dos.writeByte(ordinal());
	    }

	    public static DataType readCode(final DataInputStream dis)
		throws IOException
	    {
		return Lookup.fromCode((int)dis.readByte());
	    }
	}


	/**
	 * Byte order for raw binary numeric values.
	 */
	public static enum ByteOrder
	{
		LSB,
		MSB;

		public static ByteOrder fromString(final String value) {
		    for (ByteOrder order : values()) {
			if (order.toString().equalsIgnoreCase(value)) {
			    return order;
			}
		    }
		    throw new Intl.IllegalArgumentException("util#numeric.unknownByteOrder", value);
		}
	}


	/**
	 * Determines how strings are to be read/written to raw binary files.
	 */
	public static enum StringLength
	{
		/** Byte width specified at read/write time, padded with 0x00 bytes. */
		FIXED,
		/** String delimited by 0x00 byte at the end. */
		EOS,
		/** String length prefix (one byte, max 255 bytes). */
		PREFIX1,
		/** String length prefix (two bytes, max 32767 bytes). */
		PREFIX2,
		/** String length prefix (four bytes, max 4,294,967,295 bytes). */
		PREFIX4;

		public static StringLength fromString(final String value) {
		    for (StringLength length : values()) {
			if (length.toString().equalsIgnoreCase(value)) {
			    return length;
			}
		    }
		    throw new Intl.IllegalArgumentException("util#numeric.unknownStringLength", value);
		}
	}



	public static final BigInteger MIN_BYTE  = BigInteger.valueOf(Byte.MIN_VALUE);
	public static final BigInteger MAX_BYTE  = BigInteger.valueOf(Byte.MAX_VALUE);
	public static final BigInteger MIN_SHORT = BigInteger.valueOf(Short.MIN_VALUE);
	public static final BigInteger MAX_SHORT = BigInteger.valueOf(Short.MAX_VALUE);
	public static final BigInteger MIN_INT   = BigInteger.valueOf(Integer.MIN_VALUE);
	public static final BigInteger MAX_INT   = BigInteger.valueOf(Integer.MAX_VALUE);
	public static final BigInteger MIN_LONG  = BigInteger.valueOf(Long.MIN_VALUE);
	public static final BigInteger MAX_LONG  = BigInteger.valueOf(Long.MAX_VALUE);

	private static final Pattern VALUE_MATCH= Pattern.compile("^([0-9]+)([kKmMgGtTpPeE][iI]?)[bB]?$");

	private static final long MULT_KB = 1000L;
	private static final long MULT_MB = MULT_KB * MULT_KB;
	private static final long MULT_GB = MULT_MB * MULT_KB;
	private static final long MULT_TB = MULT_GB * MULT_KB;
	private static final long MULT_PB = MULT_TB * MULT_KB;
	private static final long MULT_EB = MULT_PB * MULT_KB;

	private static final long MULT_KIB = 1024L;
	private static final long MULT_MIB = MULT_KIB * MULT_KIB;
	private static final long MULT_GIB = MULT_MIB * MULT_KIB;
	private static final long MULT_TIB = MULT_GIB * MULT_KIB;
	private static final long MULT_PIB = MULT_TIB * MULT_KIB;
	private static final long MULT_EIB = MULT_PIB * MULT_KIB;

	private static final String _T = "\u2182";
	private static final String _F = "\u2181";
	private static final String _M = "M\u216F\u2180";
	private static final String _D = "D\u216E";
	private static final String _C = "C\u216D";
	private static final String _L = "L\u216C";
	private static final String _X = "X\u2169";
	private static final String _V = "V\u2164";
	private static final String _I = "I\u2160";

	/**
	 * Recognizes Roman numerals up to 39999 (upper case form).
	 */
	private static final Pattern ROMAN_PATTERN = Pattern.compile(String.format(
	    "^([%1$s]{0,3})"
	    +"([%3$s][%1$s]|[%3$s][%2$s]|[%2$s]?[%3$s]{0,3})"
	    +"([%5$s][%3$s]|[%5$s][%4$s]|[%4$s]?[%5$s]{0,3})"
	    +"([%7$s][%5$s]|[%7$s][%6$s]|[%6$s]?[%7$s]{0,3})"
	    +"([%9$s][%7$s]|[%9$s][%8$s]|[%8$s]?[%9$s]{0,3}|[\u2161-\u2163\u2165-\u2168\u216A-\u216B])$",
		_T, _F, _M, _D, _C, _L, _X, _V, _I));

	/** The upper case Roman Numeral values for output. */
	private static final char[] ROMAN_UPPER_CHARS = {
	    '\u2182', '\u2181', 'M', 'D', 'C', 'L', 'X', 'V', 'I'
	};
	/** The lower case Roman Numeral values for output. */
	private static final char[] ROMAN_LOWER_CHARS = {
	    '\u2182', '\u2181', 'm', 'd', 'c', 'l', 'x', 'v', 'i'
	};
	/** The maximum input value we can convert to a Roman numeral. */
	private static final int ROMAN_MAX_VALUE = 39999;

	/** The pattern for recognizing time values. */
	private static final Pattern TIME_PATTERN = Pattern.compile("(-)?([0-9]{1,2})(:([0-9]{1,2})(:([0-9]{1,2})(.([0-9]+))?)?)?([ \t]*([aApP])[mM]?)?");
	/** The pattern for recognizing duration values. */
	private static final Pattern DURATION_PATTERN = Pattern.compile("(-)?([0-9]+([\\.][0-9]*)?)[ \t]*([wWdDhHmMsS])");

	/** Number of nanoseconds in a second. */
	private static final long NANOSECONDS = 1_000_000_000L;
	/** Number of nanoseconds in one minute. */
	private static final long ONE_MINUTE = 60L * NANOSECONDS;
	/** Number of nanoseconds in one hour. */
	private static final long ONE_HOUR = 60L * ONE_MINUTE;
	/** Number of nanoseconds in twelve hours (for AM/PM conversions). */
	private static final long TWELVE_HOURS = 12L * ONE_HOUR;
	/** Number of nanoseconds in one day. */
	private static final long ONE_DAY = TWELVE_HOURS * 2L;
	/** Number of nanoseconds in one week. */
	private static final long ONE_WEEK = 7L * ONE_DAY;

	/** Decimal value of {@link #NANOSECONDS}. */
	private static BigDecimal D_NANOS = BigDecimal.valueOf(NANOSECONDS);



	private static final String[] smallWords = {
		"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
		"ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen",
		"eighteen", "nineteen"
	};
	private static final String[] tensWords = {
		"twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"
	};
	private static final long[] rangeValues = {
		 99L,  999L,  999999L,  999999999L,  999999999999L,  999999999999999L,  999999999999999999L, Long.MAX_VALUE
	};
	private static final String[] rangeWords = {
		"hundred", "thousand", "million", "billion", "trillion", "quadrillion", "quintillion"
	};
	private static final String minus = "minus";

	/**
	 * Enum to decide between modes of determining range/multipliers (Binary or SI based).
	 */
	public static enum RangeMode
	{
		/** Based on powers of 2 (each one 2**10 = 1024). */
		BINARY,
		/** Based on powers of 10 (each one 10**3 = 1000). */
		DECIMAL,
		/** Multiplier Based on powers of 2 (same as {@link #BINARY}),
		 * but with the decimal name/suffix. */
		MIXED
	}

	/**
	 * Enum to represent the range of a numeric number of bytes.
	 */
	public static enum Range
	{
		/* Range value, mult (SI),mult (bin),suffixes, short name,            long name                 */
		BYTES		(1L,      1L,       "",  "",   "bytes",  "bytes",     "Bytes",     "Bytes"      ),
		KILOBYTES	(MULT_KB, MULT_KIB, "K", "Ki", "Kbytes", "Kibytes",   "Kilobytes", "Kibibytes"  ),
		MEGABYTES	(MULT_MB, MULT_MIB, "M", "Mi", "Mbytes", "Mibytes",   "Megabytes", "Mebibytes"  ),
		GIGABYTES	(MULT_GB, MULT_GIB, "G", "Gi", "Gbytes", "Gibytes",   "Gigabytes", "Gibibytes"  ),
		TERABYTES	(MULT_TB, MULT_TIB, "T", "Ti", "Tbytes", "Tibytes",   "Terabytes", "Tebibytes"  ),
		PETABYTES	(MULT_PB, MULT_PIB, "P", "Pi", "Pbytes", "Pibytes",   "Petabytes", "Pebibytes"  ),
		EXABYTES	(MULT_EB, MULT_EIB, "E", "Ei", "Ebytes", "Eibytes",   "Exabytes",  "Exbibytes"  );

		private long siMultiplier;
		private long binMultiplier;
		private String siSuffix;
		private String binSuffix;
		private String siShortName;
		private String binShortName;
		private String siLongName;
		private String binLongName;

		private Range(final long siMult,    final long binMult,
			      final String siSfx,   final String binSfx,
			      final String siShort, final String binShort,
			      final String siLong,  final String binLong) {
		    this.siMultiplier  = siMult;
		    this.binMultiplier = binMult;
		    this.siSuffix      = siSfx;
		    this.binSuffix     = binSfx;
		    this.siShortName   = siShort;
		    this.binShortName  = binShort;
		    this.siLongName    = siLong;
		    this.binLongName   = binLong;
		}

		/** @return The mixed-mode (1024-based) multiplier for this range. */
		public long getMultiplier() {
		    return getMultiplier(RangeMode.MIXED);
		}

		/**
		 * @return The multiplier for this range, either SI or binary.
		 * @param mode Decide which value to use.
		 */
		public long getMultiplier(final RangeMode mode) {
		    switch (mode) {
			case BINARY:
			default:
			    return binMultiplier;
			case DECIMAL:
			    return siMultiplier;
		    }
		}

		/** @return The mixed-mode (1000-based) suffix for this range. */
		public String getSuffix() {
		    return getSuffix(RangeMode.MIXED);
		}

		/**
		 * @return The suffix for this range, either SI or binary.
		 * @param mode Decide which value to use.
		 */
		public String getSuffix(final RangeMode mode) {
		    switch (mode) {
			case BINARY:
			    return binSuffix;
			case DECIMAL:
			default:
			    return siSuffix;
		    }
		}

		/** @return The mixed-mode name (1000-based) for this range. */
		public String getShortName() {
		    return getShortName(RangeMode.MIXED);
		}

		/**
		 * @return The short name for this range, either SI or binary.
		 * @param mode Decide which value to use.
		 */
		public String getShortName(final RangeMode mode) {
		    switch (mode) {
			case BINARY:
			    return binShortName;
			case DECIMAL:
			default:
			    return siShortName;
		    }
		}

		/** @return The long name for this range (mixed-mode, 1000-based). */
		public String getLongName() {
		    return getLongName(RangeMode.MIXED);
		}

		/**
		 * @return The long name for this range, either SI or binary.
		 * @param mode Decide which value to use.
		 */
		public String getLongName(final RangeMode mode) {
		    switch (mode) {
			case BINARY:
			    return binLongName;
			case DECIMAL:
			default:
			    return siLongName;
		    }
		}

		/**
		 * @return A {@link Range} value that corresponds to the given suffix (as in, "K", "M", etc).
		 * @param suffix The suffix to look up (either binary or SI value).
		 */
		public static Range getRangeBySuffix(final String suffix) {
		    for (Range r : values()) {
			if (r.binSuffix.equalsIgnoreCase(suffix) || r.siSuffix.equalsIgnoreCase(suffix))
			    return r;
		    }
		    return null;
		}

		/** @return A {@link Range} value that best represents the given value,
		 * in mixed-mode units (1024-based, but 1000-based names).
		* This will result in a formatted value in the range of 0.00 .. 799.99;
		 * @param value The candidate value.
		 */
		public static Range getRangeOfValue(final long value) {
		    return getRangeOfValue(value, RangeMode.MIXED);
		}

		/** @return A {@link Range} value that best represents the given value.
		 * This will result in a formatted value in the range of 0.80 .. 799.99.
		 * @param value The candidate value.
		 * @param mode Decide which value to use.
		 */
		public static Range getRangeOfValue(final long value, final RangeMode mode) {
		    long absValue = Math.abs(value);

		    if (absValue <= 1L)
			return BYTES;

		    for (Range r : values()) {
			switch (mode) {
			    case BINARY:
			    default:
				if (absValue / r.binMultiplier < 800)
				    return r;
				break;
			    case DECIMAL:
				if (absValue / r.siMultiplier < 800)
				    return r;
				break;
			}
		    }
		    return BYTES;
		}

	}


	/**
	 * Since this is a static class, make the constructor private so no one
	 * can instantiate it.
	 */
	private NumericUtil() {
	}


	/**
	 * Helper function to convert a "nnK" or "nnM" or "nnG" to a straight number.
	 * <p> Also deals with "nnKi" for binary-based value.
	 *
	 * @param	input	The input value in one of the above formats.
	 * @return		The output as a strictly numeric value, where "nn" is
	 *			multiplied by 1024 for "K", 1024 * 1024 for "M" and
	 *			1024 * 1024 * 1024 for "G".
	 * @throws		NumberFormatException for bad input formats
	 */
	public static long convertKMGValue(final String input) {
	    Matcher m = VALUE_MATCH.matcher(input);
	    if (m.matches()) {
		long value = Long.parseLong(m.group(1));
		String suffix = m.group(2);
		Range range = Range.getRangeBySuffix(suffix);
		if (range != null) {
		    if (suffix.toUpperCase().endsWith("I"))
			value *= range.getMultiplier(RangeMode.BINARY);
		    else
			value *= range.getMultiplier(RangeMode.DECIMAL);
		}
		return value;
	    }
	    else
		throw new NumberFormatException(Intl.getString("util#numeric.badKMGFormat"));
	}


	/**
	 * Format a value using the {@link Range} enum to put into a readable range,
	 * using mixed-mode units.
	 *
	 * @param	value	The input value to format.
	 * @return		The value formatted to an appropriate range.
	 */
	public static String formatToRange(final long value) {
	    return formatToRange(value, RangeMode.MIXED);
	}

	/**
	 * Format a value using the {@link Range} enum to put into a readable range,
	 * using either Binary or SI-based units.
	 *
	 * @param	value	The input value to format.
	 * @param	mode	Decide which value to use.
	 * @return		The value formatted to an appropriate range.
	 */
	 public static String formatToRange(final long value, final RangeMode mode) {
	    Range r = Range.getRangeOfValue(value, mode);
	    if (r == Range.BYTES)
		return String.format("%1$d %2$s", value, r.getShortName(mode));
	    else {
		double scaledValue = (double)value / (double)r.getMultiplier(mode);
		String name = r.getShortName(mode);
		if (scaledValue < 10.0d)
		    return String.format("%1$3.2f %2$s", scaledValue, name);
		else if (scaledValue < 100.0d)
		    return String.format("%1$3.1f %2$s", scaledValue, name);
		else
		    return String.format("%1$3.0f %2$s", scaledValue, name);
	    }
	}


	/**
	 * Format a value using the {@link Range} enum to put into a readable range,
	 * but use just the suffix (to save space).
	 *
	 * @param	value	The input value to format.
	 * @return		The value formatted (short form) to an appropriate range.
	 */
	public static String formatToRangeShort(final long value) {
	    return formatToRangeShort(value, RangeMode.MIXED);
	}


	/**
	 * Format a value using the {@link Range} enum to put into a readable range,
	 * but use just the suffix (to save space).
	 *
	 * @param	value	The input value to format.
	 * @param	mode	Whether to use binary or SI units.
	 * @return		The value formatted (short form) to an appropriate range.
	 */
	public static String formatToRangeShort(final long value, final RangeMode mode) {
	    Range r = Range.getRangeOfValue(value, mode);
	    if (r == Range.BYTES)
		return String.format("%1$d B", value);
	    else {
		double scaledValue = (double)value / (double)r.getMultiplier(mode);
		String suffix = r.getSuffix(mode);
		if (scaledValue < 10.0d)
		    return String.format("%1$3.2f %2$sB", scaledValue, suffix);
		else if (scaledValue < 100.0d)
		    return String.format("%1$3.1f %2$sB", scaledValue, suffix);
		else
		    return String.format("%1$3.0f %2$sB", scaledValue, suffix);
	    }
	}


	/**
	 * The "long" range name will be something like "Kilobytes", or "Megabytes".
	 *
	 * @param	value	The input value to test for range.
	 * @return		The long name of the appropriate range for this value.
	 */
	public static String getLongRangeName(final long value) {
	    return Range.getRangeOfValue(value).getLongName();
	}


	/**
	 * The "long" range name will be something like "Kilobytes", or "Megabytes".
	 *
	 * @param	value	The input value to test for range.
	 * @param	mode	Whether to use binary or SI units.
	 * @return		The long name of the appropriate range for this value.
	 */
	public static String getLongRangeName(final long value, final RangeMode mode) {
	    return Range.getRangeOfValue(value, mode).getLongName(mode);
	}


	/**
	 * Convert a long number to words.
	 * <p>Examples:
	 * <ul><li>10 -&gt; ten
	 * <li>27 -&gt; twenty-seven
	 * <li>493 -&gt; four hundred ninety-three
	 * </ul>
	 *
	 * @param	value	The value to convert.
	 * @return		The value written out as its English name.
	 */
	public static String convertToWords(final long value) {
	    StringBuilder buf = new StringBuilder();
	    convertToWords(value, buf);
	    return buf.toString();
	}

	public static void convertToWords(final long inputValue, final StringBuilder buf) {
	    long value = inputValue;
	    if (value < 0L) {
		if (value == Long.MIN_VALUE) {
		    throw new Intl.IllegalArgumentException("util#numeric.outOfRange");
		}
		buf.append(minus).append(' ');
		value = -value;
	    }
	    if (value < 20L) {
		buf.append(smallWords[(int)value]);
	    }
	    else if (value < 100L) {
		int decade = (int)(value / 10L);
		int residual = (int)(value % 10L);
		buf.append(tensWords[decade - 2]);
		if (residual != 0) {
		    buf.append('-');
		    buf.append(smallWords[residual]);
		}
	    }
	    else {
		for (int i = 1; i < rangeValues.length; i++) {
		    if (value <= rangeValues[i]) {
			long scale = rangeValues[i - 1] + 1L;
			long prefix = value / scale;
			long residual = value % scale;
			convertToWords(prefix, buf);
			buf.append(' ').append(rangeWords[i - 1]);
			if (residual != 0L) {
			    if (i > 1)
				buf.append(", ");
			    else
				buf.append(' ');
			    convertToWords(residual, buf);
			}
			break;
		    }
		}
	    }
	}


	/**
	 * Convert a BCD-format byte string into a {@link BigDecimal} value.
	 *
	 * @param bcdBytes	The BCD-encoded byte string.
	 * @return		The {@link BigDecimal} equivalent.
	 * @throws IllegalArgumentException if the input bytes are not valid BCD.
	 */
	public static BigDecimal convertBCDToDecimal(final byte[] bcdBytes) {
	    String decimalString = convertBCDToString(bcdBytes);
	    return new BigDecimal(decimalString);
	}


	/**
	 * Convert a BCD-format byte string into a {@link BigInteger} value.
	 *
	 * @param bcdBytes	The BCD-encoded byte string.
	 * @return		The {@link BigInteger} equivalent.
	 * @throws IllegalArgumentException if the input bytes are not valid BCD.
	 */
	public static BigInteger convertBCDToInteger(final byte[] bcdBytes) {
	    String integerString = convertBCDToString(bcdBytes);
	    return new BigInteger(integerString);
	}


	/**
	 * Convert a BCD-format byte string into a string value, which can then
	 * be converted to a {@link BigDecimal} or {@link BigInteger}.
	 *
	 * @param bcdBytes	The BCD-encoded byte string.
	 * @return		The string equivalent.
	 * @throws IllegalArgumentException if the input bytes are not valid BCD.
	 */
	public static String convertBCDToString(final byte[] bcdBytes) {
	    StringBuilder buf = new StringBuilder(bcdBytes.length * 2 + 2);
	    int scale = bcdBytes[0];
	    int pos = 0;
	    char sign = '+';
	    // Put in the decimal and leading zeroes
	    if (scale < 0) {
		buf.append('.');
		for (int j = 0; j < -scale; j++) {
		    buf.append('0');
		}
	    }
	    for (int i = 1; i < bcdBytes.length; i++) {
		int highNibble = (bcdBytes[i] & 0xF0) >>> 4;
		int lowNibble  = (bcdBytes[i] & 0x0F);
		if (pos == scale) {
		    buf.append('.');
		}
		if (highNibble > 9) {
		    if (highNibble == 0x0D) {
			sign = '-';
		    }
		    break;
		}
		else {
		    buf.append((char)(highNibble + '0'));
		    pos++;
		}
		if (pos == scale) {
		    buf.append('.');
		}
		if (lowNibble > 9) {
		    if (lowNibble == 0x0D) {
			sign = '-';
		    }
		    break;
		}
		else {
		    buf.append((char)(lowNibble + '0'));
		    pos++;
		}
	    }
	    buf.insert(0, sign);

	    // Delete trailing decimal point so this works for BigInteger too
	    int last = buf.length() - 1;
	    if (buf.charAt(last) == '.')
		buf.deleteCharAt(last);

	    return buf.toString();
	}


	/**
	 * Convert from {@link BigDecimal} to BCD format (byte array).
	 *
	 * @param decimal	A {@link BigDecimal} value to convert.
	 * @return 		The BCD-encoded bytes of this value.
	 */
	public static byte[] convertToBCD(final BigDecimal decimal) {
	    return convertToBCD(decimal.toPlainString());
	}


	/**
	 * Convert from {@link BigInteger} to BCD format (byte array).
	 *
	 * @param integer	A {@link BigInteger} value to convert.
	 * @return 		The BCD-encoded bytes of this value.
	 */
	public static byte[] convertToBCD(final BigInteger integer) {
	    return convertToBCD(integer.toString());
	}


	/**
	 * Convert from numeric string to BCD format (byte array).
	 *
	 * @param plainText	A plain text numeric string to convert.
	 * @return 		The BCD-encoded bytes of this value.
	 */
	public static byte[] convertToBCD(final String plainText) {
	    int plainLength = plainText.length();

	    // The format is going to be:
	    // optional sign
	    // some number (non-zero) of integer digits
	    // optional decimal point, and then
	    // optional fractional digits
	    int i = 0;
	    int sign = 0x0C;	// positive
	    if (plainText.charAt(i) == '-') {
		sign = 0x0D;
		i++;
	    }
	    else if (plainText.charAt(i) == '+') {
		i++;
	    }
	    // First pass, compute scale and count # digits
	    int scale = 0;
	    int digits = 0;
	    boolean pointSeen = false;
	    for (int j = i; j < plainLength; j++) {
		// Skip leading zeros
		if (!pointSeen && scale == 0 && plainText.charAt(j) == '0') {
		    i++;
		}
		// Note: the "BigDecimal.toPlainString()" method produces a
		// canonical form, not affected by Locale, so we are safe to
		// use '.' here.
		else if (plainText.charAt(j) == '.') {
		    pointSeen = true;
		}
		else if (plainText.charAt(j) == '0') {
		    if (pointSeen && digits == 0) {
			scale--;
		    }
		    else {
			digits++;
			if (!pointSeen)
			    scale++;
		    }
		}
		else {
		    digits++;
		    if (!pointSeen)
			scale++;
		}
	    }
	    int byteLength = (digits + 2) / 2 + 1 /* for scale */;
	    byte[] bytes = new byte[byteLength];
	    int pos = 0;
	    pointSeen = false;
	    bytes[0] = (byte)scale;
	    for (int j = i; j < plainLength; j++) {
		if (plainText.charAt(j) == '.') {
		    pointSeen = true;
		    continue;
		}
		else if (plainText.charAt(j) == '0') {
		    if (pointSeen && pos == 0) {
			continue;
		    }
		}
		int ch = plainText.charAt(j);
		int nibble = ch - '0';
		if ((pos & 1) == 0) {
		    // even nibble = high nibble of the byte
		    bytes[(pos / 2) + 1] = (byte)(nibble << 4);
		}
		else {
		    // odd nibble = low nibble (combine with existing high)
		    bytes[(pos / 2) + 1] |= nibble;
		}
		pos++;
	    }
	    // Add the sign in as the last nibble
	    if ((pos & 1) == 0) {
		bytes[(pos / 2) + 1] = (byte)(sign << 4);
	    }
	    else {
		bytes[(pos / 2) + 1] |= sign;
	    }

	    return bytes;
	}

	private static byte[] readBCDBytes(final DataInputStream dis)
		throws IOException
	{
	    ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
	    // First byte is scale, so don't do the sign nibble check on it
	    byte b = dis.readByte();
	    baos.write(b);
	    int hi, lo;
	    do {
		b = dis.readByte();
		baos.write(b);
		hi = ((int)b) & 0xC0;
		lo = ((int)b) & 0x0C;
	    } while (hi != 0xC0 && lo != 0x0C);
	    return baos.toByteArray();
	}

	private static char readChar(final DataInputStream dis, final ByteOrder byteOrder)
		throws IOException
	{
	    if (byteOrder == ByteOrder.LSB) {
		return (char)readShort(dis, byteOrder);
	    }
	    return dis.readChar();
	}

	private static void writeChar(final DataOutputStream dos, final char ch, final ByteOrder byteOrder)
		throws IOException
	{
	    if (byteOrder == ByteOrder.LSB) {
		writeShort(dos, (short)ch, byteOrder);
	    }
	    else {
		dos.writeChar(ch);
	    }
	}

	private static short readShort(final DataInputStream dis, final ByteOrder byteOrder)
		throws IOException
	{
	    // This is "network" byte order, or MSB first
	    short value = dis.readShort();
	    if (byteOrder == ByteOrder.LSB) {
		return (short)Integer.rotateRight(Integer.reverseBytes((int)value), 16);
	    }
	    return value;
	}

	private static void writeShort(final DataOutputStream dos, final short value, final ByteOrder byteOrder)
		throws IOException
	{
	    if (byteOrder == ByteOrder.LSB) {
		dos.writeShort((short)Integer.rotateRight(Integer.reverseBytes((int)value), 16));
	    }
	    else {
		dos.writeShort(value);
	    }
	}

	private static int readInt(final DataInputStream dis, final ByteOrder byteOrder)
		throws IOException
	{
	    int value = dis.readInt();
	    if (byteOrder == ByteOrder.LSB) {
		return Integer.reverseBytes(value);
	    }
	    return value;
	}

	private static void writeInt(final DataOutputStream dos, final int value, final ByteOrder byteOrder)
		throws IOException
	{
	    if (byteOrder == ByteOrder.LSB) {
		dos.writeInt(Integer.reverseBytes(value));
	    }
	    else {
		dos.writeInt(value);
	    }
	}

	private static long readLong(final DataInputStream dis, final ByteOrder byteOrder)
		throws IOException
	{
	    long value = dis.readLong();
	    if (byteOrder == ByteOrder.LSB) {
		return Long.reverseBytes(value);
	    }
	    return value;
	}

	private static void writeLong(final DataOutputStream dos, final long value, final ByteOrder byteOrder)
		throws IOException
	{
	    if (byteOrder == ByteOrder.LSB) {
		dos.writeLong(Long.reverseBytes(value));
	    }
	    else {
		dos.writeLong(value);
	    }
	}

	private static float readFloat(final DataInputStream dis, final ByteOrder byteOrder)
		throws IOException
	{
	    return Float.intBitsToFloat(readInt(dis, byteOrder));
	}

	private static void writeFloat(final DataOutputStream dos, final float value, final ByteOrder byteOrder)
		throws IOException
	{
	    writeInt(dos, Float.floatToRawIntBits(value), byteOrder);
	}

	private static double readDouble(final DataInputStream dis, final ByteOrder byteOrder)
		throws IOException
	{
	    return Double.longBitsToDouble(readLong(dis, byteOrder));
	}

	private static void writeDouble(final DataOutputStream dos, final double value, final ByteOrder byteOrder)
		throws IOException
	{
	    writeLong(dos, Double.doubleToRawLongBits(value), byteOrder);
	}

	private static String readString(final DataInputStream dis, final Charset charset, final int byteLength)
		throws IOException
	{
	    byte[] bytes = new byte[byteLength];
	    dis.readFully(bytes);
	    return new String(bytes, charset);
	}

	private static void writeString(final DataOutputStream dos, final String value, final Charset charset,
					final ByteOrder byteOrder, final StringLength stringLength, final int length)
		throws IOException
	{
	    byte[] bytes = value.getBytes(charset);
	    int byteLen = bytes.length;
	    switch (stringLength) {
		case PREFIX1:
		    // Need a range of 0..255 here
		    if (byteLen > 255)
			throw new Intl.IndexOutOfBoundsException("util#numeric.stringLengthTooBig", byteLen, 255);
		    dos.write(byteLen);
		    dos.write(bytes);
		    break;
		case PREFIX2:
		    // Need a range of 0..65535 here
		    if (byteLen > 65535)
			throw new Intl.IndexOutOfBoundsException("util#numeric.stringLengthTooBig", byteLen, 65_535);
		    writeShort(dos, (short)byteLen, byteOrder);
		    dos.write(bytes);
		    break;
		case PREFIX4:
		    writeInt(dos, byteLen, byteOrder);
		    dos.write(bytes);
		    break;
		case FIXED:
		    if (byteLen >= length) {
			// Note: silent truncation of the value!
			dos.write(bytes, 0, length);
		    }
		    else {
			dos.write(Arrays.copyOf(bytes, length));
		    }
		    break;
		case EOS:
		    dos.write(bytes);
		    dos.write('\0');
		    break;
	    }
	}


	public static Object readRawBinaryValue(final DataInputStream dis, final Charset charset, final DataType dataType,
						final ByteOrder byteOrder, final StringLength stringLength, final int length)
		throws IOException
	{
	     Object value = null;
	     int byteLen = length;
	     switch (dataType) {
		case NUL:
		    // Leave the value as null
		    break;
		case BYTE:
		    value = Byte.valueOf(dis.readByte()); break;
		case SHORT:
		    value = Short.valueOf(readShort(dis, byteOrder)); break;
		case INT:
		    value = Integer.valueOf(readInt(dis, byteOrder)); break;
		case LONG:
		    value = Long.valueOf(readLong(dis, byteOrder)); break;
		case BIGINT:
		    value = convertBCDToInteger(readBCDBytes(dis)); break;
		case BOOL:
		    value = Boolean.valueOf(dis.readByte() != 0); break;
		case DEC:
		    // Note: the last nibble of the value has the sign, so read until then.
		    value = convertBCDToDecimal(readBCDBytes(dis));
		    break;
		case FLOAT:
		    value = Float.valueOf(readFloat(dis, byteOrder)); break;
		case DOUBLE:
		    value = Double.valueOf(readDouble(dis, byteOrder)); break;
		case DATE:
		    value = new Date(readLong(dis, byteOrder)); break;
		case CHAR:
		    value = Character.valueOf(readChar(dis, byteOrder)); break;
		case STRING:
		    switch (stringLength) {
			case PREFIX1:
			    // Need a range of 0..255 here
			    byteLen = dis.readUnsignedByte();
			    value = readString(dis, charset, byteLen);
			    break;
			case PREFIX2:
			    // Need a range of 0..65535 here
			    byteLen = ((int)readShort(dis, byteOrder)) & 0xFFFF;
			    value = readString(dis, charset, byteLen);
			    break;
			case PREFIX4:
			    byteLen = readInt(dis, byteOrder);
			    // Fall through
			case FIXED:
			    value = readString(dis, charset, byteLen);
			    break;
			case EOS:
			    ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
			    byte b = dis.readByte();
			    while (b != 0x00) {
				baos.write(b);
				b = dis.readByte();
			    }
			    value = new String(baos.toByteArray(), charset);
			    break;
		    }
		    break;
	     }
	     return value;
	}


	/**
	 * Read an arbitrary data type from the given {@link DataInputStream}
	 * as it was written by {@link #writeBinaryValue}.  Esp. the BCD data
	 * is an uncommon format.
	 * <p> Each piece of data is preceded by a one-byte data type code
	 * given by the {@link DataType} enum/code.  This does two things:
	 * <ul><li>Provides a fool-proof way to interpret the following bytes,</li>
	 * <li>And gives a way to encode a {@code null} value in the streami.</li>
	 * </ul>
	 *
	 * @param dis The data stream to read from.
	 * @param charset The character set to use to interpret string data.
	 * @return The object read or {@code null} if the value byte was
	 * {@link DataType#NUL}.
	 * @throws IOException especially for end of file
	 * @throws EOFException at the end of the file
	 */
	public static Object readBinaryValue(final DataInputStream dis, final Charset charset)
		throws IOException
	{
	     DataType dataType = DataType.readCode(dis);
	     return readRawBinaryValue(dis, charset, dataType, ByteOrder.MSB, StringLength.PREFIX4, -1);
	}

	/**
	 * Write an abitrary piece of data in raw binary form to the given stream.
	 *
	 * @param value Any wrapped primitive data type, or a {@link String}.
	 * @param dos The data stream to write to.
	 * @param charset The character set to use to convert strings to bytes.
	 * @param byteOrder Whether the bytes are in MSB..LSB or LSB..MSB order.
	 * @param stringLength The discipline to use for writing the string length.
	 * @param length For a fixed string length, the length to use, otherwise ignored.
	 *
	 * @throws IOException if there is a problem doing the write.
	 * @throws IllegalArgumentException if the data type isn't recognized.
	 */
	public static void writeRawBinaryValue(final Object value, final DataOutputStream dos, final Charset charset,
					       final ByteOrder byteOrder, final StringLength stringLength, final int length)
			throws IOException
	{
	    // If the value is null, we have written the NUL DataType value already, so nothing more is needed
	    if (value == null)
		return;
	    String className = value.getClass().getSimpleName();
	    switch (className) {
		case "Byte":
		    dos.writeByte(((Byte)value).intValue()); break;
		case "Character":
		    writeChar(dos, ((Character)value).charValue(), byteOrder); break;
		case "Short":
		    writeShort(dos, ((Short)value).shortValue(), byteOrder); break;
		case "Integer":
		    writeInt(dos, ((Integer)value).intValue(), byteOrder); break;
		case "Long":
		    writeLong(dos, ((Long)value).longValue(), byteOrder); break;
		case "Float":
		    writeFloat(dos, ((Float)value).floatValue(), byteOrder); break;
		case "Double":
		    writeDouble(dos, ((Double)value).doubleValue(), byteOrder); break;
		case "BigDecimal":
		    dos.write(convertToBCD((BigDecimal)value)); break;
		case "BigInteger":
		    dos.write(convertToBCD((BigInteger)value)); break;
		case "Boolean":
		    dos.writeByte(value.equals(Boolean.TRUE) ? 1 : 0); break;
		case "String":
		    writeString(dos, (String)value, charset, byteOrder, stringLength, length); break;
		default:
		    throw new Intl.IllegalArgumentException("util#numeric.badDataType", className);
	    }
	}

	/**
	 * Write an arbitrary data type to the given {@link DataOutputStream}.
	 * <p> The convention for these files is to write a byte type for each
	 * field, defined by the {@link DataType} enum
	 *
	 * @param value Any wrapped primitive data type, or a {@link String}.
	 * @param dos The data stream to write to.
	 * @param charset The character set to use to convert strings to bytes.
	 * @throws IOException if there is a problem doing the write.
	 * @throws IllegalArgumentException if the data type isn't recognized.
	 */
	public static void writeBinaryValue(final Object value, final DataOutputStream dos, final Charset charset)
		throws IOException
	{
	    DataType type = DataType.fromObjClass(value);
	    type.writeCode(dos);
	    writeRawBinaryValue(value, dos, charset, ByteOrder.MSB, StringLength.PREFIX4, -1);
	}


	private static boolean in(final char ch, final String values) {
	    return values.indexOf(ch) >= 0;
	}

	private static int countRoman(final String section, final String values) {
	    int count = 0;
	    for (int i = 0; i < section.length(); i++) {
		if (in(section.charAt(i), values))
		    count++;
	    }
	    return count;
	}

	private static int countRoman(final String input,
		final String one, final String five, final String ten, final int multiplier) {
	    int result = 0;
	    int length = input.length();

	    if (length == 2 && in(input.charAt(0), one) && in(input.charAt(1), ten))
		result = 9 * multiplier;
	    else if (length == 2 && in(input.charAt(0), one) && in(input.charAt(1), five))
		result = 4 * multiplier;
	    else if (length >= 1 && in(input.charAt(0), five))
		result = (5 + countRoman(input, one)) * multiplier;
	    else
		result = countRoman(input, one) * multiplier;

	    return result;
	}

	/**
	 * Check and convert a Roman Numeral string to an integer.
	 *
	 * @param input	The input (presumably a valid Roman Numeral), either upper- or lower-case.
	 * @return	The input translated to an integer.
	 * @throws	IllegalArgumentException if the input is malformed.
	 */
	public static int convertFromRoman(final String input) {
	    Matcher m = ROMAN_PATTERN.matcher(input.toUpperCase());
	    if (m.matches()) {
		String tenthous  = m.group(1);
		String thousands = m.group(2);
		String hundreds  = m.group(3);
		String tens      = m.group(4);
		String units     = m.group(5);
		int result = 0;

		result += countRoman(tenthous,  _T, "", "", 10000);
		result += countRoman(thousands, _M, _F, _T, 1000);
		result += countRoman(hundreds,  _C, _D, _M, 100);
		result += countRoman(tens,      _X, _L, _C, 10);
		result += countRoman(units,     _I, _V, _X, 1);

		// Now go through and add up the strange values (if any)
		for (int i = 0; i < units.length(); i++) {
		    char ch = units.charAt(i);
		    if ((ch >= '\u2161' && ch <= '\u2163') ||
			(ch >= '\u2165' && ch <= '\u2168') ||
			(ch >= '\u216A' && ch <= '\u216B'))
			result += (((int) ch) - 0x2160) + 1;
		}
		return result;
	    }
	    else {
		throw new Intl.IllegalArgumentException("util#numeric.badRomanFormat", input);
	    }
	}

	private static void addRomanDigits(final StringBuilder buf,
		final char one, final char five, final char ten, final int count) {
	    if (count != 0) {
		if (count == 9) {
		    buf.append(one).append(ten);
		}
		else if (count == 4) {
		    buf.append(one).append(five);
		}
		else if (count >= 5) {
		    buf.append(five);
		    CharUtil.makeStringOfChars(buf, one, count - 5);
		}
		else {
		    CharUtil.makeStringOfChars(buf, one, count);
		}
	    }
	}

	/**
	 * Convert a (small) integer value to a Roman Numeral string (uppercase).
	 *
	 * @param value	A value in the range {@code 1..39999} to be converted
	 * 		to a Roman numeral.
	 * @return	The converted string.
	 * @see #convertFromRoman
	 * @throws IllegalArgumentException if the input value is out of range.
	 */
	public static String convertToRoman(final int value) {
	    return convertToRoman(value, true);
	}

	/**
	 * Convert a (small) integer value to a Roman Numeral string.
	 *
	 * @param value	A value in the range {@code 1..39999} to be converted
	 * 		to a Roman numeral.
	 * @param upper	Whether to convert to UPPER case ({@code true}) or lower case.
	 * @return	The converted string.
	 * @see #convertFromRoman
	 * @throws IllegalArgumentException if the input value is out of range.
	 */
	public static String convertToRoman(final int value, final boolean upper) {
	    if (value < 1 || value > ROMAN_MAX_VALUE) {
		throw new Intl.IllegalArgumentException("util#numeric.outOfRomanRange", value);
	    }

	    StringBuilder buf = new StringBuilder(30);
	    char[] charValues = upper ? ROMAN_UPPER_CHARS : ROMAN_LOWER_CHARS;
	    int current = value;
	    int tenthousands, thousands, hundreds, tens;

	    tenthousands = current / 10000;
	    addRomanDigits(buf, charValues[0], ' ', ' ', tenthousands);
	    current -= tenthousands * 10000;

	    thousands = current / 1000;
	    addRomanDigits(buf, charValues[2], charValues[1], charValues[0], thousands);
	    current -= thousands * 1000;

	    hundreds = current / 100;
	    addRomanDigits(buf, charValues[4], charValues[3], charValues[2], hundreds);
	    current -= hundreds * 100;

	    tens = current / 10;
	    addRomanDigits(buf, charValues[6], charValues[5], charValues[4], tens);
	    current -= tens * 10;

	    addRomanDigits(buf, charValues[8], charValues[7], charValues[6], current);

	    return buf.toString();
	}


	private static long convertTime(final String t) {
	    long value = 0L;
	    if (t != null && !t.isEmpty()) {
		value = Long.valueOf(t);
	    }
	    return value;
	}

	/**
	 * Convert a string in the form of <code>HH:mm(:ss.mmmm)</code> to nanoseconds
	 * since midnight (as a long).
	 *
	 * @param timeString The input value in the above format.
	 * @return nanoseconds since midnight
	 * @throws IllegalArgumentException if the format isn't recognized
	 */
	public static long convertFromTime(final String timeString) {
	    Matcher m = TIME_PATTERN.matcher(timeString);
	    if (m.matches()) {
		String hours = m.group(2);
		String mins  = m.group(4);
		String secs  = m.group(6);
		String nans  = m.group(8);
		long nanosecs = 0L;
		try {
		    nanosecs = convertTime(hours) * 60L;
		    nanosecs += convertTime(mins);
		    nanosecs *= 60L;
		    nanosecs += convertTime(secs);
		    nanosecs *= 1000000000L;

		    if (nans != null) {
			long fracs = convertTime(nans);
			// Tricky here: since 0.001 is 1/1000 or 1,000,000 nanos
			// but the value we get back is just 1
			// so we have to multiply by the number of digits
			long mult = MathUtil.tenPower(9 - nans.length()).longValue();
			nanosecs += fracs * mult;
		    }
		}
		catch (NumberFormatException nfe) {
		    throw new IllegalArgumentException(nfe);
		}

		String ampm = m.group(10);
		if (ampm != null) {
		    switch (ampm.charAt(0)) {
			case 'a':
			case 'A':
			    // 12:00 am = 00:00
			    if (nanosecs >= TWELVE_HOURS)
				nanosecs -= TWELVE_HOURS;
			    break;
			case 'p':
			case 'P':
			    // 12:00 pm = 12:00
			    if (nanosecs < TWELVE_HOURS)
				nanosecs += TWELVE_HOURS;
			    break;
		    }
		}
		else {
		    // AM or PM takes precedence over minus sign if both are present
		    if (m.group(1) != null)
			nanosecs = -nanosecs;
		}

		return nanosecs;
	    }
	    else {
		throw new Intl.IllegalArgumentException("util#numeric.badTimeValue", timeString);
	    }
	}

	/**
	 * Convert a long number of milliseconds since midnight into a time string, omitting
	 * trailing parts if they are zero (so, the minimum output is "HH:mm").
	 *
	 * @param timeValue The input millisecond value.
	 * @param meridianFlag 'a' or 'p' to indicate adding 'am' / 'pm' indicator as appropriate.
	 * @return The formatted time string.
	 */
	public static String convertToTime(final long timeValue, final char meridianFlag) {
	    long nanosValue = timeValue;
	    boolean negative = false;

	    if (nanosValue < 0L) {
		nanosValue = -nanosValue;
		negative = true;
	    }

	    long nans = nanosValue % NANOSECONDS;
	    nanosValue /= NANOSECONDS;
	    long secs = nanosValue % 60L;
	    nanosValue /= 60L;
	    long mins = nanosValue % 60L;
	    nanosValue /= 60L;
	    long hours = nanosValue;

	    String meridian = null;
	    switch (meridianFlag) {
		case 'a':
		case 'A':
		case 'p':
		case 'P':
		    // Negative value just leaves the value as absolute
		    if (negative)
			break;

		    // hours is going to be absolute (0-24 or higher)
		    if (hours < 12L) {
			meridian = "am";
			if (hours == 0L)
			    hours += 12L;
		    }
		    else if (hours >= 12L && hours < 24L) {
			meridian = "pm";
			if (hours > 12L)
			    hours -= 12L;
		    }

		    // else outside of one day, just leave as absolute hours
		    break;
	    }

	    StringBuilder result = new StringBuilder(20);

	    if (negative)
		result.append('-');

	    result.append(String.format("%1$d:%2$02d", hours, mins));
	    if (secs != 0L || nans != 0L) {
		result.append(String.format(":%1$02d", secs));
	    }
	    if (nans != 0L) {
		// Produce either ".n", ".nn", ".nnn", ".nnnnnn", or ".nnnnnnnnn" forms
		if (nans % 100_000_000L== 0L)
		    result.append(String.format(".%1$d", (nans / 100_000_000L)));
		else if (nans % 10_000_000L == 0L)
		    result.append(String.format(".%1$02d", (nans / 10_000_000L)));
		else if (nans % 1_000_000L == 0L)
		    result.append(String.format(".%1$03d", (nans / 1_000_000L)));
		else if (nans % 1_000L == 0L)
		    result.append(String.format(".%1$06d", (nans / 1_000L)));
		else
		    result.append(String.format(".%1$09d", nans));
	    }

	    if (meridian != null) {
		result.append(' ').append(meridian);
	    }

	    return result.toString();
	}

	/**
	 * Convert from a duration value like <code>nn.nn [wdhms]</code> to nanoseconds.
	 *
	 * @param durString The input duration string.
	 * @return          Number of nanoseconds representing this duration.
	 * @throws IllegalArgumentException if the format isn't recognized.
	 */
	public static long convertFromDuration(final String durString) {
	    Matcher m = DURATION_PATTERN.matcher(durString);
	    if (m.matches()) {
		String number = m.group(2);
		String suffix = m.group(4);
		double value = Double.parseDouble(number);

		switch (suffix.charAt(0)) {
		    case 'w':
		    case 'W':
			value *= 7.0;
			// fall through
		    case 'd':
		    case 'D':
			value *= 24.0;
			// fall through
		    case 'h':
		    case 'H':
			value *= 60.0;
		    case 'm':
		    case 'M':
			value *= 60.0;
		    case 's':
		    case 'S':
			value *= 1_000_000_000.0;
			break;
		}

		if (m.group(1) != null)
		    return -((long) value);
		else
		    return (long) value;
	    }
	    else {
		throw new Intl.IllegalArgumentException("util#numeric.badDuration", durString);
	    }
	}

	/**
	 * Convert a number of nanoseconds to a duration value.
	 *
	 * @param nanos The long number of nanoseconds.
	 * @param unit  Character representing the desired duration unit ('w', 'd', 'h', etc.) or
	 *              if unknown, make it our choice depending on the magnitude.
	 * @param mc    The {@link MathContext} to use for rounding to decimal values.
	 * @param precision The decimal precision to round to (<code>Integer.MIN_VALUE</code> to ignore).
	 * @return      A string representing our best (fractional) representation of the duration
	 *              something like <code>23.75h</code>.
	 */
	public static String convertToDuration(final long nanos, final char unit, final MathContext mc, final int precision) {
	    char units = '?';
	    boolean negative = false;
	    BigDecimal decimal;

	    if (nanos < 0L) {
		negative = true;
		decimal = BigDecimal.valueOf(-nanos);
	    }
	    else {
		decimal = BigDecimal.valueOf(nanos);
	    }

	    switch (unit) {
		case 'w':
		case 'W':
		    units = 'w';
		    break;
		case 'd':
		case 'D':
		    units = 'd';
		    break;
		case 'h':
		case 'H':
		    units = 'h';
		    break;
		case 'm':
		case 'M':
		    units = 'm';
		    break;
		case 's':
		case 'S':
		    units = 's';
		    break;
		default:
		    // If units weren't specified, then figure it out
		    if (nanos > ONE_WEEK)
			units = 'w';
		    else if (nanos > ONE_DAY)
			units = 'd';
		    else if (nanos > ONE_HOUR)
			units = 'h';
		    else if (nanos > ONE_MINUTE)
			units = 'm';
		    else
			units = 's';
		    break;
	    }

	    switch (units) {
		case 'w':
		    decimal = decimal.divide(BigDecimal.valueOf(ONE_WEEK), mc);
		    break;
		case 'd':
		    decimal = decimal.divide(BigDecimal.valueOf(ONE_DAY), mc);
		    break;
		case 'h':
		    decimal = decimal.divide(BigDecimal.valueOf(ONE_HOUR), mc);
		    break;
		case 'm':
		    decimal = decimal.divide(BigDecimal.valueOf(ONE_MINUTE), mc);
		    break;
		default:
		    decimal = decimal.divide(D_NANOS, mc);
		    break;
	    }

	    // Do a final rounding if requested
	    if (precision != Integer.MIN_VALUE)
		decimal = MathUtil.round(decimal, precision);

	    return String.format("%1$s%2$s%3$c", negative ? "-" : "", decimal.toPlainString(), units);
	}

}
