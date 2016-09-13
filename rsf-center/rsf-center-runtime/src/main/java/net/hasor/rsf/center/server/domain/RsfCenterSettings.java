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
    private String   centerVersion;
    private int      pushQueueMaxSize;
    private int      pushSleepTime;
    private String   anonymousAppCode;
    //
    public RsfCenterSettings(Environment environment) {
        Settings settings = environment.getSettings();
        this.workMode = settings.getEnum("rsfCenter.workAt", WorkMode.class, WorkMode.Alone);
        this.workDir = environment.getWorkSpaceDir();
        try {
            InputStream verIns = ResourcesUtils.getResourceAsStream("/META-INF/rsf-center.version");
            List<String> dataLines = IOUtils.readLines(verIns, "UTF-8");
            this.centerVersion = !dataLines.isEmpty() ? dataLines.get(0) : null;
        } catch (Throwable e) {
            logger.error("read version file:/META-INF/rsf-center.version failed -> {}", e);
            this.centerVersion = "undefined";
        }
        //
        this.pushQueueMaxSize = settings.getInteger("rsfCenter.push.queueMaxSize", 100);// 推送队列最大长度，当待推送服务达到这个阀值之后注册中心会做一次推送动作。
        this.pushSleepTime = settings.getInteger("rsfCenter.push.sleepTime", 1000);// 当遇到推送队列满了之后等待多长时间重试一次，如果重试的时候队列依然满的，那么转发到其它机器上。
        this.anonymousAppCode = settings.getString("rsfCenter.push.anonymousAppCode", "anonymous");// 默认推送使用的：应用程序代码
    }
    //
    /** 获取RSF-Center服务器版本 */
    public String getVersion() {
        return this.centerVersion;
    }
    public WorkMode getWorkMode() {
        return workMode;
    }
    public String getWorkDir() {
        return workDir;
    }
    public int getPushQueueMaxSize() {
        return this.pushQueueMaxSize;
    }
    public int getPushSleepTime() {
        return pushSleepTime;
    }
    public String getAnonymousAppCode() {
        return anonymousAppCode;
    }
}