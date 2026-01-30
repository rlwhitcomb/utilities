/*
 a The MIT License (MIT)
 *
 * Copyright (c) 2022-2026 Roger L. Whitcomb.
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
 *	imaginary part, or BigFraction parts for a "rational" complex number.
 *	This class stores these values, and provides a number of arithmetic
 *	and other operations related to them.
 *
 * History:
 *  24-Jan-22 rlw ----	Created.
 *		  #103	More work: extend Number, implement Comparable, Serializable.
 *  30-Jan-22 rlw #103	extend aliases of "i" (must match CalcPredefine).
 *  31-Jan-22 rlw #103	Create from List and Map; convert to List and Map.
 *  01-Feb-22 rlw #103	Powers of integer and real values, negate, another alias,
 *			"toLongString" method, override "equals" and "hashCode".
 *		  #231	Use new Constants values instead of our own.
 *  05-Feb-22 rlw ----	Fix Intl keys.
 *  08-Feb-22 rlw #235	Use MathUtil.atan2 for "theta" to get full precision.
 *			A lot of refactoring, include support for "polar" form.
 *  18-Feb-22 rlw ----	Add "signum" method.
 *  14-Apr-22 rlw #272	Some (mostly) documentation fixes.
 *		  #273	Move to "math" package.
 *  21-Jun-22 rlw #314	Add SetScope to the mix: conversions to/from sets.
 *  08-Jul-22 rlw #393	Cleanup imports.
 *  19-Jul-22 rlw #420	Add new formats for "parse". Add "imaginary" constructors.
 *  01-Oct-22 rlw #497	New method for precision.
 *  12-Oct-22 rlw #514	Move resource text from "util" to "math" package.
 *  11-Nov-22 rlw #420	Adjust COMPLEX_PATTERNS to recognize '1+i' (for instance).
 *  19-Dec-22 rlw #559	Implement rational mode.
 *  20-Dec-22 rlw #559	More fractional forms; always represents fractions as "proper".
 *  31-Dec-22 rlw #558	Make F_ZERO public.
 *  05-Jan-23 rlw #558	Make copies of the fraction parts to avoid improper "proper" settings.
 *  09-Jan-23 rlw #103	Add "sqrt"; fixup results of subtract and multiply/divide.
 *  21-Feb-23 rlw #244	Implement formatting with separators.
 *  09-May-23 rlw ----	Move F_ZERO back to BigFraction (too weird in here).
 *  13-Dec-23 rlw ----	Use MaxInt.
 *  30-Jan-24 rlw #649	Options for spacing of fraction values.
 *  14-May-24 rlw ----	Conversion of Quaternion to complex in "valueOf". New "part" method
 *			to extract one part or the other.
 *  16-May-24 rlw ----	New "toBigIntegerExact()" method.
 *  15-Jan-25 rlw ----	New "ceil" and "floor" functions.
 *  29-Jan-25 rlw #702	New "idivide", "remainder", and "modulus" functions.
 *  12-Mar-25 rlw #710	New "intValueExact()" and "isPureInteger()"  methods.
 *  16-Apr-25 rlw	Really use ZERO where needed.
 *  19-Apr-25 rlw #716	Extensive refactoring.
 *  01-May-25 rlw #716	More refactoring, including updated doc.
 *  03-May-25 rlw #716	Refactor "ceil" and "floor".
 *		  #702	Fix "modulus".
 *  24-May-25 rlw #721	Add "increment()".
 *  12-Jul-25 rlw #740	Add "dot" (product) function.
 *  22-Jul-25 rlw #677	New "divideAndRemainder" function; fix "add", "subtract", "multiply" for non-rational;
 *			add "toDecimalComplex()" for these fixes.
 *  10-Aug-25 rlw #745	Change exponent for "pow" to BigDecimal.
 *  29-Nov-25 rlw #643	New method to return the list of component parts; add "decrement()".
 *  26-Jan-26 rlw #806	New "extra" parameter to "toLongString" for enhanced formatting.
 *  27-Jan-26 rlw #809	TrigMode parameter to "toPolarString" method.
 */
package info.rlwhitcomb.math;

import info.rlwhitcomb.math.MaxInt;
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
 * <p> Values are stored as separate real and imaginary values and kept in an
 * internal form, which means that pure real and pure imaginary numbers have
 * their other component set to {@code null}, which also means that {@code 0}
 * values for either are removed, to reduce the number of special cases in the math.
 * <p> For the "decimal" subclass, values are stored as {@link BigDecimal}
 * values and calculations are done with those values. The "rational"
 * subclass uses {@link BigFraction} values, and all calculations produce
 * strictly rational results.
 * <p> TODO: more math operations.
 */
public abstract class ComplexNumber extends Number implements Serializable, Comparable<ComplexNumber>
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

	/** Normal format for display. */
	protected static final String NORMAL_FORMAT = "( %1$s, %2$s )";
	/** Format for long form positive values. */
	protected static final String LONG_POS_FORMAT = "%1$s + %2$s%3$c";
	/** Format for long form negative values. */
	protected static final String LONG_NEG_FORMAT = "%1$s - %2$s%3$c";
	/** Format for positive "i" value. */
	protected static final String I_POS_FORMAT = "%1$c";
	/** Format for negative "i" value. */
	protected static final String I_NEG_FORMAT = "-%1$c";
	/** Format for pure imaginary values. */
	protected static final String IMAG_FORMAT = "%1$s%2$c";

	/**
	 * Format for {@link #toPolarString}.
	 */
	private static final String POLAR_FORMAT = "{ %1$c: %2$s, %3$c: %4$s }";

	/**
	 * A map key indicating the real part.
	 */
	protected static final String REAL_KEY = "r";

	/**
	 * Map key indicating the imaginary part.
	 */
	protected static final String IMAG_KEY = "i";

	/**
	 * Optional map key indicating the rational flag.
	 */
	protected static final String RATIONAL_KEY = "rational";

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


	/** A decimal complex value of zero ({@code (0,null)}). */
	private static ComplexNumber ZERO = zero();

	/** A rational complex value of zero ({@code (0/1,null)}). */
	private static ComplexNumber F_ZERO = rational(BigFraction.ZERO, null);


	/**
	 * Construct with a decimal value of {@code (0,0)}.
	 */
	public static ComplexNumber zero() {
	    return new DecimalComplexNumber();
	}

	/**
	 * Produce a decimal value given the real value.
	 *
	 * @param r Value for the real part, with an empty imaginary part.
	 */
	public static ComplexNumber decimal(final BigDecimal r) {
	    return new DecimalComplexNumber(r, null);
	}

	/**
	 * Produce a decimal value given the real and imaginary values.
	 *
	 * @param r Value for the real part (can be {@code null} for a pure
	 *          imaginary number (such as {@code 2i}).
	 * @param i Value for the imaginary part (can also be {@code null} for a
	 *          pure real number (such as {@code 3.5}).
	 */
	public static ComplexNumber decimal(final BigDecimal r, final BigDecimal i) {
	    return new DecimalComplexNumber(r, i);
	}

	/**
	 * Produce a decimal value of whole numbers.
	 *
	 * @param rInt Value of the real part.
	 * @param iInt Value of the imaginary part.
	 * @return A decimal complex number with these whole number parts.
	 */
	public static ComplexNumber decimal(final BigInteger rInt, final BigInteger iInt) {
	    return new DecimalComplexNumber(rInt, iInt);
	}

	/**
	 * Produce a rational value given the real value.
	 *
	 * @param rFrac Value of the real part, with an empty imaginary part.
	 * @return A rational complex number with these parts.
	 */
	public static ComplexNumber rational(final BigFraction rFrac) {
	    return new RationalComplexNumber(rFrac, null);
	}

	/**
	 * Produce a rational value given the real and imaginary values.
	 *
	 * @param rFrac Value of the real part.
	 * @param iFrac Value of the imaginary part.
	 * @return A rational complex number with these parts.
	 */
	public static ComplexNumber rational(final BigFraction rFrac, final BigFraction iFrac) {
	    return new RationalComplexNumber(rFrac, iFrac);
	}

	/**
	 * Produce a rational value of whole numbers.
	 *
	 * @param rInt Value of the real part.
	 * @param iInt Value of the imaginary part.
	 * @return A rational complex number with these whole number parts.
	 */
	public static ComplexNumber rational(final BigInteger rInt, final BigInteger iInt) {
	    return new RationalComplexNumber(rInt, iInt);
	}


	/**
	 * Get a boolean value (default {@link Boolean#FALSE}) for the rational flag.
	 *
	 * @param obj Object stored in a map, set, or list that purports to be the boolean
	 * "rational" flag.
	 * @return    Value of the flag (if present), or {@code false} if the flag is not present.
	 */
	protected static boolean getFlagValue(final Object obj) {
	    if (obj == null)
		return false;

	    return CharUtil.getBooleanValue(obj);
	}

	/**
	 * Construct from a list of one, two, or three values.
	 *
	 * @param list Any list of one to three values.
	 * @param rat  Whether to interpret some values as rational.
	 * @return     The new complex number, if possible.
	 * @throws IllegalArgumentException if the number of values is wrong.
	 */
	public static ComplexNumber fromList(final List<Object> list, final boolean rat) {
	    if (list == null || list.size() == 0)
		throw new Intl.IllegalArgumentException("math#complex.noEmptyListMap");
	    if (list.size() > 3)
		throw new Intl.IllegalArgumentException("math#complex.tooManyValues");

	    if (list.size() == 1)
		return valueOf(list.get(0), rat);

	    Object o1 = list.get(0);
	    Object o2 = list.get(1);
	    boolean isRational = false;

	    if (list.size() > 2)
		isRational = getFlagValue(list.get(2));

	    if (isRational) {
		BigFraction rFrac = BigFraction.valueOf(o1);
		BigFraction iFrac = BigFraction.valueOf(o2);

		return rational(rFrac, iFrac);
	    }
	    else {
		BigDecimal r = getDecimal(o1);
		BigDecimal i = getDecimal(o2);

		if (rat && MathUtil.isInteger(r) && MathUtil.isInteger(i)) {
		    BigInteger rInt = r.toBigIntegerExact();
		    BigInteger iInt = i.toBigIntegerExact();

		    return rational(rInt, iInt);
		}

		return decimal(r, i);
	    }
	}

	/**
	 * Construct from a map of one or two values, with or without a "rational" flag.
	 *
	 * @param map Any map with "r" and/or "i" keys, and possible "rational" key.
	 * @param rat Whether or not to interpret some values as rational.
	 * @return    The new complex number.
	 * @throws IllegalArgumentException if the input map is null or empty.
	 * @see #REAL_KEY
	 * @see #IMAG_KEY
	 * @see #RATIONAL_KEY
	 */
	public static ComplexNumber fromMap(final Map<String, Object> map, final boolean rat) {
	    if (map == null || map.size() == 0)
		throw new Intl.IllegalArgumentException("math#complex.noEmptyListMap");

	    // Here, we could have "r,i" or "r,theta", with possible "rational" flag
	    boolean isRational = getFlagValue(map.get(RATIONAL_KEY));

	    if (map.containsKey(IMAG_KEY)) {
		if (isRational) {
		    BigFraction rFrac = BigFraction.valueOf(map.get(REAL_KEY));
		    BigFraction iFrac = BigFraction.valueOf(map.get(IMAG_KEY));

		    return new RationalComplexNumber(rFrac, iFrac);
		}
		else {
		    BigDecimal r = getDecimal(map.get(REAL_KEY));
		    BigDecimal i = getDecimal(map.get(IMAG_KEY));

		    if (rat && MathUtil.isInteger(r) && MathUtil.isInteger(i)) {
			BigInteger rInt = r.toBigIntegerExact();
			BigInteger iInt = i.toBigIntegerExact();

			return rational(rInt, iInt);
		    }
		    return new DecimalComplexNumber(r, i);
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
	 * <nl><li>One decimal value, meaning this a complex number of the form {@code (v, 0)}
	 * <li>Two decimal values, meaning a complex number of the form {@code (r, i)}
	 * <li>One fraction value, and one boolean value, meaning a rational complex number of
	 * the form {@code (f, 0)}
	 * <li>Two fraction values, and one boolean value, meaning a rational complex number of
	 * the form {@code (fr, fi)}
	 * </nl>
	 *
	 * @param set Any set of one to three values.
	 * @param rat Whether or not pure integers could be rational values.
	 * @return    The new complex number, if possible.
	 * @throws IllegalArgumentException if the number of values is wrong.
	 */
	public static ComplexNumber fromSet(final Set<Object> set, final boolean rat) {
	    if (set == null || set.size() == 0)
		throw new Intl.IllegalArgumentException("math#complex.noEmptyListMap");
	    if (set.size() > 3)
		throw new Intl.IllegalArgumentException("math#complex.tooManyValues");

	    Iterator<Object> iter = set.iterator();

	    if (set.size() == 1) {
		BigDecimal v = getDecimal(iter.next());

		if (rat && MathUtil.isInteger(v)) {
		    BigInteger vInt = v.toBigIntegerExact();

		    return rational(vInt, BigInteger.ZERO);
		}

		return real(v);
	    }

	    Object o1 = iter.next();
	    Object o2 = iter.next();
	    boolean isRational = false;

	    if (iter.hasNext())
		isRational = getFlagValue(iter.next());

	    if (isRational) {
		BigFraction rFrac = BigFraction.valueOf(o1);
		BigFraction iFrac = BigFraction.valueOf(o2);

		return rational(rFrac, iFrac);
	    }
	    else if (o2 instanceof Boolean) {
		BigFraction vFrac = BigFraction.valueOf(o1);

		return real(vFrac);
	    }
	    else {
		BigDecimal r = getDecimal(o1);
		BigDecimal i = getDecimal(o2);

		if (rat && MathUtil.isInteger(r) && MathUtil.isInteger(i)) {
		    BigInteger rInt = r.toBigIntegerExact();
		    BigInteger iInt = i.toBigIntegerExact();

		    return rational(rInt, iInt);
		}

		return decimal(r, i);
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

	    return decimal(rPart, iPart);
	}

	/**
	 * Construct one of these from a pure real number.
	 *
	 * @param r The pure real value.
	 * @return  A new complex number with no imaginary part.
	 */
	public static ComplexNumber real(final BigDecimal r) {
	    return new DecimalComplexNumber(r, null);
	}

	/**
	 * Construct one of these from a pure real number.
	 *
	 * @param r The pure real value.
	 * @return  A new complex number with no imaginary part.
	 */
	public static ComplexNumber real(final BigInteger r) {
	    return new DecimalComplexNumber(r, null);
	}

	/**
	 * Construct one of these from a pure real fraction.
	 *
	 * @param rFrac The pure real fraction.
	 * @return      A new complex number with no imaginary part.
	 */
	public static ComplexNumber real(final BigFraction rFrac) {
	    return new RationalComplexNumber(rFrac, null);
	}

	/**
	 * Alternate pure real "constructor".
	 *
	 * @param r The pure real value.
	 * @return  A new complex number with no imaginary part.
	 */
	public static ComplexNumber real(final double r) {
	    return real(BigDecimal.valueOf(r));
	}

	/**
	 * Alternate pure real "constructor".
	 *
	 * @param r The pure real value.
	 * @return  A new complex number with no imaginary part.
	 */
	public static ComplexNumber real(final long r) {
	    return real(BigDecimal.valueOf(r));
	}

	/**
	 * Construct one of these from a pure imaginary number.
	 *
	 * @param i The pure imaginary value.
	 * @return  A new complex number with no real part.
	 */
	public static ComplexNumber imaginary(final BigDecimal i) {
	    return new DecimalComplexNumber(null, i);
	}

	/**
	 * Construct one of these from a pure imaginary number.
	 *
	 * @param i The pure imaginary value.
	 * @return  A new complex number with no real part.
	 */
	public static ComplexNumber imaginary(final BigInteger i) {
	    return new DecimalComplexNumber(null, i);
	}

	/**
	 * Construct one of these from a pure imaginary fraction.
	 *
	 * @param iFrac The pure imaginary fraction.
	 * @return      A new complex number with no real part.
	 */
	public static ComplexNumber imaginary(final BigFraction iFrac) {
	    return new RationalComplexNumber(null, iFrac);
	}

	/**
	 * Alternate pure imaginary "constructor".
	 *
	 * @param i The pure imaginary value.
	 * @return  A new complex number with no real part.
	 */
	public static ComplexNumber imaginary(final double i) {
	    return imaginary(BigDecimal.valueOf(i));
	}

	/**
	 * Alternate pure imaginary "constructor".
	 *
	 * @param i The pure imaginary value.
	 * @return  A new complex number with no real part.
	 */
	public static ComplexNumber imaginary(final long i) {
	    return imaginary(BigDecimal.valueOf(i));
	}


	/**
	 * Is this a rational complex number (whose real and imaginary parts
	 * are saved as rational fractions)? A {@link RationalComplexNumber}
	 * returns {@code true}.
	 *
	 * @return Whether this complex number is rational or not.
	 */
	public abstract boolean isRational();

	/**
	 * Is this complex number equal to zero?
	 * <p> Assumes this complex number has been internalized.
	 *
	 * @return {@code true} if the real part equals {@code zero}
	 *         and the imaginary part is {@code null}.
	 */
	public abstract boolean isZero();

	/**
	 * Access the real part of ourselves.
	 *
	 * @return The real part of this complex number (which will be
	 *         {@code 0} for a pure imaginary number).
	 */
	public abstract BigDecimal r();

	/**
	 * Access the imaginary part of ourselves.
	 *
	 * @return The imaginary part of this complex number (which will be
	 *         {@code 0} for a pure real number).
	 */
	public abstract BigDecimal i();

	/**
	 * Access the real fraction part of ourselves.
	 *
	 * @return The real fraction value of this complex number (which will be
	 *         {@code 0} for a pure imaginary.
	 */
	public abstract BigFraction rFrac();

	/**
	 * Access the imaginary fraction part of ourselves.
	 *
	 * @return The imaginary fraction value of this complex number (which will be
	 *         {@code 0} for a pure real.
	 */
	public abstract BigFraction iFrac();

	/**
	 * Get the precision, which is the maximum precision of both parts.
	 *
	 * @return Maximum precision of the two parts.
	 */
	public abstract int precision();

	/**
	 * @return Is this a pure real number (imaginary part is zero)?
	 */
	public abstract boolean isPureReal();

	/**
	 * @return Is this a pure imaginary number (real part is zero)?
	 */
	public abstract boolean isPureImaginary();

	/**
	 * @return Is this a pure Gaussian integer (that is, both real and imaginary parts
	 * are integers)?
	 */
	public abstract boolean isPureInteger();


	/**
	 * Convert to a list of two or three values.
	 *
	 * @return A list with the real part and imaginary part, in that order, with the
	 * optional third element if this is a rational complex number.
	 */
	public abstract List<Object> toList();

	/**
	 * Convert to a map with real and imaginary keys.
	 *
	 * @return {@code Map} with {@link #REAL_KEY} and {@link #IMAG_KEY} entries, and
	 * optional {@link #RATIONAL_KEY}.
	 */
	public abstract Map<String, Object> toMap();

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
	public abstract Set<Object> toSet();


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
	 * is {@code √(r² + i²)}.
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
	    if (isRational() && other.isRational())
		return ((RationalComplexNumber) this).add(other);
	    else
		return toDecimalComplex().add(other.toDecimalComplex());
	}

	/**
	 * Subtract the two complex numbers, this and the other.
	 * <p> The result of <code>( a, b ) - ( c, d )</code> is
	 * <code>( (a-c), (b-d) )</code>.
	 *
	 * @param other The number to subtract from this one.
	 * @param mc    Rounding and precision context for decimal values.
	 * @return      The difference.
	 */
	public ComplexNumber subtract(final ComplexNumber other, final MathContext mc) {
	    if (isRational() && other.isRational())
		return ((RationalComplexNumber) this).subtract(other);
	    else
		return toDecimalComplex().subtract(other.toDecimalComplex(), mc);
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
	    if (isRational() && other.isRational())
		return ((RationalComplexNumber) this).multiply(other);
	    else
		return toDecimalComplex().multiply(other.toDecimalComplex(), mc);
	}

	/**
	 * Compute the complex conjugate of this number, which for
	 * {@code (real, imag)} is {@code (real, -imag)}.
	 *
	 * @return A new complex number which is the conjugate of this one.
	 */
	public abstract ComplexNumber conjugate();

	/**
	 * Negate this value.
	 *
	 * @return A complex number that is the negative of this one.
	 */
	public abstract ComplexNumber negate();

	/**
	 * Divide this complex number by a real number.
	 * <p> The result is {@code (real/p, imag/p)}.
	 *
	 * @param p  The real number to divide by.
	 * @param mc The rounding mode for the result.
	 * @return   A new ComplexNumber with the result.
	 */
	public abstract ComplexNumber divide(final BigDecimal p, final MathContext mc);

	/**
	 * Divide this complex number by a real fraction.
	 * <p> The result is {@code (real/f, imag/f)}.
	 *
	 * @param f  The real fraction to divide by.
	 * @param mc The rounding mode for the result.
	 * @return   A new ComplexNumber with the result.
	 */
	public abstract ComplexNumber divide(final BigFraction f, final MathContext mc);

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
	public abstract ComplexNumber divide(final ComplexNumber other, final MathContext mc);

	/**
	 * Do an "integer" division of this number by the given one. This is the "\" operator.
	 *
	 * @param other The number to divide by.
	 * @param mc    Rounding precision to use for the division.
	 * @return      This divided by other, set to the nearest integer of that result.
	 */
	public abstract ComplexNumber idivide(final ComplexNumber other, final MathContext mc);

	/**
	 * Get the remainder after division, which is {@code c1 - (c1\c2 * c2)}.
	 *
	 * @param other Number to divide by.
	 * @param mc    Rounding precision to use for the decimal result.
	 * @return      Result of {@code this % other}.
	 */
	public ComplexNumber remainder(final ComplexNumber other, final MathContext mc) {
	    ComplexNumber quotient = idivide(other, mc);
	    return subtract(quotient.multiply(other, mc), mc);
	}

	/**
	 * Return both the "integer" quotient from division, and the remainder after division.
	 *
	 * @param other Number to divide by.
	 * @param mc    Rounding precision to use (for decimal results).
	 * @return      Quotient in [0] and remainder in [1].
	 */
	public ComplexNumber[] divideAndRemainder(final ComplexNumber other, final MathContext mc) {
	    ComplexNumber[] results = new ComplexNumber[2];
	    results[0] = idivide(other, mc);
	    results[1] = subtract(results[0].multiply(other, mc), mc);
	    return results;
	}

	/**
	 * Get the modulus of this divided by the given one; differs from the remainder for
	 * negative values.
	 *
	 * @param other Number to divide by.
	 * @param mc    Rounding precision to use for the result.
	 * @return      Result of {@code this mod other}.
	 */
	public ComplexNumber modulus(final ComplexNumber other, final MathContext mc) {
	    return subtract(other.multiply(divide(other, mc).floor(), mc), mc);
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
	    if (isZero())
		return BigInteger.ZERO;

	    if (isPureReal()) {
		int sign = isRational() ? rFrac().signum() : r().signum();
		return BigInteger.valueOf(sign);
	    }

	    return divide(radius(mc), mc);
	}

	/**
	 * Return the dot product of this complex number and the other one.
	 * Result is a scalar, with the real and imaginary parts treated as elements
	 * of a two-element vector.
	 *
	 * @param other Number to compute dot product of.
	 * @param mc    Rounding precision for the multiplications.
	 * @return      The scalar dot product of this · other.
	 */
	public abstract Number dot(final ComplexNumber other, final MathContext mc);

	/**
	 * Return a complex number that is the "ceil" value of this one, meaning
	 * both parts will be the "ceil" value of each, respectively.
	 * <p> Note: the return value will always be decimal (not rational).
	 *
	 * @return The "ceil" value of this complex number.
	 */
	public abstract ComplexNumber ceil();

	/**
	 * Return a complex number that is the "floor" value of this one, meaning
	 * both parts will be the "floor" value of each, respectively.
	 * <p> Note: the return value will always be decimal (not rational).
	 *
	 * @return The "floor" value of this complex number.
	 */
	public abstract ComplexNumber floor();

	/**
	 * Return a complex number which is one bigger than the current one.
	 *
	 * @return One bigger.
	 */
	public abstract ComplexNumber increment();

	/**
	 * Return a complex number which is one smaller than the current one.
	 *
	 * @param mc Precision and rounding settings.
	 * @return   One smaller.
	 */
	public abstract ComplexNumber decrement(final MathContext mc);

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

	    ComplexNumber one = isRational() ? CR_ONE : C_ONE;

	    if (n == 0)
		return one;
	    if (n == 1)
		return this;

	    ComplexNumber result = this;
	    ComplexNumber factor = one;

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

	    BigDecimal rPower = MathUtil.pow(radius, n, mc);

	    ComplexNumber result = polar(rPower, nTheta, mc);

	    // Note: for negative or fractional powers there are multiple roots, but we're
	    // going with the principal root, which should match signs with the input
	    if (negative)
		return result.negate();
	    else
		return result;
	}

	/**
	 * Calculate the square root of this complex number.
	 *
	 * @param mc Math context for rounding and precision.
	 * @return   The first (positive) square root.
	 */
	public ComplexNumber sqrt(final MathContext mc) {
	    BigDecimal a = r();
	    BigDecimal b = i();

	    BigDecimal a2b2 = MathUtil.sqrt(a.multiply(a).add(b.multiply(b)), mc);

	    BigDecimal r = MathUtil.sqrt(a.add(a2b2).divide(D_TWO, mc), mc);
	    BigDecimal s = MathUtil.sqrt(a2b2.subtract(a).divide(D_TWO, mc), mc);

	    // Now adjust the sign of s such that r*s = b/2
	    int signb = b.signum();
	    int signr = r.signum();
	    int signs = s.signum();

	    // b < 0 => r != s
	    // b > 0 -> r == s
	    if (signb != 0) {
		if (signb < 0) {
		    if (signr == signs)
			s = s.negate();
		}
		else {
		    if (signr != signs)
			s = s.negate();
		}
	    }

	    return decimal(r, s);
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
		return new BigDecimal(value.toString());
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
	 * Convert to a pure decimal complex number, regardless of the input type.
	 *
	 * @return A pure decimal complex number (that is, NOT rational).
	 */
	public abstract DecimalComplexNumber toDecimalComplex();

	/**
	 * Get the {@code ComplexNumber} equivalent of the input value.
	 *
	 * @param value Some (presumably compatible) value.
	 * @return      The complex equivalent.
	 * @throws IllegalArgumentException if the object can't be converted.
	 */
	public static ComplexNumber valueOf(final Object value) {
	    return valueOf(value, false);
	}

	/**
	 * Get the {@code ComplexNumber} equivalent of the input value, with the
	 * option to make it rational.
	 *
	 * @param value    Some (presumably compatible) value.
	 * @param rational Flag to make it rational (if possible).
	 * @return         The complex equivalent.
	 * @throws IllegalArgumentException if the object can't be converted.
	 */
	public static ComplexNumber valueOf(final Object value, final boolean rational) {
	    if (value == null)
		return null;

	    if (value instanceof ComplexNumber)
		return (ComplexNumber) value;

	    if (value instanceof List) {
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) value;
		return fromList(list, rational);
	    }
	    if (value instanceof Map) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) value;
		return fromMap(map, rational);
	    }
	    if (value instanceof Set) {
		@SuppressWarnings("unchecked")
		Set<Object> set = (Set<Object>) value;
		return fromSet(set, rational);
	    }

	    if (value instanceof Quaternion) {
		return ((Quaternion) value).toComplex();
	    }

	    if (value instanceof BigFraction) {
		return real((BigFraction) value);
	    }

	    if (value instanceof BigInteger) {
		return rational ? real(new BigFraction((BigInteger) value)) : real((BigInteger) value);
	    }

	    if (rational) {
		BigFraction fValue = BigFraction.valueOf(value);
		if (fValue != null)
		    return real(fValue);

		BigDecimal dValue = getDecimal(value);
		if (dValue != null)
		    return real(dValue);
	    }
	    else {
		BigDecimal dValue = getDecimal(value);
		if (dValue != null)
		    return real(dValue);

		BigFraction fValue = BigFraction.valueOf(value);
		if (fValue != null)
		    return real(fValue);
	    }

	    return parse(value.toString(), rational);
	}


	/**
	 * Parse out and construct a complex number from any of the string forms.
	 * <p> Note: this process recognizes the forms produced by both {@link #toLongString}
	 * and {@link #toPolarString}.
	 *
	 * @param string The supposed string form of a complex number.
	 * @param rat    Whether to produce a rational form or not in certain cases.
	 * @return       The parsed number.
	 * @throws       IllegalArgumentException if it cannot be parsed.
	 */
	public static ComplexNumber parse(final String string, final boolean rat) {
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
		    return rational(rFrac, iFrac);
		}
		else {
		    return imaginary(BigFraction.valueOf(m.group(2)));
		}
	    }
	    else {
		if (m.groupCount() == 5) {
		    // Pure real number
		    BigDecimal r = new BigDecimal(m.group(1));

		    if (rat && MathUtil.isInteger(r)) {
			BigInteger rInt = r.toBigIntegerExact();
			return real(new BigFraction(rInt));
		    }

		    return real(r);
		}
		else if (m.groupCount() == 6) {
		    // Pure imaginary number
		    BigDecimal i = new BigDecimal(m.group(1));

		    if (rat && MathUtil.isInteger(i)) {
			BigInteger iInt = i.toBigIntegerExact();
			return imaginary(new BigFraction(iInt));
		    }

		    return imaginary(i);
		}
		else {
		    BigDecimal rPart = new BigDecimal(m.group(1));
		    String iMult = m.group(7);
		    BigDecimal iPart = (iMult != null) ? new BigDecimal(iMult) : BigDecimal.ONE;

		    if (m.group(6) != null && m.group(6).equals("-"))
			iPart = iPart.negate();

		    if (rat && MathUtil.isInteger(rPart) && MathUtil.isInteger(iPart)) {
			BigInteger rInt = rPart.toBigIntegerExact();
			BigInteger iInt = iPart.toBigIntegerExact();

			return rational(rInt, iInt);
		    }

		    return decimal(rPart, iPart);
		}
	    }
	}

	/**
	 * Extract one of the values by index.
	 *
	 * @param index {@code 0} for the real part, {@code 1} for the imaginary part.
	 * @return      The indexed part of this value.
	 * @throws      IllegalArgumentException if this index value is out of range.
	 */
	public Number part(final int index) {
	    switch (index) {
		case 0: return isRational() ? rFrac() : r();
		case 1: return isRational() ? iFrac() : i();
		default:
		    throw new Intl.IllegalArgumentException("math#complex.badIndex", index);
	    }
	}

	/**
	 * Set one of the values by index.
	 *
	 * @param index {@code 0} for the real part, {@code 1} for the imaginary part.
	 * @param value The new value for the specified part.
	 * @return      New complex number with the specified part updated.
	 * @throws      IllegalArgumentException if this index value is out of range.
	 */
	public ComplexNumber setPart(final int index, final Object value) {
	    switch (index) {
		case 0:
		    return isRational() ? rational(BigFraction.valueOf(value), iFrac())
					: decimal(getDecimal(value), i());
		case 1:
		    return isRational() ? rational(rFrac(), BigFraction.valueOf(value))
					: decimal(r(), getDecimal(value));
		default:
		    throw new Intl.IllegalArgumentException("math#complex.badIndex", index);
	    }
	}

	/**
	 * Return a list of the component values.
	 *
	 * @return New list of the two components.
	 */
	public List<Object> parts() {
	    List<Object> results = new ArrayList<>(2);
	    results.add(isRational() ? rFrac() : r());
	    results.add(isRational() ? iFrac() : i());

	    return results;
	}


	/**
	 * Return an exact {@link BigInteger} value from this complex number, if possible.
	 *
	 * @return The exact {@code BigInteger} representation if this number is pure real with
	 *         no fractional part.
	 * @throws ArithmeticException otherwise.
	 */
	public BigInteger toBigIntegerExact() {
	    if (isPureReal()) {
		return isRational() ? BigFraction.getInteger(rFrac()) : r().toBigIntegerExact();
	    }
	    throw new Intl.ArithmeticException("math#complex.imaginaryInt");
	}

	/**
	 * Return an exact integer value, if possible.
	 *
	 * @return The exact integer value of this number, if possible.
	 * @throws ArithmeticException otherwise.
	 */
	public int intValueExact() {
	    if (isPureReal()) {
		return isRational() ? rFrac().intValueExact() : r().intValueExact();
	    }
	    throw new Intl.ArithmeticException("math#complex.imaginaryInt");
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


	protected abstract String internalToString(final boolean sep, final boolean space);


	/**
	 * Formatted version of {@link #toString} with support for thousands separators.
	 *
	 * @param sep	Whether to format with separators.
	 * @param space Whether to use extra spaces for the fractional form.
	 * @return	The formatted version.
	 */
	public String toFormatString(final boolean sep, final boolean space) {
	    return internalToString(sep, space);
	}

	@Override
	public String toString() {
	    return internalToString(false, true);
	}

	/**
	 * Format an alternate representation of this complex number that looks (more or less)
	 * like: {@code r +- ni} with variations for pure real and pure imaginary values.
	 * <p> Note: this form is recognizable by {@link #parse}.
	 *
	 * @param upperCase Casing for the representation of {@code i}.
	 * @param extra     Always format with zero parts included.
	 * @param sep       Whether to format with thousands separators.
	 * @param space     Whether to use extra spaces for the fractional form.
	 * @return The alternate string representation of this number.
	 */
	public abstract String toLongString(final boolean upperCase, final boolean extra, final boolean sep, final boolean space);


	/**
	 * Convert to a string representation of the polar form, using map notation, and {@code "r"} and {@code "\u0398"}
	 * (for upper case) or {@code "\u03B8"} (for lower case) map keys.
	 * <p> Note: this form is recognizable by {@link #parse}.
	 * <p> Also considering that the theta value is VERY rarely going to be rational even with rational parts, we
	 * will always return decimal values here.
	 *
	 * @param piWorker  Where to find values for PI.
	 * @param upperCase Case to use for the map keys.
	 * @param sep       Whether to use thousands separators.
	 * @param angleMode What trig mode to use for the angle value.
	 * @param mc        Rounding and precision for the conversion to polar values.
	 * @return Polar form (r, theta) of this complex number (as a string).
	 */
	public String toPolarString(final PiWorker piWorker, final boolean upperCase, final boolean sep, final TrigMode angleMode, final MathContext mc) {
	    char r = upperCase ? 'R' : 'r';
	    char theta = upperCase ? '\u0398' : '\u03B8';
	    BigDecimal angle = angleMode.fromRadians(piWorker, theta(mc), mc);

	    return String.format(POLAR_FORMAT,
			r,     Num.formatWithSeparators(radius(mc), sep),
			theta, Num.formatWithSeparators(angle,      sep));
	}

}

/**
 * A rational {@link ComplexNumber} with values stored as {@link BigFraction} and
 * all arithmetic done by them.
 */
class RationalComplexNumber extends ComplexNumber
{
	/**
	 * The fractional real part of this complex number.
	 */
	private BigFraction realFrac;

	/**
	 * The fractional imaginary part of this complex number.
	 */
	private BigFraction imaginaryFrac;


	protected RationalComplexNumber(final BigInteger rInt, final BigInteger iInt) {
	    this(new BigFraction(rInt), new BigFraction(iInt));
	}

	/**
	 * Construct one, given the real and imaginary rational values.
	 *
	 * @param rFrac Value for the real part (can be {@code null} for a pure
	 *              imaginary number (such as {@code 2i}).
	 * @param iFrac Value for the imaginary part (can also be {@code null} for a
	 *              pure real number (such as {@code 17/5}).
	 */
	protected RationalComplexNumber(final BigFraction rFrac, final BigFraction iFrac) {
	    realFrac      = BigFraction.properFraction(rFrac);
	    imaginaryFrac = BigFraction.properFraction(iFrac);
	    internalize();
	}

	/**
	 * Do the internalization to keep our values in a consistent form.
	 * <p> For now, until we need other things, we will remove {@code 0}
	 * values and keep them stored as {@code null} in that part.
	 */
	private void internalize() {
	    if (realFrac != null && realFrac.isZero())
		realFrac = null;
	    if (imaginaryFrac != null && imaginaryFrac.isZero())
		imaginaryFrac = null;

	    if (realFrac == null && imaginaryFrac == null)
		realFrac = BigFraction.F_ZERO;

	    if (realFrac != null)
		realFrac.setAlwaysProper(true);
	    if (imaginaryFrac != null)
		imaginaryFrac.setAlwaysProper(true);
	}

	@Override
	public boolean isRational() {
	    return true;
	}

	@Override
	public boolean isZero() {
	    return realFrac != null && realFrac.isZero() && imaginaryFrac == null;
	}

	@Override
	public boolean isPureReal() {
	    return imaginaryFrac == null;
	}

	@Override
	public boolean isPureImaginary() {
	    return realFrac == null;
	}

	@Override
	public boolean isPureInteger() {
	    return rFrac().isWholeNumber() && iFrac().isWholeNumber();
	}

	@Override
	public DecimalComplexNumber toDecimalComplex() {
	    return new DecimalComplexNumber(getDecimal(rFrac()), getDecimal(iFrac()));
	}

	@Override
	public List<Object> toList() {
	    List<Object> list = new ArrayList<>();

	    list.add(rFrac());
	    list.add(iFrac());
	    // "rational" flag
	    list.add(true);

	    return list;
	}

	@Override
	public Map<String, Object> toMap() {
	    Map<String, Object> map = new LinkedHashMap<>();

	    map.put(REAL_KEY, rFrac());
	    map.put(IMAG_KEY, iFrac());
	    map.put(RATIONAL_KEY, true);

	    return map;
	}

	@Override
	public Set<Object> toSet() {
	    Set<Object> set = new LinkedHashSet<>();

	    // This could result in two or three values
	    set.add(rFrac());
	    set.add(iFrac());
	    set.add(true);

	    return set;
	}

	@Override
	public BigDecimal r() {
	    return realFrac == null ? BigDecimal.ZERO : realFrac.toDecimal();
	}

	@Override
	public BigDecimal i() {
	    return imaginaryFrac == null ? BigDecimal.ZERO : imaginaryFrac.toDecimal();
	}

	@Override
	public BigFraction rFrac() {
	    return realFrac == null ? BigFraction.F_ZERO : realFrac;
	}

	@Override
	public BigFraction iFrac() {
	    return imaginaryFrac == null ? BigFraction.F_ZERO : imaginaryFrac;
	}

	@Override
	public int precision() {
	    MaxInt prec;

	    prec = MaxInt.of(BigFraction.F_ZERO.precision());

	    if (realFrac != null)
		prec.set(realFrac.precision());
	    if (imaginaryFrac != null)
		prec.set(imaginaryFrac.precision());

	    return prec.get();
	}

	public ComplexNumber add(final ComplexNumber other) {
	    BigFraction a1f = rFrac();
	    BigFraction a2f = other.rFrac();
	    BigFraction b1f = iFrac();
	    BigFraction b2f = other.iFrac();

	    return new RationalComplexNumber(a1f.add(a2f), b1f.add(b2f));
	}

	public ComplexNumber subtract(final ComplexNumber other) {
	    BigFraction a1f = rFrac();
	    BigFraction a2f = other.rFrac();
	    BigFraction b1f = iFrac();
	    BigFraction b2f = other.iFrac();

	    return new RationalComplexNumber(a1f.subtract(a2f), b1f.subtract(b2f));
	}

	public ComplexNumber multiply(final ComplexNumber other) {
	    BigFraction xf = rFrac();
	    BigFraction yf = iFrac();
	    BigFraction uf = other.rFrac();
	    BigFraction vf = other.iFrac();

	    BigFraction rFracTerm = xf.multiply(uf).subtract(yf.multiply(vf));
	    BigFraction iFracTerm = xf.multiply(vf).add(yf.multiply(uf));

	    return rational(rFracTerm, iFracTerm);
	}

	@Override
	public ComplexNumber conjugate() {
	    if (isPureReal())
		return this;

	    return new RationalComplexNumber(realFrac, imaginaryFrac.negate());
	}

	@Override
	public ComplexNumber negate() {
	    if (isZero())
		return this;

	    if (realFrac == null)
		return new RationalComplexNumber(realFrac, imaginaryFrac.negate());
	    else if (imaginaryFrac == null)
		return new RationalComplexNumber(realFrac.negate(), imaginaryFrac);
	    else
		return new RationalComplexNumber(realFrac.negate(), imaginaryFrac.negate());
	}

	@Override
	public ComplexNumber divide(final BigDecimal p, final MathContext mc) {
	    BigFraction pFrac = BigFraction.properFraction(p);
	    return new RationalComplexNumber(rFrac().divide(pFrac), iFrac().divide(pFrac));
	}

	@Override
	public ComplexNumber divide(final BigFraction f, final MathContext mc) {
	    return new RationalComplexNumber(rFrac().divide(f), iFrac().divide(f));
	}

	@Override
	public ComplexNumber divide(final ComplexNumber other, final MathContext mc) {
	    ComplexNumber conjugate = other.conjugate();
	    BigFraction divisor = other.multiply(conjugate, mc).rFrac();

	    return multiply(conjugate, mc).divide(divisor, mc);
	}

	@Override
	public ComplexNumber idivide(final ComplexNumber other, final MathContext mc) {
	    ComplexNumber fullResult = divide(other, mc);

	    return new RationalComplexNumber(fullResult.rFrac().toNearestInteger(), fullResult.iFrac().toNearestInteger());
	}

	@Override
	public Number dot(final ComplexNumber other, final MathContext mc) {
	    BigFraction x1 = rFrac();
	    BigFraction y1 = iFrac();
	    BigFraction x2 = other.rFrac();
	    BigFraction y2 = other.iFrac();

	    return x1.multiply(x2).add(y1.multiply(y2));
	}

	@Override
	public ComplexNumber ceil() {
	    BigFraction rFrac = rFrac();
	    BigFraction iFrac = iFrac();

	    return new RationalComplexNumber(rFrac.ceil(), iFrac.ceil());
	}

	@Override
	public ComplexNumber floor() {
	    BigFraction rFrac = rFrac();
	    BigFraction iFrac = iFrac();

	    return new RationalComplexNumber(rFrac.floor(), iFrac.floor());
	}

	@Override
	public ComplexNumber increment() {
	    return add(CR_ONE);
	}

	@Override
	public ComplexNumber decrement(final MathContext mc) {
	    return subtract(CR_ONE);
	}

	@Override
	public int hashCode() {
	    if (realFrac == null)
		return imaginaryFrac.hashCode();
	    else if (imaginaryFrac == null)
		return realFrac.hashCode();
	    else
		return realFrac.hashCode() ^ imaginaryFrac.hashCode();
	}

	@Override
	protected String internalToString(final boolean sep, final boolean space) {
	    return String.format(NORMAL_FORMAT, rFrac().toFormatString(sep, space), iFrac().toFormatString(sep, space));
	}

	@Override
	public String toLongString(final boolean upperCase, final boolean extra, final boolean sep, final boolean space) {
	    char i = upperCase ? '\u2110' : '\u2148';
	    BigFraction re = realFrac == null && extra ? BigFraction.ZERO : realFrac;
	    BigFraction im = imaginaryFrac == null && extra ? BigFraction.ZERO : imaginaryFrac;

	    if (re == null && !extra) {
		if (im.equals(BigFraction.ONE))
		    return String.format(I_POS_FORMAT, i);
		else if (im.equals(BigFraction.MINUS_ONE))
		    return String.format(I_NEG_FORMAT, i);
		else
		    return String.format(IMAG_FORMAT, im.toFormatString(sep, space), i);
	    }
	    else if (im == null && !extra) {
		return re.toFormatString(sep, space);
	    }
	    else {
		if (im.signum() < 0)
		    return String.format(LONG_NEG_FORMAT,
			    re.toFormatString(sep, space),
			    im.abs().toFormatString(sep, space), i);
		else
		    return String.format(LONG_POS_FORMAT,
			    re.toFormatString(sep, space),
			    im.toFormatString(sep, space), i);
	    }
	}
}

/**
 * A decimal {@link ComplexNumber} with values stored as {@link BigDecimal} and all
 * arithmetic done that way.
 */
class DecimalComplexNumber extends ComplexNumber
{
	/**
	 * The real part of this complex number.
	 */
	private BigDecimal realPart;

	/**
	 * The imaginary part of this complex number.
	 */
	private BigDecimal imaginaryPart;


	/**
	 * Default constructor, with a real value of {@code (0,0)}.
	 */
	public DecimalComplexNumber() {
	    realPart      = null;
	    imaginaryPart = null;
	    internalize();
	}

	/**
	 * Construct one, given the real and imaginary values.
	 *
	 * @param r Value for the real part (can be {@code null} for a pure
	 *          imaginary number (such as {@code 2i}).
	 * @param i Value for the imaginary part (can also be {@code null} for a
	 *          pure real number (such as {@code 3.5}).
	 */
	public DecimalComplexNumber(final BigDecimal r, final BigDecimal i) {
	    realPart      = r;
	    imaginaryPart = i;
	    internalize();
	}

	/**
	 * Construct one, given the real and imaginary values.
	 *
	 * @param r Value for the real part (can be {@code null} for a pure
	 *          imaginary number (such as {@code 2i}).
	 * @param i Value for the imaginary part (can also be {@code null} for a
	 *          pure real number (such as {@code 3.5}).
	 */
	public DecimalComplexNumber(final BigInteger r, final BigInteger i) {
	    realPart      = r == null ? null : new BigDecimal(r);
	    imaginaryPart = i == null ? null : new BigDecimal(i);
	    internalize();
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
	public DecimalComplexNumber(final double r, final double i) {
	    realPart      = BigDecimal.valueOf(r);
	    imaginaryPart = BigDecimal.valueOf(i);
	    internalize();
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
	public DecimalComplexNumber(final long r, final long i) {
	    realPart      = BigDecimal.valueOf(r);
	    imaginaryPart = BigDecimal.valueOf(i);
	    internalize();
	}

	/**
	 * Do the internalization to keep our values in a consistent form.
	 * <p> For now, until we need other things, we will remove {@code 0}
	 * values and keep them stored as {@code null} in that part.
	 */
	private void internalize() {
	    if (realPart != null && realPart.equals(BigDecimal.ZERO))
		realPart = null;
	    if (imaginaryPart != null && imaginaryPart.equals(BigDecimal.ZERO))
		imaginaryPart = null;

	    // Make sure both parts are not null (zero)
	    if (realPart == null && imaginaryPart == null)
		realPart = BigDecimal.ZERO;
	}

	@Override
	public boolean isRational() {
	    return false;
	}

	@Override
	public boolean isZero() {
	    return realPart != null && realPart.equals(BigDecimal.ZERO) && imaginaryPart == null;
	}

	@Override
	public boolean isPureReal() {
	    return imaginaryPart == null;
	}

	@Override
	public boolean isPureImaginary() {
	    return realPart == null;
	}

	@Override
	public boolean isPureInteger() {
	    return MathUtil.isInteger(r()) && MathUtil.isInteger(i());
	}

	@Override
	public DecimalComplexNumber toDecimalComplex() {
	    return this;
	}

	@Override
	public List<Object> toList() {
	    List<Object> list = new ArrayList<>();

	    list.add(r());
	    list.add(i());

	    return list;
	}

	@Override
	public Map<String, Object> toMap() {
	    Map<String, Object> map = new LinkedHashMap<>();

	    map.put(REAL_KEY, r());
	    map.put(IMAG_KEY, i());

	    return map;
	}

	@Override
	public Set<Object> toSet() {
	    Set<Object> set = new LinkedHashSet<>();

	    // This could result in one or two values
	    set.add(r());
	    set.add(i());

	    return set;
	}

	@Override
	public BigDecimal r() {
	    return realPart == null ? BigDecimal.ZERO : realPart;
	}

	@Override
	public BigDecimal i() {
	    return imaginaryPart == null ? BigDecimal.ZERO : imaginaryPart;
	}

	@Override
	public BigFraction rFrac() {
	    return realPart == null ? BigFraction.F_ZERO : BigFraction.properFraction(realPart);
	}

	@Override
	public BigFraction iFrac() {
	    return imaginaryPart == null ? BigFraction.F_ZERO : BigFraction.properFraction(imaginaryPart);
	}

	@Override
	public int precision() {
	    MaxInt prec;

	    prec = MaxInt.of(BigDecimal.ZERO.precision());

	    if (realPart != null)
		prec.set(realPart.precision());
	    if (imaginaryPart != null)
		prec.set(imaginaryPart.precision());

	    return prec.get();
	}

	public ComplexNumber add(final ComplexNumber other) {
	    BigDecimal a1 = r();
	    BigDecimal a2 = other.r();
	    BigDecimal b1 = i();
	    BigDecimal b2 = other.i();

	    return new DecimalComplexNumber(a1.add(a2), b1.add(b2));
	}

	public ComplexNumber subtract(final ComplexNumber other, final MathContext mc) {
	    BigDecimal a1 = r();
	    BigDecimal a2 = other.r();
	    BigDecimal b1 = i();
	    BigDecimal b2 = other.i();

	    BigDecimal rTerm = MathUtil.fixup(a1.subtract(a2), mc);
	    BigDecimal iTerm = MathUtil.fixup(b1.subtract(b2), mc);

	    return new DecimalComplexNumber(rTerm, iTerm);
	}

	public ComplexNumber multiply(final ComplexNumber other, final MathContext mc) {
	    BigDecimal x = r();
	    BigDecimal y = i();
	    BigDecimal u = other.r();
	    BigDecimal v = other.i();

	    BigDecimal rTerm = MathUtil.fixup(x.multiply(u).subtract(y.multiply(v), mc), mc);
	    BigDecimal iTerm = MathUtil.fixup(x.multiply(v).add(y.multiply(u), mc), mc);

	    return new DecimalComplexNumber(rTerm, iTerm);
	}

	@Override
	public ComplexNumber conjugate() {
	    if (isPureReal())
		return this;

	    return new DecimalComplexNumber(realPart, imaginaryPart.negate());
	}

	@Override
	public ComplexNumber negate() {
	    if (isZero())
		return this;

	    if (realPart == null)
		return new DecimalComplexNumber(realPart, imaginaryPart.negate());
	    else if (imaginaryPart == null)
		return new DecimalComplexNumber(realPart.negate(), imaginaryPart);
	    else
		return new DecimalComplexNumber(realPart.negate(), imaginaryPart.negate());
	}

	@Override
	public ComplexNumber divide(final BigDecimal p, final MathContext mc) {
	    return new DecimalComplexNumber(r().divide(p, mc), i().divide(p, mc));
	}

	@Override
	public ComplexNumber divide(final BigFraction f, final MathContext mc) {
	    BigDecimal fd = f.toDecimal(mc);
	    return new DecimalComplexNumber(r().divide(fd, mc), i().divide(fd, mc));
	}

	@Override
	public ComplexNumber divide(final ComplexNumber other, final MathContext mc) {
	    ComplexNumber conjugate = other.conjugate();
	    BigDecimal divisor = other.multiply(conjugate, mc).r();

	    return multiply(conjugate, mc).divide(divisor, mc);
	}

	@Override
	public ComplexNumber idivide(final ComplexNumber other, final MathContext mc) {
	    ComplexNumber fullResult = divide(other, mc);

	    return new DecimalComplexNumber(MathUtil.round(fullResult.r(), 0), MathUtil.round(fullResult.i(), 0));
	}

	@Override
	public Number dot(final ComplexNumber other, final MathContext mc) {
	    BigDecimal x1 = r();
	    BigDecimal y1 = i();
	    BigDecimal x2 = other.r();
	    BigDecimal y2 = other.i();

	    // Note: retain as much precision as possible until the final addition
	    return x1.multiply(x2).add(y1.multiply(y2), mc);
	}

	@Override
	public ComplexNumber ceil() {
	    BigDecimal rDec = r();
	    BigDecimal iDec = i();

	    return new DecimalComplexNumber(MathUtil.ceil(rDec), MathUtil.ceil(iDec));
	}

	@Override
	public ComplexNumber floor() {
	    BigDecimal rDec = r();
	    BigDecimal iDec = i();

	    return new DecimalComplexNumber(MathUtil.floor(rDec), MathUtil.floor(iDec));
	}

	@Override
	public ComplexNumber increment() {
	    return add(C_ONE);
	}

	@Override
	public ComplexNumber decrement(final MathContext mc) {
	    return subtract(C_ONE, mc);
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
	protected String internalToString(final boolean sep, final boolean space) {
	    return String.format(NORMAL_FORMAT, Num.formatWithSeparators(r(), sep), Num.formatWithSeparators(i(), sep));
	}

	@Override
	public String toLongString(final boolean upperCase, final boolean extra, final boolean sep, final boolean space) {
	    char i = upperCase ? '\u2110' : '\u2148';
	    BigDecimal re = realPart == null && extra ? BigDecimal.ZERO : realPart;
	    BigDecimal im = imaginaryPart == null && extra ? BigDecimal.ZERO : imaginaryPart;

	    if (re == null && !extra) {
		if (im.equals(BigDecimal.ONE))
		    return String.format(I_POS_FORMAT, i);
		else if (im.equals(D_MINUS_ONE))
		    return String.format(I_NEG_FORMAT, i);
		else
		    return String.format(IMAG_FORMAT, Num.formatWithSeparators(im, sep), i);
	    }
	    else if (im == null && !extra) {
		return Num.formatWithSeparators(re, sep);
	    }
	    else {
		if (im.signum() < 0)
		    return String.format(LONG_NEG_FORMAT,
			    Num.formatWithSeparators(re, sep),
			    Num.formatWithSeparators(im.abs(), sep), i);
		else
		    return String.format(LONG_POS_FORMAT,
			    Num.formatWithSeparators(re, sep),
			    Num.formatWithSeparators(im, sep), i);
	    }
	}
}

