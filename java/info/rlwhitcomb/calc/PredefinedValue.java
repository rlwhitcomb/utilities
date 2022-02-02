/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2022 Roger L. Whitcomb.
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
 *	04-Jan-2022 (rlwhitcomb)
 *	    #182: Allow defining into any ObjectScope.
 *	21-Jan-2022 (rlwhitcomb)
 *	    #135: Changes to allow ConstantValue to derive from this.
 *	24-Jan-2022 (rlwhitcomb)
 *	    #223: New method to decide if the value is a constant or not.
 *	02-Feb-2022 (rlwhitcomb)
 *	    #115: New "put" method to wrap values back into a map.
 */
package info.rlwhitcomb.calc;

import java.util.Map;
import java.util.function.Supplier;


/**
 * One of the predefined values, such as <code>true</code>, <code>null</code>, etc.
 * which can be computed either as a constant, or from a {@link Supplier} (which is
 * necessary for <code>today</code> (for instance) because it isn't completely constant.
 */
class PredefinedValue extends Scope
{
	/**
	 * Name of this predefined value (for error reporting).
	 */
	String name;

	/**
	 * First choice, the "get value" function used to obtain the value.
	 */
	Supplier<Object> valueSupplier;

	/**
	 * Second choice, the constant value of this predefined variable.
	 */
	Object constantValue;

	/**
	 * Constructor just given the type.
	 *
	 * @param typ The type of this object.
	 */
	protected PredefinedValue(Type typ) {
	    super(typ);
	}

	/**
	 * Construct one of these, given a supplier for the (maybe not constant) value.
	 *
	 * @param nm       Name of this predefined value.
	 * @param supplier The supplier function.
	 */
	private PredefinedValue(final String nm, final Supplier<Object> supplier) {
	    this(Type.PREDEFINED);

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
	    this(Type.PREDEFINED);

	    this.name          = nm;
	    this.valueSupplier = null;
	    this.constantValue = value;
	}

	/**
	 * Is this a constant value or not (tests the {@link #valueSupplier}).
	 *
	 * @return <code>true</code> if the value is always constant.
	 */
	public boolean isConstant() {
	    return this.valueSupplier == null;
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
	 * Define one of these into the given symbol table.
	 *
	 * @param scope		The symbol table in which to define it.
	 * @param name		The name of this predefined value.
	 * @param supplier	Supplier for the value.
	 */
	static void define(final ObjectScope scope, final String name, final Supplier<Object> supplier) {
	    PredefinedValue predef = new PredefinedValue(name, supplier);
	    scope.setValue(name, predef);
	}

	/**
	 * Define one of these into the given symbol table.
	 *
	 * @param scope		The symbol table in which to define it.
	 * @param name		The name of this predefined value.
	 * @param value		The constant value of it.
	 */
	static void define(final ObjectScope scope, final String name, final Object value) {
	    PredefinedValue predef = new PredefinedValue(name, value);
	    scope.setValue(name, predef);
	}

	/**
	 * Wrap the given map entry with one of ourselves, and put into the result map.
	 *
	 * @param entry		The entry to wrap.
	 * @param resultMap	The result map to put into.
	 */
	static void put(final Map.Entry<String, Object> entry, Map<String, Object> resultMap) {
	    String name = entry.getKey();
	    PredefinedValue predef = new PredefinedValue(name, entry.getValue());
	    resultMap.put(name, predef);
	}

}

