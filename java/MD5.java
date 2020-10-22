/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2010-2011,2015,2018-2020 Roger L. Whitcomb.
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
 * MD5 Utility Program
 *
 *  Change History:
 *   07-Jul-2010 (rlwhitcomb)
 *      First version.
 *   02-Sep-2011 (rlwhitcomb)
 *      Made into public class to be accessed from Ant.
 *   24-Aug-2015 (rlwhitcomb)
 *      Javadoc cleanup pointed out by Java 8.
 *   20-Aug-2018 (rlwhitcomb)
 *      Reflow with 4-char indents. Update some Javadoc. Allow "-" or "/" for
 *      option recognition.
 *   18-Mar-2019 (rlwhitcomb)
 *      Don't use FileInputStream due to GC problems b/c of the finalize
 *      method in these classes. No wildcard imports.
 *   06-Jun-2019 (rlwhitcomb)
 *      Allow ":" on algorithm option in addition to "=".
 *   10-Mar-2020 (rlwhitcomb)
 *	Prepare for GitHub.
 *   22-Sep-2020 (rlwhitcomb)
 *	Error message if nothing given on command line to do.
 *   21-Oct-2020 (rlwhitcomb)
 *	Allow slash in the algorithm name (for "SHA-512/256", etc.).
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Calculate MD5 (or other cryptographic) hash values for input strings or files.
 * <p> This utility is meant to be run as a stand-alone executable
 * (invoked by <tt>"java MD5"</tt>) that will compute the MD5 hash value of
 * a single string (given on the command line), a series of strings
 * (successive lines of a file), or of a complete file or files.
 * <p> A number of options are available -- see the help listing
 * (<tt>"java MD5 -?"</tt> or <tt>"java MD5 --help"</tt>) for explanations
 * (see {@link #printHelp}).
 */
public class MD5 {

	/** Flag to indicate we're processing an input file one line at a time
	 * (as opposed to hashing the whole file at once.
	 * <p> Set by the <tt>"--line"</tt> or <tt>"--file"</tt> command-line flags.
	 */
	private static boolean doLines = false;
	/** Flag to indicate we want the output as lower-case hex (e.g., "abcdef")
	 * instead of the default UPPER-CASE hex (e.g., "ABCDEF").
	 * <p> Set by the <tt>"--lower"</tt> or <tt>"--upper"</tt> command-line flags.
	 */
	private static boolean doLower = false;
	/** Flag used to indicate what the encoding of the input file should be.
	 * <p> Set by the <tt>"--utf8"</tt> or <tt>"--native"</tt> command-line flags.
	 */
	private static boolean doUTF8 = false;
	/** Flag to indicate verbose output is desired.
	 * <p> Set by the <tt>"--verbose"</tt> command-line flag.
	 */
	private static boolean verbose = false;
	/** Value set by a <tt>"--nnn"</tt> command-line flag to indicate the number of
	 * bytes of the hashed value to output.
	 */
	private static int numBytes = 0;
	/** Flag to indicate splitting the (hex) output of this process using
	 * commas (or a given separator character).
	 */
	private static boolean doSplit = false;
	/** Value to indicate which character to use to split the hex values. */
	private static char splitChar = ',';
	/** Flag to indicate putting <tt>"0x"</tt> prefix on each hash byte output
	 * (only if <tt>"--split"</tt> option is also specified).
	 * <p> Set by <tt>"--prefix"</tt> command-line option.
	 */
	private static boolean doPrefix = false;
	/** String to indicate an alternate hash algorithm for the digest (instead of MD5). */
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
	/** Pattern to parse the <tt>"--split"</tt> command-line option. */
	private static Pattern splitCmd = Pattern.compile("^[sS][pP][lL][iI][tT](\\p{Punct})?$");
	/** Pattern to parse the <tt>"--algorithm=&lt;name&gt;"</tt> command-line option. */
	private static Pattern algoName = Pattern.compile("^[aA][lL][gG][oO][rR][iI][tT][hH][mM][:=]([\\w-/]+)$");
	/** Flag to say we're running on a Windows O/S vs. some other (for command-line option recognition). */
	private static boolean onWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

	static {
	    try {
		__md = MessageDigest.getInstance("MD5");
		defaultCharset = Charset.defaultCharset();
	    } catch( Exception e ) {
		e.printStackTrace();
	    }
	}


	/**
	 * Output a small help screen.
	 */
	private static void printHelp() {
	    System.err.println("MD5 - a program to compute MD5 (or other) hash values");
	    System.err.println("-----------------------------------------------------");
	    System.err.println("Usage: java MD5 [options] [files] [values]");
	    System.err.println("Options:");
	    System.err.println("\t--lower\t\tforce hex output to lower-case");
	    System.err.println("\t--upper\t\tforce hex output to UPPER-CASE");
	    System.err.println("\t--lines\t\tprocess input files one line at a time");
	    System.err.println("\t--line\t\tsame");
	    System.err.println("\t--file\t\tcompute one hash for the entire input file");
	    System.err.println("\t--verbose\toutput verbose messages during processing");
	    System.err.println("\t--utf8\t\tprocess input files as UTF-8 encoded");
	    System.err.println("\t--UTF-8\t\tsame");
	    System.err.format("\t--native\tprocess input files using native character set: '%1$s'%n", defaultCharset.displayName());
	    System.err.println("\t--<nnn>\t\tlimit output to <nnn> bytes");
	    System.err.println("\t--split[<ch>]\tsplit the hex bytes with comma or given <ch>");
	    System.err.println("\t--prefix\tif splitting bytes, output a \"0x\" prefix on each byte");
	    System.err.println("\t--algorithm=<name>\tspecify an alternate digest algorithm (not MD5):");
	    System.err.println("\t\t\t\tMD2, SHA-1, SHA-256, SHA-384 or SHA-512");
	    System.err.println("\t--help\t\tprint this help message");
	    System.err.println("\t-? or -h\tsame");
	    System.err.println();
	    System.err.println("Note: options may be specified by \"-\", \"--\" or \"/\" (on Windows).");
	    System.err.println("Note: [files] may not contain wild-card ('?' or '*') characters");
	    System.err.println("      [values] are assumed if the string given does not match any existing file name");
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
			System.out.print("Input bytes: ");
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
			System.out.print("Input bytes: ");
			printDigest(bytes, len, 0);
		    }
		    __md.update(bytes, 0, len);
		}
		byte[] result = __md.digest();
		printDigest(result);
		fis.close();
	    }
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
		    if (s.equalsIgnoreCase("line") || s.equalsIgnoreCase("lines")) {
			doLines = true;
		    }
		    else if (s.equalsIgnoreCase("file")) {
			doLines = false;
		    }
		    else if (s.equalsIgnoreCase("lower")) {
			doLower = true;
		    }
		    else if (s.equalsIgnoreCase("upper")) {
			doLower = false;
		    }
		    else if (s.equalsIgnoreCase("utf8") || s.equalsIgnoreCase("UTF-8")) {
			doUTF8 = true;
		    }
		    else if (s.equalsIgnoreCase("native")) {
			doUTF8 = false;
		    }
		    else if (s.equalsIgnoreCase("verbose")) {
			verbose = true;
		    }
		    else if (s.equalsIgnoreCase("prefix")) {
			doPrefix = true;
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
				    System.err.format("Unrecognized option: '%1$s' -- ignored!%n", a);
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
		    System.err.format("Unrecognized digest algorithm: '%1$s'!%n%n", algorithmName);
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
	    for (String a : args) {
		if (checkOption(a) == null) {
		    didAnything = true;
		    File f = new File(a);
		    if (f.exists() && !f.isDirectory()) {
			try {
			    processFile(f);
			}
			catch (IOException ioe) {
			    System.err.format("Exception while processing file '%1$s':%n%2$s%n", f.getPath(), ioe.getMessage());
			}
		    }
		    else {
			// Just process the argument as a literal string
			__md.reset();
			if (verbose) {
			    byte[] input = a.getBytes(charset);
			    System.out.print("Input bytes: ");
			    printDigest(input, 0);
			    __md.update(input);
			}
			else
			    __md.update(a.getBytes(charset));
			byte[] bytes = __md.digest();
			printDigest(bytes);
		    }
		}
	    }
	    if (!didAnything) {
		System.err.println("No files or strings given to process!");
		System.err.println();
		printHelp();
	    }
	}

}
