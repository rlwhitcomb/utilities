/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022,2025 Roger L. Whitcomb.
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
 *  20-Sep-22 rlw #448	Initial coding.
 *  21-Oct-22 rlw #473	Add flags value to the mix.
 *  24-Oct-22 rlw #473	Tiny bit of refactoring. Add "ignoreCase" flag.
 *  07-Jun-25 rlw #723	Rework to use multiple specs for the file name.
 */
package info.rlwhitcomb.util;

import info.rlwhitcomb.directory.Match;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Optional;
import java.util.regex.Pattern;


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
	 * The wildcard spec(s) for filtering.
	 */
	private String[] wildcardSpecs;

	/**
	 * Whether any of the patterns actually have any wildcard characters.
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
	 * Pattern to use in separating multiple wildcard specs.
	 */
	private static final Pattern MULTI_SPECS = Pattern.compile("\\s*[;:]\\s*");


	/**
	 * Construct using the given wildcard file specification(s), and case-sensitive
	 * name comparison.
	 *
	 * @param specs	The wildcard pattern(s) of files (or directories) to accept.
	 */
	public WildcardFilter(final String specs) {
	    this(specs, null, false);
	}

	/**
	 * Construct using the given wildcard file specification(s), as well as
	 * flags value, and case-sensitive comparison.
	 *
	 * @param specs	The wildcard file specification(s), used to match the file name.
	 * @param flag	One of a possible set of flags to further qualify the file
	 *		to be matched by name.
	 */
	public WildcardFilter(final String specs, final String flag) {
	    this(specs, flag, false);
	}

	/**
	 * Construct using the given wildcard file specification(s), as well as
	 * flags value, with option to ignore case of names.
	 *
	 * @param specs		Wildcard file specification(s), used to match the file name.
	 * @param flag		One of a possible set of flags to further qualify the file
	 *			to be matched by name.
	 * @param ignore	Whether to ignore name case when matching.
	 */
	public WildcardFilter(final String specs, final String flag, final boolean ignore) {
	    wildcardSpecs = MULTI_SPECS.split(specs);
	    anyWild = false;
	    for (String spec : wildcardSpecs) {
		if (Match.hasWildCards(spec)) {
		    anyWild = true;
		    break;
		}
	    }
	    flags = Optional.ofNullable(flag);
	    ignoreCase = ignore;
	}

	/**
	 * Does the given name match one of the patterns?
	 *
	 * @param file	File to match (by name and flags).
	 * @return	Whether the name matches any pattern, and flags (if any).
	 */
	private boolean matches(final File file) {
	    boolean matched = false;
	    String name = file.getName();
	    for (String spec : wildcardSpecs) {
		// Note: "anyWild" is set if ANY of the specs is wild, without noting which one is
		// BUT it won't hurt to use the wildcard matching for a non-wild spec; it will just
		// take a bit longer.
		if (anyWild) {
		    matched = Match.stringMatch(name, spec, !ignoreCase);
		} else {
		    matched = ignoreCase ? name.equalsIgnoreCase(spec) : name.equals(spec);
		}
		if (matched) {
		    break;
		}
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

