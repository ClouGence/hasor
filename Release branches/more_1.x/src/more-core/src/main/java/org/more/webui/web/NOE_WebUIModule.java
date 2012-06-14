package org.noe.framework.webui.web;
import org.noe.framework.commons.config.Config;
import org.noe.framework.guice.annotation.AnnWebModule;
import com.google.inject.servlet.ServletModule;
/**
 * 
 * @version : 2012-5-16
 * @author 赵永�?(zyc@byshell.org)
 */
@AnnWebModule(9)
public class NOE_WebUIModule extends ServletModule {
    @Override
    protected void configureServlets() {
        String protocol = Config.getString("SSys.webUI.facesSuffix");
        filter("*." + protocol).through(WebFilter.class);
    }
}