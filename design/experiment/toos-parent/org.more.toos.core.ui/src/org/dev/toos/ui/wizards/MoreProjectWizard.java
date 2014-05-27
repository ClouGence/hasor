package org.dev.toos.ui.wizards;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
/**
 * 创建项目JavaProjectWizard
 * 
 * @version : 2013-3-14
 * @author 赵永春 (zyc@byshell.org)
 */
public class MoreProjectWizard extends Wizard implements INewWizard {
    // protected MavenProjectWizardLocationPage locationPage; /*选择位置*/
    // protected MavenProjectWizardArchetypePage archetypePage; /*模板选择*/
    // protected MavenProjectWizardArtifactPage artifactPage; /*项目配置*/
    // protected MavenProjectWizardArchetypeParametersPage parametersPage;
    // /*参数配置*/
    protected DependentWizardPage      configProjectPage           = null; /* noe项目配置 */
    protected SettingProjectWizardPage settingNewProjectWizardPage = null; /* noe项目配置 */
    //
    //
    public MoreProjectWizard() {
        this.setWindowTitle("New Noe Project");
    }
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        // TODO Auto-generated method stub
    }
    @Override
    public void addPages() {
        // MavenPlugin.getProjectConfigurationManager().createSimpleProject(arg0,
        // arg1, arg2, arg3, arg4, arg5);
        /* 在最后选择依赖库 */
        if (this.configProjectPage == null)
            this.configProjectPage = new DependentWizardPage();
        if (this.settingNewProjectWizardPage == null)
            this.settingNewProjectWizardPage = new SettingProjectWizardPage();
        addPage(this.configProjectPage);
        addPage(this.settingNewProjectWizardPage);
        /* 重新设置Maven项目创建标题信息。 */
        // this.locationPage.setTitle("Noe Project Maven Settings.");
        // this.archetypePage.setTitle("Noe Project Maven Settings.");
        // this.parametersPage.setTitle("Noe Project Maven Settings.");
        // this.artifactPage.setTitle("Noe Project Maven Settings.");
        this.configProjectPage.setTitle("select project for dependent.");
        this.settingNewProjectWizardPage.setTitle("select project for dependent.");
    }
    @Override
    public boolean performFinish() {
        // TODO Auto-generated method stub
        return true;
    }
}