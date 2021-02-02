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
public class BigFraction extends Number
	implements Comparable<BigFraction>, Serializable
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

	/** A value of {@code 2/1} (integer 2) as a fraction. */
	public static final BigFraction TWO = new BigFraction(2);

	/** A value of {@code 1/2} (one-half) as a fraction. */
	public static final BigFraction ONE_HALF = new BigFraction(1, 2);


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
	 * @param integer	The integer (whole number) part.
	 * @param numerator	The numerator of the fraction part.
	 * @param denominator	The denominator of the fraction part.
	 */
	public BigFraction(final long integer, final long numerator, final long denominator) {
	    this(BigInteger.valueOf(integer), BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
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
		this.numer = BigInteger.ZERO;
		this.denom = BigInteger.ONE;
		return;
	    }

	    // Reduce the fraction to its lowest common denominator
	    BigInteger gcd = num.gcd(den);

	    this.numer = num.divide(gcd).abs();
	    this.denom = den.divide(gcd).abs();

	    // Normalize sign to be on the numerator only
	    if ((signNum > 0 && signDen < 0) || (signNum < 0 && signDen > 0))
		this.numer = this.numer.negate();
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
	 * @return The rounded long equivalent of this fraction.
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
	 * @return The rounded int equivalent of this fraction.
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
	    return new BigFraction(numer.negate(), denom);
	}

	/**
	 * Increment the value by one.
	 *
	 * @return {@code (numer/denom) + 1/1}.
	 */
	public BigFraction increment() {
	    return add(1);
	}

	/**
	 * Decrement the value by one.
	 *
	 * @return {@code (numer/denom) - 1/1}.
	 */
	public BigFraction decrement() {
	    return subtract(1);
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
	    if (value == 0L)
		return BigFraction.ZERO;
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
	    if (other.equals(BigFraction.ZERO))
		return BigFraction.ZERO;
	    else if (other.equals(BigFraction.ONE))
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
	    if (other.equals(BigFraction.ZERO))
		throw new ArithmeticException(Intl.getString("util#fraction.divideByZero"));
	    else if (other.equals(BigFraction.ONE))
		return this;

	    return new BigFraction(numer.multiply(other.denom), denom.multiply(other.numer));
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
	    BigInteger num = a.multiply(b);

	    return num.divide(gcd);
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
	 * Return the value of this fraction as a decimal value, rounded to the
	 * {@link MathContext#DECIMAL128} scale.
	 *
	 * @return	The value of {@code numer / denom} as a decimal number.
	 */
	public BigDecimal toDecimal() {
	    return toDecimal(MathContext.DECIMAL128);
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
