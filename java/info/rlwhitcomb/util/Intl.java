/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2012-2018,2020-2021 Roger L. Whitcomb.
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
 *	Manage the internationalizable resources of the application.
 *
 *  History:
 *	17-Apr-2012 (rlwhitcomb)
 *	    Initial version.
 *	28-Oct-2012 (rlwhitcomb)
 *	    Massive rewrite to use a Provider interface so we can take the
 *	    Pivot dependencies out and use other sources.
 *	01-Nov-2012 (rlwhitcomb)
 *	    Add a high-level function to print a number of "help" lines for
 *	    a command-line application; add other helpers to print/format
 *	    strings to System.out and System.err.  Catch exception for
 *	    missing resources and just return the key (plus log the error).
 *	14-May-2013 (rlwhitcomb)
 *	    Add two methods to call the Launcher.startupError method with one
 *	    of our resourced strings.
 *	25-Jun-2013 (rlwhitcomb)
 *	    Add mechanism for deriving the package name from a "pkg#" prefix
 *	    to the key string; amend all the default provider methods to use
 *	    this so we can simplify mixing/matching default resource keys and
 *	    package resource keys.  So, taking out the Package-based methods.
 *	08-Oct-2013 (rlwhitcomb)
 *	    Delay creating a Logging object until absolutely needed.  We
 *	    often get called before the log system is even initialized
 *	    which can cause problems.
 *	06-Jan-2014 (rlwhitcomb)
 *	    Clarify some error messages with the resource bundle name also.
 *	07-Jan-2014 (rlwhitcomb)
 *	    Add a method to log the registered resource bundles (for debug purposes).
 *	07-May-2014 (rlwhitcomb)
 *	    Log the calling method when a resource is not found.
 *	08-Sep-2014 (rlwhitcomb)
 *	    Use new method to get the right class loader.  Adapt to the new
 *	    return value (URL) from Launcher method.  Able to load resources
 *	    from either a "file:" URL or an HTTP "jar:" URL.
 *	06-Nov-2014 (rlwhitcomb)
 *	    Move the error message strings into our local resource bundle.
 *	07-Jan-2015 (rlwhitcomb)
 *	    Add an accessor method for the Provider map so we can do reverse
 *	    checks on the resource bundles in VerifyText.
 *	14-Jan-2015 (rlwhitcomb)
 *	    Add flavors of "getString", "formatString", and "getKeyString"
 *	    that take a StringBuilder and append the result to it (convenience
 *	    in many places).
 *	14-Jan-2015 (rlwhitcomb)
 *	    Rename the parameters that can be either a straight text message
 *	    or a resource key so this is more clear.
 *	08-Apr-2015 (rlwhitcomb)
 *	    Avoid infinite recursion if our package has not been loaded.
 *	28-Apr-2015 (rlwhitcomb)
 *	    Tiny cleanup of logging information.
 *	28-Aug-2015 (rlwhitcomb)
 *	    I don't know why we are loading all the dependent jar files right
 *	    at startup -- none of our 3rd party jars have any text resources we
 *	    need to worry about.
 *	16-Oct-2015 (rlwhitcomb)
 *	    Fix Javadoc warnings found by Java 8.
 *	07-Jan-2016 (rlwhitcomb)
 *	    More Javadoc work in the new year.
 *	05-Jul-2016 (rlwhitcomb)
 *	    Add a new "installAllPackages" to PackageResourceProvider that searches
 *	    subdirectories and installs all the resources it can find.
 *	    Change HashMap to LinkedHashMap so when we enumerate the packages they
 *	    come out in the order they were added.
 *	21-Mar-2017 (rlwhitcomb)
 *	    New flavors of initialization such that it is more automatic than ever,
 *	    for both the .jar file case and the flat file case.
 *	12-Apr-2017 (rlwhitcomb)
 *	    Catch the UnsatisfiedLinkError calling the Launcher methods and just
 *	    display on the console in that case.
 *	28-Jul-2017 (rlwhitcomb)
 *	    In order to avoid having all main programs (or other users)
 *	    call the "initAllPackageResources" method, just do it ourselves in a
 *	    static initialization block.
 *	31-Jul-2017 (rlwhitcomb)
 *	    Need to initialize any internationalized resource bundles, which will
 *	    have "resources*.properties" names (such as "resources_ja.properties").
 *	12-Jun-2018 (rlwhitcomb)
 *	    Needed to update the locale inside "updateResources". Fix the
 *	    recognition of resource files inside the .jar to pick up all
 *	    the locales.
 *	26-Jun-2018 (rlwhitcomb)
 *	    Add new "getNumber" and "getInt" methods so we can get numeric values
 *	    easily out of the resources (such as for dialog sizes for translation
 *	    differences).
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	11-Sep-2020 (rlwhitcomb)
 *	    Allow "printHelp" to not have the number of lines, but just stop when
 *	    the next line number can't be found.
 *	    Rework to add "getOptionalString" to the Provider interface, and then
 *	    redo all the various methods to call helpers so that either "getString"
 *	    or "getOptionalString" can be called (without duplicating a lot of logic).
 *	    Variants of "getNumber" and "getInt" with default values if the string
 *	    can't be found.
 *	    Make all parameters final.
 *	13-Nov-2020 (rlwhitcomb)
 *	    Add variants of "printHelp" to print to an alternate PrintStream
 *	    (System.err or System.out for instance). Add empty param versions of
 *	    "outPrintln" and "errPrintln" (for convenience).
 *	21-Dec-2020 (rlwhitcomb)
 *	    Update obsolete Javadoc constructs.
 *	18-Jan-2021 (rlwhitcomb)
 *	    Add "formatKeyString" methods.
 *	18-Jan-2021 (rlwhitcomb)
 *	    New "out/errKeyFormat" methods.
 *	22-Jan-2021 (rlwhitcomb)
 *	    Fixed a glitch in "makeKey" for help display.
 *	29-Jan-2021 (rlwhitcomb)
 *	    For convenience, define subclasses of IllegalArgumentException,
 *	    IllegalStateException, IndexOutOfBoundsException, and UnsupportedOperationException
 *	    that accept keys and do the lookup or formatting before sending to their superclasses.
 *	09-Feb-2021 (rlwhitcomb)
 *	    Add options to "printHelp" to color the messages or not.
 *	10-Feb-2021 (rlwhitcomb)
 *	    Add the color map to the "printHelp" params.
 *	09-Jul-2021 (rlwhitcomb)
 *	    Final class and private constructor.
 *	21-Oct-2021 (rlwhitcomb)
 *	    Convenience class for getting and validating a Locale.
 *	    Allow null or empty string for locale to specify the default.
 *	28-Nov-2021 (rlwhitcomb)
 *	    #111: Add "getOptionalKeyString", make "isAKey" into a separate method.
 *	    Rework the color map to be Map<String, Object>.
 */
package info.rlwhitcomb.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.IllformedLocaleException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.rlwhitcomb.jarfile.Launcher;
import info.rlwhitcomb.util.ConsoleColor.Code;


/**
 * Helper class to fetch localized string resources.
 * <p> This class uses the nested <code>Provider</code> interface
 * so that it can be used in several different ways.  For use with
 * the Pivot <code>Resources</code> class without having to reference
 * Pivot classes or so that we can use ResourceBundles (or both).
 */
public final class Intl
{
	/**
	 * The default package prefix if there is none on a "pkg#" key prefix.
	 */
	private static final String PKG_PREFIX = "info.rlwhitcomb";
	/**
	 * Our package name (for testing purposes).
	 */
	private static final String OUR_PKG_NAME = Intl.class.getPackage().getName();
	/**
	 * The error prefix printed when Launcher methods cannot be found and errors
	 * are printed to console.
	 */
	private static final String ERROR = "Error: ";
	/**
	 * A pattern to match resource bundle names.
	 */
	private static final Pattern RESOURCES_PATTERN = Pattern.compile("(.*)[/\\\\]resources.*\\.properties");

	/**
	 * Interface to provide the source for the localized resources.
	 * This source can be Pivot Resources, Java Resource Bundles, or
	 * whatever source you want.
	 */
	public static interface Provider
	{
		/**
		 * @return The real source object that provides the resources.
		 */
		public Object getSource();
		/**
		 * @return The current locale being used to select the resource translation in effect.
		 */
		public Locale getLocale();
		/**
		 * The real workhorse method.
		 * Note: missing resources are logged as errors.
		 *
		 * @param	resourceName The key for the resource we need.
		 * @return	The resource string corresponding to the given key.
		 */
		public String getString(String resourceName);
		/**
		 * A variant of our basic method that returns {@code null} if the string
		 * can't be found (in other words, it is optional). No errors are logged
		 * for missing resources, since they are (by definition) okay if not found.
		 *
		 * @param	resourceName The key for the resource we need.
		 * @return	The resource string for the given key or {@code null}
		 *		if the resource is not available.
		 */
		public String getOptionalString(String resourceName);
	}


	/**
	 * The map of package/class providers.
	 */
	private static Map<String, Provider> providerMap = new LinkedHashMap<>();


	/**
	 * Allow access to the {@link #providerMap} for purposes of traversing the providers.
	 * @return	Current map of resource providers.
	 */
	public static Map<String, Provider> getProviderMap() {
	    return providerMap;
	}


	/**
	 * Generic {@link ResourceBundle} provider.
	 */
	public static class ResourceBundleProvider
		implements Provider
	{
		protected ResourceBundle resources;
		protected Locale locale;
		protected String name;

		protected ResourceBundleProvider() {
		}

		private ResourceBundleProvider(final String name, final Locale locale,
			final ClassLoader classLoader) {
		    this.name = name;
		    providerMap.put(name, this);
		    updateResources(name, locale, classLoader);
		}

		protected void updateResources(final String name, final Locale locale,
			final ClassLoader classLoader) {
		    this.locale = locale;
		    this.resources = ResourceBundle.getBundle(String.format("%1$s.resources", name), locale, classLoader);
		}

		protected static void install(final String name, final Locale locale,
			final ClassLoader classLoader) {
		    ResourceBundleProvider provider = (ResourceBundleProvider)providerMap.get(name);
		    if (provider == null) {
			new ResourceBundleProvider(name, locale, classLoader);
		    }
		    else if (!provider.getLocale().equals(locale)) {
			provider.updateResources(name, locale, classLoader);
		    }
		}

		@Override
		public Object getSource() {
		    return resources;
		}

		@Override
		public Locale getLocale() {
		    return locale;
		}

		/**
		 * Lookup the resource string for the given name.
		 * Note: it is the responsibility of the caller to log an error
		 *  (if desired) for a missing resource.
		 * @param	resourceName	The fully-qualified resource key.
		 * @return	The string for that key, or null if not found.
		 * @throws	MissingResourceException if there is no object
		 *		for the given key.
		 */
		private String getResourceString(final String resourceName) {
		    try {
			Object obj = resources.getObject(resourceName);
			if (obj instanceof String) {
			    String string = (String)obj;
			    // If a string needs leading or trailing blanks, then it must be
			    // quoted in the resource bundle, so strip these quotes
			    if (!string.isEmpty()) {
				int len = string.length();
				if (string.charAt(0) == '\"' && string.charAt(len - 1) == '\"') {
				    string = string.substring(1, len - 1);
				}
			    }
			    return string;
			}
			else if (obj != null) {
			    return obj.toString();
			}
		    }
		    catch (MissingResourceException mre) {
			throw mre;
		    }

		    return null;
		}

		@Override
		public String getString(final String resourceName) {
		    try {
			String resourceString = getResourceString(resourceName);

			if (resourceString == null) {
			    String method = ClassUtil.getCallingMethod(2);
			    getLog().error(
				    "getString: Unknown string resource: \"%1$s#%2$s\", "
				  + "referenced from %3$s",
				    this.name, resourceName, method);
			    return resourceName;
			}

			return resourceString;
		    }
		    catch (MissingResourceException mre) {
			String method = ClassUtil.getCallingMethod(2);
			getLog().except(String.format(
				"getString: Unknown string resource \"%1$s#%2$s\", "
			      + "referenced from %3$s",
				this.name, resourceName, method), mre);
			return resourceName;
		    }
		}

		@Override
		public String getOptionalString(final String resourceName) {
		    try {
			return getResourceString(resourceName);
		    }
		    catch (MissingResourceException mre) {
			return null;
		    }
		}
	}


	/**
	 * A file filter that accepts subdirectories or regular files named {@code "resources*.properties"}
	 * (but only if they can be read by the current user).
	 */
	private static class ResourceFilter implements FileFilter
	{
		@Override
		public boolean accept(final File path) {
		    if (path.exists() && path.canRead()) {
			if (path.isDirectory()) {
			    return true;
			}
			else {
			    Matcher m = RESOURCES_PATTERN.matcher(path.getPath());
			    if (m.matches())
				return true;
			}
		    }
		    return false;
		}
	}


	/**
	 * Provider that loads resources from a {@link ResourceBundle} named
	 * for a specific package.
	 */
	public static class PackageResourceProvider
		extends ResourceBundleProvider
	{
		private static FileFilter resourceFilter = new ResourceFilter();

		public static void install(final String pkgName, final Locale locale) {
		    install(pkgName, locale, ClassUtil.getClassLoader());
		}

		private static void installFromOneDirectory(final File dir, final Locale locale,
			final FileFilter filter) {
		    File[] files = dir.listFiles(filter);
		    // Process this list twice, first time installing packages from the current directory
		    // then the second time recursing through subdirectories.
		    for (File f : files) {
			if (f.isFile()) {
			    // Remove any relative path spec at the beginning
			    String parentName = f.getParent().replaceFirst("\\.\\.?[/\\\\]", "");
			    String pkgName = parentName.replaceAll("[/\\\\]", ".");
			    install(pkgName, locale);
			    // The ResourceBundle mechanism will find the right file in this directory
			    // according to the locale specified, so as soon as we install once from here
			    // then we can go onto the next directory
			    break;
			}
		    }
		    for (File f : files) {
			if (f.isDirectory()) {
			    installFromOneDirectory(f, locale, filter);
			}
		    }
		}

		/**
		 * Search the given root directory and all subdirectories looking for
		 * "resources.properties" files and install them all.
		 *
		 * @param dir		The starting directory location for the search.
		 * @param locale	The {@link Locale} to use.
		 * @throws		IllegalArgumentException if the given directory name
		 *			does not exist, is not a directory, or can't be read
		 *			by the current user.
		 */
		public static void installAllPackages(final File dir, final Locale locale) {
		    if (dir.exists() && dir.isDirectory() && dir.canRead()) {
			installFromOneDirectory(dir, locale, resourceFilter);
		    }
		    else {
			throw new IllegalArgumentException(
				String.format("installAllPackages: the directory \"%1$s\" either cannot be found or cannot be read.", dir.getPath()));
		    }
		}

		/**
		 * Search the given root directory and all subdirectories looking for
		 * "resources.properties" files and install them all.
		 *
		 * @param rootDir	The starting directory name for the search.
		 * @param locale	The {@link Locale} to use.
		 * @throws		IllegalArgumentException if the given directory name
		 *			does not exist, is not a directory, or can't be read
		 *			by the current user.
		 */
		public static void installAllPackages(final String rootDir, final Locale locale) {
		    installAllPackages(new File(rootDir), locale);
		}
	}


	/**
	 * Used by the {@link #getProviderKey} method to return both a provider
	 * and a possibly modified key with the provider prefix stripped out.
	 */
	private static class ProviderKeyPair
	{
		Provider provider;
		String key;

		ProviderKeyPair(final Provider newProvider, final String newKey) {
		    provider = newProvider;
		    key      = newKey;
		}
	}


	/**
	 * Subclass of {@link java.lang.IllegalArgumentException} that simply provides a shortcut
	 * when the message should come from the resources.
	 */
	public static class IllegalArgumentException extends java.lang.IllegalArgumentException
	{
		public IllegalArgumentException(final String keyString) {
		    super(getString(keyString));
		}

		public IllegalArgumentException(final String formatKey, Object... args) {
		    super(formatString(formatKey, args));
		}

		public IllegalArgumentException(final String keyString, Throwable cause) {
		    super(getString(keyString), cause);
		}
	}


	/**
	 * Subclass of {@link java.lang.IllegalStateException} that simply provides a shortcut
	 * when the message should come from the resources.
	 */
	public static class IllegalStateException extends java.lang.IllegalStateException
	{
		public IllegalStateException(final String keyString) {
		    super(getString(keyString));
		}

		public IllegalStateException(final String formatKey, Object... args) {
		    super(formatString(formatKey, args));
		}

		public IllegalStateException(final String keyString, Throwable cause) {
		    super(getString(keyString), cause);
		}
	}


	/**
	 * Subclass of {@link java.lang.IndexOutOfBoundsException} that simply provides a shortcut
	 * when the message should come from the resources.
	 */
	public static class IndexOutOfBoundsException extends java.lang.IndexOutOfBoundsException
	{
		public IndexOutOfBoundsException(final String keyString) {
		    super(getString(keyString));
		}

		public IndexOutOfBoundsException(final String formatKey, Object... args) {
		    super(formatString(formatKey, args));
		}
	}


	/**
	 * Subclass of {@link java.lang.UnsupportedOperationException} that simply provides a shortcut
	 * when the message should come from the resources.
	 */
	public static class UnsupportedOperationException extends java.lang.UnsupportedOperationException
	{
		public UnsupportedOperationException(final String keyString) {
		    super(getString(keyString));
		}

		public UnsupportedOperationException(final String formatKey, Object... args) {
		    super(formatString(formatKey, args));
		}

		public UnsupportedOperationException(final String keyString, Throwable cause) {
		    super(getString(keyString), cause);
		}
	}


	/**
	 * The default static provider.
	 */
	private static Provider defaultProvider = null;


	/**
	 * The logging object.
	 */
	private static Logging log = null;


	/**
	 * Initialize all of our resource bundles automatically with the default locale
	 * the first time we are called.  Note: {@link #initAllPackageResources} can
	 * be called multiple times with different locales, so programs (for instance)
	 * which can specify the Locale name as a command-line parameter will work
	 * correctly, even with this static initialization.
	 */
	static {
	    initAllPackageResources(Locale.getDefault());
	}


	/**
	 * Private constructor for this static class.
	 */
	private Intl() {
	}


	/**
	 * Get the current logging object (create if necessary).
	 * <p> Normally we would just create a static logger, but there is
	 * a chicken-and-egg problem here during startup.  The logger might
	 * need error message resources during initialization, but we might
	 * need logging resources too.  So we get into a infinite loop.  So,
	 * just create a logger at the point of need.
	 *
	 * @return	The current object for logging.
	 */
	private static Logging getLog() {
	    if (log == null) {
		log = new Logging(Intl.class);
	    }
	    return log;
	}

	/**
	 * Logs the currently registered resource bundle providers.
	 * <p> Should only be called once during startup, after all the
	 * .jar resources have been registered, but after the {@link Logging}
	 * subsystem has been initialized.
	 */
	public static void logProviders() {
	    StringBuilder list = new StringBuilder();
	    list.append("\n======= List of available Resource Bundles =======\n");
	    for (String key : providerMap.keySet())
		list.append("Package: ").append(key).append('\n');
	    list.append("======= End of list =======");
	    getLog().info(list.toString());
	}

	/**
	 * Reference the resources.
	 * @return	The source object from the default resource provider.
	 */
	public static Object getSource() {
	    return defaultProvider.getSource();
	}


	/**
	 * Validate a user's choice of {@link Locale} to see if it is valid.
	 *
	 * @param tag	A candidate locale name to be tested. A {@code null} or
	 *		empty string will return the default Locale for the system.
	 * @return	The locale object if the name is valid.
	 * @throws	IllegalArgumentException if the locale name is invalid
	 *		or does not reference a valid locale.
	 */
	public static Locale getValidLocale(final String tag) {
	    if (tag == null || tag.trim().isEmpty()) {
		return Locale.getDefault();
	    }

	    try {
		Locale.Builder builder = new Locale.Builder();
		builder.setLanguageTag(tag);
		Locale locale = builder.build();
		if (locale.getISO3Language() != null && locale.getISO3Country() != null) {
		    return locale;
		}
		throw new IllegalArgumentException("util#intl.invalidLocale", tag);
	    }
	    catch (MissingResourceException | IllformedLocaleException ex) {
		throw new IllegalArgumentException("util#intl.localeError", tag, ExceptionUtil.toString(ex));
	    }
	}


	/**
	 * Reference the locale for the resources.
	 * @return	The locale from the default provider.
	 */
	public static Locale getLocale() {
	    return defaultProvider.getLocale();
	}


	/**
	 * Initialize our localization resources.
	 * @param	provider	The default resource provider.
	 */
	public static void initResources(final Provider provider) {
	    defaultProvider = provider;
	}


	public static void initJarResources(final URL fileURL, final Locale locale) {
	    try {
		// Enumerate all the "resources*.properties" files in this .jar
		// and register their package names.
		String fileURLString = fileURL.toString();
		JarFile jarFile = Launcher.getJarFile(fileURL);
		for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements(); ) {
		    JarEntry entry = e.nextElement();
		    String name = entry.getName();
		    Matcher m = RESOURCES_PATTERN.matcher(name);
		    if (m.matches()) {
			String path = m.group(1);
			PackageResourceProvider.install(path.replace('/', '.'), locale);
		    }
		}
	    }
	    catch (IOException ex) {
		getLog().except("Intl initJarResources", ex);
	    }
	}


	/**
	 * Initialize all the package resources in the current .jar file
	 * and all .jar files listed in the class path of the manifest.
	 * <p> Also supports initialization from a build area, where we don't
	 * live inside a .jar file, by calling
	 * {@link PackageResourceProvider#installAllPackages(File, Locale)}.
	 *
	 * @param	locale	The current locale, used to select the language.
	 */
	public static void initAllPackageResources(final Locale locale) {
	    URL ourJarFile = Launcher.getOurJarFile();
	    File plainFile = Launcher.urlToPlainFile(ourJarFile);
	    if (plainFile == null || plainFile.isFile()) {
		initJarResources(ourJarFile, locale);
	    }
	    else {
		PackageResourceProvider.installAllPackages(plainFile, locale);
	    }
	}


	/**
	 * Look up the provider for the given resource name, and return both
	 * the provider (if found), and the now possible modified resource name
	 * needed to look up the string from that provider.
	 *
	 * @param	resourceName	The key name of the resource to fetch.
	 * @return	The provider/key pair needed to fetch the resource.
	 * @throws	IllegalArgumentException if the resource key is {@code null}
	 *		or if a provider cannot be found for the resource key.
	 */
	private static ProviderKeyPair getProviderKey(final String resourceName) {
	    if (resourceName == null) {
		if (providerMap.get(OUR_PKG_NAME) != null) {
		    throw new IllegalArgumentException(getString("util#intl.keyNotNull"));
		}
		else {
		    throw new IllegalArgumentException("Text resource key must not be null.");
		}
	    }
	    int ix = resourceName.indexOf('#');
	    if (ix >= 0) {
		String pkgName;
		if (ix > 0) {
		    pkgName = resourceName.substring(0, ix);
		    if (pkgName.indexOf('.') < 0)
			pkgName = String.format("%1$s.%2$s", PKG_PREFIX, pkgName);
		}
		else {
		    pkgName = PKG_PREFIX;
		}
		Provider provider = providerMap.get(pkgName);
		if (provider == null) {
		    if (providerMap.get(OUR_PKG_NAME) != null) {
			throw new IllegalArgumentException(formatString("util#intl.providerNotFound", pkgName));
		    }
		    else {
			throw new IllegalArgumentException(String.format("Unable to find resource provider for \"%1$s\" package.", pkgName));
		    }
		}
		return new ProviderKeyPair(provider, resourceName.substring(ix + 1));
	    }
	    return new ProviderKeyPair(defaultProvider, resourceName);
	}

	/**
	 * Helper function to load a single string resource.
	 * <p> Just like the {@code BXMLSerializer}, if the given
	 * string resource does not exist the result is just the
	 * resource name (to avoid costly exceptions).  But the
	 * result is logged so a postprocessor can gather up these
	 * messages for analysis.
	 * <p> If the resource name begins with "pkg#", then the
	 * appropriate package resources are searched, otherwise
	 * the default resources are used.  Either the full package
	 * name can be given (i.e., "info.rlwhitcomb.script#") or just
	 * the part following "info.rlwhitcomb" (as in, "script#").
	 * <p>Note: "#" by itself will refer to "info.rlwhitcomb" resources
	 * (although "info.rlwhitcomb#" can be used as well).
	 *
	 * @param	resourceName	The key name of the resource to fetch.
	 * @return	The requested resource corresponding to the key.
	 * @throws	IllegalArgumentException if the resource key is {@code null}
	 *		or if a provider cannot be found for the resource key.
	 */
	public static String getString(String resourceName) {
	    ProviderKeyPair source = getProviderKey(resourceName);
	    return source.provider.getString(source.key);
	}

	/**
	 * Get a string resource, but don't insist on it being available.
	 *
	 * @param	resourceName	The key name of the resource to fetch.
	 * @return	The requested resource, or null if it can't be found.
	 * @throws	IllegalArgumentException if the resource key is {@code null}
	 *		or if a provider cannot be found for the resource key.
	 */
	public static String getOptionalString(final String resourceName) {
	    ProviderKeyPair source = getProviderKey(resourceName);
	    return source.provider.getOptionalString(source.key);
	}


	/**
	 * @return	One resource name string from an "object" and "key" pieces.
	 * @param	object	The "object" name (first part of an "x.y" key).
	 * @param	key	The second part of the resource key.
	 */
	private static String makeKey(final String object, final String key) {
	    if (object.endsWith("#"))
		return String.format("%1$s%2$s", object, key);
	    else
		return String.format("%1$s.%2$s", object, key);
	}


	/**
	 * @return	One resource name string from a number of object/key pieces.
	 * @param	objectKeys	The list of pieces to put together into
	 *				the resource name key.
	 */
	private static String makeKey(final String... objectKeys) {
	    StringBuilder buf = new StringBuilder();
	    for (String key : objectKeys) {
		if (buf.length() > 0)
		    buf.append('.');
		buf.append(key);
	    }
	    return buf.toString();
	}


	/**
	 * Different flavor of {@link #getString(String)} to take the
	 * object and key forms and combine them.
	 *
	 * @param	object	The "object" name (first part of an "x.y" key).
	 * @param	key	The second part of the resource key.
	 * @return		The resource corresponding to this combined key.
	 */
	public static String getString(final String object, final String key) {
	    return getString(makeKey(object, key));
	}


	/**
	 * And yet another flavor of {@link #getString(String)} to take an arbitrary
	 * number of object prefixes and concatenate them to get the final resource
	 * key name.
	 *
	 * @param	objectKeys	The list of object or key names to concatenate.
	 * @return	The resource found from "x.y.z...".
	 */
	public static String getString(final String... objectKeys) {
	    return getString(makeKey(objectKeys));
	}


	/**
	 * Get a numeric value from the given string resource.
	 *
	 * @param	key	The resource key to fetch.
	 * @return		The value of the resource string as a {@link Number}.
	 * @throws	NumberFormatException if the string is not in a numeric format.
	 */
	public static Number getNumber(String key) {
	    return Double.valueOf(getString(key));
	}


	/**
	 * Convenience method to get an integer value from the given string resource.
	 *
	 * @param	key	The resource key to fetch.
	 * @return		The integer value of the resource string.
	 * @throws	NumberFormatException if the value is not in integer format.
	 * @see		#getNumber
	 */
	public static int getInt(String key) {
	    return Integer.valueOf(getString(key));
	}


	/**
	 * Get a numeric value from the given string resource, with a given
	 * default if the value is not found.
	 *
	 * @param	key		Name of the resource to fetch.
	 * @param	defaultValue	What to return if the resource is not found.
	 * @return	Either the numeric value of the resource if found, or the
	 *		default value if not.
	 * @throws	NumberFormatException if the string is found, but cannot
	 *		be parsed into a valid number.
	 * @see #getOptionalString
	 */
	public static Number getNumber(final String key, final Number defaultValue) {
	    String numberString = getOptionalString(key);
	    return numberString == null ? defaultValue : Double.valueOf(numberString);
	}


	/**
	 * Get an integer value from the given string resource, or a default value
	 * if the resource cannot be found.
	 *
	 * @param	key		Name of the resource to fetch.
	 * @param	defaultValue	What to return if the resource cannot be found.
	 * @return	The integer value of the resource if found, or the default
	 *		value if not found.
	 * @throws	NumberFormatException if the resource string cannot be parsed
	 *		into a valid integer.
	 */
	public static int getInt(final String key, final int defaultValue) {
	    String intString = getOptionalString(key);
	    return intString == null ? defaultValue : Integer.valueOf(intString);
	}


	/**
	 * Helper method to use {@link #getString} to retrieve a format
	 * string and then format it.
	 *
	 * @param	formatKey	Key string to be used to look up
	 *				the actual format.
	 * @param	args		Any values to be substituted in the
	 *				final string.
	 * @return			The fully formatted string.
	 */
	public static String formatString(final String formatKey, final Object... args) {
	    String format = getString(formatKey);
	    return String.format(format, args);
	}


	/**
	 * Decide if a "messageOrKey" is a key.
	 *
	 * @param	messageOrKey	Either a straight text string, which is just passed through,
	 *				or {@code "%key"} where {@code "key"} is used then to lookup
	 *				the resource.
	 * @return	{@code true} if the passed in string is a key, or {@code false} if it is just
	 *		a message (could even contain format strings).
	 */
	public static boolean isAKey(final String messageOrKey) {
	    return (messageOrKey != null &&
		    messageOrKey.length() > 1 &&
		    messageOrKey.charAt(0) == '%' &&
		    // Check to make sure this really is a key (i.e., alphabetic key or '#'
		    // identifier meaning the default package provider)
		    // If it is a format specifier at the beginning of the string we would get %n or %%
		    (Character.isAlphabetic(messageOrKey.charAt(1)) ||
		     messageOrKey.charAt(1) == '#'));
	}


	/**
	 * Helper method to check for a key string ({@code "%key"}) and call {@link #getString(String)}
	 * on the trailing part if so or just use the text as-is.
	 *
	 * @param	messageOrKey	Either a straight text string, which is just passed
	 *				through, or {@code "%key"} where {@code "key"} is used 
	 *				then to lookup the resource.
	 * @return	The resource string if the input is a key, or just the input if it is not.
	 */
	public static String getKeyString(final String messageOrKey) {
	    return isAKey(messageOrKey) ? getString(messageOrKey.substring(1)) : messageOrKey;
	}


	/**
	 * Helper method to check for a key string ({@code "%key"}) and call {@link #getOptionalString}
	 * on the trailing part if so, or just return the text as-is.
	 *
	 * @param	messageOrKey	Either a straight text string, which is just passed
	 *				through, or {@code "%key"} where {@code "key"} is used
	 *				then to lookup the resource.
	 * @return	The resource string if the input is a key, or just the input if it is not,
	 *		but if the specified resource key is not found, just return {@code null} (do not
	 *		throw an exception about the missing resource).
	 */
	public static String getOptionalKeyString(final String messageOrKey) {
	    return isAKey(messageOrKey) ? getOptionalString(messageOrKey.substring(1)) : messageOrKey;
	}


	/**
	 * Helper method to check for a key string ({@code "%key"}) and call {@link #formatString}
	 * on the trailing part if so or just use the text as-is.
	 *
	 * @param	formatOrKey	Either a straight text string, which is just passed
	 *				through, or {@code "%key"} where {@code "key"} is used
	 *				then to lookup the resource.
	 * @param	args		The arguments to substitute into the format string.
	 * @return			The fully formatted string.
	 */
	public static String formatKeyString(final String formatOrKey, final Object... args) {
	    String format = getKeyString(formatOrKey);
	    return String.format(format, args);
	}


	/**
	 * Get a resource string and append the result to the existing
	 * {@link StringBuilder}.
	 * @param	buf	Existing string under construction.
	 * @param	key	Resource key to look up.
	 * @return		The input object so operations can
	 *			be chained together.
	 * @see	#getString(String)
	 */
	public static StringBuilder getString(final StringBuilder buf, final String key) {
	    return buf.append(getString(key));
	}


	/**
	 * Fetch the format string using the given key, format the data,
	 * then append to the {@link StringBuilder} supplied and return it.
	 * @param	buf		An existing buffer where a composite result
	 *				is being built.
	 * @param	formatKey	The resource key for the format string.
	 * @param	args		Any values to be substituted in the final string.
	 * @return			The input {@link StringBuilder} with the formatted string
	 *				appended to it.
	 * @see	#formatString
	 */
	public static StringBuilder formatString(final StringBuilder buf, final String formatKey, final Object... args) {
	    return buf.append(formatString(formatKey, args));
	}


	/**
	 * Equivalent of {@link #getKeyString} that appends the result to the input
	 * {@link StringBuilder}.
	 * @param	buf		The composite string buffer.
	 * @param	messageOrKey	Either a plain text string or a resource key (beginning
	 *				with '%').
	 * @return			The input buffer with the text appended to it.
	 * @see	#getKeyString
	 */
	public static StringBuilder getKeyString(final StringBuilder buf, final String messageOrKey) {
	    return buf.append(getKeyString(messageOrKey));
	}


	/**
	 * Equivalent of {@code formatKeyString(String, Object...)} that appends the result to the input
	 * {@link StringBuilder}.
	 * @param	buf		The composite string buffer.
	 * @param	formatOrKey	Either a plain format string or a resource key (beginning
	 *				with '%') to use to lookup the format string.
	 * @param	args		The value arguments to substitute into the format.
	 * @return			The input buffer with the formatted text appended to it.
	 * @see	#formatKeyString(String, Object...)
	 */
	public static StringBuilder formatKeyString(final StringBuilder buf, final String formatOrKey, final Object... args) {
	    return buf.append(formatKeyString(formatOrKey, args));
	}


	/**
	 * Print a blank line to {@link System#out} (convenience for {@link #outPrintln(String)}).
	 */
	public static void outPrintln() {
	    System.out.println();
	}


	/**
	 * Print a resource string to {@link System#out}.
	 * @param	key	Key value passed to {@link #getString(String)}.
	 */
	public static void outPrintln(final String key) {
	    System.out.println(getString(key));
	}


	/**
	 * Print a blank line to {@link System#err} (convenience for {@link #errPrintln(String)}).
	 */
	public static void errPrintln() {
	    System.err.println();
	}


	/**
	 * Print a resource string to {@link System#err}.
	 * @param	key	Key value passed to {@link #getString(String)}.
	 */
	public static void errPrintln(final String key) {
	    System.err.println(getString(key));
	}


	/**
	 * Display a startup error message to {@link Launcher#startupError}, or
	 * to the error console if Launcher methd is not available.
	 *
	 * @param	key	Key value passed to {@link #getString(String)}.
	 */
	public static void startupError(final String key) {
	    String errorMsg = getString(key);
	    try {
	 	Launcher.startupError(errorMsg);
	    }
	    catch (UnsatisfiedLinkError ule) {
		System.err.println(ERROR + errorMsg);
	    }
	}


	/**
	 * Format a message to {@link System#out}.
	 * @see	#formatString(String, Object...)
	 * @param formatKey The key string used to obtain the format string.
	 * @param args The (possibly empty) list of arguments used to format the message.
	 */
	public static void outFormat(final String formatKey, final Object... args) {
	    System.out.println(formatString(formatKey, args));
	}


	/**
	 * Format a message to {@link System#out}.
	 * @see	#formatKeyString(String, Object...)
	 * @param formatOrKey The raw format or key string (starting with "%") used to
	 * obtain the format string.
	 * @param args The (possibly empty) list of arguments used to format the message.
	 */
	public static void outKeyFormat(final String formatOrKey, final Object... args) {
	    System.out.println(formatKeyString(formatOrKey, args));
	}


	/**
	 * Format a message to {@link System#err}.
	 * @param formatKey The key string used to obtain the format string.
	 * @param args The (possibly empty) list of arguments used to format the message.
	 */
	public static void errFormat(final String formatKey, final Object... args) {
	    System.err.println(formatString(formatKey, args));
	}


	/**
	 * Format a message to {@link System#err}.
	 * @see	#formatKeyString(String, Object...)
	 * @param formatOrKey The raw format or key string (starting with "%") used to
	 * obtain the format string.
	 * @param args The (possibly empty) list of arguments used to format the message.
	 */
	public static void errKeyFormat(final String formatOrKey, final Object... args) {
	    System.err.println(formatKeyString(formatOrKey, args));
	}


	/**
	 * Format a message and display using {@link Launcher#startupError}, or
	 * to the error console if the Launcher method is not available.
	 *
	 * @param formatKey The key string used to obtain the format string.
	 * @param args The (possibly empty) list of arguments used to format the message.
	 */
	public static void startupErrorFormat(final String formatKey, final Object... args) {
	    String errorMsg = formatString(formatKey, args);
	    try {
		Launcher.startupError(errorMsg);
	    }
	    catch (UnsatisfiedLinkError ule) {
		System.err.println(ERROR + errorMsg);
	    }
	}


	/**
	 * @return prefix + ".help" + line
	 * @param prefix The resource key prefix for each help line.
	 * @param line   The help output index number used to look up the string.
	 */
	private static String helpKey(final String prefix, final int line) {
	    return makeKey(prefix, String.format("help%1$d", line));
	}


	/**
	 * Print out a series of "help" message lines described in a certain format
	 * in the resource file to {@link System#out}.
	 * <p> They must have a common package, common first key part ("script" in
	 * the example below), and have a "number of lines" key ("helpNumberLines"),
	 * (which is optional).
	 * The individual keys are prefixed with "help".
	 * <p> A typical example is this:
	 * <pre>script.helpNumberLines = 22
	 *script.help1 = "Usage..."
	 *script.help2 = ...
	 *...
	 *script.help22 = "last message"
	 * </pre>
	 *
	 * @param	prefix	The prefix used to select the help messages.
	 */
	public static void printHelp(final String prefix) {
	    printHelp(System.out, prefix, null, true, null);
	}

	/**
	 * Print out a series of "help" message lines described in a certain format
	 * in the resource file to {@link System#out}, with the option to color or not.
	 * <p> They must have a common package, common first key part ("script" in
	 * the example below), and have a "number of lines" key ("helpNumberLines"),
	 * (which is optional).
	 * The individual keys are prefixed with "help".
	 * <p> A typical example is this:
	 * <pre>script.helpNumberLines = 22
	 *script.help1 = "Usage..."
	 *script.help2 = ...
	 *...
	 *script.help22 = "last message"
	 * </pre>
	 *
	 * @param	prefix	The prefix used to select the help messages.
	 * @param	colors	Whether or not to expand the color tags.
	 */
	public static void printHelp(final String prefix, final boolean colors) {
	    printHelp(System.out, prefix, null, colors, null);
	}

	/**
	 * Print out a series of "help" message lines described in a certain format
	 * in the resource file to {@link System#out}, with the option to color or not,
	 * and the option to map the colors.
	 * <p> They must have a common package, common first key part ("script" in
	 * the example below), and have a "number of lines" key ("helpNumberLines"),
	 * (which is optional).
	 * The individual keys are prefixed with "help".
	 * <p> A typical example is this:
	 * <pre>script.helpNumberLines = 22
	 *script.help1 = "Usage..."
	 *script.help2 = ...
	 *...
	 *script.help22 = "last message"
	 * </pre>
	 *
	 * @param	prefix	The prefix used to select the help messages.
	 * @param	colors	Whether or not to expand the color tags.
	 * @param	colorMap A mapping between color tags and real color codes.
	 */
	public static void printHelp(final String prefix, final boolean colors, final Map<String, Object> colorMap) {
	    printHelp(System.out, prefix, null, colors, colorMap);
	}

	/**
	 * Print out a series of "help" message lines described in a certain format
	 * in the resource file to the given {@link PrintStream}.
	 * <p> They must have a common package, common first key part ("script" in
	 * the example below), and have a "number of lines" key ("helpNumberLines"),
	 * (which is optional).
	 * The individual keys are prefixed with "help".
	 * <p> A typical example is this:
	 * <pre>script.helpNumberLines = 22
	 *script.help1 = "Usage..."
	 *script.help2 = ...
	 *...
	 *script.help22 = "last message"
	 * </pre>
	 *
	 * @param	ps	The {@link PrintStream} to use to display the help.
	 * @param	prefix	The prefix used to select the help messages.
	 */
	public static void printHelp(final PrintStream ps, final String prefix) {
	    printHelp(ps, prefix, null, true, null);
	}

	/**
	 * Print out a series of "help" message lines described in a certain format
	 * in the resource file to the given {@link PrintStream}, with the option
	 * to color the messages.
	 * <p> They must have a common package, common first key part ("script" in
	 * the example below), and have a "number of lines" key ("helpNumberLines"),
	 * (which is optional).
	 * The individual keys are prefixed with "help".
	 * <p> A typical example is this:
	 * <pre>script.helpNumberLines = 22
	 *script.help1 = "Usage..."
	 *script.help2 = ...
	 *...
	 *script.help22 = "last message"
	 * </pre>
	 *
	 * @param	ps	The {@link PrintStream} to use to display the help.
	 * @param	prefix	The prefix used to select the help messages.
	 * @param	colors	Whether or not to expand the color tags.
	 */
	public static void printHelp(final PrintStream ps, final String prefix, final boolean colors) {
	    printHelp(ps, prefix, null, colors, null);
	}

	/**
	 * Print out a series of "help" message lines described in a certain format
	 * in the resource file to the given {@link PrintStream}, with the option
	 * to color the messages, and to map the colors.
	 * <p> They must have a common package, common first key part ("script" in
	 * the example below), and have a "number of lines" key ("helpNumberLines"),
	 * (which is optional).
	 * The individual keys are prefixed with "help".
	 * <p> A typical example is this:
	 * <pre>script.helpNumberLines = 22
	 *script.help1 = "Usage..."
	 *script.help2 = ...
	 *...
	 *script.help22 = "last message"
	 * </pre>
	 *
	 * @param	ps	The {@link PrintStream} to use to display the help.
	 * @param	prefix	The prefix used to select the help messages.
	 * @param	colors	Whether or not to expand the color tags.
	 * @param	colorMap A mapping between color tags and real color codes.
	 */
	public static void printHelp(final PrintStream ps, final String prefix,
		final boolean colors, final Map<String, Object> colorMap) {
	    printHelp(ps, prefix, null, colors, colorMap);
	}

	/**
	 * Print out a series of "help" message lines described in a certain format
	 * in the resource file to {@link System#out}.
	 * <p> They must have a common package, common first key part ("script" in
	 * the example below), and have a "number of lines" key ("helpNumberLines"),
	 * (which is optional).
	 * The individual keys are prefixed with "help".
	 * <p> A typical example is this:
	 * <pre>script.helpNumberLines = 22
	 *script.help1 = "Usage..."
	 *script.help2 = ...
	 *...
	 *script.help22 = "last message"
	 * </pre>
	 *
	 * @param	prefix	The prefix used to select the help messages.
	 * @param	symbols	A map of symbols used to substitute values.
	 */
	public static void printHelp(final String prefix, final Map<String, String> symbols) {
	    printHelp(System.out, prefix, symbols, true, null);
	}

	/**
	 * Print out a series of "help" message lines described in a certain format
	 * in the resource file to the given {@link PrintStream}, with the option
	 * to use colors.
	 * <p> They must have a common package, common first key part ("script" in
	 * the example below), and have a "number of lines" key ("helpNumberLines"),
	 * (which is optional).
	 * The individual keys are prefixed with "help".
	 * <p> A typical example is this:
	 * <pre>script.helpNumberLines = 22
	 *script.help1 = "Usage..."
	 *script.help2 = ...
	 *...
	 *script.help22 = "last message"
	 * </pre>
	 *
	 * @param	ps	The {@link PrintStream} to use to display the help.
	 * @param	prefix	The prefix used to select the help messages.
	 * @param	symbols	A map of symbols used to substitute values.
	 * @param	colors	Whether or not to expand the color tags.
	 * @param	colorMap A mapping between color tags and real color codes.
	 */
	public static void printHelp(final PrintStream ps, final String prefix,
		final Map<String, String> symbols, final boolean colors, final Map<String, Object> colorMap) {
	    // Grab the number of help lines from the resources first
	    int numLines = getInt(makeKey(prefix, "helpNumberLines"), -1);
	    int lineNo = 1;

	    // If the number of lines isn't supplied, then just keep
	    // printing until we run out of items to print.
	    if (numLines < 1) {
		while (true) {
		    String helpLine = getOptionalString(helpKey(prefix, lineNo++));
		    if (helpLine == null)
			break;
		    System.out.println(
			ConsoleColor.color(CharUtil.substituteEnvValues(helpLine, symbols), colors, colorMap));
		}
	    }
	    else {
		while (lineNo <= numLines) {
		    String helpLine = getString(helpKey(prefix, lineNo++));
		    System.out.println(
			ConsoleColor.color(CharUtil.substituteEnvValues(helpLine, symbols), colors, colorMap));
		}
	    }
	}


}
