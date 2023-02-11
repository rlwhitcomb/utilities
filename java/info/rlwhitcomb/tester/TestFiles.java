/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022-2023 Roger L. Whitcomb.
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
 *	Holder for file-related objects for testing.
 *
 * History:
 *  26-Oct-22 rlw #540:	Move out of Tester into separate class file.
 *  10-Feb-23 rlw  ---	Small optimization not to write empty strings.
 */
package info.rlwhitcomb.tester;

import info.rlwhitcomb.util.FileUtilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;


/**
 * An object to hold the multiple file-related objects that need to be passed around
 * during operation.
 */
class TestFiles
{
	public File inputFile = null;
	public File outputFile = null;
	public File errorFile = null;
	public BufferedWriter inputWriter = null;
	public BufferedWriter outputWriter = null;
	public BufferedWriter errorWriter = null;

	public File resultFile = null;

	/**
	 * If {@code true} line endings are written as plain {@code '\n'}
	 * regardless of platform, while {@code false} indicates using
	 * the platform-specific line ending.
	 */
	private boolean ignoreLineEndings;


	public TestFiles(final boolean ignore) {
	    ignoreLineEndings = ignore;
	}

	public void createStreams(final Charset cs)
		throws IOException
	{
	    inputFile  = FileUtilities.createTempFile("canoninput");
	    outputFile = FileUtilities.createTempFile("canonoutput");
	    errorFile  = FileUtilities.createTempFile("canonerror");

	    inputWriter  = Files.newBufferedWriter(inputFile.toPath(), cs);
	    outputWriter = Files.newBufferedWriter(outputFile.toPath(), cs);
	    errorWriter  = Files.newBufferedWriter(errorFile.toPath(), cs);
	}

	public void writeInputLine(final String line)
		throws IOException
	{
	    if (line != null) {
		if (!line.isEmpty())
		    inputWriter.write(line);
		inputWriter.newLine();
	    }
	}

	private void write(final BufferedWriter writer, final String line)
		throws IOException
	{
	    if (line != null) {
		if (!line.isEmpty())
		    writer.write(line);
		if (ignoreLineEndings)
		    writer.write('\n');
		else
		    writer.newLine();
	    }
	}

	public void writeOutputLine(final String line)
		throws IOException
	{
	    write(outputWriter, line);
	}

	public void writeErrorLine(final String line)
		throws IOException
	{
	    write(errorWriter, line);
	}

	private BufferedWriter close(final BufferedWriter writer) {
	    if (writer != null) {
		writer.flush();
		writer.close();
	    }

	    return null;
	}

	public void closeStreams()
		throws IOException
	{
	    inputWriter  = close(inputWriter);
	    outputWriter = close(outputWriter);
	    errorWriter  = close(errorWriter);
	}

	public void abort()
		throws IOException
	{
	    closeStreams();
	}

	public void deleteFiles()
		throws IOException
	{
	    inputFile.delete();
	    outputFile.delete();
	    errorFile.delete();
	}
}
