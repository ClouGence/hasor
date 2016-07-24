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
package net.hasor.restful.render.velocity;
import net.hasor.restful.InvokerContext;
import net.hasor.restful.RenderEngine;
import net.hasor.web.WebAppContext;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
/**
 *
 * @version : 2016年1月3日
 * @author 赵永春(zyc@hasor.net)
 */
public class VelocityTemplateEngine implements RenderEngine {
    private String         realPath;
    private VelocityEngine velocityEngine;
    public void process(String template, Writer writer) throws Throwable {
        Template temp = velocityEngine.getTemplate(realPath + "/" + template);
        VelocityContext context = new VelocityContext();
        temp.merge(context, writer);
        temp.process();
    }
    @Override
    public void initEngine(WebAppContext appContext) throws IOException {
        this.realPath = appContext.getEnvironment().envVar("HASOR_WEBROOT");
        this.velocityEngine = new VelocityEngine();
    }
    @Override
    public void process(InvokerContext invokerContext, Writer writer) throws Throwable {
    }
    @Override
    public boolean exist(String template) throws IOException {
        Template temp = velocityEngine.getTemplate(realPath + "/" + template);
        return temp != null;
    }
}