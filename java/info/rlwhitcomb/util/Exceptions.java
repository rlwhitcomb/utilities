/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013-2018,2020-2022 Roger L. Whitcomb.
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
 *	Utility class to make handling exceptions easier.
 *
 *  History:
 *	23-Jul-2013 (rlwhitcomb)
 *	    Created.
 *	20-Feb-2014 (rlwhitcomb)
 *	    Add ability to substitute spaces for tabs.
 *	24-Apr-2015 (rlwhitcomb)
 *	    More special case handling for certain system exceptions
 *	    that have a message that is less than helpful.
 *	13-May-2015 (rlwhitcomb)
 *	    Special handling for all cases of NullPointerException
 *	    (always use the simple name along with the message even
 *	    if the message is not null).
 *	07-Jan-2016 (rlwhitcomb)
 *	    Fix Javadoc warnings from Java 8.
 *	29-May-2017 (rlwhitcomb)
 *	    Add CharacterCodingException to the list of those for which
 *	    we use the simple name as part of the message.
 *	07-May-2018 (rlwhitcomb)
 *	    Some more exceptions that needs name + message.
 *	29-Aug-2018 (rlwhitcomb)
 *	    Another exception that needs name + message.
 *	31-Aug-2018 (rlwhitcomb)
 *	    And yet another ...
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	21-Dec-2020 (rlwhitcomb)
 *	    Update obsolete Javadoc constructs.
 *	22-Jan-2021 (rlwhitcomb)
 *	    One more exception that needs the name and message.
 *	05-Mar-2021 (rlwhitcomb)
 *	    And another one. Tweak the message formatting.
 *	12-Mar-2021 (rlwhitcomb)
 *	    Remove some unneeded logic.
 *	15-Mar-2021 (rlwhitcomb)
 *	    Add in some stack trace info if available.
 *	29-Apr-2021 (rlwhitcomb)
 *	    The string index exception needs a bit more than the message.
 *	    By default don't add in the stack trace, but allow it when desired.
 *	07-Jun-2021 (rlwhitcomb)
 *	    One more strange exception.
 *	07-Jul-2021 (rlwhitcomb)
 *	    Make class final and constructor private.
 *	24-Dec-2021 (rlwhitcomb)
 *	    Add ParseException to the mix.
 *	22-Jan-2022 (rlwhitcomb)
 *	    IllegalFormatException needs name as well as message.
 *	04-Feb-2022 (rlwhitcomb)
 *	    NoSuchFieldException needs name also.
 *	09-Feb-2022 (rlwhitcomb)
 *	    UnsupportedEncodingException also needs name.
 *	18-Feb-2022 (rlwhitcomb)
 *	    Deal gracefully with UncheckedIOException; tweak "exceptionName".
 *	    Rename class.
 *	09-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 */
package info.rlwhitcomb.util;

import java.io.FileNotFoundException;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.NoSuchFileException;
import java.text.ParseException;
import java.util.IllegalFormatException;
import java.util.UnknownFormatConversionException;


/**
 * Utility class that has various methods to help with exception handling
 * and display.
 */
public final class Exceptions
{
	/**
	 * Private constructor since this is a utility class.
	 */
	private Exceptions() {
	}

	/**
	 * Standalone function to produce a reasonable string of the given
	 * exception, including the message and cause (all the way up to
	 * the original exception thrown).
	 *
	 * @param ex	Any old exception we want to report on.
	 * @return	A string representation of the error.
	 */
	public static String toString(Throwable ex) {
	    StringBuilder buf = new StringBuilder();
	    return toString(ex, buf).toString();
	}

	/**
	 * Standalone version to do a "pure" string (using spaces instead of newlines).
	 *
	 * @param ex		The exception to report.
	 * @param useSpaces	Whether to use spaces instead of newlines to separate
	 *			the chain of causal exceptions.
	 * @return		String representation of the exception.
	 */
	public static String toString(Throwable ex, boolean useSpaces) {
	    StringBuilder buf = new StringBuilder();
	    return toString(ex, buf, false, useSpaces, false, false).toString();
	}

	/**
	 * Standalone version to do a "pure" string (using spaces instead of newlines),
	 * with option to add the stack trace info.
	 *
	 * @param ex		The exception to report.
	 * @param useSpaces	Whether to use spaces instead of newlines to separate
	 *			the chain of causal exceptions.
	 * @param addStackTrace	Whether to add the top level stack info to the message.
	 * @return		String representation of the exception.
	 */
	public static String toString(Throwable ex, boolean useSpaces, boolean addStackTrace) {
	    StringBuilder buf = new StringBuilder();
	    return toString(ex, buf, false, useSpaces, false, addStackTrace).toString();
	}

	/**
	 * Incremental version which allows for prepended or appended
	 * content by being passed the buffer in which to work.
	 *
	 * @param	ex	The exception to report.
	 * @param	buf	The buffer used to build the content.
	 * @return		The input buffer (in order to chain).
	 */
	public static StringBuilder toString(Throwable ex, StringBuilder buf) {
	    return toString(ex, buf, false, false, false, false);
	}

	/**
	 * Incremental version which allows for prepended or appended
	 * content by being passed the buffer in which to work, with
	 * the option to add the stack trace info.
	 *
	 * @param	ex	The exception to report.
	 * @param	buf	The buffer used to build the content.
	 * @param addStackTrace	Whether to add the stack trace info.
	 * @return		The input buffer (in order to chain).
	 */
	public static StringBuilder toString(Throwable ex, StringBuilder buf, boolean addStackTrace) {
	    return toString(ex, buf, false, false, false, addStackTrace);
	}

	/**
	 * Produce a more readable exception name from the given exception.
	 *
	 * @param	ex	The exception in question.
	 * @return		A "nicer" or more readable name to use in reporting.
	 */
	private static String exceptionName(Throwable ex) {
	    String simpleName = ex.getClass().getSimpleName();
	    String nicerName  = simpleName;

	    switch (simpleName) {
		case "CharacterCodingException":
		case "NumberFormatException":
		    break;
		default:
		    nicerName = simpleName.replace("Exception", "").replace("Error", "");
		    break;
	    }

	    // Make the exception name a little easier to read
	    StringBuilder buf = new StringBuilder(nicerName.length() * 2);
	    for (int i = 0; i < nicerName.length(); i++) {
		char ch = nicerName.charAt(i);
		if (i > 0 && Character.isUpperCase(ch))
		    buf.append(' ').append(Character.toLowerCase(ch));
		else
		    buf.append(ch);
	    }

	    return buf.toString();
	}

	/**
	 * Incremental version with more options.
	 *
	 * @param	ex		The exception to report.
	 * @param	buf		The buffer to build the string representation in.
	 * @param	useToString	{@code true} to format using the {@link Throwable#toString}
	 *				method instead of the {@link Throwable#getMessage} for the text.
	 * @param	useSpaces	{@code true} to use spaces instead of newlines to separate
	 *				the chained exceptions.
	 * @param	convertTabs	Convert any tab characters to single spaces (for use in controls
	 *				that don't deal with tabs correctly; some do).
	 * @param	addStackTrace	On the top level add the caller's caller location.
	 * @return			The input buffer (so calls can be chained).
	 */
	public static StringBuilder toString(Throwable ex, StringBuilder buf, boolean useToString, boolean useSpaces,
		boolean convertTabs, boolean addStackTrace) {
	    boolean topLevel = true;

	    for (Throwable next = ex; next != null; ) {
		if (next instanceof UncheckedIOException) {
		    next = next.getCause();
		}

		String msg;
		if (useToString) {
		    msg = next.toString();
		}
		else {
		    msg = next.getLocalizedMessage();
		    if (msg == null || msg.trim().isEmpty()) {
			msg = exceptionName(next);
		    }
		    else if ((next instanceof UnknownHostException)
			  || (next instanceof NoClassDefFoundError)
			  || (next instanceof ClassNotFoundException)
			  || (next instanceof NullPointerException)
			  || (next instanceof CharacterCodingException)
			  || (next instanceof IllegalCharsetNameException)
			  || (next instanceof UnsupportedCharsetException)
			  || (next instanceof UnsupportedEncodingException)
			  || (next instanceof FileNotFoundException)
			  || (next instanceof NoSuchFileException)
			  || (next instanceof UnsupportedOperationException)
			  || (next instanceof NumberFormatException)
			  || (next instanceof StringIndexOutOfBoundsException)
			  || (next instanceof UnknownFormatConversionException)
			  || (next instanceof IllegalFormatException)
			  || (next instanceof NoSuchFieldException)) {
			msg = String.format("%1$s: \"%2$s\"", exceptionName(next), msg);
		    }
		    else if (next instanceof ParseException) {
			ParseException pe = (ParseException) next;
			msg = Intl.formatString("util#except.parse", exceptionName(pe), pe.getErrorOffset(), msg);
		    }
		}
		buf.append(msg);

		// First time through, add in the first stack trace info
		if (topLevel) {
		    if (addStackTrace) {
			StackTraceElement[] stack = next.getStackTrace();
			if (stack != null && stack.length > 0) {
			    buf.append(useSpaces ? " " : "\n    ");
			    buf.append(Intl.formatString("util#except.fromStack", stack[0].toString()));
			}
		    }
		    topLevel = false;
		}

		next = next.getCause();

		if (next != null) {
		    buf.append(useSpaces ? ' ' : '\n');
		}
	    }
	    if (convertTabs) {
		int ix = 0;
		while ((ix = buf.indexOf("\t", ix)) >= 0) {
		    buf.setCharAt(ix++, ' ');
		}
	    }

	    return buf;
	}

}

