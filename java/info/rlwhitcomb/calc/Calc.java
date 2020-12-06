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
 */
package info.rlwhitcomb.calc;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

/**
 * Command line calculator, which will also read files or from stdin.
 */
public class Calc
{
	private static final String LINESEP = System.lineSeparator();

	public static class ParseException extends RuntimeException
	{
		public ParseException(String message) {
		    super(message);
		}

		public ParseException(Throwable cause) {
		    super(cause.getClass().getName(), cause);
		}

		public ParseException(String message, Throwable cause) {
		    super(message, cause);
		}
	}

	public static class BailErrorStrategy extends DefaultErrorStrategy
	{
		@Override
		public void recover(Parser recognizer, RecognitionException e) {
		    throw new ParseException(e);
		}
/*
		@Override
		public Token recoverInline(Parser recognizer)
			throws RecognitionException
		{
		    throw new ParseException(new InputMismatchException(recognizer));
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
		    throw new ParseException(e);
		}
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
	    CalcLexer lexer = new CalcBailLexer(input);
	    CommonTokenStream tokens = new CommonTokenStream(lexer);
	    CalcParser parser = new CalcParser(tokens);
	    parser.setErrorHandler(errorStrategy);
	    ParseTree tree = parser.prog();
	    visitor.visit(tree);
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
		System.err.println("I/O Error: " + ioe.getMessage());
	    }
	    catch (IllegalArgumentException iae) {
		System.err.println("Error: " + iae.getMessage());
	    }
	    catch (ParseException pe) {
		System.err.println("Error: " + pe.getMessage());
	    }
	}
}

