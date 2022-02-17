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
 */
package info.rlwhitcomb.json;

import java.io.InputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;

import info.rlwhitcomb.util.CharUtil;
import static info.rlwhitcomb.util.Constants.*;
import info.rlwhitcomb.util.Environment;


/**
 * Main processor for JSON data files.
 */
public class JSON
{
	private static final String NEWLINE = Environment.lineSeparator();

	private static JSONBailErrorStrategy errorStrategy = new JSONBailErrorStrategy();

	private static JSONErrorListener errorListener = new JSONErrorListener();

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
		throw new JSONException(ioe, 1);
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

	private static String quote(final String input) {
	    return CharUtil.addDoubleQuotes(CharUtil.quoteControl(input));
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
	    StringBuilder buf = new StringBuilder();

	    toStringValue(obj, pretty, false, "", buf);

	    return buf.toString();
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

}

