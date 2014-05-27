package org.dev.toos.dbmapping.tools;
import org.dev.toos.dbmapping.model.Connection;
import org.dev.toos.dbmapping.model.Element;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteToolbar;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
/**
 * 
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
public class PaletteFactory {
    public static PaletteRoot createToolBars() {
        PaletteRoot paletteRoot = new PaletteRoot();
        paletteRoot.add(createElementTools(paletteRoot));
        paletteRoot.add(createConnectionTools(paletteRoot));
        return paletteRoot;
    }
    /**工具条A*/
    public static PaletteContainer createElementTools(PaletteRoot paletteRoot) {
        PaletteToolbar toolbar = new PaletteToolbar("Tools");
        ToolEntry tool = new PanningSelectionToolEntry();
        toolbar.add(tool);
        paletteRoot.setDefaultEntry(tool);
        // Add a marquee tool to the group
        toolbar.add(new MarqueeToolEntry());
        // Add (solid-line) connection tool 
        tool = new ConnectionCreationToolEntry("Solid connection", "Create a solid-line connection", new CreationFactory() {
            public Object getNewObject() {
                return null;
            }
            public Object getObjectType() {
                return Connection.SOLID_CONNECTION;
            }
        }, null, null);
        toolbar.add(tool);
        // Add (dashed-line) connection tool
        tool = new ConnectionCreationToolEntry("Dashed connection", "Create a dashed-line connection", new CreationFactory() {
            public Object getNewObject() {
                return null;
            }
            public Object getObjectType() {
                return Connection.DASHED_CONNECTION;
            }
        }, null, null);
        toolbar.add(tool);
        return toolbar;
    }
    /**工具条B*/
    public static PaletteDrawer createConnectionTools(PaletteRoot paletteRoot) {
        PaletteDrawer componentsDrawer = new PaletteDrawer("Element");
        CombinedTemplateCreationEntry component = new CombinedTemplateCreationEntry("Element", "Create an Element", Element.class, new SimpleFactory(Element.class), null, null);
        componentsDrawer.add(component);
        //        component = new CombinedTemplateCreationEntry("Rectangle", "Create a rectangular shape", RectangularShape.class, new SimpleFactory(RectangularShape.class), ImageDescriptor.createFromFile(ShapesPlugin.class, "icons/rectangle16.gif"), ImageDescriptor.createFromFile(ShapesPlugin.class, "icons/rectangle24.gif"));
        //        componentsDrawer.add(component);
        return componentsDrawer;
    }
}