package net.hasor.core.container.beans;
//
public class ConstructorMultiBean extends CallInitBean {
    //
    public ConstructorMultiBean(String paramName) {
        this.setName(paramName);
    }
    //
    public ConstructorMultiBean(String paramUUID, String paramName) {
        this.setUuid(paramUUID);
        this.setName(paramName);
    }
}