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
	    ""
	};

	private static final String[] HELP = {
	    ERROR_COLOR + "Help is not complete yet!  Check back later." + RESET
	};

	private static boolean guiMode = false;
	private static boolean debug   = false;

	private BXMLSerializer serializer = null;

	private Display display;

	@BXML private Window mainWindow;
	@BXML private TextArea inputTextArea;
	@BXML private TextArea outputTextArea;
	@BXML private Prompt helpPrompt;

	private static BailErrorStrategy errorStrategy = new BailErrorStrategy();
	private static CalcDisplayer displayer;
	private static CalcObjectVisitor visitor;


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
	    System.out.println(exprString + "-> " + resultString);
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
		    System.out.println(EXPR_COLOR + exprString + ARROW_COLOR + "-> " + VALUE_COLOR + resultString + RESET);
		}

		@Override
		public void displayActionMessage(String message) {
		    System.out.println(VALUE_COLOR + message + RESET);
		}

		@Override
		public void displayMessage(String message) {
		    System.out.println(ARROW_COLOR + message + RESET);
		}

		@Override
		public void displayErrorMessage(String message) {
		    System.err.println(ERROR_COLOR + message + RESET);
		}

		@Override
		public void displayErrorMessage(String message, int lineNumber) {
		    System.err.println(ERROR_COLOR + message + RESET + " at line " + lineNumber + ".");
		}
	}

	private class CalculateAction extends Action
	{
		@Override
		public void perform(Component source) {
		    String inputText = inputTextArea.getText();

		    try {
			process(CharStreams.fromString(inputText + LINESEP), visitor, errorStrategy);
		    }
		    catch (IOException ioe) {
			displayer.displayErrorMessage("I/O Error: " + ExceptionUtil.toString(ioe));
		    }

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
	    if (guiMode) {
		// TOOD: what to do here?
	    }
	    else {
		Environment.printProgramInfo();
	    }
	}

	public static void printIntro() {
	    Arrays.stream(INTRO).forEach(System.out::println);
	}

	public static void printHelp() {
	    Arrays.stream(HELP).forEach(System.out::println);
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

	private static void process(CharStream input, CalcObjectVisitor visitor, BailErrorStrategy errorStrategy)
		throws IOException
	{
	    try {
		CalcLexer lexer = new CalcBailLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CalcParser parser = new CalcParser(tokens);
		parser.setErrorHandler(errorStrategy);
		ParseTree tree = parser.prog();

		if (debug) {
		    System.out.println(tree.toStringTree(parser));
		}

		visitor.visit(tree);
	    }
	    catch (IllegalArgumentException iae) {
		displayer.displayErrorMessage("Error: " + iae.getMessage());
	    }
	    catch (CalcException ce) {
		displayer.displayErrorMessage("Error: " + ce.getMessage(), ce.getLine());
	    }
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
		default:
		    System.err.println("Unknown option \"" + arg + "\"; ignoring.");
		    break;
	    }
	    // For now, we are going to ignore unknown options, so always return true
	    return true;
	}

	public static void main(String[] args) {
	    Environment.loadProgramInfo(Calc.class);

	    List<String> argList = new ArrayList<>(args.length);

	    // Scan the input arguments for the "-gui" option, removing it if found
	    for (String arg : args) {
		if (arg.startsWith("--"))
		    processOption(arg, arg.substring(2));
		else if (arg.startsWith("-"))
		    processOption(arg, arg.substring(1));
		else if (ON_WINDOWS && arg.startsWith("/"))
		    processOption(arg, arg.substring(1));
		else
		    argList.add(arg);
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
		    DesktopApplicationContext.main(Calc.class, args);
		}
		else {
		    displayer = new ConsoleDisplayer();
		    visitor = new CalcObjectVisitor(displayer);

		    // If no input arguments were given, go into "REPL" mode, reading
		    // a line at a time from the console and processing
		    if (input == null) {
			Console console = System.console();
			if (console == null) {
			    process(CharStreams.fromStream(System.in), visitor, errorStrategy);
			}
			else {
			    printTitleAndVersion();
			    printIntro();

			    String line;
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
				    default:
					process(CharStreams.fromString(line + LINESEP), visitor, errorStrategy);
					break;
				}
			    }
			}
		    }
		    else {
			process(input, visitor, errorStrategy);
		    }
		}
	    }
	    catch (IOException ioe) {
		System.err.println(ERROR_COLOR + "I/O Error: " + ExceptionUtil.toString(ioe) + RESET);
	    }
	}

	public Calc() {
	    System.setProperty("org.apache.pivot.wtk.skin.terra.location", "/TerraTheme_old.json");

	}

}


