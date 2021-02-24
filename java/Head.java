/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Roger L. Whitcomb.
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
 *   Display the beginning (head) of a file.
 *
 * History:
 *	23-Feb-2021 (rlwhitcomb)
 *	    Initial implementation.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.ExceptionUtil;


/**
 * Display the head of a file.
 */
public class Head
{
	/** Default number of lines to display. */
	private static final int DEFAULT_LINES = 10;
	/** User can override this default, so this is the current. */
	private static int linesToDisplay = DEFAULT_LINES;
	/** The default charset to use. */
	private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();
	/** The ISO-8859-1 charset. */
	private static final Charset ISO_8859_1_CHARSET = Charset.forName("ISO-8859-1");
	/** The Windows-1252 charset. */
	private static final Charset WIN_1252_CHARSET = Charset.forName("windows-1252");
	/** A UTF-8 charset (another popular choice). */
	private static final Charset UTF_8 = StandardCharsets.UTF_8;
	/** The charset we will actually use. */
	private static Charset cs = DEFAULT_CHARSET;
	/** Whether we are operating in a Windows environment. */
	private static final boolean ON_WINDOWS = Environment.isWindows();
	/** The list of files to process. */
	private static final List<String> files = new ArrayList<>();


	/**
	 * Process one command line option.
	 *
	 * @param opt The option string, without the leading "-", etc.
	 */
	private static void processOption(final String opt) {
	    switch (opt.toLowerCase()) {
		case "utf_8":
		case "utf-8":
		case "utf8":
		case "utf":
		    cs = UTF_8;
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
		default:
		    try {
			linesToDisplay = Integer.parseInt(opt);
		    }
		    catch (NumberFormatException nfe) {
			System.err.format("Number of lines must be a valid integer.");
		    }
		    break;
	    }
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
	    try (BufferedReader rdr = Files.newBufferedReader(path, cs)) {
		String line;
		int lineNo = 0;
		while ((line = rdr.readLine()) != null && lineNo++ < linesToDisplay) {
		    System.out.println(line);
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
	    // Scan through the options to override the defaults
	    for (String arg : args) {
		if (arg.startsWith("--")) {
		    processOption(arg.substring(2));
		}
		else if (arg.startsWith("-")) {
		    processOption(arg.substring(1));
		}
		else if (ON_WINDOWS && arg.startsWith("/")) {
		    processOption(arg.substring(1));
		}
		else {
		    files.add(arg);
		}
	    }

	    // Silently do nothing if no files were specified
	    if (files.isEmpty())
		return;

	    final boolean printName = files.size() > 1;

	    files.forEach(f -> processFile(f, printName));
	}
}

