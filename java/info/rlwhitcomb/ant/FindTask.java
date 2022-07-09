/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2011,2020,2022 Roger L. Whitcomb.
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
 *  Ant task to find a file and set a property with its path.
 *  (Taken from Ant tutorials examples)
 *
 * History:
 *    18-Aug-2011 (rlwhitcomb)
 *	Initial coding from the Tutorial examples.
 *    09-Jan-2020 (rlwhitcomb)
 *	Update package, add license.
 *    09-Jul-2022 (rlwhitcomb)
 *	#393: Cleanup imports.
 */
package info.rlwhitcomb.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

import java.io.File;
import java.util.Vector;


/**
 * Ant task to find a file in a series of path locations.
 * <p> The 'all' option exists to find only the first location or all locations
 * along those paths.
 * <p> Required options are:
 * <ul>
 * <li>{@code file=} name of the file to find
 * <li>{@code location=} property to set with the found location(s)
 * <li>{@code <path>} Nested {@code Path} elements specifying places to look
 * for the given file.
 * </ul>
 * Optional options are:
 * <ul>
 * <li>{@code all="true"|"false"} whether or not to find all locations; default {@code false}
 * </ul>
 * <p> Usage example:
 * <pre>
&lt;findfile file="anttasks.jar" location="all.anttasks.location" all="true"&gt;
	&lt;path&gt;
		&lt;fileset dir="${env.ANT_HOME}/lib" includes="*.jar"/&gt;
		&lt;fileset dir="." includes="*.jar"/&gt;
	&lt;/path&gt;
&lt;/findfile&gt;
&lt;echo message="Every location of 'anttasks.jar' is '${all.anttasks.location}'"/&gt;
</pre>
 */
public class FindTask extends Task
{
	/** The name of the file we're looking for. */
	private String file = null;
	/** The property name we're going to set with the file's location. */
	private String location = null;
	/** Whether to list all the paths or not. */
	private boolean all = false;
	/** The list of paths that we are to search in to find the file. */
	private Vector<Path> paths = new Vector<Path>();
	/** Whether we are running on Windows or not. */
	private boolean isWindows = false;

	public void setFile(String file) {
	    this.file = file;
	}

	public void setLocation(String location) {
	    this.location = location;
	}

	public void addPath(Path path) {
	    paths.add(path);
	}

	public void setAll(boolean all) {
	    this.all = all;
	}

	private void validate() throws BuildException {
	    if (file == null)
		throw new BuildException("'file' is not set");
	    if (location == null)
		throw new BuildException("'location' is not set");
	    if (paths.size() == 0)
		throw new BuildException("'path' not set");
	}

	@Override
	public void init() throws BuildException {
	    this.isWindows = System.getProperty("os.name").startsWith("Windows");
	}

	@Override
	public void execute() throws BuildException {
	    validate();
	    Vector<String> foundFiles = new Vector<String>();
	    for(Path path : paths) {
		for(String includedFile : path.list()) {
		    File f = new File(includedFile);
		    if (isWindows) {
			// Name only has to match case-insensitively
			if (file.equalsIgnoreCase(f.getName()) &&
			    !foundFiles.contains(includedFile)) {
			    foundFiles.add(includedFile);
			}
		    }
		    else {
			// Name must match exactly
			if (file.equals(f.getName()) &&
			    !foundFiles.contains(includedFile)) {
			    foundFiles.add(includedFile);
			}
		    }
		}
	    }
	    String result = null;
	    if (foundFiles.size() > 0) {
		if (all) {
		    StringBuilder buf = new StringBuilder();
		    for (String s : foundFiles) {
			if (buf.length() > 0)
			    buf.append(File.pathSeparatorChar);
			buf.append(s);
		    }
		    result = buf.toString();
		}
		else {
		    result = foundFiles.firstElement();
		}
	    }
	    if (result != null)
		getProject().setNewProperty(location, result);
	}

	public FindTask() {
	}

}
