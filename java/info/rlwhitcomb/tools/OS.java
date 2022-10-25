/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2022 Roger L. Whitcomb.
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
 *	Display various environment and Java system properties.
 *
 *  Change History:
 *	02-Oct-2020 (rlwhitcomb)
 *	    First version, based on older code.
 *	19-Oct-2020 (rlwhitcomb)
 *	    Added MessageDigest list.
 *	19-Oct-2020 (rlwhitcomb)
 *	    Allow multiple choices. Make the titles look better.
 *	20-Oct-2020 (rlwhitcomb)
 *	    Use streams and a FunctionalInterface to make the code cleaner.
 *	    Add some Javadoc. Add the list of security providers.
 *	21-Oct-2020 (rlwhitcomb)
 *	    More code cleanup; move the command line choice matching into
 *	    the Choice enum itself; display all the choices for the "help",
 *	    and implement a "help" option.
 *	09-Nov-2020 (rlwhitcomb)
 *	    Tweak the help output.
 *	04-Jan-2021 (rlwhitcomb)
 *	    Allow choices to be "options" format ("-props", etc.)
 *	13-Jan-2021 (rlwhitcomb)
 *	    List fonts available in the graphics environment.
 *	19-Jan-2021 (rlwhitcomb)
 *	    Do output in columns if possible. A few more aliases for the
 *	    choices.
 *	19-Jan-2021 (rlwhitcomb)
 *	    Fix a bug in the display of the choice aliases. Reorder them.
 *	23-Feb-2021 (rlwhitcomb)
 *	    Add "cs" as an option for "charsets".
 *	17-Mar-2021 (rlwhitcomb)
 *	    Add timezones.
 *	17-Mar-2021 (rlwhitcomb)
 *	    Additional display of the default timezone if none was "equal" to it.
 *	29-Mar-2021 (rlwhitcomb)
 *	    Move to new package; reformat Change History.
 *	03-Aug-2021 (rlwhitcomb)
 *	    Add some color to most displays.
 *	22-Oct-2021 (rlwhitcomb)
 *	    More Locale information, verbose flag, and filters.
 *	19-Nov-2021 (rlwhitcomb)
 *	    #98: Use the real screen width, add "-width:nn" option, fix the indexing
 *	    in the columnar display.
 *	    Add options for no titles, no columns.
 *	19-Jan-2022 (rlwhitcomb)
 *	    #210: Fix the coloring for verbose locale display
 *	    #209: Add international currency symbol (same as Calc "info.locale").
 *	21-Jan-2022 (rlwhitcomb)
 *	    #217: Use new Options method to process an environment variable for default options.
 *	16-May-2022 (rlwhitcomb)
 *	    #326: Fix color end value sequences after changes for Calc "<>" operator.
 *	09-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 *	18-Oct-2022 (rlwhitcomb)
 *	    #530: Move help and error text to resource file; color it all.
 *	    Implement wildcard filter tags.
 *	25-Oct-2022 (rlwhitcomb)
 *	    #39: Option to list all the currencies available. List more Locale information
 *	    in verbose mode.
 */
package info.rlwhitcomb.tools;

import info.rlwhitcomb.directory.Match;
import info.rlwhitcomb.util.CharUtil;
import info.rlwhitcomb.util.ConsoleColor;
import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.Options;

import java.awt.GraphicsEnvironment;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.security.Provider;
import java.security.Security;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.*;


/**
 * Display reports of various system lists, such as the system properties,
 * available locales, etc.
 */
public class OS
{
	/** Banner for the top of each report. */
	private static final String HEADER = "=========================================";
	/** Final banner for the end of the reports. */
	private static final String FOOTER = "-----------------------------------------";

	/** Empty Locale code field substitute. */
	private static final String EMPTY_CODE = "---";

	/** The current screen width value (for columnar output). */
	private static int screenWidth;

	/** The current time (for TimeZone displays). */
	private static final Date now = new Date();

	/** Some choices can have optional additional information. */
	private static boolean verbose = false;

	/** Whether to do single column (not columnar) displays. */
	private static boolean singleColumn = false;

	/** Whether to display titles and footers (works well in conjunction with "-single"). */
	private static boolean showTitles = true;

	/** Whether to use colors on output. */
	private static boolean colors = true;

	/**
	 * List of filter values (dependent on which selection(s) is/are made
	 * (most useful for single choices).
	 */
	private static Set<String> filterValues = new HashSet<>();


	/**
	 * What the user would like to display, the method to display each bit, and
	 * the possible names to match.
	 */
	private enum Choice
	{
		PROPERTIES	(OS::displayProperties,
				 "properties", "props", "prop", "p"),
		ENVIRONMENT	(OS::displayEnvironment,
				 "environment", "environ", "envs", "env", "e"),
		CHARSETS	(OS::displayCharsets,
				 "charsets", "charset", "chars", "char", "ch", "cs", "c"),
		LOCALES		(OS::displayLocales,
				 "locales", "locale", "locs", "loc", "l"),
		CURRENCIES	(OS::displayCurrencies,
				 "currencies", "currency", "curr", "cur", "cr"),
		DIGESTS		(OS::displayDigests,
				 "message-digests", "message_digests", "messagedigests",
				 "digests", "digest", "digs", "dig", "md", "d", "m"),
		PROVIDERS	(OS::displayProviders,
				 "security-providers", "security_providers",
				 "securityproviders", "providers", "security", "provs",
				 "prov", "sec", "sp", "s"),
		FONTS		(OS::displayFonts,
				 "fonts", "font", "f"),
		TIMEZONES	(OS::displayTimeZones,
				 "timezones", "timezone", "zones", "zone", "tz", "z");

		private Runnable displayer;
		private String[] aliasNames;

		Choice(final Runnable disp, final String... names) {
		    this.displayer  = disp;
		    this.aliasNames = names;
		}

		void display() {
		    displayer.run();
		}

		/**
		 * Match the input against one of us.
		 *
		 * @param input	The input string to test.
		 * @return	A matching choice or {@code null} if
		 *		no match was made.
		 */
		static Choice match(final String input) {
		    return Arrays.stream(values())
				 .filter(c -> matches(input, c.aliasNames))
				 .findFirst()
				 .orElse(null);
		}

		/**
		 * Display all the choices available for each choice type.
		 *
		 * @param ps		The stream to print to (either {@link System#out} or {@link System#err})..
		 * @param indent	The leading indent string for consistency with other text.
		 * @param width		The line width where wrapping should occur.
		 */
		static void displayAliases(final PrintStream ps, final String indent, final int width) {
		    Arrays.stream(values())
				.forEach(
			c -> ps.println(ConsoleColor.color(String.format("%1$s,", makeArrayString(c.aliasNames, indent, width)), colors)));
		}
	}

	/**
	 * The set of reports to be run. More than one may be selected.
	 */
	private static Set<Choice> choices = EnumSet.noneOf(Choice.class);


	private static String makeArrayString(final String[] values, final String indent, final int width) {
	    int indentWidth = indent.length();
	    int printWidth  = indentWidth;
	    StringBuilder output = new StringBuilder(indent);

	    for(String value : values) {
		if (printWidth != indentWidth) {
		    output.append(", ");
		    printWidth += 2;
		}
		int valueWidth = value.length() + 2;
		if (printWidth + valueWidth > width) {
		    output.append('\n').append(indent).append(indent);
		    printWidth = indentWidth * 2;
		}
		output.append("\"<Gr>").append(value).append("<.>\"");
		printWidth += valueWidth;
	    }

	    return output.toString();
	}

	/**
	 * Print a nicely formatted banner before the report, with the
	 * given title.
	 * <p> The {@link #HEADER} line is displayed before and after,
	 * then the title string is expanded with spaces between letters
	 * and each letter UPPERcased.
	 *
	 * @param title	The title string to display in the banner.
	 */
	private static void printTitle(final String title) {
	    int len = title.length();
	    StringBuilder buffer = new StringBuilder(len * 2 + 3);
	    buffer.append("   ");
	    for (int i = 0; i < len; i++) {
		char ch = Character.toUpperCase(title.charAt(i));
		buffer.append(ch);
		if (i < len - 1)
		    buffer.append(' ');
	    }
	    System.out.println(HEADER);
	    System.out.println(buffer.toString());
	    System.out.println(HEADER);
	}

	/**
	 * Display the standard report footer.
	 */
	private static void printFooter() {
	    System.out.println(FOOTER);
	    System.out.println();
	}

	/**
	 * Match an input string (presumably a command-line argument) with a list
	 * of choices (aliases for a report choice), and test to see if any match.
	 * <p> Note: the comparison is done ignoring letter case differences using
	 * {@link String#equalsIgnoreCase}.
	 *
	 * @param input		The input string to test against the choices.
	 * @param choices	The list of possible matches.
	 * @return		Whether or not any match was made.
	 */
	private static boolean matches(final String input, final String... choices) {
	    return Arrays.stream(choices).anyMatch(c -> input.equalsIgnoreCase(c));
	}

	/**
	 * Print a "usage" or "help" message to the print stream (either {@link System#out}
	 * or {@link System#err}).
	 *
	 * @param ps	The stream to output to.
	 */
	private static void usage(final PrintStream ps) {
	    int lineNo = 1;
	    do {
		String key = String.format("tools#os.help%1$d", lineNo++);
		String help = Intl.getOptionalString(key);
		if (help == null)
		    break;
		if (help.equals("**** PLACEHOLDER FOR OPTIONS - DO NOT TRANSLATE ****"))
		    Choice.displayAliases(ps, "    ", 72);
		else
		    ps.println(ConsoleColor.color(help, colors));
	    } while(true);
	}

	/**
	 * Loop through the given command line arguments, determining which reports
	 * have been selected to be run.
	 *
	 * @param args	The command line argument array.
	 * @return	Whether or not the matching succeeded (meaning all of the args
	 *		matched one of our choices); {@code true} means success,
	 *		while {@code false} indicates an error (the arg did not match
	 *		ANY of the choices).
	 */
	private static boolean parseArgs(final String[] args) {
	    for (String arg: args) {
		String opt = arg;
		String[] parts = null;

		if (arg.startsWith("--"))
		    opt = arg.substring(2);
		else if (arg.startsWith("-"))
		    opt = arg.substring(1);
		else if (arg.startsWith("/"))
		    opt = arg.substring(1);

		int pos = opt.indexOf(':');
		if (pos > 0) {
		    parts = opt.substring(pos + 1).split("[,;]\\s*|\\s+");
		    opt = opt.substring(0, pos);
		}

		Choice choice = Choice.match(opt);
		if (choice != null) {
		    choices.add(choice);
		}
		else if (matches(opt, "all", "a")) {
		    choices = EnumSet.allOf(Choice.class);
		}
		else if (matches(opt, "single", "nocolumns", "nocolumn", "nocol")) {
		    singleColumn = true;
		}
		else if (matches(opt, "notitles", "notitle", "not")) {
		    showTitles = false;
		}
		else if (matches(opt, "nocolors", "nocolor", "noc")) {
		    colors = false;
		    Intl.setColoring(colors);
		}
		else if (matches(opt, "verbose", "v")) {
		    verbose = true;
		}
		else if (matches(opt, "width", "w")) {
		    if (parts != null) {
			try {
			    screenWidth = Integer.parseInt(parts[0]);
			    if (screenWidth >= 60 && screenWidth <= 1000)
				continue;
			}
			catch (NumberFormatException nfe) {
			}
		    }
		    Intl.errFormat("tools#os.invalidWidth", arg);
		    return false;
		}
		else if (matches(opt, "filter", "filt")) {
		    if (parts != null) {
			for (String part : parts) {
			    filterValues.add(part);
			}
			continue;
		    }
		    Intl.errFormat("tools#os.invalidFilter", arg);
		    return false;
		}
		else if (matches(opt, "help", "h", "?")) {
		    usage(System.out);
		    return false;
		}
		else {
		    Intl.errFormat("tools#os.unknownChoice", arg);
		    usage(System.err);
		    return false;
		}
	    }
	    return true;
	}

	private static void pad(final StringBuilder lineBuf, final int width) {
	    int length = ConsoleColor.textLength(lineBuf);
	    while (length++ <= width)
		lineBuf.append(' ');
	}

	private static void display(final String title, final List<String> values) {
	    int size      = values.size();
	    int maxLength = 0;

	    for (String value : values)
		maxLength = Math.max(maxLength, ConsoleColor.textLength(value));
	    maxLength += 2;	// to leave some space b/w columns

	    int numberColumns = Math.max(1, screenWidth / maxLength);
	    int columnWidth   = screenWidth / numberColumns;

	    if (showTitles)
		printTitle(title);

	    if (singleColumn || numberColumns == 1) {
		values.stream().forEach(s -> System.out.println(ConsoleColor.color(s, colors)));
	    }
	    else {
		StringBuilder lineBuf = new StringBuilder(screenWidth);
		int numberRows = size / numberColumns;
		int remainder = size - (numberColumns * numberRows);

		int[] columnRows = new int[numberColumns];
		for (int column = 0; column < numberColumns; column++) {
		    columnRows[column] = numberRows;
		    if (column < remainder)
			columnRows[column]++;
		}
		if (remainder > 0)
		    numberRows++;

		for (int row = 0; row < numberRows; row++) {
		    lineBuf.setLength(0);
		    int index = row;
		    for (int col = 0; col < numberColumns; col++) {
			if (index < size && row < columnRows[col]) {
			    lineBuf.append(values.get(index));
			    if (col < numberColumns - 1)
				pad(lineBuf, (col + 1) * columnWidth);
			}
			index += columnRows[col];
		    }
		    System.out.println(ConsoleColor.color(lineBuf.toString(), colors));
		}
	    }

	    if (showTitles)
		printFooter();
	}

	/**
	 * Display the system properties list in sorted order by key.
	 */
	private static void displayProperties() {
	    Properties sysProperties = System.getProperties();

	    Set<String> sortedNames = new TreeSet<>();
	    sortedNames.addAll(sysProperties.stringPropertyNames());

	    List<String> props = new ArrayList<>(sortedNames.size());
	    for (String propertyName: sortedNames) {
		String value = sysProperties.getProperty(propertyName);
		props.add(String.format("<Bk!>%1$s<.> = <Gr>%2$s<.>", propertyName, value));
	    }

	    display("System Properties", props);
	}

	/**
	 * Display the environment in sorted order by variable name.
	 */
	private static void displayEnvironment() {
	    Map<String, String> env = new TreeMap<>(System.getenv());

	    List<String> envs = new ArrayList<>(env.size());
	    for (Map.Entry<String, String> entry : env.entrySet()) {
		envs.add(String.format("<Bk!>%1$s<.> = <Gr>%2$s<.>", entry.getKey(), entry.getValue()));
	    }

	    display("Environment", envs);
	}

	/**
	 * Display the available charsets sorted by the character set name.
	 * <p> The default charset is identified by an {@code "*"} in the first column.
	 */
	private static void displayCharsets() {
	    Map<String, Charset> charsets = new TreeMap<>(Charset.availableCharsets());
	    Charset defaultCharset = Charset.defaultCharset();

	    List<String> sets = new ArrayList<>(charsets.size());
	    for (Map.Entry<String, Charset> entry : charsets.entrySet()) {
		String prefix = (entry.getValue().equals(defaultCharset)) ? "<Rd!>*<.> " : "  ";
		sets.add(String.format("%1$s<Gr>%2$s<.>", prefix, entry.getKey()));
	    }

	    display("Character Sets", sets);
	}

	private static String countryCode(final Locale loc) {
	    try {
		String code = loc.getISO3Country();
		return (code == null || code.isEmpty()) ? EMPTY_CODE : code;
	    }
	    catch (MissingResourceException mre) {
		return EMPTY_CODE;
	    }
	}

	private static String languageCode(final Locale loc) {
	    try {
		String code = loc.getISO3Language();
		return (code == null || code.isEmpty()) ? EMPTY_CODE : code;
	    }
	    catch (MissingResourceException mre) {
		return EMPTY_CODE;
	    }
	}

	private static String variant(final Locale loc) {
	    String code = loc.getVariant();
	    return (code == null || code.isEmpty()) ? EMPTY_CODE : code;
	}

	/**
	 * Display the available locales (there are lots), sorted by the language tag.
	 * <p> The default locale is identified by an {@code "*"}.
	 */
	private static void displayLocales() {
	    Locale[] availableLocales = Locale.getAvailableLocales();
	    Locale defaultLocale = Locale.getDefault();

	    Map<String, Locale> sortedLocales = new TreeMap<>();
	    for (Locale loc : availableLocales) {
		sortedLocales.put(loc.toLanguageTag(), loc);
	    }

	    List<String> locs = new ArrayList<>(sortedLocales.size());
	  locales:
	    for (Map.Entry<String, Locale> entry : sortedLocales.entrySet()) {
		String tag = entry.getKey();
		Locale loc = entry.getValue();

		// We will use the tag to do the filtering
		boolean matchesFilter = false;
		if (!filterValues.isEmpty()) {
		  filters:
		    for (String filter : filterValues) {
			if (Match.hasWildCards(filter)) {
			    if (Match.stringMatch(tag, filter, false)) {
				matchesFilter = true;
				break filters;
			    }
			}
			else if (filter.equalsIgnoreCase(tag)) {
			    matchesFilter = true;
			    break filters;
			}
		    }
		}
		else {
		    matchesFilter = true;
		}

		if (!matchesFilter)
		    continue locales;

		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(loc);

		String prefix = (loc.equals(defaultLocale)) ? "<Rd!>*<.>" : "";
		String message = "";
		if (verbose) {
		    message = String.format(
			"%1$1s<Cy>%2$15s<.>  <Gr>%3$s<.>  <Yw!>%4$s<-->%n" +
			"\t\t<Bk!>Country: <Gr>%5$s  <Yw!>%6$s<-->%n" +
			"\t\t<Bk!>language: <Gr>%7$s<Bk!>  country: <Gr>%8$s<Bk!>  variant: <Gr>%9$s<-->%n" +
			"\t\t<Bk!>currency: <Gr>%10$s<Bk!>  symbol: <Gr>%11$s<-->%n" +
			"\t\t<Bk!>minus: <Gr>%12$c<Bk!>  decimal: <Gr>%13$c<Bk!>  grouping: <Gr>%14$c<Bk!>  exponent: <Gr>%15$s<-->%n" +
			"\t\t<Bk!>Infinity: <Gr>%16$s<Bk!>  NaN: <Gr>%17$s<Bk!>  percent: <Gr>%18$c<-->",
			prefix, tag, loc.getDisplayName(), loc.getDisplayName(loc),
			loc.getDisplayCountry(), loc.getDisplayCountry(loc),
			languageCode(loc), countryCode(loc), variant(loc),
			symbols.getCurrencySymbol(), symbols.getInternationalCurrencySymbol(),
			symbols.getMinusSign(), symbols.getDecimalSeparator(),
			symbols.getGroupingSeparator(), symbols.getExponentSeparator(),
			symbols.getInfinity(), symbols.getNaN(), symbols.getPercent());
		}
		else {
		    message = String.format(
			"%1$1s<Cy>%2$15s<.>  <Gr>%3$s<.>  <Yw!>%4$s<-->",
			prefix, tag, loc.getDisplayName(), loc.getDisplayName(loc));
		}
		locs.add(message);
	    }

	    display("Locales", locs);
	}

	/**
	 * A comparator of {@link Currency} objects, sorting by display name.
	 *
	 * @param p1	The first object to compare.
	 * @param p2	The second object to compare to the first.
	 * @return	&lt; 0 if the name of the first is less than the
	 *		name of the second, = 0 if the names are equal,
	 *		and &gt; 0 if the name of the first is greater
	 *		than the name of the second.
	 */
	private static int compareCurrencies(Currency c1, Currency c2) {
	    return c1.getDisplayName().compareTo(c2.getDisplayName());
	}

	/**
	 * Display the available currencies, sorted by name.
	 */
	private static void displayCurrencies() {
	    Set<Currency> availableCurrencies = Currency.getAvailableCurrencies();
	    Set<Currency> sortedCurrencies    = new TreeSet<>(OS::compareCurrencies);
	    List<String> currencies           = new ArrayList<>(availableCurrencies.size());

	    for (Currency c : availableCurrencies) {
		sortedCurrencies.add(c);
	    }

	    for (Currency c : sortedCurrencies) {
		String message = String.format("<Cy>%1$45s<.> (<Gr>%2$s<.>) (<Yw!>%3$3d<.>)  <Bk!>symbol: <Bl>%4$-7s<.> <Bk!>digits: <Bl>%5$d<-->",
			c.getDisplayName(), c.getCurrencyCode(), c.getNumericCode(),
			CharUtil.addDoubleQuotes(c.getSymbol()), c.getDefaultFractionDigits());

		currencies.add(message);
	    }

	    display("Currencies", currencies);
	}

	/**
	 * Display the available message digests, sorted by algorithm name.
	 */
	private static void displayDigests() {
	    Set<String> availableDigests = Security.getAlgorithms("MessageDigest");
	    Set<String> sortedDigests    = new TreeSet<>(availableDigests);
	    List<String> digests         = new ArrayList<>(sortedDigests);

	    display("Message Digests", digests);
	}

	/**
	 * A comparator of {@link Provider} objects, sorting by name.
	 *
	 * @param p1	The first object to compare.
	 * @param p2	The second object to compare to the first.
	 * @return	&lt; 0 if the name of the first is less than the
	 *		name of the second, = 0 if the names are equal,
	 *		and &gt; 0 if the name of the first is greater
	 *		than the name of the second.
	 */
	private static int compareProviders(Provider p1, Provider p2) {
	    return p1.getName().compareTo(p2.getName());
	}

	/**
	 * Display the sorted list of security providers.
	 */
	private static void displayProviders() {
	    Provider[] providers = Security.getProviders();
	    Arrays.sort(providers, OS::compareProviders);

	    final List<String> provs = new ArrayList<>(providers.length);
	    Arrays.stream(providers).forEach(p -> provs.add(String.format("<Bk!>%1$12s<.>: <Gr>%2$s<.>", p.getName(), p.getInfo())));

	    display("Security Providers", provs);
	}

	/**
	 * Display the sorted list of fonts available in the graphics environment.
	 */
	private static void displayFonts() {
	    GraphicsEnvironment graphicsEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    String[] fontFamilies = graphicsEnv.getAvailableFontFamilyNames();
	    Arrays.sort(fontFamilies, String.CASE_INSENSITIVE_ORDER);

	    display("Font Families", Arrays.asList(fontFamilies));
	}

	/**
	 * Sort a {@link TimeZone} first by offset, then display name.
	 *
	 * @param z1 The first TimeZone to compare.
	 * @param z2 The second TimeZone to compare to.
	 * @return   {@code < 0} if {@code z1 < z2}, {@code =0} if they are the same,
	 *	     {@code > 0} if {@code z1 > z2}.
	 */
	private static int compareTimeZones(TimeZone z1, TimeZone z2) {
	    int offsetZ1 = z1.getRawOffset();
	    int offsetZ2 = z2.getRawOffset();
	    int ret = Integer.compare(offsetZ1, offsetZ2);
	    if (ret != 0)
		return ret;

	    String nameZ1 = z1.getDisplayName();
	    String nameZ2 = z2.getDisplayName();
	    return nameZ1.compareTo(nameZ2);
	}

	/**
	 * Format an offset in milliseconds to a "hh:mm" representation.
	 *
	 * @param offset	The offset value in milliseconds.
	 * @return		The value formatted as "hh:mm".
	 */
	private static String tzOffset(int offset) {
	    int rawMinutes = Math.abs(offset) / (1000 * 60);
	    int hours      = rawMinutes / 60;
	    int minutes    = rawMinutes - (hours * 60);

	    return String.format("%1$s%2$d:%3$02d", offset < 0 ? "-" : "", hours, minutes);
	}

	/**
	 * Format a timezone name for display.
	 *
	 * @param tz		The timezone under consideration.
	 * @param daylight	Whether the name should be the daylight savings time name or not.
	 * @return		A formatted string suitable for display.
	 */
	private static String tzDisplayName(TimeZone tz, boolean daylight) {
	    return String.format("<Gr>%1$s<.>: <Bk!>%2$s<.> <Bl!>(%3$s)<.> <Cy>[%4$s]<.>",
		tz.getID(),
		tz.getDisplayName(daylight, TimeZone.LONG),
		tz.getDisplayName(daylight, TimeZone.SHORT),
		tzOffset(daylight ? tz.getOffset(now.getTime()) : tz.getRawOffset()));
	}

	/**
	 * Display the list of available timezone ids.
	 */
	private static void displayTimeZones() {
	    boolean sawDefault = false;;
	    TimeZone defaultZone = TimeZone.getDefault();
	    String[] availableIDs = TimeZone.getAvailableIDs();
	    TimeZone[] availableZones = new TimeZone[availableIDs.length];

	    for (int i = 0; i < availableIDs.length; i++) {
		availableZones[i] = TimeZone.getTimeZone(availableIDs[i]);
		if (availableZones[i].equals(defaultZone))
		    sawDefault = true;
	    }

	    final List<String> zones = new ArrayList<>(availableZones.length * 2);
	    Arrays.sort(availableZones, OS::compareTimeZones);

	    Arrays.stream(availableZones).forEach(tz -> {
		boolean isDefault = tz.equals(defaultZone);
		String defMarker = isDefault ? "<Rd!>*<.> " : "  ";
		zones.add(String.format("%1$s%2$s", defMarker, tzDisplayName(tz, false)));

		if (tz.observesDaylightTime()) {
		    defMarker = isDefault && tz.inDaylightTime(now) ? "<Rd!>+<.> " : "  ";
		    zones.add(String.format("%1$s%2$s", defMarker, tzDisplayName(tz, true)));
		}
	    });

	    display("Time Zones", zones);

	    if (!sawDefault) {
		printTitle("Default Time Zone");
		String value = String.format("<Rd!>*<.> %1$s", tzDisplayName(defaultZone, false));
		System.out.println(ConsoleColor.color(value, colors));
		if (defaultZone.observesDaylightTime()) {
		    value = String.format("<Rd!>+<.> %1$s", tzDisplayName(defaultZone, true));
		    System.out.println(ConsoleColor.color(value, colors));
		}
		printFooter();
	    }
	}


	/**
	 * Parse the command line arguments to get the list of desired reports,
	 * then display them in turn.
	 *
	 * @param args	The parsed command line arguments.
	 */
	public static void main(String[] args) {
	    screenWidth = Environment.consoleWidth();
	    Intl.setColoring(colors);

	    Options.environmentOptions(OS.class, (options) -> {
		if (!parseArgs(options))
		    System.exit(1);
	    });

	    if (!parseArgs(args))
		System.exit(1);

	    if (choices.isEmpty())
		choices.add(Choice.PROPERTIES);

	    choices.forEach(Choice::display);
	}
}

