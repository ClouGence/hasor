package org.platform;
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
        connector.setPort(8082);
        server.addConnector(connector);
        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        //context.setDescriptor("web/WEB-INF/web.xml");
        context.setResourceBase("src/test/resources/webapps");
        context.setConfigurationDiscovered(true);
        server.setHandler(context);
        server.start();
        System.out.println("srart at http://127.0.0.1:8082");
    }
}
