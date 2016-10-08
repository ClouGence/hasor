/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test.net.hasor.rsf.center;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Test;
public class WebServer {
    public static void main(String[] args) throws Exception {
        //
//    }
//    @Test
//    public void main() throws Exception {
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