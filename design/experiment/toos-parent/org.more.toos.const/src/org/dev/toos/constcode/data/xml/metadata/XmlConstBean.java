package org.dev.toos.constcode.data.xml.metadata;
import org.dev.toos.constcode.data.xml.define.ConstType;
import org.dev.toos.constcode.metadata.ConstBean;
/**
 * 
 * @version : 2013-2-16
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class XmlConstBean extends ConstBean {
    private ConstType target = null;
    public XmlConstBean(ConstType target) {
        this(target, null);
    }
    public XmlConstBean(ConstType target, ConstBean parent) {
        super(parent);
        this.target = target;
    }
    public ConstType getTarget() {
        return target;
    }
}