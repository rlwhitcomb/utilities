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
 *   10-Feb-2021 (rlwhitcomb)
 *	"Calc" outputs a lot of "<" stuff that isn't a color tag (things like
 *	"<null>", "<=>", "<=", "<", etc.), so just leave a tag alone if we
 *	can't find a substitution for it.
 *   25-Feb-2021 (rlwhitcomb)
 *	Add another simpler version of "color(...)", and "textLength()" method.
 *   18-May-2021 (rlwhicomb)
 *	Redo "color" method to push/pop colors to do nesting. Rename "attrib" to "tag".
 *	String constants for the tag values also.
 *   20-May-2021 (rlwhitcomb)
 *	Add bright underlined forms (this actually works!)
 *   09-Jul-2021 (rlwhitcomb)
 *	Make the constructor private.
 */
package info.rlwhitcomb.util;

import java.util.ArrayDeque;
import java.util.Deque;
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

    private static final String _BK = "Bk";
    private static final String _RD = "Rd";
    private static final String _GR = "Gr";
    private static final String _YW = "Yw";
    private static final String _BL = "Bl";
    private static final String _MG = "Mg";
    private static final String _CY = "Cy";
    private static final String _WH = "Wh";

    private static final String BD = "*";
    private static final String UN = "_";
    private static final String BG = ".";
    private static final String BR = "!";


    /**
     * Private constructor since this is a static class.
     */
    private ConsoleColor() {
    }


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
     * @return The length of the string without the color/attribute tags, that is,
     *         the length of the text itself.
     * @param  input The colored string to measure.
     */
    public static final int textLength(final String input) {
	return color(input, false).length();
    }

    /**
     * Map color tags into the escape sequence codes.
     *
     * @param input The string adorned with color/attribute tags.
     * @return      The input with the tags converted to their escape codes.
     */
    public static final String color(final String input) {
	return color(input, true, null);
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
	Deque<Code> colorStack = new ArrayDeque<>();
	Code currentCode = Code.RESET;

	StringBuilder buf = new StringBuilder(input.length() * 3);
	for (int i = 0; i < input.length(); i++) {
	    char ch = input.charAt(i);
	    if (ch == '<') {
		int endPos = input.indexOf('>', i + 1);
		if (endPos < 0)
		    buf.append(ch);
		else {
		    String tag = input.substring(i + 1, endPos);
		    Code code;
		    if (tag.isEmpty()) {
			if (colorStack.isEmpty()) {
			    if (map != null) {
				code = map.get(tag);
				if (code == null) {
				    code = Code.fromTag(tag);
				}
			    }
			    else {
				code = Code.fromTag(tag);
			    }
			}
			else {
			    code = colorStack.pop();
			}
			if (code != null)
			    currentCode = code;
		    }
		    else {
			if (map != null) {
			    code = map.get(tag);
			    if (code == null) {
				code = Code.fromTag(tag);
			    }
			}
			else {
			    code = Code.fromTag(tag);
			}
			if (code != null) {
			    colorStack.push(currentCode);
			    currentCode = code;
			}
		    }
		    // Unknown tags should be left alone (for Calc use!)
		    if (code == null) {
			buf.append(ch);
		    }
		    else {
			if (colored) {
			    buf.append(code.escCode());
			}
			i = endPos;
		    }
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
	BLACK	(NORMAL, FOREGROUND + ConsoleColor.BLACK,   _BK),
	/** Regular red, no effects. */
	RED	(NORMAL, FOREGROUND + ConsoleColor.RED,     _RD),
	/** Regular green, no effects. */
	GREEN	(NORMAL, FOREGROUND + ConsoleColor.GREEN,   _GR),
	/** Regular yellow, no effects. */
	YELLOW	(NORMAL, FOREGROUND + ConsoleColor.YELLOW,  _YW),
	/** Regular blue, no effects. */
	BLUE	(NORMAL, FOREGROUND + ConsoleColor.BLUE,    _BL),
	/** Regular magenta, no effects. */
	MAGENTA	(NORMAL, FOREGROUND + ConsoleColor.MAGENTA, _MG),
	/** Regular cyan, no effects. */
	CYAN	(NORMAL, FOREGROUND + ConsoleColor.CYAN,    _CY),
	/** Regular white, no effects. */
	WHITE	(NORMAL, FOREGROUND + ConsoleColor.WHITE,   _WH),

	/** Black color with bold emphasis. */
	BLACK_BOLD	(BOLD, FOREGROUND + ConsoleColor.BLACK,   _BK+BD),
	/** Red color with bold emphasis. */
	RED_BOLD	(BOLD, FOREGROUND + ConsoleColor.RED,     _RD+BD),
	/** Green color with bold emphasis. */
	GREEN_BOLD	(BOLD, FOREGROUND + ConsoleColor.GREEN,   _GR+BD),
	/** Yellow color with bold emphasis. */
	YELLOW_BOLD	(BOLD, FOREGROUND + ConsoleColor.YELLOW,  _YW+BD),
	/** Blue color with bold emphasis. */
	BLUE_BOLD	(BOLD, FOREGROUND + ConsoleColor.BLUE,    _BL+BD),
	/** Magenta color with bold emphasis. */
	MAGENTA_BOLD	(BOLD, FOREGROUND + ConsoleColor.MAGENTA, _MG+BD),
	/** Cyan color with bold emphasis. */
	CYAN_BOLD	(BOLD, FOREGROUND + ConsoleColor.CYAN,    _CY+BD),
	/** White color with bold emphasis. */
	WHITE_BOLD	(BOLD, FOREGROUND + ConsoleColor.WHITE,   _WH+BD),

	/** Black color, underlined. */
	BLACK_UNDERLINED   (UNDERLINE, FOREGROUND + ConsoleColor.BLACK,   _BK+UN),
	/** Red color, underlined. */
	RED_UNDERLINED     (UNDERLINE, FOREGROUND + ConsoleColor.RED,     _RD+UN),
	/** Green color, underlined. */
	GREEN_UNDERLINED   (UNDERLINE, FOREGROUND + ConsoleColor.GREEN,   _GR+UN),
	/** Yellow color, underlined. */
	YELLOW_UNDERLINED  (UNDERLINE, FOREGROUND + ConsoleColor.YELLOW,  _YW+UN),
	/** Blue color, underlined. */
	BLUE_UNDERLINED    (UNDERLINE, FOREGROUND + ConsoleColor.BLUE,    _BL+UN),
	/** Magenta color, underlined. */
	MAGENTA_UNDERLINED (UNDERLINE, FOREGROUND + ConsoleColor.MAGENTA, _MG+UN),
	/** Cyan color, underlined. */
	CYAN_UNDERLINED    (UNDERLINE, FOREGROUND + ConsoleColor.CYAN,    _CY+UN),
	/** White color, underlined. */
	WHITE_UNDERLINED   (UNDERLINE, FOREGROUND + ConsoleColor.WHITE,   _WH+UN),

	/** Black background color. */
	BLACK_BACKGROUND   (BACKGROUND + ConsoleColor.BLACK,   _BK+BG),
	/** Red background color. */
	RED_BACKGROUND     (BACKGROUND + ConsoleColor.RED,     _RD+BG),
	/** Green background color. */
	GREEN_BACKGROUND   (BACKGROUND + ConsoleColor.GREEN,   _GR+BG),
	/** Yellow background color. */
	YELLOW_BACKGROUND  (BACKGROUND + ConsoleColor.YELLOW,  _YW+BG),
	/** Blue background color. */
	BLUE_BACKGROUND    (BACKGROUND + ConsoleColor.BLUE,    _BL+BG),
	/** Magenta background color. */
	MAGENTA_BACKGROUND (BACKGROUND + ConsoleColor.MAGENTA, _MG+BG),
	/** Cyan background color. */
	CYAN_BACKGROUND    (BACKGROUND + ConsoleColor.CYAN,    _CY+BG),
	/** White background color. */
	WHITE_BACKGROUND   (BACKGROUND + ConsoleColor.WHITE,   _WH+BG),

	/** High-intensity (lighter) black color. */
	BLACK_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.BLACK,   _BK+BR),
	/** High-intensity (lighter) red color. */
	RED_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.RED,     _RD+BR),
	/** High-intensity (lighter) green color. */
	GREEN_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.GREEN,   _GR+BR),
	/** High-intensity (lighter) yellow color. */
	YELLOW_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.YELLOW,  _YW+BR),
	/** High-intensity (lighter) blue color. */
	BLUE_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.BLUE,    _BL+BR),
	/** High-intensity (lighter) magenta color. */
	MAGENTA_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.MAGENTA, _MG+BR),
	/** High-intensity (lighter) cyan color. */
	CYAN_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.CYAN,    _CY+BR),
	/** High-intensity (lighter) white color. */
	WHITE_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.WHITE,   _WH+BR),

	/** Bold and high-intensity black color. */
	BLACK_BOLD_BRIGHT   (BOLD, BRIGHT_FOREGROUND + ConsoleColor.BLACK,   _BK+BD+BR),
	/** Bold and high-intensity red color. */
	RED_BOLD_BRIGHT     (BOLD, BRIGHT_FOREGROUND + ConsoleColor.RED,     _RD+BD+BR),
	/** Bold and high-intensity green color. */
	GREEN_BOLD_BRIGHT   (BOLD, BRIGHT_FOREGROUND + ConsoleColor.GREEN,   _GR+BD+BR),
	/** Bold and high-intensity yellow color. */
	YELLOW_BOLD_BRIGHT  (BOLD, BRIGHT_FOREGROUND + ConsoleColor.YELLOW,  _YW+BD+BR),
	/** Bold and high-intensity blue color. */
	BLUE_BOLD_BRIGHT    (BOLD, BRIGHT_FOREGROUND + ConsoleColor.BLUE,    _BL+BD+BR),
	/** Bold and high-intensity magenta color. */
	MAGENTA_BOLD_BRIGHT (BOLD, BRIGHT_FOREGROUND + ConsoleColor.MAGENTA, _MG+BD+BR),
	/** Bold and high-intensity cyan color. */
	CYAN_BOLD_BRIGHT    (BOLD, BRIGHT_FOREGROUND + ConsoleColor.CYAN,    _CY+BD+BR),
	/** Bold and high-intensity white color. */
	WHITE_BOLD_BRIGHT   (BOLD, BRIGHT_FOREGROUND + ConsoleColor.WHITE,   _WH+BD+BR),

	/** Underlined and high-intensity black color. */
	BLACK_UNDERLINED_BRIGHT   (UNDERLINE, BRIGHT_FOREGROUND + ConsoleColor.BLACK,   _BK+UN+BR),
	/** Underlined and high-intensity red color. */
	RED_UNDERLINED_BRIGHT     (UNDERLINE, BRIGHT_FOREGROUND + ConsoleColor.RED,     _RD+UN+BR),
	/** Underlined and high-intensity green color. */
	GREEN_UNDERLINED_BRIGHT   (UNDERLINE, BRIGHT_FOREGROUND + ConsoleColor.GREEN,   _GR+UN+BR),
	/** Underlined and high-intensity yellow color. */
	YELLOW_UNDERLINED_BRIGHT  (UNDERLINE, BRIGHT_FOREGROUND + ConsoleColor.YELLOW,  _YW+UN+BR),
	/** Underlined and high-intensity blue color. */
	BLUE_UNDERLINED_BRIGHT    (UNDERLINE, BRIGHT_FOREGROUND + ConsoleColor.BLUE,    _BL+UN+BR),
	/** Underlined and high-intensity magenta color. */
	MAGENTA_UNDERLINED_BRIGHT (UNDERLINE, BRIGHT_FOREGROUND + ConsoleColor.MAGENTA, _MG+UN+BR),
	/** Underlined and high-intensity cyan color. */
	CYAN_UNDERLINED_BRIGHT    (UNDERLINE, BRIGHT_FOREGROUND + ConsoleColor.CYAN,    _CY+UN+BR),
	/** Underlined and high-intensity white color. */
	WHITE_UNDERLINED_BRIGHT   (UNDERLINE, BRIGHT_FOREGROUND + ConsoleColor.WHITE,   _WH+UN+BR),

	/** High-intensity background black. */
	BLACK_BACKGROUND_BRIGHT   (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.BLACK,   _BK+BG+BR),
	/** High-intensity background red. */
	RED_BACKGROUND_BRIGHT     (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.RED,     _RD+BG+BR),
	/** High-intensity background green. */
	GREEN_BACKGROUND_BRIGHT   (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.GREEN,   _GR+BG+BR),
	/** High-intensity background yellow. */
	YELLOW_BACKGROUND_BRIGHT  (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.YELLOW,  _YW+BG+BR),
	/** High-intensity background blue. */
	BLUE_BACKGROUND_BRIGHT    (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.BLUE,    _BL+BG+BR),
	/** High-intensity background magenta. */
	MAGENTA_BACKGROUND_BRIGHT (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.MAGENTA, _MG+BG+BR),
	/** High-intensity background cyan. */
	CYAN_BACKGROUND_BRIGHT    (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.CYAN,    _CY+BG+BR),
	/** High-intensity background white. */
	WHITE_BACKGROUND_BRIGHT   (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.WHITE,   _WH+BG+BR);


	private static class Lookup
	{
		private static Map<String, Code> tagMap  = new HashMap<>();

		static void put(final String tag, final Code color) {
		    tagMap.put(tag, color);
		}

		static Code get(final String tag) {
		    return tagMap.get(tag);
		}
	}

	/** The constructed escape code sequence used to render this color. */
	private final String code;
	/** The string tag used to represent this color for conciseness. */
	private final String tagValue;


	/**
	 * Construct given the two codes for the escape sequence.
	 *
	 * @param attrCode  The integer attribute value.
	 * @param colorCode The integer color code.
	 * @param tag       The string tag for this color.
	 * @see #esc(int, int)
	 */
	private Code(final int attrCode, final int colorCode, final String tag) {
	    this.code = esc(attrCode, colorCode);
	    this.tagValue = tag;
	    Lookup.put(tag, this);
	}

	/**
	 * Construct given the single code for the escape sequence.
	 *
	 * @param colorCode The integer color code for the escape sequence.
	 * @param tag       The string tag for this color.
	 * @see #esc(int)
	 */
	private Code(final int colorCode, final String tag) {
	    this.code = esc(colorCode);
	    this.tagValue = tag;
	    Lookup.put(tag, this);
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
	public static Code fromTag(final String tag) {
	    return Lookup.get(tag);
	}

	/**
	 * @return The string tag used to represent this color
	 *         in the form of <code>&lt;<i>tag</i>&gt;</code>.
	 */
	@Override
	public String toString() {
	    return String.format("<%1$s>", this.tagValue);
	}

	/**
	 * @return The escape sequence to implement the color/style change.
	 */
	public String escCode() {
	    return this.code;
	}
    }

}

