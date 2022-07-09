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
 *	An error strategy that doesn't try to recover, but simply throws instead.
 *
 *  History:
 *      16-Feb-2022 (rlwhitcomb)
 *	    #196: Initial coding from other packages.
 *	08-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 */
package info.rlwhitcomb.json;

import info.rlwhitcomb.util.Intl;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;


/**
 * An error strategy that doesn't try to recover, but simply throws instead.
 */
public class JSONBailErrorStrategy extends DefaultErrorStrategy
{
	@Override
	public void recover(final Parser recognizer, final RecognitionException ex) {
	    Token t = ex.getOffendingToken();
	    int charPos = t.getCharPositionInLine();
	    throw new JSONException(Intl.formatString("json#errorNoAlt", charPos, t.getText()), t.getLine());
	}

/*
	@Override
	public Token recoverInline(final Parser recognizer)
		throw RecognitionException
	{
	    InputMismatchException ime = new InputMismatchException(recognizer);
	    throw new JSONException(ime, ime.getOffendingToken().getLine());
	}
*/

	@Override
	public void sync(final Parser recognizer) {
	}

}

