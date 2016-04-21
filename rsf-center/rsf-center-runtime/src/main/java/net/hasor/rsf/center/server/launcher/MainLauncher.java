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
package net.hasor.rsf.center.server.launcher;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.more.future.BasicFuture;
import org.more.util.ResourcesUtils;
import org.more.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.core.Hasor;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.rsf.center.server.launcher.telnet.TelnetClient;
import net.hasor.rsf.rpc.context.DefaultRsfSettings;
import net.hasor.rsf.utils.NetworkUtils;
/**
 * 
 * @version : 2016年3月29日
 * @author 赵永春(zyc@hasor.net)
 */
public class MainLauncher {
    protected static Logger logger = LoggerFactory.getLogger(MainLauncher.class);
    public static void main(String[] args, ClassWorld world) throws Throwable {
        logger.info(">>>>>>>>>>>>>>>>> MainLauncher <<<<<<<<<<<<<<<<<");
        String action = args[0];
        /*   */if ("start".equalsIgnoreCase(action)) {
            doStart(args);
        } else if ("stop".equalsIgnoreCase(action)) {
            doStop(args);
        } else if ("version".equalsIgnoreCase(action)) {
            doVersion(args);
        }
    }
    //
    protected static void doStart(String[] args) throws Throwable {
        logger.info(">>>>>>>>>>>>>>>>> doStart <<<<<<<<<<<<<<<<<");
        final BasicFuture<Object> future = new BasicFuture<Object>();
        final String config = args[1];
        AppContext app = Hasor.createAppContext(new File(config), new StartupModule());
        app.getEnvironment().getEventContext().addListener(AppContext.ContextEvent_Shutdown, new EventListener<AppContext>() {
            public void onEvent(String event, AppContext eventData) throws Throwable {
                future.completed(new Object());//to end
            }
        });
        //
        future.get();
    }
    protected static void doStop(String[] args) throws Throwable {
        logger.info(">>>>>>>>>>>>>>>>> doStop <<<<<<<<<<<<<<<<<");
        StringWriter commands = new StringWriter();
        commands.write("set SESSION_AFTERCLOSE = true \n");//命令执行结束就关闭回话。
        commands.write("center_app_shutdown_command\n");
        //
        StandardContextSettings settings = new StandardContextSettings("rsf-config.xml");
        settings.refresh();
        DefaultRsfSettings rsfSettings = new DefaultRsfSettings(settings);
        //
        String addressHost = rsfSettings.getBindAddress();
        addressHost = NetworkUtils.finalBindAddress(addressHost).getHostAddress();
        int consolePort = rsfSettings.getConsolePort();
        //
        BufferedReader reader = new BufferedReader(new StringReader(commands.toString()));
        TelnetClient.execCommand(addressHost, consolePort, reader);
    }
    protected static void doVersion(String[] args) {
        try {
            InputStream verIns = ResourcesUtils.getResourceAsStream("/META-INF/rsf-center.version");
            List<String> dataLines = IOUtils.readLines(verIns, "UTF-8");
            System.out.println(!dataLines.isEmpty() ? dataLines.get(0) : null);
        } catch (Throwable e) {
            logger.error("read version file:/META-INF/rsf-center.version failed -> {}", e);
            System.out.println("undefined");
        }
    }
}