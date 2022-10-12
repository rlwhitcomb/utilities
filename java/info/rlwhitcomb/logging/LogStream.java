/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2008-2010,2014-2015,2020,2022 Roger L. Whitcomb.
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
 *	Utility class to provide a stream-oriented wrapper around the Logging class.
 *
 * Change History:
 *	01-Oct-2008 (rlwhitcomb)
 *	    Initial coding.
 *	06-Jan-2009 (rlwhitcomb)
 *	    Added Javadoc comments.
 *	13-Jul-2010 (rlwhitcomb)
 *	    Cloned for second project; change package.
 *	17-Sep-2010 (rlwhitcomb)
 *	    Clean up Javadoc; add empty param "println()" method.
 *	14-Aug-2014 (rlwhitcomb)
 *	    Reformat to current conventions; address "lint" issues.
 *	22-Oct-2015 (rlwhitcomb)
 *	    Restructure to be simpler and do less memory allocation
 *	    (i.e., don't get new StringBuilder every time).  Implement
 *	    "close" method to release the buffer.
 *	17-Mar-2020 (rlwhitcomb)
 *	    Rename a method in ClientStatistics, so update the Javadoc here.
 *	16-Feb-2022 (rlwhitcomb)
 *	    Use buffer size from Constants.
 *	09-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 *	12-Oct-2022 (rlwhitcomb)
 *	    #513: Move to "logging" package.
 */
package info.rlwhitcomb.logging;

import java.io.PrintStream;

import static info.rlwhitcomb.util.Constants.*;


/**
 * Wraps the {@link Logging} class in a {@link PrintStream}-compatible object for use
 * were we need to log stuff using this interface (such as the
 * {@link ClientStatistics#reportStatistics ClientStatistics.reportStatistics} method).
 * <p> Because of the difference in semantics between the {@link Logging} class (which is line-at-a-time oriented)
 * and the {@link PrintStream} interface (which is <i>doh</i> stream oriented), we use an internal {@link StringBuffer}
 * object to buffer any pending {@link #print print} strings until a {@link #println println} is encountered.
 */
public class LogStream extends PrintStream
{
	/** Logging level to use with this stream. */
	private int logLevel = Logging.INFO;
	/**
	 * Buffer to convert stream semantics to line-at-a-time semantics.
	 * <p> Note: this buffer will be reused after the initial allocation,
	 * which will result in the maximum length buffer hanging around until
	 * this object is finalized.
	 */
	private StringBuffer buf;

	/**
	 * Constructor given a logging level to use for this stream.
	 * <p> Normally the level would be set by and external setting
	 * to correspond to the level set at startup for the whole system.
	 * <p> Calls the superclass constructor with {@link System#out} and sets the internal
	 * {@link #logLevel} to the given level.
	 *
	 * @param	level	one of the {@link Logging} level constants
	 */
	public LogStream(int level) {
	    super(System.out);
	    logLevel = level;
	    buf = new StringBuffer(CHAR_BUFFER_SIZE);
	}

	/**
	 * Constructor to use the default logging level of {@link Logging#INFO}.
	 * <p> Leaves the default value of {@link #logLevel} alone and calls the superclass constructor
	 * with {@link System#out}.
	 */
	public LogStream() {
	    this(Logging.INFO);
	}

	/**
	 * Print the given string to this stream.
	 * <p> Simply buffers the string to our internal {@link StringBuffer} object
	 * until a {@link #println println} occurs, when it will be flushed to the logging system.
	 *
	 * @param	s	string value to be logged
	 */
	public void print(String s) {
	    buf.append(s);
	}

	/**
	 * Flush any pending {@link #print} strings plus the given string to the logging system.
	 * <p> Uses the {@link #logLevel} set either by default or by the secondary constructor to
	 * log the string(s).  All the buffered output will be discarded once it is logged.
	 *
	 * @param	s	additional output to be logged on the current line
	 */
	public void println(String s) {
	    if (buf.length() == 0) {
		Logging.Log(logLevel, s);
	    }
	    else {
		buf.append(s);
		Logging.Log(logLevel, buf.toString());
		buf.setLength(0);
	    }
	}

	/**
	 * Flush any pending {@link #print} strings to the logging system.
	 * <p> Uses the {@link #logLevel} set either by default or by the secondary constructor to
	 * log the string(s).  All the buffered output will be discarded once it is logged.
	 * <p> Note: if nothing was printed since the last call to one of the {@code println()}
	 * methods, then nothing will be output to the log.
	 */
	public void println() {
	    if (buf.length() != 0) {
		Logging.Log(logLevel, buf.toString());
		buf.setLength(0);
	    }
	}

	/**
	 * Close the stream by calling {@link PrintStream#close} and then releasing
	 * our internal buffer.
	 */
	@Override
	public void close() {
	    super.close();
	    buf = null;
	}

}
