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
import net.hasor.tconsole.TelAttribute;
import net.hasor.tconsole.TelExecutor;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.hasor.tconsole.launcher.TelUtils.finalBindAddress;

/**
 * RSF终端管理器插件。
 * @version : 2016年2月18日
 * @author 赵永春 (zyc@hasor.net)
 */
public interface ConsoleApiBinder extends ApiBinder {
    /**
     * 创建一个 Host 模式的 Tel 命令交互界面，并使用标准输入输出流作为数据交换通道。
     * 提示：使用标准输入输出流作为消息通道的情况下会自动启用 answerExit
     */
    public default HostBuilder asHostWithSTDO() {
        return asHost(new InputStreamReader(System.in), new OutputStreamWriter(System.out, StandardCharsets.UTF_8)).answerExit();
    }

    /**
     * 创建一个 Host 模式的 Tel 命令交互界面。
     */
    public HostBuilder asHost(Reader reader, Writer writer);

    /**
     * 创建一个网络 Tel 命令交互界面，使用的协议是 Telnet。
     */
    public default TelnetBuilder asTelnet(String host, int port) throws UnknownHostException {
        return asTelnet(host, port, s -> true);
    }

    /**
     * 创建一个网络 Tel 命令交互界面，使用的协议是 Telnet。
     */
    public default TelnetBuilder asTelnet(String host, int port, Predicate<String> inBoundMatcher) throws UnknownHostException {
        return asTelnet(new InetSocketAddress(finalBindAddress(host), port), inBoundMatcher);
    }

    public TelnetBuilder asTelnet(InetSocketAddress address, Predicate<String> inBoundMatcher);

    public interface HostBuilder extends TelnetBuilder, TelAttribute {
        public HostBuilder silent();

        /** 预执行的命令，预执行命令最快会在容器启动 100 ms 之后开始执行。*/
        public HostBuilder preCommand(String... commands);

        public HostBuilder answerExit();
    }

    public interface TelnetBuilder {
        public CommandBindingBuilder addExecutor(String... names);
    }

    public interface CommandBindingBuilder {
        public <T extends TelExecutor> void to(Class<? extends T> executorKey);

        public <T extends TelExecutor> void toConstructor(Constructor<T> constructor);

        public <T extends TelExecutor> void toInstance(T instance);

        public <T extends TelExecutor> void toProvider(Supplier<? extends T> executorProvider);

        public <T extends TelExecutor> void toInfo(BindInfo<? extends T> executorInfo);
    }
}