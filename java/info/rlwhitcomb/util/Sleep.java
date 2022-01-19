/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2022 Roger L. Whitcomb.
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
 *	19-Jan-2022 (rlwhitcomb)
 *	    #126: Add "-quiet" and "-verbose" flags with lots of aliases 😉.
 *	    Using Optional and OptionalDouble.
 *	    Process SLEEP_OPTIONS env variable.
 */
package info.rlwhitcomb.util;

import java.util.Optional;
import java.util.OptionalDouble;
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

	/** The (optional) amount of time to sleep. */
	private static OptionalDouble sleepTimeSecs = OptionalDouble.empty();

	/** Whether to display messages during operation. */
	private static boolean prolijo = true;


	/**
	 * Simply parse the given string into a decimal number of seconds.
	 * <p> Default to an empty Optional if empty.
	 *
	 * @param arg	The supposed number of seconds to parse.
	 * @return	The parsed double value, or an empty Optional (which will use the default)
	 *		if unable to parse the input.
	 */
	public static OptionalDouble parseSeconds(final String arg) {
	    String value = (arg == null) ? "" : arg.trim();

	    if (value.isEmpty()) {
		if (prolijo)
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

			return OptionalDouble.of(dValue);
		    }
		}
		catch (NumberFormatException nfe) {
		    ;
		}

		if (prolijo)
		    System.err.format("Could not decipher a number of seconds to sleep from the argument \"%1$s\" given!%n  Defaulting to %2$11.9f second(s).%n", value, DEFAULT_SLEEP_SECS);
	    }

	    // The default value indicator (empty)
	    return OptionalDouble.empty();
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

	    if (prolijo)
		System.out.format("Sleeping for %1$11.9f seconds...", seconds);

	    long startTime = System.nanoTime();
	    try {
		if (nanoSeconds != 0)
		    Thread.sleep(milliSeconds, nanoSeconds);
		else
		    Thread.sleep(milliSeconds);

		if (prolijo)
		    System.out.println();
	    }
	    catch (InterruptedException ie) {
		if (prolijo) {
		    long interruptedTime = System.nanoTime();
		    double secs = ((double)(interruptedTime - startTime)) / 1e9d;
		    System.out.format("%nInterrupted after only %1$11.9f seconds.%n", secs);
		}
	    }
	}

	private static void processOptions(String[] args) {
	    for (String arg : args) {
		Optional<String> option = Options.checkOption(arg);
		if (option.isPresent()) {
		    switch (option.get().toLowerCase()) {
			case "quiet":
			case "quieto":
			case "q":
			case "sucinto":
			case "s":
			case "conciso":
			case "c":
			    prolijo = false;
			    break;
			case "verbose":
			case "verboso":
			case "v":
			case "prolijo":
			case "p":
			case "diserto":
			case "d":
			    prolijo = true;
			    break;
			default:
			    System.err.println("Unknown option \"" + arg + "\" specified. Ignored!");
			    break;
		    }
		}
		else {
		    if (sleepTimeSecs.isPresent()) {
			System.err.println("Only one sleep duration value allowed; ignoring any more!");
		    }
		    else {
			sleepTimeSecs = parseSeconds(arg);
		    }
		}
	    }
	}

	/**
	 * Process command line and run the process.
	 *
	 * @param args	The parsed command line arguments (we only need one).
	 */
	public static void main(String[] args) {
	    long startTime = Environment.highResTimer();

	    String envOptions = System.getenv("SLEEP_OPTIONS");
	    if (envOptions != null) {
		String[] options = envOptions.split("[,;:]");
		processOptions(options);
	    }

	    processOptions(args);

	    // Finally do the hard work of ... sleeping ... for that long
	    sleep(sleepTimeSecs.orElseGet(() -> parseSeconds("").orElse(DEFAULT_SLEEP_SECS)));

	    if (prolijo) {
		long elapsedTime = Environment.highResTimer() - startTime;
		System.out.format("Elapsed time %1$11.9f seconds.%n", Environment.timerValueToSeconds(elapsedTime));
	    }
	}
}
