package org.more.webserver;
import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.platform.web.WebInitParam;
import org.platform.web.WebServlet;
/**
 * 
 * @version : 2013-4-15
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@WebServlet(urlPatterns = "*/abc.do", initParams = { @WebInitParam(name = "testa", value = "sssssssssss") })
public class TestHttpServlet extends HttpServlet {
    /**  */
    private static final long serialVersionUID = -359303526053750341L;
    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println(config.getInitParameter("testa"));
        // TODO Auto-generated method stub
        super.init(config);
    }
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("aaaa");
        //super.service(req, resp);
        throw new ServletException();
    }
}
