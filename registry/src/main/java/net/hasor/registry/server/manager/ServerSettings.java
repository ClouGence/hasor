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
package net.hasor.registry.server.manager;
import net.hasor.registry.RsfCenterSettings;
import net.hasor.rsf.RsfEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServerSettings {
    protected static final Logger logger = LoggerFactory.getLogger(ServerSettings.class);
    //
    private int      threadSize;
    private int      queueMaxSize;
    private int      sleepTime;
    //
    private int      providerExpireTime;
    private int      consumerExpireTime;
    private boolean  allowAnonymous;
    private Class<?> dataAdapterType;
    private Class<?> authQueryType;
    //
    //
    public ServerSettings(RsfEnvironment rsfEnvironment, RsfCenterSettings settings) throws ClassNotFoundException {
        //
        this.threadSize = settings.getInteger("hasor.registry.polling.threadSize", 10);
        if (this.threadSize < 1) {
            this.threadSize = 3;
        }
        this.queueMaxSize = settings.getInteger("hasor.registry.polling.queueMaxSize", 20000);
        this.sleepTime = settings.getInteger("hasor.registry.polling.sleepTime", 1000);
        //
        this.providerExpireTime = settings.getInteger("hasor.registry.serviceManager.providerExpireTime", 60000);
        this.consumerExpireTime = settings.getInteger("hasor.registry.serviceManager.consumerExpireTime", 60000);
        this.allowAnonymous = settings.getBoolean("hasor.registry.auth.allowAnonymous", true);
        //
        ClassLoader classLoader = rsfEnvironment.getClassLoader();
        this.dataAdapterType = classLoader.loadClass(settings.getString("hasor.registry.adapterConfig.dataAdapter"));
        this.authQueryType = classLoader.loadClass(settings.getString("hasor.registry.adapterConfig.authQuery"));
    }
    //
    public int getThreadSize() {
        return threadSize;
    }
    public int getQueueMaxSize() {
        return queueMaxSize;
    }
    public int getSleepTime() {
        return sleepTime;
    }
    //
    public int getProviderExpireTime() {
        return providerExpireTime;
    }
    public int getConsumerExpireTime() {
        return consumerExpireTime;
    }
    public boolean isAllowAnonymous() {
        return allowAnonymous;
    }
    public Class<?> getDataAdapterType() {
        return dataAdapterType;
    }
    public Class<?> getAuthQueryType() {
        return authQueryType;
    }
}