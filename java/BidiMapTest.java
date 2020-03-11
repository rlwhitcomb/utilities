/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2013-2014,2016,2020 Roger L. Whitcomb.
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
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import info.rlwhitcomb.util.BidiMap;
import info.rlwhitcomb.util.Options;

/**
 * Test class for the {@link BidiMap} class.
 * <p> Command line arguments include the name of a test file to
 * read to get words to add, as well as test options.
 */
public class BidiMapTest
{
	private static final int NUMBER_SIZE = 11400;	// < 70% of 16,384 (default load factor)
	private static final int WORD_SIZE   = 2860;	// < 80% of 4,096 (80% load factor)

	private static int size;

	private static BidiMap<String, String> map;
	private static List<String> keysToRemoveList;

	private static boolean verbose = false;
	private static boolean useNumbers = false;
	private static boolean useWords = false;
	private static boolean useUTF8 = false;

	private static String[] TEXT_FILE_NAMES = {
		"Declaration",
		"Constitution",
		"Gettysburg",
		"MagnaCarta",
		"Psalm23"
	};

	public static void main(String[] args) {
		// Go through command line arguments and interpret the options there
		for (String arg : args) {
			String opt = Options.isOption(arg);
			if (opt != null) {
				if (opt.equalsIgnoreCase("verbose") ||
				    opt.equalsIgnoreCase("v"))
					verbose = true;
				else if (opt.equalsIgnoreCase("num") ||
					 opt.equalsIgnoreCase("number") ||
					 opt.equalsIgnoreCase("numbers") ||
					 opt.equalsIgnoreCase("n"))
					useNumbers = true;
				else if (opt.equalsIgnoreCase("word") ||
					 opt.equalsIgnoreCase("words") ||
					 opt.equalsIgnoreCase("w"))
					useWords = true;
				else if (opt.equalsIgnoreCase("utf8") ||
					 opt.equalsIgnoreCase("utf-8") ||
					 opt.equals("8"))
					useUTF8 = true;
				else {
					System.err.format("Unknown option: \"%1$s\"%n", arg);
					System.exit(1);
				}
			}
			else {
				System.err.format("Unknown option: \"%1$s\"%n", arg);
				System.exit(1);
			}
		}
		// Decide default options if nothing specified
		if (!useNumbers && !useWords) {
			useNumbers = true;
			verbose = true;
		}
		// For the "words" option go through the provided list of text files, reading each
		// in turn, and entering all the words into the map
		if (useWords) {
			size = WORD_SIZE;
			map = new BidiMap<String, String>(size, 0.80f);
			keysToRemoveList = new ArrayList<String>(size / 12);

			for (String name : TEXT_FILE_NAMES) {
				try (BufferedReader reader = Files.newBufferedReader(Paths.get(name + ".txt"),
				            useUTF8 ? StandardCharsets.UTF_8 : Charset.defaultCharset()))
				{
					if (verbose)
						System.out.format("Entering words from file \"%1$s.txt\"...%n", name);
					String line;
					int i = 0;
					while ((line = reader.readLine()) != null) {
						String[] words = line.split("\\s+");
						for (String word : words) {
							if (!map.containsKey(word)) {
								map.put(word, word);
								if ((i++ % 13) == 0) {
									keysToRemoveList.add(word);
								}
							}
						}
					}
					if (verbose)
						System.out.format("Entered %1$d unique words.%n", (i + 1));
				} catch (IOException ioe) {
					System.err.format("Error reading \"%1$.txt\": %2$s%n", name, ioe.getMessage());
					System.exit(2);
				}
			}
		}
		if (useNumbers) {
			size = NUMBER_SIZE;
			map = new BidiMap<String, String>(size);
			keysToRemoveList = new ArrayList<String>(size / 12);

			// Populate the map with random values pointing to itself
			boolean neg = false;
			for (int i = 0; i < size; i++) {
				long random = Double.doubleToLongBits(Math.random()) & (long)Integer.MAX_VALUE;
				if (neg)
					random = -random;
				String randomString = String.valueOf(random);
				map.put(randomString, randomString);
				// Every 13th iteration, put key into list to remove later
				if ((i % 13) == 0) {
					keysToRemoveList.add(randomString);
				}
				neg = !neg;
			}
		}
		if (verbose)
			map.dumpState();

		// Now go through and remove every 13th key/value pair, checking to make sure the removed
		// value is correct and dump the final map again.  Every other one, remove it using
		// the value instead.
		boolean which = false;
		for (String key : keysToRemoveList) {
			if (which) {
				String removedKey = map.removeValue(key);
				if (!removedKey.equals(key)) {
					System.err.format("Error removing by value '%1$s', got '%2$s' back!%n", key, removedKey);
				}
				// Test that the value isn't there after removal
				if (map.removeValue(key) != null) {
					System.err.format("Removed value '%1$s' is still found after remove!%n", key);
				}
			}
			else {
				String removedValue = map.remove(key);
				if (!removedValue.equals(key)) {
					System.err.format("Error removing by key '%1$s', got '%2$s' back!%n", key, removedValue);
				}
				if (map.remove(key) != null) {
					System.err.format("Removed key '%1$s' is still found after remove!%n", key);
				}
			}
			which = !which;
		}
		if (verbose)
			map.dumpState();
	}
}
