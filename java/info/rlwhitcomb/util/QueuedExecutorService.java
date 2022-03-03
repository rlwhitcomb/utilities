/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013,2015,2018,2020-2022 Roger L. Whitcomb.
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
 *	An executor service that uses a Queued Thread to do its work.
 *
 * History:
 *	12-Feb-2013 (rlwhitcomb)
 *	    Initial implementation.
 *	22-Oct-2015 (rlwhitcomb)
 *	    Add missing copyright and history.
 *	22-Jan-2018 (rlwhitcomb)
 *	    Interrupt and terminate the queued thread on shutdown.
 *	06-Feb-2020 (rlwhitcomb)
 *	    Properly implement orderly "shutdown". Refactor a bit for that.
 *	    Do some logging, and implement "toString" for that purpose.
 *	16-Mar-2020 (rlwhitcomb)
 *	    Simplify logging a bit.
 *	28-Mar-2020 (rlwhitcomb)
 *	    The synchronization was all wrong, I think, so get rid of it.
 *	    And we really need to identify the thread we're using for all logging.
 *	19-Feb-2021 (rlwhitcomb)
 *	    Call "start()" on our thread ourselves.
 *	26-Feb-2022 (rlwhitcomb)
 *	    #204: Allow multiple thread setup.
 */
package info.rlwhitcomb.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * This class implements the {@link ExecutorService} interface via
 * a {@link QueuedThread} implementation.  This allows us to reuse
 * a single thread, which repeatedly accepts work.
 */
public class QueuedExecutorService extends AbstractExecutorService
{
	private boolean shutdown = false;
	private Deque<QueuedThread> queuedThreads = new ArrayDeque<>();
	private Logging logger = new Logging(QueuedExecutorService.class);


	private void addThread(final QueuedThread thread, final int threadNumber) {
	    QueuedThread newThread = thread == null ? new QueuedThread() : thread;

	    synchronized (queuedThreads) {
		queuedThreads.add(newThread);
		if (!newThread.isAlive()) {
		    newThread.start();
		}
	    }

	    if (threadNumber < 0)
		logger.info("Constructed for %1$s.", newThread.getName());
	    else
		logger.info("Constructed for thread #%1$d: %2$s", threadNumber, newThread.getName());
	}

	/**
	 * Start a new service with one anonymous thread, known only to us.
	 */
	public QueuedExecutorService() {
	    addThread(null, -1);
	}

	/**
	 * Start a new service with the one given thread (so caller can access it
	 * outside of us) (not sure when this is ever useful, but ...)
	 *
	 * @param thread The {@link QueuedThread} instance we use to do work.
	 */
	public QueuedExecutorService(final QueuedThread thread) {
	    addThread(thread, -1);
	}

	/**
	 * Start a new service with the given number of anonymous threads.
	 *
	 * @param n The number of threads to make available for this service.
	 * @throws IllegalArgumentException if the value of <code>n</code> is less than one.
	 */
	public QueuedExecutorService(final int n) {
	    if (n <= 0)
		throw new Intl.IllegalArgumentException("util#executor.wrongNumber");

	    for (int i = 0; i < n; i++) {
		addThread(null, i);
	    }
	}

	@Override
	public boolean awaitTermination(final long timeout, final TimeUnit unit)
		throws InterruptedException
	{
	    if (queuedThreads.isEmpty())
		return true;

	    long millis = unit.toMillis(timeout);

	    while (!queuedThreads.isEmpty()) {
		QueuedThread thread = null;
		synchronized (queuedThreads) {
		    thread = queuedThreads.pollFirst();
		}
		if (thread != null) {
		    thread.join(millis);
		}
	    }

	    return true;
	}

	/**
	 * Terminate all our worker threads, either immediately or after waiting
	 * for them to return to idle state.
	 *
	 * @param waitForIdle Whether to wait until the thread is not busy
	 * to notify it.
	 */
	private void terminate(final boolean waitForIdle) {
	    shutdown = true;

	    while (!queuedThreads.isEmpty()) {
		QueuedThread thread = null;

		synchronized (queuedThreads) {
		    thread = queuedThreads.pollFirst();
		}

		if (thread != null) {
		    String threadName = thread.getName();

		    synchronized (thread) {
			if (waitForIdle) {
			    // We need to wait for the thread to get quiet before
			    // setting the terminated flag, or else the last job
			    // to be submitted might never run, b/c of race condition
			    // with the "terminated" check after the "take" from the
			    // work queue.
			    if (thread.isBusy()) {
				logger.debug("%1$s is busy, waiting to finish...", threadName);
				try {
				    thread.wait();
				}
				catch (InterruptedException ie) {
				    logger.debug("Interrupted waiting on %1$s to finish.", threadName);
				}
			    }
			}
			logger.debug("Set terminated flag for %1$s.", threadName);
			thread.setTerminated();

			// Note: if we are waiting for the thread to become idle it will still
			// be "waiting" on the queue for work to do, so we still must interrupt
			// it for it to take notice of the new flag state.
			logger.debug("Interrupting %1$s.", threadName);
			thread.interrupt();
		    }
		}

		// If we waited for the thread then it may still be working on something,
		// so put it back on the queue to signal later.
		if (waitForIdle) {
		    synchronized (queuedThreads) {
			queuedThreads.addLast(thread);
		    }
		}
	    }
	}

	@Override
	public void shutdown() {
	    logger.info("shutdown() called for %1%d threads.", queuedThreads.size());
	    terminate(true);
	}

	@Override
	public List<Runnable> shutdownNow() {
	    logger.info("shutdownNow() called for %1%d threads.", queuedThreads.size());
	    terminate(false);
	    return new ArrayList<Runnable>();
	}

	@Override
	public boolean isShutdown() {
	    return shutdown;
	}

	@Override
	public boolean isTerminated() {
	    return queuedThreads.isEmpty();
	}

	@Override
	public void execute(Runnable command) {
	    if (!shutdown && !queuedThreads.isEmpty()) {
		synchronized (queuedThreads) {
		    if (queuedThreads.size() == 1) {
			QueuedThread thread = queuedThreads.peekFirst();
			thread.submitWork(command);
		    }
		    else {
			QueuedThread thread = queuedThreads.pollFirst();
			thread.submitWork(command);
			queuedThreads.addLast(thread);
		    }
		}
	    }
	}

	@Override
	public String toString() {
	    synchronized (queuedThreads) {
		if (queuedThreads.size() == 1)
		    return String.format("%1$s for %2$s", getClass().getSimpleName(), queuedThreads.peekFirst().getName());
		else
		    return String.format("%1$s for %2$d threads", getClass().getSimpleName(), queuedThreads.size());
	    }
	}

}

