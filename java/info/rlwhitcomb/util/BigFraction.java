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
 */
package info.rlwhitcomb.util;

import java.math.BigInteger;


public class BigFraction
{
	private BigInteger numer;
	private BigInteger denom;

	public BigFraction(final long numerator, final long denominator) {
	    return new BigFraction(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
	}

	public BigFraction(final BigInteger numerator, final BigInteger denominator) {
	    if (denominator.equals(BigInteger.ZERO))
		throw new ArithmeticException("Fractions cannot have zero denominators.");

	    BigInteger gcd = numerator.gcd(denominator);

	    this.numer = numerator.divide(gcd);
	    this.denom = denominator.divide(gcd);
	}

	public BigFraction negate() {
	    return new BigFraction(numer.negate(), denom);
	}

	public BigFraction add(final BigFraction other) {
	    if (other.numer.equals(BigInteger.ZERO))
		return this;

	    if (other.denom.equals(this.denom))
		return BigFraction(this.numer.add(other.numer), this.denom);

	    return new BigFraction(
		this.numer.multiply(other.denom).add(other.numer.multiply(this.denom)),
		this.denom.multiply(other.denom));
	}

	public BigFraction subtract(final BigFraction other) {
	    if (other.numer.equals(BigInteger.ZERO))
		return this;

	    if (other.denom.equals(this.denom))
		return new BigFraction(this.numer.subtract(other.numer), this.denom);

	    return new BigFraction(
		this.numer.multiply(other.denom).subtract(other.numer.multiply(this.denom)),
		this.denom.multiply(other.denom));
	}

	public BigFraction multiply(final long value) {
	    return new BigFraction(this.numer.multiply(BigInteger.valueOf(value)), this.denom);
	}

	public BigFraction multiply(final BigFraction other) {
	    return new BigFraction(
		this.numer.multiply(other.numer), this.denom.multiply(other.denom));
	}

}
