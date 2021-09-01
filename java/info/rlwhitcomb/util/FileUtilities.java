/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2010-2011,2013-2017,2019-2021 Roger L. Whitcomb.
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
 */
package info.rlwhitcomb.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.iharder.b64.Base64;


/**
 * Utility class for dealing with files.
 */
public final class FileUtilities
{
    /** Source of randomness for {@link #getRandomName}. */
    private static SecureRandom random = new SecureRandom();

    /** Buffer size for the file compression method. */
    private static final int BUFFER_SIZE = 65_536;

    /** Maximum size (2MB) of a file to read into a single string. */
    private static final long FILE_STRING_SIZE_LIMIT = 2_097_152L;

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
     * Private constructor because this is a utility class with only static methods.
     */
    private FileUtilities() {
    }

    /**
     * Compute the bare name part of the file.
     *
     * @param f The file to examine.
     * @return Only the name portion of the given file, without the path
     * or the extension (if any).
     */
    public static String nameOnly(File f) {
	String name = f.getName();
	int dotPos  = name.lastIndexOf('.');
	if (dotPos < 0)
	    return name;
	else
	    return name.substring(0, dotPos);
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
    public static File decorate(String name, File dir, String ext) {
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
    public static void copyFile(File in, File out)
		throws IOException, FileNotFoundException
    {
	Files.copy(in.toPath(), out.toPath(),
		StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
    }

    /**
     * Compare two file byte-by-byte.
     * <p> NOTE: this is only meant to compare two "small-ish" files
     * because it reads the entire contents into memory.
     * @see Files#readAllBytes
     * @param file1	The first file to compare.
     * @param file2	The second file to compare.
     * @return	{@code true} if the two files compare byte-for-byte,
     *		or {@code false} if not.
     * @throws IOException if something went wrong.
     */
    public static boolean compareFiles(File file1, File file2)
	throws IOException
    {
	Path path1 = file1.toPath();
	Path path2 = file2.toPath();
	byte[] bytes1 = Files.readAllBytes(path1);
	byte[] bytes2 = Files.readAllBytes(path2);
	return Arrays.equals(bytes1, bytes2);
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
    public static String getRandomName(String prefix, String suffix) {
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
    public static File createTempFile(String prefix, String suffix, boolean deleteOnExit)
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
    public static File createTempFile(String prefix, boolean deleteOnExit)
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
    public static File createTempFile(String prefix)
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
    public static File writeToTempFile(InputStream is, String prefix, String suffix, boolean deleteOnExit)
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
    public static void writeStreamToFile(InputStream is, File f)
		throws IOException
    {
	try (OutputStream os = Files.newOutputStream(f.toPath())) {
	    byte[] buffer = new byte[BUFFER_SIZE];
	    int len;
	    while ((len = is.read(buffer)) > 0) {
		os.write(buffer, 0, len);
	    }
	    os.flush();
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
    public static boolean pathIsReadable(String path) {
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
	    Logging.Debug("Exception %1$s trying to open file '%2$s' for reading.", ExceptionUtil.toString(ex, true), path);
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
    public static int countLines(File f)
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
     */
    public static void compressFile(File inputFile)
	    throws IOException
    {
	String outputName = inputFile.getPath() + COMPRESS_EXT;
	try (InputStream fis = Files.newInputStream(inputFile.toPath());
	     GZIPOutputStream gos = new GZIPOutputStream(Files.newOutputStream(Paths.get(outputName)), BUFFER_SIZE, true))
	{
	    byte[] buffer = new byte[BUFFER_SIZE];
	    int len;
	    while ((len = fis.read(buffer)) > 0) {
		gos.write(buffer, 0, len);
	    }
	    gos.flush();
	}
	// Now remove the original file (if possible)
	inputFile.delete();
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
     */
    public static void uncompressFile(File inputFile)
	    throws IOException
    {
	String inputName = inputFile.getPath();
	String outputName;
	if (inputName.endsWith(COMPRESS_EXT))
	    outputName = inputName.substring(0, inputName.length() - COMPRESS_EXT.length());
	else
	    throw new Intl.IllegalArgumentException("util#fileutil.wrongExtension", COMPRESS_EXT);
	try (GZIPInputStream gis = new GZIPInputStream(Files.newInputStream(inputFile.toPath()), BUFFER_SIZE);
	     OutputStream fos = Files.newOutputStream(Paths.get(outputName)))
	{
	    byte[] buffer = new byte[BUFFER_SIZE];
	    int len;
	    while ((len = gis.read(buffer)) > 0) {
		fos.write(buffer, 0, len);
	    }
	    fos.flush();
	}
	// Now remove the original file (if possible)
	inputFile.delete();
    }

    /**
     * Rename the given file to the new name.
     *
     * @param	currentFile	Current file (full path).
     * @param	newName		New file name (relative to source directory).
     * @throws	IOException if the rename doesn't succeed.
     */
    public static void renameFile(File currentFile, String newName)
	    throws IOException
    {
	Path source = currentFile.toPath();
	Files.move(source, source.resolveSibling(newName), StandardCopyOption.ATOMIC_MOVE);
    }

    /**
     * Test to see if the file given by the name is actually readable.
     *
     * @param	file	The local file to test.
     * @return		Whether or not the file exists, is a regular file,
     *			and the permissions include read access.
     * @see	#canReadPath
     */
    public static boolean canRead(File file) {
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
    public static boolean canReadDir(File dir) {
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
    private static boolean canReadPath(Path path, boolean asDir) {
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
		Set<PosixFilePermission> permissions = (Set<PosixFilePermission>)attrs.get("permissions");
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
    public static boolean canWrite(File file) {
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
		    Set<PosixFilePermission> permissions = (Set<PosixFilePermission>)attrs.get("permissions");
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
    public static boolean canExecute(File path) {
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
     * Read the given local file and produce a single string from the contents.
     * <p> Default charset and tab width (8).
     *
     * @param	file	The local file to read.
     * @return		The complete contents of the file as a {@link String},
     *			with line endings translated to Unix conventions (i.e., only
     *			{@code \n}).
     * @throws	IllegalArgumentException if the file size is over 2MB (arbitrary).
     * @throws	IOException if there is a problem reading the file.
     */
    public static String readFileAsString(File file)
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
     * @throws	IllegalArgumentException if the file size is over 2MB (arbitrary).
     * @throws	IOException if there is a problem reading the file.
     */
    public static String readFileAsString(File file, Charset cs)
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
     * @throws	IllegalArgumentException if the file size is over 2MB (arbitrary).
     * @throws	IOException if there is a problem reading the file.
     */
    public static String readFileAsString(File file, Charset cs, int tabWidth)
	throws IOException
    {
	long size = file.length();
	if (size > FILE_STRING_SIZE_LIMIT) {
	    throw new Intl.IllegalArgumentException("util#fileutil.fileTooBig", size);
	}
	StringBuilder buf = new StringBuilder((int)size);

	CharsetDecoder decoder = (cs == null ? Charset.defaultCharset() : cs).newDecoder();
	decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
	decoder.onMalformedInput(CodingErrorAction.REPORT);

	InputStream fis = Files.newInputStream(file.toPath());
	InputStreamReader isr = new InputStreamReader(fis, decoder);

	try (BufferedReader reader = new BufferedReader(isr))
	{
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
	}

	return buf.toString();
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
    public static File unpackFiles(JarFile jarFile, String dirName, String exts, String prefix, boolean deleteOnExit)
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
    public static void main(String args[]) {
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
