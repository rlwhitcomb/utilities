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
 */
package info.rlwhitcomb.calc;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.wtk.*;
import org.apache.pivot.wtk.util.TextAreaOutputStream;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import static info.rlwhitcomb.util.ConsoleColor.Code.*;
import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.ExceptionUtil;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.QueuedThread;

/**
 * Command line calculator, which will also read files or from stdin.
 */
public class Calc
	implements Application, CalcDisplayer
{
	private static final boolean ON_WINDOWS = Environment.isWindows();

	public static final String EXPR_COLOR  = (ON_WINDOWS ? CYAN_BRIGHT : BLUE_BOLD).toString();
	public static final String ARROW_COLOR = (ON_WINDOWS ? WHITE : BLACK_BRIGHT).toString();
	public static final String VALUE_COLOR = (ON_WINDOWS ? GREEN_BRIGHT : GREEN_BOLD).toString();
	public static final String ERROR_COLOR = RED_BOLD.toString();

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
		LOCALE
	}

	/** What we're expecting next on the command line. */
	private static Expecting expecting;

	private static final String[] INTRO = {
	    "  Enter an expression (or multiple expressions separated by ';').",
	    "  Use '" + VALUE_COLOR + "help" + RESET
	  + "' or '" + VALUE_COLOR + "?" + RESET + "' for a list of supported functions.",
	    "  Enter '" + VALUE_COLOR + "quit" + RESET
	  + "' or '" + VALUE_COLOR + "exit" + RESET + "' to end.",
	    "  Enter '" + VALUE_COLOR + "gui" + RESET
	  + "' to enter GUI mode.",
	    ""
	};
	private static final String[] INTRO_NOCOLOR = {
	    "  Enter an expression (or multiple expressions separated by ';').",
	    "  Use 'help' or '?' for a list of supported functions.",
	    "  Enter 'quit' or 'exit' to end.",
	    "  Enter 'gui' to enter GUI mode.",
	    ""
	};

	private static final String[] HELP = {
	    ERROR_COLOR + "Help is not complete yet!  Check back later." + RESET
	};
	private static final String[] HELP_NOCOLOR = {
	    "Help is not complete yet!  Check back later."
	};

	private static boolean guiMode     = false;
	private static boolean debug       = false;
	private static boolean colors      = true;
	private static boolean timing      = false;
	private static boolean resultsOnly = false;
	private static boolean quiet       = false;

	private static Locale  locale  = null;

	private BXMLSerializer serializer = null;

	private static File inputDirectory = null;

	private Display display;

	@BXML private Window mainWindow;
	@BXML private TextArea inputTextArea;
	@BXML private TextArea outputTextArea;
	@BXML private Label outputSizeLabel;
	@BXML private Prompt helpPrompt;

	/** The background worker thread to do the calculations in GUI mode. */
	private QueuedThread queuedThread = new QueuedThread();

	private NumberFormat sizeFormat;

	private static Console console = System.console();

	private static BailErrorStrategy errorStrategy = new BailErrorStrategy();
	private static CalcDisplayer displayer;
	private static CalcObjectVisitor visitor;

	/** The text read from the command line or a file. */
	private static String inputText = null;


	@Override
	public void startup(Display display, Map<String, String> properties) {
	    this.display = display;

	    try {
		Action.getNamedActions().put("help", new HelpAction());
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

		// Increase the maximum output text length in case of humongous calculations
		outputTextArea.setMaximumLength(20_000_000);

		sizeFormat = NumberFormat.getIntegerInstance();
		sizeFormat.setGroupingUsed(true);

		// Prepopulate the text are with any text from the command line or input file
		if (inputText != null)
		    inputTextArea.setText(inputText);

		displayer = this;
		visitor = new CalcObjectVisitor(displayer);

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
		System.out.println(resultString);
	    else
		System.out.println(exprString + " -> " + resultString);
	    updateOutputSize();
	}

	@Override
	public void displayActionMessage(String message) {
	    if (!resultsOnly) {
		System.out.println(message);
		updateOutputSize();
	    }
	}

	@Override
	public void displayMessage(String message) {
	    System.out.println(message);
	    updateOutputSize();
	}

	@Override
	public void displayErrorMessage(String message) {
	    System.err.println(message);
	    updateOutputSize();
	}

	@Override
	public void displayErrorMessage(String message, int lineNumber) {
	    System.err.println(String.format("%1$s at line %2$d.", message, lineNumber));
	    updateOutputSize();
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
	 * A parser error strategy that abandons the parse without trying to recover.
	 */
	public static class BailErrorStrategy extends DefaultErrorStrategy
	{
		@Override
		public void recover(Parser recognizer, RecognitionException e) {
		    throw new CalcException(e, e.getOffendingToken().getLine());
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

	private static class ConsoleDisplayer implements CalcDisplayer
	{
		@Override
		public void displayResult(String exprString, String resultString) {
		    if (colors)
			if (resultsOnly)
			    System.out.println(VALUE_COLOR + resultString + RESET);
			else
			    System.out.println(EXPR_COLOR + exprString + ARROW_COLOR + " -> " + VALUE_COLOR + resultString + RESET);
		    else
			if (resultsOnly)
			    System.out.println(resultString);
			else
			    System.out.println(exprString + " -> " + resultString);
		}

		@Override
		public void displayActionMessage(String message) {
		    if (!resultsOnly)
			if (colors)
			    System.out.println(VALUE_COLOR + message + RESET);
			else
			    System.out.println(message);
		}

		@Override
		public void displayMessage(String message) {
		    if (message == null || message.isEmpty())
			System.out.println();
		    else if (colors)
			System.out.println(ARROW_COLOR + message + RESET);
		    else
			System.out.println(message);
		}

		@Override
		public void displayErrorMessage(String message) {
		    if (colors)
			System.err.println(ERROR_COLOR + message + RESET);
		    else
			System.err.println(message);
		}

		@Override
		public void displayErrorMessage(String message, int lineNumber) {
		    if (colors)
			System.err.println(ERROR_COLOR + message + RESET + " at line " + lineNumber + ".");
		    else
			System.err.println(message + " at line " + lineNumber + ".");
		}
	}

	private class CalculateAction extends Action
	{
		@Override
		public void perform(Component source) {
		    final String exprText = inputTextArea.getText();

		    queuedThread.submitWork(() -> processString(exprText, quiet) );

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
		    // TODO: integrate our console help text with the text in the bxml file
		    helpPrompt.open(mainWindow);
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
	    if (colors)
		Arrays.stream(INTRO).forEach(System.out::println);
	    else
		Arrays.stream(INTRO_NOCOLOR).forEach(System.out::println);
	}

	public static void printHelp() {
	    if (colors)
		Arrays.stream(HELP).forEach(System.out::println);
	    else
		Arrays.stream(HELP_NOCOLOR).forEach(System.out::println);
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
		displayer.displayErrorMessage("I/O Error: " + ExceptionUtil.toString(ioe));
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
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CalcParser parser = new CalcParser(tokens);
		parser.setErrorHandler(errorStrategy);
		ParseTree tree = parser.prog();

		if (debug) {
		    displayer.displayMessage(tree.toStringTree(parser));
		}

		returnValue = visitor.visit(tree);
	    }
	    catch (IllegalArgumentException iae) {
		displayer.displayErrorMessage("Error: " + iae.getMessage());
	    }
	    catch (CalcException ce) {
		displayer.displayErrorMessage("Error: " + ce.getMessage(), ce.getLine());
	    }
	    finally {
		endTime = Environment.highResTimer();
		visitor.setSilent(oldSilent);
	    }

	    if (timing && !silent) {
		displayer.displayMessage(String.format("Elapsed time %1$11.9f seconds.",
			Environment.timerValueToSeconds(endTime - startTime)));
	    }

	    return returnValue;
	}

	private static Expecting processOption(String arg, String option) {
	    if (expecting != Expecting.DEFAULT) {
		System.err.println("Expecting " + expecting + " value, not another option.");
		return Expecting.QUIT_NOW;
	    }

	    switch (option.toLowerCase()) {
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
		case "locale":
		case "loc":
		case "l":
		    return Expecting.LOCALE;
		case "help":
		case "h":
		case "?":
		    printIntro();
		    printHelp();
		    return Expecting.QUIT_NOW;
		case "version":
		case "vers":
		case "ver":
		case "v":
		    printTitleAndVersion();
		    return Expecting.QUIT_NOW;
		default:
		    System.err.println("Unknown option \"" + arg + "\"; ignoring.");
		    return Expecting.QUIT_NOW;
	    }
	    return Expecting.DEFAULT;
	}

	public static void main(String[] args) {
	    Environment.loadProgramInfo(Calc.class);

	    List<String> argList = new ArrayList<>(args.length);

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
			default:
			    System.err.println("Expecting " + expecting + " value.");
			    expecting = Expecting.QUIT_NOW;
			    break;
		    }
		}

		if (expecting == Expecting.QUIT_NOW)
		    return;
	    }
 
	    if (expecting != Expecting.DEFAULT) {
		System.err.println("Value for " + expecting + " option was not given.");
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
		    visitor = new CalcObjectVisitor(displayer);

		    // If no input arguments were given, go into "REPL" mode, reading
		    // a line at a time from the console and processing
		    if (input == null) {
			if (console == null) {
			    process(CharStreams.fromStream(System.in), visitor, errorStrategy, quiet);
			}
			else {
			    printTitleAndVersion();
			    printIntro();

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
					printHelp();
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
			}
		    }
		    else {
			process(input, visitor, errorStrategy, quiet);
		    }
		}
	    }
	    catch (IOException ioe) {
		if (colors)
		    System.err.println(ERROR_COLOR + "I/O Error: " + ExceptionUtil.toString(ioe) + RESET);
		else
		    System.err.println("I/O Error: " + ExceptionUtil.toString(ioe));
	    }
	}

	public Calc() {
	    System.setProperty("org.apache.pivot.wtk.skin.terra.location", "/TerraTheme_old.json");
	}

}


