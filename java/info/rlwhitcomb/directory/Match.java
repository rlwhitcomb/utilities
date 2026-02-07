/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020,2022,2026 Roger L. Whitcomb.
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
 *	String / character matching methods for use with the "D" program
 *	(or other uses).
 *
 * Change History:
 *    02-Oct-2020 (rlwhitcomb)
 *	Finished conversion from C.
 *    03-Feb-2022 (rlwhitcomb)
 *	Add "hasWildCards" method for use with Calc.
 *    06-Feb-2026 (rlwhitcomb)
 *	Small updates to Javadoc.
 */
package info.rlwhitcomb.directory;


/**
 * String and character matching methods, because we need wildcard and
 * case insensitive support, which are not succinctly supported in the
 * standard Java packages.
 * <p> Note: these methods deal in code point values, so they should
 * work correctly for surrogate pairs (in other words, for ALL Unicode
 * characters).
 */
public class Match
{
	/**
	 * All static class has private constructor.
	 */
	private Match() {
	}

	/**
	 * Check if a pattern has any wild card characters in it (because if not, then
	 * regular matching works fine, without having to use these methods).
	 *
	 * @param pattern The pattern to check.
	 * @return        Whether the pattern has any <code>'?'</code> or <code>'*'</code> in it.
	 */
	public static boolean hasWildCards(final String pattern) {
	    return (pattern.indexOf('?') >= 0 || pattern.indexOf('*') >= 0);
	}

	/**
	 * Decide whether or not the given pattern is a string
	 * of all <code>'*'</code> characters, meaning it should match any input.
	 * <p> Note: this differs from the C code which would also
	 * check for <code>'?'</code>. BUT a string of <code>'?'</code> should only match
	 * input of the same length, but this code is only (currently)
	 * used to decide if an empty input string matches the pattern
	 * so <code>'?'</code> doesn't qualify in that scenario.
	 *
	 * @param pattern	The pattern to test.
	 * @return		Whether or not the pattern is entirely
	 *			a string of <code>'*'</code>.
	 */
	private static boolean allWild(final String pattern) {
	    if (pattern == null || pattern.isEmpty())
		return false;

	    int iChar;
	    int iCount;
	    int patLen = pattern.length();

	    for (int i = 0; i < patLen; i += iCount) {
		iChar  = pattern.codePointAt(i);
		iCount = Character.charCount(iChar);

		if (iChar != '*')
		    return false;
	    }

	    return true;
	}

	/**
	 * Match an input character against the corresponding pattern character,
	 * in a case-sensitive manner or not. The single character wildcard (<code>'?'</code>)
	 * is checked here.
	 * <p> Note: all the caveats for {@link Character#toUpperCase(int)} or
	 * {@link Character#toLowerCase(int)} apply here: namely that context and locale
	 * considerations, or 1:M matching are not considered.
	 *
	 * @param in		The input code point.
	 * @param pat		The pattern code point.
	 * @param caseSensitive	Whether to do an exact match or without regard to case.
	 * @return		Whether we have a match.
	 */
	public static boolean charMatch(final int in, final int pat, final boolean caseSensitive) {
	    // Wildcard or exact match
	    if (pat == '?' || in == pat) {
		return true;
	    }

	    if (!caseSensitive) {
		// Some locales have upper or lower case equivalents but not both,
		// so it behooves us to check in both directions just in case.
		if (Character.toUpperCase(in) == Character.toUpperCase(pat) ||
		    Character.toLowerCase(in) == Character.toLowerCase(pat))
		    return true;
	    }

	    return false;
	}

	/**
	 * Do a wildcard string match of the input against the pattern, possibly in a
	 * case-insensitive manner.
	 * <p> Wildcard patterns are: <code>'*'</code> matches any run of characters in the input,
	 * while <code>'?'</code> matches any single character in the input.
	 *
	 * @param input		The input string to match.
	 * @param pattern	The pattern to match against.
	 * @param caseSensitive	If true, the characters must match exactly, false means
	 *			characters that differ only in case are considered the same.
	 * @return		Whether the given input matches the pattern.
	 */
	public static boolean stringMatch(final String input, final String pattern, boolean caseSensitive) {
	    // An empty pattern only matches empty input
	    if (pattern == null || pattern.isEmpty()) {
		return (input == null || input.isEmpty());
	    }

	    boolean allWild = allWild(pattern);

	    // Null input string only matches wildcard pattern
	    if (input == null || input.isEmpty() || allWild)
		return allWild;

	    int pPos, iPos = 0;
	    int pCount, iCount;
	    int patLen = pattern.length();
	    int inpLen = input.length();

	    for (pPos = 0; pPos < patLen && iPos < inpLen; pPos += pCount) {
		int iChar = input.codePointAt(iPos);
		int pChar = pattern.codePointAt(pPos);

		// Because we are dealing with code points, which can be multiple chars
		// we need to do a calculation for stepping through the string
		iCount = Character.charCount(iChar);
		pCount = Character.charCount(pChar);

		// Exact or wild-card single character match
		if (charMatch(iChar, pChar, caseSensitive)) {
		    iPos += iCount;
		}

		// Wild-card run match
		else if (pChar == '*') {
		    // Skip past all the run of '*' in the pattern
		    do {
			pPos += pCount;
			if (pPos >= patLen)
			    break;
			pChar = pattern.codePointAt(pPos);
			pCount = Character.charCount(pChar);
		    } while (pChar == '*');

		    // If no more pattern after the "*" then we match regardless of input
		    if (pPos >= patLen)
			return true;

		    // Look for the following pattern character in input:
		    // if not found, not a match, but if found, continue matching from there
		    for (int nPos = iPos; nPos < inpLen; nPos += iCount) {
			iChar  = input.codePointAt(nPos);
			iCount = Character.charCount(iChar);

			if (charMatch(iChar, pChar, caseSensitive)) {
			    if (stringMatch(
					input.substring(nPos + iCount),
					pattern.substring(pPos + pCount), caseSensitive)) {
				return true;
			    }
			}
		    }

		    return false;
		}

		// Else not a match, so stop here
		else {
		    return false;
		}
	    }

	    // Here we ran out of pattern, so we have a match if we also ran out of input
	    return (iPos >= inpLen);
	}

}

