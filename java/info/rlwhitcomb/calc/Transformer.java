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
 *  History:
 *      12-May-2022 (rlwhitcomb)
 *	    Initial coding.
 *	13-May-2022 (rlwhitcomb)
 *	    #320: Make "forKeys" into default function.
 *	    #320: Default is now false ("trim" and "replace" shouldn't).
 *	26-May-2022 (rlwhitcomb)
 *	    #320: Add default "applyToMap" method (can be overridden).
 */
package info.rlwhitcomb.calc;

import java.util.function.Function;


/**
 * Functional interface used for doing object transformations in Calc;
 * things such as case conversion on strings, mathematical operations,
 * trimming or padding of strings, string to number conversions, or vice-versa,
 * and so on.
 * <p> Other than the pure functional interface method, we add several other
 * methods specifically for dealing with these conversions on maps and lists.
 */
@FunctionalInterface
public interface Transformer extends Function<Object, Object>
{
	@Override
	Object apply(Object value);

	/**
	 * Does this transformation apply to map keys as well as values?
	 *
	 * @return {@code true} if map keys can be transformed, default is {@code false}.
	 */
	default boolean forKeys() {
	    return false;
	}

	/**
	 * Should a copy be done for {@code null} values (useful when the transform is a
	 * filter to include / exclude certain values)?
	 *
	 * @return {@code true} to copy all values, including {@code null}, default is {@code true};
	 * return {@code false} to use a {@code null} return from {@link #apply} or {@link #applyToMap} to
	 * filter out those values.
	 */
	default boolean copyNull() {
	    return true;
	}

	/**
	 * Apply the transform for map keys or values.
	 * <p> If {@link #forKeys} returns <code>true</code> then this method will be
	 * called twice for each map entry.
	 * <p> Default implementation is simply to call {@link #apply} on all values.
	 *
	 * @param value The key or map value to transform.
	 * @param key   Whether this call is for the key (<code>true</code>) or the value.
	 * @return      The transformed value.
	 */
	default Object applyToMap(final Object value, final boolean key) {
	    return apply(value);
	}

}

