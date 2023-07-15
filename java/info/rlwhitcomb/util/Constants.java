/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2023 Roger L. Whitcomb.
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
 *	A collection of math-related constant values that are used in multiple places.
 *
 * History:
 *  01-Feb-22 rlw #231	Created from constants already defined in other places.
 *  02-Feb-22 rlw #231	More values.
 *  08-Feb-22 rlw #235	D_FOUR added.
 *  16-Feb-22 rlw ----	Buffer sizes added.
 *  17-Feb-22 rlw ----	Double and float limits.
 *  14-Apr-22 rlw #273	Move math-related classes to "math" package.
 *  29-May-22 rlw #301	Add values used for "convertToWords" (BigInteger).
 *  29-Jun-22 rlw #380	Increase string file size for FileUtilities.
 *  09-Jul-22 rlw #393	Cleanup imports.
 *  24-Aug-22 rlw #447	Add "D_200" value.
 *                ----	Move I_MINUS_ONE into here from Calc.
 *  12-Sep-22 rlw #480	Add "I_800" for range conversions.
 *  19-Dec-22 rlw #559	New rational complex values.
 *  04-Jan-23 rlw #558	Quaternion constants.
 *  10-Jan-23 rlw #558	More quaternion constants.
 *  05-May-23 rlw ----	Remove actually unused values.
 */
package info.rlwhitcomb.util;

import info.rlwhitcomb.math.BigFraction;
import info.rlwhitcomb.math.ComplexNumber;
import info.rlwhitcomb.math.Quaternion;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


/**
 * A collection of static final constants pulled together from various other places to
 * reduce code duplication and provide one source of the truth.
 */
public final class Constants
{
	/**
	 * Private constructor since this is a static class which should never be instantiated.
	 */
	private Constants() {
	}


	/** A {@link BigInteger} value of <code>-1</code>. */
	public static final BigInteger I_MINUS_ONE = BigInteger.valueOf(-1);

	/** A {@link BigInteger} value of two. */
	public static final BigInteger I_TWO = BigInteger.valueOf(2);

	/** A {@link BigInteger} value of three. */
	public static final BigInteger I_THREE = BigInteger.valueOf(3);

	/** A {@link BigInteger} value of four. */
	public static final BigInteger I_FOUR  = BigInteger.valueOf(4);

	/** A {@link BigInteger} value of ten. */
	public static final BigInteger I_TEN = BigInteger.valueOf(10);

	/** A {@link BigInteger} value of twenty. */
	public static final BigInteger I_TWENTY = BigInteger.valueOf(20);

	/** A {@link BigInteger} value of one hundred. */
	public static final BigInteger I_HUNDRED = BigInteger.valueOf(100);

	/** A {@link BigInteger} value of 800. */
	public static final BigInteger I_800 = BigInteger.valueOf(800);

	/** A {@link BigInteger} value of one thousand. */
	public static final BigInteger I_THOUSAND = BigInteger.valueOf(1_000L);

	/** A {@link BigInteger} value of one million. */
	public static final BigInteger I_MILLION = BigInteger.valueOf(1_000_000L);


	/** A {@link BigInteger} value of the smallest byte value. */
	public static final BigInteger MIN_BYTE = BigInteger.valueOf(Byte.MIN_VALUE);

	/** A {@link BigInteger} value of the largest byte value. */
	public static final BigInteger MAX_BYTE = BigInteger.valueOf(Byte.MAX_VALUE);

	/** A {@link BigInteger} value of the smallest short value. */
	public static final BigInteger MIN_SHORT = BigInteger.valueOf(Short.MIN_VALUE);

	/** A {@link BigInteger} value of the largest short value. */
	public static final BigInteger MAX_SHORT = BigInteger.valueOf(Short.MAX_VALUE);

	/** A {@link BigInteger} value of the smallest integer value. */
	public static final BigInteger MIN_INT = BigInteger.valueOf(Integer.MIN_VALUE);

	/** A {@link BigInteger} value of the largest integer value. */
	public static final BigInteger MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);

	/** A {@link BigInteger} value of the smallest long value. */
	public static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);

	/** A {@link BigInteger} value of the largest long value. */
	public static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);

	/** A {@link BigDecimal} value of the largest float value. */
	public static final BigDecimal MAX_FLOAT = BigDecimal.valueOf(Float.MAX_VALUE);

	/** A {@link BigDecimal} value of the largest double value. */
	public static final BigDecimal MAX_DOUBLE = BigDecimal.valueOf(Double.MAX_VALUE);


	/** A {@link BigDecimal} value of <code>-1</code>. */
	public static final BigDecimal D_MINUS_ONE = BigDecimal.valueOf(-1);

	/** A {@link BigDecimal} value of <code>0.1</code>. */
	public static final BigDecimal D_ONE_TENTH = new BigDecimal("0.1");

	/** A {@link BigDecimal} value of <code>0.25</code>. */
	public static final BigDecimal D_ONE_FOURTH = new BigDecimal("0.25");

	/** A {@link BigDecimal} value of <code>0.5</code>. */
	public static final BigDecimal D_ONE_HALF = new BigDecimal("0.5");

	/** A {@link BigDecimal} value of <code>2</code>. */
	public static final BigDecimal D_TWO = BigDecimal.valueOf(2);

	/** A {@link BigDecimal} value of <code>3</code>. */
	public static final BigDecimal D_THREE = BigDecimal.valueOf(3);

	/** A {@link BigDecimal} value of <code>4</code>. */
	public static final BigDecimal D_FOUR = BigDecimal.valueOf(4);

	/** A {@link BigDecimal} value of <code>5</code>. */
	public static final BigDecimal D_FIVE = BigDecimal.valueOf(5);

	/** A {@link BigDecimal} value of <code>7</code>. */
	public static final BigDecimal D_SEVEN = BigDecimal.valueOf(7);

	/** A {@link BigDecimal} value of <code>10</code>. */
	public static final BigDecimal D_TEN = BigDecimal.valueOf(10);

	/** A {@link BigDecimal} value of <code>24</code>. */
	public static final BigDecimal D_24 = BigDecimal.valueOf(24);

	/** A {@link BigDecimal} value of <code>60</code>. */
	public static final BigDecimal D_60 = BigDecimal.valueOf(60);

	/** A {@link BigDecimal} value of <code>180</code>. */
	public static final BigDecimal D_180 = BigDecimal.valueOf(180);

	/** A {@link BigDecimal} value of <code>200</code>. */
	public static final BigDecimal D_200 = BigDecimal.valueOf(200);


	/** A complex number corresponding to {@code i}. */
	public static final ComplexNumber C_I = new ComplexNumber(0, 1);

	/** A complex number corresponding to {@code 0}. */
	public static final ComplexNumber C_ZERO = new ComplexNumber(0, 0);

	/** A complex number corresponding to a real value of {@code 1}. */
	public static final ComplexNumber C_ONE = new ComplexNumber(1, 0);

	/** A rational complex number corresponding to a rational value of {@code 1/1}. */
	public static final ComplexNumber CR_ONE = ComplexNumber.real(BigFraction.ONE);


	/** A quaternion corresponding to a rational value of {@code (1, 0, 0, 0)}. */
	public static final Quaternion QR_ONE = new Quaternion(BigFraction.ONE);

	/** Quaternion with a value of "j" ({@code (0, 0, 1, 0)}). */
	public static final Quaternion Q_J = new Quaternion(0, 0, 1, 0);

	/** Quaternion with a value of "k" ({@code (0, 0, 0, 1)}). */
	public static final Quaternion Q_K = new Quaternion(0, 0, 0, 1);


	/** Number of nanoseconds in a second. */
	public static final long NANOSECONDS = 1_000_000_000L;

	/** Nanoseconds in one minute. */
	public static final long ONE_MINUTE = 60L * NANOSECONDS;

	/** Nanoseconds in one hour. */
	public static final long ONE_HOUR = 60L * ONE_MINUTE;

	/** Nanoseconds in twelve hours (for am/pm shifting). */
	public static final long TWELVE_HOURS = 12L * ONE_HOUR;

	/** Nanoseconds in one full day (24 hours). */
	public static final long ONE_DAY = TWELVE_HOURS * 2L;

	/** Nanoseconds in one (long) week (7 days). */
	public static final long ONE_WEEK = 7L * ONE_DAY;


	/** The system default character set. */
	public static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

	/** The standard UTF-8 character set. */
	public static final Charset UTF_8_CHARSET = StandardCharsets.UTF_8;

	/** The ISO-8859-1 (Latin 1) character set. */
	public static final Charset ISO_8859_1_CHARSET = Charset.forName("ISO-8859-1");

	/** The normal Windows character set (windows-1252). */
	public static final Charset WIN_1252_CHARSET   = Charset.forName("windows-1252");


	/** Default buffer size for reading local files. */
	public static final int FILE_BUFFER_SIZE = 65_536;

	/** Default buffer size for tailing process output. */
	public static final int PROCESS_BUFFER_SIZE = 8_192;

	/** Default buffer size for byte-to-string operations. */
	public static final int CHAR_BUFFER_SIZE = 4_096;


	/** Maximum size of file we want to read into a string (10MB). */
	public static final long FILE_STRING_SIZE_LIMIT = 10_485_760L;

}
