/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2023,2025 Roger L. Whitcomb.
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
 *	Parent structure for all the composite (collection) objects in Calc,
 *	such as Object, Array, and Set.
 *
 * History:
 *  21-Jun-22 rlw ----	Initial coding.
 *  08-Jul-22 rlw #393	Cleanup imports.
 *  15-Aug-22 rlw #440	Move "size()" up to here.
 *  08-Jan-23 rlw #592	Move "immutable" down to Scope; move "isEmpty" to here.
 *  13-Jul-25 rlw #738	Add "valueList" base method.
 */
package info.rlwhitcomb.calc;

import java.util.ArrayList;
import java.util.List;

/**
 * Parent scope for all the composite (collection) objects.
 */
class CollectionScope extends Scope
{
	/**
	 * An empty collection, to be used as the basis for any other collection.
	 */
	static final CollectionScope EMPTY = new CollectionScope();

	/**
	 * An empty object list for the default {@link #valueList} implementation.
	 */
	private static final List<Object> EMPTY_LIST = new ArrayList<>(0);


	/**
	 * Private constructor, for the {@link #EMPTY} object.
	 */
	private CollectionScope() {
	    super(Type.COLLECTION, true);
	}

	/**
	 * Default constructor, given the subclass type.
	 */
	CollectionScope(Type type) {
	    super(type, false);
	}


	/**
	 * Get the size (number of first-level elements) in this collection.
	 *
	 * @return The collection's size.
	 */
	protected int size() {
	    return 0;
	}

	/**
	 * Get the first-level list of values.
	 *
	 * @return All the collection's first-level values.
	 */
	protected List<Object> valueList() {
	    return EMPTY_LIST;
	}

	/**
	 * Access whether this collection is empty.
	 *
	 * @return Whether the size is zero.
	 */
	protected boolean isEmpty() {
	    return true;
	}

}
