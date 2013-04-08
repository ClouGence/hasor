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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.more.core.global.Global;
import org.platform.api.scope.Scope;
import org.platform.api.scope.Scope.ScopeEnum;
import org.platform.api.services.IService;
/**
 * 
 * @version : 2013-3-26
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AppContext {
    /**获取初始化上下文*/
    public abstract InitContext getInitContext();
    /**通过名称创建bean实例，使用guice。*/
    public abstract <T> T getBean(String name);
    /**通过类型创建该类实例，使用guice*/
    public abstract <T> T getBean(Class<T> beanType);
    /**通过名称创建bean实例，使用guice。*/
    public abstract <T extends IService> T getService(String servicesName);
    /**通过类型创建该类实例，使用guice*/
    public abstract <T extends IService> T getService(Class<T> servicesType);
    /**获取已经注册的Bean名称。*/
    public abstract List<String> getBeanNames();
    /**取得{@link HttpServletRequest}类型对象。*/
    public abstract HttpServletRequest getHttpRequest();
    /**取得{@link HttpServletResponse}类型对象。*/
    public abstract HttpServletResponse getHttpResponse();
    /**取得{@link HttpSession}类型对象。*/
    public HttpSession getHttpSession(boolean create) {
        return this.getHttpRequest().getSession(create);
    }
    /**获取作用域操作对象。*/
    public abstract Scope getScope(ScopeEnum scopeEnmu);
    /**获取应用程序配置。*/
    public Global getSettings() {
        return this.getInitContext().getSettings();
    }
    /*----------------------------------------------------------------------*/
    /**生成一个UUID字符串。*/
    public static String genUUID() {
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
    }
}