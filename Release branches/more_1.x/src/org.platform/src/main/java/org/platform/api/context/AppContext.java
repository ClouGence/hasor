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
package org.platform.api.context;
import java.io.File;
import java.util.List;
import java.util.UUID;
import org.more.core.global.Global;
import org.platform.Assert;
import com.google.inject.Injector;
/**
 * 
 * @version : 2013-3-26
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AppContext {
    private InitContext initContext = null;
    /**获取应用程序配置。*/
    public Global getSettings() {
        InitContext initContext = this.getInitContext();
        Assert.isNotNull(initContext, "AppContext.getInitContext() return is null.");
        return initContext.getSettings();
    };
    /**获取初始化上下文*/
    public InitContext getInitContext() {
        if (this.initContext == null)
            this.initContext = this.getGuice().getInstance(InitContext.class);
        return this.initContext;
    }
    /**获得Guice环境。*/
    public abstract Injector getGuice();
    /**通过名称创建bean实例，使用guice。*/
    public <T> T getBean(String name) {
        Class<T> classType = this.getBeanType(name);
        if (classType == null)
            return null;
        return this.getBean(classType);
    };
    /**通过名获取Bean的类型。*/
    public abstract <T> Class<T> getBeanType(String name);
    /**通过类型创建该类实例，使用guice*/
    public <T> T getBean(Class<T> beanType) {
        return this.getGuice().getInstance(beanType);
    };
    //    /**通过名称创建bean实例，使用guice。*/
    //    public abstract <T extends IService> T getService(String servicesName);
    //    /**通过类型创建该类实例，使用guice*/
    //    public abstract <T extends IService> T getService(Class<T> servicesType);
    /**获取已经注册的Bean名称。*/
    public abstract List<String> getBeanNames();
    /*----------------------------------------------------------------------*/
    /**生成一个UUID字符串，32个字符串长度。*/
    public static String genIDBy32() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    /**生成一个UUID字符串，36个字符串长度。*/
    public static String genIDBy36() {
        return UUID.randomUUID().toString();
    }
    /**
     * 生成路径算法。
     * @param number 数字
     * @param size 每个目录下可以拥有的子目录或文件数目。
     */
    public static String genPath(long number, int size) {
        StringBuffer buffer = new StringBuffer();
        long b = size;
        long c = number;
        do {
            long m = number % b;
            buffer.append(m + File.separator);
            c = number / b;
            number = c;
        } while (c > 0);
        return buffer.reverse().toString();
    };
}