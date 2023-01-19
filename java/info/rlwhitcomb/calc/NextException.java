/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2023 Roger L. Whitcomb.
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
 *  16-Aug-22 rlw #439:	Initial coding.
 *  12-Jan-23 rlw  ---	Make a new method to return the instance.
 */
package info.rlwhitcomb.calc;


/**
 * An exception thrown to implement the "next" statement.
 */
public class NextException extends RuntimeException
{
	/**
	 * The only instance of this exception needed.
	 */
	private static final NextException INSTANCE = new NextException();


	private NextException() {
	    super();
	}

	/**
	 * The singleton instance of this exception.
	 *
	 * @return The only instance.
	 */
	public static final NextException instance() {
	    return INSTANCE;
	}

}

