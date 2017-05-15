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
package net.hasor.rsf.console;
import net.hasor.core.AppContext;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
import net.hasor.rsf.domain.RsfConstants;
import net.hasor.rsf.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @version : 2016年4月7日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class CommandManager {
    protected static Logger logger = LoggerFactory.getLogger(RsfConstants.LoggerName_Console);
    @Inject
    private AppContext appContext;
    private final Map<String, RsfInstruct> commandMap = new HashMap<String, RsfInstruct>();
    //
    @Init
    public void initCommand() throws Throwable {
        List<RsfInstruct> cmdSet = appContext.findBindingBean(RsfInstruct.class);
        if (cmdSet == null || cmdSet.isEmpty()) {
            logger.warn("rsfConsole -> initCommand is empty.");
            return;
        }
        //
        ArrayList<String> cmdNames = new ArrayList<String>();
        for (RsfInstruct cmdObject : cmdSet) {
            RsfCommand cmdInfo = cmdObject.getClass().getAnnotation(RsfCommand.class);
            for (String name : cmdInfo.value()) {
                name = name.toLowerCase();
                cmdNames.add(name);
                if (this.commandMap.containsKey(name)) {
                    RsfInstruct conflictCmd = this.commandMap.get(name);
                    String types = cmdObject.getClass().getName() + " , " + conflictCmd.getClass().getName();
                    throw new IllegalStateException("conflict command name '" + name + "' {" + types + "}");
                } else {
                    this.commandMap.put(name, cmdObject);
                }
            }
        }
        logger.info("load rsf Console Commands ={}.", StringUtils.join(cmdNames.toArray(), ", "));
        //
    }
    /**查找命令。*/
    public RsfInstruct findCommand(String requestCMD) {
        if (StringUtils.isBlank(requestCMD)) {
            return null;
        }
        return this.commandMap.get(requestCMD.toLowerCase());
    }
    //
    public List<String> getCommandNames() {
        List<String> names = new ArrayList<String>(this.commandMap.keySet());
        java.util.Collections.sort(names);
        return names;
    }
}