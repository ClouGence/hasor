package test.net.hasor.rsf.center;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Test;
public class WebServer {
    public static void main(String[] args) throws Exception {
        //
    }
    @Test
    public void main() throws Exception {
        Server server = new Server();
        SelectChannelConnector connector8082 = new SelectChannelConnector();
        connector8082.setPort(8082);
        server.addConnector(connector8082);
        // SelectChannelConnector connector8083 = new SelectChannelConnector();
        // connector8083.setPort(8083);
        // server.addConnector(connector8083);
        // solr.solr.home
        //
        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        // context.setDescriptor("web/WEB-INF/web.xml");
        context.setResourceBase("src/main/webapp");
        context.setConfigurationDiscovered(true);
        server.setHandler(context);
        server.start();
        System.out.println("srart at http://127.0.0.1:8082");
    }
}