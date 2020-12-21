/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2010,2013-2014,2016,2020 Roger L. Whitcomb.
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
 *	Security utility methods: dealing with encryption, obfuscation, and hashing.
 *
 *  History:
 *	30-Jul-2010 (rlwhitcomb)
 *	    Initial coding.
 *	02-Dec-2013 (rlwhitcomb)
 *	    Conform the code to our coding conventions.  Remove
 *	    unused cipher code and associated imports.  Add class
 *	    Javadoc.
 *	13-Feb-2014 (rlwhitcomb)
 *	    New flavors of obfuscate and deobfuscate now that we're
 *	    really using them that deal directly with byte arrays.
 *	30-Sep-2014 (rlwhitcomb)
 *	    Make a new method to get an object token consisting of
 *	    a secure hash of the object's bytes plus a time stamp.
 *	    Add hex decode method (reverse of hex encode).
 *	06-Nov-2014 (rlwhitcomb)
 *	    Put error message strings into resource bundle.
 *	07-Jan-2016 (rlwhitcomb)
 *	    Fix Javadoc warnings found by Java 8.
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	21-Dec-2020 (rlwhitcomb)
 *	    Update obsolete Javadoc constructs.
 */
package info.rlwhitcomb.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import net.iharder.b64.Base64;


/**
 * Some miscellaneous static functions having to do with security (encryption, obfuscation,
 * hashing, etc.).
 */
public class SecurityUtil
{
	/** Prefix used to identify passwords and other values that have been obfuscated by our
	 * internal {@link #obfuscate} routine. */
	public static final String __OBFUSCATE = "OBF:";
	/** Prefix used to identify passwords that have been hashed by our secure hash function:
	 * {@link #hash}. */
	public static final String __HASH = "SHS:";

	/** Used to hex encode various binary values. */
	private static final char[] hexChars =
		{ '0', '1', '2', '3', '4', '5', '6', '7',
		  '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/** Use to decode hex strings into binary values. */
	private static final String hexCharString = new String(hexChars);

	/** Our hashing function. */
	private static MessageDigest __md = null;

	/**
	 * Get singleton instances of the required cryptographic stuff.
	 */
	static {
	    try {
		__md = MessageDigest.getInstance("SHA-384");

	    } catch( Exception e ) {
		Logging.Except("MessageDigest init", e);
	    }
	}

	/**
	 * Encodes a byte array into hex (base 16) notation.
	 * <p> Uses the {@link #hexChars} array to encode each nybble of the input.
	 *
	 * @param	input	an array of bytes to be encoded
	 * @return		The encoded string.
	 */
	public static String hexEncode(byte[] input) {
	    StringBuilder buf = new StringBuilder(input.length * 2);
	    for (byte b : input) {
		buf.append(hexChars[(b>>>4)&0xF]);
		buf.append(hexChars[b&0xF]);
	    }
	    return buf.toString();
	}


	/**
	 * Decodes a hex encoded string into the array of the corresponding bytes.
	 * <p> Uses the {@link #hexCharString} to decode each nybble of the input.
	 * Input values can start with "0x" or "0X" (which will be ignored).
	 *
	 * @param	input	a hex-encoded string of bytes (output from {@link #hexEncode})
	 * @return		the array of bytes
	 * @throws	IllegalArgumentException if input string is badly formed
	 */
	public static byte[] hexDecode(String input) {
	    // Ignore leading "0x" or "0X" prefix
	    if (input.startsWith("0x") || input.startsWith("0X")) {
		input = input.substring(2);
	    }
	    // Input string should be even length, otherwise error
	    int len = input.length();
	    if (len % 2 != 0)
		throw new IllegalArgumentException(Intl.getString("util#security.hexNotEven"));
	    byte[] result = new byte[len / 2];
	    for (int i = 0, j = 0; i < len; i += 2) {
		char ch0 = Character.toLowerCase(input.charAt(i));
		char ch1 = Character.toLowerCase(input.charAt(i + 1));
		int val0 = hexCharString.indexOf(ch0);
		int val1 = hexCharString.indexOf(ch1);
		if (val0 < 0 || val1 < 0)
		    throw new IllegalArgumentException(Intl.formatString("util#security.hexInvalidChar", ch0, ch1));
		int val = (val0 << 4) + val1;
		if (val >= 128) {
		    result[j++] = (byte)(val - 256);
		}
		else {
		    result[j++] = (byte)val;
		}
	    }
	    return result;
	}


	/**
	 * Obfuscate an array of bytes into something not readily readable by human beings.
	 * <p>
	 * Copied algorithm from org.mortbay.jetty.security.Password
	 *
	 * @param	b	set of bytes to be made unreadable
	 * @return		Reversibly obfuscated input identified as such
	 *			by the {@link #__OBFUSCATE} prefix.
	 */
	public static String obfuscateBytes(byte[] b) {
	    StringBuilder buf = new StringBuilder();
	    buf.append(__OBFUSCATE);
	    for (int i = 0; i < b.length; i++) {
		byte b1 = b[i];
		byte b2 = b[b.length - (i+1)];
		int i1 = 127 + b1 + b2;
		int i2 = 127 + b1 - b2;
		int i0 = i1 * 256 + i2;
		String x = Integer.toString(i0, 36);

		switch(x.length()) {
		    case 1: buf.append('0');
		    case 2: buf.append('0');
		    case 3: buf.append('0');
		    default: buf.append(x);
		}
	    }
	    return buf.toString();
	}

	/**
	 * Obfuscate a string into something not readily readable by human beings.
	 * <p> Uses {@link Base64#getStringBytes Base64.getStringBytes} to encode
	 * the string (which will generally mean UTF-8 encoding).
	 *
	 * @param	s	human-readable string to be made unreadable
	 * @return		Reversibly obfuscated input identified as such
	 *			by the {@link #__OBFUSCATE} prefix.
	 */
	public static String obfuscate(String s) {
	    return obfuscateBytes(Base64.getStringBytes(s));
	}

	/**
	 * Internal method to get the deobfuscated bytes from a string.
	 *
	 * @param	s	previously obfuscated string to be deobfuscated (usually
	 *			prefixed by the {@link #__OBFUSCATE} identifier)
	 * @return		The bytes of the original value.
	 */
	private static byte[] deobfuscateBytes(String s) {
	    if (s.startsWith(__OBFUSCATE))
		s = s.substring(__OBFUSCATE.length());

	    byte[] b = new byte[s.length() / 2];
	    int l = 0;
	    for (int i = 0; i < s.length(); i += 4) {
		String x = s.substring(i, i + 4);
		int i0 = Integer.parseInt(x, 36);
		int i1 = (i0 / 256);
		int i2 = (i0 % 256);
		b[l++] = (byte)((i1 + i2 - 254) / 2);
	    }
	    if (b.length == l)
		return b;
	    return Arrays.copyOf(b, l);
	}

	/**
	 * Deobfuscate a previously obfuscated string back into something readable.
	 * <p> Uses the {@link Base64#bytesToString(byte[],int,int)} method to render
	 * the final string, which means it will usually use UTF-8 encoding to interpret
	 * the bytes.
	 *
	 * @param	s	previously obfuscated string to be deobfuscated (usually
	 *			prefixed by the {@link #__OBFUSCATE} identifier)
	 * @return		Human-readable string.
	 */
	public static String deobfuscate(String s) {
	    return Base64.bytesToString(deobfuscateBytes(s));
	}

	/**
	 * Puts the password bytes into an unreadable (obfuscated) string.
	 *
	 * @param	b	Password bytes (UTF-8 encoded) (could be {@code null}
	 *			or empty).
	 * @return		An obfuscated string (prefixed by {@link #__OBFUSCATE})
	 *			or an empty string if the input bytes are {@code null} or empty.
	 */
	public static String putPasswordBytes(byte[] b) {
	    if (b == null || b.length == 0)
		return "";
	    return obfuscateBytes(b);
	}

	/**
	 * Get the password bytes (UTF-8) from a (possibly) obfuscated password string.
	 *
	 * @param	s	Potential input password string (could be {@code null} or
	 *			empty or obfuscated).
	 * @return	The UTF-8 bytes of the real password value.
	 */
	public static byte[] getPasswordBytes(String s) {
	    if (s == null || s.isEmpty())
		return null;
	    return deobfuscateBytes(s);
	}

	/**
	 * Compute a hashed version of the input string using {@link MessageDigest}.
	 * <p> The digest routines themselves are not thread-safe, so synchronize on
	 * the digest object, since we can potentially be called by multiple
	 * threads.
	 *
	 * @param	s	input value to be securely hashed
	 * @return		Secure hash of input using predetermined MessageDigest
	 *			algorithm (base-16 encoded).
	 */
	private static String hashValue(String s) {
	    synchronized(__md) {
		__md.reset();
		__md.update(Base64.getStringBytes(s));
		byte[] digest = __md.digest();
		return hexEncode(digest);
	    }
	}

	/**
	 * Return securely hashed value (including {@link #__HASH} prefix).
	 * <p> The hash function chosen by the static initializer is
	 * used, so that the most appropriate function can be
	 * chosen without affecting the code here.
	 *
	 * @param	s	input string
	 * @return		Hashed value with {@link #__HASH} prefix.
	 */
	public static String hash(String s) {
	    StringBuilder buf = new StringBuilder(__HASH);
	    buf.append(hashValue(s));
	    return buf.toString();
	}


	/**
	 * Read from the given {@link InputStream} until the delimiter char
	 * is found, then return the base-64 decoded value (assumes characters
	 * are in UTF-8 encoding).
	 *
	 * @param	is	InputStream to read from
	 * @param	delim	delimiter character to end the read
	 * @return		Base-64 decoded value read from the input.
	 *
	 * @throws	IOException if the stream has a problem.
	 */
	public static String read(InputStream is, int delim)
		throws IOException
	{
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    int byt = is.read();
	    while (byt > 0 && byt != delim) {
		bos.write(byt);
		byt = is.read();
	    }
	    return Base64.bytesToString(bos.toByteArray());
	}


	/**
	 * Read from the given {@link InputStream} until end of stream
	 * then return the base-64 decoded value of the input (assumes input
	 * in UTF-8 encoding).
	 *
	 * @param	is	InputStream to read from
	 * @return		Base-64 decoded value read from input.
	 *
	 * @throws	IOException if the stream has a problem.
	 */
	public static String read(InputStream is)
		throws IOException
	{
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    int byt = is.read();
	    while (byt > 0) {
		bos.write(byt);
		byt = is.read();
	    }
	    return Base64.bytesToString(bos.toByteArray());
	}


	/**
	 * Write the given string to the given {@link OutputStream}.
	 * Note: string bytes are retrieved in UTF-8 encoding
	 *
	 * @param	os	OutputStream to write to
	 * @param	s	String value to write to this stream
	 *
	 * @throws	IOException if the stream has a problem.
	 */
	public static void write(OutputStream os, String s)
		throws IOException
	{
	    byte[] bytes = Base64.getStringBytes(s);
	    os.write(bytes);
	}


	/**
	 * Write the given character to the given {@link OutputStream}.
	 * This is only ever used to write a few different US-ASCII
	 * characters.  If this ever changes, then an appropriate
	 * encoding must be used to translate the Unicode character
	 * to bytes.
	 *
	 * @param	os	OutputStream to write to
	 * @param	c	character to be written (US-ASCII only)
	 *
	 * @throws	IOException if the stream has a problem.
	 */
	public static void write(OutputStream os, char c)
		throws IOException
	{
	    byte b = (byte)(c & 0x7F);
	    os.write(b);
	}


	/**
	 * Get an object token corresponding to the hash of the object's
	 * bytes, along with the current timestamp (for uniqueness).
	 *
	 * @param	object	The object we want to hash.
	 * @return		The object's hash bytes.
	 */
	public static byte[] getObjectToken(Object object) {
	    byte[] objectBytes = ClassUtil.toByteArray(object);
	    byte[] timeBytes = ClassUtil.toByteArray(System.nanoTime());
	    synchronized(__md) {
		__md.reset();
		__md.update(objectBytes);
		byte[] digest = __md.digest(timeBytes);
		return digest;
	    }
	}

}
