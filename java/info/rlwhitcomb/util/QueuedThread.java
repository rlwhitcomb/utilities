/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013,2016,2018,2020 Roger L. Whitcomb.
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
 *	Utility class that supports running multiple tasks in a single thread.
 *	This will be used to repeatedly run queries using the same thread so
 *	we can maintain the same open connection to the remote server to avoid
 *	the connection overheads each time.
 *
 * History:
 *	12-Feb-2013 (rlwhitcomb)
 *	    First version.
 *	21-Mar-2013 (rlwhitcomb)
 *	    Add processing for uncaught exceptions.  Enhance by allowing this
 *	    thread to queue itself up for work on an external queue when it
 *	    is available.  That way it can be reused in various situations.
 *	06-Jan-2016 (rlwhitcomb)
 *	    Null out the runnable as soon as possible to assist in GC.
 *	    Rename "queue" to "workQueue" to assist in comprehension!
 *	    Also rename a constructor parameter for the same reason.
 *	22-Jan-2018 (rlwhitcomb)
 *	    Allow the executor service to set a terminate flag.
 *	03-Feb-2020 (rlwhitcomb)
 *	    Use atomic variables to better work with separate threads.
 *	    Add "busy" flag; work better with the worker queue if supplied
 *	    at construction time.
 *	    Change the work queue to a LinkedTransferQueue and NOT a
 *	    SynchronousQueue, which is just not appropriate.
 *	    Better logging throughout.
 *	28-Mar-2020 (rlwhitcomb)
 *	    I think we need an atomic integer for the id.
 *	21-Dec-2020 (rlwhitcomb)
 *	    Update obsolete Javadoc constructs.
 */
package info.rlwhitcomb.util;

import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class implements a simple thread that waits on queued work and then runs it
 * when available.  The thread is a "daemon" thread that can be killed by the JVM
 * when the main thread quits without harm.
 * <p> The main point to this class is to keep one thread context around, in the case
 * (for instance) where some context is tied to the thread. Plus, the overhead of thread
 * creation for every job is avoided.
 * <p> Instantiate one instance of this thread, and then submit (via the {@link #submitWork}
 * method) as many jobs ({@link Runnable} objects) as you want.  If the previous job has
 * finished, then {@link #submitWork} will immediately transfer the work, otherwise it will
 * queue the work to start once the current task has finished.
 * <p> For convenience, this worker thread can be added to an external queue of workers,
 * such that an outside process/thread can potentially find the first available worker
 * on that queue to avoid any unnecessary waiting.
 * <p> This class pairs well with {@link QueuedExecutorService} which should be used to
 * maintain that external queue of workers (if needed), and to easily submit work.
 * But note that if you need successive jobs to really run on the same thread, then just
 * use one of these objects directly, without the executor service, because that class
 * makes no guarantees as to which thread will perform the work.
 * <p> In order to kill this thread before shutdown, interrupt it while it is waiting for
 * work -- this will break out of the infinite loop.
 */
public class QueuedThread extends Thread
{
	private static Logging logger = new Logging(QueuedThread.class);
	private static AtomicInteger id = new AtomicInteger();

	/** The external thread queue we will add ourselves to when we're waiting for work. */
	private final Queue<QueuedThread> workerQueue;

	/** The work queue we will process. */
	private final LinkedTransferQueue<Runnable> workQueue = new LinkedTransferQueue<>();

	/** Flag to say we should terminate. */
	private final AtomicBoolean terminated = new AtomicBoolean();

	/** The actual {@link Runnable} that we are running. If set, we are busy. */
	private final AtomicReference<Runnable> runnable = new AtomicReference<>();

	/** @return A uniformly formed name for this thread. */
	private static String newName() {
	    return String.format("%1$s-%2$d", QueuedThread.class.getSimpleName(), id.incrementAndGet());
	}

	/**
	 * Constructor that simply marks us as a daemon thread (i.e., can be killed
	 * at any time that nothing else is happening) and starts us running with an
	 * internal queue of work.
	 */
	public QueuedThread() {
	    this(null);
	}

	/**
	 * Constructor for when we are to make ourselves available for use on an external queue of workers.
	 *
	 * @param	workerQueue	The external queue of available worker threads that we will subscribe to.
	 */
	public QueuedThread(Queue<QueuedThread> workerQueue) {
	    super(newName());
	    setDaemon(true);
	    this.workerQueue = workerQueue;
	    start();
	}

	/**
	 * @return <code>true</code> if this thread has been marked to terminate
	 * (which means it can accept no more new work).
	 */
	public boolean isTerminated() {
	    return terminated.get();
	}

	/**
	 * Sets the flag to say we should terminate.
	 * @return Whether the terminated flag was previously set.
	 */
	public boolean setTerminated() {
	    logger.debug("setTerminated called");
	    return terminated.getAndSet(true);
	}

	/**
	 * Checks to see if we are busy.
	 *
	 * @return <code>true</code> if we are busy executing a work package;
	 * or if there is something waiting in the queue.
	 */
	public boolean isBusy() {
	    synchronized(this) {
		return runnable.get() != null || !workQueue.isEmpty();
	    }
	}

	/**
	 * Submit work for this thread to do.
	 *
	 * @param	runnable	The work package to execute.
	 * @return			<code>false</code> if waiting for
	 *				the queue to be available is
	 *				interrupted, <code>true</code>
	 *				otherwise (i.e., the job was
	 *				accepted to be run).
	 */
	public boolean submitWork(Runnable runnable) {
	    synchronized(this) {
		if (terminated.get()) {
		    // Don't accept any more work once we have been terminated.
		    return false;
		}
		// Transfer this new work to the thread immediately, if possible,
		// but if we're still busy, just queue it.
		if (!workQueue.tryTransfer(runnable)) {
		    workQueue.put(runnable);
		}
	    }
	    logger.debug("submitWork successful: %1$s", runnable.toString());
	    return true;
	}

	/**
	 * Loops waiting for and then executing the {@link Runnable}s queued
	 * up via the {@link #submitWork} method.
	 */
	@Override
	public void run() {
	    logger.debug("Running %1$s...", getName());
	  executionLoop:
	    for( ; ; ) {

		// Make ourselves available on the external queue for receiving work
		if (workerQueue != null) {
		    workerQueue.add(this);
		}

		// Each time around, check the "terminated" flag
		if (terminated.get()) {
		    break executionLoop;
		}

		try {
		    logger.debug("Waiting for work...");
		    runnable.set(workQueue.take());
		    logger.debug("Took work package %1$s...", runnable.get().toString());
		}
		catch (InterruptedException ie) {
		    logger.debug("Interrupted while waiting for work.");
		    break executionLoop;
		}

		// One more check before we run the task to see if we have been terminated
		if (terminated.get()) {
		    logger.debug("Terminated. Breaking from execution loop.");
		    break executionLoop;
		}

		// We should have already been removed, by our caller who submitted
		// the work, from the "available workers" queue

		Runnable workPackage = runnable.get();
		if (workPackage != null) {
		    logger.debug("Running work package %1$s...", workPackage.toString());
		    try {
			workPackage.run();
			logger.debug("Finished work package %1$s.", workPackage.toString());
		    }
		    catch (Throwable ex) {
			logger.debug("Unhandled exception: %1$s", ExceptionUtil.toString(ex));
			Thread.UncaughtExceptionHandler eh = getUncaughtExceptionHandler();
			if (eh != null) {
			    eh.uncaughtException(this, ex);
			}
		    }
		    finally {
			synchronized (this) {
			    runnable.set(null);

			    // Once we're available for work again, add ourselves
			    // back to the queue (if we were indeed taken out, which should
			    // be the case).
			    if (workerQueue != null) {
				if (!workerQueue.contains(this)) {
				    workerQueue.add(this);
				}
			    }

			    // Then also notify any waiters that this thread is now available again
			    notifyAll();
			}
		    }
		}
	    }
	    runnable.set(null);
	    logger.debug("Finished execution loop, exiting %1$s.", getName());
	}

}

