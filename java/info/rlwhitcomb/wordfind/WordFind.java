/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2023 Roger L. Whitcomb.
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
 *      26-Sep-2021 (rlwhitcomb)
 *          #23 Fix inconsistent options.
 *      04-Oct-2021 (rlwhitcomb)
 *          Tweak colors.
 *      21-Jan-2022 (rlwhitcomb)
 *          #217: Use new Options method to process environment methods.
 *      25-Jan-2022 (rlwhitcomb)
 *          If initial letters are playable as-is, not only announce, but display points.
 *      12-Apr-2022 (rlwhitcomb)
 *          #269: New method to load main program info (in Environment).
 *      18-Apr-2022 (rlwhitcomb)
 *          #270: Make this automatic now.
 *      09-Jul-2022 (rlwhitcomb)
 *          #393: Cleanup imports.
 *      15-Jul-2022 (rlwhitcomb)
 *          #411: Move dictionary handling out to separate class.
 *      27-Jul-2022 (rlwhitcomb)
 *          REPL command and command-line option to display dictionary statistics.
 *      04-Sep-2022 (rlwhitcomb)
 *          #29: Test code for the new dictionary "validWords" lookup.
 *      06-Sep-2022 (rlwhitcomb)
 *          #29: Switch to new lookup implemented in Dictionary class; remove old code;
 *          remove unnecessary options and errors.
 *      09-Sep-2022 (rlwhitcomb)
 *          #478: Implement patterns for "-contains" value.
 *      15-Sep-2022 (rlwhitcomb)
 *          #479: Allow ":option" for options in REPL mode.
 *      01-Dec-2022 (rlwhitcomb)
 *          #571: Fix buffer index exception during "mark()". Report the initial word being
 *          valid only after the heading is displayed.
 *      01-Jan-2023 (rlwhitcomb)
 *          #224: Add dictionary lookup.
 */
package info.rlwhitcomb.wordfind;

import info.rlwhitcomb.util.*;
import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.json.JSONSerializer;
import org.apache.pivot.web.*;
import org.apache.pivot.wtk.Window;
import org.apache.pivot.wtk.*;

import java.awt.Dimension;
import java.awt.Font;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static info.rlwhitcomb.util.ConsoleColor.Code.*;


/**
 * A utility program to make sense out of random letter tiles
 * (such as for the "Scrabble" &trade; or "Word With Friends" &trade; games).
 */
public class WordFind implements Application
{
    /** Default column width for output. */
    private static final int DEFAULT_COLUMN_WIDTH = 10;

    /** For option processing, whether we are running on a Windows OS. */
    private static final boolean ON_WINDOWS = Environment.isWindows();

    /** The "blank" marker character (surrounds the character the blank represents). */
    public static final char BLANK_MARKER = '_';

    /** The "contains" marker character (also used for "begins" and "ends"). */
    public static final char CONTAINS_MARKER = '-';

    /** A pattern to recognize (and replace) all "wild" characters. */
    private static final String WILD_PATTERN = "[ \\._\\?]";

    /** The dictionary API hostname. */
    private static final String DICTIONARY_HOST = "www.dictionaryapi.com";

    /** The dictionary API URL. */
    private static final String DICTIONARY_API = "/api/v3/references/collegiate/json/%1$s";

    /** The thesaurus API URL. */
    private static final String THESAURUS_API = "/api/v3/references/thesaurus/json/%1$s";


    /**
     * The dictionary where we keep all the valid words.
     */
    private static final Dictionary dictionary = new Dictionary();

    /** Should we look in the additional words list? */
    private static boolean findInAdditional = false;

    /** Big switch whether to run as a console app or a GUI app. */
    private static boolean runningOnConsole = true;

    /** Which word file to load (defaults to our custom one). */
    private static WordFile wordFile = WordFile.DEFAULT;

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

    /** The "contains" pattern (if any). */
    private static Pattern containsPattern;

    /** Whether next value is supposed to be an "Ends With" value. */
    private static boolean endsWith = false;

    /** Optional "Ends With" value. */
    private static Optional<String> endingValue = Optional.empty();

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

    /**
     * Sort alphabetically?
     */
    private static boolean sortAlphabetically = false;

    /**
     * Is dictionary lookup available (that is, are the dictionary API keys available)?
     */
    private static boolean lookupAvailable = false;
    /**
     * Dictionary API key.
     */
    private static String dictKey;
    /**
     * Thesaurus API key.
     */
    private static String thesKey;


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
     * A mapping function to convert the case of words, depending on the command line option {@link #lowerCase}.
     */
    private static final Function<String, String> caseMapper = s -> lowerCase ? s.toLowerCase() : s.toUpperCase();


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
            if (ch == BLANK_MARKER)
                i += 2; // skip "_X_" which is how a blank is marked
            else if (ch == CONTAINS_MARKER)
                continue;    // skip the "contained" markers (-XY-)
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
        return delimitedWord.replace(Character.toString(BLANK_MARKER), "")
                            .replace(Character.toString(CONTAINS_MARKER), "");
    }

    /**
     * Convert a "wild" input string ({@link #BLANK_MARKER} for wild chars) to a regex pattern.
     *
     * @param input The input "wild" string.
     * @return      A pattern derived from it.
     */
    private static Pattern convertToPattern(final String input) {
        StringBuilder buf = new StringBuilder(input.length());

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (ch == BLANK_MARKER)
                buf.append('.');
            else
                buf.append(ch);
        }

        return Pattern.compile(buf.toString());
    }

    /**
     * Highlight a word making special emphasis on the blank substitutions and the
     * "contained" values.
     * @param adornedWord The word to highlight with the {@link #BLANK_MARKER} and
     *                    {@link #CONTAINS_MARKER} markers.
     * @return The input with the markers replaced with the actual color codes.
     */
    private static String highlightWord(final String adornedWord) {
        int wordLen = adornedWord.length();
        StringBuilder buf = new StringBuilder(wordLen);

        // Note: we assume that the markers are mutually exclusive and not nested
        boolean insideMarkers = false;
        for (int i = 0; i < wordLen; i++) {
            char ch = adornedWord.charAt(i);
            if (ch == BLANK_MARKER || ch == CONTAINS_MARKER) {
                if (insideMarkers) {
                    buf.append(RESET);
                    insideMarkers = false;
                } else {
                    buf.append(ch == BLANK_MARKER ? wildcardColor : containsColor);
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
     * Mark around a string in the adorned word buffer, starting at the given index (into the
     * unadorned string), using the {@link #CONTAINS_MARKER}, or {@link #BLANK_MARKER}.
     *
     * @param buf   The buffer to edit.
     * @param index The position at which to insert the string (negative from end of string).
     * @param str   The string to mark around.
     */
    private static final void mark(final StringBuilder buf, final int index, final String str) {
        int len = str.length();
        int charPos;

        // "Adorn" the whole string so moving through it is easy
        for (int i = 0; i < buf.length(); i += 3) {
            char ch = buf.charAt(i);
            if (ch != CONTAINS_MARKER && ch != BLANK_MARKER) {
                buf.insert(i, '.');
                buf.insert(i + 2, '.');
            }
        }

        // Normalize a negative starting index to the beginning of the string, but do it based
        // on the fully adorned string, otherwise with some markers present we could get really confused
        if (index >= 0)
            charPos = index * 3;
        else
            charPos = buf.length() + (index * 3);

        // Now we march along the "contains" string in "str" and correctly mark the results
        // according to whether they match a letter ("contains") or a blank
        // Cases:
        // 1. letter in str and nothing in buf  -> mark buf as "contains"
        // 2. letter in str and blank in buf    -> leave as "blank"
        // 3. letter in str and contains in buf -> leave as "contains"
        // 4. blank in str and nothing in buf   -> leave as nothing
        // 5. blank in str and blank in buf     -> leave as "blank"
        // 6. blank in str and contains in buf  -> leave as "contains"

        for (int i = 0; i < len; i++) {
            char pat = str.charAt(i);
            char chr = buf.charAt(charPos);
            if (pat != BLANK_MARKER && chr == '.') {
                buf.setCharAt(charPos, CONTAINS_MARKER);
                buf.setCharAt(charPos + 2, CONTAINS_MARKER);
            }
            charPos += 3;
        }

        // Now go back and remove the "nothing" adornments, and any duplicated ones
        // (indicating a run of the same type)
        for (int i = 0; i < buf.length(); i++) {
            char ch = buf.charAt(i);
            if (ch == '.') {
                buf.deleteCharAt(i);
                buf.deleteCharAt(i + 1);
            }
            else if (ch == BLANK_MARKER || ch == CONTAINS_MARKER) {
                if (i < buf.length() - 1 && buf.charAt(i + 1) == ch) {
                    buf.deleteCharAt(i);
                    buf.deleteCharAt(i);
                    i--;
                }
            }
        }
    }

    /**
     * Setup the colors according to the light/dark background setting.
     *
     * @param light Whether to select colors for light background.
     */
    private static void setColors(final boolean light) {
        if (light) {
            headingColor  = GREEN_UNDERLINED;
            infoColor     = CYAN_BOLD;
            errorColor    = RED_UNDERLINED;
            wildcardColor = RED_BRIGHT;
            containsColor = GREEN_BOLD;
        } else {
            headingColor  = GREEN_UNDERLINED_BRIGHT;
            infoColor     = CYAN;
            errorColor    = RED_UNDERLINED_BRIGHT;
            wildcardColor = RED_BOLD;
            containsColor = YELLOW_BOLD;
        }
    }


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
            Intl.errFormat("wordfind#exception", Exceptions.toString(ex), ex.getStackTrace()[0].toString());
            ex.printStackTrace();
        }
    }

    @Override
    public boolean shutdown(final boolean optional) {
        return false;
    }

    private static void displayProgramInfo() {
        Environment.printProgramInfo(50, colored);
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
                wordFile = WordFile.DEFAULT;
        } else if (matches(arg, "original", "twl06", "orig", "o")) {
            if (ignoreOptions)
                ignored = true;
            else
                wordFile = WordFile.ORIGINAL;
        } else if (matches(arg, "antique", "enable1", "enable", "ant", "en")) {
            if (ignoreOptions)
                ignored = true;
            else
                wordFile = WordFile.ANTIQUE;
        } else if (matches(arg, "version", "vers", "ver")) {
            if (ignoreOptions)
                ignored = true;
            else {
                displayProgramInfo();
                System.exit(0);
            }
        } else if (matches(arg, "help", "h", "?")) {
            if (ignoreOptions)
                ignored = true;
            else {
                displayHelp(true);
                System.exit(0);
            }
        } else if (matches(arg, "statistics", "stats", "stat", "st")) {
            if (ignoreOptions)
                ignored = true;
            else {
                readDictionary();
                dictionary.displayStatistics(System.out);
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
     * Sort all the valid words found, by length, eliminating all that do not conform
     * to the required "begins", "ends", or "contains" constraints.
     *
     * @param words      The full list of valid words found.
     * @param validWords The array of word sets, arranged by length.
     */
    private static void sortValidWords(final List<String> words, final Set<String>[] validWords) {
        for (String validWord : words) {
            StringBuilder adornedWord = new StringBuilder(validWord);
            String word = getLettersOnly(validWord);

            // Here's where we check beginning, ending, and contains clauses
            if (beginningValue.isPresent()) {
                String beginsString = beginningValue.get();
                if (!word.startsWith(beginsString))
                    continue;
                mark(adornedWord, 0, beginsString);
            }
            if (containsValue.isPresent()) {
                String containsString = containsValue.get();
                int containsIndex;
                if (containsPattern != null) {
                    Matcher m = containsPattern.matcher(word);
                    if (!m.find())
                        continue;
                    containsIndex = m.start();
                }
                else {
                    containsIndex = word.indexOf(containsString);
                    if (containsIndex < 0)
                        continue;
                }
                mark(adornedWord, containsIndex, containsString);
            }
            if (endingValue.isPresent()) {
                String endsString = endingValue.get();
                if (!word.endsWith(endsString))
                    continue;
                mark(adornedWord, -endsString.length(), endsString);
            }

            // If we got here, the word satisfies all the specified criteria, so sort it into
            // the appropriate set, given its length.
            int len = word.length();
            Set<String> wordSet = validWords[len - 1];
            wordSet.add(adornedWord.toString());
        }
    }

    /**
     * Report the results of words found in the dictionary search.
     *
     * @param validWords The list of valid words sorted into sets by length.
     * @param largest    Largest word length found.
     * @return           Total number of words.
     */
    private static int reportResults(final Set<String>[] validWords, final int largest) {
        int numberOfWordsFound = 0;

        for (int index = largest - 1; index >= 0; index--) {
            Set<String> wordSet = validWords[index];
            int size = wordSet.size();
            if (size > 0) {
                numberOfWordsFound += size;
                section(Intl.formatString("wordfind#section", (index + 1), size));

                if (minWordSizeToReport > 1 && (index + 1) < minWordSizeToReport) {
                    output(DOTS);
                    continue;
                }
                System.out.println();

                int columnWidth = index + 5;
                int lineLength = 0;
                int wordsSoFarInSet = 0;

                for (String word : wordSet) {
                    wordsSoFarInSet++;
                    if (maxNumberOfWords > 0 && wordsSoFarInSet > maxNumberOfWords) {
                        output(DOTS);
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

        return numberOfWordsFound;
    }

    /**
     * Process all command line arguments, calling {@link #processOption} for options and
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
        containsPattern = null;
        endsWith = false;
        endingValue = Optional.empty();
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
            } else if (ignoreOptions && arg.startsWith(":")) {
                processOption(":", arg.substring(1), ignoreOptions);
            } else if (beginsWith) {
                beginningValue = Optional.of(arg.replaceAll(WILD_PATTERN, ""));
                beginsWith = false;
            } else if (contains) {
                containsValue = Optional.of(arg.replaceAll(WILD_PATTERN, Character.toString(BLANK_MARKER)));
                contains = false;
            } else if (endsWith) {
                endingValue = Optional.of(arg.replaceAll(WILD_PATTERN, ""));
                endsWith = false;
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

    private static void process(final List<String> argWords, final int totalInputSize) {
        StringBuilder letters = new StringBuilder(totalInputSize);

        // If we're running in "word" mode, then lookup each of the words given on the command line
        // to see if they are valid.  But for one letter "words" just buffer them and process
        // as a set of letters.
        for (String arg : argWords) {
            String word = (lowerCase ? arg.toLowerCase() : arg.toUpperCase()).replaceAll(WILD_PATTERN, Character.toString(BLANK_MARKER));
            // For one-letter case (--letter or word has length one), buffer to the end
            if (letter || word.length() == 1) {
                letters.append(word);
            } else {
                // Lookup each word on the command line to see if it is valid.
                if (dictionary.contains(word, findInAdditional)) {
                    info("wordfind#infoArgValid", arg, addLetterValues(arg));
                } else {
                    error("wordfind#errArgNotValid", arg);
                }
            }
        }

        startTime = System.nanoTime();

        // Okay, we might have a set of letters to process (the "--letters" mode).
        int n = letters.length();
        if (n > 0) {
            String inputWord = caseMapper.apply(letters.toString());

            StringBuilder sb = new StringBuilder();
            sb.append(Intl.formatString("wordfind#validWords", quote(letters.toString()),
                (findInAdditional ? Intl.getString("wordfind#additional") : "")));
            if (beginningValue.isPresent()) {
                beginningValue = beginningValue.map(caseMapper);
                String beginsString = beginningValue.get();
                sb.append(Intl.getString("wordfind#beginningWith")).append(quote(beginsString));
                letters.append(beginsString);
            }
            if (containsValue.isPresent()) {
                containsValue = containsValue.map(caseMapper);
                String containsString = containsValue.get();
                if (containsString.indexOf(BLANK_MARKER) >= 0) {
                    sb.append(Intl.getString("wordfind#containingPattern")).append(quote(containsString));
                    containsPattern = convertToPattern(containsString);
                    letters.append(getLettersOnly(containsString));
                }
                else {
                    sb.append(Intl.getString("wordfind#containing")).append(quote(containsString));
                    letters.append(containsString);
                }
            }
            if (endingValue.isPresent()) {
                endingValue = endingValue.map(caseMapper);
                String endsString = endingValue.get();
                sb.append(Intl.getString("wordfind#endingWith")).append(quote(endsString));
                letters.append(endsString);
            }
            heading(sb.toString());

            // See if the letters as entered are a valid word first
            if (dictionary.contains(inputWord, findInAdditional)) {
                info("wordfind#infoArgValid", inputWord, addLetterValues(inputWord));
            }

            n = letters.length();

            // Shuffle the blanks to the end to ensure that words made either with or without
            // blanks will find the "without" version first.
            StringBuilder letterSubset = new StringBuilder(n);
            int numberOfBlanks = 0;
            for (int i = 0; i < n; i++) {
                char ch = letters.charAt(i);
                if (Dictionary.isWild(ch))
                    numberOfBlanks++;
                else
                    letterSubset.append(ch);
            }
            if (numberOfBlanks > 0) {
                letters.setLength(0);
                letters.append(letterSubset);
                letters.append(CharUtil.makeStringOfChars('?', numberOfBlanks));
            }

            List<String> results = new ArrayList<>();
            int largest = dictionary.findAllValidWords(results, letters.toString(), findInAdditional);

            @SuppressWarnings("unchecked")
            Set<String>[] validWords = new Set[largest];
            for (int i = 0; i < largest; i++) {
                validWords[i] = new TreeSet<>(valueComparator);
            }

            sortValidWords(results, validWords);

            long endTime = System.nanoTime();

            int numberOfWordsFound = reportResults(validWords, largest);

            if (numberOfWordsFound == 0)
                error("wordfind#errNoValidWords");

            if (timings) {
                float secs = (float)(endTime - startTime) / 1.0e9f;
                int wordsChecked = dictionary.getNumberWords() + (findInAdditional ? dictionary.getNumberAddlWords() : 0);
                info("wordfind#infoLookup", secs, numberOfWordsFound, wordsChecked);
            }
        }
    }

    private static void lookup(final String word) {
        if (dictionary.contains(word, findInAdditional)) {
            String path = String.format(DICTIONARY_API, word);
            GetQuery query = new GetQuery(DICTIONARY_HOST, Query.DEFAULT_PORT, path, true);
            query.getParameters().add("key", dictKey);
            query.getRequestHeaders().put("Content-type", "application/json");
            try {
                Object result = query.execute();
                @SuppressWarnings("unchecked")
                org.apache.pivot.collections.ArrayList<Object> resultList = (org.apache.pivot.collections.ArrayList<Object>) result;
                @SuppressWarnings("unchecked")
                org.apache.pivot.collections.HashMap<String, Object> resultMap = (org.apache.pivot.collections.HashMap<String, Object>) resultList.get(0);
                int number = 1;
                @SuppressWarnings("unchecked")
                org.apache.pivot.collections.ArrayList<String> definitions = (org.apache.pivot.collections.ArrayList<String>) resultMap.get("shortdef");
                for (String definition : definitions) {
                    System.out.println(ConsoleColor.color(String.format(BLACK_BRIGHT + "  %1$d." + infoColor + " %2$s" + RESET, number++, definition), colored));
                }
            }
            catch (QueryException qe) {
                error("wordfind#lookupIOError", Exceptions.toString(qe));
            }
        }
    }

    /**
     * Run "console" mode, where we prompt for input and process each line, in a loop,
     * provided there is no input from the command line (or the single argument is {@code "@"}).
     *
     * @param argWords       The list arguments "words" (which could be just letters),
     *                       from the command line.
     * @param totalInputSize The total length of the input.
     */
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
            String prompt = ConsoleColor.color("<Bk!>> <.>");

        replLoop:
            while ((line = console.readLine(prompt)) != null) {
                if (line.isEmpty())
                    continue replLoop;

                String parts[] = line.trim().toLowerCase().split("\\s+");
                String cmd = parts[0];

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
                        displayProgramInfo();
                        continue replLoop;

                    case ":statistics":
                    case ":stats":
                    case ":stat":
                    case ":s":
                        dictionary.displayStatistics(System.out);
                        continue replLoop;

                    case ":lookup":
                    case ":look":
                    case ":dictionary":
                    case ":dict":
                        if (!lookupAvailable) {
                            error("wordfind#lookupNotAvailable");
                        }
                        else if (parts.length > 1) {
                            lookup(parts[1]);
                        }
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
     * Read the chosen dictionary file from disk and index the words into a searchable form.
     */
    private static void readDictionary() {
        long startTime = System.nanoTime();
        try {
            dictionary.read(wordFile, lowerCase);
        }
        catch (IOException ioe) {
            error("wordfind#errReadingWordFile", quote(wordFile.getFileName()), Exceptions.toString(ioe));
        }
        long endTime = System.nanoTime();

        if (timings) {
            float secs = (float)(endTime - startTime) / 1.0e9f;
            info("wordfind#infoDictionary", quote(wordFile.getFileName()),
                dictionary.getNumberWords(), dictionary.getNumberAddlWords(), secs);
        }
    }

    /**
     * Read the dictionary API keys (if available).
     */
    private static void readAPIKeys() {
        Properties apiProps = Environment.readPropertiesFile("/dictionary-api.properties");
        if (apiProps != null && !apiProps.isEmpty()) {
            dictKey = apiProps.getProperty("dictionary.key");
            thesKey = apiProps.getProperty("thesaurus.key");
            lookupAvailable = true;
        }
    }


    /**
     * The main program, invoked from the console.
     * @param args The parsed command line arguments.
     */
    public static void main(final String[] args) {
        Environment.setDesktopApp(true);

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
        readDictionary();

        // Also, read the dictionary lookup keys (if present).
        readAPIKeys();

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

