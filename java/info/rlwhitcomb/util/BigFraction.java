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
 */
package info.rlwhitcomb.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;


/**
 * A class used to implement a rational number system, keeping exact accuracy in
 * {@link BigInteger} numerator and denominator.
 * <p> Fractions are maintained in least common denominator form.
 */
public class BigFraction
{
	/** The exact integer numerator of this fraction. */
	private BigInteger numer;
	/** The exact integer denominator of this fraction. */
	private BigInteger denom;


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
	 * Construct a fraction given the numerator and denominator values as integer strings.
	 *
	 * @param numerString	The numerator as an integer string.
	 * @param denomString	The denominator.
	 */
	public BigFraction(final String numerString, final String denomString) {
	    this(new BigInteger(numerString), new BigInteger(denomString));
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
	    if (denominator.equals(BigInteger.ZERO))
		throw new ArithmeticException(Intl.getString("util#fraction.noZeroDenominator"));

	    BigInteger gcd = numerator.gcd(denominator);

	    this.numer = numerator.divide(gcd);
	    this.denom = denominator.divide(gcd);
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
	 * Return a string of this value in whole number plus fraction form.
	 * <p> The fraction is maintained in strictly rational form of {@code numerator / denominator },
	 * while this function will return the fraction in proper form ({@code numerator < denominator})
	 * plus the whole number.
	 *
	 * @return	A string in the form of a whole number plus the fraction.
	 */
	public String toProperString() {
	    if (numer.abs().compareTo(denom) >= 0) {
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
}
