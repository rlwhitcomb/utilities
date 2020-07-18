/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2018,2020 Roger L. Whitcomb
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
 *	Static methods to deal with parsing command-line options.
 *
 *  History:
 *	06-Jan-2015 (rlwhitcomb)
 *	    Created.
 *	09-Feb-2015 (rlwhitcomb)
 *	    Expose a method to tell if the string looks like an option.
 *	07-Jan-2016 (rlwhitcomb)
 *	    Fix Javadoc warnings found by Java 8.
 *	20-Jun-2016 (rlwhitcomb)
 *	    Move a new method for parsing an input set of numbers into
 *	    here from its original location.  Also make a simpler version
 *	    that does not have the min/max arguments.
 *	21-Jun-2016 (rlwhitcomb)
 *	    Ooops!  A LinkedHashSet just guarantees a consistent ordering,
 *	    but not a "sorted" ordering (as we claimed in the javadoc), so
 *	    change to a TreeSet so we do get an ordered set!
 *	05-Jul-2016 (rlwhitcomb)
 *	    Add a public interface we can use for enums or other classes
 *	    implementing Options choices.
 *	19-Apr-2017 (rlwhitcomb)
 *	    Add methods to deal with arbitrarily many text choices for the
 *	    same option (something like "-username", "-user" or "-u").
 *	29-Nov-2018 (rlwhitcomb)
 *	    Make use of Optional interface in Java 8 to make a cleaner
 *	    interface here (not using "null").
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	16-Jul-2020 (rlwhitcomb)
 *	    Fix bug with "isOption" and the "--" string.
 */
package info.rlwhitcomb.util;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;


public class Options
{
	private static final boolean onWindows = Environment.isWindows();

	/**
	 * An interface that can be implemented by enums or other classes such that we can
	 * call standard methods to match a command line argument with one of them.
	 */
	public interface Choice
	{
		boolean matches(String arg);
		boolean matches(String arg, boolean ignoreCase);
	}


	/**
	 * This is the workhorse method to match the first one of a number of possible forms for a given
	 * option.
	 *
	 * @param arg	The incoming command line argument.
	 * @param ignoreCase	Whether or not the match should be case-sensitive.
	 * @param forms		The various acceptable forms for the option name that are allowed.
	 * @return		Whether or not the given argument matched any of the acceptable forms.
	 */
	private static boolean matches(String arg, boolean ignoreCase, String... forms) {
	    if (ignoreCase) {
		for (String form : forms) {
		    if (arg.equalsIgnoreCase(form))
			return true;
		}
	    }
	    else {
		for (String form : forms) {
		    if (arg.equals(form))
			return true;
		}
	    }
	    return false;
	}

	/**
	 * Does the given string look like an argument (that is, does it start with
	 * "--", "-" or "/" [on Windows])?
	 * @param arg	The candidate argument value.
	 * @return	The remaining string as an {@link Optional} if so, an
	 *		empty <tt>Optional</tt> if not.
	 */
	public static Optional<String> checkOption(String arg) {
	    return Optional.ofNullable(isOption(arg));
	}

	/**
	 * Does the given string look like an argument (that is, does it start with
	 * "--", "-" or "/" [on Windows])?
	 * @param arg	The candidate argument value.
	 * @return	The remaining string if so, or <tt>null</tt> if not.
	 */
	public static String isOption(String arg) {
	    String result = null;
	    if (arg != null && !arg.isEmpty()) {
		int length = arg.length();
		if (length > 2 && arg.startsWith("--")) {
		    result = arg.substring(2);
		}
		else if (length > 1 && arg.startsWith("-") && !arg.equals("--")) {
		    result = arg.substring(1);
		}
		else if (onWindows && length > 1 && arg.startsWith("/")) {
		    result = arg.substring(1);
		}
	    }
	    return result;
	}


	public static boolean matchesOption(String arg, boolean ignoreCase, String... forms) {
	    String option = isOption(arg);
	    if (option != null) {
		return matches(option, ignoreCase, forms);
	    }
	    return false;
	}

	public static boolean matchesOption(String arg, String longForm, String shortForm, boolean ignoreCase) {
	    String option = isOption(arg);
	    if (option != null) {
		return matches(option, ignoreCase, longForm, shortForm);
	    }
	    return false;
	}

	public static boolean matchesOption(String arg, String option) {
	    return matchesOption(arg, option, null, true);
	}

	public static boolean matchesOption(String arg, String option, boolean ignoreCase) {
	    return matchesOption(arg, option, null, ignoreCase);
	}

	public static boolean matchesOption(String arg, String longForm, String shortForm) {
	    return matchesOption(arg, longForm, shortForm, true);
	}

	public static boolean matchesOption(String arg, Choice choice) {
	    return choice.matches(arg);
	}

	public static boolean matchesOption(String arg, Choice choice, boolean ignoreCase) {
	    return choice.matches(arg, ignoreCase);
	}

	/**
	 * Parse an argument string into a set of non-negative numbers, possibly constrained by min and max values.
	 * <p>The input string can have any number of values separated by commas or semicolons, and each value
	 * can be a range (as in "x-y") where all the values between "x" and "y" (inclusive) will be included in
	 * the returned set.
	 * <p>The returned set will be in numeric order from lowest to highest.
	 *
	 * @param arg	The argument to parse.
	 * @param min	The minimum value (inclusive) to allow (or -1 to ignore).
	 * @param max	The maximum value (inclusive) to allow (or -1 to ignore).
	 * @return	The set of numbers parsed from the input.
	 * @throws IllegalArgumentException if the min/max bounds are violated.
	 * @throws NumberFormatException if one of the numbers isn't a number.
	 */
	public static Set<Integer> parseNumberSet(String arg, int min, int max) {
	    int minValue = min == -1 ? Integer.MIN_VALUE : min;
	    int maxValue = max == -1 ? Integer.MAX_VALUE : max;
	    if (minValue > maxValue) {
		throw new IllegalArgumentException();
	    }

	    Set<Integer> values = new TreeSet<>();

	    try {
		String[] arguments = arg.split("[,;]");
		for (String string : arguments) {
		    // The comma-separated values could be "a-b" meaning a through b
		    int splitPoint = string.indexOf("-");
		    if (splitPoint >= 0 && splitPoint < string.length()) {
			String start = string.substring(0, splitPoint);
			String end   = string.substring(splitPoint + 1);
			int testStart = Integer.valueOf(start);
			int testEnd   = Integer.valueOf(end);
			for (int value = Math.min(testStart, testEnd);
				 value <= Math.max(testStart, testEnd);
				 value++) {
			    values.add(Integer.valueOf(value));
			}
		    }
		    else {
			Integer value = Integer.valueOf(string);
			int testValue = value.intValue();
			if (testValue < minValue || testValue > maxValue)
			    throw new IllegalArgumentException();
			else
			    values.add(value);
		    }
		}
	    }
	    catch (NumberFormatException nfe) {
		throw nfe;
	    }

	    return values;
	}

	/**
	 * Parse an argument string into a set of non-negative numbers.
	 * <p>The input string can have any number of values separated by commas or semicolons, and each value
	 * can be a range (as in "x-y") where all the values between "x" and "y" (inclusive) will be included in
	 * the returned set.
	 * <p>The returned set will be in numeric order from lowest to highest.
	 *
	 * @param arg	The argument to parse.
	 * @return	The set of numbers parsed from the input.
	 * @throws NumberFormatException if one of the numbers isn't a number.
	 */
	public static Set<Integer> parseNumberSet(String arg) {
	    return parseNumberSet(arg, -1, -1);
	}

}
