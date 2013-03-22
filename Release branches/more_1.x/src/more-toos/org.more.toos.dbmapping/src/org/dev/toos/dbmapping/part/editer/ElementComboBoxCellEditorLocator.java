package org.dev.toos.dbmapping.part.editer;
import org.dev.toos.dbmapping.figures.ElementFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Point;
/**
 * 
 * @version : 2013-3-20
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class ElementComboBoxCellEditorLocator implements CellEditorLocator {
    private ElementFigure figure = null;
    public ElementComboBoxCellEditorLocator(ElementFigure figure) {
        this.figure = figure;
    }
    @Override
    public void relocate(CellEditor celleditor) {
        CCombo combo = (CCombo) celleditor.getControl();
        combo.add("No");
        combo.add("Group");
        combo.add("School");
        Point pref = combo.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        Rectangle rect = this.figure.getTextBounds();
        combo.setBounds(rect.x - 1, rect.y - 1, pref.x + 1, pref.y + 1);
    }
}