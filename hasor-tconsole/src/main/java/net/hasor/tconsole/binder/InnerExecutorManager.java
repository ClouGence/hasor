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
import net.hasor.core.AppContext;
import net.hasor.core.container.AbstractContainer;
import net.hasor.core.spi.AppContextAware;
import net.hasor.tconsole.TelContext;
import net.hasor.tconsole.TelExecutor;
import net.hasor.tconsole.launcher.AbstractTelService;
import net.hasor.tconsole.launcher.AttributeObject;
import net.hasor.tconsole.launcher.hosts.HostTelService;
import net.hasor.tconsole.launcher.telnet.TelnetTelService;
import net.hasor.tconsole.spi.TelBeforeExecutorListener;
import net.hasor.tconsole.spi.TelHostPreFinishListener;
import net.hasor.tconsole.spi.TelStopContextListener;
import net.hasor.utils.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 负责搜集 ConsoleApiBinder 配置的各种参数，然后在doInitialize的时候进行初始化。
 * @version : 2019年10月30日
 * @author 赵永春 (zyc@hasor.net)
 */
class InnerExecutorManager extends AbstractContainer implements AppContextAware, Supplier<TelContext> {
    private static Logger                                       logger                 = LoggerFactory.getLogger(InnerExecutorManager.class);
    private        InnerTelMode                                 telMode;
    private        Map<String, Supplier<? extends TelExecutor>> telExecutors           = new HashMap<>();
    private        AttributeObject                              attributeObject        = new AttributeObject();
    private        AppContext                                   appContext;
    private        AbstractTelService                           service                = null;
    private        TriggerOnStopToContext                       triggerOnStopToContext = null;
    //
    private        InetSocketAddress                            telnetSocket;
    private        Predicate<String>                            telnetInBoundMatcher   = s -> true;
    private        boolean                                      hostAnswerExit         = false;
    private        Reader                                       hostReader;
    private        Writer                                       hostWriter;
    private        boolean                                      hostSilent;
    private        String[]                                     hostPreCommandSet;

    public void addProvider(String name, Supplier<? extends TelExecutor> provider) {
        this.telExecutors.put(name, provider);
    }

    @Override
    protected void doInitialize() {
        //
        // .创建服务
        if (InnerTelMode.Host == this.telMode) {
            this.service = new HostTelService(this.hostReader, this.hostWriter, this.appContext);
            if (this.hostSilent) {
                ((HostTelService) this.service).silent();
            }
            for (String key : this.attributeObject.getAttributeNames()) {
                ((HostTelService) this.service).setAttribute(key, this.attributeObject.getAttribute(key));
            }
            // 拦截 Close 命令，如果 hostAnswerExit 配置为 true 。那么遇到 exit 命令就执行它关闭服务。
            String[] exitCmd = new String[] { "quit", "exit" };
            this.service.addListener(TelBeforeExecutorListener.class, telCommand -> {
                String cmdName = telCommand.getCommandName();
                for (String item : exitCmd) {
                    if (cmdName.equals(item) && !hostAnswerExit) {
                        logger.info("tConsole -> Ignore close command.");
                        telCommand.cancel();
                        return;
                    }
                }
            });
            // 监听容器关闭事件，去同步关闭 AppContext 容器（如果appContext还在的话）
            this.triggerOnStopToContext = new TriggerOnStopToContext(appContext);
            this.service.addListener(TelStopContextListener.class, this.triggerOnStopToContext);
        }
        if (InnerTelMode.Telnet == this.telMode) {
            this.service = new TelnetTelService(this.telnetSocket, this.telnetInBoundMatcher, this.appContext);
        }
        //
        // .加载命令
        AbstractTelService finalService = Objects.requireNonNull(this.service);
        this.telExecutors.forEach(finalService::addCommand);
        //
        // .启动服务
        this.service.init();
    }

    // .处理 Pre Command
    public void doPreCommand(AppContext appContext) {
        if (this.service instanceof HostTelService) {
            if (this.hostPreCommandSet != null && this.hostPreCommandSet.length > 0) {
                for (String command : this.hostPreCommandSet) {
                    try {
                        ((HostTelService) this.service).sendCommand(command);
                    } catch (IOException e) {
                        throw ExceptionUtils.toRuntimeException(e);
                    }
                }
                logger.info("tConsole -> trigger TelHostPreFinishListener.onFinish");
                service.getSpiTrigger().callSpi(TelHostPreFinishListener.class, listener -> listener.onFinish(appContext));
            }
        }
    }

    @Override
    protected void doClose() {
        if (this.triggerOnStopToContext != null) {
            this.triggerOnStopToContext.disable();
        }
        if (this.service.isInit()) {
            this.service.close();
        }
        this.service = null;
    }

    @Override
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }

    public void setTelMode(InnerTelMode telMode) {
        this.telMode = telMode;
    }

    public void setTelnetSocket(InetSocketAddress telnetSocket) {
        this.telnetSocket = telnetSocket;
    }

    public void setTelnetInBoundMatcher(Predicate<String> telnetInBoundMatcher) {
        this.telnetInBoundMatcher = telnetInBoundMatcher;
    }

    public void setHostReader(Reader hostReader) {
        this.hostReader = hostReader;
    }

    public void setHostWriter(Writer hostWriter) {
        this.hostWriter = hostWriter;
    }

    public void setHostSilent(boolean hostSilent) {
        this.hostSilent = hostSilent;
    }

    public void setHostPreCommandSet(String[] hostPreCommandSet) {
        this.hostPreCommandSet = hostPreCommandSet;
    }

    public void setHostAnswerExit(boolean hostAnswerExit) {
        this.hostAnswerExit = hostAnswerExit;
    }

    public AttributeObject getAttributeObject() {
        return attributeObject;
    }

    @Override
    public TelContext get() {
        return this.service;
    }
}