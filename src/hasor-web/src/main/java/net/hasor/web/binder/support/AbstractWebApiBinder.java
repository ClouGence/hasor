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
package net.hasor.web.binder.support;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSessionListener;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.Provider;
import net.hasor.core.binder.AbstractBinder;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebEnvironment;
/**
 * 该类是{@link WebApiBinder}接口实现。
 * @version : 2013-4-10
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractWebApiBinder extends AbstractBinder implements WebApiBinder {
    //
    protected AbstractWebApiBinder(WebEnvironment envContext) {
        super(envContext);
    }
    public ServletContext getServletContext() {
        return this.getEnvironment().getServletContext();
    }
    public WebEnvironment getEnvironment() {
        return (WebEnvironment) super.getEnvironment();
    }
    //
    /*--------------------------------------------------------------------------------------Utils*/
    /***/
    private static List<String> newArrayList(String[] arr, String object) {
        ArrayList<String> list = new ArrayList<String>();
        if (arr != null)
            for (String item : arr)
                list.add(item);
        if (object != null)
            list.add(object);
        return list;
    }
    /**实体类型的Provider代理 */
    class InstanceProvider<T> implements Provider<T> {
        private T instance = null;
        public InstanceProvider(T instance) {
            this.instance = instance;
        }
        public T get() {
            return this.instance;
        }
    }
    /**Class类型的Provider代理 */
    class ClassProvider<T> implements Provider<T>, AppContextAware {
        private Class<? extends T> instanceType = null;
        private AppContext         appContext   = null;
        public ClassProvider(Class<? extends T> instanceType) {
            bindingType(instanceType);/*绑定类型*/
            registerAware(this);/*注册AppContextAware*/
            this.instanceType = instanceType;
        }
        public void setAppContext(AppContext appContext) {
            this.appContext = appContext;
        }
        public T get() {
            return this.appContext.getInstance(this.instanceType);
        }
    }
    //
    /*-------------------------------------------------------------------------------------Filter*/
    public FilterBindingBuilder filter(String urlPattern, String... morePatterns) {
        return new FiltersModuleBinder(UriPatternType.SERVLET, newArrayList(morePatterns, urlPattern));
    };
    public FilterBindingBuilder filterRegex(String regex, String... regexes) {
        return new FiltersModuleBinder(UriPatternType.REGEX, newArrayList(regexes, regex));
    };
    class FiltersModuleBinder implements FilterBindingBuilder {
        private final UriPatternType uriPatternType;
        private final List<String>   uriPatterns;
        //
        public FiltersModuleBinder(UriPatternType uriPatternType, List<String> uriPatterns) {
            this.uriPatternType = uriPatternType;
            this.uriPatterns = uriPatterns;
        }
        public void through(Class<? extends Filter> filterKey) {
            this.through(0, filterKey, null);
        }
        public void through(Filter filter) {
            this.through(0, filter, null);
        }
        public void through(Provider<Filter> filterProvider) {
            this.through(0, filterProvider, null);
        }
        public void through(Class<? extends Filter> filterKey, Map<String, String> initParams) {
            this.through(0, filterKey, initParams);
        }
        public void through(Filter filter, Map<String, String> initParams) {
            this.through(0, filter, initParams);
        }
        public void through(Provider<Filter> filterProvider, Map<String, String> initParams) {
            this.through(0, filterProvider, initParams);
        }
        //
        public void through(int index, Class<? extends Filter> filterKey) {
            this.through(index, filterKey, null);
        }
        public void through(int index, Filter filter) {
            this.through(index, filter, null);
        }
        public void through(int index, Provider<Filter> filterProvider) {
            this.through(index, filterProvider, null);
        }
        public void through(int index, Class<? extends Filter> filterKey, Map<String, String> initParams) {
            this.through(index, new ClassProvider<Filter>(filterKey), initParams);
        }
        public void through(int index, Filter filter, Map<String, String> initParams) {
            this.through(index, new InstanceProvider<Filter>(filter), initParams);
        }
        public void through(int index, Provider<Filter> filterProvider, Map<String, String> initParams) {
            if (initParams == null)
                initParams = new HashMap<String, String>();
            for (String pattern : this.uriPatterns) {
                UriPatternMatcher matcher = UriPatternType.get(this.uriPatternType, pattern);
                FilterDefinition define = new FilterDefinition(index, pattern, matcher, filterProvider, initParams);
                bindingType(FilterDefinition.class, define);
            }
        }
    }
    //
    /*------------------------------------------------------------------------------------Servlet*/
    public ServletBindingBuilder serve(String urlPattern, String... morePatterns) {
        return new ServletsModuleBuilder(UriPatternType.SERVLET, newArrayList(morePatterns, urlPattern));
    };
    public ServletBindingBuilder serveRegex(String regex, String... regexes) {
        return new ServletsModuleBuilder(UriPatternType.REGEX, newArrayList(regexes, regex));
    };
    class ServletsModuleBuilder implements ServletBindingBuilder {
        private final List<String>   uriPatterns;
        private final UriPatternType uriPatternType;
        public ServletsModuleBuilder(UriPatternType uriPatternType, List<String> uriPatterns) {
            this.uriPatterns = uriPatterns;
            this.uriPatternType = uriPatternType;
        }
        public void with(Class<? extends HttpServlet> servletKey) {
            this.with(0, servletKey, null);
        }
        public void with(HttpServlet servlet) {
            this.with(0, servlet, null);
        }
        public void with(Provider<HttpServlet> servletProvider) {
            this.with(0, servletProvider, null);
        }
        public void with(Class<? extends HttpServlet> servletKey, Map<String, String> initParams) {
            this.with(0, servletKey, initParams);
        }
        public void with(HttpServlet servlet, Map<String, String> initParams) {
            this.with(0, servlet, initParams);
        }
        public void with(Provider<HttpServlet> servletProvider, Map<String, String> initParams) {
            this.with(0, servletProvider, initParams);
        }
        //
        public void with(int index, Class<? extends HttpServlet> servletKey) {
            this.with(index, servletKey, null);
        }
        public void with(int index, HttpServlet servlet) {
            this.with(index, servlet, null);
        }
        public void with(int index, Provider<HttpServlet> servletProvider) {
            this.with(index, servletProvider, null);
        }
        public void with(int index, Class<? extends HttpServlet> servletKey, Map<String, String> initParams) {
            this.with(index, new ClassProvider<HttpServlet>(servletKey), initParams);
        }
        public void with(int index, HttpServlet servlet, Map<String, String> initParams) {
            this.with(index, new InstanceProvider<HttpServlet>(servlet), initParams);
        }
        public void with(int index, Provider<HttpServlet> servletProvider, Map<String, String> initParams) {
            if (initParams == null)
                initParams = new HashMap<String, String>();
            for (String pattern : this.uriPatterns) {
                UriPatternMatcher matcher = UriPatternType.get(this.uriPatternType, pattern);
                ServletDefinition define = new ServletDefinition(index, pattern, matcher, servletProvider, initParams);
                bindingType(ServletDefinition.class, define);
            }
        }
    }
    //
    /*---------------------------------------------------------------------ServletContextListener*/
    public ServletContextListenerBindingBuilder contextListener() {
        return new ServletContextListenerBuilder();
    }
    class ServletContextListenerBuilder implements ServletContextListenerBindingBuilder {
        public void bind(Class<? extends ServletContextListener> listenerKey) {
            this.bind(new ClassProvider<ServletContextListener>(listenerKey));
        }
        public void bind(ServletContextListener sessionListener) {
            this.bind(new InstanceProvider<ServletContextListener>(sessionListener));
        }
        public void bind(Provider<ServletContextListener> listenerProvider) {
            bindingType(ContextListenerDefinition.class, new ContextListenerDefinition(listenerProvider));
        }
    }
    //
    /*------------------------------------------------------------------------HttpSessionListener*/
    public SessionListenerBindingBuilder sessionListener() {
        return new SessionListenerBuilder();
    }
    class SessionListenerBuilder implements SessionListenerBindingBuilder {
        public void bind(Class<? extends HttpSessionListener> listenerKey) {
            this.bind(new ClassProvider<HttpSessionListener>(listenerKey));
        }
        public void bind(HttpSessionListener sessionListener) {
            this.bind(new InstanceProvider<HttpSessionListener>(sessionListener));
        }
        public void bind(Provider<HttpSessionListener> listenerProvider) {
            bindingType(HttpSessionListenerDefinition.class, new HttpSessionListenerDefinition(listenerProvider));
        }
    }
}