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
 *	Enumeration of the various dictionary files available.
 *
 *  Change History:
 *	19-Jul-2022 (rlwhitcomb)
 *	    #411: Abstracted out of WordFind.java for use in other applications.
 */
package info.rlwhitcomb.wordfind;


/**
 * Enumeration of the various dictionary files available to us.
 */
public enum WordFile
{
	/**
	 * The default dictionary -- best and most recently updated.
	 * Sourced from: <a href="https://scrabutility.com/TWL06.txt">TWL06.txt</a>
	 * with some additions and optional words as found using
	 * Words With Friends.
	 */
	DEFAULT		("TWL06a.txt"),
	/**
	 * The original master word file without any changes or additions.
	 */
	ORIGINAL	("TWL06.txt"),
	/**
	 * The original, original word file that all the others were derived from;
	 * all in UPPER case to be consistent with the others.
	 */
	ANTIQUE		("ENABLE1U.txt");

	/** Name of the file associated with this entry. */
	private String fileName;

	/**
	 * Construct an entry given the file name.
	 *
	 * @param f	The file name to associate with the entry.
	 */
	private WordFile(final String f) {
	    fileName = f;
	}

	/**
	 * @return The word file name associated with this entry.
	 */
	public String getFileName() {
	    return fileName;
	}

}

