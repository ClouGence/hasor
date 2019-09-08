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
package net.hasor.test.actions.render;
import net.hasor.web.annotation.*;
import net.hasor.web.render.RenderInvoker;

import java.util.HashMap;

public class HtmlRealRennerAction {
    @Post
    public Object testProduces1(RenderInvoker invoker) {
        invoker.renderTo("html", "/my/my.html");
        return new HashMap<String, String>() {{
            put("data", "hello");
            put("renderTo", invoker.renderTo());
            put("viewType", invoker.renderType());
        }};
    }

    @Get
    public Object testProduces2(RenderInvoker invoker) {
        invoker.renderTo("json", "/my/my.data");
        return new HashMap<String, String>() {{
            put("data", "word");
            put("renderTo", invoker.renderTo());
            put("viewType", invoker.renderType());
        }};
    }

    @Head
    public Object testProduces3(RenderInvoker invoker) {
        invoker.renderTo("/my/my.abc");
        invoker.getHttpResponse().setContentType("abcdefg");
        return new HashMap<String, String>() {{
            put("data", "word");
            put("renderTo", invoker.renderTo());
            put("viewType", invoker.renderType());
        }};
    }

    @Options
    @Produces("html")
    public Object testProduces4(RenderInvoker invoker) {
        invoker.renderTo("/my/my.abc");
        invoker.getHttpResponse().setContentType("abcdefg");    // @Produces 和 setContentType 同时配置，会导致三次 setContentType（最后一次值为 invoker.viewType()）
        return new HashMap<String, String>() {{
            put("data", "word");
            put("renderTo", invoker.renderTo());
            put("viewType", invoker.renderType());
        }};
    }

    @Put
    @Produces("html")
    public Object testProduces5(RenderInvoker invoker) {
        invoker.renderTo("json", "/my/my.abc"); // 报错，已经配置了 @Produces 注解不可以修改 viewType
        return new HashMap<String, String>() {{
            put("data", "word");
            put("renderTo", invoker.renderTo());
            put("viewType", invoker.renderType());
        }};
    }
}