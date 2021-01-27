/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2010-2011,2014-2016,2019-2021 Roger L. Whitcomb.
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
 *  Java language Pre-processor
 *
 * History:
 *    19-May-2010 (rlwhitcomb)
 *	First version after not finding anything
 *	useful via Google.
 *    24-May-2010 (rlwhitcomb)
 *	Correct backwards test of return values from
 *	processing functions: true means error, false
 *	is success.
 *    25-May-2010 (rlwhitcomb)
 *	Because of problems reporting undefined var
 *	errors even with #if defined(abc) && $(abc)...
 *	as well as possible problems recognizing 'and'
 *	as an operator instead of text :( we need to
 *	tokenize the input before passing to evaluate();
 *	this requires a complete rewrite of the parser.
 *    28-May-2010 (rlwhitcomb)
 *	Add some predefined symbols for use:
 *	__DATE__, __TIME__, __FILE__, __LINE__
 *	__JAVA_VERSION__, __JAVA_PP_VERSION__
 *    28-May-2010 (rlwhitcomb)
 *	Fix problems that give spurious errors on #endif
 *	if there were previous #endif errors.
 *    23-Jul-2010 (rlwhitcomb)
 *	Only allow '/' to start options on Windows; added
 *	"usage()" and "signOnBanner()" in response to "-?".
 *    02-Aug-2010 (rlwhitcomb)
 *	Output an error message when an input file cannot be found;
 *	implement "nologo" command-line option.
 *    04-Aug-2010 (rlwhitcomb)
 *	Implement "-p path" command-line option for searching for
 *	included files; renamed enum "Tokens" to "Token" to be better
 *	English; more descriptive Javadoc to improve documentation.
 *    06-Aug-2010 (rlwhitcomb)
 *	Implement "-C" option to specify a different directive
 *	character (for files that use "#" as a comment indicator).
 *    13-Aug-2010 (rlwhitcomb)
 *	Implement checks for input and output files already existing
 *	and output file newer than input.  This will reduce the unnecessary
 *	preprocessing of files that didn't change.  But, adding a "-A" (always)
 *	switch to force things (if, for instance, environment variables have
 *	changed).
 *    16-Aug-2010 (rlwhitcomb)
 *	Small coding change in the use of the "defines" map; made distinction
 *	between "-r" and "-R" switches and added recursive directory searching
 *	for "-R"; cleaned up setting of input and output extensions and set by
 *	default for single files from the names (if extension is present);
 *	cleaned up problems with highly nested parentheses; allow macro
 *	symbols to stand for themselves inside #ifxxx directives (that is,
 *	$(macro) is not necessary in expressions (it is supported but not
 *	required); fixed other problems with expression parsing uncovered by
 *	testing with Microsoft C headers; implemented #error <msg> directive;
 *	allow #define VAR without a value (value becomes empty string);
 *	standardized the syntax for a variable name as "[_A-Za-z]\\w*".
 *    20-Aug-2010 (rlwhitcomb)
 *	Cleaned up output a little bit by not writing Line/Directive header
 *	until we actually get a directive to display.
 *    24-Aug-2010 (rlwhitcomb)
 *	Fix parsing problems with a version string like "1.5.1"; fix error
 *	reporting position for macro substitutions; do substitutions inside
 *	quoted strings; strip quotes before doing string compares.
 *	Made the leap to make this into an Ant task -- still will execute
 *	from the command line also.
 *    06-Sep-2010 (rlwhitcomb)
 *	Silently allow an empty value for the "define=" property within Ant.
 *	Necessary because of the DEBUG setting which will be undefined unless
 *	set and there is no way to not define something with the <condition>
 *	tasks.  So, just ignore empty values.
 *    22-Sep-2010 (rlwhitcomb)
 *	Make output a little less verbose in the "skipping" case.
 *    06-Oct-2010 (rlwhitcomb)
 *	Fix subtle bugs with "!" and "||" operators; add "plus" verbose mode.
 *    20-Oct-2010 (rlwhitcomb)
 *	Fix bug with #elif; added "#elseif" as an alternative spelling.
 *    04-Nov-2010 (rlwhitcomb)
 *	Allow multiple defines or undefines (comma or semicolon delimited)
 *	on the -D or define= attribute (necessary for use inside Ant);
 *	handle VARREF at the "otherFactor" and "stringFactor" levels;
 *	add "Error: " prefix to all System.err messages.
 *    19-May-2011 (rlwhitcomb)
 *	Add ability to pass through #directives if specified as
 *	##directive.
 *    31-Aug-2011 (rlwhitcomb)
 *	Oops!  Updated COPYRIGHT_YEAR to be the correct value.
 *    05-May-2014 (rlwhitcomb)
 *	Trace the output line after doing variable substitution.
 *	Update version and copyright year.
 *    31-Aug-2015 (rlwhitcomb)
 *	Javadoc cleanup (found by Java 8).
 *    23-Feb-2016 (rlwhitcomb)
 *	Well, there are more Javadoc fixes to be made...
 *    15-Mar-2019 (rlwhitcomb)
 *	Don't use FileInputStream/FileOutputStream due to GC problems b/c of the finalize
 *	method in these classes. Fix wildcard imports. Use StandardCharsets.
 *    21-May-2019 (rlwhitcomb)
 *	Add Timezone to __TIME__ display; update copyright year string.
 *	Reformat to current tab specs.  Bump version.
 *    09-Jan-2020 (rlwhitcomb)
 *	Change package, bump version, update copyright year.
 *    21-Dec-2020 (rlwhitcomb)
 *	Update Javadoc to latest conventions.
 *    21-Jan-2021 (rlwhitcomb)
 *	Add "-L" (log file) and "-W" parameters. Recognize "${macro}" format also.
 *    27-Jan-2021 (rlwhitcomb)
 *	Fix a bug with flags when setting "recurseDirectories"; some tiny cleanup.
 */
package info.rlwhitcomb.preproc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;


/**
 * Provide C/C++ style preprocessing for Java source code.
 * <p> Supports the following constructs:
 * <ul>
 * <li><code>#define <var>var value</var></code>
 * <li><code>#define <i>var</i></code>
 * <li><code>#undef <i>var</i></code>
 * <li><code>#if</code> {expr}
 * <li><code>#ifnum</code> {expr}
 * <li><code>#ifstr</code> {expr}
 * <li><code>#ifistr</code> {expr}
 * <li><code>#ifdef</code>
 * <li><code>#ifndef</code>
 * <li><code>#else</code>
 * <li><code>#elif</code> {expr}
 * <li><code>#elseif</code> {expr}
 * <li><code>#endif</code>
 * <li><code>#include</code> {file}
 * <li><code>#error</code> {message}
 * </ul>
 * <p> The {expr} can be a combination of integer, float, or string constants
 * (floating-point numbers can be entered using exponential notation)
 * and relational or arithmetic operators:
 * <p> <code>== != &lt; &gt; &lt;= &gt;= ! &amp;&amp; || AND OR NOT</code>
 * <p> <code>+ - / * %</code> (with numeric values only)
 * or "<code>defined(<i>var</i>)</code>"
 * <p> Parentheses can be used to alter the order of operations (which
 * are otherwise done according to standard C/C++ operator precedence rules).
 * <p> The conditional operators <code>&amp;&amp;</code> (or <code>AND</code>) and <code>||</code> (or <code>OR</code>)
 * are short-circuited so that if the first argument satisfies the condition the second is not evaluated.
 * <p> By default expressions are evaluated according to the types of the values
 * involved:  if both operands are numeric, the {op} will be performed numerically
 * otherwise the numeric operators will be illegal and the comparison will be done
 * lexicographically (case-insensitive if the <code>#ifistr</code> command is used).
 * <p> Using <code>#ifnum</code> or <code>#ifstr</code> or <code>#ifistr</code> will force the operations to be done
 * strictly numerically or strictly as string values with errors given if the operands
 * do not conform (in the case of <code>#ifnum</code>).
 * <p> The rules for substituting values defined by <code>#define</code> are different than C:
 * the syntax is <code>$(<i>varname</i>)</code> or <code>${<i>varname</i>}</code>.
 * However, in expressions within the <code>#if<i>xxx</i></code> directives the <code><i>varname</i></code>
 * can be used just by itself (to match C preprocessor usage in this case).
 * Values can be defined in terms of other values.
 * <p> By default the current environment variables are all defined as variables.  So
 * for instance, if the environment contains a variable ING_REL=10.0, to access that
 * variable in the code, use <code>$(ING_REL)</code>.  According to Java conventions, variable names
 * are case-sensitive.
 * <p> There are several other predefined variables available:
 * <ul>
 * <li><code>__DATE__</code> (the data the preprocessing started in <code>yyyy-MM-dd</code> format)
 * <li><code>__TIME__</code> (the time it started in <code>HH:mm:ss.SSS z</code> format)
 * <li><code>__FILE__</code> (the current file being processed)
 * <li><code>__LINE__</code> (the line number within that file)
 * <li><code>__JAVA_VERSION__</code> (the Java version)
 * <li><code>__JAVA_PP_VERSION__</code> (the Java preprocessor version)
 * </ul>
 * <p> Command-line arguments can be:
 * <ul>
 * <li><code>-nologo</code> (don't display sign-on banner)
 * <li><code>-D<i>var</i>=<i>value</i></code> (define variable value)
 * <li><code>-D<i>var</i></code> (define variable to empty value)
 * <li><code>-U<i>var</i></code> (undefine variable)
 * <li><code>-C<i>char</i></code> (set directive indicator character [normally '#'])
 * <li><code>-O<i>ext</i></code> (specify default output file extension)
 * <li><code>-I<i>ext</i></code> (specify default input file extension)
 * <li><code>-X</code> (ignore undefined vars in expressions)
 * <li><code>-F:UTF8</code> (specify UTF-8 format for input/output files)
 * <li><code>-V</code> (verbose reporting of progress)
 * <li><code>-A</code> (always process files regardless of relative timestamps)
 * <li><code>-r</code> (in which case the file name spec(s) are processed as directories)
 * <li><code>-R</code> (file name spec(s) are processed as directories and searched recursively)
 * <li><code>-E<i>envvar</i></code> (env var to use to search for #include'd files, defaults to "INCLUDE")
 * <li><code>-P<i>path(s)</i></code> (path(s) to use to search for #include'd files, separate by ";" or ",")
 * <li><code>-L:<i>path</i></code> (path to log file to receive output - defaults to stdout)
 * <li><code>-W</code> (overwrite the output log file - defaults to append)
 * <li>file name(s)
 * </ul>
 * <p> This process can also be invoked as an Ant task by using the following in your "build.xml":
 * <p> <code>&lt;taskdef name="preproc" classname="info.rlwhitcomb.preproc.JavaPreProc" classpath="anttasks.jar"/&gt;</code>.
 * <p> The directives supported in this context are:
 * <ul><li><code>directiveChar="<i>ch</i>"</code> (same as <code>-C<i>ch</i></code> parameter)
 * <li><code>define="<i>var</i>=<i>value</i>"</code> or
 * <li><code>define="<i>var</i>"</code> (same as <code>-D<i>var</i></code>[<code>=<i>value</i></code>] parameter)
 * <li><code>undefine="<i>var</i>"</code> (same as <code>-U<i>var</i></code> parameter)
 * <li><code>outputExt="<i>.ext</i>"</code> (same as <code>-I<i>.ext</i></code> parameter)
 * <li><code>inputExt="<i>.ext</i>"</code> (same as <code>-O<i>.ext</i></code> parameter)
 * <li><code>includePath="<i>dir</i></code>[<code>,<i>dir</i></code>]<code>"</code> (same as <code>-P<i>dir,dir</i></code> parameter)
 * <li><code>ignoreUndefined="true"</code> (same as <code>-X</code> parameter)
 * <li><code>format="UTF8"</code> (same as <code>-F:UTF8</code> parameter)
 * <li><code>verbose="true"</code> (same as <code>-V</code> parameter)
 * <li><code>alwaysProcess="true"</code> (same as <code>-A</code> parameter)
 * <li><code>processAsDirectory="true"</code> (same as <code>-r</code> parameter)
 * <li><code>recurseDirectories="true"</code> (same as <code>-R</code> parameter)
 * <li><code>includeVar="<i>var</i>"</code> (same as <code>-E<i>var</i></code> parameter)
 * <li><code>nologo="true"</code> (same as <code>-nologo</code> parameter)
 * <li><code>file="<i>filename</i>"</code> (same as file name argument)
 * <li><code>dir="<i>directory</i>"</code> (same as directory name argument (with <code>-r</code> or <code>-R</code> switch)
 * <li><code>log="<i>logfilepath</i>"</code> (same as <code>-L</code> parameter)
 * <li><code>overwrite="true"</code> (same as <code>-W</code> parameter)
 * </ul>
 * <p> Default input file extension is <code>".javapp"</code> and default output
 * file extension is <code>".java"</code> (can be overridden with <code>"-O<i>ext</i>"</code> option).
 * <p> By default, all values in the current environment are automatically
 * defined for use in <code>$(<i>var</i>)</code> or <code>${<i>var</i>}</code> constructs, unless specifically excluded by
 * using the <code>-U<i>var</i></code> command-line option.  Values defined with <code>"-D<i>var</i>=<i>value</i>"</code>
 * will override values in the environment, as will values explicitly defined
 * in the source.
 * <p> File names on the #include directive can be delimited by nothing, by single or
 * double quotes or by <code>&lt;&gt;</code>, <code>{}</code> or <code>[]</code> brackets.  There is no difference in the search path
 * for the file depending on the brackets (unlike C/C++).  The search path for included files is:
 * <ul><li>Current directory
 * <li>paths specified by one or more -P values
 * <li>directories listed in the <code>INCLUDE</code> environment variable (or env var specified by <code>-E</code> flag)
 * </ul>
 * <p> If the include file name does not have an extension, the search above will be done
 * on the unadorned file name, and if not found the search will be repeated using the
 * default (or overridden by <code>-I<i>ext</i></code>) input extension.
 * <p> If an input file extension is given with <code>-I</code> but not an output one, the default output
 * value is the input value with any trailing "pp" removed.  If output is given but not input, the input
 * is the output value with "pp" appended.  If the <code>-r</code> or <code>-R</code> switches are not
 * given and no input extension is specified with <code>-I</code> then the extension of each input
 * file is used (if any) and the output extension is then inferred if <code>-O</code> is not used.
 * <p> Note: the input and output file names cannot end up being the same by these rules.
 * <p> By default, all input files are processed as if encoded in the current native
 * codepage setting, unless the <code>"-F:UTF8"</code> switch is used, in which case the input files
 * will be read as UTF-8 encoded.
 * <p> Unless the <code>"-A"</code> switch is used, the processing will be skipped if the output file
 * already exists and is more recent than the input file.  This does not take into account
 * included files, so <code>"-A"</code> should be used if there is a doubt about included files being newer.
 * <p> If directives are given as <code>##<i>directive</i></code> then they will be passed through as
 * <code>#<i>directive</i></code> to the output file.
 * <p> Also, if a <code>#*</code> line is found, it will be ignored and not passed through to the output
 * (it is a preprocessor comment).
 * <h2>Known Deficiencies:</h2>
 * <ul>
 * <li>Wild-card values are not supported on the input file name(s).
 * </ul>
*/
public class JavaPreProc extends Task
{
	/** The current list of defined symbols and their values. */
	private HashMap<String,String> defines = null;
	/** Default extension for input files. */
	private static String defaultInputExt = ".javapp";
	/** Default (or overridden) extension for input files. */
	private String inputExt = null;
	/** Default extension for output files. */
	private static String defaultOutputExt = ".java";
	/** Default or overridden extension for output files. */
	private String outputExt = null;
	/** Environment variable to use to search for included files. */
	private String inclEnvVar = "INCLUDE";
	/** Flag to say whether to process the input (and output) as UTF-8 encoded
	 * or encoded by the default platform encoding. */
	private boolean processAsUTF8 = false;
	/** Flag to say whether to verbosely log what we're doing. */
	private boolean verbose = false;
	/** Plus verbose mode also outputs lines inside #if/#else that are being output. */
	private boolean plusVerbose = false;
	/** Extended flag to output debug information also. */
	private boolean superVerbose = false;
	/** Flag to say we're parsing file name specs on the command line as directories. */
	private boolean processAsDirectory = false;
	/** Flag to say we're parsing file name specs on the command line as directories
	 * and recursively processing all applicable files within them. */
	private boolean recurseDirectories = false;
	/** Flag to say to ignore undefined symbols and silently expand them as empty
	 * strings.  The default will be to issue an error message. */
	private boolean ignoreUndefined = false;
	/** Flag to say: "display sign-on banner" (or not for more silent operation). */
	private boolean displayLogo = true;
	/** Flag to say: "always do the processing regardless of file time stamps". */
	private boolean alwaysProcess = false;
	/** List of paths to use to search for included files. */
	private Vector<String> includePaths = null;
	/** Directive start character. */
	private char directiveStartCh = '#';
	/** List of input files or directories to process. */
	private Vector<String> fileArgs = new Vector<String>();
	/** Output log stream (defaults to {@link System#out}). */
	private PrintStream out = System.out;
	/** Error stream (defaults to {@link System#err}). */
	private PrintStream err = System.err;
	/** Name of the output log file (if specified). */
	private String logFileName = null;
	/** Overwrite the output log file (not applicable to default). */
	private boolean overwriteLog = false;

	/** <code>__DATE__</code> predefined variable name. */
	private static final String DATE_VAR_NAME = "__DATE__";
	/** <code>__TIME__</code> predefined variable name. */
	private static final String TIME_VAR_NAME = "__TIME__";
	/** <code>__FILE__</code> predefined variable name. */
	private static final String FILE_VAR_NAME = "__FILE__";
	/** <code>__LINE__</code> predefined variable name. */
	private static final String LINE_VAR_NAME = "__LINE__";
	/** <code>__JAVA_VERSION__</code> predefined variable name. */
	private static final String JAVA_VERSION_VAR_NAME = "__JAVA_VERSION__";
	/** <code>__JAVA_PP_VERSION__</code> predefined variable name. */
	private static final String JAVA_PP_VERSION_VAR_NAME = "__JAVA_PP_VERSION__";

	/** Pattern to parse the <code>-D<i>var</i>=<i>value</i></code> command-line switch. */
	private static Pattern defPat = Pattern.compile("^([_A-Za-z]\\w*)=(.*)$");
	/** Pattern to parse the <code>-D<i>var</i></code> command-line switch. */
	private static Pattern defAltPat = Pattern.compile("^([_A-Za-z]\\w*)$");
	/** Format string to build a regular expression to parse and recognize one of our preprocessing instructions. */
	private static String cmdPatFormat = "^\\s*%1$c\\s*(\\S+)(.*)$";
	/** Format string to build a regular expression to parse and recognize a pass-through preprocessing instruction. */
	private static String passPatFormat = "^\\s*%1$c(%1$c\\s*\\S+.*)$";
	/** Format string to build a regular expression to parse and recognize a comment directive that is not passed through. */
	private static String commentPatFormat = "^\\s*%1$c\\*.*$";
	/** Pattern to parse our preprocessing instructions.  Built from {@link #cmdPatFormat}. */
	private Pattern cmdPat = null;
	/** Pattern to parse a pass-through preprocessing instruction.  Built from {@link #passPatFormat}. */
	private Pattern passPat = null;
	/** Pattern to parse a comment directive line.  Built from {@link #commentPatFormat}. */
	private Pattern commentPat = null;
	/** Pattern to parse a <code>#define <i>var value</i></code> directive in the source. */
	private static Pattern def2Pat = Pattern.compile("^([_A-Za-z]\\w*)\\s+(.*)$");
	/** Pattern to parse a <code>#define <i>var</i></code> directive in the source. */
	private static Pattern def3Pat = Pattern.compile("^([_A-Za-z]\\w*)\\s*$");
	/** Pattern used to separate the "INCLUDE" environment variable or define and undefine lists into pieces. */
	private static Pattern comma = Pattern.compile("[,;]");

	/** Pattern to skip white space inside an expression. */
	private static Pattern WHITE_SPACE = Pattern.compile("\\s+");
	/** Pattern to recognize a macro reference: <code>$(<i>macroname</i>)</code> */
	private static Pattern MACRO_REF = Pattern.compile("\\$\\(([_A-Za-z]\\w*)\\)");
	/** Alternate pattern to recognize a macro reference: <code>${<i>macroname</i>}</code> */
	private static Pattern MACRO_REF2 = Pattern.compile("\\$\\{([_A-Za-z]\\w*)\\}");
	/** Pattern to match the reserved word <code>"true"</code>. */
	private static Pattern TRUE_CONST = Pattern.compile("^[tT][rR][uE][eE]");
	/** Pattern to match the reserved word <code>"false"</code>. */
	private static Pattern FALSE_CONST = Pattern.compile("^[fF][aA][lL][sS][eE]");
	/** Pattern to match an integer constant. */
	private static Pattern INT_CONST = Pattern.compile("^[0-9]+");
	/** Pattern to match a floating-point constant. */
	private static Pattern FLT_CONST = Pattern.compile("^[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");
	/** Pattern to recognize the <code>"defined(<i>var</i>)"</code> function */
	private static Pattern DEFINED_FUNC = Pattern.compile("^[dD][eE][fF][iI][nN][eE][dD]\\s*\\(\\s*([_A-Za-z]\\w*)\\s*\\)");
	/** Pattern to recognize a <code>"NOT"</code> operator. */
	private static Pattern NOT_OP = Pattern.compile("^[nN][oO][tT]");
	/** Pattern to recognize an <code>"AND"</code> operator. */
	private static Pattern AND_OP = Pattern.compile("^[aA][nN][dD]");
	/** Pattern to recognize an <code>"OR"</code> operator. */
	private static Pattern OR_OP = Pattern.compile("^[oO][rR]");
	/** Pattern to recognize any old identifier. */
	private static Pattern IDENT = Pattern.compile("^([_A-Za-z]\\w*)");

	/** The default timezone value. */
	private static TimeZone zone = null;
	/** The current calendar used to format date and time. */
	private static Calendar currentCal = null;
	/** The {@link SimpleDateFormat} used for the variable <code>__DATE__</code>. */
	private static DateFormat dateFmt = null;
	/** The {@link SimpleDateFormat} used for the variable <code>__TIME__</code>. */
	private static DateFormat timeFmt = null;

	/** The current version of this software. */
	private static final String VERSION = "1.1.8";
	/** The current copyright year. */
	private static final String COPYRIGHT_YEAR = "2010-2011,2014-2016,2019-2021";


	/**
	 * Initialize variables that don't change.
	 */
	static {
		zone = TimeZone.getDefault();
		dateFmt = new SimpleDateFormat("yyyy-MM-dd");
		timeFmt = new SimpleDateFormat("HH:mm:ss.SSS z");
		currentCal = Calendar.getInstance(zone);
		dateFmt.setCalendar(currentCal);
		timeFmt.setCalendar(currentCal);
	}


	/**
	 * Enum to determine how to interpret expressions.
	 */
	enum ProcessAs
	{
		/** Process the expression as numeric.  All values will be coerced
		 * to numbers and any that can't be coerced will be flagged as errors.
		 * This is the mode for the <code>"ifnum"</code> statement.
		 */
		NUMERIC,
		/** Process the expression as strictly a string.  This means comparisons
		 * of values will be done according to lexicographic order.  This is the
		 * mode for the <code>"ifstr"</code> statement.
		 */
		FORCESTRING,
		/** Process the expression as a string, but do comparisons as
		 * case-insensitive.  This means 'abc' == 'ABC' (for instance).
		 * This is the mode for the <code>"ifistr"</code> statement.
		 */
		STRINGINSENSITIVE,
		/** Process the expression as "normal".  This means that if the values
		 * can be successfully coerced to numbers, they will be compared numerically
		 * otherwise they will be compared lexicographically.  This is the mode
		 * for the <code>"if"</code> statement.
		 */
		NORMAL
	};

	/**
	 * Enum for expression operators.
	 */
	enum Operator
	{
		/** The equals operator, that is, <code>"=="</code>. */
		EQUAL,
		/** The not equals operator, that is, <code>"!="</code>. */
		NOTEQUAL,
		/** The less than operator, that is, <code>"&lt;"</code>. */
		LESS,
		/** The less or equal operator, that is, <code>"&lt;="</code>. */
		LESSEQUAL,
		/** The greater than operator, that is, <code>"&gt;"</code>. */
		GREATER,
		/** The greater or equal operator, that is, <code>"&gt;="</code>. */
		GREATEREQUAL,
		/** The "AND" operator, that is, <code>"&amp;&amp;"</code> or the word <code>"AND"</code>. */
		ANDOP,
		/** The "OR" operator, that is, <code>"||"</code> or the word <code>"OR"</code>. */
		OROP,
		/** The "NOT" equals operator, that is, <code>"!"</code> or the word <code>"NOT"</code>. */
		NOTOP,
		/** The addition operator, that is, <code>"+"</code>. */
		ADD,
		/** The subtraction operator, that is, <code>"-"</code>. */
		SUBTRACT,
		/** The multiplication operator, that is, <code>"*"</code>. */
		MULTIPLY,
		/** The division operator, that is, <code>"/"</code>. */
		DIVIDE,
		/** The modulus operator, that is, <code>"%"</code>. */
		MODULUS,
		/** Not an operator.  This is the type for a token that is not a {@link Token#OPER}. */
		NONE
	};

	/**
	 * Enum for input tokens.  These are the broad categories of tokens that are parsed out of the input.
	 * Every character in an expression must be characterized as belonging to one of these tokens.
	 */
	enum Token
	{
		/** A single-quoted string, that is, a string surrounded by <code>'</code>.
		 * <p>Note: currently does not support embedded single quotes.  Hint: use a
		 * double-quoted string when the value must contain single quotes.
		 */
		SQSTRING,
		/** A double-quoted string, that is, a string surrounded by <code>"</code>.
		 * <p>Note: currently does not support embedded double quotes.  Hint: use a
		 * single-quoted string when the value must contain double quotes.
		 */
		DQSTRING,
		/** An integer constant.  Must only contain the digits '0' - '9'. */
		INTCONST,
		/** A floating-point constant.  The regular expression syntax is {@link #FLT_CONST}. */
		FLTCONST,
		/** Another type of constant.  At the moment only <code>"true"</code> and <code>"false"</code> fall into this category. */
		OTHERCONST,
		/** A variable reference.  This is a reference to a macro or symbol defined by <code>#define</code>.
		 * <p>This syntax for this token is: <code>$(<i>macroname</i>)</code> or <code>${<i>macroname</i>}</code>.
		 * Can also be just an identifier by itself when inside an <code>#if<i>xxx</i></code> expression
		 * (this helps compatibility with C syntax).
		 */
		VARREF,
		/** An operator, that is, one of the {@link Operator} values. */
		OPER,
		/** An open parenthesis character <code>"("</code>. */
		OPENPAREN,
		/** A close parenthesis character <code>")"</code>. */
		CLOSEPAREN,
		/** The <code>"defined(<i>var</i>)"</code> function.  The whole sequence, including the variable name, is
		 * represented by this token, whose <i>value</i> is the variable name within the parentheses.
		 */
		DEFINEDFUNC,
		/** Any kind of white space including space, tab, newline, form feed, etc. */
		WHITESPACE
	};

	/**
	 * Static class used for tokenizing input expressions.
	 */
	class TokenValue
	{
		/** Which kind of token is it. */
		public Token tok;
		/** Actual input string, or var name if a VARREF, or contents of string constant. */
		public String value;
		/** Which operator it is if an OPER type. */
		public Operator op;
		/** Starting position in the input line of this token. */
		public int startPos;
		/** Length of the token string in the input line. */
		public int tokenLen;

		public TokenValue(Token t, String v, Operator o) {
		    this.tok = t;
		    this.value = v;
		    this.op = o;
		}
		@Override
		public boolean equals(Object o) {
		    if (o instanceof TokenValue) {
			try {
			    TokenValue t = (TokenValue)o;
			    switch (this.tok) {
				case SQSTRING:
				case DQSTRING:
				    return t.value.equals(value);
				case OPER:
				    return t.op == op;
				case INTCONST:
				    return Integer.parseInt(t.value) == Integer.parseInt(value);
				case FLTCONST:
				    return Double.parseDouble(t.value) == Double.parseDouble(value);
				case OTHERCONST:
				    return t.value.equalsIgnoreCase(value);
				case VARREF:
				case DEFINEDFUNC:
				    return t.value.equals(value);
				case WHITESPACE:
				    return t.tok == tok;
			    }
			}
			catch (NumberFormatException nfe) {
			    err.format("Error: Numbers don't compare because of bad format!%n");
			}
		    }
		    return false;
		}
		public void setPos(int start, int len) {
		    this.startPos = start;
		    this.tokenLen = len;
		}
	};


	/**
	 * Enum to deal with #elif processing
	 */
	enum IfType
	{
		/** Last stacked value was from "if" (or a variant). */
		IF_STMT,
		/** Last stacked value was from "elif". */
		ELIF_STMT
	};


	/**
	 * Class to hold nested "if" statement output state.
	 */
	class IfState
	{
		/** Saved state of the global 'doingOutput' flag. */
		boolean doingOutput;
		/** Type of the statement that pushed this state. */
		IfType lastStmt;
		/**
		 * Constructor taking both values at once.
		 * @param doing The current "doingOutput" flag.
		 * @param ift   The IF/ELIF flag.
		 */
		public IfState(boolean doing, IfType ift) {
		    this.doingOutput = doing;
		    this.lastStmt = ift;
		}
	};


	/**
	 * Class to filter input file names according to the current inputExt.
	 */
	class InputFileFilter implements FilenameFilter
	{
		@Override
		public boolean accept(File dir, String name) {
		    File f = new File(dir, name);
		    return (f.exists() && f.isFile() && name.endsWith(inputExt));
		}
	}


	/**
	 * Class to filter input files that are really directories.
	 */
	class InputDirFilter implements FileFilter
	{
		@Override
		public boolean accept(File f) {
		    return (f.exists() && f.isDirectory());
		}
	}


	/**
	 * Private exception class used to gracefully exit from processing
	 * without doing anything.
	 */
	class DontProcessException extends Exception {
	}


	/**
	 * Try to get a sensible message from an {@link Exception} even if the
	 * message is empty (such as for {@code NullPointerException}).
	 *
	 * @param	ex	The exception to process.
	 * @return	A (hopefully) better message than just <code>getMesssage()</code>
	 *		will return.
	 */
	private static String exceptMessage(Exception ex) {
	    String className = ex.getClass().getSimpleName();
	    String message   = ex.getMessage();

	    if (message == null || message.isEmpty())
		message = className;
	    else if (ex instanceof NullPointerException
		  || ex instanceof CharacterCodingException
		  || ex instanceof FileNotFoundException
		  || ex instanceof NoSuchFileException
		  || ex instanceof UnsupportedOperationException)
		message = String.format("%1$s: %2$s", className, message);

	    return message;
	}

	/**
	 * Perform macro substitutions on the given input file line.
	 * <p> Macros are of the form <code>$(<i>macro</i>)</code> or
	 * <code>${<i>macro</i>}</code> where "macro" has been defined
	 * by a <code><b>#define</b></code> or in the environment.
	 * The derived value of the macro will be substituted for the
	 ( whole <code>$(<i>macro</i>)</code> or <code>${<i>macro</i>}</code> piece.
	 * <p> If one macro is defined in terms of another macro, a recursive call
	 * will be made to perform macro substitutions on the macro value.  This
	 * occurs as many times as necessary.
	 *
	 * @param	line	Input value containing <code>$(<i>macro</i>)</code> or
	 *			<code>${<i>macro</i>}</code> references.
	 * @return		Input with the appropriate macro substitutions made.
	 * @see		#MACRO_REF
	 * @see		#MACRO_REF2
	 * @see		#defines
	 */
	private String doSubs(String line) {
	    if (line.isEmpty())
		return line;

	    StringBuffer sb = new StringBuffer();

	    Matcher m = MACRO_REF.matcher(line);
	    boolean found = m.find();
	    if (!found) {
		m = MACRO_REF2.matcher(line);
		found = m.find();
	    }
	    while (found) {
		String name = m.group(1);
		String value;
		if ((value = defines.get(name)) == null) {
		    if (!ignoreUndefined)
			err.format("Error: Macro \"%1$s\" not defined!%n", name);
		}
		// Recursive call in common case that value
		// is defined in terms of other macros
		if (value != null)
		    m.appendReplacement(sb, Matcher.quoteReplacement(doSubs(value)));

		found = m.find();
	    }
	    m.appendTail(sb);
	    return sb.toString();
	}


	/**
	 * Strip leading and trailing quotes from a string.
	 *
	 * @param	value	The candidate string.
	 * @return		The string stripped of leading and trailing quotes.
	 */
	private String stripQuotes(String value) {
	    if (value != null) {
		String endQuote = null;
		if (value.startsWith("\""))
		    endQuote = "\"";
		else if (value.startsWith("'"))
		    endQuote = "'";
		if (endQuote != null) {
		    if (value.endsWith(endQuote)) {
			return value.substring(1, value.length()-1);
		    }
		}
	    }
	    return value;
	}


	/**
	 * Strip leading and trailing brackets from a string.
	 *
	 * @param	value	The candidate string.
	 * @return		The string stripped of leading and trailing brackets (or quotes).
	 */
	private String stripBrackets(String value) {
	    String endBracket = null;
	    if (value.startsWith("<")) {
		endBracket = ">";
	    }
	    else if (value.startsWith("[")) {
		endBracket = "]";
	    }
	    else if (value.startsWith("{")) {
		endBracket = "}";
	    }
	    if (endBracket != null) {
		if (value.endsWith(endBracket))
		    return value.substring(1, value.length()-1);
		// Could be an error, but why bother?
		return value;
	    }
	    // Maybe it starts with a quote instead
	    return stripQuotes(value);
	}


	/**
	 * Tokenize an input string.
	 *
	 * @param	input	The string we read from the input file.
	 * @param	startPos	The starting position in the line.
	 * @return		The input broken up into tokens.
	 * @throws	ParseException if the tokenizing found a problem
	 *		(probably should never happen, or the programmer
	 *		has made a mistake).
	 */
	private ArrayList<TokenValue> tokenizeInput(String input, int startPos)
		throws ParseException
	{
	    ArrayList<TokenValue> tokens = new ArrayList<TokenValue>(input.length());
	    int pos = 0;
	    int endPos = input.length();
	    while (pos < endPos) {
		String value = null;
		int len = -1;
		Operator op = Operator.NONE;
		Token tok = Token.OPER;

		// Start with obvious tokens
		CharSequence seq = input.subSequence(pos, endPos);
		Matcher m = null;
		m = WHITE_SPACE.matcher(seq);
		if (m.lookingAt()) {
		    len = m.end();
		    tok = Token.WHITESPACE;
		}
		else {
		    m = TRUE_CONST.matcher(seq);
		    if (m.lookingAt()) {
			len = m.end();
			tok = Token.OTHERCONST;
		    }
		    else {
			m = FALSE_CONST.matcher(seq);
			if (m.lookingAt()) {
			    len = m.end();
			    tok = Token.OTHERCONST;
			}
			else {
			    m = FLT_CONST.matcher(seq);
			    if (m.lookingAt()) {
				len = m.end();
				// The floating-point pattern also matches
				// an integer, so disambiguate here
				String test = m.group();
				if (test.indexOf(".") < 0 &&
				    test.indexOf("e") < 0 &&
				    test.indexOf("E") < 0)
				    tok = Token.INTCONST;
				else
				    tok = Token.FLTCONST;
			    }
			    else {
				// TODO: do we even need this with the tests above?
				m = INT_CONST.matcher(seq);
				if (m.lookingAt()) {
				    len = m.end();
				    tok = Token.INTCONST;
				}
				else {
				    m = DEFINED_FUNC.matcher(seq);
				    if (m.lookingAt()) {
					len = m.end();
					tok = Token.DEFINEDFUNC;
					value = m.group(1);
				    }
				    else {
					m = MACRO_REF.matcher(seq);
					boolean lookingAt = m.lookingAt();
					if (!lookingAt) {
					    m = MACRO_REF2.matcher(seq);
					    lookingAt = m.lookingAt();
					}
					if (lookingAt) {
					    len = m.end();
					    tok = Token.VARREF;
					    value = m.group(1);
					}
					else {
					    // Look for operators
					    switch (input.charAt(pos)) {
						case '<':
						    if (input.charAt(pos+1) == '=') {
							len = 2;
							op = Operator.LESSEQUAL;
						    }
						    else {
							len = 1;
							op = Operator.LESS;
						    }
						    break;
						case '>':
						    if (input.charAt(pos+1) == '=') {
							len = 2;
							op = Operator.GREATEREQUAL;
						    }
						    else {
							len = 1;
							op = Operator.GREATER;
						    }
						    break;
						case '=':
						    if (input.charAt(pos+1) == '=') {
							len = 2;
							op = Operator.EQUAL;
						    }
						    break;
						case '!':
						    if (input.charAt(pos+1) == '=') {
							len = 2;
							op = Operator.NOTEQUAL;
						    }
						    else {
							len = 1;
							op = Operator.NOTOP;
						    }
						    break;
						case '(':
						    len = 1;
						    tok = Token.OPENPAREN;
						    break;
						case ')':
						    len = 1;
						    tok = Token.CLOSEPAREN;
						    break;
						case '&':
						    if (input.charAt(pos+1) == '&') {
							len = 2;
							op = Operator.ANDOP;
						    }
						    break;
						case '|':
						    if (input.charAt(pos+1) == '|') {
							len = 2;
							op = Operator.OROP;
						    }
						    break;
						case '+':
						    len = 1;
						    op = Operator.ADD;
						    break;
						case '-':
						    len = 1;
						    op = Operator.SUBTRACT;
						    break;
						case '*':
						    len = 1;
						    op = Operator.MULTIPLY;
						    break;
						case '/':
						    len = 1;
						    op = Operator.DIVIDE;
						    break;
						case '%':
						    len = 1;
						    op = Operator.MODULUS;
						    break;
						case '\'':
						    // TODO: deal? with embedded quotes
						    for (len = 1; pos + len < endPos; len++) {
							if (input.charAt(pos + len) == '\'') {
							    len++;
							    break;
							}
						    }
						    tok = Token.SQSTRING;
						    break;
						case '"':
						    // TODO: deal? with embedded quotes
						    for (len = 1; pos + len < endPos; len++) {
							if (input.charAt(pos + len) == '"') {
							    len++;
							    break;
							}
						    }
						    tok = Token.DQSTRING;
						    break;
						default:
						    m = NOT_OP.matcher(seq);
						    if (m.lookingAt()) {
							len = m.end();
							op = Operator.NOTOP;
						    }
						    else {
							m = AND_OP.matcher(seq);
							if (m.lookingAt()) {
							    len = m.end();
							    op = Operator.ANDOP;
							}
							else {
							    m = OR_OP.matcher(seq);
							    if (m.lookingAt()) {
								len = m.end();
								op = Operator.OROP;
							    }
							    else {
								m = IDENT.matcher(seq);
								if (m.lookingAt()) {
								    len = m.end();
								    tok = Token.VARREF;
								    value = m.group(1);
								}
							    }
							}
						    }
						    break;
					    }
					}
				    }
				}
			    }
			}
		    }
		}
		if (len >= 0) {
		    // We found something that matches!
		    if (value == null)
			value = input.substring(pos, pos + len);
		    if (tok != Token.WHITESPACE) {
			TokenValue t = new TokenValue(tok, value, op);
			t.setPos(pos + startPos, len);
			tokens.add(t);
			if (superVerbose) {
			    out.format("token: %1$s, pos=%2$d, len=%3$d, value=\"%4$s\"%n",
				tok.toString(), pos, len, value);
			}
			t = null;
		    }
		    pos += len;
		}
		else {
		    throw new ParseException("Unrecognized input", pos);
		}
	    }
	    return tokens;
	}


	/**
	 * The tokenized input expression.
	 */
	private ArrayList<TokenValue> inputExpr = null;

	/**
	 * The current position in the tokenized input expression while moving
	 * through it to evaluate the expression.
	 */
	private int inputPos = -1;

	/**
	 * The size of the tokenized input (for quick comparisons).
	 */
	private int inputSize = 0;


	/**
	 * Evaluate the other kinds of factors.
	 *
	 * @param	type	The type of processing to use to evaluate expressions.
	 * @param	exprLen	The expected end of the expression.
	 * @return	The evaluated boolean result.
	 * @throws	ParseException if tokenizing went wrong.
	 */
	private boolean otherFactor(ProcessAs type, int exprLen)
		throws ParseException
	{
	    TokenValue t = inputExpr.get(inputPos++);
	    boolean v = false;
	    switch (t.tok) {
		case OTHERCONST:
		    v = Boolean.parseBoolean(t.value);
		    break;
		case DEFINEDFUNC:
		    String value = defines.get(t.value);
		    v = (value != null);
		    break;
		case OPENPAREN:
		    if (inputPos < inputSize) {
			v = orTerm(type, exprLen);
			if (inputPos < inputSize) {
			    TokenValue t2 = inputExpr.get(inputPos++);
			    if (t2.tok != Token.CLOSEPAREN)
				throw new ParseException("Expecting ')' after expression", t2.startPos);
			}
			else
			    throw new ParseException("Expecting ')' before end of expression", exprLen);
		    }
		    else
			throw new ParseException("Expecting an expression after '('", exprLen);
		    break;
		case VARREF:
		    handleVarRef(t);
		    v = otherFactor(type, exprLen);
		    break;
		case OPER:
		    if (t.op == Operator.NOTOP) {
			v = !otherFactor(type, exprLen);
			break;
		    }
		    // else fall through
		default:
		    inputPos--;
		    break;
	    }
	    return v;
	}

	/**
	 * Evaluate a string factor.
	 *
	 * @param	allowNumbers	Whether this is a numeric expression or a text expression.
	 * @param	exprLen		The length limit on the expression text.
	 * @param	eating		Whether or not we are in the "false" side of short-circuit evaluation.
	 *
	 * @return	The string result of the factor.
	 *
	 * @throws	NumberFormatException if numbers are allowed and one of them is malformed.
	 * @throws	ParseException for other kinds of syntax errors.
	 */
	private String stringFactor(boolean allowNumbers, int exprLen, boolean eating)
		throws NumberFormatException, ParseException
	{
	    String value = null;
	    TokenValue t= inputExpr.get(inputPos++);
	    switch (t.tok) {
		case SQSTRING:
		case DQSTRING:
		    value = doSubs(t.value);
		    break;
		case INTCONST:
		case FLTCONST:
		    if (allowNumbers)
			value = doSubs(t.value);
		    break;
		case VARREF:
		    handleVarRef(t);
		    value = stringFactor(allowNumbers, exprLen, eating);
		    break;
		case OPENPAREN:
		    if (inputPos < inputSize) {
			value = stringFactor(allowNumbers, exprLen, eating);
			if (inputPos < inputSize) {
			    TokenValue t2 = inputExpr.get(inputPos++);
			    if (t2.tok != Token.CLOSEPAREN)
				throw new NumberFormatException("Expecting ')' after stringFactor");
			}
			else
			    throw new NumberFormatException("Expecting ')' before end in stringFactor");
		    }
		    else
			throw new NumberFormatException("Expecting an expression after '('");
		    break;
		default:
		    inputPos--;
		    break;
	    }
	    return value;
	}

	/**
	 * Handle a macro reference in the token stream.
	 *
	 * @param	t	The input token.
	 *
	 * @throws	ParseException if the macro variable is not defined and
	 *		{@link #ignoreUndefined} is not {@code true}.
	 */
	private void handleVarRef(TokenValue t)
		throws ParseException
	{
	    String value;
	    if ((value = defines.get(t.value)) == null) {
		if (!ignoreUndefined) {
		    String s = String.format("Macro \"%1$s\" not defined!", t.value);
		    throw new ParseException(s, t.startPos);
		}
		else
		    value = "";
	    }
	    // Recursive call in common case that value
	    // is defined in terms of other macros
	    doSubs(value);
	    ArrayList<TokenValue> newTokens = tokenizeInput(value, t.startPos);

	    // Remove the VARREF token at inputPos and replace by new list
	    inputExpr.remove(--inputPos);
	    inputExpr.addAll(inputPos, newTokens);
	    inputSize = inputExpr.size();
	}

	/**
	 * Parse an integer value out of a token.
	 *
	 * @param	t	The input token.
	 * @param	type	How to process the evaluation.
	 * @param	exprLen	The length limit on the expression.
	 * @param	eating	Whether or not we are in the "false" state of short-circuit evaluation.
	 *
	 * @return	The double result.
	 *
	 * @throws	NumberFormatException if one of the numbers was not properly formed.
	 * @throws	ParseException if the expression was not properly formed
	 */
	private int integerValue(TokenValue t, ProcessAs type, int exprLen, boolean eating)
		throws NumberFormatException, ParseException
	{
	    int v = 0;
	    String value;
	    int sign = +1;
	    // Check for leading + or -
	    if (t.tok == Token.OPER) {
		if (t.op == Operator.ADD)
		    sign = +1;
		else if (t.op == Operator.SUBTRACT)
		    sign = -1;
		else
		    throw new NumberFormatException("Expecting only + or - here");
		if (inputPos < inputSize)
		    t = inputExpr.get(inputPos++);
		else
		    throw new ParseException("Expecting a number after the sign", exprLen);
	    }
	    if (eating) {
		// We only care about the high-level syntax, not whether numbers are good or not
		switch (t.tok) {
		    case OPENPAREN:
			if (inputPos < inputSize) {
			    v = integerTerm(type, exprLen, eating);
			    if (inputPos < inputSize) {
				TokenValue t2 = inputExpr.get(inputPos++);
				if (t2.tok != Token.CLOSEPAREN)
				    throw new NumberFormatException("Expecting ')' after integer term in integerValue");
			    }
			    else
				throw new NumberFormatException("Expecting ')' before end in integerValue");
			}
			else
			    throw new NumberFormatException("Expecting an expression after '('");
			break;
		}
	    }
	    else {
		switch (t.tok) {
		    case SQSTRING:
		    case DQSTRING:
			if (type == ProcessAs.NUMERIC || type == ProcessAs.NORMAL) {
			    value = stripQuotes(t.value);
			    v = Integer.parseInt(value);
			    break;
			}
			// else fall through
		    case INTCONST:
		    case FLTCONST:
			v = Integer.parseInt(t.value);
			break;
		    case VARREF:
			handleVarRef(t);
			v = integerTerm(type, exprLen, eating);
			break;
		    case OTHERCONST:
			v = Boolean.parseBoolean(t.value) ? 1 : 0;
			break;
		    case OPENPAREN:
			if (inputPos < inputSize) {
			    v = integerTerm(type, exprLen, eating);
			    if (inputPos < inputSize) {
				TokenValue t2 = inputExpr.get(inputPos++);
				if (t2.tok != Token.CLOSEPAREN)
				    throw new NumberFormatException("Expecting ')' after integer term in integerValue");
			    }
			    else
				throw new NumberFormatException("Expecting ')' before end in integerValue");
			}
			else
			    throw new NumberFormatException("Expecting an expression after '('");
			break;
		    default:
			throw new NumberFormatException("Not an integer");
		}
	    }
	    return v * sign;
	}

	/**
	 * Parse a double value out of a token.
	 *
	 * @param	t	The input token.
	 * @param	type	How to process the evaluation.
	 * @param	exprLen	The length limit on the expression.
	 * @param	eating	Whether or not we are in the "false" state of short-circuit evaluation.
	 *
	 * @return	The double result.
	 *
	 * @throws	NumberFormatException if one of the numbers was not properly formed.
	 * @throws	ParseException if the expression was not properly formed
	 */
	private double doubleValue(TokenValue t, ProcessAs type, int exprLen, boolean eating)
		throws NumberFormatException, ParseException
	{
	    double dv = 0.0;
	    String value;
	    double sign = +1.0;
	    // Check for leading + or -
	    if (t.tok == Token.OPER) {
		if (t.op == Operator.ADD)
		    sign = +1.0;
		else if (t.op == Operator.SUBTRACT)
		    sign = -1.0;
		else
		    throw new NumberFormatException("Expecting only + or - here");
		if (inputPos < inputSize)
		    t = inputExpr.get(inputPos++);
		else
		    throw new ParseException("Expecting a number after the sign", exprLen);
	    }
	    if (eating) {
		// We only care about the high-level syntax, not whether numbers are good or not
		switch (t.tok) {
		    case OPENPAREN:
			if (inputPos < inputSize) {
			    dv = doubleTerm(type, exprLen, eating);
			    if (inputPos < inputSize) {
				TokenValue t2 = inputExpr.get(inputPos++);
				if (t2.tok != Token.CLOSEPAREN)
				    throw new NumberFormatException("Expecting ')' after doubleTerm in doubleValue");
			    }
			    else
				throw new NumberFormatException("Expecting ')' before end in doubleValue");
			}
			else
			    throw new NumberFormatException("Expecting an expression after '('");
			break;
		}
	    }
	    else {
		switch (t.tok) {
		    case SQSTRING:
		    case DQSTRING:
			if (type == ProcessAs.NUMERIC || type == ProcessAs.NORMAL) {
			    String val = stripQuotes(t.value);
			    dv = Double.parseDouble(val);
			    break;
			}
			// else fall through
		    case INTCONST:
		    case FLTCONST:
			dv = Double.parseDouble(t.value);
			break;
		    case VARREF:
			handleVarRef(t);
			dv = doubleTerm(type, exprLen, eating);
			break;
		    case OTHERCONST:
			dv = Boolean.parseBoolean(t.value) ? 1.0 : 0.0;
			break;
		    case OPENPAREN:
			if (inputPos < inputSize) {
			    dv = doubleTerm(type, exprLen, eating);
			    if (inputPos < inputSize) {
				TokenValue t2 = inputExpr.get(inputPos++);
				if (t2.tok != Token.CLOSEPAREN)
				    throw new NumberFormatException("Expecting ')' after doubleTerm in doubleValue");
			    }
			    else
				throw new NumberFormatException("Expecting ')' before end in doubleValue");
			}
			else
			    throw new NumberFormatException("Expecting an expression after '('");
			break;
		    default:
			throw new NumberFormatException("Not a double");
		}
	    }
	    return dv * sign;
	}

	/**
	 * Evaluate an integer factor, which is <code>[+-] {int-value} [[+ -] [+-] {int-value}]*</code>
	 *
	 * @param	type	How to process the evaluation.
	 * @param	exprLen	The length limit on the expression.
	 * @param	eating	Whether or not we are in the "false" state of short-circuit evaluation.
	 *
	 * @return	The integer result of the expression.
	 *
	 * @throws	NumberFormatException if one of the numbers was not properly formed.
	 * @throws	ParseException if the expression was not properly formed
	 */
	private int integerFactor(ProcessAs type, int exprLen, boolean eating)
		throws NumberFormatException, ParseException
	{
	    TokenValue t = inputExpr.get(inputPos++);
	    int v = integerValue(t, type, exprLen, eating);
	    while (inputPos < inputSize) {
		TokenValue t2 = inputExpr.get(inputPos++);
		if (t2.tok == Token.OPER &&
		    (t2.op == Operator.ADD ||
		     t2.op == Operator.SUBTRACT)) {
		    if (inputPos < inputSize) {
			TokenValue t3 = inputExpr.get(inputPos++);
			int v2 = integerValue(t3, type, exprLen, eating);
			switch (t2.op) {
			    case ADD:
				v += v2;
				break;
			    case SUBTRACT:
				v -= v2;
				break;
			}
		    }
		    else {
			String s = String.format("Expecting an expression after %1$s", t2.value);
			throw new ParseException(s, exprLen);
		    }
		}
		else {
		    inputPos--;
		    break;      // Unknown operator, let caller deal with it
		}
	    }
	    return v;
	}

	/**
	 * Evaluate an integer term, which is <code>{int-factor} [[* / %] {int-factor}]*</code>
	 *
	 * @param	type	How to process the evaluation.
	 * @param	exprLen	The length limit on the expression.
	 * @param	eating	Whether or not we are in the "false" state of short-circuit evaluation.
	 *
	 * @return	The integer result of the expression.
	 *
	 * @throws	NumberFormatException if one of the numbers was not properly formed.
	 * @throws	ParseException if the expression was not properly formed
	 */
	private int integerTerm(ProcessAs type, int exprLen, boolean eating)
		throws NumberFormatException, ParseException
	{
	    int v = integerFactor(type, exprLen, eating);
	    while (inputPos < inputSize) {
		TokenValue t = inputExpr.get(inputPos++);
		if (t.tok == Token.OPER &&
		    (t.op == Operator.MULTIPLY ||
		     t.op == Operator.DIVIDE ||
		     t.op == Operator.MODULUS)) {
		    if (inputPos < inputSize) {
			int v2 = integerFactor(type, exprLen, eating);
			switch (t.op) {
			    case MULTIPLY:
				v *= v2;
				break;
			    case DIVIDE:
				if (v2 != 0) {
				    v /= v2;
				}
				else {
				    err.format("Error: Divide by zero!%n");
				    v = 0;
				}
				break;
			    case MODULUS:
				if (v2 != 0) {
				    v %= v2;
				}
				else {
				    err.format("Error: Modulus value of zero (equivalent to divide by zero)!%n");
				    v = 0;
				}
				break;
			}
		    }
		    else {
			String s = String.format("Expecting an expression after %1$s", t.value);
			throw new ParseException(s, exprLen);
		    }
		}
		else {
		    inputPos--;
		    break;  // Unknown operator, let caller decide what to do
		}
	    }
	    return v;
	}

	/**
	 * Evaluate a double factor, which is <code>[+-] {operand} [[+ -] [+-] {operand}]*</code>
	 *
	 * @param	type	How to process the evaluation.
	 * @param	exprLen	The length limit on the expression.
	 * @param	eating	Whether or not we are in the "false" state of short-circuit evaluation.
	 *
	 * @return	The double result of the expression.
	 *
	 * @throws	NumberFormatException if one of the numbers was not properly formed.
	 * @throws	ParseException if the expression was not properly formed
	 */
	private double doubleFactor(ProcessAs type, int exprLen, boolean eating)
		throws NumberFormatException, ParseException
	{
	    TokenValue t = inputExpr.get(inputPos++);
	    double dv = doubleValue(t, type, exprLen, eating);
	    while (inputPos < inputSize) {
		TokenValue t2 = inputExpr.get(inputPos++);
		if (t2.tok == Token.OPER &&
		    (t2.op == Operator.ADD ||
		     t2.op == Operator.SUBTRACT)) {
		    if (inputPos < inputSize) {
			TokenValue t3 = inputExpr.get(inputPos++);
			double dv2 = doubleValue(t3, type, exprLen, eating);
			switch (t2.op) {
			    case ADD:
				dv += dv2;
				break;
			    case SUBTRACT:
				dv -= dv2;
				break;
			}
		    }
		    else {
			String s = String.format("Expecting an expression after %1$s", t2.value);
			throw new ParseException(s, exprLen);
		    }
		}
		else {
		    inputPos--;
		    break;      // Unknown operator, let caller deal with it
		}
	    }
	    return dv;
	}

	/**
	 * Evaluate a double term, which is <code>{double-factor} [[* / %] {double-factor}]*</code>
	 *
	 * @param	type	How to process the evaluation.
	 * @param	exprLen	The length limit on the expression.
	 * @param	eating	Whether or not we are in the "false" state of short-circuit evaluation.
	 *
	 * @return	The double result of the expression.
	 *
	 * @throws	NumberFormatException if one of the numbers was not properly formed.
	 * @throws	ParseException if the expression was not properly formed
	 */
	private double doubleTerm(ProcessAs type, int exprLen, boolean eating)
		throws NumberFormatException, ParseException
	{
	    double dv = doubleFactor(type, exprLen, eating);
	    while (inputPos < inputSize) {
		TokenValue t = inputExpr.get(inputPos++);
		if (t.tok == Token.OPER &&
		    (t.op == Operator.MULTIPLY ||
		     t.op == Operator.DIVIDE ||
		     t.op == Operator.MODULUS)) {
		    if (inputPos < inputSize) {
			double dv2 = doubleFactor(type, exprLen, eating);
			switch (t.op) {
			    case MULTIPLY:
				dv *= dv2;
				break;
			    case DIVIDE:
				if (dv2 != 0.0) {
				    dv /= dv2;
				}
				else {
				    err.format("Error: Divide by zero!%n");
				    dv = 0.0;
				}
				break;
			    case MODULUS:
				if (dv2 != 0.0) {
				    dv %= dv2;
				}
				else {
				    err.format("Error: Modulus value of zero (equivalent to divide by zero)!%n");
				    dv = 0.0;
				}
				break;
			}
		    }
		    else {
			String s = String.format("Expecting an expression after %1$s", t.value);
			throw new ParseException(s, exprLen);
		    }
		}
		else {
		    inputPos--;
		    break;  // Unknown operator, let caller decide what to do
		}
	    }
	    return dv;
	}

	/**
	 * Evaluate a relational expression, which is <code>{term} {rel-op} {term}</code>
	 *
	 * @param	type	How to process the evaluation.
	 * @param	exprLen	The length limit on the expression.
	 * @param	eating	Whether or not we are in the "false" state of short-circuit evaluation.
	 *
	 * @return	The boolean result of the expression.
	 *
	 * @throws	ParseException if the expression was not properly formed
	 */
	private boolean relTerm(ProcessAs type, int exprLen, boolean eating)
		throws ParseException
	{
	    int savePos = inputPos;
	    if (type == ProcessAs.NUMERIC || type == ProcessAs.NORMAL) {
		try {
		    int v = integerTerm(type, exprLen, eating);
		    if (inputPos < inputSize) {
			TokenValue t = inputExpr.get(inputPos++);
			if (t.tok == Token.OPER &&
			    (t.op == Operator.EQUAL ||
			     t.op == Operator.NOTEQUAL ||
			     t.op == Operator.LESS ||
			     t.op == Operator.LESSEQUAL ||
			     t.op == Operator.GREATER ||
			     t.op == Operator.GREATEREQUAL)) {
			    int v2 = integerTerm(type, exprLen, eating);
			    switch (t.op) {
				case EQUAL:
				    return v == v2;
				case NOTEQUAL:
				    return v != v2;
				case LESS:
				    return v < v2;
				case LESSEQUAL:
				    return v <= v2;
				case GREATER:
				    return v > v2;
				case GREATEREQUAL:
				    return v >= v2;
			    }
			}
			else {
			    // Unknown operator, let caller deal with it
			    inputPos--;
			    return (v != 0);
			}
		    }
		    else {
			return (v != 0);
		    }
		}
		catch (NumberFormatException infe) {
		    // Not an integer, rollback the input position and try a double
		    inputPos = savePos;
		    try {
			double dv = doubleTerm(type, exprLen, eating);
			if (inputPos < inputSize) {
			    TokenValue t = inputExpr.get(inputPos++);
			    if (t.tok == Token.OPER &&
				(t.op == Operator.EQUAL ||
				 t.op == Operator.NOTEQUAL ||
				 t.op == Operator.LESS ||
				 t.op == Operator.LESSEQUAL ||
				 t.op == Operator.GREATER ||
				 t.op == Operator.GREATEREQUAL)) {
				double dv2 = doubleTerm(type, exprLen, eating);
				switch (t.op) {
				    case EQUAL:
					return dv == dv2;
				    case NOTEQUAL:
					return dv != dv2;
				    case LESS:
					return dv < dv2;
				    case LESSEQUAL:
					return dv <= dv2;
				    case GREATER:
					return dv > dv2;
				    case GREATEREQUAL:
					return dv >= dv2;
				}
			    }
			    else {
				// Unknown operator, let caller deal with it
				inputPos--;
				return (dv != 0.0);
			    }
			}
			else {
			    return (dv != 0.0);
			}
		    }
		    catch (NumberFormatException dnfe) {
			// Not a double either, drop out to string compare
		    }
		}
	    }
	    inputPos = savePos;
	    try {
		String sv = stringFactor(false, exprLen, eating);
		if (sv != null) {
		    if (type == ProcessAs.NUMERIC) {
			TokenValue t = inputExpr.get(inputPos-1);
			throw new ParseException("#ifnum requires numeric values", t.startPos);
		    }
		    if (inputPos < inputSize) {
			TokenValue t = inputExpr.get(inputPos++);
			if (t.tok == Token.OPER &&
			    (t.op == Operator.EQUAL ||
			     t.op == Operator.NOTEQUAL ||
			     t.op == Operator.LESS ||
			     t.op == Operator.LESSEQUAL ||
			     t.op == Operator.GREATER ||
			     t.op == Operator.GREATEREQUAL)) {
			    sv = stripQuotes(sv);
			    String sv2 = stripQuotes(stringFactor(true, exprLen, eating));
			    int cmp;
			    if (type == ProcessAs.STRINGINSENSITIVE)
				cmp = sv.compareToIgnoreCase(sv2);
			    else
				cmp = sv.compareTo(sv2);
			    switch (t.op) {
				case EQUAL:
				    return cmp == 0;
				case NOTEQUAL:
				    return cmp != 0;
				case LESS:
				    return cmp < 0;
				case LESSEQUAL:
				    return cmp <= 0;
				case GREATER:
				    return cmp > 0;
				case GREATEREQUAL:
				    return cmp >= 0;
			    }
			}
			else {
			    throw new ParseException("Expecting a relational operator with a string", exprLen);
			}
		    }
		    else {
			throw new ParseException("Expecting a relational operator with a string", exprLen);
		    }
		}
	    }
	    catch (NumberFormatException nfes) {
		// This means that the nested parens didn't result in a string value
		// so default to "otherFactor" to deal with
	    }
	    // When all else fails, try the last set of factors
	    inputPos = savePos;
	    return otherFactor(type, exprLen);
	}

	/**
	 * Evaluate an "and" term expression, which is <code>{rel-term} [&amp;&amp; AND] {rel-term}</code>
	 * <p> Does "short-circuit" evaluation of the expression, so that if the
	 * first <code>{and-term}</code> evaluates to {@code false} then we return right away.
	 *
	 * @param	type	How to process the evaluation.
	 * @param	exprLen	The length limit on the expression.
	 * @param	eating	Whether or not we are in the "false" state of short-circuit evaluation.
	 *
	 * @return	The boolean result of the expression.
	 *
	 * @throws	ParseException if the expression was not properly formed
	 *		according to our rules.
	 */
	private boolean andTerm(ProcessAs type, int exprLen, boolean eating)
		throws ParseException
	{
	    boolean v = relTerm(type, exprLen, eating);
	    while (inputPos < inputSize) {
		TokenValue t = inputExpr.get(inputPos++);
		if (t.tok == Token.OPER && t.op == Operator.ANDOP) {
		    if (inputPos < inputSize) {
			// Do short-circuit evaluation:
			// Stop evaluating as soon as we get 'false' result
			if (!v || eating)
			    relTerm(type, exprLen, true);   // ignore return, leave v=false
			else
			    v = relTerm(type, exprLen, false);
		    }
		    else {
			String s = String.format("Expecting an expression after %1$s", t.value);
			throw new ParseException(s, exprLen);
		    }
		}
		else {
		    inputPos--;
		    break;  // Unknown operator, let caller decide what to do
		}
	    }
	    return v;
	}

	/**
	 * Evaluate an "or" term expression, which is <code>{and-term} [|| OR] {and-term}</code>
	 * <p> Does "short-circuit" evaluation of the expression, so that if the
	 * first <code>{and-term}</code> evaluates to {@code true} then we return right away.
	 *
	 * @param	type	How to process the evaluation.
	 * @param	exprLen	The length limit on the expression.
	 *
	 * @return	The boolean result of the expression.
	 *
	 * @throws	ParseException if the expression was not properly formed
	 *		according to our rules.
	 */
	private boolean orTerm(ProcessAs type, int exprLen)
		throws ParseException
	{
	    boolean v = andTerm(type, exprLen, false);
	    while (inputPos < inputSize) {
		TokenValue t = inputExpr.get(inputPos++);
		if (t.tok == Token.OPER && t.op == Operator.OROP) {
		    if (inputPos < inputSize) {
			// Do short-circuit evaluation:
			// Stop evaluating as soon as we get 'true' result
			if (v)
			    andTerm(type, exprLen, true);
			else
			    v = andTerm(type, exprLen, false);
		    }
		    else {
			String s = String.format("Expecting an expression after %1$s", t.value);
			throw new ParseException(s, exprLen);
		    }
		}
		else {
		    inputPos--;
		    break;  // Unknown operator, let caller decide what to do
		}
	    }
	    return v;
	}

	/**
	 * Evaluate an expression given a tokenized input stream (as a ListIterator)
	 * and return the boolean result.
	 *
	 * @param	type	How to process the evaluation.
	 * @param	exprLen	The length limit on the expression.
	 *
	 * @return	The boolean result of the expression.
	 *
	 * @throws	ParseException if the expression was not properly formed
	 *		according to our rules.
	 */
	private boolean evaluate(ProcessAs type, int exprLen)
		throws ParseException
	{
	    boolean v = orTerm(type, exprLen);
	    if (inputPos < inputSize) {
		TokenValue t = inputExpr.get(inputPos++);
		throw new ParseException("Not expecting anything more", t.startPos);
	    }
	    return v;
	}

	/**
	 * Evaluate an expression and return boolean result.
	 *
	 * @param	expr	The expression string to evaluate.
	 * @param	type	How to process the evaluation.
	 *
	 * @return	The boolean result of the expression.
	 *
	 * @throws	ParseException if the expression was not properly formed
	 *		according to our rules.
	 */
	private boolean evaluate(String expr, ProcessAs type)
		throws ParseException
	{
	    // First, tokenize the input stream
	    inputExpr = tokenizeInput(expr,  0);

	    // Setup the parsing variables
	    inputPos = 0;
	    inputSize = inputExpr.size();

	    // Now evaluate the input expression using recursive descent parser
	    return evaluate(type, expr.length());
	}


	/**
	 * Output a tracing version of the current line with line number and status char.
	 *
	 * @param	lineNo	current line number in source file
	 * @param	line	input line
	 * @param	directive	<code>true</code> if this is a #directive line (always output)
	 *				or <code>false</code> meaning only output this trace if
	 *				doing output and {@link #plusVerbose} mode.
	 * @param	doingOutput	<code>true</code> if we're doing output right now
	 */
	private void traceLine(long lineNo, String line, boolean directive, boolean doingOutput) {
	    if (verbose) {
		if (plusVerbose && doingOutput)
		    out.format("%1$8d.+%2$s%n", lineNo, line);
		else if (directive)
		    out.format("%1$8d. %2$s%n", lineNo, line);
	    }
	}


	/**
	 * Process one input file.
	 * <p> This is where the actual processing of the preprocessing directives
	 * takes place.  The input file is read, directives are evaluated,
	 * lines that are to be emitted are processed and macro substitutions
	 * are made, then finally the output file is written.
	 * <p>This routine is called recursively for an included file, which
	 * is why the output writer is passed as a parameter.
	 * <p>Sets and resets the {@link #FILE_VAR_NAME} variable to this file name.
	 * <p>At the end of the file, any errors in properly nesting <code>#if<i>xxx</i></code>, <code>#else</code>
	 * and <code>#endif</code> blocks will be reported.  This means that it is not proper to
	 * begin a block in one file and end it in another -- each file must contain
	 * a complete block.
	 * <p>If this is a top-level file (i.e., not an included one), then processing
	 * can be skipped altogether if the output file exists and is newer than the input
	 * (unless the "-A" [always process] flag is given on the command line).
	 *
	 * @param	inputFile	the input file to be read and processed
	 * @param	wrtr		<code>null</code> if this is a top-level file
	 *				in which case the {@link BufferedWriter}
	 *				is created based on the input file name and
	 *				output extension.  If non-null, this means
	 *				we have been called recursively, so no new
	 *				output file is created, but the results of
	 *				processing the input file are simply appended
	 *				to what has already been generated.
	 * @return	<code>false</code> if no errors (that is, success)
	 *		<code>true</code> if errors (that is, failure)
	 */
	private boolean processFile(File inputFile, BufferedWriter wrtr) {
	    boolean errors = false;
	    int nesting = 0;
	    int tooManyEndifErrors = 0;
	    boolean doingOutput = true;
	    boolean closeOutput = false;
	    String name = inputFile.getPath();
	    LinkedList<IfState> state = new LinkedList<IfState>();
	    String previousFileName = defines.put(FILE_VAR_NAME, name);

	    try {
		String fileType = "";
		BufferedReader rdr = null;
		if (processAsUTF8) {
		    rdr = Files.newBufferedReader(inputFile.toPath(), StandardCharsets.UTF_8);
		    fileType = " (UTF-8)";
		}
		else {
		    rdr = Files.newBufferedReader(inputFile.toPath());
		}

		// Construct output file stream if necessary (not if processing #include)
		if (wrtr == null) {
		    String outName = name.replace(inputExt, outputExt);
		    if (outName.equals(name)) {
			err.format("Error: Output file name must not be the same as input file name: '%1$s'!%n", name);
			rdr.close();
			errors = true;
			throw new DontProcessException();
		    }
		    if (!alwaysProcess) {
			// Check time stamps of input and output files
			// and skip if output exists and is newer than input
			File inFile = new File(name);
			File outFile = new File(outName);
			long inTime = inFile.lastModified();
			long outTime = outFile.lastModified();
			if (outTime > inTime) {
			    if (verbose)
				out.format("Skipping file because output '%1$s'%n         is newer than input '%2$s'.%n", outName, name);
			    rdr.close();
			    throw new DontProcessException();
			}
		    }
		    if (verbose) {
			out.format("Generating output file '%1$s'%2$s%n       from input file '%3$s'...%n", outName, fileType, name);
		    }
		    if (processAsUTF8) {
			wrtr = Files.newBufferedWriter(Paths.get(outName), StandardCharsets.UTF_8);
		    }
		    else {
			wrtr = Files.newBufferedWriter(Paths.get(outName));
		    }
		    closeOutput = true;
		}
		else {
		    if (verbose) {
			out.format("Including file '%1$s'%2$s...%n", name, fileType);
		    }
		}

		boolean firstDirective = true;
		String line = null;
		long lineNo = 0;
		while ((line = rdr.readLine()) != null) {
		    lineNo++;
		    boolean isDirective = false;
		    Matcher m1 = commentPat.matcher(line);
		    if (m1.matches())
			continue;
		    Matcher m2 = passPat.matcher(line);
		    if (m2.matches()) {
			line = m2.group(1);
		    }
		    else {
			Matcher m = cmdPat.matcher(line);
			if (m.matches()) {
			    isDirective = true;
			    String directive = m.group(1);
			    String args = m.group(2);
			    int argOffset = m.end(1);
			    if (verbose) {
				if (firstDirective) {
				    out.format("  Line     Directive%n--------- --------------------------------------%n");
				    firstDirective = false;
				}
			    }
			    defines.put(LINE_VAR_NAME, String.format("%1$d", lineNo));

			    boolean ifStmt = false;
			    boolean exprResult = false;
			    IfType ift = IfType.IF_STMT;
			    try {
				if (directive.equalsIgnoreCase("if")) {
				    traceLine(lineNo, line, true, false);
				    ifStmt = true;
				    exprResult = evaluate(args, ProcessAs.NORMAL);
				}
				else if (directive.equalsIgnoreCase("ifnum")) {
				    traceLine(lineNo, line, true, false);
				    ifStmt = true;
				    exprResult = evaluate(args, ProcessAs.NUMERIC);
				}
				else if (directive.equalsIgnoreCase("ifstr")) {
				    traceLine(lineNo, line, true, false);
				    ifStmt = true;
				    exprResult = evaluate(args, ProcessAs.FORCESTRING);
				}
				else if (directive.equalsIgnoreCase("ifistr")) {
				    traceLine(lineNo, line, true, false);
				    ifStmt = true;
				    exprResult = evaluate(args, ProcessAs.STRINGINSENSITIVE);
				}
				else if (directive.equalsIgnoreCase("ifdef")) {
				    traceLine(lineNo, line, true, false);
				    ifStmt = true;
				    args = doSubs(args.trim());
				    exprResult = defines.containsKey(args);
				}
				else if (directive.equalsIgnoreCase("ifndef")) {
				    traceLine(lineNo, line, true, false);
				    ifStmt = true;
				    args = doSubs(args.trim());
				    exprResult = !defines.containsKey(args);
				}
				else if (directive.equalsIgnoreCase("else")) {
				    traceLine(lineNo, line, true, false);
				    if (nesting == 0) {
					err.format("Error: Line %1$d. #else without preceding #if!%n", lineNo);
					errors = true;
				    }
				    else {
					if (state.peekLast().doingOutput)
					    doingOutput = !doingOutput;
				    }
				}
				else if (directive.equalsIgnoreCase("elif") ||
					 directive.equalsIgnoreCase("elseif")) {
				    traceLine(lineNo, line, true, false);
				    if (nesting == 0) {
					err.format("Error: Line %1$d. #%2$s without preceding #if!%n", lineNo, directive);
					errors = true;
				    }
				    else {
					// Do combination of "else" and "if" processing here
					if (state.peekLast().doingOutput)
						doingOutput = !doingOutput;
					ifStmt = true;
					ift = IfType.ELIF_STMT;
					exprResult = evaluate(args, ProcessAs.NORMAL);
				    }
				}
				else if (directive.equalsIgnoreCase("endif")) {
				    traceLine(lineNo, line, true, false);
				    if (nesting <= 0) {
					err.format("Error: Line %1$d. #endif without preceding #if!%n", lineNo);
					errors = true;
					tooManyEndifErrors++;
				    }
				    else {
					// Pop all the nested #elif states to get to the original #if
					while (nesting > 0 && state.peekLast().lastStmt == IfType.ELIF_STMT) {
					    state.removeLast();
					    --nesting;
					}
					// Finally, do the real thing
					doingOutput = state.removeLast().doingOutput;
					--nesting;
				    }
				}
				else if (directive.equalsIgnoreCase("define")) {
				    traceLine(lineNo, line, true, doingOutput);
				    if (doingOutput) {
					args = doSubs(args.trim());
					Matcher md = def2Pat.matcher(args);
					if (md.matches()) {
					    String var = md.group(1);
					    String val = md.group(2);
					    defines.put(var, val);
					    if (superVerbose) {
						out.format("Defining '%1$s' to '%2$s'%n", var, val);
					    }
					}
					else {
					    md = def3Pat.matcher(args);
					    if (md.matches()) {
						// "#define ABC" puts empty string in as value
						String var = md.group(1);
						defines.put(var, "");
						if (superVerbose) {
						    out.format("Defining '%1$s'%n", var);
						}
					    }
					    else {
						err.format("Error: Line %1$d. Wrong syntax for '#define': %2$s%n\tformat should be: #define var value or simply #define var%n", lineNo, args);
						errors = true;
					    }
					}
				    }
				}
				else if (directive.equalsIgnoreCase("undef")) {
				    traceLine(lineNo, line, true, doingOutput);
				    if (doingOutput) {
					args = doSubs(args.trim());
					if (defines.containsKey(args)) {
					    defines.remove(args);
					    if (superVerbose) {
						out.format("Undefining '%1$s'%n", args);
					    }
					}
					else {
					    if (!ignoreUndefined) {
						err.format("Error: Line %1$d. Trying to undefine variable '%2$s' which is not defined!%n", lineNo, args);
						errors = true;
					    }
					}
				    }
				}
				else if (directive.equalsIgnoreCase("include")) {
				    traceLine(lineNo, line, true, doingOutput);
				    if (doingOutput) {
					args = doSubs(args.trim());
					// Strip quotes, brackets, etc.
					if (processIncludeFile(stripBrackets(args), wrtr))
					    errors = true;
				    }
				}
				else if (directive.equalsIgnoreCase("error")) {
				    traceLine(lineNo, line, true, doingOutput);
				    if (doingOutput) {
					args = args.trim();
					err.format("Error: %1$s%n", args);
					errors = true;
				    }
				}
				else {
				    traceLine(lineNo, line, true, false);
				    err.format("Error: Line %1$d. Unknown directive: '%2$s'%n", lineNo, directive);
				    errors = true;
				}
			    }
			    catch (ParseException pe) {
				if (!verbose) {
				    out.format("%n%1$8d. %2$s%n", lineNo, line);
				}
				StringBuilder buf = new StringBuilder();
				int sz = pe.getErrorOffset() + argOffset + 10;
				char[] blanks = new char[sz];
				Arrays.fill(blanks, ' ');
				buf.append(blanks);
				buf.append("^");
				err.format("%1$s%nError in expression: %2$s%n",
					buf.toString(), pe.getMessage());
				errors = true;
				exprResult = false;
			    }
			    if (ifStmt) {
				state.add(new IfState(doingOutput, ift));
				nesting++;
				if (doingOutput)
				    doingOutput = exprResult;
			    }
			}
		    }
		    if (!isDirective) {
			if (doingOutput) {
			    if (!line.isEmpty()) {
				defines.put(LINE_VAR_NAME, String.format("%1$d", lineNo));
				line = doSubs(line);
				wrtr.write(line, 0, line.length());
			    }
			    if (nesting > 0)
				traceLine(lineNo, line, false, doingOutput);
			    wrtr.newLine();
			}
		    }
		}
		if (nesting > 0) {
		    if (nesting == 1) {
			err.format("Error: Missing one '#endif' before the end of file \"%1$s\"!%n", name);
		    }
		    else {
			err.format("Error: Missing %1$d '#endif' statements before the end of file \"%2$s\"!%n", nesting, name);
		    }
		    errors = true;
		}
		if (tooManyEndifErrors > 0) {
		    if (tooManyEndifErrors == 1) {
			err.format("Error: One too many '#endif' statements before the end of file \"%1$s\"!%n", name);
		    }
		    else {
			err.format("Error: %1$d too many '#endif' statements before the end of file \"%2$s\"!%n", tooManyEndifErrors, name);
		    }
		    errors = true;
		}
		rdr.close();
		if (closeOutput) {
		    wrtr.flush();
		    wrtr.close();
		    wrtr = null;
		}
	    }
	    catch (IOException ioe) {
		err.format("Error: I/O error occurred while processing file '%1$s'!%n\t%2$s%n", name, exceptMessage(ioe));
		errors = true;
	    }
	    catch (DontProcessException dpe) {
		// This is a graceful exit, don't do anything
	    }
	    defines.put(FILE_VAR_NAME, previousFileName);
	    return errors;
	}


	/**
	 * Try to process one file, given {@link String} name of the file.
	 *
	 * @param	arg	file to process
	 * @param	wrtr	the output file we are generating
	 * @return	<code>false</code> if not errors (that is, success)
	 *		<code>true</code> if errors (that is, failure)
	 * @throws	FileNotFoundException for the obvious reason.
	 */
	private boolean processOneFile(String arg, BufferedWriter wrtr)
		throws FileNotFoundException
	{
	    return processOneFile(new File(arg), wrtr);
	}


	/**
	 * Try to process one file (check existence, etc.).
	 *
	 * @param	f	file to process
	 * @param	wrtr	the output file we are generating
	 * @return	<code>false</code> if not errors (that is, success)
	 *		<code>true</code> if errors (that is, failure)
	 * @throws	FileNotFoundException for the obvious reason.
	 */
	private boolean processOneFile(File f, BufferedWriter wrtr)
		throws FileNotFoundException
	{
	    boolean err = false;
	    if (f.exists() && f.isFile()) {
		if (processFile(f, wrtr)) {
		    err = true;
		}
	    }
	    else {
		// Try with the inputExt appended
		String name = f.getPath();
		File f2 = new File(name + inputExt);
		if (f2.exists() && f2.isFile()) {
		    if (processFile(f2, wrtr)) {
			    err = true;
		    }
		}
		else {
		    String msg = String.format("Cannot find file '%1$s'!", name);
		    throw new FileNotFoundException(msg);
		}
	    }
	    return err;
	}


	/**
	 * Process an included file.  This differs from regular processing
	 * in that the -P paths and the INCLUDE environment variable are used
	 * to search for the file if not found in the current directory, or by the file's
	 * absolute path.
	 *
	 * @param	arg	Full or partial path name of the file to include.
	 * @param	wrtr	The output writer.
	 *
	 * @return	<code>false</code> if not errors (that is, success)
	 *		<code>true</code> if errors (that is, failure)
	 *		most notably if the file cannot be found anywhere
	 */
	private boolean processIncludeFile(String arg, BufferedWriter wrtr) {
	    try {
		return processOneFile(arg, wrtr);
	    }
	    catch (FileNotFoundException fnfe) {
		// Try any specifically defined include paths first
		if (includePaths != null) {
		    for (String p1: includePaths) {
			File f = new File(p1, arg);
			try {
			    return processOneFile(f, wrtr);
			}
			catch (FileNotFoundException fnfe1) {
			    continue;
			}
		    }
		}

		// Okay, not found there, try using INCLUDE (or other -E variable)
		String env = System.getenv(inclEnvVar);
		if (env != null && !env.isEmpty()) {
		    String[] paths = comma.split(env);
		    for (String p : paths) {
			File f = new File(p, arg);
			try {
			    return processOneFile(f, wrtr);
			}
			catch (FileNotFoundException fnfe2) {
			    continue;
			}
		    }
		}
	    }
	    // Tried everything but could not find the file
	    err.format("Error: Unable to find include file \"%1$s\"%n", arg);
	    return true;
	}


	/**
	 * Decide if the string is an option specifier or
	 * an ordinary file name (some OS dependencies in here).
	 *
	 * @param	arg	The complete argument value.
	 *
	 * @return	{@code true} if the value is a valid option specifier
	 *		for the current platform.
	 */
	private static boolean isOptionString(String arg) {
	    if (System.getProperty("os.name").startsWith("Windows")) {
		return (arg.startsWith("-") || arg.startsWith("/"));
	    }
	    else {
		return arg.startsWith("-");
	    }
	}


	/**
	 * Set the default input and output extension values.
	 */
	private void setDefaultExtensions() {
	    if (outputExt != null && inputExt == null)
		inputExt = String.format("%1$spp", outputExt);
	    else if (inputExt != null && outputExt == null)
		outputExt = inputExt.replaceFirst("pp$", "");
	    else if (inputExt == null && outputExt == null) {
		outputExt = defaultOutputExt;
		inputExt = defaultInputExt;
	    }
	}


	/**
	 * Process all file specs on the command line.
	 * <p>Note: for now, these must be exact file names (no wildcards).
	 *
	 * @param	args	The complete list of command line arguments, some of which
	 *			are file names.
	 *
	 * @throws	BuildException if there were errors.
	 */
	private void processFileSpecs(Vector<String> args) throws BuildException {
	    for (String arg: args) {
		if (isOptionString(arg))
		    continue;

		// Set input and output extensions if not overridden on command line
		String lastInputExt = inputExt;
		String lastOutputExt = outputExt;
		if (inputExt == null) {
		    int idx = arg.lastIndexOf('.');
		    if (idx >= 0)
			inputExt = arg.substring(idx);
		}
		setDefaultExtensions();

		try {
		    if (processOneFile(arg, null)) {
			break;
		    }
		}
		catch (FileNotFoundException fnfe) {
		    throw new BuildException(exceptMessage(fnfe));
		}

		inputExt = lastInputExt;
		outputExt = lastOutputExt;
	    }
	}


	/**
	 * Process one directory (possibly recursively).
	 *
	 * @param	f	The directory to process.
	 *
	 * @return	{@code true} if there were errors.
	 */
	private boolean processDir(File f) {
	    boolean errors = false;
	    if (f.exists() && f.isDirectory()) {
		InputFileFilter filt = new InputFileFilter();
		for (File f1 : f.listFiles(filt)) {
		    if (processFile(f1, null)) {
			errors = true;
			break;
		    }
		}
		if (!errors && recurseDirectories) {
		    InputDirFilter filt2 = new InputDirFilter();
		    for (File f2 : f.listFiles(filt2)) {
			if (processDir(f2)) {
			    errors = true;
			    break;
			}
		    }
		}
	    }
	    return errors;
	}


	/**
	 * Process all directory specs on the command line.
	 * <p>Note: for now, these must be exact directory names.
	 *
	 * @param	args	The list of arguments, some of which are directories.
	 */
	private void processDirSpecs(Vector<String> args) {
	    for (String arg: args) {
		if (isOptionString(arg))
		    continue;
		if (processDir(new File(arg))) {
		    break;
		}
	    }
	}


	/**
	 * Display Sign-on Banner.
	 *
	 * @param	display	Flag to say whether or not to really display it.
	 */
	private void signOnBanner(boolean display) {
	    if (display)
		out.format("Java Pre-Processor -- version %1$s%nCopyright (c) %2$s Roger L. Whitcomb.%n", VERSION, COPYRIGHT_YEAR);
	}


	/**
	 * Display rudimentary help screen.
	 */
	private void usage() {
	    out.format("%nFor usage instructions, see the generated Javadoc%nin the package \"info.rlwhitcomb.preproc\".%n%n");
	}


	/**
	 * Process command line options.
	 *
	 * @param	inst	The preprocessor instance.
	 * @param	args	The command line arguments.
	 *
	 * @return	{@code true} to just quit without further processing (as for "-help") or
	 *		{@code false} to continue with regular processing.
	 *
	 * @throws	BuildException for errors parsing these arguments.
	 */
	private static boolean processCommandLine(JavaPreProc inst, String[] args) throws BuildException {
	    // Process the command-line switches
	    for (String arg: args) {
		if (isOptionString(arg)) {
		    arg = arg.substring(1);
		    if (arg.startsWith("C") || arg.startsWith("c")) {
			inst.setDirectiveChar(arg.substring(1));
		    }
		    else if (arg.startsWith("D") || arg.startsWith("d")) {
			inst.setDefine(arg.substring(1));
		    }
		    else if (arg.startsWith("U") || arg.startsWith("u")) {
			inst.setUndefine(arg.substring(1));
		    }
		    else if (arg.startsWith("O") || arg.startsWith("o")) {
			inst.setOutputExt(arg.substring(1));
		    }
		    else if (arg.startsWith("I") || arg.startsWith("i")) {
			inst.setInputExt(arg.substring(1));
		    }
		    else if (arg.startsWith("P") || arg.startsWith("p")) {
			inst.setIncludePath(arg.substring(1));
		    }
		    else if (arg.equalsIgnoreCase("X")) {
			inst.setIgnoreUndefined(true);
		    }
		    else if (arg.startsWith("F") || arg.startsWith("f")) {
			String value = arg.substring(1);
			if (value.startsWith(":"))
			    value = value.substring(1);
			inst.setFormat(value);
		    }
		    else if (arg.startsWith("L") || arg.startsWith("l")) {
			String value = arg.substring(1);
			if (value.startsWith(":"))
			    value = value.substring(1);
			inst.setLog(value);
		    }
		    else if (arg.equalsIgnoreCase("W")) {
			inst.setOverwrite(true);
		    }
		    else if (arg.startsWith("V") || arg.startsWith("v")) {
			inst.setVerbose(arg.substring(1));
		    }
		    else if (arg.equals("r")) {
			inst.setProcessAsDirectory(true);
		    }
		    else if (arg.equals("R")) {
			inst.setRecurseDirectories(true);
		    }
		    else if (arg.equalsIgnoreCase("A")) {
			inst.setAlwaysProcess(true);
		    }
		    else if (arg.startsWith("E") || arg.startsWith("e")) {
			inst.setIncludeVar(arg.substring(1));
		    }
		    else if (arg.equalsIgnoreCase("nologo")) {
			inst.setNologo(true);
		    }
		    else if (arg.equals("?") || arg.equalsIgnoreCase("help")) {
			inst.signOnBanner(true);
			inst.usage();
			return true;    // Just to quit without doing any processing
		    }
		    else {
			throw new BuildException(String.format("Unknown option: '%1$s'", arg));
		    }
		}
	    }
	    return false;
	}


	/**
	 * Set value for <code>directiveChar</code> option.
	 *
	 * @param	ch	The new value for the option.
	 * @throws	BuildException if the value is more than one character.
	 */
	public void setDirectiveChar(String ch) throws BuildException {
	    if (ch.length() != 1) {
		throw new BuildException("Directive indicator must be a single character.");
	    }
	    else {
		directiveStartCh = ch.charAt(0);
	    }
	}


	/**
	 * Set value for the <code>define</code> option.
	 * <p> Multiple values can be specified using comma or semicolon
	 * delimiters.
	 *
	 * @param	def	The new define specification ({@code "var=value"}).
	 * @throws	BuildException if the specification can't be parsed.
	 */
	public void setDefine(String def) throws BuildException {
	    if (def == null || def.isEmpty())
		return;
	    String[] defs = comma.split(def);
	    for (String d : defs) {
		Matcher m1 = defPat.matcher(d);
		if (m1.matches()) {
		    String var = m1.group(1);
		    String val = m1.group(2);
		    defines.put(var, val);
		    if (plusVerbose)
			out.format("Defining '%1$s' to '%2$s'%n", var, val);
		}
		else {
		    m1 = defAltPat.matcher(d);
		    if (m1.matches()) {
			String var = m1.group(1);
			defines.put(var, "");
			if (plusVerbose)
			    out.format("Defining '%1$s'%n", var);
		    }
		    else {
			throw new BuildException(String.format("Cannot parse Define value: '-D%1$s'%n\tformat should be: -Dvar=value or -Dvar", d));
		    }
		}
	    }
	}


	/**
	 * Set value for <code>undefine</code> option.
	 * <p> Muliple variables can be specified (comma or semicolon delimited).
	 *
	 * @param	var	The variable to undefine.
	 * @throws	BuildException if the variable is not defined now, and the {@link #ignoreUndefined} flag is {@code false}.
	 */
	public void setUndefine(String var) throws BuildException {
	    if (var == null || var.isEmpty())
		return;
	    String[] vars = comma.split(var);
	    for (String v : vars) {
		if (defines.remove(v) == null) {
		    if (!ignoreUndefined) {
			throw new BuildException(String.format("Variable '%1$s' is not defined in the current environment.", v));
		    }
		}
		else {
		    if (plusVerbose)
			out.format("Undefining '%1$s'%n", v);
		}
	    }
	}


	/**
	 * Set value for <code>outputExt</code> option.
	 *
	 * @param	arg	The new output file extension value.
	 * @throws	BuildException if the value is empty.
	 */
	public void setOutputExt(String arg) throws BuildException {
	    if (arg.length() > 0) {
		if (arg.charAt(0) == '.')
		    outputExt = arg;
		else
		    outputExt = "." + arg;
	    }
	    else {
		throw new BuildException("Cannot specify empty output extension value.");
	    }
	}


	/**
	 * Set value for <code>inputExt</code> option.
	 *
	 * @param	arg	The new input file extension value.
	 * @throws	BuildException if the value is empty.
	 */
	public void setInputExt(String arg) throws BuildException {
	    if (arg.length() > 0) {
		if (arg.charAt(0) == '.')
		    inputExt = arg;
		else
		    inputExt = "." + arg;
	    }
	    else {
		throw new BuildException("Cannot specify empty input extension value.");
	    }
	}


	/**
	 * Set value for <code>includePath</code> option.
	 *
	 * @param	pathArg	The new include path.
	 * @throws	BuildException if the path is empty.
	 */
	public void setIncludePath(String pathArg) throws BuildException {
	    if (pathArg.length() > 0) {
		String[] paths = comma.split(pathArg);
		includePaths = new Vector<String>();
		for (String p : paths)
		    includePaths.add(p);
	    }
	    else {
		throw new BuildException("Cannot specify empty search path list.");
	    }
	}


	/**
	 * Set value for <code>nologo</code> option.
	 *
	 * @param	var	The new value for the option.
	 */
	public void setNologo(boolean var) {
	    displayLogo = !var;
	}


	/**
	 * Set value for <code>ignoreUndefined</code> option.
	 *
	 * @param	val	The new value for the option.
	 */
	public void setIgnoreUndefined(boolean val) {
	    ignoreUndefined = val;
	}


	/**
	 * Set value for <code>verbose</code> option.
	 *
	 * @param	value	The new value for the option.
	 * @throws	BuildException if the value is invalid.
	 */
	public void setVerbose(String value) throws BuildException {
	    if (value.length() == 0)
		verbose = true;
	    else if (value.equals("+") || value.equalsIgnoreCase("plus"))
		verbose = plusVerbose = true;
	    else if (value.equals("*") || value.equalsIgnoreCase("super"))
		verbose = superVerbose = true;
	    else if (value.equalsIgnoreCase(Boolean.toString(true)))
		verbose = true;
	    else if (value.equalsIgnoreCase(Boolean.toString(false)))
		verbose = false;
	    else
		throw new BuildException(String.format("Undefined 'verbose' option '%1$s'.", value));
	}


	/**
	 * Set value for the <code>format</code> option.
	 *
	 * @param	value	The new value for the option.
	 * @throws	BuildException if the value is invalid.
	 */
	public void setFormat(String value) throws BuildException {
	    if (value.equalsIgnoreCase("UTF8") ||
		value.equalsIgnoreCase("UTF-8"))
		processAsUTF8 = true;
	    else {
		throw new BuildException(String.format("Unknown file format: '%1$s'%n\tvalid choices are: 'UTF8' or 'UTF-8'", value));
	    }
	}


	/**
	 * Set value for the output log file (<code>log</code> option).
	 *
	 * @param	value	The new file name for the output log.
	 * @throws	BuildException if the name is invalid somehow.
	 */
	public void setLog(String value) throws BuildException {
	    if (value == null || value.trim().isEmpty()) {
		throw new BuildException("Log file value must not be empty.");
	    }
	    logFileName = value;
	}


	/**
	 * Set value for the <code>overwrite</code> option.
	 *
	 * @param	value	Whether to overwrite the log file or not.
	 */
	public void setOverwrite(boolean value) {
	    overwriteLog = value;
	}


	/**
	 * Set value for the <code>processAsDirectory</code> option.
	 *
	 * @param	val	The new value for the option.
	 */
	public void setProcessAsDirectory(boolean val) {
	    processAsDirectory = val;
	}


	/**
	 * Set value for the <code>recurseDirectories</code> option.
	 *
	 * @param	val	The new value for the option.
	 */
	public void setRecurseDirectories(boolean val) {
	    if (val)
		processAsDirectory = recurseDirectories = val;
	    else
		recurseDirectories = val;
	}


	/**
	 * Set value for the <code>alwaysProcess</code> option.
	 *
	 * @param	val	The new value for the option.
	 */
	public void setAlwaysProcess(boolean val) {
	    alwaysProcess = val;
	}


	/**
	 * Set value for the <code>includeVar</code> option.
	 *
	 * @param	value	The new value for the include environment variable name.
	 */
	public void setIncludeVar(String value) throws BuildException {
	    if (value.length() > 0) {
		inclEnvVar = value;
	    }
	    else {
		throw new BuildException("Cannot specify empty environment variable name for include variable.");
	    }
	}


	/**
	 * Set value for one or more <code>file</code> options.
	 *
	 * @param	arg	The next {@code "file"} option.
	 */
	public void setFile(String arg) {
	    fileArgs.add(arg);
	}


	/**
	 * Set value for one or more <var>dir</var> options.
	 *
	 * @param	arg	The next {@code "dir"} option.
	 */
	public void setDir(String arg) {
	    fileArgs.add(arg);
	}


	/**
	 * Default constructor which initializes all the per-instance
	 * variables.
	 */
	public JavaPreProc() {
	    // Read in the environment and define everything found
	    defines = new HashMap<String,String>(System.getenv());

	    // Define some predefined variables
	    defines.put(JAVA_VERSION_VAR_NAME, System.getProperty("java.version"));
	    defines.put(JAVA_PP_VERSION_VAR_NAME, VERSION);

	    Date now = currentCal.getTime();
	    defines.put(DATE_VAR_NAME, dateFmt.format(now));
	    defines.put(TIME_VAR_NAME, timeFmt.format(now));
	    // "__FILE__" will be reset for each file processed
	    defines.put(FILE_VAR_NAME, "-- none --");
	}


	/**
	 * The main execution method (called either from {@link #main} or from Ant
	 * when the {@code <preproc ...>} task is executed).
	 */
	public void execute() throws BuildException {
	    // Check for illegal combination of options
	    if (overwriteLog && logFileName == null) {
		throw new BuildException("Overwrite option is not applicable for output to console.");
	    }

	    // Build the output log and error stream if the default is overridden
	    if (logFileName != null) {
		try {
		    File f = new File(logFileName);
		    OutputStream os = Files.newOutputStream(f.toPath(), WRITE, CREATE,
			overwriteLog ? TRUNCATE_EXISTING : APPEND);
		    PrintStream ps = new PrintStream(os, false);
		    out = ps;
		    err = ps;
		}
		catch (IOException ioe) {
		    throw new BuildException(String.format("I/O Error creating output log file: %1$s",
			exceptMessage(ioe)));
		}
	    }

	    // Output sign-on banner if "verbose" is specified and "-nologo" isn't
	    if (verbose) {
		signOnBanner(displayLogo);
	    }

	    // Build the pattern matcher to recognize our directives
	    cmdPat = Pattern.compile(String.format(cmdPatFormat, directiveStartCh));
	    passPat = Pattern.compile(String.format(passPatFormat, directiveStartCh));
	    commentPat = Pattern.compile(String.format(commentPatFormat, directiveStartCh));

	    // Process files or directories depending on the -r or -R switches
	    if (processAsDirectory) {
		setDefaultExtensions();

		processDirSpecs(fileArgs);
	    }
	    else {
		processFileSpecs(fileArgs);
	    }

	    // Flush and close the output log (if not the console)
	    if (logFileName != null) {
		out.flush();
		out.close();
		out = System.out;
		err = System.err;
	    }
	}


	/**
	 * Main driver for the Java PreProcessor.
	 * <p> Processing consists of the following steps:
	 * <ul><li>Read in the system environment and define all variables therein.
	 * <li>Define other misc. variables.
	 * <li>Process command-line options.
	 * <li>Display sign-on banner.
	 * <li>Process all file and directory specifications on the command line.
	 * </ul>
	 *
	 * @param	args	The command line arguments for the process.
	 */
	public static void main(String[] args) {

	    JavaPreProc inst = new JavaPreProc();

	    // Process the command-line switches
	    try {
		if (processCommandLine(inst, args))
		    System.exit(1);
	    }
	    catch (BuildException be1) {
		System.err.format("Error in command line: %1$s%n", be1.getMessage());
		System.exit(1);
	    }

	    // Add all the command-line arguments as potential
	    // file specs
	    for (String a : args) {
		inst.setFile(a);
	    }

	    try {
		inst.execute();
	    }
	    catch (BuildException be2) {
		System.err.format("Error: %1$s%n", be2.getMessage());
		System.exit(2);
	    }
	}
}
