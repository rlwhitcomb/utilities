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
 *	A collection of math-related constant values that are used in multiple places.
 *
 *  History:
 *	01-Feb-2022 (rlwhitcomb)
 *	    #231: Created from constants already defined in other places.
 */
package info.rlwhitcomb.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import info.rlwhitcomb.util.ComplexNumber;


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

	/** A {@link BigInteger} value of two. */
	public static final BigInteger I_TWO = BigInteger.valueOf(2);

	/** A {@link BigInteger} value of three. */
	public static final BigInteger I_THREE = BigInteger.valueOf(3);

	/** A {@link BigInteger} value of four. */
	public static final BigInteger I_FOUR  = BigInteger.valueOf(4);

	/** A {@link BigInteger} value of seven. */
	public static final BigInteger I_SEVEN = BigInteger.valueOf(7);


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


	/** A {@link BigDecimal} value of <code>-1</code>. */
	public static final BigDecimal D_MINUS_ONE = BigDecimal.valueOf(-1);

	/** A {@link BigDecimal} value of <code>2</code>. */
	public static final BigDecimal D_TWO = BigDecimal.valueOf(2);

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


	/** A complex number corresponding to {@code i}. */
	public static final ComplexNumber C_I = new ComplexNumber(0, 1);

	/** A complex number corresponding to {@code -i}. */
	public static final ComplexNumber C_MINUS_I = new ComplexNumber(0, -1);

	/** A complex number corresponding to {@code 0}. */
	public static final ComplexNumber C_ZERO = new ComplexNumber(0, 0);

	/** A complex number corresponding to a real value of {@code 1}. */
	public static final ComplexNumber C_ONE = new ComplexNumber(1, 0);


	/** Number of nanoseconds in a second. */
	public static final long NANOSECONDS = 1_000_000_000L;

	/** Nanoseconds in one minutes. */
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

	/** The ISO-8859-l (Latin 1) character set. */
	public static final Charset ISO_8859_1_CHARSET = Charset.forName("ISO-8859-1");

	/** The normal Windows character set (windows-1252). */
	public static final Charset WIN_1252_CHARSET   = Charset.forName("windows-1252");

}