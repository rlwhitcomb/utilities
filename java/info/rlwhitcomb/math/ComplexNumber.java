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
 *	    #273: Move to "math" package.
 *	21-Jun-2022 (rlwhitcomb)
 *	    #314: Add SetScope to the mix: conversions to/from sets.
 *	08-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 *	19-Jul-2022 (rlwhitcomb)
 *	    #420: Add new formats for "parse". Add "imaginary" constructors.
 *	01-Oct-2022 (rlwhitcomb)
 *	    #497: New method for precision.
 *	12-Oct-2022 (rlwhitcomb)
 *	    #514: Move resource text from "util" to "math" package.
 *	11-Nov-2022 (rlwhitcomb)
 *	    #420: Adjust COMPLEX_PATTERNS to recognize '1+i' (for instance).
 *	19-Dec-2022 (rlwhitcomb)
 *	    #559: Implement rational mode.
 *	20-Dec-2022 (rlwhitcomb)
 *	    #559: More fractional forms; always represents fractions as "proper".
 *	31-Dec-2022 (rlwhitcomb)
 *	    #558: Make F_ZERO public.
 */
package info.rlwhitcomb.math;

import info.rlwhitcomb.util.CharUtil;
import info.rlwhitcomb.util.Intl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	/** Separator for fractions that doesn't interfere with commas separating real and imaginary parts. */
	private static final String SEP = "(\\s+|\\s*[/;]\\s*)";

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

	/** For rational values, the possible forms of rational values for both real and imaginary parts. */
	private static final String FRACTION = "(" +
		SIGNED_INT + SEP + SIGNED_INT + SEP + SIGNED_INT        + "|" +
		SIGNED_INT + SEP + SIGNED_INT                           + "|" +
		SIGNED_INT + SEP + "?" + BigFraction.SIGNED_FRAC_STRING + "|" +
		BigFraction.SIGNED_FRAC_STRING                          + ")";

	/**
	 * The patterns used in {@link #parse} to recognize valid values.
	 */
	private static final Pattern COMPLEX_PATTERNS[] = {
	    Pattern.compile("^\\s*\\(\\s*(" + SIGNED_NUMBER + ")\\s*([,])\\s*(" + SIGNED_NUMBER + ")\\s*\\)\\s*$"),
	    Pattern.compile("^\\s*(" + SIGNED_NUMBER + ")\\s*([,])\\s*(" + SIGNED_NUMBER + ")\\s*$"),
	    Pattern.compile("^\\s*\\(\\s*(" + SIGNED_NUMBER + ")\\s*([+\\-])\\s*(" + NUMBER + ")?\\s*" + I_ALIASES + "\\s*\\)\\s*$"),
	    Pattern.compile("^\\s*(" + SIGNED_NUMBER + ")\\s*([+\\-])\\s*(" + NUMBER + ")?\\s*" + I_ALIASES + "\\s*$"),
	    Pattern.compile("^\\s*\\(\\s*(" + SIGNED_NUMBER + ")\\s*\\)\\s*$"),
	    Pattern.compile("^\\s*(" + SIGNED_NUMBER + ")\\s*$"),
	    Pattern.compile("^\\s*\\(\\s*(" + SIGNED_NUMBER + ")\\s*(" + I_ALIASES + ")\\s*\\)\\s*$"),
	    Pattern.compile("^\\s*(" + SIGNED_NUMBER + ")\\s*(" + I_ALIASES + ")\\s*$"),
	    Pattern.compile("^\\s*\\{\\s*" + RADIUS_ALIASES + "\\s*[:]\\s*(" + SIGNED_NUMBER + ")\\s*[,]\\s*" + THETA_ALIASES + "\\s*[:]\\s*(" + SIGNED_NUMBER + ")\\s*\\}\\s*$")
	};

	private static final Pattern COMPLEX_FRACTION_PATTERNS[] = {
	    Pattern.compile("^\\s*\\(\\s*(" + FRACTION + ")\\s*([,])\\s*(" + FRACTION + ")\\s*\\)\\s*$"),
	    Pattern.compile("^\\s*\\(\\s*(" + SIGNED_NUMBER + ")\\s*([,])\\s*(" + FRACTION + ")\\s*\\)\\s*$"),
	    Pattern.compile("^\\s*\\(\\s*(" + FRACTION + ")\\s*([,])\\s*(" + SIGNED_NUMBER + ")\\s*\\)\\s*$"),
	    Pattern.compile("^\\s*(" + FRACTION + ")\\s*([,])\\s*(" + FRACTION + ")\\s*$"),
	    Pattern.compile("^\\s*(" + SIGNED_NUMBER + ")\\s*([,])\\s*(" + FRACTION + ")\\s*$"),
	    Pattern.compile("^\\s*(" + FRACTION + ")\\s*([,])\\s*(" + SIGNED_NUMBER + ")\\s*$"),
	    Pattern.compile("^\\s*\\(\\s*(" + FRACTION + ")\\s*([+\\-])\\s*(" + FRACTION + ")?\\s*" + I_ALIASES + "\\s*\\)\\s*$"),
	    Pattern.compile("^\\s*\\(\\s*(" + SIGNED_NUMBER + ")\\s*([+\\-])\\s*(" + FRACTION + ")?\\s*" + I_ALIASES + "\\s*\\)\\s*$"),
	    Pattern.compile("^\\s*\\(\\s*(" + FRACTION + ")\\s*([+\\-])\\s*(" + NUMBER + ")?\\s*" + I_ALIASES + "\\s*\\)\\s*$"),
	    Pattern.compile("^\\s*(" + FRACTION + ")\\s*([+\\-])\\s*(" + FRACTION + ")?\\s*" + I_ALIASES + "\\s*$"),
	    Pattern.compile("^\\s*(" + SIGNED_NUMBER + ")\\s*([+\\-])\\s*(" + FRACTION + ")?\\s*" + I_ALIASES + "\\s*$"),
	    Pattern.compile("^\\s*(" + FRACTION + ")\\s*([+\\-])\\s*(" + NUMBER + ")?\\s*" + I_ALIASES + "\\s*$"),
	    Pattern.compile("^\\s*\\(\\s*(" + FRACTION + ")\\s*\\)\\s*$"),
	    Pattern.compile("^\\s*(" + FRACTION + ")\\s*$"),
	    Pattern.compile("^\\s*\\(\\s*(" + FRACTION + ")\\s*(" + I_ALIASES + ")\\s*\\)\\s*$"),
	    Pattern.compile("^\\s*(" + FRACTION + ")\\s*(" + I_ALIASES + ")\\s*$")
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
	 * Optional map key indicating the rational flag.
	 */
	private static final String RATIONAL_KEY = "rational";

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
	 * A fraction value of zero, with "alwaysProper" set.
	 */
	public static final BigFraction F_ZERO = BigFraction.properFraction(0);


	/**
	 * Flag indicating this complex number is composed of rational (fraction) parts.
	 */
	private boolean rational;

	/**
	 * The real part of this complex number.
	 */
	private BigDecimal realPart;

	/**
	 * The imaginary part of this complex number.
	 */
	private BigDecimal imaginaryPart;

	/**
	 * If the {@link #rational} flag is true, the fractional real part of this complex number.
	 */
	private BigFraction realFrac;

	/**
	 * If the {@link #rational} flag is true, the fractional imaginary part of this complex number.
	 */
	private BigFraction imaginaryFrac;


	/**
	 * Construct one, given the real and imaginary values.
	 *
	 * @param r Value for the real part (can be {@code null} for a pure
	 *          imaginary number (such as {@code 2i}).
	 * @param i Value for the imaginary part (can also be {@code null} for a
	 *          pure real number (such as {@code 3.5}).
	 */
	public ComplexNumber(final BigDecimal r, final BigDecimal i) {
	    rational = false;
	    realPart      = r;
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
	    rational = false;
	    realPart      = r == null ? null : new BigDecimal(r);
	    imaginaryPart = i == null ? null : new BigDecimal(i);
	    normalize();
	}

	/**
	 * Construct one, given the real and imaginary rational values.
	 *
	 * @param rFrac Value for the real part (can be {@code null} for a pure
	 *              imaginary number (such as {@code 2i}).
	 * @param iFrac Value for the imaginary part (can also be {@code null} for a
	 *              pure real number (such as {@code 17/5}).
	 */
	public ComplexNumber(final BigFraction rFrac, final BigFraction iFrac) {
	    rational = true;
	    realFrac      = rFrac;
	    imaginaryFrac = iFrac;
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
	    rational = false;
	    realPart      = BigDecimal.valueOf(r);
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
	    rational = false;
	    realPart      = BigDecimal.valueOf(r);
	    imaginaryPart = BigDecimal.valueOf(i);
	    normalize();
	}

	/**
	 * Get a boolean value (default {@link Boolean#FALSE}) for the rational flag.
	 *
	 * @param obj Object stored in a map, set, or list that purports to be the boolean
	 * "rational" flag.
	 * @return    Value of the flag (if present), or {@code false} if the flag is not present.
	 */
	private static boolean getFlagValue(final Object obj) {
	    if (obj == null)
		return false;

	    return CharUtil.getBooleanValue(obj);
	}

	/**
	 * Construct from a list of one, two, or three values.
	 *
	 * @param list Any list of one to three values.
	 * @return     The new complex number, if possible.
	 * @throws IllegalArgumentException if the number of values is wrong.
	 */
	public static ComplexNumber fromList(List<Object> list) {
	    if (list == null || list.size() == 0)
		throw new Intl.IllegalArgumentException("math#complex.noEmptyListMap");
	    if (list.size() > 3)
		throw new Intl.IllegalArgumentException("math#complex.tooManyValues");

	    if (list.size() == 1)
		return valueOf(list.get(0));

	    Object o1 = list.get(0);
	    Object o2 = list.get(1);
	    boolean isRational = false;

	    if (list.size() > 2)
		isRational = getFlagValue(list.get(2));

	    if (isRational) {
		BigFraction rFrac = BigFraction.valueOf(o1);
		BigFraction iFrac = BigFraction.valueOf(o2);

		return new ComplexNumber(rFrac, iFrac);
	    }
	    else {
		BigDecimal r = getDecimal(o1);
		BigDecimal i = getDecimal(o2);

		return new ComplexNumber(r, i);
	    }
	}

	/**
	 * Construct from a map of one or two values, with or without a "rational" flag.
	 *
	 * @param map Any map with "r" and/or "i" keys, and possible "rational" key.
	 * @return    The new complex number.
	 * @throws IllegalArgumentException if the input map is null or empty.
	 * @see #REAL_KEY
	 * @see #IMAG_KEY
	 * @see #RATIONAL_KEY
	 */
	public static ComplexNumber fromMap(Map<String, Object> map) {
	    if (map == null || map.size() == 0)
		throw new Intl.IllegalArgumentException("math#complex.noEmptyListMap");

	    // Here, we could have "r,i" or "r,theta", with possible "rational" flag
	    boolean isRational = getFlagValue(map.get(RATIONAL_KEY));

	    if (map.containsKey(IMAG_KEY)) {
		if (isRational) {
		    BigFraction rFrac = BigFraction.valueOf(map.get(REAL_KEY));
		    BigFraction iFrac = BigFraction.valueOf(map.get(IMAG_KEY));

		    return new ComplexNumber(rFrac, iFrac);
		}
		else {
		    BigDecimal r = getDecimal(map.get(REAL_KEY));
		    BigDecimal i = getDecimal(map.get(IMAG_KEY));

		    return new ComplexNumber(r, i);
		}
	    }
	    else {
		BigDecimal radius = BigDecimal.ZERO;
		BigDecimal theta  = BigDecimal.ZERO;

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
	 * Construct from a set of one, two, or three values.
	 * <p> This gets complicated since there are several scenarios:
	 * <nl><li>One decimal value, meaning this a complex number of the form {@code (v, v)}
	 * <li>Two decimal values, meaning a complex number of the form {@code (r, i)}
	 * <li>One fraction value, and one boolean value, meaning a rational complex number of
	 * the form {@code (f, f)}
	 * <li>Two fraction values, and one boolean value, meaning a rational complex number of
	 * the form {@code (fr, fi)}
	 * </nl>
	 *
	 * @param set Any set of one to three values.
	 * @return    The new complex number, if possible.
	 * @throws IllegalArgumentException if the number of values is wrong.
	 */
	public static ComplexNumber fromSet(Set<Object> set) {
	    if (set == null || set.size() == 0)
		throw new Intl.IllegalArgumentException("math#complex.noEmptyListMap");
	    if (set.size() > 3)
		throw new Intl.IllegalArgumentException("math#complex.tooManyValues");

	    Iterator<Object> iter = set.iterator();

	    if (set.size() == 1) {
		BigDecimal v = getDecimal(iter.next());
		return new ComplexNumber(v, v);
	    }

	    Object o1 = iter.next();
	    Object o2 = iter.next();
	    boolean isRational = false;

	    if (iter.hasNext())
		isRational = getFlagValue(iter.next());

	    if (isRational) {
		BigFraction rFrac = BigFraction.valueOf(o1);
		BigFraction iFrac = BigFraction.valueOf(o2);

		return new ComplexNumber(rFrac, iFrac);
	    }
	    else if (o2 instanceof Boolean) {
		BigFraction vFrac = BigFraction.valueOf(o1);

		return new ComplexNumber(vFrac, vFrac);
	    }
	    else {
		BigDecimal r = getDecimal(o1);
		BigDecimal i = getDecimal(o2);

		return new ComplexNumber(r, i);
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
	 * Construct one of these from a pure real fraction.
	 *
	 * @param rFrac The pure real fraction.
	 * @return      A new complex number with no imaginary part.
	 */
	public static ComplexNumber real(final BigFraction rFrac) {
	    return new ComplexNumber(rFrac, null);
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
	 * Construct one of these from a pure imaginary number.
	 *
	 * @param i The pure imaginary value.
	 * @return  A new complex number with no real part.
	 */
	public static ComplexNumber imaginary(final BigDecimal i) {
	    return new ComplexNumber(null, i);
	}

	/**
	 * Construct one of these from a pure imaginary number.
	 *
	 * @param i The pure imaginary value.
	 * @return  A new complex number with no real part.
	 */
	public static ComplexNumber imaginary(final BigInteger i) {
	    return new ComplexNumber(null, i);
	}

	/**
	 * Construct one of these from a pure imaginary fraction.
	 *
	 * @param iFrac The pure imaginary fraction.
	 * @return      A new complex number with no real part.
	 */
	public static ComplexNumber imaginary(final BigFraction iFrac) {
	    return new ComplexNumber(null, iFrac);
	}

	/**
	 * Alternate pure imaginary "constructor".
	 *
	 * @param i The pure imaginary value.
	 * @return  A new complex number with no real part.
	 */
	public static ComplexNumber imaginary(final double i) {
	    return new ComplexNumber(null, BigDecimal.valueOf(i));
	}

	/**
	 * Alternate pure imaginary "constructor".
	 *
	 * @param i The pure imaginary value.
	 * @return  A new complex number with no real part.
	 */
	public static ComplexNumber imaginary(final long i) {
	    return new ComplexNumber(null, BigDecimal.valueOf(i));
	}


	/**
	 * Is this a rational complex number (whose real and imaginary parts
	 * are saved as rational fractions)?
	 *
	 * @return The {@link #rational} flag.
	 */
	public boolean isRational() {
	    return rational;
	}

	/**
	 * Is this complex number equal to zero?
	 * <p> Assumes this complex number has been normalized.
	 *
	 * @return {@code true} if the real part equals {@code zero}
	 *         and the imaginary part is {@code null}.
	 */
	public boolean isZero() {
	    if (rational)
		return realFrac.isZero() && imaginaryFrac == null;
	    else
		return realPart.equals(BigDecimal.ZERO) && imaginaryPart == null;
	}


	/**
	 * Access the real part of ourselves.
	 *
	 * @return The real part of this complex number (which will be
	 *         {@code 0} for a pure imaginary number).
	 */
	public BigDecimal r() {
	    if (rational)
		return realFrac == null ? BigDecimal.ZERO : realFrac.toDecimal();
	    else
		return realPart == null ? BigDecimal.ZERO : realPart;
	}

	/**
	 * Access the imaginary part of ourselves.
	 *
	 * @return The imaginary part of this complex number (which will be
	 *         {@code 0} for a pure real number).
	 */
	public BigDecimal i() {
	    if (rational)
		return imaginaryFrac == null ? BigDecimal.ZERO : imaginaryFrac.toDecimal();
	    else
		return imaginaryPart == null ? BigDecimal.ZERO : imaginaryPart;
	}


	/**
	 * Access the real fraction part of ourselves.
	 *
	 * @return The real fraction value of this complex number (which will be
	 *         {@code 0} for a pure imaginary.
	 */
	public BigFraction rFrac() {
	    if (rational)
		return realFrac == null ? F_ZERO : realFrac;
	    else
		return realPart == null ? F_ZERO : BigFraction.properFraction(realPart);
	}

	/**
	 * Access the imaginary fraction part of ourselves.
	 *
	 * @return The imaginary fraction value of this complex number (which will be
	 *         {@code 0} for a pure real.
	 */
	public BigFraction iFrac() {
	    if (rational)
		return imaginaryFrac == null ? F_ZERO : imaginaryFrac;
	    else
		return imaginaryPart == null ? F_ZERO : BigFraction.properFraction(imaginaryPart);
	}


	/**
	 * Get the precision, which is the maximum precision of both parts.
	 *
	 * @return Maximum precision of the two parts.
	 */
	public int precision() {
	    int prec = 0;

	    if (rational) {
		prec = F_ZERO.precision();

		if (realFrac != null)
		    prec = Math.max(prec, realFrac.precision());
		if (imaginaryFrac != null)
		    prec = Math.max(prec, imaginaryFrac.precision());
	    }
	    else {
		prec = BigDecimal.ZERO.precision();

		if (realPart != null)
		    prec = Math.max(prec, realPart.precision());
		if (imaginaryPart != null)
		    prec = Math.max(prec, imaginaryPart.precision());
	    }

	    return prec;
	}


	/**
	 * Do the normalization to keep our values in a consistent form.
	 * <p> For now, until we need other things, we will remove {@code 0}
	 * values and keep them stored as {@code null} in that part.
	 */
	private void normalize() {
	    if (rational) {
		if (realFrac != null && realFrac.isZero())
		    realFrac = null;
		if (imaginaryFrac != null && imaginaryFrac.isZero())
		    imaginaryFrac = null;

		if (realFrac == null && imaginaryFrac == null)
		    realFrac = F_ZERO;

		if (realFrac != null)
		    realFrac.setAlwaysProper(true);
		if (imaginaryFrac != null)
		    imaginaryFrac.setAlwaysProper(true);
	    }
	    else {
		if (realPart != null && realPart.equals(BigDecimal.ZERO))
		    realPart = null;
		if (imaginaryPart != null && imaginaryPart.equals(BigDecimal.ZERO))
		    imaginaryPart = null;

		// Make sure both parts are not null (zero)
		if (realPart == null && imaginaryPart == null)
		    realPart = BigDecimal.ZERO;
	    }
	}

	/**
	 * @return Is this a pure real number (imaginary part is zero)?
	 */
	public boolean isPureReal() {
	    return (rational && imaginaryFrac == null) || (!rational && imaginaryPart == null);
	}

	/**
	 * @return Is this a pure imaginary number (real part is zero)?
	 */
	public boolean isPureImaginary() {
	    return (rational && realFrac == null) || (!rational && realPart == null);
	}


	/**
	 * Convert to a list of two or three values.
	 *
	 * @return A list with the real part and imaginary part, in that order, with the
	 * optional third element if this is a rational complex number.
	 */
	public List<Object> toList() {
	    List<Object> list = new ArrayList<>();

	    if (rational) {
		list.add(rFrac());
		list.add(iFrac());
		list.add(isRational());
	    }
	    else {
		list.add(r());
		list.add(i());
	    }

	    return list;
	}

	/**
	 * Convert to a map with real and imaginary keys.
	 *
	 * @return {@code Map} with {@link #REAL_KEY} and {@link #IMAG_KEY} entries, and
	 * optional {@link #RATIONAL_KEY}.
	 */
	public Map<String, Object> toMap() {
	    Map<String, Object> map = new LinkedHashMap<>();

	    if (rational) {
		map.put(REAL_KEY, rFrac());
		map.put(IMAG_KEY, iFrac());
		map.put(RATIONAL_KEY, isRational());
	    }
	    else {
		map.put(REAL_KEY, r());
		map.put(IMAG_KEY, i());
	    }

	    return map;
	}

	/**
	 * Convert to a set with one, two, or three values.
	 * <p> This gets complicated since there are several scenarios:
	 * <nl><li>One decimal value, meaning this a complex number of the form {@code (v, v)}
	 * <li>Two decimal values, meaning a complex number of the form {@code (r, i)}
	 * <li>One fraction value, and one boolean value, meaning a rational complex number of
	 * the form {@code (f, f)}
	 * <li>Two fraction values, and one boolean value, meaning a rational complex number of
	 * the form {@code (fr, fi)}
	 * </nl>
	 * but what does that mean? It means we take the one value as both the real
	 * and imaginary value.
	 *
	 * @return {@code Set} with either one value (for both real and imaginary)
	 * or two values, with optional third value if this is a rational number.
	 */
	public Set<Object> toSet() {
	    Set<Object> set = new LinkedHashSet<>();

	    if (rational) {
		// This could result in two or three values
		set.add(rFrac());
		set.add(iFrac());
		set.add(isRational());
	    }
	    else {
		// This could result in one or two values
		set.add(r());
		set.add(i());
	    }

	    return set;
	}


	@Override
	public int compareTo(final ComplexNumber other) {
	    // Not strictly speaking comparable unless pure real or pure imaginary
	    // It won't really help us (much) to compare fractions if this is rational
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
	    // Doubtful even if the values are rational that this value would be too ...
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
	 * <code>( (a+c), (b+d) )</code>.
	 *
	 * @param other The number to add to this one.
	 * @return      The sum.
	 */
	public ComplexNumber add(final ComplexNumber other) {
	    // If both are rational then we can stay that way
	    if (isRational() && other.isRational()) {
		BigFraction a1f = rFrac();
		BigFraction a2f = other.rFrac();
		BigFraction b1f = iFrac();
		BigFraction b2f = other.iFrac();

		return new ComplexNumber(a1f.add(a2f), b1f.add(b2f));
	    }
	    else {
		BigDecimal a1 = r();
		BigDecimal a2 = other.r();
		BigDecimal b1 = i();
		BigDecimal b2 = other.i();

		return new ComplexNumber(a1.add(a2), b1.add(b2));
	    }
	}

	/**
	 * Subtract the two complex numbers, this and the other.
	 * <p> The result of <code>( a, b ) - ( c, d )</code> is
	 * <code>( (a-c), (b-d) )</code>.
	 *
	 * @param other The number to subtract from this one.
	 * @return      The difference.
	 */
	public ComplexNumber subtract(final ComplexNumber other) {
	    if (isRational() && other.isRational()) {
		BigFraction a1f = rFrac();
		BigFraction a2f = other.rFrac();
		BigFraction b1f = iFrac();
		BigFraction b2f = other.iFrac();

		return new ComplexNumber(a1f.subtract(a2f), b1f.subtract(b2f));
	    }
	    else {
		BigDecimal a1 = r();
		BigDecimal a2 = other.r();
		BigDecimal b1 = i();
		BigDecimal b2 = other.i();

		return new ComplexNumber(a1.subtract(a2), b1.subtract(b2));
	    }
	}

	/**
	 * Multiply the two complex number, this and the other.
	 * <p> The result of <code>( x, y ) * ( u , v )</code> is
	 * <code>( (x*u - y*v), (x*v + y*u) )</code>.
	 *
	 * @param other The number to multiply this one by.
	 * @param mc    The rounding mode to use for the final product.
	 * @return      The product.
	 */
	public ComplexNumber multiply(final ComplexNumber other, final MathContext mc) {
	    // If both are rational then we can proceed on that basis
	    if (isRational() && other.isRational()) {
		BigFraction xf = rFrac();
		BigFraction yf = iFrac();
		BigFraction uf = other.rFrac();
		BigFraction vf = other.iFrac();

		BigFraction rFracTerm = xf.multiply(uf).subtract(yf.multiply(vf));
		BigFraction iFracTerm = xf.multiply(vf).add(yf.multiply(uf));

		return new ComplexNumber(rFracTerm, iFracTerm);
	    }
	    else {
		BigDecimal x = r();
		BigDecimal y = i();
		BigDecimal u = other.r();
		BigDecimal v = other.i();

		BigDecimal rTerm = x.multiply(u).subtract(y.multiply(v), mc);
		BigDecimal iTerm = x.multiply(v).add(y.multiply(u), mc);

		return new ComplexNumber(rTerm, iTerm);
	    }
	}

	/**
	 * Compute the complex conjugate of this number, which for
	 * {@code (real, imag)} is {@code (real, -imag)}.
	 *
	 * @return A new complex number which is the conjugate of this one.
	 */
	public ComplexNumber conjugate() {
	    if (rational)
		return new ComplexNumber(rFrac(), iFrac().negate());
	    else
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

	    if (rational) {
		if (realFrac == null)
		    return new ComplexNumber(realFrac, imaginaryFrac.negate());
		else if (imaginaryFrac == null)
		    return new ComplexNumber(realFrac.negate(), imaginaryFrac);
		else
		    return new ComplexNumber(realFrac.negate(), imaginaryFrac.negate());
	    }
	    else {
		if (realPart == null)
		    return new ComplexNumber(realPart, imaginaryPart.negate());
		else if (imaginaryPart == null)
		    return new ComplexNumber(realPart.negate(), imaginaryPart);
		else
		    return new ComplexNumber(realPart.negate(), imaginaryPart.negate());
	    }
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
	    if (rational) {
		BigFraction pFrac = BigFraction.properFraction(p);
		return new ComplexNumber(rFrac().divide(pFrac), iFrac().divide(pFrac));
	    }
	    else {
		return new ComplexNumber(r().divide(p, mc), i().divide(p, mc));
	    }
	}

	/**
	 * Divide this complex number by a real fraction.
	 * <p> The result is {@code (real/f, imag/f)}.
	 *
	 * @param f  The real fraction to divide by.
	 * @param mc The rounding mode for the result.
	 * @return   A new ComplexNumber with the result.
	 */
	public ComplexNumber divide(final BigFraction f, final MathContext mc) {
	    if (rational) {
		return new ComplexNumber(rFrac().divide(f), iFrac().divide(f));
	    }
	    else {
		BigDecimal bd = f.toDecimal(mc);
		return new ComplexNumber(r().divide(bd, mc), i().divide(bd, mc));
	    }
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
	    if (rational) {
		BigFraction divisor = other.multiply(conjugate, mc).rFrac();

		return multiply(conjugate, mc).divide(divisor, mc);
	    }
	    else {
		BigDecimal divisor = other.multiply(conjugate, mc).r();

		return multiply(conjugate, mc).divide(divisor, mc);
	    }
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

	    if (isPureReal()) {
		int sign = rational ? rFrac().signum() : r().signum();
		return BigInteger.valueOf(sign);
	    }

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
		return rational ? CR_ONE : C_ONE;
	    if (n == 1)
		return this;

	    ComplexNumber result = this;
	    ComplexNumber factor = rational ? CR_ONE : C_ONE;

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
	    if (value instanceof Set) {
		@SuppressWarnings("unchecked")
		Set<Object> set = (Set<Object>) value;
		return fromSet(set);
	    }

	    if (value instanceof BigFraction) {
		return real((BigFraction) value);
	    }

	    BigDecimal dValue = getDecimal(value);
	    if (dValue != null)
		return real(dValue);

	    BigFraction fValue = BigFraction.valueOf(value);
	    if (fValue != null)
		return real(fValue);

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
	    boolean rational = false;

	    Matcher m = null;
	    for (int i = 0; i < COMPLEX_PATTERNS.length; i++) {
		m = COMPLEX_PATTERNS[i].matcher(string);
		if (m.matches())
		    break;
	    }
	    if (!m.matches()) {
		for (int i = 0; i < COMPLEX_FRACTION_PATTERNS.length; i++) {
		    m = COMPLEX_FRACTION_PATTERNS[i].matcher(string);
		    if (m.matches()) {
			rational = true;
			break;
		    }
		}
		if (!m.matches()) {
		    throw new Intl.IllegalArgumentException("math#complex.notRecognized", string);
		}
	    }

	    // Different treatment for r/theta version(s)
	    if (string.indexOf(':') >= 0) {
		BigDecimal radiusPart = new BigDecimal(m.group(2));
		BigDecimal thetaPart  = new BigDecimal(m.group(8));

		return polar(radiusPart, thetaPart, null);
	    }
	    else if (rational) {
		if (m.groupCount() > 16) {
		    BigFraction rFrac = BigFraction.valueOf(m.group(2));
		    BigFraction iFrac = BigFraction.valueOf(m.group(16));
		    return new ComplexNumber(rFrac, iFrac);
		}
		else {
		    return imaginary(BigFraction.valueOf(m.group(2)));
		}
	    }
	    else {
		if (m.groupCount() == 5) {
		    // Pure real number
		    return real(new BigDecimal(m.group(1)));
		}
		else if (m.groupCount() == 6) {
		    // Pure imaginary number
		    return imaginary(new BigDecimal(m.group(1)));
		}
		else {
		    BigDecimal rPart = new BigDecimal(m.group(1));
		    String iMult = m.group(7);
		    BigDecimal iPart = (iMult != null) ? new BigDecimal(iMult) : BigDecimal.ONE;

		    if (m.group(6) != null && m.group(6).equals("-"))
			iPart = iPart.negate();

		    return new ComplexNumber(rPart, iPart);
		}
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

	    if (isRational() && c.isRational()) {
		return rFrac().equals(c.rFrac()) && iFrac().equals(c.iFrac());
	    }

	    // Any other (mixed) combination will just compare the decimal values
	    return r().equals(c.r()) && i().equals(c.i());
	}

	@Override
	public int hashCode() {
	    if (rational) {
		if (realFrac == null)
		    return imaginaryFrac.hashCode();
		else if (imaginaryFrac == null)
		    return realFrac.hashCode();
		else
		    return realFrac.hashCode() ^ imaginaryFrac.hashCode();
	    }
	    else {
		if (realPart == null)
		    return imaginaryPart.hashCode();
		else if (imaginaryPart == null)
		    return realPart.hashCode();
		else
		    return realPart.hashCode() ^ imaginaryPart.hashCode();
	    }
	}

	@Override
	public String toString() {
	    if (rational)
		return String.format("( %1$s, %2$s )", rFrac().toString(), iFrac().toString());
	    else
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

	    if (rational) {
		if (realFrac == null) {
		    if (imaginaryFrac.equals(BigFraction.ONE))
			return String.format("%1$c", i);
		    else if (imaginaryFrac.equals(BigFraction.MINUS_ONE))
			return String.format("-%1$c", i);
		    else
			return String.format("%1$s%2$c", imaginaryFrac.toString(), i);
		}
		else if (imaginaryFrac == null) {
		    return realFrac.toString();
		}
		else {
		    if (imaginaryFrac.signum() < 0)
			return String.format("%1$s - %2$s%3$c",
			    realFrac.toString(),
			    imaginaryFrac.abs().toString(), i);
		    else
			return String.format("%1$s + %2$s%3$c",
			    realFrac.toString(),
			    imaginaryFrac.toString(), i);
		}
	    }
	    else {
		if (realPart == null) {
		    if (imaginaryPart.equals(BigDecimal.ONE))
			return String.format("%1$c", i);
		    else if (imaginaryPart.equals(D_MINUS_ONE))
			return String.format("-%1$c", i);
		    else
			return String.format("%1$s%2$c", imaginaryPart.toPlainString(), i);
		}
		else if (imaginaryPart == null) {
		    return realPart.toPlainString();
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
	}

	/**
	 * Convert to a string representation of the polar form, using map notation, and {@code "r"} and {@code "\u0398"}
	 * (for upper case) or {@code "\u03B8"} (for lower case) map keys.
	 * <p> Note: this form is recognizable by {@link #parse}.
	 * <p> Also considering that the theta value is VERY rarely going to be rational even with rational parts, we
	 * will always return decimal values here.
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
