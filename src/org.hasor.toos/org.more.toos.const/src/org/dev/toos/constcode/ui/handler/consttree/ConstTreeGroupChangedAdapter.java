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
package org.dev.toos.constcode.ui.handler.consttree;
import org.dev.toos.constcode.model.ConstModelSet;
import org.dev.toos.constcode.ui.handler.AbstractAdapter;
import org.dev.toos.constcode.ui.view.ConstCodeViewPage;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.MenuItem;
/**
 * 
 * @version : 2013-2-3
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class ConstTreeGroupChangedAdapter extends AbstractAdapter implements SelectionListener {
    private String groupName = null;
    public ConstTreeGroupChangedAdapter(ConstCodeViewPage pageObject, String groupName) {
        super(pageObject);
        this.groupName = groupName;
    }
    @Override
    public void widgetSelected(SelectionEvent e) {
        MenuItem menuItem = (MenuItem) e.widget;
        if (menuItem.getSelection() == false)
            return;
        ConstModelSet.getActivateModel().setGroup(this.groupName);
        this.updataView();
    }
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {}
}