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
 *	Both a FileFilter and a FilenameFilter based on a wildcard pattern.
 *
 * History:
 *  20-Sep-22 rlw #448:	Initial coding.
 */
package info.rlwhitcomb.util;

import info.rlwhitcomb.directory.Match;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;


/**
 * A {@link FileFilter} and {@link FilenameFilter} for normal wildcard file
 * name strings (containing {@code '*'} or {@code '?'} characters).
 * <p> Currently, wildcards are only supported in file names, not
 * directory names. This may change in the future, or can be worked
 * around in the calling code.
 */
public class WildcardFilter implements FileFilter, FilenameFilter
{
	/**
	 * The wildcard spec for filtering.
	 */
	private String wildcardSpec;
	/**
	 * Whether this pattern actually has any wildcard characters.
	 */
	private boolean anyWild;


	/**
	 * Construct using the given wildcard file specification.
	 *
	 * @param spec	The wildcard pattern of files (or directories) to accept.
	 */
	public WildcardFilter(final String spec) {
	    wildcardSpec = spec;
	    anyWild = Match.hasWildCards(wildcardSpec);
	}

	/**
	 * Does the given name match the pattern.
	 *
	 * @param name	File name to match.
	 * @return	Whether the name matches the pattern.
	 */
	private boolean matches(final String name) {
	    if (anyWild) {
		return Match.stringMatch(name, wildcardSpec, true);
	    } else {
		return name.equals(wildcardSpec);
	    }
	}

	/**
	 * Method of {@link FilenameFilter}.
	 */
	@Override
	public boolean accept(final File dir, final String name) {
	    return matches(name);
	}

	/**
	 * Method of {@link FileFilter}.
	 */
	@Override
	public boolean accept(final File pathname) {
	    return matches(pathname.getName());
	}

}

