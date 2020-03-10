/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2011-2014,2017-2018,2020 Roger L. Whitcomb.
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
 *	Class used to launch the main class of a jar file.
 *
 *  History:
 *	01-Sep-2011 (rlwhitcomb)
 *	    Initial coding.
 *	02-Sep-2011 (rlwhitcomb)
 *	    Spiff up the InvocationTargetException message to get at
 *	    the cause and not the message (which appears to usually be null);
 *	    add the "checksum" parameter to main and code to do the .jar file
 *	    verification.
 *	06-Sep-2011 (rlwhitcomb)
 *	    Changed the checksum split character to "!" to fix compiler
 *	    problems with gcc.
 *	25-Oct-2011 (rlwhitcomb)
 *	    Correct some things found by FindBugs.
 *	14-Feb-2012 (rlwhitcomb)
 *	    Don't assume the existence of System.err in order to print errors;
 *	    throw runtime exceptions and let the wrapper display them however
 *	    it wants to.
 *	21-Aug-2012 (rlwhitcomb)
 *	    Add a parameter to the "main" method that is the main class name so that we
 *	    can invoke main classes other than the main-main in the .jar manifest.
 *	08-May-2013 (rlwhitcomb)
 *	    Add the program name to the "main" argument list.  Make the value available
 *	    to the rest of the application.
 *	14-May-2013 (rlwhitcomb)
 *	    Add "startupError" interface which calls the underlying utility in the C++
 *	    code (through JNI).
 *	05-Jun-2013 (rlwhitcomb)
 *	    Clean up the checksum error message to print the whole thing that doesn't
 *	    match.
 *	28-Jun-2013 (rlwhitcomb)
 *	    Make the "getOurJarFile" method public so others can use it.
 *	05-Sep-2013 (rlwhitcomb)
 *	    Print the target exception message.  Add two native methods to get and set
 *	    an environment variable (for this process).
 *	21-Mar-2014 (rlwhitcomb)
 *	    Add JNI function to close the splash screen (esp. for Linux).
 *	14-Aug-2014 (rlwhitcomb)
 *	    Cleanup "lint" warnings.
 *	04-Sep-2014 (rlwhitcomb)
 *	    Enable us to be launched as a browser applet, so accommodate an "http:" URL.
 *	26-Jan-2017 (rlwhitcomb)
 *	    Cleanup Javadoc warnings under Java 8 (this file was neglected until today).
 *	21-Mar-2017 (rlwhitcomb)
 *	    Changes to allow dealing with a plain file URL returned from "getOurJarFile"
 *	    so test programs can do things like Intl initialization easily.
 *	25-Jul-2017 (rlwhitcomb)
 *	    Add accessor for "mainClassName" so this .jar file attribute can be accessed
 *	    by the rest of the code.
 *	20-Aug-2018 (rlwhitcomb)
 *	    Replace MD5 checksum (now considered insecure) with SHA-256.
 */

package info.rlwhitcomb.jarfile;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.jar.*;

/**
 * A helper class that is invoked from JNI code to facilitate getting into the
 * .jar file manifest without having to code that stuff outside of Java.
 * <p> Our executable wrappers want to be very simple and deal only with normal
 * JVM invocation logic, and not with the intricacies of searching a .zip file
 * and extracting the manifest, etc.  Since that stuff is easy in Java, we provide
 * this wrapper that is easily invoked from JNI to do the dirty work.
 * <p> The other service we provide is a checksum check to make sure the wrapper
 * is expecting the same .jar file as we actually live in.  This provides some
 * (minimal) level of protection and/or warning of either tampered-with code or
 * an upgrade that wasn't complete.
 * <p> Any errors are signaled by throwing exceptions from this class so we don't
 * have to know what kind of UI is needed by the eventual main class.
 * <p> A second entry point is available to use a given main class name (as opposed
 * to reading the .jar file manifest to get it.  This facilitates invoking other
 * "main" classes that may live in a big .jar file.
 */
public class Launcher
{
	private static String mainProgramPath = "";
	private static String mainProgramName = "";
	private static String mainClassName = "";

	private static final String JAR_FILE_SUFFIX = ".jar";
	private static final String JAR_SUFFIX = JAR_FILE_SUFFIX + "!/";


	/**
	 * @return The main program path that we were invoked as.
	 */
	public static String getProgramPath() {
	    return mainProgramPath;
	}


	/**
	 * @return The main program name that we were invoked as.
	 */
	public static String getProgramName() {
	    return mainProgramName;
	}


	/**
	 * @return The main class name that was invoked by our {@link #main} method.
	 */
	public static String getMainClassName() {
	    return mainClassName;
	}

	/**
	 * Helper method to throw a {@link RuntimeException} with the given cause
	 * and formatted message.
	 *
	 * @param cause The underlying reason we are exiting.
	 * @param msg   The message format string.
	 * @param args  Any subsitution variable for the format string.
	 * @throws RuntimeException wrapped around the cause.
	 */
	private static void throwException(Throwable cause, String msg, Object... args) {
	    String fullMessage = String.format(msg, args);
	    throw new RuntimeException(fullMessage, cause);
	}


	/**
	 * @return The {@link URL} entry that corresponds to the
	 * .jar file that we live in.  It should be a URL
	 * in the format:
	 * <pre>jar:file://....blah.jar</pre>
	 * or
	 * <pre>jar:http://...blah.jar!/</pre>
	 * or
	 * <pre>file://...</pre>
	 * if we've just been launched from a build area and not from
	 * a .jar file.
	 */
	public static URL getOurJarFile() {
	    try {
		Class<?> c = Launcher.class;
		URL classURL = c.getResource(String.format("%1$s.class", c.getSimpleName()));
		String classURLString = classURL.toString();

		// If we live inside a .jar file the resource URL will start with "jar:"
		// But it could be a "jar:file:" for a desktop app
		// or "jar:http:" for a web applet
		if (classURLString.startsWith("jar:file:")) {
		    String classFileString = classURL.getFile();
		    // Strip off the part AFTER the ".jar"
		    int ix = classFileString.indexOf(JAR_FILE_SUFFIX);
		    if (ix >= 0)
			classURLString = classFileString.substring(0, ix + JAR_FILE_SUFFIX.length());
		}
		else if (classURLString.startsWith("jar:http:")) {
		    int ix = classURLString.indexOf(JAR_SUFFIX);
		    if (ix >= 0)
			classURLString = classURLString.substring(0, ix + JAR_SUFFIX.length());
		}
		else if (classURLString.startsWith("file:")) {
		    // This should be the path to our standalone .class file ("Launcher.class")
		    // so strip off our package/path from the end to give the root directory URL.
		    String ourClassPath = Launcher.class.getName().replace('.', '/');
		    int ix = classURLString.indexOf(ourClassPath);
		    if (ix >= 0)
			classURLString = classURLString.substring(0, ix);
		}
		return new URL(classURLString);
	    }

	    // Since we're dealing with stuff returned by the system, this
	    // will never occur, but it has to be caught or thrown anyway
	    catch (MalformedURLException ex) {}

	    return null;
	}


	/**
	 * Helper method to return a {@link File} object from a {@link URL}, catching
	 * and ignoring the {@link URISyntaxException} (not) thrown in the middle,
	 * and removing the current directory from the path (replace with "." if empty).
	 *
	 * @param url	The URL returned from {@link #getOurJarFile} (probably),
	 *		but presumably only the "file://" case.
	 * @return	The File object relative to the current directory,
	 *		if it truly is a "file://" URL, otherwise {@code null}.
	 */
	public static File urlToPlainFile(URL url) {
	    if (url.getProtocol().equals("file")) {
		try {
		    File f = new File(url.toURI());
		    File curDir = new File(System.getProperty("user.dir"));
		    String fPath = f.getPath();
		    String curDirPath = curDir.getPath();
		    if (fPath.startsWith(curDirPath)) {
			String relativePath = fPath.replace(curDirPath, ".");
			return new File(relativePath);
		    }
		    return f;
		}
		catch (URISyntaxException ex) {
		    ;	// will never happen in practice
		}
	    }
	    return null;
	}


	/**
	 * Takes the {@link URL} output from {@link #getOurJarFile} and gets a
	 * {@link JarFile} out of it.  The mechanics depends on whether we have a
	 * local file or an HTTP connection.
	 *
	 * @param jarURL The URL of our containing .jar file.
	 * @return The object corresponding to our containing .jar file.
	 * @throws IOException if there is some weird problem with the URL.
	 */
	public static JarFile getJarFile(URL jarURL)
		throws IOException
	{
	    String jarURLString = jarURL.toString();
	    JarFile jarFile = null;
	    try {
		if (jarURLString.startsWith("jar:http:")) {
		    JarURLConnection jarConnection = (JarURLConnection)(jarURL.openConnection());
		    jarFile = jarConnection.getJarFile();
		}
		else if (jarURLString.startsWith("file:")) {
		    jarFile = new JarFile(new File(new URI(jarURLString)));
		}
	    }
	    catch (URISyntaxException use) { }

	    if (jarFile == null) {
		throwException(new MalformedURLException(), "Unable to determine the location of '%1$s'", jarURLString);
	    }
	    return jarFile;
	}


	/**
	 * Does an MD5 checksum of the .jar file given by its URL to verify it is
	 * what the C-loader is expecting (helps prevent someone substituting
	 * an unknown .jar file with the same name which could invoke bad code).
	 * <p> Since we basically trust what is in the manifest, we want to make
	 * sure that the .exe file was built along with this .jar so we are
	 * executing what we were built against.
	 *
	 * @param jarFileURL    A URL pointing to our .jar file.
	 * @param checksum      The checksum string as it would have been formatted
	 *                      by the build process.
	 * @throws RuntimeException if there was any kind of error (such as the checksum
	 * doesn't match).
	 * @throws IllegalAccessException if the access to the .jar file is not allowed
	 * (unlikely).
	 * @throws IOException if the .jar file can't be read (again, unlikely or we
	 * wouldn't be executing this code).
	 * @throws NoSuchAlgorithmException if the MD5 digester couldn't be found...
	 * @see #checkChecksum(InputStream, String)
	 */
	private static void checkChecksum(URL jarFileURL, String checksum)
		throws IllegalAccessException, IOException, NoSuchAlgorithmException
	{
	    checkChecksum(jarFileURL.openStream(), checksum);
	}

	/**
	 * Does an MD5 checksum of the given .jar file to verify it is
	 * what the C-loader is expecting (helps prevent someone substituting
	 * an unknown .jar file with the same name which could invoke bad code).
	 * <p> Since we basically trust what is in the manifest, we want to make
	 * sure that the .exe file was built along with this .jar so we are
	 * executing what we were built against.
	 *
	 * @param jarFileStream An already open stream to our .jar file.
	 * @param checksum      The checksum string as it would have been formatted
	 *                      by the build process.
	 * @throws RuntimeException if there was any kind of error (such as the checksum
	 * doesn't match).
	 * @throws IllegalAccessException if the access to the .jar file is not allowed
	 * (unlikely).
	 * @throws IOException if the .jar file can't be read (again, unlikely or we
	 * wouldn't be executing this code).
	 * @throws NoSuchAlgorithmException if the MD5 digester couldn't be found...
	 */
	private static void checkChecksum(InputStream jarFileStream, String checksum)
		throws IllegalAccessException, IOException, NoSuchAlgorithmException
	{
	    if (jarFileStream == null) {
		throw new RuntimeException("Unable to read application main .jar file");
	    }
	    // Note: this algorithm choice must match what happens in "build.xml"
	    MessageDigest __md = MessageDigest.getInstance("SHA-256");
	    __md.reset();
	    try {
		byte[] bytes = new byte[8192];
		int len;
		while ((len = jarFileStream.read(bytes)) != -1) {
		    __md.update(bytes, 0, len);
		}
	    }
	    finally {
		jarFileStream.close();
	    }
	    byte[] result = __md.digest();
	    // Split the checksum string into bytes
	    // Each is a two-digit hex value separated by "!"
	    String[] checkBytes = checksum.split("!");
	    if (result.length != checkBytes.length)
		throw new RuntimeException("Checksum length is incorrect.");
	    for (int i = 0; i < result.length; i++) {
		if (result[i] != (byte)Integer.parseInt(checkBytes[i], 16)) {
		    StringBuilder buf = new StringBuilder();
		    for (byte b : result) {
			int v = ((int)b) & 0xFF;
			if (buf.length() > 1)
			    buf.append(",");
			buf.append(Integer.toHexString(v).toUpperCase());
		    }
		    throw new RuntimeException(String.format("Checksum does not match.%n Expecting:%n%1$s%n Actual:%n%2$s%n",
				checksum.replace('!', ','), buf.toString()));
		}
	    }
	}


	/**
	 * Invoke the "main(String[])" method of either the given
	 * class or the class given by the "Main-Class" attribute of
	 * the .jar file manifest.
	 * <p> Checks that the MD5 checksum of the .jar file matches
	 * passed-in value (determined at build time).  This helps
	 * prevent invoking the .jar from a non-matching executable
	 * which is either a warning of tampered-with code or a
	 * not completely upgraded system.
	 * <p> Also provides a way that Java normally doesn't have of
	 * accessing the program name by which we were invoked.
	 *
	 * @param checksum      The checksum string as formatted by the build process.
	 * @param mainClassName The main class to invoke to run the application.
	 * @param programName   The name of the executable that is actually running.
	 * @param args          The parsed command line arguments.
	 *
	 * @see #throwException throwException (for error handling)
	 */
	public static void main(String checksum, String mainClassName, String programName, String[] args) {
	    URL ourJarFile = getOurJarFile();
	    try {
		if (programName != null) {
		    File path = new File(programName);
		    mainProgramPath = path.getPath();
		    mainProgramName = path.getName();
		}
		checkChecksum(ourJarFile, checksum);
		if (mainClassName == null || mainClassName.isEmpty()) {
		    JarFile jarFile = getJarFile(ourJarFile);
		    Manifest manifest = jarFile.getManifest();
		    Attributes attributes = manifest.getMainAttributes();
		    mainClassName = attributes.getValue(Attributes.Name.MAIN_CLASS);
		}
		Class<?> c = Class.forName(mainClassName);

		// Make this available once we know it's good
		Launcher.mainClassName = mainClassName;

		Method main = c.getMethod("main", new String[0].getClass());
		Class<?>[] types = main.getParameterTypes();
		main.invoke(null, types[0].cast(args));
	    }
	    catch (NoSuchAlgorithmException nsae) {
		throwException(nsae, "Unable to verify the integrity of '%1$s' file", ourJarFile.getPath());
	    }
	    catch (IllegalAccessException iae) {
		throwException(iae, "Unable to access 'main' method in class '%1$s'", mainClassName);
	    }
	    catch (InvocationTargetException ite) {
		Throwable target = ite.getTargetException();
		String msg = target.getMessage();
		if (msg == null || msg.isEmpty())
		    msg = target.getClass().getSimpleName();
		throwException(ite, "Exception from 'main' method in class '%1$s':%n%2$s", mainClassName, msg);
	    }
	    catch (NoSuchMethodException nsme) {
		throwException(nsme, "Unable to find 'main' method in class '%1$s'", mainClassName);
	    }
	    catch (ClassNotFoundException cnfe) {
		throwException(cnfe, "Unable to find main class '%1$s'", mainClassName);
	    }
	    catch (IOException ioe) {
		throwException(ioe, "Unable to open jar file '%1$s'", ourJarFile.getPath());
	    }
	}


	/**
	 * Display a startup error in a system-specific way that guarantees it will
	 * show up to the user.
	 * <p> This is meant to be called by application code, but it will display 
	 * the message in the same way as other startup errors encountered before
	 * the JVM was invoked.
	 *
	 * @param message The error message to display.
	 */
	public static native void startupError(String message);

	/**
	 * Get the value of a process environment variable.
	 *
	 * @param key  The environment variable name.
	 * @return     The value of that environment variable (if any).
	 */
	public static native String getEnv(String key);

	/**
	 * Set the value of a process environment variable.
	 *
	 * @param key    The environment variable name.
	 * @param value  The new value to be set for that variable.
	 */
	public static native void setEnv(String key, String value);

	/**
	 * Special workaround on Linux to close the splashscreen.
	 */
	public static native void splashClose();


}
