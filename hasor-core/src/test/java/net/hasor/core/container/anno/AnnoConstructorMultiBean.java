package net.hasor.core.container.anno;
import net.hasor.core.ConstructorBy;
import net.hasor.core.container.beans.CallInitBean;
//
public class AnnoConstructorMultiBean extends CallInitBean {
    //
    public AnnoConstructorMultiBean(String paramName) {
        this.setName(paramName);
    }
    //
    @ConstructorBy
    public AnnoConstructorMultiBean(String paramUUID, String paramName) {
        this.setUuid(paramUUID);
        this.setName(paramName);
    }
}