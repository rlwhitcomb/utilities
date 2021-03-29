/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2010-2011,2015,2017-2018,2020-2021 Roger L. Whitcomb.
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
 */
package info.rlwhitcomb.tools;

import java.io.*;
import java.util.*;
import java.util.jar.*;

/**
 * Dump the .jar file contents, including any manifest that is included.
 * <p> Any number of .jar file names can be given on the command line,
 * but wildcards are not supported (yet).
 * <p> By default the attribute entries are presented sorted by key name,
 * but the "--raw" option will show them as given by {@link Attributes#keySet}.
 
 */
public class DumpManifest
{
	private static final boolean onWindows = System.getProperty("os.name").toLowerCase(Locale.ENGLISH).startsWith("windows");
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
		System.err.format("Usage: java DumpManifest [--raw] <jarfilename> [<jarfilename>]*%n");
		return;
	    }
	    for (String arg: args) {
		if (arg.equalsIgnoreCase("--raw")
		 || arg.equalsIgnoreCase("-raw")
		 || (onWindows && arg.equalsIgnoreCase("/raw"))) {
		    unsorted = true;
		}
		else {
		    try {
			JarFile jar = new JarFile(arg);
			System.out.format("---- Jarfile Entries for '%1$s' ----%n", arg);
			for (Enumeration<JarEntry> e = jar.entries(); e.hasMoreElements(); ) {
			    JarEntry entry = e.nextElement();
			    if (entry.isDirectory()) {
				System.out.format(" Dir: %1$s%n", entry.getName());
				
			    }
			    else {
				System.out.format("Name: %1$s  (%2$d -> %3$d bytes)%n", entry.getName(),
				    entry.getSize(), entry.getCompressedSize());
			    }
			}

			Manifest man = jar.getManifest();
			Attributes att = man.getMainAttributes();
			if (unsorted) {
			    System.out.format("%n---- Main attributes (%1$d) (raw) for '%2$s' ----%n", att.size(), arg);
			    for (Object o : att.keySet()) {
				System.out.format("%1$s : %2$s%n", o.toString(), att.getValue(o.toString()));
			    }
			}
			else {
			    System.out.format("%n---- Main attributes (%1$d) (sorted) for '%2$s' ----%n", att.size(), arg);
			    TreeSet<Object> sortedSet = new TreeSet<>(attributeNameComparator);
			    sortedSet.addAll(att.keySet());
			    for (Object o : sortedSet) {
				System.out.format("%1$s : %2$s%n", o.toString(), att.getValue(o.toString()));
			    }
			}

			Map<String,Attributes> attrMap = man.getEntries();
			System.out.format("%n---- Manifest entries (%1$d) for '%2$s' ----%n", attrMap.size(), arg);
			for (String key : attrMap.keySet()) {
			    Attributes attrs = attrMap.get(key);
			    System.out.format("Attributes for \"%1$s\":%n", key);
			    for (Object attrKey : attrs.keySet()) {
				System.out.format("    %1$s: %2$s%n", attrKey.toString(), attrs.get(attrKey).toString());
			    }
			}
		    }
		    catch (IOException ioe) {
			System.err.format("Unable to open jar file '%1$s', error is: %2$s%n", arg, ioe.getMessage());
			return;
		    }
		}
	    }
	}
}
