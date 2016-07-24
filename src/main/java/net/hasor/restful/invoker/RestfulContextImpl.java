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
package net.hasor.restful.invoker;
import net.hasor.core.Init;
import net.hasor.core.Inject;
import net.hasor.restful.RenderEngine;
import net.hasor.restful.RestfulContext;
import net.hasor.restful.mime.MimeType;
import net.hasor.web.WebAppContext;
import java.io.IOException;
/**
 * 线程安全
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
public class RestfulContextImpl implements RestfulContext {
    @Inject
    private WebAppContext   webAppContext;
    @Inject
    private MimeType        mimeType;
    private LayoutDecorator layoutDecorator;
    //
    @Init
    public void initContext() throws IOException {
        RenderEngine engine = webAppContext.getInstance(RenderEngine.class);
        engine.initEngine(this.webAppContext);
        this.layoutDecorator = new LayoutDecorator(engine);
        this.layoutDecorator.initEngine(this.webAppContext);
    }
    //
    @Override
    public String getMimeType(String suffix) {
        return this.mimeType.getMimeType(suffix);
    }
    @Override
    public RenderEngine getRenderEngine() {
        return this.layoutDecorator;
    }
}
