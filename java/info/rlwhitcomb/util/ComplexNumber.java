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
 *	31-Jan-2022 (rlwhitcomb)
 *	    #103: Create from List and Map; convert to List and Map.
 *	01-Feb-2022 (rlwhitcomb)
 *	    #103: Powers of integer and real values, negate, another alias,
 *	    "toLongString" method, override "equals" and "hashCode".
 *	    #231: Use new Constants values instead of our own.
 *	05-Feb-2022 (rlwhitcomb)
 *	    Fix Intl keys.
 *	08-Feb-2022 (rlwhitcomb)
 *	    #235: Use MathUtil.atan2 for "theta" to get full precision.
 *	    A lot of refactoring, include support for "polar" form.
 *	18-Feb-2022 (rlwhitcomb)
 *	    Add "signum" method.
 *	14-Apr-2022 (rlwhitcomb)
 *	    #272: Some (mostly) documentation fixes.
 */
package info.rlwhitcomb.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static info.rlwhitcomb.util.Constants.*;


/**
 * Storage of and calculations with complex numbers.
 * <p> Values are stored as {@link BigDecimal} values and kept in a
 * normalized form, which means that pure real and pure imaginary
 * numbers have their other component set to {@code null},
 * which also means that {@code 0} values for either are
 * removed, to reduce the number of special cases in the math.
 * <p> TODO: more math operations.
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
	private static final String I_ALIASES = "[iI\u0131\u0399\u03B9\u2110\u2148]";
	/** Aliases for "radius". */
	private static final String RADIUS_ALIASES = "([rR][aA][dD][iI][uU][sS]|[rR][aA][dD]|[rR])";
	/** Aliases for "theta". */
	private static final String THETA_ALIASES = "([tT][hH][eE][tT][aA]|[aA][nN][gG][lL][eE]|[\u0398]|[\u03B8])";

	/**
	 * The patterns used in {@link #parse} to recognize valid values.
	 */
	private static final Pattern COMPLEX_PATTERNS[] = {
	    Pattern.compile("^\\s*\\(\\s*(" + SIGNED_NUMBER + ")\\s*([,])\\s*(" + SIGNED_NUMBER + ")\\s*\\)\\s*$"),
	    Pattern.compile("^\\s*(" + SIGNED_NUMBER + ")\\s*([,])\\s*(" + SIGNED_NUMBER + ")\\s*$"),
	    Pattern.compile("^\\s*\\(\\s*(" + SIGNED_NUMBER + ")\\s*([+\\-])\\s*(" + NUMBER + ")\\s*" + I_ALIASES + "\\s*\\)\\s*$"),
	    Pattern.compile("^\\s*(" + SIGNED_NUMBER + ")\\s*([+\\-])\\s*(" + NUMBER + ")\\s*" + I_ALIASES + "\\s*$"),
	    Pattern.compile("^\\s*\\{\\s*" + RADIUS_ALIASES + "\\s*[:]\\s*(" + SIGNED_NUMBER + ")\\s*[,]\\s*" + THETA_ALIASES + "\\s*[:]\\s*(" + SIGNED_NUMBER + ")\\s*\\}\\s*$")
	};

	/**
	 * A map key indicating the real part.
	 */
	private static final String REAL_KEY = "r";

	/**
	 * Map key indicating the imaginary part.
	 */
	private static final String IMAG_KEY = "i";

	/**
	 * Map keys indicating the radius part.
	 */
	private static final String RADIUS_KEYS[] = {
	    "r",
	    "rad",
	    "radius"
	};

	/**
	 * Map keys indicating the angle (theta) part.
	 */
	private static final String THETA_KEYS[] = {
	    "\u03B8",
	    "\u0398",
	    "theta",
	    "angle"
	};


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
	 * Construct from a list of one or two values.
	 *
	 * @param list Any list of one or two values.
	 * @return     The new complex number, if possible.
	 * @throws IllegalArgumentException if the number of values is wrong.
	 */
	public static ComplexNumber fromList(List<Object> list) {
	    if (list == null || list.size() == 0)
		throw new Intl.IllegalArgumentException("util#complex.noEmptyListMap");
	    if (list.size() > 2)
		throw new Intl.IllegalArgumentException("util#complex.tooManyValues");

	    if (list.size() == 1)
		return valueOf(list.get(0));

	    BigDecimal r = getDecimal(list.get(0));
	    BigDecimal i = getDecimal(list.get(1));

	    return new ComplexNumber(r, i);
	}

	/**
	 * Construct from a map of one or two values.
	 *
	 * @param map Any map with "r" and/or "i" keys.
	 * @return    The new complex number.
	 * @throws IllegalArgumentException if the input map is null or empty.
	 * @see #REAL_KEY
	 * @see #IMAG_KEY
	 */
	public static ComplexNumber fromMap(Map<String, Object> map) {
	    if (map == null || map.size() == 0)
		throw new Intl.IllegalArgumentException("util#complex.noEmptyListMap");

	    // Here, we could have "r,i" or "r,theta"
	    if (map.containsKey(IMAG_KEY)) {
		BigDecimal r = getDecimal(map.get(REAL_KEY));
		BigDecimal i = getDecimal(map.get(IMAG_KEY));

		return new ComplexNumber(r, i);
	    }
	    else {
		BigDecimal radius = BigDecimal.ZERO;
		BigDecimal theta = BigDecimal.ZERO;

		for (String key : RADIUS_KEYS) {
		    if (map.containsKey(key)) {
			radius = getDecimal(map.get(key));
			break;
		    }
		}
		for (String key : THETA_KEYS) {
		    if (map.containsKey(key)) {
			theta = getDecimal(map.get(key));
			break;
		    }
		}

		return polar(radius, theta, null);
	    }
	}

	/**
	 * Construct from a polar representation (r, theta) by doing the math to convert
	 * to rectangular form: <code>x = r * cos(theta); y = r * sin(theta)</code>.
	 *
	 * @param radius	The "r" value, or distance from the origin.
	 * @param theta		The "theta" value, or angle from positive X-axis.
	 * @param mc		Optional (can be <code>null</code>) rounding context; if omitted the
	 *			value will be calculated from the maximum precision of the input values,
	 *			but at least {@link MathContext#DECIMAL128} precision.
	 * @return		New complex number with the (r, theta) converted to (x, y) (real, imaginary).
	 */
	public static ComplexNumber polar(final BigDecimal radius, final BigDecimal theta, final MathContext mc) {
	    MathContext mc2 = mc;
	    if (mc2 == null) {
		int precision = Math.max(Math.max(radius.precision(), theta.precision()), MathContext.DECIMAL128.getPrecision());
		mc2 = new MathContext(precision);
	    }

	    BigDecimal rPart = radius.multiply(MathUtil.cos(theta, mc2), mc2);
	    BigDecimal iPart = radius.multiply(MathUtil.sin(theta, mc2), mc2);

	    return new ComplexNumber(rPart, iPart);
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
	    // Make sure both parts are not null (zero)
	    if (realPart == null && imaginaryPart == null)
		realPart = BigDecimal.ZERO;
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


	/**
	 * Convert to a list of two values.
	 *
	 * @return A list with the real part and imaginary part, in that order.
	 */
	public List<Object> toList() {
	    List<Object> list = new ArrayList<>(2);

	    list.add(r());
	    list.add(i());

	    return list;
	}

	/**
	 * Convert to a map with real and imaginary keys.
	 *
	 * @return {@code Map} with {@link #REAL_KEY} and {@link #IMAG_KEY} entries.
	 */
	public Map<String, Object> toMap() {
	    Map<String, Object> map = new LinkedHashMap<>(2);

	    map.put(REAL_KEY, r());
	    map.put(IMAG_KEY, i());

	    return map;
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
	 * is {@code atan2(i, r)}.
	 *
	 * @param mc The rounding context to use for the calculation.
	 * @return The angle of this complex number in polar form (in radians).
	 */
	public BigDecimal theta(final MathContext mc) {
	    return MathUtil.atan2(i(), r(), mc);
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
	 * Negate this value.
	 *
	 * @return A complex number that is the negative of this one.
	 */
	public ComplexNumber negate() {
	    if (equals(C_ZERO))
		return this;

	    if (realPart == null)
		return new ComplexNumber(realPart, imaginaryPart.negate());
	    else if (imaginaryPart == null)
		return new ComplexNumber(realPart.negate(), imaginaryPart);
	    else
		return new ComplexNumber(realPart.negate(), imaginaryPart.negate());
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
	 * Determine the "sign" of this complex number, which is {@code z / |z|}.
	 *
	 * @param mc The rounding context to use.
	 * @return {@code 0, +1, -1} depending if real or zero, or {@code z / |z|}.
	 */
	public Object signum(final MathContext mc) {
	    if (equals(C_ZERO))
		return BigInteger.ZERO;
	    if (isPureReal())
		return BigInteger.valueOf(r().signum());

	    return divide(radius(mc), mc);
	}

	/**
	 * Calculate the complex number to the given integer power, using the "squaring"
	 * technique for efficiency and precision.
	 *
	 * @param n  The integer power (can be negative).
	 * @param mc Rounding mode to use.
	 * @return   The result of {@code this ** n}.
	 */
	public ComplexNumber power(final int n, final MathContext mc) {
	    if (n < 0)
		return C_ONE.divide(this, mc).power(-n, mc);

	    if (n == 0)
		return C_ONE;
	    if (n == 1)
		return this;

	    ComplexNumber result = this;
	    ComplexNumber factor = C_ONE;

	    for (int p = n; p > 1; ) {
		if (p % 2 == 0) {
		    p >>= 1;
		}
		else {
		    factor = result.multiply(factor, mc);
		    p = (p - 1) >> 1;
		}
		result = result.multiply(result, mc);
	    }

	    return result.multiply(factor, mc);
	}

	/**
	 * Calculate this complex number to the given real power.
	 * <p> Do this by applying DeMoivre's theorem such that:
	 * {@code (a+bi)**n = r**n * (cos(n * theta) + i sin(n * theta))}
	 *
	 * @param n  The real power to raise this number to.
	 * @param mc Rounding context for the result.
	 * @return  The result of {@code this**n}.
	 */
	public ComplexNumber pow(final BigDecimal n, final MathContext mc) {
	    // Some easy special cases
	    if (n.scale() <= 0)
		return power(n.intValueExact(), mc);

	    boolean negative = false;
	    BigDecimal theta;

	    if (r().signum() < 0) {
		negative = true;
		theta = negate().theta(mc);
	    }
	    else {
		theta = theta(mc);
	    }

	    BigDecimal radius = radius(mc);
	    BigDecimal nTheta = n.multiply(theta);

	    BigDecimal rPower = MathUtil.pow(radius, n.doubleValue(), mc);

	    ComplexNumber result = polar(rPower, nTheta, mc);

	    // Note: for negative or fractional powers there are multiple roots, but we're
	    // going with the principal root, which should match signs with the input
	    if (negative)
		return result.negate();
	    else
		return result;
	}


	/**
	 * Get a {@link BigDecimal} value from an object, using suitable conversions.
	 *
	 * @param value	Some arbitrary (hopefully compatible) value.
	 * @return	{@code BigDecimal} value derived from it, or {@code null} if
	 *		we can't do the conversion.
	 */
	public static BigDecimal getDecimal(final Object value) {
	    if (value == null)
		return null;

	    if (value instanceof BigDecimal)
		return (BigDecimal) value;
	    if (value instanceof BigInteger)
		return new BigDecimal((BigInteger) value);
	    if (value instanceof BigFraction)
		return ((BigFraction) value).toDecimal();
	    if (value instanceof Number)
		return new BigDecimal(((Number) value).doubleValue());
	    if (value instanceof String) {
		try {
		    return new BigDecimal((String) value);
		}
		catch (NumberFormatException nfe) {
		    return null;
		}
	    }

	    return null;
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

	    if (value instanceof List) {
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) value;
		return fromList(list);
	    }
	    if (value instanceof Map) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) value;
		return fromMap(map);
	    }

	    BigDecimal dValue = getDecimal(value);
	    if (dValue != null)
		return real(dValue);

	    return parse(value.toString());
	}


	/**
	 * Parse out and construct a complex number from any of the string forms.
	 * <p> Note: this process recognizes the forms produced by both {@link #toLongString}
	 * and {@link #toPolarString}.
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
		throw new Intl.IllegalArgumentException("util#complex.notRecognized", string);
	    }

	    // Different treatment for r/theta version(s)
	    if (string.indexOf(':') >= 0) {
		BigDecimal radiusPart = new BigDecimal(m.group(2));
		BigDecimal thetaPart  = new BigDecimal(m.group(8));

		return polar(radiusPart, thetaPart, null);
	    }
	    else {
		BigDecimal rPart = new BigDecimal(m.group(1));
		BigDecimal iPart = new BigDecimal(m.group(7));

		if (m.group(6) != null && m.group(6).equals("-"))
		    iPart = iPart.negate();

		return new ComplexNumber(rPart, iPart);
	    }
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
	public boolean equals(Object other) {
	    if (!(other instanceof ComplexNumber))
		return false;

	    ComplexNumber c = (ComplexNumber) other;
	    return r().equals(c.r()) && i().equals(c.i());
	}

	@Override
	public int hashCode() {
	    if (realPart == null)
		return imaginaryPart.hashCode();
	    else if (imaginaryPart == null)
		return realPart.hashCode();
	    else
		return realPart.hashCode() ^ imaginaryPart.hashCode();
	}

	@Override
	public String toString() {
	    return String.format("( %1$s, %2$s )", r().toPlainString(), i().toPlainString());
	}

	/**
	 * Format an alternate representation of this complex number that looks (more or less)
	 * like: {@code r +- ni} with variations for pure real and pure imaginary values.
	 * <p> Note: this form is recognizable by {@link #parse}.
	 *
	 * @param upperCase Casing for the representation of {@code i}.
	 * @return The alternate string representation of this number.
	 */
	public String toLongString(final boolean upperCase) {
	    char i = upperCase ? '\u2110' : '\u2148';

	    if (realPart == null) {
		if (imaginaryPart.equals(BigDecimal.ONE))
		    return String.format("%1$c", i);
		else if (imaginaryPart.equals(D_MINUS_ONE))
		    return String.format("-%1$c", i);
		else
		    return String.format("%1$s%2$c", imaginaryPart.toPlainString(), i);
	    }
	    else if (imaginaryPart == null) {
		return String.format("%1$s", realPart.toPlainString());
	    }
	    else {
		if (imaginaryPart.signum() < 0)
		    return String.format("%1$s - %2$s%3$c",
			realPart.toPlainString(),
			imaginaryPart.abs().toPlainString(), i);
		else
		    return String.format("%1$s + %2$s%3$c",
			realPart.toPlainString(),
			imaginaryPart.toPlainString(), i);
	    }
	}

	/**
	 * Convert to a string representation of the polar form, using map notation, and {@code "r"} and {@code "\u0398"}
	 * (for upper case) or {@code "\u03B8"} (for lower case) map keys.
	 * <p> Note: this form is recognizable by {@link #parse}.
	 *
	 * @param upperCase Case to use for the map keys.
	 * @param mc        Rounding and precision for the conversion to polar values.
	 * @return Polar form (r, theta) of this complex number (as a string).
	 */
	public String toPolarString(final boolean upperCase, final MathContext mc) {
	    char r = upperCase ? 'R' : 'r';
	    char theta = upperCase ? '\u0398' : '\u03B8';

	    return String.format("{ %1$c: %2$s, %3$c: %4$s }", r, radius(mc).toPlainString(), theta, theta(mc).toPlainString());
	}

}
