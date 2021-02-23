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
	/**
	 * @param args The parsed command line argument array.
	 */
	public static void main(String[] args) {
	    TimeZone gmt = TimeZone.getTimeZone("Etc/GMT");
	    SimpleDateFormat fmt = new SimpleDateFormat("E MMM dd,yyyy HH:mm:ss.SSS z");
	    Calendar now = Calendar.getInstance(gmt);
	    fmt.setCalendar(now);
	    StringBuffer buf = new StringBuffer();
	    fmt.format(now.getTime(), buf, new FieldPosition(DateFormat.DATE_FIELD));
	    int size = buf.length() - 6;
	    if (buf.charAt(size) == '+') {
		buf.setLength(size);
	    }
	    System.out.println(buf.toString());
	}
}

