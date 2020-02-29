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
package net.hasor.web.objects;
import net.hasor.utils.json.JSON;
import net.hasor.web.Invoker;
import net.hasor.web.render.RenderEngine;
import net.hasor.web.render.RenderInvoker;

import java.io.Writer;

/**
 * 使用内置 JSON 工具的一个 JSON 渲染器。
 * @version : 2020-02-29
 * @author 赵永春 (zyc@hasor.net)
 */
public class JsonRender implements RenderEngine {
    @Override
    public void process(RenderInvoker invoker, Writer writer) throws Throwable {
        Object o = invoker.get(Invoker.ROOT_DATA_KEY);
        writer.write(JSON.toString(o));
    }

    @Override
    public boolean exist(String template) {
        return true;
    }
}
