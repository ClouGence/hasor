package org.more.webui.context;
import org.more.core.iatt.Attribute;
import freemarker.template.Configuration;
/**
 * 
 * @version : 2012-4-25
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class FacesContext {
    private FacesConfig facesConfig = null;
    //
    public FacesContext(FacesConfig facesConfig) {
        this.facesConfig = facesConfig;
    };
    /**获取配置对象。*/
    public FacesConfig getFacesConfig() {
        return this.facesConfig;
    };
    /**获取页面使用的字符编码*/
    public String getEncoding() {
        return this.facesConfig.getEncoding();
    };
    /**获取freemarker的配置对象。*/
    public abstract Configuration getFreemarker();
    /**获取属性集合*/
    public abstract Attribute<Object> getAttribute();
}