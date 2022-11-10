/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Roger L. Whitcomb.
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
 * History:
 *  09-Nov-22 rlw #550:	Initial coding.
 */
package info.rlwhitcomb.calc;

import org.antlr.v4.runtime.ParserRuleContext;


/**
 * An exception thrown from a failed ":assert" directive.
 */
public class AssertException extends RuntimeException
{
	/**
	 * Context where the assert failed.
	 */
	private ParserRuleContext context;


	/**
	 * Construct with the given message and context.
	 *
	 * @param message Message about the failed assertion.
	 * @param ctx     Location where the assert failed.
	 */
	public AssertException(final String message, final ParserRuleContext ctx) {
	    super(message);
	    context = ctx;
	}

	/**
	 * @return The context where the assert failed.
	 */
	public ParserRuleContext getContext() {
	    return context;
	}

}

