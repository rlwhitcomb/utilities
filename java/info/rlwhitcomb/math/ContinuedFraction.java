/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Roger L. Whitcomb.
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
 *	Continued Fraction support.
 *
 * History:
 *  21-Nov-25 rlw #643	Initial coding.
 */
package info.rlwhitcomb.math;

import info.rlwhitcomb.util.Intl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Implementation of a continued fraction.
 */
public class ContinuedFraction extends Number implements Comparable<ContinuedFraction>
{
	/** A value of a real zero, as a continued fraction. */
	public static final ContinuedFraction ZERO = ContinuedFraction.zero();

	/** The integer part of the value. */
	private BigInteger intPart;

	/** The denominator values (can be empty). */
	private List<BigInteger> denomValues = new ArrayList<>();


	/**
	 * Construct with a value of {@code [0; ]}.
	 */
	public static ContinuedFraction zero() {
	    return new ContinuedFraction();
	}

	/**
	 * Default constructor, a value of {@code [0; ]}.
	 */
	public ContinuedFraction() {
	    this(0);
	}

	/**
	 * Constructor from an integer value.
	 *
	 * @param iPart The whole integer portion of the value.
	 */
	public ContinuedFraction(final long iPart) {
	    this(BigInteger.valueOf(iPart));
	}

	/**
	 * Constructor from a {@link BigInteger} value alone.
	 *
	 * @param iPart The whole big integer value.
	 */
	public ContinuedFraction(final BigInteger iPart) {
	    intPart = iPart;
	}

	/**
	 * Constructor from the integer and any number of denominator parts.
	 *
	 * @param iPart The whole integer part.
	 * @param parts Denominator parts.
	 */
	public ContinuedFraction(final BigInteger iPart, final BigInteger... parts) {
	    intPart = iPart;
	    for (BigInteger p : parts) {
		denomValues.add(p);
	    }
	}

	/**
	 * Alternate constructor from the integer and a collection of the denominators.
	 *
	 * @param iPart Integer portion of the value.
	 * @param parts Denominator parts as any type of collection.
	 */
	public ContinuedFraction(final BigInteger iPart, final Collection<BigInteger> parts) {
	    intPart = iPart;
	    denomValues.addAll(parts);
	}

	/**
	 * Another convenience constructor from a single list of all the values, where the
	 * first value will be the integer part, while the remainder are the denominators.
	 *
	 * @param allParts A list of the values for this new continued fraction.
	 */
	public ContinuedFraction(final List<BigInteger> allParts) {
	    if (allParts == null || allParts.isEmpty())
		intPart = BigInteger.ZERO;
	    else {
		intPart = allParts.get(0);
		if (allParts.size() >= 2) {
		    denomValues.addAll(allParts.subList(1, allParts.size()));
		}
	    }
	}

	/**
	 * Evaluate to a rational fraction.
	 *
	 * @return The fractional value of this continued fraction.
	 */
	public BigFraction toFraction() {
	    // Initialize the recurrence
	    BigInteger n0 = BigInteger.ONE;
	    BigInteger d0 = BigInteger.ZERO;
	    BigInteger n1 = intPart;
	    BigInteger d1 = BigInteger.ONE;

	    BigInteger n = n1;
	    BigInteger d = d1;

	    for (BigInteger a : denomValues) {
		n = a.multiply(n1).add(n0);
		d = a.multiply(d1).add(d0);
		n0 = n1;
		n1 = n;
		d0 = d1;
		d1 = d;
	    }

	    return new BigFraction(n, d);
	}

	/**
	 * Evaluate this fraction to the decimal equivalent according to the given precision
	 * and rounding mode.
	 *
	 * @param mc Rounding mode and precision for the conversion to decimal.
	 * @return   Decimal equivalent of this continued fraction.
	 */
	public BigDecimal toDecimal(final MathContext mc) {
	    return toFraction().toDecimal(mc);
	}

	/**
	 * Convert to an exact integer value (if possible).
	 *
	 * @return If the value is an exact integer, return that.
	 * @throws ArithmeticException if the value is not an exact integer.
	 */
	public BigInteger toIntegerExact() {
	    return toFraction().toIntegerExact();
	}

	/**
	 * Convert from a single fraction to a continued fraction, for instance
	 * {@code 31/8 -> [3; 1, 7]}.
	 *
	 * @param f The single rational (fractional) value to convert.
	 * @return  Continued fraction equivalent.
	 */
	public static ContinuedFraction fromFraction(final BigFraction f) {
	    BigInteger a = f.part(0);
	    BigInteger b = f.part(1);

	    List<BigInteger> parts = new ArrayList<>();
	    BigInteger[] divRem;
	    BigInteger r;

	    if (a.compareTo(b) < 0) {
		parts.add(BigInteger.ZERO);
		a = f.part(1);
		b = f.part(0);
	    }

	    for (;;) {
		divRem = a.divideAndRemainder(b);
		parts.add(divRem[0]);
		r = divRem[1];
		if (r.signum() == 0)
		    break;
		a = b;
		b = r;
	    }

	    return new ContinuedFraction(parts);
	}

	@Override
	public boolean equals(final Object o) {
	    if (!(o instanceof ContinuedFraction))
		return false;

	    ContinuedFraction c = (ContinuedFraction) o;

	    return c.intPart.equals(intPart) && c.denomValues.equals(denomValues);
	}

	@Override
	public int compareTo(final ContinuedFraction o) {
	    BigFraction f0 = toFraction();
	    BigFraction f1 = o.toFraction();

	    return f0.compareTo(f1);
	}

	/**
	 * Add the other continued fraction to this one, returning
	 * a new continued fraction which is the sum of the two.
	 *
	 * @param o Continued fraction to add to this value.
	 * @return  The new continued fraction of the sum.
	 */
	public ContinuedFraction add(final ContinuedFraction o) {
	    BigFraction f = toFraction().add(o.toFraction());

	    return fromFraction(f);
	}

	/**
	 * Subtract the other continued fraction from this one, returning
	 * a new fraction which is the difference between the two.
	 *
	 * @param o Continued fraction to subtract from this value.
	 * @return  New fraction of the difference.
	 */
	public ContinuedFraction subtract(final ContinuedFraction o) {
	    BigFraction f = toFraction().subtract(o.toFraction());

	    return fromFraction(f);
	}

	/**
	 * Multiple this continued fraction by the other one, returning
	 * a new fraction which is the product of the two.
	 *
	 * @param o Continued fraction to multiply with this value.
	 * @return  New fraction of the product.
	 */
	public ContinuedFraction multiply(final ContinuedFraction o) {
	    BigFraction f = toFraction().multiply(o.toFraction());

	    return fromFraction(f);
	}

	/**
	 * Divide this continued fraction by the other one, returning
	 * a new fraction which is the quotient of the two.
	 *
	 * @param o Continued fraction to divide this one by.
	 * @return  New fraction of the quotient.
	 * @throws  ArithmeticException if the other value is identically zero.
	 */
	public ContinuedFraction divide(final ContinuedFraction o) {
	    BigFraction f = toFraction().divide(o.toFraction());

	    return fromFraction(f);
	}

	/**
	 * Essentially do a conversion from an arbitrary value to a new
	 * continued fraction.
	 *
	 * @param obj The object to convert from.
	 * @return    A new continued fraction from the input, if possible,
	 *            or {@code null} if no conversion exists.
	 */
	public static ContinuedFraction valueOf(final Object obj) {
	    // this is incomplete
	    if (obj instanceof ContinuedFraction)
		return (ContinuedFraction) obj;
	    if (obj instanceof BigFraction)
		return fromFraction((BigFraction) obj);
	    if (obj instanceof BigInteger)
		return new ContinuedFraction((BigInteger) obj);
	    if (obj instanceof Number)
		return new ContinuedFraction(((Number) obj).longValue());	// this could be a double instead??
// decimals
	    return null;
	}

	@Override
	public double doubleValue() {
	    return toFraction().doubleValue();
	}

	@Override
	public float floatValue() {
	    return toFraction().floatValue();
	}

	@Override
	public long longValue() {
	    return toFraction().longValue();
	}

	@Override
	public int intValue() {
	    return toFraction().intValue();
	}

	public int intValueExact() {
	    return toFraction().intValueExact();
	}

	@Override
	public short shortValue() {
	    return toFraction().shortValue();
	}

	@Override
	public byte byteValue() {
	    return toFraction().byteValue();
	}

	@Override
	public int hashCode() {
	    return intPart.hashCode() ^ denomValues.hashCode();
	}

	@Override
	public String toString() {
	    StringBuilder buf = new StringBuilder("[");
	    buf.append(intPart.toString()).append(";");
	    for (BigInteger d : denomValues) {
		buf.append(d.toString()).append(',');
	    }
	    if (denomValues.isEmpty())
		buf.append(']');
	    else
		buf.setCharAt(buf.length() - 1, ']');
	    return buf.toString();
	}

	/**
	 * Convert to a configurable string format, depending on the given parameters.
	 *
	 * @param separators Whether the numbers should include 1000s separators.
	 * @param extraSpace Whether the final form contains "extra" spaces.
	 * @return           The final formatted string representation.
	 */
	public String toFormatString(final boolean separators, final boolean extraSpace) {
	    StringBuilder buf = new StringBuilder("[");
	    if (extraSpace)
		buf.append(' ');
	    buf.append(Num.formatWithSeparators(intPart, separators));
	    if (extraSpace)
		buf.append(" ; ");
	    else
		buf.append(';');
	    for (BigInteger d : denomValues) {
		buf.append(Num.formatWithSeparators(d, separators));
		if (extraSpace)
		    buf.append(", ");
		else
		    buf.append(',');
	    }
	    if (denomValues.isEmpty()) {
		buf.append(']');
	    }
	    else {
		if (extraSpace)
		    buf.replace(buf.length() - 2, buf.length(), " ]");
		else
		    buf.setCharAt(buf.length() - 1, ']');
	    }
	    return buf.toString();
	}

}


