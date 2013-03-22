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
package org.dev.toos.constcode.ui.handler.consttree;
import org.dev.toos.constcode.model.ConstGroup;
import org.dev.toos.constcode.model.ConstModelSet;
import org.dev.toos.constcode.model.bridge.AbstractBridge;
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
public class ConstTreeMenuStateAdapter extends AbstractAdapter implements MenuListener {
    public ConstTreeMenuStateAdapter(ConstCodeViewPage pageObject) {
        super(pageObject);
    }
    @Override
    public void menuHidden(MenuEvent e) {}
    @Override
    public void menuShown(MenuEvent e) {
        TreeViewer treeViewer = this.getViewPage().getConstTreeViewer();
        TreeSelection selection = (TreeSelection) treeViewer.getSelection();
        AbstractBridge bridge = null;
        ConstGroup atGroup = ConstModelSet.getActivateModel().getCurrentGroup();
        if (selection != null)
            bridge = (AbstractBridge) selection.getFirstElement();
        /*Add, Delete, Copy, Edit, Revert, |, Group*/
        Menu menuRoot = (Menu) e.getSource();
        MenuItem[] menus = menuRoot.getItems();
        for (int i = 0; i < menus.length; i++)
            menus[i].setEnabled(true);
        //
        if (selection.size() > 1)
            menus[3].setEnabled(false);//edit
        if (bridge == null) {
            if (atGroup != null && atGroup.isReadOnly())
                menus[0].setEnabled(false);//add
            menus[1].setEnabled(false);//delete
            menus[2].setEnabled(false);//copy
            menus[3].setEnabled(false);//edit
            menus[4].setEnabled(false);//revert
            return;
        }
        if (bridge.readOnly() == true) {
            menus[1].setEnabled(false);//delete
            menus[3].setEnabled(false);//edit
            menus[4].setEnabled(false);//revert
        }
        if (bridge.isPropertyChanged() == false && bridge.isNew() == false) {
            menus[4].setEnabled(false);//revert
        }
        if (bridge.isDelete() == true) {
            menus[1].setEnabled(false);//delete
            menus[2].setEnabled(false);//copy
            menus[3].setEnabled(false);//edit
        }
        if (bridge.getSource().isReadOnly() == true) {
            menus[0].setEnabled(false);//add
        }
    }
}