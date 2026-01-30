/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022,2024-2026 Roger L. Whitcomb.
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
 *	Calc operational settings.
 *
 *  History:
 *	26-Jan-2022 (rlwhitcomb)
 *	    Moved out of CalcObjectVisitor.
 *	01-Feb-2022 (rlwhitcomb)
 *	    Add Scriptable annotations.
 *	02-Feb-2022 (rlwhitcomb)
 *	    #115: Move "mc" and "mcDivide" into here. New "getPrecision"
 *	    method which is Scriptable so it will be exposed for viewing.
 *	05-Feb-2022 (rlwhitcomb)
 *	    #233: Take out "getPrecision" in favor of just "precision" value.
 *	14-Apr-2022 (rlwhitcomb)
 *	    #273: Move math-related classes to "math" package.
 *	08-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 *	10-Jul-2022 (rlwhitcomb)
 *	    #392: New option to sort objects by key.
 *	06-May-2024 (rlwhitcomb)
 *	    #672: New option for always using proper fraction display mode.
 *	14-Sep-2025 (rlwhitcomb)
 *	    #761: New constructor parameter to set "silent" mode.
 *	27-Jan-2026 (rlwhitcomb)
 *	    #809: Move TrigMode to "math" package.
 */
package info.rlwhitcomb.calc;

import info.rlwhitcomb.annotations.Scriptable;
import info.rlwhitcomb.math.NumericUtil.RangeMode;
import info.rlwhitcomb.math.TrigMode;

import java.math.MathContext;


/**
 * The settings object for Calc.
 */
public class Settings
{
	/** Whether trig inputs are in degrees or radians. */
	@Scriptable
	TrigMode trigMode;

	/** The kind of units to use for the "@k" format. */
	@Scriptable
	RangeMode units;

	/** Decimal vs. rational/fractional mode ({@code true} for rational); default {@code false}. */
	@Scriptable
	boolean rationalMode;

	/** Separators displayed always. */
	@Scriptable
	boolean separatorMode;

	/** Silent flag (set to true) while evaluating nested expressions (or via :quiet directive). */
	@Scriptable
	boolean silent;

	/** Silence directives flag. */
	@Scriptable
	boolean silenceDirectives;

	/** Ignore case when selecting members / variables. */
	@Scriptable
	boolean ignoreNameCase;

	/** Quote strings on output. */
	@Scriptable
	boolean quoteStrings;

	/** Always display fractions as proper fractions. */
	@Scriptable
	boolean properFractions;

	/** Sort object by key (instead of by order of entry). */
	@Scriptable
	boolean sortKeys;

	/** The mathematical precision and rounding mode currently in effect. */
	MathContext mc;

	/**
	 * The rounding precision used for division (differs from {@link #mc} if the former
	 * is <code>unlimited</code>).
	 */
	MathContext mcDivide;

	/**
	 * The calculated precision from the current {@link MathContext} in effect.
	 */
	@Scriptable
	int precision;


	/**
	 * Default constructor to supply default, default settings.
	 */
	public Settings() {
	}

	/**
	 * Construct default settings, including the command-line "-rational" flag.
	 *
	 * @param rational   The initial rational mode setting.
	 * @param separators The initial setting for displaying separators.
	 * @param quiet      Initial "silent" mode.
	 * @param silence    Whether to silence directives.
	 * @param ignoreCase Whether to ignore case on variable / member names.
	 * @param quotes     Whether to quote string values on output.
	 * @param proper     Always using proper fraction display.
	 * @param sortObjs   Whether to sort objects by keys.
	 */
	public Settings(boolean rational, boolean separators, boolean quiet, boolean silence, boolean ignoreCase, boolean quotes, boolean proper, boolean sortObjs) {
	    trigMode          = TrigMode.RADIANS;
	    units             = RangeMode.MIXED;
	    rationalMode      = rational;
	    separatorMode     = separators;
	    silent            = quiet;
	    silenceDirectives = silence;
	    ignoreNameCase    = ignoreCase;
	    quoteStrings      = quotes;
	    properFractions   = proper;
	    sortKeys          = sortObjs;
	}

	/**
	 * Copy constructor - make a copy of another {@code Settings} object.
	 *
	 * @param other The object to copy.
	 */
	public Settings(Settings other) {
	    trigMode          = other.trigMode;
	    units             = other.units;
	    rationalMode      = other.rationalMode;
	    separatorMode     = other.separatorMode;
	    silent            = other.silent;
	    silenceDirectives = other.silenceDirectives;
	    ignoreNameCase    = other.ignoreNameCase;
	    quoteStrings      = other.quoteStrings;
	    properFractions   = other.properFractions;
	    sortKeys          = other.sortKeys;
	    mc                = other.mc;
	    mcDivide          = other.mcDivide;
	    precision         = other.precision;
	}

}
