package org.dev.toos.dbmapping.view;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import org.dev.toos.dbmapping.model.Diagram;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.ide.IDE;
/**
 * 
 * @version : 2013-3-14
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class CreateMappingWizard extends Wizard implements INewWizard {
    private static int   fileCount = 1;
    private CreationPage page1;
    public void addPages() {
        addPage(page1);
    }
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        // create pages for this wizard
        page1 = new CreationPage(workbench, selection);
    }
    public boolean performFinish() {
        return page1.finish();
    }
    private class CreationPage extends WizardNewFileCreationPage {
        private static final String DEFAULT_EXTENSION = ".dbm";
        private final IWorkbench    workbench;
        CreationPage(IWorkbench workbench, IStructuredSelection selection) {
            super("shapeCreationPage1", selection);
            this.workbench = workbench;
            setTitle("Create a new " + DEFAULT_EXTENSION + " file");
            setDescription("Create a new " + DEFAULT_EXTENSION + " file");
        }
        public void createControl(Composite parent) {
            super.createControl(parent);
            setFileName("shapesExample" + fileCount + DEFAULT_EXTENSION);
            setPageComplete(validatePage());
        }
        private Object createDefaultContent() {
            return new Diagram();
        }
        boolean finish() {
            // create a new file, result != null if successful
            IFile newFile = createNewFile();
            fileCount++;
            // open newly created file in the editor
            IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
            if (newFile != null && page != null) {
                try {
                    IDE.openEditor(page, newFile, true);
                } catch (PartInitException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }
        protected InputStream getInitialContents() {
            ByteArrayInputStream bais = null;
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(createDefaultContent()); // argument must be Serializable
                oos.flush();
                oos.close();
                bais = new ByteArrayInputStream(baos.toByteArray());
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return bais;
        }
        private boolean validateFilename() {
            if (getFileName() != null && getFileName().endsWith(DEFAULT_EXTENSION)) {
                return true;
            }
            setErrorMessage("The 'file' name must end with " + DEFAULT_EXTENSION);
            return false;
        }
        protected boolean validatePage() {
            return super.validatePage() && validateFilename();
        }
    }
}
