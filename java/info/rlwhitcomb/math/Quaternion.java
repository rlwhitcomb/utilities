/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2023 Roger L. Whitcomb.
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
 *	Quaternion support.
 *
 * History:
 *  29-Dec-22 rlw #558:	Initial coding.
 *  05-Jan-23 rlw #558:	More operation methods.
 *  10-Jan-23		Constructor from long (int) values.
 *  04-Feb-23		"power(int)" function.
 *  23-Feb-23 rlw #244:	Formatting with thousands separators.
 *			"conjugate" to "inverse", implement "conjugate" correctly,
 *			Implement "magnitude(), "equals()", "hashCode()", and
 *			Comparable interface (same paradigm as ComplexNumber).
 */
package info.rlwhitcomb.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;


/**
 * Implementation of a quaternion, which is a number system that extends the complex numbers.
 * <p> In mathematics, the quaternion number system extends the complex numbers.
 * <p> Quaternions were first described by the Irish mathematician William Rowan Hamilton in 1843 and applied to mechanics in three-dimensional space.
 * Hamilton defined a quaternion as the quotient of two directed lines in a three-dimensional space, or, equivalently, as the quotient of two vectors.
 * Multiplication of quaternions is noncommutative.
 * Quaternions are generally represented in the form <code><i>a + b<b>i</b> + c<b>j</b> + d<b>k</b></i></code>
 * where a, b, c, and d are real numbers; and i, j, and k are the basic quaternions.
 * <p> Quaternions are used in pure mathematics, but also have practical uses in applied mathematics, particularly for calculations involving
 * three-dimensional rotations, such as in three-dimensional computer graphics, computer vision, and crystallographic texture analysis.
 * <p> They can be used alongside other methods of rotation, such as Euler angles and rotation matrices, or as an alternative to them,
 * depending on the application.
 * <p>Description from the Wikipedia article at: <a href="https://en.wikipedia.org/wiki/Quaternion">https://en.wikipedia.org/wiki/Quaternion</a>
 * accessed on 28-Dec-2022 at 09:10am.
 */
public final class Quaternion extends Number
	implements Comparable<Quaternion>
{
	/** A value of a real zero, as a quaternion. */
	public static final Quaternion ZERO = new Quaternion(0);

	/** A value of a real one, as a quaternion. */
	public static final Quaternion ONE = new Quaternion(1);


	/** Normal format for display. */
	private static final String NORMAL_FORMAT = "( %1$s, %2$s, %3$s, %4$s )";


	/** First term, the real part. */
	private BigDecimal a;

	/** Second term, coefficient of <code><b>i</b></code>. */
	private BigDecimal b;

	/** Third term, coefficient of <code><b>j</b></code>. */
	private BigDecimal c;

	/** Fourth term, coefficient of <code><b>k</b></code>. */
	private BigDecimal d;

	/** Whether this quaternion is based on rational coefficients or real values. */
	private boolean rational;

	/** First term, the real part, as a fraction. */
	private BigFraction aFrac;

	/** Second term, coefficient of <code><b>i</b></code>, as a fraction. */
	private BigFraction bFrac;

	/** Third term, coefficient of <code><b>j</b></code>, as a fraction. */
	private BigFraction cFrac;

	/** Fourth term, coefficient of <code><b>k</b></code>, as a fraction. */
	private BigFraction dFrac;


	/**
	 * Construct given all four coefficients (as "real", that is {@link BigDecimal}, values).
	 *
	 * @param aVal The first term coefficient.
	 * @param bVal Second coefficient (i).
	 * @param cVal Third term (j) coefficient.
	 * @param dVal Fourth coefficient (k).
	 */
	public Quaternion(final BigDecimal aVal, final BigDecimal bVal, final BigDecimal cVal, final BigDecimal dVal) {
	    rational = false;
	    a = aVal;
	    b = bVal;
	    c = cVal;
	    d = dVal;
	    normalize();
	}

	/**
	 * Construct given all four coefficients (as fractions, that is {@link BigFraction}, values).
	 *
	 * @param aF The first term coefficient.
	 * @param bF Second coefficient (i).
	 * @param cF Third term (j) coefficient.
	 * @param dF Fourth coefficient (k).
	 */
	public Quaternion(final BigFraction aF, final BigFraction bF, final BigFraction cF, final BigFraction dF) {
	    rational = true;
	    aFrac = BigFraction.properFraction(aF);
	    bFrac = BigFraction.properFraction(bF);
	    cFrac = BigFraction.properFraction(cF);
	    dFrac = BigFraction.properFraction(dF);
	    normalize();
	}

	/**
	 * Construct given all four coefficients (as "real", that is {@code long}, values).
	 *
	 * @param aVal The first term coefficient.
	 * @param bVal Second coefficient (i).
	 * @param cVal Third term (j) coefficient.
	 * @param dVal Fourth coefficient (k).
	 */
	public Quaternion(final long aVal, final long bVal, final long cVal, final long dVal) {
	    rational = false;
	    a = new BigDecimal(aVal);
	    b = new BigDecimal(bVal);
	    c = new BigDecimal(cVal);
	    d = new BigDecimal(dVal);
	    normalize();
	}


	/**
	 * Construct a real quaternion, with the given value.
	 *
	 * @param r The real value (all other parts will be zero).
	 */
	public Quaternion(final long r) {
	    this(new BigDecimal(r), null, null, null);
	}

	/**
	 * Construct a real quaternion, with the given value.
	 *
	 * @param r The real value (all other parts will be zero).
	 */
	public Quaternion(final BigInteger r) {
	    this(new BigDecimal(r), null, null, null);
	}

	/**
	 * Construct a real quaternion, with the given value.
	 *
	 * @param r The real value (all other parts will be zero).
	 */
	public Quaternion(final BigDecimal r) {
	    this(r, null, null, null);
	}

	/**
	 * Construct a real, rational quaternion with the given value.
	 *
	 * @param rFrac The real fractional value (all other parts will be zero).
	 */
	public Quaternion(final BigFraction rFrac) {
	    this(rFrac, null, null, null);
	}

	/**
	 * Construct a quaternion, given a complex number with two of the values.
	 *
	 * @param cn Complex number to convert.
	 */
	public Quaternion(final ComplexNumber cn) {
	    if (cn.isRational()) {
		aFrac = cn.rFrac();
		bFrac = cn.iFrac();
		cFrac = null;
		dFrac = null;
	    }
	    else {
		a = cn.r();
		b = cn.i();
		c = null;
		d = null;
	    }
	    normalize();
	}

	/**
	 * Convert any arbitrary object into a quaternion.
	 *
	 * @param value The value to convert (if possible).
	 * @return      A compatible quaternion, or {@code null} if no conversion is possible.
	 */
	public static Quaternion valueOf(final Object value) {
	    if (value == null)
		return ZERO;
	    if (value instanceof Quaternion)
		return (Quaternion) value;
	    if (value instanceof BigDecimal)
		return new Quaternion((BigDecimal) value);
	    if (value instanceof BigInteger)
		return new Quaternion((BigInteger) value);
	    if (value instanceof BigFraction)
		return new Quaternion((BigFraction) value);
	    if (value instanceof ComplexNumber)
		return new Quaternion((ComplexNumber) value);

// TODO: more to do, expecially String parsing
	    return null;
	}


	/**
	 * Maintain this value in its "normalized" form, that is, keeping zero (unused) terms as null instead.
	 */
	private void normalize() {
	    if (rational) {
		if (aFrac != null && aFrac.equals(BigFraction.ZERO))
		    aFrac = null;
		if (bFrac != null && bFrac.equals(BigFraction.ZERO))
		    bFrac = null;
		if (cFrac != null && cFrac.equals(BigFraction.ZERO))
		    cFrac = null;
		if (dFrac != null && dFrac.equals(BigFraction.ZERO))
		    dFrac = null;
	    }
	    else {
		if (a != null && a.equals(BigDecimal.ZERO))
		    a = null;
		if (b != null && b.equals(BigDecimal.ZERO))
		    b = null;
		if (c != null && c.equals(BigDecimal.ZERO))
		    c = null;
		if (d != null && d.equals(BigDecimal.ZERO))
		    d = null;
	    }
	}

	/**
	 * Is this a rational quaternion (that is, values are stored as {@link BigFraction})?
	 *
	 * @return The {@link #rational} flag.
	 */
	public boolean isRational() {
	    return rational;
	}

	/**
	 * Add the given quaternion to this one.
	 * <p> The operation is done by adding each component separately.
	 *
	 * @param q The other quaternion to add to this one.
	 * @return  A new quaternion that represents the sum of the two.
	 */
	public Quaternion add(final Quaternion q) {
	    if (rational && q.rational) {
		return new Quaternion(
			aFrac().add(q.aFrac()),
			bFrac().add(q.bFrac()),
			cFrac().add(q.cFrac()),
			dFrac().add(q.dFrac()));
	    }
	    else {
		return new Quaternion(
			a().add(q.a()),
			b().add(q.b()),
			c().add(q.c()),
			d().add(q.d()));
	    }
	}

	/**
	 * Subtract the given quaternion from this one.
	 * <p> The operation is done by subtracting each component separately.
	 *
	 * @param q The other quaternion to subtract from this one.
	 * @return  A new quaternion that represents the difference of the two.
	 */
	public Quaternion subtract(final Quaternion q) {
	    if (rational && q.rational) {
		return new Quaternion(
			aFrac().subtract(q.aFrac()),
			bFrac().subtract(q.bFrac()),
			cFrac().subtract(q.cFrac()),
			dFrac().subtract(q.dFrac()));
	    }
	    else {
		return new Quaternion(
			a().subtract(q.a()),
			b().subtract(q.b()),
			c().subtract(q.c()),
			d().subtract(q.d()));
	    }
	}

	/**
	 * Multiply this quaternion by another; and notice that multiplication
	 * is not commutative.
	 * <p> The result of (a, b, c, d) * (e, f, g, h) will be:
	 * <code> a*e - b*f - c*g - d*h
	 * + i (b*e + a*f + c*h - d*g)
	 * + j (a*g - b*h + c*e + d*f)
	 * + k (a*h + b*g - c*f + d*e)</code>
	 *
	 * @param q  The other qaternion to multiply this one by.
	 * @param mc Math context to control rounding and precision.
	 * @return  Result of multiplying this by the other.
	 */
	public Quaternion multiply(final Quaternion q, final MathContext mc) {
	    if (rational && q.rational) {
		BigFraction a = aFrac();
		BigFraction b = bFrac();
		BigFraction c = cFrac();
		BigFraction d = dFrac();
		BigFraction e = q.aFrac();
		BigFraction f = q.bFrac();
		BigFraction g = q.cFrac();
		BigFraction h = q.dFrac();

		return new Quaternion(
		    a.multiply(e)
			.subtract(b.multiply(f))
			.subtract(c.multiply(g))
			.subtract(d.multiply(h)),
		    b.multiply(e)
			.add(a.multiply(f))
			.add(c.multiply(h))
			.subtract(d.multiply(g)),
		    a.multiply(g)
			.subtract(b.multiply(h))
			.add(c.multiply(e))
			.add(d.multiply(f)),
		    a.multiply(h)
			.add(b.multiply(g))
			.subtract(c.multiply(f))
			.add(d.multiply(e)));
	    }
	    else {
		BigDecimal a = a();
		BigDecimal b = b();
		BigDecimal c = c();
		BigDecimal d = d();
		BigDecimal e = q.a();
		BigDecimal f = q.b();
		BigDecimal g = q.c();
		BigDecimal h = q.d();

		return new Quaternion(
		  MathUtil.fixup(
		    a.multiply(e, mc)
			.subtract(b.multiply(f, mc))
			.subtract(c.multiply(g, mc))
			.subtract(d.multiply(h, mc)), mc),
		  MathUtil.fixup(
		    b.multiply(e, mc)
			.add(a.multiply(f, mc))
			.add(c.multiply(h, mc))
			.subtract(d.multiply(g, mc)), mc),
		  MathUtil.fixup(
		    a.multiply(g, mc)
			.subtract(b.multiply(h, mc))
			.add(c.multiply(e, mc))
			.add(d.multiply(f, mc)), mc),
		  MathUtil.fixup(
		    a.multiply(h, mc)
			.add(b.multiply(g, mc))
			.subtract(c.multiply(f, mc))
			.add(d.multiply(e, mc)), mc));
	    }
	}

	/**
	 * Compute the conjugate value ({@code q'}) of this quaternion, which is
	 * {@code a - b*i - c*j - d*k}.
	 *
	 * @return This quaternion's conjugate value.
	 */
	public Quaternion conjugate() {
	    if (rational)
		return new Quaternion(aFrac(), bFrac().negate(), cFrac().negate(), dFrac().negate());
	    else
		return new Quaternion(a(), b().negate(), c().negate(), d().negate());
	}

	/**
	 * The magnitude of this rational quaternion, squared.
	 *
	 * @return {@code a*a + b*b + c*c + d*d}.
	 */
	private BigFraction magSquareFrac() {
	    BigFraction a = aFrac();
	    BigFraction b = bFrac();
	    BigFraction c = cFrac();
	    BigFraction d = dFrac();

	    return a.multiply(a)
		    .add(b.multiply(b))
		    .add(c.multiply(c))
		    .add(d.multiply(d));
	}

	/**
	 * The magnitude of this decimal quaternion, squared.
	 *
	 * @return {@code a*a + b*b + c*c + d*d}.
	 */
	private BigDecimal magSquare() {
	    BigDecimal a = a();
	    BigDecimal b = b();
	    BigDecimal c = c();
	    BigDecimal d = d();

	    return a.multiply(a)
		    .add(b.multiply(b))
		    .add(c.multiply(c))
		    .add(d.multiply(d));
	}

	/**
	 * The magnitude of this quaternion, which is {@code sqrt(a*a + b*b + c*c + d*d)}.
	 *
	 * @param mc The precision and rounding used to compute the value.
	 * @return The decimal magnitude, regardless of whether this quaternion is stored
	 * as a fraction or not.
	 */
	public BigDecimal magnitude(final MathContext mc) {
	    BigDecimal magSquare = rational ? magSquareFrac().toDecimal(mc) : magSquare();

	    return MathUtil.sqrt(magSquare, mc);
	}

	/**
	 * Compute the inverse value of this quaternion, which is {@code q'/(q*q')},
	 * where {@code q'} is the conjugate of {@code q}.
	 *
	 * @param mc The rounding context for decimal values.
	 * @return {@code 1/q} as a new value.
	 */
	public Quaternion inverse(final MathContext mc) {
	    if (rational) {
		BigFraction magSquare = magSquareFrac();

		return new Quaternion(
			aFrac().divide(magSquare),
			bFrac().divide(magSquare).negate(),
			cFrac().divide(magSquare).negate(),
			dFrac().divide(magSquare).negate());
	    }
	    else {
		BigDecimal magSquare = magSquare();

		return new Quaternion(
			MathUtil.fixup(a().divide(magSquare, mc), mc),
			MathUtil.fixup(b().divide(magSquare, mc).negate(), mc),
			MathUtil.fixup(c().divide(magSquare, mc).negate(), mc),
			MathUtil.fixup(d().divide(magSquare, mc).negate(), mc));
	    }
	}

	/**
	 * Divide this quaternion by another, which is computed as {@code this*(1/q)}.
	 *
	 * @param q  The other to divide by.
	 * @param mc Rounding context to use for decimal arithmetic (non-rational case).
	 * @return   The result of {@code this/q} rounded appropriately.
	 */
	public Quaternion divide(final Quaternion q, final MathContext mc) {
	    return multiply(q.inverse(mc), mc);
	}

	/**
	 * Take this quaternion to an integer power.
	 *
	 * @param n  The integer power to raise this quaternion to.
	 * @param mc Rounding context for the non-rational case.
	 * @return   Result of the exponentiation.
	 */
	public Quaternion power(final int n, final MathContext mc) {
	    Quaternion result = ONE;
	    Quaternion mult = this;
	    int scale = n;

	    while (scale > 0) {
		if ((scale & 1) == 1) {
		    result = result.multiply(mult, mc);
		}
		mult = mult.multiply(mult, mc);
		scale >>= 1;
	    }

	    return result;
	}


	/**
	 * The precision of this quaternion, which is the maximum precision of all parts.
	 *
	 * @return Aggregate precision of this value.
	 */
	public int precision() {
	    return rational ?
		MathUtil.maximum(
			aFrac().precision(),
			bFrac().precision(),
			cFrac().precision(),
			dFrac().precision()) :
		MathUtil.maximum(
			a().precision(),
			b().precision(),
			c().precision(),
			d().precision());
	}


	public BigDecimal a() {
	    if (rational)
		return aFrac == null ? BigDecimal.ZERO : aFrac.toDecimal();
	    else
		return a == null ? BigDecimal.ZERO : a;
	}

	public BigDecimal b() {
	    if (rational)
		return bFrac == null ? BigDecimal.ZERO : bFrac.toDecimal();
	    else
		return b == null ? BigDecimal.ZERO : b;
	}

	public BigDecimal c() {
	    if (rational)
		return cFrac == null ? BigDecimal.ZERO : cFrac.toDecimal();
	    else
		return c == null ? BigDecimal.ZERO : c;
	}

	public BigDecimal d() {
	    if (rational)
		return dFrac == null ? BigDecimal.ZERO : dFrac.toDecimal();
	    else
		return d == null ? BigDecimal.ZERO : d;
	}

	public BigFraction aFrac() {
	    if (rational)
		return aFrac == null ? ComplexNumber.F_ZERO: aFrac;
	    else
		return a == null ? ComplexNumber.F_ZERO : BigFraction.valueOf(a);
	}

	public BigFraction bFrac() {
	    if (rational)
		return bFrac == null ? ComplexNumber.F_ZERO: bFrac;
	    else
		return b == null ? ComplexNumber.F_ZERO : BigFraction.valueOf(b);
	}

	public BigFraction cFrac() {
	    if (rational)
		return cFrac == null ? ComplexNumber.F_ZERO: cFrac;
	    else
		return c == null ? ComplexNumber.F_ZERO : BigFraction.valueOf(c);
	}

	public BigFraction dFrac() {
	    if (rational)
		return dFrac == null ? ComplexNumber.F_ZERO: dFrac;
	    else
		return d == null ? ComplexNumber.F_ZERO : BigFraction.valueOf(d);
	}

	@Override
	public double doubleValue() {
	    if (a != null)
		return a.doubleValue();
	    else if (aFrac != null)
		return aFrac.doubleValue();
	    return 0.0d;
	}

	@Override
	public float floatValue() {
	    if (a != null)
		return a.floatValue();
	    else if (aFrac != null)
		return aFrac.floatValue();
	    return 0.0f;
	}

	@Override
	public long longValue() {
	    if (a != null)
		return a.longValue();
	    else if (aFrac != null)
		return aFrac.longValue();
	    return 0L;
	}

	@Override
	public int intValue() {
	    if (a != null)
		return a.intValue();
	    else if (aFrac != null)
		return aFrac.intValue();
	    return 0;
	}

	@Override
	public short shortValue() {
	    if (a != null)
		return a.shortValue();
	    else if (aFrac != null)
		return aFrac.shortValue();
	    return 0;
	}

	@Override
	public byte byteValue() {
	    if (a != null)
		return a.byteValue();
	    else if (aFrac != null)
		return aFrac.byteValue();
	    return 0;
	}

	@Override
	public int compareTo(final Quaternion o) {
	    if (rational && o.rational) {
		BigFraction mag = magSquareFrac();
		BigFraction mag2 = o.magSquareFrac();
		return mag.compareTo(mag2);
	    }
	    else {
		// Presumably since this comparison doesn't make any sense anyway
		// using an arbitrary precision here won't make a real difference
		BigDecimal mag = magnitude(MathContext.DECIMAL128);
		BigDecimal mag2 = o.magnitude(MathContext.DECIMAL128);
		// Magnitude alone says nothing about direction
		return mag.compareTo(mag2);
	    }
	}

	@Override
	public boolean equals(final Object o) {
	    if (!(o instanceof Quaternion))
		return false;

	    Quaternion q = (Quaternion) o;

	    if (rational != q.rational)
		return false;

	    if (rational) {
		return aFrac().equals(q.aFrac()) &&
		       bFrac().equals(q.bFrac()) &&
		       cFrac().equals(q.cFrac()) &&
		       dFrac().equals(q.dFrac());
	    }
	    else {
		return a().equals(q.a()) &&
		       b().equals(q.b()) &&
		       c().equals(q.c()) &&
		       d().equals(q.d());
	    }
	}

	@Override
	public int hashCode() {
	    if (rational)
		return aFrac().hashCode() ^ bFrac().hashCode() ^ cFrac().hashCode() ^ dFrac().hashCode();
	    else
		return a().hashCode() ^ b().hashCode() ^ c().hashCode() ^ d().hashCode();
	}

	/**
	 * Format the value using optional thousands separators.
	 *
	 * @param sep Whether to use separators in each value.
	 * @return    The formatted value.
	 * @see #NORMAL_FORMAT
	 */
	private String internalToString(final boolean sep) {
	    if (rational) {
		return String.format(NORMAL_FORMAT,
			aFrac().toFormatString(sep),
			bFrac().toFormatString(sep),
			cFrac().toFormatString(sep),
			dFrac().toFormatString(sep));
	    }
	    else {
		return String.format(NORMAL_FORMAT,
			Num.formatWithSeparators(a(), sep),
			Num.formatWithSeparators(b(), sep),
			Num.formatWithSeparators(c(), sep),
			Num.formatWithSeparators(d(), sep));
	    }
	}

	/**
	 * Format the value for display, with the option to use thousands separators.
	 *
	 * @param sep Whether to use thousands separators.
	 * @return    The properly formatted value.
	 */
	public String toFormatString(final boolean sep) {
	    return internalToString(sep);
	}

	@Override
	public String toString() {
	    return internalToString(false);
	}

}

