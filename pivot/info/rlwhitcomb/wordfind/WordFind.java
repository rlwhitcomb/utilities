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
import static info.rlwhitcomb.util.ConsoleColor.*;

/**
 * A utility program to make sense out of random letter tiles
 * (such as for the "Scrabble" &tm; or "Word With Friends" &tm; games).
 */
public class WordFind implements Application {
    /**
     * Name of the master word file. Sourced from:
     * <a href="https://scrabutility.com/TWL06.txt">TWL06.txt</a>
     * with some additions and optional words as found using
     * Words With Friends.
     */
    private static final String WORD_FILE = "TWL06a.txt";
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
     * @return The total point values (from {@link #POINT_VALUES} for the given word.
     * @param word The word to check.
     */
    private static int addLetterValues(final String word) {
        int pointValue = 0;
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            pointValue += POINT_VALUES[ch - alphaStart];
        }
        return pointValue;
    }

    /**
     * A comparator of letter values.
     */
    private static final Comparator<String> valueComparator = (s1, s2) -> {
        /* Only use the point values if the words are not the same letters. */
        if (s1.equals(s2)) {
            return 0;
        }
        int v1 = addLetterValues(s1);
        int v2 = addLetterValues(s2);
        /* If alpha sort is turned on OR the letter values are the same, sort alphabetically. */
        if (sortAlphabetically || v1 == v2) {
            return s1.compareTo(s2);
        }
        /* Point value sort is highest to lowest. */
        return v2 - v1;
    };

    /**
     * A mapping function to convert the case of words, depending on the command line option {@link #lowerCase}.
     */
    private static final Function<String, String> caseMapper = s -> lowerCase ? s.toLowerCase() : s.toUpperCase();

    /**
     * Find all the valid possible permutations of the input string (recursive call).
     * <p> Algorithm taken from: https://www.geeksforgeeks.org/print-all-permutations-of-a-string-in-java/
     * @param input The UPPERcase input string.
     * @param outputSoFar For the recursive call, the prefix we have already processed.
     */
    private static void findValidPermutations(final String input, final String outputSoFar, final Set<String>[] validWords) {
        if (input.isEmpty()) {
            final StringBuilder buf = new StringBuilder(outputSoFar);
            beginningValue.ifPresent(v -> buf.insert(0, v));
            endingValue.ifPresent(v -> buf.append(v));

            final String output = buf.toString();
            if (words.contains(output) || (findInAdditional && additionalWords.contains(output))) {
                int index = output.length() - 1;
                boolean matches = true;
                if (containsValue.isPresent()) {
                    matches = output.indexOf(containsValue.get()) >= 0;
                }
                if (matches) {
                    validWords[index].add(output);
                }
            }
            return;
        }

        boolean used[] = new boolean[26];
        Arrays.fill(used, false);
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            String restOfInput = input.substring(0, i) + input.substring(i + 1);
            // Deal with wildcard letter (blank)
            if (ch == ' ' || ch == '?') {
                for (int j = 0; j < 26; j++) {
                    if (!used[j]) {
                        char ch2 = (char) (alphaStart + j);
                        findValidPermutations(restOfInput, outputSoFar + ch2, validWords);
                        used[j] = true;
                    }
                }
            } else {
                if (!used[ch - alphaStart]) {
                    findValidPermutations(restOfInput, outputSoFar + ch, validWords);
                }
                used[ch - alphaStart] = true;
            }
        }
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
     * Print an error message to {@link System#err} and highlight in red.
     * @param message The error message to display.
     */
    private static void error(final String message) {
        System.err.println(RED_BOLD + message + RESET);
    }

    /**
     * Print an informational message to {@link System#out} and highlight in green.
     * @param message The informational message to display.
     */
    private static void info(final String message) {
        System.out.println(GREEN + message + RESET);
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
        for (String choice : choices) {
            if (arg.equalsIgnoreCase(choice)) {
                return true;
            }
        }
        return false;
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
        } else if (matches(arg, "points", "point", "default", "p")) {
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
        try {
            URL fileUrl = WordFind.class.getResource(wordFile);
            Path filePath = Paths.get(fileUrl.toURI());
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
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
        } catch (URISyntaxException use) {
            error("Unable to find the \"" + wordFile + "\" file: "
                + use.getClass().getSimpleName() + ": " + use.getMessage());
        }
        long endTime = System.nanoTime();
        float secs = (float)(endTime - startTime) / 1.0e9f;
        String message = String.format("Dictionary \"%1$s\" has %2$d basic and %3$d additional words (%4$5.3f secs).",
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

        // Next read in the dictionary/word file
        readDictionary(WORD_FILE, words, additionalWords);

        // BIG switch here for GUI vs console operation
        if (!runningOnConsole) {
            DesktopApplicationContext.main(WordFind.class, argWords.toArray(new String[0]));
        } else {
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
                    letters.append(containsString);
                    n += containsString.length();
                }
                if (endingValue.isPresent()) {
                    endingValue = endingValue.map(caseMapper);
                    String endsString = endingValue.get();
                    sb.append(" ending with \"" + endsString + "\"");
                }
                heading(sb.toString());

                StringBuilder buf = new StringBuilder(n);
                @SuppressWarnings("unchecked")
                Set<String>[] validWords = new Set[n];
                for (int i = 0; i < n; i++) {
                    validWords[i] = new TreeSet<>(valueComparator);
                }

                /*
                 * Find all subsets of the given set of letters by running through the values
                 * of all the binary numbers from 0 to 2^n - 1 where n is the number of letters.
                 * Algorithm taken from https://www.geeksforgeeks.org/finding-all-subsets-of-a-given-set-in-java/
                 */
                for (int i = 0; i < (1 << n); i++) {
                    buf.setLength(0);
                    for (int j = 0; j < n; j++) {
                        // (1<<j) is a number with jth bit 1
                        // so when we 'and' them with the subset 
                        // number we get which numbers are present
                        // in the subset and which are not.
                        if ((i & (1 << j)) != 0) {
                            buf.append(letters.charAt(j));
                        }
                    }
                    findValidPermutations(buf.toString(), "", validWords);
                }

                for (int index = n - 1; index >= 0; index--) {
                    Set<String> wordSet = validWords[index];
                    if (wordSet.size() > 0) {
                        int columnWidth = index + 5;
                        final String wordFormat = String.format("%%%1$ds " + BLACK_BRIGHT + "(%%2$3d)" + RESET, columnWidth);
                        section(String.valueOf(index + 1) + " letter words (" + wordSet.size() + "):");
                        int lineLength = 0;
                        for (String word : wordSet) {
                            if (lineLength + columnWidth + 6 > maxLineLength) {
                                System.out.println();
                                lineLength = 0;
                            }
                            System.out.format(wordFormat, word, addLetterValues(word));
                            lineLength += columnWidth + 6;
                        }
                        System.out.println();
                    }
                }

                long endTime = System.nanoTime();
                float secs = (float)(endTime - startTime) / 1.0e9f;
                String message = String.format("(Lookup time was %1$5.3f seconds)", secs);
                info(message);
            }
        }
    }

}

