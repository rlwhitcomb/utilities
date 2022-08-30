/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Roger L. Whitcomb.
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
 *	File Information class that uncovers all the basic pieces of information
 *	about a file, regardless of the underlying operating system.
 *
 * History:
 *  29-Aug-22 rlw #453: Initial coding.
 */
package info.rlwhitcomb.directory;

import info.rlwhitcomb.annotations.Scriptable;
import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.FileUtilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;


/**
 * Uncover and report all the basic pieces of information about a file,
 * using the operating system-specific pieces of the Java file API to do so.
 */
public final class FileInfo
{
	/**
	 * The basic indicator of O/S type we're dealing with.
	 */
	private static final boolean IS_WINDOWS = Environment.isWindows();


	/**
	 * The underlying file name and path information.
	 */
	private File file;

	/**
	 * The basic file attributes (type, size, times).
	 */
	private BasicFileAttributes basic;


	/**
	 * Construct one of these objects given the file name.
	 *
	 * @param name  Name of the file we will represent.
	 */
	public FileInfo(final String name) {
	    this(new File(name));
	}

	/**
	 * Construct one of these objects given then underlying file object.
	 *
	 * @param f  The file we represent.
	 */
	public FileInfo(final File f) {
	    file = f;
	    try {
		basic = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
	    }
	    catch (IOException ioe) {
		basic = null;
	    }
	}


	/**
	 * Access the complete, canonical, path of this file.
	 *
	 * @return The complete path name of this file.
	 */
	@Scriptable(order = 1)
	public String getFullPath() {
	    try {
		return file.getCanonicalPath();
	    }
	    catch (IOException ioe) {
		return file.getAbsolutePath();
	    }
	}

	/**
	 * Access the file name plus extension of this file.
	 */
	@Scriptable(order = 2)
	public String getName() {
	    return file.getName();
	}

	/**
	 * Decide if this file is a regular file or not.
	 *
	 * @return Is this a file?
	 */
	@Scriptable(order = 3)
	public boolean isFile() {
	    return basic != null ? basic.isRegularFile() : false;
	}

	/**
	 * Decide if this file is a directory or not.
	 *
	 * @return Is this a directory?
	 */
	@Scriptable(order = 4)
	public boolean isDirectory() {
	    return basic != null ? basic.isDirectory() : false;
	}

	/**
	 * Decide if this file is a symbolic link.
	 *
	 * @return Is this a symbolic link?
	 */
	@Scriptable(order = 5)
	public boolean isLink() {
	    return basic != null ? basic.isSymbolicLink() : false;
	}

	/**
	 * Decide if this file is some other kind of file.
	 *
	 * @param Is this another kind of file?
	 */
	@Scriptable(order = 6)
	public boolean isOther() {
	    return basic != null ? basic.isOther() : false;
	}

	/**
	 * Access the file length.
	 *
	 * @return The file's length (or size).
	 */
	@Scriptable(order = 7)
	public long getLength() {
	    return file.length();
	}

	/**
	 * Access only the name portion of the file path, without any extension.
	 *
	 * @return The bare name of the file, without the extension.
	 */
	@Scriptable(order = 8)
	public String getNameOnly() {
	    return FileUtilities.nameOnly(file);
	}

	/**
	 * Access only the file extension (if any) of this file.
	 *
	 * @return The file extension, if any, starting with ".", or {@code ""} if none.
	 */
	@Scriptable(order = 9)
	public String getExtension() {
	    return FileUtilities.extOnly(file);
	}

	/**
	 * Access the file's creation date and time.
	 *
	 * @return Date and time of file creation.
	 */
	@Scriptable(order = 10)
	public FileTime getCreationTime() {
	    return basic != null ? basic.creationTime() : FileTime.fromMillis(0L);
	}

	/**
	 * Access the file's last access date and time.
	 *
	 * @return Date and time of last access to this file.
	 */
	@Scriptable(order = 11)
	public FileTime getLastAccessTime() {
	    return basic != null ? basic.lastAccessTime() : FileTime.fromMillis(0L);
	}

	/**
	 * Access the file's last modified data and time.
	 *
	 * @return Date and time of last modification to this file.
	 */
	@Scriptable(order = 12)
	public FileTime getLastModifiedTime() {
	    return basic != null ? basic.lastModifiedTime() : FileTime.fromMillis(0L);
	}

}

