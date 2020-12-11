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
 *  History:
 *      11-Dec-2020 (rlwhitcomb)
 *	    Initial coding.
 */
package info.rlwhitcomb.calc;

import org.antlr.v4.runtime.*;

import static info.rlwhitcomb.util.ConsoleColor.Code.*;


/**
 * An exception thrown during calculation that includes the context for the
 * error so that line information can be displayed.
 */
public class CalcException extends RuntimeException
{
	private ParserRuleContext context;

	public CalcException(final String message, final ParserRuleContext ctx) {
	    super(message);
	    this.context = ctx;
	}

	public CalcException(final String message, final Throwable cause, final ParserRuleContext ctx) {
	    super(message, cause);
	    this.context = ctx;
	}

	public CalcException(final Throwable cause, final ParserRuleContext ctx) {
	    super(cause);
	    this.context = ctx;
	}

	@Override
	public String toString() {
	    String message = getLocalizedMessage();
	    int line       = context.getStart().getLine();

	    return String.format("%1$sError: %2$s%3$s at line %4$d.",
		Calc.ERROR_COLOR,
		message,
		RESET,
		line);
	}

}

