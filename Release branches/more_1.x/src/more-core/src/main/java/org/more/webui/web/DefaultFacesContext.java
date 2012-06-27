package org.more.webui.web;
import java.util.HashMap;
import java.util.Map;
import org.more.webui.context.FacesConfig;
import org.more.webui.context.FacesContext;
import freemarker.cache.MruCacheStorage;
import freemarker.template.Configuration;
/**
 * 
 * @version : 2012-4-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class DefaultFacesContext extends FacesContext {
    private Configuration cfg = null;
    /*------------------------------------------------*/
    public DefaultFacesContext(FacesConfig config) {
        super(config);
    }
    @Override
    public Configuration getFreemarker() {
        if (this.cfg == null) {
            Configuration cfg = new Configuration();
            String config = this.getFacesConfig().getEncoding();
            if (config != null)
                cfg.setDefaultEncoding(config);
            /*这条必须加，因为没有缓存会有模板重新载入丢失的问题。
             * 引发这个问题的原因是webui需要向模板中的标签写入id文件。*/
            cfg.setCacheStorage(new MruCacheStorage(0, Integer.MAX_VALUE));
            //
            cfg.setLocalizedLookup(this.getFacesConfig().isLocalizedLookup());
            cfg.setTemplateLoader(this.getFacesConfig().getMultiTemplateLoader());
            this.cfg = cfg;
        }
        return this.cfg;
    }
}