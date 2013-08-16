/*
 * Copyright 2008-2009 the original ÕÔÓÀ´º(zyc@hasor.net).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dev.toos.ui.wizards.pages;
import java.util.Observable;
import java.util.Observer;
import org.dev.toos.ui.internal.ui.fields.DirectoryDialogButton;
import org.dev.toos.ui.internal.ui.fields.SelectionButton;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
/**
 * 
 * @version : 2013-3-21
 * @author ÕÔÓÀ´º (zyc@byshell.org)
 */
public class LocationGroup extends Observable implements Observer {
    protected SelectionButton       useDefaults;
    protected DirectoryDialogButton locationButton;
    //
    //
    //
    public Composite doFillIntoGrid(Composite composite, int numColumns) {
        this.useDefaults = new SelectionButton();
        this.useDefaults.setText("Use default location");
        this.useDefaults.setSelection(true);
        this.locationButton = new DirectoryDialogButton();
        //
        // final int numColumns = 3;
        final Composite compLocation = new Composite(composite, SWT.NONE);
        compLocation.setLayout(new GridLayout(numColumns, false));
        this.useDefaults.doFillIntoGrid(composite, numColumns);
        this.locationButton.doFillIntoGrid(composite, numColumns);
        // Button useDefaultLocation = new Button(compLocation, SWT.CHECK);
        // useDefaultLocation.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        //
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        return compLocation;
        //
        //
        //
        // Label locationLabel = new Label(compLocation, SWT.NONE);
        // locationLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
        // false, false, 1, 1));
        // locationLabel.setText("location");
        // Text locationText = new Text(compLocation, SWT.BORDER);
        // locationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
        // false, 1, 1));
        // Button browseLocation = new Button(compLocation, SWT.NONE);
        // browseLocation.setText("Browse ...");
        //
        //
        // final Composite locationComposite = new Composite(composite,
        // SWT.NONE);
        // locationComposite.setLayout(new GridLayout(numColumns, false));
        // fUseDefaults.doFillIntoGrid(locationComposite, numColumns);
        // fLocation.doFillIntoGrid(locationComposite, numColumns);
        // LayoutUtil.setHorizontalGrabbing(fLocation.getTextControl(null));
        // return locationComposite;
    }
    @Override
    public void update(Observable o, Object arg) {
        // TODO Auto-generated method stub
    }
    // protected void fireEvent() {
    // setChanged();
    // notifyObservers();
    // }
    // protected String getDefaultPath(String name) {
    // final IPath path = Platform.getLocation().append(name);
    // return path.toOSString();
    // }
    // /* (non-Javadoc)
    // * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
    // */
    // public void update(Observable o, Object arg) {
    // if (isUseDefaultSelected()) {
    // fLocation.setText(getDefaultPath(fNameGroup.getName()));
    // }
    // fireEvent();
    // }
    // public IPath getLocation() {
    // if (isUseDefaultSelected()) {
    // return Platform.getLocation();
    // }
    // return Path.fromOSString(fLocation.getText().trim());
    // }
    // public boolean isUseDefaultSelected() {
    // return fUseDefaults.isSelected();
    // }
    // public void setLocation(IPath path) {
    // fUseDefaults.setSelection(path == null);
    // if (path != null) {
    // fLocation.setText(path.toOSString());
    // } else {
    // fLocation.setText(getDefaultPath(fNameGroup.getName()));
    // }
    // fireEvent();
    // }
    // /* (non-Javadoc)
    // * @see
    // org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter#changeControlPressed(org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField)
    // */
    // public void changeControlPressed(DialogField field) {
    // final DirectoryDialog dialog = new DirectoryDialog(getShell());
    // dialog.setMessage(NewWizardMessages.NewJavaProjectWizardPageOne_directory_message);
    // String directoryName = fLocation.getText().trim();
    // if (directoryName.length() == 0) {
    // String prevLocation =
    // JavaPlugin.getDefault().getDialogSettings().get(DIALOGSTORE_LAST_EXTERNAL_LOC);
    // if (prevLocation != null) {
    // directoryName = prevLocation;
    // }
    // }
    // if (directoryName.length() > 0) {
    // final File path = new File(directoryName);
    // if (path.exists())
    // dialog.setFilterPath(directoryName);
    // }
    // final String selectedDirectory = dialog.open();
    // if (selectedDirectory != null) {
    // String oldDirectory = new Path(fLocation.getText().trim()).lastSegment();
    // fLocation.setText(selectedDirectory);
    // String lastSegment = new Path(selectedDirectory).lastSegment();
    // if (lastSegment != null && (fNameGroup.getName().length() == 0 ||
    // fNameGroup.getName().equals(oldDirectory))) {
    // fNameGroup.setName(lastSegment);
    // }
    // JavaPlugin.getDefault().getDialogSettings().put(DIALOGSTORE_LAST_EXTERNAL_LOC,
    // selectedDirectory);
    // }
    // }
    // /* (non-Javadoc)
    // * @see
    // org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener#dialogFieldChanged(org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField)
    // */
    // public void dialogFieldChanged(DialogField field) {
    // if (field == fUseDefaults) {
    // final boolean checked = fUseDefaults.isSelected();
    // if (checked) {
    // fPreviousExternalLocation = fLocation.getText();
    // fLocation.setText(getDefaultPath(fNameGroup.getName()));
    // fLocation.setEnabled(false);
    // } else {
    // fLocation.setText(fPreviousExternalLocation);
    // fLocation.setEnabled(true);
    // }
    // }
    // fireEvent();
    // }
}