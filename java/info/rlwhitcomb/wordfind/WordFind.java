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
 *	"Word Finder" program; runs in either command line or GUI mode (using
 *	the Apache Pivot GUI framework).
 *
 *   Change History:
 *	22-Sep-2020 (rlwhitcomb)
 *	    Initial checkin to GitHub. Allow comments and blank lines in the
 *	    master word file. The GUI is not yet implemented.
 *	08-Oct-2020 (rlwhitcomb)
 *	    Allow choice of word files.
 *	08-Oct-2020 (rlwhitcomb)
 *	    Small changes to support GUI mode. Use CharUtil methods.
 *	    Fix bug that was missing a bunch of words when using blanks.
 *	    Also highlight and correct point values with blanks.
 *	16-Oct-2020 (rlwhitcomb)
 *	    Display the number of permutations at the end.
 *	22-Dec-2020 (rlwhitcomb)
 *	    Use a different API to read the txt files from the .jar file.
 * 	22-Dec-2020 (rlwhitcomb)
 *	    Fix obsolete Javadoc constructs and other errors. Abandon the
 *	    searching if too many are needed (temp fix).
 *	26-Dec-2020 (rlwhitcomb)
 *	    Use a better algorithm for "contains".
 */
package info.rlwhitcomb.wordfind;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;
import info.rlwhitcomb.util.CharUtil;
import static info.rlwhitcomb.util.ConsoleColor.Code.*;

/**
 * A utility program to make sense out of random letter tiles
 * (such as for the "Scrabble" &trade; or "Word With Friends" &trade; games).
 */
public class WordFind implements Application {
    /**
     * Name of the default master word file. Sourced from:
     * <a href="https://scrabutility.com/TWL06.txt">TWL06.txt</a>
     * with some additions and optional words as found using
     * Words With Friends.
     */
    private static final String WORD_FILE_DEFAULT = "TWL06a.txt";
    /**
     * The original master word file without any changes or additions.
     */
    private static final String WORD_FILE_ORIGINAL = "TWL06.txt";
    /**
     * The original, original word file that others were derived from
     * (all in upper case to be consistent with the others).
     */
    private static final String WORD_FILE_ANTIQUE = "ENABLE1U.txt";

    /** The lookup set of known words. */
    private static final Set<String> words = new HashSet<>(200_000);
    /**
     * Additional words that appear in our dictionary but not valid
     * (apparently) on Words With Friends (noted by trailing "?"
     * in the word file).
     */
    private static final Set<String> additionalWords = new HashSet<>(1000);
    /** Should we look in the {@link #additionalWords} set?  */
    private static boolean findInAdditional = false;
    /** For option processing, whether we are running on a Windows OS. */
    private static final boolean ON_WINDOWS = System.getProperty("os.name").startsWith("Windows");
    /** Big switch whether to run as a console app or a GUI app. */
    private static boolean runningOnConsole = true;
    /** Which word file to load (defaults to our custom one). */
    private static String wordFile = WORD_FILE_DEFAULT;
    /** Default column width for output. */
    private static final int DEFAULT_COLUMN_WIDTH = 10;
    /** The (possibly configurable) line length for output. */
    private static int maxLineLength = 72;
    /** Whether to interpret the command line words as letters or whole words. */
    private static boolean letter = true;
    /** Whether to deal with input and output as all lower case or UPPER case. */
    private static boolean lowerCase = false;
    /** The beginning value of the alphabet, which depends on lower or UPPER case flag. */
    private static char alphaStart;
    /** Whether next value is supposed to be a "Begins With" value. */
    private static boolean beginsWith = false;
    /** Optional "Begins With" value. */
    private static Optional<String> beginningValue = Optional.empty();
    /** Whether next value is supposed to be a "Contains" value. */
    private static boolean contains = false;
    /** Optional "Contains" value. */
    private static Optional<String> containsValue = Optional.empty();
    /** Whether next value is supposed to be an "Ends With" value. */
    private static boolean endsWith = false;
    /** Optional "Ends With" value. */
    private static Optional<String> endingValue = Optional.empty();
    /** The format string for final output of the words. */
    private static final String WORD_FORMAT = "%1$s%2$s " + BLACK_BRIGHT + "(%3$3d)" + RESET;

    /**
     * Sort alphabetically?
     */
    private static boolean sortAlphabetically = false;
    /**
     * Letter point values for sorting purposes.
     */
    private static final int[] POINT_VALUES = {
        /* A */ 1,
        /* B */ 4,
        /* C */ 4,
        /* D */ 2,
        /* E */ 1,
        /* F */ 4,
        /* G */ 3,
        /* H */ 3,
        /* I */ 1,
        /* J */10,
        /* K */ 5,
        /* L */ 2,
        /* M */ 4,
        /* N */ 2,
        /* O */ 1,
        /* P */ 4,
        /* Q */10,
        /* R */ 1,
        /* S */ 1,
        /* T */ 1,
        /* U */ 2,
        /* V */ 5,
        /* W */ 4,
        /* X */ 8,
        /* Y */ 3,
        /* Z */10
    };

    /**
     * Calculate the total point value for the given word, taking into account
     * blanks which are delimited with "_".
     *
     * @return The total point values (from {@link #POINT_VALUES} for the given word.
     * @param word The word to check.
     */
    private static int addLetterValues(final String word) {
        int pointValue = 0;
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (ch == '_')
                i += 2; // skip "_X_" which is how a blank is marked
            else
                pointValue += POINT_VALUES[ch - alphaStart];
        }
        return pointValue;
    }

    /**
     * @return The bare letter values without the _X_ markers for blanks.
     * @param blankDelimitedWord The input with "_" around letters substituted for blanks.
     */
    private static String getLettersOnly(final String blankDelimitedWord) {
        return blankDelimitedWord.replace("_", "");
    }

    /**
     * Highlight a word making special emphasis on the blank substitutions.
     * @param adornedWord The word to highlight with the "_" markers.
     * @return The input with the markers replaced with the actual color codes.
     */
    private static String highlightWord(final String adornedWord) {
        int wordLen = adornedWord.length();
        StringBuilder buf = new StringBuilder(wordLen);
        for (int i = 0; i < wordLen; i++) {
            char ch = adornedWord.charAt(i);
            if (ch == '_') {
                buf.append(RED_BRIGHT).append(adornedWord.charAt(i + 1)).append(RESET);
                i += 2;
            } else {
                buf.append(ch);
            }
        }
        return buf.toString();
    }

    /**
     * A comparator of letter values.
     */
    private static final Comparator<String> valueComparator = (s1, s2) -> {
        /* Get bare letter versions of the inputs. */
        String word1 = getLettersOnly(s1);
        String word2 = getLettersOnly(s2);

        /* Only use the point values if the words are not the same letters. */
        if (word1.equals(word2)) {
            return 0;
        }

        int v1 = addLetterValues(s1);
        int v2 = addLetterValues(s2);

        /* If alpha sort is turned on OR the letter values are the same, sort alphabetically. */
        if (sortAlphabetically || v1 == v2) {
            return word1.compareTo(word2);
        }

        /* Point value sort is highest to lowest. */
        return v2 - v1;
    };

    /**
     * Insert a string into either an adorned or unadorned string at the given index.
     *
     * @param buf   The buffer to edit.
     * @param index The position at which to insert the string (-1 is append to end).
     * @param str   The string to insert.
     * @return      The input buffer.
     */
    private static final StringBuilder insert(final StringBuilder buf, final int index, final String str) {
        int pos;

        // Don't need to do anything if the insert string is nothing
        if (str == null || str.isEmpty())
            return buf;

        if (index == 0) {
	    return buf.insert(0, str);
        }
        else if (index == -1) {
            return buf.append(str);
        }
        else if (index < 0) {
            throw new IllegalArgumentException("Negative index (except -1) not allowed.");
        }
        else {
            pos = index;
        }

        // Move from 0 past the adornments to the desired position
        int charPos = 0;
        for (int ix = 0; ix < pos; ix++) {
            if (charPos >= buf.length())
                break;
            char ch = buf.charAt(charPos);
            if (ch == '_')
                charPos += 3;
            else
                charPos++;
        }

        return buf.insert(charPos, str);
    }

    /**
     * A mapping function to convert the case of words, depending on the command line option {@link #lowerCase}.
     */
    private static final Function<String, String> caseMapper = s -> lowerCase ? s.toLowerCase() : s.toUpperCase();

    /**
     * Find all the valid possible permutations of the input string (recursive call).
     * <p> Algorithm taken from: https://www.geeksforgeeks.org/print-all-permutations-of-a-string-in-java/
     * @param prefix         The prefix we've checked so far.
     * @param str            The rest of the string to check.
     * @param permutationSet The set of permuted arrangements already made.
     * @param validWords     The final output set of valid words to add to.
     * @return               {@code false} to abort (time or space constraints), {@code true} to continue
     */
    private static boolean findValidPermutations(
	final String prefix,
	final String str,
	final Set<String> permutationSet,
	final Set<String>[] validWords)
    {
        if (str.isEmpty()) {
            // Sometimes we use the letters only, but at the end we need to leave the blank markers alone
            String word = getLettersOnly(prefix);

            // Early check for words less than two characters without additions
            if (word.length() < 2 && !containsValue.isPresent() && !beginningValue.isPresent() && !endingValue.isPresent())
                return true;

            // If this is a word with blanks, but the word without blanks is already there, then
            // don't bother working on this version. This simpler test only works because we put
            // the blanks at the end of the sequence, so we find the better choices first.
            if (!prefix.equals(word)) {
                if (permutationSet.contains(word))
                    return true;
            }

	    // If the permutation was already checked, then just leave without doing any more
            // (distinguish between the same word but using a blank vs. with regular letters)
	    if (!permutationSet.add(prefix))
		return true;

            // TODO: is there a better algorithm that will shorten the time?
            // Or a better algorithm with "contains" tests that won't exponentially
            // increase our time?  Or should this be a parameter, or time-based or ...?
            if (permutationSet.size() >= 10_000_000) {
                System.err.println("Aborting after 10,000,000 permutations checked!");
                return false;
            }

	    // Now start mucking with the valid word and "begins with", "ends with", "contains" tests
            // (and we need two copies, one with the blank markers and one without for word lookup)
            final StringBuilder bufAdorned   = new StringBuilder();
            final StringBuilder bufUnadorned = new StringBuilder();
	    int containsEnd = containsValue.isPresent() ? word.length() : 0;

	    // For "containing", loop through each position of the input, inserting the "containing" string
	    // at each position; after that insert the "beginning" and "ending" values.
	    for (int containsIndex = 0; containsIndex <= containsEnd; containsIndex++) {
                bufAdorned.setLength(0);   bufAdorned.append(prefix);
                bufUnadorned.setLength(0); bufUnadorned.append(word);

                if (containsValue.isPresent()) {
                    insert(bufAdorned,   containsIndex, containsValue.get());
                    insert(bufUnadorned, containsIndex, containsValue.get());
                }
                beginningValue.ifPresent(v -> { insert(bufAdorned, 0, v);  insert(bufUnadorned, 0, v);  });
                endingValue.ifPresent   (v -> { insert(bufAdorned, -1, v); insert(bufUnadorned, -1, v); });

                final String wordUnadorned = bufUnadorned.toString();
                // There are no valid one letter (or blank) words
                if (wordUnadorned.length() < 2)
                    break;

                if (words.contains(wordUnadorned)
                || (findInAdditional && additionalWords.contains(wordUnadorned))) {
                    int index = wordUnadorned.length() - 1;
                    validWords[index].add(bufAdorned.toString());
                }
            }
            return true;
        }

	// The intermediate stages where we are permuting the input for all possibilities
        for (int i = 0; i < str.length(); i++) {
            String restOfString;
	    String newPrefix;
            char ch;

            // Special case handling of the delimited blank substitutions
            ch = str.charAt(i);
            if (ch == '_') {
                restOfString = str.substring(0, i) + str.substring(i + 3);
            } else {
                restOfString = str.substring(0, i) + str.substring(i + 1);
            }

            // Deal with wildcard letter (blank)
            if (ch == ' ' || ch == '?') {
                for (int j = 0; j < 26; j++) {
                    char ch2 = (char) (alphaStart + j);
                    // Make an annotated string with the blank location marked with "_"
                    newPrefix = prefix + "_" + ch2 + "_";

                    if (!findValidPermutations(newPrefix, restOfString, permutationSet, validWords))
                        return false;
                }
            } else {
                if (ch == '_')
                    newPrefix = prefix + str.substring(i, i + 3);
                else
                    newPrefix = prefix + ch;

                if (!findValidPermutations(newPrefix, restOfString, permutationSet, validWords))
                    return false;
            }
        }
        return true;
    }

    @Override
    public void startup(final Display display, org.apache.pivot.collections.Map<String, String> properties) {
        // TODO: the whole GUI program!
    }

    @Override
    public boolean shutdown(final boolean optional) {
        return false;
    }

    /**
     * Print an error message to {@link System#err} and highlight in red when running
     * in the console, else send to the GUI error message text.
     *
     * @param message The error message to display.
     */
    private static void error(final String message) {
        if (runningOnConsole) {
            System.err.println(RED_BOLD + message + RESET);
        } else {
            // TODO: implement; whether to have separate error/info text boxes or the same with diff colors?
        }
    }

    /**
     * Print an informational message to {@link System#out} and highlight in green when running
     * in the console, else send to the GUI information message text.
     *
     * @param message The informational message to display.
     */
    private static void info(final String message) {
        if (runningOnConsole) {
            System.out.println(GREEN + message + RESET);
        } else {
            // TODO: implement; whether to have separate error/info text boxes or the same with diff colors?
        }
    }

    /**
     * Print a heading message to {@link System#out} and color it bold cyan.
     * @param message The heading message to display.
     */
    private static void heading(final String message) {
        System.out.println(CYAN_BOLD + message + RESET);
    }

    /**
     * Print a section message to {@link System#out} and make it bold (black).
     * @param message The section message to display.
     */
    private static void section(final String message) {
        System.out.println(BLACK_BOLD_BRIGHT + message + RESET);
    }

    /**
     * Match an incoming argument against a list of possible choices (case-insensitive).
     * @param arg The command line option to match.
     * @param choices The list of valid choices to match.
     * @return Whether the arg matches any one of the choices.
     */
    private static boolean matches(final String arg, final String... choices) {
        return CharUtil.matchesAnyOfIgnoreCase(arg, choices);
    }

    /**
     * Process all the possible options from the command line.
     * @param prefix Which of the possible option prefixes was found (for error message).
     * @param arg The command-line argument to process.
     */
    private static void processOption(final String prefix, final String arg) {
        if (matches(arg, "letters", "letter", "l")) {
            letter = true;
        } else if (matches(arg, "words", "word", "w")) {
            letter = false;
        } else if (matches(arg, "alpha", "atoz", "a")) {
           sortAlphabetically = true;
        } else if (matches(arg, "points", "point", "p")) {
           sortAlphabetically = false;
        } else if (matches(arg, "find", "additional", "addl", "extra", "ex", "f", "x")) {
           findInAdditional = true;
        } else if (matches(arg, "lowercase", "lower", "low")) {
           lowerCase = true;
        } else if (matches(arg, "beginning", "begins", "begin", "starting", "starts", "start", "b", "s")) {
           beginsWith = true;
        } else if (matches(arg, "contains", "contain", "middle", "mid", "c", "m")) {
           contains = true;
        } else if (matches(arg, "ending", "ends", "end", "e")) {
            endsWith = true;
        } else if (matches(arg, "console", "con")) {
            runningOnConsole = true;
        } else if (matches(arg, "window", "gui", "g")) {
            runningOnConsole = false;
        } else if (matches(arg, "default", "twl06a", "def", "d")) {
            wordFile = WORD_FILE_DEFAULT;
        } else if (matches(arg, "original", "twl06", "orig", "o")) {
            wordFile = WORD_FILE_ORIGINAL;
        } else if (matches(arg, "antique", "enable1", "enable", "en")) {
            wordFile = WORD_FILE_ANTIQUE;
        } else {
            error("Unknown option \"" + prefix + arg + "\" ignored!");
        }
    }

    /**
     * Process all command line arguments, calling {@link #processOption} for options and
     * saving the rest in the input list.
     * @param args The command line arguments.
     * @param nonOptions The list to fill up with all the non-option arguments.
     * @return The total length of the non-option strings.
     */
    private static int processCommandLine(final String[] args, final List<String> nonOptions) {
        int totalInputSize = 0;

        // Process all options first before regular word/letter arguments
        for (String arg : args) {
            if (arg.startsWith("--")) {
                processOption("--", arg.substring(2));
            } else if (arg.startsWith("-")) {
                processOption("-", arg.substring(1));
            } else if (ON_WINDOWS && arg.startsWith("/")) {
                processOption("/", arg.substring(1));
            } else if (beginsWith) {
                beginningValue = Optional.of(arg);
                beginsWith = false;
            } else if (contains) {
                containsValue = Optional.of(arg);
                contains = false;
            } else if (endsWith) {
                endingValue = Optional.of(arg);
                endsWith = false;
            } else {
                nonOptions.add(arg);
                totalInputSize = arg.length();
            }
        }

        // If there were flags that never got their arguments, then signal an error here
        if (beginsWith) {
            error("The \"--begins\" option was specified without a value!");
        }
        if (contains) {
            error("The \"--contains\" option was specified without a value!");
        }
        if (endsWith) {
            error("The \"--ends\" option was specified without a value!");
        }

        // Set other values that depend on the options specified.
        alphaStart = lowerCase ? 'a' : 'A';

        return totalInputSize;
    }

    /**
     * Read in the dictionary file, and populate the basic and additional word
     * sets for later use.
     * Allows blank lines and comments ("# form" or "// form" or "-- form").
     *
     * @param wordFile Name of the dictionary word file.
     * @param wordSet The basic word set to populate.
     * @param addlSet The additional word set.
     */
    private static void readDictionary(final String wordFile, final Set<String> wordSet,
        final Set<String> addlSet) {
        long startTime = System.nanoTime();
        InputStream is = WordFind.class.getResourceAsStream(wordFile);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Just for safety's sake, trim excess blanks
                line = line.trim();
                if (line.length() == 0
                 || line.startsWith("#")
                 || line.startsWith("//")
                 || line.startsWith("--")) {
                    continue;
                }
                if (line.length() == 1) {
                    info("One letter word \"" + line + "\"");
                    continue;
                }
                // Assume the input word file is all in UPPER case...
                String word = lowerCase ? line.toLowerCase() : line;
                if (word.endsWith("?")) {
                    addlSet.add(word.substring(0, word.length() - 1));
                } else {
                    wordSet.add(word);
                }
            }
        } catch (IOException ioe) {
            error("Problem reading the \"" + wordFile + "\" file: "
                + ioe.getClass().getSimpleName() + ": " + ioe.getMessage());
        }
        long endTime = System.nanoTime();
        float secs = (float)(endTime - startTime) / 1.0e9f;
        String message = String.format("Dictionary \"%1$s\" has %2$,d basic and %3$,d additional words (%4$5.3f secs).",
            wordFile, wordSet.size(), addlSet.size(), secs);
        info(message);
    }

    /**
     * The main program, invoked from the console.
     * @param args The parsed command line arguments.
     */
    public static void main(final String[] args) {
        List<String> argWords = new ArrayList<>(args.length);
        int totalInputSize = processCommandLine(args, argWords);

        // BIG switch here for GUI vs console operation
        if (!runningOnConsole) {
            DesktopApplicationContext.main(WordFind.class, argWords.toArray(new String[0]));
        } else {
            // Next read in the preferred dictionary/word file
            readDictionary(wordFile, words, additionalWords);

            StringBuilder letters = new StringBuilder(totalInputSize);

            // If we're running in "word" mode, then lookup each of the words given on the command line
            // to see if they are valid.  But for one letter "words" just buffer them and process
            // as a set of letters.
            for (String arg : argWords) {
                String word = lowerCase ? arg.toLowerCase() : arg.toUpperCase();
                // For one-letter case (--letter or word has length one), buffer to the end
                if (letter || word.length() == 1) {
                    letters.append(word);
                } else {
                    // Lookup each word on the command line to see if it is valid.
                    if (words.contains(word) || (findInAdditional && additionalWords.contains(word))) {
                        info(arg + " is valid.");
                    } else {
                        error(arg + " NOT VALID!");
                    }
                }
            }

            long startTime = System.nanoTime();

            // Okay, we might have a set of letters to process (the "--letters" mode).
            int n = letters.length();
            int cn = 0;
            if (n > 0) {
		// See if the letters as entered are a valid word first
		String inputWord = letters.toString();
		if (words.contains(inputWord) || (findInAdditional && additionalWords.contains(inputWord))) {
		    info(inputWord + " is valid.");
		}

                StringBuilder sb = new StringBuilder();
                sb.append("Valid words for: " + letters.toString() + (findInAdditional ? " (including additional)" : ""));
                if (beginningValue.isPresent()) {
                    beginningValue = beginningValue.map(caseMapper);
                    String beginsString = beginningValue.get();
                    sb.append(" beginning with \"" + beginsString + "\"");
                }
                if (containsValue.isPresent()) {
                    containsValue = containsValue.map(caseMapper);
                    String containsString = containsValue.get();
                    sb.append(" containing \"" + containsString + "\"");
                    cn = containsString.length();
                }
                if (endingValue.isPresent()) {
                    endingValue = endingValue.map(caseMapper);
                    String endsString = endingValue.get();
                    sb.append(" ending with \"" + endsString + "\"");
                }
                heading(sb.toString());

                // Shuffle the blanks to the end to ensure that words made either with or without
                // blanks will find the "without" version first.
                StringBuilder letterSubset = new StringBuilder(n);
                int numberOfBlanks = 0;
                for (int i = 0; i < n; i++) {
                    char ch = letters.charAt(i);
                    if (ch == ' ' || ch == '?')
                        numberOfBlanks++;
                    else
                        letterSubset.append(ch);
                }
                if (numberOfBlanks > 0) {
                    letters.setLength(0);
                    letters.append(letterSubset);
                    letters.append(CharUtil.makeStringOfChars('?', numberOfBlanks));
                }

                @SuppressWarnings("unchecked")
                Set<String>[] validWords = new Set[n + cn];
                for (int i = 0; i < n + cn; i++) {
                    validWords[i] = new TreeSet<>(valueComparator);
                }
		// This is the set used to avoid duplicate permutations
		Set<String> permutationSet = new TreeSet<>();

                /*
                 * Find all subsets of the given set of letters by running through the values
                 * of all the binary numbers from 0 to 2^n - 1 where n is the number of letters.
                 * Algorithm taken from https://www.geeksforgeeks.org/finding-all-subsets-of-a-given-set-in-java/
		 * (assumes n <= 32)
                 */
		int twoPowN = 1 << n;
                for (int i = 0; i < twoPowN; i++) {
                    letterSubset.setLength(0);
                    for (int j = 0, bitMask = 1; j < n; j++, bitMask <<= 1) {
                        // bitMask is a number with jth bit set to one,
                        // so when we 'and' that with the subset number 
                        // we get which letters are present in ths subset
			// and which are not.
                        if ((i & bitMask) != 0) {
                            letterSubset.append(letters.charAt(j));
                        }
                    }
                    if (!findValidPermutations("", letterSubset.toString(), permutationSet, validWords))
                        break;
                }

                int numberOfWordsFound = 0;

                for (int index = n + cn - 1; index >= 0; index--) {
                    Set<String> wordSet = validWords[index];
                    if (wordSet.size() > 0) {
                        int columnWidth = index + 5;
                        section(String.valueOf(index + 1) + " letter words (" + wordSet.size() + "):");
                        int lineLength = 0;
                        for (String word : wordSet) {
                            if (lineLength + columnWidth + 6 > maxLineLength) {
                                System.out.println();
                                lineLength = 0;
                            }
                            /*
                             * This is tricky because we need to highlight blank substitutions here,
                             * but also right-justify within the column width, which cannot count
                             * the escape sequences as part of the word length.
                             */
                            String lettersOnly = getLettersOnly(word);
                            int excessSpace = columnWidth - lettersOnly.length();
                            String leftPadding = excessSpace == 0 ? "" : CharUtil.makeStringOfChars(' ', excessSpace); 
                            String highlightedWord = highlightWord(word);
                            System.out.format(WORD_FORMAT, leftPadding, highlightedWord, addLetterValues(word));
                            lineLength += columnWidth + 6;
                            numberOfWordsFound++;
                        }
                        System.out.println();
                    }
                }

                if (numberOfWordsFound == 0)
                    error("Unable to find any valid words!");

                long endTime = System.nanoTime();
                float secs = (float)(endTime - startTime) / 1.0e9f;
                int wordsChecked = permutationSet.size() * (containsValue.isPresent() ? n : 1);
                String message = String.format("(Lookup time was %1$5.3f seconds; %2$,d words tested)",
                        secs, wordsChecked);
                info(message);
            }
        }
    }

    public WordFind() {
        System.setProperty("org.apache.pivot.wtk.skin.terra.location", "/TerraTheme_old.json");
    }

}

