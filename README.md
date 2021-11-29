[![Java 11 Build Status](https://github.com/rlwhitcomb/utilities/actions/workflows/ant-jdk11.yml/badge.svg)](https://github.com/rlwhitcomb/utilities/actions/workflows/ant-jdk11.yml)
[![Java 17 Build Status](https://github.com/rlwhitcomb/utilities/actions/workflows/ant-jdk17.yml/badge.svg)](https://github.com/rlwhitcomb/utilities/actions/workflows/ant-jdk17.yml)

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

The Java programs build with Java 8, 11, and also with 17 (11 and 17 are used in the CI builds), but do require close to the latest Apache Ant™ version (1.10.x).  The Apache Pivot™ based programs need the trunk version of the Apache Pivot code (post 2.0.5) (included in the "external-files" directory).

Complete Javadoc for the straight Java code can be built using "ant doc" and will be available rooted at "doc/index.html" (has Frames support when built with Java 11 or earlier).

All the "java" directory classes are built into a single "utilities.jar" file, which can be used as the CLASSPATH for running any of the main program (such as Cat, Tree, DumpManifest, etc.). There are some wrapper shell/bat scripts ("tre", "tre.bat", etc.) to invoke these main programs, which are good examples of how to do any additional main programs.

# ToDo

Work going on currently includes:

- InitializationTask is all wrong -- need a better paradigm, using existing Java mechanisms (see Issue #8)
- Make InitializationTask and QueuedThread work seamlessly with "synchronized"
- Finish converting D.C to Java (include supporting code).
- New Java-based "tail", and other Linux utilities missing on Windows.
- Finish -color and -nocolor flags everywhere
- Implement color coding in Calc GUI.
- Maybe find a way in Options to add "standard" flags (like "-color" above)
- Completely new paradigm for Options, similar to Apache commons-cli, including support for printing help text from the options list.
- UUID program: generate random, generate from text, separate into parts, maybe more.
- Lots of unit tests.
- Consider a standard method to read .properties files (take the code from "readVersionProperties" in Environment and make it general somewhere else?)
- Finish update of JavaPreProc to use the Antlr expression grammar for directives.
- Add a Spanish translation for all the current resources (and ongoing for new resource files).
- Finish WordFind GUI, including the input filtering or else do "editing" with a bunch of separate fields.

Also see "issues" at https://github.com/rlwhitcomb/utilities/issues

## Notes on New Programs and Features

### Copyrights
- Check for out-of-order history comments, and/or bad format, or missing entirely
- Need to deal with "git move" operations that generate false errors about bad copyright years

### Shell invoker
- Option to "boiler" to create this thing (along with the path/class)

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

### Calc
- implement Taylor series expansion of other trig/log functions to requested precision
- need sec, cot, csc, and inverses as well
- allow "_" in numeric values, like Java does
- format options for '_' ('@_') or ('@$' to format as dollar)??
- functions for financial calculations (INTEREST, NPV, PAYMENT, etc.) (**e_mort** is a start)
- ?? need a way to do "setScale(...)" on the values ??
- See all the notes on updates needed to help page (calc_help.htmlpp); use "ant update" and then "c -?" to preprocess and view the result
- Unicode symbols to add:
    unicode equivalents of  [ ] and { } and ( )
    per mille sign (1/1000) and per ten thousand sign (1/10000) both western and arabic
 - How would we implement "pop" mode on the settings checkboxes? (tri-state won't work b/c it cycles through), radio won't work, maybe a little button beside the checkbox??  need an icon for it
- "list" that converts string or map to an array (list)
- "map" that converts string or array (list) to a map (with index as the key)
- ?? could there be special processing if the list values were in "key:value" form??  and vice-versa for "list"??
- does "@_" work inside parens?  how would I convert a JSON object into a string for other processing?  ?? need "string" function??
- options to change background color in GUI (maybe in Settings dialog) and on command line (-bg red)
- some way to not clear input in GUI mode if there is a syntax error during parsing

### Small Bugs / Fixes
- Add some examples in the "list" help (or make a web page out of it to allow more verbosity)
  (I struggle with how to use the options to get what I need done)
- Take the code from Calc ("displayHelp") and put it somewhere 'standard' so other can use it (like "Lists"...)
  Not sure where, though: Environment, Launcher, Intl, ClassUtil, FileUtilities, or a new HelpUtil.... (or maybe HelpInfo)
- seems like we could matrix manipulation now (multiply of conformable matrices, addition, etc.)

### WordFind
- Finish the GUI code.
- need some kind of filter to UPPER case everything, limit input to only letters and blank, and turn blank into ?
  (probably needs to go into Pivot code)

### UUID
- lots of options
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

### Translation
- Nothing in .../tools directory has had their text moved to resources.
- Much more translation could be done.
- Is there a good way to check the existing translations with native speakers?
- Need a way to check the text resources to make sure of:
  - all keys are in all translations
  - no other necessary, translatable text is in the source

### Character Sets
- Could we normalize some of this somehow and put this processing into one place so
  everyone can use it?

