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
import net.hasor.core.provider.InfoAwareProvider;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.utils.BeanUtils;
import net.hasor.utils.StringUtils;
import net.hasor.web.*;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.definition.*;
import net.hasor.web.listener.ContextListenerDefinition;
import net.hasor.web.listener.HttpSessionListenerDefinition;
import net.hasor.web.render.RenderApiBinderImpl;
import net.hasor.web.startup.RuntimeFilter;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSessionListener;
import java.lang.reflect.Method;
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
    private ServletVersion      curVersion;
    private MimeType            mimeType;
    private RenderApiBinderImpl renderBinder;
    //
    // ------------------------------------------------------------------------------------------------------
    protected InvokerWebApiBinder(ServletVersion curVersion, MimeType mimeType, ApiBinder apiBinder) {
        super(apiBinder);
        apiBinder.bindType(String.class).nameWith(RuntimeFilter.HTTP_REQUEST_ENCODING_KEY).toProvider(this.requestEncoding);
        apiBinder.bindType(String.class).nameWith(RuntimeFilter.HTTP_RESPONSE_ENCODING_KEY).toProvider(this.responseEncoding);
        this.curVersion = Hasor.assertIsNotNull(curVersion);
        this.mimeType = Hasor.assertIsNotNull(mimeType);
        this.renderBinder = new RenderApiBinderImpl(apiBinder) {
        };
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
    @Override
    public FilterBindingBuilder<InvokerFilter> filter(String urlPattern, String... morePatterns) {
        return new FiltersModuleBinder<InvokerFilter>(InvokerFilter.class, UriPatternType.SERVLET, newArrayList(morePatterns, urlPattern)) {
            @Override
            protected void bindThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends InvokerFilter> filterRegister, Map<String, String> initParams) {
                filterThrough(index, pattern, matcher, filterRegister, initParams);
            }
        };
    }
    @Override
    public FilterBindingBuilder<InvokerFilter> filter(String[] morePatterns) {
        if (StringUtils.isEmptyArray(morePatterns)) {
            throw new NullPointerException("Filter patterns is empty.");
        }
        return this.filter(null, morePatterns);
    }
    @Override
    public FilterBindingBuilder<InvokerFilter> filterRegex(String regex, String... regexes) {
        return new FiltersModuleBinder<InvokerFilter>(InvokerFilter.class, UriPatternType.REGEX, newArrayList(regexes, regex)) {
            @Override
            protected void bindThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends InvokerFilter> filterRegister, Map<String, String> initParams) {
                filterThrough(index, pattern, matcher, filterRegister, initParams);
            }
        };
    }
    @Override
    public FilterBindingBuilder<InvokerFilter> filterRegex(String[] regexes) {
        if (StringUtils.isEmptyArray(regexes)) {
            throw new NullPointerException("Filter regexes is empty.");
        }
        return this.filterRegex(null, regexes);
    }
    //
    @Override
    public FilterBindingBuilder<Filter> jeeFilter(final String urlPattern, final String... morePatterns) {
        return new FiltersModuleBinder<Filter>(Filter.class, UriPatternType.SERVLET, newArrayList(morePatterns, urlPattern)) {
            @Override
            protected void bindThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends Filter> filterRegister, Map<String, String> initParams) {
                jeeFilterThrough(index, pattern, matcher, filterRegister, initParams);
            }
        };
    }
    @Override
    public FilterBindingBuilder<Filter> jeeFilter(final String[] morePatterns) throws NullPointerException {
        if (StringUtils.isEmptyArray(morePatterns)) {
            throw new NullPointerException("Filter patterns is empty.");
        }
        return this.jeeFilter(null, morePatterns);
    }
    @Override
    public FilterBindingBuilder<Filter> jeeFilterRegex(final String regex, final String... regexes) {
        return new FiltersModuleBinder<Filter>(Filter.class, UriPatternType.REGEX, newArrayList(regexes, regex)) {
            @Override
            protected void bindThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends Filter> filterRegister, Map<String, String> initParams) {
                jeeFilterThrough(index, pattern, matcher, filterRegister, initParams);
            }
        };
    }
    @Override
    public FilterBindingBuilder<Filter> jeeFilterRegex(final String[] regexes) throws NullPointerException {
        if (StringUtils.isEmptyArray(regexes)) {
            throw new NullPointerException("Filter regexes is empty.");
        }
        return this.jeeFilterRegex(null, regexes);
    }
    protected void jeeFilterThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends Filter> filterRegister, Map<String, String> initParams) {
        FilterDefinition define = new FilterDefinition(index, pattern, matcher, filterRegister, initParams);
        bindType(AbstractDefinition.class).uniqueName().toInstance(define);
    }
    protected void filterThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends InvokerFilter> filterRegister, Map<String, String> initParams) {
        InvokeFilterDefinition define = new InvokeFilterDefinition(index, pattern, matcher, filterRegister, initParams);
        bindType(AbstractDefinition.class).uniqueName().toInstance(define);
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
    protected void jeeServlet(long index, String pattern, BindInfo<? extends HttpServlet> servletRegister, Map<String, String> initParams) {
        InMappingServlet define = new InMappingServlet(index, servletRegister, pattern, initParams);
        bindType(InMappingDef.class).uniqueName().toInstance(define);/*单例*/
    }
    @Override
    public ServletBindingBuilder jeeServlet(final String urlPattern, final String... morePatterns) {
        return new ServletsModuleBuilder(newArrayList(morePatterns, urlPattern));
    }
    @Override
    public ServletBindingBuilder jeeServlet(final String[] morePatterns) {
        if (StringUtils.isEmptyArray(morePatterns)) {
            throw new NullPointerException("Servlet patterns is empty.");
        }
        return this.jeeServlet(null, morePatterns);
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
            BindInfo<HttpServlet> servletRegister = bindType(HttpServlet.class).uniqueName().toProvider(servletProvider).toInfo();
            this.with(index, servletRegister, initParams);
        }
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
        return new MappingToBuilder<Object>(Object.class, newArrayList(morePatterns, urlPattern)) {
            @Override
            public void with(int index, BindInfo<? extends Object> targetInfo) {
                List<Method> methodList = BeanUtils.getMethods(targetInfo.getBindType());
                for (String pattern : this.getUriPatterns()) {
                    if (StringUtils.isBlank(pattern))
                        continue;
                    //
                    InMappingDef define = new InMappingDef(index, targetInfo, pattern, methodList, false);
                    bindType(InMappingDef.class).uniqueName().toInstance(define);
                }
            }
            @Override
            protected Class<Object> findBindType(Class<Object> targetClass, Object target) {
                if (target instanceof ProviderType) {
                    return ((ProviderType) target).getType();
                } else {
                    return (Class<Object>) target.getClass();
                }
            }
            @Override
            protected Class<Object> findBindType(Class<Object> targetClass, Class<?> targetKey) {
                return (Class<Object>) targetKey;
            }
            @Override
            protected Class<Object> findBindType(Class<Object> targetClass, Provider<?> targetProvider) {
                if (targetProvider instanceof ProviderType) {
                    return ((ProviderType) targetProvider).getType();
                } else {
                    return (Class<Object>) targetProvider.get().getClass();
                }
            }
        };
    }
    @Override
    public MappingToBindingBuilder<Object> mappingTo(String[] morePatterns) {
        if (StringUtils.isEmptyArray(morePatterns)) {
            throw new NullPointerException("mappingTo patterns is empty.");
        }
        return this.mappingTo(null, morePatterns);
    }
    //
    @Override
    public void scanMappingTo() {
        this.scanMappingTo(new Matcher<Class<?>>() {
            @Override
            public boolean matches(Class<?> target) {
                return true;
            }
        });
    }
    @Override
    public void scanMappingTo(String... packages) {
        this.scanMappingTo(new Matcher<Class<?>>() {
            @Override
            public boolean matches(Class<?> target) {
                return true;
            }
        });
    }
    @Override
    public void scanMappingTo(Matcher<Class<?>> matcher, String... packages) {
        String[] defaultPackages = this.getEnvironment().getSpanPackage();
        String[] scanPackages = (packages == null || packages.length == 0) ? defaultPackages : packages;
        //
        Set<Class<?>> serviceSet = this.findClass(MappingTo.class, scanPackages);
        serviceSet = (serviceSet == null) ? new HashSet<Class<?>>() : new HashSet<Class<?>>(serviceSet);
        serviceSet.remove(MappingTo.class);
        if (serviceSet.isEmpty()) {
            logger.warn("mapingTo -> exit , not found any @MappingTo.");
        }
        for (Class<?> type : serviceSet) {
            loadType(this, type);
        }
    }
    //
    // ------------------------------------------------------------------------------------------------------
    /**拦截这些后缀的请求，这些请求会被渲染器渲染。*/
    public RenderEngineBindingBuilder<RenderEngine> suffix(String urlPattern, String... morePatterns) {
        return this.renderBinder.suffix(urlPattern, morePatterns);
    }
    /**拦截这些后缀的请求，这些请求会被渲染器渲染。*/
    public RenderEngineBindingBuilder<RenderEngine> suffix(String[] morePatterns) {
        return this.renderBinder.suffix(morePatterns);
    }
    /**扫描Render注解配置的渲染器。*/
    public void scanAnnoRender() {
        this.scanAnnoRender(new Matcher<Class<? extends RenderEngine>>() {
            @Override
            public boolean matches(Class<? extends RenderEngine> target) {
                return true;
            }
        });
    }
    /**扫描Render注解配置的渲染器。*/
    public void scanAnnoRender(String... packages) {
        this.scanAnnoRender(new Matcher<Class<? extends RenderEngine>>() {
            @Override
            public boolean matches(Class<? extends RenderEngine> target) {
                return true;
            }
        });
    }
    /**扫描Render注解配置的渲染器。*/
    public void scanAnnoRender(Matcher<Class<? extends RenderEngine>> matcher, String... packages) {
        this.renderBinder.scanAnnoRender(matcher, packages);
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
    public boolean loadType(WebApiBinder apiBinder, Class<?> clazz) {
        int modifier = clazz.getModifiers();
        if (checkIn(modifier, Modifier.INTERFACE) || checkIn(modifier, Modifier.ABSTRACT)) {
            return false;
        }
        if (!clazz.isAnnotationPresent(MappingTo.class)) {
            return false;
        }
        //
        MappingTo mto = clazz.getAnnotation(MappingTo.class);
        if (HttpServlet.class.isAssignableFrom(clazz)) {
            apiBinder.jeeServlet(mto.value()).with((Class<? extends HttpServlet>) clazz);
            logger.info("mapingTo[Servlet] -> type ‘{}’ mappingTo: ‘{}’.", clazz.getName(), mto.value());
        } else {
            apiBinder.mappingTo(mto.value()).with(clazz);
            logger.info("mapingTo[Object] -> type ‘{}’ mappingTo: ‘{}’.", clazz.getName(), mto.value());
        }
        return true;
    }
    //
    /** 通过位运算决定check是否在data里。 */
    private boolean checkIn(final int data, final int check) {
        int or = data | check;
        return or == data;
    }
}