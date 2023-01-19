/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021-2022 Roger L. Whitcomb.
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
 *	Display the time in GMT.
 *
 * History:
 *  23-Feb-21 rlw  ---	Initial implementation.
 *  23-Feb-21 rlw  ---	Add options.
 *  03-Mar-21 rlw  ---	Tweak the output so it matches what we're emulating better.
 *  29-Mar-21 rlw  ---	Move to new package.
 *  19-Dec-21 rlw #158:	Add ISO-8601 format and help.
 *  01-Feb-22 rlw  ---	ISO format doesn't need any fixup; add milliseconds there.
 *  09-Jul-22 rlw #393:	Cleanup imports.
 *  25-Oct-22 rlw #18:	Allow +/-nn on command line to get offset from GMT.
 *			Tweak the date formats.
 *			Allow arbitrary timezone selection.
 *  02-Nov-22 rlw #545:	Get default options from the environment; add "-default" option;
 *			straighten out error handling on timezone selection.
 *  14-Dec-22 rlw #561:	Display in seconds or milliseconds (raw). Three new formats: RFC822,
 *			RFC850, and "asctime()".
 */
package info.rlwhitcomb.tools;

import info.rlwhitcomb.util.CharUtil;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.Options;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;


/**
 * Display the current GMT time.
 */
public class Gmt
{
	/** Output format compatible with *nix "date" command. */
	private static final String DATE_FORMAT    = "E dd MMM yyyy hh:mm:ss a z";
	/** Default output format. */
	private static final String DEFAULT_FORMAT = "E MMM dd,yyyy HH:mm:ss.SSS z";
	/** Output format compatible with our {@code Logging} class. */
	private static final String LOGGING_FORMAT = "MMM dd,yyyy HH:mm:ss.SSS z";
	/** Output format compatible with ISO-8601 format. */
	private static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
	/** Output format per RFC 822:
	 * Sun, 06 Nov 1994 08:49:37 +0000
	 * [RFC 822](https://www.rfc-editor.org/rfc/rfc822),
	 * updated by [RFC 1123](https://www.rfc-editor.org/rfc/rfc1123) */
	private static final String RFC_822_FORMAT = "E, dd MMM yyyy HH:mm:ss Z";
	/** Output format per RFC 850:
	 * Sunday, 06-Nov-94 08:49:37 GMT
	 * [RFC 850](https://www.rfc-editor.org/rfc/rfc850),
	 * obsoleted by [RFC 1036](https://www.rfc-editor.org/rfc/rfc1036) */
	private static final String RFC_850_FORMAT = "EEEE, dd-MMM-yy HH:mm:ss z";
	/** Output format according to ANSI C's asctime() format:
	 * Sun Nov  6 08:49:37 1994 */
	private static final String ASCTIME_FORMAT = "E MMM dd HH:mm:ss yyyy";

	/** Date format to use for display. */
	private static String dateFormat = DEFAULT_FORMAT;
	/** Parsed timezone offset from GMT to use. */
	private static int tzOffset = 0;
	/** Or a timezone selected by ID to use. */
	private static TimeZone selectedZone = null;

	/** Option to just display epoch seconds instead of formatted. */
	private static boolean displaySeconds = false;
	/** And correspondingly, display the raw epoch milliseconds. */
	private static boolean displayMillis = false;


	/**
	 * Get a timezone ID with the given (hours) offset from GMT.
	 *
	 * @param offset The number of hours difference from GMT.
	 * @return       An "Etc/GMT..." zone with that offset (but negative gives "+").
	 */
	private static String getZone(int offset) {
	    if (offset == 0)
		return "Etc/GMT";
	    else if (offset < 0)
		return String.format("Etc/GMT+%1$d", -offset);
	    else
		return String.format("Etc/GMT-%1$d", offset);
	}

	private static void sub(StringBuffer buf, int charPos) {
	    if (buf.charAt(charPos) == '0')
		buf.setCharAt(charPos, ' ');
	}

	/**
	 * Process the command-line arguments.
	 *
	 * @param args The arguments parsed either from the environment options,
	 * or the command line.
	 */
	private static void processArgs(String[] args) {
	    for (String arg : args) {
		String format = arg.replaceAll("^\\-\\-?", "");

		switch (format.toLowerCase()) {
		    case "log":
		    case "l":
			displaySeconds = displayMillis = false;
			dateFormat = LOGGING_FORMAT;
			break;
		    case "date":
		    case "d":
			displaySeconds = displayMillis = false;
			dateFormat = DATE_FORMAT;
			break;
		    case "iso":
		    case "i":
			displaySeconds = displayMillis = false;
			dateFormat = ISO_8601_FORMAT;
			break;
		    case "default":
		    case "def":
			displaySeconds = displayMillis = false;
			dateFormat = DEFAULT_FORMAT;
			break;
		    case "rfc822":
		    case "822":
			displaySeconds = displayMillis = false;
			dateFormat = RFC_822_FORMAT;
			break;
		    case "rfc850":
		    case "850":
			displaySeconds = displayMillis = false;
			dateFormat = RFC_850_FORMAT;
			break;
		    case "ansic":
		    case "ansi":
		    case "asc":
		    case "a":
			displaySeconds = displayMillis = false;
			dateFormat = ASCTIME_FORMAT;
			break;

		    case "seconds":
		    case "secs":
		    case "sec":
		    case "s":
			displaySeconds = true;
			displayMillis = false;
			break;
		    case "millis":
		    case "mills":
		    case "milli":
		    case "mill":
		    case "m":
			displayMillis = true;
			displaySeconds = false;
			break;

		    case "help":
		    case "h":
		    case "?":
			Intl.printHelp("tools#gmt");
			System.exit(0);

		    default:
			// Could be a signed tz offset from GMT
			if (CharUtil.isValidSignedInt(arg)) {
			    tzOffset = Integer.parseInt(arg);
			    if (tzOffset < -12 || tzOffset > 14) {
				Intl.errFormat("tools#gmt.badZoneOffset", tzOffset);
				System.exit(2);
			    }
			    selectedZone = null;
			}
			else {
			    String[] zones = TimeZone.getAvailableIDs();
			    selectedZone = null;
			    for (String zone : zones) {
				if (arg.equalsIgnoreCase(zone)) {
				    selectedZone = TimeZone.getTimeZone(zone);
				    tzOffset = 0;
				    break;
				}
			    }
			    if (selectedZone == null) {
				Intl.errFormat("tools#gmt.badOption", arg);
				System.exit(1);
			    }
			}
			break;
		}
	    }
	}

	/**
	 * @param args The parsed command line argument array.
	 */
	public static void main(String[] args) {
	    Options.environmentOptions(Gmt.class, a -> processArgs(a));

	    processArgs(args);

	    TimeZone gmt = selectedZone == null ? TimeZone.getTimeZone(getZone(tzOffset)) : selectedZone;
	    Calendar now = Calendar.getInstance(gmt);

	    StringBuffer buf = new StringBuffer();

	    if (displayMillis || displaySeconds) {
		long rawTime = now.getTimeInMillis();
		if (displaySeconds)
		    rawTime /= 1000L;
		buf.append(rawTime);
	    }
	    else {
		SimpleDateFormat fmt = new SimpleDateFormat(dateFormat);
		fmt.setCalendar(now);

		fmt.format(now.getTime(), buf, new FieldPosition(DateFormat.DATE_FIELD));

		switch (dateFormat) {
		    case DATE_FORMAT:
			sub(buf, 4);
			break;
		    case DEFAULT_FORMAT:
			sub(buf, 8);
			sub(buf, 16);
			break;
		    case ASCTIME_FORMAT:
			sub(buf, 8);
			break;
		}
	    }

	    System.out.println(buf.toString());
	}
}

