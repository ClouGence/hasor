package org.dev.toos.dbmapping.view;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.dev.toos.dbmapping.model.Diagram;
import org.dev.toos.dbmapping.part.PartFactory;
import org.dev.toos.dbmapping.tools.PaletteFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.parts.GraphicalEditorWithPalette;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
/**
 * 
 * @version : 2013-3-5
 * @author 赵永春 (zyc@byshell.org)
 */
public class DBMappingEditer extends GraphicalEditorWithPalette {
    private PaletteRoot paletteRoot = null;
    private Diagram     diagram     = new Diagram();
    //
    //
    public DBMappingEditer() {
        /*DefaultEditDomain该类用于管理命令栈*/
        setEditDomain(new DefaultEditDomain(this));
    }
    private Diagram getModel() {
        return diagram;
    }
    @Override
    protected PaletteRoot getPaletteRoot() {
        if (paletteRoot == null)
            /*绘图板工具栏*/
            this.paletteRoot = PaletteFactory.createToolBars();
        return this.paletteRoot;
    }
    protected void configureGraphicalViewer() {
        super.configureGraphicalViewer();
        GraphicalViewer viewer = getGraphicalViewer();
        /*创建各种Part的工厂类。*/
        viewer.setEditPartFactory(new PartFactory());
        /*设置RootEditPart，通过扩展RootEditPart可以实现添加各种层。*/
        viewer.setRootEditPart(new ScalableFreeformRootEditPart());
        /**/
        //viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));
        //配置视图上的菜单
        //        ContextMenuProvider cmProvider = new EditorContextMenuProvider(viewer, getActionRegistry());
        //        viewer.setContextMenu(cmProvider);
        //        getSite().registerContextMenu(cmProvider, viewer);
    }
    @Override
    protected void initializeGraphicalViewer() {
        /**设置*/
        getGraphicalViewer().setContents(this.getModel());
        /**设置拖动监听器，可以实现拖动效果*/
        //        getGraphicalViewer().addDropTargetListener(new DiagramTemplateTransferDropTargetListener(getGraphicalViewer()));
    }
    @Override
    public void doSave(IProgressMonitor monitor) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            //
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(getModel());
            oos.close();
            //
            IFile file = ((IFileEditorInput) getEditorInput()).getFile();
            file.setContents(new ByteArrayInputStream(out.toByteArray()), true, // keep saving, even if IFile is out of sync with the Workspace
                    false, // dont keep history
                    monitor); // progress monitor
            getCommandStack().markSaveLocation();
        } catch (CoreException ce) {
            ce.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    protected void setInput(IEditorInput input) {
        super.setInput(input);
        try {
            IFile file = ((IFileEditorInput) input).getFile();
            ObjectInputStream in = new ObjectInputStream(file.getContents());
            diagram = (Diagram) in.readObject();
            in.close();
            setPartName(file.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}