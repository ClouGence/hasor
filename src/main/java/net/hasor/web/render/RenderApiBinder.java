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
package net.hasor.web.render;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.Matcher;
import net.hasor.core.Provider;
/**
 * 渲染引擎
 * @version : 2016年1月3日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RenderApiBinder extends ApiBinder {
    /**拦截这些后缀的请求，这些请求会被渲染器渲染。*/
    public RenderEngineBindingBuilder<RenderEngine> suffix(String urlPattern, String... morePatterns);

    /**拦截这些后缀的请求，这些请求会被渲染器渲染。*/
    public RenderEngineBindingBuilder<RenderEngine> suffix(String[] morePatterns);

    /**扫描Render注解配置的渲染器。*/
    public void scanAnnoRender();

    /**扫描Render注解配置的渲染器。*/
    public void scanAnnoRender(String... packages);

    /**扫描Render注解配置的渲染器。*/
    public void scanAnnoRender(Matcher<Class<? extends RenderEngine>> matcher, String... packages);
    //
    /**负责配置RenderEngine。*/
    public static interface RenderEngineBindingBuilder<T> {
        public void bind(Class<? extends T> filterKey);

        public void bind(T filter);

        public void bind(Provider<? extends T> filterProvider);

        public void bind(BindInfo<? extends T> filterRegister);
    }
}