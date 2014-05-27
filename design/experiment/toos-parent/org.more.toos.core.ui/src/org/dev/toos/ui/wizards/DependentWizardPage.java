package org.dev.toos.ui.wizards;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
/**
 * 
 * @version : 2013-3-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class DependentWizardPage extends WizardPage {
    protected DependentWizardPage() {
        super("Create Project");
        this.setTitle("select project for dependent.");
        this.setMessage("select project.");
    }
    @Override
    public void createControl(Composite parent) {
        Composite root = new Composite(parent, SWT.NULL);
        setControl(root);
        root.setLayout(new FillLayout(SWT.VERTICAL));
        CheckboxTreeViewer dependencyTreeViewer = new CheckboxTreeViewer(root, SWT.BORDER);
        Tree dependencyTree = dependencyTreeViewer.getTree();
        dependencyTree.setHeaderVisible(true);
        TreeViewerColumn treeViewerColumn = new TreeViewerColumn(dependencyTreeViewer, SWT.NONE);
        TreeColumn colProject = treeViewerColumn.getColumn();
        colProject.setWidth(300);
        colProject.setText("Project");
        TreeViewerColumn treeViewerColumn_1 = new TreeViewerColumn(dependencyTreeViewer, SWT.NONE);
        TreeColumn colHead = treeViewerColumn_1.getColumn();
        colHead.setWidth(150);
        colHead.setText("HEAD");
        Group configGroup = new Group(root, SWT.NONE);
        configGroup.setText("Config Services");
        RowLayout rl_configGroup = new RowLayout(SWT.VERTICAL);
        rl_configGroup.fill = true;
        configGroup.setLayout(rl_configGroup);
        Button constService = new Button(configGroup, SWT.CHECK);
        constService.setSelection(true);
        constService.setText("use ConstCode Service");
        Button safetyService = new Button(configGroup, SWT.CHECK);
        safetyService.setSelection(true);
        safetyService.setText("use Safety Service");
        Button dbMappingService = new Button(configGroup, SWT.CHECK);
        dbMappingService.setSelection(true);
        dbMappingService.setText("use DBMapping Service");
    }
    public boolean performFinish() {
        /**完成配置*/
        return true;
    }
}