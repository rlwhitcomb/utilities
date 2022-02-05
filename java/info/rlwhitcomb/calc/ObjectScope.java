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
 *	    Return "found" status from "remove".
 *	    Break out the guts of "isDefined", "getValue", and "setValue" so they
 *	    can be called from NestedScope in order to put things in their proper
 *	    scopes when nested. Fix "keyObjectSet".
 *	19-Oct-2021 (rlwhitcomb)
 *	    Return last value from "remove" instead of boolean.
 *	18-Dec=2021 (rlwhitcomb)
 *	    #148: Add constructor from regular Map.
 *	24-Dec-2021 (rlwhitcomb)
 *	    Add another "setValue" without default "ignoreCase" parameter.
 *	18-Jan-2022 (rlwhitcomb)
 *	    Add "getKey" method.
 *	02-Feb-2022 (rlwhitcomb)
 *	    #115: Add new constructor that wraps any object that is "Scriptable".
 *	03-Feb-2022 (rlwhitcomb)
 *	    #230: Add "getWildValues" for doing wildcard searches.
 *	05-Feb-2022 (rlwhitcomb)
 *	    #233: Add "immutable" flag to prevent additions to "settings" object.
 */
package info.rlwhitcomb.calc;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import info.rlwhitcomb.directory.Match;
import info.rlwhitcomb.util.ClassUtil;
import info.rlwhitcomb.util.Intl;


/**
 * Base class of scopes that have variables or members with values.
 */
class ObjectScope extends Scope
{
	/**
	 * The symbol table for this object (global or local), kept in order of declaration
	 * for such things as ":save" where order is important.
	 */
	private final Map<String, Object> variables;

	/**
	 * Used for the "settings" object which can't be added to or otherwise structurally
	 * changed after construction.
	 */
	private boolean immutable;


	ObjectScope() {
	    this(Type.OBJECT);
	}

	/**
	 * Construct one of our subclass types given the type value.
	 *
	 * @param t The subclass type.
	 */
	ObjectScope(final Type t) {
	    super(t);
	    variables = new LinkedHashMap<>();
	    immutable = false;
	}

	/**
	 * Construct one of these given an already existing map of key/value pairs.
	 *
	 * @param map An existing map of values.
	 */
	ObjectScope(final Map<String, Object> map) {
	    super(Type.OBJECT);
	    variables = new LinkedHashMap<>(map);
	    immutable = false;
	}

	/**
	 * For "settings" (and any future like-minded object), set to immutable after construction.
	 *
	 * @param value New value for {@link #immutable} flag.
	 */
	void setImmutable(final boolean value) {
	    immutable = value;
	}

	/**
	 * Get the value of one of our variables, explicitly in the current scope, so that
	 * with nested scopes we can find the appropriate place.
	 *
	 * @param name       The variable name to search for.
	 * @param ignoreCase Whether case is important in finding the name.
	 * @return           The value (which could be {@code null} if it hasn't been defined yet.
	 */
	Object getValueImpl(final String name, final boolean ignoreCase) {
	    if (ignoreCase) {
		// Many times, even if we're ignoring case, the name works as given
		if (variables.containsKey(name)) {
		    return variables.get(name);
		}
		else {
		    // The names could have been saved either with case-sensitive mode or not,
		    // so the keys are always case-sensitive, and we need to search the entry set
		    // and do case-insensitive compares of the keys
		    for (Map.Entry<String, Object> entry : variables.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(name)) {
			    return entry.getValue();
			}
		    }
		    return null;
		}
	    }
	    else {
		return variables.get(name);
	    }
	}

	/**
	 * Get the value of one of our variables.
	 *
	 * @param name       The variable name to search for.
	 * @param ignoreCase Whether case is important in finding the name.
	 * @return           The value (which could be {@code null} if it hasn't been defined yet.
	 */
	Object getValue(final String name, final boolean ignoreCase) {
	    return getValueImpl(name, ignoreCase);
	}

	/**
	 * Get possibly multiple values from a wildcard key search.
	 *
	 * @param wildName   The wildcard variable name to search for.
	 * @param ignoreCase Whether case is important in finding the name(s).
	 * @return           The entries found (could be empty).
	 */
	Map<String, Object> getWildValuesImpl(final String wildName, final boolean ignoreCase) {
	    // We have to enumerate the entries ourselves, since there are no "normal" mechanisms
	    // for doing a wildcard match.
	    Map<String, Object> values = new LinkedHashMap<>();

	    // The names could have been saved either with case-sensitive mode or not,
	    // so the keys are always case-sensitive, and we need to search the entry set
	    // and do case-insensitive compares of the keys
	    for (Map.Entry<String, Object> entry : variables.entrySet()) {
		String key = entry.getKey();
		if (Match.stringMatch(key, wildName, !ignoreCase)) {
		    values.put(key, entry.getValue());
		}
	    }
	    return values;
	}

	/**
	 * Get possibly multiple values with a wild-card key search.
	 *
	 * @param wildName   The variable name to search for, presumably "wild".
	 * @param ignoreCase Whether case is important in finding the name.
	 * @return           The entry map of the actual keys and values found.
	 */
	Map<String, Object> getWildValues(final String wildName, final boolean ignoreCase) {
	    return getWildValuesImpl(wildName, ignoreCase);
	}

	/**
	 * Get the key associated with one of the objects in the variables map.
	 *
	 * @param value	The value to search for.
	 * @return	The key associated with that value, if found, or {@code null} if not.
	 */
	String getKey(final Object value) {
	    for (Map.Entry<String, Object> entry : variables.entrySet()) {
		Object mapValue = entry.getValue();
		if ((mapValue != null && mapValue.equals(value)) ||
		    (mapValue == null && value == null))
		    return entry.getKey();
	    }
	    return null;
	}

	/**
	 * Set the value of one of our variables, explicitly in this object, so a nested scope
	 * can set values where they are defined.
	 *
	 * @param name       Name of the variable to set.
	 * @param ignoreCase Whether to ignore case in order to access the variable.
	 * @param value      The new value for the variable.
	 */
	void setValueImpl(final String name, final boolean ignoreCase, final Object value) {
	    if (ignoreCase) {
		if (variables.containsKey(name)) {
		    variables.put(name, value);
		}
		else {
		    for (Map.Entry<String, Object> entry : variables.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(name)) {
			    entry.setValue(value);
			    return;
			}
		    }
		    if (immutable)
			throw new Intl.IllegalStateException("calc#immutableObject", name);

		    // If the named entry didn't exist in any case, just set as-is
		    variables.put(name, value);
		}
	    }
	    else {
		if (immutable && !variables.containsKey(name))
		    throw new Intl.IllegalStateException("calc#immutableObject", name);

		variables.put(name, value);
	    }
	}

	/**
	 * Set the value of one of our variables, case-sensitive (used for predefined values).
	 *
	 * @param name       Name of the variable to set.
	 * @param value      The new value for the variable.
	 */
	void setValue(final String name, final Object value) {
	    setValueImpl(name, false, value);
	}

	/**
	 * Set the value of one of our variables.
	 *
	 * @param name       Name of the variable to set.
	 * @param ignoreCase Whether to ignore case in order to access the variable.
	 * @param value      The new value for the variable.
	 */
	void setValue(final String name, final boolean ignoreCase, final Object value) {
	    setValueImpl(name, ignoreCase, value);
	}

	/**
	 * For some functions we need to know if the name is defined or not, check in this local scope,
	 * for use by {@link NestedScope} in override method.
	 *
	 * @param name       Name of the variable to check.
	 * @param ignoreCase Whether or not to ignore the case of names when searching.
	 * @return           {@code true} or {@code false} if the scope has such a variable.
	 */
	public boolean isDefinedHere(final String name, final boolean ignoreCase) {
	    if (ignoreCase) {
		if (variables.containsKey(name)) {
		    return true;
		}
		else {
		    for (Map.Entry<String, Object> entry : variables.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(name)) {
			    return true;
			}
		    }
		    return false;
		}
	    }
	    else {
		return variables.containsKey(name);
	    }
	}

	/**
	 * For some functions we need to know if the name is defined or not.
	 *
	 * @param name       Name of the variable to check.
	 * @param ignoreCase Whether or not to ignore the case of names when searching.
	 * @return           {@code true} or {@code false} if the scope has such a variable.
	 */
	public boolean isDefined(final String name, final boolean ignoreCase) {
	    return isDefinedHere(name, ignoreCase);
	}

	/**
	 * Remove the variable entry from our map, effectively setting that variable's value back to {@code null}.
	 *
	 * @param name       Name of the variable to remove.
	 * @param ignoreCase Ignore the case of the name when searching for it.
	 * @return           The previous value of the variable, if any, or {@code null} if the entry was not found.
	 *		     The value {@code null} could also be returned if the previous value of the entry was {@code null}.
	 */
	public Object remove(final String name, final boolean ignoreCase) {
	    if (ignoreCase) {
		if (variables.containsKey(name)) {
		    return variables.remove(name);
		}
		else {
		    for (Map.Entry<String, Object> entry : variables.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(name)) {
			    return variables.remove(entry.getKey());
			}
		    }
		}
	    }
	    else {
		if (variables.containsKey(name)) {
		    return variables.remove(name);
		}
	    }
	    return null;
	}

	/**
	 * Access the complete set of variable names listed in order of declaration.
	 *
	 * @return The variable key (name) set.
	 */
	public Set<String> keySet() {
	    return variables.keySet();
	}

	/**
	 * Access the complete set of variable names as a generic Object set.
	 *
	 * @return The key name set as Objects.
	 */
	public Set<Object> keyObjectSet() {
	    Set<Object> set = new LinkedHashSet<>();
	    for (String key : variables.keySet()) {
		set.add(key);
	    }
	    return set;
	}

	/**
	 * Completely clear all the variables in this scope.
	 */
	public void clear() {
	    variables.clear();
	}

	/**
	 * Access the underlying map.
	 *
	 * @return The map we are wrapping.
	 */
	public Map<String, Object> map() {
	    return variables;
	}

	/**
	 * Access the underlying values set.
	 *
	 * @return The values set we are wrapping.
	 */
	public Collection<Object> values() {
	    return variables.values();
	}

	/**
	 * Access the current number of variables defined in this scope.
	 *
	 * @return The size of the variables map.
	 */
	public int size() {
	    return variables.size();
	}

}
