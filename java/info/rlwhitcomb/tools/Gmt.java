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

	/** Date format to use for display. */
	private static String dateFormat = DEFAULT_FORMAT;
	/** Parsed timezone offset from GMT to use. */
	private static int tzOffset = 0;
	/** Or a timezone selected by ID to use. */
	private static TimeZone selectedZone = null;


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
		String format = arg;

		switch (format.toLowerCase()) {
		    case "-log":
		    case "log":
		    case "-l":
		    case "l":
			dateFormat = LOGGING_FORMAT;
			break;
		    case "-date":
		    case "date":
		    case "-d":
		    case "d":
			dateFormat = DATE_FORMAT;
			break;
		    case "-iso":
		    case "iso":
		    case "-i":
		    case "i":
			dateFormat = ISO_8601_FORMAT;
			break;
		    case "-default":
		    case "default":
		    case "-def":
		    case "def":
			dateFormat = DEFAULT_FORMAT;
			break;
		    case "-help":
		    case "help":
		    case "-h":
		    case "h":
		    case "-?":
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

	    SimpleDateFormat fmt = new SimpleDateFormat(dateFormat);
	    TimeZone gmt = selectedZone == null ? TimeZone.getTimeZone(getZone(tzOffset)) : selectedZone;
	    Calendar now = Calendar.getInstance(gmt);
	    fmt.setCalendar(now);

	    StringBuffer buf = new StringBuffer();
	    fmt.format(now.getTime(), buf, new FieldPosition(DateFormat.DATE_FIELD));

	    switch (dateFormat) {
		case DATE_FORMAT:
		    sub(buf, 4);
		    break;
		case DEFAULT_FORMAT:
		    sub(buf, 8);
		    sub(buf, 16);
		    break;
	    }

	    System.out.println(buf.toString());
	}
}

