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
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.test.web.actions.render.DefaultLayoutHtmlAction;
import net.hasor.test.web.actions.render.DisableLayoutHtmlAction;
import net.hasor.test.web.actions.render.EnableLayoutHtmlAction;
import net.hasor.test.web.actions.render.ParentLayoutHtmlAction;
import net.hasor.test.web.render.SimpleRenderEngine;
import net.hasor.test.web.render.TestRenderEngine;
import net.hasor.web.AbstractTest;
import net.hasor.web.WebApiBinder;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RenderLayoutTest extends AbstractTest {
    protected AppContext renderAppContext(boolean enableLayout, RenderEngine renderEngine, Module... module) {
        return buildWebAppContext("/META-INF/hasor-framework/web-hconfig.xml", context -> {
            Hasor hasor = Hasor.create(context);
            hasor.addVariable("HASOR_RESTFUL_LAYOUT", String.valueOf(enableLayout));
            hasor.addVariable("HASOR_RESTFUL_LAYOUT_PATH", "/layout/mytest");
            hasor.addVariable("HASOR_RESTFUL_LAYOUT_TEMPLATES", "/templates/myfiles");
            return hasor;
        }, apiBinder -> {
            if (renderEngine != null) {
                apiBinder.addRender("html").toInstance(renderEngine);
            }
            apiBinder.installModule(module);
        }, servlet30("/"), LoadModule.Web, LoadModule.Render);
    }

    protected List<String> layoutFiles() {
        return new ArrayList<String>() {{
            add("/layout/mytest/default.html");
            add("/layout/mytest/my/default.html");
            //
            add("/templates/myfiles/login.html");
            add("/templates/myfiles/my/abc/my.html");
            add("/templates/myfiles/my/my.html");
            add("/templates/myfiles/my/my.json");
        }};
    }

    protected List<String> noneLayoutFiles() {
        return new ArrayList<String>() {{
            add("/login.html");
            add("/my/abc/my.html");
            add("/my/my.html");
            add("/my/my.json");
        }};
    }

    @Test
    public void layoutTest_basicinfo() throws Throwable {
        SimpleRenderEngine renderEngine = new SimpleRenderEngine();
        AppContext appContext = renderAppContext(true, renderEngine, apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/abc.do").with(DefaultLayoutHtmlAction.class);
        });
        //
        Field layoutPathField = RenderInvokerFilter.class.getDeclaredField("layoutPath");
        Field useLayoutField = RenderInvokerFilter.class.getDeclaredField("useLayout");
        Field templatePathField = RenderInvokerFilter.class.getDeclaredField("templatePath");
        Field engineMapField = RenderInvokerFilter.class.getDeclaredField("engineMap");
        layoutPathField.setAccessible(true);
        useLayoutField.setAccessible(true);
        templatePathField.setAccessible(true);
        engineMapField.setAccessible(true);
        //
        RenderInvokerFilter renderPlugin = appContext.getInstance(RenderInvokerFilter.class);
        String layoutPath = (String) layoutPathField.get(renderPlugin);
        boolean useLayout = (boolean) useLayoutField.get(renderPlugin);
        String templatePath = (String) templatePathField.get(renderPlugin);
        Map<String, RenderEngine> engineMap = (Map<String, RenderEngine>) engineMapField.get(renderPlugin);
        //
        assert "/layout/mytest".equals(layoutPath);
        assert useLayout;
        assert "/templates/myfiles".equals(templatePath);
        assert engineMap.size() == 1;
        assert engineMap.get("HTML") == renderEngine;
    }

    // 默认打开 layout
    @Test
    public void layoutTest_2_default() throws Throwable {
        // 加载 layout 的站点资源路径
        TestRenderEngine renderEngine = new TestRenderEngine(layoutFiles());
        // 默认打开 layout
        AppContext appContext = renderAppContext(true, renderEngine, apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/abc.do").with(DefaultLayoutHtmlAction.class);
        });
        String stringWriter = mockAndCallHttp("post", "http://www.hasor.net/abc.do", appContext);
        //
        {
            JSONObject jsonObject = JSON.parseObject(stringWriter);
            // 因为启用了 layout 而且会命中 layout，因此 placeholder 肯定有内容。
            assert jsonObject.get("content_placeholder") != null;
            // 最后一个渲染的是布局模板，因此展示印布局模板真实地址
            assert jsonObject.get("engine_renderTo").equals("/layout/mytest/my/default.html");
            // 要展示的页面真实位置
            assert jsonObject.getJSONObject("content_placeholder").getString("engine_renderTo").equals("/templates/myfiles/my/my.html");
            // 要展示的页面
            assert jsonObject.getJSONObject("resultData") != null;
            assert jsonObject.getJSONObject("resultData").getString("renderTo").equals("/my/my.html");
        }
    }

    // 默认打开 layout，但是单个 action 关闭了 layout
    @Test
    public void layoutTest_2_disable() throws Throwable {
        // 加载没有 layout 的站点资源路径
        TestRenderEngine renderEngine = new TestRenderEngine(noneLayoutFiles());
        // 默认打开 layout
        AppContext appContext = renderAppContext(true, renderEngine, apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/abc.do").with(DisableLayoutHtmlAction.class);
        });
        String stringWriter = mockAndCallHttp("post", "http://www.hasor.net/abc.do", appContext);
        //
        {
            JSONObject jsonObject = JSON.parseObject(stringWriter);
            // 因为没有启用 layout 因此 placeholder 不会出现。
            assert jsonObject.get("content_placeholder") == null;
            // 最后一个渲染的是布局模板，因此展示印布局模板真实地址
            assert jsonObject.get("engine_renderTo").equals("/my/my.html");
            // 要展示的页面
            assert jsonObject.getJSONObject("resultData") != null;
            assert jsonObject.getJSONObject("resultData").getString("renderTo").equals("/my/my.html");
        }
    }

    // 默认关闭 layout
    @Test
    public void layoutTest_3_default() throws Throwable {
        // 加载没有 layout 的站点资源路径
        TestRenderEngine renderEngine = new TestRenderEngine(noneLayoutFiles());
        AppContext appContext = renderAppContext(false, renderEngine, apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/abc.do").with(DefaultLayoutHtmlAction.class);
        });
        String stringWriter = mockAndCallHttp("post", "http://www.hasor.net/abc.do", appContext);
        //
        {
            JSONObject jsonObject = JSON.parseObject(stringWriter);
            // 因为没有启用 layout 因此 placeholder 不会出现。
            assert jsonObject.get("content_placeholder") == null;
            // 最后一个渲染的是布局模板，因此展示印布局模板真实地址
            assert jsonObject.get("engine_renderTo").equals("/my/my.html");
            // 要展示的页面
            assert jsonObject.getJSONObject("resultData") != null;
            assert jsonObject.getJSONObject("resultData").getString("renderTo").equals("/my/my.html");
        }
    }

    // 默认关闭 layout，但是单个 action 打开了 layout
    @Test
    public void layoutTest_3_enable() throws Throwable {
        // 加载 layout 的站点资源路径
        TestRenderEngine renderEngine = new TestRenderEngine(layoutFiles());
        AppContext appContext = renderAppContext(false, renderEngine, apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/abc.do").with(EnableLayoutHtmlAction.class);
        });
        String stringWriter = mockAndCallHttp("post", "http://www.hasor.net/abc.do", appContext);
        //
        {
            JSONObject jsonObject = JSON.parseObject(stringWriter);
            // 因为启用了 layout 而且会命中 layout，因此 placeholder 肯定有内容。
            assert jsonObject.get("content_placeholder") != null;
            // 最后一个渲染的是布局模板，因此展示印布局模板真实地址
            assert jsonObject.get("engine_renderTo").equals("/layout/mytest/my/default.html");
            // 要展示的页面真实位置
            assert jsonObject.getJSONObject("content_placeholder").getString("engine_renderTo").equals("/templates/myfiles/my/my.html");
            // 要展示的页面
            assert jsonObject.getJSONObject("resultData") != null;
            assert jsonObject.getJSONObject("resultData").getString("renderTo").equals("/my/my.html");
        }
    }

    // 默认打开 layout，但是 布局模版需要递归匹配
    @Test
    public void layoutTest_4_parent() throws Throwable {
        // 加载 layout 的站点资源路径
        TestRenderEngine renderEngine = new TestRenderEngine(layoutFiles());
        AppContext appContext = renderAppContext(true, renderEngine, apiBinder -> {
            apiBinder.tryCast(WebApiBinder.class).mappingTo("/abc.do").with(ParentLayoutHtmlAction.class);
        });
        String stringWriter = mockAndCallHttp("post", "http://www.hasor.net/abc.do", appContext);
        //
        {
            JSONObject jsonObject = JSON.parseObject(stringWriter);
            // 因为启用了 layout 而且会命中 layout，因此 placeholder 肯定有内容。
            assert jsonObject.get("content_placeholder") != null;
            // 最后一个渲染的是布局模板，因此展示印布局模板真实地址
            assert jsonObject.get("engine_renderTo").equals("/layout/mytest/my/default.html");
            // 要展示的页面真实位置
            assert jsonObject.getJSONObject("content_placeholder").getString("engine_renderTo").equals("/templates/myfiles/my/abc/my.html");
            // 要展示的页面
            assert jsonObject.getJSONObject("resultData") != null;
            assert jsonObject.getJSONObject("resultData").getString("renderTo").equals("/my/abc/my.html");
        }
    }
}
