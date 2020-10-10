/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013,2015,2018,2020 Roger L. Whitcomb.
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
 */
package info.rlwhitcomb.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This class implements the {@link ExecutorService} interface via
 * a {@link QueuedThread} implementation.  This allows us to reuse
 * a single thread, which repeatedly accepts work.  The use of a
 * single thread allows the remote server instance to be kept open
 * which (should) minimize the connection overhead to run a query.
 */
public class QueuedExecutorService extends AbstractExecutorService
{
	private boolean shutdown = false;
	private QueuedThread queuedThread;
	private Logging logger = new Logging(QueuedExecutorService.class);

	/**
	 * Start a new service with an anonymous thread, known only to us.
	 */
	public QueuedExecutorService() {
	    this(new QueuedThread());
	}

	/**
	 * Start a new service with the given thread (so caller can access it
	 * outside of us) (not sure when this is ever useful, but ...)
	 *
	 * @param thread The {@link QueuedThread} instance we use to do work.
	 */
	public QueuedExecutorService(QueuedThread thread) {
	    this.queuedThread = thread;
	    logger.info("Constructed for %1$s.", threadName());
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit)
		throws InterruptedException
	{
	    return true;
	}

	/**
	 * Terminate our worker thread, either immediately or after waiting
	 * for it to return to idle state.
	 *
	 * @param waitForIdle Whether to wait until the thread is not busy
	 * to notify it.
	 */
	private void terminate(boolean waitForIdle) {
	    shutdown = true;

	    if (queuedThread != null) {
		String threadName = threadName();
		synchronized (queuedThread) {
		    if (waitForIdle) {
			// We need to wait for the thread to get quiet before
			// setting the terminated flag, or else the last job
			// to be submitted might never run, b/c of race condition
			// with the "terminated" check after the "take" from the
			// work queue.
			if (queuedThread.isBusy()) {
			    logger.debug("%1$s is busy, waiting to finish...", threadName);
			    try {
				queuedThread.wait();
			    }
			    catch (InterruptedException ie) {
				logger.debug("Interrupted waiting on %1$s to finish.", threadName);
			    }
			}
		    }
		    logger.debug("Set terminated flag for %1$s.", threadName);
		    queuedThread.setTerminated();
		    // Note: if we are waiting for the thread to become idle it will still
		    // be "waiting" on the queue for work to do, so we still must interrupt
		    // it for it to take notice of the new flag state.
		    logger.debug("Interrupting %1$s.", threadName);
		    queuedThread.interrupt();
		}
	    }

	    if (!waitForIdle) {
		queuedThread = null;
	    }
	}

	@Override
	public void shutdown() {
	    logger.info("shutdown() called for %1$s.", threadName());
	    terminate(true);
	    // Note: leave the queuedThread variable set here so that
	    // "shutdownNow()" has something to notify.
	}

	@Override
	public List<Runnable> shutdownNow() {
	    logger.info("shutdownNow() called for %1$s.", threadName());
	    terminate(false);
	    return new ArrayList<Runnable>();
	}

	@Override
	public boolean isShutdown() {
	    return shutdown;
	}

	@Override
	public boolean isTerminated() {
	    return queuedThread == null || !queuedThread.isAlive();
	}

	@Override
	public void execute(Runnable command) {
	    if (!shutdown && queuedThread != null) {
		queuedThread.submitWork(command);
	    }
	}

	private String threadName() {
	    return queuedThread != null ? queuedThread.getName() : "<null>";
	}

	@Override
	public String toString() {
	    return String.format("%1$s for %2$s", getClass().getSimpleName(), threadName());
	}

}

