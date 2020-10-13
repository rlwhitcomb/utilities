/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014,2016,2020 Roger L. Whitcomb
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
 *	A Thread subclass that will be used to execute things that may never
 *	finish, so we mark them as "daemon" threads, and let them go to finish
 *	or not.
 *
 * History:
 *	12-Aug-2014 (rlwhitcomb)
 *	    First version.
 *	07-Jan-2016 (rlwhitcomb)
 *	    Fix Javadoc warnings found by Java 8.
 *	19-Feb-2020 (rlwhitcomb)
 *	    Undo the Adapter in favor of default interface methods in Task itself.
 */
package info.rlwhitcomb.util;


/**
 * A "OneShotThread" is used for something that needs doing, but that
 * may never finish, so the thread should not be expected to be reused
 * (as opposed to our {@link QueuedThread} that is meant to be reused).
 * <p> This is preferred over a regular {@link Thread} in that it runs
 * itself when constructed, and has a wider interface for the job to
 * be done than just {@link Runnable} - passing in an object for context
 * and signaling both exceptions that occur and when the task is complete
 * (if ever).
 */
public class OneShotThread extends Thread
{
	public interface Task
	{
		/**
		 * The method in the task that is responsible for doing
		 * the work within this thread. This method must be implemented
		 * by the task class.
		 *
		 * @param context	The task's context, passed into the
		 *			thread constructor.
		 */
		void execute(Object context);

		/**
		 * The method called when an exception is caught during
		 * task execution. Need not be implemented in the task class.
		 *
		 * @param ex	The exception that was caught.
		 */
		default void reportException(Throwable ex) {
		}

		/**
		 * The method in the task that will be called when the job
		 * is finished. Need not be implemented in the task class.
		 */
		default void finished() {
		}
	}


	/**
	 * Whatever context is needed for the task to run.
	 */
	private Object context;
	/**
	 * The task to be executed by this thread (probably long-running
	 * and possibly never finishing).
	 */
	private Task task;

	/**
	 * Setup the task's context, and the task to be done
	 * and then start the task.
	 *
	 * @param	context	The task-specific context.
	 * @param	task	The job to be done.
	 */
	public OneShotThread(Object context, Task task) {
	    this.context = context;
	    this.task = task;
	    setDaemon(true);
	    start();
	}

	@Override
	public void run() {
	    try {
		if (task != null) {
		    task.execute(context);
		}
	    }
	    catch (Throwable ex) {
		if (task != null) {
		    task.reportException(ex);
		}
	    }
	    finally {
		if (task != null) {
		    task.finished();
		}
		task = null;
		context = null;
	    }
	}

}
