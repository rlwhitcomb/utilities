/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2018,2020-2021 Roger L. Whitcomb
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
 *	22-Jul-2020 (rlwhitcomb)
 *	    Add processing so that we can get greater flexibility in
 *	    interpreting input options and allowing "_", and "-" word separators.
 *	    This system means that a desired option coded as "MixedCase" will also
 *	    accept "Mixed-Case", "Mixed_Case", and the lower case forms (if not
 *	    ignoring case altogether).
 *	06-Nov-2020 (rlwhitcomb)
 *	    More work using stream processing to make code more "stream"lined.
 *	    Make all parameters final. Return Set<> instead of List<> for the
 *	    option choices. Rework the matching methods. Add ChoiceEnum and
 *	    ToggleEnum interfaces.
 *	06-Nov-2020 (rlwhitcomb)
 *	    Count the number of tests and failures in the main program and report.
 *	21-Dec-2020 (rlwhitcomb)
 *	    Update obsolete Javadoc constructs.
 *	02-Sep-2021 (rlwhitcomb)
 *	    Move the main (testing) method to OptionsTest class.
 *	11-Nov-2021 (rlwhitcomb)
 *	    Make "matches" and "matchesIgnoreCase" public for use elsewhere.
 */
package info.rlwhitcomb.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;


/**
 * Try to deal in a general way with options parsed from a program's command-line arguments.
 */
public class Options
{
	/** Determines if we are running on a Windows platform, which then allows options
	 * to start with "/" (in addition to "--" and "-" allowed on all platforms).
	 */
	private static final boolean onWindows = Environment.isWindows();

	/**
	 * An interface that can be implemented by enums or other classes such that we can
	 * call standard methods to match a command line argument with one of them.
	 */
	public interface Choice
	{
		boolean matches(String arg);
		boolean matchesIgnoreCase(String arg);
	}


	/**
	 * An interface that can be implemented by an enum to present a list of valid choices
	 * for that enum value.
	 */
	public interface ChoiceEnum
	{
		String[] choices();
	}

	/**
	 * An interface that can be implemented by an enum that keeps a value of its own.
	 */
	public interface ToggleEnum extends ChoiceEnum
	{
		void set(boolean value);
		boolean isSet();
	}


	/**
	 * A template method that can be used to match one of a set of enum options based
	 * on possible choices stored in each enum value (which implement the {@link ToggleEnum}
	 * interface). The stored value is also set for {@link ToggleEnum} types.
	 * The match is done in a case-sensitive way.
	 *
	 * @param <E>		The enum type.
	 * @param values	The enum values.
	 * @param input		The input value to test against.
	 * @return		One of the enum values if there was a match, or none if there wasn't.
	 */
	public static <E extends Enum<E> & ChoiceEnum> Optional<E> match(final E[] values, final String input) {
	    Optional<E> result = Arrays.stream(values)
				       .filter(opt -> matches(input, opt.choices()))
				       .findFirst();
	    result.ifPresent(opt -> {
		if (opt instanceof ToggleEnum)
		    ((ToggleEnum)opt).set(true);
	    });
	    return result;
	}

	/**
	 * A template method that can be used to match one of a set of enum options based
	 * on possible choices stored in each enum value (which implement the {@link ToggleEnum}
	 * interface). The stored value is also set for {@link ToggleEnum} types.
	 * The match is done in a case-insensitive way.
	 *
	 * @param <E>		The enum type.
	 * @param values	The enum values.
	 * @param input		The input value to test against.
	 * @return		One of the enum values if there was a match, or none if there wasn't.
	 */
	public static <E extends Enum<E> & ChoiceEnum> Optional<E> matchIgnoreCase(final E[] values, final String input) {
	    Optional<E> result = Arrays.stream(values)
				       .filter(opt -> matchesIgnoreCase(input, opt.choices()))
				       .findFirst();
	    result.ifPresent(opt -> {
		if (opt instanceof ToggleEnum)
		    ((ToggleEnum)opt).set(true);
	    });
	    return result;
	}


	/**
	 * Construct one "new form" of an option string, given the input template, the
	 * new "break" character to use, and the list of positions where the breaks should
	 * occur.
	 * <p> There could be either "-" or "_" at the break positions, in which case that
	 * character is replaced by the given break character. Otherwise the break char
	 * is just inserted at that position.
	 *
	 * @param template	The template to use.
	 * @param toLowerCase	Whether the output should be lowercased or not.
	 * @param breakPositions	An array of "break" positions where the break
	 *				character should be replaced/inserted.
	 * @param numberOfBreaks	The number of array positions to use (assuming
	 *				the array is bigger than the valid values).
	 * @param brk		The "break" character to replace/insert at the break
	 *			positions. If ' ' then nothing will be added.
	 * @return	A new form of the input template.
	 */
	private static String newForm(final String template, final boolean toLowerCase,
		final int[] breakPositions, final int numberOfBreaks, final char brk)
	{
	    StringBuilder buf = new StringBuilder(template.length() + numberOfBreaks);

	    int breakPos = breakPositions[0];
	    for (int i = 1; i < numberOfBreaks; i++) {
		int endPos = breakPositions[i];
		char breakChar = template.charAt(endPos);

		// Append the next word, and the given separator to the output
		buf.append(template.substring(breakPos, endPos));
		if (brk != ' ')
		    buf.append(brk);

		// Advance the "breakPos" to the next starting position
		if (breakChar == '_' || breakChar == '-') {
		    breakPos = endPos + 1;
		} else {
		    breakPos = endPos;
		}
	    }
	    if (breakPos < template.length())
		buf.append(template.substring(breakPos));
	    return toLowerCase ? buf.toString().toLowerCase() : buf.toString();
	}

	/**
	 * Parse a given option string and return a set of possible values if the permitted
	 * option has MixedCase, so that "mixed_case" or "mixed-case" variants also work.
	 * <p> Note: the "ignoreCase" option if false will give more values in our
	 * output list ("MixedCase" -&gt; "MixedCase", "mixedcase", "Mixed-Case", "mixed-case", etc.).
	 *
	 * @param template	The suggested option template. If {@code null} or empty the output list
	 *			will also be empty.
	 * @param ignoreCase	Whether or not to ignore case on the user's input.
	 * @return		A set of potential spellings depending on the input.
	 */
	public static Set<String> getMixedCaseOptions(final String template, final boolean ignoreCase) {
	    Set<String> values = new HashSet<>();
	    boolean inputHasSeparators = false;

	    // Empty input will produce an empty output list
	    if (template != null && !template.isEmpty()) {
		int breakPositions[] = new int[template.length()];
		int breakNo = 0;

		// This whole process is started by the option template starting with an UPPER case letter.
		if (Character.isUpperCase(template.charAt(0))) {
		    for (int pos = 0; pos < template.length(); pos++) {
			char ch = template.charAt(pos);
			if (Character.isUpperCase(ch))
			    breakPositions[breakNo++] = pos;

			// The '-' or '_' options give us flexibility if the break char
			// is a number or other non-alphabet (like "Utf-16")
			else if (ch == '_' || ch == '-') {
			    breakPositions[breakNo++] = pos;
			    inputHasSeparators = true;
			}
		    }

		    // Add the lower-case versions of the option, with both '-' and '_' separators
		    values.add(newForm(template, true, breakPositions, breakNo, '-'));
		    values.add(newForm(template, true, breakPositions, breakNo, '_'));

		    // If we're NOT ignoring case, then we need to add versions of the template
		    // which preserve the input case, and also a version without the separators.
		    if (!ignoreCase) {
			if (!inputHasSeparators)
			    values.add(template);
			else
			    values.add(newForm(template, false, breakPositions, breakNo, ' '));

			values.add(newForm(template, false, breakPositions, breakNo, '-'));
			values.add(newForm(template, false, breakPositions, breakNo, '_'));
		    }
		}

		// If the input had no separators, then we need to also add the lowercase
		// version of the template to the set (since the original case one was already
		// added earlier if not ignoring case).
		if (!inputHasSeparators)
		    values.add(template.toLowerCase());
		else
		    values.add(newForm(template, true, breakPositions, breakNo, ' '));

		// Finally, if the list is empty, just add whatever was given to us (could look
		// like "addWord" or "add-word" at this point and we would be not ignoring case)
		if (values.isEmpty())
		    values.add(template);
	    }

	    return values;
	}

	/**
	 * This is the workhorse method to match the first one of a number of possible forms for a given
	 * option in a case-sensitive way.
	 *
	 * @param arg	The incoming command line argument.
	 * @param forms	The various acceptable forms for the option name that are allowed.
	 * @return	Whether or not the given argument matched any of the acceptable forms.
	 */
	public static boolean matches(final String arg, final String... forms) {
	        return Arrays.stream(forms)
			     .flatMap(s -> getMixedCaseOptions(s, false).stream())
			     .anyMatch(opt -> opt.equals(arg));
	}

	/**
	 * This is the workhorse method to match the first one of a number of possible forms for a given
	 * option in a case-insensitive way.
	 *
	 * @param arg	The incoming command line argument.
	 * @param forms	The various acceptable forms for the option name that are allowed.
	 * @return	Whether or not the given argument matched any of the acceptable forms.
	 */
	public static boolean matchesIgnoreCase(final String arg, final String... forms) {
	        return Arrays.stream(forms)
			     .flatMap(s -> getMixedCaseOptions(s, true).stream())
			     .anyMatch(opt -> opt.equalsIgnoreCase(arg));
	}


	/**
	 * This is the workhorse method to match the first one of a number of possible forms for a given
	 * option.
	 *
	 * @param arg		The incoming command line argument.
	 * @param ignoreCase	Whether or not the match should be case-sensitive.
	 * @param forms		The various acceptable forms for the option name that are allowed.
	 * @return		Whether or not the given argument matched any of the acceptable forms.
	 */
	private static boolean matches(String arg, boolean ignoreCase, String... forms) {
	    return ignoreCase
		? matchesIgnoreCase(arg, forms)
		: matches(arg, forms);
	}

	/**
	 * Does the given string look like an argument (that is, does it start with
	 * "--", "-" or "/" [on Windows])?
	 * @param arg	The candidate argument value.
	 * @return	The remaining string as an {@link Optional} if so, an
	 *		empty {@code Optional} if not.
	 */
	public static Optional<String> checkOption(String arg) {
	    return Optional.ofNullable(isOption(arg));
	}

	/**
	 * Does the given string look like an argument (that is, does it start with
	 * <code>"--"</code>, <code>"-"</code> or <code>"/"</code> [on Windows])?
	 * @param arg	The candidate argument value.
	 * @return	The remaining string if so, or {@code null} if not.
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
	    return ignoreCase
		? choice.matchesIgnoreCase(arg)
		: choice.matches(arg);
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

	/**
	 * Take a set of options (such as returned by {@link #getMixedCaseOptions})
	 * and return a human-readable version (such as for a "help" display).
	 *
	 * @param optionSet	The set of possible options.
	 * @return		The nicely formatted version of them.
	 */
	public static String getDisplayableOptions(Set<String> optionSet) {
	    StringBuilder buf = new StringBuilder(optionSet.size() * 10);
	    for (String option : optionSet) {
		if (buf.length() > 0)
		    buf.append(", ");
		buf.append('-').append(option);
	    }
	    return buf.toString();
	}

}
