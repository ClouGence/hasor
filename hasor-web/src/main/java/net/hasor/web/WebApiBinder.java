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
import net.hasor.core.aop.AsmTools;
import net.hasor.core.exts.aop.Matchers;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.utils.ArrayUtils;
import net.hasor.utils.ResourcesUtils;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.render.Render;
import net.hasor.web.render.RenderEngine;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
    public default WebApiBinder setEncodingCharacter(String requestEncoding, String responseEncoding) {
        return this.setRequestCharacter(requestEncoding).setResponseCharacter(responseEncoding);
    }

    /**获取容器支持的Servlet版本。*/
    public ServletVersion getServletVersion();

    /**使用 MappingTo 表达式，创建一个{@link ServletBindingBuilder}。*/
    public default ServletBindingBuilder jeeServlet(String urlPattern, String... morePatterns) {
        return this.jeeServlet(ArrayUtils.add(morePatterns, urlPattern));
    }

    /**使用 MappingTo 表达式，创建一个{@link ServletBindingBuilder}。*/
    public ServletBindingBuilder jeeServlet(String[] morePatterns);

    /**使用 MappingTo 表达式，创建一个{@link MappingToBindingBuilder}。*/
    public default <T> MappingToBindingBuilder<T> mappingTo(String urlPattern, String... morePatterns) {
        return this.mappingTo(ArrayUtils.add(morePatterns, urlPattern));
    }

    /**使用 MappingTo 表达式，创建一个{@link MappingToBindingBuilder}。*/
    public <T> MappingToBindingBuilder<T> mappingTo(String[] morePatterns);

    /** 加载带有 @MappingTo 注解的类。 */
    public default WebApiBinder loadMappingTo(Set<Class<?>> udfTypeSet) {
        return this.loadMappingTo(udfTypeSet, Matchers.annotatedWithClass(MappingTo.class));
    }

    /** 加载带有 @MappingTo 注解的类。 */
    public default WebApiBinder loadMappingTo(Set<Class<?>> mabeUdfTypeSet, Predicate<Class<?>> matcher) {
        if (mabeUdfTypeSet != null && !mabeUdfTypeSet.isEmpty()) {
            mabeUdfTypeSet.stream().filter(matcher).forEach(this::loadMappingTo);
        }
        return this;
    }

    /** 加载带有 @MappingTo 注解的类。 */
    public default WebApiBinder loadMappingTo(Class<?> mappingType) {
        Objects.requireNonNull(mappingType, "class is null.");
        int modifier = mappingType.getModifiers();
        if (AsmTools.checkOr(modifier, Modifier.INTERFACE, Modifier.ABSTRACT) || mappingType.isArray() || mappingType.isEnum()) {
            throw new IllegalStateException(mappingType.getName() + " must be normal Bean");
        }
        MappingTo[] annotationsByType = mappingType.getAnnotationsByType(MappingTo.class);
        if (annotationsByType == null || annotationsByType.length == 0) {
            throw new IllegalStateException(mappingType.getName() + " must be configure @MappingTo");
        }
        //
        if (HttpServlet.class.isAssignableFrom(mappingType)) {
            Arrays.stream(annotationsByType).peek(mappingTo -> {
            }).forEach(mappingTo -> {
                if (!isSingleton(mappingType)) {
                    throw new IllegalStateException("HttpServlet " + mappingType + " must be Singleton.");
                }
                jeeServlet(mappingTo.value()).with((Class<? extends HttpServlet>) mappingType);
            });
        } else {
            Arrays.stream(annotationsByType).peek(mappingTo -> {
            }).forEach(mappingTo -> {
                mappingTo(mappingTo.value()).with(mappingType);
            });
        }
        return this;
    }

    /**使用传统表达式，创建一个{@link FilterBindingBuilder}。*/
    public default FilterBindingBuilder<InvokerFilter> filter(String urlPattern, String... morePatterns) {
        return this.filter(ArrayUtils.add(morePatterns, urlPattern));
    }

    /**使用传统表达式，创建一个{@link FilterBindingBuilder}。*/
    public FilterBindingBuilder<InvokerFilter> filter(String[] morePatterns);

    /**使用正则表达式，创建一个{@link FilterBindingBuilder}。*/
    public default FilterBindingBuilder<InvokerFilter> filterRegex(String regex, String... regexes) {
        return this.filter(ArrayUtils.add(regexes, regex));
    }

    /**使用正则表达式，创建一个{@link FilterBindingBuilder}。*/
    public FilterBindingBuilder<InvokerFilter> filterRegex(String[] regexes);

    /**使用传统表达式，创建一个{@link FilterBindingBuilder}。*/
    public default FilterBindingBuilder<Filter> jeeFilter(String urlPattern, String... morePatterns) {
        return this.jeeFilter(ArrayUtils.add(morePatterns, urlPattern));
    }

    /**使用传统表达式，创建一个{@link FilterBindingBuilder}。*/
    public FilterBindingBuilder<Filter> jeeFilter(String[] morePatterns);

    /**使用正则表达式，创建一个{@link FilterBindingBuilder}。*/
    public default FilterBindingBuilder<Filter> jeeFilterRegex(String regex, String... regexes) {
        return this.jeeFilterRegex(ArrayUtils.add(regexes, regex));
    }

    /**使用正则表达式，创建一个{@link FilterBindingBuilder}。*/
    public FilterBindingBuilder<Filter> jeeFilterRegex(String[] regexes);

    /**
     * 注册一个 Web Listener
     * @see javax.servlet.ServletContextListener
     * @see javax.servlet.http.HttpSessionListener
     * @see javax.servlet.ServletRequestListener
     * @see #bindSpiListener(Class, EventListener)
     */
    public default <T extends EventListener> void bindSpiListener(Class<T> spiType, T listener) {
        this.bindSpiListener(spiType, (Supplier<T>) () -> listener);
    }

    /**
     * 注册一个 Web Listener
     * @see javax.servlet.ServletContextListener
     * @see javax.servlet.http.HttpSessionListener
     * @see javax.servlet.ServletRequestListener
     * @see #bindSpiListener(Class, Supplier)
     */
    public <T extends EventListener> void bindSpiListener(Class<T> spiType, Supplier<T> listener);

    public void addMimeType(String type, String mimeType);

    public default void loadMimeType(String resource) throws IOException {
        loadMimeType(Charset.forName("UTF-8"), resource);
    }

    public default void loadMimeType(InputStream inputStream) throws IOException {
        loadMimeType(Charset.forName("UTF-8"), inputStream);
    }

    public default void loadMimeType(Charset charset, String resource) throws IOException {
        loadMimeType(charset, Objects.requireNonNull(ResourcesUtils.getResourceAsStream(resource), resource + " is not exist"));
    }

    public default void loadMimeType(Charset charset, InputStream inputStream) throws IOException {
        loadMimeType(new InputStreamReader(inputStream, charset));
    }

    public void loadMimeType(Reader reader) throws IOException;

    /**负责配置Filter。*/
    public static interface FilterBindingBuilder<T> {
        public default void through(Class<? extends T> filterKey) {
            this.through(0, filterKey, null);
        }

        public default void through(T filter) {
            this.through(0, filter, null);
        }

        public default void through(Supplier<? extends T> filterProvider) {
            this.through(0, filterProvider, null);
        }

        public default void through(BindInfo<? extends T> filterRegister) {
            this.through(0, filterRegister, null);
        }

        //
        public default void through(Class<? extends T> filterKey, Map<String, String> initParams) {
            this.through(0, filterKey, initParams);
        }

        public default void through(T filter, Map<String, String> initParams) {
            this.through(0, filter, initParams);
        }

        public default void through(Supplier<? extends T> filterProvider, Map<String, String> initParams) {
            this.through(0, filterProvider, initParams);
        }

        public default void through(BindInfo<? extends T> filterRegister, Map<String, String> initParams) {
            this.through(0, filterRegister, initParams);
        }

        //
        public default void through(int index, Class<? extends T> filterKey) {
            this.through(index, filterKey, null);
        }

        public default void through(int index, T filter) {
            this.through(index, filter, null);
        }

        public default void through(int index, Supplier<? extends T> filterProvider) {
            this.through(index, filterProvider, null);
        }

        public default void through(int index, BindInfo<? extends T> filterRegister) {
            this.through(index, filterRegister, null);
        }

        //
        public void through(int index, Class<? extends T> filterKey, Map<String, String> initParams);

        public void through(int index, T filter, Map<String, String> initParams);

        public void through(int index, Supplier<? extends T> filterProvider, Map<String, String> initParams);

        public void through(int index, BindInfo<? extends T> filterRegister, Map<String, String> initParams);
    }

    /**负责配置Servlet。*/
    public static interface ServletBindingBuilder {
        public default void with(Class<? extends HttpServlet> targetKey) {
            with(0, targetKey, null);
        }

        public default void with(HttpServlet target) {
            with(0, target, null);
        }

        public default void with(Supplier<? extends HttpServlet> targetProvider) {
            with(0, targetProvider, null);
        }

        public default void with(BindInfo<? extends HttpServlet> targetInfo) {
            with(0, targetInfo, null);
        }
        //

        public default void with(Class<? extends HttpServlet> servletKey, Map<String, String> initParams) {
            this.with(0, servletKey, initParams);
        }

        public default void with(HttpServlet servlet, Map<String, String> initParams) {
            this.with(0, servlet, initParams);
        }

        public default void with(Supplier<? extends HttpServlet> servletProvider, Map<String, String> initParams) {
            this.with(0, servletProvider, initParams);
        }

        public default void with(BindInfo<? extends HttpServlet> servletRegister, Map<String, String> initParams) {
            this.with(0, servletRegister, initParams);
        }

        //
        public default void with(int index, Class<? extends HttpServlet> targetKey) {
            this.with(index, targetKey, null);
        }

        public default void with(int index, HttpServlet target) {
            this.with(index, target, null);
        }

        public default void with(int index, Supplier<? extends HttpServlet> targetProvider) {
            this.with(index, targetProvider, null);
        }

        public default void with(int index, BindInfo<? extends HttpServlet> targetInfo) {
            this.with(index, targetInfo, null);
        }

        //
        public void with(int index, Class<? extends HttpServlet> servletKey, Map<String, String> initParams);

        public void with(int index, HttpServlet servlet, Map<String, String> initParams);

        public void with(int index, Supplier<? extends HttpServlet> servletProvider, Map<String, String> initParams);

        public void with(int index, BindInfo<? extends HttpServlet> servletRegister, Map<String, String> initParams);
    }

    /**负责配置MappingTo。*/
    public static interface MappingToBindingBuilder<T> {
        public default void with(Class<? extends T> targetKey) {
            with(0, targetKey);
        }

        public default void with(T target) {
            with(0, target);
        }

        public default void with(Class<T> referKey, Supplier<? extends T> targetProvider) {
            with(0, referKey, targetProvider);
        }

        public default void with(BindInfo<? extends T> targetInfo) {
            with(0, targetInfo);
        }

        //
        public void with(int index, Class<? extends T> targetKey);

        public void with(int index, T target);

        public void with(int index, Class<T> referKey, Supplier<? extends T> targetProvider);

        public void with(int index, BindInfo<? extends T> targetInfo);
    }
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** 加载带有 @Render注解配置的渲染器。 */
    public default WebApiBinder loadRender(Set<Class<?>> udfTypeSet) {
        return this.loadRender(udfTypeSet, Matchers.annotatedWithClass(Render.class));
    }

    /** 加载带有 @Render注解配置的渲染器。 */
    public default WebApiBinder loadRender(Set<Class<?>> mabeUdfTypeSet, Predicate<Class<?>> matcher) {
        if (mabeUdfTypeSet != null && !mabeUdfTypeSet.isEmpty()) {
            mabeUdfTypeSet.stream().filter(matcher).forEach(this::loadRender);
        }
        return this;
    }

    /** 加载 @Render注解配置的渲染器。*/
    public default WebApiBinder loadRender(Class<?> renderClass) {
        Objects.requireNonNull(renderClass, "class is null.");
        int modifier = renderClass.getModifiers();
        if (AsmTools.checkOr(modifier, Modifier.INTERFACE, Modifier.ABSTRACT) || renderClass.isArray() || renderClass.isEnum()) {
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
            for (String renderName : renderInfo.value()) {
                addRender(renderName).to((Class<? extends RenderEngine>) renderClass);
            }
        }
        return this;
    }

    /**
     * 添加一个渲染器，用来将 Action 请求的结果渲染成页面。
     * @param renderName 渲染器名称
     */
    public RenderEngineBindingBuilder addRender(String renderName);

    /** 负责配置RenderEngine。*/
    public static interface RenderEngineBindingBuilder {
        /**绑定实现。*/
        public <T extends RenderEngine> void to(Class<T> renderEngineType);

        /**绑定实现。*/
        public default void toInstance(RenderEngine renderEngine) {
            this.toProvider(new InstanceProvider<>(renderEngine));
        }

        /**绑定实现。*/
        public void toProvider(Supplier<? extends RenderEngine> renderEngineProvider);

        /**绑定实现。*/
        public void bindToInfo(BindInfo<? extends RenderEngine> renderEngineInfo);
    }
    //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //    /** 创建一个映射，当匹配某个URL的时候使用指定的 Render 来渲染。如果存在多条规则按照 index 顺序裁决 index 最大的那一个*/
    //    public default void urlExtensionToRender(String extension, String renderName) {
    //        this.urlExtensionToRender(0, extension, renderName);
    //    }
    //
    //    public void urlExtensionToRender(int index, String extension, String renderName);
}