/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Roger L. Whitcomb.
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
 *  History:
 *      29-Jan-2021 (rlwhitcomb)
 *          Initial coding, not complete.
 *	29-Jan-2021 (rlwhitcomb)
 *	    Continued coding and documenting.
 *	29-Jan-2021 (rlwhitcomb)
 *	    Add a constructor from two integer strings.
 *	30-Jan-2021 (rlwhitcomb)
 *	    Add "compareTo" and "hashCode" methods; implement Comparable and
 *	    Serializable. Add methods to construct from strings. Add
 *	    BigDecimal constructor. Add ZERO and ONE constants.
 *	30-Jan-2021 (rlwhitcomb)
 *	    Normalize to keep sign always in the numerator. Add "abs",
 *	    "signum", and "equals"  methods.
 *	31-Jan-2021 (rlwhitcomb)
 *	    More methods dealing with whole number operands.
 *	    Divide by long was also missing.
 *	01-Feb-2021 (rlwhitcomb)
 *	    Tweaks to "normalize".
 *	01-Feb-2021 (rlwhitcomb)
 *	    Make this a subclass of Number; implement the required methods.
 *	02-Feb-2021 (rlwhitcomb)
 *	    Implement GCD and LCM.
 *	    Add "isWholeNumber", and "toInteger". Rework "longValue()" and
 *	    "intValue()" in terms of "toInteger" now. Add "intValueExact()"
 *	    and "longValueExact()" as well. Add "increment()" and "decrement()",
 *	    as well as two more constant values.
 *	03-Feb-2021 (rlwhitcomb)
 *	    General cleanup.
 *	    Tweak the two patterns and their doc.
 *	    More tweaking.
 *	09-Mar-2021 (rlwhitcomb)
 *	    The two and three string patterns are still not right...
 *	26-Mar-2021 (rlwhitcomb)
 *	    Move some methods from NumericUtil to MathUtil.
 *	29-Mar-2021 (rlwhitcomb)
 *	    Add a flag to signal this fraction should always be represented
 *	    as a proper fraction (helps with @F formatting in Calc).
 *	27-Jul-2021 (rlwhitcomb)
 *	    Add "pow" function.
 *	27-Jul-2021 (rlwhitcomb)
 *	    Negative powers and special cases for 0 and 1. Add "reciprocal".
 *	28-Jul-2021 (rlwhitcomb)
 *	    Beef up the "valueOf" with treatment of the Unicode fraction chars.
 *	06-Aug-2021 (rlwhitcomb)
 *	    And ... finish that work.
 *	25-Aug-2021 (rlwhitcomb)
 *	    Correct a typo in the INT_FRAC regex.
 */
package info.rlwhitcomb.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


/**
 * A class used to implement a rational number system, keeping exact accuracy in
 * {@link BigInteger} numerator and denominator.
 * <p> Fractions are maintained in least common denominator form, with the sign
 * of the fraction kept in the numerator only.
 */
public class BigFraction extends Number
	implements Comparable<BigFraction>, Serializable
{
	private static final long serialVersionUID = 3889374235914093689L;

	/** The pattern used for two ints, as in "numer [&nbsp;,/;] denom". */
	private static final Pattern TWO_INTS = Pattern.compile("(-?[0-9]+)(\\s+|\\s*[,/;]\\s*)(-?[0-9]+)");
	/** The pattern for three ints, as in "int [&nbsp;,/;] numer [&nbsp;,/;] denom". */
	private static final Pattern THREE_INTS = Pattern.compile("(-?[0-9]+)(\\s+|\\s*[,/;]\\s*)(-?[0-9]+)(\\s+|\\s*[,/;]\\s*)(-?[0-9]+)");
	/** The pattern for an int and a fraction character, as in "int [&nbsp;,/;] frac". */
	private static final Pattern INT_FRAC = Pattern.compile("(-?[0-9]+)(\\s*[,/;]?\\s*)(-?[\u00BC-\u00BE\u2150-\u215E\u2189])");
	/** The pattern for a single fraction character. */
	private static final Pattern FRAC_ONLY = Pattern.compile("-?[\u00BC-\u00BE\u2150-\u215E\u2189]");

	/** A value of {@code 0/1} (integer 0) as a fraction. */
	public static final BigFraction ZERO = new BigFraction(BigInteger.ZERO);

	/** A value of {@code 1/1} (integer 1) as a fraction. */
	public static final BigFraction ONE = new BigFraction();

	/** A value of {@code -1/1} (integer negative 1) as a fraction. */
	public static final BigFraction MINUS_ONE = new BigFraction(-1);

	/** A value of {@code 2/1} (integer 2) as a fraction. */
	public static final BigFraction TWO = new BigFraction(2);

	/** A value of {@code 1/2} (one-half) as a fraction. */
	public static final BigFraction ONE_HALF = new BigFraction(1, 2);

	/** Conversion table from Unicode fraction characters to real fractions. */
	private static final int[][] FRACTIONS = {
	    {  1,  4 },
	    {  1,  2 },
	    {  3,  4 },
	    {  1,  7 },
	    {  1,  9 },
	    {  1, 10 },
	    {  0,  3 },
	    {  1,  3 },
	    {  2,  3 },
	    {  1,  5 },
	    {  2,  5 },
	    {  3,  5 },
	    {  4,  5 },
	    {  1,  6 },
	    {  5,  6 },
	    {  1,  8 },
	    {  3,  8 },
	    {  5,  8 },
	    {  7,  8 }
	};

	/** The exact integer numerator of this fraction (could be negative). */
	private BigInteger numer;
	/** The exact integer denominator of this fraction (always positive). */
	private BigInteger denom;
	/**
	 * Flag to say this fraction is supposed to always be "proper"
	 * (affects {@link #toString} method).
	 */
	private boolean alwaysProper = false;


	/**
	 * Convert a string of <code>- <i>fraction</i></code> to a real fraction value.
	 *
	 * @param value A string consisting of an optional minus sign and a single
	 *              Unicode fraction character (hopefully I found them all!)
	 * @return The real fraction value.
	 * @throws IllegalArgumentException if the character/string is not recognized.
	 */
	private static BigFraction fractionValue(final String value) {
	    String frac = value;
	    boolean negative = !frac.isEmpty() && frac.charAt(0) == '-';
	    int index = 0;

	    if (negative)
		frac = value.substring(1);

	    switch (frac) {
		case "\u00BC": index = 0;  break; /* 1/4  */
		case "\u00BD": index = 1;  break; /* 1/2  */
		case "\u00BE": index = 2;  break; /* 3/4  */
		case "\u2150": index = 3;  break; /* 1/7  */
		case "\u2151": index = 4;  break; /* 1/9  */
		case "\u2152": index = 5;  break; /* 1/10 */
		case "\u2189": index = 6;  break; /* 0/3  */
		case "\u2153": index = 7;  break; /* 1/3  */
		case "\u2154": index = 8;  break; /* 2/3  */
		case "\u2155": index = 9;  break; /* 1/5  */
		case "\u2156": index = 10; break; /* 2/5  */
		case "\u2157": index = 11; break; /* 3/5  */
		case "\u2158": index = 12; break; /* 4/5  */
		case "\u2159": index = 13; break; /* 1/6  */
		case "\u215A": index = 14; break; /* 5/6  */
		case "\u215B": index = 15; break; /* 1/8  */
		case "\u215C": index = 16; break; /* 3/8  */
		case "\u215D": index = 17; break; /* 5/8  */
		case "\u215E": index = 18; break; /* 7/8  */
		default:
		    throw new Intl.IllegalArgumentException("util#fraction.unknownFracChar", frac);
	    }

	    if (negative)
		return new BigFraction(-FRACTIONS[index][0], FRACTIONS[index][1]);
	    else
		return new BigFraction(FRACTIONS[index][0], FRACTIONS[index][1]);
	}


	/**
	 * Default constructor -- a value of one.
	 */
	public BigFraction() {
	    this(BigInteger.ONE, BigInteger.ONE);
	}

	/**
	 * Construct a whole number fraction ({@code n / 1}) from the given
	 * value.
	 *
	 * @param n The whole number numerator.
	 */
	public BigFraction(final long n) {
	    this(n, 1L);
	}

	/**
	 * Construct a whole number fraction ({@code n / 1}) from the given
	 * value.
	 *
	 * @param n The whole number numerator.
	 */
	public BigFraction(final BigInteger n) {
	    this(n, BigInteger.ONE);
	}

	/**
	 * Construct a fraction from the given numerator and denominator,
	 * normalized to the greatest common denominator.
	 *
	 * @param numerator	The numerator of this new fraction.
	 * @param denominator	The denominator of this new fraction.
	 */
	public BigFraction(final long numerator, final long denominator) {
	    this(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
	}

	/**
	 * Construct a fraction from a decimal value.
	 * <p> Done by separating the whole number and fraction parts, and making a
	 * rational fraction out of whatever decimal parts we have.
	 *
	 * @param value	A decimal value to convert.
	 */
	public BigFraction(final BigDecimal value) {
	    int pow = value.scale();
// TODO: what about negative scale?
	    BigDecimal whole = value.scaleByPowerOfTen(pow);

	    normalize(new BigInteger(whole.toPlainString()), MathUtil.tenPower(pow));
	}

	/**
	 * Construct a fraction from the given string in one of several forms:
	 * <ul><li>one integer number - a whole number</li>
	 * <li>two integers separated by comma, semicolon, or slash</li>
	 * <li>three integers (as in {@code 3 1/2})</li>
	 * <li>or some combination with the Unicode fraction characters</li>
	 * </ul>
	 *
	 * @param value	A string formatted as above.
	 * @return	The resulting fraction value.
	 * @throws	IllegalArgumentException if the string doesn't match one
	 *		of the supported formats.
	 */
	public static BigFraction valueOf(final String value) {
	    Matcher m3 = THREE_INTS.matcher(value);
	    if (m3.matches()) {
		return new BigFraction(m3.group(1), m3.group(3), m3.group(5));
	    }
	    else {
		Matcher m2 = TWO_INTS.matcher(value);
		if (m2.matches()) {
		    return new BigFraction(m2.group(1), m2.group(3));
		}
		else {
		    Matcher m1 = INT_FRAC.matcher(value);
		    if (m1.matches()) {
			BigFraction fraction = fractionValue(m1.group(3));
			BigInteger integer   = new BigInteger(m1.group(1));
			int fracSgn = fraction.signum();
			int intSgn  = integer.signum();
			boolean negative = fracSgn < 0 || intSgn < 0;
			BigFraction fullValue = new BigFraction(integer).abs().add(fraction.abs());
			if (negative)
			    return fullValue.negate();
			else
			    return fullValue;
		    }
		    else {
			Matcher m0 = FRAC_ONLY.matcher(value);
			if (m0.matches()) {
			    return fractionValue(value);
			}
			else {
			    try {
				return new BigFraction(new BigInteger(value));
			    }
			    catch (NumberFormatException nfe) {
				;
			    }
			}
		    }
		}
	    }
	    throw new Intl.IllegalArgumentException("util#fraction.unsupportedFormat");
	}

	/**
	 * Construct a fraction given an integer value, numerator, and denominator as integer strings.
	 *
	 * @param intString	The whole number part.
	 * @param numerString	The numerator part string.
	 * @param denomString	The denominator string.
	 * @throws NumberFormatException if one of the strings is not in integer format.
	 */
	public BigFraction(final String intString, final String numerString, final String denomString) {
	   this(new BigInteger(intString), new BigInteger(numerString), new BigInteger(denomString));
	}

	/**
	 * Construct a fraction given the numerator and denominator values as integer strings.
	 *
	 * @param numerString	The numerator as an integer string.
	 * @param denomString	The denominator.
	 * @throws NumberFormatException if one of the strings is not in integer format.
	 */
	public BigFraction(final String numerString, final String denomString) {
	    this(new BigInteger(numerString), new BigInteger(denomString));
	}

	/**
	 * Construct a fraction with a whole number value, plus numerator and denominator.
	 *
	 * @param integer	The integer (whole number) part.
	 * @param numerator	The numerator of the fraction part.
	 * @param denominator	The denominator of the fraction part.
	 */
	public BigFraction(final long integer, final long numerator, final long denominator) {
	    this(BigInteger.valueOf(integer), BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
	}

	/**
	 * Construct a fraction with a whole number value, plus numerator and denominator.
	 * <p> Deal righteously with any mix of positive/negative values for the parts:
	 * <ul><li>numer,denom are normalized with the sign in the numerator</li>
	 * <li>then, if fraction or integer are either one or both negative, then the whole value is negative</li>
	 * <li>regardless of signs, the resulting value is <code>+/- int + frac</code></li>
	 * </ul>
	 *
	 * @param integer	The integer value.
	 * @param numerator	The numerator value.
	 * @param denominator	The denominator value.
	 */
	public BigFraction(final BigInteger integer, final BigInteger numerator, final BigInteger denominator) {
	    boolean negative = false;
	    BigFraction fraction = new BigFraction(numerator, denominator);
	    int wholeSgn = integer.signum();
	    int fracSgn  = fraction.signum();
	    if (wholeSgn < 0 || fracSgn < 0)
		negative = true;

	    BigFraction value = new BigFraction(integer);
	    value = value.abs().add(fraction.abs());
	    if (negative)
		this.numer = value.negate().numer;
	    else
		this.numer = value.numer;
	    this.denom = value.denom;
	}

	/**
	 * Construct a fraction from the given numerator and denominator,
	 * normalized to the greatest common denominator.
	 *
	 * @param numerator	The numerator of this new fraction.
	 * @param denominator	The denominator of the fraction.
	 * @throws ArithmeticException if the denominator equals {@link BigInteger#ZERO}.
	 */
	public BigFraction(final BigInteger numerator, final BigInteger denominator) {
	    normalize(numerator, denominator);
	}

	/**
	 * Normalize values and set the internal fields from the given values.
	 * <p> Divides both numerator and denominator values by their GCD before
	 * setting into the {@link #numer} and {@link #denom} fields. Normalizes
	 * zero to the canonical form, and sets the sign on the numerator (so
	 * {@link #negate} and {@link #abs} work correctly).
	 *
	 * @param num	The proposed numerator.
	 * @param den	The proposed denominator.
	 * @throws ArithmeticException if the proposed denominator is zero.
	 */
	private void normalize(final BigInteger num, final BigInteger den) {
	    // Manage the overall sign of the fraction from the signs of numerator and denominator
	    int signNum = num.signum();
	    int signDen = den.signum();

	    // For now (at least), we cannot handle infinite values (divide by zero)
	    if (signDen == 0)
		throw new ArithmeticException(Intl.getString("util#fraction.noZeroDenominator"));

	    // Normalize zero to "0/1"
	    if (signNum == 0) {
		numer = BigInteger.ZERO;
		denom = BigInteger.ONE;
	    }
	    else {
		// Reduce the fraction to its lowest common denominator
		BigInteger gcd = num.gcd(den);

		BigInteger n = num.divide(gcd);
		BigInteger d = den.divide(gcd);

		// Normalize sign to be on the numerator only
		if (signDen < 0) {
		    numer = n.negate();
		    denom = d.negate();
		}
		else {
		    numer = n;
		    denom = d;
		}
	    }
	}

	/**
	 * Is this fraction actually a whole number? That is,
	 * the denominator is one.
	 * <p> This does really assume that our values are kept normalized
	 * to the greatest common denominator, so that {@code 4/2} for instance
	 * would return true (because it was reduced to {@code 2/1}).
	 *
	 * @return {@code true} if the denominator is one, and this
	 * fraction actually represents a whole number.
	 */
	public boolean isWholeNumber() {
	    return denom.equals(BigInteger.ONE);
	}

	/**
	 * @return Whether or not this fraction is always formatted as a proper fraction.
	 */
	public boolean isAlwaysProper() {
	    return alwaysProper;
	}

	/**
	 * Set the flag to indicate this fraction should format (always) as
	 * a "proper" string, or not.
	 *
	 * @param proper Whether or not to always format as a proper fraction.
	 * @return       This fraction (so other operations can be chained).
	 */
	public BigFraction setAlwaysProper(final boolean proper) {
	    alwaysProper = proper;
	    return this;
	}

	/**
	 * Compute the double value of this fraction.
	 *
	 * @return The decimal equivalent to ~18 digits.
	 */
	@Override
	public double doubleValue() {
	    return toDecimal(MathContext.DECIMAL64).doubleValue();
	}

	/**
	 * Compute the float value of this fraction.
	 *
	 * @return The decimal equivalent to ~8 digits.
	 */
	@Override
	public float floatValue() {
	    return toDecimal(MathContext.DECIMAL32).floatValue();
	}

	/**
	 * Compute the long value of this fraction.
	 *
	 * @return The (possibly truncated) long equivalent of this fraction.
	 */
	@Override
	public long longValue() {
	    return toInteger().longValue();
	}

	/**
	 * Convert this fraction to an exact {@code long} value.
	 *
	 * @return The exact long value of this fraction, if possible.
	 * @throws ArithmeticException if this value is not a whole
	 * number or the value is out of range of a {@code long} type.
	 */
	public long longValueExact() {
	    if (isWholeNumber()) {
		return numer.longValueExact();
	    }
	    throw new ArithmeticException(Intl.formatString("calc#noConvertInteger", toString()));
	}

	/**
	 * Compute the integer value of this fraction.
	 *
	 * @return The (possibly truncated) int equivalent of this fraction.
	 */
	@Override
	public int intValue() {
	    return toInteger().intValue();
	}

	/**
	 * Convert this fraction to an exact {@code int} value.
	 *
	 * @return The exact int value of this fraction, if possible.
	 * @throws ArithmeticException if this value is not a whole
	 * number or the value is out of range of an {@code int} type.
	 */
	public int intValueExact() {
	    if (isWholeNumber()) {
		return numer.intValueExact();
	    }
	    throw new ArithmeticException(Intl.formatString("calc#noConvertInteger", toString()));
	}

	/**
	 * Negate this fraction.
	 *
	 * @return	The negative value of this fraction, which is the negative
	 *		of this numerator, over the same denominator.
	 */
	public BigFraction negate() {
	    if (numer.equals(BigInteger.ZERO))
		return this;

	    return new BigFraction(numer.negate(), denom);
	}

	/**
	 * Increment the value by one.
	 *
	 * @return {@code (numer/denom) + 1/1}.
	 */
	public BigFraction increment() {
	    return add(ONE);
	}

	/**
	 * Decrement the value by one.
	 *
	 * @return {@code (numer/denom) - 1/1}.
	 */
	public BigFraction decrement() {
	    return subtract(ONE);
	}

	/**
	 * Add the given whole number to this fraction.
	 *
	 * @param value	The other (whole) number to add.
	 * @return	A new fraction increased by the {@code value}.
	 */
	public BigFraction add(final long value) {
	    if (value == 0L)
		return this;

	    return add(new BigFraction(value));
	}

	/**
	 * Add the other fraction to this one.
	 *
	 * @param other	The fraction to add to this one.
	 * @return	The result of the addition, over the least common denominator.
	 */
	public BigFraction add(final BigFraction other) {
	    // Adding zero to any number yields the same number
	    if (other.numer.equals(BigInteger.ZERO))
		return this;

	    // On the other hand, if we are zero, then return the other
	    if (this.numer.equals(BigInteger.ZERO))
		return other;

	    BigInteger num;
	    BigInteger den;

	    // If the fractions have the same denominator, simply add the numerators
	    if (other.denom.equals(this.denom)) {
		num = this.numer.add(other.numer);
		den = this.denom;
	    }
	    else {
		// General algorithm: putting on a common denominator and adding the resulting
		// numerators
		num = this.numer.multiply(other.denom).add(other.numer.multiply(this.denom));
		den = this.denom.multiply(other.denom);
	    }

	    if (num.equals(BigInteger.ZERO))
		return ZERO;

	    return new BigFraction(num, den);
	}

	/**
	 * Subtract the given whole number from this fraction.
	 *
	 * @param value	The whole number to subtract.
	 * @return	Result of the subtraction.
	 */
	public BigFraction subtract(final long value) {
	    if (value == 0L)
		return this;

	    return subtract(new BigFraction(value));
	}

	/**
	 * Subtract the other fraction from this one.
	 *
	 * @param other	The fraction to subtract from this one.
	 * @return	The result of the subtraction, over the least common denominator.
	 */
	public BigFraction subtract(final BigFraction other) {
	    // Subtracting zero from any number yields the same number
	    if (other.numer.equals(BigInteger.ZERO))
		return this;

	    if (this.numer.equals(BigInteger.ZERO))
		return other.negate();

	    BigInteger num;
	    BigInteger den;

	    if (other.denom.equals(this.denom)) {
		num = this.numer.subtract(other.numer);
		den = this.denom;
	    }
	    else {
		num = this.numer.multiply(other.denom).subtract(other.numer.multiply(this.denom));
		den = this.denom.multiply(other.denom);
	    }

	    if (num.equals(BigInteger.ZERO))
		return ZERO;

	    return new BigFraction(num, den);
	}

	/**
	 * Multiply this fraction by the given long value, which is the result of
	 * multiplying the numerator by the given value, over the same denominator,
	 * but to the least common denominator.
	 *
	 * @param value	The other value to multiply this fraction by.
	 * @return	The result of multiplying this fraction by the given whole number.
	 */
	public BigFraction multiply(final long value) {
	    if (value == 0L)
		return ZERO;
	    else if (value == 1L)
		return this;

	    return multiply(new BigFraction(value));
	}

	/**
	 * Multiply this fraction by the given other fraction, which is the result of
	 * multiplying the numerators over the denominators multiplied together, then
	 * reduced to a common denominator.
	 *
	 * @param other	The other fraction to multiply by.
	 * @return	The result of multiplying this fraction by the other one.
	 */
	public BigFraction multiply(final BigFraction other) {
	    if (other.equals(ZERO) || equals(ZERO))
		return ZERO;
	    else if (other.equals(ONE))
		return this;

	    return new BigFraction(numer.multiply(other.numer), denom.multiply(other.denom));
	}

	/**
	 * Return a new fraction which is the result of this fraction divided by the
	 * given whole number.
	 *
	 * @param value	The whole number to divide by.
	 * @return	The result of {@code this.n / (this.d * value)}.
	 */
	public BigFraction divide(final long value) {
	    if (value == 0L)
		throw new ArithmeticException(Intl.getString("util#fraction.divideByZero"));
	    else if (value == 1L)
		return this;

	    return new BigFraction(numer, denom.multiply(BigInteger.valueOf(value)));
	}

	/**
	 * Return a new fraction which is the result of this fraction divided by {@code other}.
	 *
	 * @param other	The fraction to divide by.
	 * @return	The result of {@code (this.n/this.d) / (other.n/other.d)}, which is
	 *		the same as {@code (this.n * other.d), (this.d * other.n)}.
	 */
	public BigFraction divide(final BigFraction other) {
	    if (other.equals(ZERO))
		throw new ArithmeticException(Intl.getString("util#fraction.divideByZero"));
	    else if (equals(ZERO))
		return ZERO;
	    else if (other.equals(ONE))
		return this;
	    else if (equals(ONE))
		return other.reciprocal();

	    return new BigFraction(numer.multiply(other.denom), denom.multiply(other.numer));
	}

	/**
	 * Return the reciprocal of this fraction (which would be <code>denom/numer</code>).
	 *
	 * @return The result of <code>1/this</code>.
	 */
	public BigFraction reciprocal() {
	    return new BigFraction(denom, numer);
	}

	/**
	 * Return this fraction taken to the exponent power.
	 *
	 * @param exponent The power to raise this fraction to.
	 * @return this<sup>exponent</sup>.
	 */
	public BigFraction pow(final int exponent) {
	    if (exponent < 0)
		return reciprocal().pow(-exponent);
	    else if (exponent == 0)
		return ONE;
	    else if (exponent == 1)
		return this;

	    return new BigFraction(numer.pow(exponent), denom.pow(exponent));
	}

	/**
	 * Return the absolute value of this fraction.
	 * <p> Since the sign is normalized to be always in the numerator, just
	 * take the absolute value of the numerator.
	 *
	 * @return A new fraction that is the absolute value of the fraction.
	 */
	public BigFraction abs() {
	    return (numer.signum() < 0) ? negate() : this;
	}

	/**
	 * Returns the "signum" value for this function.
	 * <p> Because we keep the values normalized, the sign and zero
	 * will always be in the numerator, so we just need to check that.
	 *
	 * @return The {@code signum} function on this fraction.
	 */
	public int signum() {
	    return numer.signum();
	}

	/**
	 * Helper method to return the LCM (Least Common Multiple) of two
	 * {@link BigInteger}s.
	 * <p><code>LCM(a, b) = (a * b) / GCD(a, b)</code>
	 *
	 * @param a	The first integer.
	 * @param b	The second integer.
	 * @return	The LCM of a, b.
	 */
	public static BigInteger lcm(final BigInteger a, final BigInteger b) {
	    BigInteger gcd = a.gcd(b);

	    if (gcd.equals(BigInteger.ZERO))
		throw new ArithmeticException(Intl.getString("util#fraction.divideByZero"));

	    return a.multiply(b).divide(gcd);
	}

	/**
	 * Return the GCD (Greatest Common Divisor) of this and the other
	 * fraction.
	 * <p><code>GCD(a/b, c/d) = GCD(a, c) / LCM(b, d)</code>
	 *
	 * @param other	The other fraction to work on.
	 * @return	The GCD of this, other.
	 */
	public BigFraction gcd(final BigFraction other) {
	    return new BigFraction(
		this.numer.gcd(other.numer),
		lcm(this.denom, other.denom));
	}

	/**
	 * Return the LCM (Least Common Multiple) of this and the other
	 * fraction.
	 * <p><code>LCM(a/b, c/d) = LCM(a, c) / GCD(b, d)</code>
	 *
	 * @param other	The other fraction to work on.
	 * @return	The LCM of this, other.
	 */
	public BigFraction lcm(final BigFraction other) {
	    return new BigFraction(
		lcm(this.numer, other.numer),
		this.denom.gcd(other.denom));
	}

	/**
	 * Return the value of this fraction truncated to the next lowest integer.
	 * <p> If {@link #isWholeNumber} would return {@code true}, then this will
	 * return that whole number, otherwise it will return the value of
	 * {@code numer / denom}, dropping any remainder.
	 *
	 * @return The value of this fraction as a whole integer, which may have
	 * chopped off any real fraction.
	 */
	public BigInteger toInteger() {
	    return isWholeNumber() ? numer : numer.divide(denom);
	}

	/**
	 * Return the value of this fraction as an exact integer if possible.
	 * <p> If {@link #isWholeNumber} would return {@code true}, then this will
	 * return that whole number, otherwise throw an exception.
	 *
	 * @return The value of this fraction as a whole integer, if possible.
	 * @throws ArithmeticException if the fraction has a non-integer part.
	 */
	public BigInteger toIntegerExact() {
	    if (isWholeNumber()) {
		return numer;
	    }
	    throw new ArithmeticException(Intl.formatString("calc#noConvertInteger", this));
	}

	/**
	 * Return the value of this fraction as a decimal value, rounded to the
	 * {@link MathContext#DECIMAL128} scale.
	 *
	 * @return	The value of {@code numer / denom} as a decimal number.
	 */
	public BigDecimal toDecimal() {
	    return toDecimal(MathContext.DECIMAL128);
	}

	/**
	 * Return the value of this fraction as a decimal value, rounded to the given scale.
	 *
	 * @param mc	The {@link MathContext} used to round the result.
	 * @return	The value of {@code numer / denom} as a decimal number.
	 */
	public BigDecimal toDecimal(final MathContext mc) {
	    return new BigDecimal(numer).divide(new BigDecimal(denom), mc);
	}

	/**
	 * Compare this fractional value with another one.
	 *
	 * @param other	The fraction to compare to.
	 * @return	{@code < 0} if this is less than other,
	 *		{@code == 0} if this is equal to other,
	 *		{@code > 0} if this is greater than other.
	 */
	@Override
	public int compareTo(final BigFraction other) {
	    // Put over common denominator, as in:
	    // n1 / d1 cmp n2 / d2 ->  (n1 * d2) / (d1 * d2) cmp (n2 * d1) / (d1 * d2)
	    BigInteger n1a = this.numer.multiply(other.denom);
	    BigInteger n2a = other.numer.multiply(this.denom);

	    return n1a.compareTo(n2a);
	}

	/**
	 * @return The regular string value (<code><i>numer</i> / <i>denom</i></code>).
	 */
	private String internalToString() {
	    return String.format("%1$s/%2$s", numer, denom);
	}

	/**
	 * Return a string of this value in whole number plus fraction form.
	 * <p> The fraction is maintained in strictly rational form of {@code numerator / denominator },
	 * while this function will return the fraction in proper form ({@code numerator < denominator})
	 * plus the whole number.
	 *
	 * @return	A string in the form of a whole number plus the fraction.
	 */
	public String toProperString() {
	    if (isWholeNumber()) {
		return numer.toString();
	    }
	    else if (numer.abs().compareTo(denom) >= 0) {
		BigInteger[] results = numer.divideAndRemainder(denom);
		if (results[1].equals(BigInteger.ZERO))
		    return results[0].toString();
		else
		    return String.format("%1$s %2$s/%3$s",
				results[0], results[1].abs(), denom);
	    }
	    return internalToString();
	}

	/**
	 * Return a string in the form of <code>"<i>numer</i>/<i>denom</i>"</code>,
	 * unless the {@link #alwaysProper} flag is set, in which case call
	 * {@link #toProperString}.
	 *
	 * @return	The string form of this fraction.
	 */
	@Override
	public String toString() {
	    return alwaysProper ? toProperString() : internalToString();
	}

	/**
	 * Return a hash code of this value.
	 *
	 * @return "Unique" hash value for this fraction.
	 */
	@Override
	public int hashCode() {
	    byte[] n = numer.toByteArray();
	    byte[] d = denom.toByteArray();
	    return 31 ^ Arrays.hashCode(n) ^ Arrays.hashCode(d);
	}

	/**
	 * @return Whether this fraction equals the other one.
	 *
	 * @param other The other fraction to compare to.
	 */
	@Override
	public boolean equals(final Object other) {
	    if (other == null || !(other instanceof BigFraction))
		return false;

	    BigFraction otherFrac = (BigFraction)other;

	    // If comparing to ourselves, then obviously true
	    if (this == otherFrac)
		return true;

	    // Since these are kept normalized, then we just need to compare
	    // the numerators and denominators directly.
	    return this.numer.equals(otherFrac.numer) && this.denom.equals(otherFrac.denom);
	}

}
