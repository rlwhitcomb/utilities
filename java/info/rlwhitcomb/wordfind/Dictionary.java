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
 *	27-Jul-2022 (rlwhitcomb)
 *	    Add indexed access, and random word selection.
 *	    New "displayStatistics" method.
 */
package info.rlwhitcomb.wordfind;

import info.rlwhitcomb.util.CharUtil;
import info.rlwhitcomb.util.Intl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Methods to read and access a word file dictionary.
 */
public class Dictionary
{
	/**
	 * Enumeration of which dictionary file is currently loaded (if any).
	 */
	private WordFile currentFile = null;

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
	 * From initial read of the dictionary, the maximum word length found.
	 */
	private int maxWordLength = 0;

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

	    maxWordLength = Math.max(maxWordLength, word.length());

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

	    if (wordFile == currentFile) {
		return startingRegularIndex[26] + startingAddlIndex[26];
	    }
	    currentFile = wordFile;

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

	    // Fix up index table in case there were letters not used (for statistics display)
	    for (int i = 1; i < 26; i++) {
		if (startingRegularIndex[i] == 0)
		    startingRegularIndex[i] = startingRegularIndex[i + 1];
		if (startingAddlIndex[i] == 0)
		    startingAddlIndex[i] = startingAddlIndex[i + 1];
	    }

	    return startingRegularIndex[26] + startingAddlIndex[26];
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
	 * Get the indexed word in the regular words list.
	 *
	 * @param index Index into the regular words list.
	 * @return      The regular word at that index.
	 * @throws      IndexOutOfBoundsException if that applies.
	 */
	public String getWord(final int index) {
	    if (index < 0 || index >= regularWords.size())
		throw new Intl.IndexOutOfBoundsException("%wordfind#errIndexOutOfBounds", index, regularWords.size());

	    return regularWords.get(index);
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

	/**
	 * Get a random word from this dictionary of a maximum size.
	 *
	 * @param maxWordSize Sometimes getting ridiculously long words doesn't make sense,
	 * so limit it (but 0 = no limit).
	 * @return A random word from the loaded dictionary within the given size range.
	 */
	public String getRandomWord(final int maxWordSize) {
	    int maxSize = getNumberWords();
	    String randomWord = "";

	    do {
		int randomInt = (int) ((double) maxSize * Math.random());
		randomWord = getWord(randomInt);
	    } while (maxWordSize >= 0 && randomWord.length() > maxWordSize);

	    return randomWord;
	}

	private void displayNumbers(final PrintStream ps, final int[] indexes, final String which) {
	    String title = "Number of " + which + " words beginning with:";
	    String under1 = CharUtil.makeStringOfChars('-', title.length());
	    String under2 = CharUtil.makeStringOfChars('=', title.length());

	    ps.println(title);
	    ps.println(under1);
	    for (int i = 0; i < 26; i++) {
		int count = indexes[i + 1] - indexes[i];
		char letter = (char) ((int) firstChar + i);
		ps.format("%1$5c. %2$,7d%n", letter, count);
	    }
	    ps.format("Total: %1$,7d%n", indexes[26]);
	    ps.println(under2);
	}

	private void displaySizes(final PrintStream ps, final int[] counts, final String which) {
	    String title = "Number of " + which + " words with size:";
	    String under1 = CharUtil.makeStringOfChars('-', title.length());
	    String under2 = CharUtil.makeStringOfChars('=', title.length());

	    ps.println(title);
	    ps.println(under1);

	    int numberChars = 0;
	    int numberWords = 0;

	    // We have no zero or one character words!
	    for (int i = 2; i < counts.length; i++) {
		if (counts[i] != 0) {
		    ps.format("%1$7d: %2$,7d%n", i, counts[i]);
		    numberWords += counts[i];
		    numberChars += (counts[i] * i);
		}
	    }
	    ps.format("Average: %1$7.2f%n", (float) numberChars / (float) numberWords);
	    ps.println(under2);
	}

	/**
	 * Display statistics about the current dictionary.
	 *
	 * @param ps The {@link PrintStream} to display to (usually {@link System#out}).
	 */
	public void displayStatistics(final PrintStream ps) {
	    String title = "Statistics for " + currentFile.getFileName() + " dictionary";
	    String under = CharUtil.makeStringOfChars('=', title.length());

	    ps.println(under);
	    ps.println(title);
	    ps.println(under);
	    ps.println();

	    displayNumbers(ps, startingRegularIndex, "Regular");
	    displayNumbers(ps, startingAddlIndex, "Additional");

	    int regularSizeCounts[] = new int[maxWordLength + 1];
	    int addlSizeCounts[] = new int[maxWordLength + 1];

	    Arrays.fill(regularSizeCounts, 0);
	    Arrays.fill(addlSizeCounts, 0);

	    for (String word : regularWords) {
		int len = word.length();
		regularSizeCounts[len]++;
	    }
	    for (String word : additionalWords) {
		int len = word.length();
		addlSizeCounts[len]++;
	    }

	    displaySizes(ps, regularSizeCounts, "Regular");
	    displaySizes(ps, addlSizeCounts, "Additional");
	}

}

