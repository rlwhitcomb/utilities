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
 *	Count lines, words, and characters in files (drop-in replacement for
 *	*nix "wc" program).
 *
 *  Change History:
 *	29-Mar-2021 (rlwhitcomb)
 *	    Initial implementation.
 *	29-Mar-2021 (rlwhitcomb)
 *	    Fix formatting with overflow sizes.
 *	06-Apr-2021 (rlwhitcomb)
 *	    Add "-version" command.
 *	06-Apr-2021 (rlwhitcomb)
 *	    Add "-lines", "-words", and "-chars" options to print only that
 *	    one value (instead of all three).
 *	16-Aug-2021 (rlwhitcomb)
 *	    Put program title and version into properties file.
 *	27-Sep-2021 (rlwhitcomb)
 *	    Make this class testable using "Tester".
 *	    Add option to "regularize" paths to *nix standards (for testing).
 *	    And oops! Fix that code.
 */
package info.rlwhitcomb.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
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
 * Count lines, words, and characters in one or more files, or from standard input.
 */
public class WordCount
{
	/** The maximum file size we want to read in all at once (2MB). */
	private static final long MAX_ONE_SHOT_FILE_SIZE = 2_097_152L;

	/** The default charset to use. */
	private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();
	/** The ISO-8859-1 charset. */
	private static final Charset ISO_8859_1_CHARSET = Charset.forName("ISO-8859-1");
	/** The Windows-1252 charset. */
	private static final Charset WIN_1252_CHARSET = Charset.forName("windows-1252");
	/** A UTF-8 charset (another popular choice). */
	private static final Charset UTF_8 = StandardCharsets.UTF_8;
	/** The charset we will actually use. */
	private static Charset cs;

	/** Whether we are operating in a Windows environment. */
	private static final boolean ON_WINDOWS = Environment.isWindows();

	/** The list of files to process. */
	private static final List<String> files = new ArrayList<>();

	/** Option to regularize path separators in file names to Unix/Linux standards. */
	private static boolean regularizePaths;

	/** Option to report only the number of lines. */
	private static boolean onlyLines;
	/** Option to report only the number of words. */
	private static boolean onlyWords;
	/** Option to report only the number of characters. */
	private static boolean onlyChars;

	/** The line count for the current file. */
	private static int currentLines;
	/** The word count for the current file. */
	private static int currentWords;
	/** The character count for the current file. */
	private static int currentChars;

	/** Whether we are in the middle of a word right now. */
	private static boolean currentInWord;

	/** The total line count for all files. */
	private static int totalLines;
	/** The total word count for all files. */
	private static int totalWords;
	/** The total character count for all files. */
	private static int totalChars;


	/**
	 * Process one command line option.
	 *
	 * @param prefix The leading character(s) stripped before the "opt" value.
	 * @param opt    The option string, without the leading prefix.
	 * @return	 A code indicating an error: {@code < 0} to stop processing,
	 *		 but it's not an error, {@code 0} for no problems, {@code > 0}
	 *		 is an error, and also stop processing.
	 */
	private static int processOption(final String prefix, final String opt) {
	    int code = 0;

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
		case "lines":
		case "line":
		case "l":
		    onlyLines = true;
		    onlyWords = false;
		    onlyChars = false;
		    break;
		case "words":
		case "word":
		case "w":
		    onlyWords = true;
		    onlyLines = false;
		    onlyChars = false;
		    break;
		case "characters":
		case "character":
		case "chars":
		case "char":
		case "c":
		    onlyChars = true;
		    onlyLines = false;
		    onlyWords = false;
		    break;
		case "regularize":
		case "regular":
		case "reg":
		    regularizePaths = true;
		    break;
		case "version":
		case "vers":
		case "ver":
		case "v":
		    Environment.printProgramInfo();
		    code = -1;
		    break;
		default:
		    System.err.format("Unknown option: \"%1$s%2$s\"; ignoring.%n", prefix, opt);
		    break;
	    }

	    return code;
	}

	private static void display(final int lines, final int words, final int chars, final String desc) {
	    if (onlyLines)
		System.out.printf("%1$8d %2$s%n", lines, desc);
	    else if (onlyWords)
		System.out.printf("%1$8d %2$s%n", words, desc);
	    else if (onlyChars)
		System.out.printf("%1$8d %2$s%n", chars, desc);
	    else
		System.out.printf("%1$8d %2$7d %3$7d %4$s%n", lines, words, chars, desc);
	}

	private static void process(final int ichar) {
	    char ch = (char) ichar;
	    if (Character.isHighSurrogate(ch) || !Character.isLowSurrogate(ch))
		currentChars++;
	    if (ch == '\n')
		currentLines++;
	    if (Character.isWhitespace(ch)) {
		if (currentInWord)
		    currentInWord = false;
	    }
	    else {
		if (!currentInWord) {
		    currentInWord = true;
		    currentWords++;
		}
	    }
	}

	/**
	 * Get a displayable form of a file path, taking into account the {@link #regularizePaths} option.
	 *
	 * @param path	The input path.
	 * @return	The string form, possibly regularized.
	 */
	private static String getString(final Path path) {
	    if (path == null)
		return "";
	    if (regularizePaths) {
		return path.toString().replaceAll("[\\\\/]", "/");
	    }
	    else {
		return path.toString();
	    }
	}

	/**
	 * Process one file, that is, list the desired number of lines from the beginning.
	 *
	 * @param file      Name of the file to process, or {@code null} to read from {@link System#in}.
	 */
	private static void processFile(final String file) {
	    currentLines = 0;
	    currentWords = 0;
	    currentChars = 0;
	    currentInWord = false;

	    Path path = null;
	    if (file != null && !file.isEmpty()) {
		try {
		    path = Paths.get(file);
		}
		catch (InvalidPathException ex) {
		    System.err.println("Unable to find the file \"" + file + "\".");
		    return;
		}
	    }

	    try {
		CharsetDecoder decoder = cs.newDecoder();

		if (path == null || Files.size(path) > MAX_ONE_SHOT_FILE_SIZE) {
		    // Have to read the file in pieces
		    try (BufferedReader reader = (path == null) ? new BufferedReader(new InputStreamReader(System.in, cs)) :
			Files.newBufferedReader(path, cs))
		    {
			int ichar;
			while ((ichar = reader.read()) != -1) {
			    process(ichar);
			}
		    }
		}
		else {
		    // Able to read the file in completely in one shot
		    byte[] bytes = Files.readAllBytes(path);
		    ByteBuffer bb = ByteBuffer.wrap(bytes);
		    try {
			CharBuffer cb = decoder.decode(bb);
			cb.chars().forEach(ichar -> process(ichar));
		    }
		    catch (CharacterCodingException cce) {
			System.err.println("Unable to decode character: " + ExceptionUtil.toString(cce));
			return;
		    }
		}
	    }
	    catch (NoSuchFileException nsfe) {
		System.err.println("Unable to find the file \"" + file + "\".");
		return;
	    }
	    catch (IOException ioe) {
		System.err.println("I/O error: " + ExceptionUtil.toString(ioe));
		return;
	    }

	    display(currentLines, currentWords, currentChars, getString(path));

	    totalLines += currentLines;
	    totalWords += currentWords;
	    totalChars += currentChars;
	}

	/**
	 * @param args The parsed command line argument array.
	 */
	public static void main(final String[] args) {
	    Environment.loadProgramInfo(WordCount.class);

	    // In order to make this class testable, we need to reinitialize our static data
	    // every time through here, even though in normal operation it won't make any
	    // difference
	    files.clear();
	    cs = DEFAULT_CHARSET;
	    totalLines = 0;
	    totalWords = 0;
	    totalChars = 0;
	    regularizePaths = false;
	    onlyLines = false;
	    onlyWords = false;
	    onlyChars = false;

	    // Scan through the options to override the defaults
	    for (String arg : args) {
		int code = 0;

		if (arg.startsWith("--")) {
		    code = processOption("--", arg.substring(2));
		}
		else if (arg.startsWith("-")) {
		    code = processOption("-", arg.substring(1));
		}
		else if (ON_WINDOWS && arg.startsWith("/")) {
		    code = processOption("/", arg.substring(1));
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

	    // Try to read data from System.in if no files were specified
	    if (files.isEmpty()) {
		processFile(null);
	    }
	    else {
		files.forEach(f -> processFile(f));
	    }

	    if (files.size() > 1)
		display(totalLines, totalWords, totalChars, "total");
	}
}

