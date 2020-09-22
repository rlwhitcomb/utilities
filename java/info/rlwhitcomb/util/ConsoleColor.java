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
 * Escape sequences for colorizing console output.
 *
 *  Change History:
 *   07-Jul-2020 (rlwhitcomb)
 *	Create from examples on the web.
 *   22-Sep-2020 (rlwhitcomb)
 *      Add "getName" method.
 */
package info.rlwhitcomb.util;

/**
 * An enumeration of the various console colors and effects possible, with
 * their escape sequences supplied.
 */
public enum ConsoleColor {
    /** End string: reset to default color. */
    RESET("\033[0m"),

    /** Regular black, no effects. */
    BLACK("\033[0;30m"),
    /** Regular red, no effects. */
    RED("\033[0;31m"),
    /** Regular green, no effects. */
    GREEN("\033[0;32m"),
    /** Regular yellow, no effects. */
    YELLOW("\033[0;33m"),
    /** Regular blue, no effects. */
    BLUE("\033[0;34m"),
    /** Regular magenta, no effects. */
    MAGENTA("\033[0;35m"),
    /** Regular cyan, no effects. */
    CYAN("\033[0;36m"),
    /** Regular white, no effects. */
    WHITE("\033[0;37m"),

    /** Black color with bold emphasis. */
    BLACK_BOLD("\033[1;30m"),
    /** Black color with bold emphasis. */
    RED_BOLD("\033[1;31m"),
    /** Black color with bold emphasis. */
    GREEN_BOLD("\033[1;32m"),
    /** Black color with bold emphasis. */
    YELLOW_BOLD("\033[1;33m"),
    /** Black color with bold emphasis. */
    BLUE_BOLD("\033[1;34m"),
    /** Black color with bold emphasis. */
    MAGENTA_BOLD("\033[1;35m"),
    /** Black color with bold emphasis. */
    CYAN_BOLD("\033[1;36m"),
    /** Black color with bold emphasis. */
    WHITE_BOLD("\033[1;37m"),

    /** Black color, underlined. */
    BLACK_UNDERLINED("\033[4;30m"),
    /** Black color, underlined. */
    RED_UNDERLINED("\033[4;31m"),
    /** Black color, underlined. */
    GREEN_UNDERLINED("\033[4;32m"),
    /** Black color, underlined. */
    YELLOW_UNDERLINED("\033[4;33m"),
    /** Black color, underlined. */
    BLUE_UNDERLINED("\033[4;34m"),
    /** Black color, underlined. */
    MAGENTA_UNDERLINED("\033[4;35m"),
    /** Black color, underlined. */
    CYAN_UNDERLINED("\033[4;36m"),
    /** Black color, underlined. */
    WHITE_UNDERLINED("\033[4;37m"),

    /** Black background color. */
    BLACK_BACKGROUND("\033[40m"),
    /** Red background color. */
    RED_BACKGROUND("\033[41m"),
    /** Green background color. */
    GREEN_BACKGROUND("\033[42m"),
    /** Yellow background color. */
    YELLOW_BACKGROUND("\033[43m"),
    /** Blue background color. */
    BLUE_BACKGROUND("\033[44m"),
    /** Magenta background color. */
    MAGENTA_BACKGROUND("\033[45m"),
    /** Cyan background color. */
    CYAN_BACKGROUND("\033[46m"),
    /** White background color. */
    WHITE_BACKGROUND("\033[47m"),

    /** High-intensity (lighter) black color. */
    BLACK_BRIGHT("\033[0;90m"),
    /** High-intensity (lighter) red color. */
    RED_BRIGHT("\033[0;91m"),
    /** High-intensity (lighter) green color. */
    GREEN_BRIGHT("\033[0;92m"),
    /** High-intensity (lighter) yellow color. */
    YELLOW_BRIGHT("\033[0;93m"),
    /** High-intensity (lighter) blue color. */
    BLUE_BRIGHT("\033[0;94m"),
    /** High-intensity (lighter) magenta color. */
    MAGENTA_BRIGHT("\033[0;95m"),
    /** High-intensity (lighter) cyan color. */
    CYAN_BRIGHT("\033[0;96m"),
    /** High-intensity (lighter) white color. */
    WHITE_BRIGHT("\033[0;97m"),

    /** Bold and high-intensity black color. */
    BLACK_BOLD_BRIGHT("\033[1;90m"),
    /** Bold and high-intensity red color. */
    RED_BOLD_BRIGHT("\033[1;91m"),
    /** Bold and high-intensity green color. */
    GREEN_BOLD_BRIGHT("\033[1;92m"),
    /** Bold and high-intensity yellow color. */
    YELLOW_BOLD_BRIGHT("\033[1;93m"),
    /** Bold and high-intensity blue color. */
    BLUE_BOLD_BRIGHT("\033[1;94m"),
    /** Bold and high-intensity magenta color. */
    MAGENTA_BOLD_BRIGHT("\033[1;95m"),
    /** Bold and high-intensity cyan color. */
    CYAN_BOLD_BRIGHT("\033[1;96m"),
    /** Bold and high-intensity white color. */
    WHITE_BOLD_BRIGHT("\033[1;97m"),

    /** High-intensity background black. */
    BLACK_BACKGROUND_BRIGHT("\033[0;100m"),
    /** High-intensity background red. */
    RED_BACKGROUND_BRIGHT("\033[0;101m"),
    /** High-intensity background green. */
    GREEN_BACKGROUND_BRIGHT("\033[0;102m"),
    /** High-intensity background yellow. */
    YELLOW_BACKGROUND_BRIGHT("\033[0;103m"),
    /** High-intensity background blue. */
    BLUE_BACKGROUND_BRIGHT("\033[0;104m"),
    /** High-intensity background magenta. */
    MAGENTA_BACKGROUND_BRIGHT("\033[0;105m"),
    /** High-intensity background cyan. */
    CYAN_BACKGROUND_BRIGHT("\033[0;106m"),
    /** High-intensity background white. */
    WHITE_BACKGROUND_BRIGHT("\033[0;107m");

    private final String code;

    /**
     * Construct giving the escape sequence to implement it.
     * @param code The escape sequence.
     * @see #code
     */
    private ConsoleColor(final String code) {
        this.code = code;
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

