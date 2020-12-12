/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Roger L. Whitcomb.
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
 *      Display interface for the Calc program.
 *
 *  History:
 *      11-Dec-2020 (rlwhitcomb)
 *          Inital coding.
 */
package info.rlwhitcomb.calc;

/**
 * Interface so that calculated results can be displayed in many different ways:
 * to the console, to a GUI window, etc.
 */
public interface CalcDisplayer
{
	/**
	 * Display normal calculation results for one expression.
	 *
	 * @param exprString	The string representation of the expression.
	 * @param resultString	The result converted to a string for display.
	 */
	void displayResult(String exprString, String resultString);

	/**
	 * Display an action message (such as "cleared") possibly to a special
	 * status area, or just directly to the screen nicely colored.
	 *
	 * @param message	The message to display.
	 */
	void displayActionMessage(String message);

	/**
	 * Display an error message, possibly in a message box, in a special
	 * text area in the GUI, or just in a special color on the console.
	 *
	 * @param message	The error message to display.
	 */
	void displayErrorMessage(String message);

	/**
	 * Display an error message with line number context.
	 *
	 * @param message	The error message to display.
	 * @param lineNumber	The line number where the error occurred.
	 */
	void displayErrorMessage(String message, int lineNumber);
}

