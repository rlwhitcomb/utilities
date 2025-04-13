/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013-2014,2016-2022,2025 Roger L. Whitcomb.
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
 *	    Start history.
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
 *	09-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 *	27-Mar-2025 (rlwhitcomb)
 *	    Move text to the resource file for internationalization.
 *	12-Apr-2025 (rlwhitcomb)
 *	    Add "-noblanks" option.
 */
package info.rlwhitcomb.util;

import info.rlwhitcomb.Testable;
import info.rlwhitcomb.util.Exceptions;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
	private boolean noblanks    = false;
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


	private static void usage() {
	    usage(false);
	}

	private static void usage(boolean helpOnly) {
	    if (!helpOnly)
		System.err.println();

	    Map<String, String> symbols = new HashMap<>();
	    symbols.put("STDIN", STDIN);

	    Intl.printHelp(System.err, "util#lists", symbols, true);
	}

	private static int errUsage(String messageKey, Object... args) {
	    String message = Intl.formatString("util#lists." + messageKey, args);
	    Intl.errFormat("util#lists.error", message);
	    usage();
	    return BAD_ARGUMENT;
	}

	private static int missingValue(String expectedKey, String option) {
	    errUsage("missingValue", Intl.getString("util#lists", expectedKey), option);
	    return MISSING_OPTION;
	}

	private static int onlyOnce(String optionKey) {
	    return errUsage("onlyOnce", Intl.getString("util#lists", optionKey));
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
	    // We have no "-colors" option, so enable by default
	    Intl.setColoring(true);

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
			return missingValue("optionOutputFile", "o");
		    }
		    if (sawCutOption) {
			return missingValue("optionNumber", "x");
		    }
		    if (sawPrefixTextOption) {
			return missingValue("optionPrefix", "e");
		    }
		    if (sawPostfixTextOption) {
			return missingValue("optionPostfix", "f");
		    }
		    if (sawIndentOption) {
			return missingValue("optionNumber", "i");
		    }

		    if (Options.matchesOption(arg, true, "concatenate", "concat", "c"))
			concatenate = true;
		    else if (Options.matchesOption(arg, "join", "j"))
			concatenate = join = true;
		    else if (Options.matchesOption(arg, true, "blanks", "blank", "b"))
			blanks = true;
		    else if (Options.matchesOption(arg, true, "noblanks", "noblank", "nob", "nb"))
			noblanks = true;
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
			    return onlyOnce("optionOutputFile");
			}
			sawOutputOption = true;
		    }
		    else if (Options.matchesOption(arg, true, "prefix", "pre", "e")) {
			if (prefixText != null) {
			    return onlyOnce("optionPrefix");
			}
			sawPrefixTextOption = true;
		    }
		    else if (Options.matchesOption(arg, true, "postfix", "post", "f")) {
			if (postfixText != null) {
			    return onlyOnce("optionPostfix");
			}
			sawPostfixTextOption = true;
		    }
		    else if (Options.matchesOption(arg, true, "indent", "in", "i")) {
			if (indent > 0) {
			    return onlyOnce("optionIndent");
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
				return errUsage("badWidth", width);
			    }
			}
			catch (NumberFormatException nfe) {
			    return errUsage("unsupported", arg);
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
			    return errUsage("badCutSize", arg);
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
			    return errUsage("badIndent", arg);
			}
			sawIndentOption = false;
		    }
		    else {
			if (arg.equals(STDIN)) {
			    if (sawConsoleInput) {
				return errUsage("noDupInput", STDIN);
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
	    if (sawOutputOption) {
		return missingValue("optionOutputFile", "o");
	    }
	    if (sawCutOption) {
		return missingValue("optionNumber", "x");
	    }
	    if (sawPrefixTextOption) {
		return missingValue("optionPrefix", "e");
	    }
	    if (sawPostfixTextOption) {
		return missingValue("optionPostfix", "f");
	    }
	    if (sawIndentOption) {
		return missingValue("optionNumber", "i");
	    }

	    if (!concatenate && width > 0) {
		return errUsage("widthNeedsJoin", width);
	    }
	    if (counting && (concatenate || blanks || width > 0)) {
		return errUsage("countWontWork");
	    }
	    if (noblanks && !join) {
		return errUsage("noBlankNeedJoin");
	    }
	    if (makeUpper && makeLower) {
		return errUsage("notBothCase");
	    }

	    if (outputFileName != null) {
		try {
		    output = new PrintStream(outputFileName);
		}
		catch (IOException ioe) {
		    Intl.errFormat("util#lists.errOutputFile", outputFileName, Exceptions.toString(ioe));
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
						else if (blanks || (join && !noblanks))
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
		    Intl.errFormat("util#lists.errConsoleRead", Exceptions.toString(ioe));
		}
		else {
		    Intl.errFormat("util#lists.errFileAccess", fileName, Exceptions.toString(ioe));
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
