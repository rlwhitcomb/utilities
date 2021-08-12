/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2021 Roger L. Whitcomb.
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
 *	    Fix one place that needed ExceptionUtil to get a nicer error message.
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
 */
package info.rlwhitcomb.calc;

import java.awt.Desktop;
import java.awt.Font;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.jar.JarFile;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.collections.Sequence;
import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.*;
import org.apache.pivot.wtk.Keyboard.KeyStroke;
import org.apache.pivot.wtk.text.Document;
import org.apache.pivot.wtk.util.TextAreaOutputStream;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import info.rlwhitcomb.IntlProvider;
import info.rlwhitcomb.jarfile.Launcher;
import info.rlwhitcomb.calc.CalcObjectVisitor.Settings;
import info.rlwhitcomb.calc.CalcObjectVisitor.TrigMode;
import info.rlwhitcomb.util.BigFraction;
import info.rlwhitcomb.util.CharUtil;
import info.rlwhitcomb.util.ClassUtil;
import info.rlwhitcomb.util.ConsoleColor;
import static info.rlwhitcomb.util.ConsoleColor.Code;
import static info.rlwhitcomb.util.ConsoleColor.Code.*;
import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.ExceptionUtil;
import info.rlwhitcomb.util.FileUtilities;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.NumericUtil.RangeMode;
import info.rlwhitcomb.util.QueuedThread;

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

	/**
	 * An enumeration of what we expect next on the command line.
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
		LIBRARY
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
		NEW_MATH_CONTEXT
	}


	private static boolean noIntro     = false;
	private static boolean guiMode     = false;
	private static boolean replMode    = false;
	private static boolean debug       = false;
	private static boolean colors      = true;
	private static boolean timing      = false;
	private static boolean resultsOnly = false;
	private static boolean quiet       = false;
	private static boolean rational    = false;
	private static boolean separators  = false;
	private static boolean ignoreCase  = false;

	private static boolean useCmdEnter = true;

	private static Locale  locale  = null;

	private BXMLSerializer serializer = null;

	private static File inputDirectory = null;
	private static File rootDirectory = null;
	private static List<String> libraryNames = null;

	private Display display;

	@BXML private Window mainWindow;
	@BXML private TextPane inputTextPane;
	@BXML private TextArea outputTextArea;
	@BXML private NumberRuler inputRuler;
	@BXML private NumberRuler outputRuler;
	@BXML private Label outputSizeLabel;
	@BXML private PushButton versionButton;
	@BXML private Label versionKeyLabel;
	@BXML private Prompt versionPrompt;
	@BXML private Label versionText;
	@BXML private Label buildText;
	@BXML private Label copyrightText;
	@BXML private Label javaText;
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
	@BXML private RadioButton binaryModeButton;
	@BXML private RadioButton siModeButton;
	@BXML private RadioButton mixedModeButton;
	@BXML private Checkbox rationalCheck;
	@BXML private Checkbox timingCheck;
	@BXML private Checkbox debugCheck;
	@BXML private Checkbox quietCheck;
	@BXML private Checkbox resultsCheck;
	@BXML private Checkbox separatorCheck;
	@BXML private RadioButton useEnterButton;
	@BXML private RadioButton useCmdEnterButton;
	@BXML private RadioButton lightBackgroundButton;
	@BXML private RadioButton darkBackgroundButton;


	/** The background worker thread to do the calculations in GUI mode. */
	private QueuedThread queuedThread = new QueuedThread();

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
	private static HashMap<String, Code> colorMap = new HashMap<>();


	private static void computeColors() {
	    colorMap.put("x", (darkBackgrounds ? CYAN_BRIGHT : BLUE_BOLD));
	    colorMap.put("a", (darkBackgrounds ? WHITE : BLACK_BRIGHT));
	    colorMap.put("v", (darkBackgrounds ? GREEN_BRIGHT : GREEN_BOLD));
	    colorMap.put("e", RED_BOLD);
	    colorMap.put("r", RESET);
	}

	private static String renderColors(String decoratedString) {
	    return ConsoleColor.color(decoratedString, colors, colorMap);
	}

	private static void outFormat(String formatKey, Object... args) {
	    System.out.println(renderColors(Intl.formatString(formatKey, args)));
	}

	private static void errFormat(String formatKey, Object... args) {
	    System.err.println(renderColors(Intl.formatString(formatKey, args)));
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
			    Action.performAction("calculate", comp);
			    return true;
			}
			else if (!useCmdEnter && !Keyboard.areAnyPressed(Keyboard.Modifier.ALL_MODIFIERS)) {
			    Action.performAction("calculate", comp);
			    return true;
			}
		    }
		    // Otherwise key was not consumed
		    return false;
		}
	}

	private KeyPressListener keyPressListener = new KeyPressListener();

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
		mcNew = new MathContext(100);
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
	    Component focusComponent = settingsForm;

	    // TODO: we really should do this view load/store and data bind mappings

	    dialog.setAttribute(Attribute.ORIGINAL_SETTINGS, oldSettings);
	    dialog.setAttribute(Attribute.ORIGINAL_MATH_CONTEXT, mc);
	    dialog.setAttribute(Attribute.NEW_MATH_CONTEXT, mc);

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
	    resultsCheck.setSelected(resultsOnly);
	    separatorCheck.setSelected(settings.separatorMode);

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

		TrigMode newTrigMode = TrigMode.RADIANS;
		if (degreesModeButton.isSelected())
		    newTrigMode = TrigMode.DEGREES;
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

		boolean newResults = resultsCheck.isSelected();
		if (newResults != resultsOnly)
		    setResultsOnlyMode(newResults);

		boolean newSeparators = separatorCheck.isSelected();
		if (newSeparators != originalSettings.separatorMode)
		    visitor.setSeparatorMode(newSeparators);

		useCmdEnter = useCmdEnterButton.isSelected();

		darkBackgrounds = darkBackgroundButton.isSelected();
		computeColors();
	    }

	    dialog.setAttribute(Attribute.ORIGINAL_SETTINGS, null);
	    dialog.setAttribute(Attribute.ORIGINAL_MATH_CONTEXT, null);
	    dialog.setAttribute(Attribute.NEW_MATH_CONTEXT, null);

	    requestFocus(inputTextPane);
	}

	@Override
	public void startup(Display display, Map<String, String> properties) {
	    this.display = display;

	    try {
		Action.addNamedAction("help",      new HelpAction());
		Action.addNamedAction("version",   new VersionAction());
		Action.addNamedAction("settings",  new SettingsAction());
		Action.addNamedAction("open",      new OpenAction());
		Action.addNamedAction("clear",     new ClearAction());
		Action.addNamedAction("calculate", new CalculateAction());
		Action.addNamedAction("exit",      new ExitAction());

		IntlProvider provider = new IntlProvider(getClass());
		Intl.initResources(provider);
		serializer = new BXMLSerializer();
		serializer.readObject(getClass().getResource("calc.bxml"), provider.getResources());
		serializer.bind(this);

		// To implement the displayer, redirect System.out to our TextArea for display
		PrintStream ps = new TextAreaOutputStream(outputTextArea, 2048).toPrintStream();
		System.setOut(ps);
		System.setErr(ps);

		// For now, we won't support colors in the GUI display
		// TODO: if we ever support going back from GUI mode to console, we will need to restore
		// the old "colors" mode at that point
		colors = false;

		// Increase the maximum output text length in case of humongous calculations
		outputTextArea.setMaximumLength(20_000_000);

		sizeFormat = NumberFormat.getIntegerInstance();
		sizeFormat.setGroupingUsed(true);

		Font monospacedFont = FontUtilities.decode(FontUtilities.MONOSPACED_FONTS + "-18");
		inputTextPane.getStyles().put(Style.font, monospacedFont);
		outputTextArea.getStyles().put(Style.font, monospacedFont);
		inputRuler.getStyles().put(Style.font, monospacedFont);
		outputRuler.getStyles().put(Style.font, monospacedFont);

		inputTextPane.getComponentKeyListeners().add(keyPressListener);

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
		if (inputText != null)
		    inputTextPane.setText(inputText);
		else
		    inputTextPane.setText(EMPTY_TEXT);

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
		    public void sheetClosed(final Sheet sheet) {
			requestFocus(inputTextPane);
		    }
		});

		displayer = this;
		visitor = new CalcObjectVisitor(displayer, rational, separators, ignoreCase);

		// Try to read and process any given libraries before doing anything else
		readAndProcessLibraries(visitor, errorStrategy, quiet);

		mainWindow.open(display);
		requestFocus(inputTextPane);
	    }
	    catch (Throwable ex) {
		displayer.displayErrorMessage(ExceptionUtil.toString(ex));
	    }
	}

	private void updateOutputSize() {
	    ApplicationContext.queueCallback(() ->
		outputSizeLabel.setText(sizeFormat.format(outputTextArea.getCharacterCount()))
	    );
	}

	@Override
	public void displayResult(String exprString, String resultString) {
	    if (resultsOnly)
		outFormat("calc#resultOnly", resultString);
	    else
		outFormat("calc#result", exprString, resultString);
	    updateOutputSize();
	}

	@Override
	public void displayActionMessage(String message) {
	    if (!resultsOnly) {
		outFormat("calc#action", message);
		updateOutputSize();
	    }
	}

	@Override
	public void displayMessage(String message) {
	    outFormat("calc#message", message);
	    updateOutputSize();
	}

	private String stripLineEndings(String message) {
	    int endPos = message.length();
	    char ch;
	    while (--endPos > 0 && ((ch = message.charAt(endPos)) == '\n' || ch == '\r'))
		;
	    return message.substring(0, ++endPos);
	}

	private void displayInputTextAndIndicator() {
	    if (currentText != null) {
		outFormat("calc#resultOnly", stripLineEndings(currentText));
	    }
	    if (currentIndicator != null) {
		outFormat("calc#resultOnly", currentIndicator);
		currentIndicator = null;
	    }
	}

	@Override
	public void displayErrorMessage(String message) {
	    displayInputTextAndIndicator();
	    outFormat("calc#error", message);
	    updateOutputSize();
	}

	@Override
	public void displayErrorMessage(String message, int lineNumber) {
	    displayInputTextAndIndicator();
	    errFormat("calc#errorLine", message, lineNumber);
	    updateOutputSize();
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

	public static boolean setQuietMode(boolean mode) {
	    boolean oldMode = quiet;
	    quiet = mode;
	    visitor.setSilent(quiet);
	    return oldMode;
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
			System.out.println(currentIndicator);
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
			System.out.println(currentIndicator);
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
			outFormat("calc#resultOnly", resultString);
		    else
			outFormat("calc#result", exprString, resultString);
		}

		@Override
		public void displayActionMessage(String message) {
		    if (!resultsOnly)
			outFormat("calc#action", message);
		}

		@Override
		public void displayMessage(String message) {
		    if (message == null || message.isEmpty())
			System.out.println();
		    else
			outFormat("calc#message", message);
		}

		@Override
		public void displayErrorMessage(String message) {
		    errFormat("calc#error", message);
		}

		@Override
		public void displayErrorMessage(String message, int lineNumber) {
		    // We're going to add a period ourselves, so take it out if the
		    // underlying error already has one.
		    String regularMessage = (message.endsWith(".") ?
			message.substring(0, message.length() - 1) : message);

		    if (replMode)
			errFormat("calc#errorPeriod", regularMessage);
		    else
			errFormat("calc#errorLine", regularMessage, lineNumber);
		}
	}

	private class CalculateAction extends Action
	{
		@Override
		public void perform(Component source) {
		    final String exprText = inputTextPane.getText();
		    currentText = exprText;

		    queuedThread.submitWork(() -> {
			try {
			    processString(exprText, quiet);
			}
			finally {
			    ApplicationContext.queueCallback(() -> {
				currentText = null;
				inputTextPane.setText(EMPTY_TEXT);
				requestFocus(inputTextPane);
			    });
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
				String fileText = FileUtilities.readFileAsString(f); // need charset and tabwidth?
				if (selectedFiles.getLength() > 1) {
				    String filePath = f.getPath();
				    CharUtil.padToWidth(buf, "#", filePath.length() + 3, '-').append('\n');
				    buf.append("# ").append(filePath).append('\n');
				    CharUtil.padToWidth(buf, "#", filePath.length() + 3, '-').append("\n\n");
				}
				buf.append(fileText).append('\n');
			    }
			}
			catch (IOException ioe) {
			    Alert.alert(MessageType.ERROR, ExceptionUtil.toString(ioe), mainWindow);
			}
			inputTextPane.setText(buf.toString());
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

	public static void printTitleAndVersion() {
	    Environment.printProgramInfo(50, colors);
	}

	public static void printIntro() {
	    Intl.printHelp("calc#intro", colors, colorMap);
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
		System.err.println(ExceptionUtil.toString(ex));
	    }
	}

	/**
	 * Display the product version information for the GUI.
	 * <p> Note: this is the same information displayed by {@link Environment#printProgramInfo}.
	 */
	private void displayVersion() {
	    String productName = Environment.getProductName();
	    String versionInfo = Environment.getProductVersion();
	    String buildInfo   = Environment.getProductBuildDateTime();
	    String copyright   = Environment.getCopyrightNotice().replace("(c)", "\u00A9");;
	    String javaVersion = Environment.getJavaVersion();

	    versionPrompt.setMessage(productName);
	    versionText.setText(versionInfo);
	    buildText.setText(buildInfo);
	    copyrightText.setText(copyright);
	    javaText.setText(javaVersion);

	    versionPrompt.open(mainWindow);
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

	private static String concatArgs(String[] args) {
	    StringBuilder buf = new StringBuilder();
	    for (String arg : args) {
		buf.append(arg).append(' ');
	    }
	    buf.append(LINESEP);
	    return buf.toString();
	}

	private static void concatLines(StringBuilder buf, List<String> lines) {
	    for (String line : lines) {
		buf.append(line).append(LINESEP);
	    }
	}

	private static boolean readFile(File f, StringBuilder inputBuf)
		throws IOException
	{
	    if (f.exists() && f.isFile() && f.canRead()) {
		inputDirectory = f.getCanonicalFile().getParentFile();
		List<String> lines = Files.readAllLines(f.toPath());
		concatLines(inputBuf, lines);
		return true;
	    }
	    return false;
	}

	public static String getFileContents(String paths)
		throws IOException
	{
	    StringBuilder inputBuf = new StringBuilder();
	    String[] files = paths.split(",");
	    for (String file : files) {
		File f = new File(file);
		if (!readFile(f, inputBuf)) {
		    if (inputDirectory != null) {
			f = new File(inputDirectory, file);
			if (!readFile(f, inputBuf)) {
			    inputBuf.append(file).append(LINESEP);
			}
		    }
		    else {
			inputBuf.append(file).append(LINESEP);
		    }
		}
	    }
	    return inputBuf.toString();
	}

	public static Object processString(String inputText, boolean silent) {
	    try {
		String input = inputText.endsWith(LINESEP) ? inputText : inputText + LINESEP;
		return process(CharStreams.fromString(input), visitor, errorStrategy, silent);
	    }
	    catch (IOException ioe) {
		displayer.displayErrorMessage(Intl.formatString("calc#ioError", ExceptionUtil.toString(ioe)));
	    }
	    return null;
	}

	private static Object process(CharStream input, CalcObjectVisitor visitor, BailErrorStrategy errorStrategy, boolean silent)
		throws IOException
	{
	    Object returnValue = null;
	    boolean oldSilent  = visitor.setSilent(silent);
	    long startTime     = Environment.highResTimer();
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

		if (debug) {
		    displayer.displayMessage(tree.toStringTree(parser));
		}

		returnValue = visitor.visit(tree);
	    }
	    catch (IllegalArgumentException iae) {
		displayer.displayErrorMessage(Intl.formatString("calc#argError", ExceptionUtil.toString(iae)));
	    }
	    catch (CalcException ce) {
		displayer.displayErrorMessage(Intl.formatString("calc#argError", ce.getMessage()), ce.getLine());
	    }
	    finally {
		endTime = Environment.highResTimer();
		visitor.setSilent(oldSilent);
	    }

	    if (timing && !silent) {
		displayer.displayMessage(Intl.formatString("calc#timing",
			Environment.timerValueToSeconds(endTime - startTime)));
	    }

	    return returnValue;
	}

	private static void readAndProcessLibraries(CalcObjectVisitor visitor, BailErrorStrategy errorStrategy, boolean silent)
		throws IOException
	{
	    if (libraryNames != null) {
		for (String libraryName : libraryNames) {
		    process(CharStreams.fromString(getFileContents(libraryName)), visitor, errorStrategy, quiet);
		}
	    }
	}

	private static Expecting processOption(String arg, String option) {
	    if (expecting != Expecting.DEFAULT) {
		errFormat("calc#expectNotOption", expecting);
		return Expecting.QUIT_NOW;
	    }

	    switch (option.toLowerCase()) {
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
		case "no":
		    debug = false;
		    break;
		case "colors":
		case "color":
		case "col":
		    colors = true;
		    break;
		case "nocolors":
		case "nocolor":
		case "nocol":
		case "noc":
		    colors = false;
		    break;
		case "darkbackgrounds":
		case "darkbackground":
		case "darkback":
		case "darkbg":
		case "dark":
		case "dk":
		    darkBackgrounds = true;
		    computeColors();
		    break;
		case "lightbackgrounds":
		case "lightbackground":
		case "lightback":
		case "lightbg":
		case "light":
		case "lt":
		    darkBackgrounds = false;
		    computeColors();
		    break;
		case "timing":
		case "time":
		case "t":
		    timing = true;
		    break;
		case "notiming":
		case "notime":
		case "not":
		    timing = false;
		    break;
		case "resultsonly":
		case "resultonly":
		case "results":
		case "result":
		case "res":
		case "r":
		    resultsOnly = true;
		    break;
		case "quiet":
		case "q":
		    quiet = true;
		    break;
		case "rational":
		case "ration":
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
		case "ignorecase":
		case "ignore":
		case "caseinsensitive":
		case "insensitive":
		case "case":
		case "ign":
		case "ins":
		    ignoreCase = true;
		    break;
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
		    return Expecting.LIBRARY;
		case "cmdenter":
		case "cmd":
		    useCmdEnter = true;
		    break;
		case "enter":
		case "e":
		    useCmdEnter = false;
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
		    errFormat("calc#unknownOption", arg);
		    return Expecting.QUIT_NOW;
	    }
	    return Expecting.DEFAULT;
	}

	private static void processArgs(String[] args, List<String> argList) {
	    expecting = Expecting.DEFAULT;

	    for (String arg : args) {
		if (arg.startsWith("--"))
		    expecting = processOption(arg, arg.substring(2));
		else if (arg.startsWith("-"))
		    expecting = processOption(arg, arg.substring(1));
		else if (ON_WINDOWS && arg.startsWith("/"))
		    expecting = processOption(arg, arg.substring(1));
		else {
		    switch (expecting) {
			case DEFAULT:
			    argList.add(arg);
			    break;
			case QUIT_NOW:
			    break;
			case LOCALE:
			    locale = new Locale(arg);
			    expecting = Expecting.DEFAULT;
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
			default:
			    errFormat("calc#expectValue", expecting);
			    expecting = Expecting.QUIT_NOW;
			    break;
		    }
		}

		if (expecting == Expecting.QUIT_NOW)
		    return;
	    }
 
	    if (expecting != Expecting.DEFAULT) {
		errFormat("calc#noOptionValue", expecting);
	    }
	}

	public static void main(String[] args) {
	    Environment.loadProgramInfo(Calc.class);

	    // Preload the color values for the initial errors
	    computeColors();

	    List<String> argList = new ArrayList<>(args.length * 2);

	    // Preprocess the CALC_OPTIONS environment variable (if present)
	    String calcOptions = System.getenv("CALC_OPTIONS");
	    if (!CharUtil.isNullOrEmpty(calcOptions)) {
		String[] parts = calcOptions.split("[;,]\\s*|\\s+");
		processArgs(parts, argList);

		switch (expecting) {
		    case QUIT_NOW:
			return;
		    case DEFAULT:
			break;
		    default:
			System.exit(1);
		}
	    }

	    // Now process the command line (options will override the env var)
	    processArgs(args, argList);

	    switch (expecting) {
		case QUIT_NOW:
		    return;
		case DEFAULT:
		    break;
		default:
		    System.exit(1);
	    }

	    args = argList.toArray(new String[0]);

	    if (locale != null && !locale.equals(Locale.getDefault())) {
		Locale.setDefault(locale);
		Intl.initAllPackageResources(locale);
	    }

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
		    String commandLine = concatArgs(args);
		    input = CharStreams.fromString(commandLine);
		}

		if (guiMode) {
		    if (input != null)
			inputText = input.toString();
		    DesktopApplicationContext.main(Calc.class, new String[0]);
		}
		else {
		    displayer = new ConsoleDisplayer();
		    visitor = new CalcObjectVisitor(displayer, rational, separators, ignoreCase);

		    // Try to read and process any given libraries before doing anything else
		    readAndProcessLibraries(visitor, errorStrategy, quiet);

		    // If no input arguments were given, go into "REPL" mode, reading
		    // a line at a time from the console and processing
		    if (input == null) {
			if (console == null) {
			    process(CharStreams.fromStream(System.in), visitor, errorStrategy, quiet);
			}
			else {
			    if (!noIntro) {
				printIntro();
			    }

			    replMode = true;

			    StringBuilder buf = new StringBuilder();
			    String line;
			    String prompt = ConsoleColor.color("<Bk!>> <>");
			replLoop:
			    while ((line = console.readLine(prompt)) != null) {
				boolean scriptInput = false;
				if (buf.length() == 0) {
				    String cmd = line.trim().toLowerCase();
				    switch (cmd) {
					case "quit":
					case "exit":
					case ":quit":
					case ":exit":
					case ":q":
					case ":e":
					case ":x":
					    exit();
					    break;
					case "?":
					case "help":
					case ":?":
					case ":help":
					    printIntro();
					    displayHelp();
					    break;
					case "version":
					case ":version":
					case ":vers":
					case ":ver":
					case ":v":
					    printTitleAndVersion();
					    break;
					case "gui":
					case ":gui":
					case ":g":
					    DesktopApplicationContext.main(Calc.class, args);
					    break replLoop;
					default:
					    scriptInput = true;
					    break;
				    }
				}
				else {
				    scriptInput = true;
				}

				if (scriptInput) {
				    buf.append(line).append(LINESEP);
				    if (!line.endsWith("\\")) {
					process(CharStreams.fromString(buf.toString()), visitor, errorStrategy, quiet);
					buf.setLength(0);
				    }
				}
			    }

			    if (line == null)
				System.out.println();

			    replMode = false;
			}
		    }
		    else {
			process(input, visitor, errorStrategy, quiet);
		    }
		}
	    }
	    catch (IOException ioe) {
		errFormat("calc#inOutError", ExceptionUtil.toString(ioe));
	    }
	}

	public Calc() {
	    System.setProperty("org.apache.pivot.wtk.skin.terra.location", "/info/rlwhitcomb/TerraTheme_old.json");
	}

}


