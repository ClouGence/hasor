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
import net.hasor.core.Singleton;
import net.hasor.utils.json.JSON;
import net.hasor.web.Invoker;
import net.hasor.web.render.RenderEngine;
import net.hasor.web.render.RenderInvoker;

import java.io.Writer;

/**
 * 一个基于 Fastjson 的JSON渲染器。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-03-20
 */
@Singleton
public class JsonRenderEngine implements RenderEngine {
    @Override
    public void process(RenderInvoker invoker, Writer writer) throws Throwable {
        writer.write(JSON.toString(invoker.get(Invoker.RETURN_DATA_KEY)));
    }
}