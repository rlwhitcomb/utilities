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
 * <p> Fractions are maintained in least common denominator form.
 */
public class BigFraction
	implements Comparable<BigFraction>,
		Serializable
{
	private static final long serialVersionUID = 3889374235914093689L;

	/** The pattern used for two strings, as in "numer [;/,] denom". */
	private static final Pattern TWO_STRINGS = Pattern.compile("(-?[0-9]+)\\s*[/,;]\\s*(-?[0-9]+)");
	/** The pattern for three strings, as in "int [:/,] numer [:/,] denom". */
	private static final Pattern THREE_STRINGS = Pattern.compile("(-?[0-9]+)\\s*[/,;]?\\s*(-?[0-9]+)\\s*[/,;]?\\s*(-?[0-9]+)");

	/** A value of {@code 0/1} (integer 0) as a fraction. */
	public static final BigFraction ZERO = new BigFraction(BigInteger.ZERO);

	/** A value of {@code 1/1} (integer 1) as a fraction. */
	public static final BigFraction ONE = new BigFraction();

	/** The exact integer numerator of this fraction. */
	private BigInteger numer;
	/** The exact integer denominator of this fraction. */
	private BigInteger denom;


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
	 * @param value	The whole number numerator.
	 */
	public BigFraction(final long value) {
	    this(value, 1L);
	}

	/**
	 * Construct a whole number fraction ({@code n / 1} from the given
	 * value.
	 *
	 * @param value The whole number numerator.
	 */
	public BigFraction(final BigInteger value) {
	    this(value, BigInteger.ONE);
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

	    normalize(new BigInteger(whole.toPlainString()), NumericUtil.tenPower(pow));
	}

	/**
	 * Construct a fraction from the given string in one of several forms:
	 * <ul><li>one integer number - a whole number</li>
	 * <li>two integers separated by comma, semicolon, or slash</li>
	 * <li>three integers (as in 3 1/2)</li>
	 * </ul>
	 *
	 * @param value	A string formatted as above.
	 * @return	The resulting fraction value.
	 * @throws	IllegalArgumentException if the string doesn't match one
	 *		of the supported formats.
	 */
	public static BigFraction valueOf(final String value) {
	    Matcher m3 = THREE_STRINGS.matcher(value);
	    if (m3.matches()) {
		return new BigFraction(m3.group(1), m3.group(2), m3.group(3));
	    }
	    else {
		Matcher m2 = TWO_STRINGS.matcher(value);
		if (m2.matches()) {
		    return new BigFraction(m2.group(1), m2.group(2));
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
	 * @param integer	The integer value.
	 * @param numerator	The numerator value.
	 * @param denominator	The denominator value.
	 */
	public BigFraction(final BigInteger integer, final BigInteger numerator, final BigInteger denominator) {
	    BigInteger wholeNumber = integer.multiply(denominator);

	    normalize(wholeNumber.add(numerator), denominator);
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
	 * setting into the {@link #numer} and {@link #denom} fields.
	 *
	 * @param num	The proposed numerator.
	 * @param den	The proposed denominator.
	 * @throws ArithmeticException if the proposed denominator is zero.
	 */
	private void normalize(final BigInteger num, final BigInteger den) {
	    if (den.equals(BigInteger.ZERO))
		throw new ArithmeticException(Intl.getString("util#fraction.noZeroDenominator"));

	    // Manage the overall sign of the fraction from the signs of numerator and denominator
	    int signNum = num.signum();
	    int signDen = den.signum();

	    BigInteger gcd = num.gcd(den);

	    this.numer = num.divide(gcd).abs();
	    this.denom = den.divide(gcd).abs();

	    if ((signNum > 0 && signDen < 0) || (signNum < 0 && signDen > 0))
		this.numer = this.numer.negate();
	}

	/**
	 * Negate this fraction.
	 *
	 * @return	The negative value of this fraction, which is the negative
	 *		of this numerator, over the same denominator.
	 */
	public BigFraction negate() {
	    return new BigFraction(numer.negate(), denom);
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

	    // If the fractions have the same denominator, simply add the numerators
	    if (other.denom.equals(this.denom))
		return new BigFraction(this.numer.add(other.numer), this.denom);

	    // General algorithm: putting on a common denominator and adding the resulting
	    // numerators
	    return new BigFraction(
		this.numer.multiply(other.denom).add(other.numer.multiply(this.denom)),
		this.denom.multiply(other.denom));
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

	    if (other.denom.equals(this.denom))
		return new BigFraction(this.numer.subtract(other.numer), this.denom);

	    return new BigFraction(
		this.numer.multiply(other.denom).subtract(other.numer.multiply(this.denom)),
		this.denom.multiply(other.denom));
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
	    return new BigFraction(this.numer.multiply(BigInteger.valueOf(value)), this.denom);
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
	    return new BigFraction(
		this.numer.multiply(other.numer), this.denom.multiply(other.denom));
	}

	/**
	 * Return a new fraction which is the result of this fraction divided by {@code other}.
	 *
	 * @param other	The fraction to divide by.
	 * @return	The result of {@code (this.n/this.d) / (other.n/other.d)}, which is
	 *		the same as {@code (this.n * other.d), (this.d * other.n)}.
	 */
	public BigFraction divide(final BigFraction other) {
	    return new BigFraction(
		this.numer.multiply(other.denom), this.denom.multiply(other.numer));
	}

	/**
	 * Return the absolute value of this fraction.
	 * <p> Since the sign is normalized to be always in the numerator, just
	 * take the absolute value of the numerator.
	 *
	 * @return A new fraction that is the absolute value of the fraction.
	 */
	public BigFraction abs() {
	    return new BigFraction(numer.abs(), denom);
	}

	/**
	 * @return The {@code signum} function on this fraction.
	 */
	public int signum() {
	    return numer.signum();
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
	 * Return a string of this value in whole number plus fraction form.
	 * <p> The fraction is maintained in strictly rational form of {@code numerator / denominator },
	 * while this function will return the fraction in proper form ({@code numerator < denominator})
	 * plus the whole number.
	 *
	 * @return	A string in the form of a whole number plus the fraction.
	 */
	public String toProperString() {
	    if (denom.equals(BigInteger.ONE)) {
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
	    return toString();
	}

	/**
	 * Return a string in the form of {@code "numer / denom"}, regardless of whether
	 * the fraction is proper or not.
	 *
	 * @return	The string form of this fraction.
	 */
	@Override
	public String toString() {
	    return String.format("%1$s/%2$s", numer, denom);
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

	    // Since these are kept normalized, then we just need to compare
	    // the numerators and denominators directly.
	    return this.numer.equals(otherFrac.numer) && this.denom.equals(otherFrac.denom);
	}

}
