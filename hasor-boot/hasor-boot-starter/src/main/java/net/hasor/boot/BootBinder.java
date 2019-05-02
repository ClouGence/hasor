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

import java.util.function.Supplier;
/**
 *
 * @version : 2018-08-04
 * @author 赵永春 (zyc@hasor.net)
 */
public interface BootBinder extends ApiBinder {
    /** main启动时的启动参数，参数来源为：HasorLauncher.run(MyModule.class,args); args 参数。 */
    public String[] mainArgs();

    /** 参数总数 */
    public int mainArgsCount();

    /** 获取第 n 个参数，如果 index 越界那么会返回 null。 */
    public String mainArgs(int index);

    /** 添加 CommandLauncher */
    public void addCommand(int checkArgsIndex, String commandName, Class<? extends CommandLauncher> launcherType);

    /** 添加 CommandLauncher */
    public void addCommand(int checkArgsIndex, String commandName, CommandLauncher launcher);

    /** 添加 CommandLauncher */
    public void addCommand(int checkArgsIndex, String commandName, Supplier<? extends CommandLauncher> launcherProvider);

    /** 添加 CommandLauncher */
    public void addCommand(int checkArgsIndex, String commandName, BindInfo<? extends CommandLauncher> launcherInfo);
}