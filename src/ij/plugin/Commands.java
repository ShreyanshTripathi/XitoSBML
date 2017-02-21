/*******************************************************************************
 * Copyright 2015 Kaito Ii
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package ij.plugin;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.Undo;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.ImageWindow;
import ij.gui.NewImage;
import ij.io.FileSaver;
import ij.io.Opener;
import ij.macro.Interpreter;
import ij.plugin.frame.PlugInDialog;
import ij.plugin.frame.PlugInFrame;
import ij.plugin.frame.Recorder;
import ij.text.TextWindow;

import java.applet.Applet;
import java.awt.Window;
import java.io.File;
	
// TODO: Auto-generated Javadoc
/**	Runs miscellaneous File and Window menu commands. */
public class Commands implements PlugIn {
	
	/* (non-Javadoc)
	 * @see ij.plugin.PlugIn#run(java.lang.String)
	 */
	public void run(String cmd) {
		if (cmd.equals("new")) {
			if (IJ.altKeyDown())
				IJ.runPlugIn("ij.plugin.HyperStackMaker", "");
			else
				new NewImage();
		} else if (cmd.equals("open")) {
			if (Prefs.useJFileChooser && !IJ.macroRunning())
				new Opener().openMultiple();
			else
				new Opener().open();
		} else if (cmd.equals("close"))
			close();
		else if (cmd.equals("close-all"))
			closeAll();
		else if (cmd.equals("save"))
			save();
		else if (cmd.equals("revert"))
			revert();
		else if (cmd.equals("undo"))
			undo();
		else if (cmd.equals("ij")) {
			ImageJ ij = IJ.getInstance();
			if (ij!=null) ij.toFront();
		} else if (cmd.equals("tab"))
			WindowManager.putBehind();
		else if (cmd.equals("quit")) {
			ImageJ ij = IJ.getInstance();
			if (ij!=null) ij.quit();
		} else if (cmd.equals("startup"))
			openStartupMacros();
    }
    
    /**
     * Revert.
     */
    void revert() {
    	ImagePlus imp = WindowManager.getCurrentImage();
		if (imp!=null)
			imp.revert();
		else
			IJ.noImage();
	}

    /**
     * Save.
     */
    void save() {
    	ImagePlus imp = WindowManager.getCurrentImage();
		if (imp!=null) {
			if (imp.getStackSize()>1) {
				imp.setIgnoreFlush(true);
				new FileSaver(imp).save();
				imp.setIgnoreFlush(false);
			} else
				new FileSaver(imp).save();
		} else
			IJ.noImage();
	}
	
    /**
     * Undo.
     */
    void undo() {
    	ImagePlus imp = WindowManager.getCurrentImage();
		if (imp!=null)
			Undo.undo();
		else
			IJ.noImage();
	}

	/**
	 * Close.
	 */
	void close() {
    	ImagePlus imp = WindowManager.getCurrentImage();
		Window win = WindowManager.getActiveWindow();
		if (win==null || (Interpreter.isBatchMode() && win instanceof ImageWindow))
			closeImage(imp);
		else if (win instanceof PlugInFrame)
			((PlugInFrame)win).close();
		else if (win instanceof PlugInDialog)
			((PlugInDialog)win).close();
		else if (win instanceof TextWindow)
			((TextWindow)win).close();
		else
			closeImage(imp);
	}

	/**
	 *  Closes all image windows, or returns 'false' if the user cancels the unsaved changes dialog box.
	 *
	 * @return true, if successful
	 */
	public static boolean closeAll() {
    	int[] list = WindowManager.getIDList();
    	if (list!=null) {
    		int imagesWithChanges = 0;
			for (int i=0; i<list.length; i++) {
				ImagePlus imp = WindowManager.getImage(list[i]);
				if (imp!=null && imp.changes) imagesWithChanges++;
			}
			if (imagesWithChanges>0 && !IJ.macroRunning()) {
				GenericDialog gd = new GenericDialog("Close All");
				String msg = null;
				String pronoun = null;
				if (imagesWithChanges==1) {
					msg = "There is one image";
					pronoun = "It";
				} else {
					msg = "There are "+imagesWithChanges+" images";
					pronoun = "They";
				}
				gd.addMessage(msg+" with unsaved changes. "+pronoun
					+" will\nbe closed without being saved if you click \"OK\".");
				gd.showDialog();
				if (gd.wasCanceled())	
					return false;
			}
			for (int i=0; i<list.length; i++) {
				ImagePlus imp = WindowManager.getImage(list[i]);
				if (imp!=null) {
					imp.changes = false;
					imp.close();
				}
			}
    	}
    	return true;
	}

	/**
	 * Close image.
	 *
	 * @param imp the imp
	 */
	void closeImage(ImagePlus imp) {
		if (imp==null) {
			IJ.noImage();
			return;
		}
		imp.close();
		if (Recorder.record && !IJ.isMacro()) {
			if (Recorder.scriptMode())
				Recorder.recordCall("imp.close();");
			else
				Recorder.record("close");
			Recorder.setCommand(null); // don't record run("Close")
		}
	}
	
	/**
	 * Open startup macros.
	 */
	// Plugins>Macros>Open Startup Macros command
	void openStartupMacros() {
		Applet applet = IJ.getApplet();
		if (applet!=null) {
			IJ.run("URL...", "url="+IJ.URL+"/applet/StartupMacros.txt");
		} else {
			String path = IJ.getDirectory("macros")+"StartupMacros.txt";
			File f = new File(path);
			if (!f.exists()) {
				path = IJ.getDirectory("macros")+"StartupMacros.ijm";
				f = new File(path);
			}
			if (!f.exists())
				IJ.error("\"StartupMacros.txt\" not found in ImageJ/macros/");
			else
				IJ.open(path);
		}
	}
		
}


