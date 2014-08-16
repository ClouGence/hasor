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
import net.hasor.core.Provider;
import net.hasor.core.BindInfo;
import net.hasor.core.binder.AbstractBinder;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebEnvironment;
import org.more.util.ArrayUtils;
/**
 * 该类是{@link WebApiBinder}接口实现。
 * @version : 2013-4-10
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractWebApiBinder extends AbstractBinder implements WebApiBinder {
    //
    protected AbstractWebApiBinder(final WebEnvironment envContext) {
        super(envContext);
    }
    @Override
    public ServletContext getServletContext() {
        return this.getEnvironment().getServletContext();
    }
    @Override
    public WebEnvironment getEnvironment() {
        return (WebEnvironment) super.getEnvironment();
    }
    //
    /*--------------------------------------------------------------------------------------Utils*/
    /***/
    private static List<String> newArrayList(final String[] arr, final String object) {
        ArrayList<String> list = new ArrayList<String>();
        if (arr != null) {
            for (String item : arr) {
                list.add(item);
            }
        }
        if (object != null) {
            list.add(object);
        }
        return list;
    }
    //
    /*-------------------------------------------------------------------------------------Filter*/
    @Override
    public FilterBindingBuilder filter(final String urlPattern, final String... morePatterns) {
        return new FiltersModuleBinder(UriPatternType.SERVLET, AbstractWebApiBinder.newArrayList(morePatterns, urlPattern));
    };
    @Override
    public FilterBindingBuilder filter(final String[] morePatterns) throws NullPointerException {
        if (ArrayUtils.isEmpty(morePatterns)) {
            throw new NullPointerException("Filter patterns is empty.");
        }
        return this.filter(null, morePatterns);
    }
    @Override
    public FilterBindingBuilder filterRegex(final String regex, final String... regexes) {
        return new FiltersModuleBinder(UriPatternType.REGEX, AbstractWebApiBinder.newArrayList(regexes, regex));
    };
    @Override
    public FilterBindingBuilder filterRegex(final String[] regexes) throws NullPointerException {
        if (ArrayUtils.isEmpty(regexes)) {
            throw new NullPointerException("Filter regexes is empty.");
        }
        return this.filterRegex(null, regexes);
    }
    class FiltersModuleBinder implements FilterBindingBuilder {
        private final UriPatternType uriPatternType;
        private final List<String>   uriPatterns;
        //
        public FiltersModuleBinder(final UriPatternType uriPatternType, final List<String> uriPatterns) {
            this.uriPatternType = uriPatternType;
            this.uriPatterns = uriPatterns;
        }
        @Override
        public void through(final Class<? extends Filter> filterKey) {
            this.through(0, filterKey, null);
        }
        @Override
        public void through(final Filter filter) {
            this.through(0, filter, null);
        }
        @Override
        public void through(final Provider<Filter> filterProvider) {
            this.through(0, filterProvider, null);
        }
        @Override
        public void through(BindInfo<Filter> filterRegister) {
            this.through(0, filterRegister, null);
        }
        @Override
        public void through(final Class<? extends Filter> filterKey, final Map<String, String> initParams) {
            this.through(0, filterKey, initParams);
        }
        @Override
        public void through(final Filter filter, final Map<String, String> initParams) {
            this.through(0, filter, initParams);
        }
        @Override
        public void through(final Provider<Filter> filterProvider, final Map<String, String> initParams) {
            this.through(0, filterProvider, initParams);
        }
        @Override
        public void through(BindInfo<Filter> filterRegister, Map<String, String> initParams) {
            this.through(0, filterRegister, initParams);
        }
        @Override
        public void through(final int index, final Class<? extends Filter> filterKey) {
            this.through(index, filterKey, null);
        }
        @Override
        public void through(final int index, final Filter filter) {
            this.through(index, filter, null);
        }
        @Override
        public void through(final int index, final Provider<Filter> filterProvider) {
            this.through(index, filterProvider, null);
        }
        @Override
        public void through(int index, BindInfo<Filter> filterRegister) {
            this.through(index, filterRegister, null);
        }
        //
        @Override
        public void through(final int index, final Class<? extends Filter> filterKey, final Map<String, String> initParams) {
            BindInfo<Filter> filterRegister = bindType(Filter.class).to(filterKey).toInfo();
            this.through(index, filterRegister, initParams);
        }
        @Override
        public void through(final int index, final Filter filter, final Map<String, String> initParams) {
            BindInfo<Filter> filterRegister = bindType(Filter.class).toInstance(filter).toInfo();
            this.through(index, filterRegister, initParams);
        }
        @Override
        public void through(final int index, final Provider<Filter> filterProvider, Map<String, String> initParams) {
            BindInfo<Filter> filterRegister = bindType(Filter.class).toProvider(filterProvider).toInfo();
            this.through(index, filterRegister, initParams);
        }
        @Override
        public void through(int index, BindInfo<Filter> filterRegister, Map<String, String> initParams) {
            if (initParams == null) {
                initParams = new HashMap<String, String>();
            }
            for (String pattern : this.uriPatterns) {
                UriPatternMatcher matcher = UriPatternType.get(this.uriPatternType, pattern);
                FilterDefinition define = new FilterDefinition(index, pattern, matcher, filterRegister, initParams);
                bindType(FilterDefinition.class, define);
            }
        }
    }
    //
    /*------------------------------------------------------------------------------------Servlet*/
    @Override
    public ServletBindingBuilder serve(final String urlPattern, final String... morePatterns) {
        return new ServletsModuleBuilder(UriPatternType.SERVLET, AbstractWebApiBinder.newArrayList(morePatterns, urlPattern));
    };
    @Override
    public ServletBindingBuilder serve(final String[] morePatterns) {
        if (ArrayUtils.isEmpty(morePatterns)) {
            throw new NullPointerException("Servlet patterns is empty.");
        }
        return this.serve(null, morePatterns);
    }
    @Override
    public ServletBindingBuilder serveRegex(final String regex, final String... regexes) {
        return new ServletsModuleBuilder(UriPatternType.REGEX, AbstractWebApiBinder.newArrayList(regexes, regex));
    };
    @Override
    public ServletBindingBuilder serveRegex(final String[] regexes) {
        if (ArrayUtils.isEmpty(regexes)) {
            throw new NullPointerException("Servlet regexes is empty.");
        }
        return this.serveRegex(null, regexes);
    }
    class ServletsModuleBuilder implements ServletBindingBuilder {
        private final List<String>   uriPatterns;
        private final UriPatternType uriPatternType;
        public ServletsModuleBuilder(final UriPatternType uriPatternType, final List<String> uriPatterns) {
            this.uriPatterns = uriPatterns;
            this.uriPatternType = uriPatternType;
        }
        @Override
        public void with(final Class<? extends HttpServlet> servletKey) {
            this.with(0, servletKey, null);
        }
        @Override
        public void with(final HttpServlet servlet) {
            this.with(0, servlet, null);
        }
        @Override
        public void with(final Provider<HttpServlet> servletProvider) {
            this.with(0, servletProvider, null);
        }
        @Override
        public void with(BindInfo<HttpServlet> servletRegister) {
            this.with(0, servletRegister, null);
        }
        @Override
        public void with(final Class<? extends HttpServlet> servletKey, final Map<String, String> initParams) {
            this.with(0, servletKey, initParams);
        }
        @Override
        public void with(final HttpServlet servlet, final Map<String, String> initParams) {
            this.with(0, servlet, initParams);
        }
        @Override
        public void with(final Provider<HttpServlet> servletProvider, final Map<String, String> initParams) {
            this.with(0, servletProvider, initParams);
        }
        @Override
        public void with(BindInfo<HttpServlet> servletRegister, Map<String, String> initParams) {
            this.with(0, servletRegister, initParams);
        }
        @Override
        public void with(final int index, final Class<? extends HttpServlet> servletKey) {
            this.with(index, servletKey, null);
        }
        @Override
        public void with(final int index, final HttpServlet servlet) {
            this.with(index, servlet, null);
        }
        @Override
        public void with(final int index, final Provider<HttpServlet> servletProvider) {
            this.with(index, servletProvider, null);
        }
        @Override
        public void with(int index, BindInfo<HttpServlet> servletRegister) {
            this.with(index, servletRegister, null);
        }
        //
        @Override
        public void with(final int index, final Class<? extends HttpServlet> servletKey, final Map<String, String> initParams) {
            BindInfo<HttpServlet> servletRegister = bindType(HttpServlet.class).to(servletKey).toInfo();
            this.with(index, servletRegister, initParams);
        }
        @Override
        public void with(final int index, final HttpServlet servlet, final Map<String, String> initParams) {
            BindInfo<HttpServlet> servletRegister = bindType(HttpServlet.class).toInstance(servlet).toInfo();
            this.with(index, servletRegister, initParams);
        }
        @Override
        public void with(final int index, final Provider<HttpServlet> servletProvider, Map<String, String> initParams) {
            BindInfo<HttpServlet> servletRegister = bindType(HttpServlet.class).toProvider(servletProvider).toInfo();
            this.with(index, servletRegister, initParams);
        }
        @Override
        public void with(int index, BindInfo<HttpServlet> servletRegister, Map<String, String> initParams) {
            if (initParams == null) {
                initParams = new HashMap<String, String>();
            }
            for (String pattern : this.uriPatterns) {
                UriPatternMatcher matcher = UriPatternType.get(this.uriPatternType, pattern);
                ServletDefinition define = new ServletDefinition(index, pattern, matcher, servletRegister, initParams);
                bindType(ServletDefinition.class, define);/*单列*/
            }
        }
    }
    //
    /*---------------------------------------------------------------------ServletContextListener*/
    @Override
    public ServletContextListenerBindingBuilder contextListener() {
        return new ServletContextListenerBuilder();
    }
    class ServletContextListenerBuilder implements ServletContextListenerBindingBuilder {
        @Override
        public void bind(final Class<? extends ServletContextListener> listenerKey) {
            BindInfo<ServletContextListener> listenerRegister = bindType(ServletContextListener.class).to(listenerKey).toInfo();
            this.bind(listenerRegister);
        }
        @Override
        public void bind(final ServletContextListener sessionListener) {
            BindInfo<ServletContextListener> listenerRegister = bindType(ServletContextListener.class).toInstance(sessionListener).toInfo();
            this.bind(listenerRegister);
        }
        @Override
        public void bind(final Provider<ServletContextListener> listenerProvider) {
            BindInfo<ServletContextListener> listenerRegister = bindType(ServletContextListener.class).toProvider(listenerProvider).toInfo();
            this.bind(listenerRegister);
        }
        @Override
        public void bind(BindInfo<ServletContextListener> listenerRegister) {
            bindType(ContextListenerDefinition.class, new ContextListenerDefinition(listenerRegister));
        }
    }
    //
    /*------------------------------------------------------------------------HttpSessionListener*/
    @Override
    public SessionListenerBindingBuilder sessionListener() {
        return new SessionListenerBuilder();
    }
    class SessionListenerBuilder implements SessionListenerBindingBuilder {
        @Override
        public void bind(final Class<? extends HttpSessionListener> listenerKey) {
            BindInfo<HttpSessionListener> listenerRegister = bindType(HttpSessionListener.class).to(listenerKey).toInfo();
            this.bind(listenerRegister);
        }
        @Override
        public void bind(final HttpSessionListener sessionListener) {
            BindInfo<HttpSessionListener> listenerRegister = bindType(HttpSessionListener.class).toInstance(sessionListener).toInfo();
            this.bind(listenerRegister);
        }
        @Override
        public void bind(final Provider<HttpSessionListener> listenerProvider) {
            BindInfo<HttpSessionListener> listenerRegister = bindType(HttpSessionListener.class).toProvider(listenerProvider).toInfo();
            this.bind(listenerRegister);
        }
        @Override
        public void bind(BindInfo<HttpSessionListener> listenerRegister) {
            bindType(HttpSessionListenerDefinition.class, new HttpSessionListenerDefinition(listenerRegister));
        }
    }
}