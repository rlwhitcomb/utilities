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
 *
 *  Input Stream that counts the bytes transferred.
 *
 * History:
 *	22-Jul-2011 (rlwhitcomb)
 *	    Initial coding for use with ClientStatistics.
 *	22-Oct-2015 (rlwhitcomb)
 *	    Address "lint" issues.
 *	16-Apr-2020 (rlwhitcomb)
 *	    Cleanup for GitHub.
 */
package info.rlwhitcomb.util;

import java.io.*;


/**
 * In order to generate reasonably accurate statistics for client
 * sessions we need a way to accurately count the bytes transferred.
 * This class and the complementary {@link CountedOutputStream} class
 * provide this capability.  They defer to the underlying stream
 * for all operations, but count the bytes transferred in the
 * associated {@link ClientStatistics} object as they go by.
 */
public class CountedInputStream extends InputStream
{
	/** The object used to accumulate the bytes transferred. */
	private ClientStatistics statObj;
	/** The underlying {@link InputStream} we are wrapping. */
	private InputStream is;

	/**
	 * The only useful constructor.
	 *
	 * @param	is	The underlying InputStream we are wrapping.
	 * @param	statObj	The object for counting statistics for this session.
	 */
	public CountedInputStream(InputStream is, ClientStatistics statObj) {
	    super();
	    this.is = is;
	    this.statObj = statObj;
	}

	@Override
	public int available()
		throws IOException
	{
	    return is.available();
	}

	@Override
	public void close()
		throws IOException
	{
	    is.close();
	    is = null;
	}

	@Override
	public long skip(long n)
		throws IOException
	{
	    long num = is.skip(n);
	    statObj.addToBytes(num, true);
	    return num;
	}

	@Override
	public int read()
		throws IOException
	{
	    int i = is.read();
	    statObj.addToBytes(1, true);
	    return i;
	}

	@Override
	public int read(byte[] b)
		throws IOException
	{
	    int num = is.read(b);
	    if (num != -1)
		statObj.addToBytes(num, true);
	    return num;
	}

	@Override
	public int read(byte[] b, int off, int len)
		throws IOException
	{
	    int num = is.read(b, off, len);
	    if (num != -1)
		statObj.addToBytes(num, true);
	    return num;
	}

}


