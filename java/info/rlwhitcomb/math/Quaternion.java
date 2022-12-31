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
 *	Quaternion support.
 *
 * History:
 *  29-Dec-22 rlw #558:	Initial coding.
 */
package info.rlwhitcomb.math;

import java.math.BigDecimal;


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
{
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
	public String toString() {
	    if (rational) {
		return String.format("( %1$s, %2$s, %3$s, %4$s )",
			aFrac(), bFrac(), cFrac(), dFrac());
	    }
	    else {
		return String.format("( %1$s, %2$s, %3$s, %4$s )",
			a().toPlainString(),
			b().toPlainString(),
			c().toPlainString(),
			d().toPlainString());
	    }
	}

}

