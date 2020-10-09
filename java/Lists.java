/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013-2014,2016,2020 Roger L. Whitcomb.
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
 *	Utility program to format lists for use with other programs.
 *
 * History:
 *	23-Mar-2016 (rlwhitcomb)
 *	    Add another option to recognize blank-separated input files.
 *	10-May-2016 (rlwhitcomb)
 *	    Allow reading from System.in with file named "@".
 *	15-Jul-2016 (rlwhitcomb)
 *	    Simplify operation further by allowing no file name to mean
 *	    "read from console" (still support "@" too).  Reformat the help.
 *	    Use the Options class for the command line.
 *	08-Oct-2020 (rlwhitcomb)
 *	    Print version information.
 *
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Locale;

import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.Options;

/**
 * Utility program to reformat lists of numbers or values.
 */
public class Lists
{
        private static boolean concatenate = false;
        private static boolean blanks = false;
        private static boolean whitespace = false;
        private static int width = 0;
        private static String fileName = null;
        private static boolean readConsole = false;

        private static void usage() {
            System.err.println("Usage: java Lists [-c] [-b] [-nn] [-w] [list_file_name | @]");
            System.err.println();
            System.err.println("  If you specify the \"-c\" flag the output will have all the lines");
            System.err.println("    of the file concatenated (using commas) into a single line.");
            System.err.println();
            System.err.println("  Without that option, the input file will be deconstructed into");
            System.err.println("    one element per line and the commas (if any) will be removed.");
            System.err.println();
            System.err.println("  Using \"-b\" without \"-c\" will print a blank line between");
            System.err.println("    each output value, while with \"-c\" will put a blank after");
            System.err.println("    each comma.");
            System.err.println();
            System.err.println("  The \"-nn\" option specifies a maximum line width for the");
            System.err.println("    \"-c\" output mode.");
            System.err.println();
            System.err.println("  The \"-w\" option will recognize input files where the input");
            System.err.println("    values are separated by whitespace instead of \",\".");
            System.err.println();
            System.err.println("  Using \"@\" or nothing for the list_file_name will read from stdin.");
            System.err.println();
        }

        public static void main(String[] args) {
	    Environment.setProductName("Lists Management");

            // First parse the command line arguments
            for (String arg : args) {
                String option = Options.isOption(arg);
                if (option != null) {
                    if (Options.matchesOption(arg, "c"))
                        concatenate = true;
                    else if (Options.matchesOption(arg, "b"))
                        blanks = true;
                    else if (Options.matchesOption(arg, "w"))
                        whitespace = true;
                    else if (Options.matchesOption(arg, "help", "?")) {
                        usage();
                        return;
                    }
		    else if (Options.matchesOption(arg, true, "version", "vers", "ver", "v")) {
			Environment.printProgramInfo();
			return;
		    }
                    else {
                        try {
                            width = Integer.parseInt(option);
                            if (width < 1 || width > 255) {
                                System.err.format("Width value (%1$d) must be between 1 and 255%n", width);
                                usage();
                                return;
                            }
                        }
                        catch (NumberFormatException nfe) {
                            System.err.format("Unsupported option: \"%1$s\"%n", arg);
                            usage();
                            return;
                        }
                    }
                }
                else {
                    fileName = arg;
                }
            }

            readConsole = fileName == null || fileName.trim().equals("@");

            // Error checking on the supplied parameters
            if (!concatenate && width > 0) {
                System.err.format("Specifying an output width (\"-%1$d\") is only effective with the \"-c\" option.%n", width);
                usage();
                return;
            }

            BufferedReader r = null;
            try {
                Reader reader = readConsole ?
                        new InputStreamReader(System.in) :
                        new FileReader(fileName);
                r = new BufferedReader(reader);
                StringBuilder buf = new StringBuilder();
                String line = null;
                while ((line = r.readLine()) != null) {
                    String[] parts = whitespace ? line.split("\\s+") : line.split("\\s*,\\s*");
                    for (String part : parts) {
                        String value = part.trim();
                        if (!value.isEmpty()) {
                            if (concatenate) {
                                if (buf.length() > 0) {
                                    buf.append(",");
                                    if (width > 0 && buf.length() >= width) {
                                        System.out.println(buf.toString());
                                        buf.setLength(0);
                                    }
                                    else if (blanks)
                                        buf.append(" ");
                                }
                                if (width > 0 && buf.length() + value.length() >= width) {
                                    System.out.println(buf.toString());
                                    buf.setLength(0);
                                }
                                buf.append(value);
                            }
                            else {
                                System.out.println(value);
                                if (blanks)
                                    System.out.println();
                            }
                        }
                    }
                }
                if (concatenate) {
                    System.out.println(buf.toString());
                }
            }
            catch (IOException ioe) {
                if (readConsole) {
                    System.err.format("Error reading from the console: %1$s%n", ioe.getMessage());
                }
                else {
                    System.err.format("Error accessing the file \"%1$s\": %2$s%n", fileName, ioe.getMessage());
                }
            }
            finally {
                if (!readConsole) {
                    if (r != null) {
                        try {
                            r.close();
                        }
                        catch (IOException ignore) { }
                    }
                }
            }
        }

}
