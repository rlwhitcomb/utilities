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
 *	Deal with command line parsing and wildcard expansion therein.
 *
 * History:
 *  23-Sep-22 rlw #448,#52:	Initial commit from code in Tester.
 *  26-Sep-22 rlw #490:		Fix "~" replacement for shortened paths in Windows.
 */
package info.rlwhitcomb.util;

import info.rlwhitcomb.directory.Match;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Miscellaneous methods for dealing with command line parsing, and especially
 * expanding wildcard specs therein.
 */
public class CommandLine
{
	/**
	 * Private constructor since this is a static class.
	 */
	private CommandLine() {
	}


	/**
	 * Substitute the user's home directory for occurrence of {@code "~"} at the
	 * beginning of the path.
	 *
	 * @param path	A file path.
	 * @return	That path with the starting {@code "~"} character replaced.
	 */
	private static String sub(final String path) {
	    return path.replaceAll("^~", Environment.userHomeDirString());
	}

	/**
	 * A {@link LineProcessor} that simply collects the file names during
	 * {@link LineProcessor#preProcess}, and then supplies the sorted list
	 * for use by the caller.
	 */
	private static class ListFilesProcessor implements LineProcessor
	{
		private List<String> list;

		public ListFilesProcessor() {
		    list = new ArrayList<>();
		}

		@Override
		public boolean preProcess(final File inputFile) {
		    list.add(sub(inputFile.getPath()));
		    return true;
		}

		public List<String> fileList() {
		    Collections.sort(list);
		    return list;
		}
	}


	/**
	 * Parse the command line and expand any wildcard specs on it.
	 *
	 * @param line		The command line to parse.
	 * @param defaultDir	The default directory to use for wildcard file search (can be {@code null}).
	 * @return		Parsed into strings, with any wildcard specifications expanded to
	 *			all the matching file names.
	 */
	public static String[] parse(final String line, final File defaultDir) {
	    String[] args = CharUtil.parseCommandLine(line);
	    List<String> result = new ArrayList<>();

	    for (String arg : args) {
		if (Match.hasWildCards(arg)) {
		    File f = new File(arg);
		    File dir = f.getParentFile();
		    if (dir == null) {
			if (defaultDir != null)
			    dir = defaultDir;
			else
			    dir = Environment.userDirectory();
		    }
		    ListFilesProcessor lp = new ListFilesProcessor();
		    new DirectoryProcessor(dir, lp)
			.setNameOnlyMode(true)
			.setWildcardFilter(f.getName())
			.processDirectory();
		    result.addAll(lp.fileList());
		}
		else {
		    result.add(sub(arg));
		}
	    }

	    return result.toArray(new String[0]);
	}

}
