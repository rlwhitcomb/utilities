/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Roger L. Whitcomb.
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
 *      Data structure for a predefined symbol.
 *
 *  History:
 *	28-Oct-2021 (rlwhitcomb)
 *	    Initial coding.
 */
package info.rlwhitcomb.calc;

import java.util.function.Supplier;


/**
 * A local scope that could be nested inside another scope, so value lookup extends
 * to the enclosing scope if not found locally.
 */
class PredefinedValue extends Scope
{
	/**
	 * Name of this predefined value (for error reporting).
	 */
	final String name;

	/**
	 * First choice, the "get value" function used to obtain the value.
	 */
	final Supplier<Object> valueSupplier;

	/**
	 * Second choice, the constant value of this predefined variable.
	 */
	final Object constantValue;

	/**
	 * Construct one of these, given a supplier for the (maybe not constant) value.
	 *
	 * @param nm       Name of this predefined value.
	 * @param supplier The supplier function.
	 */
	private PredefinedValue(final String nm, final Supplier<Object> supplier) {
	    super(Type.PREDEFINED);

	    this.name          = nm;
	    this.valueSupplier = supplier;
	    this.constantValue = null;
	}

	/**
	 * Construct one of these, given its constant value.
	 *
	 * @param nm       Name of this predefined value.
	 * @param value The unchanging value of this predefined entity.
	 */
	private PredefinedValue(final String nm, final Object value) {
	    super(Type.PREDEFINED);

	    this.name          = nm;
	    this.valueSupplier = null;
	    this.constantValue = value;
	}

	/**
	 * Access the name of this object.
	 *
	 * @return The name assigned at definition time.
	 */
	String getName() {
	    return name;
	}

	/**
	 * Access the value of this object, using either the value supplier or the constant value.
	 *
	 * @return The current value.
	 */
	Object getValue() {
	    if (valueSupplier != null)
		return valueSupplier.get();

	    return constantValue;
	}

	/**
	 * Define one of these into the global symbols.
	 *
	 * @param globals	The global symbol table in which to define it.
	 * @param name		The name of this predefined value.
	 * @param supplier	Supplier for the value.
	 */
	static void define(final GlobalScope globals, final String name, final Supplier<Object> supplier) {
	    PredefinedValue predef = new PredefinedValue(name, supplier);
	    globals.setValue(name, false, predef);
	}

	/**
	 * Define one of these into the global symbols.
	 *
	 * @param globals	The global symbol table in which to define it.
	 * @param name		The name of this predefined value.
	 * @param value		The constant value of it.
	 */
	static void define(final GlobalScope globals, final String name, final Object value) {
	    PredefinedValue predef = new PredefinedValue(name, value);
	    globals.setValue(name, false, predef);
	}

}

