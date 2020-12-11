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
 */
package info.rlwhitcomb.calc;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import static info.rlwhitcomb.util.ConsoleColor.Code.*;
import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.ExceptionUtil;

/**
 * Command line calculator, which will also read files or from stdin.
 */
public class Calc
{
	private static final boolean ON_WINDOWS = Environment.isWindows();

	public static final String EXPR_COLOR  = (ON_WINDOWS ? CYAN_BRIGHT : BLUE_BOLD).toString();
	public static final String ARROW_COLOR = (ON_WINDOWS ? WHITE : BLACK_BRIGHT).toString();
	public static final String VALUE_COLOR = (ON_WINDOWS ? GREEN_BRIGHT : GREEN_BOLD).toString();
	public static final String ERROR_COLOR = RED_BOLD.toString();

	private static final String LINESEP = System.lineSeparator();

	private static final String VERSION = "0.95";

	private static final String[] TITLE_AND_VERSION = {
	    "",
	    BLUE_BOLD_BRIGHT +
	    "=======================",
	    " Expression Calculator",
	    "      Version " + VERSION,
	    "=======================",
	    "" + RESET
	};

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

	public static class ParseException extends RuntimeException
	{
		private int lineNumber;

		public ParseException(final String message, final int lineNo) {
		    super(message);
		    this.lineNumber = lineNo;
		}

		public ParseException(final Throwable cause, final int lineNo) {
		    super(cause.getClass().getName(), cause);
		    this.lineNumber = lineNo;
		}

		public ParseException(final String message, final Throwable cause, final int lineNo) {
		    super(message, cause);
		    this.lineNumber = lineNo;
		}

		@Override
		public String toString() {
		    String message = getLocalizedMessage();
		    return String.format("%1$sError: %2$s%3$s at line %4$d.",
			ERROR_COLOR,
			message,
			RESET,
			lineNumber);
		}
	}

	public static class BailErrorStrategy extends DefaultErrorStrategy
	{
		@Override
		public void recover(Parser recognizer, RecognitionException e) {
		    throw new ParseException(e, e.getOffendingToken().getLine());
		}
/*
		@Override
		public Token recoverInline(Parser recognizer)
			throws RecognitionException
		{
		    InputMismatchException ime = new InputMismatchException(recognizer);
		    throw new ParseException(ime, ime.getOffendingToken().getLine());
		}
*/
		@Override
		public void sync(Parser recognizer) { }
	}

	public static class CalcBailLexer extends CalcLexer
	{
		public CalcBailLexer(CharStream input) {
		    super(input);
		}

		@Override
		public void recover(LexerNoViableAltException e) {
		    throw new ParseException(e, getLine());
		}
	}

	public static void printTitleAndVersion() {
	    Arrays.stream(TITLE_AND_VERSION).forEach(System.out::println);
	}

	public static void printIntro() {
	    Arrays.stream(INTRO).forEach(System.out::println);
	}

	public static void printHelp() {
	    Arrays.stream(HELP).forEach(System.out::println);
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
		visitor.visit(tree);
	    }
	    catch (IllegalArgumentException iae) {
		System.err.println(ERROR_COLOR + "Error: " + iae.getMessage() + RESET);
	    }
	    catch (ParseException | CalcException e) {
		System.err.println(e.toString());
	    }
	}

	public static void main(String[] args) {
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

		BailErrorStrategy errorStrategy = new BailErrorStrategy();
		CalcObjectVisitor visitor = new CalcObjectVisitor();

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
			    process(CharStreams.fromString(line + LINESEP), visitor, errorStrategy);
			}
		    }
		}
		else {
		    process(input, visitor, errorStrategy);
		}
	    }
	    catch (IOException ioe) {
		System.err.println(ERROR_COLOR + "I/O Error: " + ExceptionUtil.toString(ioe) + RESET);
	    }
	}
}

