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
package net.hasor.registry.boot.launcher;
import net.hasor.core.*;
import net.hasor.core.environment.StandardEnvironment;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.console.RsfInstruct;
import net.hasor.rsf.console.launcher.TelnetClient;
import net.hasor.rsf.rpc.context.DefaultRsfEnvironment;
import net.hasor.rsf.utils.IOUtils;
import net.hasor.rsf.utils.NetworkUtils;
import net.hasor.rsf.utils.ResourcesUtils;
import net.hasor.utils.future.BasicFuture;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @version : 2016年3月29日
 * @author 赵永春(zyc@hasor.net)
 */
public class MainLauncher {
    protected static Logger logger = LoggerFactory.getLogger(MainLauncher.class);
    //
    public static void main(String[] args, ClassWorld world) throws Throwable {
        logger.info(">>>>>>>>>>>>>>>>> MainLauncher <<<<<<<<<<<<<<<<<");
        String action = args[0];
        /*   */
        if ("start".equalsIgnoreCase(action)) {
            doStart(args);
        } else if ("stop".equalsIgnoreCase(action)) {
            doStop(args);
        } else if ("version".equalsIgnoreCase(action)) {
            doVersion(args);
        }
    }
    //
    public static void doStart(String[] args) throws Throwable {
        logger.info(">>>>>>>>>>>>>>>>> doStart <<<<<<<<<<<<<<<<<");
        final String config = args[1];
        AppContext app = Hasor.createAppContext(new File(config), null, new Module() {
            public void loadModule(ApiBinder apiBinder) throws Throwable {
                /* 特殊 RSF 指令 */
                apiBinder.bindType(RsfInstruct.class).uniqueName().to(CenterAppShutdownInstruct.class);
            }
        });
        //
        final BasicFuture<Object> future = new BasicFuture<Object>();
        app.getEnvironment().getEventContext().addListener(AppContext.ContextEvent_Shutdown, new EventListener<AppContext>() {
            public void onEvent(String event, AppContext eventData) throws Throwable {
                future.completed(new Object());//to end
            }
        });
        future.get();
    }
    public static void doStop(String[] args) throws Throwable {
        logger.info(">>>>>>>>>>>>>>>>> doStop <<<<<<<<<<<<<<<<<");
        //
        final String config = args[1];
        RsfEnvironment settings = new DefaultRsfEnvironment(new StandardEnvironment(null, new File(config)));
        RsfSettings rsfSettings = settings.getSettings();
        //
        String addressHost = rsfSettings.getBindAddress();
        addressHost = NetworkUtils.finalBindAddress(addressHost).getHostAddress();
        int consolePort = rsfSettings.getConsolePort();
        //
        Map<String, String> envMap = new HashMap<String, String>();
        envMap.put("open_kill_self", "true");//设置 open_kill_self 环境变量,该环境变量在执行 center_app_shutdown_command 命令时候可以让应用程序退出。
        TelnetClient.execCommand(addressHost, consolePort, "center_app_shutdown_command", envMap);
    }
    public static void doVersion(String[] args) {
        try {
            InputStream verIns = ResourcesUtils.getResourceAsStream("/META-INF/rsf-center.version");
            List<String> dataLines = IOUtils.readLines(verIns);
            System.out.println(!dataLines.isEmpty() ? dataLines.get(0) : null);
        } catch (Throwable e) {
            logger.error("read version file:/META-INF/rsf-center.version failed -> {}", e);
            System.out.println("undefined");
        }
    }
}