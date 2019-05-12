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
import net.hasor.core.aop.AsmTools;
import net.hasor.core.exts.aop.Matchers;

import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.function.Predicate;
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
    public default void loadCommandSet(int checkArgsIndex, Set<Class<?>> launcherSet, Predicate<Class<?>> matcher) {
        if (launcherSet == null) {
            return;
        }
        matcher = (matcher == null) ? Matchers.anyClass() : matcher;
        for (Class<?> atClass : launcherSet) {
            if (matcher.test(atClass)) {
                this.loadCommand(checkArgsIndex, atClass);
            }
        }
    }

    /** 添加 CommandLauncher */
    public void loadCommand(int checkArgsIndex, Class<?> launcherType);

    /** 添加 CommandLauncher */
    public default void loadCommandSet(int checkArgsIndex, Set<Class<?>> launcherSet) {
        this.loadCommandSet(checkArgsIndex, launcherSet, launcherType -> {
            int modifier = launcherType.getModifiers();
            if (AsmTools.checkOr(modifier, Modifier.INTERFACE, Modifier.ABSTRACT) || launcherType.isArray() || launcherType.isEnum()) {
                return false;
            }
            if (!CommandLauncher.class.isAssignableFrom(launcherType)) {
                return false;
            }
            Command[] annotationsByType = launcherType.getAnnotationsByType(Command.class);
            if (annotationsByType == null || annotationsByType.length == 0) {
                return false;
            }
            return true;
        });
    }

    /** 添加 CommandLauncher */
    public default void addCommand(int checkArgsIndex, String commandName, Class<? extends CommandLauncher> launcherType) {
        this.addCommand(checkArgsIndex, commandName, bindType(CommandLauncher.class).idWith(commandName).to(launcherType).toInfo());
    }

    /** 添加 CommandLauncher */
    public default void addCommand(int checkArgsIndex, String commandName, CommandLauncher launcher) {
        this.addCommand(checkArgsIndex, commandName, bindType(CommandLauncher.class).idWith(commandName).toInstance(launcher).toInfo());
    }

    /** 添加 CommandLauncher */
    public default void addCommand(int checkArgsIndex, String commandName, Supplier<? extends CommandLauncher> launcherProvider) {
        this.addCommand(checkArgsIndex, commandName, bindType(CommandLauncher.class).idWith(commandName).toProvider(launcherProvider).toInfo());
    }

    /** 添加 CommandLauncher */
    public void addCommand(int checkArgsIndex, String commandName, BindInfo<? extends CommandLauncher> launcherInfo);
}