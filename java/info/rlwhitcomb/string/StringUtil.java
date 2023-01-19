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
 *	Various string utility methods.
 *
 * History:
 *  27-Dec-22 rlw  --- Initial coding.
 */
package info.rlwhitcomb.string;

import info.rlwhitcomb.math.MathUtil;
import info.rlwhitcomb.util.CharUtil;
import info.rlwhitcomb.util.Exceptions;
import info.rlwhitcomb.wordfind.Dictionary;
import info.rlwhitcomb.wordfind.WordFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * Utility methods for dealing with strings.
 */
public class StringUtil
{
	/**
	 * A utility class should not be instantiated.
	 */
	private StringUtil() {
	}

	/**
	 * Debugging code.
	 *
	 * @param values Array of integer values to display.
	 * @return       A string representation of the array.
	 */
	private static String arrayList(final int[] values) {
	    StringBuilder buf = new StringBuilder(values.length * 4 + 2);
	    buf.append("[ ");
	    for (int i = 0; i < values.length; i++) {
		buf.append(values[i]);
		if (i < values.length - 1)
		    buf.append(", ");
	    }
	    buf.append(" ]");
	    return buf.toString();
	}

	/**
	 * Calculate the Levenshtein Distance between two strings, that is, the number
	 * of changes required to change the one into the other.
	 * <p> Taken from the "Iterative with two matrix rows" algorithm from:
	 * <a href="https://en.wikipedia.org/wiki/Levenshtein_distance">https://en.wikipedia.org/wiki/Levenshtein_distance</a>
	 *
	 * @param s The first string.
	 * @param t Second string.
	 * @return  The Levenshtein Distance between the two strings.
	 */
	public static int levenshteinDistance(final String s, final String t) {
	    int m = s.length();
	    int n = t.length();
	    int[] v0 = new int[n + 1];
	    int[] v1 = new int[n + 1];

	    // initialize v0 (the previous row of distances)
	    // this row is A[0][i]: edit distance from an empty s to t;
	    // that distance is the number of characters to append to s to make t.
	    for (int i = 0; i <= n; i++)
	        v0[i] = i;
//System.out.println("A[0]: " + arrayList(v0));

	    for (int i = 1; i <= m; i++) {
	        // calculate v1 (current row distances) from the previous row v0

	        // first element of v1 is A[i][0]
	        //   edit distance is delete (i) chars from s to match empty t
	        v1[0] = i;

	        // use formula to fill in the rest of the row
	        for (int j = 1; j <= n; j++) {
	            // calculating costs for A[i][j]
	            int deletionCost = v0[j] + 1;
	            int insertionCost = v1[j - 1] + 1;
		    int substitutionCost;

	            substitutionCost = s.charAt(i - 1) == t.charAt(j - 1) ? v0[j - 1] : v0[j - 1] + 1;

	            v1[j] = MathUtil.minimum(deletionCost, insertionCost, substitutionCost);
		}
//System.out.println("A[" + i + "]: " + arrayList(v1));

	        // copy v1 (current row) to v0 (previous row) for next iteration
	        // since data in v1 is always invalidated, a swap without copy could be more efficient
		System.arraycopy(v1, 0, v0, 0, v1.length);
	    }

	    // after the last swap, the results of v1 are now in v0
	    return v0[n];
	}

	/**
	 * Calculate the "Levenshtein" distance between two strings, that is, the number of
	 * insertions, substitutions, or deletions needed to transform the first string into
	 * the second one. The algorithm is taken from an article here:
	 * <a href="https://www.kleemans.ch/levenshtein-distance">https://www.kleemans.ch/levenshtein-distance</a>
	 * <p> This distance is a measure of how close these strings (words) are to each other,
	 * which can be used to find correctly spelled words "close to" a misspelled one.
	 *
	 * @param s The first string to be transformed.
	 * @param t Second string which is the destination of the transformation.
	 * @return  The minimum number of steps needed to change the first to the second.
	 */
	public static int levenshteinDistance2(final String s, final String t) {
	    int m = s.length();
	    int n = t.length();

	    int distance[][] = new int[m + 1][n + 1];
	    for (int i = 0; i <= m; i++) {
		Arrays.fill(distance[i], 0);
	    }

	    // initialization of first row/column
	    for (int i = 0; i <= m; i++) distance[i][0] = i;
	    for (int i = 0; i <= n; i++) distance[0][i] = i;

	    int cost = 0;

	    // loop through matrix cells
	    for (int i = 1; i <= m; i++) {
		for (int j = 1; j <= n; j++) {
		    cost = s.charAt(i-1) == t.charAt(j-1) ? 0 : 1;

		    distance[i][j] = MathUtil.minimum(
			distance[i-1][j] + 1,
			distance[i][j-1] + 1,
			distance[i-1][j-1] + cost);
		}
	    }
	    return distance[m][n];
	}

	public static void main(final String[] args) {
	    if (args.length == 2) {
		String s = args[0];
		String t = args[1];
		int dist = levenshteinDistance(s, t);
		int dist2 = levenshteinDistance2(s, t);

		System.out.println("Levenshtein Distance from \"" + s + "\" to \"" + t + "\" is " + dist + ", or " + dist2);
	    }
	    else if (args.length == 1) {
		Dictionary dict = new Dictionary();
		try {
		    dict.read(WordFile.DEFAULT, true);
		}
		catch (IOException ioe) {
		    System.err.println("I/O error reading dictionary: " + Exceptions.toString(ioe));
		    return;
		}

		List<String> closeWords = dict.findClosestWords(args[0], 2);
		System.out.println("Closest words to \"" + args[0] + "\":");
		System.out.println(CharUtil.makeSimpleStringList(closeWords));
	    }
	}

}

