/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2010-2011,2013-2017,2019-2022 Roger L. Whitcomb.
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
 *   File Utilities
 *
 * History:
 *    25-Jun-2010 (rlwhitcomb)
 *	Initial implementation.  Taken from examples on web.
 *    06-Sep-2013 (rlwhitcomb)
 *	Add method to check readability of a directory using new
 *	Java 7 features.  Make "main" method call this new method
 *	so we can invoke the function from the command line.
 *    12-Sep-2013 (rlwhitcomb)
 *	Add method to generate a random file name with the given
 *	prefix and suffix (extension).
 *    25-Jan-2014 (rlwhitcomb)
 *	Add a logging statement so we can tell when and with what
 *	path the pathIsReadable is check is being called.
 *	Add comments to "copyFile".
 *    28-Jan-2014 (rlwhitcomb)
 *	Try different things for "pathIsReadable" because the code
 *	in the Files class doesn't seem to be quite right everywhere.
 *    04-Mar-2014 (rlwhitcomb)
 *	Don't try to open a FileInputStream on a directory to check
 *	read access... Extra logging.
 *    12-Mar-2015 (rlwhitcomb)
 *	Add a method for creating temporary files and allow delete
 *	on exit.
 *    17-Mar-2015 (rlwhitcomb)
 *	Add a method to count lines in a file.
 *    31-Aug-2015 (rlwhitcomb)
 *	Cleanup Javadoc (found by Java 8).
 *    07-Jan-2016 (rlwhitcomb)
 *	More Javadoc work.
 *    20-Feb-2016 (rlwhitcomb)
 *	Add a method to do gzip compression on a file and one to rename
 *	a file.
 *    30-Mar-2016 (rlwhitcomb)
 *	New method to read file into a string.
 *    12-Jul-2016 (rlwhitcomb)
 *	Add a method to compare two files (byte-by-byte).
 *	Fix a couple of funky Javadoc constructs.
 *    27-Mar-2017 (rlwhitcomb)
 *	Add "uncompressFile" method.
 *    07-Jun-2017 (rlwhitcomb)
 *	Allow null charset for 'readFileAsString' to specify system default.
 *    15-Mar-2019 (rlwhitcomb)
 *	Don't use FileInputStream/FileOutputStream due to GC problems b/c of the finalize
 *	method in these classes. Remove wildcard imports.
 *    23-May-2019 (rlwhitcomb)
 *	Migrate the "canWrite" function into here.
 *    10-Mar-2020 (rlwhitcomb)
 *	Prepare for GitHub.
 *    21-Dec-2020 (rlwhitcomb)
 *	Update obsolete Javadoc constructs.
 *    05-Jan-2021 (rlwhitcomb)
 *	Another flavor of "readFileAsString".
 *    21-Jan-2021 (rlwhitcomb)
 *	Move "canExecute" into here from other code.
 *    22-Jan-2021 (rlwhitcomb)
 *	Two new methods to munge file names/paths.
 *    27-Jan-2021 (rlwhitcomb)
 *	New method to copy an InputStream to a temp file (for Help display of HTML).
 *	Another omnibus method to copy entries from a jar file into a temp directory
 *	from a given package, with given extensions.
 *    29-Jan-2021 (rlwhitcomb)
 *	Use new Intl Exception variants for convenience. New "canReadDir" variant
 *	of "canRead" for directories.
 *    07-Jul-2021 (rlwhitcomb)
 *	Make the class final and the constructor private.
 *    23-Aug-2021 (rlwhitcomb)
 *	Add another flavor of "readFileAsString".
 *    24-Aug-2021 (rlwhitcomb)
 *	Tweak some of the Javdoc.
 *    01-Sep-2021 (rlwhitcomb)
 *	Deal with InvalidPathExceptions in the "canRead" / "canWrite" methods.
 *    09-Nov-2021 (rlwhitcomb)
 *	Add method to get extension only.
 *    06-Jan-2022 (rlwhitcomb)
 *	Fix "Zip Slip" vulnerability in "unpackFiles".
 *    24-Jan-2022 (rlwhitcomb)
 *	Add "readFileAsLines" (several flavors).
 *    16-Feb-2022 (rlwhitcomb)
 *	Move buffer and file size constants out to Constants.
 *    23-May-2022 (rlwhitcomb)
 *	Provide a default list of executable extensions for Windows
 *	in case "PATHEXT" is not present (for some unknown reason).
 *    01-Jun-2022 (rlwhitcomb)
 *	#45: New "readRawText" and "writeRawText" methods; separate out "getFileReader".
 *	make parameters final.
 *    27-Jun-2022 (rlwhitcomb)
 *	#376: Add "checkNameCase" and "exists" methods.
 *    09-Jul-2022 (rlwhitcomb)
 *	#393: Cleanup imports.
 *    31-Aug-2022 (rlwhitcomb)
 *	#453: Modifications for "dot" names.
 *    02-Oct-2022 (rlwhitcomb)
 *	#498: Add overload compress/uncompress methods with "outputName" and "delete" parameters.
 *    06-Oct-2022 (rlwhitcomb)
 *	#505: New "compareFileLines" method that ignores line ending differences.
 *	#505: Close the readers in "compareFileLines".
 *    10-Oct-2022 (rlwhitcomb)
 *	#481: New "writeStringToFile" method.
 *    12-Oct-2022 (rlwhitcomb)
 *	#513: Move Logging to new package.
 *    13-Oct-2022 (rlwhitcomb)
 *	#481: Make "getFileReader" and "getFileWriter" public.
 *    21-Oct-2022 (rlwhitcomb)
 *	#473: Add "+" processing to "exists" function.
 */
package info.rlwhitcomb.util;

import info.rlwhitcomb.logging.Logging;
import net.iharder.b64.Base64;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.security.SecureRandom;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipException;

import static info.rlwhitcomb.util.Constants.*;


/**
 * Utility class for dealing with files.
 */
public final class FileUtilities
{
    /** Source of randomness for {@link #getRandomName}. */
    private static SecureRandom random = new SecureRandom();

    /**
     * Compressed file (GZIP) extension, made available for callers
     * of the {@link #compressFile} method.
     */
    public static final String COMPRESS_EXT = ".gz";

    /**
     * The list of allowed executable file extensions (Windows only).
     */
    private static String[] EXECUTABLE_EXTENSIONS;

    /**
     * Default list of executable extensions (in case "PATHEXT" is not found in the environment.
     */
    private static final String DEFAULT_EXECUTABLES = ".COM;.EXE;.BAT;.CMD;.VBS;.VBE;.JS;.JSE;.WSF;.WSH;.MSC";


    /**
     * Private constructor because this is a utility class with only static methods.
     */
    private FileUtilities() {
    }

    /**
     * Is this a "dot" name, namely {@code "."}, {@code ".."}, {@code ".config"}, and so on.
     *
     * @param name The file name to check.
     * @return     Whether this name is one of the above names.
     */
    public static boolean dotName(final String name) {
	return name.startsWith(".");
    }

    /**
     * Compute the bare name part of the file.
     *
     * @param f The file to examine.
     * @return Only the name portion of the given file, without the path
     * or the extension (if any).
     */
    public static String nameOnly(final File f) {
	String name = f.getName();
	if (dotName(name))
	    return name;
	int dotPos  = name.lastIndexOf('.');
	if (dotPos < 0)
	    return name;
	else
	    return name.substring(0, dotPos);
    }

    /**
     * Compute the extension of the file name, including the "." if any.
     *
     * @param f The file to examine.
     * @return Only the extension portion of the given file name.
     */
    public static String extOnly(final File f) {
	String name = f.getName();
	if (dotName(name))
	    return "";
	int dotPos  = name.lastIndexOf('.');
	if (dotPos < 0)
	    return "";
	else
	    return name.substring(dotPos);
    }

    /**
     * Decorate a bare file with the required but missing pieces.
     *
     * @param name	The (possibly) bare file name to decorate.
     * @param dir	The default directory to use if the name has none.
     * @param ext	The default extension to use if the name has none.
     * @return		A new {@code File} object with the given path and extension
     *			(unless the input already has a path and/or extension).
     */
    public static File decorate(final String name, final File dir, final String ext) {
	String fullName = name;
	int dotPos = fullName.lastIndexOf('.');
	if (dotPos < 0 && ext != null) {
	    if (ext.startsWith("."))
		fullName = String.format("%1$s%2$s", name, ext);
	    else
		fullName = String.format("%1$s.%2$s", name, ext);
	}

	File f = new File(fullName);
	if (f.getParent() == null && dir != null)
	    return new File(dir, fullName);
	else
	    return f;
    }

    /**
     * Copies one file to another.
     *
     * @param in The original file.
     * @param out The new output file.
     * @throws IOException if there is an error during the copy.
     * @throws FileNotFoundException if the original file could
     * not be found.
     */
    public static void copyFile(final File in, final File out)
		throws IOException, FileNotFoundException
    {
	Files.copy(in.toPath(), out.toPath(),
		StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
    }

    /**
     * Compare two files byte-by-byte.
     * <p> NOTE: this is only meant to compare two "small-ish" files
     * because it reads the entire contents into memory.
     * @see Files#readAllBytes
     * @param file1	The first file to compare.
     * @param file2	The second file to compare.
     * @return	{@code true} if the two files compare byte-for-byte,
     *		or {@code false} if not.
     * @throws IOException if something went wrong.
     */
    public static boolean compareFiles(final File file1, final File file2)
	throws IOException
    {
	Path path1 = file1.toPath();
	Path path2 = file2.toPath();
	byte[] bytes1 = Files.readAllBytes(path1);
	byte[] bytes2 = Files.readAllBytes(path2);
	return Arrays.equals(bytes1, bytes2);
    }

    /**
     * Compare two files line-by-line.
     * <p> This is meant to compare files, ignoring line ending differences,
     * because it uses the {@link BufferedReader#readLine} method to read
     * through the file.
     *
     * @param file1	The first file to compare.
     * @param file2	The second file to compare.
     * @return	{@code true} if the two files compare byte-for-byte,
     *		or {@code false} if not.
     * @throws IOException if something went wrong.
     */
    public static boolean compareFileLines(final File file1, final File file2)
	throws IOException
    {
	Path path1 = file1.toPath();
	Path path2 = file2.toPath();

	// We use the 8859-1 charset because all 256 byte values are legal here, so we are
	// (basically) guaranteed not to get a character encoding exception reading any
	// kind of text encoding.
	try (BufferedReader rdr1 = Files.newBufferedReader(path1, ISO_8859_1_CHARSET);
	     BufferedReader rdr2 = Files.newBufferedReader(path2, ISO_8859_1_CHARSET)) {

	    String line1, line2;
	    while ((line1 = rdr1.readLine()) != null) {
		line2 = rdr2.readLine();
		if (line2 == null)
		    return false;
		if (!line1.equals(line2))
		    return false;
	    }
	    line2 = rdr2.readLine();
	    return (line2 == null);
	}
    }

    /**
     * Generate a random file name using the given prefix and suffix
     * (extension).
     *
     * @param	prefix	The beginning of the new random name.
     * @param	suffix	And the ending of it (usually an extension,
     *			if there is a leading ".").
     * @return The new random file name.
     */
    public static String getRandomName(final String prefix, final String suffix) {
	byte bytes[] = new byte[20];
	random.nextBytes(bytes);
	return String.format("%1$s%2$s%3$s",
		prefix,
		Base64.encodeBytes(bytes, Base64.URL_SAFE),
		suffix);
    }

    /**
     * Create a temporary file with the given name prefix and extension.
     *
     * @param	prefix		Prefix for the file name (must be &gt; 3 characters)
     * @param	suffix		Should start with "." (i.e., a file extension), so one will
     *				be added if not present (but can be {@code null}).
     * @param	deleteOnExit	{@code true} to always delete this file
     *				when the JVM terminates (i.e., assumes the
     *				user will not need to examine the file later).
     * @return	The file object.
     * @throws	IOException if there was disk operation error.
     */
    public static File createTempFile(final String prefix, final String suffix, final boolean deleteOnExit)
		throws IOException
    {
	File tempFile = File.createTempFile(prefix, suffix == null || suffix.startsWith(".") ? suffix : "." + suffix);
	if (deleteOnExit)
	    tempFile.deleteOnExit();
	return tempFile;
    }

    /**
     * Create a temporary file with the given name prefix and default extension (".tmp").
     * @param	prefix		Prefix for the file name (must be &gt; 3 characters)
     * @param	deleteOnExit	{@code true} to always delete this file
     *				when the JVM terminates (i.e., assumes the
     *				user will not need to examine the file later).
     * @return	The file object.
     * @throws	IOException if there was disk operation error.
     * @see	#createTempFile(String, String, boolean)
     */
    public static File createTempFile(final String prefix, final boolean deleteOnExit)
		throws IOException
    {
	return createTempFile(prefix, null, deleteOnExit);
    }

    /**
     * Create a temporary file with the given name prefix and default extension (".tmp")
     * and that caller is responsible for cleaning up (i.e., it will not be
     * automatically deleted on exit).
     * @param	prefix		Prefix for the file name (must be &gt; 3 characters)
     * @return	The file object.
     * @throws	IOException if there was disk operation error.
     * @see	#createTempFile(String, String, boolean)
     */
    public static File createTempFile(final String prefix)
		throws IOException
    {
	return createTempFile(prefix, null, false);
    }

    /**
     * Copy the incoming stream to a temp file, returning the temp file reference.
     *
     * @param	is		The input stream (maybe a .jar file resource?) to copy to
     *				a temporary file, created here.
     * @param	prefix		Prefix for the temp file name (must be &gt; 3 characters).
     * @param	suffix		The file extension to use (can be {@code null}).
     * @param	deleteOnExit	Whether to keep the file around after the JVM exits.
     * @return	The file object with the input contents already written to it.
     * @throws	IOException if there was a error reading or writing.
     * @see	#createTempFile(String, String, boolean)
     */
    public static File writeToTempFile(final InputStream is, final String prefix, final String suffix, final boolean deleteOnExit)
		throws IOException
    {
	File tempFile = createTempFile(prefix, suffix, deleteOnExit);
	writeStreamToFile(is, tempFile);
	return tempFile;
    }

    /**
     * Write an input stream to the given file.
     *
     * @param is	The input stream to write.
     * @param f		The output file to write to.
     * @throws 	IOException if anything goes wrong.
     */
    public static void writeStreamToFile(final InputStream is, final File f)
		throws IOException
    {
	try (OutputStream os = Files.newOutputStream(f.toPath())) {
	    byte[] buffer = new byte[FILE_BUFFER_SIZE];
	    int len;
	    while ((len = is.read(buffer)) > 0) {
		os.write(buffer, 0, len);
	    }
	    os.flush();
	}
    }

    /**
     * Write a string to the given file.
     *
     * @param string	The string to write.
     * @param f		The output file to write to.
     * @param cs	Character set to use in translating chars to bytes.
     * @throws 	IOException if anything goes wrong.
     */
    public static void writeStringToFile(final String string, final File f, final Charset cs)
		throws IOException
    {
	try (BufferedWriter writer = Files.newBufferedWriter(f.toPath(), cs)) {
	    writer.write(string);
	    writer.flush();
	}
    }

    /**
     * Check if the given file or directory is readable by the
     * current user (process owner).
     *
     * @param	path	The path to check.
     * @return		Whether the given path is readable by
     *			simply trying to read from it.
     */
    public static boolean pathIsReadable(final String path) {
	File f = new File(path);
	Logging.Debug("FileUtilities.pathIsReadable(String path='%1$s', Path='%2$s')", path, f.getPath());
	if (!f.exists() || !f.canRead()) {
	    Logging.Debug("\tpath does not exist or is not readable by current user");
	    return false;
	}
	// Cannot use an InputStream to read from a directory, so the above checks are fine
	// in that case
	if (f.isDirectory()) {
	    Logging.Debug("\tpath is a directory, return true");
	    return true;
	}
	Logging.Debug("\tpath exists and is readable and not a directory, so try opening a stream...");
	try (InputStream fis = Files.newInputStream(f.toPath())) {
	    Logging.Debug("\tpath exists and is readable by current user");
	    return true;
	}
	catch (Exception ex) {
	    Logging.Debug("Exception %1$s trying to open file '%2$s' for reading.", Exceptions.toString(ex, true), path);
	}
	Logging.Debug("\tsome unexpected exception was thrown trying to read the file, return false");
	return false;
    }

    /**
     * Count the number of lines in the given file. See the contract for
     * {@link BufferedReader#readLine} for what constitutes a line for this purpose.
     *
     * @param	f	The file to inspect.
     * @return	The count of the number of lines in the file.
     * @throws	IOException if the file couldn't be read.
     */
    public static int countLines(final File f)
	    throws IOException
    {
	try (BufferedReader r = new BufferedReader(new FileReader(f))) {
	    String line;
	    int lines = 0;
	    while ((line = r.readLine()) != null) {
		lines++;
	    }
	    return lines;
	}
    }

    /**
     * Compress the given file to <code>"<i>name</i>.gz"</code> and remove the original.
     * <p> This mimics the operation of the {@code "gzip"} command line tool, which
     * only leaves the <code>"<i>name</i>.gz"</code> file (and also {@code "gunzip"} which
     * leaves the original file only).
     *
     * @param	inputFile	The input file (in the proper directory).
     * @throws	IOException if something happened during the compression.
     * @see #compressFile(File, String, boolean)
     */
    public static void compressFile(final File inputFile)
	    throws IOException
    {
	compressFile(inputFile, "", true);
    }

    /**
     * Compress the given file to <code>"<i>name</i>.gz"</code> and optionally remove the original.
     *
     * @param	inputFile	The input file (in the proper directory).
     * @param	outputName	Possible output file name (can be empty or null for default).
     * @param	delete		Whether to delete the original file once compressed.
     * @throws	IOException if something happened during the compression.
     * @see #compressFile(File)
     */
    public static void compressFile(final File inputFile, final String outputName, final boolean delete)
	    throws IOException
    {
	String outName = CharUtil.isNullOrEmpty(outputName)
		? inputFile.getPath() + COMPRESS_EXT
		: decorate(outputName, null, COMPRESS_EXT).getPath();

	try (InputStream fis = Files.newInputStream(inputFile.toPath());
	     GZIPOutputStream gos = new GZIPOutputStream(Files.newOutputStream(Paths.get(outName)), FILE_BUFFER_SIZE, true))
	{
	    byte[] buffer = new byte[FILE_BUFFER_SIZE];
	    int len;
	    while ((len = fis.read(buffer)) > 0) {
		gos.write(buffer, 0, len);
	    }
	    gos.flush();
	}

	// Now remove the original file (if requested, and possible)
	if (delete) {
	    inputFile.delete();
	}
    }

    /**
     * Uncompress the given file from <code>"<i>name</i>.gz"</code> to just <code>"<i>name</i>"</code>
     * and remove the original compressed file.
     * <p> This mimics the operation of the {@code "gunzip"} command line tool, which
     * only leaves the <code>"<i>name</i>"</code> file (and also {@code "gzip"} which
     * leaves the compressed file only).
     *
     * @param	inputFile	The input file (in the proper directory).
     * @throws	IOException if something happened during the decompression.
     * @throws	IllegalArgumentException (with no message) if the input
     *		file name doesn't end with {@link #COMPRESS_EXT}.
     * @see #uncompressFile(File, String, boolean)
     */
    public static void uncompressFile(final File inputFile)
	    throws IOException
    {
	uncompressFile(inputFile, "", true);
    }

    /**
     * Uncompress the given file from <code>"<i>name</i>.gz"</code> to just <code>"<i>name</i>"</code>
     * and optionally remove the original compressed file.
     *
     * @param	inputFile	The input file (in the proper directory).
     * @param	outputName	Possible output file name (can be empty or null for default).
     * @param	delete		Whether to delete the original file once uncompressed.
     * @throws	IOException if something happened during the decompression.
     * @throws	IllegalArgumentException (with no message) if the input
     *		file name doesn't end with {@link #COMPRESS_EXT}.
     * @see #uncompressFile(File)
     */
    public static void uncompressFile(final File inputFile, final String outputName, final boolean delete)
	    throws IOException
    {
	String inputName = inputFile.getPath();
	String outName;

	if (inputName.endsWith(COMPRESS_EXT)) {
	    if (CharUtil.isNullOrEmpty(outputName))
		outName = inputName.substring(0, inputName.length() - COMPRESS_EXT.length());
	    else
		outName = outputName;
	}
	else {
	    throw new Intl.IllegalArgumentException("util#fileutil.wrongExtension", COMPRESS_EXT);
	}

	try (GZIPInputStream gis = new GZIPInputStream(Files.newInputStream(inputFile.toPath()), FILE_BUFFER_SIZE);
	     OutputStream fos = Files.newOutputStream(Paths.get(outName)))
	{
	    byte[] buffer = new byte[FILE_BUFFER_SIZE];
	    int len;
	    while ((len = gis.read(buffer)) > 0) {
		fos.write(buffer, 0, len);
	    }
	    fos.flush();
	}

	// Now remove the original file (if requested and possible)
	if (delete) {
	    inputFile.delete();
	}
    }

    /**
     * Rename the given file to the new name.
     *
     * @param	currentFile	Current file (full path).
     * @param	newName		New file name (relative to source directory).
     * @throws	IOException if the rename doesn't succeed.
     */
    public static void renameFile(final File currentFile, final String newName)
	    throws IOException
    {
	Path source = currentFile.toPath();
	Files.move(source, source.resolveSibling(newName), StandardCopyOption.ATOMIC_MOVE);
    }

    /**
     * Check if the name is the same casing as the file on the disk.
     *
     * @param	f	Candidate abstract file name.
     * @return	Whether or not the name as given is the same case as the name on disk.
     *		Will also return {@code false} if the fully-resolved name does not
     *		name an existing file.
     */
    public static boolean checkNameCase(final File f) {
	try {
	    String splitString = Matcher.quoteReplacement(Environment.fileSeparator());
	    Path realPath = f.toPath().toRealPath();
	    String realPathString = realPath.toString();
	    String fixedPath = f.getPath().replaceAll("[\\\\/]", splitString);
	    String[] realParts = realPathString.split(splitString);
	    String[] fixedParts = fixedPath.split(splitString);
	    for (int i = fixedParts.length - 1, j = realParts.length - 1; i>= 0 && j >= 0; i--) {
		if (fixedParts[i].equals("."))
		    continue;
		if (fixedParts[i].equals("..")) {
		    i--;
		    continue;
		}
		if (!fixedParts[i].equals(realParts[j]))
		    return false;
		j--;
	    }
	}
	catch (IOException ioe) {
	    return false;
	}
	return true;
    }

    /**
     * Check the existence and permissions of the given file.
     *
     * @param	f	Candidate file name.
     * @param	flags	Permissions flags to check.
     * @return	Whether the file exists and has the given permissions:
     *		{@code "d", "D", "dr", "DR", "f", "F", "fr", "FR", "fw", "FW", "fx", "FX" }.
     * @throws IllegalArgumentException if the flags value is not legal.
     */
    public static boolean exists(final File f, final String flags) {
	if (flags == null || flags.isEmpty())
	    throw new Intl.IllegalArgumentException("util#fileutil.invalidFlags", flags);

	boolean ret = false;
	String flag = flags;

	if (flag.length() > 1 && flag.charAt(flag.length() - 1) == '+') {
	    if (checkNameCase(f))
		flag = flag.replace("+", "");
	    else
		return ret;
	}

	switch (flag.charAt(0)) {
	    case 'd':
	    case 'D':
		if (flag.length() == 1) {
		    ret = f.exists() && f.isDirectory();
		}
		else if (flag.length() == 2) {
		    switch (flag.charAt(1)) {
			case 'r':
			case 'R':
			    ret = canReadDir(f);
			    break;
			default:
			    throw new Intl.IllegalArgumentException("util#fileutil.invalidFlags", flags);
		    }
		}
		else {
		    throw new Intl.IllegalArgumentException("util#fileutil.invalidFlags", flags);
		}
		break;

	    case 'f':
	    case 'F':
		if (flag.length() == 1) {
		    ret = f.exists() && f.isFile();
		}
		else if (flag.length() == 2) {
		    switch (flag.charAt(1)) {
			case 'r':
			case 'R':
			    ret = canRead(f);
			    break;
			case 'w':
			case 'W':
			    ret = canWrite(f);
			    break;
			case 'x':
			case 'X':
			    ret = canExecute(f);
			    break;
			default:
			    throw new Intl.IllegalArgumentException("util#fileutil.invalidFlags", flags);
		    }
		}
		else {
		    throw new Intl.IllegalArgumentException("util#fileutil.invalidFlags", flags);
		}
		break;

	    default:
		throw new Intl.IllegalArgumentException("util#fileutil.invalidFlags", flags);
	}

	return ret;
    }

    /**
     * Test to see if the file given by the name is actually readable.
     *
     * @param	file	The local file to test.
     * @return		Whether or not the file exists, is a regular file,
     *			and the permissions include read access.
     * @see	#canReadPath
     */
    public static boolean canRead(final File file) {
	try {
	    return canReadPath(file.toPath(), false);
	}
	catch (InvalidPathException ipe) {
	    return false;
	}
    }

    /**
     * Test to see if the directory given by the name is actually readable.
     *
     * @param	dir	The local directory to test.
     * @return		Whether or not the directory exists, is actually a
     *			directory, and the permissions include read access.
     * @see	#canReadPath
     */
    public static boolean canReadDir(final File dir) {
	try {
	    return canReadPath(dir.toPath(), true);
	}
	catch (InvalidPathException ipe) {
	    return false;
	}
    }

    /**
     * Test to see if the file given by the name is actually readable.
     * <p> The problem this solves is that no matter the permissions on the actual
     * file on Linux, the "root" user can "read" it, which is not what we want.
     * We actually need to test the file permissions.  So, do that on Linux, yet
     * the regular test is sufficient for Windows (plus on Java 10 the POSIX
     * object isn't available there).
     *
     * @param	path	The local file to test.
     * @param	asDir	Whether to treat this file as a directory ({@code true}),
     *			or as a regular file ({@code false}).
     * @return		Whether or not the path exists, is the kind of file/directory
     *			we're expecting, and the permissions include read access.
     */
    private static boolean canReadPath(final Path path, final boolean asDir) {
	if (!Files.exists(path))
	    return false;

	if (asDir) {
	    if (!Files.isDirectory(path))
		return false;
	}
	else {
	    if (!Files.isRegularFile(path))
		return false;
	}

	if (Environment.isWindows()) {
	    return Files.isReadable(path);
	}
	else {
	    // TODO: we need to follow down the parent path and check these permissions at each level
	    try {
		Map<String, Object> attrs = Files.readAttributes(path, "posix:permissions");
		@SuppressWarnings("unchecked")
		Set<PosixFilePermission> permissions = (Set<PosixFilePermission>) attrs.get("permissions");
		return permissions.contains(PosixFilePermission.OTHERS_READ)
		    || permissions.contains(PosixFilePermission.GROUP_READ)
		    || permissions.contains(PosixFilePermission.OWNER_READ);
	    }
	    catch (IOException ioe) {
		return false;
	    }
	}
    }

    /**
     * Test to see if the file given by the name is actually writable.
     * <p> The problem this solves is that no matter the permissions on the actual
     * file on Linux, the "root" user can "write" to it, which is not what we want.
     * We actually need to test the file permissions.  So, do that on Linux, yet
     * the regular test is sufficient for Windows (plus on Java 10 the POSIX
     * object isn't available there).
     *
     * @param	file	The local file to test.
     * @return		Whether or not the file permissions include write access for this file.
     */
    public static boolean canWrite(final File file) {
	try {
	    Path path = file.toPath();
	    if (Environment.isWindows()) {
		return Files.isWritable(path);
	    }
	    else {
		// TODO: we need to follow down the parent path and check these permissions at each level
		try {
		    Map<String, Object> attrs = Files.readAttributes(path, "posix:permissions");
		    @SuppressWarnings("unchecked")
		    Set<PosixFilePermission> permissions = (Set<PosixFilePermission>) attrs.get("permissions");
		    return permissions.contains(PosixFilePermission.OTHERS_WRITE)
			|| permissions.contains(PosixFilePermission.GROUP_WRITE)
			|| permissions.contains(PosixFilePermission.OWNER_WRITE);
		}
		catch (IOException ioe) {
		    return false;
		}
	    }
	}
	catch (InvalidPathException ipe) {
	    return false;
	}
    }

    /**
     * Test if a file is an executable program.
     * <p> On non-Windows platforms we can use {@link File#canExecute} because
     * there are flags to that effect. On Windows, however, every file is
     * marked as executable, but we can check the file extension to see if it
     * is in the PATHEXT list and determine that way.
     *
     * @param path	The path to the file in question.
     * @return		Whether or not the file is an "executable".
     */
    public static boolean canExecute(final File path) {
	// An obvious first check...
	if (!path.exists())
	    return false;

	if (Environment.isWindows()) {
	    String name = path.getName();
	    int dotPos  = name.lastIndexOf('.');
	    if (dotPos >= 0) {
		String ext = name.substring(dotPos).toUpperCase();
		if (EXECUTABLE_EXTENSIONS == null) {
		    String exts = System.getenv("PATHEXT");
		    if (exts == null) {
			exts = DEFAULT_EXECUTABLES;
		    }
		    EXECUTABLE_EXTENSIONS = exts.split(Environment.pathSeparator());
		    Arrays.sort(EXECUTABLE_EXTENSIONS);
		}
		return (Arrays.binarySearch(EXECUTABLE_EXTENSIONS, ext) >= 0);
	    }
	}
	else {
	    try {
		return path.canExecute();
	    }
	    catch (SecurityException se) {
		// According to the Javadoc for "canExecute" this means
		// execute access is denied, so, "NO".
	    }
	}
	return false;
    }

    /**
     * Construct a file reader for the given file and charset.
     * <p> If the file name is <code>"@"</code> or <code>"-"</code>, read the system standard input.
     *
     * @param	file	The local file to start reading.
     * @param	cs	The charset to use (can be {@code null} to use the platform default).
     * @return		A buffered reader suitable for reading the file.
     * @throws	IllegalArgumentException if the file cannot be found, is not readable,
     *			or is larger than our internal limit.
     * @throws	IOException if there is a problem starting to read the file
     */
    public static BufferedReader getFileReader(final File file, final Charset cs)
		throws IOException
    {
	InputStream in;

	String filePath = file.getPath();
	if (filePath.equals("@") || filePath.equals("-")) {
	    in = System.in;
	}
	else {
	    if (!file.exists() || !canRead(file)) {
		throw new Intl.IllegalArgumentException("util#fileutil.fileNotFound", file.getPath());
	    }

	    long size = file.length();
	    if (size > FILE_STRING_SIZE_LIMIT) {
		throw new Intl.IllegalArgumentException("util#fileutil.fileTooBig", size);
	    }

	    in = Files.newInputStream(file.toPath());
	}

	CharsetDecoder decoder = (cs == null ? Charset.defaultCharset() : cs).newDecoder();
	decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
	decoder.onMalformedInput(CodingErrorAction.REPORT);

	return new BufferedReader(new InputStreamReader(in, decoder));
    }

    /**
     * Construct a file writer for the given file and charset.
     * <p> If the file name is <code>"@"</code> or <code>"-"</code>, write to the standard output.
     *
     * @param	file	Name of the local file to write to.
     * @param	cs	The charset to use for encoding the text (can be {@code null} to use
     *			the platform default).
     * @return		A buffered writer suitable for writing the file.
     * @throws	IOException if there is a problem starting to write the file.
     */
    public static PrintWriter getFileWriter(final File file, final Charset cs)
		throws IOException
    {
	PrintWriter out;

	String filePath = file.getPath();
	if (filePath.equals("@") || filePath.equals("-")) {
	    if (cs != null && !cs.equals(Charset.defaultCharset())) {
		throw new Intl.IllegalArgumentException("util#fileutil.noCSStdOutput");
	    }
	    Console console = System.console();
	    if (console == null) {
		throw new Intl.IllegalArgumentException("util#fileutil.noConsole");
	    }
	    out = console.writer();
	}
	else {
	    CharsetEncoder encoder = (cs == null ? Charset.defaultCharset() : cs).newEncoder();
	    encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
	    encoder.onMalformedInput(CodingErrorAction.REPORT);

	    OutputStream fos = Files.newOutputStream(file.toPath());
	    OutputStreamWriter osw = new OutputStreamWriter(fos, encoder);

	    out = new PrintWriter(osw);
	}

	return out;
    }

    /**
     * Read the given local file and produce a single string from the contents.
     * <p> Default charset and tab width (8).
     *
     * @param	file	The local file to read.
     * @return		The complete contents of the file as a {@link String},
     *			with line endings translated to Unix conventions (i.e., only
     *			{@code \n}).
     * @throws	IllegalArgumentException if the file size is over 2MB or 2,097,512 bytes
     *			(arbitrary limit).
     * @throws	IOException if there is a problem reading the file.
     */
    public static String readFileAsString(final File file)
	throws IOException
    {
	return readFileAsString(file, null, 8);
    }

    /**
     * Read the given local file and produce a single string from the contents.
     *
     * @param	file	The local file to read.
     * @param	cs	The character set to use to decode the file contents. Can be
     *			{@code null} in which case the system default is used.
     * @return		The complete contents of the file as a {@link String},
     *			with line endings translated to Unix conventions (i.e., only
     *			{@code \n}).
     * @throws	IllegalArgumentException if the file size is over 2MB or 2,097,512 bytes
     *			(arbitrary limit).
     * @throws	IOException if there is a problem reading the file.
     */
    public static String readFileAsString(final File file, final Charset cs)
	throws IOException
    {
	return readFileAsString(file, cs, 8);
    }

    /**
     * Read the given local file and produce a single string from the contents.
     *
     * @param	file	The local file to read.
     * @param	cs	The character set to use to decode the file contents. Can be
     *			{@code null} in which case the system default is used.
     * @param	tabWidth	In order to convert tab characters to spaces, specify
     *				a non-zero width.
     * @return		The complete contents of the file as a {@link String},
     *			with line endings translated to Unix conventions (i.e., only
     *			{@code \n}).
     * @throws	IllegalArgumentException if the file size is over 2MB or 2,097,512 bytes
     *			(arbitrary limit).
     * @throws	IOException if there is a problem reading the file.
     */
    public static String readFileAsString(final File file, final Charset cs, final int tabWidth)
	throws IOException
    {
	try (BufferedReader reader = getFileReader(file, cs))
	{
	    StringBuilder buf = new StringBuilder((int) file.length());
	    String line;
	    while ((line = reader.readLine()) != null) {
		if (tabWidth > 0) {
		    int ix = 0, tabPos = 0, iy;
		    while ((iy = line.indexOf('\t', ix)) >= 0) {
			tabPos += (iy - ix);
			buf.append(line.substring(ix, iy));
			int spaces = tabWidth - (tabPos % tabWidth);
			for (int i = 0; i < spaces; i++) {
			    buf.append(' ');
			}
			tabPos += spaces;
			ix = iy + 1;
		    }
		    if (ix < line.length()) {
			buf.append(line.substring(ix));
		    }
		}
		else {
		    buf.append(line);
		}
		buf.append('\n');
	    }
	    return buf.toString();
	}
    }

    /**
     * Read the given local file and produce a list of lines from the contents.
     * <p> Default charset and tab width (8).
     *
     * @param	file	The local file to read.
     * @return		The complete contents of the file as a {@code List<String>}
     *			with line endings translated to Unix conventions (i.e., only
     *			{@code \n}).
     * @throws	IllegalArgumentException if the file size is over 2MB or 2,097,512 bytes
     *			(arbitrary limit).
     * @throws	IOException if there is a problem reading the file.
     */
    public static List<String> readFileAsLines(final File file)
	throws IOException
    {
	return readFileAsLines(file, null, 8);
    }

    /**
     * Read the given local file and produce a list of lines from the contents.
     *
     * @param	file	The local file to read.
     * @param	cs	The character set to use to decode the file contents. Can be
     *			{@code null} in which case the system default is used.
     * @return		The complete contents of the file as a {@code List<String>}
     *			with line endings translated to Unix conventions (i.e., only
     *			{@code \n}).
     * @throws	IllegalArgumentException if the file size is over 2MB or 2,097,512 bytes
     *			(arbitrary limit).
     * @throws	IOException if there is a problem reading the file.
     */
    public static List<String> readFileAsLines(final File file, final Charset cs)
	throws IOException
    {
	return readFileAsLines(file, cs, 8);
    }

    /**
     * Read the given local file and produce a list of lines from the contents.
     *
     * @param	file	The local file to read.
     * @param	cs	The character set to use to decode the file contents. Can be
     *			{@code null} in which case the system default is used.
     * @param	tabWidth	In order to convert tab characters to spaces, specify
     *				a non-zero width.
     * @return		The complete contents of the file as a {@code List<String>}
     *			with line endings translated to Unix conventions (i.e., only
     *			{@code \n}).
     * @throws	IllegalArgumentException if the file size is over 2MB or 2,097,512 bytes
     *			(arbitrary limit).
     * @throws	IOException if there is a problem reading the file.
     */
    public static List<String> readFileAsLines(final File file, final Charset cs, final int tabWidth)
	throws IOException
    {
	List<String> lines = new ArrayList<>();

	try (BufferedReader reader = getFileReader(file, cs))
	{
	    StringBuilder buf = new StringBuilder(2048);
	    String line;
	    while ((line = reader.readLine()) != null) {
		if (tabWidth > 0) {
		    buf.setLength(0);
		    int ix = 0, tabPos = 0, iy;
		    while ((iy = line.indexOf('\t', ix)) >= 0) {
			tabPos += (iy - ix);
			buf.append(line.substring(ix, iy));
			int spaces = tabWidth - (tabPos % tabWidth);
			for (int i = 0; i < spaces; i++) {
			    buf.append(' ');
			}
			tabPos += spaces;
			ix = iy + 1;
		    }
		    buf.append(line.substring(ix));
		    lines.add(buf.toString());
		}
		else {
		    lines.add(line);
		}
	    }
	}

	return lines;
    }

    /**
     * Read a text file without any other interpretations except charset.
     *
     * @param	file	The local file to read.
     * @param	cs	The character set to use to decode the file contents. Can be
     *			{@code null} in which case the system default is used.
     * @return	A string with the raw file contents if it can be read.
     * @throws	IllegalArgumentException if the file is bigger than our limit.
     * @throws	IOException if there was a problem reading the file or interpreting
     *		the character set.
     */
    public static String readRawText(final File file, final Charset cs)
		throws IOException
    {
	try (Reader reader = getFileReader(file, cs))
	{
	    StringBuilder buf = new StringBuilder((int) file.length());
	    int ret = -1;
	    char[] chars = new char[FILE_BUFFER_SIZE];

	    while ((ret = reader.read(chars)) != -1) {
		buf.append(chars, 0, ret);
	    }

	    return buf.toString();
	}
    }

    /**
     * Write a text file using the given data.
     *
     * @param	chars	The output text to write.
     * @param	file	File to write to.
     * @param	cs	The character set to use to encode the file contents. Can be
     *			{@code null} in which case the system default is used.
     * @return	Count of bytes written.
     * @throws	IOException if there is a problem writing to the file.
     */
    public static int writeRawText(final CharSequence chars, final File file, final Charset cs)
		throws IOException
    {
	try (PrintWriter writer = getFileWriter(file, cs))
	{
	    int len = chars.length();

	    writer.append(chars);
	    writer.flush();

	    return len;
	}
    }

    /**
     * Unpack some number of files from the .jar file into a temp directory,
     * and return the temp directory object.
     *
     * @param jarFile		The {@link JarFile} object to read things from.
     * @param dirName		Directory name where the files reside inside the .jar
     * @param exts		A list of filename extensions that we want to unpack
     *				(comma-separated).
     * @param prefix		The temp directory name prefix.
     * @param deleteOnExit	Whether the files are to be deleted on exit.
     * @return 	The temp directory name (inside the TEMP or TMP location).
     * @throws	IOException if there are problems doing any of this.
     */
    public static File unpackFiles(final JarFile jarFile, final String dirName, final String exts, final String prefix, final boolean deleteOnExit)
		throws IOException
    {
	Path tempDirPath = Files.createTempDirectory(prefix);
	File tempDir     = tempDirPath.toFile();

	if (deleteOnExit)
	    tempDir.deleteOnExit();

	String[] extensions = exts.split("\\s*[,;:|]\\s*");

	for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements(); ) {
	    JarEntry entry = e.nextElement();
	    String name    = entry.getName();

	    // Make sure we don't "slip" outside the destination dir bounds with ".." or similar
	    File trialFile = new File(tempDir, name);
	    if (!trialFile.toPath().normalize().startsWith(tempDirPath))
		throw new ZipException(Intl.formatString("util#fileutil.outsideDir", name));

	    if (name.startsWith(dirName)) {
		for (String ext : extensions) {
		    if (name.endsWith(ext)) {
			InputStream is  = jarFile.getInputStream(entry);
			int namePos     = name.lastIndexOf('/');
			String fileName = namePos < 0 ? name : name.substring(namePos + 1);

			File f = new File(tempDir, fileName);
			f.createNewFile();

			if (deleteOnExit)
			    f.deleteOnExit();

			writeStreamToFile(is, f);
		    }
		}
	    }
	}

	return tempDir;
    }

    /**
     * Command line invocation of the {@link #pathIsReadable} method.
     * <p> Returns process exit code of 0 if path is readable and
     * print "true" on stdout.  Returns exit code 100 if path is not
     * readable by the current process owner and prints "false".
     * Also if there is more or less than one path on the command line.
     *
     * @param	args	The command line arguments, which should be the
     *			single path to test.
     */
    public static void main(final String args[]) {
	if (args.length == 1) {
	    if (pathIsReadable(args[0])) {
		System.out.println("true");
		return;
	    }
	}
	System.out.println("false");
	System.exit(100);
    }

}
