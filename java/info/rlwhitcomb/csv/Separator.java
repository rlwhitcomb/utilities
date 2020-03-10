/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Roger L. Whitcomb.
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
 *	Enumeration for possible record separator characters in "CSV" (delimited) files.
 *
 * History:
 *	27-Feb-2020 (rlwhitcomb)
 *	    Initial coding.
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 */
package info.rlwhitcomb.csv;

import java.util.HashMap;
import java.util.Map;

/**
 * "Standard" choices for record separator character(s).
 * <p> These standard choices reflect the popular line ending characters
 * plus a couple more for good measure.
 * <p> Note: several of these are the same character as {@link Delimiter}
 * so be careful using them!
 */
public enum Separator
{
	NL		("\n"),
	NEWLINE		("\n"),
	LF		("\n"),
	CR		("\r"),
	CRLF		("\r\n"),
	SEMICOLON	(";"),
	BAR		("|"),
	STAR		("*"),
	PERCENT		("%"),
	DOLLAR		("$"),
	HASH		("#"),
	TILDE		("~"),
	SLASH		("/"),
	BACKSLASH	("\\");

	private static class Lookup
	{
		private static final Map<String, Separator> map = new HashMap<>();
	}

	private String sepString;

	public String getSeparator() {
	    return this.sepString;
	}

	private Separator(String value) {
	    this.sepString = value;
	    Lookup.map.put(this.toString().toUpperCase(), this);
	}


	/**
	 * Using the static {@link Lookup#map} populated at construction time,
	 * lookup the matching enum value (case-insensitive) from the given
	 * string.
	 *
	 * @param value	The supposed name of one of our values.
	 * @return	The appropriate enum value if found, or {@code null} if not.
	 */
	public static Separator fromString(String value) {
	    return Lookup.map.get(value.toUpperCase());
	}

}
