/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Roger L. Whitcomb.
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
 *      Data structure for an enumerated value.
 *
 * History:
 *  06-Aug-23 rlw #621	Created based on ConstantValue.
 */
package info.rlwhitcomb.calc;



/**
 * An enumerated (constant) value with usually an automatic, small integer value.
 * <p> Used for a list of choices where the names are meaningful, but the values
 * are not so much.
 */
class EnumValue extends ValueScope
{
	/**
	 * The (usually small integer) value for this enumerated constant.
	 */
	Object enumValue;


	/**
	 * Construct one of these, given its constant value.
	 *
	 * @param nm    Name for this enumerated value.
	 * @param value The unchanging value of this name.
	 */
	private EnumValue(final String nm, final Object value) {
	    super(nm, Type.ENUM);

	    enumValue = value;
	}


	/**
	 * Get the constant value of this name.
	 *
	 * @return The unchanging value of the name.
	 */
	@Override
	Object getValue() {
	    return enumValue;
	}

	/**
	 * Define one of these into the given symbol table.
	 *
	 * @param scope	The symbol table in which to define it.
	 * @param nm	The name of this enum value.
	 * @param value	The value of it.
	 */
	static void define(final ObjectScope scope, final String nm, final Object value) {
	    EnumValue eValue = new EnumValue(nm, value);
	    scope.setValue(nm, eValue);
	}

}

