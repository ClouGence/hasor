package org.hasor.test.app.actions;
import java.io.IOException;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.servlet.ServletException;
import org.hasor.mvc.controller.Controller;
import org.hasor.mvc.controller.Get;
import org.hasor.mvc.controller.Path;
import org.hasor.mvc.controller.support.AbstractController;
import org.hasor.test.plugin.log.OutLog;
import org.hasor.test.plugin.safety.SafetyContext;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2013-7-25
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@OutLog
@Controller("/power")
public class PowerAction extends AbstractController {
    @Inject
    private SafetyContext safetyContext;
    public void add() throws IOException, ServletException {
        String power = this.getRequest().getParameter("power");
        if (!StringUtils.isBlank(power)) {
            this.safetyContext.addPower(power);
        }
        this.getRequest().getRequestDispatcher("/power/list").forward(this.getRequest(), this.getResponse());
    }
    public void del() throws IOException, ServletException {
        String power = this.getRequest().getParameter("power");
        if (!StringUtils.isBlank(power)) {
            this.safetyContext.remotePower(power);
            this.getResponse().getWriter().write("DELETE POWER :" + power);
        }
        this.getRequest().getRequestDispatcher("/power/list").forward(this.getRequest(), this.getResponse());
    }
    @Get
    @Path("/power/list")
    public void listAction() throws IOException {
        PrintWriter w = this.getResponse().getWriter();
        w.write("POWER LIST:<br/>\n");
        for (String power : this.safetyContext.getPowers())
            w.write("has POWER :<a href='/power/del.do?power=" + power + "'>" + power + "</a><br/>\n");
        w.flush();
    }
}