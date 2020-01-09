/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2010,2020 Roger L. Whitcomb.
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
 *  Ant condition to implement "greater" (>) condition
 *  Same semantics as the built-in "equals" condition
 */
package info.rlwhitcomb.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;


public class GreaterCondition implements Condition {
    private String arg1;
    private String arg2;
    private boolean casesensitive = true;
    private boolean trim = false;
    private boolean forcestring = false;

    // The setter for the "arg1" attribute
    public void setArg1(String value) {
        this.arg1 = value;
    }

    // The setter for the "arg2" attribute
    public void setArg2(String value) {
        this.arg2 = value;
    }

    // The setter for the "casesensitive" attribute
    public void setCasesensitive(String value) {
        casesensitive = Boolean.parseBoolean(value);
    }

    // The setter for the "trim" attribute
    public void setTrim(String value) {
        trim = Boolean.parseBoolean(value);
    }

    // The setter for the "forcestring" attribute
    public void setForcestring(String value) {
        forcestring = Boolean.parseBoolean(value);
    }

    // This method evaluates the condition
    public boolean eval() {
        if (arg1 == null) {
            throw new BuildException("arg1 attribute is not set");
        }
        if (arg2 == null) {
            throw new BuildException("arg2 attribute is not set");
        }
        // Do trim on both arguments if required
        if (trim) {
            arg1 = arg1.trim();
            arg2 = arg2.trim();
        }
        // Try numeric compares unless "forcestring" is specified
        if (!forcestring) {
            // if both operands can be converted to integers, do an integer test
            try {
                int i1 = Integer.parseInt(arg1);
                int i2 = Integer.parseInt(arg2);
                return (i1 > i2);
            }
            catch (NumberFormatException nfe) {
                // try again with floating-point values
                try {
                    double d1 = Double.parseDouble(arg1);
                    double d2 = Double.parseDouble(arg2);
                    return (d1 > d2);
                }
                catch (NumberFormatException nfe2) {
                    // Allow to drop through into string compare
                }
            }
        }
        // Alright, then just compare as strings
        if (casesensitive)
            return arg1.compareTo(arg2) > 0;
        else
            return arg1.compareToIgnoreCase(arg2) > 0;
    }
}
