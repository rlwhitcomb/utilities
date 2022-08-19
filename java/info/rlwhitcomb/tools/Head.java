/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2022 Roger L. Whitcomb.
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
 *	Display the beginning (head) of a file.
 *
 *  Change History:
 *	23-Feb-2021 (rlwhitcomb)
 *	    Initial implementation.
 *	29-Mar-2021 (rlwhitcomb)
 *	    Move to new package.
 *	06-Apr-2021 (rlwhitcomb)
 *	    Add "-version" option.
 *	16-Aug-2021 (rlwhitcomb)
 *	    Get version and program title from properties file.
 *	27-Sep-2021 (rlwhitcomb)
 *	    Initialize the static variables inside "main" for testing purposes.
 *	01-Feb-2022 (rlwhitcomb)
 *	    Use new Constants values instead of our own.
 *	08-Feb-2022 (rlwhitcomb)
 *	    Move text out to resources.
 *	    Use new Options method to process command line.
 *	12-Apr-2022 (rlwhitcomb)
 *	    #269: New method to load main program info (in Environment).
 *	18-Apr-2022 (rlwhitcomb)
 *	    #270: Make this automatic now.
 *	09-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 *	18-Aug-2022 (rlwhitcomb)
 *	    #445: Add banner for multiple files with colors. Add color flags.
 *	    Add "-numbers" options.
 *	19-Aug-2022 (rlwhitcomb)
 *	    #445: Add "-header" and "-noheader" flags for multiple files.
 */
package info.rlwhitcomb.tools;

import info.rlwhitcomb.util.ConsoleColor;
import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.Exceptions;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.Options;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static info.rlwhitcomb.util.Constants.*;


/**
 * Display the head (<code><i>nn</i></code> lines) of a file, with optional
 * character set designation.
 */
public class Head
{
	/** Default number of lines to display. */
	private static final int DEFAULT_LINES = 10;

	/** User can override this default, so this is the current. */
	private static int linesToDisplay;

	/** Whether to color the output. */
	private static boolean colored = true;

	/** Whether to add line numbers. */
	private static boolean numbers = false;

	/** Whether to output headers for multiple files or not. */
	private static boolean headers = true;

	/** The charset we will actually use. */
	private static Charset cs = null;

	/** The list of files to process. */
	private static final List<String> files = new ArrayList<>();


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
		case "utf_8":
		case "utf-8":
		case "utf8":
		case "utf":
		    cs = UTF_8_CHARSET;
		    break;
		case "iso-8859-1":
		case "iso_8859_1":
		case "iso88591":
		case "iso8859":
		case "iso":
		    cs = ISO_8859_1_CHARSET;
		    break;
		case "windows-1252":
		case "windows1252":
		case "win1252":
		case "windows":
		case "win":
		    cs = WIN_1252_CHARSET;
		    break;
		case "default":
		case "def":
		    cs = DEFAULT_CHARSET;
		    break;
		case "colors":
		case "color":
		case "clr":
		case "c":
		    colored = true;
		    break;
		case "nocolors":
		case "nocolor":
		case "noclr":
		case "noc":
		case "nc":
		    colored = false;
		    break;
		case "numbers":
		case "number":
		case "num":
		case "n":
		     numbers = true;
		     break;
		case "nonumbers":
		case "nonumber":
		case "nonum":
		case "non":
		case "no":
		    numbers = false;
		    break;
		case "headers":
		case "header":
		case "hdr":
		case "hd":
		case "h":
		    headers = true;
		    break;
		case "noheaders":
		case "noheader":
		case "nohdr":
		case "nohd":
		case "noh":
		case "nh":
		    headers = false;
		    break;
		case "version":
		case "vers":
		case "ver":
		case "v":
		    Environment.printProgramInfo(colored);
		    code = -1;
		    break;
		default:
		    try {
			linesToDisplay = Integer.parseInt(opt);
		    }
		    catch (NumberFormatException nfe) {
			Intl.errPrintln("tools#head.invalidLines");
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
	 * @see #headers
	 */
	private static void processFile(final String file, final boolean printName) {
	    Path path = null;
	    try {
		path = Paths.get(file);
	    }
	    catch (InvalidPathException ex) {
		Intl.errFormat("tools#head.invalidPath", file);
		return;
	    }
	    if (printName && headers) {
		Intl.outFormat("tools#head.fileHeader", path);
	    }
	    try (BufferedReader rdr = Files.newBufferedReader(path, cs)) {
		String line;
		int lineNo = 0;
		while ((line = rdr.readLine()) != null && lineNo++ < linesToDisplay) {
		    if (numbers) {
			System.out.print(ConsoleColor.color(Intl.formatString("tools#head.number", lineNo), colored));
		    }
		    System.out.println(line);
		}
	    }
	    catch (NoSuchFileException nsfe) {
		Intl.errFormat("tools#head.invalidPath", file);
	    }
	    catch (IOException ioe) {
		Intl.errFormat("tools#head.ioError", Exceptions.toString(ioe));
	    }
	    if (printName && headers) {
		System.out.println();
	    }
	}

	/**
	 * @param args The parsed command line argument array.
	 */
	public static void main(final String[] args) {
	    // In order to be testable, we need to actually initialize the static
	    // variables here, so it happens for each test run.
	    files.clear();
	    cs = DEFAULT_CHARSET;
	    linesToDisplay = DEFAULT_LINES;

	    // Scan through the options to override the defaults
	    int code = Options.process(args, opt -> { return processOption(opt); }, arg -> files.add(arg));

	    if (code != 0) {
		if (code < 0)
		    return;
		System.exit(code);
	    }

	    // Silently do nothing if no files were specified
	    if (files.isEmpty())
		return;

	    Intl.setColoring(colored);
	    final boolean printName = files.size() > 1;

	    files.forEach(f -> processFile(f, printName));
	}
}

