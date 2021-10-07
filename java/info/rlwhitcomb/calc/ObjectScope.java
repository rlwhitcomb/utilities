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
 *      Data structures for Calc to hold user-defined function definitions,
 *      local symbol tables, etc.
 *
 *  History:
 *	06-Oct-2021 (rlwhitcomb)
 *	    Initial coding.
 *	07-Oct-2021 (rlwhitcomb)
 *	    Return "found" status from "remove".
 */
package info.rlwhitcomb.calc;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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
	private final Map<String, Object> variables = new LinkedHashMap<>();


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
	}


	/**
	 * Get the value of one of our variables.
	 *
	 * @param name       The variable name to search for.
	 * @param ignoreCase Whether case is important in finding the name.
	 * @return           The value (which could be {@code null} if it hasn't been defined yet.
	 */
	Object getValue(final String name, final boolean ignoreCase) {
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
	 * Set the value of one of our variables.
	 *
	 * @param name       Name of the variable to set.
	 * @param ignoreCase Whether to ignore case in order to access the variable.
	 * @param value      The new value for the variable.
	 */
	void setValue(final String name, final boolean ignoreCase, final Object value) {
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
		    // If the named entry didn't exist in any case, just set as-is
		    variables.put(name, value);
		}
	    }
	    else {
		variables.put(name, value);
	    }
	}

	/**
	 * For some functions we need to know if the name is defined or not.
	 *
	 * @param name     Name of the variable to check.
	 * @param ignoreCase Whether or not to ignore the case of names when searching.
	 * @return           {@code true} or {@code false} if the scope has such a variable.
	 */
	public boolean isDefined(final String name, final boolean ignoreCase) {
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
	 * Remove the variable entry from our map, effectively setting that variable's value back to {@code null}.
	 *
	 * @param name       Name of the variable to remove.
	 * @param ignoreCase Ignore the case of the name when searching for it.
	 * @return           Whether or not the variable was found to be removed.
	 */
	public boolean remove(final String name, final boolean ignoreCase) {
	    if (ignoreCase) {
		if (variables.containsKey(name)) {
		    variables.remove(name);
		    return true;
		}
		else {
		    for (Map.Entry<String, Object> entry : variables.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(name)) {
			    variables.remove(entry.getKey());
			    return true;
			}
		    }
		}
	    }
	    else {
		if (variables.containsKey(name)) {
		    variables.remove(name);
		    return true;
		}
	    }
	    return false;
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
	    Collections.addAll(set, variables.keySet());
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
