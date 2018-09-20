package net.hasor.core.container;
//
public class ConstructorBean {
    private String param = null;
    public ConstructorBean(String param) {
        this.param = param;
    }
    public String getParam() {
        return param;
    }
}