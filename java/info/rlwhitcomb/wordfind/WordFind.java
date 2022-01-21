/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2022 Roger L. Whitcomb.
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
 *      "Word Finder" program; runs in either command line or GUI mode (using
 *      the Apache Pivot GUI framework).
 *
 *  Change History:
 *      22-Sep-2020 (rlwhitcomb)
 *          Initial checkin to GitHub. Allow comments and blank lines in the
 *          master word file. The GUI is not yet implemented.
 *      08-Oct-2020 (rlwhitcomb)
 *          Allow choice of word files.
 *      08-Oct-2020 (rlwhitcomb)
 *          Small changes to support GUI mode. Use CharUtil methods.
 *          Fix bug that was missing a bunch of words when using blanks.
 *          Also highlight and correct point values with blanks.
 *      16-Oct-2020 (rlwhitcomb)
 *          Display the number of permutations at the end.
 *      22-Dec-2020 (rlwhitcomb)
 *          Use a different API to read the txt files from the .jar file.
 *      22-Dec-2020 (rlwhitcomb)
 *          Fix obsolete Javadoc constructs and other errors. Abandon the
 *          searching if too many are needed (temp fix).
 *      26-Dec-2020 (rlwhitcomb)
 *          Use a better algorithm for "contains".
 *      25-Jan-2021 (rlwhitcomb)
 *          Implement "-version" command.
 *      04-Feb-2021 (rlwhitcomb)
 *          Highlight the "contained" strings in the final output.
 *          Display total words at the end.
 *          Implement "-nocolor" option.
 *      29-Mar-2021 (rlwhitcomb)
 *          Move the theme file.
 *      29-Mar-2021 (rlwhitcomb)
 *          More actual implementation of the GUI.
 *      26-Apr-2021 (rlwhitcomb)
 *          Fix index out of bounds exception. Add rudimentary help.
 *          Tweak some of the command line options.
 *          Allow "WORDFIND_OPTIONS" in the environment.
 *          Add options to show timings or not.
 *      07-May-2021 (rlwhitcomb)
 *          Add option for max iterations and option with code for max time.
 *          Big change to add a REPL mode for repeated use without re-reading
 *          the dictionary.
 *          Options for min word size to report and max values of each size.
 *      07-May-2021 (rlwhitcomb)
 *          Options for colors based on window background color.
 *      13-May-2021 (rlwhitcomb)
 *          Tweak dark background color.
 *      20-May-2021 (rlwhitcomb)
 *          Fix output coloring.
 *      07-Jul-2021 (rlwhitcomb)
 *          Determine console width from Environment.
 *      23-Aug-2021 (rlwhitcomb)
 *          In REPL mode, don't automatically clear the letters each time.
 *          Tweak some colors.
 *      24-Aug-2021 (rlwhitcomb)
 *          Allow '.' as the wildcard also; allow '/' to signal an option even
 *          on non-Windows platforms, since we never have paths on the command
 *          line (which is the principal source of confusion for *nix).
 *      27-Aug-2021 (rlwhitcomb)
 *          Allow some more directives in REPL mode.
 *          Move all the text to resources file.
 *          Add "-maxlinelength" option for automated testing.
 *	26-Sep-2021 (rlwhitcomb)
 *	    #23 Fix inconsistent options.
 *	04-Oct-2021 (rlwhitcomb)
 *	    Tweak colors.
 *	21-Jan-2022 (rlwhitcomb)
 *	    #217: Use new Options method to process environment methods.
 */
package info.rlwhitcomb.wordfind;

import java.awt.Dimension;
import java.awt.Font;
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

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.text.CharSpan;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.Style;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.TextPane;
import org.apache.pivot.wtk.Window;

import info.rlwhitcomb.util.CharUtil;
import info.rlwhitcomb.util.ConsoleColor;
import static info.rlwhitcomb.util.ConsoleColor.Code.*;
import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.ExceptionUtil;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.Options;


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

    /**
     * The default number of maximum iterations looking for valid words.
     */
    private static final long DEFAULT_MAX_ITERATIONS = 10_000_000L;
    /**
     * The default amount of time (seconds) to spend iterating.
     */
    private static final float DEFAULT_MAX_TIME = 10.0f;

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
    private static int maxLineLength;
    /** Whether to use colors for the output. */
    private static boolean colored = true;
    /** Whether to report timings or not. */
    private static boolean timings = true;
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
    /** Whether next value is supposed to be a max size value. */
    private static boolean needMaxSize = false;
    /** Maximum number of iterations to perform. */
    private static long maxIterations = DEFAULT_MAX_ITERATIONS;
    /** Whether next value is supposed to be a max time value. */
    private static boolean needMaxTime = false;
    /** Maximum number of seconds to spend looking for values. */
    private static float maxSeconds = DEFAULT_MAX_TIME;
    /** Whether next value is supposed to be a min word size value. */
    private static boolean needMinWordSize = false;
    /** Minimum word length to report on (normally everything: {@code <= 0}). */
    private static int minWordSizeToReport = 0;
    /** Whether or not the next value is supposed to be the max number of words. */
    private static boolean needMaxNumber = false;
    /** Maximum numbers of words of each size to report (normally all: {@code <= 0}). */
    private static int maxNumberOfWords = 0;
    /** Whether or not the next value is supposed to be the max line length. */
    private static boolean needMaxLineLength = false;
    /** The format string for final output of the words. */
    private static final String WORD_FORMAT = "%1$s%2$s " + BLACK_BRIGHT + "(%3$3d)" + RESET;
    /** Continuation. */
    private static final String DOTS = " " + BLACK_BRIGHT + "..." + RESET;
    /** Heading color. */
    private static ConsoleColor.Code headingColor = null;
    /** Info message color. */
    private static ConsoleColor.Code infoColor = null;
    /** Error message color. */
    private static ConsoleColor.Code errorColor = null;
    /** Wildcard highlight color. */
    private static ConsoleColor.Code wildcardColor = null;
    /** Containing highlight color. */
    private static ConsoleColor.Code containsColor = null;

    /** The current start time of the calculation. */
    private static long startTime;

    /** The display object for the GUI. */
    private Display display;

    @BXML private Window mainWindow;
    @BXML private TextInput lettersInput;
    @BXML private TextInput containsInput;
    @BXML private TextInput startsWithInput;
    @BXML private TextInput endsWithInput;
    @BXML private PushButton clearLettersButton;
    @BXML private PushButton clearContainsButton;
    @BXML private PushButton clearStartsWithButton;
    @BXML private PushButton clearEndsWithButton;
    @BXML private TextPane outputArea;

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
     * blanks which are delimited with "_", and the "contained" markers ("-").
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
            else if (ch == '-')
                continue;    // skip the "contained" marker
            else
                pointValue += POINT_VALUES[ch - alphaStart];
        }
        return pointValue;
    }

    /**
     * @return The bare letter values without the {@code _X_} markers for blanks, or the
     * {@code -X-} markers for the "contained" values.
     * @param delimitedWord The input with "_" or "-" markers included.
     */
    private static String getLettersOnly(final String delimitedWord) {
        return delimitedWord.replace("_", "").replace("-", "");
    }

    /**
     * Highlight a word making special emphasis on the blank substitutions and the
     * "contained" values.
     * @param adornedWord The word to highlight with the "_" and "-" markers.
     * @return The input with the markers replaced with the actual color codes.
     */
    private static String highlightWord(final String adornedWord) {
        int wordLen = adornedWord.length();
        StringBuilder buf = new StringBuilder(wordLen);
        // Note: we assume that the markers are mutually exclusive and not nested
        boolean insideMarkers = false;
        for (int i = 0; i < wordLen; i++) {
            char ch = adornedWord.charAt(i);
            if (ch == '_' || ch == '-') {
                if (insideMarkers) {
                    buf.append(RESET);
                    insideMarkers = false;
                } else {
                    buf.append(ch == '_' ? wildcardColor : containsColor);
                    insideMarkers = true;
                }
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
            throw new Intl.IllegalArgumentException("wordfind#errNegativeIndex");
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
            else if (ch == '-')
                charPos += 2;
            else
                charPos++;
        }

        return buf.insert(charPos, str);
    }

    private static void setColors(final boolean light) {
        if (light) {
            headingColor = GREEN_UNDERLINED;
            infoColor = CYAN_BOLD;
            errorColor = RED_UNDERLINED;
            wildcardColor = RED_BRIGHT;
            containsColor = GREEN_BOLD;
        } else {
            headingColor = GREEN_UNDERLINED_BRIGHT;
            infoColor = CYAN;
            errorColor = RED_UNDERLINED_BRIGHT;
            wildcardColor = RED_BOLD;
            containsColor = YELLOW_BOLD;
        }
    }

    private static final String adorn(final String unadorned) {
        return String.format("-%1$s-", unadorned);
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

            if (maxIterations > 0L && permutationSet.size() >= maxIterations) {
                Intl.errFormat("wordfind#maxIterationsReached", maxIterations);
                return false;
            }
            long currentTime = System.nanoTime();
            float secs = (float)(currentTime - startTime) / 1.0e9f;
            if (maxSeconds > 0.0f && secs >= maxSeconds) {
                Intl.errFormat("wordfind#maxTimeReached", secs);
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
                    String contains        = containsValue.get();
                    String containsAdorned = adorn(contains);
                    insert(bufAdorned,   containsIndex, containsAdorned);
                    insert(bufUnadorned, containsIndex, contains);
                }
                beginningValue.ifPresent(v -> { insert(bufAdorned, 0, adorn(v));  insert(bufUnadorned, 0, v);  });
                endingValue.ifPresent   (v -> { insert(bufAdorned, -1, adorn(v)); insert(bufUnadorned, -1, v); });

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

            // Special case handling of the delimited substitutions
            ch = str.charAt(i);
            if (ch == '_') {
                restOfString = str.substring(0, i) + str.substring(i + 3);
            } else if (ch == '-') {
                restOfString = str.substring(0, i) + str.substring(i + 2);
            } else {
                restOfString = str.substring(0, i) + str.substring(i + 1);
            }

            // Deal with wildcard letter (blank)
            if (ch == ' ' || ch == '?' || ch == '.') {
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
                else if (ch == '-')
                    newPrefix = prefix + str.substring(i, i + 2);
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
        this.display = display;

        try {
            BXMLSerializer serializer = new BXMLSerializer();
            serializer.readObject(WordFind.class, "wordfind.bxml");
            serializer.bind(this);

            mainWindow.open(display);
            lettersInput.requestFocus();
        } catch (Exception ex) {
            Intl.errFormat("wordfind#exception", ExceptionUtil.toString(ex), ex.getStackTrace()[0].toString());
            ex.printStackTrace();
        }
    }

    @Override
    public boolean shutdown(final boolean optional) {
        return false;
    }

    private static String quote(final String input) {
        return String.format("\"%1$s\"", input);
    }

    /**
     * Print an error message to {@link System#err} and highlight in red when running
     * in the console, else send to the GUI error message text.
     *
     * @param messageKey The message key for the error to display.
     * @param args       Any optional formatting arguments for the message.
     */
    private static void error(final String messageKey, final Object... args) {
        String message = Intl.formatString(messageKey, args);

        if (runningOnConsole) {
            if (colored)
                System.err.println(ConsoleColor.color(errorColor + message + RESET));
            else
                System.err.println(message);
        } else {
            // TODO: implement; whether to have separate error/info text boxes or the same with diff colors?
        }
    }

    private static void errorMissingValue(final String valueName) {
        error("wordfind#errMissingValue", valueName);
    }

    private static void output(final String coloredMessage) {
        System.out.print(ConsoleColor.color(coloredMessage, colored));
    }

    private static void outputln(final String coloredMessage) {
        System.out.println(ConsoleColor.color(coloredMessage, colored));
    }

    /**
     * Print an informational message to {@link System#out} and highlight in green when running
     * in the console, else send to the GUI information message text.
     *
     * @param messageKey The key for the informational message to display.
     * @param args       Optional format arguments for the message.
     */
    private static void info(final String messageKey, final Object... args) {
        String message = Intl.formatString(messageKey, args);

        if (runningOnConsole) {
            outputln(infoColor + message + RESET);
        } else {
            // TODO: implement; whether to have separate error/info text boxes or the same with diff colors?
        }
    }

    /**
     * Print a heading message to {@link System#out} and color it bold cyan.
     * @param message The heading message to display.
     */
    private static void heading(final String message) {
        outputln(headingColor + message + RESET);
    }

    /**
     * Print a section message to {@link System#out} and make it bold (black).
     * @param message The section message to display.
     */
    private static void section(final String message) {
        output(BLACK_BOLD_BRIGHT + message + RESET);
    }

    /**
     * Print one of two flavors of help: one from the command line, and the other one
     * during REPL mode, which has more detail.
     * @param intro Display the opening help message.
     */
    private static void displayHelp(final boolean intro) {
        Intl.printHelp(intro ? "wordfind#intro" : "wordfind#repl", colored);
        if (intro) {
            if (timings)
                Intl.printHelp("wordfind#repl", colored);
            Intl.printHelp("wordfind#addl", colored);
        }
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
     * @param ignoreOptions Whether or not to ignore certain options, like console/GUI mode, and word file
     * choices, which can only be processed once at startup time.
     */
    private static void processOption(final String prefix, final String arg, final boolean ignoreOptions) {
        boolean ignored = false;

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
            if (ignoreOptions)
                ignored = true;
            else
                lowerCase = true;
        } else if (matches(arg, "uppercase", "upper", "up")) {
            if (ignoreOptions)
                ignored = true;
            else
                lowerCase = false;
        } else if (matches(arg, "beginning", "begins", "begin", "starting", "starts", "start", "b", "s")) {
            beginsWith = true;
        } else if (matches(arg, "contains", "contain", "middle", "mid", "c", "m")) {
            contains = true;
        } else if (matches(arg, "ending", "ends", "end", "e")) {
            endsWith = true;
        } else if (matches(arg, "colored", "colors", "color", "col")) {
            colored = true;
        } else if (matches(arg, "notcolored", "nocolors", "nocolor", "nocol", "noc")) {
            colored = false;
        } else if (matches(arg, "lightbackground", "lightback", "lightbg", "light")) {
            setColors(true);
        } else if (matches(arg, "darkbackground", "darkback", "darkbg", "dark")) {
            setColors(false);
        } else if (matches(arg, "notimings", "notiming", "quiet", "not", "q")) {
            timings = false;
        } else if (matches(arg, "timings", "timing", "verbose", "time", "t", "v")) {
            timings = true;
        } else if (matches(arg, "maxsize", "size", "max")) {
            needMaxSize = true;
        } else if (matches(arg, "maxtime", "maxseconds", "maxsecs", "maxt", "seconds", "secs")) {
            needMaxTime = true;
        } else if (matches(arg, "minwordsize", "minword", "minsize", "min")) {
            needMinWordSize = true;
        } else if (matches(arg, "maxnumberwords", "maxnumber", "maxwords")) {
            needMaxNumber = true;
        } else if (matches(arg, "maxlinelength", "maxwidth", "width", "wid")) {
            needMaxLineLength = true;
        } else if (matches(arg, "console", "con")) {
            if (ignoreOptions)
                ignored = true;
            else
                runningOnConsole = true;
        } else if (matches(arg, "window", "win", "gui", "g")) {
            if (ignoreOptions)
                ignored = true;
            else
                runningOnConsole = false;
        } else if (matches(arg, "default", "twl06a", "def", "d")) {
            if (ignoreOptions)
                ignored = true;
            else
                wordFile = WORD_FILE_DEFAULT;
        } else if (matches(arg, "original", "twl06", "orig", "o")) {
            if (ignoreOptions)
                ignored = true;
            else
                wordFile = WORD_FILE_ORIGINAL;
        } else if (matches(arg, "antique", "enable1", "enable", "ant", "en")) {
            if (ignoreOptions)
                ignored = true;
            else
                wordFile = WORD_FILE_ANTIQUE;
        } else if (matches(arg, "version", "vers", "ver")) {
            if (ignoreOptions)
                ignored = true;
            else {
                Environment.printProgramInfo(50, colored);
                System.exit(0);
            }
        } else if (matches(arg, "help", "h", "?")) {
            if (ignoreOptions)
                ignored = true;
            else {
                displayHelp(true);
                System.exit(0);
            }
        } else {
            error("wordfind#errUnknownOption", quote(prefix + arg));
        }
        if (ignored) {
            error("wordfind#errIgnoredOption", quote(prefix + arg));
        }
    }

    /**
     * Process all command line arguments, calling {@link #processOption} for options and
     * saving the rest in the input list.
     * @param args The command line arguments.
     * @param nonOptions The list to fill up with all the non-option arguments (can be
     * {@code null} to not collect any).
     * @param ignoreOptions For REPL mode, don't process some options on input lines.
     * @return The total length of the non-option strings.
     */
    private static int processCommandLine(final String[] args, final List<String> nonOptions,
            final boolean ignoreOptions) {
        int totalInputSize = 0;

        // Reset all the variables we can set over and over
        beginsWith = false;
        beginningValue = Optional.empty();
        contains = false;
        containsValue = Optional.empty();
        endsWith = false;
        endingValue = Optional.empty();
        needMaxSize = false;
        needMaxTime = false;
        needMinWordSize = false;
        needMaxNumber = false;
        needMaxLineLength = false;

        // Process all options first before regular word/letter arguments
        for (String arg : args) {
            if (arg.startsWith("--")) {
                processOption("--", arg.substring(2), ignoreOptions);
            } else if (arg.startsWith("-")) {
                processOption("-", arg.substring(1), ignoreOptions);
            } else if (arg.startsWith("/")) {
                processOption("/", arg.substring(1), ignoreOptions);
            } else if (beginsWith) {
                beginningValue = Optional.of(arg);
                beginsWith = false;
            } else if (contains) {
                containsValue = Optional.of(arg);
                contains = false;
            } else if (endsWith) {
                endingValue = Optional.of(arg);
                endsWith = false;
            } else if (needMaxSize) {
                try {
                    maxIterations = Long.parseLong(arg);
                } catch (NumberFormatException nfe) {
                    error("wordfind#errBadOptionValue", "maxsize", arg);
                }
                needMaxSize = false;
            } else if (needMaxTime) {
                try {
                    maxSeconds = Float.parseFloat(arg);
                } catch (NumberFormatException nfe) {
                    error("wordfind#errBadOptionValue", "maxtime", arg);
                }
                needMaxTime = false;
            } else if (needMinWordSize) {
                try {
                    minWordSizeToReport = Integer.parseInt(arg);
                } catch (NumberFormatException nfe) {
                    error("wordfind#errBadOptionValue", "minwordsize", arg);
                }
                needMinWordSize = false;
            } else if (needMaxNumber) {
                try {
                    maxNumberOfWords = Integer.parseInt(arg);
                } catch (NumberFormatException nfe) {
                    error("wordfind#errBadOptionValue", "maxnumber", arg);
                }
                needMaxNumber = false;
            } else if (needMaxLineLength) {
                try {
                    maxLineLength = Integer.parseInt(arg);
                } catch (NumberFormatException nfe) {
                    error("wordfind#errBadOptionValue", "maxlinelength", arg);
                }
                needMaxLineLength = false;
            } else {
                if (nonOptions != null) {
                    if (totalInputSize == 0)
                        nonOptions.clear();
                    nonOptions.add(arg);
                    totalInputSize += arg.length();
                }
            }
        }

        // If there were flags that never got their arguments, then signal an error here
        if (beginsWith)
            errorMissingValue("begins");
        if (contains)
            errorMissingValue("contains");
        if (endsWith)
            errorMissingValue("ends");
        if (needMaxSize)
            errorMissingValue("maxsize");
        if (needMaxTime)
            errorMissingValue("maxtime");
        if (needMinWordSize)
            errorMissingValue("minwordsize");
        if (needMaxNumber)
            errorMissingValue("maxnumber");
        if (needMaxLineLength)
            errorMissingValue("maxlinelength");

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
                    info("wordfind#infoOneLetterWord", quote(line));
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
            error("wordfind#errReadingWordFile", quote(wordFile), ExceptionUtil.toString(ioe));
        }
        long endTime = System.nanoTime();

        if (timings) {
            float secs = (float)(endTime - startTime) / 1.0e9f;
            info("wordfind#infoDictionary", quote(wordFile), wordSet.size(), addlSet.size(), secs);
        }
    }

    private static void process(final List<String> argWords, final int totalInputSize) {
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
                    info("wordfind#infoArgValid", arg);
                } else {
                    error("wordfind#errArgNotValid", arg);
                }
            }
        }

        startTime = System.nanoTime();

        // Okay, we might have a set of letters to process (the "--letters" mode).
        int n = letters.length();
        int cn = 0;
        if (n > 0) {
            // See if the letters as entered are a valid word first
            String inputWord = letters.toString();
            if (words.contains(inputWord) || (findInAdditional && additionalWords.contains(inputWord))) {
                info("wordfind#infoArgValid", inputWord);
            }

            StringBuilder sb = new StringBuilder();
            sb.append(Intl.formatString("wordfind#validWords", quote(letters.toString()),
                (findInAdditional ? Intl.getString("wordfind#additional") : "")));
            if (beginningValue.isPresent()) {
                beginningValue = beginningValue.map(caseMapper);
                String beginsString = beginningValue.get();
                sb.append(Intl.getString("wordfind#beginningWith")).append(quote(beginsString));
                cn = beginsString.length();
            }
            if (containsValue.isPresent()) {
                containsValue = containsValue.map(caseMapper);
                String containsString = containsValue.get();
                sb.append(Intl.getString("wordfind#containing")).append(quote(containsString));
                cn = containsString.length();
            }
            if (endingValue.isPresent()) {
                endingValue = endingValue.map(caseMapper);
                String endsString = endingValue.get();
                sb.append(Intl.getString("wordfind#endingWith")).append(quote(endsString));
                cn = endsString.length();
            }
            heading(sb.toString());

            // Shuffle the blanks to the end to ensure that words made either with or without
            // blanks will find the "without" version first.
            StringBuilder letterSubset = new StringBuilder(n);
            int numberOfBlanks = 0;
            for (int i = 0; i < n; i++) {
                char ch = letters.charAt(i);
                if (ch == ' ' || ch == '?' || ch == '.')
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
                int size = wordSet.size();
                if (size > 0) {
                    numberOfWordsFound += size;
                    section(Intl.formatString("wordfind#section", (index + 1), size));

                    if (minWordSizeToReport > 1 && (index + 1) < minWordSizeToReport) {
                        outputln(DOTS);
                        continue;
                    }
                    System.out.println();

                    int columnWidth = index + 5;
                    int lineLength = 0;
                    int wordsSoFarInSet = 0;

                    for (String word : wordSet) {
                        wordsSoFarInSet++;
                        if (maxNumberOfWords > 0 && wordsSoFarInSet > maxNumberOfWords) {
                            outputln(DOTS);
                            break;
                        }

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
                        int value = addLetterValues(word);
                        output(String.format(WORD_FORMAT, leftPadding, highlightWord(word), value));
                        lineLength += columnWidth + 6;
                    }
                    System.out.println();
                }
            }

            if (numberOfWordsFound == 0)
                error("wordfind#errNoValidWords");

            long endTime = System.nanoTime();

            if (timings) {
                float secs = (float)(endTime - startTime) / 1.0e9f;
                int wordsChecked = permutationSet.size() * (containsValue.isPresent() ? n : 1);
                info("wordfind#infoLookup", secs, numberOfWordsFound, wordsChecked);
            }
        }
    }

    private static void consoleMode(final List<String> argWords, final int totalInputSize) {
        boolean replMode = false;

        if (argWords.isEmpty() || totalInputSize == 0)
            replMode = true;
        else if (argWords.size() == 1 && argWords.get(0).equals("@"))
            replMode = true;

        if (replMode) {
            Console console = System.console();
            if (console == null) {
                process(argWords, totalInputSize);
                return;
            }

            String line;
            String prompt = ConsoleColor.color("<Bk!>> <>");
        replLoop:
            while ((line = console.readLine(prompt)) != null) {
                if (line.isEmpty())
                    continue replLoop;

                String cmd = line.trim().toLowerCase();
                switch (cmd) {
                    case ":quit":
                    case ":exit":
                    case ":q":
                    case ":x":
                        break replLoop;
                    case ":help":
                    case ":h":
                    case ":?":
                        displayHelp(false);
                        continue replLoop;
                    case ":version":
                    case ":vers":
                    case ":ver":
                    case ":v":
                        Environment.printProgramInfo(50, colored);
                        continue replLoop;
                    default:
                        if (cmd.startsWith("#") || cmd.startsWith("!") || cmd.startsWith("//"))
                            continue replLoop;
                        break;
                }

                String[] args = CharUtil.parseCommandLine(line);
                int inputSize = processCommandLine(args, argWords, true);
                process(argWords, inputSize);
            }
            System.out.println();
        } else {
            process(argWords, totalInputSize);
        }
    }

    /**
     * The main program, invoked from the console.
     * @param args The parsed command line arguments.
     */
    public static void main(final String[] args) {
        Environment.setDesktopApp(true);
        Environment.loadProgramInfo(WordFind.class);
        Dimension dim = Environment.consoleSize();
        maxLineLength = dim.width;

        // Set default colors before options so there is a setting for error messages right away
        setColors(!ON_WINDOWS);

	Options.environmentOptions(WordFind.class, (options) -> {
            processCommandLine(options, null, false);
        });

        // Command line options override the defaults (if any)
        List<String> argWords = new ArrayList<>(args.length);
        int totalInputSize = processCommandLine(args, argWords, false);

        // Next read in the preferred dictionary/word file
        readDictionary(wordFile, words, additionalWords);

        // BIG switch here for GUI vs console operation
        if (!runningOnConsole) {
            DesktopApplicationContext.main(WordFind.class, argWords.toArray(new String[0]));
        } else {
            consoleMode(argWords, totalInputSize);
        }
    }

    public WordFind() {
        System.setProperty("org.apache.pivot.wtk.skin.terra.location", "/info/rlwhitcomb/TerraTheme_old.json");
    }

}

