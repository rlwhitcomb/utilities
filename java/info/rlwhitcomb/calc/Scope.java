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
 *	26-Oct-2021 (rlwhitcomb)
 *	    Add PREDEFINED type.
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
	public static enum Type
	{
		/** The global symbol table. */
		GLOBAL,
		/** A user-defined object (map). */
		OBJECT,
		/** A user-defined array (list). */
		ARRAY,
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
		PREDEFINED
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
	    this.type = currentType;
	}

	/**
	 * Access this scope's type.
	 *
	 * @return The type of scope.
	 */
	Type getType() {
	    return this.type;
	}

	@Override
	public String toString() {
	    if (type == Type.FUNCTION)
		return String.format("function %1$s scope", ((FunctionScope) this).getDeclaration().getFunctionName());
	    else
		return String.format("%1$s scope", type);
	}
}

