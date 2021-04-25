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
package net.hasor.tconsole.binder;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.tconsole.ConsoleApiBinder;
import net.hasor.tconsole.TelExecutor;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * ConsoleApiBinder 接口实现
 * @version : 2019年10月30日
 * @author 赵永春 (zyc@hasor.net)
 */
class InnerConsoleApiBinder extends ApiBinderWrap implements ConsoleApiBinder {
    private final InnerExecutorManager executorManager;

    InnerConsoleApiBinder(InnerExecutorManager executorManager, ApiBinder apiBinder) {
        super(apiBinder);
        this.executorManager = executorManager;
    }

    @Override
    public TelnetBuilder asHostWithEnv() {
        return new TelnetBuilderImpl();
    }

    @Override
    public HostBuilder asHost(Reader reader, Writer writer) {
        this.executorManager.setTelMode(InnerTelMode.Host);
        this.executorManager.setHostReader(reader);
        this.executorManager.setHostWriter(writer);
        return new TelnetBuilderImpl();
    }

    @Override
    public TelnetBuilder asTelnet(InetSocketAddress address, Predicate<String> inBoundMatcher) {
        this.executorManager.setTelMode(InnerTelMode.Telnet);
        this.executorManager.setTelnetSocket(address);
        this.executorManager.setTelnetInBoundMatcher(inBoundMatcher);
        return new TelnetBuilderImpl();
    }

    private class TelnetBuilderImpl implements HostBuilder, TelnetBuilder {
        @Override
        public HostBuilder silent() {
            executorManager.setHostSilent(true);
            return this;
        }

        @Override
        public HostBuilder preCommand(String... commands) {
            executorManager.setHostPreCommandSet(commands);
            return this;
        }

        @Override
        public HostBuilder answerExit() {
            executorManager.setHostAnswerExit(true);
            return this;
        }

        @Override
        public CommandBindingBuilder addExecutor(String... names) {
            if (names == null || names.length == 0) {
                throw new NullPointerException("command names undefined.");
            }
            return new CommandBindingBuilderImpl(names);
        }

        @Override
        public Object getAttribute(String key) {
            return executorManager.getAttributeObject().getAttribute(key);
        }

        @Override
        public void setAttribute(String key, Object value) {
            executorManager.getAttributeObject().setAttribute(key, value);
        }

        @Override
        public Set<String> getAttributeNames() {
            return executorManager.getAttributeObject().getAttributeNames();
        }
    }

    private class CommandBindingBuilderImpl implements CommandBindingBuilder {
        private String[] names;

        CommandBindingBuilderImpl(String[] names) {
            this.names = names;
        }

        @Override
        public <T extends TelExecutor> void to(Class<? extends T> executorKey) {
            toInfo(bindType(TelExecutor.class).uniqueName().to(executorKey).toInfo());
        }

        @Override
        public <T extends TelExecutor> void toConstructor(Constructor<T> constructor) {
            toInfo(bindType(TelExecutor.class).toConstructor(constructor).toInfo());
        }

        @Override
        public <T extends TelExecutor> void toInfo(BindInfo<? extends T> executorInfo) {
            toProvider(getProvider(executorInfo));
        }

        @Override
        public <T extends TelExecutor> void toInstance(T instance) {
            toProvider(() -> instance);
        }

        @Override
        public <T extends TelExecutor> void toProvider(Supplier<? extends T> executorProvider) {
            for (String name : this.names) {
                executorManager.addProvider(name, executorProvider);
            }
        }
    }
}
