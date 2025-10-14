/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2011,2013-2014,2016-2018,2020-2023,2025 Roger L. Whitcomb.
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
 *	26-Aug-2021 (rlwhitcomb)
 *	    Add a converter method for all the strange Unicode digit/number glyphs.
 *	02-Sep-2021 (rlwhitcomb)
 *	    Duration now uses BigInteger, not long (to fix overflow errors).
 *	01-Feb-2022 (rlwhitcomb)
 *	    #231: Use new Constants class values instead of our own.
 *	    #115: New RangeMode.getFrom method.
 *	14-Apr-2022 (rlwhitcomb)
 *	    #273: Move to "math" package.
 *	29-May-2022 (rlwhitcomb)
 *	    #301: Rework "convertToWords" for unlimited BigInteger range.
 *	31-May-2022 (rlwhitcomb)
 *	    #301: Next step of Conway-Guy-Wechsler algorithm for large exponent naming.
 *	08-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 *	11-Sep-2022 (rlwhitcomb)
 *	    #480: Extend range of byte values; switch to BigInteger. Add "formatToRangeLong".
 *	23-Sep-2022 (rlwhitcomb)
 *	    #52: Add overload of "convertToWords" for long values.
 *	28-Sep-2022 (rlwhitcomb)
 *	    #488: Add overloads for long values on the "formatToRange" methods. Refactor the
 *	    base method to reduce code duplication.
 *	30-Sep-2022 (rlwhitcomb)
 *	    #496: New parameter to "convertToWords" to add/eliminate commas.
 *	12-Oct-2022 (rlwhitcomb)
 *	    #514: Move resource text from "util" package to here.
 *	04-Nov-2022 (rlwhitcomb)
 *	    #48: Introduce TINY format for names (for "Dir" utility).
 *	03-May-2023 (rlwhitcomb)
 *	    #599: New option for "convertToWords" for British usage (to use "and" as in "three hundred and twenty").
 *	07-Apr-2025 (rlwhitcomb)
 *	    #711: Tweak some values in the number word algorithm.
 *	05-Jun-2025 (rlwhitcomb)
 *	    #711: Tweak that last code in the number -> word function.
 *	20-Sep-2025 (rlwhitcomb)
 *	    Fix "getRangeBySuffix" for single-char match (which is allowed by the Calc grammar).
 *	13-Oct-2025 (rlwhitcomb)
 *	    #777: Update the range table with the new official prefixes: "ronna" and "quetta".
 *	    Allow decimals in KMG values; update "convertKMGValue" to use BigDecimal.
 */
package info.rlwhitcomb.math;

import info.rlwhitcomb.util.CharUtil;
import info.rlwhitcomb.util.Constants;
import info.rlwhitcomb.util.Intl;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static info.rlwhitcomb.util.Constants.*;


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
			throw new Intl.IllegalArgumentException("math#numeric.badDataType", value.getClass().getName());
		    }
		    return type;
		}

		static DataType fromCode(final int code) {
		    if (code < 0 || code > numValues) {
			throw new Intl.IllegalArgumentException("math#numeric.badDataTypeCode", code);
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
		    throw new Intl.IllegalArgumentException("math#numeric.unknownByteOrder", value);
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
		    throw new Intl.IllegalArgumentException("math#numeric.unknownStringLength", value);
		}
	}


	private static final Pattern VALUE_MATCH= Pattern.compile("^([0-9\\.]+)([kKmMgGtTpPeEzZyYrRqQ][iI]?[bB]?)$");


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

	/** Decimal value of {@link Constants#NANOSECONDS}. */
	private static final BigDecimal D_NANOS = BigDecimal.valueOf(NANOSECONDS);

	/** Number of bits required for each successive power of ten (value of <code>ln2(10)</code>). */
	private static final double BITS_PER_POWER = 3.32192809488736234787031942948939d;


	private static final String[] SMALL_WORDS = {
		"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
		"ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen",
		"eighteen", "nineteen"
	};
	private static final String[] TENS_WORDS = {
		"twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"
	};
	private static final String SMALL_TABLE[] = {
	    "ni", "mi", "bi", "tri", "quadri", "quinti", "sexti", "septi", "octi", "noni"
	};
	private static final String UNITS_TABLE[] = {
	    // This follows the table in "The Book of Numbers" on page 15 EXCEPT they have "quin" as "quinqua"
	    // which doesn't agree with common usage (as in "quindecillion"), nor with the Wikipedia example table.
	    "un", "duo", "tre", "quattuor", "quin", "se", "septe", "octo", "nove"
	};
	private static final String TENS_TABLE[] = {
	    "deci", "viginti", "triginta", "quadraginta", "quinquaginta", "sexaginta", "septuaginta", "octoginta", "nonaginta"
	};
	private static final String HUNDREDS_TABLE[] = {
	    "centi", "ducenti", "trecenti", "quadringenti", "quingenti", "sescenti", "septingenti", "octingenti", "nongenti"
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
		MIXED;


		public static RangeMode getFrom(Object obj) {
		    if (obj instanceof RangeMode)
			return (RangeMode) obj;

		    String name = obj.toString();
		    return valueOf(name.toUpperCase());
		}
	}

	/**
	 * Enum to represent the range of a numeric number of bytes.
	 */
	public static enum Range
	{
		/* Range,   mult (10),mult (2),suffixes,    short name,          long name                   */
		BYTES	    (0,       0,       "B",  "B",   "bytes",  "bytes",   "Bytes",       "Bytes"      ),
		KILOBYTES   (3,       10,      "KB", "KiB", "Kbytes", "Kibytes", "Kilobytes",   "Kibibytes"  ),
		MEGABYTES   (6,       20,      "MB", "MiB", "Mbytes", "Mibytes", "Megabytes",   "Mebibytes"  ),
		GIGABYTES   (9,       30,      "GB", "GiB", "Gbytes", "Gibytes", "Gigabytes",   "Gibibytes"  ),
		TERABYTES   (12,      40,      "TB", "TiB", "Tbytes", "Tibytes", "Terabytes",   "Tebibytes"  ),
		PETABYTES   (15,      50,      "PB", "PiB", "Pbytes", "Pibytes", "Petabytes",   "Pebibytes"  ),
		EXABYTES    (18,      60,      "EB", "EiB", "Ebytes", "Eibytes", "Exabytes",    "Exbibytes"  ),
		ZETTABYTES  (21,      70,      "ZB", "ZiB", "Zbytes", "Zibytes", "Zettabytes",  "Zebibytes"  ),
		YOTTABYTES  (24,      80,      "YB", "YiB", "Ybytes", "Yibytes", "Yottabytes",  "Yobibytes"  ),
		RONNABYTES  (27,      90,      "RB", "RiB", "Rbytes", "Ribytes", "Ronnabytes",  "Robibytes"  ),
		QUETTABYTES (30,     100,      "QB", "QiB", "Qbytes", "Qibytes", "Quettabytes", "Quebibytes" );

		private int tenPower;
		private int twoPower;
		private String siSuffix;
		private String binSuffix;
		private String siShortName;
		private String binShortName;
		private String siLongName;
		private String binLongName;

		private Range(final int tenMult,    final int twoMult,
			      final String siSfx,   final String binSfx,
			      final String siShort, final String binShort,
			      final String siLong,  final String binLong) {
		    this.tenPower      = tenMult;
		    this.twoPower      = twoMult;
		    this.siSuffix      = siSfx;
		    this.binSuffix     = binSfx;
		    this.siShortName   = siShort;
		    this.binShortName  = binShort;
		    this.siLongName    = siLong;
		    this.binLongName   = binLong;
		}

		/** @return The mixed-mode (1024-based) multiplier for this range. */
		public BigInteger getMultiplier() {
		    return getMultiplier(RangeMode.MIXED);
		}

		/**
		 * @return The multiplier for this range, either SI or binary.
		 * @param mode Decide which value to use.
		 */
		public BigInteger getMultiplier(final RangeMode mode) {
		    switch (mode) {
			case BINARY:
			default:
			    return I_TWO.pow(twoPower);
			case DECIMAL:
			    return I_TEN.pow(tenPower);
		    }
		}

		/**
		 * @return The decimal multiplier for this range, either SI or binary.
		 * @param mode Decide which value to use.
		 */
		public BigDecimal getDecMultiplier(final RangeMode mode) {
		    switch (mode) {
			case BINARY:
			default:
			    return D_TWO.pow(twoPower);
			case DECIMAL:
			    return D_TEN.pow(tenPower);
		    }
		}

		/** @return The mixed-mode (1000-based) tiny format name. */
		public String getTiny() {
		    String suffix = getSuffix();
		    if (suffix.equals("B"))
			suffix = " ";
		    else
			suffix = suffix.substring(0, 1);
		    return suffix;
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
		    if (suffix.length() == 1) {
			char single = Character.toUpperCase(suffix.charAt(0));
			for (Range r : values()) {
			    if (r.siSuffix.charAt(0) == single)
				return r;
			}
		    }
		    else {
			for (Range r : values()) {
			    if (r.siSuffix.equalsIgnoreCase(suffix) || r.binSuffix.equalsIgnoreCase(suffix))
				return r;
			}
		    }
		    return null;
		}

		/** @return A {@link Range} value that best represents the given value,
		 * in mixed-mode units (1024-based, but 1000-based names).
		* This will result in a formatted value in the range of 0.00 .. 799.99;
		 * @param value The candidate value.
		 */
		public static Range getRangeOfValue(final BigInteger value) {
		    return getRangeOfValue(value, RangeMode.MIXED);
		}

		/** @return A {@link Range} value that best represents the given value.
		 * This will result in a formatted value in the range of 0.80 .. 799.99.
		 * @param value The candidate value.
		 * @param mode Decide which value to use.
		 */
		public static Range getRangeOfValue(final BigInteger value, final RangeMode mode) {
		    BigInteger absValue = value.abs();

		    if (absValue.compareTo(BigInteger.ONE) <= 0)
			return BYTES;

		    for (Range r : values()) {
			switch (mode) {
			    case BINARY:
			    default:
				if (absValue.divide(r.getMultiplier(RangeMode.BINARY)).compareTo(I_800) < 0)
				    return r;
				break;
			    case DECIMAL:
				if (absValue.divide(r.getMultiplier(RangeMode.DECIMAL)).compareTo(I_800) < 0)
				    return r;
				break;
			}
		    }
		    // Well beyond the range of our largest, but use it anyway
		    return Range.QUETTABYTES;
		}
	}


	/**
	 * Which name format to use?
	 */
	public static enum NameFormat
	{
		/** Tiny, with no suffix for bytes, and just {@code K} for multiples. */
		TINY,
		/** Just a suffix, such as {@code B}, {@code KB}, {@code MB}, etc. */
		SUFFIX,
		/** The short name, such as {@code KBytes}, {@code MBytes}, etc. */
		SHORT,
		/** The long name, such as {@code Kilobytes}, {@code Megabytes}, etc. */
		LONG
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
	public static BigDecimal convertKMGValue(final String input) {
	    Matcher m = VALUE_MATCH.matcher(input);
	    if (m.matches()) {
		BigDecimal value = new BigDecimal(m.group(1));
		String suffix = m.group(2);
		Range range = Range.getRangeBySuffix(suffix);
		if (range != null) {
		    String end = suffix.toUpperCase();
		    if (end.endsWith("IB") || end.endsWith("I"))
			value = value.multiply(range.getDecMultiplier(RangeMode.BINARY));
		    else
			value = value.multiply(range.getDecMultiplier(RangeMode.DECIMAL));
		}
		return value.stripTrailingZeros();
	    }
	    else
		throw new NumberFormatException(Intl.getString("math#numeric.badKMGFormat"));
	}


	/**
	 * Format a value using the {@link Range} enum to put into a readable range,
	 * using mixed-mode units.
	 *
	 * @param	value	The input value to format.
	 * @return		The value formatted to an appropriate range.
	 */
	public static String formatToRange(final long value) {
	    return internalFormatToRange(BigInteger.valueOf(value), RangeMode.MIXED, NameFormat.SHORT);
	}


	/**
	 * Format a value using the {@link Range} enum to put into a readable range,
	 * using mixed-mode units.
	 *
	 * @param	value	The input value to format.
	 * @return		The value formatted to an appropriate range.
	 */
	public static String formatToRange(final BigInteger value) {
	    return internalFormatToRange(value, RangeMode.MIXED, NameFormat.SHORT);
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
	    return internalFormatToRange(BigInteger.valueOf(value), mode, NameFormat.SHORT);
	}


	/**
	 * Format a value using the {@link Range} enum to put into a readable range,
	 * using either Binary or SI-based units.
	 *
	 * @param	value	The input value to format.
	 * @param	mode	Decide which value to use.
	 * @return		The value formatted to an appropriate range.
	 */
	public static String formatToRange(final BigInteger value, final RangeMode mode) {
	    return internalFormatToRange(value, mode, NameFormat.SHORT);
	}


	/**
	 * Format a value using the {@link Range} enum to put into a readable range,
	 * but use just the tiny format (to save space).
	 *
	 * @param	value	The input value to format.
	 * @return		The value formatted (tiny form) to an appropriate range.
	 */
	public static String formatToRangeTiny(final long value) {
	    return internalFormatToRange(BigInteger.valueOf(value), RangeMode.MIXED, NameFormat.TINY);
	}


	/**
	 * Format a value using the {@link Range} enum to put into a readable range,
	 * but use just the tiny format (to save space).
	 *
	 * @param	value	The input value to format.
	 * @return		The value formatted (tiny form) to an appropriate range.
	 */
	public static String formatToRangeTiny(final BigInteger value) {
	    return internalFormatToRange(value, RangeMode.MIXED, NameFormat.TINY);
	}


	/**
	 * Format a value using the {@link Range} enum to put into a readable range,
	 * but use just the tiny format (to save space).
	 *
	 * @param	value	The input value to format.
	 * @param	mode	Whether to use binary or SI units.
	 * @return		The value formatted (tiny form) to an appropriate range.
	 */
	public static String formatToRangeTiny(final long value, final RangeMode mode) {
	    return internalFormatToRange(BigInteger.valueOf(value), mode, NameFormat.TINY);
	}


	/**
	 * Format a value using the {@link Range} enum to put into a readable range,
	 * but use just the tiny format (to save space).
	 *
	 * @param	value	The input value to format.
	 * @param	mode	Whether to use binary or SI units.
	 * @return		The value formatted (tiny form) to an appropriate range.
	 */
	public static String formatToRangeTiny(final BigInteger value, final RangeMode mode) {
	    return internalFormatToRange(value, mode, NameFormat.TINY);
	}


	/**
	 * Format a value using the {@link Range} enum to put into a readable range,
	 * but use just the suffix (to save space).
	 *
	 * @param	value	The input value to format.
	 * @return		The value formatted (short form) to an appropriate range.
	 */
	public static String formatToRangeShort(final long value) {
	    return internalFormatToRange(BigInteger.valueOf(value), RangeMode.MIXED, NameFormat.SUFFIX);
	}


	/**
	 * Format a value using the {@link Range} enum to put into a readable range,
	 * but use just the suffix (to save space).
	 *
	 * @param	value	The input value to format.
	 * @return		The value formatted (short form) to an appropriate range.
	 */
	public static String formatToRangeShort(final BigInteger value) {
	    return internalFormatToRange(value, RangeMode.MIXED, NameFormat.SUFFIX);
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
	    return internalFormatToRange(BigInteger.valueOf(value), mode, NameFormat.SUFFIX);
	}


	/**
	 * Format a value using the {@link Range} enum to put into a readable range,
	 * but use just the suffix (to save space).
	 *
	 * @param	value	The input value to format.
	 * @param	mode	Whether to use binary or SI units.
	 * @return		The value formatted (short form) to an appropriate range.
	 */
	public static String formatToRangeShort(final BigInteger value, final RangeMode mode) {
	    return internalFormatToRange(value, mode, NameFormat.SUFFIX);
	}


	/**
	 * Format a value using the {@link Range} enum to put into a readable range,
	 * but use the long name (such as "Kilobytes").
	 *
	 * @param	value	The input value to format.
	 * @return		The value formatted (long form) to an appropriate range.
	 */
	public static String formatToRangeLong(final long value) {
	    return internalFormatToRange(BigInteger.valueOf(value), RangeMode.MIXED, NameFormat.LONG);
	}


	/**
	 * Format a value using the {@link Range} enum to put into a readable range,
	 * but use the long name (such as "Kilobytes").
	 *
	 * @param	value	The input value to format.
	 * @return		The value formatted (long form) to an appropriate range.
	 */
	public static String formatToRangeLong(final BigInteger value) {
	    return internalFormatToRange(value, RangeMode.MIXED, NameFormat.LONG);
	}


	/**
	 * Format a value using the {@link Range} enum to put into a readable range,
	 * but use the long name (such as "Kilobytes").
	 *
	 * @param	value	The input value to format.
	 * @param	mode	Whether to use binary or SI units.
	 * @return		The value formatted (long form) to an appropriate range.
	 */
	public static String formatToRangeLong(final long value, final RangeMode mode) {
	    return internalFormatToRange(BigInteger.valueOf(value), mode, NameFormat.LONG);
	}


	/**
	 * Format a value using the {@link Range} enum to put into a readable range,
	 * but use the long name (such as "Kilobytes").
	 *
	 * @param	value	The input value to format.
	 * @param	mode	Whether to use binary or SI units.
	 * @return		The value formatted (long form) to an appropriate range.
	 */
	public static String formatToRangeLong(final BigInteger value, final RangeMode mode) {
	    return internalFormatToRange(value, mode, NameFormat.LONG);
	}


	/**
	 * Format a value using the {@link Range} enum to put into a readable range,
	 * using the specified name format (suffix, short, or long).
	 *
	 * @param	value	The input value to format.
	 * @param	mode	Whether to use binary or SI units.
	 * @param	name	Format of unit name to use.
	 * @return		The value formatted (name form) to an appropriate range.
	 */
	public static String formatToRange(final long value, final RangeMode mode, final NameFormat name) {
	    return internalFormatToRange(BigInteger.valueOf(value), mode, name);
	}


	/**
	 * Format a value using the {@link Range} enum to put into a readable range,
	 * using the specified name format (suffix, short, or long).
	 *
	 * @param	value	The input value to format.
	 * @param	mode	Whether to use binary or SI units.
	 * @param	name	Format of unit name to use.
	 * @return		The value formatted (name form) to an appropriate range.
	 */
	public static String formatToRange(final BigInteger value, final RangeMode mode, final NameFormat name) {
	    return internalFormatToRange(value, mode, name);
	}


	/**
	 * Format a value using the {@link Range} enum to put into a readable range,
	 * using the specified name format (suffix, short, or long).
	 *
	 * @param	value	The input value to format.
	 * @param	mode	Whether to use binary or SI units.
	 * @param	name	Format of unit name to use.
	 * @return		The value formatted (name form) to an appropriate range.
	 */
	private static String internalFormatToRange(final BigInteger value, final RangeMode mode, final NameFormat name) {
	    String units = "";

	    Range r = Range.getRangeOfValue(value, mode);

	    switch (name) {
		case TINY:   units = r.getTiny();          break;
		case SUFFIX: units = r.getSuffix(mode);    break;
		case SHORT:  units = r.getShortName(mode); break;
		case LONG:   units = r.getLongName(mode);  break;
	    }

	    if (r == Range.BYTES) {
		if (name == NameFormat.TINY)
		    return value.toString();
		else
		    return String.format("%1$s %2$s", value.toString(), units);
	    } else {
		double scaledValue = value.doubleValue() / r.getMultiplier(mode).doubleValue();

		if (scaledValue < 10.0d) {
		    if (name == NameFormat.TINY)
			return String.format("%1$3.2f%2$s", scaledValue, units);
		    else
			return String.format("%1$3.2f %2$s", scaledValue, units);
		}
		else if (scaledValue < 100.0d) {
		    if (name == NameFormat.TINY)
			return String.format("%1$3.1f%2$s", scaledValue, units);
		    else
			return String.format("%1$3.1f %2$s", scaledValue, units);
		}
		else {
		    if (name == NameFormat.TINY)
			return String.format("%1$3.0f%2$s", scaledValue, units);
		    else
			return String.format("%1$3.0f %2$s", scaledValue, units);
		}
	    }
	}


	/**
	 * The "long" range name will be something like "Kilobytes", or "Megabytes".
	 *
	 * @param	value	The input value to test for range.
	 * @return		The long name of the appropriate range for this value.
	 */
	public static String getLongRangeName(final BigInteger value) {
	    return Range.getRangeOfValue(value).getLongName();
	}


	/**
	 * The "long" range name will be something like "Kilobytes", or "Megabytes".
	 *
	 * @param	value	The input value to test for range.
	 * @param	mode	Whether to use binary or SI units.
	 * @return		The long name of the appropriate range for this value.
	 */
	public static String getLongRangeName(final BigInteger value, final RangeMode mode) {
	    return Range.getRangeOfValue(value, mode).getLongName(mode);
	}


	/**
	 * Create the appropriate "zillion" name for the given power of ten base.
	 *
	 * @param base The power of ten base to derive a name for.
	 * @return     The appropriate name.
	 * @see #getZillionName(int, boolean, boolean)
	 */
	public static String getZillionName(final int base) {
	    return getZillionName(base, false, true);
	}


	/**
	 * Create the appropriate "zillion" name for the given power of ten base.
	 * <p> This is taken from a proposal by Conway &amp; Guy in "The Book of Numbers"
	 * chapter one, pp 14-15 and referenced from here:
	 * <a href="https://en.wikipedia.org/wiki/Names_of_large_numbers">https://en.wikipedia.org/wiki/Names_of_large_numbers</a>
	 * <p> Given a power of ten which is <code>3 * N + 3</code> we need a name for the base N, which we derive here.
	 * <p> This algorithm extends to infinity using the Wechsler proposal given in the book, except as a practical note
	 * we are only allowing a base up to less than 1,000,000,000.
	 *
	 * @param base       The power of ten base to derive a name for.
	 * @param useNillion For recursive use beyond N = 1000, use "nillion" for zero values.
	 * @param addSuffix  Also for recursive use, whether to add the "illion", or "illi" suffix.
	 * @return           The appropriate name, given the convention, such as <code>29 -&gt; "novemvigintillion"</code>.
	 */
	public static String getZillionName(final int base, final boolean useNillion, final boolean addSuffix) {
	    StringBuilder buf = new StringBuilder(60);

	    if ((base == 0 && useNillion) || (base >= 1 && base <= 9)) {
		buf.append(SMALL_TABLE[base]);
	    }
	    else if (base >= 1_000) {
		if (base >= 1_000_000_000) {
		    // Note: this can go on indefinitely toward infinity, but this will suffice for all our needs
		    throw new Intl.IllegalArgumentException("math#numeric.outOfRangeWords");
		}
		if (base >= 1_000_000) {
		    int millis = base / 1_000_000;
		    int thous  = (base % 1_000_000) / 1_000;
		    int ones   = base % 1_000;

		    buf.append(getZillionName(millis, true, true));
		    buf.append(getZillionName(thous, true, true));
		    buf.append(getZillionName(ones, true, false));
		}
		else {
		    int thous = base / 1_000;
		    int ones  = base % 1_000;

		    buf.append(getZillionName(thous, true, true));
		    buf.append(getZillionName(ones, true, false));
		}
	    }
	    else {
		int _100s = base / 100;
		int _10s = (base % 100) / 10;
		int _1s = base % 10;

		if (_1s != 0)
		    buf.append(UNITS_TABLE[_1s - 1]);

		char combine = ' ';
		switch (_1s) {
		    case 3: combine = 's'; break;
		    case 6: combine = 'x'; break;
		    case 7:
		    case 9: combine = 'n'; break;
		}

		if (_10s != 0) {
		    switch (_10s) {
			case 1:
			case 6:
			case 7: if (combine == 'n') buf.append(combine);
				break;
			case 2: if (combine == 'n') buf.append('m');
				else if (combine == 's' || combine == 'x') buf.append('s');
				break;
			case 3:
			case 4:
			case 5: if (combine == 'n') buf.append(combine);
				else if (combine == 's' || combine == 'x') buf.append('s');
				break;
			case 8: if (combine == 'n') buf.append('m');
				else if (combine == 's' || combine == 'x') buf.append(combine);
				break;
		    }
		    combine = ' ';
		    buf.append(TENS_TABLE[_10s - 1]);
		}

		if (_100s != 0) {
		    switch (_100s) {
			case 1: if (combine == 'n' || combine == 's' || combine == 'x') buf.append(combine);
				break;
			case 2:
			case 6:
			case 7: if (combine == 'n') buf.append(combine);
				break;
			case 3:
			case 4:
			case 5: if (combine == 'n') buf.append(combine);
				else if (combine == 's' || combine == 'x') buf.append('s');
				break;
			case 8: if (combine == 'n') buf.append('m');
				else if (combine == 's' || combine == 'x') buf.append(combine);
				break;
		    }
		    buf.append(HUNDREDS_TABLE[_100s - 1]);
		}
	    }

	    // Replace last vowel with "illion"
	    int len = buf.length();
	    if (addSuffix && len > 0 && "aeiou".indexOf(buf.charAt(len - 1)) >= 0) {
		buf.replace(len - 1, len, useNillion ? "illi" : "illion");
	    }

	    // The Conway-Guy-Wechsler system generates two values that are not in the dictionary
	    // these days, so switch them out.
	    // Found the problem from this:
	    // https://www.quora.com/What-are-some-of-the-most-mind-blowing-facts/answer/Sunil-Kumar-Singh-38?ch=17&oid=53430214&share=d8589e1a&srid=obNJ&target_type=answer
	    // Note: this could probably easily be done with the combining tables but I'm too tired tonight to figure it out...
	    // "sedeci" -> "sexdeci" and "novendeci" -> "novemdeci"
	    int ix;
	    while ((ix = buf.indexOf("sedeci")) >= 0)
		buf.insert(ix + 2, 'x');
	    while ((ix = buf.indexOf("novendeci")) >= 0)
		buf.setCharAt(ix + 4, 'm');

	    return buf.toString();
	}


	/**
	 * Convert a long number to words.
	 *
	 * @param	value	The value to convert.
	 * @return		The value converted to its English name.
	 * @see	#convertToWords(BigInteger)
	 */
	public static String convertToWords(final long value) {
	    return convertToWords(BigInteger.valueOf(value));
	}


	/**
	 * Convert a long number to words, with optional comma separators..
	 *
	 * @param	value	The value to convert.
	 * @param	commas	Whether to use commas to separate 10**3 blocks.
	 * @return		The value converted to its English name.
	 * @see	#convertToWords(BigInteger, boolean)
	 */
	public static String convertToWords(final long value, final boolean commas) {
	    return convertToWords(BigInteger.valueOf(value), commas);
	}


	/**
	 * Convert a BigInteger number to words.
	 * <p>Examples:
	 * <ul><li>10 -&gt; ten
	 * <li>27 -&gt; twenty-seven
	 * <li>493 -&gt; four hundred ninety-three
	 * </ul>
	 *
	 * @param	value	The value to convert.
	 * @return		The value written out as its English name.
	 * @see #convertToWords(BigInteger, StringBuilder, boolean, boolean)
	 */
	public static String convertToWords(final BigInteger value) {
	    StringBuilder buf = new StringBuilder();
	    convertToWords(value, buf, false, false);
	    return buf.toString();
	}


	/**
	 * Convert a BigInteger number to words.
	 * <p>Examples:
	 * <ul><li>10 -&gt; ten
	 * <li>27 -&gt; twenty-seven
	 * <li>493 -&gt; four hundred ninety-three
	 * </ul>
	 *
	 * @param	value	The value to convert.
	 * @param	commas	Whether to use commas to separate 10**3 blocks.
	 * @return		The value written out as its English name.
	 * @see #convertToWords(BigInteger, StringBuilder, boolean, boolean)
	 */
	public static String convertToWords(final BigInteger value, final boolean commas) {
	    StringBuilder buf = new StringBuilder();
	    convertToWords(value, buf, commas, false);
	    return buf.toString();
	}


	/**
	 * Convert a BigInteger number to words, with optional comma separators.
	 * <p>Examples:
	 * <ul><li>10 -&gt; ten
	 * <li>27 -&gt; twenty-seven
	 * <li>493 -&gt; four hundred ninety-three
	 * </ul>
	 *
	 * @param	iValue	The value to convert.
	 * @param	buf	Buffer to append the value to.
	 * @param	commas	Whether to use commas to separate 10**3 blocks.
	 * @param	useAnd	For "British" usage, add "and" for the hundreds residues.
	 */
	public static void convertToWords(final BigInteger iValue, final StringBuilder buf, final boolean commas, final boolean useAnd) {
	    BigInteger value = iValue;
	    int sign = value.signum();
	    int bits = value.bitLength();

	    if (sign < 0) {
		buf.append(minus).append(' ');
		value = value.negate();
	    }
	    if (value.compareTo(I_TWENTY) < 0) {
		buf.append(SMALL_WORDS[value.intValue()]);
	    }
	    else if (value.compareTo(I_HUNDRED) < 0) {
		int ivalue = value.intValue();
		int decade = ivalue / 10;
		int residual = ivalue % 10;
		buf.append(TENS_WORDS[decade - 2]);
		if (residual != 0) {
		    buf.append('-');
		    buf.append(SMALL_WORDS[residual]);
		}
	    }
	    else if (value.compareTo(I_THOUSAND) < 0) {
		int ivalue = value.intValue();
		int chiliad = ivalue / 100;
		int residual = ivalue % 100;
		buf.append(SMALL_WORDS[chiliad]).append(" hundred");
		if (residual != 0) {
		    buf.append(useAnd ? " and " : " ");
		    convertToWords(BigInteger.valueOf(residual), buf, commas, useAnd);
		}
	    }
	    else if (value.compareTo(I_MILLION) < 0) {
		int ivalue = value.intValue();
		int milliad = ivalue / 1000;
		int residual = ivalue % 1000;
		convertToWords(BigInteger.valueOf(milliad), buf, commas, useAnd);
		buf.append(" thousand");
		if (residual != 0) {
		    buf.append(commas ? ", " : " ");
		    convertToWords(BigInteger.valueOf(residual), buf, commas, useAnd);
		}
	    }
	    else {
		// Underestimate a bit of the power of ten we're dealing with based on the number of bits
		// used to store the value.
		int tenpow = ((int) Math.floor((double) bits / BITS_PER_POWER) / 3 - 1) * 3;
		BigInteger scale = I_TEN.pow(tenpow);
		BigInteger oldScale = scale;

		while (value.compareTo(scale) >= 0) {
		    tenpow += 3;
		    oldScale = scale;
		    scale = scale.multiply(I_THOUSAND);
		}
		// Once we've found the proper scale for the value, back off to the previous
		// 10**3 range
		scale = oldScale;
		tenpow -= 3;

		BigInteger[] parts = value.divideAndRemainder(scale);
		convertToWords(parts[0], buf, commas, useAnd);
		buf.append(' ').append(getZillionName((tenpow - 3) / 3));
		BigInteger residual = parts[1];
		if (residual.signum() != 0) {
		    buf.append(commas ? ", " : " ");
		    convertToWords(residual, buf, commas, useAnd);
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
			throw new Intl.IndexOutOfBoundsException("math#numeric.stringLengthTooBig", byteLen, 255);
		    dos.write(byteLen);
		    dos.write(bytes);
		    break;
		case PREFIX2:
		    // Need a range of 0..65535 here
		    if (byteLen > 65535)
			throw new Intl.IndexOutOfBoundsException("math#numeric.stringLengthTooBig", byteLen, 65_535);
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
		    throw new Intl.IllegalArgumentException("math#numeric.badDataType", className);
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
		throw new Intl.IllegalArgumentException("math#numeric.badRomanFormat", input);
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
		throw new Intl.IllegalArgumentException("math#numeric.outOfRomanRange", value);
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
		    nanosecs *= NANOSECONDS;

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
		throw new Intl.IllegalArgumentException("math#numeric.badTimeValue", timeString);
	    }
	}

	/**
	 * Convert a long number of nanoseconds since midnight into a time string, omitting
	 * trailing parts if they are zero (so, the minimum output is "HH:mm").
	 *
	 * @param timeValue The input nanosecond value.
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
	public static BigInteger convertFromDuration(final String durString) {
	    Matcher m = DURATION_PATTERN.matcher(durString);
	    if (m.matches()) {
		String number = m.group(2);
		String suffix = m.group(4);
		BigDecimal value = new BigDecimal(number);

		switch (suffix.charAt(0)) {
		    case 'w':
		    case 'W':
			value = value.multiply(D_SEVEN);
			// fall through
		    case 'd':
		    case 'D':
			value = value.multiply(D_24);
			// fall through
		    case 'h':
		    case 'H':
			value = value.multiply(D_60);
		    case 'm':
		    case 'M':
			value = value.multiply(D_60);
		    case 's':
		    case 'S':
			value = value.multiply(D_NANOS);
			break;
		}

		if (m.group(1) != null)
		    return value.toBigInteger().negate();
		else
		    return value.toBigInteger();
	    }
	    else {
		throw new Intl.IllegalArgumentException("math#numeric.badDuration", durString);
	    }
	}

	/**
	 * Convert a number of nanoseconds to a duration value.
	 *
	 * @param nanos The number of nanoseconds.
	 * @param unit  Character representing the desired duration unit ('w', 'd', 'h', etc.) or
	 *              if unknown, make it our choice depending on the magnitude.
	 * @param mc    The {@link MathContext} to use for rounding to decimal values.
	 * @param precision The decimal precision to round to (<code>Integer.MIN_VALUE</code> to ignore).
	 * @return      A string representing our best (fractional) representation of the duration
	 *              something like <code>23.75h</code>.
	 */
	public static String convertToDuration(final BigInteger nanos, final char unit, final MathContext mc, final int precision) {
	    char units = '?';
	    boolean negative = false;
	    BigDecimal decimal;

	    if (nanos.signum() < 0) {
		negative = true;
		decimal = new BigDecimal(nanos.negate());
	    }
	    else {
		decimal = new BigDecimal(nanos);
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
		    if (nanos.compareTo(BigInteger.valueOf(ONE_WEEK)) > 0)
			units = 'w';
		    else if (nanos.compareTo(BigInteger.valueOf(ONE_DAY)) > 0)
			units = 'd';
		    else if (nanos.compareTo(BigInteger.valueOf(ONE_HOUR)) > 0)
			units = 'h';
		    else if (nanos.compareTo(BigInteger.valueOf(ONE_MINUTE)) > 0)
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

	/**
	 * The conversion tables for the dingbat numbers.
	 */
	private static final int DINGBAT_TABLE[][] = {
	 /* base,  start,   end   */
	    {  1, 0x2460,  0x2473  },
	    {  1, 0x2474,  0x2487  },
	    {  1, 0x2488,  0x249B  },
	    {  0, 0x24EA,  0x24EA  },
	    {  0, 0x24FF,  0x24FF  },
	    { 11, 0x24EB,  0x24F4  },
	    {  1, 0x24F5,  0x24FE  },
	    {  1, 0x2776,  0x277F  },
	    {  1, 0x2780,  0x2789  },
	    {  1, 0x278A,  0x2793  },
	    {  0, 0xFF10,  0xFF19  },
	    {  0, 0x1D7CE, 0x1D7D7 },
	    {  0, 0x1D7D8, 0x1D7E1 },
	    {  0, 0x1D7E2, 0x1D7EB },
	    {  0, 0x1D7EC, 0x1D7F5 },
	    {  0, 0x1D7F6, 0x1D7FF }
	};

	/**
	 * Convert a single codepoint dingbat number to a real integer.
	 * <p> This will convert things like &#x2460; or &#xFF10; to their equivalent numeric values.
	 *
	 * @param input	The input codepoint.
	 * @return	The value it represents.
	 * @throws	IllegalArgumentException if the input value isn't something we recognize.
	 */
	public static int convertDingbatNumber(final int input) {
	    for (int i = 0; i < DINGBAT_TABLE.length; i++) {
		int base  = DINGBAT_TABLE[i][0];
		int start = DINGBAT_TABLE[i][1];
		int end   = DINGBAT_TABLE[i][2];
		if (input >= start && input <= end) {
		    return input - start + base;
		}
	    }
	    // If the value wasn't matched, then we have an error
	    String value = new String(Character.toChars(input));
	    throw new Intl.IllegalArgumentException("math#numeric.badNumberSymbol", input, value);
	}

}
