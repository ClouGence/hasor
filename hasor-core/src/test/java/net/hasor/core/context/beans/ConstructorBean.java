package net.hasor.core.context.beans;
import net.hasor.core.ConstructorBy;
import net.hasor.core.Init;
import net.hasor.core.container.beans.CallInitBean;
//
public class ConstructorBean extends CallInitBean {
    //
    @ConstructorBy
    public ConstructorBean(String paramName) {
        this.setName(paramName);
    }
    //
    @Init
    public void aaa() {
        this.setUuid("aaa");
    }
}