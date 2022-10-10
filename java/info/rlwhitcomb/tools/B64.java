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
 *	Base64 encoding/decoding tool.
 *
 * History:
 *  10-Oct-22 rlw #481:	Initial implementation.
 */
package info.rlwhitcomb.tools;

import info.rlwhitcomb.Testable;
import info.rlwhitcomb.util.Constants;
import info.rlwhitcomb.util.Exceptions;
import info.rlwhitcomb.util.FileUtilities;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.Options;
import net.iharder.b64.Base64;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.*;


/**
 * Encode or decode files using Base64 encoding.
 */
public class B64
{
	/**
	 * Should we encode (raw to base64) (default)?
	 */
	private static boolean encode = true;

	/**
	 * Or should we decode (base64 back to raw)?
	 */
	private static boolean decode = false;

	/**
	 * Is the input from a file?
	 */
	private static boolean file = false;

	/**
	 * Is the input just a string on the command line?
	 */
	private static boolean string = false;

	/**
	 * The input argument (could be a string or a file name).
	 */
	private static StringBuilder input = new StringBuilder();

	/**
	 * For file input, the character set to use when reading it.
	 */
	private static Charset charset = null;

	/**
	 * The (optional) output file name.
	 */
	private static String output = null;


	/**
	 * @param args The parsed command line argument array.
	 */
	public static void main(final String[] args) {
	    boolean needOutput = false;
	    boolean needCharset = false;

	    for (String arg : args) {
		String option = Options.isOption(arg);
		if (option != null) {
		    if (Options.matchesIgnoreCase(option, "encode", "enc", "e")) {
			encode = true;
			decode = false;
		    }
		    else if (Options.matchesIgnoreCase(option, "decode", "dec", "d")) {
			decode = true;
			encode = false;
		    }
		    else if (Options.matchesIgnoreCase(option, "file", "f")) {
			file = true;
			string = false;
		    }
		    else if (Options.matchesIgnoreCase(option, "string", "str", "s")) {
			string = true;
			file = false;
		    }
		    else if (Options.matchesIgnoreCase(option, "output", "out", "o")) {
			needOutput = true;
		    }
		    else if (Options.matchesIgnoreCase(option, "help", "h", "?")) {
			Intl.printHelp("tools#base64");
			System.exit(Testable.SUCCESS);
		    }
		    else {
			Intl.errFormat("tools#base64.unknownOption", arg);
			System.exit(Testable.BAD_ARGUMENT);
		    }
		}
		else if (needOutput) {
		    if (output != null) {
			Intl.errFormat("tools#base64.onlyOneOutput");
			System.exit(Testable.BAD_ARGUMENT);
		    }
		    else {
			output = arg;
		    }
		    needOutput = false;
		}
		else if (needCharset) {
		    if (charset != null) {
			Intl.errFormat("tools#base64.onlyOneCharset");
			System.exit(Testable.BAD_ARGUMENT);
		    }
		    else {
			try {
			    charset = Charset.forName(arg);
			}
			catch (IllegalCharsetNameException | UnsupportedCharsetException ex) {
			    Intl.errFormat("tools#base64.badCharset", arg, Exceptions.toString(ex));
			    System.exit(Testable.BAD_CHARSET);
			}
		    }
		    needCharset = false;
		}
		else {
		    if (input.length() == 0) {
			input.append(arg);
		    }
		    else {
			input.append(' ');
			input.append(arg);
		    }
		}
	    }

	    if (input.length() == 0) {
		Intl.errFormat("tools#base64.noInput");
		System.exit(Testable.NO_INPUTS);
	    }

	    if (needOutput) {
		Intl.errFormat("tools#base64.missingOutput");
		System.exit(Testable.MISSING_OPTION);
	    }

	    if (needCharset) {
		Intl.errFormat("tools#base64.missingCharset");
		System.exit(Testable.MISSING_OPTION);
	    }

	    String inputValue = input.toString();
	    String result = null;
	    byte[] bytes = null;

	    if (!file && !string) {
		// If neither input source mentioned, try file first, then if not found count as string
		if (FileUtilities.canRead(new File(inputValue)))
		    file = true;
		else
		    string = true;
	    }

	    if (charset == null) {
		charset = Constants.UTF_8_CHARSET;
	    }

	    try {
		if (file) {
		    File inputFile = new File(inputValue);
		    if (FileUtilities.canRead(inputFile)) {
			if (encode) {
			    if (output == null) {
				result = Base64.encodeFromFile(inputValue);
				System.out.println(result);
			    }
			    else {
				if (!Base64.encodeFileToFile(inputValue, output)) {
				    Intl.errFormat("tools#base64.encodeFileError",
					inputValue, output, Exceptions.toString(Base64.getCaughtException()));
				    System.exit(Testable.OUTPUT_IO_ERROR);
				}
			    }
			}
			else {
			    if (output == null) {
				bytes = Base64.decodeFromFile(inputValue);
				result = new String(bytes, charset);
				System.out.println(result);
			    }
			    else {
				if (!Base64.decodeFileToFile(inputValue, output)) {
				    Intl.errFormat("tools#base64.decodeFileError",
					inputValue, output, Exceptions.toString(Base64.getCaughtException()));
				    System.exit(Testable.OUTPUT_IO_ERROR);
				}
			    }
			}
		    }
		    else {
			Intl.errFormat("tools#base64.cannotReadInput", inputValue);
			System.exit(Testable.FILE_NOT_FOUND);
		    }
		}
		else {
		    if (encode) {
			bytes = inputValue.getBytes(charset);
			result = Base64.encodeBytes(bytes);
			if (output == null) {
			    System.out.println(result);
			}
			else {
			    FileUtilities.writeStringToFile(result, new File(output), charset);
			}
		    }
		    else {
			bytes = Base64.decode(inputValue);
			if (output == null) {
			    result = new String(bytes, charset);
			    System.out.println(result);
			}
			else {
			    FileUtilities.writeStreamToFile(new ByteArrayInputStream(bytes), new File(output));
			}
		    }
		}
	    }
	    catch (IOException ioe) {
		Intl.errFormat("tools#base64.ioError", Exceptions.toString(ioe));
		System.exit(Testable.OUTPUT_IO_ERROR);
	    }
	}
}

