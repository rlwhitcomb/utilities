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
 *	Main processor for JSON data files.
 *
 *  History:
 *      16-Feb-2022 (rlwhitcomb)
 *	    #196: Initial coding.
 *	    Add option to "toStringValue" to switch line endings.
 *	23-Mar-2022 (rlwhitcomb)
 *	    New "writeObject" methods.
 *	08-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 */
package info.rlwhitcomb.json;

import info.rlwhitcomb.util.CharUtil;
import info.rlwhitcomb.util.Environment;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static info.rlwhitcomb.util.Constants.*;


/**
 * Main processor for JSON data files.
 */
public class JSON
{
	/**
	 * The current environment's line separator.
	 */
	private static final String NEWLINE = Environment.lineSeparator();

	/**
	 * Custom error strategy for the generated parser.
	 */
	private static JSONBailErrorStrategy errorStrategy = new JSONBailErrorStrategy();

	/**
	 * Custom error listener for the generated parser.
	 */
	private static JSONErrorListener errorListener = new JSONErrorListener();

	/**
	 * The generated parser visitor.
	 */
	private static JSONObjectVisitor visitor = new JSONObjectVisitor();


	/**
	 * Private constructor since this is a static class.
	 */
	private JSON() {
	}


	/**
	 * Read a JSON stream and turn it into regular Java objects using the default (UTF-8) charset.
	 *
	 * @param inputStream The stream to read the object(s) from.
	 * @return            {@code List}s and {@code Map}s parsed from the input.
	 * @throws JSONException if there are errors reading the stream or parsing the JSON syntax.
	 */
	public static Object readObject(final InputStream inputStream) {
	    return readObject(inputStream, UTF_8_CHARSET);
	}

	/**
	 * Read a JSON stream and turn it into regular Java objects.
	 *
	 * @param inputStream The stream to read the object from.
	 * @param charset     Charset used to interpret the bytes as characters.
	 * @return            {@code List}s and {@code Map}s parsed from the input.
	 * @throws JSONException if there are errors reading the stream or parsing the JSON syntax.
	 */
	public static Object readObject(final InputStream inputStream, final Charset charset)
		throws JSONException
	{
	    try {
		CharStream input = CharStreams.fromStream(inputStream, charset);

		return parseStream(input);
	    }
	    catch (IOException ioe) {
		throw new JSONException(ioe);
	    }
	}

	/**
	 * Read a JSON-formatted string and turn it into regular Java objects.
	 *
	 * @param input The input string to parse.
	 * @return      The {@code List}s and {@code Map}s parsed from the input.
	 * @throws JSONException if there were parsing errors.
	 */
	public static Object readString(final String input) {
	    return parseStream(CharStreams.fromString(input));
	}

	/**
	 * Read a {@link CharStream} and turn it into regular Java objects.
	 *
	 * @param stream The character stream to read from.
	 * @return       The parsed JSON object.
	 * @throws JSONException if there were any parse errors.
	 */
	private static Object parseStream(final CharStream stream)
		throws JSONException
	{
		JSONLexer lexer = new JSONBailLexer(stream);
		lexer.removeErrorListeners();
		lexer.addErrorListener(errorListener);

		CommonTokenStream tokens = new CommonTokenStream(lexer);

		JSONParser parser = new JSONParser(tokens);
		parser.setErrorHandler(errorStrategy);
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);

		ParseTree tree = parser.json();

		return visitor.visit(tree);
	}


	/**
	 * Process and quote a string to make it JSON-compatible for output.
	 *
	 * @param input The internal string to process.
	 * @return      Properly escaped and quoted string, suitable for output.
	 */
	private static String quote(final String input) {
	    return CharUtil.addDoubleQuotes(CharUtil.quoteControl(input));
	}

	/**
	 * Convert an arbitrary object to JSON-compatible format for writing. For this to work, the given
	 * object should have only Java-native {@link List} or {@link Map} objects internally to represent
	 * JSON arrays or objects. Otherwise, values will be represented by their {@link Object#toString}
	 * representation.
	 *
	 * @param obj      Input object to convert.
	 * @param pretty   Whether or not to use "pretty" printing in the output, that is, line separators
	 *                 for new objects, and indentation to successive levels.
	 * @param newlines Whether or not to use just {@code '\n'} for line separators (only applicable for
	 *                 "pretty" printing). Use {@code false} to use the current environment's separators.
	 * @param indent   The cumulative indent to use for pretty printing (should be {@code ""} for the
	 *                 outermost level, usually).
	 * @param buf      The buffer to use as the destination for the output.
	 */
	private static void toStringValue(final Object obj, final boolean pretty, final boolean newlines, final String indent, final StringBuilder buf) {
	    if (obj instanceof List) {
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) obj;
		toStringValue(list, pretty, newlines, indent, buf);
	    }
	    else if (obj instanceof Map) {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) obj;
		toStringValue(map, pretty, newlines, indent, buf);
	    }
	    else if (obj instanceof BigDecimal) {
		buf.append(((BigDecimal) obj).toPlainString());
	    }
	    else if (obj instanceof BigInteger) {
		buf.append(((BigInteger) obj).toString());
	    }
	    else if (obj instanceof String) {
		String stringValue = (String) obj;
		buf.append(quote(stringValue));
	    }
	    else if (obj == null) {
		buf.append("null");
	    }
	    else {
		buf.append(obj.toString());
	    }
	}

	private static void toStringValue(final List<Object> list, final boolean pretty, final boolean newlines, final String indent, final StringBuilder buf) {
	    String nextIndent = indent + "  ";
	    if (list.size() > 0) {
		boolean comma = false;
		String newLine = newlines ? "\n" : NEWLINE;

		if (pretty)
		    buf.append("[").append(newLine);
		else
		    buf.append("[ ");

		for (Object value : list) {
		    if (comma) {
			if (pretty)
			    buf.append(",").append(newLine);
			else
			    buf.append(", ");
		    }
		    else {
			comma = true;
		    }
		    if (pretty)
			buf.append(nextIndent);

		    toStringValue(value, pretty, newlines, nextIndent, buf);
		}
		if (pretty)
		    buf.append(newLine).append(indent).append("]");
		else
		    buf.append(" ]");
	    }
	    else {
		buf.append("[ ]");
	    }
	}

	private static void toStringValue(final Map<String, Object> map, final boolean pretty, final boolean newlines, final String indent, final StringBuilder buf) {
	    String nextIndent = indent + "  ";
	    if (map.size() > 0) {
		boolean comma = false;
		String newLine = newlines ? "\n" : NEWLINE;
		if (pretty)
		    buf.append("{").append(newLine);
		else
		    buf.append("{ ");
		for (Map.Entry<String, Object> entry : map.entrySet()) {
		    if (comma) {
			if (pretty)
			    buf.append(",").append(newLine);
			else
			    buf.append(", ");
		    }
		    else {
			comma = true;
		    }
		    if (pretty)
			buf.append(nextIndent);

		    buf.append(quote(entry.getKey())).append(": ");
		    toStringValue(entry.getValue(), pretty, newlines, nextIndent, buf);
		}
		if (pretty)
		    buf.append(newLine).append(indent).append("}");
		else
		    buf.append(" }");
	    }
	    else {
		buf.append("{ }");
	    }
	}

	/**
	 * Turn an object into a String for display, using platform-specific line separator.
	 *
	 * @param obj    The object to stringify.
	 * @param pretty Whether to do indenting, etc.
	 * @return       The string value of this object.
	 */
	public static String toStringValue(final Object obj, final boolean pretty) {
	    return toStringValue(obj, pretty, false);
	}

	/**
	 * Turn an object into a String for display with option for newlines.
	 *
	 * @param obj      The object to stringify.
	 * @param pretty   Whether to do indenting, etc.
	 * @param newlines {@code true} to use just {@code '\n'} for line separators,
	 *                 or {@code false} to use the platform-specific separator
	 *                 (only applies for pretty-printing).
	 * @return         The string value of this object.
	 */
	public static String toStringValue(final Object obj, final boolean pretty, final boolean newlines) {
	    StringBuilder buf = new StringBuilder();

	    toStringValue(obj, pretty, newlines, "", buf);

	    return buf.toString();
	}

	/**
	 * Write an object to the given output stream using the UTF-8 character set. Output will be "raw"
	 * JSON format (that is, with no extra line separators or indentation).
	 *
	 * @param os     The output stream to write the object to.
	 * @param obj    Object to turn into a JSON string to write to the stream.
	 * @throws JSONException if there was a problem writing to the stream
	 */
	public static void writeObject(final OutputStream os, final Object obj)
		throws JSONException
	{
	    writeObject(os, UTF_8_CHARSET, obj, false, false);
	}

	/**
	 * Write an object to the given output stream using the UTF-8 character set and the platform-default
	 * line separator (only applies when pretty printing).
	 *
	 * @param os     The output stream to write the object to.
	 * @param obj    Object to turn into a JSON string to write to the stream.
	 * @param pretty Whether to do indenting, etc.
	 * @throws JSONException if there was a problem writing to the stream
	 */
	public static void writeObject(final OutputStream os, final Object obj, final boolean pretty)
		throws JSONException
	{
	    writeObject(os, UTF_8_CHARSET, obj, pretty, false);
	}

	/**
	 * Write an object to the given output stream using the given character set to
	 * encode the result. The object will be converted to a string in JSON format, then the
	 * resulting string will be converted to bytes according to the given charset before writing.
	 *
	 * @param os       The output stream to write the object to.
	 * @param cs       Charset to use to convert the output to bytes for writing to the stream.
	 * @param obj      Object to turn into a JSON string to write to the stream.
	 * @param pretty   Whether to do indenting, etc.
	 * @param newlines {@code true} to use just {@code '\n'} for line separators,
	 *                 or {@code false} to use the platform-specific separator
	 *                 (only applies for pretty-printing).
	 * @throws JSONException if there was a problem writing to the stream
	 */
	public static void writeObject(final OutputStream os, final Charset cs, final Object obj,
		final boolean pretty, final boolean newlines) throws JSONException
	{
	    String value = toStringValue(obj, pretty, newlines);
	    try {
		os.write(value.getBytes(cs));
	    }
	    catch (IOException ioe) {
		throw new JSONException(ioe);
	    }
	}

}

