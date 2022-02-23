/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2022 Roger L. Whitcomb.
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
 *	27-Jul-2021 (rlwhitcomb)
 *	    Check for correctness of version strings using Semantic Version parser.
 *	08-Aug-2021 (rlwhitcomb)
 *	    Use box-drawing characters for the separator lines; implement options
 *	    for colors or not.
 *	22-Feb-2022 (rlwhitcomb)
 *	    #254: Options to display LICENSE and NOTICE files.
 *	23-Feb-2022 (rlwhitcomb)
 *	    #254: Help option.
 */
package info.rlwhitcomb;

import java.text.ParseException;
import java.util.List;

import de.onyxbits.SemanticVersion;

import info.rlwhitcomb.util.CharUtil;
import static info.rlwhitcomb.util.CharUtil.Justification.*;
import info.rlwhitcomb.util.ClassUtil;
import static info.rlwhitcomb.util.ConsoleColor.Code.*;
import info.rlwhitcomb.util.ConsoleColor;
import info.rlwhitcomb.util.Environment;
import static info.rlwhitcomb.util.Environment.ProgramInfo;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.Options;


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
	/** Whether or not to display info in color (default <code>true</code>). */
	private static boolean colors = true;

	/** The display width for the information (constant). */
	private static final int WIDTH = 50;

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
	    System.out.println(ConsoleColor.color(message, colors));
	}

	private static void printInfo(String function, String value) {
	    String padFunc = CharUtil.padToWidth(function, 28, CharUtil.Justification.RIGHT);
	    output(BLUE_BOLD_BRIGHT + padFunc + "()" + BLACK_BRIGHT + " -> " + GREEN + value + RESET);
	}

	private static void displayTextFile(String name) {
	    String contents = ClassUtil.getResourceAsString("/META-INF/" + name);
	    String[] lines = CharUtil.stringToLines(contents);
	    int width = CharUtil.maxLength(lines);

	    String header = CharUtil.makeStringOfChars(colors ? '\u2550' : '=', width);
	    String separator = BLACK_BRIGHT + header + RESET;

	    output(separator);
	    output();
	    output(BLUE_BOLD_BRIGHT + CharUtil.padToWidth(name, width, CENTER));
	    output();
	    output(separator);

	    for (String line : lines) {
		output(line);
	    }

	    String underline = CharUtil.makeStringOfChars(colors ? '\u2500' : '-', width);
	    separator = BLACK_BRIGHT + underline + RESET;

	    output(separator);
	    output();
	}

	/**
	 * Prints the version to the command line, along with all the available subproject
	 * version information (contained in the <code>version.properties</code> file).
	 *
	 * @param args The parsed command line arguments (unused).
	 */
	public static void main(String[] args) {
	    boolean displayLicense = false;
	    boolean displayNotice = false;

	    // Parse the command line options
	    for (String arg : args) {
		String option = Options.isOption(arg);

		if (option != null) {
		    switch (option) {
			case "nocolors":
			case "nocolor":
			case "nocols":
			case "nocol":
			case "noc":
			case "nc":
			    colors = false;
			    break;
			case "colors":
			case "color":
			case "cols":
			case "col":
			case "c":
			    colors = true;
			    break;
			case "license":
			case "lic":
			case "l":
			    displayLicense = true;
			    break;
			case "notice":
			case "note":
			case "n":
			    displayNotice = true;
			    break;
			case "help":
			case "h":
			case "?":
			    Environment.printProgramInfo(WIDTH, colors);
			    Intl.printHelp("#version");
			    return;
			default:
			    // just ignore (silently)
			    break;
		    }
		}
	    }

	    Environment.printProgramInfo(WIDTH, colors);

	    String underline = CharUtil.makeStringOfChars(colors ? '\u2500' : '-', WIDTH);
	    String separator = BLACK_BRIGHT + underline + RESET;

	    printInfo("getVersion",                getVersion());
	    printInfo("getBuild",                  getBuild());
	    printInfo("getReleaseDate",            getReleaseDate());
	    printInfo("getProduct",                getProduct());
	    printInfo("getImplementationLanguage", getImplementationLanguage());
	    output();

	    output(separator);

	    List<ProgramInfo> infos = Environment.getAllProgramInfo();

	    for (ProgramInfo info : infos) {
		String rawVersion = info.version;
		// Get the version string in a "canonical" form (according to the Semantic Version spec).
		try {
		    SemanticVersion semVer = new SemanticVersion(info.version);
		    rawVersion = semVer.toString();
		}
		catch (ParseException pe) {
		    rawVersion += " (raw)";	// this is not in the resource file b/c it is an error
						// condition that should never happen anyway.
		}
		String version = String.format("Version %1$s", rawVersion);

		output(BLUE_BOLD_BRIGHT + CharUtil.padToWidth(info.title, WIDTH, CENTER));
		output(GREEN            + CharUtil.padToWidth(version, WIDTH, CENTER));
		output(separator);
	    }
	    output();

	    if (displayLicense) {
		displayTextFile("LICENSE");
	    }

	    if (displayNotice) {
		displayTextFile("NOTICE");
	    }

	}
}
