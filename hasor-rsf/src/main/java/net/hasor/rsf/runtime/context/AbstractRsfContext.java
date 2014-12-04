/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.rsf.runtime.context;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import java.util.concurrent.Executor;
import net.hasor.core.Settings;
import net.hasor.rsf.executes.ExecutesManager;
import net.hasor.rsf.executes.NameThreadFactory;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.runtime.RsfContext;
import net.hasor.rsf.runtime.RsfSettings;
import net.hasor.rsf.serialize.SerializeFactory;
/**
 * 
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractRsfContext implements RsfContext {
    private SerializeFactory serializeFactory = null;
    private ExecutesManager  executesManager  = null;
    private EventLoopGroup   loopGroup        = null;
    private RsfSettingsImpl  rsfSettings      = null;
    //
    public AbstractRsfContext(Settings settings) {
        this.rsfSettings = new RsfSettingsImpl(settings);
        this.rsfSettings.init();
        this.initSettings();
    }
    //
    /**获取使用的{@link EventLoopGroup}*/
    public EventLoopGroup getLoopGroup() {
        return this.loopGroup;
    }
    /**获取配置*/
    public RsfSettings getSettings() {
        return this.rsfSettings;
    }
    /**获取{@link Executor}用于安排执行任务。*/
    public Executor getCallExecute(String serviceName) {
        return this.executesManager.getExecute(serviceName);
    }
    /**获取序列化管理器。*/
    public SerializeFactory getSerializeFactory() {
        return this.serializeFactory;
    }
    //
    protected void initSettings() {
        RsfSettings settings = this.getSettings();
        this.serializeFactory = SerializeFactory.createFactory(settings);
        int queueSize = settings.getQueueMaxSize();
        int minCorePoolSize = settings.getQueueMinPoolSize();
        int maxCorePoolSize = settings.getQueueMaxPoolSize();
        long keepAliveTime = settings.getQueueKeepAliveTime();
        this.executesManager = new ExecutesManager(minCorePoolSize, maxCorePoolSize, queueSize, keepAliveTime);
        //
        int workerThread = settings.getNetworkWorker();
        this.loopGroup = new NioEventLoopGroup(workerThread, new NameThreadFactory("RSF-Nio-%s"));
        //
    };
    /**根据服务名获取服务描述。*/
    public <T> ServiceMetaData<T> getService(String serviceName, String group, String version) {
        return this.getRegisterCenter().getService(serviceName, group, version);
    }
}