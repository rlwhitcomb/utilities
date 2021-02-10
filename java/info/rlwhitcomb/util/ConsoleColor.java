/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2021 Roger L. Whitcomb.
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
 * Escape sequences for colorizing console output.
 *
 *  Change History:
 *   07-Jul-2020 (rlwhitcomb)
 *	Create from examples on the web.
 *   22-Sep-2020 (rlwhitcomb)
 *      Add "getName" method.
 *   07-Dec-2020 (rlwhitcomb)
 *	Refactor into a base class and an embedded enum in order to more legibly
 *	construct all the values (less chance for typo errors).	Fix copy/paste
 *	comment typos.
 *   04-Feb-2021 (rlwhitcomb)
 *	Add methods to return the escape code prefix and suffix.
 *   09-Feb-2021 (rlwhitcomb)
 *	New color attribute tags; methods to convert the tags to color codes
 *	so that we can color resource strings, etc.
 *   09-Feb-2021 (rlwhitcomb)
 *	New "map" param to "color()" so that we can use either custom tags or
 *	custom color choices in some situations (like catering to different
 *	color backgrounds); used in Calc, at least.
 */
package info.rlwhitcomb.util;

import java.util.HashMap;
import java.util.Map;


/**
 * Class to enumerate the various console colors and attributes available
 * using ANSI escape sequences.
 * <p> Various constants and simple formatting methods are provided, as
 * well as a static enum with all the possible combinations already
 * constructed.
 */
public final class ConsoleColor
{
    /** The (single) reset / end-of-color code. */
    public static final int RESET = 0;

    // The secondary attribute codes
    public static final int NORMAL    = 0;
    public static final int BOLD      = 1;
    public static final int UNDERLINE = 4;

    // The base color codes
    public static final int FOREGROUND        = 30;
    public static final int BACKGROUND        = 40;
    public static final int BRIGHT_FOREGROUND = 90;
    public static final int BRIGHT_BACKGROUND = 100;

    // The primary color codes
    public static final int BLACK   = 0;
    public static final int RED     = 1;
    public static final int GREEN   = 2;
    public static final int YELLOW  = 3;
    public static final int BLUE    = 4;
    public static final int MAGENTA = 5;
    public static final int CYAN    = 6;
    public static final int WHITE   = 7;

    private static final String ESC  = "\033[";
    private static final String END  = "m";
    private static final String ESC1 = ESC + "%1$d" + END;
    private static final String ESC2 = ESC + "%1$d;%2$d" + END;

    /**
     * Format an escape sequence for the given attribute and color code.
     * <p> For instance, to get a bright green underlined foreground, use:
     * <pre>( UNDERLINE, BRIGHT_FOREGROUND + GREEN )</pre>
     *
     * @param attrCode  The integer attribute value.
     * @param colorCode The integer color code.
     * @return The formatted escape sequence to enable this color/attribute combination.
     */
    public static final String esc(final int attrCode, final int colorCode) {
	return String.format(ESC2, attrCode, colorCode);
    }

    /**
     * Format an escape sequence for the given color code.
     * <p> For instance, to get a red foreground, use:
     * <pre>( FOREGROUND + RED )</pre>
     * <p> Note: the code to reset and return to normal should always be:
     * <pre>( RESET )</pre>
     *
     * @param colorCode The integer color (or reset) code.
     * @return The formatted escape sequence for this color.
     */
    public static final String esc(final int colorCode) {
	return String.format(ESC1, colorCode);
    }

    /**
     * @return The escape code prefix, so that others can tell when one of these
     * is coming down the pike.
     */
    public static final String escapeCodePrefix() {
	return ESC;
    }

    /**
     * @return The escape code suffix, or the end of one of these, for scanners
     * to recognized the end of the sequence.
     */
    public static final String escapeCodeSuffix() {
	return END;
    }

    /**
     * Map color tags into the escape sequence codes.
     *
     * @param input   The string adorned with color/attribute tags.
     * @param colored {@code true} to use the color values, {@code false} to just
     *                remove the tags, leaving the bare text.
     * @return        The input with the tags converted to their escape codes.
     */
    public static final String color(final String input, final boolean colored) {
	return color(input, colored, null);
    }

    /**
     * Map color tags into the escape sequence codes.
     *
     * @param input   The string adorned with color/attribute tags.
     * @param colored {@code true} to use the color values, {@code false} to just
     *                remove the tags, leaving the bare text.
     * @param map     A {@code <String, Code>} map used to lookup custom tags, or
     *                to override the default color selections (can be {@code null}).
     * @return        The input with the tags converted to their escape codes.
     */
    public static final String color(final String input, final boolean colored, final Map<String, Code> map) {
	StringBuilder buf = new StringBuilder(input.length() * 3);
	for (int i = 0; i < input.length(); i++) {
	    char ch = input.charAt(i);
	    if (ch == '<') {
		int endPos = input.indexOf('>', i + 1);
		if (endPos < 0)
		    buf.append(ch);
		else {
		    if (colored) {
			String tag = input.substring(i + 1, endPos);
			Code code;
			if (map != null) {
			    code = map.get(tag);
			    if (code == null) {
				code = Code.fromAttrib(tag);
			    }
			}
			else {
			    code = Code.fromAttrib(tag);
			}
			if (code != null) {
			    buf.append(code.toString());
			}
		    }
		    i = endPos;
		}
	    }
	    else {
		buf.append(ch);
	    }
	}
	return buf.toString();
    }


    /**
     * An enumeration of the various console colors and effects possible, with
     * their escape sequences pre-computed.
     */
    public enum Code
    {
	/** End string: reset to default color. */
	RESET(ConsoleColor.RESET, ""),

	/** Regular black, no effects. */
	BLACK	(NORMAL, FOREGROUND + ConsoleColor.BLACK,   "Bk"),
	/** Regular red, no effects. */
	RED	(NORMAL, FOREGROUND + ConsoleColor.RED,     "Rd"),
	/** Regular green, no effects. */
	GREEN	(NORMAL, FOREGROUND + ConsoleColor.GREEN,   "Gr"),
	/** Regular yellow, no effects. */
	YELLOW	(NORMAL, FOREGROUND + ConsoleColor.YELLOW,  "Yw"),
	/** Regular blue, no effects. */
	BLUE	(NORMAL, FOREGROUND + ConsoleColor.BLUE,    "Bl"),
	/** Regular magenta, no effects. */
	MAGENTA	(NORMAL, FOREGROUND + ConsoleColor.MAGENTA, "Mg"),
	/** Regular cyan, no effects. */
	CYAN	(NORMAL, FOREGROUND + ConsoleColor.CYAN,    "Cy"),
	/** Regular white, no effects. */
	WHITE	(NORMAL, FOREGROUND + ConsoleColor.WHITE,   "Wh"),

	/** Black color with bold emphasis. */
	BLACK_BOLD	(BOLD, FOREGROUND + ConsoleColor.BLACK,   "Bk*"),
	/** Red color with bold emphasis. */
	RED_BOLD	(BOLD, FOREGROUND + ConsoleColor.RED,     "Rd*"),
	/** Green color with bold emphasis. */
	GREEN_BOLD	(BOLD, FOREGROUND + ConsoleColor.GREEN,   "Gr*"),
	/** Yellow color with bold emphasis. */
	YELLOW_BOLD	(BOLD, FOREGROUND + ConsoleColor.YELLOW,  "Yw*"),
	/** Blue color with bold emphasis. */
	BLUE_BOLD	(BOLD, FOREGROUND + ConsoleColor.BLUE,    "Bl*"),
	/** Magenta color with bold emphasis. */
	MAGENTA_BOLD	(BOLD, FOREGROUND + ConsoleColor.MAGENTA, "Mg*"),
	/** Cyan color with bold emphasis. */
	CYAN_BOLD	(BOLD, FOREGROUND + ConsoleColor.CYAN,    "Cy*"),
	/** White color with bold emphasis. */
	WHITE_BOLD	(BOLD, FOREGROUND + ConsoleColor.WHITE,   "Wh*"),

	/** Black color, underlined. */
	BLACK_UNDERLINED   (UNDERLINE, FOREGROUND + ConsoleColor.BLACK,   "Bk_"),
	/** Red color, underlined. */
	RED_UNDERLINED     (UNDERLINE, FOREGROUND + ConsoleColor.RED,     "Rd_"),
	/** Green color, underlined. */
	GREEN_UNDERLINED   (UNDERLINE, FOREGROUND + ConsoleColor.GREEN,   "Gr_"),
	/** Yellow color, underlined. */
	YELLOW_UNDERLINED  (UNDERLINE, FOREGROUND + ConsoleColor.YELLOW,  "Yw_"),
	/** Blue color, underlined. */
	BLUE_UNDERLINED    (UNDERLINE, FOREGROUND + ConsoleColor.BLUE,    "Bl_"),
	/** Magenta color, underlined. */
	MAGENTA_UNDERLINED (UNDERLINE, FOREGROUND + ConsoleColor.MAGENTA, "Mg_"),
	/** Cyan color, underlined. */
	CYAN_UNDERLINED    (UNDERLINE, FOREGROUND + ConsoleColor.CYAN,    "Cy_"),
	/** White color, underlined. */
	WHITE_UNDERLINED   (UNDERLINE, FOREGROUND + ConsoleColor.WHITE,   "Wh_"),

	/** Black background color. */
	BLACK_BACKGROUND   (BACKGROUND + ConsoleColor.BLACK,   "Bk."),
	/** Red background color. */
	RED_BACKGROUND     (BACKGROUND + ConsoleColor.RED,     "Rd."),
	/** Green background color. */
	GREEN_BACKGROUND   (BACKGROUND + ConsoleColor.GREEN,   "Gr."),
	/** Yellow background color. */
	YELLOW_BACKGROUND  (BACKGROUND + ConsoleColor.YELLOW,  "Yw."),
	/** Blue background color. */
	BLUE_BACKGROUND    (BACKGROUND + ConsoleColor.BLUE,    "Bl."),
	/** Magenta background color. */
	MAGENTA_BACKGROUND (BACKGROUND + ConsoleColor.MAGENTA, "Mg."),
	/** Cyan background color. */
	CYAN_BACKGROUND    (BACKGROUND + ConsoleColor.CYAN,    "Cy."),
	/** White background color. */
	WHITE_BACKGROUND   (BACKGROUND + ConsoleColor.WHITE,   "Wh."),

	/** High-intensity (lighter) black color. */
	BLACK_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.BLACK,   "Bk!"),
	/** High-intensity (lighter) red color. */
	RED_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.RED,     "Rd!"),
	/** High-intensity (lighter) green color. */
	GREEN_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.GREEN,   "Gr!"),
	/** High-intensity (lighter) yellow color. */
	YELLOW_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.YELLOW,  "Yw!"),
	/** High-intensity (lighter) blue color. */
	BLUE_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.BLUE,    "Bl!"),
	/** High-intensity (lighter) magenta color. */
	MAGENTA_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.MAGENTA, "Mg!"),
	/** High-intensity (lighter) cyan color. */
	CYAN_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.CYAN,    "Cy!"),
	/** High-intensity (lighter) white color. */
	WHITE_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.WHITE,   "Wh!"),

	/** Bold and high-intensity black color. */
	BLACK_BOLD_BRIGHT   (BOLD, BRIGHT_FOREGROUND + ConsoleColor.BLACK,   "Bk*!"),
	/** Bold and high-intensity red color. */
	RED_BOLD_BRIGHT     (BOLD, BRIGHT_FOREGROUND + ConsoleColor.RED,     "Rd*!"),
	/** Bold and high-intensity green color. */
	GREEN_BOLD_BRIGHT   (BOLD, BRIGHT_FOREGROUND + ConsoleColor.GREEN,   "Gr*!"),
	/** Bold and high-intensity yellow color. */
	YELLOW_BOLD_BRIGHT  (BOLD, BRIGHT_FOREGROUND + ConsoleColor.YELLOW,  "Yw*!"),
	/** Bold and high-intensity blue color. */
	BLUE_BOLD_BRIGHT    (BOLD, BRIGHT_FOREGROUND + ConsoleColor.BLUE,    "Bl*!"),
	/** Bold and high-intensity magenta color. */
	MAGENTA_BOLD_BRIGHT (BOLD, BRIGHT_FOREGROUND + ConsoleColor.MAGENTA, "Mg*!"),
	/** Bold and high-intensity cyan color. */
	CYAN_BOLD_BRIGHT    (BOLD, BRIGHT_FOREGROUND + ConsoleColor.CYAN,    "Cy*!"),
	/** Bold and high-intensity white color. */
	WHITE_BOLD_BRIGHT   (BOLD, BRIGHT_FOREGROUND + ConsoleColor.WHITE,   "Wh*!"),

	/** High-intensity background black. */
	BLACK_BACKGROUND_BRIGHT   (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.BLACK,   "Bk.!"),
	/** High-intensity background red. */
	RED_BACKGROUND_BRIGHT     (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.RED,     "Rd.!"),
	/** High-intensity background green. */
	GREEN_BACKGROUND_BRIGHT   (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.GREEN,   "Gr.!"),
	/** High-intensity background yellow. */
	YELLOW_BACKGROUND_BRIGHT  (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.YELLOW,  "Yw.!"),
	/** High-intensity background blue. */
	BLUE_BACKGROUND_BRIGHT    (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.BLUE,    "Bl.!"),
	/** High-intensity background magenta. */
	MAGENTA_BACKGROUND_BRIGHT (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.MAGENTA, "Mg.!"),
	/** High-intensity background cyan. */
	CYAN_BACKGROUND_BRIGHT    (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.CYAN,    "Cy.!"),
	/** High-intensity background white. */
	WHITE_BACKGROUND_BRIGHT   (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.WHITE,   "Wh.!");


	private static class Lookup
	{
		private static Map<String, Code>  attribMap  = new HashMap<>();

		static void put(final String attrib, final Code color) {
		    attribMap.put(attrib, color);
		}

		static Code get(final String attrib) {
		    return attribMap.get(attrib);
		}
	}

	/** The constructed escape code sequence used to render this color. */
	private final String code;


	/**
	 * Construct given the two codes for the escape sequence.
	 *
	 * @param attrCode  The integer attribute value.
	 * @param colorCode The integer color code.
	 * @param attrib    The string attribute for this color.
	 * @see #esc(int, int)
	 */
	private Code(final int attrCode, final int colorCode, final String attrib) {
	    this.code = esc(attrCode, colorCode);
	    Lookup.put(attrib, this);
	}

	/**
	 * Construct given the single code for the escape sequence.
	 *
	 * @param colorCode The integer color code for the escape sequence.
	 * @param attrib    The string attribute for this color.
	 * @see #esc(int)
	 */
	private Code(final int colorCode, final String attrib) {
	    this.code = esc(colorCode);
	    Lookup.put(attrib, this);
	}

	/**
	 * @return The color name (enum value), as opposed to the
	 * escape code to implement it (given by {@link #toString}).
	 */
	public String getName() {
	    return super.toString();
	}

	/**
	 * @return The color enum given the string attribute tag.
	 * @param tag The tag matching the color.
	 */
	public static Code fromAttrib(final String tag) {
	    return Lookup.get(tag);
	}

	/**
	 * @return The escape sequence to implement the color/style change.
	 */
	@Override
	public String toString() {
	    return code;
	}
    }

}

