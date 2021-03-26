/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017,2020-2021 Roger L. Whitcomb.
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
 *	Custom Cipher stream that has different behavior around reads such
 *	that we read in full cipher chunks and decrypt at once, to be doled
 *	out to the individual "read" methods as required.
 *
 *  History:
 *	25-Aug-2017 (rlwhitcomb)
 *	    First version with just the framework.
 *	28-Aug-2017 (rlwhitcomb)
 *	    Flesh out the internals.
 *	28-Aug-2017 (rlwhitcomb)
 *	    Fix problem with the way the "ensureNextChunk" method works.
 *	    Change to require DataStream as input so we can transmit/receive
 *	    a block size each time.  Reuse a max-sized block for the
 *	    encrypted bytes being read in each time.
 *	29-Aug-2017 (rlwhitcomb)
 *	    If the initial read in "ensureNextChunk" doesn't satisfy the
 *	    full block length, then keep reading until it does, because
 *	    the server wrote that much, so it must be split among TCP/IP
 *	    blocks.
 *	29-Aug-2017 (rlwhitcomb)
 *	    Take out most of the logging now that things are working.
 *	18-Jun-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	21-Dec-2020 (rlwhitcomb)
 *	    Update obsolete Javadoc constructs.
 *	26-Mar-2021 (rlwhitcomb)
 *	    Move some methods from NumericUtil to MathUtil.
 */
package info.rlwhitcomb.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.DataInputStream;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;


/**
 * A cipher stream that reads in full chunks encrypted by the sender and buffers
 * the results to be given to our callers as needed.
 */
public class CipherInputStream extends FilterInputStream
{
	/**
	 * The cipher (initialized in decrypt mode) that will be used to
	 * decrypt all incoming data.
	 */
	private Cipher decryptCipher;

	/**
	 * Reused buffer, sized to the max necessary, to read in the encrypted
	 * bytes before being sent to the cipher for decryption.
	 */
	private byte[] encryptedBytes = null;

	/**
	 * The next buffered and decrypted chunk of data that is available
	 * through the <code>"read"</code> methods.
	 */
	private byte[] availableBytes = null;
	/**
	 * The next position in the {@link #availableBytes} array.
	 */
	private int nextPos = 0;

	private Logging logger = new Logging(CipherInputStream.class);

	/**
	 * Construct a new cipher stream on top of an existing input stream
	 * with the given cipher (already initialized in decrypt mode).
	 *
	 * @param is The existing input stream to wrap with the cipher.
	 * @param cipher The decrypting cipher to use to retrieve the plaintext data.
	 */
	public CipherInputStream(DataInputStream is, Cipher cipher) {
	    super(is);
	    this.decryptCipher = cipher;
	    encryptedBytes = new byte[16384];	// Note: same size as byte buffer in CipherOutputStream
	    logger.debug("Construct on top of %1$s with %2$s cipher...", is.getClass().getSimpleName(), cipher.getAlgorithm());
	}

	@Override
	public int available()
		throws IOException
	{
	    try {
		int available = in.available();
		return available;
	    }
	    catch (IOException ex) {
		throw ex;
	    }
	}

	@Override
	public void close()
		throws IOException
	{
	    try {
		in.close();
	    }
	    catch (IOException ex) {
		throw ex;
	    }
	}

	@Override
	public void mark(int readLimit) {
	    in.mark(readLimit);
	}

	@Override
	public boolean markSupported() {
	    boolean supported = in.markSupported();
	    return supported;
	}

	private void ensureNextChunk()
		throws IOException
	{
	    if (availableBytes == null || nextPos >= availableBytes.length) {
		DataInputStream dis = (DataInputStream)in;
		int blockLen = dis.readInt();
		if (blockLen > encryptedBytes.length) {
		    encryptedBytes = new byte[MathUtil.roundUpPowerTwo(blockLen)];
		}
		int readLen = 0;
		// Make sure we read the whole encrypted chunk or we won't get proper decryption
		while (readLen != -1 && readLen < blockLen) {
		    int len = dis.read(encryptedBytes, readLen, (blockLen - readLen));
		    if (len == -1) {
			logger.debug("ensureNextChunk: at EOF (len==-1), read=%1$d, block=%2$d", readLen, blockLen);
			availableBytes = null;
			readLen = -1;  // to terminate the loop
		    }
		    else {
			readLen += len;
		    }
		}
		if (readLen != -1) {
		    try {
			availableBytes = decryptCipher.doFinal(encryptedBytes, 0, readLen);
		    }
		    catch (IllegalBlockSizeException | BadPaddingException ex) {
			// Not likely to happen, but we need to know about it (that is, it will be a programmer error)
			logger.except("ensureNextChunk method", ex);
			availableBytes = null;
		    }
		}
		nextPos = 0;
	    }
	}

	@Override
	public int read()
		throws IOException
	{
	    try {
		ensureNextChunk();
		if (availableBytes == null || nextPos >= availableBytes.length) {
		    return -1;
		}
		return (int)availableBytes[nextPos++];
	    }
	    catch (IOException ex) {
		throw ex;
	    }
	}

	@Override
	public int read(byte[] b)
		throws IOException
	{
	    try {
		return read(b, 0, b.length);
	    }
	    catch (IOException ex) {
		throw ex;
	    }
	}

	@Override
	public int read(byte[] b, int off, int len)
		throws IOException
	{
	    try {
		ensureNextChunk();
		if (availableBytes == null || nextPos >= availableBytes.length) {
		    return -1;
		}
		int outLen = 0;
		while (outLen < len && nextPos < availableBytes.length) {
		    b[off + outLen++] = availableBytes[nextPos++];
		}
		return outLen;
	    }
	    catch (IOException ex) {
		throw ex;
	    }
	}

	@Override
	public void reset()
		throws IOException
	{
	    try {
		in.reset();
	    }
	    catch (IOException ex) {
		throw ex;
	    }
	}

	@Override
	public long skip(long n)
		throws IOException
	{
	    try {
		long skipped = in.skip(n);
		return skipped;
	    }
	    catch (IOException ex) {
		throw ex;
	    }
	}

}

