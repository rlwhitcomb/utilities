/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022,2024-2026 Roger L. Whitcomb.
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
 *      Subclass of NestedScope that also contains a list of parameters.
 *      Includes the GlobalScope and FunctionScope.
 *
 * History:
 *  12-Feb-22 rlw #199	Initial coding, abstracted out of other code.
 *  25-May-22 rlw #348	Make all methods package private.
 *  22-Mar-24 rlw #664	Open "parameters" to subclasses.
 *  01-Jun-25 rlw #724	Changes to delay defining "_*" and "_#" until the
 *			end of parameter processing to avoid infinite
 *			recursion if "_*" is used in a nested call.
 *  15-Jan-26 rlw #795	"With" scope is one of these now also. Save and make
 *			accessor for the name prefix ("$" or "_").
 */
package info.rlwhitcomb.calc;

import java.math.BigInteger;


/**
 * A {@link NestedScope} that also contains a parameter list.
 */
class ParameterizedScope extends NestedScope
{
	/**
	 * Name prefix for use with local variables defined by the declaration / invocation.
	 */
	private String namePrefix;

	/**
	 * Name of the array variable that accesses the complete array of parameters.
	 */
	private String arrayName;

	/**
	 * Name of the count variable that holds the number of parameters.
	 */
	private String countName;

	/**
	 * An array, addressable by index, of the parameter values in order of declaration.
	 */
	protected final ArrayScope<ParameterValue> parameters;


	/**
	 * Constructor given the type and other defining information.
	 *
	 * @param t      The subclass' type.
	 * @param prefix The name prefix for the array and count variables used to access
	 *               the parameter list.
	 */
	ParameterizedScope(final Type t, final String prefix) {
	    super(t);

	    this.namePrefix = prefix;

	    arrayName = String.format("%1$s*", prefix);
	    countName = String.format("%1$s#", prefix);

	    parameters = new ArrayScope<>();
	    parameters.setName(arrayName);
	}

	/**
	 * Access the name prefix for use with names set automatically.
	 *
	 * @return Name prefix defined during construction.
	 */
	String getPrefix() {
	    return namePrefix;
	}

	/**
	 * Add the next parameter to the {@link #parameters} list, which includes updating
	 * the {@link #countName} value.
	 *
	 * @param param The next parameter in the list.
	 */
	void addParamValue(final ParameterValue param) {
	    setValue(param.getName(), param);
	    parameters.add(param);
	}

	/**
	 * Finalize the parameter setup by defining the "_*" and "_#" variables with their final values.
	 */
	void finalizeParameters() {
	    ParameterValue.put(this, arrayName, parameters);
	    ParameterValue.put(this, countName, BigInteger.valueOf(parameters.size()));
	}
}


/**
 * The (singular) global variable scope, which also contains the command line
 * argument "parameters".
 */
class GlobalScope extends ParameterizedScope
{
	/** Prefix for the global (command-line) arguments. */
	static final String GLOBAL_PREFIX = "$";


	GlobalScope() {
	    super(Type.GLOBAL, GLOBAL_PREFIX);
	    setName("globals");
	}
}

/**
 * Scope for a "WITH" statement, which will have "parameters" defined, which are the group
 * strings returned from the regex group values.
 */
class WithScope extends ParameterizedScope
{
	/** Prefix for the group (parameter) values. */
	static final String WITH_PREFIX = "_";


	WithScope() {
	    super(Type.WITH, WITH_PREFIX);
	    setName("with");
	}
}

