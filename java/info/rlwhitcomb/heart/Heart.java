/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2010-2011,2015,2017-2018,2020 Roger L. Whitcomb.
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
 * Example program inspired by a question on Quora:
 *
 */
package info.rlwhitcomb.heart;

/**
 * Demonstration program to use bitmaps to display character pictures.
 * Inspired by a question I answered on Quora:
 * <a href="https://www.quora.com/I-have-to-use-6-rows-and-7-columns-for-a-heart-pattern-in-Java-programming-Can-you-help-me">I have to use 6 rows and 7 columns for a heart pattern in Java programming. Can you help me?</a>
 */
public class Heart {
    /** The original data to display the heart pattern. */
    private static int[] HEART_DATA = {
        0b0110110,
        0b1001001,
        0b1000001,
        0b0100010,
        0b0010100,
        0b0001000
    };
    /** Another example with a different bit size. */
    private static int[] CROSS_DATA = {
        0b00011000,
        0b00011000,
        0b00011000,
        0b11111111,
        0b11111111,
        0b00011000,
        0b00011000,
        0b00011000,
        0b00011000,
        0b00011000,
        0b00011000,
        0b00011000
    };

    /**
     * Given a character and a width, produce a string with that character
     * repeated for the entire width.
     * @param dotChar The "dot" character to be replicated.
     * @param width The number of times to replicate the character.
     * @return The completed string.
     */
    private static String getStringOfNChars(final char dotChar, final int width) {
        StringBuilder buf = new StringBuilder(width);
        for (int i = 0; i < width; i++) {
            buf.append(dotChar);
        }
        return buf.toString();
    }

    /**
     * Shortcut method to display the data with default character, bit size, and width.
     * @param data The bitmap data to display (in the low-order bits).
     */
    private static void printData(final int[] data) {
        printData(data, 8);
    }

    /**
     * Shortcut method to display the data with default character, and width, but
     * specifying how many low-order bits are relevant in the input data.
     * @param data The bitmap data to display (in the low-order bits).
     * @param bits The number of low-order bits in each integer of the data that are
     * significant (that is, that hold the design to display).
     */
    private static void printData(final int[] data, final int bits) {
        printData(data, bits, '*', 2);
    }

    /**
     * Shortcut method to display the data with default character, but
     * specifying how many low-order bits are relevant in the input data, and the
     * "width" of each bit position.
     * @param data The bitmap data to display (in the low-order bits).
     * @param bits The number of low-order bits in each integer of the data that are
     * significant (that is, that hold the design to display).
     * @param width The multiplier for the character for each bit of the data. A width
     * of two will print two of the character for each bit of data, for instance.
     */
    private static void printData(final int[] data, final int bits, final int width) {
        printData(data, bits, '*', width);
    }

    /**
     * The main method to display the data with the given character, specifying how many
     * low-order bits are relevant in the input data, and the "width" of each bit position.
     * @param data The bitmap data to display (in the low-order bits).
     * @param bits The number of low-order bits in each integer of the data that are
     * significant (that is, that hold the design to display).
     * @param dotChar The character to display for each one bit in the data, which is
     * multiplied by the width.
     * @param width The multiplier for the character for each bit of the data. A width
     * of two will print two of the character for each bit of data, for instance.
     */
    private static void printData(final int[] data, final int bits, final char dotChar, final int width) {
        final String dot = getStringOfNChars(dotChar, width);
        final String blank = getStringOfNChars(' ', width);
        for (int i = 0; i < data.length; i++) {
            int row = data[i];
            for (int j = bits - 1; j >= 0; j--) {
                boolean bit = ((row >> j) & 1) == 1;
                System.out.print(bit ? dot : blank);
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Run the program from the command line.
     * @param args The parsed command line arguments.
     */
    public static void main(final String[] args) {
        printData(HEART_DATA, 7);
        printData(HEART_DATA, 7, '#', 2);
        printData(HEART_DATA, 7, '#', 1);
        printData(CROSS_DATA);
        printData(CROSS_DATA, 8, 1);
        printData(CROSS_DATA, 8, '#', 1);
    }
}

