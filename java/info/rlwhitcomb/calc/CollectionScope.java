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
 *	Parent structure for all the composite (collection) objects in Calc,
 *	such as Object, Array, and Set.
 *
 * History:
 *  21-Jun-22 rlw  ---  Initial coding.
 *  08-Jul-22 rlw #393: Cleanup imports.
 *  15-Aug-22 rlw #440: Move "size()" up to here.
 */
package info.rlwhitcomb.calc;

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
	 * Flag to indicate the object should not be modified (after initial construction).
	 */
	private boolean immutable;


	/**
	 * Private constructor, for the {@link #EMPTY} object.
	 */
	private CollectionScope() {
	    super(Type.COLLECTION);
	    immutable = true;
	}

	/**
	 * Default constructor, given the subclass type.
	 */
	CollectionScope(Type type) {
	    super(type);
	    immutable = false;
	}

	/**
	 * Is this object immutable or not?
	 *
	 * @return The {@link #immutable} flag.
	 */
	@Override
	protected boolean isImmutable() {
	    return immutable;
	}

	/**
	 * Sets the flag to make this object immutable (or not).
	 *
	 * @param value New value for the {@link #immutable} flag.
	 */
	void setImmutable(final boolean value) {
	    immutable = value;
	}

	/**
	 * Get the size (number of first-level elements) in this collection.
	 *
	 * @return The collection's size.
	 */
	protected int size() {
	    return 0;
	}

}
