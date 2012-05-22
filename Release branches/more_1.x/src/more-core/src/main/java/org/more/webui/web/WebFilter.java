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
import org.more.webui.context.DefaultFacesContextFactory;
import org.more.webui.context.FacesConfig;
import org.more.webui.context.FacesContext;
import org.more.webui.context.ViewContext;
import org.more.webui.lifestyle.DefaultLifestyleFactory;
import org.more.webui.lifestyle.Lifecycle;
/**
 * Web入口
 * @version : 2012-5-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class WebFilter implements Filter {
    private String       facesSuffix = null;
    private Lifecycle    lifecycle   = null;
    private FacesConfig  config      = null;
    private FacesContext uiContext   = null;
    //
    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) arg0;
        HttpServletResponse res = (HttpServletResponse) arg1;
        //判断请求资源是否满足尾缀要求。
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
        this.config = new FacesConfig(arg0);
        this.facesSuffix = this.config.getFacesSuffix();
        this.uiContext = new DefaultFacesContextFactory().createFacesContext(this.config);
        this.lifecycle = new DefaultLifestyleFactory().createLifestyle();
    }
    @Override
    public void destroy() {}
}