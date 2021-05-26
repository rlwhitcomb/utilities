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
 *	21-Jan-2021 (rlwhitcomb)
 *	    Display all the defined fields in the "main" display.
 *	    Add "getBuild()".
 *	11-Feb-2021 (rlwhitcomb)
 *	    Simplify the "main" display.
 *	24-Feb-2021 (rlwhitcomb)
 *	    Update the Javadoc.
 *	26-May-2021 (rlwhitcomb)
 *	    Update outputs using colors because of new ConsoleColor
 *	    paradigm.
 */
package info.rlwhitcomb;

import java.util.List;

import info.rlwhitcomb.util.CharUtil;
import static info.rlwhitcomb.util.CharUtil.Justification.*;
import static info.rlwhitcomb.util.ConsoleColor.Code.*;
import info.rlwhitcomb.util.ConsoleColor;
import info.rlwhitcomb.util.Environment;
import static info.rlwhitcomb.util.Environment.ProgramInfo;

/**
 * Administrative class to keep track of the version number of the code release.
 *
 * <p>This class implements the upcoming standard of having
 *  <code>org.apache.<i>project-name</i>.Version.getVersion()</code> be a standard
 *  way to get version information in the <a href="https://apache.org">ASF</a> world.
 *
 * <p>This implementation for this "utilities" project relies on the {@link Environment}
 * class to provide the necessary information.
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
	 * @return The build "number".
	 */
	public static String getBuild() {
	    return Environment.getAppBuild();
	}

	/**
	 * @return The date of the release / build.
	 */
	public static String getReleaseDate() {
	    return Environment.getBuildDate();
	}

	/**
	 * @return Name of the product.
	 */
	public static String getProduct() {
	    return Environment.getProductName();
	}

	/**
	 * @return Implementation language of this project.
	 */
	public static String getImplementationLanguage() {
	    return "Java";
	}

	private static void output() {
	    System.out.println();
	}

	private static void output(String message) {
	    System.out.println(ConsoleColor.color(message));
	}

	private static void printInfo(String function, String value) {
	    String padFunc = CharUtil.padToWidth(function, 28, CharUtil.Justification.RIGHT);
	    output(BLUE_BOLD_BRIGHT + padFunc + "()" + BLACK_BRIGHT + " -> " + GREEN + value + RESET);
	}

	/**
	 * Prints the version to the command line, along with all the available subproject
	 * version information (contained in the <code>version.properties</code> file).
	 *
	 * @param args The parsed command line arguments (unused).
	 */
	public static void main(String[] args) {
	    Environment.printProgramInfo(50);

	    String underline = CharUtil.padToWidth("", 50, '-');

	    printInfo("getVersion",                getVersion());
	    printInfo("getBuild",                  getBuild());
	    printInfo("getReleaseDate",            getReleaseDate());
	    printInfo("getProduct",                getProduct());
	    printInfo("getImplementationLanguage", getImplementationLanguage());
	    output();

	    output(BLACK_BRIGHT + underline + RESET);

	    List<ProgramInfo> infos = Environment.getAllProgramInfo();

	    for (ProgramInfo info : infos) {
		String version = String.format("Version %1$s", info.version);

		output(BLUE_BOLD_BRIGHT + CharUtil.padToWidth(info.title, 50, CENTER));
		output(GREEN            + CharUtil.padToWidth(version, 50, CENTER));
		output(BLACK_BRIGHT     + underline + RESET);
	    }
	    output();
	}
}
