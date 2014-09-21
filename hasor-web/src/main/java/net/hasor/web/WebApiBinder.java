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
package net.hasor.web;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSessionListener;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.Provider;
/**
 * 提供了注册Servlet和Filter的方法。
 * @version : 2013-4-10
 * @author 赵永春 (zyc@hasor.net)
 */
public interface WebApiBinder extends ApiBinder {
    /**获取ServletContext对象。*/
    public ServletContext getServletContext();
    //
    /**使用传统表达式，创建一个{@link FilterBindingBuilder}。*/
    public FilterBindingBuilder filter(String urlPattern, String... morePatterns);
    /**使用传统表达式，创建一个{@link FilterBindingBuilder}。*/
    public FilterBindingBuilder filter(String[] morePatterns);
    /**使用正则表达式，创建一个{@link FilterBindingBuilder}。*/
    public FilterBindingBuilder filterRegex(String regex, String... regexes);
    /**使用正则表达式，创建一个{@link FilterBindingBuilder}。*/
    public FilterBindingBuilder filterRegex(String[] regexes);
    /**使用传统表达式，创建一个{@link ServletBindingBuilder}。*/
    public ServletBindingBuilder serve(String urlPattern, String... morePatterns);
    /**使用传统表达式，创建一个{@link ServletBindingBuilder}。*/
    public ServletBindingBuilder serve(String[] morePatterns);
    /**使用正则表达式，创建一个{@link ServletBindingBuilder}。*/
    public ServletBindingBuilder serveRegex(String regex, String... regexes);
    /**使用正则表达式，创建一个{@link ServletBindingBuilder}。*/
    public ServletBindingBuilder serveRegex(String[] regexes);
    //
    /**注册一个Session监听器。*/
    public SessionListenerBindingBuilder sessionListener();
    /**注册一个ServletContextListener监听器。*/
    public ServletContextListenerBindingBuilder contextListener();
    //
    /**负责配置Filter。*/
    public static interface FilterBindingBuilder {
        public void through(Class<? extends Filter> filterKey);
        public void through(Filter filter);
        public void through(Provider<Filter> filterProvider);
        public void through(BindInfo<Filter> filterRegister);
        //
        public void through(Class<? extends Filter> filterKey, Map<String, String> initParams);
        public void through(Filter filter, Map<String, String> initParams);
        public void through(Provider<Filter> filterProvider, Map<String, String> initParams);
        public void through(BindInfo<Filter> filterRegister, Map<String, String> initParams);
        //
        public void through(int index, Class<? extends Filter> filterKey);
        public void through(int index, Filter filter);
        public void through(int index, Provider<Filter> filterProvider);
        public void through(int index, BindInfo<Filter> filterRegister);
        //
        public void through(int index, Class<? extends Filter> filterKey, Map<String, String> initParams);
        public void through(int index, Filter filter, Map<String, String> initParams);
        public void through(int index, Provider<Filter> filterProvider, Map<String, String> initParams);
        public void through(int index, BindInfo<Filter> filterRegister, Map<String, String> initParams);
    }
    /**负责配置Servlet。*/
    public static interface ServletBindingBuilder {
        public void with(Class<? extends HttpServlet> servletKey);
        public void with(HttpServlet servlet);
        public void with(Provider<HttpServlet> servletProvider);
        public void with(BindInfo<HttpServlet> servletRegister);
        //
        public void with(Class<? extends HttpServlet> servletKey, Map<String, String> initParams);
        public void with(HttpServlet servlet, Map<String, String> initParams);
        public void with(Provider<HttpServlet> servletProvider, Map<String, String> initParams);
        public void with(BindInfo<HttpServlet> servletRegister, Map<String, String> initParams);
        //
        public void with(int index, Class<? extends HttpServlet> servletKey);
        public void with(int index, HttpServlet servlet);
        public void with(int index, Provider<HttpServlet> servletProvider);
        public void with(int index, BindInfo<HttpServlet> servletRegister);
        //
        public void with(int index, Class<? extends HttpServlet> servletKey, Map<String, String> initParams);
        public void with(int index, HttpServlet servlet, Map<String, String> initParams);
        public void with(int index, Provider<HttpServlet> servletProvider, Map<String, String> initParams);
        public void with(int index, BindInfo<HttpServlet> servletRegister, Map<String, String> initParams);
    }
    /**负责配置SessionListener。*/
    public static interface SessionListenerBindingBuilder {
        public void bind(Class<? extends HttpSessionListener> listenerKey);
        public void bind(HttpSessionListener sessionListener);
        public void bind(Provider<HttpSessionListener> listenerProvider);
        public void bind(BindInfo<HttpSessionListener> listenerRegister);
    }
    /**负责配置ServletContextListener。*/
    public static interface ServletContextListenerBindingBuilder {
        public void bind(Class<? extends ServletContextListener> listenerKey);
        public void bind(ServletContextListener sessionListener);
        public void bind(Provider<ServletContextListener> listenerProvider);
        public void bind(BindInfo<ServletContextListener> listenerRegister);
    }
}