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
 */
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
	private static final String HEADER = "=======================================";
	/** Final banner for the end of the reports. */
	private static final String FOOTER = "---------------------------------------";

	/**
	 * What the user would like to display, and the method to display each bit.
	 */
	private enum Choice
	{
		PROPERTIES	(OS::displayProperties),
		ENVIRONMENT	(OS::displayEnvironment),
		CHARSETS	(OS::displayCharsets),
		LOCALES		(OS::displayLocales),
		DIGESTS		(OS::displayDigests),
		PROVIDERS	(OS::displayProviders);

		private Runnable displayer;

		Choice(Runnable disp) {
		    this.displayer = disp;
		}

		void display() {
		    displayer.run();
		}
	}

	/**
	 * The set of reports to be run. More than one may be selected.
	 */
	private static Set<Choice> choices = EnumSet.noneOf(Choice.class);


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
	 * Loop through the given command line arguments, determining which reports
	 * have been selected to be run.
	 *
	 * @param args	The command line argument array.
	 * @return	Whether or not the matching succeeded (meaning all of the args
	 *		matchied one of our choices); {@code true} means success,
	 *		while {@code false} indicates an error (the arg did not match
	 *		ANY of the choices).
	 */
	private static boolean parseArgs(final String[] args) {
	    for (String arg: args) {
		if (matches(arg, "properties", "props", "p")) {
		    choices.add(Choice.PROPERTIES);
		}
		else if (matches(arg, "environment", "environ", "env", "e")) {
		    choices.add(Choice.ENVIRONMENT);
		}
		else if (matches(arg, "charsets", "charset", "chars", "char", "ch", "c")) {
		    choices.add(Choice.CHARSETS);
		}
		else if (matches(arg, "locales", "locale", "loc", "l")) {
		    choices.add(Choice.LOCALES);
		}
		else if (matches(arg, "message-digests", "message_digests", "messagedigests",
				"digests", "digest", "dig", "d", "m", "md")) {
		    choices.add(Choice.DIGESTS);
		}
		else if (matches(arg, "security-providers", "security_providers",
				"securityproviders", "providers", "security", "prov", "sec", "s", "sp")) {
		    choices.add(Choice.PROVIDERS);
		}
		else if (matches(arg, "all", "a")) {
		    choices = EnumSet.allOf(Choice.class);
		}
		else {
		    System.err.println("Unknown choice value of \"" + arg + "\".");
		    System.err.println("Valid choices are: 'properties', 'environment', 'charsets', 'locales', 'digests', or 'all'.");
		    System.err.println("  (default is 'properties')");
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
	    System.out.println(FOOTER);
	    System.out.println();
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
	    System.out.println(FOOTER);
	    System.out.println();
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
	    System.out.println(FOOTER);
	    System.out.println();
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
	    System.out.println(FOOTER);
	    System.out.println();
	}

	/**
	 * Display the available message digests, sorted by algorithm name.
	 */
	private static void displayDigests() {
	    Set<String> availableDigests = Security.getAlgorithms("MessageDigest");
	    Set<String> sortedDigests    = new TreeSet<>(availableDigests);

	    printTitle("Message Digests");
	    sortedDigests.forEach(System.out::println);
	    System.out.println(FOOTER);
	    System.out.println();
	}

	private static int compare(Provider p1, Provider p2) {
	    return p1.getName().compareTo(p2.getName());
	}

	/**
	 * Display the sorted list of security providers.
	 */
	private static void displayProviders() {
	    Provider[] providers = Security.getProviders();
	    Arrays.sort(providers, OS::compare);

	    printTitle("Security Providers");
	    Arrays.stream(providers).forEach(p -> System.out.format("%1$12s: %2$s%n", p.getName(), p.getInfo()));
	    System.out.println(FOOTER);
	    System.out.println();
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

