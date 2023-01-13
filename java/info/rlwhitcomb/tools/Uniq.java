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
 *  11-Jan-23		Remove line ending options; add case-sensitive options.
 */
package info.rlwhitcomb.tools;

import info.rlwhitcomb.util.Environment;
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
	 * Possible command line options.
	 */
	private static enum Opts implements Options.ToggleEnum
	{
		SENSITIVE	(true,
				 "CaseSensitive", "sensitive", "exact", "ex", "s"),
		INSENSITIVE	(SENSITIVE,
				 "CaseInsensitive", "insensitive", "ignore", "ign", "i"),
		UNIQUE		(true,
				 "unique", "uniq", "un", "u"),
		DUPLICATES	(UNIQUE,
				 "duplicates", "duplicate", "dup", "d"),

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

	private static boolean matchLine(final String line, final String lastUniqueLine) {
	    if (Opts.SENSITIVE.isSet()) {
		return line.equals(lastUniqueLine);
	    }
	    else {
		return line.equalsIgnoreCase(lastUniqueLine);
	    }
	}

	/**
	 * Input is a single file path.  The program will set the process exit code
	 * (test with {@code ERRORLEVEL} on Windows or {@code $?} on other O/Ses).
	 *
	 * @param args The parsed command line argument array.
	 */
	public static void main(final String[] args) {
	    // Reset the options to standard values each time (for testing)
	    Opts.SENSITIVE.set(true);
	    Opts.UNIQUE.set(true);

	    String lastUniqueLine = "";
	    final List<String> files = new ArrayList<>();

	    int ret = Options.process(args, opt -> { return processOption(opt); }, arg -> files.add(arg));

	    if (ret != 0) {
		if (ret < 0)
		    return;
		System.exit(ret);
	    }

	    if (files.isEmpty())
		return;

	    for (String file : files) {
		File f = new File(file);
		if (FileUtilities.exists(f, "fr")) {
		    try {
			List<String> lines = FileUtilities.readFileAsLines(f);
			lastUniqueLine = lines.get(0);

			if (Opts.UNIQUE.isSet())
			    System.out.println(lastUniqueLine);

			int duplicates = 0;

			for (int i = 1; i < lines.size(); i++) {
			    String line = lines.get(i);
			    if (Opts.UNIQUE.isSet()) {
				if (!matchLine(line, lastUniqueLine)) {
				    System.out.println(line);
				    lastUniqueLine = line;
				}
			    }
			    else {
				if (matchLine(line, lastUniqueLine)) {
				    if (duplicates == 0)
					System.out.println(lastUniqueLine);
				    duplicates++;
				}
				else {
				    lastUniqueLine = line;
				    duplicates = 0;
				}
			    }
			}
		    }
		    catch (IOException ioe) {
		    }
		}
	    }
	}
}

