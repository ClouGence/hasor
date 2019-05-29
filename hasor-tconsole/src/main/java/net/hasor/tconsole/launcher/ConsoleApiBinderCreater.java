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
package net.hasor.tconsole.launcher;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.HasorUtils;
import net.hasor.core.binder.ApiBinderCreater;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.tconsole.CommandExecutor;
import net.hasor.tconsole.ConsoleApiBinder;
import net.hasor.core.ApiBinder.InjectPropertyBindingBuilder;
import net.hasor.core.ApiBinder.LifeBindingBuilder;
import net.hasor.tconsole.ConsoleApiBinder.CommandBindingBuilder;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;
/**
 * DataQL 扩展接口。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2017-03-23
 */
public class ConsoleApiBinderCreater implements ApiBinderCreater<ConsoleApiBinder> {
    @Override
    public ConsoleApiBinder createBinder(final ApiBinder apiBinder) {
        return new ConsoleApiBinderImpl(apiBinder);
    }
    //
    private static class ConsoleApiBinderImpl extends ApiBinderWrap implements ConsoleApiBinder {
        public ConsoleApiBinderImpl(ApiBinder apiBinder) {
            super(apiBinder);
        }
        @Override
        public CommandBindingBuilder addCommand(String... names) {
            if (names == null || names.length == 0) {
                throw new NullPointerException("command names undefined.");
            }
            return new CommandBindingBuilderImpl(this, names);
        }
    }
    private static class CommandBindingBuilderImpl implements CommandBindingBuilder {
        private String[]  names;
        private ApiBinder apiBinder;
        public CommandBindingBuilderImpl(ApiBinder apiBinder, String[] names) {
            this.names = names;
            this.apiBinder = apiBinder;
        }
        //
        @Override
        public <T extends CommandExecutor> InjectPropertyBindingBuilder<? extends CommandExecutor> to(Class<T> implementation) {
            InjectPropertyBindingBuilder<CommandExecutor> bindingBuilder = apiBinder//
                    .bindType(CommandExecutor.class)    //
                    .uniqueName()                       //
                    .to(implementation);
            //
            toInfo(bindingBuilder.toInfo());
            return bindingBuilder;
        }
        @Override
        public <T extends CommandExecutor> LifeBindingBuilder<CommandExecutor> toProvider(Supplier<T> supplier) {
            LifeBindingBuilder<CommandExecutor> bindingBuilder = apiBinder//
                    .bindType(CommandExecutor.class)    //
                    .uniqueName()                       //
                    .toProvider(supplier);
            //
            toInfo(bindingBuilder.toInfo());
            return bindingBuilder;
        }
        @Override
        public <T extends CommandExecutor> LifeBindingBuilder<CommandExecutor> toConstructor(Constructor<T> constructor) {
            LifeBindingBuilder<CommandExecutor> bindingBuilder = apiBinder//
                    .bindType(CommandExecutor.class)    //
                    .uniqueName()                       //
                    .toConstructor(constructor);
            //
            toInfo(bindingBuilder.toInfo());
            return bindingBuilder;
        }
        public void toInfo(BindInfo<? extends CommandExecutor> bindInfo) {
            apiBinder.bindType(ExecutorDefine.class)    //
                    .uniqueName()                       //
                    .toInstance(HasorUtils.autoAware(   //
                            apiBinder.getEnvironment(), //
                            new ExecutorDefine(names, bindInfo)//
                    ));
        }
    }
}