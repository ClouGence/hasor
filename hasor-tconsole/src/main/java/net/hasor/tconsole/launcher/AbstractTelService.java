/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
import io.netty.buffer.ByteBufAllocator;
import net.hasor.core.AppContext;
import net.hasor.core.container.AbstractContainer;
import net.hasor.core.container.SpiCallerContainer;
import net.hasor.core.spi.SpiTrigger;
import net.hasor.tconsole.TelContext;
import net.hasor.tconsole.TelExecutor;
import net.hasor.tconsole.commands.GetSetExecutor;
import net.hasor.tconsole.commands.HelpExecutor;
import net.hasor.tconsole.commands.QuitExecutor;
import net.hasor.tconsole.spi.TelContextListener;
import net.hasor.utils.NameThreadFactory;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Supplier;

/**
 * tConsole 服务
 * @version : 20169年09月20日
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractTelService extends AbstractContainer implements TelContext {
    public static final String                                       CMD            = "tConsole>";
    protected static    Logger                                       logger         = LoggerFactory.getLogger(AbstractTelService.class);
    protected final     ClassLoader                                  classLoader;
    private final       SpiTrigger                                   spiTrigger;
    private final       Map<String, Supplier<? extends TelExecutor>> telExecutorMap = new ConcurrentHashMap<>();
    private             ScheduledExecutorService                     executor       = null;

    /** 创建 tConsole 服务 */
    public AbstractTelService(AppContext appContext) {
        if (appContext != null) {
            this.classLoader = appContext.getClassLoader();
            this.spiTrigger = appContext.getInstance(SpiTrigger.class);
        } else {
            // .空实现，防止npe
            this.classLoader = Thread.currentThread().getContextClassLoader();
            this.spiTrigger = new SpiCallerContainer();
        }
    }

    /** 注册一个 SPI 监听器 */
    public synchronized <T extends EventListener> void addListener(Class<T> spiType, T spiListener) {
        this.addListener(spiType, (Supplier<T>) () -> spiListener);
    }

    /** 注册一个 SPI 监听器 */
    public synchronized <T extends EventListener> void addListener(Class<T> spiType, Supplier<T> spiListener) {
        if (!(this.spiTrigger instanceof SpiCallerContainer)) {
            throw new IllegalStateException("spiTrigger is not SpiCallerContainer.");
        }
        ((SpiCallerContainer) this.spiTrigger).addListener(spiType, spiListener);
    }

    protected void applyCommand() {
        this.addCommand(new String[] { "get", "set" }, new GetSetExecutor());
        this.addCommand(new String[] { "quit", "exit" }, new QuitExecutor());
        this.addCommand(new String[] { "help" }, new HelpExecutor());
    }

    @Override
    protected void doInitialize() {
        // .触发SPI
        this.spiTrigger.callSpi(TelContextListener.class, listener -> {
            listener.onStart(AbstractTelService.this);
        });
        //
        logger.info("tConsole -> applyCommand.");
        this.applyCommand();
        //
        // .执行线程池
        String shortName = "tConsole-Work";
        int workSize = 2;
        this.executor = Executors.newScheduledThreadPool(workSize, new NameThreadFactory(shortName, this.classLoader));
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) this.executor;
        threadPool.setCorePoolSize(workSize);
        threadPool.setMaximumPoolSize(workSize);
        logger.info("tConsole -> create TelnetHandler , threadShortName={} , workThreadSize = {}.", shortName, workSize);
    }

    @Override
    protected void doClose() {
        if (this.executor != null) {
            logger.info("tConsole -> executor shutdownNow.");
            this.executor.shutdownNow();
            this.executor = null;
        }
        this.telExecutorMap.clear();
        // .触发SPI
        this.spiTrigger.callSpi(TelContextListener.class, listener -> {
            listener.onStop(AbstractTelService.this);
        });
    }

    public void asyncExecute(Runnable runnable) {
        if (!this.isInit()) {
            throw new IllegalStateException("the Container need init.");
        }
        this.executor.execute(runnable);
    }

    public SpiTrigger getSpiTrigger() {
        return this.spiTrigger;
    }

    /** 添加命令 */
    public void addCommand(String cmdName, TelExecutor telExecutor) {
        this.addCommand(new String[] { cmdName }, () -> telExecutor);
    }

    /** 添加命令 */
    public void addCommand(String[] cmdName, TelExecutor telExecutor) {
        this.addCommand(cmdName, () -> telExecutor);
    }

    /** 添加命令 */
    public void addCommand(String cmdName, Supplier<? extends TelExecutor> provider) {
        this.addCommand(new String[] { cmdName }, provider);
    }

    /** 添加命令 */
    public void addCommand(String[] cmdName, Supplier<? extends TelExecutor> provider) {
        for (String name : cmdName) {
            if (StringUtils.isNotBlank(name)) {
                this.telExecutorMap.put(name, provider);
            }
        }
    }

    @Override
    public TelExecutor findCommand(String cmdName) {
        Supplier<? extends TelExecutor> supplier = this.telExecutorMap.get(cmdName);
        if (supplier != null) {
            return supplier.get();
        }
        return null;
    }

    @Override
    public List<String> getCommandNames() {
        return new ArrayList<>(this.telExecutorMap.keySet());
    }

    public abstract ByteBufAllocator getByteBufAllocator();
}