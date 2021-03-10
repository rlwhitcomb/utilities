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
 *  History:
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
import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.wtk.*;
import org.apache.pivot.wtk.util.TextAreaOutputStream;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import info.rlwhitcomb.jarfile.Launcher;
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
		DIRECTORY
	}

	/** What we're expecting next on the command line. */
	private static Expecting expecting;

	private static final String[] INTRO = {
	    "  Enter an expression (or multiple expressions separated by ';').",
	    "  Use '<v>help<r>' or '<v>?<r>' for a list of supported functions.",
	    "  Enter '<v>quit<r>' or '<v>exit<r>' to end.",
	    "  Enter '<v>gui<r>' to enter GUI mode.",
	    ""
	};


	private static boolean noIntro     = false;
	private static boolean guiMode     = false;
	private static boolean replMode    = false;
	private static boolean debug       = false;
	private static boolean colors      = true;
	private static boolean timing      = false;
	private static boolean resultsOnly = false;
	private static boolean quiet       = false;
	private static boolean rational    = false;

	private static Locale  locale  = null;

	private BXMLSerializer serializer = null;

	private static File inputDirectory = null;

	private Display display;

	@BXML private Window mainWindow;
	@BXML private TextArea inputTextArea;
	@BXML private TextArea outputTextArea;
	@BXML private Label outputSizeLabel;
	@BXML private Prompt versionPrompt;
	@BXML private Label versionText;
	@BXML private Label buildText;
	@BXML private Label copyrightText;
	@BXML private Label javaText;


	/** The background worker thread to do the calculations in GUI mode. */
	private QueuedThread queuedThread = new QueuedThread();

	private NumberFormat sizeFormat;

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
	 * Listen for key press events inside the input {@link TextArea} and respond.
	 * <p> Primarily to allow a keystroke to initiate calculation while typing.
	 */
	private static class KeyPressListener implements ComponentKeyListener
	{
		@Override
		public boolean keyPressed(Component comp, int keyCode, Keyboard.KeyLocation keyLocation) {
		    if (keyCode == Keyboard.KeyCode.ENTER && Keyboard.isCmdPressed()) {
			Action.getNamedActions().get("calculate").perform(comp);
// soon			Action.performAction("calculate", comp);
			return true;
		    }
		    // Otherwise key was not consumed
		    return false;
		}
	}

	private KeyPressListener keyPressListener = new KeyPressListener();


	@Override
	public void startup(Display display, Map<String, String> properties) {
	    this.display = display;

	    try {
		Action.getNamedActions().put("help", new HelpAction());
		Action.getNamedActions().put("version", new VersionAction());
		Action.getNamedActions().put("clear", new ClearAction());
		Action.getNamedActions().put("calculate", new CalculateAction());
		Action.getNamedActions().put("exit", new ExitAction());

		serializer = new BXMLSerializer();
		serializer.readObject(Calc.class, "calc.bxml");
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
		inputTextArea.getStyles().put(Style.font, monospacedFont);
		outputTextArea.getStyles().put(Style.font, monospacedFont);

		inputTextArea.getComponentKeyListeners().add(keyPressListener);

		// Prepopulate the text are with any text from the command line or input file
		if (inputText != null)
		    inputTextArea.setText(inputText);

		displayer = this;
		visitor = new CalcObjectVisitor(displayer, rational);

		mainWindow.open(display);
		inputTextArea.requestFocus();
	    }
	    catch (Throwable ex) {
		displayer.displayErrorMessage(ex.getMessage());
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

	@Override
	public void displayErrorMessage(String message) {
	    outFormat("calc#error", message);
	    updateOutputSize();
	}

	@Override
	public void displayErrorMessage(String message, int lineNumber) {
	    errFormat("calc#errorLine", message, lineNumber);
	    updateOutputSize();
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
		    if (replMode) {
			String indicator = CharUtil.makeStringOfChars(' ', charPositionInLine + 2);
			System.out.format("%1$s^%n", indicator);
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
		    if (replMode) {
			String indicator = CharUtil.makeStringOfChars(' ', charPos + 2);
			System.out.format("%1$s^%n", indicator);
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
		    if (replMode)
			errFormat("calc#errorPeriod", message);
		    else
			errFormat("calc#errorLine", message, lineNumber);
		}
	}

	private class CalculateAction extends Action
	{
		@Override
		public void perform(Component source) {
		    final String exprText = inputTextArea.getText();

		    queuedThread.submitWork(() -> processString(exprText, quiet));

		    inputTextArea.setText("");
		    inputTextArea.requestFocus();
		}

	}

	private class ClearAction extends Action
	{
		@Override
		public void perform(Component source) {
		    inputTextArea.setText("");
		    outputTextArea.setText("");
		    updateOutputSize();
		    inputTextArea.requestFocus();
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
	    String copyright   = Environment.getCopyrightNotice();
	    String javaVersion = Environment.getJavaVersion();

	    versionPrompt.setMessage(productName);
	    versionText.setText(versionInfo);
	    buildText.setText(buildInfo);
	    copyrightText.setText(copyright);
	    javaText.setText(javaVersion);

	    versionPrompt.open(mainWindow);
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
		case "locale":
		case "loc":
		case "l":
		    return Expecting.LOCALE;
		case "inputdir":
		case "input":
		case "dir":
		    return Expecting.DIRECTORY;
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
		    visitor = new CalcObjectVisitor(displayer, rational);

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

			    String line;
			replLoop:
			    while ((line = console.readLine("> ")) != null) {
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
					process(CharStreams.fromString(line + LINESEP), visitor, errorStrategy, quiet);
					break;
				}
			    }
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
	    System.setProperty("org.apache.pivot.wtk.skin.terra.location", "/TerraTheme_old.json");
	}

}


