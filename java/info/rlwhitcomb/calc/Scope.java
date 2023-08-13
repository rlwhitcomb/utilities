/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2023 Roger L. Whitcomb.
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
 * History:
 *  06-Oct-21 rlw  ---  Initial coding.
 *  26-Oct-21 rlw  ---  Add PREDEFINED type.
 *  21-Jan-22 rlw #135: Add CONSTANT; refactor "toString()".
 *  04-Feb-22 rlw  ---  Add "isPredefined" method.
 *		  #233: Add SYSTEM_VALUE, used for Settings.
 *  12-Feb-22 rlw #199: Add PARAMETER; rename SYSTEM_VALUE to just SYSTEM;
 *			add "isImmutable" for read-only values.
 *  14-Feb-22 rlw #199: Only put the default implementation of "isPredefined", "isImmutable",
 *			and "toString" in here, let the appropriate subclasses override.
 *  25-May-22 rlw #348: Make the enum package private.
 *  21-Jun-22 rlw #314: Add SET type.
 *  08-Jan-23 rlw #225:	New description field and methods.
 *		  #592:	Move basic fields out of subclasses into this base class.
 *  06-Aug-23 rlw #621:	Add "ENUM" type.
 */
package info.rlwhitcomb.calc;


/**
 * Generic base class for symbol tables, system and predefined values, user-defined objects,
 * and user-defined functions.
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

		/** An enumerated value, with a (mostly) automatic value. */
		ENUM,

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
	 * Most of these have names, which are used to refer to their values.
	 */
	private String name = null;

	/**
	 * A short description of this object.
	 */
	private String description = null;

	/**
	 * Whether this value is immutable (many system and predefined values are like this).
	 */
	private boolean immutable = false;


	/**
	 * Construct given the object type.
	 *
	 * @param currentType The type enumeration for this scope.
	 */
	Scope(final Type currentType) {
	    type = currentType;
	}

	/**
	 * Construct given the object type and immutable flag.
	 *
	 * @param currentType The type enumeration for this scope.
	 * @param readOnly    Whether this object can be changed.
	 */
	Scope(final Type currentType, final boolean readOnly) {
	    type = currentType;
	    immutable = readOnly;
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
	 * Access this object's name.
	 *
	 * @return The name of this object.
	 */
	final String getName() {
	    return name;
	}

	/**
	 * Set the name of this object.
	 *
	 * @param nm The new name of this object.
	 */
	final void setName(final String nm) {
	    name = nm;
	}

	/**
	 * Is this an immutable value? These are set initially, probably soon after definition
	 * but thereafter cannot be changed.
	 *
	 * @return Default implementation is {@code false}.
	 */
	final boolean isImmutable() {
	    return immutable;
	}

	/**
	 * Set the "immutable" flag. Usually done shortly after definition, signaling that
	 * the value cannot afterwards be changed.
	 *
	 * @param flag The new value of the immutable flag (default is {@code false}).
	 */
	final void setImmutable(final boolean flag) {
	    immutable = flag;
	}

	/**
	 * Set the description for this object.
	 *
	 * @param desc Current description for this object.
	 */
	final void setDescription(final String desc) {
	    description = desc;
	}

	/**
	 * Access this object's description.
	 *
	 * @return The description for this object.
	 */
	final String getDescription() {
	    return description;
	}

	/**
	 * Return the "book case" version of the type.
	 *
	 * @return {@code "SYSTEM" -> "System"}.
	 */
	final String toBookCase() {
	    String upperName = type.toString();
	    return String.format("%1$c%2$s", upperName.charAt(0), upperName.substring(1).toLowerCase());
	}

	@Override
	public String toString() {
	    return String.format("%1$s scope", toBookCase());
	}
}

