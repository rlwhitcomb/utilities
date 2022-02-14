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
 *	12-Feb-2022 (rlwhitcomb)
 *	    #199: Derive from ValueScope, so remove common fields / methods.
 *	    Take out the "put" method which is unused now.
 *	14-Feb-2022 (rlwhitcomb)
 *	    #199: Add in override of "isPredefined" and "isImmutable".
 */
package info.rlwhitcomb.calc;

import java.util.Map;
import java.util.function.Supplier;


/**
 * One of the predefined values, such as <code>true</code>, <code>null</code>, etc.
 * which can be computed either as a constant, or from a {@link Supplier} (which is
 * necessary for <code>today</code> (for instance) because it isn't completely constant.
 */
class PredefinedValue extends ValueScope
{
	/**
	 * First choice, the "get value" function used to obtain the value.
	 */
	Supplier<Object> valueSupplier;

	/**
	 * Second choice, the constant value of this predefined variable.
	 */
	Object constantValue;


	/**
	 * Construct one of these, given a supplier for the (maybe not constant) value.
	 *
	 * @param nm       Name of this predefined value.
	 * @param supplier The supplier function.
	 */
	private PredefinedValue(final String nm, final Supplier<Object> supplier) {
	    super(nm, Type.PREDEFINED);

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
	    super(nm, Type.PREDEFINED);

	    this.valueSupplier = null;
	    this.constantValue = value;
	}

	/**
	 * Is this a constant value or not (tests the {@link #valueSupplier}).
	 *
	 * @return <code>true</code> if the value is always constant.
	 */
	boolean isConstant() {
	    return this.valueSupplier == null;
	}

	/**
	 * @return This kind of value is the only one that is actually "predefined".
	 */
	@Override
	protected boolean isPredefined() {
	    return true;
	}

	/**
	 * @return This is immutable for all intents and purposes.
	 */
	@Override
	protected boolean isImmutable() {
	    return true;
	}

	/**
	 * Access the value of this object, using either the value supplier or the constant value.
	 *
	 * @return The current value.
	 */
	@Override
	Object getValue() {
	    if (valueSupplier != null)
		return valueSupplier.get();

	    return constantValue;
	}

	/**
	 * Define one of these into the given symbol table.
	 *
	 * @param scope		The symbol table in which to define it.
	 * @param nm		The name of this predefined value.
	 * @param supplier	Supplier for the value.
	 */
	static void define(final ObjectScope scope, final String nm, final Supplier<Object> supplier) {
	    PredefinedValue predef = new PredefinedValue(nm, supplier);
	    scope.setValue(nm, predef);
	}

	/**
	 * Define one of these into the given symbol table.
	 *
	 * @param scope		The symbol table in which to define it.
	 * @param nm		The name of this predefined value.
	 * @param value		The constant value of it.
	 */
	static void define(final ObjectScope scope, final String nm, final Object value) {
	    PredefinedValue predef = new PredefinedValue(nm, value);
	    scope.setValue(nm, predef);
	}

}

