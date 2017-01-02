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
package net.hasor.web.pipeline;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.Provider;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.web.InvokerFilter;
import net.hasor.web.WebApiBinder;
import net.hasor.web.invoker.UriPatternMatcher;
import net.hasor.web.invoker.UriPatternType;
import net.hasor.web.startup.RuntimeFilter;
import org.more.util.ArrayUtils;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSessionListener;
import java.util.*;
/**
 * @version : 2013-4-10
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class PipelineWebApiBinder extends ApiBinderWrap implements WebApiBinder {
    private final InstanceProvider<String> requestEncoding  = new InstanceProvider<String>("");
    private final InstanceProvider<String> responseEncoding = new InstanceProvider<String>("");
    //
    protected PipelineWebApiBinder(ApiBinder apiBinder) {
        super(apiBinder);
        apiBinder.bindType(String.class).nameWith(RuntimeFilter.HTTP_REQUEST_ENCODING_KEY).toProvider(this.requestEncoding);
        apiBinder.bindType(String.class).nameWith(RuntimeFilter.HTTP_RESPONSE_ENCODING_KEY).toProvider(this.responseEncoding);
    }
    @Override
    public ServletContext getServletContext() {
        return (ServletContext) this.getEnvironment().getContext();
    }
    @Override
    public WebApiBinder setRequestCharacter(String encoding) {
        this.requestEncoding.set(encoding);
        return this;
    }
    @Override
    public WebApiBinder setResponseCharacter(String encoding) {
        this.responseEncoding.set(encoding);
        return this;
    }
    @Override
    public WebApiBinder setEncodingCharacter(String requestEncoding, String responseEncoding) {
        return this.setRequestCharacter(requestEncoding).setResponseCharacter(responseEncoding);
    }
    //
    /*--------------------------------------------------------------------------------------Utils*/
    /***/
    protected static List<String> newArrayList(final String[] arr, final String object) {
        ArrayList<String> list = new ArrayList<String>();
        if (arr != null) {
            Collections.addAll(list, arr);
        }
        if (object != null) {
            list.add(object);
        }
        return list;
    }
    //
    /*-------------------------------------------------------------------------------------Filter*/
    protected void throughFilter(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends Filter> filterRegister, Map<String, String> initParams) {
        FilterDefinition define = new FilterDefinition(index, pattern, matcher, filterRegister, initParams);
        bindType(FilterDefinition.class).uniqueName().toInstance(define);
    }
    protected abstract void throughInvFilter(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends InvokerFilter> filterRegister, Map<String, String> initParams);
    //
    @Override
    public FilterBindingBuilder<Filter> filter(final String urlPattern, final String... morePatterns) {
        return new FiltersModuleBinder<Filter>(Filter.class, UriPatternType.SERVLET, PipelineWebApiBinder.newArrayList(morePatterns, urlPattern)) {
            @Override
            protected void bindThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends Filter> filterRegister, Map<String, String> initParams) {
                throughFilter(index, pattern, matcher, filterRegister, initParams);
            }
        };
    }
    @Override
    public FilterBindingBuilder<Filter> filter(final String[] morePatterns) throws NullPointerException {
        if (ArrayUtils.isEmpty(morePatterns)) {
            throw new NullPointerException("Filter patterns is empty.");
        }
        return this.filter(null, morePatterns);
    }
    @Override
    public FilterBindingBuilder<Filter> filterRegex(final String regex, final String... regexes) {
        return new FiltersModuleBinder<Filter>(Filter.class, UriPatternType.REGEX, PipelineWebApiBinder.newArrayList(regexes, regex)) {
            @Override
            protected void bindThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends Filter> filterRegister, Map<String, String> initParams) {
                throughFilter(index, pattern, matcher, filterRegister, initParams);
            }
        };
    }
    @Override
    public FilterBindingBuilder<Filter> filterRegex(final String[] regexes) throws NullPointerException {
        if (ArrayUtils.isEmpty(regexes)) {
            throw new NullPointerException("Filter regexes is empty.");
        }
        return this.filterRegex(null, regexes);
    }
    @Override
    public FilterBindingBuilder<InvokerFilter> invFilter(String urlPattern, String... morePatterns) {
        return new FiltersModuleBinder<InvokerFilter>(InvokerFilter.class, UriPatternType.SERVLET, PipelineWebApiBinder.newArrayList(morePatterns, urlPattern)) {
            @Override
            protected void bindThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends InvokerFilter> filterRegister, Map<String, String> initParams) {
                throughInvFilter(index, pattern, matcher, filterRegister, initParams);
            }
        };
    }
    @Override
    public FilterBindingBuilder<InvokerFilter> invFilter(String[] morePatterns) {
        if (ArrayUtils.isEmpty(morePatterns)) {
            throw new NullPointerException("Filter patterns is empty.");
        }
        return this.invFilter(null, morePatterns);
    }
    @Override
    public FilterBindingBuilder<InvokerFilter> invFilterRegex(String regex, String... regexes) {
        return new FiltersModuleBinder<InvokerFilter>(InvokerFilter.class, UriPatternType.REGEX, PipelineWebApiBinder.newArrayList(regexes, regex)) {
            @Override
            protected void bindThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends InvokerFilter> filterRegister, Map<String, String> initParams) {
                throughInvFilter(index, pattern, matcher, filterRegister, initParams);
            }
        };
    }
    @Override
    public FilterBindingBuilder<InvokerFilter> invFilterRegex(String[] regexes) {
        if (ArrayUtils.isEmpty(regexes)) {
            throw new NullPointerException("Filter regexes is empty.");
        }
        return this.invFilterRegex(null, regexes);
    }
    //
    private abstract class FiltersModuleBinder<T> implements FilterBindingBuilder<T> {
        private final Class<T>       targetType;
        private final UriPatternType uriPatternType;
        private final List<String>   uriPatterns;
        //
        FiltersModuleBinder(Class<T> targetType, final UriPatternType uriPatternType, final List<String> uriPatterns) {
            this.targetType = targetType;
            this.uriPatternType = uriPatternType;
            this.uriPatterns = uriPatterns;
        }
        @Override
        public void through(final Class<? extends T> filterKey) {
            this.through(0, filterKey, null);
        }
        @Override
        public void through(final T filter) {
            this.through(0, filter, null);
        }
        @Override
        public void through(final Provider<? extends T> filterProvider) {
            this.through(0, filterProvider, null);
        }
        @Override
        public void through(BindInfo<? extends T> filterRegister) {
            this.through(0, filterRegister, null);
        }
        @Override
        public void through(final Class<? extends T> filterKey, final Map<String, String> initParams) {
            this.through(0, filterKey, initParams);
        }
        @Override
        public void through(final T filter, final Map<String, String> initParams) {
            this.through(0, filter, initParams);
        }
        @Override
        public void through(final Provider<? extends T> filterProvider, final Map<String, String> initParams) {
            this.through(0, filterProvider, initParams);
        }
        @Override
        public void through(BindInfo<? extends T> filterRegister, Map<String, String> initParams) {
            this.through(0, filterRegister, initParams);
        }
        @Override
        public void through(final int index, final Class<? extends T> filterKey) {
            this.through(index, filterKey, null);
        }
        @Override
        public void through(final int index, final T filter) {
            this.through(index, filter, null);
        }
        @Override
        public void through(final int index, final Provider<? extends T> filterProvider) {
            this.through(index, filterProvider, null);
        }
        @Override
        public void through(int index, BindInfo<? extends T> filterRegister) {
            this.through(index, filterRegister, null);
        }
        //
        @Override
        public void through(final int index, final Class<? extends T> filterKey, final Map<String, String> initParams) {
            BindInfo<T> filterRegister = bindType(targetType).uniqueName().to(filterKey).toInfo();
            this.through(index, filterRegister, initParams);
        }
        @Override
        public void through(final int index, final T filter, final Map<String, String> initParams) {
            BindInfo<T> filterRegister = bindType(targetType).uniqueName().toInstance(filter).toInfo();
            this.through(index, filterRegister, initParams);
        }
        @Override
        public void through(final int index, final Provider<? extends T> filterProvider, Map<String, String> initParams) {
            BindInfo<T> filterRegister = bindType(targetType).uniqueName().toProvider((Provider<T>) filterProvider).toInfo();
            this.through(index, filterRegister, initParams);
        }
        @Override
        public void through(int index, BindInfo<? extends T> filterRegister, Map<String, String> initParams) {
            if (initParams == null) {
                initParams = new HashMap<String, String>();
            }
            for (String pattern : this.uriPatterns) {
                UriPatternMatcher matcher = UriPatternType.get(this.uriPatternType, pattern);
                this.bindThrough(index, pattern, matcher, filterRegister, initParams);
            }
        }
        protected abstract void bindThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends T> filterRegister, Map<String, String> initParams);
    }
    //
    /*------------------------------------------------------------------------------------Servlet*/
    @Override
    public ServletBindingBuilder serve(final String urlPattern, final String... morePatterns) {
        return new ServletsModuleBuilder(UriPatternType.SERVLET, PipelineWebApiBinder.newArrayList(morePatterns, urlPattern));
    }
    @Override
    public ServletBindingBuilder serve(final String[] morePatterns) {
        if (ArrayUtils.isEmpty(morePatterns)) {
            throw new NullPointerException("Servlet patterns is empty.");
        }
        return this.serve(null, morePatterns);
    }
    @Override
    public ServletBindingBuilder serveRegex(final String regex, final String... regexes) {
        return new ServletsModuleBuilder(UriPatternType.REGEX, PipelineWebApiBinder.newArrayList(regexes, regex));
    }
    @Override
    public ServletBindingBuilder serveRegex(final String[] regexes) {
        if (ArrayUtils.isEmpty(regexes)) {
            throw new NullPointerException("Servlet regexes is empty.");
        }
        return this.serveRegex(null, regexes);
    }
    private class ServletsModuleBuilder implements ServletBindingBuilder {
        private final List<String>   uriPatterns;
        private final UriPatternType uriPatternType;
        ServletsModuleBuilder(final UriPatternType uriPatternType, final List<String> uriPatterns) {
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
        public void with(final Provider<? extends HttpServlet> servletProvider) {
            this.with(0, servletProvider, null);
        }
        @Override
        public void with(BindInfo<? extends HttpServlet> servletRegister) {
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
        public void with(final Provider<? extends HttpServlet> servletProvider, final Map<String, String> initParams) {
            this.with(0, servletProvider, initParams);
        }
        @Override
        public void with(BindInfo<? extends HttpServlet> servletRegister, Map<String, String> initParams) {
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
        public void with(final int index, final Provider<? extends HttpServlet> servletProvider) {
            this.with(index, servletProvider, null);
        }
        @Override
        public void with(int index, BindInfo<? extends HttpServlet> servletRegister) {
            this.with(index, servletRegister, null);
        }
        //
        @Override
        public void with(final int index, final Class<? extends HttpServlet> servletKey, final Map<String, String> initParams) {
            BindInfo<HttpServlet> servletRegister = bindType(HttpServlet.class).uniqueName().to(servletKey).toInfo();
            this.with(index, servletRegister, initParams);
        }
        @Override
        public void with(final int index, final HttpServlet servlet, final Map<String, String> initParams) {
            BindInfo<HttpServlet> servletRegister = bindType(HttpServlet.class).uniqueName().toInstance(servlet).toInfo();
            this.with(index, servletRegister, initParams);
        }
        @Override
        public void with(final int index, final Provider<? extends HttpServlet> servletProvider, Map<String, String> initParams) {
            BindInfo<HttpServlet> servletRegister = bindType(HttpServlet.class).uniqueName().toProvider((Provider<HttpServlet>) servletProvider).toInfo();
            this.with(index, servletRegister, initParams);
        }
        @Override
        public void with(int index, BindInfo<? extends HttpServlet> servletRegister, Map<String, String> initParams) {
            if (initParams == null) {
                initParams = new HashMap<String, String>();
            }
            for (String pattern : this.uriPatterns) {
                UriPatternMatcher matcher = UriPatternType.get(this.uriPatternType, pattern);
                ServletDefinition define = new ServletDefinition(index, pattern, matcher, (BindInfo<HttpServlet>) servletRegister, initParams);
                bindType(ServletDefinition.class).uniqueName().toInstance(define);/*单列*/
            }
        }
    }
    //
    /*---------------------------------------------------------------------ServletContextListener*/
    @Override
    public ServletContextListenerBindingBuilder contextListener() {
        return new ServletContextListenerBuilder();
    }
    private class ServletContextListenerBuilder implements ServletContextListenerBindingBuilder {
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
        public void bind(final Provider<? extends ServletContextListener> listenerProvider) {
            BindInfo<ServletContextListener> listenerRegister = bindType(ServletContextListener.class).toProvider((Provider<ServletContextListener>) listenerProvider).toInfo();
            this.bind(listenerRegister);
        }
        @Override
        public void bind(BindInfo<? extends ServletContextListener> listenerRegister) {
            bindType(ContextListenerDefinition.class).uniqueName().toInstance(new ContextListenerDefinition(listenerRegister));
        }
    }
    //
    /*------------------------------------------------------------------------HttpSessionListener*/
    @Override
    public SessionListenerBindingBuilder sessionListener() {
        return new SessionListenerBuilder();
    }
    private class SessionListenerBuilder implements SessionListenerBindingBuilder {
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
        public void bind(final Provider<? extends HttpSessionListener> listenerProvider) {
            BindInfo<HttpSessionListener> listenerRegister = bindType(HttpSessionListener.class).toProvider((Provider<HttpSessionListener>) listenerProvider).toInfo();
            this.bind(listenerRegister);
        }
        @Override
        public void bind(BindInfo<? extends HttpSessionListener> listenerRegister) {
            bindType(HttpSessionListenerDefinition.class).uniqueName().toInstance(new HttpSessionListenerDefinition(listenerRegister));
        }
    }
}