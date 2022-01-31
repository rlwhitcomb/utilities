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
 *	A complex number, consisting of BigDecimal real part and BigDecimal
 *	imaginary part. This class stores these values, and provides a number
 *	of arithmetic and other operations related to them.
 *
 *  History:
 *	24-Jan-2022 (rlwhitcomb)
 *	    Created.
 *	    #103: More work: extend Number, implement Comparable, Serializable.
 *	30-Jan-2022 (rlwhitcomb)
 *	    #103: extend aliases of "i" (must match CalcPredefine).
 */
package info.rlwhitcomb.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


/**
 * Storage of and calculations with complex numbers.
 * <p> Values are kept in a normalized form, which means
 * (among other things, TBD) that pure real and pure imaginary
 * numbers have their other component set to {@code null},
 * which also means that {@code 0} values for either are
 * removed, to reduce the number of special cases in the math.
 * <p> TODO: more math operations, conversions, possibly extend Number
 * and implement Comparable and Serializable (see BigFraction).
 */
public class ComplexNumber extends Number implements Serializable, Comparable<ComplexNumber>
{
	private static final long serialVersionUID = 1786873163402226934L;

	/** String pattern to recognize INT (same as "INT" in Calc.g4). */
	private static final String INT = "(0|[1-9][0-9]*)";
	/** Signed integer string pattern. */
	private static final String SIGNED_INT = "[+\\-]?" + INT;

	/** String pattern to recognize NUMBER (same as "NUMBER" in Calc.g4). */
	private static final String NUMBER = INT + "(\\.[0-9]*)?([Ee]" + SIGNED_INT + ")?";
	/** Signed number string pattern. */
	private static final String SIGNED_NUMBER = SIGNED_INT + "(\\.[0-9]*)?([Ee]" + SIGNED_INT + ")?";
	/** Character aliases for "i" (must match CalcPredefine "I_ALIASES"). */
	private static final String I_ALIASES = "[iI\u0131\u0399\u03B9\u2148]";

	/**
	 * The patterns used in {@link #parse} to recognize valid values.
	 */
	private static final Pattern COMPLEX_PATTERNS[] = {
	    Pattern.compile("^\\s*\\(\\s*(" + SIGNED_NUMBER + ")\\s*([,])\\s*(" + SIGNED_NUMBER + ")\\s*\\)\\s*$"),
	    Pattern.compile("^\\s*(" + SIGNED_NUMBER + ")\\s*([,])\\s*(" + SIGNED_NUMBER + ")\\s*$"),
	    Pattern.compile("^\\s*\\(\\s*(" + SIGNED_NUMBER + ")\\s*([+\\-])\\s*(" + NUMBER + ")\\s*" + I_ALIASES + "\\s*\\)\\s*$"),
	    Pattern.compile("^\\s*(" + SIGNED_NUMBER + ")\\s*([+\\-])\\s*(" + NUMBER + ")\\s*" + I_ALIASES + "\\s*$")
	};

	/**
	 * A static value of {@code (1, 0)} (or real {@code 1.0}).
	 */
	public static final ComplexNumber ONE = new ComplexNumber(1, 0);

	/**
	 * A static value of {@code (0, 1)} (or {@code i}).
	 */
	public static final ComplexNumber I = new ComplexNumber(0, 1);


	/**
	 * The real part of this complex number.
	 */
	private BigDecimal realPart;

	/**
	 * The imaginary part of this complex number.
	 */
	private BigDecimal imaginaryPart;

	/**
	 * Construct one, given the real and imaginary values.
	 *
	 * @param r Value for the real part (can be {@code null} for a pure
	 *          imaginary number (such as {@code 2i}).
	 * @param i Value for the imaginary part (can also be {@code null} for a
	 *          pure real number (such as {@code 3.5}).
	 */
	public ComplexNumber(final BigDecimal r, final BigDecimal i) {
	    realPart = r;
	    imaginaryPart = i;
	    normalize();
	}

	/**
	 * Construct one, given the real and imaginary values.
	 *
	 * @param r Value for the real part (can be {@code null} for a pure
	 *          imaginary number (such as {@code 2i}).
	 * @param i Value for the imaginary part (can also be {@code null} for a
	 *          pure real number (such as {@code 3.5}).
	 */
	public ComplexNumber(final BigInteger r, final BigInteger i) {
	    realPart = r == null ? null : new BigDecimal(r);
	    imaginaryPart = i == null ? null : new BigDecimal(i);
	    normalize();
	}

	/**
	 * Alternate constructor, just to do the necessary conversions
	 * to our internal storage. Note: this constructor does not
	 * support either pure real or pure imaginary numbers; there
	 * are others (similar) for that use case.
	 *
	 * @param r Value of the real part.
	 * @param i Value for the imaginary part.
	 */
	public ComplexNumber(final double r, final double i) {
	    realPart = BigDecimal.valueOf(r);
	    imaginaryPart = BigDecimal.valueOf(i);
	    normalize();
	}

	/**
	 * Alternate constructor, just to do the necessary conversions
	 * to our internal storage. Note: this constructor does not
	 * support either pure real or pure imaginary numbers; there
	 * are others (similar) for that use case.
	 *
	 * @param r Value of the real part.
	 * @param i Value for the imaginary part.
	 */
	public ComplexNumber(final long r, final long i) {
	    realPart = BigDecimal.valueOf(r);
	    imaginaryPart = BigDecimal.valueOf(i);
	    normalize();
	}

	/**
	 * Construct one of these from a pure real number.
	 *
	 * @param r The pure real value.
	 * @return  A new complex number with no imaginary part.
	 */
	public static ComplexNumber real(final BigDecimal r) {
	    return new ComplexNumber(r, null);
	}

	/**
	 * Construct one of these from a pure real number.
	 *
	 * @param r The pure real value.
	 * @return  A new complex number with no imaginary part.
	 */
	public static ComplexNumber real(final BigInteger r) {
	    return new ComplexNumber(r, null);
	}

	/**
	 * Alternate pure real "constructor".
	 *
	 * @param r The pure real value.
	 * @return  A new complex number with no imaginary part.
	 */
	public static ComplexNumber real(final double r) {
	    return new ComplexNumber(BigDecimal.valueOf(r), null);
	}

	/**
	 * Alternate pure real "constructor".
	 *
	 * @param r The pure real value.
	 * @return  A new complex number with no imaginary part.
	 */
	public static ComplexNumber real(final long r) {
	    return new ComplexNumber(BigDecimal.valueOf(r), null);
	}


	/**
	 * Access the real part of ourselves.
	 *
	 * @return The real part of this complex number (which will be
	 *         {@code 0} for a pure imaginary number).
	 */
	public BigDecimal r() {
	    return realPart == null ? BigDecimal.ZERO : realPart;
	}

	/**
	 * Access the imaginary part of ourselves.
	 *
	 * @return The imaginary part of this complex number (which will be
	 *         {@code 0} for a pure real number).
	 */
	public BigDecimal i() {
	    return imaginaryPart == null ? BigDecimal.ZERO : imaginaryPart;
	}


	/**
	 * Do the normalization to keep our values in a consistent form.
	 * <p> For now, until we need other things, we will remove {@code 0}
	 * values and keep them stored as {@code null} in that part.
	 */
	private void normalize() {
	    if (realPart != null && realPart.equals(BigDecimal.ZERO))
		realPart = null;
	    if (imaginaryPart != null && imaginaryPart.equals(BigDecimal.ZERO))
		imaginaryPart = null;
	}

	/**
	 * @return Is this a pure real number (imaginary part is zero)?
	 */
	public boolean isPureReal() {
	    return imaginaryPart == null;
	}

	/**
	 * @return Is this a pure imaginary number (real part is zero)?
	 */
	public boolean isPureImaginary() {
	    return realPart == null;
	}


	@Override
	public int compareTo(final ComplexNumber other) {
	    // Not strictly speaking comparable unless pure real or pure imaginary
	    if (isPureReal() && other.isPureReal()) {
		return r().compareTo(other.r());
	    }
	    if (isPureImaginary() && other.isPureImaginary()) {
		return i().compareTo(other.i());
	    }

	    // Otherwise, ordering makes no sense, but one thing to try is comparing
	    // magnitudes, or distance from the origin.
	    // Note: we can't have the MathContext passed in here, so we're going
	    // to use the default in Calc because it's unlikely we'd ever have
	    // two unequal values so close that it would make a difference.
	    MathContext mc = MathContext.DECIMAL128;
	    return radius(mc).compareTo(other.radius(mc));
	}


	/**
	 * Calculate the radius of the polar form of this complex number, which
	 * is {@code sqrt(r^2 + i^2)}.
	 *
	 * @param mc The rounding context to use for the calculation.
	 * @return The polar radius of this number.
	 */
	public BigDecimal radius(final MathContext mc) {
	    BigDecimal rPart = r();	// to get real zeros if null
	    BigDecimal iPart = i();
	    BigDecimal rTerm = rPart.multiply(rPart);
	    BigDecimal iTerm = iPart.multiply(iPart);

	    return MathUtil.sqrt(rTerm.add(iTerm), mc);
	}

	/**
	 * Calculate the angle of the polar form of this complex number, which
	 * is {@code atan2(b, a)} for {@code a > 0}, or {@code atan2(b, a) + pi}
	 * for {@code a < 0}.
	 * <p> Note: for now, the calculation is done using {@link Double} precision
	 * until we have the {@code atan2} calculation for BigDecimal.
	 *
	 * @param mc The rounding context to use for the calculation.
	 * @return The angle of this complex number in polar form (in radians).
	 * @throws ArithmeticException for a pure imaginary number ({@code a == 0}).
	 */
	public BigDecimal theta(final MathContext mc) {
	    if (imaginaryPart == null)
		return BigDecimal.ZERO;

	    double angle;

	    if (realPart == null) {
		angle = Math.PI / 2.0d;
		if (imaginaryPart.signum() < 0)
		    angle = -angle;
	    }
	    else {
		double b = i().doubleValue();
		double a = r().doubleValue();
		angle = Math.atan2(b, a);

		if (a < 0.0d)
		    angle += Math.PI;
	    }

	    return BigDecimal.valueOf(angle);
	}


	/**
	 * Add the two complex numbers, this and the other.
	 * <p> The result of <code>( a, b ) + ( c, d )</code> is
	 * <code>( (a+b), (c+d) )</code>.
	 *
	 * @param other The number to add to this one.
	 * @return      The sum.
	 */
	public ComplexNumber add(final ComplexNumber other) {
	    BigDecimal a1 = r();
	    BigDecimal a2 = other.r();
	    BigDecimal b1 = i();
	    BigDecimal b2 = other.i();

	    return new ComplexNumber(a1.add(a2), b1.add(b2));
	}

	/**
	 * Subtract the two complex numbers, this and the other.
	 * <p> The result of <code>( a, b ) - ( c, d )</code> is
	 * <code>( (a-b), (c-d) )</code>.
	 *
	 * @param other The number to subtract from this one.
	 * @return      The difference.
	 */
	public ComplexNumber subtract(final ComplexNumber other) {
	    BigDecimal a1 = r();
	    BigDecimal a2 = other.r();
	    BigDecimal b1 = i();
	    BigDecimal b2 = other.i();

	    return new ComplexNumber(a1.subtract(a2), b1.subtract(b2));
	}

	/**
	 * Multiply the two complex number, this and the other.
	 * <p> The result of <code>( x, y) * ( u , v )</code> is
	 * <code>( (x*u - y*v), (x*v + y*u) )</code>.
	 *
	 * @param other The number to multiply this one by.
	 * @param mc    The rounding mode to use for the final product.
	 * @return      The product.
	 */
	public ComplexNumber multiply(final ComplexNumber other, final MathContext mc) {
	    BigDecimal x = r();
	    BigDecimal y = i();
	    BigDecimal u = other.r();
	    BigDecimal v = other.i();

	    BigDecimal rTerm = x.multiply(u).subtract(y.multiply(v), mc);
	    BigDecimal iTerm = x.multiply(v).add(y.multiply(u), mc);

	    return new ComplexNumber(rTerm, iTerm);
	}

	/**
	 * Compute the complex conjugate of this number, which for
	 * {@code (real, imag)} is {@code (real, -imag)}.
	 *
	 * @return A new complex number which is the conjugate of this one.
	 */
	public ComplexNumber conjugate() {
	    return new ComplexNumber(r(), i().negate());
	}

	/**
	 * Divide this complex number by a real number.
	 * <p> The result is {@code (real/p, imag/p)}.
	 *
	 * @param p  The real number to divide by.
	 * @param mc The rounding mode for the result.
	 * @return   A new ComplexNumber with the result.
	 */
	public ComplexNumber divide(final BigDecimal p, final MathContext mc) {
	    return new ComplexNumber(r().divide(p, mc), i().divide(p, mc));
	}

	/**
	 * Divide this complex number by the other.
	 * <p> To accomplish this, we actually multiply both by the complex conjugate
	 * of the denominator (which doesn't change the value, since it is effectively
	 * a multiply by one). But, the denominator multiplied by its conjugate gives
	 * a real number, which is divided into the numerator multiplication to yield
	 * the result.
	 *
	 * @param other The number to divide by.
	 * @param mc    The rounding mode to use for the result.
	 * @return      This divided by the other.
	 */
	public ComplexNumber divide(final ComplexNumber other, final MathContext mc) {
	    ComplexNumber conjugate = other.conjugate();
	    BigDecimal divisor = other.multiply(conjugate, mc).r();

	    return multiply(conjugate, mc).divide(divisor, mc);
	}

	/**
	 * The absolute value of a complex number is the distance from the origin in the
	 * polar plane, which can be calculated using the Pythagorean theorem.
	 *
	 * @param mc The rounding context to use for the result.
	 * @return   The absolute value of this complex number.
	 * @see #radius
	 */
	public BigDecimal abs(final MathContext mc) {
	    return radius(mc);
	}


	/**
	 * Get the {@code ComplexNumber} equivalent of the input value.
	 *
	 * @param value Some (presumably compatible) value.
	 * @return      The complex equivalent.
	 * @throws IllegalArgumentException if the object can't be converted.
	 */
	public static ComplexNumber valueOf(final Object value) {
	    if (value == null)
		return null;
	    if (value instanceof ComplexNumber)
		return (ComplexNumber) value;
	    if (value instanceof BigDecimal)
		return real((BigDecimal) value);
	    if (value instanceof BigInteger)
		return real((BigInteger) value);
	    if (value instanceof Number)
		return real(((Number) value).doubleValue());
	    return parse(value.toString());
	}


	/**
	 * Parse out and construct a complex number from the canonical string form.
	 *
	 * @param string The supposed string form of a complex number.
	 * @return       The parsed number.
	 * @throws       IllegalArgumentException if it cannot be parsed.
	 */
	public static ComplexNumber parse(final String string) {
	    Matcher m = null;
	    for (int i = 0; i < COMPLEX_PATTERNS.length; i++) {
		m = COMPLEX_PATTERNS[i].matcher(string);
		if (m.matches())
		    break;
	    }
	    if (!m.matches()) {
		throw new Intl.IllegalArgumentException("%util#complex.notRecognized", string);
	    }

	    BigDecimal rPart = new BigDecimal(m.group(1));
	    BigDecimal iPart = new BigDecimal(m.group(7));
	    if (m.group(6) != null && m.group(6).equals("-"))
		iPart = iPart.negate();

	    return new ComplexNumber(rPart, iPart);
	}


	@Override
	public double doubleValue() {
	    return r().doubleValue();
	}

	@Override
	public float floatValue() {
	    return r().floatValue();
	}

	@Override
	public long longValue() {
	    return r().longValue();
	}

	@Override
	public int intValue() {
	    return r().intValue();
	}

	@Override
	public String toString() {
	    return String.format("( %1$s, %2$s )", r().toPlainString(), i().toPlainString());
	}

}
