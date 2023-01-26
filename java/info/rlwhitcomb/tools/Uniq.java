/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Roger L. Whitcomb.
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
 *	Source for the "uniq" replacement utility, "unq".
 *
 * History:
 *  05-Jan-23 rlw #28:	Coding.
 *  26-Jan-23		Remove line ending options; add case-sensitive options.
 *			Redo "-u" and "-d" options; add "-c", "-f", and "-s" options.
 */
package info.rlwhitcomb.tools;

import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.Exceptions;
import info.rlwhitcomb.util.FileUtilities;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.Options;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * "Uniq" utility replacement: read the input file and output either the unique
 * lines, or the non-unique lines
 */
public class Uniq
{
	/**
	 * "Expecting" a value on the command line?
	 */
	private static int expecting;

	/**
	 * Number of fields to skip at the start of each line.
	 */
	private static int fieldsToSkip = -1;

	/**
	 * Number of characters to skip on each line.
	 */
	private static int charsToSkip = -1;


	/**
	 * Possible command line options.
	 */
	private static enum Opts implements Options.ToggleEnum
	{
		INSENSITIVE	(false,
				 "CaseInsensitive", "insensitive", "ignore", "ign", "i"),
		UNIQUE		(false,
				 "unique", "uniq", "un", "u"),
		DUPLICATES	(false,
				 "duplicates", "duplicate", "dup", "d"),
		COUNTS		(false,
				 "counts", "count", "c"),

		FIELDS		(false,
				 "fields", "fld", "f"),
		CHARS		(false,
				 "chars", "char", "s"),

		HELP		("help", "h", "?"),
		VERSION		("version", "vers", "ver", "v");

		private String[] choices;
		private boolean value;
		private Opts obverseOpt;

		Opts(final boolean initialValue, final String... validChoices) {
		    choices    = validChoices;
		    value      = initialValue;
		    obverseOpt = null;
		}

		Opts(final Opts otherOpt, final String... validChoices) {
		    choices    = validChoices;
		    value      = false;
		    obverseOpt = otherOpt;
		}

		Opts(final String... validChoices) {
		    choices    = validChoices;
		    value      = false;
		    obverseOpt = null;
		}

		@Override
		public String[] choices() {
		    return choices;
		}

		@Override
		public void set(final boolean newValue) {
		    if (obverseOpt != null)
			obverseOpt.value = !newValue;
		    else
			value = newValue;
		}

		@Override
		public boolean isSet() {
		    if (obverseOpt != null)
			return !obverseOpt.value;
		    else
			return value;
		}

		public static Optional<Opts> match(final String input) {
		    return Options.matchIgnoreCase(values(), input);
		}
	}


	private static int processOption(final String opt) {
	    int code = 0;

	    Optional<Opts> option = Opts.match(opt);
	    if (!option.isPresent()) {
		Intl.errFormat("tools#unique.badOption", opt);
		code = 1;
	    }
	    else {
		switch (option.get()) {
		    case FIELDS:
			expecting = 1;
			break;
		    case CHARS:
			expecting = 2;
			break;

		    case HELP:
			Intl.printHelp("tools#unique");
			code = -1;
			break;
		    case VERSION:
			Environment.printProgramInfo();
			code = -1;
			break;
		}
	    }

	    return code;
	}

	private static int getInt(final String arg) {
	    try {
		return Integer.parseInt(arg);
	    }
	    catch (NumberFormatException nfe) {
		Intl.errFormat("tools#unique.NaN", arg);
		return -1;
	    }
	}

	private static String updateLine(final String line) {
	    String updatedLine = line;

	    if (fieldsToSkip > 0) {
		int field = 0;
		boolean inField = false;
		for (int i = 0; i < line.length(); i++) {
		    char ch = line.charAt(i);
		    if (Character.isWhitespace(ch)) {
			if (inField) {
			    inField = false;
			    field++;
			}
		    }
		    else {
			if (!inField) {
			    inField = true;
			    if (field >= fieldsToSkip) {
				updatedLine = line.substring(i);
				break;
			    }
			}
		    }
		}
	    }
	    else if (charsToSkip > 0) {
		if (line.length() > charsToSkip)
		    updatedLine = line.substring(charsToSkip);
		else
		    updatedLine = "";
	    }

	    return updatedLine;
	}

	private static boolean matchLine(final String line, final String lastUniqueLine) {
	    if (Opts.INSENSITIVE.isSet()) {
		return line.equalsIgnoreCase(lastUniqueLine);
	    }
	    else {
		return line.equals(lastUniqueLine);
	    }
	}

	private static void output(final String line, final int count) {
	    if (Opts.COUNTS.isSet())
		System.out.format("%1$4d %2$s%n", count, line);
	    else
		System.out.println(line);
	}

	/**
	 * Input is a single file path.  The program will set the process exit code
	 * (test with {@code ERRORLEVEL} on Windows or {@code $?} on other O/Ses).
	 *
	 * @param args The parsed command line argument array.
	 */
	public static void main(final String[] args) {
	    // Reset the options to standard values each time (for testing)
	    expecting    = 0;
	    fieldsToSkip = -1;
	    charsToSkip  = -1;

	    Opts.INSENSITIVE.set(false);
	    Opts.UNIQUE.set(false);
	    Opts.DUPLICATES.set(false);
	    Opts.COUNTS.set(false);

	    String lastUniqueLine = "";
	    String lastUpdatedLine = "";
	    final List<String> files = new ArrayList<>();

	    int ret = Options.process(args,
		opt -> {
		    if (expecting != 0) {
			Intl.errFormat("tools#unique.expectNumber", opt);
			return 1;
		    }
		    else {
			return processOption(opt);
		    }
		},
		arg -> {
		    switch (expecting) {
			case 1:
			    fieldsToSkip = getInt(arg);
			    break;
			case 2:
			    charsToSkip = getInt(arg);
			    break;
			default:
			    files.add(arg);
			    break;
		    }
		    expecting = 0;
		});

	    if (ret != 0) {
		if (ret < 0)
		    return;
		System.exit(ret);
	    }

	    if (files.isEmpty())
		return;

	    if (fieldsToSkip > 0 && charsToSkip > 0) {
		Intl.errFormat("tools#unique.badSkips");
		System.exit(1);
	    }

	    boolean unique    = Opts.UNIQUE.isSet();
	    boolean duplicate = Opts.DUPLICATES.isSet();
	    boolean different = !unique && !duplicate;

	    for (String file : files) {
		File f = new File(file);
		if (FileUtilities.exists(f, "fr")) {
		    try {
			List<String> lines = FileUtilities.readFileAsLines(f);
			lastUniqueLine  = lines.get(0);
			lastUpdatedLine = updateLine(lastUniqueLine);
			int count = 1;

			for (int i = 1; i < lines.size(); i++) {
			    String line        = lines.get(i);
			    String updatedLine = updateLine(line);

			    if (matchLine(updatedLine, lastUpdatedLine)) {
				count++;
			    }
			    else {
				if (different || (unique && count == 1) || (duplicate && count > 1)) {
				    output(lastUniqueLine, count);
				}
				lastUniqueLine  = line;
				lastUpdatedLine = updatedLine;
				count = 1;
			    }
			}
			if (different || (unique && count == 1) || (duplicate && count > 1)) {
			    output(lastUniqueLine, count);
			}
		    }
		    catch (IOException ioe) {
			Intl.errFormat("tools#unique.ioError", f.getPath(), Exceptions.toString(ioe));
			System.exit(2);
		    }
		}
		else {
		    Intl.errFormat("tools#unique.fileNotFound", f.getPath());
		}
	    }
	}
}

