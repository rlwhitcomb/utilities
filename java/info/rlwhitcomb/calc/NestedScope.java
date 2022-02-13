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
 *      Data structures for Calc to hold user-defined function definitions,
 *      local symbol tables, etc.
 *
 *  History:
 *	06-Oct-2021 (rlwhitcomb)
 *	    Initial coding.
 *	07-Oct-2021 (rlwhitcomb)
 *	    Override "remove". Also override "isDefined" and "setValue", and tweak
 *	    the way "getValue" works so that we get/set things in the proper scope.
 *	19-Oct-2021 (rlwhitcomb)
 *	    Change return value of "remove" to the previous value.
 *	03-Feb-2022 (rlwhitcomb)
 *	    Add "getWildValues" here for wild-card search.
 *	13-Feb-2022 (rlwhitcomb)
 *	    #199: Move GlobalScope out to ParameterizedScope; add LOOP_VAR to LoopScope.
 */
package info.rlwhitcomb.calc;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * A local scope that could be nested inside another scope, so value lookup extends
 * to the enclosing scope if not found locally.
 */
class NestedScope extends ObjectScope
{
	/**
	 * Reference to the next closest enclosing scope.
	 */
	NestedScope enclosingScope;


	NestedScope(final Type t) {
	    super(t);
	    this.enclosingScope = null;
	}

	NestedScope(final Type t, final NestedScope outer) {
	    super(t);
	    this.enclosingScope = outer;
	}


	/**
	 * Access the enclosing scope of this scope.
	 *
	 * @return The enclosing scope.
	 */
	public NestedScope getEnclosingScope() {
	    return enclosingScope;
	}

	/**
	 * Set the enclosing scope for this one.
	 *
	 * @param outer The current outer scope.
	 */
	public void setEnclosingScope(final NestedScope outer) {
	    this.enclosingScope = outer;
	}

	/**
	 * See if the given name is defined in this or any enclosing scope(s).
	 *
	 * @param name       Name of the variable to search for.
	 * @param ignoreCase Whether to consider case in the search or not.
	 * @return           Does this name exist anywhere?
	 */
	@Override
	public boolean isDefined(final String name, final boolean ignoreCase) {
	    if (isDefinedHere(name, ignoreCase))
		return true;
	    if (enclosingScope != null)
		return enclosingScope.isDefined(name, ignoreCase);
	    return false;
	}

	/**
	 * Search for a value in this and any enclosing scope(s).
	 *
	 * @param name       Name of the variable to search for.
	 * @param ignoreCase Whether to ignore case in the search.
	 * @return           The value of the variable, if found, or {@code null} if not.
	 */
	@Override
	public Object getValue(final String name, final boolean ignoreCase) {
	    NestedScope scope = this;
	    while (scope != null && !scope.isDefinedHere(name, ignoreCase))
		scope = scope.enclosingScope;

	    if (scope != null)
		return scope.getValueImpl(name, ignoreCase);

	    return null;
	}

	/**
	 * Get possibly multiple values with a wild-card key search.
	 *
	 * @param wildName   The variable name to search for, presumably "wild".
	 * @param ignoreCase Whether to ignore case in the search.
	 * @return           The entry map of the actual keys and values found.
	 */
	Map<String, Object> getWildValues(final String wildName, final boolean ignoreCase) {
	    Map<String, Object> allValues = new LinkedHashMap<>();

	    for (NestedScope scope = this; scope != null; scope = scope.enclosingScope) {
		Map<String, Object> localValues = scope.getWildValuesImpl(wildName, ignoreCase);
		allValues.putAll(localValues);
	    }
	    return allValues;
	}

	/**
	 * Set the value of one of our variables, which could be in an enclosing scope already.
	 *
	 * @param name       Name of the variable to set.
	 * @param ignoreCase Whether or not to ignore the case of name in finding it.
	 * @param value      The new value for the variable.
	 */
	@Override
	public void setValue(final String name, final boolean ignoreCase, final Object value) {
	    NestedScope scope = this;
	    while (scope != null && !scope.isDefinedHere(name, ignoreCase))
		scope = scope.enclosingScope;

	    if (scope != null)
		scope.setValueImpl(name, ignoreCase, value);
	    else
		setValueImpl(name, ignoreCase, value);
	}

	/**
	 * Remove a variable in either this, or the most closely enclosing scope
	 * that actually has the variable.
	 *
	 * @param name       The variable name to remove.
	 * @param ignoreCase Whether to ignore name case to find it.
	 * @return           The previous value mapped to the variable.
	 */
	@Override
	public Object remove(final String name, final boolean ignoreCase) {
	    if (isDefinedHere(name, ignoreCase))
		return super.remove(name, ignoreCase);
	    if (enclosingScope != null)
		return enclosingScope.remove(name, true);
	    return null;
	}

}


/**
 * A local scope used for {@code LOOP} statements in order to define local variables there.
 */
class LoopScope extends NestedScope
{
	public static final String LOOP_VAR = "__";


	LoopScope() {
	    super(Type.LOOP);
	}
}

/**
 * A local scope for either the "if" or "else" portion of that statement.
 */
class IfScope extends NestedScope
{
	IfScope() {
	    super(Type.IF);
	}
}

/**
 * A local scope for the statements of a "while" loop.
 */
class WhileScope extends NestedScope
{
	WhileScope() {
	    super(Type.WHILE);
	}
}

/**
 * A local scope for the statements in a "case" block.
 */
class CaseScope extends NestedScope
{
	CaseScope() {
	    super(Type.CASE);
	}
}

