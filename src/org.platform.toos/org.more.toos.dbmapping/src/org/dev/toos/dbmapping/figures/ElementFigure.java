/*
 * Created on 2005-1-24
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.dev.toos.dbmapping.figures;
import org.dev.toos.dbmapping.model.Element;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
/**
 * 
 * @version : 2013-3-20
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class ElementFigure extends RectangleFigure {
    private RectangleFigure rectangleFigure;
    private Label           label;
    private Element         element;
    //
    //
    public ElementFigure(Element element) {
        this.element = element;
        this.label = new Label(this.element.getName());
        this.rectangleFigure = new RectangleFigure();
        this.rectangleFigure.setFill(true);
        this.add(rectangleFigure);
        this.add(label);
        this.setSize(element.getSize());
    }
    public String getText() {
        return this.label.getText();
    }
    public Rectangle getTextBounds() {
        return this.label.getTextBounds();
    }
    public void setName(String name) {
        this.label.setText(name);
        this.element.setName(name);
        this.repaint();
    }
    //------------------------------------------------------------------------
    public void setBounds(Rectangle rect) {
        super.setBounds(rect);
        //        this.rectangleFigure.setBounds(rect);
        //        this.label.setBounds(rect);
    }
}