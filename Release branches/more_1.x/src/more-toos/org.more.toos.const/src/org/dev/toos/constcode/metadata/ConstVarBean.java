package org.dev.toos.constcode.metadata;
/**
 * 
 * @version : 2012-2-20
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public abstract class ConstVarBean {
    private ConstBean    targetConst = null;
    private ConstVarBean parent      = null;
    private String       varKey      = "New Key";
    private String       varVar      = "";
    private String       varLat      = "";
    private String       varExtData  = "";
    //
    //
    public ConstVarBean(ConstBean targetConst) {
        this(targetConst, null);
    }
    public ConstVarBean(ConstBean targetConst, ConstVarBean parent) {
        this.targetConst = targetConst;
        this.parent = parent;
    }
    public ConstBean getConst() {
        return targetConst;
    }
    public void setConst(ConstBean targetConst) {
        this.targetConst = targetConst;
    }
    public ConstVarBean getParent() {
        return parent;
    }
    public void setParent(ConstVarBean parent) {
        this.parent = parent;
    }
    public String getVarKey() {
        return varKey;
    }
    public void setVarKey(String varKey) {
        this.varKey = varKey;
    }
    public String getVarVar() {
        return varVar;
    }
    public void setVarVar(String varVar) {
        this.varVar = varVar;
    }
    public String getVarLat() {
        return varLat;
    }
    public void setVarLat(String varLat) {
        this.varLat = varLat;
    }
    public String getVarExtData() {
        return varExtData;
    }
    public void setVarExtData(String varExtData) {
        this.varExtData = varExtData;
    }
}