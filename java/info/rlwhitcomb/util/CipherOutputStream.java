/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017,2020 Roger L. Whitcomb.
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
 *	A custom Cipher stream that encrypts in chunks and sends on "flush"
 *	calls to the receiver.
 *
 *  History:
 *	25-Aug-2017 (rlwhitcomb)
 *	    First version with just the framework.
 *	28-Aug-2017 (rlwhitcomb)
 *	    Flesh out the internals.
 *	28-Aug-2017 (rlwhitcomb)
 *	    Add logging to constructor; change to use DataStreams as input
 *	    so that we can transmit/receive a block size each time.
 *	29-Aug-2017 (rlwhitcomb)
 *	    Take out most of the logging now that things are working.
 *	18-Jun-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	09-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 */
package info.rlwhitcomb.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;


/**
 * A cipher stream that differs from the standard cipher streams in that
 * once the {@link #flush} call is made all the buffered bytes are encrypted
 * and sent to the underlying stream (without waiting for full cipher blocks).
 */
public class CipherOutputStream extends FilterOutputStream
{
	/** 
	 * The cipher (initialized in encrypt mode) that is used to process
	 * all the outgoing data.
	 */
	private Cipher encryptCipher;

	/**
	 * The output stream of bytes that have been written since the constructor
	 * or the last call to {@link #flush}.
	 */
	private ByteArrayOutputStream baos = null;

	Logging logger = new Logging(CipherOutputStream.class);


	public CipherOutputStream(DataOutputStream os, Cipher cipher) {
	    super(os);
	    this.encryptCipher = cipher;
	    baos = new ByteArrayOutputStream(16384);	// arbitrary size, but bigger than we anticipate needing
	    logger.debug("Construct on top of %1$s with %2$s cipher...", os.getClass().getSimpleName(), cipher.getAlgorithm());
	}

	@Override
	public void close()
		throws IOException
	{
	    try {
		flush();
		out.close();
	    }
	    catch (IOException ex) {
		throw ex;
	    }
	}

	@Override
	public void flush()
		throws IOException
	{
	    try {
		int size = baos.size();
		if (size != 0) {
		    byte[] dataBytes = baos.toByteArray();
		    try {
			byte[] encryptedBytes = encryptCipher.doFinal(dataBytes);
			DataOutputStream dos = (DataOutputStream)out;
			dos.writeInt(encryptedBytes.length);
			dos.write(encryptedBytes);
		    }
		    catch (IllegalBlockSizeException | BadPaddingException ex) {
			// These would be programmer errors!
			logger.except("flush method", ex);
		    }
		    baos.reset();
		}
		out.flush();
	    }
	    catch (IOException ex) {
		throw ex;
	    }
	}

	@Override
	public void write(byte[] b)
		throws IOException
	{
	    baos.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len)
		throws IOException
	{
	    baos.write(b, off, len);
	}

	@Override
	public void write(int b)
		throws IOException
	{
	    baos.write(b);
	}

}

