/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Roger L. Whitcomb.
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
 *	Utility program that utilizes the timing facilities in Environment
 *	to time the execution of an external program.
 *
 * History:
 *	30-Aug-2021 (rlwhitcomb)
 *	    Initial coding.
 */
package info.rlwhitcomb.util;


/**
 * Invoke an external program using the {@link RunCommand} class and time its
 * operation, all the while echoing its output to the console. Report the timing
 * at the end of the operation.
 * <p> Utililizes several utility classes already present.
 */
public class TimeThis
{
	/**
	 * The {@link Runnable} that will be invoked by the timing method.
	 */
	private static class CommandRunnable implements Runnable
	{
		private String[] args;
		private int retCode;


		public CommandRunnable(String[] cmdArgs) {
		    this.args = cmdArgs;
		    this.retCode = -1;
		}

		@Override
		public void run() {
		    RunCommand cmd = new RunCommand(args);
		    retCode = cmd.runToCompletion();
		}

		public int getRetCode() {
		    return retCode;
		}
	}


	/**
	 * Gather the command line arguments, run the given program and report
	 * the timing information.
	 * <p> Sets our process exit code to that of the invoked program.
	 *
	 * @param args	The parsed command line arguments.
	 */
	public static void main(String[] args) {
	    CommandRunnable cmd = new CommandRunnable(args);
	    Environment.timeThis(cmd);
	    int ret = cmd.getRetCode();
	    if (ret != 0)
		System.exit(ret);
	}

}

