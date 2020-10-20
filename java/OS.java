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
 */
import java.nio.charset.Charset;
import java.security.Security;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class OS
{
	private static final String HEADER = "=====================================";
	private static final String FOOTER = "-------------------------------------";

	/**
	 * What the user would like to display.
	 */
	private enum Choice
	{
		PROPERTIES,
		ENVIRONMENT,
		CHARSETS,
		LOCALES,
		DIGESTS,
		ALL
	}

	private static Choice choice = Choice.PROPERTIES;


	private static boolean matches(final String input, String... choices) {
	    for (String choice : choices) {
		if (input.equalsIgnoreCase(choice))
		    return true;
	    }
	    return false;
	}

	private static boolean parseArgs(final String[] args) {
	    for (String arg: args) {
		if (matches(arg, "properties", "props", "p")) {
		    choice = Choice.PROPERTIES;
		}
		else if (matches(arg, "environment", "environ", "env", "e")) {
		    choice = Choice.ENVIRONMENT;
		}
		else if (matches(arg, "charsets", "charset", "chars", "char", "ch", "c")) {
		    choice = Choice.CHARSETS;
		}
		else if (matches(arg, "locales", "locale", "loc", "l")) {
		    choice = Choice.LOCALES;
		}
		else if (matches(arg, "message-digests", "message_digests", "messagedigests",
				"digests", "digest", "dig", "d", "m", "md")) {
		    choice = Choice.DIGESTS;
		}
		else if (matches(arg, "all", "a")) {
		    choice = Choice.ALL;
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

	private static void displayProperties() {
	    Properties sysProperties = System.getProperties();

	    Set<String> sortedNames = new TreeSet<>();
	    sortedNames.addAll(sysProperties.stringPropertyNames());

	    System.out.println("System Properties");
	    System.out.println(HEADER);
	    for (String propertyName: sortedNames) {
		String value = sysProperties.getProperty(propertyName);
		System.out.format("%1$s = %2$s%n", propertyName, value);
	    }
	    System.out.println(FOOTER);
	    System.out.println();
	}

	private static void displayEnvironment() {
	    Map<String, String> env = new TreeMap<>(System.getenv());

	    System.out.println("Environment");
	    System.out.println(HEADER);
	    for (Map.Entry<String, String> entry : env.entrySet()) {
		System.out.format("%1$s = %2$s%n", entry.getKey(), entry.getValue());
	    }
	    System.out.println(FOOTER);
	    System.out.println();
	}

	private static void displayCharsets() {
	    Map<String, Charset> charsets = new TreeMap<>(Charset.availableCharsets());
	    Charset defaultCharset = Charset.defaultCharset();

	    System.out.println("Character Sets");
	    System.out.println(HEADER);
	    for (Map.Entry<String, Charset> entry : charsets.entrySet()) {
		String prefix = (entry.getValue().equals(defaultCharset)) ? "* " : "  ";
		System.out.println(prefix + entry.getKey());
	    }
	    System.out.println(FOOTER);
	    System.out.println();
	}

	private static void displayLocales() {
	    Locale[] availableLocales = Locale.getAvailableLocales();
	    Locale defaultLocale = Locale.getDefault();

	    Map<String, Locale> sortedLocales = new TreeMap<>();
	    for (Locale loc : availableLocales) {
		sortedLocales.put(loc.toLanguageTag(), loc);
	    }

	    System.out.println("Locales");
	    System.out.println(HEADER);
	    for (Map.Entry<String, Locale> entry : sortedLocales.entrySet()) {
		String tag = entry.getKey();
		Locale loc = entry.getValue();
		String prefix = (loc.equals(defaultLocale)) ? "*" : "";
		System.out.format("%1$1s%2$15s  %3$s%n", prefix, tag, loc.getDisplayName());
	    }
	    System.out.println(FOOTER);
	    System.out.println();
	}

	private static void displayDigests() {
	    Set<String> availableDigests = Security.getAlgorithms("MessageDigest");
	    Set<String> sortedDigests    = new TreeSet<>(availableDigests);

	    System.out.println("Message Digests");
	    System.out.println(HEADER);
	    sortedDigests.forEach(name -> System.out.println(name));
	    System.out.println(FOOTER);
	    System.out.println();
	}

	public static void main(String[] args) {
	    if (!parseArgs(args))
		System.exit(1);

	    switch (choice) {
		case PROPERTIES:
		    displayProperties();
		    break;
		case ENVIRONMENT:
		    displayEnvironment();
		    break;
		case CHARSETS:
		    displayCharsets();
		    break;
		case LOCALES:
		    displayLocales();
		    break;
		case DIGESTS:
		    displayDigests();
		    break;
		case ALL:
		    displayProperties();
		    displayEnvironment();
		    displayCharsets();
		    displayLocales();
		    displayDigests();
		    break;
	    }
	}
}

