/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 Roger L. Whitcomb.
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
 *	Wrapper around PreProc to make it into an Ant Task.
 *
 * History:
 *  14-Jan-23 rlw #593:	Created from the original PreProc code.
 */
package info.rlwhitcomb.preproc;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.IOException;


/**
 * Wrapper around {@link PreProc} that implements an Ant {@link Task}.
 * <p> For the complete documentation see the doc for {@link PreProc}.
 */
public class PreProcTask extends Task
{
	private PreProc instance = null;


	public void setDirectiveChar(final String ch) throws BuildException {
	    try {
		instance.setDirectiveChar(ch);
	    }
	    catch (IllegalArgumentException ex) {
		throw new BuildException(ex);
	    }
	}

	public void setDefine(final String def) throws BuildException {
	    try {
		instance.setDefine(def);
	    }
	    catch (IllegalArgumentException ex) {
		throw new BuildException(ex);
	    }
	}

	public void setUndefine(final String var) throws BuildException {
	    try {
		instance.setUndefine(var);
	    }
	    catch (IllegalArgumentException ex) {
		throw new BuildException(ex);
	    }
	}

	public void setOutputExt(final String arg) throws BuildException {
	    try {
		instance.setOutputExt(arg);
	    }
	    catch (IllegalArgumentException ex) {
		throw new BuildException(ex);
	    }
	}

	public void setInputExt(final String arg) throws BuildException {
	    try {
		instance.setInputExt(arg);
	    }
	    catch (IllegalArgumentException ex) {
		throw new BuildException(ex);
	    }
	}

	public void setIncludePath(final String pathArg) throws BuildException {
	    try {
		instance.setIncludePath(pathArg);
	    }
	    catch (IllegalArgumentException ex) {
		throw new BuildException(ex);
	    }
	}

	public void setNologo(final boolean var) {
	    instance.setNologo(var);
	}

	public void setIgnoreUndefined(final boolean value) {
	    instance.setIgnoreUndefined(value);
	}

	public void setVerbose(final String value) throws BuildException {
	    try {
		instance.setVerbose(value);
	    }
	    catch (IllegalArgumentException ex) {
		throw new BuildException(ex);
	    }
	}

	public void setFormat(final String value) throws BuildException {
	    try {
		instance.setFormat(value);
	    }
	    catch (IllegalArgumentException ex) {
		throw new BuildException(ex);
	    }
	}

	public void setLog(final String value) throws BuildException {
	    try {
		instance.setLog(value);
	    }
	    catch (IllegalArgumentException ex) {
		throw new BuildException(ex);
	    }
	}

	public void setOverwrite(final boolean value) {
	    instance.setOverwrite(value);
	}

	public void setProcessAsDirectory(final boolean value) {
	    instance.setProcessAsDirectory(value);
	}

	public void setRecurseDirectories(final boolean value) {
	    instance.setRecurseDirectories(value);
	}

	public void setAlwaysProcess(final boolean value) {
	    instance.setAlwaysProcess(value);
	}

	public void setIncludeVar(final String value) throws BuildException {
	    try {
		instance.setIncludeVar(value);
	    }
	    catch (IllegalArgumentException ex) {
		throw new BuildException(ex);
	    }
	}

	public void setFile(final String arg) {
	    instance.setFile(arg);
	}

	public void setDir(final String arg) {
	    instance.setDir(arg);
	}

	@Override
	public void execute() throws BuildException {
	    try {
		instance.execute();
	    }
	    catch (IllegalArgumentException | IOException ex) {
		throw new BuildException(ex);
	    }
	}

	@Override
	public void init() throws BuildException {
	    instance = new PreProc();
	}

}
