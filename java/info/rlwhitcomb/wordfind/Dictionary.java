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
 * History:
 *  19-Jul-22 rlw #411: Abstracted out of WordFind for use with other programs.
 *  27-Jul-22 rlw  ---  Add indexed access, and random word selection.
 *			New "displayStatistics" method.
 *			Omit additional word list statistics if there are none (some of
 *			the dictionaries don't have any).
 *  04-Sep-22 rlw #29:	Save a dictionary entry for each word.
 *			Search for valid words using the letter counts.
 *			Further refactoring.
 *  05-Sep-22		Properly deal with wild card letters.
 *  06-Sep-22		Slight optimization if there are no wildcards.
 *  15-Sep-22 rlw #478:	Also allow '_' as wild character.
 *  27-Dec-22 rlw	Add method to use Levenshtein distance to find words
 *			"close" to an input string.
 */
package info.rlwhitcomb.wordfind;

import info.rlwhitcomb.string.StringUtil;
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
	/** Where to keep the count of wildcard letters in the "letters" array. */
	static final int WILD_INDEX = 26;

	/**
	 * A dictionary entry, consisting of the word, along with the letter
	 * frequency count, first letter and length (for convenience).
	 */
	public static class Entry implements Comparable<Entry>
	{
		String word;
		char   firstLetter;
		int    index;
		int    length;
		int    numberWild;
		int[]  letters;

		/**
		 * Construct an entry from the given word, converting case as needed.
		 *
		 * @param w     The word to construct this entry from.
		 * @param lower Whether to convert to lower or upper case.
		 */
		Entry(final String w, final boolean lower) {
		    word = lower ? w.toLowerCase() : w.toUpperCase();
		    firstLetter = word.charAt(0);
		    index = (int) firstLetter - (lower ? 'a' : 'A');
		    length = word.length();
		    letters = countLetters(word);
		}

		@Override
		public int compareTo(Entry o) {
		    return word.compareTo(o.word);
		}

		/**
		 * Determine if this entry could be spelled by the given set of letters.
		 *
		 * @param letterEntry The set of letters we have in hand to spell with.
		 * @param wildChars   If any wildcard letters were used, what are they?
		 * @param lowerCase   Whether the dictionary is lowercased or not.
		 * @return            Whether these letters could spell this entry.
		 */
		public boolean couldBeSpelledBy(final Entry letterEntry, final StringBuilder wildChars, final boolean lowerCase) {
		    // If there are not enough letters in the candidate entry, then no.
		    if (letterEntry.length < length)
			return false;

		    int wildCount = letterEntry.letters[WILD_INDEX];
		    wildChars.setLength(0);

		    for (int i = 0; i < WILD_INDEX; i++) {
			// If this word requires more of this one letter than the list of
			// letters contains, check for available wildcards.
			if (letters[i] > letterEntry.letters[i]) {
			    if (letters[i] > (letterEntry.letters[i] + wildCount))
				return false;

			    wildCount -= (letters[i] - letterEntry.letters[i]);
			    wildChars.append((char) ((lowerCase ? 'a' : 'A') + i));
			}
		    }

		    return true;
		}

		@Override
		public String toString() {
		    StringBuilder buf = new StringBuilder(length + 84);
		    buf.append(word).append(": [ ");
		    for (int i = 0; i < letters.length; i++) {
			buf.append(String.format("%1$2d", letters[i]));
			if (i < letters.length - 1)
			    buf.append(',');
		    }
		    buf.append(" ]");

		    return buf.toString();
		}
	}


	/**
	 * Is the given character a "wild card" character, that is, one of
	 * {@code ' '}, {@code '?'}, or {@code '.'}.
	 *
	 * @param ch The given character to check.
	 * @return   Whether it is "wild"?
	 */
	public static boolean isWild(final char ch) {
	    return (ch == ' ') || (ch == '?') || (ch == '.') || (ch == '_');
	}


	/**
	 * Count the frequency of each letter in the given word.
	 * <p> If the letter is a "wild card" ({@code '?'}, {@code ' '}, or {@code '.'})
	 * then the count of every letter is incremented.
	 *
	 * @param word  The word to count, all upper- or lower-case.
	 * @return      Letter frequency array.
	 */
	public static int[] countLetters(final String word) {
	    int[] letters = new int[WILD_INDEX + 1];
	    Arrays.fill(letters, 0);

	    int base = (int) (Character.isLowerCase(word.charAt(0)) ? 'a' : 'A');

	    for (int i = 0; i < word.length(); i++) {
		char letter = word.charAt(i);
		if (isWild(letter)) {
		    letters[WILD_INDEX]++;
		}
		else {
		    int index = (int) letter - base;
		    letters[index]++;
		}
	    }

	    return letters;
	}


	/**
	 * Enumeration of which dictionary file is currently loaded (if any).
	 */
	private WordFile currentFile = null;

	/**
	 * Regular word list, arranged alphabetically.
	 */
	private final List<Entry> regularWords = new ArrayList<>(200_000);

	/**
	 * Additional word list, also alphabetical.
	 */
	private final List<Entry> additionalWords = new ArrayList<>(500);

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
	 * @return The maximum word size of this dictionary.
	 */
	public int getMaxWordLength() {
	    return maxWordLength;
	}


	/**
	 * Add the new word to the appropriate list and update the starting indexes.
	 *
	 * @param word      Word to add.
	 * @param list      The list to add the word to.
	 * @param indexes   Starting indexes based on first character.
	 * @param lastStart Previous starting character.
	 * @return          Updated starting character, if it changed.
	 */
	private char addWord(final String word, final List<Entry> list, final int[] indexes, final char lastStart) {
	    Entry entry = new Entry(word, lowerWords);

	    char startChar = entry.firstLetter;
	    char newLast = lastStart;

	    if (startChar != lastStart) {
		indexes[entry.index] = list.size();
		newLast = startChar;
	    }

	    maxWordLength = Math.max(maxWordLength, entry.length);

	    list.add(entry);

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
		    String word = line;
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

	    return regularWords.get(index).word;
	}

	/**
	 * Search the given list using the indexes provided to see if the input word is found.
	 *
	 * @param word    Word to search for.
	 * @param list    The list to search in.
	 * @param indexes Indexes into the list based on starting letter.
	 */
	private boolean contained(final String word, final List<Entry> list, final int[] indexes) {
	    char startChar = word.charAt(0);
	    int charIndex = (int) (startChar - firstChar);
	    int startIndex = indexes[charIndex];
	    int endIndex = indexes[charIndex + 1];
	    Entry wordEntry = new Entry(word, lowerWords);

	    return Collections.binarySearch(list.subList(startIndex, endIndex), wordEntry) >= 0;
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
	 * Find all the valid words for the given string of letters in the word list.
	 *
	 * @param letterEntry Entry with the letters to search for.
	 * @param words       List of words to check against.
	 * @param result      The result list to build.
	 * @return            The maximum length of the valid words found.
	 */
	private int findValid(final Entry letterEntry, final List<Entry> words, final List<String> result) {
	    int maxLength = 0;
	    StringBuilder wildChars = new StringBuilder(maxWordLength);

	    for (Entry entry : words) {
		if (entry.couldBeSpelledBy(letterEntry, wildChars, lowerWords)) {
		    if (wildChars.length() > 0) {
			StringBuilder adornedWord = new StringBuilder(entry.word);
			for (int i = 0; i < wildChars.length(); i++) {
			    String wildStr = wildChars.substring(i, i + 1);
			    int ix = adornedWord.lastIndexOf(wildStr);
			    adornedWord.insert(ix, WordFind.BLANK_MARKER);
			    adornedWord.insert(ix + 2, WordFind.BLANK_MARKER);
			}
			result.add(adornedWord.toString());
		    }
		    else {
			result.add(entry.word);
		    }
		    maxLength = Math.max(maxLength, entry.length);
		}
	    }

	    return maxLength;
	}

	/**
	 * Get a list of words from this dictionary that can be made by the given string of letters.
	 * <p> Compare the words with not more than the length of the target letters in length, and
	 * compile the words that can be made from these letters, by their frequency (determined at
	 * construction time for the dictionary).
	 *
	 * @param result  The list of valid words to populate
	 * @param letters The string of letters to check.
	 * @param addl    Whether to (also) check the additional words list.
	 * @return        Maximum length of all the words found (0 if none).
	 */
	public int findAllValidWords(List<String> result, final String letters, final boolean addl) {
	    Entry letterEntry = new Entry(letters, lowerWords);
	    int maxLength;

	    maxLength = findValid(letterEntry, regularWords, result);

	    if (addl) {
		maxLength = Math.max(maxLength, findValid(letterEntry, additionalWords, result));
	    }

	    return maxLength;
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

	/**
	 * Get the list of all dictionary words that are "closest" to the given input string
	 * (which is presumably a misspelled word).
	 *
	 * @param input   Input string to get alternatives for.
	 * @param maxDist The "closeness" value we will tolerate.
	 * @return        List of words in the dictionary that are within the given Levenshtein
	 *                distance of the input.
	 * @see StringUtil#levenshteinDistance
	 */
	public List<String> findClosestWords(final String input, final int maxDist) {
	    List<String> result = new ArrayList<>();
	    @SuppressWarnings("unchecked")
	    ArrayList<String>[] lists = (ArrayList<String>[]) new ArrayList[maxDist + 1];
	    for (int dist = 0; dist <= maxDist; dist++) {
		lists[dist] = new ArrayList<>();
	    }

	    for (int i = 0; i < regularWords.size(); i++) {
		String word = regularWords.get(i).word;
		int dist = StringUtil.levenshteinDistance(input, word);
		if (dist <= maxDist)
		    lists[dist].add(word);
		// Special case if the word is valid
		if (dist == 0)
		    break;
	    }

	    for (int i = 0; i <= maxDist; i++)
		result.addAll(lists[i]);

	    return result;
	}

	private void displayNumbers(final PrintStream ps, final int[] indexes, final String which) {
	    if (indexes[26] == 0)
		return;

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

	    for (Entry entry: regularWords) {
		int len = entry.length;
		regularSizeCounts[len]++;
	    }
	    for (Entry entry : additionalWords) {
		int len = entry.length;
		addlSizeCounts[len]++;
	    }

	    displaySizes(ps, regularSizeCounts, "Regular");
	    if (startingAddlIndex[26] != 0)
		displaySizes(ps, addlSizeCounts, "Additional");
	}

}

