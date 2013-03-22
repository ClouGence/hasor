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
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
/**
 * 
 * @version : 2013-2-3
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class VarTreeMenuStateAdapter extends AbstractAdapter implements MenuListener {
    public VarTreeMenuStateAdapter(ConstCodeViewPage pageObject) {
        super(pageObject);
    }
    @Override
    public void menuHidden(MenuEvent e) {}
    @Override
    public void menuShown(MenuEvent e) {
        TreeViewer constTreeViewer = this.getViewPage().getConstTreeViewer();
        TreeViewer varTreeViewer = this.getViewPage().getVarTreeViewer();
        TreeSelection constSelect = (TreeSelection) constTreeViewer.getSelection();
        TreeSelection varSelect = (TreeSelection) varTreeViewer.getSelection();
        ConstBeanBridge constBridge = null;
        VarBeanBridge varBridge = null;
        if (constSelect != null && constSelect.isEmpty() == false)
            constBridge = (ConstBeanBridge) constSelect.getFirstElement();
        if (varSelect != null && varSelect.isEmpty() == false)
            varBridge = (VarBeanBridge) varSelect.getFirstElement();
        /*Add, Delete, Copy, Edit, Revert*/
        Menu menuRoot = (Menu) e.getSource();
        MenuItem[] menus = menuRoot.getItems();
        for (int i = 0; i < menus.length; i++)
            menus[i].setEnabled(true);
        //
        if (constBridge == null || constBridge.isDelete()) {
            menus[0].setEnabled(false);//add
            menus[1].setEnabled(false);//Child  
            menus[2].setEnabled(false);//delete
            menus[3].setEnabled(false);//copy
            menus[4].setEnabled(false);//edit
            menus[5].setEnabled(false);//revert
            return;
        }
        if (constBridge.readOnly() == true) {
            menus[0].setEnabled(false);//add
            menus[1].setEnabled(false);//Child
            menus[2].setEnabled(false);//delete
            //menus[3].setEnabled(false);//copy
            menus[4].setEnabled(false);//edit
            menus[5].setEnabled(false);//revert
            return;
        }
        if (varBridge == null) {
            menus[1].setEnabled(false);//Child
            menus[2].setEnabled(false);//delete
            menus[3].setEnabled(false);//copy
            menus[4].setEnabled(false);//edit
            menus[5].setEnabled(false);//revert
            return;
        }
        if (varBridge.isDelete()) {
            menus[1].setEnabled(false);//Child
            menus[2].setEnabled(false);//delete
            menus[4].setEnabled(false);//edit
        }
        if (varBridge.isPropertyChanged() == false && varBridge.isNew() == false) {
            menus[5].setEnabled(false);//revert
        }
    }
}