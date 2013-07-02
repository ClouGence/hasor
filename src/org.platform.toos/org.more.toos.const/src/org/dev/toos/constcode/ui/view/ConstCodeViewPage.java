package org.dev.toos.constcode.ui.view;
import java.util.List;
import org.dev.toos.constcode.model.ConstGroup;
import org.dev.toos.constcode.model.ConstModel;
import org.dev.toos.constcode.model.ConstModelSet;
import org.dev.toos.constcode.ui.editor.CC_CellModifier;
import org.dev.toos.constcode.ui.editor.CC_ConstTypeCellEditor;
import org.dev.toos.constcode.ui.editor.CV_CellModifier;
import org.dev.toos.constcode.ui.handler.consttree.ConstTreeAddAdapter;
import org.dev.toos.constcode.ui.handler.consttree.ConstTreeCopyAdapter;
import org.dev.toos.constcode.ui.handler.consttree.ConstTreeDeleteAdapter;
import org.dev.toos.constcode.ui.handler.consttree.ConstTreeDoFilterAdapter;
import org.dev.toos.constcode.ui.handler.consttree.ConstTreeDoubleClickAdapter;
import org.dev.toos.constcode.ui.handler.consttree.ConstTreeEditAdapter;
import org.dev.toos.constcode.ui.handler.consttree.ConstTreeGroupChangedAdapter;
import org.dev.toos.constcode.ui.handler.consttree.ConstTreeMenuStateAdapter;
import org.dev.toos.constcode.ui.handler.consttree.ConstTreeRevertAdapter;
import org.dev.toos.constcode.ui.handler.consttree.ConstTreeSelectChangedAdapter;
import org.dev.toos.constcode.ui.handler.consttree.ConstTreeSortAdapter;
import org.dev.toos.constcode.ui.handler.vartree.VarTreeAddChildAdapter;
import org.dev.toos.constcode.ui.handler.vartree.VarTreeAddRootAdapter;
import org.dev.toos.constcode.ui.handler.vartree.VarTreeCopyAdapter;
import org.dev.toos.constcode.ui.handler.vartree.VarTreeDeleteAdapter;
import org.dev.toos.constcode.ui.handler.vartree.VarTreeDoubleClickAdapter;
import org.dev.toos.constcode.ui.handler.vartree.VarTreeEditAdapter;
import org.dev.toos.constcode.ui.handler.vartree.VarTreeMenuStateAdapter;
import org.dev.toos.constcode.ui.handler.vartree.VarTreeRevertAdapter;
import org.dev.toos.constcode.ui.handler.vartree.VarTreeSelectChangedAdapter;
import org.dev.toos.constcode.ui.provider.ConstSortProviderProxy;
import org.dev.toos.constcode.ui.provider.ConstTreeFilter;
import org.dev.toos.constcode.ui.provider.ConstTreeProvider;
import org.dev.toos.constcode.ui.provider.ConstVarTreeProvider;
import org.dev.toos.ui.internal.ui.eclipse.wb.swt.ResourceManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import swing2swt.layout.BorderLayout;
/**
 * 
 * @version : 2013-2-2
 * @author 赵永春 (zyc@byshell.org)
 */
public class ConstCodeViewPage extends Composite {
    private ConstCodeView parentView       = null;
    /**当前正在编辑的文件*/
    private Label         modelPath;
    /**常量筛选快捷输入*/
    private Combo         constInput       = null;
    private ToolItem      includeDBGroup   = null;
    /**常量树*/
    private Tree          constTree        = null;
    /**常量树视图*/
    private TreeViewer    constTreeViewer  = null;
    /**常量值树*/
    private Tree          varTree          = null;
    /**常量值树视图*/
    private TreeViewer    varTreeViewer    = null;
    /**常量值XML编辑器*/
    private StyledText    varCodeXMLEditer = null;
    /**装载的分组*/
    private MenuItem      menuConstGroup   = null;
    //
    public ConstCodeViewPage(ConstCodeView parentView, Composite parent) {
        super(parent, SWT.NONE);
        this.parentView = parentView;
        this.initPartControl();
    }
    public ConstCodeView getParentView() {
        return this.parentView;
    }
    //
    /**构建视图*/
    public void initPartControl() {
        setLayout(new BorderLayout(0, 0));
        Composite panelTop = new Composite(this, SWT.NONE);
        panelTop.setLayoutData(BorderLayout.NORTH);
        panelTop.setLayout(new BorderLayout(5, 5));
        modelPath = new Label(panelTop, SWT.NONE);
        modelPath.setLayoutData(BorderLayout.CENTER);
        modelPath.setText("(ProjectName)......");
        SashForm panelCenter = new SashForm(this, SWT.NONE);
        panelCenter.setSashWidth(4);
        panelCenter.setLayoutData(BorderLayout.CENTER);
        Composite panelLeft = new Composite(panelCenter, SWT.NONE);
        GridLayout gl_panelLeft = new GridLayout(1, false);
        gl_panelLeft.marginHeight = 0;
        gl_panelLeft.verticalSpacing = 2;
        gl_panelLeft.marginWidth = 0;
        gl_panelLeft.horizontalSpacing = 3;
        panelLeft.setLayout(gl_panelLeft);
        Composite composite = new Composite(panelLeft, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        composite.setLayout(new BorderLayout(0, 0));
        constInput = new Combo(composite, SWT.NONE);
        constInput.addKeyListener(new ConstTreeDoFilterAdapter(this));
        constInput.addSelectionListener(new ConstTreeDoFilterAdapter(this));
        ToolBar toolBar = new ToolBar(composite, SWT.NONE);
        toolBar.setLayoutData(BorderLayout.EAST);
        includeDBGroup = new ToolItem(toolBar, SWT.CHECK);
        includeDBGroup.setImage(ResourceManager.getPluginImage("org.eclipse.datatools.connectivity.ui", "/icons/jdbc_16.gif"));
        includeDBGroup.setToolTipText("Include DB");
        //ResourceManager.getPluginImage("org.eclipse.datatools.connectivity.ui", "/icons/connection1_newwiz.gif")
        constTreeViewer = new TreeViewer(panelLeft, SWT.BORDER);
        constTreeViewer.addFilter(new ConstTreeFilter(this.constInput));
        constTreeViewer.addSelectionChangedListener(new ConstTreeSelectChangedAdapter(this));
        constTreeViewer.addDoubleClickListener(new ConstTreeDoubleClickAdapter(this));//
        constTree = constTreeViewer.getTree();
        constTree.setSortDirection(SWT.DOWN);
        constTree.setLinesVisible(true);
        constTree.setHeaderVisible(true);
        constTree.setTouchEnabled(true);
        constTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        TreeViewerColumn constTreeViewerCol_Const = new TreeViewerColumn(constTreeViewer, SWT.NONE);
        TreeColumn constTreeCol_Const = constTreeViewerCol_Const.getColumn();
        constTreeCol_Const.setWidth(200);
        constTreeCol_Const.setText("Const");
        constTreeCol_Const.setAlignment(SWT.LEFT);
        TreeViewerColumn constTreeViewerCol_Type = new TreeViewerColumn(constTreeViewer, SWT.NONE);
        TreeColumn constTreeCol_Type = constTreeViewerCol_Type.getColumn();
        constTreeCol_Type.setWidth(60);
        constTreeCol_Type.setText("Lat Type");
        //constTreeCol_Type.setResizable(false);
        constTreeCol_Type.setAlignment(SWT.LEFT);
        TreeViewerColumn constTreeViewerCol_Source = new TreeViewerColumn(constTreeViewer, SWT.NONE);
        TreeColumn constTreeCol_Source = constTreeViewerCol_Source.getColumn();
        constTreeCol_Source.setWidth(80);
        constTreeCol_Source.setText("Source");
        //constTreeCol_Source.setResizable(false);
        constTreeCol_Source.setAlignment(SWT.LEFT);
        Menu menuConst = new Menu(constTree);
        constTree.setMenu(menuConst);
        menuConst.addMenuListener(new ConstTreeMenuStateAdapter(this));
        MenuItem menuConstAdd = new MenuItem(menuConst, SWT.NONE);
        menuConstAdd.addSelectionListener(new ConstTreeAddAdapter(this));
        menuConstAdd.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/obj16/add_obj.gif"));
        menuConstAdd.setText("Add");
        MenuItem menuConstDelete = new MenuItem(menuConst, SWT.NONE);
        menuConstDelete.addSelectionListener(new ConstTreeDeleteAdapter(this));
        menuConstDelete.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/obj16/delete_obj.gif"));
        menuConstDelete.setText("Delete");
        MenuItem menuConstCopy = new MenuItem(menuConst, SWT.NONE);
        menuConstCopy.addSelectionListener(new ConstTreeCopyAdapter(this));
        menuConstCopy.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/etool16/copy_edit.gif"));
        menuConstCopy.setText("Copy");
        MenuItem menuConstEdit = new MenuItem(menuConst, SWT.NONE);
        menuConstEdit.addSelectionListener(new ConstTreeEditAdapter(this));
        menuConstEdit.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/etool16/editor_area.gif"));
        menuConstEdit.setText("Edit");
        MenuItem menuConstRecover = new MenuItem(menuConst, SWT.NONE);
        menuConstRecover.addSelectionListener(new ConstTreeRevertAdapter(this));
        menuConstRecover.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/etool16/undo_edit.gif"));
        menuConstRecover.setText("Revert");
        new MenuItem(menuConst, SWT.SEPARATOR);
        menuConstGroup = new MenuItem(menuConst, SWT.CASCADE);
        menuConstGroup.setText("Group");
        SashForm panelRight = new SashForm(panelCenter, SWT.VERTICAL);
        panelRight.setSashWidth(4);
        varTreeViewer = new TreeViewer(panelRight, SWT.BORDER);
        varTreeViewer.addSelectionChangedListener(new VarTreeSelectChangedAdapter(this));
        varTreeViewer.addDoubleClickListener(new VarTreeDoubleClickAdapter(this));//
        varTree = varTreeViewer.getTree();
        varTree.setLinesVisible(true);
        varTree.setHeaderVisible(true);
        TreeViewerColumn varTreeViewerCol_Code = new TreeViewerColumn(varTreeViewer, SWT.NONE);
        TreeColumn varTreeCol_Code = varTreeViewerCol_Code.getColumn();
        varTreeCol_Code.setWidth(100);
        varTreeCol_Code.setText("Key");
        TreeViewerColumn varTreeViewerCol_Value = new TreeViewerColumn(varTreeViewer, SWT.NONE);
        TreeColumn varTreeCol_Value = varTreeViewerCol_Value.getColumn();
        varTreeCol_Value.setWidth(100);
        varTreeCol_Value.setText("Var");
        TreeViewerColumn varTreeViewerCol_Lat = new TreeViewerColumn(varTreeViewer, SWT.NONE);
        TreeColumn varTreeCol_Lat = varTreeViewerCol_Lat.getColumn();
        varTreeCol_Lat.setWidth(100);
        varTreeCol_Lat.setText("Lat");
        Menu menuVar = new Menu(varTree);
        varTree.setMenu(menuVar);
        menuVar.addMenuListener(new VarTreeMenuStateAdapter(this));
        MenuItem menuVarAdd = new MenuItem(menuVar, SWT.NONE);
        menuVarAdd.addSelectionListener(new VarTreeAddRootAdapter(this));
        menuVarAdd.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/obj16/add_obj.gif"));
        menuVarAdd.setText("Add Root");
        MenuItem menuVarAddChild = new MenuItem(menuVar, SWT.NONE);
        menuVarAddChild.addSelectionListener(new VarTreeAddChildAdapter(this));
        menuVarAddChild.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/obj16/add_obj.gif"));
        menuVarAddChild.setText("Add Children");
        MenuItem menuVarDelete = new MenuItem(menuVar, SWT.NONE);
        menuVarDelete.addSelectionListener(new VarTreeDeleteAdapter(this));
        menuVarDelete.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/obj16/delete_obj.gif"));
        menuVarDelete.setText("Delete");
        MenuItem menuVarCopy = new MenuItem(menuVar, SWT.NONE);
        menuVarCopy.addSelectionListener(new VarTreeCopyAdapter(this));
        menuVarCopy.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/etool16/copy_edit.gif"));
        menuVarCopy.setText("Copy");
        MenuItem menuVarEdit = new MenuItem(menuVar, SWT.NONE);
        menuVarEdit.addSelectionListener(new VarTreeEditAdapter(this));
        menuVarEdit.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/etool16/editor_area.gif"));
        menuVarEdit.setText("Edit");
        MenuItem menuVarRecover = new MenuItem(menuVar, SWT.NONE);
        menuVarRecover.addSelectionListener(new VarTreeRevertAdapter(this));
        menuVarRecover.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/etool16/undo_edit.gif"));
        menuVarRecover.setText("Revert");
        varCodeXMLEditer = new StyledText(panelRight, SWT.BORDER);
        Menu menuXml = new Menu(varCodeXMLEditer);
        varCodeXMLEditer.setMenu(menuXml);
        //        MenuItem menuXmlCut = new MenuItem(menuXml, SWT.NONE);
        //        menuXmlCut.addSelectionListener(new MenuXml_CutAdapter(this));
        //        menuXmlCut.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/etool16/cut_edit.gif"));
        //        menuXmlCut.setText("Cut");
        //        MenuItem menuXmlCopy = new MenuItem(menuXml, SWT.NONE);
        //        menuXmlCopy.addSelectionListener(new MenuXml_CopyAdapter(this));
        //        menuXmlCopy.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/etool16/copy_edit.gif"));
        //        menuXmlCopy.setText("Copy");
        //        MenuItem menuXmlPaste = new MenuItem(menuXml, SWT.NONE);
        //        menuXmlPaste.addSelectionListener(new MenuXml_PasteAdapter(this));
        //        menuXmlPaste.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/etool16/paste_edit.gif"));
        //        menuXmlPaste.setText("Paste");
        //        MenuItem menuXmlDelete = new MenuItem(menuXml, SWT.NONE);
        //        menuXmlDelete.addSelectionListener(new MenuXml_DeleteAdapter(this));
        //        menuXmlDelete.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/etool16/delete.gif"));
        //        menuXmlDelete.setText("Delete");
        //        new MenuItem(menuXml, SWT.SEPARATOR);
        //        MenuItem menuXmlApply = new MenuItem(menuXml, SWT.NONE);
        //        menuXmlApply.addSelectionListener(new MenuXml_ApplyAdapter(this));
        //        menuXmlApply.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/elcl16/synced.gif"));
        //        menuXmlApply.setText("Apply");
        panelRight.setWeights(new int[] { 80, 20 });
        panelCenter.setWeights(new int[] { 182, 311 });
        //
        //
        constTreeViewer.setColumnProperties(new String[] { "Code", "LatType" });
        constTreeViewer.setCellModifier(new CC_CellModifier(constTreeViewer));
        constTreeViewer.setCellEditors(new CellEditor[] { new TextCellEditor(constTree), new CC_ConstTypeCellEditor(constTree) });
        constTreeViewer.setContentProvider(new ConstSortProviderProxy(new ConstTreeProvider()));
        constTreeViewer.setLabelProvider(new ConstTreeProvider());
        constTreeCol_Const.addSelectionListener(new ConstTreeSortAdapter(this));
        //
        varTreeViewer.setColumnProperties(new String[] { "Key", "Var", "Lat" });
        varTreeViewer.setCellModifier(new CV_CellModifier(varTreeViewer));
        varTreeViewer.setCellEditors(new CellEditor[] { new TextCellEditor(varTree), new TextCellEditor(varTree), new TextCellEditor(varTree) });
        varTreeViewer.setContentProvider(new ConstVarTreeProvider());
        varTreeViewer.setLabelProvider(new ConstVarTreeProvider());
    }
    public void updataView() {
        ConstModel modelBean = ConstModelSet.getActivateModel();
        if (modelBean == null)
            return;
        //
        List<ConstGroup> groupList = modelBean.getGroups();
        ConstGroup currentGroup = modelBean.getCurrentGroup();
        String currentGroupName = (currentGroup != null) ? currentGroup.getName() : "";
        //
        Menu menuConstGroupBody = new Menu(menuConstGroup);
        menuConstGroup.setMenu(menuConstGroupBody);
        MenuItem menuConstGroup_default = new MenuItem(menuConstGroupBody, SWT.RADIO);
        menuConstGroup_default.setSelection(currentGroup == null);
        menuConstGroup_default.setText("(ALL)");
        menuConstGroup_default.addSelectionListener(new ConstTreeGroupChangedAdapter(this, null));
        if (groupList != null)
            for (ConstGroup groupData : groupList) {
                MenuItem menuConstGroup_item = new MenuItem(menuConstGroupBody, SWT.RADIO);
                String title = (groupData.isReadOnly() == true) ? "(ReadOnly) " : "";
                title += groupData.getName();
                menuConstGroup_item.setSelection(groupData.getName().equals(currentGroupName));
                menuConstGroup_item.setText(title);
                menuConstGroup_item.addSelectionListener(new ConstTreeGroupChangedAdapter(this, groupData.getName()));
            }
        varTreeViewer.setInput(null);
        varTreeViewer.refresh();
        constTreeViewer.setInput(currentGroupName);
        constTreeViewer.refresh();
        //
        //
    }
    //
    //
    //
    //
    public Label getModelPath() {
        return modelPath;
    }
    public Combo getConstInput() {
        return constInput;
    }
    public Tree getConstTree() {
        return constTree;
    }
    public TreeViewer getConstTreeViewer() {
        return constTreeViewer;
    }
    public Tree getVarTree() {
        return varTree;
    }
    public TreeViewer getVarTreeViewer() {
        return varTreeViewer;
    }
    public StyledText getVarCodeXMLEditer() {
        return varCodeXMLEditer;
    }
    public MenuItem getMenuConstGroup() {
        return menuConstGroup;
    }
}