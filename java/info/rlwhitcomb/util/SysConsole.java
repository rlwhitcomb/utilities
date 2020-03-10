/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017,2020 Roger L. Whitcomb.
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
 *	A system console interface, normally implemented by the Java
 *	Console object, but which can also be implemented by a custom
 *	replacement.
 *
 *  History:
 *	04-Dec-2017 (rlwhitcomb)
 *	    Created.
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 */
package info.rlwhitcomb.util;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.Charset;


/**
 * An abstract class that implements a system "Console", normally implemented
 * by the Java {@link Console} object, but which can be overridden by another
 * subclass.
 * <p> For now, this class only defines the single method used in our code, but
 * which obviously could be extended for further needs.
 */
public abstract class SysConsole
{
	/**
	 * A system console object based on the standard {@link Console} (obtained
	 * from {@link System#console}.
	 */
	public static class Default extends SysConsole
	{
		private Console console;

		public Default() {
		    if ((this.console = System.console()) == null) {
			throw new IllegalStateException(Intl.getString("util#console.noConsole"));
		    }
		}

		@Override
		public String readLine(String fmt, Object... args) {
		    return this.console.readLine(fmt, args);
		}
	}


	/**
	 * A system console object based on using {@link System#in} and {@link System#out}
	 * so that redirects of these will work properly.
	 */
	public static class Redirect extends SysConsole
	{
		private Charset charset;
		private BufferedReader reader;

		public Redirect() {
		    this(null);
		}

		public Redirect(Charset charset) {
		    this.charset = charset == null ? Charset.defaultCharset() : charset;
		    this.reader = new BufferedReader(new InputStreamReader(System.in, charset));
		}

		@Override
		public String readLine(String fmt, Object... args) {
		    System.out.format(fmt, args);
		    System.out.flush();
		    try {
			String line = reader.readLine();
			System.out.println(line);
			return line;
		    }
		    catch (IOException ioe) {
		        throw new RuntimeException(ioe);
		    }
		}
	}


	/**
	 * Format a prompt using the given format string and (possibly empty)
	 * list of arguments for it, then read the user response line and return
	 * it (will be {@code null} at the end of the input).
	 *
	 * @param fmt The format string used to format the prompt.
	 * @param args Any argument necessary to format the prompt.
	 * @return The next line of user input in response to that prompt.
	 */
	public abstract String readLine(String fmt, Object... args);

}
