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
	private static Charset cs = DEFAULT_CHARSET;
	/** Whether we are operating in a Windows environment. */
	private static final boolean ON_WINDOWS = Environment.isWindows();
	/** The list of files to process. */
	private static final List<String> files = new ArrayList<>();

	/** The line count for the current file. */
	private static int currentLines;
	/** The word count for the current file. */
	private static int currentWords;
	/** The character count for the current file. */
	private static int currentChars;

	/** Whether we are in the middle of a word right now. */
	private static boolean currentInWord;

	/** The total line count for all files. */
	private static int totalLines = 0;
	/** The total word count for all files. */
	private static int totalWords = 0;
	/** The total character count for all files. */
	private static int totalChars = 0;


	/**
	 * Process one command line option.
	 *
	 * @param prefix The leading character(s) stripped before the "opt" value.
	 * @param opt    The option string, without the leading prefix.
	 */
	private static void processOption(final String prefix, final String opt) {
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
		    System.err.format("Unknown option: \"%1$s%2$s\"; ignoring.", prefix, opt);
		    break;
	    }
	}

	private static void display(final int lines, final int words, final int chars, final String desc) {
	    System.out.printf("%1$8d%2$8d%3$8d %4$s%n", lines, words, chars, desc);
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

	    display(currentLines, currentWords, currentChars, path == null ? "" : path.toString());

	    totalLines += currentLines;
	    totalWords += currentWords;
	    totalChars += currentChars;
	}

	/**
	 * @param args The parsed command line argument array.
	 */
	public static void main(final String[] args) {
	    // Scan through the options to override the defaults
	    for (String arg : args) {
		if (arg.startsWith("--")) {
		    processOption("--", arg.substring(2));
		}
		else if (arg.startsWith("-")) {
		    processOption("-", arg.substring(1));
		}
		else if (ON_WINDOWS && arg.startsWith("/")) {
		    processOption("/", arg.substring(1));
		}
		else {
		    files.add(arg);
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

