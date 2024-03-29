/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2023 Roger L. Whitcomb.
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
 *	Deal with UUIDs.
 *
 * History:
 *  09-Nov-21 rlw ----	Initial first-pass implementation to just generate random UUIDs.
 *  11-Nov-21 rlw #80	Add "-lower", "-upper", "-string", "-bytes" options, as well as "-nn".
 *  12-Nov-21 rlw #80	Add "-int" option.
 *  21-Jan-22 rlw #217	Allow environment options through new Options method.
 *  08-Feb-22 rlw ----	Move text to resources.
 *  09-Jul-22 rlw #393	Cleanup imports.
 *  02-Nov-23 rlw #633	Allow "-opt" and "-noopt" to control processing of default
 *			options in the environment.
 */
package info.rlwhitcomb.tools;

import info.rlwhitcomb.util.CharUtil;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.Options;

import java.math.BigInteger;
import java.util.UUID;


/**
 * Generate a random UUID.
 *
 * <p> TODO: much more to come.
 */
public class Uuid
{
	/** Option to format as a string (default). */
	private static boolean toString = true;

	/** Option to format as an array of bytes. */
	private static boolean toBytes = false;

	/** Option to format as a long, long integer. */
	private static boolean toInteger = false;

	/** Option to make the result uppercase (default for bytes). */
	private static boolean toUpper = false;

	/** Option to make the result lowercase (default for string). */
	private static boolean toLower = false;

	/** Whether or not we have seen one of the casing options. */
	private static boolean seenCaseOption = false;

	/** Number of iterations, default one. */
	private static int numberOfValues = 1;


	/**
	 * Display a message about the usage of this program.
	 */
	private static void usage() {
	    Intl.printHelp("tools#uuid");
	}

	/**
	 * Convert a long value to its constituent bytes (network byte order: MSB first).
	 *
	 * @param msb	The input most-significant long value.
	 * @param lsb	The input least-significant long value.
	 * @return	The MSB-first array of bytes of the two long values.
	 */
	private static byte[] longLongToBytes(final long msb, final long lsb) {
	    byte[] result = new byte[16];
	    long shiftedValue = 0L;
	    for (int i = 0; i < 16; i++) {
		if (i == 0)
		    shiftedValue = msb;
		else if (i == 8)
		    shiftedValue = lsb;

		shiftedValue = Long.rotateLeft(shiftedValue, 8);
		byte b = (byte) (shiftedValue & 0xFF);
		result[i] = b;
	    }
	    return result;
	}

	private static boolean processOptions(final String[] options) {
	    for (String opt : options) {
		String value = Options.isOption(opt);
		if (value != null) {
		    if (Options.matchesIgnoreCase(value, "lowercase", "lower", "low", "l")) {
			toUpper = false;
			toLower = true;
			seenCaseOption = true;
		    }
		    else if (Options.matchesIgnoreCase(value, "uppercase", "upper", "up", "u")) {
			toUpper = true;
			toLower = false;
			seenCaseOption = true;
		    }
		    else if (Options.matchesIgnoreCase(value, "string", "str", "s")) {
			toBytes = false;
			toString = true;
			toInteger = false;
			// Strings default to lower case
			if (!seenCaseOption) {
			    toUpper = false;
			    toLower = true;
			}
		    }
		    else if (Options.matchesIgnoreCase(value, "bytes", "byte", "by", "b")) {
			toBytes = true;
			toString = false;
			toInteger = false;
			// bytes default to upper case
			if (!seenCaseOption) {
			    toUpper = true;
			    toLower = false;
			}
		    }
		    else if (Options.matchesIgnoreCase(value, "integer", "int", "i")) {
			toBytes = false;
			toString = false;
			toInteger = true;
		    }
		    else if (Options.matchesIgnoreCase(value, "help", "h", "?")) {
			usage();
			return false;
		    }
		    else if (Options.checkOptionOption(opt) == Options.OptionChoice.NONE) {
			try {
			    int nn = Integer.parseInt(value);
			    if (nn < 1 || nn > 99) {
				Intl.errPrintln("tools#uuid.invalidNumber");
				usage();
				return false;
			    }
			    numberOfValues = nn;
			}
			catch (NumberFormatException nfe) {
			    Intl.errFormat("tools#uuid.unrecognizedOption", opt);
			    usage();
			    return false;
			}
		    }
		}
		else {
		    Intl.errFormat("tools#uuid.unrecognizedArg", opt);
		    usage();
		    return false;
		}
	    }
	    return true;
	}

	/**
	 * @param args The parsed command line argument array.
	 */
	public static void main(String[] args) {

	    if (Options.allowEnvironmentOptions(args, false)) {
		Options.processEnvironmentOptions(Uuid.class, options -> {
		    if (!processOptions(options))
			System.exit(0);
		});
	    }

	    if (!processOptions(args))
		return;

	    for (int i = 0; i < numberOfValues; i++) {
		UUID uuid = UUID.randomUUID();
		if (toString) {
		    String result = uuid.toString();
		    if (toUpper)
			System.out.println(result.toUpperCase());
		    else
			System.out.println(result); // is lowercase by default
		}
		else if (toBytes) {
		    byte[] bytes = longLongToBytes(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
		    String result = CharUtil.toHexArrayForm(bytes);
		    if (toLower)
			System.out.println(result.toLowerCase());
		    else
			System.out.println(result); // is uppercase by default
		}
		else {
		    byte[] bytes = longLongToBytes(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
		    BigInteger iValue = new BigInteger(1, bytes);
		    System.out.println(iValue.toString());
		}
	    }
	}
}

