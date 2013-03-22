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
package org.dev.toos.constcode.ui.handler.toolbar;
import java.util.List;
import org.dev.toos.constcode.model.ConstModel;
import org.dev.toos.constcode.model.ConstModelSet;
import org.dev.toos.constcode.ui.handler.AbstractHandler;
import org.dev.toos.constcode.ui.view.ConstCodeView;
import org.dev.toos.ui.internal.ui.eclipse.wb.swt.ResourceManager;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
/**
 * 
 * @version : 2013-2-2
 * @author 赵永春 (zyc@byshell.org)
 */
public class OpenActionHandler extends AbstractHandler implements IMenuCreator {
    private MenuManager dropDownMenuMgr = null;
    //
    public OpenActionHandler(ConstCodeView uiView) {
        super("Open Xml", uiView);
        this.setImageDescriptor(ResourceManager.getPluginImageDescriptor("org.eclipse.ui", "/icons/full/obj16/fldr_obj.gif"));
        this.setMenuCreator(this);
        this.createDropDownMenuMgr();
    }
    private void createDropDownMenuMgr() {
        if (this.dropDownMenuMgr == null) {
            this.dropDownMenuMgr = new MenuManager();
            this.dropDownMenuMgr.setRemoveAllWhenShown(true);//所有菜单全部重新装载
            this.dropDownMenuMgr.addMenuListener(new MenuListener(this));
        }
    }
    @Override
    public void dispose() {
        if (dropDownMenuMgr != null) {
            dropDownMenuMgr.dispose();
            dropDownMenuMgr = null;
        }
    }
    @Override
    public Menu getMenu(Control parent) { //在工具栏被调用
        createDropDownMenuMgr();
        return this.dropDownMenuMgr.createContextMenu(parent);
    }
    @Override
    public Menu getMenu(Menu parent) { //在菜单栏被调用
        createDropDownMenuMgr();
        Menu menu = new Menu(parent);
        IContributionItem[] items = dropDownMenuMgr.getItems();
        for (int i = 0; i < items.length; i++) {
            IContributionItem item = items[i];
            IContributionItem newItem = item;
            if (item instanceof ActionContributionItem) {
                newItem = new ActionContributionItem(((ActionContributionItem) item).getAction());
            }
            newItem.fill(menu, -1);
        }
        return menu;
    }
    @Override
    public void run() {
        List<ConstModel> listModel = ConstModelSet.getModeBeanList();
        int index = listModel.indexOf(ConstModelSet.getActivateModel());
        index++;
        if (index >= listModel.size())
            index = 0;
        String projectName = listModel.get(index).getProjectName();
        ConstModelSet.activateModel(projectName);
        this.updataView();
    }
    //
    //
    //
    /**当重新创建菜单时的监听器*/
    private class MenuListener implements IMenuListener {
        private OpenActionHandler actionHandler = null;
        public MenuListener(OpenActionHandler actionHandler) {
            this.actionHandler = actionHandler;
        }
        @Override
        public void menuAboutToShow(IMenuManager manager) {
            for (ConstModel model : ConstModelSet.getModeBeanList())
                manager.add(new ModeItemAction(this.actionHandler, model));
        }
    }
    /**菜单项目*/
    class ModeItemAction extends AbstractHandler {
        private ConstModel currentModel = null;
        //
        public ModeItemAction(OpenActionHandler openActionHandler, ConstModel currentModel) {
            super(currentModel.getTitle(), openActionHandler.getUiView());
            this.currentModel = currentModel;
            ConstModel activateModel = ConstModelSet.getActivateModel();
            if (activateModel != null)
                if (activateModel == currentModel)
                    this.setChecked(true);
            this.setText(currentModel.getTitle());
        }
        @Override
        public void run() {
            ConstModelSet.activateModel(this.currentModel.getProjectName());
            this.updataView();
        }
    }
}