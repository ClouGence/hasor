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
package net.hasor.web.invoker;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.core.provider.InfoAwareProvider;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.web.*;
import net.hasor.web.definition.*;
import net.hasor.web.listener.ContextListenerDefinition;
import net.hasor.web.listener.HttpSessionListenerDefinition;
import net.hasor.web.pipeline.PipelineWebApiBinder;
import net.hasor.web.startup.RuntimeFilter;
import org.more.util.ArrayUtils;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionListener;
import java.util.*;
/**
 * 该类是{@link WebApiBinder}接口实现。
 * @version : 2013-4-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class InnerWebApiBinder extends PipelineWebApiBinder implements WebApiBinder {
    private final InstanceProvider<String> requestEncoding  = new InstanceProvider<String>("");
    private final InstanceProvider<String> responseEncoding = new InstanceProvider<String>("");
    private ServletVersion curVersion;
    private MimeType       mimeType;
    //
    // ------------------------------------------------------------------------------------------------------
    protected InnerWebApiBinder(ServletVersion curVersion, MimeType mimeType, ApiBinder apiBinder) {
        super(apiBinder);
        apiBinder.bindType(String.class).nameWith(RuntimeFilter.HTTP_REQUEST_ENCODING_KEY).toProvider(this.requestEncoding);
        apiBinder.bindType(String.class).nameWith(RuntimeFilter.HTTP_RESPONSE_ENCODING_KEY).toProvider(this.responseEncoding);
        this.curVersion = Hasor.assertIsNotNull(curVersion);
        this.mimeType = Hasor.assertIsNotNull(mimeType);
    }
    //
    // ------------------------------------------------------------------------------------------------------
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
    @Override
    public String getMimeType(String suffix) {
        return this.mimeType.getMimeType(suffix);
    }
    @Override
    public ServletVersion getServletVersion() {
        return this.curVersion;
    }
    //
    // ------------------------------------------------------------------------------------------------------
    @Override
    public WebApiBinder addPlugin(Class<? extends WebPlugin> webPlugin) {
        webPlugin = Hasor.assertIsNotNull(webPlugin);
        BindInfo<WebPlugin> bindInfo = this.bindType(WebPlugin.class).to(webPlugin).toInfo();
        this.bindType(WebPluginDefinition.class).toInstance(new WebPluginDefinition(bindInfo));
        return this;
    }
    @Override
    public WebApiBinder addPlugin(WebPlugin webPlugin) {
        webPlugin = Hasor.assertIsNotNull(webPlugin);
        BindInfo<WebPlugin> bindInfo = this.bindType(WebPlugin.class).toInstance(webPlugin).toInfo();
        this.bindType(WebPluginDefinition.class).toInstance(new WebPluginDefinition(bindInfo));
        return this;
    }
    @Override
    public WebApiBinder addPlugin(Provider<? extends WebPlugin> webPlugin) {
        webPlugin = Hasor.assertIsNotNull(webPlugin);
        BindInfo<WebPlugin> bindInfo = this.bindType(WebPlugin.class).toProvider(webPlugin).toInfo();
        this.bindType(WebPluginDefinition.class).toInstance(new WebPluginDefinition(bindInfo));
        return this;
    }
    @Override
    public WebApiBinder addPlugin(BindInfo<? extends WebPlugin> webPlugin) {
        webPlugin = Hasor.assertIsNotNull(webPlugin);
        this.bindType(WebPluginDefinition.class).toInstance(new WebPluginDefinition(webPlugin));
        return this;
    }
    //
    // ------------------------------------------------------------------------------------------------------
    @Override
    public WebApiBinder addSetup(Class<? extends MappingSetup> setup) {
        this.bindType(MappingSetup.class).to(Hasor.assertIsNotNull(setup));
        return this;
    }
    @Override
    public WebApiBinder addSetup(MappingSetup setup) {
        this.bindType(MappingSetup.class).toInstance(Hasor.assertIsNotNull(setup));
        return this;
    }
    @Override
    public WebApiBinder addSetup(Provider<? extends MappingSetup> setup) {
        this.bindType(MappingSetup.class).toProvider(Hasor.assertIsNotNull(setup));
        return this;
    }
    @Override
    public WebApiBinder addSetup(BindInfo<? extends MappingSetup> setup) {
        InfoAwareProvider<MappingSetup> provider = new InfoAwareProvider<MappingSetup>(Hasor.assertIsNotNull(setup));
        this.bindType(MappingSetup.class).toProvider(Hasor.autoAware(this.getEnvironment(), provider));
        return this;
    }
    //
    // ------------------------------------------------------------------------------------------------------
    @Override
    public void addServletListener(Class<? extends ServletContextListener> targetKey) {
        BindInfo<ServletContextListener> listenerRegister = bindType(ServletContextListener.class).to(targetKey).toInfo();
        this.addServletListener(listenerRegister);
    }
    @Override
    public void addServletListener(ServletContextListener sessionListener) {
        BindInfo<ServletContextListener> listenerRegister = bindType(ServletContextListener.class).toInstance(sessionListener).toInfo();
        this.addServletListener(listenerRegister);
    }
    @Override
    public void addServletListener(Provider<? extends ServletContextListener> targetProvider) {
        BindInfo<ServletContextListener> listenerRegister = bindType(ServletContextListener.class).toProvider(targetProvider).toInfo();
        this.addServletListener(listenerRegister);
    }
    @Override
    public void addServletListener(BindInfo<? extends ServletContextListener> targetRegister) {
        this.bindType(ContextListenerDefinition.class).uniqueName().toInstance(new ContextListenerDefinition(targetRegister));
    }
    @Override
    public void addSessionListener(Class<? extends HttpSessionListener> targetKey) {
        BindInfo<HttpSessionListener> listenerRegister = bindType(HttpSessionListener.class).to(targetKey).toInfo();
        this.addSessionListener(listenerRegister);
    }
    @Override
    public void addSessionListener(HttpSessionListener sessionListener) {
        BindInfo<HttpSessionListener> listenerRegister = bindType(HttpSessionListener.class).toInstance(sessionListener).toInfo();
        this.addSessionListener(listenerRegister);
    }
    @Override
    public void addSessionListener(Provider<? extends HttpSessionListener> targetProvider) {
        BindInfo<HttpSessionListener> listenerRegister = bindType(HttpSessionListener.class).toProvider(targetProvider).toInfo();
        this.addSessionListener(listenerRegister);
    }
    @Override
    public void addSessionListener(BindInfo<? extends HttpSessionListener> targetRegister) {
        bindType(HttpSessionListenerDefinition.class).uniqueName().toInstance(new HttpSessionListenerDefinition(targetRegister));
    }
    //
    // ------------------------------------------------------------------------------------------------------
    protected void throughFilter(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends Filter> filterRegister, Map<String, String> initParams) {
        FilterDefinition define = new FilterDefinition(index, pattern, matcher, filterRegister, initParams);
        bindType(AbstractDefinition.class).uniqueName().toInstance(define);
    }
    protected void throughInvFilter(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends InvokerFilter> filterRegister, Map<String, String> initParams) {
        InvokeFilterDefinition define = new InvokeFilterDefinition(index, pattern, matcher, filterRegister, initParams);
        bindType(AbstractDefinition.class).uniqueName().toInstance(define);
    }
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
    // ------------------------------------------------------------------------------------------------------
    //
    //
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
}