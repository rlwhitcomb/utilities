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
 *	Command line calculator, based on Antlr (v4) grammar / parser.
 *
 *  Change History:
 *      04-Dec-2020 (rlwhitcomb)
 *	    First version, not complete yet.
 *	05-Dec-2020 (rlwhitcomb)
 *	    With no input arguments, execute each line in REPL mode.
 *	07-Dec-2020 (rlwhitcomb)
 *	    Initial help message in REPL mode; use color codes in messages;
 *	    catch errors inside "process" so REPL mode can contine afterwards.
 *	09-Dec-2020 (rlwhitcomb)
 *	    Update version; tweak title message; enhance error reporting.
 *	11-Dec-2020 (rlwhitcomb)
 *	    Use new program info mechanism.
 *	19-Dec-2020 (rlwhitcomb)
 *	    Regularize the exit process.
 *	20-Dec-2020 (rlwhitcomb)
 *	    Redo the way we handle commands in REPL mode.
 *	23-Dec-2020 (rlwhitcomb)
 *	    GUI mode command in REPL mode.
 *	24-Dec-2020 (rlwhitcomb)
 *	    DEBUG command.
 *	28-Dec-2020 (rlwhitcomb)
 *	    Allow embedded expressions (for string interpolation).
 *	28-Dec-2020 (rlwhitcomb)
 *	    Options for color / no color.
 *	28-Dec-2020 (rlwhitcomb)
 *	    Command line "help" and "version" options.
 *	30-Dec-2020 (rlwhitcomb)
 *	    Preload input text area from command line or file contents.
 *	04-Jan-2021 (rlwhitcomb)
 *	    Add Locale option (use with numeric formatting).
 *	05-Jan-2021 (rlwhitcomb)
 *	    Allow a comma-separated list of files on command line.
 *	05-Jan-2021 (rlwhitcomb)
 *	    Remember initial file directory for $include.
 *	05-Jan-2021 (rlwhitcomb)
 *	    Option to report timing on each "process" call.
 *	06-Jan-2021 (rlwhitcomb)
 *	    Option to just print results (without echoing the expression).
 *	06-Jan-2021 (rlwhitcomb)
 *	    Display output text size (for large results).
 *	06-Jan-2021 (rlwhitcomb)
 *	    Use a background thread in the GUI to do the calculations
 *	    so the GUI remains responsive throughout.
 *	07-Jan-2021 (rlwhitcomb)
 *	    New directive to set the "resultsOnly" mode.
 *	08-Jan-2021 (rlwhitcomb)
 *	    Allow directive prefix (":") on commands in REPL mode.
 *	10-Jan-2021 (rlwhitcomb)
 *	    Quiet mode setting.
 *	11-Jan-2021 (rlwhitcomb)
 *	    Don't display timing for silent calculations.
 *	12-Jan-2021 (rlwhitcomb)
 *	    Allow CALC_OPTIONS set in the environment. Options for
 *	    light and dark background modes.
 *	14-Jan-2021 (rlwhitcomb)
 *	    Set the text area fonts in code.
 *	18-Jan-2021 (rlwhitcomb)
 *	    Move all the other (colored) messages to the resources;
 *	    simplify the color rendering.
 *	25-Jan-2021 (rlwhitcomb)
 *	    Add "-inputdir" command line option.
 *	27-Jan-2021 (rlwhitcomb)
 *	    Display HTML page for help.
 *	31-Jan-2021 (rlwhitcomb)
 *	    Only display the Intro (not Title and Version) at start of REPL mode.
 *	01-Feb-2021 (rlwhitcomb)
 *	    Set rational/decimal mode on the command line; convey to Visitor.
 *	10-Feb-2021 (rlwhitcomb)
 *	    Switch over to using the ConsoleColor.color mechanism for messages.
 *	22-Feb-2021 (rlwhitcomb)
 *	    Line numbers for errors don't make sense in REPL mode (i.e., line is always 1).
 *	    Refine syntax error reporting.
 *	23-Feb-2021 (rlwhitcomb)
 *	    Allow Ctrl/Cmd-Enter to initiate a calculation from the input text.
 *	    Add Cmd-F1 ("version") command to the GUI.
 *	23-Feb-2021 (rlwhitcomb)
 *	    Add ":timing" directive.
 *	24-Feb-2021 (rlwhitcomb)
 *	    Tweak the Javadoc.
 *	26-Feb-2021 (rlwhitcomb)
 *	    Allow comma-separated values in CALC_OPTIONS.
 *	10-Mar-2021 (rlwhitcomb)
 *	    Introduce "-nointro" flag.
 *	10-Mar-2021 (rlwhitcomb)
 *	    Tweak the copyright text for GUI display.
 *	11-Mar-2021 (rlwhitcomb)
 *	    Color the REPL prompt string.
 *	15-Mar-2021 (rlwhitcomb)
 *	    Tweak the error message display to eliminate duplicate "."
 *	24-Mar-2021 (rlwhitcomb)
 *	    Change to use a TextPane for input so we can paste in Unicode on OSX.
 *	29-Mar-2021 (rlwhitcomb)
 *	    Move the default theme file.
 *	01-Apr-2021 (rlwhitcomb)
 *	    No joke!  Starting on the Settings dialog.
 *	02-Apr-2021 (rlwhitcomb)
 *	    Finish work on Settings dialog.
 *	05-Apr-2021 (rlwhitcomb)
 *	    Code cleanup around Settings; fix bug on escape character.
 *	06-Apr-2021 (rlwhitcomb)
 *	    Tweak the initial focus component in the Settings dialog.
 *	07-Apr-2021 (rlwhitcomb)
 *	    Add line number rulers to the text areas.
 *	09-Apr-2021 (rlwhitcomb)
 *	    Add "Open" action.
 *	12-Apr-2021 (rlwhitcomb)
 *	    When opening multiple files at once, add a commented header to each
 *	    one in the input window to make it easier to tell what's what.
 *	14-Apr-2021 (rlwhitcomb)
 *	    Dynamically set the "Cmd-F1" tooltip and label text.
 *	15-Apr-2021 (rlwhitcomb)
 *	    Initialize Intl with the new GUI resources. Get the version tip text
 *	    from there. Reverse the Settings dialog buttons to match convention.
 *	20-Apr-2021 (rlwhitcomb)
 *	    Initialize the file browser with the current directory.
 *	    Accessor for "replMode".
 *	22-Apr-2021 (rlwhitcomb)
 *	    Allow line continuations in REPL mode (needs new grammar support to work).
 *	29-Apr-2021 (rlwhitcomb)
 *	    Fix one place that needed Exceptions to get a nicer error message.
 *	29-Apr-2021 (rlwhitcomb)
 *	    Change the GUI "Version" key label id.
 *	08-May-2021 (rlwhitcomb)
 *	    Add option to switch between Cmd-Enter and just Enter to do the calculations.
 *	    Add tabs to the Settings dialog, and put this option into the second one.
 *	10-May-2021 (rlwhitcomb)
 *	    Save file directory from Open for next time.
 *	    Fix bug on Window Settings tab.
 *	02-Jul-2021 (rlwhitcomb)
 *	    New Settings for the background colors (still not effective, but ...).
 *	    Option to always display thousands separators.
 *	10-Jul-2021 (rlwhitcomb)
 *	    Option to ignore case on variable names.
 *	03-Aug-2021 (rlwhitcomb)
 *	    Display the last input on errors in GUI mode.
 *	04-Aug-2021 (rlwhitcomb)
 *	    Set focus back to input field after Version closes.
 *	12-Aug-2021 (rlwhitcomb)
 *	    Add "-library" command line option.
 *	16-Aug-2021 (rlwhitcomb)
 *	    Immediately process stdin if redirected.
 *	16-Aug-2021 (rlwhitcomb)
 *	    Add "noseparators" and "casesense" options.
 *	17-Aug-2021 (rlwhitcomb)
 *	    After some consideration (and fixing broken tests) it appears that
 *	    permanently setting "inputDirectory" during library processing is daft.
 *	    So, reset to what the command line had before processing the libraries.
 *	19-Aug-2021 (rlwhitcomb)
 *	    Only select a font that is capable of displaying at least some of our recognized
 *	    Unicode character symbols (new API in Pivot).
 *	23-Aug-2021 (rlwhitcomb)
 *	    The biggest problem is that the TextAreaOutputStream was using the default system
 *	    charset instead of UTF-8....
 *	    Use a different arrow for results in the GUI.
 *	23-Aug-2021 (rlwhitcomb)
 *	    Ship and use a standard open-source font for the GUI.
 *	23-Aug-2021 (rlwhitcomb)
 *	    In the absence of a charset designation on the Open dialog, trap exceptions
 *	    and try UTF-8 in the case of coding errors.
 *	23-Aug-2021 (rlwhitcomb)
 *	    Actually ... the best font on Windows is MONOSPACED.
 *	25-Aug-2021 (rlwhitcomb)
 *	    Implement arguments on the command line.
 *	31-Aug-2021 (rlwhitcomb)
 *	    Regularize reading files, whether from the command line or the GUI file browser.
 *	01-Sep-2021 (rlwhitcomb)
 *	    We desperately need a "-utf8" flag for reading files (esp. on Windows). Add several
 *	    other options for input charset as well.
 *	26-Sep-2021 (rlwhitcomb)
 *	    Use more color in error messages; color the error position indicator. Tweak the color mapping.
 *	    Tweak one message from Antlr to match our colored version of it.
 *	05-Oct-2021 (rlwhitcomb)
 *	    Add charset spec to "getFileContents" and "readFile".
 *	05-Oct-2021 (rlwhitcomb)
 *	    Add Save function to GUI.
 *	07-Oct-2021 (rlwhitcomb)
 *	    New parameter to "saveVariables".
 *	15-Oct-2021 (rlwhitcomb)
 *	    New command-line option to ignore previous libraries.
 *	21-Oct-2021 (rlwhitcomb)
 *	    #41: Use new Intl method to more correctly construct the Locale desired.
 *	03-Nov-2021 (rlwhitcomb)
 *	    Clear "argValues" in "main".
 *	07-Nov-2021 (rlwhitcomb)
 *	    Don't allow non-directive forms (as in plain "quit" or "help") in REPL mode
 *	    anymore, in preparation for allowing all function/command words as identifiers.
 *	09-Nov-2021 (rlwhitcomb)
 *	    #51: Try default extensions to find files.
 *	12-Nov-2021 (rlwhitcomb)
 *	    #81: Options to quote result strings or not.
 *	28-Nov-2021 (rlwhitcomb)
 *	    #111: New color map for quoted (not colored) output.
 *	01-Dec-2021 (rlwhitcomb)
 *	    #109: Add "quote strings" to the Settings dialog.
 *	03-Dec-2021 (rlwhitcomb)
 *	    #116: Break out parse and execution times.
 *	05-Dec-2021 (rlwhitcomb)
 *	    #106: Catch LeaveException at the highest level to gracefully exit the whole script.
 *	18-Dec-2021 (rlwhitcomb)
 *	    #159: New command line option to silence directives.
 *	01-Jan-2022 (rlwhitcomb)
 *	    #178: Set quiet mode reading libraries (by default; they can still turn quiet off if desired).
 *	    #172: Fix parse timing if there is a parser error.
 *	    #175: Fix decimal digits input in Settings dialog.
 *	    #177: Do version check of library code to ensure compatibility.
 *	02-Jan-2022 (rlwhitcomb)
 *	    #192: Fix coloring of some error messages with embedded quotes.
 *	09-Jan-2022 (rlwhitcomb)
 *	    #201: Add "-expressions" option (opposite of "-resultsonly").
 *	10-Jan-2022 (rlwhitcomb)
 *	    #153: Define variables on command line.
 *	11-Jan-2022 (rlwhitcomb)
 *	    #132: Disable GUI buttons and input field while calculating. Reorganize
 *	    actions in a CalcAction enum.
 *	19-Jan-2022 (rlwhitcomb)
 *	    #93: Delegate all string coloring to new mechanism in Intl.
 *	21-Jan-2022 (rlwhitcomb)
 *	    #217: Delegate environment options parsing to new Options method.
 *	30-Jan-2022 (rlwhitcomb)
 *	    #132: More work on disabling actions.
 *	03-Feb-2022 (rlwhitcomb)
 *	    #132: Need to enable Calc and Clear after GUI file load.
 *	05-Feb-2022 (rlwhitcomb)
 *	    #233: Change methods called from "settings" setValue to take Objects.
 *	    Outer-level "catch" of IllegalStateException also.
 *	09-Feb-2022 (rlwhitcomb)
 *	    #240: Display timing during initial library load.
 *	    Tweak LIB_VERSION to better match library pattern in case of some variation.
 *	11-Feb-2022 (rlwhitcomb)
 *	    #245: Change the way we set quiet mode for libraries, etc. in "process()".
 *	13-Feb-2022 (rlwhitcomb)
 *	    When loading text into the GUI input field, make sure the actions are enabled.
 *	14-Feb-2022 (rlwhitcomb)
 *	    #195: Save splitter position per user in the Preferences for this package.
 *	    Refactor the preferences code to allow for future expansion.
 *	    #247: Implement light/dark color schemes in GUI.
 *	16-Feb-2022 (rlwhitcomb)
 *	    #248: Add buttons to Version prompt for the LICENSE and NOTICE files.
 *	28-Feb-2022 (rlwhitcomb)
 *	    Changed to use QueuedExecutorService for background GUI thread.
 *	12-Apr-2022 (rlwhitcomb)
 *	    #269: New method to load main program info (in Environment).
 *	14-Apr-2022 (rlwhitcomb)
 *	    #273: Move math-related classes to "math" package.
 *	18-Apr-2022 (rlwhitcomb)
 *	    #270: Update Version dialog to latest program info format.
 *	03-May-2022 (rlwhitcomb)
 *	    #68: Catch IndexOutOfBoundsException at the same place as other low-level
 *	    exceptions thrown from helper methods.
 *	05-May-2022 (rlwhitcomb)
 *	    #308: Update the reset color tag.
 *	07-May-2022 (rlwhitcomb)
 *	    #292: Don't do the library version check here; use new ":require" directive instead.
 *	16-May-2022 (rlwhitcomb)
 *	    #328: Implement "-clear" command line option.
 *	23-May-2022 (rlwhitcomb)
 *	    Reword an error message to display more information.
 *	27-May-2022 (rlwhitcomb)
 *	    Move "saveVariables" to CalcUtil.
 *	11-Jun-2022 (rlwhitcomb)
 *	    #363: Set process exit code on errors in non-REPL mode.
 *	20-Jun-2022 (rlwhitcomb)
 *	    #364: Allow echoing to stderr.
 *	08-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 *	10-Jul-2022 (rlwhitcomb)
 *	    #397: Change the way we process line continuations in REPL mode.
 *	    #392: Option to sort by keys.
 *	    #403: For raw string support, don't expand tabs on input files unless
 *	    running as GUI (because the text field can't support tabs).
 *	19-Jul-2022 (rlwhitcomb)
 *	    #417: Throw error if file is not found on ":include".
 *	29-Jul-2022 (rlwhitcomb)
 *	    #402: New "-requires" options on command line.
 *	08-Aug-2022 (rlwhitcomb)
 *	    #432: In preparation for new flag, call "getFileContents" even on command line.
 *	    And don't need LINESEP to signal end of input.
 *	    #432: Add flags ("-file", "-text", and "-filetext") as well as input handling for this.
 *	16-Aug-2022 (rlwhitcomb)
 *	    #439: Implement fallback processing of "next" statement.
 *	24-Aug-2022 (rlwhitcomb)
 *	    #454: Process new colored option in Settings dialog; allow setting colored mode from visitor.
 *	    #447: New grads trig mode.
 *	28-Aug-2022 (rlwhitcomb)
 *	    #464: Add "-output" and associated options to redirect streams to files.
 *	31-Aug-2022 (rlwhitcomb)
 *	    #471: Add "-ctrlenter" and "-ctrl" command line options.
 *	08-Oct-2022 (rlwhitcomb)
 *	    #506: Command-line options to only print LF line endings, or use system default.
 *	18-Oct-2022 (rlwhitcomb)
 *	    #527: Fix processing of ":include" with embedded spaces.
 *	19-Oct-2022 (rlwhitcomb)
 *	    Add some Javadoc and move some methods out to CharUtil.
 *	09-Nov-2022 (rlwhitcomb)
 *	    #550: Catch AssertException at the highest levels.
 *	13-Dec-2022 (rlwhitcomb)
 *	    #582: Add ".clc" and ".xpr" as supported file extensions.
 *	24-Mar-2023 (rlwhitcomb)
 *	    #596: Need to set "guiMode" from REPL ":gui" command. Move pure REPL commands
 *	    into the grammar itself.
 *	01-Jun-2023 (rlwhitcomb)
 *	    #614: Ignore debug mode during initial library loads.
 *	15-Jun-2023 (rlwhitcomb)
 *	    #618: Add "-nol" and other command-line option aliases.
 *	29-Sep-2023 (rlwhitcomb)
 *	    #622: Add "f" to the color map for the RED_BOLD_BRIGHT color.
 *	19-Oct-2023 (rlwhitcomb)
 *	    #624: Add new interface message to CalcDisplayer for timing messages and implement
 *	    appropriately for console, GUI, and files.
 *	27-Oct-2023 (rlwhitcomb)
 *	    #633: Add options on the command line to ignore (or not) the CALC_OPTIONS settings
 *	    on startup (new methods in Options class).
 *	    Rename the new methods in Options.
 *	26-Nov-2023 (rlwhitcomb)
 *	    #634: Add command line option to clear the GUI preferences on startup.
 *	    Change default split ratio.
 *	27-Nov-2023 (rlwhitcomb)
 *	    New aliases for rational mode on the command line.
 */
package info.rlwhitcomb.calc;

import info.rlwhitcomb.IntlProvider;
import info.rlwhitcomb.jarfile.Launcher;
import info.rlwhitcomb.math.BigFraction;
import info.rlwhitcomb.math.NumericUtil.RangeMode;
import info.rlwhitcomb.util.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.util.Vote;
import org.apache.pivot.wtk.*;
import org.apache.pivot.wtk.Keyboard.KeyStroke;
import org.apache.pivot.wtk.text.Document;
import org.apache.pivot.wtk.util.TextAreaOutputStream;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarFile;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static info.rlwhitcomb.calc.CalcUtil.checkRequiredVersions;
import static info.rlwhitcomb.util.ConsoleColor.Code.*;

/**
 * Command line calculator, which will also read from {@link System#in} or from one or more files.
 * <p> Works from just the command line, from reading input from the console, or in GUI mode.
 * <p> Built from an <a href="https://www.antlr.org">Antlr</a> parser, this calculator implements a
 * wide variety of operators and functions, and is built on top of the Java {@link BigDecimal}
 * and {@link BigInteger} classes for essentially unlimited precision arithmetic. It also uses
 * our {@link BigFraction} class to do exact rational number arithmetic.
 */
public class Calc
	implements Application, CalcDisplayer
{
	private static final boolean ON_WINDOWS = Environment.isWindows();

	private static boolean darkBackgrounds = ON_WINDOWS;

	private static final String LINESEP = System.lineSeparator();

	private static final String EMPTY_TEXT = "\n";

	/** Preferences key for the saved split ratio for the main GUI window. */
	private static final String SPLIT_RATIO = "splitRatio";

	/** Default split ratio; this is the percentage (fraction) occupied by the top pane. */
	private static final float DEFAULT_SPLIT_RATIO = 0.6f;


	/**
	 * An enumeration of what we expect next on the command line.
	 * <p> Each command-line option that needs an additional argument value
	 * has an entry here.
	 */
	private enum Expecting
	{
		/** Default is to expect an expression or file name. */
		DEFAULT,
		/** Some options (like "-version" or "-help") will quit right afterwards. */
		QUIT_NOW,
		/** Locale option requires a locale name. */
		LOCALE,
		/** Input directory option requires a directory name. */
		DIRECTORY,
		/** Library name(s) requires the names. */
		LIBRARY,
		/** All the remaining parameters are $n global parameters. */
		ARGUMENTS,
		/** A charset to use for reading files. */
		CHARSET,
		/** A named variable declaration. */
		VARIABLE,
		/** A version string. */
		VERSION,
		/** A base version string. */
		BASE_VERSION,
		/** Output file path. */
		OUTPUT_FILE,
		/** Error output file path. */
		ERROR_FILE,
		/** Output file charset. */
		OUTPUT_CHARSET
	}

	/**
	 * An enumeration of the button actions for the GUI screen.
	 */
	private enum CalcAction
	{
		HELP     ("help",      HelpAction.class),
		VERSION  ("version",   VersionAction.class),
		SETTINGS ("settings",  SettingsAction.class),
		SAVE     ("save",      SaveAction.class),
		OPEN     ("open",      OpenAction.class),
		CLEAR    ("clear",     ClearAction.class),
		CALCULATE("calculate", CalculateAction.class),
		EXIT     ("exit",      ExitAction.class),
		LICENSE  ("license",   LicenseAction.class),
		NOTICE   ("notice",    NoticeAction.class);

		private final String id;
		private final Class<? extends Action> actionClass;
		private Action action;

		CalcAction(final String name, final Class<? extends Action> act) {
		    id = name;
		    actionClass = act;
		}

		@Override
		public String toString() {
		    return id;
		}

		Action register(final Calc instance) {
		    try {
			Constructor<? extends Action> constructor = actionClass.getDeclaredConstructor(Calc.class);
			constructor.setAccessible(true);
			Action act = constructor.newInstance(instance);
			return Action.addNamedAction(id, act);
		    }
		    catch (Exception ex) {
			throw new RuntimeException(ex);
		    }
		}

		void enable(final boolean enabled) {
		    Action.getNamedAction(id).setEnabled(enabled);
		}

		void perform(Component source) {
		    Action.getNamedAction(id).perform(source);
		}

	}


	/** What we're expecting next on the command line. */
	private static Expecting expecting;


	/**
	 * Component attributes for the Settings dialog to store old and new settings.
	 */
	private enum Attribute
	{
		ORIGINAL_SETTINGS,
		ORIGINAL_MATH_CONTEXT,
		NEW_MATH_CONTEXT,
		ORIGINAL_COLORED
	}


	private static boolean noIntro           = false;
	private static boolean guiMode           = false;
	private static boolean replMode          = false;
	private static boolean debug             = false;
	private static boolean colors            = true;
	private static boolean timing            = false;
	private static boolean resultsOnly       = false;
	private static boolean quiet             = false;
	private static boolean rational          = false;
	private static boolean separators        = false;
	private static boolean ignoreCase        = false;
	private static boolean quotes            = true;
	private static boolean sortKeys          = false;
	private static boolean silenceDirectives = false;

	private static boolean treatAsText = false;
	private static boolean treatAsFile = false;

	private static boolean useCmdEnter = true;

	private static boolean initialLibraryLoad = false;

	private static boolean clearPrefs = false;

	private static Locale  locale  = null;

	private static String  requiredVersion = null;
	private static String  baseRequiredVersion = null;

	private BXMLSerializer serializer = null;

	private static File inputDirectory = null;
	private static File rootDirectory = null;
	private static List<String> libraryNames = null;

	private static File outputFile = null;
	private static File errorFile = null;

	private static Charset inputCharset = null;
	private static String outputCharsetName = null;

	private Display display;

	@BXML private Window mainWindow;
	@BXML private SplitPane splitPane;
	@BXML private TextPane inputTextPane;
	@BXML private TextArea outputTextArea;
	@BXML private NumberRuler inputRuler;
	@BXML private NumberRuler outputRuler;
	@BXML private Label outputSizeLabel;
	@BXML private PushButton versionButton;
	@BXML private Label versionKeyLabel;
	@BXML private Prompt versionPrompt;
	@BXML private Label versionText;
	@BXML private Label implementationText;
	@BXML private Label buildText;
	@BXML private Label copyrightText;
	@BXML private Label javaText;
	@BXML private Label mainClassText;
	@BXML private Label processText;
	@BXML private Prompt settingsPrompt;
	@BXML private Form settingsForm;
	@BXML private RadioButton decimalPrecisionButton;
	@BXML private RadioButton defaultPrecisionButton;
	@BXML private RadioButton doublePrecisionButton;
	@BXML private RadioButton floatPrecisionButton;
	@BXML private RadioButton unlimitedPrecisionButton;
	@BXML private TextInput decimalDigitsInput;
	@BXML private RadioButton degreesModeButton;
	@BXML private RadioButton radiansModeButton;
	@BXML private RadioButton gradsModeButton;
	@BXML private RadioButton binaryModeButton;
	@BXML private RadioButton siModeButton;
	@BXML private RadioButton mixedModeButton;
	@BXML private Checkbox rationalCheck;
	@BXML private Checkbox timingCheck;
	@BXML private Checkbox debugCheck;
	@BXML private Checkbox quietCheck;
	@BXML private Checkbox silenceCheck;
	@BXML private Checkbox resultsCheck;
	@BXML private Checkbox separatorCheck;
	@BXML private Checkbox quoteStringsCheck;
	@BXML private Checkbox sortKeysCheck;
	@BXML private Checkbox coloredCheck;
	@BXML private RadioButton useEnterButton;
	@BXML private RadioButton useCmdEnterButton;
	@BXML private RadioButton lightBackgroundButton;
	@BXML private RadioButton darkBackgroundButton;


	/** The background worker thread to do the calculations in GUI mode. */
	private QueuedExecutorService execService = new QueuedExecutorService();

	private NumberFormat sizeFormat;

	/** The last expression text to be displayed from the GUI in case of error. */
	private static String currentText = null;
	/** An error indicator pointing to the offending text. */
	private static String currentIndicator = null;

	private static Console console = System.console();

	private static BailErrorStrategy errorStrategy = new BailErrorStrategy();
	private static ErrorListener errorListener = new ErrorListener();
	private static CalcDisplayer displayer;
	private static CalcObjectVisitor visitor;

	/** The text read from the command line or a file. */
	private static String inputText = null;

	/** The previously displayed help directory. */
	private static File tempHelpDirectory = null;

	/** The color map used for our custom colors. */
	private static HashMap<String, Object> colorMap = new HashMap<>();

	/** The color map used for quoting values instead of coloring them. */
	private static HashMap<String, Object> quoteMap = new HashMap<>();

	/** The argument values from the command line. */
	private static List<String> argValues = new ArrayList<>();

	/** The map of variable declarations from the command line. */
	private static HashMap<String, String> variables = new HashMap<>();


	/** The list of default file extensions to use to find input files. */
	private static final String[] DEFAULT_EXTS = {
		".calc",
		".expr",
		".clc",
		".xpr",
		".ca",
		".ex"
	};


	/**
	 * Initialize or update the color map and quote map (used for
	 * coloring or else quoting values in error messages, etc.).
	 * <p> Also sets the appropriate map based on the coloring mode
	 * and sets the global {@link #colors} flag from the parameter.
	 *
	 * @param useColors	The (new) value for the {@link #colors}
	 *			flag to indicate colored messages or not.
	 */
	private static void computeColors(final boolean useColors) {
	    if (colorMap.isEmpty()) {
		colorMap.put("v", GREEN_BOLD);
		colorMap.put("u", GREEN_BOLD);
		colorMap.put("e", RED_BOLD);
		colorMap.put("f", RED_BOLD_BRIGHT);
		colorMap.put("r", RESET);
		colorMap.put(".", null);
	    }
	    colorMap.put("x", (darkBackgrounds ? YELLOW_BRIGHT : BLUE_BOLD));
	    colorMap.put("y", (darkBackgrounds ? YELLOW_BRIGHT : BLUE_BOLD));
	    colorMap.put("a", (darkBackgrounds ? WHITE : BLACK_BRIGHT));

	    if (quoteMap.isEmpty()) {
		quoteMap.put("x", "");
		quoteMap.put("y", "\u201C");
		quoteMap.put("a", "");
		quoteMap.put("v", "");
		quoteMap.put("u", "\u201C");
		quoteMap.put("e", "");
		quoteMap.put("f", "\u201C");
		quoteMap.put("r", "");
		quoteMap.put(".", "\u201D");
	    }

	    colors = useColors;
	    Intl.setColoring(true, useColors ? colorMap : quoteMap);
	}

	/**
	 * From the {@link #darkBackgrounds} flag, set the appropriate colors on the given component.
	 *
	 * @param comp The individual component to set colors on.
	 */
	private void setGUIComponentColors(Component comp) {
	    int bgIndex;
	    Color fgColor, bgColor;

	    if (darkBackgrounds) {
		bgIndex = 2;
		fgColor = Color.WHITE;
		bgColor = Color.BLACK;
	    }
	    else {
		bgIndex = 11;
		fgColor = Color.BLACK;
		bgColor = Color.WHITE;
	    }

	    if (comp instanceof PushButton) {
		comp.putStyle(Style.backgroundColor, darkBackgrounds ? 6 : 10);
	    }
	    else if (comp instanceof Label) {
		comp.putStyle(Style.color, fgColor);
	    }
	    else if (comp instanceof Border) {
		comp.putStyle(Style.color, darkBackgrounds ? 10 : 7);
		comp.putStyle(Style.backgroundColor, bgIndex);
		if (darkBackgrounds)
		    comp.putStyle("titleColor", 17);
	    }
	    else if (comp instanceof ScrollPane.Corner) {
		comp.putStyle(Style.backgroundColor, bgIndex);
	    }
	    else if (comp instanceof NumberRuler) {
		String bgName = darkBackgrounds ? "SteelBlue" : "LemonChiffon";
		String fgName = darkBackgrounds ? "Gold" : "SteelBlue";
		comp.putStyle(Style.color, fgName);
		comp.putStyle(Style.backgroundColor, bgName);
	    }
	    else if (comp instanceof TextArea || comp instanceof TextPane) {
		comp.putStyle(Style.color, fgColor);
		comp.putStyle(Style.backgroundColor, bgIndex);
	    }
	    else if (comp instanceof Container) {
		Color oldBgColor = comp.getStyleColor(Style.backgroundColor);
		if (oldBgColor != null) {
		    comp.putStyle(Style.backgroundColor, bgColor);
		}
	    }
	}

	/**
	 * Set the colors for the GUI components, depending on the dark/light settings.
	 *
	 * @param container The main (or other parent) container component to color.
	 * @see #setGUIComponentColors
	 */
	private void setGUIContainerColors(Container container) {
	    setGUIComponentColors(container);
	    for (Component comp : container) {
		if (comp instanceof Container)
		    setGUIContainerColors((Container) comp);
		else
		    setGUIComponentColors(comp);
	    }
	}


	/**
	 * Listen for key press events inside the input {@link TextPane} and respond.
	 * <p> Primarily to allow a keystroke to initiate calculation while typing.
	 */
	private static class KeyPressListener implements ComponentKeyListener
	{
		@Override
		public boolean keyPressed(Component comp, int keyCode, Keyboard.KeyLocation keyLocation) {
		    if (keyCode == Keyboard.KeyCode.ENTER) {
			if (useCmdEnter && Keyboard.isCmdPressed()) {
			    Action.performAction(CalcAction.CALCULATE, comp);
			    return true;
			}
			else if (!useCmdEnter && !Keyboard.areAnyPressed(Keyboard.Modifier.ALL_MODIFIERS)) {
			    Action.performAction(CalcAction.CALCULATE, comp);
			    return true;
			}
		    }
		    // Otherwise key was not consumed
		    return false;
		}
	}

	/**
	 * The key press listener used to implement automatic calculation from either {@code Enter}
	 * or {@code Cmd-Enter} pressed in the expressions window.
	 */
	private KeyPressListener keyPressListener = new KeyPressListener();


	/**
	 * Listen for characters inserted / removed from the input text area, updating the action enablement.
	 */
	private class CharacterListener implements TextPaneCharacterListener
	{
		public void enableActions() {
		    int inputSize = inputTextPane.getCharacterCount();
		    int outputSize = outputTextArea.getCharacterCount();
		    // The input almost always has a trailing '\n', so we use 1 as the discriminant
		    CalcAction.CALCULATE.enable(inputSize > 1);
		    CalcAction.CLEAR.enable(inputSize > 1 || outputSize > 0);
		}

		@Override
		public void charactersInserted(TextPane textPane, int index, int count) {
		    enableActions();
		}

		@Override
		public void charactersRemoved(TextPane textPane, int index, int count) {
		    enableActions();
		}
	}

	/**
	 * Listener to enable/disable actions based on whether the input and output fields
	 * have any content.
	 */
	private CharacterListener characterListener = new CharacterListener();


	/**
	 * Simple method to request focus on the given component.
	 * <p> The reason we have to do this is that this always requires a small delay
	 * (for repaint or other chained events to be queued first).
	 *
	 * @param comp The component that should be given focus.
	 */
	private void requestFocus(final Component comp) {
	    ApplicationContext.scheduleCallback(() -> comp.requestFocus(), 200L);
	}


	private void handleRadioSelection(Button selectedButton) {
	    boolean digitsInputEnabled = false;
	    MathContext mcNew;

	    if (selectedButton == defaultPrecisionButton)
		mcNew = MathContext.DECIMAL128;
	    else if (selectedButton == doublePrecisionButton)
		mcNew = MathContext.DECIMAL64;
	    else if (selectedButton == floatPrecisionButton)
		mcNew = MathContext.DECIMAL32;
	    else if (selectedButton == unlimitedPrecisionButton)
		mcNew = MathContext.UNLIMITED;
	    else {
		mcNew = new MathContext(visitor.getMathContext().toString());
		digitsInputEnabled = true;
	    }

	    decimalDigitsInput.setEnabled(digitsInputEnabled);
	    decimalDigitsInput.setText(String.valueOf(mcNew.getPrecision()));
	    if (digitsInputEnabled)
		requestFocus(decimalDigitsInput);

	    settingsPrompt.setAttribute(Attribute.NEW_MATH_CONTEXT, mcNew);
	}

	private Component handleDialogOpen(Prompt dialog) {
	    MathContext mc = visitor.getMathContext();
	    Settings settings = visitor.getSettings();
	    Settings oldSettings = new Settings(settings);
	    boolean oldColored = colors;
	    Component focusComponent = settingsForm;

	    // TODO: we really should do this view load/store and data bind mappings

	    dialog.setAttribute(Attribute.ORIGINAL_SETTINGS, oldSettings);
	    dialog.setAttribute(Attribute.ORIGINAL_MATH_CONTEXT, mc);
	    dialog.setAttribute(Attribute.NEW_MATH_CONTEXT, mc);
	    dialog.setAttribute(Attribute.ORIGINAL_COLORED, oldColored);

	    decimalDigitsInput.setText(String.valueOf(mc.getPrecision()));
	    decimalDigitsInput.setEnabled(false);

	    if (mc.equals(MathContext.UNLIMITED))
		unlimitedPrecisionButton.setSelected(true);
	    else if (mc.equals(MathContext.DECIMAL128))
		defaultPrecisionButton.setSelected(true);
	    else if (mc.equals(MathContext.DECIMAL64))
		doublePrecisionButton.setSelected(true);
	    else if (mc.equals(MathContext.DECIMAL32))
		floatPrecisionButton.setSelected(true);
	    else {
		decimalPrecisionButton.setSelected(true);
		decimalDigitsInput.setEnabled(true);
	    }
	    ButtonGroup precisionGroup = decimalPrecisionButton.getButtonGroup();
	    focusComponent = precisionGroup.getSelection();

	    switch (settings.trigMode) {
		case DEGREES:
		    degreesModeButton.setSelected(true);
		    break;
		case RADIANS:
		    radiansModeButton.setSelected(true);
		    break;
		case GRADS:
		    gradsModeButton.setSelected(true);
		    break;
	    }

	    switch (settings.units) {
		case BINARY:
		    binaryModeButton.setSelected(true);
		    break;
		case DECIMAL:
		    siModeButton.setSelected(true);
		    break;
		case MIXED:
		    mixedModeButton.setSelected(true);
		    break;
	    }

	    rationalCheck.setSelected(settings.rationalMode);
	    timingCheck.setSelected(timing);
	    debugCheck.setSelected(debug);

	    quietCheck.setSelected(quiet);
	    silenceCheck.setSelected(silenceDirectives);
	    resultsCheck.setSelected(resultsOnly);
	    separatorCheck.setSelected(settings.separatorMode);
	    quoteStringsCheck.setSelected(settings.quoteStrings);
	    sortKeysCheck.setSelected(settings.sortKeys);
	    coloredCheck.setSelected(colors);

	    if (useCmdEnter)
		useCmdEnterButton.setSelected(true);
	    else
		useEnterButton.setSelected(true);

	    if (darkBackgrounds)
		darkBackgroundButton.setSelected(true);
	    else
		lightBackgroundButton.setSelected(true);

	    return focusComponent;
	}

	private void handleDialogClosed(Prompt dialog) {
	    if (dialog.getResult() && dialog.getSelectedOptionIndex() == 0) {
		MathContext originalMathContext = (MathContext) dialog.getAttribute(Attribute.ORIGINAL_MATH_CONTEXT);
		MathContext newMathContext = (MathContext) dialog.getAttribute(Attribute.NEW_MATH_CONTEXT);
		if (!originalMathContext.equals(newMathContext))
		    visitor.setMathContext(newMathContext);

		Settings originalSettings = (Settings) dialog.getAttribute(Attribute.ORIGINAL_SETTINGS);
		boolean originalColored = (Boolean) dialog.getAttribute(Attribute.ORIGINAL_COLORED);

		TrigMode newTrigMode = TrigMode.RADIANS;
		if (degreesModeButton.isSelected())
		    newTrigMode = TrigMode.DEGREES;
		else if (gradsModeButton.isSelected())
		    newTrigMode = TrigMode.GRADS;
		if (newTrigMode != originalSettings.trigMode)
		    visitor.setTrigMode(newTrigMode);

		RangeMode newUnits = RangeMode.MIXED;
		if (binaryModeButton.isSelected())
		    newUnits = RangeMode.BINARY;
		else if (siModeButton.isSelected())
		    newUnits = RangeMode.DECIMAL;
		if (newUnits != originalSettings.units)
		    visitor.setUnits(newUnits);

		boolean newRational = rationalCheck.isSelected();
		if (newRational != originalSettings.rationalMode)
		    visitor.setRationalMode(newRational);

		boolean newTiming = timingCheck.isSelected();
		if (newTiming != timing)
		    visitor.setTimingMode(newTiming);

		boolean newDebug = debugCheck.isSelected();
		if (newDebug != debug)
		    visitor.setDebugMode(newDebug);

		boolean newQuiet = quietCheck.isSelected();
		if (newQuiet != quiet)
		    setQuietMode(newQuiet);

		boolean newSilence = silenceCheck.isSelected();
		if (newSilence != silenceDirectives)
		    setSilenceMode(newSilence);

		boolean newResults = resultsCheck.isSelected();
		if (newResults != resultsOnly)
		    setResultsOnlyMode(newResults);

		boolean newSeparators = separatorCheck.isSelected();
		if (newSeparators != originalSettings.separatorMode)
		    visitor.setSeparatorMode(newSeparators);

		boolean newQuoteStrings = quoteStringsCheck.isSelected();
		if (newQuoteStrings != originalSettings.quoteStrings)
		    visitor.setQuoteStringsMode(newQuoteStrings);

		boolean newSortKeys = sortKeysCheck.isSelected();
		if (newSortKeys != originalSettings.sortKeys)
		    visitor.setSortKeysMode(newSortKeys);

		boolean newColored = coloredCheck.isSelected();
		if (newColored != originalColored)
		    visitor.setColoredMode(newColored);

		useCmdEnter = useCmdEnterButton.isSelected();

		darkBackgrounds = darkBackgroundButton.isSelected();

		computeColors(colors);
		setGUIContainerColors(mainWindow);
	    }

	    dialog.setAttribute(Attribute.ORIGINAL_SETTINGS, null);
	    dialog.setAttribute(Attribute.ORIGINAL_MATH_CONTEXT, null);
	    dialog.setAttribute(Attribute.NEW_MATH_CONTEXT, null);
	    dialog.setAttribute(Attribute.ORIGINAL_COLORED, null);

	    requestFocus(inputTextPane);
	}

	/**
	 * Get the main preferences node for this package.
	 *
	 * @return The preference node to use for save / load of preference values.
	 */
	private Preferences getPrefsNode() {
	    return Preferences.userNodeForPackage(Calc.class);
	}

	/**
	 * Read all our saved value from the current user's preferences for this package.
	 */
	private void loadPreferences() {
	    try {
		Preferences node = getPrefsNode();

		if (clearPrefs)
		    node.clear();

		splitPane.setSplitRatio(node.getFloat(SPLIT_RATIO, DEFAULT_SPLIT_RATIO));
	    }
	    catch (BackingStoreException ex) {
		// Ignore this, which is only thrown by "clear()", which would mean the
		// "-noprefs" flag is effectively ignored
	    }
	}

	/**
	 * Save all the desired values from here to the user's preferences for this package.
	 */
	private void savePreferences() {
	    try {
		Preferences node = getPrefsNode();

		node.putFloat(SPLIT_RATIO, splitPane.getSplitRatio());

		node.flush();
	    }
	    catch (BackingStoreException ex) {
		// Going to ignore this, as the default(s) will come up next time
	    }
	}


	@Override
	public void startup(Display displayValue, Map<String, String> properties) {
	    display = displayValue;

	    try {
		for (CalcAction action : CalcAction.values()) {
		    action.register(this);
		}

		IntlProvider provider = new IntlProvider(getClass());
		Intl.initResources(provider);

		serializer = new BXMLSerializer();
		serializer.readObject(getClass().getResource("calc.bxml"), provider.getResources());
		serializer.bind(this);

		// To implement the displayer, redirect System.out to our TextArea for display
		PrintStream ps = new TextAreaOutputStream(outputTextArea, StandardCharsets.UTF_8, 16_384).toPrintStream();
		System.setOut(ps);
		System.setErr(ps);

		// For now, we won't support colors in the GUI display
		computeColors(false);
		setGUIContainerColors(mainWindow);

		// Increase the maximum output text length in case of humongous calculations
		outputTextArea.setMaximumLength(20_000_000);

		sizeFormat = NumberFormat.getIntegerInstance();
		sizeFormat.setGroupingUsed(true);

		// On MacOS at least we can count on the monospaced font list to give us something nice,
		// but on Windows, explicitly request the font we have just installed
		String fontNames = ON_WINDOWS ? Font.MONOSPACED : FontUtilities.MONOSPACED_FONTS;
		// These are some of the (most) useful Unicode chars we recognize, so it would be nice
		// if we could display them ...
		String testChars = "\u21e8\u1d28\u213c\u213f\u2107\u221a\u221b\u220f\u2211";
		Font monospacedFont = FontUtilities.decodeCapable(fontNames + "-18", testChars);

		inputTextPane.getStyles().put(Style.font, monospacedFont);
		outputTextArea.getStyles().put(Style.font, monospacedFont);
		inputRuler.getStyles().put(Style.font, monospacedFont);
		outputRuler.getStyles().put(Style.font, monospacedFont);

		inputTextPane.getComponentKeyListeners().add(keyPressListener);
		inputTextPane.getTextPaneCharacterListeners().add(characterListener);

		KeyStroke versionKey = KeyStroke.decode("Cmd-F1");
		String key = versionKey.toString();

		versionButton.setTooltipText(Intl.formatString("versionTip", key));
		versionKeyLabel.setText(key);

		KeyStroke enterKey = KeyStroke.decode("Enter");
		KeyStroke cmdEnterKey = KeyStroke.decode("Cmd-Enter");

		String keyTemplate = Intl.getString("enterToCalculate");
		useEnterButton.setButtonData(String.format(keyTemplate, enterKey));
		useCmdEnterButton.setButtonData(String.format(keyTemplate, cmdEnterKey));

		inputTextPane.setDocument(new Document());

		// Prepopulate the text are with any text from the command line or input file
		inputTextPane.setText(inputText == null ? EMPTY_TEXT : inputText);
		characterListener.enableActions();

		loadPreferences();

		decimalPrecisionButton.getButtonGroup().getButtonGroupListeners().add(new ButtonGroupListener() {
		    @Override
		    public void selectionChanged(final ButtonGroup buttonGroup, final Button previousSelection) {
			handleRadioSelection(buttonGroup.getSelection());
		    }
		});

		settingsPrompt.getSheetStateListeners().add(new SheetStateListener() {
		    @Override
		    public void sheetClosed(final Sheet sheet) {
			handleDialogClosed((Prompt) sheet);
		    }
		});

		versionPrompt.getSheetStateListeners().add(new SheetStateListener() {
		    @Override
		    public Vote previewSheetClose(Sheet sheet, boolean result) {
			int selectedOption = ((Prompt) sheet).getSelectedOptionIndex();
			if (result) {
			    switch (selectedOption) {
				case 0:
				    CalcAction.LICENSE.perform(sheet);
				    break;
				case 1:
				    CalcAction.NOTICE.perform(sheet);
				    break;
				case 2:
				    return Vote.APPROVE;
			    }
			    return Vote.DENY;
			}
			else {
			    return Vote.APPROVE;
			}
		    }

		    @Override
		    public void sheetClosed(final Sheet sheet) {
			requestFocus(inputTextPane);
		    }
		});

		displayer = this;
		visitor = new CalcObjectVisitor(displayer, rational, separators, silenceDirectives, ignoreCase, quotes, sortKeys);

		// Set the command-line arguments into the symbol table as $nn
		int index = 0;
		for (String argument : argValues) {
		    visitor.setArgument(index++, argument);
		}

		// Set the variables defined on the command line
		for (java.util.Map.Entry<String, String> var : variables.entrySet()) {
		    visitor.setVariable(var.getKey(), var.getValue());
		}

		// Try to read and process any given libraries before doing anything else
		readAndProcessLibraries(visitor, errorStrategy);

		mainWindow.open(display);
		requestFocus(inputTextPane);
	    }
	    catch (Throwable ex) {
		String message = Exceptions.toString(ex) + ClassUtil.getCallingMethod(1);
		if (displayer != null)
		    displayer.displayErrorMessage(message);
		else {
		    System.err.print(message);
		    Intl.println(System.err);
		}
	    }
	}

	@Override
	public boolean shutdown(boolean optional) {
	    savePreferences();

	    if (optional) {
		execService.shutdown();
		try {
		    execService.awaitTermination(100L, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException ie) {
		    return true;
		}
	    }
	    else {
		execService.shutdownNow();
	    }

	    // Always proceed with shutdown unless optional
	    return false;
	}

	private void updateOutputSize() {
	    ApplicationContext.queueCallback(() -> {
		outputSizeLabel.setText(sizeFormat.format(outputTextArea.getCharacterCount()));
		characterListener.enableActions();
	    });
	}

	@Override
	public void displayResult(String exprString, String resultString) {
	    if (resultsOnly)
		Intl.outFormat("calc#resultOnly", resultString);
	    else
		Intl.outFormat("calc#resultGUI", exprString, resultString);
	    updateOutputSize();
	}

	@Override
	public void displayActionMessage(String message) {
	    if (!resultsOnly) {
		Intl.outFormat("calc#action", message);
		updateOutputSize();
	    }
	}

	/**
	 * The main output routine that handles output selection.
	 *
	 * @param message The already formatted message to output.
	 * @param output  Output selection choice (either "stdout", "stderr", or both).
	 */
	private static void output(String message, CalcDisplayer.Output output) {
	    if (message == null || message.isEmpty()) {
		switch (output) {
		    case OUTPUT:
			Intl.outPrintln();
			break;
		    case ERROR:
			Intl.errPrintln();
			break;
		    case BOTH:
			Intl.outPrintln();
			Intl.errPrintln();
			break;
		}
	    }
	    else {
		switch (output) {
		    case OUTPUT:
			Intl.outFormat("calc#message", message);
			break;
		    case ERROR:
			Intl.errFormat("calc#message", message);
			break;
		    case BOTH:
			Intl.outFormat("calc#message", message);
			Intl.errFormat("calc#message", message);
			break;
		}
	    }
	}

	@Override
	public void displayMessage(String message, CalcDisplayer.Output output) {
	    output(message, output);
	    updateOutputSize();
	}

	/**
	 * Used to display error messages along with an indicator of where in the line the error was found.
	 *
	 * @see #currentText
	 * @see #currentIndicator
	 */
	private void displayInputTextAndIndicator() {
	    if (currentText != null) {
		Intl.outFormat("calc#resultOnly", CharUtil.stripLineEndings(currentText));
	    }
	    if (currentIndicator != null) {
		Intl.outFormat("calc#resultOnly", currentIndicator);
		currentIndicator = null;
	    }
	}

	@Override
	public void displayErrorMessage(String message) {
	    displayInputTextAndIndicator();
	    Intl.outFormat("calc#error", message);
	    updateOutputSize();
	}

	@Override
	public void displayErrorMessage(String message, int lineNumber) {
	    displayInputTextAndIndicator();
	    Intl.errFormat("calc#errorLine", message, lineNumber);
	    updateOutputSize();
	}

	@Override
	public void displayTimingMessage(String message) {
	    // In the GUI these come out in the results window as always
	    displayMessage(message, CalcDisplayer.Output.OUTPUT);
	}


	public static boolean getReplMode() {
	    return replMode;
	}

	public static boolean setTimingMode(boolean mode) {
	    boolean oldMode = timing;
	    timing = mode;
	    return oldMode;
	}

	public static boolean setDebugMode(boolean mode) {
	    boolean oldMode = debug;
	    debug = mode;
	    return oldMode;
	}

	public static boolean setResultsOnlyMode(boolean mode) {
	    boolean oldMode = resultsOnly;
	    resultsOnly = mode;
	    return oldMode;
	}

	public static boolean setQuietMode(Object mode) {
	    boolean oldMode = quiet;
	    quiet = CharUtil.getBooleanValue(mode);
	    visitor.setSilent(quiet);
	    return oldMode;
	}

	public static boolean setSilenceMode(Object mode) {
	    boolean oldMode = silenceDirectives;
	    silenceDirectives = CharUtil.getBooleanValue(mode);
	    visitor.setSilenceDirectives(silenceDirectives);
	    return oldMode;
	}

	public static boolean getColoredMode() {
	    return colors;
	}

	public static void setColoredMode(final boolean colored) {
	    computeColors(colored && !guiMode);
	}


	/**
	 * An error listener that hooks into our error reporting strategy.
	 */
	public static class ErrorListener extends BaseErrorListener
	{
		@Override
		public void syntaxError(Recognizer<?, ?> recognizer,
			Object offendingSymbol,
			int line, int charPositionInLine,
			String message,
			RecognitionException e) {
		    int width = guiMode ? charPositionInLine + 1 : charPositionInLine + 3;
		    currentIndicator = CharUtil.padToWidth("^", width, CharUtil.Justification.RIGHT);
		    if (replMode) {
			Intl.outFormat("calc#error", currentIndicator);
		    }
		    int ix = message.indexOf("at input '");
		    if (ix > 0) {
			message = message.replace("at input '", "at input <x>");
			// Sometimes the message has embedded quotes, so be careful
			int count = CharUtil.countQuotes(message, '\'');
			if (count > 2) {
			    // embedded quotes, replace only the last one with end tag
			    ix = message.lastIndexOf('\'');
			    message = message.substring(0, ix) + "<.>" + message.substring(ix + 1);
			}
			else {
			    message = message.replace("'", "<.>");
			}
		    }
		    throw new CalcException(Intl.formatString("calc#syntaxError", charPositionInLine, message), line);
		}
	}

	/**
	 * A parser error strategy that abandons the parse without trying to recover.
	 */
	public static class BailErrorStrategy extends DefaultErrorStrategy
	{
		@Override
		public void recover(Parser recognizer, RecognitionException e) {
		    Token t = e.getOffendingToken();
		    int charPos = t.getCharPositionInLine();
		    int width = guiMode ? charPos + 1 : charPos + 3;
		    currentIndicator = CharUtil.padToWidth("^", width, CharUtil.Justification.RIGHT);
		    if (replMode) {
			Intl.outFormat("calc#error", currentIndicator);
		    }
		    throw new CalcException(Intl.formatString("calc#errorNoAlt", charPos, t.getText()), t.getLine());
		}
/*
		@Override
		public Token recoverInline(Parser recognizer)
			throws RecognitionException
		{
		    InputMismatchException ime = new InputMismatchException(recognizer);
		    throw new CalcException(ime, ime.getOffendingToken().getLine());
		}
*/
		@Override
		public void sync(Parser recognizer) { }
	}

	/**
	 * A custom lexer that just quits after lexer errors.
	 */
	public static class CalcBailLexer extends CalcLexer
	{
		public CalcBailLexer(CharStream input) {
		    super(input);
		}

		@Override
		public void recover(LexerNoViableAltException e) {
		    throw new CalcException(e, getLine());
		}
	}


	/**
	 * The default displayer that displays messages and results (usually with colors)
	 * to the system console ({@link System#out}).
	 */
	private static class ConsoleDisplayer implements CalcDisplayer
	{
		@Override
		public void displayResult(String exprString, String resultString) {
		    if (resultsOnly)
			Intl.outFormat("calc#resultOnly", resultString);
		    else
			Intl.outFormat("calc#result", exprString, resultString);
		}

		@Override
		public void displayActionMessage(String message) {
		    if (!resultsOnly)
			Intl.outFormat("calc#action", message);
		}

		@Override
		public void displayMessage(String message, CalcDisplayer.Output output) {
		    output(message, output);
		}

		@Override
		public void displayErrorMessage(String message) {
		    Intl.errFormat("calc#error", message);
		}

		@Override
		public void displayErrorMessage(String message, int lineNumber) {
		    // We're going to add a period ourselves, so take it out if the
		    // underlying error already has one.
		    String regularMessage = (message.endsWith(".") ?
			message.substring(0, message.length() - 1) : message);

		    if (replMode)
			Intl.errFormat("calc#errorPeriod", regularMessage);
		    else
			Intl.errFormat("calc#errorLine", regularMessage, lineNumber);
		}

		@Override
		public void displayTimingMessage(String message) {
		    // In the console and output files these come out on the error channel to avoid
		    // corrupting the canon file
		    displayMessage(message, CalcDisplayer.Output.ERROR);
		}
	}


	/**
	 * Displayer that outputs to redirected files, possibly with different charset.
	 * <p> The only difference from the {@link ConsoleDisplayer} is the setup where
	 * we redirect the standard streams to files.
	 */
	private static class FileDisplayer extends ConsoleDisplayer
	{
		FileDisplayer(File output, File error, String csName)
			throws FileNotFoundException, UnsupportedEncodingException
		{
		    if (output != null)
			System.setOut(CharUtil.isNullOrEmpty(csName) ? new PrintStream(output) : new PrintStream(output, csName));

		    if (error != null)
			System.setErr(CharUtil.isNullOrEmpty(csName) ? new PrintStream(error) : new PrintStream(error, csName));
		}
	}


	private void enableActions(boolean enable) {
	    CalcAction.SETTINGS.enable(enable);
	    CalcAction.SAVE.enable(enable);
	    CalcAction.OPEN.enable(enable);
	    CalcAction.CLEAR.enable(enable);
	    CalcAction.CALCULATE.enable(enable);
	    CalcAction.EXIT.enable(enable);

	    inputTextPane.setEnabled(enable);
	}

	private class CalculateAction extends Action
	{
		@Override
		public void perform(Component source) {
		    final String exprText = inputTextPane.getText();
		    currentText = exprText;
		    enableActions(false);

		    execService.execute(() -> {
			try {
			    processString(exprText, quiet);
			}
			finally {
			    ApplicationContext.queueCallback(() -> {
				currentText = null;
				inputTextPane.setText(EMPTY_TEXT);
				enableActions(true);
				characterListener.enableActions();
				requestFocus(inputTextPane);
			    });
			}
		    });
		}
	}

	private class SaveAction extends Action
	{
		@Override
		public void perform(Component source) {
		    if (inputDirectory == null) {
			if (rootDirectory == null) {
			    rootDirectory = new File("./");
			}
		    }
		    else if (rootDirectory == null) {
			rootDirectory = inputDirectory;
		    }
		    try {
			rootDirectory = rootDirectory.getCanonicalFile();
		    }
		    catch (IOException ioe) {
			// just leave root directory as-is
		    }
		    final FileBrowserSheet browser =
			new FileBrowserSheet(FileBrowserSheet.Mode.SAVE_AS, rootDirectory.getPath());
		    browser.open(mainWindow, sheet -> {
			if (!sheet.getResult())
			    return;
			File selectedFile = browser.getSelectedFile();
			rootDirectory = browser.getRootDirectory();
			try {
			    CalcUtil.saveVariables(visitor, null, visitor.getVariables(), selectedFile.toPath(), null);
			    Alert.alert(MessageType.INFO, Intl.formatString("saveSuccess", selectedFile.getPath()), Intl.getString("save"), null, mainWindow, null);
			}
			catch (IOException ioe) {
			    Alert.alert(MessageType.ERROR, Exceptions.toString(ioe), ioe.getClass().getSimpleName(), null, mainWindow, null);
			}
		    });
		}
	}

	private class OpenAction extends Action
	{
		@Override
		public void perform(Component source) {
		    if (inputDirectory == null) {
			if (rootDirectory == null) {
			    rootDirectory = new File("./");
			}
		    }
		    else if (rootDirectory == null) {
			rootDirectory = inputDirectory;
		    }
		    try {
			rootDirectory = rootDirectory.getCanonicalFile();
		    }
		    catch (IOException ioe) {
			// just leave root directory as-is
		    }
		    final FileBrowserSheet browser =
			new FileBrowserSheet(FileBrowserSheet.Mode.OPEN_MULTIPLE, rootDirectory.getPath());
		    browser.open(mainWindow, sheet -> {
			if (!sheet.getResult())
			    return;
			Sequence<File> selectedFiles = browser.getSelectedFiles();
			rootDirectory = browser.getRootDirectory();
			StringBuilder buf = new StringBuilder();
			try {
			    for (int i = 0; i < selectedFiles.getLength(); i++) {
				File f = selectedFiles.get(i);
				if (selectedFiles.getLength() > 1) {
				    String filePath = f.getPath();
				    CharUtil.padToWidth(buf, "#", filePath.length() + 3, '-').append('\n');
				    buf.append("# ").append(filePath).append('\n');
				    CharUtil.padToWidth(buf, "#", filePath.length() + 3, '-').append("\n\n");
				}
				readFile(f, buf, null);
			    }
			}
			catch (IOException ioe) {
			    Alert.alert(MessageType.ERROR, Exceptions.toString(ioe), ioe.getClass().getSimpleName(), null, mainWindow, null);
			}
			inputTextPane.setText(buf.toString());
			characterListener.enableActions();
		    });
		}
	}

	private class ClearAction extends Action
	{
		@Override
		public void perform(Component source) {
		    inputTextPane.setText(EMPTY_TEXT);
		    outputTextArea.setText("");
		    updateOutputSize();
		    requestFocus(inputTextPane);
		}
	}

	private class HelpAction extends Action
	{
		@Override
		public void perform(Component source) {
		    Calc.displayHelp();
		}
	}

	private class VersionAction extends Action
	{
		@Override
		public void perform(Component source) {
		    displayVersion();
		}
	}

	private class SettingsAction extends Action
	{
		@Override
		public void perform(Component source) {
		    displaySettings();
		}
	}

	private class ExitAction extends Action
	{
		@Override
		public void perform(Component source) {
		    Calc.exit();
		}
	}

	private class LicenseAction extends Action
	{
		@Override
		public void perform(Component source) {
		    displayTextDialog("versionLicense", "/META-INF/LICENSE", source);
		}
	}

	private class NoticeAction extends Action
	{
		@Override
		public void perform(Component source) {
		    displayTextDialog("versionNotice", "/META-INF/NOTICE", source);
		}
	}


	public static void printTitleAndVersion() {
	    Environment.printProgramInfo(50, colors);
	}

	public static void printIntro() {
	    if (!guiMode) {
		Intl.printHelp("calc#intro", colors);
	    }
	}

	public static void doGuiMode(String[] args) {
	    if (!guiMode) {
		guiMode = true;
		DesktopApplicationContext.main(Calc.class, args);
	    }
	}

	/**
	 * Extract our help page(s) and supporting files, then open in a browser.
	 */
	public static void displayHelp() {
	    try {
		if (tempHelpDirectory == null) {
		    JarFile jarFile = Launcher.getJarFile(Launcher.getOurJarFile());
		    tempHelpDirectory = FileUtilities.unpackFiles(jarFile, ClassUtil.getClassDirectory(Calc.class),
				".html;.png", "calchelp", true);
		}
		File helpFile = new File(tempHelpDirectory, "calc_help.html");
		Desktop.getDesktop().open(helpFile);
	    }
	    catch (IOException ex) {
		System.err.print(Exceptions.toString(ex));
		Intl.println(System.err);
	    }
	}

	/**
	 * Display the product version information for the GUI.
	 * <p> Note: this is the same information displayed by {@link Environment#printProgramInfo}.
	 */
	private void displayVersion() {
	    String productName = Environment.getProductName();
	    String versionInfo = Environment.getProductVersion();
	    String implInfo    = Environment.getImplementationVersion();
	    String buildInfo   = Environment.getProductBuildDateTime();
	    String copyright   = Environment.getCopyrightNotice().replace("(c)", "\u00A9");;
	    String javaVersion = Environment.getJavaVersion();
	    String mainClass   = Environment.getMainClass();
	    String process     = Environment.getProcess();

	    versionPrompt.setMessage(productName);
	    versionText.setText(versionInfo);
	    implementationText.setText(implInfo);
	    buildText.setText(buildInfo);
	    copyrightText.setText(copyright);
	    javaText.setText(javaVersion);
	    mainClassText.setText(mainClass);
	    processText.setText(process);

	    if (versionPrompt.getOptions().getLength() == 1) {
		versionPrompt.getOptions().insert(Intl.getString("versionLicense"), 0);
		versionPrompt.getOptions().insert(Intl.getString("versionNotice"), 1);
	    }

	    versionPrompt.open(mainWindow);
	}

	/**
	 * Display a dialog with the given text resource as the contents.
	 *
	 * @param titleKey    Text resource key of the dialog title.
	 * @param contentPath Resource path of the contents to display.
	 * @param source      Source component (whose {@code Dialog} ancestor is the owner).
	 */
	private void displayTextDialog(String titleKey, String contentPath, Component source) {
	    Window owner = (Window) source.getAncestor(Window.class);
	    String contentText = ClassUtil.getResourceAsString(contentPath);

	    FillPane textDialogContents = new FillPane();
	    ScrollPane scrollPane = new ScrollPane(ScrollPane.ScrollBarPolicy.FILL, ScrollPane.ScrollBarPolicy.FILL_TO_CAPACITY);
	    TextArea contentTextArea = new TextArea();
	    contentTextArea.putStyle(Style.font, FontUtilities.decode("MONOSPACED-12"));
	    contentTextArea.setEditable(false);
	    contentTextArea.setText(contentText);
	    scrollPane.setView(contentTextArea);
	    textDialogContents.add(scrollPane);

	    Dialog textDialog = new Dialog(Intl.getString(titleKey), textDialogContents, true);
	    textDialog.setMaximumHeight(mainWindow.getHeight() - 200);
	    textDialog.copyStyle(Style.backgroundColor, contentTextArea);
	    textDialog.open(owner);
	    ApplicationContext.scheduleCallback(() -> {
		contentTextArea.setSelection(0, 0);
		scrollPane.setScrollTop(0);
		contentTextArea.requestFocus();
	    }, 200L);
	}

	/**
	 * Display the settings dialog.
	 */
	private void displaySettings() {
	    Component focusComponent = handleDialogOpen(settingsPrompt);

	    settingsPrompt.open(mainWindow);
	    requestFocus(focusComponent);
	}

	public static void exit() {
	    if (guiMode)
		DesktopApplicationContext.exit(false);
	    else
		System.exit(0);
	}

	/**
	 * Read the contents of one file and append to the buffer, if the file
	 * can be found as given, and is readable. If a charset is specified, use
	 * that, otherwise try with the charset given on the command line (if any),
	 * or the platform default as a last resort. But, if a decoding error occurs
	 * use UTF-8 one more time (which we assume will work).
	 *
	 * @param f		The file path to read (no other location is attempted).
	 * @param inputBuf	The buffer to append the file contents to.
	 * @param charset	The charset to use to decode the contents (can be {@code null}).
	 * @return		Whether or not the file could be found and was readable.
	 * @throws IOException if there was a problem reading the existing file.
	 */
	private static boolean readFile(File f, StringBuilder inputBuf, Charset charset)
		throws IOException
	{
	    if (FileUtilities.canRead(f)) {
		inputDirectory = f.getCanonicalFile().getParentFile();
		String fileText = "";
		int tabWidth = guiMode ? 8 : 0;
		try {
		    if (charset != null) {
			fileText = FileUtilities.readFileAsString(f, charset, tabWidth);
		    }
		    else if (inputCharset != null) {
			fileText = FileUtilities.readFileAsString(f, inputCharset, tabWidth);
		    }
		    else {
			fileText = FileUtilities.readFileAsString(f, null, tabWidth);
		    }
		}
		catch (IOException ioe) {
		    // We're gonna bet the problem is the charset
		    fileText = FileUtilities.readFileAsString(f, StandardCharsets.UTF_8, tabWidth);
		}

		inputBuf.append(fileText);
		return true;
	    }
	    else {
		if (!FileUtilities.dotName(f.getName()) && FileUtilities.extOnly(f).isEmpty()) {
		    for (String ext : DEFAULT_EXTS) {
			File newFile = FileUtilities.decorate(f.getName(), f.getParentFile(), ext);
			if (readFile(newFile, inputBuf, charset))
			    return true;
		    }
		}
	    }
	    return false;
	}

	/**
	 * Take the input as a delimited string of file names/paths and try to
	 * read all of them in. If the path does not exist as given, then try to
	 * find it using the latest {@link #inputDirectory} value. But, if any
	 * of the potential files cannot be found, then take the entire input
	 * string as a single expression and return that instead.
	 *
	 * @param paths	A possible list of file names/paths separated by either
	 *		comma or semicolon.
	 * @return	Either the contents of all the files listed, if found,
	 *		or the <code>paths</code> string itself as an expression.
	 * @throws	IOException if there was an error trying to read the files
	 *		that exist (obviously if the files do not exist this is
	 *		not an "error" condition per se, unless {@code throwError} is true).
	 */
	public static String getFileContents(String paths)
		throws IOException
	{
	    return getFileContents(paths, null, false);
	}

	/**
	 * Take the input as a delimited string of file names/paths and try to
	 * read all of them in. If the path does not exist as given, then try to
	 * find it using the latest {@link #inputDirectory} value. But, if any
	 * of the potential files cannot be found, then take the entire input
	 * string as a single expression and return that instead.
	 *
	 * @param paths	A possible list of file names/paths separated by either
	 *		comma or semicolon.
	 * @param charset The charset to use for decoding the files (could be {@code null}
	 *		in which case the logic in {@link #readFile} is used instead).
	 * @param throwError Whether to throw on error, or just treat the input as expressions.
	 * @return	Either the contents of all the files listed, if found,
	 *		or the <code>paths</code> string itself as an expression.
	 * @throws	IOException if there was an error trying to read the files
	 *		that exist (obviously if the files do not exist this is
	 *		not an "error" condition per se, unless {@code throwError} is true).
	 */
	public static String getFileContents(String paths, Charset charset, boolean throwError)
		throws IOException
	{
	    /* We must be able to read all the files listed, or else the input
	     * is treated as a single expression. */
	    boolean unableToRead = false;
	    StringBuilder inputBuf = new StringBuilder();

	    String[] files = paths.split(ON_WINDOWS ? "\\s*[,;]\\s*|\\s+" : "\\s*[,;:]\\s*|\\s+");
	    for (String file : files) {
		if (CharUtil.isNullOrEmpty(file))
		    continue;

		if (inputBuf.length() > 0)
		    inputBuf.append(LINESEP);

		// From the ":include" directive (throwError == false)
		// or if neither option is given (treatAsFile and treatAsText both false)
		// or the "-file" directive is given

		if (initialLibraryLoad || throwError || treatAsFile || !treatAsText) {
		    File f = new File(file);
		    if (!readFile(f, inputBuf, charset)) {
			if (inputDirectory != null) {
			    f = new File(inputDirectory, file);
			    if (!readFile(f, inputBuf, charset)) {
				unableToRead = true;
			    }
			}
			else {
			    unableToRead = true;
			}
		    }
		}

		// If the "-text" option is given
		else if (treatAsText) {
		    unableToRead = true;
		}

		if (unableToRead) {
		    if (throwError || treatAsFile) {
			throw new FileNotFoundException(file);
		    }
		    else {
			inputBuf.setLength(0);
			inputBuf.append(paths);
			break;
		    }
		}
	    }

	    return inputBuf.toString();
	}

	/**
	 * Take an expression string and process it, optionally silent (that is, not outputting anything).
	 * <p> Called from the main loop, and also from inside interpolated strings.
	 * <p> Traps any {@link IOException}s thrown by {@link #process}.
	 *
	 * @param inputText The text to parse and evaluate.
	 * @param silent    Whether to output anything to the displayer.
	 * @return          The last value returned from the evaluated expression text.
	 */
	public static Object processString(String inputText, boolean silent) {
	    try {
		return process(CharStreams.fromString(inputText), visitor, errorStrategy, silent, false);
	    }
	    catch (IOException ioe) {
		displayer.displayErrorMessage(Intl.formatString("calc#ioError", Exceptions.toString(ioe)));
	    }
	    return null;
	}

	/**
	 * Instantiate a new lexer and parser, and process the input stream through them to produce the last result.
	 * <p> Implements the display for the {@code "-timing"} directive.
	 *
	 * @param input         The input character stream.
	 * @param visitor       Tree visitor which traverses the parsed expression tree to produce the result.
	 * @param errorStrategy Error handler which implements our error reporting strategy.
	 * @param silent        Set {@code true} from inside interpolated strings, otherwise set by the global setting
	 *                      value and/or the command-line option.
	 * @param throwError    If {@code true} then trapped exceptions are re-thrown, otherwise reported to the displayer.
	 * @return              The last object produced by the visitor from the parsed input.
	 * @throws IOException if there is a problem readng the input stream.
	 */
	private static Object process(CharStream input, CalcObjectVisitor visitor, BailErrorStrategy errorStrategy, boolean silent, boolean throwError)
		throws IOException
	{
	    Object returnValue = null;
	    boolean oldSilent  = setQuietMode(silent);
	    long startTime     = Environment.highResTimer();
	    long parseEndTime  = 0L;
	    long execStartTime = 0L;
	    long endTime;

	    try {
		CalcLexer lexer = new CalcBailLexer(input);
		lexer.removeErrorListeners();
		lexer.addErrorListener(errorListener);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CalcParser parser = new CalcParser(tokens);
		parser.setErrorHandler(errorStrategy);
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);

		ParseTree tree = parser.prog();

		parseEndTime = Environment.highResTimer();

		if (debug && !initialLibraryLoad) {
		    displayer.displayMessage(tree.toStringTree(parser), CalcDisplayer.Output.OUTPUT);
		}

		execStartTime = Environment.highResTimer();

		returnValue = visitor.visit(tree);
	    }
	    catch (NextException next) {
		if (throwError)
		    throw next;
		else
		    displayer.displayErrorMessage(Intl.formatString("calc#nextError"));
	    }
	    catch (IllegalArgumentException | IllegalStateException | IndexOutOfBoundsException ex) {
		if (throwError)
		    throw ex;
		else
		    displayer.displayErrorMessage(Intl.formatString("calc#argError", Exceptions.toString(ex)));
	    }
	    catch (CalcException ce) {
		if (throwError)
		    throw ce;
		else
		    displayer.displayErrorMessage(Intl.formatString("calc#argError", ce.getMessage()), ce.getLine());
	    }
	    catch (AssertException ae) {
		if (throwError)
		    throw ae;
		else
		    displayer.displayErrorMessage(Intl.formatString("calc#argError", ae.getMessage()), ae.getContext().getStart().getLine());
	    }
	    finally {
		endTime = Environment.highResTimer();
		if (parseEndTime == 0L)
		    parseEndTime = endTime;
		if (execStartTime == 0L)
		    execStartTime = endTime;
		setQuietMode(oldSilent);
	    }

	    if (timing && (!silent || initialLibraryLoad)) {
		double parseTime = Environment.timerValueToSeconds(parseEndTime - startTime);
		double execTime  = Environment.timerValueToSeconds(endTime - execStartTime);
		double totalTime = Environment.timerValueToSeconds(endTime - startTime);
		displayer.displayTimingMessage(Intl.formatString("calc#timing", parseTime, execTime, totalTime));
	    }

	    return returnValue;
	}

	private static void readAndProcessLibraries(CalcObjectVisitor visitor, BailErrorStrategy errorStrategy)
		throws IOException
	{
	    if (libraryNames != null) {
		initialLibraryLoad = true;
		try {
		    for (String libraryName : libraryNames) {
			process(CharStreams.fromString(getFileContents(libraryName)), visitor, errorStrategy, true, false);
		    }
		}
		finally {
		    initialLibraryLoad = false;
		}
	    }
	}

	/**
	 * Process one command line option.
	 *
	 * @param arg    The complete argument, including leading characters.
	 * @param option Only the word part after the initial "-", "--", or "/".
	 * @return       For options that require a value, which value to expect.
	 */
	private static Expecting processOption(String arg, String option) {
	    if (option.isEmpty() && arg.equals("--")) {
		if (expecting == Expecting.ARGUMENTS)
		    return Expecting.DEFAULT;
		else if (expecting == Expecting.DEFAULT)
		    return Expecting.ARGUMENTS;
	    }

	    // Allow whatever kind of string while reading arguments
	    if (expecting == Expecting.ARGUMENTS) {
		argValues.add(arg);
		return expecting;
	    }

	    if (expecting != Expecting.DEFAULT) {
		Intl.errFormat("calc#expectNotOption", expecting, arg);
		return Expecting.QUIT_NOW;
	    }

	    String lowerOption = option.toLowerCase();

	    switch (lowerOption) {
		case "nointro":
		case "noi":
		    noIntro = true;
		    break;
		case "intro":
		case "int":
		case "i":
		    noIntro = false;
		    break;
		case "gui":
		case "g":
		    guiMode = true;
		    break;
		case "console":
		case "cons":
		case "con":
		case "c":
		    guiMode = false;
		    break;
		case "debug":
		case "deb":
		case "d":
		    debug = true;
		    break;
		case "nodebug":
		case "nodeb":
		case "nod":
		case "no":
		    debug = false;
		    break;
		case "colors":
		case "color":
		case "col":
		    computeColors(true);
		    break;
		case "nocolors":
		case "nocolor":
		case "nocol":
		case "noc":
		case "nc":
		    computeColors(false);
		    break;
		case "darkbackgrounds":
		case "darkbackground":
		case "darkback":
		case "darkbg":
		case "dark":
		case "dk":
		    darkBackgrounds = true;
		    computeColors(colors);
		    break;
		case "lightbackgrounds":
		case "lightbackground":
		case "lightback":
		case "lightbg":
		case "light":
		case "lt":
		    darkBackgrounds = false;
		    computeColors(colors);
		    break;
		case "timing":
		case "time":
		case "tm":
		case "t":
		    timing = true;
		    break;
		case "notiming":
		case "notime":
		case "not":
		case "nt":
		    timing = false;
		    break;
		case "resultsonly":
		case "resultonly":
		case "results":
		case "result":
		case "res":
		case "rs":
		case "r":
		    resultsOnly = true;
		    break;
		case "expressions":
		case "expression":
		case "express":
		case "exprs":
		case "expr":
		case "exp":
		case "ex":
		    resultsOnly = false;
		    break;
		case "quiet":
		case "q":
		    quiet = true;
		    break;
		case "fractions":
		case "rational":
		case "fraction":
		case "ration":
		case "frac":
		case "rat":
		    rational = true;
		    break;
		case "decimal":
		case "dec":
		    rational = false;
		    break;
		case "separators":
		case "separator":
		case "seps":
		case "sep":
		case "s":
		    separators = true;
		    break;
		case "noseparators":
		case "noseparator":
		case "noseps":
		case "nosep":
		case "nos":
		case "ns":
		    separators = false;
		    break;
		case "ignorecase":
		case "ignore":
		case "caseinsensitive":
		case "insensitive":
		case "case":
		case "ign":
		case "ins":
		    ignoreCase = true;
		    break;
		case "casesense":
		case "casesensitive":
		case "sensitive":
		case "sense":
		case "sens":
		case "sen":
		    ignoreCase = false;
		    break;
		case "quotestrings":
		case "quotestring":
		case "quotes":
		case "quote":
		case "quo":
		    quotes = true;
		    break;
		case "noquotestrings":
		case "noquotestring":
		case "noquotes":
		case "noquote":
		case "noq":
		    quotes = false;
		    break;
		case "sortobjects":
		case "sortobject":
		case "sortkeys":
		case "sortkey":
		    sortKeys = true;
		    break;
		case "nosortobjects":
		case "nosortobject":
		case "nosortkeys":
		case "nosortkey":
		    sortKeys = false;
		    break;
		case "silencedirectives":
		case "silentdirectives":
		case "silencedir":
		case "silentdir":
		case "silence":
		case "silent":
		    silenceDirectives = true;
		    break;
		case "displaydirectives":
		case "displaydir":
		case "display":
		    silenceDirectives = false;
		    break;
		case "requiresversion":
		case "requireversion":
		case "requires":
		case "require":
		case "req":
		    return Expecting.VERSION;
		case "requiresbaseversion":
		case "requirebaseversion":
		case "baseversion":
		case "base":
		    return Expecting.BASE_VERSION;
		case "locale":
		case "loc":
		case "l":
		    return Expecting.LOCALE;
		case "inputdir":
		case "input":
		case "dir":
		    return Expecting.DIRECTORY;
		case "libraries":
		case "library":
		case "libs":
		case "lib":
		case "lb":
		    return Expecting.LIBRARY;
		case "ignorelibraries":
		case "nolibraries":
		case "nolibrary":
		case "nolibs":
		case "nolib":
		case "nolb":
		case "nol":
		case "nl":
		    libraryNames = null;
		    break;
		case "files":
		case "file":
		case "f":
		    treatAsFile = true;
		    treatAsText = false;
		    break;
		case "text":
		case "txt":
		case "tx":
		    treatAsText = true;
		    treatAsFile = false;
		    break;
		case "filetext":
		case "ft":
		    treatAsText = treatAsFile = false;
		    break;
		case "cleararguments":
		case "clearargs":
		case "clear":
		case "clr":
		    argValues.clear();
		    break;
		case "nopreferences":
		case "noprefs":
		case "nopref":
		case "nop":
		    clearPrefs = true;
		    break;
		case "loadpreferences":
		case "loadprefs":
		case "loadpref":
		case "ldp":
		    clearPrefs = false;
		    break;
		case "variable":
		case "define":
		case "var":
		    return Expecting.VARIABLE;
		case "ctrlenter":
		case "cmdenter":
		case "ctrl":
		case "cmd":
		    useCmdEnter = true;
		    break;
		case "enter":
		case "ent":
		case "e":
		    useCmdEnter = false;
		    break;
		case "utf8":
		case "utf":
		case "u":
		    inputCharset = StandardCharsets.UTF_8;
		    break;
		case "win1252":
		case "win":
		case "w":
		    inputCharset = Charset.forName("windows-1252");
		    break;
		case "charset":
		case "char":
		case "cs":
		    return Expecting.CHARSET;
		case "output":
		case "out":
		case "o":
		    return Expecting.OUTPUT_FILE;
		case "error":
		case "err":
		    return Expecting.ERROR_FILE;
		case "outputcharset":
		case "outputcs":
		case "outchar":
		case "outcs":
		case "ocs":
		    return Expecting.OUTPUT_CHARSET;
		case "default":
		case "def":
		    inputCharset = null;
		    break;
		case "linefeed":
		case "lfonly":
		case "lf":
		    Intl.setLfLineEnding(true);
		    break;
		case "lineending":
		case "lineend":
		case "ln":
		    Intl.setLfLineEnding(false);
		    break;
		case "help":
		case "h":
		case "?":
		    displayHelp();
		    return Expecting.QUIT_NOW;
		case "version":
		case "vers":
		case "ver":
		case "v":
		    printTitleAndVersion();
		    return Expecting.QUIT_NOW;
		default:
		    // Allow the option options (and ignore them here)
		    if (Options.checkOptionOption(lowerOption) == Options.OptionChoice.NONE) {
			Intl.errFormat("calc#unknownOption", arg);
			return Expecting.QUIT_NOW;
		    }
	    }
	    return Expecting.DEFAULT;
	}

	private static void processArgs(String[] args, List<String> argList) {
	    expecting = Expecting.DEFAULT;

	    for (String arg : args) {
		String opt = Options.isOption(arg, true);
		if (opt != null) {
		    expecting = processOption(arg, opt);
		}
		else {
		    switch (expecting) {
			case DEFAULT:
			    argList.add(arg);
			    break;
			case QUIT_NOW:
			    break;
			case LOCALE:
			    try {
				locale = Intl.getValidLocale(arg);
				expecting = Expecting.DEFAULT;
			    }
			    catch (IllegalArgumentException iae) {
				Intl.errFormat("calc#errorPeriod", Exceptions.toString(iae));
			    }
			    break;
			case DIRECTORY:
			    inputDirectory = new File(arg);
			    expecting = Expecting.DEFAULT;
			    break;
			case LIBRARY:
			    if (libraryNames == null)
				libraryNames = new ArrayList<>();
			    String[] names = arg.split(ON_WINDOWS ? "[;,]" : "[,;:]");
			    for (String name : names)
				libraryNames.add(name);
			    expecting = Expecting.DEFAULT;
			    break;
			case ARGUMENTS:
			    argValues.add(arg);
			    break;
			case CHARSET:
			    try {
				inputCharset = Charset.forName(arg);
				expecting = Expecting.DEFAULT;
			    }
			    catch (IllegalCharsetNameException | UnsupportedCharsetException cse) {
				Intl.errFormat("calc#charsetError", arg, Exceptions.toString(cse));
				expecting = Expecting.QUIT_NOW;
			    }
			    break;
			case OUTPUT_FILE:
			    outputFile = new File(arg);
			    expecting = Expecting.DEFAULT;
			    break;
			case ERROR_FILE:
			    errorFile = new File(arg);
			    expecting = Expecting.DEFAULT;
			    break;
			case OUTPUT_CHARSET:
			    try {
				Charset cs = Charset.forName(arg);
				outputCharsetName = cs.name();
				expecting = Expecting.DEFAULT;
			    }
			    catch (IllegalCharsetNameException | UnsupportedCharsetException cse) {
				Intl.errFormat("calc#charsetError", arg, Exceptions.toString(cse));
				expecting = Expecting.QUIT_NOW;
			    }
			    break;
			case VARIABLE:
			    String parts[] = arg.split("[=:]");
			    if (parts.length == 2) {
				if (CharUtil.isNullOrEmpty(parts[0])) {
				    Intl.errFormat("calc#declError", arg);
				    expecting = Expecting.QUIT_NOW;
				}
				else {
				    variables.put(parts[0], parts[1]);
				    expecting = Expecting.DEFAULT;
				}
			    }
			    else {
				Intl.errFormat("calc#declError", arg);
				expecting = Expecting.QUIT_NOW;
			    }
			    break;
			case VERSION:
			    requiredVersion = arg;
			    expecting = Expecting.DEFAULT;
			    break;
			case BASE_VERSION:
			    baseRequiredVersion = arg;
			    expecting = Expecting.DEFAULT;
			    break;
			default:
			    Intl.errFormat("calc#expectValue", expecting);
			    expecting = Expecting.QUIT_NOW;
			    break;
		    }
		}

		if (expecting == Expecting.QUIT_NOW)
		    return;
	    }
 
	    switch (expecting) {
		case DEFAULT:
		    break;
		case ARGUMENTS:
		    if (argValues.size() > 0)
			break;
		    // else fall through to error
		default:
		    Intl.errFormat("calc#noOptionValue", expecting);
	    }
	}

	public static void main(String[] args) {
	    Environment.loadMainProgramInfo();

	    // Preload the color values for the initial errors
	    computeColors(colors);

	    final List<String> argList = new ArrayList<>(args.length * 2);
	    argValues.clear();

	    // Preprocess the command line arguments to see if we should process
	    // the environment options at all
	    if (Options.allowEnvironmentOptions(args, true)) {
		// Preprocess the CALC_OPTIONS environment variable (if present)
		Options.processEnvironmentOptions(Calc.class, options -> {
		    processArgs(options, argList);

		    switch (expecting) {
			case QUIT_NOW:
			    System.exit(0);
			case DEFAULT:
			case ARGUMENTS:
			    break;
			default:
			    System.exit(1);
		    }
		});
	    }

	    // Now process the command line (options will override the env var)
	    processArgs(args, argList);

	    switch (expecting) {
		case QUIT_NOW:
		    return;
		case DEFAULT:
		case ARGUMENTS:
		    break;
		default:
		    System.exit(1);
	    }

	    args = argList.toArray(new String[0]);

	    if (locale != null && !locale.equals(Locale.getDefault())) {
		Locale.setDefault(locale);
		Intl.initAllPackageResources(locale);
	    }

	    Object exitValue = null;

	    try {
		CharStream input = null;

		if (args.length == 1) {
		    if (args[0].charAt(0) == '@') {
			if (args[0].equals("@")) {
			    input = CharStreams.fromStream(System.in);
			}
			else {
			    input = CharStreams.fromString(getFileContents(args[0].substring(1)));
			}
		    }
		    else {
			input = CharStreams.fromString(getFileContents(args[0]));
		    }
		}
		else if (args.length > 0) {
		    String commandLine = CharUtil.makeSimpleStringList(args, ' ');
		    input = CharStreams.fromString(getFileContents(commandLine));
		}

		if (guiMode) {
		    if (outputFile != null || errorFile != null || outputCharsetName != null) {
			Intl.errPrintln("calc#noOutputGuiMode");
			exitValue = "95";
		    }
		    else {
			if (input != null)
			    inputText = input.toString();

			if (clearPrefs) {
			    try {
				// Also clear the window preferences setup by Pivot (same code as DesktopApplicationContext)
				// before instantiating the main window
				Preferences windowNode = Preferences.userNodeForPackage(DesktopApplicationContext.class);
				windowNode = windowNode.node(Calc.class.getName());
				windowNode.clear();
			    }
			    catch (BackingStoreException bse) {
				// Going to ignore this (same as other places) because it makes no difference, really
			    }
			}

			DesktopApplicationContext.main(Calc.class);
		    }
		}
		else {
		    if (outputFile != null || errorFile != null)
			displayer = new FileDisplayer(outputFile, errorFile, outputCharsetName);
		    else
			displayer = new ConsoleDisplayer();

		    visitor = new CalcObjectVisitor(displayer, rational, separators, silenceDirectives, ignoreCase, quotes, sortKeys);

		    // In case there are version requirements for libraries or variables,
		    // check the values set by "-requires", etc.
		    // The check method will throw if there is a problem.
		    if (requiredVersion != null || baseRequiredVersion != null) {
			checkRequiredVersions(requiredVersion, baseRequiredVersion);
		    }

		    // Set the command-line arguments into the symbol table as $nn
		    int index = 0;
		    for (String argument : argValues) {
			visitor.setArgument(index++, argument);
		    }

		    // Set the variables defined on the command line
		    for (java.util.Map.Entry<String, String> var : variables.entrySet()) {
			visitor.setVariable(var.getKey(), var.getValue());
		    }

		    // Try to read and process any given libraries before doing anything else
		    // But save the "-inputdir" setting and restore once we're done
		    File savedInputDirectory = inputDirectory;
		    readAndProcessLibraries(visitor, errorStrategy);
		    inputDirectory = savedInputDirectory;

		    // If no input arguments were given, go into "REPL" mode, reading
		    // a line at a time from the console and processing
		    if (input == null) {
			if (console == null || System.in.available() > 0) {
			    process(CharStreams.fromStream(System.in), visitor, errorStrategy, quiet, true);
			}
			else {
			    if (!noIntro) {
				printIntro();
			    }

			    replMode = true;

			    StringBuilder buf = new StringBuilder();
			    String line;
			    String prompt = ConsoleColor.color("<Bk!>> <.>");
			replLoop:
			    while ((line = console.readLine(prompt)) != null) {
				if (line.endsWith("\\")) {
				    buf.append(line.substring(0, line.length() - 1)).append(LINESEP);
				}
				else {
				    buf.append(line);
				    process(CharStreams.fromString(buf.toString()), visitor, errorStrategy, quiet, false);
				    buf.setLength(0);
				}
			    }

			    if (line == null)
				System.out.println();

			    replMode = false;
			}
		    }
		    else {
			process(input, visitor, errorStrategy, quiet, true);
		    }
		}
	    }
	    catch (CalcExprException cee) {
		displayer.displayErrorMessage(Intl.formatString("calc#argError", cee.getMessage()), cee.getLine());
		exitValue = "98";
	    }
	    catch (CalcException ce) {
		displayer.displayErrorMessage(Intl.formatString("calc#argError", ce.getMessage()), ce.getLine());
		exitValue = "99";
	    }
	    catch (IllegalArgumentException | IllegalStateException | IndexOutOfBoundsException ex) {
		displayer.displayErrorMessage(Intl.formatString("calc#argError", Exceptions.toString(ex)));
		exitValue = "97";
	    }
	    catch (LeaveException lex) {
		if (lex.hasValue()) {
		    exitValue = lex.getValue();
		}
	    }
	    catch (IOException ioe) {
		Intl.errFormat("calc#inOutError", Exceptions.toString(ioe));
		exitValue = "96";
	    }
	    catch (AssertException ae) {
		displayer.displayErrorMessage(Intl.formatString("calc#argError", ae.getMessage()), ae.getContext().getStart().getLine());
		exitValue = "95";
	    }

	    if (exitValue != null) {
		short exitCode = 0;
		try {
		    exitCode = Short.parseShort(exitValue.toString());
		}
		catch (NumberFormatException nfe) {
		    ;
		}
		if (exitCode != 0) {
		    System.exit(exitCode);
		}
	    }
	}

	public Calc() {
	    System.setProperty("org.apache.pivot.wtk.skin.terra.location", "/info/rlwhitcomb/TerraTheme_old.json");
	}

}


