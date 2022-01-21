/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017,2019-2022 Roger L. Whitcomb.
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
 *	Class to test the CSV parsing classes.
 *
 *  Change History:
 *	27-Feb-2014 (rlwhitcomb)
 *	    Initial coding, based on many other CSV packages.
 *	03-Mar-2014 (rlwhitcomb)
 *	    Add options to test escape char and option to ignore
 *	    empty lines.
 *	04-Mar-2014 (rlwhitcomb)
 *	    Add support for reading UTF-8 streams.  Reset all
 *	    static variables inside "main" method so this can
 *	    be reused inside the script tester.
 *	04-Mar-2014 (rlwhitcomb)
 *	    Moved all text to the resources file.
 *	13-Mar-2014 (rlwhitcomb)
 *	    Output field numbers and check for mismatched number
 *	    of fields on each record.
 *	16-Sep-2014 (rlwhitcomb)
 *	    Add additional test for the Iterable/Iterator interface.
 *	12-Mar-2015 (rlwhitcomb)
 *	    Use new FileUtilities method for temp file creation.
 *	28-Jun-2016 (rlwhitcomb)
 *	    Enable specifying -Qquote and -Ddelimiter values based
 *	    on the new enums.
 *	28-Jul-2017 (rlwhitcomb)
 *	    As part of cleanup for users of the management-api, move the default
 *	    package resource initialization into Intl itself, so all the callers
 *	    don't have to do it.
 *	18-Mar-2019 (rlwhitcomb)
 *	    Don't use FileInputStream/FileOutputStream due to GC problems b/c of the finalize
 *	    method in these classes.
 *	30-Dec-2019 (rlwhitcomb)
 *	    Use Object instead of String for CSVRecord to properly implement "alwaysDelimitStrings".
 *	27-Feb-2020 (rlwhitcomb)
 *	    Allow "/" options on Windows (like Options class). Add processing for predefined
 *	    Separator enum.
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	13-Nov-2020 (rlwhitcomb)
 *	    Print help on some errors. More error checking.
 *	02-Mar-2021 (rlwhitcomb)
 *	    Add options for "hasHeaderRow", "alwaysDelimitStrings", and "preserveQuotes"
 *	    (new options in CSVFormat).
 *	29-Mar-2021 (rlwhitcomb)
 *	    Move to new package.
 *	15-Dec-2021 (rlwhitcomb)
 *	    #150: Change "=E" option to be "no escape" and make "-H" into "has header row".
 *	21-Jan-2021 (rlwhitcomb)
 *	    #217: Allow default options from CSVTEST_OPTIONS environment variable, using new
 *	    Options method.
 */
package info.rlwhitcomb.test;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import info.rlwhitcomb.csv.*;
import info.rlwhitcomb.util.Environment;
import info.rlwhitcomb.util.FileUtilities;
import info.rlwhitcomb.util.Intl;
import info.rlwhitcomb.util.Options;


/**
 * Read a text file and parse into CSV-formatted fields and records.
 * For now we're just going to display them as a verification of the methods.
 * Also tests the writer by writing out the read records and reading back in
 * the written file which is then compared to the originally read records.
 * Later there may be more extensive testing.
 */
public class CSVTest
{
	private static final Charset utf8Charset = StandardCharsets.UTF_8;

	private static Character quoteChar = null;
	private static Quotes quote = null;
	private static Character delimChar = null;
	private static Delimiter delimiter = null;
	private static Character escapeChar = null;
	private static String recordSep = null;
	private static Separator separator = null;
	private static boolean preserveQuotes = false;
	private static boolean preserveWhitespace = false;
	private static boolean alwaysDelimitStrings = false;
	private static boolean hasHeaderRow = false;
	private static boolean ignoreEmpty = false;
	private static boolean writeBack = false;
	private static boolean useUTF8 = false;
	private static boolean useIterator = false;

	private static boolean argErrors = false;


	/**
	 * Reinitialize our static members for use with {@code Tester}
	 * where static initialization only happens once for multiple tests.
	 */
	private static final void resetOptions() {
	    quoteChar = null;
	    quote = null;
	    delimChar = null;
	    delimiter = null;
	    escapeChar = null;
	    recordSep = null;
	    separator = null;
	    preserveQuotes = false;
	    preserveWhitespace = false;
	    alwaysDelimitStrings = false;
	    hasHeaderRow = false;
	    ignoreEmpty = false;
	    writeBack = false;
	    useUTF8 = false;
	    useIterator = false;
	    argErrors = false;
	}

	private static void doHelp(PrintStream ps) {
	    Intl.printHelp(ps, "csv#test");
	}

	private static boolean processArg(String arg) {
	    if (arg.length() > 1) {
		char arg0 = arg.charAt(0);
		char arg1 = arg.charAt(1);
		boolean moreThanOne = arg.length() > 2;

		switch (arg0) {
		    case 'q':	// quote char
			if (moreThanOne) {
			    Intl.errFormat("csv#test.onlyOneChar", "q");
			    return false;
			}
			quoteChar = arg1;
			break;
		    case 'Q':	// select from Quotes
			String quoteName = arg.substring(1);
			quote = Quotes.fromString(quoteName);
			if (quote == null) {
			    Intl.errFormat("csv#test.unknownQuote", quoteName);
			    return false;
			}
			break;
		    case 'd':	// delimiter
			if (moreThanOne) {
			    Intl.errFormat("csv#test.onlyOneChar", "d");
			    return false;
			}
			delimChar = arg1;
			break;
		    case 'D':	// delimiter from Delimiter
			String delimName = arg.substring(1);
			delimiter = Delimiter.fromString(delimName);
			if (delimiter == null) {
			    Intl.errFormat("csv#test.unknownDelim", delimName);
			    return false;
			}
			break;
		    case 'r':	// record separator
			recordSep = arg.substring(1);
			break;
		    case 'S':	// separator from Separator
			String recordSepName = arg.substring(1);
			separator = Separator.fromString(recordSepName);
			if (separator == null) {
			    Intl.errFormat("csv#test.unknownSeparator", recordSepName);
			    return false;
			}
			break;
		    case 'e':	// escape char
			if (moreThanOne) {
			    Intl.errFormat("csv#test.onlyOneChar", "e");
			    return false;
			}
			escapeChar = Character.valueOf(arg1);
			break;
		    default:
			if (arg.equalsIgnoreCase("help")) {
			    doHelp(System.out);
			    System.exit(0);
			}
			Intl.errFormat("csv#test.unknownOption", arg);
			return false;
		}
	    }
	    else if (arg.length() > 0) {
		char arg0 = arg.charAt(0);
		switch (arg0) {
		    case 'a':	// always delimit strings
			alwaysDelimitStrings = true;
			break;
		    case 'u':	// preserve quotes
			preserveQuotes = true;
			break;
		    case 'w':	// preserve whitespace
			preserveWhitespace = true;
			break;
		    case 'E':	// no escape char
			escapeChar = Character.valueOf('\0');
			break;
		    case 'B':	// blank
			delimiter = Delimiter.SPACE;
			break;
		    case 'T':	// TAB
			delimiter = Delimiter.TAB;
			break;
		    case 'R':	// CR
			separator = Separator.CR;
			break;
		    case 'L':	// LF (newline)
			separator = Separator.NEWLINE;
			break;
		    case 'H':	// has header row
			hasHeaderRow = true;
			break;
		    case 'I':	// ignore empty lines
			ignoreEmpty = true;
			break;
		    case 'W':	// writeback test
			writeBack = true;
			break;
		    case 'i':	// use iterator
			useIterator = true;
			break;
		    case '8':	// UTF-8 charset
			useUTF8 = true;
			break;
		    case '?':
			doHelp(System.out);
			System.exit(0);
		    default:
			Intl.errFormat("csv#test.unknownOption", arg);
			return false;
		}
	    }

	    // no error at this point
	    return true;
	}

	private static void processArguments(final String[] args, final List<String> fileList) {
	    for (String arg : args) {
		if (arg.startsWith("--")) {
		    if (!processArg(arg.substring(2)))
			argErrors = true;
		}
		else if (arg.startsWith("-")) {
		    if (!processArg(arg.substring(1)))
			argErrors = true;
		}
		else if (Environment.isWindows() && arg.startsWith("/")) {
		    if (!processArg(arg.substring(1)))
			argErrors = true;
		}
		else if (fileList != null) {
		    fileList.add(arg);
		}
	    }
	}

	public static void main(String[] args) {
	    List<String> fileList = new ArrayList<>();

	    resetOptions();

	    // Process default options from the environment, but ignore any files listed there
	    Options.environmentOptions(CSVTest.class, (options) -> {
		processArguments(options, null);
	    });

	    // Process override options AND file names present on the command line
	    processArguments(args, fileList);

	    if (fileList.size() == 0) {
		Intl.errPrintln("csv#test.noInputFiles");
		argErrors = true;
	    }

	    if (argErrors) {
		doHelp(System.err);
		System.exit(1);
	    }

	    CSVFormat format = new CSVFormat();
	    if (quoteChar != null)
		format.withQuoteChar(quoteChar);
	    if (quote != null)
		format.withQuote(quote);
	    if (delimChar != null)
		format.withFieldSepChar(delimChar);
	    if (delimiter != null)
		format.withDelimiter(delimiter);
	    if (recordSep != null)
		format.withRecordSep(recordSep);
	    if (separator != null)
		format.withSeparator(separator);
	    if (escapeChar != null)
		format.withEscapeChar(escapeChar);
	    if (preserveQuotes)
		format.withPreserveQuotes(true);
	    if (preserveWhitespace)
		format.withPreserveWhitespace(true);
	    if (alwaysDelimitStrings)
		format.withAlwaysDelimitStrings(true);
	    if (hasHeaderRow)
		format.withHasHeaderRow(true);
	    if (ignoreEmpty)
		format.withIgnoreEmptyLines(true);

	    List<CSVRecord> recordList = null;
	    if (writeBack) {
		recordList = new ArrayList<CSVRecord>();
	    }

	    try {
		for (String file : fileList) {
		    Reader reader = useUTF8 ?
			new InputStreamReader(Files.newInputStream(Paths.get(file)), utf8Charset) :
			new FileReader(file);
		    CSVReader csvr = new CSVReader(reader, format);
		    int num = 0;
		    int numberOfFields = 0;
		    if (useIterator) {
			for (CSVRecord record : csvr) {
			    Intl.outFormat("csv#test.recordNum", ++num);
			    int fld = 0;
			    for (Object field : record) {
				Intl.outFormat("csv#test.record", ++fld, field);
			    }
			    // Establish the baseline number of fields from the first record
			    if (num == 1) {
				numberOfFields = record.size();
			    }
			    else {
				if (numberOfFields != record.size()) {
				    Intl.errFormat("csv#test.mismatchFieldCount", num, record.size(), numberOfFields);
				}
			    }
			    if (writeBack)
				recordList.add(record);
			}
		    }
		    else {
			CSVRecord record = null;
			while ((record = csvr.getNextRecord()) != null) {
			    Intl.outFormat("csv#test.recordNum", ++num);
			    int fld = 0;
			    for (Object field : record) {
				Intl.outFormat("csv#test.record", ++fld, field);
			    }
			    // Establish the baseline number of fields from the first record
			    if (num == 1) {
				numberOfFields = record.size();
			    }
			    else {
				if (numberOfFields != record.size()) {
				    Intl.errFormat("csv#test.mismatchFieldCount", num, record.size(), numberOfFields);
				}
			    }
			    if (writeBack)
				recordList.add(record);
			}
		    }
		    Intl.outPrintln("csv#test.separator");
		    Intl.outPrintln();

		    if (writeBack) {
			int recordErrors = 0;
			int lengthErrors = 0;
			int compareErrors = 0;
			File outputFile = FileUtilities.createTempFile("csvoutput");
			Writer writer = useUTF8 ? 
				new OutputStreamWriter(Files.newOutputStream(outputFile.toPath()), utf8Charset) :
				new FileWriter(outputFile);
			CSVWriter csvw = new CSVWriter(writer, format);
			for (CSVRecord outrec : recordList) {
			    csvw.writeNextRecord(outrec);
			}
			csvw.writeNextRecord(null);

			// Now read back and parse the newly written file
			// comparing as we go to the originally parsed records
			int recordNumber = 0;
			Iterator<CSVRecord> itr = recordList.iterator();
			Reader reader2 = useUTF8 ?
				new InputStreamReader(Files.newInputStream(outputFile.toPath()), utf8Charset) :
				new FileReader(outputFile);
			CSVReader csvr2 = new CSVReader(reader2, format);
			CSVRecord record2 = null;
			while ((record2 = csvr2.getNextRecord()) != null) {
			    recordNumber++;
			    if (!itr.hasNext()) {
				recordErrors++;
				Intl.errFormat("csv#test.outputNoInput", recordNumber);
			    }
			    else {
				CSVRecord record = itr.next();
				Object[] inputFields = record.getFields();
				Object[] outputFields = record2.getFields();
				if (inputFields.length != outputFields.length) {
				    lengthErrors++;
				    Intl.errFormat("csv#test.lengthMismatch",
					recordNumber, inputFields.length, outputFields.length);
				}
				for (int n = 0; n < inputFields.length; n++) {
				    if (!inputFields[n].equals(outputFields[n])) {
					compareErrors++;
					Intl.errFormat("csv#test.fieldMismatch",
						recordNumber, n, inputFields[n], outputFields[n]);
				    }
				}
			    }
			}
			if (itr.hasNext()) {
			    recordErrors++;
			    Intl.errFormat("csv#test.inputNoOutput", recordNumber);
			}
			int totalErrors = recordErrors + lengthErrors + compareErrors;
			if (totalErrors == 0) {
			    outputFile.delete();
			    Intl.outFormat("csv#test.inputEqualsOutput", file);
			}
			else {
			    Intl.outFormat("csv#test.inputNotEqualOutput",
				file, outputFile.getPath(), recordErrors, lengthErrors, compareErrors);
			}
		    }
		}
	    }
	    catch (IOException ioe) {
		System.err.println(ioe.getMessage());
	    }
	    catch (CSVException csve) {
		System.err.println(csve.getMessage());
	    }
	}

}

