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
 *	Command line utility to expose the Java "regex" functionality.
 *
 *  Change History:
 *	18-Sep-2020 (rlwhitcomb)
 *	    Initial checkin.
 *	29-Mar-2021 (rlwhitcomb)
 *	    Move test code to a new package.
 *	18-Feb-2022 (rlwhitcomb)
 *	    Use Exceptions to get a better error message.
 *	08-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 */
package info.rlwhitcomb.matches;

import info.rlwhitcomb.util.Exceptions;
import info.rlwhitcomb.util.Options;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * Class that basically exposes the Java regex facilities to the command line.
 * TODO:  -not, -xor flags; global and other options; verbosity printouts
 * TODO: can we do inline -not, -or, -and flags to do equivalent of "if (this &amp;&amp; !that || theother)"
 * and make it work like parenthesized would? ((this || that) &amp;&amp; (this || that || theother))
 */
public class Matches
{
	/**
	 * arg[0] = value
	 * arg[1] = regex
	 * arg[2..n] = additional regex
	 * return code 0 = matches (true)
	 * return code 1 = no match (false)
	 *
	 * Process the command line arguments into a "matched" result.
	 *
	 * @param args	The complete list of command line arguments.
	 * @return Whether or not the input argument matched any ("-or") or all ("-and")
	 * of the given regular expressions. Reversed with "-not" flag.
	 *
	 * @see info.rlwhitcomb.test.MatchesTest Made into a separate
	 * function to facilitate testing of the algorithm.
	 */
	public static boolean match(final String[] args) {
	    boolean or        = false;
	    boolean and       = true;
	    boolean not       = false;
	    boolean verbose   = false;
	    boolean hadErrors = false;

	    // Pass through arguments first, saving the non-options and processing the options
	    List<String> arguments = new ArrayList<>();
	    for (String arg : args) {
		if (Options.matchesOption(arg, true, "or", "o")) {
		    or  = true;
		    and = false;
		}
		else if (Options.matchesOption(arg, true, "and", "a")) {
		    and = true;
		    or  = false;
		}
		else if (Options.matchesOption(arg, true, "not", "n")) {
		    not = true;
		}
		else if (Options.matchesOption(arg, true, "verbose", "v")) {
		    verbose = true;
		}
		else if (Options.matchesOption(arg, true, "quiet", "q")) {
		    verbose = false;	// default
		}
		else {
		    arguments.add(arg);
		}
	    }

	    boolean matches = false;

	    // If no args, return true (null matches null)
	    if (arguments.size() == 0) {
		matches = true;
	    }
	    else {
		// If one arg, return false (something does not match null)
		if (arguments.size() == 1) {
		    matches = false;
		}
		else {
		    String inputArg = arguments.get(0);

		    // For two or more args, matching is according to the "-or" and
		    // "-and" flags on the command line (default is "-and")
		    // That is, return (input.matches[1] && input.matches[2] ...)
		    //      or, return (input.matches[1] || input.matches[2] ...)
		    for (int i = 1; i < arguments.size(); i++) {
			try {
			    matches = Pattern.matches(arguments.get(i), inputArg);

			    if (verbose) {
				System.out.println("Input: \"" + inputArg + "\", /"
				    + arguments.get(i) + "/ -> " + matches);
			    }

			    if (or) {
				if (matches) {
				    break;
				}
			    }
			    else if (and) {
				if (!matches) {
				    break;
				}
			    }
			}
			catch (PatternSyntaxException pse) {
			    System.out.println("Invalid pattern: " + Exceptions.toString(pse));
			    hadErrors = true;
			    break;
			}
		    }
		}
	    }

	    /* Deal with the final outcome, which depends on whether there were errors, 
	     * and the "-not" flag.
	     */
	    if (hadErrors) {
		matches = false;
	    }
	    else {
		if (not)
		    matches = !matches;
	    }

	    if (verbose) {
		System.out.println(matches ? "true" : "false");
	    }

	    return matches;
	}

	/**
	 * Main program invoked from the command line. Process exit code is
	 * 0 for a match (success) and 1 for a fail.
	 *
	 * @param args	The complete set of (parsed) command line arguments.
	 */
	public static void main(String[] args) {
	    System.exit(match(args) ? 0 : 1);
	}
}
