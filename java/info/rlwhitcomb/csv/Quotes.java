/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017,2020-2021 Roger L. Whitcomb.
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
 *	Enumeration for possible quote characters in "CSV" (delimited) files.
 *
 * History:
 *	28-Jun-Mar-2016 (rlwhitcomb)
 *	    Initial coding.
 *	24-May-2017 (rlwhitcomb)
 *	    Implement "quoting disabled" by adding a "None" value here.
 *	    Change the character use for "ALL" to the Unicode non-breaking
 *	    space (U+00A0).
 *	27-Feb-2020 (rlwhitcomb)
 *	    Use a Map for the string lookup; move all the lookups to a static
 *	    class for simplicity.
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	17-Dec-2021 (rlwhitcomb)
 *	    #155: Fix NO_QUOTE_CHAR to be '\0' to be consistent with usage in CSVFormat.
 *	    Simplify constructors.
 *	19-Dec-2021 (rlwhitcomb)
 *	    #154: Set back tick as another valid value.
 */
package info.rlwhitcomb.csv;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * "Standard" choices for quote character(s).
 */
public enum Quotes
{
	NONE		(Constants.NO_QUOTE_CHAR, false),
	SINGLE		('\''),
	DOUBLE		('"'),
	BACK		('`'),
	PAREN		('(', ')'),
	ANGLE		('<', '>'),
	BRACKET		('[', ']'),
	BRACE		('{', '}'),
	ARROW		('\u2039', '\u203A'),
	DOUBLEARROW	('\u00AB', '\u00BB'),
	SMARTSINGLE	('\u2018', '\u2019'),
	SMARTDOUBLE	('\u201C', '\u201D'),
	ALL		(Constants.ALL_QUOTE_CHAR, false);

	public static class Constants
	{
		public static final char NO_QUOTE_CHAR  = '\0';
		public static final char ALL_QUOTE_CHAR = '\u00A0';
	}

	private static class Lookup
	{
		private static final Map<String, Quotes> map = new HashMap<>();
		private static final Set<Character> leftSet = new HashSet<>();
		private static final Set<Character> rightSet = new HashSet<>();
	}

	private char leftChar;
	private char rightChar;
	private boolean allowDoubled;

	/** @return The left quote character for this quote type. */
	public char leftChar() {
	    return this.leftChar;
	}

	/** @return The right quote character for this quote type (could be
	 * the same as the left, but not always). */
	public char rightChar() {
	    return this.rightChar;
	}

	/** @return Are the left and right delimiters the same so that
	 * doubled delimiters count as a single and don't end the field. */
	public boolean allowDoubled() {
	    return this.allowDoubled;
	}

	private Quotes(final char quote) {
	    this(quote, quote, true);
	}

	private Quotes(final char quote, final boolean set) {
	    this(quote, quote, set);
	}

	private Quotes(final char left, final char right) {
	    this(left, right, true);
	}

	private Quotes(final char left, final char right, final boolean set) {
	    this.leftChar = left;
	    this.rightChar = right;
	    this.allowDoubled = (left == right);

	    Lookup.map.put(this.toString().toUpperCase(), this);
	    if (set) {
		Lookup.leftSet.add(this.leftChar);
		Lookup.rightSet.add(this.rightChar);
	    }
	}

	public static boolean isValidStart(final char ch) {
	    return Lookup.leftSet.contains(ch);
	}

	public static boolean isValidEnd(final char ch) {
	    return Lookup.rightSet.contains(ch);
	}


	/**
	 * Choose one of these from the given left (and optional right)
	 * characters.
	 *
	 * @param left	A candidate left (or only) quote character.
	 * @param right	If given (that is not {@code '\0'} then a candidate
	 *		right quote character (could be the same or different
	 *		than the left quote).
	 * @return	If we found a match, then one of ourselves.  If not
	 *		return {@code null}.
	 */
	public static Quotes fromChar(final char left, final char right) {
	    for (Quotes quote : values()) {
		if (quote.leftChar == left && (right == '\0' || quote.rightChar == right)) {
		    return quote;
		}
	    }
	    return null;
	}

	/**
	 * Using the static {@link Lookup#map} populated at construction time,
	 * lookup the matching enum value (case-insensitive) from the given
	 * string.
	 *
	 * @param value	The supposed name of one of our values.
	 * @return	The appropriate enum value if found, or {@code null} if not.
	 */
	public static Quotes fromString(final String value) {
	    return Lookup.map.get(value.toUpperCase());
	}

}
