/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Roger L. Whitcomb.
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
 *   Display the time in GMT.
 *
 * History:
 *	23-Feb-2021 (rlwhitcomb)
 *	    Initial implementation.
 *	23-Feb-2021 (rlwhitcomb)
 *	    Add options.
 *	03-Mar-2021 (rlwhitcomb)
 *	    Tweak the output so it matches what we're emulating better.
 */
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

		if (format.equalsIgnoreCase("-log")
		 || format.equalsIgnoreCase("log")
		 || format.equalsIgnoreCase("-l")
		 || format.equalsIgnoreCase("l"))
		    dateFormat = LOGGING_FORMAT;
		else if (format.equalsIgnoreCase("-date")
		      || format.equalsIgnoreCase("date")
		      || format.equalsIgnoreCase("-d")
		      || format.equalsIgnoreCase("d"))
		    dateFormat = DATE_FORMAT;
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

