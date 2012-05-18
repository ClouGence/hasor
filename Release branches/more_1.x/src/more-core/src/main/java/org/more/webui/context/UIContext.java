package org.more.webui.context;
import org.more.core.global.Global;
import freemarker.template.Configuration;
/**
 * 
 * @version : 2012-4-25
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class UIContext {
    public static String  Request_StateKEY = "WebUI.StateKEY";
    //
    //
    private Register      register         = null;
    private Global        uiConfig         = null;
    private Configuration cfg              = null;
    public UIContext(Global uiConfig, Configuration cfg) {
        this.uiConfig = uiConfig;
        //
        this.register = new Register(uiConfig);
        this.cfg = cfg;
        // TODO Auto-generated constructor stub
    }
    //
    public Configuration getFreemarkerConfiguration() {
        // TODO Auto-generated method stub
        return cfg;
    }
    public String getEncoding() {
        return "utf-8";
    }
    public Global getGlobal() {
        return uiConfig;
    }
    public Register getRegister() {
        return register;
    }
    //    public HttpServletResponse getHttpResponse() {
    //        // TODO Auto-generated method stub
    //        return null;
    //    }
    //    public Object getUIContext() {
    //        // TODO Auto-generated method stub
    //        return null;
    //    }
    //    public Object getTemplate() {
    //        // TODO Auto-generated method stub
    //        return null;
    //    } 
}