package org.dev.toos.constcode.metadata;
/**
 * 
 * @version : 2013-2-16
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public abstract class ConstBean {
    private ConstBean parent       = null;
    private String    constCode    = "New Const";
    private String    constVar     = "";
    private LatType   constLatType = LatType.No;
    private String    constExtData = "";
    //
    public ConstBean() {}
    public ConstBean(ConstBean parent) {
        this.parent = parent;
    }
    public ConstBean getParent() {
        return parent;
    }
    //
    public void setParent(ConstBean parent) {
        this.parent = parent;
    }
    public String getConstCode() {
        return constCode;
    }
    public void setConstCode(String constCode) {
        this.constCode = constCode;
    }
    public String getConstVar() {
        return constVar;
    }
    public void setConstVar(String constVar) {
        this.constVar = constVar;
    }
    public LatType getConstLatType() {
        return constLatType;
    }
    public void setConstLatType(LatType constLatType) {
        this.constLatType = constLatType;
    }
    public String getConstExtData() {
        return constExtData;
    }
    public void setConstExtData(String constExtData) {
        this.constExtData = constExtData;
    }
}