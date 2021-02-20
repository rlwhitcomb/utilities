/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014,2020-2021 Roger L. Whitcomb.
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
 *	18-Feb-2021 (rlwhitcomb)
 *	    Running "start()" inside our constructor means that the "run()"
 *	    method (and therefore the subclass' "task()") could start or even
 *	    finish before the subclass constructor is finished, which bollixes
 *	    up everything. See https://stackoverflow.com/questions/84285/calling-thread-start-within-its-own-constructor
 *	    for the answer to this question that I never even asked...
 *	    So, we should NOT overload Thread, but make a thread pool and accept a Runnable or Callable, maybe
 *	    using FutureTask or RunnableFuture, or something like that.
 *	19-Feb-2021 (rlwhitcomb)
 *	    Don't inherit from Thread, but use a thread pool instead to run the tasks.
 */

package info.rlwhitcomb.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;


/**
 * Instantiate one of these and override the {@link #task} method to do whatever needs to
 * be done in the background during startup.
 * <p> To use the class, create your own class that subclasses {@link InitializationTask}
 * and implements the {@link #task()} method. Once you are ready to start the process
 * call {@link #start()} for the subclass object. This will submit the task to a thread
 * pool where it will be executed in the background.
 * <p> Before accessing any resources in the object that need to be computed in the
 * background, call the {@link #waitUntilFinished} method first.
 */
public abstract class InitializationTask implements Runnable
{
	/**
	 * The logging object.
	 */
	private static final Logging logger = new Logging(InitializationTask.class);

	/**
	 * The thread pool with a number of threads equal to the number of processors available
	 * in the system. This will maximize the available throughput during initialization if
	 * a bunch of things should get ramped up in parallel.
	 */
	private static final ExecutorService threadPool = Executors.newWorkStealingPool();

	/**
	 * The semaphore used to wait on the background thread to start. This is used to wait
	 * for the job to be submitted to the background thread pool. The reason for this is
	 * that {@link #waitUntilFinished} relies on the {@link Future} object to tell if
	 * the task is finished, but that object is not available until after submission
	 * of ourselves to the thread pool for execution. So, the caller must wait for
	 * the semaphore before accessing the {@code Future}.
	 */
	private volatile Semaphore startSemaphore = new Semaphore(0);

	/**
	 * The future object that is used to signal when the task is finished.
	 */
	private volatile Future<?> future = null;


	/**
	 * Default constructor.
	 */
	public InitializationTask() {
	}

	/**
	 * Internal method that executes the overridden {@link #task} method as a background task.
	 */
	@Override
	public final void run() {
	    try {
		logger.debug("In 'run' method: executing 'task()'...");
		task();
		logger.debug("In 'run' method: finished 'task()'.");
	    }
	    catch (Throwable ex) {
		logger.except("Caught exception running 'task()'", ex);
	    }
	    logger.debug("Finished in 'run' method.");
	}

	/**
	 * Start this process in the background using the thread pool already established.
	 */
	public final void start() {
	    logger.debug("In 'start' method, submitting 'task()' to the thread pool...");
	    future = threadPool.submit(this);
	    logger.debug("In 'start' method: releasing the semaphore now that the 'future' is available.");
	    startSemaphore.release();
	    logger.debug("Finished with 'start' method.");
	}

	/**
	 * Override this method to do whatever initialization task(s) is/are required to be
	 * done in the background, in order to keep the UI responsive.
	 */
	protected abstract void task();

	/**
	 * Override this method in order to be notified of any exceptions that occur during
	 * processing of the {@link #task} method.
	 *
	 * @param ex	The exception that occurred during processing.
	 */
	protected void exceptionOccurred(final Throwable ex) {
	    // default is to do nothing
	}

	/**
	 * Call this method before every access of resources that are supposed to be
	 * initialized by the {@link #task} method.  This will ensure that the background
	 * thread is done with its work before accessing the resources.
	 */
	public final void waitUntilFinished() {
	    logger.debug("Inside 'waitUntilFinished': waiting for semaphore...");
	    // We will not acquire a permit until the "future" object is available
	    startSemaphore.acquireUninterruptibly();
	    logger.debug("Inside 'waitUntilFinished': acquired semaphore.");
	    // But then, this release makes the "acquire" complete instantly after that
	    startSemaphore.release();
	    logger.debug("Inside 'waitUntilFinished: waiting on Future to complete...");
	    try {
		future.get();
	    }
	    catch (InterruptedException | ExecutionException ex) {
		// TODO: is there anything we should do here?  The task is (probably) not done,
		// but what can we do about it?
		logger.except("Exception during 'future.get()'", ex);
	    }
	    logger.debug("Finished with 'waitUntilFinished' method.");
	}
}
