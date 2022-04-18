/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013-2014,2016-2022 Roger L. Whitcomb.
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
 *          Print version information.
 *      16-Oct-2020 (rlwhitcomb)
 *          Incorporate latest code.
 *      21-Dec-2020 (rlwhitcomb)
 *          Update obsolete Javadoc constructs.
 *      05-Jan-2021 (rlwhitcomb)
 *          Add "-single" option to join lines without anything else.
 *      06-Jan-2021 (rlwhitcomb)
 *          Fix "-single" processing. Use regular Environment program info.
 *          Also use the regular classes instead of our duplicates (no
 *          need for a standalone class anymore).
 *	11-Jan-2021 (rlwhitcomb)
 *	    The "-single" option really should just make one line out of
 *	    the input without stripping out the commas, or anything else.
 *	11-Jan-2021 (rlwhitcomb)
 *	    Refactor to unify and simplify error reporting.
 *	22-Jan-2021 (rlwhitcomb)
 *	    New "-u" option ("unchanged") which will read the input, do
 *	    any prefix/postfix text, but otherwise leave the input alone.
 *	    Refactoring. Move to "util" package so others can use us
 *	    programmatically. Implements the Testable interface so we
 *	    can use with the (upcoming) tester program.
 *	22-Aug-2021 (rlwhitcomb)
 *	    Add "-upper" and "-lower" options.
 *	27-Oct-2021 (rlwhitcomb)
 *	    Strip off line continuations ("\" at the end of the line).
 *	03-Dec-2021 (rlwhitcomb)
 *	    Add "-indent" option.
 *	18-Feb-2022 (rlwhitcomb)
 *	    Use Exceptions to get better error messages.
 *	12-Apr-2022 (rlwhitcomb)
 *	    #269: New method for loading main program info (in Environment).
 *	18-Apr-2022 (rlwhitcomb)
 *	    #270: Make this automatic.
 */
package info.rlwhitcomb.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import info.rlwhitcomb.Testable;
import info.rlwhitcomb.util.Exceptions;


/**
 * Utility program for converting lists of values between various formats
 * that we need sometimes, such as a single line separated by commas,
 * separate lines for each value, separate lines with commas, etc.
 */
public class Lists
	implements Testable
{
	private boolean concatenate = false;
	private boolean join        = false;
	private boolean blanks      = false;
	private boolean whitespace  = false;
	private boolean counting    = false;
	private boolean cutting     = false;
	private boolean newlines    = false;
	private boolean single      = false;
	private boolean unchanged   = false;
	private boolean makeLower   = false;
	private boolean makeUpper   = false;

	private int width   = 0;
	private int cutSize = 0;
	private int indent  = 0;

	private List<String> fileNames = null;
	private String outputFileName  = null;
	private PrintStream output     = null;

	private boolean sawConsoleInput = false;

	private String indentText     = null;
	private String prefixText     = null;
	private String postfixText    = null;
	private int prefixTextLength  = 0;
	private int postfixTextLength = 0;

	private static final String STDIN = "@";

	private static final String[] HELP = {
	    "Usage: java Lists [-c] [-j] [-b] [-n] [-l] [-nnn] [-w] [-e prefix_text] [-f postfix_text] [-x nn]",
	    "                  [-lower] [-upper] [-o output_file] [-i nn] [list_file_name+ | " + STDIN + "]",
	    "",
	    "  Aliases: -c | -concat | -concatenate; -j | -join; -b | -blank | -blanks; -n | -count | -counting",
	    "           -l | -line | -lines | -newlines; -w | -white | -whitespace; -e | -pre | -prefix",
	    "           -f | -post | -postfix; -x | -cut | -cutting; -s | -single; -u | -unchanged",
	    "           -lower | -low; -upper | -up; -o | -out | -output; -i | -in | -indent",
	    "",
	    "  If you specify the \"-c\" flag the output will have all the lines",
	    "    of the file concatenated (using commas) into a single line.",
	    "",
	    "  The \"-j\" flag is similar except the lines will be joined by a",
	    "    single space between each into a single line.",
	    "",
	    "  Without either of these options, the input file will be deconstructed",
	    "    into one element per line and the commas (if any) will be removed.",
	    "",
	    "  The \"-x nn\" option will cut the nn number of characters (if present)",
	    "    from the start of each input line (useful for \"svn st\" output).",
	    "",
	    "  Using \"-b\" without \"-c\" will print a blank line between",
	    "    each output value, while with \"-c\" will put a blank after",
	    "    each comma.",
	    "",
	    "  Using \"-l\" will print newlines after each entry, even with \"-c\"",
	    "",
	    "  The \"-nnn\" option (where \"nnn\" is a number from 1 to 255) specifies",
	    "    a maximum line width for the \"-c\" output mode.",
	    "",
	    "  The \"-n\" option simply counts the number of entries in the input",
	    "    using the rules from the other options and outputs the number of entries.",
	    "",
	    "  The \"-w\" option will recognize input files where the input",
	    "    values are separated by whitespace instead of \",\".",
	    "",
	    "  The \"-e prefix_text\" and \"-f postfix_text\" options will add this text",
	    "    before (after) the single line or each line of the output.",
	    "",
	    "  The \"-s\" option will join the whole file into a single string without",
	    "    changing anything else.",
	    "",
	    "  The \"-u\" option will do nothing to the input except do any cutting specified,",
	    "    and add the prefix or postfix text to each line.",
	    "",
	    "  The \"-i nn\" option will indent each line by nn tab/space characters.",
	    "",
	    "  Using \"" + STDIN + "\" or nothing for the list_file_name will read from stdin.",
	    "  Multiple file names are allowed as well as mixing \"" + STDIN + "\" with regular",
	    "    file names, which will simply read and concatenate everything together.",
	    "",
	    "  Unless the \"-o output_file\" option is given, all output will be sent to",
	    "    stdout. This output file option is preferable when reading from the console.",
	    "",
	    "  Use \"-help\" (or \"-h\" or \"-?\") to display this help text.",
	    "  Use \"-version\" (or \"-vers\", \"-ver\", or \"-v\" to display version information.)",
	    ""
	};


	private static void usage() {
	    usage(false);
	}

	private static void usage(boolean helpOnly) {
	    if (!helpOnly)
		System.err.println();
	    Arrays.stream(HELP).forEach(System.err::println);
	}

	private static int errUsage(String messageFormat, Object... args) {
	    System.err.println(String.format(messageFormat, args));
	    usage();
	    return BAD_ARGUMENT;
	}

	private static int missingValue(String expected, String option) {
	    errUsage("Expecting %1$s for the \"-%2$s\" option!", expected, option);
	    return MISSING_OPTION;
	}

	private static int onlyOnce(String option) {
	    return errUsage("Can only specify the %1$s once!", option);
	}

	private void outPrint(String value) {
	    if (makeLower)
		output.print(value.toLowerCase());
	    else if (makeUpper)
		output.print(value.toUpperCase());
	    else
		output.print(value);
	}

	private void outPrintln() {
	    output.println();
	}

	private void outPrintln(String value) {
	    outPrint(value);
	    outPrintln();
	}

	private void outputLine(StringBuilder buf) {
	    if (postfixText != null)
		buf.append(postfixText);

	    outPrintln(buf.toString());

	    buf.setLength(0);

	    if (indentText != null)
		buf.append(indentText);

	    if (prefixText != null)
		buf.append(prefixText);
	}


	@Override
	public String getVersion() {
	    return Environment.getAppVersion();
	}


	@Override
	public String getTestName() {
	    return CharUtil.makeFileStringList(fileNames);
	}


	@Override
	public int setup(String[] args) {
	    fileNames = new ArrayList<>();

	    // First parse the command line arguments
	    boolean sawOutputOption      = false;
	    boolean sawCutOption         = false;
	    boolean sawPrefixTextOption  = false;
	    boolean sawPostfixTextOption = false;
	    boolean sawIndentOption      = false;

	    for (String arg : args) {
		String option = Options.isOption(arg);
		if (option != null) {
		    if (sawOutputOption) {
			return missingValue("an output file name", "o");
		    }
		    if (sawCutOption) {
			return missingValue("a number", "x");
		    }
		    if (sawPrefixTextOption) {
			return missingValue("prefix text", "e");
		    }
		    if (sawPostfixTextOption) {
			return missingValue("postfix text", "f");
		    }
		    if (sawIndentOption) {
			return missingValue("a number", "i");
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
		    else if (Options.matchesOption(arg, true, "unchanged", "nochange", "noc", "un", "no", "u"))
			unchanged = true;
		    else if (Options.matchesOption(arg, true, "counting", "count", "n"))
			counting = true;
		    else if (Options.matchesOption(arg, true, "cutting", "cut", "x")) {
			cutting = true;
			sawCutOption = true;
		    }
		    else if (Options.matchesOption(arg, true, "lower", "low"))
			makeLower = true;
		    else if (Options.matchesOption(arg, true, "upper", "up"))
			makeUpper = true;
		    else if (Options.matchesOption(arg, true, "output", "out", "o")) {
			if (outputFileName != null) {
			    return onlyOnce("output file name");
			}
			sawOutputOption = true;
		    }
		    else if (Options.matchesOption(arg, true, "prefix", "pre", "e")) {
			if (prefixText != null) {
			    return onlyOnce("prefix text");
			}
			sawPrefixTextOption = true;
		    }
		    else if (Options.matchesOption(arg, true, "postfix", "post", "f")) {
			if (postfixText != null) {
			    return onlyOnce("postfix text");
			}
			sawPostfixTextOption = true;
		    }
		    else if (Options.matchesOption(arg, true, "indent", "in", "i")) {
			if (indent > 0) {
			    return onlyOnce("indent value");
			}
			sawIndentOption = true;
		    }
		    else if (Options.matchesOption(arg, true, "help", "h", "?")) {
			usage(true);
			return ACTION_DONE;
		    }
		    else if (Options.matchesOption(arg, true, "version", "vers", "ver", "v")) {
			Environment.printProgramInfo(System.err);
			return ACTION_DONE;
		    }
		    else {
			try {
			    width = Integer.parseInt(option);
			    if (width < 1 || width > 255) {
				return errUsage("Width value (%1$d) must be between 1 and 255.", width);
			    }
			}
			catch (NumberFormatException nfe) {
			    return errUsage("Unsupported option: \"%1$s\".", arg);
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
			    return errUsage("The cut size (%1$s) should be between 1 and 255.", arg);
			}
			sawCutOption = false;
		    }
		    else if (sawPrefixTextOption) {
			prefixText = arg;
			prefixTextLength = prefixText.length();
			sawPrefixTextOption = false;
		    }
		    else if (sawPostfixTextOption) {
			postfixText = arg;
			postfixTextLength = postfixText.length();
			sawPostfixTextOption = false;
		    }
		    else if (sawIndentOption) {
			try {
			    indent = Integer.parseInt(arg);
			}
			catch (NumberFormatException nfe) {
			    indent = -1; // to trigger the error below
			}
			if (indent < 1 || indent > 255) {
			    return errUsage("The indent value (%1$s) should be between 1 and 255.", arg);
			}
			sawIndentOption = false;
		    }
		    else {
			if (arg.equals(STDIN)) {
			    if (sawConsoleInput) {
				return errUsage("Cannot specify \"%1$s\" for the input more than once.", STDIN);
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
		return errUsage("Specifying an output width (\"-%1$d\") is only effective with the \"-c\" or \"-j\" options.", width);
	    }

	    if (counting && (concatenate || blanks || width > 0)) {
		return errUsage("The \"-count\" option should not be used together with either the%n\"-c\", \"-j\", or \"-b\" options%nor with an output width.");
	    }

	    if (makeUpper && makeLower) {
		return errUsage("You can specify either \"-lower\" or \"-upper\", but not both.");
	    }

	    if (outputFileName != null) {
		try {
		    output = new PrintStream(outputFileName);
		}
		catch (IOException ioe) {
		    errUsage("Unable to open \"%1$s\" file for writing: %2$s", outputFileName, Exceptions.toString(ioe));
		    return OUTPUT_IO_ERROR;
		}
	    }
	    else {
		output = System.out;
	    }

	    return SUCCESS;
	}

	private static String indentString(final int width) {
	    StringBuilder buf = new StringBuilder(width);
	    int tabs = width / 8;
	    for (int i = 0; i < tabs; i++)
		buf.append('\t');
	    int spaces = width - (tabs * 8);
	    for (int i = 0; i < spaces; i++)
		buf.append(' ');
	    return buf.toString();
	}

	private static int outputLength(final StringBuilder buf) {
	    int length = 0;
	    for (int i = 0; i < buf.length(); i++) {
		char ch = buf.charAt(i);
		if (ch == '\t')
		    length += 8;
		else
		    length++;
	    }
	    return length;
	}


	@Override
	public int execute() {
	    int numberOfValues  = 0;
	    int nextFile        = 0;
	    boolean readConsole = false;
	    String fileName     = "";
	    BufferedReader r    = null;

	    if (indent > 0) {
		indentText = indentString(indent);
	    }

	    try {
		StringBuilder buf = new StringBuilder();
		if (indentText != null)
		    buf.append(indentText);
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
			// In "single" mode, strip off a line continuation character
			// (maybe, eventually, all modes; still not sure)
			if (single) {
			    if (line.endsWith("\\"))
				line = line.substring(0, line.length() - 1);
			}

			// First off, do any required cutting of the input line
			if (cutting) {
			    if (line.length() > cutSize) {
				line = line.substring(cutSize);
			    }
			}

			boolean empty = line.isEmpty();

			if (single) {
			    if (!empty) {
				if (counting) {
				    numberOfValues++;
				}
				else {
				    buf.append(line);
				}
			    }
			}
			else if (unchanged) {
			    if (counting) {
				numberOfValues++;
			    }
			    else {
				if (!empty) {
				    buf.append(line);
				}
				outputLine(buf);
			    }
			}
			else if (!empty) {
			    String[] parts = whitespace ? line.split("\\s+") : line.split("\\s*,\\s*");
			    for (String part : parts) {
				String value = part.trim();
				if (!value.isEmpty()) {
				    if (counting) {
					numberOfValues++;
				    }
				    else {
					if (concatenate) {
					    int textLength = outputLength(buf);
					    if (textLength > indent + prefixTextLength) {
						if (!join)
						    buf.append(",");
						if (width > 0 && outputLength(buf) >= width) {
						    outputLine(buf);
						}
						else if (blanks || join)
						    buf.append(" ");
					    }
					    if (width > 0 && outputLength(buf) + value.length() >= width) {
						outputLine(buf);
					    }
					    buf.append(value);
					}
					else {
					    if (indentText != null)
						outPrint(indentText);
					    if (prefixText != null)
						outPrint(prefixText);
					    outPrint(value);
					    if (newlines)
						outPrint(",");
					    if (postfixText != null)
						outPrint(postfixText);
					    outPrintln();
					    if (blanks)
						outPrintln();
					}
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
		    outPrintln(buf.toString());
		}

		if (counting) {
		    outPrintln(String.format("%1$d", numberOfValues));
		}

		if (outputFileName != null) {
		    output.flush();
		    output.close();
		}
	    }
	    catch (IOException ioe) {
		if (readConsole) {
		    System.err.format("Error reading from the console: %1$s%n", Exceptions.toString(ioe));
		}
		else {
		    System.err.format("Error accessing the file \"%1$s\": %2$s%n", fileName, Exceptions.toString(ioe));
		}
		return INPUT_IO_ERROR;
	    }

	    return SUCCESS;
	}

	public Lists() {
	}

	public static void main(String[] args) {
	    Lists instance = new Lists();

	    int result = SUCCESS;
	    if ((result = instance.setup(args)) == SUCCESS) {
		result = instance.execute();
	    }

	    Testable.exit(result);
	}
}
