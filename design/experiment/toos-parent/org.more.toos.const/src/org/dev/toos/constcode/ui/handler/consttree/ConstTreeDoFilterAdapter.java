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
package org.dev.toos.constcode.ui.handler.consttree;
import org.dev.toos.constcode.ui.handler.AbstractAdapter;
import org.dev.toos.constcode.ui.view.ConstCodeViewPage;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
/**
 * 
 * @version : 2013-2-3
 * @author 赵永春 (zyc@byshell.org)
 */
public class ConstTreeDoFilterAdapter extends AbstractAdapter implements KeyListener, SelectionListener {
    public ConstTreeDoFilterAdapter(ConstCodeViewPage pageObject) {
        super(pageObject);
    }
    private void doEvent(Combo comboe) {
        String textStr = comboe.getText().trim();
        boolean mark = false;
        for (int i = 0; i < comboe.getItemCount(); i++) {
            String itemStr = comboe.getItem(i);
            if (textStr.toLowerCase().endsWith(itemStr.toLowerCase()) == true)
                mark = true;
        }
        if (mark == false) {
            if (comboe.getItemCount() > 15)
                comboe.remove(comboe.getItemCount() - 1);
            comboe.add(comboe.getText(), 0);
        }
        this.updataView();
    }
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.character != '\r')
            return;
        doEvent((Combo) e.getSource());
    }
    @Override
    public void widgetSelected(SelectionEvent e) {
        doEvent((Combo) e.getSource());
    }
    //
    @Override
    public void keyPressed(KeyEvent e) {}
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {}
}