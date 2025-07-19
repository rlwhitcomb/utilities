/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2025 Roger L. Whitcomb.
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
 *  29-Dec-22 rlw #558	Initial coding.
 *  05-Jan-23 rlw #558	More operation methods.
 *  10-Jan-23		Constructor from long (int) values.
 *  04-Feb-23		"power(int)" function.
 *  23-Feb-23 rlw #244	Formatting with thousands separators.
 *			"conjugate" to "inverse", implement "conjugate" correctly,
 *			Implement "magnitude(), "equals()", "hashCode()", and
 *			Comparable interface (same paradigm as ComplexNumber).
 *  05-May-23 rlw #558	New "negate()" method. Move F_ZERO into BigFraction.
 *  30-Jan-24 rlw #649	Options for extra spacing in fractional form.
 *  14-May-24 rlw #674	New methods to determine if this is real, imaginary, or complex,
 *			and then better conversions to real and complex.
 *  16-May-24 rlw ----	Add "toBigIntegerExact()" method.
 *  15-Jan-25 rlw ----	Rename "normalize" to "internalize" and make a real "normal"
 *			method; add "signum", "ceil", and "floor" methods; new constructor
 *			from BigInteger values.
 *  14-Mar-25 rlw #710	Add "intValueExact()"; code cleanup.
 *  13-Apr-25 rlw #702	New "idivide" and "remainder" methods.
 *  01-May-25 rlw #716	Massive refactoring.
 *  03-May-25 rlw #716	Refactor "ceil" and "floor".
 *		  #702	Fix "modulus".
 *  11-May-25 rlw #702	Ooops! "modulus" still needed work.
 *  24-May-25 rlw #721	Add R_ONE constant and "increment()" methods.
 *  12-Jul-25 rlw #740	Add "dot" (product) method.
 */
package info.rlwhitcomb.math;

import info.rlwhitcomb.util.Intl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;


/**
 * Implementation of a quaternion.
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
public abstract class Quaternion extends Number implements Comparable<Quaternion>
{
	/** A value of a real zero, as a quaternion. */
	public static final Quaternion ZERO = Quaternion.zero();

	/** A value of a real one, as a quaternion. */
	public static final Quaternion ONE = Quaternion.real(1);

	/** A value of a rational one, as a quaternion. */
	public static final Quaternion R_ONE = Quaternion.rational(BigFraction.ONE);


	/** Normal format for display. */
	protected static final String NORMAL_FORMAT = "( %1$s, %2$s, %3$s, %4$s )";


	/**
	 * @return Whether or not this is a rational (composed of {@link BigFraction} values) quaternion?
	 */
	public abstract boolean isRational();


	/**
	 * Construct with a decimal value of {@code (0, 0, 0, 0)}.
	 */
	public static Quaternion zero() {
	    return real(0);
	}

	/**
	 * Construct given all four coefficients (as "decimal", that is {@link BigDecimal}, values).
	 *
	 * @param aVal The first term coefficient.
	 * @param bVal Second coefficient (i).
	 * @param cVal Third term (j) coefficient.
	 * @param dVal Fourth coefficient (k).
	 */
	public static Quaternion decimal(final BigDecimal aVal, final BigDecimal bVal, final BigDecimal cVal, final BigDecimal dVal) {
	    return new DecimalQuaternion(aVal, bVal, cVal, dVal);
	}

	/**
	 * Construct given integer values for the coefficients, but make it a "decimal" quaternion.
	 *
	 * @param aInt The first term coefficient.
	 * @param bInt Second coefficient (i).
	 * @param cInt Third term (j) coefficient.
	 * @param dInt Fourth coefficient (k).
	 */
	public static Quaternion decimal(final BigInteger aInt, final BigInteger bInt, final BigInteger cInt, final BigInteger dInt) {
	    return new DecimalQuaternion(aInt, bInt, cInt, dInt);
	}

	/**
	 * Construct given all four coefficients (as fractions, that is {@link BigFraction}, values).
	 *
	 * @param aF The first term coefficient.
	 * @param bF Second coefficient (i).
	 * @param cF Third term (j) coefficient.
	 * @param dF Fourth coefficient (k).
	 */
	public static Quaternion rational(final BigFraction aF, final BigFraction bF, final BigFraction cF, final BigFraction dF) {
	    return new RationalQuaternion(aF, bF, cF, dF);
	}

	/**
	 * Construct given all four coefficients (as "decimal", that is {@code long}, values).
	 *
	 * @param aVal The first term coefficient.
	 * @param bVal Second coefficient (i).
	 * @param cVal Third term (j) coefficient.
	 * @param dVal Fourth coefficient (k).
	 */
	public static Quaternion decimal(final long aVal, final long bVal, final long cVal, final long dVal) {
	    return new DecimalQuaternion(aVal, bVal, cVal, dVal);
	}


	/**
	 * Construct a real (decimal) quaternion, with the given value.
	 *
	 * @param r The real value (all other parts will be zero).
	 */
	public static Quaternion real(final long r) {
	    return decimal(new BigDecimal(r), null, null, null);
	}

	/**
	 * Construct a real (decimal) quaternion, with the given value.
	 *
	 * @param r The real value (all other parts will be zero).
	 */
	public static Quaternion real(final BigInteger r) {
	    return decimal(new BigDecimal(r), null, null, null);
	}

	/**
	 * Construct a real (decimal) quaternion, with the given value.
	 *
	 * @param r The real value (all other parts will be zero).
	 */
	public static Quaternion real(final BigDecimal r) {
	    return decimal(r, null, null, null);
	}

	/**
	 * Construct a real (rational) quaternion with the given value.
	 *
	 * @param rFrac The real fractional value (all other parts will be zero).
	 */
	public static Quaternion rational(final BigFraction rFrac) {
	    return rational(rFrac, null, null, null);
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
		return Quaternion.real((BigDecimal) value);
	    if (value instanceof BigInteger)
		return Quaternion.real((BigInteger) value);
	    if (value instanceof BigFraction)
		return Quaternion.rational((BigFraction) value);
	    if (value instanceof ComplexNumber) {
		ComplexNumber cn = (ComplexNumber) value;
		if (cn.isRational())
		    return new RationalQuaternion(cn.rFrac(), cn.iFrac(), null, null);
		else
		    return new DecimalQuaternion(cn.r(), cn.i(), null, null);
	    }

// TODO: more to do, expecially String parsing
	    return null;
	}


	/**
	 * Is this quaternion uniquely the value zero?
	 *
	 * @return {@code true} if all parts are zero.
	 */
	public abstract boolean isZero();

	/**
	 * Is this a pure real number (that is, the "b", "c", and "d" values are all zero)?
	 * <p> Note: "a" could be zero as well, which means the value is zero, which is still
	 * a pure real value.
	 *
	 * @return {@code true} if this quaternion is a pure real value.
	 */
	public abstract boolean isPureReal();

	/**
	 * Is this is a pure imaginary number (that would be "a", "c", and "d" are all zero,
	 * and "b" is non-zero)?
	 *
	 * @return {@code true} if this quaternion is a pure imaginary value.
	 */
	public abstract boolean isPureImaginary();

	/**
	 * Is this a pure complex number (that is, both "c" and "d" are zero)?
	 * <p> Note: this will also return {@code true} if either {@link #isPureReal} or
	 * {@link #isPureImaginary} return {@code true}, so those conditions should be
	 * checked first.
	 *
	 * @return {@code true} if this quaternion is actually just a complex value.
	 */
	public abstract boolean isPureComplex();

	/**
	 * Convert this quaternion to a real value; if it {@link #isPureReal} then return
	 * that value, otherwise compute the real {@link #magnitude} and return that.
	 *
	 * @param mc The rounding context for conversion (if necessary).
	 * @return   The pure real value of this quaternion.
	 */
	public Number toReal(final MathContext mc) {
	    return isPureReal() ? (isRational() ? aFrac() : a()) : magnitude(mc);
	}

	/**
	 * If this quaternion is really just a complex number, then convert to that.
	 *
	 * @return The {@link ComplexNumber} value of this quaternion, if {@link #isPureComplex}
	 * is {@code true}.
	 * @throws IllegalArgumentException if there would be a loss of value.
	 */
	public ComplexNumber toComplex() {
	    if (isPureComplex()) {
		if (isRational())
		    return ComplexNumber.rational(aFrac(), bFrac());
		else
		    return ComplexNumber.decimal(a(), b());
	    }
	    throw new Intl.IllegalArgumentException("math#quaternion.lossOfValue");
	}

	/**
	 * Add the given quaternion to this one.
	 * <p> The operation is done by adding each component separately.
	 *
	 * @param q The other quaternion to add to this one.
	 * @return  A new quaternion that represents the sum of the two.
	 */
	public Quaternion add(final Quaternion q) {
	    if (isRational() && q.isRational())
		return ((RationalQuaternion) this).add(q);
	    else
		return ((DecimalQuaternion) this).add(q);
	}

	/**
	 * Subtract the given quaternion from this one.
	 * <p> The operation is done by subtracting each component separately.
	 *
	 * @param q The other quaternion to subtract from this one.
	 * @return  A new quaternion that represents the difference of the two.
	 */
	public Quaternion subtract(final Quaternion q) {
	    if (isRational() && q.isRational())
		return ((RationalQuaternion) this).subtract(q);
	    else
		return ((DecimalQuaternion) this).subtract(q);
	}

	/**
	 * Negate this quaternion, which is the same as {@link #ZERO} minus this one.
	 *
	 * @return A new quaternion that is the negative of this one.
	 */
	public abstract Quaternion negate();


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
	    if (isRational() && q.isRational())
		return ((RationalQuaternion) this).multiply(q, mc);
	    else
		return ((DecimalQuaternion) this).multiply(q, mc);
	}

	/**
	 * Compute the conjugate value ({@code q'}) of this quaternion, which is
	 * {@code a - b*i - c*j - d*k}.
	 *
	 * @return This quaternion's conjugate value.
	 */
	public abstract Quaternion conjugate();


	/**
	 * The magnitude of this rational quaternion, squared.
	 *
	 * @return {@code a*a + b*b + c*c + d*d}.
	 */
	protected BigFraction magSquareFrac() {
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
	protected BigDecimal magSquare() {
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
	    BigDecimal magSquare = isRational() ? magSquareFrac().toDecimal(mc) : magSquare();

	    return MathUtil.sqrt(magSquare, mc);
	}

	/**
	 * Compute a normalized form of this quaternion, which has a magnitude of one,
	 * but the same direction as this quaternion.
	 * <p> Note: even if the original was in fractional form, the returned value
	 * will be in decimal form, in order for the new magnitude to be as close to
	 * one as the precision allows.
	 *
	 * @param mc  The rounding value for computing the magnitude (for division).
	 * @return    Original value with coefficients divided by the magnitude, to
	 *            make the new magnitude equal to one.
	 */
	public Quaternion normal(final MathContext mc) {
	    BigDecimal mag = magnitude(mc);

	    return Quaternion.decimal(
		a().divide(mag, mc),
		b().divide(mag, mc),
		c().divide(mag, mc),
		d().divide(mag, mc));
	}

	/**
	 * Return the signum value of this quaternion, which is the original if the value
	 * is zero, and the normalized value (magnitude one) if the value is non-zero.
	 *
	 * @param mc MathContext for use in normalizing the value if needed.
	 * @return   The original value if zero, or the normalized value if not.
	 */
	public Quaternion signum(final MathContext mc) {
	    return isZero() ? this : normal(mc);
	}

	/**
	 * Compute the inverse value of this quaternion, which is {@code q'/(q*q')},
	 * where {@code q'} is the conjugate of {@code q}.
	 *
	 * @param mc The rounding context for decimal values.
	 * @return {@code 1/q} as a new value.
	 */
	public abstract Quaternion inverse(final MathContext mc);

	/**
	 * Compute the dot product of this quaternion and the other. This is a scalar value.
	 *
	 * @param other The other quaternion to dot with.
	 * @param mc    Rounding precision for the result (in the "decimal" case).
	 * @return      Dot product of this · other.
	 */
	public abstract Number dot(final Quaternion other, final MathContext mc);

	/**
	 * Return a new quaternion consisting of the "ceil" value of each component.
	 *
	 * @return The "ceil" value of this quaternion.
	 */
	public abstract Quaternion ceil();

	/**
	 * Return a new quaternion consisting of the "floor" value of each component.
	 *
	 * @return The "floor" value of this quaternion.
	 */
	public abstract Quaternion floor();

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
	 * Do an "integer" division of this number by the given one. This is the "\" operator.
	 *
	 * @param other The number to divide by.
	 * @param mc    Rounding precision to use for the division.
	 * @return      This divided by other, set to the nearest integer of that result.
	 */
	public abstract Quaternion idivide(final Quaternion other, final MathContext mc);

	/**
	 * Get the remainder after division, which is {@code q1 - (q1\q2 * q2)}.
	 *
	 * @param other Number to divide by.
	 * @param mc    Rounding precision to use for the decimal results.
	 * @return      Result of {@code this % other}.
	 */
	public Quaternion remainder(final Quaternion other, final MathContext mc) {
	    Quaternion quotient = idivide(other, mc);
	    return subtract(quotient.multiply(other, mc));
	}

	/**
	 * Get the modulus of this divided by the given one; differs from the remainder for
	 * negative values.
	 *
	 * @param other Number to divide by.
	 * @param mc    Rounding precision to use for the result.
	 * @return      Result of {@code this mod other}.
	 */
	public Quaternion modulus(final Quaternion other, final MathContext mc) {
	    return subtract(other.multiply(divide(other, mc).floor(), mc));
	}

	/**
	 * Increment the value of this quaternion.
	 *
	 * @return One bigger.
	 */
	public abstract Quaternion increment();

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
	public abstract int precision();


	/**
	 * Return one part of this value (index operation).
	 *
	 * @param index An index from 0 .. 3 for one part of this value.
	 * @return      This given part (either rational or not).
	 * @throws      IllegalArgumentException if the index is out of range.
	 */
	public abstract Number part(final int index);

	/**
	 * Set one part of this value (index operation).
	 *
	 * @param index An index from 0 .. 3 for one part of this value.
	 * @param value New value for the specified part.
	 * @return      New quaternion value with the given part updated.
	 * @throws      IllegalArgumentException if the index is out of range.
	 */
	public abstract Quaternion setPart(final int index, final Object value);


	/**
	 * Convert to an exact integer representation, if possible.
	 *
	 * @return The {@link BigInteger} representation of this value, if it is pure real,
	 *         and has no fractional part.
	 * @throws ArithmeticException otherwise.
	 */
	public BigInteger toBigIntegerExact() {
	    if (isPureReal()) {
		return isRational() ? BigFraction.getInteger(aFrac()) : a().toBigIntegerExact();
	    }
	    throw new Intl.ArithmeticException("math#complex.imaginaryInt");
	}


	/**
	 * Convert to an exact integer representation, if possible.
	 *
	 * @return The {@code int} representation of this value, if it is pure real,
	 *         and has no fractional part.
	 * @throws ArithmeticException otherwise.
	 */
	public int intValueExact() {
	    if (isPureReal()) {
		return isRational() ? aFrac().intValueExact() : a().intValueExact();
	    }
	    throw new Intl.ArithmeticException("math#complex.imaginaryInt");
	}


	public abstract BigDecimal a();

	public abstract BigDecimal b();

	public abstract BigDecimal c();

	public abstract BigDecimal d();

	public abstract BigFraction aFrac();

	public abstract BigFraction bFrac();

	public abstract BigFraction cFrac();

	public abstract BigFraction dFrac();

	@Override
	public boolean equals(final Object o) {
	    if (!(o instanceof Quaternion))
		return false;

	    Quaternion q = (Quaternion) o;

	    if (isRational() != q.isRational())
		return false;

	    if (isRational()) {
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
	public int compareTo(final Quaternion o) {
	    if (isRational() && o.isRational()) {
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

	/**
	 * Format the value using optional thousands separators.
	 *
	 * @param sep   Whether to use separators in each value.
	 * @param space Whether to put extra space in the fractional form.
	 * @return      The formatted value.
	 * @see #NORMAL_FORMAT
	 */
	protected abstract String internalToString(final boolean sep, final boolean space);

	/**
	 * Format the value for display, with the option to use thousands separators.
	 *
	 * @param sep   Whether to use thousands separators.
	 * @param space Whether to use extra spaces in fractional form.
	 * @return      The properly formatted value.
	 */
	public String toFormatString(final boolean sep, final boolean space) {
	    return internalToString(sep, space);
	}

	@Override
	public String toString() {
	    return internalToString(false, true);
	}

}

/**
 * A rational quaternion, whose coordinates are {@link BigFraction} values and all calculations
 * are done as exact fractions.
 */
class RationalQuaternion extends Quaternion
{
	/** First term, the real part, as a fraction. */
	private BigFraction aFrac;

	/** Second term, coefficient of <code><b>i</b></code>, as a fraction. */
	private BigFraction bFrac;

	/** Third term, coefficient of <code><b>j</b></code>, as a fraction. */
	private BigFraction cFrac;

	/** Fourth term, coefficient of <code><b>k</b></code>, as a fraction. */
	private BigFraction dFrac;


	@Override
	public boolean isRational() {
	    return true;
	}

	/**
	 * Construct given all four coefficients (as fractions, that is {@link BigFraction}, values).
	 *
	 * @param aF The first term coefficient.
	 * @param bF Second coefficient (i).
	 * @param cF Third term (j) coefficient.
	 * @param dF Fourth coefficient (k).
	 */
	public RationalQuaternion(final BigFraction aF, final BigFraction bF, final BigFraction cF, final BigFraction dF) {
	    aFrac = aF == null ? null : BigFraction.properFraction(aF);
	    bFrac = bF == null ? null : BigFraction.properFraction(bF);
	    cFrac = cF == null ? null : BigFraction.properFraction(cF);
	    dFrac = dF == null ? null : BigFraction.properFraction(dF);
	    internalize();
	}

	@Override
	public boolean isZero() {
	    // Assumes already internalized
	    return (aFrac != null && aFrac.equals(BigFraction.ZERO)) && bFrac == null && cFrac == null && dFrac == null;
	}

	@Override
	public boolean isPureReal() {
	    return bFrac == null && cFrac == null && dFrac == null;
	}

	@Override
	public boolean isPureImaginary() {
	    return aFrac == null && bFrac != null && cFrac == null && dFrac == null;
	}

	@Override
	public boolean isPureComplex() {
	    return (aFrac != null || bFrac != null) && cFrac == null && dFrac == null;
	}

	/**
	 * Maintain this value in its "internalized" form, that is, keeping zero (unused) terms as {@code null} instead.
	 */
	private void internalize() {
	    if (aFrac != null && aFrac.equals(BigFraction.ZERO))
		aFrac = null;
	    if (bFrac != null && bFrac.equals(BigFraction.ZERO))
		bFrac = null;
	    if (cFrac != null && cFrac.equals(BigFraction.ZERO))
		cFrac = null;
	    if (dFrac != null && dFrac.equals(BigFraction.ZERO))
		dFrac = null;

	    if (aFrac == null && bFrac == null && cFrac == null && dFrac == null)
		aFrac = BigFraction.ZERO;
	}

	public Quaternion add(final Quaternion q) {
	    return new RationalQuaternion(
			aFrac().add(q.aFrac()),
			bFrac().add(q.bFrac()),
			cFrac().add(q.cFrac()),
			dFrac().add(q.dFrac()));
	}

	public Quaternion subtract(final Quaternion q) {
	    return new RationalQuaternion(
			aFrac().subtract(q.aFrac()),
			bFrac().subtract(q.bFrac()),
			cFrac().subtract(q.cFrac()),
			dFrac().subtract(q.dFrac()));
	}

	@Override
	public Quaternion negate() {
	    return new RationalQuaternion(aFrac().negate(), bFrac().negate(), cFrac().negate(), dFrac().negate());
	}

	@Override
	public Quaternion conjugate() {
	    return new RationalQuaternion(aFrac(), bFrac().negate(), cFrac().negate(), dFrac().negate());
	}

	public Quaternion multiply(final Quaternion q, final MathContext mc) {
	    BigFraction a = aFrac();
	    BigFraction b = bFrac();
	    BigFraction c = cFrac();
	    BigFraction d = dFrac();
	    BigFraction e = q.aFrac();
	    BigFraction f = q.bFrac();
	    BigFraction g = q.cFrac();
	    BigFraction h = q.dFrac();

	    return new RationalQuaternion(
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

	@Override
	public Quaternion inverse(final MathContext mc) {
	    BigFraction magSquare = magSquareFrac();

	    return new RationalQuaternion(
			aFrac().divide(magSquare),
			bFrac().divide(magSquare).negate(),
			cFrac().divide(magSquare).negate(),
			dFrac().divide(magSquare).negate());
	}

	@Override
	public Number dot(final Quaternion other, final MathContext mc) {
	    BigFraction x1 = aFrac();
	    BigFraction y1 = bFrac();
	    BigFraction z1 = cFrac();
	    BigFraction w1 = dFrac();
	    BigFraction x2 = other.aFrac();
	    BigFraction y2 = other.bFrac();
	    BigFraction z2 = other.cFrac();
	    BigFraction w2 = other.dFrac();

	    return x1.multiply(x2).add(y1.multiply(y2)).add(z1.multiply(z2)).add(w1.multiply(w2));
	}

	@Override
	public Quaternion idivide(final Quaternion other, final MathContext mc) {
	    Quaternion fullResult = divide(other, mc);

	    return new RationalQuaternion(fullResult.aFrac().toNearestInteger(),
					  fullResult.bFrac().toNearestInteger(),
					  fullResult.cFrac().toNearestInteger(),
					  fullResult.dFrac().toNearestInteger());
	}

	@Override
	public Quaternion increment() {
	    return add(R_ONE);
	}

	@Override
	public Quaternion ceil() {
	    return new RationalQuaternion(
		aFrac().ceil(),
		bFrac().ceil(),
		cFrac().ceil(),
		dFrac().ceil());
	}

	@Override
	public Quaternion floor() {
	    return new RationalQuaternion(
		aFrac().floor(),
		bFrac().floor(),
		cFrac().floor(),
		dFrac().floor());
	}

	@Override
	public int precision() {
	    return MathUtil.maximum(
			aFrac().precision(),
			bFrac().precision(),
			cFrac().precision(),
			dFrac().precision());
	}

	@Override
	public Number part(final int index) {
	    switch (index) {
		case 0: return aFrac();
		case 1: return bFrac();
		case 2: return cFrac();
		case 3: return dFrac();
		default:
		    throw new Intl.IllegalArgumentException("math#quaternion.badIndex", index);
	    }
	}

	@Override
	public Quaternion setPart(final int index, final Object value) {
	    BigFraction part = BigFraction.valueOf(value);

	    switch (index) {
		case 0:
		    return new RationalQuaternion(part, bFrac(), cFrac(), dFrac());
		case 1:
		    return new RationalQuaternion(aFrac(), part, cFrac(), dFrac());
		case 2:
		    return new RationalQuaternion(aFrac(), bFrac(), part, dFrac());
		case 3:
		    return new RationalQuaternion(aFrac(), bFrac(), cFrac(), part);
		default:
		    throw new Intl.IllegalArgumentException("math#quaternion.badIndex", index);
	    }
	}

	@Override
	public BigDecimal a() {
	    return aFrac == null ? BigDecimal.ZERO : aFrac.toDecimal();
	}

	@Override
	public BigDecimal b() {
	    return bFrac == null ? BigDecimal.ZERO : bFrac.toDecimal();
	}

	@Override
	public BigDecimal c() {
	    return cFrac == null ? BigDecimal.ZERO : cFrac.toDecimal();
	}

	@Override
	public BigDecimal d() {
	    return dFrac == null ? BigDecimal.ZERO : dFrac.toDecimal();
	}

	@Override
	public BigFraction aFrac() {
	    return aFrac == null ? BigFraction.F_ZERO: aFrac;
	}

	@Override
	public BigFraction bFrac() {
	    return bFrac == null ? BigFraction.F_ZERO: bFrac;
	}

	@Override
	public BigFraction cFrac() {
	    return cFrac == null ? BigFraction.F_ZERO: cFrac;
	}

	@Override
	public BigFraction dFrac() {
	    return dFrac == null ? BigFraction.F_ZERO: dFrac;
	}

	@Override
	public double doubleValue() {
	    if (aFrac != null)
		return aFrac.doubleValue();
	    return 0.0d;
	}

	@Override
	public float floatValue() {
	    if (aFrac != null)
		return aFrac.floatValue();
	    return 0.0f;
	}

	@Override
	public long longValue() {
	    if (aFrac != null)
		return aFrac.longValue();
	    return 0L;
	}

	@Override
	public int intValue() {
	    if (aFrac != null)
		return aFrac.intValue();
	    return 0;
	}

	@Override
	public short shortValue() {
	    if (aFrac != null)
		return aFrac.shortValue();
	    return 0;
	}

	@Override
	public byte byteValue() {
	    if (aFrac != null)
		return aFrac.byteValue();
	    return 0;
	}

	@Override
	protected String internalToString(final boolean sep, final boolean space) {
	    return String.format(NORMAL_FORMAT,
			aFrac().toFormatString(sep, space),
			bFrac().toFormatString(sep, space),
			cFrac().toFormatString(sep, space),
			dFrac().toFormatString(sep, space));
	}

	@Override
	public int hashCode() {
	    return aFrac().hashCode() ^ bFrac().hashCode() ^ cFrac().hashCode() ^ dFrac().hashCode();
	}

}

/**
 * A decimal quaternion, whose coordinates are {@link BigDecimal} values and all calculations
 * are done to an appropriate precision.
 */
class DecimalQuaternion extends Quaternion
{
	/** First term, the real part. */
	private BigDecimal a;

	/** Second term, coefficient of <code><b>i</b></code>. */
	private BigDecimal b;

	/** Third term, coefficient of <code><b>j</b></code>. */
	private BigDecimal c;

	/** Fourth term, coefficient of <code><b>k</b></code>. */
	private BigDecimal d;


	@Override
	public boolean isRational() {
	    return false;
	}

	public DecimalQuaternion(final BigDecimal aVal, final BigDecimal bVal, final BigDecimal cVal, final BigDecimal dVal) {
	    a = aVal;
	    b = bVal;
	    c = cVal;
	    d = dVal;

	    internalize();
	}

	/**
	 * Construct given integer values for the coefficients, but make it a "real" quaternion.
	 *
	 * @param aInt The first term coefficient.
	 * @param bInt Second coefficient (i).
	 * @param cInt Third term (j) coefficient.
	 * @param dInt Fourth coefficient (k).
	 */
	public DecimalQuaternion(final BigInteger aInt, final BigInteger bInt, final BigInteger cInt, final BigInteger dInt) {
	    a = aInt == null ? null : new BigDecimal(aInt);
	    b = bInt == null ? null : new BigDecimal(bInt);
	    c = cInt == null ? null : new BigDecimal(cInt);
	    d = dInt == null ? null : new BigDecimal(dInt);

	    internalize();
	}

	/**
	 * Construct given all four coefficients (as "decimal", that is {@code long}, values).
	 *
	 * @param aVal The first term coefficient.
	 * @param bVal Second coefficient (i).
	 * @param cVal Third term (j) coefficient.
	 * @param dVal Fourth coefficient (k).
	 */
	public DecimalQuaternion(final long aVal, final long bVal, final long cVal, final long dVal) {
	    a = new BigDecimal(aVal);
	    b = new BigDecimal(bVal);
	    c = new BigDecimal(cVal);
	    d = new BigDecimal(dVal);

	    internalize();
	}

	@Override
	public boolean isZero() {
	    // Assumes already internalized
	    return (a != null && a.equals(BigDecimal.ZERO)) && b == null && c == null && d == null;
	}

	@Override
	public boolean isPureReal() {
	    return b == null && c == null && d == null;
	}

	@Override
	public boolean isPureImaginary() {
	    return a == null && b != null && c == null && d == null;
	}

	@Override
	public boolean isPureComplex() {
	    return (a != null || b != null) && c == null && d == null;
	}

	/**
	 * Maintain this value in its "internalized" form, that is, keeping zero (unused) terms as {@code null} instead.
	 */
	private void internalize() {
	    if (a != null && a.equals(BigDecimal.ZERO))
		a = null;
	    if (b != null && b.equals(BigDecimal.ZERO))
		b = null;
	    if (c != null && c.equals(BigDecimal.ZERO))
		c = null;
	    if (d != null && d.equals(BigDecimal.ZERO))
		d = null;

	    if (a == null && b == null && c == null && d == null)
		a = BigDecimal.ZERO;
	}

	public Quaternion add(final Quaternion q) {
	    return new DecimalQuaternion(
			a().add(q.a()),
			b().add(q.b()),
			c().add(q.c()),
			d().add(q.d()));
	}

	public Quaternion subtract(final Quaternion q) {
	    return new DecimalQuaternion(
			a().subtract(q.a()),
			b().subtract(q.b()),
			c().subtract(q.c()),
			d().subtract(q.d()));
	}

	@Override
	public Quaternion negate() {
	    return new DecimalQuaternion(a().negate(), b().negate(), c().negate(), d().negate());
	}

	@Override
	public Quaternion conjugate() {
	    return new DecimalQuaternion(a(), b().negate(), c().negate(), d().negate());
	}

	public Quaternion multiply(final Quaternion q, final MathContext mc) {
	    BigDecimal a = a();
	    BigDecimal b = b();
	    BigDecimal c = c();
	    BigDecimal d = d();
	    BigDecimal e = q.a();
	    BigDecimal f = q.b();
	    BigDecimal g = q.c();
	    BigDecimal h = q.d();

	    return new DecimalQuaternion(
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

	@Override
	public Quaternion inverse(final MathContext mc) {
	    BigDecimal magSquare = magSquare();

	    return new DecimalQuaternion(
			MathUtil.fixup(a().divide(magSquare, mc), mc),
			MathUtil.fixup(b().divide(magSquare, mc).negate(), mc),
			MathUtil.fixup(c().divide(magSquare, mc).negate(), mc),
			MathUtil.fixup(d().divide(magSquare, mc).negate(), mc));
	}

	@Override
	public Number dot(final Quaternion other, final MathContext mc) {
	    BigDecimal x1 = a();
	    BigDecimal y1 = b();
	    BigDecimal z1 = c();
	    BigDecimal w1 = d();
	    BigDecimal x2 = other.a();
	    BigDecimal y2 = other.b();
	    BigDecimal z2 = other.c();
	    BigDecimal w2 = other.d();

	    // Note: retain all possible precision until the final result
	    return MathUtil.fixup(x1.multiply(x2).add(y1.multiply(y2)).add(z1.multiply(z2)).add(w1.multiply(w2)), mc);
	}

	@Override
	public Quaternion idivide(final Quaternion other, final MathContext mc) {
	    Quaternion fullResult = divide(other, mc);

	    return new DecimalQuaternion(MathUtil.round(fullResult.a(), 0),
					 MathUtil.round(fullResult.b(), 0),
					 MathUtil.round(fullResult.c(), 0),
					 MathUtil.round(fullResult.d(), 0));
	}

	@Override
	public Quaternion increment() {
	    return add(ONE);
	}

	@Override
	public Quaternion ceil() {
	    return new DecimalQuaternion(
		MathUtil.ceil(a()),
		MathUtil.ceil(b()),
		MathUtil.ceil(c()),
		MathUtil.ceil(d()));
	}

	@Override
	public Quaternion floor() {
	    return new DecimalQuaternion(
		MathUtil.floor(a()),
		MathUtil.floor(b()),
		MathUtil.floor(c()),
		MathUtil.floor(d()));
	}

	@Override
	public int precision() {
	    return MathUtil.maximum(
			a().precision(),
			b().precision(),
			c().precision(),
			d().precision());
	}

	@Override
	public Number part(final int index) {
	    switch (index) {
		case 0: return a();
		case 1: return b();
		case 2: return c();
		case 3: return d();
		default:
		    throw new Intl.IllegalArgumentException("math#quaternion.badIndex", index);
	    }
	}

	@Override
	public Quaternion setPart(final int index, final Object value) {
	    BigDecimal part = ComplexNumber.getDecimal(value);

	    switch (index) {
		case 0:
		    return new DecimalQuaternion(part, b(), c(), d());
		case 1:
		    return new DecimalQuaternion(a(), part, c(), d());
		case 2:
		    return new DecimalQuaternion(a(), b(), part, d());
		case 3:
		    return new DecimalQuaternion(a(), b(), c(), part);
		default:
		    throw new Intl.IllegalArgumentException("math#quaternion.badIndex", index);
	    }
	}

	@Override
	public BigDecimal a() {
	    return a == null ? BigDecimal.ZERO : a;
	}

	@Override
	public BigDecimal b() {
	    return b == null ? BigDecimal.ZERO : b;
	}

	@Override
	public BigDecimal c() {
	    return c == null ? BigDecimal.ZERO : c;
	}

	@Override
	public BigDecimal d() {
	    return d == null ? BigDecimal.ZERO : d;
	}

	@Override
	public BigFraction aFrac() {
	    return a == null ? BigFraction.F_ZERO : BigFraction.valueOf(a);
	}

	@Override
	public BigFraction bFrac() {
	    return b == null ? BigFraction.F_ZERO : BigFraction.valueOf(b);
	}

	@Override
	public BigFraction cFrac() {
	    return c == null ? BigFraction.F_ZERO : BigFraction.valueOf(c);
	}

	@Override
	public BigFraction dFrac() {
	    return d == null ? BigFraction.F_ZERO : BigFraction.valueOf(d);
	}

	@Override
	public double doubleValue() {
	    if (a != null)
		return a.doubleValue();
	    return 0.0d;
	}

	@Override
	public float floatValue() {
	    if (a != null)
		return a.floatValue();
	    return 0.0f;
	}

	@Override
	public long longValue() {
	    if (a != null)
		return a.longValue();
	    return 0L;
	}

	@Override
	public int intValue() {
	    if (a != null)
		return a.intValue();
	    return 0;
	}

	@Override
	public short shortValue() {
	    if (a != null)
		return a.shortValue();
	    return 0;
	}

	@Override
	public byte byteValue() {
	    if (a != null)
		return a.byteValue();
	    return 0;
	}

	@Override
	protected String internalToString(final boolean sep, final boolean space) {
	    return String.format(NORMAL_FORMAT,
			Num.formatWithSeparators(a(), sep),
			Num.formatWithSeparators(b(), sep),
			Num.formatWithSeparators(c(), sep),
			Num.formatWithSeparators(d(), sep));
	}

	@Override
	public int hashCode() {
	    return a().hashCode() ^ b().hashCode() ^ c().hashCode() ^ d().hashCode();
	}

}


