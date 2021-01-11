/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013-2014,2016-2021 Roger L. Whitcomb.
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
 *      Utility program to format lists for use with other programs.
 *
 * History:
 *      23-Mar-2016 (rlwhitcomb)
 *          Add another option to recognize blank-separated input files.
 *      10-May-2016 (rlwhitcomb)
 *          Allow reading from System.in with file named "@".
 *      15-Jul-2016 (rlwhitcomb)
 *          Simplify operation further by allowing no file name to mean
 *          "read from console" (still support "@" too).  Reformat the help.
 *          Use the Options class for the command line.
 *      12-Jul-2017 (rlwhitcomb)
 *          Add "-join" option to just join lines with a single space between.
 *          Clean up the help text.  Add the "-n" (to count) option.  Allow
 *          multiple file names and file names mixed with console input.
 *          Able to specify an output file name (needed when mixing console
 *          input with other files). Add prefix ("-e") and postfix ("-f")
 *          text options.
 *      21-Sep-2017 (rlwhitcomb)
 *          Add "-n(ewline)" option in conjunction with "-c" to put commas
 *          and newlines after each entry.
 *      17-Aug-2018 (rlwhitcomb)
 *          Add "-x" option to cut a certain number of characters from the
 *          input lines.  This is to deal with "svn st" output.
 *      30-Jul-2019 (rlwhitcomb)
 *          Reformat; move code from info.rlwhitcomb.util into here to make it
 *          runnable standalone.
 *      13-Feb-2020 (rlwhitcomb)
 *          Allow longer aliases for the command-line options, just for grins.
 *          Update "usage" to document the aliases.
 *      08-Oct-2020 (rlwhitcomb)
 *         Print version information.
 *      16-Oct-2020 (rlwhitcomb)
 *         Incorporate latest code.
 *      21-Dec-2020 (rlwhitcomb)
 *         Update obsolete Javadoc constructs.
 *      05-Jan-2021 (rlwhitcomb)
 *         Add "-single" option to join lines without anything else.
 *      06-Jan-2021 (rlwhitcomb)
 *         Fix "-single" processing. Use regular Environment program info.
 *         Also use the regular classes instead of our duplicates (no
 *         need for a standalone class anymore).
 *	11-Jan-2021 (rlwhitcomb)
 *	   The "-single" option really should just make one line out of
 *	   the input without stripping out the commas, or anything else.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.Options;

/**
 * Utility program for converting lists of values between various formats
 * that we need sometimes, such as a single line separated by commas,
 * separate lines for each value, separate lines with commas, etc.
 */
public class Lists
{
	private static boolean concatenate = false;
	private static boolean join        = false;
	private static boolean blanks      = false;
	private static boolean whitespace  = false;
	private static boolean counting    = false;
	private static boolean cutting     = false;
	private static boolean newlines    = false;
	private static boolean single      = false;

	private static int width   = 0;
	private static int cutSize = 0;

	private static List<String> fileNames = null;
	private static String outputFileName  = null;
	private static PrintStream output     = null;

	private static boolean sawConsoleInput = false;

	private static String prefixText     = null;
	private static String postfixText    = null;
	private static int prefixTextLength  = 0;
	private static int postfixTextLength = 0;

	private static final String STDIN = "@";


	private static void usage() {
	    usage(false);
	}

	private static void usage(boolean helpOnly) {
	    if (!helpOnly)
		System.err.println();
	    System.err.println("Usage: java Lists [-c] [-j] [-b] [-n] [-l] [-nnn] [-w] [-e prefix_text] [-f postfix_text] [-x nn]");
	    System.err.format ("                  [-o output_file] [list_file_name+ | %1$s]%n", STDIN);
	    System.err.println();
	    System.err.println("  Aliases: -c | -concat | -concatenate; -j | -join; -b | -blank | -blanks; -n | -count | -counting");
	    System.err.println("           -l | -line | -lines | -newlines; -w | -white | -whitespace; -e | -pre | -prefix");
	    System.err.println("           -f | -post | -postfix; -x | -cut | -cutting; -s | -single");
	    System.err.println("           -o | -out  | -output");
	    System.err.println();
	    System.err.println("  If you specify the \"-c\" flag the output will have all the lines");
	    System.err.println("    of the file concatenated (using commas) into a single line.");
	    System.err.println();
	    System.err.println("  The \"-j\" flag is similar except the lines will be joined by a");
	    System.err.println("    single space between each into a single line.");
	    System.err.println();
	    System.err.println("  Without either of these options, the input file will be deconstructed");
	    System.err.println("    into one element per line and the commas (if any) will be removed.");
	    System.err.println();
	    System.err.println("  The \"-x nn\" option will cut the nn number of characters (if present)");
	    System.err.println("    from the start of each input line (useful for \"svn st\" output).");
	    System.err.println();
	    System.err.println("  Using \"-b\" without \"-c\" will print a blank line between");
	    System.err.println("    each output value, while with \"-c\" will put a blank after");
	    System.err.println("    each comma.");
	    System.err.println();
	    System.err.println("  Using \"-l\" will print newlines after each entry, even with \"-c\"");
	    System.err.println();
	    System.err.println("  The \"-nnn\" option (where \"nnn\" is a number from 1 to 255) specifies");
	    System.err.println("    a maximum line width for the \"-c\" output mode.");
	    System.err.println();
	    System.err.println("  The \"-n\" option simply counts the number of entries in the input");
	    System.err.println("    using the rules from the other options and outputs the number of entries.");
	    System.err.println();
	    System.err.println("  The \"-w\" option will recognize input files where the input");
	    System.err.println("    values are separated by whitespace instead of \",\".");
	    System.err.println();
	    System.err.println("  The \"-e prefix_text\" and \"-f postfix_text\" options will add this text");
	    System.err.println("    before (after) the single line or each line of the output.");
	    System.err.println();
	    System.err.println("  The \"-s\" option will join the whole file into a single string without");
	    System.err.println("    changing anything else.");
	    System.err.println();
	    System.err.format ("  Using \"%1$s\" or nothing for the list_file_name will read from stdin.%n", STDIN);
	    System.err.format ("  Multiple file names are allowed as well as mixing \"%1$s\" with regular%n", STDIN);
	    System.err.println("    file names, which will simply read and concatenate everything together.");
	    System.err.println();
	    System.err.println("  Unless the \"-o output_file\" option is given, all output will be sent to");
	    System.err.println("    stdout. This output file option is preferable when reading from the console.");
	    System.err.println();
	    System.err.println("  Use \"-help\" (or \"-h\" or \"-?\") to display this help text.");
	    System.err.println("  Use \"-version\" (or \"-vers\", \"-ver\", or \"-v\" to display version information.)");
	    System.err.println();
	}

	public static void main(String[] args) {
	    Environment.loadProgramInfo(Lists.class);

	    fileNames = new ArrayList<>();

	    // First parse the command line arguments
	    boolean sawOutputOption      = false;
	    boolean sawCutOption         = false;
	    boolean sawPrefixTextOption  = false;
	    boolean sawPostfixTextOption = false;

	    for (String arg : args) {
		String option = Options.isOption(arg);
		if (option != null) {
		    if (sawOutputOption) {
			System.err.println("Expecting an output file name for the \"-o\" option!");
			usage();
			return;
		    }
		    if (sawCutOption) {
			System.err.println("Expecting a number for the \"-x\" option!");
			usage();
			return;
		    }
		    if (sawPrefixTextOption) {
			System.err.println("Expecting prefix text for the \"-e\" option!");
			usage();
			return;
		    }
		    if (sawPostfixTextOption) {
			System.err.println("Expecting postfix text for the \"-f\" option!");
			usage();
			return;
		    }
		    if (Options.matchesOption(arg, true, "concatenate", "concat", "c"))
			concatenate = true;
		    else if (Options.matchesOption(arg, "join", "j"))
			concatenate = join = true;
		    else if (Options.matchesOption(arg, true, "blanks", "blank", "b"))
			blanks = true;
		    else if (Options.matchesOption(arg, true, "whitespace", "white", "w"))
			whitespace = true;
		    else if (Options.matchesOption(arg, true, "newlines", "lines", "line", "l")) {
			newlines = true;
			concatenate = false;
		    }
		    else if (Options.matchesOption(arg, "single", "s"))
			single = true;
		    else if (Options.matchesOption(arg, true, "counting", "count", "n"))
			counting = true;
		    else if (Options.matchesOption(arg, true, "cutting", "cut", "x")) {
			cutting = true;
			sawCutOption = true;
		    }
		    else if (Options.matchesOption(arg, true, "output", "out", "o")) {
			if (outputFileName != null) {
			    System.err.println("Can only specify the output file name once.");
			    usage();
			    return;
			}
			sawOutputOption = true;
		    }
		    else if (Options.matchesOption(arg, true, "prefix", "pre", "e")) {
			if (prefixText != null) {
			    System.err.println("Can only specify the prefix text once.");
			    usage();
			    return;
			}
			sawPrefixTextOption = true;
		    }
		    else if (Options.matchesOption(arg, true, "postfix", "post", "f")) {
			if (postfixText != null) {
			    System.err.println("Can only specify the postfix text once.");
			    usage();
			    return;
			}
			sawPostfixTextOption = true;
		    }
		    else if (Options.matchesOption(arg, true, "help", "h", "?")) {
			usage(true);
			return;
		    }
		    else if (Options.matchesOption(arg, true, "version", "vers", "ver", "v")) {
			Environment.printProgramInfo(System.err);
			return;
		    }
		    else {
			try {
			    width = Integer.parseInt(option);
			    if (width < 1 || width > 255) {
				System.err.format("Width value (%1$d) must be between 1 and 255%n", width);
				usage();
				return;
			    }
			}
			catch (NumberFormatException nfe) {
			    System.err.format("Unsupported option: \"%1$s\"%n", arg);
			    usage();
			    return;
			}
		    }
		}
		else {
		    if (sawOutputOption) {
			outputFileName = arg;
			sawOutputOption = false;
		    }
		    else if (sawCutOption) {
			try {
			    cutSize = Integer.parseInt(arg);
			}
			catch (NumberFormatException nfe) {
			    cutSize = -1; // to trigger the error below
			}
			if (cutSize < 1 || cutSize > 255) {
			    System.err.format("The cut size (%1$s) should be between 1 and 255%n", arg);
			    usage();
			    return;
			}
			sawCutOption = false;
		    }
		    else if (sawPrefixTextOption) {
			prefixText = arg + " ";
			prefixTextLength = prefixText.length();
			sawPrefixTextOption = false;
		    }
		    else if (sawPostfixTextOption) {
			postfixText = " " + arg;
			postfixTextLength = postfixText.length();
			sawPostfixTextOption = false;
		    }
		    else {
			if (arg.equals(STDIN)) {
			    if (sawConsoleInput) {
				System.err.format("Cannot specify \"%1$s\" for the input more than once.%n", STDIN);
				usage();
				return;
			    }
			    sawConsoleInput = true;
			}
			fileNames.add(arg);
		    }
		}
	    }

	    if (fileNames.isEmpty()) {
		fileNames.add(STDIN);
	    }

	    // Error checking on the supplied parameters
	    if (!concatenate && width > 0) {
		System.err.format("Specifying an output width (\"-%1$d\") is only effective with the \"-c\" or \"-j\" options.%n", width);
		usage();
		return;
	    }

	    if (counting && (single || concatenate || blanks || width > 0)) {
		System.err.format("The \"-n\" option should not be used together with either the%n\"-s\", \"-c\", \"-j\", or \"-b\" options%nor with an output width.%n");
		usage();
		return;
	    }

	    if (outputFileName != null) {
		try {
		    output = new PrintStream(outputFileName);
		}
		catch (IOException ioe) {
		    System.err.format("Unable to open \"%1$s\" file for writing: %2$s%n", outputFileName, ioe.getMessage());
		    usage();
		    return;
		}
	    }
	    else {
		output = System.out;
	    }

	    int numberOfValues  = 0;
	    int nextFile        = 0;
	    boolean readConsole = false;
	    String fileName     = "";
	    BufferedReader r    = null;

	    try {
		StringBuilder buf = new StringBuilder();
		if (prefixText != null)
		    buf.append(prefixText);

		while (nextFile < fileNames.size()) {
		    fileName      = fileNames.get(nextFile++);
		    readConsole   = fileName.equals(STDIN);
		    Reader reader = readConsole ?
			    new InputStreamReader(System.in) :
			    new FileReader(fileName);
		    r = new BufferedReader(reader);

		    String line = null;

		    while ((line = r.readLine()) != null) {
			if (single) {
			    buf.append(line);
			    continue;
			}
			if (cutting) {
			    if (line.length() > cutSize) {
				line = line.substring(cutSize);
			    }
			}
			String[] parts = whitespace ? line.split("\\s+") : line.split("\\s*,\\s*");
			for (String part : parts) {
			    String value = part.trim();
			    if (!value.isEmpty()) {
				if (counting) {
				    numberOfValues++;
				}
				else {
				    if (concatenate) {
					if (buf.length() > prefixTextLength) {
					    if (!join)
						buf.append(",");
					    if (width > 0 && buf.length() >= width) {
						if (postfixText != null)
						    buf.append(postfixText);
						output.println(buf.toString());
						buf.setLength(0);
						if (prefixText != null)
						    buf.append(prefixText);
					    }
					    else if (blanks || join)
						buf.append(" ");
					}
					if (width > 0 && buf.length() + value.length() >= width) {
					    if (postfixText != null)
						buf.append(postfixText);
					    output.println(buf.toString());
					    buf.setLength(0);
					    if (prefixText != null)
						buf.append(prefixText);
					}
					buf.append(value);
				    }
				    else {
					if (prefixText != null)
					    output.print(prefixText);
					output.print(value);
					if (newlines)
					    output.print(",");
					if (postfixText != null)
					    output.print(postfixText);
					output.println();
					if (blanks)
					    output.println();
				    }
				}
			    }
			}
		    }

		    if (!readConsole) {
			if (r != null) {
			    r.close();
			}
		    }
		}

		if (concatenate || single) {
		    if (postfixText != null)
			buf.append(postfixText);
		    output.println(buf.toString());
		}

		if (counting) {
		    output.format("%1$d%n", numberOfValues);
		}

		if (outputFileName != null) {
		    output.flush();
		    output.close();
		}
	    }
	    catch (IOException ioe) {
		if (readConsole) {
		    System.err.format("Error reading from the console: %1$s%n", ioe.getMessage());
		}
		else {
		    System.err.format("Error accessing the file \"%1$s\": %2$s%n", fileName, ioe.getMessage());
		}
	    }
	}

}
