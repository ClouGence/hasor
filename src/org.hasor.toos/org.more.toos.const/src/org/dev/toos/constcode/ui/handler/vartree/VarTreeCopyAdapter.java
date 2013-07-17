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
import java.util.Iterator;
import org.dev.toos.constcode.model.bridge.VarBeanBridge;
import org.dev.toos.constcode.ui.handler.AbstractAdapter;
import org.dev.toos.constcode.ui.view.ConstCodeViewPage;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
/**
 * 
 * @version : 2013-2-3
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class VarTreeCopyAdapter extends AbstractAdapter implements SelectionListener {
    public VarTreeCopyAdapter(ConstCodeViewPage pageObject) {
        super(pageObject);
    }
    @Override
    public void widgetSelected(SelectionEvent e) {
        TreeSelection selection = (TreeSelection) this.getViewPage().getConstTreeViewer().getSelection();
        if (selection.isEmpty() == true)
            return;
        StringBuffer stringBuf = new StringBuffer("");
        Iterator<Object> iterator = selection.iterator();
        while (iterator.hasNext()) {
            VarBeanBridge bridge = (VarBeanBridge) iterator.next();
            stringBuf.append(bridge.getKey() + "\r");
        }
        Clipboard clipboard = new Clipboard(Display.getDefault());
        clipboard.setContents(new String[] { stringBuf.toString() }, new Transfer[] { TextTransfer.getInstance() });
        clipboard.dispose();
    }
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {}
}