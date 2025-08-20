/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2010-2011,2015,2017-2018,2020-2022,2025 Roger L. Whitcomb.
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
 *	Dump a .jar file manifest.
 *
 *  Change History:
 *	    ... lost in antiquity ...
 *	29-Mar-2021 (rlwhitcomb)
 *	    Move to new "tools" package; add "Change History".
 *	18-Feb-2022 (rlwhitcomb)
 *	    Use Exceptions for better exception messages.
 *	09-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 *	25-Jul-2022 (rlwhitcomb)
 *	    Change messages to use double quotes around file names
 *	    so they can be copy/pasted and used directly (on Windows).
 *	20-Aug-2025 (rlwhitcomb)
 *	    Cleanup messages when there are no entries; error message for unknown option.
 */
package info.rlwhitcomb.tools;

import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.Exceptions;

import java.io.IOException;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;


/**
 * Dump the .jar file contents, including any manifest that is included.
 * <p> Any number of .jar file names can be given on the command line,
 * but wildcards are not supported (yet).
 * <p> By default the attribute entries are presented sorted by key name,
 * but the "--raw" option will show them as given by {@link Attributes#keySet}.
 
 */
public class DumpManifest
{
	private static final String USAGE = "Usage: java DumpManifest [--raw] <jarfilename> [<jarfilename>]*%n%n";

	private static final boolean onWindows = Environment.isWindows();
	private static boolean unsorted = false;


	private static Comparator<Object> attributeNameComparator = new Comparator<Object>() {
		@Override
		public int compare(Object t1, Object t2) {
		    if (t1 == null) {
			if (t2 == null)
			    return 0;
			return -1;
		    }
		    else {
			if (t2 == null)
			    return +1;
			return t1.toString().compareTo(t2.toString());
		    }
		}
	};

	public static void main(String[] args) {
	    if (args.length == 0) {
		System.err.format(USAGE);
		return;
	    }
	    for (String arg: args) {
		if (arg.equalsIgnoreCase("--raw")
		 || arg.equalsIgnoreCase("-raw")
		 || (onWindows && arg.equalsIgnoreCase("/raw"))) {
		    unsorted = true;
		}
		else if ((arg.startsWith("--") && arg.length() > 2)
		      || (arg.startsWith("-") && arg.length() > 1)
		      || (onWindows && arg.startsWith("/") && arg.length() > 1)) {
		    System.err.format("Unsupported option: \"%1$s\"%n%n", arg);
		    System.err.format(USAGE);
		    return;
		}
		else {
		    try {
			JarFile jar = new JarFile(arg);
			System.out.format("---- Jarfile Entries for \"%1$s\" ----%n", arg);
			for (Enumeration<JarEntry> e = jar.entries(); e.hasMoreElements(); ) {
			    JarEntry entry = e.nextElement();
			    if (entry.isDirectory()) {
				System.out.format(" Dir: %1$s%n", entry.getName());
			    }
			    else {
				System.out.format("File: %1$s  (%2$d -> %3$d bytes)%n", entry.getName(),
				    entry.getSize(), entry.getCompressedSize());
			    }
			}

			Manifest man = jar.getManifest();
			Attributes att = man.getMainAttributes();
			if (att.size() == 0) {
			    System.out.format("%n---- No main attributes for \"%1$s\" ----%n", arg);
			}
			else {
			    if (unsorted) {
				System.out.format("%n---- Main attributes (%1$d) (raw) for \"%2$s\" ----%n", att.size(), arg);
				for (Object o : att.keySet()) {
				    String key = o.toString();
				    System.out.format("%1$s : %2$s%n", key, att.getValue(key));
				}
			    }
			    else {
				System.out.format("%n---- Main attributes (%1$d) (sorted) for \"%2$s\" ----%n", att.size(), arg);
				TreeSet<Object> sortedSet = new TreeSet<>(attributeNameComparator);
				sortedSet.addAll(att.keySet());
				for (Object o : sortedSet) {
				    String key = o.toString();
				    System.out.format("%1$s : %2$s%n", key, att.getValue(key));
				}
			    }
			}

			Map<String,Attributes> attrMap = man.getEntries();
			if (attrMap.size() == 0) {
			    System.out.format("%n---- No manifest entries for \"%1$s\" ----%n", arg);
			}
			else {
			    System.out.format("%n---- Manifest entries (%1$d) for \"%2$s\" ----%n", attrMap.size(), arg);
			    for (String key : attrMap.keySet()) {
				Attributes attrs = attrMap.get(key);
				System.out.format("Attributes for \"%1$s\":%n", key);
				for (Object attrKey : attrs.keySet()) {
				    System.out.format("    %1$s: %2$s%n", attrKey.toString(), attrs.get(attrKey).toString());
				}
			    }
			}
		    }
		    catch (IOException ioe) {
			System.err.format("Unable to open jar file \"%1$s\", error is: %2$s%n", arg, Exceptions.toString(ioe));
			return;
		    }
		}
	    }
	}
}
