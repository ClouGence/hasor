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
package net.hasor.rsf.center.server.domain;
import net.hasor.core.Environment;
import net.hasor.core.Settings;
import org.more.util.ResourcesUtils;
import org.more.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
/**
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfCenterSettings {
    protected static final Logger logger = LoggerFactory.getLogger(RsfCenterSettings.class);
    // - 通用
    private WorkMode workMode;
    private String   workDir;
    //
    private String   version;
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
    public RsfCenterSettings(Environment environment) throws ClassNotFoundException {
        Settings settings = environment.getSettings();
        this.workMode = settings.getEnum("rsfCenter.workAt", WorkMode.class, WorkMode.Alone);
        this.workDir = environment.getWorkSpaceDir();
        try {
            InputStream verIns = ResourcesUtils.getResourceAsStream("/META-INF/rsf-center.version");
            List<String> dataLines = IOUtils.readLines(verIns, "UTF-8");
            this.version = !dataLines.isEmpty() ? dataLines.get(0) : null;
        } catch (Throwable e) {
            logger.error("read version file:/META-INF/rsf-center.version failed -> {}", e);
            this.version = "undefined";
        }
        //
        this.threadSize = settings.getInteger("rsfCenter.polling.threadSize", 10);
        if (this.threadSize < 1) {
            this.threadSize = 3;
        }
        this.queueMaxSize = settings.getInteger("rsfCenter.polling.queueMaxSize", 20000);
        this.sleepTime = settings.getInteger("rsfCenter.polling.sleepTime", 1000);
        //
        this.providerExpireTime = settings.getInteger("rsfCenter.serviceManager.providerExpireTime", 30000);
        this.consumerExpireTime = settings.getInteger("rsfCenter.serviceManager.consumerExpireTime", 30000);
        this.allowAnonymous = settings.getBoolean("rsfCenter.auth.allowAnonymous", true);
        //
        ClassLoader classLoader = environment.getClassLoader();
        this.dataAdapterType = classLoader.loadClass(settings.getString("rsfCenter.adapterConfig.dataAdapter"));
        this.authQueryType = classLoader.loadClass(settings.getString("rsfCenter.adapterConfig.authQuery"));
    }
    //
    /** 获取RSF-Center服务器版本 */
    public String getVersion() {
        return this.version;
    }
    public WorkMode getWorkMode() {
        return workMode;
    }
    public String getWorkDir() {
        return workDir;
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