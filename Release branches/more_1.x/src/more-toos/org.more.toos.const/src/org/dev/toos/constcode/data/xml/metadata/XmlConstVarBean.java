package org.dev.toos.constcode.data.xml.metadata;
import org.dev.toos.constcode.data.xml.define.VarType;
import org.dev.toos.constcode.metadata.ConstBean;
import org.dev.toos.constcode.metadata.ConstVarBean;
/**
 * 
 * @version : 2012-2-20
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class XmlConstVarBean extends ConstVarBean {
    private VarType target = null;
    public XmlConstVarBean(VarType target, ConstBean targetConst) {
        this(target, targetConst, null);
    }
    public XmlConstVarBean(VarType target, ConstBean targetConst, ConstVarBean parent) {
        super(targetConst, parent);
        this.target = target;
    }
    public VarType getTarget() {
        return target;
    }
}