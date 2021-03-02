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
import java.math.RoundingMode;
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
public class NumericUtil
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


	private static final Logging logger = new Logging(NumericUtil.class);


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

	private static final BigInteger I_TWO = BigInteger.valueOf(2L);
	private static final BigDecimal D_TWO = BigDecimal.valueOf(2L);

	/**
	 * A rational approximation of PI good to ~25 decimal digits.
	 * This is the fastest way to calculate the value for such small precision.
	 * <p> Sourced from: <a href="http://oeis.org/A002485">A002485</a> and
	 * <a href="http://oeis.org/A002486">A002486</a>.
	 */
	private static final BigFraction PI_APPROX = new BigFraction(8958937768937L, 2851718461558L);
	/** The largest set of calculated PI digits so far. */
	private static String PI_DIGITS = null;
	/** The previously calculated PI value (if any); cached to eliminate repeated costly calculations. */
	private static BigDecimal CALCULATED_PI = null;
	/* Some related values calculated at the same time (for convenience). */
	private static BigDecimal TWO_PI;
	private static BigDecimal MINUS_TWO_PI;
	private static BigDecimal PI_OVER_TWO;
	private static BigDecimal MINUS_PI_OVER_TWO;


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
	 * using mixed-mode units..
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


	/**
	 * Round up a value to the next highest power of two.
	 *
	 * @param n Any non-negative number.
	 * @return The next highest power of two greater or equal to
	 * the input.
	 */
	public static int roundUpPowerTwo(final int n) {
	    int p = 1;

	    // Check for exact power of two
//	    if (n != 0 && (n & (n - 1)) == 0)
//		return n;

	    while (p < n)
		p <<= 1;

	    return p;
	}


	/**
	 * @return The result of the base to the exp power, done in <code>BigDecimal</code>
	 * precision.
	 * @param base     The number to raise to the given power.
	 * @param inputExp The power to raise the number to.
	 * @throws IllegalArgumentException if the exponent is infinite or not-a-number.
	 */
	public static BigDecimal pow(final BigDecimal base, final double inputExp) {
	    double exp = inputExp;
	    if (Double.isNaN(exp) || Double.isInfinite(exp))
		throw new Intl.IllegalArgumentException("util#numeric.outOfRange");

	    if (exp == 0.0d)
		return BigDecimal.ONE;

	    boolean reciprocal = false;
	    if (exp < 0) {
		reciprocal = true;
		exp = -exp;
	    }

	    int intExp     = (int)Math.floor(exp);
	    double fracExp = exp - (double)intExp;

	    // Turn an integer power of two into a "setBit" on a BigInteger
	    if (base.equals(D_TWO) && (double)intExp == inputExp) {
		BigInteger value = BigInteger.ZERO.setBit(intExp);
		return new BigDecimal(value);
	    }

	    BigDecimal result = BigDecimal.ONE;
	    BigDecimal mult   = base;
	    for (int iExp = intExp; iExp != 0; iExp >>= 1) {
		if (iExp % 2 == 1)
		    result = result.multiply(mult);
		mult = mult.multiply(mult);
	    }

	    // 2.14**2.14 = 2.14**2 * 2.14**.14
	    BigDecimal fracResult = new BigDecimal(Math.pow(base.doubleValue(), fracExp));
	    result = result.multiply(fracResult);

	    if (reciprocal) {
		result = BigDecimal.ONE.divide(result);
	    }

	    return result;
	}


	/**
	 * @return The result of the base to the exp power, done in <code>BigInteger</code>,
	 * or <code>BigDecimal</code> precision depending on the value of the exponent.
	 * @param base The number to raise to the given power.
	 * @param exp The power to raise the number to.
	 * @throws IllegalArgumentException if the exponent is infinite, or not-a-number.
	 */
	public static Number pow(final BigInteger base, final double exp) {
	    if (Double.isNaN(exp) || Double.isInfinite(exp))
		throw new Intl.IllegalArgumentException("util#numeric.outOfRange");
	    if (exp == 0.0d)
		return BigInteger.ONE;

	    // Test for negative or fractional powers and convert to BigDecimal for those cases
	    double wholeExp = Math.floor(exp);
	    if (exp < 0.0d || wholeExp != exp) {
		return pow(new BigDecimal(base), exp);
	    }

	    int intExp = (int)wholeExp;
	    return base.pow(intExp);
	}


	/**
	 * @return A BigInteger of 10**pow.
	 *
	 * @param pow	The power of 10 we need.
	 */
	public static BigInteger tenPower(final int pow) {
	    return new BigInteger(CharUtil.padToWidth("1", pow + 1, '0'));
	}

	/**
	 * Compute the factorial value for the given integer value.
	 * <p> The value for <code>n!</code> is <code>1 * 2 * 3 * 4</code>... to n
	 * and where <code>0! = 1</code> (by definition)
	 *
	 * @param base	The integer base (n).
	 * @param mc	The {@link MathContext} used to round the result (only if base is negative).
	 * @return	The value of <code>n!</code>
	 */
	public static BigDecimal factorial(final Number base, final MathContext mc) {
	    double baseDouble = base.doubleValue();
	    double baseFloor  = Math.floor(baseDouble);

	    if (baseFloor != baseDouble)
		throw new Intl.IllegalArgumentException("util#numeric.wholeInteger");

	    long loops = base.longValue();

	    if (loops == 0L || loops == 1L)
		return BigDecimal.ONE;

	    boolean negative = false;
	    if (loops < 0L) {
		negative = true;
		loops = -loops - 1L;
	    }
	    BigInteger result = BigInteger.ONE;
	    BigInteger term   = BigInteger.ONE;

	    for (long i = 2L; i <= loops; i++) {
		term   = term.add(BigInteger.ONE);
		result = result.multiply(term);
	    }

	    BigDecimal dResult = new BigDecimal(result);
	    if (negative) {
		// The so-called "Roman factorial"
		dResult = BigDecimal.ONE.divide(dResult, mc);
		return (loops % 2L == 1L) ? dResult.negate() : dResult;
	    }

	    return dResult;
	}


	/**
	 * Find the n-th Fibonacci number, where fib(0) = 0,
	 * fib(1) = 1, and fib(n) = fib(n - 1) + fib(n - 2);
	 *
	 * @param n	The desired term number (can be negative).
	 * @return	The n-th Fibonacci number.
	 */
	public static BigDecimal fib(final Number n) {
	    double nDouble = n.doubleValue();
	    double nInt    = Math.rint(nDouble);

	    if (nInt != nDouble)
		throw new Intl.IllegalArgumentException("util#numeric.wholeInteger");

	    long loops        = Math.abs(n.longValue());
	    boolean negative  = nInt < 0.0d;
	    BigInteger n_2    = BigInteger.ZERO;
	    BigInteger n_1    = BigInteger.ONE;
	    BigInteger result = BigInteger.ONE;

	    if (loops == 0L)
		result = n_2;
	    else if (loops == 1L)
		result = n_1;
	    else {
		for (long i = 2L; i <= loops; i++) {
		    result = n_2.add(n_1);
		    n_2    = n_1;
		    n_1    = result;
		}
	    }

	    if (negative) {
		if (loops % 2L == 0)
		    return new BigDecimal(result).negate();
	    }
	    return new BigDecimal(result);
	}


	/**
	 * A cache of BigFraction values for B(n), so that this expensive operation
	 * doesn't have to be done more than once per index.
	 * <p> The index is n / 2 since every other value is zero.
	 */
	private static DynamicArray<BigFraction> bernoulliCache = new DynamicArray<>(BigFraction.class, 60);

	/**
	 * From <a href="https://rosettacode.org/wiki/Bernoulli_numbers">rosettacode.org</a>
	 * the algorithm is as follows:
	 * allocate n+1 BigFractions
	 * for (m = 0 to n) {
	 *   arr[m] = BigFraction(1, (m+1))
	 *   for (n = m downto 1) {
	 *      arr[n-1] = (arr[n-1] - arr[n]) * n
	 *   }
	 * }
	 * return arr[0]
	 *
	 * @param n Which Bernoulli number to calculate.
	 * @return  The value as a fraction.
	 */
	private static BigFraction bern(int n) {
	    int num = Math.abs(n);

	    // First, check if we have already computed and cached the value
	    int cacheIndex = num >> 1;
	    BigFraction cachedResult = bernoulliCache.get(cacheIndex);
	    if (cachedResult != null) {
		logger.debug("bern(%1$d) gotten from cache", num);
		return cachedResult;
	    }

	    BigFraction[] arr = new BigFraction[num+1];
	    for (int m = 0; m <= num; m++) {
		arr[m] = new BigFraction(1, (m+1));
		for (int i = m; i >= 1; i--) {
		    arr[i-1] = (arr[i-1].subtract(arr[i])).multiply(i);
		}
	    }

	    bernoulliCache.put(cacheIndex, arr[0]);
	    return arr[0];
	}

	/**
	 * Get the value of the N-th Bernoulli number.
	 *
	 * @param n	Which Bernoulli number to get.
	 * @param mc	The {@link MathContext} to use for rounding the division
	 *		(non-rational mode).
	 * @param rational Whether to return the result as a rational number.
	 * @return	Then N-th Bernoulli number as a decimal (rounded to {@code mc}),
	 *		or as a fraction.
	 */
	public static Object bernoulli(int n, MathContext mc, boolean rational) {
	    if (n == 0)
		return rational ? BigFraction.ONE : BigDecimal.ONE;
	    if (n == 1 || n == -1) {
		if (rational)
		    return new BigFraction(n, 2);
		else
		    return BigDecimal.valueOf(n).divide(D_TWO);
	    }
	    if (n % 2 == 1)
		return rational ? BigFraction.ZERO : BigDecimal.ZERO;

	    BigFraction bn = bern(n);
	    if (rational) {
		return bn;
	    }
	    else {
		return bn.toDecimal(mc);
	    }
	}

	private static BigDecimal toDecimal(final Number x, final MathContext mc) {
	    if (x instanceof BigDecimal)
		return (BigDecimal) x;
	    else if (x instanceof BigInteger)
		return new BigDecimal((BigInteger) x);
	    else if (x instanceof BigFraction)
		return ((BigFraction) x).toDecimal(mc);
	    else
		return BigDecimal.valueOf(x.doubleValue());
	}

	/**
	 * Find the value of sin(x) (where x is in radians).
	 *
	 * @param x	The value in radians to compute the "sin" function of.
	 * @param mc	The {@link MathContext} to use for rounding during the computation.
	 * @return	The value of the sin of x.
	 */
	public static BigDecimal sin(final Number x, final MathContext mc) {
	    BigDecimal xValue = toDecimal(x, mc);

	    pi(mc.getPrecision());

	    /* First do some range reduction to the range -2*pi to 2*pi */
	    logger.debug("sin:     original x = %1$s", xValue.toPlainString());
	    if (xValue.compareTo(MINUS_TWO_PI) < 0 || xValue.compareTo(TWO_PI) > 0) {
		xValue = xValue.remainder(TWO_PI, mc);
		logger.debug("sin: range reduced x = %1$s", xValue.toPlainString());
	    }

	    BigDecimal result   = xValue;
	    BigDecimal power    = xValue;
	    BigDecimal xSquared = xValue.multiply(xValue);
	    BigDecimal fact     = BigDecimal.ONE;
	    BigDecimal factTerm = BigDecimal.ONE;

	    // This converges very rapidly, except when the value is near zero
	    int loops = mc.getPrecision() * 3 / 2;

	    MathContext mc2 = new MathContext(mc.getPrecision() * 2);

	    for (int i = 1; i < loops; i++) {
		power     = power.multiply(xSquared);
		factTerm  = factTerm.add(BigDecimal.ONE);
		fact      = fact.multiply(factTerm);
		factTerm  = factTerm.add(BigDecimal.ONE);
		fact      = fact.multiply(factTerm);
		BigDecimal seriesTerm = power.divide(fact, mc2);
		if (i % 2 == 1)
		    result = result.subtract(seriesTerm);
		else
		    result = result.add(seriesTerm);
		logger.debug("sin: loop %1$d -> %2$s", i, result.toPlainString());
	    }

	    return result.round(mc);
	}

	/**
	 * Find the value of cos(x) (where x is in radians).
	 *
	 * @param x	The value in radians to compute the "cos" function of.
	 * @param mc	The {@link MathContext} to use for the computation.
	 * @return	The value of the cos of x.
	 */
	public static BigDecimal cos(final Number x, final MathContext mc) {
	    BigDecimal xValue = toDecimal(x, mc);

	    pi(mc.getPrecision());

	    /* First do some range reduction to the range -2*pi to 2*pi */
	    logger.debug("cos:     original x = %1$s", xValue.toPlainString());
	    if (xValue.compareTo(MINUS_TWO_PI) < 0 || xValue.compareTo(TWO_PI) > 0) {
		xValue = xValue.remainder(TWO_PI, mc);
		logger.debug("cos: range reduced x = %1$s", xValue.toPlainString());
	    }

	    BigDecimal xSquared = xValue.multiply(xValue);
	    BigDecimal result   = BigDecimal.ONE;
	    BigDecimal power    = BigDecimal.ONE;
	    BigDecimal fact     = BigDecimal.ONE;
	    BigDecimal factTerm = BigDecimal.ONE;

	    // This converges very rapidly, except when the value is near zero
	    int loops = mc.getPrecision() * 3 / 2;

	    MathContext mc2 = new MathContext(mc.getPrecision() * 2);

	    for (int i = 1; i < loops; i++) {
		power     = power.multiply(xSquared);
		factTerm  = factTerm.add(BigDecimal.ONE);
		fact      = fact.multiply(factTerm);

		BigDecimal seriesTerm = power.divide(fact, mc2);

		if (i % 2 == 1)
		    result = result.subtract(seriesTerm);
		else
		    result = result.add(seriesTerm);

		logger.debug("cos: loop %1$d -> %2$s", i, result.toPlainString());
		factTerm  = factTerm.add(BigDecimal.ONE);
		fact      = fact.multiply(factTerm);
	    }

	    return result.round(mc);
	}

	/**
	 * Find the value of tan(x) (where x is in radians).
	 *
	 * @param x	The value in radians to compute the "tan" function of.
	 * @param mc	The {@link MathContext} to use for the computation.
	 * @return	The value of the tan of x.
	 */
	public static BigDecimal tan(final Number x, final MathContext mc) {
	    // Rounding context for the loops, to ensure we get accuracy to the requested precision
	    MathContext mc2 = new MathContext(mc.getPrecision() + 2);

	    BigDecimal xValue = toDecimal(x, mc2);

	    pi(mc2.getPrecision());

	    /* First, do some range reductions to the range of -pi/2 to pi/2 */
	    if (xValue.compareTo(MINUS_PI_OVER_TWO) < 0 || xValue.compareTo(PI_OVER_TWO) > 0) {
		xValue = xValue.remainder(PI_OVER_TWO, mc);
	    }

	    // Some simplifications
	    if (xValue.equals(BigDecimal.ZERO))
		return BigDecimal.ZERO;

	    BigDecimal result     = xValue;
	    BigInteger FOUR       = BigInteger.valueOf(4L);
	    BigInteger twoPower   = FOUR;
	    BigDecimal xPower     = xValue;
	    BigDecimal xSquared   = xValue.multiply(xValue);
	    BigInteger factTerm   = I_TWO;
	    BigInteger factorial  = I_TWO; // 2!
	    BigDecimal lastResult = result;
	    BigInteger numer;

	    // This seems to require (precision/4) * input/0.1 iterations, so for
	    // precision 20, about 5 * 0.1, for 34 about 8 * 0.1, etc.
	    int approxRange = (int)Math.floor(xValue.divide(new BigDecimal("0.1"), mc).doubleValue()) + 1;
	    int loopCountPerRange = (mc.getPrecision() + 3) / 4;
	    int loops = (approxRange + approxRange / 6) * loopCountPerRange + 3;
	    logger.debug("tan: precision = %1$d, approx range = %2$d, loops per = %3$d -> loops = %4$d", mc.getPrecision(), approxRange, loopCountPerRange, loops);
	    // Big decision here:  at some point at around 1.2 (where approxRange / 6 is > 1) we start getting
	    // diminishing returns, so switch to using sin(x) / cos(x) as quicker AND more accurate
	    if (approxRange > 12) {
		return sin(xValue, mc).divide(cos(xValue, mc), mc);
	    }

	    for (int i = 2; i < loops; i++) {
		twoPower  = twoPower.multiply(FOUR);
		xPower    = xPower.multiply(xSquared);
		numer     = twoPower.multiply(twoPower.subtract(BigInteger.ONE));
		factTerm  = factTerm.add(BigInteger.ONE);
		factorial = factorial.multiply(factTerm);
		factTerm  = factTerm.add(BigInteger.ONE);
		factorial = factorial.multiply(factTerm);

		BigFraction t  = new BigFraction(numer, factorial);
		// There is supposed to be a (-1)**(n-1) term, but it is exactly
		// balanced by the oscillating sign of Bn, so just do abs() here
		// and ignore the -1 term
		BigFraction bn = bern(i * 2).abs();
		BigFraction termn = t.multiply(bn);
		logger.debug("tan: i = %1$d, bn(i*2) = %2$s, t (num/fact) = %3$s, t*bn = %4$s", i, bn, t, termn);
		BigDecimal term = termn.toDecimal(mc2).multiply(xPower, mc2);
		result          = result.add(term, mc2);
		logger.debug("tan: term = %1$s, new result = %2$s, lastResult = %3$s", term.toPlainString(), result.toPlainString(), lastResult.toPlainString());
		if (lastResult.equals(result)) {
		    break;
		}
		lastResult = result;
	    }

	    return result.round(mc);
	}

	/**
	 * Find the positive square root of a number (non-negative).
	 *
	 * @param x	The value to find the square root of.
	 * @param mc	The {@code MathContext} to use for rounding / calculating the result.
	 * @return	The {@code sqrt(x)} value such that {@code x = result * result}.
	 */
	public static BigDecimal sqrt(final BigDecimal x, final MathContext mc) {
	    if (x.signum() < 0)
		throw new Intl.IllegalArgumentException("util#numeric.sqrtNegative");
	    if (x.equals(BigDecimal.ZERO) || x.equals(BigDecimal.ONE))
		return x;

	    BigDecimal trial_root = BigDecimal.ONE.movePointRight((x.precision() - x.scale()) / 2);
	    BigDecimal result = trial_root;
	    BigDecimal lastResult = result;

	    logger.debug("sqrt: trial_root = %1$s", trial_root.toPlainString());
	    // 50 is entirely arbitrary; normally the results converge in 5-10 iterations
	    // up to 100s of digits
	    for (int i = 0; i < 50; i++) {
		result = result.add(x.divide(result, mc)).divide(D_TWO, mc);
		logger.debug("sqrt: result = %1$s, lastResult = %2$s", result.toPlainString(), lastResult.toPlainString());
		if (result.equals(lastResult)) {
		    logger.debug("sqrt: break out early, i = %1$d", i);
		    break;
		}
		lastResult = result;
	    }
	    logger.debug("sqrt: result = %1$s", result.toPlainString());
	    return result.round(mc);
	}

	/**
	 * Find the cube root of a number (either positive or negative).
	 *
	 * @param x	The value to find the cube root of.
	 * @param mc	The {@code MathContext} to use for rounding / calculating the result.
	 * @return	The {@code cbrt(x)} value such that {@code x = result * result * result}.
	 */
	public static BigDecimal cbrt(final BigDecimal x, final MathContext mc) {
	    int sign = x.signum();
	    BigDecimal xValue = x.abs();

	    // Taken from https://stackoverflow.com/questions/7463486/seeding-the-newton-iteration-for-cube-root-efficiently
	    // BigDecimal trial_root = new BigDecimal("1.4774329094")
	    //    .subtract(new BigDecimal("0.8414323527").divide(x.add(new BigDecimal("0.7387320679")), mc));
	    BigDecimal trial_root = BigDecimal.ONE.movePointRight((x.precision() - x.scale()) / 3);
	    logger.debug("cbrt: trial_root = %1$s", trial_root.toPlainString());
	    BigDecimal result = trial_root;
	    BigDecimal lastResult = result;

	    for (int i = 0; i < 50; i++) {
		BigDecimal x2 = result.multiply(result);
		BigDecimal x3 = x2.multiply(result);
		BigDecimal numer = xValue.subtract(x3);
		BigDecimal denom = x2.multiply(BigDecimal.valueOf(3L));
		result = result.add(numer.divide(denom, mc), mc);
		logger.debug("cbrt: result = %1$s, lastResult = %2$s", result.toPlainString(), lastResult.toPlainString());
		if (result.equals(lastResult)) {
		    logger.debug("cbrt: break out early, i = %1$d", i);
		    break;
		}
		lastResult = result;
	    }
	    return (sign < 0) ? result.negate(mc) : result.plus(mc);
	}


        private static final int SCALE = 10000;
        private static final int ARRINIT = 2000;

	/**
	 * Taken from <a href="http://www.codecodex.com/wiki/index.php?title=Digits_of_pi_calculation#Java">
	 * http://www.codecodex.com/wiki/index.php?title=Digits_of_pi_calculation#Java</a>
	 *
	 * @param digits - returns good results up to 12500 digits
	 * @return that many digits of PI
	 * @throws IllegalArgumentException if the requested number of digits is &gt; 12,500
	 * @throws IllegalStateException if we don't get the right number of digits from
	 * the calculation.
	 */
	public static String piDigits(final int digits) {
	    // According to the original documentation, the given SCALE and ARRINIT
	    // values work up to approx. 12,500 digits, so error out if we're over that
	    if (digits > 12_500)
		throw new Intl.IllegalArgumentException("util#numeric.tooManyPiDigits");

	    // Since each loop reduces the count by 14 while only providing 4 digits
	    // of output, in order to produce the required number of digits we must
	    // scale up the loop count proportionally. Add one more loop to make sure
	    // we have enough.
	    int loops = (digits + 1) * 14 / 4;

	    StringBuffer pi = new StringBuffer(loops);
	    int[] arr = new int[loops + 1];
	    int carry = 0;

	    for (int i = 0; i <= loops; ++i)
		arr[i] = ARRINIT;

	    for (int i = loops; i > 0; i-= 14) {
		int sum = 0;
		for (int j = i; j > 0; --j) {
		    sum = sum * j + SCALE * arr[j];
		    arr[j] = sum % (j * 2 - 1);
		    sum /= j * 2 - 1;
		}

		pi.append(String.format("%04d", carry + sum / SCALE));
		carry = sum % SCALE;
	    }

	    // We calculated a few more digits than we need (hopefully), so truncate
	    // the result to the exact digit count requested. Exception thrown if we
	    // calculated wrong.
	    if (pi.length() < digits)
		throw new Intl.IllegalStateException("util#numeric.piDigitMismatch",
			pi.length(), digits);
	    else if (pi.length() > digits)
		pi.setLength(digits);

	    return pi.toString();
	}


	/**
	 * Like pi, e is a real number with an infinite number of non-repeating digits.  We can
	 * approximate e with the following formula:  e = 1/0! + 1/1! + 1/2! + 1/3! + 1/4! + ...
	 *
	 * @param digits The number of digits to compute.
	 * @return       The decimal value of e to the given number of digits.
	 */
	public static BigDecimal eDecimal(final int digits) {
	    // e will accumulate the sum of 1/i! for an ever increasing i
	    BigDecimal e         = D_TWO;
	    BigDecimal factorial = BigDecimal.ONE;
	    // loops is a little extra to make sure the last digit we want is accurate
	    int loops = digits + 10;
	    MathContext roundingContext = new MathContext(digits + 1, RoundingMode.DOWN);

	    for (int i = 2; i < loops; i++) {
		factorial = factorial.multiply(BigDecimal.valueOf(i));
		// compute 1/i!, note divide is overloaded, this version is used to
		//    ensure a limit to the iterations when division is limitless like 1/3
		BigDecimal term = BigDecimal.ONE.divide(factorial, loops, RoundingMode.HALF_UP);
		e = e.add(term);
	    }
	    return e.round(roundingContext);
	}


	/**
	 * @return A {@link BigDecimal} constant of PI to the requested number of fractional digits
	 * (up to around 12,500).
	 *
	 * @param digits The number of digits desired after the decimal point.
	 * @see #piDigits
	 * @throws IllegalArgumentException if the number of digits is more than we can handle.
	 */
	public static BigDecimal pi(final int digits) {
	    // Use +1 for precision because of the "3." integer portion
	    MathContext mc = new MathContext(digits + 1, RoundingMode.DOWN);

	    // For very small values, use the rational approximation
	    if (digits < 25) {
		return PI_APPROX.toDecimal(mc);
	    }

	    // Use the previously calculated value if possible
	    if (CALCULATED_PI == null || CALCULATED_PI.scale() != digits) {
		// Calculate a new value with the requested scale
		// (+1 because of the leading "3" digit)
		if (PI_DIGITS == null || PI_DIGITS.length() <= digits) {
		    PI_DIGITS = piDigits(digits + 1);
		}
		CALCULATED_PI = new BigDecimal(PI_DIGITS.substring(0, digits + 1)).movePointLeft(digits);

		// Now calculate the related values at the same scale
		TWO_PI            = CALCULATED_PI.multiply(D_TWO, mc);
		MINUS_TWO_PI      = TWO_PI.negate();
		PI_OVER_TWO       = CALCULATED_PI.divide(D_TWO, mc);
		MINUS_PI_OVER_TWO = PI_OVER_TWO.negate();
	    }

	    return CALCULATED_PI;
	}


	/** The previously calculated E value (if any); cached to eliminate repeated costly calculations. */
	private static BigDecimal CALCULATED_E = null;
	/** The largest number of digits of E calculated so far: use a substring for lesser precision. */
	private static String E_DIGITS = null;

	/**
	 * @return A {@link BigDecimal} constant of E to the requested number of fractional digits.
	 *
	 * @param digits The number of digits desired after the decimal point.
	 */
	public static BigDecimal e(final int digits) {
	    // Use the previously calculated value if possible
	    if (CALCULATED_E == null || CALCULATED_E.scale() != digits) {
		if (E_DIGITS == null || E_DIGITS.length() < digits + 2) {
		    CALCULATED_E = eDecimal(digits);
		    E_DIGITS = CALCULATED_E.toPlainString();
		}
		else {
		    CALCULATED_E = new BigDecimal(E_DIGITS.substring(0, digits + 2));
		}
	    }

	    return CALCULATED_E;
	}

	private static final BigInteger MAX_PRIME = BigInteger.valueOf(Integer.MAX_VALUE);

	private static int findLowestClearBit(final BigInteger bitArray, final int start, final int length) {
	    for (int i = start; i < length; i++) {
		if (!bitArray.testBit(i))
		    return i;
	    }
	    return -1;
	}

	/**
	 * Using a Sieve of Eratosthenes, figure out if the given number is prime.
	 * <p> Because this uses a bunch of space, the calculation is limited to
	 * a relatively small value (~10**7).
	 *
	 * @param n The number to check for possible prime-ness.
	 * @return  {@code true} if {@code n} is a prime number, {@code false}
	 *          if the number is composite.
	 * @throws  IllegalArgumentException if the number is "too big" for this method.
	 */
	public static boolean isPrime(final BigInteger n) {
	    if (n.compareTo(MAX_PRIME) > 0)
		throw new Intl.IllegalArgumentException("util#numeric.primeTooBig");

	    // Negative numbers are essentially the same primality as their positive counterparts
	    BigInteger posN = n.abs();

	    // Easy decision here, even numbers > 2 are not prime...
	    if (posN.equals(BigInteger.ZERO))
		return false;

	    if (posN.compareTo(I_TWO) <= 0)
		return true;

	    if (posN.remainder(I_TWO).equals(BigInteger.ZERO))
		return false;

	    int max = (int)(Math.ceil(Math.sqrt(posN.doubleValue())) + 0.5d) + 1;

	    // In this implementation, a 0 bit means prime, 1 bit is composite.
	    BigInteger sieve = BigInteger.ZERO;

	    // In this implementation, only the odd bits are present, and correspond so:
	    // bit 0 -> 3
	    // bit 1 -> 5
	    // bit 2 -> 7
	    int i = 0;
	    while (i >= 0 && i <= max) {
		int prime = (i * 2) + 3;
		if (posN.equals(BigInteger.valueOf(prime)))
		    return true;

		BigInteger rem = posN.remainder(BigInteger.valueOf(prime));
		if (rem.equals(BigInteger.ZERO))
		    return false;

		for (int j = prime; j <= max; j += prime) {
		    if (j % 2 == 1) {
			sieve = sieve.setBit((j - 3) / 2);
		    }
		}

		int next = findLowestClearBit(sieve, i, max);
		if (i == next) {
		    break;
		}
		i = next;
	    }
	    return true;
	}

}
