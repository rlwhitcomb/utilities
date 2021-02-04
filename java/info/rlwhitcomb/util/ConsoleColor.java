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
 */
package info.rlwhitcomb.util;

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
     * An enumeration of the various console colors and effects possible, with
     * their escape sequences pre-computed.
     */
    public enum Code
    {
	/** End string: reset to default color. */
	RESET(ConsoleColor.RESET),

	/** Regular black, no effects. */
	BLACK	(NORMAL, FOREGROUND + ConsoleColor.BLACK),
	/** Regular red, no effects. */
	RED	(NORMAL, FOREGROUND + ConsoleColor.RED),
	/** Regular green, no effects. */
	GREEN	(NORMAL, FOREGROUND + ConsoleColor.GREEN),
	/** Regular yellow, no effects. */
	YELLOW	(NORMAL, FOREGROUND + ConsoleColor.YELLOW),
	/** Regular blue, no effects. */
	BLUE	(NORMAL, FOREGROUND + ConsoleColor.BLUE),
	/** Regular magenta, no effects. */
	MAGENTA	(NORMAL, FOREGROUND + ConsoleColor.MAGENTA),
	/** Regular cyan, no effects. */
	CYAN	(NORMAL, FOREGROUND + ConsoleColor.CYAN),
	/** Regular white, no effects. */
	WHITE	(NORMAL, FOREGROUND + ConsoleColor.WHITE),

	/** Black color with bold emphasis. */
	BLACK_BOLD	(BOLD, FOREGROUND + ConsoleColor.BLACK),
	/** Red color with bold emphasis. */
	RED_BOLD	(BOLD, FOREGROUND + ConsoleColor.RED),
	/** Green color with bold emphasis. */
	GREEN_BOLD	(BOLD, FOREGROUND + ConsoleColor.GREEN),
	/** Yellow color with bold emphasis. */
	YELLOW_BOLD	(BOLD, FOREGROUND + ConsoleColor.YELLOW),
	/** Blue color with bold emphasis. */
	BLUE_BOLD	(BOLD, FOREGROUND + ConsoleColor.BLUE),
	/** Magenta color with bold emphasis. */
	MAGENTA_BOLD	(BOLD, FOREGROUND + ConsoleColor.MAGENTA),
	/** Cyan color with bold emphasis. */
	CYAN_BOLD	(BOLD, FOREGROUND + ConsoleColor.CYAN),
	/** White color with bold emphasis. */
	WHITE_BOLD	(BOLD, FOREGROUND + ConsoleColor.WHITE),

	/** Black color, underlined. */
	BLACK_UNDERLINED   (UNDERLINE, FOREGROUND + ConsoleColor.BLACK),
	/** Red color, underlined. */
	RED_UNDERLINED     (UNDERLINE, FOREGROUND + ConsoleColor.RED),
	/** Green color, underlined. */
	GREEN_UNDERLINED   (UNDERLINE, FOREGROUND + ConsoleColor.GREEN),
	/** Yellow color, underlined. */
	YELLOW_UNDERLINED  (UNDERLINE, FOREGROUND + ConsoleColor.YELLOW),
	/** Blue color, underlined. */
	BLUE_UNDERLINED    (UNDERLINE, FOREGROUND + ConsoleColor.BLUE),
	/** Magenta color, underlined. */
	MAGENTA_UNDERLINED (UNDERLINE, FOREGROUND + ConsoleColor.MAGENTA),
	/** Cyan color, underlined. */
	CYAN_UNDERLINED    (UNDERLINE, FOREGROUND + ConsoleColor.CYAN),
	/** White color, underlined. */
	WHITE_UNDERLINED   (UNDERLINE, FOREGROUND + ConsoleColor.WHITE),

	/** Black background color. */
	BLACK_BACKGROUND   (BACKGROUND + ConsoleColor.BLACK),
	/** Red background color. */
	RED_BACKGROUND     (BACKGROUND + ConsoleColor.RED),
	/** Green background color. */
	GREEN_BACKGROUND   (BACKGROUND + ConsoleColor.GREEN),
	/** Yellow background color. */
	YELLOW_BACKGROUND  (BACKGROUND + ConsoleColor.YELLOW),
	/** Blue background color. */
	BLUE_BACKGROUND    (BACKGROUND + ConsoleColor.BLUE),
	/** Magenta background color. */
	MAGENTA_BACKGROUND (BACKGROUND + ConsoleColor.MAGENTA),
	/** Cyan background color. */
	CYAN_BACKGROUND    (BACKGROUND + ConsoleColor.CYAN),
	/** White background color. */
	WHITE_BACKGROUND   (BACKGROUND + ConsoleColor.WHITE),

	/** High-intensity (lighter) black color. */
	BLACK_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.BLACK),
	/** High-intensity (lighter) red color. */
	RED_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.RED),
	/** High-intensity (lighter) green color. */
	GREEN_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.GREEN),
	/** High-intensity (lighter) yellow color. */
	YELLOW_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.YELLOW),
	/** High-intensity (lighter) blue color. */
	BLUE_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.BLUE),
	/** High-intensity (lighter) magenta color. */
	MAGENTA_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.MAGENTA),
	/** High-intensity (lighter) cyan color. */
	CYAN_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.CYAN),
	/** High-intensity (lighter) white color. */
	WHITE_BRIGHT	(NORMAL, BRIGHT_FOREGROUND + ConsoleColor.WHITE),

	/** Bold and high-intensity black color. */
	BLACK_BOLD_BRIGHT   (BOLD, BRIGHT_FOREGROUND + ConsoleColor.BLACK),
	/** Bold and high-intensity red color. */
	RED_BOLD_BRIGHT     (BOLD, BRIGHT_FOREGROUND + ConsoleColor.RED),
	/** Bold and high-intensity green color. */
	GREEN_BOLD_BRIGHT   (BOLD, BRIGHT_FOREGROUND + ConsoleColor.GREEN),
	/** Bold and high-intensity yellow color. */
	YELLOW_BOLD_BRIGHT  (BOLD, BRIGHT_FOREGROUND + ConsoleColor.YELLOW),
	/** Bold and high-intensity blue color. */
	BLUE_BOLD_BRIGHT    (BOLD, BRIGHT_FOREGROUND + ConsoleColor.BLUE),
	/** Bold and high-intensity magenta color. */
	MAGENTA_BOLD_BRIGHT (BOLD, BRIGHT_FOREGROUND + ConsoleColor.MAGENTA),
	/** Bold and high-intensity cyan color. */
	CYAN_BOLD_BRIGHT    (BOLD, BRIGHT_FOREGROUND + ConsoleColor.CYAN),
	/** Bold and high-intensity white color. */
	WHITE_BOLD_BRIGHT   (BOLD, BRIGHT_FOREGROUND + ConsoleColor.WHITE),

	/** High-intensity background black. */
	BLACK_BACKGROUND_BRIGHT   (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.BLACK),
	/** High-intensity background red. */
	RED_BACKGROUND_BRIGHT     (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.RED),
	/** High-intensity background green. */
	GREEN_BACKGROUND_BRIGHT   (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.GREEN),
	/** High-intensity background yellow. */
	YELLOW_BACKGROUND_BRIGHT  (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.YELLOW),
	/** High-intensity background blue. */
	BLUE_BACKGROUND_BRIGHT    (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.BLUE),
	/** High-intensity background magenta. */
	MAGENTA_BACKGROUND_BRIGHT (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.MAGENTA),
	/** High-intensity background cyan. */
	CYAN_BACKGROUND_BRIGHT    (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.CYAN),
	/** High-intensity background white. */
	WHITE_BACKGROUND_BRIGHT   (NORMAL, BRIGHT_BACKGROUND + ConsoleColor.WHITE);


	/** The constructed escape code sequence used to render this color. */
	private final String code;


	/**
	 * Construct given the two codes for the escape sequence.
	 *
	 * @param attrCode  The integer attribute value.
	 * @param colorCode The integer color code.
	 * @see #esc(int, int)
	 */
	private Code(final int attrCode, final int colorCode) {
	    this.code = esc(attrCode, colorCode);
	}

	/**
	 * Construct given the single code for the escape sequence.
	 *
	 * @param colorCode The integer color code for the escape sequence.
	 * @see #esc(int)
	 */
	private Code(final int colorCode) {
	    this.code = esc(colorCode);
	}

	/**
	 * @return The color name (enum value), as opposed to the
	 * escape code to implement it (given by {@link #toString}).
	 */
	public String getName() {
	    return super.toString();
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

