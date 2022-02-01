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
 *	    Created from constants already defined in other places.
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
public final class MathConstants
{
	/**
	 * Private constructor since this is a static class which should never be instantiated.
	 */
	private MathConstants() {
	}

	public static final BigInteger I_TWO = BigInteger.valueOf(2);
	public static final BigInteger I_THREE = BigInteger.valueOf(3);
	public static final BigInteger I_FOUR  = BigInteger.valueOf(4);
	public static final BigInteger I_SEVEN = BigInteger.valueOf(7);

	public static final BigInteger MIN_BYTE = BigInteger.valueOf(Byte.MIN_VALUE);
	public static final BigInteger MAX_BYTE = BigInteger.valueOf(Byte.MAX_VALUE);
	public static final BigInteger MIN_SHORT = BigInteger.valueOf(Short.MIN_VALUE);
	public static final BigInteger MAX_SHORT = BigInteger.valueOf(Short.MAX_VALUE);
	public static final BigInteger MIN_INT = BigInteger.valueOf(Integer.MIN_VALUE);
	public static final BigInteger MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);
	public static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
	public static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);

	public static final BigDecimal D_MINUS_ONE = BigDecimal.valueOf(-1);
	public static final BigDecimal D_TWO = BigDecimal.valueOf(2);
	public static final BigDecimal D_FIVE = BigDecimal.valueOf(5);
	public static final BigDecimal D_TEN = BigDecimal.valueOf(10);

	public static final ComplexNumber C_MINUS_I = new ComplexNumber(0, -1);

	public static final long NANOSECONDS = 1_000_000_000L;
	public static final long ONE_MINUTE = 60L * NANOSECONDS;
	public static final long ONE_HOUR = 60L * ONE_MINUTE;
	public static final long TWELVE_HOURS = 12L * ONE_HOUR;
	public static final long ONE_DAY = TWELVE_HOURS * 2L;
	public static final long ONE_WEEK = 7L * ONE_DAY;

	public static final Charset UTF_8_CHARSET = StandardCharsets.UTF_8;
	public static final Charset DEFAULT_CHARSET = Charset.defaultCharset();
	public static final Charset ISO_8859_1_CHARSET = Charset.forName("ISO-8859-1");
	public static final Charset WIN_1252_CHARSET   = Charset.forName("windows-1252");

}
