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
package org.platform.context.support;
import static org.platform.PlatformConfig.Platform_LoadPackages;
import java.util.Enumeration;
import java.util.Set;
import javax.servlet.ServletContext;
import org.more.util.ClassUtil;
import org.platform.Assert;
import org.platform.context.Config;
import org.platform.context.InitContext;
import org.platform.context.support.clock.Clock;
/**
 * {@link InitContext}接口的实现类。
 * @version : 2013-4-9
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractInitContext implements InitContext {
    private Config config    = null;
    private long   startTime = 0;   //系统启动时间
    protected AbstractInitContext(Config config) {
        this.config = config;
        this.startTime = Clock.getSyncTime();
        Assert.isNotNull(config);
    }
    /*----------------------------------------------------------------------*/
    public long getStartTime() {
        return startTime;
    }
    /**获取环境初始化参数。*/
    public String getInitParameter(String name) {
        return this.config.getInitParameter(name);
    };
    /**获取环境初始化参数名称集合。*/
    public Enumeration<String> getInitParameterNames() {
        return this.config.getInitParameterNames();
    };
    /**获取应用程序配置。*/
    public Config getConfig() {
        return this.config;
    }
    @Override
    public ServletContext getServletContext() {
        return this.config.getServletContext();
    }
    @Override
    public Set<Class<?>> getClassSet(Class<?> featureType) {
        if (featureType == null)
            return null;
        String loadPackages = this.getConfig().getSettings().getString(Platform_LoadPackages);
        String[] spanPackage = loadPackages.split(",");
        return ClassUtil.getClassSet(spanPackage, featureType);
    }
}