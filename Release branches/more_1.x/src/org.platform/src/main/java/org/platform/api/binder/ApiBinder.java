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
package org.platform.api.binder;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import org.platform.api.context.InitContext;
import com.google.inject.Binder;
import com.google.inject.Key;
/**
 * 该类是代理了{@link Binder}并且提供了注册Servlet和Filter的方法。
 * @version : 2013-4-10
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ApiBinder {
    /**获取Config*/
    public InitContext getInitContext();
    /**使用传统表达式，创建一个{@link FilterBindingBuilder}。*/
    public FilterBindingBuilder filter(String urlPattern, String... morePatterns);
    /**使用正则表达式，创建一个{@link FilterBindingBuilder}。*/
    public FilterBindingBuilder filterRegex(String regex, String... regexes);
    /**使用传统表达式，创建一个{@link ServletBindingBuilder}。*/
    public ServletBindingBuilder serve(String urlPattern, String... morePatterns);
    /**使用正则表达式，创建一个{@link ServletBindingBuilder}。*/
    public ServletBindingBuilder serveRegex(String regex, String... regexes);
    /**获取用于初始化Guice的Binder。*/
    public Binder getGuiceBinder();
    /*----------------------------------------------------------------------------*/
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
}