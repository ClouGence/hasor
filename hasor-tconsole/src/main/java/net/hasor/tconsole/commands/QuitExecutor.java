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
package net.hasor.tconsole.commands;
import net.hasor.core.Singleton;
import net.hasor.tconsole.TelCommand;
import net.hasor.tconsole.TelExecutorVoid;
import net.hasor.tconsole.TelOptions;

/**
 * 关闭 tConsole session。
 * @version : 2019年10月30日
 * @author 赵永春 (zyc@hasor.net)
 */
@Singleton
public class QuitExecutor implements TelExecutorVoid {
    @Override
    public String helpInfo() {
        return "out of console.\r\n"//
                + " -t <n> (when n second after to close telnet.)\r\n"//
                + " -n <n> (when n commands after to close telnet.)\r\n"//
                + " -next  (when next commands after to close telnet.)\r\n"//
                + "     Tips: If you set -n -t at the same time, then -t failure.";
    }

    @Override
    public void voidCommand(TelCommand telCommand) throws Throwable {
        String[] args = telCommand.getCommandArgs();
        int parseInt = 0;
        int nextCommand = 0;
        if (args.length > 0) {
            for (String arg : args) {
                if (arg.startsWith("-next")) {
                    nextCommand = telCommand.getSession().curentCounter() + 1;
                    continue;
                }
                if (arg.startsWith("-t")) {
                    parseInt = Integer.parseInt(arg.substring(2).trim());
                    continue;
                }
                if (arg.startsWith("-n")) {
                    int nextInt = Integer.parseInt(arg.substring(2).trim());
                    if (nextInt > 0) {
                        nextCommand = telCommand.getSession().curentCounter() + nextInt;
                    }
                    continue;
                }
            }
        }
        //
        if (nextCommand > 0) {
            telCommand.getSession().setAttribute(TelOptions.MAX_EXECUTOR_NUM, nextCommand);
            return;
        }
        //
        telCommand.getSession().close(parseInt);
    }
}