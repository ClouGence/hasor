package org.more.webui.web;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.more.core.global.Global;
import org.more.core.global.assembler.XmlGlobalFactory;
import org.more.webui.context.UIContext;
import org.more.webui.context.ViewContext;
import org.more.webui.freemarker.loader.template.ClassPathTemplateLoader;
import org.more.webui.freemarker.loader.template.MultiTemplateLoader;
import org.more.webui.lifestyle.DefaultLifestyleFactory;
import org.more.webui.lifestyle.Lifecycle;
import freemarker.template.Configuration;
/**
 * 
 * @version : 2012-5-11
 * @author ÕÔÓÀ´º (zyc@byshell.org)
 */
public class WebFilter implements Filter {
    private String    facesSuffix = ".xhtml";
    private Lifecycle lifecycle   = null;
    private UIContext uiContext   = null;
    //
    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) arg0;
        HttpServletResponse res = (HttpServletResponse) arg1;
        //ÅÐ¶ÏÇëÇó×ÊÔ´ÊÇ·ñÂú×ãÎ²×ºÒªÇó¡£
        if (req.getRequestURI().endsWith(this.facesSuffix) == true) {
            ViewContext viewContext = new ViewContext(req, res, this.uiContext);
            ViewContext.setCurrentViewContext(viewContext);
            this.lifecycle.execute(viewContext);
            ViewContext.setCurrentViewContext(null);
        } else
            arg2.doFilter(req, res);
    }
    @Override
    public void init(FilterConfig arg0) throws ServletException {
        try {
            Configuration cfg = new Configuration();
            MultiTemplateLoader multi = new MultiTemplateLoader();
            multi.addTemplateLoader(new ClassPathTemplateLoader("org.more.webui.freemarker.xhtml.parser"));
            cfg.setLocalizedLookup(false);
            cfg.setTemplateLoader(multi);
            //
            //A.´´½¨×¢²áÆ÷
            XmlGlobalFactory xmlGlobal = new XmlGlobalFactory();
            xmlGlobal.setIgnoreRootElement(true);
            xmlGlobal.getLoadNameSpace().add("http://project.byshell.org/more/schema/webui");
            Global uiConfig = xmlGlobal.createGlobal("utf-8", new Object[] { "META-INF/resource/webui/webui-register.xml" });
            //
            this.lifecycle = new DefaultLifestyleFactory().createLifestyle();
            this.uiContext = new UIContext(uiConfig, cfg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // TODO Auto-generated method stub
    }
    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }
}