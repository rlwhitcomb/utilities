/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2012,2014-2015,2021 Roger L. Whitcomb.
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
 *	This is the implementation of the Intl.Provider interface that uses
 *	the Pivot Resources class.
 *
 *  History:
 *	28-Oct-2012 (rlwhitcomb)
 *	    Massive rewrite of Intl to use the Provider interface.  This code
 *	    was moved from Intl into here to make it more general.
 *	07-May-2014 (rlwhitcomb)
 *	    Log the calling method when a resource is not found.
 *	16-Oct-2015 (rlwhitcomb)
 *	    Address Javadoc warnings found by Java 8.
 *	15-Apr-2021 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	15-Apr-2021 (rlwhitcomb)
 *	    Alternate constructors. Add "getResources()".
 *	22-Apr-2021 (rlwhitcomb)
 *	    Another alternate constructor.
 */
package info.rlwhitcomb;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.apache.pivot.json.JSON;
import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.util.Resources;

import info.rlwhitcomb.util.ClassUtil;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.Logging;


/**
 * Bridge class between our generic {@link Intl} resource provider and the
 * {@link Resources} class of Apache Pivot.
 * <p> This allows using the {@link Intl} interface to access either package resources
 * or Pivot resources in the same way.
 */
public class IntlProvider
	implements Intl.Provider
{
	/**
	 * The locale of the resources currently in effect.
	 */
	private Locale locale;
	/**
	 * The Pivot Resources we're currently using.
	 */
	private Resources resources;

	/**
	 * The {@link Logging} object for our local use.
	 */
	private static Logging log = new Logging(IntlProvider.class);



	/**
	 * Initialize our localization resources, using the default locale setting.
	 *
	 * @param	mainClass The starting point in the main .jar file where we can find
	 *			  our string resources (*.json files).
	 */
	public IntlProvider(final Class<?> mainClass) {
	    this(mainClass.getName(), Locale.getDefault());
	}

	/**
	 * Initialize our localization resources, using the given locale setting.
	 *
	 * @param	mainClass The starting point in the main .jar file where we can find
	 *			  our string resources (*.json files).
	 * @param	uiLocale  The locale to use to select the appropriate language.
	 */
	public IntlProvider(final Class<?> mainClass, final Locale uiLocale) {
	    this(mainClass.getName(), uiLocale);
	}

	/**
	 * Initialize our localization resources, using the default locale setting.
	 *
	 * @param	mainClassName	The starting point in the main .jar file where we can find
	 *				our string resources (*.json files).
	 */
	public IntlProvider(final String mainClassName) {
	    this(mainClassName, Locale.getDefault());
	}

	/**
	 * Initialize our localization resources.
	 *
	 * @param	mainClassName	The starting point in the main .jar file where we can find
	 *				our string resources (*.json files).
	 * @param	uiLocale	The locale to use to select the appropriate language.
	 */
	public IntlProvider(final String mainClassName, final Locale uiLocale) {
	    try {
		// First get the default (English) version, which is presumed to be complete
		Resources resParent = new Resources(null, mainClassName, Locale.US, StandardCharsets.UTF_8);
		if (uiLocale.equals(Locale.US)) {
		    resources = resParent;
		    log.info("Picking US English resources (%1$s) because our locale is US English", resources);
		} else {
		    resources = new Resources(resParent, mainClassName, uiLocale, StandardCharsets.UTF_8);
		    log.info("Picking '%1$s' resources (%2$s) with parent '%3$s'", uiLocale, resources, resParent);
		}
	    }
	    catch (SerializationException se) {
		log.except("IntlProvider init", se);
	    }
	    catch (IOException ioe) {
		log.except("IntlProvider init", ioe);
	    }
	}


	@Override
	public Locale getLocale() {
	    return locale;
	}

	@Override
	public Object getSource() {
	    return resources;
	}

	/**
	 * @return The source of our resources as the {@link Resources} object it really is.
	 */
	public Resources getResources() {
	    return resources;
	}

	/**
	 * Helper function to load a single string resource.
	 * <p> Just like the {@code BXMLSerializer}, if the given
	 * string resource does not exist the result is just the
	 * resource name (to avoid costly exceptions).  But the
	 * result is logged so a postprocessor can gather up these
	 * messages for analysis.
	 */
	@Override
	public String getString(final String resourceName) {
	    Object obj = JSON.get(resources, resourceName);
	    if (obj instanceof String) {
		return (String) obj;
	    }
	    else if (obj == null) {
		String method = ClassUtil.getCallingMethod(2);
		log.error("getString: Unknown string resource: '%1$s' referenced from %2$s", resourceName, method);
		return resourceName;
	    }
	    else {
		return obj.toString();
	    }
	}

	@Override
	public String getOptionalString(final String resourceName) {
	    Object obj = JSON.get(resources, resourceName);
	    if (obj instanceof String) {
		return (String) obj;
	    }
	    else if (obj == null) {
		return null;
	    }
	    else {
		return obj.toString();
	    }
	}

}
