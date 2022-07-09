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
 *	    Make the address more readable with yellow.
 *	    #238: Use different substitute character for control characters.
 *	08-Feb-2022 (rlwhitcomb)
 *	    #238: Move text to resources.
 *	    Use new Options method to process command line.
 *	09-Feb-2022 (rlwhitcomb)
 *	    #238: Add help.
 *	    Color code the hex the same way the ASCII is coded.
 *	    Implement "-out" and "-charset" parameters.
 *	10-Feb-2022 (rlwhitcomb)
 *	    Implement options for spaces or not.
 *	    Use 0 width (default) to size output according to console width.
 *	20-Feb-2022 (rlwhitcomb)
 *	    #253: Fix line width calculations.
 *	12-Apr-2022 (rlwhitcomb)
 *	    #269: New method to load main program info (in Environment).
 *	18-Apr-2022 (rlwhitcomb)
 *	    #270: Make this automatic.
 *	09-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 */
package info.rlwhitcomb.tools;

import info.rlwhitcomb.util.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static info.rlwhitcomb.util.ConsoleColor.Code.*;


/**
 * Display a file in hex.
 */
public class HexDump
{
	/** Default number of bytes to display per line of output (0 = use console width to calculate). */
	private static final int DEFAULT_BYTES_PER_LINE = 0;

	/** Maximum number of bytes per line we're going to allow. */
	private static final int MAX_BYTES_PER_LINE = 999;

	/** Override number of bytes to display. */
	private static int numberBytesPerLine = DEFAULT_BYTES_PER_LINE;

	/** Lowercase or uppercase output. */
	private static boolean lowerCase = false;

	/** Whether to enhance the output with spaces. */
	private static boolean spaces = true;

	/** Colored output or not? */
	private static boolean colored = true;

	/** Format for offset printout. */
	private static String offsetFormat;

	/** Format for ASCII bytes printout. */
	private static String asciiByteFormat;

	/** Format for ASCII control character bytes. */
	private static String controlByteFormat;

	/** Format for high bytes printout. */
	private static String highByteFormat;

	/** The list of files to process. */
	private static final List<String> files = new ArrayList<>();

	/** Override output destination. */
	private static PrintStream out = System.out;

	/** Output file name. */
	private static String outputFileName;

	/** Output file charset name. */
	private static String outputFileCharset;

	/** Next expected argument on the command line. */
	private static int nextArgument;


	/**
	 * Process one command line option.
	 *
	 * @param opt The option string, without the leading "-", etc.
	 * @return    A code indicating the outcome: {@code == -1 } to stop
	 *	      processing, but it's not an error, {@code < -1} for
	 *	      an actual error, {@code == 0} for no problems,
	 *	      {@code > 0} is a signal of what to expect next
	 *	      on the command line..
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

		case "nospaces":
		case "nospace":
		case "nosp":
		case "nos":
		    spaces = false;
		    break;

		case "spaces":
		case "space":
		case "sp":
		case "s":
		    spaces = true;
		    break;

		case "output":
		case "out":
		case "o":
		    nextArgument = 1;
		    break;

		case "charset":
		case "chars":
		case "char":
		case "cs":
		    nextArgument = 2;
		    break;

		case "version":
		case "vers":
		case "ver":
		case "v":
		    Environment.printProgramInfo();
		    code = -1;
		    break;

		case "help":
		case "h":
		case "?":
		    Intl.printHelp("tools#hexdump");
		    code = -1;
		    break;

		default:
		    try {
			numberBytesPerLine = Integer.parseInt(opt);
			if (numberBytesPerLine < 0 || numberBytesPerLine > MAX_BYTES_PER_LINE) {
			    Intl.errPrintln("tools#hexdump.invalidNumberBytes");
			    code = -2;
			}
		    }
		    catch (NumberFormatException nfe) {
			Intl.errPrintln("tools#hexdump.invalidNumberBytes");
			code = -2;
		    }
		    break;
	    }

	    return code;
	}

	private static void processNonOption(final String arg) {
	    switch (nextArgument) {
		case 1:
		    outputFileName = arg;
		    break;

		case 2:
		    outputFileCharset = arg;
		    break;

		default:
		    files.add(arg);
		    break;
	    }
	    nextArgument = -1;
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
		Intl.errFormat("tools#hexdump.invalidPath", file);
		return;
	    }

	    if (printName) {
		out.println(path);
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
			    else if ((by >= 0x00 && by <= 0x1F) || (by == 0x7F))
				output.append(String.format(controlByteFormat, by));
			    else
				output.append(String.format(highByteFormat, by));
			}
			else {
			    output.append("   ");
			}

			if (spaces) {
			    if (i % 8 == 3)
				output.append(" ");
			    else if (i % 8 == 7)
				output.append("  ");
			}
		    }
		    output.append((spaces ? " " : "   ") + CYAN_BRIGHT);

		    for (i = 0; i < bytesRead; i++) {
			int by = ((int) bytes[i]) & 0xFF;
			if (by >= 0x20 && by <= 0x7E)
			    output.appendCodePoint(by);
			else if ((by >= 0x00 && by <= 0x1F) || (by == 0x7F))
			    output.append(String.format("%1$s%2$s%3$c%4$s", RESET, RED, '\u25AB', CYAN_BRIGHT));
			else
			    output.append(String.format("%1$s%2$s%3$c%4$s", RESET, YELLOW, '\u25E6', CYAN_BRIGHT));

			if (spaces) {
			    if (i % 8 == 3)
				output.append(" ");
			    else if (i % 8 == 7)
				output.append("  ");
			}
		    }
		    output.append(END);

		    out.println(ConsoleColor.color(output.toString(), colored));
		}
	    }
	    catch (NoSuchFileException nsfe) {
		Intl.errFormat("tools#hexdump.invalidPath", file);
	    }
	    catch (IOException ioe) {
		Intl.errFormat("tools#hexdump.ioError", Exceptions.toString(ioe));
	    }
	    if (printName) {
		out.println();
	    }
	}

	/**
	 * @param args The parsed command line argument array.
	 */
	public static void main(final String[] args) {
	    // In order to be testable, we need to actually initialize the static
	    // variables here, so it happens for each test run.
	    files.clear();
	    numberBytesPerLine = DEFAULT_BYTES_PER_LINE;
	    lowerCase = false;
	    spaces = true;
	    nextArgument = -1;
	    outputFileName = null;
	    outputFileCharset = null;

	    // Scan through the options to override the defaults
	    int code = Options.process(args, opt -> { return processOption(opt); }, arg -> processNonOption(arg));

	    if (code != 0) {
		if (code == -1)
		    return;
		if (code < -1)
		    System.exit(-code - 1);
	    }

	    switch (nextArgument) {
		case -1:
		    break;
		case 1:
		    Intl.errFormat("tools#hexdump.missingOutputFile");
		    System.exit(3);
		case 2:
		    Intl.errFormat("tools#hexdump.missingOutputCharset");
		    System.exit(4);
	    }

	    // Silently do nothing if no files were specified
	    if (files.isEmpty())
		return;

	    if (numberBytesPerLine == 0) {
		int consoleWidth = Environment.consoleWidth();
		numberBytesPerLine = (consoleWidth - 13) / 4;
		// Start with a multiple of four
		numberBytesPerLine -= (numberBytesPerLine % 4);

		if (spaces) {
		    // In the hex display, 1 extra per four and 2 extra for every 8, and the same for the
		    // character display. Header and blank separator are 13 altogether, each byte
		    // is 3 in the hex, plus one in the character (or 4 altogether)
		    while (numberBytesPerLine * 4 + (numberBytesPerLine / 4 * 2) + (numberBytesPerLine / 8 * 2) + 13 > consoleWidth)
			numberBytesPerLine -= 4;
		}
	    }
	    else {
		if (spaces && numberBytesPerLine % 4 != 0) {
		    Intl.errPrintln("tools#hexdump.numberMultipleFour");
		    System.exit(5);
		}
	    }

	    final boolean printName = files.size() > 1;

	    char formatChar = lowerCase ? 'x' : 'X';
	    offsetFormat      = String.format(YELLOW_BOLD  + "%%1$08%1$c:  " + RESET, formatChar);
	    asciiByteFormat   = String.format(GREEN_BOLD   + "%%1$02%1$c "   + RESET, formatChar);
	    controlByteFormat = String.format(RED          + "%%1$02%1$c "   + RESET, formatChar);
	    highByteFormat    = String.format(BLACK_BRIGHT + "%%1$02%1$c "   + RESET, formatChar);

	    if (outputFileName != null) {
		if (outputFileCharset == null)
		    outputFileCharset = StandardCharsets.UTF_8.name();;

		Path path = null;
		try {
		    path = Paths.get(outputFileName);
		}
		catch (InvalidPathException ex) {
		    Intl.errFormat("tools#hexdump.invalidPath", outputFileName);
		    System.exit(2);
		}
		try {
		    out = new PrintStream(Files.newOutputStream(path), false, outputFileCharset);
		}
		catch (IOException ioe) {
		    Intl.errFormat("tools#hexdump.ioError", Exceptions.toString(ioe));
		}
	    }

	    files.forEach(f -> processFile(f, printName));
	}
}

