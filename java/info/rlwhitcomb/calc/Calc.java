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
 */
package info.rlwhitcomb.calc;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

	private static boolean guiMode = false;
	private static boolean debug   = false;
	private static boolean colors  = true;

	private BXMLSerializer serializer = null;

	private Display display;

	@BXML private Window mainWindow;
	@BXML private TextArea inputTextArea;
	@BXML private TextArea outputTextArea;
	@BXML private Prompt helpPrompt;

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

	@Override
	public void displayResult(String exprString, String resultString) {
	    System.out.println(exprString + " -> " + resultString);
	}

	@Override
	public void displayActionMessage(String message) {
	    System.out.println(message);
	}

	@Override
	public void displayMessage(String message) {
	    System.out.println(message);
	}

	@Override
	public void displayErrorMessage(String message) {
	    System.err.println(message);
	}

	@Override
	public void displayErrorMessage(String message, int lineNumber) {
	    System.err.println(String.format("%1$s at line %2$d.", message, lineNumber));
	}


	public static void setDebugMode(boolean mode) {
	    debug = mode;
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
			System.out.println(EXPR_COLOR + exprString + ARROW_COLOR + " -> " + VALUE_COLOR + resultString + RESET);
		    else
			System.out.println(exprString + " -> " + resultString);
		}

		@Override
		public void displayActionMessage(String message) {
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
		    String exprText = inputTextArea.getText();

		    processString(exprText, false);

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

	public static Object processString(String inputText, boolean silent) {
	    try {
		return process(CharStreams.fromString(inputText + LINESEP), visitor, errorStrategy, silent);
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
	    boolean oldSilent = visitor.setSilent(silent);

	    try {
		CalcLexer lexer = new CalcBailLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CalcParser parser = new CalcParser(tokens);
		parser.setErrorHandler(errorStrategy);
		ParseTree tree = parser.prog();

		if (debug) {
		    System.out.println(tree.toStringTree(parser));
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
		visitor.setSilent(oldSilent);
	    }

	    return returnValue;
	}

	private static boolean processOption(String arg, String option) {
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
		case "d":
		    debug = true;
		    break;
		case "nodebug":
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
		    colors = false;
		    break;
		case "help":
		case "h":
		case "?":
		    printIntro();
		    printHelp();
		    return false;
		case "version":
		case "vers":
		case "ver":
		case "v":
		    printTitleAndVersion();
		    return false;
		default:
		    System.err.println("Unknown option \"" + arg + "\"; ignoring.");
		    break;
	    }
	    return true;
	}

	public static void main(String[] args) {
	    Environment.loadProgramInfo(Calc.class);

	    List<String> argList = new ArrayList<>(args.length);

	    // Scan the input arguments for the "-gui" option, removing it if found
	    for (String arg : args) {
		boolean optionOnly = false;

		if (arg.startsWith("--"))
		    optionOnly = !processOption(arg, arg.substring(2));
		else if (arg.startsWith("-"))
		    optionOnly = !processOption(arg, arg.substring(1));
		else if (ON_WINDOWS && arg.startsWith("/"))
		    optionOnly = !processOption(arg, arg.substring(1));
		else
		    argList.add(arg);

		// For some "options" (like "-help") we just quit once we
		// process them.
		if (optionOnly)
		    return;
	    }
 
	    args = argList.toArray(new String[0]);

	    try {
		CharStream input = null;

		if (args.length == 1) {
		    if (args[0].charAt(0) == '@') {
			if (args[0].equals("@")) {
			    input = CharStreams.fromStream(System.in);
			}
			else {
			    input = CharStreams.fromFileName(args[0].substring(1));
			}
		    }
		    else {
			File f = new File(args[0]);
			if (f.exists() && f.isFile() && f.canRead()) {
			    input = CharStreams.fromFileName(args[0]);
			}
			else {
			    input = CharStreams.fromString(concatArgs(args));
			}
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
			Console console = System.console();
			if (console == null) {
			    process(CharStreams.fromStream(System.in), visitor, errorStrategy, false);
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
					exit();
					break;
				    case "?":
				    case "help":
					printIntro();
					printHelp();
					break;
				    case "version":
					printTitleAndVersion();
					break;
				    case "gui":
					DesktopApplicationContext.main(Calc.class, args);
					break replLoop;
				    default:
					process(CharStreams.fromString(line + LINESEP), visitor, errorStrategy, false);
					break;
				}
			    }
			}
		    }
		    else {
			process(input, visitor, errorStrategy, false);
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


