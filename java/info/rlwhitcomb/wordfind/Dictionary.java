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
 *	Utility methods associated with reading and accessing a word file (or
 *	dictionary).
 *
 *  Change History:
 *	19-Jul-2022 (rlwhitcomb)
 *	    #411: Abstracted out of WordFind for use with other programs.
 */
package info.rlwhitcomb.wordfind;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Methods to read and access a word file dictionary.
 */
public class Dictionary
{
	/**
	 * Regular word list, arranged alphabetically.
	 */
	private final List<String> regularWords = new ArrayList<>(200_000);

	/**
	 * Additional word list, also alphabetical.
	 */
	private final List<String> additionalWords = new ArrayList<>(500);

	/**
	 * Whether the dictionary is kept in UPPER- or lower-case.
	 */
	private boolean lowerWords;

	/**
	 * Depending on the case the first character in the index table.
	 */
	private char firstChar;

	/**
	 * Indexes into the word list for each letter.
	 */
	private final int[] startingRegularIndex = new int[27];

	/**
	 * Indexes into the additional word list for each letter.
	 */
	private final int[] startingAddlIndex = new int[27];


	/**
	 * Add the new word to the appropriate list and update the starting indexes.
	 *
	 * @param word      Word to add.
	 * @param list      The list to add the word to.
	 * @param indexes   Starting indexes based on first character.
	 * @param lastStart Previous starting character.
	 * @return          Updated starting character, if it changed.
	 */
	private char addWord(final String word, final List<String> list, final int[] indexes, final char lastStart) {
	    char startChar = word.charAt(0);
	    char newLast = lastStart;

	    if (startChar != lastStart) {
		int index = (int) (startChar - firstChar);
		indexes[index] = list.size();
		newLast = startChar;
	    }

	    list.add(word);

	    return newLast;
	}

	/**
	 * Read the given dictionary into our word lists.
	 *
	 * @param wordFile  Which word list file to read.
	 * @param lowerCase How to store the words in the list.
	 * @return The number of words read and stored.
	 * @throws IOException if there is a problem reading the file.
	 */
	public int read(final WordFile wordFile, final boolean lowerCase)
		throws IOException
	{
	    char lastRegularStart = '\0';
	    char lastAddlStart = '\0';
	    lowerWords = lowerCase;
	    firstChar = lowerCase ? 'a' : 'A';

	    InputStream is = this.getClass().getResourceAsStream(wordFile.getFileName());
	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
		String line;
		while ((line = reader.readLine()) != null) {
		    line = line.trim();
		    if (line.length() <= 1
		     || line.startsWith("#")
		     || line.startsWith("//")
		     || line.startsWith("--")) {
			continue;
		    }
		    String word = lowerCase ? line.toLowerCase() : line.toUpperCase();
		    if (word.endsWith("?")) {
			word = word.substring(0, word.length() - 1);
			lastAddlStart = addWord(word, additionalWords, startingAddlIndex, lastAddlStart);
		    }
		    else {
			lastRegularStart = addWord(word, regularWords, startingRegularIndex, lastRegularStart);
		    }
		}
		startingRegularIndex[26] = regularWords.size();
		startingAddlIndex[26] = additionalWords.size();;
	    }

	    return regularWords.size() + additionalWords.size();
	}


	/**
	 * Get the number of regular words in the dictionary.
	 *
	 * @return The number of words in the {@link #regularWords} list.
	 */
	public int getNumberWords() {
	    return regularWords.size();
	}

	/**
	 * Get the number of additional words in the dictionary.
	 *
	 * @return The number of words in the {@link #additionalWords} list.
	 */
	public int getNumberAddlWords() {
	    return additionalWords.size();
	}

	/**
	 * Search the given list using the indexes provided to see if the input word is found.
	 *
	 * @param word    Word to search for.
	 * @param list    The list to search in.
	 * @param indexes Indexes into the list based on starting letter.
	 */
	private boolean contained(final String word, final List<String> list, final int[] indexes) {
	    char startChar = word.charAt(0);
	    int charIndex = (int) (startChar - firstChar);
	    int startIndex = indexes[charIndex];
	    int endIndex = indexes[charIndex + 1];

	    return Collections.binarySearch(list.subList(startIndex, endIndex), word) >= 0;
	}

	/**
	 * Is the given word found in the current dictionary?
	 *
	 * @param word             The word to lookup.
	 * @param findInAdditional Should we search the additional word list if not found
	 *                         in the regular list.
	 * @return                 Whether or not the word is in the word list.
	 */
	public boolean contains(final String word, final boolean findInAdditional) {
	    String searchWord = lowerWords ? word.toLowerCase() : word.toUpperCase();

	    return contained(searchWord, regularWords, startingRegularIndex) ||
		(findInAdditional && contained(searchWord, additionalWords, startingAddlIndex));
	}

}

