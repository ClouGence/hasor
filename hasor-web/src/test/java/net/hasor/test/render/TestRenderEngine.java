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
package net.hasor.test.render;
import com.alibaba.fastjson.JSON;
import net.hasor.core.AppContext;
import net.hasor.utils.StringUtils;
import net.hasor.web.Invoker;
import net.hasor.web.render.RenderEngine;
import net.hasor.web.render.RenderInvoker;
import net.hasor.web.valid.ValidInvoker;

import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

public class TestRenderEngine implements RenderEngine {//
    private Set<String> templateSet = new HashSet<>(); //

    public TestRenderEngine(List<String> templateSet) {
        this.templateSet.addAll(templateSet.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList()));
    }

    @Override
    public void initEngine(AppContext appContext) throws Throwable {
        //
    }

    @Override
    public void process(RenderInvoker invoker, Writer writer) throws Throwable {
        Map<String, Object> invokerData = new HashMap<>();
        invoker.keySet().stream().filter(s -> {                         //
            return !Invoker.ROOT_DATA_KEY.equalsIgnoreCase(s) &&        //
                    !ValidInvoker.VALID_DATA_KEY.equalsIgnoreCase(s) && //
                    !Invoker.REQUEST_KEY.equalsIgnoreCase(s) &&         //
                    !Invoker.RESPONSE_KEY.equalsIgnoreCase(s);          //
        }).peek(s -> {
            Object valueData = invoker.get(s);
            if (valueData == null) {
                invokerData.put(s, null);
            } else if (valueData instanceof String) {
                try {
                    invokerData.put(s, JSON.parseObject(valueData.toString()));
                } catch (Throwable e) {
                    invokerData.put(s, valueData);
                }
            } else {
                invokerData.put(s, valueData);
            }
        }).forEach(s -> {
            //
        });
        //
        invokerData.put("engine_renderTo", invoker.renderTo());
        invokerData.put("engine_viewType", invoker.renderType());
        //
        writer.write(JSON.toJSONString(invokerData));
    }

    @Override
    public boolean exist(String template) throws IOException {
        return templateSet.contains(template);
    }
}