/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Roger L. Whitcomb.
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
 *      Data structure for a constant value.
 *
 *  History:
 *	21-Jan-2022 (rlwhitcomb)
 *	    #135: Initial coding.
 *	12-Feb-2022 (rlwhitcomb)
 *	    #199: Derive from new ValueScope base class, and add back in the constant
 *	    object value and implement the "getValue" method.
 *	14-Feb-2022 (rlwhitcomb)
 *	    #199: Add back in "isImmutable" as an override.
 *	    Now move back to "ValueScope".
 *	20-Jun-2022 (rlwhitcomb)
 *	    #365: Recursively set "immutable" flag in objects and lists as part of "define".
 *	23-Jun-2022 (rlwhitcomb)
 *	    #314: Set processing.
 */
package info.rlwhitcomb.calc;



/**
 * A read-only (or constant) value defined by the user. So, an alias for a numeric
 * or other value that gives it a meaningful name in the context of the program.
 */
class ConstantValue extends ValueScope
{
	/**
	 * The constant value of this constant.
	 */
	Object constantValue;


	/**
	 * Construct one of these, given its constant value.
	 *
	 * @param nm    Name of this constant value.
	 * @param value The unchanging value of this constant entity.
	 */
	private ConstantValue(final String nm, final Object value) {
	    super(nm, Type.CONSTANT);

	    constantValue = value;
	}


	/**
	 * Get the constant value of this constant.
	 *
	 * @return The really constant value of this constant.
	 */
	@Override
	Object getValue() {
	    return constantValue;
	}

	/**
	 * Recursively make an object immutable as part of a "const" object.
	 *
	 * @param value The value to set as immutable.
	 */
	static private void setImmutable(final Object value) {
	    if (value instanceof ObjectScope) {
		ObjectScope map = (ObjectScope) value;
		map.setImmutable(true);

		for (String key : map.keySet()) {
		    define(map, key, map.getValueLocally(key, false));
		}
	    }
	    else if (value instanceof ArrayScope) {
		@SuppressWarnings("unchecked")
		ArrayScope<Object> list = (ArrayScope<Object>) value;
		list.setImmutable(true);

		for (int index = 0; index < list.size(); index++) {
		    setImmutable(list.getValue(index));
		}
	    }
	    else if (value instanceof SetScope) {
		@SuppressWarnings("unchecked")
		SetScope<Object> set = (SetScope<Object>) value;
		set.setImmutable(true);

		for (Object obj : set.set()) {
		    setImmutable(obj);
		}
	    }
	}

	/**
	 * Define one of these into the given symbol table.
	 *
	 * @param scope	The symbol table in which to define it.
	 * @param nm	The name of this constant value.
	 * @param value	The value of it.
	 */
	static void define(final ObjectScope scope, final String nm, final Object value) {
	    ConstantValue constant = new ConstantValue(nm, value);
	    scope.setValue(nm, constant);

	    if (value instanceof CollectionScope) {
		setImmutable(value);
	    }
	}

}

