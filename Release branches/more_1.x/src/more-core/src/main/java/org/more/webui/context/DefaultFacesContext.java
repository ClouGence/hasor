package org.more.webui.context;
import org.more.core.iatt.Attribute;
import freemarker.template.Configuration;
/**
 * 
 * @version : 2012-4-25
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class DefaultFacesContext extends FacesContext {
    private Configuration     cfg = null;
    private Attribute<Object> att = null;
    /*------------------------------------------------*/
    public DefaultFacesContext(FacesConfig config) {
        super(config);
    }
    @Override
    public Attribute<Object> getAttribute() {
        if (this.att == null)
            this.att = new Attribute<Object>();
        return this.att;
    }
    @Override
    public Configuration getFreemarker() {
        if (this.cfg == null) {
            Configuration cfg = new Configuration();
            String config = this.getFacesConfig().getEncoding();
            if (config != null)
                cfg.setDefaultEncoding(config);
            config = this.getFacesConfig().getDateTimeFormat();
            if (config != null)
                cfg.setDateTimeFormat(config);
            config = this.getFacesConfig().getBooleanFormat();
            if (config != null)
                cfg.setBooleanFormat(config);
            config = this.getFacesConfig().getNumberFormat();
            if (config != null)
                cfg.setNumberFormat(config);
            //
            cfg.setLocalizedLookup(this.getFacesConfig().isLocalizedLookup());
            cfg.setTemplateLoader(this.getFacesConfig().getMultiTemplateLoader());
            this.cfg = cfg;
        }
        return this.cfg;
    }
}