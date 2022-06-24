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
 *	26-Oct-2021 (rlwhitcomb)
 *	    Add PREDEFINED type.
 *	21-Jan-2022 (rlwhitcomb)
 *	    #135: Add CONSTANT; refactor "toString()".
 *	04-Feb-2022 (rlwhitcomb)
 *	    Add "isPredefined" method.
 *	    #233: Add SYSTEM_VALUE, used for Settings.
 *	12-Feb-2022 (rlwhitcomb)
 *	    #199: Add PARAMETER; rename SYSTEM_VALUE to just SYSTEM;
 *	    add "isImmutable" for read-only values.
 *	14-Feb-2022 (rlwhitcomb)
 *	    #199: Only put the default implementation of "isPredefined", "isImmutable",
 *	    and "toString" in here, let the appropriate subclasses override.
 *	25-May-2022 (rlwhitcomb)
 *	    #348: Make the enum package private.
 *	21-Jun-2022 (rlwhitcomb)
 *	    #314: Add SET type.
 */
package info.rlwhitcomb.calc;


/**
 * Generic base class for symbol tables, user-defined objects, and user-defined functions.
 */
class Scope
{
	/**
	 * The various types of entities that we represent.
	 */
	static enum Type
	{
		/** The global symbol table. */
		GLOBAL,

		/** Generic collection. */
		COLLECTION,

		/** A user-defined object (map). */
		OBJECT,

		/** A user-defined array (list). */
		ARRAY,

		/** A user-defined set. */
		SET,

		/** A {@code LOOP} statement block. */
		LOOP,

		/** An {@code IF} or {@code ELSE} statement block. */
		IF,

		/** A {@code WHILE} statement block. */
		WHILE,

		/** A {@code CASE} statement block. */
		CASE,

		/** A user-defined function, which includes a local symbol table. */
		FUNCTION,

		/** A predefined variable or function. */
		PREDEFINED,

		/** A constant value, evaluated once and cached. */
		CONSTANT,

		/** A function parameter, including the param array and count values. */
		PARAMETER,

		/** A "system" value, set by method, accessed by field. */
		SYSTEM
	}

	/**
	 * The type of object this represents.
	 */
	private Type type;


	/**
	 * Construct given the object type.
	 *
	 * @param currentType The type enumeration for this scope.
	 */
	Scope(final Type currentType) {
	    type = currentType;
	}

	/**
	 * Access this scope's type.
	 *
	 * @return The type of scope.
	 */
	final Type getType() {
	    return type;
	}

	/**
	 * Is this a predefined value type?
	 *
	 * @return Default implementation is {@code false}.
	 */
	protected boolean isPredefined() {
	    return false;
	}

	/**
	 * Is this an immutable value? These are set initially, probably soon after definition
	 * but thereafter cannot be changed.
	 *
	 * @return Default implementation is {@code false}.
	 */
	protected boolean isImmutable() {
	    return false;
	}

	/**
	 * Return the "book case" version of the type.
	 *
	 * @return {@code "SYSTEM" -> "System"}.
	 */
	String toBookCase() {
	    String upperName = type.toString();
	    return String.format("%1$c%2$s", upperName.charAt(0), upperName.substring(1).toLowerCase());
	}

	@Override
	public String toString() {
	    return String.format("%1$s scope", toBookCase());
	}
}

