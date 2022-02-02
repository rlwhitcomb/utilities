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
 *  Change History:
 *	23-Feb-2021 (rlwhitcomb)
 *	    Initial implementation.
 *	23-Feb-2021 (rlwhitcomb)
 *	    Add options.
 *	03-Mar-2021 (rlwhitcomb)
 *	    Tweak the output so it matches what we're emulating better.
 *	29-Mar-2021 (rlwhitcomb)
 *	    Move to new package.
 *	19-Dec-2021 (rlwhitcomb)
 *	    #158: Add ISO-8601 format and help.
 *	01-Feb-2022 (rlwhitcomb)
 *	    ISO format doesn't need any fixup; add milliseconds there.
 */
package info.rlwhitcomb.tools;

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
	private static final String DATE_FORMAT    = "E MMM dd HH:mm:ss z yyyy";
	/** Default output format. */
	private static final String DEFAULT_FORMAT = "E MMM dd,yyyy HH:mm:ss.SSS z";
	/** Output format compatible with our {@code Logging} class. */
	private static final String LOGGING_FORMAT = "MMM dd,yyyy HH:mm:ss.SSS z";
	/** Output format compatible with ISO-8601 format. */
	private static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	/** The help text for this program. */
	private static final String[] HELP = {
	    "",
	    "Usage: gmt [options]",
	    "",
	    "\twhere 'options' can be:",
	    "\t  -log  = display in format compatible with our Logging class",
	    "\t  -date = display compatible with Linux 'date' command",
	    "\t  -iso  = display in ISO-8601 format",
	    "",
	    "\tNote: options can be with or without '-' or can be just the first character.",
	    "",
	    "\tExamples:",
	    "\t  -log  -> Dec 20,2021 06:45:49.829 GMT",
	    "\t  -date -> Mon Dec 20  6:46:19 GMT 2021",
	    "\t  -iso  -> 2021-12-20T06:46:43.526Z",
	    "\tdefault -> Mon Dec 20,2021  6:48:22.584 GMT",
	    ""
	};


	private static void sub(StringBuffer buf, int charPos) {
	    if (buf.charAt(charPos) == '0')
		buf.setCharAt(charPos, ' ');
	}

	/**
	 * @param args The parsed command line argument array.
	 */
	public static void main(String[] args) {
	    String dateFormat = DEFAULT_FORMAT;

	    if (args.length > 0) {
		String format = args[0];

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
		    case "-help":
		    case "help":
		    case "-h":
		    case "h":
		    case "-?":
		    case "?":
			for (String helpLine : HELP) {
			    System.out.println(helpLine);
			}
			return;
		}
	    }

	    SimpleDateFormat fmt = new SimpleDateFormat(dateFormat);
	    TimeZone gmt = TimeZone.getTimeZone("Etc/GMT");
	    Calendar now = Calendar.getInstance(gmt);
	    fmt.setCalendar(now);

	    StringBuffer buf = new StringBuffer();
	    fmt.format(now.getTime(), buf, new FieldPosition(DateFormat.DATE_FIELD));
	    int size = buf.length() - 6;
	    if (buf.charAt(size) == '+') {
		buf.setLength(size);
	    }

	    switch (dateFormat) {
		case DATE_FORMAT:
		    sub(buf, 8);
		    sub(buf, 11);
		    break;
		case DEFAULT_FORMAT:
		    sub(buf, 8);
		    sub(buf, 16);
		    break;
	    }

	    System.out.println(buf.toString());
	}
}

