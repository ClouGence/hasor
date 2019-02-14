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
package net.hasor.web.invoker.beans;
import net.hasor.core.AppContext;
import net.hasor.web.RenderEngine;
import net.hasor.web.RenderInvoker;
import net.hasor.web.annotation.Render;

import java.io.IOException;
import java.io.Writer;
/**
 * @version : 2017-01-08
 * @author 赵永春 (zyc@hasor.net)
 */
@Render({ "jspx", "asp" })
public class TestRenderEngine implements RenderEngine {
    @Override
    public void initEngine(AppContext appContext) throws Throwable {
        //
    }
    @Override
    public void process(RenderInvoker invoker, Writer writer) throws Throwable {
        //
    }
    @Override
    public boolean exist(String template) throws IOException {
        return false;//
    }
}