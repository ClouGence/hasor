package org.noe.platform.modules.freemarker.loader;
import freemarker.cache.TemplateLoader;
/**
 * 
 * @version : 2012-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
public interface FmTemplateLoader extends TemplateLoader {
    /**获取类型*/
    public String getType();
}