package org.platform.app.actions;
import java.io.IOException;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hasor.web.controller.Controller;
import org.hasor.web.controller.RestfulMapping;
import org.more.util.StringUtils;
import org.platform.plugin.log.OutLog;
import org.platform.plugin.safety.SafetyContext;
/**
 * 
 * @version : 2013-7-25
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@OutLog
@Controller("/power")
public class PowerAction {
    @Inject
    private SafetyContext safetyContext;
    public void add(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String power = req.getParameter("power");
        if (!StringUtils.isBlank(power)) {
            this.safetyContext.addPower(power);
        }
        req.getRequestDispatcher("/power/list").forward(req, resp);
    }
    public void del(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String power = req.getParameter("power");
        if (!StringUtils.isBlank(power)) {
            this.safetyContext.remotePower(power);
            resp.getWriter().write("DELETE POWER :" + power);
        }
        req.getRequestDispatcher("/power/list").forward(req, resp);
    }
    @RestfulMapping("/list")
    public void listAction(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter w = resp.getWriter();
        w.write("POWER LIST:<br/>\n");
        for (String power : this.safetyContext.getPowers())
            w.write("has POWER :<a href='/power/del.do?power=" + power + "'>" + power + "</a><br/>\n");
        w.flush();
    }
}