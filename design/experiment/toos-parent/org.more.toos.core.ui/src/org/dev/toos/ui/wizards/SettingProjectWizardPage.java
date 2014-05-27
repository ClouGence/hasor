package org.dev.toos.ui.wizards;
import org.dev.toos.ui.wizards.pages.LocationGroup;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import swing2swt.layout.BorderLayout;
import org.eclipse.swt.layout.FillLayout;
/**
 * 
 * @version : 2013-3-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class SettingProjectWizardPage extends WizardPage {
    public SettingProjectWizardPage() {
        super("Create Project");
        this.setTitle("select project for dependent.");
        this.setMessage("select project.");
    }
    @Override
    public void createControl(Composite parent) {
        Composite root = new Composite(parent, SWT.NULL);
        setControl(root);
//        LocationGroup group = new LocationGroup();
//        Composite compLocation = group.createControl(root);
//        compLocation.setLayoutData(BorderLayout.NORTH);
        root.setLayout(new GridLayout(1, false));
        
        Composite composite = new Composite(root, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        composite.setLayout(null);
        
        
        
        
        
        Group settingProject = new Group(root, SWT.NONE);
        settingProject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        settingProject.setText("Project Setting");
        settingProject.setLayout(new GridLayout(2, false));
        Label groupLabel = new Label(settingProject, SWT.NONE);
        groupLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        groupLabel.setText("Group ID：");
        Combo groupText = new Combo(settingProject, SWT.NONE);
        groupText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        Label artifactLabel = new Label(settingProject, SWT.NONE);
        artifactLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        artifactLabel.setText("Artifact ID：");
        Combo artifactText = new Combo(settingProject, SWT.NONE);
        artifactText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        Label versionLabel = new Label(settingProject, SWT.NONE);
        versionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        versionLabel.setText("Version：");
        Composite comA = new Composite(settingProject, SWT.NONE);
        comA.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        comA.setLayout(new GridLayout(3, false));
        Combo versionText = new Combo(comA, SWT.NONE);
        Label packageLabel = new Label(comA, SWT.NONE);
        packageLabel.setText("Package：");
        Combo packageText = new Combo(comA, SWT.NONE);
        packageText.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        Group settingParent = new Group(root, SWT.NONE);
        settingParent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        settingParent.setText("Parent Project");
        settingParent.setLayout(new GridLayout(2, false));
        Label pGroupLabel = new Label(settingParent, SWT.NONE);
        pGroupLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        pGroupLabel.setBounds(0, 0, 76, 20);
        pGroupLabel.setText("Group ID：");
        Combo pGroupText = new Combo(settingParent, SWT.NONE);
        pGroupText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        Label pArtifactLabel = new Label(settingParent, SWT.NONE);
        pArtifactLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        pArtifactLabel.setText("Artifact ID：");
        Combo pArtifactText = new Combo(settingParent, SWT.NONE);
        pArtifactText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        Label pVersionLabel = new Label(settingParent, SWT.NONE);
        pVersionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        pVersionLabel.setText("Version：");
        Combo pVersionText = new Combo(settingParent, SWT.NONE);
        pVersionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        settingParent.setTabList(new Control[] { pGroupText, pArtifactText, pVersionText });
        Group settingWorkingSet = new Group(root, SWT.NONE);
        settingWorkingSet.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        settingWorkingSet.setText("Working sets");
        settingWorkingSet.setLayout(new GridLayout(3, false));
        Button useWorkingSet = new Button(settingWorkingSet, SWT.CHECK);
        useWorkingSet.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
        useWorkingSet.setText("Add project to working sets");
        Label workingSetLabel = new Label(settingWorkingSet, SWT.NONE);
        workingSetLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        workingSetLabel.setText("Working sets");
        Combo workingSetText = new Combo(settingWorkingSet, SWT.NONE);
        workingSetText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        Button selectWorkingSet = new Button(settingWorkingSet, SWT.NONE);
        selectWorkingSet.setText("Select ...");
    }
    public boolean performFinish() {
        /** 完成配置 */
        return true;
    }
}