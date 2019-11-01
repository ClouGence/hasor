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
import net.hasor.core.AppContext;
import net.hasor.test.web.actions.render.HtmlProduces;
import net.hasor.web.WebApiBinder;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class NoRenderProducesTest extends RenderLayoutTest {
    //  [get]abc.do 因为扩展名不是 .html 因此无法匹配到 html 渲染器。但是标记了 @Produces 注解
    @Test
    public void get_noRender_hasProduces_noMime() throws Throwable {
        // 默认打开 layout
        AppContext appContext = renderAppContext(true, null, apiBinder -> {
            // HtmlProduces 的 get 方法会设置使用 aabbcc 类型响应
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/abc.do").with(HtmlProduces.class);
        });
        //
        Set<String> responseType = new HashSet<>();
        Set<String> dispatcher = new HashSet<>();
        String stringWriter = mockAndCallHttp("get", "http://www.hasor.net/abc.do", appContext, responseType, dispatcher);
        //
        assert stringWriter.equals("");
        assert dispatcher.contains("/my/my.html");      // 没有命中任何模板配置，因此走了 getRequestDispatcher
        assert responseType.contains("text/javacc_jj"); // @Produces 注解配置
    }

    //  [put]abc.do 因为扩展名不是 .html 因此无法匹配到 html 渲染器。同时没有标记 @Produces 注解
    @Test
    public void put_noRender_noProduces_noMime() throws Throwable {
        // 默认打开 layout
        AppContext appContext = renderAppContext(true, null, apiBinder -> {
            // HtmlProduces 的 get 方法会设置使用 aabbcc 类型响应
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/abc.do").with(HtmlProduces.class);
        });
        //
        Set<String> responseType = new HashSet<>();
        Set<String> dispatcher = new HashSet<>();
        String stringWriter = mockAndCallHttp("put", "http://www.hasor.net/abc.do", appContext, responseType, dispatcher);
        //
        assert stringWriter.equals("");
        assert dispatcher.contains("/my/my.html");  // 没有命中任何模板配置，因此走了 getRequestDispatcher
        assert responseType.size() == 0;            // @Produces 注解配置
    }

    //  [put]abc.do 因为扩展名不是 .html 因此无法匹配到 html 渲染器。同时没有标记 @Produces 注解，但是匹配到了 mime
    @Test
    public void put_noRender_noProduces_hasMime() throws Throwable {
        // 默认打开 layout
        AppContext appContext = renderAppContext(true, null, apiBinder -> {
            // HtmlProduces 的 get 方法会设置使用 aabbcc 类型响应
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/abc.do").with(HtmlProduces.class);
            apiBinder.tryCast(WebApiBinder.class).addMimeType("do", "app/do");
        });
        //
        Set<String> responseType = new HashSet<>();
        Set<String> dispatcher = new HashSet<>();
        String stringWriter = mockAndCallHttp("put", "http://www.hasor.net/abc.do", appContext, responseType, dispatcher);
        //
        assert stringWriter.equals("");
        assert dispatcher.contains("/my/my.html");  // 没有命中任何模板配置，因此走了 getRequestDispatcher
        assert responseType.contains("app/do");     // @Produces 注解配置
    }

    //  [put]abc.do 因为扩展名不是 .html 因此无法匹配到 html 渲染器。同时没有标记 @Produces 注解，但是匹配到了 mime
    @Test
    public void get_noRender_hasProduces_hasMime() throws Throwable {
        // 默认打开 layout
        AppContext appContext = renderAppContext(true, null, apiBinder -> {
            // HtmlProduces 的 get 方法会设置使用 aabbcc 类型响应
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/abc.do").with(HtmlProduces.class);
            apiBinder.tryCast(WebApiBinder.class).addMimeType("do", "app/do");
        });
        //
        Set<String> responseType = new HashSet<>();
        Set<String> dispatcher = new HashSet<>();
        String stringWriter = mockAndCallHttp("get", "http://www.hasor.net/abc.do", appContext, responseType, dispatcher);
        //
        assert stringWriter.equals("");
        assert dispatcher.contains("/my/my.html");      // 没有命中任何模板配置，因此走了 getRequestDispatcher
        assert responseType.contains("text/javacc_jj"); // @Produces 注解配置
    }
}