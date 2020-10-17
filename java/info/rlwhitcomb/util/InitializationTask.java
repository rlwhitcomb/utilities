//*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014,2020 Roger L. Whitcomb.
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
 *	A Thread object that can be used for background initialization tasks.
 *
 *  History:
 *	27-Feb-2014 (rlwhitcomb)
 *	    Created from the first implementation in GraphicsUtil,
 *	    but generalized for further use.
 *	16-Oct-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 */

package info.rlwhitcomb.util;

import java.util.concurrent.Semaphore;


/**
 * Instantiate one of these and override the {@link #task} method to do whatever needs to
 * be done in the background during startup.
 * <p> The background activity is protected by a {@link Semaphore} so that every access to the
 * resource is checked to make sure the background thread has completed before access.
 */
public abstract class InitializationTask extends Thread
{
	/** The semaphore used to wait on the background thread to finish. The initial count of zero
	 * ensures that {@link #waitUntilFinished} will not complete until the initial {@link #task}
	 * has completed (called by the {@link #run} method, which does a {@link Semaphore#release}
	 * at the end).
	 */
	private volatile Semaphore waitSemaphore = new Semaphore(0);

	/**
	 * Construct the thread, and run the task.
	 */
	public InitializationTask() {
	    super();
	    // Since this is a convenience (i.e., the "waitUntilFinished" may never be called),
	    // don't stall shutdown waiting for this task to finish
	    setDaemon(true);
	    start();
	}

	/**
	 * Internal method that executes the overridden {@link #task} method as a background task.
	 */
	@Override
	public void run() {
	    try {
		task();
	    }
	    finally {
		waitSemaphore.release();
	    }
	}

	/**
	 * Override this method to do whatever initialization task(s) is/are required to be
	 * done in the background, in order to keep the UI responsive.
	 */
	protected abstract void task();

	/**
	 * Call this method before every access of resources that are supposed to be
	 * initialized by the {@link #task} method.  This will ensure that the background
	 * thread is done with its work before accessing the resources.
	 */
	public void waitUntilFinished() {
	    // We may not acquire a permit until (at least) the release at the end of the "run" method
	    waitSemaphore.acquireUninterruptibly();
	    // But then, this release makes the "acquire" complete instantly after that
	    waitSemaphore.release();
	}
}
