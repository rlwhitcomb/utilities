/**
 * Copyright 2019 Patrick Ahlbrecht
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package de.onyxbits;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Implementation of semantic versioning (see: <a
 * href="http://semver.org">semver.org</a>).
 *
 * @author patrick
 *
 */
public final class SemanticVersion implements Comparable<SemanticVersion> {

	/**
	 * Major version number
	 */
	public final int major;

	/**
	 * Minor version number
	 */
	public final int minor;

	/**
	 * Patch level
	 */
	public final int patch;

	/**
	 * Pre-release tags (potentially empty, but never null). This is private to
	 * ensure read only access.
	 */
	private final String[] preRelease;

	/**
	 * Build meta data tags (potentially empty, but never null). This is private
	 * to ensure read only access.
	 */
	private final String[] buildMeta;

	/**
	 * Construct a new default version <code>0.0.0</code> object.
	 */
	public SemanticVersion() {
		this(0, 0, 0);
	}

	/**
	 * Construct a new plain version object
	 *
	 * @param major
	 *          major version number. Must not be negative
	 * @param minor
	 *          minor version number. Must not be negative
	 * @param patch
	 *          patchlevel. Must not be negative.
	 */
	public SemanticVersion(final int major, final int minor, final int patch) {
		this(major, minor, patch, new String[0], new String[0]);
	}

	/**
	 * Construct a fully featured version object with all bells and whistles.
	 *
	 * @param major
	 *          major version number (must not be negative)
	 * @param minor
	 *          minor version number (must not be negative)
	 * @param patch
	 *          patch level (must not be negative).
	 * @param preRelease
	 *          pre release identifiers. Must not be null, all parts must match
	 *          "[0-9A-Za-z-]+".
	 * @param buildMeta
	 *          build meta identifiers. Must not be null, all parts must match
	 *          "[0-9A-Za-z-]+".
	 */
	public SemanticVersion(final int major, final int minor, final int patch,
			final String[] preRelease, final String[] buildMeta) {
		if (major < 0 || minor < 0 || patch < 0) {
			throw new IllegalArgumentException("Version numbers must be positive!");
		}
		this.buildMeta = new String[buildMeta.length];
		this.preRelease = new String[preRelease.length];
		Pattern p = Pattern.compile("[0-9A-Za-z-]+");
		for (int i = 0; i < preRelease.length; i++) {
			if (preRelease[i] == null || !p.matcher(preRelease[i]).matches()) {
				throw new IllegalArgumentException("Pre Release tag: " + i);
			}
			this.preRelease[i] = preRelease[i];
		}
		for (int i = 0; i < buildMeta.length; i++) {
			if (buildMeta[i] == null || !p.matcher(buildMeta[i]).matches()) {
				throw new IllegalArgumentException("Build Meta tag: " + i);
			}
			this.buildMeta[i] = buildMeta[i];
		}

		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}

	/**
	 * Convenience constructor for creating a Version object from the
	 * "Implementation-Version:" property of the Manifest file.
	 *
	 * @param clazz
	 *          a class in the JAR file (or that otherwise has its
	 *          implementationVersion attribute set).
	 * @throws ParseException
	 *           if the versionstring does not conform to the semver specs.
	 */
	public SemanticVersion(final Class<?> clazz) throws ParseException {
		this(clazz.getPackage().getImplementationVersion());
	}

	/**
	 * Construct a version object by parsing a string.
	 *
	 * @param version
	 *          version in flat string format
	 * @throws ParseException
	 *           if the version string does not conform to the semver specs.
	 */
	public SemanticVersion(final String version) throws ParseException {
		vParts = new int[5];
		preParts = new ArrayList<String>(5);
		metaParts = new ArrayList<String>(5);
		input = version.toCharArray();

		if (!stateMajor()) { // Start recursive descend
			throw new ParseException(version, errPos);
		}

		major = vParts[0];
		minor = vParts[1];
		patch = vParts[2];

		preRelease = preParts.toArray(new String[preParts.size()]);
		buildMeta = metaParts.toArray(new String[metaParts.size()]);
	}

	/**
	 * Check if this version has a given build Meta tags.
	 *
	 * @param tag
	 *          the tag to check for.
	 * @return true if the tag is found in {@link SemanticVersion#buildMeta}.
	 */
	public boolean hasBuildMeta(final String tag) {
		for (String s : buildMeta) {
			if (s.equals(tag)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if this version has a given pre release tag.
	 *
	 * @param tag
	 *          the tag to check for
	 * @return true if the tag is found in {@link SemanticVersion#preRelease}.
	 */
	public boolean hasPreRelease(final String tag) {
		for (String s : preRelease) {
			if (s.equals(tag)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the pre release tags
	 *
	 * @return a potentially empty array, but never null.
	 */
	public String[] getPreRelease() {
		String[] ret = new String[preRelease.length];
		System.arraycopy(preRelease, 0, ret, 0, ret.length);
		return ret;
	}

	/**
	 * Get the build meta tags
	 *
	 * @return a potentially empty array, but never null.
	 */
	public String[] getBuildMeta() {
		String ret[] = new String[buildMeta.length];
		System.arraycopy(buildMeta, 0, ret, 0, ret.length);
		return ret;
	}

	/**
	 * Convenience method to check if this version is an update.
	 *
	 * @param v
	 *          the other version object
	 * @return true if this version is newer than the other one.
	 */
	public boolean isUpdateFor(final SemanticVersion v) {
		return compareTo(v) > 0;
	}

	/**
	 * Convenience method to check if this version is a compatible update.
	 *
	 * @param v
	 *          the other version object.
	 * @return true if this version is newer and both have the same major version.
	 */
	public boolean isCompatibleUpdateFor(final SemanticVersion v) {
		return isUpdateFor(v) && (major == v.major) && (major != 0 || v.major != 0);
	}

	/**
	 * Convenience method to check if this is a stable version.
	 *
	 * @return true if the major version number is greater than zero and there are
	 *         no pre release tags.
	 */
	public boolean isStable() {
		return major > 0 && preRelease.length == 0;
	}

	/**
	 * Get the prerelease information (if any) as a string.
	 *
	 * @return The prerelease tags, combined with '.' and prefixed
	 *         by '-', if there are any, or an empty string if none.
	 */
	public String getPreReleaseString() {
		StringBuilder ret = new StringBuilder();
		if (preRelease.length > 0) {
			ret.append('-');
			for (int i = 0; i < preRelease.length; i++) {
				ret.append(preRelease[i]);
				if (i < preRelease.length - 1) {
					ret.append('.');
				}
			}
		}
		return ret.toString();
	}

	/**
	 * Get the build metadata information (if any) as a string.
	 *
	 * @return The build metadata tags, combined with '.' and prefixed
	 *         by '+' or '_', if there are any, or an empty string if none.
	 */
	public String getBuildMetaString() {
		StringBuilder ret = new StringBuilder();
		if (buildMeta.length > 0) {
			ret.append(metaPrefix);
			for (int i = 0; i < buildMeta.length; i++) {
				ret.append(buildMeta[i]);
				if (i < buildMeta.length - 1) {
					ret.append('.');
				}
			}
		}
		return ret.toString();
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append(major);
		ret.append('.');
		ret.append(minor);
		ret.append('.');
		ret.append(patch);
		// If we parsed from a string and there were extra version parts, add them now
		for (int i = 3; i < nextVPart; i++) {
			ret.append('.');
			ret.append(vParts[i]);
		}
		ret.append(getPreReleaseString());
		ret.append(getBuildMetaString());
		return ret.toString();
	}

	public String toPreReleaseString() {
		StringBuilder ret = new StringBuilder();
		ret.append(major);
		ret.append('.');
		ret.append(minor);
		ret.append('.');
		ret.append(patch);
		// If we parsed from a string and there were extra version parts, add them now
		for (int i = 3; i < nextVPart; i++) {
			ret.append('.');
			ret.append(vParts[i]);
		}
		ret.append(getPreReleaseString());
		return ret.toString();
	}

	public String toSimpleString() {
		StringBuilder ret = new StringBuilder();
		ret.append(major);
		ret.append('.');
		ret.append(minor);
		ret.append('.');
		ret.append(patch);
		return ret.toString();
	}

	@Override
	public int hashCode() {
		return toString().hashCode(); // Lazy
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SemanticVersion)) {
			return false;
		}
		SemanticVersion ov = (SemanticVersion) other;
		if (ov.major != major || ov.minor != minor || ov.patch != patch) {
			return false;
		}
		if (ov.preRelease.length != preRelease.length) {
			return false;
		}
		for (int i = 0; i < preRelease.length; i++) {
			if (!preRelease[i].equals(ov.preRelease[i])) {
				return false;
			}
		}
		if (ov.buildMeta.length != buildMeta.length) {
			return false;
		}
		for (int i = 0; i < buildMeta.length; i++) {
			if (!buildMeta[i].equals(ov.buildMeta[i])) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int compareTo(final SemanticVersion v) {
		int result = major - v.major;
		if (result == 0) { // Same major
			result = minor - v.minor;
			if (result == 0) { // Same minor
				result = patch - v.patch;
				if (result == 0) { // Same patch
					if (preRelease.length == 0 && v.preRelease.length > 0) {
						result = 1; // No pre release wins over pre release
					}
					if (v.preRelease.length == 0 && preRelease.length > 0) {
						result = -1; // No pre release wins over pre release
					}
					if (preRelease.length > 0 && v.preRelease.length > 0) {
						int len = Math.min(preRelease.length, v.preRelease.length);
						int count = 0;
						for (count = 0; count < len; count++) {
							result = comparePreReleaseTag(count, v);
							if (result != 0) {
								break;
							}
						}
						if (result == 0 && count == len) { // Longer version wins.
							result = preRelease.length - v.preRelease.length;
						}
					}
				}
			}
		}
		return result;
	}

	private int comparePreReleaseTag(final int pos, final SemanticVersion ov) {
		Integer here = null;
		Integer there = null;
		try {
			here = Integer.parseInt(preRelease[pos], 10);
		}
		catch (NumberFormatException e) {
		}
		try {
			there = Integer.parseInt(ov.preRelease[pos], 10);
		}
		catch (NumberFormatException e) {
		}
		if (here != null && there == null) {
			return -1; // Strings take precedence over numbers
		}
		if (here == null && there != null) {
			return 1; // Strings take precedence over numbers
		}
		if (here == null && there == null) {
			return (preRelease[pos].compareTo(ov.preRelease[pos])); // ASCII compare
		}
		return here.compareTo(there); // Number compare
	}

	// Parser implementation below

	private int[] vParts;
	private int nextVPart;
	private char metaPrefix;
	private ArrayList<String> preParts, metaParts;
	private int errPos;
	private char[] input;

	private boolean stateMajor() {
		int pos = 0;
		while (pos < input.length && input[pos] >= '0' && input[pos] <= '9') {
			pos++; // match [0..9]+
		}
		if (pos == 0) { // Empty String -> Error
			return false;
		}
		if (input[0] == '0' && pos > 1) { // Leading zero
			return false;
		}

		vParts[nextVPart++] = Integer.parseInt(new String(input, 0, pos), 10);

		if (pos == input.length) { // We have a clean version string
			return true;
		}

		if (input[pos] == '.') {
			return stateMinor(pos + 1);
		}
		if (input[pos] == '+' || input[pos] == '_') { // We have build meta tags -> descend
			return stateMeta(pos + 1);
		}
		if (input[pos] == '-') { // We have pre release tags -> descend
			return stateRelease(pos + 1);
		}

		errPos = pos; // We have junk
		return false;
	}

	private boolean stateMinor(final int index) {
		int pos = index;
		while (pos < input.length && input[pos] >= '0' && input[pos] <= '9') {
			pos++;// match [0..9]+
		}
		if (pos == index) { // Empty String -> Error
			errPos = index;
			return false;
		}
		if (input[0] == '0' && pos - index > 1) { // Leading zero
			errPos = index;
			return false;
		}

		vParts[nextVPart++] = Integer.parseInt(new String(input, index, pos - index), 10);

		if (pos == input.length) { // We have a clean version string
			return true;
		}

		if (input[pos] == '.') {
			return statePatch(pos + 1);
		}
		if (input[pos] == '+' || input[pos] == '_') { // We have build meta tags -> descend
			return stateMeta(pos + 1);
		}
		if (input[pos] == '-') { // We have pre release tags -> descend
			return stateRelease(pos + 1);
		}

		errPos = pos;
		return false;
	}

	private boolean statePatch(final int index) {
		int pos = index;
		while (pos < input.length && input[pos] >= '0' && input[pos] <= '9') {
			pos++; // match [0..9]+
		}
		if (pos == index) { // Empty String -> Error
			errPos = index;
			return false;
		}
		if (input[0] == '0' && pos - index > 1) { // Leading zero
			errPos = index;
			return false;
		}

		vParts[nextVPart++] = Integer.parseInt(new String(input, index, pos - index), 10);

		if (pos == input.length) { // We have a clean version string
			return true;
		}

		if (input[pos] == '.') { // Unusual but we have a fractional patch level
			return statePatch(pos + 1);
		}
		if (input[pos] == '+' || input[pos] == '_') { // We have build meta tags -> descend
			return stateMeta(pos + 1);
		}
		if (input[pos] == '-') { // We have pre release tags -> descend
			return stateRelease(pos + 1);
		}

		errPos = pos; // We have junk
		return false;
	}

	private boolean stateRelease(final int index) {
		int pos = index;
		while ((pos < input.length)
				&& ((input[pos] >= '0' && input[pos] <= '9')
						|| (input[pos] >= 'a' && input[pos] <= 'z')
						|| (input[pos] >= 'A' && input[pos] <= 'Z') || input[pos] == '-')) {
			pos++; // match [0..9a-zA-Z-]+
		}
		if (pos == index) { // Empty String -> Error
			errPos = index;
			return false;
		}

		preParts.add(new String(input, index, pos - index));
		if (pos == input.length) { // End of input
			return true;
		}
		if (input[pos] == '.') { // More parts -> descend
			return stateRelease(pos + 1);
		}
		if (input[pos] == '+' || input[pos] == '_') { // We have build meta tags -> descend
			return stateMeta(pos + 1);
		}

		errPos = pos;
		return false;
	}

	private boolean stateMeta(final int index) {
		int pos = index;
		if (input[pos - 1] != '.') {
			metaPrefix = input[pos - 1];
		}

		while ((pos < input.length)
				&& ((input[pos] >= '0' && input[pos] <= '9')
						|| (input[pos] >= 'a' && input[pos] <= 'z')
						|| (input[pos] >= 'A' && input[pos] <= 'Z') || input[pos] == '-')) {
			pos++; // match [0..9a-zA-Z-]+
		}
		if (pos == index) { // Empty String -> Error
			errPos = index;
			return false;
		}

		metaParts.add(new String(input, index, pos - index));
		if (pos == input.length) { // End of input
			return true;
		}
		if (input[pos] == '.') { // More parts -> descend
			return stateMeta(pos + 1);
		}

		errPos = pos;
		return false;
	}
}

