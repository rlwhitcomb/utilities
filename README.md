# utilities
Some of my personal utility programs, collected over a number of years. Most were developed to be useful to me in my own programming, and so may be useful to others.

## bat
Useful Windows batch files/programs.

## c
C programs for use on the command line (mostly old/MS-DOS based).

## java
Java command line programs. Many of the latest are translations from C to Java to be useful on multiple platforms.

Other pieces here include four new Ant conditions, a new Ant task, plus a preprocessor class invocable from the command line or as an Ant task.

Some GUI programs based on the Apache Pivot™ platform.



# Building

The C programs are so old that they were originally built on 32-bit Windows / MS-DOS, and I have no idea whether they even build with the latest Visual Studio. Almost none are useful on non-WIndows platforms. I am in the process of converting the most useful ones to Java anyway.

The Java programs build certainly with Java 8, and also build with Java 11 on OSX (and probably later), but do require close to the latest Apache Ant™ version (1.10.x).  The Apache Pivot™ based programs need the trunk version of the Apache Pivot code (post 2.0.5).

Complete Javadoc for the straight Java code can be built using "ant doc" and will be available rooted at "doc/index.html" (has Frames support when built with Java 11 or earlier). Building the Pivot-based code is incomplete, and thus there is no Javadoc target yet.

All the "java" directory classes are built into a single "utilities.jar" file, which can be used as the CLASSPATH for running any of the main program (such as Cat, Tree, DumpManifest, etc.). There are some wrapper shell/bat scripts ("tre", "tre.bat", etc.) to invoke these main programs, which are good examples of how to do any additional main programs.

# ToDo

Work going on currently includes:

- InitializationTask is all wrong -- need a better paradigm, using existing Java mechanisms (see Issue #8)
- Make InitializationTask and QueuedThread work seamlessly with "synchronized"
- Convert D.C to Java (include supporting code).
- Get "pivot" builds working, including Javadoc, packaging, etc.
- New Java-based "tail", "head", and other Linux utilities missing on Windows.
- Use new color coding to spruce up all help and error messages; add -color and -nocolor flags everywhere
- Maybe find a way in Options to add "standard" flags (like "-color" above)
- Completely new paradigm for Options, similar to Apache commons-cli, including support for printing help text from the options list.
- UUID program: generate random, generate from text, separate into parts, maybe more.
- Lots of unit tests.
- Consider a standard method to read .properties files (take the code from "readVersionProperties" in Environment and make it general somewhere else?)
- Completely update JavaPreProc to use an Antlr expression grammar to process the directives.
- Add a Spanish translation for all the current resources (and ongoing for new resource files).

Also see "issues" at https://github.com/rlwhitcomb/utilities/issues

## Notes on New Programs and Features

### License
- Need something to check proper license, copyright, etc. in all files
- Check copyright years by history; also check for out-of-order history comments, and/or bad format, or missing entirely

### Shell invoker
- Option to "boiler" to create this thing (along with the path/class)
- Figure out a location to put these wrapper scripts (in "bat" folder? or root? or along with the Java source?)

### Boilerplate program
- Name should be "boiler"
- cmd line options (at least some) to specify -cmdline vs -gui or -both, maybe path, etc.
- Specify license option: MIT, Apache, GPL, BSD, others?
- Option to create invoker script (windows, *nix or both)
- Add license with correct copyright date
- .properties file with Author, maybe History line format, default program type option, default "create wrapper" option
- GUI program, but with (possible) option to set most/all of the values from command line and just generate it
- Add title and version to "version.properties"

### Paradigm for doing either command line or GUI (Pivot) depending on flags
- Add to boilerplate options
- Boilerplate program will use this code b/c we sometime want to just do command line, other times want the GUI
- Word Finder can also use this option
- Unclear yet if a switch ***back*** from GUI to console will work.

### Calc
- implement Taylor series expansion of other trig/log functions to requested precision
- separate out the log/trig functions into TrigLogUtil (or something like that), for better encapsulation (NumericUtil is getting too big)
- redo the [box] formats of strings (to be? x'...', b'...', o'...') then parse such beasts back to strings
- allow "_" in numeric values, like Java does
- format options for commas (or _) (';,' or ';__') or (';$' to format as dollar)??
- get help and intro text (both console and GUI) from resources
- add option (both GUI and REPL modes) to open another file
- functions for financial calculations (INTEREST, NPV, PAYMENT, etc.) (**e_mort** is a start)
- ?? need a way to do "setScale(...)" on the values ??
- See all the notes on updates needed to help page (calc_help.htmlpp); use "ant update" and then "c -?" to preprocess and view the result
- Unicode symbols to add:
    4th root 221C
    heavy plus 2795
    heavy minus 2796
    heavy divide 2797
    superscript 2 00b2 (** 2)
    superscript 3 00b3
    superscript 4-9 (2074-2079)
    unicode equivalents of  [ ] and { } and ( )
    per mille sign (1/1000) and per ten thousand sign (1/10000) both western and arabic
    other mathematical digits, or dingbat numbers
    such as 2460--2473 (1) .. (20) or 2474--2487 or 2488--249b or 24ea, 24eb--24f4, 24f5--24fe, 24ff, 2776..277F, 2780..2789, 278a..2793
    FF10--FF19, osmanya digit 0 at 104a0-104a9, 1d7ce-1d7d7, 1d7d8-1d7e1...1d7ff
- roman numerals - ROMAN NUMERAL MODE (upper / lower case)
    2160-216f and 2170-217f

- !!null seems like it should work (!!"true" -> true)
- seems like we could have an "exit from GUI mode back to REPL mode" which is available when running from within REPL mode.

#### Calc: Want to add parameters and local variables to functions.
define a($b, $c, $d) = { $f = blah; ... }

#### problems:
- need to have local variables, but how do we get that incorporated into LValueContext?
- how to deal with recursive invocations of functions with local variables (creating the dynamic context)?
- need a way to store the param names, then to associate the values with the names
- how about lists and maps:  pass by reference or value?  should there be an option?
- e.g.: def npv($pv, $pmt, $i) = { loop slkdjf;lkj npv; }
- are the variables "defined" inside the function local to the function (i.e., what if we say def ... { a = 27; ...} where a was not defined before?  I imagine with the changes to LValueContext, the "variables" at the outer level would be local to the function... but how to reference truly global variables then? (link to global "variables"???)

### Small Bugs / Fixes

- Add an indent parameter to "lists" (since we don't seem to be able get leading/trailing spaces with -e or -f options.
- Add some examples in the "list" help (or make a web page out of it to allow more verbosity)
  (I struggle with how to use the options to get what I need done)
- Take the code from Calc ("displayHelp") and put it somewhere 'standard' so other can use it (like "Lists"...)
  Not sure where, though: Environment, Launcher, Intl, ClassUtil, FileUtilities, or a new HelpUtil.... (or maybe HelpInfo)
- seems like we could matrix manipulation now (multiply of conformable matrices, addition, etc.)
- For Javadoc in CalcObjectVisitor: heading should be the grammar as in Calc.g4

### WordFind
- Finish the GUI code.

### UUID
- lots of options
- generate random one
- generate from "name" using command-line string or read from file (charset given) or from -stdin (see Cat for details)
- option to dissect and print out each field if a UUID is given on the command line (with or without - separators)
- ??? figure out how to generate a time-based one based on the RFC (is this necessary??)
- Use code (Apache 2) from here: https://github.com/apache/cassandra/blob/trunk/src/java/org/apache/cassandra/utils/UUIDGen.java

### Options difficulties
- how to just set an enum of what to expect next
- how to concatenate flags or allow separately (tar -cvf for instance)
- option to only allow long name with "--", while short name can do just "-"
- option to allow "/" on Windows for some/all
- each option needs case-sensitive flag
- how to invoke code inline (like the "cat" program reading from stdin at the point the flag is encountered
- options to just warn on bad flag, ignore it, or halt right there
- how to handle empty command line
- option to print help, and option whether to print it along with bad option message(s)
- How to deal with positional vs. process options first
- if the latter, how to package up the non-option values for later processing
- Need to be able to format a coherent "help" message from these option objects, including the messy bit in "Tree" needed to do the various case-sensitive options, where some had to be dropped out, or formatted with the "makeDisplayableList" method. UGH!  This is hard!
- whether the option is true/false, or sets a value
- validation required ? (like valid Charset?) and then the message to print if the value is invalid (and/or just a validation method that has all the message, etc.)
- Intl options (use "getKeyString" so the text itself specifies whether it is a key or just a string)

### Options suggestions
- pass in an empty map, and option structures have a map key specified to set in the map if the option is given
- option to set true/false in the map
- pass in an empty list, that can be filled in (if given) with the non-option values
- each Option has flags, key options (long and short name, etc.), option type (flag, needs add'l value, can be combined, +/- turn on/off, value is int, value is charset, value is file name, etc.), description (for help), default value
- Result of options parsing is a map, with all the default values applied, and any values given on the command line filled in with their values; warning and errors displayed, help displayed, etc.  Also list of non-option supplied on the command line.  This doesn't say how to do custom validation, how to iterate over positional arguments, etc.
- Maybe "options" concept includes non-options, which are saved positionally, and can be accessed that way
- can we iterate over the (already processed) command line, and get back "this is an option" or "this is a non-option string" for each position, and then we can choose to process the options positionally or ignore cuz they have already been done.... Need a way to reset to defaults if positionally is being done.
- some kind of functional interface (Runnable or Callable)? or Predicate for custom validation, or additional processing
- ?? should we just do that for validation?  Validator interface "boolean isValid(Object input)" ...info/.../validation package, with Validator interface, and custom implementations (such as IntValidator, FloatValidator, etc. CharsetValidator
- How do the new interfaces fit in with this scheme (ChoiceEnum)?

