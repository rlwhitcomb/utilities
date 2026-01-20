/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022,2024,2026 Roger L. Whitcomb.
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
 *      Data structure for a function parameter.
 *
 * History:
 *  12-Feb-22 rlw #199	Initial coding.
 *  14-Feb-22 rlw #199	Add back in the "isImmutable" override method.
 *			Now move back to "ValueScope".
 *  17-Feb-22 rlw #252	Make sure to set the parameter in the local scope.
 *  25-May-22 rlw #348	Just call "setValue" which defaults to locally.
 *  04-Feb-24 rlw #645	Allow parameters to pass by reference (and thereby
 *			be modifiable).
 *  15-Jan-25 rlw #795	Add "define" method with an index value for the name.
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
	 * @param con   Except when it is markable modifiable.
	 */
	private ParameterValue(final String nm, final Object value, final boolean con) {
	    super(nm, Type.PARAMETER);

	    paramValue = value;

	    setImmutable(con);
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
	 * Define a constant one of these identified by number into the given scope.
	 *
	 * @param scope	The enclosing scope in which to define it.
	 * @param index	Index value which is used for the name of this parameter.
	 * @param value	The current value of it.
	 */
	static void define(final ParameterizedScope scope, final int index, final Object value) {
	    define(scope, String.format("%1$s%2$d", scope.getPrefix(), index), value, true);
	}

	/**
	 * Define a constant one of these into the given scope.
	 *
	 * @param scope	The enclosing scope in which to define it.
	 * @param nm	The name of this parameter value.
	 * @param value	The current value of it.
	 */
	static void define(final ParameterizedScope scope, final String nm, final Object value) {
	    define(scope, nm, value, true);
	}

	/**
	 * Define one of these into the given scope.
	 *
	 * @param scope	The enclosing scope in which to define it.
	 * @param nm	The name of this parameter value.
	 * @param value	The current value of it.
	 * @param con	Whether the value is constant or not.
	 */
	static void define(final ParameterizedScope scope, final String nm, final Object value, final boolean con) {
	    ParameterValue param = new ParameterValue(nm, value, con);
	    scope.addParamValue(param);
	}

	/**
	 * Set the value of one of these into the given scope. This is used only for the arg count
	 * and argument array; always immutable.
	 *
	 * @param scope	The enclosing scope in which to define it.
	 * @param nm	The name of this parameter value.
	 * @param value	The current value of it.
	 */
	static void put(final ParameterizedScope scope, final String nm, final Object value) {
	    ParameterValue param = new ParameterValue(nm, value, true);
	    scope.setValue(nm, param);
	}

}

