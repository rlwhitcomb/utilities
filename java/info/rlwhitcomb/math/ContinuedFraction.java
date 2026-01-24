/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025-2026 Roger L. Whitcomb.
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
 *  09-Dec-25 rlw #643	More coding.
 *  23-Jan-26 rlw #803	Mediant method.
 */
package info.rlwhitcomb.math;

import info.rlwhitcomb.util.Constants;
import info.rlwhitcomb.util.Intl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Implementation of a continued fraction.
 * <p> Note: much of the work in here relies on the operations in {@link BigFraction}
 * for several reasons: a) the conversion from this to a regular fraction is exact (well, it is
 * until we implement repeating values), and b) there are often alternate arrangements here that
 * correspond with the same real valuei (such as {@code [0;]} and {@code [1;-1]}, and
 * c) negative values really are tricky here. So, for these reasons the most accurate way to
 * do arithmetic is just to convert to a fraction, do the math, and then convert back.
 */
public class ContinuedFraction extends Number implements Comparable<ContinuedFraction>
{
	/** A value of a real zero, as a continued fraction. */
	public static final ContinuedFraction ZERO = ContinuedFraction.zero();

	/** A value of a real one, as a continued fraction. */
	public static final ContinuedFraction ONE = new ContinuedFraction(1L);

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

	/**
	 * Convert from a decimal value to a continued fraction, for instance
	 * {@code 3.14159 -> [ 3 ; 7, 15, 1, 25, 1, 7, 4 ]}.
	 * <p> Note: this algorithm was first tested in the file "test/files/cf2.calc".
	 *
	 * @param d  The decimal value to convert.
	 * @param mc Precision setting to use for limits during division.
	 * @return   Continued fraction equivalent.
	 */
	public static ContinuedFraction fromDecimal(final BigDecimal d, final MathContext mc) {
	    int prec = mc.getPrecision();
	    BigDecimal eps = Constants.D_TEN.pow(-(prec - 9), mc);
	    int MAX_LEN = prec * 5 / 2;
	    List<BigInteger> parts = new ArrayList<>();
	    BigDecimal a = d;

	    BigInteger a0 = MathUtil.floor(a);
	    parts.add(a0);
	    a = a.subtract(new BigDecimal(a0));

	    while (a.compareTo(eps) > 0 && parts.size() < MAX_LEN) {
		a = BigDecimal.ONE.divide(a, mc);
		a0 = MathUtil.floor(a);
		parts.add(a0);
		a = a.subtract(new BigDecimal(a0));
	    }

	    // Adjustment if the last term is 1
	    // since [3;1,6,1] is the same as [3;1,7]
	    int length = parts.size() - 1;
	    if (parts.get(length).equals(BigInteger.ONE)) {
		parts.remove(length--);
		parts.set(length, parts.get(length).add(BigInteger.ONE));
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
	    return fromFraction(toFraction().add(o.toFraction()));
	}

	/**
	 * Compute the mediant value of this fraction and the other.
	 * <p> The mediant is going to be {@code (n1 + n2) / (d1 + d2)}
	 * (with these fractions transformed to regular fractions).
	 *
	 * @param o Continued fraction to compute the mediant with.
	 * @return  New continued fraction of the mediant value.
	 */
	public ContinuedFraction mediant(final ContinuedFraction o) {
	    return fromFraction(toFraction().mediant(o.toFraction()));
	}

	/**
	 * Subtract the other continued fraction from this one, returning
	 * a new fraction which is the difference between the two.
	 *
	 * @param o Continued fraction to subtract from this value.
	 * @return  New fraction of the difference.
	 */
	public ContinuedFraction subtract(final ContinuedFraction o) {
	    return fromFraction(toFraction().subtract(o.toFraction()));
	}

	/**
	 * Multiple this continued fraction by the other one, returning
	 * a new fraction which is the product of the two.
	 *
	 * @param o Continued fraction to multiply with this value.
	 * @return  New fraction of the product.
	 */
	public ContinuedFraction multiply(final ContinuedFraction o) {
	    return fromFraction(toFraction().multiply(o.toFraction()));
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
	    return fromFraction(toFraction().divide(o.toFraction()));
	}

	/**
	 * Return a whole number which is the result of this divided by the other.
	 *
	 * @param o Fraction to divide this one by.
	 * @return  Integer part of the quotient of the division.
	 */
	public ContinuedFraction idivide(final ContinuedFraction o) {
	    return fromFraction(toFraction().idivide(o.toFraction()));
	}

	/**
	 * Return the fractional remainder after division by the other fraction.
	 *
	 * @param o Fraction to divide this one by.
	 * @return  The remainder after the division.
	 */
	public ContinuedFraction remainder(final ContinuedFraction o) {
	    return fromFraction(toFraction().remainder(o.toFraction()));
	}

	/**
	 * Compute the modulus of two fractions, which is {@code this mod y := this - y * floor(this / y)}.
	 *
	 * @param y  Fraction of the modulus value.
	 * @return   Result of {@code this mod y}.
	 */
	public ContinuedFraction modulus(final ContinuedFraction y) {
	    return fromFraction(toFraction().modulus(y.toFraction()));
	}

	/**
	 * Compute both the integer quotient and the remainder in one operation.
	 *
	 * @param other The other fraction to divide by.
	 * @return      Quotient in [0] and remainder in [1].
	 */
	public ContinuedFraction[] divideAndRemainder(final ContinuedFraction other) {
	    ContinuedFraction[] results = new ContinuedFraction[2];
	    results[0] = idivide(other);
	    results[1] = subtract(results[0].multiply(other));
	    return results;
	}

	/**
	 * Compute the integer power value of this continued fraction.
	 *
	 * @param iPower An integer power.
	 * @return       New fraction that is the result.
	 */
	public ContinuedFraction power(final int iPower) {
	    return fromFraction(toFraction().pow(iPower));
	}

	/**
	 * Return a new value incremented by one.
	 *
	 * @return A new value which is this value incremented by one.
	 */
	public ContinuedFraction increment() {
	    return add(ONE);
	}

	/**
	 * Return a new value decremented by one.
	 *
	 * @return A new value, which is this value minus one.
	 */
	public ContinuedFraction decrement() {
	    return subtract(ONE);
	}

	/**
	 * Return this value with a reversed sign in the integer part.
	 *
	 * @return New negated value.
	 */
	public ContinuedFraction negate() {
	    return fromFraction(toFraction().negate());
	}

	/**
	 * Return a new value which is the absolute value (that is, positive)
	 * of this fraction.
	 *
	 * @return New absolute value.
	 */
	public ContinuedFraction abs() {
	    return fromFraction(toFraction().abs());
	}

	/**
	 * Return the sign value of this continued fraction. This is not strictly
	 * due to the sign / value of the integer part, if some of the denominator
	 * values are negative.
	 *
	 * @return {@code -1} if this value is negate, {@code 0} if exactly zero,
	 *         and {@code +1} if the value is positive.
	 */
	public int signum() {
	    return toFraction().signum();
	}

	/**
	 * Return the ceiling value of this fraction.
	 *
	 * @return Smallest integer value which is greater or equal to this value.
	 */
	public BigInteger ceil() {
	    return toFraction().ceil().toIntegerExact();
	}

	/**
	 * Return the floor value of this fraction.
	 *
	 * @return Largest integer value less or equal to this value.
	 */
	public BigInteger floor() {
	    return toFraction().floor().toIntegerExact();
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
	    if (obj instanceof BigDecimal)
		return fromDecimal((BigDecimal) obj, MathUtil.toMC((BigDecimal) obj));
	    if (obj instanceof Number) {
		BigDecimal bd = new BigDecimal(((Number) obj).doubleValue());
		return fromDecimal(bd, MathUtil.toMC(bd));
	    }
	    if (obj instanceof List) {
		@SuppressWarnings("unchecked")
		List<BigInteger> list = (List<BigInteger>) obj;
		return new ContinuedFraction(list);
	    }
	    return null;
	}

	/**
	 * Access one value in the list.
	 *
	 * @param index Zero-based index into the list of values.
	 * @return      The integer value at that index (0 is the integer part),
	 *              returns {@code null} if the index is beyond the current list size.
	 */
	public BigInteger part(final int index) {
	    if (index == 0)
		return intPart;
	    else if (index >= 1) {
		if (index <= denomValues.size())
		    return denomValues.get(index - 1);
		else
		    return null;
	    }
	    else
		throw new Intl.IndexOutOfBoundsException("math#cfrac.badIndex", index, denomValues.size() + 1);
	}

	/**
	 * Set a single value into position within the list of values. Index 0 corresponds
	 * to the integer part.
	 *
	 * @param index Zero-based index into the list of values.
	 * @param value New value to set in that posiiton.
	 * @return      The updated value.
	 */
	public ContinuedFraction setPart(final int index, final Object value) {
	    BigInteger iValue = BigFraction.getInteger(value);
	    if (index == 0)
		intPart = iValue;
	    else if (index >= 1) {
		if (index <= denomValues.size())
		    denomValues.set(index - 1, iValue);
		else
		    denomValues.add(index - 1, iValue);
	    }
	    else
		throw new Intl.IndexOutOfBoundsException("math#cfrac.badIndex", index, denomValues.size() + 1);

	    return this;
	}

	/**
	 * Return the number of elements in this continued fraction, which
	 * will be the number of denominator values plus one.
	 *
	 * @return Number of elements
	 */
	public int size() {
	    return denomValues.size() + 1;
	}

	/**
	 * Return the list of all the values.
	 *
	 * @return List of values as objects.
	 */
	public List<Object> values() {
	    List<Object> list = new ArrayList<>(denomValues.size() + 1);
	    list.add(intPart);
	    list.addAll(denomValues);

	    return list;
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


