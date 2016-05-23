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
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.List;
import org.more.util.ResourcesUtils;
import org.more.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.Environment;
import net.hasor.core.Settings;
/**
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfCenterCfg {
    protected static final Logger logger = LoggerFactory.getLogger(RsfCenterCfg.class);
    // - 通用
    private WorkMode              workMode;
    private String                workDir;
    //
    private String                centerVersion;
    private int                   pushQueueMaxSize;
    private int                   pushSleepTime;
    private String                anonymousAppCode;
    //
    //
    private RsfCenterCfg() {}
    public static RsfCenterCfg buildFormConfig(Environment env) throws UnknownHostException {
        RsfCenterCfg cfg = new RsfCenterCfg();
        Settings settings = env.getSettings();
        cfg.workMode = settings.getEnum("rsfCenter.workAt", WorkMode.class, WorkMode.Alone);
        //
        cfg.workDir = env.getWorkSpaceDir();
        try {
            InputStream verIns = ResourcesUtils.getResourceAsStream("/META-INF/rsf-center.version");
            List<String> dataLines = IOUtils.readLines(verIns, "UTF-8");
            cfg.centerVersion = !dataLines.isEmpty() ? dataLines.get(0) : null;
        } catch (Throwable e) {
            logger.error("read version file:/META-INF/rsf-center.version failed -> {}", e);
            cfg.centerVersion = "undefined";
        }
        //
        cfg.pushQueueMaxSize = settings.getInteger("rsfCenter.push.queueMaxSize", 100);// 推送队列最大长度，当待推送服务达到这个阀值之后注册中心会做一次推送动作。
        cfg.pushSleepTime = settings.getInteger("rsfCenter.push.sleepTime", 1000);// 当遇到推送队列满了之后等待多长时间重试一次，如果重试的时候队列依然满的，那么转发到其它机器上。
        cfg.anonymousAppCode = settings.getString("rsfCenter.push.anonymousAppCode", "anonymous");// 默认推送使用的：应用程序代码
        return cfg;
    }
    //
    /** 获取RSF-Center服务器版本 */
    public String getVersion() {
        return this.centerVersion;
    }
    public WorkMode getWorkMode() {
        return workMode;
    }
    public void setWorkMode(WorkMode workMode) {
        this.workMode = workMode;
    }
    public String getWorkDir() {
        return workDir;
    }
    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }
    public int getPushQueueMaxSize() {
        return this.pushQueueMaxSize;
    }
    public void setPushQueueMaxSize(int pushQueueMaxSize) {
        this.pushQueueMaxSize = pushQueueMaxSize;
    }
    public int getPushSleepTime() {
        return pushSleepTime;
    }
    public void setPushSleepTime(int pushSleepTime) {
        this.pushSleepTime = pushSleepTime;
    }
    public String getAnonymousAppCode() {
        return anonymousAppCode;
    }
    public void setAnonymousAppCode(String anonymousAppCode) {
        this.anonymousAppCode = anonymousAppCode;
    }
}