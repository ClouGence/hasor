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
package net.hasor.boot;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.Provider;
import net.hasor.core.binder.ApiBinderCreater;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.utils.StringUtils;
/**
 * 只有通过 Hasor Boot 启动才可以使用。
 * @version : 2018-08-04
 * @author 赵永春 (zyc@hasor.net)
 */
public class BootBinderCreater implements ApiBinderCreater {
    @Override
    public BootBinder createBinder(ApiBinder apiBinder) {
        return HasorLauncher.usingBoot ? new BootBinderImpl(apiBinder) : null;
    }
    private static class BootBinderImpl extends ApiBinderWrap implements BootBinder {
        public BootBinderImpl(ApiBinder apiBinder) {
            super(apiBinder);
        }
        //
        @Override
        public String[] mainArgs() {
            return HasorLauncher.mainArgs;
        }
        @Override
        public int mainArgsCount() {
            return HasorLauncher.mainArgs.length;
        }
        @Override
        public String mainArgs(int index) {
            if (index < 0 || index >= HasorLauncher.mainArgs.length) {
                return null;
            }
            return HasorLauncher.mainArgs[index];
        }
        //
        @Override
        public void addCommand(int checkArgsIndex, String commandName, Class<? extends CommandLauncher> launcherType) {
            this.addCommand(checkArgsIndex, commandName, bindType(CommandLauncher.class).idWith(commandName).to(launcherType).toInfo());
        }
        @Override
        public void addCommand(int checkArgsIndex, String commandName, CommandLauncher launcher) {
            this.addCommand(checkArgsIndex, commandName, bindType(CommandLauncher.class).idWith(commandName).toInstance(launcher).toInfo());
        }
        @Override
        public void addCommand(int checkArgsIndex, String commandName, Provider<? extends CommandLauncher> launcherProvider) {
            this.addCommand(checkArgsIndex, commandName, bindType(CommandLauncher.class).idWith(commandName).toProvider(launcherProvider).toInfo());
        }
        @Override
        public void addCommand(int checkArgsIndex, String commandName, BindInfo<? extends CommandLauncher> launcherInfo) {
            if (checkArgsIndex < 0) {
                throw new IndexOutOfBoundsException("checkArgsIndex error.");
            }
            if (StringUtils.isBlank(commandName)) {
                throw new NullPointerException("commandName name undefined.");
            }
            CommandLauncherDef define = new CommandLauncherDef(checkArgsIndex, commandName, launcherInfo);
            this.bindType(CommandLauncherDef.class).uniqueName().toInstance(define);
        }
    }
}