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
/**
 * 
 * @version : 2013-3-20
 * @author 赵永春 (zyc@byshell.org)
 */
public class SelectionButton extends AbstractViewField<Boolean> implements SelectionListener {
    private String  text         = ".";
    private Boolean selection    = false;
    /*----------------------------------------------------------------------------*/
    //按钮相关
    private Button  dialogButton = null;
    private Button getButton(Composite parentComposite) {
        if (this.dialogButton != null)
            return this.dialogButton;
        Assert.isNotNull(parentComposite, "build Text an error. parentComposite is null.");
        this.dialogButton = new Button(parentComposite, SWT.CHECK);
        this.dialogButton.setText(this.text);
        this.dialogButton.setSelection(this.selection);
        this.dialogButton.addSelectionListener(this);
        return this.dialogButton;
    };
    /*----------------------------------------------------------------------------*/
    @Override
    public int getNumberOfControls() {
        return 1;
    }
    @Override
    public Control[] doFillIntoGrid(Composite parentComposite, int numColumnsCount) {
        assertEnoughColumns(numColumnsCount);
        //
        Button button = this.getButton(parentComposite);
        //
        button.setLayoutData(gridDataForLabel(numColumnsCount));
        return new Control[] { button };
    }
    public Boolean getFieldValue() {
        return this.selection;
    }
    public boolean getSelection() {
        return this.getFieldValue();
    }
    public void setSelection(boolean selection) {
        this.selection = selection;
        if (this.dialogButton != null)
            this.dialogButton.setSelection(this.selection);
        else
            fireOnFieldChanged();//如果对象还未创建则仅仅引发事件。
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
        if (isOkToUse(this.dialogButton) == true)
            this.dialogButton.setText(text);
    }
    /*----------------------------------------------------------------------------*/
    public void widgetSelected(SelectionEvent e) {
        Boolean selectValue = this.selection;
        this.selection = this.dialogButton.getSelection();
        //如果字段值发生变化则引发事件。
        if (this.selection.equals(selectValue) == false)
            fireOnFieldChanged();
    }
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {}
}