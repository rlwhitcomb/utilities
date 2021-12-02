/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2021 Roger L. Whitcomb.
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
 */
package info.rlwhitcomb.calc;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.concurrent.Semaphore;

import info.rlwhitcomb.util.MathUtil;
import info.rlwhitcomb.util.QueuedThread;

/**
 * Calculates and maintains the value of <code>pi</code> and <code>e</code>
 * to what could be a significant number of decimal places (and therefore
 * requiring some seconds of calculation time, potentially) by doing the
 * expensive calculations in a background thread.
 */
public class CalcPiWorker
{
	private MathContext mc;
	private int precision;

	private BigDecimal e;
	private BigDecimal pi;
	private BigDecimal piOver180;

	/** Value used to help convert radians back and forth to degrees. */
	private static final BigDecimal B180 = BigDecimal.valueOf(180L);

	/** The captive thread used to do the background calculations. */
	private final QueuedThread queuedThread = new QueuedThread();

	/** Initialize the semaphore with one permit available, which will
	 *  be acquired right away from the constructor when it calls
	 *  {@code calculate()}.
	 */
	private final Semaphore readySem = new Semaphore(1);

	/**
	 * Private since we need a precision to do anything, so not allowed.
	 */
	private CalcPiWorker() {
	}

	/**
	 * Construct and start a new calculation with the specified precision.
	 *
	 * @param newMC	The new context (precision) to use for the calculation.
	 */
	public CalcPiWorker(final MathContext newMC) {
	    calculate(newMC);
	}

	/**
	 * Call to recalculate the values to a new precision.
	 * <p> Immediately makes the result unavailable, sets the new precision
	 * and then starts a new calculation in the background thread.  Once that
	 * is complete, the sempahore will be released and the result will be
	 * accessible by others.
	 *
	 * @param newMC	The new precision for the calculated values.
	 */
	public void calculate(final MathContext newMC) {
	    // Starting a new calculation; the result is not available until it's done
	    readySem.acquireUninterruptibly();

	    mc        = newMC;
	    precision = mc.getPrecision();

	    queuedThread.submitWork(() -> calculate());
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
	    piOver180 = pi.divide(B180, mc);

	    // Release a permit to say the calculation results are now available
	    readySem.release();
	}

	/**
	 * Get the calculated value of <code>e</code> to the current number of digits.
	 * <p> Waits until the background thread is done with the calculation
	 * if a new precision was just recently specified.
	 *
	 * @return The value of <code>e</code> to the current precision.
	 */
	public BigDecimal getE() {
	    readySem.acquireUninterruptibly();
	    try {
		return e.round(mc);
	    }
	    finally {
		readySem.release();
	    }
	}

	/**
	 * Get the calculated value of <code>pi</code> to the current number of digits.
	 * <p> Waits until the background thread is done with the calculation
	 * if a new precision was just recently specified.
	 *
	 * @return The value of <code>pi</code> to the current precision.
	 */
	public BigDecimal getPi() {
	    readySem.acquireUninterruptibly();
	    try {
		return pi.round(mc);
	    }
	    finally {
		readySem.release();
	    }
	}

	/**
	 * Get the calculated value of <code>pi / 180</code> to the current number of digits.
	 * <p> Waits until the background thread is done with the calculation
	 * if a new precision was just recently specified.
	 *
	 * @return The value of <code>pi / 180</code> to the current precision.
	 */
	public BigDecimal getPiOver180() {
	    readySem.acquireUninterruptibly();
	    try {
		return piOver180;
	    }
	    finally {
		readySem.release();
	    }
	}

}

