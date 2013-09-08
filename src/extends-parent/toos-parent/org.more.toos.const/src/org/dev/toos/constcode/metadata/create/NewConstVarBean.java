package org.dev.toos.constcode.metadata.create;
import org.dev.toos.constcode.metadata.ConstBean;
import org.dev.toos.constcode.metadata.ConstVarBean;
/**
 * 
 * @version : 2012-2-20
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class NewConstVarBean extends ConstVarBean implements NEW {
    public NewConstVarBean(ConstBean targetConst, ConstVarBean parent) {
        super(targetConst, parent);
    }
}