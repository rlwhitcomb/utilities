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
 *	Version number object, specific to Tester.
 *
 * History:
 *  26-Oct-22 rlw #540:	Moved out of Tester to separate class.
 */
package info.rlwhitcomb.tester;


/**
 * A simplified version object for use with Tester.
 */
class Version implements Comparable<Version>
{
	/** Major version (or -1 if empty) */
	int major;
	/** Minor version (or -1 if empty or equal "x" or "*") */
	int minor;

	public Version() {
	    this(-1, -1);
	}

	public Version(final int maj, final int min) {
	    major = maj;
	    minor = min;
	}

	public Version(final String input) {
	    String vers[] = input.split("\\.");

	    if (vers[0].isEmpty())
		major = -1;
	    else
		major = Integer.parseInt(vers[0]);

	    if (vers.length > 1) {
		if (vers[1].equalsIgnoreCase("x") ||
		    vers[1].equals("*"))
		    minor = -1;
		else
		    minor = Integer.parseInt(vers[1]);
	    }
	    else {
		minor = -1;
	    }
	}

	/**
	 * @param other The other version to compare to.
	 * @return -1 if version is &lt; other
	 *         0 if versions are equal
	 *         +1 if version is &gt; other
	 *         will return 0 if major is -1 or major is equal and minor is -1
	 */
	@Override
	public int compareTo(final Version other) {
	    if (major == -1)
		return 0;

	    if (major != other.major) {
		return Integer.signum(major - other.major);
	    }
	    if (minor == -1)
		return 0;

	    return Integer.signum(minor - other.minor);
	}

}
