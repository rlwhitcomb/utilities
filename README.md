# utilities
Some of my personal utility programs, collected over a number of years. Most were developed to be useful to me in my own programming, and so may be useful to others.

## bat
Useful Windows batch files/programs.

## c
C programs for use on the command line.

## java
Java command line programs. Many of the latest are translations from C to Java to be useful on multiple platforms.

## pivot
GUI programs based on the Apache Pivot™ platform.



# Building

The C programs are so old that they were originally built on 32-bit Windows / MS-DOS, and I have no idea whether they even build with the latest Visual Studio. Almost none are useful on non-WIndows platforms. I am in the process of converting the most useful ones to Java anyway.

The Java programs build certainly with Java 8, and also build with Java 11 on OSX (and probably later), but do require close to the latest Apache Ant™ version (1.10.x).  The Apache Pivot™ based programs need the trunk version of the Apache Pivot code (post 2.0.5).

Complete Javadoc for the straight Java code can be built using "ant doc" and will be available rooted at "doc/index.html" (has Frames support when built with Java 11 or earlier). Building the Pivot-based code is incomplete, and thus there is no Javadoc target yet.



# ToDo

Work going on currently includes:

- Making a GUI version of WordFind, with the same program able to operate either from command line or as a GUI form.
- Convert D.C to Java (include supporting code).
- Get "pivot" builds working, including Javadoc, packaging, etc.
- New Java-based "tail", "head", and other Linux utilities missing on Windows.
- Add support for color-coding within Intl text using HTML-like tags, then update help text in existing programs.
- Completely new paradigm for Options, similar to Apache commons-cli, including support for printing help text from the options list.
- UUID program: generate random, generate from text, separate into parts, maybe more.
- Lists program for converting to/from single- and multiple-line lists.
- Pivot demo program for CSS colors.
- Lots of unit tests.

