/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2011,2015,2020 Roger L. Whitcomb.
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
 *  Output Stream that counts the bytes transferred.
 *
 * History:
 *	22-Jul-2011 (rlwhitcomb)
 *	    Initial coding for use with ClientStatistics.
 *	22-Oct-2015 (rlwhitcomb)
 *	    Address "lint" issues.
 *	16-Apr-2020 (rlwhitcomb)
 *	    Cleanup and prepare for GitHub.
 */
package info.rlwhitcomb.util;

import java.io.*;


/**
 * In order to generate reasonably accurate statistics for client
 * sessions we need a way to accurately count the bytes transferred.
 * This class and the complementary {@link CountedInputStream} class
 * provide this capability.  They defer to the underlying stream
 * for all operations, but count the bytes transferred in the
 * associated {@link ClientStatistics} object as they go by.
 */
public class CountedOutputStream extends OutputStream
{
	/** The object used to accumulate the bytes transferred. */
	private ClientStatistics statObj;
	/** The underlying {@link OutputStream} we are wrapping. */
	private OutputStream os;

	/**
	 * The only useful constructor.
	 *
	 * @param	os	The underlying OutputStream we are wrapping.
	 * @param	statObj	The object for counting statistics for this session.
	 */
	public CountedOutputStream(OutputStream os, ClientStatistics statObj) {
	    super();
	    this.os = os;
	    this.statObj = statObj;
	}

	@Override
	public void close()
		throws IOException
	{
	    os.close();
	    os = null;
	}

	@Override
	public void flush()
		throws IOException
	{
	    os.flush();
	}

	@Override
	public void write(int b)
		throws IOException
	{
	    os.write(b);
	    statObj.addToBytes(1, false);
	}

	@Override
	public void write(byte[] b)
		throws IOException
	{
	    os.write(b);
	    statObj.addToBytes(b.length, false);
	}

	@Override
	public void write(byte[] b, int off, int len)
		throws IOException
	{
	    os.write(b, off, len);
	    statObj.addToBytes(len, false);
	}

}


