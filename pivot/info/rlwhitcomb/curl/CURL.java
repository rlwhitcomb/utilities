 /*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2018,2020 Roger L. Whitcomb.
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
 *	GUI utility to invoke the "curl" command line program and
 *	execute it with given parameters and display the result.
 *
 *  History:
 *	15-Sep-2016 (rlwhitcomb)
 *	    Initial coding.
 *	16-Sep-2016 (rlwhitcomb)
 *	    Fix quoting on curl command line for non-Windows platforms.
 *	10-Nov-2017 (rlwhitcomb)
 *	    Add class Javadoc.
 *	08-Mar-2018 (rlwhitcomb)
 *	    Use "--data-binary" to retain the user's exact data. Use lambdas
 *	    where appropriate to improve readability. Add an activity indicator
 *	    to the input data field while processing, so run the query in a
 *	    background task. Allow Enter key in URL input field to press the
 *	    "Go" button. Add an entry field for extra "--header" information.
 *	14-Apr-2020 (rlwhitcomb)
 *	    Tweaks for GitHub.
 *	15-Apr-2020 (rlwhitcomb)
 *	    Speedup output display. Refactor code a bit.
 *	    Add some more keyboard shortcuts.
 */
package info.rlwhitcomb.curl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.serialization.SerializationException;
import org.apache.pivot.util.concurrent.Task;
import org.apache.pivot.util.concurrent.TaskExecutionException;
import org.apache.pivot.util.concurrent.TaskListener;
import org.apache.pivot.wtk.*;
import info.rlwhitcomb.util.CharUtil;
import info.rlwhitcomb.util.ClassUtil;
import info.rlwhitcomb.util.CommandProcessor;
import info.rlwhitcomb.util.FileUtilities;
import info.rlwhitcomb.util.Which;


/**
 * A GUI front-end for the <tt>"curl"</tt> command-line utility.
 * <p>Useful for doing basic testing on web services.
 * <p> Ctrl-&lt;key&gt; will nagivate to the appropriate form field:
 * <ul><li>Ctrl-C (or Ctrl-T) to "Content Type"</li>
 * <li>Ctrl-R to "Request"</li>
 * <li>Ctrl-H to "Header"</li>
 * <li>Ctrl-S to "Silent"</li>
 * <li>Ctrl-L (or Ctrl-U) to "URL"</li>
 * <li>Ctrl-I to "Data" (Input)</li>
 * <li>Ctrl-O to "Result" (Output)</li>
 * <li>Ctrl-G to "Go!" button</li>
 * </ul>
 */
public class CURL
	implements Application
{
	BXMLSerializer serializer = null;

	private Display display;

	@BXML private Window mainWindow;
	@BXML private ListButton contentTypeList;
	@BXML private ListButton requestType;
	@BXML private TextInput headerInput;
	@BXML private Checkbox silentCheck;
	@BXML private TextInput urlInput;
	@BXML private PushButton goButton;
	@BXML private TextArea inputTextArea;
	@BXML private TextArea outputTextArea;
	@BXML private GridPane activityGrid;
	@BXML private ActivityIndicator indicator;


	private class KeyPressButtonListener implements ComponentKeyListener
	{
		private PushButton buttonToPress;

		public KeyPressButtonListener(PushButton button) {
		    buttonToPress = button;
		}

		@Override
		public boolean keyPressed(Component component, int keyCode, Keyboard.KeyLocation keyLocation) {
		    if (keyCode == Keyboard.KeyCode.ENTER) {
			buttonToPress.press();
			return true;
		    }
		    return false;
		}
	}

	private class KeyPressListener implements ComponentKeyListener
	{
		@Override
		public boolean keyPressed(Component component, int keyCode, Keyboard.KeyLocation keyLocation) {
		    if (Keyboard.isPressed(Keyboard.Modifier.CTRL)) {
			Component focusComponent = null;
			switch (keyCode) {
			    case Keyboard.KeyCode.L:
			    case Keyboard.KeyCode.U:
				focusComponent = urlInput;
				break;
			    case Keyboard.KeyCode.H:
				focusComponent = headerInput;
				break;
			    case Keyboard.KeyCode.C:
			    case Keyboard.KeyCode.T:
				focusComponent = contentTypeList;
				break;
			    case Keyboard.KeyCode.R:
				focusComponent = requestType;
				break;
			    case Keyboard.KeyCode.S:
				focusComponent = silentCheck;
				break;
			    case Keyboard.KeyCode.G:
				focusComponent = goButton;
				break;
			    case Keyboard.KeyCode.I:
				focusComponent = inputTextArea;
				break;
			    case Keyboard.KeyCode.O:
				focusComponent = outputTextArea;
				break;
			}
			if (focusComponent != null) {
			    focusComponent.requestFocus();
			    return true;
			}
		    }
		    return false;
		}
	}


	private class CURLCommandProcessor extends CommandProcessor
	{
		public CURLCommandProcessor(File headerFile, String requestType, String url, File dataFile, boolean silent) {
		    super("curl", "--header", "@" + headerFile.getPath(),
				"--request", requestType,
				silent ? "--silent" : "--no-silent",
				silent ? "--show-error" : "--no-show-error",
				"--data-binary", "@" + dataFile.getPath(),
				url);
		}

		@Override
		public boolean process(final String line) {
		    ApplicationContext.queueCallback(() -> {
			int length = outputTextArea.getCharacterCount();
			outputTextArea.insertText(line, length);
			outputTextArea.insertText("\n", length + line.length());
		    });
		    return true;
		}


	}


	private class CURLTask extends Task<Void>
	{
		@Override
		public Void execute() throws TaskExecutionException {
		    try {
			String inputText = inputTextArea.getText();

			File dataFile = FileUtilities.createTempFile("curl", true);
			if (!CharUtil.isNullOrEmpty(inputText)) {
			    FileWriter writer = new FileWriter(dataFile);
			    writer.write(inputText);
			    writer.flush();
			    writer.close();
			}

			File headerFile = FileUtilities.createTempFile("curl", true);
			FileWriter headerWriter = new FileWriter(headerFile);
			headerWriter.write("Content-type: " + (String)contentTypeList.getSelectedItem());
			headerWriter.write('\n');
			String headerInfo = headerInput.getText();
			if (!CharUtil.isNullOrEmpty(headerInfo)) {
			    headerWriter.write(headerInfo);
			    headerWriter.write('\n');
			}
			headerWriter.flush();
			headerWriter.close();

			CURLCommandProcessor ccp = new CURLCommandProcessor(headerFile,
				(String)requestType.getSelectedItem(),
				urlInput.getText(),
				dataFile,
				silentCheck.isSelected());
			ccp.run(null);
		    }
		    catch (IOException ioe) {
			throw new RuntimeException(ioe);
		    }

		    return null;
		}
	}


	private void setActive(boolean active) {
	    indicator.setActive(active);
	    activityGrid.setVisible(active);
	}

	private void handleCURLQuery() {
	    outputTextArea.setText("");
	    setActive(true);

	    CURLTask task = new CURLTask();
	    TaskListener<Void> taskListener = new TaskListener<Void>() {
		@Override
		public void taskExecuted(Task<Void> task) {
		    setActive(false);
		}

		@Override
		public void executeFailed(Task<Void> task) {
		    setActive(false);
		}
	    };
	    task.execute(new TaskAdapter<Void>(taskListener));
	}


	@Override
	public void startup(Display display, Map<String, String> properties) {
	    this.display = display;

	    try {
		serializer.readObject(CURL.class, "curl.bxml");
		serializer.bind(this);

		goButton.getButtonPressListeners().add((button) -> handleCURLQuery());
		urlInput.getComponentKeyListeners().add(new KeyPressButtonListener(goButton));
		mainWindow.getComponentKeyListeners().add(0, new KeyPressListener());

		mainWindow.open(display);

		if (Which.findExecutable("curl") == null) {
		    throw new RuntimeException("Unable to locate 'curl' utility on the system PATH!");
		}

		ApplicationContext.scheduleCallback(() -> urlInput.requestFocus(), 200L);
	    }
	    catch (SerializationException se) {
		throw new RuntimeException(se);
	    }
	    catch (IOException ioe) {
		throw new RuntimeException(ioe);
	    }
	}


	public CURL() {
	    // Select the old TerraTheme (and colors)
	    System.setProperty(
		"org.apache.pivot.wtk.skin.terra.location",
		ClassUtil.getResourcePath(this) + "TerraTheme_old.json");

	    serializer = new BXMLSerializer();
	}


	public static void main(String[] args) {
	    DesktopApplicationContext.main(CURL.class, args);
	}

}
