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
package org.dev.toos.constcode.ui.handler.vartree;
import org.dev.toos.constcode.model.bridge.ConstBeanBridge;
import org.dev.toos.constcode.model.bridge.VarBeanBridge;
import org.dev.toos.constcode.ui.handler.AbstractAdapter;
import org.dev.toos.constcode.ui.view.ConstCodeViewPage;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
/**
 * 
 * @version : 2013-2-3
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class VarTreeAddRootAdapter extends AbstractAdapter implements SelectionListener {
    public VarTreeAddRootAdapter(ConstCodeViewPage pageObject) {
        super(pageObject);
    }
    @Override
    public void widgetSelected(SelectionEvent e) {
        TreeViewer constTreeViewer = this.getViewPage().getConstTreeViewer();
        TreeViewer varTreeViewer = this.getViewPage().getVarTreeViewer();
        TreeSelection constSelect = (TreeSelection) constTreeViewer.getSelection();
        TreeSelection varSelect = (TreeSelection) varTreeViewer.getSelection();
        ConstBeanBridge atConst = null;
        VarBeanBridge beforeVar = null;
        //
        if (constSelect == null || constSelect.isEmpty() == true)
            return;
        atConst = (ConstBeanBridge) constSelect.getFirstElement();
        if (varSelect != null && varSelect.isEmpty() == false)
            beforeVar = (VarBeanBridge) varSelect.getFirstElement();
        //
        int index = -1;
        if (beforeVar != null) {
            atConst = beforeVar.getConst();
            index = atConst.getVarRoots().indexOf(beforeVar);
        }
        //
        VarBeanBridge newVar = new VarBeanBridge(atConst, null, atConst.getSource());
        atConst.addVar(index + 1, newVar);
        newVar.doEdit();
        varTreeViewer.refresh();
        varTreeViewer.setSelection(new TreeSelection(new TreePath(new Object[] { newVar })), true);
        varTreeViewer.editElement(newVar, 0);
    }
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {}
}