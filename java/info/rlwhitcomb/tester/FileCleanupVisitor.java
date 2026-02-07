/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 Roger L. Whitcomb.
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
 *	File visitor used by Tester to do cleanup of temp files on exit.
 *
 * History:
 *  06-Feb-26 rlw #800	New code to implement cleanup of temp files.
 */
package info.rlwhitcomb.tester;

import info.rlwhitcomb.directory.Match;
import info.rlwhitcomb.util.Intl;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.List;


/**
 * File visitor used to delete (temp) files.
 */
public class FileCleanupVisitor extends SimpleFileVisitor<Path>
{
	/**
	 * Report operations as they are performed?
	 */
	private boolean report;

	/**
	 * Case-sensitive compare or not?
	 */
	private boolean caseSensitive;

	/**
	 * List of file specs to match.
	 */
	private List<String> matchingSpecs = new ArrayList<>();


	/**
	 * Constructor to set any number of (wildcard) file specs that should be
	 * deleted by the body of this process. Note: the file specs are matched
	 * to the incoming file name (only), not to the complete or partial path of
	 * the file.
	 *
	 * @param verbose   Whether to report every actual deletion or not.
	 * @param cased     {@code true} to be case-sensitive, {@code false} means case
	 *                  does not matter.
	 * @param fileSpecs Any number of file specs that may be wild.
	 */
	public FileCleanupVisitor(final boolean verbose, final boolean cased, final String... fileSpecs) {
	    report = verbose;
	    caseSensitive = cased;

	    for (String spec : fileSpecs) {
		matchingSpecs.add(spec);
	    }
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	    String fileName = file.getFileName().toString();

	    // if matches any of list of file specs specified at construction time then delete it
	    for (String spec : matchingSpecs) {
		if (Match.stringMatch(fileName, spec, caseSensitive)) {
		    FileSystemProvider provider = file.getFileSystem().provider();

		    if (report)
			Intl.outFormat("tester#cleanupFileName", file.toString());

		    provider.delete(file);
		    break;
		}
	    }

	    return FileVisitResult.CONTINUE;
	}

}

