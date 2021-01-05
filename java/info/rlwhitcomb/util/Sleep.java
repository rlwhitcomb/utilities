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
 *	Simply wait/sleep for a given number of seconds (default one).
 *
 *  History:
 *      16-Dec-2020 (rlwhitcomb)
 *          First version.
 *	04-Jan-2021 (rlwhitcomb)
 *	    Move to named package. Allow minutes, hours, etc. intervals.
 *	05-Jan-2021 (rlwhitcomb)
 *	    Just for info, report the total elapsed time at the end.
 */
package info.rlwhitcomb.util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


/**
 * Sleep for the given number of seconds (can be a fraction).
 */
public class Sleep
{
	/** The default sleep interval if none given, or we can't decipher the given value. */
	private static final double DEFAULT_SLEEP_SECS = 1.0d;

	/** The pattern used to recognized valid sleep durations. */
	private static final Pattern SLEEP_PATTERN = Pattern.compile("([0-9]+(\\.[0-9]*)?)([sSmMhHdDwW]?)");

	/**
	 * Simply parse the given string into a decimal number of seconds.
	 * <p> Default to the default ({@link #DEFAULT_SLEEP_SECS}) if we can't do that.
	 *
	 * @param arg	The supposed number of seconds to parse.
	 * @return	The parsed double value, or the default if we can't successfully
	 *		parse the input.
	*/
	public static double parseSeconds(final String arg) {
	    String value = (arg == null) ? "" : arg.trim();

	    if (value.isEmpty()) {
		System.err.format("Missing or empty value, defaulting to %1$11.9f second(s).%n", DEFAULT_SLEEP_SECS);
	    }
	    else {
		try {
		    Matcher m = SLEEP_PATTERN.matcher(value);
		    if (m.matches()) {
			// base value in seconds
			double dValue = Double.valueOf(m.group(1));

			// multiplier suffixes
			String suffix = m.group(3);
			if (!suffix.isEmpty()) {
			    switch (suffix.charAt(0)) {
				case 'w':
				case 'W':
				    dValue *= 7.0d;
				    // fall through
				case 'd':
				case 'D':
				    dValue *= 24.0d;
				    // fall through
				case 'h':
				case 'H':
				    dValue *= 60.0d;
				    // fall through
				case 'm':
				case 'M':
				    dValue *= 60.0d;
				    // fall through
				case 's':
				case 'S':
				    // Default unit is seconds; nothing more to do
				    break;
			    }
			}

			return dValue;
		    }
		}
		catch (NumberFormatException nfe) {
		    ;
		}

		System.err.format("Could not decipher a number of seconds to sleep from the argument \"%1$s\" given!%n  Defaulting to %2$11.9f second(s).%n", value, DEFAULT_SLEEP_SECS);
	    }

	    // The default value
	    return DEFAULT_SLEEP_SECS;
	}

	/**
	 * Sleep for the given double value of seconds; must convert to millis/nanos
	 * in order to do this.
	 *
	 * @param seconds	The whole/fractional number of seconds to sleep (using either
	 *			{@link Thread#sleep(long)} or {@link Thread#sleep(long,int)}
	 *			to accomplish this).
	 */
	public static void sleep(final double seconds) {
	    // Do the (only) difficult work here of converting int/fraction double seconds to millis and nanos
	    double wholePart = Math.floor(seconds);
	    double fracPart  = seconds - wholePart;
	    double milliPart = Math.floor(fracPart * 1000.0d);
	    double nanoPart  = (fracPart - (milliPart / 1000.0d)) * 1e9d;

	    long wholeSeconds = (long)wholePart;
	    long milliSeconds = wholeSeconds * 1000L + (long)milliPart;
	    int nanoSeconds   = (int)nanoPart;

	    System.out.format("Sleeping for %1$11.9f seconds...", seconds);

	    long startTime = System.nanoTime();
	    try {
		if (nanoSeconds != 0)
		    Thread.sleep(milliSeconds, nanoSeconds);
		else
		    Thread.sleep(milliSeconds);
		System.out.println();
	    }
	    catch (InterruptedException ie) {
		long interruptedTime = System.nanoTime();
		double secs = ((double)(interruptedTime - startTime)) / 1e9d;
		System.out.format("%nInterrupted after only %1$11.9f seconds.%n", secs);
	    }
	}

	/**
	 * Process command line and run the process.
	 *
	 * @param args	The parsed command line arguments (we only need one).
	 */
	public static void main(String[] args) {
	    double sleepTimeSecs;
	    long startTime = Environment.highResTimer();

	    if (args.length == 1) {
		sleepTimeSecs = parseSeconds(args[0]);
	    }
	    else if (args.length == 0) {
		sleepTimeSecs = parseSeconds("");
	    }
	    else {
		System.err.println("Only one argument needed (time value); ignoring the rest!");
		sleepTimeSecs = parseSeconds(args[0]);
	    }

	    // Finally do the hard work of ... sleeping ... for that long
	    sleep(sleepTimeSecs);

	    long elapsedTime = Environment.highResTimer() - startTime;
	    System.out.format("Elapsed time %1$11.9f seconds.%n", Environment.timerValueToSeconds(elapsedTime));
	}
}
