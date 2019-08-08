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
package net.hasor.tconsole.launcher;
import net.hasor.core.AppContext;
import net.hasor.core.spi.AppContextAware;
import net.hasor.tconsole.CommandExecutor;
import net.hasor.tconsole.CommandFinder;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @version : 2016年4月7日
 * @author 赵永春 (zyc@hasor.net)
 */
class Manager implements CommandFinder, AppContextAware {
    protected static Logger                      logger     = LoggerFactory.getLogger(Manager.class);
    private final    Map<String, ExecutorDefine> commandMap = new HashMap<String, ExecutorDefine>();
    private          AppContext                  appContext = null;

    @Override
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
        List<ExecutorDefine> cmdSet = appContext.findBindingBean(ExecutorDefine.class);
        if (cmdSet == null || cmdSet.isEmpty()) {
            logger.warn("tConsole -> initCommand is empty.");
            return;
        }
        //
        ArrayList<String> cmdNames = new ArrayList<String>();
        for (ExecutorDefine cmdObject : cmdSet) {
            for (String name : cmdObject.getNames()) {
                name = name.toLowerCase();
                cmdNames.add(name);
                if (this.commandMap.containsKey(name)) {
                    ExecutorDefine conflictCmd = this.commandMap.get(name);
                    String types = cmdObject.getTargetClassName() + " , " + conflictCmd.getTargetClassName();
                    throw new IllegalStateException("conflict command name '" + name + "' {" + types + "}");
                } else {
                    this.commandMap.put(name, cmdObject);
                }
            }
        }
        logger.info("load tConsole Commands ={}.", StringUtils.join(cmdNames.toArray(), ", "));
    }

    /**查找命令。*/
    public CommandExecutor findCommand(String requestCMD) {
        if (StringUtils.isBlank(requestCMD)) {
            return null;
        }
        return this.commandMap.get(requestCMD.toLowerCase());
    }

    public List<String> getCommandNames() {
        List<String> names = new ArrayList<String>(this.commandMap.keySet());
        java.util.Collections.sort(names);
        return names;
    }

    @Override
    public AppContext getAppContext() {
        return this.appContext;
    }
}