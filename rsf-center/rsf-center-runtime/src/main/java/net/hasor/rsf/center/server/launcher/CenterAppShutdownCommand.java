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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.AppContext;
import net.hasor.rsf.console.RsfCmd;
import net.hasor.rsf.console.RsfCommand;
import net.hasor.rsf.console.RsfCommandRequest;
/**
 * 
 * @version : 2016年3月29日
 * @author 赵永春(zyc@hasor.net)
 */
@RsfCmd("center_app_shutdown_command")
public class CenterAppShutdownCommand implements RsfCommand {
    protected static Logger logger = LoggerFactory.getLogger(CenterAppShutdownCommand.class);
    @Override
    public String helpInfo() {
        return "shutdown center.";
    }
    @Override
    public boolean inputMultiLine(RsfCommandRequest request) {
        return false;
    }
    @Override
    public String doCommand(RsfCommandRequest request) throws Throwable {
        try {
            //延迟3秒，shutdown
            final AppContext appContext = request.getRsfContext().getAppContext();
            Thread thread = new Thread() {
                public void run() {
                    appContext.shutdown();
                    //
                    for (int i = 10; i == 0; i--) {
                        logger.error("after {} seconds to kill self.", i);
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) { /**/ }
                    }
                    //
                    System.exit(1);
                };
            };
            //
            thread.setDaemon(true);
            thread.setName("Shutdown");
            thread.start();
            return "do shutdown center.";
        } catch (Exception e) {
            return "shutdown error " + e.getMessage();
        }
    }
}