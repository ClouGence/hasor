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
import net.hasor.core.provider.InstanceProvider;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

/**
 * TConsol 为您提供 telnet 下和应用程序交互的能力。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2018-04-09
 */
public interface ConsoleApiBinder extends ApiBinder {
    /**是否启用状态*/
    public default boolean isEnable() {
        return getEnvironment().getSettings().getBoolean("hasor.tConsole.enable", true);
    }

    /** 添加 CommandExecutor */
    public default void addCommand(String[] names, Class<? extends CommandExecutor> instructType) {
        this.addCommand(names, bindType(CommandExecutor.class).uniqueName().to(instructType).toInfo());
    }

    /** 添加 CommandExecutor */
    public default void addCommand(String[] names, CommandExecutor instruct) {
        this.addCommand(names, bindType(CommandExecutor.class).uniqueName().toInstance(instruct).toInfo());
    }

    /** 添加 CommandExecutor */
    public default void addCommand(String[] names, Supplier<? extends CommandExecutor> instructProvider) {
        this.addCommand(names, bindType(CommandExecutor.class).uniqueName().toProvider(instructProvider).toInfo());
    }

    /** 添加 CommandExecutor */
    public default void addCommand(String[] names, BindInfo<? extends CommandExecutor> bindInfo) {
        this.addCommand(names).toInfo(bindInfo);
    }

    /** 添加 CommandExecutor */
    public CommandBindingBuilder addCommand(String... names);

    /**绑定元信息*/
    public interface CommandBindingBuilder {
        /**
         * 为绑定设置一个实现类。
         * @param implementation 实现类型
         * @return 返回 - {@link InjectPropertyBindingBuilder}。
         */
        public <T extends CommandExecutor> InjectPropertyBindingBuilder<? extends CommandExecutor> to(Class<T> implementation);

        /**
         * 为绑定设置一个实例
         * @param instance 实例对象
         * @return 返回 - {@link OptionPropertyBindingBuilder}。
         */
        public default <T extends CommandExecutor> LifeBindingBuilder<CommandExecutor> toInstance(final T instance) {
            return this.toProvider(InstanceProvider.of(instance));
        }

        /**
         * 为绑定设置一个实例
         * @param bindInfo 实例对象
         */
        public void toInfo(BindInfo<? extends CommandExecutor> bindInfo);

        /**
         * 为绑定设置一个 {@link Supplier}。
         * @param supplier supplier 可以用来封装类型实例创建的细节。
         * @return 返回 - {@link LifeBindingBuilder}。
         */
        public <T extends CommandExecutor> LifeBindingBuilder<CommandExecutor> toProvider(Supplier<T> supplier);

        /**
         * 为绑定设置一个构造方法。
         * @param constructor 使用的构造方法。
         * @return 返回 - {@link InjectConstructorBindingBuilder}。
         */
        public <T extends CommandExecutor> LifeBindingBuilder<CommandExecutor> toConstructor(Constructor<T> constructor);
    }
}