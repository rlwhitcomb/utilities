/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020,2022-2023 Roger L. Whitcomb.
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
 * History:
 *  11-Dec-20 rlw ----	Inital coding.
 *  16-Dec-20 rlw ----	New interface method for "$echo".
 *  20-Jun-22 rlw #364	Add parameter and enum to "displayMessage" for output destination.
 *  12-Jun-23 rlw #616	Error for "Output.fromString" with unknown, non-empty value.
 */
package info.rlwhitcomb.calc;

import info.rlwhitcomb.util.Intl;


/**
 * Interface so that calculated results can be displayed in many different ways:
 * to the console, to a GUI window, etc.
 */
public interface CalcDisplayer
{
	/**
	 * Output destinations for {@link #displayMessage}.
	 */
	enum Output
	{
		OUTPUT,
		ERROR,
		BOTH;

		/**
		 * Decode one of these values from an input string.
		 *
		 * @param input A numeric value or (case-insensitive) version
		 * of one of these values.
		 * @return The decoded value, or {@link #OUTPUT} by default.
		 */
		public static Output fromString(final String input) {
		    if (input != null && input.trim().isEmpty()) {
			return OUTPUT;
		    }
		    else if (input != null) {
			try {
			    return valueOf(input.toUpperCase());
			}
			catch (IllegalArgumentException iae) {
			    try {
				switch (Integer.parseInt(input)) {
				    case 0:
					return OUTPUT;
				    case 1:
					return ERROR;
				    case 2:
					return BOTH;
				    default:
					break;
				}
			    }
			    catch (NumberFormatException nfe) {
				;
			    }
			}
		    }
		    throw new Intl.IllegalArgumentException("calc#unknownOutput", input);
		}
	}


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
	 * Display a general message (such as from "$echo").
	 *
	 * @param message	The message to display.
	 * @param output	The destination for the output: {@link Output}.
	 */
	void displayMessage(String message, Output output);

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

