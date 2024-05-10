/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2010-2011,2015,2018-2022,2024 Roger L. Whitcomb.
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
 *	Hash Utility Program
 *
 *  Change History:
 *	07-Jul-2010 (rlwhitcomb)
 *	    First version.
 *	02-Sep-2011 (rlwhitcomb)
 *	    Made into public class to be accessed from Ant.
 *	24-Aug-2015 (rlwhitcomb)
 *	    Javadoc cleanup pointed out by Java 8.
 *	20-Aug-2018 (rlwhitcomb)
 *	    Reflow with 4-char indents. Update some Javadoc. Allow "-" or "/" for
 *	    option recognition.
 *	18-Mar-2019 (rlwhitcomb)
 *	    Don't use FileInputStream due to GC problems b/c of the finalize
 *	    method in these classes. No wildcard imports.
 *	06-Jun-2019 (rlwhitcomb)
 *	    Allow ":" on algorithm option in addition to "=".
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	22-Sep-2020 (rlwhitcomb)
 *	    Error message if nothing given on command line to do.
 *	21-Oct-2020 (rlwhitcomb)
 *	    Allow slash in the algorithm name (for "SHA-512/256", etc.).
 *	21-Dec-2020 (rlwhitcomb)
 *	    Update obsolete Javadoc constructs.
 *	29-Mar-2021 (rlwhitcomb)
 *	    Move to new package; reformat a little.
 *	20-Nov-2021 (rlwhitcomb)
 *	    #90: Rename from MD5 to Hash; make default SHA-256.
 *	30-Jan-2022 (rlwhitcomb)
 *	    #43: Allow "-lines" to combine all command line into one line string.
 *	18-Feb-2022 (rlwhitcomb)
 *	    Use Exceptions for exception messages.
 *	09-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 *	10-May-2024 (rlwhitcomb)
 *	    #671: Move all text into resource file. Add version printout.
 *	    More aliases for command-line options.
 */
package info.rlwhitcomb.tools;

import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.Exceptions;
import info.rlwhitcomb.util.Intl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Calculate cryptographic hash values for input strings or files.
 * <p> This utility is meant to be run as a stand-alone executable
 * (invoked by <code>"java Hash"</code>) that will compute the hash value of
 * a single string (given on the command line), a series of strings
 * (successive lines of a file), or of a complete file or files.
 * <p> A number of options are available -- see the help listing
 * (<code>"java Hash -?"</code> or <code>"java Hash --help"</code>) for explanations
 * (see {@link #printHelp}).
 */
public class Hash {

	/** Flag to indicate we're processing an input file one line at a time
	 * (as opposed to hashing the whole file at once.
	 * <p> Set by the <code>"--line"</code> or <code>"--file"</code> command-line flags.
	 */
	private static boolean doLines = false;
	/** Flag to indicate we want the output as lower-case hex (e.g., "abcdef")
	 * instead of the default UPPER-CASE hex (e.g., "ABCDEF").
	 * <p> Set by the <code>"--lower"</code> or <code>"--upper"</code> command-line flags.
	 */
	private static boolean doLower = false;
	/** Flag used to indicate what the encoding of the input file should be.
	 * <p> Set by the <code>"--utf8"</code> or <code>"--native"</code> command-line flags.
	 */
	private static boolean doUTF8 = false;
	/** Flag to indicate verbose output is desired.
	 * <p> Set by the <code>"--verbose"</code> command-line flag.
	 */
	private static boolean verbose = false;
	/** Value set by a <code>"--nnn"</code> command-line flag to indicate the number of
	 * bytes of the hashed value to output.
	 */
	private static int numBytes = 0;
	/** Flag to indicate splitting the (hex) output of this process using
	 * commas (or a given separator character).
	 */
	private static boolean doSplit = false;
	/** Value to indicate which character to use to split the hex values. */
	private static char splitChar = ',';
	/** Flag to indicate putting <code>"0x"</code> prefix on each hash byte output
	 * (only if <code>"--split"</code> option is also specified).
	 * <p> Set by <code>"--prefix"</code> command-line option.
	 */
	private static boolean doPrefix = false;
	/** String to indicate an alternate hash algorithm for the digest (instead of SHA-256). */
	private static String algorithmName = null;
	/** {@link MessageDigest} used to do the hashing of the input file(s). */
	private static MessageDigest __md = null;
	/** {@link Charset} used to do decoding of the input file(s). */
	private static Charset charset = null;
	/** Default {@link Charset} for this environment. */
	private static Charset defaultCharset = null;
	/** Digit array used to output the final hashed value. */
	private static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	/** Hex character array used for lower-case encoding of the output. */
	private static char hexLowerDigits[] = { 'a', 'b', 'c', 'd', 'e', 'f' };
	/** Hex character array used for UPPER-CASE encoding of the output. */
	private static char hexUpperDigits[] = { 'A', 'B', 'C', 'D', 'E', 'F' };
	/** Pattern to parse the <code>"--split"</code> command-line option. */
	private static Pattern splitCmd = Pattern.compile("^[sS][pP][lL][iI][tT](\\p{Punct})?$");
	/** Pattern to parse the <code>"--algorithm=&lt;name&gt;"</code> command-line option. */
	private static Pattern algoName = Pattern.compile("^[aA][lL][gG][oO][rR][iI][tT][hH][mM][:=]([\\w-/]+)$");
	/** Flag to say we're running on a Windows O/S vs. some other (for command-line option recognition). */
	private static boolean onWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

	static {
	    try {
		__md = MessageDigest.getInstance("SHA-256");
		defaultCharset = Charset.defaultCharset();
	    } catch( Exception e ) {
		e.printStackTrace();
	    }
	}


	/**
	 * Output a small help screen.
	 */
	private static void printHelp() {
	    Map<String, String> values = new HashMap<>();
	    values.put("CHARSET", defaultCharset.displayName());

	    Intl.printHelp("tools#hash", values);
	}


	/**
	 * Output one hex lower or UPPER case digit to {@link System#out}.
	 * @param dig The digit value to print (0-15)
	 */
	private static void printHexDigit(int dig) {
	    if (dig > 9) {
		if (doLower)
		    System.out.print(hexLowerDigits[dig - 10]);
		else
		    System.out.print(hexUpperDigits[dig - 10]);
	    }
	    else
		System.out.print(hexDigits[dig]);
	}


	/**
	 * Output one hex byte (2 digits) to {@link System#out}.
	 * @param val The byte value to print (0-255)
	 */
	private static void printHex(int val) {
	    printHexDigit(val / 16);
	    printHexDigit(val % 16);
	}


	/**
	 * Output the final digest value (given by an array of bytes).
	 * @param bytes The final digest value to print.
	 */
	private static void printDigest(byte[] bytes) {
	    printDigest(bytes, bytes.length, numBytes);
	}


	/**
	 * Output the final digest value (given by an array of bytes).
	 * @param bytes The final digest value to print.
	 * @param maxNum The maximum number of bytes to print (different
	 * than the size given on the command line)
	 */
	private static void printDigest(byte[] bytes, int maxNum) {
	    printDigest(bytes, bytes.length, maxNum);
	}


	/**
	 * Output a digest value given by a given number of bytes
	 * from a byte array.
	 * @param bytes The digest value to print.
	 * @param len The number of bytes from the array to print.
	 * @param maxNum The (perhaps different) maximum number to print
	 * (0 means use the len value).
	 */
	private static void printDigest(byte[] bytes, int len, int maxNum) {
	    if (maxNum != 0)
		len = Math.min(maxNum, len);
	    for (int i = 0; i < len; i++) {
		byte b = bytes[i];
		if (doPrefix && doSplit)
		    System.out.print("0x");
		if (b < 0)
		    printHex((int)b + 256);
		else
		    printHex((int)b);
		if (doSplit && i < len - 1)
		    System.out.format("%1$c", splitChar);
	    }
	    System.out.println();
	}


	/**
	 * Do the processing of one input file.
	 * @param f The input file to process.
	 * @throws IOException from the underlying I/O methods
	 */
	private static void processFile(File f)
		throws IOException
	{
	    // Drastic difference if processing lines or the whole file
	    if (doLines) {
		BufferedReader rdr = Files.newBufferedReader(f.toPath(), charset);
		String line = null;
		while ((line = rdr.readLine()) != null) {
		    __md.reset();
		    if (verbose) {
			byte[] input = line.getBytes(charset);
			System.out.print(Intl.getString("tools#hash.inputBytes"));
			printDigest(input, 0);
			__md.update(input);
		    }
		    else
			__md.update(line.getBytes(charset));
		    byte[] bytes = __md.digest();
		    printDigest(bytes);
		}
		rdr.close();
	    }
	    else {
		__md.reset();
		InputStream fis = Files.newInputStream(f.toPath());
		byte[] bytes = new byte[4096];
		int len;
		while ((len = fis.read(bytes)) != -1) {
		    if (verbose) {
			System.out.print(Intl.getString("tools#hash.inputBytes"));
			printDigest(bytes, len, 0);
		    }
		    __md.update(bytes, 0, len);
		}
		byte[] result = __md.digest();
		printDigest(result);
		fis.close();
	    }
	}

	private static void processString(String line) {
	    __md.reset();
	    if (verbose) {
		byte[] input = line.getBytes(charset);
		System.out.print(Intl.getString("tools#hash.inputBytes"));
		printDigest(input, 0);
		__md.update(input);
	    }
	    else {
		__md.update(line.getBytes(charset));
	    }
	    byte[] bytes = __md.digest();
	    printDigest(bytes);
	}

	private static String checkOption(String arg) {
	    if (arg.startsWith("--"))
		return arg.substring(2);
	    else if (arg.startsWith("-"))
		return arg.substring(1);
	    else if (onWindows && arg.startsWith("/"))
		return arg.substring(1);
	    return null;
	}


	/**
	 * Main program -- process command-line flags and then process
	 * all files given on the command line.
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
	    // Scan for feature flags in the arguments
	    for (String a : args) {
		String s;
		if ((s = checkOption(a)) != null) {
		    if (s.equalsIgnoreCase("lines") || s.equalsIgnoreCase("line") || s.equalsIgnoreCase("l")) {
			doLines = true;
		    }
		    else if (s.equalsIgnoreCase("file") || s.equalsIgnoreCase("f")) {
			doLines = false;
		    }
		    else if (s.equalsIgnoreCase("lower") || s.equalsIgnoreCase("low")) {
			doLower = true;
		    }
		    else if (s.equalsIgnoreCase("upper") || s.equalsIgnoreCase("up")) {
			doLower = false;
		    }
		    else if (s.equalsIgnoreCase("utf8") || s.equalsIgnoreCase("UTF-8")) {
			doUTF8 = true;
		    }
		    else if (s.equalsIgnoreCase("native") || s.equalsIgnoreCase("n")) {
			doUTF8 = false;
		    }
		    else if (s.equalsIgnoreCase("verbose") || s.equalsIgnoreCase("v")) {
			verbose = true;
		    }
		    else if (s.equalsIgnoreCase("prefix") || s.equalsIgnoreCase("pre")) {
			doPrefix = true;
		    }
		    else if (s.equalsIgnoreCase("version") || s.equalsIgnoreCase("vers") || s.equalsIgnoreCase("ver")) {
			Environment.printProgramInfo();
			return;
		    }
		    else if (s.equalsIgnoreCase("help") || s.equalsIgnoreCase("h") || s.equals("?")) {
			printHelp();
			return;
		    }
		    else {
			Matcher m = splitCmd.matcher(s);
			if (m.matches()) {
			    String ch = m.group(1);
			    if (ch != null) {
				splitChar = ch.charAt(0);
			    }
			    doSplit = true;
			}
			else {
			    Matcher m2 = algoName.matcher(s);
			    if (m2.matches()) {
				algorithmName = m2.group(1);
			    }
			    else {
				try {
				    numBytes = Integer.parseInt(s);
				}
				catch (NumberFormatException nfe) {
				    Intl.errFormat("tools#hash.errBadOption", a);
				}
			    }
			}
		    }
		}
	    }

	    if (algorithmName != null) {
		try {
		    __md = MessageDigest.getInstance(algorithmName);
		}
		catch (NoSuchAlgorithmException nsae) {
		    Intl.errFormat("tools#hash.badDigest", algorithmName);
		    printHelp();
		    return;
		}
	    }

	    if (doUTF8)
		charset = Charset.forName("UTF-8");
	    else
		charset = defaultCharset;

	    // Now scan for the file name argument(s) (if any)
	    boolean didAnything = false;
	    StringBuilder line = new StringBuilder();

	    for (String a : args) {
		if (checkOption(a) == null) {
		    didAnything = true;
		    File f = new File(a);
		    if (f.exists() && !f.isDirectory()) {
			try {
			    processFile(f);
			}
			catch (IOException ioe) {
			    Intl.errFormat("tools#hash.errException", f.getPath(), Exceptions.toString(ioe));
			}
		    }
		    else if (doLines) {
			if (line.length() > 0)
			    line.append(' ');
			line.append(a);
		    }
		    else {
			didAnything = true;
			// Just process the argument as a literal string
			processString(a);
		    }
		}
	    }

	    if (line.length() > 0) {
		didAnything = true;
		processString(line.toString());
	    }

	    if (!didAnything) {
		Intl.errPrintln("tools#hash.errNoFiles");
		Intl.errPrintln();
		printHelp();
	    }
	}

}
