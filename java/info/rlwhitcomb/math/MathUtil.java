/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2025 Roger L. Whitcomb.
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
 *	Various static methods for trig, log, and other mathematical calculations.
 *
 * History:
 *  26-Mar-21 rlw ----	Moved out of NumericUtil into this separate class.
 *  27-Mar-21 rlw ----	Add "ePower" method (which is e**x, or anti-logarithm).
 *  27-Mar-21 rlw ----	Clean up code in "pow()"
 *  29-Mar-21 rlw ----	Implement simpler, faster "ln2" function.
 *			Rename the resource strings.
 *  30-Mar-21 rlw ----	Implement Taylor series for "ln" function. Clean up "pow" and "ePower".
 *  08-Apr-21 rlw ----	Move the "round" function from Calc into here.
 *  26-Apr-21 rlw ----	Tweak some error messages.
 *  07-Jul-21 rlw ----	Make the class final.
 *  20-Sep-21 rlw ----	Add 'tenPower' method (like 'ePower').
 *  05-Oct-21 rlw ----	Make "fixup" method that does "round" and "stripTrailingZeros".
 *  07-Oct-21 rlw ----	Fix operation of "round" when rounding to more precision than the original.
 *  18-Nov-21 rlw #95	Add calculation of "phi".
 *  01-Dec-21 rlw #95	Add "ratphi" and "fib2" to support it.
 *  29-Dec-21 rlw #188	Add "ceil" and "floor" methods.
 *  01-Feb-22 rlw #231	Use new Constants class values instead of our own.
 *  08-Feb-22 rlw #235	Add "atan2" code.
 *  14-Apr-22 rlw #273	Move to "math" package.
 *  08-Jul-22 rlw #393	Cleanup imports.
 *  15-Sep-22 rlw #485	Add "modulus" function.
 *  30-Sep-22 rlw ----	Enlarge the "ratphi" table up to precision of 1,000.
 *		  #288	Add a method to return rational values of pi up to a certain precision.
 *  01-Oct-22 rlw #288	Add source links to the PI_VALUES table, rename "piFraction" to "ratpi".
 *  03-Oct-22 rlw #497	Methods to get a MathContext for division particularly for large dividends.
 *  06-Oct-22 rlw #501	BigDecimal to radix conversion.
 *  08-Oct-22 rlw #501	Radix back to BigDecimal conversion.
 *  12-Oct-22 rlw #513	Move Logging to new package.
 *                #514	Move text resources out of "util" package to here.
 *  19-Dec-22 rlw #79	Move BigDecimal "random" function into here.
 *  22-Dec-22 rlw #79	More work on fixing the distribution of random numbers.
 *  27-Dec-22 rlw ----	New varargs "minimum" and "maximum" (int) methods.
 *  05-Jan-23 rlw #558	"divideContext" for quaternions.
 *  19-Jun-23 rlw #613	Make MAX_PRIME the square of MAX_INT to expand the range
 *			of prime checking and factoring.
 *			Some small optimizations around checking for zeros.
 *			Major optimizations in constructing the prime sieve.
 *  14-Dec-23 rlw ----	Use MaxInt.
 *  15-Feb-24 rlw #654	Simple additional test in prime number calculations to avoid exceptions.
 *  05-Mar-24 rlw ----	Optimization in "pow" for 10**n.
 *  14-May-24 rlw #674	New "sqrt2" method to return a complex result if the value is negative.
 *  19-Feb-25 rlw #708	Fix "round()".
 *		  #710	Add "harmonic()" to compute harmonic numbers; rework factorial calculations.
 *			New "intValueExact()" method also.
 *		  ----	New constructors for MinInt and MaxInt.
 *  26-Mar-25 rlw ----	Move "isInteger()" from ClassUtil into here.
 *  12-Apr-25 rlw ----	Add "fixup" to several results.
 *			Extra code in "pow()" to use "ePower" if the base is "e"; increase precision
 *			in there for better results.
 *  01-May-25 rlw #716	New constructors for ComplexNumber.
 */
package info.rlwhitcomb.math;

import info.rlwhitcomb.logging.Logging;
import info.rlwhitcomb.math.MaxInt;
import info.rlwhitcomb.util.ClassUtil;
import info.rlwhitcomb.util.DynamicArray;
import info.rlwhitcomb.util.Intl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static info.rlwhitcomb.util.Constants.*;


/**
 * A static class used for trigonometric and logarithmic calculations, as well as
 * other "mathematical" operations, such as square root, Bernoulli numbers, factorial,
 * and etc.
 */
public final class MathUtil
{
	private static final Logging logger = new Logging(MathUtil.class);


	/**
	 * A rational approximation of PI good to ~25 decimal digits.
	 * This is the fastest way to calculate the value for such small precision.
	 * <p> Sourced from: <a href="http://oeis.org/A002485">A002485</a> and
	 * <a href="http://oeis.org/A002486">A002486</a>.
	 */
	private static final BigFraction PI_APPROX = new BigFraction(8958937768937L, 2851718461558L);
	/** The largest set of calculated PI digits so far. */
	private static String PI_DIGITS = null;
	/** The previously calculated PI value (if any); cached to eliminate repeated costly calculations. */
	private static BigDecimal CALCULATED_PI = null;
	/* Some related values calculated at the same time (for convenience). */
	private static BigDecimal TWO_PI;
	private static BigDecimal MINUS_TWO_PI;
	private static BigDecimal PI_OVER_TWO;
	private static BigDecimal MINUS_PI_OVER_TWO;
	private static BigDecimal PI_OVER_FOUR;
	private static BigDecimal MINUS_PI_OVER_FOUR;

	/** A rounding context to round up to the next highest integer. */
	private static final MathContext MC_ONE = new MathContext(1);

	/** Number of bits per base-ten digit. */
	private static final double BITS_PER_DIGIT = Math.log(10.0) / Math.log(2.0);

	/**
	 * Our provider of random values.
	 */
	private static Random random = null;


	/**
	 * Since this is a static class, make the constructor private so no one
	 * can instantiate it.
	 */
	private MathUtil() {
	}


	/**
	 * Return a new {@link MathContext} with the precision increased/decreased by the
	 * given amount.
	 *
	 * @param mc   The root context to update.
	 * @param incr Amount to add/subtract from the given context's precision.
	 * @return     A new context with updated precision, and the same rounding as the original.
	 */
	private static MathContext newPrecision(final MathContext mc, final int incr) {
	    return new MathContext(mc.getPrecision() + incr, mc.getRoundingMode());
	}


	/**
	 * Round up a value to the next highest power of two.
	 *
	 * @param n Any non-negative number.
	 * @return The next highest power of two greater or equal to
	 * the input.
	 */
	public static int roundUpPowerTwo(final int n) {
	    int p = 1;

	    // Check for exact power of two
//	    if (n != 0 && (n & (n - 1)) == 0)
//		return n;

	    while (p < n)
		p <<= 1;

	    return p;
	}


	/**
	 * Determine if the given object is an "integer" type, which includes the built-in
	 * integers, as well as the {@link BigInteger} type and {@link BigDecimal} values
	 * with no decimal part.
	 *
	 * @param obj The object to check.
	 * @return    Whether or not this object is an integer type.
	 */
	public static boolean isInteger(final Object obj) {
	    if (obj instanceof Number) {
		Class<?> clz = obj.getClass();
		if (clz == BigInteger.class)
		    return true;
		if (clz == Integer.class || clz == Integer.TYPE)
		    return true;
		if (clz == Long.class || clz == Long.TYPE)
		    return true;
		if (clz == Short.class || clz == Short.TYPE)
		    return true;
		if (clz == Byte.class || clz == Byte.TYPE)
		    return true;
		if (clz == BigDecimal.class) {
		    return isInteger((BigDecimal) obj);
		}
	    }

	    return false;
	}

	/**
	 * Another flavor of {@link #isInteger(Object)} that just works for {@link BigDecimal}.
	 *
	 * @param bd A decimal number that is potentially just an integer.
	 * @return   Whether it really is just an integer (or not).
	 */
	public static boolean isInteger(final BigDecimal bd) {
	    return bd.stripTrailingZeros().scale() <= 0;
	}

	/**
	 * Get an exact integer value of the given {@link Number} input, throwing up if the
	 * value is not exact.
	 *
	 * @param num	Input value to convert.
	 * @return	Exact integer equivalent.
	 * @throws	ArithmeticException if the value overflows {@code int} range, or
	 *		if the input value is fractional and can't be converted exactly.
	 */
	public static final int intValueExact(final Number num) {
	    Class<?> cls = num.getClass();
	    if (cls == Integer.class || cls == Integer.TYPE)
		return ((Integer) num).intValue();
	    if (cls == Long.class || cls == Long.TYPE) {
		long lValue = ((Long) num).longValue();
		if (lValue <= Integer.MAX_VALUE && lValue >= Integer.MIN_VALUE)
		    return (int) lValue;
		throw new Intl.ArithmeticException("math#math.nOutOfRange", lValue);
	    }
	    if (cls == Float.class || cls == Float.TYPE || cls == Double.class || cls == Double.TYPE) {
		double dValue = num.doubleValue();
		if (dValue != Math.floor(dValue))
		    throw new Intl.ArithmeticException("math#math.math.fOutOfRange", dValue);
		if (dValue <= (double) Integer.MAX_VALUE && dValue >= (double) Integer.MIN_VALUE)
		    return (int) dValue;
		throw new Intl.ArithmeticException("math#math.fOutOfRange", dValue);
	    }
	    if (cls == BigInteger.class)
		return ((BigInteger) num).intValueExact();
	    if (cls == BigDecimal.class)
		return ((BigDecimal) num).intValueExact();
	    if (cls == BigFraction.class)
		return ((BigFraction) num).intValueExact();
	    if (cls == ComplexNumber.class)
		return ((ComplexNumber) num).intValueExact();
	    if (cls == Quaternion.class)
		return ((Quaternion) num).intValueExact();

	    throw new Intl.ArithmeticException("math#math.unsupportedType", num.toString());
	}


	/**
	 * Round a value to the specified number of fractional places, no matter how many integer
	 * digits there are.  This is different than simply rounding to a given precision, which
	 * tracks the total number of digits.
	 * <p><code>fPlaces</code> is going to be the number of fractional digits to round to:
         * 0 = round to an integer, 1 to x.y, 2 to x.yy, etc.
         * and a negative number will round above the decimal point, as in: -2 to x00.
	 * <p> So, if precision is the number of total digits we keep, and scale is how far left
	 * of the rightmost digit the decimal point is situated, then <code>(precision - scale)</code>
	 * is the number of integer (whole number) digits, then we can add that to "fPlaces" to get
	 * the {@link MathContext} precision to use for rounding here.
	 * <p> Also it appears that rounding to 0 means no change, so we set a minimum value of one
	 * to ensure that *some* rounding always occurs, such that 0.714... rounded to -2 will give
	 * 0.7, not retain the 0.714... value.
	 *
	 * @param value   The incoming value to round.
	 * @param fPlaces The number of fractional digits to round to.
	 * @return        The incoming value rounded as above.
	 */
	public static BigDecimal round(final BigDecimal value, final int fPlaces) {
	    if (fPlaces <= 0)
		return value.setScale(fPlaces, RoundingMode.HALF_UP);

	    int prec   = value.precision();
	    int scale  = value.scale();
	    int places = (prec - scale) + fPlaces;

	    if (places <= 0)
		return value.setScale(places, RoundingMode.HALF_UP);

	    return value.round(new MathContext(places));
	}


	/**
	 * From the given "divide" precision and the precision of the dividend, get the "best"
	 * actual precision to use for the division.
	 *
	 * @param dividend	The dividend of a division operation.
	 * @param mc		Probably limited precision context.
	 * @return		The max of the dividend's precision and the context precision.
	 */
	public static MathContext divideContext(final BigDecimal dividend, final MathContext mc) {
	    return new MathContext(Math.max(dividend.precision(), mc.getPrecision()), mc.getRoundingMode());
	}


	/**
	 * From the given "divide" precision and the precision of the dividend, get the "best"
	 * actual precision to use for the division.
	 *
	 * @param dividend	The dividend of a complex division operation.
	 * @param mc		Probably limited precision context.
	 * @return		The max of the dividend's precision and the context precision.
	 */
	public static MathContext divideContext(final ComplexNumber dividend, final MathContext mc) {
	    return new MathContext(Math.max(dividend.precision(), mc.getPrecision()), mc.getRoundingMode());
	}


	/**
	 * From the given "divide" precision and the precision of the dividend, get the "best"
	 * actual precision to use for the division.
	 *
	 * @param dividend	The dividend of a quaternion division operation.
	 * @param mc		Probably limited precision context.
	 * @return		The max of the dividend's precision and the context precision.
	 */
	public static MathContext divideContext(final Quaternion dividend, final MathContext mc) {
	    return new MathContext(Math.max(dividend.precision(), mc.getPrecision()), mc.getRoundingMode());
	}


	/**
	 * Round the final value to the given precision and strip trailing zeros.
	 *
	 * @param result The final result of a calculation, ready to be fixed up and returned to caller.
	 * @param mc     The rounding mode and precision.
	 * @return       Final result rounded as specified and with trailing (superfluous) zeros removed.
	 */
	public static BigDecimal fixup(final BigDecimal result, final MathContext mc) {
	    return result.round(mc).stripTrailingZeros();
	}


	/**
	 * @return The result of the base to the exp power, done in <code>BigDecimal</code>
	 * precision.
	 * @param base     The number to raise to the given power.
	 * @param inputExp The power to raise the number to.
	 * @param mc       Precision and rounding for the result.
	 * @throws IllegalArgumentException if the exponent is infinite or not-a-number.
	 */
	public static BigDecimal pow(final BigDecimal base, final double inputExp, final MathContext mc) {
	    double exp = inputExp;
	    if (Double.isNaN(exp) || Double.isInfinite(exp))
		throw new Intl.IllegalArgumentException("math#numeric.outOfRange");

	    if (exp == 0.0d)
		return BigDecimal.ONE;

	    boolean reciprocal = false;
	    if (exp < 0) {
		reciprocal = true;
		exp = -exp;
	    }

	    int intExp     = (int) Math.floor(exp);
	    double fracExp = exp - (double) intExp;

	    BigDecimal result;

	    // Turn an integer power of two into a "setBit" on a BigInteger
	    if (base.equals(D_TWO) && (double) intExp == inputExp) {
		BigInteger value = BigInteger.ZERO.setBit(intExp);
		result = new BigDecimal(value);
	    }
	    // Turn an integer power of ten into a simple scale change
	    else if (base.equals(D_TEN) && (double) intExp == inputExp) {
		result = BigDecimal.ONE.scaleByPowerOfTen(intExp);
	    }
	    else if (e(mc).equals(base)) {
		result = ePower(new BigDecimal(exp), mc);
	    }
	    else {
		// Do the integer power part
		result = base.pow(intExp);

		// If there is any fractional exponent, multiply that in
		// (for now, use doubles for this, until we get the real code in place)
		if (fracExp != 0.0d) {
		    // 2.14**2.14 = 2.14**2 * 2.14**.14
		    BigDecimal fracResult = new BigDecimal(Math.pow(base.doubleValue(), fracExp));
		    result = result.multiply(fracResult);
		}
	    }

	    if (reciprocal)
		result = BigDecimal.ONE.divide(result, mc);
	    else
		result = fixup(result, mc);

	    return result;
	}


	/**
	 * @return The result of the base to the exp power, done in <code>BigInteger</code>,
	 * or <code>BigDecimal</code> precision depending on the value of the exponent.
	 * @param base The number to raise to the given power.
	 * @param exp The power to raise the number to.
	 * @param mc The precision and rounding mode for the result.
	 * @throws IllegalArgumentException if the exponent is infinite, or not-a-number.
	 */
	public static Number pow(final BigInteger base, final double exp, final MathContext mc) {
	    if (Double.isNaN(exp) || Double.isInfinite(exp))
		throw new Intl.IllegalArgumentException("math#numeric.outOfRange");
	    if (exp == 0.0d)
		return BigInteger.ONE;

	    // Test for negative or fractional powers and convert to BigDecimal for those cases
	    double wholeExp = Math.floor(exp);
	    if (exp < 0.0d || wholeExp != exp) {
		return pow(new BigDecimal(base), exp, mc);
	    }

	    int intExp = (int) wholeExp;

	    // Turn a power of two into a "setBit"
	    if (base.equals(I_TWO)) {
		return BigInteger.ZERO.setBit(intExp);
	    }

	    return base.pow(intExp);
	}


	/**
	 * @return A BigInteger of 10**pow.
	 *
	 * @param pow	The power of 10 we need.
	 */
	public static BigInteger tenPower(final int pow) {
	    return BigInteger.TEN.pow(pow);
	}


	/**
	 * Table of small factorial values ({@link #intFactorial} uses this).
	 */
	private static final long FACTORIALS[] = {
	    1L, 1L, 2L, 6L, 24L, 120L, 720L, 5040L, 40320L, 362880L, 3628800L,
	    39916800L, 479001600L, 6227020800L, 87178291200L, 1307674368000L,
	    20922789888000L, 355687428096000L, 6402373705728000L, 121645100408832000L, 2432902008176640000L
	};


	/**
	 * Compute an integer factorial, using some shortcuts to make things faster.
	 *
	 * @param base	The integer base (n).
	 * @return	Value of {@code n!} as a {@link BigInteger}.
	 * @throws	IllegalArgumentException if the base is negative.
	 */
	public static BigInteger intFactorial(final long base) {
	    if (base < 0L)
		throw new Intl.IllegalArgumentException("math#math.math.nOutOfRange", base);

	    // Start with the (small) static lookup table (up to limit of "long")
	    if (base < FACTORIALS.length)
		return BigInteger.valueOf(FACTORIALS[(int) base]);

	    // This starts with 21! so reduce base by starting value
	    BigInteger result = BigInteger.valueOf(FACTORIALS[FACTORIALS.length - 1]);
	    for (long factor = FACTORIALS.length; factor <= base; factor++) {
		BigInteger multiplier = BigInteger.valueOf(factor);
		result = result.multiply(multiplier);
	    }

	    return result;
	}


	/**
	 * Compute the factorial value for the given integer value.
	 * <p> The value for <code>n!</code> is <code>1 * 2 * 3 * 4</code>... to n
	 * and where <code>0! = 1</code> (by definition)
	 *
	 * @param base	The integer base (n).
	 * @param mc	The {@link MathContext} used to round the result (only if base is negative).
	 * @return	The value of <code>n!</code>
	 */
	public static BigDecimal factorial(final Number base, final MathContext mc) {
	    double baseDouble = base.doubleValue();
	    double baseFloor  = Math.floor(baseDouble);

	    if (baseFloor != baseDouble)
		throw new Intl.IllegalArgumentException("math#math.wholeInteger", baseDouble);

	    long loops = base.longValue();

	    if (loops == 0L || loops == 1L)
		return BigDecimal.ONE;

	    boolean negative = false;
	    if (loops < 0L) {
		negative = true;
		loops = -loops - 1L;
	    }

	    BigInteger result = intFactorial(loops);

	    BigDecimal dResult = new BigDecimal(result);
	    if (negative) {
		// The so-called "Roman factorial"
		dResult = BigDecimal.ONE.divide(dResult, mc);
		return (loops % 2L == 1L) ? dResult.negate() : dResult;
	    }

	    return dResult;
	}


	/**
	 * Find the n-th Fibonacci number, where fib(0) = 0,
	 * fib(1) = 1, and fib(n) = fib(n - 1) + fib(n - 2);
	 *
	 * @param n	The desired term number (can be negative).
	 * @return	The n-th Fibonacci number.
	 */
	public static BigDecimal fib(final Number n) {
	    double nDouble = n.doubleValue();
	    double nInt    = Math.rint(nDouble);

	    if (nInt != nDouble)
		throw new Intl.IllegalArgumentException("math#math.wholeInteger", nDouble);

	    long loops        = Math.abs(n.longValue());
	    boolean negative  = nInt < 0.0d;
	    BigInteger n_2    = BigInteger.ZERO;
	    BigInteger n_1    = BigInteger.ONE;
	    BigInteger result = BigInteger.ONE;

	    if (loops == 0L)
		result = n_2;
	    else if (loops == 1L)
		result = n_1;
	    else {
		for (long i = 2L; i <= loops; i++) {
		    result = n_2.add(n_1);
		    n_2    = n_1;
		    n_1    = result;
		}
	    }

	    if (negative) {
		if (loops % 2L == 0)
		    return new BigDecimal(result).negate();
	    }
	    return new BigDecimal(result);
	}


	/**
	 * Special version of {@link #fib} that returns {@code fib(n)} and {@code fib(n+1)}
	 * for use with {@link #ratphi} to calculate the closest rational approximations of "phi".
	 *
	 * @param n	The desired term number (can NOT be negative in this version).
	 * @return	The n-th and n+1-th Fibonacci numbers.
	 */
	private static BigInteger[] fib2(final Number n) {
	    long nValue = n.longValue();

	    long loops        = nValue + 1L;
	    BigInteger n_2    = BigInteger.ZERO;
	    BigInteger n_1    = BigInteger.ONE;
	    BigInteger result = BigInteger.ONE;

	    if (loops == 0L)
		result = n_2;
	    else if (loops == 1L)
		result = n_1;
	    else {
		for (long i = 2L; i <= loops; i++) {
		    result = n_2.add(n_1);
		    n_2    = n_1;
		    n_1    = result;
		}
	    }

	    BigInteger[] results = new BigInteger[2];
	    results[0] = n_2;
	    results[1] = n_1;

	    return results;
	}


	/**
	 * The best rational approximations of pi for each precision (number of significant digits).
	 * <p> Taken from these tables:
	 * <a href="http://oeis.org/A002485">http://oeis.org/A002485</a> and
	 * <a href="http://oeis.org/A002486">http://oeis.org/A002486</a>, with
	 * the last numerator interpolated from the last denominator (by multiplying by pi).
	 */
	private static final long[][] PI_VALUES = {
		{               3L,              1L },
		{              22L,              7L },
		{             333L,            106L },
		{             355L,            113L },
		{          103993L,          33102L },
		{          104348L,          33215L },
		{          208341L,          66317L },
		{          312689L,          99532L },
		{          833719L,         265381L },
		{         1146408L,         364913L },
		{         4272943L,        1360120L },
		{         5419351L,        1725033L },
		{        80143857L,       25510582L },
		{       165707065L,       52746197L },
		{       245850922L,       78256779L },
		{       411557987L,      131002976L },
		{      1068966896L,      340262731L },
		{      2549491779L,      811528438L },
		{      6167950454L,     1963319607L },
		{     14885392687L,     4738167652L },
		{     21053343141L,     6701487259L },
		{   1783366216531L,   567663097408L },
		{   3587785776203L,  1142027682075L },
		{   5371151992734L,  1709690779483L },
		{   8958937768937L,  2851718461558L },
		{ 139755218526789L, 44485467702853L }
	};

	/**
	 * Return the best fractional (rational) approximation of pi for the given precision.
	 *
	 * @param precision	The number of decimal digits of precision required for the value
	 *			(1 .. the length of the {@link #PI_VALUES} table).
	 * @return		A fraction that is the best approximation to that precision, and
	 *			in fact, whose decimal expansion is exactly the same as the computed
	 *			decimal value by our other method, or {@code null} if the given
	 *			precision falls outside our table of values.
	 * @see #PI_VALUES
	 */
	public static BigFraction ratpi(final int precision) {
	    if (precision > 0 && precision <= PI_VALUES.length) {
		return new BigFraction(PI_VALUES[precision - 1][0], PI_VALUES[precision - 1][1]);
	    }
	    return null;
	}

	/**
	 * Table of empirically-derived closest approximations of rational value of "phi" constructed from the ratio
	 * of consecutive Fibonacci numbers. Index is number of digits of precision required, starting at 2.
	 * Values are {@code n} where {@code fib(n + 1) / fib(n) == phi}.
	 * <p> Empirically derived using this program ("ratphi.calc"):
	 * <pre> :quiet on
	 * define ratphi($n) = { fib($n+1) / fib($n) }
	 * PHI_VALUES = [ ]
	 * loop over 2..1000 { :dec $_; loop $n in $_ * 4 { if (ratphi($n) == phi) { if isnull(PHI_VALUES[$_]) { PHI_VALUES[$_] = $n } } } }
	 * :quiet pop
	 * PHI_VALUES@j</pre>
	 * <p> Also note that a number of these values had no "solution" (such as prec = 97, 117, 122, 137, etc.) so the
	 * value here is roughly interpolated from the surrounding values. The values should be +/- one LSD.
	 */
	private static final int[] PHI_VALUES = {
	    4, 7, 9, 12, 14, 16, 19, 21, 24, 26, 28, 31, 33, 36, 38, 40, 42, 44, 47, 50, 52, 54, 58, 60, 62,
	    64, 66, 70, 72, 74, 76, 78, 81, 82, 86, 88, 90, 93, 94, 98, 100, 102, 105, 107, 110, 112, 114,
	    116, 120, 122, 124, 126, 128, 130, 134, 136, 138, 140, 144, 146, 148, 150, 152, 155, 158, 160,
	    162, 165, 166, 170, 172, 174, 176, 179, 182, 184, 186, 188, 190, 194, 196, 198, 200, 202, 205,
	    208, 210, 213, 214, 216, 220, 222, 224, 227, 229, 231 /* ? */, 234, 236, 238, 242, 244, 246, 248, 250,
	    253, 256, 258, 261, 262, 266, 268, 270, 272, 275, 277, 279 /* ? */, 282, 284, 286, 288, 291 /* ? */, 294,
	    296, 298, 300, 304, 306, 308, 310, 312, 316, 318, 320, 322, 325, 327 /* ? */, 329 /* ? */, 332, 334, 336,
	    338, 342, 344, 347, 348, 351, 354, 356, 358, 360, 362, 366, 368, 370, 372, 374, 378, 380, 382,
	    385, 387, 389, 392, 394, 396, 399, 402, 404, 406, 409, 412, 414, 416, 418, 421, 422, 426, 428,
	    430, 432, 434, 437 /* ? */, 440, 442, 444, 446, 448, 452, 454, 456, 459, 460, 464, 466, 468, 470, 474,
	    476, 478, 481, 482, 486, 488, 490, 493, 495, 498, 500, 502, 504, 507, 508, 512, 514, 516, 518,
	    521, 524 /* ? */, 526, 528, 530, 532, 535, 538, 540, 542, 544, 547 /* ? */, 550, 553, 555, 556, 560, 562 /* ? */,
	    564, 566, 568, 572, 574, 576, 578, 581, 584, 586, 588, 590, 593, 596, 598, 600, 602, 604, 606,
	    610, 612, 614, 617, 618, 622, 624, 626, 628, 632, 634, 636, 639, 640, 642, 646, 648, 650, 653,
	    656, 657, 660, 662, 665, 668, 670, 672, 674, 676, 678, 682, 684, 686, 689, 690, 694, 696, 698,
	    700, 702, 706, 708, 710, 712, 714, 718, 720, 722, 724, 726, 730, 732, 734, 736, 739, 741, 744,
	    746, 748, 750, 753, 756, 758, 760, 762, 764, 768, 770, 772, 774, 777, 780, 782, 784, 786, 790,
	    792, 794, 796, 798, 801, 804, 806, 809, 811, 813, 816, 818, 820, 822, 825, 828, 830, 832, 834,
	    836, 838, 842, 844, 846, 849, 852, 854, 856, 858, 860, 864, 866, 868, 871, 873, 874, 878, 880,
	    882, 884, 888, 890, 892, 894, 897, 899, 902, 904, 906, 909, 910, 914, 916, 919, 920, 924, 926,
	    928, 930, 932, 935, 938, 940, 942, 944, 948, 950, 952, 954, 956, 958, 962, 964 /* ? */, 966, 968, 970,
	    974, 976, 978, 980, 983, 986, 988, 990, 992, 995, 998, 1000, 1002, 1004, 1006, 1009, 1012, 1014,
	    1016, 1019, 1020, 1024, 1026, 1028, 1031, 1033, 1036, 1038, 1040, 1042, 1044, 1047 /* ? */, 1050, 1052,
	    1055, 1057, 1060 /* ? */, 1062, 1064, 1066, 1070, 1071, 1074, 1076, 1078, 1080, 1084, 1086 /* ? */, 1088, 1090,
	    1092, 1095, 1098, 1100, 1102, 1106, 1107, 1110, 1112, 1114, 1116, 1120, 1122, 1124, 1126, 1128,
	    1132, 1134, 1136, 1138, 1141, 1144, 1146, 1148, 1150, 1152, 1156, 1158 /* ? */, 1160, 1162, 1164, 1167,
	    1169, 1172, 1174, 1177, 1179, 1182, 1184, 1186, 1189, 1192, 1193, 1196, 1198, 1200, 1202, 1206,
	    1208, 1210, 1212, 1214, 1218, 1220, 1222, 1224, 1226, 1230, 1232, 1234, 1236, 1239, 1242, 1244,
	    1246, 1248, 1250, 1252, 1256, 1258, 1260, 1263, 1266, 1268, 1270, 1273, 1274, 1276, 1280, 1282,
	    1284, 1286, 1290, 1292, 1294, 1296, 1298, 1300, 1304, 1306, 1308, 1310, 1312, 1316, 1318, 1320,
	    1322, 1324, 1328, 1330, 1332, 1334, 1336, 1340, 1342, 1345, 1347, 1349, 1351, 1354, 1356, 1358,
	    1360, 1363, 1366, 1368, 1371, 1372, 1376, 1378, 1380, 1382, 1384, 1388, 1390 /* ? */, 1392, 1394, 1397,
	    1398, 1402, 1404, 1406, 1408, 1410, 1414, 1416, 1418, 1420, 1422, 1426, 1428, 1430, 1432, 1434,
	    1438, 1440, 1443, 1445, 1446, 1450, 1452, 1454, 1456, 1458, 1461 /* ? */, 1464, 1466, 1468, 1470, 1473,
	    1476, 1478, 1480, 1482, 1484, 1488, 1490, 1492, 1496, 1498, 1500, 1502, 1504, 1507, 1508, 1512,
	    1514, 1516, 1518, 1521, 1524 /* ? */, 1526, 1528, 1530, 1532, 1536, 1538, 1541, 1543, 1545, 1548 /* ? */, 1550,
	    1552, 1554, 1557, 1560, 1562 /* ? */, 1564, 1566, 1568, 1572, 1574, 1576, 1578, 1580, 1582, 1586, 1588,
	    1591, 1592, 1596, 1598, 1600, 1603, 1604, 1606, 1609 /* ? */, 1612, 1614, 1616, 1620, 1622, 1624, 1626,
	    1628, 1632, 1634, 1636, 1638, 1640, 1642, 1646, 1648, 1650, 1652, 1656, 1658, 1660, 1662, 1665,
	    1667, 1670 /* ? */, 1672, 1674, 1676, 1678, 1682, 1684, 1686, 1688, 1690, 1694, 1696, 1698, 1700, 1703,
	    1706, 1708, 1710, 1713, 1714, 1718, 1720, 1722, 1725, 1727, 1729, 1732, 1734, 1737, 1738, 1742,
	    1744, 1746, 1748, 1750, 1752, 1755 /* ? */, 1758, 1760, 1763, 1765, 1768, 1770, 1772, 1774, 1778, 1779,
	    1782, 1784, 1787, 1790, 1792, 1794, 1797, 1798, 1802, 1804, 1806, 1808, 1810, 1813, 1816, 1818,
	    1820, 1822, 1824, 1828, 1830, 1832, 1835, 1836, 1840, 1842, 1844, 1847, 1849, 1852, 1854, 1856,
	    1858, 1861, 1863, 1866, 1868, 1870, 1872, 1875, 1878, 1880, 1882, 1885, 1886, 1890, 1892, 1894,
	    1897, 1899, 1902, 1904, 1906, 1908, 1912, 1914, 1916, 1918, 1920, 1924, 1926, 1928, 1930, 1933,
	    1934, 1938, 1940, 1942, 1944, 1946, 1950, 1952, 1954, 1957, 1958, 1962, 1964 /* ? */, 1966, 1968, 1970,
	    1974, 1976, 1978, 1980, 1982, 1985 /* ? */, 1988, 1990, 1992, 1995, 1996, 2000, 2002, 2004, 2007, 2010,
	    2012, 2014, 2016, 2018, 2020, 2024, 2026, 2029, 2031, 2032, 2036, 2038, 2040, 2043, 2044, 2048,
	    2050, 2052, 2054, 2056, 2059 /* ? */, 2062, 2064, 2066, 2068, 2072, 2074, 2076, 2078, 2082, 2083, 2086,
	    2088, 2090, 2092, 2096, 2098, 2100, 2102, 2104, 2107, 2110, 2112, 2114, 2117, 2120, 2122, 2124,
	    2126, 2129, 2132, 2134 /* ? */, 2136, 2139, 2140, 2144, 2146, 2148, 2150, 2152, 2156, 2158, 2160, 2162,
	    2165, 2166, 2170, 2172, 2174, 2177, 2179, 2182 /* ? */, 2184, 2186, 2189, 2191, 2194 /* ? */, 2196, 2199, 2200,
	    2204, 2206, 2208, 2210, 2212, 2215, 2218, 2220, 2222, 2224, 2226, 2230, 2232, 2234, 2236, 2239,
	    2242, 2244, 2246, 2248, 2250, 2254, 2256, 2258, 2260, 2263, 2264, 2267 /* ? */, 2270, 2273, 2275, 2278,
	    2280, 2282, 2285, 2287, 2290, 2292, 2294, 2296, 2299, 2302, 2304, 2306, 2308, 2310, 2312, 2316,
	    2318, 2320, 2322, 2326, 2328, 2330, 2332, 2334, 2338, 2339, 2342, 2344, 2346, 2348, 2352, 2354,
	    2356, 2358, 2362, 2364, 2366 /* ? */, 2368, 2370, 2372, 2376, 2378, 2380, 2382, 2385, 2388, 2390, 2392
	};


	/**
	 * Calculate the closest rational approximation to "phi" (the "Golden Ratio") as a ratio of two
	 * consecutive Fibonacci numbers.
	 * <p> Note: above a certain precision (currently ~200 digits, the size of {@link #PHI_VALUES} above)
	 * we will resort to calculating the decimal expansion and converting that to a fraction instead of
	 * using this (interesting) rational number approach.
	 *
	 * @param mc	The given precision needed.
	 * @param recip	Whether to calculate {@code 1 / phi} (the reciprocal).
	 * @return	{@link BigFraction} value.
	 */
	public static BigFraction ratphi(final MathContext mc, final boolean recip) {
	    int prec = mc.getPrecision();
	    if (prec < 2)
		prec = 2;
	    else if (prec >= PHI_VALUES.length + 2) {
		// Default for high precision to using decimal "phi" converted to BigFraction
		return new BigFraction(phi(mc, recip));
	    }

	    BigInteger[] values = fib2(PHI_VALUES[prec - 2]);
	    return recip ? new BigFraction(values[0], values[1]) : new BigFraction(values[1], values[0]);
	}


	/**
	 * A cache of BigFraction values for B(n), so that this expensive operation
	 * doesn't have to be done more than once per index.
	 * <p> The index is n / 2 since every other value is zero.
	 */
	private static DynamicArray<BigFraction> bernoulliCache = new DynamicArray<>(BigFraction.class, 60);

	/**
	 * From <a href="https://rosettacode.org/wiki/Bernoulli_numbers">rosettacode.org</a>
	 * the algorithm is as follows:
	 * allocate n+1 BigFractions
	 * for (m = 0 to n) {
	 *   arr[m] = BigFraction(1, (m+1))
	 *   for (n = m downto 1) {
	 *      arr[n-1] = (arr[n-1] - arr[n]) * n
	 *   }
	 * }
	 * return arr[0]
	 *
	 * @param n Which Bernoulli number to calculate.
	 * @return  The value as a fraction.
	 */
	private static BigFraction bern(final int n) {
	    int num = Math.abs(n);

	    // First, check if we have already computed and cached the value
	    int cacheIndex = num >> 1;
	    BigFraction cachedResult = bernoulliCache.get(cacheIndex);
	    if (cachedResult != null) {
		logger.debug("bern(%1$d) gotten from cache", num);
		return cachedResult;
	    }

	    BigFraction[] arr = new BigFraction[num+1];
	    for (int m = 0; m <= num; m++) {
		arr[m] = new BigFraction(1, (m+1));
		for (int i = m; i >= 1; i--) {
		    arr[i-1] = (arr[i-1].subtract(arr[i])).multiply(i);
		}
	    }

	    bernoulliCache.put(cacheIndex, arr[0]);
	    return arr[0];
	}

	/**
	 * Get the value of the N-th Bernoulli number.
	 *
	 * @param n	Which Bernoulli number to get.
	 * @param mc	The {@link MathContext} to use for rounding the division
	 *		(non-rational mode).
	 * @param rational Whether to return the result as a rational number.
	 * @return	The N-th Bernoulli number as a decimal (rounded to {@code mc}),
	 *		or as an exact fraction.
	 */
	public static Object bernoulli(final int n, final MathContext mc, final boolean rational) {
	    if (n == 0)
		return rational ? BigFraction.ONE : BigDecimal.ONE;
	    if (n == 1 || n == -1) {
		if (rational)
		    return new BigFraction(n, 2);
		else
		    return BigDecimal.valueOf(n).divide(D_TWO);
	    }
	    if (n % 2 == 1)
		return rational ? BigFraction.ZERO : BigDecimal.ZERO;

	    BigFraction bn = bern(n);

	    return rational ? bn : bn.toDecimal(mc);
	}


	/**
	 * Calculate the N-th Harmonic number.
	 * <p> We will do the calculations as an (exact) rational fraction, and
	 * only convert to decimal at the end if needed ({@code rational} parameter).
	 * <p> Empirically, instead of doing the entire calculation as fractions
	 * (which requires renormalization every time), calculate the numerator
	 * and denominator separately and only normalize right at the end.
	 *
	 * @param num	Which Harmonic number to get.
	 * @param mc	The {@link MathContext} to use for rounding the division
	 *		(non-rational mode).
	 * @param rational Whether to return the result as a rational number.
	 * @return	The N-th Harmonic number as a decimal (rounded to {@code mc}),
	 *		or as an exact fraction.
	 * @throws	IllegalArgumentException if {@code num} is less than one, or not an integer.
	 */
	public static Object harmonic(final Number num, final MathContext mc, final boolean rational) {
	    int n = intValueExact(num);

	    // Not sure, but do this for now
	    if (n <= 0)
		throw new Intl.IllegalArgumentException("math#math.nOutOfRange", n);

	    BigInteger factorial = intFactorial(n);
	    // This starts as n! / 1
	    BigInteger sum = factorial;

	    for (int i = 2; i <= n; i++) {
		BigInteger factor = factorial.divide(BigInteger.valueOf((long) i));
		sum = sum.add(factor);
	    }
	    BigFraction result = new BigFraction(sum, factorial);

	    return rational ? result : result.toDecimal(mc);
	}


	/**
	 * Helper method to convert any input "number" to equivalent {@link BigDecimal} value.
	 * <p> Note: dubious results for complex or quaternion inputs, even though these are
	 * derived from {@link Number}.
	 *
	 * @param x	An arbitrary input numeric value.
	 * @param mc	Rounding context for the fractional conversion (if necessary).
	 * @return	{@code BigDecimal} equivalent of the input, rounded to the specific precision.
	 */
	private static BigDecimal toDecimal(final Number x, final MathContext mc) {
	    if (x instanceof BigDecimal)
		return (BigDecimal) x;
	    else if (x instanceof BigInteger)
		return new BigDecimal((BigInteger) x);
	    else if (x instanceof BigFraction)
		return ((BigFraction) x).toDecimal(mc);
	    else
		return BigDecimal.valueOf(x.doubleValue());
	}

	/**
	 * Find the value of sin(x) (where x is in radians).
	 *
	 * @param x	The value in radians to compute the "sin" function of.
	 * @param mc	The {@link MathContext} to use for rounding during the computation.
	 * @return	The value of the sin of x.
	 */
	public static BigDecimal sin(final Number x, final MathContext mc) {
	    BigDecimal xValue = toDecimal(x, mc);

	    pi(mc.getPrecision());

	    /* First do some range reduction to the range -2*pi to 2*pi */
	    logger.debug("sin:     original x = %1$s", xValue.toPlainString());
	    if (xValue.compareTo(MINUS_TWO_PI) < 0 || xValue.compareTo(TWO_PI) > 0) {
		xValue = xValue.remainder(TWO_PI, mc);
		logger.debug("sin: range reduced x = %1$s", xValue.toPlainString());
	    }

	    BigDecimal result   = xValue;
	    BigDecimal power    = xValue;
	    BigDecimal xSquared = xValue.multiply(xValue);
	    BigDecimal fact     = BigDecimal.ONE;
	    BigDecimal factTerm = BigDecimal.ONE;

	    // This converges very rapidly, except when the value is near zero
	    int prec  = mc.getPrecision();
	    int loops = prec * 3 / 2;

	    MathContext mc2 = newPrecision(mc, prec);

	    for (int i = 1; i < loops; i++) {
		power     = power.multiply(xSquared);
		factTerm  = factTerm.add(BigDecimal.ONE);
		fact      = fact.multiply(factTerm);
		factTerm  = factTerm.add(BigDecimal.ONE);
		fact      = fact.multiply(factTerm);
		BigDecimal seriesTerm = power.divide(fact, mc2);
		if (i % 2 == 1)
		    result = result.subtract(seriesTerm);
		else
		    result = result.add(seriesTerm);
		logger.debug("sin: loop %1$d -> %2$s", i, result.toPlainString());
	    }

	    return fixup(result, mc);
	}

	/**
	 * Find the value of cos(x) (where x is in radians).
	 *
	 * @param x	The value in radians to compute the "cos" function of.
	 * @param mc	The {@link MathContext} to use for the computation.
	 * @return	The value of the cos of x.
	 */
	public static BigDecimal cos(final Number x, final MathContext mc) {
	    BigDecimal xValue = toDecimal(x, mc);

	    pi(mc.getPrecision());

	    /* First do some range reduction to the range -2*pi to 2*pi */
	    logger.debug("cos:     original x = %1$s", xValue.toPlainString());
	    if (xValue.compareTo(MINUS_TWO_PI) < 0 || xValue.compareTo(TWO_PI) > 0) {
		xValue = xValue.remainder(TWO_PI, mc);
		logger.debug("cos: range reduced x = %1$s", xValue.toPlainString());
	    }

	    BigDecimal xSquared = xValue.multiply(xValue);
	    BigDecimal result   = BigDecimal.ONE;
	    BigDecimal power    = BigDecimal.ONE;
	    BigDecimal fact     = BigDecimal.ONE;
	    BigDecimal factTerm = BigDecimal.ONE;

	    // This converges very rapidly, except when the value is near zero
	    int prec  = mc.getPrecision();
	    int loops = prec * 3 / 2;

	    MathContext mc2 = newPrecision(mc, prec);

	    for (int i = 1; i < loops; i++) {
		power     = power.multiply(xSquared);
		factTerm  = factTerm.add(BigDecimal.ONE);
		fact      = fact.multiply(factTerm);

		BigDecimal seriesTerm = power.divide(fact, mc2);

		if (i % 2 == 1)
		    result = result.subtract(seriesTerm);
		else
		    result = result.add(seriesTerm);

		logger.debug("cos: loop %1$d -> %2$s", i, result.toPlainString());
		factTerm  = factTerm.add(BigDecimal.ONE);
		fact      = fact.multiply(factTerm);
	    }

	    return fixup(result, mc);
	}

	/**
	 * Find the value of tan(x) (where x is in radians).
	 *
	 * @param x	The value in radians to compute the "tan" function of.
	 * @param mc	The {@link MathContext} to use for the computation.
	 * @return	The value of the tan of x.
	 */
	public static BigDecimal tan(final Number x, final MathContext mc) {
	    // Rounding context for the loops, to ensure we get accuracy to the requested precision
	    MathContext mc2 = newPrecision(mc, 2);

	    BigDecimal xValue = toDecimal(x, mc2);

	    pi(mc2.getPrecision());

	    /* First, do some range reductions to the range of -pi/2 to pi/2 */
	    if (xValue.compareTo(MINUS_PI_OVER_TWO) < 0 || xValue.compareTo(PI_OVER_TWO) > 0) {
		xValue = xValue.remainder(PI_OVER_TWO, mc);
	    }

	    // Some simplifications
	    if (xValue.signum() == 0)
		return BigDecimal.ZERO;

	    BigDecimal result     = xValue;
	    BigInteger twoPower   = I_FOUR;
	    BigDecimal xPower     = xValue;
	    BigDecimal xSquared   = xValue.multiply(xValue);
	    BigInteger factTerm   = I_TWO;
	    BigInteger factorial  = I_TWO; // 2!
	    BigDecimal lastResult = result;
	    BigInteger numer;

	    // This seems to require (precision/4) * input/0.1 iterations, so for
	    // precision 20, about 5 * 0.1, for 34 about 8 * 0.1, etc.
	    int approxRange = (int) Math.floor(xValue.divide(D_ONE_TENTH, mc).doubleValue()) + 1;
	    int loopCountPerRange = (mc.getPrecision() + 3) / 4;
	    int loops = (approxRange + approxRange / 6) * loopCountPerRange + 3;
	    logger.debug("tan: precision = %1$d, approx range = %2$d, loops per = %3$d -> loops = %4$d", mc.getPrecision(), approxRange, loopCountPerRange, loops);
	    // Big decision here:  at some point at around 1.2 (where approxRange / 6 is > 1) we start getting
	    // diminishing returns, so switch to using sin(x) / cos(x) as quicker AND more accurate
	    if (approxRange > 12) {
		return sin(xValue, mc).divide(cos(xValue, mc), mc);
	    }

	    for (int i = 2; i < loops; i++) {
		twoPower  = twoPower.multiply(I_FOUR);
		xPower    = xPower.multiply(xSquared);
		numer     = twoPower.multiply(twoPower.subtract(BigInteger.ONE));
		factTerm  = factTerm.add(BigInteger.ONE);
		factorial = factorial.multiply(factTerm);
		factTerm  = factTerm.add(BigInteger.ONE);
		factorial = factorial.multiply(factTerm);

		BigFraction t  = new BigFraction(numer, factorial);
		// There is supposed to be a (-1)**(n-1) term, but it is exactly
		// balanced by the oscillating sign of Bn, so just do abs() here
		// and ignore the -1 term
		BigFraction bn = bern(i * 2).abs();
		BigFraction termn = t.multiply(bn);
		logger.debug("tan: i = %1$d, bn(i*2) = %2$s, t (num/fact) = %3$s, t*bn = %4$s", i, bn, t, termn);
		BigDecimal term = termn.toDecimal(mc2).multiply(xPower, mc2);
		result          = result.add(term, mc2);
		logger.debug("tan: term = %1$s, new result = %2$s, lastResult = %3$s", term.toPlainString(), result.toPlainString(), lastResult.toPlainString());
		if (lastResult.equals(result)) {
		    break;
		}
		lastResult = result;
	    }

	    return fixup(result, mc);
	}

	/**
	 * Find the <code>arctan(y/x)</code> value, which is the "theta" value when converting complex numbers
	 * to polar form.
	 * <p> Relying on the formulas from here:
	 * <a href="https://proofwiki.org/wiki/Power_Series_Expansion_for_Real_Arctangent_Function">Power Series Expansion for Real Arctangent Function</a>
	 *
	 * @param y	The imaginary or y-coordinate of this number.
	 * @param x	The real or x-coordinate of the number.
	 * @param mc	The rounding context to use for the result (at least).
	 * @return	The angle whose tangent is <code>y / x</code>.
	 * @throws IllegalArgumentException if x and y are both zero.
	 */
	public static BigDecimal atan2(final BigDecimal y, final BigDecimal x, final MathContext mc) {
	    int ySign = y.signum();
	    int xSign = x.signum();

	    pi(mc.getPrecision());

	    BigDecimal result = null;

	    // If imaginary part (y) is zero, then angle is 0 or pi depending on sign of x
	    if (ySign == 0) {
		switch (xSign) {
		    case 0:
			// ?? to me, this is undefined, but Math.atan2 returns 0 for this case
		    case +1:
			result = BigDecimal.ZERO;
			break;
		    case -1:
			result = CALCULATED_PI;
			break;
		}
	    }
	    // If real part (x) is zero, then angle is +/- pi/2 depending on sign of y
	    else if (xSign == 0) {
		result = ySign < 0 ? MINUS_PI_OVER_TWO : PI_OVER_TWO;
	    }
	    // If x and y are the same magnitude the result is +/- pi/4 or 3*pi/4
	    else if (y.abs().equals(x.abs())) {
		if (ySign > 0 && xSign > 0)
		    result = PI_OVER_FOUR;
		else if (ySign < 0 && xSign > 0)
		    result = MINUS_PI_OVER_FOUR;
		else if (ySign > 0 && xSign < 0)
		    result = PI_OVER_FOUR.multiply(D_THREE);
		else
		    result = PI_OVER_FOUR.multiply(D_THREE).negate();
	    }
	    if (result != null)
		return result.round(mc);

	    // Use extra precision to ensure we get as accurate a value as possible
	    MathContext mc2 = newPrecision(mc, mc.getPrecision());

	    // Else there is one of three series depending on the size of the value
	    BigDecimal _x = y.divide(x, mc2);

	    boolean above = _x.compareTo(BigDecimal.ONE) >= 0;
	    boolean below = _x.compareTo(D_MINUS_ONE) <= 0;

	    BigDecimal term, multTerm, powerTerm = _x;
	    BigDecimal x_square = _x.multiply(_x);
	    BigDecimal lastResult = BigDecimal.ZERO;
	    int sign = -1;

	    // -1 <= x <= 1
	    if (!above && !below) {
		result = powerTerm;
		powerTerm = powerTerm.multiply(x_square, mc2);
		multTerm = D_THREE;
	    }
	    // x >= 1 or x <= -1
	    else {
		result = above ? PI_OVER_TWO : MINUS_PI_OVER_TWO;
		multTerm = BigDecimal.ONE;
	    }

	    while (result.compareTo(lastResult) != 0) {
		lastResult = result;

		if (!above && !below) {
		    term = powerTerm.divide(multTerm, mc2);
		}
		else {
		    term = BigDecimal.ONE.divide(multTerm.multiply(powerTerm), mc2);
		}

		if (sign < 0)
		    result = result.subtract(term, mc2);
		else
		    result = result.add(term, mc2);

		multTerm = multTerm.add(D_TWO, mc2);
		powerTerm = powerTerm.multiply(x_square, mc2);
		sign = -sign;
	    }

	    return result.round(mc);
	}

	/**
	 * Find the square root of a number, which could be real or complex.
	 *
	 * @param x	The value to find the square root of.
	 * @param mc	The {@code MathContext} to use for rounding / calculating the result.
	 * @return	The {@code sqrt(x)} value such that {@code x = result * result} (which
	 *		could be a complex number).
	 */
	public static Number sqrt2(final BigDecimal x, final MathContext mc) {
	    int sign = x.signum();

	    BigDecimal y = sign < 0 ? x.abs() : x;
	    BigDecimal root = sqrt(y, mc);

	    return sign < 0 ? ComplexNumber.imaginary(root) : root;
	}

	/**
	 * Find the positive square root of a number (non-negative).
	 *
	 * @param x	The value to find the square root of.
	 * @param mc	The {@code MathContext} to use for rounding / calculating the result.
	 * @return	The {@code sqrt(x)} value such that {@code x = result * result}.
	 * @throws	IllegalArgumentException if the value is negative.
	 */
	public static BigDecimal sqrt(final BigDecimal x, final MathContext mc) {
	    int sign = x.signum();

	    if (sign == 0 || x.equals(BigDecimal.ONE))
		return x;

	    if (sign < 0)
		throw new Intl.IllegalArgumentException("math#math.sqrtNegative");

	    BigDecimal trial_root = BigDecimal.ONE.movePointRight((x.precision() - x.scale()) / 2);
	    BigDecimal result = trial_root;
	    BigDecimal lastResult = result;

	    logger.debug("sqrt: trial_root = %1$s", trial_root.toPlainString());
	    // 50 is entirely arbitrary; normally the results converge in 5-10 iterations
	    // up to 100s of digits
	    for (int i = 0; i < 50; i++) {
		result = result.add(x.divide(result, mc)).divide(D_TWO, mc);
		logger.debug("sqrt: result = %1$s, lastResult = %2$s", result.toPlainString(), lastResult.toPlainString());
		if (result.equals(lastResult)) {
		    logger.debug("sqrt: break out early, i = %1$d", i);
		    break;
		}
		lastResult = result;
	    }
	    logger.debug("sqrt: result = %1$s", result.toPlainString());
	    return fixup(result, mc);
	}

	/**
	 * Find the cube root of a number (either positive or negative).
	 *
	 * @param x	The value to find the cube root of.
	 * @param mc	The {@code MathContext} to use for rounding / calculating the result.
	 * @return	The {@code cbrt(x)} value such that {@code x = result * result * result}.
	 */
	public static BigDecimal cbrt(final BigDecimal x, final MathContext mc) {
	    int sign = x.signum();
	    BigDecimal xValue = x.abs();

	    // Taken from https://stackoverflow.com/questions/7463486/seeding-the-newton-iteration-for-cube-root-efficiently
	    // BigDecimal trial_root = new BigDecimal("1.4774329094")
	    //    .subtract(new BigDecimal("0.8414323527").divide(x.add(new BigDecimal("0.7387320679")), mc));
	    BigDecimal trial_root = BigDecimal.ONE.movePointRight((x.precision() - x.scale()) / 3);
	    logger.debug("cbrt: trial_root = %1$s", trial_root.toPlainString());
	    BigDecimal result = trial_root;
	    BigDecimal lastResult = result;

	    for (int i = 0; i < 50; i++) {
		BigDecimal x2 = result.multiply(result);
		BigDecimal x3 = x2.multiply(result);
		BigDecimal numer = xValue.subtract(x3);
		BigDecimal denom = x2.multiply(D_THREE);
		result = result.add(numer.divide(denom, mc), mc);
		logger.debug("cbrt: result = %1$s, lastResult = %2$s", result.toPlainString(), lastResult.toPlainString());
		if (result.equals(lastResult)) {
		    logger.debug("cbrt: break out early, i = %1$d", i);
		    break;
		}
		lastResult = result;
	    }
	    return ((sign < 0) ? result.negate(mc) : result.plus(mc)).stripTrailingZeros();
	}


        private static final int SCALE = 10000;
        private static final int ARRINIT = 2000;

	/**
	 * Taken from <a href="http://www.codecodex.com/wiki/index.php?title=Digits_of_pi_calculation#Java">
	 * http://www.codecodex.com/wiki/index.php?title=Digits_of_pi_calculation#Java</a>
	 *
	 * @param digits - returns good results up to 12500 digits
	 * @return that many digits of PI
	 * @throws IllegalArgumentException if the requested number of digits is &gt; 12,500
	 * @throws IllegalStateException if we don't get the right number of digits from
	 * the calculation.
	 */
	public static String piDigits(final int digits) {
	    // According to the original documentation, the given SCALE and ARRINIT
	    // values work up to approx. 12,500 digits, so error out if we're over that
	    if (digits > 12_500)
		throw new Intl.IllegalArgumentException("math#math.tooManyPiDigits");

	    // Since each loop reduces the count by 14 while only providing 4 digits
	    // of output, in order to produce the required number of digits we must
	    // scale up the loop count proportionally. Add one more loop to make sure
	    // we have enough.
	    int loops = (digits + 1) * 14 / 4;

	    StringBuffer pi = new StringBuffer(loops);
	    int[] arr = new int[loops + 1];
	    int carry = 0;

	    Arrays.fill(arr, ARRINIT);

	    for (int i = loops; i > 0; i-= 14) {
		int sum = 0;
		for (int j = i; j > 0; --j) {
		    sum = sum * j + SCALE * arr[j];
		    arr[j] = sum % (j * 2 - 1);
		    sum /= j * 2 - 1;
		}

		pi.append(String.format("%04d", carry + sum / SCALE));
		carry = sum % SCALE;
	    }

	    // We calculated a few more digits than we need (hopefully), so truncate
	    // the result to the exact digit count requested. Exception thrown if we
	    // calculated wrong.
	    if (pi.length() < digits)
		throw new Intl.IllegalStateException("math#math.piDigitMismatch",
			pi.length(), digits);
	    else if (pi.length() > digits)
		pi.setLength(digits);

	    return pi.toString();
	}


	/**
	 * Like pi, e is a real number with an infinite number of non-repeating digits.  We can
	 * approximate e with the following formula:  e = 1/0! + 1/1! + 1/2! + 1/3! + 1/4! + ...
	 *
	 * @param digits The number of digits to compute.
	 * @return       The decimal value of e to the given number of digits.
	 */
	public static BigDecimal eDecimal(final int digits) {
	    // e will accumulate the sum of 1/i! for an ever increasing i
	    BigDecimal e         = D_TWO;
	    BigDecimal factorial = BigDecimal.ONE;
	    // loops is a little extra to make sure the last digit we want is accurate
	    int loops = digits + 10;
	    MathContext roundingContext = new MathContext(digits + 1, RoundingMode.DOWN);

	    for (int i = 2; i < loops; i++) {
		factorial = factorial.multiply(BigDecimal.valueOf(i));
		// compute 1/i!, note divide is overloaded, this version is used to
		//    ensure a limit to the iterations when division is limitless like 1/3
		BigDecimal term = BigDecimal.ONE.divide(factorial, loops, RoundingMode.HALF_UP);
		e = e.add(term);
	    }
	    return e.round(roundingContext);
	}


	/**
	 * Calculate e**x, or the anti-logarithm of x to an arbitrary precision.
	 * <p> The formula (same as for {@link #eDecimal} with a numerator of x**n:
	 * <code>e**x = 1 + x/1! + x**2/2! + x**3/3! + x**4/4! + ...</code>
	 *
	 * @param exp	The power of e we are calculating.
	 * @param mc	The precision and rounding mode for the result.
	 * @return	The result of e**x rounded to the given precision.
	 */
	public static BigDecimal ePower(final BigDecimal exp, final MathContext mc) {
	    int sign = exp.signum();

	    // e**0 == 1
	    if (sign == 0)
		return BigDecimal.ONE;

	    BigDecimal result;

	    boolean reciprocal = false;
	    BigDecimal exponent = exp;
	    if (sign < 0) {
		reciprocal = true;
		exponent = exp.abs();
	    }

	    MathContext mc2 = newPrecision(mc, 2);

	    if (exp.equals(BigDecimal.ONE)) {
		result = e(mc2);
		if (reciprocal)
		    result = BigDecimal.ONE.divide(result, mc2);
		return fixup(result, mc);
	    }

	    int intExp         = exponent.intValue();
	    BigDecimal fracExp = exponent.subtract(new BigDecimal(intExp));

	    result = BigDecimal.ONE;

	    if (fracExp.signum() != 0) {
		result = result.add(fracExp);

		BigDecimal numer      = fracExp;
		BigDecimal factorial  = BigDecimal.ONE;
		BigDecimal lastResult = result;

		// loops is extra to make sure the last digit we want is accurate
		int loops = mc.getPrecision() * 2;

		for (int i = 2; i < loops; i++) {
		    factorial = factorial.multiply(BigDecimal.valueOf(i));
		    // compute x**i/i!, note divide is overloaded, this version is used to
		    //    ensure a limit to the iterations when division is limitless like 1/3
		    numer = numer.multiply(fracExp);
		    BigDecimal term = numer.divide(factorial, loops, RoundingMode.HALF_UP);
		    result = result.add(term);

		    if (result.equals(lastResult))
			break;
		    lastResult = result;
		}
	    }

	    result = e(mc2).pow(intExp).multiply(result, mc2);
	    if (reciprocal)
		result = BigDecimal.ONE.divide(result, mc2);

	    return fixup(result, mc);
	}


	/**
	 * Calculate 10**x to an arbitrary precision.
	 *
	 * @param exp	The power of 10 we are calculating.
	 * @param mc	The precision and rounding mode for the result.
	 * @return	The result of 10**x rounded to the given precision.
	 */
	public static BigDecimal tenPower(final BigDecimal exp, final MathContext mc) {
	    // 10**0 == 1
	    if (exp.signum() == 0)
		return BigDecimal.ONE;

	    BigDecimal result;

	    int intExp         = exp.intValue();
	    BigDecimal fracExp = exp.subtract(new BigDecimal(intExp));

	    if (fracExp.signum() != 0) {
		boolean reciprocal = false;
		BigDecimal exponent = exp;
		if (exp.signum() < 0) {
		    reciprocal = true;
		    exponent = exp.abs();
		}

		result = pow(D_TEN, fracExp.doubleValue(), mc);
	    }
	    else {
		// Integer (positive or negative) power
		if (intExp > 0)
		    result = BigDecimal.ONE.scaleByPowerOfTen(intExp);
		else
		    result = D_TEN.pow(intExp, mc);
	    }

	    return fixup(result, mc);
	}


	/**
	 * @return A {@link BigDecimal} constant of PI to the requested number of fractional digits
	 * (up to around 12,500).
	 *
	 * @param digits The number of digits desired after the decimal point.
	 * @see #piDigits
	 * @throws IllegalArgumentException if the number of digits is more than we can handle.
	 */
	public static BigDecimal pi(final int digits) {
	    // Use +1 for precision because of the "3." integer portion
	    MathContext mc = new MathContext(digits + 1, RoundingMode.DOWN);

	    // For very small values, use the rational approximation
	    if (digits < 25) {
		return PI_APPROX.toDecimal(mc);
	    }

	    // Use the previously calculated value if possible
	    if (CALCULATED_PI == null || CALCULATED_PI.scale() != digits) {
		// Calculate a new value with the requested scale
		// (+1 because of the leading "3" digit)
		if (PI_DIGITS == null || PI_DIGITS.length() <= digits) {
		    PI_DIGITS = piDigits(digits + 1);
		}
		CALCULATED_PI = new BigDecimal(PI_DIGITS.substring(0, digits + 1)).movePointLeft(digits);

		// Now calculate the related values at the same scale
		TWO_PI             = CALCULATED_PI.multiply(D_TWO, mc);
		MINUS_TWO_PI       = TWO_PI.negate();
		PI_OVER_TWO        = CALCULATED_PI.divide(D_TWO, mc);
		MINUS_PI_OVER_TWO  = PI_OVER_TWO.negate();
		PI_OVER_FOUR       = CALCULATED_PI.divide(D_FOUR, mc);
		MINUS_PI_OVER_FOUR = PI_OVER_FOUR.negate();
	    }

	    return CALCULATED_PI;
	}


	/** The previously calculated E value (if any); cached to eliminate repeated costly calculations. */
	private static BigDecimal CALCULATED_E = null;
	/** The largest number of digits of E calculated so far: use a substring for lesser precision. */
	private static String E_DIGITS = null;

	/**
	 * @return A {@link BigDecimal} constant of E to the requested precision.
	 *
	 * @param mc The desired precision.
	 */
	public static BigDecimal e(final MathContext mc) {
	    return e(mc.getPrecision() - 1);
	}

	/**
	 * @return A {@link BigDecimal} constant of E to the requested number of fractional digits.
	 *
	 * @param digits The number of digits desired after the decimal point.
	 */
	public static BigDecimal e(final int digits) {
	    // Use the previously calculated value if possible
	    if (CALCULATED_E == null || CALCULATED_E.scale() != digits) {
		if (E_DIGITS == null || E_DIGITS.length() < digits + 2) {
		    CALCULATED_E = eDecimal(digits);
		    E_DIGITS = CALCULATED_E.toPlainString();
		}
		else {
		    CALCULATED_E = new BigDecimal(E_DIGITS.substring(0, digits + 2));
		}
	    }

	    return CALCULATED_E;
	}

	/**
	 * Calculate the value of "phi" (the "Golden Ratio"), which is the solution to
	 * the equation <code>x^2 - x - 1 = 0</code> which is <code>(1 + sqrt(5)) / 2</code>.
	 * <p> Interestingly, <code>1 / phi == phi - 1</code>
	 * <p> <code>phi - 1 == 1 / phi</code> but because of rounding this doesn't always
	 * work out in practice, so this code prioritizes that <code>1 / phi == PHI</code>
	 * and <code>1 / PHI == phi</code>, which also means that <code>phi - 1 != PHI</code>
	 * though it should be, and <code>PHI + 1 != phi</code> though that should be also.
	 *
	 * @param mc         The math context (precision) requested.
	 * @param reciprocal Whether to get the reciprocal value (upper case PHI).
	 *
	 * @return A {@link BigDecimal} constant of PHI to the requested precision.
	 */
	public static BigDecimal phi(final MathContext mc, final boolean reciprocal) {
	    BigDecimal term = sqrt(D_FIVE, mc).add(BigDecimal.ONE);
	    if (reciprocal)
		return D_TWO.divide(term, mc);
	    else
		return term.divide(D_TWO, mc);
	}


	private static final BigInteger MAX_PRIME = MAX_INT.multiply(MAX_INT);

	/**
	 * This is the "Sieve of Eratosthenes" used for primality tests.
	 * <p> The one bits in this sieve correspond to the multiples of
	 * each prime number starting with 3 (that is, the composite numbers)
	 * while the zero bits are the primes in between them. Only the
	 * odd numbers starting at 3 are represented, with bit 0 (the least-
	 * significant bit) representing 3.
	 */
	private static BigInteger primeSieve = BigInteger.ZERO;
	/**
	 * This represents the size in bits of the sieve that we have already
	 * set correctly. Any value bigger than this is undefined.
	 */
	private static long primeSieveMax = -1L;
	/**
	 * The maximum bit position of the current sieve, indicating the maximum prime
	 * found so far in the sieve.
	 */
	private static int primeSieveBitPos = 0;

	private static final long SIEVE_CHUNK_SIZE = 16384L;

	/**
	 * Construct or reconstruct the Sieve of Eratosthenes to the given size (in bits).
	 * <p> If the current size is at least the given size then nothing will happen.
	 * <p> To reduce the number of resizes required, the sieve is constructed in chunks
	 * of {@link #SIEVE_CHUNK_SIZE} bits.
	 * <p> For optimization, when we resize larger there should be no need to recompute
	 * everything from scratch, but simply use a segmented sieve algorithm to generate the
	 * next higher chunks. At bigger sizes this should substantially reduce the computation
	 * time.
	 *
	 * @param size The number of bits to construct, which corresponds to a maximum prime number
	 * of {@code size * 2 + 3}, since only the odd values are present and the bits start at 3.
	 * @see #primeSieve
	 * @see #primeSieveMax
	 * @see #primeSieveBitPos
	 */
	private static void constructSieve(final long size) {
	    long sizeInBits = (Math.max(size, SIEVE_CHUNK_SIZE) + SIEVE_CHUNK_SIZE - 1L) / SIEVE_CHUNK_SIZE * SIEVE_CHUNK_SIZE;

	    if (sizeInBits >= Integer.MAX_VALUE)
		throw new Intl.IllegalArgumentException("math#math.primeTooBig", BigInteger.valueOf(sizeInBits * 2L + 3L));

	    // Don't need to do anything if the sieve is already big enough
	    if (sizeInBits > primeSieveMax) {
		// In this implementation, a 0 bit means prime, 1 bit is composite.
		BigInteger sieve = primeSieve;

		// Only the odd bits are present, and correspond so:
		// bit 0 -> 3
		// bit 1 -> 5
		// bit 2 -> 7
		int bitPos = primeSieveBitPos;

		// Okay, okay. This is going to take an enormous amount of time even well below the maximum
		// bit position (2**31 - 1). Empirically a "sizeInBits" value of 1.5 billion takes hours to compute.
		// This actually should be done as a segmented sieve, where we start with the existing one and only
		// set the values from the previous top value up to the new bit size.
		while (bitPos >= 0 && bitPos <= (int) sizeInBits) {
		    int prime = (bitPos * 2) + 3;

		    // Start at prime * 3, increment by prime * 2 to get 3p, 5p, 7p, ...
		    // (since all the even multiples are even numbers, and thus NOT prime
		    // and not even represented in this bitmap)
		    for (int j = bitPos + prime; j <= (int) sizeInBits; j += prime) {
			sieve = sieve.setBit(j);
		    }

		    // This corresponds to the next larger prime number
		    int nextBitPos = findLowestClearBit(sieve, bitPos + 1, (int) sizeInBits);
		    if (nextBitPos < 0) {
			break;
		    }
		    bitPos = nextBitPos;
	        }

		// Save the cached sieve for next time
		primeSieve = sieve;
		primeSieveMax = sizeInBits;
		primeSieveBitPos = bitPos;
	    }
	}

	private static int findLowestClearBit(final BigInteger bitArray, final int startBitPos, final int lengthInBits) {
	    for (int bitPos = startBitPos; bitPos < lengthInBits; bitPos++) {
		if (!bitArray.testBit(bitPos))
		    return bitPos;
	    }
	    return -1;
	}

	/**
	 * Compute a number ~ the square root of the given (positive) number, which is
	 * at least as big as the integer square root, then change to a sieve bit position.
	 * This is the highest bit position to check for prime factors.
	 *
	 * @param posN	A positive number to be tested for primality or factored.
	 * @return	The maximum bit position that should be tested for prime factors.
	 */
	private static long maxPrimeBitPos(final BigInteger posN) {
	    long max = ((long) (Math.ceil(Math.sqrt(posN.doubleValue())) + 0.5d) + 1) * 2;
	    return (max + 1 - 3) / 2;
	}

	/**
	 * The prime numbers less than 1,000. For use with primality testing for small values.
	 */
	private static final int[] SMALL_PRIMES = {
		2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97,
		101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199,
		211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293,
		307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397,
		401, 409, 419, 421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499,
		503, 509, 521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 593, 599,
		601, 607, 613, 617, 619, 631, 641, 643, 647, 653, 659, 661, 673, 677, 683, 691,
		701, 709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 797,
		809, 811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887,
		907, 911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997
	};

	/** A "certainty" factor used with {@link BigInteger#isProbablePrime}. */
	private static final int PRIME_CERTAINTY = 10;

	/**
	 * Using a Sieve of Eratosthenes, figure out if the given number is prime.
	 * <p> Because this uses a bunch of space, the calculation is limited to
	 * a relatively small value (~10**7).
	 *
	 * @param n The number to check for possible prime-ness.
	 * @return  {@code true} if {@code n} is a prime number, {@code false}
	 *          if the number is composite.
	 * @throws  IllegalArgumentException if the number is "too big" for this method.
	 */
	public static boolean isPrime(final BigInteger n) {
	    // Negative numbers are essentially the same primality as their positive counterparts
	    BigInteger posN = n.abs();

	    if (posN.compareTo(MAX_PRIME) >= 0)
		throw new Intl.IllegalArgumentException("math#math.primeTooBig", posN);

	    // Easy decisions here: zero and one are not prime
	    if (posN.compareTo(BigInteger.ONE) <= 0)
		return false;

	    // While two IS prime
	    if (posN.equals(I_TWO))
		return true;

	    // Any other even number is NOT prime
	    if (posN.remainder(I_TWO).signum() == 0)
		return false;

	    // Now, make a preliminary pass to see if the number is divisible by any of the other "small" primes
	    // in the list above before we go to the expensive sieve operation.
	    for (int i = 1; i < SMALL_PRIMES.length; i++) {
		BigInteger smallPrime = BigInteger.valueOf(SMALL_PRIMES[i]);
		if (posN.compareTo(smallPrime) > 0) {
		    if (posN.remainder(smallPrime).signum() == 0)
			return false;
		}
		else {
		    // The number is now smaller than the next "small" prime and hasn't been divided yet,
		    // thence it is itself prime.
		    return true;
		}
	    }

	    // Quick test for "is this probably a prime" vs. "is this definitely composite"?
	    if (!posN.isProbablePrime(PRIME_CERTAINTY))
		return false;

	    // Choose a size for our sieve that is at least as big as the square root
	    // of the number in question (a little bit bigger is better)
	    long maxBitPos = maxPrimeBitPos(posN);

	    // Make a preliminary check in case the number itself is within the sieve size
	    // and we can just test directly
	    int bitPos = (posN.intValue() - 3) / 2;
	    if (bitPos >= 0 && bitPos < primeSieveMax)
		return !primeSieve.testBit(bitPos);

	    // Loop through all the primes in the sieve less than ~sqrt(n)
	    // to see if the number has any prime factors
	    // bitPos 0 corresponds to 3, and bitPos will only ever correspond
	    // to a "clear" bit in the sieve, which is a prime number
	    bitPos = (SMALL_PRIMES[SMALL_PRIMES.length - 1] - 3) / 2;
	    while (true) {
		int prime = (bitPos * 2) + 3;
		BigInteger iPrime = BigInteger.valueOf(prime);

		// If the number is divided evenly by one of the primes, then we have a factor
		// and the number is by definition NOT prime
		if (posN.remainder(iPrime).signum() == 0)
		    return false;

		// Create or expand the sieve to accommodate this next possible prime factor
		constructSieve(bitPos);

		int nextBitPos = findLowestClearBit(primeSieve, bitPos + 1, (int) maxBitPos);

		// No more possible prime factors below the square root -> the number must be prime
		if (nextBitPos < 0)
		    return true;

		bitPos = nextBitPos;
	    }
	}

	/**
	 * Get a list of all the factors of an integer number.
	 * <p> Because this can be expensive, limit to a relatively small value (~2**63).
	 *
	 * @param n The number to factor.
	 * @param factors The (empty) list to populate with the factors of the number..
	 * @throws  IllegalArgumentException if the number is "too big" for this method.
	 */
	public static void getFactors(final BigInteger n, final List<BigInteger> factors) {
	    int sign = n.signum();
	    BigInteger posN = (sign < 0) ? n.negate() : n;

	    if (posN.compareTo(MAX_PRIME) >= 0)
		throw new Intl.IllegalArgumentException("math#math.primeTooBig", posN);

	    // Zero has no factors
	    if (posN.signum() == 0)
		return;

	    // Every non-zero number has 1 and itself as a factor
	    factors.add(BigInteger.ONE);
	    if (sign < 0)
		factors.add(I_MINUS_ONE);

	    if (posN.equals(BigInteger.ONE))
		return;

	    factors.add(posN);
	    if (sign < 0)
		factors.add(posN.negate());

	    // For 1, 2, and 3 we are done already
	    if (posN.compareTo(I_THREE) <= 0)
		return;

	    BigInteger factor = I_TWO;
	    Set<BigInteger> existingFactors = new HashSet<>();

	    while (true) {
		// If we've gone beyond the square root, then we're done
		if (factor.multiply(factor).compareTo(posN) > 0)
		    break;

		if (!existingFactors.contains(factor)) {
		    BigInteger[] parts = posN.divideAndRemainder(factor);
		    if (parts[1].signum() == 0) {
		        existingFactors.add(factor);
			existingFactors.add(parts[0]);
		    }
		}

		factor = factor.add(BigInteger.ONE);
	    }

	    // Add all the factors we found to the list
	    for (BigInteger fact : existingFactors) {
		factors.add(fact);
		if (sign < 0)
		    factors.add(fact.negate());
	    }

	    // Finally, sort all the factors since they are kinda out of order
	    Collections.sort(factors);
	}

	private static BigInteger addPrimeFactors(final BigInteger value, final BigInteger factor, final int sign, final List<BigInteger> factors) {
	    BigInteger currentValue = value;
	    while (true) {
		BigInteger[] possibleFactorParts = currentValue.divideAndRemainder(factor);
		if (possibleFactorParts[1].signum() == 0) {
		    factors.add(factor);
		    if (sign < 0)
			factors.add(factor.negate());
		    currentValue = possibleFactorParts[0];
		}
		else {
		    // The given factor no longer evenly divides the value, so we are done as far as
		    // factoring by that prime factor
		    break;
		}
	    }

	    // Return the initial value with all the powers of the factor divided out
	    return currentValue;
	}

	/**
	 * Using a Sieve of Eratosthenes, figure out the prime factors of a small-ish number.
	 *
	 * @param n The number to factor.
	 * @param factors The (empty) list to populate with the prime factors.
	 * @throws  IllegalArgumentException if the number is "too big" for this method.
	 */
	public static void getPrimeFactors(final BigInteger n, final List<BigInteger> factors) {
	    int sign = n.signum();

	    // Zero has no factors
	    if (sign == 0)
		return;

	    BigInteger posN = (sign < 0) ? n.negate() : n;

	    if (posN.compareTo(MAX_PRIME) >= 0)
		throw new Intl.IllegalArgumentException("math#math.primeTooBig", posN);

	    // One has only itself
	    if (posN.equals(BigInteger.ONE)) {
		factors.add(BigInteger.ONE);
		if (sign < 0)
		    factors.add(I_MINUS_ONE);
		return;
	    }

	    // Factor out all the powers of all the "small" primes first
	    BigInteger currentN = posN;
	    for (int i = 0; i < SMALL_PRIMES.length; i++) {
		BigInteger smallPrime = BigInteger.valueOf(SMALL_PRIMES[i]);
		if (currentN.compareTo(smallPrime) > 0)
		    currentN = addPrimeFactors(currentN, smallPrime, sign, factors);
		else
		    break;
	    }

	    // Choose a size for our sieve that is at least as big as the square root
	    // of the number in question (a little bit bigger is better)
	    long maxBitPos = maxPrimeBitPos(currentN);

	    // Go through the sieve and find the prime factors
	    int bitPos = (SMALL_PRIMES[SMALL_PRIMES.length - 1] - 3) / 2;
	    while (true) {
		int prime = (bitPos * 2) + 3;
		BigInteger iPrime = BigInteger.valueOf(prime);

		// If we've gone beyond the square root, then we're done
		if (iPrime.multiply(iPrime).compareTo(currentN) > 0)
		    break;

		// Create or expand the sieve to accommodate this next possible prime factor
		constructSieve(bitPos);

		// If the number is divided evenly by one of the primes, then we have a factor
		currentN = addPrimeFactors(currentN, iPrime, sign, factors);

		// Also redo the max sieve position since we just reduced the number we're examining
		maxBitPos = maxPrimeBitPos(currentN);

		int nextBitPos = findLowestClearBit(primeSieve, bitPos + 1, (int) maxBitPos);

		// No more possible prime factors below the square root
		if (nextBitPos < 0)
		    break;

		bitPos = nextBitPos;
	    }

	    // Any remaining value not equal one will be the final prime factor
	    // (at most one prime factor greater than square root)
	    if (!currentN.equals(BigInteger.ONE)) {
		factors.add(currentN);
		if (sign < 0)
		    factors.add(currentN.negate());
	    }

	    // Finally, sort all the factors since they might be out of order (esp. if the number is negative)
	    Collections.sort(factors);
	}

	/**
	 * Calculate the natural logarithm of a number.
	 *
	 * @param input The value to calculate the natural logarithm of.
	 * @param mc    The desired precision and rounding mode for the result.
	 * @return      The value such that <code>e ** value == input</code>.
	 * @throws IllegalArgumentException if the input is negative or zero.
	 */
	public static BigDecimal ln(final BigDecimal input, final MathContext mc) {
	    if (input.signum() <= 0)
		throw new Intl.IllegalArgumentException("math#numeric.outOfRange");

	    // Calculate a sufficient number of loops for the value to converge nicely
	    int loops = mc.getPrecision() * 15;	// TODO: find out a good value
	    MathContext lc = newPrecision(mc, 10);

	    // Range reduce to get the argument value below one, so that:
	    // ln(x) = ln(e**n * x/e**n)
	    //       = ln(e**n) + ln(x/e**n)
	    //       = n * ln(e) + ln(x/e**n)
	    //       = n + ln(x/e**n)
	    // when x/e**n < 2
	    BigDecimal x = input;
	    BigDecimal eValue = e(lc);
	    BigDecimal eSmall = BigDecimal.ONE.divide(eValue, lc);
	    int n = 0;

	    while (x.compareTo(eValue) >= 0) {
		n++;
		x = x.divide(eValue, lc);
		logger.debug("ln.range reduce: n = %1$d, x = %2$s", n, x.toPlainString());
	    }
	    while (x.compareTo(eSmall) <= 0) {
		n--;
		x = x.multiply(eValue, lc);
		logger.debug("ln.range reduce: n = %1$d, x = %2$s", n, x.toPlainString());
	    }

	    BigDecimal xm1 = x.subtract(BigDecimal.ONE);	// (x - 1)
	    BigDecimal xp1 = x.add(BigDecimal.ONE);		// (x + 1)
	    BigDecimal term = xm1.divide(xp1, lc);		// (x - 1) / (x + 1)
	    BigDecimal sqterm = term.multiply(term, lc);	// ((x - 1) / (x + 1)) ** 2
	    BigDecimal power = term;
	    BigDecimal result = power;
	    BigDecimal lastResult = result;

	    for (int k = 3; k < loops * 2; k += 2) {
		BigDecimal kValue = new BigDecimal((long) k);
		power = power.multiply(sqterm, lc);
		BigDecimal loopTerm = power.divide(kValue, lc);
		result = result.add(loopTerm, lc);
		logger.debug("ln: k=%1$d, loopTerm=%2$s, result=%3$s", k, loopTerm.toPlainString(), result.toPlainString());
		if (result.equals(lastResult))
		    break;
		lastResult = result;
	    }

	    result = result.multiply(D_TWO, mc);
	    if (n != 0)
		result = result.add(new BigDecimal(n));

	    return result;
	}

	/**
	 * Calculate the logarithm base two of a number.
	 * <p> Calculation taken from <a href="http://www.claysturner.com/dsp/BinaryLogarithm.pdf">http://www.claysturner.com/dsp/BinaryLogarithm.pdf</a>
	 *
	 * @param input The value to calculate the logarithm base two of.
	 * @param mc    The desired precision and rounding mode for the result.
	 * @return      The value such that <code>2 ** value == input</code>.
	 * @throws IllegalArgumentException if the input is negative or zero.
	 */
	public static BigDecimal ln2(final BigDecimal input, final MathContext mc) {
	    if (input.signum() <= 0)
		throw new Intl.IllegalArgumentException("math#numeric.outOfRange");

	    BigDecimal y = BigDecimal.ZERO;
	    BigDecimal b = D_ONE_HALF;
	    BigDecimal x = input;

	    // Get the integer number power required to get the input between one and two
	    // from where we can get the fractional value below
	    while (x.compareTo(BigDecimal.ONE) < 0 || x.compareTo(D_TWO) >= 0) {
		while (x.compareTo(BigDecimal.ONE) < 0) {
		    logger.debug("ln2: x = %1$s, y = %2$s", x.toPlainString(), y.toPlainString());
		    x = x.multiply(D_TWO);
		    y = y.subtract(BigDecimal.ONE);
		}
		while (x.compareTo(D_TWO) >= 0) {
		    logger.debug("ln2: x = %1$s, y = %2$s", x.toPlainString(), y.toPlainString());
		    x = x.divide(D_TWO);
		    y = y.add(BigDecimal.ONE);
		}
	    }

	    // Intermediate calculations can use a couple extra digits of precision to ensure
	    // LSD accuracy at the end
	    MathContext mc2 = newPrecision(mc, 2);

	    // Since we are calculating one bit at a time, the number of loops should be about
	    // precision * ln2(10) or about 3.3219 or say 3.4 to give us some wiggle room
	    int loops = (mc.getPrecision() + 1) * 17 / 5;
	    logger.debug("ln2: x = %1$s, y = %2$s, loops = %3$d", x.toPlainString(), y.toPlainString(), loops);

	    for (int loop = 0; loop < loops; loop++) {
		logger.debug("ln2: loop = %1$d, x = %2$s, y = %3$s, b = %4$s", loop, x.toPlainString(), y.toPlainString(), b.toPlainString());
		x = x.multiply(x, mc2);
		if (x.compareTo(D_TWO) >= 0) {
		    x = x.divide(D_TWO);
		    y = y.add(b);
		}
		b = b.divide(D_TWO);
	    }

	    return fixup(y, mc);
	}

	/**
	 * Calculate the "ceil" of the given value, which is the smallest value greater than or equal
	 * to the given value, which is an exact integer.
	 *
	 * @param value	The given value.
	 * @return	The {@code ceil} of that value.
	 */
	public static BigInteger ceil(final BigDecimal value) {
	    return value.setScale(0, RoundingMode.CEILING).unscaledValue();
	}

	/**
	 * Calculate the "floor" of the given value, which is the largest value less than or equal
	 * to the given value, which is an exact integer.
	 *
	 * @param value	The given value.
	 * @return	The {@code floor} of that value.
	 */
	public static BigInteger floor(final BigDecimal value) {
	    return value.setScale(0, RoundingMode.FLOOR).unscaledValue();
	}

	/**
	 * Calculate the value of {@code x mod y := x - y * floor(x / y)} for given
	 * decimal x and y values. By definition, to avoid division by zero, the
	 * value of {@code x mod 0 := x}.
	 *
	 * @param x   As above, the value to be operated on.
	 * @param y   The modulus value.
	 * @param mc  The precision to use.
	 * @return    The value of {@code x mod y}.
	 */
	public static BigDecimal modulus(final BigDecimal x, final BigDecimal y, final MathContext mc) {
	    // x mod 0 == x
	    if (y.signum() == 0)
		return x;

	    BigDecimal floorValue = x.divide(y, mc).setScale(0, RoundingMode.FLOOR);
	    return x.subtract(y.multiply(floorValue));
	}

	/**
	 * Convert {@link BigDecimal} to string in the given radix.
	 *
	 * @param value	The value to convert.
	 * @param radix	The base to convert to.
	 * @param mc	Rounding context for conversion of the fraction.
	 * @return	Value converted to the base.
	 */
	public static String toString(final BigDecimal value, final int radix, final MathContext mc) {
	    StringBuilder buf = new StringBuilder(value.precision() * 2);
	    BigDecimal scale = new BigDecimal(radix);
	    BigInteger intPart = floor(value);
	    buf.append(intPart.toString(radix));

	    BigDecimal fracPart = value.subtract(new BigDecimal(intPart));
	    if (fracPart.signum() != 0) {
		int adjPrec = (int) ((double) mc.getPrecision() * Math.log(10) / Math.log(radix) + 0.5d);
		int digits = buf.length();
		buf.append('.');
		while (fracPart.signum() != 0 && digits < adjPrec) {
		    fracPart = fracPart.multiply(scale, mc);
		    intPart = floor(fracPart);
		    fracPart = fracPart.subtract(new BigDecimal(intPart));
		    buf.append(intPart.toString(radix));
		    digits = buf.length();
		}
		// Last digit needs to be rounded
		fracPart = fracPart.multiply(scale, mc);
		intPart = floor(fracPart.round(MC_ONE));
		buf.append(intPart.toString(radix));

		// Remove trailing zeros
		int len = buf.length();
		while (len > 0 && buf.charAt(len - 1) == '0') {
		    len--;
		}
		buf.setLength(len);
	    }

	    return buf.toString();
	}

	/**
	 * Convert string in base N form back to numeric form.
	 *
	 * @param value	The value to convert back to a number.
	 * @param radix	The base to convert from.
	 * @param mc	Rounding context for the result.
	 * @return	The number represented in the given base by the input.
	 */
	public static BigDecimal fromString(final String value, final int radix, final MathContext mc) {
	    int pointPos = value.indexOf('.');
	    String intPart = pointPos < 0 ? value : value.substring(0, pointPos);
	    BigInteger integer = new BigInteger(intPart, radix);
	    BigDecimal fraction = BigDecimal.ZERO;
	    BigDecimal divisor = new BigDecimal(radix);
	    BigDecimal mult = new BigDecimal(radix);
	    MathContext mcDivide = newPrecision(mc, mc.getPrecision());

	    if (pointPos > 0) {
		for (int pos = pointPos + 1; pos < value.length(); pos++) {
		    String digit = value.substring(pos, pos + 1);
		    BigDecimal place = new BigDecimal(new BigInteger(digit, radix));
		    fraction = fraction.add(place.divide(divisor, mcDivide));
		    divisor = divisor.multiply(mult);
		}
	    }

	    return new BigDecimal(integer).add(fraction, mc);
	}

	/**
	 * Generate random {@link BigDecimal} value between {@code 0.0} and {@code 1.0}.
	 *
	 * @param seed An optional random seed value (can be set for repeatable "random"
	 *             sequences, or to seed with an even more random value).
	 * @param prec The number of digits of precision to calculate.
	 * @param mc   Math Context for rounding, etc. for the final result.
	 * @return     The next random value.
	 */
	public static BigDecimal random(final Object seed, final int prec, final MathContext mc) {
	    if (seed != null) {
		byte[] bytes = ClassUtil.getBytes(seed);
		BigInteger seedInt = new BigInteger(bytes);
		random = new Random(seedInt.longValue());
	    }
	    if (random == null) {
		random = new SecureRandom();
	    }
	    int bits = (int) (prec * BITS_PER_DIGIT) + 1;
	    BigInteger randomBits = new BigInteger(bits, random);
	    BigDecimal dValue = new BigDecimal(randomBits, mc);
	    return dValue.scaleByPowerOfTen(dValue.scale() - prec);
	}

	/**
	 * Calculate the minimum of a series of integers.
	 *
	 * @param values The set of values to inspect.
	 * @return       Minimum value of the input set.
	 */
	public static int minimum(final int... values) {
	    return new MinInt(values).get();
	}

	/**
	 * Calculate the maximum of a series of integers.
	 *
	 * @param values The set of values to inspect.
	 * @return       Maximum value of the input set.
	 */
	public static int maximum(final int... values) {
	    return new MaxInt(values).get();
	}

}
