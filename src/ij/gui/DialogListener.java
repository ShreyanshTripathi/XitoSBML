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
package ij.gui;
import java.awt.AWTEvent;

// TODO: Auto-generated Javadoc
/**
 * PlugIns or PlugInFilters that want to listen to changes in a GenericDialog
 * without adding listeners for each dialog field should implementthis method.
 * The dialogItemChanged method of a PlugIn or PlugInFilter can and should read
 * the various dialog items by the appropriate GenericDialog methods like
 * getNextNumber (items that are not read in the dialogItemChanged method will
 * not be recorded by the Macro recorder).
 * 
 * The PlugIn or PlugInFilter has to be added to the GenericDialog by
 * its addDialogListener method:
 * gd.addDialogListener(this);
 *
 * @see DialogEvent
 */
public interface DialogListener {

    /**
     * This method is invoked by a Generic Dialog if any of the inputs have changed
     *  (CANCEL does not trigger it; OK and running the dialog from a macro only
     *   trigger the first DialogListener added to a GenericDialog).
     *
     * @param gd  A reference to the GenericDialog.
     * @param e   The event that has been generated by the user action in the dialog.
     *            Note that <code>e</code> is <code>null</code> if the
     *            dialogItemChanged method is called after the user has pressed the
     *            OK button or if the GenericDialog has read its parameters from a
     *            macro.
     * @return    Should be true if the dialog input is valid. False disables the
     *              OK button and preview (if any).
     */
    boolean dialogItemChanged(GenericDialog gd, AWTEvent e);
}