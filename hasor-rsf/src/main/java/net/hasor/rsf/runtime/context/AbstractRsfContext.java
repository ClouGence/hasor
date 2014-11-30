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
import net.hasor.core.XmlNode;
import net.hasor.rsf.executes.ExecutesManager;
import net.hasor.rsf.executes.NameThreadFactory;
import net.hasor.rsf.general.ProtocolVersion;
import net.hasor.rsf.general.RSFConstants;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.runtime.RsfContext;
import net.hasor.rsf.runtime.RsfFilter;
import net.hasor.rsf.runtime.RsfOptionSet;
import net.hasor.rsf.serialize.SerializeFactory;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractRsfContext implements RsfContext {
    private int              defaultTimeout      = RSFConstants.ClientTimeout;
    private SerializeFactory serializeFactory    = null;
    private ExecutesManager  executesManager     = null;
    private EventLoopGroup   loopGroup           = null;
    private OptionManager    serverOptionManager = new OptionManager();
    private OptionManager    clientOptionManager = new OptionManager();
    //
    /**获取使用的{@link EventLoopGroup}*/
    public EventLoopGroup getLoopGroup() {
        return this.loopGroup;
    }
    /**获取{@link Executor}用于安排执行任务。*/
    public Executor getCallExecute(String serviceName) {
        return this.executesManager.getExecute(serviceName);
    }
    /**获取序列化管理器。*/
    public SerializeFactory getSerializeFactory() {
        return this.serializeFactory;
    }
    public byte getVersion() {
        return ProtocolVersion.V_1_0.value();
    }
    /**获取默认超时时间。*/
    public int getDefaultTimeout() {
        return this.defaultTimeout;
    }
    /**获取配置的服务器端选项*/
    public RsfOptionSet getServerOption() {
        return this.serverOptionManager;
    }
    /**获取配置的客户端选项*/
    public RsfOptionSet getClientOption() {
        return this.clientOptionManager;
    }
    /**获取服务上配置有效的过滤器。*/
    public abstract RsfFilter[] getRsfFilters(ServiceMetaData metaData);
    //
    public void init() {
        Settings settings = this.getSettings();
        this.serializeFactory = SerializeFactory.createFactory(settings);
        int queueSize = settings.getInteger("hasor.rsfConfig.queue.maxSize", 4096);
        int minCorePoolSize = settings.getInteger("hasor.rsfConfig.queue.minPoolSize", 1);
        int maxCorePoolSize = settings.getInteger("hasor.rsfConfig.queue.maxPoolSize", 7);
        long keepAliveTime = settings.getLong("hasor.rsfConfig.queue.keepAliveTime", 300L);
        this.executesManager = new ExecutesManager(minCorePoolSize, maxCorePoolSize, queueSize, keepAliveTime);
        //
        this.defaultTimeout = settings.getInteger("hasor.rsfConfig.client.defaultTimeout", RSFConstants.ClientTimeout);
        //
        int workerThread = settings.getInteger("hasor.rsfConfig.network.workerThread", 2);
        this.loopGroup = new NioEventLoopGroup(workerThread, new NameThreadFactory("RSF-Nio-%s"));
        //
        XmlNode[] clientOptSetArray = settings.getXmlNodeArray("hasor.rsfConfig.clientOptionSet");
        if (clientOptSetArray != null) {
            for (XmlNode optSet : clientOptSetArray) {
                for (XmlNode opt : optSet.getChildren("option")) {
                    String key = opt.getAttribute("key");
                    String var = opt.getText();
                    if (StringUtils.isBlank(key) == false) {
                        this.clientOptionManager.addOption(key, var);
                    }
                }
            }
        }
        //
        XmlNode[] serverOptSetArray = settings.getXmlNodeArray("hasor.rsfConfig.serverOptionSet");
        if (serverOptSetArray != null) {
            for (XmlNode optSet : serverOptSetArray) {
                for (XmlNode opt : optSet.getChildren("option")) {
                    String key = opt.getAttribute("key");
                    String var = opt.getText();
                    if (StringUtils.isBlank(key) == false) {
                        this.serverOptionManager.addOption(key, var);
                    }
                }
            }
        }
        //
    }
}