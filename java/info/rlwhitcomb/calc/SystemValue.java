/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2023 Roger L. Whitcomb.
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
 *	A "system" value that is set by a "set" method and queried
 *	by a field descriptor.
 *
 * History:
 *  04-Feb-22 rlw #233:	Initial coding.
 *  12-Feb-22 rlw #199:	Derive from ValueScope.
 *  14-Feb-22 rlw #199:	Override "isImmutable" here from default in ValueScope.
 *  25-May-22 rlw #348:	Return new value from "setValue" methods.
 *  08-Jul-22 rlw #393:	Cleanup imports.
 *  08-Jan-23 rlw #592:	Move "immutable" down to Scope.
 */
package info.rlwhitcomb.calc;

import info.rlwhitcomb.util.ClassUtil;

import java.lang.reflect.Field;
import java.util.function.Consumer;


/**
 * A "system" value set via a method, and queried by a field descriptor.
 * @param <V> Type of the value this wraps.
 */
class SystemValue<V> extends ValueScope
{
	/**
	 * The object where this value resides.
	 */
	private Object object;

	/**
	 * The field descriptor used to retrieve the value.
	 */
	private Field getField;

	/**
	 * The "set" method for this value.
	 */
	private Consumer<V> setMethod;


	/**
	 * Construct one of these, given its constant value.
	 *
	 * @param nm     Name of this system value.
	 * @param obj    Object where the value resides.
	 * @param field  The "get" field for the current value.
	 * @param method The "set" method for this value.
	 */
	private SystemValue(final String nm, final Object obj, final Field field, final Consumer<V> method) {
	    super(nm, Type.SYSTEM, false);

	    object    = obj;
	    getField  = field;
	    setMethod = method;
	}


	/**
	 * Get this system value using its defined field object.
	 *
	 * @return	The system value there.
	 */
	@Override
	Object getValue() {
	    try {
		Object value = getField.get(object);
		if (value instanceof Enum)
		    return value.toString();
		return value;
	    }
	    catch (IllegalAccessException ex) {
		return null;
	    }
	}

	/**
	 * Set this sytem value using its defined "set" method.
	 *
	 * @param value	The new value for it.
	 * @return      That same value (for convenience).
	 */
	V setValue(final V value) {
	    setMethod.accept(value);

	    return value;
	}

	/**
	 * Define one of these into the given symbol table.
	 *
	 * @param <V>    Type of value for this system variable.
	 * @param scope	 The symbol table in which to define it.
	 * @param obj    Object where this value resides.
	 * @param nm     Name of the field for this value.
	 * @param method The "set" method for this value.
	 */
	static <V> void define(final ObjectScope scope, final Object obj, final String nm, final Consumer<V> method) {
	    Field field = ClassUtil.getField(obj, nm);
	    SystemValue<V> value = new SystemValue<>(nm, obj, field, method);
	    scope.setValue(nm, value);
	}

}

