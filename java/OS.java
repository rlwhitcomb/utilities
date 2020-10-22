/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Roger L. Whitcomb.
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
 *    02-Oct-2020 (rlwhitcomb)
 *	First version, based on older code.
 *    19-Oct-2020 (rlwhitcomb)
 *	Added MessageDigest list.
 *    19-Oct-2020 (rlwhitcomb)
 *	Allow multiple choices. Make the titles look better.
 *    20-Oct-2020 (rlwhitcomb)
 *	Use streams and a FunctionalInterface to make the code cleaner.
 *	Add some Javadoc. Add the list of security providers.
 *    21-Oct-2020 (rlwhitcomb)
 *	More code cleanup; move the command line choice matching into
 *	the Choice enum itself; display all the choices for the "help",
 *	and implement a "help" option.
 */
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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

	/**
	 * What the user would like to display, the method to display each bit, and
	 * the possible names to match.
	 */
	private enum Choice
	{
		PROPERTIES	(OS::displayProperties,
				 "properties", "props", "p"),
		ENVIRONMENT	(OS::displayEnvironment,
				 "environment", "environ", "env", "e"),
		CHARSETS	(OS::displayCharsets,
				 "charsets", "charset", "chars", "char", "ch", "c"),
		LOCALES		(OS::displayLocales,
				 "locales", "locale", "loc", "l"),
		DIGESTS		(OS::displayDigests,
				 "message-digests", "message_digests", "messagedigests",
				 "digests", "digest", "dig", "d", "m", "md"),
		PROVIDERS	(OS::displayProviders,
				 "security-providers", "security_providers",
				 "securityproviders", "providers", "security", "prov",
				 "sec", "s", "sp");

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
			c -> ps.format("%1$s,%n", makeArrayString(c.aliasNames, indent, width)));
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
		if (printWidth > width) {
		    output.append('\n').append(indent);
		    printWidth = indentWidth;
		}
		output.append('"').append(value).append('"');
		printWidth += value.length() + 2;
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
	    ps.println("Usage: java OS [choice]*");
	    ps.println("Valid choices are:");
	    Choice.displayAliases(ps, "    ", 60);
	    ps.println(" or \"all\" (default is \"properties\"),");
	    ps.println(" or \"help\", \"h\", or \"?\" to display this message.");
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
		Choice choice = Choice.match(arg);
		if (choice != null) {
		    choices.add(choice);
		}
		else if (matches(arg, "all", "a")) {
		    choices = EnumSet.allOf(Choice.class);
		}
		else if (matches(arg, "help", "h", "?")) {
		    usage(System.out);
		    return false;
		}
		else {
		    System.err.println("Unknown choice value of \"" + arg + "\".");
		    System.err.println();
		    usage(System.err);
		    return false;
		}
	    }
	    return true;
	}

	/**
	 * Display the system properties list in sorted order by key.
	 */
	private static void displayProperties() {
	    Properties sysProperties = System.getProperties();

	    Set<String> sortedNames = new TreeSet<>();
	    sortedNames.addAll(sysProperties.stringPropertyNames());

	    printTitle("System Properties");
	    for (String propertyName: sortedNames) {
		String value = sysProperties.getProperty(propertyName);
		System.out.format("%1$s = %2$s%n", propertyName, value);
	    }
	    printFooter();
	}

	/**
	 * Display the environment in sorted order by variable name.
	 */
	private static void displayEnvironment() {
	    Map<String, String> env = new TreeMap<>(System.getenv());

	    printTitle("Environment");
	    for (Map.Entry<String, String> entry : env.entrySet()) {
		System.out.format("%1$s = %2$s%n", entry.getKey(), entry.getValue());
	    }
	    printFooter();
	}

	/**
	 * Display the available charsets sorted by the character set name.
	 * <p> The default charset is identified by an {@code "*"} in the first column.
	 */
	private static void displayCharsets() {
	    Map<String, Charset> charsets = new TreeMap<>(Charset.availableCharsets());
	    Charset defaultCharset = Charset.defaultCharset();

	    printTitle("Character Sets");
	    for (Map.Entry<String, Charset> entry : charsets.entrySet()) {
		String prefix = (entry.getValue().equals(defaultCharset)) ? "* " : "  ";
		System.out.println(prefix + entry.getKey());
	    }
	    printFooter();
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

	    printTitle("Locales");
	    for (Map.Entry<String, Locale> entry : sortedLocales.entrySet()) {
		String tag = entry.getKey();
		Locale loc = entry.getValue();
		String prefix = (loc.equals(defaultLocale)) ? "*" : "";
		System.out.format("%1$1s%2$15s  %3$s%n", prefix, tag, loc.getDisplayName());
	    }
	    printFooter();
	}

	/**
	 * Display the available message digests, sorted by algorithm name.
	 */
	private static void displayDigests() {
	    Set<String> availableDigests = Security.getAlgorithms("MessageDigest");
	    Set<String> sortedDigests    = new TreeSet<>(availableDigests);

	    printTitle("Message Digests");
	    sortedDigests.forEach(System.out::println);
	    printFooter();
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

	    printTitle("Security Providers");
	    Arrays.stream(providers).forEach(p -> System.out.format("%1$12s: %2$s%n", p.getName(), p.getInfo()));
	    printFooter();
	}

	/**
	 * Parse the command line arguments to get the list of desired reports,
	 * then display them in turn.
	 *
	 * @param args	The parsed command line arguments.
	 */
	public static void main(String[] args) {
	    if (!parseArgs(args))
		System.exit(1);

	    if (choices.isEmpty())
		choices.add(Choice.PROPERTIES);

	    choices.forEach(Choice::display);
	}
}

