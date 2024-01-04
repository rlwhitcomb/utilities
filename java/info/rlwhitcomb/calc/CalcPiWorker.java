/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2022,2024 Roger L. Whitcomb.
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
 *  History:
 *      31-Dec-2020 (rlwhitcomb)
 *	    Initial version.
 *	26-Mar-2021 (rlwhitcomb)
 *	    Move some methods from NumericUtil to MathUtil.
 *	01-Dec-2021 (rlwhitcomb)
 *	    #114: Fix final precision of e/pi compared to "phi" (normal precision).
 *	03-Dec-2021 (rlwhitcomb)
 *	    #122: Refactor to reduce duplicated code.
 *	26-Jan-2022 (rlwhitcomb)
 *	    Make new Supplier methods for CalcPredefine.
 *	06-Feb-2022 (rlwhitcomb)
 *	    Also calculate pi/2.
 *	14-Apr-2022 (rlwhitcomb)
 *	    #273: Move math-related classes to "math" package.
 *	08-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 *	24-Aug-2022 (rlwhitcomb)
 *	    #447: Add constant for pi/200 for gradian/grad conversion.
 *	30-Sep-2022 (rlwhitcomb)
 *	    #288: Return best fractional values for PI in rational mode.
 *	01-Oct-2022 (rlwhitcomb)
 *	    #288: Rename "piFraction" to "ratpi".
 *	03-Jan-2024 (rlwhitcomb)
 *	    #640: Refactor.
 */
package info.rlwhitcomb.calc;

import info.rlwhitcomb.math.BigFraction;
import info.rlwhitcomb.math.MathUtil;
import info.rlwhitcomb.util.ClassUtil;
import info.rlwhitcomb.util.QueuedThread;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

import static info.rlwhitcomb.util.Constants.*;


/**
 * Calculates and maintains the value of <code>pi</code> and <code>e</code>
 * to what could be a significant number of decimal places (and therefore
 * requiring some seconds of calculation time, potentially) by doing the
 * expensive calculations in a background thread.
 */
public class CalcPiWorker
{
	private MathContext mc = null;
	private int precision;
	private boolean rational = false;

	/**
	 * Euler's constant (e) or the base of the natural logarithms.
	 */
	private BigDecimal e;
	/**
	 * The ratio of the circumference to the diameter of a circle (or pi).
	 */
	private BigDecimal pi;
	/**
	 * One half of {@link #pi}.
	 */
	private BigDecimal piOver2;
	/**
	 * {@link #pi} divided by 180, or the conversion between degrees and radians.
	 */
	private BigDecimal piOver180;
	/**
	 * {@link #pi} divided by 200, or the conversion between grads and radians.
	 */
	private BigDecimal piOver200;


	/** The captive thread used to do the background calculations. */
	private final QueuedThread queuedThread = new QueuedThread();

	/** Initialize the semaphore with one permit available, which will
	 *  be acquired right away from the constructor when it calls
	 *  {@code calculate()}.
	 */
	private final Semaphore readySem = new Semaphore(1);


	/**
	 * The only constructor of this object.
	 * <p> Use the {@link #apply} method before attempting to acquire any values.
	 */
	public CalcPiWorker() {
	}

	/**
	 * Apply the new settings to this object and initiate a recalculation if necessary.
	 *
	 * @param newMC  New settings for math precision.
	 * @param newRat Whether we need rational values or not.
	 */
	public void apply(final MathContext newMC, final boolean newRat) {
	    // This value is only used in the "supplier" code and changing it
	    // doesn't require a new calculation
	    rational = newRat;

	    // Only start a new calculation if the settings have changed
	    if (!ClassUtil.objectsEqual(mc, newMC)) {
		readySem.acquireUninterruptibly();

		mc        = newMC;
		precision = mc.getPrecision();

		// Starting a new calculation; the result is not available until it's done
		queuedThread.submitWork( () -> calculate() );
	    }
	}

	/**
	 * The process run in the background thread to do the actual calculations.
	 * <p> Once the calculation is finished, release a permit to the semaphore
	 * so others can access the values.
	 * <p> Note: this calculation goes two more places than necessary, which helps
	 * other calculations be more accurate. The final accuracy (see {@link #getE}
	 * and {@link #getPi} is determined exactly by the desired precision.
	 */
	private void calculate() {
	    e         = MathUtil.e(precision + 1);
	    pi        = MathUtil.pi(precision + 1);

	    piOver2   = pi.divide(D_TWO, mc);
	    piOver180 = pi.divide(D_180, mc);
	    piOver200 = pi.divide(D_200, mc);

	    // Release a permit to say the calculation results are now available
	    readySem.release();
	}

	/**
	 * Generic function to wait on the {@link #readySem} before returning the value
	 * produced by the given supplier.
	 * <p> Once the value is ready the semaphore is released for the next caller.
	 *
	 * @param f	The value supplier lambda expression.
	 * @return	The value produced by the supplier.
	 */
	private BigDecimal getWhenReady(final Supplier<BigDecimal> f) {
	    ClassUtil.throwNullException(mc, "calc#workerNotStarted");

	    readySem.acquireUninterruptibly();
	    try {
		return f.get();
	    }
	    finally {
		readySem.release();
	    }
	}

	/**
	 * Get the calculated value of <code>e</code> to the current number of digits.
	 * <p> Waits until the background thread is done with the calculation
	 * if a new precision was just recently specified.
	 *
	 * @return The value of <code>e</code> to the current precision.
	 */
	public BigDecimal getE() {
	    return getWhenReady( () -> e.round(mc) );
	}

	/**
	 * Get the calculated value of <code>pi</code> to the current number of digits.
	 * <p> Waits until the background thread is done with the calculation
	 * if a new precision was just recently specified.
	 *
	 * @return The value of <code>pi</code> to the current precision.
	 */
	public BigDecimal getPi() {
	    return getWhenReady( () -> pi.round(mc) );
	}

	/**
	 * Get the calculated value of <code>pi / 2</code> to the current number of digits.
	 * <p> Waits until the background thread is done with the calculation
	 * if a new precision was just recently specified.
	 *
	 * @return The value of <code>pi / 2</code> to the current precision.
	 */
	public BigDecimal getPiOver2() {
	    return getWhenReady( () -> piOver2 );
	}

	/**
	 * Get the calculated value of <code>pi / 180</code> to the current number of digits.
	 * <p> Waits until the background thread is done with the calculation
	 * if a new precision was just recently specified.
	 *
	 * @return The value of <code>pi / 180</code> to the current precision.
	 */
	public BigDecimal getPiOver180() {
	    return getWhenReady( () -> piOver180 );
	}

	/**
	 * Get the calculated value of <code>pi / 200</code> to the current number of digits.
	 * <p> Waits until the background thread is done with the calculation
	 * if a new precision was just recently specified.
	 *
	 * @return The value of <code>pi / 200</code> to the current precision.
	 */
	public BigDecimal getPiOver200() {
	    return getWhenReady( () -> piOver200 );
	}

	/**
	 * Set the current value of the rational flag for use with the suppliers below.
	 *
	 * @param rationalFlag The current value of the rational flag.
	 */
	public void setRational(final boolean rationalFlag) {
	    rational = rationalFlag;
	}

	/**
	 * Get either the decimal or rational value of <code>pi</code>, depending
	 * on the {@link #rational} flag.
	 */
	public Supplier<Object> piSupplier = () -> {
	    if (rational) {
		BigFraction value = MathUtil.ratpi(precision);
		if (value != null) {
		    return value;
		}
	    }

	    BigDecimal value = getPi();
	    return rational ? new BigFraction(value) : value;
	};

	/**
	 * Get either the decimal or rational value of <code>e</code>, depending
	 * on the {@link #rational} flag.
	 */
	public Supplier<Object> eSupplier = () -> {
	    BigDecimal dValue = getE();

	    if (rational)
		return new BigFraction(dValue);
	    else
		return dValue;
	};

}

