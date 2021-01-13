/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Roger L. Whitcomb.
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
 *	Generic version information.
 *
 *  History:
 *	13-Jan-2021 (rlwhitcomb)
 *	    First version, based off the Apache POI code.
 */
package info.rlwhitcomb;

import info.rlwhitcomb.util.Environment;


/**
 * Administrative class to keep track of the version number of the code release.
 *
 * <p>This class implements the upcoming standard of having
 *  <code>org.apache.<i>project-name</i>.Version.getVersion()</code> be a standard
 *  way to get version information.
 *
 * <p>Relies on the {@link Environment} class to provide the necessary information.
 */
public class Version
{
	/**
	 * @return The basic version string, of the form
	 *  <code>nn.nn(.nn)</code>.
	 */
	public static String getVersion() {
	    return Environment.getAppVersion();
	}

	/**
	 * @return The date of the release / build.
	 */
	public static String getReleaseDate() {
	    return Environment.getBuildDate();
	}

	/**
	 * @return Name of product.
	 */
	public static String getProduct() {
	    return Environment.getProductName();
	}

	/**
	 * @return Implementation language.
	 */
	public static String getImplementationLanguage() {
	    return "Java";
	}

	/**
	 * Prints the version to the command line.
	 *
	 * @param args The parsed command line arguments (unused).
	 */
	public static void main(String[] args) {
	    Environment.printProgramInfo();
	}
}
