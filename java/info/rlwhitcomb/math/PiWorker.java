/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2022,2024-2026 Roger L. Whitcomb.
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
 * History:
 *  31-Dec-20 rlw ----	Initial version.
 *  26-Mar-21 rlw ----	Move some methods from NumericUtil to MathUtil.
 *  01-Dec-21 rlw #114	Fix final precision of e/pi compared to "phi" (normal precision).
 *  03-Dec-21 rlw #122	Refactor to reduce duplicated code.
 *  26-Jan-22 rlw ----	Make new Supplier methods for CalcPredefine.
 *  06-Feb-22 rlw ----	Also calculate pi/2.
 *  14-Apr-22 rlw #273	Move math-related classes to "math" package.
 *  08-Jul-22 rlw #393	Cleanup imports.
 *  24-Aug-22 rlw #447	Add constant for pi/200 for gradian/grad conversion.
 *  30-Sep-22 rlw #288	Return best fractional values for PI in rational mode.
 *  01-Oct-22 rlw #288	Rename "piFraction" to "ratpi".
 *  03-Jan-24 rlw #640	Refactor.
 *  30-Jul-25 rlw #746	Add "ln(10)" to the calculation.
 *  28-Jan-26 rlw #809	Rename and change packages.
 *  08-Mar-26 rlw #818	Allow interruption; use separate futures to release each
 *			value as it is ready.
 */
package info.rlwhitcomb.math;

import info.rlwhitcomb.logging.Logging;
import info.rlwhitcomb.util.ClassUtil;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.QueuedThread;
import static info.rlwhitcomb.util.Constants.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.EnumMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;


/**
 * Calculates and maintains the value of <code>pi</code> and <code>e</code>
 * (plus related values) to what could be a significant number of decimal places (and therefore
 * requiring some seconds of calculation time, potentially) by doing the
 * expensive calculations in a background thread.
 */
public class PiWorker
{
	private static final Logging logger = new Logging(PiWorker.class);

	private MathContext mc = null;
	private MathContext mc2 = null;
	private int precision;
	private boolean rational = false;

	/**
	 * The ratio of the circumference to the diameter of a circle (or pi);
	 * cached here for the derivative values.
	 */
	private BigDecimal pi;

	/**
	 * Set while {@link #calculate} is actually working.
	 */
	private volatile boolean calculating = false;

	/**
	 * A monitor for the {@link #calculating} flag.
	 */
	private final Object calcMonitor = new Object();


	/**
	 * Enumeration of each of the values we calculate so as to make
	 *  each available as they finish calculation.
	 */
	private enum CALCULATED_VALUES
	{
		/**
		 * Euler's constant (e) or the base of the natural logarithms.
		 */
		E,
		/**
		 * "PI" value, or the ratio between circumference and diameter of a circle.
		 */
		PI,
		/**
		 * {@link #pi} divided by two.
		 */
		PI2,
		/**
		 * {@link #pi} divided by 180, or the conversion between degrees and radians.
		 */
		PI180,
		/**
		 * {@link #pi} divided by 200, or the conversion between grads and radians.
		 */
		PI200,
		/**
		 * Natural logarithm of 10, used to calculate "log".
		 */
		LN10
	}


	/** The captive thread used to do the background calculations. */
	private final QueuedThread queuedThread = new QueuedThread();

	/** Set of future values corresponding to each completed calculation. */
	private EnumMap<CALCULATED_VALUES, CompletableFuture<BigDecimal>> futures =
		new EnumMap<>(CALCULATED_VALUES.class);


	/**
	 * The only constructor of this object.
	 * <p> Use the {@link #apply} method before attempting to acquire any values.
	 */
	public PiWorker() {
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
		// Before we go on, we need to make sure an earlier calculation
		//  that is still in progress is stopped since the results won't
		//  be used anyway.
		if (calculating) {
		    logger.debug("calculating, so interrupting queued thread");
		    queuedThread.interrupt();

		    // now need to wait for "calculating" to be reset
		    logger.debug("waiting for 'calculating' to be reset");
		    synchronized (calcMonitor) {
			while (calculating) {
			    try {
				calcMonitor.wait();
				logger.debug("done waiting for 'calculating' flag");
			    }
			    catch (InterruptedException ie) {
				logger.debug("interrupted while waiting on 'calculating' monitor");
			    }
			}
			logger.debug("'calculating' is now %1$s", calculating);
		    }
		}

		// Initially reset all the futures to "incomplete" until we finish
		synchronized (futures) {
		    logger.debug("resetting futures to 'incomplete'");
		    for (CALCULATED_VALUES value : CALCULATED_VALUES.values()) {
			futures.put(value, new CompletableFuture<>());
		    }
		}

		mc        = newMC;
		mc2       = MathUtil.newPrecision(mc, 2);
		precision = mc.getPrecision();

		// Starting a new calculation; the result is not available until it's done
		logger.debug("calculating set to true");
		synchronized (calcMonitor) {
		    calculating = true;
		}
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
	    // Step through all the calculations with interrupt checks in between each,
	    //  but we will rely on the longer methods to check "interrupted" status
	    //  periodically themselves.
	    try {
		BigDecimal value = null;

		for (CALCULATED_VALUES v : CALCULATED_VALUES.values()) {
		    logger.debug("starting calculation for value %1$s", v);

		    switch (v) {
			case E:
			    value = MathUtil.e(precision + 2);
			    break;

			case PI:
			    value = pi = MathUtil.pi(precision + 2);
			    break;

			case PI2:
			    value = pi.divide(D_TWO, mc2);
			    break;

			case PI180:
			    value = pi.divide(D_180, mc2);
			    break;

			case PI200:
			    value = pi.divide(D_200, mc2);
			    break;

			case LN10:
			    value = MathUtil.ln(D_TEN, mc2);
			    break;
		    }

		    // If we interrupted this calculation, make sure we don't "complete" the value
		    //  because we will reset them anyway.
		    // Note: this "interrupted" call will reset the flag, effectively ending the
		    //  interruption.
		    if (Thread.interrupted()) {
			logger.debug("thread is interrupted at value %1$s", v);
			break;	// out of CALCULATED_VALUES loop
		    }

		    // If we were NOT interrupted, then make the value we just finished available
		    // to other callers now.
		    synchronized (futures) {
			logger.debug("complete future for value %1$s", v);
			futures.get(v).complete(value);
		    }
		}
	    }
	    finally {
		logger.debug("calculating set to false");
		synchronized (calcMonitor) {
		    calculating = false;
		    calcMonitor.notifyAll();
		}
	    }
	}

	/**
	 * Generic function to wait on the given future before returning the value.
	 *
	 * @param v     Identifier for which future to wait on.
	 * @return	The value returned from the given future.
	 */
	private BigDecimal getWhenReady(final CALCULATED_VALUES v) {
	    ClassUtil.throwNullException(mc, "calc#workerNotStarted");

	    logger.debug("getWhenReady(%1$s)", v);
	    try {
		CompletableFuture<BigDecimal> future = null;
		synchronized (futures) {
		    logger.debug("about to get future %1$s", v);
		    future = futures.get(v);
		}

		logger.debug("now about to get future value %1$s", v);
		BigDecimal value = future.get();
		logger.debug("got future value for %1$s", v);
		return value;
	    }
	    catch (InterruptedException | ExecutionException ex) {
		throw new Intl.IllegalStateException("util#piworker.valueNotAvail", v);
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
	    return getWhenReady(CALCULATED_VALUES.E).round(mc);
	}

	/**
	 * Get the calculated value of <code>pi</code> to the current number of digits.
	 * <p> Waits until the background thread is done with the calculation
	 * if a new precision was just recently specified.
	 *
	 * @return The value of <code>pi</code> to the current precision.
	 */
	public BigDecimal getPi() {
	    return getWhenReady(CALCULATED_VALUES.PI).round(mc);
	}

	/**
	 * Get the calculated value of <code>pi / 2</code> to the current number of digits.
	 * <p> Waits until the background thread is done with the calculation
	 * if a new precision was just recently specified.
	 *
	 * @return The value of <code>pi / 2</code> to the current precision plus 2.
	 */
	public BigDecimal getPiOver2() {
	    return getWhenReady(CALCULATED_VALUES.PI2);
	}

	/**
	 * Get the calculated value of <code>pi / 180</code> to the current number of digits.
	 * <p> Waits until the background thread is done with the calculation
	 * if a new precision was just recently specified.
	 *
	 * @return The value of <code>pi / 180</code> to the current precision plus 2.
	 */
	public BigDecimal getPiOver180() {
	    return getWhenReady(CALCULATED_VALUES.PI180);
	}

	/**
	 * Get the calculated value of <code>pi / 200</code> to the current number of digits.
	 * <p> Waits until the background thread is done with the calculation
	 * if a new precision was just recently specified.
	 *
	 * @return The value of <code>pi / 200</code> to the current precision plus 2.
	 */
	public BigDecimal getPiOver200() {
	    return getWhenReady(CALCULATED_VALUES.PI200);
	}

	/**
	 * Get the calculated value of <code>ln(10)</code> to the current number of digits.
	 * <p> Waits until the background thread is done with the calculation
	 * if a new precision was just recently specified.
	 *
	 * @return The value of <code>ln 10</code> to the current precision plus 2.
	 */
	public BigDecimal getLn10() {
	    return getWhenReady(CALCULATED_VALUES.LN10);
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

