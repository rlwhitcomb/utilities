/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013-2016,2018,2020 Roger L. Whitcomb.
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
 *	Wrapper for the RunCommand class that provides a clean
 *	interface for running the command and processing its
 *	output stream, one line at a time.
 *
 * Change history:
 *	21-May-2013 (rlwhitcomb)
 *	    Cloned from Submit.java and added Javadoc.
 *	29-Jul-2014 (rlwhitcomb)
 *	    Added access to the subprocess environment.
 *	03-Mar-2015 (rlwhitcomb)
 *	    Moved to the info.rlwhitcomb.util package.
 *	25-Jun-2015 (rlwhitcomb)
 *	    Roll our own "readLine" method that will only
 *	    exactly match the system lineSeparator string.
 *	    This is because some utilities might put out funky
 *	    data within a line that shouldn't be interpreted as
 *	    an end-of-line in the same sense that BufferedReader
 *	    allows.  Relies on RunCommand just passing on the
 *	    bytes directly that come from the process.
 *	31-Aug-2015 (rlwhitcomb)
 *	    Cleanup Javadoc (detected by Java 8).
 *	18-Sep-2015 (rlwhitcomb)
 *	    Add a flag to restore the default eol processing
 *	    in cases where it makes more sense.
 *	30-Sep-2015 (rlwhitcomb)
 *	    Fix Javadoc warnings uncovered by Java 8.
 *	07-Jan-2016 (rlwhitcomb)
 *	    More Javadoc fixes.
 *	21-Jun-2016 (rlwhitcomb)
 *	    Add an accessor method for the subprocess' command line.
 *	12-Mar-2018 (rlwhitcomb)
 *	    Add "beginProcess" callback.
 *	27-Mar-2018 (rlwhitcomb)
 *	    Allow for the RunCommand to be created after the constructor.
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 */
package info.rlwhitcomb.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Map;

/**
 * Wrapper class for {@link RunCommand} that also processes
 * the output from the command into line-at-a-time semantics
 * and calls a subclass processing routine for each line.
 * <p> Typical usage would look something like this:
 * <pre>	private class WorkingListProcessor extends CommandProcessor
 *	{
 *		public WorkingListProcessor() {
 *		    super("dir", "/w");
 *		}
 *		&#64;Override
 *		public void beginProcess() {
 *		    &lt;do initialization here&gt;
 *		}
 *		&#64;Override
 *		public boolean process(String line) {
 *		    &lt;process "line"&gt;
 *		    return true; // to continue, or false to stop the process
 *		}
 *		&#64;Override
 *		public void endProcess() {
 *		    &lt;do cleanup here&gt;
 *		}
 *	}</pre>
 * <p> Couple of caveats:
 * <ul><li>No character set support, so output is treated as system default
 * character set.
 * <li>Any I/O exception will (probably) terminate the calling process with
 * a {@link RuntimeException}.
 * </ul>
 */
public class CommandProcessor
{
	private RunCommand rc = null;
	private int retCode = -1;
	private static final String LS = System.lineSeparator();
	private static final int LS_LEN = LS.length();
	private boolean useDefaultSeparators = false;

	/**
	 * Method called before the command is executed.
	 */
	protected void beginProcess() {
	}

	/**
	 * Method called for each output line produced by the command.
	 * @param	line	The next output line produced by the command.
	 * @return		<tt>true</tt> to continue processing, <tt>false</tt>
	 *			if the consumer does not want to continue processing.
	 */
	protected boolean process(String line) {
	    return true;
	}

	/**
	 * Method called when the command has finished, and after all the output
	 * lines have been processed.
	 */
	protected void endProcess() {
	}

	/**
	 * Default constructor.
	 */
	public CommandProcessor() {
	}

	/**
	 * Constructor that takes the command name and any arguments.
	 *
	 * @param	command	The command name and its arguments.
	 */
	public CommandProcessor(String... command) {
	    setup(command);
	}

	/**
	 * Setup the command name and any arguments.
	 *
	 * @param	command	The command name and its arguments.
	 */
	public void setup(String... command) {
	    rc = new RunCommand(command);
	}

	/**
	 * Access the subprocess environment.
	 *
	 * @return <tt>String, String</tt> map of the environment for the process
	 * we will run.
	 */
	public Map<String, String> environment() {
	    return rc.environment();
	}

	/**
	 * Get the subprocess' command line.
	 *
	 * @return The complete command line for the subprocess.
	 */
	public String commandLine() {
	    return rc.commandLine();
	}

	/**
	 * Access the process' return code.
	 *
	 * @return Return code from the subprocess.
	 */
	public int getReturnCode() {
	    return retCode;
	}

	/**
	 * Set the flag to say "use default line separators"
	 * @param value {@code true} to accept the default line separator(s)
	 * while parsing the process output (which is generally LF, or CR/LF);
	 * {@code false} to only accept the specific line separators for the
	 * current platform.
	 */
	public void setUseDefaultSeparators(boolean value) {
	    this.useDefaultSeparators = value;
	}

	/**
	 * Execute the command and process its output.
	 */
	public void run() {
	    run(null);
	}

	/**
	 * Read an entire line from the process' output stream, breaking at the
	 * exact system line separator (see {@link #LS}).
	 * @param reader The output stream of the subprocess.
	 * @return The next line from the stream or <tt>null</tt> at the end
	 * of file.
	 * @throws IOException if there are problems
	 */
	private String readLine(Reader reader)
		throws IOException
	{
	    StringBuilder buf = new StringBuilder();
	    StringBuilder eolBuf = new StringBuilder(LS_LEN);
	    int ch;
	    int eolIndex = 0;
	    while ((ch = reader.read()) != -1) {
		if (ch == LS.charAt(eolIndex)) {
		    eolIndex++;
		    if (eolIndex >= LS_LEN) {
			return buf.toString();
		    }
		    eolBuf.append((char)ch);
		    continue;
		}
		else if (eolIndex > 0) {
		    // Mismatch on the EOL string, so append what we matched
		    // so far and reset the match index
		    buf.append(eolBuf);
		    eolBuf.setLength(0);
		    eolIndex = 0;
		}
		// We need this check again in the case of CR, CR, LF so that
		// the second CR is property recognized as a possible EOL char
		if (ch == LS.charAt(eolIndex)) {
		    eolIndex++;
		    if (eolIndex >= LS_LEN) {
			return buf.toString();
		    }
		    eolBuf.append((char)ch);
		    continue;
		}
		buf.append((char)ch);
	    }
	    // At EOF, return null if nothing has been read
	    if (buf.length() == 0) {
		return null;
	    }
	    return buf.toString();
	}


	/**
	 * Run the command in the given directory and process its output.
	 *
	 * @param	dir	The working directory to set for the process.
	 */
	public void run(File dir) {
	    beginProcess();
	    ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
	    PrintStream ps = new PrintStream(baos);
	    retCode = (dir == null ? rc.runToCompletion(ps) : rc.runToCompletion(dir, ps));
	    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
	    InputStreamReader isr = new InputStreamReader(bais);
	    String line = null;
	    try {
		if (useDefaultSeparators) {
		    // Using Java default line separators, use the regular
		    // BufferedReader to parse the lines.
		    BufferedReader rdr = new BufferedReader(isr);
		    while ((line = rdr.readLine()) != null) {
			if (!process(line)) {
			    break;
			}
		    }
		}
		else {
		    // Using exact line separators for the platform,
		    // use our own routine to find ends of lines.
		    while ((line = readLine(isr)) != null) {
			if (!process(line)) {
			    break;
			}
		    }
		}
	    }
	    catch (IOException ioe) {
		throw new RuntimeException(ioe);
	    }
	    finally {
		ps.close();
	    }
	    endProcess();
	}
}


