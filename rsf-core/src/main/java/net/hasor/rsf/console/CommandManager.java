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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.RepeateException;
import org.more.json.JSON;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.AppContext;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.core.Singleton;
/**
 * 
 * @version : 2016年4月7日
 * @author 赵永春(zyc@hasor.net)
 */
@Singleton
public class CommandManager {
    protected Logger                      logger     = LoggerFactory.getLogger(getClass());
    @Inject
    private AppContext                    appContext;
    private final Map<String, RsfCommand> commandMap = new HashMap<String, RsfCommand>();
    //
    @Init
    public void initCommand() throws Throwable {
        List<RsfCommand> cmdSet = appContext.findBindingBean(RsfCommand.class);
        if (cmdSet == null || cmdSet.isEmpty()) {
            this.logger.warn("load rsf Console Command is empty.");
            return;
        } else {
            ArrayList<String> cmdNames = new ArrayList<String>();
            for (RsfCommand cmdObject : cmdSet) {
                RsfCmd cmdInfo = cmdObject.getClass().getAnnotation(RsfCmd.class);
                for (String name : cmdInfo.value()) {
                    name = name.toLowerCase();
                    cmdNames.add(name);
                    if (this.commandMap.containsKey(name)) {
                        RsfCommand conflictCmd = this.commandMap.get(name);
                        String types = cmdObject.getClass().getName() + " , " + conflictCmd.getClass().getName();
                        throw new RepeateException("conflict command name '" + name + "' {" + types + "}");
                    } else {
                        this.commandMap.put(name, cmdObject);
                    }
                }
            }
            this.logger.info("load rsf Console Commands ={}.", JSON.toString(cmdNames));
        }
        //
    }
    /**查找命令。*/
    public RsfCommand findCommand(String requestCMD) {
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