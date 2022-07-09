/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2012-2013,2020,2022 Roger L. Whitcomb.
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
 *	30-Aug-2012 (rlwhitcomb)
 *	    Created.
 *	01-Oct-2013 (rlwhitcomb)
 *	    Not that we use it, but change default to "true".
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	09-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 */
package info.rlwhitcomb.annotations;

import java.lang.annotation.*;

/**
 * A marker annotation to a property that means it can be specified in the script language
 * as a valid option to the structure.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD, ElementType.METHOD})
@Documented
public @interface Scriptable
{
    boolean value() default true;
}
