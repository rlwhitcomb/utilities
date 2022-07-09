/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016,2020,2022 Roger L. Whitcomb.
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
 *	An InputStream that also echoes its input to a secondary
 *	OutputStream.
 *
 *  History:
 *	03-Nov-2016 (rlwhitcomb)
 *	    Created.
 *	12-Oct-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	09-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 */
package info.rlwhitcomb.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An {@link InputStream} that reads from another input stream and echoes
 * everything that is read to an output stream.
 */
public class EchoInputStream extends InputStream
{
        private InputStream originalInputStream;
	private OutputStream outputStream;

	/**
	 * Construct one of these streams, specifying both the input stream to read
	 * and the output stream to echo to.
	 *
	 * @param iStream	The original input stream we will read from (can be {@link System#in})..
	 * @param oStream	The output stream to echo to (can be {@link System#out}).
	 */
	public EchoInputStream(InputStream iStream, OutputStream oStream) {
	    this.originalInputStream = iStream;
	    this.outputStream        = oStream;
	}

	@Override
	public int read()
		throws IOException
	{
	    int value = originalInputStream.read();
	    if (value != -1) {
		outputStream.write(value);
	    }
	    return value;
	}

	@Override
	public void close()
		throws IOException
	{
	    if (originalInputStream != null && originalInputStream != System.in) {
		originalInputStream.close();
	    }
	    originalInputStream = null;

	    if (outputStream != null) {
		outputStream.flush();
		if (outputStream != System.out) {
		    outputStream.close();
		}
	    }
	    outputStream = null;
	}

}
