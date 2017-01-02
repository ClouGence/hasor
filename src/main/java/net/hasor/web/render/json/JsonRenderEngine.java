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
package net.hasor.web.render.json;
import net.hasor.core.AppContext;
import net.hasor.web.Invoker;
import net.hasor.web.render.RenderEngine;
import net.hasor.web.render.RenderInvoker;
import org.more.bizcommon.json.JSON;

import java.io.IOException;
import java.io.Writer;
/**
 * Json 渲染器
 * @version : 2016年1月3日
 * @author 赵永春(zyc@hasor.net)
 */
public class JsonRenderEngine implements RenderEngine {
    @Override
    public void initEngine(AppContext appContext) throws IOException {
    }
    @Override
    public void process(RenderInvoker data, Writer writer) throws Throwable {
        String json = JSON.DEFAULT.toJSON(data.get(Invoker.RETURN_DATA_KEY));
        writer.write(json);
    }
    @Override
    public boolean exist(String template) throws IOException {
        return true;
    }
}