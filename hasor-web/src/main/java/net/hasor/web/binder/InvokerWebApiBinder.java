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
package net.hasor.web.binder;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.core.exts.aop.Matchers;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.utils.CheckUtils;
import net.hasor.utils.StringUtils;
import net.hasor.web.InvokerFilter;
import net.hasor.web.render.RenderEngine;
import net.hasor.web.ServletVersion;
import net.hasor.web.WebApiBinder;
import net.hasor.web.mime.MimeTypeSupplier;
import net.hasor.web.startup.RuntimeFilter;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.Supplier;

/**
 * 该类是{@link WebApiBinder}接口实现。
 * @version : 2017-01-10
 * @author 赵永春 (zyc@hasor.net)
 */
public class InvokerWebApiBinder extends ApiBinderWrap implements WebApiBinder {
    private final InstanceProvider<String> requestEncoding  = new InstanceProvider<>("");
    private final InstanceProvider<String> responseEncoding = new InstanceProvider<>("");
    private       ServletVersion           curVersion;
    private       MimeTypeSupplier         mimeType;
    // ------------------------------------------------------------------------------------------------------

    protected InvokerWebApiBinder(ServletVersion curVersion, MimeTypeSupplier mimeType, ApiBinder apiBinder) {
        super(apiBinder);
        apiBinder.bindType(String.class).nameWith(RuntimeFilter.HTTP_REQUEST_ENCODING_KEY).toProvider(this.requestEncoding);
        apiBinder.bindType(String.class).nameWith(RuntimeFilter.HTTP_RESPONSE_ENCODING_KEY).toProvider(this.responseEncoding);
        this.curVersion = Objects.requireNonNull(curVersion);
        this.mimeType = Objects.requireNonNull(mimeType);
    }
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
    public String getMimeType(String suffix) {
        return this.mimeType.getMimeType(suffix);
    }

    @Override
    public void addMimeType(String type, String mimeType) {
        this.mimeType.addMimeType(type, mimeType);
    }

    @Override
    public void loadMimeType(Reader reader) throws IOException {
        this.mimeType.loadReader(reader);
    }

    @Override
    public ServletVersion getServletVersion() {
        return this.curVersion;
    }

    // ------------------------------------------------------------------------------------------------------
    @Override
    public FilterBindingBuilder<InvokerFilter> filter(String[] morePatterns) {
        List<String> uriPatterns = CheckUtils.checkEmpty(Arrays.asList(morePatterns), "Filter patterns is empty.");
        return new FiltersModuleBinder<InvokerFilter>(InvokerFilter.class, UriPatternType.SERVLET, uriPatterns) {
            @Override
            protected void bindThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends InvokerFilter> filterRegister, Map<String, String> initParams) {
                filterThrough(index, pattern, matcher, filterRegister, initParams);
            }
        };
    }

    @Override
    public FilterBindingBuilder<InvokerFilter> filterRegex(String[] regexes) {
        List<String> uriPatterns = CheckUtils.checkEmpty(Arrays.asList(regexes), "Filter patterns is empty.");
        return new FiltersModuleBinder<InvokerFilter>(InvokerFilter.class, UriPatternType.REGEX, uriPatterns) {
            @Override
            protected void bindThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends InvokerFilter> filterRegister, Map<String, String> initParams) {
                filterThrough(index, pattern, matcher, filterRegister, initParams);
            }
        };
    }

    @Override
    public FilterBindingBuilder<Filter> jeeFilter(final String[] morePatterns) throws NullPointerException {
        List<String> uriPatterns = CheckUtils.checkEmpty(Arrays.asList(morePatterns), "Filter patterns is empty.");
        return new FiltersModuleBinder<Filter>(Filter.class, UriPatternType.SERVLET, uriPatterns) {
            @Override
            protected void bindThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends Filter> filterRegister, Map<String, String> initParams) {
                jeeFilterThrough(index, pattern, matcher, filterRegister, initParams);
            }
        };
    }

    @Override
    public FilterBindingBuilder<Filter> jeeFilterRegex(final String[] regexes) throws NullPointerException {
        List<String> uriPatterns = CheckUtils.checkEmpty(Arrays.asList(regexes), "Filter patterns is empty.");
        return new FiltersModuleBinder<Filter>(Filter.class, UriPatternType.REGEX, uriPatterns) {
            @Override
            protected void bindThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends Filter> filterRegister, Map<String, String> initParams) {
                jeeFilterThrough(index, pattern, matcher, filterRegister, initParams);
            }
        };
    }

    /** Filter 转换为 InvokerFilter */
    protected void jeeFilterThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends Filter> filterRegister, Map<String, String> initParams) {
        if (!this.isSingleton(filterRegister)) {
            throw new IllegalStateException("Filter must be Singleton.");
        }
        J2eeFilterAsFilter filterAsFilter = new J2eeFilterAsFilter(this.getProvider(filterRegister));
        BindInfo<J2eeFilterAsFilter> bindInfo = bindType(J2eeFilterAsFilter.class).uniqueName().toInstance(filterAsFilter).toInfo();
        this.filterThrough(index, pattern, matcher, bindInfo, initParams);
    }

    protected void filterThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends InvokerFilter> bindInfo, Map<String, String> initParams) {
        if (!this.isSingleton(bindInfo)) {
            throw new IllegalStateException("InvokerFilter must be Singleton.");
        }
        Supplier<AppContext> appContext = getProvider(AppContext.class);
        FilterDef define = new FilterDef(index, matcher, initParams, bindInfo, appContext);
        bindType(FilterDef.class).uniqueName().toInstance(define);
    }

    // ------------------------------------------------------------------------------------------------------
    private abstract class FiltersModuleBinder<T> implements FilterBindingBuilder<T> {
        private final Class<T>       targetType;
        private final UriPatternType uriPatternType;
        private final List<String>   uriPatterns;

        FiltersModuleBinder(Class<T> targetType, final UriPatternType uriPatternType, final List<String> uriPatterns) {
            this.targetType = targetType;
            this.uriPatternType = uriPatternType;
            this.uriPatterns = uriPatterns;
        }

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
        public void through(final int index, final Supplier<? extends T> filterProvider, Map<String, String> initParams) {
            BindInfo<T> filterRegister = bindType(targetType).uniqueName().toProvider(filterProvider).asEagerSingleton().toInfo();
            this.through(index, filterRegister, initParams);
        }

        @Override
        public void through(int index, BindInfo<? extends T> filterRegister, Map<String, String> initParams) {
            if (initParams == null) {
                initParams = new HashMap<>();
            }
            for (String pattern : this.uriPatterns) {
                UriPatternMatcher matcher = UriPatternType.get(this.uriPatternType, pattern);
                this.bindThrough(index, pattern, matcher, filterRegister, initParams);
            }
        }

        protected abstract void bindThrough(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends T> filterRegister, Map<String, String> initParams);
    }
    // ------------------------------------------------------------------------------------------------------

    /** HttpServlet 转换为 MappingTo 形式 */
    protected void jeeServlet(int index, String pattern, BindInfo<? extends HttpServlet> servletRegister, Map<String, String> initParams) {
        if (!this.isSingleton(servletRegister)) {
            throw new IllegalStateException("HttpServlet must be Singleton.");
        }
        OneConfig oneConfig = new OneConfig(servletRegister.getBindID(), initParams, getProvider(AppContext.class));
        Supplier<? extends Servlet> j2eeServlet = getProvider(servletRegister);
        mappingTo(pattern).with(index, new J2eeServletAsMapping(oneConfig, j2eeServlet));
    }

    @Override
    public ServletBindingBuilder jeeServlet(final String[] morePatterns) {
        return new ServletsModuleBuilder(CheckUtils.checkEmpty(Arrays.asList(morePatterns), "Servlet patterns is empty."));
    }

    private class ServletsModuleBuilder implements ServletBindingBuilder {
        private List<String> uriPatterns;

        ServletsModuleBuilder(List<String> uriPatterns) {
            this.uriPatterns = uriPatterns;
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
        public void with(final int index, final Supplier<? extends HttpServlet> servletProvider, Map<String, String> initParams) {
            BindInfo<HttpServlet> servletRegister = bindType(HttpServlet.class).uniqueName().toProvider(servletProvider).asEagerSingleton().toInfo();
            this.with(index, servletRegister, initParams);
        }

        @Override
        public void with(int index, BindInfo<? extends HttpServlet> servletRegister, Map<String, String> initParams) {
            if (initParams == null) {
                initParams = new HashMap<>();
            }
            for (String pattern : this.uriPatterns) {
                jeeServlet(index, pattern, servletRegister, initParams);
            }
            logger.info("mapingTo[Servlet] -> bindID ‘{}’ mappingTo: ‘{}’.", servletRegister.getBindID(), this.uriPatterns);
        }
    }
    // ------------------------------------------------------------------------------------------------------

    @Override
    public <T> MappingToBindingBuilder<T> mappingTo(String[] morePatterns) {
        CheckUtils.checkEmpty(Arrays.asList(morePatterns), "mappingTo patterns is empty.");
        return new MappingToBindingBuilder<T>() {
            @Override
            public void with(int index, Class<? extends T> targetKey) {
                this.with(index, bindType(targetKey).uniqueName().toInfo());
            }

            @Override
            public void with(int index, T target) {
                Class<T> targetType = (Class<T>) target.getClass();
                this.with(index, bindType(targetType).uniqueName().toInstance(target).toInfo());
            }

            @Override
            public void with(int index, Class<T> referKey, Supplier<? extends T> targetProvider) {
                this.with(index, bindType(referKey).uniqueName().toProvider(targetProvider).toInfo());
            }

            @Override
            public void with(int index, BindInfo<? extends T> targetInfo) {
                Arrays.stream(morePatterns).filter(StringUtils::isNotBlank).forEach(pattern -> {
                    MappingDef define = new MappingDef(index, targetInfo, pattern, Matchers.anyMethod(), true);
                    bindType(MappingDef.class).uniqueName().toInstance(define);
                });
                logger.info("mapingTo[{}] -> bindType ‘{}’ mappingTo: ‘{}’.", targetInfo.getBindID(), targetInfo.getBindType(), morePatterns);
            }
        };
    }
    // ------------------------------------------------------------------------------------------------------

    /** 拦截这些后缀的请求，这些请求会被渲染器渲染。*/
    public WebApiBinder.RenderEngineBindingBuilder addRender(String renderName) {
        return new RenderEngineBindingBuilderImpl(Objects.requireNonNull(renderName, "Render renderName is empty.")) {
            @Override
            protected void bindRender(String renderName, BindInfo<? extends RenderEngine> bindInfo) {
                bindType(RenderDef.class).nameWith(renderName).toInstance(new RenderDef(renderName, bindInfo));
            }
        };
    }
    // ------------------------------------------------------------------------------------------------------

    private abstract class RenderEngineBindingBuilderImpl implements WebApiBinder.RenderEngineBindingBuilder {
        private String renderName;

        public RenderEngineBindingBuilderImpl(String renderName) {
            this.renderName = renderName;
        }

        @Override
        public <T extends RenderEngine> void to(Class<T> renderEngineType) {
            bindRender(this.renderName, bindType(RenderEngine.class).uniqueName().to(renderEngineType).toInfo());
        }

        @Override
        public void toProvider(Supplier<? extends RenderEngine> renderEngineProvider) {
            bindRender(this.renderName, bindType(RenderEngine.class).uniqueName().toProvider(renderEngineProvider).toInfo());
        }

        @Override
        public void bindToInfo(BindInfo<? extends RenderEngine> renderEngineInfo) {
            bindRender(this.renderName, Objects.requireNonNull(renderEngineInfo));
        }

        protected abstract void bindRender(String renderName, BindInfo<? extends RenderEngine> bindInfo);
    }
}