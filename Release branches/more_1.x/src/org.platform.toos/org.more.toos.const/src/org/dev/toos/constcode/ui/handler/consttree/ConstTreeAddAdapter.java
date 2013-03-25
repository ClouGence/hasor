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
import org.dev.toos.constcode.model.bridge.ConstBeanBridge;
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
public class ConstTreeAddAdapter extends AbstractAdapter implements SelectionListener {
    public ConstTreeAddAdapter(ConstCodeViewPage pageObject) {
        super(pageObject);
    }
    @Override
    public void widgetSelected(SelectionEvent e) {
        TreeViewer treeViewer = this.getViewPage().getConstTreeViewer();
        TreeSelection selection = (TreeSelection) treeViewer.getSelection();
        ConstGroup atGroup = null;
        int index = -1;
        if (selection.isEmpty() == true) {
            atGroup = ConstModelSet.getActivateModel().getCurrentGroup();
            if (atGroup == null)
                atGroup = ConstModelSet.getActivateModel().getGroups().get(0);
            if (atGroup.isReadOnly() == true)
                return;
        } else {
            ConstBeanBridge constElement = (ConstBeanBridge) selection.getFirstElement();
            atGroup = constElement.getSource();
            index = constElement.getSource().constList().indexOf(constElement);
        }
        //
        ConstBeanBridge newConst = new ConstBeanBridge(null, atGroup);
        atGroup.addConst(index + 1, newConst);
        treeViewer.refresh();
        treeViewer.setSelection(new TreeSelection(new TreePath(new Object[] { newConst })), true);
        newConst.doEdit();
        treeViewer.editElement(newConst, 0);
    }
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {}
}