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
 *      Base scope for various kinds of values.
 *
 * History:
 *  12-Feb-22 rlw #199: Abstract from existing code.
 *  14-Feb-22 rlw #199: Override "toString" here.
 *			Override "isImmutable" here too.
 *  08-Jan-23 rlw #592:	Move "name" and "immutable" down to Scope.
 */
package info.rlwhitcomb.calc;


/**
 * Base class for various kinds of values, with various sources of the value.
 */
abstract class ValueScope extends Scope
{
	/**
	 * Construct one of these, given its name and type.
	 *
	 * @param nm Name of this value.
	 * @param t  The type of this value object.
	 */
	ValueScope(final String nm, final Type t) {
	    super(t, true);
	    setName(nm);
	}

	/**
	 * Construct one of these, given its name, type, and immutable flag.
	 *
	 * @param nm Name of this value.
	 * @param t  The type of this value object.
	 * @param flag "Immutable" flag for this value (mostly for {@link SystemValue}).
	 */
	ValueScope(final String nm, final Type t, final boolean flag) {
	    super(t, flag);
	    setName(nm);
	}


	/**
	 * Get the value of this object.
	 *
	 * @return The value of this object.
	 */
	abstract Object getValue();


	/**
	 * @return An appropriate value for one of our values.
	 */
	@Override
	public String toString() {
	    return String.format("%1$s value", toBookCase());
	}

}

