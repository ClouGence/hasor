/*
 * Copyright 2008-2009 the original author or authors.
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
package org.dev.toos.ui.internal.ui.fields;
import org.dev.toos.ui.internal.ui.AbstractViewField;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
/**
 * 
 * @version : 2013-3-20
 * @author 赵永春 (zyc@byshell.org)
 */
public class DirectoryDialogButton extends AbstractViewField<String> implements SelectionListener {
    /*----------------------------------------------------------------------------*/
    //对话框相关
    private DirectoryDialog dialog = null;
    private DirectoryDialog getDirectoryDialog(Composite parentComposite) {
        if (this.dialog != null)
            return this.dialog;
        //
        Shell shell = null;
        if (parentComposite == null)
            shell = Display.getDefault().getActiveShell();
        else
            shell = parentComposite.getShell();
        this.dialog = new DirectoryDialog(shell);
        this.dialog.setMessage("Choose Directory for the project.");
        this.dialog.setText("Choose Directory");
        return this.dialog;
    };
    /*----------------------------------------------------------------------------*/
    //文本框相关
    private Text locationText = null;
    private Text getText(Composite parentComposite) {
        if (this.locationText != null)
            return this.locationText;
        Assert.isNotNull(parentComposite, "build Text an error. parentComposite is null.");
        this.locationText = new Text(parentComposite, SWT.BORDER);
        return this.locationText;
    };
    /*----------------------------------------------------------------------------*/
    //按钮相关
    private Button dialogButton = null;
    private Button getButton(Composite parentComposite) {
        if (this.dialogButton != null)
            return this.dialogButton;
        Assert.isNotNull(parentComposite, "build Text an error. parentComposite is null.");
        this.dialogButton = new Button(parentComposite, SWT.NONE);
        this.dialogButton.setText("Browse...");
        this.dialogButton.addSelectionListener(this);
        return this.dialogButton;
    };
    /*----------------------------------------------------------------------------*/
    @Override
    public String getFieldValue() {
        return this.locationText.getText();
    }
    @Override
    public int getNumberOfControls() {
        return 3;
    }
    @Override
    public Control[] doFillIntoGrid(Composite parentComposite, int numColumnsCount) {
        assertEnoughColumns(numColumnsCount);
        //
        DirectoryDialog locationDialog = this.getDirectoryDialog(parentComposite);/*仅创建*/
        Label locationLabel = this.getLabelControl(parentComposite);
        Text locationText = this.getText(parentComposite);
        Button dialogButton = this.getButton(parentComposite);
        //
        locationLabel.setLayoutData(gridDataForLabel(1));
        locationText.setLayoutData(gridDataForLabel(numColumnsCount - 2));
        dialogButton.setLayoutData(gridDataForLabel(1));
        return new Control[] { locationLabel, locationText, dialogButton };
    }
    /*----------------------------------------------------------------------------*/
    public void widgetSelected(SelectionEvent e) {
        String oldSelectFile = this.locationText.getText();
        String newSelectFile = this.dialog.open();
        this.locationText.setText(newSelectFile);
        //如果字段值发生变化则引发事件。
        if (oldSelectFile.equals(newSelectFile) == false)
            this.fireOnFieldChanged();
    }
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {}
}