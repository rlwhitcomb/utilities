/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Roger L. Whitcomb.
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
 *  History:
 *	26-Mar-2021 (rlwhitcomb)
 *	    Moved out of NumericUtil into this separate class.
 *	27-Mar-2021 (rlwhitcomb)
 *	    Add "ePower" method (which is e**x, or anti-logarithm).
 *	27-Mar-2021 (rlwhitcomb)
 *	    Clean up code in "pow()"
 *	29-Mar-2021 (rlwhitcomb)
 *	    Implement simpler, faster "ln2" function.
 *	    Rename the resource strings.
 */
package info.rlwhitcomb.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * A static class used for trigonometric and logarithmic calculations, as well as
 * other "mathematical" operations, such as square root, Bernoulli numbers, factorial,
 * and etc.
 */
public class MathUtil
{
	private static final Logging logger = new Logging(MathUtil.class);

	private static final BigInteger I_TWO = BigInteger.valueOf(2L);
	private static final BigDecimal D_TWO = BigDecimal.valueOf(2L);

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


	/**
	 * Since this is a static class, make the constructor private so no one
	 * can instantiate it.
	 */
	private MathUtil() {
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
	 * @return The result of the base to the exp power, done in <code>BigDecimal</code>
	 * precision.
	 * @param base     The number to raise to the given power.
	 * @param inputExp The power to raise the number to.
	 * @throws IllegalArgumentException if the exponent is infinite or not-a-number.
	 */
	public static BigDecimal pow(final BigDecimal base, final double inputExp) {
	    double exp = inputExp;
	    if (Double.isNaN(exp) || Double.isInfinite(exp))
		throw new Intl.IllegalArgumentException("util#numeric.outOfRange");

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

	    if (reciprocal) {
		result = BigDecimal.ONE.divide(result);
	    }

	    return result;
	}


	/**
	 * @return The result of the base to the exp power, done in <code>BigInteger</code>,
	 * or <code>BigDecimal</code> precision depending on the value of the exponent.
	 * @param base The number to raise to the given power.
	 * @param exp The power to raise the number to.
	 * @throws IllegalArgumentException if the exponent is infinite, or not-a-number.
	 */
	public static Number pow(final BigInteger base, final double exp) {
	    if (Double.isNaN(exp) || Double.isInfinite(exp))
		throw new Intl.IllegalArgumentException("util#numeric.outOfRange");
	    if (exp == 0.0d)
		return BigInteger.ONE;

	    // Test for negative or fractional powers and convert to BigDecimal for those cases
	    double wholeExp = Math.floor(exp);
	    if (exp < 0.0d || wholeExp != exp) {
		return pow(new BigDecimal(base), exp);
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
	    return new BigInteger(CharUtil.padToWidth("1", pow + 1, '0'));
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
		throw new Intl.IllegalArgumentException("util#math.wholeInteger");

	    long loops = base.longValue();

	    if (loops == 0L || loops == 1L)
		return BigDecimal.ONE;

	    boolean negative = false;
	    if (loops < 0L) {
		negative = true;
		loops = -loops - 1L;
	    }
	    BigInteger result = BigInteger.ONE;
	    BigInteger term   = BigInteger.ONE;

	    for (long i = 2L; i <= loops; i++) {
		term   = term.add(BigInteger.ONE);
		result = result.multiply(term);
	    }

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
		throw new Intl.IllegalArgumentException("util#math.wholeInteger");

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
	private static BigFraction bern(int n) {
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
	 * @return	Then N-th Bernoulli number as a decimal (rounded to {@code mc}),
	 *		or as a fraction.
	 */
	public static Object bernoulli(int n, MathContext mc, boolean rational) {
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
	    if (rational) {
		return bn;
	    }
	    else {
		return bn.toDecimal(mc);
	    }
	}

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
	    int loops = mc.getPrecision() * 3 / 2;

	    MathContext mc2 = new MathContext(mc.getPrecision() * 2);

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

	    return result.round(mc);
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
	    int loops = mc.getPrecision() * 3 / 2;

	    MathContext mc2 = new MathContext(mc.getPrecision() * 2);

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

	    return result.round(mc);
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
	    MathContext mc2 = new MathContext(mc.getPrecision() + 2);

	    BigDecimal xValue = toDecimal(x, mc2);

	    pi(mc2.getPrecision());

	    /* First, do some range reductions to the range of -pi/2 to pi/2 */
	    if (xValue.compareTo(MINUS_PI_OVER_TWO) < 0 || xValue.compareTo(PI_OVER_TWO) > 0) {
		xValue = xValue.remainder(PI_OVER_TWO, mc);
	    }

	    // Some simplifications
	    if (xValue.equals(BigDecimal.ZERO))
		return BigDecimal.ZERO;

	    BigDecimal result     = xValue;
	    BigInteger FOUR       = BigInteger.valueOf(4L);
	    BigInteger twoPower   = FOUR;
	    BigDecimal xPower     = xValue;
	    BigDecimal xSquared   = xValue.multiply(xValue);
	    BigInteger factTerm   = I_TWO;
	    BigInteger factorial  = I_TWO; // 2!
	    BigDecimal lastResult = result;
	    BigInteger numer;

	    // This seems to require (precision/4) * input/0.1 iterations, so for
	    // precision 20, about 5 * 0.1, for 34 about 8 * 0.1, etc.
	    int approxRange = (int)Math.floor(xValue.divide(new BigDecimal("0.1"), mc).doubleValue()) + 1;
	    int loopCountPerRange = (mc.getPrecision() + 3) / 4;
	    int loops = (approxRange + approxRange / 6) * loopCountPerRange + 3;
	    logger.debug("tan: precision = %1$d, approx range = %2$d, loops per = %3$d -> loops = %4$d", mc.getPrecision(), approxRange, loopCountPerRange, loops);
	    // Big decision here:  at some point at around 1.2 (where approxRange / 6 is > 1) we start getting
	    // diminishing returns, so switch to using sin(x) / cos(x) as quicker AND more accurate
	    if (approxRange > 12) {
		return sin(xValue, mc).divide(cos(xValue, mc), mc);
	    }

	    for (int i = 2; i < loops; i++) {
		twoPower  = twoPower.multiply(FOUR);
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

	    return result.round(mc);
	}

	/**
	 * Find the positive square root of a number (non-negative).
	 *
	 * @param x	The value to find the square root of.
	 * @param mc	The {@code MathContext} to use for rounding / calculating the result.
	 * @return	The {@code sqrt(x)} value such that {@code x = result * result}.
	 */
	public static BigDecimal sqrt(final BigDecimal x, final MathContext mc) {
	    if (x.signum() < 0)
		throw new Intl.IllegalArgumentException("util#math.sqrtNegative");
	    if (x.equals(BigDecimal.ZERO) || x.equals(BigDecimal.ONE))
		return x;

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
	    return result.round(mc);
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
		BigDecimal denom = x2.multiply(BigDecimal.valueOf(3L));
		result = result.add(numer.divide(denom, mc), mc);
		logger.debug("cbrt: result = %1$s, lastResult = %2$s", result.toPlainString(), lastResult.toPlainString());
		if (result.equals(lastResult)) {
		    logger.debug("cbrt: break out early, i = %1$d", i);
		    break;
		}
		lastResult = result;
	    }
	    return (sign < 0) ? result.negate(mc) : result.plus(mc);
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
		throw new Intl.IllegalArgumentException("util#math.tooManyPiDigits");

	    // Since each loop reduces the count by 14 while only providing 4 digits
	    // of output, in order to produce the required number of digits we must
	    // scale up the loop count proportionally. Add one more loop to make sure
	    // we have enough.
	    int loops = (digits + 1) * 14 / 4;

	    StringBuffer pi = new StringBuffer(loops);
	    int[] arr = new int[loops + 1];
	    int carry = 0;

	    for (int i = 0; i <= loops; ++i)
		arr[i] = ARRINIT;

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
		throw new Intl.IllegalStateException("util#math.piDigitMismatch",
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
	    BigDecimal result    = exp.add(BigDecimal.ONE);
	    BigDecimal numer     = exp;
	    BigDecimal factorial = BigDecimal.ONE;

	    // loops is a little extra to make sure the last digit we want is accurate
	    int loops = mc.getPrecision() + 10;

	    for (int i = 2; i < loops; i++) {
		factorial = factorial.multiply(BigDecimal.valueOf(i));
		// compute x**i/i!, note divide is overloaded, this version is used to
		//    ensure a limit to the iterations when division is limitless like 1/3
		numer = numer.multiply(exp);
		BigDecimal term = numer.divide(factorial, loops, RoundingMode.HALF_UP);
		result = result.add(term);
	    }
	    return result.round(mc);
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
		TWO_PI            = CALCULATED_PI.multiply(D_TWO, mc);
		MINUS_TWO_PI      = TWO_PI.negate();
		PI_OVER_TWO       = CALCULATED_PI.divide(D_TWO, mc);
		MINUS_PI_OVER_TWO = PI_OVER_TWO.negate();
	    }

	    return CALCULATED_PI;
	}


	/** The previously calculated E value (if any); cached to eliminate repeated costly calculations. */
	private static BigDecimal CALCULATED_E = null;
	/** The largest number of digits of E calculated so far: use a substring for lesser precision. */
	private static String E_DIGITS = null;

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

	private static final BigInteger MAX_PRIME = BigInteger.valueOf(Integer.MAX_VALUE);

	private static BigInteger primeSieve = BigInteger.ZERO;
	private static int primeSieveMax = -1;

	private static void constructSieve(final int size) {
	    int sizeInBits = roundUpPowerTwo(Math.max(size, 100));

	    // Don't need to do anything if the sieve is already big enough
	    if (sizeInBits > primeSieveMax) {
		// In this implementation, a 0 bit means prime, 1 bit is composite.
		BigInteger sieve = BigInteger.ZERO;

		// Only the odd bits are present, and correspond so:
		// bit 0 -> 3
		// bit 1 -> 5
		// bit 2 -> 7
		int bitPos = 0;
		while (bitPos >= 0 && bitPos <= sizeInBits) {
		    int prime = (bitPos * 2) + 3;

		    // Start at prime * 3, increment by prime * 2
		    // to get 3p, 5p, 7p, ... (since all the even multiples
		    // are even numbers, and thus NOT prime and not represented
		    // in this bitmap)
		    for (int j = bitPos + prime; j <= sizeInBits; j += prime) {
			sieve = sieve.setBit(j);
		    }

		    int nextBitPos = findLowestClearBit(sieve, bitPos + 1, sizeInBits);
		    if (nextBitPos < 0) {
			break;
		    }
		    bitPos = nextBitPos;
	        }

		// Save the cached sieve for next time
		primeSieve = sieve;
		primeSieveMax = sizeInBits;
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
	private static int maxPrimeBitPos(final BigInteger posN) {
	    int max = ((int)(Math.ceil(Math.sqrt(posN.doubleValue())) + 0.5d) + 1) * 2;
	    return (max + 1 - 3) / 2;
	}

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

	    if (posN.compareTo(MAX_PRIME) > 0)
		throw new Intl.IllegalArgumentException("util#math.primeTooBig");

	    // Easy decisions here: zero and one are not prime
	    if (posN.compareTo(BigInteger.ONE) <= 0)
		return false;

	    // While two IS prime
	    if (posN.equals(I_TWO))
		return true;

	    // Any other even number is NOT prime
	    if (posN.remainder(I_TWO).equals(BigInteger.ZERO))
		return false;

	    // Choose a size for our sieve that is at least as big as the square root
	    // of the number in question (a little bit bigger is better)
	    int maxBitPos = maxPrimeBitPos(posN);

	    // Create or expand the sieve to accommodate this ~square root value
	    constructSieve(maxBitPos);

	    // Make a preliminary check in case the number itself is within the sieve size
	    // and we can just test directly
	    int bitPos = (posN.intValue() - 3) / 2;
	    if (bitPos < primeSieveMax)
		return !primeSieve.testBit(bitPos);

	    // Loop through all the primes in the sieve less than ~sqrt(n)
	    // to see if the number has any prime factors
	    // bitPos 0 corresponds to 3, and bitPos will only ever correspond
	    // to a "clear" bit in the sieve, which is a prime number
	    bitPos = 0;
	    while (true) {
		int prime = (bitPos * 2) + 3;
		BigInteger iPrime = BigInteger.valueOf(prime);

		// If the number is divided evenly by one of the primes, then we have a factor
		// and the number is by definition NOT prime
		if (posN.remainder(iPrime).equals(BigInteger.ZERO))
		    return false;

		int nextBitPos = findLowestClearBit(primeSieve, bitPos + 1, maxBitPos);

		// No more possible prime factors below the square root -> the number must be prime
		if (nextBitPos < 0)
		    return true;

		bitPos = nextBitPos;
	    }
	}

	/**
	 * Get a list of all the factors of an integer number.
	 * <p> Because this can be expensive, limit to a relatively small value (~10**7).
	 *
	 * @param n The number to factor.
	 * @param factors The (empty) list to populate with the factors of the number..
	 * @throws  IllegalArgumentException if the number is "too big" for this method.
	 */
	public static void getFactors(final BigInteger n, final List<Integer> factors) {
	    int sign = n.signum();
	    BigInteger posN = (sign < 0) ? n.negate() : n;

	    if (posN.compareTo(MAX_PRIME) > 0)
		throw new Intl.IllegalArgumentException("util#math.primeTooBig");

	    // Zero has no factors
	    if (posN.equals(BigInteger.ZERO))
		return;

	    // Every non-zero number has 1 and itself as a factor
	    factors.add(1);
	    if (sign < 0)
		factors.add(-1);

	    if (posN.equals(BigInteger.ONE))
		return;

	    int intFactor = posN.intValue();
	    factors.add(intFactor);
	    if (sign < 0)
		factors.add(-intFactor);

	    // For 1, 2, and 3 we are done already
	    if (posN.compareTo(BigInteger.valueOf(3)) <= 0)
		return;

	    BigInteger factor = I_TWO;
	    Set<BigInteger> existingFactors = new HashSet<>();

	    while (true) {
		// If we've gone beyond the square root, then we're done
		if (factor.multiply(factor).compareTo(posN) > 0)
		    break;

		if (!existingFactors.contains(factor)) {
		    BigInteger[] parts = posN.divideAndRemainder(factor);
		    if (parts[1].equals(BigInteger.ZERO)) {
		        existingFactors.add(factor);
			existingFactors.add(parts[0]);
		    }
		}

		factor = factor.add(BigInteger.ONE);
	    }

	    // Add all the factors we found to the list
	    for (BigInteger fact : existingFactors) {
		intFactor = fact.intValue();
		factors.add(intFactor);
		if (sign < 0)
		    factors.add(-intFactor);
	    }

	    // Finally, sort all the factors since they are kinda out of order
	    Collections.sort(factors);
	}

	private static BigInteger addPrimeFactors(final BigInteger value, final BigInteger factor, final int sign, final List<Integer> factors) {
	    BigInteger currentValue = value;
	    while (true) {
		BigInteger[] possibleFactorParts = currentValue.divideAndRemainder(factor);
		if (possibleFactorParts[1].equals(BigInteger.ZERO)) {
		    int intFactor = factor.intValue();
		    factors.add(intFactor);
		    if (sign < 0)
			factors.add(-intFactor);
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
	 * <p> Because this uses a bunch of space, the calculation is limited to
	 * a relatively small value (~10**7).
	 * <p> NOTE: this is incomplete and incorrect as of 3/5/21 as I think through how
	 * to implement.
	 *
	 * @param n The number to factor.
	 * @param factors The (empty) list to populate with the prime factors.
	 * @throws  IllegalArgumentException if the number is "too big" for this method.
	 */
	public static void getPrimeFactors(final BigInteger n, final List<Integer> factors) {
	    int sign = n.signum();
	    BigInteger posN = (sign < 0) ? n.negate() : n;

	    if (posN.compareTo(MAX_PRIME) > 0)
		throw new Intl.IllegalArgumentException("util#math.primeTooBig");

	    // Zero has no factors
	    if (posN.equals(BigInteger.ZERO))
		return;

	    // One has only itself
	    if (posN.equals(BigInteger.ONE)) {
		factors.add(1);
		if (sign < 0)
		    factors.add(-1);
		return;
	    }

	    // Factor out all the powers of two first
	    BigInteger currentN = addPrimeFactors(posN, I_TWO, sign, factors);

	    // Choose a size for our sieve that is at least as big as the square root
	    // of the number in question (a little bit bigger is better)
	    int maxBitPos = maxPrimeBitPos(currentN);

	    // Create or expand the sieve to accommodate this ~square root value
	    constructSieve(maxBitPos);

	    // Go through the sieve and find the prime factors
	    int bitPos = 0;	// corresponds to 3, which is a prime
	    while (true) {
		int prime = (bitPos * 2) + 3;
		BigInteger iPrime = BigInteger.valueOf(prime);

		// If we've gone beyond the square root, then we're done
		if (iPrime.multiply(iPrime).compareTo(currentN) > 0)
		    break;

		// If the number is divided evenly by one of the primes, then we have a factor
		currentN = addPrimeFactors(currentN, iPrime, sign, factors);

		int nextBitPos = findLowestClearBit(primeSieve, bitPos + 1, maxBitPos);

		// No more possible prime factors below the square root
		if (nextBitPos < 0)
		    break;

		bitPos = nextBitPos;
	    }

	    // Any remaining value not equal one will be the final prime factor
	    // (at most one prime factor greater than square root)
	    if (!currentN.equals(BigInteger.ONE)) {
		int intFactor = currentN.intValue();
		factors.add(intFactor);
		if (sign < 0)
		    factors.add(-intFactor);
	    }

	    // Finally, sort all the factors since they might be out of order (esp. if the number is negative)
	    Collections.sort(factors);
	}


	/**
	 * Calculate the logarithm base two of a number.
	 * <p> Calculation taken from <a href="http://www.claysturner.com/dsp/BinaryLogarithm.pdf">http://www.claysturner.com/dsp/BinaryLogarithm.pdf</a>
	 *
	 * @param input The value to calculate the logarithm base two of.
	 * @param mc    The desired precision and rounding mode for the result.
	 * @return The value such that <code>2 ** value == input</code>.
	 * @throws IllegalArgumentException if the input is negative or zero.
	 */
	public static BigDecimal ln2(final BigDecimal input, final MathContext mc) {
	    if (input.compareTo(BigDecimal.ZERO) <= 0)
		throw new Intl.IllegalArgumentException("util#numeric.outOfRange");

	    BigDecimal y = BigDecimal.ZERO;
	    BigDecimal b = new BigDecimal("0.5");
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
	    MathContext mc2 = new MathContext(mc.getPrecision() + 2, mc.getRoundingMode());

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

	    return y.round(mc);
	}

}