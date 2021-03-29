/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2021 Roger L. Whitcomb.
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
 *	Utility program to clean up the system path.
 *
 *  Change History:
 *	18-Nov-2020 (rlwhitcomb)
 *	    First version.
 *	19-Jan-2021 (rlwhitcomb)
 *	    Clean up code and comments.
 *	29-Mar-2021 (rlwhitcomb)
 *	    Move to new package.
 */
package info.rlwhitcomb.tools;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


/**
 * Utility to clean up the system path, eliminating empty elements, duplicated
 * paths, relative paths, quoted values, etc.
 * <p> Reads the path from the system environment, and outputs the cleaned path
 * to {@link System#out} (so an external script needs to set the system path again).
 */
public class FixPath
{
	/**
	 * Fix the system path by doing the following:
	 * <ul><li>remove duplicate entries</li>
	 * <li>remove quotes around individual elements</li>
	 * <li>make all paths absolute (remove . and .. refs)</li>
	 * <li>fix any {@code /} to {@code \} (or vice-versa, depending on the O/S)</li>
	 * <li>remove trailing {@code ;}</li>
	 * </ul>
	 * <p> Read the path from the system environment and emit the fixed path
	 * to {@link System#out}.
	 *
	 * @param args	The command line arguments (ignored).
	 */
	public static void main(String[] args) {
	    final String pathSep = System.getProperty("path.separator");
	    final String fileSep = System.getProperty("file.separator");

	    final String pathSepRegex = "[" + pathSep + "]";

	    /* Used to remove duplicate elements. */
	    final Set<String> pathElementSet = new HashSet<>();

	    String originalPath   = System.getenv("PATH");
	    String[] pathElements = originalPath.split(pathSepRegex);

	    StringBuilder buf = new StringBuilder(originalPath.length());

	    for (String pathElement : pathElements) {
		if (!pathElement.isEmpty()) {
		    String modifiedElement = pathElement;

		    /* Remove quotes around the element. */
		    if (pathElement.charAt(0) == '"' && pathElement.charAt(pathElement.length() - 1) == '"')
			modifiedElement = pathElement.substring(1, pathElement.length() - 1);

		    File pathDir = new File(modifiedElement);
		    if (pathDir.exists() && pathDir.isDirectory()) {
			try {
			    modifiedElement = pathDir.getCanonicalPath();

			    if (!pathElementSet.contains(modifiedElement)) {
				pathElementSet.add(modifiedElement);
				if (buf.length() > 0)
				    buf.append(pathSep);
				buf.append(modifiedElement);
			    }
			}
			catch (IOException ioe) {
			    System.err.println("Error getting canonical path of \"" + pathDir.getPath() + "\": " + ioe.getMessage());
			}
		    }
		}
	    }

	    System.out.println(buf.toString());
	}
}
