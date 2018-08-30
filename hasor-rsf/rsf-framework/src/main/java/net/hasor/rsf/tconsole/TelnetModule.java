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
package net.hasor.rsf.tconsole;
import net.hasor.rsf.RsfApiBinder;
import net.hasor.rsf.RsfModule;
import net.hasor.tconsole.ConsoleApiBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Rsf 框架 Telnet 命令模块。
 * @version : 2014年11月12日
 * @author 赵永春 (zyc@hasor.net)
 */
public final class TelnetModule extends RsfModule {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public final void loadModule(RsfApiBinder apiBinder) throws Throwable {
        ConsoleApiBinder consoleApiBinder = apiBinder.tryCast(ConsoleApiBinder.class);
        if (!consoleApiBinder.isEnable()) {
            return;
        }
        //
        logger.info("rsf Command registered to tConsole.");
        consoleApiBinder.addCommand(new String[] { "detail" }, DetailRsfInstruct.class);
        consoleApiBinder.addCommand(new String[] { "flow" }, FlowRsfInstruct.class);
        consoleApiBinder.addCommand(new String[] { "info" }, InfoRsfInstruct.class);
        consoleApiBinder.addCommand(new String[] { "list" }, ListRsfInstruct.class);
        consoleApiBinder.addCommand(new String[] { "rule" }, RuleRsfInstruct.class);
        consoleApiBinder.addCommand(new String[] { "status" }, StatusRsfInstruct.class);
    }
}