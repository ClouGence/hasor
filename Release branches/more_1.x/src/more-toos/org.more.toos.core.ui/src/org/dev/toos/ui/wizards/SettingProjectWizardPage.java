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
/**
 * 
 * @version : 2013-3-14
 * @author ’‘”¿¥∫ (zyc@byshell.org)
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
        root.setLayout(new BorderLayout(0, 0));
        LocationGroup group = new LocationGroup();
        group.createControl(root);
        // Composite compLocation = new Composite(root, SWT.NONE);
        // compLocation.setLayoutData(BorderLayout.NORTH);
        // compLocation.setLayout(new GridLayout(3, false));
        // Button useDefaultLocation = new Button(compLocation, SWT.CHECK);
        // useDefaultLocation.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
        // false, false, 3, 1));
        // useDefaultLocation.setText("Use default location");
        // Label locationLabel = new Label(compLocation, SWT.NONE);
        // locationLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
        // false, false, 1, 1));
        // locationLabel.setText("location");
        // Text locationText = new Text(compLocation, SWT.BORDER);
        // locationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
        // false, 1, 1));
        // Button browseLocation = new Button(compLocation, SWT.NONE);
        // browseLocation.setText("Browse ...");
        Composite compProject = new Composite(root, SWT.NONE);
        compProject.setLayout(new BorderLayout(0, 0));
        Group settingProject = new Group(compProject, SWT.NONE);
        settingProject.setLayoutData(BorderLayout.NORTH);
        settingProject.setText("Project Setting");
        settingProject.setLayout(new GridLayout(2, false));
        Label groupLabel = new Label(settingProject, SWT.NONE);
        groupLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        groupLabel.setText("Group ID£∫");
        Combo groupText = new Combo(settingProject, SWT.NONE);
        groupText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        Label artifactLabel = new Label(settingProject, SWT.NONE);
        artifactLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        artifactLabel.setText("Artifact ID£∫");
        Combo artifactText = new Combo(settingProject, SWT.NONE);
        artifactText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        Label versionLabel = new Label(settingProject, SWT.NONE);
        versionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        versionLabel.setText("Version£∫");
        Composite comA = new Composite(settingProject, SWT.NONE);
        comA.setLayout(new GridLayout(3, false));
        Combo versionText = new Combo(comA, SWT.NONE);
        Label packageLabel = new Label(comA, SWT.NONE);
        packageLabel.setText("Package£∫");
        Combo packageText = new Combo(comA, SWT.NONE);
        packageText.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        Composite comB = new Composite(compProject, SWT.NONE);
        comB.setLayoutData(BorderLayout.CENTER);
        comB.setLayout(new BorderLayout(0, 0));
        Group settingParent = new Group(comB, SWT.NONE);
        settingParent.setLayoutData(BorderLayout.NORTH);
        settingParent.setText("Parent Project");
        settingParent.setLayout(new GridLayout(2, false));
        Label pGroupLabel = new Label(settingParent, SWT.NONE);
        pGroupLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        pGroupLabel.setBounds(0, 0, 76, 20);
        pGroupLabel.setText("Group ID£∫");
        Combo pGroupText = new Combo(settingParent, SWT.NONE);
        pGroupText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        Label pArtifactLabel = new Label(settingParent, SWT.NONE);
        pArtifactLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        pArtifactLabel.setText("Artifact ID£∫");
        Combo pArtifactText = new Combo(settingParent, SWT.NONE);
        pArtifactText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        Label pVersionLabel = new Label(settingParent, SWT.NONE);
        pVersionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        pVersionLabel.setText("Version£∫");
        Combo pVersionText = new Combo(settingParent, SWT.NONE);
        pVersionText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        settingParent.setTabList(new Control[] { pGroupText, pArtifactText, pVersionText });
        Group settingWorkingSet = new Group(comB, SWT.NONE);
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
        /** ÕÍ≥…≈‰÷√ */
        return true;
    }
    //
    //
    //
    // private final NameGroup fNameGroup;
    // private final LocationGroup fLocationGroup = null;
    // private final DetectGroup fDetectGroup;
    // private final Validator fValidator;
    // private final WorkingSetGroup fWorkingSetGroup = null;
    // //
    // /**
    // * Request a project name. Fires an event whenever the text field is
    // * changed, regardless of its content.
    // */
    // private final class NameGroup extends Observable implements
    // IDialogFieldListener {
    // protected final StringDialogField fNameField;
    // public NameGroup() {
    // // text field for project name
    // fNameField = new StringDialogField();
    // fNameField.setLabelText(NewWizardMessages.NewJavaProjectWizardPageOne_NameGroup_label_text);
    // fNameField.setDialogFieldListener(this);
    // }
    // public Control createControl(Composite composite) {
    // Composite nameComposite = new Composite(composite, SWT.NONE);
    // nameComposite.setFont(composite.getFont());
    // nameComposite.setLayout(new GridLayout(2, false));
    // fNameField.doFillIntoGrid(nameComposite, 2);
    // LayoutUtil.setHorizontalGrabbing(fNameField.getTextControl(null));
    // return nameComposite;
    // }
    // protected void fireEvent() {
    // setChanged();
    // notifyObservers();
    // }
    // public String getName() {
    // return fNameField.getText().trim();
    // }
    // public void postSetFocus() {
    // fNameField.postSetFocusOnDialogField(getShell().getDisplay());
    // }
    // public void setName(String name) {
    // fNameField.setText(name);
    // }
    // /* (non-Javadoc)
    // * @see
    // org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener#dialogFieldChanged(org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField)
    // */
    // public void dialogFieldChanged(DialogField field) {
    // fireEvent();
    // }
    // }
    // private final class WorkingSetGroup {
    // private WorkingSetConfigurationBlock fWorkingSetBlock;
    // public WorkingSetGroup() {
    // String[] workingSetIds = new String[] { IWorkingSetIDs.JAVA,
    // IWorkingSetIDs.RESOURCE };
    // fWorkingSetBlock = new WorkingSetConfigurationBlock(workingSetIds,
    // JavaPlugin.getDefault().getDialogSettings());
    // //fWorkingSetBlock.setDialogMessage(NewWizardMessages.NewJavaProjectWizardPageOne_WorkingSetSelection_message);
    // }
    // public Control createControl(Composite composite) {
    // Group workingSetGroup = new Group(composite, SWT.NONE);
    // workingSetGroup.setFont(composite.getFont());
    // workingSetGroup.setText(NewWizardMessages.NewJavaProjectWizardPageOne_WorkingSets_group);
    // workingSetGroup.setLayout(new GridLayout(1, false));
    // fWorkingSetBlock.createContent(workingSetGroup);
    // return workingSetGroup;
    // }
    // public void setWorkingSets(IWorkingSet[] workingSets) {
    // fWorkingSetBlock.setWorkingSets(workingSets);
    // }
    // public IWorkingSet[] getSelectedWorkingSets() {
    // return fWorkingSetBlock.getSelectedWorkingSets();
    // }
    // }
}