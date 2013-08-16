/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package org.dev.toos.constcode.ui.handler.vartree;
import org.dev.toos.constcode.model.bridge.VarBeanBridge;
import org.dev.toos.constcode.ui.handler.AbstractAdapter;
import org.dev.toos.constcode.ui.view.ConstCodeViewPage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
/**
 * 
 * @version : 2013-2-3
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class VarTreeSelectChangedAdapter extends AbstractAdapter implements ISelectionChangedListener {
    public VarTreeSelectChangedAdapter(ConstCodeViewPage pageObject) {
        super(pageObject);
    }
    private VarBeanBridge lastElement = null;
    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        TreeViewer varTreeViewer = this.getViewPage().getVarTreeViewer();
        TreeViewer constTreeViewer = this.getViewPage().getConstTreeViewer();
        if (varTreeViewer.isCellEditorActive() == true)
            varTreeViewer.cancelEditing();
        //
        VarBeanBridge selectElement = null;
        //
        TreeSelection varTreeSelect = (TreeSelection) event.getSelection();
        if (varTreeSelect.isEmpty() == false)
            selectElement = (VarBeanBridge) varTreeSelect.getFirstElement();
        //
        if (this.lastElement != selectElement) {
            if (constTreeViewer.isCellEditorActive() == true)
                constTreeViewer.cancelEditing();
            if (lastElement != null) {
                lastElement.cancelEdit();
                constTreeViewer.refresh(this.lastElement);
            }
            this.lastElement = selectElement;
        }
    }
}