/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013,2015,2022 Roger L. Whitcomb.
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
 *	Adapter in order to use Commons Logging API over our homegrown Logging module.
 *
 * History:
 *  xx-May-13 rlw  ---	Rewritten from the first attempt to make Logging itself
 *			into a Commons Logging implementation.  This had the
 *			unfortunate complication of needing the commons-logging.jar
 *			file all the time, even if we're not really using this class
 *			except in some GUI situations.
 *  08-Oct-15 rlw  ---	Address Javadoc warnings found by Java 8.
 *  12-Oct-22 rlw #513:	Move to new package.
 */

package info.rlwhitcomb.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Adapter to allow use of Commons Logging API with our homegrown {@link Logging} system.
 * <p> This class conforms to the Commons Logging interface, and accepts registration as a
 * {@link LogFactory} to provide logging instances to those facilities that understand this API.
 * Typically this would be done in the configuration file with a line like this:
 * <pre>org.apache.commons.logging.LogFactory = info.rlwhitcomb.logging.CommonsLoggingAdapter$Factory</pre>
 * The {@link Log} interface provides one additional level (<tt>trace</tt>) that we don't support,
 * so we will equate it to the {@link #DEBUG} level (at least for now).  If this class is setup
 * as a <tt>LogFactory</tt> interface then it will accept configuration of the usual attributes
 * via the {@link LogFactory#setAttribute} method.
 */
public class CommonsLoggingAdapter extends Logging implements Log
{
	/**
	 * Factory class that instantiates instances of {@link CommonsLoggingAdapter} for use
	 * by Commons Logging clients.
	 */
	public static class Factory extends LogFactory
	{
		/**
		 * Get the named logging attribute.
		 * @param	name	Attribute name to fetch.
		 * @see		#getAttributeNames
		 * @see		#setAttribute
		 */
		@Override
		public Object getAttribute(String name) {
		    if (name.startsWith(loggingClassName)) {
			name = name.substring(loggingClassName.length());
		    }
		    if (name.equalsIgnoreCase(LOGGING_LEVEL)) {
			return getLoggingLevel();
		    }
		    else if (name.equalsIgnoreCase(LOG_DIRECTORY)) {
			return logFileDir;
		    }
		    else if (name.equalsIgnoreCase(LOG_FILE)) {
			return logFileTemplate;
		    }
		    else if (name.equalsIgnoreCase(LOG_TO_CONSOLE)) {
			return Boolean.valueOf(logToConsole);
		    }
		    return null;
		}

		/** The list of supported attributes. */
		private static String[] attributeNames = {
			LOGGING_LEVEL,
			LOG_DIRECTORY,
			LOG_FILE,
			LOG_TO_CONSOLE
		};

		/**
		 * Get the list of currently defined logging attributes.
		 * @return	The list of currently supported attributes.
		 * @see		#attributeNames
		 * @see		#getAttribute
		 * @see		#setAttribute
		 */
		@Override
		public String[] getAttributeNames() {
		    return attributeNames;
		}


		/**
		 * Remove the given logging attribute.
		 * @param	name	The name of the attribute to remove.
		 * @throws	UnsupportedOperationException because we ... don't support this operation.
		 */
		@Override
		public void removeAttribute(String name) {
		    throw new UnsupportedOperationException("Removing logging attributes is not supported.");
		}


		/**
		 * Set the given logging attribute.
		 * @param	name	Name of the attribute to set.
		 * @param	obj	Value of this attribute to set.
		 * @see		#getAttribute
		 * @see		#getAttributeNames
		 */
		@Override
		public void setAttribute(String name, Object obj) {
		    String value = obj.toString();
		    if (name.startsWith(loggingClassName)) {
			name = name.substring(loggingClassName.length());
		    }
		    if (name.equalsIgnoreCase(LOGGING_LEVEL)) {
			setLoggingLevel(value);
		    }
		    else if (name.equalsIgnoreCase(LOG_DIRECTORY)) {
			if (logFileTemplate == null)
			    setLogFile(value, "daily-" + DATE_TOKEN + ".log");
			else
			    setLogFile(value, logFileTemplate);
		    }
		    else if (name.equalsIgnoreCase(LOG_FILE)) {
			if (logFileDir == null)
			    setLogFile(".", value);
			else
			    setLogFile(logFileDir, value);
		    }
		    else if (name.equalsIgnoreCase(LOG_TO_CONSOLE)) {
			setConsoleLogging(Boolean.parseBoolean(value));
		    }
		}


		/**
		 * Get a new logging instance for the given class.
		 * @param	clazz	Client class to use to annotate this
		 *			logging instance.
		 * @return	A new instance for the given class.
		 */
		@Override
		public Log getInstance(Class clazz) {
		    return new CommonsLoggingAdapter(clazz);
		}


		/**
		 * Get a new logging instance for the given name.
		 * @param	name	Name to be used to annotate this
		 *			logging instance.
		 * @return	The new instance.
		 */
		@Override
		public Log getInstance(String name) {
		    return new CommonsLoggingAdapter(name);
		}


		/**
		 * Release any internal references to previously created
		 * logging instances.
		 */
		@Override
		public void release() {
		}
	}


	/**
	 * Constructor used by {@link Factory} to create instances.
	 *
	 * @param	name	The prefix name to be used for this logging
	 *			instance (printed with each message).
	 */
	private CommonsLoggingAdapter(String name) {
	    super(name);
	}


	/**
	 * Constructor used by {@link Factory} to create instances.
	 *
	 * @param	clazz	The client class to begin logging for
	 *			(determines the prefix used in the log
	 *			messages).
	 */
	private CommonsLoggingAdapter(Class clazz) {
	    super(clazz);
	}


	/**
	 * Log a <tt>TRACE</tt> level statement.
	 *
	 * @param	message	The message to be logged at {@code TRACE} level.
	 */
	@Override
	public void trace(Object message) {
	    log(DEBUG, "%1$s", message);
	}

	/**
	 * Log a <tt>TRACE</tt> level error.
	 *
	 * @param	message	Message to be logged along with this error.
	 * @param	t	The error to be logged ({@code TRACE} level).
	 */
	@Override
	public void trace(Object message, Throwable t) {
	    if (isTraceEnabled())
		if (message == null)
		    except(t);
		else
		    except(message.toString(), t);
	}


	/**
	 * Log a {@link #DEBUG} level statement.
	 *
	 * @param	message	The message to be logged at {@code DEBUG} level.
	 */
	@Override
	public void debug(Object message) {
	    log(DEBUG, "%1$s", message);
	}

	/**
	 * Log a {@link #DEBUG} level error.
	 *
	 * @param	message	Message to be logged along with this error.
	 * @param	t	The error to be logged ({@code DEBUG} level).
	 */
	@Override
	public void debug(Object message, Throwable t) {
	    if (isDebugEnabled())
		if (message == null)
		    except(t);
		else
		    except(message.toString(), t);
	}


	/**
	 * Log an {@link #INFO} level statement.
	 *
	 * @param	message	The message to be logged at {@code INFO} level.
	 */
	@Override
	public void info(Object message) {
	    log(INFO, "%1$s", message);
	}

	/**
	 * Log an {@link #INFO} level error.
	 *
	 * @param	message	Message to be logged along with this error.
	 * @param	t	The error to be logged ({@code INFO} level).
	 */
	@Override
	public void info(Object message, Throwable t) {
	    if (isInfoEnabled())
		if (message == null)
		    except(t);
		else
		    except(message.toString(), t);
	}


	/**
	 * Log a {@link #WARN} level statement.
	 *
	 * @param	message	The message to be logged at {@code WARN} level.
	 */
	@Override
	public void warn(Object message) {
	    log(WARN, "%1$s", message);
	}

	/**
	 * Log a {@link #WARN} level error.
	 *
	 * @param	message	Message to be logged along with this error.
	 * @param	t	The error to be logged ({@code WARN} level).
	 */
	@Override
	public void warn(Object message, Throwable t) {
	    if (isWarnEnabled())
		if (message == null)
		    except(t);
		else
		    except(message.toString(), t);
	}


	/**
	 * Log an {@link #ERROR} level statement.
	 *
	 * @param	message	The message to be logged at {@code ERROR} level.
	 */
	@Override
	public void error(Object message) {
	    logError(ERROR, "%1$s", message);
	}

	/**
	 * Log an {@link #ERROR} level error.
	 *
	 * @param	message	Message to be logged along with this error.
	 * @param	t	The error to be logged ({@code ERROR} level).
	 */
	@Override
	public void error(Object message, Throwable t) {
	    if (isErrorEnabled())
		if (message == null)
		    except(t);
		else
		    except(message.toString(), t);
	}


	/**
	 * Log a {@link #FATAL} level statement in an instance context.
	 *
	 * @param   fmt     what you want to log if <code>FATAL</code> logging is enabled
	 * @param   args    arguments to substitute for the %nn$f placeholders in the fmt string
	 */
	public void fatal(String fmt, Object ... args) {
	    logError(FATAL, fmt, args);
	}

	/**
	 * Log a {@link #FATAL} level statement.
	 *
	 * @param	message	The message to be logged at {@code FATAL} level.
	 */
	@Override
	public void fatal(Object message) {
	    logError(FATAL, "%1$s", message);
	}

	/**
	 * Log a {@link #FATAL} level error.
	 *
	 * @param	message	Message to be logged along with this error.
	 * @param	t	The error to be logged ({@code FATAL} level).
	 */
	@Override
	public void fatal(Object message, Throwable t) {
	    if (isFatalEnabled())
		if (message == null)
		    except(t);
		else
		    except(message.toString(), t);
	}


	/**
	 * Check if the <tt>TRACE</tt> level logging is enabled.
	 * This is not supported directly by our homegrown logger,
	 * so is equated to {@link #DEBUG} level.
	 */
	@Override
	public boolean isTraceEnabled() {
	    return Logging.isLevelEnabled(DEBUG);
	}


}
