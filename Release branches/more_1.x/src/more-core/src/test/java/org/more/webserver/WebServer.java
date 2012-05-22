package org.more.webserver;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.more.webui.web.WebFilter;
public class WebServer {
    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(8081);
        server.addConnector(connector);
        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        //context.setDescriptor("web/WEB-INF/web.xml");
        context.setResourceBase("web");
        context.setConfigurationDiscovered(true);
        context.addFilter(WebFilter.class, "/*", null);
        server.setHandler(context);
        server.start();
    }
}
