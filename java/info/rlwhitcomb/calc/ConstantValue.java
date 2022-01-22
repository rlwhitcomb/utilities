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
 *      Data structure for a constant value.
 *
 *  History:
 *	21-Jan-2022 (rlwhitcomb)
 *	    #135: Initial coding.
 */
package info.rlwhitcomb.calc;



/**
 * A read-only (or constant) value defined by the user. So, an alias for a numeric
 * or other value that gives it a meaningful name in the context of the program.
 */
class ConstantValue extends PredefinedValue
{
	/**
	 * Construct one of these, given its constant value.
	 *
	 * @param nm       Name of this predefined value.
	 * @param value The unchanging value of this predefined entity.
	 */
	private ConstantValue(final String nm, final Object value) {
	    super(Type.CONSTANT);

	    this.name          = nm;
	    this.valueSupplier = null;
	    this.constantValue = value;
	}

	/**
	 * Define one of these into the given symbol table.
	 *
	 * @param scope		The symbol table in which to define it.
	 * @param name		The name of this constant value.
	 * @param value		The value of it.
	 */
	static void define(final ObjectScope scope, final String name, final Object value) {
	    ConstantValue constant = new ConstantValue(name, value);
	    scope.setValue(name, constant);
	}

}

