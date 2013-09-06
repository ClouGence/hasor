package org.dev.toos.constcode.ui.view;
import java.util.List;
import org.dev.toos.constcode.model.ConstGroup;
import org.dev.toos.constcode.model.ConstModel;
import org.dev.toos.constcode.model.ConstModelSet;
import org.dev.toos.constcode.ui.handler.toolbar.IncludeLibraryActionHandler;
import org.dev.toos.constcode.ui.handler.toolbar.OpenActionHandler;
import org.dev.toos.constcode.ui.handler.toolbar.OpenXmlActionHandler;
import org.dev.toos.constcode.ui.handler.toolbar.RefreshActionHandler;
import org.dev.toos.constcode.ui.handler.toolbar.SaveActionHandler;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
/**
 * 
 * @version : 2013-2-2
 * @author 赵永春 (zyc@byshell.org)
 */
public class ConstCodeView extends ViewPart {
    private Composite                   parent                      = null;
    //
    private StackLayout                 stackLayout                 = null;
    private Label                       msgLabel                    = null;
    private ConstCodeViewPage           constCodeViewPage           = null;
    //
    private SaveActionHandler           saveActionHandler           = null;
    private RefreshActionHandler        refreshActionHandler        = null;
    private IncludeLibraryActionHandler includeLibraryActionHandler = null;
    private OpenXmlActionHandler        openXmlActionHandler        = null;
    private OpenActionHandler           openActionHandler           = null;
    //
    //
    public ConstCodeView() {}
    //
    //
    /**构建视图*/
    public void createPartControl(Composite parent) {
        this.parent = parent;
        this.stackLayout = new StackLayout();
        parent.setLayout(this.stackLayout);
        msgLabel = new Label(parent, SWT.NONE);
        msgLabel.setText("have not const xml file in this project.");
        constCodeViewPage = new ConstCodeViewPage(this, parent);
        //
        this.initMenuBar();
        this.initToolBar();
        this.hideViewPage();
    }
    /**创建上下文菜单*/
    protected void initMenuBar() {
        IMenuManager menuManager = this.getViewSite().getActionBars().getMenuManager();
    }
    /**创建视图工具条*/
    public void initToolBar() {
        IToolBarManager toolBarManager = this.getViewSite().getActionBars().getToolBarManager();
        refreshActionHandler = new RefreshActionHandler(this);
        toolBarManager.add(refreshActionHandler);
        saveActionHandler = new SaveActionHandler(this);
        toolBarManager.add(saveActionHandler);
        includeLibraryActionHandler = new IncludeLibraryActionHandler(this);
        toolBarManager.add(includeLibraryActionHandler);
        this.openXmlActionHandler = new OpenXmlActionHandler(this);
        toolBarManager.add(openXmlActionHandler);
        openActionHandler = new OpenActionHandler(this);
        toolBarManager.add(openActionHandler);
    }
    /**切换视图到主体功能*/
    public void showViewPage() {
        this.stackLayout.topControl = this.constCodeViewPage;
        this.saveActionHandler.setEnabled(true);
        this.includeLibraryActionHandler.setEnabled(true);
        this.openXmlActionHandler.setEnabled(false);
        this.openActionHandler.setEnabled(true);
        this.parent.layout(true);
    }
    /**切换视图到主体功能*/
    public void hideViewPage() {
        this.stackLayout.topControl = this.msgLabel;
        this.saveActionHandler.setEnabled(false);
        this.includeLibraryActionHandler.setEnabled(false);
        this.openXmlActionHandler.setEnabled(false);
        this.openActionHandler.setEnabled(false);
        this.parent.layout(true);
    }
    @Override
    public void setFocus() {}
    public void updataToolsBar() {
        ConstModel modelBean = ConstModelSet.getActivateModel();
        if (modelBean == null) {
            this.hideViewPage();
            return;
        } else
            this.showViewPage();
        //
        this.constCodeViewPage.getModelPath().setText(modelBean.getTitle());
        this.openActionHandler.setToolTipText("Opened Projet '" + modelBean.getTitle() + "'");
        this.includeLibraryActionHandler.setChecked(modelBean.isLoadedLibrary());
        //
        boolean constChanged = false;
        List<ConstGroup> groupList = modelBean.getGroups();
        if (groupList != null)
            for (ConstGroup groupData : groupList)
                if (groupData.isConstChanged() == true)
                    constChanged = true;
        //
        //        if (constChanged == true)
        //            this.saveActionHandler.setEnabled(true);
        //        else
        //            this.saveActionHandler.setEnabled(false);
    }
    /**更新视图，当ConstCodeModelSet.getActivateModel()有返回值时候才会更新UI。*/
    public void updataView() {
        this.updataToolsBar();
        this.constCodeViewPage.updataView();
        this.parent.layout(true);
    }
}