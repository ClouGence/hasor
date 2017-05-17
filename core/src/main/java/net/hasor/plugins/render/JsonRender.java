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
package net.hasor.plugins.render;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.web.Invoker;
import net.hasor.web.RenderEngine;
import net.hasor.web.RenderInvoker;

import java.io.IOException;
import java.io.Writer;
/**
 * JSON 渲染器，您可以通过 apiBinder.bind(JsonRenderEngine.class).... 来设置您自定义的渲染方式。
 * 默认情况下，JsonRender会自动按照下面顺序尝试寻找可以使用的 JSON 库：fastjson、Gson
 * @version : 2016年1月3日
 * @author 赵永春(zyc@hasor.net)
 */
public class JsonRender implements RenderEngine {
    private JsonRenderEngine jsonRenderEngine;
    @Override
    public void initEngine(AppContext appContext) throws Throwable {
        BindInfo<JsonRenderEngine> bindInfo = appContext.getBindInfo(JsonRenderEngine.class);
        if (bindInfo == null) {
            try {
                Class.forName("com.alibaba.fastjson.JSON");
                this.jsonRenderEngine = new FastJsonRenderEngine();
            } catch (Exception e1) {
                try {
                    Class.forName("com.google.gson.Gson");
                    this.jsonRenderEngine = new GsonRenderEngine();
                } catch (Exception e2) {
                    //                    try {
                    //                        Class.forName("net.sf.json.JSONObject");
                    //                        this.jsonRenderEngine = new JsonLibRenderEngine();
                    //                    } catch (Exception e3) {
                    throw new ClassNotFoundException("Did not find any of the following set up (Fastjson、Gson)");
                    //                    }
                }
            }
        } else {
            this.jsonRenderEngine = appContext.getInstance(bindInfo);
        }
    }
    @Override
    public boolean exist(String template) throws IOException {
        return true;
    }
    @Override
    public void process(RenderInvoker renderData, Writer writer) throws Throwable {
        Object data = renderData.get(Invoker.RETURN_DATA_KEY);
        this.jsonRenderEngine.writerJson(data, writer);
    }
    public static interface JsonRenderEngine {
        public void writerJson(Object renderData, Writer writerTo) throws Throwable;
    }
    //-----------------------------------------
    /** FastJSON渲染器 */
    public static class FastJsonRenderEngine implements JsonRenderEngine {
        @Override
        public void writerJson(Object renderData, Writer writerTo) throws Throwable {
            JSON.writeJSONString(writerTo, renderData);
        }
    }
    /** Gson渲染器 */
    public static class GsonRenderEngine implements JsonRenderEngine {
        private Gson gson = new GsonBuilder().create();
        @Override
        public void writerJson(Object renderData, Writer writerTo) throws Throwable {
            JsonWriter jsonWriter = this.gson.newJsonWriter(writerTo);
            this.gson.toJson(renderData, renderData.getClass(), jsonWriter);
        }
    }
    //    /** JsonLib渲染器 */
    //    public static class JsonLibRenderEngine implements JsonRenderEngine {
    //        @Override
    //        public void writerJson(Object renderData, Writer writerTo) throws Throwable {
    //            JSONObject jsonObject = JSONObject.fromObject(renderData);
    //            jsonObject.write(writerTo);
    //        }
    //    }
}