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
 *	OutputStream that filters out CR (0x0D) characters to produce
 *	platform-independent output files.
 *
 * History:
 *	18-Feb-2022 (rlwhitcomb)
 *	    Initial coding for use with Tester.
 */
package info.rlwhitcomb.util;

import java.io.*;


/**
 * Some test programs generate different line endings on different
 * platforms, complicating the creation of simple "canon" files for
 * comparison. This class helps to deal with that by converting all
 * output to a Unix/Linux standard of just using linefeed (0x0A)
 * characters as the line terminators.
 * <p> The logic is all in the {@link #write(int)} method which
 * maintains a flag such that any CRs seen set the flag, which
 * will output the next char if it is a linefeed, otherwise it
 * will output a linefeed instead. Either way the flag is reset.
 */
public class NewlineOutputStream extends FilterOutputStream
{
	/** Flag to say the last character we saw was a CR (0x0D). */
	private boolean sawCR;

	private final static int CR = 0x0D;
	private final static int LF = 0x0A;

	/**
	 * The only useful constructor.
	 *
	 * @param	os	The underlying OutputStream we are wrapping.
	 */
	public NewlineOutputStream(final OutputStream os) {
	    super(os);
	    sawCR = false;
	}


	@Override
	public void write(final int b)
		throws IOException
	{
	    if (sawCR) {
		if (b == LF)
		    out.write(b);	// CR,LF -> LF
		else
		    out.write(LF);	// CR,xx -> LF

		sawCR = (b == CR);	// CR,CR -> LF,LF
	    }
	    else {
		if (b == CR)
		    sawCR = true;
		else
		    out.write(b);	// LF -> LF here
	    }
	}

}

