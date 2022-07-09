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
 *  Base class for several Condition classes that implement >, >=, <, and <=
 *  conditions with the same semantics as the build-in "equals" condition.
 */
package info.rlwhitcomb.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.condition.Condition;

import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * Base class for a number of {@link Condition}s that check
 * <code>arg1</code>&nbsp;<i>op</i>&nbsp;<code>arg2</code>,
 * with options for case-sensitivity, removing blanks, and forcing
 * a string comparison (even with what look like numeric values).
 */
public abstract class CompareCondition implements Condition {
    /** The first argument in the comparison. */
    private String arg1;
    /** The second argument in the comparison. */
    private String arg2;

    /**
     * Whether to do the (string) comparison as case-sensitive or not
     * (default {@code true}).
     */
    private boolean casesensitive = true;
    /**
     * Whether to trim leading and trailing whitespace from the arguments
     * (default {@code false}).
     */
    private boolean trim = false;
    /**
     * Whether or not to force the comparison to be done as strings (that is,
     * skip trying to do numeric comparisons) (default {@code false}).
     */
    private boolean forcestring = false;


    /**
     * The setter for the "arg1" attribute.
     * @param value New value for the first argument in the comparison.
     */
    public void setArg1(String value) {
        this.arg1 = value;
    }

    /**
     * The setter for the "arg2" attribute.
     * @param value New value for the second argument in the comparison.
     */
    public void setArg2(String value) {
        this.arg2 = value;
    }

    /**
     * The setter for the "casesensitive" attribute.
     * @param value New value for the attribute.
     */
    public void setCasesensitive(String value) {
        casesensitive = Boolean.parseBoolean(value);
    }

    /**
     * The setter for the "trim" attribute.
     * @param value New value for the attribute.
     */
    public void setTrim(String value) {
        trim = Boolean.parseBoolean(value);
    }

    /**
     * The setter for the "forcestring" attribute.
     * @param value New value for the attribute.
     */
    public void setForcestring(String value) {
        forcestring = Boolean.parseBoolean(value);
    }

    /**
     * This base method evaluates the condition.
     * @return The result of <code>arg1</code>&nbsp;<i>compareTo</i>&nbsp;<code>arg2</code>.
     * @throws BuildException if the arguments are not set.
     */
    public int evaluate() {
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

        // Try numeric compare first unless "forcestring" is specified
        if (!forcestring) {
            // if both operands can be converted to integers, do an integer test
            try {
                BigInteger i1 = new BigInteger(arg1);
                BigInteger i2 = new BigInteger(arg2);
                return i1.compareTo(i2);
            }
            catch (NumberFormatException nfe) {
                // try again with floating-point values
                try {
                    BigDecimal d1 = new BigDecimal(arg1);
                    BigDecimal d2 = new BigDecimal(arg2);
                    return d1.compareTo(d2);
                }
                catch (NumberFormatException nfe2) {
                    // Allow to drop through into string compare
                }
            }
        }

        // Alright, then just compare as strings
        if (casesensitive)
            return arg1.compareTo(arg2);
        else
            return arg1.compareToIgnoreCase(arg2);
    }

    /**
     * Evaluate the condition.
     * @return The result of <code>arg1</code>&nbsp;<i>compare</i>&nbsp;<code>arg2</code>.
     */
    public abstract boolean eval();

}

