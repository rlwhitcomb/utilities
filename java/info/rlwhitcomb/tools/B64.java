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
 *  13-Oct-22 rlw #481: New -format option to reformat an encoded file to the
 *			76-char line length. Add "-version" command.
 *		  #481: Read from console. Fixes to do proper init for Tester.
 *  14-Oct-22 rlw #518:	Add option for "URL_SAFE" encoding and decoding.
 */
package info.rlwhitcomb.tools;

import info.rlwhitcomb.Testable;
import info.rlwhitcomb.logging.Logging;
import info.rlwhitcomb.util.Constants;
import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.Exceptions;
import info.rlwhitcomb.util.FileUtilities;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.Options;
import net.iharder.b64.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.*;


/**
 * Encode or decode files using Base64 encoding.
 */
public class B64
{
	/**
	 * The system-specific line separator.
	 */
	private static final String LF = Environment.lineSeparator();

	/**
	 * Should we encode (raw to base64) (default)?
	 */
	private static boolean encode;

	/**
	 * Or should we decode (base64 back to raw)?
	 */
	private static boolean decode;

	/**
	 * Or are we just reformatting a file?
	 */
	private static boolean format;

	/**
	 * Is the input from the console?
	 */
	private static boolean console;

	/**
	 * Is the input from a file?
	 */
	private static boolean file;

	/**
	 * Is the input just a string on the command line?
	 */
	private static boolean string;

	/**
	 * Use the "URL_SAFE" encoding / decoding.
	 */
	private static boolean urlSafe;

	/**
	 * The input argument (could be a string or a file name).
	 */
	private static StringBuilder input = new StringBuilder();

	/**
	 * For file input, the character set to use when reading it, or
	 * for file output, the character set for writing.
	 * <p> Note: Base64 encoded data is all well withing the ASCII
	 * character range, so the charset is not needed for this, only
	 * for the original data (for encoding) or the final data (for
	 * decoding) (assuming it is not binary in the first place)
	 * (and assuming that it doesn't conform to the platform default
	 * charset either).
	 */
	private static Charset charset;

	/**
	 * The (optional) output file name.
	 */
	private static String output;


	/**
	 * Reformat an already encoded string into max line length bits.
	 *
	 * @param input  The already encoded string.
	 * @param output The output file name (if null, then write to console).
	 */
	private static void writeFormatted(final String input, final String output)
		throws IOException
	{
	    String result = input.replaceAll("[\\r?\\n]", "");
	    File f = new File(output == null ? "@" : output);
	    try (PrintWriter writer = FileUtilities.getFileWriter(f, null)) {
		int endPos = 0;
		int length = result.length();
		for (int pos = 0; pos < length; pos = endPos) {
		    endPos = Math.min(pos + Base64.MAX_LINE_LENGTH, length);
		    writer.println(result.substring(pos, endPos));
		}
		writer.flush();
	    }
	}


	/**
	 * @param args The parsed command line argument array.
	 */
	public static void main(final String[] args) {
	    // Reset static variables each time (for testing)
	    encode = true;
	    decode = false;
	    format = false;

	    console = false;
	    file = false;
	    string = false;
	    urlSafe = false;

	    input.setLength(0);

	    charset = null;
	    output = null;

	    boolean needOutput = false;
	    boolean needCharset = false;

	    for (String arg : args) {
		String option = Options.isOption(arg);
		if (option != null) {
		    if (Options.matchesIgnoreCase(option, "encode", "enc", "e")) {
			encode = true;
			decode = false;
			format = false;
		    }
		    else if (Options.matchesIgnoreCase(option, "decode", "dec", "d")) {
			decode = true;
			encode = false;
			format = false;
		    }
		    else if (Options.matchesIgnoreCase(option, "format", "form", "frm")) {
			decode = false;
			encode = false;
			format = true;
		    }
		    else if (Options.matchesIgnoreCase(option, "urlsafe", "url", "u")) {
			urlSafe = true;
		    }
		    else if (Options.matchesIgnoreCase(option, "stdin", "in")) {
			console = true;
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
		    else if (Options.matchesIgnoreCase(option, "version", "vers", "ver", "v")) {
			Environment.printProgramInfo();
			System.exit(Testable.SUCCESS);
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
		else if (arg.equals("@") || arg.equals("--") || arg.equals("-")) {
		    console = true;
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

	    if (needOutput) {
		Intl.errFormat("tools#base64.missingOutput");
		System.exit(Testable.MISSING_OPTION);
	    }

	    if (needCharset) {
		Intl.errFormat("tools#base64.missingCharset");
		System.exit(Testable.MISSING_OPTION);
	    }

	    if (charset == null) {
		charset = Constants.UTF_8_CHARSET;
	    }

	    String result = null;
	    byte[] bytes = null;

	    if (console) {
		try {
		    Console console = System.console();
		    if (System.in.available() > 0) {
			if (encode) {
			    // The input could be redirected from a binary file, so read bytes, not chars
			    ByteArrayOutputStream baos = new ByteArrayOutputStream(Constants.PROCESS_BUFFER_SIZE);
			    byte[] inputBuf = new byte[Constants.PROCESS_BUFFER_SIZE];
			    int ret = 0;
			    while ((ret = System.in.read(inputBuf)) > 0) {
				baos.write(inputBuf, 0, ret);
			    }
			    bytes = baos.toByteArray();
			}
			else {
			    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, charset));
			    while ((result = reader.readLine()) != null) {
				input.append(result).append(LF);
			    }
			}
		    }
		    else if (console != null) {
			while ((result = console.readLine()) != null) {
			    input.append(result).append(LF);
			}
		    }
		    else {
			Intl.errPrintln("tools#base64.noConsole");
			System.exit(Testable.BAD_ARGUMENT);
		    }
		}
		catch (IOException ioe) {
		    Intl.errFormat("tools#base64.consoleIOError", Exceptions.toString(ioe));
		    System.exit(Testable.INPUT_IO_ERROR);
		}
	    }

	    if (input.length() == 0 && bytes == null) {
		Intl.errFormat("tools#base64.noInput");
		System.exit(Testable.NO_INPUTS);
	    }

	    String inputValue = input.toString();

	    if (console) {
		string = true;
		file = false;
	    }
	    else if (!file && !string) {
		// If neither input source mentioned, try file first, then if not found count as string
		if (FileUtilities.canRead(new File(inputValue)))
		    file = true;
		else
		    string = true;
	    }

	    int options = urlSafe ? Base64.URL_SAFE : Base64.NO_OPTIONS;

	    try {
		if (file) {
		    File inputFile = new File(inputValue);
		    if (FileUtilities.canRead(inputFile)) {
			if (encode) {
			    if (output == null) {
				result = Base64.encodeFromFile(inputValue, options);
				if (result == null) {
				    Intl.errFormat("tools#base64.encodeError", inputValue);
				    System.exit(Testable.OUTPUT_IO_ERROR);
				}
				System.out.println(result);
			    }
			    else {
				if (!Base64.encodeFileToFile(inputValue, output, options)) {
				    Intl.errFormat("tools#base64.encodeFileError",
					inputValue, output, Exceptions.toString(Base64.getCaughtException()));
				    System.exit(Testable.OUTPUT_IO_ERROR);
				}
			    }
			}
			else if (decode) {
			    if (output == null) {
				bytes = Base64.decodeFromFile(inputValue, options);
				if (bytes == null) {
				    Intl.errFormat("tools#base64.decodeError", inputValue);
				    System.exit(Testable.OUTPUT_IO_ERROR);
				}
				result = new String(bytes, charset);
				System.out.println(result);
			    }
			    else {
				if (!Base64.decodeFileToFile(inputValue, output, options)) {
				    Intl.errFormat("tools#base64.decodeFileError",
					inputValue, output, Exceptions.toString(Base64.getCaughtException()));
				    System.exit(Testable.OUTPUT_IO_ERROR);
				}
			    }
			}
			else /* format */ {
			    writeFormatted(FileUtilities.readFileAsString(inputFile), output);
			}
		    }
		    else {
			Intl.errFormat("tools#base64.cannotReadInput", inputValue);
			System.exit(Testable.FILE_NOT_FOUND);
		    }
		}
		else /* string */ {
		    if (encode) {
			if (bytes == null) {
			    bytes = inputValue.getBytes(charset);
			}
			result = Base64.encodeBytes(bytes, options);
			if (output == null) {
			    System.out.println(result);
			}
			else {
			    FileUtilities.writeStringToFile(result, new File(output), charset);
			}
		    }
		    else if (decode) {
			bytes = Base64.decode(inputValue, options);
			if (output == null) {
			    result = new String(bytes, charset);
			    System.out.println(result);
			}
			else {
			    FileUtilities.writeStreamToFile(new ByteArrayInputStream(bytes), new File(output));
			}
		    }
		    else /* format */ {
			writeFormatted(inputValue, output);
		    }
		}
	    }
	    catch (IOException ioe) {
		Intl.errFormat("tools#base64.ioError", Exceptions.toString(ioe));
		System.exit(Testable.OUTPUT_IO_ERROR);
	    }
	}
}

