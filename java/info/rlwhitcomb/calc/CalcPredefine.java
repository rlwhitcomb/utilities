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
 *	Predefine all the Calc predefined values (not functions).
 *
 * History:
 *  26-Jan-22 rlw #206	Moved out of CalcObjectVisitor.
 *			Added the hardware values out of Environment, and more "os" fields.
 *  30-Jan-22 rlw #103	Extend "I_ALIASES" (must agree with ComplexNumber).
 *  01-Feb-22 rlw #103	Another I_ALIAS.
 *		  #231	Use Constants class values instead of where they used to be.
 *  02-Feb-22 rlw #115	Make (for now predefined) "info.settings" for these values.
 *			New "makePredefinedMap" method to wrap the map values.
 *  05-Feb-22 rlw #233	Remove that method as it's not needed now with SystemValue.
 *  07-Feb-22 rlw ----	Add "specificationversion" to "info.java" in case we ever need it.
 *  12-Feb-22 rlw #199	Take out "arguments" and the ARG_ initialization because it moved
 *			into ParameterizedScope and GlobalScope.
 *  14-Apr-22 rlw #273	Move math-related classes to "math" package.
 *  17-May-22 rlw #323	Add "env" variables as predefined.
 *  28-May-22 rlw #344	Quote map keys for "env" that aren't valid identifiers.
 *  11-Jun-22 rlw #365	Mark the outer objects as immutable.
 *  08-Jul-22 rlw #393	Cleanup imports.
 *  28-Jul-22 rlw #429	Make "cpu" memory values into dynamic suppliers; add "usedmemory".
 *  25-Dec-22 rlw #83	Another alias for "e".
 *  10-Jan-23 rlw #558	Add aliases for "j" and "k" (quaternions).
 *  11-Jan-23 rlw #558	More aliases for "j" and "k".
 *  09-Feb-23 rlw ----	Rearrange some code and all history.
 *  05-May-23 rlw #610	Add "currentdir" to "info.os".
 *  05-Jan-25 rlw #698	Add copyright string to "info" at the top level.
 */
package info.rlwhitcomb.calc;

import de.onyxbits.SemanticVersion;
import info.rlwhitcomb.util.CharUtil;
import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.Exceptions;

import java.awt.Dimension;
import java.math.BigInteger;
import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Supplier;

import static info.rlwhitcomb.util.Constants.*;


/**
 * Manage predefining of Calc default / predefined values (such as "pi", "e", "phi", "i", "today", etc.).
 */
class CalcPredefine
{
	/**
	 * Aliases for "pi" - need prefixes of '\\' and 'u'  before calling {@link CharUtil#convertEscapeSequences}.
	 */
	private static final String[] PI_ALIASES = {
	    "\u03A0", "\u03C0", "\u03D6", "\u1D28", "\u213C", "\u213F",
	    "{1D6B7}", "{1D6D1}", "{1D6E1}",
	    "{1D6F1}", "{1D70B}", "{1D71B}",
	    "{1D72B}", "{1D745}", "{1D755}",
	    "{1D765}", "{1D77F}", "{1D78F}",
	    "{1D79F}", "{1D7B9}", "{1D7C9}"
	};

	/**
	 * Aliases for "e".
	 */
	private static final String[] E_ALIASES = {
	    "\u2107", "\u2147", "\u212F"
	};

	/**
	 * Aliases for "phi" -- lower case for the value (the "Golden Ratio"),
	 * and UPPER case for the reciprocal (the "Silver Ratio") (semi-standard usage,
	 * according to https://en.wikipedia.org/wiki/Golden_ratio).
	 */
	private static final String[] PHI_ALIASES = {
	    "phi", "\u03C6", "\u03D5", "PHI", "\u03A6"
	};

	/**
	 * Aliases for "i".
	 */
	private static final String[] I_ALIASES = {
	    "i", "I", "\u0131", "\u0399", "\u03B9", "\u2110", "\u2148"
	};

	/**
	 * Aliases for "j" (the quaternion {@code (0, 0, 1, 0)}).
	 */
	private static final String[] J_ALIASES = {
	    "j", "J"
	};

	/**
	 * Aliases for "k" (the quaternion {@code (0, 0, 0, 1)}).
	 */
	private static final String[] K_ALIASES = {
	    "k", "K"
	};



	private static String tzOffset(final int offset) {
	    int rawMinutes = Math.abs(offset) / (1000 * 60);
	    int hours      = rawMinutes / 60;
	    int minutes    = rawMinutes - (hours * 60);

	    return String.format("h'%1$s%2$d:%3$02d'", offset < 0 ? "-" : "", hours, minutes);
	}

	/**
	 * Predefine all the global variables we start with (things like <code>true</code>,
	 * <code>null</code>, <code>today</code>, <code>pi</code>, <code>PHI</code>, and
	 * stuff like that).
	 *
	 * @param globalScope	The global symbol table where these need to be defined.
	 * @param piWorker	Source of pi/e values (background thread).
	 * @param phiSupplier	Source of values for "phi".
	 * @param phi1Supplier	Source of values for "PHI" (the reciprocal).
	 */
	public static void define(
		final GlobalScope globalScope,
		final CalcPiWorker piWorker,
		final Supplier<Object> phiSupplier,
		final Supplier<Object> phi1Supplier)
	{
	    PredefinedValue.define(globalScope, "true", Boolean.TRUE);
	    PredefinedValue.define(globalScope, "false", Boolean.FALSE);
	    PredefinedValue.define(globalScope, "null", null);
	    PredefinedValue.define(globalScope, "nil", null);
	    PredefinedValue.define(globalScope, "\u2400", null);

	    PredefinedValue.define(globalScope, "today", () -> {
		LocalDate today = LocalDate.now();
		return BigInteger.valueOf(today.toEpochDay());
	    });
	    PredefinedValue.define(globalScope, "now", () -> {
		LocalTime now = LocalTime.now();
		return BigInteger.valueOf(now.toNanoOfDay());
	    });

	    SemanticVersion v = Environment.programVersion();
	    ObjectScope version = new ObjectScope();

	    PredefinedValue.define(version, "major",      v.major);
	    PredefinedValue.define(version, "minor",      v.minor);
	    PredefinedValue.define(version, "patch",      v.patch);
	    PredefinedValue.define(version, "prerelease", v.getPreReleaseString());
	    PredefinedValue.define(version, "build",      v.getBuildMetaString());
	    version.setImmutable(true);

	    SemanticVersion lib = Environment.implementationVersion();
	    ObjectScope libVersion = new ObjectScope();

	    PredefinedValue.define(libVersion, "major",      lib.major);
	    PredefinedValue.define(libVersion, "minor",      lib.minor);
	    PredefinedValue.define(libVersion, "patch",      lib.patch);
	    PredefinedValue.define(libVersion, "prerelease", lib.getPreReleaseString());
	    PredefinedValue.define(libVersion, "build",      lib.getBuildMetaString());
	    libVersion.setImmutable(true);

	    ObjectScope cpu = new ObjectScope();

	    PredefinedValue.define(cpu, "cores",       Environment.numberOfProcessors());
	    PredefinedValue.define(cpu, "maxmemory",   () -> {
		return BigInteger.valueOf(Environment.maximumMemorySize());
	    });
	    PredefinedValue.define(cpu, "freememory",  () -> {
		return BigInteger.valueOf(Environment.freeMemorySize());
	    });
	    PredefinedValue.define(cpu, "totalmemory", () -> {
		return BigInteger.valueOf(Environment.totalMemorySize());
	    });
	    PredefinedValue.define(cpu, "usedmemory",  () -> {
		return BigInteger.valueOf(Environment.totalMemorySize() - Environment.freeMemorySize());
	    });
	    cpu.setImmutable(true);

	    ObjectScope os = new ObjectScope();

	    PredefinedValue.define(os, "platform",   Environment.platform());
	    PredefinedValue.define(os, "version",    Environment.osVersion());
	    PredefinedValue.define(os, "id",         Environment.platformIdentifier());
	    PredefinedValue.define(os, "hostname",   Environment.hostName());
	    PredefinedValue.define(os, "user",       Environment.currentUser());
	    PredefinedValue.define(os, "linesep",    Environment.lineSeparator());
	    PredefinedValue.define(os, "filesep",    Environment.fileSeparator());
	    PredefinedValue.define(os, "pathsep",    Environment.pathSeparator());
	    PredefinedValue.define(os, "tempdir",    Environment.tempDirName());
	    PredefinedValue.define(os, "userdir",    Environment.userHomeDirString());
	    PredefinedValue.define(os, "currentdir", Environment.userDirectory().getPath());

	    PredefinedValue.define(os, "screenheight", () -> {
		Dimension consoleSize = Environment.consoleSize();
		return BigInteger.valueOf(consoleSize.height);
	    });
	    PredefinedValue.define(os, "screenwidth", () -> {
		Dimension consoleSize = Environment.consoleSize();
		return BigInteger.valueOf(consoleSize.width);
	    });
	    os.setImmutable(true);

	    ObjectScope java = new ObjectScope();
	    int javaMajor = Environment.javaMajorVersion();
	    String javaVersion = Environment.javaVersion();

	    PredefinedValue.define(java, "major", javaMajor);

	    try {
		SemanticVersion jv = new SemanticVersion(javaVersion);
		if (javaMajor > 8) {
		    PredefinedValue.define(java, "minor",      jv.minor);
		    PredefinedValue.define(java, "patch",      jv.patch);
		    PredefinedValue.define(java, "prerelease", jv.getPreReleaseString());
		    PredefinedValue.define(java, "build",      jv.getBuildMetaString());
		}
		else {
		    // Version looks like: "1.8.0_292"
		    PredefinedValue.define(java, "minor", jv.patch);
		    PredefinedValue.define(java, "patch", jv.getBuildMetaString());
		}
	    }
	    catch (ParseException pe) {
		// This is a programmer error and needs to be fixed in SemanticVersion
		System.err.println("ERROR: Problem with Java version: " + Exceptions.toString(pe));
	    }

	    PredefinedValue.define(java, "version", javaVersion);
	    PredefinedValue.define(java, "model",   Environment.dataModel());

	    PredefinedValue.define(java, "specificationversion", System.getProperty("java.specification.version"));
	    java.setImmutable(true);

	    DateFormatSymbols dfs       = new DateFormatSymbols();
	    ArrayScope<String> amPm     = new ArrayScope<>(dfs.getAmPmStrings());
	    String[] weekDayNames       = dfs.getWeekdays();
	    String[] monthNames         = dfs.getMonths();
	    ArrayScope<String> weekDays = new ArrayScope<>(
		weekDayNames[Calendar.SUNDAY],
		weekDayNames[Calendar.MONDAY],
		weekDayNames[Calendar.TUESDAY],
		weekDayNames[Calendar.WEDNESDAY],
		weekDayNames[Calendar.THURSDAY],
		weekDayNames[Calendar.FRIDAY],
		weekDayNames[Calendar.SATURDAY]);
	    ArrayScope<String> months   = new ArrayScope<>(
		monthNames[0], monthNames[1], monthNames[2], monthNames[3], monthNames[4], monthNames[5],
		monthNames[6], monthNames[7], monthNames[8], monthNames[9], monthNames[10], monthNames[11]);
	    amPm.setImmutable(true);
	    weekDays.setImmutable(true);
	    months.setImmutable(true);

	    ObjectScope locale       = new ObjectScope();
	    Locale currentLocale     = Locale.getDefault();
	    DecimalFormatSymbols efs = new DecimalFormatSymbols();

	    PredefinedValue.define(locale, "name",      currentLocale.getDisplayName());
	    PredefinedValue.define(locale, "tag",       currentLocale.toLanguageTag());
	    PredefinedValue.define(locale, "language",  currentLocale.getISO3Language());
	    PredefinedValue.define(locale, "country",   currentLocale.getISO3Country());
	    PredefinedValue.define(locale, "variant",   currentLocale.getVariant());

	    PredefinedValue.define(locale, "currency",     efs.getCurrencySymbol());
	    PredefinedValue.define(locale, "currencycode", efs.getInternationalCurrencySymbol());
	    PredefinedValue.define(locale, "decimal",      Character.toString(efs.getDecimalSeparator()));
	    PredefinedValue.define(locale, "exponent",     efs.getExponentSeparator());
	    PredefinedValue.define(locale, "minus",        Character.toString(efs.getMinusSign()));
	    PredefinedValue.define(locale, "separator",    Character.toString(efs.getGroupingSeparator()));

	    PredefinedValue.define(locale, "ampm",      amPm);
	    PredefinedValue.define(locale, "weekdays",  weekDays);
	    PredefinedValue.define(locale, "months",    months);
	    locale.setImmutable(true);

	    ObjectScope tz   = new ObjectScope();
	    TimeZone zone    = TimeZone.getDefault();
	    Date now         = new Date();
	    boolean daylight = zone.inDaylightTime(now);
	    int offset       = daylight ? zone.getOffset(now.getTime()) : zone.getRawOffset();

	    PredefinedValue.define(tz, "id",       zone.getID());
	    PredefinedValue.define(tz, "daylight", daylight);
	    PredefinedValue.define(tz, "longname", zone.getDisplayName(daylight, TimeZone.LONG));
	    PredefinedValue.define(tz, "name",     zone.getDisplayName(daylight, TimeZone.SHORT));
	    PredefinedValue.define(tz, "offset",   tzOffset(offset));
	    tz.setImmutable(true);

	    String copyright = Environment.getCopyrightNotice();

	    ObjectScope info = new ObjectScope();

	    PredefinedValue.define(info, "copyright",  copyright);
	    PredefinedValue.define(info, "version",    version);
	    PredefinedValue.define(info, "libversion", libVersion);
	    PredefinedValue.define(info, "cpu",        cpu);
	    PredefinedValue.define(info, "os",         os);
	    PredefinedValue.define(info, "java",       java);
	    PredefinedValue.define(info, "locale",     locale);
	    PredefinedValue.define(info, "timezone",   tz);
	    info.setImmutable(true);

	    PredefinedValue.define(globalScope, "info", info);

	    ObjectScope env = new ObjectScope();

	    TreeMap<String, String> envMap = new TreeMap<>(System.getenv());
	    for (Map.Entry<String, String> entry : envMap.entrySet()) {
		String key = entry.getKey();
		if (!CalcUtil.isValidIdentifier(key)) {
		    key = CharUtil.addDoubleQuotes(key);
		}
		PredefinedValue.define(env, key, entry.getValue());
	    }
	    env.setImmutable(true);

	    PredefinedValue.define(globalScope, "env", env);

	    PredefinedValue.define(globalScope, "pi", piWorker.piSupplier);
	    for (int i = 0; i < PI_ALIASES.length; i++) {
		String alias = PI_ALIASES[i];
		if (alias.charAt(0) == '{') {
		    String key = CharUtil.convertEscapeSequences("\\u" + alias);
		    PredefinedValue.define(globalScope, key, piWorker.piSupplier);
		}
		else {
		    PredefinedValue.define(globalScope, alias, piWorker.piSupplier);
		}
	    }

	    PredefinedValue.define(globalScope, "e", piWorker.eSupplier);
	    for (int i = 0; i < E_ALIASES.length; i++) {
		String alias = E_ALIASES[i];
		PredefinedValue.define(globalScope, alias, piWorker.eSupplier);
	    }

	    Supplier<Object> supplier = phiSupplier;
	    for (int i = 0; i < PHI_ALIASES.length; i++) {
		String alias = PHI_ALIASES[i];
		// Switch to the reciprocal
		if (alias.equals("PHI"))
		    supplier = phi1Supplier;
		PredefinedValue.define(globalScope, alias, supplier);
	    }
	    for (int i = 0; i < I_ALIASES.length; i++) {
		PredefinedValue.define(globalScope, I_ALIASES[i], C_I);
	    }
	    for (int i = 0; i < J_ALIASES.length; i++) {
		PredefinedValue.define(globalScope, J_ALIASES[i], Q_J);
	    }
	    for (int i = 0; i < K_ALIASES.length; i++) {
		PredefinedValue.define(globalScope, K_ALIASES[i], Q_K);
	    }
	}

 }
