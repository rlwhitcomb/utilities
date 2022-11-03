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
 *  20-Sep-22 rlw #448: Initial coding.
 *  21-Oct-22 rlw #473: Add flags value to the mix.
 *  24-Oct-22 rlw #473: Tiny bit of refactoring. Add "ignoreCase" flag.
 */
package info.rlwhitcomb.util;

import info.rlwhitcomb.directory.Match;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Optional;


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
	 * The flags ({@code "d", "dr", "f", "fr", "fw", "fx"}).
	 */
	private Optional<String> flags;
	/**
	 * Whether to ignore case when matching names.
	 */
	private boolean ignoreCase;


	/**
	 * Construct using the given wildcard file specification, and case-sensitive
	 * name comparison.
	 *
	 * @param spec	The wildcard pattern of files (or directories) to accept.
	 */
	public WildcardFilter(final String spec) {
	    this(spec, null, false);
	}

	/**
	 * Construct using the given wildcard file specification, as well as
	 * flags value, and case-sensitive comparison.
	 *
	 * @param spec	A wildcard file specification, used to match the file name.
	 * @param flag	One of a possible set of flags to further qualify the file
	 *		to be matched by name.
	 */
	public WildcardFilter(final String spec, final String flag) {
	    this(spec, flag, false);
	}

	/**
	 * Construct using the given wildcard file specification, as well as
	 * flags value, with option to ignore case of names.
	 *
	 * @param spec		A wildcard file specification, used to match the file name.
	 * @param flag		One of a possible set of flags to further qualify the file
	 *			to be matched by name.
	 * @param ignore	Whether to ignore name case when matching.
	 */
	public WildcardFilter(final String spec, final String flag, final boolean ignore) {
	    wildcardSpec = spec;
	    anyWild = Match.hasWildCards(wildcardSpec);
	    flags = Optional.ofNullable(flag);
	    ignoreCase = ignore;
	}

	/**
	 * Does the given name match the pattern?
	 *
	 * @param file	File to match (by name and flags).
	 * @return	Whether the name matches the pattern, and flags (if any).
	 */
	private boolean matches(final File file) {
	    boolean matched;
	    String name = file.getName();
	    if (anyWild) {
		matched = Match.stringMatch(name, wildcardSpec, !ignoreCase);
	    } else {
		matched = ignoreCase ? name.equalsIgnoreCase(wildcardSpec) : name.equals(wildcardSpec);
	    }
	    if (matched && flags.isPresent()) {
		matched = FileUtilities.exists(file, flags.get());
	    }
	    return matched;
	}

	/**
	 * Method of {@link FilenameFilter}.
	 */
	@Override
	public boolean accept(final File dir, final String name) {
	    return matches(new File(dir, name));
	}

	/**
	 * Method of {@link FileFilter}.
	 */
	@Override
	public boolean accept(final File pathname) {
	    return matches(pathname);
	}

}

