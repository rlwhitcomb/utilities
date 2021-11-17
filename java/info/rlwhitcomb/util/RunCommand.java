/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2009-2011,2013-2018,2020-2021 Roger L. Whitcomb.
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
 *	Class to facilitate running an external program from within Java,
 *	and capturing its output.
 *
 * Change history:
 *  20-Mar-2009 (rlwhitcomb)
 *	Initial coding.
 *  27-Mar-2009 (rlwhitcomb)
 *	Added ability to echo to any arbitray PrintStream (such as LogStream)
 *  06-Oct-2010 (rlwhitcomb)
 *	Branch and move to "info.rlwhitcomb.util" package.
 *  17-May-2013 (rlwhitcomb)
 *	Change logging level check.
 *  15-Sep-2013 (rlwhitcomb)
 *	Add method to allow writing to subprocess' stdin stream.
 *  29-Jul-2014 (rlwhitcomb)
 *	Add accessor for subprocess environment.
 *  31-Jul-2014 (rlwhitcomb)
 *	Add method to get the command line (for debugging, logging, etc.)
 *  25-Jun-2015 (rlwhitcomb)
 *	In order to better deal with strange issues related to mixed line
 *	endings, change from using BufferedReader to just BufferedInputStream
 *	and let callers deal with translation issues.
 *  07-Jan-2016 (rlwhitcomb)
 *	Fix Javadoc warnings found by Java 8.
 *  30-Mar-2017 (rlwhitcomb)
 *	An additional flavor of constructor for convenience with variable
 *	number of command line arguments.
 *  10-Apr-2018 (rlwhitcomb)
 *	Simplify and fix the "commandLine" method.
 *  10-Mar-2020 (rlwhitcomb)
 *	Prepare for GitHub.
 *  21-Dec-2020 (rlwhitcomb)
 *	Update obsolete Javadoc constructs.
 *  07-Apr-2021 (rlwhitcomb)
 *	Tighten up the code and comments.
 *	Add a "runToCompletion" that writes to a StringBuilder to store the output.
 *  16-Nov-2021 (rlwhitcomb)
 *	#85: Further versions to log exceptions or throw them.
 */
package info.rlwhitcomb.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;


/**
 * Run an external command and capture the output in a {@link BufferedInputStream}.
 * <p> Any error messages directed to the "stderr" stream of the command
 * will be merged inline into the standard output stream.
 * <p> The "stdin" stream is also available in order to write to the process.
 */
public class RunCommand
{
	/** The initial byte array buffer size when writing process output to a string. */
	private static final int BUFFER_SIZE = 8192;

	/** The exit code from the child process. Integer.MIN_VALUE indicates the process has not yet terminated. */
	private int errorLevel = Integer.MIN_VALUE;
	/** The object used to setup the running environment for the child process. */
	private ProcessBuilder pb = null;
	/** The actual process object of the child. */
	private Process p = null;
	/** Actually this is the merged <code>stdout</code> and <code>stderr</code> of the child process. */
	private BufferedInputStream stdInput = null;
	/** The child process' <code>stdin</code> stream (available for writing). */
	private BufferedOutputStream stdOutput = null;
	/** The {@link PrintStream} to use to echo the command output. */
	private PrintStream out = System.out;

	/**
	 * Return the process exit code.
	 * <p> We may have to wait for the process to exit if the
	 * saved value has not been set yet (as when the caller reads
	 * the output as it comes).
	 *
	 * @return	the {@link #errorLevel} value
	 */
	public int getErrorLevel() {
	    if (errorLevel == Integer.MIN_VALUE) {
		try {
		    errorLevel = p.waitFor();
		}
		catch (InterruptedException ie) {
		    Logging.Except(ie);
		}
	    }
	    return errorLevel;
	}

	/**
	 * Set the echo stream to non-default value.
	 *
	 * @param	o	new {@link PrintStream} to use to echo command output.
	 */
	public void setEchoStream(final PrintStream o) {
	    out = o;
	}

	/**
	 * @return The subprocess' <code>stdin</code> stream to write to.
	 */
	public OutputStream getInputStream() {
	    return stdOutput;
	}

	/**
	 * Constructor given the command and its arguments to run.
	 *
	 * @param	command	variable number of command arguments --
	 *			the first one of which must be the executable name
	 */
	public RunCommand(final String... command) {
	    init(command);
	}

	/**
	 * Convenience constructor given a command and a list (as opposed
	 * to an array) of arguments.
	 *
	 * @param	command	The name of the program to run.
	 * @param	args	The list of arguments (could be empty).
	 */
	public RunCommand(final String command, final List<String> args) {
	    String[] allArgs = new String[args.size() + 1];
	    allArgs[0] = command;
	    int i = 1;
	    for (String arg : args) {
		allArgs[i++] = arg;
	    }
	    init(allArgs);
	}

	private void init(final String... command) {
	    pb = new ProcessBuilder(command);
	    pb.redirectErrorStream(true);
	}

	/**
	 * @return The subprocess environment.
	 */
	public Map<String, String> environment() {
	    return pb.environment();
	}

	/**
	 * @return The process' command line.
	 */
	public String commandLine() {
	    return CharUtil.makeSimpleStringList(pb.command(), ' ');
	}

	/**
	 * Run this child process using whatever has been established as the command
	 * and its environment.
	 *
	 * @return	pointer to the merged error and standard output of the child process
	 */
	public InputStream run() {
	    return run(false);
	}

	/**
	 * Run this child process using whatever has been established as the command
	 * and its environment.
	 *
	 * @param	logOrThrow <code>false</code> to log exceptions or <code>true</code>
	 *		to wrap in a {@link RuntimeException} and throw them
	 * @return	pointer to the merged error and standard output of the child process
	 */
	public InputStream run(final boolean logOrThrow) {
	    try {
		if (Logging.isLevelEnabled(Logging.DEBUG)) {
		    Logging.Debug("RunCommand.run: %1$s", commandLine());
		}

		p = pb.start();

		stdInput = new BufferedInputStream(p.getInputStream());
		stdOutput = new BufferedOutputStream(p.getOutputStream());

		return stdInput;
	    }
	    catch (IOException ioe) {
		if (logOrThrow)
		    throw new RuntimeException(ioe);
		else
		    Logging.Except(ioe);
	    }
	    return null;
	}

	/**
	 * Run the given command in this new working directory.
	 *
	 * @param	workingDir	new working directory where the process should start
	 * @return			pointer to merged output from the child process
	 */
	public InputStream run(final File workingDir) {
	    return run(workingDir, false);
	}

	/**
	 * Run the given command in this new working directory.
	 *
	 * @param	workingDir	new working directory where the process should start
	 * @param	logOrThrow	<code>false</code> to log exceptions or <code>true</code>
	 *				to wrap in a {@link RuntimeException} and throw them
	 * @return			pointer to merged output from the child process
	 */
	public InputStream run(final File workingDir, final boolean logOrThrow) {
	    pb.directory(workingDir);
	    return run(logOrThrow);
	}

	/**
	 * Start the child process and wait for it to complete, possibly
	 * echoing the output to the console.
	 *
	 * @param	echoOutput	{@code true} if the output from the child
	 *				process should be echoed to {@link System#out}
	 * @return	return code from the child process
	 */
	public int runToCompletion(final boolean echoOutput) {
	    return runToCompletion(echoOutput, false);
	}

	/**
	 * Start the child process and wait for it to complete, possibly
	 * echoing the output to the console.
	 *
	 * @param	echoOutput	{@code true} if the output from the child
	 *				process should be echoed to {@link System#out}
	 * @param	logOrThrow	<code>false</code> to log exceptions or <code>true</code>
	 *				to wrap in a {@link RuntimeException} and throw them
	 * @return	return code from the child process
	 */
	public int runToCompletion(final boolean echoOutput, final boolean logOrThrow) {
	    try {
		InputStream output = run(logOrThrow);
		int ch;

		if (output != null) {
		    if (echoOutput) {
			while ((ch = output.read()) != -1) {
			    out.write(ch);
			}
		    }
		    else {
			while ((ch = output.read()) != -1)
			    ;
		    }
		    output.close();
		    errorLevel = p.waitFor();
		}
	    }
	    catch (IOException ioe) {
		if (logOrThrow)
		    throw new RuntimeException(ioe);
		else
		    Logging.Except(ioe);
	    }
	    catch (InterruptedException ie) {
		if (logOrThrow)
		    throw new RuntimeException(ie);
		else
		    Logging.Except(ie);
	    }

	    return errorLevel;
	}

	/**
	 * Start the child process and wait for it to complete, all output
	 * is echoed to the console.
	 *
	 * @return		return code from the child process
	 */
	public int runToCompletion() {
	    return runToCompletion(true, false);
	}

	/**
	 * Start the child process and wait for it to complete, all output
	 * is echoed to the given stream.
	 *
	 * @param	o	{@link PrintStream} to use to echo process output
	 * @return		return code from the child process
	 */
	public int runToCompletion(final PrintStream o) {
	    setEchoStream(o);
	    return runToCompletion();
	}

	/**
	 * Start the child process and wait for it to complete, all output
	 * is echoed to the given string buffer.
	 *
	 * @param	buf	{@link StringBuilder} to use to echo process output
	 * @return		return code from the child process
	 */
	public int runToCompletion(final StringBuilder buf) {
	    return runToCompletion(buf, false);
	}

	/**
	 * Start the child process and wait for it to complete, all output
	 * is echoed to the given string buffer.
	 *
	 * @param	buf		{@link StringBuilder} to use to echo process output
	 * @param	logOrThrow	<code>false</code> to log exceptions, or <code>true</code>
	 *				to wrap in a {@link RuntimeException} and throw them
	 * @return	return code from the child process
	 */
	public int runToCompletion(final StringBuilder buf, final boolean logOrThrow) {
	    ByteArrayOutputStream os = new ByteArrayOutputStream(BUFFER_SIZE);
	    setEchoStream(new PrintStream(os, true));
	    int ret = runToCompletion(true, logOrThrow);
	    buf.append(os.toString());
	    return ret;
	}

	/**
	 * Start the child process and wait for it to complete, setting the working
	 * directory first and echoing all output to the console.
	 *
	 * @param	workingDir	the child process' working directory
	 * @return			return code from the child process
	 */
	public int runToCompletion(final File workingDir) {
	    pb.directory(workingDir);
	    return runToCompletion();
	}

	/**
	 * Start the child process and wait for it to complete, setting the working
	 * directory first and echoing all output to the given stream.
	 *
	 * @param	workingDir	the child process' working directory
	 * @param	o		a {@link PrintStream} to use to echo the output
	 * @return			return code from the child process
	 */
	public int runToCompletion(final File workingDir, final PrintStream o) {
	    pb.directory(workingDir);
	    setEchoStream(o);
	    return runToCompletion();
	}

	/**
	 * Start the child process and wait for it to complete, setting the working
	 * directory first and giving a choice to echo output to the console or not.
	 *
	 * @param	workingDir	the child process' working directory
	 * @param	echoOutput	{@code true} if the output from the child
	 *				process should be echoed to {@link System#out}
	 * @return			return code from the child process, which will
	 *				be {@link Integer#MIN_VALUE} if the process has
	 *				not yet completed when this method returns.
	 */
	public int runToCompletion(final File workingDir, final boolean echoOutput) {
	    return runToCompletion(workingDir, echoOutput, false);
	}

	/**
	 * Start the child process and wait for it to complete, setting the working
	 * directory first and giving a choice to echo output to the console or not.
	 *
	 * @param	workingDir	the child process' working directory
	 * @param	echoOutput	{@code true} if the output from the child
	 *				process should be echoed to {@link System#out}
	 * @param	logOrThrow	<code>false</code> to log exceptions, or <code>true</code>
	 *				to wrap them in a {@link RuntimeException} and throw them
	 * @return			return code from the child process, which will
	 *				be {@link Integer#MIN_VALUE} if the process has
	 *				not yet completed when this method returns.
	 */
	public int runToCompletion(final File workingDir, final boolean echoOutput, final boolean logOrThrow) {
	    pb.directory(workingDir);
	    return runToCompletion(echoOutput, logOrThrow);
	}

}
