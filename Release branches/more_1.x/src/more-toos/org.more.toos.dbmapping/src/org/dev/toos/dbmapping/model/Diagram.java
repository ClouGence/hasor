package org.dev.toos.dbmapping.model;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * 代表整个设计图
 * @version : 2013-3-8
 * @author 赵永春 (zyc@byshell.org)
 */
public class Diagram extends AbstractModel {
    private static final long serialVersionUID = 6445402230441667429L;
    public static String      Prop_Element     = "Element";
    private List<Element>     elements         = new ArrayList<Element>(); //所有元素
    //----------------------------------------------------------------------
    public Diagram() {}
    //----------------------------------------------------------------------
    public List<Element> getElements() {
        return Collections.unmodifiableList(elements);
    }
    /**从当前元素创建一个到目标元素的连接。*/
    public Element createElement() {
        return new Element(this);
    }
    /*当“元素”对象create方法被调用时候会调用该方法。*/
    void addEmenemt(Element element) {
        this.elements.add(element);
    }
    /*当“元素”对象delete方法被调用时候会调用该方法。*/
    void removeElement(Element element) {
        if (elements.contains(element) == false)
            return;
        //
        this.elements.remove(element);
        element.getOutputList();
    }
    //
    //
    //
    //
    /**将当前模型对象序列化保存。*/
    public InputStream getAsStream() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(os);
        out.writeObject(this);
        out.close();
        InputStream istream = new ByteArrayInputStream(os.toByteArray());
        os.close();
        return istream;
    }
    /**从输入流中装载模型对象。*/
    public static Diagram makeFromStream(InputStream istream) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(istream);
        Diagram diagram = (Diagram) ois.readObject();
        ois.close();
        return diagram;
    }
}