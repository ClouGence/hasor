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
package net.hasor.tconsole.autoscan;
import net.hasor.core.ApiBinder;
import net.hasor.core.Module;
import net.hasor.tconsole.CommandExecutor;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
/**
 * Instruct 指令注解化扫描加载器。
 * @version : 2016年2月18日
 * @author 赵永春 (zyc@hasor.net)
 */
public class AutoScanModule implements Module {
    protected static Logger logger = LoggerFactory.getLogger(AutoScanModule.class);
    //
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        if (!apiBinder.getEnvironment().getSettings().getBoolean("hasor.tConsole.autoScan", false)) {
            logger.info("tConsole -> autoscan is disable.");
            return;
        }
        //
        String[] spanPackage = apiBinder.getEnvironment().getSpanPackage();
        logger.info("tConsole -> autoscan on '" + StringUtils.join(spanPackage, ",") + "'");
        //
        final Set<Class<?>> rsfCommandSet = new HashSet<Class<?>>(apiBinder.getEnvironment().findClass(Command.class));
        rsfCommandSet.remove(Command.class);
        if (rsfCommandSet.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("event -> init failed , not found any @RsfCommand.");
            }
            return;
        }
        for (final Class<?> commandClass : rsfCommandSet) {
            if (commandClass == Command.class || !CommandExecutor.class.isAssignableFrom(commandClass)) {
                continue;
            }
            if (!commandClass.getPackage().isAnnotationPresent(SearchInclude.class)) {
                continue;
            }
            logger.info("tConsole -> new order {}.", commandClass);
            apiBinder.bindType(CommandExecutor.class).uniqueName().to((Class<? extends CommandExecutor>) commandClass);
        }
    }
}