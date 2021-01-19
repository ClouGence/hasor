package net.hasor.spring.beans;
import net.hasor.core.AppContext;
import net.hasor.web.binder.OneConfig;
import net.hasor.web.startup.RuntimeFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RuntimeFilter2Controller {
    private final RuntimeFilter runtimeFilter;

    public RuntimeFilter2Controller(RuntimeFilter runtimeFilter, AppContext appContext) throws ServletException {
        this.runtimeFilter = runtimeFilter;
        runtimeFilter.init(new OneConfig("", () -> appContext));
    }

    public void doHandler(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        this.runtimeFilter.doFilter(request, response, (req, res) -> {
            HttpServletResponse httpRes = (HttpServletResponse) res;
            if (!httpRes.isCommitted()) {
                httpRes.sendError(404, "Not Found Resource.");
            }
        });
    }
}