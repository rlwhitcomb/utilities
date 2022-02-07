/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Roger L. Whitcomb.
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
 *	Dump a file in hex / character.
 *
 *  Change History:
 *	06-Feb-2022 (rlwhitcomb)
 *	    #189: Initial implementation.
 *	07-Feb-2022 (rlwhitcomb)
 *	    #189: Implement colored output.
 *	    #238: Color code the hex as well as ASCII.
 */
package info.rlwhitcomb.tools;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import info.rlwhitcomb.util.ConsoleColor;
import static info.rlwhitcomb.util.ConsoleColor.Code.*;
import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.ExceptionUtil;


/**
 * Display a file in hex.
 */
public class HexDump
{
	/** Whether we're running on Windows operating system. */
	private static final boolean ON_WINDOWS = Environment.isWindows();

	/** Default number of bytes to display per line of output. */
	private static final int DEFAULT_BYTES_PER_LINE = 16;

	/** Override number of bytes to display. */
	private static int numberBytesPerLine = DEFAULT_BYTES_PER_LINE;

	/** Lowercase or uppercase output. */
	private static boolean lowerCase = false;

	/** Colored output or not? */
	private static boolean colored = true;

	/** Format for offset printout. */
	private static String offsetFormat;

	/** Format for ASCII bytes printout. */
	private static String asciiByteFormat;

	/** Format for non-ASCII bytes printout. */
	private static String otherByteFormat;

	/** The list of files to process. */
	private static final List<String> files = new ArrayList<>();

	/** Override output destination. */
	private static PrintStream out = System.out;


	/**
	 * Process one command line option.
	 *
	 * @param opt The option string, without the leading "-", etc.
	 * @return    A code indicating an error: {@code < 0} to stop
	 *	      processing, but it's not an error, {@code 0} for
	 *	      no problems, {@code > 0} is an error, and also
	 *	      stop processing.
	 */
	private static int processOption(final String opt) {
	    int code = 0;

	    switch (opt.toLowerCase()) {
		case "nocolors":
		case "nocolor":
		case "nocols":
		case "nocol":
		case "noc":
		case "n":
		    colored = false;
		    break;

		case "colors":
		case "color":
		case "cols":
		case "col":
		case "c":
		    colored = true;
		    break;

		case "lower":
		case "low":
		case "l":
		    lowerCase = true;
		    break;

		case "upper":
		case "up":
		case "u":
		    lowerCase = false;
		    break;

		case "version":
		case "vers":
		case "ver":
		case "v":
		    Environment.printProgramInfo();
		    code = -1;
		    break;

		default:
		    try {
			numberBytesPerLine = Integer.parseInt(opt);
		    }
		    catch (NumberFormatException nfe) {
			System.err.println("Number of bytes must be a valid integer.");
			code = 1;
		    }
		    break;
	    }

	    return code;
	}

	/**
	 * Process one file, that is, list the desired number of lines from the beginning.
	 *
	 * @param file      Name of the file to process.
	 * @param printName Whether or not we are processing multiple files, so each one
	 *                  should be identified.
	 */
	private static void processFile(final String file, final boolean printName) {
	    Path path = null;
	    try {
		path = Paths.get(file);
	    }
	    catch (InvalidPathException ex) {
		System.err.println("Unable to find the file \"" + file + "\".");
		return;
	    }

	    if (printName) {
		System.out.println(path);
	    }

	    try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(path))) {
		int offset = 0;
		int bytesRead = -1;
		int i;
		byte[] bytes = new byte[numberBytesPerLine];
		StringBuilder output = new StringBuilder(numberBytesPerLine * 5 + 20);

		while ((bytesRead = bis.read(bytes)) > 0) {
		    output.setLength(0);
		    output.append(String.format(offsetFormat, offset));

		    offset += bytesRead;

		    for (i = 0; i < numberBytesPerLine; i++) {
			if (i < bytesRead) {
			    int by = ((int) bytes[i]) & 0xFF;
			    if (by >= 0x20 && by <= 0x7E)
				output.append(String.format(asciiByteFormat, by));
			    else
				output.append(String.format(otherByteFormat, by));
			}
			else {
			    output.append("   ");
			}

			if (i % 8 == 3)
			    output.append(" ");
			else if (i % 8 == 7)
			    output.append("  ");
		    }
		    output.append(" " + CYAN_BRIGHT);

		    for (i = 0; i < bytesRead; i++) {
			int by = ((int) bytes[i]) & 0xFF;
			if (by >= 0x20 && by <= 0x7E)
			    output.appendCodePoint(by);
			else
			    output.append(String.format("%1$s%2$s%3$c%4$s", RESET, YELLOW, '\u25E6', CYAN_BRIGHT));

			if (i % 8 == 3)
			    output.append(" ");
			else if (i % 8 == 7)
			    output.append("  ");
		    }
		    output.append(END);

		    out.println(ConsoleColor.color(output.toString(), colored));
		}
	    }
	    catch (NoSuchFileException nsfe) {
		System.err.println("Unable to find the file \"" + file + "\".");
	    }
	    catch (IOException ioe) {
		System.err.println("I/O error: " + ExceptionUtil.toString(ioe));
	    }
	    if (printName) {
		System.out.println();
	    }
	}

	/**
	 * @param args The parsed command line argument array.
	 */
	public static void main(final String[] args) {
	    Environment.loadProgramInfo(HexDump.class);

	    // In order to be testable, we need to actually initialize the static
	    // variables here, so it happens for each test run.
	    files.clear();
	    numberBytesPerLine = DEFAULT_BYTES_PER_LINE;
	    lowerCase = false;

	    // Scan through the options to override the defaults
	    for (String arg : args) {
		int code = 0;

		if (arg.startsWith("--")) {
		    code = processOption(arg.substring(2));
		}
		else if (arg.startsWith("-")) {
		    code = processOption(arg.substring(1));
		}
		else if (ON_WINDOWS && arg.startsWith("/")) {
		    code = processOption(arg.substring(1));
		}
		else {
		    files.add(arg);
		}

		if (code != 0) {
		    if (code < 0)
			return;
		    System.exit(code);
		}
	    }

	    // Silently do nothing if no files were specified
	    if (files.isEmpty())
		return;

	    final boolean printName = files.size() > 1;

	    char formatChar = lowerCase ? 'x' : 'X';
	    offsetFormat = String.format(BLACK_BRIGHT + "%%1$08%1$c:  " + RESET, formatChar);
	    asciiByteFormat = String.format(GREEN_BOLD + "%%1$02%1$c " + RESET, formatChar);
	    otherByteFormat = String.format(BLACK_BRIGHT + "%%1$02%1$c " + RESET, formatChar);

	    files.forEach(f -> processFile(f, printName));
	}
}

