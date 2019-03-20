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
import net.hasor.core.*;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.core.exts.aop.Matchers;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.utils.StringUtils;
import net.hasor.web.*;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.annotation.Render;
import net.hasor.web.definition.*;
import net.hasor.web.startup.RuntimeFilter;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSessionListener;
import java.lang.reflect.Modifier;
import java.util.*;
/**
 * 该类是{@link WebApiBinder}接口实现。
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class InvokerWebApiBinder extends ApiBinderWrap implements WebApiBinder {
    private final InstanceProvider<String> requestEncoding  = new InstanceProvider<String>("");
    private final InstanceProvider<String> responseEncoding = new InstanceProvider<String>("");
    private       ServletVersion           curVersion;
    private       MimeType                 mimeType;
    //
    // ------------------------------------------------------------------------------------------------------
    protected InvokerWebApiBinder(ServletVersion curVersion, MimeType mimeType, ApiBinder apiBinder) {
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
    public void addPlugin(Class<? extends WebPlugin> webPlugin) {
        webPlugin = Hasor.assertIsNotNull(webPlugin);
        BindInfo<WebPlugin> bindInfo = this.bindType(WebPlugin.class).to(webPlugin).toInfo();
        this.bindType(WebPluginDefinition.class).toInstance(new WebPluginDefinition(bindInfo));
    }
    @Override
    public void addPlugin(WebPlugin webPlugin) {
        webPlugin = Hasor.assertIsNotNull(webPlugin);
        BindInfo<WebPlugin> bindInfo = this.bindType(WebPlugin.class).toInstance(webPlugin).toInfo();
        this.bindType(WebPluginDefinition.class).toInstance(new WebPluginDefinition(bindInfo));
    }
    @Override
    public void addPlugin(Provider<? extends WebPlugin> webPlugin) {
        webPlugin = Hasor.assertIsNotNull(webPlugin);
        BindInfo<WebPlugin> bindInfo = this.bindType(WebPlugin.class).toProvider(webPlugin).toInfo();
        this.bindType(WebPluginDefinition.class).toInstance(new WebPluginDefinition(bindInfo));
    }
    @Override
    public void addPlugin(BindInfo<? extends WebPlugin> webPlugin) {
        webPlugin = Hasor.assertIsNotNull(webPlugin);
        this.bindType(WebPluginDefinition.class).toInstance(new WebPluginDefinition(webPlugin));
    }
    //
    // ------------------------------------------------------------------------------------------------------
    @Override
    public void addDiscoverer(Class<? extends MappingDiscoverer> discoverer) {
        discoverer = Hasor.assertIsNotNull(discoverer);
        BindInfo<MappingDiscoverer> bindInfo = this.bindType(MappingDiscoverer.class).to(discoverer).toInfo();
        this.addDiscoverer(bindInfo);
    }
    @Override
    public void addDiscoverer(MappingDiscoverer discoverer) {
        discoverer = Hasor.assertIsNotNull(discoverer);
        BindInfo<MappingDiscoverer> bindInfo = this.bindType(MappingDiscoverer.class).toInstance(discoverer).toInfo();
        this.addDiscoverer(bindInfo);
    }
    @Override
    public void addDiscoverer(Provider<? extends MappingDiscoverer> discoverer) {
        discoverer = Hasor.assertIsNotNull(discoverer);
        BindInfo<MappingDiscoverer> bindInfo = this.bindType(MappingDiscoverer.class).toProvider(discoverer).toInfo();
        this.addDiscoverer(bindInfo);
    }
    @Override
    public void addDiscoverer(BindInfo<? extends MappingDiscoverer> discoverer) {
        discoverer = Hasor.assertIsNotNull(discoverer);
        MappingDiscovererDefinition definition = Hasor.autoAware(getEnvironment(), new MappingDiscovererDefinition(discoverer));
        this.bindType(MappingDiscovererDefinition.class).toInstance(definition);
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
    //
    // ------------------------------------------------------------------------------------------------------
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
    @Override
    public FilterBindingBuilder<InvokerFilter> filter(String urlPattern, String... morePatterns) {
        List<String> uriPatterns = checkEmpty(newArrayList(morePatterns, urlPattern), "Filter patterns is empty.");
        return new FiltersModuleBinder<InvokerFilter>(InvokerFilter.class, UriPatternType.SERVLET, uriPatterns) {
            @Override
            protected void bindThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends InvokerFilter> filterRegister, Map<String, String> initParams) {
                filterThrough(index, pattern, matcher, filterRegister, initParams);
            }
        };
    }
    @Override
    public FilterBindingBuilder<InvokerFilter> filter(String[] morePatterns) {
        return this.filter(null, checkEmpty(morePatterns, "Filter patterns is empty."));
    }
    @Override
    public FilterBindingBuilder<InvokerFilter> filterRegex(String regex, String... regexes) {
        List<String> uriPatterns = checkEmpty(newArrayList(regexes, regex), "Filter patterns is empty.");
        return new FiltersModuleBinder<InvokerFilter>(InvokerFilter.class, UriPatternType.REGEX, uriPatterns) {
            @Override
            protected void bindThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends InvokerFilter> filterRegister, Map<String, String> initParams) {
                filterThrough(index, pattern, matcher, filterRegister, initParams);
            }
        };
    }
    @Override
    public FilterBindingBuilder<InvokerFilter> filterRegex(String[] regexes) {
        return this.filterRegex(null, checkEmpty(regexes, "Filter patterns is empty."));
    }
    //
    // ------------------------------------------------------------------------------------------------------
    @Override
    public FilterBindingBuilder<Filter> jeeFilter(final String urlPattern, final String... morePatterns) {
        List<String> uriPatterns = checkEmpty(newArrayList(morePatterns, urlPattern), "Filter patterns is empty.");
        return new FiltersModuleBinder<Filter>(Filter.class, UriPatternType.SERVLET, uriPatterns) {
            @Override
            protected void bindThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends Filter> filterRegister, Map<String, String> initParams) {
                jeeFilterThrough(index, pattern, matcher, filterRegister, initParams);
            }
        };
    }
    @Override
    public FilterBindingBuilder<Filter> jeeFilter(final String[] morePatterns) throws NullPointerException {
        return this.jeeFilter(null, checkEmpty(morePatterns, "Filter patterns is empty."));
    }
    @Override
    public FilterBindingBuilder<Filter> jeeFilterRegex(final String regex, final String... regexes) {
        List<String> uriPatterns = checkEmpty(newArrayList(regexes, regex), "Filter patterns is empty.");
        return new FiltersModuleBinder<Filter>(Filter.class, UriPatternType.REGEX, uriPatterns) {
            @Override
            protected void bindThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends Filter> filterRegister, Map<String, String> initParams) {
                jeeFilterThrough(index, pattern, matcher, filterRegister, initParams);
            }
        };
    }
    @Override
    public FilterBindingBuilder<Filter> jeeFilterRegex(final String[] regexes) throws NullPointerException {
        return this.jeeFilterRegex(null, checkEmpty(regexes, "Filter patterns is empty."));
    }
    protected void jeeFilterThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends Filter> filterRegister, Map<String, String> initParams) {
        FilterDefinition define = new FilterDefinition(index, pattern, matcher, filterRegister, initParams);
        bindType(AbstractDefinition.class).uniqueName().toInstance(define);
        bindToCreater(filterRegister, define);
    }
    protected void filterThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends InvokerFilter> filterRegister, Map<String, String> initParams) {
        InvokeFilterDefinition define = new InvokeFilterDefinition(index, pattern, matcher, filterRegister, initParams);
        bindType(AbstractDefinition.class).uniqueName().toInstance(define);
        bindToCreater(filterRegister, define);
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
            BindInfo<T> filterRegister = bindType(targetType).uniqueName().toProvider(filterProvider).toInfo();
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
    protected void jeeServlet(int index, String pattern, BindInfo<? extends HttpServlet> servletRegister, Map<String, String> initParams) {
        InMappingServlet define = new InMappingServlet(index, servletRegister, pattern, initParams, this.getServletContext());
        bindType(InMappingDef.class).uniqueName().toInstance(define);/* InMappingServlet是单例 */
        bindToCreater(servletRegister, define);
    }
    @Override
    public ServletBindingBuilder jeeServlet(final String urlPattern, final String... morePatterns) {
        return new ServletsModuleBuilder(checkEmpty(newArrayList(morePatterns, urlPattern), "Servlet patterns is empty."));
    }
    @Override
    public ServletBindingBuilder jeeServlet(final String[] morePatterns) {
        return this.jeeServlet(null, checkEmpty(morePatterns, "Servlet patterns is empty."));
    }
    //
    private class ServletsModuleBuilder extends MappingToBuilder<HttpServlet> implements ServletBindingBuilder {
        ServletsModuleBuilder(final List<String> uriPatterns) {
            super(HttpServlet.class, uriPatterns);
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
        //
        @Override
        public void with(int index, BindInfo<? extends HttpServlet> servletRegister) {
            this.with(index, servletRegister, null);
        }
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
            BindInfo<HttpServlet> servletRegister = bindType(HttpServlet.class).uniqueName().toProvider(servletProvider).toInfo();
            this.with(index, servletRegister, initParams);
        }
        //
        @Override
        public void with(int index, BindInfo<? extends HttpServlet> servletRegister, Map<String, String> initParams) {
            if (initParams == null) {
                initParams = new HashMap<String, String>();
            }
            for (String pattern : this.getUriPatterns()) {
                jeeServlet(index, pattern, servletRegister, initParams);
            }
        }
    }
    //
    // ------------------------------------------------------------------------------------------------------
    @Override
    public MappingToBindingBuilder<Object> mappingTo(String urlPattern, String... morePatterns) {
        return new MappingToBuilder<Object>(Object.class, checkEmpty(newArrayList(morePatterns, urlPattern), "mappingTo patterns is empty.")) {
            @Override
            public void with(int index, BindInfo<? extends Object> targetInfo) {
                for (String pattern : this.getUriPatterns()) {
                    if (StringUtils.isBlank(pattern)) {
                        continue;
                    }
                    InMappingDef define = new InMappingDef(index, targetInfo, pattern, Matchers.anyMethod(), true);
                    bindType(InMappingDef.class).uniqueName().toInstance(define);
                }
            }
            @Override
            protected Class<Object> findBindType(Class<Object> targetClass, Object target) {
                return (Class<Object>) target.getClass();
            }
            @Override
            protected Class<Object> findBindType(Class<Object> targetClass, Class<?> targetKey) {
                return (Class<Object>) targetKey;
            }
            @Override
            protected Class<Object> findBindType(Class<Object> targetClass, Provider<?> targetProvider) {
                return (Class<Object>) targetProvider.get().getClass();
            }
        };
    }
    @Override
    public MappingToBindingBuilder<Object> mappingTo(String[] morePatterns) {
        return this.mappingTo(null, checkEmpty(morePatterns, "mappingTo patterns is empty."));
    }
    @Override
    public void loadMappingTo(Set<Class<?>> mabeMappingToSet) {
        this.loadMappingTo(mabeMappingToSet, Matchers.anyClass());
    }
    @Override
    public void loadMappingTo(Set<Class<?>> mabeMappingToSet, Matcher<Class<?>> matcher) {
        int counts = 0;
        if (mabeMappingToSet != null && !mabeMappingToSet.isEmpty()) {
            for (Class<?> type : mabeMappingToSet) {
                if (matcher.matches(type)) {
                    loadMappingTo(type);
                    counts++;
                }
            }
        }
        if (counts == 0) {
            logger.warn("mapingTo -> exit , not matcher any MappingTo.");
        } else {
            logger.info("mapingTo -> found {} counts.");
        }
    }
    public void loadMappingTo(Class<?> mabeMappingType) {
        Hasor.assertIsNotNull(mabeMappingType, "class is null.");
        int modifier = mabeMappingType.getModifiers();
        if (checkIn(modifier, Modifier.INTERFACE) || checkIn(modifier, Modifier.ABSTRACT) || mabeMappingType.isArray() || mabeMappingType.isEnum()) {
            throw new IllegalStateException(mabeMappingType.getName() + " must be normal Bean");
        }
        if (!mabeMappingType.isAnnotationPresent(MappingTo.class)) {
            throw new IllegalStateException(mabeMappingType.getName() + " must be configure @MappingTo");
        }
        //
        MappingTo mto = mabeMappingType.getAnnotation(MappingTo.class);
        if (HttpServlet.class.isAssignableFrom(mabeMappingType)) {
            this.jeeServlet(mto.value()).with((Class<? extends HttpServlet>) mabeMappingType);
            logger.info("mapingTo[Servlet] -> type ‘{}’ mappingTo: ‘{}’.", mabeMappingType.getName(), mto.value());
        } else {
            this.mappingTo(mto.value()).with(mabeMappingType);
            logger.info("mapingTo[Object] -> type ‘{}’ mappingTo: ‘{}’.", mabeMappingType.getName(), mto.value());
        }
    }
    //
    // ------------------------------------------------------------------------------------------------------
    /**加载Render注解配置的渲染器。*/
    public void loadRender(Set<Class<?>> renderSet) {
        this.loadRender(renderSet, Matchers.anyClass());
    }
    /**加载Render注解配置的渲染器。*/
    public void loadRender(Set<Class<?>> renderSet, Matcher<Class<?>> matcher) {
        int counts = 0;
        if (renderSet != null && !renderSet.isEmpty()) {
            for (Class<?> type : renderSet) {
                if (matcher.matches(type)) {
                    loadRender(type);
                    counts++;
                }
            }
        }
        if (counts == 0) {
            logger.warn("render -> exit , not matcher any Render.");
        } else {
            logger.info("render -> found {} counts.");
        }
    }
    /**加载Render注解配置的渲染器。*/
    public void loadRender(Class<?> renderClass) {
        Hasor.assertIsNotNull(renderClass, "class is null.");
        int modifier = renderClass.getModifiers();
        if (checkIn(modifier, Modifier.INTERFACE) || checkIn(modifier, Modifier.ABSTRACT) || renderClass.isArray() || renderClass.isEnum()) {
            throw new IllegalStateException(renderClass.getName() + " must be normal Bean");
        }
        if (!renderClass.isAnnotationPresent(Render.class)) {
            throw new IllegalStateException(renderClass.getName() + " must be configure @Render");
        }
        if (!RenderEngine.class.isAssignableFrom(renderClass)) {
            throw new IllegalStateException(renderClass.getName() + " must be implements RenderEngine.");
        }
        //
        Render renderInfo = renderClass.getAnnotation(Render.class);
        if (renderInfo != null && renderInfo.value().length > 0) {
            String[] renderName = checkEmpty(renderInfo.value(), "Render patterns is empty.");
            suffix(renderName).bind((Class<? extends RenderEngine>) renderClass);
        }
    }
    //
    // ------------------------------------------------------------------------------------------------------
    /**拦截这些后缀的请求，这些请求会被渲染器渲染。*/
    public WebApiBinder.RenderEngineBindingBuilder suffix(String suffix, String... moreSuffix) {
        return new RenderEngineBindingBuilderImpl(checkEmpty(newArrayList(moreSuffix, suffix), "Render patterns is empty.")) {
            @Override
            protected void bindSuffix(List<String> suffixList, BindInfo<? extends RenderEngine> bindInfo) {
                InvokerWebApiBinder.this.bindSuffix(suffixList, bindInfo);
            }
        };
    }
    /**拦截这些后缀的请求，这些请求会被渲染器渲染。*/
    public WebApiBinder.RenderEngineBindingBuilder suffix(String[] suffixArrays) {
        return new RenderEngineBindingBuilderImpl(checkEmpty(Arrays.asList(suffixArrays), "Render patterns is empty.")) {
            @Override
            protected void bindSuffix(List<String> suffixList, BindInfo<? extends RenderEngine> bindInfo) {
                InvokerWebApiBinder.this.bindSuffix(suffixList, bindInfo);
            }
        };
    }
    //
    private void bindSuffix(List<String> suffixList, BindInfo<? extends RenderEngine> bindInfo) {
        suffixList = Collections.unmodifiableList(suffixList);
        this.bindType(RenderDefinition.class).toInstance(new RenderDefinition(suffixList, bindInfo)).toInfo();
    }
    //
    // ------------------------------------------------------------------------------------------------------
    //
    private abstract class MappingToBuilder<T> implements MappingToBindingBuilder<T> {
        private       Class<T>     targetClass;
        private final List<String> uriPatterns;
        MappingToBuilder(Class<T> targetClass, List<String> uriPatterns) {
            this.targetClass = targetClass;
            this.uriPatterns = uriPatterns;
        }
        @Override
        public void with(final Class<? extends T> targetKey) {
            this.with(0, targetKey);
        }
        @Override
        public void with(final T target) {
            this.with(0, target);
        }
        @Override
        public void with(final Provider<? extends T> targetProvider) {
            this.with(0, targetProvider);
        }
        @Override
        public void with(BindInfo<? extends T> targetInfo) {
            this.with(0, targetInfo);
        }
        @Override
        public void with(final int index, final Class<? extends T> targetKey) {
            Hasor.assertIsNotNull(targetKey);
            Class<T> bindType = this.findBindType(this.targetClass, targetKey);
            BindInfo<? extends T> info = bindType(bindType).uniqueName().to(targetKey).toInfo();
            this.with(index, info);
        }
        @Override
        public void with(final int index, final T target) {
            Hasor.assertIsNotNull(target);
            Class<T> bindType = this.findBindType(this.targetClass, target);
            BindInfo<? extends T> info = bindType(bindType).uniqueName().toInstance(target).toInfo();
            this.with(index, info);
        }
        @Override
        public void with(final int index, final Provider<? extends T> targetProvider) {
            Hasor.assertIsNotNull(targetProvider);
            Class<T> bindType = this.findBindType(this.targetClass, targetProvider);
            BindInfo<? extends T> info = bindType(bindType).uniqueName().toProvider(targetProvider).toInfo();
            this.with(index, info);
        }
        public List<String> getUriPatterns() {
            return this.uriPatterns;
        }
        //
        //
        protected Class<T> findBindType(Class<T> targetClass, T target) {
            return targetClass;
        }
        protected Class<T> findBindType(Class<T> targetClass, Class<? extends T> targetKey) {
            return targetClass;
        }
        protected Class<T> findBindType(Class<T> targetClass, Provider<? extends T> targetProvider) {
            return targetClass;
        }
        public abstract void with(int index, BindInfo<? extends T> targetInfo);
    }
    private abstract class RenderEngineBindingBuilderImpl implements WebApiBinder.RenderEngineBindingBuilder {
        private List<String> suffixList;
        public RenderEngineBindingBuilderImpl(List<String> suffixList) {
            Set<String> suffixSet = new LinkedHashSet<String>();
            for (String str : suffixList) {
                if (StringUtils.isNotBlank(str)) {
                    suffixSet.add(str.toUpperCase());
                }
            }
            this.suffixList = new ArrayList<String>(suffixSet);
        }
        @Override
        public <T extends RenderEngine> void bind(Class<T> renderEngineType) {
            if (!this.suffixList.isEmpty()) {
                bindSuffix(this.suffixList, bindType(RenderEngine.class).uniqueName().to(renderEngineType).toInfo());
            }
        }
        @Override
        public void bind(RenderEngine renderEngine) {
            if (!this.suffixList.isEmpty()) {
                bindSuffix(this.suffixList, bindType(RenderEngine.class).uniqueName().toInstance(renderEngine).toInfo());
            }
        }
        @Override
        public void bind(Provider<? extends RenderEngine> renderEngineProvider) {
            if (!this.suffixList.isEmpty()) {
                bindSuffix(this.suffixList, bindType(RenderEngine.class).uniqueName().toProvider(renderEngineProvider).toInfo());
            }
        }
        @Override
        public void bind(BindInfo<? extends RenderEngine> renderEngineInfo) {
            if (!this.suffixList.isEmpty()) {
                bindSuffix(this.suffixList, Hasor.assertIsNotNull(renderEngineInfo));
            }
        }
        protected abstract void bindSuffix(List<String> suffixList, BindInfo<? extends RenderEngine> bindInfo);
    }
    //
    // ------------------------------------------------------------------------------------------------------
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
    /** 通过位运算决定check是否在data里。 */
    private boolean checkIn(final int data, final int check) {
        int or = data | check;
        return or == data;
    }
    private static String[] checkEmpty(String[] patternArrays, String npeMessage) {
        checkEmpty(Arrays.asList(patternArrays), npeMessage);
        return patternArrays;
    }
    private static List<String> checkEmpty(List<String> patternArrays, String npeMessage) {
        boolean needThrow = true;
        for (String pattern : patternArrays) {
            if (StringUtils.isBlank(pattern)) {
                continue;
            }
            needThrow = false;
            break;
        }
        if (needThrow) {
            throw new NullPointerException(npeMessage);
        }
        return patternArrays;
    }
}