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
package net.hasor.web;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.Matcher;
import net.hasor.core.Provider;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSessionListener;
import java.util.Map;
/**
 * 提供了注册Servlet和Filter的方法。
 * @version : 2016-12-26
 * @author 赵永春 (zyc@hasor.net)
 */
public interface WebApiBinder extends ApiBinder, MimeType {
    /**获取ServletContext对象。*/
    public ServletContext getServletContext();

    /** 设置请求编码 */
    public WebApiBinder setRequestCharacter(String encoding);

    /** 设置响应编码 */
    public WebApiBinder setResponseCharacter(String encoding);

    /** 设置请求响应编码 */
    public WebApiBinder setEncodingCharacter(String requestEncoding, String responseEncoding);

    /**获取容器支持的Servlet版本。*/
    public ServletVersion getServletVersion();
    //

    /**使用 MappingTo 表达式，创建一个{@link ServletBindingBuilder}。*/
    public ServletBindingBuilder jeeServlet(String urlPattern, String... morePatterns);

    /**使用 MappingTo 表达式，创建一个{@link ServletBindingBuilder}。*/
    public ServletBindingBuilder jeeServlet(String[] morePatterns);

    /**使用 MappingTo 表达式，创建一个{@link MappingToBindingBuilder}。*/
    public MappingToBindingBuilder<Object> mappingTo(String urlPattern, String... morePatterns);

    /**使用 MappingTo 表达式，创建一个{@link MappingToBindingBuilder}。*/
    public MappingToBindingBuilder<Object> mappingTo(String[] morePatterns);

    //
    public void scanMappingTo();

    public void scanMappingTo(String... packages);

    public void scanMappingTo(Matcher<Class<?>> matcher, String... packages);
    //

    /**使用传统表达式，创建一个{@link FilterBindingBuilder}。*/
    public FilterBindingBuilder<InvokerFilter> filter(String urlPattern, String... morePatterns);

    /**使用传统表达式，创建一个{@link FilterBindingBuilder}。*/
    public FilterBindingBuilder<InvokerFilter> filter(String[] morePatterns);

    /**使用正则表达式，创建一个{@link FilterBindingBuilder}。*/
    public FilterBindingBuilder<InvokerFilter> filterRegex(String regex, String... regexes);

    /**使用正则表达式，创建一个{@link FilterBindingBuilder}。*/
    public FilterBindingBuilder<InvokerFilter> filterRegex(String[] regexes);
    //

    /**使用传统表达式，创建一个{@link FilterBindingBuilder}。*/
    public FilterBindingBuilder<Filter> jeeFilter(String urlPattern, String... morePatterns);

    /**使用传统表达式，创建一个{@link FilterBindingBuilder}。*/
    public FilterBindingBuilder<Filter> jeeFilter(String[] morePatterns);

    /**使用正则表达式，创建一个{@link FilterBindingBuilder}。*/
    public FilterBindingBuilder<Filter> jeeFilterRegex(String regex, String... regexes);

    /**使用正则表达式，创建一个{@link FilterBindingBuilder}。*/
    public FilterBindingBuilder<Filter> jeeFilterRegex(String[] regexes);
    //

    /**注册一个ServletContextListener监听器。*/
    public void addServletListener(Class<? extends ServletContextListener> targetKey);

    /**注册一个ServletContextListener监听器。*/
    public void addServletListener(ServletContextListener sessionListener);

    /**注册一个ServletContextListener监听器。*/
    public void addServletListener(Provider<? extends ServletContextListener> targetProvider);

    /**注册一个ServletContextListener监听器。*/
    public void addServletListener(BindInfo<? extends ServletContextListener> targetRegister);

    /**注册一个HttpSessionListener监听器。*/
    public void addSessionListener(Class<? extends HttpSessionListener> targetKey);

    /**注册一个HttpSessionListener监听器。*/
    public void addSessionListener(HttpSessionListener sessionListener);

    /**注册一个HttpSessionListener监听器。*/
    public void addSessionListener(Provider<? extends HttpSessionListener> targetProvider);

    /**注册一个HttpSessionListener监听器。*/
    public void addSessionListener(BindInfo<? extends HttpSessionListener> targetRegister);

    /**添加插件*/
    public WebApiBinder addPlugin(Class<? extends WebPlugin> webPlugin);

    /**添加插件*/
    public WebApiBinder addPlugin(WebPlugin webPlugin);

    /**添加插件*/
    public WebApiBinder addPlugin(Provider<? extends WebPlugin> webPlugin);

    /**添加插件*/
    public WebApiBinder addPlugin(BindInfo<? extends WebPlugin> webPlugin);

    /**添加MappingSetup*/
    public WebApiBinder addSetup(Class<? extends MappingSetup> setup);

    /**添加MappingSetup*/
    public WebApiBinder addSetup(MappingSetup setup);

    /**添加MappingSetup*/
    public WebApiBinder addSetup(Provider<? extends MappingSetup> setup);

    /**添加MappingSetup*/
    public WebApiBinder addSetup(BindInfo<? extends MappingSetup> setup);
    //
    /**负责配置Filter。*/
    public static interface FilterBindingBuilder<T> {
        public void through(Class<? extends T> filterKey);

        public void through(T filter);

        public void through(Provider<? extends T> filterProvider);

        public void through(BindInfo<? extends T> filterRegister);

        //
        public void through(Class<? extends T> filterKey, Map<String, String> initParams);

        public void through(T filter, Map<String, String> initParams);

        public void through(Provider<? extends T> filterProvider, Map<String, String> initParams);

        public void through(BindInfo<? extends T> filterRegister, Map<String, String> initParams);

        //
        public void through(int index, Class<? extends T> filterKey);

        public void through(int index, T filter);

        public void through(int index, Provider<? extends T> filterProvider);

        public void through(int index, BindInfo<? extends T> filterRegister);

        //
        public void through(int index, Class<? extends T> filterKey, Map<String, String> initParams);

        public void through(int index, T filter, Map<String, String> initParams);

        public void through(int index, Provider<? extends T> filterProvider, Map<String, String> initParams);

        public void through(int index, BindInfo<? extends T> filterRegister, Map<String, String> initParams);
    }
    /**负责配置Servlet。*/
    public static interface ServletBindingBuilder extends MappingToBindingBuilder<HttpServlet> {
        //
        public void with(Class<? extends HttpServlet> servletKey, Map<String, String> initParams);

        public void with(HttpServlet servlet, Map<String, String> initParams);

        public void with(Provider<? extends HttpServlet> servletProvider, Map<String, String> initParams);

        public void with(BindInfo<? extends HttpServlet> servletRegister, Map<String, String> initParams);

        //
        public void with(int index, Class<? extends HttpServlet> servletKey, Map<String, String> initParams);

        public void with(int index, HttpServlet servlet, Map<String, String> initParams);

        public void with(int index, Provider<? extends HttpServlet> servletProvider, Map<String, String> initParams);

        public void with(int index, BindInfo<? extends HttpServlet> servletRegister, Map<String, String> initParams);
    }
    /**负责配置Servlet。*/
    public static interface MappingToBindingBuilder<T> {
        public void with(Class<? extends T> targetKey);

        public void with(T target);

        public void with(Provider<? extends T> targetProvider);

        public void with(BindInfo<? extends T> targetInfo);

        //
        public void with(int index, Class<? extends T> targetKey);

        public void with(int index, T target);

        public void with(int index, Provider<? extends T> targetProvider);

        public void with(int index, BindInfo<? extends T> targetInfo);
    }
    //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**拦截这些后缀的请求，这些请求会被渲染器渲染。*/
    public RenderEngineBindingBuilder<RenderEngine> suffix(String urlPattern, String... morePatterns);

    /**拦截这些后缀的请求，这些请求会被渲染器渲染。*/
    public RenderEngineBindingBuilder<RenderEngine> suffix(String[] morePatterns);

    /**扫描Render注解配置的渲染器。*/
    public void scanAnnoRender();

    /**扫描Render注解配置的渲染器。*/
    public void scanAnnoRender(String... packages);

    /**扫描Render注解配置的渲染器。*/
    public void scanAnnoRender(Matcher<Class<? extends RenderEngine>> matcher, String... packages);
    //
    /**负责配置RenderEngine。*/
    public static interface RenderEngineBindingBuilder<T> {
        /**绑定实现。*/
        public void bind(Class<? extends T> filterKey);

        /**绑定实现。*/
        public void bind(T filter);

        /**绑定实现。*/
        public void bind(Provider<? extends T> filterProvider);

        /**绑定实现。*/
        public void bind(BindInfo<? extends T> filterRegister);
    }
}