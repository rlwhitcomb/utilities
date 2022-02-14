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
 *	12-Feb-2022 (rlwhitcomb)
 *	    #199: Initial coding.
 *	14-Feb-2022 (rlwhitcomb)
 *	    #199: Add back in the "isImmutable" override method.
 */
package info.rlwhitcomb.calc;


/**
 * A function or global parameter, including the predefined array and count
 * values, and varargs values.
 */
class ParameterValue extends ValueScope
{
	/**
	 * The value of this parameter.
	 */
	private Object paramValue;


	/**
	 * Construct one of these, given its value.
	 *
	 * @param nm    Name of this parameter value.
	 * @param value The unchanging value of this parameter.
	 */
	private ParameterValue(final String nm, final Object value) {
	    super(nm, Type.CONSTANT);

	    this.paramValue  = value;
	}


	/**
	 * @return This is one of the immutable value types.
	 */
	@Override
	protected boolean isImmutable() {
	    return true;
	}

	/**
	 * Get the value of this parameter.
	 *
	 * @return The current value of this parameter.
	 */
	@Override
	Object getValue() {
	    return paramValue;
	}

	/**
	 * Define one of these into the given scope.
	 *
	 * @param scope	The enclosing scope in which to define it.
	 * @param nm	The name of this parameter value.
	 * @param value	The current value of it.
	 */
	static void define(final ParameterizedScope scope, final String nm, final Object value) {
	    ParameterValue param = new ParameterValue(nm, value);
	    scope.addParamValue(param);
	}

	/**
	 * Set the value of one of these into the given scope.
	 *
	 * @param scope	The enclosing scope in which to define it.
	 * @param nm	The name of this parameter value.
	 * @param value	The current value of it.
	 */
	static void put(final ParameterizedScope scope, final String nm, final Object value) {
	    ParameterValue param = new ParameterValue(nm, value);
	    scope.setValue(nm, param);
	}

}

