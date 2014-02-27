/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.dev.toos.ui.internal.ui;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
/**
 * 
 * @version : 2013-3-20
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractViewField<T> {
    /*启用字段，只有启用了的字段才能收到监听器的事件通知。*/
    private boolean            enable            = true;
    /*字段可以用于显示的标签。*/
    private Label              fieldLabel        = null;
    /*字段标签用于显示的文本内容。*/
    private String             fieldLabelText    = "";
    /*字段监听器。*/
    private IViewFieldListener viewFieldListener = null;
    //
    //
    //
    /**添加{@link IViewFieldListener}监听器*/
    public void setViewFieldListener(IViewFieldListener listener) {
        this.viewFieldListener = listener;
    }
    /**删除{@link IViewFieldListener}监听器*/
    public IViewFieldListener getViewFieldListener() {
        return this.viewFieldListener;
    }
    /**引发字段值变化事件。*/
    protected void fireOnFieldChanged() {
        if (this.viewFieldListener != null)
            this.viewFieldListener.viewFieldChanged(this);
    }
    /**字段得到焦点，子类在扩展该方法时候可以让指定的控件得到焦点。*/
    public boolean setFocus() {
        return false;
    }
    /**获取ViewField所代表的Label对象。*/
    protected Label getLabelControl(Composite parent) {
        if (this.fieldLabel == null) {
            Assert.isNotNull(parent, "uncreated control requested with composite null");
            this.fieldLabel = new Label(parent, SWT.LEFT | SWT.WRAP);
            this.fieldLabel.setFont(parent.getFont());
            this.fieldLabel.setEnabled(this.enable);
            if (this.fieldLabelText != null && !"".equals(this.fieldLabelText)) {
                this.fieldLabel.setText(this.fieldLabelText);
            } else {
                // XXX: to avoid a 16 pixel wide empty label - revisit
                this.fieldLabel.setText("."); //$NON-NLS-1$
                this.fieldLabel.setVisible(false);
            }
        }
        return this.fieldLabel;
    }
    /**获取ViewField对象的enable属性。*/
    public final boolean isEnable() {
        return this.enable;
    }
    /**设置ViewField对象的enable属性。*/
    public final void setEnable(boolean enabled) {
        if (enabled != this.enable) {
            this.enable = enabled;
            updateEnableState();
        }
    }
    /** 收到通知ViewField的enable状态属性被修改。 */
    protected void updateEnableState() {
        if (this.fieldLabel != null)
            this.fieldLabel.setEnabled(this.enable);
    }
    /*-----------------------------------------------------------------*/
    /**获取字段值*/
    public abstract T getFieldValue();
    /**获取该组建会创建多少个Controls对象。*/
    public int getNumberOfControls() {
        return 1;
    }
    /**将组建填充到目标容器中。*/
    public Control[] doFillIntoGrid(Composite parentComposite, int numColumnsCount) {
        assertEnoughColumns(numColumnsCount);
        Label label = getLabelControl(parentComposite);//获取标签对象
        label.setLayoutData(gridDataForLabel(numColumnsCount));
        return new Control[] { label };
    }
    /*-----------------------------------------------------------------*/
    /**用于检查目标容器最大列数目是否少于需要的数量，如果容器规格不足会抛出异常。*/
    protected final void assertEnoughColumns(int nColumns) {
        Assert.isTrue(nColumns >= getNumberOfControls(), "given number of columns is too small"); //$NON-NLS-1$
    }
    protected static final boolean isOkToUse(Control control) {
        return (control != null) && (Display.getCurrent() != null) && !control.isDisposed();
    }
    /**根据要求的span大小生成GridData布局管理器。*/
    protected static GridData gridDataForLabel(int span) {
        GridData gd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
        gd.horizontalSpan = span;
        return gd;
    }
}