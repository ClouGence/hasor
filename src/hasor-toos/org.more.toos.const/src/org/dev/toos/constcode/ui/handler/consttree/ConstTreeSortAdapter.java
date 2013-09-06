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
import org.dev.toos.constcode.ui.handler.AbstractAdapter;
import org.dev.toos.constcode.ui.provider.ConstSortProviderProxy;
import org.dev.toos.constcode.ui.provider.ConstSortProviderProxy.SortType;
import org.dev.toos.constcode.ui.view.ConstCodeViewPage;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
/**
 * 
 * @version : 2013-2-3
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class ConstTreeSortAdapter extends AbstractAdapter implements SelectionListener {
    public ConstTreeSortAdapter(ConstCodeViewPage pageObject) {
        super(pageObject);
    }
    @Override
    public void widgetSelected(SelectionEvent e) {
        TreeViewer treeViewer = this.getViewPage().getConstTreeViewer();
        IContentProvider provider = treeViewer.getContentProvider();
        if (provider instanceof ConstSortProviderProxy) {
            ConstSortProviderProxy sortProxy = (ConstSortProviderProxy) provider;
            if (SortType.None == sortProxy.getSortType()) {
                sortProxy.sortBy(SortType.Asc);
            } else if (SortType.Asc == sortProxy.getSortType()) {
                sortProxy.sortBy(SortType.Desc);
            } else if (SortType.Desc == sortProxy.getSortType()) {
                sortProxy.sortBy(SortType.None);
            }
            treeViewer.refresh();
        }
    }
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {}
}