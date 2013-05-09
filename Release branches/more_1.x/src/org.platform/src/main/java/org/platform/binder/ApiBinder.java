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
package org.platform.binder;
import java.util.Map;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSessionListener;
import org.platform.context.InitContext;
import org.platform.context.SettingListener;
import org.platform.context.Settings;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.binder.LinkedBindingBuilder;
/**
 * 该类是代理了{@link Binder}并且提供了注册Servlet和Filter的方法。
 * @version : 2013-4-10
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ApiBinder {
    /**获取配置信息*/
    public Settings getSettings();
    /**获取Config*/
    public InitContext getInitContext();
    /**添加配置文件监听器*/
    public void addSettingsListener(SettingListener settingListener);
    /**使用传统表达式，创建一个{@link FilterBindingBuilder}。*/
    public FilterBindingBuilder filter(String urlPattern, String... morePatterns);
    /**使用正则表达式，创建一个{@link FilterBindingBuilder}。*/
    public FilterBindingBuilder filterRegex(String regex, String... regexes);
    /**使用传统表达式，创建一个{@link ServletBindingBuilder}。*/
    public ServletBindingBuilder serve(String urlPattern, String... morePatterns);
    /**使用正则表达式，创建一个{@link ServletBindingBuilder}。*/
    public ServletBindingBuilder serveRegex(String regex, String... regexes);
    /**绑定一个Servlet 异常处理程序。*/
    public ErrorBindingBuilder error(Class<? extends Throwable> error);
    /**注册一个Session监听器。*/
    public SessionListenerBindingBuilder sessionListener();
    /**获取用于初始化Guice的Binder。*/
    public Binder getGuiceBinder();
    /**在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记的注解）*/
    public Set<Class<?>> getClassSet(Class<?> featureType);
    /**注册一个bean。*/
    public BeanBindingBuilder newBean(String beanName);
    /*----------------------------------------------------------------------------*/
    /**负责配置Filter，参考Guice 3.0接口设计。*/
    public static interface BeanBindingBuilder {
        /**别名*/
        public BeanBindingBuilder aliasName(String aliasName);
        /**bean绑定的类型。*/
        public <T> LinkedBindingBuilder<T> bindType(Class<T> beanClass);
    }
    /**负责配置Filter，参考Guice 3.0接口设计。*/
    public static interface FilterBindingBuilder {
        public void through(Class<? extends Filter> filterKey);
        public void through(Key<? extends Filter> filterKey);
        public void through(Filter filter);
        public void through(Class<? extends Filter> filterKey, Map<String, String> initParams);
        public void through(Key<? extends Filter> filterKey, Map<String, String> initParams);
        public void through(Filter filter, Map<String, String> initParams);
    }
    /**负责配置Servlet，参考Guice 3.0接口设计。*/
    public static interface ServletBindingBuilder {
        public void with(Class<? extends HttpServlet> servletKey);
        public void with(Key<? extends HttpServlet> servletKey);
        public void with(HttpServlet servlet);
        public void with(Class<? extends HttpServlet> servletKey, Map<String, String> initParams);
        public void with(Key<? extends HttpServlet> servletKey, Map<String, String> initParams);
        public void with(HttpServlet servlet, Map<String, String> initParams);
    }
    /**负责配置Error。*/
    public static interface ErrorBindingBuilder {
        public void bind(Class<? extends ErrorHook> errorKey);
        public void bind(Key<? extends ErrorHook> errorKey);
        public void bind(ErrorHook errorHook);
        public void bind(Class<? extends ErrorHook> errorKey, Map<String, String> initParams);
        public void bind(Key<? extends ErrorHook> errorKey, Map<String, String> initParams);
        public void bind(ErrorHook errorHook, Map<String, String> initParams);
    }
    /**负责配置SessionListener。*/
    public static interface SessionListenerBindingBuilder {
        public void bind(Class<? extends HttpSessionListener> listenerKey);
        public void bind(Key<? extends HttpSessionListener> listenerKey);
        public void bind(HttpSessionListener sessionListener);
    }
}