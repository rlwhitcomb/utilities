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
 *	Concatenate one or more files (equivalent the *nix "cat" program).
 *
 * History:
 *	16-Jul-2020 (rlwhitcomb)
 *	    First coding in Java.
 *	22-Jul-2020 (rlwhitcomb)
 *	    Recast the options now that Options.java has more capability.
 *	31-Jul-2020 (rlwhitcomb)
 *	    Add option to display product information; set program name.
 *	08-Oct-2020 (rlwhitcomb)
 *	    Use flavor of "printProgramInfo" with defaults.
 *	14-Oct-2020 (rlwhitcomb)
 *	    Move text to resources.
 *	11-Dec-2020 (rlwhitcomb)
 *	    Use new product information mechanism.
 *	24-Aug-2021 (rlwhitcomb)
 *	    Add "-locale" option (and Spanish translation).
 *	21-Oct-2021 (rlwhitcomb)
 *	    Use better method to get a valid Locale.
 *	21-Jan-2022 (rlwhitcomb)
 *	    #217: Allow environment default options from CAT_OPTIONS via
 *	    new Options method.
 *	17-Feb-2022 (rlwhitcomb)
 *	    #251: Trap charset encoding problems.
 *	12-Apr-2022 (rlwhitcomb)
 *	    #269: New method to load main program info (in Environment).
 *	18-Apr-2022 (rlwhitcomb)
 *	    #270: Now this is automatic inside "printProgramInfo".
 *
 *	    TODO: wildcard directory names on input
 *	    TODO: -nn to limit to first nn lines, +nn to limit to LAST nn lines (hard to do?)
 */
package info.rlwhitcomb.cat;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnmappableCharacterException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;
import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.Exceptions;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.Options;

/**
 * Java implementation of the *nix "cat" command.
 */
public class Cat {
	/**
	 * Enum of available expected values from the command line.
	 */
	private static enum Expected {
		/** A charset name for the following input file(s). */
		INPUT_CHARSET,
		/** A charset name for the output file. */
		OUTPUT_CHARSET,
		/** An output file name. */
		OUTPUT_FILE,
		/** The locale to use for messages. */
		LOCALE
	}

	/**
	 * The charset currently in use for input files, defaults to the platform
	 * default. Changes with the "-charset", "-utf8" or "-default" options.
	 */
	private static Charset currentInputCharset;

	/**
	 * The output file charset.
	 */
	private static Charset outputCharset;

	/**
	 * File object for the output file.
	 */
	private static File outputFile;

	/**
	 * The only output stream we are writing to.
	 */
	private static PrintStream outputStream = System.out;

	/**
	 * The locale to use for our translated messages.
	 */
	private static Locale locale = null;

	/**
	 * The iteration over the input arguments we are currently on.
	 * Pass 1 scans for output file arguments.
	 * Pass 2 scans for input file(s) and charset(s), and other options.
	 */
	private static int pass;

	/**
	 * Count of the number of input files specified (so we know whether to read from stdin or not).
	 */
	private static int numberOfInputFiles = 0;

	/**
	 * What to expect next on the command line (empty means a file or option flag).
	 */
	private static Optional<Expected> expectedValue = Optional.empty();

	/**
	 * Flag to indicate that there will be no more options, so that all the remaining
	 * command-line values will be file names (or stdin designators).
	 */
	private static boolean noMoreOptions = false;


	/**
	 * Process one of our options.
	 * What happens here depends on the {@link #pass} we are on.
	 *
	 * @param arg	An argument string (with the leading "-", "--", or "/").
	 */
	private static void processOption(final String arg) {
	    if (Options.matchesOption(arg, true, "charset", "cs", "c")) {
		expectedValue = Optional.of(Expected.INPUT_CHARSET);
	    } else if (Options.matchesOption(arg, true, "utf8", "Utf-8", "u", "8")) {
		// Note: here and following: we set the input charset in either pass so that
		// error checking after pass 1 can detect that a charset was given but no files.
		currentInputCharset = StandardCharsets.UTF_8;
	    } else if (Options.matchesOption(arg, true, "utf16", "Utf-16", "16")) {
		currentInputCharset = StandardCharsets.UTF_16;
	    } else if (Options.matchesOption(arg, true, "utf16be", "Utf-16be", "16be", "be")) {
		currentInputCharset = StandardCharsets.UTF_16BE;
	    } else if (Options.matchesOption(arg, true, "utf16le", "Utf-16le", "16le", "le")) {
		currentInputCharset = StandardCharsets.UTF_16LE;
	    } else if (Options.matchesOption(arg, true, "default", "def", "standard", "d", "s")) {
		currentInputCharset = Charset.defaultCharset();
	    } else if (Options.matchesOption(arg, true, "win1252", "win", "w")) {
		currentInputCharset = getCharset("win1252");
	    } else if (Options.matchesOption(arg, true, "iso88591", "iso-8859-1", "iso", "i")) {
		currentInputCharset = StandardCharsets.ISO_8859_1;
	    } else if (Options.matchesOption(arg, true, "ascii", "asc", "a")) {
		currentInputCharset = StandardCharsets.US_ASCII;
	    } else if (Options.matchesOption(arg, false,
			"OutputCharset", "OutputCs", "OutCharset", "OutCs", "ocs")) {
		expectedValue = Optional.of(Expected.OUTPUT_CHARSET);
	    } else if (Options.matchesOption(arg, true,	"OutputFile", "OutFile", "out", "o")) {
		expectedValue = Optional.of(Expected.OUTPUT_FILE);
	    } else if (Options.matchesOption(arg, true, "locale", "loc", "l")) {
		expectedValue = Optional.of(Expected.LOCALE);
	    } else if (Options.matchesOption(arg, true, "NoMoreOptions", "NoOptions", "NoOption",
			"NoMore", "NoOpt", "no", "n")) {
		noMoreOptions = true;
	    } else if (Options.matchesOption(arg, true, "stdin", "std", "in")) {
		if (pass == 1) {
		    numberOfInputFiles++;
		} else {
		    readFromConsole();
		}
	    } else if (Options.matchesOption(arg, true, "version", "vers", "ver", "v")) {
		Environment.printProgramInfo();
		System.exit(0);
	    } else {
		if (pass == 1) {
		    Intl.errFormat("cat#unrecognized", arg);
		    System.exit(2);
		}
	    }
	}

	/**
	 * Loop reading from stdin until EOF, writing to the output file.
	 */
	private static void readFromConsole() {
	    Console console = System.console();
	    if (console == null) {
		// Likely no console to print to either, but we'll try...
		Intl.errPrintln("cat#noConsole");
		System.exit(6);
	    } else {
		String line;
		while ((line = console.readLine()) != null) {
		    outputStream.println(line);
		}
	    }
	}

	/**
	 * Process a single file.
	 *
	 * @param name	Name (path) of the file to process.
	 */
	private static void processFile(final String name) {
	    File file = new File(name);
	    if (file.exists() && !file.isDirectory() && file.canRead()) {
		try {
		    Files.lines(file.toPath(), currentInputCharset).forEach(outputStream::println);
		} catch (UncheckedIOException uioe) {
		    Throwable ex = uioe.getCause();
		    if (ex instanceof UnmappableCharacterException ||
			ex instanceof MalformedInputException) {
			Intl.errFormat("cat#decodeError",
				file.getPath(),
				currentInputCharset.displayName(),
				Exceptions.toString(uioe));
		    } else {
			Intl.errFormat("cat#ioError", file.getPath(), Exceptions.toString(uioe));
		    }
		} catch (IOException ioe) {
		    Intl.errFormat("cat#ioError", file.getPath(), Exceptions.toString(ioe));
		}
	    } else {
		Intl.errFormat("cat#noFileRead", name);
	    }
	}

	private static Charset getCharset(final String name) {
	    try {
		return Charset.forName(name);
	    } catch (IllegalCharsetNameException | UnsupportedCharsetException ex) {
		System.err.println(Exceptions.toString(ex));
		System.exit(3);
	    }
	    return null;
	}

	/**
	 * Iterate over the command line arguments, processing them for either first or second pass.
	 *
	 * @param args	The parsed command line arguments (could be file names or options).
	 */
	private static void processArguments(final String[] args) {
	    for (String arg : args) {
		if (expectedValue.isPresent()) {
		    switch (expectedValue.get()) {
			case INPUT_CHARSET:
			    currentInputCharset = getCharset(arg);
			    break;
			case OUTPUT_CHARSET:
			    if (pass == 1) {
				if (outputCharset != null) {
				    Intl.errPrintln("cat#oneCharset");
				    System.exit(3);
				} else {
				    outputCharset = getCharset(arg);
				}
			    }
			    break;
			case OUTPUT_FILE:
			    if (pass == 1) {
				if (outputFile != null) {
				    Intl.errPrintln("cat#oneOutputFile");
				    System.exit(3);
				} else {
				    outputFile = new File(arg);
				}
			    }
			    break;
			case LOCALE:
			    if (pass == 1) {
				if (locale != null) {
				    Intl.errPrintln("cat#oneLocale");
				    System.exit(3);
				} else {
				    try {
					locale = Intl.getValidLocale(arg);
					if (locale != null && !locale.equals(Locale.getDefault())) {
					    Locale.setDefault(locale);
					    Intl.initAllPackageResources(locale);
					}
				    }
				    catch (IllegalArgumentException iae) {
					System.err.println(Exceptions.toString(iae));
					System.exit(3);
				    }
				}
			    }
			    break;
		    }
		    expectedValue = Optional.empty();
		} else {
		    Optional<String> option = Options.checkOption(arg);
		    if (!noMoreOptions && option.isPresent()) {
			processOption(arg);
		    } else {
			if (pass == 1) {
			    numberOfInputFiles++;
			} else {
			    if (arg.equals("--") || arg.equals("-") || arg.equals("@")) {
				readFromConsole();
			    } else {
				processFile(arg);
			    }
			}
		    }
		}
	    }
	}

	/**
	 * Main program invoked from the command line.
	 * <p> Parses command line for options and file name values.
	 * The options and files are processed in the order they appear,
	 * so options only affect the files listed after that option.
	 * This is particularly to be noted for the encoding flags.
	 * <p> Except that the output file options (file and encoding)
	 * are all processed before any input file options, because
	 * a) It doesn't make sense to have more than one output file; and
	 * b) It makes even less sense to have multiple output charsets.
	 *
	 * @param args	The parsed command line arguments.
	 */
	public static void main(final String[] args) {
	    // Process environment options first, under pass 1 rules
	    pass = 1;
	    Options.environmentOptions(Cat.class, (options) -> {
		processArguments(options);
	    });

	    // First pass: process output file / charset options only
	    pass = 1;
	    processArguments(args);

	    // Now do error checking on the options
	    if (expectedValue.isPresent()) {
		Intl.errFormat("cat#missingOptionValue", expectedValue.get());
		System.exit(1);
	    }

	    // Setup for the output file (if those options were specified on the first pass).
	    if (outputCharset != null && outputFile == null) {
		Intl.errPrintln("cat#outputNameCharset");
		System.exit(4);
	    }
	    if (outputFile != null && outputCharset == null) {
		outputCharset = Charset.defaultCharset();
	    }
	    if (outputFile != null) {
		try {
		    outputStream = new PrintStream(Files.newOutputStream(outputFile.toPath()), false, outputCharset.name());
		} catch (IOException ioe) {
		    // Just default (again) to stdout if there was any problem. TODO: error message also or instead?
		    outputStream = System.out;
		}
	    }

	    if (numberOfInputFiles == 0 && currentInputCharset != null) {
		Intl.errPrintln("cat#noCharsetConsole");
		System.exit(5);
	    }
	    if (currentInputCharset == null && numberOfInputFiles > 0) {
		currentInputCharset = Charset.defaultCharset();
	    }

	    // Second pass: process all the other options and the input files specified
	    pass = 2;
	    // Reset this positional flag so it works correctly on the second pass
	    noMoreOptions = false;
	    processArguments(args);

	    // If no input files (or "-stdin" arguments) specified, then loop reading from console
	    if (numberOfInputFiles == 0) {
		readFromConsole();
	    }
	}
}
