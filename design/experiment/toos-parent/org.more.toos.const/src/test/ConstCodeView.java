package test;
import org.dev.toos.ui.internal.ui.eclipse.wb.swt.ResourceManager;
import org.dev.toos.ui.internal.ui.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;
import swing2swt.layout.BorderLayout;
/**
 * 
 * @version : 2013-2-2
 * @author 赵永春 (zyc@byshell.org)
 */
public class ConstCodeView extends ViewPart {
    private Composite   parent      = null;
    //
    private StackLayout stackLayout = null;
    private Label       msgLabel    = null;
    private Action      action;
    //
    //
    //
    public ConstCodeView() {
        createActions();
    }
    private void createActions() {
        {
            action = new Action("New Action") {};
        }
    }
    //
    //
    /**构建视图*/
    public void createPartControl(Composite parent) {
        this.parent = parent;
        parent.setLayout(new BorderLayout(0, 0));
        ToolBar toolBar = new ToolBar(parent, SWT.FLAT | SWT.RIGHT);
        toolBar.setLayoutData(BorderLayout.WEST);
        ToolItem tltmNewItem = new ToolItem(toolBar, SWT.CHECK);
        tltmNewItem.setSelection(true);
        tltmNewItem.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/etool16/editor_area.gif"));
        tltmNewItem.setText("New Item");
        TreeViewer treeViewer = new TreeViewer(parent, SWT.BORDER);
        Tree tree = treeViewer.getTree();
        tree.setTouchEnabled(true);
        tree.setFont(SWTResourceManager.getFont("微软雅黑", 9, SWT.BOLD | SWT.ITALIC));
        tree.setLayoutData(BorderLayout.CENTER);
        tree.setLinesVisible(true);
        tree.setHeaderVisible(true);
        TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
        TreeColumn treeColumn = treeViewerColumn.getColumn();
        treeColumn.setWidth(100);
        treeColumn.setText("123");
        TreeViewerColumn treeViewerColumn_2 = new TreeViewerColumn(treeViewer, SWT.NONE);
        TreeColumn treeColumn_2 = treeViewerColumn_2.getColumn();
        treeColumn_2.setWidth(100);
        treeColumn_2.setText("567");
        TreeViewerColumn treeViewerColumn_1 = new TreeViewerColumn(treeViewer, SWT.NONE);
        TreeColumn treeColumn_1 = treeViewerColumn_1.getColumn();
        treeColumn_1.setWidth(100);
        treeColumn_1.setText("345");
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(BorderLayout.NORTH);
        composite.setLayout(new BorderLayout(0, 0));
        Combo combo = new Combo(composite, SWT.NONE);
        Button btnNewButton = new Button(composite, SWT.NONE);
        btnNewButton.setLayoutData(BorderLayout.EAST);
        btnNewButton.setText("New Button");
        //
        this.initMenuBar();
        this.initToolBar();
    }
    /**创建上下文菜单*/
    protected void initMenuBar() {
        IMenuManager menuManager = this.getViewSite().getActionBars().getMenuManager();
    }
    /**创建视图工具条*/
    public void initToolBar() {
        IToolBarManager toolBarManager = this.getViewSite().getActionBars().getToolBarManager();
    }
    @Override
    public void setFocus() {}
}