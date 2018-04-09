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
package net.hasor.tconsole;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.Provider;
/**
 * TConsol 为您提供 telnet 下和应用程序交互的能力。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2018-04-09
 */
public interface ConsoleApiBinder extends ApiBinder {
    /**是否启用状态*/
    public boolean isEnable();

    /** 添加 CommandExecutor */
    public void addCommand(String[] name, Class<? extends CommandExecutor> executorType);

    /** 添加 CommandExecutor */
    public void addCommand(String[] name, CommandExecutor executor);

    /** 添加 CommandExecutor */
    public void addCommand(String[] name, Provider<? extends CommandExecutor> executorProvider);

    /** 添加 CommandExecutor */
    public void addCommand(String[] name, BindInfo<? extends CommandExecutor> executorInfo);
}